package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerDrawCreaseRestricted extends BaseMouseHandlerInputRestricted {
    public MouseHandlerDrawCreaseRestricted(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.DRAW_CREASE_RESTRICTED_11;
    }

    //マウス操作(mouseMode==11線分入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        LineSegment s = new LineSegment();
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        Point closest_point = d.getClosestPoint(p);
        if (p.distance(closest_point) > d.selectionDistance) {
            return;
        }
        s.set(p, closest_point);
        s.setColor(d.lineColor);

        d.lineStepAdd(s);
        s.setActive(LineSegment.ActiveState.ACTIVE_B_2);
    }

    //マウス操作(mouseMode==11線分入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        //近い既存点のみ表示

        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.lineStep.get(0).setA(p);

        if (d.gridInputAssist) {
            d.lineCandidate.clear();

            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.selectionDistance) {
                d.lineCandidate.add(new LineSegment(closestPoint, closestPoint, d.lineColor));
                d.lineStep.get(0).setA(d.lineCandidate.get(0).getA());
            }
        }
    }//近い既存点のみ表示

    //マウス操作(mouseMode==11線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.lineStep.size() == 1) {
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            Point closestPoint = d.getClosestPoint(p);
            d.lineStep.get(0).setA(closestPoint);
            if (p.distance(closestPoint) <= d.selectionDistance) {
                if (d.lineStep.get(0).getLength() > 0.00000001) {
                    d.addLineSegment(d.lineStep.get(0));
                    d.record();
                }
            }

            d.lineStep.clear();
        }
    }
}
