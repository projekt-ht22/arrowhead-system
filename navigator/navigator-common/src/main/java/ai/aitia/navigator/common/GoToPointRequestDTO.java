package ai.aitia.navigator.common;

import java.io.Serializable;

import ai.aitia.mission_scheduler.common.GPSPoint;

public class GoToPointRequestDTO implements Serializable {

    private GPSPoint point;

    public GoToPointRequestDTO() {}
    public GoToPointRequestDTO(GPSPoint point) {
        this.point = point;
    }

    public GPSPoint getPoint() {
        return point;
    }

    public void setPoint(GPSPoint point) {
        this.point = point;
    }
}
