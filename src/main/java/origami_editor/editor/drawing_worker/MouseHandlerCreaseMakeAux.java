package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerCreaseMakeAux extends BaseMouseHandler{
    public MouseHandlerCreaseMakeAux(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_MAKE_AUX_60;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==60 "->HK" でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        d.mPressed_A_box_select(p0);
    }

    //マウス操作(mouseMode==60でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        d.mDragged_A_box_select(p0);
    }

    //マウス操作(mouseMode==60 でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        d.i_drawing_stage = 0;

        if (d.p19_1.distance(p0) > 0.000001) {
            if (d.insideToAux(d.p19_1, p0)) {
                d.record();
            }//この関数は不完全なのでまだ未公開20171126
        }

        if (d.p19_1.distance(p0) <= 0.000001) {
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            if (d.foldLineSet.closestLineSegmentDistance(p) < d.selectionDistance) {//点pに最も近い線分の番号での、その距離を返す	public double closestLineSegmentDistance(Ten p)
                if (d.foldLineSet.getColor(d.foldLineSet.closestLineSegmentSearchReversedOrder(p)).getNumber() < 3) {
                    LineSegment add_sen = new LineSegment();
                    add_sen.set(d.foldLineSet.get(d.foldLineSet.closestLineSegmentSearchReversedOrder(p)));
                    add_sen.setColor(LineColor.CYAN_3);

                    d.foldLineSet.deleteLineSegment_vertex(d.foldLineSet.closestLineSegmentSearchReversedOrder(p));
                    d.addLineSegment(add_sen);

                    d.organizeCircles();
                    d.record();
                }
            }
        }
    }
}
