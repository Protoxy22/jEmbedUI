package com.jembedui.core;

import com.jembedui.events.*;
import com.jembedui.render.NVGRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Root manager for the UI system.
 * Handles input dispatch, event propagation, and rendering pipeline.
 */
public class UIContext {
    
    private final UIContainer root;
    private final NVGRenderer renderer;
    private float windowWidth;
    private float windowHeight;
    private float pixelRatio = 1.0f;
    
    private UIBaseElement hoveredElement;
    private UIBaseElement focusedElement;
    private UIBaseElement pressedElement;
    
    public UIContext(float windowWidth, float windowHeight, float pixelRatio) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.pixelRatio = pixelRatio;
        this.renderer = new NVGRenderer();
        this.root = new UIContainer();
        this.root.setBounds(0, 0, windowWidth, windowHeight);
    }
    
    public UIContainer getRoot() {
        return root;
    }
    
    public NVGRenderer getRenderer() {
        return renderer;
    }
    
    public void setWindowSize(float width, float height) {
        this.windowWidth = width;
        this.windowHeight = height;
        root.setWidth(width);
        root.setHeight(height);
        root.layout();
    }
    
    public float getWindowWidth() {
        return windowWidth;
    }
    
    public float getWindowHeight() {
        return windowHeight;
    }
    
    // Focus management
    public void setFocus(UIBaseElement element) {
        if (focusedElement != element) {
            focusedElement = element;
        }
    }
    
    public UIBaseElement getFocusedElement() {
        return focusedElement;
    }
    
    // Input handling
    public void handleMouseMove(float x, float y) {
        UIBaseElement element = root.getElementAt(x, y);
        
        if (element != hoveredElement) {
            if (hoveredElement != null) {
                MouseEvent leaveEvent = new MouseEvent(hoveredElement, MouseEvent.MouseEventType.MOUSE_LEAVE, x, y);
                propagateEvent(hoveredElement, leaveEvent);
            }
            
            if (element != null) {
                MouseEvent enterEvent = new MouseEvent(element, MouseEvent.MouseEventType.MOUSE_ENTER, x, y);
                propagateEvent(element, enterEvent);
            }
            
            hoveredElement = element;
        }
        
        if (element != null) {
            MouseEvent moveEvent = new MouseEvent(element, MouseEvent.MouseEventType.MOUSE_MOVE, x, y);
            propagateEvent(element, moveEvent);
        }
    }
    
    public void handleMouseButton(float x, float y, MouseEvent.MouseButton button, boolean pressed) {
        UIBaseElement element = root.getElementAt(x, y);
        
        if (pressed) {
            pressedElement = element;
            if (element != null) {
                setFocus(element);
                MouseEvent downEvent = new MouseEvent(element, MouseEvent.MouseEventType.MOUSE_DOWN, x, y, button);
                propagateEvent(element, downEvent);
            }
        } else {
            if (element != null) {
                MouseEvent upEvent = new MouseEvent(element, MouseEvent.MouseEventType.MOUSE_UP, x, y, button);
                propagateEvent(element, upEvent);
                
                if (element == pressedElement) {
                    MouseEvent clickEvent = new MouseEvent(element, MouseEvent.MouseEventType.MOUSE_CLICK, x, y, button);
                    propagateEvent(element, clickEvent);
                }
            }
            pressedElement = null;
        }
    }
    
    public void handleMouseWheel(float x, float y, float deltaX, float deltaY) {
        UIBaseElement element = root.getElementAt(x, y);
        if (element != null) {
            MouseEvent wheelEvent = new MouseEvent(element, MouseEvent.MouseEventType.MOUSE_WHEEL, 
                                                  x, y, MouseEvent.MouseButton.NONE, deltaX, deltaY);
            propagateEvent(element, wheelEvent);
        }
    }
    
    public void handleKeyEvent(int key, int scancode, int mods, boolean pressed, char character) {
        if (focusedElement != null) {
            KeyboardEvent.KeyEventType type = pressed ? KeyboardEvent.KeyEventType.KEY_DOWN : KeyboardEvent.KeyEventType.KEY_UP;
            KeyboardEvent keyEvent = new KeyboardEvent(focusedElement, type, key, scancode, mods, character);
            propagateEvent(focusedElement, keyEvent);
        }
    }
    
    // Event propagation (capture → target → bubble)
    private void propagateEvent(UIBaseElement target, UIEvent event) {
        List<UIBaseElement> path = new ArrayList<>();
        UIBaseElement current = target;
        while (current != null) {
            path.add(0, current);
            current = current.getParent();
        }
        
        // Capture phase
        event.setPhase(UIEvent.EventPhase.CAPTURE);
        for (UIBaseElement element : path) {
            if (element == target) break;
            event.setCurrentTarget(element);
            element.dispatchEvent(event);
            if (event.isPropagationStopped()) return;
        }
        
        // Target phase
        event.setPhase(UIEvent.EventPhase.TARGET);
        event.setCurrentTarget(target);
        target.dispatchEvent(event);
        if (event.isPropagationStopped()) return;
        
        // Bubble phase
        event.setPhase(UIEvent.EventPhase.BUBBLE);
        for (int i = path.size() - 2; i >= 0; i--) {
            UIBaseElement element = path.get(i);
            event.setCurrentTarget(element);
            element.dispatchEvent(event);
            if (event.isPropagationStopped()) return;
        }
    }
    
    // Update and render
    public void update(float deltaTime) {
        root.update(deltaTime);
    }
    
    public void render() {
        renderer.beginFrame(windowWidth, windowHeight, pixelRatio);
        root.render(renderer);
        renderer.endFrame();
    }
    
    public void cleanup() {
        renderer.cleanup();
    }
}
