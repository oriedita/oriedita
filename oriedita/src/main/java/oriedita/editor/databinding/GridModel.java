package oriedita.editor.databinding;

import javax.inject.Inject;
import javax.inject.Singleton;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

@Singleton
public class GridModel implements Serializable {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private int intervalGridSize;
    private int gridSize;
    private double gridXA;
    private double gridXB;
    private double gridXC;
    private double gridYA;
    private double gridYB;
    private double gridYC;
    private double gridAngle;
    private State baseState;
    private int verticalScalePosition;
    private int horizontalScalePosition;

    private boolean drawDiagonalGridlines;

    @Inject
    public GridModel() {
        reset();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public void reset() {
        gridSize = 8;
        baseState = State.WITHIN_PAPER;

        gridAngle = 90.0;

        horizontalScalePosition = 0;
        verticalScalePosition = 0;

        intervalGridSize = 5;

        resetGridX();
        resetGridY();

        this.pcs.firePropertyChange(null, null, null);
    }

    public State getBaseState() {
        return baseState;
    }

    public void setBaseState(State newBaseState) {
        State oldBaseState = this.baseState;
        this.baseState = newBaseState;
        this.pcs.firePropertyChange("baseState", oldBaseState, newBaseState);
    }

    public void advanceBaseState() {
        setBaseState(baseState.advance());
    }

    public boolean getDrawDiagonalGridlines(){
        return drawDiagonalGridlines;
    }

    public void setDrawDiagonalGridlines(boolean newVal) {
        this.drawDiagonalGridlines = newVal;
        this.pcs.firePropertyChange("drawDiagonalGridlines", !newVal, newVal);
    }

    public int getVerticalScalePosition() {
        return verticalScalePosition;
    }

    public void setVerticalScalePosition(int newVerticalScalePosition) {
        int oldVerticalScalePosition = this.verticalScalePosition;
        this.verticalScalePosition = newVerticalScalePosition;
        this.pcs.firePropertyChange("verticalScalePosition", oldVerticalScalePosition, newVerticalScalePosition);
    }

    public int getHorizontalScalePosition() {
        return horizontalScalePosition;
    }

    public void setHorizontalScalePosition(int newHorizontalScalePosition) {
        int oldHorizontalScalePosition = this.horizontalScalePosition;
        this.horizontalScalePosition = newHorizontalScalePosition;
        this.pcs.firePropertyChange("horizontalScalePosition", oldHorizontalScalePosition, newHorizontalScalePosition);
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(final int gridSize) {
        int oldGridSize = this.gridSize;
        this.gridSize = Math.max(gridSize, 1);
        this.pcs.firePropertyChange("gridSize", oldGridSize, this.gridSize);
    }

    public double getGridXA() {
        return gridXA;
    }

    public void applyGridX(double gridXA, double gridXB, double gridXC) {
        if (validateGrid(gridXA, gridXB, gridXC)) {
            this.gridXA = gridXA;
            this.gridXB = gridXB;
            this.gridXC = gridXC;
        } else {
            resetGridX();
        }
    }

    public void setGridXA(final double gridXA) {
        double oldGridXA = this.gridXA;
        if (validateGrid(gridXA, gridXB, gridXC)) {
            this.gridXA = gridXA;
        } else {
            resetGridX();
        }
        this.pcs.firePropertyChange("gridXA", oldGridXA, this.gridXA);
    }

    public double getGridXB() {
        return gridXB;
    }

    public void setGridXB(final double gridXB) {
        double oldGridXB = this.gridXB;
        if (validateGrid(gridXA, gridXB, gridXC)) {
            this.gridXB = gridXB;
        } else {
            resetGridX();
        }
        this.pcs.firePropertyChange("gridXB", oldGridXB, this.gridXB);
    }

    public int getIntervalGridSize() {
        return intervalGridSize;
    }

    public void setIntervalGridSize(final int intervalGridSize) {
        int oldIntervalGridSize = this.intervalGridSize;
        this.intervalGridSize = Math.max(intervalGridSize, 1);
        this.pcs.firePropertyChange("intervalGridSize", oldIntervalGridSize, this.intervalGridSize);
    }

    public double getGridXC() {
        return gridXC;
    }

    public void setGridXC(final double gridXC) {
        double oldGridXC = this.gridXC;
        if (validateGrid(gridXA, gridXB, gridXC)) {
            this.gridXC = Math.max(gridXC, 0.0);
        } else {
            resetGridX();
        }
        this.pcs.firePropertyChange("gridXC", oldGridXC, this.gridXC);
    }

    public double getGridYA() {
        return gridYA;
    }

    public void applyGridY(double gridYA, double gridYB, double gridYC) {
        if (validateGrid(gridYA, gridYB, gridYC)) {
            this.gridYA = gridYA;
            this.gridYB = gridYB;
            this.gridYC = gridYC;
        } else {
            resetGridY();
        }
    }

    public void setGridYA(final double gridYA) {
        double oldGridYA = this.gridYA;
        if (validateGrid(gridYA, gridYB, gridYC)) {
            this.gridYA = gridYA;
        } else {
            resetGridY();
        }
        this.pcs.firePropertyChange("gridYA", oldGridYA, this.gridYA);
    }

    public double getGridYB() {
        return gridYB;
    }

    public void setGridYB(final double gridYB) {
        double oldGridYB = this.gridYB;
        if (validateGrid(gridYA, gridYB, gridYC)) {
            this.gridYB = gridYB;
        } else {
            resetGridY();
        }
        this.pcs.firePropertyChange("gridYB", oldGridYB, this.gridYB);
    }

    public double getGridYC() {
        return gridYC;
    }

    public void setGridYC(final double gridYC) {
        double oldGridYC = this.gridYC;
        if (validateGrid(gridYA, gridYB, gridYC)) {
            this.gridYC = Math.max(gridYC, 0.0);
        } else {
            resetGridY();
        }
        this.pcs.firePropertyChange("gridYC", oldGridYC, this.gridYC);
    }

    public double getGridAngle() {
        return gridAngle;
    }

    public void setGridAngle(final double gridAngle) {
        double oldAngle = this.gridAngle;
        double newAngle = gridAngle;
        if (Math.abs(OritaCalc.angle_between_0_360(this.gridAngle)) < Epsilon.UNKNOWN_01) {
            newAngle = 90.0;
        }
        if (Math.abs(OritaCalc.angle_between_0_360(this.gridAngle - 180.0)) < Epsilon.UNKNOWN_01) {
            newAngle = 90.0;
        }
        if (Math.abs(OritaCalc.angle_between_0_360(this.gridAngle - 360.0)) < Epsilon.UNKNOWN_01) {
            newAngle = 90.0;
        }

        this.gridAngle = newAngle;
        this.pcs.firePropertyChange("gridAngle", oldAngle, newAngle);
    }

    private boolean validateGrid(double a, double b, double c) {
        double gridLength = a + b * Math.sqrt(c);
        if (gridLength < 0.0) {
            return false;
        }

        if (Math.abs(gridLength) < Epsilon.UNKNOWN_1EN4) {
            return false;
        }

        return true;
    }

    public double determineGridXLength() {
        return gridXA + gridXB * Math.sqrt(gridXC);
    }

    public double determineGridYLength() {
        return gridYA + gridYB * Math.sqrt(gridYC);
    }

    public void resetGridY() {
        gridYA = 1.0;
        gridYB = 0.0;
        gridYC = 1.0;
    }

    public void resetGridX() {
        gridXA = 1.0;
        gridXB = 0.0;
        gridXC = 1.0;
    }

    public void changeHorizontalScalePosition() {
        int horizontalScalePosition = this.horizontalScalePosition + 1;
        if (horizontalScalePosition >= intervalGridSize) {
            horizontalScalePosition = 0;
        }

        setHorizontalScalePosition(horizontalScalePosition);
    }

    public void changeVerticalScalePosition() {
        int verticalScalePosition = this.verticalScalePosition + 1;
        if (verticalScalePosition >= intervalGridSize) {
            verticalScalePosition = 0;
        }

        setVerticalScalePosition(verticalScalePosition);
    }

    public void set(GridModel gridModel) {
        intervalGridSize = gridModel.getIntervalGridSize();
        gridSize = gridModel.getGridSize();
        gridXA = gridModel.getGridXA();
        gridXB = gridModel.getGridXB();
        gridXC = gridModel.getGridXC();
        gridYA = gridModel.getGridYA();
        gridYB = gridModel.getGridYB();
        gridYC = gridModel.getGridYC();
        gridAngle = gridModel.getGridAngle();
        baseState = gridModel.getBaseState();
        verticalScalePosition = gridModel.getVerticalScalePosition();
        horizontalScalePosition = gridModel.getHorizontalScalePosition();

        this.pcs.firePropertyChange(null, null, null);
    }

    /**
     * The state of the grid, either within the paper, spanning the whole screen or hidden.
     */
    public enum State {
        HIDDEN(0),
        WITHIN_PAPER(1),
        FULL(2);

        int state;

        State(int state) {
            this.state = state;
        }

        public static State from(String state) {
            return from(Integer.parseInt(state));
        }

        public static State from(int state) {
            for (State val : State.values()) {
                if (val.getState() == state) {
                    return val;
                }
            }

            throw new IllegalArgumentException();
        }

        public State advance() {
            return values()[(ordinal() + 1) % values().length];
        }

        public int getState() {
            return state;
        }

        @Override
        public String toString() {
            return Integer.toString(state);
        }
    }
}