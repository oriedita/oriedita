package jp.gr.java_conf.mt777.zukei2d.oritacalc;

import jp.gr.java_conf.mt777.zukei2d.en.*;
import jp.gr.java_conf.mt777.zukei2d.ten.*;
import jp.gr.java_conf.mt777.zukei2d.oritacalc.tyokusen.*;
import jp.gr.java_conf.mt777.zukei2d.senbun.*;
//import  jp.gr.java_conf.mt777.zukei2d.Ten;
import java.math.BigDecimal;

public class OritaCalc {


    //d2s ダブルをストリングに変える　小数点2桁目で四捨五入("");ｄ２ｓ
    public String d2s(double d0) {
        BigDecimal bd = new BigDecimal(d0);

        //小数第2位で四捨五入
        BigDecimal bd1 = bd.setScale(1, BigDecimal.ROUND_HALF_UP);

        String sr = new String();
        sr = bd1.toString();
        return sr;
    }


    //ただのSystem.out.println("String");
    public void hyouji(String s0) {
        System.out.println(s0);
    }


    //直線t上の点pの影の位置（点pと最も近い直線t上の位置）を求める。
    public Point shadow_request(StraightLine t, Point p) {

        StraightLine t1 = new StraightLine();
        t1.set(t);
        t1.orthogonalize(p);//点p1を通って tに直行する直線u1を求める。
        return findIntersection(t, t1);
    }

    //点P0とP1を通る直線t上の点pの影の位置（点pと最も近い直線t上の位置）を求める。
    public  Point shadow_request(Point p0, Point p1, Point p) {
        StraightLine t = new StraightLine(p0, p1);
        return shadow_request(t, p);
    }

    //Find the position of the shadow of the point p on the straight line t including the line segment s0 (the position on the straight line t closest to the point p).
    public Point shadow_request(LineSegment s0, Point p) {
        return shadow_request(s0.getA(), s0.getB(), p);
    }


    //A function that determines whether two points are in the same position (true) or different (false) -------------------------------- -
    public boolean equal(Point p1, Point p2) {
        return equal(p1, p2, 0.1);//The error is defined here.
    }

    public boolean equal(Point p1, Point p2, double r) {//r is the error tolerance. Strict judgment if r is negative.

        //厳密に判定。
        if (r <= 0.0) {
            if ((p1.getX() == p2.getX()) && (p1.getY() == p2.getY())) {
                return true;
            }
        }
        //誤差を許容。
        if (r > 0) {
            if (distance(p1, p2) <= r) {
                return true;
            }
        }
        return false;
    }

    //２点間の距離（整数）を求める関数----------------------------------------------------
    public double distance(Point p0, Point p1) {
        return p0.distance(p1);
    }

    //A function that finds the angle between the vector ab and the x-axis by specifying a and b between two points. If a = b, return -10000.0 ----------------------------------------- -----------
    public double angle(Point a, Point b) {
        double ax, ay, bx, by, x, y, L, c, ret;
        ax = a.getX();
        ay = a.getY();
        bx = b.getX();
        by = b.getY();
        x = bx - ax;
        y = by - ay;
        L = Math.sqrt(x * x + y * y);
        if (L <= 0.0) {
            return -10000.0;
        }
        c = x / L;
        if (c > 1.0) {
            c = 1.0;
        }

        ret = Math.acos(c);
        if (y < 0.0) {
            ret = -ret;
        }
        ret = 180.0 * ret / Math.PI;
        if (ret < 0) {
            ret = ret + 360.0;
        }
        return ret;
    }


    //線分を指定して、ベクトルabとx軸のなす角度を求める関数。もしa=bなら-10000.0を返す----------------------------------------------------
    public double angle(LineSegment s) {
        return angle(s.getA(), s.getB());
    }

    //線分を指定して、ベクトルabとx軸のなす角度を求める関数。もしa=bなら-10000.0を返す----------------------------------------------------
    public double kakudozure(LineSegment s, double a) {
        double b;//実際の角度をaで割った時の剰余
        b = angle(s) % a;
        if (a - b < b) {
            b = a - b;
        }
        return b;
    }

    //A function that returns 2 if the point pa is in a rectangle containing two line segments that is orthogonal to the line segment ending at the two points p1 and p2 at the points p1 and p2.
    public int isInside(Point p1, Point pa, Point p2) {
        StraightLine t = new StraightLine(p1, p2);//p1,p2を通る直線tを求める。
        StraightLine u1 = new StraightLine(p1, p2);
        u1.orthogonalize(p1);//点p1を通って tに直行する直線u1を求める。
        StraightLine u2 = new StraightLine(p1, p2);
        u2.orthogonalize(p2);//点p2を通って tに直行する直線u2を求める。

        if (u1.dainyuukeisan(pa) * u2.dainyuukeisan(pa) == 0.0) {
            return 1;
        }
        if (u1.dainyuukeisan(pa) * u2.dainyuukeisan(pa) < 0.0) {
            return 2;
        }
        return 0;//箱の外部にある場合
    }


    //点paが、二点p1,p2を端点とする線分に点p1と点p2で直行する、2つの線分を含む長方形内にある場合は2を返す関数。これは 少しはみ出しても長方形内にあるとみなす。
    //具体的には線分の中に点があるかの判定の際、わずかに点が線分の外にある場合は、線分の中にあると、甘く判定する。描き職人で展開図を描くときは、この甘いほうを使わないとT字型の線分の交差分割に失敗する
    //しかし、なぜか、折り畳み推定にこの甘いほうを使うと無限ループになるようで、うまくいかない。この正確な解明は未解決20161105
    public int hakononaka_amai(Point p1, Point pa, Point p2) {
        StraightLine t = new StraightLine(p1, p2);//p1,p2を通る直線tを求める。
        StraightLine u1 = new StraightLine(p1, p2);
        u1.orthogonalize(p1);//点p1を通って tに直行する直線u1を求める。
        StraightLine u2 = new StraightLine(p1, p2);
        u2.orthogonalize(p2);//点p2を通って tに直行する直線u2を求める。

        //if(u1.dainyuukeisan(pa)*u2.dainyuukeisan(pa) ==0.0){return 1;}

        if (u1.calculateDistance(pa) < 0.00001) {
            return 1;
        }
        if (u2.calculateDistance(pa) < 0.00001) {
            return 1;
        }

        if (u1.dainyuukeisan(pa) * u2.dainyuukeisan(pa) < 0.0) {
            return 2;
        }
        return 0;//箱の外部にある場合
    }


