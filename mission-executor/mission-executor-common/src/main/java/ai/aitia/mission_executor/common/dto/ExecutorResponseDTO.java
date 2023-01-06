package ai.aitia.mission_executor.common.dto;

import java.io.Serializable;

import ai.aitia.mission_scheduler.common.Mission;


public class ExecutorResponseDTO implements Serializable{

	// This is used by serializable to version the class
	private static final long serialVersionUID = 1232432L;

	//=================================================================================================
	// members

	private String status;

	//=================================================================================================
	// methods
	
	// This empty constructor is needed
	//-------------------------------------------------------------------------------------------------
	public ExecutorResponseDTO() {
		this.status = "Ok";
	}

	//-------------------------------------------------------------------------------------------------
	// Each member needs to have a public getter and setter

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
