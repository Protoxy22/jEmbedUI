package com.jembedui.components.navigation;

import com.jembedui.components.buttons.UIButton;
import com.jembedui.core.UIBaseElement;
import com.jembedui.core.UIContainer;
import com.jembedui.events.MouseEvent;
import com.jembedui.layout.VerticalLayout;
import com.jembedui.render.NVGRenderer;
import com.jembedui.style.Color;

/**
 * Menu item with optional dropdown.
 */
public class UIMenuItem extends UIButton {
    
    private UIContainer dropdown;
    private boolean dropdownVisible = false;
    
    public UIMenuItem(String label) {
        super(label);
        setWidth(80);
        setHeight(30);
        setNormalColor(new Color(0.25f, 0.25f, 0.25f));
        setHoverColor(new Color(0.35f, 0.35f, 0.35f));
    }
    
    public void addDropdownItem(String label, Runnable action) {
        if (dropdown == null) {
            dropdown = new UIContainer();
            dropdown.setLayoutManager(new VerticalLayout(0));
            dropdown.setVisible(false);
            dropdown.getStyle().setBackgroundColor(new Color(0.3f, 0.3f, 0.3f));
            dropdown.getStyle().setBorderWidth(1);
            dropdown.getStyle().setBorderColor(new Color(0.5f, 0.5f, 0.5f));
        }
        
        UIButton item = new UIButton(label);
        item.setWidth(150);
        item.setHeight(25);
        item.setOnClick(() -> {
            if (action != null) action.run();
            hideDropdown();
        });
        dropdown.addChild(item);
    }
    
    public void showDropdown() {
        if (dropdown != null) {
            dropdownVisible = true;
            dropdown.setVisible(true);
            dropdown.setX(getX());
            dropdown.setY(getY() + getHeight());
            dropdown.setWidth(150);
            dropdown.setHeight(dropdown.getChildCount() * 25);
            if (getParent() != null) {
                getParent().addChild(dropdown);
            }
            dropdown.layout();
        }
    }
    
    public void hideDropdown() {
        if (dropdown != null) {
            dropdownVisible = false;
            dropdown.setVisible(false);
            if (dropdown.getParent() != null) {
                dropdown.getParent().removeChild(dropdown);
            }
        }
    }
    
    @Override
    public void render(NVGRenderer renderer) {
        super.render(renderer);
        
        // Handle dropdown visibility on click
        addEventListener(MouseEvent.class, event -> {
            if (event.getEventType() == MouseEvent.MouseEventType.MOUSE_CLICK) {
                if (dropdownVisible) {
                    hideDropdown();
                } else {
                    showDropdown();
                }
            }
        });
    }
}
