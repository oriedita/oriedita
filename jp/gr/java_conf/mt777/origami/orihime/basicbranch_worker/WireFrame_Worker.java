package jp.gr.java_conf.mt777.origami.orihime.basicbranch_worker;

import jp.gr.java_conf.mt777.origami.dougu.linestore.*;
import jp.gr.java_conf.mt777.origami.dougu.camera.*;

import java.awt.*;

import jp.gr.java_conf.mt777.kiroku.memo.*;

import jp.gr.java_conf.mt777.zukei2d.senbun.*;
import jp.gr.java_conf.mt777.zukei2d.oritacalc.*;
import jp.gr.java_conf.mt777.zukei2d.oritaoekaki.*;
import jp.gr.java_conf.mt777.zukei2d.takakukei.Polygon;
import jp.gr.java_conf.mt777.zukei2d.ten.Point;

// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
public class WireFrame_Worker {
    OritaCalc oc = new OritaCalc(); //各種計算用の関数を使うためのクラスのインスタンス化
    double r = 3.0;                   //基本枝構造の直線の両端の円の半径、枝と各種ポイントの近さの判定基準
    int icol;//線分の色
    int taisyousei;

    Point pa = new Point(); //Vector from the position where the mouse button is pressed to point a
    Point pb = new Point(); //マウスボタンが押された位置からb点までのベクトル

    int ugokasi_mode = 0;    //枝を動かす動作モード。0=なにもしない、1=a点を動かす、2=b点を動かす、3=枝を平行移動 、4=新規追加
    int ieda;              //アクティブな枝の番号

    int i_saigo_no_senbun_no_maru_kaku = 1;    //1描く、0描かない

    WireFrame k = new WireFrame();    //Instantiation of basic branch structure
    // Senbunsyuugou k ;    //基本枝構造
    Polygon gomibako = new Polygon(4);    //ゴミ箱のインスタンス化
    Polygon tyuuoutai = new Polygon(4);    //中央帯のインスタンス化
    double tyuuoutai_xmin = 180.0;
    double tyuuoutai_xmax = 206.0;
    double tyuuoutai_ymin = 50.0;
    double tyuuoutai_ymax = 300.0;

    double kijyun_kakudo = 22.5; //<<<<<<<<<<<<<<<<<<<<<<<基準角度<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    //入力方法用のパラメータ
    int nyuuryoku_houhou = 0;
    int nyuuryoku_kitei = 0;
    int kensa_houhou = 0;
    int nhi = 0;

    Point nhPoint = new Point();
    Point nhPoint1 = new Point();

    int kousi_haba = 24;  //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<格子幅
    int i_kousi_x_min = -1000;
    int i_kousi_x_max = 2000;
    int i_kousi_y_min = -1000;
    int i_kousi_y_max = 1000;
    Camera camera = new Camera();

    //---------------------------------
    // Kihonshi_Syokunin(  Senbunsyuugou k0,double r0 ){  //コンストラクタ
    public WireFrame_Worker(double r0) {  //コンストラクタ
        r = r0;
        ugokasi_mode = 0;
        ieda = 0;
        icol = 0;
        gomibako.set(new Point(10.0, 150.0), 1, new Point(0.0, 0.0));
        gomibako.set(new Point(10.0, 150.0), 2, new Point(50.0, 0.0));
        gomibako.set(new Point(10.0, 150.0), 3, new Point(40.0, 50.0));
        gomibako.set(new Point(10.0, 150.0), 4, new Point(10.0, 50.0));

        tyuuoutai.set(1, new Point(tyuuoutai_xmin, tyuuoutai_ymin));
        tyuuoutai.set(2, new Point(tyuuoutai_xmax, tyuuoutai_ymin));
        tyuuoutai.set(3, new Point(tyuuoutai_xmax, tyuuoutai_ymax));
        tyuuoutai.set(4, new Point(tyuuoutai_xmin, tyuuoutai_ymax));

        taisyousei = 0;
    }


    //---------------------------------
    public void reset() {
        r = 2.0;
        k.reset();
        ugokasi_mode = 0;
        ieda = 0;
        icol = 0;
        taisyousei = 0;

        camera.reset();
    }

    //-----------------------------
    public void setMemo(Memo memo1) {
        k.setMemo(memo1);
    }

    //-----------------------------
    public void setCamera(Camera cam0) {


        //camera.set_camera_id(cam0.get_camera_id());
        camera.set_camera_kagami(cam0.get_camera_kagami());

        camera.setCameraPositionX(cam0.getCameraPositionX());
        camera.setCameraPositionY(cam0.getCameraPositionY());
        camera.setCameraZoomX(cam0.getCameraZoomX());
        camera.setCameraZoomY(cam0.getCameraZoomY());
        camera.setCameraAngle(cam0.getCameraAngle());
        camera.setDisplayPositionX(cam0.getDisplayPositionX());
        camera.setDisplayPositionY(cam0.getDisplayPositionY());
    }

    //-----------------------------
    public void zen_yama_tani_henkan() {
        k.zen_yama_tani_henkan();
    }

    //----------------
    public void eda_kesi(double r) {
        k.eda_kesi(r);
    }

    //--------------------------------------------
    public void set(WireFrame ss) {
        k.set(ss);
    }

    //----------------------------------------------
    public WireFrame get() {
        return k;
    }

    //--------------------------------------------
    public void set_r(double r0) {
        r = r0;
    }

    //--------------------------------------------
    public void set_nyuuryoku_houhou(int i) {
        nyuuryoku_houhou = i;
    }

    //--------------------------------------------
    public void set_nyuuryoku_kitei(int i) {
        nyuuryoku_kitei = i;
        if (nyuuryoku_kitei == 1) {
            kousi_haba = 48;
        }
        if (nyuuryoku_kitei == 2) {
            kousi_haba = 24;
        }
        if (nyuuryoku_kitei == 3) {
            kousi_haba = 48;
        }
    }

    //--------------------------------------------
    public void set_kensa_houhou(int i) {
        kensa_houhou = i;
    }

    //--------------------------------------------
    public int getsousuu() {
        return k.getTotal();
    }


    //-----------------------------
    public void set_kijyun_kakudo(double x) {
        kijyun_kakudo = x;
    } //<<<<<<<<<<<<<<<<<<<<<<<基準角度<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    //------------------------
    public Memo getMemo() {
        return k.getMemo();
    }

    //対称性の指定
    public void settaisyousei(int i) {
        taisyousei = i;
    }

    public void setcolor(int i) {
        icol = i;
    }
    //


    //
   /*
      void 	k_kasseika(Ten t,double r){
       	//Ten p1=new Ten();

       if(nyuuryoku_kitei==1){
       //  p1.set(kitei_idou(p1));}
         t.set(kitei_idou(t));}
         k.kasseika( t,r);
     }

      //
     void k_set(Ten t){
       	Ten p1=new Ten();

       if(nyuuryoku_kitei==1){
         p1.set(kitei_idou(p1));}

         k.set( p1);
     }

      //--------------
      void   k_addsenbun(Ten t1,Ten t2){

	Ten p1=new Ten();
        Ten p2=new Ten();

	p1.set(t1);p2.set(t2);

        if(nyuuryoku_kitei==1){
         p1.set(kitei_idou(p1));
         p2.set(kitei_idou(p2));


	}

      k.addsenbun(p1,p2);
      }

    */

