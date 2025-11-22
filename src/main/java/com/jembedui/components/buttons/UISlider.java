package com.jembedui.components.buttons;

import com.jembedui.core.UIBaseElement;
import com.jembedui.events.MouseEvent;
import com.jembedui.render.NVGRenderer;
import com.jembedui.style.Color;

/**
 * Slider component for value selection.
 */
public class UISlider extends UIBaseElement {
    
    public enum Orientation {
        HORIZONTAL, VERTICAL
    }
    
    private Orientation orientation = Orientation.HORIZONTAL;
    private float value = 0.5f;  // 0.0 to 1.0
    private float minValue = 0.0f;
    private float maxValue = 1.0f;
    private boolean dragging = false;
    private Runnable onChangeHandler;
    
    public UISlider() {
        super();
        setupEventHandlers();
        width = 200;
        height = 20;
    }
    
    public UISlider(Orientation orientation) {
        this();
        this.orientation = orientation;
        if (orientation == Orientation.VERTICAL) {
            width = 20;
            height = 200;
        }
    }
    
    private void setupEventHandlers() {
        addEventListener(MouseEvent.class, this::handleMouseEvent);
    }
    
    private void handleMouseEvent(MouseEvent event) {
        if (!enabled) return;
        
        switch (event.getEventType()) {
            case MOUSE_DOWN -> {
                dragging = true;
                updateValue(event.getX(), event.getY());
            }
            case MOUSE_UP -> {
                dragging = false;
            }
            case MOUSE_MOVE -> {
                if (dragging) {
                    updateValue(event.getX(), event.getY());
                }
            }
        }
    }
    
    private void updateValue(float mouseX, float mouseY) {
        float newValue;
        if (orientation == Orientation.HORIZONTAL) {
            float relX = mouseX - getAbsoluteX();
            newValue = Math.max(0, Math.min(1, relX / width));
        } else {
            float relY = mouseY - getAbsoluteY();
            newValue = 1.0f - Math.max(0, Math.min(1, relY / height));
        }
        
        if (newValue != value) {
            value = newValue;
            markDirty();
            if (onChangeHandler != null) {
                onChangeHandler.run();
            }
        }
    }
    
    public float getValue() {
        return value;
    }
    
    public void setValue(float value) {
        this.value = Math.max(0, Math.min(1, value));
        markDirty();
    }
    
    public float getActualValue() {
        return minValue + (maxValue - minValue) * value;
    }
    
    public void setActualValue(float actualValue) {
        setValue((actualValue - minValue) / (maxValue - minValue));
    }
    
    public void setRange(float min, float max) {
        this.minValue = min;
        this.maxValue = max;
    }
    
    public void setOnChange(Runnable handler) {
        this.onChangeHandler = handler;
    }
    
    @Override
    public void render(NVGRenderer renderer) {
        if (!visible) return;
        
        float ax = getAbsoluteX();
        float ay = getAbsoluteY();
        
        // Draw track
        Color trackColor = new Color(0.2f, 0.2f, 0.2f);
        if (orientation == Orientation.HORIZONTAL) {
            renderer.drawRect(ax, ay + height / 2 - 2, width, 4, trackColor, 2);
        } else {
            renderer.drawRect(ax + width / 2 - 2, ay, 4, height, trackColor, 2);
        }
        
        // Draw filled portion
        Color fillColor = new Color(0.2f, 0.6f, 0.8f);
        if (orientation == Orientation.HORIZONTAL) {
            renderer.drawRect(ax, ay + height / 2 - 2, width * value, 4, fillColor, 2);
        } else {
            float filledHeight = height * value;
            renderer.drawRect(ax + width / 2 - 2, ay + height - filledHeight, 4, filledHeight, fillColor, 2);
        }
        
        // Draw thumb
        float thumbSize = 12;
        Color thumbColor = enabled ? new Color(0.3f, 0.7f, 0.9f) : new Color(0.4f, 0.4f, 0.4f);
        if (orientation == Orientation.HORIZONTAL) {
            float thumbX = ax + width * value;
            renderer.drawCircle(thumbX, ay + height / 2, thumbSize / 2, thumbColor);
            renderer.drawCircleOutline(thumbX, ay + height / 2, thumbSize / 2, 1, Color.WHITE);
        } else {
            float thumbY = ay + height - height * value;
            renderer.drawCircle(ax + width / 2, thumbY, thumbSize / 2, thumbColor);
            renderer.drawCircleOutline(ax + width / 2, thumbY, thumbSize / 2, 1, Color.WHITE);
        }
        
        markClean();
    }
}
