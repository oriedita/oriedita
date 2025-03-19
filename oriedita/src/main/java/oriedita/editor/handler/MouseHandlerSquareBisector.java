package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.tinylog.Logger;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@Handles(MouseMode.SQUARE_BISECTOR_7)
public class MouseHandlerSquareBisector extends BaseMouseHandlerInputRestricted {
    private int counter_3P = 0;
    private int counter_2L = 0;
    private List<Point> pointsList_3P = Arrays.asList(null, null, null);
    private LineSegment destinationSegment_3P;
    private List<LineSegment> segmentsList_2L = Arrays.asList(null, null);
    private LineSegment destinationSegment_2L_NP;
    private List<LineSegment> indicatorsList_2L_P = Arrays.asList(null, null);
    private int counter_2L_P = 0;
    private List<LineSegment> destinationSegmentsList_2L_P = Arrays.asList(null, null);
    private Step currentStep = Step.SELECT_2L_OR_3P;

    private enum Step {
        SELECT_2L_OR_3P,
        SELECT_3P,
        SELECT_DESTINATION_3P,
        SELECT_2L,
        CHECK_IF_PARALLEL,
        SELECT_DESTINATION_2L_NP,
        SELECT_DESTINATION_2L_P,
    }

    private class StepNode {
        private Step step;
        private Runnable action;

        public StepNode(Step step, Runnable action) {
            this.step = step;
            this.action = action;
        }

        public void performAction() { action.run(); }
    }

