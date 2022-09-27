package oriedita.editor.action;

import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MouseHandlerPerpendicularDraw extends BaseMouseHandlerInputRestricted {
    @Inject
    public MouseHandlerPerpendicularDraw() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.PERPENDICULAR_DRAW_9;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        if (d.getLineStep().size() == 0) {
            super.mouseMoved(p0);
        }
    }

//52 52 52 52 52    mouseMode==52　;連続折り返しモード ****************************************

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.getCamera().TV2object(p0));

        //Step 1: Click a point
        if (d.getLineStep().size() == 0) {
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, d.getLineColor()));
                return;
            }
        }

        //Step 2: Click a destination line / base line
        if (d.getLineStep().size() == 1) {

            LineSegment closestLineSegment = new LineSegment();
            closestLineSegment.set(d.getClosestLineSegment(p));

            if (!(OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance())) {
                return;
            }
            closestLineSegment.setColor(LineColor.GREEN_6);
            d.lineStepAdd(closestLineSegment);

            //Step 3 (situational if clicked base line): Show purple candidate line if the selected line goes through the selected point
            if (OritaCalc.determineLineSegmentDistance(d.getLineStep().get(0).getA(), d.getLineStep().get(1)) < Epsilon.UNKNOWN_1EN4) {
                d.lineStepAdd(new LineSegment(d.getLineStep().get(0).getA(), OritaCalc.findProjection(OritaCalc.moveParallel(d.getLineStep().get(1), 25.0), d.getLineStep().get(0).getA())));
                d.lineStepAdd(new LineSegment(d.getLineStep().get(0).getA(), OritaCalc.findProjection(OritaCalc.moveParallel(d.getLineStep().get(1), -25.0), d.getLineStep().get(0).getA())));
                d.getLineStep().get(2).setColor(LineColor.PURPLE_8);
                d.getLineStep().get(3).setColor(LineColor.PURPLE_8);
            }
            return;
        }

        //Continuation from step 3: Click a final destination line
        if (d.getLineStep().size() == 4) {

            LineSegment closestLineSegment = new LineSegment();
            closestLineSegment.set(d.getClosestLineSegment(p));

            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance()) {
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

        if (d.getLineStep().size() == 2) {

            //直線t上の点pの影の位置（点pと最も近い直線t上の位置）を求める。public Ten oc.kage_motome(Tyokusen t,Ten p){
            LineSegment add_sen = new LineSegment(d.getLineStep().get(0).getA(), OritaCalc.findProjection(OritaCalc.lineSegmentToStraightLine(d.getLineStep().get(1)), d.getLineStep().get(0).getA()), d.getLineColor());

            if (Epsilon.high.gt0(add_sen.determineLength())) {
                d.addLineSegment(add_sen);
                d.record();
            }

            d.getLineStep().clear();
        } else if (d.getLineStep().size() == 5) {

            LineSegment point = d.getLineStep().get(0); //Point
            LineSegment perpendicular = d.getLineStep().get(2); //One of the two purple indicators
            LineSegment destinationLine = d.getLineStep().get(4); //Third line

            point.setB(new Point(point.determineAX() + perpendicular.determineBX() - perpendicular.determineAX(), point.determineAY() + perpendicular.determineBY() - perpendicular.determineAY()));

            if (s_step_additional_intersection(4, point, destinationLine, d.getLineColor()) > 0) {
                d.addLineSegment(destinationLine);
                d.record();
            }

            d.getLineStep().clear();
        }
    }

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
            d.getLineStep().get(i_e_d).set(add_sen);
            return 1;
        }

        return -500;
    }
}
