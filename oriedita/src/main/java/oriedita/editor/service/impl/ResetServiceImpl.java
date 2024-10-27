package oriedita.editor.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.CameraModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.databinding.GridModel;
import oriedita.editor.databinding.InternalDivisionRatioModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.service.ResetService;

@ApplicationScoped
public class ResetServiceImpl implements ResetService {
    private final CreasePattern_Worker mainCreasePatternWorker;
    private final Camera creasePatternCamera;
    private final CanvasModel canvasModel;
    private final InternalDivisionRatioModel internalDivisionRatioModel;
    private final FoldedFigureModel foldedFigureModel;
    private final GridModel gridModel;
    private final AngleSystemModel angleSystemModel;
    private final CameraModel creasePatternCameraModel;
    private final FoldedFiguresList foldedFiguresList;
    private final ApplicationModel applicationModel;

    @Inject
    public ResetServiceImpl(@Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker,
                            @Named("creasePatternCamera") Camera creasePatternCamera,
                            CanvasModel canvasModel,
                            InternalDivisionRatioModel internalDivisionRatioModel,
                            FoldedFigureModel foldedFigureModel,
                            GridModel gridModel,
                            AngleSystemModel angleSystemModel,
                            CameraModel creasePatternCameraModel,
                            FoldedFiguresList foldedFiguresList,
                            ApplicationModel applicationModel) {
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.creasePatternCamera = creasePatternCamera;
        this.canvasModel = canvasModel;
        this.internalDivisionRatioModel = internalDivisionRatioModel;
        this.foldedFigureModel = foldedFigureModel;
        this.gridModel = gridModel;
        this.angleSystemModel = angleSystemModel;
        this.creasePatternCameraModel = creasePatternCameraModel;
        this.foldedFiguresList = foldedFiguresList;
        this.applicationModel = applicationModel;
    }

    public void developmentView_initialization() {
        mainCreasePatternWorker.reset();
        mainCreasePatternWorker.initialize();

        //camera_of_orisen_nyuuryokuzu	の設定;
        creasePatternCamera.setCameraPositionX(0.0);
        creasePatternCamera.setCameraPositionY(0.0);
        creasePatternCamera.setCameraAngle(0.0);
        creasePatternCamera.setCameraMirror(1.0);
        creasePatternCamera.setCameraZoomX(1.0);
        creasePatternCamera.setCameraZoomY(1.0);
        creasePatternCamera.setDisplayPositionX(350.0);
        creasePatternCamera.setDisplayPositionY(350.0);

        mainCreasePatternWorker.setCamera(creasePatternCamera);

        canvasModel.reset();
        internalDivisionRatioModel.reset();
        foldedFigureModel.reset();

        gridModel.reset();
        gridModel.setGridSize(applicationModel.getDefaultGridSize());
        angleSystemModel.reset();
        creasePatternCameraModel.reset();

        foldedFiguresList.removeAllElements();
    }

    @Override
    public void Button_shared_operation() {
        mainCreasePatternWorker.resetLineStep(0);
        mainCreasePatternWorker.resetCircleStep();
        // TODO Reset Voronoi

        canvasModel.markDirty();
    }
}
