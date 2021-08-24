package jp.gr.java_conf.mt777.origami.orihime.egaki_syokunin.egaki_syokunin_dougubako;

import jp.gr.java_conf.mt777.origami.dougu.orisensyuugou.*;

import jp.gr.java_conf.mt777.zukei2d.ten.*;
import jp.gr.java_conf.mt777.zukei2d.senbun.*;
import jp.gr.java_conf.mt777.zukei2d.oritacalc.*;
import jp.gr.java_conf.mt777.zukei2d.oritacalc.tyokusen.*;

// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
public class Drawing_Worker_Toolbox {
    FoldLineSet ori_s;


    public Drawing_Worker_Toolbox(FoldLineSet o_s) {  //コンストラクタ
        ori_s = o_s;
    }

    //ベクトルab(=s0)を点aからb方向に、最初に他の折線と交差するところまで延長する
    LineSegment kousaten_made_nobasi_lineSegment = new LineSegment();
    Point kousaten_made_nobasi_point = new Point();
    int kousaten_made_nobasi_flg = 0;//abを伸ばした最初の交点の状況
    int kousaten_made_nobasi_orisen_fukumu_flg = 0;//abを直線化したのが、既存の折線を含むなら3
    LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment();//abを直線化したのと、最初にぶつかる既存の折線

    // -------------------
    public void kousaten_made_nobasi_crossing_included_lineSegment_disregard(Point a, Point b) {//ベクトルab(=s0)を点aからb方向に、最初に他の折線(直線に含まれる線分は無視。)と交差するところまで延長する//他の折線と交差しないなら、Point aを返す
        LineSegment s0 = new LineSegment();
        s0.set(a, b);
        LineSegment add_sen = new LineSegment();
        add_sen.set(s0);
        Point kousa_point = new Point(1000000.0, 1000000.0); //この方法だと、エラーの原因になりうる。本当なら全線分のx_max、y_max以上の点を取ればいい。今後修正予定20161120
        double kousa_ten_kyori = kousa_point.distance(add_sen.getA());
        StraightLine tyoku1 = new StraightLine(add_sen.getA(), add_sen.getB());
        int i_kousa_flg;

        kousaten_made_nobasi_flg = 0;
        kousaten_made_nobasi_orisen_fukumu_flg = 0;
        for (int i = 1; i <= ori_s.getTotal(); i++) {

            i_kousa_flg = tyoku1.lineSegment_intersect_reverse_detail(ori_s.get(i));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
            //if(i_kousa_flg==3){kousaten_made_nobasi_orisen_fukumu_flg=3;}
            if ((i_kousa_flg == 1 || i_kousa_flg == 21) || i_kousa_flg == 22) {

                kousa_point.set(OritaCalc.findIntersection(tyoku1, ori_s.get(i)));//線分を直線とみなして他の直線との交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す

                if (kousa_point.distance(add_sen.getA()) > 0.00001) {

                    if (kousa_point.distance(add_sen.getA()) < kousa_ten_kyori) {
                        double d_kakudo = OritaCalc.angle(add_sen.getA(), add_sen.getB(), add_sen.getA(), kousa_point);

                        if (d_kakudo < 1.0 || d_kakudo > 359.0) {

                            kousa_ten_kyori = kousa_point.distance(add_sen.getA());
                            add_sen.set(add_sen.getA(), kousa_point);

                            kousaten_made_nobasi_flg = i_kousa_flg;
                            kousaten_made_nobasi_saisyono_lineSegment.set(ori_s.get(i));

                        }

                    }

                }

            }

        }
        //return add_sen.getb();

        kousaten_made_nobasi_lineSegment.set(add_sen);//System.out.println("kousaten_made_nobasi_senbun.set 20201129 kousaten_made_nobasi_keisan_fukumu_senbun_musi");
        kousaten_made_nobasi_point.set(add_sen.getB());
    }


