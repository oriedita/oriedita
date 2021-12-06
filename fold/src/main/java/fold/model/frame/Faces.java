package fold.model.frame;

public class Faces {
    /**
     * For each face, an array of vertex IDs for the vertices around the face in counterclockwise order. This array
     * can repeat the same vertex multiple times (e.g., if the face has a "slit" in it). Recommended in any
     * frame having faces.
     */
    int[][] vertices;
    /**
     * For each face, an array of edge IDs for the edges around the face in counterclockwise order. In addition to
     * the matching cyclic order, faces_vertices and faces_edges should align in start so that faces_edges[f][i] is
     * the edge connecting faces_vertices[f][i] and faces_vertices[f][(i+1)%d] where d is the degree of face f.
     */
    int[][] edges;

    public int[][] getVertices() {
        return vertices;
    }

    public void setVertices(int[][] vertices) {
        this.vertices = vertices;
    }

    public int[][] getEdges() {
        return edges;
    }

    public void setEdges(int[][] edges) {
        this.edges = edges;
    }
}