    //---------------------------------
    public int get_ieda() {
        return ieda;
    }


    //不要な線分を消去する-----------------------------------------------
    public void gomisute() {

        for (int i = 1; i <= k.getTotal(); i++) {
            int idel = 0;
            //if(gomibako.naibu(k.geta(i))>0){idel=1;}    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            //if(gomibako.kousa(k.get( i))){idel=1;}

            if (gomibako.convex_inside(k.get(i)) == 1) {
                idel = 1;
            }


            if (idel == 1) {
                k.deleteLineSegment(i);
                i = i - 1;
                ieda = k.getTotal() + 1;    //<<<<<<<<<<<<<<<<<<
            }
        }
    }

    //枝を動かした後の処理を行う関数----------------------------------------------------
    public void eda_atosyori_01() {//枝の長さを変えずに、枝全体を平行移動して微調整する。
        //アクティブな帯の位置を微調整する
        Point ab = new Point(1, k.getB(ieda), -1, k.getA(ieda));//アクティブな枝の、点aから点bへ向かうベクトル
        Point ba = new Point(1, k.getA(ieda), -1, k.getB(ieda));//アクティブな枝の、点aから点bへ向かうベクトル

        int jeda;   //アクティブな枝と近い別の枝
        int jbasyo; //アクティブな枝と近い別の枝のどこが近いのかを示すための番号

        //　アクティブな枝のa点　と　別の枝　との距離が　ｒ　より近い場合

        jeda = k.lineSegment_search(k.getA(ieda), 2 * r, ieda);//アクティブな枝のa点と近い別の枝を求める。
        jbasyo = k.lineSegment_position_search(jeda, k.getA(ieda), 2 * r);//別の枝のどの部所が近いかを求める。
        if ((jeda != 0) && (jbasyo == 1)) { //アクティブな枝のa点と、別の枝のa点が近い場合
            k.seta(ieda, k.getA(jeda));
            k.setb(ieda, new Point(1, k.getA(ieda), 1, ab));//こう書いてもちゃんと動く様なので、このまま使う。
        }
        if ((jeda != 0) && (jbasyo == 2)) { //アクティブな枝のa点と、別の枝のb点が近い場合
            k.seta(ieda, k.getB(jeda));
            k.setb(ieda, new Point(1, k.getA(ieda), 1, ab));
        }

        //　アクティブな枝のb点　と　別の枝　との距離が　ｒ　より近い場合
        jeda = k.lineSegment_search(k.getB(ieda), 2 * r, ieda);//アクティブな枝のb点と近い別の枝を求める。
        jbasyo = k.lineSegment_position_search(jeda, k.getB(ieda), 2 * r);//別の枝のどの部所が近いかを求める。
        if ((jeda != 0) && (jbasyo == 1)) { //アクティブな枝のb点と、別の枝のa点が近い場合
            k.setb(ieda, k.getA(jeda));
            k.seta(ieda, new Point(1, k.getB(ieda), 1, ba));
        }
        if ((jeda != 0) && (jbasyo == 2)) { //アクティブな枝のb点と、別の枝のb点が近い場合
            k.setb(ieda, k.getB(jeda));
            k.seta(ieda, new Point(1, k.getB(ieda), 1, ba));
        }
    }


    //枝を動かした後の処理を行う関数----------------------------------------------------
    public void eda_atosyori_02() {//一端の点だけを移動して反対の端の点は動かさないで微調整する。
        //アクティブな帯の位置を微調整する

        int jeda;   //アクティブな枝と近い別の枝
        int jbasyo; //アクティブな枝と近い別の枝のどこが近いのかを示すための番号
        if (k.getnagasa(ieda) >= r) {
            //　アクティブな枝のa点　と　別の枝との距離が　ｒ　より近い場合
            jeda = k.lineSegment_search(k.getA(ieda), r, ieda);//アクティブな枝のa点と近い別の枝を求める。
            jbasyo = k.lineSegment_position_search(jeda, k.getA(ieda), r);//別の枝のどの部所が近いかを求める。
            if ((jeda != 0) && (jbasyo == 1)) {
                k.seta(ieda, k.getA(jeda));
            }//アクティブな枝のa点と、別の枝のa点が近い場合
            if ((jeda != 0) && (jbasyo == 2)) {
                k.seta(ieda, k.getB(jeda));
            }//アクティブな枝のa点と、別の枝のb点が近い場合

            //　アクティブな枝(ieda)のb点　と　別の枝(jeda)との距離が　ｒ　より近い場合
            jeda = k.lineSegment_search(k.getB(ieda), r, ieda);//アクティブな枝のb点と近い別の枝を求める。
            jbasyo = k.lineSegment_position_search(jeda, k.getB(ieda), r);//別の枝のどの部所が近いかを求める。
            if ((jeda != 0) && (jbasyo == 1)) {
                k.setb(ieda, k.getA(jeda));
            }//アクティブな枝のb点と、別の枝のa点が近い場合
            if ((jeda != 0) && (jbasyo == 2)) {
                k.setb(ieda, k.getB(jeda));
            }//アクティブな枝のb点と、別の枝のb点が近い場合

            //以下は070317に追加 複数の線分が集まった頂点を別の頂点近くに持っていったときの後処理用
//150312　　2*r　を　r　に修正


            //　アクティブな枝のa点　と　別の枝との距離が　ｒ　より近い場合
            jeda = k.lineSegment_search(k.getA(ieda), r, -10);//アクティブなieda枝のa点と近い別の枝を求める。
            jbasyo = k.lineSegment_position_search(jeda, k.getA(ieda), r);//別の枝のどの部所が近いかを求める。

            if ((jeda != 0) && (jbasyo == 1)) {
                k.kasseika(k.getA(jeda), r);
                k.set(k.getA(jeda));
            }//アクティブなieda枝のa点と、別の枝のa点が近い場合
            if ((jeda != 0) && (jbasyo == 2)) {
                k.kasseika(k.getB(jeda), r);
                k.set(k.getB(jeda));
            }//アクティブなieda枝のa点と、別の枝のb点が近い場合

            //　アクティブな枝(ieda)のb点　と　別の枝(jeda)との距離が　ｒ　より近い場合
            jeda = k.lineSegment_search(k.getB(ieda), r, -10);//アクティブなieda枝のb点と近い別の枝を求める。
            jbasyo = k.lineSegment_position_search(jeda, k.getB(ieda), r);//別の枝のどの部所が近いかを求める。

            if ((jeda != 0) && (jbasyo == 1)) {
                k.kasseika(k.getA(jeda), r);
                k.set(k.getA(jeda));
            }//アクティブなieda枝のb点と、別の枝のa点が近い場合
            if ((jeda != 0) && (jbasyo == 2)) {
                k.kasseika(k.getB(jeda), r);
                k.set(k.getB(jeda));
            }//アクティブなieda枝のb点と、別の枝のb点が近い場合
        }

    }

