package ai.aitia.mission_scheduler.mission_scheduler_provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import eu.arrowhead.common.CommonConstants;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, MissionSchedulerProviderConstants.BASE_PACKAGE})
public class MissionSchedulerProviderMain {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static void main(final String[] args) {
		SpringApplication.run(MissionSchedulerProviderMain.class, args);
	}	
}
