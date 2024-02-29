package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.StraightLine;

@ApplicationScoped
@Handles(MouseMode.AXIOM_5)
public class MouseHandlerAxiom5 extends BaseMouseHandlerInputRestricted{
    @Inject
    public MouseHandlerAxiom5() {
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        if (d.getLineStep().isEmpty()) {
            super.mouseMoved(p0);
        }
    }

    @Override
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);

        // 1. target point
        if(d.getLineStep().isEmpty()){
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, d.getLineColor()));
                return;
            }
        }

        // 2. target segment
        if(d.getLineStep().size() == 1){
            LineSegment closestLineSegment = new LineSegment(d.getClosestLineSegment(p));

            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance()) {
                closestLineSegment.setColor(LineColor.GREEN_6);
                d.lineStepAdd(closestLineSegment);
            }
            return;
        }

        // 3. pivot point
        if(d.getLineStep().size() == 2){
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.getSelectionDistance() && OritaCalc.determineLineSegmentDistance(closestPoint, d.getLineStep().get(0)) > Epsilon.UNKNOWN_1EN7) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, d.getLineColor()));
                return;
            }
        }

        // index 3 and 4 are the purple indicators
        // 4. indicators (case 1) & destination line (case 2)
        if(d.getLineStep().size() == 5){
            if (OritaCalc.determineLineSegmentDistance(p, d.getLineStep().get(3)) < d.getSelectionDistance() ||
                    OritaCalc.determineLineSegmentDistance(p, d.getLineStep().get(4)) < d.getSelectionDistance()) {
                LineSegment s = d.getClosestLineStepSegment(p, 4, 5);
                s = new LineSegment(s.getB(), s.getA(), d.getLineColor());
                s = (OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), s));

                d.addLineSegment(s);
                d.record();
                d.getLineStep().clear();
                return;
            }

            LineSegment closestLineSegment = d.getClosestLineSegment(p);

            if (!(OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance())) {
                return;
            }

            closestLineSegment.setColor(LineColor.GREEN_6);
            d.lineStepAdd(closestLineSegment);
        }
    }

    @Override
    public void mouseDragged(Point p0) {}

    @Override
    public void mouseReleased(Point p0) {
        Point p = d.getCamera().TV2object(p0);

        // first 3 are clicked
        if(d.getLineStep().size() == 3){
            double radius = OritaCalc.distance(d.getLineStep().get(0).getA(), d.getLineStep().get(2).getA());
            drawAxiom5FoldIndicators(radius, d.getLineStep().get(0).getA(), d.getLineStep().get(1), d.getLineStep().get(2).getA());
        }

        // Case 2: Click on destination line and check of mouse point is closer to one of the 2 purple indicators
        if(d.getLineStep().size() == 6){
            Point intersectPoint1 = OritaCalc.findIntersection(d.getLineStep().get(3), d.getLineStep().get(5));
            Point intersectPoint2 = OritaCalc.findIntersection(d.getLineStep().get(4), d.getLineStep().get(5));

            double d1 = OritaCalc.distance(p, intersectPoint1);
            double d2 = OritaCalc.distance(p, intersectPoint2);
            d.addLineSegment(new LineSegment(d.getLineStep().get(2).getA(), d1 < d2 ? intersectPoint1 : intersectPoint2, d.getLineColor()));
            d.record();
            d.getLineStep().clear();
        }
    }
    public void drawAxiom5FoldIndicators(double radius, Point target, LineSegment targetSegment, Point pivot) {
        // Make sure circle radius is not 0
        if(radius <= Epsilon.UNKNOWN_1EN7){ return; }

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

                    d.lineStepAdd(OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(midPoint, OritaCalc.findProjection(OritaCalc.moveParallel(projectionLine, -1.0), midPoint), LineColor.PURPLE_8)));
                    d.lineStepAdd(OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(midPoint, OritaCalc.findProjection(OritaCalc.moveParallel(projectionLine, 1.0), midPoint), LineColor.PURPLE_8)));
                    return;
                }

                d.lineStepAdd(OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(pivot, OritaCalc.findProjection(OritaCalc.moveParallel(projectionLine, 1.0), pivot), LineColor.PURPLE_8)));
                d.lineStepAdd(OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(pivot, OritaCalc.findProjection(OritaCalc.moveParallel(projectionLine, -1.0), pivot), LineColor.PURPLE_8)));
                return;
            }

            LineSegment s = new LineSegment();

            if(OritaCalc.isLineSegmentParallel(new LineSegment(pivot, target), projectionLine) == OritaCalc.ParallelJudgement.NOT_PARALLEL){
                s = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(pivot, OritaCalc.center(pivot, target, projectionPoint), LineColor.PURPLE_8));
            } else{
                s = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(pivot, projectionPoint, LineColor.PURPLE_8));
            }

            d.lineStepAdd(s);
            d.lineStepAdd(s);
        } else if (length_a > radius) { // Doesn't intersect
            d.getLineStep().clear();
        } else {  // Intersect at two points
            LineSegment l = new LineSegment(target, pivot);
            Point projectPoint = OritaCalc.findProjection(targetSegment, pivot);
            LineSegment projectLine = new LineSegment(pivot, projectPoint);

            // Length of the last segment of a right triangle (circle center, projection point, and the ultimate guiding point for indicators)
            double length_b = Math.sqrt((radius * radius) - (length_a * length_a));
            LineSegment l1 = new LineSegment(projectPoint, OritaCalc.findProjection(OritaCalc.moveParallel(projectLine, length_b), projectPoint));
            l1 = new LineSegment(pivot, l1.getB());
            LineSegment l2 = new LineSegment(projectPoint, OritaCalc.findProjection(OritaCalc.moveParallel(projectLine, -length_b), projectPoint));
            l2 = new LineSegment(pivot, l2.getB());

            Pair<LineSegment, LineSegment> ls = processPivotWithinSegmentSpan(l1, l2, targetSegment, pivot);
            l1 = ls.getLeft();
            l2 = ls.getRight();

            // Center points for placeholders to draw bisecting indicators on
            Point center1 = OritaCalc.center(pivot, l1.determineFurthestEndpoint(pivot), l.determineFurthestEndpoint(pivot));
            Point center2 = OritaCalc.center(pivot, l2.determineFurthestEndpoint(pivot), l.determineFurthestEndpoint(pivot));

            // If l and l1/l2 are aligned
            if(OritaCalc.isLineSegmentParallel(new StraightLine(l.determineFurthestEndpoint(pivot), pivot), new StraightLine(pivot, l1.determineFurthestEndpoint(pivot))) == OritaCalc.ParallelJudgement.PARALLEL_EQUAL){
                LineSegment seg = new LineSegment(pivot, OritaCalc.findProjection(OritaCalc.moveParallel(l, 1), pivot));
                center1 = OritaCalc.center(l.determineFurthestEndpoint(pivot), l1.determineFurthestEndpoint(pivot), seg.determineFurthestEndpoint(pivot));
            }
            if(OritaCalc.isLineSegmentParallel(new StraightLine(l.determineFurthestEndpoint(pivot), pivot), new StraightLine(pivot, l2.determineFurthestEndpoint(pivot))) == OritaCalc.ParallelJudgement.PARALLEL_EQUAL){
                LineSegment seg = new LineSegment(pivot, OritaCalc.findProjection(OritaCalc.moveParallel(l, 1), pivot));
                center2 = OritaCalc.center(l.determineFurthestEndpoint(pivot), l2.determineFurthestEndpoint(pivot), seg.determineFurthestEndpoint(pivot));
            }

            determineIndicators(l, l1, l2, pivot, center1, center2, target, targetSegment, pivot);
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
                LineSegment s1 = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(center, center1, LineColor.PURPLE_8));
                LineSegment s2 = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(center, center2, LineColor.PURPLE_8));

                d.lineStepAdd(s1);
                d.lineStepAdd(s2);
                return;
            }
            // If target point is within target segment span
            if(OritaCalc.isLineSegmentParallel(l1, l) == OritaCalc.ParallelJudgement.PARALLEL_EQUAL){
                //target not in span 2
                LineSegment s = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(center, center2, LineColor.PURPLE_8));

                d.lineStepAdd(s);
                d.lineStepAdd(s);
                return;
            }
            if(OritaCalc.isLineSegmentParallel(l2, l) == OritaCalc.ParallelJudgement.PARALLEL_EQUAL){
                //target not in span 3
                LineSegment s = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(center, center1, LineColor.PURPLE_8));

                d.lineStepAdd(s);
                d.lineStepAdd(s);
            }
        } else{
            LineSegment s1 = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(pivot, OritaCalc.findProjection(OritaCalc.moveParallel(l1, 1.0), pivot), LineColor.PURPLE_8));
            LineSegment s2 = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(pivot, OritaCalc.findProjection(OritaCalc.moveParallel(l2, -1.0), pivot), LineColor.PURPLE_8));

            d.lineStepAdd(s1);
            d.lineStepAdd(s2);
        }
    }
}
