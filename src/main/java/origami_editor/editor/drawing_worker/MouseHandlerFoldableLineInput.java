package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;
import origami_editor.sortingbox.SortingBox;
import origami_editor.sortingbox.WeightedValue;

public class MouseHandlerFoldableLineInput extends BaseMouseHandler{
    private final MouseHandlerPolygonSetNoCorners mouseHandlerPolygonSetNoCorners;
    DrawingWorker.FourPointStep i_step_for_copy_4p = DrawingWorker.FourPointStep.STEP_0;

    public MouseHandlerFoldableLineInput(DrawingWorker d) {
        super(d);
        mouseHandlerPolygonSetNoCorners = new MouseHandlerPolygonSetNoCorners(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.FOLDABLE_LINE_INPUT_39;
    }

    //マウス操作(マウスを動かしたとき)を行う関数    //System.out.println("_");
    public void mouseMoved(Point p0) {
        if (d.i_drawing_stage == 0) {
            i_step_for_copy_4p = DrawingWorker.FourPointStep.STEP_0;
        }
        if (d.gridInputAssist) {
            d.i_candidate_stage = 0;

            Point p = new Point();
            p.set(d.camera.TV2object(p0));

            if (d.i_drawing_stage == 0) {
                i_step_for_copy_4p = DrawingWorker.FourPointStep.STEP_0;
            }
            System.out.println("i_egaki_dankai= " + d.i_drawing_stage + "  ;   i_step_for_copy_4p= " + i_step_for_copy_4p);

            switch (i_step_for_copy_4p) {
                case STEP_0:
                    mouseHandlerPolygonSetNoCorners.mouseMoved(p0);
                    break;
                case STEP_1:
                    d.closest_lineSegment.set(d.get_moyori_step_lineSegment(p, 1, d.i_drawing_stage));
                    if ((d.i_drawing_stage >= 2) && (OritaCalc.distance_lineSegment(p, d.closest_lineSegment) < d.selectionDistance)) {
                        d.i_candidate_stage = 1;
                        d.line_candidate[1].set(d.closest_lineSegment);//line_candidate[1].setcolor(2);
                        return;
                    }

                    d.closest_point.set(d.getClosestPoint(p));
                    if (p.distance(d.closest_point) < d.selectionDistance) {
                        d.line_candidate[1].set(d.closest_point, d.closest_point);
                        d.line_candidate[1].setColor(d.lineColor);
                        d.i_candidate_stage = 1;
                        return;
                    }
                    return;
                case STEP_2: //i_step_for_copy_4p==2であれば、以下でs_step[1]を入力折線を確定する
                    d.closest_point.set(d.getClosestPoint(p));

                    if (d.closest_point.distance(d.line_step[1].getA()) < 0.00000001) {
                        d.i_candidate_stage = 1;
                        d.line_candidate[1].set(d.closest_point, d.closest_point);
                        d.line_candidate[1].setColor(d.lineColor);
                        System.out.println("i_step_for39_2_   1");

                        return;
                    }

                    if ((p.distance(d.line_step[1].getB()) < d.selectionDistance) && (p.distance(d.line_step[1].getB()) <= p.distance(d.closest_point))) {
                        d.i_candidate_stage = 1;
                        d.line_candidate[1].set(d.line_step[1].getB(), d.line_step[1].getB());
                        d.line_candidate[1].setColor(d.lineColor);
                        System.out.println("i_step_for39_2_   2");

                        return;
                    }

                    if (p.distance(d.closest_point) < d.selectionDistance) {
                        d.i_candidate_stage = 1;
                        d.line_candidate[1].set(d.closest_point, d.closest_point);
                        d.line_candidate[1].setColor(d.lineColor);
                        System.out.println("i_step_for39_2_   3");

                        return;
                    }

                    d.closest_lineSegment.set(d.getClosestLineSegment(p));
                    LineSegment moyori_step_lineSegment = new LineSegment();
                    moyori_step_lineSegment.set(d.get_moyori_step_lineSegment(p, 1, d.i_drawing_stage));
                    if (OritaCalc.distance_lineSegment(p, d.closest_lineSegment) >= d.selectionDistance) {//最寄の既存折線が遠い場合
                        if (OritaCalc.distance_lineSegment(p, moyori_step_lineSegment) < d.selectionDistance) {//最寄のstep_senbunが近い場合
                            return;
                        }
                        //最寄のstep_senbunが遠い場合
                        System.out.println("i_step_for39_2_   4");

                        return;
                    }

                    if (OritaCalc.distance_lineSegment(p, d.closest_lineSegment) < d.selectionDistance) {//最寄の既存折線が近い場合
                        d.i_candidate_stage = 1;
                        d.line_candidate[1].set(d.closest_lineSegment);
                        d.line_candidate[1].setColor(d.lineColor);

                        System.out.println("i_step_for39_2_   5");
                        return;
                    }
                    return;
            }

            return;
        }
    }

    //マウス操作(ボタンを押したとき)時の作業--------------
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        if (d.i_drawing_stage == 0) {
            i_step_for_copy_4p = DrawingWorker.FourPointStep.STEP_0;
        }

        if (i_step_for_copy_4p == DrawingWorker.FourPointStep.STEP_0) {
            double decision_distance = 0.000001;

            //任意の点が与えられたとき、端点もしくは格子点で最も近い点を得る
            d.closest_point.set(d.getClosestPoint(p));

            if (p.distance(d.closest_point) < d.selectionDistance) {
                //moyori_tenを端点とする折線をNarabebakoに入れる
                SortingBox<LineSegment> nbox = new SortingBox<>();
                for (int i = 1; i <= d.foldLineSet.getTotal(); i++) {
                    LineSegment s = d.foldLineSet.get(i);
                    if (s.getColor().isFoldingLine()) {
                        if (d.closest_point.distance(s.getA()) < decision_distance) {
                            nbox.container_i_smallest_first(new WeightedValue<>(s, OritaCalc.angle(s.getA(), s.getB())));
                        } else if (d.closest_point.distance(s.getB()) < decision_distance) {
                            nbox.container_i_smallest_first(new WeightedValue<>(s, OritaCalc.angle(s.getB(), s.getA())));
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

                        if ((kakukagenti / 2.0 > 0.0 + 0.000001) && (kakukagenti / 2.0 < add_kakudo_1 - 0.000001)) {
                            d.i_drawing_stage = d.i_drawing_stage + 1;

                            //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d)
                            LineSegment s_kiso = new LineSegment();
                            LineSegment nboxLineSegment = nbox.getValue(i);
                            if (d.closest_point.distance(nboxLineSegment.getA()) < decision_distance) {
                                s_kiso.set(nboxLineSegment.getA(), nboxLineSegment.getB());
                            } else if (d.closest_point.distance(nboxLineSegment.getB()) < decision_distance) {
                                s_kiso.set(nboxLineSegment.getB(), nboxLineSegment.getA());
                            }

                            double s_kiso_length = s_kiso.getLength();

                            d.line_step[d.i_drawing_stage].set(OritaCalc.lineSegment_rotate(s_kiso, kakukagenti / 2.0, d.grid.getGridWidth() / s_kiso_length));
                            d.line_step[d.i_drawing_stage].setColor(LineColor.PURPLE_8);
                            d.line_step[d.i_drawing_stage].setActive(LineSegment.ActiveState.ACTIVE_A_1);
                        }
                    }

                    if (d.i_drawing_stage == 1) {
                        i_step_for_copy_4p = DrawingWorker.FourPointStep.STEP_2;
                    }
                    if (d.i_drawing_stage > 1) {
                        i_step_for_copy_4p = DrawingWorker.FourPointStep.STEP_1;
                    }
                }

                if (d.i_drawing_stage == 0) {//折畳み可能化線がない場合//System.out.println("_");
                    d.i_drawing_stage = 1;
                    i_step_for_copy_4p = DrawingWorker.FourPointStep.STEP_1;
                    d.line_step[1].set(d.closest_point, d.closest_point);
                    d.line_step[1].setColor(LineColor.PURPLE_8);
                    d.line_step[1].setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
                }

            }
            return;
        }

        if (i_step_for_copy_4p == DrawingWorker.FourPointStep.STEP_1) {
            d.closest_lineSegment.set(d.get_moyori_step_lineSegment(p, 1, d.i_drawing_stage));
            if ((d.i_drawing_stage >= 2) && (OritaCalc.distance_lineSegment(p, d.closest_lineSegment) < d.selectionDistance)) {
                i_step_for_copy_4p = DrawingWorker.FourPointStep.STEP_2;
                d.i_drawing_stage = 1;
                d.line_step[1].set(d.closest_lineSegment);
                return;
            }
            d.closest_point.set(d.getClosestPoint(p));
            if (p.distance(d.closest_point) < d.selectionDistance) {
                d.line_step[1].setB(d.closest_point);
                i_step_for_copy_4p = DrawingWorker.FourPointStep.STEP_2;
                d.i_drawing_stage = 1;
                return;
            }
            d.i_drawing_stage = 0;
            d.i_candidate_stage = 0;
            return;
        }


        if (i_step_for_copy_4p == DrawingWorker.FourPointStep.STEP_2) {//i_step_for_copy_4p==2であれば、以下でs_step[1]を入力折線を確定する
            d.closest_point.set(d.getClosestPoint(p));

            if (d.closest_point.distance(d.line_step[1].getA()) < 0.00000001) {
                d.i_drawing_stage = 0;
                d.i_candidate_stage = 0;
                return;
            }

            if ((p.distance(d.line_step[1].getB()) < d.selectionDistance) &&
                    (
                            p.distance(d.line_step[1].getB()) <= p.distance(d.closest_point)
                            //moyori_ten.kyori(line_step[1].getb())<0.00000001
                    )) {
                LineSegment add_sen = new LineSegment(d.line_step[1].getA(), d.line_step[1].getB(), d.lineColor);
                d.addLineSegment(add_sen);
                d.record();
                d.i_drawing_stage = 0;
                d.i_candidate_stage = 0;
                return;
            }

            if (p.distance(d.closest_point) < d.selectionDistance) {
                d.line_step[1].setB(d.closest_point);
                return;
            }


            d.closest_lineSegment.set(d.getClosestLineSegment(p));

            LineSegment moyori_step_lineSegment = new LineSegment();
            moyori_step_lineSegment.set(d.get_moyori_step_lineSegment(p, 1, d.i_drawing_stage));
            if (OritaCalc.distance_lineSegment(p, d.closest_lineSegment) >= d.selectionDistance) {//最寄の既存折線が遠い場合
                if (OritaCalc.distance_lineSegment(p, moyori_step_lineSegment) < d.selectionDistance) {//最寄のstep_senbunが近い場合
                    return;
                }
                //最寄のstep_senbunが遠い場合

                d.i_drawing_stage = 0;
                d.i_candidate_stage = 0;
                return;
            }

            if (OritaCalc.distance_lineSegment(p, d.closest_lineSegment) < d.selectionDistance) {//最寄の既存折線が近い場合
                d.line_step[2].set(d.closest_lineSegment);
                d.line_step[2].setColor(LineColor.GREEN_6);
                Point kousa_point = new Point();
                kousa_point.set(OritaCalc.findIntersection(d.line_step[1], d.line_step[2]));
                LineSegment add_sen = new LineSegment(kousa_point, d.line_step[1].getA(), d.lineColor);
                if (add_sen.getLength() > 0.00000001) {//最寄の既存折線が有効の場合
                    d.addLineSegment(add_sen);
                    d.record();
                    d.i_drawing_stage = 0;
                    d.i_candidate_stage = 0;
                    return;
                }
                //最寄の既存折線が無効の場合
                d.closest_point.set(d.getClosestPoint(p));
                if (p.distance(d.closest_point) < d.selectionDistance) {
                    d.line_step[1].setB(d.closest_point);
                    return;
                }
                //最寄のstep_senbunが近い場合
                if (OritaCalc.distance_lineSegment(p, moyori_step_lineSegment) < d.selectionDistance) {
                    return;
                }
                //最寄のstep_senbunが遠い場合
                d.i_drawing_stage = 0;
                d.i_candidate_stage = 0;
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
