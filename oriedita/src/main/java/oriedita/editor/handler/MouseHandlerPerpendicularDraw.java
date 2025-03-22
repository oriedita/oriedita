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
import java.util.Arrays;
import java.util.Collections;

@ApplicationScoped
@Handles(MouseMode.PERPENDICULAR_DRAW_9)
public class MouseHandlerPerpendicularDraw extends BaseMouseHandlerInputRestricted {

    private Point p = new Point();
    private StepGraph<Step> steps = new StepGraph<>(Step.SELECT_TARGET_POINT, this::action_select_target_point);

    private Point targetPoint;
    private LineSegment perpendicularSegment;
    private LineSegment indicator;
    private LineSegment destinationSegment;

    private enum Step {
        SELECT_TARGET_POINT,
        SELECT_PERPENDICULAR_SEGMENT,
        SELECT_DESTINATION_OR_INDICATOR,
    }

    @Inject
    public MouseHandlerPerpendicularDraw() { initializeSteps(); }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) { steps.runCurrentAction(); }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) { highlightSelection(p0); }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) { highlightSelection(p0); }

    //マウス操作(ボタンを離したとき)を行う関数
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
            case SELECT_PERPENDICULAR_SEGMENT: {
                if (OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p)) < d.getSelectionDistance()) {
                    perpendicularSegment = d.getClosestLineSegment(p).withColor(LineColor.GREEN_6);
                } else perpendicularSegment = null;
                return;
            }
            case SELECT_DESTINATION_OR_INDICATOR: {
                double indicatorDistance = OritaCalc.determineLineSegmentDistance(p, indicator);
                double normalDistance = OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p));
                double baseDistance = OritaCalc.determineLineSegmentDistance(p, perpendicularSegment);

                double minDistance = Collections.min(Arrays.asList(indicatorDistance, normalDistance, baseDistance));

                if(Math.abs(minDistance - indicatorDistance) < Epsilon.UNKNOWN_1EN6
                    && indicatorDistance < d.getSelectionDistance()) {
                    destinationSegment = indicator.withColor(LineColor.ORANGE_4);
                } else if (Math.abs(minDistance - baseDistance) < Epsilon.UNKNOWN_1EN6
                        && baseDistance < d.getSelectionDistance()) {
                    destinationSegment = null;
                } else if (Math.abs(minDistance - normalDistance) < Epsilon.UNKNOWN_1EN6
                        && normalDistance < d.getSelectionDistance()
                        && OritaCalc.isLineSegmentParallel(d.getClosestLineSegment(p), indicator) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {
                    destinationSegment = d.getClosestLineSegment(p).withColor(LineColor.ORANGE_4);
                } else destinationSegment = null;
            }
        }
    }

    public LineSegment s_step_additional_intersection(LineSegment s_o, LineSegment s_k, LineColor icolo) {

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
        DrawingUtil.drawLineStep(g2, perpendicularSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, indicator, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, destinationSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawText(g2, steps.getCurrentStep().name(), p.withX(p.getX() + 20).withY(p.getY() + 20), camera);
    }

    @Override
    public void reset() {
        targetPoint = null;
        perpendicularSegment = null;
        indicator = null;
        destinationSegment = null;
        initializeSteps();
    }

    private void initializeSteps() {
        steps = new StepGraph<>(Step.SELECT_TARGET_POINT, this::action_select_target_point);
        steps.addNode(Step.SELECT_PERPENDICULAR_SEGMENT, this::action_select_perpendicular_segment);
        steps.addNode(Step.SELECT_DESTINATION_OR_INDICATOR, this::select_destination_or_indicator);

        steps.connectNodes(Step.SELECT_TARGET_POINT, Step.SELECT_PERPENDICULAR_SEGMENT);
        steps.connectNodes(Step.SELECT_PERPENDICULAR_SEGMENT, Step.SELECT_DESTINATION_OR_INDICATOR);
    }

    private Step action_select_target_point() {
        if(targetPoint == null) return null;
        return Step.SELECT_PERPENDICULAR_SEGMENT;
    }

    private Step action_select_perpendicular_segment() {
        if(perpendicularSegment == null) return null;

        if (OritaCalc.isPointWithinLineSpan(targetPoint, perpendicularSegment)) {
            indicator = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(targetPoint, OritaCalc.findProjection(OritaCalc.moveParallel(perpendicularSegment, 1.0), targetPoint), LineColor.PURPLE_8));
            indicator = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), indicator.withCoordinates(indicator.getB(), indicator.getA()));
            return Step.SELECT_DESTINATION_OR_INDICATOR;
        }

        LineSegment nonBaseResultLine = new LineSegment(targetPoint, OritaCalc.findProjection(OritaCalc.lineSegmentToStraightLine(perpendicularSegment), targetPoint), d.getLineColor());

        if (Epsilon.high.gt0(nonBaseResultLine.determineLength())) {
            d.addLineSegment(nonBaseResultLine);
            d.record();
            reset();
        }
        return null;
    }

    private Step select_destination_or_indicator() {
        if (OritaCalc.determineLineSegmentDistance(p, indicator) < d.getSelectionDistance()) {
            d.addLineSegment(indicator.withColor(d.getLineColor()));
            d.record();
            reset();
            return null;
        }

        if (destinationSegment == null) return null;
        LineSegment temp = new LineSegment(targetPoint,
                new Point(
                        targetPoint.getX() + indicator.determineBX() - indicator.determineAX(),
                        targetPoint.getY() + indicator.determineBY() - indicator.determineAY())
        );
        LineSegment newLine = s_step_additional_intersection(temp, destinationSegment, d.getLineColor());

        d.addLineSegment(newLine);
        d.record();
        reset();
        return null;
    }
}
