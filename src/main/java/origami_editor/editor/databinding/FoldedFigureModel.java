package origami_editor.editor.databinding;

import origami_editor.editor.folded_figure.FoldedFigure;

import java.awt.*;

public class FoldedFigureModel {
    private Color frontColor;
    private Color backColor;
    private Color lineColor;

    private double scale;
    private double rotation;

    private boolean antiAlias;
    private boolean displayShadows;
    private FoldedFigure.State state;

    public FoldedFigureModel() {
        reset();
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
    }

    public Color getFrontColor() {
        return frontColor;
    }

    public void setFrontColor(Color frontColor) {
        this.frontColor = frontColor;
    }

    public Color getBackColor() {
        return backColor;
    }

    public void setBackColor(Color backColor) {
        this.backColor = backColor;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        if (scale > 0.0) {
            this.scale = scale;
        }
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public boolean getAntiAlias() {
        return antiAlias;
    }

    public void setAntiAlias(boolean antiAlias) {
        this.antiAlias = antiAlias;
    }

    public boolean getDisplayShadows() {
        return displayShadows;
    }

    public void setDisplayShadows(boolean displayShadows) {
        this.displayShadows = displayShadows;
    }

    public FoldedFigure.State getState() {
        return state;
    }

    public void setState(FoldedFigure.State state) {
        this.state = state;
    }

    public void toggleAntiAlias() {
        antiAlias = !antiAlias;
    }

    public void toggleDisplayShadows() {
        displayShadows = !displayShadows;
    }

    public void advanceState() {
        state = state.advance();
    }
}
