package fold.model.frame;

import fold.model.FoldEdgeAssignment;

public class Edges {
    private int[][] vertices;
    private FoldEdgeAssignment[] assignment;
    private double[] foldAngle;
    private double[] length;

    public int[][] getVertices() {
        return vertices;
    }

    public void setVertices(int[][] vertices) {
        this.vertices = vertices;
    }

    public FoldEdgeAssignment[] getAssignment() {
        return assignment;
    }

    public void setAssignment(FoldEdgeAssignment[] assignment) {
        this.assignment = assignment;
    }

    public double[] getFoldAngle() {
        return foldAngle;
    }

    public void setFoldAngle(double[] foldAngle) {
        this.foldAngle = foldAngle;
    }

    public double[] getLength() {
        return length;
    }

    public void setLength(double[] length) {
        this.length = length;
    }
}
