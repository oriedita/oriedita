package origami.crease_pattern;

import org.tinylog.Logger;
import origami.Epsilon;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;
import origami.crease_pattern.element.*;
import origami.data.quadTree.QuadTree;
import origami.data.quadTree.adapter.DivideAdapter;
import origami.data.quadTree.adapter.LineSegmentListAdapter;
import origami.data.quadTree.adapter.LineSegmentListEndPointAdapter;
import origami.data.quadTree.collector.LineSegmentCollector;
import origami.data.quadTree.collector.PointCollector;
import origami.data.save.LineSegmentSave;
import origami.folding.util.SortingBox;
import origami.folding.util.WeightedValue;

import java.awt.*;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Representation of the current drawn crease pattern.
 */
public class FoldLineSet {
        int total;               //Total number of line segments actually used
    List<LineSegment> lineSegments = new ArrayList<>(); //折線とする線分のインスタンス化

    Queue<LineSegment> Check1LineSegment = new ConcurrentLinkedQueue<>(); //Instantiation of line segments to store check information
    Queue<LineSegment> Check2LineSegment = new ConcurrentLinkedQueue<>(); //Instantiation of line segments to store check information
    Queue<LineSegment> Check3LineSegment = new ConcurrentLinkedQueue<>(); //Instantiation of line segments to store check information
    Queue<LineSegment> Check4LineSegment = new ConcurrentLinkedQueue<>(); //Instantiation of line segments to store check information

    List<Circle> circles = new ArrayList<>(); //円のインスタンス化

    // Specify the point Q, delete the line segments AQ and QC, and add the line segment AC (however, only two line segments have Q as the end point) // When implemented, 1 when nothing is done Returns 0.
    // The procedure is (1) The point p is determined by clicking the mouse.
    // (2) Point p The end point q included in the nearest crease pattern is determined.
    // (3) If the distance between the end point of the fold line in the crease pattern that is closer to q and q is r or less, the fold line is assumed to be connected to the point q.
    int[] i_s = new int[2];//この変数はdel_Vとtyouten_syuui_sensuuとで共通に使う。tyouten_syuui_sensuuで、頂点回りの折線数が2のときにその2折線の番号を入れる変数。なお、折線数が3以上のときは意味を成さない。//qを端点とする2本の線分の番号

    public FoldLineSet() {
        reset();
    } //コンストラクタ

    public void reset() {
        total = 0;
        lineSegments.clear();
        lineSegments.add(new LineSegment());
        Check1LineSegment.clear();
        Check2LineSegment.clear();
        Check3LineSegment.clear();
        Check4LineSegment.clear();
        circles.clear();
    }

    public void set(FoldLineSet foldLineSet) {
        setTotal(foldLineSet.getTotal());
        for (int i = 0; i <= total; i++) {
            LineSegment s = lineSegments.get(i);
            s.set(foldLineSet.get(i));
        }
    }

    //Get the total number of line segments
    public int getTotal() {
        return total;
    }

    private void setTotal(int total) {
        this.total = total;

        while (lineSegments.size() < total + 1) {
            lineSegments.add(new LineSegment());
        }
    }

    //Get a line segment
    public LineSegment get(int i) {
        return lineSegments.get(i);
    }

    //Enter the value of the i-th line segment
    public void set(int i, Point p, Point q) {
        LineSegment s = lineSegments.get(i);
        s.setA(p);
        s.setB(q);
    }

    //Enter the value of the i-th line segment
    public void set(int i, Point p, Point q, LineColor ic, LineSegment.ActiveState ia) {
        LineSegment s = lineSegments.get(i);
        s.set(p, q, ic, ia);
    }

    //Enter the color of the i-th line segment
    public void setColor(int i, LineColor icol) {
        LineSegment s = lineSegments.get(i);
        s.setColor(icol);
    }

    //Output the color of the i-th line segment
    public LineColor getColor(int i) {
        LineSegment s = lineSegments.get(i);
        return s.getColor();
    }

    public void setCircleCustomized(int i, int customized) {
        Circle e = circles.get(i);
        e.setCustomized(customized);
    }

    public void setCircleCustomizedColor(int i, Color c0) {
        Circle e = circles.get(i);
        e.setCustomizedColor(c0);
    }

    //Enter the activity of the i-th line segment
    public void setActive(int i, LineSegment.ActiveState iactive) {
        LineSegment s = lineSegments.get(i);
        s.setActive(iactive);
    }

    //Output the activity of the i-th line segment
    public LineSegment.ActiveState getActive(int i) {
        LineSegment s = lineSegments.get(i);
        return s.getActive();
    }

    public void getSave(LineSegmentSave save) {
        getSave(save, "_");
    }

    public void getSave(LineSegmentSave save, String title) {
        save.setTitle(title);

        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);

            save.addLineSegment(s.clone());
            // Save linesegment
        }

        for (Circle circle : circles) {
            // Save circle

            Circle circle1 = new Circle();
            circle1.set(circle);

            save.addCircle(circle1);
        }
    }

    //Output the information of all line segments of the line segment set as Memo. // Iactive does not write out the fold line of excluding in the memo
    public void getMemo_active_excluding(LineSegmentSave save, LineSegment.ActiveState excluding) {
        for (int i = 1; i <= total; i++) {
            if (getActive(i) != excluding) {
                save.addLineSegment(lineSegments.get(i).clone());
            }
        }

        for (Circle circle : circles) {
            save.addCircle(circle);
        }
    }

    public void h_getSave(LineSegmentSave save) {
        for (int i = 1; i <= total; i++) {
            save.addAuxLineSegment(lineSegments.get(i));
        }
    }

    //Output the line segment set information as Memo for folding estimation. // Do not write out auxiliary lines with icol of 3 (cyan = light blue) or more in the memo
    public void getMemo_for_folding(LineSegmentSave save) {
        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);
            if (s.getColor().isFoldingLine()) {
                save.addLineSegment(s.clone());
            }
        }
    }

    //Output the line segment set information as Memo for folding estimation. // Do not write out auxiliary lines with icol of 3 (cyan = light blue) or more in the memo
    public void getSaveForSelectFolding(LineSegmentSave save) {
        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);
            if ((s.getColor().isFoldingLine()) && (s.getSelected() == 2)) {
                save.addLineSegment(s.clone());
            }
        }
    }

    //The number of broken lines of the line segment set selected for folding estimation is output as an int.
    // Do not count auxiliary lines with icol of 3 (cyan = light blue) or more
    public int getFoldLineTotalForSelectFolding() {
        int number = 0;
        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);
            if (s.getColor().isFoldingLine() && s.getSelected() == 2) {
                number++;
            }
        }
        return number;
    }

    public String setSave(LineSegmentSave save) {
        circles.clear();
        circles.addAll(save.getCircles());

        lineSegments.clear();
        lineSegments.add(new LineSegment());
        lineSegments.addAll(save.getLineSegments());

        total = lineSegments.size() - 1;

        return save.getTitle();
    }

    public void setAuxSave(LineSegmentSave save) {
        lineSegments.clear();
        lineSegments.add(new LineSegment());
        lineSegments.addAll(save.getAuxLineSegments());
        total = lineSegments.size() - 1;
    }

    public void addSave(LineSegmentSave memo1) {
        for (LineSegment s : memo1.getLineSegments()) {
            //First the total number of line segments was calculated

            LineSegment s0 = new LineSegment();
            s0.set(s);

            lineSegments.add(s0);
            total++;
        }
        for (Circle c : memo1.getCircles()) {
            Circle c0 = new Circle();
            c0.set(c);

            circles.add(c0);
        }
    }

    //Replace the mountains and valleys of all lines. There is no change in line types other than mountains and valleys such as boundaries.
    public void allMountainValleyChange() {
        for (int ic_id = 1; ic_id <= total; ic_id++) {
            LineSegment s = lineSegments.get(ic_id);
            s.setColor(s.getColor().changeMV());
        }
    }

    //Output the information of all line segments of the line segment set as Memo.
    // Do not write down the fold line of select except
    public <T extends LineSegmentSave> T getMemoExceptSelected(T save, int except) {
        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);

            if (s.getSelected() != except) {
                save.addLineSegment(s.clone());
            }
        }

        for (Circle circle : circles) {
            save.addCircle(circle);
        }

        return save;
    }

    //Output the information of all line segments of the line segment set as Memo.
    // select writes out option polygonal line in a memo
    public <T extends LineSegmentSave> T getMemoSelectOption(T save, int option) {
        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);

            if (s.getSelected() == option) {
                save.addLineSegment(s.clone());
            }
        }
        return save;
    }

    public void select_all() {
        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);
            s.setSelected(2);
        }
    }

    public void unselect_all() {
        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);
            s.setSelected(0);
        }
    }

    public void select(int i) {
        LineSegment s = lineSegments.get(i);
        s.setSelected(2);
    }

    public void select(Point p1, Point p2, Point p3) {
        Polygon triangle = new Polygon(3);
        triangle.set(1, p1);
        triangle.set(2, p2);
        triangle.set(3, p3);

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = lineSegments.get(i);
            if (triangle.totu_boundary_inside(s)) {
                s.setSelected(2);
            }
        }
    }

    public void select(Polygon p) {
        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);
            if (p.totu_boundary_inside(s)) {

                s.setSelected(2);

            }
        }
    }

    public void unselect(Polygon p) {
        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);
            if (p.totu_boundary_inside(s)) {
                s.setSelected(0);
            }
        }
    }

    //--------------------------------
    public int MV_change(Polygon p) {
        int i_r = 0;

        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);
            if (p.totu_boundary_inside(s)) {
                LineColor ic_temp = s.getColor();/**/
                if (ic_temp == LineColor.RED_1) {
                    s.setColor(LineColor.BLUE_2);
                } else if (ic_temp == LineColor.BLUE_2) {
                    s.setColor(LineColor.RED_1);
                }
                i_r = 1;
            }
        }
        return i_r;
    }

    //--------------------------------
    public boolean insideToMountain(Polygon p) {
        boolean i_r = false;

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = lineSegments.get(i);
            if (p.totu_boundary_inside(s)) {
                s.setColor(LineColor.RED_1);
                i_r = true;
            }
        }
        return i_r;
    }

    //--------------------------------
    public boolean insideToValley(Polygon b) {
        boolean i_r = false;

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = lineSegments.get(i);
            if (b.totu_boundary_inside(s)) {
                s.setColor(LineColor.BLUE_2);
                i_r = true;
            }
        }
        return i_r;
    }


    public boolean insideToEdge(Polygon b) {
        boolean i_r = false;

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = lineSegments.get(i);
            if (b.totu_boundary_inside(s)) {
                s.setColor(LineColor.BLACK_0);
                i_r = true;
            }
        }
        return i_r;
    }

    public boolean insideToAux(Polygon b) {
        boolean i_r = false;

        int okikae_suu = 0;
        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);
            if (s.getColor().isFoldingLine()) {
                if (b.totu_boundary_inside(s)) {
                    okikae_suu = okikae_suu + 1;

                    LineSegment add_sen = new LineSegment();
                    add_sen.set(s);
                    add_sen.setColor(LineColor.CYAN_3);

                    deleteLine(i);
                    addLine(add_sen);
                    i = i - 1;

                    i_r = true;
                }
            }
        }

        int kawatteinai_kazu = total - okikae_suu;
        if (kawatteinai_kazu == 0) {
            divideLineSegmentIntersections();
        }
        if (kawatteinai_kazu >= 1) {
            if (okikae_suu >= 1) {
                divideLineSegmentWithNewLines(total - okikae_suu, total);
            }
        }
