package com.jembedui.components.buttons;

import com.jembedui.render.NVGRenderer;
import com.jembedui.style.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Radio button with exclusive group selection.
 */
public class UIRadioButton extends UICheckbox {
    
    private static final List<RadioButtonGroup> groups = new ArrayList<>();
    private String groupName;
    
    public UIRadioButton(String label, String groupName) {
        super(label);
        this.groupName = groupName;
    }
    
    @Override
    public void setChecked(boolean checked) {
        if (checked && groupName != null) {
            // Uncheck all other radio buttons in the same group
            RadioButtonGroup group = getOrCreateGroup(groupName);
            for (UIRadioButton button : group.buttons) {
                if (button != this) {
                    button.setCheckedInternal(false);
                }
            }
            getOrCreateGroup(groupName).addButton(this);
        }
        super.setChecked(checked);
    }
    
    private void setCheckedInternal(boolean checked) {
        super.setChecked(checked);
    }
    
    private RadioButtonGroup getOrCreateGroup(String name) {
        for (RadioButtonGroup group : groups) {
            if (group.name.equals(name)) {
                return group;
            }
        }
        RadioButtonGroup newGroup = new RadioButtonGroup(name);
        groups.add(newGroup);
        return newGroup;
    }
    
    @Override
    public void render(NVGRenderer renderer) {
        if (!visible) return;
        
        // Draw radio button as circle instead of square
        float cx = getAbsoluteX() + getWidth() / 2;
        float cy = getAbsoluteY() + getHeight() / 2;
        float radius = Math.min(getWidth(), getHeight()) / 2;
        
        // Draw outer circle
        Color bgColor = isEnabled() ? Color.WHITE : new Color(0.7f, 0.7f, 0.7f);
        renderer.drawCircle(cx, cy, radius, bgColor);
        
        // Draw border
        Color borderColor = isEnabled() ? new Color(0.5f, 0.5f, 0.5f) : new Color(0.4f, 0.4f, 0.4f);
        renderer.drawCircleOutline(cx, cy, radius, 1, borderColor);
        
        // Draw inner circle if checked
        if (isChecked()) {
            Color checkColor = isEnabled() ? new Color(0.2f, 0.6f, 0.8f) : new Color(0.4f, 0.4f, 0.4f);
            renderer.drawCircle(cx, cy, radius * 0.6f, checkColor);
        }
        
        // Draw label if present
        if (!getLabel().isEmpty()) {
            float fontSize = getStyle().getFontSize();
            Color textColor = isEnabled() ? getStyle().getForegroundColor() : new Color(0.5f, 0.5f, 0.5f);
            renderer.drawText(getAbsoluteX() + getWidth() + 5, 
                            getAbsoluteY() + (getHeight() + fontSize) / 2, 
                            getLabel(), fontSize, textColor);
        }
        
        markClean();
    }
    
    private static class RadioButtonGroup {
        String name;
        List<UIRadioButton> buttons = new ArrayList<>();
        
        RadioButtonGroup(String name) {
            this.name = name;
        }
        
        void addButton(UIRadioButton button) {
            if (!buttons.contains(button)) {
                buttons.add(button);
            }
        }
    }
}
