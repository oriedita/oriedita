package fold.adapter;

import fold.model.Edge;
import fold.model.Face;
import fold.model.Vertex;
import fold.model.internal.InternalFoldFrame;
import fold.model.internal.frame.Edges;
import fold.model.internal.frame.Faces;
import fold.model.internal.frame.FrameMetadata;
import fold.model.internal.frame.Vertices;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FrameAdapter implements Adapter<InternalFoldFrame, fold.model.FoldFrame> {
    private final Adapter<Vertices, List<Vertex>> verticesAdapter;
    private final Adapter<Edges, List<Edge>> edgesAdapter;
    private final Adapter<Faces, List<Face>> facesAdapter;

    public FrameAdapter(Adapter<Vertices, List<Vertex>> verticesAdapter, Adapter<Edges, List<Edge>> edgesAdapter, Adapter<Faces, List<Face>> facesAdapter) {
        this.verticesAdapter = verticesAdapter;
        this.edgesAdapter = edgesAdapter;
        this.facesAdapter = facesAdapter;
    }

    @Override
    public fold.model.FoldFrame convert(InternalFoldFrame from) {

        fold.model.FoldFrame frame = new fold.model.FoldFrame();

        frame.setVertices(verticesAdapter.convert(from.getVertices()));
        frame.setEdges(edgesAdapter.convert(from.getEdges()));
        frame.setFaces(facesAdapter.convert(from.getFaces()));

        frame.setFrameClasses(from.getFrame().getClasses());
        frame.setFrameAuthor(from.getFrame().getAuthor());
        frame.setFrameDescription(from.getFrame().getDescription());
        frame.setFrameTitle(from.getFrame().getTitle());
        frame.setInherit(from.getFrame().getInherit());
        frame.setUnit(from.getFrame().getUnit());
        frame.setParent(from.getFrame().getParent());
        frame.setAttributes(from.getFrame().getAttributes());

        for (int i = 0; i < from.getVertices().getFaces().size(); i++) {
            Vertex vertex = frame.getVertices().get(i);

            for (int j = 0; j < from.getVertices().getFaces().get(i).size(); j++) {
                vertex.getFaces().add(frame.getFaces().get(from.getVertices().getFaces().get(i).get(j)));
            }
        }

        for (int i = 0; i < from.getEdges().getVertices().size(); i++) {
            int startId = from.getEdges().getVertices().get(i).get(0);
            int endId = from.getEdges().getVertices().get(i).get(1);
            frame.getEdges().get(i).setStart(frame.getVertices().get(startId));
            frame.getEdges().get(i).setEnd(frame.getVertices().get(endId));
        }

        for (int i = 0; i < from.getFaces().getEdges().size(); i++) {
            Face face = frame.getFaces().get(i);
            for (int j = 0; j < from.getFaces().getEdges().get(i).size(); j++) {
                face.getEdges().add(frame.getEdges().get(from.getFaces().getEdges().get(i).get(j)));
            }
            for (int j = 0; j < from.getFaces().getVertices().get(i).size(); j++) {
                face.getVertices().add(frame.getVertices().get(from.getFaces().getVertices().get(i).get(j)));
            }
        }

        for (int i = 0; i < from.getFaceOrders().size(); i++) {
            List<Integer> faceOrdersList = from.getFaceOrders().get(i);
            fold.model.FoldFrame.FaceOrder faceOrder = new fold.model.FoldFrame.FaceOrder();

            faceOrder.setFace1(frame.getFaces().get(faceOrdersList.get(0)));
            faceOrder.setFace2(frame.getFaces().get(faceOrdersList.get(1)));
            faceOrder.setFace1AboveFace2(convertOrder(faceOrdersList.get(2)));

            frame.getFaceOrders().add(faceOrder);
        }

        for (int i = 0; i < from.getEdgeOrders().size(); i++) {
            List<Integer> edgeOrdersList = from.getEdgeOrders().get(i);
            fold.model.FoldFrame.EdgeOrder edgeOrder = new fold.model.FoldFrame.EdgeOrder();

            edgeOrder.setEdge1(frame.getEdges().get(edgeOrdersList.get(0)));
            edgeOrder.setEdge2(frame.getEdges().get(edgeOrdersList.get(1)));
            edgeOrder.setEdge1AboveEdge2(convertOrder(edgeOrdersList.get(2)));

            frame.getEdgeOrders().add(edgeOrder);
        }

        return frame;
    }

    private static Boolean convertOrder(Integer order) {
        if (order == null || order == 0) {
            return null;
        }

        return order > 0;
    }

    @Override
    public InternalFoldFrame convertBack(fold.model.FoldFrame from) {
        InternalFoldFrame frame = new InternalFoldFrame();

        FrameMetadata frameMetadata = new FrameMetadata();

        frameMetadata.setTitle(from.getFrameTitle());
        frameMetadata.setAttributes(from.getAttributes());
        frameMetadata.setInherit(from.getInherit());
        frameMetadata.setParent(from.getParent());
        frameMetadata.setUnit(from.getUnit());
        frameMetadata.setAuthor(from.getFrameAuthor());
        frameMetadata.setClasses(from.getFrameClasses());
        frameMetadata.setDescription(from.getFrameDescription());

        frame.setFrame(frameMetadata);

        frame.setEdges(edgesAdapter.convertBack(from.getEdges()));
        frame.setFaces(facesAdapter.convertBack(from.getFaces()));
        frame.setVertices(verticesAdapter.convertBack(from.getVertices()));

        for (int i = 0; i < from.getFaceOrders().size(); i++) {
            fold.model.FoldFrame.FaceOrder faceOrder = from.getFaceOrders().get(i);

            frame.getFaceOrders().add(Arrays.asList(faceOrder.getFace1().getId(), faceOrder.getFace2().getId(), convertOrderBack(faceOrder.getFace1AboveFace2())));
        }

        for (int i = 0; i < from.getEdgeOrders().size(); i++) {
            fold.model.FoldFrame.EdgeOrder edgeOrder = from.getEdgeOrders().get(i);

            frame.getEdgeOrders().add(Arrays.asList(edgeOrder.getEdge1().getId(), edgeOrder.getEdge2().getId(), convertOrderBack(edgeOrder.getEdge1AboveEdge2())));
        }

        return frame;
    }

    private static int convertOrderBack(Boolean face1AboveFace2) {
        if (face1AboveFace2 ==  null) {
            return 0;
        }

        if (face1AboveFace2) {
            return 1;
        }

        return -1;
    }
}
