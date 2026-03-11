package oriedita.editor.handler.step;

import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.handler.DrawingSettings;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class IntersectingLinesNode<T extends Enum<T>> extends DragLineStepNode<T> {
    private final CreasePattern_Worker d;
    private final List<LineSegment> highlightedLines = new ArrayList<>();
    private final Predicate<LineSegment> lineFilter;

    public IntersectingLinesNode(T step,
                                 LineColor color,
                                 Function<Collection<LineSegment>, T> releaseAction,
                                 Consumer<Point> moveAction,
                                 Consumer<LineSegment> dragAction,
                                 Predicate<LineSegment> lineFilter,
                                 Camera camera,
                                 AngleSystemModel angleSystemModel,
                                 CreasePattern_Worker d) {
        super(step, color,
                l -> releaseAction.apply(d.getFoldLineSet().getInsideLine(l, FoldLineSet.IntersectionMode.CONTAIN_OR_INTERSECT)
                        .stream()
                        .filter(lineFilter)
                        .toList()),
                moveAction, dragAction, camera, angleSystemModel, d);
        this.d = d;
        this.lineFilter = lineFilter;
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        for (LineSegment segment : highlightedLines) {
            DrawingUtil.drawLineStep(g2, segment, camera, settings.getLineWidth() + 1);
        }
    }

    @Override
    public void runDragAction(Point mousePos, MouseEvent e) {
        super.runDragAction(mousePos, e);
        highlightedLines.clear();
        highlightedLines.addAll(
                d.getFoldLineSet().getInsideLine(getDragLine(), FoldLineSet.IntersectionMode.CONTAIN_OR_INTERSECT)
                        .stream()
                        .filter(lineFilter)
                        .toList()
        );
    }

    @Override
    public void runHighlightSelection(Point mousePos) {
        super.runHighlightSelection(mousePos);
        highlightedLines.clear();
    }

    @Override
    public T runReleaseAction(Point mousePos) {
        return super.runReleaseAction(mousePos);
    }
}
