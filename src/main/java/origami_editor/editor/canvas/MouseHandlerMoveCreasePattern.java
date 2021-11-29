package origami_editor.editor.canvas;

import origami.crease_pattern.element.Point;
import origami_editor.editor.Canvas;
import origami_editor.editor.MouseMode;
import origami_editor.editor.drawing.FoldedFigure_Drawer;

import javax.swing.*;
import java.util.EnumSet;

public class MouseHandlerMoveCreasePattern implements MouseModeHandler {
    private final Canvas canvas;
    private final DefaultComboBoxModel<FoldedFigure_Drawer> foldedFiguresList;
    private final CreasePattern_Worker mainCreasePatternWorker;

    public MouseHandlerMoveCreasePattern(Canvas canvas, DefaultComboBoxModel<FoldedFigure_Drawer> foldedFiguresList, CreasePattern_Worker mainCreasePatternWorker) {
        this.canvas = canvas;
        this.foldedFiguresList = foldedFiguresList;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.MOVE_CREASE_PATTERN_2;
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
        canvas.creasePatternCamera.camera_position_specify_from_TV(p0);
        canvas.mouse_temp0.set(p0);
    }

    @Override
    public void mouseDragged(Point p0) {
        canvas.creasePatternCamera.displayPositionMove(canvas.mouse_temp0.other_Point_position(p0));
        mainCreasePatternWorker.setCamera(canvas.creasePatternCamera);

//20180225追加
        for (int i_oz = 0; i_oz < foldedFiguresList.getSize(); i_oz++) {
            FoldedFigure_Drawer OZi = foldedFiguresList.getElementAt(i_oz);

            OZi.foldedFigureCamera.displayPositionMove(canvas.mouse_temp0.other_Point_position(p0));
            OZi.foldedFigureFrontCamera.displayPositionMove(canvas.mouse_temp0.other_Point_position(p0));
            OZi.foldedFigureRearCamera.displayPositionMove(canvas.mouse_temp0.other_Point_position(p0));
            OZi.transparentFrontCamera.displayPositionMove(canvas.mouse_temp0.other_Point_position(p0));
            OZi.transparentRearCamera.displayPositionMove(canvas.mouse_temp0.other_Point_position(p0));
        }
//20180225追加　ここまで

        canvas.mouse_temp0.set(p0);
    }

    @Override
    public void mouseReleased(Point p0) {
        canvas.creasePatternCamera.displayPositionMove(canvas.mouse_temp0.other_Point_position(p0));
        mainCreasePatternWorker.setCamera(canvas.creasePatternCamera);


//20180225追加
        for (int i_oz = 0; i_oz < foldedFiguresList.getSize(); i_oz++) {
            FoldedFigure_Drawer OZi = foldedFiguresList.getElementAt(i_oz);

            OZi.foldedFigureCamera.displayPositionMove(canvas.mouse_temp0.other_Point_position(p0));
            OZi.foldedFigureFrontCamera.displayPositionMove(canvas.mouse_temp0.other_Point_position(p0));
            OZi.foldedFigureRearCamera.displayPositionMove(canvas.mouse_temp0.other_Point_position(p0));
            OZi.transparentFrontCamera.displayPositionMove(canvas.mouse_temp0.other_Point_position(p0));
            OZi.transparentRearCamera.displayPositionMove(canvas.mouse_temp0.other_Point_position(p0));
        }
//20180225追加　ここまで

        canvas.mouse_temp0.set(p0);
    }
}
