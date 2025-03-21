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
    private StepGraph<Step> steps = new StepGraph<>(Step.SELECT_TARGET_POINT, this::action_select_target_point);

    private Point midPoint = new Point();
    private Point targetPoint;
    private LineSegment targetSegment;
    private LineSegment perpendicularSegment;
    private LineSegment indicator1;
    private LineSegment indicator2;
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
    public void mousePressed(Point p0) {}

    public void mouseMoved(Point p0) { highlightSelection(p0); }

    @Override
    public void mouseDragged(Point p0) { highlightSelection(p0); }

    @Override
    public void mouseReleased(Point p0) {
        steps.runCurrentAction();
    }

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
                    perpendicularSegment = d.getClosestLineSegment(p).withColor(LineColor.ORANGE_4);
                } else perpendicularSegment = null;
                return;
            }
            case SELECT_DESTINATION_OR_INDICATOR: {
                if (OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p)) < d.getSelectionDistance() &&
                        OritaCalc.isLineSegmentParallel(d.getClosestLineSegment(p), indicator1) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {
                    destinationSegment = d.getClosestLineSegment(p).withColor(LineColor.GREEN_6);
                } else destinationSegment = null;
            }
        }
    }

    public Point drawAxiom7FoldIndicators(Point targetPoint, LineSegment targetSegment, LineSegment perpendicularSegment){
        LineSegment temp = new LineSegment(targetPoint, new Point(
                targetPoint.getX() + perpendicularSegment.determineBX() - perpendicularSegment.determineAX(),
                targetPoint.getY() + perpendicularSegment.determineBY() - perpendicularSegment.determineAY()));
        LineSegment extendLine = getExtendedSegment(temp, targetSegment, LineColor.PURPLE_8);

        if (extendLine == null) { return null; }

        Point mid = OritaCalc.midPoint(targetPoint, OritaCalc.findIntersection(extendLine, targetSegment));

        indicator1 = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(mid, OritaCalc.findProjection(OritaCalc.moveParallel(extendLine, 1), mid), LineColor.PURPLE_8));
        indicator2 = OritaCalc.fullExtendUntilHit(d .getFoldLineSet(), new LineSegment(mid, OritaCalc.findProjection(OritaCalc.moveParallel(extendLine, -1), mid), LineColor.PURPLE_8));
        return mid;
    }

    public LineSegment getExtendedSegment(LineSegment s_o, LineSegment s_k, LineColor icolo) {
        Point cross_point = new Point();

        if (OritaCalc.isLineSegmentParallel(s_o, s_k, Epsilon.UNKNOWN_1EN7) == OritaCalc.ParallelJudgement.PARALLEL_NOT_EQUAL) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            return null;
        }

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
        DrawingUtil.drawLineStep(g2, indicator1, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, indicator2, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, destinationSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawText(g2, steps.getCurrentStep().name(), p.withX(p.getX() + 20).withY(p.getY() + 20), camera);
    }

    @Override
    public void reset() {
        targetPoint = null;
        targetSegment = null;
        perpendicularSegment = null;
        indicator1 = null;
        indicator2 = null;
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
        midPoint = drawAxiom7FoldIndicators(targetPoint, targetSegment, perpendicularSegment);
        return Step.SELECT_DESTINATION_OR_INDICATOR;
    }

    private Step action_select_destination_or_indicator() {
        if (OritaCalc.determineLineSegmentDistance(p, indicator1) < d.getSelectionDistance() ||
                OritaCalc.determineLineSegmentDistance(p, indicator2) < d.getSelectionDistance()) {
            LineSegment s = OritaCalc.determineLineSegmentDistance(p, indicator1) < OritaCalc.determineLineSegmentDistance(p, indicator2)
                    ? indicator1 : indicator2;
            s = new LineSegment(s.getB(), s.getA(), d.getLineColor());
            s = (OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), s));

            d.addLineSegment(s);
            d.record();
            reset();
            return null;
        }

        if (destinationSegment == null) return null;
        LineSegment midTemp = new LineSegment(
                midPoint,
                new Point(
                        midPoint.getX() + indicator1.determineBX() - indicator1.determineAX(),
                        midPoint.getY() + indicator1.determineBY() - indicator1.determineAY()));
        LineSegment result = getExtendedSegment(midTemp, destinationSegment, d.getLineColor());
        d.addLineSegment(result);
        d.record();
        reset();
        return null;
    }
}
