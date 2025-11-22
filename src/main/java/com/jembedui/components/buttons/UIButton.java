package com.jembedui.components.buttons;

import com.jembedui.core.UIBaseElement;
import com.jembedui.events.MouseEvent;
import com.jembedui.render.NVGRenderer;
import com.jembedui.style.Color;

import static org.lwjgl.nanovg.NanoVG.*;

/**
 * Clickable button component.
 */
public class UIButton extends UIBaseElement {
    
    private String label = "Button";
    private boolean hovered = false;
    private boolean pressed = false;
    private Runnable onClickHandler;
    
    private Color normalColor = new Color(0.3f, 0.3f, 0.35f);
    private Color hoverColor = new Color(0.4f, 0.4f, 0.45f);
    private Color pressedColor = new Color(0.2f, 0.2f, 0.25f);
    private Color disabledColor = new Color(0.2f, 0.2f, 0.2f);
    
    public UIButton() {
        super();
        setupEventHandlers();
        width = 100;
        height = 30;
    }
    
    public UIButton(String label) {
        this();
        this.label = label;
    }
    
    public UIButton(String label, float x, float y, float width, float height) {
        this(label);
        setBounds(x, y, width, height);
    }
    
    private void setupEventHandlers() {
        addEventListener(MouseEvent.class, this::handleMouseEvent);
    }
    
    private void handleMouseEvent(MouseEvent event) {
        if (!enabled) return;
        
        switch (event.getEventType()) {
            case MOUSE_ENTER -> {
                hovered = true;
                markDirty();
            }
            case MOUSE_LEAVE -> {
                hovered = false;
                pressed = false;
                markDirty();
            }
            case MOUSE_DOWN -> {
                pressed = true;
                markDirty();
            }
            case MOUSE_UP -> {
                pressed = false;
                markDirty();
            }
            case MOUSE_CLICK -> {
                if (onClickHandler != null) {
                    onClickHandler.run();
                }
            }
        }
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
        markDirty();
    }
    
    public void setOnClick(Runnable handler) {
        this.onClickHandler = handler;
    }
    
    public void setNormalColor(Color color) {
        this.normalColor = color;
        markDirty();
    }
    
    public void setHoverColor(Color color) {
        this.hoverColor = color;
        markDirty();
    }
    
    public void setPressedColor(Color color) {
        this.pressedColor = color;
        markDirty();
    }
    
    @Override
    public void render(NVGRenderer renderer) {
        if (!visible) return;
        
        // Determine button color based on state
        Color bgColor;
        if (!enabled) {
            bgColor = disabledColor;
        } else if (pressed) {
            bgColor = pressedColor;
        } else if (hovered) {
            bgColor = hoverColor;
        } else {
            bgColor = normalColor;
        }
        
        // Draw button background
        renderer.drawRect(getAbsoluteX(), getAbsoluteY(), width, height, bgColor, 4);
        
        // Draw border
        Color borderColor = enabled ? new Color(0.5f, 0.5f, 0.5f) : new Color(0.3f, 0.3f, 0.3f);
        renderer.drawRectOutline(getAbsoluteX(), getAbsoluteY(), width, height, 1, borderColor, 4);
        
        // Draw label
        float fontSize = style.getFontSize();
        Color textColor = enabled ? style.getForegroundColor() : new Color(0.4f, 0.4f, 0.4f);
        
        float[] textSize = renderer.measureText(label, fontSize);
        float textX = getAbsoluteX() + (width - textSize[0]) / 2;
        float textY = getAbsoluteY() + (height + textSize[1]) / 2;
        
        renderer.drawText(textX, textY, label, fontSize, textColor);
        
        markClean();
    }
}
