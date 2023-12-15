package oriedita.editor.drawing.tools;

import oriedita.editor.handler.DrawingSettings;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineSegment;

import java.awt.Graphics2D;

public class MouseHandlerDrawingHelper {
    private final Graphics2D g;
    private final DrawingSettings settings;
    private final Camera camera;
    private final boolean gridInputAssist;

    public MouseHandlerDrawingHelper(Graphics2D g,
                                     DrawingSettings settings,
                                     Camera camera,
                                     boolean gridInputAssist) {
        this.g = g;
        this.settings = settings;
        this.camera = camera;
        this.gridInputAssist = gridInputAssist;
    }

    public void drawLineStep(LineSegment segment) {
        if (segment == null) {
            return;
        }
        DrawingUtil.drawLineStep(g, segment, camera, settings.getLineWidth(), gridInputAssist);
    }

    public void drawCircle(Circle circle) {
        if (circle == null) {
            return;
        }
        DrawingUtil.drawCircleStep(g, circle, camera);
    }
}
