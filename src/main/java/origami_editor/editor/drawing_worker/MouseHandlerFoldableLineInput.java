package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;
import origami_editor.sortingbox.SortingBox;
import origami_editor.sortingbox.WeightedValue;

public class MouseHandlerFoldableLineInput extends BaseMouseHandlerInputRestricted {
    DrawingWorker.FourPointStep i_step_for_copy_4p = DrawingWorker.FourPointStep.STEP_0;

    public MouseHandlerFoldableLineInput(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.FOLDABLE_LINE_INPUT_39;
    }

    //マウス操作(マウスを動かしたとき)を行う関数    //System.out.println("_");
    public void mouseMoved(Point p0) {
        if (d.lineStep.size() == 0) {
            i_step_for_copy_4p = DrawingWorker.FourPointStep.STEP_0;
        }
        if (d.gridInputAssist) {
            d.lineCandidate.clear();

            Point p = new Point();
            p.set(d.camera.TV2object(p0));

            if (d.lineStep.size() == 0) {
                i_step_for_copy_4p = DrawingWorker.FourPointStep.STEP_0;
            }
            System.out.println("i_egaki_dankai= " + d.lineStep.size() + "  ;   i_step_for_copy_4p= " + i_step_for_copy_4p);

            switch (i_step_for_copy_4p) {
                case STEP_0:
                    super.mouseMoved(p0);
                    break;
                case STEP_1: {
                    LineSegment closestLineSegment = new LineSegment();
                    closestLineSegment.set(d.get_moyori_step_lineSegment(p, 1, d.lineStep.size()));
                    if ((d.lineStep.size() >= 2) && (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.selectionDistance)) {
                        d.lineCandidate.clear();
                        d.lineCandidate.add(closestLineSegment);
                        return;
                    }

                    Point closest_point = d.getClosestPoint(p);
                    if (p.distance(closest_point) < d.selectionDistance) {
                        d.lineCandidate.clear();
                        d.lineCandidate.add(new LineSegment(closest_point, closest_point, d.lineColor));
                        return;
                    }
                    return;
                }
                case STEP_2: {//i_step_for_copy_4p==2であれば、以下でs_step[1]を入力折線を確定する
                    Point closest_point = d.getClosestPoint(p);

                    if (closest_point.distance(d.lineStep.get(0).getA()) < 0.00000001) {
                        d.lineCandidate.clear();
                        d.lineCandidate.add(new LineSegment(closest_point, closest_point, d.lineColor));
                        System.out.println("i_step_for39_2_   1");

                        return;
                    }

                    if ((p.distance(d.lineStep.get(0).getB()) < d.selectionDistance) && (p.distance(d.lineStep.get(0).getB()) <= p.distance(closest_point))) {
                        d.lineCandidate.clear();
                        d.lineCandidate.add(new LineSegment(d.lineStep.get(0).getB(), d.lineStep.get(0).getB(), d.lineColor));
                        System.out.println("i_step_for39_2_   2");

                        return;
                    }

                    if (p.distance(closest_point) < d.selectionDistance) {
                        d.lineCandidate.clear();
                        d.lineCandidate.add(new LineSegment(closest_point, closest_point, d.lineColor));
                        System.out.println("i_step_for39_2_   3");

                        return;
                    }

                    LineSegment closestLineSegment = new LineSegment();
                    closestLineSegment.set(d.getClosestLineSegment(p));
                    LineSegment moyori_step_lineSegment = new LineSegment();
                    moyori_step_lineSegment.set(d.get_moyori_step_lineSegment(p, 1, d.lineStep.size()));
                    if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) >= d.selectionDistance) {//最寄の既存折線が遠い場合
                        if (OritaCalc.determineLineSegmentDistance(p, moyori_step_lineSegment) < d.selectionDistance) {//最寄のstep_senbunが近い場合
                            return;
                        }
                        //最寄のstep_senbunが遠い場合
                        System.out.println("i_step_for39_2_   4");
                    } else if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.selectionDistance) {//最寄の既存折線が近い場合
                        d.lineCandidate.clear();
                        closestLineSegment.setColor(d.lineColor);
                        d.lineCandidate.add(closestLineSegment);

                        System.out.println("i_step_for39_2_   5");
                    }
                }
            }
        }
    }

    //マウス操作(ボタンを押したとき)時の作業--------------
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        if (d.lineStep.size() == 0) {
            i_step_for_copy_4p = DrawingWorker.FourPointStep.STEP_0;
        }

        switch (i_step_for_copy_4p) {
            case STEP_0: {
                double decision_distance = 0.000001;

                //任意の点が与えられたとき、端点もしくは格子点で最も近い点を得る
                Point closest_point = d.getClosestPoint(p);

                if (p.distance(closest_point) < d.selectionDistance) {
                    //moyori_tenを端点とする折線をNarabebakoに入れる
                    SortingBox<LineSegment> nbox = new SortingBox<>();
                    for (int i = 1; i <= d.foldLineSet.getTotal(); i++) {
                        LineSegment s = d.foldLineSet.get(i);
                        if (s.getColor().isFoldingLine()) {
                            if (closest_point.distance(s.getA()) < decision_distance) {
                                nbox.container_i_smallest_first(new WeightedValue<>(s, OritaCalc.angle(s.getA(), s.getB())));
                            } else if (closest_point.distance(s.getB()) < decision_distance) {
                                nbox.container_i_smallest_first(new WeightedValue<>(s, OritaCalc.angle(s.getB(), s.getA())));
                            }
                        }
                    }
                    if (nbox.getTotal() % 2 == 1) {//moyori_tenを端点とする折線の数が奇数のときだけif{}内の処理をする
                        for (int i = 0; i < nbox.getTotal(); i++) {//iは角加減値を求める最初の折線のid
                            //折線が奇数の頂点周りの角加減値を2.0で割ると角加減値の最初折線と、折り畳み可能にするための追加の折線との角度になる。
                            double kakukagenti = 0.0;
                            int tikai_orisen_jyunban;
                            int tooi_orisen_jyunban;
                            for (int k = 0; k < nbox.getTotal(); k++) {//kは角加減値を求める角度の順番
                                tikai_orisen_jyunban = i + k - 1;
                                if (tikai_orisen_jyunban >= nbox.getTotal()) {
                                    tikai_orisen_jyunban = tikai_orisen_jyunban - nbox.getTotal();
                                }
                                tooi_orisen_jyunban = i + k;
                                if (tooi_orisen_jyunban >= nbox.getTotal()) {
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
                            if (tikai_orisen_jyunban >= nbox.getTotal()) {
                                tikai_orisen_jyunban = tikai_orisen_jyunban - nbox.getTotal();
                            }
                            tooi_orisen_jyunban = i + 1;
                            if (tooi_orisen_jyunban >= nbox.getTotal()) {
                                tooi_orisen_jyunban = tooi_orisen_jyunban - nbox.getTotal();
                            }

                            double add_kakudo_1 = OritaCalc.angle_between_0_360(nbox.getWeight(tooi_orisen_jyunban) - nbox.getWeight(tikai_orisen_jyunban));
                            if (nbox.getTotal() == 1) {
                                add_kakudo_1 = 360.0;
                            }

                            if ((kakukagenti / 2.0 > 0.0 + 0.000001) && (kakukagenti / 2.0 < add_kakudo_1 - 0.000001)) {
                                //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d)
                                LineSegment s_kiso = new LineSegment();
                                LineSegment nboxLineSegment = nbox.getValue(i);
                                if (closest_point.distance(nboxLineSegment.getA()) < decision_distance) {
                                    s_kiso.set(nboxLineSegment.getA(), nboxLineSegment.getB());
                                } else if (closest_point.distance(nboxLineSegment.getB()) < decision_distance) {
                                    s_kiso.set(nboxLineSegment.getB(), nboxLineSegment.getA());
                                }

                                double s_kiso_length = s_kiso.determineLength();

                                LineSegment s = OritaCalc.lineSegment_rotate(s_kiso, kakukagenti / 2.0, d.grid.getGridWidth() / s_kiso_length);
                                s.setColor(LineColor.PURPLE_8);
                                s.setActive(LineSegment.ActiveState.ACTIVE_A_1);

                                d.lineStepAdd(s);
                            }
                        }

                        if (d.lineStep.size() == 1) {
                            i_step_for_copy_4p = DrawingWorker.FourPointStep.STEP_2;
                        } else if (d.lineStep.size() > 1) {
                            i_step_for_copy_4p = DrawingWorker.FourPointStep.STEP_1;
                        }
                    }

                    if (d.lineStep.size() == 0) {//折畳み可能化線がない場合//System.out.println("_");
                        i_step_for_copy_4p = DrawingWorker.FourPointStep.STEP_1;
                        LineSegment s = new LineSegment(closest_point, closest_point, LineColor.PURPLE_8);
                        s.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
                        d.lineStepAdd(s);
                    }

                }
                return;
            }
            case STEP_1: {
                LineSegment closestLineSegment = new LineSegment();
                closestLineSegment.set(d.get_moyori_step_lineSegment(p, 1, d.lineStep.size()));
                if ((d.lineStep.size() >= 2) && (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.selectionDistance)) {
                    i_step_for_copy_4p = DrawingWorker.FourPointStep.STEP_2;
                    d.lineStep.clear();
                    LineSegment s = new LineSegment();
                    s.set(closestLineSegment);
                    d.lineStepAdd(s);
                    return;
                }
                Point closest_point = d.getClosestPoint(p);
                if (p.distance(closest_point) < d.selectionDistance) {
                    d.lineStep.get(0).setB(closest_point);
                    i_step_for_copy_4p = DrawingWorker.FourPointStep.STEP_2;
                    return;
                }
                d.lineStep.clear();
                d.lineCandidate.clear();
                return;
            }
            case STEP_2: {//i_step_for_copy_4p==2であれば、以下でs_step[1]を入力折線を確定する
                Point closest_point = d.getClosestPoint(p);

                if (closest_point.distance(d.lineStep.get(0).getA()) < 0.00000001) {
                    d.lineStep.clear();
                    d.lineCandidate.clear();
                    return;
                }

                if ((p.distance(d.lineStep.get(0).getB()) < d.selectionDistance) &&
                        (
                                p.distance(d.lineStep.get(0).getB()) <= p.distance(closest_point)
                                //moyori_ten.kyori(line_step[1].getb())<0.00000001
                        )) {
                    LineSegment add_sen = new LineSegment(d.lineStep.get(0).getA(), d.lineStep.get(0).getB(), d.lineColor);
                    d.addLineSegment(add_sen);
                    d.record();
                    d.lineStep.clear();
                    d.lineCandidate.clear();
                    return;
                }

                if (p.distance(closest_point) < d.selectionDistance) {
                    d.lineStep.get(0).setB(closest_point);
                    return;
                }

                LineSegment closestLineSegment = new LineSegment();
                closestLineSegment.set(d.getClosestLineSegment(p));

                LineSegment moyori_step_lineSegment = new LineSegment();
                moyori_step_lineSegment.set(d.get_moyori_step_lineSegment(p, 1, d.lineStep.size()));
                if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) >= d.selectionDistance) {//最寄の既存折線が遠い場合
                    if (OritaCalc.determineLineSegmentDistance(p, moyori_step_lineSegment) < d.selectionDistance) {//最寄のstep_senbunが近い場合
                        return;
                    }
                    //最寄のstep_senbunが遠い場合

                    d.lineStep.clear();
                    d.lineCandidate.clear();
                    return;
                }

                if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.selectionDistance) {//最寄の既存折線が近い場合
                    LineSegment s = new LineSegment();
                    s.set(closestLineSegment);
                    s.setColor(LineColor.GREEN_6);
                    d.lineStepAdd(s);
                    Point kousa_point = new Point();
                    kousa_point.set(OritaCalc.findIntersection(d.lineStep.get(0), d.lineStep.get(1)));
                    LineSegment add_sen = new LineSegment(kousa_point, d.lineStep.get(0).getA(), d.lineColor);
                    if (add_sen.determineLength() > 0.00000001) {//最寄の既存折線が有効の場合
                        d.addLineSegment(add_sen);
                        d.record();
                        d.lineStep.clear();
                        d.lineCandidate.clear();
                        return;
                    }
                    //最寄の既存折線が無効の場合
                    closest_point = d.getClosestPoint(p);
                    if (p.distance(closest_point) < d.selectionDistance) {
                        d.lineStep.get(0).setB(closest_point);
                        return;
                    }
                    //最寄のstep_senbunが近い場合
                    if (OritaCalc.determineLineSegmentDistance(p, moyori_step_lineSegment) < d.selectionDistance) {
                        return;
                    }
                    //最寄のstep_senbunが遠い場合
                    d.lineStep.clear();
                    d.lineCandidate.clear();
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
