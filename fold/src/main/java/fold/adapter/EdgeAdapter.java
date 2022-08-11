package fold.adapter;

import fold.model.Edge;
import fold.model.internal.frame.Edges;

import java.util.Arrays;
import java.util.List;

public class EdgeAdapter implements Adapter<Edges, List<Edge>> {
    @Override
    public List<Edge> convert(Edges from, List<Edge> edges) {
        for (int i = 0; i < from.getAssignment().size(); i++) {
            Edge edge = new Edge();

            edge.setId(i);
            if (from.getAssignment().size() > i) {
                edge.setAssignment(from.getAssignment().get(i));
            }
            if (from.getFoldAngle().size() > i) {
                edge.setFoldAngle(from.getFoldAngle().get(i));
            }
            if (from.getLength().size() > i) {
                edge.setLength(from.getLength().get(i));
            }

            edges.add(edge);
        }

        return edges;
    }

    @Override
    public Edges convertBack(List<Edge> from, Edges edges) {
        for (Edge edge : from) {
            if (edge.getAssignment() != null) {
                edges.getAssignment().add(edge.getAssignment());
            }
            if (edge.getFoldAngle() != null) {
                edges.getFoldAngle().add(edge.getFoldAngle());
            }
            if (edge.getLength() != null) {
                edges.getLength().add(edge.getLength());
            }
            if (edge.getStart() != null && edge.getEnd() != null) {
                edges.getVertices().add(Arrays.asList(edge.getStart().getId(), edge.getEnd().getId()));
            }
        }

        return edges;
    }
}
