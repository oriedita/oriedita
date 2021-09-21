package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerBackgroundChangePosition extends BaseMouseHandler{
    public MouseHandlerBackgroundChangePosition(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.BACKGROUND_CHANGE_POSITION_26;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        if (d.i_drawing_stage == 3) {
            d.i_drawing_stage = 4;
            d.closest_point.set(d.getClosestPoint(p));
            if (p.distance(d.closest_point) < d.selectionDistance) {
                p.set(d.closest_point);
            }
            d.line_step[4].set(p, p);
            d.line_step[4].setColor(LineColor.fromNumber(d.i_drawing_stage));
        }

        if (d.i_drawing_stage == 2) {
            d.i_drawing_stage = 3;
            d.closest_point.set(d.getClosestPoint(p));
            if (p.distance(d.closest_point) < d.selectionDistance) {
                p.set(d.closest_point);
            }
            d.line_step[3].set(p, p);
            d.line_step[3].setColor(LineColor.fromNumber(d.i_drawing_stage));
        }

        if (d.i_drawing_stage == 1) {
            d.i_drawing_stage = 2;
            d.line_step[2].set(p, p);
            d.line_step[2].setColor(LineColor.fromNumber(d.i_drawing_stage));
        }

        if (d.i_drawing_stage == 0) {
            d.i_drawing_stage = 1;
            d.line_step[1].set(p, p);
            d.line_step[1].setColor(LineColor.fromNumber(d.i_drawing_stage));
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        d.app.Button_shared_operation();
        LineSegment s_1 = new LineSegment();
        s_1.set(d.get_s_step(1));
        LineSegment s_2 = new LineSegment();
        s_2.set(d.get_s_step(2));
        LineSegment s_3 = new LineSegment();
        s_3.set(d.get_s_step(3));
        LineSegment s_4 = new LineSegment();
        s_4.set(d.get_s_step(4));

        d.app.backgroundModel.setLockBackground(false);

        d.app.background_set(d.camera.object2TV(s_1.getA()),
                d.camera.object2TV(s_2.getA()),
                d.camera.object2TV(s_3.getA()),
                d.camera.object2TV(s_4.getA()));
    }
}