    public void split_seiri() {
        k.split_arrangement();
    }

    public void split_arrangement_for_Smen_hassei() {
        k.bunkatu_seiri_for_Smen_hassei();
    }//kとは線分集合のこと、Senbunsyuugou k =new Senbunsyuugou();

    public void kousabunkatu() {
        k.intersect_divide();
    }

    public void ten_sakujyo() {
        k.point_removal();
    }

    public void ten_sakujyo(double r) {
        k.point_removal(r);
    }

    public void overlapping_line_sakujyo() {
        k.overlapping_line_removal();
    }

    public void overlapping_line_sakujyo(double r) {
        k.overlapping_line_removal(r);
    }


    //------------------------------------------------------------------------------
//基本枝の描画000000000000000000000000000000000000000000000000000000000000000000
//------------------------------------------------------------------------------
    public void oekaki(Graphics g, int iTenkaizuSenhaba) {
        String text = "";//文字列処理用のクラスのインスタンス化
        double d;
        OritaOekaki OO = new OritaOekaki();

        if (kensa_houhou == 1) {//検査用
            int kr = 10;
            g.setColor(Color.red);
            for (int i = 1; i <= k.getTotal(); i++) {
                if (oc.equal(k.getA(i), k.getB(i), r)) {
                    g.fillOval((int) k.getax(i) - kr, (int) k.getay(i) - kr, 2 * kr, 2 * kr); //円
                }
            }
            for (int i = 1; i <= k.getTotal() - 1; i++) {
                for (int j = i + 1; j <= k.getTotal(); j++) {
                    if (oc.line_intersect_decide(k.get(i), k.get(j)) == 31) {
                        OO.widthLine(g, k.get(i), kr, 1);//  太線
                        g.fillOval((int) k.getax(i) - kr, (int) k.getay(i) - kr, 2 * kr, 2 * kr); //円
                        g.fillOval((int) k.getbx(i) - kr, (int) k.getby(i) - kr, 2 * kr, 2 * kr); //円
                    }
                }
            }
        }
        g.setColor(Color.black);

        //-------------------------------
        if (taisyousei > 0) {
            g.setColor(Color.green);
            tyuuoutai.draw(g);
            g.setColor(Color.black);
        }

        //  ごみ箱の描画
        g.setColor(new Color(150, 150, 150));
        //g.setColor(new Color(100,100,100));
        gomibako.draw(g);
        g.setColor(Color.black);
        g.drawString("ごみ箱", 18, 180);

        g.drawString("線分の数　" + k.getTotal(), 30, 50);

        //描画
        Point a = new Point();
        Point b = new Point();
        int ir = (int) r;

        //入力規定が1（格子）の場合の格子線の描画
        if (nyuuryoku_kitei >= 1) {
            g.setColor(Color.gray);
            for (int i = 1; i <= 700 / kousi_haba; i++) {
                g.drawLine(0, kousi_haba * i, 1000, kousi_haba * i); //直線
            }
            for (int i = 1; i <= 1000 / kousi_haba; i++) {
                g.drawLine(kousi_haba * i, 0, kousi_haba * i, 700); //直線
            }
        }

        //対称性がある場合の処理
        g.setColor(new Color(200, 200, 200));
        if (taisyousei > 0) {
            for (int i = 1; i <= k.getTotal(); i++) {
                if ((k.getA(i).getX() < tyuuoutai_xmin)
                        ||
                        (k.getB(i).getX() < tyuuoutai_xmin)) {
                    a.set(k.getA(i));
                    b.set(k.getB(i));
                    a.set(tyuuoutai_xmin + tyuuoutai_xmax - a.getX(), a.getY());
                    b.set(tyuuoutai_xmin + tyuuoutai_xmax - b.getX(), b.getY());

                    g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線
                    g.drawOval((int) a.getX() - ir, (int) a.getY() - ir, 2 * ir, 2 * ir); //円
                    g.drawOval((int) b.getX() - ir, (int) b.getY() - ir, 2 * ir, 2 * ir); //円
                }
            }
        }
        g.setColor(Color.black);

        //対称性があってもなくても行う描画
        double L = 100.0;
        if (icol == -2) { //角度系用icol=-2
            g.setColor(new Color(245, 245, 245));
            for (int i = 1; i <= k.getTotal(); i++) {
                a.set(k.getA(i));
                b.set(k.getB(i));
                d = 0.0;
                while (d < 360.0) {
                    g.drawLine((int) a.getX(), (int) a.getY(), (int) (a.getX() + L * Math.cos(d * 3.14159265 / 180.0)), (int) (a.getY() + L * Math.sin(d * 3.14159265 / 180.0))); //直線
                    g.drawLine((int) b.getX(), (int) b.getY(), (int) (b.getX() + L * Math.cos(d * 3.14159265 / 180.0)), (int) (b.getY() + L * Math.sin(d * 3.14159265 / 180.0))); //直線
                    d = d + kijyun_kakudo;
                }
            }

            if (ieda <= k.getTotal()) {
                g.setColor(new Color(205, 245, 245));
                d = 0.0;
                a.set(k.getA(ieda));
                b.set(k.getB(ieda));
                if (ugokasi_mode == 1) {
                    while (d < 360.0) {
                        g.drawLine((int) a.getX(), (int) a.getY(), (int) (a.getX() + L * Math.cos(d * 3.14159265 / 180.0)), (int) (a.getY() + L * Math.sin(d * 3.14159265 / 180.0))); //直線
                        //g.drawLine( (int)b.getx(),(int)b.gety(),(int)(b.getx()+L*Math.cos(d*3.14159265/180.0)),(int)(b.gety()+L*Math.sin(d*3.14159265/180.0))); //直線
                        d = d + kijyun_kakudo;
                    }
                }
                if (ugokasi_mode == 2) {
                    while (d < 360.0) {
                        //g.drawLine( (int)a.getx(),(int)a.gety(),(int)(a.getx()+L*Math.cos(d*3.14159265/180.0)),(int)(a.gety()+L*Math.sin(d*3.14159265/180.0))); //直線
                        g.drawLine((int) b.getX(), (int) b.getY(), (int) (b.getX() + L * Math.cos(d * 3.14159265 / 180.0)), (int) (b.getY() + L * Math.sin(d * 3.14159265 / 180.0))); //直線
                        d = d + kijyun_kakudo;
                    }
                }

                if (ugokasi_mode == 3) {
                    while (d < 360.0) {
                        g.drawLine((int) a.getX(), (int) a.getY(), (int) (a.getX() + L * Math.cos(d * 3.14159265 / 180.0)), (int) (a.getY() + L * Math.sin(d * 3.14159265 / 180.0))); //直線
                        g.drawLine((int) b.getX(), (int) b.getY(), (int) (b.getX() + L * Math.cos(d * 3.14159265 / 180.0)), (int) (b.getY() + L * Math.sin(d * 3.14159265 / 180.0))); //直線
                        d = d + kijyun_kakudo;
                    }
                }

                if (ugokasi_mode == 4) {
                    while (d < 360.0) {
                        //g.drawLine( (int)a.getx(),(int)a.gety(),(int)(a.getx()+L*Math.cos(d*3.14159265/180.0)),(int)(a.gety()+L*Math.sin(d*3.14159265/180.0))); //直線
                        g.drawLine((int) b.getX(), (int) b.getY(), (int) (b.getX() + L * Math.cos(d * 3.14159265 / 180.0)), (int) (b.getY() + L * Math.sin(d * 3.14159265 / 180.0))); //直線
                        d = d + kijyun_kakudo;
                    }
                }
            }
        }

/*
      int itemp=0;
      for (int i=1; i<=k.getsousuu()-1; i++ ){
      for (int j=i+1; j<=k.getsousuu(); j++ ){
            if(oc.senbun_kousa_hantei(k.get(i),k.get(j))!=0) {
             g.drawString("交差点　"+text.valueOf(oc.senbun_kousa_hantei(k.get(i),k.get(j)) ),30,150+10*itemp);
	     itemp++;
	    }

      }
      }
*/

        for (int i = 1; i <= k.getTotal(); i++) {
            a.set(k.getA(i));
            b.set(k.getB(i));
            if (k.getColor(i) == 0) {
                g.setColor(Color.black);
            }
            if (k.getColor(i) == 1) {
                g.setColor(Color.red);
            }
            if (k.getColor(i) == 2) {
                g.setColor(Color.blue);
            }
            if (icol == -1) {
			}
            if (icol == -2) {
                g.setColor(new Color(100, 200, 0));//竹色
                if (oc.kakudozure(k.get(i), kijyun_kakudo) < 2.0) {
                    g.setColor(new Color(200, 100, 0));//おうど色
                }
                if (oc.kakudozure(k.get(i), kijyun_kakudo) < 0.5) {
                    g.setColor(Color.black);
                }
            }
            g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線

            if (iTenkaizuSenhaba != 1) {
                OO.widthLine(g, k.get(i), iTenkaizuSenhaba, k.getColor(i));
            }//  太線
            //  OO.habaLine( g,k.get(i),ir,k.getcolor(i));//  太線

            g.setColor(Color.white);
            g.fillOval((int) a.getX() - ir, (int) a.getY() - ir, 2 * ir, 2 * ir); //円
            g.fillOval((int) b.getX() - ir, (int) b.getY() - ir, 2 * ir, 2 * ir); //円

            g.setColor(Color.black);
            g.drawOval((int) a.getX() - ir, (int) a.getY() - ir, 2 * ir, 2 * ir); //円
            g.drawOval((int) b.getX() - ir, (int) b.getY() - ir, 2 * ir, 2 * ir); //円
        }
        g.setColor(Color.black);
    }