    //点pが指定された線分とどの部所で近い(r以内)かどうかを判定する関数　---------------------------------
    //0=近くない、1=a点に近い、2=b点に近い、3=柄の部分に近い
    public int lineSegment_busyo_search(Point p, LineSegment s0, double r) {
        if (r > distance(p, s0.getA())) {
            return 1;
        }//a点に近いかどうか
        if (r > distance(p, s0.getB())) {
            return 2;
        }//b点に近いかどうか
        if (r > distance_lineSegment(p, s0)) {
            return 3;
        }//柄の部分に近いかどうか
        return 0;
    }


    //点p0と、二点p1,p2を両端とする線分との間の距離を求める関数----------------------------------------------------
    public double distance_lineSegment(Point p0, Point p1, Point p2) {
        // Ten p1 = new Ten();   p1.set(s.geta());
        // Ten p2 = new Ten();   p2.set(s.getb());

        //p1とp2が同じ場合
        if (distance(p1, p2) == 0.0) {
            return distance(p0, p1);
        }

        //p1とp2が異なる場合
        StraightLine t = new StraightLine(p1, p2);//p1,p2を通る直線tを求める。
        StraightLine u = new StraightLine(p1, p2);
        u.orthogonalize(p0);//点p0を通って tに直行する直線uを求める。

        if (isInside(p1, findIntersection(t, u), p2) >= 1) {
            return t.calculateDistance(p0);
        }//tとuの交点がp1とp2の間にある場合。
        return Math.min(distance(p0, p1), distance(p0, p2));//tとuの交点がp1とp2の間にない場合。
    }

    //点p0と、線分sとの間の距離を求める関数----------------------------------------------------
    public double distance_lineSegment(Point p0, LineSegment s) {
        Point p1 = new Point();
        p1.set(s.getA());
        Point p2 = new Point();
        p2.set(s.getB());
        return distance_lineSegment(p0, p1, p2);
    }

    // A function that determines whether two line segments intersect ---------------------------------- ------------------ ------------------
// 0 = Do not intersect,
// 1 = Two line segments are not parallel and intersect at one point in a crossroads shape,
// 2nd generation = Two line segments are not parallel and intersect in a T-junction or dogleg shape at one point
// 3 = Two line segments are parallel and intersect
// 4 = Line segment s1 and line segment s2 intersect at a point
// 5 = Line segment s1 intersects at a point
// 6 = Line segment s2 intersects at a point
    //Note! If p1 and p2 are the same, or p3 and p4 are the same, the result will be strange,
// This function itself does not have a check mechanism, so it may be difficult to notice.
    public int line_intersect_decide(LineSegment s1, LineSegment s2) {
        //return senbun_kousa_hantei( s1,s2,0.001,0.001) ;
        return line_intersect_decide(s1, s2, 0.01, 0.01);
    }


    public int line_intersect_decide_sweet(LineSegment s1, LineSegment s2) {
        return line_intersect_decide_sweet(s1, s2, 0.01, 0.01);
    }


