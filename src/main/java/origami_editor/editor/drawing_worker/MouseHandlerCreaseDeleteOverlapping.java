package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerCreaseDeleteOverlapping extends BaseMouseHandlerInputRestricted {
    private final MouseHandlerDrawCreaseRestricted mouseHandlerDrawCreaseRestricted;

    public MouseHandlerCreaseDeleteOverlapping(DrawingWorker d) {
        super(d);
        mouseHandlerDrawCreaseRestricted = new MouseHandlerDrawCreaseRestricted(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_DELETE_OVERLAPPING_64;
    }

    //マウス操作(mouseMode==64　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        d.lineStep.clear();

        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        Point closest_point = d.getClosestPoint(p);
        if (p.distance(closest_point) > d.selectionDistance) {
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
        if (d.lineStep.size() == 1) {
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            Point closest_point = d.getClosestPoint(p);
            d.lineStep.get(0).setA(closest_point);
            if (p.distance(closest_point) <= d.selectionDistance) {
                if (d.lineStep.get(0).determineLength() > 0.00000001) {
                    d.foldLineSet.deleteInsideLine(d.lineStep.get(0), "l");//lは小文字のエル

                    d.record();
                }
            }
            d.lineStep.clear();
        }

    }
}
