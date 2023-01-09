package ai.aitia.snowblower.common.dto;

import java.io.Serializable;

import ai.aitia.snowblower.common.dto.GetAccessoryCommandResponseDTO.commandTypes;

public class SetAccessoryStopStartResponseDTO implements Serializable {

	// This is used by serializable to version the class
	private static final long serialVersionUID = 461L;

	//=================================================================================================
	// members
	
	private boolean activated = true;

	//=================================================================================================
	// methods
	
	// This empty constructor is needed
	public SetAccessoryStopStartResponseDTO() {}
	//-------------------------------------------------------------------------------------------------
	public SetAccessoryStopStartResponseDTO(final Boolean activated) {
		this.activated = activated;
	}

	//-------------------------------------------------------------------------------------------------
	// Each member needs to have a public getter and setter

	public boolean isActivated() {
        return activated;
    }
    
    public void setActivated(boolean activated) {
        this.activated = activated;
    }

}
