package com.jembedui.events;

import com.jembedui.core.UIBaseElement;

/**
 * Keyboard event for handling keyboard input.
 */
public class KeyboardEvent extends UIEvent {
    
    public enum KeyEventType {
        KEY_DOWN, KEY_UP, KEY_PRESS
    }
    
    private final KeyEventType eventType;
    private final int key;
    private final int scancode;
    private final int mods;
    private final char character;
    
    public KeyboardEvent(UIBaseElement target, KeyEventType eventType, int key, 
                        int scancode, int mods, char character) {
        super(target);
        this.eventType = eventType;
        this.key = key;
        this.scancode = scancode;
        this.mods = mods;
        this.character = character;
    }
    
    public KeyboardEvent(UIBaseElement target, KeyEventType eventType, int key, int scancode, int mods) {
        this(target, eventType, key, scancode, mods, '\0');
    }
    
    public KeyEventType getEventType() {
        return eventType;
    }
    
    public int getKey() {
        return key;
    }
    
    public int getScancode() {
        return scancode;
    }
    
    public int getMods() {
        return mods;
    }
    
    public char getCharacter() {
        return character;
    }
    
    public boolean isShiftDown() {
        return (mods & 0x01) != 0;
    }
    
    public boolean isControlDown() {
        return (mods & 0x02) != 0;
    }
    
    public boolean isAltDown() {
        return (mods & 0x04) != 0;
    }
}
