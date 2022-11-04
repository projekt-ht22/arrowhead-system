package ai.aitia.hello.common.dto;

import java.io.Serializable;

public class HelloResponseDTO implements Serializable{

	//=================================================================================================
	// members
	private static final long serialVersionUID = 222L;

	private String hello;

	//=================================================================================================
	// methods
	public HelloResponseDTO() {}
	
	//-------------------------------------------------------------------------------------------------
	public HelloResponseDTO(final String hello) {
		this.hello = hello;
	}

	//-------------------------------------------------------------------------------------------------

	public String getHello() {
		return this.hello;
	}
	//-------------------------------------------------------------------------------------------------
	public void setHello(final String hello) {
		this.hello = hello;
	}
}
