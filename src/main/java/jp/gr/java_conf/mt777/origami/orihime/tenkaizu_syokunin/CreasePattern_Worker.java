package jp.gr.java_conf.mt777.origami.orihime.tenkaizu_syokunin;

import java.awt.*;

import jp.gr.java_conf.mt777.origami.dougu.linestore.*;
import jp.gr.java_conf.mt777.origami.dougu.camera.*;
import jp.gr.java_conf.mt777.origami.dougu.pointstore.*;
import jp.gr.java_conf.mt777.origami.orihime.undo_box.*;
import jp.gr.java_conf.mt777.kiroku.memo.*;
import jp.gr.java_conf.mt777.zukei2d.senbun.*;
import jp.gr.java_conf.mt777.zukei2d.heikinzahyou.*;
import jp.gr.java_conf.mt777.zukei2d.oritacalc.*;
import jp.gr.java_conf.mt777.zukei2d.ten.Point;
// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------

public class CreasePattern_Worker {//This crease pattern craftsman class has only one PointStore c as a crease pattern.
    //折り畳み等をやった結果得られるTensyuugouは外部に返すようにして、自分自身では保持しない。
    OritaCalc oc = new OritaCalc(); //各種計算用の関数を使うためのクラスのインスタンス化
    double r = 3.0;                   //Criteria for determining the radius of the circles at both ends of the straight line of the basic branch structure and the proximity of the branches to various points

    CreasePattern creasePattern = new CreasePattern();    //展開図

    Undo_Box Ubox = new Undo_Box();

    //VVVVVVVVVVVV oritatami　と　oekaki で使う変数　の定義　VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
    int[] iFacePosition;//ある面が基準面と何面離れているかを示す。基準面では1、基準面の隣では2、その隣では3という様に値を入れる
    int referencePlane_id;
    int[] nextFaceId;//ある面の隣の面（基準面側）のid
    int[] associatedStickId;//ある面と隣の面（基準面側）の間の棒のid

    AverageCoordinates[] tnew;//折った時の点の位置を格納

    //  Ten naibuTen [] = new Ten[888];         //面の内部の点を格納

    //マウスでTenを選択した場合のTenの番号を格納
    public int i_ugokasuTen = 0;

    Camera camera = new Camera();
    Camera cam_front = new Camera();
    Camera cam_rear = new Camera();

    Camera cam_transparent_front = new Camera();
    Camera cam_transparent_rear = new Camera();


    public Point point_of_referencePlane_ob = new Point();

    //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

    public CreasePattern_Worker(double r0) {  //コンストラクタ
        r = r0;
    }

    public void reset() {
        r = 3.0;
        creasePattern.reset();

        camera.reset();
        cam_front.reset();
        cam_rear.reset();

        cam_transparent_front.reset();
        cam_transparent_rear.reset();
    }

    private void configure(int pointsTotal, int sticksTotal, int facesTotal) {
        tnew = new AverageCoordinates[pointsTotal + 1];
        for (int i = 0; i <= pointsTotal; i++) {
            tnew[i] = new AverageCoordinates();
        }
        iFacePosition = new int[facesTotal + 1];
        nextFaceId = new int[facesTotal + 1];         //The id of the surface (reference surface side) next to a certain surface
        associatedStickId = new int[facesTotal + 1];         //The id of the bar between one surface and the next surface (reference surface side)
    }

    //-----------
    public void add_referencePlane_id() {
        referencePlane_id = referencePlane_id + 1;
        if (referencePlane_id > creasePattern.getFacesTotal()) {
            referencePlane_id = 1;
        }
    }


    //-------------------------------------------
    public int getReferencePlane_id() {
        return referencePlane_id;
    }


    //-------------------------------------------
    public Point getReferencePlaneUpperRightPoint() {
        return creasePattern.getFaceUpperRightPoint(referencePlane_id);
    }

    //-------------------------------------------
    public Point get_point_of_referencePlane_ob() {
        return point_of_referencePlane_ob;
    }

    //-------------------------------------------
    public Point get_point_of_referencePlane_tv() {
        return camera.object2TV(point_of_referencePlane_ob);
    }

    //-------------------------------------------
    public int set_referencePlane_id(int i) {
        referencePlane_id = i;

        if (referencePlane_id > creasePattern.getFacesTotal()) {
            referencePlane_id = creasePattern.getFacesTotal();
        }
        if (referencePlane_id < 1) {
            referencePlane_id = 1;
        }

        point_of_referencePlane_ob = creasePattern.insidePoint_surface(referencePlane_id);

        return referencePlane_id;
    }


