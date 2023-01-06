package ai.aitia.mission_scheduler.mission_scheduler_consumer;

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
import ai.aitia.mission_scheduler.common.FollowPathTask;
import ai.aitia.mission_scheduler.common.GPSPoint;
import ai.aitia.mission_scheduler.common.GoToPointTask;
import ai.aitia.mission_scheduler.common.Mission;
import ai.aitia.mission_scheduler.common.MissionTask;
import ai.aitia.mission_scheduler.common.MissionTask.TaskType;
import ai.aitia.mission_scheduler.common.dto.AddMissionRequestDTO;
import ai.aitia.mission_scheduler.common.dto.AddMissionResponseDTO;
import ai.aitia.mission_scheduler.common.dto.GetNextMissionResponseDTO;
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
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, HelloConsumerConstants.BASE_PACKAGE})
public class HelloConsumerMain implements ApplicationRunner {
    
    //=================================================================================================
	// members
	
    @Autowired
	private ArrowheadService arrowheadService;
    
    @Autowired
	protected SSLProperties sslProperties;
    
    private final Logger logger = LogManager.getLogger(HelloConsumerMain.class);
    
    //=================================================================================================
	// methods

	//------------------------------------------------------------------------------------------------
    public static void main( final String[] args ) {
    	SpringApplication.run(HelloConsumerMain.class, args);
    }

    //-------------------------------------------------------------------------------------------------
	// This function is started after the consumer is initialized
    @Override
	public void run(final ApplicationArguments args) throws Exception {
		addMissionOrchestrationAndConsumption();
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
		T ret = arrowheadService.consumeServiceHTTP(responseType, HttpMethod.valueOf(orchestrationResult.getMetadata().get(HelloConsumerConstants.HTTP_METHOD)),
				orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri(),
				getInterface(), token, request, new String[0]);
		
		return ret;
	}
    

	public void addMissionOrchestrationAndConsumption() {
    	logger.info("Orchestration request for " + HelloConsumerConstants.ADD_MISSION_SERVICE_DEFINITION + " service:");

		OrchestrationResponseDTO orchestrationResponse = getOrchestrationResponse(HelloConsumerConstants.ADD_MISSION_SERVICE_DEFINITION);

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
			validateOrchestrationResult(orchestrationResult, HelloConsumerConstants.ADD_MISSION_SERVICE_DEFINITION);

			List<Mission> missionList = new ArrayList<>();
			
			// Create a mission
			final List<MissionTask> tasks = new ArrayList<>();

			GoToPointTask task1 = new GoToPointTask("go to start", 65.05, 22.0);
			List<GPSPoint> points2 = new ArrayList<>();
			points2.add(new GPSPoint(65.06, 22.0));
			points2.add(new GPSPoint(65.07, 22.0));
			points2.add(new GPSPoint(65.08, 22.0));
			points2.add(new GPSPoint(65.09, 22.0));
			points2.add(new GPSPoint(65.10, 22.0));
			points2.add(new GPSPoint(65.11, 22.0));
			FollowPathTask task2 = new FollowPathTask("follow path", points2);
			GoToPointTask task3 = new GoToPointTask("go to home", 65.15, 22.0);
			tasks.add(task1);
			tasks.add(task2);
			tasks.add(task3);
			missionList.add(new Mission(tasks, "plow mission", 2));

			/*
			final List<String> tasks2 = new ArrayList<>();
			tasks2.add("go to other place");
			tasks2.add("plow here");
			tasks2.add("stop plowing");
			tasks2.add("spin around");
			missionList.add(new Mission(tasks2, "another plow mission", 3));

			final List<String> tasks3 = new ArrayList<>();
			tasks3.add("follow path");
			tasks3.add("find charger");
			tasks3.add("charge");
			tasks3.add("follow path");
			missionList.add(new Mission(tasks3, "another plow mission", 2));
			*/

			for (Mission m : missionList) {
				logger.info("Create a add request:");
				final AddMissionRequestDTO request = new AddMissionRequestDTO(m, 0);
				printOut(request);
				
				AddMissionResponseDTO response = consumeServiceRequestAndResponse(orchestrationResult, request, AddMissionResponseDTO.class);
				printOut(response);
			}
		}
	}

    
    //=================================================================================================
	// assistant methods
    
    //-------------------------------------------------------------------------------------------------
    private String getInterface() {
    	return sslProperties.isSslEnabled() ? HelloConsumerConstants.INTERFACE_SECURE : HelloConsumerConstants.INTERFACE_INSECURE;
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
