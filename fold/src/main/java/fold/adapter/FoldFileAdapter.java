package fold.adapter;

import fold.FoldFileFormatException;
import fold.model.internal.FoldFile;
import fold.model.internal.FoldFrame;
import fold.model.internal.file.FileMetadata;

import java.util.Map;
import java.util.stream.Collectors;

public class FoldFileAdapter implements Adapter<FoldFile, fold.model.FoldFile> {
    @Override
    public fold.model.FoldFile convert(FoldFile from) {
        fold.model.FoldFile foldFile = new fold.model.FoldFile();

        fold.model.FoldFrame frame = new FrameAdapter().convert(from);

        foldFile.setAttributes(frame.getAttributes());
        foldFile.setEdgeOrders(frame.getEdgeOrders());
        foldFile.setEdges(frame.getEdges());
        foldFile.setFaceOrders(frame.getFaceOrders());
        foldFile.setFaces(frame.getFaces());
        foldFile.setVertices(frame.getVertices());


        foldFile.setTitle(from.getFile().getTitle());
        foldFile.setAuthor(from.getFile().getAuthor());
        foldFile.setSpec(from.getFile().getSpec());
        foldFile.setDescription(from.getFile().getDescription());
        foldFile.setClasses(from.getFile().getClasses());
        foldFile.setCreator(from.getFile().getCreator());

        foldFile.setFrameClasses(from.getFrame().getClasses());
        foldFile.setFrameAuthor(from.getFrame().getAuthor());
        foldFile.setFrameDescription(from.getFrame().getDescription());
        foldFile.setFrameTitle(from.getFrame().getTitle());
        foldFile.setInherit(from.getFrame().getInherit());
        foldFile.setUnit(from.getFrame().getUnit());
        foldFile.setParent(from.getFrame().getParent());
        foldFile.setAttributes(from.getFrame().getAttributes());


        foldFile.setFrames(from.getFile().getFrames().stream().map(f -> new FrameAdapter().convert(f)).collect(Collectors.toList()));

        for (Map.Entry<String, Object> entry : from.getCustomPropertyMap().entrySet()) {
            foldFile.addCustomProperty(entry.getKey(), entry.getValue());
        }

        return foldFile;
    }

    @Override
    public FoldFile convertBack(fold.model.FoldFile from) {
        FoldFile foldFile = new FoldFile();

        FoldFrame frame = new FrameAdapter().convertBack(from);

        foldFile.setEdgeOrders(frame.getEdgeOrders());
        foldFile.setEdges(frame.getEdges());
        foldFile.setFaces(frame.getFaces());
        foldFile.setVertices(frame.getVertices());
        foldFile.setFaceOrders(frame.getFaceOrders());
        foldFile.setFrame(frame.getFrame());

        foldFile.getFrame().setTitle(from.getFrameTitle());
        foldFile.getFrame().setAttributes(from.getAttributes());
        foldFile.getFrame().setInherit(from.getInherit());
        foldFile.getFrame().setParent(from.getParent());
        foldFile.getFrame().setUnit(from.getUnit());
        foldFile.getFrame().setAuthor(from.getFrameAuthor());
        foldFile.getFrame().setClasses(from.getFrameClasses());
        foldFile.getFrame().setDescription(from.getFrameDescription());

        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setTitle(from.getTitle());
        fileMetadata.setAuthor(from.getAuthor());
        fileMetadata.setClasses(from.getClasses());
        fileMetadata.setDescription(from.getDescription());
        fileMetadata.setCreator(from.getCreator());
        fileMetadata.setSpec(from.getSpec());
        fileMetadata.setFrames(from.getFrames().stream().map(f -> new FrameAdapter().convertBack(f)).collect(Collectors.toList()));
        foldFile.setFile(fileMetadata);

        for (Map.Entry<String, Object> entry : from.getCustomPropertyMap().entrySet()) {
            try {
                foldFile.setCustomProperty(entry.getKey(), entry.getValue());
            } catch (FoldFileFormatException e) {
                throw new RuntimeException(e);
            }
        }

        return foldFile;
    }
}
