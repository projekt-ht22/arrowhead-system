package ai.aitia.robot_controller.common.dto;

import java.io.Serializable;

import ai.aitia.robot_controller.common.Message;

public class GetMessageResponseDTO implements Serializable{

    private static final long serialVersionUID = 123122L;

    private Message message;

    public GetMessageResponseDTO() {}
    public GetMessageResponseDTO(final Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