    //-----------これは基準面指定モードでマウスを押されたときの対応201503
    public int set_referencePlane_id(Point p0) {//実際に有効になっている基準面idを返す
        Point p = new Point();
        p.set(camera.TV2object(p0));
        if (creasePattern.inside(p) > 0) {
            referencePlane_id = creasePattern.inside(p);
            point_of_referencePlane_ob.set(p);
        }//c.naibu(p)=0ならどの面の内部にもない、マイナスなら境界線上、正の数なら内部。該当する面番号が複数ある場合は番号の小さいほうが返される。
        return referencePlane_id;
    }


    //-----------Ten p0が折り上がり図の内部に有るかどうかを判定する
    public int naibu_hantei(Point p0) {//実際にp0がある面idを返す
        Point p = new Point();
        p.set(camera.TV2object(p0));
        return creasePattern.inside(p);//c.naibu(p)=0ならどの面の内部にもない、マイナスなら境界線上、正の数なら内部。該当する面番号が複数ある場合は番号の小さいほうが返される。
    }

    //-----------Ten p0が折り上がり図(表)の内部に有るかどうかを判定する
    public int naibu_hantei_front(Point p0) {//実際にp0がある面idを返す
        Point p = new Point();
        p.set(cam_front.TV2object(p0));
        return creasePattern.inside(p);//Tensyuugou c.naibu(p)=0ならどの面の内部にもない、マイナスなら境界線上、正の数なら内部。該当する面番号が複数ある場合は番号の小さいほうが返される。
    }

    //-----------Ten p0が折り上がり図(裏)の内部に有るかどうかを判定する
    public int naibu_hantei_rear(Point p0) {//実際にp0がある面idを返す
        Point p = new Point();
        p.set(cam_rear.TV2object(p0));
        return creasePattern.inside(p);//Tensyuugou c.naibu(p)=0ならどの面の内部にもない、マイナスなら境界線上、正の数なら内部。該当する面番号が複数ある場合は番号の小さいほうが返される。
    }

    //-----------Ten p0が折り上がり図に付属して表示される透過図(表)の内部に有るかどうかを判定する
    public int naibu_hantei_transparent_front(Point p0) {//実際にp0がある面idを返す
        Point p = new Point();
        p.set(cam_transparent_front.TV2object(p0));
        return creasePattern.inside(p);//Tensyuugou c.naibu(p)=0ならどの面の内部にもない、マイナスなら境界線上、正の数なら内部。該当する面番号が複数ある場合は番号の小さいほうが返される。
    }

    //-----------Ten p0が折り上がり図に付属して表示される透過図(裏)の内部に有るかどうかを判定する
    public int naibu_hantei_transparent_rear(Point p0) {//実際にp0がある面idを返す
        Point p = new Point();
        p.set(cam_transparent_rear.TV2object(p0));
        return creasePattern.inside(p);//Tensyuugou c.naibu(p)=0ならどの面の内部にもない、マイナスなら境界線上、正の数なら内部。該当する面番号が複数ある場合は番号の小さいほうが返される。
    }


    //--------------------------------------------
    public void set_r(double r0) {
        r = r0;
    }

    //--------------------------------------------
    public void setCamera(Camera cam0) {
        camera.setCamera(cam0);
    }

    public void setCam_front(Camera cam0) {
        cam_front.setCamera(cam0);
    }

    public void setCam_rear(Camera cam0) {
        cam_rear.setCamera(cam0);
    }

    public void setCam_transparent_front(Camera cam0) {
        cam_transparent_front.setCamera(cam0);
    }

    public void setCam_transparent_rear(Camera cam0) {
        cam_transparent_rear.setCamera(cam0);
    }


    // ----------------
    public int getFacesTotal() {
        return creasePattern.getFacesTotal();
    }

    //-------------
    public void parallelMove(double x, double y) {
        creasePattern.parallelMove(x, y);
    }

    //
    public void turnOver() {//重心の位置を中心に左右に裏返す。
        creasePattern.turnOver();
    }

    //面の内部の点を求める//---------------------------------------
    public Point naibuTen_motome(int i) {
        return creasePattern.insidePoint_surface(i);
    }

    //点集合の持つ棒の総数を得る
    public int getSticksTotal() {
        return creasePattern.getSticksTotal();
    }

    //点集合の持つ棒の色を得る（点集合を展開図として扱う場合では、この色は山谷をあらわす）。
    public int getcolor(int i) {
        return creasePattern.getColor(i);
    }

    //-------------------------------------------
    public int getIFacePosition(int i) {
        return iFacePosition[i];
    }

