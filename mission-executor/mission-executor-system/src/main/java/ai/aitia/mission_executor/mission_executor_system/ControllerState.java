package ai.aitia.mission_executor.mission_executor_system;

import ai.aitia.mission_scheduler.common.Mission;

public class ControllerState {
    private boolean isRunning;
    private int currentTaskIndex;
    private Mission currentMission;

    public ControllerState() {
        isRunning = false;
        currentTaskIndex = 0;
        currentMission = null;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public Mission getCurrentMission() {
        return currentMission;
    }

    public int getCurrentTaskIndex() {
        return currentTaskIndex;
    }

    public void setCurrentMission(Mission currentMission) {
        this.currentMission = currentMission;
    }

    public void setCurrentTaskIndex(int currentTaskIndex) {
        this.currentTaskIndex = currentTaskIndex;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
}
