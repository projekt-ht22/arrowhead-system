package ai.aitia.robot_controller.common.dto;

import java.io.Serializable;

public class SetSpeedRequestDTO implements Serializable {

	// This is used by serializable to version the class
	private static final long serialVersionUID = 178L;

	//=================================================================================================
	// members

	private int left_track;
	private int right_track;

	//=================================================================================================
	// methods
	
	// This empty constructor is needed
	public SetSpeedRequestDTO() {}
	//-------------------------------------------------------------------------------------------------
	public SetSpeedRequestDTO(final int left_track,final int right_track) {
		this.left_track = left_track;
		this.right_track = right_track;
	}

	//-------------------------------------------------------------------------------------------------
	// Each member needs to have a public getter and setter

	public int getRightTrackSpeed() {
		return this.right_track;
	}

	public int getLeftTrackSpeed() {
		return this.left_track;
	}

	public void setRightTrackSpeed(int right_track) {
		this.right_track = right_track;
	}

	public void setLeftTrackSpeed(int left_track) {
		this.left_track = left_track;
	}
}
