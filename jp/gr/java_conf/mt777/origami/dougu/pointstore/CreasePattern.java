package jp.gr.java_conf.mt777.origami.dougu.pointstore;

import jp.gr.java_conf.mt777.zukei2d.ten.*;
import jp.gr.java_conf.mt777.zukei2d.senbun.*;
import jp.gr.java_conf.mt777.zukei2d.oritacalc.*;
import jp.gr.java_conf.mt777.zukei2d.takakukei.*;
import jp.gr.java_conf.mt777.origami.dougu.bou.*;
import jp.gr.java_conf.mt777.origami.dougu.men.*;
import jp.gr.java_conf.mt777.kiroku.memo.*;

import java.util.*;

// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------

public class CreasePattern {

    int numFaces_temp;

    int pointsTotal;               //実際に使う点の総数
    int sticksTotal;               //実際に使う棒の総数
    int facesTotal;               //実際に使う面の総数
    Point_p[] points;//点のインスタンス化
    Stick[] sticks;//棒のインスタンス化
    int[] Stick_moti_FaceId_min;
    int[] Stick_moti_FaceId_max;

    Face[] faces;//Face instantiation

    double[] Stick_x_max;
    double[] Stick_x_min;
    double[] Stick_y_max;
    double[] Stick_y_min;

    double[] Surface_x_max;
    double[] Surface_x_min;
    double[] Surface_y_max;
    double[] Surface_y_min;

    OritaCalc oc = new OritaCalc();          //Instantiation of classes to use functions for various calculations
    ArrayList<Integer>[] point_linking;//t_renketu[i][j]はt[i]に連決しているPointの番号。t[0]には、Temの数を格納。

    int[][] Face_adjacent;//Face_adjacent [i] [j] is the Stick number at the boundary between m [i] and m [j]. Stores 0 when m [i] and m [j] are not adjacent.

    public CreasePattern() {
        reset();
    } //コンストラクタ

    //---------------------------------------
    public void reset() {
        pointsTotal = 0;
        sticksTotal = 0;
        facesTotal = 0;
    }

    //---------------------------------------
    public void configure(int numPoints, int numSticks, int numFaces) { //Make sure it passes at the beginning and after a reset.

        numFaces_temp = numFaces;

        points = new Point_p[numPoints + 1];
        point_linking = new ArrayList[numPoints + 1];

        point_linking[0] = new ArrayList<>();
        for (int i = 0; i <= numPoints; i++) {
            point_linking[i] = new ArrayList<>();
            point_linking[i].add(0);
        }

        for (int i = 0; i <= numPoints; i++) {
            points[i] = new Point_p();
            setPointLinking(i, 0, 0);
        }

        sticks = new Stick[numSticks + 1];
        int[] BMmin = new int[numSticks + 1];
        int[] BMmax = new int[numSticks + 1];
        Stick_moti_FaceId_min = BMmin;
        Stick_moti_FaceId_max = BMmax;
        for (int i = 0; i <= numSticks; i++) {
            sticks[i] = new Stick();
            Stick_moti_FaceId_min[i] = 0;
            Stick_moti_FaceId_max[i] = 0;
        }

        faces = new Face[numFaces + 1];

        Face_adjacent = new int[numFaces + 1][numFaces + 1];

        for (int i = 0; i <= numFaces; i++) {
            faces[i] = new Face();
            for (int j = 0; j <= numFaces; j++) {
                Face_adjacent[i][j] = 0;
            }

        }

        double[] Bxmax = new double[numSticks + 1];
        double[] Bxmin = new double[numSticks + 1];
        double[] Bymax = new double[numSticks + 1];
        double[] Bymin = new double[numSticks + 1];

        double[] Mxmax = new double[numFaces + 1];
        double[] Mxmin = new double[numFaces + 1];
        double[] Mymax = new double[numFaces + 1];
        double[] Mymin = new double[numFaces + 1];

        Stick_x_max = Bxmax;
        Stick_x_min = Bxmin;
        Stick_y_max = Bymax;
        Stick_y_min = Bymin;

        Surface_x_max = Mxmax;
        Surface_x_min = Mxmin;
        Surface_y_max = Mymax;
        Surface_y_min = Mymin;
    }

    //---------------
    private int getPointLinking(int i, int j) {
        return point_linking[i].get(j);
    }

    private void setPointLinking(int i, int j, int tid) {
        if (j + 1 > point_linking[i].size()) {
            while (j + 1 > point_linking[i].size()) {
                point_linking[i].add(0);
            }
        }//It won't work without this sentence. I don't know exactly why it should be this sentence.
        point_linking[i].set(j, tid);
    }

    //------------------------------
    private double getAverage_x() {
        double x = 0.0;
        for (int i = 1; i <= pointsTotal; i++) {
            x = x + points[i].getX();
        }
        return x / ((double) pointsTotal);
    }

