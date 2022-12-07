package ai.aitia.navigator.navigator_tester;

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
import ai.aitia.mission_scheduler.common.GPSPoint;
import ai.aitia.navigator.common.FollowPathRequestDTO;
import ai.aitia.navigator.common.GoToPointRequestDTO;
import ai.aitia.navigator.common.NavigatorResponseDTO;
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
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, NavigatorTesterConstants.BASE_PACKAGE})
public class NavigatorTesterMain implements ApplicationRunner {
    
    //=================================================================================================
	// members
	
    @Autowired
	private ArrowheadService arrowheadService;
    
    @Autowired
	protected SSLProperties sslProperties;
    
    private final Logger logger = LogManager.getLogger(NavigatorTesterMain.class);
    
    //=================================================================================================
	// methods

	//------------------------------------------------------------------------------------------------
    public static void main( final String[] args ) {
    	SpringApplication.run(NavigatorTesterMain.class, args);
    }

    //-------------------------------------------------------------------------------------------------
	// This function is started after the consumer is initialized
    @Override
	public void run(final ApplicationArguments args) throws Exception {
		//goToPointRequest();
		followPathRequest();
	}
    

	public void goToPointRequest() {
    	logger.info("Orchestration request for " + NavigatorTesterConstants.GO_TO_POINT_SERVICE_DEFINITION + " service:");

		// Create a request for the orchestrator asking for the hello service
    	final ServiceQueryFormDTO serviceQueryForm = new ServiceQueryFormDTO.Builder(NavigatorTesterConstants.GO_TO_POINT_SERVICE_DEFINITION)
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
			validateOrchestrationResult(orchestrationResult, NavigatorTesterConstants.GO_TO_POINT_SERVICE_DEFINITION);
			
			// Create a hello request
			logger.info("Create a hello request:");
			//65.6171979879289, 22.13895483376356
			final GoToPointRequestDTO request = new GoToPointRequestDTO(new GPSPoint(65.61719922092598, 22.13900041548656), 0);
			printOut(request);
			
			// Get the security token
			final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
			logger.info("Consume service");

			@SuppressWarnings("unchecked")
			// Send a request to the provider and get a response
			final NavigatorResponseDTO response = arrowheadService.consumeServiceHTTP(NavigatorResponseDTO.class, HttpMethod.valueOf(orchestrationResult.getMetadata().get(NavigatorTesterConstants.HTTP_METHOD)),
					orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri(),
					getInterface(), token, request, new String[0]);
			logger.info("Provider response");
			printOut(response);
		}
	}

	public void followPathRequest() {
    	logger.info("Orchestration request for " + NavigatorTesterConstants.FOLLOW_PATH_SERVICE_DEFINITION + " service:");

		// Create a request for the orchestrator asking for the hello service
    	final ServiceQueryFormDTO serviceQueryForm = new ServiceQueryFormDTO.Builder(NavigatorTesterConstants.FOLLOW_PATH_SERVICE_DEFINITION)
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
			validateOrchestrationResult(orchestrationResult, NavigatorTesterConstants.FOLLOW_PATH_SERVICE_DEFINITION);
			
			// Create a hello request
			logger.info("Create a hello request:");

			List points = new ArrayList<GPSPoint>();
			points.add(new GPSPoint(65.61728942110328, 22.13903160985256));
			points.add(new GPSPoint(65.61723970518048, 22.139100855434997));
			points.add(new GPSPoint(65.61715643079656, 22.139176122372437));
			points.add(new GPSPoint(65.61707937068101, 22.139243862616127));
			points.add(new GPSPoint(65.61700479615794, 22.139307086843573));
			points.add(new GPSPoint(65.61698428812655, 22.139197197114918));
			points.add(new GPSPoint(65.61698677394939, 22.139120424838733));
			points.add(new GPSPoint(65.61698739540506, 22.138992471045096));
			points.add(new GPSPoint(65.61704581217214, 22.13894881622138));
			points.add(new GPSPoint(65.6170911782942, 22.138888602671432));
			points.add(new GPSPoint(65.61714835192421, 22.13884344250897));
			points.add(new GPSPoint(65.61720117527484, 22.13887957063894));
			points.add(new GPSPoint(65.61726456315378, 22.138960858931373));
			points.add(new GPSPoint(65.61727512778523, 22.139063221966282));

			FollowPathRequestDTO request = new FollowPathRequestDTO(points, 0, 0);
			printOut(request);
			
			// Get the security token
			final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
			logger.info("Consume service");

			@SuppressWarnings("unchecked")
			// Send a request to the provider and get a response
			final NavigatorResponseDTO response = arrowheadService.consumeServiceHTTP(NavigatorResponseDTO.class, HttpMethod.valueOf(orchestrationResult.getMetadata().get(NavigatorTesterConstants.HTTP_METHOD)),
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
    	return sslProperties.isSslEnabled() ? NavigatorTesterConstants.INTERFACE_SECURE : NavigatorTesterConstants.INTERFACE_INSECURE;
    }
    
    //-------------------------------------------------------------------------------------------------
    private void validateOrchestrationResult(final OrchestrationResultDTO orchestrationResult, final String serviceDefinition) {
    	if (!orchestrationResult.getService().getServiceDefinition().equalsIgnoreCase(serviceDefinition)) {
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