    @Inject
    public MouseHandlerSquareBisector() {}

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) { highlightSelection(p0); }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {}

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) { highlightSelection(p0); }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        switch (currentStep) {
            case SELECT_2L_OR_3P: {
                if (pointsList_3P.get(counter_3P) != null) {
                    counter_3P++;
                    currentStep = Step.SELECT_3P;
                    return;
                }
                if (segmentsList_2L.get(counter_2L) != null) {
                    counter_2L++;
                    currentStep = Step.SELECT_2L;
                    return;
                }
                return;
            }
            case SELECT_3P: {
                if(pointsList_3P.get(counter_3P) == null) return;
                counter_3P++;
                if (counter_3P != 3) return;
                currentStep = Step.SELECT_DESTINATION_3P;
                return;
            }
            case SELECT_DESTINATION_3P: {
                if (destinationSegment_3P == null) return;

                Point naisin = OritaCalc.center(pointsList_3P.get(0), pointsList_3P.get(1), pointsList_3P.get(2));
                LineSegment add_sen2 = new LineSegment(pointsList_3P.get(1), naisin);
                Point cross_point = OritaCalc.findIntersection(add_sen2, destinationSegment_3P);

                LineSegment add_sen = new LineSegment(cross_point, pointsList_3P.get(1), d.getLineColor());
                if (Epsilon.high.gt0(add_sen.determineLength())) {
                    d.addLineSegment(add_sen);
                    d.record();
                    reset();
                }
            }
            case SELECT_2L: {
                if(segmentsList_2L.get(counter_2L) == null) return;
                counter_2L++;
                if (counter_2L != 2) return;
                currentStep = Step.CHECK_IF_PARALLEL;   // Continue immediately
            }
            case CHECK_IF_PARALLEL: {
                if (OritaCalc.isLineSegmentParallel(segmentsList_2L.get(0), segmentsList_2L.get(1), Epsilon.UNKNOWN_1EN4) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {
                    currentStep = Step.SELECT_DESTINATION_2L_NP;
                    return;
                } else {
                    Point projectedPoint = OritaCalc.findProjection(segmentsList_2L.get(0), segmentsList_2L.get(1).getA());

                    // Get midpoint
                    Point midPoint = OritaCalc.midPoint(segmentsList_2L.get(1).getA(), projectedPoint);

                    // Draw purple indicators for bisector
                    LineSegment tempPerpenLine = new LineSegment(segmentsList_2L.get(1).getA(), projectedPoint);
                    indicatorsList_2L_P.set(0, OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(midPoint, OritaCalc.findProjection(OritaCalc.moveParallel(tempPerpenLine, -1.0), midPoint), LineColor.PURPLE_8)));
                    indicatorsList_2L_P.set(1, OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(midPoint, OritaCalc.findProjection(OritaCalc.moveParallel(tempPerpenLine, 1.0), midPoint), LineColor.PURPLE_8)));
                    currentStep = Step.SELECT_DESTINATION_2L_P;
                    return;
                }
            }
            case SELECT_DESTINATION_2L_NP: {
                if (destinationSegment_2L_NP == null) return;

                Point intersection = OritaCalc.findIntersection(segmentsList_2L.get(0), segmentsList_2L.get(1));

                // Find another point by taking the center point of 3 points
                /* 2 points that are not the intersection have to be far away from said intersection
                 * to prevent them from being the intersection themselves, which can cause problems when
                 * finding the triangle center.
                 */
                Point center = OritaCalc.center(intersection, segmentsList_2L.get(0).determineFurthestEndpoint(intersection), segmentsList_2L.get(1).determineFurthestEndpoint(intersection));

                // Make a temporary line to connect intersection and center
                LineSegment tempBisect = new LineSegment(intersection, center);

                // Find intersection of temp line to the destination line
                Point cross_point = OritaCalc.findIntersection(tempBisect, destinationSegment_2L_NP);

                // Draw the bisector
                LineSegment destinationLine = new LineSegment(cross_point, intersection, d.getLineColor());
                if (Epsilon.high.gt0(destinationLine.determineLength())) {
                    d.addLineSegment(destinationLine);
                    d.record();
                    reset();
                }
                return;
            }
            case SELECT_DESTINATION_2L_P: {
                if (OritaCalc.determineLineSegmentDistance(p, indicatorsList_2L_P.get(0)) < d.getSelectionDistance() ||
                        OritaCalc.determineLineSegmentDistance(p, indicatorsList_2L_P.get(1)) < d.getSelectionDistance()) {
                    LineSegment s = OritaCalc.determineLineSegmentDistance(p, indicatorsList_2L_P.get(0)) < OritaCalc.determineLineSegmentDistance(p, indicatorsList_2L_P.get(1))
                            ? indicatorsList_2L_P.get(0) : indicatorsList_2L_P.get(1);
                    s = new LineSegment(s.getB(), s.getA(), d.getLineColor());
                    s = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), s).withColor(d.getLineColor());
                    d.addLineSegment(s);
                    d.record();
                    reset();
                    return;
                }

                if(destinationSegmentsList_2L_P.get(counter_2L_P) == null) return;
                counter_2L_P++;
                if (counter_2L_P != 2) return;
                Point intersect1 = OritaCalc.findIntersection(indicatorsList_2L_P.get(0), destinationSegmentsList_2L_P.get(0));
                Point intersect2 = OritaCalc.findIntersection(indicatorsList_2L_P.get(0), destinationSegmentsList_2L_P.get(1));

                // Draw the bisector
                LineSegment bisector = new LineSegment(intersect1, intersect2, d.getLineColor());

                if (Epsilon.high.gt0(bisector.determineLength())) {
                    d.addLineSegment(bisector);
                    d.record();
                    reset();
                }
            }
        }
    }

    public void highlightSelection(Point p0){
        Point p = d.getCamera().TV2object(p0);
        Logger.info(currentStep);
        switch (currentStep) {
            case SELECT_2L_OR_3P: {
                double pointDistance = p.distance(d.getClosestPoint(p));
                double segmentDistance = OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p));
                boolean isPointInRadius = pointDistance < d.getSelectionDistance();
                boolean isSegmentInRadius = segmentDistance < d.getSelectionDistance();

                if (isPointInRadius) {
                    pointsList_3P.set(counter_3P, d.getClosestPoint(p));
                } else pointsList_3P.set(counter_3P, null);

                if (!isPointInRadius && isSegmentInRadius) {
                    segmentsList_2L.set(counter_2L, d.getClosestLineSegment(p).withColor(LineColor.GREEN_6));
                } else segmentsList_2L.set(counter_2L, null);
                return;
            }
            case SELECT_DESTINATION_3P: {
                if (OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p)) < d.getSelectionDistance()) {
                    destinationSegment_3P = d.getClosestLineSegment(p).withColor(LineColor.GREEN_6);
                } else destinationSegment_3P = null;
                return;
            }
            case SELECT_3P: {
                if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
                    pointsList_3P.set(counter_3P, d.getClosestPoint(p));
                } else pointsList_3P.set(counter_3P, null);
                return;
            }
            case SELECT_2L: {
                if (OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p)) < d.getSelectionDistance()) {
                    segmentsList_2L.set(counter_2L, d.getClosestLineSegment(p).withColor(LineColor.GREEN_6));
                } else segmentsList_2L.set(counter_2L, null);
                return;
            }
            case SELECT_DESTINATION_2L_NP: {
                if (OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p)) < d.getSelectionDistance()) {
                    destinationSegment_2L_NP = d.getClosestLineSegment(p).withColor(LineColor.GREEN_6);
                } else destinationSegment_2L_NP = null;
                return;
            }case SELECT_DESTINATION_2L_P: {
                if (OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p)) < d.getSelectionDistance()) {
                    destinationSegmentsList_2L_P.set(counter_2L_P, d.getClosestLineSegment(p).withColor(LineColor.ORANGE_4));
                } else destinationSegmentsList_2L_P.set(counter_2L_P, null);
            }
        }
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, pointsList_3P.get(0), d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, pointsList_3P.get(1), d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, pointsList_3P.get(2), d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, destinationSegment_3P, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, segmentsList_2L.get(0), camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, segmentsList_2L.get(1), camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, indicatorsList_2L_P.get(0), camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, indicatorsList_2L_P.get(1), camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, destinationSegmentsList_2L_P.get(0), camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, destinationSegmentsList_2L_P.get(1), camera, settings.getLineWidth(), d.getGridInputAssist());
    }

    @Override
    public void reset() {
        currentStep = Step.SELECT_2L_OR_3P;
        counter_3P = 0;
        counter_2L = 0;
        pointsList_3P = Arrays.asList(null, null, null);
        destinationSegment_3P = null;
        segmentsList_2L = Arrays.asList(null, null);
        destinationSegment_2L_NP = null;
        indicatorsList_2L_P = Arrays.asList(null, null);
        counter_2L_P = 0;
        destinationSegmentsList_2L_P = Arrays.asList(null, null);
    }
}

