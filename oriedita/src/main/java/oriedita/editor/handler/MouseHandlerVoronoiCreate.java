package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.tinylog.Logger;
import oriedita.editor.action.All_s_step_to_orisenAction;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.StraightLine;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Handles(MouseMode.VORONOI_CREATE_62)
public class MouseHandlerVoronoiCreate extends BaseMouseHandler implements All_s_step_to_orisenAction.VoronoiHandler {

    private List<LineSegmentVoronoi> voronoiLineSet = new ArrayList<>();
    private final List<LineSegmentVoronoi> voronoiLinesAroundNewPoint = new ArrayList<>(); //Line segment around one point in Voronoi diagram
    private final List<Point> seedPoints = new  ArrayList<>();

    @Inject
    public MouseHandlerVoronoiCreate() {
    }

    @Override
    public EnumSet<MouseHandlerSettingGroup>  getSettings() {
        return EnumSet.of(MouseHandlerSettingGroup.APPLY_LINES);
    }

    @Override
    public EnumSet<Feature> getSubscribedFeatures() {
        return EnumSet.of(Feature.BUTTON_1, Feature.BUTTON_3);
    }

    //Function to operate the mouse (mouseMode == 62 Voronoi when the mouse is moved)
    public void mouseMoved(Point p0) {

    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        for (LineSegmentVoronoi ls : this.voronoiLineSet) {
            DrawingUtil.drawLineStep(g2, ls.getLineSegment().withColor(LineColor.MAGENTA_5),
                    camera, settings.getLineWidth());
        }
        for (Point p : seedPoints) {
            DrawingUtil.drawStepVertex(g2, p, LineColor.CYAN_3, camera);
        }
    }

