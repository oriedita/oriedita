package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerPolygonSetNoCorners extends BaseMouseHandler{

    public MouseHandlerPolygonSetNoCorners(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.POLYGON_SET_NO_CORNERS_29;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        if (d.gridInputAssist) {
            d.line_candidate[1].setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
            d.i_candidate_stage = 0;
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            d.closest_point.set(d.getClosestPoint(p));
            if (p.distance(d.closest_point) < d.selectionDistance) {
                d.i_candidate_stage = 1;
                d.line_candidate[1].set(d.closest_point, d.closest_point);
                d.line_candidate[1].setColor(d.lineColor);
            }
        }
    }

    //マウス操作(mouseMode==29正多角形入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        d.line_step[1].setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);

        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        if (d.i_drawing_stage == 0) {    //第1段階として、点を選択
            d.closest_point.set(d.getClosestPoint(p));
            if (p.distance(d.closest_point) < d.selectionDistance) {
                d.i_drawing_stage = d.i_drawing_stage + 1;
                d.line_step[d.i_drawing_stage].set(d.closest_point, d.closest_point);
                d.line_step[d.i_drawing_stage].setColor(LineColor.MAGENTA_5);
            }
            return;
        }

        if (d.i_drawing_stage == 1) {    //第2段階として、点を選択
            d.closest_point.set(d.getClosestPoint(p));
            if (p.distance(d.closest_point) >= d.selectionDistance) {
                d.i_drawing_stage = 0;
                return;
            }
            if (p.distance(d.closest_point) < d.selectionDistance) {

                d.i_drawing_stage = d.i_drawing_stage + 1;
                d.line_step[d.i_drawing_stage].set(d.closest_point, d.closest_point);
                d.line_step[d.i_drawing_stage].setColor(LineColor.fromNumber(d.i_drawing_stage));
                d.line_step[1].setB(d.line_step[2].getB());
            }
            if (d.line_step[1].getLength() < 0.00000001) {
                d.i_drawing_stage = 0;
            }
        }
    }

    //マウス操作(mouseMode==29正多角形入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
    }

    //マウス操作(mouseMode==29正多角形入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.i_drawing_stage == 2) {
            d.i_drawing_stage = 0;
            LineSegment s_tane = new LineSegment();
            LineSegment s_deki = new LineSegment();


            s_tane.set(d.line_step[1]);
            s_tane.setColor(d.lineColor);
            d.addLineSegment(s_tane);
            for (int i = 2; i <= d.numPolygonCorners; i++) {
                s_deki.set(OritaCalc.lineSegment_rotate(s_tane, (double) (d.numPolygonCorners - 2) * 180.0 / (double) d.numPolygonCorners));
                s_tane.set(s_deki.getB(), s_deki.getA());
                s_tane.setColor(d.lineColor);
                d.addLineSegment(s_tane);

            }
            d.foldLineSet.unselect_all();
            d.record();
        }
    }
}
