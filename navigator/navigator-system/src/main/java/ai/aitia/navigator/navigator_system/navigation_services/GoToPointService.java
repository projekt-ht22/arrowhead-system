package ai.aitia.navigator.navigator_system.navigation_services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ai.aitia.mission_scheduler.common.GPSPoint;
import ai.aitia.navigator.navigator_system.PIDController;
import ai.aitia.navigator.navigator_system.StaticFunctions;

public class GoToPointService implements Runnable {
    private final Logger logger = LogManager.getLogger(GoToPointService.class);

    private GPSPoint currentPosition;
    private double currentHeading;

    private double simlat = 65;
    private double simlon = 22;

    private GPSPoint goalPosition;

    private Boolean stop = false;

    public GoToPointService(GPSPoint goal) {
        this.goalPosition = goal;
    }

    public void stop() {
        synchronized(stop) {
            stop = true;
        }
    }

    private void simulateUpdateCurrent() {
        currentPosition = new GPSPoint(simlat, simlon);
        simlat += 0.0000001;
        currentHeading = 0;
        //       65.61686316645734
    }

    @Override
    public void run() {
        // create pid
        PIDController pid = new PIDController(1.2, 0.01, 0);

        // do orchestrations


        while (true) {
            synchronized(stop) {
                if (stop) {
                    return;
                }
            }

            // update current position and heading
            simulateUpdateCurrent();

            // calculate distance to goal
            double distance = StaticFunctions.calculateDistance(goalPosition, currentPosition);

            // check if at goal position
            if (distance < 0.03) {
                // send ready to executor
                return;
            }

            // calculate bearing to goal
            double bearing = StaticFunctions.calculateBearing(currentPosition, goalPosition);

            // Calculate control value using pid and difference between headings
            double e = bearing - currentHeading;
            double u = pid.getNextU(e);

            // set speeds of tracks
            double leftRPM = (4 + u) * 1000;
            double rightRPM = (4 - u) * 1000;

            // cap rpm
            leftRPM = leftRPM < 1000 ? 1000 : leftRPM;
            rightRPM = rightRPM < 1000 ? 1000 : rightRPM;
            leftRPM = leftRPM > 7000 ? 7000 : leftRPM;
            rightRPM = rightRPM > 7000 ? 7000 : rightRPM;

            logger.info("goal: lat: {} lon: {} current: lat: {} lon: {}",
                goalPosition.getLatitude(), goalPosition.getLongitude(),
                currentPosition.getLatitude(), currentPosition.getLongitude());
            logger.info("bearing: {} heading: {} e: {}", bearing, currentHeading, e);
            logger.info("u: {} leftRPM: {} rightRPM: {}", u, leftRPM, rightRPM);
        }
    }
    
}
