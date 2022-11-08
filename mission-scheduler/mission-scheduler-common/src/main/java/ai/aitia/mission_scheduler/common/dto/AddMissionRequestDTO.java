package ai.aitia.mission_scheduler.common.dto;

import java.io.Serializable;

import ai.aitia.mission_scheduler.common.Mission;

public class AddMissionRequestDTO implements Serializable{

	// This is used by serializable to version the class
	private static final long serialVersionUID = 144;

	//=================================================================================================
	// members

	private Mission mission;
	private int priority;

	//=================================================================================================
	// methods
	
	// This empty constructor is needed
	public AddMissionRequestDTO() {}
	//-------------------------------------------------------------------------------------------------
	public AddMissionRequestDTO(final Mission mission, final int priority) {
		this.mission = mission;
		this.priority = priority;
	}

	//-------------------------------------------------------------------------------------------------
	// Each member needs to have a public getter and setter

	public Mission getMission() {
		return mission;
	}

	public int getPriority() {
		return priority;
	}

	public void setMission(Mission mission) {
		this.mission = mission;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
}
