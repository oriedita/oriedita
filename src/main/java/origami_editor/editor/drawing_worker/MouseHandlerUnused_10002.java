package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerUnused_10002 extends BaseMouseHandler{
    public MouseHandlerUnused_10002(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.UNUSED_10002;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==10002　でボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.closest_lineSegment.set(d.getClosestLineSegment(p));
        if (OritaCalc.distance_lineSegment(p, d.closest_lineSegment) < d.selectionDistance) {
            d.i_drawing_stage = d.i_drawing_stage + 1;
            d.line_step[d.i_drawing_stage].set(d.closest_lineSegment);//line_step[i_egaki_dankai].setcolor(i_egaki_dankai);
            d.line_step[d.i_drawing_stage].setColor(LineColor.GREEN_6);
        }
    }

    //マウス操作(mouseMode==10002　でドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(mouseMode==10002　でボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.i_drawing_stage == 3) {
            d.i_drawing_stage = 0;
        }
    }
}
