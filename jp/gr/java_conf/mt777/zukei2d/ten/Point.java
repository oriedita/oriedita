package jp.gr.java_conf.mt777.zukei2d.ten;

//import java.util.*;

public class Point {//点の座標や方向ベクトルなどをあらわすときに用いる
    //Used to represent point coordinates, direction vectors, etc.

    double x, y;


    public Point(Point p) {
        set(p);
    }//コンストラクタ

    public Point() {
        x = 0;
        y = 0;
    }//コンストラクタ

    public Point(double i, double j) {
        x = i;
        y = j;
    } //コンストラクタ

    public Point(double a, Point p, double b, Point q) {
        x = a * p.getx() + b * q.getx();
        y = a * p.gety() + b * q.gety();
    } //コンストラクタ

    public void hyouji(String str0) {
        System.out.println(str0 + " (" + x + " , " + y + ")");
    }


    public void set(Point p) {
        x = p.getx();
        y = p.gety();
    }

    //public void set(Ten p){double dx,dy; dx=p.getx();dy=p.gety();x=dx;y=dy;}
    public void set(double i, double j) {
        x = i;
        y = j;
    }

    public void set(double a, Point p, double b, Point q) {
        x = a * p.getx() + b * q.getx();
        y = a * p.gety() + b * q.gety();
    }

    public void setx(double xx) {
        x = xx;
    }

    public void sety(double yy) {
        y = yy;
    }

    //public double getx(){double dx=x;return dx;}
    //public double gety(){double dy=y;return dy;}
    public double getx() {
        return x;
    }

    public double gety() {
        return y;
    }

    public void reset() {
        x = 0.0;
        y = 0.0;
    }

// int getix(){return (int)x;}
// int getiy(){return (int)y;}

    public void heikou_idou(double x1, double y1) {
        x = x + x1;
        y = y + y1;
    }

    //他の点との距離（double）を求める関数----------------------------------------------------
    public double kyori(Point p) {
        //double x1=p.getx(),y1=p.gety();
        //return Math.sqrt((x1-x)*(x1-x)+(y1-y)*(y1-y));

        double x1 = p.getx() - x, y1 = p.gety() - y;
        return Math.sqrt(x1 * x1 + y1 * y1);

    }


    /*	//他の点との距離（double）を求める関数----------------------------------------------------
        public double kyori_(Ten p){
            //double x1=p.getx(),y1=p.gety();
            //return Math.sqrt((x1-x)*(x1-x)+(y1-y)*(y1-y));

            double x1=p.getx()-x,y1=p.gety()-y;
            return Math.sqrt(x1*x1+y1*y1);

        }
    */
    //他の点との距離の2乗（double）を求める関数----------------------------------------------------
    public double kyori2jyou(Point p) {
        double x1 = p.getx() - x, y1 = p.gety() - y;
        return x1 * x1 + y1 * y1;
    }


    //自Tenを基準としてみたとき、他の点の位置をTenで返す。
    public Point tano_Ten_iti(Point taten) {
        Point rten = new Point();
        rten.setx(taten.getx() - x);
        rten.sety(taten.gety() - y);
        return rten;
    }

    public void idou(Point addten) {
        x = x + addten.getx();
        y = y + addten.gety();
    }


}


//==========================================================


