package origami_editor.editor;

import origami_editor.graphic2d.grid.Grid;
import origami_editor.graphic2d.oritacalc.OritaCalc;

import java.awt.*;

public class GridConfiguration {
    private int intervalGridSize;
    private int gridSize;
    private double gridXA;
    private double gridXB;
    private double gridXC;
    private double gridYA;
    private double gridYB;
    private double gridYC;
    private double gridAngle;

    private Color gridColor;
    private Color gridScaleColor;

    private int gridLineWidth;
    private Grid.State baseState;

    private int verticalScalePosition;
    private int horizontalScalePosition;

    public GridConfiguration() {
        reset();
    }

    public void reset() {
        gridColor = new Color(230, 230, 230);
        gridScaleColor = new Color(180, 200, 180);
        gridLineWidth = 1;
        gridSize = 8;
        baseState = Grid.State.WITHIN_PAPER;

        horizontalScalePosition = 0;
        verticalScalePosition = 0;

        intervalGridSize = 5;

        resetGridX();
        resetGridY();
    }


    public Color getGridColor() {
        return gridColor;
    }

    public void setGridColor(Color gridColor) {
        this.gridColor = gridColor;
    }

    public Color getGridScaleColor() {
        return gridScaleColor;
    }

    public void setGridScaleColor(Color gridScaleColor) {
        this.gridScaleColor = gridScaleColor;
    }

    public int getGridLineWidth() {
        return gridLineWidth;
    }

    public void setGridLineWidth(int gridLineWidth) {
        this.gridLineWidth = gridLineWidth;
    }


    public void decreaseGridLineWidth() {
        gridLineWidth = gridLineWidth - 2;
        if (gridLineWidth < 1) {
            gridLineWidth = 1;
        }
    }

    public void increaseGridLineWidth() {
        gridLineWidth = gridLineWidth + 2;
    }

    public Grid.State getBaseState() {
        return baseState;
    }

    public void setBaseState(Grid.State baseState) {
        this.baseState = baseState;
    }

    public void advanceBaseState() {
        baseState = baseState.advance();
    }

    public int getVerticalScalePosition() {
        return verticalScalePosition;
    }

    public void setVerticalScalePosition(int verticalScalePosition) {
        this.verticalScalePosition = verticalScalePosition;
    }

    public int getHorizontalScalePosition() {
        return horizontalScalePosition;
    }

    public void setHorizontalScalePosition(int horizontalScalePosition) {
        this.horizontalScalePosition = horizontalScalePosition;
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(final int gridSize) {
        this.gridSize = Math.max(gridSize, 1);
    }

    public double getGridXA() {
        return gridXA;
    }

    public void setGridXA(final double gridXA) {
        if (validateGrid(gridXA, gridXB, gridXC)) {
            this.gridXA = gridXA;
        } else {
            resetGridX();
        }
    }

    public double getGridXB() {
        return gridXB;
    }

    public void setGridXB(final double gridXB) {
        if (validateGrid(gridXA, gridXB, gridXC)) {
            this.gridXB = gridXB;
        } else {
            resetGridX();
        }
    }

    public int getIntervalGridSize() {
        return intervalGridSize;
    }

    public void setIntervalGridSize(final int intervalGridSize) {
        this.intervalGridSize = Math.max(intervalGridSize, 1);
    }

    public double getGridXC() {
        return gridXC;
    }

    public void setGridXC(final double gridXC) {
        if (validateGrid(gridXA, gridXB, gridXC)) {
            this.gridXC = Math.max(gridXC, 0.0);
        } else {
            resetGridX();
        }
    }

    public double getGridYA() {
        return gridYA;
    }

    public void setGridYA(final double gridYA) {
        if (validateGrid(gridYA, gridYB, gridYC)) {
            this.gridYA = gridYA;
        } else {
            resetGridY();
        }
    }

    public double getGridYB() {
        return gridYB;
    }

    public void setGridYB(final double gridYB) {
        if (validateGrid(gridYA, gridYB, gridYC)) {
            this.gridYB = gridYB;
        } else {
            resetGridY();
        }
    }

    public double getGridYC() {
        return gridYC;
    }

    public void setGridYC(final double gridYC) {
        if (validateGrid(gridYA, gridYB, gridYC)) {
            this.gridYC = Math.max(gridYC, 0.0);
        } else {
            resetGridY();
        }
    }

    public double getGridAngle() {
        return gridAngle;
    }

    public void setGridAngle(final double gridAngle) {
        this.gridAngle = gridAngle;
        if (Math.abs(OritaCalc.angle_between_0_360(this.gridAngle)) < 0.1) {
            this.gridAngle = 90.0;
        }
        if (Math.abs(OritaCalc.angle_between_0_360(this.gridAngle - 180.0)) < 0.1) {
            this.gridAngle = 90.0;
        }
        if (Math.abs(OritaCalc.angle_between_0_360(this.gridAngle - 360.0)) < 0.1) {
            this.gridAngle = 90.0;
        }
    }

    private boolean validateGrid(double a, double b, double c) {
        double gridLength = a + b * Math.sqrt(c);
        if (gridLength < 0.0) {
            return false;
        }

        if (Math.abs(gridLength) < 0.0001) {
            return false;
        }

        return true;
    }

    public double getGridXLength() {
        return gridXA + gridXB * Math.sqrt(gridXC);
    }

    public double getGridYLength() {
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
        gridXC = 0.0;
    }

    public void changeHorizontalScalePosition() {
        horizontalScalePosition++;
        if (horizontalScalePosition >= intervalGridSize) {
            horizontalScalePosition = 0;
        }
    }

    public void changeVerticalScalePosition() {
        verticalScalePosition++;
        if (verticalScalePosition >= intervalGridSize) {
            verticalScalePosition = 0;
        }
    }
}