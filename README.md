# jEmbedUI

A complete, modular Java UI library with NanoVG rendering, designed to run seamlessly on both desktop environments and embedded systems like Raspberry Pi.

## Features

### Comprehensive UI System
- **50+ UI Components**: Buttons, sliders, text fields, menus, tabs, and more
- **Flexible Layouts**: Horizontal, Vertical, and Grid layout managers
- **Event System**: Full W3C-style event propagation (capture → target → bubble)
- **Rich Styling**: Colors, fonts, borders, padding, margins, and border radius
- **NanoVG Rendering**: Hardware-accelerated vector graphics
- **OpenGL ES Compatible**: Runs on embedded systems

### Platform Support
jEmbedUI works consistently across different environments:
- **Desktop**: Runs on standard Linux systems with OpenGL 3.3+
- **Embedded**: Runs on Raspberry Pi with OpenGL ES 2.0+
- **Cross-Architecture**: Automatic platform detection (ARM vs x86/x64)

## Components

### Interactive Components
- **Buttons**: UIButton, UIToggleButton, UICheckbox, UIRadioButton
- **Sliders**: UISlider (horizontal/vertical), UIProgressBar
- **Input**: UITextField with cursor and placeholder support

### Display Components
- **Text**: UIText, UILabel with alignment options
- **Containers**: UIPanel, UIScrollContainer with automatic scrollbars

### Navigation
- **Menus**: UIMenu, UIMenuItem with dropdown support
- **Tabs**: UITabView for multi-panel interfaces

### Layout System
- **HorizontalLayout**: Arrange children left-to-right
- **VerticalLayout**: Arrange children top-to-bottom
- **GridLayout**: Arrange children in a grid

## Quick Start

### Build and Run

```bash
# Build the project
./gradlew build

# Run the comprehensive example
./gradlew run

# Or install and run
./gradlew installDist
./build/install/jEmbedUI/bin/jEmbedUI
```

### Simple Example

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
button.setOnClick(() -> System.out.println("Clicked!"));
root.addChild(button);

// Render loop
while (running) {
    uiContext.update(deltaTime);
    uiContext.render();
}
```

## Architecture

### Core Components
- **UIContext**: Root manager for input dispatch and rendering
- **UIBaseElement**: Base class for all UI elements
- **UIContainer**: Container that manages child elements
- **NVGRenderer**: NanoVG-based rendering engine

### Event System
Events follow W3C DOM event flow:
1. **Capture Phase**: From root to target
2. **Target Phase**: At the target element
3. **Bubble Phase**: From target to root

### Styling
Each component has a `Style` object with:
- Background/foreground colors
- Border width, color, and radius
- Padding and margins
- Font family, size, bold, italic
- Opacity

## Technology Stack

- **LWJGL 3.3.4**: Java bindings for native libraries
- **NanoVG**: Vector graphics rendering
- **GLFW**: Cross-platform window and input management
- **OpenGL 3.3+ / OpenGL ES 2.0+**: Graphics rendering
- **Java 17+**: Modern Java features

## Example Application

The included `ComprehensiveExample` demonstrates:
- Menu bar with File and Edit menus
- Tab-based navigation
- Multiple button types (normal, toggle, checkbox, radio)
- Sliders (horizontal and vertical)
- Progress bars with different colors
- Text input fields
- Layout managers in action
- Event handling examples

![UI Example](docs/screenshot.png) *(Screenshot when running)*

## Documentation

For detailed documentation, see [DOCUMENTATION.md](DOCUMENTATION.md) which covers:
- Complete API reference
- Usage examples for all components
- Creating custom components
- Custom layout managers
- Event handling patterns
- Performance optimization
- Platform-specific notes

## Use Cases

- **Raspberry Pi Interfaces**: Native UI for embedded projects
- **Desktop Applications**: Lightweight cross-platform apps
- **Kiosk Systems**: Full-screen interactive displays
- **Industrial Controls**: Touch-enabled control panels
- **Media Centers**: Custom media player interfaces

## Requirements

- Java 17 or higher
- OpenGL 3.3+ (desktop) or OpenGL ES 2.0+ (embedded)
- Linux (x64 or ARM64)
- GLFW-compatible system

## Building from Source

```bash
git clone https://github.com/Protoxy22/jEmbedUI.git
cd jEmbedUI
./gradlew build
```

## License

MIT License. See [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Areas for improvement:
- Additional components (TreeView, ComboBox, etc.)
- More layout options
- Animation system
- Theme support
- Touch gesture support

## Acknowledgments

- **LWJGL**: Java bindings for native libraries
- **NanoVG**: Excellent vector graphics library
- **GLFW**: Reliable windowing system

## Future Roadmap

See [DOCUMENTATION.md](DOCUMENTATION.md) for the complete roadmap of planned features.
