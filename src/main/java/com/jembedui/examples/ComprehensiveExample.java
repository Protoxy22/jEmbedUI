package com.jembedui.examples;

import com.jembedui.components.UIPanel;
import com.jembedui.components.buttons.*;
import com.jembedui.components.display.UILabel;
import com.jembedui.components.input.UITextField;
import com.jembedui.components.navigation.UIMenu;
import com.jembedui.components.navigation.UIMenuItem;
import com.jembedui.components.navigation.UITabView;
import com.jembedui.core.UIContainer;
import com.jembedui.core.UIContext;
import com.jembedui.events.MouseEvent;
import com.jembedui.layout.*;
import com.jembedui.style.Color;
import org.lwjgl.opengles.GLES;
import org.lwjgl.sdl.SDL_Event;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengles.GLES20.*;
import static org.lwjgl.sdl.SDLError.SDL_GetError;
import static org.lwjgl.sdl.SDLEvents.*;
import static org.lwjgl.sdl.SDLHints.SDL_HINT_VIDEO_DRIVER;
import static org.lwjgl.sdl.SDLHints.SDL_SetHint;
import static org.lwjgl.sdl.SDLInit.*;
import static org.lwjgl.sdl.SDLKeycode.*;
import static org.lwjgl.sdl.SDLMouse.*;
import static org.lwjgl.sdl.SDLTimer.SDL_Delay;
import static org.lwjgl.sdl.SDLVideo.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memUTF8;

/**
 * Comprehensive example application demonstrating all UI components.
 */
public class ComprehensiveExample {
    
    private long window;
    private long glContext;
    private UIContext uiContext;
    private int windowWidth = 800;
    private int windowHeight = 480;
    private boolean running = true;

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
        // Detect ARM architecture (likely Raspberry Pi or similar embedded device)
        String osArch = System.getProperty("os.arch").toLowerCase();
        String osName = System.getProperty("os.name").toLowerCase();
        boolean isARM = osArch.contains("arm") || osArch.contains("aarch");
        boolean isLinux = osName.contains("linux");

        // Use KMSDRM on ARM Linux systems (like Raspberry Pi)
        if (isARM && isLinux) {
            System.out.println("ARM architecture detected (" + osArch + "), using KMSDRM video driver");
            SDL_SetHint(SDL_HINT_VIDEO_DRIVER, "kmsdrm");
        } else {
            System.out.println("Non-ARM architecture detected (" + osArch + "), using default video driver");
        }

        if (!SDL_Init(SDL_INIT_VIDEO)) {
            throw new IllegalStateException("SDL_Init failed: " + SDL_GetError());
        }

