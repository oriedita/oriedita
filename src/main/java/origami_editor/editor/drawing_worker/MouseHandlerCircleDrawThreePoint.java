package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.StraightLine;
import origami_editor.editor.MouseMode;

public class MouseHandlerCircleDrawThreePoint extends BaseMouseHandler{
    public MouseHandlerCircleDrawThreePoint(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CIRCLE_DRAW_THREE_POINT_43;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.closest_point.set(d.getClosestPoint(p));
        if (p.distance(d.closest_point) < d.selectionDistance) {
            d.i_drawing_stage = d.i_drawing_stage + 1;
            d.line_step[d.i_drawing_stage].set(d.closest_point, d.closest_point);
            d.line_step[d.i_drawing_stage].setColor(LineColor.fromNumber(d.i_drawing_stage));
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.i_drawing_stage == 3) {
            d.i_drawing_stage = 0;

            LineSegment sen1 = new LineSegment(d.line_step[1].getA(), d.line_step[2].getA());
            if (sen1.getLength() < 0.00000001) {
                return;
            }
            LineSegment sen2 = new LineSegment(d.line_step[2].getA(), d.line_step[3].getA());
            if (sen2.getLength() < 0.00000001) {
                return;
            }
            LineSegment sen3 = new LineSegment(d.line_step[3].getA(), d.line_step[1].getA());
            if (sen3.getLength() < 0.00000001) {
                return;
            }

            if (Math.abs(OritaCalc.angle(sen1, sen2) - 0.0) < 0.000001) {
                return;
            }
            if (Math.abs(OritaCalc.angle(sen1, sen2) - 180.0) < 0.000001) {
                return;
            }
            if (Math.abs(OritaCalc.angle(sen1, sen2) - 360.0) < 0.000001) {
                return;
            }

            if (Math.abs(OritaCalc.angle(sen2, sen3) - 0.0) < 0.000001) {
                return;
            }
            if (Math.abs(OritaCalc.angle(sen2, sen3) - 180.0) < 0.000001) {
                return;
            }
            if (Math.abs(OritaCalc.angle(sen2, sen3) - 360.0) < 0.000001) {
                return;
            }

            if (Math.abs(OritaCalc.angle(sen3, sen1) - 0.0) < 0.000001) {
                return;
            }
            if (Math.abs(OritaCalc.angle(sen3, sen1) - 180.0) < 0.000001) {
                return;
            }
            if (Math.abs(OritaCalc.angle(sen3, sen1) - 360.0) < 0.000001) {
                return;
            }

            StraightLine t1 = new StraightLine(sen1);
            t1.orthogonalize(OritaCalc.internalDivisionRatio(sen1.getA(), sen1.getB(), 1.0, 1.0));
            StraightLine t2 = new StraightLine(sen2);
            t2.orthogonalize(OritaCalc.internalDivisionRatio(sen2.getA(), sen2.getB(), 1.0, 1.0));
            d.addCircle(OritaCalc.findIntersection(t1, t2), OritaCalc.distance(d.line_step[1].getA(), OritaCalc.findIntersection(t1, t2)), LineColor.CYAN_3);
            d.record();
        }
    }
}
