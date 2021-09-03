package origami_editor.editor.databinding;

import origami_editor.editor.LineStyle;

public class CanvasModel {
    private boolean displayPointSpotlight;
    private boolean displayPointOffset;
    private boolean displayGridInputAssist;
    private boolean displayComments;
    private boolean displayCpLines;
    private boolean displayAuxLines;
    private boolean displayLiveAuxLines;
    private boolean displayMarkings;
    private boolean displayCreasePatternOnTop;
    private boolean displayFoldingProgress;

    private int lineWidth;
    private int auxLineWidth;
    private int pointSize;
    private LineStyle lineStyle;
    private boolean antiAlias;
    private boolean mouseWheelMovesCreasePattern;

    public CanvasModel() {
        reset();
    }

    public int getAuxLineWidth() {
        return auxLineWidth;
    }

    public void setAuxLineWidth(int auxLineWidth) {
        this.auxLineWidth = auxLineWidth;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public int getPointSize() {
        return pointSize;
    }

    public void setPointSize(int pointSize) {
        this.pointSize = pointSize;
    }

    public LineStyle getLineStyle() {
        return lineStyle;
    }

    public void setLineStyle(LineStyle lineStyle) {
        this.lineStyle = lineStyle;
    }

    public boolean getAntiAlias() {
        return antiAlias;
    }

    public void setAntiAlias(boolean antiAlias) {
        this.antiAlias = antiAlias;
    }

    public boolean getMouseWheelMovesCreasePattern() {
        return mouseWheelMovesCreasePattern;
    }

    public void setMouseWheelMovesCreasePattern(boolean mouseWheelMovesCreasePattern) {
        this.mouseWheelMovesCreasePattern = mouseWheelMovesCreasePattern;
    }

    public void reset() {
        displayComments = true;
        displayCpLines = true;
        displayAuxLines = true;
        displayLiveAuxLines = true;
        displayMarkings = true;
        displayCreasePatternOnTop = false;
        displayFoldingProgress = false;
        mouseWheelMovesCreasePattern = true;
        lineWidth = 1;
        pointSize = 1;
        lineStyle = LineStyle.COLOR;
        antiAlias = false;
        auxLineWidth = 3;
    }

    public boolean getDisplayPointSpotlight() {
        return displayPointSpotlight;
    }

    public void setDisplayPointSpotlight(boolean displayPointSpotlight) {
        this.displayPointSpotlight = displayPointSpotlight;
    }

    public boolean getDisplayPointOffset() {
        return displayPointOffset;
    }

    public void setDisplayPointOffset(boolean displayPointOffset) {
        this.displayPointOffset = displayPointOffset;
    }

    public boolean getDisplayGridInputAssist() {
        return displayGridInputAssist;
    }

    public void setDisplayGridInputAssist(boolean displayGridInputAssist) {
        this.displayGridInputAssist = displayGridInputAssist;
    }

    public boolean getDisplayComments() {
        return displayComments;
    }

    public void setDisplayComments(boolean displayComments) {
        this.displayComments = displayComments;
    }

    public boolean getDisplayCpLines() {
        return displayCpLines;
    }

    public void setDisplayCpLines(boolean displayCpLines) {
        this.displayCpLines = displayCpLines;
    }

    public boolean getDisplayAuxLines() {
        return displayAuxLines;
    }

    public void setDisplayAuxLines(boolean displayAuxLines) {
        this.displayAuxLines = displayAuxLines;
    }

    public boolean getDisplayLiveAuxLines() {
        return displayLiveAuxLines;
    }

    public void setDisplayLiveAuxLines(boolean displayLiveAuxLines) {
        this.displayLiveAuxLines = displayLiveAuxLines;
    }

    public boolean getDisplayMarkings() {
        return displayMarkings;
    }

    public void setDisplayMarkings(boolean displayMarkings) {
        this.displayMarkings = displayMarkings;
    }

    public boolean getDisplayCreasePatternOnTop() {
        return displayCreasePatternOnTop;
    }

    public void setDisplayCreasePatternOnTop(boolean displayCreasePatternOnTop) {
        this.displayCreasePatternOnTop = displayCreasePatternOnTop;
    }

    public boolean getDisplayFoldingProgress() {
        return displayFoldingProgress;
    }

    public void setDisplayFoldingProgress(boolean displayFoldingProgress) {
        this.displayFoldingProgress = displayFoldingProgress;
    }

    public void decreasePointSize() {
        pointSize = pointSize - 1;
        if (pointSize < 0) {
            pointSize = 0;
        }
    }

    public void increasePointSize() {
        pointSize++;
    }

    public void advanceLineStyle() {
        lineStyle = lineStyle.advance();
    }

    public void toggleAntiAlias() {
        antiAlias = !antiAlias;
    }

    public void decreaseLineWidth() {
        lineWidth = lineWidth - 2;
        if (lineWidth < 1) {
            lineWidth = 1;
        }
    }

    public void increaseLineWidth() {
        lineWidth = lineWidth + 2;
    }

    public float getCalculatedLineWidth() {
        float fLineWidth = (float) lineWidth;

        if (antiAlias) {
            fLineWidth += 0.2f;
        }

        return fLineWidth;
    }

    public void decreaseAuxLineWidth() {
        auxLineWidth = auxLineWidth - 2;
        if (auxLineWidth < 3) {
            auxLineWidth = 3;
        }
    }

    public void increaseAuxLineWidth() {
        auxLineWidth = auxLineWidth + 2;
    }

    public float getCalculatedAuxLineWidth() {
        float fAuxLineWidth = (float) auxLineWidth;

        if (antiAlias) {
            fAuxLineWidth += 0.2f;
        }

        return fAuxLineWidth;
    }
}
