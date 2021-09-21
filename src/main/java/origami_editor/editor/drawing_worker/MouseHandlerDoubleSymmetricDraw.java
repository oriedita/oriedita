package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerDoubleSymmetricDraw extends BaseMouseHandler{
    private final MouseHandlerDrawCreaseRestricted mouseHandlerDrawCreaseRestricted;

    public MouseHandlerDoubleSymmetricDraw(DrawingWorker d) {
        super(d);
        mouseHandlerDrawCreaseRestricted = new MouseHandlerDrawCreaseRestricted(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.DOUBLE_SYMMETRIC_DRAW_35;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        mouseHandlerDrawCreaseRestricted.mouseMoved(p0);
    }//近い既存点のみ表示

    //マウス操作(mouseMode==35　でドラッグしたとき)を行う関数----------------------------------------------------

    //マウス操作(mouseMode==35　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        d.i_drawing_stage = 1;

        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.closest_point.set(d.getClosestPoint(p));
        if (p.distance(d.closest_point) > d.selectionDistance) {
            d.i_drawing_stage = 0;
        }
        d.line_step[1].set(p, d.closest_point);
        d.line_step[1].setColor(d.lineColor);
    }

    public void mouseDragged(Point p0) {
        mouseHandlerDrawCreaseRestricted.mouseDragged(p0);
    }


    //マウス操作(mouseMode==35　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.i_drawing_stage == 1) {
            d.i_drawing_stage = 0;
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            d.closest_point.set(d.getClosestPoint(p));
            d.line_step[1].setA(d.closest_point);
            if (p.distance(d.closest_point) <= d.selectionDistance) {
                if (d.line_step[1].getLength() > 0.00000001) {
                    int imax = d.foldLineSet.getTotal();
                    for (int i = 1; i <= imax; i++) {
                        LineSegment s = d.foldLineSet.get(i);
                        LineSegment.Intersection i_lineSegment_intersection_decision = OritaCalc.line_intersect_decide_sweet(s, d.line_step[1], 0.01, 0.01);
                        int i_jikkou = 0;
                        if (i_lineSegment_intersection_decision == LineSegment.Intersection.INTERSECTS_TSHAPE_S1_VERTICAL_BAR_25) {
                            i_jikkou = 1;
                        }//T字型 s1が縦棒
                        if (i_lineSegment_intersection_decision == LineSegment.Intersection.INTERSECTS_TSHAPE_S1_VERTICAL_BAR_26) {
                            i_jikkou = 1;
                        }//T字型 s1が縦棒

                        if (i_jikkou == 1) {
                            Point t_moto = new Point();
                            t_moto.set(s.getA());
                            System.out.println("i_senbun_kousa_hantei_" + i_lineSegment_intersection_decision);
                            if (OritaCalc.distance_lineSegment(t_moto, d.line_step[1]) < OritaCalc.distance_lineSegment(s.getB(), d.line_step[1])) {
                                t_moto.set(s.getB());
                            }


                            //２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){
                            Point t_taisyou = new Point();
                            t_taisyou.set(OritaCalc.findLineSymmetryPoint(d.line_step[1].getA(), d.line_step[1].getB(), t_moto));

                            LineSegment add_sen = new LineSegment(OritaCalc.findIntersection(s, d.line_step[1]), t_taisyou);

                            add_sen.set(d.extendToIntersectionPoint(add_sen));
                            add_sen.setColor(s.getColor());
                            if (add_sen.getLength() > 0.00000001) {
                                d.addLineSegment(add_sen);
                            }
                        }

                    }

                    d.record();
                }
            }
        }
    }
}
