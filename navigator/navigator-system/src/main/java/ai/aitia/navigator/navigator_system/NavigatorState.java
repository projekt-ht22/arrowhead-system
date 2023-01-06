package ai.aitia.navigator.navigator_system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.navigator.navigator_system.navigation_services.FollowPathService;
import eu.arrowhead.common.SSLProperties;

@Component
public class NavigatorState {

    private Boolean cancel;

    private Boolean stop;

    @Autowired
    SSLProperties sslProperties;

    @Autowired
    ArrowheadService arrowheadService;

    public NavigatorState() {
        stop = false;
        cancel = false;
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

    public void cancel() {
        synchronized(cancel) {
            cancel = true;
        }
    }

    public void notCancel() {
        synchronized(cancel) {
            cancel = false;
        }
    }

    public boolean shouldCancel() {
        boolean ret;
        synchronized(cancel) {
            ret = cancel;
        }
        return ret;
    }
}
