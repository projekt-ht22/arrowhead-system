package ai.aitia.mission_scheduler.common;

import java.io.Serializable;
import java.util.List;

public class Mission implements Serializable{
    private List<String> tasks;
    private String name;
    private int priority;

    public Mission() {}
    public Mission(final List<String> tasks,final String name, final int priority) {
        this.tasks = tasks;
        this.name = name;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }
    public List<String> getTasks() {
        return tasks;
    }
    public int getPriority() {
        return priority;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setTasks(List<String> tasks) {
        this.tasks = tasks;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }
}