    //-------------------------------------------
    //折りたたみ推定（ここでできるのは面の重なりを考えていない針金図）
    //Folding estimation (What you can do here is a wire diagram that does not consider the overlap of surfaces)
    public CreasePattern folding() {//折りたたみ推定
        CreasePattern creasePattern = new CreasePattern();    //展開図
        creasePattern.configure(this.creasePattern.getPointsTotal(), this.creasePattern.getSticksTotal(), this.creasePattern.getFacesTotal());
        creasePattern.set(this.creasePattern);

        for (int i = 0; i <= this.creasePattern.getFacesTotal(); i++) {
            nextFaceId[i] = 0;
            associatedStickId[i] = 0;
            iFacePosition[i] = 0;
        }
        //Grasp the positional relationship between the faces in preparation for folding
        System.out.println("折りたたみの準備として面同士の位置関係を把握する");
        iFacePosition[referencePlane_id] = 1;

        int current_face_position = 1;
        int remaining_facesTotal;
        remaining_facesTotal = this.creasePattern.getFacesTotal() - 1;

        while (remaining_facesTotal > 0) {
            for (int i = 1; i <= this.creasePattern.getFacesTotal(); i++) {
                if (iFacePosition[i] == current_face_position) {
                    for (int j = 1; j <= this.creasePattern.getFacesTotal(); j++) {
                        int mth = this.creasePattern.Face_tonari_hantei(i, j);
                        if ((mth > 0) && (iFacePosition[j] == 0)) {
                            iFacePosition[j] = current_face_position + 1;
                            nextFaceId[j] = i;
                            associatedStickId[j] = mth;
                        }
                    }
                }
            }

            current_face_position = current_face_position + 1;

            remaining_facesTotal = 0;
            for (int i = 1; i <= this.creasePattern.getFacesTotal(); i++) {
                if (iFacePosition[i] == 0) {
                    remaining_facesTotal = remaining_facesTotal + 1;
                }
            }

            System.out.println("remaining_facesTotal = " + remaining_facesTotal);
        }

        System.out.println("折ったときの点の位置を求める。");
        // Find the position of the point when folded.
        // If the point it is included in the face im
        // Find where to move when the development drawing is folded by moving the face im.

        System.out.println("折ったときの点の位置を求める（開始）");
        for (int it = 1; it <= this.creasePattern.getPointsTotal(); it++) {
            tnew[it].reset();
            for (int im = 1; im <= this.creasePattern.getFacesTotal(); im++) {
                if (this.creasePattern.Point_moti_determine(im, it) == 1) {//c.Ten_moti_hantei returns 1 if the boundary of Face [im] contains Point [it], 0 if it does not.
                    tnew[it].addPoint(fold_movement(it, im));
                    creasePattern.setPoint(it, tnew[it].getAveragePoint());
                }
            }
        }
        System.out.println("折ったときの点の位置を求めた（終了）");

        return creasePattern;
    }

    //------------------------------------------------------------
    private Point fold_movement(int it, int im) { //A function that finds the position of the destination when the point it is folded as a member of the surface im

        Point p = new Point();
        p.set(creasePattern.getPoint(it));
        int idestination_faceId;
        idestination_faceId = im;//The id number of the first face. From now on, we will follow the planes adjacent to the reference plane.
        while (idestination_faceId != referencePlane_id) {
            //p.set(sentaisyou_ten_motome(c.getBou(kyoukaiBouid[idestination_faceId]),p));
            p.set(lineSymmetry_point_determine(associatedStickId[idestination_faceId], p));
            idestination_faceId = nextFaceId[idestination_faceId];
        }
        return p;
    }


// ***********************************

    //Folding estimation (What you can do here is a wire diagram that does not consider the overlap of surfaces)
    public CreasePattern surface_position_request() {//Folding estimate
        CreasePattern cn = new CreasePattern();    //展開図
        cn.configure(creasePattern.getPointsTotal(), creasePattern.getSticksTotal(), creasePattern.getFacesTotal());
        cn.set(creasePattern);

        for (int i = 0; i <= creasePattern.getFacesTotal(); i++) {
            nextFaceId[i] = 0;
            associatedStickId[i] = 0;
            iFacePosition[i] = 0;
        }
        //Grasp the positional relationship between the faces in preparation for folding
        System.out.println("折りたたみの準備として面同士の位置関係を把握する");
        iFacePosition[referencePlane_id] = 1;

        int current_FacePosition = 1;
        int remaining_facesTotal;
        remaining_facesTotal = creasePattern.getFacesTotal() - 1;

        while (remaining_facesTotal > 0) {
            for (int i = 1; i <= creasePattern.getFacesTotal(); i++) {
                if (iFacePosition[i] == current_FacePosition) {
                    for (int j = 1; j <= creasePattern.getFacesTotal(); j++) {
                        int mth = creasePattern.Face_tonari_hantei(i, j);
                        if ((mth > 0) && (iFacePosition[j] == 0)) {
                            iFacePosition[j] = current_FacePosition + 1;
                            nextFaceId[j] = i;
                            associatedStickId[j] = mth;
                        }
                    }
                }
            }

            current_FacePosition = current_FacePosition + 1;

            remaining_facesTotal = 0;
            for (int i = 1; i <= creasePattern.getFacesTotal(); i++) {
                if (iFacePosition[i] == 0) {
                    remaining_facesTotal = remaining_facesTotal + 1;
                }
            }

            System.out.println("remaining_facesTotal = " + remaining_facesTotal);
        }

        return cn;
    }


// **********************************


