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
    private boolean hovered = false;
    private Runnable onChangeHandler;
    
    // Generic increment/decrement configuration
    private float stepSize = 0.05f;  // 5% increment per step (works for scroll, MIDI, keys, etc.)
    private boolean incrementEnabled = true;

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
            case MOUSE_ENTER -> {
                hovered = true;
                markDirty();
            }
            case MOUSE_LEAVE -> {
                hovered = false;
                markDirty();
            }
            case MOUSE_WHEEL -> {
                // Only respond to scroll when hovered (important for multiple sliders)
                if (hovered && incrementEnabled) {
                    // Positive wheelDeltaY = scroll up = increase
                    // Negative wheelDeltaY = scroll down = decrease
                    if (event.getWheelDeltaY() > 0) {
                        increment();
                    } else if (event.getWheelDeltaY() < 0) {
                        decrement();
                    }
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
    
    /**
     * Generic increment method - increases value by configured step size.
     * Can be called from mouse wheel, MIDI CC, keyboard, or any other input source.
     */
    public void increment() {
        if (!enabled || !incrementEnabled) return;

        float newValue = Math.min(1.0f, value + stepSize);
        if (newValue != value) {
            value = newValue;
            markDirty();
            if (onChangeHandler != null) {
                onChangeHandler.run();
            }
        }
    }

    /**
     * Generic decrement method - decreases value by configured step size.
     * Can be called from mouse wheel, MIDI CC, keyboard, or any other input source.
     */
    public void decrement() {
        if (!enabled || !incrementEnabled) return;

        float newValue = Math.max(0.0f, value - stepSize);
        if (newValue != value) {
            value = newValue;
            markDirty();
            if (onChangeHandler != null) {
                onChangeHandler.run();
            }
        }
    }

    /**
     * Generic relative adjustment - adds delta to current value.
     * Useful for MIDI relative encoders or other continuous controllers.
     * @param delta The amount to add (can be positive or negative)
     */
    public void adjustBy(float delta) {
        if (!enabled || !incrementEnabled) return;

        float newValue = Math.max(0.0f, Math.min(1.0f, value + delta));
        if (newValue != value) {
            value = newValue;
            markDirty();
            if (onChangeHandler != null) {
                onChangeHandler.run();
            }
        }
    }

    /**
     * Set the step size for increment/decrement operations.
     * This applies to mouse wheel, MIDI, keyboard, and any other incremental input.
     * @param step Step size (0.0 to 1.0, typically 0.01 to 0.1)
     */
    public void setStepSize(float step) {
        this.stepSize = Math.max(0.001f, Math.min(1.0f, step));
    }

    public float getStepSize() {
        return stepSize;
    }

    /**
     * Enable or disable incremental adjustments (scroll, MIDI, keyboard, etc.)
     */
    public void setIncrementEnabled(boolean enabled) {
        this.incrementEnabled = enabled;
    }

    public boolean isIncrementEnabled() {
        return incrementEnabled;
    }

    public boolean isHovered() {
        return hovered;
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
        
        // Draw filled portion with hover feedback
        Color fillColor = hovered ? new Color(0.3f, 0.7f, 0.9f) : new Color(0.2f, 0.6f, 0.8f);
        if (orientation == Orientation.HORIZONTAL) {
            renderer.drawRect(ax, ay + height / 2 - 2, width * value, 4, fillColor, 2);
        } else {
            float filledHeight = height * value;
            renderer.drawRect(ax + width / 2 - 2, ay + height - filledHeight, 4, filledHeight, fillColor, 2);
        }
        
        // Draw thumb with hover and dragging feedback
        float thumbSize = (hovered || dragging) ? 14 : 12;
        Color thumbColor = enabled ? (hovered ? new Color(0.4f, 0.8f, 1.0f) : new Color(0.3f, 0.7f, 0.9f))
                                   : new Color(0.4f, 0.4f, 0.4f);
        if (orientation == Orientation.HORIZONTAL) {
            float thumbX = ax + width * value;
            renderer.drawCircle(thumbX, ay + height / 2, thumbSize / 2, thumbColor);
            renderer.drawCircleOutline(thumbX, ay + height / 2, thumbSize / 2, (hovered || dragging) ? 2 : 1, Color.WHITE);
        } else {
            float thumbY = ay + height - height * value;
            renderer.drawCircle(ax + width / 2, thumbY, thumbSize / 2, thumbColor);
            renderer.drawCircleOutline(ax + width / 2, thumbY, thumbSize / 2, (hovered || dragging) ? 2 : 1, Color.WHITE);
        }
        
        markClean();
    }
}
