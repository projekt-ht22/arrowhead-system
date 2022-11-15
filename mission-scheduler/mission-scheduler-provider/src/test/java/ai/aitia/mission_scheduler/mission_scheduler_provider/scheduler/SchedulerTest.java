package ai.aitia.mission_scheduler.mission_scheduler_provider.scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ai.aitia.mission_scheduler.common.Mission;
import ai.aitia.mission_scheduler.common.MissionTask;

public class SchedulerTest {

    Scheduler scheduler;

    @BeforeEach
    void newScheduler() {
        scheduler = new Scheduler();
    }

    @Test
    void testAddingMissionsSamePriority() {
        scheduler.addMission(new Mission(new ArrayList<MissionTask>(), "first", 0));
        scheduler.addMission(new Mission(new ArrayList<MissionTask>(), "second", 0));
        scheduler.addMission(new Mission(new ArrayList<MissionTask>(), "third", 0));

        assertEquals(scheduler.getNextAndRemoveMission().getName(), "first");
        assertEquals(scheduler.getNextAndRemoveMission().getName(), "second");
        assertEquals(scheduler.getNextAndRemoveMission().getName(), "third");
    }

    @Test
    void testAddingMissionDifferentPriority() {
        scheduler.addMission(new Mission(new ArrayList<MissionTask>(), "low", 1));
        scheduler.addMission(new Mission(new ArrayList<MissionTask>(), "high", 20));
        scheduler.addMission(new Mission(new ArrayList<MissionTask>(), "middle", 3));

        assertEquals(scheduler.getNextAndRemoveMission().getName(), "high");
        assertEquals(scheduler.getNextAndRemoveMission().getName(), "middle");
        assertEquals(scheduler.getNextAndRemoveMission().getName(), "low");
    }
    
}
