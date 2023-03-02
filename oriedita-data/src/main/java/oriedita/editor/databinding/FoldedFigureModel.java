package oriedita.editor.databinding;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import origami.folding.FoldedFigure;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

@ApplicationScoped
public class FoldedFigureModel implements Serializable {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private Color frontColor;
    private Color backColor;
    private Color lineColor;
    private double scale;
    private double rotation;
    private boolean antiAlias;
    private boolean displayShadows;
    private FoldedFigure.State state;
    private int foldedCases;
    private boolean findAnotherOverlapValid;

    public boolean isFindAnotherOverlapValid() {
        return findAnotherOverlapValid;
    }

    public void setFindAnotherOverlapValid(boolean findAnotherOverlapValid) {
        boolean oldFindAnotherOverlapValid = this.findAnotherOverlapValid;
        this.findAnotherOverlapValid = findAnotherOverlapValid;
        this.pcs.firePropertyChange("findAnotherOverlapValid", oldFindAnotherOverlapValid, findAnotherOverlapValid);
    }

    public int getFoldedCases() {
        return foldedCases;
    }

    public void setFoldedCases(int foldedCases) {
        int oldFoldedCases = this.foldedCases;
        this.foldedCases = foldedCases;
        this.pcs.firePropertyChange("foldedCases", oldFoldedCases, foldedCases);
    }

    public int getTransparentTransparency() {
        return transparentTransparency;
    }

    public void setTransparentTransparency(int transparentTransparency) {
        int oldTransparentTransparency = this.transparentTransparency;
        this.transparentTransparency = transparentTransparency;
        this.pcs.firePropertyChange("transparentTransparency", oldTransparentTransparency, transparentTransparency);
    }

    private int transparentTransparency;

    private boolean transparencyColor;

    public boolean isTransparencyColor() {
        return transparencyColor;
    }

    public void setTransparencyColor(boolean transparencyColor) {
        boolean oldTransparencyColor = this.transparencyColor;
        this.transparencyColor = transparencyColor;
        this.pcs.firePropertyChange("transparencyColor", oldTransparencyColor, transparencyColor);
    }

    @Inject
    public FoldedFigureModel() {
        reset();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public void reset() {
        scale = 1.0;
        rotation = 0.0;
        antiAlias = true;
        displayShadows = false;
        state = FoldedFigure.State.FRONT_0;

        frontColor = new Color(255, 255, 50);
        backColor = new Color(233, 233, 233);
        lineColor = Color.black;

        transparencyColor = false;

        transparentTransparency = 16;

        foldedCases = 1;

        this.pcs.firePropertyChange(null, null, null);
    }

    public void restorePrefDefaults(){
        antiAlias = true;

        this.pcs.firePropertyChange(null, null, null);
    }

    public boolean isSame (FoldedFigureModel foldedFigureModel){
        if(antiAlias == foldedFigureModel.getAntiAlias()){
            return true;
        } else { return false; }
    }

    public Color getFrontColor() {
        return frontColor;
    }

    public void setFrontColor(Color frontColor) {
        Color oldFrontColor = this.frontColor;
        this.frontColor = frontColor;
        this.pcs.firePropertyChange("frontColor", oldFrontColor, frontColor);
    }

    public Color getBackColor() {
        return backColor;
    }

    public void setBackColor(Color backColor) {
        Color oldFrontColor = this.backColor;
        this.backColor = backColor;
        this.pcs.firePropertyChange("backColor", oldFrontColor, backColor);
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color lineColor) {
        Color oldFrontColor = this.lineColor;
        this.lineColor = lineColor;
        this.pcs.firePropertyChange("lineColor", oldFrontColor, lineColor);
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        if (scale > 0.0) {
            double oldScale = this.scale;
            this.scale = scale;
            this.pcs.firePropertyChange("scale", oldScale, scale);
        }
    }

    public void zoomIn(double zoomSpeed) {
        zoomBy(-1, zoomSpeed);
    }

    public void zoomOut(double zoomSpeed) {
        zoomBy(1, zoomSpeed);
    }

    public void zoomBy(double value, double zoomSpeed) {
        setScale(getScaleForZoomBy(value, zoomSpeed));
    }

    public double getScaleForZoomBy(double value, double zoomSpeed) {
        return getScaleForZoomBy(value, zoomSpeed, scale);
    }

    public double getScaleForZoomBy(double value, double zoomSpeed, double initialScale) {
        double zoomBase = 1 + zoomSpeed/10;
        if (value > 0) {
            return (initialScale / Math.pow(zoomBase, value));
        } else if (value < 0) {
            return (initialScale * Math.pow(zoomBase, Math.abs(value)));
        }
        return initialScale;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        double oldRotation = this.rotation;
        this.rotation = rotation;
        this.pcs.firePropertyChange("rotation", oldRotation, rotation);
    }

    public boolean getAntiAlias() {
        return antiAlias;
    }

    public void setAntiAlias(boolean antiAlias) {
        boolean oldAntiAlias = this.antiAlias;
        this.antiAlias = antiAlias;
        this.pcs.firePropertyChange("antiAlias", oldAntiAlias, antiAlias);
    }

    public boolean getDisplayShadows() {
        return displayShadows;
    }

    public void setDisplayShadows(boolean displayShadows) {
        boolean oldDisplayShadows = this.displayShadows;
        this.displayShadows = displayShadows;
        this.pcs.firePropertyChange("displayShadows", oldDisplayShadows, displayShadows);
    }

    public FoldedFigure.State getState() {
        return state;
    }

    public void setState(FoldedFigure.State state) {
        FoldedFigure.State oldState = this.state;
        this.state = state;
        this.pcs.firePropertyChange("state", oldState, state);
    }

    public void set(FoldedFigureModel model) {
        scale = model.getScale();
        rotation = model.getRotation();
        antiAlias = model.getAntiAlias();
        displayShadows = model.getDisplayShadows();
        state = model.getState();

        frontColor = model.getFrontColor();
        backColor = model.getBackColor();
        lineColor = model.getLineColor();

        transparencyColor = model.isTransparencyColor();

        transparentTransparency = model.getTransparentTransparency();

        foldedCases = model.getFoldedCases();

        this.pcs.firePropertyChange(null, null, null);
    }

    public void toggleAntiAlias() {
        setAntiAlias(!antiAlias);
    }

    public void toggleDisplayShadows() {
        setDisplayShadows(!displayShadows);
    }

    public void advanceState() {
        setState(state.advance());
    }

    public void decreaseTransparency() {
        setTransparentTransparency(Math.max(this.transparentTransparency / 2, 1));
    }

    public void increaseTransparency() {

        setTransparentTransparency(Math.min(this.transparentTransparency * 2, 64));
    }
}
