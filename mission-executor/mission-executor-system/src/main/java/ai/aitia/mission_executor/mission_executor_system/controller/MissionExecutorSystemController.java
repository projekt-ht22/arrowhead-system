package ai.aitia.mission_executor.mission_executor_system.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.ContainerElementNodeBuilderCustomizableContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ai.aitia.mission_executor.common.dto.DoMissionRequestDTO;
import ai.aitia.mission_executor.common.dto.DoMissionResponseDTO;
import ai.aitia.mission_executor.common.dto.HelloRequestDTO;
import eu.arrowhead.common.Utilities;
import ai.aitia.mission_executor.common.dto.HelloResponseDTO;
import ai.aitia.mission_executor.common.dto.DoMissionResponseDTO.Status;
import ai.aitia.mission_executor.mission_executor_system.ControllerState;
import ai.aitia.mission_executor.mission_executor_system.DoMissionService;
import ai.aitia.mission_executor.mission_executor_system.MissionExecutorSystemConstants;;

@RestController
@RequestMapping(MissionExecutorSystemConstants.DO_MISSION_URI)
public class MissionExecutorSystemController {
	
	//=================================================================================================
	// members	
	@Autowired
	private ControllerState controllerState;

    private final Logger logger = LogManager.getLogger(MissionExecutorSystemConstants.class);
	//=================================================================================================
	// methods

	// POST mapping for the hello service
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public DoMissionResponseDTO doMission(@RequestBody final DoMissionRequestDTO dto) {
		logger.info("Handle request do mission.");

		if (controllerState.isRunning()) {
			// replace mission
			// add reminder of mission to scheduler
		}

		DoMissionService doMissionService = new DoMissionService(dto.getMission(), controllerState);

		Thread t = new Thread(doMissionService);

		t.start();

		return new DoMissionResponseDTO(Status.STARTED);
	}

    private void printOut(final Object object) {
    	System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
    }
}
