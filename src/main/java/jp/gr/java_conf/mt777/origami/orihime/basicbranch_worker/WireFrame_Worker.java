package jp.gr.java_conf.mt777.origami.orihime.basicbranch_worker;

import jp.gr.java_conf.mt777.origami.dougu.linestore.*;
import jp.gr.java_conf.mt777.origami.dougu.camera.*;

import java.awt.*;

import jp.gr.java_conf.mt777.kiroku.memo.*;

import jp.gr.java_conf.mt777.origami.orihime.LineType;
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
    LineType icol;//線分の色
    int taisyousei;

    Point pa = new Point(); //Vector from the position where the mouse button is pressed to point a
    Point pb = new Point(); //マウスボタンが押された位置からb点までのベクトル

    int move_mode = 0;    //Operation mode to move the branches. 0 = Do nothing, 1 = Move point a, 2 = Move point b, 3 = Translate branch, 4 = Add new
    int iActiveBranch;              //Active branch number

    int i_saigo_no_lineSegment_no_maru_kaku = 1;    //1 draw, 0 not draw

    LineSet lineSet = new LineSet();    //Instantiation of basic branch structure
    // Senbunsyuugou k ;    //基本枝構造
    Polygon trash = new Polygon(4);    //Trash instantiation
    Polygon medianStrip = new Polygon(4);    //Median strip instantiation
    double tyuuoutai_xmin = 180.0;
    double tyuuoutai_xmax = 206.0;
    double tyuuoutai_ymin = 50.0;
    double tyuuoutai_ymax = 300.0;

    double kijyun_kakudo = 22.5; //<<<<<<<<<<<<<<<<<<<<<<<基準角度<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    //Parameters for input method
    int input_method = 0;
    int input_rules = 0;
    int inspection_method = 0;
    int nhi = 0;

    Point nhPoint = new Point();
    Point nhPoint1 = new Point();

    int grid_width = 24;  //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Grid width
    int i_grid_x_min = -1000;
    int i_grid_x_max = 2000;
    int i_grid_y_min = -1000;
    int i_grid_y_max = 1000;
    Camera camera = new Camera();

    //---------------------------------
    // Kihonshi_Syokunin(  Senbunsyuugou k0,double r0 ){  //コンストラクタ
    public WireFrame_Worker(double r0) {  //コンストラクタ
        r = r0;
        move_mode = 0;
        iActiveBranch = 0;
        icol = LineType.BLACK_0;
        trash.set(new Point(10.0, 150.0), 1, new Point(0.0, 0.0));
        trash.set(new Point(10.0, 150.0), 2, new Point(50.0, 0.0));
        trash.set(new Point(10.0, 150.0), 3, new Point(40.0, 50.0));
        trash.set(new Point(10.0, 150.0), 4, new Point(10.0, 50.0));

        medianStrip.set(1, new Point(tyuuoutai_xmin, tyuuoutai_ymin));
        medianStrip.set(2, new Point(tyuuoutai_xmax, tyuuoutai_ymin));
        medianStrip.set(3, new Point(tyuuoutai_xmax, tyuuoutai_ymax));
        medianStrip.set(4, new Point(tyuuoutai_xmin, tyuuoutai_ymax));

        taisyousei = 0;
    }


    //---------------------------------
    public void reset() {
        r = 2.0;
        lineSet.reset();
        move_mode = 0;
        iActiveBranch = 0;
        icol = LineType.BLACK_0;
        taisyousei = 0;

        camera.reset();
    }

    //-----------------------------
    public void setMemo(Memo memo1) {
        lineSet.setMemo(memo1);
    }

    //-----------------------------
    public void setCamera(Camera cam0) {
        camera.setCamera(cam0);
    }

    //-----------------------------
    public void zen_yama_tani_henkan() {
        lineSet.all_mountain_valley_change();
    }

    //----------------
    public void branch_trim(double r) {
        lineSet.branch_trim(r);
    }

    //--------------------------------------------
    public void set(LineSet ss) {
        lineSet.set(ss);
    }

    //----------------------------------------------
    public LineSet get() {
        return lineSet;
    }

    //--------------------------------------------
    public void set_r(double r0) {
        r = r0;
    }

    //--------------------------------------------
    public void set_nyuuryoku_houhou(int i) {
        input_method = i;
    }

    //--------------------------------------------
    public void set_nyuuryoku_kitei(int i) {
        input_rules = i;
        if (input_rules == 1) {
            grid_width = 48;
        }
        if (input_rules == 2) {
            grid_width = 24;
        }
        if (input_rules == 3) {
            grid_width = 48;
        }
    }

    //--------------------------------------------
    public void set_kensa_houhou(int i) {
        inspection_method = i;
    }

    //--------------------------------------------
    public int getTotal() {
        return lineSet.getTotal();
    }


    //-----------------------------
    public void set_kijyun_kakudo(double x) {
        kijyun_kakudo = x;
    } //<<<<<<<<<<<<<<<<<<<<<<<基準角度<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    //------------------------
    public Memo getMemo() {
        return lineSet.getMemo();
    }

    //対称性の指定
    public void settaisyousei(int i) {
        taisyousei = i;
    }

    public void setcolor(LineType i) {
        icol = i;
    }
    //

    //---------------------------------
    public int get_ieda() {
        return iActiveBranch;
    }


    //Erase unnecessary line segments-----------------------------------------------
    public void garbageCollect() {

        for (int i = 1; i <= lineSet.getTotal(); i++) {
            int idel = 0;

            if (trash.convex_inside(lineSet.get(i))) {
                idel = 1;
            }

            if (idel == 1) {
                lineSet.deleteLineSegment(i);
                i = i - 1;
                iActiveBranch = lineSet.getTotal() + 1;    //<<<<<<<<<<<<<<<<<<
            }
        }
    }

    //枝を動かした後の処理を行う関数----------------------------------------------------
    public void eda_atosyori_01() {//枝の長さを変えずに、枝全体を平行移動して微調整する。
        //アクティブな帯の位置を微調整する
        Point ab = new Point(1, lineSet.getB(iActiveBranch), -1, lineSet.getA(iActiveBranch));//アクティブな枝の、点aから点bへ向かうベクトル
        Point ba = new Point(1, lineSet.getA(iActiveBranch), -1, lineSet.getB(iActiveBranch));//アクティブな枝の、点aから点bへ向かうベクトル

        int jeda;   //アクティブな枝と近い別の枝
        int jbasyo; //アクティブな枝と近い別の枝のどこが近いのかを示すための番号

        //　アクティブな枝のa点　と　別の枝　との距離が　ｒ　より近い場合

        jeda = lineSet.lineSegment_search(lineSet.getA(iActiveBranch), 2 * r, iActiveBranch);//アクティブな枝のa点と近い別の枝を求める。
        jbasyo = lineSet.lineSegment_position_search(jeda, lineSet.getA(iActiveBranch), 2 * r);//別の枝のどの部所が近いかを求める。
        if ((jeda != 0) && (jbasyo == 1)) { //アクティブな枝のa点と、別の枝のa点が近い場合
            lineSet.setA(iActiveBranch, lineSet.getA(jeda));
            lineSet.setB(iActiveBranch, new Point(1, lineSet.getA(iActiveBranch), 1, ab));//こう書いてもちゃんと動く様なので、このまま使う。
        }
        if ((jeda != 0) && (jbasyo == 2)) { //アクティブな枝のa点と、別の枝のb点が近い場合
            lineSet.setA(iActiveBranch, lineSet.getB(jeda));
            lineSet.setB(iActiveBranch, new Point(1, lineSet.getA(iActiveBranch), 1, ab));
        }

        //　アクティブな枝のb点　と　別の枝　との距離が　ｒ　より近い場合
        jeda = lineSet.lineSegment_search(lineSet.getB(iActiveBranch), 2 * r, iActiveBranch);//アクティブな枝のb点と近い別の枝を求める。
        jbasyo = lineSet.lineSegment_position_search(jeda, lineSet.getB(iActiveBranch), 2 * r);//別の枝のどの部所が近いかを求める。
        if ((jeda != 0) && (jbasyo == 1)) { //アクティブな枝のb点と、別の枝のa点が近い場合
            lineSet.setB(iActiveBranch, lineSet.getA(jeda));
            lineSet.setA(iActiveBranch, new Point(1, lineSet.getB(iActiveBranch), 1, ba));
        }
        if ((jeda != 0) && (jbasyo == 2)) { //アクティブな枝のb点と、別の枝のb点が近い場合
            lineSet.setB(iActiveBranch, lineSet.getB(jeda));
            lineSet.setA(iActiveBranch, new Point(1, lineSet.getB(iActiveBranch), 1, ba));
        }
    }


    //枝を動かした後の処理を行う関数----------------------------------------------------
    public void eda_atosyori_02() {//一端の点だけを移動して反対の端の点は動かさないで微調整する。
        //アクティブな帯の位置を微調整する

        int jeda;   //アクティブな枝と近い別の枝
        int jbasyo; //アクティブな枝と近い別の枝のどこが近いのかを示すための番号
        if (lineSet.getLength(iActiveBranch) >= r) {
            //　アクティブな枝のa点　と　別の枝との距離が　ｒ　より近い場合
            jeda = lineSet.lineSegment_search(lineSet.getA(iActiveBranch), r, iActiveBranch);//アクティブな枝のa点と近い別の枝を求める。
            jbasyo = lineSet.lineSegment_position_search(jeda, lineSet.getA(iActiveBranch), r);//別の枝のどの部所が近いかを求める。
            if ((jeda != 0) && (jbasyo == 1)) {
                lineSet.setA(iActiveBranch, lineSet.getA(jeda));
            }//アクティブな枝のa点と、別の枝のa点が近い場合
            if ((jeda != 0) && (jbasyo == 2)) {
                lineSet.setA(iActiveBranch, lineSet.getB(jeda));
            }//アクティブな枝のa点と、別の枝のb点が近い場合

            //　アクティブな枝(ieda)のb点　と　別の枝(jeda)との距離が　ｒ　より近い場合
            jeda = lineSet.lineSegment_search(lineSet.getB(iActiveBranch), r, iActiveBranch);//アクティブな枝のb点と近い別の枝を求める。
            jbasyo = lineSet.lineSegment_position_search(jeda, lineSet.getB(iActiveBranch), r);//別の枝のどの部所が近いかを求める。
            if ((jeda != 0) && (jbasyo == 1)) {
                lineSet.setB(iActiveBranch, lineSet.getA(jeda));
            }//アクティブな枝のb点と、別の枝のa点が近い場合
            if ((jeda != 0) && (jbasyo == 2)) {
                lineSet.setB(iActiveBranch, lineSet.getB(jeda));
            }//アクティブな枝のb点と、別の枝のb点が近い場合

            //以下は070317に追加 複数の線分が集まった頂点を別の頂点近くに持っていったときの後処理用
//150312　　2*r　を　r　に修正


            //　アクティブな枝のa点　と　別の枝との距離が　ｒ　より近い場合
            jeda = lineSet.lineSegment_search(lineSet.getA(iActiveBranch), r, -10);//アクティブなieda枝のa点と近い別の枝を求める。
            jbasyo = lineSet.lineSegment_position_search(jeda, lineSet.getA(iActiveBranch), r);//別の枝のどの部所が近いかを求める。

            if ((jeda != 0) && (jbasyo == 1)) {
                lineSet.activate(lineSet.getA(jeda), r);
                lineSet.set(lineSet.getA(jeda));
            }//アクティブなieda枝のa点と、別の枝のa点が近い場合
            if ((jeda != 0) && (jbasyo == 2)) {
                lineSet.activate(lineSet.getB(jeda), r);
                lineSet.set(lineSet.getB(jeda));
            }//アクティブなieda枝のa点と、別の枝のb点が近い場合

            //　アクティブな枝(ieda)のb点　と　別の枝(jeda)との距離が　ｒ　より近い場合
            jeda = lineSet.lineSegment_search(lineSet.getB(iActiveBranch), r, -10);//アクティブなieda枝のb点と近い別の枝を求める。
            jbasyo = lineSet.lineSegment_position_search(jeda, lineSet.getB(iActiveBranch), r);//別の枝のどの部所が近いかを求める。

            if ((jeda != 0) && (jbasyo == 1)) {
                lineSet.activate(lineSet.getA(jeda), r);
                lineSet.set(lineSet.getA(jeda));
            }//アクティブなieda枝のb点と、別の枝のa点が近い場合
            if ((jeda != 0) && (jbasyo == 2)) {
                lineSet.activate(lineSet.getB(jeda), r);
                lineSet.set(lineSet.getB(jeda));
            }//アクティブなieda枝のb点と、別の枝のb点が近い場合
        }

    }

    public void split_seiri() {
        lineSet.split_arrangement();
    }

    public void split_arrangement_for_SubFace_generation() {
        lineSet.bunkatu_seiri_for_SubFace_generation();
    }//kとは線分集合のこと、Senbunsyuugou k =new Senbunsyuugou();

    public void kousabunkatu() {
        lineSet.intersect_divide();
    }

    public void ten_sakujyo() {
        lineSet.point_removal();
    }

    public void ten_sakujyo(double r) {
        lineSet.point_removal(r);
    }

    public void overlapping_line_sakujyo() {
        lineSet.overlapping_line_removal();
    }

    public void overlapping_line_sakujyo(double r) {
        lineSet.overlapping_line_removal(r);
    }


    //------------------------------------------------------------------------------
