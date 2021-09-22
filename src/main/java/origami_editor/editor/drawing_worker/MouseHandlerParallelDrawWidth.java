package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerParallelDrawWidth extends BaseMouseHandler{
    public MouseHandlerParallelDrawWidth(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.PARALLEL_DRAW_WIDTH_51;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==51 平行線　幅指定入力モード　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.closest_point.set(d.getClosestPoint(p));

        if ((d.i_drawing_stage == 0) && (d.circleStep.size() == 0)) {
            d.closest_lineSegment.set(d.getClosestLineSegment(p));
            if (OritaCalc.distance_lineSegment(p, d.closest_lineSegment) < d.selectionDistance) {
                d.i_drawing_stage = 1;
                d.line_step[1].set(d.closest_lineSegment);
                d.line_step[1].setColor(LineColor.GREEN_6);
            }
            return;
        }

        if ((d.i_drawing_stage == 1) && (d.circleStep.size() == 0)) {
            if (p.distance(d.closest_point) > d.selectionDistance) {
                return;
            }
            d.i_drawing_stage = 4;
            d.line_step[2].set(p, d.closest_point);
            d.line_step[2].setColor(LineColor.CYAN_3);
            d.line_step[3].set(d.line_step[1]);
            d.line_step[3].setColor(LineColor.PURPLE_8);
            d.line_step[4].set(d.line_step[1]);
            d.line_step[4].setColor(LineColor.PURPLE_8);
            return;
        }


        if ((d.i_drawing_stage == 4) && (d.circleStep.size() == 0)) {
            d.i_drawing_stage = 3;
            LineSegment closest_step_lineSegment = d.get_moyori_step_lineSegment(p, 3, 4);

            d.line_step[3].set(closest_step_lineSegment);
        }
    }

    //マウス操作(mouseMode==51 平行線　幅指定入力モード　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        if ((d.i_drawing_stage == 4) && (d.circleStep.size() == 0)) {
            d.line_step[2].setA(p);
            d.line_step[3].set(OritaCalc.moveParallel(d.line_step[1], d.line_step[2].getLength()));
            d.line_step[3].setColor(LineColor.PURPLE_8);
            d.line_step[4].set(OritaCalc.moveParallel(d.line_step[1], -d.line_step[2].getLength()));
            d.line_step[4].setColor(LineColor.PURPLE_8);
        }
    }

    //マウス操作(mouseMode==51 平行線　幅指定入力モード　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.closest_point.set(d.getClosestPoint(p));

        if ((d.i_drawing_stage == 4) && (d.circleStep.size() == 0)) {
            if (p.distance(d.closest_point) >= d.selectionDistance) {
                d.i_drawing_stage = 1;
                return;
            }

            d.line_step[2].setA(d.closest_point);

            if (d.line_step[2].getLength() < 0.00000001) {
                d.i_drawing_stage = 1;
                return;
            }
            d.line_step[3].set(OritaCalc.moveParallel(d.line_step[1], d.line_step[2].getLength()));
            d.line_step[3].setColor(LineColor.PURPLE_8);
            d.line_step[4].set(OritaCalc.moveParallel(d.line_step[1], -d.line_step[2].getLength()));
            d.line_step[4].setColor(LineColor.PURPLE_8);
        }


        if ((d.i_drawing_stage == 3) && (d.circleStep.size() == 0)) {
            d.i_drawing_stage = 0;

            d.line_step[3].setColor(d.lineColor);
            d.addLineSegment(d.line_step[3]);
            d.record();
        }
    }
}
