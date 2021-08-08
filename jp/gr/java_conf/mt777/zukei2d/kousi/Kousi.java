package jp.gr.java_conf.mt777.zukei2d.kousi;

import jp.gr.java_conf.mt777.origami.dougu.camera.*;

import java.awt.*;

import jp.gr.java_conf.mt777.zukei2d.ten.*;
import jp.gr.java_conf.mt777.zukei2d.senbun.*;
import jp.gr.java_conf.mt777.zukei2d.oritacalc.*;

// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
public class Kousi {
    //String c=new String();
    //int kakusuu;             //何角形か
    //ArrayList TenList = new ArrayList();

    //Ten t[];//頂点

    OritaCalc oc = new OritaCalc();          //各種計算用の関数を使うためのクラスのインスタンス化

    double d_kousi_haba = 200.0;//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<格子幅(double)

    double d_kousi_a_nagasa = 1.0;
    double d_kousi_b_nagasa = 1.0;
    double d_kousi_kakudo = -90.0;

    double d_kousi_ax = 1.0;//格子の横方向の単位ベクトルのX成分の比率
    double d_kousi_ay = 0.0;//格子の横方向の単位ベクトルのY成分の比率

    double d_kousi_bx = 0.0;//格子の縦方向の単位ベクトルのX成分の比率
    double d_kousi_by = 1.0;//格子の縦方向の単位ベクトルのY成分の比率

    double okx0 = -200.0;//obiject系での格子のx座標の原点
    double oky0 = +200.0;//obiject系での格子のy座標の原点

    double taikakusen_max = 1.0;//単位胞の対角線の長いほう
    double taikakusen_min = 1.0;//単位胞の対角線の短いほう


    int i_kitei_jyoutai = 1;//基底（格子）の状況=0は全域で無効だが、格子幅だけは既存端点への引き寄せ半径の設定に使うので有効、状況=1は用紙内のみ有効、状況=2は全領域で有効


    //用紙の分割なしならkousi_zahyou[0から1]なのでkousi_bunkatu_suuは１、kousi_bunkatu_suu
    //用紙の2分割ならkousi_zahyou[0,1,2]なのでkousi_bunkatu_suuは2、
    //用紙の4分割ならkousi_zahyou[0,1,2,3,4]なのでkousi_bunkatu_suuは4、
    int kousi_bunkatu_suu = 2;
    //int kousi_zahyou[] = new int[1024000];
    //double d_kousi_zahyou[] = new double[1024000];

    //int i_kousi_x_min =-1000;int i_kousi_x_max =2000;int i_kousi_y_min =-1000;int i_kousi_y_max =1000;


    int a_to_heikouna_memori_kannkaku = 5;
    int a_to_heikouna_memori_iti = 0;


    int b_to_heikouna_memori_kannkaku = 5;
    int b_to_heikouna_memori_iti = 0;

    Color kousi_color = new Color(230, 230, 230);//格子線の色
    Color kousi_memori_color = new Color(180, 200, 180);//格子目盛り線の色

    int kousi_senhaba = 1;//格子の線幅

