package com.jembedui.components.display;

import com.jembedui.core.UIBaseElement;
import com.jembedui.render.NVGRenderer;
import com.jembedui.style.Color;

import static org.lwjgl.nanovg.NanoVG.*;

/**
 * Text display component.
 */
public class UIText extends UIBaseElement {
    
    private String text = "";
    private int align = NVG_ALIGN_LEFT | NVG_ALIGN_TOP;
    
    public UIText() {
        super();
    }
    
    public UIText(String text) {
        super();
        this.text = text;
    }
    
    public UIText(String text, float x, float y) {
        super();
        this.text = text;
        setX(x);
        setY(y);
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
        markDirty();
    }
    
    public void setAlign(int align) {
        this.align = align;
        markDirty();
    }
    
    @Override
    public void render(NVGRenderer renderer) {
        if (!visible) return;
        
        super.render(renderer);
        
        float fontSize = style.getFontSize();
        Color color = style.getForegroundColor();
        
        renderer.drawText(getAbsoluteX() + style.getPaddingLeft(), 
                         getAbsoluteY() + style.getPaddingTop() + fontSize, 
                         text, "default", fontSize, color, align);

        markClean();
    }
}
