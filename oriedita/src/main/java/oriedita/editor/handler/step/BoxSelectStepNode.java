package oriedita.editor.handler.step;

import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.handler.DrawingSettings;
import oriedita.editor.handler.MouseModeHandler;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;
import origami.crease_pattern.element.Rectangle;

import java.awt.Graphics2D;
import java.util.function.Function;

public class BoxSelectStepNode<T extends Enum<T>> extends AbstractCameraStepNode<T> implements IStepNode<T>, ICameraStepNode, IPreviewStepNode {

    Point selectionStart = new Point();
    private LineSegment[] lines = new LineSegment[4];
    private final Function<Polygon, T> releaseAction;

    public BoxSelectStepNode(T step,
                             Function<Polygon, T> releaseAction, Camera camera) {
        super(step);
        this.releaseAction = releaseAction;
        this.camera = camera;
    }

    @Override
    public void runHighlightSelection(Point mousePos) {
        selectionStart = mousePos;

        Point p = camera.TV2object(mousePos);
        lines = new LineSegment[4];
        lines[0] = new LineSegment(p, p, LineColor.MAGENTA_5);
        lines[1] = new LineSegment(p, p, LineColor.MAGENTA_5);
        lines[2] = new LineSegment(p, p, LineColor.MAGENTA_5);
        lines[3] = new LineSegment(p, p, LineColor.MAGENTA_5);
    }

    @Override
    public T runPressAction(Point mousePos, MouseModeHandler.Feature mouseButton) {
        return getStep();
    }

    @Override
    public void runDragAction(Point mousePos) {
        Point p19_2 = new Point(selectionStart.getX(), mousePos.getY());
        Point p19_4 = new Point(mousePos.getX(), selectionStart.getY());

        Point p19_a = camera.TV2object(selectionStart);
        Point p19_b = camera.TV2object(p19_2);
        Point p19_c = camera.TV2object(mousePos);
        Point p19_d = camera.TV2object(p19_4);

        lines[0] = lines[0].withCoordinates(p19_a, p19_b);
        lines[1] = lines[1].withCoordinates(p19_b, p19_c);
        lines[2] = lines[2].withCoordinates(p19_c, p19_d);
        lines[3] = lines[3].withCoordinates(p19_d, p19_a);
    }

    @Override
    public T runReleaseAction(Point mousePos) {
        var ret = releaseAction.apply(getBox());
        lines = new  LineSegment[4];
        return ret;
    }

    public Rectangle getBox() {
        return new Rectangle(lines[0].getA(), lines[1].getA(), lines[2].getA(), lines[3].getA());
    }

    public void drawPreview(Graphics2D g, Camera camera, DrawingSettings settings) {
        for (LineSegment line : lines) {
            if (line != null) {
                DrawingUtil.drawLineStep(g, line, LineSegment.ActiveState.ACTIVE_BOTH_3, camera, settings.getLineWidth(), settings.getGridInputAssist());
            }
        }
    }
}
