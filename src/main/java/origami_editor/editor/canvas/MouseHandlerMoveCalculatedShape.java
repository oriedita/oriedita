package origami_editor.editor.canvas;

import origami.crease_pattern.element.Point;
import origami_editor.editor.Canvas;
import origami_editor.editor.MouseMode;
import origami_editor.editor.drawing.FoldedFigure_Drawer;

import javax.swing.*;

public class MouseHandlerMoveCalculatedShape implements MouseModeHandler {
    private final DefaultComboBoxModel<FoldedFigure_Drawer> foldedFiguresList;
    private final Canvas canvas;

    public MouseHandlerMoveCalculatedShape(DefaultComboBoxModel<FoldedFigure_Drawer> foldedFiguresList, Canvas canvas) {
        this.foldedFiguresList = foldedFiguresList;
        this.canvas = canvas;
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
        canvas.pointInCreasePatternOrFoldedFigure(p0);

        FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) foldedFiguresList.getSelectedItem();

        switch (canvas.i_cp_or_oriagari) {
            case CREASE_PATTERN_0:
                canvas.creasePatternCamera.camera_position_specify_from_TV(p0);
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

        canvas.mouse_temp0.set(p0);
    }

    @Override
    public void mouseDragged(Point p0) {
        FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) foldedFiguresList.getSelectedItem();

        switch (canvas.i_cp_or_oriagari) {
            case CREASE_PATTERN_0:
                canvas.creasePatternCamera.displayPositionMove(canvas.mouse_temp0.other_Point_position(p0));
                break;
            case FOLDED_FRONT_1:
                selectedFigure.foldedFigureFrontCamera.displayPositionMove(canvas.mouse_temp0.other_Point_position(p0));
                break;
            case FOLDED_BACK_2:
                selectedFigure.foldedFigureRearCamera.displayPositionMove(canvas.mouse_temp0.other_Point_position(p0));
                break;
            case TRANSPARENT_FRONT_3:
                selectedFigure.transparentFrontCamera.displayPositionMove(canvas.mouse_temp0.other_Point_position(p0));
                break;
            case TRANSPARENT_BACK_4:
                selectedFigure.transparentRearCamera.displayPositionMove(canvas.mouse_temp0.other_Point_position(p0));
                break;
        }

        canvas.mouse_temp0.set(p0);//mouse_temp0は一時的に使うTen、mouse_temp0.tano_Ten_iti(p)はmouse_temp0から見たpの位置
    }

    @Override
    public void mouseReleased(Point p0) {
        mouseDragged(p0);
    }
}
