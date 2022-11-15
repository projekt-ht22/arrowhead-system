package ai.aitia.mission_scheduler.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jose4j.json.internal.json_simple.JSONArray;
import org.jose4j.json.internal.json_simple.JSONObject;

import ai.aitia.mission_scheduler.common.MissionTask.TaskType;

public class Mission implements Serializable{
    private List<MissionTask> tasks;
    private String name;
    private int priority;

    public Mission() {}
    public Mission(final List<MissionTask> tasks,final String name, final int priority) {
        this.tasks = tasks;
        this.name = name;
        this.priority = priority;
    }
    public Mission(JSONObject json) {
        this.name = (String) json.get("name");
        this.priority = ((Long) json.get("priority")).intValue();

        this.tasks = new ArrayList<>();
        JSONArray tasksJson = (JSONArray) json.get("tasks");
        for (Object taskObject : tasksJson) {
            MissionTask ttask = new MissionTask((JSONObject) taskObject);
            switch (ttask.getType()) {
                case GO_TO_POINT:
                    this.tasks.add(new GoToPointTask((JSONObject) taskObject));
                    break;
                case FOLLOW_PATH:
                    this.tasks.add(new FollowPathTask((JSONObject) taskObject));
                    break;
            }
        }
    }

    public String getName() {
        return name;
    }
    public List<MissionTask> getTasks() {
        return tasks;
    }
    public int getPriority() {
        return priority;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setTasks(List<MissionTask> tasks) {
        this.tasks = tasks;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }
}
