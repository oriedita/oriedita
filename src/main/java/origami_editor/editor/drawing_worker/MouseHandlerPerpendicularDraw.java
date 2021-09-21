package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerPerpendicularDraw extends BaseMouseHandler{
    private final MouseHandlerPolygonSetNoCorners mouseHandlerPolygonSetNoCorners;

    public MouseHandlerPerpendicularDraw(DrawingWorker d) {
        super(d);
        this.mouseHandlerPolygonSetNoCorners = new MouseHandlerPolygonSetNoCorners(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.PERPENDICULAR_DRAW_9;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        if (d.i_drawing_stage == 0) {
            mouseHandlerPolygonSetNoCorners.mouseMoved(p0);
        }
    }

//52 52 52 52 52    mouseMode==52　;連続折り返しモード ****************************************

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        if (d.i_drawing_stage == 0) {
            d.closest_point.set(d.getClosestPoint(p));
            if (p.distance(d.closest_point) < d.selectionDistance) {
                d.i_drawing_stage = d.i_drawing_stage + 1;
                d.line_step[d.i_drawing_stage].set(d.closest_point, d.closest_point);
                d.line_step[d.i_drawing_stage].setColor(d.lineColor);
                return;
            }
        }

        if (d.i_drawing_stage == 1) {
            d.closest_lineSegment.set(d.getClosestLineSegment(p));
            if (OritaCalc.distance_lineSegment(p, d.closest_lineSegment) < d.selectionDistance) {
                d.i_drawing_stage = d.i_drawing_stage + 1;
                d.line_step[d.i_drawing_stage].set(d.closest_lineSegment);
                d.line_step[d.i_drawing_stage].setColor(LineColor.GREEN_6);
                return;
            }
            d.i_drawing_stage = 0;
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.i_drawing_stage == 2) {
            d.i_drawing_stage = 0;
            //直線t上の点pの影の位置（点pと最も近い直線t上の位置）を求める。public Ten oc.kage_motome(Tyokusen t,Ten p){

            LineSegment add_sen = new LineSegment(d.line_step[1].getA(), OritaCalc.findProjection(OritaCalc.lineSegmentToStraightLine(d.line_step[2]), d.line_step[1].getA()), d.lineColor);
            if (add_sen.getLength() > 0.00000001) {
                d.addLineSegment(add_sen);
                d.record();
            }
        }
    }
}
