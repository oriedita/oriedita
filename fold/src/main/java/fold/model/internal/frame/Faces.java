package fold.model.internal.frame;

import java.util.ArrayList;
import java.util.List;

public class Faces {
    /**
     * For each face, an array of vertex IDs for the vertices around the face in counterclockwise order. This array
     * can repeat the same vertex multiple times (e.g., if the face has a "slit" in it). Recommended in any
     * frame having faces.
     */
    List<List<Integer>> vertices = new ArrayList<>();
    /**
     * For each face, an array of edge IDs for the edges around the face in counterclockwise order. In addition to
     * the matching cyclic order, faces_vertices and faces_edges should align in start so that faces_edges[f][i] is
     * the edge connecting faces_vertices[f][i] and faces_vertices[f][(i+1)%d] where d is the degree of face f.
     */
    List<List<Integer>> edges = new ArrayList<>();

    public List<List<Integer>> getVertices() {
        return vertices;
    }

    public void setVertices(List<List<Integer>> vertices) {
        this.vertices = vertices;
    }

    public List<List<Integer>> getEdges() {
        return edges;
    }

    public void setEdges(List<List<Integer>> edges) {
        this.edges = edges;
    }
}
