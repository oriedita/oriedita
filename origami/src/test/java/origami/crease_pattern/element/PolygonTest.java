package origami.crease_pattern.element;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PolygonTest {
    Box box;

    @BeforeEach
    public void setup() {
        box = new Box(new Point(-1, -1), new Point(-1, 1), new Point(1, 1), new Point(1, -1));
    }

    @Test
    public void testGetLineSegments() {
        var pointA = new Point(0, 1);
        var pointB = new Point(1, 1);
        var pointC = new Point(1, 0);

        var polygon = new Polygon(3);
        polygon.add(pointA);
        polygon.add(pointB);
        polygon.add(pointC);

        var lineSegments = polygon.getLineSegments();

        assertEquals(List.of(new LineSegment(pointA, pointB), new LineSegment(pointB, pointC), new LineSegment(pointC, pointA)), lineSegments);
    }

    @Test
    public void insideTest() {
        assertEquals(Polygon.Intersection.INSIDE, box.inside(new Point(0, 0)));
        assertEquals(Polygon.Intersection.OUTSIDE, box.inside(new Point(10, 0)));
        assertEquals(Polygon.Intersection.BORDER, box.inside(new Point(1, 0)));
    }

    /**
     * A part of the linesegment is inside the polygon
     */
    @Test
    public void testTotu_boundary_insideLineSegment() {
        assertTrue(box.totu_boundary_inside(new LineSegment(new Point(0, 0), new Point(100, 100))));
        assertFalse(box.totu_boundary_inside(new LineSegment(new Point(1, 1.1), new Point(100, 100))));
        assertTrue(box.totu_boundary_inside(new LineSegment(new Point(-100, 0), new Point(100, 0))));
    }

    /**
     * A part of the circle is inside the polygon
     */
    @Test
    public void testTotu_boundary_insideCircle() {
        assertTrue(box.totu_boundary_inside(new Circle(new Point(0, 0), 10.0, LineColor.CYAN_3)));
        assertTrue(box.totu_boundary_inside(new Circle(new Point(10, 0), 10.0, LineColor.CYAN_3)));
        assertFalse(box.totu_boundary_inside(new Circle(new Point(10, 10), 10.0, LineColor.CYAN_3)));
    }

    @Test
    public void testCalculateArea() {
        assertEquals(4, box.calculateArea());
    }

    @Test
    public void testInside_outside_check() {
        assertEquals(Polygon.Intersection.OUTSIDE_BORDER_INSIDE, box.inside_outside_check(new LineSegment(new Point(-100, 0), new Point(100, 0))));
        assertEquals(Polygon.Intersection.BORDER_INSIDE, box.inside_outside_check(new LineSegment(new Point(-1, 0), new Point(1, 0))));
        assertEquals(Polygon.Intersection.INSIDE, box.inside_outside_check(new LineSegment(new Point(-0.9, 0), new Point(0.9, 0))));
        assertEquals(Polygon.Intersection.BORDER, box.inside_outside_check(new LineSegment(new Point(-1, 0.1), new Point(-1, -0.1))));
        assertEquals(Polygon.Intersection.OUTSIDE_BORDER, box.inside_outside_check(new LineSegment(new Point(-1, 10), new Point(-1, -0.1))));
        assertEquals(Polygon.Intersection.OUTSIDE_BORDER, box.inside_outside_check(new LineSegment(new Point(-1, 10), new Point(-1, -10))));
        assertEquals(Polygon.Intersection.OUTSIDE, box.inside_outside_check(new LineSegment(new Point(-2, 10), new Point(-2, -0.1))));
    }
}