package jp.gr.java_conf.mt777.zukei2d.senbun;

import java.awt.*;

import jp.gr.java_conf.mt777.zukei2d.ten.Point;
//import  jp.gr.java_conf.mt777.zukei2d.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class LineSegment {
    private final Point a = new Point(); //Branch a point
    private final Point b = new Point(); //Branch b point
    int active;//0 is inactive. 1 is active in a. 2 is active in b. 3 is active in both a and b.
    int color;//Color specification 　0=black,1=blue,2=red.

    int customized = 0;//Custom property parameters
    Color customizedColor = new Color(100, 200, 200);//Color if custom made

    int selected;//0 is not selected. 1 or more is set appropriately according to the situation
    int maxX;//Larger when rounding up the x-coordinate of the end point
    int minX;//The smaller one when truncating the x-coordinate of the end point
    int maxY;//Larger when rounding up the y-coordinate of the end point
    int minY;//The smaller one when truncating the y coordinate of the end point

    //コンストラクタ
    public LineSegment() {
        a.set(0.0, 0.0);
        b.set(0.0, 0.0);
        active = 0;
        color = 0;
        selected = 0;
        maxX = 0;
        minX = 0;
        maxY = 0;
        minY = 0;
    }

    public LineSegment(Point t1, Point t2) {
        a.set(t1);
        b.set(t2);
        active = 0;
        color = 0;
        selected = 0;
        maxX = 0;
        minX = 0;
        maxY = 0;
        minY = 0;
    }

    public LineSegment(Point t1, Point t2, int color) {
        a.set(t1);
        b.set(t2);
        active = 0;
        this.color = color;
        selected = 0;
        maxX = 0;
        minX = 0;
        maxY = 0;
        minY = 0;
    }

    public LineSegment(double i1, double i2, double i3, double i4) {
        a.set(i1, i2);
        b.set(i3, i4);
        active = 0;
        color = 0;
        selected = 0;
        maxX = 0;
        minX = 0;
        maxY = 0;
        minY = 0;
    }

    public void reset() {
        a.set(0.0, 0.0);
        b.set(0.0, 0.0);
        active = 0;
        color = 0;
        selected = 0;
        maxX = 0;
        minX = 0;
        maxY = 0;
        minY = 0;
    }

    //d2s Double is changed to a string. Rounded to the second decimal place (""); public void display (String str0) is used only.
    public String d2s(double d0) {
        BigDecimal bd = new BigDecimal(d0);

        //Rounded to the first decimal place
        BigDecimal bd1 = bd.setScale(1, RoundingMode.HALF_UP);

        String sr;
        sr = bd1.toString();
        return sr;
    }

    public void display(String str0) {
        System.out.println(str0 + " (" + d2s(a.getX()) + " , " + d2s(a.getY()) + "),(" + d2s(b.getX()) + " , " + d2s(b.getY()) + ") ,ia=" + active + ",ic=" + color + ",is=" + selected);
    }


    //-------------------------------------------
    public void set(LineSegment s) {
        a.set(s.getA());
        b.set(s.getB());
        active = s.getActive();
        color = s.getColor();
        selected = s.getSelected();
        maxX = s.getMaxX();
        minX = s.getMinX();
        maxY = s.getMaxY();
        minY = s.getMinY();
        setCustomized(s.getCustomized());
        setCustomizedColor(s.getCustomizedColor());
    }

    //----------
    public void set(double ax, double ay, double bx, double by) {
        a.set(ax, ay);
        b.set(bx, by);
        maxX = (int) Math.ceil(ax);
        minX = (int) Math.floor(bx);
        if (ax < bx) {
            maxX = (int) Math.ceil(bx);
            minX = (int) Math.floor(ax);
        }
        maxY = (int) Math.ceil(ay);
        minY = (int) Math.floor(by);
        if (ay < by) {
            maxY = (int) Math.ceil(by);
            minY = (int) Math.floor(ay);
        }
    }

    //----------
    public void set(double ax, double ay, double bx, double by, int ic) {
        set(ax, ay, bx, by);
        color = ic;
    }

    //----------
    public void setA(Point p) {
        set(p.getX(), p.getY(), b.getX(), b.getY());
    }

    public void setB(Point p) {
        set(a.getX(), a.getY(), p.getX(), p.getY());
    }

    //----------
    //Set the coordinates of the activated point to p !!!!!!!!!!!! If you make a mistake, this function is dangerous because it is hard to notice, preferably change it to another name 20170507
    public void set(Point p) {
        if (active == 1) {
            setA(p);
        }
        if (active == 2) {
            setB(p);
        }
    }


    //---------
    public void set(Point p, Point q, int ic, int ia) {
        set(p, q);
        color = ic;
        active = ia;
    }

    public void set(Point p, Point q, int ic) {
        set(p, q);
        color = ic;
    }

    public void set(Point p, Point q) {
        set(p.getX(), p.getY(), q.getX(), q.getY());
    }

    //-------------------------------------------
    public int getMaxX() {
        return maxX;
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMinY() {
        return minY;
    }


    public void setColor(int i) {
        color = i;
    }

    public int getColor() {
        return color;
    }

    public void setActive(int i) {
        active = i;
    }

    public int getActive() {
        return active;
    }

    public void setSelected(int i) {
        selected = i;
    }

    public int getSelected() {
        return selected;
    }

    //This line segment is activated depending on whether it is close to a certain point.
    public void activate(Point p, double r) {
        active = 0;
        if (p.distanceSquared(a) <= r * r) {
            active = 1;
        }
        if (p.distanceSquared(b) <= r * r) {
            active = 2;
        }
    }

    //Deactivate this line segment
    public void deactivate() {
        active = 0;
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

    public void setAX(double d) {
        a.setX(d);
    }

    public void setAY(double d) {
        a.setY(d);
    }

    public void setBX(double d) {
        b.setX(d);
    }

    public void setBY(double d) {
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