    //------------------------------------------------------------------------------
//基本枝の描画111111111111111111111111111111111111111111111111111111111111111111
//------------------------------------------------------------------------------
    public void oekaki_with_camera(Graphics g, int iTenkaizuSenhaba) {
        String text = "";//文字列処理用のクラスのインスタンス化
        double d;
        OritaOekaki OO = new OritaOekaki();

        LineSegment s_tv = new LineSegment();
        Point a = new Point();
        Point b = new Point();
        int ir = (int) (r * camera.getCameraZoomX());

        //格子線の描画
        LineSegment s_ob = new LineSegment();
        //入力規定が1か2（正方格子）の場合の格子線の描画


        if ((nyuuryoku_kitei == 1) || (nyuuryoku_kitei == 2)) {
            g.setColor(Color.gray);
            for (int i = i_kousi_y_min / kousi_haba; i <= i_kousi_y_max / kousi_haba; i++) {
//				g.drawLine( 	i_kousi_x_min,	kousi_haba*i,	i_kousi_x_max,	kousi_haba*i); //直線
                s_ob.set(i_kousi_x_min, kousi_haba * i, i_kousi_x_max, kousi_haba * i);
                s_tv.set(camera.object2TV(s_ob));
                g.drawLine((int) s_tv.getAx(), (int) s_tv.getay(), (int) s_tv.getbx(), (int) s_tv.getby()); //直線

            }
            for (int i = i_kousi_x_min / kousi_haba; i <= i_kousi_x_max / kousi_haba; i++) {
//				g.drawLine(	kousi_haba*i,	i_kousi_y_min,	kousi_haba*i,	i_kousi_y_max); //直線
                s_ob.set(kousi_haba * i, i_kousi_y_min, kousi_haba * i, i_kousi_y_max);
                s_tv.set(camera.object2TV(s_ob));
                g.drawLine((int) s_tv.getAx(), (int) s_tv.getay(), (int) s_tv.getbx(), (int) s_tv.getby()); //直線

            }


            //格子点に丸を描く

            g.setColor(Color.gray);
            Point t_ob = new Point();
            Point t_tv = new Point();
            for (int i = i_kousi_x_min / kousi_haba; i <= i_kousi_x_max / kousi_haba; i++) {
                for (int j = i_kousi_y_min / kousi_haba; j <= i_kousi_y_max / kousi_haba; j++) {
                    t_ob.set(kousi_haba * i, kousi_haba * j);
                    t_tv.set(camera.object2TV(t_ob));
                    g.drawOval((int) t_tv.getX() - ir, (int) t_tv.getY() - ir, 2 * ir, 2 * ir); //円

                }
            }


        }

        //入力規定が1か3（三角格子）の場合の格子線の描画


        double sqr3 = 1.732051;
        double sankaku_kousi_takasa;
        sankaku_kousi_takasa = kousi_haba * sqr3 / 2.0;
        if (nyuuryoku_kitei == 3) {
            g.setColor(Color.gray);
            for (int i = -30; i <= 30; i++) {
//				g.drawLine( 	-3000,	sankaku_kousi_haba*i,	3000,	sankaku_kousi_haba*i); //直線
                s_ob.set(-3000, sankaku_kousi_takasa * i, 3000, sankaku_kousi_takasa * i);
                s_tv.set(camera.object2TV(s_ob));
                g.drawLine((int) s_tv.getAx(), (int) s_tv.getay(), (int) s_tv.getbx(), (int) s_tv.getby()); //直線

            }

            //  y=sqr3*x + 2.0*sankaku_kousi_haba*i
            for (int i = -30; i <= 30; i++) {
                s_ob.set(-3000, -3000.0 * sqr3 + 2.0 * sankaku_kousi_takasa * i, 3000, 3000.0 * sqr3 + 2.0 * sankaku_kousi_takasa * i);
                s_tv.set(camera.object2TV(s_ob));
                g.drawLine((int) s_tv.getAx(), (int) s_tv.getay(), (int) s_tv.getbx(), (int) s_tv.getby()); //直線
            }


            //  y=-sqr3*x + sankaku_kousi_haba*i
            for (int i = -30; i <= 30; i++) {
                s_ob.set(-3000, -3000.0 * (-sqr3) + 2.0 * sankaku_kousi_takasa * i, 3000, 3000.0 * (-sqr3) + 2.0 * sankaku_kousi_takasa * i);
                s_tv.set(camera.object2TV(s_ob));
                g.drawLine((int) s_tv.getAx(), (int) s_tv.getay(), (int) s_tv.getbx(), (int) s_tv.getby()); //直線
            }

            //格子点に丸を描く

            g.setColor(Color.gray);
            Point t_ob = new Point();
            Point t_tv = new Point();
            for (int i = -30; i <= 30; i++) {
                for (int j = -30; j <= 30; j = j + 2) {
                    t_ob.set(kousi_haba * i, sankaku_kousi_takasa * j);
                    t_tv.set(camera.object2TV(t_ob));
                    g.drawOval((int) t_tv.getX() - ir, (int) t_tv.getY() - ir, 2 * ir, 2 * ir); //円
                }
                for (int j = -31; j <= 31; j = j + 2) {
                    t_ob.set(kousi_haba * i + kousi_haba / 2.0, sankaku_kousi_takasa * j);
                    t_tv.set(camera.object2TV(t_ob));
                    g.drawOval((int) t_tv.getX() - ir, (int) t_tv.getY() - ir, 2 * ir, 2 * ir); //円
                }

            }


        }


        //camera中心を十字で描く
        OO.jyuuji(g, camera.object2TV(camera.get_camera_ichi()), 5.0, 2.0, 3);


        //展開図の描画

        for (int i = 1; i <= k.getTotal() - 1; i++) {

            if (k.getColor(i) == 0) {
                g.setColor(Color.black);
            }
            if (k.getColor(i) == 1) {
                g.setColor(Color.red);
            }
            if (k.getColor(i) == 2) {
                g.setColor(Color.blue);
            }

            s_tv.set(camera.object2TV(k.get(i)));
            a.set(s_tv.getA());
            b.set(s_tv.getB());

            g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線
            if (iTenkaizuSenhaba != 1) {
                OO.widthLine(g, s_tv, iTenkaizuSenhaba, k.getColor(i));
            }//  太線

            //OO.habaLine( g,s_tv,iTenkaizuSenhaba,k.getcolor(i));

            g.setColor(Color.white);
            g.fillOval((int) a.getX() - ir, (int) a.getY() - ir, 2 * ir, 2 * ir); //円
            g.fillOval((int) b.getX() - ir, (int) b.getY() - ir, 2 * ir, 2 * ir); //円

            g.setColor(Color.black);
            g.drawOval((int) a.getX() - ir, (int) a.getY() - ir, 2 * ir, 2 * ir); //円
            g.drawOval((int) b.getX() - ir, (int) b.getY() - ir, 2 * ir, 2 * ir); //円
        }

        int i = k.getTotal();

        if (i != 0) {

            if (k.getColor(i) == 0) {
                g.setColor(Color.black);
            }
            if (k.getColor(i) == 1) {
                g.setColor(Color.red);
            }
            if (k.getColor(i) == 2) {
                g.setColor(Color.blue);
            }

            s_tv.set(camera.object2TV(k.get(i)));
            a.set(s_tv.getA());
            b.set(s_tv.getB());

            g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線
            if (iTenkaizuSenhaba != 1) {
                OO.widthLine(g, s_tv, iTenkaizuSenhaba, k.getColor(i));
            }//  太線

            if (i_saigo_no_senbun_no_maru_kaku == 1) {
                g.setColor(Color.white);
                g.fillOval((int) a.getX() - ir, (int) a.getY() - ir, 2 * ir, 2 * ir); //円
                g.fillOval((int) b.getX() - ir, (int) b.getY() - ir, 2 * ir, 2 * ir); //円

                g.setColor(Color.black);
                g.drawOval((int) a.getX() - ir, (int) a.getY() - ir, 2 * ir, 2 * ir); //円
                g.drawOval((int) b.getX() - ir, (int) b.getY() - ir, 2 * ir, 2 * ir); //円

            }
        }


        g.setColor(Color.black);
    }


//--------------------------------------------------------------------------------------
//マウス操作----------------------------------------------------------------------------
//--------------------------------------------------------------------------------------


