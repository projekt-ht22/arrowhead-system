package ai.aitia.snowblower.common.dto;

import java.io.Serializable;


public class GetAccessoryCommandResponseDTO implements Serializable{

    private static final long serialVersionUID = 1491L;

    public enum commandTypes {
        SNOWBLOWER_HORIZONTAL_ANGEL,
        SNOWBLOWER_VERTICAL_ANGLE,
        SNOWBLOWER_RPM,
        UNKNOWN_COMMAND,
      };

    private GetAccessoryIDResponseDTO accessoryID;
    private int taskId;
    private commandTypes commandType;
    private int value;


    public GetAccessoryCommandResponseDTO() {}
    public GetAccessoryCommandResponseDTO(final GetAccessoryIDResponseDTO accessoryID, final int taskId, final commandTypes commandType, final int value) {

        this.accessoryID = accessoryID;
        this.taskId = taskId;
        this.commandType = commandType;
        this.value = value;
    }


    public GetAccessoryIDResponseDTO getAccessoryID() {
        return accessoryID;
    }
    public void setAccessoryID(GetAccessoryIDResponseDTO accessoryID) {
        this.accessoryID = accessoryID;
    }
    public int getTaskId() {
        return taskId;
    }
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
    public commandTypes getCommandType() {
        return commandType;
    }
    public void setCommandType(commandTypes commandType) {
        this.commandType = commandType;
    }
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }    
}