//上２行の場合わけが必要な理由は、kousabunkatu()をやってしまうと折線と補助活線との交点で折線が分割されるから。kousabunkatu(1,sousuu-okikae_suu,sousuu-okikae_suu+1,sousuu)だと折線は分割されない。


        return i_r;

    }

    public boolean deleteInsideLine(LineSegment s_step1, String Dousa_mode) {
        //"l"  lXは小文字のエル。Senbun s_step1と重複する部分のある線分を削除するモード。
        //"lX" lXは小文字のエルと大文字のエックス。Senbun s_step1と重複する部分のある線分やX交差する線分を削除するモード。
        boolean i_r = false;//たくさんある折線のうち、一本でも削除すれば1、1本も削除しないなら0。

        FoldLineSave save = new FoldLineSave();
        boolean i_kono_orisen_wo_sakujyo;//i_この折線を削除　0削除しない、1削除する
        for (int i = 1; i <= total; i++) {

            LineSegment s;
            s = lineSegments.get(i);

            i_kono_orisen_wo_sakujyo = false;

            if (Dousa_mode.equals("l")) {
                if (OritaCalc.isLineSegmentOverlapping(s, s_step1)) {
                    i_kono_orisen_wo_sakujyo = true;
                }
            }

            if (Dousa_mode.equals("lX")) {
                if (OritaCalc.isLineSegmentOverlapping(s, s_step1)) {
                    i_kono_orisen_wo_sakujyo = true;
                }
                if (OritaCalc.lineSegment_X_kousa_decide(s, s_step1)) {
                    i_kono_orisen_wo_sakujyo = true;
                }
            }


            if (i_kono_orisen_wo_sakujyo) {
                i_r = true;
            }
            if (!i_kono_orisen_wo_sakujyo) {
                save.addLineSegment(s.clone());
            }
        }

        Point ec = new Point();//円の中心座標を入れる変数

        int ii = 0;
        for (Circle circle : circles) {
            boolean idel = false;
            Circle e_temp = new Circle();
            e_temp.set(circle);
            ec.set(e_temp.determineCenter());

            if (idel) {
                i_r = true;
            }
            if (!idel) {
                ii = ii + 1;
                save.addCircle(e_temp);
            }
        }

        reset();
        setSave(save);

        return i_r;
    }

    public boolean deleteInside(Polygon p) {
        boolean i_r = false;

        FoldLineSave save = new FoldLineSave();

        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);

            if (p.totu_boundary_inside(s)) {
                i_r = true;
            } else {
                save.addLineSegment(s.clone());
            }
        }

        for (Circle circle : circles) {
            Circle e_temp = new Circle();
            e_temp.set(circle);

            if (p.totu_boundary_inside(e_temp)) {
                i_r = true;
            } else {
                save.addCircle(e_temp);
            }
        }

        reset();
        setSave(save);

        return i_r;
    }

    public boolean deleteInside_foldingLine(Polygon p) {//Delete only the polygonal line
        boolean i_r = false;

        FoldLineSave save = new FoldLineSave();

        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);

            if ((p.totu_boundary_inside(s)) && s.getColor().isFoldingLine()) {
                i_r = true;
            }//黒赤青線はmemo1に書かれない。つまり削除される。
            else if ((!p.totu_boundary_inside(s)) || !s.getColor().isFoldingLine()) {
                save.addLineSegment(s.clone());
            }
        }

        int ii = 0;
        for (Circle circle : circles) {
            Circle e_temp = new Circle();
            e_temp.set(circle);//ec.set(e_temp.get_tyuusin());er=e_temp.getr();

            ii = ii + 1;
            save.addCircle(e_temp);
        }

        reset();

        setSave(save);

        return i_r;
    }

    public boolean deleteInside_edge(Polygon p) {//Delete only the polygonal line
        boolean i_r = false;

        FoldLineSave save = new FoldLineSave();
        int ibangou = 0;

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = lineSegments.get(i);

            if ((p.totu_boundary_inside(s)) && (s.getColor() == LineColor.BLACK_0)) {
                i_r = true;
            }//黒線はmemo1に書かれない。つまり削除される。
            else if ((!p.totu_boundary_inside(s)) || (s.getColor() != LineColor.BLACK_0)) {
                ibangou = ibangou + 1;
                save.addLineSegment(s.clone());
            }
        }

        int ii = 0;
        for (Circle circle : circles) {
            Circle e_temp = new Circle();
            e_temp.set(circle);//ec.set(e_temp.get_tyuusin());er=e_temp.getr();
            ii = ii + 1;
            save.addCircle(e_temp);
        }

        reset();
        setSave(save);

        return i_r;
    }

    public boolean deleteInside_aux(Polygon p) {//Delete only auxiliary live line
        boolean i_r = false;

        FoldLineSave save = new FoldLineSave();

        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);

            if ((p.totu_boundary_inside(s)) && (s.getColor() == LineColor.CYAN_3)) {
                i_r = true;
            } else if ((!p.totu_boundary_inside(s)) || (s.getColor() != LineColor.CYAN_3)) {
                save.addLineSegment(s.clone());
            }
        }

        for (Circle circle : circles) {
            Circle e_temp = new Circle();
            e_temp.set(circle);

            if (p.totu_boundary_inside(e_temp)) {
                i_r = true;
            } else {
                save.addCircle(e_temp);
            }
        }

        reset();
        setSave(save);

        return i_r;
    }

    //--------------------------------
    public boolean change_property_in_4kakukei(Polygon p, Color sen_tokutyuu_color) {//Change properties such as the color of circles and auxiliary live lines inside a quadrangle
        boolean i_r = false;

        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);

            if (p.totu_boundary_inside(s) && (s.getColor() == LineColor.CYAN_3)) {
                i_r = true;
                s.setCustomized(1);
                s.setCustomizedColor(sen_tokutyuu_color);
            }
        }

        for (Circle circle : circles) {
            Circle e_temp = new Circle();
            e_temp.set(circle);

            if (p.totu_boundary_inside(e_temp)) {
                i_r = true;
                circle.setCustomized(1);
                circle.setCustomizedColor(sen_tokutyuu_color);
            }
        }

        return i_r;
    }

    public int get_select(int i) {
        LineSegment s = lineSegments.get(i);
        return s.getSelected();
    }

    public void delSelectedLineSegmentFast() {
        FoldLineSave memo_temp = new FoldLineSave();
        getMemoExceptSelected(memo_temp, 2);
        reset();
        setSave(memo_temp);
    }

    //Remove dotted line segments
    public void removePoints() {
        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);
            if (OritaCalc.equal(s.getA(), s.getB())) {
                deleteLine(i);
                i = i - 1;
            }
        }
    }

    // When there are two completely overlapping line segments, the one with the later number is deleted.
    public void removeOverlappingLines(double r) {
        boolean[] removal_flg = new boolean[total + 1];
        LineSegment[] snew = new LineSegment[total + 1];
        for (int i = 1; i <= total; i++) {
            removal_flg[i] = false;
            snew[i] = new LineSegment();
        }

        for (int i = 1; i <= total - 1; i++) {
            LineSegment si;
            si = lineSegments.get(i);
            for (int j = i + 1; j <= total; j++) {
                LineSegment sj;
                sj = lineSegments.get(j);
                if (r <= -9999.9) {
                    if (OritaCalc.determineLineSegmentIntersection(si, sj) == LineSegment.Intersection.PARALLEL_EQUAL_31) {
                        removal_flg[j] = true;
                    }
                } else {
                    if (OritaCalc.determineLineSegmentIntersection(si, sj, r) == LineSegment.Intersection.PARALLEL_EQUAL_31) {
                        removal_flg[j] = true;
                    }
                }
            }
        }

        int smax = 0;
        for (int i = 1; i <= total; i++) {
            if (!removal_flg[i]) {
                LineSegment si;
                si = lineSegments.get(i);
                smax = smax + 1;
                snew[smax].set(si);
            }
        }

        setTotal(smax);
        for (int i = 1; i <= total; i++) {
            LineSegment si;
            si = lineSegments.get(i);
            si.set(snew[i]);
        }
    }

    public void removeOverlappingLines() {
        removeOverlappingLines(-10000.0);
    }

    //------------------zzzzzzzzz-------------------------------------------------------------------
    /**
     * Divide the two line segments at the intersection of the two intersecting line
     * segments. If there were two line segments that completely overlapped, both
     * would remain without any processing.
     * 
     * Crossing division when originalEnd + 1 to addedEnd fold lines are added to
     * the original 1 to originalEnd fold lines.
     */
    public void divideLineSegmentWithNewLines(int originalEnd, int addedEnd) {
        for (int i = 1; i <= total; i++) {
            setActive(i, LineSegment.ActiveState.INACTIVE_0);
        }//削除すべき線は iactive=100とする
        //Logger.info("1234567890   kousabunkatu");
        ArrayList<Integer> k_flg = new ArrayList<>();//交差分割の影響があることを示すフラッグ。

        for (int i = 0; i <= total + 100; i++) {
            k_flg.add(0);
        }//0は交差分割の対象外、１は元からあった折線、2は加える折線として交差分割される。3は削除すべきと判定された折線
        for (int i = 1; i <= originalEnd; i++) {
            k_flg.set(i, 1);
        }//0 is not subject to cross-division, 1 is the original polygonal line, and 2 is the cross-division to be added.
        for (int i = originalEnd + 1; i <= addedEnd; i++) {
            k_flg.set(i, 2);
        }//0は交差分割の対象外、１は元からあった折線、2は加える折線として交差分割される

        // This QuadTree only stores the original lines for better performance.
        QuadTree qt = new QuadTree(new DivideAdapter(lineSegments, originalEnd));

        for (int i = originalEnd + 1; i <= total; i++) {
            if (k_flg.get(i) == 2) {//k_flg.set(i,new Integer(0));
                for (int j : qt.collect(new LineSegmentCollector(lineSegments.get(i)))) {
                    LineSegment.Intersection itemp = divideIntersectionsFast(i, j);//i is the one to add (2), j is the original one (1)
                    switch (itemp) {
                        case INTERSECTS_1:
                            k_flg.add(2);
                            k_flg.add(1);
                            k_flg.set(total - 1, 2);
                            k_flg.set(total, 1);
                            qt.addIndex(total);
                            break;
                        case INTERSECTS_AUX_2:
                        case INTERSECT_T_A_211:
                        case INTERSECT_T_B_221:
                            k_flg.add(2);
                            k_flg.set(total, 2);
                            break;
                        case INTERSECTS_AUX_3:
                        case INTERSECT_T_A_121:
                        case INTERSECT_T_B_122:
                        case PARALLEL_S2_INCLUDES_S1_363:
                        case PARALLEL_S2_INCLUDES_S1_364:
                            k_flg.add(1);
                            k_flg.set(total, 1);
                            qt.addIndex(total);
                            break;
                        case PARALLEL_S1_INCLUDES_S2_361:
                        case PARALLEL_S1_INCLUDES_S2_362:
                            k_flg.set(j, 0);
                            k_flg.add(2);
                            k_flg.set(total, 2);
                            break;
                        case PARALLEL_S1_END_OVERLAPS_S2_START_371:
                        case PARALLEL_S1_START_OVERLAPS_S2_END_373:
                        case PARALLEL_S1_END_OVERLAPS_S2_END_372:
                        case PARALLEL_S1_START_OVERLAPS_S2_START_374:
                            k_flg.add(0);
                            k_flg.set(total, 0);
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        FoldLineSave memo_temp = new FoldLineSave();
        getMemo_active_excluding(memo_temp, LineSegment.ActiveState.MARK_FOR_DELETION_100);
        reset();
        setSave(memo_temp);
    }

    public LineSegment.Intersection divideIntersectionsFast(int i, int j) {//i is the one to add (2), j is the original one (1) // = 0 does not intersect
        LineSegment si = lineSegments.get(i);
        LineSegment sj = lineSegments.get(j);

        if (si.determineMaxX() < sj.determineMinX()) {
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }
        if (sj.determineMaxX() < si.determineMinX()) {
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }
        if (si.determineMaxY() < sj.determineMinY()) {
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }
        if (sj.determineMaxY() < si.determineMinY()) {
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }
        Point intersect_point = new Point();
        StraightLine.Intersection intersect_flg0, intersect_flg1;

        // Here, as the idea of "how two line segments A and B intersect", (1) make the line segment A a straight line, and make the line segment B a line segment as it is (2) a line segment. Think of whether the two endpoints of B are both on one side of the straight line, or separately on both sides of the straight line.
        // After this confirmation is completed, next, make the line segment B a straight line, make the line segment A a line segment as it is, and confirm in the same way. Considering how the two line segments A and B intersect.

        StraightLine straightLine0 = new StraightLine(si.getA(), si.getB());
        intersect_flg0 = straightLine0.lineSegment_intersect_reverse_detail(sj);//senbun_kousa_hantei(Senbun s0){//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
        if (intersect_flg0 == StraightLine.Intersection.NONE_0) {
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }

        StraightLine straightLine1 = new StraightLine(sj.getA(), sj.getB());
        intersect_flg1 = straightLine1.lineSegment_intersect_reverse_detail(si);
        if (intersect_flg1 == StraightLine.Intersection.NONE_0) {
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }

        // --------------------------------------
        //	X intersection
        // --------------------------------------
        if ((intersect_flg0 == StraightLine.Intersection.INTERSECT_X_1) && (intersect_flg1 == StraightLine.Intersection.INTERSECT_X_1)) {//(intersect_flg0==1)&&(intersect_flg1==1) 加える折線と既存の折線はX型で交わる
            intersect_point.set(OritaCalc.findIntersection(straightLine0, straightLine1));

            if (((si.getColor() != LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3))
                    || ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3))) {

                addLine(intersect_point, si.getB(), si);
                si.setB(intersect_point);  //Divide the i-th line segment (end points a and b) at point p. Change the i-th line segment ab to ap and add the line segment pb.

                addLine(intersect_point, sj.getB(), sj);

                sj.setB(intersect_point);  //Divide the i-th line segment (end points a and b) at point p. Change the i-th line segment ab to ap and add the line segment pb.

                return LineSegment.Intersection.INTERSECTS_1;
            }

            if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {//The one to add i is the light blue line (auxiliary live line), and the one from the original j is the polygonal line.
                addLine(intersect_point, si.getB(), si);

                si.setB(intersect_point);  //Divide the i-th line segment (end points a and b) at point p. Change the i-th line segment ab to ap and add the line segment pb.

                return LineSegment.Intersection.INTERSECTS_AUX_2;
            }

            if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {//加えるほうiが折線、元からあるほうjが水色線（補助活線）
                addLine(intersect_point, sj.getB(), sj);

                sj.setB(intersect_point);  //j番目の線分(端点aとb)を点pで分割する。j番目の線分abをapに変え、線分pbを加える。

                return LineSegment.Intersection.INTERSECTS_AUX_3;
            }
        }


        // --------------------------------------
        //	T交差(加える折線のa点で交わる)
        // --------------------------------------
        if ((intersect_flg0 == StraightLine.Intersection.INTERSECT_X_1) && (intersect_flg1 == StraightLine.Intersection.INTERSECT_T_A_21)) {//加える折線と既存の折線はT型(加える折線が縦、既存の折線が横)で交わる(縦のa点で交わる)

            Point pk = new Point();
            pk.set(OritaCalc.findProjection(OritaCalc.lineSegmentToStraightLine(sj), si.getA()));//pkは点pの（線分を含む直線上の）影
            intersect_point.set(pk);//交差点は折線i上のs0の端点の影 20161129
            //foldLineSet.senbun_bunkatu(i , kousa_ten);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
            //以上で操作終了			kousa_ten.set(oc.kouten_motome(straightLine0,straightLine1));

            if (((si.getColor() != LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3))
                    || ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3))) {
                addLine(intersect_point, sj.getB(), sj);

                sj.setB(intersect_point);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
                return LineSegment.Intersection.INTERSECT_T_A_121;
            }

            if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {//加えるほうiが水色線（補助活線）、元からあるほうjが折線
                return LineSegment.Intersection.NO_INTERSECTION_0;//T交差はしてるが、縦線が補助活線なので何もしないので、0でreturnする。
            }

            if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {//加えるほうiが折線、元からあるほうjが水色線（補助活線）
                addLine(intersect_point, sj.getB(), sj);

                sj.setB(intersect_point);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
                return LineSegment.Intersection.INTERSECT_T_A_121;
            }
        }

        // --------------------------------------
        //	T交差(加える折線のb点で交わる)
        // --------------------------------------
        if ((intersect_flg0 == StraightLine.Intersection.INTERSECT_X_1) && (intersect_flg1 == StraightLine.Intersection.INTERSECT_T_B_22)) {//加える折線と既存の折線はT型(加える折線が縦、既存の折線が横)で交わる(縦のb点で交わる)
            Point pk = new Point();
            pk.set(OritaCalc.findProjection(OritaCalc.lineSegmentToStraightLine(sj), si.getB()));//pkは点pの（線分を含む直線上の）影
            intersect_point.set(pk);//交差点は折線i上のs0の端点の影 20161129
            //foldLineSet.senbun_bunkatu(i , kousa_ten);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
            //以上で操作終了			kousa_ten.set(oc.kouten_motome(straightLine0,straightLine1));

            if (((si.getColor() != LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3))
                    || ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3))) {
                addLine(intersect_point, sj.getB(), sj);

                sj.setB(intersect_point);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
                return LineSegment.Intersection.INTERSECT_T_B_122;
            }

            if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {//加えるほうiが水色線（補助活線）、元からあるほうjが折線
                return LineSegment.Intersection.NO_INTERSECTION_0;//T交差はしてるが、縦線が補助活線なので何もしないので、0でreturnする。
            }

            if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {//加えるほうiが折線、元からあるほうjが水色線（補助活線）
                addLine(intersect_point, sj.getB(), sj);

                sj.setB(intersect_point);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
                return LineSegment.Intersection.INTERSECT_T_B_122;
            }
        }

        // --------------------------------------
        //	T intersection (intersect at point a of the original polygonal line)
        // --------------------------------------
        if ((intersect_flg0 == StraightLine.Intersection.INTERSECT_T_A_21) && (intersect_flg1 == StraightLine.Intersection.INTERSECT_X_1)) {//The added fold line and the existing fold line intersect at a T shape (the added fold line is horizontal and the existing fold line is vertical) (intersect at the vertical a point).
            Point pk = new Point();
            pk.set(OritaCalc.findProjection(OritaCalc.lineSegmentToStraightLine(si), sj.getA()));//pk is the projection (on a straight line including the line segment) of point p
            intersect_point.set(pk);//交差点は折線i上のs0の端点の影 20161129
            //foldLineSet.senbun_bunkatu(i , kousa_ten);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
            //This is the end of the operation 			kousa_ten.set(oc.kouten_motome(straightLine0,straightLine1));

            if (((si.getColor() != LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3))
                    || ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3))) {
                addLine(intersect_point, si.getB(), si);

                si.setB(intersect_point);
                return LineSegment.Intersection.INTERSECT_T_A_211;
            }

            if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {//The one to add i is the light blue line (auxiliary live line), and the one from the original j is the polygonal line.
                addLine(intersect_point, si.getB(), si);

                si.setB(intersect_point);
                return LineSegment.Intersection.INTERSECT_T_A_211;
            }

            if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {//The one to add i is the polygonal line, and the one from the original j is the light blue line (auxiliary live line)
                return LineSegment.Intersection.NO_INTERSECTION_0;//There is a T intersection, but since the vertical line is an auxiliary live line, nothing is done, so return with 0.
            }
        }

        // --------------------------------------
        //	T intersection (intersect at point b of the original polygonal line)
        // --------------------------------------
        if ((intersect_flg0 == StraightLine.Intersection.INTERSECT_T_B_22) && (intersect_flg1 == StraightLine.Intersection.INTERSECT_X_1)) {//The added fold line and the existing fold line intersect at a T shape (the added fold line is horizontal and the existing fold line is vertical) (intersect at the vertical a point).
            Point pk = new Point();
            pk.set(OritaCalc.findProjection(OritaCalc.lineSegmentToStraightLine(si), sj.getB()));//pkは点pの（線分を含む直線上の）影
            intersect_point.set(pk);//The intersection is the shadow of the end point of s0 on the polygonal line i 20161129
            //foldLineSet.senbun_bunkatu(i , kousa_ten);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
            //以上で操作終了			kousa_ten.set(oc.kouten_motome(straightLine0,straightLine1));

            if (((si.getColor() != LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3))
                    || ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3))) {
                addLine(intersect_point, si.getB(), si);

                si.setB(intersect_point);
                return LineSegment.Intersection.INTERSECT_T_B_221;
            }

            if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {//The one to add i is the light blue line (auxiliary live line), and the one from the original j is the polygonal line.
                addLine(intersect_point, si.getB(), si);

                si.setB(intersect_point);
                return LineSegment.Intersection.INTERSECT_T_B_221;
            }

            if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {//加えるほうiが折線、元からあるほうjが水色線（補助活線）
                return LineSegment.Intersection.NO_INTERSECTION_0;//T交差はしてるが、縦線が補助活線なので何もしないので、0でreturnする。
            }
        }


        // --------------------------------------
        //	加える折線と既存の折線は平行
        // --------------------------------------
        if (intersect_flg0 == StraightLine.Intersection.INCLUDED_3) {//加える折線と既存の折線は同一直線上にある
            Point p1 = new Point();
            p1.set(si.getA());
            Point p2 = new Point();
            p2.set(si.getB());
            Point p3 = new Point();
            p3.set(sj.getA());
            Point p4 = new Point();
            p4.set(sj.getB());

            //setiactive(j,100)とされた折線は、kousabunkatu(int i1,int i2,int i3,int i4)の操作が戻った後で削除される。

            LineSegment.Intersection i_intersection_decision = OritaCalc.determineLineSegmentIntersection(si, sj, Epsilon.UNKNOWN_1EN6);//iは加える方(2)、jは元からある方(1)


            switch (i_intersection_decision) {
                case PARALLEL_EQUAL_31: //The two line segments are exactly the same
                    if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                    if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                    setActive(j, LineSegment.ActiveState.MARK_FOR_DELETION_100);
                    return LineSegment.Intersection.PARALLEL_EQUAL_31;

                case PARALLEL_START_OF_S1_CONTAINS_START_OF_S2_321: //(p1=p3)_p4_p2、siにsjが含まれる。
                    if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                    if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    }//加えるほうiが折線、元からあるほうjが水色線（補助活線）
                    sj.setColor(si.getColor());
                    si.setA(sj.getB());
                    return LineSegment.Intersection.PARALLEL_START_OF_S1_CONTAINS_START_OF_S2_321;
                case PARALLEL_START_OF_S2_CONTAINS_START_OF_S1_322: //(p1=p3)_p2_p4、siがsjに含まれる。
                    if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                    if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                    sj.setA(si.getB());
                    return LineSegment.Intersection.PARALLEL_START_OF_S2_CONTAINS_START_OF_S1_322;
                case PARALLEL_START_OF_S1_CONTAINS_END_OF_S2_331: //(p1=p4)_p3_p2、siにsjが含まれる。
                    if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                    if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                    sj.setColor(si.getColor());
                    si.setA(sj.getA());
                    return LineSegment.Intersection.PARALLEL_START_OF_S1_CONTAINS_END_OF_S2_331;
                case PARALLEL_END_OF_S2_CONTAINS_START_OF_S1_332: //(p1=p4)_p2_p3、siがsjに含まれる。
                    if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                    if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                    sj.setB(si.getB());
                    return LineSegment.Intersection.PARALLEL_END_OF_S2_CONTAINS_START_OF_S1_332;


                case PARALLEL_END_OF_S1_CONTAINS_START_OF_S2_341: //(p2=p3)_p4_p1、siにsjが含まれる。
                    if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                    if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    }//The one to add i is the polygonal line, and the one from the original j is the light blue line (auxiliary live line)

                    sj.setColor(si.getColor());
                    si.setB(sj.getB());
                    return LineSegment.Intersection.PARALLEL_END_OF_S1_CONTAINS_START_OF_S2_341;

                case PARALLEL_START_OF_S2_CONTAINS_END_OF_S1_342: //(p2=p3)_p1_p4、siがsjに含まれる。
                    if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                    if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                    sj.setA(si.getA());
                    return LineSegment.Intersection.PARALLEL_START_OF_S2_CONTAINS_END_OF_S1_342;


                case PARALLEL_END_OF_S1_CONTAINS_END_OF_S2_351: //(p2=p4)_p3_p1、siにsjが含まれる。
                    if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                    if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                    sj.setColor(si.getColor());
                    si.setB(sj.getA());
                    return LineSegment.Intersection.PARALLEL_END_OF_S1_CONTAINS_END_OF_S2_351;


                case PARALLEL_END_OF_S2_CONTAINS_END_OF_S1_352: //(p2=p4)_p1_p3、siがsjに含まれる。
                    if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                    if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                    sj.setB(si.getA());
                    return LineSegment.Intersection.PARALLEL_END_OF_S2_CONTAINS_END_OF_S1_352;


                case PARALLEL_S1_INCLUDES_S2_361: //線分(p1,p2)に線分(p3,p4)が含まれる ori_s_temp.senbun_bunkatu(s1.geta()); ori_s_temp.senbun_bunkatu(s1.getb());   foldLineSet.setiactive(i,100);//imax=imax-1;
                    if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                    if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                    sj.setColor(si.getColor());
                    addLine(sj.getB(), si.getB(), si);

                    si.setB(sj.getA());
                    return LineSegment.Intersection.PARALLEL_S1_INCLUDES_S2_361;
                case PARALLEL_S1_INCLUDES_S2_362: //線分(p1,p2)に線分(p4,p3)が含まれる; ori_s_temp.senbun_bunkatu(s1.getb());   foldLineSet.setiactive(i,100);//imax=imax-1;
                    if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                    if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                    sj.setColor(si.getColor());
                    addLine(sj.getA(), si.getB(), si);

                    si.setB(sj.getB());
                    return LineSegment.Intersection.PARALLEL_S1_INCLUDES_S2_362;
                case PARALLEL_S2_INCLUDES_S1_363: //線分(p3,p4)に線分(p1,p2)が含まれる foldLineSet.addsenbun(s0.getb(),s1.getb(),s1.getcolor());foldLineSet.setb(i,s0.geta());
                    if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                    if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                    addLine(si.getB(), sj.getB(), sj);

                    sj.setB(si.getA());
                    return LineSegment.Intersection.PARALLEL_S2_INCLUDES_S1_363;
                case PARALLEL_S2_INCLUDES_S1_364: //線分(p3,p4)に線分(p2,p1)が含まれるori_s.addsenbun(s0.geta(),s1.getb(),s1.getcolor());foldLineSet.setb(i,s0.getb());
                    if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                    if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                    addLine(si.getA(), sj.getB(), sj);

                    sj.setB(si.getB());
                    return LineSegment.Intersection.PARALLEL_S2_INCLUDES_S1_364;


                case PARALLEL_S1_END_OVERLAPS_S2_START_371: //線分(p1,p2)のP2側と線分(p3,p4)のP3側が部分的に重なる//ori_s_temp.senbun_bunkatu(s1.geta());foldLineSet.seta(i,s0.getb());
                    if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                    if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                    addLine(p3, p2, si);

                    si.setB(p3);
                    sj.setA(p2);
                    return LineSegment.Intersection.PARALLEL_S1_END_OVERLAPS_S2_START_371;

                case PARALLEL_S1_END_OVERLAPS_S2_END_372: //線分(p1,p2)のP2側と線分(p4,p3)のP4側が部分的に重なる//ori_s_temp.senbun_bunkatu(s1.getb());foldLineSet.setb(i,s0.getb());
                    if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                    if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                    addLine(p4, p2, si);

                    si.setB(p4);
                    sj.setB(p2);
                    return LineSegment.Intersection.PARALLEL_S1_END_OVERLAPS_S2_END_372;

                case PARALLEL_S1_START_OVERLAPS_S2_END_373: //線分(p3,p4)のP4側と線分(p1,p2)のP1側が部分的に重なる//ori_s_temp.senbun_bunkatu(s1.getb());foldLineSet.setb(i,s0.geta());
                    if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                    if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                    addLine(p1, p4, si);

                    si.setA(p4);
                    sj.setB(p1);
                    return LineSegment.Intersection.PARALLEL_S1_START_OVERLAPS_S2_END_373;

                case PARALLEL_S1_START_OVERLAPS_S2_START_374: //線分(p4,p3)のP3側と線分(p1,p2)のP1側が部分的に重なる//ori_s_temp.senbun_bunkatu(s1.geta());foldLineSet.seta(i,s0.geta());
                    if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                    if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                        return LineSegment.Intersection.NO_INTERSECTION_0;
                    }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                    addLine(p1, p3, si);

                    si.setA(p3);
                    sj.setA(p1);
                    return LineSegment.Intersection.PARALLEL_S1_START_OVERLAPS_S2_START_374;
                default:
                    break;
            }
        }
        return LineSegment.Intersection.NO_INTERSECTION_0;
    }

    //---------------------
    //交差している２つの線分の交点で２つの線分を分割する。　まったく重なる線分が２つあった場合は、なんの処理もなされないまま２つとも残る。
    public void divideLineSegmentIntersections() {
        int ibunkatu = 1;//分割があれば1、なければ0
        ArrayList<Boolean> k_flg = new ArrayList<>();//交差分割の影響があることを示すフラッグ。

        for (int i = 0; i <= total + 1; i++) {
            k_flg.add(true);
        }

        while (ibunkatu != 0) {
            ibunkatu = 0;
            for (int i = 1; i <= total; i++) {
                if (k_flg.get(i)) {
                    k_flg.set(i, false);
                    for (int j = 1; j <= total; j++) {
                        if (i != j) {
                            if (k_flg.get(j)) {
                                int old_sousuu = total;
                                boolean itemp = divideLineSegmentIntersections(i, j);
                                if (old_sousuu < total) {
                                    for (int is = old_sousuu + 1; is <= total; is++) {
                                        k_flg.add(true);
                                    }
                                }
                                if (itemp) {
                                    ibunkatu = ibunkatu + 1;
                                    k_flg.set(i, true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    //円の追加-------------------------------

    //Divide the two line segments at the intersection of the two intersecting line segments. After splitting 1. Returns 0 if not done. From Orihime 2.002, the color of the line after splitting is also controlled (if there is an overlap, it will be unified and the color with the later number will be used).
    public boolean divideLineSegmentIntersections(int i, int j) {
        if (i == j) {
            return false;
        }

        LineSegment si = lineSegments.get(i);
        LineSegment sj = lineSegments.get(j);

        if (si.determineMaxX() < sj.determineMinX()) {
            return false;
        }//これはSenbunにi_max_xがちゃんと定義されているときでないとうまくいかない
        if (sj.determineMaxX() < si.determineMinX()) {
            return false;
        }//これはSenbunにi_min_xがちゃんと定義されているときでないとうまくいかない
        if (si.determineMaxY() < sj.determineMinY()) {
            return false;
        }//これはSenbunにi_max_yがちゃんと定義されているときでないとうまくいかない
        if (sj.determineMaxY() < si.determineMinY()) {
            return false;
        }//これはSenbunにi_min_yがちゃんと定義されているときでないとうまくいかない

        Point p1 = new Point();
        p1.set(si.getA());
        Point p2 = new Point();
        p2.set(si.getB());
        Point p3 = new Point();
        p3.set(sj.getA());
        Point p4 = new Point();
        p4.set(sj.getB());
        Point pk = new Point();

        double ixmax = si.determineAX();
        double ixmin = si.determineAX();
        double iymax = si.determineAY();
        double iymin = si.determineAY();

        if (ixmax < si.determineBX()) {
            ixmax = si.determineBX();
        }
        if (ixmin > si.determineBX()) {
            ixmin = si.determineBX();
        }
        if (iymax < si.determineBY()) {
            iymax = si.determineBY();
        }
        if (iymin > si.determineBY()) {
            iymin = si.determineBY();
        }

        double jxmax = sj.determineAX();
        double jxmin = sj.determineAX();
        double jymax = sj.determineAY();
        double jymin = sj.determineAY();

        if (jxmax < sj.determineBX()) {
            jxmax = sj.determineBX();
        }
        if (jxmin > sj.determineBX()) {
            jxmin = sj.determineBX();
        }
        if (jymax < sj.determineBY()) {
            jymax = sj.determineBY();
        }
        if (jymin > sj.determineBY()) {
            jymin = sj.determineBY();
        }

        if (ixmax + Epsilon.UNKNOWN_05 < jxmin) {
            return false;
        }
        if (jxmax + Epsilon.UNKNOWN_05 < ixmin) {
            return false;
        }
        if (iymax + Epsilon.UNKNOWN_05 < jymin) {
            return false;
        }
        if (jymax + Epsilon.UNKNOWN_05 < iymin) {
            return false;
        }

        LineSegment.Intersection intersection = OritaCalc.determineLineSegmentIntersection(si, sj);
        switch (intersection) {
            case INTERSECTS_1:
                pk.set(OritaCalc.findIntersection(si, sj));
                si.setA(p1);
                si.setB(pk);
                sj.setA(p3);
                sj.setB(pk);
                addLine(p2, pk, si.getColor());
                addLine(p4, pk, sj.getColor());
                return true;
            case INTERSECTS_TSHAPE_S1_VERTICAL_BAR_25:
            case INTERSECTS_TSHAPE_S1_VERTICAL_BAR_26:
                pk.set(OritaCalc.findIntersection(si, sj));
                sj.setA(p3);
                sj.setB(pk);
                addLine(p4, pk, sj.getColor());
                return true;
            case INTERSECTS_TSHAPE_S2_VERTICAL_BAR_27:
            case INTERSECTS_TSHAPE_S2_VERTICAL_BAR_28:
                pk.set(OritaCalc.findIntersection(si, sj));
                si.setA(p1);
                si.setB(pk);
                addLine(p2, pk, si.getColor());
                return true;
            case NO_INTERSECTION_0: //このifないと本来この後で処理されるべき条件がここで処理されてしまうことある
                if (OritaCalc.determineLineSegmentDistance(si.getA(), sj) < Epsilon.UNKNOWN_001) {
                    if (OritaCalc.determineClosestLineSegmentEndpoint(si.getA(), sj, Epsilon.UNKNOWN_001) == 3) { //20161107 わずかに届かない場合
                        pk.set(OritaCalc.findIntersection(si, sj));
                        sj.setA(p3);
                        sj.setB(pk);
                        addLine(p4, pk, sj.getColor());
                        return true;
                    }
                }

                if (OritaCalc.determineLineSegmentDistance(si.getB(), sj) < Epsilon.UNKNOWN_001) {
                    if (OritaCalc.determineClosestLineSegmentEndpoint(si.getB(), sj, Epsilon.UNKNOWN_001) == 3) { //20161107 わずかに届かない場合
                        pk.set(OritaCalc.findIntersection(si, sj));
                        sj.setA(p3);
                        sj.setB(pk);
                        addLine(p4, pk, sj.getColor());
                        return true;
                    }
                }

                if (OritaCalc.determineLineSegmentDistance(sj.getA(), si) < Epsilon.UNKNOWN_001) {
                    if (OritaCalc.determineClosestLineSegmentEndpoint(sj.getA(), si, Epsilon.UNKNOWN_001) == 3) { //20161107 わずかに届かない場合
                        pk.set(OritaCalc.findIntersection(si, sj));
                        si.setA(p1);
                        si.setB(pk);
                        addLine(p2, pk, si.getColor());
                        return true;
                    }
                }

                if (OritaCalc.determineLineSegmentDistance(sj.getB(), si) < Epsilon.UNKNOWN_001) {
                    if (OritaCalc.determineClosestLineSegmentEndpoint(sj.getB(), si, Epsilon.UNKNOWN_001) == 3) { //20161107 わずかに届かない場合
                        pk.set(OritaCalc.findIntersection(si, sj));    //<<<<<<<<<<<<<<<<<<<<<<<
                        si.setA(p1);
                        si.setB(pk);
                        addLine(p2, pk, si.getColor());
                        return true;
                    }
                }

                break;
            case PARALLEL_EQUAL_31: //2つの線分がまったく同じ場合は、何もしない。
                return false;
            case PARALLEL_START_OF_S1_CONTAINS_START_OF_S2_321: {//2つの線分の端点どうし(p1とp3)が1点で重なる。siにsjが含まれる
                si.setA(p2);
                si.setB(p4);

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                sj.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_START_OF_S2_CONTAINS_START_OF_S1_322: {//2つの線分の端点どうし(p1とp3)が1点で重なる。sjにsiが含まれる
                sj.setA(p2);
                sj.setB(p4);

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                si.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_START_OF_S1_CONTAINS_END_OF_S2_331: {//2つの線分の端点どうし(p1とp4)が1点で重なる。siにsjが含まれる
                si.setA(p2);
                si.setB(p3);

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                sj.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_END_OF_S2_CONTAINS_START_OF_S1_332: {//2つの線分の端点どうし(p1とp4)が1点で重なる。sjにsiが含まれる
                sj.setA(p2);
                sj.setB(p3);
                LineColor overlapping_col;
                overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                si.setColor(overlapping_col);
                return true;
            }
            case PARALLEL_END_OF_S1_CONTAINS_START_OF_S2_341: {//2つの線分の端点どうし(p2とp3)が1点で重なる。siにsjが含まれる
                si.setA(p1);
                si.setB(p4);
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                sj.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_START_OF_S2_CONTAINS_END_OF_S1_342: {//2つの線分の端点どうし(p2とp3)が1点で重なる。sjにsiが含まれる
                sj.setA(p1);
                sj.setB(p4);
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                si.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_END_OF_S1_CONTAINS_END_OF_S2_351: {//2つの線分の端点どうし(p2とp4)が1点で重なる。siにsjが含まれる
                si.setA(p1);
                si.setB(p3);

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                sj.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_END_OF_S2_CONTAINS_END_OF_S1_352: {//2つの線分の端点どうし(p2とp4)が1点で重なる。sjにsiが含まれる
                sj.setA(p1);
                sj.setB(p3);
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                si.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_S1_INCLUDES_S2_361: {//p1-p3-p4-p2の順
                si.setA(p1);
                si.setB(p3);

                addLine(p2, p4, si.getColor());
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                sj.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_S1_INCLUDES_S2_362: {//p1-p4-p3-p2の順
                si.setA(p1);
                si.setB(p4);

                addLine(p2, p3, si.getColor());

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                sj.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_S2_INCLUDES_S1_363: {//p3-p1-p2-p4の順
                sj.setA(p1);
                sj.setB(p3);

                addLine(p2, p4, sj.getColor());

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                si.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_S2_INCLUDES_S1_364: {//p3-p2-p1-p4の順
                sj.setA(p1);
                sj.setB(p4);

                addLine(p2, p3, sj.getColor());

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                si.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_S1_END_OVERLAPS_S2_START_371: {//p1-p3-p2-p4の順
                si.setA(p1);
                si.setB(p3);

                sj.setA(p2);
                sj.setB(p4);

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                addLine(p2, p3, overlapping_col);
                return true;
            }
            case PARALLEL_S1_END_OVERLAPS_S2_END_372: {//p1-p4-p2-p3の順
                si.setA(p1);
                si.setB(p4);

                sj.setA(p3);
                sj.setB(p2);

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                addLine(p2, p4, overlapping_col);
                return true;
            }
            case PARALLEL_S1_START_OVERLAPS_S2_END_373: {//p3-p1-p4-p2の順
                sj.setA(p1);
                sj.setB(p3);
                si.setA(p2);
                si.setB(p4);
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                addLine(p1, p4, overlapping_col);
                return true;
            }
            case PARALLEL_S1_START_OVERLAPS_S2_START_374: {//p4-p1-p3-p2の順
                sj.setA(p1);
                sj.setB(p4);
                si.setA(p3);
                si.setB(p2);
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                addLine(p1, p3, overlapping_col);
                return true;
            }
            default:
                break;
        }

        return false;
    }

    public void addCircle(double dx, double dy, double dr, LineColor ic) {
        circles.add(new Circle(dx, dy, dr, ic));
    }

    public void addCircle(Point t, double dr) {
        addCircle(t.getX(), t.getY(), dr, LineColor.BLACK_0);
    }

    //Generates a circle with a radius of 0 at the intersection of circles -------------------------------
    public void applyCircleCircleIntersection(int imin, int imax, int jmin, int jmax) {
        for (int i = imin; i <= imax; i++) {
            Circle ei = new Circle();
            ei.set(circles.get(i));
            if (ei.getR() > Epsilon.UNKNOWN_1EN7) {//Circles with a radius of 0 are not applicable
                for (int j = jmin; j <= jmax; j++) {

                    Circle ej = new Circle();
                    ej.set(circles.get(j));
                    if (ej.getR() > Epsilon.UNKNOWN_1EN7) {//Circles with a radius of 0 are not applicable
                        if (OritaCalc.distance(ei.determineCenter(), ej.determineCenter()) < Epsilon.UNKNOWN_1EN6) {
                            //Two circles are concentric and do not intersect
                        } else if (Math.abs(OritaCalc.distance(ei.determineCenter(), ej.determineCenter()) - ei.getR() - ej.getR()) < Epsilon.UNKNOWN_1EN4) {
                            //Two circles intersect at one point
                            addCircle(OritaCalc.internalDivisionRatio(ei.determineCenter(), ej.determineCenter(), ei.getR(), ej.getR()), 0.0);
                        } else if (OritaCalc.distance(ei.determineCenter(), ej.determineCenter()) > ei.getR() + ej.getR()) {
                            //Two circles do not intersect
                        } else if (Math.abs(OritaCalc.distance(ei.determineCenter(), ej.determineCenter()) - Math.abs(ei.getR() - ej.getR())) < Epsilon.UNKNOWN_1EN4) {
                            //Two circles intersect at one point
                            addCircle(OritaCalc.internalDivisionRatio(ei.determineCenter(), ej.determineCenter(), -ei.getR(), ej.getR()), 0.0);
                        } else if (OritaCalc.distance(ei.determineCenter(), ej.determineCenter()) < Math.abs(ei.getR() - ej.getR())) {
                            //Two circles do not intersect
                        } else {//Two circles intersect at two points
                            LineSegment lineSegment = new LineSegment();
                            lineSegment.set(OritaCalc.circle_to_circle_no_intersection_wo_musubu_lineSegment(ei, ej));

                            addCircle(lineSegment.getA(), 0.0);
                            addCircle(lineSegment.getB(), 0.0);
                        }
                    }
                }
            }
        }
    }

    //A circle with a radius of 0 is generated at the intersection of the circle and the polygonal line.
    public void applyLineSegmentCircleIntersection(int imin, int imax, int jmin, int jmax) {
        for (int i = imin; i <= imax; i++) {
            LineSegment si = lineSegments.get(i);

            StraightLine ti = new StraightLine();
            ti.set(OritaCalc.lineSegmentToStraightLine(si));
            for (int j = jmin; j <= jmax; j++) {

                Circle ej = new Circle();
                ej.set(circles.get(j));
                if (ej.getR() > Epsilon.UNKNOWN_1EN7) {//Circles with a radius of 0 are not applicable
                    double tc_kyori = ti.calculateDistance(ej.determineCenter()); //Distance between the center of a straight line and a circle

                    if (Math.abs(tc_kyori - ej.getR()) < Epsilon.UNKNOWN_1EN6) {//Circle and straight line intersect at one point
                        if (Math.abs(OritaCalc.determineLineSegmentDistance(ej.determineCenter(), si) - ej.getR()) < Epsilon.UNKNOWN_1EN6) {
                            addCircle(OritaCalc.findProjection(ti, ej.determineCenter()), 0.0);
                        }
                    } else if (tc_kyori > ej.getR()) {
                        //Circles and straight lines do not intersect
                    } else {//Circle and straight line intersect at two points
                        LineSegment k_senb = new LineSegment();
                        k_senb.set(OritaCalc.circle_to_straightLine_no_intersect_wo_connect_LineSegment(ej, ti));

                        if (OritaCalc.determineLineSegmentDistance(k_senb.getA(), si) < Epsilon.UNKNOWN_1EN5) {
                            addCircle(k_senb.getA(), 0.0);
                        }
                        if (OritaCalc.determineLineSegmentDistance(k_senb.getB(), si) < Epsilon.UNKNOWN_1EN5) {
                            addCircle(k_senb.getB(), 0.0);
                        }
                    }
                }
            }
        }
    }

    //Delete circle-----------------------------------------
    public void deleteCircle(int j) {   //Delete the jth circle
        circles.remove(j);
    }

    //Arrangement of circles -----------------------------------------
    public boolean organizeCircles(int i0) {//Organize the jth circle. Returns 1 if deleted by pruning, 0 if not deleted.
        int ies3 = determineCircleState(i0, 3);
        int ies4 = determineCircleState(i0, 4);
        int ies5 = determineCircleState(i0, 5);

        if (ies3 == 100000) {
            return false;
        }
        if (ies3 == 2) {
            return false;
        }
        if ((ies3 == 1) && (ies4 >= 1)) {
            return false;
        }
        if ((ies3 == 1) && (ies5 >= 1)) {
            return false;
        }

        circles.remove(i0);
        return true;
    }

    //円の整理-----------------------------------------
    public void organizeCircles() {//Organize all circles.
        for (int i = circles.size() - 1; i >= 0; i--) {
            organizeCircles(i);
        }
    }

    //Circle status display -----------------------------------------
    public int determineCircleState(int i0, int i_mode) {   //Indicates the state of the i-th circle.
        // = 100000 The radius of the i-th circle is not 0
        // =      0 The radius of the i-th circle is 0. It is far from the circumference of other circles. It is far from the center of other circles. Twice
        // =      1 1st digit number. The number that the i-th circle has a radius of 0 and overlaps the center of another circle with a radius of 0. When it overlaps with two or more, it is displayed as 2.
        // =     10 Second digit number. The number of i-th circles with a radius of 0 and other non-zero radii that overlap the center of the circle. When it overlaps with two or more, it is displayed as 2.
        // =   100 3rd digit number. The number of circles with an i-th radius of 0 that overlap the circumference of other non-zero radii. When it overlaps with two or more, it is displayed as 2.
        // =  1000 4th digit number. The number of i-th circles with a radius of 0 that overlaps with other polygonal lines. When it overlaps with two or more, it is displayed as 2.
        // = 10000 5th digit number. The number of i-th circles with a radius of 0 that overlaps with other auxiliary hot lines. When it overlaps with two or more, it is displayed as 2.
        Circle e_temp = new Circle();
        e_temp.set(circles.get(i0));
        double er_0 = e_temp.getR();
        Point ec_0 = new Point();
        ec_0.set(e_temp.determineCenter());

        double er_1;
        Point ec_1 = new Point();

        int ir1 = 0;
        int ir2 = 0;
        int ir3 = 0;
        int ir4 = 0;
        int ir5 = 0;

        if (er_0 < Epsilon.UNKNOWN_1EN7) {
            for (int i = 0; i < circles.size(); i++) {
                if (i != i0) {
                    e_temp.set(circles.get(i));
                    er_1 = e_temp.getR();
                    ec_1.set(e_temp.determineCenter());
                    if (er_1 < Epsilon.UNKNOWN_1EN7) {//The radius of the other circle is 0
                        if (ec_0.distance(ec_1) < Epsilon.UNKNOWN_1EN7) {
                            ir1 = ir1 + 1;
                        }
                    } else {//The radius of the other circle is not 0
                        if (ec_0.distance(ec_1) < Epsilon.UNKNOWN_1EN7) {
                            ir2 = ir2 + 1;
                        } else if (Math.abs(ec_0.distance(ec_1) - er_1) < Epsilon.UNKNOWN_1EN7) {
                            ir3 = ir3 + 1;
                        }
                    }
                }
            }

            for (int i = 1; i <= total; i++) {
                LineSegment si;
                si = lineSegments.get(i);
                if (OritaCalc.determineLineSegmentDistance(ec_0, si) < Epsilon.UNKNOWN_1EN6) {

                    if (si.getColor().getNumber() <= 2) {
                        ir4 = ir4 + 1;
                    } else if (si.getColor() == LineColor.CYAN_3) {
                        ir5 = ir5 + 1;
                    }
                }
            }

            if (ir1 > 2) {
                ir1 = 2;
            }
            if (ir2 > 2) {
                ir2 = 2;
            }
            if (ir3 > 2) {
                ir3 = 2;
            }
            if (ir4 > 2) {
                ir4 = 2;
            }
            if (ir5 > 2) {
                ir5 = 2;
            }

            if (i_mode == 0) {
                return ir1 + ir2 * 10 + ir3 * 100 + ir4 * 1000 + ir5 * 10000;
            }
            if (i_mode == 1) {
                return ir1;
            }
            if (i_mode == 2) {
                return ir2;
            }
            if (i_mode == 3) {
                return ir3;
            }
            if (i_mode == 4) {
                return ir4;
            }
            if (i_mode == 5) {
                return ir5;
            }
        }

        return 100000;
    }

    //線分の追加-------------------------------
    public void addLine(Point pi, Point pj, LineColor i_c) {
        total++;

        LineSegment s = new LineSegment();
        s.set(pi, pj, i_c);

        lineSegments.add(s);
    }

    //線分の追加-------------------------------wwwwwwwwww
    public void addLine(Point pi, Point pj, LineSegment s0) {//Ten piからTen pjまでの線分を追加。この追加する線分のその他のパラメータはs0と同じ
        total++;

        LineSegment s = new LineSegment();
        s.set(s0);
        s.set(pi, pj);

        lineSegments.add(s);
    }

    //線分の追加-------------------------------
    public void addLine(Point pi, Point pj, LineColor i_c, LineSegment.ActiveState i_a) {
        total++;

        LineSegment s = new LineSegment();
        s.set(pi, pj, i_c, i_a);

        lineSegments.add(s);
    }

    //Add line segment -------------------------------
    public void addLine(double ax, double ay, double bx, double by, LineColor ic) {
        total++;

        LineSegment s = new LineSegment();
        s.set(ax, ay, bx, by, ic);

        lineSegments.add(s);
    }

    //線分の追加-------------------------------
    public void addLine(Point pi, Point pj) {
        total++;

        LineSegment s = new LineSegment();

        s.setA(pi);
        s.setB(pj);

        lineSegments.add(s);
    }

    //線分の追加-------------------------------
    public void addLine(LineSegment s0) {
        addLine(s0.getA(), s0.getB(), s0.getColor(), s0.getActive());//20181110追加
    }

    //線分の削除-----------------------------------------
    public void deleteLine(int j) {   //j番目の線分を削除する  このsi= sen(i)は大丈夫なのだろうか????????si= sen(i)　20161106
        lineSegments.remove(j);
        total--;
    }

    public void deleteLine(LineSegment s) {
        if (lineSegments.remove(s)) {
            total--;
        } else {
            throw new IllegalStateException("LineSegment not contained in FoldLineSet");
        }
    }

    //線分の分割-----------------qqqqq------------------------
    public void applyLineSegmentDivide(LineSegment s0, Point p) {   //Divide the i-th line segment (end points a and b) at point p. Change the i-th line segment ab to ap and add the line segment pb.
        LineSegment s1 = new LineSegment(p, s0.getB());//Create the i-th line segment ab before changing it to ap
        LineColor i_c = s0.getColor();

        s0.setB(p);//Change the i-th line segment ab to ap

        s1.setColor(i_c);
        addLine(s1);
    }

    //Remove the branching line segments without forming a closed polygon.
    public void applyBranchTrim() {
        double r = Epsilon.UNKNOWN_1EN6;
        boolean iflga;
        boolean iflgb;
        for (int i = 1; i <= total; i++) {
            iflga = false;
            iflgb = false;
            LineSegment si;
            si = lineSegments.get(i);
            for (int j = 1; j <= total; j++) {
                if (i != j) {
                    LineSegment sj;
                    sj = lineSegments.get(j);
                    if (OritaCalc.distance(si.getA(), sj.getA()) < r) {
                        iflga = true;
                    }
                    if (OritaCalc.distance(si.getA(), sj.getB()) < r) {
                        iflga = true;
                    }
                    if (OritaCalc.distance(si.getB(), sj.getA()) < r) {
                        iflgb = true;
                    }
                    if (OritaCalc.distance(si.getB(), sj.getB()) < r) {
                        iflgb = true;
                    }
                }
            }

            if (!iflga || !iflgb) {
                deleteLineSegment_vertex(i);
                i = 1;
            }
        }
    }

    public void deleteLineSegment_vertex(int i) {//When erasing the i-th fold line, if the end point of the fold line can also be erased, erase it.
        LineSegment s = lineSegments.get(i);

        Point pa = new Point();
        pa.set(s.getA());
        Point pb = new Point();
        pb.set(s.getB());
        deleteLine(i);

        del_V(pa, Epsilon.UNKNOWN_1EN6, Epsilon.UNKNOWN_1EN6);
        del_V(pb, Epsilon.UNKNOWN_1EN6, Epsilon.UNKNOWN_1EN6);
    }

    public void deleteLineSegment_vertex(LineSegment s) {//When erasing the i-th fold line, if the end point of the fold line can also be erased, erase it.
        Point pa = new Point();
        pa.set(s.getA());
        Point pb = new Point();
        pb.set(s.getB());
        deleteLine(s);

        del_V(pa, Epsilon.UNKNOWN_1EN6, Epsilon.UNKNOWN_1EN6);
        del_V(pb, Epsilon.UNKNOWN_1EN6, Epsilon.UNKNOWN_1EN6);
    }

    //Find and return the number of the circle closest to the point p in reverse order (the higher the number means priority)
    public int closest_circle_search_reverse_order(Point p) {
        int minrid = 0;
        double minr = 100000;
        double rtemp;
        for (int i = 0; i < circles.size(); i++) {
            Circle e_temp = new Circle();
            e_temp.set(circles.get(i));

            rtemp = p.distance(e_temp.determineCenter());
            if (minr >= rtemp) {
                minr = rtemp;
                minrid = i;
            }

            rtemp = Math.abs(p.distance(e_temp.determineCenter()) - e_temp.getR());
            if (minr >= rtemp) {
                minr = rtemp;
                minrid = i;
            }
        }

        return minrid;
    }

    // Returns the distance at the number of the circle closest to the point p
    public double closestCircleDistance(Point p) {
        double minr = 100000;
        double rtemp;
        for (Circle circle : circles) {
            Circle e_temp = new Circle();
            e_temp.set(circle);


            rtemp = p.distance(e_temp.determineCenter());
            if (minr > rtemp) {
                minr = rtemp;
            }

            rtemp = Math.abs(p.distance(e_temp.determineCenter()) - e_temp.getR());
            if (minr > rtemp) {
                minr = rtemp;
            }
        }

        return minr;
    }

    //Returns the number of the line segment closest to the point p
    public LineSegment closestLineSegmentSearch(Point p) {
        double minr = 100000;
        LineSegment sClosest = null;
        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);
            double sk = OritaCalc.determineLineSegmentDistance(p, s);
            if (minr > sk) {
                minr = sk;
                sClosest = s;
            }//Whether it is close to the handle

        }
        return sClosest;
    }

    //Find and return the number of the line segment closest to the point p from the opposite (meaning from the larger number to the smaller number)
    public LineSegment closestLineSegmentSearchReversedOrder(Point p) {
        LineSegment minLs = null;
        double minr = 100000;
        for (int i = total; i >= 1; i--) {
            LineSegment s = get(i);
            double sk = OritaCalc.determineLineSegmentDistance(p, s);
            if (minr > sk) {
                minr = sk;
                minLs = s;
            }//Whether it is close to the handle

        }
        return minLs;
    }

    //Returns the distance at the number of the line segment closest to the point p
    public double closestLineSegmentDistance(Point p) {
        double minr = 100000.0;
        for (int i = 1; i <= total; i++) {
            double sk = OritaCalc.determineLineSegmentDistance(p, get(i));
            if (minr > sk) {
                minr = sk;
            }//Whether it is close to the handle

        }
        return minr;
    }

    //Returns the distance at the number of the line segment closest to the point p. However, the polygonal line parallel to the line segment s0 is not included in the survey. That is, even if parallel polygonal lines overlap, they are not considered to be close to each other.
    public double closestLineSegmentDistanceExcludingParallel(Point p, LineSegment s0) {
        double minr = 100000.0;
        for (int i = 1; i <= total; i++) {
            if (OritaCalc.isLineSegmentParallel(get(i), s0, Epsilon.UNKNOWN_1EN4) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {

                double sk = OritaCalc.determineLineSegmentDistance(p, get(i));
                if (minr > sk) {
                    minr = sk;
                }
            }
        }
        return minr;
    }

    public Circle closestCircleMidpoint(Point p) {
        double minr = 100000.0;
        Circle closestCircle = new Circle(100000.0, 100000.0, 1.0, LineColor.BLACK_0);
        for (Circle circle : circles) {
            double ek = OritaCalc.distance_circumference(p, circle);
            if (minr > ek) {
                minr = ek;
                closestCircle = circle;
            }//Whether it is close to the circumference
        }

        return closestCircle;
    }

    public LineSegment getClosestLineSegment(Point p) {
        int minrid = 0;
        double minr = 100000.0;
        LineSegment s1 = new LineSegment(100000.0, 100000.0, 100000.0, 100000.0 + Epsilon.UNKNOWN_01);
        for (int i = 1; i <= total; i++) {
            double sk = OritaCalc.determineLineSegmentDistance(p, get(i));
            if (minr > sk) {
                minr = sk;
                minrid = i;
            }//Whether it is close to the handle
        }

        if (minrid == 0) {
            return s1;
        }

        return get(minrid);
    }

    //Returns the "end point of the line segment" closest to the point p
    public Point closestPoint(Point p) {
        Point p_return = new Point();
        p_return.set(100000.0, 100000.0);
        Point p_temp = new Point();
        for (int i = 1; i <= total; i++) {
            LineSegment si = lineSegments.get(i);
            p_temp.set(si.getA());
            if (p.distanceSquared(p_temp) < p.distanceSquared(p_return)) {
                p_return.set(p_temp.getX(), p_temp.getY());
            }
            p_temp.set(si.getB());
            if (p.distanceSquared(p_temp) < p.distanceSquared(p_return)) {
                p_return.set(p_temp.getX(), p_temp.getY());
            }

        }
        return p_return;
    }

    //Returns the "center point of the circle" closest to the point p
    public Point closestCenter(Point p) {
        Point p_return = new Point();
        p_return.set(100000.0, 100000.0);
        Point p_temp = new Point();
        for (Circle circle : circles) {
            Circle e_temp = new Circle();
            e_temp.set(circle);
            p_temp.set(e_temp.determineCenter());
            if (p.distanceSquared(p_temp) < p.distanceSquared(p_return)) {
                p_return.set(p_temp.getX(), p_temp.getY());
            }
        }
        return p_return;
    }

    //Returns the "end point of the line segment" closest to the point p. However, auxiliary live lines are not applicable
    public Point closestPointOfFoldLine(Point p) {
        Point p_return = new Point();
        p_return.set(100000.0, 100000.0);
        Point p_temp = new Point();
        for (int i = 1; i <= total; i++) {
            LineSegment si = lineSegments.get(i);
            if (si.getColor().isFoldingLine()) {
                p_temp.set(si.getA());
                if (p.distanceSquared(p_temp) < p.distanceSquared(p_return)) {
                    p_return.set(p_temp.getX(), p_temp.getY());
                }
                p_temp.set(si.getB());
                if (p.distanceSquared(p_temp) < p.distanceSquared(p_return)) {
                    p_return.set(p_temp.getX(), p_temp.getY());
                }

            }
        }
        return p_return;
    }

    public LineSegment del_V(LineSegment si, LineSegment sj) {//Erasing when two fold lines are the same color and there are no end points for other fold lines
        LineSegment.Intersection i_lineSegment_intersection_decision = OritaCalc.determineLineSegmentIntersection(si, sj, Epsilon.UNKNOWN_1EN5);

        LineSegment addLine = null;
        if (i_lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_START_OF_S1_INTERSECTS_START_OF_S2_323) {
            addLine = new LineSegment(si.getB(), sj.getB());
        }
        if (i_lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_START_OF_S1_INTERSECTS_END_OF_S2_333) {
            addLine = new LineSegment(si.getB(), sj.getA());
        }
        if (i_lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_END_OF_S1_INTERSECTS_START_OF_S2_343) {
            addLine = new LineSegment(si.getA(), sj.getB());
        }
        if (i_lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_END_OF_S1_INTERSECTS_END_OF_S2_353) {
            addLine = new LineSegment(si.getA(), sj.getA());
        }
        if (addLine == null) return null;

        LineColor i_c = LineColor.BLACK_0;
        LineColor siColor = si.getColor();
        LineColor sjColor = sj.getColor();
        switch (siColor) {
            case BLACK_0:
                switch (sjColor) {
                    case BLACK_0:
                    case RED_1:
                    case BLUE_2:
                        i_c = sjColor;
                        break;
                    case CYAN_3:
                        return null;
                    default:
                        break;
                }
                break;
            case RED_1:
                switch (sjColor) {
                    case BLACK_0:
                        i_c = LineColor.RED_1;
                        break;
                    case RED_1:
                        i_c = LineColor.RED_1;
                        break;
                    case BLUE_2:
                        i_c = LineColor.BLACK_0;
                        break;
                    case CYAN_3:
                        return null;
                    default:
                        break;
                }
                break;
            case BLUE_2:
                switch (sjColor) {
                    case BLACK_0:
                        i_c = LineColor.BLUE_2;
                        break;
                    case RED_1:
                        i_c = LineColor.BLACK_0;
                        break;
                    case BLUE_2:
                        i_c = LineColor.BLUE_2;
                        break;
                    case CYAN_3:
                        return null;
                    default:
                        break;
                }
                break;
            case CYAN_3:
                switch (sjColor) {
                    case BLACK_0:
                    case BLUE_2:
                    case RED_1:
                        return null;
                    case CYAN_3:
                        i_c = LineColor.CYAN_3;
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }

        deleteLine(si);
        deleteLine(sj);
        addLine.setColor(i_c);
        addLine(addLine);
        //p2,p1,p4 ixb_ixa,iya_iyb
        return addLine;
    }

    public void del_V_all() throws InterruptedException {
        PointLineMap map = new PointLineMap(lineSegments);
        for(Point p : map.getPoints()) {
            List<LineSegment> lines = map.getLines(p);
            if(lines.size() == 2) {
                LineSegment si = lines.get(0);
                LineSegment sj = lines.get(1);
                if (si.getColor() == sj.getColor() && si.getColor() != LineColor.CYAN_3) {
                    LineSegment new_line = del_V(si, sj);
                    if(new_line != null) {
                        map.replaceLine(si, new_line);
                        map.replaceLine(sj, new_line);
                    }
                }
            }
        }
    }

    public void del_V_all_cc() throws InterruptedException {
        PointLineMap map = new PointLineMap(lineSegments);
        for(Point p : map.getPoints()) {
            List<LineSegment> lines = map.getLines(p);
            if(lines.size() == 2) {
                LineSegment si = lines.get(0);
                LineSegment sj = lines.get(1);
                LineSegment new_line = del_V(si, sj);
                if(new_line != null) {
                    map.replaceLine(si, new_line);
                    map.replaceLine(sj, new_line);
                }
            }
        }
    }

    public boolean del_V(Point p, double hikiyose_hankei, double r) {
        Point q = new Point();
        q.set(closestPoint(p));//q is the end point closer to the point p
        if (q.distanceSquared(p) > hikiyose_hankei * hikiyose_hankei) {
            return false;
        }

        if (vertex_syuui_numLines_for_del_V(q, r) == 2) {
            int ix, iy;
            ix = i_s[0];
            iy = i_s[1];

            LineSegment lix = lineSegments.get(ix);
            LineSegment liy = lineSegments.get(iy);
            boolean i_decision;
            i_decision = false;//If i_hantei is 1, the two line segments do not overlap and are connected in a straight line.
            LineSegment.Intersection i_lineSegment_intersection_decision;
            i_lineSegment_intersection_decision = OritaCalc.determineLineSegmentIntersection(lix, liy, Epsilon.UNKNOWN_1EN6);

            if (i_lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_START_OF_S1_INTERSECTS_START_OF_S2_323) {
                i_decision = true;
            }
            if (i_lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_START_OF_S1_INTERSECTS_END_OF_S2_333) {
                i_decision = true;
            }
            if (i_lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_END_OF_S1_INTERSECTS_START_OF_S2_343) {
                i_decision = true;
            }
            if (i_lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_END_OF_S1_INTERSECTS_END_OF_S2_353) {
                i_decision = true;
            }

            Logger.info("i_lineSegment_intersection_decision=" + i_lineSegment_intersection_decision + "---tyouten_syuui_sensuu_for_del_V(q,r)_" + vertex_syuui_numLines_for_del_V(q, r));
            if (!i_decision) {
                return false;
            }


            if (lix.getColor() != liy.getColor()) {
                return false;
            }//If the two are not the same color, do not carry out

            LineColor i_c;
            i_c = lix.getColor();

            LineSegment s_ixb_iyb = new LineSegment(lix.getB(), liy.getB(), i_c);
            LineSegment s_ixb_iya = new LineSegment(lix.getB(), liy.getA(), i_c);
            LineSegment s_ixa_iyb = new LineSegment(lix.getA(), liy.getB(), i_c);
            LineSegment s_ixa_iya = new LineSegment(lix.getA(), liy.getA(), i_c);


            if (i_lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_START_OF_S1_INTERSECTS_START_OF_S2_323) {
                deleteLine(iy);
                deleteLine(ix);
                addLine(s_ixb_iyb);
            }//p2,p1,p4 ixb_ixa,iya_iyb
            if (i_lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_START_OF_S1_INTERSECTS_END_OF_S2_333) {
                deleteLine(iy);
                deleteLine(ix);
                addLine(s_ixb_iya);
            }//p2,p1,p3 ixb_ixa,iyb_iya
            if (i_lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_END_OF_S1_INTERSECTS_START_OF_S2_343) {
                deleteLine(iy);
                deleteLine(ix);
                addLine(s_ixa_iyb);
            }//p1,p2,p4 ixa_ixb,iya_iyb
            if (i_lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_END_OF_S1_INTERSECTS_END_OF_S2_353) {
                deleteLine(iy);
                deleteLine(ix);
                addLine(s_ixa_iya);
            }//p1,p2,p3 ixa_ixb,iyb_iya
        }

        return false;
    }


    public boolean del_V_cc(Point p, double hikiyose_hankei, double r) {//2つの折線の色が違った場合カラーチェンジして、点削除する。黒赤は赤赤、黒青は青青、青赤は黒にする
        Point q = new Point();
        q.set(closestPoint(p));//qは点pに近い方の端点
        if (q.distanceSquared(p) > hikiyose_hankei * hikiyose_hankei) {
            return false;
        }

        if (vertex_syuui_numLines_for_del_V(q, r) == 2) {
            int ix = i_s[0];
            int iy = i_s[1];
            LineSegment lix = lineSegments.get(ix);
            LineSegment liy = lineSegments.get(iy);
            boolean i_decision = false;//i_hanteiは１なら2線分は重ならず、直線状に繋がっている
            LineSegment.Intersection lineSegment_intersection_decision;
            lineSegment_intersection_decision = OritaCalc.determineLineSegmentIntersection(lix, liy, Epsilon.UNKNOWN_1EN6);

            if (lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_START_OF_S1_INTERSECTS_START_OF_S2_323) {
                i_decision = true;
            }
            if (lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_START_OF_S1_INTERSECTS_END_OF_S2_333) {
                i_decision = true;
            }
            if (lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_END_OF_S1_INTERSECTS_START_OF_S2_343) {
                i_decision = true;
            }
            if (lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_END_OF_S1_INTERSECTS_END_OF_S2_353) {
                i_decision = true;
            }
            if (!i_decision) {
                return false;
            }

            if ((lix.getColor() == LineColor.BLACK_0) && (liy.getColor() == LineColor.BLACK_0)) {
                lix.setColor(LineColor.BLACK_0);
                liy.setColor(LineColor.BLACK_0);
            }
            if ((lix.getColor() == LineColor.BLACK_0) && (liy.getColor() == LineColor.RED_1)) {
                lix.setColor(LineColor.RED_1);
                liy.setColor(LineColor.RED_1);
            }
            if ((lix.getColor() == LineColor.BLACK_0) && (liy.getColor() == LineColor.BLUE_2)) {
                lix.setColor(LineColor.BLUE_2);
                liy.setColor(LineColor.BLUE_2);
            }
            if ((lix.getColor() == LineColor.BLACK_0) && (liy.getColor() == LineColor.CYAN_3)) {
                return false;
            }

            if ((lix.getColor() == LineColor.RED_1) && (liy.getColor() == LineColor.BLACK_0)) {
                lix.setColor(LineColor.RED_1);
                liy.setColor(LineColor.RED_1);
            }
            if ((lix.getColor() == LineColor.RED_1) && (liy.getColor() == LineColor.RED_1)) {
                lix.setColor(LineColor.RED_1);
                liy.setColor(LineColor.RED_1);
            }
            if ((lix.getColor() == LineColor.RED_1) && (liy.getColor() == LineColor.BLUE_2)) {
                lix.setColor(LineColor.BLACK_0);
                liy.setColor(LineColor.BLACK_0);
            }
            if ((lix.getColor() == LineColor.RED_1) && (liy.getColor() == LineColor.CYAN_3)) {
                return false;
            }

            if ((lix.getColor() == LineColor.BLUE_2) && (liy.getColor() == LineColor.BLACK_0)) {
                lix.setColor(LineColor.BLUE_2);
                liy.setColor(LineColor.BLUE_2);
            }
            if ((lix.getColor() == LineColor.BLUE_2) && (liy.getColor() == LineColor.RED_1)) {
                lix.setColor(LineColor.BLACK_0);
                liy.setColor(LineColor.BLACK_0);
            }
            if ((lix.getColor() == LineColor.BLUE_2) && (liy.getColor() == LineColor.BLUE_2)) {
                lix.setColor(LineColor.BLUE_2);
                liy.setColor(LineColor.BLUE_2);
            }
            if ((lix.getColor() == LineColor.BLUE_2) && (liy.getColor() == LineColor.CYAN_3)) {
                return false;
            }

            if ((lix.getColor() == LineColor.CYAN_3) && (liy.getColor() == LineColor.BLACK_0)) {
                return false;
            }
            if ((lix.getColor() == LineColor.CYAN_3) && (liy.getColor() == LineColor.RED_1)) {
                return false;
            }
            if ((lix.getColor() == LineColor.CYAN_3) && (liy.getColor() == LineColor.BLUE_2)) {
                return false;
            }
            if ((lix.getColor() == LineColor.CYAN_3) && (liy.getColor() == LineColor.CYAN_3)) {
                lix.setColor(LineColor.CYAN_3);
                liy.setColor(LineColor.CYAN_3);
            }


            LineColor i_c = lix.getColor();

            LineSegment s_ixb_iyb = new LineSegment(lix.getB(), liy.getB(), i_c);
            LineSegment s_ixb_iya = new LineSegment(lix.getB(), liy.getA(), i_c);
            LineSegment s_ixa_iyb = new LineSegment(lix.getA(), liy.getB(), i_c);
            LineSegment s_ixa_iya = new LineSegment(lix.getA(), liy.getA(), i_c);

            switch (lineSegment_intersection_decision) {
                case PARALLEL_START_OF_S1_INTERSECTS_START_OF_S2_323:
                    deleteLine(iy);
                    deleteLine(ix);
                    addLine(s_ixb_iyb);
                    break;
//p2,p1,p4 ixb_ixa,iya_iyb
                case PARALLEL_START_OF_S1_INTERSECTS_END_OF_S2_333:
                    deleteLine(iy);
                    deleteLine(ix);
                    addLine(s_ixb_iya);
                    break;
//p2,p1,p3 ixb_ixa,iyb_iya
                case PARALLEL_END_OF_S1_INTERSECTS_START_OF_S2_343:
                    deleteLine(iy);
                    deleteLine(ix);
                    addLine(s_ixa_iyb);
                    break;
//p1,p2,p4 ixa_ixb,iya_iyb
                case PARALLEL_END_OF_S1_INTERSECTS_END_OF_S2_353:
                    deleteLine(iy);
                    deleteLine(ix);
                    addLine(s_ixa_iya);
                    break;
                default:
                    break;
            }
        }

        return false;
    }

    //If the end point of the line segment closest to the point p and the end point closer to the point p is the apex, how many line segments are out (the number of line segments with an end point within the apex and r)
    // for del_V Function of
    public int vertex_syuui_numLines_for_del_V(Point p, double r) {//del_V用の関数
        Point q = new Point();
        q.set(closestPoint(p));//qは点pに近い方の端点
        Point p_temp = new Point();


        int i_return = 0;
        int i_temp = 1;//ここのi_tempはi_temp=1-i_tempの形でつかうので、0,1,0,1,0,1,,,という風に変化していく
        for (int i = 1; i <= total; i++) {
            LineSegment si = lineSegments.get(i);
            p_temp.set(si.getA());
            if (q.distanceSquared(si.getB()) < q.distanceSquared(si.getA())) {
                p_temp.set(si.getB());
            }
            if (q.distanceSquared(p_temp) < r * r) {
                i_temp = 1 - i_temp;
                i_s[i_temp] = i;
                i_return = i_return + 1;
            }

        }

        return i_return;
    }

    //If the end point of the line segment closest to the point p and the end point closer to the point p is the vertex, how many line segments are present (the number of line segments having an end point within the vertex and r).
    public int vertex_surrounding_lineCount(Point p, double r) {
        Point q = new Point();
        q.set(closestPoint(p));//qは点pに近い方の端点
        Point p_temp = new Point();

        int i_return = 0;

        for (int i = 1; i <= total; i++) {
            LineSegment si = lineSegments.get(i);
            p_temp.set(si.getA());
            if (q.distanceSquared(si.getB()) < q.distanceSquared(si.getA())) {
                p_temp.set(si.getB());
            }

            if (q.distanceSquared(p_temp) < r * r) {
                i_return++;
            }
        }

        return i_return;
    }

    //If the end point of the line segment closest to the point p and the end point closer to the point p is the vertex, how many red line segments appear (the number of line segments having an end point within the vertex and r).
    public int vertex_surrounding_lineCount_red(Point p, double r) {
        Point q = new Point();
        q.set(closestPoint(p));//q is the end point closer to the point p
        Point p_temp = new Point();

        int i_return = 0;

        for (int i = 1; i <= total; i++) {
            LineSegment si = lineSegments.get(i);
            p_temp.set(si.getA());
            if (q.distanceSquared(si.getB()) < q.distanceSquared(si.getA())) {
                p_temp.set(si.getB());
            }
            if (q.distanceSquared(p_temp) < r * r) {
                if (si.getColor() == LineColor.RED_1) {
                    i_return++;
                }
            }
        }

        return i_return;
    }

    //--------------------------------------------
    //If the end point of the line segment closest to the point p and the end point closer to the point p is the vertex, how many blue line segments appear (the number of line segments having an end point within the vertex and r).
    public int vertex_surrounding_lineCount_blue(Point p, double r) {
        Point q = new Point();
        q.set(closestPoint(p));//qは点pに近い方の端点
        Point p_temp = new Point();

        int i_return = 0;

        for (int i = 1; i <= total; i++) {
            LineSegment si = lineSegments.get(i);
            p_temp.set(si.getA());
            if (q.distanceSquared(si.getB()) < q.distanceSquared(si.getA())) {
                p_temp.set(si.getB());
            }
            if (q.distanceSquared(p_temp) < r * r) {
                if (si.getColor() == LineColor.BLUE_2) {
                    i_return++;
                }
            }

        }

        return i_return;
    }

    //--------------------------------------------
    //If the end point of the line segment closest to the point p and the end point closer to the point p is the vertex, how many black line segments appear (the number of line segments having an end point within the vertex and r).
    public int vertex_surrounding_lineCount_black(Point p, double r) {
        Point q = new Point();
        q.set(closestPoint(p));//qは点pに近い方の端点
        Point p_temp = new Point();

        int i_return = 0;

        for (int i = 1; i <= total; i++) {
            LineSegment si = lineSegments.get(i);
            p_temp.set(si.getA());
            if (q.distanceSquared(si.getB()) < q.distanceSquared(si.getA())) {
                p_temp.set(si.getB());
            }
            if (q.distanceSquared(p_temp) < r * r) {
                if (si.getColor() == LineColor.BLACK_0) {
                    i_return++;
                }
            }
        }

        return i_return;
    }

    //--------------------------------------------
    //If the end point of the line segment closest to the point p and the end point closer to the point p is the vertex, how many auxiliary live lines are present (the number of line segments having an end point within the vertex and r).
    public int vertex_surrounding_lineCount_auxiliary_live_line(Point p, double r) {
        Point q = new Point();
        q.set(closestPoint(p));//qは点pに近い方の端点
        Point p_temp = new Point();


        int i_return = 0;

        for (int i = 1; i <= total; i++) {
            LineSegment si = lineSegments.get(i);
            p_temp.set(si.getA());
            if (q.distanceSquared(si.getB()) < q.distanceSquared(si.getA())) {
                p_temp.set(si.getB());
            }
            if (q.distanceSquared(p_temp) < r * r) {
                if (!si.getColor().isFoldingLine()) {
                    i_return = i_return + 1;
                }
            }
        }

        return i_return;
    }

    //Divide the polygonal line i by the projection of the point p. However, if the projection of point p is considered to be the same as the end point of any polygonal line, nothing is done.
    public void applyLineSegmentDivide(Point p, int i) {//何もしない=0,分割した=1
        LineSegment s = lineSegments.get(i);

        LineSegment mts = new LineSegment(s.getA(), s.getB());//mtsは点pに最も近い線分

        //直線t上の点pの影の位置（点pと最も近い直線t上の位置）を求める。public Ten oc.kage_motome(Tyokusen t,Ten p){}
        //線分を含む直線を得る public Tyokusen oc.Senbun2Tyokusen(Senbun s){}
        Point pk = new Point();
        pk.set(OritaCalc.findProjection(OritaCalc.lineSegmentToStraightLine(mts), p));//pkは点pの（線分を含む直線上の）影
        //線分の分割-----------------------------------------
        applyLineSegmentDivide(s, pk);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
    }

    public void move(double dx, double dy) {//折線集合全体の位置を移動する。
        Point temp_a = new Point();
        Point temp_b = new Point();
        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);
            temp_a.set(s.getA());
            temp_b.set(s.getB());
            temp_a.setX(temp_a.getX() + dx);
            temp_a.setY(temp_a.getY() + dy);
            temp_b.setX(temp_b.getX() + dx);
            temp_b.setY(temp_b.getY() + dy);
            s.setA(temp_a);
            s.setB(temp_b);
        }

        for (Circle circle : circles) {
            Circle e_temp = new Circle();
            e_temp.set(circle);

            e_temp.setX(e_temp.getX() + dx);
            e_temp.setY(e_temp.getY() + dy);
            circle.set(e_temp);
        }
    }

    public void move(Point ta, Point tb, Point tc, Point td) {//Move the position of the entire set of polygonal lines.
        double d = OritaCalc.angle(ta, tb, tc, td);
        double r = tc.distance(td) / ta.distance(tb);

        double dx = tc.getX() - ta.getX();
        double dy = tc.getY() - ta.getY();

        Point temp_a = new Point();
        Point temp_b = new Point();
        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);
            temp_a.set(OritaCalc.point_rotate(ta, s.getA(), d, r));
            temp_b.set(OritaCalc.point_rotate(ta, s.getB(), d, r));
            temp_a.setX(temp_a.getX() + dx);
            temp_a.setY(temp_a.getY() + dy);
            temp_b.setX(temp_b.getX() + dx);
            temp_b.setY(temp_b.getY() + dy);
            s.setA(temp_a);
            s.setB(temp_b);
        }
    }

    public void check1() {
        Check1LineSegment.clear();
        unselect_all();
        for (int i = 1; i <= total - 1; i++) {
            LineSegment si = lineSegments.get(i);

            if (si.getColor() != LineColor.CYAN_3) {

                for (int j = i + 1; j <= total; j++) {
                    LineSegment sj = lineSegments.get(j);//r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度
                    if (sj.getColor() != LineColor.CYAN_3) {

                        LineSegment si1 = new LineSegment();
                        si1.set(si);
                        LineSegment sj1 = new LineSegment();
                        sj1.set(sj);

                        LineSegment.Intersection intersection = OritaCalc.determineLineSegmentIntersection(si, sj, Epsilon.UNKNOWN_0001, Epsilon.PARALLEL);
                        switch (intersection) {
                            case PARALLEL_EQUAL_31:
                            case PARALLEL_START_OF_S1_CONTAINS_START_OF_S2_321:
                            case PARALLEL_START_OF_S2_CONTAINS_START_OF_S1_322:
                            case PARALLEL_START_OF_S1_CONTAINS_END_OF_S2_331:
                            case PARALLEL_END_OF_S2_CONTAINS_START_OF_S1_332:
                            case PARALLEL_END_OF_S1_CONTAINS_START_OF_S2_341:
                            case PARALLEL_START_OF_S2_CONTAINS_END_OF_S1_342:
                            case PARALLEL_END_OF_S1_CONTAINS_END_OF_S2_351:
                            case PARALLEL_END_OF_S2_CONTAINS_END_OF_S1_352:
                                Check1LineSegment.add(si1);
                                Check1LineSegment.add(sj1);   /* set_select(i,2);set_select(j,2); */
                                break;
                            default:
                                break;
                        }
                        if (intersection.isContainedInside()) {
                            Check1LineSegment.add(si1);
                            Check1LineSegment.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }

                    }
                }
            }
        }
    }

    public boolean fix1() {//Returns 0 if nothing is done, 1 if something is modified.
        unselect_all();
        for (int i = 1; i <= total - 1; i++) {
            LineSegment si = lineSegments.get(i);
            if (si.getColor() != LineColor.CYAN_3) {
                for (int j = i + 1; j <= total; j++) {
                    LineSegment sj = lineSegments.get(j);//r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度
                    if (sj.getColor() != LineColor.CYAN_3) {
                        //T字型交差
                        LineSegment.Intersection intersection = OritaCalc.determineLineSegmentIntersection(si, sj, Epsilon.UNKNOWN_0001, Epsilon.PARALLEL);
                        switch (intersection) {
                            case PARALLEL_EQUAL_31:
                                si.setColor(sj.getColor());
                                deleteLine(j);
                                return true;
                            case PARALLEL_START_OF_S1_CONTAINS_START_OF_S2_321:
                            case PARALLEL_START_OF_S2_CONTAINS_START_OF_S1_322:
                            case PARALLEL_START_OF_S1_CONTAINS_END_OF_S2_331:
                            case PARALLEL_END_OF_S2_CONTAINS_START_OF_S1_332:
                            case PARALLEL_END_OF_S1_CONTAINS_START_OF_S2_341:
                            case PARALLEL_START_OF_S2_CONTAINS_END_OF_S1_342:
                            case PARALLEL_END_OF_S1_CONTAINS_END_OF_S2_351:
                            case PARALLEL_END_OF_S2_CONTAINS_END_OF_S1_352:
                                si.setSelected(2);
                                sj.setSelected(2);
                                break;
                            default:
                                break;
                        }
                        if (intersection.isContainedInside()) {
                            si.setSelected(2);
                            sj.setSelected(2);
                        }
                    }
                }
            }
        }
        return false;
    }

    public void check2() {
        Check2LineSegment.clear();

        unselect_all();
        for (int i = 1; i <= total - 1; i++) {
            LineSegment si = lineSegments.get(i);
            if (si.getColor() != LineColor.CYAN_3) {

                for (int j = i + 1; j <= total; j++) {
                    LineSegment sj = lineSegments.get(j);//r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度
                    if (sj.getColor() != LineColor.CYAN_3) {

                        LineSegment si1 = new LineSegment();
                        si1.set(si);
                        LineSegment sj1 = new LineSegment();
                        sj1.set(sj);

                        //T-shaped intersection
                        LineSegment.Intersection intersection = OritaCalc.determineLineSegmentIntersectionSweet(si, sj, Epsilon.UNKNOWN_0001, Epsilon.PARALLEL);
                        switch (intersection) {
                            case INTERSECTS_TSHAPE_S1_VERTICAL_BAR_25:
                            case INTERSECTS_TSHAPE_S1_VERTICAL_BAR_26:
                            case INTERSECTS_TSHAPE_S2_VERTICAL_BAR_27:
                            case INTERSECTS_TSHAPE_S2_VERTICAL_BAR_28:
                                Check2LineSegment.add(si1);
                                Check2LineSegment.add(sj1);   /* set_select(i,2);set_select(j,2); */
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
    }

    public void fix2() {
        unselect_all();
        QuadTree qt = new QuadTree(new LineSegmentListAdapter(lineSegments));
        for (int i = 1; i <= total - 1; i++) {
            LineSegment si = lineSegments.get(i);
            if (si.getColor() != LineColor.CYAN_3) {

                for (int j : qt.getPotentialCollision(i)) {
                    LineSegment sj = lineSegments.get(j);
                    if (sj.getColor() != LineColor.CYAN_3) {
                        //T-intersection
                        //折線iをその点pの影で分割する。ただし、点pの影がどれか折線の端点と同じとみなされる場合は何もしない。
                        //r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度
                        LineSegment.Intersection intersection = OritaCalc.determineLineSegmentIntersectionSweet(si, sj, Epsilon.UNKNOWN_0001, Epsilon.PARALLEL);
                        switch (intersection) {
                            case INTERSECTS_TSHAPE_S1_VERTICAL_BAR_25:
                                applyLineSegmentDivide(si.getA(), j);
                                qt.grow(1);
                                break;
                            case INTERSECTS_TSHAPE_S1_VERTICAL_BAR_26:
                                applyLineSegmentDivide(si.getB(), j);
                                qt.grow(1);
                                break;
                            case INTERSECTS_TSHAPE_S2_VERTICAL_BAR_27:
                                applyLineSegmentDivide(sj.getA(), i);
                                qt.grow(1);
                                break;
                            case INTERSECTS_TSHAPE_S2_VERTICAL_BAR_28:
                                applyLineSegmentDivide(sj.getB(), i);
                                qt.grow(1);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
    }

    // ***********************************ppppppppppppqqqqqq
    //Cirには1番目からcir_size()番目までデータが入っている

    public int numCircles() {
        return circles.size();
    }

    public Iterable<Circle> getCircles() {
        return circles;
    }

    public Iterable<LineSegment> getCheck1LineSegments() {
        return Check1LineSegment;
    }

    public Iterable<LineSegment> getCheck2LineSegments() {
        return Check2LineSegment;
    }

    public Iterable<LineSegment> getCheck3LineSegments() {
        return Check3LineSegment;
    }

    public Queue<LineSegment> getCheck4LineSegments() {
        return Check4LineSegment;
    }

    public void check3() {//Check the number of lines around the vertex
        double r = Epsilon.UNKNOWN_1EN4;
        Check3LineSegment.clear();
        unselect_all();
        for (int i = 1; i <= total; i++) {
            LineSegment si = lineSegments.get(i);
            if (si.getColor() != LineColor.CYAN_3) {
                Point p = new Point();
                int tss;    //頂点の周りの折線の数。　tss%2==0 偶数、==1 奇数
                int tss_red;    //Number of mountain fold lines around the vertex 。
                int tss_blue;    //頂点の周りの谷折線の数。
                int tss_black;    //頂点の周りの境界線の数。
                int tss_hojyo_kassen;    //頂点の周りの補助活線の数。

                //-----------------
                p.set(si.getA());
                tss = vertex_surrounding_lineCount(p, r);
                tss_red = vertex_surrounding_lineCount_red(p, r);
                tss_blue = vertex_surrounding_lineCount_blue(p, r);
                tss_black = vertex_surrounding_lineCount_black(p, r);
                tss_hojyo_kassen = vertex_surrounding_lineCount_auxiliary_live_line(p, r);

                if ((tss_black != 0) && (tss_black != 2)) {//黒線がないか2本以外の場合はおかしい。
                    Check3LineSegment.add(new LineSegment(p, p));//set_select(i,2);
                }

                if (tss_black == 0) {//黒線がない場合
                    if (tss - tss_hojyo_kassen == tss_red + tss_blue) {//（前提として境界は黒で、山谷未設定折線はないこと。）頂点周囲に赤か青しかない。つまり、用紙内部の点

                        if (Math.abs(tss_red - tss_blue) != 2) {//用紙内部の点で前川定理を満たさないのはダメ
                            Check3LineSegment.add(new LineSegment(p, p));//set_select(i,2);
                        }
                    }
                    if (!extended_fushimi_decide_inside(p)) {
                        Check3LineSegment.add(new LineSegment(p, p));//set_select(i,2);
                    }
                }

                if (tss_black == 2) {//黒線が2本の場合
                    if (!extended_fushimi_decide_sides(p)) {
                        Check3LineSegment.add(new LineSegment(p, p));//set_select(i,2);
                    }
                }

                //-----------------
                p.set(si.getB());
                tss = vertex_surrounding_lineCount(p, r);
                tss_red = vertex_surrounding_lineCount_red(p, r);
                tss_blue = vertex_surrounding_lineCount_blue(p, r);
                tss_black = vertex_surrounding_lineCount_black(p, r);
                tss_hojyo_kassen = vertex_surrounding_lineCount_auxiliary_live_line(p, r);

                //-----------------
                if ((tss_black != 0) && (tss_black != 2)) {//黒線がないか2本以外の場合はおかしい。
                    Check3LineSegment.add(new LineSegment(p, p));//set_select(i,2);
                }

                if (tss_black == 0) {//黒線がない場合
                    if (tss - tss_hojyo_kassen == tss_red + tss_blue) {//（前提として境界は黒で、山谷未設定折線はないこと。）頂点周囲に赤か青しかない。つまり、用紙内部の点
                        if (Math.abs(tss_red - tss_blue) != 2) {//用紙内部の点で前川定理を満たさないのはダメ
                            Check3LineSegment.add(new LineSegment(p, p));//set_select(i,2);
                        }
                    }
                    if (!extended_fushimi_decide_inside(p)) {
                        Check3LineSegment.add(new LineSegment(p, p));//set_select(i,2);
                    }
                }

                if (tss_black == 2) {//黒線が2本の場合
                    if (!extended_fushimi_decide_sides(p)) {
                        Check3LineSegment.add(new LineSegment(p, p));//set_select(i,2);
                    }
                }
            }
        }
    }

    public void check4() throws InterruptedException {//Check the number of lines around the apex
        Check4LineSegment.clear();
        unselect_all();

        PointLineMap map = new PointLineMap(lineSegments);
        Logger.info("check4_T_size() = " + map.getPoints().size());

        ExecutorService service = Executors.newWorkStealingPool();

        //Selection of whether the place to be checked can be folded flat
        for (Point point : map.getPoints()) {
            service.submit(() -> {
                Point p = new Point(point);
                try {
                    if (!i_flat_ok(p, map.getLines(point))) {
                        Check4LineSegment.add(new LineSegment(p, p));
                    }
                } catch (InterruptedException e) {
                    // finish thread.
                }
            });
        }

        // Done adding tasks, shut down ExecutorService
        service.shutdown();
        try {
            if (!service.awaitTermination(60, TimeUnit.SECONDS)) {
                throw new RuntimeException("Check cAMV did not finish!");
            }
        } catch (InterruptedException e) {
            service.shutdownNow();
            if (!service.awaitTermination(60, TimeUnit.SECONDS)) {
                throw new RuntimeException("Check cAMV did not exit!");
            }
        }
    }

    private boolean i_flat_ok(Point p, List<LineSegment> list) throws InterruptedException {//Foldable flat = 1
        //If the end point of the line segment closest to the point p and the end point closer to the point p is the apex, how many line segments are present (the number of line segments having an end point within the apex and r).
        int i_tss_red = 0;
        int i_tss_blue = 0;
        int i_tss_black = 0;

        SortingBox<LineSegment> nbox = new SortingBox<>();

        for (LineSegment s : list) {
            if (s.getColor() == LineColor.RED_1) {
                i_tss_red++;
            } else if (s.getColor() == LineColor.BLUE_2) {
                i_tss_blue++;
            } else if (s.getColor() == LineColor.BLACK_0) {
                i_tss_black++;
            }

            //Put a polygonal line with p as the end point in Narabebako
            if (s.getColor().isFoldingLine()) { //Auxiliary live lines are excluded at this stage
                if (p.distance(s.getA()) < Epsilon.FLAT) {
                    nbox.addByWeight(s, OritaCalc.angle(s.getA(), s.getB()));
                } else if (p.distance(s.getB()) < Epsilon.FLAT) {
                    nbox.addByWeight(s, OritaCalc.angle(s.getB(), s.getA()));
                }
            }
        }

        // Judgment start-------------------------------------------
        if ((i_tss_black != 0) && (i_tss_black != 2)) {//It is strange if there are no black lines or if there are other than two lines.
            return false;
        }

        if (i_tss_black == 0) {//If there is no black line
            if (Math.abs(i_tss_red - i_tss_blue) != 2) {//Do not satisfy Maekawa's theorem in terms of the inside of the paper
                return false;
            }

            return extended_fushimi_decide_inside(p, nbox);
        }

        //When there are two black lines
        return extended_fushimi_decide_sides(p, nbox);
    }

    //Point p に最も近い用紙辺部の端点が拡張伏見定理を満たすか判定
    public boolean extended_fushimi_decide_sides(Point p) {//return　0=満たさない、　1=満たす。　
        Point t1 = new Point();
        t1.set(closestPointOfFoldLine(p));//点pに最も近い、「線分の端点」を返すori_s.closestPointは近い点がないと p_return.set(100000.0,100000.0)と返してくる

        //t1を端点とする折線をNarabebakoに入れる
        SortingBox<LineSegment> nbox = new SortingBox<>();
        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);
            if (s.getColor().isFoldingLine()) { //この段階で補助活線は除く
                if (t1.distance(s.getA()) < Epsilon.FLAT) {
                    nbox.addByWeight(s, OritaCalc.angle(s.getA(), s.getB()));
                } else if (t1.distance(s.getB()) < Epsilon.FLAT) {
                    nbox.addByWeight(s, OritaCalc.angle(s.getB(), s.getA()));
                }
            }
        }

        return extended_fushimi_decide_sides(p, nbox);
    }

    // ---------------------------------
    public boolean extended_fushimi_decide_sides(Point p, SortingBox<LineSegment> nbox) {//return　0=満たさない、　1=満たす。　
        if (nbox.getTotal() == 2) {//t1を端点とする折線の数が2のとき
            if (nbox.getValue(1).getColor() != LineColor.BLACK_0) {//1本目が黒でないならダメ
                return false;
            }
            //2本目が黒でないならダメ
            return nbox.getValue(2).getColor() == LineColor.BLACK_0;

            //2本の線種が黒黒
        }


        //以下はt1を端点とする折線の数が3以上の偶数のとき

        //fushimi_decision_angle_goukei=360.0;


        //辺の折線が,ならべばこnbox,の一番目と最後の順番になるようにする。

        int saisyo_ni_suru = -10;
        for (int i = 1; i <= nbox.getTotal() - 1; i++) {
            if ((nbox.getValue(i).getColor() == LineColor.BLACK_0) &&
                    (nbox.getValue(i + 1).getColor() == LineColor.BLACK_0)) {
                saisyo_ni_suru = i + 1;
            }
        }

        if ((nbox.getValue(nbox.getTotal()).getColor() == LineColor.BLACK_0) &&
                (nbox.getValue(1).getColor() == LineColor.BLACK_0)) {
            saisyo_ni_suru = 1;
        }

        if (saisyo_ni_suru < 0) {
            return false;
        }

        for (int i = 1; i <= saisyo_ni_suru - 1; i++) {
            nbox.shift();
        }

        //ならべばこnbox,の一番目の折線がx軸となす角度が0になるようにする。
        SortingBox<LineSegment> nbox1 = new SortingBox<>();

        double sasihiku_kakudo = nbox.getWeight(1);

        for (int i = 1; i <= nbox.getTotal(); i++) {
            WeightedValue<LineSegment> i_d_0 = new WeightedValue<>();
            i_d_0.set(nbox.getWeightedValue(i));

            i_d_0.setWeight(OritaCalc.angle_between_0_360(i_d_0.getWeight() - sasihiku_kakudo));
            nbox1.add(i_d_0);
        }

        nbox.set(nbox1);

        while (nbox.getTotal() > 2) {//点から出る折線の数が2になるまで実行する
            nbox1.set(extended_fushimi_determine_sides_theorem(nbox));
            if (nbox1.getTotal() == nbox.getTotal()) {
                return false;
            }
            nbox.set(nbox1);
        }

        return true;
    }

    //Obtain SortingBox<Integer> with a polygonal line starting at b. They are arranged in ascending order of angle with the line segment ba.
    public SortingBox<LineSegment> get_SortingBox_of_vertex_b_surrounding_foldLine(Point a, Point b) {
        SortingBox<LineSegment> r_nbox = new SortingBox<>();

        //Put a polygonal line with b as the end point in Narabebako

        for (int i = 1; i <= getTotal(); i++) {
            LineSegment si = lineSegments.get(i);
            if (si.getColor().isFoldingLine()) { //Auxiliary live lines are excluded at this stage
                if (b.distance(si.getA()) < Epsilon.FLAT) {
                    r_nbox.addByWeight(si, OritaCalc.angle(b, a, si.getA(), si.getB()));
                } else if (b.distance(si.getB()) < Epsilon.FLAT) {
                    r_nbox.addByWeight(si, OritaCalc.angle(b, a, si.getB(), si.getA()));
                }
            }
        }

        return r_nbox;
    }

    //Operation to make three angles adjacent to each other at the points of the side into one angle or to cut the corner of the side like the extended Fushimi theorem
    public SortingBox<LineSegment> extended_fushimi_determine_sides_theorem(SortingBox<LineSegment> nbox0) {
        SortingBox<LineSegment> nbox1 = new SortingBox<>();

        double angle_min = 10000.0;
        double temp_angle;

        //Find the minimum angle angle_min
        for (int k = 1; k <= nbox0.getTotal() - 1; k++) {//kは角度の順番
            temp_angle = nbox0.getWeight(k + 1) - nbox0.getWeight(k);
            if (temp_angle < angle_min) {
                angle_min = temp_angle;
            }
        }

        temp_angle = nbox0.getWeight(2) - nbox0.getWeight(1);
        if (Math.abs(temp_angle - angle_min) < Epsilon.FLAT) {// 折線を1つ減らせる条件に適合したので、新たにnbox1を作ってリターンする。
            for (int i = 2; i <= nbox0.getTotal(); i++) {
                WeightedValue<LineSegment> i_d_0 = new WeightedValue<>();
                i_d_0.set(nbox0.getWeightedValue(i));
                nbox1.add(i_d_0);
            }
            return nbox1;
        }

        temp_angle = nbox0.getWeight(nbox0.getTotal()) - nbox0.getWeight(nbox0.getTotal() - 1);
        if (Math.abs(temp_angle - angle_min) < Epsilon.FLAT) {// 折線を1つ減らせる条件に適合したので、新たにnbox1を作ってリターンする。
            for (int i = 1; i <= nbox0.getTotal() - 1; i++) {
                WeightedValue<LineSegment> i_d_0 = new WeightedValue<>();
                i_d_0.set(nbox0.getWeightedValue(i));
                nbox1.add(i_d_0);
            }
            return nbox1;
        }

        for (int k = 2; k <= nbox0.getTotal() - 2; k++) {//kは角度の順番
            temp_angle = nbox0.getWeight(k + 1) - nbox0.getWeight(k);
            if (Math.abs(temp_angle - angle_min) < Epsilon.FLAT) {
                if (nbox0.getValue(k).getColor() != nbox0.getValue(k + 1).getColor()) {//この場合に隣接する３角度を1つの角度にする
                    // 折線を2つ減らせる条件に適合したので、新たにnbox1を作ってリターンする。

                    for (int i = 1; i <= k - 1; i++) {
                        WeightedValue<LineSegment> i_d_0 = new WeightedValue<>();
                        i_d_0.set(nbox0.getWeightedValue(i));
                        nbox1.add(i_d_0);
                    }

                    for (int i = k + 2; i <= nbox0.getTotal(); i++) {
                        WeightedValue<LineSegment> i_d_0 = new WeightedValue<>();
                        i_d_0.set(nbox0.getWeightedValue(i));
                        i_d_0.setWeight(
                                i_d_0.getWeight() - 2.0 * angle_min
                        );
                        nbox1.add(i_d_0);
                    }

                    return nbox1;
                }
            }
        }

        // 折線を減らせる条件に適合した角がなかった場合nbox0とおなじnbox1を作ってリターンする。
        for (int i = 1; i <= nbox0.getTotal(); i++) {
            nbox1.add(nbox0.getWeightedValue(i));
        }
        return nbox1;
    }

    //Determine if the endpoint inside the paper closest to Point p satisfies the extended Fushimi theorem
    public boolean extended_fushimi_decide_inside(Point p) {//return　0=満たさない、　1=満たす。　
        Point t1 = new Point();
        t1.set(closestPointOfFoldLine(p));//点pに最も近い、「線分の端点」を返すori_s.mottomo_tikai_Tenは近い点がないと p_return.set(100000.0,100000.0)と返してくる

        //t1を端点とする折線をNarabebakoに入れる
        SortingBox<LineSegment> nbox = new SortingBox<>();
        for (int i = 1; i <= getTotal(); i++) {
            LineSegment si = lineSegments.get(i);
            if (si.getColor().isFoldingLine()) { //この段階で補助活線は除く
                if (t1.distance(si.getA()) < Epsilon.FLAT) {
                    nbox.addByWeight(si, OritaCalc.angle(si.getA(), si.getB()));
                } else if (t1.distance(si.getB()) < Epsilon.FLAT) {
                    nbox.addByWeight(si, OritaCalc.angle(si.getB(), si.getA()));
                }
            }
        }

        return extended_fushimi_decide_inside(p, nbox);
    }

    public boolean extended_fushimi_decide_inside(Point p, SortingBox<LineSegment> nbox) {//return　0=満たさない、　1=満たす。　
        if (nbox.getTotal() % 2 == 1) {//t1を端点とする折線の数が奇数のとき
            return false;
        }

        if (nbox.getTotal() == 2) {//t1を端点とする折線の数が2のとき
            if (nbox.getValue(1).getColor() != nbox.getValue(2).getColor()) {//2本の線種が違うなら角度関係なしにダメ
                return false;
            }

            //The following is when the two line types are blue-blue or red-red
            LineSegment.Intersection i_senbun_kousa_hantei = OritaCalc.determineLineSegmentIntersection(nbox.getValue(1), nbox.getValue(2), Epsilon.FLAT);

            switch (i_senbun_kousa_hantei) {
                case PARALLEL_START_OF_S1_INTERSECTS_START_OF_S2_323:
                case PARALLEL_START_OF_S1_INTERSECTS_END_OF_S2_333:
                case PARALLEL_END_OF_S1_INTERSECTS_END_OF_S2_353:
                case PARALLEL_END_OF_S1_INTERSECTS_START_OF_S2_343:
                    return true;
                default:
                    return false;
            }
        }

        //以下はt1を端点とする折線の数が4以上の偶数のとき
        double fushimi_decision_angle_goukei = 360.0;

        SortingBox<LineSegment> nbox1 = new SortingBox<>();

        while (nbox.getTotal() > 2) {//点から出る折線の数が2になるまで実行する
            SortingBox<LineSegment> result = null;//Operation to make three adjacent angles into one angle by the extended Fushimi theorem
            SortingBox<LineSegment> nboxtemp = new SortingBox<>();
            SortingBox<LineSegment> nbox11 = new SortingBox<>();
            int tikai_orisen_jyunban;
            int tooi_orisen_jyunban;

            double kakudo_min = 10000.0;

            //角度の最小値kakudo_minを求める
            for (int k = 1; k <= nbox.getTotal(); k++) {//kは角度の順番
                tikai_orisen_jyunban = k;
                if (tikai_orisen_jyunban > nbox.getTotal()) {
                    tikai_orisen_jyunban = tikai_orisen_jyunban - nbox.getTotal();
                }
                tooi_orisen_jyunban = k + 1;
                if (tooi_orisen_jyunban > nbox.getTotal()) {
                    tooi_orisen_jyunban = tooi_orisen_jyunban - nbox.getTotal();
                }

                double temp_kakudo = OritaCalc.angle_between_0_kmax(
                        OritaCalc.angle_between_0_kmax(nbox.getWeight(tooi_orisen_jyunban), fushimi_decision_angle_goukei)
                                -
                                OritaCalc.angle_between_0_kmax(nbox.getWeight(tikai_orisen_jyunban), fushimi_decision_angle_goukei)

                        , fushimi_decision_angle_goukei
                );

                if (temp_kakudo < kakudo_min) {
                    kakudo_min = temp_kakudo;
                }
            }

            for (int k = 1; k <= nbox.getTotal(); k++) {//kは角度の順番
                double temp_kakudo = OritaCalc.angle_between_0_kmax(nbox.getWeight(2) - nbox.getWeight(1), fushimi_decision_angle_goukei);

                if (Math.abs(temp_kakudo - kakudo_min) < Epsilon.FLAT) {
                    if (nbox.getValue(1).getColor() != nbox.getValue(2).getColor()) {//この場合に隣接する３角度を1つの角度にする
                        // 折線を2つ減らせる条件に適合したので、新たにnbox1を作ってリターンする。

                        double kijyun_kakudo = nbox.getWeight(3);

                        for (int i = 1; i <= nbox.getTotal(); i++) {
                            WeightedValue<LineSegment> i_d_0 = new WeightedValue<>();
                            i_d_0.set(nbox.getWeightedValue(i));

                            i_d_0.setWeight(
                                    OritaCalc.angle_between_0_kmax(i_d_0.getWeight() - kijyun_kakudo, fushimi_decision_angle_goukei)
                            );

                            nboxtemp.add(i_d_0);
                        }

                        for (int i = 3; i <= nboxtemp.getTotal(); i++) {
                            WeightedValue<LineSegment> i_d_0 = new WeightedValue<>();
                            i_d_0.set(nboxtemp.getWeightedValue(i));

                            nbox11.add(i_d_0);
                        }

                        fushimi_decision_angle_goukei = fushimi_decision_angle_goukei - 2.0 * kakudo_min;
                        result = nbox11;
                        break;
                    }
                }
                nbox.shift();

            }
            if (result == null) {// 折線を2つ減らせる条件に適合した角がなかった場合nbox0とおなじnbox1を作ってリターンする。
                for (int i = 1; i <= nbox.getTotal(); i++) {
                    nbox11.add(nbox.getWeightedValue(i));
                }
                result = nbox11;
            }

            nbox1.set(result);
            if (nbox1.getTotal() == nbox.getTotal()) {
                return false;
            }
            nbox.set(nbox1);
        }

        double temp_kakudo = OritaCalc.angle_between_0_kmax(
                OritaCalc.angle_between_0_kmax(nbox.getWeight(1), fushimi_decision_angle_goukei)
                        -
                        OritaCalc.angle_between_0_kmax(nbox.getWeight(2), fushimi_decision_angle_goukei)
                , fushimi_decision_angle_goukei
        );

        return Math.abs(fushimi_decision_angle_goukei - temp_kakudo * 2.0) < Epsilon.FLAT;//この0だけ、角度がおかしいという意味
    }

    public double get_x_max() {//sousuu=0のときは0.0を返す
        if (total == 0) {
            return 0.0;
        }
        double dm = lineSegments.get(1).determineAX();
        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);
            if (dm < s.determineAX()) {
                dm = s.determineAX();
            }
            if (dm < s.determineBX()) {
                dm = s.determineBX();
            }
        }
        return dm;
    }

    public double get_x_min() {//sousuu=0のときは0.0を返す
        if (total == 0) {
            return 0.0;
        }
        double dm = lineSegments.get(1).determineAX();
        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);
            if (dm > s.determineAX()) {
                dm = s.determineAX();
            }
            if (dm > s.determineBX()) {
                dm = s.determineBX();
            }
        }
        return dm;
    }

    public double get_y_min() {
        if (total == 0) {
            return 0.0;
        }
        double dm = lineSegments.get(1).determineAX();
        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);
            if (dm > s.determineAY()) {
                dm = s.determineAY();
            }
            if (dm > s.determineBY()) {
                dm = s.determineBY();
            }
        }
        return dm;
    }

    public double get_y_max() {//sousuu=0のときは0.0を返す
        if (total == 0) {
            return 0.0;
        }
        double dm = lineSegments.get(1).determineAY();
        for (int i = 1; i <= total; i++) {
            LineSegment s = lineSegments.get(i);
            if (dm < s.determineAY()) {
                dm = s.determineAY();
            }
            if (dm < s.determineBY()) {
                dm = s.determineBY();
            }
        }
        return dm;
    }

    public void select_Takakukei(Polygon polygon, String Dousa_mode) {
        //select_lX,unselect_lX
        //"lX" lXは小文字のエルと大文字のエックス。Senbun s_step1と重複する部分のある線分やX交差する線分を対象にするモード。

        boolean i_kono_foldLine_wo_kaeru;//i_この折線を変える

        for (int i = 1; i <= total; i++) {
            i_kono_foldLine_wo_kaeru = false;
            LineSegment s = lineSegments.get(i);

            Polygon.Intersection intersection = polygon.inside_outside_check(s);

            if (intersection == Polygon.Intersection.BORDER) {
                i_kono_foldLine_wo_kaeru = true;
            }
            if (intersection == Polygon.Intersection.BORDER_INSIDE) {
                i_kono_foldLine_wo_kaeru = true;
            }
            if (intersection == Polygon.Intersection.INSIDE) {
                i_kono_foldLine_wo_kaeru = true;
            }

            if (i_kono_foldLine_wo_kaeru) {
                if (Dousa_mode.equals("select")) {
                    s.setSelected(2);
                }
                if (Dousa_mode.equals("unselectAction")) {
                    s.setSelected(0);
                }
            }
        }

    }

    public void select_lX(LineSegment s_step1, String Dousa_mode) {
        //select_lX,unselect_lX
        //"lX" lXは小文字のエルと大文字のエックス。Senbun s_step1と重複する部分のある線分やX交差する線分を対象にするモード。

        boolean i_kono_foldLine_wo_kaeru;//i_この折線を変える

        for (int i = 1; i <= total; i++) {
            i_kono_foldLine_wo_kaeru = false;
            LineSegment s = lineSegments.get(i);

            if (OritaCalc.isLineSegmentOverlapping(s, s_step1)) {
                i_kono_foldLine_wo_kaeru = true;
            }
            if (OritaCalc.lineSegment_X_kousa_decide(s, s_step1)) {
                i_kono_foldLine_wo_kaeru = true;
            }

            if (i_kono_foldLine_wo_kaeru) {
                if (Dousa_mode.equals("select_lX")) {
                    s.setSelected(2);
                }
                if (Dousa_mode.equals("unselect_lX")) {
                    s.setSelected(0);
                }
            }
        }
    }

    /**
     * selects all Line Segments that are somehow connected to p (even indirectly).
     * This method does very rough approximation, so it will always select all lines that are connected, but
     * could sometimes select lines that are not connected, if they are closer than 1 unit to the connected lines.
     *
     * @param p Point which the lines should be connected to
     */
    public void selectProbablyConnected(Point p) {
        // Build map of connections
        QuadTree qtA = new QuadTree(new LineSegmentListEndPointAdapter(lineSegments, l -> l.getA()));
        QuadTree qtB = new QuadTree(new LineSegmentListEndPointAdapter(lineSegments, l -> l.getB()));

        // Traverse connection map to find all connected points
        Set<Point> activePoints = new HashSet<>();
        Set<Point> newActivePoints = new HashSet<>();
        Set<Point> processedPoints = new HashSet<>();
        Set<LineSegment> connectedLines = new HashSet<>();

        activePoints.add(p);

        while (!activePoints.isEmpty()) {
            for (Point activePoint : activePoints) {
                processedPoints.add(activePoint);
                for(int i : qtA.collect(new PointCollector(activePoint))) {
                    LineSegment activeLine = lineSegments.get(i);
                    if(OritaCalc.equal(activeLine.getA(), activePoint)) {
                        connectedLines.add(activeLine);
                        if (!processedPoints.contains(activeLine.getB())) {
                            newActivePoints.add(activeLine.getB());
                        }
                    }
                }
                for(int i : qtB.collect(new PointCollector(activePoint))) {
                    LineSegment activeLine = lineSegments.get(i);
                    if(OritaCalc.equal(activeLine.getB(), activePoint)) {
                        connectedLines.add(activeLine);
                        if (!processedPoints.contains(activeLine.getA())) {
                            newActivePoints.add(activeLine.getA());
                        }
                    }
                }
            }
            activePoints.clear();
            activePoints.addAll(newActivePoints);
            newActivePoints.clear();
        }
        for (LineSegment connectedLine : connectedLines) {
            connectedLine.setSelected(2);
        }
    }

    public Collection<LineSegment> getLines() {
        List<LineSegment> lineSegments = new ArrayList<>();
        for (int i = 1; i <= total; i++) {
            lineSegments.add(get(i));
        }
        return lineSegments;
    }

    /**
     * Internal class used for quickly copying the contents of a foldlineset.
     */
    private static class FoldLineSave implements LineSegmentSave {
        private String title;
        private List<LineSegment> lineSegments;
        private List<Circle> circles;
        private List<LineSegment> auxLineSegments;

        public FoldLineSave() {
            lineSegments = new ArrayList<>();
            circles = new ArrayList<>();
            auxLineSegments = new ArrayList<>();
        }

        @Override
        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public List<LineSegment> getLineSegments() {
            return lineSegments;
        }

        @Override
        public void setLineSegments(List<LineSegment> lineSegments) {
            this.lineSegments = lineSegments;
        }

        @Override
        public void addLineSegment(LineSegment lineSegment) {
            lineSegments.add(lineSegment);
        }

        @Override
        public void addCircle(Circle circle) {
            circles.add(circle);
        }

        @Override
        public List<Circle> getCircles() {
            return circles;
        }

        @Override
        public void setCircles(List<Circle> circles) {
            this.circles = circles;
        }

        @Override
        public List<LineSegment> getAuxLineSegments() {
            return auxLineSegments;
        }

        @Override
        public void setAuxLineSegments(List<LineSegment> auxLineSegments) {
            this.auxLineSegments = auxLineSegments;
        }

        @Override
        public void addAuxLineSegment(LineSegment lineSegment) {
            auxLineSegments.add(lineSegment);
        }

        @Override
        public String getTitle() {
            return title;
        }
    }
}
