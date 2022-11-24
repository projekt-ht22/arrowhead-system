package ai.aitia.navigator.navigator_system;

import ai.aitia.mission_scheduler.common.GPSPoint;

public class StaticFunctions {

    // calculate the bearing in radians from two gps points
    public static double calculateBearing(GPSPoint current, GPSPoint goal) {
        double currentLatRad = Math.toRadians(current.getLatitude());
        double currentLongRad = Math.toRadians(current.getLongitude());
        double goalLatRad = Math.toRadians(goal.getLatitude());
        double goalLongRad = Math.toRadians(goal.getLongitude());

        double y = Math.cos(goalLatRad) * Math.sin(goalLongRad - currentLongRad);
        double x = Math.cos(currentLatRad) * Math.sin(goalLatRad) - Math.sin(currentLatRad) * Math.cos(goalLatRad) * Math.cos(goalLongRad - currentLongRad);

        return Math.atan2(y, x);
    }

    // calculate the distance in meters between two gps points
    // Estimates in a way that is okej for close points
    public static double calculateDistance(GPSPoint p1, GPSPoint p2) {
        // solution taken from here https://www.movable-type.co.uk/scripts/latlong.html

        double lat1rad = Math.toRadians(p1.getLatitude());
        double lon1rad = Math.toRadians(p1.getLongitude());
        double lat2rad = Math.toRadians(p2.getLatitude());
        double lon2rad = Math.toRadians(p2.getLongitude());

        double R = 6356800; // earth radius "nominal "zero tide" polar" from https://en.wikipedia.org/wiki/Earth_radius

        double deltalat = lat2rad - lat1rad;
        double deltalon = lon2rad - lon1rad;

        double a = Math.sin(deltalat / 2) * Math.sin(deltalat / 2) + Math.cos(lat1rad) * Math.cos(lat2rad) * Math.sin(deltalon / 2) * Math.sin(deltalon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    public static void main(String[] args) {
        // should be around 14m
        System.out.println(
        calculateDistance(
            //65.61686316645734, 22.139252438508013
            new GPSPoint(65.61686316645734, 22.139252438508013),
            //65.61686319901285, 22.13955817173417
            new GPSPoint(65.61686319901285, 22.13955817173417)
        ));
    }
}
