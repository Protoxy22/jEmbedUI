package com.jembedui.components.navigation;

import com.jembedui.core.UIContainer;
import com.jembedui.layout.HorizontalLayout;

/**
 * Tab view for switching between different panels.
 */
public class UITabView extends UIContainer {
    
    private UIContainer tabBar;
    private UIContainer contentArea;
    private int activeTab = 0;
    
    public UITabView() {
        super();
        
        tabBar = new UIContainer();
        tabBar.setLayoutManager(new HorizontalLayout(2));
        tabBar.setBounds(0, 0, width, 30);
        tabBar.getStyle().setBackgroundColor(new com.jembedui.style.Color(0.2f, 0.2f, 0.2f));
        addChild(tabBar);
        
        contentArea = new UIContainer();
        contentArea.setBounds(0, 30, width, height - 30);
        contentArea.getStyle().setBackgroundColor(new com.jembedui.style.Color(0.15f, 0.15f, 0.15f));
        addChild(contentArea);
    }
    
    public void addTab(String title, UIContainer content) {
        int tabIndex = tabBar.getChildCount();
        
        com.jembedui.components.buttons.UIButton tabButton = 
            new com.jembedui.components.buttons.UIButton(title);
        tabButton.setWidth(100);
        tabButton.setHeight(30);
        tabButton.setOnClick(() -> switchToTab(tabIndex));
        
        tabBar.addChild(tabButton);
        content.setVisible(tabIndex == activeTab);
        contentArea.addChild(content);
        
        layout();
    }
    
    public void switchToTab(int index) {
        if (index >= 0 && index < contentArea.getChildCount()) {
            for (int i = 0; i < contentArea.getChildCount(); i++) {
                contentArea.getChildAt(i).setVisible(i == index);
            }
            activeTab = index;
            markDirty();
        }
    }
    
    @Override
    public void layout() {
        super.layout();
        if (contentArea != null) {
            contentArea.setBounds(0, 30, width, height - 30);
        }
    }
}
