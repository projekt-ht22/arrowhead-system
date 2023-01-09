package ai.aitia.snowblower.common.dto;

import java.io.Serializable;


public class GetAccessoryStopStartResponseDTO implements Serializable{

    // This is used by serializable to version the class
    private static final long serialVersionUID = 1491L;

	//=================================================================================================
	// members

	private boolean activated ;

	//=================================================================================================
	// methods
	
	// This empty constructor is needed
	public GetAccessoryStopStartResponseDTO() {}

    //-------------------------------------------------------------------------------------------------
    public GetAccessoryStopStartResponseDTO(final Boolean activated) {
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
