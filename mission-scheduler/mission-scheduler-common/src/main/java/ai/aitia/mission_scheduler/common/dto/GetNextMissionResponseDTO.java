package ai.aitia.mission_scheduler.common.dto;

import java.io.Serializable;

import ai.aitia.mission_scheduler.common.Mission;

public class GetNextMissionResponseDTO implements Serializable{

    private static final long serialVersionUID = 123122L;

    private Mission mission;

    public GetNextMissionResponseDTO() {}
    public GetNextMissionResponseDTO(final Mission mission) {
        this.mission = mission;
    }

    public Mission getMission() {
        return mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }
}
