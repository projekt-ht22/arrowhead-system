package ai.aitia.gps_controller.common.dto;

import java.io.Serializable;


public class GetGPSHeadingResponseDTO implements Serializable{

    private static final long serialVersionUID = 12921L;

    private double heading;

    public GetGPSHeadingResponseDTO() {}
    public GetGPSHeadingResponseDTO(final double heading) {

        this.heading = heading;
    }

    public double getHeading() {
        return heading;
    }
    public void setHeading(double heading) {
        this.heading = heading;
    }

}
