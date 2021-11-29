package fold.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import fold.model.frame.Edges;
import fold.model.frame.Faces;
import fold.model.frame.FrameMetadata;
import fold.model.frame.Vertices;

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

    private int[][] faceOrders;
    private int[][] edgeOrders;

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

    public int[][] getFaceOrders() {
        return faceOrders;
    }

    public void setFaceOrders(int[][] faceOrders) {
        this.faceOrders = faceOrders;
    }

    public int[][] getEdgeOrders() {
        return edgeOrders;
    }

    public void setEdgeOrders(int[][] edgeOrders) {
        this.edgeOrders = edgeOrders;
    }
}
