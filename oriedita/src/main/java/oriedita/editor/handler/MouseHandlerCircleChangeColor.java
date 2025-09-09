package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.handler.step.StepFactory;
import oriedita.editor.handler.step.StepGraph;
import oriedita.editor.handler.step.StepMouseHandler;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Handles(MouseMode.CIRCLE_CHANGE_COLOR_59)
public class MouseHandlerCircleChangeColor extends StepMouseHandler<MouseHandlerCircleChangeColor.Step> {

    public enum Step {
        SELECT_CIRCLES
    }

    private final List<Circle> closestCircles = new ArrayList<>();
    private final List<LineSegment> closestAuxLines = new ArrayList<>();

    @Override
    public void reset() {
        super.reset();
        closestCircles.clear();
        closestAuxLines.clear();
    }

    @Override
    protected StepGraph<Step> initStepGraph(StepFactory stepFactory) {
        var graph = new StepGraph<>(Step.SELECT_CIRCLES);
        graph.addNode(stepFactory.createBoxSelectNode(Step.SELECT_CIRCLES,
                polygon -> {
                    for (Circle circle : closestCircles) {
                        d.getFoldLineSet().setCircleCustomizedColor(circle, d.getCustomCircleColor());
                    }
                    for (LineSegment ls : closestAuxLines) {
                        d.getFoldLineSet().setCustomized(ls, d.getCustomCircleColor());
                    }
                    if (!closestCircles.isEmpty() || !closestAuxLines.isEmpty()) {
                        d.record();
                    }
                    return Step.SELECT_CIRCLES;
                },
                p -> {
                    if (!closestCircles.isEmpty()) {
                        d.getFoldLineSet().setCircleCustomizedColor(closestCircles.get(0), d.getCustomCircleColor());
                        d.record();
                    } else if (!closestAuxLines.isEmpty()) {
                        d.getFoldLineSet().setCustomized(closestAuxLines.get(0), d.getCustomCircleColor());
                    }
                    return Step.SELECT_CIRCLES;
                }, p -> {
                    closestAuxLines.clear();
                    closestCircles.clear();
                    var line = d.getFoldLineSet().closestLineSegmentInRange(p, d.getSelectionDistance())
                            .filter(l -> l.getColor() == LineColor.CYAN_3);
                    var circle = d.getFoldLineSet().closestCircleInRange(p, d.getSelectionDistance());
                    if (line.isPresent() && circle.isPresent()) {
                        if (OritaCalc.determineLineSegmentDistance(p, line.get()) > OritaCalc.distance_circumference(p, circle.get())) {
                            closestCircles.add(circle.get());
                        } else {
                            closestAuxLines.add(line.get());
                        }
                    } else if (line.isPresent()) {
                        closestAuxLines.add(line.get());
                    } else if (circle.isPresent()) {
                        closestCircles.add(circle.get());
                    }
                }, p -> {
                    closestAuxLines.clear();
                    closestAuxLines.addAll(d.getFoldLineSet().lineSegmentsInside(p)
                            .stream()
                            .filter(l -> l.getColor() == LineColor.CYAN_3)
                            .toList());
                    closestCircles.clear();
                    closestCircles.addAll(d.getFoldLineSet().circlesInside(p));
                }));
        return graph;
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        for (Circle circle : closestCircles) {
            DrawingUtil.drawCircle(g2, circle, camera, settings.getLineWidth() + 1, settings.getPointSize());
        }
        for (LineSegment line : closestAuxLines) {
            DrawingUtil.drawAuxLine(g2, line, camera, settings.getLineWidth() + 1, settings.getPointSize(), settings.useRoundedEnds());
        }
    }

    @Inject
    public MouseHandlerCircleChangeColor() {
    }
}
