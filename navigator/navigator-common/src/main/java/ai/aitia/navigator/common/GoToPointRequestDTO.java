package ai.aitia.navigator.common;

import java.io.Serializable;

import ai.aitia.mission_scheduler.common.GPSPoint;

public class GoToPointRequestDTO implements Serializable {

    private GPSPoint point;
    private long taskID;

    public GoToPointRequestDTO() {}
    public GoToPointRequestDTO(GPSPoint point, long taskID) {
        this.point = point;
        this.taskID = taskID;
    }

    public GPSPoint getPoint() {
        return point;
    }
    public long getTaskID() {
        return taskID;
    }

    public void setPoint(GPSPoint point) {
        this.point = point;
    }
    public void setTaskID(long taskID) {
        this.taskID = taskID;
    }
}
