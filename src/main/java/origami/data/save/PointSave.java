package origami.data.save;

import origami.crease_pattern.element.Point;

import java.util.List;

/**
 * Savefile containing points
 * @see origami.crease_pattern.PointSet
 */
public interface PointSave {
    void addPoint(Point p);

    List<Point> getPoints();

    void setPoints(List<Point> points);
}
