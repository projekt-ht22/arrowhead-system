package ai.aitia.hello.hello_provider.controller;

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

import ai.aitia.hello.common.dto.HelloRequestDTO;
import ai.aitia.hello.common.dto.HelloResponseDTO;
import ai.aitia.hello.hello_provider.HelloProviderConstants;
import eu.arrowhead.common.Utilities;

@RestController
@RequestMapping(HelloProviderConstants.HELLO_URI)
public class HelloServiceController {
	
	//=================================================================================================
	// members	
	
    private final Logger logger = LogManager.getLogger(HelloServiceController.class);
	//=================================================================================================
	// methods

	// POST mapping for the hello service
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public List<HelloResponseDTO> hello(@RequestBody final HelloRequestDTO dto) {
		logger.info("Handle request.");

		List <HelloResponseDTO> ret = new ArrayList<HelloResponseDTO>();

		for (int n = 0; n < dto.getTimes(); n++) {
			ret.add(new HelloResponseDTO("Hello"));
		}

		return ret;
	}

    private void printOut(final Object object) {
    	System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
    }
}
