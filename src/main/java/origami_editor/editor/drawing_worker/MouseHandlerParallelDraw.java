package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerParallelDraw extends BaseMouseHandler{
    private final MouseHandlerPolygonSetNoCorners mouseHandlerPolygonSetNoCorners;

    public MouseHandlerParallelDraw(DrawingWorker d) {
        super(d);
        mouseHandlerPolygonSetNoCorners = new MouseHandlerPolygonSetNoCorners(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.PARALLEL_DRAW_40;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        if (d.i_drawing_stage == 0) {
            mouseHandlerPolygonSetNoCorners.mouseMoved(p0);
        }

    }

// ------------------------------------------------------------
    //２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){
    //Ten t_taisyou =new Ten(); t_taisyou.set(oc.sentaisyou_ten_motome(line_step[2].geta(),line_step[3].geta(),line_step[1].geta()));

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
            return;
        }


        if (d.i_drawing_stage == 2) {
            d.closest_lineSegment.set(d.getClosestLineSegment(p));
            if (OritaCalc.distance_lineSegment(p, d.closest_lineSegment) < d.selectionDistance) {
                d.i_drawing_stage = d.i_drawing_stage + 1;
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
        if (d.i_drawing_stage == 3) {
            d.i_drawing_stage = 0;
            d.line_step[1].setB(new Point(d.line_step[1].getAX() + d.line_step[2].getBX() - d.line_step[2].getAX(), d.line_step[1].getAY() + d.line_step[2].getBY() - d.line_step[2].getAY()));

            if (s_step_tuika_koutenmade(3, d.line_step[1], d.line_step[3], d.lineColor) > 0) {
                d.addLineSegment(d.line_step[4]);
                d.record();
                d.i_drawing_stage = 0;
            }
        }
    }

    //i_egaki_dankaiがi_e_dのときに、線分s_oをTen aはそのままで、Ten b側をs_kの交点までのばした一時折線s_step[i_e_d+1](色はicolo)を追加。成功した場合は1、なんらかの不都合で追加できなかった場合は-500を返す。
    public int s_step_tuika_koutenmade(int i_e_d, LineSegment s_o, LineSegment s_k, LineColor icolo) {

        Point cross_point = new Point();

        if (OritaCalc.parallel_judgement(s_o, s_k, 0.0000001) == OritaCalc.ParallelJudgement.PARALLEL_NOT_EQUAL) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            return -500;
        }

        if (OritaCalc.parallel_judgement(s_o, s_k, 0.0000001) == OritaCalc.ParallelJudgement.PARALLEL_EQUAL) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            cross_point.set(s_k.getA());
            if (OritaCalc.distance(s_o.getA(), s_k.getA()) > OritaCalc.distance(s_o.getA(), s_k.getB())) {
                cross_point.set(s_k.getB());
            }
        }

        if (OritaCalc.parallel_judgement(s_o, s_k, 0.0000001) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            cross_point.set(OritaCalc.findIntersection(s_o, s_k));
        }

        LineSegment add_sen = new LineSegment(cross_point, s_o.getA(), icolo);

        if (add_sen.getLength() > 0.00000001) {
            d.line_step[i_e_d + 1].set(add_sen);
            return 1;
        }
        return -500;
    }
}