    public Kousi() {  //コンストラクタ


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

    public void set_kousi_senhaba(int i0) {
        kousi_senhaba = i0;
    }


    public int get_a_to_heikouna_memori_iti() {
        return a_to_heikouna_memori_iti;
    }

    public int get_b_to_heikouna_memori_iti() {
        return b_to_heikouna_memori_iti;
    }

    public int get_kousi_senhaba() {
        return kousi_senhaba;
    }


    // ------------------------------------------------------
    public void set_kousi_bunkatu_suu(int i) {
        kousi_bunkatu_suu = i;
        d_kousi_haba = 400.0 / (double) kousi_bunkatu_suu;

        kousi_keisan();

    }

    // ------------------------------------------------------
    public double d_haba() {
        return d_kousi_haba;
    }

    //public double get_d_kousi_haba(){return d_kousi_haba;}
    public int bunsuu() {
        return kousi_bunkatu_suu;
    }

    // ----------------------------------------
    public void set_d_kousi(double dkxn, double dkyn, double dkk) {
        d_kousi_a_nagasa = dkxn;
        d_kousi_b_nagasa = dkyn;
        d_kousi_kakudo = -dkk;

        kousi_keisan();


    }

    // ----------------------------------------
    public void kousi_keisan() {

        d_kousi_ax = d_kousi_haba * d_kousi_a_nagasa;
        d_kousi_ay = d_kousi_haba * 0.0;

        double d_rad = (Math.PI / 180) * d_kousi_kakudo;
        d_kousi_bx = d_kousi_haba * d_kousi_b_nagasa * Math.cos(d_rad);
        d_kousi_by = d_kousi_haba * d_kousi_b_nagasa * Math.sin(d_rad);

        taikakusen_max = oc.kyori(new Ten(0.0, 0.0), new Ten(d_kousi_ax + d_kousi_bx, d_kousi_ay + d_kousi_by));
        taikakusen_min = oc.kyori(new Ten(d_kousi_ax, d_kousi_ay), new Ten(d_kousi_bx, d_kousi_by));
        if (taikakusen_max < taikakusen_min) {
            taikakusen_min = oc.kyori(new Ten(0.0, 0.0), new Ten(d_kousi_ax + d_kousi_bx, d_kousi_ay + d_kousi_by));
            taikakusen_max = oc.kyori(new Ten(d_kousi_ax, d_kousi_ay), new Ten(d_kousi_bx, d_kousi_by));
        }


        if (jyoutai() == 1) {
            if (Math.abs(d_kousi_a_nagasa - 1.0) > 0.000001) {
                set_i_kitei_jyoutai(2);
            }
            if (Math.abs(d_kousi_b_nagasa - 1.0) > 0.000001) {
                set_i_kitei_jyoutai(2);
            }
            if (Math.abs(d_kousi_kakudo - (-90.0)) > 0.000001) {
                set_i_kitei_jyoutai(2);
            }
        }


    }


    public void kousi_senhaba_sage() {
        kousi_senhaba = kousi_senhaba - 2;
        if (kousi_senhaba < 1) {
            kousi_senhaba = 1;
        }
    }

    public void kousi_senhaba_age() {
        kousi_senhaba = kousi_senhaba + 2;
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
            if (Math.abs(d_kousi_a_nagasa - 1.0) > 0.000001) {
                set_i_kitei_jyoutai(2);
            }
            if (Math.abs(d_kousi_b_nagasa - 1.0) > 0.000001) {
                set_i_kitei_jyoutai(2);
            }
            if (Math.abs(d_kousi_kakudo - (-90.0)) > 0.000001) {
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

    public Ten get_sisuu(Ten t0) {//obj系座標のTenから、格子の指数を得る
        //行列 [d_kousi_ax, d_kousi_bx]によって[1]は格子ベクトルaに変換され、[1]は格子ベクトルbに変換される。
        //     [d_kousi_ay, d_kousi_by]        [0]　　　　　　　 　　　　　　[0]
        //この逆行列によってobj系座標のTenは格子の指数に変換される。
        //　　　　　
        //行列の記号の定義
        double ax = d_kousi_ax;
        double ay = d_kousi_ay;
        double bx = d_kousi_bx;
        double by = d_kousi_by;

        //逆行列の記号の定義
        double det = ax * by - bx * ay;
        double gax = by / det;
        double gay = -ay / det;
        double gbx = -bx / det;
        double gby = ax / det;


        double kx = t0.getx() - okx0;
        double ky = t0.gety() - oky0;

        double sisuu_x = gax * kx + gbx * ky;
        double sisuu_y = gay * kx + gby * ky;

        return new Ten(sisuu_x, sisuu_y);

    }

    // ----------------------------
    public int get_a_sisuu_min(Ten p_a, Ten p_b, Ten p_c, Ten p_d) {//obj座標系の4つの点を指定し、各点のaベクトルの指数より小さい整数の指数を得る。
        Ten p_a_sisuu = new Ten();
        p_a_sisuu.set(get_sisuu(p_a));//p_aの格子系の指数
        Ten p_b_sisuu = new Ten();
        p_b_sisuu.set(get_sisuu(p_b));//p_bの格子系の指数
        Ten p_c_sisuu = new Ten();
        p_c_sisuu.set(get_sisuu(p_c));//p_cの格子系の指数
        Ten p_d_sisuu = new Ten();
        p_d_sisuu.set(get_sisuu(p_d));//p_dの格子系の指数

        double a_sisuu_max = p_a_sisuu.getx();
        if (p_b_sisuu.getx() > a_sisuu_max) {
            a_sisuu_max = p_b_sisuu.getx();
        }
        if (p_c_sisuu.getx() > a_sisuu_max) {
            a_sisuu_max = p_c_sisuu.getx();
        }
        if (p_d_sisuu.getx() > a_sisuu_max) {
            a_sisuu_max = p_d_sisuu.getx();
        }
        int kousi_a_max = (int) Math.ceil(a_sisuu_max);
        double a_sisuu_min = p_a_sisuu.getx();
        if (p_b_sisuu.getx() < a_sisuu_min) {
            a_sisuu_min = p_b_sisuu.getx();
        }
        if (p_c_sisuu.getx() < a_sisuu_min) {
            a_sisuu_min = p_c_sisuu.getx();
        }
        if (p_d_sisuu.getx() < a_sisuu_min) {
            a_sisuu_min = p_d_sisuu.getx();
        }
        int kousi_a_min = (int) Math.floor(a_sisuu_min);
        double b_sisuu_max = p_a_sisuu.gety();
        if (p_b_sisuu.gety() > b_sisuu_max) {
            b_sisuu_max = p_b_sisuu.gety();
        }
        if (p_c_sisuu.gety() > b_sisuu_max) {
            b_sisuu_max = p_c_sisuu.gety();
        }
        if (p_d_sisuu.gety() > b_sisuu_max) {
            b_sisuu_max = p_d_sisuu.gety();
        }
        int kousi_b_max = (int) Math.ceil(b_sisuu_max);
        double b_sisuu_min = p_a_sisuu.gety();
        if (p_b_sisuu.gety() < b_sisuu_min) {
            b_sisuu_min = p_b_sisuu.gety();
        }
        if (p_c_sisuu.gety() < b_sisuu_min) {
            b_sisuu_min = p_c_sisuu.gety();
        }
        if (p_d_sisuu.gety() < b_sisuu_min) {
            b_sisuu_min = p_d_sisuu.gety();
        }
        int kousi_b_min = (int) Math.floor(b_sisuu_min);

        //AAAAAAAAAAAAAAAAAAA
        return kousi_a_min;
    }

    // ----------------------------
    public int get_a_sisuu_max(Ten p_a, Ten p_b, Ten p_c, Ten p_d) {//obj座標系の4つの点を指定し、各点のaベクトルの指数より大きい整数の指数を得る。
        Ten p_a_sisuu = new Ten();
        p_a_sisuu.set(get_sisuu(p_a));//p_aの格子系の指数
        Ten p_b_sisuu = new Ten();
        p_b_sisuu.set(get_sisuu(p_b));//p_bの格子系の指数
        Ten p_c_sisuu = new Ten();
        p_c_sisuu.set(get_sisuu(p_c));//p_cの格子系の指数
        Ten p_d_sisuu = new Ten();
        p_d_sisuu.set(get_sisuu(p_d));//p_dの格子系の指数

        double a_sisuu_max = p_a_sisuu.getx();
        if (p_b_sisuu.getx() > a_sisuu_max) {
            a_sisuu_max = p_b_sisuu.getx();
        }
        if (p_c_sisuu.getx() > a_sisuu_max) {
            a_sisuu_max = p_c_sisuu.getx();
        }
        if (p_d_sisuu.getx() > a_sisuu_max) {
            a_sisuu_max = p_d_sisuu.getx();
        }
        int kousi_a_max = (int) Math.ceil(a_sisuu_max);
        double a_sisuu_min = p_a_sisuu.getx();
        if (p_b_sisuu.getx() < a_sisuu_min) {
            a_sisuu_min = p_b_sisuu.getx();
        }
        if (p_c_sisuu.getx() < a_sisuu_min) {
            a_sisuu_min = p_c_sisuu.getx();
        }
        if (p_d_sisuu.getx() < a_sisuu_min) {
            a_sisuu_min = p_d_sisuu.getx();
        }
        int kousi_a_min = (int) Math.floor(a_sisuu_min);
        double b_sisuu_max = p_a_sisuu.gety();
        if (p_b_sisuu.gety() > b_sisuu_max) {
            b_sisuu_max = p_b_sisuu.gety();
        }
        if (p_c_sisuu.gety() > b_sisuu_max) {
            b_sisuu_max = p_c_sisuu.gety();
        }
        if (p_d_sisuu.gety() > b_sisuu_max) {
            b_sisuu_max = p_d_sisuu.gety();
        }
        int kousi_b_max = (int) Math.ceil(b_sisuu_max);
        double b_sisuu_min = p_a_sisuu.gety();
        if (p_b_sisuu.gety() < b_sisuu_min) {
            b_sisuu_min = p_b_sisuu.gety();
        }
        if (p_c_sisuu.gety() < b_sisuu_min) {
            b_sisuu_min = p_c_sisuu.gety();
        }
        if (p_d_sisuu.gety() < b_sisuu_min) {
            b_sisuu_min = p_d_sisuu.gety();
        }
        int kousi_b_min = (int) Math.floor(b_sisuu_min);

        //AAAAAAAAAAAAAAAAAAA
        return kousi_a_max;
    }

    // ----------------------------
    public int get_b_sisuu_min(Ten p_a, Ten p_b, Ten p_c, Ten p_d) {//obj座標系の4つの点を指定し、各点のbベクトルの指数より小さい整数の指数を得る。
        Ten p_a_sisuu = new Ten();
        p_a_sisuu.set(get_sisuu(p_a));//p_aの格子系の指数
        Ten p_b_sisuu = new Ten();
        p_b_sisuu.set(get_sisuu(p_b));//p_bの格子系の指数
        Ten p_c_sisuu = new Ten();
        p_c_sisuu.set(get_sisuu(p_c));//p_cの格子系の指数
        Ten p_d_sisuu = new Ten();
        p_d_sisuu.set(get_sisuu(p_d));//p_dの格子系の指数

        double a_sisuu_max = p_a_sisuu.getx();
        if (p_b_sisuu.getx() > a_sisuu_max) {
            a_sisuu_max = p_b_sisuu.getx();
        }
        if (p_c_sisuu.getx() > a_sisuu_max) {
            a_sisuu_max = p_c_sisuu.getx();
        }
        if (p_d_sisuu.getx() > a_sisuu_max) {
            a_sisuu_max = p_d_sisuu.getx();
        }
        int kousi_a_max = (int) Math.ceil(a_sisuu_max);
        double a_sisuu_min = p_a_sisuu.getx();
        if (p_b_sisuu.getx() < a_sisuu_min) {
            a_sisuu_min = p_b_sisuu.getx();
        }
        if (p_c_sisuu.getx() < a_sisuu_min) {
            a_sisuu_min = p_c_sisuu.getx();
        }
        if (p_d_sisuu.getx() < a_sisuu_min) {
            a_sisuu_min = p_d_sisuu.getx();
        }
        int kousi_a_min = (int) Math.floor(a_sisuu_min);
        double b_sisuu_max = p_a_sisuu.gety();
        if (p_b_sisuu.gety() > b_sisuu_max) {
            b_sisuu_max = p_b_sisuu.gety();
        }
        if (p_c_sisuu.gety() > b_sisuu_max) {
            b_sisuu_max = p_c_sisuu.gety();
        }
        if (p_d_sisuu.gety() > b_sisuu_max) {
            b_sisuu_max = p_d_sisuu.gety();
        }
        int kousi_b_max = (int) Math.ceil(b_sisuu_max);
        double b_sisuu_min = p_a_sisuu.gety();
        if (p_b_sisuu.gety() < b_sisuu_min) {
            b_sisuu_min = p_b_sisuu.gety();
        }
        if (p_c_sisuu.gety() < b_sisuu_min) {
            b_sisuu_min = p_c_sisuu.gety();
        }
        if (p_d_sisuu.gety() < b_sisuu_min) {
            b_sisuu_min = p_d_sisuu.gety();
        }
        int kousi_b_min = (int) Math.floor(b_sisuu_min);

        //AAAAAAAAAAAAAAAAAAA
        return kousi_b_min;
    }

    // ----------------------------
    public int get_b_sisuu_max(Ten p_a, Ten p_b, Ten p_c, Ten p_d) {//obj座標系の4つの点を指定し、各点のbベクトルの指数より大きい整数の指数を得る。
        Ten p_a_sisuu = new Ten();
        p_a_sisuu.set(get_sisuu(p_a));//p_aの格子系の指数
        Ten p_b_sisuu = new Ten();
        p_b_sisuu.set(get_sisuu(p_b));//p_bの格子系の指数
        Ten p_c_sisuu = new Ten();
        p_c_sisuu.set(get_sisuu(p_c));//p_cの格子系の指数
        Ten p_d_sisuu = new Ten();
        p_d_sisuu.set(get_sisuu(p_d));//p_dの格子系の指数

        double a_sisuu_max = p_a_sisuu.getx();
        if (p_b_sisuu.getx() > a_sisuu_max) {
            a_sisuu_max = p_b_sisuu.getx();
        }
        if (p_c_sisuu.getx() > a_sisuu_max) {
            a_sisuu_max = p_c_sisuu.getx();
        }
        if (p_d_sisuu.getx() > a_sisuu_max) {
            a_sisuu_max = p_d_sisuu.getx();
        }
        int kousi_a_max = (int) Math.ceil(a_sisuu_max);
        double a_sisuu_min = p_a_sisuu.getx();
        if (p_b_sisuu.getx() < a_sisuu_min) {
            a_sisuu_min = p_b_sisuu.getx();
        }
        if (p_c_sisuu.getx() < a_sisuu_min) {
            a_sisuu_min = p_c_sisuu.getx();
        }
        if (p_d_sisuu.getx() < a_sisuu_min) {
            a_sisuu_min = p_d_sisuu.getx();
        }
        int kousi_a_min = (int) Math.floor(a_sisuu_min);
        double b_sisuu_max = p_a_sisuu.gety();
        if (p_b_sisuu.gety() > b_sisuu_max) {
            b_sisuu_max = p_b_sisuu.gety();
        }
        if (p_c_sisuu.gety() > b_sisuu_max) {
            b_sisuu_max = p_c_sisuu.gety();
        }
        if (p_d_sisuu.gety() > b_sisuu_max) {
            b_sisuu_max = p_d_sisuu.gety();
        }
        int kousi_b_max = (int) Math.ceil(b_sisuu_max);
        double b_sisuu_min = p_a_sisuu.gety();
        if (p_b_sisuu.gety() < b_sisuu_min) {
            b_sisuu_min = p_b_sisuu.gety();
        }
        if (p_c_sisuu.gety() < b_sisuu_min) {
            b_sisuu_min = p_c_sisuu.gety();
        }
        if (p_d_sisuu.gety() < b_sisuu_min) {
            b_sisuu_min = p_d_sisuu.gety();
        }
        int kousi_b_min = (int) Math.floor(b_sisuu_min);

        //AAAAAAAAAAAAAAAAAAA
        return kousi_b_max;
    }


    public void set_kousi_color(Color color0) {
        kousi_color = color0;
    }

    public Color get_kousi_color() {
        return kousi_color;
    }

    public void set_kousi_memori_color(Color color0) {
        kousi_memori_color = color0;
    }

    public Color get_kousi_memori_color() {
        return kousi_memori_color;
    }


    //描画-----------------------------------------------------------------
    public void oekaki(Graphics g, Camera camera, int p0x_max, int p0y_max, int i_irokae) {    //i_irokae=1なら一定数ごとに格子線の色を変える
        //入力規定が1か2（正方格子）の場合の格子線の描画
        Graphics2D g2 = (Graphics2D) g;

        Senbun s_tv = new Senbun();
        //Ten a =new Ten(); Ten b =new Ten();

        Senbun s_ob = new Senbun();


        //格子線の描画
        //g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状
        g2.setStroke(new BasicStroke((float) kousi_senhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状


        if (jyoutai() != 0) {
            Ten p0_a = new Ten();
            p0_a.set(0, 0);//画面の左上カドのTV系座標
            Ten p0_b = new Ten();
            p0_b.set(0, p0y_max);//画面の左下カドのTV系座標
            Ten p0_c = new Ten();
            p0_c.set(p0x_max, p0y_max);//画面の右下カドのTV系座標
            Ten p0_d = new Ten();
            p0_d.set(p0x_max, 0);//画面の右上カドのTV系座標

            Ten p_a = new Ten();
            p_a.set(camera.TV2object(p0_a));//画面の左上カドのobj系座標
            Ten p_b = new Ten();
            p_b.set(camera.TV2object(p0_b));//画面の左下カドのobj系座標
            Ten p_c = new Ten();
            p_c.set(camera.TV2object(p0_c));//画面の右下カドのobj系座標
            Ten p_d = new Ten();
            p_d.set(camera.TV2object(p0_d));//画面の右上カドのobj系座標


            int kousi_gamen_a_max = get_a_sisuu_max(p_a, p_b, p_c, p_d);
            int kousi_gamen_a_min = get_a_sisuu_min(p_a, p_b, p_c, p_d);
            int kousi_gamen_b_max = get_b_sisuu_max(p_a, p_b, p_c, p_d);
            int kousi_gamen_b_min = get_b_sisuu_min(p_a, p_b, p_c, p_d);

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
                g.setColor(kousi_color);
                for (int i = kousi_gamen_a_min; i <= kousi_gamen_a_max; i++) {
                    //double k_zah = d_haba()*i;

                    s_ob.set(d_kousi_ax * i + d_kousi_bx * kousi_gamen_b_min + okx0,
                            d_kousi_ay * i + d_kousi_by * kousi_gamen_b_min + oky0,
                            d_kousi_ax * i + d_kousi_bx * kousi_gamen_b_max + okx0,
                            d_kousi_ay * i + d_kousi_by * kousi_gamen_b_max + oky0);
                    s_tv.set(camera.object2TV(s_ob));
                    g.drawLine((int) s_tv.getax(), (int) s_tv.getay(), (int) s_tv.getbx(), (int) s_tv.getby()); //直線
                }

                for (int i = kousi_gamen_b_min; i <= kousi_gamen_b_max; i++) {
                    //double k_zah = d_haba()*i;

                    s_ob.set(d_kousi_ax * kousi_gamen_a_min + d_kousi_bx * i + okx0,
                            d_kousi_ay * kousi_gamen_a_min + d_kousi_by * i + oky0,
                            d_kousi_ax * kousi_gamen_a_max + d_kousi_bx * i + okx0,
                            d_kousi_ay * kousi_gamen_a_max + d_kousi_by * i + oky0);


                    s_tv.set(camera.object2TV(s_ob));
                    g.drawLine((int) s_tv.getax(), (int) s_tv.getay(), (int) s_tv.getbx(), (int) s_tv.getby()); //直線
                }

                //一定数ごとに格子線の色を変える-----------------------------------------------
                if (i_irokae == 1) {

                    //g.setColor(new Color(180, 200,180));
                    g.setColor(kousi_memori_color);

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


                            s_ob.set(d_kousi_ax * i + d_kousi_bx * kousi_gamen_b_min + okx0,
                                    d_kousi_ay * i + d_kousi_by * kousi_gamen_b_min + oky0,
                                    d_kousi_ax * i + d_kousi_bx * kousi_gamen_b_max + okx0,
                                    d_kousi_ay * i + d_kousi_by * kousi_gamen_b_max + oky0);
                            s_tv.set(camera.object2TV(s_ob));
                            g.drawLine((int) s_tv.getax(), (int) s_tv.getay(), (int) s_tv.getbx(), (int) s_tv.getby()); //直線
                        }
                    }

                    for (int i = kousi_gamen_b_min; i <= kousi_gamen_b_max; i++) {
                        i_jyouyo = i % a_to_heikouna_memori_kannkaku;
                        if (i_jyouyo < 0) {
                            i_jyouyo = i_jyouyo + a_to_heikouna_memori_kannkaku;
                        }

                        if (i_jyouyo == a_to_heikouna_memori_iti) {

                            s_ob.set(d_kousi_ax * kousi_gamen_a_min + d_kousi_bx * i + okx0,
                                    d_kousi_ay * kousi_gamen_a_min + d_kousi_by * i + oky0,
                                    d_kousi_ax * kousi_gamen_a_max + d_kousi_bx * i + okx0,
                                    d_kousi_ay * kousi_gamen_a_max + d_kousi_by * i + oky0);


                            s_tv.set(camera.object2TV(s_ob));
                            g.drawLine((int) s_tv.getax(), (int) s_tv.getay(), (int) s_tv.getbx(), (int) s_tv.getby()); //直線
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

    public Ten moyori_kousi_ten(Ten t0) {

        Ten t2 = new Ten(); //格子点
        double kousi_x;
        double kousi_y;


        kousi_x = Math.round((t0.getx() - okx0) / d_haba()) * d_haba() + okx0;
        kousi_y = Math.round((t0.gety() - oky0) / d_haba()) * d_haba() + oky0;


        if (bunsuu() > 0) {

            //用紙枠の中の格子点との近さを検討
            if (jyoutai() == 1) {

                Ten t_1 = new Ten(t0.getx() - taikakusen_max, t0.gety() - taikakusen_max);
                Ten t_2 = new Ten(t0.getx() - taikakusen_max, t0.gety() + taikakusen_max);
                Ten t_3 = new Ten(t0.getx() + taikakusen_max, t0.gety() + taikakusen_max);
                Ten t_4 = new Ten(t0.getx() + taikakusen_max, t0.gety() - taikakusen_max);

                int kousi_a_max = get_a_sisuu_max(t_1, t_2, t_3, t_4);
                int kousi_a_min = get_a_sisuu_min(t_1, t_2, t_3, t_4);
                int kousi_b_max = get_b_sisuu_max(t_1, t_2, t_3, t_4);
                int kousi_b_min = get_b_sisuu_min(t_1, t_2, t_3, t_4);


                double kyori_min = taikakusen_max;
                for (int i = kousi_a_min; i <= kousi_a_max; i++) {
                    for (int j = kousi_b_min; j <= kousi_b_max; j++) {

                        Ten t_tmp = new Ten(okx0 + d_kousi_ax * i + d_kousi_bx * j, oky0 + d_kousi_ay * i + d_kousi_by * j);
                        if (((-200.000001 <= t_tmp.getx()) && (t_tmp.getx() <= 200.000001)) && ((-200.000001 <= t_tmp.gety()) && (t_tmp.gety() <= 200.000001))) {

                            if (t0.kyori(t_tmp) <= kyori_min) {
                                kyori_min = t0.kyori(t_tmp);
                                t2.set(t_tmp);
                            }

                        }
                    }
                }
            }

            //用紙枠の内外に関係なく格子点との近さを検討
            if (jyoutai() == 2) {

                Ten t_1 = new Ten(t0.getx() - taikakusen_max, t0.gety() - taikakusen_max);
                Ten t_2 = new Ten(t0.getx() - taikakusen_max, t0.gety() + taikakusen_max);
                Ten t_3 = new Ten(t0.getx() + taikakusen_max, t0.gety() + taikakusen_max);
                Ten t_4 = new Ten(t0.getx() + taikakusen_max, t0.gety() - taikakusen_max);

                int kousi_a_max = get_a_sisuu_max(t_1, t_2, t_3, t_4);
                int kousi_a_min = get_a_sisuu_min(t_1, t_2, t_3, t_4);
                int kousi_b_max = get_b_sisuu_max(t_1, t_2, t_3, t_4);
                int kousi_b_min = get_b_sisuu_min(t_1, t_2, t_3, t_4);


                double kyori_min = taikakusen_max;
                for (int i = kousi_a_min; i <= kousi_a_max; i++) {
                    for (int j = kousi_b_min; j <= kousi_b_max; j++) {
                        Ten t_tmp = new Ten(okx0 + d_kousi_ax * i + d_kousi_bx * j, oky0 + d_kousi_ay * i + d_kousi_by * j);
                        if (t0.kyori(t_tmp) <= kyori_min) {
                            kyori_min = t0.kyori(t_tmp);
                            t2.set(t_tmp);
                        }
                    }
                }
            }


        }
        return t2;
    }


}
