package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerCircleDraw extends BaseMouseHandler{

    public MouseHandlerCircleDraw(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CIRCLE_DRAW_42;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==42 円入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        d.i_drawing_stage = 1;
        d.i_circle_drawing_stage = 1;

        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.closest_point.set(d.getClosestPoint(p));
        if (p.distance(d.closest_point) > d.selectionDistance) {
            d.i_drawing_stage = 0;
            d.i_circle_drawing_stage = 0;
        }
        d.line_step[1].set(p, d.closest_point);
        d.line_step[1].setColor(LineColor.CYAN_3);
        d.circle_step[1].set(d.closest_point.getX(), d.closest_point.getY(), 0.0);
        d.circle_step[1].setColor(LineColor.CYAN_3);
    }

    //マウス操作(mouseMode==42 円入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.line_step[1].setA(p);
        d.circle_step[1].setR(OritaCalc.distance(d.line_step[1].getA(), d.line_step[1].getB()));
    }

    //マウス操作(mouseMode==42 円入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.i_drawing_stage == 1) {
            d.i_drawing_stage = 0;
            d.i_circle_drawing_stage = 0;

            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            d.closest_point.set(d.getClosestPoint(p));
            d.line_step[1].setA(d.closest_point);
            if (p.distance(d.closest_point) <= d.selectionDistance) {
                if (d.line_step[1].getLength() > 0.00000001) {
                    d.addCircle(d.line_step[1].getBX(), d.line_step[1].getBY(), d.line_step[1].getLength(), LineColor.CYAN_3);
                    d.record();
                }
            }
        }
    }
}
