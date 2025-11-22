# jEmbedUI

A lightweight Java UI library designed to run seamlessly on both desktop environments and embedded systems like Raspberry Pi.

## Goal

jEmbedUI aims to provide a simple, cross-platform UI framework that works consistently across different environments:

- **Desktop**: Runs on X11-based Linux systems using standard video drivers
- **Embedded**: Runs directly on Raspberry Pi using KMS/DRM mode (no X11 required)

The library automatically detects the platform architecture and configures the appropriate video backend, making it easy to develop UI applications that work on both powerful desktops and resource-constrained embedded devices.

## Features

- Automatic platform detection (ARM vs x86/x64)
- OpenGL ES 2.0 rendering for broad hardware compatibility
- SDL3-based window management (compatible with KMS/DRM context)

## Technology Stack

- **LWJGL** - Java bindings for native libraries
- **SDL3** - Cross-platform window and input management
- **OpenGL ES 2.0** - Graphics rendering

## Use Cases

- Raspberry Pi user interfaces
- Lightweight desktop applications

## Getting Started

The library is built with Gradle. Simply compile and run - the appropriate video driver will be selected automatically based on your system architecture.

## License

MIT License. See LICENSE file for details.
