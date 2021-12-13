package oriedita.editor.action.selector;

import oriedita.editor.action.selector.drawing.DrawingSettings;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.*;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Selects a line out of the line collection supplied by lineSegmentSetSupplier.
 * noCloseLineValue determines what value is returned when there is no line in the selection radius
 */
public class LineSelectorFromCollection extends ElementSelector<LineSegment> {
    private final LineColor color;
    private final Supplier<Collection<LineSegment>> lineSegmentSetSupplier;
    private final NoCloseLineValue noCloseLineValue;

    public enum NoCloseLineValue {
        NONE, MOUSE_POS
    }

    public LineSelectorFromCollection(
            Supplier<Collection<LineSegment>> lineSegmentSetSupplier, LineColor color,
            NoCloseLineValue noCloseLineValue) {
        this.color = color;
        this.lineSegmentSetSupplier = lineSegmentSetSupplier;
        this.noCloseLineValue = noCloseLineValue;
    }

    @Override
    protected LineSegment determineSelected(Point mousePos, MouseEventInfo eventInfo) {
        Point p = d.camera.TV2object(mousePos);
        Optional<LineSegment> closestLineSegmentO = lineSegmentSetSupplier.get().stream()
                .min(Comparator.comparingDouble(cand -> OritaCalc.determineLineSegmentDistance(p, cand)));
        if (closestLineSegmentO.isPresent()) {
            LineSegment closestLineSegment = closestLineSegmentO.get();
            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.selectionDistance) {
                LineSegment s = new LineSegment();
                s.set(closestLineSegment);
                s.setColor(color);
                return s;
            }
        }
        if (noCloseLineValue == NoCloseLineValue.MOUSE_POS) {
            return new LineSegment(p,p, color);
        }
        return null;
    }

    @Override
    protected boolean validate(LineSegment element, MouseEventInfo eventInfo) {
        return element != null;
    }

    @Override
    public void draw(LineSegment element, Graphics2D g2, Camera camera, DrawingSettings settings) {
        DrawingUtil.drawLineStep(g2, element, camera, settings);
    }
}
