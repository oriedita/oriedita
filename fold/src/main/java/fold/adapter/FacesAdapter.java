package fold.adapter;

import fold.model.Edge;
import fold.model.Face;
import fold.model.Vertex;
import fold.model.internal.frame.Faces;

import java.util.List;
import java.util.stream.Collectors;

public class FacesAdapter implements Adapter<Faces, List<Face>> {
    @Override
    public List<Face> convert(Faces from, List<Face> faces) {
        for (int i = 0; i < from.getVertices().size(); i++) {
            Face face = new Face();

            face.setId(i);

            faces.add(face);
            // Get vertices and edges in FrameAdapter
        }

        return faces;
    }

    @Override
    public Faces convertBack(List<Face> from, Faces faces) {
        for (Face face : from) {
            if (face.getEdges().size() > 0) {
                faces.getEdges().add(face.getEdges().stream().map(Edge::getId).collect(Collectors.toList()));
            }
            if (face.getVertices().size() > 0) {
                faces.getVertices().add(face.getVertices().stream().map(Vertex::getId).collect(Collectors.toList()));
            }
        }

        return faces;
    }
}
