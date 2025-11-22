package net.jembedui;

import org.lwjgl.opengles.GLES;
import org.lwjgl.sdl.SDL_Event;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengles.GLES20.*;
import static org.lwjgl.sdl.SDLError.SDL_GetError;
import static org.lwjgl.sdl.SDLEvents.*;
import static org.lwjgl.sdl.SDLHints.SDL_HINT_VIDEO_DRIVER;
import static org.lwjgl.sdl.SDLHints.SDL_SetHint;
import static org.lwjgl.sdl.SDLInit.*;
import static org.lwjgl.sdl.SDLVideo.*;
import static org.lwjgl.sdl.SDLTimer.SDL_Delay;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {

    // Vertex shader with position and color attributes
    private static final String VERTEX_SHADER = """
            #version 100
            attribute vec2 aPosition;
            attribute vec3 aColor;
            varying vec3 vColor;
            void main() {
                gl_Position = vec4(aPosition, 0.0, 1.0);
                vColor = aColor;
            }
            """;

    // Fragment shader that interpolates colors
    private static final String FRAGMENT_SHADER = """
            #version 100
            precision mediump float;
            varying vec3 vColor;
            void main() {
                gl_FragColor = vec4(vColor, 1.0);
            }
            """;

    private static int createShader(int type, String source) {
        int shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer status = stack.mallocInt(1);
            glGetShaderiv(shader, GL_COMPILE_STATUS, status);
            if (status.get(0) == GL_FALSE) {
                String log = glGetShaderInfoLog(shader);
                throw new RuntimeException("Shader compilation failed: " + log);
            }
        }
        return shader;
    }

    private static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = createShader(GL_VERTEX_SHADER, vertexSource);
        int fragmentShader = createShader(GL_FRAGMENT_SHADER, fragmentSource);

        int program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer status = stack.mallocInt(1);
            glGetProgramiv(program, GL_LINK_STATUS, status);
            if (status.get(0) == GL_FALSE) {
                String log = glGetProgramInfoLog(program);
                throw new RuntimeException("Program linking failed: " + log);
            }
        }

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        return program;
    }

    public static void main(String[] args) {
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

        // ask for GLES2
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_PROFILE_MASK, SDL_GL_CONTEXT_PROFILE_ES);
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_MAJOR_VERSION, 2);
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_MINOR_VERSION, 0);
        SDL_GL_SetAttribute(SDL_GL_DOUBLEBUFFER, 1);

        int width = 800;
        int height = 480;

        long window = SDL_CreateWindow(
                "LWJGL SDL KMSDRM",
                width,
                height,
                SDL_WINDOW_OPENGL // you can also OR SDL_WINDOW_RESIZABLE here
        );
        if (window == NULL) {
            SDL_Quit();
            throw new IllegalStateException("SDL_CreateWindow failed: " + SDL_GetError());
        }

        // SDL3 + Wayland: explicitly show it
        SDL_ShowWindow(window);

        long glContext = SDL_GL_CreateContext(window);
        if (glContext == NULL) {
            SDL_DestroyWindow(window);
            SDL_Quit();
            throw new IllegalStateException("SDL_GL_CreateContext failed: " + SDL_GetError());
        }

        // IMPORTANT on Wayland: make it current
        if (!SDL_GL_MakeCurrent(window, glContext)) {
            String err = SDL_GetError();
            SDL_GL_DestroyContext(glContext);
            SDL_DestroyWindow(window);
            SDL_Quit();
            throw new IllegalStateException("SDL_GL_MakeCurrent failed: " + err);
        }

        // optional but helps: vsync
        SDL_GL_SetSwapInterval(1);

        // tell LWJGL to load GLES symbols
        GLES.createCapabilities();

        System.out.println("Video driver: " + SDL_GetCurrentVideoDriver());
        System.out.println("GL Version: " + glGetString(GL_VERSION));
        System.out.println("GL Vendor: " + glGetString(GL_VENDOR));
        System.out.println("GL Renderer: " + glGetString(GL_RENDERER));

        // Create shader program
        int program = createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        int aPosition = glGetAttribLocation(program, "aPosition");
        int aColor = glGetAttribLocation(program, "aColor");

        System.out.println("aPosition location: " + aPosition);
        System.out.println("aColor location: " + aColor);

        // Triangle vertices: position (x, y) + color (r, g, b)
        // Top vertex (red), Bottom-left (green), Bottom-right (blue)
        float[] triangleData = {
                // x     y      r    g    b
                0.0f,  0.6f,  1.0f, 0.0f, 0.0f,  // Top vertex - Red
                -0.6f, -0.6f,  0.0f, 1.0f, 0.0f,  // Bottom-left - Green
                0.6f, -0.6f,  0.0f, 0.0f, 1.0f   // Bottom-right - Blue
        };

        int vbo;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer buffers = stack.mallocInt(1);
            glGenBuffers(buffers);
            vbo = buffers.get(0);

            FloatBuffer vertexBuffer = stack.mallocFloat(triangleData.length);
            vertexBuffer.put(triangleData).flip();

            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        }

        System.out.println("VBO created: " + vbo);

        boolean running = true;
        SDL_Event event = SDL_Event.calloc();

        while (running) {
            while (SDL_PollEvent(event)) {
                if (event.type() == SDL_EVENT_QUIT) {
                    running = false;
                }
            }

            // Dark background
            glViewport(0, 0, width, height);
            glClearColor(0.1f, 0.1f, 0.15f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            // Use our shader program
            glUseProgram(program);

            // Bind the VBO
            glBindBuffer(GL_ARRAY_BUFFER, vbo);

            // Enable and configure position attribute (2 floats, stride 5 floats, offset 0)
            glEnableVertexAttribArray(aPosition);
            glVertexAttribPointer(aPosition, 2, GL_FLOAT, false, 5 * 4, 0);

            // Enable and configure color attribute (3 floats, stride 5 floats, offset 2 floats)
            glEnableVertexAttribArray(aColor);
            glVertexAttribPointer(aColor, 3, GL_FLOAT, false, 5 * 4, 2 * 4);

            // Draw the triangle
            glDrawArrays(GL_TRIANGLES, 0, 3);

            // Cleanup
            glDisableVertexAttribArray(aPosition);
            glDisableVertexAttribArray(aColor);

            SDL_GL_SwapWindow(window);
            SDL_Delay(16);
        }

        glDeleteBuffers(vbo);
        glDeleteProgram(program);
        event.free();
        SDL_GL_DestroyContext(glContext);
        SDL_DestroyWindow(window);
        SDL_Quit();
    }
}