package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerCreaseUnselect extends BaseMouseHandler{
    public MouseHandlerCreaseUnselect(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_UNSELECT_20;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==19  select　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        d.mPressed_A_box_select(p0);
    }

    //マウス操作(mouseMode==19 select　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        d.mDragged_A_box_select(p0);
    }

    //マウス操作(mouseMode==20 select　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        d.i_drawing_stage = 0;
        d.unselect(d.p19_1, p0);

        if (d.p19_1.distance(p0) <= 0.000001) {
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            if (d.foldLineSet.closestLineSegmentDistance(p) < d.selectionDistance) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                d.foldLineSet.closestLineSegmentSearch(p).setSelected(0);
            }
        }
    }
}
