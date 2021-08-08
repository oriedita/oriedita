package jp.gr.java_conf.mt777.zukei2d.oritacalc.tyokusen;

import jp.gr.java_conf.mt777.zukei2d.ten.*;
import jp.gr.java_conf.mt777.zukei2d.senbun.*;

public class StraightLine {
    //注意！p1=p2の場合は結果がおかしくなるがこの関数にチェック機構がないので、気づきにくいかも。
    //aは0以上。もしa＝0なら、bが0以上になるようにすること。こうしないと、直線との距離の符号がおかしくなる。
    double a, b, c;//a*x+b*y+c=0,  a,b,c,x,y,は整数として扱う(20181115このコメントおかしいのでは？)

    public StraightLine() {  //コンストラクタ
        double x1 = 0.0;
        double y1 = 0.0;
        double x2 = 1.0;
        double y2 = 1.0;

        a = y2 - y1;
        b = x1 - x2;
        c = y1 * x2 - x1 * y2;
        coefficient();
    }

    public StraightLine(double a0, double b0, double c0) {  //コンストラクタ
        a = a0;
        b = b0;
        c = c0;
        coefficient();
    }


    public StraightLine(Point p1, Point p2) {  //コンストラクタ
        //二点を指定して直線のa,b,cを求める
        double x1 = p1.getx(), y1 = p1.gety();
        double x2 = p2.getx(), y2 = p2.gety();
        a = y2 - y1;
        b = x1 - x2;
        c = y1 * x2 - x1 * y2;
        coefficient();
    }


    public StraightLine(Line s0) {  //コンストラクタ
        //Senbunを指定して直線のa,b,cを求める
        double x1 = s0.getax(), y1 = s0.getay();
        double x2 = s0.getbx(), y2 = s0.getby();
        a = y2 - y1;
        b = x1 - x2;
        c = y1 * x2 - x1 * y2;
        coefficient();
    }


    public StraightLine(double x1, double y1, double x2, double y2) {  //コンストラクタ
        //二点を指定して直線のa,b,cを求める

        a = y2 - y1;
        b = x1 - x2;
        c = y1 * x2 - x1 * y2;
        coefficient();
    }

    //
    void coefficient() {
        if ((a < 0.0)) {
            a = -a;
            b = -b;
            c = -c;
        }
        //	if((a==0.0)&&(b<0.0)){
        if ((-0.1 < a) && (a < 0.1)) {
            if (b < 0.0) {
                a = -a;
                b = -b;
                c = -c;
            }
        }
    }


    public void hyouji(String str0) {
        System.out.println(str0 + "   " + a + " x + " + b + " y + " + c + " = 0.0 ");
    }


    //平行移動
    public void heikouidou(double d) {
        c = c + d * Math.sqrt(a * a + b * b);
    }

    //
    public void set(StraightLine t) {
        a = t.geta();
        b = t.getb();
        c = t.getc();
        coefficient();
    }

    //

    public void seta(double a0) {
        a = a0;
    }

    public void setb(double b0) {
        a = b0;
    }

    public void setc(double c0) {
        a = c0;
    }

    public double geta() {
        return a;
    }

    public double getb() {
        return b;
    }

    public double getc() {
        return c;
    }

    public double calculateDistance(Point p) {//直線と点pとの距離
        double x = p.getx();
        double y = p.gety();
        //return Math.abs((double) ((float)(a*x+b*y+c)/Math.sqrt((float)(a*a+b*b))));
        return Math.abs((a * x + b * y + c) / Math.sqrt(a * a + b * b));
    }


    public double distance_2jyou_keisan(Point p) {//直線と点pとの距離の二乗
        double x = p.getx();
        double y = p.gety();
        //return Math.abs((double) ((float)(a*x+b*y+c)/Math.sqrt((float)(a*a+b*b))));
        return (a * x + b * y + c) * (a * x + b * y + c) / (a * a + b * b);
    }


    public void tyokkouka(Point p) { //点（x,y)を通って ax+by+c=0に直交する直線 (bx-ay+d=0)に変換
        double e;
        double x = p.getx();
        double y = p.gety();
        c = -b * x + a * y;
        e = a;
        a = b;
        b = -e;

        coefficient();
    }


    public int onajigawa(Point p1, Point p2) {//2点が直線の同じ側なら１、別の側なら-1、直線上の点があるなら０を返す
        double dd = dainyuukeisan(p1) * dainyuukeisan(p2);
		return Double.compare(dd, 0.0);
	}


    public double dainyuukeisan(Point p) {
        return a * p.getx() + b * p.gety() + c;
    }  //a*x+b*y+cにx,yを代入した値を返す

    /*
        public int senbun_kousa_hantei(Senbun s0){//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
            double d_a=dainyuukeisan(s0.geta());
            double d_b=dainyuukeisan(s0.getb());

            if(d_a*d_b>0.0){return 0;}
            if(d_a*d_b<0.0){return 1;}
            if(d_a==0.0&&d_b!=0){return 2;}
            if(d_a!=0.0&&d_b==0){return 2;}

            return 3;

        }
    */
    public int senbun_kousa_hantei_kuwasii(Line s0) {//0=この直線は与えられた線分と交差しない、1=X型で交差する、21=線分のa点でT型で交差する、22=線分のb点でT型で交差する、3=線分は直線に含まれる。


        double d_a2 = distance_2jyou_keisan(s0.geta());
        double d_b2 = distance_2jyou_keisan(s0.getb());

        if (d_a2 < 0.00000001 && d_b2 < 0.00000001) {
            return 3;
        }


        if (d_a2 < 0.00000001 && d_b2 >= 0.00000001) {
            return 21;
        }
        if (d_a2 >= 0.00000001 && d_b2 < 0.00000001) {
            return 22;
        }


        //以下は線分のa点もb点も直線上にはないと判断される場合

        double d_a = dainyuukeisan(s0.geta());
        double d_b = dainyuukeisan(s0.getb());

        if (d_a * d_b > 0.0) {
            return 0;
        }
        if (d_a * d_b < 0.0) {
            return 1;
        }


        return 3;

    }


    //他の直線との交点を求める関数　20170312追加
    public Point kouten_motome(StraightLine t2) {
        double a1 = a, b1 = b, c1 = c;//直線t1, a1*x+b1*y+c1=0の各係数を求める。
        double a2 = t2.geta(), b2 = t2.getb(), c2 = t2.getc();//直線t2, a2*x+b2*y+c2=0の各係数を求める。

        return new Point((b1 * c2 - b2 * c1) / (a1 * b2 - a2 * b1), (a2 * c1 - a1 * c2) / (a1 * b2 - a2 * b1));
    }


    //直線上の点pの影の位置（点pと最も近い直線上の位置）を求める。　20170312追加
    public Point kage_motome(Point p) {
        StraightLine t1 = new StraightLine(a, b, c);
        t1.tyokkouka(p);//点p1を通って tに直行する直線を求める。
        return kouten_motome(t1);
    }


}


















