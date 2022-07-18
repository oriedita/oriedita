package fold.model.internal;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import fold.model.internal.frame.Edges;
import fold.model.internal.frame.Faces;
import fold.model.internal.frame.FrameMetadata;
import fold.model.internal.frame.Vertices;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FoldFrame {
    /**
     * Frame-level metadata properties
     */
    private FrameMetadata frame = new FrameMetadata();
    /**
     * Vertex information.
     */
    private Vertices vertices = new Vertices();
    /**
     * Edge information.
     */
    private Edges edges = new Edges();
    /**
     * Face information.
     */
    private Faces faces = new Faces();

    private List<List<Integer>> faceOrders = new ArrayList<>();
    private List<List<Integer>> edgeOrders = new ArrayList<>();

    @JsonUnwrapped(prefix = "frame_")
    public FrameMetadata getFrame() {
        return frame;
    }

    @JsonUnwrapped(prefix = "frame_")
    public void setFrame(FrameMetadata frame) {
        this.frame = frame;
    }

    @JsonUnwrapped(prefix = "edges_")
    public Edges getEdges() {
        return edges;
    }

    @JsonUnwrapped(prefix = "edges_")
    public void setEdges(Edges edges) {
        this.edges = edges;
    }

    @JsonUnwrapped(prefix = "vertices_")
    public Vertices getVertices() {
        return vertices;
    }

    @JsonUnwrapped(prefix = "vertices_")
    public void setVertices(Vertices vertices) {
        this.vertices = vertices;
    }

    @JsonUnwrapped(prefix = "faces_")
    public Faces getFaces() {
        return faces;
    }

    @JsonUnwrapped(prefix = "faces_")
    public void setFaces(Faces faces) {
        this.faces = faces;
    }

    public List<List<Integer>> getFaceOrders() {
        return faceOrders;
    }

    public void setFaceOrders(List<List<Integer>> faceOrders) {
        this.faceOrders = faceOrders;
    }

    public List<List<Integer>> getEdgeOrders() {
        return edgeOrders;
    }

    public void setEdgeOrders(List<List<Integer>> edgeOrders) {
        this.edgeOrders = edgeOrders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoldFrame foldFrame = (FoldFrame) o;
        return getFrame().equals(foldFrame.getFrame()) && getVertices().equals(foldFrame.getVertices()) && getEdges().equals(foldFrame.getEdges()) && getFaces().equals(foldFrame.getFaces()) && getFaceOrders().equals(foldFrame.getFaceOrders()) && getEdgeOrders().equals(foldFrame.getEdgeOrders());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFrame(), getVertices(), getEdges(), getFaces(), getFaceOrders(), getEdgeOrders());
    }
}
