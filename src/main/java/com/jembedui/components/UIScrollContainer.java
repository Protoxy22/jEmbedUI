package com.jembedui.components;

import com.jembedui.core.UIContainer;
import com.jembedui.events.MouseEvent;
import com.jembedui.render.NVGRenderer;
import com.jembedui.style.Color;

/**
 * Scrollable container with scrollbars.
 */
public class UIScrollContainer extends UIContainer {
    
    private float scrollX = 0;
    private float scrollY = 0;
    private float contentWidth = 0;
    private float contentHeight = 0;
    private boolean showScrollbars = true;
    private boolean draggingVertical = false;
    private boolean draggingHorizontal = false;
    
    public UIScrollContainer() {
        super();
        setupEventHandlers();
    }
    
    private void setupEventHandlers() {
        addEventListener(MouseEvent.class, this::handleMouseEvent);
    }
    
    private void handleMouseEvent(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MouseEventType.MOUSE_WHEEL) {
            scrollY += event.getWheelDeltaY() * 20;
            scrollY = Math.max(0, Math.min(scrollY, Math.max(0, contentHeight - height)));
            markDirty();
        }
    }
    
    public void setScrollX(float scrollX) {
        this.scrollX = Math.max(0, Math.min(scrollX, Math.max(0, contentWidth - width)));
        markDirty();
    }
    
    public void setScrollY(float scrollY) {
        this.scrollY = Math.max(0, Math.min(scrollY, Math.max(0, contentHeight - height)));
        markDirty();
    }
    
    public float getScrollX() {
        return scrollX;
    }
    
    public float getScrollY() {
        return scrollY;
    }
    
    public void setShowScrollbars(boolean show) {
        this.showScrollbars = show;
        markDirty();
    }
    
    @Override
    public void layout() {
        super.layout();
        
        // Calculate content size
        contentWidth = 0;
        contentHeight = 0;
        for (var child : children) {
            if (child.isVisible()) {
                contentWidth = Math.max(contentWidth, child.getX() + child.getWidth());
                contentHeight = Math.max(contentHeight, child.getY() + child.getHeight());
            }
        }
    }
    
    @Override
    public void render(NVGRenderer renderer) {
        if (!visible) return;
        
        // Render background
        super.render(renderer);
        
        // Set up scissoring for content area
        renderer.save();
        renderer.setScissor(getAbsoluteX(), getAbsoluteY(), width, height);
        
        // Render children with scroll offset
        for (var child : children) {
            if (child.isVisible()) {
                float originalX = child.getX();
                float originalY = child.getY();
                child.setX(originalX - scrollX);
                child.setY(originalY - scrollY);
                child.render(renderer);
                child.setX(originalX);
                child.setY(originalY);
            }
        }
        
        renderer.resetScissor();
        renderer.restore();
        
        // Draw scrollbars if needed
        if (showScrollbars) {
            drawScrollbars(renderer);
        }
        
        markClean();
    }
    
    private void drawScrollbars(NVGRenderer renderer) {
        Color scrollbarColor = new Color(0.4f, 0.4f, 0.4f, 0.7f);
        Color trackColor = new Color(0.2f, 0.2f, 0.2f, 0.3f);
        
        float scrollbarWidth = 8;
        float ax = getAbsoluteX();
        float ay = getAbsoluteY();
        
        // Vertical scrollbar
        if (contentHeight > height) {
            float trackHeight = height;
            float thumbHeight = Math.max(20, (height / contentHeight) * trackHeight);
            float thumbY = (scrollY / (contentHeight - height)) * (trackHeight - thumbHeight);
            
            // Draw track
            renderer.drawRect(ax + width - scrollbarWidth, ay, scrollbarWidth, trackHeight, trackColor, 4);
            
            // Draw thumb
            renderer.drawRect(ax + width - scrollbarWidth, ay + thumbY, scrollbarWidth, thumbHeight, 
                            scrollbarColor, 4);
        }
        
        // Horizontal scrollbar
        if (contentWidth > width) {
            float trackWidth = width;
            float thumbWidth = Math.max(20, (width / contentWidth) * trackWidth);
            float thumbX = (scrollX / (contentWidth - width)) * (trackWidth - thumbWidth);
            
            // Draw track
            renderer.drawRect(ax, ay + height - scrollbarWidth, trackWidth, scrollbarWidth, trackColor, 4);
            
            // Draw thumb
            renderer.drawRect(ax + thumbX, ay + height - scrollbarWidth, thumbWidth, scrollbarWidth, 
                            scrollbarColor, 4);
        }
    }
}
