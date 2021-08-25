package jp.gr.java_conf.mt777.origami.orihime.basicbranch_worker;

import jp.gr.java_conf.mt777.origami.dougu.linestore.*;
import jp.gr.java_conf.mt777.origami.dougu.camera.*;

import java.awt.*;

import jp.gr.java_conf.mt777.kiroku.memo.*;

import jp.gr.java_conf.mt777.origami.orihime.LineColor;
import jp.gr.java_conf.mt777.graphic2d.linesegment.*;
import jp.gr.java_conf.mt777.graphic2d.oritacalc.*;
import jp.gr.java_conf.mt777.graphic2d.oritaoekaki.*;
import jp.gr.java_conf.mt777.graphic2d.polygon.Polygon;
import jp.gr.java_conf.mt777.graphic2d.point.Point;

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
        lineSegmentSet.bunkatu_seiri_for_SubFace_generation();
    }//kとは線分集合のこと、Senbunsyuugou k =new Senbunsyuugou();
}
