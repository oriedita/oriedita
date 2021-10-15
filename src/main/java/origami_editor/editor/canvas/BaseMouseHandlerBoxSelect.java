package origami_editor.editor.canvas;

import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

/**
 * Mouse handler for modes which perform some box select.
 */
public abstract class BaseMouseHandlerBoxSelect extends BaseMouseHandler {
    Point selectionStart = new Point();

    @Override
    public void mouseMoved(Point p0) {

    }

    @Override
    public void mousePressed(Point p0) {
        selectionStart.set(p0);

        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        d.lineStep.clear();
        d.lineStepAdd(new LineSegment(p, p, LineColor.MAGENTA_5));
        d.lineStepAdd(new LineSegment(p, p, LineColor.MAGENTA_5));
        d.lineStepAdd(new LineSegment(p, p, LineColor.MAGENTA_5));
        d.lineStepAdd(new LineSegment(p, p, LineColor.MAGENTA_5));
    }

    @Override
    public void mouseDragged(Point p0) {
        Point p19_2 = new Point(selectionStart.getX(), p0.getY());
        Point p19_4 = new Point(p0.getX(), selectionStart.getY());

        Point p19_a = new Point(d.camera.TV2object(selectionStart));
        Point p19_b = new Point(d.camera.TV2object(p19_2));
        Point p19_c = new Point(d.camera.TV2object(p0));
        Point p19_d = new Point(d.camera.TV2object(p19_4));

        d.lineStep.get(0).set(p19_a, p19_b);
        d.lineStep.get(1).set(p19_b, p19_c);
        d.lineStep.get(2).set(p19_c, p19_d);
        d.lineStep.get(3).set(p19_d, p19_a);
    }
}
