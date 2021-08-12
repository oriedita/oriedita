package jp.gr.java_conf.mt777.origami.dougu.linestore;

import jp.gr.java_conf.mt777.kiroku.memo.*;

import jp.gr.java_conf.mt777.zukei2d.ten.*;
import jp.gr.java_conf.mt777.zukei2d.senbun.*;
import jp.gr.java_conf.mt777.zukei2d.oritacalc.*;


import java.util.*;

// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
public class WireFrame {
    int total;               //Total number of line segments actually used
    ArrayList<LineSegment> lineSegments = new ArrayList<>(); //Instantiation of line segments
    OritaCalc oc = new OritaCalc();          //Instantiation of classes to use functions for various calculations

    public WireFrame() {
        reset();
    } //コンストラクタ

    public void reset() {
        total = 0;
        lineSegments.clear();
        lineSegments.add(new LineSegment());
    }

    public void set(WireFrame ss) {
        total = ss.getTotal();
        for (int i = 0; i <= total; i++) {
            LineSegment s;
            s = getLine(i);
            s.set(ss.get(i));
        }
    }

    private LineSegment getLine(int i) {
        if (total + 1 > lineSegments.size()) {
            while (total + 1 > lineSegments.size()) {
                lineSegments.add(new LineSegment());
            }
        }//It won't work without this sentence. I don't know exactly why it should be this sentence.
        return lineSegments.get(i);
    }

    //
    private void setLine(int i, LineSegment s) {
        if (total + 1 > lineSegments.size()) {
            while (total + 1 > lineSegments.size()) {
                lineSegments.add(new LineSegment());
            }
        }//It won't work without this sentence. I don't know exactly why it should be this sentence.
        if (i + 1 <= lineSegments.size()) {
            lineSegments.set(i, s);
        } //For some reason, it doesn't work without this if
    }

    //Get the total number of line segments
    public int getTotal() {
        return total;
    }

    public void setTotal(int i) {
        total = i;
    }

    //Get a line segment
    public LineSegment get(int i) {
        return getLine(i);
    }

    //Get the endpoint of the i-th line segment
    public Point getA(int i) {
        LineSegment s;
        s = getLine(i);
        return s.getA();
    }

    public Point getB(int i) {
        LineSegment s;
        s = getLine(i);
        return s.getB();
    }

    //Get the endpoint of the i-th line segment
    public double getax(int i) {
        LineSegment s;
        s = getLine(i);
        return s.getAX();
    }

    public double getbx(int i) {
        LineSegment s;
        s = getLine(i);
        return s.getBX();
    }

    public double getay(int i) {
        LineSegment s;
        s = getLine(i);
        return s.getAY();
    }

    public double getby(int i) {
        LineSegment s;
        s = getLine(i);
        return s.getBY();
    }

    //Set the position of the end point of the i-th line segment
    public void seta(int i, Point p) {
        LineSegment s;
        s = getLine(i);
        s.setA(p);
    }

    public void setb(int i, Point p) {
        LineSegment s;
        s = getLine(i);
        s.setB(p);
    }

    //Enter the value of the i-th line segment
    public void set(int i, Point p, Point q, int ic, int ia) {
        LineSegment s;
        s = getLine(i);
        s.set(p, q, ic, ia);
    }

    //Enter the color of the i-th line segment
    public void setColor(int i, int icol) {
        LineSegment s;
        s = getLine(i);
        s.setcolor(icol);
    }

    //Output the color of the i-th line segment
    public int getColor(int i) {
        LineSegment s;
        s = getLine(i);
        return s.getColor();
    }

    //Output the activity of the i-th line segment
    public int getiactive(int i) {
        LineSegment s;
        s = getLine(i);
        return s.getiactive();
    }

