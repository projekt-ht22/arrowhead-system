package ai.aitia.mission_executor.common.dto;

import java.io.Serializable;

import ai.aitia.mission_scheduler.common.Mission;

public class TaskDoneRequestDTO implements Serializable{

	// This is used by serializable to version the class
	private static final long serialVersionUID = 423124342334L;

	//=================================================================================================
	// members

	private long id;


	//=================================================================================================
	// methods
	
	// This empty constructor is needed
	public TaskDoneRequestDTO() {}
	//-------------------------------------------------------------------------------------------------
	public TaskDoneRequestDTO(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	//-------------------------------------------------------------------------------------------------
	// Each member needs to have a public getter and setter
}
