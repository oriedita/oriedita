package jp.gr.java_conf.mt777.zukei2d.kousi;

import jp.gr.java_conf.mt777.origami.dougu.camera.*;

import java.awt.*;

import jp.gr.java_conf.mt777.zukei2d.senbun.*;
import jp.gr.java_conf.mt777.zukei2d.oritacalc.*;
import jp.gr.java_conf.mt777.zukei2d.ten.Point;

// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
public class Grid {
    OritaCalc oc = new OritaCalc();          //各種計算用の関数を使うためのクラスのインスタンス化

    double d_grid_haba = 200.0;//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<格子幅(double)

    double d_grid_a_nagasa = 1.0;
    double d_grid_b_nagasa = 1.0;
    double d_grid_kakudo = -90.0;

    double d_grid_ax = 1.0;//格子の横方向の単位ベクトルのX成分の比率
    double d_grid_ay = 0.0;//格子の横方向の単位ベクトルのY成分の比率

    double d_grid_bx = 0.0;//格子の縦方向の単位ベクトルのX成分の比率
    double d_grid_by = 1.0;//格子の縦方向の単位ベクトルのY成分の比率

    double okx0 = -200.0;//obiject系での格子のx座標の原点
    double oky0 = +200.0;//obiject系での格子のy座標の原点

    double taikakusen_max = 1.0;//単位胞の対角線の長いほう
    double taikakusen_min = 1.0;//単位胞の対角線の短いほう


    int i_kitei_jyoutai = 1;//基底（格子）の状況=0は全域で無効だが、格子幅だけは既存端点への引き寄せ半径の設定に使うので有効、状況=1は用紙内のみ有効、状況=2は全領域で有効


    //用紙の分割なしならkousi_zahyou[0から1]なのでkousi_bunkatu_suuは１、kousi_bunkatu_suu
    //用紙の2分割ならkousi_zahyou[0,1,2]なのでkousi_bunkatu_suuは2、
    //用紙の4分割ならkousi_zahyou[0,1,2,3,4]なのでkousi_bunkatu_suuは4、
    int grid_bunkatu_suu = 2;
    //int kousi_zahyou[] = new int[1024000];
    //double d_kousi_zahyou[] = new double[1024000];

    //int i_kousi_x_min =-1000;int i_kousi_x_max =2000;int i_kousi_y_min =-1000;int i_kousi_y_max =1000;


    int a_to_heikouna_memori_kannkaku = 5;
    int a_to_heikouna_memori_iti = 0;


    int b_to_heikouna_memori_kannkaku = 5;
    int b_to_heikouna_memori_iti = 0;

    Color grid_color = new Color(230, 230, 230);//格子線の色
    Color grid_scale_color = new Color(180, 200, 180);//格子目盛り線の色

    int gridLineWidth = 1;//Grid line width

    public Grid() {  //コンストラクタ

    }

    // ------------------------------------------------------
    public void set_a_to_heikouna_memori_kannkaku(int i) {
        a_to_heikouna_memori_kannkaku = i;
        if (a_to_heikouna_memori_iti >= a_to_heikouna_memori_kannkaku) {
            a_to_heikouna_memori_iti = 0;
        }
    }

    public void set_b_to_heikouna_memori_kannkaku(int i) {
        b_to_heikouna_memori_kannkaku = i;
        if (b_to_heikouna_memori_iti >= b_to_heikouna_memori_kannkaku) {
            b_to_heikouna_memori_iti = 0;
        }
    }

    public void a_to_heikouna_memori_iti_idou() {
        a_to_heikouna_memori_iti = a_to_heikouna_memori_iti + 1;
        if (a_to_heikouna_memori_iti >= a_to_heikouna_memori_kannkaku) {
            a_to_heikouna_memori_iti = 0;
        }
    }

    public void b_to_heikouna_memori_iti_idou() {
        b_to_heikouna_memori_iti = b_to_heikouna_memori_iti + 1;
        if (b_to_heikouna_memori_iti >= b_to_heikouna_memori_kannkaku) {
            b_to_heikouna_memori_iti = 0;
        }
    }


    public void set_a_to_heikouna_memori_iti(int i0) {
        a_to_heikouna_memori_iti = i0;
    }

    public void set_b_to_heikouna_memori_iti(int i0) {
        b_to_heikouna_memori_iti = i0;
    }

    public void setGridLineWidth(int i0) {
        gridLineWidth = i0;
    }