    //マウス操作(mouseMode==62ボロノイ　でボタンを押したとき)時の作業----------------------------------------------------
    @Override
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);

        //Arranged i_line_step_size to be only the conventional Voronoi mother point (yet, we have not decided whether to add the point p as line_step to the Voronoi mother point)


        //Find the point-like line segment s_temp consisting of the closest points of p newly added at both ends (if there is no nearest point, both ends of s_temp are p)
        Point closest_point = d.getClosestPoint(p);
        Point selectedPoint;
        if (p.distance(closest_point) < d.getSelectionDistance()) {
            selectedPoint = closest_point;
        } else {
            selectedPoint = p;
        }


        //Confirm that the newly added p does not overlap with the previously added Ten
        int overlappingSeedPointIndex = -1;

        for (int i = 0; i < seedPoints.size(); i++) {
            Point s = seedPoints.get(i);
            if (OritaCalc.distance(s, selectedPoint) <= d.getSelectionDistance()) {
                overlappingSeedPointIndex = i;
            }
        }

        //Confirm that the newly added p does not overlap with the previously added Point.

        if (overlappingSeedPointIndex == -1) {

            seedPoints.add(selectedPoint);
            //(ここでやっと、点pをs_stepとしてボロノイ母点に加えると決まった)

            //今までのボロノイ図を元に、１個の新しいボロノイ母点を加えたボロノイ図を作る--------------------------------------

            //voronoi_01();//低速、エラーはほとんどないはず
            voronoi_02();//Fast, maybe there are still errors
        } else {//Removed Voronoi mother points with order i_mouse_modeA_62_point_overlapping
            //順番がi_mouse_modeA_62_ten_kasanariのボロノイ母点と順番が最後(=i_egaki_dankai)のボロノイ母点を入れ替える
            //line_step[i]の入れ替え
            Point S_replace = seedPoints.get(overlappingSeedPointIndex);
            seedPoints.set(overlappingSeedPointIndex, seedPoints.get(seedPoints.size() - 1));
            seedPoints.set(seedPoints.size() - 1, S_replace);


            for (LineSegmentVoronoi lsv : voronoiLineSet) {
                //Swapping the voronoiA of the line segment in voronoiLineSet
                if (lsv.getVoronoiA() == overlappingSeedPointIndex) {
                    lsv.setVoronoiA(seedPoints.size() - 1);
                } else if (lsv.getVoronoiA() == seedPoints.size() - 1) {
                    lsv.setVoronoiA(overlappingSeedPointIndex);
                }

                //Replacing the voronoiB of the line segment in voronoiLineSet
                if (lsv.getVoronoiB() == overlappingSeedPointIndex) {
                    lsv.setVoronoiB(seedPoints.size() - 1);
                } else if (lsv.getVoronoiB() == seedPoints.size() - 1) {
                    lsv.setVoronoiB(overlappingSeedPointIndex);
                }
            }

            //Deleted the Voronoi mother point of the last order (= i_line_step_size)
            seedPoints.remove(seedPoints.size() - 1);

            List<LineSegmentVoronoi> ori_v_temp2 = new ArrayList<>();

            //Deselect all voronoiLineSet line segments first
            for (LineSegmentVoronoi lsv : voronoiLineSet) {
                lsv.setSelected(0);
            }

            //i_egaki_dankai+1のボロノイ母点からのボロノイ線分を選択状態にする
            for (LineSegmentVoronoi lsv : voronoiLineSet) {
                if (lsv.getVoronoiA() == seedPoints.size()) {//The two Voronoi vertices of the Voronoi line segment are recorded in voronoiA and voronoiB.
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

                    for (LineSegmentVoronoi add_S : voronoiLinesAroundNewPoint) {
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
                            ori_v_temp2.add(add_S);
                        }
                    }
                } else if (lsv.getVoronoiB() == seedPoints.size()) {//The two Voronoi vertices of the Voronoi line segment are recorded in iactive and color.
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

                    for (LineSegmentVoronoi add_S : voronoiLinesAroundNewPoint) {
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
                            ori_v_temp2.add(add_S);
                        }
                    }
                }
            }
            //選択状態のものを削除

            voronoiLineSet = voronoiLineSet.stream().filter(s -> s.getSelected() != 2).collect(Collectors.toList());

            voronoiLineSet.addAll(ori_v_temp2);
        }


        //ボロノイ図も表示するようにs_stepの後にボロノイ図の線を入れる
    }

    @Override
    public void reset() {
        super.reset();
        voronoiLineSet.clear();
        voronoiLinesAroundNewPoint.clear();
        seedPoints.clear();
    }

    public void voronoi_02() {
        //i=1からi_egaki_dankaiまでのs_step[i]と、i_egaki_dankai-1までのボロノイ図からi_egaki_dankaiのボロノイ図を作成

        //i_egaki_dankai番目のボロノイ頂点を取り囲むボロノイ線分のアレイリストを得る。
        Senb_boro_1p_motome(seedPoints.size() - 1);

        //20181109ここでori_v.の既存のボロノイ線分の整理が必要

        //ori_vの線分を最初に全て非選択にする
        voronoiLineSet.forEach(s -> s.setSelected(0));

        //

        for (int ia = 0; ia < voronoiLinesAroundNewPoint.size() - 1; ia++) {
            for (int ib = ia + 1; ib < voronoiLinesAroundNewPoint.size(); ib++) {

                LineSegmentVoronoi s_begin = new LineSegmentVoronoi(voronoiLinesAroundNewPoint.get(ia));
                LineSegmentVoronoi s_end = new LineSegmentVoronoi(voronoiLinesAroundNewPoint.get(ib));

                StraightLine t_begin = new StraightLine(s_begin.getLineSegment());

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
                int finalI_begin = i_begin;
                int finalI_end = i_end;
                voronoiLineSet = voronoiLineSet.stream().map(s_kizon -> {
                    int i_kizon_syou = s_kizon.getVoronoiA();
                    int i_kizon_dai = s_kizon.getVoronoiB();

                    if (i_kizon_syou > i_kizon_dai) {
                        i_kizon_dai = s_kizon.getVoronoiA();
                        i_kizon_syou = s_kizon.getVoronoiB();
                    }

                    if (i_kizon_syou == finalI_begin) {
                        if (i_kizon_dai == finalI_end) {
                            //20181110ここポイント
                            //
                            //	-1		0		1
                            //-1 	何もせず	何もせず	交点まで縮小
                            // 0	何もせず	有り得ない	削除
                            // 1	交点まで縮小	削除		削除
                            //
                            Point kouten = OritaCalc.findIntersection(s_begin.getLineSegment(), s_kizon.getLineSegment());

                            Point a = seedPoints.get(seedPoints.size() - 1);
                            if ((t_begin.sameSide(a, s_kizon.getA()) >= 0) && (t_begin.sameSide(a, s_kizon.getB()) >= 0)) {
                                s_kizon.setSelected(2);
                            }

                            if ((t_begin.sameSide(a, s_kizon.getA()) == -1) && (t_begin.sameSide(a, s_kizon.getB()) == 1)) {
                                return s_kizon.withB(kouten);
                            }

                            if ((t_begin.sameSide(a, s_kizon.getA()) == 1) && (t_begin.sameSide(a, s_kizon.getB()) == -1)) {
                                return s_kizon.withA(kouten);
                            }
                        }
                    }
                    return s_kizon;
                }).collect(Collectors.toList());
            }
        }

        //選択状態のものを削除
        voronoiLineSet = voronoiLineSet.stream().filter(s -> s.getSelected() != 2).collect(Collectors.toList());

        //Add the line segment of Senb_boro_1p to the end of senbun of voronoiLineSet
        voronoiLineSet.addAll(voronoiLinesAroundNewPoint);
    }

    public void Senb_boro_1p_motome(int newSeedPointIndex) {//It can be used when line_step contains only Voronoi mother points. Get Senb_boro_1p as a set of Voronoi line segments around newSeedPointIndex
        //i_egaki_dankai Obtain an array list of Voronoi line segments surrounding the third Voronoi vertex. // i_egaki_dankai The third Voronoi apex is line_step [i_egaki_dankai] .geta ()
        voronoiLinesAroundNewPoint.clear();

        for (int i_e_d = 0; i_e_d < seedPoints.size(); i_e_d++) {
            if (i_e_d != newSeedPointIndex) {
                //Find the line segment to add
                LineSegmentVoronoi add_lineSegment = new LineSegmentVoronoi(
                        OritaCalc.bisection(seedPoints.get(i_e_d), seedPoints.get(newSeedPointIndex), 1000.0));

                Logger.info("newSeedPointIndex= " + newSeedPointIndex + " ,i_e_d= " + i_e_d);

                if (i_e_d < newSeedPointIndex) {
                    add_lineSegment.setVoronoiA(i_e_d);
                    add_lineSegment.setVoronoiB(newSeedPointIndex);//Record the two Voronoi vertices of the Voronoi line segment in iactive and color
                } else {
                    add_lineSegment.setVoronoiA(newSeedPointIndex);
                    add_lineSegment.setVoronoiB(i_e_d);//Record the two Voronoi vertices of the Voronoi line segment in iactive and color
                }
                voronoi_02_01(newSeedPointIndex, add_lineSegment);
            }
        }
    }


    private void voronoi_02_01(int newSeedPointIndex, LineSegmentVoronoi add_lineSegment) {
        //i_egaki_dankai番目のボロノイ頂点は　　line_step[i_egaki_dankai].geta()　　　

        //Organize the line segments to be added
        StraightLine add_straightLine = new StraightLine(add_lineSegment.getLineSegment());

        int i_saisyo = voronoiLinesAroundNewPoint.size() - 1;
        for (int i = i_saisyo; i >= 0; i--) {
            //Organize existing line segments
            LineSegmentVoronoi existing_lineSegment = new LineSegmentVoronoi(voronoiLinesAroundNewPoint.get(i));
            StraightLine existing_straightLine = new StraightLine(existing_lineSegment.getLineSegment());

            //Fight the line segment to be added with the existing line segment

            OritaCalc.ParallelJudgement parallel = OritaCalc.isLineSegmentParallel(add_straightLine, existing_straightLine, Epsilon.UNKNOWN_1EN4);//0 = not parallel, 1 = parallel and 2 straight lines do not match, 2 = parallel and 2 straight lines match

            Point a = seedPoints.get(newSeedPointIndex);
            if (parallel == OritaCalc.ParallelJudgement.PARALLEL_EQUAL) {
                return;
            }
            if (parallel == OritaCalc.ParallelJudgement.PARALLEL_NOT_EQUAL) {//When the line segment to be added and the existing line segment are parallel and the two straight lines do not match
                if (add_straightLine.sameSide(a, existing_lineSegment.getA()) == -1) {
                    voronoiLinesAroundNewPoint.remove(i);
                } else if (existing_straightLine.sameSide(a, add_lineSegment.getA()) == -1) {
                    return;
                }
            } else if (parallel == OritaCalc.ParallelJudgement.NOT_PARALLEL) {//When the line segment to be added and the existing line segment are parallel and the two straight lines match
                //When the line segment to be added and the existing line segment are non-parallel
                Point intersection = OritaCalc.findIntersection(add_straightLine, existing_straightLine);

                if ((add_straightLine.sameSide(a, existing_lineSegment.getA()) <= 0) &&
                        (add_straightLine.sameSide(a, existing_lineSegment.getB()) <= 0)) {
                    voronoiLinesAroundNewPoint.remove(i);
                } else if ((add_straightLine.sameSide(a, existing_lineSegment.getA()) == 1) &&
                        (add_straightLine.sameSide(a, existing_lineSegment.getB()) == -1)) {
                    existing_lineSegment = existing_lineSegment.withB(intersection);
                    if (existing_lineSegment.getLineSegment().determineLength() < Epsilon.UNKNOWN_1EN7) {
                        voronoiLinesAroundNewPoint.remove(i);
                    } else {
                        voronoiLinesAroundNewPoint.set(i, existing_lineSegment);
                    }
                } else if ((add_straightLine.sameSide(a, existing_lineSegment.getA()) == -1) &&
                        (add_straightLine.sameSide(a, existing_lineSegment.getB()) == 1)) {
                    existing_lineSegment = existing_lineSegment.withA(intersection);
                    if (existing_lineSegment.getLineSegment().determineLength() < Epsilon.UNKNOWN_1EN7) {
                        voronoiLinesAroundNewPoint.remove(i);
                    } else {
                        voronoiLinesAroundNewPoint.set(i, existing_lineSegment);
                    }
                }

                if ((existing_straightLine.sameSide(a, add_lineSegment.getA()) <= 0) &&
                        (existing_straightLine.sameSide(a, add_lineSegment.getB()) <= 0)) {
                    return;
                } else if ((existing_straightLine.sameSide(a, add_lineSegment.getA()) == 1) &&
                        (existing_straightLine.sameSide(a, add_lineSegment.getB()) == -1)) {
                    add_lineSegment = add_lineSegment.withB(intersection);
                    if (add_lineSegment.getLineSegment().determineLength() < Epsilon.UNKNOWN_1EN7) {
                        return;
                    }
                } else if ((existing_straightLine.sameSide(a, add_lineSegment.getA()) == -1) &&
                        (existing_straightLine.sameSide(a, add_lineSegment.getB()) == 1)) {
                    add_lineSegment = add_lineSegment.withA(intersection);
                    if (add_lineSegment.getLineSegment().determineLength() < Epsilon.UNKNOWN_1EN7) {
                        return;
                    }
                }
            }
        }

        voronoiLinesAroundNewPoint.add(add_lineSegment);
    }

    // -----------------------------------------------------------------------------
    //マウス操作(mouseMode==62ボロノイ　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
    }

    // -----------------------------------------------------------------------------
    //マウス操作(mouseMode==62ボロノイ　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
    }

    @Override
    public void apply() {
        for (LineSegmentVoronoi ls : voronoiLineSet) {
            d.addLineSegment(ls.lineSegment.withColor(d.getLineColor()));
        }
        for (Point p : seedPoints){
            d.addCircle(new Circle(p, 5, LineColor.CYAN_3));
        }

        reset();
    }
}

