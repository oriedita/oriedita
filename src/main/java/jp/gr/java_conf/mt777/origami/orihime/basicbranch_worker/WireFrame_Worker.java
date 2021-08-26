package jp.gr.java_conf.mt777.origami.orihime.basicbranch_worker;

import jp.gr.java_conf.mt777.origami.dougu.linestore.*;

public class WireFrame_Worker {
    LineSegmentSet lineSegmentSet = new LineSegmentSet();    //Instantiation of basic branch structure

    public WireFrame_Worker(double r0) {
    }

    public void reset() {
        lineSegmentSet.reset();
    }

    public void set(LineSegmentSet ss) {
        lineSegmentSet.set(ss);
    }

    public LineSegmentSet get() {
        return lineSegmentSet;
    }

    public void split_arrangement_for_SubFace_generation() {
        lineSegmentSet.split_arrangement_for_SubFace_generation();
    }//k is a set of line segments, LineSegmentSet k = new LineSegmentSet ();
}
