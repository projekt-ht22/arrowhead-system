package ai.aitia.mission_executor.mission_executor_system.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.ContainerElementNodeBuilderCustomizableContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.mission_executor.common.dto.DoMissionRequestDTO;
import ai.aitia.mission_executor.common.dto.DoMissionResponseDTO;
import ai.aitia.mission_executor.common.dto.HelloRequestDTO;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO;
import eu.arrowhead.common.dto.shared.OrchestrationResponseDTO;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.dto.shared.ServiceInterfaceResponseDTO;
import eu.arrowhead.common.dto.shared.ServiceQueryFormDTO;
import eu.arrowhead.common.dto.shared.OrchestrationFlags.Flag;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO.Builder;
import eu.arrowhead.common.exception.InvalidParameterException;
import ai.aitia.mission_executor.common.dto.HelloResponseDTO;
import ai.aitia.mission_executor.common.dto.DoMissionResponseDTO.Status;
import ai.aitia.mission_executor.mission_executor_system.ControllerState;
import ai.aitia.mission_executor.mission_executor_system.DoMissionService;
import ai.aitia.mission_executor.mission_executor_system.MissionExecutorSystemConstants;
import ai.aitia.mission_scheduler.common.Mission;
import ai.aitia.mission_scheduler.common.dto.ExecutorReadyDTO;
import ai.aitia.mission_scheduler.common.dto.ExecutorReadyDTO.ExecutorStatus;;

@RestController
@RequestMapping(MissionExecutorSystemConstants.DO_MISSION_URI)
public class MissionExecutorSystemController {
	
	//=================================================================================================
	// members	
	@Autowired
	private ControllerState controllerState;

    @Autowired
	private ArrowheadService arrowheadService;
    
    @Autowired
	protected SSLProperties sslProperties;

	private Thread doMissionThread;

    private final Logger logger = LogManager.getLogger(MissionExecutorSystemConstants.class);
	//=================================================================================================
	// methods

	// POST mapping for the hello service
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public DoMissionResponseDTO doMission(HttpEntity<String> httpEntity) {
		logger.info("Handle request do mission.");

		synchronized (controllerState) {
			if (controllerState.isRunning()) {
				// replace mission
				// TODO: add reminder of mission to scheduler
				controllerState.setRunning(false);
				doMissionThread.interrupt();
			}
		}

		// wait for thread to end if exist
		if (doMissionThread != null) {
			try {
				doMissionThread.join();
			} catch (InterruptedException e) {
				return new DoMissionResponseDTO(Status.ERROR);
			}
		}

		try {
			Object obj = new JSONParser().parse(httpEntity.getBody());
			JSONObject jsonObject = (JSONObject) obj;

			JSONObject missionObject = (JSONObject) jsonObject.get("mission");

			Mission mission = new Mission(missionObject);
			logger.info("Mission received:");
			printOut(mission);

			// update the state
			synchronized (controllerState) {
				controllerState.setRunning(true);
				controllerState.setCurrentMission(mission);
			}

			DoMissionService doMissionService = new DoMissionService(mission, controllerState, sslProperties, arrowheadService);

			Thread t = new Thread(doMissionService);
			doMissionThread = t;

			t.start();

		} catch (ParseException e) {
			logger.error("unable to parse mission");
			return new DoMissionResponseDTO(Status.ERROR);
		}


		return new DoMissionResponseDTO(Status.STARTED);
	}

    private void printOut(final Object object) {
    	System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
    }

	private OrchestrationResponseDTO getOrchestrationResponse(String serviceDefinition) {
		// Create a request for the orchestrator asking for the hello service
    	final ServiceQueryFormDTO serviceQueryForm = new ServiceQueryFormDTO.Builder(serviceDefinition)
    																		.interfaces(getInterface())
    																		.build();
		final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
		final OrchestrationFormRequestDTO orchestrationFormRequest = orchestrationFormBuilder.requestedService(serviceQueryForm)
																					   .flag(Flag.MATCHMAKING, true)
																					   .flag(Flag.OVERRIDE_STORE, true)
																					   .build();
		
		printOut(orchestrationFormRequest);		
		
		// Send request to orchestrator and get a response
		final OrchestrationResponseDTO orchestrationResponse = arrowheadService.proceedOrchestration(orchestrationFormRequest);
		return orchestrationResponse;
	}

	private <T, E> T consumeServiceRequestAndResponse(OrchestrationResultDTO orchestrationResult, E request, Class<T> responseType) {
		// Get the security token
		final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
		logger.info("Consume service");
		@SuppressWarnings("unchecked")

		// Send a request to the provider and get a response
		T ret = arrowheadService.consumeServiceHTTP(responseType, HttpMethod.valueOf(orchestrationResult.getMetadata().get(MissionExecutorSystemConstants.HTTP_METHOD)),
				orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri(),
				getInterface(), token, request, new String[0]);
		
		return ret;
	}

	public void executorReady(ExecutorStatus status) {
    	logger.info("Orchestration request for " + MissionExecutorSystemConstants.EXECUTOR_READY_SERVICE_DEFINITION + " service:");

		OrchestrationResponseDTO orchestrationResponse = getOrchestrationResponse(MissionExecutorSystemConstants.EXECUTOR_READY_SERVICE_DEFINITION);

		logger.info("Orchestration response:");
		printOut(orchestrationResponse);
		
		// Check for a valid response
		if (orchestrationResponse == null) {
			logger.info("No orchestration response received");
		} else if (orchestrationResponse.getResponse().isEmpty()) {
			logger.info("No provider found during the orchestration");
		} else {
			// Valid response received
			final OrchestrationResultDTO orchestrationResult = orchestrationResponse.getResponse().get(0);
			validateOrchestrationResult(orchestrationResult, MissionExecutorSystemConstants.DO_MISSION_SERVICE_DEFINITION);
			
			final ExecutorReadyDTO request = new ExecutorReadyDTO(status);
			printOut(request);

			String response = consumeServiceRequestAndResponse(orchestrationResult, request, String.class);
			
			logger.info("Provider response");
			printOut(response);
		}
	}

    
    //=================================================================================================
	// assistant methods
    
    //-------------------------------------------------------------------------------------------------
    private String getInterface() {
    	return sslProperties.isSslEnabled() ? MissionExecutorSystemConstants.INTERFACE_SECURE : MissionExecutorSystemConstants.INTERFACE_INSECURE;
    }
    
    //-------------------------------------------------------------------------------------------------
    private void validateOrchestrationResult(final OrchestrationResultDTO orchestrationResult, final String serviceDefinitin) {
    	if (!orchestrationResult.getService().getServiceDefinition().equalsIgnoreCase(serviceDefinitin)) {
			throw new InvalidParameterException("Requested and orchestrated service definition do not match");
		}
    	
    	boolean hasValidInterface = false;
    	for (final ServiceInterfaceResponseDTO serviceInterface : orchestrationResult.getInterfaces()) {
			if (serviceInterface.getInterfaceName().equalsIgnoreCase(getInterface())) {
				hasValidInterface = true;
				break;
			}
		}
    	if (!hasValidInterface) {
    		throw new InvalidParameterException("Requested and orchestrated interface do not match");
		}
    }
}