    //Output the information of all line segments of the line segment set as Memo.
    public Memo getMemo() {
        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<線分集合>"); // <Line segment set>

        for (int i = 1; i <= total; i++) {
            memo1.addLine("番号," + i); // number,
            LineSegment s = getLine(i);
            memo1.addLine("色," + s.getColor()); // colour,
            memo1.addLine("座標," + s.getAX() + "," + s.getAY() + "," +
                    s.getBX() + "," + s.getBY()); // Coordinate,
        }

        return memo1;
    }

    //-----------------------------
    public void setMemo(Memo memo1) {
        int yomiflg = 0;//If it is 0, it will not be read. If it is 1, read it.
        int iNumber = 0;
        int ic = 0;

        double ax, ay, bx, by;
        String str = "";

        //Read the file .orh for Orihime

        //First find the total number of line segments
        int iLine = 0;
        for (int i = 1; i <= memo1.getLineSize(); i++) {
            StringTokenizer tk = new StringTokenizer(memo1.getLine(i), ",");

            str = tk.nextToken();
            if (str.equals("<線分集合>")) { // <Line segment set>
                yomiflg = 1;
            }

            if ((yomiflg == 1) && (str.equals("番号"))) { // number
                iLine = iLine + 1;
            }
        }

        total = iLine;
        //First the total number of line segments was calculated

        for (int i = 1; i <= memo1.getLineSize(); i++) {
            StringTokenizer tk = new StringTokenizer(memo1.getLine(i), ",");
            str = tk.nextToken();

            if (str.equals("<線分集合>")) { // Line segment set
                yomiflg = 1;
            }

            if ((yomiflg == 1) && (str.equals("番号"))) { // number
                str = tk.nextToken();
                iNumber = Integer.parseInt(str);

            }
            if ((yomiflg == 1) && (str.equals("色"))) { // colour
                str = tk.nextToken();
                ic = Integer.parseInt(str);
                LineSegment s = getLine(iNumber);
                s.setcolor(ic);
            }
            if ((yomiflg == 1) && (str.equals("座標"))) { // coordinate
                str = tk.nextToken();
                ax = Double.parseDouble(str);
                str = tk.nextToken();
                ay = Double.parseDouble(str);
                str = tk.nextToken();
                bx = Double.parseDouble(str);
                str = tk.nextToken();
                by = Double.parseDouble(str);

                LineSegment s = getLine(iNumber);
                s.set(ax, ay, bx, by);
            }
        }
    }

    //展開図入力時の線分集合の整理

    public void split_arrangement() {//折り畳み推定などで得られる針金図の整理
        System.out.println("分割整理　１、点削除");
        point_removal();          //念のため、点状の線分を除く
        System.out.println("分割整理　２、重複線分削除");
        overlapping_line_removal();//念のため、全く一致する線分が２つあれば１つを除く
        System.out.println("分割整理　３、交差分割");
        intersect_divide();
        System.out.println("分割整理　４、点削除");
        point_removal();             //折り畳み推定の針金図の整理のため、点状の線分を除く
        System.out.println("分割整理　５、重複線分削除");
        overlapping_line_removal(); //折り畳み推定の針金図の整理のため、全く一致する線分が２つあれば１つを除く
    }


    //全線分の山谷を入れ替える。境界線等の山谷以外の線種は変化なし。
    public void zen_yama_tani_henkan() {
        int ic_temp;

        for (int ic_id = 1; ic_id <= total; ic_id++) {
            ic_temp = getColor(ic_id);
            if (ic_temp == 1) {
                ic_temp = 2;
            } else if (ic_temp == 2) {
                ic_temp = 1;
            }
            setColor(ic_id, ic_temp);
        }
    }


    //Arrangement of line segment sets to generate Smen

