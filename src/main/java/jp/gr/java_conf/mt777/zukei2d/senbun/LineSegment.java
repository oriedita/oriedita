package jp.gr.java_conf.mt777.zukei2d.senbun;

import java.awt.*;

import jp.gr.java_conf.mt777.zukei2d.ten.Point;
//import  jp.gr.java_conf.mt777.zukei2d.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class LineSegment {
    private final Point a = new Point(); //枝のa点
    private final Point b = new Point(); //枝のb
    int iactive;//0 is inactive. 1 is active in a. 2 is active in b. 3 is active in both a and b.
    int icol;//色の指定　0=black,1=blue,2=red.

    int customized = 0;//Custom property parameters
    Color customizedColor = new Color(100, 200, 200);//Color if custom made

    int i_select;//0は選択されていない。1以上は場面に応じて適宜設定
    int i_max_x;//端点のx座標の切り上げ時の大きいほう
    int i_min_x;//端点のx座標の切り捨て時の小さいほう
    int i_max_y;//端点のy座標の切り上げ時の大きいほう
    int i_min_y;//端点のy座標の切り捨て時の小さいほう

    //コンストラクタ
    public LineSegment() {
        a.set(0.0, 0.0);
        b.set(0.0, 0.0);
        iactive = 0;
        icol = 0;
        i_select = 0;
        i_max_x = 0;
        i_min_x = 0;
        i_max_y = 0;
        i_min_y = 0;
    }

    public LineSegment(Point t1, Point t2) {
        a.set(t1);
        b.set(t2);
        iactive = 0;
        icol = 0;
        i_select = 0;
        i_max_x = 0;
        i_min_x = 0;
        i_max_y = 0;
        i_min_y = 0;
    }

    public LineSegment(Point t1, Point t2, int icollor) {
        a.set(t1);
        b.set(t2);
        iactive = 0;
        icol = icollor;
        i_select = 0;
        i_max_x = 0;
        i_min_x = 0;
        i_max_y = 0;
        i_min_y = 0;
    }

    public LineSegment(double i1, double i2, double i3, double i4) {
        a.set(i1, i2);
        b.set(i3, i4);
        iactive = 0;
        icol = 0;
        i_select = 0;
        i_max_x = 0;
        i_min_x = 0;
        i_max_y = 0;
        i_min_y = 0;
    }


    public void reset() {
        a.set(0.0, 0.0);
        b.set(0.0, 0.0);
        iactive = 0;
        icol = 0;
        i_select = 0;
        i_max_x = 0;
        i_min_x = 0;
        i_max_y = 0;
        i_min_y = 0;
    }


    //d2s ダブルをストリングに変える　小数点2桁目で四捨五入("");public void hyouji(String str0)にしか使っていない。
    public String d2s(double d0) {
        BigDecimal bd = new BigDecimal(d0);

        //小数第2位で四捨五入
        BigDecimal bd1 = bd.setScale(1, RoundingMode.HALF_UP);

        String sr;
        sr = bd1.toString();
        return sr;
    }

    public void display(String str0) {
        System.out.println(str0 + " (" + d2s(a.getX()) + " , " + d2s(a.getY()) + "),(" + d2s(b.getX()) + " , " + d2s(b.getY()) + ") ,ia=" + iactive + ",ic=" + icol + ",is=" + i_select);
    }


    //-------------------------------------------
    public void set(LineSegment s) {
        a.set(s.getA());
        b.set(s.getB());
        iactive = s.getiactive();
        icol = s.getColor();
        i_select = s.get_i_select();
        i_max_x = s.get_i_max_x();
        i_min_x = s.get_i_min_x();
        i_max_y = s.get_i_max_y();
        i_min_y = s.get_i_min_y();
        setCustomized(s.getCustomized());
        setCustomizedColor(s.getCustomizedColor());


    }

    //----------
    public void set(double ax, double ay, double bx, double by) {
        a.set(ax, ay);
        b.set(bx, by);
        i_max_x = (int) Math.ceil(ax);
        i_min_x = (int) Math.floor(bx);
        if (ax < bx) {
            i_max_x = (int) Math.ceil(bx);
            i_min_x = (int) Math.floor(ax);
        }
        i_max_y = (int) Math.ceil(ay);
        i_min_y = (int) Math.floor(by);
        if (ay < by) {
            i_max_y = (int) Math.ceil(by);
            i_min_y = (int) Math.floor(ay);
        }
    }

    //----------
    public void set(double ax, double ay, double bx, double by, int ic) {
        set(ax, ay, bx, by);
        icol = ic;
    }

    //----------
    public void setA(Point p) {
        set(p.getX(), p.getY(), b.getX(), b.getY());
    }

    public void setB(Point p) {
        set(a.getX(), a.getY(), p.getX(), p.getY());
    }

    //public void seta(Ten p){a.set(p);}
    //public void setb(Ten p){b.set(p);}

    //----------
    //活性化された点の座標をpにする   !!!!!!!!!!!!この関数は間違えたとき、気づきにくいので危険、できれば別名に変える20170507
    public void set(Point p) {
        if (iactive == 1) {
            setA(p);
        }
        if (iactive == 2) {
            setB(p);
        }
    }


    //---------
    public void set(Point p, Point q, int ic, int ia) {
        set(p, q);
        icol = ic;
        iactive = ia;
    }

    public void set(Point p, Point q, int ic) {
        set(p, q);
        icol = ic;
    }

    public void set(Point p, Point q) {
        set(p.getX(), p.getY(), q.getX(), q.getY());
    }

    //-------------------------------------------
    public int get_i_max_x() {
        return i_max_x;
    }

    public int get_i_min_x() {
        return i_min_x;
    }

    public int get_i_max_y() {
        return i_max_y;
    }

    public int get_i_min_y() {
        return i_min_y;
    }


    public void setcolor(int i) {
        icol = i;
    }

    public int getColor() {
        return icol;
    }

    public void setiactive(int i) {
        iactive = i;
    }

    public int getiactive() {
        return iactive;
    }


    public void set_i_select(int i) {
        i_select = i;
    }

    public int get_i_select() {
        return i_select;
    }

    //This line segment is activated depending on whether it is close to a certain point.
    public void activate(Point p, double r) {
        iactive = 0;
        if (p.distanceSquared(a) <= r * r) {
            iactive = 1;
        }
        if (p.distanceSquared(b) <= r * r) {
            iactive = 2;
        }
    }

    //Deactivate this line segment
    public void deactivate() {
        iactive = 0;
    }


    //Exchange the coordinates of both end points a and b
    public void a_b_swap() {
        Point t_temp = new Point(a);
        a.set(b);
        b.set(t_temp);
    }


    public Point getA() {
        return new Point(a.getX(), a.getY());
    }

    public Point getB() {
        return new Point(b.getX(), b.getY());
    }

    public Point getClosestEndpoint(Point p) {//Returns the endpoint closest to point P
        if (p.distanceSquared(a) <= p.distanceSquared(b)) {
            return a;
        }
        return b;
    }

    public Point getFurthestEndpoint(Point p) {//Returns the point P and the farther end point
        if (p.distanceSquared(a) >= p.distanceSquared(b)) {
            return a;
        }
        return b;
    }

    public double getLength() {
        return a.distance(b);
    }

    public double getAX() {
        return a.getX();
    }

    public double getAY() {
        return a.getY();
    }

    public double getBX() {
        return b.getX();
    }

    public double getBY() {
        return b.getY();
    }

    public void setax(double d) {
        a.setX(d);
    }

    public void setay(double d) {
        a.setY(d);
    }

    public void setbx(double d) {
        b.setX(d);
    }

    public void setby(double d) {
        b.setY(d);
    }


    public void setCustomized(int i) {
        customized = i;
    }

    public int getCustomized() {
        return customized;
    }

    public void setCustomizedColor(Color c0) {
        customizedColor = c0;
    }

    public Color getCustomizedColor() {
        return customizedColor;
    }
}