    private Point lineSymmetry_point_determine(int bouid, Point point) {//Given the id of the bar and any point, returns the point that is axisymmetric of the given point with respect to the corresponding bar.
        return oc.lineSymmetry_point_find(creasePattern.getBeginPointFromStickId(bouid), creasePattern.getEndPointFromStickId(bouid), point);
    }


    public int getPointsTotal() {
        return creasePattern.getPointsTotal();
    }


    //-------------------------------------------
    public void set(CreasePattern ts) {
        configure(ts.getPointsTotal(), ts.getSticksTotal(), ts.getFacesTotal());
        creasePattern.configure(ts.getPointsTotal(), ts.getSticksTotal(), ts.getFacesTotal());
        creasePattern.set(ts);
    }

    public CreasePattern get() {
        return creasePattern;
    }

    //------------------
    public WireFrame getLineStore() {
        WireFrame ss = new WireFrame();    //Instantiation of basic branch structure

        ss.setTotal(creasePattern.getSticksTotal());
        for (int i = 1; i <= creasePattern.getSticksTotal(); i++) {
            ss.set(i, creasePattern.getPoint(creasePattern.getBegin(i)), creasePattern.getPoint(creasePattern.getEnd(i)), creasePattern.getColor(i), 0);
        }
        return ss;
    }


    //　ここは class Tenkaizu_Syokunin  の中です

    //--------------------------------------------------------------------------
    public void lineStore2pointStore(WireFrame wireFrame) {

        Point ti = new Point();
        reset();

        //まず、Tensyuugou内で点を定義する。
        System.out.println("線分集合->点集合：点集合内で点の定義");
        int flag1;
        double x, y;

        double[] addPointX = new double[wireFrame.getTotal() + 1];//If you do not add +1 you will get an error when the number of faces is 1.
        double[] addPointY = new double[wireFrame.getTotal() + 1];//If you do not add +1 you will get an error when the number of faces is 1.
        int addPointNum = 0;

        for (int i = 1; i <= wireFrame.getTotal(); i++) {
            flag1 = 0;
            ti = wireFrame.getA(i);
            x = ti.getX();
            y = ti.getY();

            for (int j = 1; j <= addPointNum; j++) {
                if (oc.equal(ti, new Point(addPointX[j], addPointY[j]))) {
                    flag1 = 1;
                }
            }


            if (flag1 == 0) {
                addPointNum = addPointNum + 1;
                addPointX[addPointNum] = x;
                addPointY[addPointNum] = y;
            }
            flag1 = 0;
            ti = wireFrame.getB(i);
            x = ti.getX();
            y = ti.getY();

            for (int j = 1; j <= addPointNum; j++) {
                if (oc.equal(ti, new Point(addPointX[j], addPointY[j]))) {
                    flag1 = 1;
                }
            }


            if (flag1 == 0) {
                //	c.addTen(x,y);
                addPointNum = addPointNum + 1;
                addPointX[addPointNum] = x;
                addPointY[addPointNum] = y;

            }
        }

        System.out.print("点の全数　addPointNum＝　");
        System.out.println(addPointNum);//System.out.println(c.getTensuu());

        configure(addPointNum, wireFrame.getTotal(), wireFrame.getTotal() - addPointNum + 100);//<< It may be better to have more room here to ensure redundancy. Consideration required 20150315
        creasePattern.configure(addPointNum, wireFrame.getTotal(), wireFrame.getTotal() - addPointNum + 100);//<< It may be better to have more room here to ensure redundancy. Consideration required 20150315

        for (int i = 1; i <= addPointNum; i++) {
            creasePattern.addPoint(addPointX[i], addPointY[i]);

        }

        //次に、Tensyuugou内で棒を定義する。
        System.out.println("線分集合->点集合：点集合内で棒の定義");

        int[] ika2ic = new int[wireFrame.getTotal() + 1];
        int[] ikb2ic = new int[wireFrame.getTotal() + 1];
        for (int n = 1; n <= wireFrame.getTotal(); n++) {
            for (int i = 1; i <= creasePattern.getPointsTotal(); i++) {
                if (oc.equal(wireFrame.getA(n), creasePattern.getPoint(i))) {
                    ika2ic[n] = i;
                    break;
                }
            }
            for (int i = 1; i <= creasePattern.getPointsTotal(); i++) {
                if (oc.equal(wireFrame.getB(n), creasePattern.getPoint(i))) {
                    ikb2ic[n] = i;
                    break;
                }
            }
        }

        for (int n = 1; n <= wireFrame.getTotal(); n++) {
            creasePattern.addStick(ika2ic[n], ikb2ic[n], wireFrame.getColor(n));
        }


        System.out.print("棒の全数　＝　");
        System.out.println(creasePattern.getSticksTotal());
        //
        System.out.println("線分集合->点集合：点集合内で面を発生　開始");
        //その次に、Tensyuugou内で面を発生させる。
        creasePattern.FaceOccurrence();

        System.out.println("線分集合->点集合：点集合内で面を発生　終了");

    }


//----------------------------------------------------------------------------------------------------------------------------------------    

