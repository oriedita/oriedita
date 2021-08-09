package jp.gr.java_conf.mt777.zukei2d.ten;

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
        x = a * p.getX() + b * q.getX();
        y = a * p.getY() + b * q.getY();
    } //コンストラクタ

    public void hyouji(String str0) {
        System.out.println(str0 + " (" + x + " , " + y + ")");
    }


    public void set(Point p) {
        x = p.getX();
        y = p.getY();
    }

    //public void set(Ten p){double dx,dy; dx=p.getx();dy=p.gety();x=dx;y=dy;}
    public void set(double i, double j) {
        x = i;
        y = j;
    }

    public void set(double a, Point p, double b, Point q) {
        x = a * p.getX() + b * q.getX();
        y = a * p.getY() + b * q.getY();
    }

    public void setX(double xx) {
        x = xx;
    }

    public void setY(double yy) {
        y = yy;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void reset() {
        x = 0.0;
        y = 0.0;
    }

// int getix(){return (int)x;}
// int getiy(){return (int)y;}

    public void parallel_move(double x1, double y1) {
        x = x + x1;
        y = y + y1;
    }

    //他の点との距離（double）を求める関数----------------------------------------------------
    public double distance(Point p) {
        //double x1=p.getx(),y1=p.gety();
        //return Math.sqrt((x1-x)*(x1-x)+(y1-y)*(y1-y));

        double x1 = p.getX() - x, y1 = p.getY() - y;
        return Math.sqrt(x1 * x1 + y1 * y1);

    }

    //他の点との距離の2乗（double）を求める関数----------------------------------------------------
    public double distanceSquared(Point p) {
        double x1 = p.getX() - x, y1 = p.getY() - y;
        return x1 * x1 + y1 * y1;
    }


    //自Tenを基準としてみたとき、他の点の位置をTenで返す。
    public Point tano_Point_iti(Point taPoint) {
        Point rPoint = new Point();
        rPoint.setX(taPoint.getX() - x);
        rPoint.setY(taPoint.getY() - y);
        return rPoint;
    }

    public void move(Point addPoint) {
        x = x + addPoint.getX();
        y = y + addPoint.getY();
    }


}


//==========================================================


