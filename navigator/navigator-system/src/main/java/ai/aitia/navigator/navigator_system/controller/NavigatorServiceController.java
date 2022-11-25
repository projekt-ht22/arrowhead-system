package ai.aitia.navigator.navigator_system.controller;

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

import eu.arrowhead.common.Utilities;
import ai.aitia.navigator.common.GoToPointRequestDTO;
import ai.aitia.navigator.common.NavigatorResponseDTO;
import ai.aitia.navigator.common.NavigatorResponseDTO.NavigatorStatus;
import ai.aitia.navigator.navigator_system.NavigatorSystemConstants;
import ai.aitia.navigator.navigator_system.navigation_services.GoToPointService;;

@RestController
public class NavigatorServiceController {
	
	//=================================================================================================
	// members	
	
    private final Logger logger = LogManager.getLogger(NavigatorServiceController.class);
	//=================================================================================================
	// methods

	// POST mapping for the hello service
	@PostMapping(path = NavigatorSystemConstants.GO_TO_POINT_URI, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public NavigatorResponseDTO hello(@RequestBody final GoToPointRequestDTO dto) {
		logger.info("Handle request.");

		GoToPointService runner = new GoToPointService(dto.getPoint());
		Thread t = new Thread(runner);

		t.start();

		return new NavigatorResponseDTO(NavigatorStatus.OK);
	}

    private void printOut(final Object object) {
    	System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
    }
}