    private double getAverage_y() {
        double y = 0.0;
        for (int i = 1; i <= pointsTotal; i++) {
            y = y + points[i].getY();
        }
        return y / ((double) pointsTotal);
    }

    //
    public void turnOver() {//Turn it over to the left and right around the position of the center of gravity.
        double xh;
        int icol;
        xh = getAverage_x();
        for (int i = 1; i <= pointsTotal; i++) {
            points[i].setX(2.0 * xh - points[i].getX());
        }
        for (int i = 1; i <= sticksTotal; i++) {
            icol = sticks[i].getColor();
            if (icol == 1) {
                sticks[i].setColor(2);
            }
            if (icol == 2) {
                sticks[i].setColor(1);
            }
        }

    }

    public void parallelMove(double x, double y) {
        for (int i = 0; i <= pointsTotal; i++) {
            points[i].parallel_move(x, y);
        }
    }

    public void set(CreasePattern ts) {
        pointsTotal = ts.getPointsTotal();
        sticksTotal = ts.getSticksTotal();
        facesTotal = ts.getFacesTotal();
        for (int i = 0; i <= pointsTotal; i++) {
            points[i].set(ts.getPoint(i));                                                         //  <<<-------
            for (int j = 1; j <= ts.getPointLinking(i, 0); j++) {
                setPointLinking(i, j, ts.getPointLinking(i, j));
            }
        }
        for (int i = 0; i <= sticksTotal; i++) {
            sticks[i].set(ts.getStick(i));
            Stick_moti_FaceId_min[i] = ts.get_Stick_moti_Menid_min(i);
            Stick_moti_FaceId_max[i] = ts.get_Stick_moti_Menid_max(i);
        }
        for (int i = 0; i <= facesTotal; i++) {
            faces[i] = new Face(ts.getFace(i));
            for (int j = 0; j <= facesTotal; j++) {
                Face_adjacent[i][j] = ts.getFaceAdjecent(i, j);
            }
        }
    }

    public void set(int i, Point tn) {
        points[i].set(tn);
    }                                               //  <<<-------

    private int getFaceAdjecent(int i, int j) {
        return Face_adjacent[i][j];
    }

    //
    private int get_Stick_moti_Menid_min(int i) {
        return Stick_moti_FaceId_min[i];
    }

    //
    private int get_Stick_moti_Menid_max(int i) {
        return Stick_moti_FaceId_max[i];
    }

    //
    private double get_Stick_x_max(int i) {
        return Stick_x_max[i];
    }

    //
    private double get_Stick_x_min(int i) {
        return Stick_x_min[i];
    }

    //
    private double get_Stick_y_max(int i) {
        return Stick_y_max[i];
    }

    //
    private double get_Stick_y_min(int i) {
        return Stick_y_min[i];
    }

    //
    private double get_Surface_x_max(int i) {
        return Surface_x_max[i];
    }

    //
    private double get_Surface_x_min(int i) {
        return Surface_x_min[i];
    }

    //
    private double get_Surface_y_max(int i) {
        return Surface_y_max[i];
    }

    //
    private double get_Surface_y_min(int i) {
        return Surface_y_min[i];
    }

    //Determine if the point is inside a face. 0 is not inside, 1 is on the border, 2 is inside
    public int simple_inside(Point p, int n) {      //0=外部、　1=境界、　2=内部
        //System.out.println("2016");
        if (p.getX() + 0.5 < Surface_x_min[n]) {
            return 0;
        }
        if (p.getX() - 0.5 > Surface_x_max[n]) {
            return 0;
        }
        if (p.getY() + 0.5 < Surface_y_min[n]) {
            return 0;
        }
        if (p.getY() - 0.5 > Surface_y_max[n]) {
            return 0;
        }
        return inside(p, faces[n]);
    }

    //点が面の内部にあるかどうかを判定する。
    public int inside(Point p, int n) {      //0=外部、　1=境界、　2=内部
        return inside(p, faces[n]);
    }

    //点が面の内部にあるかどうかを判定する。0なら内部にない、1なら境界線上、2なら内部
    private int inside(Point p, Face mn) {      //0=外部、　1=境界、　2=内部
        Polygon tk;
        tk = makePolygon(mn);
        return tk.inside(p);
    }

    //Determine which surface the point is inside. If it is 0, it is not inside any surface, if it is negative, it is on the boundary line, and if it is a positive number, it is inside. If there are multiple applicable surface numbers, the one with the smaller number is returned.
    public int inside(Point p) {
        for (int i = 1; i <= getFacesTotal(); i++) {
            if (inside(p, i) == 2) {
                return i;
            }
            if (inside(p, i) == 1) {
                return -i;
            }
        }
        return 0;
    }


