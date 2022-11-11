package ai.aitia.mission_executor.mission_executor_system;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import ai.aitia.mission_scheduler.common.Mission;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DoMissionService implements Runnable {
    private final Logger logger = LogManager.getLogger(DoMissionService.class);
    
    private Mission mission;
    private ControllerState state;

    public DoMissionService(Mission mission, ControllerState state) {
        this.mission = mission;
        this.state = state;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
        logger.info("Started mission");
        for (String n : mission.getTasks()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.info("unable to wait aborting");
                return;
            }
            logger.info("doing: {}", n);
        }

        logger.info("done with mission");
    }
}
