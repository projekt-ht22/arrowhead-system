package ai.aitia.navigator.common;

import java.io.Serializable;

public class NavigatorResponseDTO implements Serializable {
    public enum NavigatorStatus {
        ERROR, OK, STOPPED
    }

    private NavigatorStatus status;

    public NavigatorResponseDTO () {}
    public NavigatorResponseDTO (NavigatorStatus status) {
        this.status = status;
    }

    public NavigatorStatus getStatus() {
        return status;
    }

    public void setStatus(NavigatorStatus status) {
        this.status = status;
    }
}