    //Men を多角形にする
    private Polygon makePolygon(Face mn) {
        Polygon tk = new Polygon(mn.getPointsCount());
        tk.setVerticesCount(mn.getPointsCount());
        for (int i = 0; i <= mn.getPointsCount(); i++) {
            tk.set(i, points[mn.getPointId(i)]);
        }
        return tk;
    }

    // Even a part of the line segment s0 is inside the surface of the convex polygon (the boundary line is not regarded as the inside)
    // Returns 1 if it exists, 0 otherwise. If the surface is a concave polygon, the result will be strange, so do not use it.
    public int simple_convex_inside(int ib, int im) {
        //バグがあるようだったが，多分取り除けた
        if (Stick_x_max[ib] + 0.5 < Surface_x_min[im]) {
            return 0;
        }
        if (Stick_x_min[ib] - 0.5 > Surface_x_max[im]) {
            return 0;
        }
        if (Stick_y_max[ib] + 0.5 < Surface_y_min[im]) {
            return 0;
        }
        if (Stick_y_min[ib] - 0.5 > Surface_y_max[im]) {
            return 0;
        }

        return convex_inside(new LineSegment(points[sticks[ib].getBegin()], points[sticks[ib].getEnd()]), faces[im]);
    }

    private int convex_inside(LineSegment s0, Face mn) {
        Polygon tk;//=new Takakukei();
        tk = makePolygon(mn);
        return tk.convex_inside(s0);
    }

    private int convex_inside(int ib, int im) {
        return convex_inside(new LineSegment(points[sticks[ib].getBegin()], points[sticks[ib].getEnd()]), faces[im]);
    }

    public int convex_inside(double d, int ib, int im) {
        LineSegment sn = new LineSegment(points[sticks[ib].getBegin()], points[sticks[ib].getEnd()]);
        return convex_inside(oc.moveParallel(sn, d), faces[im]);
    }

    private int simple_convex_inside(double d, int ib, int im) {
        LineSegment sn = new LineSegment(points[sticks[ib].getBegin()], points[sticks[ib].getEnd()]);
        LineSegment snm = oc.moveParallel(sn, d);
        double s_x_max = snm.getAx();
        double s_x_min = snm.getAx();
        double s_y_max = snm.getay();
        double s_y_min = snm.getay();
        if (s_x_max < snm.getbx()) {
            s_x_max = snm.getbx();
        }
        if (s_x_min > snm.getbx()) {
            s_x_min = snm.getbx();
        }
        if (s_y_max < snm.getby()) {
            s_y_max = snm.getby();
        }
        if (s_y_min > snm.getby()) {
            s_y_min = snm.getby();
        }

        if (s_x_max + 0.5 < Surface_x_min[im]) {
            return 0;
        }
        if (s_x_min - 0.5 > Surface_x_max[im]) {
            return 0;
        }
        if (s_y_max + 0.5 < Surface_y_min[im]) {
            return 0;
        }
        if (s_y_min - 0.5 > Surface_y_max[im]) {
            return 0;
        }

        return convex_inside(snm, faces[im]);
    }


    //棒を線分にする
    private LineSegment stickToLineSegment(Stick stick) {
        return new LineSegment(points[stick.getBegin()], points[stick.getEnd()]);
    }

    //Returns 1 if two Sticks are parallel and partially or wholly overlap, otherwise 0. If one point overlaps, 0 is returned.
    public int parallel_overlap(int ib1, int ib2) {
        int skh;
        skh = oc.line_intersect_decide(stickToLineSegment(sticks[ib1]), stickToLineSegment(sticks[ib2]));
        if (skh == 31) {
            return 1;
        }
        if (skh == 321) {
            return 1;
        }
        if (skh == 322) {
            return 1;
        }
        if (skh == 331) {
            return 1;
        }
        if (skh == 332) {
            return 1;
        }
        if (skh == 341) {
            return 1;
        }
        if (skh == 342) {
            return 1;
        }
        if (skh == 351) {
            return 1;
        }
        if (skh == 352) {
            return 1;
        }

        if (skh == 361) {
            return 1;
        }
        if (skh == 362) {
            return 1;
        }
        if (skh == 363) {
            return 1;
        }
        if (skh == 364) {
            return 1;
        }

        if (skh == 371) {
            return 1;
        }
        if (skh == 372) {
            return 1;
        }
        if (skh == 373) {
            return 1;
        }
        if (skh == 374) {
            return 1;
        }

        return 0;
    }


    //面の内部の点を求める
    public Point insidePoint_surface(int n) {
        return insidePoint_surface(faces[n]);
    }

    //面の内部の点を求める
    private Point insidePoint_surface(Face mn) {
        Polygon tk;
        tk = makePolygon(mn);
        return tk.insidePoint_find();
    }

