package origami_editor.editor.save;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.data.save.LineSegmentSave;
import origami.data.save.PointSave;
import origami_editor.editor.databinding.ApplicationModel;
import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.databinding.FoldedFigureModel;
import origami_editor.editor.databinding.GridModel;
import origami_editor.editor.drawing.tools.Camera;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, property = "@version", defaultImpl = SaveV1.class)
@JsonSubTypes({@Type(SaveV1.class)})
public interface Save extends PointSave, LineSegmentSave, Serializable {
    ApplicationModel getApplicationModel();
    void setApplicationModel(ApplicationModel applicationModel);

    Camera getCreasePatternCamera();
    void setCreasePatternCamera(Camera creasePatternCamera);

    CanvasModel getCanvasModel();
    void setCanvasModel(CanvasModel canvasModel);

    GridModel getGridModel();
    void setGridModel(GridModel gridModel);

    FoldedFigureModel getFoldedFigureModel();
    void setFoldedFigureModel(FoldedFigureModel foldedFigureModel);

    void set(Save save);
    void add(Save save);

    /**
     * Returns if this save contains lines which are not savable to a .cp file without losing information.
     */
    boolean canSaveAsCp();
}
