package origami_editor.editor.drawing_worker;

import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

import java.util.ArrayList;
import java.util.List;

public class MouseHandlerVoronoiCreate extends BaseMouseHandler {
    int i_mouse_modeA_62_point_overlapping;

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.VORONOI_CREATE_62;
    }

    //Function to operate the mouse (mouseMode == 62 Voronoi when the mouse is moved)
    public void mouseMoved(Point p0) {
        if (d.gridInputAssist) {
            LineSegment candidate = new LineSegment();
            candidate.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);

            Point p = new Point();
            p.set(d.camera.TV2object(p0));

            Point closest_point = d.getClosestPoint(p);
            if (p.distance(closest_point) < d.selectionDistance) {
                candidate.set(closest_point, closest_point);
            } else {
                candidate.set(p, p);
            }

            if (d.i_foldLine_additional == FoldLineAdditionalInputMode.POLY_LINE_0) {
                candidate.setColor(d.lineColor);
            }
            if (d.i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LINE_1) {
                candidate.setColor(d.auxLineColor);
            }

            d.lineCandidate.clear();
            d.lineCandidate.add(candidate);

        }
    }

    //マウス操作(mouseMode==62ボロノイ　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        //Arranged i_line_step_size to be only the conventional Voronoi mother point (yet, we have not decided whether to add the point p as line_step to the Voronoi mother point)
        d.lineStep = s_step_no_1_top_continue_no_point_no_number();//Tenの数

        //Find the point-like line segment s_temp consisting of the closest points of p newly added at both ends (if there is no nearest point, both ends of s_temp are p)
        LineSegment s_temp = new LineSegment();
        Point closest_point = d.getClosestPoint(p);
        if (p.distance(closest_point) < d.selectionDistance) {
            s_temp.set(closest_point, closest_point);
            s_temp.setColor(LineColor.MAGENTA_5);
        } else {
            s_temp.set(p, p);
            s_temp.setColor(LineColor.MAGENTA_5);
        }


        //Confirm that the newly added p does not overlap with the previously added Ten
        int i_mouse_modeA_62_point_overlapping = -1;

        List<LineSegment> lineStep = d.lineStep;
        for (int i = 0; i < lineStep.size(); i++) {
            LineSegment s = lineStep.get(i);
            if (OritaCalc.distance(s.getA(), s_temp.getA()) <= d.selectionDistance) {
                i_mouse_modeA_62_point_overlapping = i;
            }
        }

        //Confirm that the newly added p does not overlap with the previously added Point.

        if (i_mouse_modeA_62_point_overlapping == -1) {

            d.lineStepAdd(s_temp);
            s_temp.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
            //(ここでやっと、点pをs_stepとしてボロノイ母点に加えると決まった)

            //今までのボロノイ図を元に、１個の新しいボロノイ母点を加えたボロノイ図を作る--------------------------------------

            //voronoi_01();//低速、エラーはほとんどないはず
            d.voronoi_02();//Fast, maybe there are still errors
        } else {//Removed Voronoi mother points with order i_mouse_modeA_62_point_overlapping
            //順番がi_mouse_modeA_62_ten_kasanariのボロノイ母点と順番が最後(=i_egaki_dankai)のボロノイ母点を入れ替える
            //line_step[i]の入れ替え
            LineSegment S_replace = new LineSegment();
            S_replace.set(d.lineStep.get(i_mouse_modeA_62_point_overlapping));
            d.lineStep.get(i_mouse_modeA_62_point_overlapping).set(d.lineStep.get(d.lineStep.size() - 1));
            d.lineStep.get(d.lineStep.size() - 1).set(S_replace);


            for (int j = 1; j <= d.voronoiLineSet.getTotal(); j++) {
                //Swapping the voronoiA of the line segment in voronoiLineSet
                if (d.voronoiLineSet.getVoronoiA(j) == i_mouse_modeA_62_point_overlapping) {
                    d.voronoiLineSet.setVoronoiA(j, d.lineStep.size() - 1);
                } else if (d.voronoiLineSet.getVoronoiA(j) == d.lineStep.size() - 1) {
                    d.voronoiLineSet.setVoronoiA(j, i_mouse_modeA_62_point_overlapping);
                }

                //Replacing the voronoiB of the line segment in voronoiLineSet
                if (d.voronoiLineSet.getVoronoiB(j) == i_mouse_modeA_62_point_overlapping) {
                    d.voronoiLineSet.setVoronoiB(j, d.lineStep.size() - 1);
                } else if (d.voronoiLineSet.getVoronoiB(j) == d.lineStep.size() - 1) {
                    d.voronoiLineSet.setVoronoiB(j, i_mouse_modeA_62_point_overlapping);
                }
            }

            //Deleted the Voronoi mother point of the last order (= i_line_step_size)
            d.lineStep.remove(d.lineStep.size() - 1);

            FoldLineSet ori_v_temp = new FoldLineSet();    //修正用のボロノイ図の線を格納する

            //Deselect all voronoiLineSet line segments first
            d.voronoiLineSet.unselect_all();

            //i_egaki_dankai+1のボロノイ母点からのボロノイ線分を選択状態にする
            LineSegment s_tem = new LineSegment();
            LineSegment s_tem2 = new LineSegment();
            for (int j = 1; j <= d.voronoiLineSet.getTotal(); j++) {
                s_tem.set(d.voronoiLineSet.get(j));//s_temとしてボロノイ母点からのボロノイ線分か判定
                if (s_tem.getVoronoiA() == d.lineStep.size()) {//The two Voronoi vertices of the Voronoi line segment are recorded in voronoiA and voronoiB.
                    d.voronoiLineSet.select(j);
                    for (int h = 1; h <= d.voronoiLineSet.getTotal(); h++) {
                        s_tem2.set(d.voronoiLineSet.get(h));
                        if (s_tem.getVoronoiB() == s_tem2.getVoronoiB()) {
                            d.voronoiLineSet.select(h);
                        }
                        if (s_tem.getVoronoiB() == s_tem2.getVoronoiA()) {
                            d.voronoiLineSet.select(h);
                        }
                    }


                    //削除されるi_egaki_dankai+1番目のボロノイ母点と組になる、もう一つのボロノイ母点を取り囲むボロノイ線分のアレイリストを得る。
                    d.Senb_boro_1p_motome(s_tem.getVoronoiB());

                    for (LineSegment lineSegment : d.lineSegment_voronoi_onePoint) {
                        LineSegment add_S = new LineSegment();
                        add_S.set(lineSegment);
                        LineSegment add_S2 = new LineSegment();


                        //Pre-check whether to add add_S to ori_v_temp
                        boolean i_tuika = true;//1なら追加する。0なら追加しない。
                        for (int h = 1; h <= ori_v_temp.getTotal(); h++) {
                            add_S2.set(ori_v_temp.get(h));
                            if ((add_S.getVoronoiB() == add_S2.getVoronoiB()) && (add_S.getVoronoiA() == add_S2.getVoronoiA())) {
                                i_tuika = false;
                            }
                            if ((add_S.getVoronoiB() == add_S2.getVoronoiA()) && (add_S.getVoronoiA() == add_S2.getVoronoiB())) {
                                i_tuika = false;
                            }
                        }
                        //ori_v_tempにadd_Sを追加するかどうかの事前チェックはここまで

                        if (i_tuika) {
                            ori_v_temp.addLine(lineSegment);
                        }
                    }
                } else if (s_tem.getVoronoiB() == d.lineStep.size()) {//The two Voronoi vertices of the Voronoi line segment are recorded in iactive and color.
                    d.voronoiLineSet.select(j);
                    for (int h = 1; h <= d.voronoiLineSet.getTotal(); h++) {
                        s_tem2.set(d.voronoiLineSet.get(h));
                        if (s_tem.getVoronoiA() == s_tem2.getVoronoiB()) {
                            d.voronoiLineSet.select(h);
                        }
                        if (s_tem.getVoronoiA() == s_tem2.getVoronoiA()) {
                            d.voronoiLineSet.select(h);
                        }
                    }

                    //削除されるi_egaki_dankai+1番目のボロノイ母点と組になる、もう一つのボロノイ母点を取り囲むボロノイ線分のアレイリストを得る。
                    d.Senb_boro_1p_motome(s_tem.getVoronoiA());

                    for (LineSegment lineSegment : d.lineSegment_voronoi_onePoint) {
                        LineSegment add_S = new LineSegment();
                        add_S.set(lineSegment);
                        LineSegment add_S2 = new LineSegment();

                        //ori_v_tempにadd_Sを追加するかどうかの事前チェック
                        boolean i_tuika = true;//1なら追加する。0なら追加しない。
                        for (int h = 1; h <= ori_v_temp.getTotal(); h++) {
                            add_S2.set(ori_v_temp.get(h));
                            if ((add_S.getVoronoiB() == add_S2.getVoronoiB()) && (add_S.getVoronoiA() == add_S2.getVoronoiA())) {
                                i_tuika = false;
                            }
                            if ((add_S.getVoronoiB() == add_S2.getVoronoiA()) && (add_S.getVoronoiA() == add_S2.getVoronoiB())) {
                                i_tuika = false;
                            }
                        }
                        //This is the end of the pre-check whether to add add_S to ori_v_temp

                        if (i_tuika) {
                            ori_v_temp.addLine(lineSegment);
                        }
                    }
                }
            }
            //選択状態のものを削除
            d.voronoiLineSet.delSelectedLineSegmentFast();
            d.voronoiLineSet.del_V_all(); //You may not need this line

            for (int j = 1; j <= ori_v_temp.getTotal(); j++) {
                LineSegment s_t = new LineSegment();
                s_t.set(ori_v_temp.get(j));
                d.voronoiLineSet.addLine(s_t);
            }

            d.voronoiLineSet.del_V_all();

        }


        //ボロノイ図も表示するようにs_stepの後にボロノイ図の線を入れる

        int imax = d.voronoiLineSet.getTotal();
        if (imax > 1020) {
            imax = 1020;
        }


        for (int i = 1; i <= imax; i++) {
            LineSegment s = new LineSegment();
            s.set(d.voronoiLineSet.get(i));
            s.setActive(LineSegment.ActiveState.INACTIVE_0);
            s.setColor(LineColor.MAGENTA_5);
            d.lineStepAdd(s);
        }


    }

    List<LineSegment> s_step_no_1_top_continue_no_point_no_number() {//line_step [i] returns the number of Point (length 0) from the beginning. Returns 0 if there are no dots
        List<LineSegment> lineSegments = new ArrayList<>();
        for (LineSegment s : d.lineStep) {
            if (s.determineLength() > 0.00000001) {
                break;
            }
            lineSegments.add(s);
        }
        return lineSegments;
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