    //面積求める
    public double calculateArea(int n) {
        return calculateArea(faces[n]);
    }


    private double calculateArea(Face mn) {
        Polygon tk;
        tk = makePolygon(mn);
        return tk.menseki_motome();
    }

    public int getPointsTotal() {
        return pointsTotal;
    }   //点の総数を得る

    public int getSticksTotal() {
        return sticksTotal;
    }   //棒の総数を得る

    public int getFacesTotal() {
        return facesTotal;
    }   //面の総数を得る

    public int getPointId(int i, int j) {
        return faces[i].getPointId(j);
    }  // void setTensuu(int i){Tensuu=i;}

    // void setBousuu(int i){Bousuu=i;}
    public double getPointX(int i) {
        return points[i].getX();
    }

    public double getPointY(int i) {
        return points[i].getY();
    }

    public Point getPoint(int i) {
        return points[i];
    }   //点を得る       <<<------------tは、スーパークラスのTenのサブクラスTen_Pクラスのオブジェクト。スーパークラスの変数にサブクラスのオブジェクトを代入可能なので、このまま使う。

    private Stick getStick(int i) {
        return sticks[i];
    }   //棒を得る

    public Point getBeginPointFromStickId(int i) {
        return points[getBegin(i)];
    }    //棒のidから前点を得る              <<<------------　　同上

    public Point getEndPointFromStickId(int i) {
        return points[getEnd(i)];
    }    //棒のidから後点を得る              <<<------------　　同上


    public LineSegment getLineSegmentFromStickId(int i) {
        return stickToLineSegment(getStick(i));
    }    //棒のidからSenbunを得る

    private Face getFace(int i) {
        return faces[i];
    }   //面を得る

    public int getBegin(int i) {
        return sticks[i].getBegin();
    } //棒のidから前点のidを得る

    public int getEnd(int i) {
        return sticks[i].getEnd();
    } //棒のidから後点のidを得る

    public double getBeginX(int i) {
        return points[sticks[i].getBegin()].getX();
    }

    public double getBeginY(int i) {
        return points[sticks[i].getBegin()].getY();
    }

    public double getEndX(int i) {
        return points[sticks[i].getEnd()].getX();
    }

    public double getEndY(int i) {
        return points[sticks[i].getEnd()].getY();
    }

    public int getPointsCount(int i) {
        return faces[i].getPointsCount();
    }

    public void setPoint(int i, Point tn) {
        points[i].set(tn);
    }                                                        //   <<<------------

    private void setPoint(int i, double x, double y) {
        points[i].set(x, y);
    }

    public void addPoint(double x, double y) {
        pointsTotal = pointsTotal + 1;
        points[pointsTotal].set(x, y);
    }   //点を加える

    public void addStick(int i, int j, int icol) {
        sticksTotal = sticksTotal + 1;
        sticks[sticksTotal].set(i, j, icol);
    }   //棒を加える

    //i番目の棒の色を入出力する
    private void setColor(int i, int icol) {
        sticks[i].setColor(icol);
    }

    public int getColor(int i) {
        return sticks[i].getColor();
    }

    private void t_renketu_sakusei() {
        for (int k = 1; k <= sticksTotal; k++) {
            setPointLinking(sticks[k].getBegin(), 0, getPointLinking(sticks[k].getBegin(), 0) + 1);
            setPointLinking(sticks[k].getBegin(), getPointLinking(sticks[k].getBegin(), 0), sticks[k].getEnd());
            setPointLinking(sticks[k].getEnd(), 0, getPointLinking(sticks[k].getEnd(), 0) + 1);
            setPointLinking(sticks[k].getEnd(), getPointLinking(sticks[k].getEnd(), 0), sticks[k].getBegin());
        }
    }

    //点iと点jが棒で連結していれば1、していなければ0を返す。
    private int renketu_hantei(int i, int j) {
        for (int k = 1; k <= sticksTotal; k++) {
            if (
                    ((sticks[k].getBegin() == i) && (sticks[k].getEnd() == j))
                            ||
                            ((sticks[k].getBegin() == j) && (sticks[k].getEnd() == i))
            ) {
                return 1;
            }
        }
        return 0;
    }

    //Find the number of the point when going from point i to point j and then going from point j to the right side of point i.
    private int getRPoint(int i, int j) {
        int n = 0;
        double angle = 876.0;   //Keep angle in a large number

        int iflg = 0;
        for (int k = 1; k <= getPointLinking(i, 0); k++) {
            if (getPointLinking(i, k) == j) {
                iflg = 1;
            }
        }

        if (iflg == 0) {
            return 0;
        }//点iと点jが連結していない時は0を返す

        for (int ik = 1; ik <= getPointLinking(j, 0); ik++) {
            int k;
            k = getPointLinking(j, ik);
            if (k != i) {
                if (oc.angle(points[j], points[i], points[j], points[k]) <= angle) {
                    n = k;
                    angle = oc.angle(points[j], points[i], points[j], points[k]);
                }
            }
        }
        return n; //点jに連結している点が点iしかない時は0を返す
    }
    //--------------------------------

