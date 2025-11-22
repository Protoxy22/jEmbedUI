package com.jembedui.components.navigation;

import com.jembedui.core.UIContainer;
import com.jembedui.layout.HorizontalLayout;

/**
 * Menu bar container.
 */
public class UIMenu extends UIContainer {
    
    public UIMenu() {
        super();
        setLayoutManager(new HorizontalLayout(0));
        height = 30;
    }
}
