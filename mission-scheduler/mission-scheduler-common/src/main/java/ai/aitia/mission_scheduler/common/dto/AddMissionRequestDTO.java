package ai.aitia.mission_scheduler.common.dto;

import java.io.Serializable;

public class AddMissionRequestDTO implements Serializable{

	// This is used by serializable to version the class
	private static final long serialVersionUID = 144;

	//=================================================================================================
	// members

	private int times;

	//=================================================================================================
	// methods
	
	// This empty constructor is needed
	public AddMissionRequestDTO() {}
	//-------------------------------------------------------------------------------------------------
	public AddMissionRequestDTO(final int times) {
		this.times = times;
	}

	//-------------------------------------------------------------------------------------------------
	// Each member needs to have a public getter and setter

	public int getTimes() {
		return this.times;
	}
	//-------------------------------------------------------------------------------------------------
	public void setTimes(final int times) {
		this.times = times;
	}
}
