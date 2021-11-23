package oriedita.editor.action;

import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.folding.util.SortingBox;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.canvas.CreasePattern_Worker;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MouseHandlerVertexMakeAngularlyFlatFoldable extends BaseMouseHandlerInputRestricted {
    public boolean workDone = false;
    LineColor icol_temp = LineColor.BLACK_0;
    CreasePattern_Worker.FourPointStep i_step_for_move_4p = CreasePattern_Worker.FourPointStep.STEP_0;

    @Inject
    public MouseHandlerVertexMakeAngularlyFlatFoldable() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38;
    }

    //マウス操作(マウスを動かしたとき)を行う関数    //System.out.println("_");
    public void mouseMoved(Point p0) {
        if (d.gridInputAssist) {
            if (d.lineStep.size() == 0) {
                i_step_for_move_4p = CreasePattern_Worker.FourPointStep.STEP_0;
            }
            Point p = new Point();

            switch (i_step_for_move_4p) {
                case STEP_0:
                    super.mouseMoved(p0);
                    break;
                case STEP_1: {
                    LineSegment candidate = new LineSegment();
                    candidate.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
                    d.lineCandidate.clear();
                    p.set(d.camera.TV2object(p0));

                    LineSegment closestLineSegment = new LineSegment();
                    closestLineSegment.set(d.get_moyori_step_lineSegment(p, 1, d.lineStep.size()));
                    if ((d.lineStep.size() >= 2) && (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.selectionDistance)) {
                        candidate.set(closestLineSegment.getA(), closestLineSegment.getB());
                        d.lineCandidate.add(candidate);
                        return;
                    }
                    break;
                }
                case STEP_2: {
                    d.lineCandidate.clear();
                    p.set(d.camera.TV2object(p0));

                    LineSegment closestLineSegment = new LineSegment();
                    closestLineSegment.set(d.getClosestLineSegment(p));
                    if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.selectionDistance) {//最寄の既存折線が近い場合
                        d.lineCandidate.add(closestLineSegment);
                        return;
                    }
                    break;
                }
            }
        }
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {//Returns 1 only if all the work is done and a new polygonal line is added. Otherwise, it returns 0.
        d.lineCandidate.clear();

        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        if (d.lineStep.size() == 0) {
            i_step_for_move_4p = CreasePattern_Worker.FourPointStep.STEP_0;
        }

        switch (i_step_for_move_4p) {
            case STEP_0:
                double decision_distance = Epsilon.UNKNOWN_1EN6;

                Point t1 = new Point();
                t1.set(d.foldLineSet.closestPointOfFoldLine(p));//点pに最も近い、「線分の端点」を返すori_s.mottomo_tikai_Tenは近い点がないと p_return.set(100000.0,100000.0)と返してくる


                if (p.distance(t1) < d.selectionDistance) {
                    //t1を端点とする折線をNarabebakoに入れる
                    SortingBox<LineSegment> nbox = new SortingBox<>();
                    for (int i = 1; i <= d.foldLineSet.getTotal(); i++) {
                        LineSegment s = d.foldLineSet.get(i);
                        if (s.getColor().isFoldingLine()) {
                            if (t1.distance(s.getA()) < decision_distance) {
                                nbox.addByWeight(s, OritaCalc.angle(s.getA(), s.getB()));
                            } else if (t1.distance(s.getB()) < decision_distance) {
                                nbox.addByWeight(s, OritaCalc.angle(s.getB(), s.getA()));
                            }
                        }
                    }

                    if (nbox.getTotal() % 2 == 1) {//t1を端点とする折線の数が奇数のときだけif{}内の処理をする
                        icol_temp = d.lineColor;
                        if (nbox.getTotal() == 1) {
                            icol_temp = nbox.getValue(1).getColor();
                        }//20180503この行追加。これは、折線が1本だけの頂点から折り畳み可能線追加機能で、その折線の延長を行った場合に、線の色を延長前の折線と合わせるため

                        for (int i = 1; i <= nbox.getTotal(); i++) {//iは角加減値を求める最初の折線のid
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

                            //System.out.println("kakukagenti="+kakukagenti);
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
                                if (t1.distance(nboxLineSegment.getA()) < decision_distance) {
                                    s_kiso.set(nboxLineSegment.getA(), nboxLineSegment.getB());
                                } else if (t1.distance(nboxLineSegment.getB()) < decision_distance) {
                                    s_kiso.set(nboxLineSegment.getB(), nboxLineSegment.getA());
                                }

                                double s_kiso_length = s_kiso.determineLength();

                                LineSegment s = OritaCalc.lineSegment_rotate(s_kiso, kakukagenti / 2.0, d.grid.getGridWidth() / s_kiso_length);
                                s.setColor(LineColor.PURPLE_8);
                                s.setActive(LineSegment.ActiveState.INACTIVE_0);
                                d.lineStepAdd(s);
                            }
                        }
                        if (d.lineStep.size() == 1) {
                            i_step_for_move_4p = CreasePattern_Worker.FourPointStep.STEP_2;
                        } else if (d.lineStep.size() > 1) {
                            i_step_for_move_4p = CreasePattern_Worker.FourPointStep.STEP_1;
                        }
                    }
                }
                workDone = false;
                return;
            case STEP_1: {
                LineSegment closestLineSegment = new LineSegment();
                closestLineSegment.set(d.get_moyori_step_lineSegment(p, 1, d.lineStep.size()));
                if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.selectionDistance) {
                    i_step_for_move_4p = CreasePattern_Worker.FourPointStep.STEP_2;
                    d.lineStep.clear();
                    d.lineStepAdd(closestLineSegment);

                    workDone = false;
                    return;
                }
                if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) >= d.selectionDistance) {
                    d.lineStep.clear();
                    workDone = false;
                    return;
                }
                break;
            }
            case STEP_2: {
                LineSegment closestLineSegment = new LineSegment();
                closestLineSegment.set(d.getClosestLineSegment(p));
                LineSegment moyori_step_lineSegment = new LineSegment();
                moyori_step_lineSegment.set(d.get_moyori_step_lineSegment(p, 1, d.lineStep.size()));
                if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) >= d.selectionDistance) {//最寄の既存折線が遠くて選択無効の場合
                    if (OritaCalc.determineLineSegmentDistance(p, moyori_step_lineSegment) < d.selectionDistance) {//最寄のstep_senbunが近い場合
                        workDone = false;
                        return;
                    }

                    //最寄のstep_senbunが遠い場合
                    d.lineStep.clear();
                    workDone = false;
                    return;
                }

                if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.selectionDistance) {//最寄の既存折線が近い場合

                    closestLineSegment.setColor(LineColor.GREEN_6);
                    d.lineStepAdd(closestLineSegment);

                    Point kousa_point = new Point();
                    kousa_point.set(OritaCalc.findIntersection(d.lineStep.get(0), d.lineStep.get(1)));
                    LineSegment add_sen = new LineSegment(kousa_point, d.lineStep.get(0).getA(), icol_temp);//20180503変更
                    if (Epsilon.high.gt0(add_sen.determineLength())) {//最寄の既存折線が有効の場合
                        d.addLineSegment(add_sen);
                        d.record();
                        d.lineStep.clear();
                        workDone = true;
                        return;

                    }

                    //最寄の既存折線が無効の場合

                    //最寄のstep_senbunが近い場合
                    if (OritaCalc.determineLineSegmentDistance(p, moyori_step_lineSegment) < d.selectionDistance) {
                        workDone = false;
                        return;
                    }

                    //最寄のstep_senbunが遠い場合
                    d.lineStep.clear();
                    workDone = false;
                    return;
                }
                break;
            }
        }

        workDone = false;
    }


//------Foldable line + grid point system input

    //Function that performs mouse operation (when dragged)
    public void mouseDragged(Point p0) {
    }
//
//課題　step線と既存折線が平行の時エラー方向に線を引くことを改善すること20170407
//
//動作仕様
//（１）点を選択（既存点選択規制）
//（２a）選択点が3以上の奇数折線の頂点の場合
//（３）
//
//
//（２b）２a以外の場合
//
//Ten t1 =new Ten();

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {

    }
}
