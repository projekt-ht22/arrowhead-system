package ai.aitia.gps_controller.gps_controller_provider.controller;

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

import ai.aitia.gps_controller.common.dto.GetGPSAccuracyResponseDTO;
import ai.aitia.gps_controller.common.dto.GetGPSAccuracyResponseDTO.Navigation_status;
import ai.aitia.gps_controller.common.dto.GetGPSCordinatesResponseDTO;
import ai.aitia.gps_controller.common.dto.GetGPSHeadingResponseDTO;
import ai.aitia.gps_controller.gps_controller_provider.GPSProviderConstants;
import ai.aitia.gps_controller.gps_controller_provider.gpsinterface.GPSInterface;


import eu.arrowhead.common.Utilities;

@RestController
public class GetGPSDataController {
	
	//=================================================================================================
	// members	

	@Autowired
	private GPSInterface gpsInterface;
	
    private final Logger logger = LogManager.getLogger(GetGPSDataController.class);
	//=================================================================================================
	// methods

	@GetMapping(path = GPSProviderConstants.GET_GPS_CORDINATES_URI, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public GetGPSCordinatesResponseDTO getCordinates() {

		synchronized (gpsInterface) {
			return new GetGPSCordinatesResponseDTO(gpsInterface.getLatitude(), gpsInterface.getLongitude());
		}
	}


	@GetMapping(path = GPSProviderConstants.GET_GPS_HEADING_URI, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public GetGPSHeadingResponseDTO getHeading() {

		synchronized (gpsInterface) {
			return new GetGPSHeadingResponseDTO(gpsInterface.getHeading());
		}
	}


	@GetMapping(path = GPSProviderConstants.GET_GPS_ACCURACY_URI, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public GetGPSAccuracyResponseDTO getAccuracy() {

		synchronized (gpsInterface) {

			// logger.info("nav_status: {}", gpsInterface.getNavigation_status());

			Navigation_status nav_status = Navigation_status.DO_NOT_USE;

			switch(gpsInterface.getNavigation_status()) {
				case 1:
					nav_status = Navigation_status.NO_SATELLITES;
					break;
				case 2:
					nav_status = Navigation_status.INITIALISING;
				  	break;
				case 3:
				  	nav_status = Navigation_status.LOCKING;
					break;
				case 4:
					nav_status = Navigation_status.REAL_TIME_DATA;
					break;
				case 20:
				  	nav_status = Navigation_status.TRIGGER_PACKET;
					break;
				case 21:
				  	nav_status = Navigation_status.TRIGGER_PACKET;
					break;
				case 22:
				  	nav_status = Navigation_status.TRIGGER_PACKET;
					break;
				default:
				  	// defult to Navigation_status.DO_NOT_USE
			}
			  

			return new GetGPSAccuracyResponseDTO(nav_status, gpsInterface.getSatellites(), gpsInterface.getNorth_velocity_accuracy(), gpsInterface.getEast_velocity_accuracy(), gpsInterface.getHeading_accuracy());
		}
	}

    private void printOut(final Object object) {
    	System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
    }
}
