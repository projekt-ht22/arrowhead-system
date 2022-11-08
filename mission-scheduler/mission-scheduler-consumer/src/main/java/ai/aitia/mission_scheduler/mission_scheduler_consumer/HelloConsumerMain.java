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
import ai.aitia.mission_scheduler.common.Mission;
import ai.aitia.mission_scheduler.common.dto.AddMissionRequestDTO;
import ai.aitia.mission_scheduler.common.dto.AddMissionResponseDTO;
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
		helloOrchestrationAndConsumption();
	}
    

	public void helloOrchestrationAndConsumption() {
    	logger.info("Orchestration request for " + HelloConsumerConstants.ADD_MISSION_SERVICE_DEFINITION + " service:");

		// Create a request for the orchestrator asking for the hello service
    	final ServiceQueryFormDTO serviceQueryForm = new ServiceQueryFormDTO.Builder(HelloConsumerConstants.ADD_MISSION_SERVICE_DEFINITION)
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
			
			// Create a hello request
			final List<String> tasks = new ArrayList<>();
			tasks.add("go to place");
			tasks.add("plow");
			tasks.add("done");

			final Mission mission = new Mission(tasks, "plow mission", 2);
			printOut(mission);

			logger.info("Create a hello request:");
			final AddMissionRequestDTO request = new AddMissionRequestDTO(mission, 0);
			printOut(request);
			
			// Get the security token
			final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
			logger.info("Consume service");
			@SuppressWarnings("unchecked")

			// Send a request to the provider and get a response
			final AddMissionResponseDTO response = arrowheadService.consumeServiceHTTP(AddMissionResponseDTO.class, HttpMethod.valueOf(orchestrationResult.getMetadata().get(HelloConsumerConstants.HTTP_METHOD)),
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
