package com.jembedui.events;

import com.jembedui.core.UIBaseElement;

/**
 * Base class for all UI events in the system.
 * Implements event propagation with capture, target, and bubble phases.
 */
public abstract class UIEvent {
    
    public enum EventPhase {
        CAPTURE,  // Event travels down from root to target
        TARGET,   // Event is at the target element
        BUBBLE    // Event travels up from target to root
    }
    
    private UIBaseElement target;
    private UIBaseElement currentTarget;
    private EventPhase phase = EventPhase.TARGET;
    private boolean propagationStopped = false;
    private boolean defaultPrevented = false;
    private long timestamp;
    
    public UIEvent(UIBaseElement target) {
        this.target = target;
        this.currentTarget = target;
        this.timestamp = System.currentTimeMillis();
    }
    
    public UIBaseElement getTarget() {
        return target;
    }
    
    public UIBaseElement getCurrentTarget() {
        return currentTarget;
    }
    
    public void setCurrentTarget(UIBaseElement currentTarget) {
        this.currentTarget = currentTarget;
    }
    
    public EventPhase getPhase() {
        return phase;
    }
    
    public void setPhase(EventPhase phase) {
        this.phase = phase;
    }
    
    public void stopPropagation() {
        this.propagationStopped = true;
    }
    
    public boolean isPropagationStopped() {
        return propagationStopped;
    }
    
    public void preventDefault() {
        this.defaultPrevented = true;
    }
    
    public boolean isDefaultPrevented() {
        return defaultPrevented;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
}
