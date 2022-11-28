package ai.aitia.mission_executor.common.dto;

import java.io.Serializable;

import ai.aitia.mission_scheduler.common.Mission;

public class DoMissionRequestDTO implements Serializable{

	// This is used by serializable to version the class
	private static final long serialVersionUID = 1232432L;

	//=================================================================================================
	// members

	private Mission mission;

	//=================================================================================================
	// methods
	
	// This empty constructor is needed
	public DoMissionRequestDTO() {}
	//-------------------------------------------------------------------------------------------------
	public DoMissionRequestDTO(final Mission mission) {
		this.mission = mission;
	}

	//-------------------------------------------------------------------------------------------------
	// Each member needs to have a public getter and setter

	public Mission getMission() {
		return mission;
	}

	public void setMission(Mission mission) {
		this.mission = mission;
	}
}
