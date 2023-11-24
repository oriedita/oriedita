package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.StraightLine;

@ApplicationScoped
@Handles(MouseMode.CIRCLE_DRAW_THREE_POINT_43)
public class MouseHandlerCircleDrawThreePoint extends BaseMouseHandler {
    @Inject
    public MouseHandlerCircleDrawThreePoint() {
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        Point closest_point = d.getClosestPoint(p);
        if (p.distance(closest_point) < d.getSelectionDistance()) {
            d.lineStepAdd(new LineSegment(closest_point, closest_point, LineColor.fromNumber(d.getLineStep().size() + 1)));
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.getLineStep().size() == 3) {
            LineSegment sen1 = new LineSegment(d.getLineStep().get(0).getA(), d.getLineStep().get(1).getA());
            if (Epsilon.high.le0(sen1.determineLength())) {
                return;
            }
            LineSegment sen2 = new LineSegment(d.getLineStep().get(1).getA(), d.getLineStep().get(2).getA());
            if (Epsilon.high.le0(sen2.determineLength())) {
                return;
            }
            LineSegment sen3 = new LineSegment(d.getLineStep().get(2).getA(), d.getLineStep().get(0).getA());
            if (Epsilon.high.le0(sen3.determineLength())) {
                return;
            }

            if (Math.abs(OritaCalc.angle(sen1, sen2) - 0.0) < Epsilon.UNKNOWN_1EN6) {
                return;
            }
            if (Math.abs(OritaCalc.angle(sen1, sen2) - 180.0) < Epsilon.UNKNOWN_1EN6) {
                return;
            }
            if (Math.abs(OritaCalc.angle(sen1, sen2) - 360.0) < Epsilon.UNKNOWN_1EN6) {
                return;
            }

            if (Math.abs(OritaCalc.angle(sen2, sen3) - 0.0) < Epsilon.UNKNOWN_1EN6) {
                return;
            }
            if (Math.abs(OritaCalc.angle(sen2, sen3) - 180.0) < Epsilon.UNKNOWN_1EN6) {
                return;
            }
            if (Math.abs(OritaCalc.angle(sen2, sen3) - 360.0) < Epsilon.UNKNOWN_1EN6) {
                return;
            }

            if (Math.abs(OritaCalc.angle(sen3, sen1) - 0.0) < Epsilon.UNKNOWN_1EN6) {
                return;
            }
            if (Math.abs(OritaCalc.angle(sen3, sen1) - 180.0) < Epsilon.UNKNOWN_1EN6) {
                return;
            }
            if (Math.abs(OritaCalc.angle(sen3, sen1) - 360.0) < Epsilon.UNKNOWN_1EN6) {
                return;
            }

            StraightLine t1 = new StraightLine(sen1);
            t1.orthogonalize(OritaCalc.internalDivisionRatio(sen1.getA(), sen1.getB(), 1.0, 1.0));
            StraightLine t2 = new StraightLine(sen2);
            t2.orthogonalize(OritaCalc.internalDivisionRatio(sen2.getA(), sen2.getB(), 1.0, 1.0));
            d.addCircle(OritaCalc.findIntersection(t1, t2), OritaCalc.distance(d.getLineStep().get(0).getA(), OritaCalc.findIntersection(t1, t2)), LineColor.CYAN_3);
            d.record();

            d.getLineStep().clear();
        }
    }
}
