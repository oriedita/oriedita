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

@ApplicationScoped
@Handles(MouseMode.AXIOM_5)
public class MouseHandlerAxiom5 extends BaseMouseHandlerInputRestricted{

    private Point targetPoint;
    private LineSegment targetSegment;
    private Point pivotPoint;
    private LineSegment indicator1;
    private LineSegment indicator2;
    private LineSegment destinationSegment;
    private Step currentStep = Step.SELECT_TARGET_POINT;

    private enum Step {
        SELECT_TARGET_POINT,
        SELECT_TARGET_SEGMENT,
        SELECT_PIVOT_POINT,
        SHOW_INDICATORS,
        SELECT_DESTINATION_OR_INDICATOR,
        SELECT_DESTINATION
    }

    @Inject
    public MouseHandlerAxiom5() {}

    @Override
    public void mousePressed(Point p0) {}

    public void mouseMoved(Point p0) { highlightSelection(p0); }

    @Override
    public void mouseDragged(Point p0) { highlightSelection(p0); }

    @Override
    public void mouseReleased(Point p0) {
        Point p = d.getCamera().TV2object(p0);

        switch (currentStep) {
            case SELECT_TARGET_POINT: {
                if (targetPoint == null) return;
                currentStep = Step.SELECT_TARGET_SEGMENT;
                return;
            }
            case SELECT_TARGET_SEGMENT: {
                if (targetSegment == null) return;
                currentStep = Step.SELECT_PIVOT_POINT;
                return;
            }
            case SELECT_PIVOT_POINT: {
                if (pivotPoint == null) return;
                currentStep = Step.SHOW_INDICATORS; // Continue immediately
            }
            case SHOW_INDICATORS: {
                double radius = OritaCalc.distance(targetPoint, pivotPoint);
                drawAxiom5FoldIndicators(radius, targetPoint, targetSegment, pivotPoint);
                currentStep = Step.SELECT_DESTINATION_OR_INDICATOR;
                return;
            }
            case SELECT_DESTINATION_OR_INDICATOR: {
                if (OritaCalc.determineLineSegmentDistance(p, indicator1) < d.getSelectionDistance() ||
                        OritaCalc.determineLineSegmentDistance(p, indicator2) < d.getSelectionDistance()) {
                    LineSegment s = OritaCalc.determineLineSegmentDistance(p, indicator1) < OritaCalc.determineLineSegmentDistance(p, indicator2)
                            ? indicator1 : indicator2;
                    s = new LineSegment(s.getB(), s.getA(), d.getLineColor());
                    s = (OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), s));

                    d.addLineSegment(s);
                    d.record();
                    reset();
                    return;
                }

                if (destinationSegment == null) return;
                currentStep = Step.SELECT_DESTINATION;  // Continue immediately
            }
            case SELECT_DESTINATION: {
                Point intersectPoint1 = OritaCalc.findIntersection(indicator1, destinationSegment);
                Point intersectPoint2 = OritaCalc.findIntersection(indicator2, destinationSegment);

                double d1 = OritaCalc.distance(p, intersectPoint1);
                double d2 = OritaCalc.distance(p, intersectPoint2);
                d.addLineSegment(new LineSegment(pivotPoint, d1 < d2 ? intersectPoint1 : intersectPoint2, d.getLineColor()));
                d.record();
                reset();
            }
        }
    }

    public void highlightSelection(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        switch (currentStep) {
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
                if (OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p)) < d.getSelectionDistance()) {
                    destinationSegment = d.getClosestLineSegment(p).withColor(LineColor.GREEN_6);
                } else destinationSegment = null;
            }
        }
    }

    public void drawAxiom5FoldIndicators(double radius, Point target, LineSegment targetSegment, Point pivot) {
        // Make sure circle radius is not 0
        if(radius <= Epsilon.UNKNOWN_1EN7){ reset(); return; }

        double length_a = 0.0; //Distance between the center of the circle and target segment
        Point center = new Point(pivot);

        // If pivot point is not within the target segment span
        if(!OritaCalc.isPointWithinLineSpan(pivot, targetSegment)){
            length_a = OritaCalc.distance(center, OritaCalc.findProjection(targetSegment, center));
        }

        // Intersect at one point
        if(Math.abs(length_a - radius) < Epsilon.UNKNOWN_1EN7){
            Point projectionPoint = OritaCalc.findProjection(targetSegment, pivot);
            LineSegment projectionLine = new LineSegment(pivot, projectionPoint);

            if(OritaCalc.isPointWithinLineSpan(target, projectionLine)){
                if(OritaCalc.distance(projectionPoint, target) < Epsilon.UNKNOWN_1EN7){
                    Point midPoint = new Point(OritaCalc.midPoint(pivot, projectionPoint));

                    indicator1 = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(midPoint, OritaCalc.findProjection(OritaCalc.moveParallel(projectionLine, -1.0), midPoint), LineColor.PURPLE_8));
                    indicator2 = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(midPoint, OritaCalc.findProjection(OritaCalc.moveParallel(projectionLine, 1.0), midPoint), LineColor.PURPLE_8));
                    return;
                }

                indicator1 = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(pivot, OritaCalc.findProjection(OritaCalc.moveParallel(projectionLine, 1.0), pivot), LineColor.PURPLE_8));
                indicator2 = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(pivot, OritaCalc.findProjection(OritaCalc.moveParallel(projectionLine, -1.0), pivot), LineColor.PURPLE_8));
                return;
            }

            LineSegment s;

            if(OritaCalc.isLineSegmentParallel(new LineSegment(pivot, target), projectionLine) == OritaCalc.ParallelJudgement.NOT_PARALLEL){
                s = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(pivot, OritaCalc.center(pivot, target, projectionPoint), LineColor.PURPLE_8));
            } else{
                s = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(pivot, projectionPoint, LineColor.PURPLE_8));
            }
            indicator1 = s;
            indicator2 = s;
        } else if (length_a > radius) { // Doesn't intersect
            reset();
        } else {  // Intersect at two points
            LineSegment l = new LineSegment(target, pivot);
            Point projectPoint = OritaCalc.findProjection(targetSegment, pivot);

            // Length of the last segment of a right triangle (circle center, projection point, and the ultimate guiding point for indicators)
            double length_b = Math.sqrt((radius * radius) - (length_a * length_a));
            LineSegment l1 = processProjectedLineOfIndicator(pivot, projectPoint, length_b);
            LineSegment l2 = processProjectedLineOfIndicator(pivot, projectPoint, -length_b);

            // Recalibrate l1 and l2 for later calculations
            Pair<LineSegment, LineSegment> ls = processPivotWithinSegmentSpan(l1, l2, targetSegment, pivot);
            l1 = ls.getLeft();
            l2 = ls.getRight();

            // Center points for placeholders to draw bisecting indicators on
            Point center1 = processCenter(pivot, l, l1);
            Point center2 = processCenter(pivot, l, l2);

            // Decide the indicators on different cases
            determineIndicators(l, l1, l2, pivot, center1, center2, target, targetSegment, pivot);
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
    }

    @Override
    public void reset() {
        currentStep = Step.SELECT_TARGET_POINT;
        targetPoint = null;
        targetSegment = null;
        pivotPoint = null;
        indicator1 = null;
        indicator2 = null;
        destinationSegment = null;
    }
}
