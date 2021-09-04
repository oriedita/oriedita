package origami_editor.editor.databinding;

import origami_editor.editor.LineColor;
import origami_editor.editor.LineStyle;
import origami_editor.editor.MouseMode;
import origami_editor.editor.drawing_worker.DrawingWorker;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class CanvasModel {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
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

    private LineColor lineColor;
    private LineColor auxLiveLineColor;
    private MouseMode mouseMode;
    private MouseMode mouseModeAfterColorSelection;

    private DrawingWorker.FoldLineAdditionalInputMode foldLineAdditionalInputMode;
    private DrawingWorker.FoldLineAdditionalInputMode foldLineAdditionalInputMode_old;

    public CanvasModel() {
        reset();
    }

    public void restoreFoldLineAdditionalInputMode() {
        setFoldLineAdditionalInputMode(foldLineAdditionalInputMode_old);
    }

    public DrawingWorker.FoldLineAdditionalInputMode getFoldLineAdditionalInputMode() {
        return foldLineAdditionalInputMode;
    }

    public void setFoldLineAdditionalInputMode(DrawingWorker.FoldLineAdditionalInputMode foldLineAdditionalInputMode) {
        DrawingWorker.FoldLineAdditionalInputMode oldFoldLineAdditionalInputMode = this.foldLineAdditionalInputMode;
        this.foldLineAdditionalInputMode_old = this.foldLineAdditionalInputMode;
        this.foldLineAdditionalInputMode = foldLineAdditionalInputMode;
        this.pcs.firePropertyChange("foldLineAdditionalInputMode", oldFoldLineAdditionalInputMode, foldLineAdditionalInputMode);
    }

    public MouseMode getMouseModeAfterColorSelection() {
        return mouseModeAfterColorSelection;
    }

    public void setMouseModeAfterColorSelection(MouseMode mouseModeAfterColorSelection) {
        MouseMode oldMouseModeAfterColorSelection = this.mouseModeAfterColorSelection;
        this.mouseModeAfterColorSelection = mouseModeAfterColorSelection;
        this.pcs.firePropertyChange("mouseModeAfterColorSelection", oldMouseModeAfterColorSelection, mouseModeAfterColorSelection);
    }

    public MouseMode getMouseMode() {
        return mouseMode;
    }

    public void setMouseMode(MouseMode mouseMode) {
        MouseMode oldMouseMode = this.mouseMode;
        this.mouseMode = mouseMode;
        this.pcs.firePropertyChange("mouseMode", oldMouseMode, mouseMode);
    }

    public LineColor getLineColor() {
        return lineColor;
    }

    public void setLineColor(LineColor lineColor) {
        LineColor oldLineColor = this.lineColor;
        this.lineColor = lineColor;
        this.pcs.firePropertyChange("lineColor", oldLineColor, lineColor);
    }

    public LineColor getAuxLiveLineColor() {
        return auxLiveLineColor;
    }

    public void setAuxLiveLineColor(LineColor auxLiveLineColor) {
        LineColor oldAuxLiveLineColor = this.auxLiveLineColor;
        this.auxLiveLineColor = auxLiveLineColor;
        this.pcs.firePropertyChange("auxLiveLineColor", oldAuxLiveLineColor, auxLiveLineColor);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public void setAuxLineWidth(int auxLineWidth) {
        int oldAuxLineWidth = this.auxLineWidth;
        this.auxLineWidth = auxLineWidth;
        this.pcs.firePropertyChange("auxLineWidth", oldAuxLineWidth, auxLineWidth);
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        int oldLineWidth = this.lineWidth;
        this.lineWidth = lineWidth;
        this.pcs.firePropertyChange("lineWidth", oldLineWidth, lineWidth);
    }

    public int getPointSize() {
        return pointSize;
    }

    public void setPointSize(int pointSize) {
        int oldPointSize = this.pointSize;
        this.pointSize = pointSize;
        this.pcs.firePropertyChange("pointSize", oldPointSize, pointSize);
    }

    public LineStyle getLineStyle() {
        return lineStyle;
    }

    public void setLineStyle(LineStyle lineStyle) {
        LineStyle oldLineStyle = this.lineStyle;
        this.lineStyle = lineStyle;
        this.pcs.firePropertyChange("lineStyle", oldLineStyle, lineStyle);
    }

    public boolean getAntiAlias() {
        return antiAlias;
    }

    public void setAntiAlias(boolean antiAlias) {
        boolean oldAntiAlias = this.antiAlias;
        this.antiAlias = antiAlias;
        this.pcs.firePropertyChange("antiAlias", oldAntiAlias, antiAlias);
    }

    public boolean getMouseWheelMovesCreasePattern() {
        return mouseWheelMovesCreasePattern;
    }

    public void setMouseWheelMovesCreasePattern(boolean mouseWheelMovesCreasePattern) {
        boolean oldMouseWheelMovesCreasePattern = this.mouseWheelMovesCreasePattern;
        this.mouseWheelMovesCreasePattern = mouseWheelMovesCreasePattern;
        this.pcs.firePropertyChange("mouseWheelMovesCreasePattern", oldMouseWheelMovesCreasePattern, mouseWheelMovesCreasePattern);
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

        lineColor = LineColor.RED_1;
        auxLiveLineColor = LineColor.ORANGE_4;

        mouseMode = MouseMode.FOLDABLE_LINE_DRAW_71;
        mouseModeAfterColorSelection = MouseMode.FOLDABLE_LINE_DRAW_71;

        foldLineAdditionalInputMode = DrawingWorker.FoldLineAdditionalInputMode.POLY_LINE_0;
        foldLineAdditionalInputMode_old = DrawingWorker.FoldLineAdditionalInputMode.POLY_LINE_0;

        this.pcs.firePropertyChange(null, null, null);
    }

    public boolean getDisplayPointSpotlight() {
        return displayPointSpotlight;
    }

    public void setDisplayPointSpotlight(boolean displayPointSpotlight) {
        boolean oldDisplayPointSpotlight = this.displayPointSpotlight;
        this.displayPointSpotlight = displayPointSpotlight;
        this.pcs.firePropertyChange("displayPointSpotlight", oldDisplayPointSpotlight, displayPointSpotlight);
    }

    public boolean getDisplayPointOffset() {
        return displayPointOffset;
    }

    public void setDisplayPointOffset(boolean displayPointOffset) {
        boolean oldDisplayPointOffset = this.displayPointOffset;
        this.displayPointOffset = displayPointOffset;
        this.pcs.firePropertyChange("displayPointOffset", oldDisplayPointOffset, displayPointOffset);
    }

    public boolean getDisplayGridInputAssist() {
        return displayGridInputAssist;
    }

    public void setDisplayGridInputAssist(boolean displayGridInputAssist) {
        boolean oldDisplayGridInputAssist = this.displayGridInputAssist;
        this.displayGridInputAssist = displayGridInputAssist;
        this.pcs.firePropertyChange("displayGridInputAssist", oldDisplayGridInputAssist, displayGridInputAssist);
    }

    public boolean getDisplayComments() {
        return displayComments;
    }

    public void setDisplayComments(boolean displayComments) {
        boolean oldDisplayComments = this.displayComments;
        this.displayComments = displayComments;
        this.pcs.firePropertyChange("displayComments", oldDisplayComments, displayComments);
    }

    public boolean getDisplayCpLines() {
        return displayCpLines;
    }

    public void setDisplayCpLines(boolean displayCpLines) {
        boolean oldDisplayCpLines = this.displayCpLines;
        this.displayCpLines = displayCpLines;
        this.pcs.firePropertyChange("displayCpLines", oldDisplayCpLines, displayCpLines);
    }

    public boolean getDisplayAuxLines() {
        return displayAuxLines;
    }

    public void setDisplayAuxLines(boolean displayAuxLines) {
        boolean oldDisplayAuxLines = this.displayAuxLines;
        this.displayAuxLines = displayAuxLines;
        this.pcs.firePropertyChange("displayAuxLines", oldDisplayAuxLines, displayAuxLines);
    }

    public boolean getDisplayLiveAuxLines() {
        return displayLiveAuxLines;
    }

    public void setDisplayLiveAuxLines(boolean displayLiveAuxLines) {
        boolean oldDisplayLiveAuxLines = this.displayLiveAuxLines;
        this.displayLiveAuxLines = displayLiveAuxLines;
        this.pcs.firePropertyChange("displayLiveAuxLines", oldDisplayLiveAuxLines, displayLiveAuxLines);
    }

    public boolean getDisplayMarkings() {
        return displayMarkings;
    }

    public void setDisplayMarkings(boolean displayMarkings) {
        boolean oldDisplayMarkings = this.displayMarkings;
        this.displayMarkings = displayMarkings;
        this.pcs.firePropertyChange("displayMarkings", oldDisplayMarkings, displayMarkings);
    }

    public boolean getDisplayCreasePatternOnTop() {
        return displayCreasePatternOnTop;
    }

    public void setDisplayCreasePatternOnTop(boolean displayCreasePatternOnTop) {
        boolean oldDisplayCreasePatternOnTop = this.displayCreasePatternOnTop;
        this.displayCreasePatternOnTop = displayCreasePatternOnTop;
        this.pcs.firePropertyChange("displayCreasePatternOnTop", oldDisplayCreasePatternOnTop, displayCreasePatternOnTop);
    }

    public boolean getDisplayFoldingProgress() {
        return displayFoldingProgress;
    }

    public void setDisplayFoldingProgress(boolean displayFoldingProgress) {
        boolean oldDisplayFoldingProgress = this.displayFoldingProgress;
        this.displayFoldingProgress = displayFoldingProgress;
        this.pcs.firePropertyChange("displayFoldingProgress", oldDisplayFoldingProgress, displayFoldingProgress);
    }

    public void decreasePointSize() {
        int pointSize = this.pointSize - 1;
        if (pointSize < 0) {
            pointSize = 0;
        }
        setPointSize(pointSize);
    }

    public void increasePointSize() {
        setPointSize(pointSize + 1);
    }

    public void advanceLineStyle() {
        setLineStyle(lineStyle.advance());
    }

    public void toggleAntiAlias() {
        setAntiAlias(!antiAlias);
    }

    public void decreaseLineWidth() {
        int lineWidth = this.lineWidth - 2;
        if (lineWidth < 1) {
            lineWidth = 1;
        }
        setLineWidth(lineWidth);
    }

    public void increaseLineWidth() {
        setLineWidth(lineWidth + 2);
    }

    public float getCalculatedLineWidth() {
        float fLineWidth = (float) lineWidth;

        if (antiAlias) {
            fLineWidth += 0.2f;
        }

        return fLineWidth;
    }

    public void decreaseAuxLineWidth() {
        int auxLineWidth = this.auxLineWidth - 2;
        if (auxLineWidth < 3) {
            auxLineWidth = 3;
        }
        setAuxLineWidth(auxLineWidth);
    }

    public void increaseAuxLineWidth() {
        setAuxLineWidth(auxLineWidth + 2);
    }

    public float getCalculatedAuxLineWidth() {
        float fAuxLineWidth = (float) auxLineWidth;

        if (antiAlias) {
            fAuxLineWidth += 0.2f;
        }

        return fAuxLineWidth;
    }
}
