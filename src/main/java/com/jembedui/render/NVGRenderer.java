package com.jembedui.render;

import com.jembedui.style.Color;
import org.lwjgl.nanovg.*;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * NanoVG-based renderer for UI elements.
 */
public class NVGRenderer {
    
    private long nvgContext;
    private int defaultFont = -1;
    
    public NVGRenderer() {
        // Create NanoVG context with OpenGL3 backend
        nvgContext = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);
        if (nvgContext == NULL) {
            throw new RuntimeException("Could not initialize NanoVG");
        }
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
        if (nvgContext != NULL) {
            nvgDelete(nvgContext);
            nvgContext = NULL;
        }
    }
    
    // Font management
    public int loadFont(String name, String path) {
        int font = nvgCreateFont(nvgContext, name, path);
        if (font == -1) {
            System.err.println("Failed to load font: " + path);
        }
        if (defaultFont == -1) {
            defaultFont = font;
        }
        return font;
    }
    
    public int loadFontFromMemory(String name, ByteBuffer data) {
        int font = nvgCreateFontMem(nvgContext, name, data, false);
        if (font == -1) {
            System.err.println("Failed to load font from memory: " + name);
        }
        if (defaultFont == -1) {
            defaultFont = font;
        }
        return font;
    }
    
    // Drawing primitives
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
    
    // Text rendering
    public void drawText(float x, float y, String text, String fontFamily, float fontSize, Color color, int align) {
        nvgFontSize(nvgContext, fontSize);
        if (defaultFont != -1) {
            nvgFontFaceId(nvgContext, defaultFont);
        }
        nvgTextAlign(nvgContext, align);
        nvgFillColor(nvgContext, nvgRGBAf(color.r(), color.g(), color.b(), color.a()));
        nvgText(nvgContext, x, y, text);
    }
    
    public void drawText(float x, float y, String text, float fontSize, Color color) {
        drawText(x, y, text, "sans-serif", fontSize, color, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
    }
    
    public float[] measureText(String text, float fontSize) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            float[] bounds = new float[4];
            nvgFontSize(nvgContext, fontSize);
            if (defaultFont != -1) {
                nvgFontFaceId(nvgContext, defaultFont);
            }
            nvgTextBounds(nvgContext, 0, 0, text, bounds);
            return new float[] { bounds[2] - bounds[0], bounds[3] - bounds[1] };
        }
    }
    
    // Scissoring
    public void setScissor(float x, float y, float width, float height) {
        nvgScissor(nvgContext, x, y, width, height);
    }
    
    public void resetScissor() {
        nvgResetScissor(nvgContext);
    }
    
    // Transform
    public void save() {
        nvgSave(nvgContext);
    }
    
    public void restore() {
        nvgRestore(nvgContext);
    }
    
    // Helper to convert NanoVG color
    private NVGColor nvgRGBAf(float r, float g, float b, float a) {
        NVGColor color = NVGColor.create();
        color.r(r);
        color.g(g);
        color.b(b);
        color.a(a);
        return color;
    }
}
