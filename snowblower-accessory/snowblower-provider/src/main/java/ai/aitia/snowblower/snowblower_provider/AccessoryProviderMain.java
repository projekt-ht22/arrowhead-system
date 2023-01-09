package ai.aitia.snowblower.snowblower_provider;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

// import ai.aitia.snowblower.snowblower_provider.gpsinterface.GPSInterface;
import eu.arrowhead.common.CommonConstants;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, AccessoryProviderConstants.BASE_PACKAGE})
public class AccessoryProviderMain {

	//=================================================================================================
	// methods

    // private final Logger logger = LogManager.getLogger(AccessoryProviderMain.class);
    
	//-------------------------------------------------------------------------------------------------
	public static void main(final String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(AccessoryProviderMain.class, args);

		// // Get snowblowerInterface from autowire
		// snowblowerInterface snowblowerInterface = context.getBean(GPSInterface.class);

		// // loop get data (blocks when no data is received)
		// while (true) {
			
		// 	// System.out.println(gpsInterface.getNavigation_status());

		// 	try {
		// 		gpsInterface.getData();
		// 	} catch (IOException e) {
		// 		// TODO Auto-generated catch block
		// 		e.printStackTrace();
		// 	}
		// }
	}
}