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

public class PointStore {

    int numFaces_temp;

//Ten_p tp=new Ten_p();

    int pointsTotal;               //実際に使う点の総数
    int sticksTotal;               //実際に使う棒の総数
    int facesTotal;               //実際に使う面の総数
    Point_p[] t;//点のインスタンス化
    Stick[] b;//棒のインスタンス化
    int[] Stick_moti_Menid_min;
    int[] Stick_moti_Menid_max;

    Face[] m;//面のインスタンス化

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

    public PointStore() {
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

        t = new Point_p[numPoints + 1];
        point_linking = new ArrayList[numPoints + 1];

        point_linking[0] = new ArrayList<>();
        for (int i = 0; i <= numPoints; i++) {
            point_linking[i] = new ArrayList<>();
            point_linking[i].add(0);
        }

        for (int i = 0; i <= numPoints; i++) {
            t[i] = new Point_p();
            set_T_renketu(i, 0, 0);
        }

        b = new Stick[numSticks + 1];
        int[] BMmin = new int[numSticks + 1];
        int[] BMmax = new int[numSticks + 1];
        Stick_moti_Menid_min = BMmin;
        Stick_moti_Menid_max = BMmax;
        for (int i = 0; i <= numSticks; i++) {
            b[i] = new Stick();
            Stick_moti_Menid_min[i] = 0;
            Stick_moti_Menid_max[i] = 0;
        }

        m = new Face[numFaces + 1];

        Face_adjacent = new int[numFaces + 1][numFaces + 1];

        for (int i = 0; i <= numFaces; i++) {
            //for(int i=0;i<=numFaces+1;i++){
            m[i] = new Face();
            for (int j = 0; j <= numFaces; j++) {
                //for(int j=0;j<=numFaces+1;j++){

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
    private int get_T_renketu(int i, int j) {
        return point_linking[i].get(j);
    }

    private void set_T_renketu(int i, int j, int tid) {
        if (j + 1 > point_linking[i].size()) {
            while (j + 1 > point_linking[i].size()) {
                point_linking[i].add(0);
            }
        }//この文がないとうまく行かない。なぜこの文でないといけないかという理由が正確にはわからない。
        point_linking[i].set(j, tid);
    }

    //------------------------------
    private double getAverage_x() {
        double x = 0.0;
        for (int i = 1; i <= pointsTotal; i++) {
            x = x + t[i].getX();
        }
        return x / ((double) pointsTotal);
    }

    private double getAverage_y() {
        double y = 0.0;
        for (int i = 1; i <= pointsTotal; i++) {
            y = y + t[i].getY();
        }
        return y / ((double) pointsTotal);
    }

    //
    public void turnOver() {//Turn it over to the left and right around the position of the center of gravity.
        double xh;
        int icol;
        xh = getAverage_x();
        for (int i = 1; i <= pointsTotal; i++) {
            t[i].setX(2.0 * xh - t[i].getX());
        }
        for (int i = 1; i <= sticksTotal; i++) {
            icol = b[i].getColor();
            if (icol == 1) {
                b[i].setColor(2);
            }
            if (icol == 2) {
                b[i].setColor(1);
            }
        }

    }


    //public double menseki(int men_id) {return 1.0; }//   面の面積を返す


    public void parallelMove(double x, double y) {
        for (int i = 0; i <= pointsTotal; i++) {
            t[i].parallel_move(x, y);
        }
    }

    public void set(PointStore ts) {
        pointsTotal = ts.getPointsTotal();
        sticksTotal = ts.getSticksTotal();
        facesTotal = ts.getFacesTotal();
        for (int i = 0; i <= pointsTotal; i++) {
            t[i].set(ts.getPoint(i));                                                         //  <<<-------
            for (int j = 1; j <= ts.get_T_renketu(i, 0); j++) {
                set_T_renketu(i, j, ts.get_T_renketu(i, j));
            }
        }
        for (int i = 0; i <= sticksTotal; i++) {
            b[i].set(ts.getStick(i));
            Stick_moti_Menid_min[i] = ts.get_Stick_moti_Menid_min(i);
            Stick_moti_Menid_max[i] = ts.get_Stick_moti_Menid_max(i);
        }
        for (int i = 0; i <= facesTotal; i++) {
            m[i] = new Face(ts.getMen(i));
            for (int j = 0; j <= facesTotal; j++) {
                Face_adjacent[i][j] = ts.get_Men_tonari(i, j);
            }
        }
    }

    public void set(int i, Point tn) {
        t[i].set(tn);
    }                                               //  <<<-------

    private int get_Men_tonari(int i, int j) {
        return Face_adjacent[i][j];
    }

    //
    private int get_Stick_moti_Menid_min(int i) {
        return Stick_moti_Menid_min[i];
    }

    //
    private int get_Stick_moti_Menid_max(int i) {
        return Stick_moti_Menid_max[i];
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

    //点が面の内部にあるかどうかを判定する。0なら内部にない、1なら境界線上、2なら内部
    public int kantan_inside(Point p, int n) {      //0=外部、　1=境界、　2=内部
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
        //System.out.println("2017");
        return inside(p, m[n]);
    }

    //点が面の内部にあるかどうかを判定する。
    public int inside(Point p, int n) {      //0=外部、　1=境界、　2=内部
        return inside(p, m[n]);
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
        tk.setkakusuu(mn.getPointsCount());
        for (int i = 0; i <= mn.getPointsCount(); i++) {
            tk.set(i, t[mn.getPointId(i)]);
        }
        return tk;
    }

    //線分s0の一部でも凸多角形の面の内部（境界線は内部とみなさない）に
    //存在するとき1、しないなら0を返す。面が凹多角形の場合は結果がおかしくなるので使わないこと
    public int kantan_totu_inside(int ib, int im) {
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

        return convex_inside(new LineSegment(t[b[ib].getBegin()], t[b[ib].getEnd()]), m[im]);
    }

    private int convex_inside(LineSegment s0, Face mn) {
        Polygon tk;//=new Takakukei();
        tk = makePolygon(mn);
        return tk.convex_inside(s0);
    }

    private int convex_inside(int ib, int im) {
        return convex_inside(new LineSegment(t[b[ib].getBegin()], t[b[ib].getEnd()]), m[im]);
    }

    public int convex_inside(double d, int ib, int im) {
        LineSegment sn = new LineSegment(t[b[ib].getBegin()], t[b[ib].getEnd()]);
        return convex_inside(oc.moveParallel(sn, d), m[im]);
    }

    private int kantan_totu_inside(double d, int ib, int im) {
        LineSegment sn = new LineSegment(t[b[ib].getBegin()], t[b[ib].getEnd()]);
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

        return convex_inside(snm, m[im]);
    }


    //棒を線分にする
    private LineSegment stickToLineSegment(Stick bu) {
        return new LineSegment(t[bu.getBegin()], t[bu.getEnd()]);
    }

    //Returns 1 if two Sticks are parallel and partially or wholly overlap, otherwise 0. If one point overlaps, 0 is returned.
    public int parallel_overlap(int ib1, int ib2) {
        int skh;
        skh = oc.line_intersect_decide(stickToLineSegment(b[ib1]), stickToLineSegment(b[ib2]));
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
        return insidePoint_surface(m[n]);
    }

    //面の内部の点を求める
    private Point insidePoint_surface(Face mn) {
        Polygon tk;
        tk = makePolygon(mn);
        return tk.naibuTen_motome();
    }

    //面積求める
    public double calculateArea(int n) {
        return calculateArea(m[n]);
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
        return m[i].getPointId(j);
    }  // void setTensuu(int i){Tensuu=i;}

    // void setBousuu(int i){Bousuu=i;}
    public double getPointX(int i) {
        return t[i].getX();
    }

    public double getPointY(int i) {
        return t[i].getY();
    }

    public Point getPoint(int i) {
        return t[i];
    }   //点を得る       <<<------------tは、スーパークラスのTenのサブクラスTen_Pクラスのオブジェクト。スーパークラスの変数にサブクラスのオブジェクトを代入可能なので、このまま使う。

    private Stick getStick(int i) {
        return b[i];
    }   //棒を得る

    public Point get_maeTen_from_Stick_id(int i) {
        return t[getmae(i)];
    }    //棒のidから前点を得る              <<<------------　　同上

    public Point get_atoTen_from_Bou_id(int i) {
        return t[getato(i)];
    }    //棒のidから後点を得る              <<<------------　　同上


    public LineSegment get_Senbun_from_Bou_id(int i) {
        return stickToLineSegment(getStick(i));
    }    //棒のidからSenbunを得る

    private Face getMen(int i) {
        return m[i];
    }   //面を得る

    public int getmae(int i) {
        return b[i].getBegin();
    } //棒のidから前点のidを得る

    public int getato(int i) {
        return b[i].getEnd();
    } //棒のidから後点のidを得る

    public double getmaex(int i) {
        return t[b[i].getBegin()].getX();
    }

    public double getmaey(int i) {
        return t[b[i].getBegin()].getY();
    }

    public double getatox(int i) {
        return t[b[i].getEnd()].getX();
    }

    public double getatoy(int i) {
        return t[b[i].getEnd()].getY();
    }

    public int getTenidsuu(int i) {
        return m[i].getPointsCount();
    }

    public void setTen(int i, Point tn) {
        t[i].set(tn);
    }                                                        //   <<<------------

    private void setTen(int i, double x, double y) {
        t[i].set(x, y);
    }

    public void addPoint(double x, double y) {
        pointsTotal = pointsTotal + 1;
        t[pointsTotal].set(x, y);
    }   //点を加える

    public void addStick(int i, int j, int icol) {
        sticksTotal = sticksTotal + 1;
        b[sticksTotal].set(i, j, icol);
    }   //棒を加える

    //i番目の棒の色を入出力する
    private void setcolor(int i, int icol) {
        b[i].setColor(icol);
    }

    public int getcolor(int i) {
        return b[i].getColor();
    }

    private void t_renketu_sakusei() {
        for (int k = 1; k <= sticksTotal; k++) {
            set_T_renketu(b[k].getBegin(), 0, get_T_renketu(b[k].getBegin(), 0) + 1);
            set_T_renketu(b[k].getBegin(), get_T_renketu(b[k].getBegin(), 0), b[k].getEnd());
            set_T_renketu(b[k].getEnd(), 0, get_T_renketu(b[k].getEnd(), 0) + 1);
            set_T_renketu(b[k].getEnd(), get_T_renketu(b[k].getEnd(), 0), b[k].getBegin());
        }
    }

    //点iと点jが棒で連結していれば1、していなければ0を返す。
    private int renketu_hantei(int i, int j) {
        for (int k = 1; k <= sticksTotal; k++) {
            if (
                    ((b[k].getBegin() == i) && (b[k].getEnd() == j))
                            ||
                            ((b[k].getBegin() == j) && (b[k].getEnd() == i))
            ) {
                return 1;
            }
        }
        return 0;
    }

    //点iから点jに進んで、次に、点jから点iの右隣に進む時の点の番号を求める。
    private int getRTen(int i, int j) {
        int n = 0;
        double kakudo = 876.0;   //kakudoは適当な大きい数にしておく

        // if(renketu_hantei(i,j)==0){return 0;}//点iと点jが連結していない時は0を返す

        int iflg = 0;
        for (int k = 1; k <= get_T_renketu(i, 0); k++) {
            if (get_T_renketu(i, k) == j) {
                iflg = 1;
            }
        }

        if (iflg == 0) {
            return 0;
        }//点iと点jが連結していない時は0を返す

        for (int ik = 1; ik <= get_T_renketu(j, 0); ik++) {
            int k;
            k = get_T_renketu(j, ik);
            if (k != i) {
                if (oc.angle(t[j], t[i], t[j], t[k]) <= kakudo) {
                    n = k;
                    kakudo = oc.angle(t[j], t[i], t[j], t[k]);
                }
            }
        }
        return n; //点jに連結している点が点iしかない時は0を返す
    }
    //--------------------------------

    private Face Face_request(int i, int j) {//i番目の点、j番目の点から初めて右側の棒をたどって面を求める
        Face mtemp = new Face();
        //mtemp.reset();
        mtemp.addPointId(i);
        mtemp.addPointId(j);
        int nextT = 0;

        nextT = getRTen(mtemp.getPointId(1), mtemp.getPointId(2));
        while (nextT != mtemp.getPointId(1)) {
            if (nextT == 0) {
                mtemp.reset();
                return mtemp;
            }//エラー時の対応
            mtemp.addPointId(nextT);
            nextT = getRTen(mtemp.getPointId(mtemp.getPointsCount() - 1), mtemp.getPointId(mtemp.getPointsCount()));
        }
        mtemp.align();
        return mtemp;
    }

    //-------------------------------------
    public void Menhassei() {
        int flag1;
        Face mtemp = new Face();
        facesTotal = 0;
        t_renketu_sakusei();

        for (int i = 1; i <= sticksTotal; i++) {
            //System.out.print("面発生　＝　"+i+"    ");System.out.println(Mensuu);

            //
            mtemp = Face_request(b[i].getBegin(), b[i].getEnd());
            flag1 = 0;   //　0なら面を追加する。1なら　面を追加しない。
            for (int j = 1; j <= facesTotal; j++) {
                if (onaji_ka_hantei(mtemp, m[j]) == 1) {
                    flag1 = 1;
                    break;
                }
            }

            if (((flag1 == 0) && (mtemp.getPointsCount() != 0)) &&
                    (calculateArea(mtemp) > 0.0)) {
                addFace(mtemp);
            }
            //

            mtemp = Face_request(b[i].getEnd(), b[i].getBegin());
            flag1 = 0;   //　0なら面を追加する。1なら　面を追加しない。
            for (int j = 1; j <= facesTotal; j++) {
                if (onaji_ka_hantei(mtemp, m[j]) == 1) {
                    flag1 = 1;
                    break;
                }
            }

            if (((flag1 == 0) && (mtemp.getPointsCount() != 0)) && (calculateArea(mtemp) > 0.0)) {
                //System.out.println("面発生ループ内　003");
                addFace(mtemp);
                //System.out.println("面発生ループ内　004");
            }

            //-----
            //	if(Mensuu%20==0){
            //		System.out.print("今までに発生した面数　＝　");System.out.println(Mensuu);
            //	}
        }

        System.out.print("全面数　＝　");
        System.out.println(facesTotal);
        Men_tonari_sakusei();

        //Bouの両側の面の登録
        for (int ib = 1; ib <= sticksTotal; ib++) {

            Stick_moti_Menid_min[ib] = Stick_moti_Menid_min_sagasi(ib);
            Stick_moti_Menid_max[ib] = Stick_moti_Menid_max_sagasi(ib);
        }
    }

    //BouやMenの座標の最大値、最小値を求める。kantan_totu_naibu関数にのみ用いる。kantan_totu_naibu関数を使うなら折り畳み推定毎にやる必要あり。
    public void BouMenMaxMinZahyou() {
        //Bouの座標の最大最小を求める（これはここでやるより、Bouが加えられた直後にやるほうがよいかも知れない。）
        for (int ib = 1; ib <= sticksTotal; ib++) {

            Stick_x_max[ib] = t[b[ib].getBegin()].getX();
            Stick_x_min[ib] = t[b[ib].getBegin()].getX();
            Stick_y_max[ib] = t[b[ib].getBegin()].getY();
            Stick_y_min[ib] = t[b[ib].getBegin()].getY();

            if (Stick_x_max[ib] < t[b[ib].getEnd()].getX()) {
                Stick_x_max[ib] = t[b[ib].getEnd()].getX();
            }
            if (Stick_x_min[ib] > t[b[ib].getEnd()].getX()) {
                Stick_x_min[ib] = t[b[ib].getEnd()].getX();
            }
            if (Stick_y_max[ib] < t[b[ib].getEnd()].getY()) {
                Stick_y_max[ib] = t[b[ib].getEnd()].getY();
            }
            if (Stick_y_min[ib] > t[b[ib].getEnd()].getY()) {
                Stick_y_min[ib] = t[b[ib].getEnd()].getY();
            }
            MenMaxMinZahyou();
        }
    }

    private void MenMaxMinZahyou() {
        //Menの座標の最大最小を求める
        for (int im = 1; im <= facesTotal; im++) {
            Surface_x_max[im] = t[m[im].getPointId(1)].getX();
            Surface_x_min[im] = t[m[im].getPointId(1)].getX();
            Surface_y_max[im] = t[m[im].getPointId(1)].getY();
            Surface_y_min[im] = t[m[im].getPointId(1)].getY();
            for (int i = 2; i <= m[im].getPointsCount(); i++) {
                if (Surface_x_max[im] < t[m[im].getPointId(i)].getX()) {
                    Surface_x_max[im] = t[m[im].getPointId(i)].getX();
                }
                if (Surface_x_min[im] > t[m[im].getPointId(i)].getX()) {
                    Surface_x_min[im] = t[m[im].getPointId(i)].getX();
                }
                if (Surface_y_max[im] < t[m[im].getPointId(i)].getY()) {
                    Surface_y_max[im] = t[m[im].getPointId(i)].getY();
                }
                if (Surface_y_min[im] > t[m[im].getPointId(i)].getY()) {
                    Surface_y_min[im] = t[m[im].getPointId(i)].getY();
                }
            }
        }
    }


    public Point get_men_migiue_Ten(int im) {//imは面番号　。migiue	指定された番号の面を含む最小の長方形の右上の頂点を返す。　折り上がり図の裏返図の位置指定に使う。
        //Menの座標の最大最小を求める

        double x_max = t[m[im].getPointId(1)].getX();
        double x_min = t[m[im].getPointId(1)].getX();
        double y_max = t[m[im].getPointId(1)].getY();
        double y_min = t[m[im].getPointId(1)].getY();
        for (int i = 2; i <= m[im].getPointsCount(); i++) {
            if (x_max < t[m[im].getPointId(i)].getX()) {
                x_max = t[m[im].getPointId(i)].getX();
            }
            if (x_min > t[m[im].getPointId(i)].getX()) {
                x_min = t[m[im].getPointId(i)].getX();
            }
            if (y_max < t[m[im].getPointId(i)].getY()) {
                y_max = t[m[im].getPointId(i)].getY();
            }
            if (y_min > t[m[im].getPointId(i)].getY()) {
                y_min = t[m[im].getPointId(i)].getY();
            }
        }

        return new Point(x_max, y_min);

    }


    //--------------
    //棒ibを境界として含む面(最大で2面ある)のうちでMenidの小さいほうのMenidを返す。棒を境界として含む面が無い場合は0を返す
    private int Stick_moti_Menid_min_sagasi(int ib) {
        for (int im = 1; im <= facesTotal; im++) {
            if (Stick_moti_hantei(im, ib) == 1) {
                return im;
            }
        }
        return 0;
    }

    //棒ibを境界として含む面(最大で2面ある)のうちでMenidの大きいほうのMenidを返す。棒を境界として含む面が無い場合は0を返す
    private int Stick_moti_Menid_max_sagasi(int ib) {
        for (int im = facesTotal; im >= 1; im--) {
            if (Stick_moti_hantei(im, ib) == 1) {
                return im;
            }
        }
        return 0;
    }

    //---------------

    //Boundary of rods Boundary surface (two sides in yellow) Here, Menid of the proliferating branch of Menid was made.
    public int Stick_moti_Menid_min_motome(int ib) {
        return Stick_moti_Menid_min[ib];
    }

    //Returns the Menid with the larger Menid of the faces containing the bar ib as the boundary (there are up to two faces). Returns 0 if there is no face containing the bar as the boundary
    public int Stick_moti_Menid_max_motome(int ib) {
        return Stick_moti_Menid_max[ib];
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

    //Men[im]の境界にTen[it]が含まれるなら1、含まれないなら0を返す
    public int Ten_moti_hantei(int im, int it) {
        for (int i = 1; i <= m[im].getPointsCount(); i++) {
            if (it == m[im].getPointId(i)) {
                return 1;
            }
        }
        return 0;
    }

    //Men[im]の境界にBou[ib]が含まれるなら1、含まれないなら0を返す
    private int Stick_moti_hantei(int im, int ib) {
        for (int i = 1; i <= m[im].getPointsCount() - 1; i++) {
            if ((b[ib].getBegin() == m[im].getPointId(i)) && (b[ib].getEnd() == m[im].getPointId(i + 1))) {
                return 1;
            }
            if ((b[ib].getEnd() == m[im].getPointId(i)) && (b[ib].getBegin() == m[im].getPointId(i + 1))) {
                return 1;
            }
        }
        if ((b[ib].getBegin() == m[im].getPointId(m[im].getPointsCount())) && (b[ib].getEnd() == m[im].getPointId(1))) {
            return 1;
        }
        if ((b[ib].getEnd() == m[im].getPointId(m[im].getPointsCount())) && (b[ib].getBegin() == m[im].getPointId(1))) {
            return 1;
        }

        return 0;
    }

    //------------------------------------------------------
    private void Men_tonari_sakusei() {
        System.out.println("面となり作成　開始");
        for (int im = 1; im <= facesTotal - 1; im++) {
            for (int in = im + 1; in <= facesTotal; in++) {
                Face_adjacent[im][in] = 0;
                Face_adjacent[in][im] = 0;
                int ima, imb, ina, inb;
                for (int iim = 1; iim <= m[im].getPointsCount(); iim++) {
                    ima = m[im].getPointId(iim);
                    if (iim == m[im].getPointsCount()) {
                        imb = m[im].getPointId(1);
                    } else {
                        imb = m[im].getPointId(iim + 1);
                    }

                    for (int iin = 1; iin <= m[in].getPointsCount(); iin++) {
                        ina = m[in].getPointId(iin);

                        if (iin == m[in].getPointsCount()) {
                            inb = m[in].getPointId(1);
                        } else {
                            inb = m[in].getPointId(iin + 1);
                        }

                        if (((ima == ina) && (imb == inb)) || ((ima == inb) && (imb == ina))) {
                            int ib;
                            ib = Bou_sagasi(ima, imb);
                            Face_adjacent[im][in] = ib;
                            Face_adjacent[in][im] = ib;
                        }
                    }
                }

            }
        }
        System.out.println("面となり作成　終了");
    }

    //点t1とt2を含むBouの番号を返す
    private int Bou_sagasi(int t1, int t2) {
        for (int i = 1; i <= sticksTotal; i++) {
            if ((b[i].getBegin() == t1) && (b[i].getEnd() == t2)) {
                return i;
            }
            if ((b[i].getBegin() == t2) && (b[i].getEnd() == t1)) {
                return i;
            }
        }
        return 0;
    }

    //Men[im]とMen[ib]が隣接するならその境界にある棒のid番号を返す。隣接しないなら0を返す
    public int Face_tonari_hantei(int im, int in) {
		/*
		for(int i=1;i<=Bousuu;i++){
       	        	if(( Bou_moti_hantei( im,i)==1)&&( Bou_moti_hantei( in,i)==1)){return i;}
		}
		return 0;
		*/
        return Face_adjacent[im][in];
    }

    //
    private void addFace(Face mtemp) {
        facesTotal = facesTotal + 1;
//System.out.println("点集合：addMen 1   Mensuu = "+Mensuu+"  Msuu = "+Msuu_temp );

        m[facesTotal].reset();
        //for (int i=0; i<50; i++ ){m[Mensuu].setTenid(i,mtemp.getTenid(i));}
//System.out.println("点集合：addMen 2   Mensuu = "+  Mensuu    );
        for (int i = 1; i <= mtemp.getPointsCount(); i++) {
            m[facesTotal].addPointId(mtemp.getPointId(i));
        }
//System.out.println("点集合：addMen 3   Mensuu = "+  Mensuu );
        m[facesTotal].setColor(mtemp.getColor());
    }

    //与えられた座標と一定の距離より近い近傍にあって、かつ最も近い点の番号を返す。もし、一定の距離以内にTenがないなら0を返す。
    public int mottomo_tikai_Tenid(Point p, double r) {
        int ireturn = 0;
        double rmin = 1000000.0;
        double rtemp;
        for (int i = 1; i <= pointsTotal; i++) {
            rtemp = oc.distance(p, t[i]);
            if (rtemp < r) {
                if (rtemp < rmin) {
                    rmin = rtemp;
                    ireturn = i;
                }
            }
        }
        return ireturn;
    }


    //与えられた座標と一定の距離より近い近傍にあって、かつ最も近い点の距離を返す。もし、一定の距離以内にTenがないなら1000000.0を返す。
    public double mottomo_tikai_Point_distance(Point p, double r) {
        int ireturn = 0;
        double rmin = 1000000.0;
        double rtemp;
        for (int i = 1; i <= pointsTotal; i++) {
            rtemp = oc.distance(p, t[i]);
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
                if (oc.distance(t[i], t[j]) < r) {
                    t[j].set(t[i]);
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
                if (oc.distance_lineSegment(t[i], t[b[ib].getBegin()], t[b[ib].getEnd()]) < r) {
                    //Tyokusen ty =new Tyokusen(t[b[ib].getmae()],t[b[ib].getato()]);
                    //t[i].set( oc.kage_motome(ty,t[i]));
                    t[i].set(oc.shadow_request(t[b[ib].getBegin()], t[b[ib].getEnd()], t[i]));
                }
            }
        }
        //  return ireturn;
    }

    //--------------------
    public int getSelectedPointsNum() {
        int r_int = 0;
        for (int i = 1; i <= pointsTotal; i++) {

            if (t[i].getPointState() == 1) {
                r_int = r_int + 1;
            }

        }
        return r_int;
    }

    //--------------------
    public void setPointState1(int i) {
        t[i].setPointState1();
    }

    //--------------------
    public void setPointState0(int i) {
        t[i].setPointState0();
    }

    //--------------------
    public void setAllPointState0() {
        for (int i = 1; i <= pointsTotal; i++) {
            t[i].setPointState0();
        }
    }


    //--------------------
    public void changePointState(int i) {
        if (t[i].getPointState() == 1) {
            t[i].setPointState0();
        } else if (t[i].getPointState() == 0) {
            t[i].setPointState1();
        }
    }

    //--------------------
    public byte getPointState(int i) {
        return t[i].getPointState();
    }


    //--------------------
    public void statePointMove(Point p) {
        for (int i = 1; i <= pointsTotal; i++) {

            if (t[i].getPointState() == 1) {
                set(i, p);
            }

        }

    }

    //--------------------
    public void statePointMove(Point ugokasu_maeno_sentaku_point, Point pa, Point pb) {
        Point p_u = new Point();
        p_u.set(ugokasu_maeno_sentaku_point.getX(), ugokasu_maeno_sentaku_point.getY());
        p_u.move(pa.tano_Point_iti(pb));

        for (int i = 1; i <= pointsTotal; i++) {
            if (t[i].getPointState() == 1) {
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
            memo1.addLine("座標," + t[i].getX() + "," + t[i].getY());
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
                t[ibangou].set(ax, ay);
            }


        }


    }


//-----------------------------


}

