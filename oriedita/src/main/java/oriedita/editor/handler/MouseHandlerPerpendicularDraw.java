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
@Handles(MouseMode.PERPENDICULAR_DRAW_9)
public class MouseHandlerPerpendicularDraw extends BaseMouseHandlerInputRestricted {

    private Point targetPoint;
    private LineSegment perpendicularSegment;
    private LineSegment indicator1;
    private LineSegment indicator2;
    private LineSegment destinationSegment;
    private Step currentStep = Step.SELECT_TARGET_POINT;

    private enum Step {
        SELECT_TARGET_POINT,
        SELECT_PERPENDICULAR_SEGMENT,
        CHECK_IF_NON_BASE,
        SELECT_DESTINATION_NON_BASE,
        SELECT_DESTINATION_OR_INDICATOR,
        SELECT_DESTINATION
    }

    @Inject
    public MouseHandlerPerpendicularDraw() {}

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {}

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) { highlightSelection(p0); }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) { highlightSelection(p0); }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        switch (currentStep) {
            case SELECT_TARGET_POINT: {
                if(targetPoint == null) return;
                d.lineStepAdd(new LineSegment(targetPoint, targetPoint, d.getLineColor()));
                currentStep = Step.SELECT_PERPENDICULAR_SEGMENT;
                return;
            }
            case SELECT_PERPENDICULAR_SEGMENT:{
                if(perpendicularSegment == null) return;
                d.lineStepAdd(perpendicularSegment);
                currentStep = Step.CHECK_IF_NON_BASE;
                }
            case CHECK_IF_NON_BASE: {
                if (OritaCalc.isPointWithinLineSpan(targetPoint, perpendicularSegment)) {
                    indicator1 = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(targetPoint, OritaCalc.findProjection(OritaCalc.moveParallel(perpendicularSegment, 1.0), targetPoint), LineColor.PURPLE_8));
                    indicator2 = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(targetPoint, OritaCalc.findProjection(OritaCalc.moveParallel(perpendicularSegment, -1.0), targetPoint), LineColor.PURPLE_8));
                    d.lineStepAdd(indicator1);
                    d.lineStepAdd(indicator2);
                    currentStep = Step.SELECT_DESTINATION_OR_INDICATOR;
                    return;
                } else {
                    currentStep = Step.SELECT_DESTINATION_NON_BASE;
                }
            }
            case SELECT_DESTINATION_NON_BASE: {
                LineSegment nonBaseResultLine = new LineSegment(targetPoint, OritaCalc.findProjection(OritaCalc.lineSegmentToStraightLine(d.getLineStep().get(1)), d.getLineStep().get(0).getA()), d.getLineColor());

                if (Epsilon.high.gt0(nonBaseResultLine.determineLength())) {
                    d.addLineSegment(nonBaseResultLine);
                    d.record();
                    d.getLineStep().clear();
                    reset();
                }
                return;
            }
            case SELECT_DESTINATION_OR_INDICATOR: {
                if (OritaCalc.determineLineSegmentDistance(p, indicator1) < d.getSelectionDistance() ||
                        OritaCalc.determineLineSegmentDistance(p, indicator2) < d.getSelectionDistance()) {
                    LineSegment s = d.getClosestLineStepSegment(p, 3, 4);
                    s = new LineSegment(s.getB(), s.getA(), d.getLineColor());
                    s = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), s);

                    d.addLineSegment(s);
                    d.record();
                    d.getLineStep().clear();
                    reset();
                    return;
                }

                if (destinationSegment == null) return;
                d.lineStepAdd(destinationSegment);
                currentStep = Step.SELECT_DESTINATION;
            }
            case SELECT_DESTINATION: {
                LineSegment temp = new LineSegment(targetPoint,
                        new Point(
                                targetPoint.getX() + indicator1.determineBX() - indicator1.determineAX(),
                                targetPoint.getY() + indicator1.determineBY() - indicator1.determineAY())
                );
                LineSegment newLine = s_step_additional_intersection(temp, destinationSegment, d.getLineColor());

                if (newLine == null) return;

                d.addLineSegment(newLine);
                d.record();
                d.getLineStep().clear();
                reset();
            }
        }
    }

    public void highlightSelection(Point p0){
        if (d.getLineStep().isEmpty() && currentStep != Step.SELECT_TARGET_POINT) reset();
        Point p = d.getCamera().TV2object(p0);
        switch (currentStep) {
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
                if (OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p)) < d.getSelectionDistance()
                        && OritaCalc.isLineSegmentParallel(d.getClosestLineSegment(p), indicator1) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {
                    destinationSegment = d.getClosestLineSegment(p).withColor(LineColor.GREEN_6);
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
        switch (currentStep) {
            case SELECT_TARGET_POINT: {
                if (targetPoint == null) return;
                DrawingUtil.drawStepVertex(g2, targetPoint, d.getLineColor(), camera, d.getGridInputAssist());
                return;
            }
            case SELECT_PERPENDICULAR_SEGMENT: {
                if (perpendicularSegment == null) return;
                DrawingUtil.drawLineStep(g2, perpendicularSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
                return;
            }
            case SELECT_DESTINATION_OR_INDICATOR: {
                if (destinationSegment == null) return;
                DrawingUtil.drawLineStep(g2, destinationSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
            }
        }
    }

    @Override
    public void reset() {
        currentStep = Step.SELECT_TARGET_POINT;
        targetPoint = null;
        perpendicularSegment = null;
        indicator1 = null;
        indicator2 = null;
        destinationSegment = null;
    }
}