//基本枝の描画000000000000000000000000000000000000000000000000000000000000000000
//------------------------------------------------------------------------------
    public void draw(Graphics g, int iTenkaizuSenhaba) {
        double d;
        OritaDrawing OO = new OritaDrawing();

        if (inspection_method == 1) {//検査用
            int kr = 10;
            g.setColor(Color.red);
            for (int i = 1; i <= lineSet.getTotal(); i++) {
                if (oc.equal(lineSet.getA(i), lineSet.getB(i), r)) {
                    g.fillOval((int) lineSet.getAX(i) - kr, (int) lineSet.getAY(i) - kr, 2 * kr, 2 * kr); //Circle
                }
            }
            for (int i = 1; i <= lineSet.getTotal() - 1; i++) {
                for (int j = i + 1; j <= lineSet.getTotal(); j++) {
                    if (oc.line_intersect_decide(lineSet.get(i), lineSet.get(j)) == IntersectionState.PARALLEL_EQUAL_31) {
                        OO.widthLine(g, lineSet.get(i), kr, LineType.RED_1);//  Thick line
                        g.fillOval((int) lineSet.getAX(i) - kr, (int) lineSet.getAY(i) - kr, 2 * kr, 2 * kr); //Circle
                        g.fillOval((int) lineSet.getBX(i) - kr, (int) lineSet.getBY(i) - kr, 2 * kr, 2 * kr); //Circle
                    }
                }
            }
        }
        g.setColor(Color.black);

        //-------------------------------
        if (taisyousei > 0) {
            g.setColor(Color.green);
            medianStrip.draw(g);
            g.setColor(Color.black);
        }

        //  ごみ箱の描画
        g.setColor(new Color(150, 150, 150));
        //g.setColor(new Color(100,100,100));
        trash.draw(g);
        g.setColor(Color.black);
        g.drawString("ごみ箱", 18, 180);

        g.drawString("線分の数　" + lineSet.getTotal(), 30, 50);

        //描画
        Point a = new Point();
        Point b = new Point();
        int ir = (int) r;

        //入力規定が1（格子）の場合の格子線の描画
        if (input_rules >= 1) {
            g.setColor(Color.gray);
            for (int i = 1; i <= 700 / grid_width; i++) {
                g.drawLine(0, grid_width * i, 1000, grid_width * i); //直線
            }
            for (int i = 1; i <= 1000 / grid_width; i++) {
                g.drawLine(grid_width * i, 0, grid_width * i, 700); //直線
            }
        }

        //対称性がある場合の処理
        g.setColor(new Color(200, 200, 200));
        if (taisyousei > 0) {
            for (int i = 1; i <= lineSet.getTotal(); i++) {
                if ((lineSet.getA(i).getX() < tyuuoutai_xmin)
                        ||
                        (lineSet.getB(i).getX() < tyuuoutai_xmin)) {
                    a.set(lineSet.getA(i));
                    b.set(lineSet.getB(i));
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
        if (icol == LineType.ANGLE) { //Angle system with icol=-2
            g.setColor(new Color(245, 245, 245));
            for (int i = 1; i <= lineSet.getTotal(); i++) {
                a.set(lineSet.getA(i));
                b.set(lineSet.getB(i));
                d = 0.0;
                while (d < 360.0) {
                    g.drawLine((int) a.getX(), (int) a.getY(), (int) (a.getX() + L * Math.cos(d * 3.14159265 / 180.0)), (int) (a.getY() + L * Math.sin(d * 3.14159265 / 180.0))); //直線
                    g.drawLine((int) b.getX(), (int) b.getY(), (int) (b.getX() + L * Math.cos(d * 3.14159265 / 180.0)), (int) (b.getY() + L * Math.sin(d * 3.14159265 / 180.0))); //直線
                    d = d + kijyun_kakudo;
                }
            }

            if (iActiveBranch <= lineSet.getTotal()) {
                g.setColor(new Color(205, 245, 245));
                d = 0.0;
                a.set(lineSet.getA(iActiveBranch));
                b.set(lineSet.getB(iActiveBranch));
                if (move_mode == 1) {
                    while (d < 360.0) {
                        g.drawLine((int) a.getX(), (int) a.getY(), (int) (a.getX() + L * Math.cos(d * 3.14159265 / 180.0)), (int) (a.getY() + L * Math.sin(d * 3.14159265 / 180.0))); //直線
                        //g.drawLine( (int)b.getx(),(int)b.gety(),(int)(b.getx()+L*Math.cos(d*3.14159265/180.0)),(int)(b.gety()+L*Math.sin(d*3.14159265/180.0))); //直線
                        d = d + kijyun_kakudo;
                    }
                }
                if (move_mode == 2) {
                    while (d < 360.0) {
                        //g.drawLine( (int)a.getx(),(int)a.gety(),(int)(a.getx()+L*Math.cos(d*3.14159265/180.0)),(int)(a.gety()+L*Math.sin(d*3.14159265/180.0))); //直線
                        g.drawLine((int) b.getX(), (int) b.getY(), (int) (b.getX() + L * Math.cos(d * 3.14159265 / 180.0)), (int) (b.getY() + L * Math.sin(d * 3.14159265 / 180.0))); //直線
                        d = d + kijyun_kakudo;
                    }
                }

                if (move_mode == 3) {
                    while (d < 360.0) {
                        g.drawLine((int) a.getX(), (int) a.getY(), (int) (a.getX() + L * Math.cos(d * 3.14159265 / 180.0)), (int) (a.getY() + L * Math.sin(d * 3.14159265 / 180.0))); //直線
                        g.drawLine((int) b.getX(), (int) b.getY(), (int) (b.getX() + L * Math.cos(d * 3.14159265 / 180.0)), (int) (b.getY() + L * Math.sin(d * 3.14159265 / 180.0))); //直線
                        d = d + kijyun_kakudo;
                    }
                }

                if (move_mode == 4) {
                    while (d < 360.0) {
                        //g.drawLine( (int)a.getx(),(int)a.gety(),(int)(a.getx()+L*Math.cos(d*3.14159265/180.0)),(int)(a.gety()+L*Math.sin(d*3.14159265/180.0))); //直線
                        g.drawLine((int) b.getX(), (int) b.getY(), (int) (b.getX() + L * Math.cos(d * 3.14159265 / 180.0)), (int) (b.getY() + L * Math.sin(d * 3.14159265 / 180.0))); //直線
                        d = d + kijyun_kakudo;
                    }
                }
            }
        }

        for (int i = 1; i <= lineSet.getTotal(); i++) {
            a.set(lineSet.getA(i));
            b.set(lineSet.getB(i));
            if (lineSet.getColor(i) == LineType.BLACK_0) {
                g.setColor(Color.black);
            }
            if (lineSet.getColor(i) == LineType.RED_1) {
                g.setColor(Color.red);
            }
            if (lineSet.getColor(i) == LineType.BLUE_2) {
                g.setColor(Color.blue);
            }
            if (icol == LineType.NONE) {
			}
            if (icol == LineType.ANGLE) {
                g.setColor(new Color(100, 200, 0));//竹色
                if (oc.angle_difference(lineSet.get(i), kijyun_kakudo) < 2.0) {
                    g.setColor(new Color(200, 100, 0));//おうど色
                }
                if (oc.angle_difference(lineSet.get(i), kijyun_kakudo) < 0.5) {
                    g.setColor(Color.black);
                }
            }
            g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線

            if (iTenkaizuSenhaba != 1) {
                OO.widthLine(g, lineSet.get(i), iTenkaizuSenhaba, lineSet.getColor(i));
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
    public void draw_with_camera(Graphics g, int iTenkaizuSenhaba) {
        String text = "";//文字列処理用のクラスのインスタンス化
        double d;
        OritaDrawing OO = new OritaDrawing();

        LineSegment s_tv = new LineSegment();
        Point a = new Point();
        Point b = new Point();
        int ir = (int) (r * camera.getCameraZoomX());

        //格子線の描画
        LineSegment s_ob = new LineSegment();
        //入力規定が1か2（正方格子）の場合の格子線の描画


        if ((input_rules == 1) || (input_rules == 2)) {
            g.setColor(Color.gray);
            for (int i = i_grid_y_min / grid_width; i <= i_grid_y_max / grid_width; i++) {
//				g.drawLine( 	i_kousi_x_min,	kousi_haba*i,	i_kousi_x_max,	kousi_haba*i); //直線
                s_ob.set(i_grid_x_min, grid_width * i, i_grid_x_max, grid_width * i);
                s_tv.set(camera.object2TV(s_ob));
                g.drawLine((int) s_tv.getAX(), (int) s_tv.getAY(), (int) s_tv.getBX(), (int) s_tv.getBY()); //直線

            }
            for (int i = i_grid_x_min / grid_width; i <= i_grid_x_max / grid_width; i++) {
//				g.drawLine(	kousi_haba*i,	i_kousi_y_min,	kousi_haba*i,	i_kousi_y_max); //直線
                s_ob.set(grid_width * i, i_grid_y_min, grid_width * i, i_grid_y_max);
                s_tv.set(camera.object2TV(s_ob));
                g.drawLine((int) s_tv.getAX(), (int) s_tv.getAY(), (int) s_tv.getBX(), (int) s_tv.getBY()); //直線

            }


            //格子点に丸を描く

            g.setColor(Color.gray);
            Point t_ob = new Point();
            Point t_tv = new Point();
            for (int i = i_grid_x_min / grid_width; i <= i_grid_x_max / grid_width; i++) {
                for (int j = i_grid_y_min / grid_width; j <= i_grid_y_max / grid_width; j++) {
                    t_ob.set(grid_width * i, grid_width * j);
                    t_tv.set(camera.object2TV(t_ob));
                    g.drawOval((int) t_tv.getX() - ir, (int) t_tv.getY() - ir, 2 * ir, 2 * ir); //円

                }
            }


        }

        //入力規定が1か3（三角格子）の場合の格子線の描画


        double sqr3 = 1.732051;
        double sankaku_grid_takasa;
        sankaku_grid_takasa = grid_width * sqr3 / 2.0;
        if (input_rules == 3) {
            g.setColor(Color.gray);
            for (int i = -30; i <= 30; i++) {
//				g.drawLine( 	-3000,	sankaku_kousi_haba*i,	3000,	sankaku_kousi_haba*i); //直線
                s_ob.set(-3000, sankaku_grid_takasa * i, 3000, sankaku_grid_takasa * i);
                s_tv.set(camera.object2TV(s_ob));
                g.drawLine((int) s_tv.getAX(), (int) s_tv.getAY(), (int) s_tv.getBX(), (int) s_tv.getBY()); //直線

            }

            //  y=sqr3*x + 2.0*sankaku_kousi_haba*i
            for (int i = -30; i <= 30; i++) {
                s_ob.set(-3000, -3000.0 * sqr3 + 2.0 * sankaku_grid_takasa * i, 3000, 3000.0 * sqr3 + 2.0 * sankaku_grid_takasa * i);
                s_tv.set(camera.object2TV(s_ob));
                g.drawLine((int) s_tv.getAX(), (int) s_tv.getAY(), (int) s_tv.getBX(), (int) s_tv.getBY()); //直線
            }


            //  y=-sqr3*x + sankaku_kousi_haba*i
            for (int i = -30; i <= 30; i++) {
                s_ob.set(-3000, -3000.0 * (-sqr3) + 2.0 * sankaku_grid_takasa * i, 3000, 3000.0 * (-sqr3) + 2.0 * sankaku_grid_takasa * i);
                s_tv.set(camera.object2TV(s_ob));
                g.drawLine((int) s_tv.getAX(), (int) s_tv.getAY(), (int) s_tv.getBX(), (int) s_tv.getBY()); //直線
            }

            //格子点に丸を描く

            g.setColor(Color.gray);
            Point t_ob = new Point();
            Point t_tv = new Point();
            for (int i = -30; i <= 30; i++) {
                for (int j = -30; j <= 30; j = j + 2) {
                    t_ob.set(grid_width * i, sankaku_grid_takasa * j);
                    t_tv.set(camera.object2TV(t_ob));
                    g.drawOval((int) t_tv.getX() - ir, (int) t_tv.getY() - ir, 2 * ir, 2 * ir); //円
                }
                for (int j = -31; j <= 31; j = j + 2) {
                    t_ob.set(grid_width * i + grid_width / 2.0, sankaku_grid_takasa * j);
                    t_tv.set(camera.object2TV(t_ob));
                    g.drawOval((int) t_tv.getX() - ir, (int) t_tv.getY() - ir, 2 * ir, 2 * ir); //円
                }

            }


        }


        //camera中心を十字で描く
        OO.cross(g, camera.object2TV(camera.get_camera_position()), 5.0, 2.0, LineType.CYAN_3);


        //展開図の描画

        for (int i = 1; i <= lineSet.getTotal() - 1; i++) {

            if (lineSet.getColor(i) == LineType.BLACK_0) {
                g.setColor(Color.black);
            }
            if (lineSet.getColor(i) == LineType.RED_1) {
                g.setColor(Color.red);
            }
            if (lineSet.getColor(i) == LineType.BLUE_2) {
                g.setColor(Color.blue);
            }

            s_tv.set(camera.object2TV(lineSet.get(i)));
            a.set(s_tv.getA());
            b.set(s_tv.getB());

            g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線
            if (iTenkaizuSenhaba != 1) {
                OO.widthLine(g, s_tv, iTenkaizuSenhaba, lineSet.getColor(i));
            }//  太線

            //OO.habaLine( g,s_tv,iTenkaizuSenhaba,k.getcolor(i));

            g.setColor(Color.white);
            g.fillOval((int) a.getX() - ir, (int) a.getY() - ir, 2 * ir, 2 * ir); //円
            g.fillOval((int) b.getX() - ir, (int) b.getY() - ir, 2 * ir, 2 * ir); //円

            g.setColor(Color.black);
            g.drawOval((int) a.getX() - ir, (int) a.getY() - ir, 2 * ir, 2 * ir); //円
            g.drawOval((int) b.getX() - ir, (int) b.getY() - ir, 2 * ir, 2 * ir); //円
        }

        int i = lineSet.getTotal();

        if (i != 0) {

            if (lineSet.getColor(i) == LineType.BLACK_0) {
                g.setColor(Color.black);
            }
            if (lineSet.getColor(i) == LineType.RED_1) {
                g.setColor(Color.red);
            }
            if (lineSet.getColor(i) == LineType.BLUE_2) {
                g.setColor(Color.blue);
            }

            s_tv.set(camera.object2TV(lineSet.get(i)));
            a.set(s_tv.getA());
            b.set(s_tv.getB());

            g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線
            if (iTenkaizuSenhaba != 1) {
                OO.widthLine(g, s_tv, iTenkaizuSenhaba, lineSet.getColor(i));
            }//  太線

            if (i_saigo_no_lineSegment_no_maru_kaku == 1) {
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

        if (trash.inside(p) >= 1) {
            lineSet.singleLineSegment_delete(r);
            nhi = 0;//入力方法が多角形の場合の初期化を行う
        }

        //
        if (input_method != 2) {
            nhi = 0;
        }

        if (input_method <= 1) {

            if (input_method == 1) {
                int mtsid = lineSet.closest_lineSegment_Search(p);
                lineSet.addLine(lineSet.getB(mtsid), p);

                lineSet.set(mtsid, lineSet.getA(mtsid), p, lineSet.getColor(mtsid), 0);
                lineSet.setColor(lineSet.getTotal(), lineSet.getColor(mtsid));
                if (icol.getNumber() >= 0) {
                    lineSet.set(mtsid, lineSet.getA(mtsid), p, icol, 0);
                    lineSet.setColor(lineSet.getTotal(), icol);

                }
            }

            //マウスと近い位置にある枝を探す。
            iActiveBranch = lineSet.lineSegment_search(p, r, 0);
            //基本枝構造の中の、どの枝とも遠い場合。
            if (iActiveBranch == 0) {
                lineSet.addLine(p, p);
                iActiveBranch = lineSet.getTotal();
                move_mode = 4;
                if (icol.getNumber() >= 0) {
                    lineSet.setColor(iActiveBranch, icol);
                }
            }
            //基本枝構造の中の、どれかの枝に近い場合。
            else {
                if (1 == lineSet.lineSegment_position_search(iActiveBranch, p, r)) {
                    move_mode = 1;
                } //a点に近い場合。
                if (2 == lineSet.lineSegment_position_search(iActiveBranch, p, r)) {
                    move_mode = 2;
                } //b点に近い場合。
                if (3 == lineSet.lineSegment_position_search(iActiveBranch, p, r)) {                 //柄の部分に近い場合。
                    pa.set(1, lineSet.getA(iActiveBranch), -1, p);
                    pb.set(1, lineSet.getB(iActiveBranch), -1, p);
                    move_mode = 3;
                    if (icol.getNumber() >= 0) {
                        lineSet.setColor(iActiveBranch, icol);
                    }
                }
            }

            System.out.print("ieda = ");
            System.out.println(iActiveBranch);
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
            lineSet.activate(p, r);
        }
        //--------------
        if (input_method == 2) {
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
                lineSet.addLine(p, p);
                nhPoint = p;
                for (int i = 1; i <= lineSet.getTotal() - 1; i++) {
                    if (oc.equal(lineSet.getA(i), p, 2.0 * r)) {
                        lineSet.set(lineSet.getTotal(), lineSet.getA(i), lineSet.getA(i), LineType.BLACK_0, 0);
                        nhPoint = lineSet.getA(i);
                        break;
                    }
                    if (oc.equal(lineSet.getB(i), p, 2.0 * r)) {
                        lineSet.set(lineSet.getTotal(), lineSet.getB(i), lineSet.getB(i), LineType.BLACK_0, 0);
                        nhPoint = lineSet.getB(i);
                        break;
                    }
                }


            }
            if (nhi != 1) {   // set(int i, Ten p,Ten q,int ic,int ia)

                lineSet.addLine(nhPoint, p);
                for (int i = 1; i <= lineSet.getTotal() - 1; i++) {
                    if (oc.equal(lineSet.getA(i), p, 2.0 * r)) {
                        lineSet.set(lineSet.getTotal(), lineSet.getA(i), lineSet.getB(lineSet.getTotal() - 1), LineType.BLACK_0, 0);
                        nhi = 0;
                        break;
                    }
                    if (oc.equal(lineSet.getB(i), p, 2.0 * r)) {
                        lineSet.set(lineSet.getTotal(), lineSet.getB(i), lineSet.getB(lineSet.getTotal() - 1), LineType.BLACK_0, 0);
                        nhi = 0;
                        break;
                    }
                }
                nhPoint = p;
            }

            if (icol.getNumber() >= 0) {
                lineSet.setColor(lineSet.getTotal(), icol);
            }
        }

        //--------------
        if (input_method == 2) {

        }


    }

    //マウス操作(ドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged(Point p) {

        if (input_method <= 1) {

            if (move_mode == 1) {
                lineSet.set(p);
            } //a点を変更
            if (move_mode == 2) {
                lineSet.set(p);
            } //b点を変更
            if (move_mode == 3) {//枝を平行移動
                lineSet.setA(iActiveBranch, new Point(1, p, 1, pa));
                lineSet.setB(iActiveBranch, new Point(1, p, 1, pb));
            }
            if (move_mode == 4) {
                lineSet.set(p);
            } //a点を変更(ugokasi_mode==1)と同じ動作をする

        }
        //--------------
        if (input_method == 1) {
	    /*
		if(ugokasi_mode==3){//枝を平行移動
			k.seta(ieda,new Ten(1,p,1,pa));
			k.setb(ieda,new Ten(1,p,1,pb));
		}
	     */
        }

        //--------------
        if (input_method == 2) {

        }


    }

    //マウス操作(ボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased(Point p) {

        if (input_method <= 1) {

            if (move_mode == 1) {
                lineSet.set(p);
            } //a点を変更
            if (move_mode == 2) {
                lineSet.set(p);
            } //b点を変更
            if (move_mode == 3) {//枝を平行移動
                lineSet.setA(iActiveBranch, new Point(1, p, 1, pa));
                lineSet.setB(iActiveBranch, new Point(1, p, 1, pb));
                if (move_mode == 4) {
                    lineSet.set(p);
                }//a点を変更(ugokasi_mode==1)と同じ動作をする

            }
            //

            //eda_atosyori_01();                //枝の長さを変えずに、枝全体を平行移動して微調整する。<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            eda_atosyori_02();                //一端の点だけを移動して反対の端の点は動かさないで微調整する。 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


            //----------格子に乗せる
            if ((input_rules == 1) || (input_rules == 2)) {
                //  int i=get_ieda();
                //  k.seta(i,kitei_idou(k.geta(i)));
                //  k.setb(i,kitei_idou(k.getb(i)));

                // k.set(kitei_idou(p)) ;


                for (int i = 1; i <= lineSet.getTotal(); i++) {
                    // Ten tn=new Ten();
                    //tn.set(k.get(i));
                    //   k.set(i,kitei_idou(k.geta(i)));

                    System.out.print("iactive ");
                    System.out.print(i);
                    System.out.print(": ");
                    System.out.println(lineSet.getActive(i));
                    if (lineSet.getActive(i) >= 1) {
                        lineSet.setA(i, kitei_idou(lineSet.getA(i)));
                        lineSet.setB(i, kitei_idou(lineSet.getB(i)));
                    }

                }
                /**/

            }


            lineSet.deactivate(); //非活性化

            //対称性がある場合の処理
            if (taisyousei > 0) {
                if (((tyuuoutai_xmin < lineSet.getA(iActiveBranch).getX()) && (lineSet.getA(iActiveBranch).getX() < tyuuoutai_xmax))
                        &&
                        ((tyuuoutai_xmin < lineSet.getB(iActiveBranch).getX()) && (lineSet.getB(iActiveBranch).getX() < tyuuoutai_xmax))) {
                    lineSet.setA(iActiveBranch, new Point((tyuuoutai_xmin + tyuuoutai_xmax) / 2, lineSet.getA(iActiveBranch).getY()));
                    lineSet.setB(iActiveBranch, new Point((tyuuoutai_xmin + tyuuoutai_xmax) / 2, lineSet.getB(iActiveBranch).getY()));
                }
            }

            //ゴミ捨て。　これをやるとアクティブなieda枝の番号がずれるので、一応eda_atosyoriの後でやるようにする。
            if ((move_mode == 4) && (lineSet.getLength(lineSet.getTotal()) < r)) { //新規追加分の線分が点状の（長さがrより小さい）とき削除。
                lineSet.deleteLineSegment(lineSet.getTotal());
            } else {
                garbageCollect();//Erasing line segments in the Recycle Bin
            }


        }
        //--------------
        if (input_method == 1) {

        }

        //--------------
        if (input_method == 2) {

        }
    }


    //000000000000000000000000000000000000000000000000000000000000000000000000000000000
    //マウス操作(i_mouse_modeA==0　旧動作　見本のために残している。削除可----------------------------------------------------
    public void mPressed_A_00(Point p) {
        //ゴミ箱がクリックされたら、単独の線分を削除する
        if (trash.inside(p) >= 1) {
            lineSet.singleLineSegment_delete(r);
            nhi = 0;//入力方法が多角形の場合の初期化を行う
        }
        // nyuuryoku_houhouは入力方法の指定。0なら通常の方法、1なら多角形入力、２なら直線を指定した点に引き寄せる
        if (input_method != 2) {
            nhi = 0;
        }
        if (input_method <= 1) {
            if (input_method == 1) {
                int mtsid = lineSet.closest_lineSegment_Search(p);
                lineSet.addLine(lineSet.getB(mtsid), p);

                lineSet.set(mtsid, lineSet.getA(mtsid), p, lineSet.getColor(mtsid), 0);
                lineSet.setColor(lineSet.getTotal(), lineSet.getColor(mtsid));
                if (icol.getNumber() >= 0) {
                    lineSet.set(mtsid, lineSet.getA(mtsid), p, icol, 0);
                    lineSet.setColor(lineSet.getTotal(), icol);
                }
            }

            //マウスと近い位置にある枝を探す。
            iActiveBranch = lineSet.lineSegment_search(p, r, 0);
            //基本枝構造の中の、どの枝とも遠い場合。
            if (iActiveBranch == 0) {
                lineSet.addLine(p, p);
                iActiveBranch = lineSet.getTotal();
                move_mode = 4;
                if (icol.getNumber() >= 0) {
                    lineSet.setColor(iActiveBranch, icol);
                }
            }
            //基本枝構造の中の、どれかの枝に近い場合。
            else {
                if (1 == lineSet.lineSegment_position_search(iActiveBranch, p, r)) {
                    move_mode = 1;
                } //a点に近い場合。
                if (2 == lineSet.lineSegment_position_search(iActiveBranch, p, r)) {
                    move_mode = 2;
                } //b点に近い場合。
                if (3 == lineSet.lineSegment_position_search(iActiveBranch, p, r)) {                 //柄の部分に近い場合。
                    pa.set(1, lineSet.getA(iActiveBranch), -1, p);
                    pb.set(1, lineSet.getB(iActiveBranch), -1, p);
                    move_mode = 3;
                    if (icol.getNumber() >= 0) {
                        lineSet.setColor(iActiveBranch, icol);
                    }
                }
            }
            System.out.print("ieda = ");
            System.out.println(iActiveBranch);
            lineSet.activate(p, r);
        }
        //--------------
        if (input_method == 2) {
            nhi = nhi + 1;
            //System.out.print("nh1 = ");System.out.print("nh1 = ")
            if (nhi == 1) {
                lineSet.addLine(p, p);
                nhPoint = p;
                for (int i = 1; i <= lineSet.getTotal() - 1; i++) {
                    if (oc.equal(lineSet.getA(i), p, 2.0 * r)) {
                        lineSet.set(lineSet.getTotal(), lineSet.getA(i), lineSet.getA(i), LineType.BLACK_0, 0);
                        nhPoint = lineSet.getA(i);
                        break;
                    }
                    if (oc.equal(lineSet.getB(i), p, 2.0 * r)) {
                        lineSet.set(lineSet.getTotal(), lineSet.getB(i), lineSet.getB(i), LineType.BLACK_0, 0);
                        nhPoint = lineSet.getB(i);
                        break;
                    }
                }
            }
            if (nhi != 1) {   // set(int i, Ten p,Ten q,int ic,int ia)
                lineSet.addLine(nhPoint, p);
                for (int i = 1; i <= lineSet.getTotal() - 1; i++) {
                    if (oc.equal(lineSet.getA(i), p, 2.0 * r)) {
                        lineSet.set(lineSet.getTotal(), lineSet.getA(i), lineSet.getB(lineSet.getTotal() - 1), LineType.BLACK_0, 0);
                        nhi = 0;
                        break;
                    }
                    if (oc.equal(lineSet.getB(i), p, 2.0 * r)) {
                        lineSet.set(lineSet.getTotal(), lineSet.getB(i), lineSet.getB(lineSet.getTotal() - 1), LineType.BLACK_0, 0);
                        nhi = 0;
                        break;
                    }
                }
                nhPoint = p;
            }
            if (icol.getNumber() >= 0) {
                lineSet.setColor(lineSet.getTotal(), icol);
            }
        }
    }


    //マウス操作(i_mouse_modeA==0　旧動作　見本のために残している。削除可----------------------------------------------------
    public void mDragged_A_00(Point p) {
        if (input_method <= 1) {
            if (move_mode == 1) {
                lineSet.set(p);
            } //a点を変更
            if (move_mode == 2) {
                lineSet.set(p);
            } //b点を変更
            if (move_mode == 3) {        //枝を平行移動
                lineSet.setA(iActiveBranch, new Point(1, p, 1, pa));
                lineSet.setB(iActiveBranch, new Point(1, p, 1, pb));
            }
            if (move_mode == 4) {
                lineSet.set(p);
            } //a点を変更(ugokasi_mode==1)と同じ動作をする
        }
        //--------------
        if (input_method == 1) {
        }
        //--------------
        if (input_method == 2) {
        }
    }

    //マウス操作(i_mouse_modeA==0　旧動作　見本のために残している。削除可----------------------------------------------------
    public void mReleased_A_00(Point p) {
        if (input_method <= 1) {
            if (move_mode == 1) {
                lineSet.set(p);
            } //a点を変更
            if (move_mode == 2) {
                lineSet.set(p);
            } //b点を変更
            if (move_mode == 3) {//枝を平行移動
                lineSet.setA(iActiveBranch, new Point(1, p, 1, pa));
                lineSet.setB(iActiveBranch, new Point(1, p, 1, pb));
                if (move_mode == 4) {
                    lineSet.set(p);
                }//a点を変更(ugokasi_mode==1)と同じ動作をする
            }
            //eda_atosyori_01();                //枝の長さを変えずに、枝全体を平行移動して微調整する。<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            eda_atosyori_02();                //一端の点だけを移動して反対の端の点は動かさないで微調整する。 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

            //----------格子に乗せる
            if (input_rules >= 1) {
                for (int i = 1; i <= lineSet.getTotal(); i++) {
                    System.out.print("iactive ");
                    System.out.print(i);
                    System.out.print(": ");
                    System.out.println(lineSet.getActive(i));
                    if (lineSet.getActive(i) >= 1) {
                        lineSet.setA(i, kitei_idou(lineSet.getA(i)));
                        lineSet.setB(i, kitei_idou(lineSet.getB(i)));
                    }

                }
            }

            lineSet.deactivate(); //非活性化

            //対称性がある場合の処理
            if (taisyousei > 0) {
                if (((tyuuoutai_xmin < lineSet.getA(iActiveBranch).getX()) && (lineSet.getA(iActiveBranch).getX() < tyuuoutai_xmax))
                        &&
                        ((tyuuoutai_xmin < lineSet.getB(iActiveBranch).getX()) && (lineSet.getB(iActiveBranch).getX() < tyuuoutai_xmax))) {
                    lineSet.setA(iActiveBranch, new Point((tyuuoutai_xmin + tyuuoutai_xmax) / 2, lineSet.getA(iActiveBranch).getY()));
                    lineSet.setB(iActiveBranch, new Point((tyuuoutai_xmin + tyuuoutai_xmax) / 2, lineSet.getB(iActiveBranch).getY()));
                }
            }

            //Garbage disposal. If you do this, the number of the active ieda branch will shift, so for the time being, do it after eda_atosyori.
            if ((move_mode == 4) && (lineSet.getLength(lineSet.getTotal()) < r)) { //新規追加分の線分が点状の（長さがrより小さい）とき削除。
                lineSet.deleteLineSegment(lineSet.getTotal());
            } else {
                garbageCollect();//Erasing line segments in the Recycle Bin
            }
        }
        //--------------
        if (input_method == 1) {
        }

        //--------------
        if (input_method == 2) {
        }
    }

    //1111111111111111111111111111111111111111111111111111111
    //マウス操作(i_mouse_modeA==1線分入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_01(Point p0) {

        i_saigo_no_lineSegment_no_maru_kaku = 0;
        Point p = new Point();
        p.set(camera.TV2object(p0));
        lineSet.addLine(p, p);
        iActiveBranch = lineSet.getTotal();
        lineSet.setColor(iActiveBranch, icol);
    }

    //マウス操作(i_mouse_modeA==1線分入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_01(Point p0) {
        Point p = new Point();
        p.set(camera.TV2object(p0));
        lineSet.setA(iActiveBranch, p);
    }

    //マウス操作(i_mouse_modeA==1線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_01(Point p0) {
        i_saigo_no_lineSegment_no_maru_kaku = 1;
        Point p = new Point();
        p.set(camera.TV2object(p0));
        lineSet.setA(iActiveBranch, p);
        eda_atosyori_02();                //一端の点だけを移動して反対の端の点は動かさないで微調整する。 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        //----------格子に乗せる
        if ((input_rules == 1) || (input_rules == 2)) {
            //for(int i=1;i<=k.getsousuu();i++){
            //System.out.print("iactive 20150312"); System.out.print(i); System.out.print(": ");
            //System.out.println(k.getiactive(i));
            //if(k.getiactive(i)>=1){
            lineSet.setA(lineSet.getTotal(), kitei_idou(lineSet.getA(lineSet.getTotal())));
            lineSet.setB(lineSet.getTotal(), kitei_idou(lineSet.getB(lineSet.getTotal())));
            //}
            //}
        }


        if (input_rules == 3) {

            lineSet.setA(lineSet.getTotal(), sankaku_kitei_idou(lineSet.getA(lineSet.getTotal())));
            lineSet.setB(lineSet.getTotal(), sankaku_kitei_idou(lineSet.getB(lineSet.getTotal())));

        }


        //今入力した線分が、今までに入力済みのどれかの線分と全く同じ位置で一致する場合は、今入力した線分の色だけ反映させて、今入力した線分は追加はしない。
        int i_overlapping;
        i_overlapping = lineSet.overlapping_lineSegment_search(lineSet.getTotal());
        if (i_overlapping > 0) {
            lineSet.setColor(i_overlapping, lineSet.getColor(lineSet.getTotal()));
            lineSet.deleteLineSegment(getTotal());
        }
        lineSet.intersect_divide();//全く重なる線分がない状態で実施される。//2回やらないと反応しない場合がある。原因不明。
        lineSet.intersect_divide();


    }

    //------------------------------------------------------
    public Point kitei_idou(Point t1) {
        Point t_ob = new Point();

        for (int i = i_grid_x_min / grid_width; i <= i_grid_x_max / grid_width; i++) {
            for (int j = i_grid_y_min / grid_width; j <= i_grid_y_max / grid_width; j++) {
                t_ob.set(grid_width * i, grid_width * j);
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
        double sankaku_grid_takasa;
        sankaku_grid_takasa = grid_width * sqr3 / 2.0;
        Point t_ob = new Point();
        for (int i = -30; i <= 30; i++) {
            for (int j = -30; j <= 30; j = j + 2) {
                t_ob.set(grid_width * i, sankaku_grid_takasa * j);
                if (oc.distance(t_ob, t1) < r) {
                    return t_ob;
                }
            }
            for (int j = -31; j <= 31; j = j + 2) {
                t_ob.set(grid_width * i + grid_width / 2.0, sankaku_grid_takasa * j);
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
        minrid = lineSet.closest_lineSegment_Search(p);
        if (lineSet.lineSegment_position_search(minrid, p, r) != 0) {
            lineSet.deleteLineSegment(minrid);
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
        minrid = lineSet.closest_lineSegment_Search(p);
        if (lineSet.lineSegment_position_search(minrid, p, r) != 0) {
            LineType ic_temp;
            ic_temp = lineSet.getColor(minrid);
            ic_temp = LineType.fromNumber(ic_temp.getNumber() + 1);
            if (ic_temp == LineType.CYAN_3) {
                ic_temp = LineType.BLACK_0;
            }
            lineSet.setColor(minrid, ic_temp);
        }


    }//マウス操作(i_mouse_modeA==4線_変換　でボタンを離したとき)を行う関数


}
