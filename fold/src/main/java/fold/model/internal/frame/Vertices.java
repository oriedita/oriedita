package fold.model.internal.frame;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Vertex information : vertices_...
 * <p>
 * The values of the following properties are zero-indexed arrays by vertex ID.
 */
public class Vertices {
    /**
     * For each vertex, an array of coordinates, such as [x, y, z] or [x, y] (where z is implicitly zero).
     * In higher dimensions, all unspecified coordinates are implicitly zero. Recommended except for
     * frames with attribute "abstract".
     */
    private List<List<Double>> coords = new ArrayList<>();
    /**
     * For each vertex, an array of vertices (vertex IDs) that are adjacent along edges. If the frame represents
     * an orientable manifold or planar linkage, this list should be ordered counterclockwise around the
     * vertex (possibly repeating a vertex more than once). If the frame is a nonorientable manifold, this
     * list should be cyclicly ordered around the vertex (possibly repeating a vertex). Otherwise, the order
     * is arbitrary. Recommended in any frame lacking edges_vertices property (otherwise vertices_vertices
     * can easily be computed from edges_vertices as needed).
     */
    private List<List<Integer>> vertices = new ArrayList<>();
    /**
     * For each vertex, an array of face IDs for the faces incident to the vertex. If the frame represents an
     * orientable manifold, this list should be ordered counterclockwise around the vertex (possibly repeating
     * a face more than once). If the frame is a nonorientable manifold, this list should be cyclicly ordered
     * around the vertex (possibly repeating a vertex), and matching the cyclic order of vertices_vertices
     * (if both are specified). In addition to the matching cyclic order, vertices_vertices and vertices_faces
     * should align in start so that vertices_faces[v][i] contains vertices vertices_vertices[v][i] and
     * vertices_vertices[v][(i+1)%d] where d is the degree of vertex v.
     */
    private List<List<Integer>> faces = new ArrayList<>();

    public List<List<Double>> getCoords() {
        return coords;
    }

    public void setCoords(List<List<Double>> coords) {
        this.coords = coords;
    }

    public List<List<Integer>> getVertices() {
        return vertices;
    }

    public void setVertices(List<List<Integer>> vertices) {
        this.vertices = vertices;
    }

    public List<List<Integer>> getFaces() {
        return faces;
    }

    public void setFaces(List<List<Integer>> faces) {
        this.faces = faces;
    }
}
