package oriedita.editor.handler;

import oriedita.editor.canvas.LineStyle;

public class DrawingSettings {
    private float lineWidth;
    private LineStyle lineStyle;
    private int height;
    private int width;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public DrawingSettings(float lineWidth, LineStyle lineStyle, int height, int width) {
        this.lineWidth = lineWidth;
        this.lineStyle = lineStyle;
        this.width = width;
        this.height = height;
    }

    public LineStyle getLineStyle() {
        return lineStyle;
    }

    public void setLineStyle(LineStyle lineStyle) {
        this.lineStyle = lineStyle;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }
}
