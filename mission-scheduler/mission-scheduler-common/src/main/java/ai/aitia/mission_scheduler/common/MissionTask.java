package ai.aitia.mission_scheduler.common;

import java.io.Serializable;

public class MissionTask implements Serializable {
    
    public enum TaskType {
        GO_TO_POINT, FOLLOW_PATH
    }

    private TaskType type;
    private String description;

    public MissionTask() {}
    public MissionTask(TaskType type, String description) {
        this.type = type;
        this.description = description;
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
