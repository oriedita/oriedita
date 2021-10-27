package origami_editor.editor.canvas;

import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerParallelDraw extends BaseMouseHandlerInputRestricted {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.PARALLEL_DRAW_40;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        if (d.lineStep.size() == 0) {
            super.mouseMoved(p0);
        }
    }

    //２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){
    //Ten t_taisyou =new Ten(); t_taisyou.set(oc.sentaisyou_ten_motome(lineStep.get(1).geta(),line_step[3].geta(),lineStep.get(0).geta()));

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        if (d.lineStep.size() == 0) {
            Point closestPoint = d.getClosestPoint(p);

            if (p.distance(closestPoint) < d.selectionDistance) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, d.lineColor));
            }
        } else if (d.lineStep.size() == 1) {
            LineSegment closestLineSegment = new LineSegment();
            closestLineSegment.set(d.getClosestLineSegment(p));
            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.selectionDistance) {
                closestLineSegment.setColor(LineColor.GREEN_6);
                d.lineStepAdd(closestLineSegment);
            }
        } else if (d.lineStep.size() == 2) {
            LineSegment closestLineSegment = new LineSegment();
            closestLineSegment.set(d.getClosestLineSegment(p));
            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.selectionDistance) {
                closestLineSegment.setColor(LineColor.GREEN_6);
                d.lineStepAdd(closestLineSegment);
            }
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.lineStep.size() == 3) {
            LineSegment s0 = d.lineStep.get(0);
            LineSegment s1 = d.lineStep.get(1);
            LineSegment s2 = d.lineStep.get(2);

            s0.setB(new Point(s0.determineAX() + s1.determineBX() - s1.determineAX(), s0.determineAY() + s1.determineBY() - s1.determineAY()));

            if (s_step_additional_intersection(2, s0, s2, d.lineColor) > 0) {
                d.addLineSegment(s2);
                d.record();
            }

            d.lineStep.clear();
        }
    }

    /**
     * When i_egaki_dankai is i_e_d, add a temporary fold line s_step [i_e_d + 1] (color is i colo) that
     * extends the line segment s_o to the intersection of s_k while keeping Point a as it is. Returns 1 on success,
     * -500 on failure to add due to some inconvenience.
     */
    public int s_step_additional_intersection(int i_e_d, LineSegment s_o, LineSegment s_k, LineColor icolo) {

        Point cross_point = new Point();

        if (OritaCalc.isLineSegmentParallel(s_o, s_k, Epsilon.UNKNOWN_1EN7) == OritaCalc.ParallelJudgement.PARALLEL_NOT_EQUAL) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            return -500;
        }

        if (OritaCalc.isLineSegmentParallel(s_o, s_k, Epsilon.UNKNOWN_1EN7) == OritaCalc.ParallelJudgement.PARALLEL_EQUAL) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            cross_point.set(s_k.getA());
            if (OritaCalc.distance(s_o.getA(), s_k.getA()) > OritaCalc.distance(s_o.getA(), s_k.getB())) {
                cross_point.set(s_k.getB());
            }
        }

        if (OritaCalc.isLineSegmentParallel(s_o, s_k, Epsilon.UNKNOWN_1EN7) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            cross_point.set(OritaCalc.findIntersection(s_o, s_k));
        }

        LineSegment add_sen = new LineSegment(cross_point, s_o.getA(), icolo);

        if (Epsilon.high.gt0(add_sen.determineLength())) {
            d.lineStep.get(i_e_d).set(add_sen);
            return 1;
        }

        return -500;
    }
}
