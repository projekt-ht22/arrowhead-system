package ai.aitia.navigator.navigator_system;

import ai.aitia.mission_scheduler.common.GPSPoint;

public class StaticFunctions {
    public static double calculateBearing(GPSPoint current, GPSPoint goal) {
        double currentLatRad = Math.toRadians(current.getLatitude());
        double currentLongRad = Math.toRadians(current.getLongitude());
        double goalLatRad = Math.toRadians(goal.getLatitude());
        double goalLongRad = Math.toRadians(goal.getLongitude());

        double y = Math.cos(goalLatRad) * Math.sin(goalLongRad - currentLongRad);
        double x = Math.cos(currentLatRad) * Math.sin(goalLatRad) - Math.sin(currentLatRad) * Math.cos(goalLatRad) * Math.cos(goalLongRad - currentLongRad);

        return Math.atan2(y, x);
    }
}
