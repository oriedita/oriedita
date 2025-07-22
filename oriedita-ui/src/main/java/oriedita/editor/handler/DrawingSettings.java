package oriedita.editor.handler;

import oriedita.editor.canvas.LineStyle;

public class DrawingSettings {
    private final float lineWidth;
    private final LineStyle lineStyle;
    private final int height;
    private final int width;
    private final boolean roundedEnds;
    private final boolean showComments;
    private final boolean showCurrentStep;

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public DrawingSettings(float lineWidth,
                           LineStyle lineStyle,
                           int height,
                           int width,
                           boolean roundedEnds,
                           boolean showComments,
                           boolean showCurrentStep) {
        this.lineWidth = lineWidth;
        this.lineStyle = lineStyle;
        this.width = width;
        this.height = height;
        this.roundedEnds = roundedEnds;
        this.showComments = showComments;
        this.showCurrentStep = showCurrentStep;
    }

    public LineStyle getLineStyle() {
        return lineStyle;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public boolean useRoundedEnds() {
        return roundedEnds;
    }

    public boolean getShowComments() {
        return showComments;
    }

    public boolean getShowCurrentStep() {
        return showCurrentStep;
    }
}
