package com.jembedui.core;

import com.jembedui.layout.LayoutManager;
import com.jembedui.render.NVGRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Container that can hold child elements.
 */
public class UIContainer extends UIBaseElement {
    
    protected final List<UIBaseElement> children = new ArrayList<>();
    protected LayoutManager layoutManager;
    
    public UIContainer() {
        super();
    }
    
    // Child management
    public void addChild(UIBaseElement child) {
        if (child.parent != null) {
            child.parent.removeChild(child);
        }
        children.add(child);
        child.setParent(this);
        markDirty();
    }
    
    public void removeChild(UIBaseElement child) {
        if (children.remove(child)) {
            child.setParent(null);
            markDirty();
        }
    }
    
    public void removeAllChildren() {
        for (UIBaseElement child : new ArrayList<>(children)) {
            removeChild(child);
        }
    }
    
    public List<UIBaseElement> getChildren() {
        return new ArrayList<>(children);
    }
    
    public int getChildCount() {
        return children.size();
    }
    
    public UIBaseElement getChildAt(int index) {
        return children.get(index);
    }
    
    // Layout
    public void setLayoutManager(LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        markDirty();
    }
    
    public LayoutManager getLayoutManager() {
        return layoutManager;
    }
    
    @Override
    public void layout() {
        if (layoutManager != null) {
            layoutManager.layout(this);
        }
        
        // Layout children
        for (UIBaseElement child : children) {
            child.layout();
        }
    }
    
    // Hit testing
    public UIBaseElement getElementAt(float x, float y) {
        // Check children in reverse order (top to bottom)
        for (int i = children.size() - 1; i >= 0; i--) {
            UIBaseElement child = children.get(i);
            if (!child.isVisible() || !child.isEnabled()) continue;
            
            if (child instanceof UIContainer) {
                UIBaseElement found = ((UIContainer) child).getElementAt(x, y);
                if (found != null) return found;
            } else if (child.containsPoint(x, y)) {
                return child;
            }
        }
        
        // Check self
        if (containsPoint(x, y)) {
            return this;
        }
        
        return null;
    }
    
    @Override
    public void render(NVGRenderer renderer) {
        if (!visible) return;
        
        // Render self
        super.render(renderer);
        
        // Render children
        for (UIBaseElement child : children) {
            if (child.isVisible()) {
                child.render(renderer);
            }
        }
    }
    
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        
        // Update children
        for (UIBaseElement child : children) {
            child.update(deltaTime);
        }
    }
}