    public int get_a_to_heikouna_memori_iti() {
        return a_to_heikouna_memori_iti;
    }

    public int get_b_to_heikouna_memori_iti() {
        return b_to_heikouna_memori_iti;
    }

    public int getGridLIneWidth() {
        return gridLineWidth;
    }


    // ------------------------------------------------------
    public void set_grid_bunkatu_suu(int i) {
        grid_bunkatu_suu = i;
        d_grid_haba = 400.0 / (double) grid_bunkatu_suu;

        grid_keisan();

    }

    // ------------------------------------------------------
    public double d_haba() {
        return d_grid_haba;
    }

    public int bunsuu() {
        return grid_bunkatu_suu;
    }

    // ----------------------------------------
    public void set_d_grid(double dkxn, double dkyn, double dkk) {
        d_grid_a_nagasa = dkxn;
        d_grid_b_nagasa = dkyn;
        d_grid_kakudo = -dkk;

        grid_keisan();
    }

    // ----------------------------------------
    public void grid_keisan() {
        d_grid_ax = d_grid_haba * d_grid_a_nagasa;
        d_grid_ay = d_grid_haba * 0.0;

        double d_rad = (Math.PI / 180) * d_grid_kakudo;
        d_grid_bx = d_grid_haba * d_grid_b_nagasa * Math.cos(d_rad);
        d_grid_by = d_grid_haba * d_grid_b_nagasa * Math.sin(d_rad);

        taikakusen_max = oc.distance(new Point(0.0, 0.0), new Point(d_grid_ax + d_grid_bx, d_grid_ay + d_grid_by));
        taikakusen_min = oc.distance(new Point(d_grid_ax, d_grid_ay), new Point(d_grid_bx, d_grid_by));
        if (taikakusen_max < taikakusen_min) {
            taikakusen_min = oc.distance(new Point(0.0, 0.0), new Point(d_grid_ax + d_grid_bx, d_grid_ay + d_grid_by));
            taikakusen_max = oc.distance(new Point(d_grid_ax, d_grid_ay), new Point(d_grid_bx, d_grid_by));
        }


        if (jyoutai() == 1) {
            if (Math.abs(d_grid_a_nagasa - 1.0) > 0.000001) {
                set_i_kitei_jyoutai(2);
            }
            if (Math.abs(d_grid_b_nagasa - 1.0) > 0.000001) {
                set_i_kitei_jyoutai(2);
            }
            if (Math.abs(d_grid_kakudo - (-90.0)) > 0.000001) {
                set_i_kitei_jyoutai(2);
            }
        }


    }


    public void grid_senhaba_sage() {
        gridLineWidth = gridLineWidth - 2;
        if (gridLineWidth < 1) {
            gridLineWidth = 1;
        }
    }

    public void grid_senhaba_age() {
        gridLineWidth = gridLineWidth + 2;
    }
// ----------------------------------------

    public void set_i_kitei_jyoutai(int i) {
        i_kitei_jyoutai = i;
        if (i_kitei_jyoutai > 2) {
            i_kitei_jyoutai = 0;
        }
        if (i_kitei_jyoutai < 0) {
            i_kitei_jyoutai = 2;
        }

        if (jyoutai() == 1) {
            if (Math.abs(d_grid_a_nagasa - 1.0) > 0.000001) {
                set_i_kitei_jyoutai(2);
            }
            if (Math.abs(d_grid_b_nagasa - 1.0) > 0.000001) {
                set_i_kitei_jyoutai(2);
            }
            if (Math.abs(d_grid_kakudo - (-90.0)) > 0.000001) {
                set_i_kitei_jyoutai(2);
            }
        }
    }

    //------
    //public int  get_i_kitei_jyoutai() {return i_kitei_jyoutai	;}
    public int jyoutai() {
        return i_kitei_jyoutai;
    }
//------

    public Point getIndex(Point t0) {//obj系座標のTenから、格子の指数を得る
        //行列 [d_kousi_ax, d_kousi_bx]によって[1]は格子ベクトルaに変換され、[1]は格子ベクトルbに変換される。
        //     [d_kousi_ay, d_kousi_by]        [0]　　　　　　　 　　　　　　[0]
        //この逆行列によってobj系座標のTenは格子の指数に変換される。
        //　　　　　
        //行列の記号の定義
        double ax = d_grid_ax;
        double ay = d_grid_ay;
        double bx = d_grid_bx;
        double by = d_grid_by;

        //逆行列の記号の定義
        double det = ax * by - bx * ay;
        double gax = by / det;
        double gay = -ay / det;
        double gbx = -bx / det;
        double gby = ax / det;


        double kx = t0.getX() - okx0;
        double ky = t0.getY() - oky0;

        double index_x = gax * kx + gbx * ky;
        double index_y = gay * kx + gby * ky;

        return new Point(index_x, index_y);

    }

