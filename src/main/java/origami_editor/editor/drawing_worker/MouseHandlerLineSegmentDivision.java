package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerLineSegmentDivision extends BaseMouseHandler{

    public MouseHandlerLineSegmentDivision(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.LINE_SEGMENT_DIVISION_27;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        d.mMoved_m_00a(p0, d.lineColor);//マウスで選択できる候補点を表示する。近くに既成の点があるときはその点、無いときはマウスの位置自身が候補点となる。
    }

    //マウス操作(mouseMode==27線分入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        d.i_drawing_stage = 1;
        d.line_step[1].setActive(LineSegment.ActiveState.ACTIVE_B_2);
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.closest_point.set(d.getClosestPoint(p));
        if (p.distance(d.closest_point) < d.selectionDistance) {
            d.line_step[1].set(p, d.closest_point);
            d.line_step[1].setColor(d.lineColor);
            return;
        }
        d.line_step[1].set(p, p);
        d.line_step[1].setColor(d.lineColor);
    }


// 19 19 19 19 19 19 19 19 19 select 選択

    //マウス操作(mouseMode==27線分入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.line_step[1].setA(p);
        if (d.gridInputAssist) {
            d.i_candidate_stage = 0;
            d.closest_point.set(d.getClosestPoint(p));
            if (p.distance(d.closest_point) < d.selectionDistance) {
                d.i_candidate_stage = 1;
                d.line_candidate[1].set(d.closest_point, d.closest_point);
                d.line_candidate[1].setColor(d.lineColor);
                d.line_step[1].setA(d.line_candidate[1].getA());
            }
        }


    }

    //マウス操作(mouseMode==27線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        d.i_drawing_stage = 0;
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        d.line_step[1].setA(p);
        d.closest_point.set(d.getClosestPoint(p));

        if (p.distance(d.closest_point) <= d.selectionDistance) {
            d.line_step[1].setA(d.closest_point);
        }
        if (d.line_step[1].getLength() > 0.00000001) {
            for (int i = 0; i <= d.foldLineDividingNumber - 1; i++) {
                double ax = ((double) (d.foldLineDividingNumber - i) * d.line_step[1].getAX() + (double) i * d.line_step[1].getBX()) / ((double) d.foldLineDividingNumber);
                double ay = ((double) (d.foldLineDividingNumber - i) * d.line_step[1].getAY() + (double) i * d.line_step[1].getBY()) / ((double) d.foldLineDividingNumber);
                double bx = ((double) (d.foldLineDividingNumber - i - 1) * d.line_step[1].getAX() + (double) (i + 1) * d.line_step[1].getBX()) / ((double) d.foldLineDividingNumber);
                double by = ((double) (d.foldLineDividingNumber - i - 1) * d.line_step[1].getAY() + (double) (i + 1) * d.line_step[1].getBY()) / ((double) d.foldLineDividingNumber);
                LineSegment s_ad = new LineSegment(ax, ay, bx, by);
                s_ad.setColor(d.lineColor);
                d.addLineSegment(s_ad);
            }
            d.record();
        }

    }
}
