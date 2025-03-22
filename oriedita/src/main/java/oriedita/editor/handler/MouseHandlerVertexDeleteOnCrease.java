package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.VERTEX_DELETE_ON_CREASE_41)
public class MouseHandlerVertexDeleteOnCrease extends BaseMouseHandler {

    @Inject
    public MouseHandlerVertexDeleteOnCrease() {}

    @Override
    public void mouseMoved(Point p0) {}

    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        d.getFoldLineSet().del_V_cc(p, d.getSelectionDistance(), Epsilon.UNKNOWN_1EN6);
        d.record();
    }

    public void mouseDragged(Point p0) {}

    public void mouseReleased(Point p0) {}
}
