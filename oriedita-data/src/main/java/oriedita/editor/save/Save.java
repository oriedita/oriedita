package oriedita.editor.save;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.GridModel;
import oriedita.editor.drawing.tools.Camera;
import origami.data.save.LineSegmentSave;
import origami.data.save.PointSave;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, property = "@version", defaultImpl = BaseSave.class)
@JsonSubTypes({@Type(BaseSave.class), @Type(SaveV1_0.class), @Type(SaveV1_1.class)})
public interface Save extends PointSave, LineSegmentSave, TextSave, Serializable {
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
