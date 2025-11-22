package com.jembedui.components.buttons;

import com.jembedui.render.NVGRenderer;
import com.jembedui.style.Color;

/**
 * Checkbox component.
 */
public class UICheckbox extends UIButton {
    
    private boolean checked = false;
    
    public UICheckbox() {
        super("");
        setWidth(20);
        setHeight(20);
    }
    
    public UICheckbox(String label) {
        super(label);
        setWidth(20);
        setHeight(20);
    }
    
    public boolean isChecked() {
        return checked;
    }
    
    public void setChecked(boolean checked) {
        this.checked = checked;
        markDirty();
    }
    
    @Override
    public void render(NVGRenderer renderer) {
        if (!visible) return;
        
        // Draw checkbox background
        Color bgColor = isEnabled() ? Color.WHITE : new Color(0.7f, 0.7f, 0.7f);
        renderer.drawRect(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight(), bgColor, 2);
        
        // Draw border
        Color borderColor = isEnabled() ? new Color(0.5f, 0.5f, 0.5f) : new Color(0.4f, 0.4f, 0.4f);
        renderer.drawRectOutline(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight(), 1, borderColor, 2);
        
        // Draw check mark if checked
        if (checked) {
            Color checkColor = isEnabled() ? new Color(0.2f, 0.6f, 0.8f) : new Color(0.4f, 0.4f, 0.4f);
            float margin = 4;
            renderer.drawLine(getAbsoluteX() + margin, getAbsoluteY() + getHeight() / 2,
                            getAbsoluteX() + getWidth() / 2, getAbsoluteY() + getHeight() - margin,
                            2, checkColor);
            renderer.drawLine(getAbsoluteX() + getWidth() / 2, getAbsoluteY() + getHeight() - margin,
                            getAbsoluteX() + getWidth() - margin, getAbsoluteY() + margin,
                            2, checkColor);
        }
        
        // Draw label if present
        if (!getLabel().isEmpty()) {
            float fontSize = getStyle().getFontSize();
            Color textColor = isEnabled() ? getStyle().getForegroundColor() : new Color(0.5f, 0.5f, 0.5f);
            renderer.drawText(getAbsoluteX() + getWidth() + 5, 
                            getAbsoluteY() + getHeight() / 2,
                            getLabel(), "default", fontSize, textColor,
                            org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT | org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE);
        }
        
        markClean();
    }
}
