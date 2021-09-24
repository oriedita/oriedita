package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.Point;
import origami_editor.editor.App;
import origami_editor.editor.MouseMode;

public class MouseHandlerModifyCalculatedShape implements MouseModeHandler {
    private final App app;

    public MouseHandlerModifyCalculatedShape(App app) {
        this.app = app;
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.MODIFY_CALCULATED_SHAPE_101;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    @Override
    public void mousePressed(Point p0) {
        app.OZ.foldedFigure_operation_mouse_on(p0);
    }

    @Override
    public void mouseDragged(Point p0) {
        app.OZ.foldedFigure_operation_mouse_drag(p0);
    }

    @Override
    public void mouseReleased(Point p0) {
        app.OZ.foldedFigure_operation_mouse_off(p0);
    }
}
