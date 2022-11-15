package ai.aitia.mission_scheduler.common;

import java.io.Serializable;

import org.jose4j.json.internal.json_simple.JSONObject;

public class GPSPoint implements Serializable {
    private double longitude;
    private double latitude;

    public GPSPoint() {}
    public GPSPoint(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public GPSPoint(JSONObject json) {
        this.longitude = (double) json.get("longitude");
        this.latitude = (double) json.get("latitude");
    }

    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
    
}
