package ai.aitia.mission_executor.mission_executor_system;

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

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, MissionExecutorSystemConstants.BASE_PACKAGE})
public class MissionExecutorSystemMain implements ApplicationRunner{

	//=================================================================================================
	// methods
    @Autowired
	private ArrowheadService arrowheadService;
    
    @Autowired
	protected SSLProperties sslProperties;
    
    private final Logger logger = LogManager.getLogger(MissionExecutorSystemMain.class);

	//-------------------------------------------------------------------------------------------------
	public static void main(final String[] args) {
		SpringApplication.run(MissionExecutorSystemMain.class, args);
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
			logger.info("Orchestration response received.");

			final OrchestrationResultDTO orchestrationResult = orchestrationResponse.getResponse().get(0);
			logger.info("extracted orchestration result.");
			validateOrchestrationResult(orchestrationResult, MissionExecutorSystemConstants.EXECUTOR_READY_SERVICE_DEFINITION);
			logger.info("Validated orchestration result");
			
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

	@Override
	public void run(ApplicationArguments args) throws Exception {
		logger.info("Tell scheduler i'm ready.");
		executorReady(ExecutorStatus.READY);
	}
}
