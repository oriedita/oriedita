package oriedita.editor.handler.step;

import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.handler.DrawingSettings;
import oriedita.editor.handler.MouseModeHandler;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;
import java.util.function.Consumer;
import java.util.function.Function;

public class SelectPointStepNode<T extends Enum<T>> extends AbstractStepNode<T> implements IPreviewStepNode{
    private final Camera camera;
    private Point selectedPoint;
    private final LineColor color;
    private final CreasePattern_Worker creasePatternWorker;
    // allow free selection of points that are not in the cp
    private final boolean free;

    private final Consumer<Point> onHighlight;
    private final Function<Point, T> onRelease;

    protected SelectPointStepNode(T step, LineColor color, Camera camera,
                                  CreasePattern_Worker creasePatternWorker, boolean free,
                                  Consumer<Point> onHighlight,
                                  Function<Point, T> onRelease) {
        super(step);
        this.camera = camera;
        this.color = color;
        this.creasePatternWorker = creasePatternWorker;
        this.free = free;
        this.onHighlight = onHighlight;
        this.onRelease = onRelease;
    }

    @Override
    public void drawPreview(Graphics2D g, Camera camera, DrawingSettings drawingSettings) {
        if (selectedPoint == null || color == null){
            return;
        }
        DrawingUtil.drawStepVertex(g, selectedPoint, color, camera, drawingSettings.getGridInputAssist());
    }

    @Override
    public void runHighlightSelection(Point mousePos) {
        var p = camera.TV2object(mousePos);
        var closest = creasePatternWorker.getClosestPoint(p);
        if (closest.distance(p) <= creasePatternWorker.getSelectionDistance()){
            selectedPoint = closest;
        } else if (free) {
            selectedPoint = p;
        } else {
            selectedPoint = null;
        }
        onHighlight.accept(selectedPoint);
    }

    @Override
    public T runPressAction(Point mousePos, MouseModeHandler.Feature mouseButton) {
        return getStep();
    }

    @Override
    public void runDragAction(Point mousePos) {

    }

    @Override
    public T runReleaseAction(Point mousePos) {
        var result = onRelease.apply(selectedPoint);
        selectedPoint = null;
        return result;
    }
}