    public int line_intersect_decide(LineSegment s1, LineSegment s2, double rhit, double rhei) {    //r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度
        double x1max = s1.getAx();
        double x1min = s1.getAx();
        double y1max = s1.getay();
        double y1min = s1.getay();
        if (x1max < s1.getbx()) {
            x1max = s1.getbx();
        }
        if (x1min > s1.getbx()) {
            x1min = s1.getbx();
        }
        if (y1max < s1.getby()) {
            y1max = s1.getby();
        }
        if (y1min > s1.getby()) {
            y1min = s1.getby();
        }
        double x2max = s2.getAx();
        double x2min = s2.getAx();
        double y2max = s2.getay();
        double y2min = s2.getay();
        if (x2max < s2.getbx()) {
            x2max = s2.getbx();
        }
        if (x2min > s2.getbx()) {
            x2min = s2.getbx();
        }
        if (y2max < s2.getby()) {
            y2max = s2.getby();
        }
        if (y2min > s2.getby()) {
            y2min = s2.getby();
        }

        if (x1max + rhit + 0.1 < x2min) {
            return 0;
        }
        if (x1min - rhit - 0.1 > x2max) {
            return 0;
        }
        if (y1max + rhit + 0.1 < y2min) {
            return 0;
        }
        if (y1min - rhit - 0.1 > y2max) {
            return 0;
        }

        //System.out.println("###########");

        Point p1 = new Point();
        p1.set(s1.getA());
        Point p2 = new Point();
        p2.set(s1.getB());
        Point p3 = new Point();
        p3.set(s2.getA());
        Point p4 = new Point();
        p4.set(s2.getB());

        StraightLine t1 = new StraightLine(p1, p2);
        StraightLine t2 = new StraightLine(p3, p4);
        //System.out.print("　　線分交差判定での平行判定の結果　＝　");
        //System.out.println (heikou_hantei(t1,t2,rhei));
        // heikou_hantei(t1,t2,rhei)

        //例外処理　線分s1と線分s2が点の場合
        if (((p1.getX() == p2.getX()) && (p1.getY() == p2.getY()))
                &&
                ((p3.getX() == p4.getX()) && (p3.getY() == p4.getY()))) {
            if ((p1.getX() == p3.getX()) && (p1.getY() == p3.getY())) {
                return 4;
            }
            return 0;
        }

        //例外処理　線分s1が点の場合
        if ((p1.getX() == p2.getX()) && (p1.getY() == p2.getY())) {
            if ((isInside(p3, p1, p4) >= 1) && (t2.dainyuukeisan(p1) == 0.0)) {
                return 5;
            }
            return 0;
        }

        //例外処理　線分s2が点の場合
        if ((p3.getX() == p4.getX()) && (p3.getY() == p4.getY())) {
            if ((isInside(p1, p3, p2) >= 1) && (t1.dainyuukeisan(p3) == 0.0)) {
                return 6;
            }
            return 0;
        }

        // System.out.println("AAAAAAAAAAAA");
        if (parallel_judgement(t1, t2, rhei) == 0) {    //２つの直線が平行でない
            Point pk = new Point();
            pk.set(findIntersection(t1, t2));    //<<<<<<<<<<<<<<<<<<<<<<<
            if ((isInside(p1, pk, p2) >= 1)
                    && (isInside(p3, pk, p4) >= 1)) {
                if (equal(p1, p3, rhit)) {
                    return 21;
                }//L字型
                if (equal(p1, p4, rhit)) {
                    return 22;
                }//L字型
                if (equal(p2, p3, rhit)) {
                    return 23;
                }//L字型
                if (equal(p2, p4, rhit)) {
                    return 24;
                }//L字型
                if (equal(p1, pk, rhit)) {
                    return 25;
                }//T字型 s1が縦棒
                if (equal(p2, pk, rhit)) {
                    return 26;
                }//T字型 s1が縦棒
                if (equal(p3, pk, rhit)) {
                    return 27;
                }//T字型 s2が縦棒
                if (equal(p4, pk, rhit)) {
                    return 28;
                }//T字型 s2が縦棒
                return 1;                    // <<<<<<<<<<<<<<<<< return 1;
            }
            return 0;
        }

        if (parallel_judgement(t1, t2, rhei) == 1) { //２つの直線が平行で、y切片は一致しない
            // System.out.println("BBBBBBBBBBB");
            return 0;
        }

        // ２つの線分が全く同じ
        if (equal(p1, p3, rhit) && equal(p2, p4, rhit)) {
            return 31;
        }
        if (equal(p1, p4, rhit) && equal(p2, p3, rhit)) {
            return 31;
        }

        // System.out.println("###########");

        //The two straight lines are parallel and the y-intercept matches
        if (parallel_judgement(t1, t2, rhei) == 2) {
            if (equal(p1, p3, rhit)) { //2つの線分の端点どうしが1点で重なる場合
                if (isInside(p1, p4, p2) == 2) {
                    return 321;
                }
                if (isInside(p3, p2, p4) == 2) {
                    return 322;
                }
                if (isInside(p2, p1, p4) == 2) {
                    return 323;
                }//2つの線分は1点で重なるだけで、それ以外では重ならない
            }

            if (equal(p1, p4, rhit)) {
                if (isInside(p1, p3, p2) == 2) {
                    return 331;
                }
                if (isInside(p4, p2, p3) == 2) {
                    return 332;
                }
                if (isInside(p2, p1, p3) == 2) {
                    return 333;
                }//2つの線分は1点で重なるだけで、それ以外では重ならない
            }

            if (equal(p2, p3, rhit)) {
                if (isInside(p2, p4, p1) == 2) {
                    return 341;
                }
                if (isInside(p3, p1, p4) == 2) {
                    return 342;
                }
                if (isInside(p1, p2, p4) == 2) {
                    return 343;
                }//2つの線分は1点で重なるだけで、それ以外では重ならない
            }

            if (equal(p2, p4, rhit)) {
                if (isInside(p2, p3, p1) == 2) {
                    return 351;
                }
                if (isInside(p4, p1, p3) == 2) {
                    return 352;
                }
                if (isInside(p1, p2, p3) == 2) {
                    return 353;
                }//2つの線分は1点で重なるだけで、それ以外では重ならない
            }

            //2つの線分の端点どうしが重ならない場合
            if ((isInside(p1, p3, p4) == 2) && (isInside(p3, p4, p2) == 2)) {
                return 361;
            }//線分(p1,p2)に線分(p3,p4)が含まれる
            if ((isInside(p1, p4, p3) == 2) && (isInside(p4, p3, p2) == 2)) {
                return 362;
            }//線分(p1,p2)に線分(p3,p4)が含まれる

            if ((isInside(p3, p1, p2) == 2) && (isInside(p1, p2, p4) == 2)) {
                return 363;
            }//線分(p3,p4)に線分(p1,p2)が含まれる
            if ((isInside(p3, p2, p1) == 2) && (isInside(p2, p1, p4) == 2)) {
                return 364;
            }//線分(p3,p4)に線分(p1,p2)が含まれる


            if ((isInside(p1, p3, p2) == 2) && (isInside(p3, p2, p4) == 2)) {
                return 371;
            }//線分(p1,p2)のP2側と線分(p3,p4)のP3側が部分的に重なる
            if ((isInside(p1, p4, p2) == 2) && (isInside(p4, p2, p3) == 2)) {
                return 372;
            }//線分(p1,p2)のP2側と線分(p4,p3)のP4側が部分的に重なる

            if ((isInside(p3, p1, p4) == 2) && (isInside(p1, p4, p2) == 2)) {
                return 373;
            }//線分(p3,p4)のP4側と線分(p1,p2)のP1側が部分的に重なる
            if ((isInside(p4, p1, p3) == 2) && (isInside(p1, p3, p2) == 2)) {
                return 374;
            }//線分(p4,p3)のP3側と線分(p1,p2)のP1側が部分的に重なる

            return 0;
        }
        return -1;//This passes in case of some error 。

    }


    // The sweet part of senbun_kousa_hantei_amai is that if ((hakononaka (p1, pk, p2)> = 1) && (hakononaka (p3, pk, p4)> = 1), which is the premise of return 21 to return 28. )) Instead of
    // (hakononaka_amai (p1, pk, p2)> = 1) && (hakononaka_amai (p3, pk, p4) is used. Hakononaka_amai is
    // A function that returns 2 if the point pa is in a rectangle containing two line segments that is orthogonal to the line segment with the two points p1 and p2 as the end points at the points p1 and p2. This is considered to be inside the rectangle even if it protrudes a little.
    // Specifically, when determining whether there is a point inside the line segment, if the point is slightly outside the line segment, it is judged to be sweet if it is inside the line segment. When drawing a development drawing with a drawing craftsman, if you do not use this sweet one, the intersection division of the T-shaped line segment will fail
    // But for some reason, using this sweeter one for folding estimation seems to result in an infinite loop, which doesn't work. This exact elucidation is unresolved 20161105

