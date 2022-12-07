package ai.aitia.navigator.navigator_system.navigation_services;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpMethod;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.gps_controller.common.dto.GetGPSAccuracyResponseDTO;
import ai.aitia.gps_controller.common.dto.GetGPSCordinatesResponseDTO;
import ai.aitia.gps_controller.common.dto.GetGPSHeadingResponseDTO;
import ai.aitia.gps_controller.common.dto.GetGPSAccuracyResponseDTO.Navigation_status;
import ai.aitia.mission_scheduler.common.GPSPoint;
import ai.aitia.navigator.navigator_system.NavigatorState;
import ai.aitia.navigator.navigator_system.NavigatorSystemConstants;
import ai.aitia.navigator.navigator_system.PIDController;
import ai.aitia.navigator.navigator_system.StaticFunctions;
import ai.aitia.robot_controller.common.dto.AddMessageResponseDTO;
import ai.aitia.robot_controller.common.dto.SetSpeedRequestDTO;
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

public class FollowPathService implements Runnable {
    private final Logger logger = LogManager.getLogger(FollowPathService.class);

    private GPSPoint currentPosition;
    private double currentHeading;

    private double simlat = 65;
    private double simlon = 22;

    private Boolean running = false;

    private SSLProperties sslProperties;
    private ArrowheadService arrowheadService;

    private Queue<GPSPoint> path;
    private NavigatorState state;

    public FollowPathService(SSLProperties sslProperties, ArrowheadService arrowheadService, NavigatorState state) {
        this.sslProperties = sslProperties;
        this.arrowheadService = arrowheadService;
        this.state = state;

        path = new LinkedList<>();
    }

    public Boolean getRunning() {
        synchronized(running) {
            return running;
        }
    }

