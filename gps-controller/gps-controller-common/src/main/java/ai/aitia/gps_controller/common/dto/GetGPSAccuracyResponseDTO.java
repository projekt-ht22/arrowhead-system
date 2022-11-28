package ai.aitia.gps_controller.common.dto;

import java.io.Serializable;


public class GetGPSAccuracyResponseDTO implements Serializable{

    private static final long serialVersionUID = 14921L;


	private Navigation_status navigation_status;
	private byte satellites;
	private short north_velocity_accuracy;
	private short east_velocity_accuracy;
    private short heading_accuracy;

    public enum Navigation_status{
		NO_SATELLITES,
		INITIALISING,
		LOCKING,
		REAL_TIME_DATA,
		TRIGGER_PACKET,
		DO_NOT_USE,
	}

    public GetGPSAccuracyResponseDTO() {}
    public GetGPSAccuracyResponseDTO(final Navigation_status navigation_status, final byte satellites, final short north_velocity_accuracy, final short east_velocity_accuracy, final short heading_accuracy) {

        this.satellites = satellites;
        this.north_velocity_accuracy = north_velocity_accuracy;
        this.east_velocity_accuracy = east_velocity_accuracy;
        this.heading_accuracy = heading_accuracy;
        this.navigation_status = navigation_status;
    }

    public Navigation_status getNavigation_status() {
        return navigation_status;
    }
    public void setNavigation_status(Navigation_status navigation_status) {
        this.navigation_status = navigation_status;
    }
    public byte getSatellites() {
        return satellites;
    }

    public void setSatellites(byte satellites) {
        this.satellites = satellites;
    }
    
    public short getNorth_velocity_accuracy() {
        return north_velocity_accuracy;
    }
    
    public void setNorth_velocity_accuracy(short north_velocity_accuracy) {
        this.north_velocity_accuracy = north_velocity_accuracy;
    }
    
    public short getEast_velocity_accuracy() {
        return east_velocity_accuracy;
    }
    
    public void setEast_velocity_accuracy(short east_velocity_accuracy) {
        this.east_velocity_accuracy = east_velocity_accuracy;
    }
    
    public short getHeading_accuracy() {
        return heading_accuracy;
    }
    
    public void setHeading_accuracy(short heading_accuracy) {
        this.heading_accuracy = heading_accuracy;
    }

}
