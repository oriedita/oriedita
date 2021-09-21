package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerCircleDrawConcentric extends BaseMouseHandler{
    public MouseHandlerCircleDrawConcentric(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CIRCLE_DRAW_CONCENTRIC_48;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==48 同心円　線分入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.closest_circumference.set(d.getClosestCircleMidpoint(p));
        d.closest_point.set(d.getClosestPoint(p));

        if ((d.i_drawing_stage == 0) && (d.i_circle_drawing_stage == 0)) {
            if (OritaCalc.distance_circumference(p, d.closest_circumference) > d.selectionDistance) {
                return;
            }

            d.i_drawing_stage = 0;
            d.i_circle_drawing_stage = 1;
            d.circle_step[1].set(d.closest_circumference);
            d.circle_step[1].setColor(LineColor.GREEN_6);
            return;
        }

        if ((d.i_drawing_stage == 0) && (d.i_circle_drawing_stage == 1)) {
            if (p.distance(d.closest_point) > d.selectionDistance) {
                return;
            }

            d.i_drawing_stage = 1;
            d.i_circle_drawing_stage = 2;
            d.line_step[1].set(p, d.closest_point);
            d.line_step[1].setColor(LineColor.CYAN_3);
            d.circle_step[2].set(d.circle_step[1]);
            d.circle_step[2].setColor(LineColor.CYAN_3);
        }
    }

    //マウス操作(mouseMode==48 同心円　線分入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        if ((d.i_drawing_stage == 1) && (d.i_circle_drawing_stage == 2)) {
            d.line_step[1].setA(p);
            d.circle_step[2].setR(d.circle_step[1].getRadius() + d.line_step[1].getLength());
        }
    }

    //マウス操作(mouseMode==48 同心円　線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if ((d.i_drawing_stage == 1) && (d.i_circle_drawing_stage == 2)) {
            d.i_drawing_stage = 0;
            d.i_circle_drawing_stage = 0;

            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            d.closest_point.set(d.getClosestPoint(p));
            d.line_step[1].setA(d.closest_point);
            if (p.distance(d.closest_point) <= d.selectionDistance) {
                if (d.line_step[1].getLength() > 0.00000001) {
                    d.addLineSegment(d.line_step[1]);
                    d.circle_step[2].setR(d.circle_step[1].getRadius() + d.line_step[1].getLength());
                    d.addCircle(d.circle_step[2]);
                    d.record();
                }
            }
        }
    }
}
