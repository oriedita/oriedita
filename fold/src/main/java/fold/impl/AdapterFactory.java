package fold.impl;

import fold.adapter.*;
import fold.model.*;
import fold.model.internal.InternalFoldFile;
import fold.model.internal.InternalFoldFrame;
import fold.model.internal.frame.Edges;
import fold.model.internal.frame.Faces;
import fold.model.internal.frame.Vertices;

import java.util.List;

public class AdapterFactory {
    public static Adapter<Edges, List<Edge>> getEdgesAdapter() {
        return new EdgeAdapter();
    }

    public static Adapter<Faces, List<Face>> getFacesAdapter() {
        return new FacesAdapter();
    }

    public static Adapter<Vertices, List<Vertex>> getVerticesAdapter() {
        return new VerticesAdapter();
    }

    public static Adapter<InternalFoldFrame, FoldFrame> getFoldFrameAdapter() {
        return new FrameAdapter(getVerticesAdapter(), getEdgesAdapter(), getFacesAdapter());
    }

    public static Adapter<InternalFoldFile, FoldFile> getFoldFileAdapter() {
        return new FoldFileAdapter<>(InternalFoldFile.class, FoldFile.class, getFoldFrameAdapter());
    }

    public static <T extends InternalFoldFile, V extends FoldFile> Adapter<T, V> getCustomFoldFileAdapter(Class<T> tClass, Class<V> vClass) {
        return new FoldFileAdapter<>(tClass, vClass, getFoldFrameAdapter());
    }
}
