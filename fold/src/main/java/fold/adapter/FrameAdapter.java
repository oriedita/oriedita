package fold.adapter;

import fold.model.Edge;
import fold.model.Face;
import fold.model.FoldFrame;
import fold.model.Vertex;
import fold.model.internal.InternalFoldFrame;
import fold.model.internal.frame.Edges;
import fold.model.internal.frame.Faces;
import fold.model.internal.frame.FrameMetadata;
import fold.model.internal.frame.Vertices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FrameAdapter implements Adapter<InternalFoldFrame, FoldFrame> {
    private final Adapter<Vertices, List<Vertex>> verticesAdapter;
    private final Adapter<Edges, List<Edge>> edgesAdapter;
    private final Adapter<Faces, List<Face>> facesAdapter;

    public FrameAdapter(Adapter<Vertices, List<Vertex>> verticesAdapter, Adapter<Edges, List<Edge>> edgesAdapter, Adapter<Faces, List<Face>> facesAdapter) {
        this.verticesAdapter = verticesAdapter;
        this.edgesAdapter = edgesAdapter;
        this.facesAdapter = facesAdapter;
    }

    @Override
    public FoldFrame convert(InternalFoldFrame from, FoldFrame frame) {
        frame.setVertices(verticesAdapter.convert(from.getVertices(), new ArrayList<>()));
        frame.setEdges(edgesAdapter.convert(from.getEdges(), new ArrayList<>()));
        frame.setFaces(facesAdapter.convert(from.getFaces(), new ArrayList<>()));

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
            FoldFrame.FaceOrder faceOrder = new FoldFrame.FaceOrder();

            faceOrder.setFace1(frame.getFaces().get(faceOrdersList.get(0)));
            faceOrder.setFace2(frame.getFaces().get(faceOrdersList.get(1)));
            faceOrder.setFace1AboveFace2(convertOrder(faceOrdersList.get(2)));

            frame.getFaceOrders().add(faceOrder);
        }

        for (int i = 0; i < from.getEdgeOrders().size(); i++) {
            List<Integer> edgeOrdersList = from.getEdgeOrders().get(i);
            FoldFrame.EdgeOrder edgeOrder = new FoldFrame.EdgeOrder();

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
    public InternalFoldFrame convertBack(FoldFrame from, InternalFoldFrame frame) {
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

        frame.setEdges(edgesAdapter.convertBack(from.getEdges(), new Edges()));
        frame.setFaces(facesAdapter.convertBack(from.getFaces(), new Faces()));
        frame.setVertices(verticesAdapter.convertBack(from.getVertices(), new Vertices()));

        for (int i = 0; i < from.getFaceOrders().size(); i++) {
            FoldFrame.FaceOrder faceOrder = from.getFaceOrders().get(i);

            frame.getFaceOrders().add(Arrays.asList(faceOrder.getFace1().getId(), faceOrder.getFace2().getId(), convertOrderBack(faceOrder.getFace1AboveFace2())));
        }

        for (int i = 0; i < from.getEdgeOrders().size(); i++) {
            FoldFrame.EdgeOrder edgeOrder = from.getEdgeOrders().get(i);

            frame.getEdgeOrders().add(Arrays.asList(edgeOrder.getEdge1().getId(), edgeOrder.getEdge2().getId(), convertOrderBack(edgeOrder.getEdge1AboveEdge2())));
        }

        return frame;
    }

    private static int convertOrderBack(Boolean face1AboveFace2) {
        if (face1AboveFace2 == null) {
            return 0;
        }

        if (face1AboveFace2) {
            return 1;
        }

        return -1;
    }
}
