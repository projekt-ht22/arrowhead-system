package ai.aitia.snowblower.snowblower_provider.controller;

import java.io.Serializable;
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

import ai.aitia.snowblower.common.dto.GetAccessoryIDResponseDTO;
import ai.aitia.snowblower.common.dto.GetAccessoryStopStartResponseDTO;
import ai.aitia.snowblower.common.dto.SetAccessoryStopStartResponseDTO;
import ai.aitia.snowblower.common.dto.GetAccessoryCommandResponseDTO;
import ai.aitia.snowblower.common.dto.SetBlowerRequestDTO;
import ai.aitia.snowblower.common.dto.GetAccessoryCommandResponseDTO.commandTypes;
import ai.aitia.snowblower.snowblower_provider.AccessoryProviderConstants;
import ai.aitia.snowblower.snowblower_provider.blowerInterface.BlowerInterface;


import eu.arrowhead.common.Utilities;

@RestController
public class AccessoryController {
	
	//=================================================================================================
	// members	

	@Autowired
	private BlowerInterface blowerInterface;
	
    private final Logger logger = LogManager.getLogger(AccessoryController.class);
	//=================================================================================================
	// methods

	@GetMapping(path = AccessoryProviderConstants.GET_ACCESSORY_ID_URI, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public GetAccessoryIDResponseDTO getAccessoryID() {

		return new GetAccessoryIDResponseDTO();
	}


	@PostMapping(path = AccessoryProviderConstants.START_ACCESSORY_URI, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public GetAccessoryStopStartResponseDTO startAccessory(@RequestBody final SetAccessoryStopStartResponseDTO dto) {

		return new GetAccessoryStopStartResponseDTO(dto.isActivated());
	}

	@PostMapping(path = AccessoryProviderConstants.STOP_ACCESSORY_URI, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public GetAccessoryStopStartResponseDTO stopAccessory(@RequestBody final SetAccessoryStopStartResponseDTO dto) {

		return new GetAccessoryStopStartResponseDTO(dto.isActivated());
	}


	@PostMapping(path = AccessoryProviderConstants.COMMAND_ACCESSORY_URI, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public GetAccessoryCommandResponseDTO commandAccessory(@RequestBody final SetBlowerRequestDTO dto) {

		switch (dto.getCommandType()) {
			case SNOWBLOWER_HORIZONTAL_ANGEL:
				blowerInterface.setPwmPin12(dto.getPwm_pin12());
				return new GetAccessoryCommandResponseDTO(getAccessoryID(), 0, commandTypes.SNOWBLOWER_HORIZONTAL_ANGEL, dto.getPwm_pin12());
			case SNOWBLOWER_VERTICAL_ANGLE:
				blowerInterface.setPwmPin13(dto.getPwm_pin13());
				return new GetAccessoryCommandResponseDTO(getAccessoryID(), 0, commandTypes.SNOWBLOWER_VERTICAL_ANGLE, dto.getPwm_pin13());
			case SNOWBLOWER_RPM:
				blowerInterface.SetRpm(dto.getrpm());
				return new GetAccessoryCommandResponseDTO(getAccessoryID(), 0, commandTypes.SNOWBLOWER_RPM, dto.getrpm());
			default:
				return new GetAccessoryCommandResponseDTO(getAccessoryID(), 0, commandTypes.UNKNOWN_COMMAND, 0);
		}
	}

    private void printOut(final Object object) {
    	System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
    }
}
