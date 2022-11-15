package ai.aitia.robot_controller.robot_controller_consumer;

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
import ai.aitia.robot_controller.common.Message;
import ai.aitia.robot_controller.common.dto.AddMessageRequestDTO;
import ai.aitia.robot_controller.common.dto.AddMessageResponseDTO;
import ai.aitia.robot_controller.common.dto.GetMessageResponseDTO;
import ai.aitia.robot_controller.common.dto.SetSpeedRequestDTO;
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
		addMessageOrchestrationAndConsumption();
		// getMessageOrchestrationAndConsumption();
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
    

	public void addMessageOrchestrationAndConsumption() {
    	logger.info("Orchestration request for " + HelloConsumerConstants.SET_TRACK_SPEED_SERVICE_DEFINITION + " service:");

		OrchestrationResponseDTO orchestrationResponse = getOrchestrationResponse(HelloConsumerConstants.SET_TRACK_SPEED_SERVICE_DEFINITION);

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
			validateOrchestrationResult(orchestrationResult, HelloConsumerConstants.SET_TRACK_SPEED_SERVICE_DEFINITION);

			// Create a hello request
			logger.info("Create a speed request:");
			SetSpeedRequestDTO request = new SetSpeedRequestDTO(1000,500);
			logger.info("Create a speed request done");
			printOut(request);
			logger.info("after print");
			
			// Get the security token
			final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
			logger.info("Consume service");
			@SuppressWarnings("unchecked")
			

			final AddMessageResponseDTO response = arrowheadService.consumeServiceHTTP(AddMessageResponseDTO.class, HttpMethod.valueOf(orchestrationResult.getMetadata().get(HelloConsumerConstants.HTTP_METHOD)),
					orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri(),
					getInterface(), token, request, new String[0]);
			logger.info("Provider response");
			printOut(response);

			// List<Message> messageList = new ArrayList<>();
			
			// // Create a message
			// final List<String> tasks = new ArrayList<>();
			// tasks.add("go to place");
			// tasks.add("plow");
			// tasks.add("done");
			// messageList.add(new Message(tasks, "plow message", 2));

			// final List<String> tasks2 = new ArrayList<>();
			// tasks2.add("go to other place");
			// tasks2.add("plow here");
			// tasks2.add("stop plowing");
			// tasks2.add("spin around");
			// messageList.add(new Message(tasks2, "another plow message", 3));

			// final List<String> tasks3 = new ArrayList<>();
			// tasks3.add("follow path");
			// tasks3.add("find charger");
			// tasks3.add("charge");
			// tasks3.add("follow path");
			// messageList.add(new Message(tasks3, "another plow message", 2));

			// for (Message m : messageList) {
			// 	logger.info("Create a add request:");
			// 	final AddMessageRequestDTO request = new AddMessageRequestDTO(m, 0);
			// 	printOut(request);
				
			// 	// Get the security token
			// 	final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
			// 	logger.info("Consume service");
			// 	@SuppressWarnings("unchecked")
			// 	// Send a request to the provider and get a response
			// 	final AddMessageResponseDTO response = arrowheadService.consumeServiceHTTP(AddMessageResponseDTO.class, HttpMethod.valueOf(orchestrationResult.getMetadata().get(HelloConsumerConstants.HTTP_METHOD)),
			// 			orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri(),
			// 			getInterface(), token, request, new String[0]);
			// 	logger.info("Provider response");
			// 	printOut(response);
			// }
		}
	}

	// public void getMessageOrchestrationAndConsumption() {

	// 	OrchestrationResponseDTO orchestrationResponse = getOrchestrationResponse(HelloConsumerConstants.GET_MESSAGE_SERVICE_DEFINITION);
	// 	// Check for a valid response
	// 	if (orchestrationResponse == null) {
	// 		logger.info("No orchestration response received");
	// 	} else if (orchestrationResponse.getResponse().isEmpty()) {
	// 		logger.info("No provider found during the orchestration");
	// 	} else {
	// 		// Valid response received
	// 		final OrchestrationResultDTO orchestrationResult = orchestrationResponse.getResponse().get(0);
	// 		validateOrchestrationResult(orchestrationResult, HelloConsumerConstants.GET_MESSAGE_SERVICE_DEFINITION);
			
	// 		final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
	// 		// Create a hello request
	// 		// Send a request to the provider and get a response
	// 		for (int i = 0; i < 3; i++) {
	// 			@SuppressWarnings("unchecked")
	// 			final List<GetMessageResponseDTO> response = arrowheadService.consumeServiceHTTP(List.class, HttpMethod.valueOf(orchestrationResult.getMetadata().get(HelloConsumerConstants.HTTP_METHOD)),
	// 					orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri(),
	// 					getInterface(), token, null, new String[0]);
	// 			logger.info("Provider response");
	// 			printOut(response);
	// 		}
	// 	}
	// }

    
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
