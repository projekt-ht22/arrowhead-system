package ai.aitia.robot_controller.common.dto;

import java.io.Serializable;

import ai.aitia.robot_controller.common.Message;

public class AddMessageRequestDTO implements Serializable{

	// This is used by serializable to version the class
	private static final long serialVersionUID = 144;

	//=================================================================================================
	// members

	private Message message;
	//=================================================================================================
	// methods
	
	// This empty constructor is needed
	public AddMessageRequestDTO() {}
	//-------------------------------------------------------------------------------------------------
	public AddMessageRequestDTO(final Message message) {
		this.message = message;
	}

	//-------------------------------------------------------------------------------------------------
	// Each member needs to have a public getter and setter

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}
}
