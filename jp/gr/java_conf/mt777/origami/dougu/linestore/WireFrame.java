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
    int total;               //実際に使う線分の総数
    ArrayList<LineSegment> lineSegments = new ArrayList<>(); //線分のインスタンス化
    OritaCalc oc = new OritaCalc();          //各種計算用の関数を使うためのクラスのインスタンス化

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
        }//この文がないとうまく行かない。なぜこの文でないといけないかという理由が正確にはわからない。
        return lineSegments.get(i);
    }

    //
    private void setLine(int i, LineSegment s) {
        if (total + 1 > lineSegments.size()) {
            while (total + 1 > lineSegments.size()) {
                lineSegments.add(new LineSegment());
            }
        }//この文がないとうまく行かない。なぜこの文でないといけないかという理由が正確にはわからない。
        if (i + 1 <= lineSegments.size()) {
            lineSegments.set(i, s);
        } //なぜか、このifがないとうまく行かない
    }

    //線分の総数を得る
    public int getTotal() {
        return total;
    }

    public void setTotal(int i) {
        total = i;
    }

    //線分を得る
    public LineSegment get(int i) {
        //Senbun s;s= sen(i);return s;
        return getLine(i);
    }

    //i番目の線分の端点を得る
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

    //i番目の線分の端点を得る
    public double getax(int i) {
        LineSegment s;
        s = getLine(i);
        return s.getAx();
    }

    public double getbx(int i) {
        LineSegment s;
        s = getLine(i);
        return s.getbx();
    }

    public double getay(int i) {
        LineSegment s;
        s = getLine(i);
        return s.getay();
    }

    public double getby(int i) {
        LineSegment s;
        s = getLine(i);
        return s.getby();
    }

    //i番目の線分の端点の位置をセットする
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

    //i番目の線分の値を入力する
    public void set(int i, Point p, Point q, int ic, int ia) {
        LineSegment s;
        s = getLine(i);
        s.set(p, q, ic, ia);
    }

    //i番目の線分の色を入力する
    public void setColor(int i, int icol) {
        LineSegment s;
        s = getLine(i);
        s.setcolor(icol);
    }

    //i番目の線分の色を出力する
    public int getColor(int i) {
        LineSegment s;
        s = getLine(i);
        return s.getColor();
    }

    //i番目の線分の活性を出力する
    public int getiactive(int i) {
        LineSegment s;
        s = getLine(i);
        return s.getiactive();
    }

    //線分集合の全線分の情報を Memoとして出力する。
    public Memo getMemo() {
        String str = "";//文字列処理用のクラスのインスタンス化

        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<線分集合>");

        for (int i = 1; i <= total; i++) {
            memo1.addLine("番号," + i);
            LineSegment s;
            s = getLine(i);
            memo1.addLine("色," + s.getColor());
            memo1.addLine("座標," + s.getAx() + "," + s.getay() + "," +
                    s.getbx() + "," + s.getby());


        }


        return memo1;
    }

    //-----------------------------
    public void setMemo(Memo memo1) {
        int yomiflg = 0;//0なら読み込みを行わない。1なら読み込む。
        int ibangou = 0;
        int ic = 0;

        String st = "";
        Double Dd = 0.0;
        Integer Ii = 0;

        double ax, ay, bx, by;
        String str = "";
        //int jtok;

//オリヒメ用ファイル.orhを読む

//最初に線分の総数を求める
        int isen = 0;
        for (int i = 1; i <= memo1.getLineSize(); i++) {


            StringTokenizer tk = new StringTokenizer(memo1.getLine(i), ",");
            //jtok=    tk.countTokens();


            str = tk.nextToken();
            if (str.equals("<線分集合>")) {
                yomiflg = 1;
            }

            if ((yomiflg == 1) && (str.equals("番号"))) {
                isen = isen + 1;

            }
        }

        total = isen;
//最初に線分の総数が求められた
        //

        for (int i = 1; i <= memo1.getLineSize(); i++) {


            StringTokenizer tk = new StringTokenizer(memo1.getLine(i), ",");
            //jtok=    tk.countTokens();
            str = tk.nextToken();
            //  	System.out.println("::::::::::"+ str+"<<<<<" );

            if (str.equals("<線分集合>")) {
                yomiflg = 1;
            }

            if ((yomiflg == 1) && (str.equals("番号"))) {
                str = tk.nextToken();
                ibangou = Integer.parseInt(str);

            }
            if ((yomiflg == 1) && (str.equals("色"))) {
                str = tk.nextToken();
                ic = Integer.parseInt(str);
                LineSegment s;
                s = getLine(ibangou);
                s.setcolor(ic);

            }
            if ((yomiflg == 1) && (str.equals("座標"))) {
                str = tk.nextToken();
                ax = Double.parseDouble(str);
                str = tk.nextToken();
                ay = Double.parseDouble(str);
                str = tk.nextToken();
                bx = Double.parseDouble(str);
                str = tk.nextToken();
                by = Double.parseDouble(str);

                LineSegment s;
                s = getLine(ibangou);
                s.set(ax, ay, bx, by);
                //	System.out.println(ax );
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


    //Smenを発生させるための線分集合の整理

    public void bunkatu_seiri_for_Smen_hassei() {//折り畳み推定などで得られる針金図の整理
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


    //点状の線分を削除
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

    // 全く重なる線分が2本存在するときに番号の遅いほうを削除する。
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
    public int overlapping_line_removal(int i, int j) {    //重複の削除をしたら1、しなければ0を返す
        if (i == j) {
            return 0;
        }
        LineSegment si;
        si = getLine(i);
        LineSegment sj;
        sj = getLine(j);
        if (oc.line_intersect_decide(si, sj) == 31) {  //31はsiとsjが全く同じに重なることを示す
            deleteLineSegment(j);
            return 1;
        }
        return 0;
    }

    //交差している２つの線分の交点で２つの線分を分割する。　まったく重なる線分が２つあった場合は、なんの処理もなされないまま２つとも残る。
    public void intersect_divide() {
        int i_divide = 1;//分割があれば1、なければ0

        ArrayList<Integer> k_flg = new ArrayList<>();//交差分割の影響があることを示すフラッグ。

        for (int i = 0; i <= total + 1; i++) {
            k_flg.add(1);
        }

        while (i_divide != 0) {
            i_divide = 0;
            for (int i = 1; i <= total; i++) {
                Integer I_k_flag = (Integer) k_flg.get(i);
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

        ixmax = si.getAx();
        ixmin = si.getAx();
        iymax = si.getay();
        iymin = si.getay();

        if (ixmax < si.getbx()) {
            ixmax = si.getbx();
        }
        if (ixmin > si.getbx()) {
            ixmin = si.getbx();
        }
        if (iymax < si.getby()) {
            iymax = si.getby();
        }
        if (iymin > si.getby()) {
            iymin = si.getby();
        }

        double jxmax;
        double jxmin;
        double jymax;
        double jymin;

        jxmax = sj.getAx();
        jxmin = sj.getAx();
        jymax = sj.getay();
        jymin = sj.getay();

        if (jxmax < sj.getbx()) {
            jxmax = sj.getbx();
        }
        if (jxmin > sj.getbx()) {
            jxmin = sj.getbx();
        }
        if (jymax < sj.getby()) {
            jymax = sj.getby();
        }
        if (jymin > sj.getby()) {
            jymin = sj.getby();
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
        if (oc.line_intersect_decide(si, sj) == 31) {//2つの線分がまったく同じ場合は、何もしない。
            return 0;
        }


        if (oc.line_intersect_decide(si, sj) == 321) {//2つの線分の端点どうし(p1とp3)が1点で重なる。siにsjが含まれる
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

        if (oc.line_intersect_decide(si, sj) == 322) {//2つの線分の端点どうし(p1とp3)が1点で重なる。sjにsiが含まれる
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

        if (oc.line_intersect_decide(si, sj) == 331) {//2つの線分の端点どうし(p1とp4)が1点で重なる。siにsjが含まれる
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

        if (oc.line_intersect_decide(si, sj) == 332) {//2つの線分の端点どうし(p1とp4)が1点で重なる。sjにsiが含まれる
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

        if (oc.line_intersect_decide(si, sj) == 341) {//2つの線分の端点どうし(p2とp3)が1点で重なる。siにsjが含まれる
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

        if (oc.line_intersect_decide(si, sj) == 342) {//2つの線分の端点どうし(p2とp3)が1点で重なる。sjにsiが含まれる
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

        if (oc.line_intersect_decide(si, sj) == 351) {//2つの線分の端点どうし(p2とp4)が1点で重なる。siにsjが含まれる


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

        if (oc.line_intersect_decide(si, sj) == 352) {//2つの線分の端点どうし(p2とp4)が1点で重なる。sjにsiが含まれる
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


        if (oc.line_intersect_decide(si, sj) == 361) {//p1-p3-p4-p2の順
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

        if (oc.line_intersect_decide(si, sj) == 362) {//p1-p4-p3-p2の順
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

        if (oc.line_intersect_decide(si, sj) == 363) {//p3-p1-p2-p4の順
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

        if (oc.line_intersect_decide(si, sj) == 364) {//p3-p2-p1-p4の順
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
        if (oc.line_intersect_decide(si, sj) == 371) {//p1-p3-p2-p4の順
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

        if (oc.line_intersect_decide(si, sj) == 372) {//p1-p4-p2-p3の順
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

        if (oc.line_intersect_decide(si, sj) == 373) {//p3-p1-p4-p2の順
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

        if (oc.line_intersect_decide(si, sj) == 374) {//p4-p1-p3-p2の順
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


    //線分の追加-------------------------------
    public void addLine(Point pi, Point pj, int i_c) {
        total++;

        LineSegment s;
        s = getLine(total);
        s.set(pi, pj, i_c);
    }

    //線分の追加-------------------------------
    public void addLine(double ax, double ay, double bx, double by, int ic) {
        total++;

        LineSegment s;
        s = getLine(total);
        s.set(ax, ay, bx, by, ic);
    }

    //線分の追加-------------------------------
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

    //閉多角形を形成せず、枝状になっている線分を削除する。
    public void eda_kesi(double r) {
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
