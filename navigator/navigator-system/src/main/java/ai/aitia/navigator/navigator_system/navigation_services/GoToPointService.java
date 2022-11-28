package ai.aitia.navigator.navigator_system.navigation_services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpMethod;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.gps_controller.common.dto.GetGPSAccuracyResponseDTO;
import ai.aitia.gps_controller.common.dto.GetGPSCordinatesResponseDTO;
import ai.aitia.gps_controller.common.dto.GetGPSHeadingResponseDTO;
import ai.aitia.gps_controller.common.dto.GetGPSAccuracyResponseDTO.Navigation_status;
import ai.aitia.mission_scheduler.common.GPSPoint;
import ai.aitia.navigator.navigator_system.NavigatorSystemConstants;
import ai.aitia.navigator.navigator_system.PIDController;
import ai.aitia.navigator.navigator_system.StaticFunctions;
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

public class GoToPointService implements Runnable {
    private final Logger logger = LogManager.getLogger(GoToPointService.class);

    private GPSPoint currentPosition;
    private double currentHeading;

    private double simlat = 65;
    private double simlon = 22;

    private GPSPoint goalPosition;

    private Boolean stop = false;

    private SSLProperties sslProperties;
    private ArrowheadService arrowheadService;

    public GoToPointService(GPSPoint goal, SSLProperties sslProperties, ArrowheadService arrowheadService) {
        this.goalPosition = goal;
        this.sslProperties = sslProperties;
        this.arrowheadService = arrowheadService;
    }

    public void stop() {
        synchronized(stop) {
            stop = true;
        }
    }

    private void simulateUpdateCurrent() {
        currentPosition = new GPSPoint(simlat, simlon);
        simlat += 0.0000001;
        currentHeading = 0;
        //       65.61686316645734
    }

    private OrchestrationResultDTO getOrchestrationResultBlocking(String serviceDefinition) {
        OrchestrationResponseDTO response = null;
        while (true) {
            response = getOrchestrationResponse(serviceDefinition);
            
            if(response == null) {
                continue;
            } else if (response.getResponse().isEmpty()) {
                continue;
            } else {
                break;
            }
        }
        return response.getResponse().get(0);
    }

    @Override
    public void run() {
        // create pid
        PIDController pid = new PIDController(1.2, 0.01, 0);

        // do orchestrations
        final OrchestrationResultDTO getCoordinates = getOrchestrationResultBlocking(NavigatorSystemConstants.GET_GPS_CORDINATES_SERVICE_DEFINITION);
        final OrchestrationResultDTO getHeading = getOrchestrationResultBlocking(NavigatorSystemConstants.GET_GPS_HEADING_SERVICE_DEFINITION);
        final OrchestrationResultDTO getAccuracy = getOrchestrationResultBlocking(NavigatorSystemConstants.GET_GPS_ACCURACY_SERVICE_DEFINITION);
        final OrchestrationResultDTO setTrackSpeed = getOrchestrationResultBlocking(NavigatorSystemConstants.SET_TRACK_SPEED_SERVICE_DEFINITION);

        Navigation_status navigationStatus = consumeServiceResponse(getAccuracy, GetGPSAccuracyResponseDTO.class).getNavigation_status();
        while (navigationStatus != Navigation_status.REAL_TIME_DATA) {
            navigationStatus = consumeServiceResponse(getAccuracy, GetGPSAccuracyResponseDTO.class).getNavigation_status();
        }

        while (true) {
            synchronized(stop) {
                if (stop) {
                    return;
                }
            }

            // update current position and heading
            //simulateUpdateCurrent();

            GetGPSCordinatesResponseDTO gpsResponse = consumeServiceResponse(getCoordinates, GetGPSCordinatesResponseDTO.class);
            double lat = gpsResponse.getLatitude();
            double lon = gpsResponse.getLongitude();

            currentPosition = new GPSPoint(lat, lon);

            // calculate distance to goal
            double distance = StaticFunctions.calculateDistance(goalPosition, currentPosition);

            // check if at goal position
            if (distance < 0.03) {
                // send ready to executor
                return;
            }

            // calculate bearing to goal
            double bearing = StaticFunctions.calculateBearing(currentPosition, goalPosition);

            // update heading
            GetGPSHeadingResponseDTO gpsheading = consumeServiceResponse(getHeading, GetGPSHeadingResponseDTO.class);
            currentHeading = gpsheading.getHeading();

            // Calculate control value using pid and difference between headings
            double e = bearing - currentHeading;
            double u = pid.getNextU(e);

            // set speeds of tracks
            double leftRPM = (4 + u) * 1000;
            double rightRPM = (4 - u) * 1000;

            // cap rpm
            leftRPM = leftRPM < 1000 ? 1000 : leftRPM;
            rightRPM = rightRPM < 1000 ? 1000 : rightRPM;
            leftRPM = leftRPM > 7000 ? 7000 : leftRPM;
            rightRPM = rightRPM > 7000 ? 7000 : rightRPM;

            //logger.info("goal: lat: {} lon: {} current: lat: {} lon: {}",
            //    goalPosition.getLatitude(), goalPosition.getLongitude(),
            //    currentPosition.getLatitude(), currentPosition.getLongitude());
            //logger.info("bearing: {} heading: {} e: {}", bearing, currentHeading, e);
            logger.info("u: {} leftRPM: {} rightRPM: {}", u, leftRPM, rightRPM);
        }
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
		T ret = arrowheadService.consumeServiceHTTP(responseType, HttpMethod.valueOf(orchestrationResult.getMetadata().get(NavigatorSystemConstants.HTTP_METHOD)),
				orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri(),
				getInterface(), token, request, new String[0]);
		
		return ret;
	}

	private <T> T consumeServiceResponse(OrchestrationResultDTO orchestrationResult, Class<T> responseType) {
		// Get the security token
		final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
		logger.info("Consume service");
		@SuppressWarnings("unchecked")

		// Send a request to the provider and get a response
		T ret = arrowheadService.consumeServiceHTTP(responseType, HttpMethod.valueOf(orchestrationResult.getMetadata().get(NavigatorSystemConstants.HTTP_METHOD)),
				orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri(),
				getInterface(), token, null, new String[0]);
		
		return ret;
	}

    /*
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
    */

    
    //=================================================================================================
	// assistant methods
    
    //-------------------------------------------------------------------------------------------------
    private String getInterface() {
    	return sslProperties.isSslEnabled() ? NavigatorSystemConstants.INTERFACE_SECURE : NavigatorSystemConstants.INTERFACE_INSECURE;
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
