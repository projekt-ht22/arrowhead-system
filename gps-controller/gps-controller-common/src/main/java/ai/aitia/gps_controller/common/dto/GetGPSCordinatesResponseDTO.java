package ai.aitia.gps_controller.common.dto;

import java.io.Serializable;


public class GetGPSCordinatesResponseDTO implements Serializable{

    private static final long serialVersionUID = 12922L;

    private double latitude;
    private double longitude;

    public GetGPSCordinatesResponseDTO() {}
    public GetGPSCordinatesResponseDTO(final double latitude, final double longitude) {

        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
