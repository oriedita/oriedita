package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.StraightLine;

import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Collections;

@ApplicationScoped
@Handles(MouseMode.AXIOM_5)
public class MouseHandlerAxiom5 extends BaseMouseHandlerInputRestricted{
    private Point p = new Point();
    private StepCollection<Step> steps;

    private Point targetPoint;
    private LineSegment targetSegment;
    private Point pivotPoint;
    private LineSegment indicator1;
    private LineSegment indicator2;
    private LineSegment destinationSegment;

    private enum Step {
        SELECT_TARGET_POINT,
        SELECT_TARGET_SEGMENT,
        SELECT_PIVOT_POINT,
        SELECT_DESTINATION_OR_INDICATOR,
    }

    // axiom 5 is a fucking bitch to write
    // 2.3 metric fucktons of edge cases
    // coz each segment instance has 2 distinct endpoints
    // so if say l1 has A and B going left to right, and l2 is right to left
    // gotta handle that shit
    // the logic is purely geometric

    @Inject
    public MouseHandlerAxiom5() { initializeSteps(); }

    @Override
    public void mousePressed(Point p0) { steps.runCurrentAction(); }

    public void mouseMoved(Point p0) { highlightSelection(p0); }

    @Override
    public void mouseDragged(Point p0) { highlightSelection(p0); }

    @Override
    public void mouseReleased(Point p0) {}