    //棒ibを境界として含む面(最大で2面ある)のうちでMenidの小さいほうのMenidを返す。棒を境界として含む面が無い場合は0を返す
    public int Stick_moti_FaceId_min_request(int ib) {
        return creasePattern.Stick_moti_Menid_min_motome(ib);
    }

    //棒ibを境界として含む面(最大で2面ある)のうちでMenidの大きいほうのMenidを返す。棒を境界として含む面が無い場合は0を返す
    public int Bou_moti_Menid_max_motome(int ib) {
        return creasePattern.Stick_moti_Menid_max_motome(ib);
    }

    //　ここは class Tenkaizu_Syokunin  の中です


    //マウス操作(ボタンを押したとき)時の作業----------------------------------------------------
    public int mPressed(Point p, int ura_omote) {
        //マウスと近い位置にあるTenを探す。
        Point pn = new Point();
        pn.set(p);
        if (ura_omote == 1) {
            pn.setX(-p.getX() + 2.0 * 700.0);
        }//裏側表示の場合の処理。
        i_ugokasuTen = creasePattern.closestPointId(pn, r * 3);
        return i_ugokasuTen;
    }

    //マウス操作(ドラッグしたとき)を行う関数----------------------------------------------------
    public int mDragged(Point p, int ura_omote) {


        if (i_ugokasuTen != 0) {//Tenを変更

            Point pn = new Point();
            pn.set(p);
            if (ura_omote == 1) {
                pn.setX(-p.getX() + 2.0 * 700.0);
            }//裏側表示の場合の処理。
            creasePattern.set(i_ugokasuTen, pn);
        }
        return i_ugokasuTen;
    }


    //マウス操作(ボタンを離したとき)を行う関数----------------------------------------------------
    public int mReleased(Point p, int ura_omote) {
        int ireturn = 0;

        if (i_ugokasuTen != 0) {
            Point pn = new Point();
            pn.set(p);
            if (ura_omote == 1) {
                pn.setX(-p.getX() + 2.0 * 700.0);
            }//裏側表示の場合の処理。
            creasePattern.set(i_ugokasuTen, pn);
        } //Tenを変更
        ireturn = i_ugokasuTen;
        i_ugokasuTen = 0;
        return ireturn;

    }


//マウス操作 with camera

    //マウス操作(ボタンを押したとき)時の作業----------------------------------------------------


    public int mPressed_with_camera(Point p0, int ip4) {
        Point p = new Point();
        if (ip4 == 0) {
            p.set(cam_front.TV2object(p0));
        }
        if (ip4 == 1) {
            p.set(cam_rear.TV2object(p0));
        }


        //マウスと近い位置にあるTenを探す。
        //Ten pn = new Ten(); pn.set(p);
        //i_ugokasuTen=c.mottomo_tikai_Tenid(pn,r*3);

        i_ugokasuTen = creasePattern.closestPointId(p, r * 3);
        return i_ugokasuTen;
    }


    //--------------------------------------------------
    public void mDragged_sentakuten_ugokasi_with_camera(Point p0, int ip4) {   //選択された点を動かす

        Point p = new Point();
        if (ip4 == 0) {
            p.set(cam_front.TV2object(p0));
        }
        if (ip4 == 1) {
            p.set(cam_rear.TV2object(p0));
        }

        creasePattern.statePointMove(p);
    }


