package fold.adapter;

import fold.FoldFileFormatException;
import fold.model.FoldFile;
import fold.model.FoldFrame;
import fold.model.internal.InternalFoldFile;
import fold.model.internal.InternalFoldFrame;
import fold.model.internal.file.FileMetadata;

import java.util.Map;
import java.util.stream.Collectors;

public class FoldFileAdapter<T extends InternalFoldFile, V extends FoldFile> implements Adapter<T, V> {
    private final Adapter<InternalFoldFrame, FoldFrame> frameAdapter;

    public FoldFileAdapter(Adapter<InternalFoldFrame, FoldFrame> frameAdapter) {
        this.frameAdapter = frameAdapter;
    }

    @Override
    public V convert(T from, V foldFile) {
        FoldFrame frame = frameAdapter.convert(from, new FoldFrame());

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


        foldFile.setFrames(from.getFile().getFrames().stream().map((InternalFoldFrame to) -> frameAdapter.convert(to, new FoldFrame())).collect(Collectors.toList()));

        for (Map.Entry<String, Object> entry : from.getCustomPropertyMap().entrySet()) {
            foldFile.getCustomPropertyMap().put(entry.getKey(), entry.getValue());
        }

        return foldFile;
    }

    @Override
    public T convertBack(V from, T internalFoldFile) {

        InternalFoldFrame frame = frameAdapter.convertBack(from, new InternalFoldFrame());

        internalFoldFile.setEdgeOrders(frame.getEdgeOrders());
        internalFoldFile.setEdges(frame.getEdges());
        internalFoldFile.setFaces(frame.getFaces());
        internalFoldFile.setVertices(frame.getVertices());
        internalFoldFile.setFaceOrders(frame.getFaceOrders());
        internalFoldFile.setFrame(frame.getFrame());

        internalFoldFile.getFrame().setTitle(from.getFrameTitle());
        internalFoldFile.getFrame().setAttributes(from.getAttributes());
        internalFoldFile.getFrame().setInherit(from.getInherit());
        internalFoldFile.getFrame().setParent(from.getParent());
        internalFoldFile.getFrame().setUnit(from.getUnit());
        internalFoldFile.getFrame().setAuthor(from.getFrameAuthor());
        internalFoldFile.getFrame().setClasses(from.getFrameClasses());
        internalFoldFile.getFrame().setDescription(from.getFrameDescription());

        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setTitle(from.getTitle());
        fileMetadata.setAuthor(from.getAuthor());
        fileMetadata.setClasses(from.getClasses());
        fileMetadata.setDescription(from.getDescription());
        fileMetadata.setCreator(from.getCreator());
        fileMetadata.setSpec(from.getSpec());
        fileMetadata.setFrames(from.getFrames().stream().map((FoldFrame to) -> frameAdapter.convertBack(to, new InternalFoldFrame())).collect(Collectors.toList()));
        internalFoldFile.setFile(fileMetadata);

        for (Map.Entry<String, Object> entry : from.getCustomPropertyMap().entrySet()) {
            try {
                internalFoldFile.setCustomProperty(entry.getKey(), entry.getValue());
            } catch (FoldFileFormatException e) {
                throw new RuntimeException(e);
            }
        }

        return internalFoldFile;
    }
}
