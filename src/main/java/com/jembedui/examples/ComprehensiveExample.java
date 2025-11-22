package com.jembedui.examples;

import com.jembedui.components.UIPanel;
import com.jembedui.components.buttons.*;
import com.jembedui.components.display.UILabel;
import com.jembedui.components.display.UIText;
import com.jembedui.components.input.UITextField;
import com.jembedui.components.navigation.UIMenu;
import com.jembedui.components.navigation.UIMenuItem;
import com.jembedui.components.navigation.UITabView;
import com.jembedui.core.UIContainer;
import com.jembedui.core.UIContext;
import com.jembedui.events.MouseEvent;
import com.jembedui.layout.*;
import com.jembedui.style.Color;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Comprehensive example application demonstrating all UI components.
 */
public class ComprehensiveExample {
    
    private long window;
    private UIContext uiContext;
    private int windowWidth = 1024;
    private int windowHeight = 768;
    
    public static void main(String[] args) {
        new ComprehensiveExample().run();
    }
    
    public void run() {
        init();
        setupUI();
        loop();
        cleanup();
    }
    
    private void init() {
        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        
        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        
        // Create the window
        window = glfwCreateWindow(windowWidth, windowHeight, "jEmbedUI - Comprehensive Example", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }
        
        // Setup callbacks
        glfwSetKeyCallback(window, this::keyCallback);
        glfwSetMouseButtonCallback(window, this::mouseButtonCallback);
        glfwSetCursorPosCallback(window, this::cursorPosCallback);
        glfwSetScrollCallback(window, this::scrollCallback);
        glfwSetCharCallback(window, this::charCallback);
        glfwSetFramebufferSizeCallback(window, this::framebufferSizeCallback);
        
        // Center window
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(window, 
                (vidmode.width() - pWidth.get(0)) / 2,
                (vidmode.height() - pHeight.get(0)) / 2
            );
        }
        
        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);  // Enable v-sync
        glfwShowWindow(window);
        
        // Initialize OpenGL bindings
        GL.createCapabilities();
        
        System.out.println("OpenGL Version: " + glGetString(GL_VERSION));
        System.out.println("OpenGL Vendor: " + glGetString(GL_VENDOR));
        System.out.println("OpenGL Renderer: " + glGetString(GL_RENDERER));
        
        // Create UI context
        uiContext = new UIContext(windowWidth, windowHeight, 1.0f);
    }
    
    private void setupUI() {
        UIContainer root = uiContext.getRoot();
        root.getStyle().setBackgroundColor(new Color(0.15f, 0.15f, 0.18f));
        
        // Create menu bar
        UIMenu menuBar = new UIMenu();
        menuBar.setBounds(0, 0, windowWidth, 30);
        menuBar.getStyle().setBackgroundColor(new Color(0.2f, 0.2f, 0.22f));
        
        UIMenuItem fileMenu = new UIMenuItem("File");
        fileMenu.addDropdownItem("New", () -> System.out.println("New clicked"));
        fileMenu.addDropdownItem("Open", () -> System.out.println("Open clicked"));
        fileMenu.addDropdownItem("Save", () -> System.out.println("Save clicked"));
        fileMenu.addDropdownItem("Exit", () -> glfwSetWindowShouldClose(window, true));
        menuBar.addChild(fileMenu);
        
        UIMenuItem editMenu = new UIMenuItem("Edit");
        editMenu.addDropdownItem("Cut", () -> System.out.println("Cut clicked"));
        editMenu.addDropdownItem("Copy", () -> System.out.println("Copy clicked"));
        editMenu.addDropdownItem("Paste", () -> System.out.println("Paste clicked"));
        menuBar.addChild(editMenu);
        
        root.addChild(menuBar);
        
        // Create tab view
        UITabView tabView = new UITabView();
        tabView.setBounds(0, 30, windowWidth, windowHeight - 30);
        
        // Tab 1: Buttons Demo
        UIContainer buttonsTab = createButtonsTab();
        tabView.addTab("Buttons", buttonsTab);
        
        // Tab 2: Sliders & Progress
        UIContainer slidersTab = createSlidersTab();
        tabView.addTab("Sliders", slidersTab);
        
        // Tab 3: Input Controls
        UIContainer inputTab = createInputTab();
        tabView.addTab("Input", inputTab);
        
        root.addChild(tabView);
        root.layout();
    }
    
    private UIContainer createButtonsTab() {
        UIPanel panel = new UIPanel();
        panel.setBounds(10, 10, windowWidth - 20, windowHeight - 80);
        panel.getStyle().setBackgroundColor(new Color(0.12f, 0.12f, 0.15f));
        panel.getStyle().setPadding(20);
        panel.setLayoutManager(new VerticalLayout(15));
        
        // Title
        UILabel title = new UILabel("Button Components");
        title.getStyle().setFontSize(24);
        title.getStyle().setForegroundColor(new Color(0.9f, 0.9f, 0.9f));
        title.setHeight(30);
        panel.addChild(title);
        
        // Regular buttons
        UIPanel buttonRow1 = new UIPanel();
        buttonRow1.setHeight(40);
        buttonRow1.setLayoutManager(new HorizontalLayout(10));
        buttonRow1.getStyle().setBackgroundColor(Color.TRANSPARENT);
        
        UIButton btn1 = new UIButton("Click Me");
        btn1.setOnClick(() -> System.out.println("Button 1 clicked!"));
        buttonRow1.addChild(btn1);
        
        UIButton btn2 = new UIButton("Disabled");
        btn2.setEnabled(false);
        buttonRow1.addChild(btn2);
        
        UIButton btn3 = new UIButton("Another Button");
        btn3.setOnClick(() -> System.out.println("Button 3 clicked!"));
        buttonRow1.addChild(btn3);
        
        panel.addChild(buttonRow1);
        
        // Toggle buttons
        UILabel toggleLabel = new UILabel("Toggle Buttons:");
        toggleLabel.getStyle().setFontSize(18);
        toggleLabel.setHeight(25);
        panel.addChild(toggleLabel);
        
        UIPanel toggleRow = new UIPanel();
        toggleRow.setHeight(40);
        toggleRow.setLayoutManager(new HorizontalLayout(10));
        toggleRow.getStyle().setBackgroundColor(Color.TRANSPARENT);
        
        UIToggleButton toggle1 = new UIToggleButton("Toggle 1");
        toggle1.setOnClick(() -> {
            toggle1.setToggled(!toggle1.isToggled());
            System.out.println("Toggle 1: " + toggle1.isToggled());
        });
        toggleRow.addChild(toggle1);
        
        UIToggleButton toggle2 = new UIToggleButton("Toggle 2");
        toggle2.setOnClick(() -> {
            toggle2.setToggled(!toggle2.isToggled());
            System.out.println("Toggle 2: " + toggle2.isToggled());
        });
        toggleRow.addChild(toggle2);
        
        panel.addChild(toggleRow);
        
        // Checkboxes
        UILabel checkLabel = new UILabel("Checkboxes:");
        checkLabel.getStyle().setFontSize(18);
        checkLabel.setHeight(25);
        panel.addChild(checkLabel);
        
        UIPanel checkRow = new UIPanel();
        checkRow.setHeight(30);
        checkRow.setLayoutManager(new HorizontalLayout(15));
        checkRow.getStyle().setBackgroundColor(Color.TRANSPARENT);
        
        UICheckbox check1 = new UICheckbox("Option 1");
        check1.setOnClick(() -> {
            check1.setChecked(!check1.isChecked());
            System.out.println("Checkbox 1: " + check1.isChecked());
        });
        checkRow.addChild(check1);
        
        UICheckbox check2 = new UICheckbox("Option 2");
        check2.setOnClick(() -> {
            check2.setChecked(!check2.isChecked());
            System.out.println("Checkbox 2: " + check2.isChecked());
        });
        checkRow.addChild(check2);
        
        UICheckbox check3 = new UICheckbox("Option 3");
        check3.setOnClick(() -> {
            check3.setChecked(!check3.isChecked());
            System.out.println("Checkbox 3: " + check3.isChecked());
        });
        checkRow.addChild(check3);
        
        panel.addChild(checkRow);
        
        return panel;
    }
    
    private UIContainer createSlidersTab() {
        UIPanel panel = new UIPanel();
        panel.setBounds(10, 10, windowWidth - 20, windowHeight - 80);
        panel.getStyle().setBackgroundColor(new Color(0.12f, 0.12f, 0.15f));
        panel.getStyle().setPadding(20);
        panel.setLayoutManager(new VerticalLayout(20));
        
        // Title
        UILabel title = new UILabel("Sliders & Progress");
        title.getStyle().setFontSize(24);
        title.getStyle().setForegroundColor(new Color(0.9f, 0.9f, 0.9f));
        title.setHeight(30);
        panel.addChild(title);
        
        // Horizontal slider
        UILabel sliderLabel1 = new UILabel("Horizontal Slider:");
        sliderLabel1.getStyle().setFontSize(16);
        sliderLabel1.setHeight(20);
        panel.addChild(sliderLabel1);
        
        UISlider hSlider = new UISlider(UISlider.Orientation.HORIZONTAL);
        hSlider.setValue(0.5f);
        hSlider.setOnChange(() -> System.out.println("H-Slider: " + hSlider.getValue()));
        panel.addChild(hSlider);
        
        // Vertical slider
        UILabel sliderLabel2 = new UILabel("Vertical Slider:");
        sliderLabel2.getStyle().setFontSize(16);
        sliderLabel2.setHeight(20);
        panel.addChild(sliderLabel2);
        
        UISlider vSlider = new UISlider(UISlider.Orientation.VERTICAL);
        vSlider.setValue(0.7f);
        vSlider.setOnChange(() -> System.out.println("V-Slider: " + vSlider.getValue()));
        panel.addChild(vSlider);
        
        // Progress bars
        UILabel progressLabel = new UILabel("Progress Bars:");
        progressLabel.getStyle().setFontSize(16);
        progressLabel.setHeight(20);
        panel.addChild(progressLabel);
        
        UIProgressBar progress1 = new UIProgressBar();
        progress1.setProgress(0.3f);
        panel.addChild(progress1);
        
        UIProgressBar progress2 = new UIProgressBar();
        progress2.setProgress(0.7f);
        progress2.setBarColor(new Color(0.2f, 0.8f, 0.3f));
        panel.addChild(progress2);
        
        UIProgressBar progress3 = new UIProgressBar();
        progress3.setProgress(1.0f);
        progress3.setBarColor(new Color(0.8f, 0.2f, 0.2f));
        panel.addChild(progress3);
        
        return panel;
    }
    
    private UIContainer createInputTab() {
        UIPanel panel = new UIPanel();
        panel.setBounds(10, 10, windowWidth - 20, windowHeight - 80);
        panel.getStyle().setBackgroundColor(new Color(0.12f, 0.12f, 0.15f));
        panel.getStyle().setPadding(20);
        panel.setLayoutManager(new VerticalLayout(20));
        
        // Title
        UILabel title = new UILabel("Input Controls");
        title.getStyle().setFontSize(24);
        title.getStyle().setForegroundColor(new Color(0.9f, 0.9f, 0.9f));
        title.setHeight(30);
        panel.addChild(title);
        
        // Text fields
        UILabel textFieldLabel = new UILabel("Text Fields:");
        textFieldLabel.getStyle().setFontSize(16);
        textFieldLabel.setHeight(20);
        panel.addChild(textFieldLabel);
        
        UITextField textField1 = new UITextField("Enter your name");
        textField1.setOnChange(() -> System.out.println("Text 1: " + textField1.getText()));
        panel.addChild(textField1);
        
        UITextField textField2 = new UITextField("Enter your email");
        textField2.setOnChange(() -> System.out.println("Text 2: " + textField2.getText()));
        panel.addChild(textField2);
        
        UITextField textField3 = new UITextField("Enter a message");
        textField3.setOnChange(() -> System.out.println("Text 3: " + textField3.getText()));
        panel.addChild(textField3);
        
        // Info text
        UILabel infoLabel = new UILabel("Type in the text fields above to see the output in the console.");
        infoLabel.getStyle().setFontSize(14);
        infoLabel.getStyle().setForegroundColor(new Color(0.7f, 0.7f, 0.7f));
        infoLabel.setHeight(20);
        panel.addChild(infoLabel);
        
        return panel;
    }
    
    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            // Clear
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glClearColor(0.15f, 0.15f, 0.18f, 1.0f);
            
            // Update UI
            uiContext.update(0.016f);  // ~60 FPS
            
            // Render UI
            uiContext.render();
            
            // Swap buffers and poll events
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }
    
    private void cleanup() {
        uiContext.cleanup();
        glfwDestroyWindow(window);
        glfwTerminate();
    }
    
    // GLFW Callbacks
    
    private void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            glfwSetWindowShouldClose(window, true);
        }
        uiContext.handleKeyEvent(key, scancode, mods, action == GLFW_PRESS || action == GLFW_REPEAT, '\0');
    }
    
    private void charCallback(long window, int codepoint) {
        uiContext.handleKeyEvent(0, 0, 0, true, (char) codepoint);
    }
    
    private void mouseButtonCallback(long window, int button, int action, int mods) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            double[] xpos = new double[1];
            double[] ypos = new double[1];
            glfwGetCursorPos(window, xpos, ypos);
            
            MouseEvent.MouseButton btn = switch (button) {
                case GLFW_MOUSE_BUTTON_LEFT -> MouseEvent.MouseButton.LEFT;
                case GLFW_MOUSE_BUTTON_RIGHT -> MouseEvent.MouseButton.RIGHT;
                case GLFW_MOUSE_BUTTON_MIDDLE -> MouseEvent.MouseButton.MIDDLE;
                default -> MouseEvent.MouseButton.NONE;
            };
            
            uiContext.handleMouseButton((float) xpos[0], (float) ypos[0], btn, action == GLFW_PRESS);
        }
    }
    
    private void cursorPosCallback(long window, double xpos, double ypos) {
        uiContext.handleMouseMove((float) xpos, (float) ypos);
    }
    
    private void scrollCallback(long window, double xoffset, double yoffset) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            double[] xpos = new double[1];
            double[] ypos = new double[1];
            glfwGetCursorPos(window, xpos, ypos);
            uiContext.handleMouseWheel((float) xpos[0], (float) ypos[0], 
                                      (float) xoffset, (float) yoffset);
        }
    }
    
    private void framebufferSizeCallback(long window, int width, int height) {
        windowWidth = width;
        windowHeight = height;
        glViewport(0, 0, width, height);
        uiContext.setWindowSize(width, height);
    }
}
