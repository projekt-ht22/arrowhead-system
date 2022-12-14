package ai.aitia.mission_executor.mission_executor_tester;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.mission_executor.common.dto.DoMissionRequestDTO;
import ai.aitia.mission_executor.common.dto.DoMissionResponseDTO;
import ai.aitia.mission_executor.common.dto.HelloRequestDTO;
import ai.aitia.mission_executor.common.dto.HelloResponseDTO;
import ai.aitia.mission_scheduler.common.Mission;
import ai.aitia.mission_scheduler.common.MissionTask;
import ai.aitia.mission_scheduler.common.MissionTask.TaskType;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.dto.shared.OrchestrationFlags.Flag;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO.Builder;
import eu.arrowhead.common.dto.shared.OrchestrationResponseDTO;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.dto.shared.ServiceInterfaceResponseDTO;
import eu.arrowhead.common.dto.shared.ServiceQueryFormDTO;
import eu.arrowhead.common.exception.InvalidParameterException;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, MissionExecutorTesterConstants.BASE_PACKAGE})
public class MissionExecutorTesterMain implements ApplicationRunner {
    
    //=================================================================================================
	// members
	
    @Autowired
	private ArrowheadService arrowheadService;
    
    @Autowired
	protected SSLProperties sslProperties;
    
    private final Logger logger = LogManager.getLogger(MissionExecutorTesterMain.class);
    
    //=================================================================================================
	// methods

	//------------------------------------------------------------------------------------------------
    public static void main( final String[] args ) {
    	SpringApplication.run(MissionExecutorTesterMain.class, args);
    }

    //-------------------------------------------------------------------------------------------------
	// This function is started after the consumer is initialized
    @Override
	public void run(final ApplicationArguments args) throws Exception {
		doMission();
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
		T ret = arrowheadService.consumeServiceHTTP(responseType, HttpMethod.valueOf(orchestrationResult.getMetadata().get(MissionExecutorTesterConstants.HTTP_METHOD)),
				orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri(),
				getInterface(), token, request, new String[0]);
		
		return ret;
	}

	public void doMission() {
    	logger.info("Orchestration request for " + MissionExecutorTesterConstants.DO_MISSION_SERVICE_DEFINITION + " service:");

		OrchestrationResponseDTO orchestrationResponse = getOrchestrationResponse(MissionExecutorTesterConstants.DO_MISSION_SERVICE_DEFINITION);

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
			validateOrchestrationResult(orchestrationResult, MissionExecutorTesterConstants.DO_MISSION_SERVICE_DEFINITION);
			
			// Create a hello request
			final List<MissionTask> tasks = new ArrayList<>();
			tasks.add(new MissionTask(TaskType.GO_TO_POINT, "go to start"));
			tasks.add(new MissionTask(TaskType.FOLLOW_PATH, "Follow path"));
			tasks.add(new MissionTask(TaskType.GO_TO_POINT, "go home"));
			Mission mission = new Mission(tasks, "plow mission", 2);
			final DoMissionRequestDTO request = new DoMissionRequestDTO(mission);
			printOut(request);

			DoMissionResponseDTO response = consumeServiceRequestAndResponse(orchestrationResult, request, DoMissionResponseDTO.class);
			
			logger.info("Provider response");
			printOut(response);
		}
	}

    
    //=================================================================================================
	// assistant methods
    
    //-------------------------------------------------------------------------------------------------
    private String getInterface() {
    	return sslProperties.isSslEnabled() ? MissionExecutorTesterConstants.INTERFACE_SECURE : MissionExecutorTesterConstants.INTERFACE_INSECURE;
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
    
    //-------------------------------------------------------------------------------------------------
    private void printOut(final Object object) {
    	System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
    }
}
