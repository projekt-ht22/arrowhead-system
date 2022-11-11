package ai.aitia.mission_executor.common.dto;

import java.io.Serializable;

import ai.aitia.mission_scheduler.common.Mission;


public class DoMissionResponseDTO implements Serializable{

	// This is used by serializable to version the class
	private static final long serialVersionUID = 1232432L;

	public enum Status {
		STARTED,
		ERROR
	}

	//=================================================================================================
	// members

	private Status status;

	//=================================================================================================
	// methods
	
	// This empty constructor is needed
	public DoMissionResponseDTO() {}
	//-------------------------------------------------------------------------------------------------
	public DoMissionResponseDTO(final Status status) {
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
