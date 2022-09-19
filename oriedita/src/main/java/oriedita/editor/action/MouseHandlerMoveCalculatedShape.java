package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.service.FoldedFigureCanvasSelectService;
import origami.crease_pattern.element.Point;

import java.util.EnumSet;

@ApplicationScoped
@Handles(MouseMode.MOVE_CALCULATED_SHAPE_102)
public class MouseHandlerMoveCalculatedShape implements MouseModeHandler {
    private final Point mouse_temp0 = new Point();//マウスの動作対応時に、一時的に使うTen

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
    public EnumSet<Feature> getSubscribedFeatures() {
        return EnumSet.of(Feature.BUTTON_1);
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    @Override
    public void mousePressed(Point p0) {
        foldedFigureCanvasSelectService.pointInCreasePatternOrFoldedFigure(p0);

        FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();

        switch (canvasModel.getMouseInCpOrFoldedFigure()) {
            case CREASE_PATTERN_0:
                creasePatternCamera.camera_position_specify_from_TV(p0);
                break;
            case FOLDED_FRONT_1:
                selectedFigure.getFoldedFigureFrontCamera().camera_position_specify_from_TV(p0);
                break;
            case FOLDED_BACK_2:
                selectedFigure.getFoldedFigureRearCamera().camera_position_specify_from_TV(p0);
                break;
            case TRANSPARENT_FRONT_3:
                selectedFigure.getTransparentFrontCamera().camera_position_specify_from_TV(p0);
                break;
            case TRANSPARENT_BACK_4:
                selectedFigure.getTransparentRearCamera().camera_position_specify_from_TV(p0);
                break;
        }

        mouse_temp0.set(p0);
    }

    @Override
    public void mouseDragged(Point p0) {
        FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();

        switch (canvasModel.getMouseInCpOrFoldedFigure()) {
            case CREASE_PATTERN_0:
                creasePatternCamera.displayPositionMove(mouse_temp0.other_Point_position(p0));
                break;
            case FOLDED_FRONT_1:
                selectedFigure.getFoldedFigureFrontCamera().displayPositionMove(mouse_temp0.other_Point_position(p0));
                break;
            case FOLDED_BACK_2:
                selectedFigure.getFoldedFigureRearCamera().displayPositionMove(mouse_temp0.other_Point_position(p0));
                break;
            case TRANSPARENT_FRONT_3:
                selectedFigure.getTransparentFrontCamera().displayPositionMove(mouse_temp0.other_Point_position(p0));
                break;
            case TRANSPARENT_BACK_4:
                selectedFigure.getTransparentRearCamera().displayPositionMove(mouse_temp0.other_Point_position(p0));
                break;
        }

        mouse_temp0.set(p0);//mouse_temp0は一時的に使うTen、mouse_temp0.tano_Ten_iti(p)はmouse_temp0から見たpの位置
    }

    @Override
    public void mouseReleased(Point p0) {
        mouseDragged(p0);
    }
}
