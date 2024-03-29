package ai.aitia.mission_executor.mission_executor_system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;

import ai.aitia.mission_scheduler.common.FollowPathTask;
import ai.aitia.mission_scheduler.common.GPSPoint;
import ai.aitia.mission_scheduler.common.GoToPointTask;
import ai.aitia.mission_scheduler.common.Mission;
import ai.aitia.mission_scheduler.common.MissionTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.mission_scheduler.common.dto.ExecutorReadyDTO;
import ai.aitia.mission_scheduler.common.dto.ExecutorReadyDTO.ExecutorStatus;
import ai.aitia.navigator.common.FollowPathRequestDTO;
import ai.aitia.navigator.common.GoToPointRequestDTO;
import ai.aitia.navigator.common.NavigatorResponseDTO;
import eu.arrowhead.common.CommonConstants;
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

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DoMissionService implements Runnable {
    private final Logger logger = LogManager.getLogger(DoMissionService.class);

	private ArrowheadService arrowheadService;
	protected SSLProperties sslProperties;
    private Mission mission;
	private ControllerState controllerState;

    public DoMissionService(Mission mission, ControllerState controllerState,SSLProperties sslProperties, ArrowheadService arrowheadService) {
        this.mission = mission;
        this.controllerState = controllerState;
		this.sslProperties = sslProperties;
		this.arrowheadService = arrowheadService;
    }

	private void doGotoPointTask(GoToPointTask task) {
		logger.info("Going to point: lat: {} long: {}", task.getPoint().getLatitude(), task.getPoint().getLongitude());
		OrchestrationResultDTO response = getOrchestrationResultBlocking(MissionExecutorSystemConstants.GO_TO_POINT_SERVICE_DEFINITION);
		consumeServiceRequestAndResponse(response, new GoToPointRequestDTO(task.getPoint(), controllerState.changeTaskId()), NavigatorResponseDTO.class);
	}

	private void doFollowPathTask(FollowPathTask task) { // Follow path now sending the whole path for now
		logger.info("Following path");

		OrchestrationResultDTO response = getOrchestrationResultBlocking(MissionExecutorSystemConstants.FOLLOW_PATH_SERVICE_DEFINITION);
		consumeServiceRequestAndResponse(response, new FollowPathRequestDTO(task.getPoints(), 0, controllerState.changeTaskId()), NavigatorResponseDTO.class);
	}

	private void waitForTaskReady() {
		while (true) {
			if (controllerState.isLastFinishedTaskIdCurrentTaskId()) { // if we got confirmation task is done return
				return;
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				logger.info("interrupted");
				return;
			}
		}
	}
	
	private void doTask(MissionTask task) {
		switch (task.getType()) {
			case GO_TO_POINT:
				doGotoPointTask((GoToPointTask) task);
				break;
			case FOLLOW_PATH:
				doFollowPathTask((FollowPathTask) task);
				break;
		}
	}

    @Override
    public void run() {
        List<MissionTask> tasks = mission.getTasks();

        logger.info("Started mission");
        for (int i = 0; i < tasks.size(); i++) {
            MissionTask n = tasks.get(i);
			if (!controllerState.isRunning()) {
				return;
			}
			controllerState.setCurrentTaskIndex(i);
            logger.info("doing: {}", n.getDescription());
			doTask(n);
			waitForTaskReady();
        }

        logger.info("done with mission");
		controllerState.setRunning(false);
        executorReady(ExecutorStatus.READY);
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

    private OrchestrationResultDTO getOrchestrationResultBlocking(String serviceDefinition) {
        OrchestrationResponseDTO response = null;
        while (true) {
            response = getOrchestrationResponse(serviceDefinition);
            
            if(response == null) {
                logger.info("null response");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                continue;
            } else if (response.getResponse().isEmpty()) {
                logger.info("empty response");
                continue;
            } else {
                break;
            }
        }
        return response.getResponse().get(0);
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
			validateOrchestrationResult(orchestrationResult, MissionExecutorSystemConstants.EXECUTOR_READY_SERVICE_DEFINITION);
			
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

    private void printOut(final Object object) {
    	System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
    }
}
