package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerSymmetricDraw extends BaseMouseHandler{
    private final MouseHandlerPolygonSetNoCorners mouseHandlerPolygonSetNoCorners;

    public MouseHandlerSymmetricDraw(DrawingWorker d) {
        super(d);
        mouseHandlerPolygonSetNoCorners = new MouseHandlerPolygonSetNoCorners(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.SYMMETRIC_DRAW_10;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        mouseHandlerPolygonSetNoCorners.mouseMoved(p0);
    }//近い既存点のみ表示

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.closest_point.set(d.getClosestPoint(p));
        if (p.distance(d.closest_point) < d.selectionDistance) {
            d.i_drawing_stage = d.i_drawing_stage + 1;
            d.line_step[d.i_drawing_stage].set(d.closest_point, d.closest_point);
            d.line_step[d.i_drawing_stage].setColor(d.lineColor);
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

//--------------------------------------------
//29 29 29 29 29 29 29 29  mouseMode==29正多角形入力	入力 29 29 29 29 29 29 29 29
    //動作概要　
    //mouseMode==1と線分分割以外は同じ　
    //

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.i_drawing_stage == 3) {
            d.i_drawing_stage = 0;

            //２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){
            Point t_taisyou = new Point();
            t_taisyou.set(OritaCalc.findLineSymmetryPoint(d.line_step[2].getA(), d.line_step[3].getA(), d.line_step[1].getA()));

            LineSegment add_sen = new LineSegment(d.line_step[2].getA(), t_taisyou);

            add_sen.set(d.extendToIntersectionPoint(add_sen));
            add_sen.setColor(d.lineColor);
            if (add_sen.getLength() > 0.00000001) {
                d.addLineSegment(add_sen);
                d.record();
            }
        }
    }
}
