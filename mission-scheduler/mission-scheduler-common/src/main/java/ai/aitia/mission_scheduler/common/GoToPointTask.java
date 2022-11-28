package ai.aitia.mission_scheduler.common;

import org.jose4j.json.internal.json_simple.JSONObject;

public class GoToPointTask extends MissionTask {

    private GPSPoint point;

    public GoToPointTask() {
        super(TaskType.GO_TO_POINT, "");
    }
    public GoToPointTask(String description, Double longitude, Double latitude) {
        super(TaskType.GO_TO_POINT, description);
        this.point = new GPSPoint(longitude, latitude);
    }
    public GoToPointTask(String description, GPSPoint point) {
        super(TaskType.GO_TO_POINT, description);
        this.point = point;
    }
    public GoToPointTask(JSONObject json) {
        super(json);
        this.point = new GPSPoint((JSONObject) json.get("point"));
    }

    public GPSPoint getPoint() {
        return point;
    }
    public void setPoint(GPSPoint point) {
        this.point = point;
    }
}