    //マウス操作(ボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed(Point p) {
        //ゴミ箱がクリックされたら、単独の線分を削除する

        if (gomibako.inside(p) >= 1) {
            k.tanSenbun_sakujyo(r);
            nhi = 0;//入力方法が多角形の場合の初期化を行う
        }

        //
        if (nyuuryoku_houhou != 2) {
            nhi = 0;
        }

        if (nyuuryoku_houhou <= 1) {

            if (nyuuryoku_houhou == 1) {
                int mtsid = k.mottomo_tikai_lineSegment_Search(p);
                k.addLine(k.getB(mtsid), p);

                k.set(mtsid, k.getA(mtsid), p, k.getColor(mtsid), 0);
                k.setColor(k.getTotal(), k.getColor(mtsid));
                if (icol >= 0) {
                    k.set(mtsid, k.getA(mtsid), p, icol, 0);
                    k.setColor(k.getTotal(), icol);

                }
            }

            //マウスと近い位置にある枝を探す。
            ieda = k.lineSegment_search(p, r, 0);
            //基本枝構造の中の、どの枝とも遠い場合。
            if (ieda == 0) {
                k.addLine(p, p);
                ieda = k.getTotal();
                ugokasi_mode = 4;
                if (icol >= 0) {
                    k.setColor(ieda, icol);
                }
            }
            //基本枝構造の中の、どれかの枝に近い場合。
            else {
                if (1 == k.lineSegment_position_search(ieda, p, r)) {
                    ugokasi_mode = 1;
                } //a点に近い場合。
                if (2 == k.lineSegment_position_search(ieda, p, r)) {
                    ugokasi_mode = 2;
                } //b点に近い場合。
                if (3 == k.lineSegment_position_search(ieda, p, r)) {                 //柄の部分に近い場合。
                    pa.set(1, k.getA(ieda), -1, p);
                    pb.set(1, k.getB(ieda), -1, p);
                    ugokasi_mode = 3;
                    if (icol >= 0) {
                        k.setColor(ieda, icol);
                    }
                }
            }

            System.out.print("ieda = ");
            System.out.println(ieda);
          /*     System.out.print("6:") ;
		System.out.print(k.getax(6)) ;System.out.print(",") ;
	        System.out.print(k.getay(6)) ;System.out.print(",") ;
		System.out.print(k.getbx(6)) ;System.out.print(",") ;
	        System.out.print(k.getby(6)) ;System.out.println(",");
                System.out.print("7:") ;
		System.out.print(k.getax(7)) ;System.out.print(",") ;
	        System.out.print(k.getay(7)) ;System.out.print(",") ;
		System.out.print(k.getbx(7)) ;System.out.print(",") ;
	        System.out.print(k.getby(7)) ;System.out.println(",,,") ;
                System.out.println(oc.senbun_kousa_hantei(k.get(6),k.get(7)));
	   */
            k.kasseika(p, r);
        }
        //--------------
        if (nyuuryoku_houhou == 2) {
	    /*     ugokasi_mode=0;
	     		// 枝の平行移動はできるようにする。
                ieda=k.senbun_sagasi(p,r,0);
				//基本枝構造の中の、どれかの枝に近い場合。
		if( ieda!=0){
			if(3==k.senbun_busyo_sagasi(ieda,p,r)){                 //柄の部分に近い場合。
				pa.set(1,k.geta(ieda),-1,p);
				pb.set(1,k.getb(ieda),-1,p);
				ugokasi_mode=3 ;
				if(icol>=0){k.setcolor(ieda,icol);}
			}
		k.kasseika(p,r);
                }

		//


	        //---------

	       else{ */

            nhi = nhi + 1;

            //System.out.print("nh1 = ");System.out.print("nh1 = ")

            if (nhi == 1) {
                k.addLine(p, p);
                nhPoint = p;
                for (int i = 1; i <= k.getTotal() - 1; i++) {
                    if (oc.equal(k.getA(i), p, 2.0 * r)) {
                        k.set(k.getTotal(), k.getA(i), k.getA(i), 0, 0);
                        nhPoint = k.getA(i);
                        break;
                    }
                    if (oc.equal(k.getB(i), p, 2.0 * r)) {
                        k.set(k.getTotal(), k.getB(i), k.getB(i), 0, 0);
                        nhPoint = k.getB(i);
                        break;
                    }
                }


            }
            if (nhi != 1) {   // set(int i, Ten p,Ten q,int ic,int ia)

                k.addLine(nhPoint, p);
                for (int i = 1; i <= k.getTotal() - 1; i++) {
                    if (oc.equal(k.getA(i), p, 2.0 * r)) {
                        k.set(k.getTotal(), k.getA(i), k.getB(k.getTotal() - 1), 0, 0);
                        nhi = 0;
                        break;
                    }
                    if (oc.equal(k.getB(i), p, 2.0 * r)) {
                        k.set(k.getTotal(), k.getB(i), k.getB(k.getTotal() - 1), 0, 0);
                        nhi = 0;
                        break;
                    }
                }
                nhPoint = p;
            }

            if (icol >= 0) {
                k.setColor(k.getTotal(), icol);
            }
        }

