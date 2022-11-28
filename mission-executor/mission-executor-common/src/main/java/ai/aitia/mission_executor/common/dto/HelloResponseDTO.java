package ai.aitia.mission_executor.common.dto;

import java.io.Serializable;

public class HelloResponseDTO implements Serializable{

	// This is used by serializable to version the class
	private static final long serialVersionUID = 222L;

	//=================================================================================================
	// members

	private String hello;

	//=================================================================================================
	// methods
	// This empty constructor is needed
	public HelloResponseDTO() {}
	
	//-------------------------------------------------------------------------------------------------
	public HelloResponseDTO(final String hello) {
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
