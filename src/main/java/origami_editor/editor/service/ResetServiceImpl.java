package origami_editor.editor.service;

import dagger.Provides;
import origami_editor.editor.canvas.CreasePattern_Worker;
import origami_editor.editor.databinding.*;
import origami_editor.editor.drawing.tools.Camera;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
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

    @Inject
    public ResetServiceImpl(CreasePattern_Worker mainCreasePatternWorker,
                        @Named("creasePatternCamera") Camera creasePatternCamera,
                        CanvasModel canvasModel,
                        InternalDivisionRatioModel internalDivisionRatioModel,
                        FoldedFigureModel foldedFigureModel,
                        GridModel gridModel,
                        AngleSystemModel angleSystemModel,
                        CameraModel creasePatternCameraModel,
                        FoldedFiguresList foldedFiguresList) {
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.creasePatternCamera = creasePatternCamera;
        this.canvasModel = canvasModel;
        this.internalDivisionRatioModel = internalDivisionRatioModel;
        this.foldedFigureModel = foldedFigureModel;
        this.gridModel = gridModel;
        this.angleSystemModel = angleSystemModel;
        this.creasePatternCameraModel = creasePatternCameraModel;
        this.foldedFiguresList = foldedFiguresList;
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
        angleSystemModel.reset();
        creasePatternCameraModel.reset();

        foldedFiguresList.removeAllElements();
    }
}
