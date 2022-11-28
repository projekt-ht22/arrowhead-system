package ai.aitia.mission_scheduler.common.dto;

import java.io.Serializable;

public class AddMissionResponseDTO implements Serializable{

	public enum Status{
		ADDED,
		ERROR
	}

	// This is used by serializable to version the class
	private static final long serialVersionUID = 244L;

	//=================================================================================================
	// members

	private Status status;

	//=================================================================================================
	// methods
	// This empty constructor is needed
	public AddMissionResponseDTO() {}
	
	//-------------------------------------------------------------------------------------------------
	public AddMissionResponseDTO(final Status status) {
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
