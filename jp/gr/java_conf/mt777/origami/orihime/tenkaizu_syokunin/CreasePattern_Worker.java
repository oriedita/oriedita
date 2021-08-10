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
    double r = 3.0;                   //基本枝構造の直線の両端の円の半径、枝と各種ポイントの近さの判定基準

    CreasePattern c = new CreasePattern();    //展開図

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
    Camera cam_omote = new Camera();
    Camera cam_ura = new Camera();

    Camera cam_touka_omote = new Camera();
    Camera cam_touka_ura = new Camera();


    public Point point_of_referencePlane_ob = new Point();

    //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

    public CreasePattern_Worker(double r0) {  //コンストラクタ
        //	 reset();
        r = r0;
    }

    public void reset() {
        r = 3.0;
        c.reset();
        //for(int i=0;i<=c.getTensuu();i++){tnew[i].reset();}

        camera.reset();
        cam_omote.reset();
        cam_ura.reset();


        cam_touka_omote.reset();
        cam_touka_ura.reset();

    }

    private void configure(int Tsuu, int Bsuu, int Msuu) {
        AverageCoordinates[] t_new = new AverageCoordinates[Tsuu + 1];
        tnew = t_new;
        for (int i = 0; i <= Tsuu; i++) {
            tnew[i] = new AverageCoordinates();
        }
        int[] i_Meniti = new int[Msuu + 1];
        iFacePosition = i_Meniti;
        int[] tonari_Menid = new int[Msuu + 1];
        nextFaceId = tonari_Menid;         //ある面の隣の面（基準面側）のid
        int[] kyoukai_Bouid = new int[Msuu + 1];
        associatedStickId = kyoukai_Bouid;         //ある面と隣の面（基準面側）の間の棒のid
    }

    //---------------
    //public void Iti_sitei(double x, double y){c.Iti_sitei(x ,y);}

    //-----------
    public void add_kijyunmen_id() {
        referencePlane_id = referencePlane_id + 1;
        if (referencePlane_id > c.getFacesTotal()) {
            referencePlane_id = 1;
        }
    }


    //-------------------------------------------
    public int get_kijyunmen_id() {
        return referencePlane_id;
    }


    //-------------------------------------------
    public Point get_kijyunmen_migiue_Ten() {

        return c.get_men_migiue_Ten(referencePlane_id);

    }

    //-------------------------------------------
    public Point get_ten_of_kijyunmen_ob() {

        return point_of_referencePlane_ob;

    }

    //-------------------------------------------
    public Point get_ten_of_kijyunmen_tv() {

        return camera.object2TV(point_of_referencePlane_ob);

    }


    //-------------------------------------------
    public int set_referencePlane_id(int i) {
        referencePlane_id = i;


        if (referencePlane_id > c.getFacesTotal()) {
            referencePlane_id = c.getFacesTotal();
        }
        if (referencePlane_id < 1) {
            referencePlane_id = 1;
        }

        point_of_referencePlane_ob = c.insidePoint_surface(referencePlane_id);


        return referencePlane_id;
    }


    //-----------これは基準面指定モードでマウスを押されたときの対応201503
    public int set_referencePlane_id(Point p0) {//実際に有効になっている基準面idを返す
        Point p = new Point();
        p.set(camera.TV2object(p0));
        if (c.inside(p) > 0) {
            referencePlane_id = c.inside(p);
            point_of_referencePlane_ob.set(p);
        }//c.naibu(p)=0ならどの面の内部にもない、マイナスなら境界線上、正の数なら内部。該当する面番号が複数ある場合は番号の小さいほうが返される。
        return referencePlane_id;
    }


    //-----------Ten p0が折り上がり図の内部に有るかどうかを判定する
    public int naibu_hantei(Point p0) {//実際にp0がある面idを返す
        Point p = new Point();
        p.set(camera.TV2object(p0));
        return c.inside(p);//c.naibu(p)=0ならどの面の内部にもない、マイナスなら境界線上、正の数なら内部。該当する面番号が複数ある場合は番号の小さいほうが返される。
    }

    //-----------Ten p0が折り上がり図(表)の内部に有るかどうかを判定する
    public int naibu_hantei_omote(Point p0) {//実際にp0がある面idを返す
        Point p = new Point();
        p.set(cam_omote.TV2object(p0));
        return c.inside(p);//Tensyuugou c.naibu(p)=0ならどの面の内部にもない、マイナスなら境界線上、正の数なら内部。該当する面番号が複数ある場合は番号の小さいほうが返される。
    }

    //-----------Ten p0が折り上がり図(裏)の内部に有るかどうかを判定する
    public int naibu_hantei_ura(Point p0) {//実際にp0がある面idを返す
        Point p = new Point();
        p.set(cam_ura.TV2object(p0));
        return c.inside(p);//Tensyuugou c.naibu(p)=0ならどの面の内部にもない、マイナスなら境界線上、正の数なら内部。該当する面番号が複数ある場合は番号の小さいほうが返される。
    }

    //-----------Ten p0が折り上がり図に付属して表示される透過図(表)の内部に有るかどうかを判定する
    public int naibu_hantei_touka_omote(Point p0) {//実際にp0がある面idを返す
        Point p = new Point();
        p.set(cam_touka_omote.TV2object(p0));
        return c.inside(p);//Tensyuugou c.naibu(p)=0ならどの面の内部にもない、マイナスなら境界線上、正の数なら内部。該当する面番号が複数ある場合は番号の小さいほうが返される。
    }

    //-----------Ten p0が折り上がり図に付属して表示される透過図(裏)の内部に有るかどうかを判定する
    public int naibu_hantei_touka_ura(Point p0) {//実際にp0がある面idを返す
        Point p = new Point();
        p.set(cam_touka_ura.TV2object(p0));
        return c.inside(p);//Tensyuugou c.naibu(p)=0ならどの面の内部にもない、マイナスなら境界線上、正の数なら内部。該当する面番号が複数ある場合は番号の小さいほうが返される。
    }


    //--------------------------------------------
    public void set_r(double r0) {
        r = r0;
    }

    //--------------------------------------------
    public void setCamera(Camera cam0) {
        camera.set_camera_kagami(cam0.get_camera_kagami());
        camera.setCameraPositionX(cam0.getCameraPositionX());
        camera.setCameraPositionY(cam0.getCameraPositionY());
        camera.setCameraZoomX(cam0.getCameraZoomX());
        camera.setCameraZoomY(cam0.getCameraZoomY());
        camera.setCameraAngle(cam0.getCameraAngle());
        camera.setDisplayPositionX(cam0.getDisplayPositionX());
        camera.setDisplayPositionY(cam0.getDisplayPositionY());
    }


    public void setCam_front(Camera cam0) {
        cam_omote.set_camera_kagami(cam0.get_camera_kagami());
        cam_omote.setCameraPositionX(cam0.getCameraPositionX());
        cam_omote.setCameraPositionY(cam0.getCameraPositionY());
        cam_omote.setCameraZoomX(cam0.getCameraZoomX());
        cam_omote.setCameraZoomY(cam0.getCameraZoomY());
        cam_omote.setCameraAngle(cam0.getCameraAngle());
        cam_omote.setDisplayPositionX(cam0.getDisplayPositionX());
        cam_omote.setDisplayPositionY(cam0.getDisplayPositionY());
    }

    public void setCam_rear(Camera cam0) {
        cam_ura.set_camera_kagami(cam0.get_camera_kagami());
        cam_ura.setCameraPositionX(cam0.getCameraPositionX());
        cam_ura.setCameraPositionY(cam0.getCameraPositionY());
        cam_ura.setCameraZoomX(cam0.getCameraZoomX());
        cam_ura.setCameraZoomY(cam0.getCameraZoomY());
        cam_ura.setCameraAngle(cam0.getCameraAngle());
        cam_ura.setDisplayPositionX(cam0.getDisplayPositionX());
        cam_ura.setDisplayPositionY(cam0.getDisplayPositionY());
    }


    public void setCam_transparent_front(Camera cam0) {
        cam_touka_omote.set_camera_kagami(cam0.get_camera_kagami());
        cam_touka_omote.setCameraPositionX(cam0.getCameraPositionX());
        cam_touka_omote.setCameraPositionY(cam0.getCameraPositionY());
        cam_touka_omote.setCameraZoomX(cam0.getCameraZoomX());
        cam_touka_omote.setCameraZoomY(cam0.getCameraZoomY());
        cam_touka_omote.setCameraAngle(cam0.getCameraAngle());
        cam_touka_omote.setDisplayPositionX(cam0.getDisplayPositionX());
        cam_touka_omote.setDisplayPositionY(cam0.getDisplayPositionY());
    }

    public void setCam_transparent_rear(Camera cam0) {
        cam_touka_ura.set_camera_kagami(cam0.get_camera_kagami());
        cam_touka_ura.setCameraPositionX(cam0.getCameraPositionX());
        cam_touka_ura.setCameraPositionY(cam0.getCameraPositionY());
        cam_touka_ura.setCameraZoomX(cam0.getCameraZoomX());
        cam_touka_ura.setCameraZoomY(cam0.getCameraZoomY());
        cam_touka_ura.setCameraAngle(cam0.getCameraAngle());
        cam_touka_ura.setDisplayPositionX(cam0.getDisplayPositionX());
        cam_touka_ura.setDisplayPositionY(cam0.getDisplayPositionY());
    }


    // ----------------
    public int getMensuu() {
        return c.getFacesTotal();
    }

    //-------------
    public void heikou_idou(double x, double y) {
        c.parallelMove(x, y);
    }

    //
    public void uragaesi() {//重心の位置を中心に左右に裏返す。
        c.turnOver();
    }

    //面の内部の点を求める//---------------------------------------
    public Point naibuTen_motome(int i) {
        return c.insidePoint_surface(i);
    }

    //点集合の持つ棒の総数を得る
    public int getBousuu() {
        return c.getSticksTotal();
    }

    //点集合の持つ棒の色を得る（点集合を展開図として扱う場合では、この色は山谷をあらわす）。
    public int getcolor(int i) {
        return c.getcolor(i);
    }

    //-------------------------------------------
    public int getiMeniti(int i) {
        return iFacePosition[i];
    }

    //-------------------------------------------
    //折りたたみ推定（ここでできるのは面の重なりを考えていない針金図）
    //Folding estimation (What you can do here is a wire diagram that does not consider the overlap of surfaces)
    public CreasePattern folding() {//折りたたみ推定
        CreasePattern creasePattern = new CreasePattern();    //展開図
        creasePattern.configure(c.getPointsTotal(), c.getSticksTotal(), c.getFacesTotal());
        creasePattern.set(c);

        for (int i = 0; i <= c.getFacesTotal(); i++) {
            nextFaceId[i] = 0;
            associatedStickId[i] = 0;
            iFacePosition[i] = 0;
        }
        //Grasp the positional relationship between the faces in preparation for folding
        System.out.println("折りたたみの準備として面同士の位置関係を把握する");
        iFacePosition[referencePlane_id] = 1;

        int current_face_position = 1;
        int remaining_facesTotal;
        remaining_facesTotal = c.getFacesTotal() - 1;

        while (remaining_facesTotal > 0) {
            for (int i = 1; i <= c.getFacesTotal(); i++) {
                if (iFacePosition[i] == current_face_position) {
                    for (int j = 1; j <= c.getFacesTotal(); j++) {
                        int mth = c.Face_tonari_hantei(i, j);
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
            for (int i = 1; i <= c.getFacesTotal(); i++) {
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
        for (int it = 1; it <= c.getPointsTotal(); it++) {
            tnew[it].reset();
            for (int im = 1; im <= c.getFacesTotal(); im++) {
                if (c.Ten_moti_hantei(im, it) == 1) {//c.Ten_moti_hanteiは、Men[im]の境界にTen[it]が含まれるなら1、含まれないなら0を返す
                    tnew[it].addPoint(ori_idou(it, im));
                    creasePattern.setPoint(it, tnew[it].getAveragePoint());
                }
            }
        }
        System.out.println("折ったときの点の位置を求めた（終了）");

        return creasePattern;
    }

    //------------------------------------------------------------
    private Point ori_idou(int it, int im) { //点itが面imの一員として折られた場合の移動先の位置を求める関数

        Point p = new Point();  // p1.set(s.geta());
        p.set(c.getPoint(it));
        int idousakino_Menid;
        idousakino_Menid = im;//最初の面のid番号。これから基準面の方向に隣接する面をたどっていく。
        while (idousakino_Menid != referencePlane_id) {
            //p.set(sentaisyou_ten_motome(c.getBou(kyoukaiBouid[idousakino_Menid]),p));
            p.set(sentaisyou_ten_motome(associatedStickId[idousakino_Menid], p));
            idousakino_Menid = nextFaceId[idousakino_Menid];
        }
        return p;
    }


// ***********************************

    //Folding estimation (What you can do here is a wire diagram that does not consider the overlap of surfaces)
    public CreasePattern surface_position_request() {//Folding estimate

        CreasePattern cn = new CreasePattern();    //展開図
        cn.configure(c.getPointsTotal(), c.getSticksTotal(), c.getFacesTotal());
        cn.set(c);


//System.out.println("折りたたみ推定001   c.getTenx(1) = "+c.getTenx(1)+"   :   cn.getTenx(1) = "+cn.getTenx(1));

        //kijyunmen_id=1;//基準になる面を指定する

        for (int i = 0; i <= c.getFacesTotal(); i++) {
            nextFaceId[i] = 0;
            associatedStickId[i] = 0;
            iFacePosition[i] = 0;
        }
//System.out.println("折りたたみ推定002   c.getTenx(1) = "+c.getTenx(1)+"   :   cn.getTenx(1) = "+cn.getTenx(1));
        //Grasp the positional relationship between the faces in preparation for folding
        System.out.println("折りたたみの準備として面同士の位置関係を把握する");
        iFacePosition[referencePlane_id] = 1;

        int current_FacePosition = 1;
        int remaining_FaceNum;
        remaining_FaceNum = c.getFacesTotal() - 1;

        while (remaining_FaceNum > 0) {
            for (int i = 1; i <= c.getFacesTotal(); i++) {
                if (iFacePosition[i] == current_FacePosition) {
                    for (int j = 1; j <= c.getFacesTotal(); j++) {
                        int mth = c.Face_tonari_hantei(i, j);
                        if ((mth > 0) && (iFacePosition[j] == 0)) {
                            iFacePosition[j] = current_FacePosition + 1;
                            nextFaceId[j] = i;
                            associatedStickId[j] = mth;
                        }
                    }
                }
            }

            current_FacePosition = current_FacePosition + 1;

            remaining_FaceNum = 0;
            for (int i = 1; i <= c.getFacesTotal(); i++) {
                if (iFacePosition[i] == 0) {
                    remaining_FaceNum = remaining_FaceNum + 1;
                }
            }

            System.out.println("remaining_FaceNum = " + remaining_FaceNum);
        }

        return cn;
    }


// **********************************


    private Point sentaisyou_ten_motome(int bouid, Point tn) {//棒のidと、任意の点を与えて、idが対応する棒に対して、与えた点の線対称になる点を返す
        return oc.lineControl_point_find(c.get_maeTen_from_Stick_id(bouid), c.get_atoTen_from_Bou_id(bouid), tn);
    }


    public int getTensuu() {
        return c.getPointsTotal();
    }
//	int getBousuu(){return c.getBousuu();}	
//	int getMensuu(){return c.getMensuu();}	


    //-------------------------------------------
    public void set(CreasePattern ts) {
        configure(ts.getPointsTotal(), ts.getSticksTotal(), ts.getFacesTotal());
        c.configure(ts.getPointsTotal(), ts.getSticksTotal(), ts.getFacesTotal());
        c.set(ts);
//System.out.println("折りたたみset 001   c.getTenx(1) = "+c.getTenx(1));		

    }

    public CreasePattern get() {
        return c;
    }

    //------------------
    public WireFrame getLineStore() {
        WireFrame ss = new WireFrame();    //基本枝構造のインスタンス化

        ss.setTotal(c.getSticksTotal());
        for (int i = 1; i <= c.getSticksTotal(); i++) {
            ss.set(i, c.getPoint(c.getmae(i)), c.getPoint(c.getato(i)), c.getcolor(i), 0);
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
        c.configure(addPointNum, wireFrame.getTotal(), wireFrame.getTotal() - addPointNum + 100);//<< It may be better to have more room here to ensure redundancy. Consideration required 20150315

        for (int i = 1; i <= addPointNum; i++) {
            c.addPoint(addPointX[i], addPointY[i]);

        }

        //次に、Tensyuugou内で棒を定義する。
        System.out.println("線分集合->点集合：点集合内で棒の定義");

        int[] ika2ic = new int[wireFrame.getTotal() + 1];
        int[] ikb2ic = new int[wireFrame.getTotal() + 1];
        for (int n = 1; n <= wireFrame.getTotal(); n++) {
            for (int i = 1; i <= c.getPointsTotal(); i++) {
                if (oc.equal(wireFrame.getA(n), c.getPoint(i))) {
                    ika2ic[n] = i;
                    break;
                }
            }
            for (int i = 1; i <= c.getPointsTotal(); i++) {
                if (oc.equal(wireFrame.getB(n), c.getPoint(i))) {
                    ikb2ic[n] = i;
                    break;
                }
            }
        }

        for (int n = 1; n <= wireFrame.getTotal(); n++) {
            c.addStick(ika2ic[n], ikb2ic[n], wireFrame.getColor(n));
        }


        System.out.print("棒の全数　＝　");
        System.out.println(c.getSticksTotal());
        //
        System.out.println("線分集合->点集合：点集合内で面を発生　開始");
        //その次に、Tensyuugou内で面を発生させる。
        c.FaceOccurrence();

        System.out.println("線分集合->点集合：点集合内で面を発生　終了");

    }


//----------------------------------------------------------------------------------------------------------------------------------------    

    //棒ibを境界として含む面(最大で2面ある)のうちでMenidの小さいほうのMenidを返す。棒を境界として含む面が無い場合は0を返す
    public int Stick_moti_FaceId_min_request(int ib) {
        return c.Stick_moti_Menid_min_motome(ib);
    }

    //棒ibを境界として含む面(最大で2面ある)のうちでMenidの大きいほうのMenidを返す。棒を境界として含む面が無い場合は0を返す
    public int Bou_moti_Menid_max_motome(int ib) {
        return c.Stick_moti_Menid_max_motome(ib);
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
        i_ugokasuTen = c.mottomo_tikai_Tenid(pn, r * 3);
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
            c.set(i_ugokasuTen, pn);
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
            c.set(i_ugokasuTen, pn);
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
            p.set(cam_omote.TV2object(p0));
        }
        if (ip4 == 1) {
            p.set(cam_ura.TV2object(p0));
        }


        //マウスと近い位置にあるTenを探す。
        //Ten pn = new Ten(); pn.set(p);
        //i_ugokasuTen=c.mottomo_tikai_Tenid(pn,r*3);

        i_ugokasuTen = c.mottomo_tikai_Tenid(p, r * 3);
        return i_ugokasuTen;
    }


    //マウス操作(ドラッグしたとき)を行う関数----------------------------------------------------

/*
	public int mDragged_with_camera(Ten p0,int ip4) {   

		Ten p =new Ten(); 
		if(ip4==0){	p.set(cam_omote.TV2object(p0));}
		if(ip4==1){	p.set(cam_ura.TV2object(p0));}
		

		if(i_ugokasuTen!=0){//Tenを変更
                	//Ten pn = new Ten(); pn.set(p); 
			//c.set(i_ugokasuTen,pn);
			c.set(i_ugokasuTen,p);
		} 
		return i_ugokasuTen;
	}
*/

    //--------------------------------------------------
    public void mDragged_sentakuten_ugokasi_with_camera(Point p0, int ip4) {   //選択された点を動かす

        Point p = new Point();
        if (ip4 == 0) {
            p.set(cam_omote.TV2object(p0));
        }
        if (ip4 == 1) {
            p.set(cam_ura.TV2object(p0));
        }

        c.statePointMove(p);
/*
		if(i_ugokasuTen!=0){//Tenを変更
                	//Ten pn = new Ten(); pn.set(p); 
			//c.set(i_ugokasuTen,pn);
			c.set(i_ugokasuTen,p);
		} 
		//return i_ugokasuTen;

*/
    }


    //--------------------------------------------------
    public void mDragged_sentakuten_ugokasi_with_camera(Point ugokasu_maeno_sentaku_point, Point p0, Point p1, int ip4) {   //選択された点を動かす

        Point pa = new Point();
        if (ip4 == 0) {
            pa.set(cam_omote.TV2object(p0));
        }
        if (ip4 == 1) {
            pa.set(cam_ura.TV2object(p0));
        }

        Point pb = new Point();
        if (ip4 == 0) {
            pb.set(cam_omote.TV2object(p1));
        }
        if (ip4 == 1) {
            pb.set(cam_ura.TV2object(p1));
        }


        Point p_u = new Point();
        p_u.set(ugokasu_maeno_sentaku_point.getX(), ugokasu_maeno_sentaku_point.getY());
        p_u.move(pa.other_Point_position(pb));

        c.statePointMove(p_u);


    }

    //--------------------------------------------------
    public void mReleased_sentakuten_ugokasi_with_camera(Point ugokasu_maeno_sentaku_point, Point p0, Point p1, int ip4) {   //選択された点を動かす

        Point pa = new Point();
        if (ip4 == 0) {
            pa.set(cam_omote.TV2object(p0));
        }
        if (ip4 == 1) {
            pa.set(cam_ura.TV2object(p0));
        }

        Point pb = new Point();
        if (ip4 == 0) {
            pb.set(cam_omote.TV2object(p1));
        }
        if (ip4 == 1) {
            pb.set(cam_ura.TV2object(p1));
        }


        Point p_u = new Point();
        p_u.set(ugokasu_maeno_sentaku_point.getX(), ugokasu_maeno_sentaku_point.getY());
        p_u.move(pa.other_Point_position(pb));

        c.statePointMove(p_u);


    }

    //マウス操作(ボタンを離したとき)を行う関数----------------------------------------------------


    public int mReleased_with_camera(Point p0, int ip4) {

        Point p = new Point();
        if (ip4 == 0) {
            p.set(cam_omote.TV2object(p0));
        }
        if (ip4 == 1) {
            p.set(cam_ura.TV2object(p0));
        }


        int ireturn = 0;

        if (i_ugokasuTen != 0) {
            //Ten pn = new Ten(); pn.set(p);
            //c.set(i_ugokasuTen,pn);
            c.set(i_ugokasuTen, p);

        } //Tenを変更


        ireturn = i_ugokasuTen;
        i_ugokasuTen = 0;
        return ireturn;
    }

// -----------------------

    public void mReleased_sentakuten_ugokasi_with_camera(Point p0, int ip4) {

        Point p = new Point();
        if (ip4 == 0) {
            p.set(cam_omote.TV2object(p0));
        }
        if (ip4 == 1) {
            p.set(cam_ura.TV2object(p0));
        }


        c.statePointMove(p);

        //	if(i_ugokasuTen!=0){
//			c.set(i_ugokasuTen,p); 
//		} //Tenを変更

//                i_ugokasuTen=0;
    }


    //図をかく際の数値変換用関数-----------------------------------------------------------------

    public int gx(double d) {
        return (int) d;
    }

    public int gy(double d) {
        return (int) d;
    }

    //展開図の描画-----------------------------------------------------------------

    //　ここは class Tenkaizu_Syokunin  の中です

    public void oekaki(Graphics g) {
        String text = "";//文字列処理用のクラスのインスタンス化
        //int ir=(int)r;
        //	for (int i=1; i<=c.getTensuu(); i++ ){ g.drawOval( gx(c.getTenx(i)-r),gy(c.getTeny(i)-r),2*ir,2*ir);} //円
        //	for (int i=1; i<=c.getTensuu(); i++ ){ g.drawOval( gx(tnew[i].getx()-r),gy(tnew[i].gety()-r),2*ir,2*ir);} //円
        g.setColor(Color.black);
        //	for (int i=1; i<=c.getTensuu(); i++ ){ g.drawString( text.valueOf(i),gx(c.getTenx(i)),gy(c.getTeny(i)));}
        for (int i = 1; i <= c.getSticksTotal(); i++) {
            if (c.getcolor(i) == 0) {
                g.setColor(Color.black);
            }
            if (c.getcolor(i) == 1) {
                g.setColor(Color.red);
            }
            if (c.getcolor(i) == 2) {
                g.setColor(Color.blue);
            }
            g.drawLine(gx(c.getmaex(i)), gy(c.getmaey(i)), gx(c.getatox(i)), gy(c.getatoy(i))); //直線
            //g.drawLine( gx(tnew[c.getmae(i)].getx()),gy(tnew[c.getmae(i)].gety()), gx(tnew[c.getato(i)].getx()),gy(tnew[c.getato(i)].gety())); //直線
        }
    }


    //展開図の描画 with camera-----------------------------------------------------------------

    public void oekaki_with_camera(Graphics g) {
//System.out.println("折りたたみ oekaki 001   c.getTenx(1) = "+c.getTenx(1));	
        LineSegment s_tv = new LineSegment();
        String text = "";//文字列処理用のクラスのインスタンス化
        g.setColor(Color.black);
        for (int i = 1; i <= c.getSticksTotal(); i++) {
            if (c.getcolor(i) == 0) {
                g.setColor(Color.black);
            }
            if (c.getcolor(i) == 1) {
                g.setColor(Color.red);
            }
            if (c.getcolor(i) == 2) {
                g.setColor(Color.blue);
            }

            s_tv.set(camera.object2TV(c.get_Senbun_from_Bou_id(i)));

            g.drawLine(gx(s_tv.getAx()), gy(s_tv.getay()), gx(s_tv.getbx()), gy(s_tv.getby())); //直線
        }
    }

    // ------------------------------
    public void oekaki_Ten_id_with_camera(Graphics g, int i) {    //点を描く
        Point tn = new Point();
        tn.set(camera.object2TV(c.getPoint(i)));
        int ir = 7;//半径
        g.setColor(new Color(0, 255, 255, 100));//水色
        g.fillOval(gx(tn.getX()) - ir, gy(tn.getY()) - ir, 2 * ir, 2 * ir); //円
    }

    // ------------------------------
    public void oekaki_Ten_id_with_camera_green(Graphics g, int i) {    //点を描く
        Point tn = new Point();
        tn.set(camera.object2TV(c.getPoint(i)));
        int ir = 15;//半径
        g.setColor(new Color(0, 255, 0, 100));//緑色
        g.fillOval(gx(tn.getX()) - ir, gy(tn.getY()) - ir, 2 * ir, 2 * ir); //円
    }


//g.setColor(Color.cyan);


    public void oekaki_Ten_id_with_camera(Graphics g, int i, int ip4) {
        //点を描く
        Point tn = new Point();
        tn.set(camera.object2TV(c.getPoint(i)));
        int ir = 10;//半径
        g.setColor(new Color(0, 255, 0, 50));//緑色

        if (ip4 == 0) {
            tn.set(cam_omote.object2TV(c.getPoint(i)));
            g.fillOval(gx(tn.getX()) - ir, gy(tn.getY()) - ir, 2 * ir, 2 * ir); //円
        }
        if (ip4 == 1) {
            tn.set(cam_ura.object2TV(c.getPoint(i)));
            g.fillOval(gx(tn.getX()) - ir, gy(tn.getY()) - ir, 2 * ir, 2 * ir); //円
        }
        if ((ip4 == 2) || (ip4 == 3)) {
            tn.set(cam_omote.object2TV(c.getPoint(i)));
            g.fillOval(gx(tn.getX()) - ir, gy(tn.getY()) - ir, 2 * ir, 2 * ir); //円
            tn.set(cam_ura.object2TV(c.getPoint(i)));
            g.fillOval(gx(tn.getX()) - ir, gy(tn.getY()) - ir, 2 * ir, 2 * ir); //円
        }

    }


    public void oekaki_with_camera(Graphics g, int ip4) {

        LineSegment s_tv = new LineSegment();
        String text = "";//文字列処理用のクラスのインスタンス化
        g.setColor(Color.black);
        for (int i = 1; i <= c.getSticksTotal(); i++) {
            if (c.getcolor(i) == 0) {
                g.setColor(Color.black);
            }
            if (c.getcolor(i) == 1) {
                g.setColor(Color.red);
            }
            if (c.getcolor(i) == 2) {
                g.setColor(Color.blue);
            }

            if (ip4 == 0) {
                s_tv.set(cam_omote.object2TV(c.get_Senbun_from_Bou_id(i)));
                g.drawLine(gx(s_tv.getAx()), gy(s_tv.getay()), gx(s_tv.getbx()), gy(s_tv.getby())); //直線
            }
            if (ip4 == 1) {
                s_tv.set(cam_ura.object2TV(c.get_Senbun_from_Bou_id(i)));
                g.drawLine(gx(s_tv.getAx()), gy(s_tv.getay()), gx(s_tv.getbx()), gy(s_tv.getby())); //直線
            }
            if ((ip4 == 2) || (ip4 == 3)) {
                s_tv.set(cam_omote.object2TV(c.get_Senbun_from_Bou_id(i)));
                g.drawLine(gx(s_tv.getAx()), gy(s_tv.getay()), gx(s_tv.getbx()), gy(s_tv.getby())); //直線
                s_tv.set(cam_ura.object2TV(c.get_Senbun_from_Bou_id(i)));
                g.drawLine(gx(s_tv.getAx()), gy(s_tv.getay()), gx(s_tv.getbx()), gy(s_tv.getby())); //直線
            }


            //g.drawLine( gx(s_tv.getax()),gy(s_tv.getay()),gx(s_tv.getbx()),gy(s_tv.getby())); //直線
        }
    }


    public void oekaki_kijyunmen_id_with_camera(Graphics g) {
        //面内部の点を描く
        Point tn = new Point();
        //tn.reset();
        tn.set(camera.object2TV(point_of_referencePlane_ob));

        g.setColor(new Color(200, 50, 255, 90));
        g.fillOval(gx(tn.getX()) - 50, gy(tn.getY()) - 50, 100, 100); //円

        g.setColor(Color.yellow);
        g.fillOval(gx(tn.getX()) - 5, gy(tn.getY()) - 5, 10, 10); //円
        g.setColor(Color.black);
        g.drawOval(gx(tn.getX()) - 5, gy(tn.getY()) - 5, 10, 10); //円
        g.setColor(Color.black);

    }


    //---------------------------------------------------
    public void Point_match(double r) {
        c.Point_match(r);
    }

    public void Point_Stick_match(double r) {
        c.Point_Stick_match(r);
    }

    //-------------------------------------------------


    public int getSelectedPointsNum() {
        return c.getSelectedPointsNum();
    }


    //--------------------
    public void setPointState1(int i) {
        c.setPointState1(i);
    }

    //--------------------
    public void setPointState0(int i) {
        c.setPointState0(i);
    }

    //--------------------
    public void setAllPointState0() {
        c.setAllPointState0();
    }

    //--------------------
    public void changePointState(int i) {
        c.changePointState(i);
    }

    //--------------------
    public byte getPointState(int i) {
        return c.getPointState(i);
    }//i番目の点の選択状態が０か１かを得る

    //-----------------uuuuuuu---


    double d_h_k = 10.0;//一定の距離より近い近傍かの判定距離

    //与えられた座標と一定の距離より近い近傍にあって、かつ最も近い点の番号を返す。もし、一定の距離以内にTenがないなら0を返す。
    public int mottomo_tikai_Tenid_with_camera(Point p0) {//展開図用
        Point p = new Point();
        p.set(camera.TV2object(p0));

        return c.mottomo_tikai_Tenid(p, d_h_k / camera.getCameraZoomX());
    }


    //与えられた座標と一定の距離より近い近傍にあって、かつ最も近い点の番号を返す。もし、一定の距離以内にTenがないなら0を返す。
    public int mottomo_tikai_Tenid_with_camera(Point p0, int ip4) {//折り上がり図用
        Point p = new Point();
        if (ip4 == 0) {
            p.set(cam_omote.TV2object(p0));
            return c.mottomo_tikai_Tenid(p, d_h_k / cam_omote.getCameraZoomX());
        }
        if (ip4 == 1) {
            p.set(cam_ura.TV2object(p0));
            return c.mottomo_tikai_Tenid(p, d_h_k / cam_ura.getCameraZoomX());
        }
        return 0;
    }


    //--------------------

    //与えられた座標と一定の距離より近い近傍にあって、かつ最も近い点の距離を返す。もし、一定の距離以内にTenがないなら1000000.0を返す。
    public double mottomo_tikai_Ten_kyori_with_camera(Point p0) {//p0はTV座標。リターンされるのはobでの距離
        Point p = new Point();
        p.set(camera.TV2object(p0));
        return c.closest_Point_distance(p, d_h_k / camera.getCameraZoomX());
    }


    //与えられた座標と一定の距離より近い近傍にあって、かつ最も近い点の距離を返す。もし、一定の距離以内にTenがないなら1000000.0を返す。
    public double mottomo_tikai_Ten_kyori_with_camera(Point p0, int ip4) {//p0はTV座標。リターンされるのはobでの距離
        Point p = new Point();
        if (ip4 == 0) {
            p.set(cam_omote.TV2object(p0));
            return c.closest_Point_distance(p, d_h_k / cam_omote.getCameraZoomX());
        }
        if (ip4 == 1) {
            p.set(cam_ura.TV2object(p0));
            return c.closest_Point_distance(p, d_h_k / cam_ura.getCameraZoomX());
        }
        return 1000000.0;
    }

    // ------------------------------
    public Point getTen(int i) {
        return c.getPoint(i);
    }


    // ---------------------------------------------------------------------------------------------
    public void set_Ubox_undo_suu(int i) {
        Ubox.set_i_undo_suu(i);
    }

//String s_title=new String(); //フレームの最上端に出てくるタイトルを保持するために使用

    public void kiroku() {
        Ubox.kiroku(getMemo());
    }


    public void undo() {
        setMemo_for_redo_undo(Ubox.undo());

    }


    public void redo() {
        setMemo_for_redo_undo(Ubox.redo());


    }


    public Memo getMemo() {
        return c.getMemo();
    }


    public void setMemo_for_redo_undo(Memo memo1) {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<undo,redoでのkiroku復元用

        c.setMemo(memo1);
    }


//折り上がり図変形で変更されるのは　c.sentaku_ten_move(p_u);cはTensyuugou


}   //　 class Tenkaizu_Syokunin  はここまでです