    public int line_intersect_decide_sweet(LineSegment s1, LineSegment s2, double rhit, double rhei) {    //r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度
        double x1max = s1.getAx();
        double x1min = s1.getAx();
        double y1max = s1.getay();
        double y1min = s1.getay();
        if (x1max < s1.getbx()) {
            x1max = s1.getbx();
        }
        if (x1min > s1.getbx()) {
            x1min = s1.getbx();
        }
        if (y1max < s1.getby()) {
            y1max = s1.getby();
        }
        if (y1min > s1.getby()) {
            y1min = s1.getby();
        }
        double x2max = s2.getAx();
        double x2min = s2.getAx();
        double y2max = s2.getay();
        double y2min = s2.getay();
        if (x2max < s2.getbx()) {
            x2max = s2.getbx();
        }
        if (x2min > s2.getbx()) {
            x2min = s2.getbx();
        }
        if (y2max < s2.getby()) {
            y2max = s2.getby();
        }
        if (y2min > s2.getby()) {
            y2min = s2.getby();
        }

        if (x1max + rhit + 0.1 < x2min) {
            return 0;
        }
        if (x1min - rhit - 0.1 > x2max) {
            return 0;
        }
        if (y1max + rhit + 0.1 < y2min) {
            return 0;
        }
        if (y1min - rhit - 0.1 > y2max) {
            return 0;
        }

        //System.out.println("###########");

        Point p1 = new Point();
        p1.set(s1.getA());
        Point p2 = new Point();
        p2.set(s1.getB());
        Point p3 = new Point();
        p3.set(s2.getA());
        Point p4 = new Point();
        p4.set(s2.getB());

        StraightLine t1 = new StraightLine(p1, p2);
        StraightLine t2 = new StraightLine(p3, p4);
        //System.out.print("　　線分交差判定での平行判定の結果　＝　");
        //System.out.println (heikou_hantei(t1,t2,rhei));
        // heikou_hantei(t1,t2,rhei)

        //例外処理　線分s1と線分s2が点の場合
        if (((p1.getX() == p2.getX()) && (p1.getY() == p2.getY()))
                &&
                ((p3.getX() == p4.getX()) && (p3.getY() == p4.getY()))) {
            if ((p1.getX() == p3.getX()) && (p1.getY() == p3.getY())) {
                return 4;
            }
            return 0;
        }

        //例外処理　線分s1が点の場合
        if ((p1.getX() == p2.getX()) && (p1.getY() == p2.getY())) {
            if ((isInside(p3, p1, p4) >= 1) && (t2.dainyuukeisan(p1) == 0.0)) {
                return 5;
            }
            return 0;
        }

        //例外処理　線分s2が点の場合
        if ((p3.getX() == p4.getX()) && (p3.getY() == p4.getY())) {
            if ((isInside(p1, p3, p2) >= 1) && (t1.dainyuukeisan(p3) == 0.0)) {
                return 6;
            }
            return 0;
        }

        // System.out.println("AAAAAAAAAAAA");
        if (parallel_judgement(t1, t2, rhei) == 0) {    //２つの直線が平行でない
            Point pk = new Point();
            pk.set(findIntersection(t1, t2));    //<<<<<<<<<<<<<<<<<<<<<<<
            if ((hakononaka_amai(p1, pk, p2) >= 1)
                    && (hakononaka_amai(p3, pk, p4) >= 1)) {
                if (equal(p1, p3, rhit)) {
                    return 21;
                }//L-shaped
                if (equal(p1, p4, rhit)) {
                    return 22;
                }//L字型
                if (equal(p2, p3, rhit)) {
                    return 23;
                }//L字型
                if (equal(p2, p4, rhit)) {
                    return 24;
                }//L字型
                if (equal(p1, pk, rhit)) {
                    return 25;
                }//T字型 s1が縦棒
                if (equal(p2, pk, rhit)) {
                    return 26;
                }//T字型 s1が縦棒
                if (equal(p3, pk, rhit)) {
                    return 27;
                }//T字型 s2が縦棒
                if (equal(p4, pk, rhit)) {
                    return 28;
                }//T字型 s2が縦棒
                return 1;
            }
            return 0;
        }

        if (parallel_judgement(t1, t2, rhei) == 1) { //２つの直線が平行で、y切片は一致しない
            // System.out.println("BBBBBBBBBBB");
            return 0;
        }

        // The two line segments are exactly the same
        if (equal(p1, p3, rhit) && equal(p2, p4, rhit)) {
            return 31;
        }
        if (equal(p1, p4, rhit) && equal(p2, p3, rhit)) {
            return 31;
        }

        // System.out.println("###########");

        //The two straight lines are parallel and the y-intercept matches
        if (parallel_judgement(t1, t2, rhei) == 2) {
            if (equal(p1, p3, rhit)) { //2つの線分の端点どうしが1点で重なる場合
                if (isInside(p1, p4, p2) == 2) {
                    return 321;
                }//長い線分に短い線分が含まれる
                if (isInside(p3, p2, p4) == 2) {
                    return 322;
                }//長い線分に短い線分が含まれる
                if (isInside(p2, p1, p4) == 2) {
                    return 323;
                }//2つの線分は1点で重なるだけで、それ以外では重ならない
            }

            if (equal(p1, p4, rhit)) {
                if (isInside(p1, p3, p2) == 2) {
                    return 331;
                }//長い線分に短い線分が含まれる
                if (isInside(p4, p2, p3) == 2) {
                    return 332;
                }//長い線分に短い線分が含まれる
                if (isInside(p2, p1, p3) == 2) {
                    return 333;
                }//2つの線分は1点で重なるだけで、それ以外では重ならない
            }

            if (equal(p2, p3, rhit)) {
                if (isInside(p2, p4, p1) == 2) {
                    return 341;
                }//長い線分に短い線分が含まれる
                if (isInside(p3, p1, p4) == 2) {
                    return 342;
                }//長い線分に短い線分が含まれる
                if (isInside(p1, p2, p4) == 2) {
                    return 343;
                }//2つの線分は1点で重なるだけで、それ以外では重ならない
            }

            if (equal(p2, p4, rhit)) {
                if (isInside(p2, p3, p1) == 2) {
                    return 351;
                }//A long line segment contains a short line segment
                if (isInside(p4, p1, p3) == 2) {
                    return 352;
                }//長い線分に短い線分が含まれる
                if (isInside(p1, p2, p3) == 2) {
                    return 353;
                }//2つの線分は1点で重なるだけで、それ以外では重ならない
            }

            //2つの線分の端点どうしが重ならない場合
            if ((isInside(p1, p3, p4) == 2) && (isInside(p3, p4, p2) == 2)) {
                return 361;
            }//線分(p1,p2)に線分(p3,p4)が含まれる
            if ((isInside(p1, p4, p3) == 2) && (isInside(p4, p3, p2) == 2)) {
                return 362;
            }//線分(p1,p2)に線分(p3,p4)が含まれる

            if ((isInside(p3, p1, p2) == 2) && (isInside(p1, p2, p4) == 2)) {
                return 363;
            }//線分(p3,p4)に線分(p1,p2)が含まれる
            if ((isInside(p3, p2, p1) == 2) && (isInside(p2, p1, p4) == 2)) {
                return 364;
            }//線分(p3,p4)に線分(p1,p2)が含まれる

            if ((isInside(p1, p3, p2) == 2) && (isInside(p3, p2, p4) == 2)) {
                return 371;
            }
            if ((isInside(p1, p4, p2) == 2) && (isInside(p4, p2, p3) == 2)) {
                return 372;
            }
            if ((isInside(p3, p1, p4) == 2) && (isInside(p1, p4, p2) == 2)) {
                return 373;
            }
            if ((isInside(p4, p1, p3) == 2) && (isInside(p1, p3, p2) == 2)) {
                return 374;
            }

            return 0;
        }
        return -1;//ここは何らかのエラーの時に通る。

    }


