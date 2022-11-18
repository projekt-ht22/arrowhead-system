package ai.aitia.robot_controller.common;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable{
    private int messageID;
    private List<Integer> data;

    public Message() {}
    public Message(final int messageID, final List<Integer> data) {
        this.messageID = messageID;
        this.data = data;
    }

    public List<Integer> getData() {
        return data;
    }
    public int getMessageID() {
        return messageID;
    }

    public void setData(List<Integer> data) {
        this.data = data;
    }
    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }
}
