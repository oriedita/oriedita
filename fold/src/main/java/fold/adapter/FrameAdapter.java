package fold.adapter;

import fold.model.Face;
import fold.model.internal.FoldFrame;
import fold.model.internal.frame.FrameMetadata;

public class FrameAdapter implements Adapter<FoldFrame, fold.model.FoldFrame> {
    @Override
    public fold.model.FoldFrame convert(FoldFrame from) {

        fold.model.FoldFrame frame = new fold.model.FoldFrame();

        frame.setVertices(new VerticesAdapter().convert(from.getVertices()));
        frame.setEdges(new EdgeAdapter().convert(from.getEdges()));
        frame.setFaces(new FacesAdapter().convert(from.getFaces()));

        frame.setFrameClasses(from.getFrame().getClasses());
        frame.setFrameAuthor(from.getFrame().getAuthor());
        frame.setFrameDescription(from.getFrame().getDescription());
        frame.setFrameTitle(from.getFrame().getTitle());
        frame.setInherit(from.getFrame().getInherit());
        frame.setUnit(from.getFrame().getUnit());
        frame.setParent(from.getFrame().getParent());
        frame.setAttributes(from.getFrame().getAttributes());

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

        return frame;
    }

    @Override
    public FoldFrame convertBack(fold.model.FoldFrame from) {
        FoldFrame frame = new FoldFrame();

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

        frame.setEdges(new EdgeAdapter().convertBack(from.getEdges()));
        frame.setFaces(new FacesAdapter().convertBack(from.getFaces()));
        frame.setVertices(new VerticesAdapter().convertBack(from.getVertices()));

        return frame;
    }
}
