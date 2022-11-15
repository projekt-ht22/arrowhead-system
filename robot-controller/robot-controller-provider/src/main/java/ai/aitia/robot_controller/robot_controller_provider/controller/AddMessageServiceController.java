package ai.aitia.robot_controller.robot_controller_provider.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ai.aitia.robot_controller.common.Message;
import ai.aitia.robot_controller.common.dto.AddMessageRequestDTO;
import ai.aitia.robot_controller.common.dto.SetSpeedRequestDTO;
import ai.aitia.robot_controller.common.dto.SetTiltRequestDTO;
import ai.aitia.robot_controller.common.dto.AddMessageResponseDTO;
import ai.aitia.robot_controller.common.dto.GetMessageResponseDTO;
import ai.aitia.robot_controller.common.dto.AddMessageResponseDTO.Status;
import ai.aitia.robot_controller.robot_controller_provider.RobotControllerProviderConstants;
import ai.aitia.robot_controller.robot_controller_provider.serial.Serial;
import eu.arrowhead.common.Utilities;


@RestController
public class AddMessageServiceController {
	
	//=================================================================================================
	// members	

	@Autowired
	private Serial serial;
	
    private final Logger logger = LogManager.getLogger(AddMessageServiceController.class);
	//=================================================================================================
	// methods

	// POST mapping for the hello service
	@PostMapping(path = RobotControllerProviderConstants.SET_TRACK_SPEED_URI, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public AddMessageResponseDTO setTrackSpeed(@RequestBody final SetSpeedRequestDTO dto) {
		logger.info("Handle request.");

		int baud = serial.get_baud();
		String s2=String.valueOf(baud);
		logger.info(s2);
		
		try {
			serial.set_speed(dto.getLeftTrackSpeed(), dto.getRightTrackSpeed());
			logger.info("Send speed.");
			return new AddMessageResponseDTO(Status.SENT);
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("no port.");
			return new AddMessageResponseDTO(Status.ERROR);
		}
	}

	// POST mapping for the hello service
	@PostMapping(path = RobotControllerProviderConstants.SET_TILT_AMOUNT_URI, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public AddMessageResponseDTO setTiltAmount(@RequestBody final SetTiltRequestDTO dto) {
		logger.info("Handle request.");

		int baud = serial.get_baud();
		String s2=String.valueOf(baud);
		logger.info(s2);
		
		try {
			serial.set_tilt(dto.getTilt());
			logger.info("Send tilt.");
			return new AddMessageResponseDTO(Status.SENT);
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("no port.");
			return new AddMessageResponseDTO(Status.ERROR);
		}
	}

	// This is only for debugging and should never be used in the real system
	@GetMapping(path = RobotControllerProviderConstants.GET_MESSAGE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public Message getNextMessage() {
		logger.info("Handle request.");

		serial.read_data();



		return null;

	}


    private void printOut(final Object object) {
    	System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
    }
}
