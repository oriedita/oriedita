package oriedita.editor.databinding;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.inject.Inject;
import javax.inject.Singleton;
import oriedita.editor.Colors;
import oriedita.editor.canvas.LineStyle;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This model is saved to disk and restored when the application starts.
 */
@Singleton
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
    private boolean displaySelfIntersection;
    private boolean preciseZoom;
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
    private int windowState;
    private Point windowPosition;
    private String laf;
    private Dimension windowSize;
    private List<File> recentFileList;

    private Color gridColor;
    private Color gridScaleColor;
    private int gridLineWidth;
    private boolean displayNumbers;

    @Inject
    public ApplicationModel() {
        reset();
    }

    public boolean getDisplayNumbers() {
        return displayNumbers;
    }

    public void setDisplayNumbers(boolean displayNumbers) {
        boolean oldDisplayNumbers = this.displayNumbers;
        this.displayNumbers = displayNumbers;
        this.pcs.firePropertyChange("displayNumbers", oldDisplayNumbers, displayNumbers);
    }

    public List<File> getRecentFileList() {
        return recentFileList;
    }

    public void setRecentFileList(List<File> recentFileList) {
        List<File> oldRecentFileList = this.recentFileList;
        this.recentFileList = recentFileList;
        this.pcs.firePropertyChange("recentFileList", oldRecentFileList, recentFileList);
    }

    public void removeRecentFile(File recentFile) {
        List<File> oldList = recentFileList;
        recentFileList = new ArrayList<>(recentFileList.subList(0, Math.min(recentFileList.size(), 20)));
        recentFileList.remove(recentFile);
        this.pcs.firePropertyChange("recentFileList", oldList, recentFileList);
    }

    public void addRecentFile(File selectedFile) {
        List<File> oldList = recentFileList;
        recentFileList = new ArrayList<>(recentFileList.subList(0, Math.min(recentFileList.size(), 20)));
        recentFileList.remove(selectedFile);
        recentFileList.add(0, selectedFile);
        this.pcs.firePropertyChange("recentFileList", oldList, recentFileList);
    }

    public boolean getDisplaySelfIntersection() {
        return displaySelfIntersection;
    }

    public void setDisplaySelfIntersection(boolean displaySelfIntersection) {
        boolean oldDisplaySelfIntersection = this.displaySelfIntersection;
        this.displaySelfIntersection = displaySelfIntersection;
        this.pcs.firePropertyChange("displaySelfIntersection", oldDisplaySelfIntersection, displaySelfIntersection);
    }

    public boolean isPreciseZoom() {
        return preciseZoom;
    }

    public void setPreciseZoom(boolean preciseZoom) {
        boolean oldPreciseZoom = this.preciseZoom;
        this.preciseZoom = preciseZoom;
        this.pcs.firePropertyChange("preciseZoom", oldPreciseZoom, preciseZoom);
    }

    public String getLaf() {
        return laf;
    }

    public void setLaf(String laf) {
        String oldLaf = this.laf;
        if (!this.laf.equals(laf)) {
            this.laf = laf;
            this.pcs.firePropertyChange("laf", oldLaf, laf);
        }
    }

    public Dimension getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(Dimension windowSize) {
        Dimension oldWindowSize = this.windowSize;
        this.windowSize = windowSize;
        this.pcs.firePropertyChange("windowSize", oldWindowSize, windowSize);
    }

    public int getWindowState() {
        return windowState;
    }

    public void setWindowState(int windowState) {
        int oldWindowState = this.windowState;
        this.windowState = windowState;
        this.pcs.firePropertyChange("windowState", oldWindowState, windowState);
    }

    public Point getWindowPosition() {
        return windowPosition;
    }

    public void setWindowPosition(Point windowPosition) {
        Point oldWindowPosition = this.windowPosition;
        this.windowPosition = windowPosition;
        this.pcs.firePropertyChange("windowPosition", oldWindowPosition, windowPosition);
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
        displaySelfIntersection = true;
        lineWidth = 1;
        pointSize = 1;
        lineStyle = LineStyle.COLOR;
        antiAlias = false;
        auxLineWidth = 3;

        preciseZoom = true;

        helpVisible = true;
        consoleVisible = false;

        check4Enabled = false;

        circleCustomizedColor = new Color(100, 200, 200);

        selectPersistent = false;
        correctCpBeforeFolding = false;
        numPolygonCorners = 5;
        foldLineDividingNumber = 2;
        defaultDirectory = null;
        recentFileList = new ArrayList<>();

        windowPosition = null;
        windowState = Frame.NORMAL;
        windowSize = null;

        gridColor = Colors.GRID_LINE;
        gridScaleColor = Colors.GRID_SCALE;
        gridLineWidth = 1;

        laf = FlatLightLaf.class.getName();

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
        displaySelfIntersection = applicationModel.getDisplaySelfIntersection();
        lineWidth = applicationModel.getLineWidth();
        auxLineWidth = applicationModel.getAuxLineWidth();
        pointSize = applicationModel.getPointSize();
        lineStyle = applicationModel.getLineStyle();
        antiAlias = applicationModel.getAntiAlias();
        mouseWheelMovesCreasePattern = applicationModel.getMouseWheelMovesCreasePattern();
        helpVisible = applicationModel.getHelpVisible();
        preciseZoom = applicationModel.isPreciseZoom();

        circleCustomizedColor = applicationModel.getCircleCustomizedColor();
        selectPersistent = applicationModel.getSelectPersistent();
        correctCpBeforeFolding = applicationModel.getCorrectCpBeforeFolding();
        check4Enabled = applicationModel.getCheck4Enabled();
        numPolygonCorners = applicationModel.getNumPolygonCorners();
        foldLineDividingNumber = applicationModel.getFoldLineDividingNumber();
        defaultDirectory = applicationModel.getDefaultDirectory();
        windowSize = applicationModel.getWindowSize();
        windowPosition = applicationModel.getWindowPosition();
        windowState = applicationModel.getWindowState();

        gridColor = applicationModel.getGridColor();
        gridScaleColor = applicationModel.getGridScaleColor();
        gridLineWidth = applicationModel.getGridLineWidth();

        laf = applicationModel.getLaf();
        recentFileList = applicationModel.getRecentFileList().stream().filter(File::exists).collect(Collectors.toList());

        this.pcs.firePropertyChange(null, null, null);
    }

    public void toggleHelpVisible() {
        setHelpVisible(!helpVisible);
    }

    public void toggleDarkMode() {
        if (laf.equals(FlatLightLaf.class.getName())) {
            setLaf(FlatDarkLaf.class.getName());
        } else {
            setLaf(FlatLightLaf.class.getName());
        }
    }

    public void setDarkMode(boolean darkMode) {
        if (darkMode) {
            setLaf(FlatDarkLaf.class.getName());
        } else {
            setLaf(FlatLightLaf.class.getName());
        }
    }

    public boolean determineLafDark() {
        try {
            Class<?> lnfClass = Class.forName(laf);

            LookAndFeel lookAndFeel = (LookAndFeel) lnfClass.getDeclaredConstructor().newInstance();
            return lookAndFeel instanceof FlatLaf && ((FlatLaf) lookAndFeel).isDark();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return false;
        }
    }

    public void togglePreciseZoom() {
        setPreciseZoom(!preciseZoom);
    }

    public void toggleDisplaySelfIntersection() {
        setDisplaySelfIntersection(!displaySelfIntersection);
    }

    public void reload() {
        this.pcs.firePropertyChange(null, null, null);
    }


    public Color getGridColor() {
        return gridColor;
    }

    public void setGridColor(Color newGridColor) {
        Color oldGridColor = this.gridColor;
        this.gridColor = newGridColor;

        this.pcs.firePropertyChange("gridColor", oldGridColor, newGridColor);
    }

    public Color getGridScaleColor() {
        return gridScaleColor;
    }

    public void setGridScaleColor(Color newGridScaleColor) {
        Color oldGridScaleColor = this.gridScaleColor;
        this.gridScaleColor = newGridScaleColor;
        this.pcs.firePropertyChange("gridScaleColor", oldGridScaleColor, newGridScaleColor);
    }

    public int getGridLineWidth() {
        return gridLineWidth;
    }

    public void setGridLineWidth(int newGridLineWidth) {
        int oldGridLineWidth = this.gridLineWidth;
        this.gridLineWidth = newGridLineWidth;
        this.pcs.firePropertyChange("gridLineWidth", oldGridLineWidth, newGridLineWidth);
    }


    public void decreaseGridLineWidth() {
        int gridLineWidth = this.gridLineWidth - 2;
        if (gridLineWidth < 1) {
            gridLineWidth = 1;
        }

        setGridLineWidth(gridLineWidth);
    }

    public void increaseGridLineWidth() {
        setGridLineWidth(gridLineWidth + 2);
    }

}