    //--------------------------------------------------
    public void mDragged_sentakuten_ugokasi_with_camera(Point ugokasu_maeno_sentaku_point, Point p0, Point p1, int ip4) {   //選択された点を動かす

        Point pa = new Point();
        if (ip4 == 0) {
            pa.set(cam_front.TV2object(p0));
        }
        if (ip4 == 1) {
            pa.set(cam_rear.TV2object(p0));
        }

        Point pb = new Point();
        if (ip4 == 0) {
            pb.set(cam_front.TV2object(p1));
        }
        if (ip4 == 1) {
            pb.set(cam_rear.TV2object(p1));
        }


        Point p_u = new Point();
        p_u.set(ugokasu_maeno_sentaku_point.getX(), ugokasu_maeno_sentaku_point.getY());
        p_u.move(pa.other_Point_position(pb));

        creasePattern.statePointMove(p_u);
    }

    //--------------------------------------------------
    public void mReleased_sentakuten_ugokasi_with_camera(Point ugokasu_maeno_sentaku_point, Point p0, Point p1, int ip4) {   //選択された点を動かす

        Point pa = new Point();
        if (ip4 == 0) {
            pa.set(cam_front.TV2object(p0));
        }
        if (ip4 == 1) {
            pa.set(cam_rear.TV2object(p0));
        }

        Point pb = new Point();
        if (ip4 == 0) {
            pb.set(cam_front.TV2object(p1));
        }
        if (ip4 == 1) {
            pb.set(cam_rear.TV2object(p1));
        }


        Point p_u = new Point();
        p_u.set(ugokasu_maeno_sentaku_point.getX(), ugokasu_maeno_sentaku_point.getY());
        p_u.move(pa.other_Point_position(pb));

        creasePattern.statePointMove(p_u);
    }

    //マウス操作(ボタンを離したとき)を行う関数----------------------------------------------------


    public int mReleased_with_camera(Point p0, int ip4) {

        Point p = new Point();
        if (ip4 == 0) {
            p.set(cam_front.TV2object(p0));
        }
        if (ip4 == 1) {
            p.set(cam_rear.TV2object(p0));
        }


        int ireturn = 0;

        if (i_ugokasuTen != 0) {
            creasePattern.set(i_ugokasuTen, p);
        } //Tenを変更


        ireturn = i_ugokasuTen;
        i_ugokasuTen = 0;
        return ireturn;
    }

// -----------------------

    public void mReleased_sentakuten_ugokasi_with_camera(Point p0, int ip4) {

        Point p = new Point();
        if (ip4 == 0) {
            p.set(cam_front.TV2object(p0));
        }
        if (ip4 == 1) {
            p.set(cam_rear.TV2object(p0));
        }

        creasePattern.statePointMove(p);
    }


    //Numerical conversion function when drawing a figure -----------------------------------------------------------------

    public int gx(double d) {
        return (int) d;
    }

    public int gy(double d) {
        return (int) d;
    }

    //Drawing of development view -----------------------------------------------------------------

    //　ここは class Tenkaizu_Syokunin  の中です

    public void drawing(Graphics g) {
        String text = "";//文字列処理用のクラスのインスタンス化
        //int ir=(int)r;
        //	for (int i=1; i<=c.getTensuu(); i++ ){ g.drawOval( gx(c.getTenx(i)-r),gy(c.getTeny(i)-r),2*ir,2*ir);} //円
        //	for (int i=1; i<=c.getTensuu(); i++ ){ g.drawOval( gx(tnew[i].getx()-r),gy(tnew[i].gety()-r),2*ir,2*ir);} //円
        g.setColor(Color.black);
        //	for (int i=1; i<=c.getTensuu(); i++ ){ g.drawString( text.valueOf(i),gx(c.getTenx(i)),gy(c.getTeny(i)));}
        for (int i = 1; i <= creasePattern.getSticksTotal(); i++) {
            if (creasePattern.getColor(i) == 0) {
                g.setColor(Color.black);
            }
            if (creasePattern.getColor(i) == 1) {
                g.setColor(Color.red);
            }
            if (creasePattern.getColor(i) == 2) {
                g.setColor(Color.blue);
            }
            g.drawLine(gx(creasePattern.getBeginX(i)), gy(creasePattern.getBeginY(i)), gx(creasePattern.getEndX(i)), gy(creasePattern.getEndY(i))); //直線
        }
    }


    //展開図の描画 with camera-----------------------------------------------------------------

