package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.folding.util.SortingBox;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Handles(MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38)
public class MouseHandlerVertexMakeAngularlyFlatFoldable extends BaseMouseHandlerInputRestricted {
    public boolean isWorkDone() { return workDone; }

    private boolean workDone = false;
    LineColor icol_temp = LineColor.BLACK_0;
    CreasePattern_Worker.FourPointStep i_step_for_move_4p = CreasePattern_Worker.FourPointStep.STEP_0;

    Point p = new Point();
    StepGraph<Step> steps;

    private Point invalidPoint;
    private List<LineSegment> candidates = new ArrayList<>();
    private LineSegment selectedCandidate;
    private LineSegment destinationSegment;

    private enum Step {
        SELECT_INVALID_VERTEX,
        SELECT_CANDIDATE,
        SELECT_DESTINATION
    }

    @Inject
    public MouseHandlerVertexMakeAngularlyFlatFoldable() { initializeSteps(); }

    public void mouseMoved(Point p0) { highlightSelection(p0); }

    public void mouseDragged(Point p0) { highlightSelection(p0); }

    //Returns 1 only if all the work is done and a new polygonal line is added. Otherwise, it returns 0.
    public void mousePressed(Point p0) { steps.runCurrentAction(); }

    public void mouseReleased(Point p0) {}

