package ai.aitia.navigator.common;

import java.io.Serializable;
import java.util.List;

import ai.aitia.mission_scheduler.common.GPSPoint;

public class FollowPathRequestDTO implements Serializable {

    private List<GPSPoint> path;
    private int remindWhenLeft; // The number of points left to navigate to when a reminder to send more is sent out
    private int missionID;

    public FollowPathRequestDTO() {}
    public FollowPathRequestDTO(List<GPSPoint> path, int remindWhenLeft, int missionID) {
        this.path = path;
        this.missionID = missionID;
        this.remindWhenLeft = remindWhenLeft;
    }

    public List<GPSPoint> getPath() {
        return path;
    }
    public int getMissionID() {
        return missionID;
    }
    public int getRemindWhenLeft() {
        return remindWhenLeft;
    }

    public void setPath(List<GPSPoint> path) {
        this.path = path;
    }
    public void setMissionID(int missionID) {
        this.missionID = missionID;
    }
    public void setRemindWhenLeft(int remindWhenLeft) {
        this.remindWhenLeft = remindWhenLeft;
    }
}
