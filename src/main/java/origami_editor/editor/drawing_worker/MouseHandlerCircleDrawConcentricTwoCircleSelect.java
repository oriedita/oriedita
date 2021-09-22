package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerCircleDrawConcentricTwoCircleSelect extends BaseMouseHandler{
    Circle closest_circumference = new Circle(100000.0, 100000.0, 10.0,LineColor.PURPLE_8); //Circle with the circumference closest to the mouse


    public MouseHandlerCircleDrawConcentricTwoCircleSelect(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CIRCLE_DRAW_CONCENTRIC_TWO_CIRCLE_SELECT_50;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        closest_circumference.set(d.getClosestCircleMidpoint(p));
        d.closest_point.set(d.getClosestPoint(p));

        if ((d.i_drawing_stage == 0) && (d.i_circle_drawing_stage == 0)) {
            if (OritaCalc.distance_circumference(p, closest_circumference) > d.selectionDistance) {
                return;
            }

            d.i_drawing_stage = 0;
            d.i_circle_drawing_stage = 1;
            d.circle_step[1].set(closest_circumference);
            d.circle_step[1].setColor(LineColor.GREEN_6);
            return;
        }

        if ((d.i_drawing_stage == 0) && (d.i_circle_drawing_stage == 1)) {
            if (OritaCalc.distance_circumference(p, closest_circumference) > d.selectionDistance) {
                return;
            }

            d.i_drawing_stage = 0;
            d.i_circle_drawing_stage = 2;
            d.circle_step[2].set(closest_circumference);
            d.circle_step[2].setColor(LineColor.GREEN_6);
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if ((d.i_drawing_stage == 0) && (d.i_circle_drawing_stage == 2)) {
            d.i_circle_drawing_stage = 0;
            double add_r = (OritaCalc.distance(d.circle_step[1].getCenter(), d.circle_step[2].getCenter()) - d.circle_step[1].getRadius() - d.circle_step[2].getRadius()) * 0.5;

            if (Math.abs(add_r) > 0.00000001) {
                double new_r1 = add_r + d.circle_step[1].getRadius();
                double new_r2 = add_r + d.circle_step[2].getRadius();

                if ((new_r1 > 0.00000001) && (new_r2 > 0.00000001)) {
                    d.circle_step[1].setR(new_r1);
                    d.circle_step[1].setColor(LineColor.CYAN_3);
                    d.addCircle(d.circle_step[1]);
                    d.circle_step[2].setR(new_r2);
                    d.circle_step[2].setColor(LineColor.CYAN_3);
                    d.addCircle(d.circle_step[2]);
                    d.record();
                }
            }
        }

    }
}
