package ai.aitia.mission_executor.mission_executor_system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import ai.aitia.mission_scheduler.common.Mission;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DoMissionService implements Runnable {
    private final Logger logger = LogManager.getLogger(DoMissionService.class);
    
    private Mission mission;
	private ControllerState controllerState;

    public DoMissionService(Mission mission, ControllerState controllerState) {
        this.mission = mission;
        this.controllerState = controllerState;
    }

    @Override
    public void run() {
        List<String> tasks = mission.getTasks();

        logger.info("Started mission");
        for (int i = 0; i < tasks.size(); i++) {
            String n = tasks.get(i);
            synchronized(controllerState) {
                if (!controllerState.isRunning()) {
                    return;
                }
                controllerState.setCurrentTaskIndex(i);
            }
            logger.info("doing: {}", n);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                logger.info("interrupted");
            }
        }

        logger.info("done with mission");
        synchronized(controllerState) {
            controllerState.setRunning(false);
        }
    }
}
