package com.jembedui.components.input;

import com.jembedui.core.UIBaseElement;
import com.jembedui.events.KeyboardEvent;
import com.jembedui.events.MouseEvent;
import com.jembedui.render.NVGRenderer;
import com.jembedui.style.Color;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Single-line text input field.
 */
public class UITextField extends UIBaseElement {
    
    private StringBuilder text = new StringBuilder();
    private int cursorPosition = 0;
    private boolean focused = false;
    private String placeholder = "";
    private Runnable onChangeHandler;
    
    // Cursor blinking
    private double cursorBlinkTime = 0.0;
    private static final double CURSOR_BLINK_INTERVAL = 1; // Blink every 1000ms
    private boolean cursorVisible = true;

    public UITextField() {
        super();
        setupEventHandlers();
        width = 200;
        height = 30;
    }
    
    public UITextField(String placeholder) {
        this();
        this.placeholder = placeholder;
    }
    
    private void setupEventHandlers() {
        addEventListener(MouseEvent.class, this::handleMouseEvent);
        addEventListener(KeyboardEvent.class, this::handleKeyboardEvent);
    }
    
    private void handleMouseEvent(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MouseEventType.MOUSE_DOWN) {
            // Only gain focus if clicking inside this text field
            if (!focused) {
                focused = true;
                cursorBlinkTime = 0.0;
                cursorVisible = true;
                markDirty();
            }
        }
    }

    public void loseFocus() {
        if (focused) {
            focused = false;
            markDirty();
        }
    }

    public boolean isFocused() {
        return focused;
    }

    private void handleKeyboardEvent(KeyboardEvent event) {
        if (!focused || !enabled) return;
        if (event.getEventType() != KeyboardEvent.KeyEventType.KEY_DOWN) return;
        
        // Reset cursor blink when typing
        cursorBlinkTime = 0.0;
        cursorVisible = true;

        int key = event.getKey();
        
        if (key == GLFW_KEY_BACKSPACE && cursorPosition > 0) {
            text.deleteCharAt(cursorPosition - 1);
            cursorPosition--;
            markDirty();
            notifyChange();
        } else if (key == GLFW_KEY_DELETE && cursorPosition < text.length()) {
            text.deleteCharAt(cursorPosition);
            markDirty();
            notifyChange();
        } else if (key == GLFW_KEY_LEFT && cursorPosition > 0) {
            cursorPosition--;
            markDirty();
        } else if (key == GLFW_KEY_RIGHT && cursorPosition < text.length()) {
            cursorPosition++;
            markDirty();
        } else if (key == GLFW_KEY_HOME) {
            cursorPosition = 0;
            markDirty();
        } else if (key == GLFW_KEY_END) {
            cursorPosition = text.length();
            markDirty();
        } else {
            char c = event.getCharacter();
            if (c >= 32 && c < 127) {  // Printable ASCII
                text.insert(cursorPosition, c);
                cursorPosition++;
                markDirty();
                notifyChange();
            }
        }
    }
    
    private void notifyChange() {
        if (onChangeHandler != null) {
            onChangeHandler.run();
        }
    }
    
    public String getText() {
        return text.toString();
    }
    
    public void setText(String text) {
        this.text = new StringBuilder(text);
        cursorPosition = text.length();
        markDirty();
    }
    
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        markDirty();
    }
    
    public void setOnChange(Runnable handler) {
        this.onChangeHandler = handler;
    }
    
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // Update cursor blinking when focused
        if (focused) {
            cursorBlinkTime += deltaTime;
            if (cursorBlinkTime >= CURSOR_BLINK_INTERVAL) {
                cursorBlinkTime = 0.0;
                cursorVisible = !cursorVisible;
                markDirty();
            }
        }
    }

    @Override
    public void render(NVGRenderer renderer) {
        if (!visible) return;
        
        float ax = getAbsoluteX();
        float ay = getAbsoluteY();
        
        // Draw background
        Color bgColor = focused ? Color.WHITE : new Color(0.95f, 0.95f, 0.95f);
        renderer.drawRect(ax, ay, width, height, bgColor, 4);
        
        // Draw border
        Color borderColor = focused ? new Color(0.2f, 0.6f, 0.8f) : new Color(0.5f, 0.5f, 0.5f);
        float borderWidth = focused ? 2 : 1;
        renderer.drawRectOutline(ax, ay, width, height, borderWidth, borderColor, 4);
        
        // Draw text or placeholder
        float fontSize = style.getFontSize();
        float textX = ax + 5;
        float textY = ay + height / 2;

        if (text.length() > 0) {
            renderer.drawText(textX, textY, text.toString(), "default", fontSize, Color.BLACK,
                             org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT | org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE);
        } else if (!placeholder.isEmpty()) {
            renderer.drawText(textX, textY, placeholder, "default", fontSize, new Color(0.6f, 0.6f, 0.6f),
                             org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT | org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE);
        }
        
        // Draw cursor if focused and visible (blinking)
        if (focused && cursorVisible) {
            String beforeCursor = text.substring(0, cursorPosition);
            float[] cursorOffset = renderer.measureText(beforeCursor, fontSize);
            renderer.drawLine(textX + cursorOffset[0], ay + 5, 
                            textX + cursorOffset[0], ay + height - 5, 
                            1, Color.BLACK);
        }
        
        markClean();
    }
}