        // Configure OpenGL context - using OpenGL 3.3 Core for NanoVG
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_PROFILE_MASK, SDL_GL_CONTEXT_PROFILE_ES);
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_MAJOR_VERSION, 2);
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_MINOR_VERSION, 0);
        SDL_GL_SetAttribute(SDL_GL_DOUBLEBUFFER, 1);


        // Create window
        window = SDL_CreateWindow(
                "jEmbedUI - Comprehensive Example",
                windowWidth,
                windowHeight,
                SDL_WINDOW_OPENGL | SDL_WINDOW_RESIZABLE
        );
        if (window == NULL) {
            SDL_Quit();
            throw new IllegalStateException("SDL_CreateWindow failed: " + SDL_GetError());
        }

        // SDL3 + Wayland: explicitly show it
        SDL_ShowWindow(window);

        // Create OpenGL context
        glContext = SDL_GL_CreateContext(window);
        if (glContext == NULL) {
            SDL_DestroyWindow(window);
            SDL_Quit();
            throw new IllegalStateException("SDL_GL_CreateContext failed: " + SDL_GetError());
        }

        // Make context current
        if (!SDL_GL_MakeCurrent(window, glContext)) {
            String err = SDL_GetError();
            SDL_GL_DestroyContext(glContext);
            SDL_DestroyWindow(window);
            SDL_Quit();
            throw new IllegalStateException("SDL_GL_MakeCurrent failed: " + err);
        }

        // Enable vsync
        SDL_GL_SetSwapInterval(1);

        // Initialize OpenGL ES bindings
        GLES.createCapabilities();

        System.out.println("Video driver: " + SDL_GetCurrentVideoDriver());
        System.out.println("GL Version: " + glGetString(GL_VERSION));
        System.out.println("GL Vendor: " + glGetString(GL_VENDOR));
        System.out.println("GL Renderer: " + glGetString(GL_RENDERER));

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
        fileMenu.addDropdownItem("Exit", () -> running = false);
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
        
        // Create checkboxes in vertical layout for better alignment
        UIPanel checkContainer = new UIPanel();
        checkContainer.setHeight(80);
        checkContainer.setLayoutManager(new VerticalLayout(10));
        checkContainer.getStyle().setBackgroundColor(Color.TRANSPARENT);

        UICheckbox check1 = new UICheckbox("Option 1");
        check1.setHeight(20);
        check1.setOnClick(() -> {
            check1.setChecked(!check1.isChecked());
            System.out.println("Checkbox 1: " + check1.isChecked());
        });
        checkContainer.addChild(check1);

        UICheckbox check2 = new UICheckbox("Option 2");
        check2.setHeight(20);
        check2.setOnClick(() -> {
            check2.setChecked(!check2.isChecked());
            System.out.println("Checkbox 2: " + check2.isChecked());
        });
        checkContainer.addChild(check2);

        UICheckbox check3 = new UICheckbox("Option 3");
        check3.setHeight(20);
        check3.setOnClick(() -> {
            check3.setChecked(!check3.isChecked());
            System.out.println("Checkbox 3: " + check3.isChecked());
        });
        checkContainer.addChild(check3);

        panel.addChild(checkContainer);

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
        SDL_Event event = SDL_Event.calloc();

        while (running) {
            // Poll events
            while (SDL_PollEvent(event)) {
                handleEvent(event);
            }

            // Clear
            glViewport(0, 0, windowWidth, windowHeight);
            glClearColor(0.15f, 0.15f, 0.18f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Update UI
            uiContext.update(0.016f);  // ~60 FPS
            
            // Render UI
            uiContext.render();
            
            // Swap buffers
            SDL_GL_SwapWindow(window);
            SDL_Delay(16);
        }

        event.free();
    }
    
    private void cleanup() {
        uiContext.cleanup();
        SDL_GL_DestroyContext(glContext);
        SDL_DestroyWindow(window);
        SDL_Quit();
    }
    
    // SDL Event Handler

    private void handleEvent(SDL_Event event) {
        int eventType = event.type();

        switch (eventType) {
            case SDL_EVENT_QUIT -> running = false;

            case SDL_EVENT_KEY_DOWN, SDL_EVENT_KEY_UP -> {
                int scancode = event.key().scancode();
                int keycode = event.key().key();
                int mods = event.key().mod();
                boolean pressed = (eventType == SDL_EVENT_KEY_DOWN);

                // Handle ESC key
                if (keycode == SDLK_ESCAPE && !pressed) {
                    running = false;
                }

                uiContext.handleKeyEvent(keycode, scancode, mods, pressed, '\0');
            }

            case SDL_EVENT_TEXT_INPUT -> {
                String text = memUTF8(event.text().text());
                if (text != null && !text.isEmpty()) {
                    uiContext.handleKeyEvent(0, 0, 0, true, text.charAt(0));
                }
            }

            case SDL_EVENT_MOUSE_BUTTON_DOWN, SDL_EVENT_MOUSE_BUTTON_UP -> {
                float x = event.button().x();
                float y = event.button().y();
                int button = event.button().button();
                boolean pressed = (eventType == SDL_EVENT_MOUSE_BUTTON_DOWN);

                MouseEvent.MouseButton btn = switch (button) {
                    case SDL_BUTTON_LEFT -> MouseEvent.MouseButton.LEFT;
                    case SDL_BUTTON_RIGHT -> MouseEvent.MouseButton.RIGHT;
                    case SDL_BUTTON_MIDDLE -> MouseEvent.MouseButton.MIDDLE;
                    default -> MouseEvent.MouseButton.NONE;
                };

                uiContext.handleMouseButton(x, y, btn, pressed);
            }

            case SDL_EVENT_MOUSE_MOTION -> {
                float x = event.motion().x();
                float y = event.motion().y();
                uiContext.handleMouseMove(x, y);
            }

            case SDL_EVENT_MOUSE_WHEEL -> {
                float xoffset = event.wheel().x();
                float yoffset = event.wheel().y();

                // Get current mouse position
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    FloatBuffer px = stack.mallocFloat(1);
                    FloatBuffer py = stack.mallocFloat(1);
                    SDL_GetMouseState(px, py);
                    uiContext.handleMouseWheel(px.get(0), py.get(0), xoffset, yoffset);
                }
            }

            case SDL_EVENT_WINDOW_RESIZED, SDL_EVENT_WINDOW_PIXEL_SIZE_CHANGED -> {
                windowWidth = event.window().data1();
                windowHeight = event.window().data2();
                glViewport(0, 0, windowWidth, windowHeight);
                uiContext.setWindowSize(windowWidth, windowHeight);
            }
        }
    }
}