    private Face Face_request(int i, int j) {//Find the surface by following the bar on the right side for the first time from the i-th point and the j-th point.
        Face tempFace = new Face();
        //tempFace.reset();
        tempFace.addPointId(i);
        tempFace.addPointId(j);
        int nextT = 0;

        nextT = getRPoint(tempFace.getPointId(1), tempFace.getPointId(2));
        while (nextT != tempFace.getPointId(1)) {
            if (nextT == 0) {
                tempFace.reset();
                return tempFace;
            }//エラー時の対応
            tempFace.addPointId(nextT);
            nextT = getRPoint(tempFace.getPointId(tempFace.getPointsCount() - 1), tempFace.getPointId(tempFace.getPointsCount()));
        }
        tempFace.align();
        return tempFace;
    }

    //-------------------------------------
    public void FaceOccurrence() {
        int flag1;
        Face tempFace = new Face();
        facesTotal = 0;
        t_renketu_sakusei();

        for (int i = 1; i <= sticksTotal; i++) {
            //System.out.print("面発生　＝　"+i+"    ");System.out.println(Mensuu);

            //
            tempFace = Face_request(sticks[i].getBegin(), sticks[i].getEnd());
            flag1 = 0;   //　0なら面を追加する。1なら　面を追加しない。
            for (int j = 1; j <= facesTotal; j++) {
                if (onaji_ka_hantei(tempFace, faces[j]) == 1) {
                    flag1 = 1;
                    break;
                }
            }

            if (((flag1 == 0) && (tempFace.getPointsCount() != 0)) &&
                    (calculateArea(tempFace) > 0.0)) {
                addFace(tempFace);
            }
            //

            tempFace = Face_request(sticks[i].getEnd(), sticks[i].getBegin());
            flag1 = 0;   //　0なら面を追加する。1なら　面を追加しない。
            for (int j = 1; j <= facesTotal; j++) {
                if (onaji_ka_hantei(tempFace, faces[j]) == 1) {
                    flag1 = 1;
                    break;
                }
            }

            if (((flag1 == 0) && (tempFace.getPointsCount() != 0)) && (calculateArea(tempFace) > 0.0)) {
                //System.out.println("面発生ループ内　003");
                addFace(tempFace);
                //System.out.println("面発生ループ内　004");
            }
        }

        System.out.print("全面数　＝　");
        System.out.println(facesTotal);
        Face_adjecent_create();

        //Bouの両側の面の登録
        for (int ib = 1; ib <= sticksTotal; ib++) {

            Stick_moti_FaceId_min[ib] = Stick_moti_Menid_min_search(ib);
            Stick_moti_FaceId_max[ib] = Stick_moti_Menid_max_search(ib);
        }
    }

    //BouやMenの座標の最大値、最小値を求める。kantan_totu_naibu関数にのみ用いる。kantan_totu_naibu関数を使うなら折り畳み推定毎にやる必要あり。
    public void BouMenMaxMinZahyou() {
        //Bouの座標の最大最小を求める（これはここでやるより、Bouが加えられた直後にやるほうがよいかも知れない。）
        for (int ib = 1; ib <= sticksTotal; ib++) {

            Stick_x_max[ib] = points[sticks[ib].getBegin()].getX();
            Stick_x_min[ib] = points[sticks[ib].getBegin()].getX();
            Stick_y_max[ib] = points[sticks[ib].getBegin()].getY();
            Stick_y_min[ib] = points[sticks[ib].getBegin()].getY();

            if (Stick_x_max[ib] < points[sticks[ib].getEnd()].getX()) {
                Stick_x_max[ib] = points[sticks[ib].getEnd()].getX();
            }
            if (Stick_x_min[ib] > points[sticks[ib].getEnd()].getX()) {
                Stick_x_min[ib] = points[sticks[ib].getEnd()].getX();
            }
            if (Stick_y_max[ib] < points[sticks[ib].getEnd()].getY()) {
                Stick_y_max[ib] = points[sticks[ib].getEnd()].getY();
            }
            if (Stick_y_min[ib] > points[sticks[ib].getEnd()].getY()) {
                Stick_y_min[ib] = points[sticks[ib].getEnd()].getY();
            }
            MenMaxMinZahyou();
        }
    }

