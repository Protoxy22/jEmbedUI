package com.jembedui.style;

/**
 * Represents an RGBA color.
 */
public class Color {
    private final float r;
    private final float g;
    private final float b;
    private final float a;
    
    public Color(float r, float g, float b, float a) {
        this.r = clamp(r);
        this.g = clamp(g);
        this.b = clamp(b);
        this.a = clamp(a);
    }
    
    public Color(float r, float g, float b) {
        this(r, g, b, 1.0f);
    }
    
    public Color(int rgba) {
        this.r = ((rgba >> 24) & 0xFF) / 255.0f;
        this.g = ((rgba >> 16) & 0xFF) / 255.0f;
        this.b = ((rgba >> 8) & 0xFF) / 255.0f;
        this.a = (rgba & 0xFF) / 255.0f;
    }
    
    private float clamp(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }
    
    public float r() { return r; }
    public float g() { return g; }
    public float b() { return b; }
    public float a() { return a; }
    
    // Common colors
    public static final Color WHITE = new Color(1, 1, 1);
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color RED = new Color(1, 0, 0);
    public static final Color GREEN = new Color(0, 1, 0);
    public static final Color BLUE = new Color(0, 0, 1);
    public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
    public static final Color GRAY = new Color(0.5f, 0.5f, 0.5f);
    public static final Color LIGHT_GRAY = new Color(0.75f, 0.75f, 0.75f);
    public static final Color DARK_GRAY = new Color(0.25f, 0.25f, 0.25f);
    public static final Color YELLOW = new Color(1, 1, 0);
    public static final Color CYAN = new Color(0, 1, 1);
    public static final Color MAGENTA = new Color(1, 0, 1);
    
    public Color withAlpha(float alpha) {
        return new Color(r, g, b, alpha);
    }
    
    public Color interpolate(Color other, float t) {
        return new Color(
            r + (other.r - r) * t,
            g + (other.g - g) * t,
            b + (other.b - b) * t,
            a + (other.a - a) * t
        );
    }
}
