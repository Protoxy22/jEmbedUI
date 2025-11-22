package com.jembedui.core;

import com.jembedui.events.*;
import com.jembedui.render.NVGRenderer;
import com.jembedui.style.Style;

import java.util.*;

/**
 * Base class for all UI elements.
 */
public abstract class UIBaseElement {
    
    protected float x = 0;
    protected float y = 0;
    protected float width = 100;
    protected float height = 100;
    protected boolean visible = true;
    protected boolean enabled = true;
    protected Style style = new Style();
    protected UIContainer parent;
    protected boolean dirty = true;
    
    // Event listeners
    private final Map<Class<? extends UIEvent>, List<com.jembedui.events.EventListener>> eventListeners = new HashMap<>();
    
    public UIBaseElement() {
    }
    
    // Position and size
    public float getX() { return x; }
    public void setX(float x) { this.x = x; markDirty(); }
    
    public float getY() { return y; }
    public void setY(float y) { this.y = y; markDirty(); }
    
    public float getWidth() { return width; }
    public void setWidth(float width) { this.width = width; markDirty(); }
    
    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; markDirty(); }
    
    public void setBounds(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        markDirty();
    }
    
    // Absolute position calculation
    public float getAbsoluteX() {
        return parent != null ? parent.getAbsoluteX() + x : x;
    }
    
    public float getAbsoluteY() {
        return parent != null ? parent.getAbsoluteY() + y : y;
    }
    
    // Visibility
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { 
        this.visible = visible; 
        markDirty();
    }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { 
        this.enabled = enabled; 
        markDirty();
    }
    
    // Style
    public Style getStyle() { return style; }
    public void setStyle(Style style) { 
        this.style = style; 
        markDirty();
    }
    
    // Parent
    public UIContainer getParent() { return parent; }
    protected void setParent(UIContainer parent) { 
        this.parent = parent; 
    }
    
    // Dirty flag for optimized rendering
    public boolean isDirty() { return dirty; }
    public void markDirty() { 
        this.dirty = true;
        if (parent != null) {
            parent.markDirty();
        }
    }
    public void markClean() { this.dirty = false; }
    
    // Hit testing
    public boolean containsPoint(float px, float py) {
        float ax = getAbsoluteX();
        float ay = getAbsoluteY();
        return px >= ax && px <= ax + width && py >= ay && py <= ay + height;
    }
    
    // Event handling
    @SuppressWarnings("unchecked")
    public <T extends UIEvent> void addEventListener(Class<T> eventClass, com.jembedui.events.EventListener<T> listener) {
        eventListeners.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(listener);
    }
    
    @SuppressWarnings("unchecked")
    public <T extends UIEvent> void removeEventListener(Class<T> eventClass, com.jembedui.events.EventListener<T> listener) {
        List<com.jembedui.events.EventListener> listeners = eventListeners.get(eventClass);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }
    
    @SuppressWarnings("unchecked")
    public void dispatchEvent(UIEvent event) {
        List<com.jembedui.events.EventListener> listeners = eventListeners.get(event.getClass());
        if (listeners != null) {
            for (com.jembedui.events.EventListener listener : new ArrayList<>(listeners)) {
                listener.onEvent(event);
                if (event.isPropagationStopped()) {
                    break;
                }
            }
        }
    }
    
    // Layout
    public void layout() {
        // Override in subclasses that need layout
    }
    
    // Rendering
    public void render(NVGRenderer renderer) {
        if (!visible) return;
        
        // Render background
        if (style.getBackgroundColor().a() > 0) {
            renderer.drawRect(getAbsoluteX(), getAbsoluteY(), width, height, 
                            style.getBackgroundColor(), style.getBorderRadius());
        }
        
        // Render border
        if (style.getBorderWidth() > 0 && style.getBorderColor().a() > 0) {
            renderer.drawRectOutline(getAbsoluteX(), getAbsoluteY(), width, height, 
                                   style.getBorderWidth(), style.getBorderColor(), 
                                   style.getBorderRadius());
        }
        
        markClean();
    }
    
    // Update (for animations, etc.)
    public void update(float deltaTime) {
        // Override in subclasses that need updates
    }
}
