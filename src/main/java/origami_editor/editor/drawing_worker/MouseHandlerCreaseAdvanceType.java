package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerCreaseAdvanceType extends BaseMouseHandler{
    public MouseHandlerCreaseAdvanceType(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_ADVANCE_TYPE_30;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    public void mousePressed(Point p0) {    //マウス操作(mouseMode==4線_変換　でボタンを押したとき)時の作業
        Point p  =new Point();
        p.set(d.camera.TV2object(p0));
        d.lineSegment_ADVANCE_CREASE_TYPE_30 = null;
        if (d.foldLineSet.closestLineSegmentDistance(p) < d.selectionDistance) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
            d.lineSegment_ADVANCE_CREASE_TYPE_30 = d.foldLineSet.closestLineSegmentSearch(p);
            LineSegment s01 = new LineSegment();
            s01.set(OritaCalc.lineSegment_double(d.lineSegment_ADVANCE_CREASE_TYPE_30, 0.01));
            d.lineSegment_ADVANCE_CREASE_TYPE_30.setB(s01.getB());
        }
    }

    public void mouseDragged(Point p0) {//マウス操作(mouseMode==4線_変換　でドラッグしたとき)を行う関数
        if (d.lineSegment_ADVANCE_CREASE_TYPE_30 != null) {

            LineSegment s01 = new LineSegment();
            s01.set(OritaCalc.lineSegment_double(d.lineSegment_ADVANCE_CREASE_TYPE_30, 100.0));
            d.lineSegment_ADVANCE_CREASE_TYPE_30.setB(s01.getB());
            d.lineSegment_ADVANCE_CREASE_TYPE_30 = null;
        }

    }

    //マウス操作(mouseMode==30 除け_線_変換　でボタンを離したとき)を行う関数（背景に展開図がある場合用）
    public void mouseReleased(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        if (d.lineSegment_ADVANCE_CREASE_TYPE_30 != null) {

            LineSegment s01 = new LineSegment();
            s01.set(OritaCalc.lineSegment_double(d.lineSegment_ADVANCE_CREASE_TYPE_30, 100.0));
            d.lineSegment_ADVANCE_CREASE_TYPE_30.setB(s01.getB());

            LineColor ic_temp = d.lineSegment_ADVANCE_CREASE_TYPE_30.getColor();
            int is_temp = d.lineSegment_ADVANCE_CREASE_TYPE_30.getSelected();

            if ((ic_temp == LineColor.BLACK_0) && (is_temp == 0)) {
                d.lineSegment_ADVANCE_CREASE_TYPE_30.setSelected(2);
            } else if ((ic_temp == LineColor.BLACK_0) && (is_temp == 2)) {
                d.lineSegment_ADVANCE_CREASE_TYPE_30.setColor(LineColor.RED_1);
                d.lineSegment_ADVANCE_CREASE_TYPE_30.setSelected(0);
            } else if ((ic_temp == LineColor.RED_1) && (is_temp == 0)) {
                d.lineSegment_ADVANCE_CREASE_TYPE_30.setColor(LineColor.BLUE_2);
            } else if ((ic_temp == LineColor.BLUE_2) && (is_temp == 0)) {
                d.lineSegment_ADVANCE_CREASE_TYPE_30.setColor(LineColor.BLACK_0);
            }

            d.record();
        }
    }
}
