package com.jembedui.layout;

import com.jembedui.core.UIBaseElement;
import com.jembedui.core.UIContainer;

/**
 * Horizontal layout manager.
 */
public class HorizontalLayout implements LayoutManager {
    
    private float spacing = 5;
    private Alignment alignment = Alignment.START;
    
    public enum Alignment {
        START, CENTER, END
    }
    
    public HorizontalLayout() {
    }
    
    public HorizontalLayout(float spacing) {
        this.spacing = spacing;
    }
    
    public HorizontalLayout(float spacing, Alignment alignment) {
        this.spacing = spacing;
        this.alignment = alignment;
    }
    
    public void setSpacing(float spacing) {
        this.spacing = spacing;
    }
    
    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }
    
    @Override
    public void layout(UIContainer container) {
        if (container.getChildCount() == 0) return;
        
        float contentWidth = 0;
        float maxHeight = 0;
        
        // Calculate total width and max height
        for (UIBaseElement child : container.getChildren()) {
            if (!child.isVisible()) continue;
            contentWidth += child.getWidth();
            maxHeight = Math.max(maxHeight, child.getHeight());
        }
        contentWidth += (container.getChildCount() - 1) * spacing;
        
        float startX = switch (alignment) {
            case START -> container.getStyle().getPaddingLeft();
            case CENTER -> (container.getWidth() - contentWidth) / 2;
            case END -> container.getWidth() - contentWidth - container.getStyle().getPaddingRight();
        };
        
        float currentX = startX;
        
        for (UIBaseElement child : container.getChildren()) {
            if (!child.isVisible()) continue;
            
            float y = container.getStyle().getPaddingTop() + (maxHeight - child.getHeight()) / 2;
            child.setX(currentX);
            child.setY(y);
            currentX += child.getWidth() + spacing;
        }
    }
}
