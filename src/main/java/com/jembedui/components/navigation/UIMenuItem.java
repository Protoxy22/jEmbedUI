package com.jembedui.components.navigation;

import com.jembedui.components.buttons.UIButton;
import com.jembedui.core.UIBaseElement;
import com.jembedui.core.UIContainer;
import com.jembedui.events.MouseEvent;
import com.jembedui.layout.VerticalLayout;
import com.jembedui.style.Color;

/**
 * Menu item with optional dropdown.
 */
public class UIMenuItem extends UIButton {
    
    private UIContainer dropdown;
    private boolean dropdownVisible = false;
    private boolean listenerAdded = false;

    public UIMenuItem(String label) {
        super(label);
        setWidth(80);
        setHeight(30);
        setNormalColor(new Color(0.25f, 0.25f, 0.25f));
        setHoverColor(new Color(0.35f, 0.35f, 0.35f));

        // Set up click handler
        setOnClick(() -> {
            if (dropdownVisible) {
                hideDropdown();
            } else {
                showDropdown();
            }
        });
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
            dropdown.setX(getAbsoluteX());
            dropdown.setY(getAbsoluteY() + getHeight());
            dropdown.setWidth(150);
            dropdown.setHeight(dropdown.getChildCount() * 25);
            // Add dropdown to root container so it renders on top of everything
            UIContainer root = getRootContainer();
            if (root != null) {
                root.addChild(dropdown);

                // Add a global click handler to close dropdown when clicking outside
                if (!listenerAdded) {
                    root.addEventListener(MouseEvent.class, event -> {
                        if (event.getEventType() == MouseEvent.MouseEventType.MOUSE_CLICK && dropdownVisible) {
                            UIBaseElement target = event.getTarget();
                            // Check if click is outside both the menu item and dropdown
                            if (target != this && target != dropdown && !isChildOf(dropdown, target)) {
                                hideDropdown();
                            }
                        }
                    });
                    listenerAdded = true;
                }
            }
            dropdown.layout();
        }
    }
    
    private boolean isChildOf(UIContainer container, UIBaseElement element) {
        if (container == null || element == null) return false;
        for (UIBaseElement child : container.getChildren()) {
            if (child == element) return true;
            if (child instanceof UIContainer && isChildOf((UIContainer) child, element)) {
                return true;
            }
        }
        return false;
    }

    private UIContainer getRootContainer() {
        UIContainer current = getParent();
        while (current != null && current.getParent() != null) {
            current = current.getParent();
        }
        return current;
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
    
    public boolean isDropdownVisible() {
        return dropdownVisible;
    }
}
