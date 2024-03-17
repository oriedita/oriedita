package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.SYMMETRIC_DRAW_10)
public class MouseHandlerSymmetricDraw extends BaseMouseHandlerInputRestricted {
    @Inject
    public MouseHandlerSymmetricDraw() {
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p;
        LineSegment line;
        p = d.getCamera().TV2object(p0);

        if (d.getLineStep().isEmpty() || d.getLineStep().get(0).determineLength() > 0) {
            if (d.getLineStep().isEmpty() && d.getClosestPoint(p).distance(p) > d.getSelectionDistance()) {
                line = d.getClosestLineSegment(p);
                if (OritaCalc.determineLineSegmentDistance(p, line) < d.getSelectionDistance()) {
                    d.lineStepAdd(line.withColor(LineColor.GREEN_6));
                }
                return;
            }
            if(!d.getLineStep().isEmpty()){
                line = d.getClosestLineSegment(p);
                if (OritaCalc.determineLineSegmentDistance(p, line) < d.getSelectionDistance() && OritaCalc.isLineSegmentParallel(d.getLineStep().get(0), line) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {
                    d.lineStepAdd(line.withColor(LineColor.GREEN_6));
                }
                return;
            }
        }

        if (d.getLineStep().isEmpty() || d.getLineStep().get(0).determineLength() <= 0.0) {
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, d.getLineColor()));
            }

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
        if(d.getLineStep().size() == 2 && d.getLineStep().get(0).determineLength() > 0){
            Point cross = OritaCalc.findIntersection(d.getLineStep().get(0), d.getLineStep().get(1));
            Point t_taisyou = OritaCalc.findLineSymmetryPoint(cross, d.getLineStep().get(1).determineFurthestEndpoint(cross), d.getLineStep().get(0).determineFurthestEndpoint(cross));
            LineSegment add_sen = new LineSegment(cross, t_taisyou, d.getLineColor());
            add_sen = d.extendToIntersectionPoint(add_sen);

            if (Epsilon.high.gt0(add_sen.determineLength())) {
                d.addLineSegment(add_sen);
                d.record();
            }
            d.getLineStep().clear();
        }

        if (d.getLineStep().size() == 3) {
            //２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){
            Point t_taisyou = OritaCalc.findLineSymmetryPoint(d.getLineStep().get(1).getA(), d.getLineStep().get(2).getA(), d.getLineStep().get(0).getA());

            LineSegment add_sen = new LineSegment(d.getLineStep().get(1).getA(), t_taisyou);
            add_sen = d.extendToIntersectionPoint(add_sen);
            add_sen.setColor(d.getLineColor());

            if (Epsilon.high.gt0(add_sen.determineLength())) {
                d.addLineSegment(add_sen);
                d.record();
            }

            d.getLineStep().clear();
        }
    }
}
