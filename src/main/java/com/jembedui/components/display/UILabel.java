package com.jembedui.components.display;

import static org.lwjgl.nanovg.NanoVG.*;

/**
 * Label with text and alignment support.
 */
public class UILabel extends UIText {
    
    public enum HAlign {
        LEFT(NVG_ALIGN_LEFT),
        CENTER(NVG_ALIGN_CENTER),
        RIGHT(NVG_ALIGN_RIGHT);
        
        final int nvgAlign;
        HAlign(int nvgAlign) { this.nvgAlign = nvgAlign; }
    }
    
    public enum VAlign {
        TOP(NVG_ALIGN_TOP),
        MIDDLE(NVG_ALIGN_MIDDLE),
        BOTTOM(NVG_ALIGN_BOTTOM);
        
        final int nvgAlign;
        VAlign(int nvgAlign) { this.nvgAlign = nvgAlign; }
    }
    
    public UILabel() {
        super();
    }
    
    public UILabel(String text) {
        super(text);
    }
    
    public UILabel(String text, HAlign hAlign, VAlign vAlign) {
        super(text);
        setAlignment(hAlign, vAlign);
    }
    
    public void setAlignment(HAlign hAlign, VAlign vAlign) {
        setAlign(hAlign.nvgAlign | vAlign.nvgAlign);
    }
}
