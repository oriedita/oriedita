package origami.crease_pattern.element;

import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.folding.util.SortingBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Polygon {
    List<Point> vertices; //vertex

    public Polygon() {
        vertices = new ArrayList<>();
    }

    public Polygon(List<Point> points) {
        this.vertices = points.stream().map(Point::new).collect(Collectors.toList());
    }

    protected void add(Point p) {
        vertices.add(p);
    }

    public Point get(int i) {
        return vertices.get(i);
    }

    public void set(int i, Point p) {
        vertices.set(i, p);
    }

    /**
     * Iterate over the linesegments in this polygon.
     */
    public List<LineSegment> getLineSegments() {
        List<LineSegment> lineSegments = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            LineSegment s;
            if (i == vertices.size() - 1) {
                s = new LineSegment(vertices.get(vertices.size() - 1), vertices.get(0)); //Line segment
            } else {
                s = new LineSegment(vertices.get(i), vertices.get(i + 1));
            }

            lineSegments.add(s);
        }

        return lineSegments;
    }

    // 0, when all of the line segment s0 exists outside the convex polygon (the boundary line is not considered inside)
    // When the line segment s0 exists both outside the convex polygon and across the boundary line 1,
    // When the line segment s0 exists inside the convex polygon, the boundary line, and the outside 2,
    // When all of the line segments s0 are on the boundary of the convex polygon 3,
    // When the line segment s0 exists both inside the convex polygon and across the boundary line 4,
    // When all of the line segment s0 exists inside the convex polygon (the boundary line is not considered to be inside) 5,
    //return it
    public Intersection inside_outside_check(LineSegment s0) {
        SortingBox<Point> nbox = new SortingBox<>();

        List<Point> intersections = new ArrayList<>();

        intersections.add(s0.getA());
        intersections.add(s0.getB());

        for (LineSegment s : getLineSegments()) {
            LineSegment.Intersection kh = OritaCalc.determineLineSegmentIntersection(s0, s);

            switch (kh) {
                case INTERSECTS_1, INTERSECTS_TSHAPE_S2_VERTICAL_BAR_27, INTERSECTS_TSHAPE_S2_VERTICAL_BAR_28:
                    intersections.add(OritaCalc.findIntersection(s0, s));
                    break;
                case PARALLEL_START_OF_S1_CONTAINS_START_OF_S2_321, PARALLEL_END_OF_S1_CONTAINS_START_OF_S2_341, PARALLEL_S1_START_OVERLAPS_S2_END_373:
                    intersections.add(new Point(s.getB()));
                    break;
                case PARALLEL_START_OF_S1_CONTAINS_END_OF_S2_331, PARALLEL_END_OF_S1_CONTAINS_END_OF_S2_351, PARALLEL_S1_START_OVERLAPS_S2_START_374:
                    intersections.add(new Point(s.getA()));
                    break;
                case PARALLEL_S1_INCLUDES_S2_361, PARALLEL_S1_INCLUDES_S2_362, PARALLEL_S1_END_OVERLAPS_S2_START_371:
                    intersections.add(new Point(s.getA()));
                    intersections.add(new Point(s.getB()));
                    break;
                default:
            }

        }

        for (var intersection : intersections) {
            nbox.addByWeight(intersection, intersection.distance(s0.getA()));
        }

        // 0, when all of the line segment s0 exists outside the convex polygon (the boundary line is not considered inside)
        // When the line segment s0 exists both outside the convex polygon and across the boundary line 1,
        // When the line segment s0 exists inside the convex polygon, the boundary line, and the outside 2,
        // When all of the line segments s0 are on the boundary of the convex polygon 3,
        // When the line segment s0 exists both inside the convex polygon and across the boundary line 4,
        // When all of the line segment s0 exists inside the convex polygon (the boundary line is not considered to be inside) 5,

        // naibu (Temp) {// 0 = external, 1 = boundary, 2 = internal

        boolean outside = false;
        boolean border = false;
        boolean inside = false;

        for (int i = 1; i <= nbox.getTotal(); i++) {
            switch (inside(nbox.getValue(i))) {
                case OUTSIDE -> outside = true;
                case BORDER -> border = true;
                case INSIDE -> inside = true;
                default -> {}
            }

            if (i != nbox.getTotal()) {
                switch (inside(OritaCalc.midPoint(nbox.getValue(i), nbox.getValue(i + 1)))) {
                    case OUTSIDE -> outside = true;
                    case BORDER -> border = true;
                    case INSIDE -> inside = true;
                    default -> {}
                }
            }
        }


        return Polygon.Intersection.create(outside, border, inside);
    }

    // Even a part of the line segment s0 is inside the convex polygon (the boundary line is not regarded as the inside)
    // Returns 1 if present, 0 otherwise
    public boolean convex_inside(LineSegment s0) {
        int numPointsOnPolygon = 0;// Number of corners in the polygon that intersect with s0

        for (LineSegment s : getLineSegments()) {
            // We need to use the sweet version here, or things can go very wrong
            LineSegment.Intersection kh = OritaCalc.determineLineSegmentIntersectionSweet(s0, s);
            switch (kh) {
                case INTERSECTS_1:
                    return true;
                case INTERSECT_AT_POINT_4:
                case INTERSECT_AT_POINT_S2_6:
                case INTERSECT_AT_POINT_S1_5:
                    return false;
                default:
            }

            if (kh.isOverlapping()) {
                // given line segment lies on top of edge of polygon. So it is not inside
                return false;
            }

            if (kh.isEndpointIntersection()) {
                numPointsOnPolygon = numPointsOnPolygon + 1;
            }// This is actually executed when kh is 20 or more and less than 30.
        }

        if (numPointsOnPolygon == 0) {
            return inside(Point.mid(s0.getA(), s0.getB())) == Intersection.INSIDE;
        }

        if (numPointsOnPolygon == 1) {
            return inside(Point.mid(s0.getA(), s0.getB())) == Intersection.INSIDE;
        }

        if (numPointsOnPolygon == 2) {
            if (inside(Point.mid(s0.getA(), s0.getB())) == Intersection.INSIDE) {
                return true;
            }
            if (inside(s0.getA()) == Intersection.INSIDE) {
                return true;
            }
            return inside(s0.getB()) == Intersection.INSIDE;
        }

        if (numPointsOnPolygon == 3) {
            return true;
        }

        return numPointsOnPolygon == 4;//In reality, there should be no situation where you can reach this point.
    }

    public boolean totu_boundary_inside(Circle c) {
        for (LineSegment s : getLineSegments()) {
            if (OritaCalc.determineLineSegmentDistance(c.determineCenter(), s) <= c.getR()) {
                if ((OritaCalc.distance(s.getA(), c.determineCenter()) >= c.getR()) || (OritaCalc.distance(s.getB(), c.determineCenter()) >= c.getR())) {
                    return true;
                }
            }
        }

        return totu_boundary_inside(new LineSegment(c.determineCenter(), c.determineCenter()));
    }

    // Even a part of the line segment s0 is inside the convex polygon (the boundary line is also regarded as the inside)
    // Returns 1 if present, 0 otherwise
    public boolean totu_boundary_inside(LineSegment s0) {// Returns 1 if even part of s0 touches a polygon.
        LineSegment.Intersection kh; //oc.line_intersect_decide(s0,s)の値の格納用

        for (LineSegment s : getLineSegments()) {
            kh = OritaCalc.determineLineSegmentIntersection(s0, s);
            if (kh != LineSegment.Intersection.NO_INTERSECTION_0) {
                return true;
            }
        }

        return inside(Point.mid(s0.getA(), s0.getB())) == Intersection.INSIDE;
    }

    //A function that determines if a point is inside this polygon (true) or not (false)----------------------------------
    public Intersection inside(Point p) {      //0 = outside, 1 = boundary, 2 = inside
        int kousakaisuu = 0;
        int jyuuji_kousakaisuu;
        boolean appropriate = false;
        double rad = 0.0;//A radian used to make sure that there is an external point.

        //First, it is determined whether the point p is on the boundary line of the polygon.
        for (LineSegment ls : getLineSegments()) {
            if (OritaCalc.determineLineSegmentDistance(p, ls) < Epsilon.UNKNOWN_001) {
                return Intersection.BORDER;
            }
        }

        //点pが多角形の境界線上に無い場合、内部にあるか外部にあるか判定する

        while (!appropriate) {   //交差回数が0または、すべての交差が十字路型なら適切。
            kousakaisuu = 0;
            jyuuji_kousakaisuu = 0;

            //確実に外部にある点qと、点pで線分を作る。
            rad += 1.0;
            Point q = new Point((100000.0 * Math.cos(rad)), (100000.0 * Math.sin(rad))); //<<<<<<<<<<<<<<<<<<

            LineSegment sq = new LineSegment(p, q);

            for (LineSegment ls : getLineSegments()) {
                if (OritaCalc.determineLineSegmentIntersection(sq, ls, 0.0).isIntersection()) {
                    kousakaisuu++;
                }
                if (OritaCalc.determineLineSegmentIntersection(sq, ls, 0.0) == LineSegment.Intersection.INTERSECTS_1) {
                    jyuuji_kousakaisuu++;
                }
            }

            if (kousakaisuu == jyuuji_kousakaisuu) {
                appropriate = true;
            }
        }

        if (kousakaisuu % 2 == 1) {
            return Intersection.INSIDE;
        } //交差回数が奇数なら内部

        return Intersection.OUTSIDE;
    }

    //Find the area when the vertex coordinates of the polygon are clockwise (x1, y1), (x2, y2), ..., (xn, yn).
    public double calculateArea() {
        double area = 0.0;

        area = area + (vertices.get(vertices.size() - 1).getX() - vertices.get(1).getX()) * vertices.get(0).getY();
        for (int i = 1; i < vertices.size() - 1; i++) {
            area = area + (vertices.get(i - 1).getX() - vertices.get(i + 1).getX()) * vertices.get(i).getY();
        }
        area = area + (vertices.get(vertices.size() - 2).getX() - vertices.get(0).getX()) * vertices.get(vertices.size() - 1).getY();
        area = -area / 2;

        return area;
    }

    //Find the distance between a point and a polygon (the minimum value of the distance between a point and a point on the boundary of the polygon)
    public double findDistance(Point tn) {
        Optional<Double> min = getLineSegments().stream()
                .map(ls -> OritaCalc.determineLineSegmentDistance(tn, ls))
                .min(Double::compareTo);
        if (min.isEmpty()) {
            throw new IllegalStateException("Polygon is empty");
        }
        return min.get();
    }

    //Find the points inside the polygon
    public Point insidePoint_find() {
        Point tn;
        Point tr = new Point();
        double distance = -10.0;

        for (int i = 1; i < vertices.size() - 1; i++) {
            tn = OritaCalc.center(vertices.get(i - 1), vertices.get(i), vertices.get(i + 1));
            if ((distance < findDistance(tn)) && (inside(tn) == Intersection.INSIDE)) {
                distance = findDistance(tn);
                tr = tn;
            }
        }
        //
        tn = OritaCalc.center(vertices.get(vertices.size() - 2), vertices.get(vertices.size() - 1), vertices.get(0));
        if ((distance < findDistance(tn)) && (inside(tn) == Intersection.INSIDE)) {
            distance = findDistance(tn);
            tr = tn;
        }
        //
        tn = OritaCalc.center(vertices.get(vertices.size() - 1), vertices.get(0), vertices.get(1));
        if ((distance < findDistance(tn)) && (inside(tn) == Intersection.INSIDE)) {
            tr = tn;
        }
        //
        return tr;
    }

    public double getXMin() {
        Optional<Double> min = vertices.stream().map(Point::getX).min(Double::compareTo);

        if (min.isEmpty()) {
            throw new IllegalStateException("Polygon is empty");
        }

        return min.get();
    }//多角形のx座標の最小値を求める

    public double getXMax() {
        Optional<Double> max = vertices.stream().map(Point::getX).max(Double::compareTo);

        if (max.isEmpty()) {
            throw new IllegalStateException("Polygon is empty");
        }

        return max.get();
    }//多角形のx座標の最大値を求める

    public double getYMin() {
        Optional<Double> min = vertices.stream().map(Point::getY).min(Double::compareTo);

        if (min.isEmpty()) {
            throw new IllegalStateException("Polygon is empty");
        }

        return min.get();
    }//多角形のy座標の最小値を求める

    public double getYMax() {
        Optional<Double> max = vertices.stream().map(Point::getY).max(Double::compareTo);

        if (max.isEmpty()) {
            throw new IllegalStateException("Polygon is empty");
        }
        return max.get();
    }//多角形のy座標の最大値を求める

    /**
     * A Point or LineSegment intersecting a Polygon
     */
    public enum Intersection {
        /**
         * When all of the line segment s0 exists outside the convex polygon (the boundary line is not considered inside)
         */
        OUTSIDE,
        /**
         * When all of the line segments s0 are on the boundary of the convex polygon
         */
        BORDER,
        /**
         * When all of the line segment s0 exists inside the convex polygon (the boundary line is not considered to be inside)
         */
        INSIDE,
        /**
         * When the line segment s0 exists both outside the convex polygon and across the boundary line
         */
        OUTSIDE_BORDER,
        /**
         * When the line segment s0 exists inside the convex polygon, the boundary line, and the outside
         */
        OUTSIDE_BORDER_INSIDE,
        /**
         * When the line segment s0 exists both inside the convex polygon and across the boundary line
         */
        BORDER_INSIDE,
        ;

        public static Intersection create(boolean outside, boolean border, boolean inside) {
            if (outside && border && inside) return OUTSIDE_BORDER_INSIDE;
            if (outside && border) return OUTSIDE_BORDER;
            if (border && inside) return BORDER_INSIDE;
            if (outside) return OUTSIDE;
            if (inside) return INSIDE;
            if (border) return BORDER;

            throw new IllegalArgumentException();
        }
    }
}
