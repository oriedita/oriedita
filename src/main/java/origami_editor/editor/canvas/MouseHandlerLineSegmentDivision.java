package origami_editor.editor.canvas;

import org.springframework.stereotype.Component;
import origami.Epsilon;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

@Component
public class MouseHandlerLineSegmentDivision extends BaseMouseHandlerInputRestricted {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.LINE_SEGMENT_DIVISION_27;
    }

    //マウス操作(mouseMode==27線分入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        Point closest_point = d.getClosestPoint(p);
        if (p.distance(closest_point) < d.selectionDistance) {
            d.lineStepAdd(new LineSegment(p, closest_point, d.lineColor));
            d.lineStep.get(0).setActive(LineSegment.ActiveState.ACTIVE_B_2);
            return;
        }
        d.lineStepAdd(new LineSegment(p, p, d.lineColor));
        d.lineStep.get(0).setActive(LineSegment.ActiveState.ACTIVE_B_2);
    }


// 19 19 19 19 19 19 19 19 19 select 選択

    //マウス操作(mouseMode==27線分入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.lineStep.get(0).setA(p);
        if (d.gridInputAssist) {
            d.lineCandidate.clear();
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.selectionDistance) {
                d.lineCandidate.add(new LineSegment(closestPoint, closestPoint, d.lineColor));
                d.lineStep.get(0).setA(d.lineStep.get(0).getA());
            }
        }
    }

    //マウス操作(mouseMode==27線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        d.lineStep.get(0).setA(p);

        Point closestPoint = d.getClosestPoint(p);

        if (p.distance(closestPoint) <= d.selectionDistance) {
            d.lineStep.get(0).setA(closestPoint);
        }
        if (Epsilon.high.gt0(d.lineStep.get(0).determineLength())) {
            for (int i = 0; i <= d.foldLineDividingNumber - 1; i++) {
                double ax = ((double) (d.foldLineDividingNumber - i) * d.lineStep.get(0).determineAX() + (double) i * d.lineStep.get(0).determineBX()) / ((double) d.foldLineDividingNumber);
                double ay = ((double) (d.foldLineDividingNumber - i) * d.lineStep.get(0).determineAY() + (double) i * d.lineStep.get(0).determineBY()) / ((double) d.foldLineDividingNumber);
                double bx = ((double) (d.foldLineDividingNumber - i - 1) * d.lineStep.get(0).determineAX() + (double) (i + 1) * d.lineStep.get(0).determineBX()) / ((double) d.foldLineDividingNumber);
                double by = ((double) (d.foldLineDividingNumber - i - 1) * d.lineStep.get(0).determineAY() + (double) (i + 1) * d.lineStep.get(0).determineBY()) / ((double) d.foldLineDividingNumber);
                LineSegment s_ad = new LineSegment(ax, ay, bx, by);
                s_ad.setColor(d.lineColor);
                d.addLineSegment(s_ad);
            }
            d.record();
        }

        d.lineStep.clear();
    }
}
