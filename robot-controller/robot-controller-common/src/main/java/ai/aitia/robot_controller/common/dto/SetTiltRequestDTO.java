package ai.aitia.robot_controller.common.dto;

import java.io.Serializable;

import ai.aitia.robot_controller.common.Message;

public class SetTiltRequestDTO implements Serializable{

	// This is used by serializable to version the class
	private static final long serialVersionUID = 144;

	//=================================================================================================
	// members

	private Integer tilt;
	//=================================================================================================
	// methods
	
	// This empty constructor is needed
	public SetTiltRequestDTO() {}
	//-------------------------------------------------------------------------------------------------
	public SetTiltRequestDTO(final Integer tilt) {
		this.tilt = tilt;
	}

	//-------------------------------------------------------------------------------------------------
	// Each member needs to have a public getter and setter

	public Integer getMessage() {
		return tilt;
	}

	public void setMessage(Integer tilt) {
		this.tilt = tilt;
	}
}
