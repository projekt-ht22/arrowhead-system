package ai.aitia.navigator.common;

import java.io.Serializable;
import java.util.List;

import ai.aitia.mission_scheduler.common.GPSPoint;

public class FollowPathRequestDTO implements Serializable {

    private List<GPSPoint> path;
    private int remindWhenLeft; // The number of points left to navigate to when a reminder to send more is sent out

    public FollowPathRequestDTO() {}
    public FollowPathRequestDTO(List<GPSPoint> path) {
        this.path = path;
    }

    public List<GPSPoint> getPath() {
        return path;
    }

    public void setPath(List<GPSPoint> path) {
        this.path = path;
    }
}
