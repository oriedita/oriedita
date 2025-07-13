package oriedita.editor.handler;

import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;

/**
 * Mouse handler for modes which perform some box select.
 */
public abstract class BaseMouseHandlerBoxSelect extends BaseMouseHandler {
    Point selectionStart = new Point();
    private LineSegment[] lines = new LineSegment[4];

    @Override
    public void mouseMoved(Point p0) {

    }

    @Override
    public void mousePressed(Point p0) {
        selectionStart = p0;

        Point p = d.getCamera().TV2object(p0);
        lines = new LineSegment[4];
        lines[0] = new LineSegment(p, p, LineColor.MAGENTA_5);
        lines[1] = new LineSegment(p, p, LineColor.MAGENTA_5);
        lines[2] = new LineSegment(p, p, LineColor.MAGENTA_5);
        lines[3] = new LineSegment(p, p, LineColor.MAGENTA_5);
        d.getLineStep().clear();
    }

    @Override
    public void mouseDragged(Point p0) {
        Point p19_2 = new Point(selectionStart.getX(), p0.getY());
        Point p19_4 = new Point(p0.getX(), selectionStart.getY());

        Point p19_a = d.getCamera().TV2object(selectionStart);
        Point p19_b = d.getCamera().TV2object(p19_2);
        Point p19_c = d.getCamera().TV2object(p0);
        Point p19_d = d.getCamera().TV2object(p19_4);

        lines[0] = lines[0].withCoordinates(p19_a, p19_b);
        lines[1] = lines[1].withCoordinates(p19_b, p19_c);
        lines[2] = lines[2].withCoordinates(p19_c, p19_d);
        lines[3] = lines[3].withCoordinates(p19_d, p19_a);
    }

    @Override
    public void mouseReleased(Point p0) {
        lines = new LineSegment[4];
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        for (LineSegment line : lines) {
            if (line != null) {
                DrawingUtil.drawLineStep(g2, line, LineSegment.ActiveState.ACTIVE_BOTH_3, camera, settings.getLineWidth(), d.getGridInputAssist());
            }
        }
    }
}
