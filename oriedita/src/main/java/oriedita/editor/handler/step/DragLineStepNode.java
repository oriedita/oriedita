package oriedita.editor.handler.step;

import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.handler.DrawingSettings;
import oriedita.editor.handler.MouseHandlerSettingGroup;
import oriedita.editor.handler.MouseModeHandler;
import oriedita.editor.tools.SnappingUtil;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class DragLineStepNode<T extends Enum<T>> extends AbstractStepNode<T>
        implements IStepNode<T>, IPreviewStepNode, IStepWithSettings {
    @Override
    public Collection<MouseHandlerSettingGroup> getSettings() {
        return List.of(MouseHandlerSettingGroup.ANGLE_SYSTEM, MouseHandlerSettingGroup.LINE_SELECT_HELP_TEXT);
    }

    private final LineColor color;
    private final Function<LineSegment, T> releaseAction;
    private final Consumer<Point> moveAction;
    private final Consumer<LineSegment> dragAction;
    private final Camera camera;
    private final AngleSystemModel angleSystemModel;
    private final CreasePattern_Worker d;

    private boolean snap = false;

    Point dragStart = new Point();
    LineSegment dragLine = null;

    public DragLineStepNode(
            T step,
            LineColor color,
            Function<LineSegment, T> releaseAction,
            Consumer<Point> moveAction,
            Consumer<LineSegment> dragAction,
            Camera camera,
            AngleSystemModel angleSystemModel,
            CreasePattern_Worker d
    ) {
        super(step);
        this.color = color;
        this.releaseAction = releaseAction;
        this.moveAction = moveAction;
        this.dragAction = dragAction;
        this.camera = camera;
        this.angleSystemModel = angleSystemModel;
        this.d = d;
    }

    @Override
    public void drawPreview(Graphics2D g, Camera camera, DrawingSettings drawingSettings) {
        DrawingUtil.drawStepVertex(g, dragStart, color, camera);
        DrawingUtil.drawLineStep(g, dragLine, camera, drawingSettings.getLineWidth());
    }

    @Override
    public void runHighlightSelection(Point mousePos) {
        dragStart = camera.TV2object(mousePos);
        moveAction.accept(dragStart);
    }

    @Override
    public T runPressAction(Point mousePos, MouseModeHandler.Feature mouseButton) {
        return getStep();
    }

    @Override
    public void runDragAction(Point mousePos, MouseEvent e) {
        snap = e.isControlDown();
        var p =  camera.TV2object(mousePos);

        Point closestPoint = d.getClosestPoint(p);
        if (p.distance(closestPoint) < d.getSelectionDistance() && !snap) {
            p = closestPoint;
        }

        dragLine = new LineSegment(dragStart, p, color);

        if (snap) {
            dragLine = dragLine.withB(SnappingUtil.snapToClosePointInActiveAngleSystem(
                    d, dragLine.getA(), dragLine.getB(),
                    angleSystemModel.getCurrentAngleSystemDivider(), angleSystemModel.getAngles()));
        }
        dragAction.accept(dragLine);
    }

    @Override
    public void runDragAction(Point mousePos) {}

    @Override
    public T runReleaseAction(Point mousePos) {
        var res = releaseAction.apply(dragLine);
        dragLine = null;
        dragStart = camera.TV2object(mousePos);
        return res;
    }
}
