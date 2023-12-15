package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.CREASE_DELETE_OVERLAPPING_64)
public class MouseHandlerCreaseDeleteOverlapping extends BaseMouseHandlerInputRestricted {
    private final MouseHandlerDrawCreaseRestricted mouseHandlerDrawCreaseRestricted;

    @Inject
    public MouseHandlerCreaseDeleteOverlapping(@Handles(MouseMode.DRAW_CREASE_RESTRICTED_11) MouseHandlerDrawCreaseRestricted mouseHandlerDrawCreaseRestricted) {
        this.mouseHandlerDrawCreaseRestricted = mouseHandlerDrawCreaseRestricted;
    }

    //マウス操作(mouseMode==64　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        d.getLineStep().clear();

        Point p = d.getCamera().TV2object(p0);
        Point closest_point = d.getClosestPoint(p);
        if (p.distance(closest_point) > d.getSelectionDistance()) {
            return;
        }

        d.lineStepAdd(new LineSegment(p, closest_point, LineColor.MAGENTA_5));
    }

    //マウス操作(mouseMode==64　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        mouseHandlerDrawCreaseRestricted.mouseDragged(p0);
    }

    //マウス操作(mouseMode==64　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.getLineStep().size() == 1) {
            Point p = d.getCamera().TV2object(p0);
            Point closest_point = d.getClosestPoint(p);
            d.getLineStep().set(0, d.getLineStep().get(0).withA(closest_point));
            if (p.distance(closest_point) <= d.getSelectionDistance()) {
                if (Epsilon.high.gt0(d.getLineStep().get(0).determineLength())) {
                    d.getFoldLineSet().deleteInsideLine(d.getLineStep().get(0), "l");//lは小文字のエル

                    d.record();
                }
            }
            d.getLineStep().clear();
        }

    }
}