    // ----------------------------
    public int get_a_index_min(Point p_a, Point p_b, Point p_c, Point p_d) {//obj座標系の4つの点を指定し、各点のaベクトルの指数より小さい整数の指数を得る。
        Point p_a_index = new Point();
        p_a_index.set(getIndex(p_a));//p_aの格子系の指数
        Point p_b_index = new Point();
        p_b_index.set(getIndex(p_b));//p_bの格子系の指数
        Point p_c_index = new Point();
        p_c_index.set(getIndex(p_c));//p_cの格子系の指数
        Point p_d_index = new Point();
        p_d_index.set(getIndex(p_d));//p_dの格子系の指数

        double a_index_max = p_a_index.getX();
        if (p_b_index.getX() > a_index_max) {
            a_index_max = p_b_index.getX();
        }
        if (p_c_index.getX() > a_index_max) {
            a_index_max = p_c_index.getX();
        }
        if (p_d_index.getX() > a_index_max) {
            a_index_max = p_d_index.getX();
        }
        int kousi_a_max = (int) Math.ceil(a_index_max);
        double a_index_min = p_a_index.getX();
        if (p_b_index.getX() < a_index_min) {
            a_index_min = p_b_index.getX();
        }
        if (p_c_index.getX() < a_index_min) {
            a_index_min = p_c_index.getX();
        }
        if (p_d_index.getX() < a_index_min) {
            a_index_min = p_d_index.getX();
        }
        int kousi_a_min = (int) Math.floor(a_index_min);
        double b_index_max = p_a_index.getY();
        if (p_b_index.getY() > b_index_max) {
            b_index_max = p_b_index.getY();
        }
        if (p_c_index.getY() > b_index_max) {
            b_index_max = p_c_index.getY();
        }
        if (p_d_index.getY() > b_index_max) {
            b_index_max = p_d_index.getY();
        }
        int kousi_b_max = (int) Math.ceil(b_index_max);
        double b_index_min = p_a_index.getY();
        if (p_b_index.getY() < b_index_min) {
            b_index_min = p_b_index.getY();
        }
        if (p_c_index.getY() < b_index_min) {
            b_index_min = p_c_index.getY();
        }
        if (p_d_index.getY() < b_index_min) {
            b_index_min = p_d_index.getY();
        }
        int kousi_b_min = (int) Math.floor(b_index_min);

        //AAAAAAAAAAAAAAAAAAA
        return kousi_a_min;
    }

    // ----------------------------
    public int get_a_index_max(Point p_a, Point p_b, Point p_c, Point p_d) {//obj座標系の4つの点を指定し、各点のaベクトルの指数より大きい整数の指数を得る。
        Point p_a_index = new Point();
        p_a_index.set(getIndex(p_a));//p_aの格子系の指数
        Point p_b_index = new Point();
        p_b_index.set(getIndex(p_b));//p_bの格子系の指数
        Point p_c_index = new Point();
        p_c_index.set(getIndex(p_c));//p_cの格子系の指数
        Point p_d_index = new Point();
        p_d_index.set(getIndex(p_d));//p_dの格子系の指数

        double a_index_max = p_a_index.getX();
        if (p_b_index.getX() > a_index_max) {
            a_index_max = p_b_index.getX();
        }
        if (p_c_index.getX() > a_index_max) {
            a_index_max = p_c_index.getX();
        }
        if (p_d_index.getX() > a_index_max) {
            a_index_max = p_d_index.getX();
        }
        int kousi_a_max = (int) Math.ceil(a_index_max);
        double a_index_min = p_a_index.getX();
        if (p_b_index.getX() < a_index_min) {
            a_index_min = p_b_index.getX();
        }
        if (p_c_index.getX() < a_index_min) {
            a_index_min = p_c_index.getX();
        }
        if (p_d_index.getX() < a_index_min) {
            a_index_min = p_d_index.getX();
        }
        int kousi_a_min = (int) Math.floor(a_index_min);
        double b_index_max = p_a_index.getY();
        if (p_b_index.getY() > b_index_max) {
            b_index_max = p_b_index.getY();
        }
        if (p_c_index.getY() > b_index_max) {
            b_index_max = p_c_index.getY();
        }
        if (p_d_index.getY() > b_index_max) {
            b_index_max = p_d_index.getY();
        }
        int kousi_b_max = (int) Math.ceil(b_index_max);
        double b_index_min = p_a_index.getY();
        if (p_b_index.getY() < b_index_min) {
            b_index_min = p_b_index.getY();
        }
        if (p_c_index.getY() < b_index_min) {
            b_index_min = p_c_index.getY();
        }
        if (p_d_index.getY() < b_index_min) {
            b_index_min = p_d_index.getY();
        }
        int kousi_b_min = (int) Math.floor(b_index_min);

        //AAAAAAAAAAAAAAAAAAA
        return kousi_a_max;
    }