        //--------------
        if (nyuuryoku_houhou == 2) {

        }


    }

    //マウス操作(ドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged(Point p) {

        if (nyuuryoku_houhou <= 1) {

            if (ugokasi_mode == 1) {
                k.set(p);
            } //a点を変更
            if (ugokasi_mode == 2) {
                k.set(p);
            } //b点を変更
            if (ugokasi_mode == 3) {//枝を平行移動
                k.seta(ieda, new Point(1, p, 1, pa));
                k.setb(ieda, new Point(1, p, 1, pb));
            }
            if (ugokasi_mode == 4) {
                k.set(p);
            } //a点を変更(ugokasi_mode==1)と同じ動作をする

        }
        //--------------
        if (nyuuryoku_houhou == 1) {
	    /*
		if(ugokasi_mode==3){//枝を平行移動
			k.seta(ieda,new Ten(1,p,1,pa));
			k.setb(ieda,new Ten(1,p,1,pb));
		}
	     */
        }

        //--------------
        if (nyuuryoku_houhou == 2) {

        }


    }

    //マウス操作(ボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased(Point p) {

        if (nyuuryoku_houhou <= 1) {

            if (ugokasi_mode == 1) {
                k.set(p);
            } //a点を変更
            if (ugokasi_mode == 2) {
                k.set(p);
            } //b点を変更
            if (ugokasi_mode == 3) {//枝を平行移動
                k.seta(ieda, new Point(1, p, 1, pa));
                k.setb(ieda, new Point(1, p, 1, pb));
                if (ugokasi_mode == 4) {
                    k.set(p);
                }//a点を変更(ugokasi_mode==1)と同じ動作をする

            }
            //

            //eda_atosyori_01();                //枝の長さを変えずに、枝全体を平行移動して微調整する。<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            eda_atosyori_02();                //一端の点だけを移動して反対の端の点は動かさないで微調整する。 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


            //----------格子に乗せる
            if ((nyuuryoku_kitei == 1) || (nyuuryoku_kitei == 2)) {
                //  int i=get_ieda();
                //  k.seta(i,kitei_idou(k.geta(i)));
                //  k.setb(i,kitei_idou(k.getb(i)));

                // k.set(kitei_idou(p)) ;


                for (int i = 1; i <= k.getTotal(); i++) {
                    // Ten tn=new Ten();
                    //tn.set(k.get(i));
                    //   k.set(i,kitei_idou(k.geta(i)));

                    System.out.print("iactive ");
                    System.out.print(i);
                    System.out.print(": ");
                    System.out.println(k.getiactive(i));
                    if (k.getiactive(i) >= 1) {
                        k.seta(i, kitei_idou(k.getA(i)));
                        k.setb(i, kitei_idou(k.getB(i)));
                    }

                }
                /**/

            }


            k.hikasseika(); //非活性化

            //対称性がある場合の処理
            if (taisyousei > 0) {
                if (((tyuuoutai_xmin < k.getA(ieda).getX()) && (k.getA(ieda).getX() < tyuuoutai_xmax))
                        &&
                        ((tyuuoutai_xmin < k.getB(ieda).getX()) && (k.getB(ieda).getX() < tyuuoutai_xmax))) {
                    k.seta(ieda, new Point((tyuuoutai_xmin + tyuuoutai_xmax) / 2, k.getA(ieda).getY()));
                    k.setb(ieda, new Point((tyuuoutai_xmin + tyuuoutai_xmax) / 2, k.getB(ieda).getY()));
                }
            }

            //ゴミ捨て。　これをやるとアクティブなieda枝の番号がずれるので、一応eda_atosyoriの後でやるようにする。
            if ((ugokasi_mode == 4) && (k.getnagasa(k.getTotal()) < r)) { //新規追加分の線分が点状の（長さがrより小さい）とき削除。
                k.deleteLineSegment(k.getTotal());
            } else {
                gomisute();//ごみ箱に入った線分の消去
            }


        }
        //--------------
        if (nyuuryoku_houhou == 1) {

        }

        //--------------
        if (nyuuryoku_houhou == 2) {

        }
    }


    //000000000000000000000000000000000000000000000000000000000000000000000000000000000
    //マウス操作(i_mouse_modeA==0　旧動作　見本のために残している。削除可----------------------------------------------------
    public void mPressed_A_00(Point p) {
        //ゴミ箱がクリックされたら、単独の線分を削除する
        if (gomibako.inside(p) >= 1) {
            k.tanSenbun_sakujyo(r);
            nhi = 0;//入力方法が多角形の場合の初期化を行う
        }
        // nyuuryoku_houhouは入力方法の指定。0なら通常の方法、1なら多角形入力、２なら直線を指定した点に引き寄せる
        if (nyuuryoku_houhou != 2) {
            nhi = 0;
        }
        if (nyuuryoku_houhou <= 1) {
            if (nyuuryoku_houhou == 1) {
                int mtsid = k.mottomo_tikai_lineSegment_Search(p);
                k.addLine(k.getB(mtsid), p);

                k.set(mtsid, k.getA(mtsid), p, k.getColor(mtsid), 0);
                k.setColor(k.getTotal(), k.getColor(mtsid));
                if (icol >= 0) {
                    k.set(mtsid, k.getA(mtsid), p, icol, 0);
                    k.setColor(k.getTotal(), icol);
                }
            }

            //マウスと近い位置にある枝を探す。
            ieda = k.lineSegment_search(p, r, 0);
            //基本枝構造の中の、どの枝とも遠い場合。
            if (ieda == 0) {
                k.addLine(p, p);
                ieda = k.getTotal();
                ugokasi_mode = 4;
                if (icol >= 0) {
                    k.setColor(ieda, icol);
                }
            }
            //基本枝構造の中の、どれかの枝に近い場合。
            else {
                if (1 == k.lineSegment_position_search(ieda, p, r)) {
                    ugokasi_mode = 1;
                } //a点に近い場合。
                if (2 == k.lineSegment_position_search(ieda, p, r)) {
                    ugokasi_mode = 2;
                } //b点に近い場合。
                if (3 == k.lineSegment_position_search(ieda, p, r)) {                 //柄の部分に近い場合。
                    pa.set(1, k.getA(ieda), -1, p);
                    pb.set(1, k.getB(ieda), -1, p);
                    ugokasi_mode = 3;
                    if (icol >= 0) {
                        k.setColor(ieda, icol);
                    }
                }
            }
            System.out.print("ieda = ");
            System.out.println(ieda);
            k.kasseika(p, r);
        }
        //--------------
        if (nyuuryoku_houhou == 2) {
            nhi = nhi + 1;
            //System.out.print("nh1 = ");System.out.print("nh1 = ")
            if (nhi == 1) {
                k.addLine(p, p);
                nhPoint = p;
                for (int i = 1; i <= k.getTotal() - 1; i++) {
                    if (oc.equal(k.getA(i), p, 2.0 * r)) {
                        k.set(k.getTotal(), k.getA(i), k.getA(i), 0, 0);
                        nhPoint = k.getA(i);
                        break;
                    }
                    if (oc.equal(k.getB(i), p, 2.0 * r)) {
                        k.set(k.getTotal(), k.getB(i), k.getB(i), 0, 0);
                        nhPoint = k.getB(i);
                        break;
                    }
                }
            }
            if (nhi != 1) {   // set(int i, Ten p,Ten q,int ic,int ia)
                k.addLine(nhPoint, p);
                for (int i = 1; i <= k.getTotal() - 1; i++) {
                    if (oc.equal(k.getA(i), p, 2.0 * r)) {
                        k.set(k.getTotal(), k.getA(i), k.getB(k.getTotal() - 1), 0, 0);
                        nhi = 0;
                        break;
                    }
                    if (oc.equal(k.getB(i), p, 2.0 * r)) {
                        k.set(k.getTotal(), k.getB(i), k.getB(k.getTotal() - 1), 0, 0);
                        nhi = 0;
                        break;
                    }
                }
                nhPoint = p;
            }
            if (icol >= 0) {
                k.setColor(k.getTotal(), icol);
            }
        }
    }


    //マウス操作(i_mouse_modeA==0　旧動作　見本のために残している。削除可----------------------------------------------------
    public void mDragged_A_00(Point p) {
        if (nyuuryoku_houhou <= 1) {
            if (ugokasi_mode == 1) {
                k.set(p);
            } //a点を変更
            if (ugokasi_mode == 2) {
                k.set(p);
            } //b点を変更
            if (ugokasi_mode == 3) {        //枝を平行移動
                k.seta(ieda, new Point(1, p, 1, pa));
                k.setb(ieda, new Point(1, p, 1, pb));
            }
            if (ugokasi_mode == 4) {
                k.set(p);
            } //a点を変更(ugokasi_mode==1)と同じ動作をする
        }
        //--------------
        if (nyuuryoku_houhou == 1) {
        }
        //--------------
        if (nyuuryoku_houhou == 2) {
        }
    }

    //マウス操作(i_mouse_modeA==0　旧動作　見本のために残している。削除可----------------------------------------------------
    public void mReleased_A_00(Point p) {
        if (nyuuryoku_houhou <= 1) {
            if (ugokasi_mode == 1) {
                k.set(p);
            } //a点を変更
            if (ugokasi_mode == 2) {
                k.set(p);
            } //b点を変更
            if (ugokasi_mode == 3) {//枝を平行移動
                k.seta(ieda, new Point(1, p, 1, pa));
                k.setb(ieda, new Point(1, p, 1, pb));
                if (ugokasi_mode == 4) {
                    k.set(p);
                }//a点を変更(ugokasi_mode==1)と同じ動作をする
            }
            //eda_atosyori_01();                //枝の長さを変えずに、枝全体を平行移動して微調整する。<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            eda_atosyori_02();                //一端の点だけを移動して反対の端の点は動かさないで微調整する。 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

            //----------格子に乗せる
            if (nyuuryoku_kitei >= 1) {
                for (int i = 1; i <= k.getTotal(); i++) {
                    System.out.print("iactive ");
                    System.out.print(i);
                    System.out.print(": ");
                    System.out.println(k.getiactive(i));
                    if (k.getiactive(i) >= 1) {
                        k.seta(i, kitei_idou(k.getA(i)));
                        k.setb(i, kitei_idou(k.getB(i)));
                    }

                }
            }

            k.hikasseika(); //非活性化

            //対称性がある場合の処理
            if (taisyousei > 0) {
                if (((tyuuoutai_xmin < k.getA(ieda).getX()) && (k.getA(ieda).getX() < tyuuoutai_xmax))
                        &&
                        ((tyuuoutai_xmin < k.getB(ieda).getX()) && (k.getB(ieda).getX() < tyuuoutai_xmax))) {
                    k.seta(ieda, new Point((tyuuoutai_xmin + tyuuoutai_xmax) / 2, k.getA(ieda).getY()));
                    k.setb(ieda, new Point((tyuuoutai_xmin + tyuuoutai_xmax) / 2, k.getB(ieda).getY()));
                }
            }

            //ゴミ捨て。　これをやるとアクティブなieda枝の番号がずれるので、一応eda_atosyoriの後でやるようにする。
            if ((ugokasi_mode == 4) && (k.getnagasa(k.getTotal()) < r)) { //新規追加分の線分が点状の（長さがrより小さい）とき削除。
                k.deleteLineSegment(k.getTotal());
            } else {
                gomisute();//ごみ箱に入った線分の消去
            }
        }
        //--------------
        if (nyuuryoku_houhou == 1) {
        }

        //--------------
        if (nyuuryoku_houhou == 2) {
        }
    }

    //1111111111111111111111111111111111111111111111111111111
    //マウス操作(i_mouse_modeA==1線分入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_01(Point p0) {

        i_saigo_no_senbun_no_maru_kaku = 0;
        Point p = new Point();
        p.set(camera.TV2object(p0));
        k.addLine(p, p);
        ieda = k.getTotal();
        k.setColor(ieda, icol);
    }

    //マウス操作(i_mouse_modeA==1線分入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_01(Point p0) {
        Point p = new Point();
        p.set(camera.TV2object(p0));
        k.seta(ieda, p);
    }

    //マウス操作(i_mouse_modeA==1線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_01(Point p0) {
        i_saigo_no_senbun_no_maru_kaku = 1;
        Point p = new Point();
        p.set(camera.TV2object(p0));
        k.seta(ieda, p);
        eda_atosyori_02();                //一端の点だけを移動して反対の端の点は動かさないで微調整する。 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        //----------格子に乗せる
        if ((nyuuryoku_kitei == 1) || (nyuuryoku_kitei == 2)) {
            //for(int i=1;i<=k.getsousuu();i++){
            //System.out.print("iactive 20150312"); System.out.print(i); System.out.print(": ");
            //System.out.println(k.getiactive(i));
            //if(k.getiactive(i)>=1){
            k.seta(k.getTotal(), kitei_idou(k.getA(k.getTotal())));
            k.setb(k.getTotal(), kitei_idou(k.getB(k.getTotal())));
            //}
            //}
        }


        if (nyuuryoku_kitei == 3) {

            k.seta(k.getTotal(), sankaku_kitei_idou(k.getA(k.getTotal())));
            k.setb(k.getTotal(), sankaku_kitei_idou(k.getB(k.getTotal())));

        }


        //今入力した線分が、今までに入力済みのどれかの線分と全く同じ位置で一致する場合は、今入力した線分の色だけ反映させて、今入力した線分は追加はしない。
        int i_overlapping;
        i_overlapping = k.overlapping_lineSegment_search(k.getTotal());
        if (i_overlapping > 0) {
            k.setColor(i_overlapping, k.getColor(k.getTotal()));
            k.deleteLineSegment(getsousuu());
        }
        k.intersect_divide();//全く重なる線分がない状態で実施される。//2回やらないと反応しない場合がある。原因不明。
        k.intersect_divide();


    }

    //------------------------------------------------------
    public Point kitei_idou(Point t1) {
        Point t_ob = new Point();

        for (int i = i_kousi_x_min / kousi_haba; i <= i_kousi_x_max / kousi_haba; i++) {
            for (int j = i_kousi_y_min / kousi_haba; j <= i_kousi_y_max / kousi_haba; j++) {
                t_ob.set(kousi_haba * i, kousi_haba * j);
                if (oc.distance(t_ob, t1) < r) {
                    return t_ob;
                }

            }
        }


        return t1;
    }

    //------------------------------------------------------
    public Point sankaku_kitei_idou(Point t1) {
        double sqr3 = 1.732051;
        double sankaku_kousi_takasa;
        sankaku_kousi_takasa = kousi_haba * sqr3 / 2.0;
        Point t_ob = new Point();
        for (int i = -30; i <= 30; i++) {
            for (int j = -30; j <= 30; j = j + 2) {
                t_ob.set(kousi_haba * i, sankaku_kousi_takasa * j);
                if (oc.distance(t_ob, t1) < r) {
                    return t_ob;
                }
            }
            for (int j = -31; j <= 31; j = j + 2) {
                t_ob.set(kousi_haba * i + kousi_haba / 2.0, sankaku_kousi_takasa * j);
                if (oc.distance(t_ob, t1) < r) {
                    return t_ob;
                }
            }

        }


        return t1;
    }