    private void MenMaxMinZahyou() {
        //Menの座標の最大最小を求める
        for (int im = 1; im <= facesTotal; im++) {
            Surface_x_max[im] = points[faces[im].getPointId(1)].getX();
            Surface_x_min[im] = points[faces[im].getPointId(1)].getX();
            Surface_y_max[im] = points[faces[im].getPointId(1)].getY();
            Surface_y_min[im] = points[faces[im].getPointId(1)].getY();
            for (int i = 2; i <= faces[im].getPointsCount(); i++) {
                if (Surface_x_max[im] < points[faces[im].getPointId(i)].getX()) {
                    Surface_x_max[im] = points[faces[im].getPointId(i)].getX();
                }
                if (Surface_x_min[im] > points[faces[im].getPointId(i)].getX()) {
                    Surface_x_min[im] = points[faces[im].getPointId(i)].getX();
                }
                if (Surface_y_max[im] < points[faces[im].getPointId(i)].getY()) {
                    Surface_y_max[im] = points[faces[im].getPointId(i)].getY();
                }
                if (Surface_y_min[im] > points[faces[im].getPointId(i)].getY()) {
                    Surface_y_min[im] = points[faces[im].getPointId(i)].getY();
                }
            }
        }
    }


    public Point getFaceUpperRightPoint(int im) {
        //im is the surface number. upperRight Returns the upper right vertex of the smallest rectangle containing the face with the specified number. Used to specify the position of the inside-out view of the folded-up view.
        //Find the maximum and minimum of Face's coordinates

        double x_max = points[faces[im].getPointId(1)].getX();
        double x_min = points[faces[im].getPointId(1)].getX();
        double y_max = points[faces[im].getPointId(1)].getY();
        double y_min = points[faces[im].getPointId(1)].getY();
        for (int i = 2; i <= faces[im].getPointsCount(); i++) {
            if (x_max < points[faces[im].getPointId(i)].getX()) {
                x_max = points[faces[im].getPointId(i)].getX();
            }
            if (x_min > points[faces[im].getPointId(i)].getX()) {
                x_min = points[faces[im].getPointId(i)].getX();
            }
            if (y_max < points[faces[im].getPointId(i)].getY()) {
                y_max = points[faces[im].getPointId(i)].getY();
            }
            if (y_min > points[faces[im].getPointId(i)].getY()) {
                y_min = points[faces[im].getPointId(i)].getY();
            }
        }

        return new Point(x_max, y_min);

    }


    //--------------
    //棒ibを境界として含む面(最大で2面ある)のうちでMenidの小さいほうのMenidを返す。棒を境界として含む面が無い場合は0を返す
    private int Stick_moti_Menid_min_search(int ib) {
        for (int im = 1; im <= facesTotal; im++) {
            if (Stick_moti_determine(im, ib) == 1) {
                return im;
            }
        }
        return 0;
    }

    //棒ibを境界として含む面(最大で2面ある)のうちでMenidの大きいほうのMenidを返す。棒を境界として含む面が無い場合は0を返す
    private int Stick_moti_Menid_max_search(int ib) {
        for (int im = facesTotal; im >= 1; im--) {
            if (Stick_moti_determine(im, ib) == 1) {
                return im;
            }
        }
        return 0;
    }

    //---------------

    //Boundary of rods Boundary surface (two sides in yellow) Here, Menid of the proliferating branch of Menid was made.
    public int Stick_moti_Menid_min_motome(int ib) {
        return Stick_moti_FaceId_min[ib];
    }

    //Returns the Menid with the larger Menid of the faces containing the bar ib as the boundary (there are up to two faces). Returns 0 if there is no face containing the bar as the boundary
    public int Stick_moti_Menid_max_motome(int ib) {
        return Stick_moti_FaceId_max[ib];
    }

    //---------------
    private int onaji_ka_hantei(Face m, Face n) { //同じなら1、違うなら0を返す

        if (m.getPointsCount() != n.getPointsCount()) {
            return 0;
        }

        for (int i = 1; i <= m.getPointsCount(); i++) {
            if (m.getPointId(i) != n.getPointId(i)) {
                return 0;
            }
        }

        return 1;

    }

    //Returns 1 if the boundary of Face [im] contains Point [it], 0 if it does not.
    public int Point_moti_determine(int im, int it) {
        for (int i = 1; i <= faces[im].getPointsCount(); i++) {
            if (it == faces[im].getPointId(i)) {
                return 1;
            }
        }
        return 0;
    }