    public void bunkatu_seiri_for_Smen_hassei() {//Arrangement of wire diagrams obtained by folding estimation, etc.
        System.out.println("　　Senbunsyuugouの中で、Smenを発生させるための線分集合の整理");
        System.out.println("分割整理　１、点削除前	getsousuu() = " + getTotal());
        point_removal();          //念のため、点状の線分を除く
        System.out.println("分割整理　２、重複線分削除前	getsousuu() = " + getTotal());
        overlapping_line_removal();//念のため、全く一致する線分が２つあれば１つを除く
        System.out.println("分割整理　３、交差分割前	getsousuu() = " + getTotal());
        intersect_divide();
        System.out.println("分割整理　４、点削除前	getsousuu() = " + getTotal());
        point_removal();             //折り畳み推定の針金図の整理のため、点状の線分を除く
        System.out.println("分割整理　５、重複線分削除前	getsousuu() = " + getTotal());
        overlapping_line_removal(); //折り畳み推定の針金図の整理のため、全く一致する線分が２つあれば１つを除く
        System.out.println("分割整理　５、重複線分削除後	getsousuu() = " + getTotal());
    }


    //Remove dotted line segments
    public void point_removal() {
        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = getLine(i);
            if (oc.equal(s.getA(), s.getB())) {
                deleteLineSegment(i);
                i = i - 1;
            }
        }
    }

    public void point_removal(double r) {
        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = getLine(i);
            if (oc.equal(s.getA(), s.getB(), r)) {
                deleteLineSegment(i);
                i = i - 1;
            }
        }
    }

    // When there are two completely overlapping line segments, the one with the latest number is deleted.。
    public void overlapping_line_removal(double r) {
        int[] sakujyo_flg = new int[total + 1];
        LineSegment[] snew = new LineSegment[total + 1];
        for (int i = 1; i <= total; i++) {
            sakujyo_flg[i] = 0;
            snew[i] = new LineSegment();
        }

        for (int i = 1; i <= total - 1; i++) {
            LineSegment si;
            si = getLine(i);
            for (int j = i + 1; j <= total; j++) {
                LineSegment sj;
                sj = getLine(j);
                if (r <= -9999.9) {
                    if (oc.line_intersect_decide(si, sj) == 31) {
                        sakujyo_flg[j] = 1;
                    }
                } else {
                    if (oc.line_intersect_decide(si, sj, r, r) == 31) {
                        sakujyo_flg[j] = 1;
                    }
                }
            }
        }

        int smax = 0;
        for (int i = 1; i <= total; i++) {
            if (sakujyo_flg[i] == 0) {
                LineSegment si;
                si = getLine(i);
                smax = smax + 1;
                snew[smax].set(si);
            }
        }

        total = smax;
        for (int i = 1; i <= total; i++) {
            LineSegment si;
            si = getLine(i);
            si.set(snew[i]);
        }
    }

    //
    public void overlapping_line_removal() {
        overlapping_line_removal(-10000.0);
    }

    //
    public int overlapping_line_removal(int i, int j) {    //Returns 1 if duplicates are removed, 0 otherwise
        if (i == j) {
            return 0;
        }
        LineSegment si;
        si = getLine(i);
        LineSegment sj;
        sj = getLine(j);
        if (oc.line_intersect_decide(si, sj) == 31) {  //31 indicates that si and sj overlap exactly the same
            deleteLineSegment(j);
            return 1;
        }
        return 0;
    }

    //Divide the two line segments at the intersection of the two intersecting line segments. If there were two line segments that completely overlapped, both would remain without any processing.
    public void intersect_divide() {
        int i_divide = 1;//1 if there is a split, 0 if not

        ArrayList<Integer> k_flg = new ArrayList<>();//A flag that indicates that there is an effect of crossing.

        for (int i = 0; i <= total + 1; i++) {
            k_flg.add(1);
        }

        while (i_divide != 0) {
            i_divide = 0;
            for (int i = 1; i <= total; i++) {
                Integer I_k_flag = k_flg.get(i);
                if (I_k_flag == 1) {
                    k_flg.set(i, 0);
                    for (int j = 1; j <= total; j++) {
                        if (i != j) {
                            Integer J_k_flag = k_flg.get(j);
                            if (J_k_flag == 1) {
                                int itemp = 0;
                                int old_sousuu = total;
                                itemp = intersect_divide(i, j);
                                if (old_sousuu < total) {
                                    for (int is = old_sousuu + 1; is <= total; is++) {
                                        k_flg.add(1);
                                    }
                                }
                                if (itemp == 1) {
                                    i_divide = i_divide + 1;
                                    k_flg.set(i, 1);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

//---------------------

    //交差している２つの線分の交点で２つの線分を分割する。分割を行ったら1。行わなかったら0を返す。オリヒメ2.002から分割後の線の色も制御するようにした(重複部がある場合は一本化し、番号の遅いほうの色になる)。
    public int intersect_divide(int i, int j) {
        if (i == j) {
            return 0;
        }

        LineSegment si;
        si = getLine(i);
        LineSegment sj;
        sj = getLine(j);

        Point p1 = new Point();
        p1.set(si.getA());
        Point p2 = new Point();
        p2.set(si.getB());
        Point p3 = new Point();
        p3.set(sj.getA());
        Point p4 = new Point();
        p4.set(sj.getB());
        Point pk = new Point();


        double ixmax;
        double ixmin;
        double iymax;
        double iymin;

        ixmax = si.getAX();
        ixmin = si.getAX();
        iymax = si.getAY();
        iymin = si.getAY();

        if (ixmax < si.getBX()) {
            ixmax = si.getBX();
        }
        if (ixmin > si.getBX()) {
            ixmin = si.getBX();
        }
        if (iymax < si.getBY()) {
            iymax = si.getBY();
        }
        if (iymin > si.getBY()) {
            iymin = si.getBY();
        }

        double jxmax;
        double jxmin;
        double jymax;
        double jymin;

        jxmax = sj.getAX();
        jxmin = sj.getAX();
        jymax = sj.getAY();
        jymin = sj.getAY();

        if (jxmax < sj.getBX()) {
            jxmax = sj.getBX();
        }
        if (jxmin > sj.getBX()) {
            jxmin = sj.getBX();
        }
        if (jymax < sj.getBY()) {
            jymax = sj.getBY();
        }
        if (jymin > sj.getBY()) {
            jymin = sj.getBY();
        }

        if (ixmax + 0.5 < jxmin) {
            return 0;
        }
        if (jxmax + 0.5 < ixmin) {
            return 0;
        }
        if (iymax + 0.5 < jymin) {
            return 0;
        }
        if (jymax + 0.5 < iymin) {
            return 0;
        }

        if (oc.line_intersect_decide(si, sj) == 1) {
            pk.set(oc.findIntersection(si, sj));    //<<<<<<<<<<<<<<<<<<<<<<<
            si.setA(p1);
            si.setB(pk);
            sj.setA(p3);
            sj.setB(pk);
            addLine(p2, pk, si.getColor());
            addLine(p4, pk, sj.getColor());
            return 1;
        }

        //oc.senbun_kousa_hantei(si,sj)が21から24まではくの字型の交差で、なにもしない。

        if (oc.line_intersect_decide(si, sj) == 25) {
            pk.set(oc.findIntersection(si, sj));    //<<<<<<<<<<<<<<<<<<<<<<<
            sj.setA(p3);
            sj.setB(pk);
            addLine(p4, pk, sj.getColor());
            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 26) {
            pk.set(oc.findIntersection(si, sj));    //<<<<<<<<<<<<<<<<<<<<<<<
            sj.setA(p3);
            sj.setB(pk);
            addLine(p4, pk, sj.getColor());
            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 27) {
            pk.set(oc.findIntersection(si, sj));    //<<<<<<<<<<<<<<<<<<<<<<<
            si.setA(p1);
            si.setB(pk);
            addLine(p2, pk, si.getColor());
            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 28) {
            pk.set(oc.findIntersection(si, sj));    //<<<<<<<<<<<<<<<<<<<<<<<
            si.setA(p1);
            si.setB(pk);
            addLine(p2, pk, si.getColor());
            return 1;
        }
        //
        if (oc.line_intersect_decide(si, sj) == 31) {//If the two line segments are exactly the same, do nothing.
            return 0;
        }


        if (oc.line_intersect_decide(si, sj) == 321) {//The endpoints of two line segments (p1 and p3) overlap at one point. si contains sj
            si.setA(p2);
            si.setB(p4);

            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            sj.setcolor(overlapping_col);

            return 1;


        }

        if (oc.line_intersect_decide(si, sj) == 322) {//The endpoints of two line segments (p1 and p3) overlap at one point. sj contains si
            sj.setA(p2);
            sj.setB(p4);
            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            si.setcolor(overlapping_col);

            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 331) {//The endpoints of two line segments (p1 and p4) overlap at one point. si contains sj
            si.setA(p2);
            si.setB(p3);

            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            sj.setcolor(overlapping_col);

            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 332) {//The endpoints of two line segments (p1 and p4) overlap at one point. sj contains si
            sj.setA(p2);
            sj.setB(p3);
            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            si.setcolor(overlapping_col);
            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 341) {//The endpoints of two line segments (p2 and p3) overlap at one point. si contains sj
            si.setA(p1);
            si.setB(p4);
            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            sj.setcolor(overlapping_col);

            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 342) {//The endpoints of two line segments (p2 and p3) overlap at one point. sj contains si
            sj.setA(p1);
            sj.setB(p4);
            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            si.setcolor(overlapping_col);


            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 351) {//The endpoints of two line segments (p2 and p4) overlap at one point. si contains sj


            si.setA(p1);
            si.setB(p3);

            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            sj.setcolor(overlapping_col);

            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 352) {//The endpoints of two line segments (p2 and p4) overlap at one point. sj contains si
            sj.setA(p1);
            sj.setB(p3);
            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            si.setcolor(overlapping_col);

            return 1;
        }


        if (oc.line_intersect_decide(si, sj) == 361) {//In order of p1-p3-p4-p2
            si.setA(p1);
            si.setB(p3);

            addLine(p2, p4, si.getColor());
            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            sj.setcolor(overlapping_col);

            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 362) {//In order of p1-p4-p3-p2
            si.setA(p1);
            si.setB(p4);

            addLine(p2, p3, si.getColor());

            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            sj.setcolor(overlapping_col);

            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 363) {//In order of p3-p1-p2-p4
            sj.setA(p1);
            sj.setB(p3);

            addLine(p2, p4, sj.getColor());

            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            si.setcolor(overlapping_col);

            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 364) {//In order of p3-p2-p1-p4
            sj.setA(p1);
            sj.setB(p4);

            addLine(p2, p3, sj.getColor());

            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            si.setcolor(overlapping_col);

            return 1;
        }

        //
        if (oc.line_intersect_decide(si, sj) == 371) {//In order of p1-p3-p2-p4
            //System.out.println("371");
            si.setA(p1);
            si.setB(p3);

            sj.setA(p2);
            sj.setB(p4);

            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            addLine(p2, p3, overlapping_col);
            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 372) {//In order of p1-p4-p2-p3
            //System.out.println("372");
            si.setA(p1);
            si.setB(p4);

            sj.setA(p3);
            sj.setB(p2);

            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            addLine(p2, p4, overlapping_col);
            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 373) {//In order of p3-p1-p4-p2
            //System.out.println("373");
            sj.setA(p1);
            sj.setB(p3);
            si.setA(p2);
            si.setB(p4);
            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            addLine(p1, p4, overlapping_col);
            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 374) {//In order of p4-p1-p3-p2
            //System.out.println("374");
            sj.setA(p1);
            sj.setB(p4);
            si.setA(p3);
            si.setB(p2);
            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            addLine(p1, p3, overlapping_col);
            return 1;
        }

        return 0;
    }


    //Add line segment-------------------------------
    public void addLine(Point pi, Point pj, int i_c) {
        total++;

        LineSegment s;
        s = getLine(total);
        s.set(pi, pj, i_c);
    }

    //Add line segment-------------------------------
    public void addLine(double ax, double ay, double bx, double by, int ic) {
        total++;

        LineSegment s;
        s = getLine(total);
        s.set(ax, ay, bx, by, ic);
    }

    //Add line segment-------------------------------
    public void addLine(Point pi, Point pj) {
        total++;

        LineSegment s;
        s = getLine(total);

        s.setA(pi);
        s.setB(pj);
    }

    //線分の削除-----------------------------------------
    public void deleteLineSegment(int j) {   //j番目の線分を削除する
        for (int i = j; i <= total - 1; i++) {
            LineSegment si;
            si = getLine(i);
            LineSegment si1;
            si1 = getLine(i + 1);
            si.set(si1);

        }
        total--;
    }

    //i番目の線分の長さを得る---------------------------
    public double getnagasa(int i) {
        LineSegment s;
        s = getLine(i);
        return s.getLength();
    }

    //Remove the branching line segments without forming a closed polygon.
    public void branch_trim(double r) {
        int iflga = 0;
        int iflgb = 0;
        for (int i = 1; i <= total; i++) {
            iflga = 0;
            iflgb = 0;
            LineSegment si;
            si = getLine(i);
            for (int j = 1; j <= total; j++) {
                if (i != j) {
                    LineSegment sj;
                    sj = getLine(j);
                    if (oc.distance(si.getA(), sj.getA()) < r) {
                        iflga = 1;
                    }
                    if (oc.distance(si.getA(), sj.getB()) < r) {
                        iflga = 1;
                    }
                    if (oc.distance(si.getB(), sj.getA()) < r) {
                        iflgb = 1;
                    }
                    if (oc.distance(si.getB(), sj.getB()) < r) {
                        iflgb = 1;
                    }
                }
            }

            if ((iflga == 0) || (iflgb == 0)) {
                deleteLineSegment(i);
                i = 1;
            }
        }
    }

    //一本だけの離れてある線分を削除する。
    public void tanSenbun_sakujyo(double r) {
        int iflg = 0;
        for (int i = 1; i <= total; i++) {
            iflg = 0;
            LineSegment si;
            si = getLine(i);
            for (int j = 1; j <= total; j++) {
                if (i != j) {
                    LineSegment sj;
                    sj = getLine(j);
                    if (oc.distance(si.getA(), sj.getA()) < r) {
                        iflg = 1;
                    }
                    if (oc.distance(si.getB(), sj.getB()) < r) {
                        iflg = 1;
                    }
                    if (oc.distance(si.getA(), sj.getB()) < r) {
                        iflg = 1;
                    }
                    if (oc.distance(si.getB(), sj.getA()) < r) {
                        iflg = 1;
                    }
                }
            }

            if (iflg == 0) {
                deleteLineSegment(i);
                i = 1;
            }
        }
    }


    //点pに近い(r以内)線分をさがし、その番号を返す関数(ただし、j番目の線分は対象外)。近い線分がなければ、0を返す---------------------------------
    //もし対象外にする線分が無い場合は、jを0とか負の整数とかにする。
    //070317　追加機能　j　が　-10　の時は　活性化していない枝（getiactive(i)が0）を対象にする。

    public int lineSegment_search(Point p, double r, int j) {
        if (j == -10) {
            for (int i = 1; i <= total; i++) {
                if (((lineSegment_position_search(i, p, r) == 1) && (i != j)) && (getiactive(i) == 0)) {
                    return i;
                }
            }
            for (int i = 1; i <= total; i++) {
                if (((lineSegment_position_search(i, p, r) == 2) && (i != j)) && (getiactive(i) == 0)) {
                    return i;
                }
            }
            for (int i = 1; i <= total; i++) {
                if (((lineSegment_position_search(i, p, r) == 3) && (i != j)) && (getiactive(i) == 0)) {
                    return i;
                }
            }
            return 0;
        }

        for (int i = 1; i <= total; i++) {
            if ((lineSegment_position_search(i, p, r) == 1) && (i != j)) {
                return i;
            }
        }
        for (int i = 1; i <= total; i++) {
            if ((lineSegment_position_search(i, p, r) == 2) && (i != j)) {
                return i;
            }
        }
        for (int i = 1; i <= total; i++) {
            if ((lineSegment_position_search(i, p, r) == 3) && (i != j)) {
                return i;
            }
        }
        return 0;
    }


    //点pが指定された線分とどの部所で近い(r以内)かどうかを判定する関数　---------------------------------
    //0=近くない、1=a点に近い、2=b点に近い、3=柄の部分に近い
    public int lineSegment_position_search(int i, Point p, double r) {
        if (r > oc.distance(p, getA(i))) {
            return 1;
        }//a点に近いかどうか
        if (r > oc.distance(p, getB(i))) {
            return 2;
        }//b点に近いかどうか
        if (r > oc.distance_lineSegment(p, get(i))) {
            return 3;
        }//柄の部分に近いかどうか
        return 0;
    }


    //点pに最も近い線分の番号を返す
    public int mottomo_tikai_lineSegment_Search(Point p) {
        int minrid = 0;
        double minr = 100000;
        for (int i = 1; i <= total; i++) {
            double sk = oc.distance_lineSegment(p, get(i));
            if (minr > sk) {
                minr = sk;
                minrid = i;
            }//柄の部分に近いかどうか

        }
        return minrid;
    }


    //点pに最も近い線分の端点を返す
    public Point mottomo_tikai_point_search(Point p) {

        Point p_return = new Point();
        p_return.set(100000.0, 100000.0);
        Point p_temp = new Point();
        for (int i = 1; i <= total; i++) {
            p_temp.set(getA(i));
            if (p.distanceSquared(p_temp) < p.distanceSquared(p_return)) {
                p_return.set(p_temp.getX(), p_temp.getY());
            }
            p_temp.set(getB(i));
            if (p.distanceSquared(p_temp) < p.distanceSquared(p_return)) {
                p_return.set(p_temp.getX(), p_temp.getY());
            }
            //p_temp.set(geta(i));if(p.kyori(p_temp)<p.kyori(p_return) ) {p_return.set(p_temp.getx(),p_temp.gety()); }
            //p_temp.set(getb(i));if(p.kyori(p_temp)<p.kyori(p_return) ) {p_return.set(p_temp.getx(),p_temp.gety()); }

        }
        return p_return;
    }


    //点pの近くの線分の活性化
    public void kasseika(Point p, double r) {
        for (int i = 1; i <= total; i++) {
            LineSegment si;
            si = getLine(i);
            si.activate(p, r);
        }
    }

    //全線分の非活性化
    public void hikasseika() {
        for (int i = 1; i <= total; i++) {
            LineSegment si;
            si = getLine(i);
            si.deactivate();
        }
    }


    //線分の活性化されたものを点pの座標にする
    public void set(Point p) {
        for (int i = 1; i <= total; i++) {
            LineSegment si;
            si = getLine(i);
            si.set(p);
        }

    }

    //線分集合の中の線分i0と、i0以外で、全く重なる線分があれば、その番号を返す。なければ-10を返す。
    public int overlapping_lineSegment_search(int i0) {
        //int minrid=0;double minr=100000;
        for (int i = 1; i <= total; i++) {
            if (i != i0) {
                if (oc.line_intersect_decide(get(i), get(i0)) == 31) {
                    return i;
                }
            }
        }
        return -10;


    }


}
