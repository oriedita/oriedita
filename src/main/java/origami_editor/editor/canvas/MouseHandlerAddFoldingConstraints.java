package origami_editor.editor.canvas;

import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerAddFoldingConstraints implements MouseModeHandler{
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.ADD_FOLDING_CONSTRAINT;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    @Override
    public void mousePressed(Point p0) {
        System.out.println("Pressed in constraint mode");
    }

    @Override
    public void mouseDragged(Point p0) {

    }

    @Override
    public void mouseReleased(Point p0) {

    }
}
