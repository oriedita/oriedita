package jp.gr.java_conf.mt777.zukei2d.takakukei;

import jp.gr.java_conf.mt777.seiretu.narabebako.SortingBox_int_double;
import jp.gr.java_conf.mt777.seiretu.narabebako.int_double;
import jp.gr.java_conf.mt777.zukei2d.oritacalc.IntersectionState;
import jp.gr.java_conf.mt777.zukei2d.oritacalc.OritaCalc;
import jp.gr.java_conf.mt777.zukei2d.senbun.LineSegment;
import jp.gr.java_conf.mt777.zukei2d.ten.Point;

import java.awt.*;

public class Polygon {
    int vertexCount;             //How many vertices

    Point[] vertices;//vertex

    OritaCalc oc = new OritaCalc();          //Instantiation of classes to use functions for various calculations


    public Polygon(int _vertexCount) {  //コンストラクタ
        vertexCount = _vertexCount;
        Point[] t0 = new Point[vertexCount + 1];   //頂点
        for (int i = 0; i <= vertexCount; i++) {
            t0[i] = new Point();
        }
        // red=255;green=0;blue=0;
        vertices = t0;
    }

    //Set the number of angles of the polygon
    public void setVertexCount(int count) {
        vertexCount = count;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    //Set the i-th vertex of the polygon
    public void set(int i, Point p) {
        vertices[i].set(p);
    }

    //Get the i-th vertex of a polygon
    public Point get(int i) {
        return vertices[i];
    }

    //Set the i-th vertex of the polygon with respect to the point p0
    public void set(Point p0, int i, Point p) {
        vertices[i].set(p0.getX() + p.getX(), p0.getY() + p.getY());
    }

    // A function that determines whether a line segment intersects (true) or not (false) with an edge of this polygon ------------------------- ---------
    public boolean intersects(LineSegment s0) {
        int itrue = 0;
        LineSegment s = new LineSegment();
        for (int i = 1; i <= vertexCount - 1; i++) {
            s.set(vertices[i], vertices[i + 1]); //line segment
            if (oc.line_intersect_decide(s0, s).isIntersection()) {
                itrue = 1;
            }
        }

        s.set(vertices[vertexCount], vertices[1]); //line segment
        if (oc.line_intersect_decide(s0, s).isIntersection()) {
            itrue = 1;
        }

        return itrue == 1;
    }


    // 0, when all of the line segment s0 exists outside the convex polygon (the boundary line is not considered inside)
    // When the line segment s0 exists both outside the convex polygon and across the boundary line 1,
    // When the line segment s0 exists inside the convex polygon, the boundary line, and the outside 2,
    // When all of the line segments s0 are on the boundary of the convex polygon 3,
    // When the line segment s0 exists both inside the convex polygon and across the boundary line 4,
    // When all of the line segment s0 exists inside the convex polygon (the boundary line is not considered to be inside) 5,
    //return it
    public int inside_outside_check(LineSegment s0) {

        SortingBox_int_double nbox = new SortingBox_int_double();

        int i_intersection = 0;

        Point[] intersection = new Point[vertexCount * 2 + 3];
        for (int i = 0; i <= vertexCount * 2 + 2; i++) {
            intersection[i] = new Point();
        }

        i_intersection = i_intersection + 1;
        intersection[i_intersection].set(s0.getA());

        i_intersection = i_intersection + 1;
        intersection[i_intersection].set(s0.getB());

        IntersectionState kh = IntersectionState.NO_INTERSECTION_0; //oc.senbun_kousa_hantei(s0,s)の値の格納用

        LineSegment s = new LineSegment();

        for (int i = 1; i <= vertexCount; i++) {

            if (i == vertexCount) {
                s.set(vertices[vertexCount], vertices[1]); //Line segment
            } else {
                s.set(vertices[i], vertices[i + 1]);
            } //Line segment

            kh = oc.line_intersect_decide(s0, s);

            if (kh == IntersectionState.INTERSECTS_1) {
                i_intersection = i_intersection + 1;
                intersection[i_intersection].set(oc.findIntersection(s0, s));
            }
            if (kh == IntersectionState.INTERSECTS_TSHAPE_S2_VERTICAL_BAR_27) {
                i_intersection = i_intersection + 1;
                intersection[i_intersection].set(oc.findIntersection(s0, s));
            }
            if (kh == IntersectionState.INTERSECTS_TSHAPE_S2_VERTICAL_BAR_28) {
                i_intersection = i_intersection + 1;
                intersection[i_intersection].set(oc.findIntersection(s0, s));
            }
            if (kh == IntersectionState.PARALLEL_START_OF_S1_CONTAINS_START_OF_S2_321) {
                i_intersection = i_intersection + 1;
                intersection[i_intersection].set(s.getB());
            }
            if (kh == IntersectionState.PARALLEL_START_OF_S1_CONTAINS_END_OF_S2_331) {
                i_intersection = i_intersection + 1;
                intersection[i_intersection].set(s.getA());
            }
            if (kh == IntersectionState.PARALLEL_END_OF_S1_CONTAINS_START_OF_S2_341) {
                i_intersection = i_intersection + 1;
                intersection[i_intersection].set(s.getB());
            }
            if (kh == IntersectionState.PARALLEL_END_OF_S1_CONTAINS_END_OF_S2_351) {
                i_intersection = i_intersection + 1;
                intersection[i_intersection].set(s.getA());
            }

            if (kh == IntersectionState.PARALLEL_S1_INCLUDES_S2_361) {
                i_intersection = i_intersection + 1;
                intersection[i_intersection].set(s.getA());
                i_intersection = i_intersection + 1;
                intersection[i_intersection].set(s.getB());
            }
            if (kh == IntersectionState.PARALLEL_S1_INCLUDES_S2_362) {
                i_intersection = i_intersection + 1;
                intersection[i_intersection].set(s.getA());
                i_intersection = i_intersection + 1;
                intersection[i_intersection].set(s.getB());
            }

            if (kh == IntersectionState.PARALLEL_S1_END_OVERLAPS_S2_START_371) {
                i_intersection = i_intersection + 1;
                intersection[i_intersection].set(s.getA());
            }
            if (kh == IntersectionState.PARALLEL_S1_END_OVERLAPS_S2_START_371) {
                i_intersection = i_intersection + 1;
                intersection[i_intersection].set(s.getB());
            }
            if (kh == IntersectionState.PARALLEL_S1_START_OVERLAPS_S2_END_373) {
                i_intersection = i_intersection + 1;
                intersection[i_intersection].set(s.getB());
            }
            if (kh == IntersectionState.PARALLEL_S1_START_OVERLAPS_S2_START_374) {
                i_intersection = i_intersection + 1;
                intersection[i_intersection].set(s.getA());
            }

        }

        for (int i = 1; i <= i_intersection; i++) {
            nbox.container_i_smallest_first(new int_double(i, intersection[i].distance(s0.getA())));
        }

        // 0, when all of the line segment s0 exists outside the convex polygon (the boundary line is not considered inside)
        // When the line segment s0 exists both outside the convex polygon and across the boundary line 1,
        // When the line segment s0 exists inside the convex polygon, the boundary line, and the outside 2,
        // When all of the line segments s0 are on the boundary of the convex polygon 3,
        // When the line segment s0 exists both inside the convex polygon and across the boundary line 4,
        // When all of the line segment s0 exists inside the convex polygon (the boundary line is not considered to be inside) 5,

// naibu (Temp) {// 0 = external, 1 = boundary, 2 = internal

        int outside = 0;
        int border = 0;
        int inside = 0;

        int i_nai = 0;

        for (int i = 1; i <= nbox.getTotal(); i++) {

            i_nai = inside(intersection[nbox.getInt(i)]);
            if (i_nai == 0) {
                outside = 1;
            }
            if (i_nai == 1) {
                border = 1;
            }
            if (i_nai == 2) {
                inside = 1;
            }

            if (i != nbox.getTotal()) {
                i_nai = inside(oc.midPoint(intersection[nbox.getInt(i)], intersection[nbox.getInt(i + 1)]));
                if (i_nai == 0) {
                    outside = 1;
                }
                if (i_nai == 1) {
                    border = 1;
                }
                if (i_nai == 2) {
                    inside = 1;
                }
            }
        }

        // 0, when all of the line segment s0 exists outside the convex polygon (the boundary line is not considered inside)
        // When the line segment s0 exists both outside the convex polygon and across the boundary line 1,
        // When the line segment s0 exists inside the convex polygon, the boundary line, and the outside 2,
        // When all of the line segments s0 are on the boundary of the convex polygon 3,
        // When the line segment s0 exists both inside the convex polygon and across the boundary line 4,
        // When all of the line segment s0 exists inside the convex polygon (the boundary line is not considered to be inside) 5,

        int i_r = 0;

        if (outside == 0) {
            if (border == 0) {
                if (inside == 1) {
                    i_r = 5;
                }
            }
        }
        if (outside == 0) {
            if (border == 1) {
                if (inside == 0) {
                    i_r = 3;
                }
            }
        }
        if (outside == 0) {
            if (border == 1) {
                if (inside == 1) {
                    i_r = 4;
                }
            }
        }
        if (outside == 1) {
            if (border == 0) {
                if (inside == 0) {
                    i_r = 0;
                }
            }
        }
        if (outside == 1) {
            if (border == 1) {
                if (inside == 0) {
                    i_r = 1;
                }
            }
        }
        if (outside == 1) {
            if (border == 1) {
                if (inside == 1) {
                    i_r = 2;
                }
            }
        }

        return i_r;
    }


//----------------------------------------------------------------------------------------------

    // Even a part of the line segment s0 is inside the convex polygon (the boundary line is not regarded as the inside)
    // Returns 1 if present, 0 otherwise
    public boolean convex_inside(LineSegment s0) {
        int iflag = 0;//
        IntersectionState kh = IntersectionState.NO_INTERSECTION_0; //For storing the value of oc.line_intersect_decide (s0, s)

        LineSegment s = new LineSegment();
        for (int i = 1; i <= vertexCount - 1; i++) {
            s.set(vertices[i], vertices[i + 1]); //線分
            kh = oc.line_intersect_decide(s0, s);
            if (kh == IntersectionState.INTERSECTS_1) {
                return true;
            }
            if (kh == IntersectionState.INTERSECT_AT_POINT_4) {
                return false;
            }
            if (kh == IntersectionState.INTERSECT_AT_POINT_S1_5) {
                return false;
            }
            if (kh == IntersectionState.INTERSECT_AT_POINT_S2_6) {
                return false;
            }
            if (kh.getState() >= 30) {
                return false;
            }
            if (kh.getState() >= 20) {
                iflag = iflag + 1;
            }// This is actually executed when kh is 20 or more and less than 30.
        }

        s.set(vertices[vertexCount], vertices[1]); //Line segment
        kh = oc.line_intersect_decide(s0, s);
        if (kh == IntersectionState.INTERSECTS_1) {
            return true;
        }
        if (kh == IntersectionState.INTERSECT_AT_POINT_4) {
            return false;
        }
        if (kh == IntersectionState.INTERSECT_AT_POINT_S1_5) {
            return false;
        }
        if (kh == IntersectionState.INTERSECT_AT_POINT_S2_6) {
            return false;
        }
        if (kh.isParallel()) {
            return false;
        }
        if (kh.isEndpointIntersection()) {
            iflag = iflag + 1;
        }

        if (iflag == 0) {
            if (inside(new Point(0.5, s0.getA(), 0.5, s0.getB())) == 2) {
                return true;
            }
            return false;
        }

        if (iflag == 1) {
            if (inside(new Point(0.5, s0.getA(), 0.5, s0.getB())) == 2) {
                return true;
            }
            return false;
        }

        if (iflag == 2) {
            if (inside(new Point(0.5, s0.getA(), 0.5, s0.getB())) == 2) {
                return true;
            }
            if (inside(s0.getA()) == 2) {
                return true;
            }
            if (inside(s0.getB()) == 2) {
                return true;
            }
            return false;
        }

        if (iflag == 3) {
            return true;
        }
        if (iflag == 4) {
            return true;
        }

        return false;      //In reality, there should be no situation where you can reach this point.
    }


    // Even a part of the line segment s0 is inside the convex polygon (the boundary line is also regarded as the inside)
    // Returns 1 if present, 0 otherwise
    public int totu_boundary_inside(LineSegment s0) {// Returns 1 if even part of s0 touches a polygon.
        IntersectionState kh = IntersectionState.NO_INTERSECTION_0; //oc.line_intersect_decide(s0,s)の値の格納用

        LineSegment s = new LineSegment();
        for (int i = 1; i <= vertexCount - 1; i++) {
            s.set(vertices[i], vertices[i + 1]); //線分
            kh = oc.line_intersect_decide(s0, s);
            if (kh != IntersectionState.NO_INTERSECTION_0) {
                return 1;
            }
        }

        s.set(vertices[vertexCount], vertices[1]); //線分
        kh = oc.line_intersect_decide(s0, s);
        if (kh != IntersectionState.NO_INTERSECTION_0) {
            return 1;
        }

        if (inside(new Point(0.5, s0.getA(), 0.5, s0.getB())) == 2) {
            return 1;
        }

        return 0;
    }


    //A function that determines if a point is inside this polygon (true) or not (false)----------------------------------
    public int inside(Point p) {      //0 = outside, 1 = boundary, 2 = inside
        LineSegment s = new LineSegment();
        LineSegment sq = new LineSegment();
        Point q = new Point();

        int kousakaisuu = 0;
        int jyuuji_kousakaisuu = 0;
        int tekisetu = 0;
        double rad = 0.0;//A radian used to make sure that there is an external point.

        //First, it is determined whether the point p is on the boundary line of the polygon.
        for (int i = 1; i <= vertexCount - 1; i++) {
            s.set(vertices[i], vertices[i + 1]);
            if (oc.distance_lineSegment(p, s) < 0.01) {
                return 1;
            }
        }
        s.set(vertices[vertexCount], vertices[1]);
        if (oc.distance_lineSegment(p, s) < 0.01) {
            return 1;
        }

        //点pが多角形の境界線上に無い場合、内部にあるか外部にあるか判定する

        while (tekisetu == 0) {   //交差回数が0または、すべての交差が十字路型なら適切。
            kousakaisuu = 0;
            jyuuji_kousakaisuu = 0;

            //確実に外部にある点qと、点pで線分を作る。
            rad += 1.0;
            q.set((100000.0 * Math.cos(rad)), (100000.0 * Math.sin(rad))); //<<<<<<<<<<<<<<<<<<

            sq.set(p, q);

            for (int i = 1; i <= vertexCount - 1; i++) {
                s.set(vertices[i], vertices[i + 1]); //線分
                if (oc.line_intersect_decide(sq, s, 0.0, 0.0).isIntersection()) {
                    kousakaisuu++;
                }
                if (oc.line_intersect_decide(sq, s, 0.0, 0.0) == IntersectionState.INTERSECTS_1) {
                    jyuuji_kousakaisuu++;
                }
            }

            s.set(vertices[vertexCount], vertices[1]); //線分
            if (oc.line_intersect_decide(sq, s, 0.0, 0.0).isIntersection()) {
                kousakaisuu++;
            }
            if (oc.line_intersect_decide(sq, s, 0.0, 0.0) == IntersectionState.INTERSECTS_1) {
                jyuuji_kousakaisuu++;
            }

            if (kousakaisuu == jyuuji_kousakaisuu) {
                tekisetu = 1;
            }
        }

        if (kousakaisuu % 2 == 1) {
            return 2;
        } //交差回数が奇数なら内部

        return 0;
    }

    //多角形の頂点座標を時計回りに順に（x1,y1），（x2,y2），...，（xn,yn）とした場合の面積を求める
    public double area_calculate() {
        double area = 0.0;

        area = area + (vertices[vertexCount].getX() - vertices[2].getX()) * vertices[1].getY();
        for (int i = 2; i <= vertexCount - 1; i++) {
            area = area + (vertices[i - 1].getX() - vertices[i + 1].getX()) * vertices[i].getY();
        }
        area = area + (vertices[vertexCount - 1].getX() - vertices[1].getX()) * vertices[vertexCount].getY();
        area = -0.5 * area;

        return area;
    }

    //ある点と多角形の距離（ある点と多角形の境界上の点の距離の最小値）を求める
    public double distance_find(Point tn) {
        double distance;
        distance = oc.distance_lineSegment(tn, vertices[vertexCount], vertices[1]);
        for (int i = 1; i <= vertexCount - 1; i++) {
            if (oc.distance_lineSegment(tn, vertices[i], vertices[i + 1]) < distance) {
                distance = oc.distance_lineSegment(tn, vertices[i], vertices[i + 1]);
            }
        }

        return distance;
    }


    //多角形の内部の点を求める
    public Point insidePoint_find() {
        Point tn = new Point();
        Point tr = new Point();
        double distance = -10.0;

        for (int i = 2; i <= vertexCount - 1; i++) {
            tn.set(oc.center(vertices[i - 1], vertices[i], vertices[i + 1]));
            if ((distance < distance_find(tn)) && (inside(tn) == 2)) {
                distance = distance_find(tn);
                tr.set(tn);
            }
        }
        //
        tn.set(oc.center(vertices[vertexCount - 1], vertices[vertexCount], vertices[1]));
        if ((distance < distance_find(tn)) && (inside(tn) == 2)) {
            distance = distance_find(tn);
            tr.set(tn);
        }
        //
        tn.set(oc.center(vertices[vertexCount], vertices[1], vertices[2]));
        if ((distance < distance_find(tn)) && (inside(tn) == 2)) {
            distance = distance_find(tn);
            tr.set(tn);
        }
        //
        return tr;
    }

    //描画-----------------------------------------------------------------
    public void draw(Graphics g) {

        int[] x = new int[100];
        int[] y = new int[100];
        for (int i = 1; i <= vertexCount - 1; i++) {
            x[i] = (int) vertices[i].getX();
            y[i] = (int) vertices[i].getY();
        }
        x[0] = (int) vertices[vertexCount].getX();
        y[0] = (int) vertices[vertexCount].getY();
        g.fillPolygon(x, y, vertexCount);
    }


    public double get_x_min() {
        double r;
        r = vertices[1].getX();
        for (int i = 2; i <= vertexCount; i++) {
            if (r > vertices[i].getX()) {
                r = vertices[i].getX();
            }
        }
        return r;
    }//多角形のx座標の最小値を求める

    public double get_x_max() {
        double r;
        r = vertices[1].getX();
        for (int i = 2; i <= vertexCount; i++) {
            if (r < vertices[i].getX()) {
                r = vertices[i].getX();
            }
        }
        return r;
    }//多角形のx座標の最大値を求める

    public double get_y_min() {
        double r;
        r = vertices[1].getY();
        for (int i = 2; i <= vertexCount; i++) {
            if (r > vertices[i].getY()) {
                r = vertices[i].getY();
            }
        }
        return r;
    }//多角形のy座標の最小値を求める

    public double get_y_max() {
        double r;
        r = vertices[1].getY();
        for (int i = 2; i <= vertexCount; i++) {
            if (r < vertices[i].getY()) {
                r = vertices[i].getY();
            }
        }
        return r;
    }//多角形のy座標の最大値を求める
}
