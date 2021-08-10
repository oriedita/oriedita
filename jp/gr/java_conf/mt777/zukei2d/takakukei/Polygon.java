package jp.gr.java_conf.mt777.zukei2d.takakukei;

import java.awt.*;

import jp.gr.java_conf.mt777.zukei2d.senbun.*;
import jp.gr.java_conf.mt777.zukei2d.oritacalc.*;
import jp.gr.java_conf.mt777.seiretu.narabebako.*;
import jp.gr.java_conf.mt777.zukei2d.ten.Point;

public class Polygon {
    String c = "";
    int kakusuu;             //何角形か
    //ArrayList TenList = new ArrayList();

    Point[] t;//頂点

    OritaCalc oc = new OritaCalc();          //各種計算用の関数を使うためのクラスのインスタンス化


    public Polygon(int kaku) {  //コンストラクタ
        kakusuu = kaku;
        Point[] t0 = new Point[kaku + 1];   //頂点
        for (int i = 0; i <= kaku; i++) {
            t0[i] = new Point();
        }
        // red=255;green=0;blue=0;
        t = t0;
    }

    //多角形の角数をセットする
    public void setkakusuu(int kaku) {
        kakusuu = kaku;
    }

    public int getkakusuu() {
        return kakusuu;
    }

    //多角形のi番目の頂点をセットする
    public void set(int i, Point p) {
        t[i].set(p);
    }

    //多角形のi番目の頂点をゲットする
    public Point get(int i) {
        return t[i];
    }

    //点p0を基準に多角形のi番目の頂点をセットする
    public void set(Point p0, int i, Point p) {
        t[i].set(p0.getX() + p.getX(), p0.getY() + p.getY());
    }

    //線分が、この多角形の辺と交差する(true)かしない(false)か判定する関数----------------------------------
    public boolean kousa(LineSegment s0) {
        int itrue = 0;
        // Senbun s0 =new Senbun();
        // s0.set(sa);
        LineSegment s = new LineSegment();
        for (int i = 1; i <= kakusuu - 1; i++) {
            s.set(t[i], t[i + 1]); //線分
            if (oc.line_intersect_decide(s0, s) >= 1) {
                itrue = 1;
            }
        }

        s.set(t[kakusuu], t[1]); //線分
        if (oc.line_intersect_decide(s0, s) >= 1) {
            itrue = 1;
        }

        return itrue == 1;
    }


    //線分s0の全部が凸多角形の外部（境界線は内部とみなさない）に存在するとき0、
    //線分s0が凸多角形の外部と境界線の両方に渡って存在するとき1、
    //線分s0が凸多角形の内部と境界線と外部に渡って存在するとき2、
    //線分s0の全部が凸多角形の境界線に存在するとき3、
    //線分s0が凸多角形の内部と境界線の両方に渡って存在するとき4、
    //線分s0の全部が凸多角形の内部（境界線は内部とみなさない）に存在するとき5、
    //を返す
    public int naibu_gaibu_hantei(LineSegment s0) {

        Narabebako_int_double nbox = new Narabebako_int_double();

        int i_kouten = 0;

        Point[] kouten = new Point[kakusuu * 2 + 3];   //交点
        for (int i = 0; i <= kakusuu * 2 + 2; i++) {
            kouten[i] = new Point();
        }

        //kouten[0].set(s0.geta());

        //s0.geta()
        i_kouten = i_kouten + 1;
        kouten[i_kouten].set(s0.getA());

        //s0.getb()
        i_kouten = i_kouten + 1;
        kouten[i_kouten].set(s0.getB());

        int iflag = 0;//
        int kh = 0; //oc.senbun_kousa_hantei(s0,s)の値の格納用

        LineSegment s = new LineSegment();

        for (int i = 1; i <= kakusuu; i++) {

            if (i == kakusuu) {
                s.set(t[kakusuu], t[1]); //線分
            } else {
                s.set(t[i], t[i + 1]);
            } //線分

            kh = oc.line_intersect_decide(s0, s);

            if (kh == 1) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(oc.findIntersection(s0, s));
            }
            if (kh == 27) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(oc.findIntersection(s0, s));
            }
            if (kh == 28) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(oc.findIntersection(s0, s));
            }
            if (kh == 321) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(s.getB());
            }
            if (kh == 331) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(s.getA());
            }
            if (kh == 341) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(s.getB());
            }
            if (kh == 351) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(s.getA());
            }


            if (kh == 361) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(s.getA());
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(s.getB());
            }
            if (kh == 362) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(s.getA());
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(s.getB());
            }

            if (kh == 371) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(s.getA());
            }
            if (kh == 371) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(s.getB());
            }
            if (kh == 373) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(s.getB());
            }
            if (kh == 374) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(s.getA());
            }

        }


        for (int i = 1; i <= i_kouten; i++) {
            nbox.ire_i_tiisaijyun(new int_double(i, kouten[i].distance(s0.getA())));
        }

        //線分s0の全部が凸多角形の外部（境界線は内部とみなさない）に存在するとき0、
        //線分s0が凸多角形の外部と境界線の両方に渡って存在するとき1、
        //線分s0が凸多角形の内部と境界線と外部に渡って存在するとき2、
        //線分s0の全部が凸多角形の境界線に存在するとき3、
        //線分s0が凸多角形の内部と境界線の両方に渡って存在するとき4、
        //線分s0の全部が凸多角形の内部（境界線は内部とみなさない）に存在するとき5、

