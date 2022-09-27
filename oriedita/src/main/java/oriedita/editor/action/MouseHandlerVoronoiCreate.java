package oriedita.editor.action;

import org.tinylog.Logger;
import oriedita.editor.canvas.FoldLineAdditionalInputMode;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class MouseHandlerVoronoiCreate extends BaseMouseHandler {
    public List<LineSegmentVoronoi> voronoiLineSet = new ArrayList<>();
    List<LineSegmentVoronoi> lineSegment_voronoi_onePoint = new ArrayList<>(); //Line segment around one point in Voronoi diagram

    @Inject
    public MouseHandlerVoronoiCreate() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.VORONOI_CREATE_62;
    }

    @Override
    public EnumSet<Feature> getSubscribedFeatures() {
        return EnumSet.of(Feature.BUTTON_1, Feature.BUTTON_3);
    }

    //Function to operate the mouse (mouseMode == 62 Voronoi when the mouse is moved)
    public void mouseMoved(Point p0) {
        if (d.getGridInputAssist()) {
            LineSegment candidate = new LineSegment();
            candidate.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);

            Point p = new Point();
            p.set(d.getCamera().TV2object(p0));

            Point closest_point = d.getClosestPoint(p);
            if (p.distance(closest_point) < d.getSelectionDistance()) {
                candidate.set(closest_point, closest_point);
            } else {
                candidate.set(p, p);
            }

            if (d.getI_foldLine_additional() == FoldLineAdditionalInputMode.POLY_LINE_0) {
                candidate.setColor(d.getLineColor());
            }
            if (d.getI_foldLine_additional() == FoldLineAdditionalInputMode.AUX_LINE_1) {
                candidate.setColor(d.getAuxLineColor());
            }

            d.getLineCandidate().clear();
            d.getLineCandidate().add(candidate);

        }
    }

    //マウス操作(mouseMode==62ボロノイ　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.getCamera().TV2object(p0));

        //Arranged i_line_step_size to be only the conventional Voronoi mother point (yet, we have not decided whether to add the point p as line_step to the Voronoi mother point)
        d.getLineStep().clear();
        d.getLineStep().addAll(s_step_no_1_top_continue_no_point_no_number());

        //Find the point-like line segment s_temp consisting of the closest points of p newly added at both ends (if there is no nearest point, both ends of s_temp are p)
        LineSegment s_temp = new LineSegment();
        Point closest_point = d.getClosestPoint(p);
        if (p.distance(closest_point) < d.getSelectionDistance()) {
            s_temp.set(closest_point, closest_point);
            s_temp.setColor(LineColor.MAGENTA_5);
        } else {
            s_temp.set(p, p);
            s_temp.setColor(LineColor.MAGENTA_5);
        }


        //Confirm that the newly added p does not overlap with the previously added Ten
        int i_mouse_modeA_62_point_overlapping = -1;

        List<LineSegment> lineStep = d.getLineStep();
        for (int i = 0; i < lineStep.size(); i++) {
            LineSegment s = lineStep.get(i);
            if (OritaCalc.distance(s.getA(), s_temp.getA()) <= d.getSelectionDistance()) {
                i_mouse_modeA_62_point_overlapping = i;
            }
        }

        //Confirm that the newly added p does not overlap with the previously added Point.

        if (i_mouse_modeA_62_point_overlapping == -1) {

            d.lineStepAdd(s_temp);
            s_temp.setActive(LineSegment.ActiveState.INACTIVE_0);
            //(ここでやっと、点pをs_stepとしてボロノイ母点に加えると決まった)

            //今までのボロノイ図を元に、１個の新しいボロノイ母点を加えたボロノイ図を作る--------------------------------------

            //voronoi_01();//低速、エラーはほとんどないはず
            voronoi_02();//Fast, maybe there are still errors
        } else {//Removed Voronoi mother points with order i_mouse_modeA_62_point_overlapping
            //順番がi_mouse_modeA_62_ten_kasanariのボロノイ母点と順番が最後(=i_egaki_dankai)のボロノイ母点を入れ替える
            //line_step[i]の入れ替え
            LineSegment S_replace = new LineSegment();
            S_replace.set(d.getLineStep().get(i_mouse_modeA_62_point_overlapping));
            d.getLineStep().get(i_mouse_modeA_62_point_overlapping).set(d.getLineStep().get(d.getLineStep().size() - 1));
            d.getLineStep().get(d.getLineStep().size() - 1).set(S_replace);


            for (LineSegmentVoronoi lsv : voronoiLineSet) {
                //Swapping the voronoiA of the line segment in voronoiLineSet
                if (lsv.getVoronoiA() == i_mouse_modeA_62_point_overlapping) {
                    lsv.setVoronoiA(d.getLineStep().size() - 1);
                } else if (lsv.getVoronoiA() == d.getLineStep().size() - 1) {
                    lsv.setVoronoiA(i_mouse_modeA_62_point_overlapping);
                }

                //Replacing the voronoiB of the line segment in voronoiLineSet
                if (lsv.getVoronoiB() == i_mouse_modeA_62_point_overlapping) {
                    lsv.setVoronoiB(d.getLineStep().size() - 1);
                } else if (lsv.getVoronoiB() == d.getLineStep().size() - 1) {
                    lsv.setVoronoiB(i_mouse_modeA_62_point_overlapping);
                }
            }

            //Deleted the Voronoi mother point of the last order (= i_line_step_size)
            d.getLineStep().remove(d.getLineStep().size() - 1);

            List<LineSegmentVoronoi> ori_v_temp2 = new ArrayList<>();

            //Deselect all voronoiLineSet line segments first
            for (LineSegmentVoronoi lsv : voronoiLineSet) {
                lsv.setSelected(0);
            }

            //i_egaki_dankai+1のボロノイ母点からのボロノイ線分を選択状態にする
            for (LineSegmentVoronoi lsv : voronoiLineSet) {
                if (lsv.getVoronoiA() == d.getLineStep().size()) {//The two Voronoi vertices of the Voronoi line segment are recorded in voronoiA and voronoiB.
                    lsv.setSelected(2);
                    for (LineSegmentVoronoi lsv2 : voronoiLineSet) {
                        if (lsv.getVoronoiB() == lsv2.getVoronoiB()) {
                            lsv2.setSelected(2);
                        }
                        if (lsv.getVoronoiB() == lsv2.getVoronoiA()) {
                            lsv2.setSelected(2);
                        }
                    }


                    //削除されるi_egaki_dankai+1番目のボロノイ母点と組になる、もう一つのボロノイ母点を取り囲むボロノイ線分のアレイリストを得る。
                    Senb_boro_1p_motome(lsv.getVoronoiB());

                    for (LineSegmentVoronoi add_S : lineSegment_voronoi_onePoint) {
                        //Pre-check whether to add add_S to ori_v_temp
                        boolean i_tuika = true;//1なら追加する。0なら追加しない。

                        for (LineSegmentVoronoi add_S2 : ori_v_temp2) {
                            if ((add_S.getVoronoiB() == add_S2.getVoronoiB()) && (add_S.getVoronoiA() == add_S2.getVoronoiA())) {
                                i_tuika = false;
                            }
                            if ((add_S.getVoronoiB() == add_S2.getVoronoiA()) && (add_S.getVoronoiA() == add_S2.getVoronoiB())) {
                                i_tuika = false;
                            }
                        }
                        //ori_v_tempにadd_Sを追加するかどうかの事前チェックはここまで

                        if (i_tuika) {
                            ori_v_temp2.add(add_S.clone());
                        }
                    }
                } else if (lsv.getVoronoiB() == d.getLineStep().size()) {//The two Voronoi vertices of the Voronoi line segment are recorded in iactive and color.
                    lsv.setSelected(2);
                    for (LineSegmentVoronoi lsv2 : voronoiLineSet) {
                        if (lsv.getVoronoiA() == lsv2.getVoronoiB()) {
                            lsv2.setSelected(2);
                        }
                        if (lsv.getVoronoiA() == lsv2.getVoronoiA()) {
                            lsv2.setSelected(2);
                        }
                    }

                    //削除されるi_egaki_dankai+1番目のボロノイ母点と組になる、もう一つのボロノイ母点を取り囲むボロノイ線分のアレイリストを得る。
                    Senb_boro_1p_motome(lsv.getVoronoiA());

                    for (LineSegmentVoronoi add_S : lineSegment_voronoi_onePoint) {
                        //ori_v_tempにadd_Sを追加するかどうかの事前チェック
                        boolean i_tuika = true;//1なら追加する。0なら追加しない。
                        for (LineSegmentVoronoi add_S2 : ori_v_temp2) {
                            if ((add_S.getVoronoiB() == add_S2.getVoronoiB()) && (add_S.getVoronoiA() == add_S2.getVoronoiA())) {
                                i_tuika = false;
                            }
                            if ((add_S.getVoronoiB() == add_S2.getVoronoiA()) && (add_S.getVoronoiA() == add_S2.getVoronoiB())) {
                                i_tuika = false;
                            }
                        }
                        //This is the end of the pre-check whether to add add_S to ori_v_temp

                        if (i_tuika) {
                            ori_v_temp2.add(add_S.clone());
                        }
                    }
                }
            }
            //選択状態のものを削除

            voronoiLineSet = voronoiLineSet.stream().filter(s -> s.getSelected() != 2).collect(Collectors.toList());

            for (LineSegmentVoronoi s_t : ori_v_temp2) {
                voronoiLineSet.add(s_t.clone());
            }
        }


        //ボロノイ図も表示するようにs_stepの後にボロノイ図の線を入れる

        for (LineSegmentVoronoi lsv : voronoiLineSet) {
            LineSegment s = new LineSegment();
            s.set(lsv);
            s.setActive(LineSegment.ActiveState.INACTIVE_0);
            s.setColor(LineColor.MAGENTA_5);
            d.getLineStep().add(s);
        }
    }

    List<LineSegment> s_step_no_1_top_continue_no_point_no_number() {//line_step [i] returns the number of Point (length 0) from the beginning. Returns 0 if there are no dots
        List<LineSegment> lineSegments = new ArrayList<>();
        for (LineSegment s : d.getLineStep()) {
            if (Epsilon.high.gt0(s.determineLength())) {
                break;
            }
            lineSegments.add(s);
        }
        return lineSegments;
    }

    public void voronoi_02() {
        //i=1からi_egaki_dankaiまでのs_step[i]と、i_egaki_dankai-1までのボロノイ図からi_egaki_dankaiのボロノイ図を作成

        //i_egaki_dankai番目のボロノイ頂点を取り囲むボロノイ線分のアレイリストを得る。
        Senb_boro_1p_motome(d.getLineStep().size() - 1);

        //20181109ここでori_v.の既存のボロノイ線分の整理が必要

        //ori_vの線分を最初に全て非選択にする
        voronoiLineSet.forEach(s -> s.setSelected(0));

        //
        LineSegmentVoronoi s_begin = new LineSegmentVoronoi();
        LineSegmentVoronoi s_end = new LineSegmentVoronoi();

        for (int ia = 0; ia < lineSegment_voronoi_onePoint.size() - 1; ia++) {
            for (int ib = ia + 1; ib < lineSegment_voronoi_onePoint.size(); ib++) {

                s_begin.set(lineSegment_voronoi_onePoint.get(ia));
                s_end.set(lineSegment_voronoi_onePoint.get(ib));

                StraightLine t_begin = new StraightLine(s_begin);

                int i_begin = s_begin.getVoronoiA();//In this case, voronoiA contains the number of the existing Voronoi mother point when the Voronoi line segment is added.
                int i_end = s_end.getVoronoiA();//In this case, voronoiA contains the number of the existing Voronoi mother point when the Voronoi line segment is added.

                if (i_begin > i_end) {
                    int i_temp = i_begin;
                    i_begin = i_end;
                    i_end = i_temp;
                }

                //The surrounding Voronoi line segment created by adding a new Voronoi matrix is being sought. The polygon of this Voronoi line segment is called a new cell.
                // Before adding a new cell to voronoiLineSet, process so that there is no existing line segment of voronoiLineSet that is inside the new cell.

                //20181109ここでori_v.の既存のボロノイ線分(iactive()が必ずicolorより小さくなっている)を探す
                for (LineSegmentVoronoi s_kizon : voronoiLineSet) {
                    int i_kizon_syou = s_kizon.getVoronoiA();
                    int i_kizon_dai = s_kizon.getVoronoiB();

                    if (i_kizon_syou > i_kizon_dai) {
                        i_kizon_dai = s_kizon.getVoronoiA();
                        i_kizon_syou = s_kizon.getVoronoiB();
                    }

                    if (i_kizon_syou == i_begin) {
                        if (i_kizon_dai == i_end) {

//20181110ここポイント
//
//	-1		0		1
//-1 	何もせず	何もせず	交点まで縮小
// 0	何もせず	有り得ない	削除
// 1	交点まで縮小	削除		削除
//

                            Point kouten = new Point();
                            kouten.set(OritaCalc.findIntersection(s_begin, s_kizon));

                            Point a = d.getLineStep().get(d.getLineStep().size() - 1).getA();
                            if ((t_begin.sameSide(a, s_kizon.getA()) >= 0) && (t_begin.sameSide(a, s_kizon.getB()) >= 0)) {
                                s_kizon.setSelected(2);
                            }

                            if ((t_begin.sameSide(a, s_kizon.getA()) == -1) && (t_begin.sameSide(a, s_kizon.getB()) == 1)) {
                                s_kizon.setB(kouten);
                            }

                            if ((t_begin.sameSide(a, s_kizon.getA()) == 1) && (t_begin.sameSide(a, s_kizon.getB()) == -1)) {
                                s_kizon.setA(kouten);
                            }
                        }
                    }
                }
            }
        }

        //選択状態のものを削除
        voronoiLineSet = voronoiLineSet.stream().filter(s -> s.getSelected() != 2).collect(Collectors.toList());

        //Add the line segment of Senb_boro_1p to the end of senbun of voronoiLineSet
        for (LineSegmentVoronoi lineSegment : lineSegment_voronoi_onePoint) {
            voronoiLineSet.add(lineSegment.clone());
        }
    }

    public void Senb_boro_1p_motome(int center_point_count) {//It can be used when line_step contains only Voronoi mother points. Get Senb_boro_1p as a set of Voronoi line segments around center_point_count
        //i_egaki_dankai Obtain an array list of Voronoi line segments surrounding the third Voronoi vertex. // i_egaki_dankai The third Voronoi apex is line_step [i_egaki_dankai] .geta ()
        lineSegment_voronoi_onePoint.clear();

        for (int i_e_d = 0; i_e_d < d.getLineStep().size(); i_e_d++) {
            if (i_e_d != center_point_count) {
                //Find the line segment to add
                LineSegmentVoronoi add_lineSegment = new LineSegmentVoronoi();

                add_lineSegment.set(OritaCalc.bisection(d.getLineStep().get(i_e_d).getA(), d.getLineStep().get(center_point_count).getA(), 1000.0));

                Logger.info("center_point_count= " + center_point_count + " ,i_e_d= " + i_e_d);

                if (i_e_d < center_point_count) {
                    add_lineSegment.setVoronoiA(i_e_d);
                    add_lineSegment.setVoronoiB(center_point_count);//Record the two Voronoi vertices of the Voronoi line segment in iactive and color
                } else {
                    add_lineSegment.setVoronoiA(center_point_count);
                    add_lineSegment.setVoronoiB(i_e_d);//Record the two Voronoi vertices of the Voronoi line segment in iactive and color
                }
                voronoi_02_01(center_point_count, add_lineSegment);
            }
        }
    }


    public void voronoi_02_01(int center_point_count, LineSegmentVoronoi add_lineSegment) {
        //i_egaki_dankai番目のボロノイ頂点は　　line_step[i_egaki_dankai].geta()　　　

        //Organize the line segments to be added
        StraightLine add_straightLine = new StraightLine(add_lineSegment);

        int i_saisyo = lineSegment_voronoi_onePoint.size() - 1;
        for (int i = i_saisyo; i >= 0; i--) {
            //Organize existing line segments
            LineSegmentVoronoi existing_lineSegment = new LineSegmentVoronoi();
            existing_lineSegment.set(lineSegment_voronoi_onePoint.get(i));
            StraightLine existing_straightLine = new StraightLine(existing_lineSegment);

            //Fight the line segment to be added with the existing line segment

            OritaCalc.ParallelJudgement parallel = OritaCalc.isLineSegmentParallel(add_straightLine, existing_straightLine, Epsilon.UNKNOWN_1EN4);//0 = not parallel, 1 = parallel and 2 straight lines do not match, 2 = parallel and 2 straight lines match

            Point a = d.getLineStep().get(center_point_count).getA();
            if (parallel == OritaCalc.ParallelJudgement.NOT_PARALLEL) {//When the line segment to be added and the existing line segment are non-parallel
                Point intersection = new Point();
                intersection.set(OritaCalc.findIntersection(add_straightLine, existing_straightLine));

                if ((add_straightLine.sameSide(a, existing_lineSegment.getA()) <= 0) &&
                        (add_straightLine.sameSide(a, existing_lineSegment.getB()) <= 0)) {
                    lineSegment_voronoi_onePoint.remove(i);
                } else if ((add_straightLine.sameSide(a, existing_lineSegment.getA()) == 1) &&
                        (add_straightLine.sameSide(a, existing_lineSegment.getB()) == -1)) {
                    existing_lineSegment.set(existing_lineSegment.getA(), intersection);
                    if (existing_lineSegment.determineLength() < Epsilon.UNKNOWN_1EN7) {
                        lineSegment_voronoi_onePoint.remove(i);
                    } else {
                        lineSegment_voronoi_onePoint.set(i, existing_lineSegment);
                    }
                } else if ((add_straightLine.sameSide(a, existing_lineSegment.getA()) == -1) &&
                        (add_straightLine.sameSide(a, existing_lineSegment.getB()) == 1)) {
                    existing_lineSegment.set(intersection, existing_lineSegment.getB());
                    if (existing_lineSegment.determineLength() < Epsilon.UNKNOWN_1EN7) {
                        lineSegment_voronoi_onePoint.remove(i);
                    } else {
                        lineSegment_voronoi_onePoint.set(i, existing_lineSegment);
                    }
                }

                if ((existing_straightLine.sameSide(a, add_lineSegment.getA()) <= 0) &&
                        (existing_straightLine.sameSide(a, add_lineSegment.getB()) <= 0)) {
                    return;
                } else if ((existing_straightLine.sameSide(a, add_lineSegment.getA()) == 1) &&
                        (existing_straightLine.sameSide(a, add_lineSegment.getB()) == -1)) {
                    add_lineSegment.set(add_lineSegment.getA(), intersection);
                    if (add_lineSegment.determineLength() < Epsilon.UNKNOWN_1EN7) {
                        return;
                    }
                } else if ((existing_straightLine.sameSide(a, add_lineSegment.getA()) == -1) &&
                        (existing_straightLine.sameSide(a, add_lineSegment.getB()) == 1)) {
                    add_lineSegment.set(intersection, add_lineSegment.getB());
                    if (add_lineSegment.determineLength() < Epsilon.UNKNOWN_1EN7) {
                        return;
                    }
                }


            } else if (parallel == OritaCalc.ParallelJudgement.PARALLEL_NOT_EQUAL) {//When the line segment to be added and the existing line segment are parallel and the two straight lines do not match
                if (add_straightLine.sameSide(a, existing_lineSegment.getA()) == -1) {
                    lineSegment_voronoi_onePoint.remove(i);
                } else if (existing_straightLine.sameSide(a, add_lineSegment.getA()) == -1) {
                    return;
                }
            } else if (parallel == OritaCalc.ParallelJudgement.PARALLEL_EQUAL) {//When the line segment to be added and the existing line segment are parallel and the two straight lines match
                return;
            }
        }

        lineSegment_voronoi_onePoint.add(add_lineSegment);
    }

    // -----------------------------------------------------------------------------
    //マウス操作(mouseMode==62ボロノイ　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
    }

    // -----------------------------------------------------------------------------
    //マウス操作(mouseMode==62ボロノイ　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
    }
}
