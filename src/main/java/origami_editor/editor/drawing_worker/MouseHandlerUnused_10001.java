package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerUnused_10001 extends BaseMouseHandler {
    public MouseHandlerUnused_10001(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.UNUSED_10001;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //Work when operating the mouse (when the button is pressed with mouseMode == 10001)
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

    //マウス操作(mouseMode==10001　でドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(mouseMode==10001　でボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.i_drawing_stage == 3) {
            d.i_drawing_stage = 0;
        }
    }
}
