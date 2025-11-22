package com.jembedui.render;

import com.jembedui.style.Color;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGLES2.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * NanoVG-based renderer for UI elements.
 */
public class NVGRenderer {

    private long nvgContext;
    private int defaultFont = -1;

    // Keep reference so the direct buffer is not GC'd
    private ByteBuffer defaultFontBuffer;

    public NVGRenderer() {
        // GLES2 backend (since you import NanoVGGLES2)
        nvgContext = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES | NVG_DEBUG);
        if (nvgContext == NULL) {
            throw new RuntimeException("Could not initialize NanoVG");
        }

        loadDefaultFont();
    }

    private void loadDefaultFont() {
        // Try to load Bender from resources
        try (InputStream inputStream = getClass().getResourceAsStream("/Bender.ttf")) {
            if (inputStream == null) {
                System.err.println("Bender.ttf not found on classpath (expected /Bender.ttf)");
                warnNoFont();
                return;
            }

            byte[] fontData = inputStream.readAllBytes();
            System.out.println("Bender.ttf loaded from resources, size = " + fontData.length + " bytes");

            if (fontData.length < 4) {
                System.err.println("Bender.ttf too small to be a valid TTF.");
                warnNoFont();
                return;
            }

            // --- Check TrueType signature (0x00010000 or 'ttcf') ---
            int sig = ((fontData[0] & 0xFF) << 24)
                    | ((fontData[1] & 0xFF) << 16)
                    | ((fontData[2] & 0xFF) << 8)
                    | (fontData[3] & 0xFF);

            boolean looksTTF = (sig == 0x00010000) || (sig == 0x74746366); // 0x74746366 == 'ttcf'

            if (!looksTTF) {
                System.err.printf(
                        "Bender.ttf does not look like a TrueType font (signature=0x%08X).%n", sig
                );
                System.err.println("You probably have an OTF/CFF font; NanoVG/stb_truetype can't load it.");
                warnNoFont();
                return;
            }

            // --- Wrap in direct buffer ---
            defaultFontBuffer = BufferUtils.createByteBuffer(fontData.length);
            defaultFontBuffer.put(fontData).flip();

            // freeData = false -> Java/GC owns the buffer (we keep reference in defaultFontBuffer)
            defaultFont = nvgCreateFontMem(nvgContext, "default", defaultFontBuffer, false);
            if (defaultFont == -1) {
                System.err.println("nvgCreateFontMem failed for Bender.ttf even though it looks like TTF.");
                warnNoFont();
                defaultFontBuffer = null; // allow GC
                return;
            }

            System.out.println("Loaded default font from resources: Bender.ttf (handle = " + defaultFont + ")");

        } catch (IOException e) {
            System.err.println("Failed to load Bender.ttf from resources: " + e.getMessage());
            warnNoFont();
        }
    }


    private void warnNoFont() {
        System.err.println("WARNING: No default font could be loaded. Text will not render!");
        System.err.println("Ensure src/main/resources/Bender.ttf is a valid TrueType font.");
    }

    public long getContext() {
        return nvgContext;
    }

    public void beginFrame(float windowWidth, float windowHeight, float pixelRatio) {
        nvgBeginFrame(nvgContext, windowWidth, windowHeight, pixelRatio);
    }

    public void endFrame() {
        nvgEndFrame(nvgContext);
    }

    public void cleanup() {
        // We do NOT manually free defaultFontBuffer: it's owned by Java/GC
        defaultFontBuffer = null;

        if (nvgContext != NULL) {
            nvgDelete(nvgContext);
            nvgContext = NULL;
        }
    }

    // -------------------------------------------------------------------------
    // Font management
    // -------------------------------------------------------------------------

    public int loadFont(String name, String path) {
        int font = nvgCreateFont(nvgContext, name, path);
        if (font == -1) {
            System.err.println("Failed to load font: " + path);
        }
        if (defaultFont == -1 && font != -1) {
            defaultFont = font;
        }
        return font;
    }

    public int loadFontFromMemory(String name, ByteBuffer data) {
        int font = nvgCreateFontMem(nvgContext, name, data, false);
        if (font == -1) {
            System.err.println("Failed to load font from memory: " + name);
        }
        if (defaultFont == -1 && font != -1) {
            defaultFont = font;
        }
        return font;
    }

    // -------------------------------------------------------------------------
    // Drawing primitives
    // -------------------------------------------------------------------------

    public void drawRect(float x, float y, float width, float height, Color color, float cornerRadius) {
        nvgBeginPath(nvgContext);
        if (cornerRadius > 0) {
            nvgRoundedRect(nvgContext, x, y, width, height, cornerRadius);
        } else {
            nvgRect(nvgContext, x, y, width, height);
        }
        nvgFillColor(nvgContext, nvgRGBAf(color.r(), color.g(), color.b(), color.a()));
        nvgFill(nvgContext);
    }

    public void drawRectOutline(float x, float y, float width, float height,
                                float strokeWidth, Color color, float cornerRadius) {
        nvgBeginPath(nvgContext);
        if (cornerRadius > 0) {
            nvgRoundedRect(nvgContext, x, y, width, height, cornerRadius);
        } else {
            nvgRect(nvgContext, x, y, width, height);
        }
        nvgStrokeWidth(nvgContext, strokeWidth);
        nvgStrokeColor(nvgContext, nvgRGBAf(color.r(), color.g(), color.b(), color.a()));
        nvgStroke(nvgContext);
    }

    public void drawCircle(float cx, float cy, float radius, Color color) {
        nvgBeginPath(nvgContext);
        nvgCircle(nvgContext, cx, cy, radius);
        nvgFillColor(nvgContext, nvgRGBAf(color.r(), color.g(), color.b(), color.a()));
        nvgFill(nvgContext);
    }

    public void drawCircleOutline(float cx, float cy, float radius, float strokeWidth, Color color) {
        nvgBeginPath(nvgContext);
        nvgCircle(nvgContext, cx, cy, radius);
        nvgStrokeWidth(nvgContext, strokeWidth);
        nvgStrokeColor(nvgContext, nvgRGBAf(color.r(), color.g(), color.b(), color.a()));
        nvgStroke(nvgContext);
    }

    public void drawLine(float x1, float y1, float x2, float y2, float strokeWidth, Color color) {
        nvgBeginPath(nvgContext);
        nvgMoveTo(nvgContext, x1, y1);
        nvgLineTo(nvgContext, x2, y2);
        nvgStrokeWidth(nvgContext, strokeWidth);
        nvgStrokeColor(nvgContext, nvgRGBAf(color.r(), color.g(), color.b(), color.a()));
        nvgStroke(nvgContext);
    }

    // -------------------------------------------------------------------------
    // Text rendering
    // -------------------------------------------------------------------------

    public void drawText(float x, float y, String text,
                         String fontFamily, float fontSize, Color color, int align) {
        nvgFontSize(nvgContext, fontSize);

        if (defaultFont != -1) {
            nvgFontFaceId(nvgContext, defaultFont);
        } else if (fontFamily != null) {
            nvgFontFace(nvgContext, fontFamily);
        }

        nvgTextAlign(nvgContext, align);
        nvgFillColor(nvgContext, nvgRGBAf(color.r(), color.g(), color.b(), color.a()));
        nvgText(nvgContext, x, y, text);
    }

    public void drawText(float x, float y, String text, float fontSize, Color color) {
        drawText(x, y, text, "sans", fontSize, color, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
    }

    public float[] measureText(String text, float fontSize) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer bounds = stack.mallocFloat(4);

            nvgFontSize(nvgContext, fontSize);
            if (defaultFont != -1) {
                nvgFontFaceId(nvgContext, defaultFont);
            }

            nvgTextBounds(nvgContext, 0.0f, 0.0f, text, bounds);

            float x0 = bounds.get(0);
            float y0 = bounds.get(1);
            float x1 = bounds.get(2);
            float y1 = bounds.get(3);

            return new float[]{x1 - x0, y1 - y0};
        }
    }

    // -------------------------------------------------------------------------
    // Scissoring
    // -------------------------------------------------------------------------

    public void setScissor(float x, float y, float width, float height) {
        nvgScissor(nvgContext, x, y, width, height);
    }

    public void resetScissor() {
        nvgResetScissor(nvgContext);
    }

    // -------------------------------------------------------------------------
    // Transform
    // -------------------------------------------------------------------------

    public void save() {
        nvgSave(nvgContext);
    }

    public void restore() {
        nvgRestore(nvgContext);
    }

    // -------------------------------------------------------------------------
    // Helper to convert NanoVG color
    // -------------------------------------------------------------------------

    private NVGColor nvgRGBAf(float r, float g, float b, float a) {
        NVGColor color = NVGColor.create();
        color.r(r);
        color.g(g);
        color.b(b);
        color.a(a);
        return color;
    }
}
