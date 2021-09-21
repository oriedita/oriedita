package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerCircleDrawInverted extends BaseMouseHandler{
    public MouseHandlerCircleDrawInverted(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CIRCLE_DRAW_INVERTED_46;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        d.closest_circumference.set(d.getClosestCircleMidpoint(p));

        if (d.i_drawing_stage + d.i_circle_drawing_stage == 0) {
            d.closest_lineSegment.set(d.getClosestLineSegment(p));

            if (OritaCalc.distance_lineSegment(p, d.closest_lineSegment) < OritaCalc.distance_circumference(p, d.closest_circumference)) {//線分の方が円周より近い
                d.i_drawing_stage = 0;
                d.i_circle_drawing_stage = 0;
                if (OritaCalc.distance_lineSegment(p, d.closest_lineSegment) > d.selectionDistance) {
                    return;
                }
                d.i_drawing_stage = 1;
                d.i_circle_drawing_stage = 0;
                d.line_step[1].set(d.closest_lineSegment);
                d.line_step[1].setColor(LineColor.GREEN_6);
                return;
            }

            d.i_drawing_stage = 0;
            d.i_circle_drawing_stage = 0;
            if (OritaCalc.distance_circumference(p, d.closest_circumference) > d.selectionDistance) {
                return;
            }

            d.i_drawing_stage = 0;
            d.i_circle_drawing_stage = 1;
            d.circle_step[1].set(d.closest_circumference);
            d.circle_step[1].setColor(LineColor.GREEN_6);
            return;
        }

        if (d.i_drawing_stage + d.i_circle_drawing_stage == 1) {
            if (OritaCalc.distance_circumference(p, d.closest_circumference) > d.selectionDistance) {
                return;
            }
            d.i_circle_drawing_stage = d.i_circle_drawing_stage + 1;
            d.circle_step[d.i_circle_drawing_stage].set(d.closest_circumference);
            d.circle_step[d.i_circle_drawing_stage].setColor(LineColor.RED_1);
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if ((d.i_drawing_stage == 1) && (d.i_circle_drawing_stage == 1)) {

            d.add_hanten(d.line_step[1], d.circle_step[1]);
            d.i_drawing_stage = 0;
            d.i_circle_drawing_stage = 0;
        }

        if ((d.i_drawing_stage == 0) && (d.i_circle_drawing_stage == 2)) {
            d.add_hanten(d.circle_step[1], d.circle_step[2]);
            d.i_drawing_stage = 0;
            d.i_circle_drawing_stage = 0;
        }
    }
}
