package fold.adapter;

import fold.model.Edge;
import fold.model.Face;
import fold.model.Vertex;
import fold.model.internal.frame.Faces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FacesAdapter implements Adapter<Faces, List<Face>> {
    @Override
    public List<Face> convert(Faces from) {
        List<Face> faces = new ArrayList<>();

        for (int i = 0; i < from.getVertices().size(); i++) {
            Face face = new Face();

            face.setId(i);

            faces.add(face);
            // Get vertices and edges in FrameAdapter
        }

        return faces;
    }

    @Override
    public Faces convertBack(List<Face> from) {
        Faces faces = new Faces();

        for (Face face : from) {
            faces.getEdges().add(face.getEdges().stream().map(Edge::getId).collect(Collectors.toList()));
            faces.getVertices().add(face.getVertices().stream().map(Vertex::getId).collect(Collectors.toList()));
        }

        return faces;
    }
}
