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
import java.util.function.Consumer;
import java.util.function.Function;

public class BoxSelectStepNode<T extends Enum<T>> extends AbstractStepNode<T> implements IStepNode<T>, IPreviewStepNode {

    protected final Camera camera;
    Point selectionStart = new Point();
    private LineSegment[] lines = new LineSegment[4];
    private final Function<Polygon, T> releaseBoxAction;
    private final Function<Point, T> releasePointAction;
    private final Consumer<Point> moveAction;
    private final Consumer<Polygon> dragAction;
    private boolean showPreview = false;

    public BoxSelectStepNode(T step, Function<Polygon, T> releaseBoxAction, Function<Point, T> releasePointAction,
                             Consumer<Point> moveAction, Consumer<Polygon> dragAction, Camera camera) {
        super(step);
        this.releaseBoxAction = releaseBoxAction;
        this.camera = camera;
        this.releasePointAction = releasePointAction;
        this.moveAction = moveAction;
        this.dragAction = dragAction;
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
        moveAction.accept(p);
    }

    @Override
    public T runPressAction(Point mousePos, MouseModeHandler.Feature mouseButton) {
        showPreview = true;
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
        dragAction.accept(getBox());
    }

    @Override
    public T runReleaseAction(Point mousePos) {
        T ret;
        if (selectionStart.distance(mousePos) > 0){
            ret = releaseBoxAction.apply(getBox());
        } else {
            ret = releasePointAction.apply(camera.TV2object(mousePos));
        }
        showPreview = false;
        lines = new  LineSegment[4];
        return ret;
    }

    public Rectangle getBox() {
        return new Rectangle(lines[0].getA(), lines[1].getA(), lines[2].getA(), lines[3].getA());
    }

    public void drawPreview(Graphics2D g, Camera camera, DrawingSettings settings) {
        if (!showPreview) {return;}
        for (LineSegment line : lines) {
            if (line != null) {
                DrawingUtil.drawLineStep(g, line, LineSegment.ActiveState.ACTIVE_BOTH_3, camera, settings.getLineWidth(), settings.getGridInputAssist());
            }
        }
    }
}
