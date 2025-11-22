# jEmbedUI - Complete UI System Documentation

A comprehensive, modular UI system built with NanoVG and OpenGL for Java, designed to run on both desktop and embedded systems (Raspberry Pi).

## Features

### Core Architecture
- **UIContext**: Root manager handling input dispatch, event propagation, and rendering pipeline
- **Event System**: Full event propagation with capture → target → bubble phases
- **Layout System**: Flexible layout managers (Horizontal, Vertical, Grid)
- **Styling System**: Comprehensive styling with colors, fonts, borders, padding, and margins
- **NanoVG Rendering**: Hardware-accelerated vector graphics with OpenGL ES compatibility

### UI Components

#### Foundational Components
- **UIBaseElement**: Base class for all UI elements with position, size, visibility, and style
- **UIContainer**: Container that can hold and manage child elements
- **UIPanel**: Simple container with background styling
- **UIScrollContainer**: Scrollable area with automatic scrollbars

#### Interactive Components

**Buttons:**
- **UIButton**: Standard clickable button with hover and pressed states
- **UIToggleButton**: ON/OFF toggle button with state visualization
- **UICheckbox**: Checkbox with check mark rendering
- **UIRadioButton**: Exclusive selection radio button with group management

**Sliders:**
- **UISlider**: Horizontal/vertical slider with value range
- **UIProgressBar**: Progress indicator with customizable colors

**Input Controls:**
- **UITextField**: Single-line text input with cursor and placeholder support

#### Display Components
- **UIText**: Static text display
- **UILabel**: Text with alignment options (left, center, right, top, middle, bottom)

#### Navigation Components
- **UIMenu**: Menu bar container with horizontal layout
- **UIMenuItem**: Menu item with dropdown support
- **UITabView**: Tab-based navigation with content switching

## Quick Start

### Building the Project

```bash
./gradlew build
```

### Running the Example

```bash
./gradlew run
```

Or using the distribution:

```bash
./gradlew installDist
./build/install/jEmbedUI/bin/jEmbedUI
```

## Usage Examples

### Creating a Simple UI

```java
import com.jembedui.core.*;
import com.jembedui.components.buttons.*;
import com.jembedui.style.Color;

// Create UI context
UIContext uiContext = new UIContext(800, 600, 1.0f);
UIContainer root = uiContext.getRoot();

// Create a button
UIButton button = new UIButton("Click Me");
button.setBounds(100, 100, 120, 40);
button.setOnClick(() -> System.out.println("Button clicked!"));
root.addChild(button);

// Render loop
while (running) {
    uiContext.update(deltaTime);
    uiContext.render();
}
```

### Using Layouts

```java
import com.jembedui.components.UIPanel;
import com.jembedui.layout.*;

// Create panel with vertical layout
UIPanel panel = new UIPanel();
panel.setLayoutManager(new VerticalLayout(10)); // 10px spacing
panel.setBounds(50, 50, 300, 400);

// Add buttons - they'll be arranged vertically
panel.addChild(new UIButton("Button 1"));
panel.addChild(new UIButton("Button 2"));
panel.addChild(new UIButton("Button 3"));

panel.layout(); // Apply layout
```

### Event Handling

```java
import com.jembedui.events.*;

button.addEventListener(MouseEvent.class, event -> {
    switch (event.getEventType()) {
        case MOUSE_ENTER -> System.out.println("Mouse entered");
        case MOUSE_LEAVE -> System.out.println("Mouse left");
        case MOUSE_CLICK -> System.out.println("Clicked!");
    }
});
```

### Custom Styling

```java
import com.jembedui.style.*;

Style style = button.getStyle();
style.setBackgroundColor(new Color(0.2f, 0.6f, 0.8f));
style.setForegroundColor(Color.WHITE);
style.setBorderWidth(2);
style.setBorderColor(new Color(0.1f, 0.4f, 0.6f));
style.setBorderRadius(8);
style.setPadding(10);
style.setFontSize(16);
```

### Creating Menus

```java
import com.jembedui.components.navigation.*;

UIMenu menuBar = new UIMenu();
menuBar.setBounds(0, 0, windowWidth, 30);

UIMenuItem fileMenu = new UIMenuItem("File");
fileMenu.addDropdownItem("New", () -> createNew());
fileMenu.addDropdownItem("Open", () -> open());
fileMenu.addDropdownItem("Save", () -> save());
menuBar.addChild(fileMenu);

root.addChild(menuBar);
```

### Tab View

```java
UITabView tabView = new UITabView();
tabView.setBounds(0, 30, windowWidth, windowHeight - 30);

// Create tab content
UIPanel tab1Content = new UIPanel();
// ... add components to tab1Content

UIPanel tab2Content = new UIPanel();
// ... add components to tab2Content

tabView.addTab("Tab 1", tab1Content);
tabView.addTab("Tab 2", tab2Content);

root.addChild(tabView);
```