    public void addToQueue(List<GPSPoint> toAdd) {
        synchronized(path) {
            path.addAll(toAdd);
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

    @Override
    public void run() {
        logger.info("start follow path service");
        synchronized(running) {
            running = true;
        }
        // create pid
        PIDController pid = new PIDController(1.2, 0.01, 0);
        logger.info("Start orchestration");

        // do orchestrations
        final OrchestrationResultDTO setTrackSpeed = getOrchestrationResultBlocking(NavigatorSystemConstants.SET_TRACK_SPEED_SERVICE_DEFINITION);
        logger.info("orchestration for setTrackSpeed received.");
        final OrchestrationResultDTO getAccuracy = getOrchestrationResultBlocking(NavigatorSystemConstants.GET_GPS_ACCURACY_SERVICE_DEFINITION);
        logger.info("orchestration for getAccuracy received.");
        final OrchestrationResultDTO getCoordinates = getOrchestrationResultBlocking(NavigatorSystemConstants.GET_GPS_CORDINATES_SERVICE_DEFINITION);
        logger.info("orchestration for getCoordinates received.");
        final OrchestrationResultDTO getHeading = getOrchestrationResultBlocking(NavigatorSystemConstants.GET_GPS_HEADING_SERVICE_DEFINITION);
        logger.info("orchestration for getHeading received.");


        logger.info("Wait until gps is ready.");
        Navigation_status navigationStatus = consumeServiceResponse(getAccuracy, GetGPSAccuracyResponseDTO.class).getNavigation_status();
        // while (navigationStatus != Navigation_status.REAL_TIME_DATA) {
        //     logger.info("GPS not ready trying again. Status: {}", navigationStatus);
        //     navigationStatus = consumeServiceResponse(getAccuracy, GetGPSAccuracyResponseDTO.class).getNavigation_status();
        //     try {
        //         Thread.sleep(2000);
        //     } catch (InterruptedException e) {
        //         // TODO Auto-generated catch block
        //         e.printStackTrace();
        //     }
        // }

        // setup first goal position
        while (path.size() == 0); // do not need to protect here. Why is left as a exercise for the reader

        GPSPoint goalPosition;
        synchronized(path) {
            goalPosition = path.remove();
        }

        logger.info("GPS ready start running.");

        while (true) {
            if (state.shouldStop()) {
                SetSpeedRequestDTO request = new SetSpeedRequestDTO(0, 0);
                AddMessageResponseDTO response = consumeServiceRequestAndResponse(setTrackSpeed, request, AddMessageResponseDTO.class);
                synchronized(running) {
                    running = false;
                }
                return;
            }

            // update current position and heading
            simulateUpdateCurrent();
            currentPosition = new GPSPoint(simlat, simlon);

            GetGPSCordinatesResponseDTO gpsResponse = consumeServiceResponse(getCoordinates, GetGPSCordinatesResponseDTO.class);
            //double lat = gpsResponse.getLatitude();
            //double lon = gpsResponse.getLongitude();
            //currentPosition = new GPSPoint(lat, lon);

            // calculate distance to goal
            double distance = StaticFunctions.calculateDistance(goalPosition, currentPosition);

            // check if at goal position
            if (distance < 0.1) {
                // send ready to executor
                if (path.size() != 0) {
                    synchronized(path) {
                        goalPosition = path.remove();
                    }
                } else {
                    synchronized(running) {
                        running = false;
                    }
                    return;
                }
            }

            // calculate bearing to goal
            double bearing = StaticFunctions.calculateBearing(currentPosition, goalPosition);

            // update heading
            GetGPSHeadingResponseDTO gpsheading = consumeServiceResponse(getHeading, GetGPSHeadingResponseDTO.class);
            currentHeading = gpsheading.getHeading();
            //currentHeading = 0;

            // Calculate control value using pid and difference between headings
            double e = bearing - currentHeading;

            if (e < -1 * Math.PI) {
                e = e + 2 * Math.PI;
            } else if (e > Math.PI) {
                e = e - 2 * Math.PI;
            }

            double u = pid.getNextU(e);

            // set speeds of tracks
            double leftRPM = (4 + u) * 1000;
            double rightRPM = (4 - u) * 1000;

            // cap rpm
            leftRPM = leftRPM < 1000 ? 1000 : leftRPM;
            rightRPM = rightRPM < 1000 ? 1000 : rightRPM;
            leftRPM = leftRPM > 7000 ? 7000 : leftRPM;
            rightRPM = rightRPM > 7000 ? 7000 : rightRPM;

            SetSpeedRequestDTO request = new SetSpeedRequestDTO(((Double)leftRPM).intValue(), ((Double)rightRPM).intValue());
            AddMessageResponseDTO response = consumeServiceRequestAndResponse(setTrackSpeed, request, AddMessageResponseDTO.class);

            logger.info("goal: lat: {} lon: {} current: lat: {} lon: {}",
                goalPosition.getLatitude(), goalPosition.getLongitude(),
                currentPosition.getLatitude(), currentPosition.getLongitude());
            logger.info("bearing: {} heading: {} e: {}", bearing, currentHeading, e);
            logger.info("u: {} leftRPM: {} rightRPM: {}", u, leftRPM, rightRPM);
            logger.info("Distance: {}", distance);
        }
    }

	private OrchestrationResponseDTO getOrchestrationResponse(String serviceDefinition) {
		// Create a request for the orchestrator asking for the hello service
        //printOut(serviceDefinition);
    	final ServiceQueryFormDTO serviceQueryForm = new ServiceQueryFormDTO.Builder(serviceDefinition)
    																		.interfaces(getInterface())
    																		.build();
		final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
		final OrchestrationFormRequestDTO orchestrationFormRequest = orchestrationFormBuilder.requestedService(serviceQueryForm)
																					   .flag(Flag.MATCHMAKING, true)
																					   .flag(Flag.OVERRIDE_STORE, true)
																					   .build();
		
		//printOut(orchestrationFormRequest);		
		
		// Send request to orchestrator and get a response
		final OrchestrationResponseDTO orchestrationResponse = arrowheadService.proceedOrchestration(orchestrationFormRequest);
        //printOut(orchestrationResponse);
		return orchestrationResponse;
	}

	private <T, E> T consumeServiceRequestAndResponse(OrchestrationResultDTO orchestrationResult, E request, Class<T> responseType) {
		// Get the security token
		final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
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
		@SuppressWarnings("unchecked")

		// Send a request to the provider and get a response
		T ret = arrowheadService.consumeServiceHTTP(responseType, HttpMethod.valueOf(orchestrationResult.getMetadata().get(NavigatorSystemConstants.HTTP_METHOD)),
				orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri(),
				getInterface(), token, null, new String[0]);
		
		return ret;
	}

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
