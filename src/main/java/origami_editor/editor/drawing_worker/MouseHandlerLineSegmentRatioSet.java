package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerLineSegmentRatioSet extends BaseMouseHandlerInputRestricted {

    public MouseHandlerLineSegmentRatioSet(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.LINE_SEGMENT_RATIO_SET_28;
    }

    //マウス操作(mouseMode==28線分内分入力 でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        d.lineStep.clear();

        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        Point closestPoint = d.getClosestPoint(p);
        if (p.distance(closestPoint) < d.selectionDistance) {
            LineSegment s = new LineSegment(p, closestPoint, d.lineColor);
            s.setActive(LineSegment.ActiveState.ACTIVE_B_2);
            d.lineStepAdd(s);
            return;
        }

        LineSegment s = new LineSegment(p, p, d.lineColor);
        s.setActive(LineSegment.ActiveState.ACTIVE_B_2);

        d.lineStepAdd(s);
    }

    //マウス操作(mouseMode==28線分入力 でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.lineStep.get(0).setA(p);

        if (d.gridInputAssist) {
            d.lineCandidate.clear();
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.selectionDistance) {
                d.lineCandidate.add(new LineSegment(closestPoint, closestPoint, d.lineColor));
            } else {
                d.lineCandidate.add(new LineSegment(p, p, d.lineColor));
            }
            d.lineStep.get(0).setA(d.lineCandidate.get(0).getA());
        }
    }

    //マウス操作(mouseMode==28線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        d.lineStep.get(0).setA(p);
        Point closestPoint = d.getClosestPoint(p);

        if (p.distance(closestPoint) <= d.selectionDistance) {
            d.lineStep.get(0).setA(closestPoint);
        }
        if (d.lineStep.get(0).determineLength() > 0.00000001) {
            if ((d.internalDivisionRatio_s == 0.0) && (d.internalDivisionRatio_t == 0.0)) {
            }
            if ((d.internalDivisionRatio_s == 0.0) && (d.internalDivisionRatio_t != 0.0)) {
                d.addLineSegment(d.lineStep.get(0));
            }
            if ((d.internalDivisionRatio_s != 0.0) && (d.internalDivisionRatio_t == 0.0)) {
                d.addLineSegment(d.lineStep.get(0));
            }
            if ((d.internalDivisionRatio_s != 0.0) && (d.internalDivisionRatio_t != 0.0)) {
                LineSegment s_ad = new LineSegment();
                s_ad.setColor(d.lineColor);
                double nx = (d.internalDivisionRatio_t * d.lineStep.get(0).determineBX() + d.internalDivisionRatio_s * d.lineStep.get(0).determineAX()) / (d.internalDivisionRatio_s + d.internalDivisionRatio_t);
                double ny = (d.internalDivisionRatio_t * d.lineStep.get(0).determineBY() + d.internalDivisionRatio_s * d.lineStep.get(0).determineAY()) / (d.internalDivisionRatio_s + d.internalDivisionRatio_t);
                s_ad.set(d.lineStep.get(0).determineAX(), d.lineStep.get(0).determineAY(), nx, ny);
                d.addLineSegment(s_ad);
                s_ad.set(d.lineStep.get(0).determineBX(), d.lineStep.get(0).determineBY(), nx, ny);
                d.addLineSegment(s_ad);
            }
            d.record();
        }

        d.lineStep.clear();
    }
}