## Architecture

### Component Hierarchy

```
UIBaseElement (abstract)
├── UIContainer
│   ├── UIPanel
│   ├── UIScrollContainer
│   └── UITabView
├── UIButton
│   ├── UIToggleButton
│   ├── UICheckbox
│   └── UIRadioButton
├── UISlider
├── UIProgressBar
├── UITextField
└── UIText
    └── UILabel
```

### Event System

Events follow the W3C DOM event flow:

1. **Capture Phase**: Event travels from root to target
2. **Target Phase**: Event reaches the target element
3. **Bubble Phase**: Event travels from target back to root

You can stop propagation at any phase using `event.stopPropagation()`.

### Layout System

Three built-in layout managers:

- **HorizontalLayout**: Arranges children left to right
- **VerticalLayout**: Arranges children top to bottom
- **GridLayout**: Arranges children in a grid pattern

Custom layouts can be created by implementing the `LayoutManager` interface.

### Rendering Pipeline

1. **Update**: Process animations and state changes
2. **Layout**: Calculate positions and sizes
3. **Render**: Draw using NanoVG
   - Background and borders
   - Component-specific rendering
   - Children rendering (recursive)

## Platform Support

- **Linux x64**: Full support with GLFW
- **Linux ARM64 (Raspberry Pi)**: Full support with GLFW
- **OpenGL 3.3+**: Core profile
- **OpenGL ES 2.0+**: For embedded systems

## Dependencies

- **LWJGL 3.3.4**: Java bindings for native libraries
- **NanoVG**: Vector graphics rendering
- **GLFW**: Window and input management
- **STB**: Image loading and font rendering

## Performance Considerations

- **Dirty Rectangles**: Only re-render changed components
- **Scissoring**: Clip rendering to visible areas
- **Hardware Acceleration**: All rendering uses GPU
- **Event Optimization**: Events only dispatched to affected elements

## Thread Safety

The UI system is **not thread-safe**. All UI operations must be performed on the main thread. For multi-threaded applications:

```java
// Use a command queue pattern
Queue<Runnable> uiCommands = new ConcurrentLinkedQueue<>();

// From worker thread
uiCommands.offer(() -> button.setText("Updated"));

// In main render loop
while (!uiCommands.isEmpty()) {
    uiCommands.poll().run();
}
```

## Extending the System

### Creating Custom Components

```java
public class MyCustomComponent extends UIBaseElement {
    
    @Override
    public void render(NVGRenderer renderer) {
        if (!visible) return;
        
        // Custom rendering logic
        renderer.drawRect(getAbsoluteX(), getAbsoluteY(), 
                         width, height, style.getBackgroundColor(), 0);
        
        markClean();
    }
    
    @Override
    public void update(float deltaTime) {
        // Custom update logic
    }
}
```

### Creating Custom Layouts

```java
public class MyLayout implements LayoutManager {
    
    @Override
    public void layout(UIContainer container) {
        // Custom layout logic
        for (UIBaseElement child : container.getChildren()) {
            // Position child
            child.setBounds(x, y, width, height);
        }
    }
}
```

## Examples

The `ComprehensiveExample` class demonstrates:
- Menu bar with dropdowns
- Tab-based navigation
- All button types (Button, Toggle, Checkbox, Radio)
- Sliders (horizontal and vertical)
- Progress bars
- Text input fields
- Various layouts
- Event handling

Run it with:
```bash
./gradlew run
```

## License

MIT License - See LICENSE file for details

## Contributing

Contributions are welcome! Areas for improvement:
- Additional components (ComboBox, ListBox, TreeView)
- More layout managers (FlexLayout, AbsoluteLayout)
- Animation system
- Theme support
- Image components
- Rich text rendering
- Accessibility features

## Future Roadmap

- [ ] ComboBox/Dropdown selector
- [ ] ListBox with scrolling
- [ ] Rotary knob/dial control
- [ ] Value knob (A4/Elektron style)
- [ ] Image component with scaling
- [ ] Nine-patch image support
- [ ] Icon component with sprite sheets
- [ ] Dialog/Modal popup
- [ ] Tooltip system
- [ ] Animation framework
- [ ] Theme system
- [ ] Font management improvements
- [ ] Touch gesture support
- [ ] Accessibility (screen reader support)
- [ ] Performance profiling tools

## Troubleshooting

### Window doesn't appear
- Ensure graphics drivers are up to date
- Check OpenGL version compatibility
- Try running with `-Dorg.lwjgl.util.Debug=true`

### Rendering issues
- Verify NanoVG is properly initialized
- Check console for OpenGL errors
- Ensure window has valid OpenGL context

### Input not working
- Verify GLFW callbacks are properly set up
- Check event propagation isn't stopped prematurely
- Ensure elements are visible and enabled

## Contact

For issues, questions, or contributions, please visit the GitHub repository.
