package com.jembedui.layout;

import com.jembedui.core.UIBaseElement;
import com.jembedui.core.UIContainer;

/**
 * Grid layout manager.
 */
public class GridLayout implements LayoutManager {
    
    private int columns;
    private int rows;
    private float hSpacing = 5;
    private float vSpacing = 5;
    
    public GridLayout(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;
    }
    
    public GridLayout(int columns, int rows, float spacing) {
        this.columns = columns;
        this.rows = rows;
        this.hSpacing = spacing;
        this.vSpacing = spacing;
    }
    
    public GridLayout(int columns, int rows, float hSpacing, float vSpacing) {
        this.columns = columns;
        this.rows = rows;
        this.hSpacing = hSpacing;
        this.vSpacing = vSpacing;
    }
    
    public void setColumns(int columns) {
        this.columns = columns;
    }
    
    public void setRows(int rows) {
        this.rows = rows;
    }
    
    public void setSpacing(float spacing) {
        this.hSpacing = spacing;
        this.vSpacing = spacing;
    }
    
    @Override
    public void layout(UIContainer container) {
        if (container.getChildCount() == 0) return;
        
        float paddingLeft = container.getStyle().getPaddingLeft();
        float paddingTop = container.getStyle().getPaddingTop();
        float paddingRight = container.getStyle().getPaddingRight();
        float paddingBottom = container.getStyle().getPaddingBottom();
        
        float availableWidth = container.getWidth() - paddingLeft - paddingRight;
        float availableHeight = container.getHeight() - paddingTop - paddingBottom;
        
        float cellWidth = (availableWidth - (columns - 1) * hSpacing) / columns;
        float cellHeight = (availableHeight - (rows - 1) * vSpacing) / rows;
        
        int index = 0;
        for (UIBaseElement child : container.getChildren()) {
            if (!child.isVisible()) continue;
            if (index >= columns * rows) break;
            
            int row = index / columns;
            int col = index % columns;
            
            float x = paddingLeft + col * (cellWidth + hSpacing);
            float y = paddingTop + row * (cellHeight + vSpacing);
            
            child.setBounds(x, y, cellWidth, cellHeight);
            index++;
        }
    }
}
