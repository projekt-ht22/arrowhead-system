package ai.aitia.gps_controller.gps_controller_provider;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import ai.aitia.gps_controller.gps_controller_provider.gpsinterface.GPSInterface;
import eu.arrowhead.common.CommonConstants;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, GPSProviderConstants.BASE_PACKAGE})
public class GPSProviderMain {

	//=================================================================================================
	// methods

    // private final Logger logger = LogManager.getLogger(GPSProviderMain.class);
    
	//-------------------------------------------------------------------------------------------------
	public static void main(final String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(GPSProviderMain.class, args);

		// Get GPSInterface from autowire
		GPSInterface gpsInterface = context.getBean(GPSInterface.class);

		// loop get data (blocks when no data is received)
		while (true) {
			
			// System.out.println(gpsInterface.getNavigation_status());

			try {
				gpsInterface.getData();
			} catch (IOException  e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}