    //Men[im]の境界にBou[ib]が含まれるなら1、含まれないなら0を返す
    private int Stick_moti_determine(int im, int ib) {
        for (int i = 1; i <= faces[im].getPointsCount() - 1; i++) {
            if ((sticks[ib].getBegin() == faces[im].getPointId(i)) && (sticks[ib].getEnd() == faces[im].getPointId(i + 1))) {
                return 1;
            }
            if ((sticks[ib].getEnd() == faces[im].getPointId(i)) && (sticks[ib].getBegin() == faces[im].getPointId(i + 1))) {
                return 1;
            }
        }
        if ((sticks[ib].getBegin() == faces[im].getPointId(faces[im].getPointsCount())) && (sticks[ib].getEnd() == faces[im].getPointId(1))) {
            return 1;
        }
        if ((sticks[ib].getEnd() == faces[im].getPointId(faces[im].getPointsCount())) && (sticks[ib].getBegin() == faces[im].getPointId(1))) {
            return 1;
        }

        return 0;
    }

    //------------------------------------------------------
    private void Face_adjecent_create() {
        System.out.println("面となり作成　開始");
        for (int im = 1; im <= facesTotal - 1; im++) {
            for (int in = im + 1; in <= facesTotal; in++) {
                Face_adjacent[im][in] = 0;
                Face_adjacent[in][im] = 0;
                int ima, imb, ina, inb;
                for (int iim = 1; iim <= faces[im].getPointsCount(); iim++) {
                    ima = faces[im].getPointId(iim);
                    if (iim == faces[im].getPointsCount()) {
                        imb = faces[im].getPointId(1);
                    } else {
                        imb = faces[im].getPointId(iim + 1);
                    }

                    for (int iin = 1; iin <= faces[in].getPointsCount(); iin++) {
                        ina = faces[in].getPointId(iin);

                        if (iin == faces[in].getPointsCount()) {
                            inb = faces[in].getPointId(1);
                        } else {
                            inb = faces[in].getPointId(iin + 1);
                        }

                        if (((ima == ina) && (imb == inb)) || ((ima == inb) && (imb == ina))) {
                            int ib;
                            ib = Stick_search(ima, imb);
                            Face_adjacent[im][in] = ib;
                            Face_adjacent[in][im] = ib;
                        }
                    }
                }

            }
        }
        System.out.println("面となり作成　終了");
    }

    //Returns the Stick number containing points t1 and t2
    private int Stick_search(int t1, int t2) {
        for (int i = 1; i <= sticksTotal; i++) {
            if ((sticks[i].getBegin() == t1) && (sticks[i].getEnd() == t2)) {
                return i;
            }
            if ((sticks[i].getBegin() == t2) && (sticks[i].getEnd() == t1)) {
                return i;
            }
        }
        return 0;
    }

    // If Face [im] and Face [ib] are adjacent, return the id number of the bar at the boundary. Returns 0 if not adjacent
    public int Face_tonari_hantei(int im, int in) {
        return Face_adjacent[im][in];
    }

    //
    private void addFace(Face tempFace) {
        facesTotal = facesTotal + 1;

        faces[facesTotal].reset();
        for (int i = 1; i <= tempFace.getPointsCount(); i++) {
            faces[facesTotal].addPointId(tempFace.getPointId(i));
        }
//System.out.println("点集合：addMen 3   Mensuu = "+  Mensuu );
        faces[facesTotal].setColor(tempFace.getColor());
    }

    //与えられた座標と一定の距離より近い近傍にあって、かつ最も近い点の番号を返す。もし、一定の距離以内にTenがないなら0を返す。
    public int mottomo_tikai_Tenid(Point p, double r) {
        int ireturn = 0;
        double rmin = 1000000.0;
        double rtemp;
        for (int i = 1; i <= pointsTotal; i++) {
            rtemp = oc.distance(p, points[i]);
            if (rtemp < r) {
                if (rtemp < rmin) {
                    rmin = rtemp;
                    ireturn = i;
                }
            }
        }
        return ireturn;
    }


    //Returns the distance of the closest point that is closer than a certain distance to the given coordinates. If there is no Ten within a certain distance, 1000000.0 is returned.
    public double closest_Point_distance(Point p, double r) {
        int ireturn = 0;
        double rmin = 1000000.0;
        double rtemp;
        for (int i = 1; i <= pointsTotal; i++) {
            rtemp = oc.distance(p, points[i]);
            if (rtemp < r) {
                if (rtemp < rmin) {
                    rmin = rtemp;
                    ireturn = i;
                }
            }
        }
        return rmin;
    }


    //一定の距離より近い位置関係にあるTen同士の位置を、共に番号の若い方の位置にする。
    public void Point_match(double r) {

        for (int i = 1; i <= pointsTotal - 1; i++) {
            for (int j = i + 1; j <= pointsTotal; j++) {
                if (oc.distance(points[i], points[j]) < r) {
                    points[j].set(points[i]);
                }
            }
        }
    }

