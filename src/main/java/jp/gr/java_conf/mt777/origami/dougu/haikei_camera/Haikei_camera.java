package jp.gr.java_conf.mt777.origami.dougu.haikei_camera;


import jp.gr.java_conf.mt777.zukei2d.ten.*;
import jp.gr.java_conf.mt777.zukei2d.oritacalc.*;
import jp.gr.java_conf.mt777.origami.dougu.camera.*;

public class Haikei_camera {//実際の座標と、表示座標の仲立ち

    //背景画を、画像の左上はしを、ウィンドウの(0,0)に合わせて回転や拡大なしで表示した場合を基準状態とする。
//背景画上の点h1を中心としてa倍拡大する。次に、h1を展開図上の点h3と重なるように背景画を平行移動する。この状態の展開図を、h3を中心にb度回転したよう見えるように座標を回転させて貼り付けて、その後、座標の回転を元に戻すという関数。
//引数は、Graphics2D g2h,Image imgh,Ten h1,Ten h2,Ten h3,Ten h4
//h2,とh4も重なるようにする
    OritaCalc oc = new OritaCalc();
    Point h1 = new Point();
    Point h2 = new Point();
    Point h3 = new Point();
    Point h4 = new Point();

    Point h3_obj = new Point();
    Point h4_obj = new Point();

    double haikei_haba;
    double haikei_takasa;

    Camera camera = new Camera();
//		Ten p =new Ten();p.set(camera.TV2object(p0)) ;

    double p_bairitu = 1.0;
    double p_idou_x = 0.0;
    double p_idou_y = 0.0;
    double p_kaiten_angle = 0.0;
    double p_kaiten_x = 0.0;
    double p_kaiten_y = 0.0;

    int i_Lock_on = 0;

    //int camera_id;//cameraの識別用。名前の変わりに使うだけ

    public Haikei_camera() {//コンストラクタ
        reset();
    }


    public void reset() {
        set_h1(new Point(0.0, 0.0));
        set_h2(new Point(1.0, 1.0));
        set_h3(new Point(120.0, 120.0));
        set_h4(new Point(121.0, 121.0));

        parameter_keisan();

    }


    public void setCamera(Camera cam0) {


        //camera.set_camera_id(cam0.get_camera_id());
        camera.setCameraMirror(cam0.getCameraMirror());


        camera.setCameraPositionX(cam0.getCameraPositionX());
        camera.setCameraPositionY(cam0.getCameraPositionY());
        camera.setCameraZoomX(cam0.getCameraZoomX());
        camera.setCameraZoomY(cam0.getCameraZoomY());
        camera.setCameraAngle(cam0.getCameraAngle());
        camera.setDisplayPositionX(cam0.getDisplayPositionX());
        camera.setDisplayPositionY(cam0.getDisplayPositionY());
    }


    public Point get_kijyun_jyoutai_iti(Point pt) {
        Point pt1 = new Point();
        Point pt2 = new Point();
        Point pt3 = new Point();

        pt1.set(oc.point_rotate(new Point(p_kaiten_x, p_kaiten_y), pt, -get_angle()));
        pt2.set(pt1.getX() - p_idou_x, pt1.getY() - p_idou_y);
        pt3.set(pt2.getX() / p_bairitu, pt2.getY() / p_bairitu);

        return pt3;
    }


    //public void set_h1(Ten ht){h1.set(ht);}
//public void set_h2(Ten ht){h2.set(ht);}
    public void set_h1(Point ht) {
        h1.set(get_kijyun_jyoutai_iti(ht));
    }

    public void set_h2(Point ht) {
        h2.set(get_kijyun_jyoutai_iti(ht));
    }

    public void set_h3(Point ht) {
        h3.set(ht);
    }

    public void set_h4(Point ht) {
        h4.set(ht);
    }

    public void set_h3_obj(Point ht) {
        h3_obj.set(ht);
    }

    public void set_h4_obj(Point ht) {
        h4_obj.set(ht);
    }

    public Point get_h1() {
        return h1;
    }

    public Point get_h2() {
        return h2;
    }

    public Point get_h3() {
        return h3;
    }

    public Point get_h4() {
        return h4;
    }

    public Point get_h3_obj() {
        return h3_obj;
    }

    public Point get_h4_obj() {
        return h4_obj;
    }

    public void parameter_keisan() {
        p_bairitu = h3.distance(h4) / h1.distance(h2);
        p_idou_x = (1.0 - p_bairitu) * h1.getX() + h3.getX() - h1.getX();
        p_idou_y = (1.0 - p_bairitu) * h1.getY() + h3.getY() - h1.getY();
        p_kaiten_angle = oc.angle(h1, h2, h3, h4);
        p_kaiten_x = h3.getX();
        p_kaiten_y = h3.getY();

        oc.display("Haikei_camera--------------------parameter_keisan()");
        h1.display(" h1  ");
        h2.display(" h2  ");
        h3.display(" h3  ");
        h4.display(" h4  ");


    }


    public void set_haikei_haba(double d0) {
        haikei_haba = d0;
    }

    public void set_haikei_takasa(double d0) {
        haikei_takasa = d0;
    }

//　g.drawImage(BufferedImage型変数名,横位置,縦位置,表示幅,表示高さ,this);
//  g.drawImage(image, x0,y0,x1,y1,this);


    public int get_x0() {
        return (int) ((1.0 - p_bairitu) * h1.getX() + h3.getX() - h1.getX());
    }

    public int get_y0() {
        return (int) ((1.0 - p_bairitu) * h1.getY() + h3.getY() - h1.getY());
    }

    public int get_x1() {
        return (int) (haikei_haba * p_bairitu);
    }

    public int get_y1() {
        return (int) (haikei_takasa * p_bairitu);
    }

//public double get_bai(){return h3.kyori(h4)/h1.kyori(h2);}

    public double get_angle() {
        return p_kaiten_angle;
    }

    public int get_cx() {
        return (int) p_kaiten_x;
    }

    public int get_cy() {
        return (int) p_kaiten_y;
    }


    public void set_i_Lock_on(int i_L) {
        i_Lock_on = i_L;
    }

    public void h3_obj_and_h4_obj_keisan() {
        h3_obj.set(camera.TV2object(h3));
        h4_obj.set(camera.TV2object(h4));
    }

    public void h3_and_h4_keisan() {
        h3.set(camera.object2TV(h3_obj));
        h4.set(camera.object2TV(h4_obj));
    }

}


  


