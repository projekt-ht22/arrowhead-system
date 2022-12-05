package ai.aitia.navigator.navigator_system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.navigator.navigator_system.navigation_services.FollowPathService;
import eu.arrowhead.common.SSLProperties;

@Component
public class NavigatorState {

    private Boolean stop;
    private FollowPathService followPathService;

    @Autowired
    SSLProperties sslProperties;

    @Autowired
    ArrowheadService arrowheadService;

    public NavigatorState() {
        stop = false;
        followPathService = new FollowPathService(sslProperties, arrowheadService);
    }
    
    public FollowPathService getFollowPathService() {
        return followPathService;
    }
   
    public void stop() {
        synchronized(stop) {
            stop = true;
        }
    }

    public void start() {
        synchronized(stop) {
            stop = false;
        }
    }

    public boolean shouldStop() {
        boolean ret;
        synchronized(stop) {
            ret = stop;
        }

        return ret;
    }
}
