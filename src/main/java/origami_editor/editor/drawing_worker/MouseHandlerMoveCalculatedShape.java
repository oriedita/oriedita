package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.Point;
import origami_editor.editor.App;
import origami_editor.editor.MouseMode;

public class MouseHandlerMoveCalculatedShape implements MouseModeHandler {
    private final App app;

    public MouseHandlerMoveCalculatedShape(App app) {
        this.app = app;
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
        app.OZ.foldedFigureCamera.camera_position_specify_from_TV(p0);
        app.OZ.foldedFigureFrontCamera.camera_position_specify_from_TV(p0);
        app.OZ.foldedFigureRearCamera.camera_position_specify_from_TV(p0);

        app.OZ.transparentFrontCamera.camera_position_specify_from_TV(p0);
        app.OZ.transparentRearCamera.camera_position_specify_from_TV(p0);

        app.canvas.mouse_temp0.set(p0);
    }

    @Override
    public void mouseDragged(Point p0) {
        app.OZ.foldedFigureCamera.displayPositionMove(app.canvas.mouse_temp0.other_Point_position(p0));
        app.OZ.foldedFigureFrontCamera.displayPositionMove(app.canvas.mouse_temp0.other_Point_position(p0));
        app.OZ.foldedFigureRearCamera.displayPositionMove(app.canvas.mouse_temp0.other_Point_position(p0));

        app.OZ.transparentFrontCamera.displayPositionMove(app.canvas.mouse_temp0.other_Point_position(p0));
        app.OZ.transparentRearCamera.displayPositionMove(app.canvas.mouse_temp0.other_Point_position(p0));

        app.canvas.mouse_temp0.set(p0);//mouse_temp0は一時的に使うTen、mouse_temp0.tano_Ten_iti(p)はmouse_temp0から見たpの位置

    }

    @Override
    public void mouseReleased(Point p0) {
        mouseDragged(p0);
    }
}
