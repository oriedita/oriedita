package origami_editor.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

@Singleton
public class MouseHandlerPolygonSetNoCorners extends BaseMouseHandler {
    @Inject
    public MouseHandlerPolygonSetNoCorners() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.POLYGON_SET_NO_CORNERS_29;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        if (d.gridInputAssist) {
            d.lineCandidate.clear();
            LineSegment candidate = new LineSegment();
            candidate.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.selectionDistance) {
                candidate.set(closestPoint, closestPoint);
                candidate.setColor(d.lineColor);

                d.lineCandidate.add(candidate);
            }
        }
    }

    //マウス操作(mouseMode==29正多角形入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {

        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        Point closestPoint = d.getClosestPoint(p);

        if (d.lineStep.size() == 0) {    //第1段階として、点を選択
            if (p.distance(closestPoint) < d.selectionDistance) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.MAGENTA_5));
                d.lineStep.get(0).setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
            }
        } else if (d.lineStep.size() == 1) {    //第2段階として、点を選択
            if (p.distance(closestPoint) >= d.selectionDistance) {
                d.lineStep.clear();
                return;
            } else if (p.distance(closestPoint) < d.selectionDistance) {

                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.fromNumber(d.lineStep.size() + 1)));
                d.lineStep.get(0).setB(d.lineStep.get(1).getB());
            }
            if (Epsilon.high.le0(d.lineStep.get(0).determineLength())) {
                d.lineStep.clear();
            }
        }
    }

    //マウス操作(mouseMode==29正多角形入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
    }

    //マウス操作(mouseMode==29正多角形入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.lineStep.size() == 2) {
            LineSegment s_tane = new LineSegment();
            LineSegment s_deki = new LineSegment();


            s_tane.set(d.lineStep.get(0));
            s_tane.setColor(d.lineColor);
            d.addLineSegment(s_tane);
            for (int i = 2; i <= d.numPolygonCorners; i++) {
                s_deki.set(OritaCalc.lineSegment_rotate(s_tane, (double) (d.numPolygonCorners - 2) * 180.0 / (double) d.numPolygonCorners));
                s_tane.set(s_deki.getB(), s_deki.getA());
                s_tane.setColor(d.lineColor);
                d.addLineSegment(s_tane);

            }
            d.foldLineSet.unselect_all();
            d.record();

            d.lineStep.clear();
        }
    }
}