    //２つの直線が平行かどうかを判定する関数。
    public int parallel_judgement(StraightLine t1, StraightLine t2) {
        return parallel_judgement(t1, t2, 0.1);
    }

    //２つの線分が平行かどうかを判定する関数。
    public int parallel_judgement(LineSegment s1, LineSegment s2, double r) {
        return parallel_judgement(lineSegmentToStraightLine(s1), lineSegmentToStraightLine(s2), r);
    }


    public int parallel_judgement(StraightLine t1, StraightLine t2, double r) {//rは誤差の許容度。rが負なら厳密判定。
        //0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
        double a1 = t1.getA(), b1 = t1.getB(), c1 = t1.getC();//直線t1, a1*x+b1*y+c1=0の各係数を求める。
        double a2 = t2.getA(), b2 = t2.getB(), c2 = t2.getC();//直線t2, a2*x+b2*y+c2=0の各係数を求める。

        //System.out.print("平行判定のr　＝　");System.out.println(r);
        //厳密に判定----------------------------------------
        if (r <= 0.0) {
            //２直線が平行の場合
            if (a1 * b2 - a2 * b1 == 0) {
                //２直線は同一の場合
                if ((a1 * a1 + b1 * b1) * c2 * c2 == (a2 * a2 + b2 * b2) * c1 * c1) {
                    return 2;
                }//厳密に判定。
                //２直線が異なる場合
                else {
                    return 1;
                }
            }
        }

        //誤差を許容----------------------------------------
        if (r > 0) {
            //２直線が平行の場合
            if (Math.abs(a1 * b2 - a2 * b1) < r) {
                //２直線は同一の場合


                //原点（0、0）と各直線との距離を比較
                //double kyoriT=Math.abs(c1/Math.sqrt(a1*a1+b1*b1)-c2/Math.sqrt(a2*a2+b2*b2));//20181027、ver3.049までのバグありの処理
                //double kyoriT=Math.abs(   Math.abs(  c1/Math.sqrt(a1*a1+b1*b1)  )  -   Math.abs(  c2/Math.sqrt(a2*a2+b2*b2)  )      );//20181027、ver3.050以降のバグ無しの処理
                double kyoriT = t2.calculateDistance(t1.findShadow(new Point(0.0, 0.0)));//t1上の点とt2との距離//t1.kage_motome(new Ten(0.0,0.0))   は点（0,0）のt1上の影を求める（t1上の点ならなんでもいい）//20181115修正


                if (kyoriT < r) {//誤差を許容。
                    return 2;
                }
                //２直線が異なる場合
                else {
                    return 1;
                }
            }
        }

        //２直線が非平行の場合-------------------------------------------------
        return 0;
    }


    //Function to find the intersection of two straight lines
    public Point findIntersection(StraightLine t1, StraightLine t2) {
        double a1 = t1.getA(), b1 = t1.getB(), c1 = t1.getC();//直線t1, a1*x+b1*y+c1=0の各係数を求める。
        double a2 = t2.getA(), b2 = t2.getB(), c2 = t2.getC();//直線t2, a2*x+b2*y+c2=0の各係数を求める。

        return new Point((b1 * c2 - b2 * c1) / (a1 * b2 - a2 * b1), (a2 * c1 - a1 * c2) / (a1 * b2 - a2 * b1));
    }


    //Function to find the intersection of two straight lines (replication)
    public Point findIntersection_01(StraightLine t1, StraightLine t2) {
        double a1 = t1.getA(), b1 = t1.getB(), c1 = t1.getC();//直線t1, a1*x+b1*y+c1=0の各係数を求める。
        double a2 = t2.getA(), b2 = t2.getB(), c2 = t2.getC();//直線t2, a2*x+b2*y+c2=0の各係数を求める。
        return new Point((b1 * c2 - b2 * c1) / (a1 * b2 - a2 * b1), (a2 * c1 - a1 * c2) / (a1 * b2 - a2 * b1));
    }


    public StraightLine lineSegmentToStraightLine(LineSegment s) {//線分を含む直線を得る
        return new StraightLine(s.getA(), s.getB());
    }

    //２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
    public Point findIntersection(LineSegment s1, LineSegment s2) {
        return findIntersection(lineSegmentToStraightLine(s1), lineSegmentToStraightLine(s2));
    }

    //線分を直線とみなして他の直線との交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
    public Point findIntersection(StraightLine t1, LineSegment s2) {
        return findIntersection(t1, lineSegmentToStraightLine(s2));
    }

