package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerInward extends BaseMouseHandler {
    private final MouseHandlerPolygonSetNoCorners mouseHandlerPolygonSetNoCorners;

    public MouseHandlerInward(DrawingWorker d) {
super(d);
this.mouseHandlerPolygonSetNoCorners = new MouseHandlerPolygonSetNoCorners(d);
    }

//------

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.INWARD_8;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        mouseHandlerPolygonSetNoCorners.mouseMoved(p0);
    }//近い既存点のみ表示

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {

        Point p  =new Point();
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

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.i_drawing_stage == 3) {
            d.i_drawing_stage = 0;

            //三角形の内心を求める	public Ten oc.center(Ten ta,Ten tb,Ten tc)
            Point center = new Point();
            center.set(OritaCalc.center(d.line_step[1].getA(), d.line_step[2].getA(), d.line_step[3].getA()));

            LineSegment add_sen1 = new LineSegment(d.line_step[1].getA(), center, d.lineColor);
            if (add_sen1.getLength() > 0.00000001) {
                d.addLineSegment(add_sen1);
            }
            LineSegment add_sen2 = new LineSegment(d.line_step[2].getA(), center, d.lineColor);
            if (add_sen2.getLength() > 0.00000001) {
                d.addLineSegment(add_sen2);
            }
            LineSegment add_sen3 = new LineSegment(d.line_step[3].getA(), center, d.lineColor);
            if (add_sen3.getLength() > 0.00000001) {
                d.addLineSegment(add_sen3);
            }
            d.record();
        }
    }
}