    public void highlightSelection(Point p0) {
        p = d.getCamera().TV2object(p0);
        switch (steps.getCurrentStep()) {
            case SELECT_TARGET_POINT: {
                if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
                    targetPoint = d.getClosestPoint(p);
                } else targetPoint = null;
                return;
            }
            case SELECT_TARGET_SEGMENT: {
                if (OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p)) < d.getSelectionDistance()) {
                    targetSegment = d.getClosestLineSegment(p).withColor(LineColor.GREEN_6);
                } else targetSegment = null;
                return;
            }
            case SELECT_PIVOT_POINT: {
                if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()
                        && OritaCalc.determineLineSegmentDistance(d.getClosestPoint(p), new LineSegment(targetPoint, targetPoint)) > Epsilon.UNKNOWN_1EN7
                        && !(OritaCalc.isPointWithinLineSpan(d.getClosestPoint(p), targetSegment) && OritaCalc.isPointWithinLineSpan(targetPoint, targetSegment))) {
                    pivotPoint = d.getClosestPoint(p);
                } else pivotPoint = null;
                return;
            }
            case SELECT_DESTINATION_OR_INDICATOR: {
                double indicator1Distance = OritaCalc.determineLineSegmentDistance(p, indicator1);
                double indicator2Distance = OritaCalc.determineLineSegmentDistance(p, indicator2);
                double targetSegmentDistance = OritaCalc.determineLineSegmentDistance(p, targetSegment);
                double normalDistance = OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p));

                double minDistance = Collections.min(Arrays.asList(indicator1Distance, indicator2Distance, targetSegmentDistance, normalDistance));

                if (Math.abs(minDistance - indicator1Distance) < Epsilon.UNKNOWN_1EN6
                        && indicator1Distance < d.getSelectionDistance()) {
                    destinationSegment = indicator1.withColor(LineColor.ORANGE_4);
                } else if (Math.abs(minDistance - indicator2Distance) < Epsilon.UNKNOWN_1EN6
                        && indicator2Distance < d.getSelectionDistance()) {
                    destinationSegment = indicator2.withColor(LineColor.ORANGE_4);
                } else if (Math.abs(minDistance - targetSegmentDistance) < Epsilon.UNKNOWN_1EN6
                        && targetSegmentDistance < d.getSelectionDistance()) {
                    destinationSegment = null;
                } else if (Math.abs(minDistance - normalDistance) < Epsilon.UNKNOWN_1EN6
                        && normalDistance < d.getSelectionDistance()) {
                    destinationSegment = d.getClosestLineSegment(p).withColor(LineColor.ORANGE_4);
                } else destinationSegment = null;
            }
        }
    }

    public void drawAxiom5FoldIndicators(double radius) {
        // Make sure circle radius is not 0
        if(radius <= Epsilon.UNKNOWN_1EN7){ reset(); return; }

        double length_a = 0.0; //Distance between the center of the circle and target segment
        Point center = new Point(pivotPoint);

        // If pivot point is not within the target segment span
        if(!OritaCalc.isPointWithinLineSpan(pivotPoint, targetSegment)){
            length_a = OritaCalc.distance(center, OritaCalc.findProjection(targetSegment, center));
        }

        // Intersect at one point
        if(Math.abs(length_a - radius) < Epsilon.UNKNOWN_1EN7){
            Point projectionPoint = OritaCalc.findProjection(targetSegment, pivotPoint);
            LineSegment projectionLine = new LineSegment(pivotPoint, projectionPoint);

            if(OritaCalc.isPointWithinLineSpan(targetPoint, projectionLine)){
                if(OritaCalc.distance(projectionPoint, targetPoint) < Epsilon.UNKNOWN_1EN7){
                    Point midPoint = new Point(OritaCalc.midPoint(pivotPoint, projectionPoint));

                    indicator1 = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(midPoint, OritaCalc.findProjection(OritaCalc.moveParallel(projectionLine, -1.0), midPoint), LineColor.PURPLE_8));
                    indicator2 = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(midPoint, OritaCalc.findProjection(OritaCalc.moveParallel(projectionLine, 1.0), midPoint), LineColor.PURPLE_8));
                    return;
                }

                indicator1 = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(pivotPoint, OritaCalc.findProjection(OritaCalc.moveParallel(projectionLine, 1.0), pivotPoint), LineColor.PURPLE_8));
                indicator2 = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(pivotPoint, OritaCalc.findProjection(OritaCalc.moveParallel(projectionLine, -1.0), pivotPoint), LineColor.PURPLE_8));
                return;
            }

            LineSegment s;

            if(OritaCalc.isLineSegmentParallel(new LineSegment(pivotPoint, targetPoint), projectionLine) == OritaCalc.ParallelJudgement.NOT_PARALLEL){
                s = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(pivotPoint, OritaCalc.center(pivotPoint, targetPoint, projectionPoint), LineColor.PURPLE_8));
            } else{
                s = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(pivotPoint, projectionPoint, LineColor.PURPLE_8));
            }
            indicator1 = s;
            indicator2 = s;
        } else if (length_a > radius) { // Doesn't intersect
            reset();
        } else {  // Intersect at two points
            LineSegment l = new LineSegment(targetPoint, pivotPoint);
            Point projectPoint = OritaCalc.findProjection(targetSegment, pivotPoint);

            // Length of the last segment of a right triangle (circle center, projection point, and the ultimate guiding point for indicators)
            double length_b = Math.sqrt((radius * radius) - (length_a * length_a));
            LineSegment l1 = processProjectedLineOfIndicator(pivotPoint, projectPoint, length_b);
            LineSegment l2 = processProjectedLineOfIndicator(pivotPoint, projectPoint, -length_b);

            // Recalibrate l1 and l2 for later calculations
            Pair<LineSegment, LineSegment> ls = processPivotWithinSegmentSpan(l1, l2, targetSegment, pivotPoint);
            l1 = ls.getLeft();
            l2 = ls.getRight();

            // Center points for placeholders to draw bisecting indicators on
            Point center1 = processCenter(pivotPoint, l, l1);
            Point center2 = processCenter(pivotPoint, l, l2);

            // Decide the indicators on different cases
            determineIndicators(l, l1, l2, pivotPoint, center1, center2, targetPoint, targetSegment, pivotPoint);
        }
    }

    private LineSegment processProjectedLineOfIndicator(Point pivot, Point projectPoint, double length){
        LineSegment projectLine = new LineSegment(pivot, projectPoint);
        LineSegment line = new LineSegment(projectPoint, OritaCalc.findProjection(OritaCalc.moveParallel(projectLine, length), projectPoint));
        return new LineSegment(pivot, line.getB());
    }

    private Point processCenter(Point pivot, LineSegment l1, LineSegment l2){
        // l1 and l2 are 2 lines forming a triangle with a common point
        // If l1 and l2 are aligned/parallel
        //       l1 ⤵              l2 ⤵
        // ------------------O-------------------
        //     common point ⤴
        if(OritaCalc.isLineSegmentParallel(new StraightLine(l1.determineFurthestEndpoint(pivot), pivot), new StraightLine(pivot, l2.determineFurthestEndpoint(pivot))) == OritaCalc.ParallelJudgement.PARALLEL_EQUAL){
            LineSegment seg = new LineSegment(pivot, OritaCalc.findProjection(OritaCalc.moveParallel(l1, 1), pivot));
            // return the center using a different point on a shifted line parallel to l2
            return OritaCalc.center(l1.determineFurthestEndpoint(pivot), l2.determineFurthestEndpoint(pivot), seg.determineFurthestEndpoint(pivot));
        } else { // return the default center
            return OritaCalc.center(pivot, l2.determineFurthestEndpoint(pivot), l1.determineFurthestEndpoint(pivot));
        }
    }

    private Pair<LineSegment, LineSegment> processPivotWithinSegmentSpan(LineSegment l1, LineSegment l2, LineSegment targetSegment, Point pivot){
        // If pivot point is within the target segment span
        if(OritaCalc.isPointWithinLineSpan(pivot, targetSegment)){
            // pivot within span
            if(OritaCalc.distance(pivot, targetSegment.getA()) < Epsilon.UNKNOWN_1EN7){
                //pivot within span touching A
                l1 = (new LineSegment(pivot, OritaCalc.point_rotate(pivot, targetSegment.getB(), 180)));
                l2 = (new LineSegment(pivot, targetSegment.getB()));
                return new ImmutablePair<>(l1, l2);
            }
            if(OritaCalc.distance(pivot, targetSegment.getB()) < Epsilon.UNKNOWN_1EN7){
                //pivot within span touching B
                l1 = (new LineSegment(pivot, targetSegment.getA()));
                l2 = (new LineSegment(pivot, OritaCalc.point_rotate(pivot, targetSegment.getA(), 180)));
                return new ImmutablePair<>(l1, l2);
            }

            boolean isOutsideSegmentA = targetSegment.determineLength() > OritaCalc.distance(targetSegment.getA(), pivot) &&
                    OritaCalc.distance(targetSegment.getB(), pivot) > targetSegment.determineLength();
            boolean isOutsideSegmentB = targetSegment.determineLength() > OritaCalc.distance(targetSegment.getB(), pivot) &&
                    OritaCalc.distance(targetSegment.getA(), pivot) > targetSegment.determineLength();

            // If pivot point is outside the target segment
            //pivot within span outside A
            l1 = (new LineSegment(pivot,isOutsideSegmentA ? OritaCalc.point_rotate(pivot, targetSegment.getB(), 180) : targetSegment.getA()));

            // pivot within span outside B
            l2 = (new LineSegment(pivot,isOutsideSegmentB ? OritaCalc.point_rotate(pivot, targetSegment.getA(), 180) : targetSegment.getB()));
            return new ImmutablePair<>(l1, l2);
        }
        return new ImmutablePair<>(l1, l2);
    }

    private void determineIndicators(LineSegment l, LineSegment l1, LineSegment l2, Point center, Point center1, Point center2, Point target, LineSegment targetSegment, Point pivot){
        if(OritaCalc.distance(center1, OritaCalc.findProjection(targetSegment, center1)) > Epsilon.UNKNOWN_1EN7 ||
                OritaCalc.distance(center2, OritaCalc.findProjection(targetSegment, center2)) > Epsilon.UNKNOWN_1EN7){
            // If target point is not within target segment span
            if(!OritaCalc.isPointWithinLineSpan(target, targetSegment)){
                //target not in span 1
                indicator1 = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(center, center1, LineColor.PURPLE_8));
                indicator2 = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(center, center2, LineColor.PURPLE_8));
                return;
            }
            // If target point is within target segment span
            if(OritaCalc.isLineSegmentParallel(l1, l) == OritaCalc.ParallelJudgement.PARALLEL_EQUAL){
                //target not in span 2
                LineSegment s = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(center, center2, LineColor.PURPLE_8));
                indicator1 = s;
                indicator2 = s;
                return;
            }
            if(OritaCalc.isLineSegmentParallel(l2, l) == OritaCalc.ParallelJudgement.PARALLEL_EQUAL){
                //target not in span 3
                LineSegment s = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(center, center1, LineColor.PURPLE_8));
                indicator1 = s;
                indicator2 = s;
            }
        } else{
            indicator1 = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(pivot, OritaCalc.findProjection(OritaCalc.moveParallel(l1, 1.0), pivot), LineColor.PURPLE_8));
            indicator2 = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(pivot, OritaCalc.findProjection(OritaCalc.moveParallel(l2, -1.0), pivot), LineColor.PURPLE_8));
        }
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, targetPoint, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, targetSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, pivotPoint, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, indicator1, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, indicator2, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, destinationSegment, camera, settings.getLineWidth(), d.getGridInputAssist());

        double textPosX = p.getX() + 20 / camera.getCameraZoomX();
        double textPosY = p.getY() + 20 / camera.getCameraZoomY();
        DrawingUtil.drawText(g2, steps.getCurrentStep().name(), p.withX(textPosX).withY(textPosY), camera);
    }

    @Override
    public void reset() {
        targetPoint = null;
        targetSegment = null;
        pivotPoint = null;
        indicator1 = null;
        indicator2 = null;
        destinationSegment = null;
        initializeSteps();
    }

    private void initializeSteps() {
        steps = new StepCollection<>(Step.SELECT_TARGET_POINT, this::action_select_target_point);
        steps.addNode(Step.SELECT_TARGET_SEGMENT, this::action_select_target_segment);
        steps.addNode(Step.SELECT_PIVOT_POINT, this::action_select_pivot_point);
        steps.addNode(Step.SELECT_DESTINATION_OR_INDICATOR, this::action_select_destination_or_indicator);
    }

    private void action_select_target_point() {
        if (targetPoint == null) return;
        steps.setCurrentStep(Step.SELECT_TARGET_SEGMENT);
    }

    private void action_select_target_segment() {
        if (targetSegment == null) return;
        steps.setCurrentStep(Step.SELECT_PIVOT_POINT);
    }

    private void action_select_pivot_point() {
        if (pivotPoint == null) return;
        double radius = OritaCalc.distance(targetPoint, pivotPoint);
        drawAxiom5FoldIndicators(radius);
        steps.setCurrentStep(Step.SELECT_DESTINATION_OR_INDICATOR);
    }

    private void action_select_destination_or_indicator() {
        if (OritaCalc.determineLineSegmentDistance(p, indicator1) < d.getSelectionDistance() ||
                OritaCalc.determineLineSegmentDistance(p, indicator2) < d.getSelectionDistance()) {
            LineSegment s = OritaCalc.determineLineSegmentDistance(p, indicator1) < OritaCalc.determineLineSegmentDistance(p, indicator2)
                    ? indicator1 : indicator2;
            s = new LineSegment(s.getB(), s.getA(), d.getLineColor());
            s = (OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), s));

            d.addLineSegment(s);
            d.record();
            reset();
        }

        if (destinationSegment == null) return;
        Point intersectPoint1 = OritaCalc.findIntersection(indicator1, destinationSegment);
        Point intersectPoint2 = OritaCalc.findIntersection(indicator2, destinationSegment);

        double d1 = OritaCalc.distance(p, intersectPoint1);
        double d2 = OritaCalc.distance(p, intersectPoint2);
        d.addLineSegment(new LineSegment(pivotPoint, d1 < d2 ? intersectPoint1 : intersectPoint2, d.getLineColor()));
        d.record();
        reset();
    }
}
