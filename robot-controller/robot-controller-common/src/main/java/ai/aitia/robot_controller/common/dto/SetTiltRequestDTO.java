package ai.aitia.robot_controller.common.dto;

import java.io.Serializable;

public class SetTiltRequestDTO implements Serializable{

	// This is used by serializable to version the class
	private static final long serialVersionUID = 144;

	//=================================================================================================
	// members

	private byte tilt;
	//=================================================================================================
	// methods
	
	// This empty constructor is needed
	public SetTiltRequestDTO() {}
	//-------------------------------------------------------------------------------------------------
	public SetTiltRequestDTO(final byte tilt) {
		this.tilt = tilt;
	}

	//-------------------------------------------------------------------------------------------------
	// Each member needs to have a public getter and setter

	public byte getTilt() {
		return tilt;
	}

	public void setTilt(byte tilt) {
		this.tilt = tilt;
	}
}
