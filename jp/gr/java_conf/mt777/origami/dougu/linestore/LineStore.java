package jp.gr.java_conf.mt777.origami.dougu.linestore;

import jp.gr.java_conf.mt777.kiroku.memo.*;

import jp.gr.java_conf.mt777.zukei2d.ten.*;
import jp.gr.java_conf.mt777.zukei2d.senbun.*;
import jp.gr.java_conf.mt777.zukei2d.oritacalc.*;


import java.util.*;

// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
public class LineStore {
    int total;               //実際に使う線分の総数
    ArrayList<Line> lines = new ArrayList<>(); //線分のインスタンス化
    OritaCalc oc = new OritaCalc();          //各種計算用の関数を使うためのクラスのインスタンス化

    public LineStore() {
        reset();
    } //コンストラクタ

    public void reset() {
        total = 0;
        lines.clear();
        lines.add(new Line());
    }

    public void set(LineStore ss) {
        total = ss.getTotal();
        for (int i = 0; i <= total; i++) {
            Line s;
            s = getLine(i);
            s.set(ss.get(i));
        }
    }

    private Line getLine(int i) {
        if (total + 1 > lines.size()) {
            while (total + 1 > lines.size()) {
                lines.add(new Line());
            }
        }//この文がないとうまく行かない。なぜこの文でないといけないかという理由が正確にはわからない。
        return lines.get(i);
    }