    // -------------------
    public void kousaten_made_nobasi_keisan_fukumu_senbun_musi_new(Point a, Point b) {//ベクトルab(=s0)を点aからb方向に、最初に他の折線(直線に含まれる線分は無視。)と交差するところまで延長する//他の折線と交差しないなら、Ten aを返す
        LineSegment s0 = new LineSegment();
        s0.set(a, b);
        LineSegment add_sen = new LineSegment();
        add_sen.set(s0);
        Point kousa_point = new Point(1000000.0, 1000000.0); //この方法だと、エラーの原因になりうる。本当なら全線分のx_max、y_max以上の点を取ればいい。今後修正予定20161120
        double kousa_ten_kyori = kousa_point.distance(add_sen.getA());
        StraightLine tyoku1 = new StraightLine(add_sen.getA(), add_sen.getB());
        int i_kousa_flg;

        kousaten_made_nobasi_flg = 0;
        kousaten_made_nobasi_orisen_fukumu_flg = 0;
        for (int i = 1; i <= ori_s.getTotal(); i++) {

//System.out.println("000 20201129 col = "+ori_s.get(i).getcolor());  
            if (ori_s.get(i).getColor().isFoldingLine()) {//System.out.println("  kousaten_made_nobasi_keisan_fukumu_senbun_musi_new 20201128");
//0=この直線は与えられた線分と交差しない、
//1=X型で交差する、
//21=線分のa点でT型で交差する、
//22=線分のb点でT型で交差する、
//3=線分は直線に含まれる。
                i_kousa_flg = tyoku1.lineSegment_intersect_reverse_detail(ori_s.get(i));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                //if(i_kousa_flg==3){kousaten_made_nobasi_orisen_fukumu_flg=3;}
                if ((i_kousa_flg == 1 || i_kousa_flg == 21) || i_kousa_flg == 22) {

                    kousa_point.set(OritaCalc.findIntersection(tyoku1, ori_s.get(i)));//線分を直線とみなして他の直線との交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す

                    if (kousa_point.distance(add_sen.getA()) > 0.00001) {

                        if (kousa_point.distance(add_sen.getA()) < kousa_ten_kyori) {
                            double d_kakudo = OritaCalc.angle(add_sen.getA(), add_sen.getB(), add_sen.getA(), kousa_point);

                            if (d_kakudo < 1.0 || d_kakudo > 359.0) {
//System.out.println("20201129 col = "+ori_s.get(i).getcolor());  
                                kousa_ten_kyori = kousa_point.distance(add_sen.getA());
                                add_sen.set(add_sen.getA(), kousa_point);

                                kousaten_made_nobasi_flg = i_kousa_flg;
                                kousaten_made_nobasi_saisyono_lineSegment.set(ori_s.get(i));

                            }

                        }

                    }

                }
            }
        }
        //return add_sen.getb();

        kousaten_made_nobasi_lineSegment.set(add_sen);//System.out.println("kousaten_made_nobasi_senbun.set 20201129 kousaten_made_nobasi_keisan_fukumu_senbun_musi_new");
        kousaten_made_nobasi_point.set(add_sen.getB());
    }

    // -------------------
    public int get_kousaten_made_nobasi_flg_new(Point a, Point b) {//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
        //kousaten_made_nobasi_keisan_fukumu_senbun_musi_new(a,b);
        return kousaten_made_nobasi_flg;
    }


    // -------------------
    public int get_kousaten_made_nobasi_flg(Point a, Point b) {//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
        kousaten_made_nobasi_crossing_included_lineSegment_disregard(a, b);
        return kousaten_made_nobasi_flg;
    }
// -------------------
/*
public int get_kousaten_made_nobasi_orisen_fukumu_flg(Ten a,Ten b){//abを直線化したのが、既存の折線を含むなら3
	kousaten_made_nobasi_keisan(a,b);
	return kousaten_made_nobasi_orisen_fukumu_flg;
}*/

    // -------------------
    public LineSegment get_kousaten_made_nobasi_senbun(Point a, Point b) {
        kousaten_made_nobasi_crossing_included_lineSegment_disregard(a, b);
        return kousaten_made_nobasi_lineSegment;
    }

    // -------------------
    public LineSegment get_kousaten_made_nobasi_senbun_new() {
        //kousaten_made_nobasi_keisan_fukumu_senbun_musi_new(a,b);
        return kousaten_made_nobasi_lineSegment;
    }

    // -------------------
    public LineSegment get_kousaten_made_nobasi_saisyono_senbun(Point a, Point b) {
        kousaten_made_nobasi_crossing_included_lineSegment_disregard(a, b);
        return kousaten_made_nobasi_saisyono_lineSegment;
    }

    // -------------------
    public LineSegment get_kousaten_made_nobasi_saisyono_senbun_new() {
        //kousaten_made_nobasi_keisan_fukumu_senbun_musi(a,b);
        return kousaten_made_nobasi_saisyono_lineSegment;
    }


    // -------------------
    public Point get_kousaten_made_nobasi_ten(Point a, Point b) {
        kousaten_made_nobasi_crossing_included_lineSegment_disregard(a, b);
        return kousaten_made_nobasi_point;
    }

    public Point get_kousaten_made_nobasi_ten_new() {
        //kousaten_made_nobasi_keisan_fukumu_senbun_musi(a,b);
        return kousaten_made_nobasi_point;
    }


//e_s_dougubako





/*
一値分解する関数itti_bunkai();

public void itti_bunkai(){//（１）2点a,bを指定







}

（１）2点a,bを指定
（２）aを基点とするベクトルabが最初にぶつかる折線との交点cを求める。abと重なる折線は無視
（３）
cが既存の折線の柄の部分だった場合、その線で鏡映し、cをaとし、bを鏡映線の先の点として再帰的に。
cが点だった場合、すでに通過していた点なら、return;


cからベクトルacと一値性を持つベクトルを求める、



*/


    //--------------------------------------------
    public void test1() {//デバック等のテスト用

        System.out.println("_________");

    }

    //--------------------------------------------

    //メモ
    //icol=0 black
    //icol=1 red
    //icol=2 blue
    //icol=3 cyan
    //icol=4 orange
    //icol=5 mazenta
    //icol=6 green
    //icol=7 yellow
    //icol=8 new Color(210,0,255) //紫


}
