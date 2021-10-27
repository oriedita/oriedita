package origami_editor.editor.canvas;

import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerDoubleSymmetricDraw extends BaseMouseHandlerInputRestricted {
    private final MouseHandlerDrawCreaseRestricted mouseHandlerDrawCreaseRestricted = new MouseHandlerDrawCreaseRestricted();

    @Override
    public void setDrawingWorker(CreasePattern_Worker d) {
        super.setDrawingWorker(d);
        mouseHandlerDrawCreaseRestricted.setDrawingWorker(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.DOUBLE_SYMMETRIC_DRAW_35;
    }

    //マウス操作(mouseMode==35　でドラッグしたとき)を行う関数----------------------------------------------------

    //マウス操作(mouseMode==35　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        d.lineStep.clear();

        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        Point closestPoint = d.getClosestPoint(p);
        if (p.distance(closestPoint) > d.selectionDistance) {
            return;
        }
        d.lineStepAdd(new LineSegment(p, closestPoint, d.lineColor));
    }

    public void mouseDragged(Point p0) {
        mouseHandlerDrawCreaseRestricted.mouseDragged(p0);
    }


    //マウス操作(mouseMode==35　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.lineStep.size() == 1) {
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            Point closestPoint = d.getClosestPoint(p);

            d.lineStep.get(0).setA(closestPoint);
            if (p.distance(closestPoint) <= d.selectionDistance) {
                if (Epsilon.high.gt0(d.lineStep.get(0).determineLength())) {
                    int imax = d.foldLineSet.getTotal();
                    for (int i = 1; i <= imax; i++) {
                        LineSegment s = d.foldLineSet.get(i);
                        LineSegment.Intersection i_lineSegment_intersection_decision = OritaCalc.determineLineSegmentIntersectionSweet(s, d.lineStep.get(0), Epsilon.UNKNOWN_001, Epsilon.UNKNOWN_001);
                        boolean i_jikkou = false;
                        if (i_lineSegment_intersection_decision == LineSegment.Intersection.INTERSECTS_TSHAPE_S1_VERTICAL_BAR_25) {
                            i_jikkou = true;
                        }//T字型 s1が縦棒
                        if (i_lineSegment_intersection_decision == LineSegment.Intersection.INTERSECTS_TSHAPE_S1_VERTICAL_BAR_26) {
                            i_jikkou = true;
                        }//T字型 s1が縦棒

                        if (i_jikkou) {
                            Point t_moto = new Point();
                            t_moto.set(s.getA());
                            System.out.println("i_senbun_kousa_hantei_" + i_lineSegment_intersection_decision);
                            if (OritaCalc.determineLineSegmentDistance(t_moto, d.lineStep.get(0)) < OritaCalc.determineLineSegmentDistance(s.getB(), d.lineStep.get(0))) {
                                t_moto.set(s.getB());
                            }

                            //２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){
                            Point t_taisyou = new Point();
                            t_taisyou.set(OritaCalc.findLineSymmetryPoint(d.lineStep.get(0).getA(), d.lineStep.get(0).getB(), t_moto));

                            LineSegment add_sen = new LineSegment(OritaCalc.findIntersection(s, d.lineStep.get(0)), t_taisyou);

                            add_sen.set(d.extendToIntersectionPoint(add_sen));
                            add_sen.setColor(s.getColor());
                            if (Epsilon.high.gt0(add_sen.determineLength())) {
                                d.addLineSegment(add_sen);
                            }
                        }

                    }

                    d.record();
                }
            }

            d.lineStep.clear();
        }
    }
}
