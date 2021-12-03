package oriedita.editor.action;

import origami.crease_pattern.element.Point;
import oriedita.editor.canvas.MouseMode;

public interface MouseModeHandler {
    MouseMode getMouseMode();

    void mouseMoved(Point p0);

    void mousePressed(Point p0);

    void mouseDragged(Point p0);

    void mouseReleased(Point p0);
}
