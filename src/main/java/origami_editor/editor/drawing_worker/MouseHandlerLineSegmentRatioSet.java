package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerLineSegmentRatioSet extends BaseMouseHandler{

    public MouseHandlerLineSegmentRatioSet(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.LINE_SEGMENT_RATIO_SET_28;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        d.mMoved_m_00a(p0, d.lineColor);//マウスで選択できる候補点を表示する。近くに既成の点があるときはその点、無いときはマウスの位置自身が候補点となる。
    }

    //マウス操作(mouseMode==28線分内分入力 でボタンを押したとき)時の作業----------------------------------------------------
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

    //マウス操作(mouseMode==28線分入力 でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.line_step[1].setA(p);

        if (d.gridInputAssist) {
            d.closest_point.set(d.getClosestPoint(p));
            d.i_candidate_stage = 1;
            if (p.distance(d.closest_point) < d.selectionDistance) {
                d.line_candidate[1].set(d.closest_point, d.closest_point);
            } else {
                d.line_candidate[1].set(p, p);
            }
            d.line_candidate[1].setColor(d.lineColor);
            d.line_step[1].setA(d.line_candidate[1].getA());
        }
    }

    //マウス操作(mouseMode==28線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
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
            if ((d.internalDivisionRatio_s == 0.0) && (d.internalDivisionRatio_t == 0.0)) {
            }
            if ((d.internalDivisionRatio_s == 0.0) && (d.internalDivisionRatio_t != 0.0)) {
                d.addLineSegment(d.line_step[1]);
            }
            if ((d.internalDivisionRatio_s != 0.0) && (d.internalDivisionRatio_t == 0.0)) {
                d.addLineSegment(d.line_step[1]);
            }
            if ((d.internalDivisionRatio_s != 0.0) && (d.internalDivisionRatio_t != 0.0)) {
                LineSegment s_ad = new LineSegment();
                s_ad.setColor(d.lineColor);
                double nx = (d.internalDivisionRatio_t * d.line_step[1].getBX() + d.internalDivisionRatio_s * d.line_step[1].getAX()) / (d.internalDivisionRatio_s + d.internalDivisionRatio_t);
                double ny = (d.internalDivisionRatio_t * d.line_step[1].getBY() + d.internalDivisionRatio_s * d.line_step[1].getAY()) / (d.internalDivisionRatio_s + d.internalDivisionRatio_t);
                s_ad.set(d.line_step[1].getAX(), d.line_step[1].getAY(), nx, ny);
                d.addLineSegment(s_ad);
                s_ad.set(d.line_step[1].getBX(), d.line_step[1].getBY(), nx, ny);
                d.addLineSegment(s_ad);
            }
            d.record();
        }
    }
}
