package ai.aitia.mission_scheduler.mission_scheduler_provider.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jose4j.json.internal.json_simple.JSONArray;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.mission_executor.common.dto.DoMissionRequestDTO;
import ai.aitia.mission_executor.common.dto.DoMissionResponseDTO;
import ai.aitia.mission_scheduler.common.Mission;
import ai.aitia.mission_scheduler.common.MissionTask.TaskType;
import ai.aitia.mission_scheduler.common.dto.AddMissionRequestDTO;
import ai.aitia.mission_scheduler.common.dto.AddMissionResponseDTO;
import ai.aitia.mission_scheduler.common.dto.ExecutorReadyDTO;
import ai.aitia.mission_scheduler.common.dto.GetNextMissionResponseDTO;
import ai.aitia.mission_scheduler.common.dto.AddMissionResponseDTO.Status;
import ai.aitia.mission_scheduler.mission_scheduler_provider.MissionSchedulerProviderConstants;
import ai.aitia.mission_scheduler.mission_scheduler_provider.scheduler.Scheduler;
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

@RestController
public class AddMissionServiceController {
	
	//=================================================================================================
	// members	

	@Autowired
	private Scheduler scheduler;

    @Autowired
	private ArrowheadService arrowheadService;
    
    @Autowired
	protected SSLProperties sslProperties;

	private boolean ready = false;
	
    private final Logger logger = LogManager.getLogger(AddMissionServiceController.class);
	//=================================================================================================
	// methods

	// POST mapping for the hello service
	@PostMapping(path = MissionSchedulerProviderConstants.ADD_MISSION_URI, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public AddMissionResponseDTO addMission(HttpEntity<String> httpEntity) {
		logger.info("Handle request.");
		String rawJson = httpEntity.getBody();
		System.out.println(Utilities.toPrettyJson(rawJson));

		try {
			Object obj = new JSONParser().parse(rawJson);
			JSONObject jsonObject = (JSONObject) obj;

			JSONObject missionObject = (JSONObject) jsonObject.get("mission");
			Mission mission = new Mission(missionObject);
			printOut(mission);
		} catch (ParseException e) {
			logger.error("unable to parse mission");
			return new AddMissionResponseDTO(Status.ERROR);
		}

		/*
		Mission mission = dto.getMission();
		logger.info("Mission received:");
		printOut(mission);

		// if not ready add mission to schedule else do the mission
		if (ready) {
			ready = false;
			if (!doMission(mission)) {
				// something went wrong just add mission to queue
				scheduler.addMission(mission);
			}
		} else {
			scheduler.addMission(mission);
		}
		*/



		return new AddMissionResponseDTO(Status.ADDED);
	}

	// private class used to spawn a thread to run doMission().
	private class DoMissionRequestAsync implements Runnable {
		private Mission mission;

		DoMissionRequestAsync(Mission mission) {
			this.mission = mission;
		}
		
		@Override
		public void run() {
			doMission(mission);
		}
	}

	@PostMapping(path = MissionSchedulerProviderConstants.EXECUTOR_READY_URI,consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public String ExecutorReady(@RequestBody final ExecutorReadyDTO dto) {
		Mission mission = scheduler.getNextAndRemoveMission();
		if (mission == null) {
			ready = true;
			return "OK";
		} else {
			// needs to run in separate thread to not have circular waiting with mission executer.
			DoMissionRequestAsync doMissionRequestAsync = new DoMissionRequestAsync(mission);
			Thread t = new Thread(doMissionRequestAsync);
			t.start();
		}

		return "OK";
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
		T ret = arrowheadService.consumeServiceHTTP(responseType, HttpMethod.valueOf(orchestrationResult.getMetadata().get(MissionSchedulerProviderConstants.HTTP_METHOD)),
				orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri(),
				getInterface(), token, request, new String[0]);
		
		return ret;
	}

	// sends a doMission request to mission executer
	// return true if succesfull and false otherwise
	public Boolean doMission(Mission mission) {
    	logger.info("Orchestration request for " + MissionSchedulerProviderConstants.DO_MISSION_SERVICE_DEFINITION + " service:");

		OrchestrationResponseDTO orchestrationResponse = getOrchestrationResponse(MissionSchedulerProviderConstants.DO_MISSION_SERVICE_DEFINITION);

		logger.info("Orchestration response:");
		printOut(orchestrationResponse);		
		
		// Check for a valid response
		if (orchestrationResponse == null) {
			logger.info("No orchestration response received");
			return false;
		} else if (orchestrationResponse.getResponse().isEmpty()) {
			logger.info("No provider found during the orchestration");
			return false;
		} else {
			// Valid response received
			final OrchestrationResultDTO orchestrationResult = orchestrationResponse.getResponse().get(0);
			validateOrchestrationResult(orchestrationResult, MissionSchedulerProviderConstants.DO_MISSION_SERVICE_DEFINITION);
			
			final DoMissionRequestDTO request = new DoMissionRequestDTO(mission);
			printOut(request);

			DoMissionResponseDTO response = consumeServiceRequestAndResponse(orchestrationResult, request, DoMissionResponseDTO.class);
			
			logger.info("Provider response");
			printOut(response);

			return response.getStatus() == DoMissionResponseDTO.Status.STARTED;
		}
	}

    
    //=================================================================================================
	// assistant methods
    
    //-------------------------------------------------------------------------------------------------
    private String getInterface() {
    	return sslProperties.isSslEnabled() ? MissionSchedulerProviderConstants.INTERFACE_SECURE : MissionSchedulerProviderConstants.INTERFACE_INSECURE;
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
