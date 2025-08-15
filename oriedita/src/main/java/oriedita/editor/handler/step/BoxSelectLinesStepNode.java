package oriedita.editor.handler.step;

import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.handler.DrawingSettings;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class BoxSelectLinesStepNode<T extends Enum<T>> extends BoxSelectStepNode<T> {
    private final Predicate<LineSegment> lineFilter; // lines for which this function returns false will be ignored
    private final CreasePattern_Worker d;

    public BoxSelectLinesStepNode(T step, Function<Collection<LineSegment>, T> releaseBoxAction,
                                  Function<LineSegment, T> releasePointAction,
                                  Consumer<Point> moveAction, Consumer<Polygon> dragAction,
                                  Predicate<LineSegment> lineFilter,
                                  Camera camera,
                                  CreasePattern_Worker d) {
        super(step,
                p -> releaseBoxAction.apply(d.getFoldLineSet()
                        .lineSegmentsInside(p)
                        .stream()
                        .filter(lineFilter)
                        .toList()),
                p ->
                        d.getFoldLineSet().closestLineSegmentInRange(p, d.getSelectionDistance())
                                .filter(lineFilter)
                                .map(releasePointAction)
                                .orElse(step),
                moveAction, dragAction, camera);
        this.lineFilter = lineFilter;
        this.d = d;
    }

    private final Collection<LineSegment> highlightedLines = new ArrayList<>();

    @Override
    public void runHighlightSelection(Point mousePos) {
        super.runHighlightSelection(mousePos);
        highlightedLines.clear();
        var p = camera.TV2object(mousePos);
        d.getFoldLineSet().closestLineSegmentInRange(p, d.getSelectionDistance())
                .filter(lineFilter)
                .ifPresent(highlightedLines::add);
    }

    @Override
    public void runDragAction(Point mousePos) {
        super.runDragAction(mousePos);
        highlightedLines.clear();
        highlightedLines.addAll(
                d.getFoldLineSet().lineSegmentsInside(getBox())
                        .stream()
                        .filter(lineFilter)
                        .toList());
    }

    @Override
    public T runReleaseAction(Point mousePos) {
        highlightedLines.clear();
        return super.runReleaseAction(mousePos);
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        for (LineSegment highlightedLine : highlightedLines) {
            DrawingUtil.drawLineStep(g2, highlightedLine, camera, settings.getLineWidth() + 1, d.getGridInputAssist());
        }
    }
}
