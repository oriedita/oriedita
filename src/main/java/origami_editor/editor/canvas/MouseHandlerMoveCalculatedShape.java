package origami_editor.editor.canvas;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;
import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.databinding.FoldedFiguresList;
import origami_editor.editor.drawing.FoldedFigure_Drawer;
import origami_editor.editor.service.FoldedFigureCanvasSelectService;
import origami_editor.tools.Camera;

@Singleton
public class MouseHandlerMoveCalculatedShape implements MouseModeHandler {
    public Point mouse_temp0 = new Point();//マウスの動作対応時に、一時的に使うTen

    private final FoldedFiguresList foldedFiguresList;
    private final Camera creasePatternCamera;
    private final FoldedFigureCanvasSelectService foldedFigureCanvasSelectService;
    private final CanvasModel canvasModel;

    @Inject
    public MouseHandlerMoveCalculatedShape(FoldedFiguresList foldedFiguresList,
                                           @Named("creasePatternCamera") Camera creasePatternCamera,
                                           FoldedFigureCanvasSelectService foldedFigureCanvasSelectService,
                                           CanvasModel canvasModel) {
        this.foldedFiguresList = foldedFiguresList;
        this.creasePatternCamera = creasePatternCamera;
        this.foldedFigureCanvasSelectService = foldedFigureCanvasSelectService;
        this.canvasModel = canvasModel;
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.MOVE_CALCULATED_SHAPE_102;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    @Override
    public void mousePressed(Point p0) {
        foldedFigureCanvasSelectService.pointInCreasePatternOrFoldedFigure(p0);

        FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) foldedFiguresList.getSelectedItem();

        switch (canvasModel.getMouseInCpOrFoldedFigure()) {
            case CREASE_PATTERN_0:
                creasePatternCamera.camera_position_specify_from_TV(p0);
                break;
            case FOLDED_FRONT_1:
                selectedFigure.foldedFigureFrontCamera.camera_position_specify_from_TV(p0);
                break;
            case FOLDED_BACK_2:
                selectedFigure.foldedFigureRearCamera.camera_position_specify_from_TV(p0);
                break;
            case TRANSPARENT_FRONT_3:
                selectedFigure.transparentFrontCamera.camera_position_specify_from_TV(p0);
                break;
            case TRANSPARENT_BACK_4:
                selectedFigure.transparentRearCamera.camera_position_specify_from_TV(p0);
                break;
        }

        mouse_temp0.set(p0);
    }

    @Override
    public void mouseDragged(Point p0) {
        FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) foldedFiguresList.getSelectedItem();

        switch (canvasModel.getMouseInCpOrFoldedFigure()) {
            case CREASE_PATTERN_0:
                creasePatternCamera.displayPositionMove(mouse_temp0.other_Point_position(p0));
                break;
            case FOLDED_FRONT_1:
                selectedFigure.foldedFigureFrontCamera.displayPositionMove(mouse_temp0.other_Point_position(p0));
                break;
            case FOLDED_BACK_2:
                selectedFigure.foldedFigureRearCamera.displayPositionMove(mouse_temp0.other_Point_position(p0));
                break;
            case TRANSPARENT_FRONT_3:
                selectedFigure.transparentFrontCamera.displayPositionMove(mouse_temp0.other_Point_position(p0));
                break;
            case TRANSPARENT_BACK_4:
                selectedFigure.transparentRearCamera.displayPositionMove(mouse_temp0.other_Point_position(p0));
                break;
        }

        mouse_temp0.set(p0);//mouse_temp0は一時的に使うTen、mouse_temp0.tano_Ten_iti(p)はmouse_temp0から見たpの位置
    }

    @Override
    public void mouseReleased(Point p0) {
        mouseDragged(p0);
    }
}