//naibu(Ten p){      //0=外部、　1=境界、　2=内部

        int soto = 0;
        int kyoukai = 0;
        int naka = 0;

        int i_nai = 0;

        for (int i = 1; i <= nbox.getsousuu(); i++) {

            i_nai = inside(kouten[nbox.get_int(i)]);
            if (i_nai == 0) {
                soto = 1;
            }
            if (i_nai == 1) {
                kyoukai = 1;
            }
            if (i_nai == 2) {
                naka = 1;
            }

            if (i != nbox.getsousuu()) {
                i_nai = inside(oc.midPoint(kouten[nbox.get_int(i)], kouten[nbox.get_int(i + 1)]));
                if (i_nai == 0) {
                    soto = 1;
                }
                if (i_nai == 1) {
                    kyoukai = 1;
                }
                if (i_nai == 2) {
                    naka = 1;
                }
            }
        }

        //線分s0の全部が凸多角形の外部（境界線は内部とみなさない）に存在するとき0、
        //線分s0が凸多角形の外部と境界線の両方に渡って存在するとき1、
        //線分s0が凸多角形の内部と境界線と外部に渡って存在するとき2、
        //線分s0の全部が凸多角形の境界線に存在するとき3、
        //線分s0が凸多角形の内部と境界線の両方に渡って存在するとき4、
        //線分s0の全部が凸多角形の内部（境界線は内部とみなさない）に存在するとき5、

        int i_r = 0;

        //if(soto==0){if(kyoukai==0){if(naka==0){i_r=-1;}}}
        if (soto == 0) {
            if (kyoukai == 0) {
                if (naka == 1) {
                    i_r = 5;
                }
            }
        }
        if (soto == 0) {
            if (kyoukai == 1) {
                if (naka == 0) {
                    i_r = 3;
                }
            }
        }
        if (soto == 0) {
            if (kyoukai == 1) {
                if (naka == 1) {
                    i_r = 4;
                }
            }
        }
        if (soto == 1) {
            if (kyoukai == 0) {
                if (naka == 0) {
                    i_r = 0;
                }
            }
        }
        //if(soto==1){if(kyoukai==0){if(naka==1){i_r= -2;}}}
        if (soto == 1) {
            if (kyoukai == 1) {
                if (naka == 0) {
                    i_r = 1;
                }
            }
        }
        if (soto == 1) {
            if (kyoukai == 1) {
                if (naka == 1) {
                    i_r = 2;
                }
            }
        }

        return i_r;

//return 0; 
    }


