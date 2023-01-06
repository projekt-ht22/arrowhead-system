package ai.aitia.mission_executor.mission_executor_system;

import org.springframework.stereotype.Component;

import ai.aitia.mission_scheduler.common.Mission;

@Component
public class ControllerState {
    private boolean isRunning;
    private int currentTaskIndex;
    private Mission currentMission;
    private long currentTaskId;
    private long lastFinishedTaskId;

    public ControllerState() {
        isRunning = false;
        currentTaskIndex = 0;
        currentMission = null;
        currentTaskId = 0;
        lastFinishedTaskId = -1;
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }

    public synchronized Mission getCurrentMission() {
        return currentMission;
    }

    public synchronized int getCurrentTaskIndex() {
        return currentTaskIndex;
    }

    public synchronized void setCurrentMission(Mission currentMission) {
        this.currentMission = currentMission;
    }

    public synchronized void setCurrentTaskIndex(int currentTaskIndex) {
        this.currentTaskIndex = currentTaskIndex;
    }

    public synchronized void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public synchronized long changeTaskId() {
        currentTaskId += 1;
        return currentTaskId;
    }

    public synchronized void setLastFinishedTaskId(long lastFinishedTaskId) {
        this.lastFinishedTaskId = lastFinishedTaskId;
    }

    public synchronized boolean isLastFinishedTaskIdCurrentTaskId() {
        return lastFinishedTaskId == currentTaskId;
    }

    public synchronized boolean isCurentTaskIdThisTaskId(long thisTaskId) {
        return currentTaskId == thisTaskId;
    }
}
