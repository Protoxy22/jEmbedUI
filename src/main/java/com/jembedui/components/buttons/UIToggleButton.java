package com.jembedui.components.buttons;

import com.jembedui.render.NVGRenderer;
import com.jembedui.style.Color;

/**
 * Toggle button with ON/OFF state.
 */
public class UIToggleButton extends UIButton {
    
    private boolean toggled = false;
    private Color toggledColor = new Color(0.2f, 0.6f, 0.8f);
    private Color toggledHoverColor = new Color(0.3f, 0.7f, 0.9f);
    
    public UIToggleButton() {
        super();
    }
    
    public UIToggleButton(String label) {
        super(label);
    }
    
    public UIToggleButton(String label, boolean initialState) {
        super(label);
        this.toggled = initialState;
    }
    
    public boolean isToggled() {
        return toggled;
    }
    
    public void setToggled(boolean toggled) {
        this.toggled = toggled;
        markDirty();
    }
    
    public void setToggledColor(Color color) {
        this.toggledColor = color;
        markDirty();
    }
    
    @Override
    public void render(NVGRenderer renderer) {
        if (!visible) return;
        
        // Determine button color based on state
        Color bgColor;
        if (!isEnabled()) {
            bgColor = new Color(0.2f, 0.2f, 0.2f);
        } else if (toggled) {
            bgColor = toggledColor;
        } else {
            return; // Use parent rendering for non-toggled state
        }
        
        // Override to show toggled state
        if (toggled) {
            renderer.drawRect(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight(), bgColor, 4);
            renderer.drawRectOutline(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight(), 
                                    2, new Color(0.3f, 0.8f, 1.0f), 4);
            
            float fontSize = getStyle().getFontSize();
            Color textColor = Color.WHITE;
            
            float[] textSize = renderer.measureText(getLabel(), fontSize);
            float textX = getAbsoluteX() + (getWidth() - textSize[0]) / 2;
            float textY = getAbsoluteY() + (getHeight() + textSize[1]) / 2;
            
            renderer.drawText(textX, textY, getLabel(), fontSize, textColor);
            markClean();
        } else {
            super.render(renderer);
        }
    }
}
