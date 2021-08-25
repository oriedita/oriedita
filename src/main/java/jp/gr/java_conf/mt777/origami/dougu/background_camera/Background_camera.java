package jp.gr.java_conf.mt777.origami.dougu.background_camera;


import jp.gr.java_conf.mt777.graphic2d.point.*;
import jp.gr.java_conf.mt777.graphic2d.oritacalc.*;
import jp.gr.java_conf.mt777.origami.dougu.camera.*;

public class Background_camera {//Mediation between actual coordinates and display coordinates

    // The background image is based on the case where the upper left corner of the image is displayed according to (0,0) of the window without rotation or enlargement.
// Enlarge a times around the point h1 on the background image. Next, the background image is translated so that h1 overlaps the point h3 on the developed view. A function that rotates and pastes the developed view of this state so that it looks like it is rotated b degrees around h3, and then restores the rotation of the coordinates.
// The arguments are Graphics2D g2h, Image imgh, Point h1, Point h2, Point h3, Point h4
// Make h2 and h4 also overlap
    Point h1 = new Point();
    Point h2 = new Point();
    Point h3 = new Point();
    Point h4 = new Point();

    Point h3_obj = new Point();
    Point h4_obj = new Point();

    double background_width;
    double background_height;

    Camera camera = new Camera();

    double p_bairitu = 1.0;
    double p_idou_x = 0.0;
    double p_idou_y = 0.0;
    double p_rotation_angle = 0.0;
    double p_rotation_x = 0.0;
    double p_rotation_y = 0.0;

    boolean i_Lock_on = false;

    public Background_camera() {//コンストラクタ
        reset();
    }

    public void reset() {
        set_h1(new Point(0.0, 0.0));
        set_h2(new Point(1.0, 1.0));
        set_h3(new Point(120.0, 120.0));
        set_h4(new Point(121.0, 121.0));

        parameter_calculation();
    }


    public void setCamera(Camera cam0) {
        camera.setCameraMirror(cam0.getCameraMirror());


        camera.setCameraPositionX(cam0.getCameraPositionX());
        camera.setCameraPositionY(cam0.getCameraPositionY());
        camera.setCameraZoomX(cam0.getCameraZoomX());
        camera.setCameraZoomY(cam0.getCameraZoomY());
        camera.setCameraAngle(cam0.getCameraAngle());
        camera.setDisplayPositionX(cam0.getDisplayPositionX());
        camera.setDisplayPositionY(cam0.getDisplayPositionY());
    }


    public Point get_kijyun_jyoutai_position(Point pt) {
        Point pt1 = new Point();
        Point pt2 = new Point();
        Point pt3 = new Point();

        pt1.set(OritaCalc.point_rotate(new Point(p_rotation_x, p_rotation_y), pt, -get_angle()));
        pt2.set(pt1.getX() - p_idou_x, pt1.getY() - p_idou_y);
        pt3.set(pt2.getX() / p_bairitu, pt2.getY() / p_bairitu);

        return pt3;
    }

    public void set_h1(Point ht) {
        h1.set(get_kijyun_jyoutai_position(ht));
    }

    public void set_h2(Point ht) {
        h2.set(get_kijyun_jyoutai_position(ht));
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

    public void parameter_calculation() {
        p_bairitu = h3.distance(h4) / h1.distance(h2);
        p_idou_x = (1.0 - p_bairitu) * h1.getX() + h3.getX() - h1.getX();
        p_idou_y = (1.0 - p_bairitu) * h1.getY() + h3.getY() - h1.getY();
        p_rotation_angle = OritaCalc.angle(h1, h2, h3, h4);
        p_rotation_x = h3.getX();
        p_rotation_y = h3.getY();

        OritaCalc.display("Haikei_camera--------------------parameter_keisan()");
        h1.display(" h1  ");
        h2.display(" h2  ");
        h3.display(" h3  ");
        h4.display(" h4  ");
    }


    public void setBackgroundWidth(double d0) {
        background_width = d0;
    }

    public void setBackgroundHeight(double d0) {
        background_height = d0;
    }

    public int get_x0() {
        return (int) ((1.0 - p_bairitu) * h1.getX() + h3.getX() - h1.getX());
    }

    public int get_y0() {
        return (int) ((1.0 - p_bairitu) * h1.getY() + h3.getY() - h1.getY());
    }

    public int get_x1() {
        return (int) (background_width * p_bairitu);
    }

    public int get_y1() {
        return (int) (background_height * p_bairitu);
    }

    public double get_angle() {
        return p_rotation_angle;
    }

    public int get_cx() {
        return (int) p_rotation_x;
    }

    public int get_cy() {
        return (int) p_rotation_y;
    }


    public void set_i_Lock_on(boolean i_L) {
        i_Lock_on = i_L;
    }

    public void h3_obj_and_h4_obj_calculation() {
        h3_obj.set(camera.TV2object(h3));
        h4_obj.set(camera.TV2object(h4));
    }

    public void h3_and_h4_calculation() {
        h3.set(camera.object2TV(h3_obj));
        h4.set(camera.object2TV(h4_obj));
    }
}


  


