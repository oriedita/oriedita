package oriedita.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.tinylog.Logger;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import oriedita.editor.canvas.MouseMode;

@Singleton
public class MouseHandlerDoubleSymmetricDraw extends BaseMouseHandlerInputRestricted {
    private final MouseHandlerDrawCreaseRestricted mouseHandlerDrawCreaseRestricted;

    @Inject
    public MouseHandlerDoubleSymmetricDraw(MouseHandlerDrawCreaseRestricted mouseHandlerDrawCreaseRestricted) {
        this.mouseHandlerDrawCreaseRestricted = mouseHandlerDrawCreaseRestricted;
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.DOUBLE_SYMMETRIC_DRAW_35;
    }

    //マウス操作(mouseMode==35　でドラッグしたとき)を行う関数----------------------------------------------------

    //マウス操作(mouseMode==35　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        d.getLineStep().clear();

        Point p = new Point();
        p.set(d.getCamera().TV2object(p0));
        Point closestPoint = d.getClosestPoint(p);
        if (p.distance(closestPoint) > d.getSelectionDistance()) {
            return;
        }
        d.lineStepAdd(new LineSegment(p, closestPoint, d.getLineColor()));
    }

    public void mouseDragged(Point p0) {
        mouseHandlerDrawCreaseRestricted.mouseDragged(p0);
    }


    //マウス操作(mouseMode==35　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.getLineStep().size() == 1) {
            Point p = new Point();
            p.set(d.getCamera().TV2object(p0));
            Point closestPoint = d.getClosestPoint(p);

            d.getLineStep().get(0).setA(closestPoint);
            if (p.distance(closestPoint) <= d.getSelectionDistance()) {
                if (Epsilon.high.gt0(d.getLineStep().get(0).determineLength())) {
                    int imax = d.getFoldLineSet().getTotal();
                    for (int i = 1; i <= imax; i++) {
                        LineSegment s = d.getFoldLineSet().get(i);
                        LineSegment.Intersection i_lineSegment_intersection_decision = OritaCalc.determineLineSegmentIntersectionSweet(s, d.getLineStep().get(0), Epsilon.UNKNOWN_001, Epsilon.UNKNOWN_001);
                        boolean i_jikkou = i_lineSegment_intersection_decision == LineSegment.Intersection.INTERSECTS_TSHAPE_S1_VERTICAL_BAR_25;
                        //T字型 s1が縦棒
                        if (i_lineSegment_intersection_decision == LineSegment.Intersection.INTERSECTS_TSHAPE_S1_VERTICAL_BAR_26) {
                            i_jikkou = true;
                        }//T字型 s1が縦棒

                        if (i_jikkou) {
                            Point t_moto = new Point();
                            t_moto.set(s.getA());
                            Logger.info("i_senbun_kousa_hantei_" + i_lineSegment_intersection_decision);
                            if (OritaCalc.determineLineSegmentDistance(t_moto, d.getLineStep().get(0)) < OritaCalc.determineLineSegmentDistance(s.getB(), d.getLineStep().get(0))) {
                                t_moto.set(s.getB());
                            }

                            //２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){
                            Point t_taisyou = new Point();
                            t_taisyou.set(OritaCalc.findLineSymmetryPoint(d.getLineStep().get(0).getA(), d.getLineStep().get(0).getB(), t_moto));

                            LineSegment add_sen = new LineSegment(OritaCalc.findIntersection(s, d.getLineStep().get(0)), t_taisyou);

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

            d.getLineStep().clear();
        }
    }
}