    public void drawing_with_camera(Graphics g) {
        LineSegment s_tv = new LineSegment();
        g.setColor(Color.black);
        for (int i = 1; i <= creasePattern.getSticksTotal(); i++) {
            if (creasePattern.getColor(i) == 0) {
                g.setColor(Color.black);
            }
            if (creasePattern.getColor(i) == 1) {
                g.setColor(Color.red);
            }
            if (creasePattern.getColor(i) == 2) {
                g.setColor(Color.blue);
            }

            s_tv.set(camera.object2TV(creasePattern.getLineSegmentFromStickId(i)));

            g.drawLine(gx(s_tv.getAX()), gy(s_tv.getAY()), gx(s_tv.getBX()), gy(s_tv.getBY())); //直線
        }
    }

    // ------------------------------
    public void drawing_pointId_with_camera(Graphics g, int i) {    //点を描く
        Point tn = new Point();
        tn.set(camera.object2TV(creasePattern.getPoint(i)));
        int radius = 7;//radius
        g.setColor(new Color(0, 255, 255, 100));//light blue
        g.fillOval(gx(tn.getX()) - radius, gy(tn.getY()) - radius, 2 * radius, 2 * radius); //円
    }

    // ------------------------------
    public void drawing_pointId_with_camera_green(Graphics g, int i) {    //点を描く
        Point tn = new Point();
        tn.set(camera.object2TV(creasePattern.getPoint(i)));
        int radius = 15;//半径
        g.setColor(new Color(0, 255, 0, 100));//green
        g.fillOval(gx(tn.getX()) - radius, gy(tn.getY()) - radius, 2 * radius, 2 * radius); //円
    }

    public void drawing_pointId_with_camera(Graphics g, int i, int ip4) {
        //点を描く
        Point point = new Point();
        point.set(camera.object2TV(creasePattern.getPoint(i)));
        int radius = 10;//半径
        g.setColor(new Color(0, 255, 0, 50));//緑色

        if (ip4 == 0) {
            point.set(cam_front.object2TV(creasePattern.getPoint(i)));
            g.fillOval(gx(point.getX()) - radius, gy(point.getY()) - radius, 2 * radius, 2 * radius); //円
        }
        if (ip4 == 1) {
            point.set(cam_rear.object2TV(creasePattern.getPoint(i)));
            g.fillOval(gx(point.getX()) - radius, gy(point.getY()) - radius, 2 * radius, 2 * radius); //円
        }
        if ((ip4 == 2) || (ip4 == 3)) {
            point.set(cam_front.object2TV(creasePattern.getPoint(i)));
            g.fillOval(gx(point.getX()) - radius, gy(point.getY()) - radius, 2 * radius, 2 * radius); //円
            point.set(cam_rear.object2TV(creasePattern.getPoint(i)));
            g.fillOval(gx(point.getX()) - radius, gy(point.getY()) - radius, 2 * radius, 2 * radius); //円
        }
    }


    public void drawing_with_camera(Graphics g, int ip4) {
        LineSegment s_tv = new LineSegment();
        g.setColor(Color.black);
        for (int i = 1; i <= creasePattern.getSticksTotal(); i++) {
            if (creasePattern.getColor(i) == 0) {
                g.setColor(Color.black);
            }
            if (creasePattern.getColor(i) == 1) {
                g.setColor(Color.red);
            }
            if (creasePattern.getColor(i) == 2) {
                g.setColor(Color.blue);
            }

            if (ip4 == 0) {
                s_tv.set(cam_front.object2TV(creasePattern.getLineSegmentFromStickId(i)));
                g.drawLine(gx(s_tv.getAX()), gy(s_tv.getAY()), gx(s_tv.getBX()), gy(s_tv.getBY())); //Straight line
            }
            if (ip4 == 1) {
                s_tv.set(cam_rear.object2TV(creasePattern.getLineSegmentFromStickId(i)));
                g.drawLine(gx(s_tv.getAX()), gy(s_tv.getAY()), gx(s_tv.getBX()), gy(s_tv.getBY())); //Straight line
            }
            if ((ip4 == 2) || (ip4 == 3)) {
                s_tv.set(cam_front.object2TV(creasePattern.getLineSegmentFromStickId(i)));
                g.drawLine(gx(s_tv.getAX()), gy(s_tv.getAY()), gx(s_tv.getBX()), gy(s_tv.getBY())); //Straight line
                s_tv.set(cam_rear.object2TV(creasePattern.getLineSegmentFromStickId(i)));
                g.drawLine(gx(s_tv.getAX()), gy(s_tv.getAY()), gx(s_tv.getBX()), gy(s_tv.getBY())); //Straight line
            }
        }
    }


