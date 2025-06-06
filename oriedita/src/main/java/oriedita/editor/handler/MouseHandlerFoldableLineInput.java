package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.tinylog.Logger;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.folding.util.SortingBox;

@ApplicationScoped
@Handles(MouseMode.FOLDABLE_LINE_INPUT_39)
public class MouseHandlerFoldableLineInput extends BaseMouseHandlerInputRestricted {

    CreasePattern_Worker.FourPointStep i_step_for_copy_4p = CreasePattern_Worker.FourPointStep.STEP_0;

    @Inject
    public MouseHandlerFoldableLineInput() {
    }

    //マウス操作(マウスを動かしたとき)を行う関数    //Logger.info("_");
    public void mouseMoved(Point p0) {
        if (d.getLineStep().size() == 0) {
            i_step_for_copy_4p = CreasePattern_Worker.FourPointStep.STEP_0;
        }
        if (d.getGridInputAssist()) {
            d.getLineCandidate().clear();

            Point p = d.getCamera().TV2object(p0);

            if (d.getLineStep().size() == 0) {
                i_step_for_copy_4p = CreasePattern_Worker.FourPointStep.STEP_0;
            }
            Logger.info("i_egaki_dankai= " + d.getLineStep().size() + "  ;   i_step_for_copy_4p= " + i_step_for_copy_4p);

            switch (i_step_for_copy_4p) {
                case STEP_0:
                    super.mouseMoved(p0);
                    break;
                case STEP_1: {
                    LineSegment closestLineSegment = new LineSegment(d.getClosestLineStepSegment(p, 1, d.getLineStep().size()));
                    if ((d.getLineStep().size() >= 2) && (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance())) {
                        d.getLineCandidate().clear();
                        d.getLineCandidate().add(closestLineSegment);
                        return;
                    }

                    Point closest_point = d.getClosestPoint(p);
                    if (p.distance(closest_point) < d.getSelectionDistance()) {
                        d.getLineCandidate().clear();
                        d.getLineCandidate().add(new LineSegment(closest_point, closest_point, d.getLineColor()));
                        return;
                    }
                    return;
                }
                case STEP_2: {//i_step_for_copy_4p==2であれば、以下でs_step[1]を入力折線を確定する
                    Point closest_point = d.getClosestPoint(p);

                    if (Epsilon.high.le0(closest_point.distance(d.getLineStep().get(0).getA()))) {
                        d.getLineCandidate().clear();
                        d.getLineCandidate().add(new LineSegment(closest_point, closest_point, d.getLineColor()));
                        Logger.info("i_step_for39_2_   1");

                        return;
                    }

                    if ((p.distance(d.getLineStep().get(0).getB()) < d.getSelectionDistance()) && (p.distance(d.getLineStep().get(0).getB()) <= p.distance(closest_point))) {
                        d.getLineCandidate().clear();
                        d.getLineCandidate().add(new LineSegment(d.getLineStep().get(0).getB(), d.getLineStep().get(0).getB(), d.getLineColor()));
                        Logger.info("i_step_for39_2_   2");

                        return;
                    }

                    if (p.distance(closest_point) < d.getSelectionDistance()) {
                        d.getLineCandidate().clear();
                        d.getLineCandidate().add(new LineSegment(closest_point, closest_point, d.getLineColor()));
                        Logger.info("i_step_for39_2_   3");

                        return;
                    }

                    LineSegment closestLineSegment = new LineSegment(d.getClosestLineSegment(p));
                    LineSegment moyori_step_lineSegment = new LineSegment(
                            d.getClosestLineStepSegment(p, 1, d.getLineStep().size()));
                    if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) >= d.getSelectionDistance()) {//最寄の既存折線が遠い場合
                        if (OritaCalc.determineLineSegmentDistance(p, moyori_step_lineSegment) < d.getSelectionDistance()) {//最寄のstep_senbunが近い場合
                            return;
                        }
                        //最寄のstep_senbunが遠い場合
                        Logger.info("i_step_for39_2_   4");
                    } else if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance()) {//最寄の既存折線が近い場合
                        d.getLineCandidate().clear();
                        closestLineSegment = closestLineSegment.withColor(d.getLineColor());
                        d.getLineCandidate().add(closestLineSegment);

                        Logger.info("i_step_for39_2_   5");
                    }
                }
            }
        }
    }

    //マウス操作(ボタンを押したとき)時の作業--------------
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);

        if (d.getLineStep().size() == 0) {
            i_step_for_copy_4p = CreasePattern_Worker.FourPointStep.STEP_0;
        }

        switch (i_step_for_copy_4p) {
            case STEP_0: {
                double decision_distance = Epsilon.UNKNOWN_1EN6;

                //任意の点が与えられたとき、端点もしくは格子点で最も近い点を得る
                Point closest_point = d.getClosestPoint(p);

                if (p.distance(closest_point) < d.getSelectionDistance()) {
                    //moyori_tenを端点とする折線をNarabebakoに入れる
                    SortingBox<LineSegment> nbox = new SortingBox<>();
                    for (var s : d.getFoldLineSet().getLineSegmentsIterable()) {
                        if (s.getColor().isFoldingLine()) {
                            if (closest_point.distance(s.getA()) < decision_distance) {
                                nbox.addByWeight(s, OritaCalc.angle(s.getA(), s.getB()));
                            } else if (closest_point.distance(s.getB()) < decision_distance) {
                                nbox.addByWeight(s, OritaCalc.angle(s.getB(), s.getA()));
                            }
                        }
                    }
                    if (nbox.getTotal() % 2 == 1) {//moyori_tenを端点とする折線の数が奇数のときだけif{}内の処理をする
                        for (int i = 1; i <= nbox.getTotal(); i++) {//iは角加減値を求める最初の折線のid
                            //折線が奇数の頂点周りの角加減値を2.0で割ると角加減値の最初折線と、折り畳み可能にするための追加の折線との角度になる。
                            double kakukagenti = 0.0;
                            int tikai_orisen_jyunban;
                            int tooi_orisen_jyunban;
                            for (int k = 1; k <= nbox.getTotal(); k++) {//kは角加減値を求める角度の順番
                                tikai_orisen_jyunban = i + k - 1;
                                if (tikai_orisen_jyunban > nbox.getTotal()) {
                                    tikai_orisen_jyunban = tikai_orisen_jyunban - nbox.getTotal();
                                }
                                tooi_orisen_jyunban = i + k;
                                if (tooi_orisen_jyunban > nbox.getTotal()) {
                                    tooi_orisen_jyunban = tooi_orisen_jyunban - nbox.getTotal();
                                }

                                double add_kakudo = OritaCalc.angle_between_0_360(nbox.getWeight(tooi_orisen_jyunban) - nbox.getWeight(tikai_orisen_jyunban));
                                if (k % 2 == 1) {
                                    kakukagenti = kakukagenti + add_kakudo;
                                } else if (k % 2 == 0) {
                                    kakukagenti = kakukagenti - add_kakudo;
                                }
                            }

                            if (nbox.getTotal() == 1) {
                                kakukagenti = 360.0;
                            }
                            //チェック用に角加減値の最初の角度の中にkakukagenti/2.0があるかを確認する
                            tikai_orisen_jyunban = i;
                            if (tikai_orisen_jyunban > nbox.getTotal()) {
                                tikai_orisen_jyunban = tikai_orisen_jyunban - nbox.getTotal();
                            }
                            tooi_orisen_jyunban = i + 1;
                            if (tooi_orisen_jyunban > nbox.getTotal()) {
                                tooi_orisen_jyunban = tooi_orisen_jyunban - nbox.getTotal();
                            }

                            double add_kakudo_1 = OritaCalc.angle_between_0_360(nbox.getWeight(tooi_orisen_jyunban) - nbox.getWeight(tikai_orisen_jyunban));
                            if (nbox.getTotal() == 1) {
                                add_kakudo_1 = 360.0;
                            }

                            if ((kakukagenti / 2.0 > 0.0 + Epsilon.UNKNOWN_1EN6) && (kakukagenti / 2.0 < add_kakudo_1 - Epsilon.UNKNOWN_1EN6)) {
                                //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d)
                                LineSegment s_kiso = new LineSegment();
                                LineSegment nboxLineSegment = nbox.getValue(i);
                                if (closest_point.distance(nboxLineSegment.getA()) < decision_distance) {
                                    s_kiso = new LineSegment(nboxLineSegment.getA(), nboxLineSegment.getB());
                                } else if (closest_point.distance(nboxLineSegment.getB()) < decision_distance) {
                                    s_kiso = new LineSegment(nboxLineSegment.getB(), nboxLineSegment.getA());
                                }

                                double s_kiso_length = s_kiso.determineLength();

                                LineSegment s = OritaCalc.lineSegment_rotate(s_kiso, kakukagenti / 2.0, d.getGrid().getGridWidth() / s_kiso_length);
                                s = s.withColor(LineColor.PURPLE_8);
                                s.setActive(LineSegment.ActiveState.ACTIVE_A_1);

                                d.lineStepAdd(s);
                            }
                        }

                        if (d.getLineStep().size() == 1) {
                            i_step_for_copy_4p = CreasePattern_Worker.FourPointStep.STEP_2;
                        } else if (d.getLineStep().size() > 1) {
                            i_step_for_copy_4p = CreasePattern_Worker.FourPointStep.STEP_1;
                        }
                    }

                    if (d.getLineStep().size() == 0) {//折畳み可能化線がない場合//Logger.info("_");
                        i_step_for_copy_4p = CreasePattern_Worker.FourPointStep.STEP_1;
                        LineSegment s = new LineSegment(closest_point, closest_point, LineColor.PURPLE_8);
                        s.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
                        d.lineStepAdd(s);
                    }

                }
                return;
            }
            case STEP_1: {
                LineSegment closestLineSegment = new LineSegment(d.getClosestLineStepSegment(p, 1, d.getLineStep().size()));
                if ((d.getLineStep().size() >= 2) && (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance())) {
                    i_step_for_copy_4p = CreasePattern_Worker.FourPointStep.STEP_2;
                    d.getLineStep().clear();
                    LineSegment s = new LineSegment(closestLineSegment);
                    d.lineStepAdd(s);
                    return;
                }
                Point closest_point = d.getClosestPoint(p);
                if (p.distance(closest_point) < d.getSelectionDistance()) {
                    d.getLineStep().set(0, d.getLineStep().get(0).withB(closest_point));
                    i_step_for_copy_4p = CreasePattern_Worker.FourPointStep.STEP_2;
                    return;
                }
                d.getLineStep().clear();
                d.getLineCandidate().clear();
                return;
            }
            case STEP_2: {//i_step_for_copy_4p==2であれば、以下でs_step[1]を入力折線を確定する
                Point closest_point = d.getClosestPoint(p);

                if (Epsilon.high.le0(closest_point.distance(d.getLineStep().get(0).getA()))) {
                    d.getLineStep().clear();
                    d.getLineCandidate().clear();
                    return;
                }

                if ((p.distance(d.getLineStep().get(0).getB()) < d.getSelectionDistance()) &&
                        (
                                p.distance(d.getLineStep().get(0).getB()) <= p.distance(closest_point)
                                //moyori_ten.kyori(line_step[1].getb())<Epsilon.UNKNOWN_1en8
                        )) {
                    LineSegment add_sen = new LineSegment(d.getLineStep().get(0).getA(), d.getLineStep().get(0).getB(), d.getLineColor());
                    d.addLineSegment(add_sen);
                    d.record();
                    d.getLineStep().clear();
                    d.getLineCandidate().clear();
                    return;
                }

                if (p.distance(closest_point) < d.getSelectionDistance()) {
                    d.getLineStep().set(0, d.getLineStep().get(0).withB(closest_point));
                    return;
                }

                LineSegment closestLineSegment = new LineSegment(d.getClosestLineSegment(p));

                LineSegment moyori_step_lineSegment = new LineSegment(
                        d.getClosestLineStepSegment(p, 1, d.getLineStep().size()));
                if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) >= d.getSelectionDistance()) {//最寄の既存折線が遠い場合
                    if (OritaCalc.determineLineSegmentDistance(p, moyori_step_lineSegment) < d.getSelectionDistance()) {//最寄のstep_senbunが近い場合
                        return;
                    }
                    //最寄のstep_senbunが遠い場合

                    d.getLineStep().clear();
                    d.getLineCandidate().clear();
                    return;
                }

                if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance()) {//最寄の既存折線が近い場合
                    LineSegment s = new LineSegment(closestLineSegment, LineColor.GREEN_6);
                    d.lineStepAdd(s);
                    Point kousa_point = OritaCalc.findIntersection(d.getLineStep().get(0), d.getLineStep().get(1));
                    LineSegment add_sen = new LineSegment(kousa_point, d.getLineStep().get(0).getA(), d.getLineColor());
                    if (Epsilon.high.gt0(add_sen.determineLength())) {//最寄の既存折線が有効の場合
                        d.addLineSegment(add_sen);
                        d.record();
                        d.getLineStep().clear();
                        d.getLineCandidate().clear();
                        return;
                    }
                    //最寄の既存折線が無効の場合
                    closest_point = d.getClosestPoint(p);
                    if (p.distance(closest_point) < d.getSelectionDistance()) {
                        d.getLineStep().set(0, d.getLineStep().get(0).withB(closest_point));
                        return;
                    }
                    //最寄のstep_senbunが近い場合
                    if (OritaCalc.determineLineSegmentDistance(p, moyori_step_lineSegment) < d.getSelectionDistance()) {
                        return;
                    }
                    //最寄のstep_senbunが遠い場合
                    d.getLineStep().clear();
                    d.getLineCandidate().clear();
                }
                break;
            }
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }


//33 33 33 33 33 33 33 33 33 33 33魚の骨

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
    }
}
