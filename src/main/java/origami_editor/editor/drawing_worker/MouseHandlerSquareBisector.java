package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerSquareBisector extends BaseMouseHandler{
    private final MouseHandlerPolygonSetNoCorners mouseHandlerPolygonSetNoCorners;

    public MouseHandlerSquareBisector(DrawingWorker d) {
        super(d);
        mouseHandlerPolygonSetNoCorners = new MouseHandlerPolygonSetNoCorners(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.SQUARE_BISECTOR_7;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        if ((d.i_drawing_stage >= 0) && (d.i_drawing_stage <= 2)) {
            //Only close existing points are displayed
            mouseHandlerPolygonSetNoCorners.mouseMoved(p0);
        }

    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        if ((d.i_drawing_stage >= 0) && (d.i_drawing_stage <= 2)) {
            d.closest_point.set(d.getClosestPoint(p));
            if (p.distance(d.closest_point) < d.selectionDistance) {
                d.i_drawing_stage++;
                d.line_step[d.i_drawing_stage].set(d.closest_point, d.closest_point);
                d.line_step[d.i_drawing_stage].setColor(d.lineColor);
                return;
            }
        }

        if (d.i_drawing_stage == 3) {
            d.closest_lineSegment.set(d.getClosestLineSegment(p));
            if (OritaCalc.distance_lineSegment(p, d.closest_lineSegment) < d.selectionDistance) {
                d.i_drawing_stage++;
                d.line_step[d.i_drawing_stage].set(d.closest_lineSegment);//line_step[i_egaki_dankai].setcolor(i_egaki_dankai);
                d.line_step[d.i_drawing_stage].setColor(LineColor.GREEN_6);
            }
        }

    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.i_drawing_stage == 4) {
            d.i_drawing_stage = 0;

            //三角形の内心を求める	public Ten oc.naisin(Ten ta,Ten tb,Ten tc)
            Point naisin = new Point();
            naisin.set(OritaCalc.center(d.line_step[1].getA(), d.line_step[2].getA(), d.line_step[3].getA()));

            LineSegment add_sen2 = new LineSegment(d.line_step[2].getA(), naisin);

            //add_sen2とs_step[4]の交点はoc.kouten_motome(Senbun s1,Senbun s2)で求める//２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
            Point cross_point = new Point();
            cross_point.set(OritaCalc.findIntersection(add_sen2, d.line_step[4]));

            LineSegment add_sen = new LineSegment(cross_point, d.line_step[2].getA(), d.lineColor);
            if (add_sen.getLength() > 0.00000001) {
                d.addLineSegment(add_sen);
                d.record();
            }
        }
    }
}
