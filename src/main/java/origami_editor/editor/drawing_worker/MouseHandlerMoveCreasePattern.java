package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.Point;
import origami_editor.editor.App;
import origami_editor.editor.MouseMode;
import origami_editor.editor.folded_figure.FoldedFigure;

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
        app.mainDrawingWorker.setCamera(app.canvas.creasePatternCamera);

//20180225追加
        FoldedFigure OZi;
        for (int i_oz = 1; i_oz <= app.foldedFigures.size() - 1; i_oz++) {
            OZi = app.foldedFigures.get(i_oz);

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
        app.mainDrawingWorker.setCamera(app.canvas.creasePatternCamera);


//20180225追加
        FoldedFigure OZi;
        for (int i_oz = 1; i_oz <= app.foldedFigures.size() - 1; i_oz++) {
            OZi = app.foldedFigures.get(i_oz);

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
