package jp.gr.java_conf.mt777.zukei2d.en;

import java.awt.*;

import jp.gr.java_conf.mt777.zukei2d.senbun.*;
import jp.gr.java_conf.mt777.zukei2d.oritacalc.tyokusen.*;
import jp.gr.java_conf.mt777.zukei2d.ten.Point;

public class Circle {//点の座標や方向ベクトルなどをあらわすときに用いる

    double x, y, r;//中心の座標と半径

    int icol;//色の指定　0=black,1=blue,2=red.
    int customized = 0;//Custom property parameters
    Color customizedColor = new Color(100, 200, 200);//特注ある場合の色

    public Circle() {
        x = 0.0;
        y = 0.0;
        r = 0.0;
        icol = 0;
    }//コンストラクタ

    public Circle(double i, double j, double k, int m) {
        x = i;
        y = j;
        r = k;
        icol = m;
    } //コンストラクタ

    public Circle(Point tc, double k, int m) {
        x = tc.getX();
        y = tc.getY();
        r = k;
        icol = m;
    } //コンストラクタ

    public Circle(LineSegment s0, int m) {//コンストラクタ 線分を直径とする円
        x = (s0.getAX() + s0.getBX()) / 2.0;
        y = (s0.getAY() + s0.getBY()) / 2.0;
        r = s0.getLength() / 2.0;
        icol = m;
    }


    public void set(Circle e) {
        x = e.getx();
        y = e.gety();
        r = e.getRadius();
        icol = e.getcolor();
        customized = e.getCustomized();
        customizedColor = e.getCustomizedColor();
    }

    public void set(double i, double j, double k, int m) {
        x = i;
        y = j;
        r = k;
        icol = m;
    }

    public void set(Point tc, double k, int m) {
        x = tc.getX();
        y = tc.getY();
        r = k;
        icol = m;
    }

    public void set(double i, double j, double k) {
        x = i;
        y = j;
        r = k;
    }

    public void set(LineSegment s0, int m) {
        x = (s0.getAX() + s0.getBX()) / 2.0;
        y = (s0.getAY() + s0.getBY()) / 2.0;
        r = s0.getLength() / 2.0;
        icol = m;
    }

    public void setx(double xx) {
        x = xx;
    }

    public void sety(double yy) {
        y = yy;
    }

    public void setr(double rr) {
        r = rr;
    }

    public double getx() {
        return x;
    }

    public double gety() {
        return y;
    }

    public double getRadius() {
        return r;
    }

    public void reset() {
        x = 0.0;
        y = 0.0;
        r = 0.0;
        icol = 0;
    }

    public void setcolor(int i) {
        icol = i;
    }

    public int getcolor() {
        return icol;
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

    public void heikou_idou(double x1, double y1) {
        x = x + x1;
        y = y + y1;
    }

    public Point getCenter() {
        return new Point(getx(), gety());
    }

    //Function that inverts other points ----------------------------------------------------
    public Point turnAround(Point t0) {//An error occurs when t0 and (x, y) are in the same position.
        double x1 = t0.getX() - x;
        double y1 = t0.getY() - y;
        double d1 = Math.sqrt(x1 * x1 + y1 * y1);
        double d2, x2, y2, x3, y3;


        if (Math.abs(d1 - r) < 0.0000001) {
            return t0;
        }
        d2 = r * r / d1;
        x2 = d2 * x1 / d1;
        y2 = d2 * y1 / d1;
        x3 = x2 + x;
        y3 = y2 + y;
        return new Point(x3, y3);
    }

    //A function that inverts another circle to a circle ----------------------------------------------------
    public Circle turnAround(Circle e0) {//e0の円周が(x,y)を通らないとき用　//e0の円周が(x,y)を通るときはエラーとなる。またe0の円周の内部に(x,y)がくるときもおかしな結果になるっぽい。
        double x1 = e0.getx() - x;
        double y1 = e0.gety() - y;
        double d1 = Math.sqrt(x1 * x1 + y1 * y1);
        double da1 = d1 - e0.getRadius();
        double db1 = d1 + e0.getRadius();

        double xa1, ya1;
        double xa0, ya0;
        double xb1, yb1;
        double xb0, yb0;

        if (d1 < 0.000001) {
            xa1 = da1;
            ya1 = 0.0;
            xa0 = xa1 + x;
            ya0 = ya1 + y;
            xb1 = db1;
            yb1 = 0.0;
            xb0 = xb1 + x;
            yb0 = yb1 + y;
        } else {
            xa1 = da1 * x1 / d1;
            ya1 = da1 * y1 / d1;
            xa0 = xa1 + x;
            ya0 = ya1 + y;
            xb1 = db1 * x1 / d1;
            yb1 = db1 * y1 / d1;
            xb0 = xb1 + x;
            yb0 = yb1 + y;
        }

        int ic = 5;//if(e0.getcolor()==5){ic=3;}

        return new Circle(new LineSegment(turnAround(new Point(xa0, ya0)), turnAround(new Point(xb0, yb0))), ic);
    }

    //(x,y)を通る他の円を線分に反転する関数----------------------------------------------------
    public LineSegment turnAround_CircleToLineSegment(Circle e0) {//e0の円周が(x,y)を通るとき用　//e0の円周が(x,y)を通らないときはおかしな結果になる。
        double x1 = e0.getx() - x, y1 = e0.gety() - y;
        Point th = new Point();
        th.set(turnAround(new Point(x1 * 2.0 + x, y1 * 2.0 + y)));
        Point t1 = new Point();
        t1.set(th.getX() - x, th.getY() - y);
        Point tha = new Point();
        tha.set(th.getX() + 3.0 * y1, th.getY() - 3.0 * x1);
        Point thb = new Point();
        thb.set(th.getX() - 3.0 * y1, th.getY() + 3.0 * x1);
        return new LineSegment(tha, thb, 3);
    }


    //(x,y)を通らない線分を他の円に反転する関数----------------------------------------------------
    public Circle turnAround_LineSegmentToCircle(LineSegment s0) {//s0が(x,y)を通るときはおかしな結果になる。
        StraightLine ty = new StraightLine(s0);
        Point t0 = new Point();
        t0.set(ty.findShadow(getCenter()));
        return new Circle(new LineSegment(turnAround(t0), getCenter()), 5);
    }
}
