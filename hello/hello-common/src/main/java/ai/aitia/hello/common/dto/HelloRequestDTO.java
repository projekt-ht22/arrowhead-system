package ai.aitia.hello.common.dto;

import java.io.Serializable;

public class HelloRequestDTO implements Serializable{

	//=================================================================================================
	// members

	private static final long serialVersionUID = 122L;

	private int times;

	//=================================================================================================
	// methods
	
	public HelloRequestDTO() {}
	//-------------------------------------------------------------------------------------------------
	public HelloRequestDTO(final int times) {
		this.times = times;
	}

	//-------------------------------------------------------------------------------------------------

	public int getTimes() {
		return this.times;
	}
	//-------------------------------------------------------------------------------------------------
	public void setTimes(final int times) {
		this.times = times;
	}
}
