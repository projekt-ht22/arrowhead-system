package ai.aitia.snowblower.snowblower_consumer;

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
import ai.aitia.snowblower.common.dto.GetAccessoryCommandResponseDTO;
import ai.aitia.snowblower.common.dto.GetAccessoryIDResponseDTO;
import ai.aitia.snowblower.common.dto.GetAccessoryStopStartResponseDTO;
import ai.aitia.snowblower.common.dto.SetAccessoryStopStartResponseDTO;
import ai.aitia.snowblower.common.dto.SetBlowerRequestDTO;
import ai.aitia.snowblower.common.dto.GetAccessoryCommandResponseDTO.commandTypes;
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
		getAccessoryIDOrchestrationAndConsumption();
		// StartAccessoryOrchestrationAndConsumption();
		// StopAccessoryOrchestrationAndConsumption();
		CommandRPMAccessoryOrchestrationAndConsumption();
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

	public void getAccessoryIDOrchestrationAndConsumption() {

		OrchestrationResponseDTO orchestrationResponse = getOrchestrationResponse(HelloConsumerConstants.GET_ACCESSORY_ID_SERVICE_DEFINITION);
		// Check for a valid response
		if (orchestrationResponse == null) {
			logger.info("No orchestration response received");
		} else if (orchestrationResponse.getResponse().isEmpty()) {
			logger.info("No provider found during the orchestration");
		} else {
			// Valid response received
			final OrchestrationResultDTO orchestrationResult = orchestrationResponse.getResponse().get(0);
			validateOrchestrationResult(orchestrationResult, HelloConsumerConstants.GET_ACCESSORY_ID_SERVICE_DEFINITION);
			
			final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
			// Create a hello request
			// Send a request to the provider and get a response

			@SuppressWarnings("unchecked")
			final GetAccessoryIDResponseDTO response = arrowheadService.consumeServiceHTTP(GetAccessoryIDResponseDTO.class, HttpMethod.valueOf(orchestrationResult.getMetadata().get(HelloConsumerConstants.HTTP_METHOD)),
					orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri(),
					getInterface(), token, null, new String[0]);
			logger.info("Provider response");
			printOut(response);
		}
	}



	public void StartAccessoryOrchestrationAndConsumption() {
    	logger.info("Orchestration request for " + HelloConsumerConstants.START_ACCESSORY_SERVICE_DEFINITION + " service:");

		OrchestrationResponseDTO orchestrationResponse = getOrchestrationResponse(HelloConsumerConstants.START_ACCESSORY_SERVICE_DEFINITION);

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
			validateOrchestrationResult(orchestrationResult, HelloConsumerConstants.START_ACCESSORY_SERVICE_DEFINITION);

			// Create a hello request
			logger.info("Activate accessory:");
			SetAccessoryStopStartResponseDTO request = new SetAccessoryStopStartResponseDTO(true);
			printOut(request);
			
			// Get the security token
			final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
			logger.info("Consume service");
			@SuppressWarnings("unchecked")
			

			final GetAccessoryStopStartResponseDTO response = arrowheadService.consumeServiceHTTP(GetAccessoryStopStartResponseDTO.class, HttpMethod.valueOf(orchestrationResult.getMetadata().get(HelloConsumerConstants.HTTP_METHOD)),
					orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri(),
					getInterface(), token, request, new String[0]);
			logger.info("Provider response");
			printOut(response);

		}
	}

	public void StopAccessoryOrchestrationAndConsumption() {
    	logger.info("Orchestration request for " + HelloConsumerConstants.STOP_ACCESSORY_SERVICE_DEFINITION + " service:");

		OrchestrationResponseDTO orchestrationResponse = getOrchestrationResponse(HelloConsumerConstants.STOP_ACCESSORY_SERVICE_DEFINITION);

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
			validateOrchestrationResult(orchestrationResult, HelloConsumerConstants.STOP_ACCESSORY_SERVICE_DEFINITION);

			// Create a hello request
			logger.info("Deactivate accessory:");
			SetAccessoryStopStartResponseDTO request = new SetAccessoryStopStartResponseDTO(false);
			printOut(request);
			
			// Get the security token
			final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
			logger.info("Consume service");
			@SuppressWarnings("unchecked")
			

			final GetAccessoryStopStartResponseDTO response = arrowheadService.consumeServiceHTTP(GetAccessoryStopStartResponseDTO.class, HttpMethod.valueOf(orchestrationResult.getMetadata().get(HelloConsumerConstants.HTTP_METHOD)),
					orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri(),
					getInterface(), token, request, new String[0]);
			logger.info("Provider response");
			printOut(response);

		}
	}

	public void CommandRPMAccessoryOrchestrationAndConsumption() {
    	logger.info("Orchestration request for " + HelloConsumerConstants.COMMAND_ACCESSORY_SERVICE_DEFINITION + " service:");

		OrchestrationResponseDTO orchestrationResponse = getOrchestrationResponse(HelloConsumerConstants.COMMAND_ACCESSORY_SERVICE_DEFINITION);

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
			validateOrchestrationResult(orchestrationResult, HelloConsumerConstants.COMMAND_ACCESSORY_SERVICE_DEFINITION);

			// Create a hello request
			logger.info("Create a RPM request:");
			SetBlowerRequestDTO request = new SetBlowerRequestDTO(0, 0, 5000, commandTypes.SNOWBLOWER_RPM);
			printOut(request);
			
			// Get the security token
			final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
			logger.info("Consume service");
			@SuppressWarnings("unchecked")
			

			final GetAccessoryCommandResponseDTO response = arrowheadService.consumeServiceHTTP(GetAccessoryCommandResponseDTO.class, HttpMethod.valueOf(orchestrationResult.getMetadata().get(HelloConsumerConstants.HTTP_METHOD)),
					orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri(),
					getInterface(), token, request, new String[0]);
			logger.info("Provider response");
			printOut(response);

		}
	}

	public void CommandPWM12AccessoryOrchestrationAndConsumption() {
    	logger.info("Orchestration request for " + HelloConsumerConstants.COMMAND_ACCESSORY_SERVICE_DEFINITION + " service:");

		OrchestrationResponseDTO orchestrationResponse = getOrchestrationResponse(HelloConsumerConstants.COMMAND_ACCESSORY_SERVICE_DEFINITION);

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
			validateOrchestrationResult(orchestrationResult, HelloConsumerConstants.COMMAND_ACCESSORY_SERVICE_DEFINITION);

			// Create a hello request
			logger.info("Create a RPM request:");
			SetBlowerRequestDTO request = new SetBlowerRequestDTO(10, 10, 0, commandTypes.SNOWBLOWER_HORIZONTAL_ANGEL);
			printOut(request);
			
			// Get the security token
			final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
			logger.info("Consume service");
			@SuppressWarnings("unchecked")
			

			final GetAccessoryCommandResponseDTO response = arrowheadService.consumeServiceHTTP(GetAccessoryCommandResponseDTO.class, HttpMethod.valueOf(orchestrationResult.getMetadata().get(HelloConsumerConstants.HTTP_METHOD)),
					orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri(),
					getInterface(), token, request, new String[0]);
			logger.info("Provider response");
			printOut(response);

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
