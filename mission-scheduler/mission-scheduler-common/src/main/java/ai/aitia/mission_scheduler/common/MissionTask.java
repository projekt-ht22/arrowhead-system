package ai.aitia.mission_scheduler.common;

import java.io.Serializable;

import org.jose4j.json.internal.json_simple.JSONObject;

public class MissionTask implements Serializable {
    
    public enum TaskType {
        GO_TO_POINT, FOLLOW_PATH, NONE
    }

    private TaskType type;
    private String description;

    private static TaskType taskTypeFromString(String str) {
        switch (str) {
            case "GO_TO_POINT":
                return TaskType.GO_TO_POINT;
            case "FOLLOW_PATH":
                return TaskType.FOLLOW_PATH;
            default:
                return TaskType.NONE;
        }
    }

    public MissionTask() {}
    public MissionTask(TaskType type, String description) {
        this.type = type;
        this.description = description;
    }
    public MissionTask(JSONObject json) {
        this.description = (String) json.get("description");
        this.type = taskTypeFromString((String) json.get("type"));
    }

    public String getDescription() {
        return description;
    }
    public TaskType getType() {
        return type;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public void setType(TaskType type) {
        this.type = type;
    }
}