    //線分を直線とみなして他の直線との交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
    public Point findIntersection(LineSegment s1, StraightLine t2) {
        return findIntersection(lineSegmentToStraightLine(s1), t2);
    }


    //A function that moves a line segment in parallel to the side (returns a new line segment without changing the original line segment)
    public LineSegment moveParallel(LineSegment s, double d) {
        StraightLine t = new StraightLine(s.getA(), s.getB());
        StraightLine ta = new StraightLine(s.getA(), s.getB());
        StraightLine tb = new StraightLine(s.getA(), s.getB());
        ta.orthogonalize(s.getA());
        tb.orthogonalize(s.getB());
        StraightLine td = new StraightLine(s.getA(), s.getB());
        td.translate(d);

        return new LineSegment(findIntersection_01(ta, td), findIntersection_01(tb, td));
    }

    //------------------------------------
    //A function that returns a point obtained by rotating point b by d degrees around point a (returns a new point without changing the original point)
    public Point point_rotate(Point a, Point b, double d) {
        double Mcd = Math.cos(d * Math.PI / 180.0);
        double Msd = Math.sin(d * Math.PI / 180.0);

        double bx1 = Mcd * (b.getX() - a.getX()) - Msd * (b.getY() - a.getY()) + a.getX();
        double by1 = Msd * (b.getX() - a.getX()) + Mcd * (b.getY() - a.getY()) + a.getY();

        return new Point(bx1, by1);
    }

    //------------------------------------
    //A function that rotates point b by d degrees around point a and returns a point whose ab distance is r times (returns a new point without changing the original point)
    public Point point_rotate(Point a, Point b, double d, double r) {

        double Mcd = Math.cos(d * Math.PI / 180.0);
        double Msd = Math.sin(d * Math.PI / 180.0);

        double bx1 = r * (Mcd * (b.getX() - a.getX()) - Msd * (b.getY() - a.getY())) + a.getX();
        double by1 = r * (Msd * (b.getX() - a.getX()) + Mcd * (b.getY() - a.getY())) + a.getY();

        return new Point(bx1, by1);
    }

    //------------------------------------
    //A function that returns a point centered on point a and based on point b with a distance of ab times r (returns a new point without changing the original point) 20161224 Unverified
    public Point point_double(Point a, Point b, double r) {
        double bx1 = r * (b.getX() - a.getX()) + a.getX();
        double by1 = r * (b.getY() - a.getY()) + a.getY();

        return new Point(bx1, by1);
    }


//------------------------------------

    //線分abをcを中心にr倍してd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）
    public LineSegment lineSegment_rotate(LineSegment s0, Point c, double d, double r) {
        return new LineSegment(point_rotate(s0.getA(), c, d, r), point_rotate(s0.getB(), c, d, r));
    }


// ------------------------------------

    //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）
    public LineSegment lineSegment_rotate(LineSegment s0, double d) {
        double Mcd = Math.cos(d * Math.PI / 180.0);
        double Msd = Math.sin(d * Math.PI / 180.0);

        double bx1 = Mcd * (s0.getbx() - s0.getAx()) - Msd * (s0.getby() - s0.getay()) + s0.getAx();
        double by1 = Msd * (s0.getbx() - s0.getAx()) + Mcd * (s0.getby() - s0.getay()) + s0.getay();

        double ax1 = s0.getAx();
        double ay1 = s0.getay();

        return new LineSegment(ax1, ay1, bx1, by1);
    }


    //線分abをaを中心にr倍してd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）
    public LineSegment lineSegment_rotate(LineSegment s0, double d, double r) {
        double Mcd = Math.cos(d * Math.PI / 180.0);
        double Msd = Math.sin(d * Math.PI / 180.0);

        double bx1 = r * (Mcd * (s0.getbx() - s0.getAx()) - Msd * (s0.getby() - s0.getay())) + s0.getAx();
        double by1 = r * (Msd * (s0.getbx() - s0.getAx()) + Mcd * (s0.getby() - s0.getay())) + s0.getay();

        double ax1 = s0.getAx();
        double ay1 = s0.getay();

        return new LineSegment(ax1, ay1, bx1, by1);
    }


    //A function that returns a line segment obtained by multiplying the line segment ab by r with a as the center (returns a new line segment without changing the original line segment)
    public LineSegment lineSegment_double(LineSegment s0, double r) {

        double bx1 = r * (s0.getbx() - s0.getAx()) + s0.getAx();
        double by1 = r * (s0.getby() - s0.getay()) + s0.getay();

        double ax1 = s0.getAx();
        double ay1 = s0.getay();

        return new LineSegment(ax1, ay1, bx1, by1);
    }


    //線分Aの、線分Jを軸とした対照位置にある線分Bを求める関数
    public LineSegment sentaisyou_senbun_motome(LineSegment s0, LineSegment jiku) {
        Point p_a = new Point();
        p_a.set(s0.getA());
        Point p_b = new Point();
        p_b.set(s0.getB());
        Point jiku_a = new Point();
        jiku_a.set(jiku.getA());
        Point jiku_b = new Point();
        jiku_b.set(jiku.getB());

        LineSegment s1 = new LineSegment();
        s1.set(lineSymmetry_point_find(jiku_a, jiku_b, p_a), lineSymmetry_point_find(jiku_a, jiku_b, p_b));

        return s1;
    }


    //直線t0に関して、点pの対照位置にある点を求める関数
    public Point lineSymmetry_point_find(StraightLine t0, Point p) {
        Point p1 = new Point();  // p1.set(s.geta());
        Point p2 = new Point();  // p2.set(s.getb());

        StraightLine s1 = new StraightLine();
        s1.set(t0);
        StraightLine s2 = new StraightLine();
        s2.set(t0);

        s2.orthogonalize(p);//点pを通って s1に直行する直線s2を求める。

        p1 = findIntersection(s1, s2);
        p2.set(2.0 * p1.getX() - p.getX(), 2.0 * p1.getY() - p.getY());
        return p2;
    }

