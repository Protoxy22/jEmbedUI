package com.jembedui.style;

/**
 * Style properties for UI elements.
 */
public class Style {
    // Colors
    private Color backgroundColor = Color.TRANSPARENT;
    private Color foregroundColor = Color.WHITE;
    private Color borderColor = Color.GRAY;
    
    // Dimensions
    private float borderWidth = 0;
    private float borderRadius = 0;
    
    // Spacing
    private float paddingTop = 0;
    private float paddingRight = 0;
    private float paddingBottom = 0;
    private float paddingLeft = 0;
    
    private float marginTop = 0;
    private float marginRight = 0;
    private float marginBottom = 0;
    private float marginLeft = 0;
    
    // Font
    private String fontFamily = "sans-serif";
    private float fontSize = 14;
    private boolean fontBold = false;
    private boolean fontItalic = false;
    
    // Opacity
    private float opacity = 1.0f;
    
    // Getters and setters
    public Color getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(Color backgroundColor) { this.backgroundColor = backgroundColor; }
    
    public Color getForegroundColor() { return foregroundColor; }
    public void setForegroundColor(Color foregroundColor) { this.foregroundColor = foregroundColor; }
    
    public Color getBorderColor() { return borderColor; }
    public void setBorderColor(Color borderColor) { this.borderColor = borderColor; }
    
    public float getBorderWidth() { return borderWidth; }
    public void setBorderWidth(float borderWidth) { this.borderWidth = borderWidth; }
    
    public float getBorderRadius() { return borderRadius; }
    public void setBorderRadius(float borderRadius) { this.borderRadius = borderRadius; }
    
    public float getPaddingTop() { return paddingTop; }
    public void setPaddingTop(float paddingTop) { this.paddingTop = paddingTop; }
    
    public float getPaddingRight() { return paddingRight; }
    public void setPaddingRight(float paddingRight) { this.paddingRight = paddingRight; }
    
    public float getPaddingBottom() { return paddingBottom; }
    public void setPaddingBottom(float paddingBottom) { this.paddingBottom = paddingBottom; }
    
    public float getPaddingLeft() { return paddingLeft; }
    public void setPaddingLeft(float paddingLeft) { this.paddingLeft = paddingLeft; }
    
    public void setPadding(float padding) {
        this.paddingTop = this.paddingRight = this.paddingBottom = this.paddingLeft = padding;
    }
    
    public float getMarginTop() { return marginTop; }
    public void setMarginTop(float marginTop) { this.marginTop = marginTop; }
    
    public float getMarginRight() { return marginRight; }
    public void setMarginRight(float marginRight) { this.marginRight = marginRight; }
    
    public float getMarginBottom() { return marginBottom; }
    public void setMarginBottom(float marginBottom) { this.marginBottom = marginBottom; }
    
    public float getMarginLeft() { return marginLeft; }
    public void setMarginLeft(float marginLeft) { this.marginLeft = marginLeft; }
    
    public void setMargin(float margin) {
        this.marginTop = this.marginRight = this.marginBottom = this.marginLeft = margin;
    }
    
    public String getFontFamily() { return fontFamily; }
    public void setFontFamily(String fontFamily) { this.fontFamily = fontFamily; }
    
    public float getFontSize() { return fontSize; }
    public void setFontSize(float fontSize) { this.fontSize = fontSize; }
    
    public boolean isFontBold() { return fontBold; }
    public void setFontBold(boolean fontBold) { this.fontBold = fontBold; }
    
    public boolean isFontItalic() { return fontItalic; }
    public void setFontItalic(boolean fontItalic) { this.fontItalic = fontItalic; }
    
    public float getOpacity() { return opacity; }
    public void setOpacity(float opacity) { this.opacity = Math.max(0.0f, Math.min(1.0f, opacity)); }
    
    public Style copy() {
        Style copy = new Style();
        copy.backgroundColor = this.backgroundColor;
        copy.foregroundColor = this.foregroundColor;
        copy.borderColor = this.borderColor;
        copy.borderWidth = this.borderWidth;
        copy.borderRadius = this.borderRadius;
        copy.paddingTop = this.paddingTop;
        copy.paddingRight = this.paddingRight;
        copy.paddingBottom = this.paddingBottom;
        copy.paddingLeft = this.paddingLeft;
        copy.marginTop = this.marginTop;
        copy.marginRight = this.marginRight;
        copy.marginBottom = this.marginBottom;
        copy.marginLeft = this.marginLeft;
        copy.fontFamily = this.fontFamily;
        copy.fontSize = this.fontSize;
        copy.fontBold = this.fontBold;
        copy.fontItalic = this.fontItalic;
        copy.opacity = this.opacity;
        return copy;
    }
}