class LineSegmentVoronoi {
    int voronoiA;
    int voronoiB;
    LineSegment lineSegment;
    private int selected;

    public LineSegmentVoronoi(LineSegment ls) {
        lineSegment = ls;
        voronoiA = 0;
        voronoiB = 0;
        selected = 0;
    }

    public LineSegmentVoronoi(LineSegmentVoronoi s) {
        lineSegment = s.lineSegment;
        voronoiA = s.getVoronoiA();
        voronoiB = s.getVoronoiB();
        selected = s.getSelected();
    }

    public LineSegmentVoronoi withB(Point b) {
        LineSegmentVoronoi v = new LineSegmentVoronoi(this.lineSegment.withB(b));

        v.voronoiA = getVoronoiA();
        v.voronoiB = getVoronoiB();
        return v;
    }

    public LineSegmentVoronoi withA(Point a) {
        LineSegmentVoronoi v = new LineSegmentVoronoi(this.lineSegment.withA(a));
        v.voronoiA = getVoronoiA();
        v.voronoiB = getVoronoiB();
        return v;
    }

    public int getVoronoiA() {
        return voronoiA;
    }

    public void setVoronoiA(int i) {
        voronoiA = i;
    }

    public int getVoronoiB() {
        return voronoiB;
    }

    public void setVoronoiB(int i) {
        voronoiB = i;
    }

    public void setSelected(int selected){
        this.selected = selected;
    }
    public int getSelected(){
        return selected;
    }

    public LineSegment getLineSegment() {
        return lineSegment;
    }

    public Point getA() {
        return lineSegment.getA();
    }
    public Point getB() {
        return lineSegment.getB();
    }
}
