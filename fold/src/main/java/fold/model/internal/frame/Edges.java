package fold.model.internal.frame;

import fold.model.FoldEdgeAssignment;

import java.util.ArrayList;
import java.util.List;

public class Edges {
    private List<List<Integer>> vertices = new ArrayList<>();
    private List<FoldEdgeAssignment> assignment = new ArrayList<>();
    private List<Double> foldAngle = new ArrayList<>();
    private List<Double> length = new ArrayList<>();

    public List<List<Integer>> getVertices() {
        return vertices;
    }

    public void setVertices(List<List<Integer>> vertices) {
        this.vertices = vertices;
    }

    public List<FoldEdgeAssignment> getAssignment() {
        return assignment;
    }

    public void setAssignment(List<FoldEdgeAssignment> assignment) {
        this.assignment = assignment;
    }

    public List<Double> getFoldAngle() {
        return foldAngle;
    }

    public void setFoldAngle(List<Double> foldAngle) {
        this.foldAngle = foldAngle;
    }

    public List<Double> getLength() {
        return length;
    }

    public void setLength(List<Double> length) {
        this.length = length;
    }
}
