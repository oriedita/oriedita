package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.SQUARE_BISECTOR_7)
public class MouseHandlerSquareBisector extends BaseMouseHandlerInputRestricted {
    @Inject
    public MouseHandlerSquareBisector() {
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        if (d.getLineStep().size() <= 2) {
            //Only close existing points are displayed
            super.mouseMoved(p0);
        }
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = new Point();
        LineSegment line = new LineSegment();
        p.set(d.getCamera().TV2object(p0));

        // If condition is for 2 lines bisect
        if (d.getLineStep().isEmpty() || d.getLineStep().get(0).determineLength() > 0) {
            // Click 2 lines to form bisect and then a destination line
            // Only in first line click, no point is allowed within the selection radius
            if (d.getLineStep().isEmpty() && d.getClosestPoint(p).distance(p) > d.getSelectionDistance()) {
                line.set(d.getClosestLineSegment(p));
                if (OritaCalc.determineLineSegmentDistance(p, line) < d.getSelectionDistance()) {
                    line.setColor(LineColor.GREEN_6);
                    d.lineStepAdd(line);
                }
                return;
            }
            if (!d.getLineStep().isEmpty()) {
                line.set(d.getClosestLineSegment(p));
                if(d.getLineStep().size() == 1 && OritaCalc.determineLineSegmentDistance(p, line) < d.getSelectionDistance() &&
                        (OritaCalc.distance(OritaCalc.findProjection(d.getLineStep().get(0), line.getA()), line.getA()) > Epsilon.UNKNOWN_1EN7 ||
                                OritaCalc.distance(OritaCalc.findProjection(d.getLineStep().get(0), line.getB()), line.getB()) > Epsilon.UNKNOWN_1EN7)){
                    line.setColor(LineColor.GREEN_6);
                    d.lineStepAdd(line);
                    return;
                }
                if (d.getLineStep().size() > 1 && OritaCalc.determineLineSegmentDistance(p, line) < d.getSelectionDistance()) {
                    line.setColor(LineColor.GREEN_6);
                    d.lineStepAdd(line);
                    return;
                }
                if(d.getLineStep().size() == 4){
                    if (OritaCalc.determineLineSegmentDistance(p, d.getLineStep().get(2)) < d.getSelectionDistance() ||
                            OritaCalc.determineLineSegmentDistance(p, d.getLineStep().get(3)) < d.getSelectionDistance()) {
                        LineSegment s = d.get_moyori_step_lineSegment(p, 3, 4);
                        s.set(s.getB(), s.getA(), d.getLineColor());
                        s.set(OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), s));

                        d.addLineSegment(s);
                        d.record();
                        d.getLineStep().clear();
                        return;
                    }
                }
            }
        }
        // Else if condition is for 3 points bisect
        if (d.getLineStep().isEmpty() || d.getLineStep().get(0).determineLength() <= 0.0) {
            if (d.getLineStep().size() <= 2) {
                Point closestPoint = d.getClosestPoint(p);
                if (p.distance(closestPoint) < d.getSelectionDistance()) {
                    d.lineStepAdd(new LineSegment(closestPoint, closestPoint, d.getLineColor()));
                }
            } else if (d.getLineStep().size() == 3) {
                LineSegment closestLineSegment = new LineSegment();
                closestLineSegment.set(d.getClosestLineSegment(p));
                if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance()) {
                    closestLineSegment.setColor(LineColor.GREEN_6);
                    d.lineStepAdd(closestLineSegment);
                }
            }
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        Point p = new Point();
        p.set(d.getCamera().TV2object(p0));

        // Calculation for 3 points
        if (d.getLineStep().size() == 4 && d.getLineStep().get(0).determineLength() < Epsilon.UNKNOWN_1EN4) {
            //三角形の内心を求める    public Ten oc.naisin(Ten ta,Ten tb,Ten tc)
            Point naisin = new Point();
            naisin.set(OritaCalc.center(d.getLineStep().get(0).getA(), d.getLineStep().get(1).getA(), d.getLineStep().get(2).getA()));

            LineSegment add_sen2 = new LineSegment(d.getLineStep().get(1).getA(), naisin);

            //add_sen2とs_step[4]の交点はoc.kouten_motome(Senbun s1,Senbun s2)で求める//２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
            Point cross_point = new Point();
            cross_point.set(OritaCalc.findIntersection(add_sen2, d.getLineStep().get(3)));

            LineSegment add_sen = new LineSegment(cross_point, d.getLineStep().get(1).getA(), d.getLineColor());
            if (Epsilon.high.gt0(add_sen.determineLength())) {
                d.addLineSegment(add_sen);
                d.record();
            }

            d.getLineStep().clear();
        }
        // Calculation for 2 lines
        else if (d.getLineStep().size() >= 2 && d.getLineStep().get(0).determineLength() > 0) {
            // When the first 2 lines aren't parallel
            if (OritaCalc.isLineSegmentParallel(d.getLineStep().get(0), d.getLineStep().get(1), Epsilon.UNKNOWN_1EN4) == OritaCalc.ParallelJudgement.NOT_PARALLEL && d.getLineStep().size() == 3) {
                // Find intersection of 2 lines
                Point intersection = new Point();
                intersection.set(OritaCalc.findIntersection(d.getLineStep().get(0), d.getLineStep().get(1)));

                // Find another point by taking the center point of 3 points
                /* 2 points that are not the intersection have to be far away from said intersection
                 * to prevent them from being the intersection themselves, which can cause problems when
                 * finding the triangle center.
                 */
                Point center = new Point();
                center.set(OritaCalc.center(intersection, d.getLineStep().get(0).determineFurthestEndpoint(intersection), d.getLineStep().get(1).determineFurthestEndpoint(intersection)));

                // Make a temporary line to connect intersection and center
                LineSegment tempBisect = new LineSegment(intersection, center);

                // Find intersection of temp line to the destination line
                Point cross_point = new Point();
                cross_point.set(OritaCalc.findIntersection(tempBisect, d.getLineStep().get(2)));

                // Draw the bisector
                LineSegment destinationLine = new LineSegment(cross_point, intersection, d.getLineColor());
                if (Epsilon.high.gt0(destinationLine.determineLength())) {
                    d.addLineSegment(destinationLine);
                    d.record();
                }

                d.getLineStep().clear();
            }

            // When the first 2 lines are parallel
            // Step 1: draw the bisector indicator
            else if (OritaCalc.isLineSegmentParallel(d.getLineStep().get(0), d.getLineStep().get(1), Epsilon.UNKNOWN_1EN4) == OritaCalc.ParallelJudgement.PARALLEL_NOT_EQUAL && d.getLineStep().size() == 2) {
                // Get a point projected on the other line
                Point projectedPoint = new Point();
                projectedPoint.set(OritaCalc.findProjection(d.getLineStep().get(0), d.getLineStep().get(1).getA()));

                // Get midpoint
                Point midPoint = new Point();
                midPoint.set(OritaCalc.midPoint(d.getLineStep().get(1).getA(), projectedPoint));

                /*
                 Draw purple indicators for bisector
                 At this point, there should be 3 lines in lineStep (first 2 initial line clicks and a line for midpoint)
                 --> Next 2 should be at index 3 and 4
                */
                LineSegment tempPerpenLine = new LineSegment();
                tempPerpenLine.set(d.getLineStep().get(1).getA(), projectedPoint);
                d.lineStepAdd(OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(midPoint, OritaCalc.findProjection(OritaCalc.moveParallel(tempPerpenLine, -1.0), midPoint), LineColor.PURPLE_8)));
                d.lineStepAdd(OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(midPoint, OritaCalc.findProjection(OritaCalc.moveParallel(tempPerpenLine, 1.0), midPoint), LineColor.PURPLE_8)));
                return;
            }
        }

        // Step 2.b: Get the 2 destination lines and form the actual bisector
        if (d.getLineStep().size() == 6) {
            // Find 2 intersection points
            Point intersect1 = new Point();
            intersect1.set(OritaCalc.findIntersection(d.getLineStep().get(2), d.getLineStep().get(4)));
            Point intersect2 = new Point();
            intersect2.set(OritaCalc.findIntersection(d.getLineStep().get(2), d.getLineStep().get(5)));

            // Draw the bisector
            LineSegment bisector = new LineSegment(intersect1, intersect2, d.getLineColor());

            if (Epsilon.high.gt0(bisector.determineLength())) {
                d.addLineSegment(bisector);
                d.record();
            }
            d.getLineStep().clear();
        }
    }
}

