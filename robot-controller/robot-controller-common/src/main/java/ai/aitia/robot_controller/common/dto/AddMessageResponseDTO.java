package ai.aitia.robot_controller.common.dto;

import java.io.Serializable;

public class AddMessageResponseDTO implements Serializable{

	public enum Status implements Serializable{
		SENT,
		ERROR
	}

	// This is used by serializable to version the class
	private static final long serialVersionUID = 240L;

	//=================================================================================================
	// members

	private Status status;

	//=================================================================================================
	// methods
	// This empty constructor is needed
	public AddMessageResponseDTO() {}
	
	//-------------------------------------------------------------------------------------------------
	public AddMessageResponseDTO(final Status status) {
		this.status = status;
	}

	//-------------------------------------------------------------------------------------------------
	// Each member needs to have a public getter and setter

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
