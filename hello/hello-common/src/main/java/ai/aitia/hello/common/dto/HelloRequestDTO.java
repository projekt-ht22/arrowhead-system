package ai.aitia.hello.common.dto;

import java.io.Serializable;

public class HelloRequestDTO implements Serializable{

	// This is used by serializable to version the class
	private static final long serialVersionUID = 122L;

	//=================================================================================================
	// members

	private int times;

	//=================================================================================================
	// methods
	
	// This empty constructor is needed
	public HelloRequestDTO() {}
	//-------------------------------------------------------------------------------------------------
	public HelloRequestDTO(final int times) {
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
