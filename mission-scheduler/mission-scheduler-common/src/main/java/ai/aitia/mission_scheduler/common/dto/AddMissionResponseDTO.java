package ai.aitia.mission_scheduler.common.dto;

import java.io.Serializable;

public class AddMissionResponseDTO implements Serializable{

	// This is used by serializable to version the class
	private static final long serialVersionUID = 244;

	//=================================================================================================
	// members

	private String hello;

	//=================================================================================================
	// methods
	// This empty constructor is needed
	public AddMissionResponseDTO() {}
	
	//-------------------------------------------------------------------------------------------------
	public AddMissionResponseDTO(final String hello) {
		this.hello = hello;
	}

	//-------------------------------------------------------------------------------------------------
	// Each member needs to have a public getter and setter

	public String getHello() {
		return this.hello;
	}
	//-------------------------------------------------------------------------------------------------
	public void setHello(final String hello) {
		this.hello = hello;
	}
}