/*

			//格子点に丸を描く

			g.setColor(Color.gray);	Ten t_ob =new Ten();	Ten t_tv =new Ten();
			for(int i=-30;i<=30;i++ ){
				for(int j=-30;j<=30;j=j+2 ){
					t_ob.set(kousi_haba*i,sankaku_kousi_takasa*j);
					t_tv.set(camera.object2TV(t_ob));
					g.drawOval( (int)t_tv.getx()-ir,(int)t_tv.gety()-ir,2*ir,2*ir); //円
				}
				for(int j=-31;j<=31;j=j+2 ){
					t_ob.set(kousi_haba*i+kousi_haba/2.0,sankaku_kousi_takasa*j);
					t_tv.set(camera.object2TV(t_ob));
					g.drawOval( (int)t_tv.getx()-ir,(int)t_tv.gety()-ir,2*ir,2*ir); //円
				}

			}
*/


    //22222222222222222222222222222222222222222222222222222222222222
    public void mPressed_A_02(Point p0) {
    }//マウス操作(i_mouse_modeA==2展開図調整　でボタンを押したとき)時の作業

    public void mDragged_A_02(Point p0) {
    }//マウス操作(i_mouse_modeA==2線分入力　でドラッグしたとき)を行う関数

    public void mReleased_A_02(Point p0) {
    }//マウス操作(i_mouse_modeA==2線分入力　でボタンを離したとき)を行う関数

    //3333333333333333333333333333333333333333333333333333333333333
    //マウス操作(i_mouse_modeA==3線分削除　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_03(Point p0) {
    }

    //マウス操作(i_mouse_modeA==3線分削除　　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_03(Point p0) {
    }

    //マウス操作(i_mouse_modeA==3線分削除　　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_03(Point p0) {
        Point p = new Point();
        p.set(camera.TV2object(p0));
        int minrid;
        double minr;
        minrid = k.mottomo_tikai_lineSegment_Search(p);
        if (k.lineSegment_position_search(minrid, p, r) != 0) {
            k.deleteLineSegment(minrid);
        }
    }

    //44444444444444444444444444444444444444444444444444444444444444
    public void mPressed_A_04(Point p0) {
    }//マウス操作(i_mouse_modeA==4線_変換　でボタンを押したとき)時の作業

    public void mDragged_A_04(Point p0) {
    }//マウス操作(i_mouse_modeA==4線_変換　でドラッグしたとき)を行う関数

    public void mReleased_A_04(Point p0) {
        Point p = new Point();
        p.set(camera.TV2object(p0));
        int minrid;
        double minr;
        minrid = k.mottomo_tikai_lineSegment_Search(p);
        if (k.lineSegment_position_search(minrid, p, r) != 0) {
            int ic_temp;
            ic_temp = k.getColor(minrid);
            ic_temp = ic_temp + 1;
            if (ic_temp == 3) {
                ic_temp = 0;
            }
            k.setColor(minrid, ic_temp);
        }


    }//マウス操作(i_mouse_modeA==4線_変換　でボタンを離したとき)を行う関数


}
