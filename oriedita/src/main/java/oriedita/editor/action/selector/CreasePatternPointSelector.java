package oriedita.editor.action.selector;

import oriedita.editor.action.selector.drawing.DrawingSettings;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;

import java.awt.*;
import java.util.function.Supplier;

/**
 * Selects a Point out of the creasePattern in d
 */
public class CreasePatternPointSelector extends ElementSelector<Point> {
    private final SelectionMode selectionMode;
    private final Supplier<LineColor> color;

    public enum SelectionMode {
        FREE(false), CLOSEST_POINT_ONLY(true), CLOSEST_POINT_OR_FREE(true);
        private final boolean snapToClosePoint;

        SelectionMode(boolean snapToClosePoint) {
            this.snapToClosePoint = snapToClosePoint;
        }

        public boolean shouldSnapToClosePoint() {
            return snapToClosePoint;
        }
    }

    public CreasePatternPointSelector(SelectionMode selectionMode, LineColor color) {
        this.selectionMode = selectionMode;
        this.color = () -> color;
    }

    public CreasePatternPointSelector(SelectionMode selectionMode, Supplier<LineColor> color) {
        this.selectionMode = selectionMode;
        this.color = color;
    }

    @Override
    public Point determineSelected(Point mousePos, MouseEventInfo eventInfo) {
        Point p = d.camera.TV2object(mousePos);
        if (selectionMode.shouldSnapToClosePoint()) {
            Point snapped = d.getClosestPoint(p);
            if (snapped.distance(p) >= d.selectionDistance) {
                snapped = p;
            }
            return snapped;
        }
        return p;
    }

    @Override
    public boolean validate(Point p, MouseEventInfo eventInfo) {
        if (selectionMode == SelectionMode.CLOSEST_POINT_ONLY) {
            return p.equals(d.getClosestPoint(p));
        }
        return true;
    }

    @Override
    public void draw(Point p, Graphics2D g2, Camera camera, DrawingSettings settings) {
        DrawingUtil.drawStepVertex(g2, p, color.get(), camera, settings);
    }
}
