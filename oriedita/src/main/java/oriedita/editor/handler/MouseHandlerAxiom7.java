package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;

@ApplicationScoped
@Handles(MouseMode.AXIOM_7)
public class MouseHandlerAxiom7 extends BaseMouseHandlerInputRestricted{
    private Point p = new Point();
    private StepGraph<Step> steps;

    private Point targetPoint;
    private LineSegment targetSegment;
    private LineSegment perpendicularSegment;
    private LineSegment indicator;
    private LineSegment destinationSegment;

    private enum Step {
        SELECT_TARGET_POINT,
        SELECT_TARGET_SEGMENT,
        SELECT_PERPENDICULAR_SEGMENT,
        SELECT_DESTINATION_OR_INDICATOR,
    }

    @Inject
    public MouseHandlerAxiom7(){ initializeSteps(); }

    @Override
    public void mousePressed(Point p0) { steps.runCurrentAction(); }

    public void mouseMoved(Point p0) { highlightSelection(p0); }

    @Override
    public void mouseDragged(Point p0) { highlightSelection(p0); }

    @Override
    public void mouseReleased(Point p0) {}

    public void highlightSelection(Point p0){
        p = d.getCamera().TV2object(p0);
        switch (steps.getCurrentStep()) {
            case SELECT_TARGET_POINT: {
                if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
                    targetPoint = d.getClosestPoint(p);
                } else targetPoint = null;
                return;
            }
            case SELECT_TARGET_SEGMENT: {
                if (OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p)) < d.getSelectionDistance() &&
                        !OritaCalc.isPointWithinLineSpan(targetPoint, d.getClosestLineSegment(p))) {
                    targetSegment = d.getClosestLineSegment(p).withColor(LineColor.GREEN_6);
                } else targetSegment = null;
                return;
            }
            case SELECT_PERPENDICULAR_SEGMENT: {
                if (OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p)) < d.getSelectionDistance() &&
                        OritaCalc.isLineSegmentParallel(d.getClosestLineSegment(p), targetSegment) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {
                    perpendicularSegment = d.getClosestLineSegment(p).withColor(LineColor.GREEN_6);
                } else perpendicularSegment = null;
                return;
            }
            case SELECT_DESTINATION_OR_INDICATOR: {
                double indicatorDistance = OritaCalc.determineLineSegmentDistance(p, indicator);
                double normalDistance = OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p));
                if (indicatorDistance < normalDistance
                        && indicatorDistance < d.getSelectionDistance()) {
                    destinationSegment = indicator.withColor(LineColor.ORANGE_4);
                } else if (normalDistance < indicatorDistance
                        && normalDistance < d.getSelectionDistance()
                        && OritaCalc.isLineSegmentParallel(d.getClosestLineSegment(p), indicator) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {
                    destinationSegment = d.getClosestLineSegment(p).withColor(LineColor.ORANGE_4);
                } else destinationSegment = null;
            }
        }
    }

    public void drawAxiom7FoldIndicators(){
        LineSegment temp = new LineSegment(targetPoint, new Point(
                targetPoint.getX() + perpendicularSegment.determineBX() - perpendicularSegment.determineAX(),
                targetPoint.getY() + perpendicularSegment.determineBY() - perpendicularSegment.determineAY()));
        LineSegment extendLine = getExtendedSegment(temp, targetSegment, LineColor.PURPLE_8);
        if (extendLine == null) return;

        Point mid = OritaCalc.midPoint(targetPoint, OritaCalc.findIntersection(extendLine, targetSegment));
        indicator = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(mid, OritaCalc.findProjection(OritaCalc.moveParallel(extendLine, 1), mid), LineColor.PURPLE_8));
        indicator = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), indicator.withCoordinates(indicator.getB(), indicator.getA()));
    }

    public LineSegment getExtendedSegment(LineSegment s_o, LineSegment s_k, LineColor icolo) {
        if (OritaCalc.isLineSegmentParallel(s_o, s_k, Epsilon.UNKNOWN_1EN7) == OritaCalc.ParallelJudgement.PARALLEL_NOT_EQUAL) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            return null;
        }

        Point cross_point = new Point();
        if (OritaCalc.isLineSegmentParallel(s_o, s_k, Epsilon.UNKNOWN_1EN7) == OritaCalc.ParallelJudgement.PARALLEL_EQUAL) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            cross_point = s_k.getA();
            if (OritaCalc.distance(s_o.getA(), s_k.getA()) > OritaCalc.distance(s_o.getA(), s_k.getB())) {
                cross_point = s_k.getB();
            }
        }

        if (OritaCalc.isLineSegmentParallel(s_o, s_k, Epsilon.UNKNOWN_1EN7) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            cross_point = OritaCalc.findIntersection(s_o, s_k);
        }

        LineSegment add_sen = new LineSegment(cross_point, s_o.getA(), icolo);
        if (Epsilon.high.gt0(add_sen.determineLength())) {
            return add_sen;
        }

        return null;
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, targetPoint, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, targetSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, perpendicularSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, indicator, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, destinationSegment, camera, settings.getLineWidth(), d.getGridInputAssist());

        double textPosX = p.getX() + 20 / camera.getCameraZoomX();
        double textPosY = p.getY() + 20 / camera.getCameraZoomY();
        DrawingUtil.drawText(g2, steps.getCurrentStep().name(), p.withX(textPosX).withY(textPosY), camera);
    }

    @Override
    public void reset() {
        targetPoint = null;
        targetSegment = null;
        perpendicularSegment = null;
        indicator = null;
        destinationSegment = null;
        initializeSteps();
    }

    private void initializeSteps() {
        steps = new StepGraph<>(Step.SELECT_TARGET_POINT, this::action_select_target_point);
        steps.addNode(Step.SELECT_TARGET_SEGMENT, this::action_select_target_segment);
        steps.addNode(Step.SELECT_PERPENDICULAR_SEGMENT, this::action_select_perpendicular_segment);
        steps.addNode(Step.SELECT_DESTINATION_OR_INDICATOR, this::action_select_destination_or_indicator);

        steps.connectNodes(Step.SELECT_TARGET_POINT, Step.SELECT_TARGET_SEGMENT);
        steps.connectNodes(Step.SELECT_TARGET_SEGMENT, Step.SELECT_PERPENDICULAR_SEGMENT);
        steps.connectNodes(Step.SELECT_PERPENDICULAR_SEGMENT, Step.SELECT_DESTINATION_OR_INDICATOR);
    }

    private Step action_select_target_point() {
        if (targetPoint == null) return null;
        return Step.SELECT_TARGET_SEGMENT;
    }

    private Step action_select_target_segment() {
        if (targetSegment == null) return null;
        return Step.SELECT_PERPENDICULAR_SEGMENT;
    }

    private Step action_select_perpendicular_segment() {
        if (perpendicularSegment == null) return null;
        drawAxiom7FoldIndicators();
        return Step.SELECT_DESTINATION_OR_INDICATOR;
    }

    private Step action_select_destination_or_indicator() {
        if (OritaCalc.determineLineSegmentDistance(p, indicator) < d.getSelectionDistance()) {
            d.addLineSegment(indicator.withColor(d.getLineColor()));
            d.record();
            reset();
            return null;
        }

        if (destinationSegment == null) return null;
        LineSegment result = getExtendedSegment(indicator, destinationSegment, d.getLineColor());
        d.addLineSegment(result);
        d.record();
        reset();
        return null;
    }
}
