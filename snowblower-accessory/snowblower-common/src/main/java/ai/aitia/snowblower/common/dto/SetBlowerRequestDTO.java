package ai.aitia.snowblower.common.dto;

import java.io.Serializable;

import ai.aitia.snowblower.common.dto.GetAccessoryCommandResponseDTO.commandTypes;

public class SetBlowerRequestDTO implements Serializable {

	// This is used by serializable to version the class
	private static final long serialVersionUID = 278L;

	//=================================================================================================
	// members

	private int pwm_pin12;
	private int pwm_pin13;
	private int rpm;
	private commandTypes commandType;

	//=================================================================================================
	// methods
	
	// This empty constructor is needed
	public SetBlowerRequestDTO() {}
	//-------------------------------------------------------------------------------------------------
	public SetBlowerRequestDTO(final int pwm_pin12,final int pwm_pin13, final int rpm, final commandTypes commandType) {
		this.pwm_pin12 = pwm_pin12;
		this.pwm_pin13 = pwm_pin13;
		this.rpm = rpm;
		this.commandType = commandType;
	}

	//-------------------------------------------------------------------------------------------------
	// Each member needs to have a public getter and setter


	public int getPwm_pin12() {
		return pwm_pin12;
	}
	public void setPwm_pin12(int pwm_pin12) {
		this.pwm_pin12 = pwm_pin12;
	}
	public int getPwm_pin13() {
		return pwm_pin13;
	}
	public void setPwm_pin13(int pwm_pin13) {
		this.pwm_pin13 = pwm_pin13;
	}
	public int getrpm() {
		return rpm;
	}
	public void setrpm(int rpm) {
		this.rpm = rpm;
	}
	public commandTypes getCommandType() {
		return commandType;
	}
	public void setCommandType(commandTypes commandType) {
		this.commandType = commandType;
	}


}
