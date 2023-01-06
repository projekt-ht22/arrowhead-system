package ai.aitia.navigator.navigator_system.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.mission_scheduler.common.GPSPoint;
import ai.aitia.navigator.common.FollowPathRequestDTO;
import ai.aitia.navigator.common.GoToPointRequestDTO;
import ai.aitia.navigator.common.NavigatorResponseDTO;
import ai.aitia.navigator.common.NavigatorResponseDTO.NavigatorStatus;
import ai.aitia.navigator.navigator_system.NavigatorState;
import ai.aitia.navigator.navigator_system.NavigatorSystemConstants;
import ai.aitia.navigator.navigator_system.navigation_services.FollowPathService;
import ai.aitia.navigator.navigator_system.navigation_services.GoToPointService;
import ai.aitia.robot_controller.common.dto.AddMessageResponseDTO;
import ai.aitia.robot_controller.common.dto.SetSpeedRequestDTO;;

@RestController
public class NavigatorServiceController {
	
	//=================================================================================================
	// members	
	@Autowired
	private ArrowheadService arrowheadService;
	@Autowired
	protected SSLProperties sslProperties;

	@Autowired
	protected NavigatorState state;
	
    private final Logger logger = LogManager.getLogger(NavigatorServiceController.class);
	//=================================================================================================
	// methods

	private Thread currentThread;
	private GoToPointService currentGoToPointService;
	private FollowPathService currentFollowPathService;

	// POST mapping for the hello service
	@PostMapping(path = NavigatorSystemConstants.GO_TO_POINT_URI, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public NavigatorResponseDTO hello(@RequestBody final GoToPointRequestDTO dto) {
		logger.info("Handle request.");

		if (currentThread != null) {
			// terminate current
			logger.info("------------------------- interupted");

			state.cancel();
			try {
				currentThread.join();
			} catch (InterruptedException e) {
				logger.info("Interrupted doing join. This should never happen continuing.");
			}
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			logger.info("interupted during sleep");
		}

		state.notCancel();

		currentGoToPointService = new GoToPointService(dto.getPoint(), sslProperties, arrowheadService, state, dto.getTaskID());
		currentThread = new Thread(currentGoToPointService);

		currentThread.start();

		return new NavigatorResponseDTO(NavigatorStatus.OK);
	}

	private void endThread() {
		if (currentThread != null) {
			// terminate current
			logger.info("------------------------- interupted");

			state.cancel();

			logger.info("Wait on current thread");
			try {
				currentThread.join();
			} catch (InterruptedException e) {
				logger.info("Interrupted doing join. This should never happen continuing.");
			}
		}
		state.notCancel();
	}

	@PostMapping(path = NavigatorSystemConstants.FOLLOW_PATH_URI, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public NavigatorResponseDTO followPath(@RequestBody final FollowPathRequestDTO dto) {
		logger.info("Se if thread is running");

		// This block should not be here TODO: move


		List<GPSPoint> path = dto.getPath();

		if (currentFollowPathService == null) { // create new path follower
			currentFollowPathService = new FollowPathService(sslProperties, arrowheadService, state, dto.getTaskID());
			currentFollowPathService.addToQueue(path);
			currentThread = new Thread(currentFollowPathService);
			currentThread.start();
			logger.info("Created new path follower and started");
		} else if (!currentFollowPathService.getRunning()) { // use old path follower
			currentFollowPathService.resetTaskId(dto.getTaskID());
			currentFollowPathService.addToQueue(path);
			currentThread = new Thread(currentFollowPathService);
			currentThread.start();
			logger.info("Started new thread with new follower");
		} else { // add to running path follower
			currentFollowPathService.addToQueue(path);
			logger.info("added to running follower");
		}

		return new NavigatorResponseDTO(NavigatorStatus.OK);
	}

	@GetMapping(path = NavigatorSystemConstants.STOP_URI, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public NavigatorResponseDTO stop() {
		state.stop();
        final OrchestrationResultDTO setTrackSpeed = getOrchestrationResultBlocking(NavigatorSystemConstants.SET_TRACK_SPEED_SERVICE_DEFINITION);

		SetSpeedRequestDTO request = new SetSpeedRequestDTO(0, 0);
		AddMessageResponseDTO response = consumeServiceRequestAndResponse(setTrackSpeed, request, AddMessageResponseDTO.class);

		return new NavigatorResponseDTO(NavigatorStatus.OK);
	}

	@GetMapping(path = NavigatorSystemConstants.START_URI, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public NavigatorResponseDTO start() {
		state.start();

		return new NavigatorResponseDTO(NavigatorStatus.OK);
	}

    private AddMessageResponseDTO consumeServiceRequestAndResponse(OrchestrationResultDTO setTrackSpeed,
			SetSpeedRequestDTO request, Class<AddMessageResponseDTO> class1) {
		return null;
	}

	private OrchestrationResultDTO getOrchestrationResultBlocking(String setTrackSpeedServiceDefinition) {
		return null;
	}

	private void printOut(final Object object) {
    	System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
    }
}