    //
    private void setLine(int i, Line s) {
        if (total + 1 > lines.size()) {
            while (total + 1 > lines.size()) {
                lines.add(new Line());
            }
        }//この文がないとうまく行かない。なぜこの文でないといけないかという理由が正確にはわからない。
        if (i + 1 <= lines.size()) {
            lines.set(i, s);
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
    public Line get(int i) {
        //Senbun s;s= sen(i);return s;
        return getLine(i);
    }

    //i番目の線分の端点を得る
    public Point geta(int i) {
        Line s;
        s = getLine(i);
        return s.geta();
    }

    public Point getb(int i) {
        Line s;
        s = getLine(i);
        return s.getb();
    }

    //i番目の線分の端点を得る
    public double getax(int i) {
        Line s;
        s = getLine(i);
        return s.getax();
    }

    public double getbx(int i) {
        Line s;
        s = getLine(i);
        return s.getbx();
    }

    public double getay(int i) {
        Line s;
        s = getLine(i);
        return s.getay();
    }

    public double getby(int i) {
        Line s;
        s = getLine(i);
        return s.getby();
    }

    //i番目の線分の端点の位置をセットする
    public void seta(int i, Point p) {
        Line s;
        s = getLine(i);
        s.seta(p);
    }

    public void setb(int i, Point p) {
        Line s;
        s = getLine(i);
        s.setb(p);
    }

    //i番目の線分の値を入力する
    public void set(int i, Point p, Point q, int ic, int ia) {
        Line s;
        s = getLine(i);
        s.set(p, q, ic, ia);
    }

    //i番目の線分の色を入力する
    public void setcolor(int i, int icol) {
        Line s;
        s = getLine(i);
        s.setcolor(icol);
    }

    //i番目の線分の色を出力する
    public int getcolor(int i) {
        Line s;
        s = getLine(i);
        return s.getcolor();
    }

    //i番目の線分の活性を出力する
    public int getiactive(int i) {
        Line s;
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
            Line s;
            s = getLine(i);
            memo1.addLine("色," + s.getcolor());
            memo1.addLine("座標," + s.getax() + "," + s.getay() + "," +
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
                Line s;
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

                Line s;
                s = getLine(ibangou);
                s.set(ax, ay, bx, by);
                //	System.out.println(ax );
            }
        }
    }

    //展開図入力時の線分集合の整理

    public void bunkatu_seiri() {//折り畳み推定などで得られる針金図の整理
        System.out.println("分割整理　１、点削除");
        ten_sakujyo();          //念のため、点状の線分を除く
        System.out.println("分割整理　２、重複線分削除");
        jyuufuku_senbun_sakujyo();//念のため、全く一致する線分が２つあれば１つを除く
        System.out.println("分割整理　３、交差分割");
        kousabunkatu();
        System.out.println("分割整理　４、点削除");
        ten_sakujyo();             //折り畳み推定の針金図の整理のため、点状の線分を除く
        System.out.println("分割整理　５、重複線分削除");
        jyuufuku_senbun_sakujyo(); //折り畳み推定の針金図の整理のため、全く一致する線分が２つあれば１つを除く
    }


    //全線分の山谷を入れ替える。境界線等の山谷以外の線種は変化なし。
    public void zen_yama_tani_henkan() {
        int ic_temp;

        for (int ic_id = 1; ic_id <= total; ic_id++) {
            ic_temp = getcolor(ic_id);
            if (ic_temp == 1) {
                ic_temp = 2;
            } else if (ic_temp == 2) {
                ic_temp = 1;
            }
            setcolor(ic_id, ic_temp);
        }
    }


    //Smenを発生させるための線分集合の整理

    public void bunkatu_seiri_for_Smen_hassei() {//折り畳み推定などで得られる針金図の整理
        System.out.println("　　Senbunsyuugouの中で、Smenを発生させるための線分集合の整理");
        System.out.println("分割整理　１、点削除前	getsousuu() = " + getTotal());
        ten_sakujyo();          //念のため、点状の線分を除く
        System.out.println("分割整理　２、重複線分削除前	getsousuu() = " + getTotal());
        jyuufuku_senbun_sakujyo();//念のため、全く一致する線分が２つあれば１つを除く
        System.out.println("分割整理　３、交差分割前	getsousuu() = " + getTotal());
        kousabunkatu();
        System.out.println("分割整理　４、点削除前	getsousuu() = " + getTotal());
        ten_sakujyo();             //折り畳み推定の針金図の整理のため、点状の線分を除く
        System.out.println("分割整理　５、重複線分削除前	getsousuu() = " + getTotal());
        jyuufuku_senbun_sakujyo(); //折り畳み推定の針金図の整理のため、全く一致する線分が２つあれば１つを除く
        System.out.println("分割整理　５、重複線分削除後	getsousuu() = " + getTotal());
    }


    //点状の線分を削除
    public void ten_sakujyo() {
        for (int i = 1; i <= total; i++) {
            Line s;
            s = getLine(i);
            if (oc.hitosii(s.geta(), s.getb())) {
                delsenbun(i);
                i = i - 1;
            }
        }
    }

    public void ten_sakujyo(double r) {
        for (int i = 1; i <= total; i++) {
            Line s;
            s = getLine(i);
            if (oc.hitosii(s.geta(), s.getb(), r)) {
                delsenbun(i);
                i = i - 1;
            }
        }
    }

    // 全く重なる線分が2本存在するときに番号の遅いほうを削除する。
    public void jyuufuku_senbun_sakujyo(double r) {
        int[] sakujyo_flg = new int[total + 1];
        Line[] snew = new Line[total + 1];
        for (int i = 1; i <= total; i++) {
            sakujyo_flg[i] = 0;
            snew[i] = new Line();
        }

        for (int i = 1; i <= total - 1; i++) {
            Line si;
            si = getLine(i);
            for (int j = i + 1; j <= total; j++) {
                Line sj;
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
                Line si;
                si = getLine(i);
                smax = smax + 1;
                snew[smax].set(si);
            }
        }

        total = smax;
        for (int i = 1; i <= total; i++) {
            Line si;
            si = getLine(i);
            si.set(snew[i]);
        }
    }

    //
    public void jyuufuku_senbun_sakujyo() {
        jyuufuku_senbun_sakujyo(-10000.0);
    }

    //
    public int jyuufuku_senbun_sakujyo(int i, int j) {    //重複の削除をしたら1、しなければ0を返す
        if (i == j) {
            return 0;
        }
        Line si;
        si = getLine(i);
        Line sj;
        sj = getLine(j);
        if (oc.line_intersect_decide(si, sj) == 31) {  //31はsiとsjが全く同じに重なることを示す
            delsenbun(j);
            return 1;
        }
        return 0;
    }

    //交差している２つの線分の交点で２つの線分を分割する。　まったく重なる線分が２つあった場合は、なんの処理もなされないまま２つとも残る。
    public void kousabunkatu() {
        int ibunkatu = 1;//分割があれば1、なければ0

        ArrayList<Integer> k_flg = new ArrayList<>();//交差分割の影響があることを示すフラッグ。

        for (int i = 0; i <= total + 1; i++) {
            k_flg.add(1);
        }

        while (ibunkatu != 0) {
            ibunkatu = 0;
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
                                itemp = kousabunkatu(i, j);
                                if (old_sousuu < total) {
                                    for (int is = old_sousuu + 1; is <= total; is++) {
                                        k_flg.add(1);
                                    }
                                }
                                if (itemp == 1) {
                                    ibunkatu = ibunkatu + 1;
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
    public int kousabunkatu(int i, int j) {
        if (i == j) {
            return 0;
        }

        Line si;
        si = getLine(i);
        Line sj;
        sj = getLine(j);

        Point p1 = new Point();
        p1.set(si.geta());
        Point p2 = new Point();
        p2.set(si.getb());
        Point p3 = new Point();
        p3.set(sj.geta());
        Point p4 = new Point();
        p4.set(sj.getb());
        Point pk = new Point();


        double ixmax;
        double ixmin;
        double iymax;
        double iymin;

        ixmax = si.getax();
        ixmin = si.getax();
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

        jxmax = sj.getax();
        jxmin = sj.getax();
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
            pk.set(oc.kouten_motome(si, sj));    //<<<<<<<<<<<<<<<<<<<<<<<
            si.seta(p1);
            si.setb(pk);
            sj.seta(p3);
            sj.setb(pk);
            addsenbun(p2, pk, si.getcolor());
            addsenbun(p4, pk, sj.getcolor());
            return 1;
        }

        //oc.senbun_kousa_hantei(si,sj)が21から24まではくの字型の交差で、なにもしない。

        if (oc.line_intersect_decide(si, sj) == 25) {
            pk.set(oc.kouten_motome(si, sj));    //<<<<<<<<<<<<<<<<<<<<<<<
            sj.seta(p3);
            sj.setb(pk);
            addsenbun(p4, pk, sj.getcolor());
            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 26) {
            pk.set(oc.kouten_motome(si, sj));    //<<<<<<<<<<<<<<<<<<<<<<<
            sj.seta(p3);
            sj.setb(pk);
            addsenbun(p4, pk, sj.getcolor());
            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 27) {
            pk.set(oc.kouten_motome(si, sj));    //<<<<<<<<<<<<<<<<<<<<<<<
            si.seta(p1);
            si.setb(pk);
            addsenbun(p2, pk, si.getcolor());
            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 28) {
            pk.set(oc.kouten_motome(si, sj));    //<<<<<<<<<<<<<<<<<<<<<<<
            si.seta(p1);
            si.setb(pk);
            addsenbun(p2, pk, si.getcolor());
            return 1;
        }
        //
        if (oc.line_intersect_decide(si, sj) == 31) {//2つの線分がまったく同じ場合は、何もしない。
            return 0;
        }


        if (oc.line_intersect_decide(si, sj) == 321) {//2つの線分の端点どうし(p1とp3)が1点で重なる。siにsjが含まれる
            si.seta(p2);
            si.setb(p4);

            int jyuufuku_col;
            jyuufuku_col = si.getcolor();
            if (i < j) {
                jyuufuku_col = sj.getcolor();
            }
            sj.setcolor(jyuufuku_col);

            return 1;


        }

        if (oc.line_intersect_decide(si, sj) == 322) {//2つの線分の端点どうし(p1とp3)が1点で重なる。sjにsiが含まれる
            sj.seta(p2);
            sj.setb(p4);
            int jyuufuku_col;
            jyuufuku_col = si.getcolor();
            if (i < j) {
                jyuufuku_col = sj.getcolor();
            }
            si.setcolor(jyuufuku_col);

            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 331) {//2つの線分の端点どうし(p1とp4)が1点で重なる。siにsjが含まれる
            si.seta(p2);
            si.setb(p3);

            int jyuufuku_col;
            jyuufuku_col = si.getcolor();
            if (i < j) {
                jyuufuku_col = sj.getcolor();
            }
            sj.setcolor(jyuufuku_col);

            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 332) {//2つの線分の端点どうし(p1とp4)が1点で重なる。sjにsiが含まれる
            sj.seta(p2);
            sj.setb(p3);
            int jyuufuku_col;
            jyuufuku_col = si.getcolor();
            if (i < j) {
                jyuufuku_col = sj.getcolor();
            }
            si.setcolor(jyuufuku_col);
            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 341) {//2つの線分の端点どうし(p2とp3)が1点で重なる。siにsjが含まれる
            si.seta(p1);
            si.setb(p4);
            int jyuufuku_col;
            jyuufuku_col = si.getcolor();
            if (i < j) {
                jyuufuku_col = sj.getcolor();
            }
            sj.setcolor(jyuufuku_col);

            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 342) {//2つの線分の端点どうし(p2とp3)が1点で重なる。sjにsiが含まれる
            sj.seta(p1);
            sj.setb(p4);
            int jyuufuku_col;
            jyuufuku_col = si.getcolor();
            if (i < j) {
                jyuufuku_col = sj.getcolor();
            }
            si.setcolor(jyuufuku_col);


            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 351) {//2つの線分の端点どうし(p2とp4)が1点で重なる。siにsjが含まれる


            si.seta(p1);
            si.setb(p3);

            int jyuufuku_col;
            jyuufuku_col = si.getcolor();
            if (i < j) {
                jyuufuku_col = sj.getcolor();
            }
            sj.setcolor(jyuufuku_col);

            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 352) {//2つの線分の端点どうし(p2とp4)が1点で重なる。sjにsiが含まれる
            sj.seta(p1);
            sj.setb(p3);
            int jyuufuku_col;
            jyuufuku_col = si.getcolor();
            if (i < j) {
                jyuufuku_col = sj.getcolor();
            }
            si.setcolor(jyuufuku_col);

            return 1;
        }


        if (oc.line_intersect_decide(si, sj) == 361) {//p1-p3-p4-p2の順
            si.seta(p1);
            si.setb(p3);

            addsenbun(p2, p4, si.getcolor());
            int jyuufuku_col;
            jyuufuku_col = si.getcolor();
            if (i < j) {
                jyuufuku_col = sj.getcolor();
            }
            sj.setcolor(jyuufuku_col);

            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 362) {//p1-p4-p3-p2の順
            si.seta(p1);
            si.setb(p4);

            addsenbun(p2, p3, si.getcolor());

            int jyuufuku_col;
            jyuufuku_col = si.getcolor();
            if (i < j) {
                jyuufuku_col = sj.getcolor();
            }
            sj.setcolor(jyuufuku_col);

            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 363) {//p3-p1-p2-p4の順
            sj.seta(p1);
            sj.setb(p3);

            addsenbun(p2, p4, sj.getcolor());

            int jyuufuku_col;
            jyuufuku_col = si.getcolor();
            if (i < j) {
                jyuufuku_col = sj.getcolor();
            }
            si.setcolor(jyuufuku_col);

            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 364) {//p3-p2-p1-p4の順
            sj.seta(p1);
            sj.setb(p4);

            addsenbun(p2, p3, sj.getcolor());

            int jyuufuku_col;
            jyuufuku_col = si.getcolor();
            if (i < j) {
                jyuufuku_col = sj.getcolor();
            }
            si.setcolor(jyuufuku_col);

            return 1;
        }

        //
        if (oc.line_intersect_decide(si, sj) == 371) {//p1-p3-p2-p4の順
            //System.out.println("371");
            si.seta(p1);
            si.setb(p3);

            sj.seta(p2);
            sj.setb(p4);

            int jyuufuku_col;
            jyuufuku_col = si.getcolor();
            if (i < j) {
                jyuufuku_col = sj.getcolor();
            }
            addsenbun(p2, p3, jyuufuku_col);
            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 372) {//p1-p4-p2-p3の順
            //System.out.println("372");
            si.seta(p1);
            si.setb(p4);

            sj.seta(p3);
            sj.setb(p2);

            int jyuufuku_col;
            jyuufuku_col = si.getcolor();
            if (i < j) {
                jyuufuku_col = sj.getcolor();
            }
            addsenbun(p2, p4, jyuufuku_col);
            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 373) {//p3-p1-p4-p2の順
            //System.out.println("373");
            sj.seta(p1);
            sj.setb(p3);
            si.seta(p2);
            si.setb(p4);
            int jyuufuku_col;
            jyuufuku_col = si.getcolor();
            if (i < j) {
                jyuufuku_col = sj.getcolor();
            }
            addsenbun(p1, p4, jyuufuku_col);
            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 374) {//p4-p1-p3-p2の順
            //System.out.println("374");
            sj.seta(p1);
            sj.setb(p4);
            si.seta(p3);
            si.setb(p2);
            int jyuufuku_col;
            jyuufuku_col = si.getcolor();
            if (i < j) {
                jyuufuku_col = sj.getcolor();
            }
            addsenbun(p1, p3, jyuufuku_col);
            return 1;
        }

        return 0;
    }


    //線分の追加-------------------------------
    public void addsenbun(Point pi, Point pj, int i_c) {
        total++;

        Line s;
        s = getLine(total);
        s.set(pi, pj, i_c);
    }

    //線分の追加-------------------------------
    public void addsenbun(double ax, double ay, double bx, double by, int ic) {
        total++;

        Line s;
        s = getLine(total);
        s.set(ax, ay, bx, by, ic);
    }

    //線分の追加-------------------------------
    public void addsenbun(Point pi, Point pj) {
        total++;

        Line s;
        s = getLine(total);

        s.seta(pi);
        s.setb(pj);
    }

    //線分の削除-----------------------------------------
    public void delsenbun(int j) {   //j番目の線分を削除する
        for (int i = j; i <= total - 1; i++) {
            Line si;
            si = getLine(i);
            Line si1;
            si1 = getLine(i + 1);
            si.set(si1);

        }
        total--;
    }

    //i番目の線分の長さを得る---------------------------
    public double getnagasa(int i) {
        Line s;
        s = getLine(i);
        return s.getnagasa();
    }

    //閉多角形を形成せず、枝状になっている線分を削除する。
    public void eda_kesi(double r) {
        int iflga = 0;
        int iflgb = 0;
        for (int i = 1; i <= total; i++) {
            iflga = 0;
            iflgb = 0;
            Line si;
            si = getLine(i);
            for (int j = 1; j <= total; j++) {
                if (i != j) {
                    Line sj;
                    sj = getLine(j);
                    if (oc.kyori(si.geta(), sj.geta()) < r) {
                        iflga = 1;
                    }
                    if (oc.kyori(si.geta(), sj.getb()) < r) {
                        iflga = 1;
                    }
                    if (oc.kyori(si.getb(), sj.geta()) < r) {
                        iflgb = 1;
                    }
                    if (oc.kyori(si.getb(), sj.getb()) < r) {
                        iflgb = 1;
                    }
                }
            }

            if ((iflga == 0) || (iflgb == 0)) {
                delsenbun(i);
                i = 1;
            }
        }
    }

    //一本だけの離れてある線分を削除する。
    public void tanSenbun_sakujyo(double r) {
        int iflg = 0;
        for (int i = 1; i <= total; i++) {
            iflg = 0;
            Line si;
            si = getLine(i);
            for (int j = 1; j <= total; j++) {
                if (i != j) {
                    Line sj;
                    sj = getLine(j);
                    if (oc.kyori(si.geta(), sj.geta()) < r) {
                        iflg = 1;
                    }
                    if (oc.kyori(si.getb(), sj.getb()) < r) {
                        iflg = 1;
                    }
                    if (oc.kyori(si.geta(), sj.getb()) < r) {
                        iflg = 1;
                    }
                    if (oc.kyori(si.getb(), sj.geta()) < r) {
                        iflg = 1;
                    }
                }
            }

            if (iflg == 0) {
                delsenbun(i);
                i = 1;
            }
        }
    }


    //点pに近い(r以内)線分をさがし、その番号を返す関数(ただし、j番目の線分は対象外)。近い線分がなければ、0を返す---------------------------------
    //もし対象外にする線分が無い場合は、jを0とか負の整数とかにする。
    //070317　追加機能　j　が　-10　の時は　活性化していない枝（getiactive(i)が0）を対象にする。

    public int senbun_sagasi(Point p, double r, int j) {
        if (j == -10) {
            for (int i = 1; i <= total; i++) {
                if (((senbun_busyo_sagasi(i, p, r) == 1) && (i != j)) && (getiactive(i) == 0)) {
                    return i;
                }
            }
            for (int i = 1; i <= total; i++) {
                if (((senbun_busyo_sagasi(i, p, r) == 2) && (i != j)) && (getiactive(i) == 0)) {
                    return i;
                }
            }
            for (int i = 1; i <= total; i++) {
                if (((senbun_busyo_sagasi(i, p, r) == 3) && (i != j)) && (getiactive(i) == 0)) {
                    return i;
                }
            }
            return 0;
        }

        for (int i = 1; i <= total; i++) {
            if ((senbun_busyo_sagasi(i, p, r) == 1) && (i != j)) {
                return i;
            }
        }
        for (int i = 1; i <= total; i++) {
            if ((senbun_busyo_sagasi(i, p, r) == 2) && (i != j)) {
                return i;
            }
        }
        for (int i = 1; i <= total; i++) {
            if ((senbun_busyo_sagasi(i, p, r) == 3) && (i != j)) {
                return i;
            }
        }
        return 0;
    }


    //点pが指定された線分とどの部所で近い(r以内)かどうかを判定する関数　---------------------------------
    //0=近くない、1=a点に近い、2=b点に近い、3=柄の部分に近い
    public int senbun_busyo_sagasi(int i, Point p, double r) {
        if (r > oc.kyori(p, geta(i))) {
            return 1;
        }//a点に近いかどうか
        if (r > oc.kyori(p, getb(i))) {
            return 2;
        }//b点に近いかどうか
        if (r > oc.kyori_senbun(p, get(i))) {
            return 3;
        }//柄の部分に近いかどうか
        return 0;
    }


    //点pに最も近い線分の番号を返す
    public int mottomo_tikai_senbun_sagasi(Point p) {
        int minrid = 0;
        double minr = 100000;
        for (int i = 1; i <= total; i++) {
            double sk = oc.kyori_senbun(p, get(i));
            if (minr > sk) {
                minr = sk;
                minrid = i;
            }//柄の部分に近いかどうか

        }
        return minrid;
    }


    //点pに最も近い線分の端点を返す
    public Point mottomo_tikai_Ten_sagasi(Point p) {

        Point p_return = new Point();
        p_return.set(100000.0, 100000.0);
        Point p_temp = new Point();
        for (int i = 1; i <= total; i++) {
            p_temp.set(geta(i));
            if (p.kyori2jyou(p_temp) < p.kyori2jyou(p_return)) {
                p_return.set(p_temp.getx(), p_temp.gety());
            }
            p_temp.set(getb(i));
            if (p.kyori2jyou(p_temp) < p.kyori2jyou(p_return)) {
                p_return.set(p_temp.getx(), p_temp.gety());
            }
            //p_temp.set(geta(i));if(p.kyori(p_temp)<p.kyori(p_return) ) {p_return.set(p_temp.getx(),p_temp.gety()); }
            //p_temp.set(getb(i));if(p.kyori(p_temp)<p.kyori(p_return) ) {p_return.set(p_temp.getx(),p_temp.gety()); }

        }
        return p_return;
    }


    //点pの近くの線分の活性化
    public void kasseika(Point p, double r) {
        for (int i = 1; i <= total; i++) {
            Line si;
            si = getLine(i);
            si.kasseika(p, r);
        }
    }

    //全線分の非活性化
    public void hikasseika() {
        for (int i = 1; i <= total; i++) {
            Line si;
            si = getLine(i);
            si.hikasseika();
        }
    }


    //線分の活性化されたものを点pの座標にする
    public void set(Point p) {
        for (int i = 1; i <= total; i++) {
            Line si;
            si = getLine(i);
            si.set(p);
        }

    }

    //線分集合の中の線分i0と、i0以外で、全く重なる線分があれば、その番号を返す。なければ-10を返す。
    public int kasanari_senbun_sagasi(int i0) {
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
