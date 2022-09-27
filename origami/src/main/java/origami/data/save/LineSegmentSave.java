package origami.data.save;

import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineSegment;

import java.util.List;

/**
 * Savefile containing lines, circles and auxlines
 *
 * @see origami.crease_pattern.FoldLineSet
 * @see origami.crease_pattern.LineSegmentSet
 */
public interface LineSegmentSave {
    void setTitle(String title);

    List<LineSegment> getLineSegments();

    void setLineSegments(List<LineSegment> lineSegments);

    void addLineSegment(LineSegment lineSegment);

    void addCircle(Circle circle);

    List<Circle> getCircles();

    void setCircles(List<Circle> circles);

    List<LineSegment> getAuxLineSegments();

    void setAuxLineSegments(List<LineSegment> auxLineSegments);

    void addAuxLineSegment(LineSegment lineSegment);

    String getTitle();
}