    //A function that finds a point at the control position of point p with respect to a straight line passing through two points t1 and t2.
    public Point lineSymmetry_point_find(Point t1, Point t2, Point p) {
        Point p1 = new Point();  // p1.set(s.geta());
        Point p2 = new Point();  // p2.set(s.getb());

        StraightLine s1 = new StraightLine(t1, t2);
        StraightLine s2 = new StraightLine(t1, t2);

        s2.orthogonalize(p);//Find the straight line s2 that passes through the point p and is orthogonal to s1.

        p1 = findIntersection(s1, s2);
        p2.set(2.0 * p1.getX() - p.getX(), 2.0 * p1.getY() - p.getY());
        return p2;
    }

    //角度を-180.0度より大きく180.0度以下に押さえる関数
    public double angle_between_m180_180(double kakudo) {
        while (kakudo <= -180.0) {
            kakudo = kakudo + 360.0;
        }
        while (kakudo > 180.0) {
            kakudo = kakudo - 360.0;
        }
        return kakudo;
    }

    //A function that keeps the angle between 0.0 degrees and 360.0 degrees
    public double angle_between_0_360(double kakudo) {
        while (kakudo < 0.0) {
            kakudo = kakudo + 360.0;
        }
        while (kakudo >= 360.0) {
            kakudo = kakudo - 360.0;
        }
        return kakudo;
    }


    //角度を0.0度以上kmax度未満に押さえる関数(円錐の頂点の伏見定理などで使う)
    public double angle_betwen_0_kmax(double kakudo, double kmax) {
        while (kakudo < 0.0) {
            kakudo = kakudo + kmax;
        }
        while (kakudo >= kmax) {
            kakudo = kakudo - kmax;
        }
        return kakudo;
    }


    //線分s1とs2のなす角度
    public double angle(LineSegment s1, LineSegment s2) {
        Point a = new Point();
        a.set(s1.getA());
        Point b = new Point();
        b.set(s1.getB());
        Point c = new Point();
        c.set(s2.getA());
        Point d = new Point();
        d.set(s2.getB());

        return angle_between_0_360(angle(c, d) - angle(a, b));
    }


    //ベクトルabとcdのなす角度
    public double angle(Point a, Point b, Point c, Point d) {
        return angle_between_0_360(angle(c, d) - angle(a, b));
    }

    /**
     * Find the inner heart of the triangle
     */
    public Point center(Point ta, Point tb, Point tc) {
        double A, B, C, XA, XB, XC, YA, YB, YC, XD, YD, XE, YE, G, H, K, L, P, Q, XN, YN;
        Point tn = new Point();
        XA = ta.getX();
        YA = ta.getY();
        XB = tb.getX();
        YB = tb.getY();
        XC = tc.getX();
        YC = tc.getY();

        A = Math.sqrt((XC - XB) * (XC - XB) + (YC - YB) * (YC - YB));
        B = Math.sqrt((XA - XC) * (XA - XC) + (YA - YC) * (YA - YC));
        C = Math.sqrt((XB - XA) * (XB - XA) + (YB - YA) * (YB - YA));
        XD = (C * XC + B * XB) / (B + C);
        YD = (C * YC + B * YB) / (B + C);
        XE = (C * XC + A * XA) / (A + C);
        YE = (C * YC + A * YA) / (A + C);
        G = XD - XA;
        H = YD - YA;
        K = XE - XB;
        L = YE - YB;
        P = G * YA - H * XA;
        Q = K * YB - L * XB;
        XN = (G * Q - K * P) / (H * K - G * L);
        YN = (L * P - H * Q) / (G * L - H * K);

        tn.set(XN, YN);

        return tn;
    }

    // -------------------------------
    //内分点を求める。
    public Point naibun(Point a, Point b, double d_naibun_s, double d_naibun_t) {
        Point r_point = new Point(-10000.0, -10000.0);
        if (distance(a, b) < 0.000001) {
            return r_point;
        }

        if ((d_naibun_s == 0.0) && (d_naibun_t == 0.0)) {
            return r_point;
        }
        if ((d_naibun_s == 0.0) && (d_naibun_t != 0.0)) {
            return a;
        }
        if ((d_naibun_s != 0.0) && (d_naibun_t == 0.0)) {
            return b;
        }
        if ((d_naibun_s != 0.0) && (d_naibun_t != 0.0)) {
            LineSegment s_ab = new LineSegment(a, b);
            double nx = (d_naibun_t * s_ab.getAx() + d_naibun_s * s_ab.getbx()) / (d_naibun_s + d_naibun_t);
            double ny = (d_naibun_t * s_ab.getay() + d_naibun_s * s_ab.getby()) / (d_naibun_s + d_naibun_t);
            r_point.set(nx, ny);
            return r_point;
        }
        return r_point;
    }

    /**
     * -------------------------------
     * Find the midpoint.
     */
    public Point midPoint(Point a, Point b) {

        return new Point((a.getX() + b.getX()) / 2.0, (a.getY() + b.getY()) / 2.0);
    }

    // -------------------------------
    public StraightLine circle_to_circle_no_intersection_wo_tooru_straightLine(Circle e1, Circle e2) {
        double x1 = e1.getx();
        double y1 = e1.gety();
        double r1 = e1.getRadius();
        double x2 = e2.getx();
        double y2 = e2.gety();
        double r2 = e2.getRadius();

        double a = 2.0 * x1 - 2.0 * x2;
        double b = 2.0 * y1 - 2.0 * y2;
        double c = x2 * x2 - x1 * x1 + y2 * y2 - y1 * y1 + r1 * r1 - r2 * r2;

        return new StraightLine(a, b, c);
    }

