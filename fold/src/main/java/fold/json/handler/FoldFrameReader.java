package fold.json.handler;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.jr.ob.JSONObjectException;
import com.fasterxml.jackson.jr.ob.api.ValueReader;
import com.fasterxml.jackson.jr.ob.impl.JSONReader;
import fold.FoldFileFormatException;
import fold.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FoldFrameReader extends ValueReader {
    public FoldFrameReader() {
        super(FoldFrame.class);
    }

    @Override
    public Object read(JSONReader reader, JsonParser p) throws IOException {
        FoldFrame frame = new FoldFrame();

        String fieldName;

        FoldFrameFactory foldFrameFactory = new FoldFrameFactory(frame);

        while ((fieldName = p.nextFieldName()) != null) {
            foldFrameFactory.readField(fieldName, reader, p);
        }

        foldFrameFactory.postProcess();

        return frame;
    }

    public static class FoldFrameFactory {
        private final FoldFrame instance;
        List<List<Double>> vertices_coords = new ArrayList<>();
        List<List<Integer>> vertices_vertices = new ArrayList<>();
        List<List<Integer>> vertices_faces = new ArrayList<>();

        List<List<Integer>> edges_vertices = new ArrayList<>();
        List<List<Integer>> edges_faces = new ArrayList<>();
        List<String> edges_assignment = new ArrayList<>();
        List<Double> edges_foldAngle = new ArrayList<>();
        List<Double> edges_length = new ArrayList<>();

        List<List<Integer>> faces_vertices = new ArrayList<>();
        List<List<Integer>> faces_edges = new ArrayList<>();

        List<List<Integer>> edgeOrders = new ArrayList<>();
        List<List<Integer>> faceOrders = new ArrayList<>();

        public FoldFrameFactory(FoldFrame instance) {
            this.instance = instance;
        }

        public void readField(String fieldName, JSONReader reader, JsonParser p) throws IOException {
            switch (fieldName) {
                case "frame_author":
                    instance.setFrameAuthor(p.nextTextValue());
                    break;
                case "frame_title":
                    instance.setFrameTitle(p.nextTextValue());
                    break;
                case "frame_description":
                    instance.setFrameDescription(p.nextTextValue());
                    break;
                case "frame_classes":
                    p.nextToken();
                    instance.setFrameClasses(reader.readListOf(String.class));
                    break;
                case "frame_attributes":
                    p.nextToken();
                    instance.setAttributes(reader.readListOf(String.class));
                    break;
                case "frame_unit":
                    instance.setUnit(p.nextTextValue());
                    break;
                case "vertices_coords":
                    vertices_coords = readListListOf(Double.class, reader, p);
                    break;
                case "vertices_vertices":
                    vertices_vertices = readListListOf(Integer.class, reader, p);
                    break;
                case "vertices_faces":
                    vertices_faces = readListListOf(Integer.class, reader, p);
                    break;
                case "edges_vertices":
                    edges_vertices = readListListOf(Integer.class, reader, p);
                    break;
                case "edges_faces":
                    edges_faces = readListListOf(Integer.class, reader, p);
                    break;
                case "edges_assignment":
                    p.nextToken();
                    edges_assignment = reader.readListOf(String.class);
                    break;
                case "edges_foldAngle":
                    p.nextToken();
                    edges_foldAngle = reader.readListOf(Double.class);
                    break;
                case "edges_length":
                    p.nextToken();
                    edges_length = reader.readListOf(Double.class);
                    break;
                case "faces_vertices":
                    faces_vertices = readListListOf(Integer.class, reader, p);
                    break;
                case "faces_edges":
                    faces_edges = readListListOf(Integer.class, reader, p);
                    break;
                case "edgeOrders":
                    edgeOrders = readListListOf(Integer.class, reader, p);
                    break;
                case "faceOrders":
                    faceOrders = readListListOf(Integer.class, reader, p);
                    break;
                default:
                    throw new FoldFileFormatException("Field \"" + fieldName + "\" not valid");
            }
        }

        public void postProcess() throws FoldFileFormatException {
            if (vertices_coords == null || vertices_vertices == null) {
                throw new FoldFileFormatException("vertices_coords and vertices_vertices are needed");
            }

            instance.setVertices(convertVertices(vertices_coords, vertices_vertices, new ArrayList<>()));
            instance.setEdges(convertEdges(edges_assignment, edges_foldAngle, edges_length, new ArrayList<>()));
            instance.setFaces(convertFaces(faces_vertices, new ArrayList<>()));

            for (int i = 0; i < vertices_faces.size(); i++) {
                Vertex vertex = instance.getVertices().get(i);

                for (int j = 0; j < vertices_faces.get(i).size(); j++) {
                    vertex.getFaces().add(instance.getFaces().get(vertices_faces.get(i).get(j)));
                }
            }

            for (int i = 0; i < edges_vertices.size(); i++) {
                int startId = edges_vertices.get(i).get(0);
                int endId = edges_vertices.get(i).get(1);
                instance.getEdges().get(i).setStart(instance.getVertices().get(startId));
                instance.getEdges().get(i).setEnd(instance.getVertices().get(endId));
            }

            for (int i = 0; i < faces_edges.size(); i++) {
                Face face = instance.getFaces().get(i);
                for (int j = 0; j < faces_edges.get(i).size(); j++) {
                    face.getEdges().add(instance.getEdges().get(faces_edges.get(i).get(j)));
                }
                for (int j = 0; j < faces_vertices.get(i).size(); j++) {
                    face.getVertices().add(instance.getVertices().get(faces_vertices.get(i).get(j)));
                }
            }

            for (List<Integer> faceOrdersList : faceOrders) {
                FoldFrame.FaceOrder faceOrder = new FoldFrame.FaceOrder();

                faceOrder.setFace1(instance.getFaces().get(faceOrdersList.get(0)));
                faceOrder.setFace2(instance.getFaces().get(faceOrdersList.get(1)));
                faceOrder.setFace1AboveFace2(convertOrder(faceOrdersList.get(2)));

                instance.getFaceOrders().add(faceOrder);
            }

            for (List<Integer> edgeOrdersList : edgeOrders) {
                FoldFrame.EdgeOrder edgeOrder = new FoldFrame.EdgeOrder();

                edgeOrder.setEdge1(instance.getEdges().get(edgeOrdersList.get(0)));
                edgeOrder.setEdge2(instance.getEdges().get(edgeOrdersList.get(1)));
                edgeOrder.setEdge1AboveEdge2(convertOrder(edgeOrdersList.get(2)));

                instance.getEdgeOrders().add(edgeOrder);
            }
        }
    }

    private static <T> List<List<T>> readListListOf(Class<T> type, JSONReader reader, JsonParser p) throws IOException {
        JsonToken t = p.nextToken();
        if (t == JsonToken.VALUE_NULL) {
            return null;
        }
        if (t != JsonToken.START_ARRAY) {
            throw JSONObjectException.from(p,
                    "Can not read a List: expect to see START_ARRAY ('['), instead got: " + ValueReader._tokenDesc(p));
        }

        if (p.nextToken() == JsonToken.END_ARRAY) {
            return Collections.emptyList();
        }
        List<T> value = reader.readListOf(type);
        if (p.nextToken() == JsonToken.END_ARRAY) {
            return Collections.singletonList(value);
        }
        List<List<T>> l = new ArrayList<>();
        l.add(value);
        do {
            l.add(reader.readListOf(type));
        } while (p.nextToken() != JsonToken.END_ARRAY);
        return l;

    }

    private static List<Vertex> convertVertices(
            List<List<Double>> vertices_coords,
            List<List<Integer>> vertices_vertices,
            List<Vertex> vertices
    ) {
        int id = 0;

        for (List<Double> coords : vertices_coords) {
            Vertex vertex = new Vertex();

            vertex.setId(id++);

            vertex.setX(coords.get(0));
            vertex.setY(coords.get(1));

            if (coords.size() > 2) {
                vertex.setZ(coords.get(2));
            }

            vertices.add(vertex);
        }

        if (vertices_vertices.size() > 0) {
            for (int i = 0; i < vertices.size(); i++) {
                List<Vertex> vertexList = new ArrayList<>();
                for (int j = 0; j < vertices_vertices.get(i).size(); j++) {
                    vertexList.add(vertices.get(vertices_vertices.get(i).get(j)));
                }

                vertices.get(i).setVertices(vertexList);
            }
        }

        return vertices;
    }

    private static List<Edge> convertEdges(
            List<String> edges_assignment,
            List<Double> edges_foldAngle,
            List<Double> edges_length,
            List<Edge> edges
    ) {
        for (int i = 0; i < edges_assignment.size(); i++) {
            Edge edge = new Edge();

            edge.setId(i);
            if (edges_assignment.size() > i) {
                edge.setAssignment(FoldEdgeAssignment.of(edges_assignment.get(i)));
            }
            if (edges_foldAngle.size() > i) {
                edge.setFoldAngle(edges_foldAngle.get(i));
            }
            if (edges_length.size() > i) {
                edge.setLength(edges_length.get(i));
            }

            edges.add(edge);
        }

        return edges;
    }

    private static List<Face> convertFaces(
            List<List<Integer>> faces_vertices,
            List<Face> faces
    ) {
        for (int i = 0; i < faces_vertices.size(); i++) {
            Face face = new Face();

            face.setId(i);

            faces.add(face);
            // Get vertices and edges in FrameAdapter
        }

        return faces;
    }

    private static Boolean convertOrder(Integer order) {
        if (order == null || order == 0) {
            return null;
        }

        return order > 0;
    }
}
