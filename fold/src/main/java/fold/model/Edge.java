package fold.model;

public class Edge {
    private Integer id;
    private Vertex start;
    private Vertex end;
    private FoldEdgeAssignment assignment;
    private Double foldAngle;
    private Double length;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Vertex getStart() {
        return start;
    }

    public void setStart(Vertex start) {
        this.start = start;
    }

    public Vertex getEnd() {
        return end;
    }

    public void setEnd(Vertex end) {
        this.end = end;
    }

    public FoldEdgeAssignment getAssignment() {
        return assignment;
    }

    public void setAssignment(FoldEdgeAssignment assignment) {
        this.assignment = assignment;
    }

    public Double getFoldAngle() {
        return foldAngle;
    }

    public void setFoldAngle(Double foldAngle) {
        this.foldAngle = foldAngle;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }
}
