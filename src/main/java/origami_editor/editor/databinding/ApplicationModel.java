package origami_editor.editor.databinding;

import origami_editor.editor.LineStyle;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

/**
 * This model is saved to disk and restored when the application starts.
 */
public class ApplicationModel implements Serializable {
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
    private Color circleCustomizedColor;
    private boolean selectPersistent;
    private boolean correctCpBeforeFolding;
    private boolean check4Enabled;
    private boolean helpVisible;

    private int foldLineDividingNumber;
    private int numPolygonCorners;

    private String defaultDirectory;

    public ApplicationModel() {
        reset();
    }

    public boolean getHelpVisible() {
        return helpVisible;
    }

    public void setHelpVisible(boolean helpVisible) {
        boolean oldHelpVisible = this.helpVisible;
        this.helpVisible = helpVisible;
        this.pcs.firePropertyChange("helpVisible", oldHelpVisible, helpVisible);
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

        helpVisible = true;

        check4Enabled = false;

        circleCustomizedColor = new Color(100, 200, 200);

        selectPersistent = false;
        correctCpBeforeFolding = false;
        numPolygonCorners = 5;
        foldLineDividingNumber = 2;
        defaultDirectory = null;

        this.pcs.firePropertyChange(null, null, null);
    }


    public int getAuxLineWidth() {
        return auxLineWidth;
    }

    public void setAuxLineWidth(int auxLineWidth) {
        int oldAuxLineWidth = this.auxLineWidth;
        this.auxLineWidth = auxLineWidth;
        this.pcs.firePropertyChange("auxLineWidth", oldAuxLineWidth, auxLineWidth);
    }

    public boolean getCorrectCpBeforeFolding() {
        return correctCpBeforeFolding;
    }

    public void setCorrectCpBeforeFolding(boolean correctCpBeforeFolding) {
        boolean oldCorrectCpBeforeFolding = this.correctCpBeforeFolding;
        this.correctCpBeforeFolding = correctCpBeforeFolding;
        this.pcs.firePropertyChange("correctCpBeforeFolding", oldCorrectCpBeforeFolding, correctCpBeforeFolding);
    }

    public boolean getSelectPersistent() {
        return selectPersistent;
    }

    public void setSelectPersistent(boolean selectPersistent) {
        boolean oldSelectPersistent = this.selectPersistent;
        this.selectPersistent = selectPersistent;
        this.pcs.firePropertyChange("selectPersistent", oldSelectPersistent, selectPersistent);
    }

    public Color getCircleCustomizedColor() {
        return circleCustomizedColor;
    }

    public void setCircleCustomizedColor(Color circleCustomizedColor) {
        Color oldCircleCustomizedColor = this.circleCustomizedColor;
        this.circleCustomizedColor = circleCustomizedColor;
        this.pcs.firePropertyChange("circleCustomizedColor", oldCircleCustomizedColor, circleCustomizedColor);
    }

    public boolean getCheck4Enabled() {
        return check4Enabled;
    }

    public void setCheck4Enabled(boolean check4Enabled) {
        boolean oldCheck4Enabled = this.check4Enabled;
        this.check4Enabled = check4Enabled;
        this.pcs.firePropertyChange("check4Enabled", oldCheck4Enabled, check4Enabled);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
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

    public float determineCalculatedLineWidth() {
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

    public float determineCalculatedAuxLineWidth() {
        float fAuxLineWidth = (float) auxLineWidth;

        if (antiAlias) {
            fAuxLineWidth += 0.2f;
        }

        return fAuxLineWidth;
    }

    public int getNumPolygonCorners() {
        return numPolygonCorners;
    }

    public void setNumPolygonCorners(int numPolygonCorners) {
        int oldNumPolygonCorners = this.numPolygonCorners;

        if (numPolygonCorners < 3) {
            numPolygonCorners = 3;
        }
        if (numPolygonCorners > 100) {
            numPolygonCorners = 100;
        }

        this.numPolygonCorners = numPolygonCorners;
        this.pcs.firePropertyChange("numPolygonCorners", oldNumPolygonCorners, numPolygonCorners);
    }

    public int getFoldLineDividingNumber() {
        return foldLineDividingNumber;
    }

    public void setFoldLineDividingNumber(int foldLineDividingNumber) {
        int oldFoldLineDividingNumber = this.foldLineDividingNumber;
        this.foldLineDividingNumber = Math.max(foldLineDividingNumber, 1);
        this.pcs.firePropertyChange("foldLineDividingNumber", oldFoldLineDividingNumber, this.foldLineDividingNumber);
    }


    public String getDefaultDirectory() {
        return defaultDirectory;
    }

    public void setDefaultDirectory(String defaultDirectory) {
        String oldDefaultDirectory = this.defaultDirectory;
        this.defaultDirectory = defaultDirectory;
        this.pcs.firePropertyChange("defaultDirectory", oldDefaultDirectory, defaultDirectory);
    }

    public void set(ApplicationModel applicationModel) {
        displayPointSpotlight = applicationModel.getDisplayPointSpotlight();
        displayPointOffset = applicationModel.getDisplayPointOffset();
        displayGridInputAssist = applicationModel.getDisplayGridInputAssist();
        displayComments = applicationModel.getDisplayComments();
        displayCpLines = applicationModel.getDisplayCpLines();
        displayAuxLines = applicationModel.getDisplayAuxLines();
        displayLiveAuxLines = applicationModel.getDisplayLiveAuxLines();
        displayMarkings = applicationModel.getDisplayMarkings();
        displayCreasePatternOnTop = applicationModel.getDisplayCreasePatternOnTop();
        displayFoldingProgress = applicationModel.getDisplayFoldingProgress();
        lineWidth = applicationModel.getLineWidth();
        auxLineWidth = applicationModel.getAuxLineWidth();
        pointSize = applicationModel.getPointSize();
        lineStyle = applicationModel.getLineStyle();
        antiAlias = applicationModel.getAntiAlias();
        mouseWheelMovesCreasePattern = applicationModel.getMouseWheelMovesCreasePattern();
        helpVisible = applicationModel.getHelpVisible();

        circleCustomizedColor = applicationModel.getCircleCustomizedColor();
        selectPersistent = applicationModel.getSelectPersistent();
        correctCpBeforeFolding = applicationModel.getCorrectCpBeforeFolding();
        check4Enabled = applicationModel.getCheck4Enabled();
        numPolygonCorners = applicationModel.getNumPolygonCorners();
        foldLineDividingNumber = applicationModel.getFoldLineDividingNumber();
        defaultDirectory = applicationModel.getDefaultDirectory();

        this.pcs.firePropertyChange(null, null, null);
    }

    public void toggleHelpVisible() {
        setHelpVisible(!helpVisible);
    }
}
