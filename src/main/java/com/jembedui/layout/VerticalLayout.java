package com.jembedui.layout;

import com.jembedui.core.UIBaseElement;
import com.jembedui.core.UIContainer;

/**
 * Vertical layout manager.
 */
public class VerticalLayout implements LayoutManager {
    
    private float spacing = 5;
    private Alignment alignment = Alignment.START;
    
    public enum Alignment {
        START, CENTER, END
    }
    
    public VerticalLayout() {
    }
    
    public VerticalLayout(float spacing) {
        this.spacing = spacing;
    }
    
    public VerticalLayout(float spacing, Alignment alignment) {
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
        
        float contentHeight = 0;
        float maxWidth = 0;
        
        // Calculate total height and max width
        for (UIBaseElement child : container.getChildren()) {
            if (!child.isVisible()) continue;
            contentHeight += child.getHeight();
            maxWidth = Math.max(maxWidth, child.getWidth());
        }
        contentHeight += (container.getChildCount() - 1) * spacing;
        
        float startY = switch (alignment) {
            case START -> container.getStyle().getPaddingTop();
            case CENTER -> (container.getHeight() - contentHeight) / 2;
            case END -> container.getHeight() - contentHeight - container.getStyle().getPaddingBottom();
        };
        
        float currentY = startY;
        
        for (UIBaseElement child : container.getChildren()) {
            if (!child.isVisible()) continue;
            
            float x = container.getStyle().getPaddingLeft() + (maxWidth - child.getWidth()) / 2;
            child.setX(x);
            child.setY(currentY);
            currentY += child.getHeight() + spacing;
        }
    }
}
