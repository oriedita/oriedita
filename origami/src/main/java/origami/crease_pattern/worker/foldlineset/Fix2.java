package origami.crease_pattern.worker.foldlineset;

import origami.Epsilon;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.data.quadTree.QuadTree;
import origami.data.quadTree.adapter.LineSegmentListAdapter;

public class Fix2 {
    public static void apply(FoldLineSet foldLineSet) {
        foldLineSet.unselect_all();
        QuadTree qt = new QuadTree(new LineSegmentListAdapter(foldLineSet.getLineSegments()));
        for (int i = 1; i <= foldLineSet.getTotal() - 1; i++) {
            LineSegment si = foldLineSet.get(i);
            if (si.getColor() != LineColor.CYAN_3) {

                for (int j : qt.getPotentialCollision(i)) {
                    LineSegment sj = foldLineSet.get(j);
                    if (sj.getColor() != LineColor.CYAN_3) {
                        //T-intersection
                        //折線iをその点pの影で分割する。ただし、点pの影がどれか折線の端点と同じとみなされる場合は何もしない。
                        //r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度
                        LineSegment.Intersection intersection = OritaCalc.determineLineSegmentIntersectionSweet(si, sj, Epsilon.UNKNOWN_0001, Epsilon.PARALLEL);
                        switch (intersection) {
                            case INTERSECTS_TSHAPE_S1_VERTICAL_BAR_25:
                                applyLineSegmentDivide(foldLineSet, si.getA(), j);
                                qt.grow(1);
                                break;
                            case INTERSECTS_TSHAPE_S1_VERTICAL_BAR_26:
                                applyLineSegmentDivide(foldLineSet, si.getB(), j);
                                qt.grow(1);
                                break;
                            case INTERSECTS_TSHAPE_S2_VERTICAL_BAR_27:
                                applyLineSegmentDivide(foldLineSet, sj.getA(), i);
                                qt.grow(1);
                                break;
                            case INTERSECTS_TSHAPE_S2_VERTICAL_BAR_28:
                                applyLineSegmentDivide(foldLineSet, sj.getB(), i);
                                qt.grow(1);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
    }

    public static void applyLineSegmentDivide(FoldLineSet foldLineSet, Point p, int i) {//何もしない=0,分割した=1
        LineSegment s = foldLineSet.get(i);

        LineSegment mts = new LineSegment(s.getA(), s.getB());//mtsは点pに最も近い線分

        //直線t上の点pの影の位置（点pと最も近い直線t上の位置）を求める。public Ten oc.kage_motome(Tyokusen t,Ten p){}
        //線分を含む直線を得る public Tyokusen oc.Senbun2Tyokusen(Senbun s){}
        Point pk = new Point();
        pk.set(OritaCalc.findProjection(OritaCalc.lineSegmentToStraightLine(mts), p));//pkは点pの（線分を含む直線上の）影
        //線分の分割-----------------------------------------
        applyLineSegmentDivide(foldLineSet, s, pk);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
    }

    public static void applyLineSegmentDivide(FoldLineSet foldLineSet, LineSegment s0, Point p) {   //Divide the i-th line segment (end points a and b) at point p. Change the i-th line segment ab to ap and add the line segment pb.
        LineSegment s1 = new LineSegment(p, s0.getB());//Create the i-th line segment ab before changing it to ap
        LineColor i_c = s0.getColor();

        s0.setB(p);//Change the i-th line segment ab to ap

        s1.setColor(i_c);
        foldLineSet.addLine(s1);
    }
}
