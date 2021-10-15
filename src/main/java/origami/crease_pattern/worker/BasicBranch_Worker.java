package origami.crease_pattern.worker;

import origami.crease_pattern.LineSegmentSet;

public class BasicBranch_Worker {
    LineSegmentSet lineSegmentSet = new LineSegmentSet();    //Instantiation of basic branch structure

    public void reset() {
        lineSegmentSet.reset();
    }

    public void set(LineSegmentSet ss) {
        lineSegmentSet.set(ss);
    }

    public LineSegmentSet get() {
        return lineSegmentSet;
    }

    public void split_arrangement_for_SubFace_generation() throws InterruptedException {
        System.out.println("　　Senbunsyuugouの中で、Smenを発生させるための線分集合の整理");
        System.out.println("分割整理　１、点削除前	getsousuu() = " + lineSegmentSet.getNumLineSegments());
        lineSegmentSet.point_removal();          //Just in case, remove the dotted line segment
        System.out.println("分割整理　２、重複線分削除前	getsousuu() = " + lineSegmentSet.getNumLineSegments());
        lineSegmentSet.overlapping_line_removal();//念のため、全く一致する線分が２つあれば１つを除く
        System.out.println("分割整理　３、交差分割前	getsousuu() = " + lineSegmentSet.getNumLineSegments());
        lineSegmentSet.intersect_divide();
        System.out.println("分割整理　４、点削除前	getsousuu() = " + lineSegmentSet.getNumLineSegments());
        lineSegmentSet.point_removal();             //折り畳み推定の針金図の整理のため、点状の線分を除く
        System.out.println("分割整理　５、重複線分削除前	getsousuu() = " + lineSegmentSet.getNumLineSegments());
        lineSegmentSet.overlapping_line_removal(); //折り畳み推定の針金図の整理のため、全く一致する線分が２つあれば１つを除く
        System.out.println("分割整理　５、重複線分削除後	getsousuu() = " + lineSegmentSet.getNumLineSegments());
    }//k is a set of line segments, LineSegmentSet k = new LineSegmentSet ();
}
