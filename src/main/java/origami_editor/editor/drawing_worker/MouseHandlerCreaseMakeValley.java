package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerCreaseMakeValley extends BaseMouseHandler{

    public MouseHandlerCreaseMakeValley(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_MAKE_VALLEY_24;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==24 "->V" でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        d.mPressed_A_box_select(p0);
    }

    //マウス操作(mouseMode==24でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        d.mDragged_A_box_select(p0);
    }

    //マウス操作(mouseMode==24 でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {//ここの処理の終わりに fix2(0.001,0.5);　をするのは、元から折線だったものと、補助線から変換した折線との組合せで頻発するT字型不接続を修正するため
        d.i_drawing_stage = 0;

        if (d.p19_1.distance(p0) > 0.000001) {
            if (d.insideToValley(d.p19_1, p0)) {
                d.fix2(0.001, 0.5);
                d.record();
            }
        }
        if (d.p19_1.distance(p0) <= 0.000001) {
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            if (d.foldLineSet.closestLineSegmentDistance(p) < d.selectionDistance) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                d.foldLineSet.closestLineSegmentSearch(p).setColor(LineColor.BLUE_2);
                d.fix2(0.001, 0.5);
                d.record();
            }
        }
    }
}