    // ----------------------------
    public int get_b_index_min(Point p_a, Point p_b, Point p_c, Point p_d) {//obj座標系の4つの点を指定し、各点のbベクトルの指数より小さい整数の指数を得る。
        Point p_a_index = new Point();
        p_a_index.set(getIndex(p_a));//p_aの格子系の指数
        Point p_b_index = new Point();
        p_b_index.set(getIndex(p_b));//p_bの格子系の指数
        Point p_c_index = new Point();
        p_c_index.set(getIndex(p_c));//p_cの格子系の指数
        Point p_d_index = new Point();
        p_d_index.set(getIndex(p_d));//p_dの格子系の指数

        double a_index_max = p_a_index.getX();
        if (p_b_index.getX() > a_index_max) {
            a_index_max = p_b_index.getX();
        }
        if (p_c_index.getX() > a_index_max) {
            a_index_max = p_c_index.getX();
        }
        if (p_d_index.getX() > a_index_max) {
            a_index_max = p_d_index.getX();
        }
        int kousi_a_max = (int) Math.ceil(a_index_max);
        double a_index_min = p_a_index.getX();
        if (p_b_index.getX() < a_index_min) {
            a_index_min = p_b_index.getX();
        }
        if (p_c_index.getX() < a_index_min) {
            a_index_min = p_c_index.getX();
        }
        if (p_d_index.getX() < a_index_min) {
            a_index_min = p_d_index.getX();
        }
        int kousi_a_min = (int) Math.floor(a_index_min);
        double b_index_max = p_a_index.getY();
        if (p_b_index.getY() > b_index_max) {
            b_index_max = p_b_index.getY();
        }
        if (p_c_index.getY() > b_index_max) {
            b_index_max = p_c_index.getY();
        }
        if (p_d_index.getY() > b_index_max) {
            b_index_max = p_d_index.getY();
        }
        int kousi_b_max = (int) Math.ceil(b_index_max);
        double b_index_min = p_a_index.getY();
        if (p_b_index.getY() < b_index_min) {
            b_index_min = p_b_index.getY();
        }
        if (p_c_index.getY() < b_index_min) {
            b_index_min = p_c_index.getY();
        }
        if (p_d_index.getY() < b_index_min) {
            b_index_min = p_d_index.getY();
        }
        int kousi_b_min = (int) Math.floor(b_index_min);

        //AAAAAAAAAAAAAAAAAAA
        return kousi_b_min;
    }

