package origami_editor.editor.canvas;

import origami.crease_pattern.element.Point;
import origami_editor.editor.App;
import origami_editor.editor.MouseMode;
import origami_editor.editor.drawing.FoldedFigure_Drawer;

public class MouseHandlerMoveCreasePattern implements MouseModeHandler {
    private final App app;

    public MouseHandlerMoveCreasePattern(App app) {
        this.app = app;
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.MOVE_CREASE_PATTERN_2;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    @Override
    public void mousePressed(Point p0) {
        app.canvas.creasePatternCamera.camera_position_specify_from_TV(p0);
        app.canvas.mouse_temp0.set(p0);
    }

    @Override
    public void mouseDragged(Point p0) {
        app.canvas.creasePatternCamera.displayPositionMove(app.canvas.mouse_temp0.other_Point_position(p0));
        app.mainCreasePatternWorker.setCamera(app.canvas.creasePatternCamera);

//20180225追加
        for (int i_oz = 0; i_oz < app.foldedFiguresList.getSize(); i_oz++) {
            FoldedFigure_Drawer OZi = app.foldedFiguresList.getElementAt(i_oz);

            OZi.foldedFigureCamera.displayPositionMove(app.canvas.mouse_temp0.other_Point_position(p0));
            OZi.foldedFigureFrontCamera.displayPositionMove(app.canvas.mouse_temp0.other_Point_position(p0));
            OZi.foldedFigureRearCamera.displayPositionMove(app.canvas.mouse_temp0.other_Point_position(p0));
            OZi.transparentFrontCamera.displayPositionMove(app.canvas.mouse_temp0.other_Point_position(p0));
            OZi.transparentRearCamera.displayPositionMove(app.canvas.mouse_temp0.other_Point_position(p0));
        }
//20180225追加　ここまで

        app.canvas.mouse_temp0.set(p0);
    }

    @Override
    public void mouseReleased(Point p0) {
        app.canvas.creasePatternCamera.displayPositionMove(app.canvas.mouse_temp0.other_Point_position(p0));
        app.mainCreasePatternWorker.setCamera(app.canvas.creasePatternCamera);


//20180225追加
        for (int i_oz = 0; i_oz < app.foldedFiguresList.getSize(); i_oz++) {
            FoldedFigure_Drawer OZi = app.foldedFiguresList.getElementAt(i_oz);

            OZi.foldedFigureCamera.displayPositionMove(app.canvas.mouse_temp0.other_Point_position(p0));
            OZi.foldedFigureFrontCamera.displayPositionMove(app.canvas.mouse_temp0.other_Point_position(p0));
            OZi.foldedFigureRearCamera.displayPositionMove(app.canvas.mouse_temp0.other_Point_position(p0));
            OZi.transparentFrontCamera.displayPositionMove(app.canvas.mouse_temp0.other_Point_position(p0));
            OZi.transparentRearCamera.displayPositionMove(app.canvas.mouse_temp0.other_Point_position(p0));
        }
//20180225追加　ここまで

        app.canvas.mouse_temp0.set(p0);
    }
}