    private void highlightSelection(Point p0) {
        p = d.getCamera().TV2object(p0);

        switch (steps.getCurrentStep()) {
            case SELECT_INVALID_VERTEX: {
                if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
                    invalidPoint = d.getClosestPoint(p);
                } else invalidPoint = null;
                return;
            }
            case SELECT_CANDIDATE: {
                for (LineSegment candidate : candidates) {
                    if(OritaCalc.determineLineSegmentDistance(p, candidate) < d.getSelectionDistance()) {
                        selectedCandidate = candidate.withColor(LineColor.GREEN_6);
                        return;
                    }
                }
                selectedCandidate = null;
                return;
            }
            case SELECT_DESTINATION: {
                if(OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p)) < d.getSelectionDistance()) {
                    destinationSegment = d.getClosestLineSegment(p).withColor(LineColor.ORANGE_4);
                } else destinationSegment = null;
            }
        }
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, invalidPoint, LineColor.PURPLE_8, camera, d.getGridInputAssist());
        for(LineSegment candidate : candidates) {
            DrawingUtil.drawLineStep(g2, candidate, camera, settings.getLineWidth(), d.getGridInputAssist());
        }
        DrawingUtil.drawLineStep(g2, selectedCandidate, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, destinationSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawText(g2, steps.getCurrentStep().name(), p.withX(p.getX() + 20).withY(p.getY() + 20), camera);
    }

    @Override
    public void reset() {
        invalidPoint = null;
        candidates = new ArrayList<>();
        selectedCandidate = null;
        destinationSegment = null;
        initializeSteps();
    }

    private void initializeSteps() {
        steps = new StepGraph<>(Step.SELECT_INVALID_VERTEX, this::action_select_invalid_vertex);
        steps.addNode(Step.SELECT_CANDIDATE, this::action_select_candidate);
        steps.addNode(Step.SELECT_DESTINATION, this::action_select_destination);

        gt_1_solution: {
            steps.connectNodes(Step.SELECT_INVALID_VERTEX, Step.SELECT_CANDIDATE);
            steps.connectNodes(Step.SELECT_CANDIDATE, Step.SELECT_DESTINATION);
        }

        only_1_solution: {
            steps.connectNodes(Step.SELECT_INVALID_VERTEX, Step.SELECT_DESTINATION);
        }
    }

    private Step action_select_invalid_vertex() {
        if(invalidPoint == null) return Step.SELECT_INVALID_VERTEX;

        //t1を端点とする折線をNarabebakoに入れる
        SortingBox<LineSegment> nbox = new SortingBox<>();
        for (var s : d.getFoldLineSet().getLineSegmentsIterable()) {
            if (s.getColor().isFoldingLine()) {
                if (invalidPoint.distance(s.getA()) < Epsilon.UNKNOWN_1EN6) {
                    nbox.addByWeight(s, OritaCalc.angle(s.getA(), s.getB()));
                } else if (invalidPoint.distance(s.getB()) < Epsilon.UNKNOWN_1EN6) {
                    nbox.addByWeight(s, OritaCalc.angle(s.getB(), s.getA()));
                }
            }
        }

        if (nbox.getTotal() % 2 == 1) {//t1を端点とする折線の数が奇数のときだけif{}内の処理をする
            icol_temp = d.getLineColor();
            if (nbox.getTotal() == 1) {
                icol_temp = nbox.getValue(1).getColor();
            }//20180503この行追加。これは、折線が1本だけの頂点から折り畳み可能線追加機能で、その折線の延長を行った場合に、線の色を延長前の折線と合わせるため

            //iは角加減値を求める最初の折線のid
            for (int i = 1; i <= nbox.getTotal(); i++) {
                //折線が奇数の頂点周りの角加減値を2.0で割ると角加減値の最初折線と、折り畳み可能にするための追加の折線との角度になる。
                double kakukagenti = 0.0;
                int tikai_foldLine_jyunban;
                int tooi_foldLine_jyunban;
                for (int k = 1; k <= nbox.getTotal(); k++) {//kは角加減値を求める角度の順番
                    tikai_foldLine_jyunban = i + k - 1;
                    if (tikai_foldLine_jyunban > nbox.getTotal()) {
                        tikai_foldLine_jyunban = tikai_foldLine_jyunban - nbox.getTotal();
                    }
                    tooi_foldLine_jyunban = i + k;
                    if (tooi_foldLine_jyunban > nbox.getTotal()) {
                        tooi_foldLine_jyunban = tooi_foldLine_jyunban - nbox.getTotal();
                    }

                    double add_angle = OritaCalc.angle_between_0_360(nbox.getWeight(tooi_foldLine_jyunban) - nbox.getWeight(tikai_foldLine_jyunban));
                    if (k % 2 == 1) {
                        kakukagenti = kakukagenti + add_angle;
                    } else if (k % 2 == 0) {
                        kakukagenti = kakukagenti - add_angle;
                    }
                }

                if (nbox.getTotal() == 1) {
                    kakukagenti = 360.0;
                }

                //チェック用に角加減値の最初の角度の中にkakukagenti/2.0があるかを確認する
                tikai_foldLine_jyunban = i;
                if (tikai_foldLine_jyunban > nbox.getTotal()) {
                    tikai_foldLine_jyunban = tikai_foldLine_jyunban - nbox.getTotal();
                }
                tooi_foldLine_jyunban = i + 1;
                if (tooi_foldLine_jyunban > nbox.getTotal()) {
                    tooi_foldLine_jyunban = tooi_foldLine_jyunban - nbox.getTotal();
                }

                double add_kakudo_1 = OritaCalc.angle_between_0_360(nbox.getWeight(tooi_foldLine_jyunban) - nbox.getWeight(tikai_foldLine_jyunban));
                if (nbox.getTotal() == 1) {
                    add_kakudo_1 = 360.0;
                }

                if ((kakukagenti / 2.0 > 0.0 + Epsilon.UNKNOWN_1EN6) && (kakukagenti / 2.0 < add_kakudo_1 - Epsilon.UNKNOWN_1EN6)) {
                    //if((kakukagenti/2.0>0.0-Epsilon.UNKNOWN_0000001)&&(kakukagenti/2.0<add_kakudo_1+Epsilon.UNKNOWN_0000001)){

                    //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d)
                    LineSegment s_kiso = new LineSegment();
                    LineSegment nboxLineSegment = nbox.getValue(i);
                    if (invalidPoint.distance(nboxLineSegment.getA()) < Epsilon.UNKNOWN_1EN6) {
                        s_kiso = new LineSegment(nboxLineSegment.getA(), nboxLineSegment.getB());
                    } else if (invalidPoint.distance(nboxLineSegment.getB()) < Epsilon.UNKNOWN_1EN6) {
                        s_kiso = new LineSegment(nboxLineSegment.getB(), nboxLineSegment.getA());
                    }

                    double s_kiso_length = s_kiso.determineLength();

                    LineSegment s = OritaCalc.lineSegment_rotate(s_kiso, kakukagenti / 2.0, d.getGrid().getGridWidth() / s_kiso_length);
                    s = s.withColor(LineColor.PURPLE_8);
                    s.setActive(LineSegment.ActiveState.INACTIVE_0);
                    candidates.add(s);
                }
            }
            if (d.getLineStep().size() == 1) {
                i_step_for_move_4p = CreasePattern_Worker.FourPointStep.STEP_2;
            } else if (d.getLineStep().size() > 1) {
                i_step_for_move_4p = CreasePattern_Worker.FourPointStep.STEP_1;
            }

        }
        workDone = false;
        if (candidates.size() == 1) {
            selectedCandidate = candidates.get(0);
            return Step.SELECT_DESTINATION;
        }
        return Step.SELECT_CANDIDATE;
    }

    private Step action_select_candidate() {
        if(selectedCandidate == null) return Step.SELECT_CANDIDATE;
        if (OritaCalc.determineLineSegmentDistance(p, selectedCandidate) >= d.getSelectionDistance()) {
            workDone = false;
            return Step.SELECT_CANDIDATE;
        }
        workDone = false;
        return Step.SELECT_DESTINATION;
    }

    private Step action_select_destination() {
        if(destinationSegment == null) return Step.SELECT_DESTINATION;
        if (OritaCalc.determineLineSegmentDistance(p, destinationSegment) >= d.getSelectionDistance()) {//最寄の既存折線が遠くて選択無効の場合
            destinationSegment = null;
            return Step.SELECT_DESTINATION;
        }

        Point kousa_point = OritaCalc.findIntersection(selectedCandidate, destinationSegment);
        LineSegment add_sen = new LineSegment(kousa_point, invalidPoint, icol_temp);//20180503変更
        if (Epsilon.high.gt0(add_sen.determineLength())) {//最寄の既存折線が有効の場合
            d.addLineSegment(add_sen);
            d.record();
            workDone = true;
            reset();
            return Step.SELECT_INVALID_VERTEX;
        }

        destinationSegment = null;
        return Step.SELECT_DESTINATION;
    }

}
