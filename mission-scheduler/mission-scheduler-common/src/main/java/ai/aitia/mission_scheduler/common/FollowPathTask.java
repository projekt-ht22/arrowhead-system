package ai.aitia.mission_scheduler.common;

import java.util.ArrayList;
import java.util.List;

import org.jose4j.json.internal.json_simple.JSONArray;
import org.jose4j.json.internal.json_simple.JSONObject;

public class FollowPathTask extends MissionTask {
    private List<GPSPoint> points;

    public FollowPathTask() {
        super(TaskType.FOLLOW_PATH, "");
    }
    public FollowPathTask(String description, List<GPSPoint> points) {
        super(TaskType.FOLLOW_PATH, description);
        this.points = points;
    }
    public FollowPathTask(JSONObject json) {
        super(json);
        this.points = new ArrayList<>();

        JSONArray pointsJson = (JSONArray) json.get("points");
        for (Object pointObject : pointsJson) {
            this.points.add(new GPSPoint((JSONObject) pointObject));
        }
    }

    public List<GPSPoint> getPoints() {
        return points;
    }

    public void setPoints(List<GPSPoint> points) {
        this.points = points;
    }
    
}
