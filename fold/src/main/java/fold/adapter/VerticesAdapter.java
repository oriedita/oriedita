package fold.adapter;

import fold.model.Face;
import fold.model.Vertex;
import fold.model.internal.frame.Vertices;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VerticesAdapter implements Adapter<Vertices, List<Vertex>> {
    @Override
    public List<Vertex> convert(Vertices from) {
        List<Vertex> vertices = new ArrayList<>();

        int id = 0;

        for (List<Double> coords : from.getCoords()) {
            Vertex vertex = new Vertex();

            vertex.setId(id++);

            vertex.setX(coords.get(0));
            vertex.setY(coords.get(1));

            if (coords.size() > 2) {
                vertex.setZ(coords.get(2));
            }

            vertices.add(vertex);
        }

        if (from.getVertices().size() > 0) {
            for (int i = 0; i < vertices.size(); i++) {
                List<Vertex> vertexList = new ArrayList<>();
                for (int j = 0; j < from.getVertices().get(i).size(); j++) {
                    vertexList.add(vertices.get(from.getVertices().get(i).get(j)));
                }

                vertices.get(i).setVertices(vertexList);
            }
        }

        return vertices;
    }

    @Override
    public Vertices convertBack(List<Vertex> from) {
        Vertices vertices = new Vertices();

        for (Vertex vertex : from) {
            if (vertex.getX() != null && vertex.getY() != null) {
                List<Double> coords = new ArrayList<>();
                coords.add(vertex.getX());
                coords.add(vertex.getY());

                if (vertex.getZ() != null) {
                    coords.add(vertex.getZ());
                }
                vertices.getCoords().add(coords);
            }
            if (vertex.getFaces().size() > 0) {
                vertices.getFaces().add(vertex.getFaces().stream().map(Face::getId).collect(Collectors.toList()));
            }
            if (vertex.getVertices().size() > 0) {
                vertices.getVertices().add(vertex.getVertices().stream().map(Vertex::getId).collect(Collectors.toList()));
            }
        }

        return vertices;
    }
}
