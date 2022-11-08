package ai.aitia.mission_scheduler.mission_scheduler_provider.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import ai.aitia.mission_scheduler.common.Mission;

@Component
public class Scheduler {
    // holds the missions in the queue
    private List<Mission> queue;


    public Scheduler() {
        this.queue = new ArrayList<>();
    }

    public void addMission(Mission mission) {
        int priority = mission.getPriority();

        if (this.queue.size() == 0) {
            // queue is empty just add
            this.queue.add(mission);
            return;
        }

        // insert before the first mission with a higher priority
        for(int i = 0; i < this.queue.size(); i++) {
            int currentPriority = this.queue.get(i).getPriority();
            if (currentPriority > priority) {
                this.queue.add(currentPriority, mission);
                return;
            }
        }

        // For loop ended so no mission with higher priority add to end
        this.queue.add(mission);
    }

    public Mission getNextAndRemoveMission() {
        if (queue.size() == 0) {
            return null;
        }

        return queue.get(queue.size() - 1);
    }
}
