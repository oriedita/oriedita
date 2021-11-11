package origami_editor.editor.canvas;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;
import origami_editor.editor.databinding.FoldedFiguresList;
import origami_editor.editor.drawing.FoldedFigure_Drawer;
import origami_editor.tools.Camera;

@Component
public class MouseHandlerMoveCreasePattern implements MouseModeHandler {
    public Point mouse_temp0 = new Point();//マウスの動作対応時に、一時的に使うTen

    private final Camera creasePatternCamera;
    private final FoldedFiguresList foldedFiguresList;
    private final CreasePattern_Worker mainCreasePatternWorker;

    public MouseHandlerMoveCreasePattern(@Qualifier("creasePatternCamera") Camera creasePatternCamera, FoldedFiguresList foldedFiguresList, CreasePattern_Worker mainCreasePatternWorker) {
        this.creasePatternCamera = creasePatternCamera;
        this.foldedFiguresList = foldedFiguresList;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
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
        creasePatternCamera.camera_position_specify_from_TV(p0);
        mouse_temp0.set(p0);
    }

    @Override
    public void mouseDragged(Point p0) {
        creasePatternCamera.displayPositionMove(mouse_temp0.other_Point_position(p0));
        mainCreasePatternWorker.setCamera(creasePatternCamera);

//20180225追加
        for (int i_oz = 0; i_oz < foldedFiguresList.getSize(); i_oz++) {
            FoldedFigure_Drawer OZi = foldedFiguresList.getElementAt(i_oz);

            OZi.foldedFigureCamera.displayPositionMove(mouse_temp0.other_Point_position(p0));
            OZi.foldedFigureFrontCamera.displayPositionMove(mouse_temp0.other_Point_position(p0));
            OZi.foldedFigureRearCamera.displayPositionMove(mouse_temp0.other_Point_position(p0));
            OZi.transparentFrontCamera.displayPositionMove(mouse_temp0.other_Point_position(p0));
            OZi.transparentRearCamera.displayPositionMove(mouse_temp0.other_Point_position(p0));
        }
//20180225追加　ここまで

        mouse_temp0.set(p0);
    }

    @Override
    public void mouseReleased(Point p0) {
        creasePatternCamera.displayPositionMove(mouse_temp0.other_Point_position(p0));
        mainCreasePatternWorker.setCamera(creasePatternCamera);


//20180225追加
        for (int i_oz = 0; i_oz < foldedFiguresList.getSize(); i_oz++) {
            FoldedFigure_Drawer OZi = foldedFiguresList.getElementAt(i_oz);

            OZi.foldedFigureCamera.displayPositionMove(mouse_temp0.other_Point_position(p0));
            OZi.foldedFigureFrontCamera.displayPositionMove(mouse_temp0.other_Point_position(p0));
            OZi.foldedFigureRearCamera.displayPositionMove(mouse_temp0.other_Point_position(p0));
            OZi.transparentFrontCamera.displayPositionMove(mouse_temp0.other_Point_position(p0));
            OZi.transparentRearCamera.displayPositionMove(mouse_temp0.other_Point_position(p0));
        }
//20180225追加　ここまで

        mouse_temp0.set(p0);
    }
}
