package origami_editor.editor.canvas;

import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public interface MouseModeHandler {
    MouseMode getMouseMode();

    void mouseMoved(Point p0);

    void mousePressed(Point p0);

    void mouseDragged(Point p0);

    void mouseReleased(Point p0);
}
