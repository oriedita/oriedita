package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.FoldLineAdditionalInputMode;
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
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Handles(MouseMode.LINE_SEGMENT_DELETE_3)
public class MouseHandlerLineSegmentDelete extends StepMouseHandler<MouseHandlerLineSegmentDelete.Step> {
    public enum Step {
        SELECT_LINES
    }

    private final List<Circle> closestCircles = new ArrayList<>();
    private final List<LineSegment> closestLines = new ArrayList<>();
    private final List<LineSegment> closestAuxLines = new ArrayList<>();

    @Override
    protected StepGraph<Step> initStepGraph(StepFactory stepFactory) {
        var st = new StepGraph<>(Step.SELECT_LINES);
        st.addNode(stepFactory.createBoxSelectNode(Step.SELECT_LINES,
                this::deleteInsideBox, this::deleteSingleLineOrCircle,
                this::highlightSingleLineOrCircle, this::highlightBox));
        return st;
    }

    private void highlightBox(Polygon p) {
        closestCircles.clear();
        closestLines.clear();
        closestAuxLines.clear();
        var lines = d.getFoldLineSet().lineSegmentsInside(p);
        var circles = d.getFoldLineSet().getCircles().stream()
                .filter(p::totu_boundary_inside).toList();
        var auxLines = d.getAuxLines().lineSegmentsInside(p);

        var mode = d.getI_foldLine_additional();
        switch (mode) {
            case POLY_LINE_0 -> closestLines.addAll(
                    lines.stream()
                    .filter(l -> l.getColor().isFoldingLine())
                    .toList());
            case BLACK_LINE_2 -> closestLines.addAll(
                    lines.stream()
                    .filter(l -> l.getColor() == LineColor.BLACK_0)
                    .toList());
            case AUX_LIVE_LINE_3 -> closestLines.addAll(
                    lines.stream()
                    .filter(l -> l.getColor() == LineColor.CYAN_3)
                    .toList());
            case BOTH_4 -> {
                closestLines.addAll(lines);
                closestCircles.addAll(circles);
            }
            case AUX_LINE_1 -> closestAuxLines.addAll(auxLines);
        }
    }

    private void highlightSingleLineOrCircle(Point p) {
        closestCircles.clear();
        closestLines.clear();
        closestAuxLines.clear();
        var line = d.getFoldLineSet().closestLineSegmentInRange(p, d.getSelectionDistance());
        var circle = d.getFoldLineSet().closestCircleInRange(p, d.getSelectionDistance());
        var mode = d.getI_foldLine_additional();
        if (mode == FoldLineAdditionalInputMode.AUX_LINE_1){
            line = d.getAuxLines().closestLineSegmentInRange(p, d.getSelectionDistance());
        }
        if (mode != FoldLineAdditionalInputMode.BOTH_4 && line.isPresent()) {
            var l = line.get();
            switch (mode) {
                case AUX_LIVE_LINE_3 -> {
                    if (l.getColor() == LineColor.CYAN_3) {
                        closestLines.add(l);
                    }
                }
                case BLACK_LINE_2 -> {
                    if (l.getColor() == LineColor.BLACK_0) {
                        closestLines.add(l);
                    }
                }
                case POLY_LINE_0 -> {
                    if (l.getColor().isFoldingLine()){
                        closestLines.add(l);
                    }
                }
                case AUX_LINE_1 -> closestAuxLines.add(l);
                default -> {}
            }
        }
        if (mode == FoldLineAdditionalInputMode.BOTH_4) {
            if (line.isPresent() && circle.isPresent()) {
                if (OritaCalc.determineLineSegmentDistance(p, line.get()) > OritaCalc.distance_circumference(p, circle.get())) {
                    closestCircles.add(circle.get());
                } else {
                    closestLines.add(line.get());
                }
            } else if (line.isPresent()) {
                closestLines.add(line.get());
            } else if (circle.isPresent()) {
                closestCircles.add(circle.get());
            }
        }
    }

    private Step deleteInsideBox(Polygon polygon) {
        closestLines.forEach(c -> d.getFoldLineSet().deleteLine(c));
        closestCircles.forEach(c -> d.getFoldLineSet().deleteCircle(c));
        closestAuxLines.forEach(c -> d.getAuxLines().deleteLineSegment_vertex(c));
        d.organizeCircles();
        if (closestCircles.size() + closestLines.size() + closestAuxLines.size() > 0) {
            d.record();
        }
        return Step.SELECT_LINES;
    }

    private Step deleteSingleLineOrCircle(Point p) {
        var changed = false;
        if (!closestLines.isEmpty()) {
            d.getFoldLineSet().deleteLineSegment_vertex(closestLines.get(0));
            changed = true;
        }
        if (!closestCircles.isEmpty()) {
            d.getFoldLineSet().deleteCircle(closestCircles.get(0));
            changed = true;
            d.organizeCircles();
        }
        if (!closestAuxLines.isEmpty()) {
            d.getAuxLines().deleteLineSegment_vertex(closestAuxLines.get(0));
            changed = true;
        }
        if (changed) {
            d.record();
        }
        return Step.SELECT_LINES;
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        for (Circle circle : closestCircles) {
            DrawingUtil.drawCircle(g2, circle, camera, settings.getLineWidth() + 1, settings.getPointSize());
        }
        for (LineSegment line : closestLines) {
            DrawingUtil.drawLineStep(g2, line, camera, settings.getLineWidth() + 1, settings.getGridInputAssist());
        }
        for (LineSegment line : closestAuxLines) {
            g2.setStroke(new BasicStroke(settings.getAuxLineWidth() + 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
            DrawingUtil.drawAuxLiveLine(g2, line, camera, settings.getLineWidth() + 1,
                    settings.getPointSize(), settings.getAuxLineWidth() + 1);
        }
    }

    @Inject
    public MouseHandlerLineSegmentDelete() {
    }
}