    // When Point is closer to Stick than a certain distance, the position of Point should be on top of Stick.
    public void Point_Stick_match(double r) {
        //int ireturn=0;double rmin=10000.0; double rtemp;
        for (int ib = 1; ib <= sticksTotal; ib++) {
            //   Senbun s =new Senbun();
            //     s.set( Bou2Senbun(b[ib])) ;
            for (int i = 1; i <= pointsTotal - 1; i++) {
                if (oc.distance_lineSegment(points[i], points[sticks[ib].getBegin()], points[sticks[ib].getEnd()]) < r) {
                    //Tyokusen ty =new Tyokusen(t[b[ib].getmae()],t[b[ib].getato()]);
                    //t[i].set( oc.kage_motome(ty,t[i]));
                    points[i].set(oc.shadow_request(points[sticks[ib].getBegin()], points[sticks[ib].getEnd()], points[i]));
                }
            }
        }
        //  return ireturn;
    }

    //--------------------
    public int getSelectedPointsNum() {
        int r_int = 0;
        for (int i = 1; i <= pointsTotal; i++) {

            if (points[i].getPointState() == 1) {
                r_int = r_int + 1;
            }

        }
        return r_int;
    }

    //--------------------
    public void setPointState1(int i) {
        points[i].setPointState1();
    }

    //--------------------
    public void setPointState0(int i) {
        points[i].setPointState0();
    }

    //--------------------
    public void setAllPointState0() {
        for (int i = 1; i <= pointsTotal; i++) {
            points[i].setPointState0();
        }
    }


    //--------------------
    public void changePointState(int i) {
        if (points[i].getPointState() == 1) {
            points[i].setPointState0();
        } else if (points[i].getPointState() == 0) {
            points[i].setPointState1();
        }
    }

    //--------------------
    public byte getPointState(int i) {
        return points[i].getPointState();
    }


    //--------------------
    public void statePointMove(Point p) {
        for (int i = 1; i <= pointsTotal; i++) {

            if (points[i].getPointState() == 1) {
                set(i, p);
            }

        }

    }

    //--------------------
    public void statePointMove(Point ugokasu_maeno_sentaku_point, Point pa, Point pb) {
        Point p_u = new Point();
        p_u.set(ugokasu_maeno_sentaku_point.getX(), ugokasu_maeno_sentaku_point.getY());
        p_u.move(pa.other_Point_position(pb));

        for (int i = 1; i <= pointsTotal; i++) {
            if (points[i].getPointState() == 1) {
                set(i, p_u);
            }
        }
    }

    //--------------------


/*

		for(int i=1;i<=Tensuu;i++){
			if(t[i].get_ten_sentaku()==1){
				set(i,p_u);
			}
		}
*/


    //線分集合の全線分の情報を Memoとして出力する。 //undo,redoの記録用に使う
    public Memo getMemo() {
        String str = "";//文字列処理用のクラスのインスタンス化

        Memo memo1 = new Memo();
        memo1.reset();

        memo1.addLine("<点>");

        for (int i = 1; i <= pointsTotal; i++) {
            memo1.addLine("番号," + i);
            memo1.addLine("座標," + points[i].getX() + "," + points[i].getY());
        }
        memo1.addLine("</点>");


        return memo1;
    }

    // -----------------------------------------------------
    public void setMemo(Memo memo1) {
        //最初に点の総数を求める

        int yomiflg = 0;//0なら読み込みを行わない。1なら読み込む。
        int ibangou = 0;
        Double Dd = 0.0;
        Integer Ii = 0;

        int iten = 0;

        String str = "";
        double ax, ay;

        for (int i = 1; i <= memo1.getLineSize(); i++) {

            StringTokenizer tk = new StringTokenizer(memo1.getLine(i), ",");
            //jtok=    tk.countTokens();

            str = tk.nextToken();
            if (str.equals("<点>")) {
                yomiflg = 1;
            } else if (str.equals("</点>")) {
                yomiflg = 0;
            }
            if ((yomiflg == 1) && (str.equals("番号"))) {
                iten = iten + 1;
            }
        }
        //sousuu =isen;
        //最初に補助線分の総数が求められた

        for (int i = 1; i <= memo1.getLineSize(); i++) {


            StringTokenizer tk = new StringTokenizer(memo1.getLine(i), ",");
            //jtok=    tk.countTokens();
            str = tk.nextToken();
            //  	System.out.println("::::::::::"+ str+"<<<<<" );
            if (str.equals("<点>")) {
                yomiflg = 1;
            }
            if ((yomiflg == 1) && (str.equals("番号"))) {
                str = tk.nextToken();
                ibangou = Integer.parseInt(str);
            }
            if ((yomiflg == 1) && (str.equals("座標"))) {
                str = tk.nextToken();
                ax = Double.parseDouble(str);
                str = tk.nextToken();
                ay = Double.parseDouble(str);
                points[ibangou].set(ax, ay);
            }


        }


    }


//-----------------------------


}

