package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.LINE_SEGMENT_DIVISION_27)
public class MouseHandlerLineSegmentDivision extends BaseMouseHandlerInputRestricted {
    @Inject
    public MouseHandlerLineSegmentDivision() {
    }

    //マウス操作(mouseMode==27線分入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.getCamera().TV2object(p0));
        Point closest_point = d.getClosestPoint(p);
        if (p.distance(closest_point) < d.getSelectionDistance()) {
            d.lineStepAdd(new LineSegment(p, closest_point, d.getLineColor()));
            d.getLineStep().get(0).setActive(LineSegment.ActiveState.ACTIVE_B_2);
            return;
        }
        d.lineStepAdd(new LineSegment(p, p, d.getLineColor()));
        d.getLineStep().get(0).setActive(LineSegment.ActiveState.ACTIVE_B_2);
    }


// 19 19 19 19 19 19 19 19 19 select 選択

    //マウス操作(mouseMode==27線分入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        Point p = new Point();
        p.set(d.getCamera().TV2object(p0));
        d.getLineStep().get(0).setA(p);
        if (d.getGridInputAssist()) {
            d.getLineCandidate().clear();
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                d.getLineCandidate().add(new LineSegment(closestPoint, closestPoint, d.getLineColor()));
                d.getLineStep().get(0).setA(d.getLineStep().get(0).getA());
            }
        }
    }

    //マウス操作(mouseMode==27線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        Point p = new Point();
        p.set(d.getCamera().TV2object(p0));

        d.getLineStep().get(0).setA(p);

        Point closestPoint = d.getClosestPoint(p);

        if (p.distance(closestPoint) <= d.getSelectionDistance()) {
            d.getLineStep().get(0).setA(closestPoint);
        }
        if (Epsilon.high.gt0(d.getLineStep().get(0).determineLength())) {
            for (int i = 0; i <= d.getFoldLineDividingNumber() - 1; i++) {
                double ax = ((double) (d.getFoldLineDividingNumber() - i) * d.getLineStep().get(0).determineAX() + (double) i * d.getLineStep().get(0).determineBX()) / ((double) d.getFoldLineDividingNumber());
                double ay = ((double) (d.getFoldLineDividingNumber() - i) * d.getLineStep().get(0).determineAY() + (double) i * d.getLineStep().get(0).determineBY()) / ((double) d.getFoldLineDividingNumber());
                double bx = ((double) (d.getFoldLineDividingNumber() - i - 1) * d.getLineStep().get(0).determineAX() + (double) (i + 1) * d.getLineStep().get(0).determineBX()) / ((double) d.getFoldLineDividingNumber());
                double by = ((double) (d.getFoldLineDividingNumber() - i - 1) * d.getLineStep().get(0).determineAY() + (double) (i + 1) * d.getLineStep().get(0).determineBY()) / ((double) d.getFoldLineDividingNumber());
                LineSegment s_ad = new LineSegment(ax, ay, bx, by);
                s_ad.setColor(d.getLineColor());
                d.addLineSegment(s_ad);
            }
            d.record();
        }

        d.getLineStep().clear();
    }
}
