package oriedita.editor.handler;

import oriedita.editor.canvas.LineStyle;

public class DrawingSettings {
    private final float lineWidth;
    private final float auxLineWidth;
    private final int pointSize;
    private final LineStyle lineStyle;
    private final int height;
    private final int width;
    private final boolean roundedEnds;
    private final boolean showComments;
    private final boolean showCurrentStep;
    private boolean gridInputAssist;

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public DrawingSettings(float lineWidth,
                           float auxLineWidth,
                           int pointSize,
                           LineStyle lineStyle,
                           int height,
                           int width,
                           boolean roundedEnds,
                           boolean showComments,
                           boolean showCurrentStep,
                           boolean gridInputAssist) {
        this.lineWidth = lineWidth;
        this.auxLineWidth = auxLineWidth;
        this.pointSize = pointSize;
        this.lineStyle = lineStyle;
        this.width = width;
        this.height = height;
        this.roundedEnds = roundedEnds;
        this.showComments = showComments;
        this.showCurrentStep = showCurrentStep;
        this.gridInputAssist = gridInputAssist;
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

    public boolean getGridInputAssist() {
        return gridInputAssist;
    }

    public void setGridInputAssist(boolean gridInputAssist) {
        this.gridInputAssist = gridInputAssist;
    }

    public float getAuxLineWidth() {
        return auxLineWidth;
    }

    public int getPointSize() {
        return pointSize;
    }
}