//----------------------------------------------------------------------------------------------

    //線分s0の一部でも凸多角形の内部（境界線は内部とみなさない）に
    //存在するとき1、しないなら0を返す
    public int convex_inside(LineSegment s0) {
        int iflag = 0;//
        int kh = 0; //oc.senbun_kousa_hantei(s0,s)の値の格納用
        // Senbun s0 =new Senbun();
        // s0.set(sa);
        LineSegment s = new LineSegment();
        for (int i = 1; i <= kakusuu - 1; i++) {
            s.set(t[i], t[i + 1]); //線分
            kh = oc.line_intersect_decide(s0, s);
            if (kh == 1) {
                return 1;
            }
            if (kh == 4) {
                return 0;
            }
            if (kh == 5) {
                return 0;
            }
            if (kh == 6) {
                return 0;
            }
            if (kh >= 30) {
                return 0;
            }
            if (kh >= 20) {
                iflag = iflag + 1;
            } //ここは実際にはkhが20以上30未満のときに実行される。
        }

        s.set(t[kakusuu], t[1]); //線分
        kh = oc.line_intersect_decide(s0, s);
        if (kh == 1) {
            return 1;
        }
        if (kh == 4) {
            return 0;
        }
        if (kh == 5) {
            return 0;
        }
        if (kh == 6) {
            return 0;
        }
        if (kh >= 30) {
            return 0;
        }
        if (kh >= 20) {
            iflag = iflag + 1;
        } //ここは実際にはkhが20以上30未満のときに実行される。

        if (iflag == 0) {
            if (inside(new Point(0.5, s0.getA(), 0.5, s0.getB())) == 2) {
                return 1;
            }
            return 0;
        }

        if (iflag == 1) {
            if (inside(new Point(0.5, s0.getA(), 0.5, s0.getB())) == 2) {
                return 1;
            }
            return 0;
        }

        if (iflag == 2) {
            if (inside(new Point(0.5, s0.getA(), 0.5, s0.getB())) == 2) {
                return 1;
            }
            if (inside(s0.getA()) == 2) {
                return 1;
            }
            if (inside(s0.getB()) == 2) {
                return 1;
            }
            return 0;
        }

        if (iflag == 3) {
            return 1;
        }
        if (iflag == 4) {
            return 1;
        }

        return 0;      //実際はここまでたどり着くような状態は起きないはず
    }


    // Even a part of the line segment s0 is inside the convex polygon (the boundary line is also regarded as the inside)
    // Returns 1 if present, 0 otherwise
    public int totu_boundary_inside(LineSegment s0) {// Returns 1 if even part of s0 touches a polygon.
        int iflag = 0;//
        int kh = 0; //oc.senbun_kousa_hantei(s0,s)の値の格納用

        LineSegment s = new LineSegment();
        for (int i = 1; i <= kakusuu - 1; i++) {
            s.set(t[i], t[i + 1]); //線分
            kh = oc.line_intersect_decide(s0, s);
            if (kh != 0) {
                return 1;
            }
        }

        s.set(t[kakusuu], t[1]); //線分
        kh = oc.line_intersect_decide(s0, s);
        if (kh != 0) {
            return 1;
        }

        if (inside(new Point(0.5, s0.getA(), 0.5, s0.getB())) == 2) {
            return 1;
        }


        return 0;
    }


    //点が、この多角形の内部にある(true)かない(false)か判定する関数----------------------------------
    public int inside(Point p) {      //0=外部、　1=境界、　2=内部
        LineSegment s = new LineSegment();
        LineSegment sq = new LineSegment();
        Point q = new Point();

        int kousakaisuu = 0;
        int jyuuji_kousakaisuu = 0;
        int tekisetu = 0;
        double rad = 0.0;//確実に外部にある点を作るときに使うラジアン。

        //まず、点pが多角形の境界線上にあるか判定する。
        for (int i = 1; i <= kakusuu - 1; i++) {
            s.set(t[i], t[i + 1]);
            //if(oc.kyori_senbun(p,s)==0){return 1;}//20201022delete
            if (oc.distance_lineSegment(p, s) < 0.01) {
                return 1;
            }//20201022add
        }
        s.set(t[kakusuu], t[1]);
        //if(oc.kyori_senbun(p,s)==0){return 1;}//20201022delete
        if (oc.distance_lineSegment(p, s) < 0.01) {
            return 1;
        }//20201022add

        //点pが多角形の境界線上に無い場合、内部にあるか外部にあるか判定する

        while (tekisetu == 0) {   //交差回数が0または、すべての交差が十字路型なら適切。
            kousakaisuu = 0;
            jyuuji_kousakaisuu = 0;

            //確実に外部にある点qと、点pで線分を作る。
            rad += 1.0;
            q.set((100000.0 * Math.cos(rad)), (100000.0 * Math.sin(rad))); //<<<<<<<<<<<<<<<<<<

            sq.set(p, q);

            for (int i = 1; i <= kakusuu - 1; i++) {
                s.set(t[i], t[i + 1]); //線分
                if (oc.line_intersect_decide(sq, s, 0.0, 0.0) >= 1) {
                    kousakaisuu++;
                }
                if (oc.line_intersect_decide(sq, s, 0.0, 0.0) == 1) {
                    jyuuji_kousakaisuu++;
                }
            }

            s.set(t[kakusuu], t[1]); //線分
            if (oc.line_intersect_decide(sq, s, 0.0, 0.0) >= 1) {
                kousakaisuu++;
            }
            if (oc.line_intersect_decide(sq, s, 0.0, 0.0) == 1) {
                jyuuji_kousakaisuu++;
            }

            if (kousakaisuu == jyuuji_kousakaisuu) {
                tekisetu = 1;
            }
        }

        if (kousakaisuu % 2 == 1) {
            return 2;
        } //交差回数が奇数なら内部

        //if(jyuuji_kousakaisuu==1){return true; } //交差回数が奇数なら内部
        return 0;
    }

    //多角形の頂点座標を時計回りに順に（x1,y1），（x2,y2），...，（xn,yn）とした場合の面積を求める
    public double menseki_motome() {
        double menseki = 0.0;

        menseki = menseki + (t[kakusuu].getX() - t[2].getX()) * t[1].getY();
        for (int i = 2; i <= kakusuu - 1; i++) {
            menseki = menseki + (t[i - 1].getX() - t[i + 1].getX()) * t[i].getY();
        }
        menseki = menseki + (t[kakusuu - 1].getX() - t[1].getX()) * t[kakusuu].getY();
        menseki = -0.5 * menseki;

        return menseki;
    }

    //ある点と多角形の距離（ある点と多角形の境界上の点の距離の最小値）を求める
    public double distance_find(Point tn) {
        double kyori;
        kyori = oc.distance_lineSegment(tn, t[kakusuu], t[1]);
        for (int i = 1; i <= kakusuu - 1; i++) {
            if (oc.distance_lineSegment(tn, t[i], t[i + 1]) < kyori) {
                kyori = oc.distance_lineSegment(tn, t[i], t[i + 1]);
            }
        }

        return kyori;
    }


    //多角形の内部の点を求める
    public Point insidePoint_find() {
        Point tn = new Point();
        Point tr = new Point();
        double distance;
        distance = -10.0;

        for (int i = 2; i <= kakusuu - 1; i++) {
            tn.set(oc.center(t[i - 1], t[i], t[i + 1]));
            if ((distance < distance_find(tn)) && (inside(tn) == 2)) {
                distance = distance_find(tn);
                tr.set(tn);
            }
        }
        //
        tn.set(oc.center(t[kakusuu - 1], t[kakusuu], t[1]));
        if ((distance < distance_find(tn)) && (inside(tn) == 2)) {
            distance = distance_find(tn);
            tr.set(tn);
        }
        //
        tn.set(oc.center(t[kakusuu], t[1], t[2]));
        if ((distance < distance_find(tn)) && (inside(tn) == 2)) {
            distance = distance_find(tn);
            tr.set(tn);
        }
        //
        return tr;
    }

    //描画-----------------------------------------------------------------
    public void draw(Graphics g) {

        int[] x = new int[100];
        int[] y = new int[100];
        for (int i = 1; i <= kakusuu - 1; i++) {
            x[i] = (int) t[i].getX();
            y[i] = (int) t[i].getY();
        }
        x[0] = (int) t[kakusuu].getX();
        y[0] = (int) t[kakusuu].getY();
        g.fillPolygon(x, y, kakusuu);
    }


    public double get_x_min() {
        double r;
        r = t[1].getX();
        for (int i = 2; i <= kakusuu; i++) {
            if (r > t[i].getX()) {
                r = t[i].getX();
            }
        }
        return r;
    }//多角形のx座標の最小値を求める

    public double get_x_max() {
        double r;
        r = t[1].getX();
        for (int i = 2; i <= kakusuu; i++) {
            if (r < t[i].getX()) {
                r = t[i].getX();
            }
        }
        return r;
    }//多角形のx座標の最大値を求める

    public double get_y_min() {
        double r;
        r = t[1].getY();
        for (int i = 2; i <= kakusuu; i++) {
            if (r > t[i].getY()) {
                r = t[i].getY();
            }
        }
        return r;
    }//多角形のy座標の最小値を求める

    public double get_y_max() {
        double r;
        r = t[1].getY();
        for (int i = 2; i <= kakusuu; i++) {
            if (r < t[i].getY()) {
                r = t[i].getY();
            }
        }
        return r;
    }//多角形のy座標の最大値を求める


}
