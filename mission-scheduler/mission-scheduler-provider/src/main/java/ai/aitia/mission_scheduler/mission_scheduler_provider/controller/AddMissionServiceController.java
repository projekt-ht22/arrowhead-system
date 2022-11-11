package ai.aitia.mission_scheduler.mission_scheduler_provider.controller;

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

import ai.aitia.mission_scheduler.common.Mission;
import ai.aitia.mission_scheduler.common.dto.AddMissionRequestDTO;
import ai.aitia.mission_scheduler.common.dto.AddMissionResponseDTO;
import ai.aitia.mission_scheduler.common.dto.GetNextMissionResponseDTO;
import ai.aitia.mission_scheduler.common.dto.AddMissionResponseDTO.Status;
import ai.aitia.mission_scheduler.mission_scheduler_provider.MissionSchedulerProviderConstants;
import ai.aitia.mission_scheduler.mission_scheduler_provider.scheduler.Scheduler;
import eu.arrowhead.common.Utilities;

@RestController
public class AddMissionServiceController {
	
	//=================================================================================================
	// members	

	@Autowired
	private Scheduler scheduler;
	
    private final Logger logger = LogManager.getLogger(AddMissionServiceController.class);
	//=================================================================================================
	// methods

	// POST mapping for the hello service
	@PostMapping(path = MissionSchedulerProviderConstants.ADD_MISSION_URI, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public AddMissionResponseDTO addMission(@RequestBody final AddMissionRequestDTO dto) {
		logger.info("Handle request.");

		Mission mission = dto.getMission();

		scheduler.addMission(mission);


		return new AddMissionResponseDTO(Status.ADDED);
	}


    private void printOut(final Object object) {
    	System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
    }
}
