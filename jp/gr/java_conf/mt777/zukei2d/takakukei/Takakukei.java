package jp.gr.java_conf.mt777.zukei2d.takakukei;

import java.awt.*;

import jp.gr.java_conf.mt777.zukei2d.ten.*;
import jp.gr.java_conf.mt777.zukei2d.senbun.*;
import jp.gr.java_conf.mt777.zukei2d.oritacalc.*;
import jp.gr.java_conf.mt777.seiretu.narabebako.*;

public class Takakukei {
    String c = "";
    int kakusuu;             //何角形か
    //ArrayList TenList = new ArrayList();

    Ten[] t;//頂点

    OritaCalc oc = new OritaCalc();          //各種計算用の関数を使うためのクラスのインスタンス化


    public Takakukei(int kaku) {  //コンストラクタ
        kakusuu = kaku;
        Ten[] t0 = new Ten[kaku + 1];   //頂点
        for (int i = 0; i <= kaku; i++) {
            t0[i] = new Ten();
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
    public void set(int i, Ten p) {
        t[i].set(p);
    }

    //多角形のi番目の頂点をゲットする
    public Ten get(int i) {
        return t[i];
    }

    //点p0を基準に多角形のi番目の頂点をセットする
    public void set(Ten p0, int i, Ten p) {
        t[i].set(p0.getx() + p.getx(), p0.gety() + p.gety());
    }

    //線分が、この多角形の辺と交差する(true)かしない(false)か判定する関数----------------------------------
    public boolean kousa(Senbun s0) {
        int itrue = 0;
        // Senbun s0 =new Senbun();
        // s0.set(sa);
        Senbun s = new Senbun();
        for (int i = 1; i <= kakusuu - 1; i++) {
            s.set(t[i], t[i + 1]); //線分
            if (oc.senbun_kousa_hantei(s0, s) >= 1) {
                itrue = 1;
            }
        }

        s.set(t[kakusuu], t[1]); //線分
        if (oc.senbun_kousa_hantei(s0, s) >= 1) {
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
    public int naibu_gaibu_hantei(Senbun s0) {

        Narabebako_int_double nbox = new Narabebako_int_double();

        int i_kouten = 0;

        Ten[] kouten = new Ten[kakusuu * 2 + 3];   //交点
        for (int i = 0; i <= kakusuu * 2 + 2; i++) {
            kouten[i] = new Ten();
        }

        //kouten[0].set(s0.geta());

        //s0.geta()
        i_kouten = i_kouten + 1;
        kouten[i_kouten].set(s0.geta());

        //s0.getb()
        i_kouten = i_kouten + 1;
        kouten[i_kouten].set(s0.getb());

        int iflag = 0;//
        int kh = 0; //oc.senbun_kousa_hantei(s0,s)の値の格納用

        Senbun s = new Senbun();

        for (int i = 1; i <= kakusuu; i++) {

            if (i == kakusuu) {
                s.set(t[kakusuu], t[1]); //線分
            } else {
                s.set(t[i], t[i + 1]);
            } //線分

            kh = oc.senbun_kousa_hantei(s0, s);

            if (kh == 1) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(oc.kouten_motome(s0, s));
            }
            if (kh == 27) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(oc.kouten_motome(s0, s));
            }
            if (kh == 28) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(oc.kouten_motome(s0, s));
            }
            if (kh == 321) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(s.getb());
            }
            if (kh == 331) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(s.geta());
            }
            if (kh == 341) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(s.getb());
            }
            if (kh == 351) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(s.geta());
            }


            if (kh == 361) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(s.geta());
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(s.getb());
            }
            if (kh == 362) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(s.geta());
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(s.getb());
            }

            if (kh == 371) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(s.geta());
            }
            if (kh == 371) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(s.getb());
            }
            if (kh == 373) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(s.getb());
            }
            if (kh == 374) {
                i_kouten = i_kouten + 1;
                kouten[i_kouten].set(s.geta());
            }

        }


        for (int i = 1; i <= i_kouten; i++) {
            nbox.ire_i_tiisaijyun(new int_double(i, kouten[i].kyori(s0.geta())));
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

            i_nai = naibu(kouten[nbox.get_int(i)]);
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
                i_nai = naibu(oc.tyuukanten(kouten[nbox.get_int(i)], kouten[nbox.get_int(i + 1)]));
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
    public int totu_naibu(Senbun s0) {
        int iflag = 0;//
        int kh = 0; //oc.senbun_kousa_hantei(s0,s)の値の格納用
        // Senbun s0 =new Senbun();
        // s0.set(sa);
        Senbun s = new Senbun();
        for (int i = 1; i <= kakusuu - 1; i++) {
            s.set(t[i], t[i + 1]); //線分
            kh = oc.senbun_kousa_hantei(s0, s);
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
        kh = oc.senbun_kousa_hantei(s0, s);
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
            if (naibu(new Ten(0.5, s0.geta(), 0.5, s0.getb())) == 2) {
                return 1;
            }
            return 0;
        }

        if (iflag == 1) {
            if (naibu(new Ten(0.5, s0.geta(), 0.5, s0.getb())) == 2) {
                return 1;
            }
            return 0;
        }

        if (iflag == 2) {
            if (naibu(new Ten(0.5, s0.geta(), 0.5, s0.getb())) == 2) {
                return 1;
            }
            if (naibu(s0.geta()) == 2) {
                return 1;
            }
            if (naibu(s0.getb()) == 2) {
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


    //線分s0の一部でも凸多角形の内部（境界線も内部とみなす）に
    //存在するとき1、しないなら0を返す
    public int totu_kyoukai_naibu(Senbun s0) {//s0の一部でも多角形に触れているなら1を返す。
        int iflag = 0;//
        int kh = 0; //oc.senbun_kousa_hantei(s0,s)の値の格納用

        Senbun s = new Senbun();
        for (int i = 1; i <= kakusuu - 1; i++) {
            s.set(t[i], t[i + 1]); //線分
            kh = oc.senbun_kousa_hantei(s0, s);
            if (kh != 0) {
                return 1;
            }
        }

        s.set(t[kakusuu], t[1]); //線分
        kh = oc.senbun_kousa_hantei(s0, s);
        if (kh != 0) {
            return 1;
        }

        if (naibu(new Ten(0.5, s0.geta(), 0.5, s0.getb())) == 2) {
            return 1;
        }


        return 0;
    }


    //点が、この多角形の内部にある(true)かない(false)か判定する関数----------------------------------
    public int naibu(Ten p) {      //0=外部、　1=境界、　2=内部
        Senbun s = new Senbun();
        Senbun sq = new Senbun();
        Ten q = new Ten();

        int kousakaisuu = 0;
        int jyuuji_kousakaisuu = 0;
        int tekisetu = 0;
        double rad = 0.0;//確実に外部にある点を作るときに使うラジアン。

        //まず、点pが多角形の境界線上にあるか判定する。
        for (int i = 1; i <= kakusuu - 1; i++) {
            s.set(t[i], t[i + 1]);
            //if(oc.kyori_senbun(p,s)==0){return 1;}//20201022delete
            if (oc.kyori_senbun(p, s) < 0.01) {
                return 1;
            }//20201022add
        }
        s.set(t[kakusuu], t[1]);
        //if(oc.kyori_senbun(p,s)==0){return 1;}//20201022delete
        if (oc.kyori_senbun(p, s) < 0.01) {
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
                if (oc.senbun_kousa_hantei(sq, s, 0.0, 0.0) >= 1) {
                    kousakaisuu++;
                }
                if (oc.senbun_kousa_hantei(sq, s, 0.0, 0.0) == 1) {
                    jyuuji_kousakaisuu++;
                }
            }

            s.set(t[kakusuu], t[1]); //線分
            if (oc.senbun_kousa_hantei(sq, s, 0.0, 0.0) >= 1) {
                kousakaisuu++;
            }
            if (oc.senbun_kousa_hantei(sq, s, 0.0, 0.0) == 1) {
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

        menseki = menseki + (t[kakusuu].getx() - t[2].getx()) * t[1].gety();
        for (int i = 2; i <= kakusuu - 1; i++) {
            menseki = menseki + (t[i - 1].getx() - t[i + 1].getx()) * t[i].gety();
        }
        menseki = menseki + (t[kakusuu - 1].getx() - t[1].getx()) * t[kakusuu].gety();
        menseki = -0.5 * menseki;

        return menseki;
    }

    //ある点と多角形の距離（ある点と多角形の境界上の点の距離の最小値）を求める
    public double kyori_motome(Ten tn) {
        double kyori;
        kyori = oc.kyori_senbun(tn, t[kakusuu], t[1]);
        for (int i = 1; i <= kakusuu - 1; i++) {
            if (oc.kyori_senbun(tn, t[i], t[i + 1]) < kyori) {
                kyori = oc.kyori_senbun(tn, t[i], t[i + 1]);
            }
        }

        return kyori;
    }


    //多角形の内部の点を求める
    public Ten naibuTen_motome() {
        Ten tn = new Ten();
        Ten tr = new Ten();
        double kyori;
        kyori = -10.0;

        for (int i = 2; i <= kakusuu - 1; i++) {
            tn.set(oc.naisin(t[i - 1], t[i], t[i + 1]));
            if ((kyori < kyori_motome(tn)) && (naibu(tn) == 2)) {
                kyori = kyori_motome(tn);
                tr.set(tn);
            }
        }
        //
        tn.set(oc.naisin(t[kakusuu - 1], t[kakusuu], t[1]));
        if ((kyori < kyori_motome(tn)) && (naibu(tn) == 2)) {
            kyori = kyori_motome(tn);
            tr.set(tn);
        }
        //
        tn.set(oc.naisin(t[kakusuu], t[1], t[2]));
        if ((kyori < kyori_motome(tn)) && (naibu(tn) == 2)) {
            kyori = kyori_motome(tn);
            tr.set(tn);
        }
        //
        return tr;
    }

    //描画-----------------------------------------------------------------
    public void oekaki(Graphics g) {
		/*
		for (int i=1; i<=kakusuu-1; i++ ){
			g.drawLine( t[i].getx(),t[i].gety(),t[i+1].getx(),t[i+1].gety()); //直線
		}
		g.drawLine( t[kakusuu].getx(),t[kakusuu].gety(),t[1].getx(),t[1].gety()); //直線    
		*/
        int[] x = new int[100];
        int[] y = new int[100];
        for (int i = 1; i <= kakusuu - 1; i++) {
            x[i] = (int) t[i].getx();
            y[i] = (int) t[i].gety();
        }
        x[0] = (int) t[kakusuu].getx();
        y[0] = (int) t[kakusuu].gety();
        //  g.setColor(new Color(red,green,blue));
        // g.setColor(Color.yellow);
        g.fillPolygon(x, y, kakusuu);
        // g.setColor(Color.black);
        //   g.drawString("gomi "+c.valueOf(f[1])+" : "+c.valueOf(f[2])+" : "
        //                       +c.valueOf(f[3])+" : "+c.valueOf(f[4]),10,80);
    }


    public double get_x_min() {
        double r;
        r = t[1].getx();
        for (int i = 2; i <= kakusuu; i++) {
            if (r > t[i].getx()) {
                r = t[i].getx();
            }
        }
        return r;
    }//多角形のx座標の最小値を求める

    public double get_x_max() {
        double r;
        r = t[1].getx();
        for (int i = 2; i <= kakusuu; i++) {
            if (r < t[i].getx()) {
                r = t[i].getx();
            }
        }
        return r;
    }//多角形のx座標の最大値を求める

    public double get_y_min() {
        double r;
        r = t[1].gety();
        for (int i = 2; i <= kakusuu; i++) {
            if (r > t[i].gety()) {
                r = t[i].gety();
            }
        }
        return r;
    }//多角形のy座標の最小値を求める

    public double get_y_max() {
        double r;
        r = t[1].gety();
        for (int i = 2; i <= kakusuu; i++) {
            if (r < t[i].gety()) {
                r = t[i].gety();
            }
        }
        return r;
    }//多角形のy座標の最大値を求める


}
