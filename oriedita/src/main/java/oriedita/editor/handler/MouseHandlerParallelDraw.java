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
@Handles(MouseMode.PARALLEL_DRAW_40)
public class MouseHandlerParallelDraw extends BaseMouseHandlerInputRestricted {

    private StepGraph<Step> steps = new StepGraph<>(Step.SELECT_TARGET_POINT, this::action_select_target_point);

    private Point targetPoint;
    private LineSegment parallelSegment;
    private LineSegment destinationSegment;
    private LineSegment resultSegment;

    private enum Step {
        SELECT_TARGET_POINT,
        SELECT_PARALLEL_SEGMENT,
        SELECT_DESTINATION
    }

    @Inject
    public MouseHandlerParallelDraw() { initializeSteps(); }

    //２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){
    //Ten t_taisyou =new Ten(); t_taisyou.set(oc.sentaisyou_ten_motome(lineStep.get(1).geta(),line_step[3].geta(),lineStep.get(0).geta()));

    public void mousePressed(Point p0) {}

    public void mouseMoved(Point p0) { highlightSelection(p0); }

    public void mouseDragged(Point p0) { highlightSelection(p0); }

    public void mouseReleased(Point p0) { steps.runCurrentAction(); }

    public void highlightSelection(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        switch (steps.getCurrentStep()) {
            case SELECT_TARGET_POINT: {
                if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
                    targetPoint = d.getClosestPoint(p);
                } else targetPoint = null;
                return;
            }
            case SELECT_PARALLEL_SEGMENT: {
                if (OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p)) < d.getSelectionDistance()) {
                    parallelSegment = d.getClosestLineSegment(p).withColor(LineColor.GREEN_6);
                } else parallelSegment = null;
                return;
            }
            case SELECT_DESTINATION: {
                if (OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p)) < d.getSelectionDistance()
                        && OritaCalc.isLineSegmentParallel(parallelSegment, d.getClosestLineSegment(p)) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {
                    destinationSegment = d.getClosestLineSegment(p).withColor(LineColor.GREEN_6);
                } else destinationSegment = null;
            }
        }
    }

    /**
     * When i_egaki_dankai is i_e_d, add a temporary fold line s_step [i_e_d + 1] (color is i colo) that
     * extends the line segment s_o to the intersection of s_k while keeping Point a as it is. Returns 1 on success,
     * -500 on failure to add due to some inconvenience.
     */
    public int s_step_additional_intersection(LineSegment s_o, LineSegment s_k, LineColor icolo) {

        Point cross_point = new Point();

        if (OritaCalc.isLineSegmentParallel(s_o, s_k, Epsilon.UNKNOWN_1EN7) == OritaCalc.ParallelJudgement.PARALLEL_NOT_EQUAL) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            return -500;
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
            resultSegment = add_sen;
            return 1;
        }

        return -500;
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, targetPoint, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, parallelSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, destinationSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
    }

    @Override
    public void reset() {
        targetPoint = null;
        parallelSegment = null;
        destinationSegment = null;
        resultSegment = null;
        initializeSteps();
    }

    private void initializeSteps() {
        steps = new StepGraph<>(Step.SELECT_TARGET_POINT, this::action_select_target_point);
        steps.addNode(Step.SELECT_PARALLEL_SEGMENT, this::action_select_parallel_segment);
        steps.addNode(Step.SELECT_DESTINATION, this::action_select_destination);

        steps.connectNodes(Step.SELECT_TARGET_POINT, Step.SELECT_PARALLEL_SEGMENT);
        steps.connectNodes(Step.SELECT_PARALLEL_SEGMENT, Step.SELECT_DESTINATION);
    }

    private Step action_select_target_point() {
        if (targetPoint == null) return null;
        return Step.SELECT_PARALLEL_SEGMENT;
    }

    private Step action_select_parallel_segment() {
        if (parallelSegment == null) return null;
        return Step.SELECT_DESTINATION;
    }

    private Step action_select_destination() {
        if (destinationSegment == null) return null;
        LineSegment s = new LineSegment(targetPoint, new Point(
                targetPoint.getX() + parallelSegment.determineBX() - parallelSegment.determineAX(),
                targetPoint.getY() + parallelSegment.determineBY() - parallelSegment.determineAY()));

        if (s_step_additional_intersection(s, destinationSegment, d.getLineColor()) > 0) {
            d.addLineSegment(resultSegment);
            d.record();
            reset();
        }
        return null;
    }
}
