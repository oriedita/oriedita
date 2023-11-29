package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.POLYGON_SET_NO_CORNERS_29)
public class MouseHandlerPolygonSetNoCorners extends BaseMouseHandler {
    @Inject
    public MouseHandlerPolygonSetNoCorners() {
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        if (d.getGridInputAssist()) {
            d.getLineCandidate().clear();
            Point p = d.getCamera().TV2object(p0);
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                LineSegment candidate = new LineSegment(closestPoint, closestPoint,
                        d.getLineColor(), LineSegment.ActiveState.ACTIVE_BOTH_3);
                d.getLineCandidate().add(candidate);
            }
        }
    }

    //マウス操作(mouseMode==29正多角形入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {

        Point p = d.getCamera().TV2object(p0);

        Point closestPoint = d.getClosestPoint(p);

        if (d.getLineStep().size() == 0) {    //第1段階として、点を選択
            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.MAGENTA_5));
                d.getLineStep().get(0).setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
            }
        } else if (d.getLineStep().size() == 1) {    //第2段階として、点を選択
            if (p.distance(closestPoint) >= d.getSelectionDistance()) {
                d.getLineStep().clear();
                return;
            } else if (p.distance(closestPoint) < d.getSelectionDistance()) {

                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.fromNumber(d.getLineStep().size() + 1)));
                d.getLineStep().get(0).setB(d.getLineStep().get(1).getB());
            }
            if (Epsilon.high.le0(d.getLineStep().get(0).determineLength())) {
                d.getLineStep().clear();
            }
        }
    }

    //マウス操作(mouseMode==29正多角形入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
    }

    //マウス操作(mouseMode==29正多角形入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.getLineStep().size() == 2) {


            LineSegment s_tane = new LineSegment(d.getLineStep().get(0));
            s_tane.setColor(d.getLineColor());
            d.addLineSegment(s_tane);
            for (int i = 2; i <= d.getNumPolygonCorners(); i++) {
                LineSegment s_deki = OritaCalc.lineSegment_rotate(s_tane, (double) (d.getNumPolygonCorners() - 2) * 180.0 / (double) d.getNumPolygonCorners());
                s_tane = new LineSegment(s_deki.getB(), s_deki.getA());
                s_tane.setColor(d.getLineColor());
                d.addLineSegment(s_tane);

            }
            d.getFoldLineSet().unselect_all();
            d.record();

            d.getLineStep().clear();
        }
    }
}
