package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerCircleDrawConcentricSelect extends BaseMouseHandler {
    public MouseHandlerCircleDrawConcentricSelect(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CIRCLE_DRAW_CONCENTRIC_SELECT_49;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==49 同心円　同心円入力　でボタンを押したとき)時の作業----------------------------------------------------
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
            if (OritaCalc.distance_circumference(p, d.closest_circumference) > d.selectionDistance) {
                return;
            }

            d.i_drawing_stage = 0;
            d.i_circle_drawing_stage = 2;
            d.circle_step[2].set(d.closest_circumference);
            d.circle_step[2].setColor(LineColor.PURPLE_8);
            return;
        }

        if ((d.i_drawing_stage == 0) && (d.i_circle_drawing_stage == 2)) {
            if (OritaCalc.distance_circumference(p, d.closest_circumference) > d.selectionDistance) {
                return;
            }

            d.i_drawing_stage = 0;
            d.i_circle_drawing_stage = 3;
            d.circle_step[3].set(d.closest_circumference);
            d.circle_step[3].setColor(LineColor.PURPLE_8);
        }
    }

    //マウス操作(mouseMode==49 同心円　同心円入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {

    }

    //マウス操作(mouseMode==49 同心円　同心円入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if ((d.i_drawing_stage == 0) && (d.i_circle_drawing_stage == 3)) {
            d.i_circle_drawing_stage = 0;
            double add_r = d.circle_step[3].getRadius() - d.circle_step[2].getRadius();
            if (Math.abs(add_r) > 0.00000001) {
                double new_r = add_r + d.circle_step[1].getRadius();

                if (new_r > 0.00000001) {
                    d.circle_step[1].setR(new_r);
                    d.circle_step[1].setColor(LineColor.CYAN_3);
                    d.addCircle(d.circle_step[1]);
                    d.record();
                }
            }
        }
    }
}