    // -------------------------------
    public LineSegment circle_to_circle_no_intersection_wo_musubu_lineSegment(Circle e1, Circle e2) {
        StraightLine t0 = new StraightLine();
        t0.set(circle_to_circle_no_intersection_wo_tooru_straightLine(e1, e2));
        StraightLine t1 = new StraightLine(e1.getCenter(), e2.getCenter());
        Point intersection_t0t1 = new Point();
        intersection_t0t1.set(findIntersection(t0, t1));
        double length_a = t0.calculateDistance(e1.getCenter());  //t0とt1の交点からe1の中心までの長さ

//double length_a=kyori(intersection_t0t1,e1.get_tyuusin());  //t0とt1の交点からe1の中心までの長さ
        double length_b = Math.sqrt(e1.getRadius() * e1.getRadius() - length_a * length_a); //t0とt1の交点からe1とe2の交点までの長さ
//t0と平行な方向ベクトルは(t0.getb() , -t0.geta())
//t0と平行な方向ベクトルで長さがnagasa_bのものは(t0.getb()*length_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() ) , -t0.geta()*length_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() ))

        return new LineSegment(
                intersection_t0t1.getX() + t0.getB() * length_b / Math.sqrt(t0.getB() * t0.getB() + t0.getA() * t0.getA()),
                intersection_t0t1.getY() - t0.getA() * length_b / Math.sqrt(t0.getB() * t0.getB() + t0.getA() * t0.getA()),
                intersection_t0t1.getX() - t0.getB() * length_b / Math.sqrt(t0.getB() * t0.getB() + t0.getA() * t0.getA()),
                intersection_t0t1.getY() + t0.getA() * length_b / Math.sqrt(t0.getB() * t0.getB() + t0.getA() * t0.getA())
        );
    }


    // --------qqqqqqqqqqqqqqq-----------------------
    public LineSegment circle_to_straightLine_no_kouten_wo_musubu_LineSegment(Circle e1, StraightLine t0) {

        Point kouten_t0t1 = new Point();
        kouten_t0t1.set(shadow_request(t0, e1.getCenter()));
        double nagasa_a = t0.calculateDistance(e1.getCenter());  //t0とt1の交点からe1の中心までの長さ

        double nagasa_b = Math.sqrt(e1.getRadius() * e1.getRadius() - nagasa_a * nagasa_a); //t0とt1の交点からe1とe2の交点までの長さ
//t0と平行な方向ベクトルは(t0.getb() , -t0.geta())
//t0と平行な方向ベクトルで長さがnagasa_bのものは(t0.getb()*nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() ) , -t0.geta()*nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() ))

        return new LineSegment(
                kouten_t0t1.getX() + t0.getB() * nagasa_b / Math.sqrt(t0.getB() * t0.getB() + t0.getA() * t0.getA()),
                kouten_t0t1.getY() - t0.getA() * nagasa_b / Math.sqrt(t0.getB() * t0.getB() + t0.getA() * t0.getA()),
                kouten_t0t1.getX() - t0.getB() * nagasa_b / Math.sqrt(t0.getB() * t0.getB() + t0.getA() * t0.getA()),
                kouten_t0t1.getY() + t0.getA() * nagasa_b / Math.sqrt(t0.getB() * t0.getB() + t0.getA() * t0.getA())
        );
    }

    // Function to find the distance between the point p0 and the circumference of the circle e0 ------------------------------- --------------------- ---------------------
    public double distance_circumference(Point p0, Circle e0) {
        return Math.abs(distance(p0, e0.getCenter()) - e0.getRadius());
    }

    //Minを返す関数
    public double min(double d1, double d2, double d3, double d4) {
        double min_d = d1;
        if (min_d > d2) {
            min_d = d2;
        }
        if (min_d > d3) {
            min_d = d3;
        }
        if (min_d > d4) {
            min_d = d4;
        }
        return min_d;
    }


    public LineSegment bisection(Point t1, Point t2, double d0) {
        Point tm = new Point((t1.getX() + t2.getX()) / 2.0, (t1.getY() + t2.getY()) / 2.0);

        double bai = d0 / distance(t1, t2);

        LineSegment s1 = new LineSegment();
        s1.set(lineSegment_rotate(new LineSegment(tm, t1), 90.0, bai));
        LineSegment s2 = new LineSegment();
        s2.set(lineSegment_rotate(new LineSegment(tm, t2), 90.0, bai));

        return new LineSegment(s1.getB(), s2.getB());
    }


    //--------------------------------------------------------
    public int LineSegment_overlapping_decide(LineSegment s1, LineSegment s2) {//0は重ならない。1は重なる。20201012追加

        int i_senbun_kousa_hantei = line_intersect_decide(s1, s2, 0.0001, 0.0001);
        int i_jikkou = 0;
        if (i_senbun_kousa_hantei == 31) {
            i_jikkou = 1;
        }
        if (i_senbun_kousa_hantei == 321) {
            i_jikkou = 1;
        }
        if (i_senbun_kousa_hantei == 322) {
            i_jikkou = 1;
        }
        if (i_senbun_kousa_hantei == 331) {
            i_jikkou = 1;
        }
        if (i_senbun_kousa_hantei == 332) {
            i_jikkou = 1;
        }
        if (i_senbun_kousa_hantei == 341) {
            i_jikkou = 1;
        }
        if (i_senbun_kousa_hantei == 342) {
            i_jikkou = 1;
        }
        if (i_senbun_kousa_hantei == 351) {
            i_jikkou = 1;
        }
        if (i_senbun_kousa_hantei == 352) {
            i_jikkou = 1;
        }

        if (i_senbun_kousa_hantei == 361) {
            i_jikkou = 1;
        }
        if (i_senbun_kousa_hantei == 362) {
            i_jikkou = 1;
        }
        if (i_senbun_kousa_hantei == 363) {
            i_jikkou = 1;
        }
        if (i_senbun_kousa_hantei == 364) {
            i_jikkou = 1;
        }
        if (i_senbun_kousa_hantei == 371) {
            i_jikkou = 1;
        }
        if (i_senbun_kousa_hantei == 372) {
            i_jikkou = 1;
        }
        if (i_senbun_kousa_hantei == 373) {
            i_jikkou = 1;
        }
        if (i_senbun_kousa_hantei == 374) {
            i_jikkou = 1;
        }
        return i_jikkou;
    }

    //--------------------------------------------------------
    public int Senbun_X_kousa_hantei(LineSegment s1, LineSegment s2) {//0はX交差しない。1は交差する。20201017追加

        int i_senbun_kousa_hantei = line_intersect_decide(s1, s2, 0.0001, 0.0001);
        int i_jikkou = 0;
        if (i_senbun_kousa_hantei == 1) {
            i_jikkou = 1;
        }

        return i_jikkou;
    }


//--------------------------------------------------------


}