    // ----------------------------
    public int get_b_index_max(Point p_a, Point p_b, Point p_c, Point p_d) {//obj座標系の4つの点を指定し、各点のbベクトルの指数より大きい整数の指数を得る。
        Point p_a_index = new Point();
        p_a_index.set(getIndex(p_a));//p_aの格子系の指数
        Point p_b_index = new Point();
        p_b_index.set(getIndex(p_b));//p_bの格子系の指数
        Point p_c_index = new Point();
        p_c_index.set(getIndex(p_c));//p_cの格子系の指数
        Point p_d_index = new Point();
        p_d_index.set(getIndex(p_d));//p_dの格子系の指数

        double a_index_max = p_a_index.getX();
        if (p_b_index.getX() > a_index_max) {
            a_index_max = p_b_index.getX();
        }
        if (p_c_index.getX() > a_index_max) {
            a_index_max = p_c_index.getX();
        }
        if (p_d_index.getX() > a_index_max) {
            a_index_max = p_d_index.getX();
        }
        int kousi_a_max = (int) Math.ceil(a_index_max);
        double a_index_min = p_a_index.getX();
        if (p_b_index.getX() < a_index_min) {
            a_index_min = p_b_index.getX();
        }
        if (p_c_index.getX() < a_index_min) {
            a_index_min = p_c_index.getX();
        }
        if (p_d_index.getX() < a_index_min) {
            a_index_min = p_d_index.getX();
        }
        int kousi_a_min = (int) Math.floor(a_index_min);
        double b_index_max = p_a_index.getY();
        if (p_b_index.getY() > b_index_max) {
            b_index_max = p_b_index.getY();
        }
        if (p_c_index.getY() > b_index_max) {
            b_index_max = p_c_index.getY();
        }
        if (p_d_index.getY() > b_index_max) {
            b_index_max = p_d_index.getY();
        }
        int kousi_b_max = (int) Math.ceil(b_index_max);
        double b_index_min = p_a_index.getY();
        if (p_b_index.getY() < b_index_min) {
            b_index_min = p_b_index.getY();
        }
        if (p_c_index.getY() < b_index_min) {
            b_index_min = p_c_index.getY();
        }
        if (p_d_index.getY() < b_index_min) {
            b_index_min = p_d_index.getY();
        }
        int kousi_b_min = (int) Math.floor(b_index_min);

        //AAAAAAAAAAAAAAAAAAA
        return kousi_b_max;
    }


    public void setGridColor(Color color0) {
        grid_color = color0;
    }

    public Color getGridColor() {
        return grid_color;
    }

    public void set_kousi_memori_color(Color color0) {
        grid_scale_color = color0;
    }

    public Color get_kousi_memori_color() {
        return grid_scale_color;
    }


