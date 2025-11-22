package com.jembedui.events;

import com.jembedui.core.UIBaseElement;

/**
 * Mouse event for handling mouse input.
 */
public class MouseEvent extends UIEvent {
    
    public enum MouseButton {
        LEFT, RIGHT, MIDDLE, NONE
    }
    
    public enum MouseEventType {
        MOUSE_DOWN, MOUSE_UP, MOUSE_MOVE, MOUSE_ENTER, MOUSE_LEAVE, MOUSE_CLICK, MOUSE_WHEEL
    }
    
    private final MouseEventType eventType;
    private final float x;
    private final float y;
    private final MouseButton button;
    private final float wheelDeltaX;
    private final float wheelDeltaY;
    
    public MouseEvent(UIBaseElement target, MouseEventType eventType, float x, float y, 
                     MouseButton button, float wheelDeltaX, float wheelDeltaY) {
        super(target);
        this.eventType = eventType;
        this.x = x;
        this.y = y;
        this.button = button;
        this.wheelDeltaX = wheelDeltaX;
        this.wheelDeltaY = wheelDeltaY;
    }
    
    public MouseEvent(UIBaseElement target, MouseEventType eventType, float x, float y, MouseButton button) {
        this(target, eventType, x, y, button, 0, 0);
    }
    
    public MouseEvent(UIBaseElement target, MouseEventType eventType, float x, float y) {
        this(target, eventType, x, y, MouseButton.NONE, 0, 0);
    }
    
    public MouseEventType getEventType() {
        return eventType;
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public MouseButton getButton() {
        return button;
    }
    
    public float getWheelDeltaX() {
        return wheelDeltaX;
    }
    
    public float getWheelDeltaY() {
        return wheelDeltaY;
    }
}
