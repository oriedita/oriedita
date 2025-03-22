package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.DELETE_POINT_15)
public class MouseHandlerDeletePoint extends BaseMouseHandler {
    @Inject
    public MouseHandlerDeletePoint() {}

    @Override
    public void mouseMoved(Point p0) {}

    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        d.getFoldLineSet().del_V(p, d.getSelectionDistance(), Epsilon.UNKNOWN_1EN6);
        d.record();
    }

    public void mouseDragged(Point p0) {}

    public void mouseReleased(Point p0) {}
}
