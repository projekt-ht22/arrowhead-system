package ai.aitia.navigator.common;

import java.io.Serializable;

import ai.aitia.mission_scheduler.common.GPSPoint;

public class GoToPointRequestDTO implements Serializable {

    private GPSPoint point;
    private int missionID;

    public GoToPointRequestDTO() {}
    public GoToPointRequestDTO(GPSPoint point, int missionID) {
        this.point = point;
        this.missionID = missionID;
    }

    public GPSPoint getPoint() {
        return point;
    }
    public int getMissionID() {
        return missionID;
    }

    public void setPoint(GPSPoint point) {
        this.point = point;
    }
    public void setMissionID(int missionID) {
        this.missionID = missionID;
    }
}
