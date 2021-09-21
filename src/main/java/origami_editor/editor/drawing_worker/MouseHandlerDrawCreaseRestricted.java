package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerDrawCreaseRestricted extends BaseMouseHandler{
    public MouseHandlerDrawCreaseRestricted(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.DRAW_CREASE_RESTRICTED_11;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        d.mMoved_m_00a(p0, d.lineColor);
    }//近い既存点のみ表示

    //マウス操作(mouseMode==11線分入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        d.mPressed_m_00a(p0, d.lineColor);
    }

//------

    //マウス操作(mouseMode==11線分入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        d.mDragged_m_00a(p0, d.lineColor);
    }//近い既存点のみ表示

    //マウス操作(mouseMode==11線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.i_drawing_stage == 1) {
            d.i_drawing_stage = 0;

            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            d.closest_point.set(d.getClosestPoint(p));
            d.line_step[1].setA(d.closest_point);
            if (p.distance(d.closest_point) <= d.selectionDistance) {
                if (d.line_step[1].getLength() > 0.00000001) {
                    d.addLineSegment(d.line_step[1]);
                    d.record();
                }
            }
        }
    }
}
