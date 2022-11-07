package ai.aitia.mission_scheduler.mission_scheduler_provider.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ai.aitia.mission_scheduler.common.dto.AddMissionRequestDTO;
import ai.aitia.mission_scheduler.common.dto.AddMissionResponseDTO;
import ai.aitia.mission_scheduler.mission_scheduler_provider.MissionSchedulerProviderConstants;

import eu.arrowhead.common.Utilities;

@RestController
@RequestMapping(MissionSchedulerProviderConstants.ADD_MISSION_URI)
public class HelloServiceController {
	
	//=================================================================================================
	// members	
	
    private final Logger logger = LogManager.getLogger(HelloServiceController.class);
	//=================================================================================================
	// methods

	// POST mapping for the hello service
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public AddMissionResponseDTO hello(@RequestBody final AddMissionRequestDTO dto) {
		logger.info("Handle request.");

		String ret = "";

		for (int n = 0; n < dto.getTimes(); n++) {
			ret += "hello ";
		}

		return new AddMissionResponseDTO(ret);
	}

    private void printOut(final Object object) {
    	System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
    }
}
