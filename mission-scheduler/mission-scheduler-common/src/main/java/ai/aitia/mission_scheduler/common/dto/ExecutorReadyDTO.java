package ai.aitia.mission_scheduler.common.dto;

import java.io.Serializable;

public class ExecutorReadyDTO implements Serializable {
	private static final long serialVersionUID = 7890745L;
    
    public enum ExecutorStatus {
        READY,
        NOT_READY
    }

    private ExecutorStatus status;

    public ExecutorReadyDTO() {}
    public ExecutorReadyDTO(ExecutorStatus status) {
        this.status = status;
    }

    public ExecutorStatus getStatus() {
        return status;
    }
    public void setStatus(ExecutorStatus status) {
        this.status = status;
    }
}