    public void drawing_referencePlane_with_camera(Graphics g) {
        //面内部の点を描く
        Point point = new Point();
        point.set(camera.object2TV(point_of_referencePlane_ob));

        g.setColor(new Color(200, 50, 255, 90));
        g.fillOval(gx(point.getX()) - 50, gy(point.getY()) - 50, 100, 100); //円

        g.setColor(Color.yellow);
        g.fillOval(gx(point.getX()) - 5, gy(point.getY()) - 5, 10, 10); //円
        g.setColor(Color.black);
        g.drawOval(gx(point.getX()) - 5, gy(point.getY()) - 5, 10, 10); //円
        g.setColor(Color.black);
    }


    //---------------------------------------------------
    public void Point_match(double r) {
        creasePattern.Point_match(r);
    }

    public void Point_Stick_match(double r) {
        creasePattern.Point_Stick_match(r);
    }

    //-------------------------------------------------


    public int getSelectedPointsNum() {
        return creasePattern.getSelectedPointsNum();
    }


    //--------------------
    public void setPointState1(int i) {
        creasePattern.setPointState1(i);
    }

    //--------------------
    public void setPointState0(int i) {
        creasePattern.setPointState0(i);
    }

    //--------------------
    public void setAllPointState0() {
        creasePattern.setAllPointState0();
    }

    //--------------------
    public void changePointState(int i) {
        creasePattern.changePointState(i);
    }

    //--------------------
    public byte getPointState(int i) {
        return creasePattern.getPointState(i);
    }//i番目の点の選択状態が０か１かを得る

    //-----------------uuuuuuu---


    double d_h_k = 10.0;//一定の距離より近い近傍かの判定距離

    //与えられた座標と一定の距離より近い近傍にあって、かつ最も近い点の番号を返す。もし、一定の距離以内にTenがないなら0を返す。
    public int closestPointId_with_camera(Point p0) {//展開図用
        Point p = new Point();
        p.set(camera.TV2object(p0));

        return creasePattern.closestPointId(p, d_h_k / camera.getCameraZoomX());
    }


    //与えられた座標と一定の距離より近い近傍にあって、かつ最も近い点の番号を返す。もし、一定の距離以内にTenがないなら0を返す。
    public int closestPointId_with_camera(Point p0, int ip4) {//折り上がり図用
        Point p = new Point();
        if (ip4 == 0) {
            p.set(cam_front.TV2object(p0));
            return creasePattern.closestPointId(p, d_h_k / cam_front.getCameraZoomX());
        }
        if (ip4 == 1) {
            p.set(cam_rear.TV2object(p0));
            return creasePattern.closestPointId(p, d_h_k / cam_rear.getCameraZoomX());
        }
        return 0;
    }


    //--------------------

    //与えられた座標と一定の距離より近い近傍にあって、かつ最も近い点の距離を返す。もし、一定の距離以内にTenがないなら1000000.0を返す。
    public double closest_point_distance_with_camera(Point p0) {//p0はTV座標。リターンされるのはobでの距離
        Point p = new Point();
        p.set(camera.TV2object(p0));
        return creasePattern.closest_Point_distance(p, d_h_k / camera.getCameraZoomX());
    }


    //与えられた座標と一定の距離より近い近傍にあって、かつ最も近い点の距離を返す。もし、一定の距離以内にTenがないなら1000000.0を返す。
    public double closest_point_distance_with_camera(Point p0, int ip4) {//p0はTV座標。リターンされるのはobでの距離
        Point p = new Point();
        if (ip4 == 0) {
            p.set(cam_front.TV2object(p0));
            return creasePattern.closest_Point_distance(p, d_h_k / cam_front.getCameraZoomX());
        }
        if (ip4 == 1) {
            p.set(cam_rear.TV2object(p0));
            return creasePattern.closest_Point_distance(p, d_h_k / cam_rear.getCameraZoomX());
        }
        return 1000000.0;
    }

    // ------------------------------
    public Point getPoint(int i) {
        return creasePattern.getPoint(i);
    }


    // ---------------------------------------------------------------------------------------------
    public void setUndoBoxUndoTotal(int i) {
        Ubox.set_i_undo_total(i);
    }


    public void record() {
        Ubox.record(getMemo());
    }


    public void undo() {
        setMemo_for_redo_undo(Ubox.undo());
    }


    public void redo() {
        setMemo_for_redo_undo(Ubox.redo());
    }


    public Memo getMemo() {
        return creasePattern.getMemo();
    }


    public void setMemo_for_redo_undo(Memo memo1) {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<undo,redoでのkiroku復元用
        creasePattern.setMemo(memo1);
    }


//折り上がり図変形で変更されるのは　c.sentaku_ten_move(p_u);cはTensyuugou


}   //　 class Tenkaizu_Syokunin  はここまでです
