package com.jembedui.components.buttons;

import com.jembedui.core.UIBaseElement;
import com.jembedui.render.NVGRenderer;
import com.jembedui.style.Color;

/**
 * Progress bar component.
 */
public class UIProgressBar extends UIBaseElement {
    
    private float progress = 0.5f;  // 0.0 to 1.0
    private Color barColor = new Color(0.2f, 0.6f, 0.8f);
    private Color backgroundColor = new Color(0.2f, 0.2f, 0.2f);
    
    public UIProgressBar() {
        super();
        width = 200;
        height = 20;
    }
    
    public float getProgress() {
        return progress;
    }
    
    public void setProgress(float progress) {
        this.progress = Math.max(0, Math.min(1, progress));
        markDirty();
    }
    
    public void setBarColor(Color color) {
        this.barColor = color;
        markDirty();
    }
    
    @Override
    public void render(NVGRenderer renderer) {
        if (!visible) return;
        
        float ax = getAbsoluteX();
        float ay = getAbsoluteY();
        
        // Draw background
        renderer.drawRect(ax, ay, width, height, backgroundColor, 4);
        
        // Draw progress bar
        if (progress > 0) {
            renderer.drawRect(ax, ay, width * progress, height, barColor, 4);
        }
        
        // Draw border
        renderer.drawRectOutline(ax, ay, width, height, 1, new Color(0.5f, 0.5f, 0.5f), 4);
        
        markClean();
    }
}
