package oriedita.editor.action.selector.drawing;

import oriedita.editor.canvas.LineStyle;

/**
 * Data class to contain all settings related to rendering
 */
public class DrawingSettings {
    private float lineWidth;
    private LineStyle lineStyle;
    private int height;
    private int width;
    private boolean gridInputAssist;

    public boolean displayGridInputAssist() {
        return gridInputAssist;
    }

    public void setGridInputAssist(boolean gridInputAssist) {
        this.gridInputAssist = gridInputAssist;
    }

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

    public DrawingSettings(float lineWidth, LineStyle lineStyle, int height, int width, boolean gridInputAssist) {
        this.lineWidth = lineWidth;
        this.lineStyle = lineStyle;
        this.width = width;
        this.height = height;
        this.gridInputAssist = gridInputAssist;
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