    //描画-----------------------------------------------------------------
    public void draw(Graphics g, Camera camera, int p0x_max, int p0y_max, int i_irokae) {    //i_irokae=1なら一定数ごとに格子線の色を変える
        //入力規定が1か2（正方格子）の場合の格子線の描画
        Graphics2D g2 = (Graphics2D) g;

        LineSegment s_tv = new LineSegment();
        //Ten a =new Ten(); Ten b =new Ten();

        LineSegment s_ob = new LineSegment();


        //格子線の描画
        //g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状
        g2.setStroke(new BasicStroke((float) gridLineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状


        if (jyoutai() != 0) {
            Point p0_a = new Point();
            p0_a.set(0, 0);//画面の左上カドのTV系座標
            Point p0_b = new Point();
            p0_b.set(0, p0y_max);//画面の左下カドのTV系座標
            Point p0_c = new Point();
            p0_c.set(p0x_max, p0y_max);//画面の右下カドのTV系座標
            Point p0_d = new Point();
            p0_d.set(p0x_max, 0);//画面の右上カドのTV系座標

            Point p_a = new Point();
            p_a.set(camera.TV2object(p0_a));//画面の左上カドのobj系座標
            Point p_b = new Point();
            p_b.set(camera.TV2object(p0_b));//画面の左下カドのobj系座標
            Point p_c = new Point();
            p_c.set(camera.TV2object(p0_c));//画面の右下カドのobj系座標
            Point p_d = new Point();
            p_d.set(camera.TV2object(p0_d));//画面の右上カドのobj系座標


            int kousi_gamen_a_max = get_a_index_max(p_a, p_b, p_c, p_d);
            int kousi_gamen_a_min = get_a_index_min(p_a, p_b, p_c, p_d);
            int kousi_gamen_b_max = get_b_index_max(p_a, p_b, p_c, p_d);
            int kousi_gamen_b_min = get_b_index_min(p_a, p_b, p_c, p_d);

            //-------------------------------------
            if (jyoutai() == 1) {

                int kousi_yousi_x_max = bunsuu();
                int kousi_yousi_x_min = 0;
                int kousi_yousi_y_max = bunsuu();
                int kousi_yousi_y_min = 0;

                if (kousi_gamen_a_max > kousi_yousi_x_max) {
                    kousi_gamen_a_max = kousi_yousi_x_max;
                }
                if (kousi_gamen_a_min < kousi_yousi_x_min) {
                    kousi_gamen_a_min = kousi_yousi_x_min;
                }
                if (kousi_gamen_b_max > kousi_yousi_y_max) {
                    kousi_gamen_b_max = kousi_yousi_y_max;
                }
                if (kousi_gamen_b_min < kousi_yousi_y_min) {
                    kousi_gamen_b_min = kousi_yousi_y_min;
                }

            }

            if (jyoutai() <= 2) {
                //g.setColor(new Color(230, 230, 230));
                g.setColor(grid_color);
                for (int i = kousi_gamen_a_min; i <= kousi_gamen_a_max; i++) {
                    //double k_zah = d_haba()*i;

                    s_ob.set(d_grid_ax * i + d_grid_bx * kousi_gamen_b_min + okx0,
                            d_grid_ay * i + d_grid_by * kousi_gamen_b_min + oky0,
                            d_grid_ax * i + d_grid_bx * kousi_gamen_b_max + okx0,
                            d_grid_ay * i + d_grid_by * kousi_gamen_b_max + oky0);
                    s_tv.set(camera.object2TV(s_ob));
                    g.drawLine((int) s_tv.getAX(), (int) s_tv.getAY(), (int) s_tv.getBX(), (int) s_tv.getBY()); //直線
                }

                for (int i = kousi_gamen_b_min; i <= kousi_gamen_b_max; i++) {
                    //double k_zah = d_haba()*i;

                    s_ob.set(d_grid_ax * kousi_gamen_a_min + d_grid_bx * i + okx0,
                            d_grid_ay * kousi_gamen_a_min + d_grid_by * i + oky0,
                            d_grid_ax * kousi_gamen_a_max + d_grid_bx * i + okx0,
                            d_grid_ay * kousi_gamen_a_max + d_grid_by * i + oky0);


                    s_tv.set(camera.object2TV(s_ob));
                    g.drawLine((int) s_tv.getAX(), (int) s_tv.getAY(), (int) s_tv.getBX(), (int) s_tv.getBY()); //直線
                }

                //一定数ごとに格子線の色を変える-----------------------------------------------
                if (i_irokae == 1) {

                    //g.setColor(new Color(180, 200,180));
                    g.setColor(grid_scale_color);

//System.out.println("20170526  ********************");
//System.out.println("b_to_heikouna_memori_kannkaku = "+b_to_heikouna_memori_kannkaku);

                    int i_jyouyo;//剰余

                    for (int i = kousi_gamen_a_min; i <= kousi_gamen_a_max; i++) {
//System.out.println("	i = "+i);
//System.out.println("	i % b_to_heikouna_memori_kannkaku = "+i % b_to_heikouna_memori_kannkaku);
                        i_jyouyo = i % b_to_heikouna_memori_kannkaku;
                        if (i_jyouyo < 0) {
                            i_jyouyo = i_jyouyo + b_to_heikouna_memori_kannkaku;
                        }
                        if (i_jyouyo == b_to_heikouna_memori_iti) {


                            s_ob.set(d_grid_ax * i + d_grid_bx * kousi_gamen_b_min + okx0,
                                    d_grid_ay * i + d_grid_by * kousi_gamen_b_min + oky0,
                                    d_grid_ax * i + d_grid_bx * kousi_gamen_b_max + okx0,
                                    d_grid_ay * i + d_grid_by * kousi_gamen_b_max + oky0);
                            s_tv.set(camera.object2TV(s_ob));
                            g.drawLine((int) s_tv.getAX(), (int) s_tv.getAY(), (int) s_tv.getBX(), (int) s_tv.getBY()); //直線
                        }
                    }

                    for (int i = kousi_gamen_b_min; i <= kousi_gamen_b_max; i++) {
                        i_jyouyo = i % a_to_heikouna_memori_kannkaku;
                        if (i_jyouyo < 0) {
                            i_jyouyo = i_jyouyo + a_to_heikouna_memori_kannkaku;
                        }

                        if (i_jyouyo == a_to_heikouna_memori_iti) {

                            s_ob.set(d_grid_ax * kousi_gamen_a_min + d_grid_bx * i + okx0,
                                    d_grid_ay * kousi_gamen_a_min + d_grid_by * i + oky0,
                                    d_grid_ax * kousi_gamen_a_max + d_grid_bx * i + okx0,
                                    d_grid_ay * kousi_gamen_a_max + d_grid_by * i + oky0);


                            s_tv.set(camera.object2TV(s_ob));
                            g.drawLine((int) s_tv.getAX(), (int) s_tv.getAY(), (int) s_tv.getBX(), (int) s_tv.getBY()); //直線
                        }
                    }

                    //一定数ごとに格子線の色を変える　ここまで--------------------------------------------

                }


            }









/*
				if(jyoutai()==1){
		double p_x_max=p_a.getx();
if(p_b.getx()>p_x_max){p_x_max=p_b.getx();}
if(p_c.getx()>p_x_max){p_x_max=p_c.getx();}
if(p_d.getx()>p_x_max){p_x_max=p_d.getx();}if(p_x_max<200.0+1.0){p_x_max=200.0+1.0;}

		double p_x_min=p_a.getx();
if(p_b.getx()<p_x_min){p_x_min=p_b.getx();}
if(p_c.getx()<p_x_min){p_x_min=p_c.getx();}
if(p_d.getx()<p_x_min){p_x_min=p_d.getx();}if(p_x_min>-200.0-1.0){p_x_min=-200.0-1.0;}

		double p_y_max=p_a.gety();
if(p_b.gety()>p_y_max){p_y_max=p_b.gety();}
if(p_c.gety()>p_y_max){p_y_max=p_c.gety();}
if(p_d.gety()>p_y_max){p_y_max=p_d.gety();}if(p_y_max<200.0+1.0){p_y_max=200.0+1.0;}

		double p_y_min=p_a.gety();
if(p_b.gety()<p_y_min){p_y_min=p_b.gety();}
if(p_c.gety()<p_y_min){p_y_min=p_c.gety();}
if(p_d.gety()<p_y_min){p_y_min=p_d.gety();}if(p_y_min>-200.0-1.0){p_y_min=-200.0-1.0;}



		int x[]= new int[4];
		int y[] = new int[4];
		Ten t1=new Ten();Ten t2=new Ten();Ten t3=new Ten();Ten t4=new Ten();
		Ten tt1=new Ten();Ten tt2=new Ten();Ten tt3=new Ten();Ten tt4=new Ten();

		//四角形1   (p_x_min,p_y_min),(p_x_min,p_y_max),(-200.0,p_y_max),(-200.0,p_y_min)
		t1.set(p_x_min,p_y_min);t2.set(p_x_min,p_y_max);t3.set(-200.0,p_y_max);t4.set(-200.0,p_y_min);
		tt1.set(camera.object2TV(t1));tt2.set(camera.object2TV(t2));tt3.set(camera.object2TV(t3));tt4.set(camera.object2TV(t4));

		x[0]=(int)tt1.getx(); y[0]=(int)tt1.gety();
		x[1]=(int)tt2.getx(); y[1]=(int)tt2.gety();
		x[2]=(int)tt3.getx(); y[2]=(int)tt3.gety();
		x[3]=(int)tt4.getx(); y[3]=(int)tt4.gety();

		g.setColor(Color.white);
		g.fillPolygon(x,y,4); 


		//四角形2   (p_x_min,200.0),(p_x_min,p_y_max),(p_x_max,p_y_max),(p_x_max,200.0)
		t1.set(p_x_min,200.0);t2.set(p_x_min,p_y_max);t3.set(p_x_max,p_y_max);t4.set(p_x_max,200.0);
		tt1.set(camera.object2TV(t1));tt2.set(camera.object2TV(t2));tt3.set(camera.object2TV(t3));tt4.set(camera.object2TV(t4));

		x[0]=(int)tt1.getx(); y[0]=(int)tt1.gety();
		x[1]=(int)tt2.getx(); y[1]=(int)tt2.gety();
		x[2]=(int)tt3.getx(); y[2]=(int)tt3.gety();
		x[3]=(int)tt4.getx(); y[3]=(int)tt4.gety();

		g.setColor(Color.white);
		g.fillPolygon(x,y,4); 



		//四角形3   (200.0,p_y_min),(200.0,p_y_max),(p_x_max,p_y_max),(p_x_max,p_y_min)
		t1.set(200.0,p_y_min);t2.set(200.0,p_y_max);t3.set(p_x_max,p_y_max);t4.set(p_x_max,p_y_min);
		tt1.set(camera.object2TV(t1));tt2.set(camera.object2TV(t2));tt3.set(camera.object2TV(t3));tt4.set(camera.object2TV(t4));

		x[0]=(int)tt1.getx(); y[0]=(int)tt1.gety();
		x[1]=(int)tt2.getx(); y[1]=(int)tt2.gety();
		x[2]=(int)tt3.getx(); y[2]=(int)tt3.gety();
		x[3]=(int)tt4.getx(); y[3]=(int)tt4.gety();

		g.setColor(Color.white);
		g.fillPolygon(x,y,4); 


		//四角形4   (p_x_min,p_y_min),(p_x_min,-200.0),(p_x_max,-200.0),(p_x_max,p_y_min)
		t1.set(p_x_min,p_y_min);t2.set(p_x_min,-200.0);t3.set(p_x_max,-200.0);t4.set(p_x_max,p_y_min);
		tt1.set(camera.object2TV(t1));tt2.set(camera.object2TV(t2));tt3.set(camera.object2TV(t3));tt4.set(camera.object2TV(t4));

		x[0]=(int)tt1.getx(); y[0]=(int)tt1.gety();
		x[1]=(int)tt2.getx(); y[1]=(int)tt2.gety();
		x[2]=(int)tt3.getx(); y[2]=(int)tt3.gety();
		x[3]=(int)tt4.getx(); y[3]=(int)tt4.gety();

		g.setColor(Color.white);
		g.fillPolygon(x,y,4); 

				}
*/

            //}


        }


    }


    // --------------------------

    public Point moyori_kousi_ten(Point t0) {

        Point t2 = new Point(); //格子点
        double kousi_x;
        double kousi_y;


        kousi_x = Math.round((t0.getX() - okx0) / d_haba()) * d_haba() + okx0;
        kousi_y = Math.round((t0.getY() - oky0) / d_haba()) * d_haba() + oky0;


        if (bunsuu() > 0) {

            //用紙枠の中の格子点との近さを検討
            if (jyoutai() == 1) {

                Point t_1 = new Point(t0.getX() - taikakusen_max, t0.getY() - taikakusen_max);
                Point t_2 = new Point(t0.getX() - taikakusen_max, t0.getY() + taikakusen_max);
                Point t_3 = new Point(t0.getX() + taikakusen_max, t0.getY() + taikakusen_max);
                Point t_4 = new Point(t0.getX() + taikakusen_max, t0.getY() - taikakusen_max);

                int kousi_a_max = get_a_index_max(t_1, t_2, t_3, t_4);
                int kousi_a_min = get_a_index_min(t_1, t_2, t_3, t_4);
                int kousi_b_max = get_b_index_max(t_1, t_2, t_3, t_4);
                int kousi_b_min = get_b_index_min(t_1, t_2, t_3, t_4);


                double kyori_min = taikakusen_max;
                for (int i = kousi_a_min; i <= kousi_a_max; i++) {
                    for (int j = kousi_b_min; j <= kousi_b_max; j++) {

                        Point t_tmp = new Point(okx0 + d_grid_ax * i + d_grid_bx * j, oky0 + d_grid_ay * i + d_grid_by * j);
                        if (((-200.000001 <= t_tmp.getX()) && (t_tmp.getX() <= 200.000001)) && ((-200.000001 <= t_tmp.getY()) && (t_tmp.getY() <= 200.000001))) {

                            if (t0.distance(t_tmp) <= kyori_min) {
                                kyori_min = t0.distance(t_tmp);
                                t2.set(t_tmp);
                            }

                        }
                    }
                }
            }

            //用紙枠の内外に関係なく格子点との近さを検討
            if (jyoutai() == 2) {

                Point t_1 = new Point(t0.getX() - taikakusen_max, t0.getY() - taikakusen_max);
                Point t_2 = new Point(t0.getX() - taikakusen_max, t0.getY() + taikakusen_max);
                Point t_3 = new Point(t0.getX() + taikakusen_max, t0.getY() + taikakusen_max);
                Point t_4 = new Point(t0.getX() + taikakusen_max, t0.getY() - taikakusen_max);

                int kousi_a_max = get_a_index_max(t_1, t_2, t_3, t_4);
                int kousi_a_min = get_a_index_min(t_1, t_2, t_3, t_4);
                int kousi_b_max = get_b_index_max(t_1, t_2, t_3, t_4);
                int kousi_b_min = get_b_index_min(t_1, t_2, t_3, t_4);


                double kyori_min = taikakusen_max;
                for (int i = kousi_a_min; i <= kousi_a_max; i++) {
                    for (int j = kousi_b_min; j <= kousi_b_max; j++) {
                        Point t_tmp = new Point(okx0 + d_grid_ax * i + d_grid_bx * j, oky0 + d_grid_ay * i + d_grid_by * j);
                        if (t0.distance(t_tmp) <= kyori_min) {
                            kyori_min = t0.distance(t_tmp);
                            t2.set(t_tmp);
                        }
                    }
                }
            }


        }
        return t2;
    }


}
