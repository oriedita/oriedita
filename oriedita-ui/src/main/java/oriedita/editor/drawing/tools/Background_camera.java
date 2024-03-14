package oriedita.editor.drawing.tools;

import org.tinylog.Logger;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Point;

/**
 * Mediation between actual coordinates and display coordinates
 * The background image is based on the case where the upper left corner of the image is displayed according to (0,0) of the window without rotation or enlargement.
 * Enlarge a times around the point h1 on the background image. Next, the background image is translated so that h1 overlaps the point h3 on the developed view. A function that rotates and pastes the developed view of this state so that it looks like it is rotated b degrees around h3, and then restores the rotation of the coordinates.
 * The arguments are Graphics2D g2h, Image imgh, Point h1, Point h2, Point h3, Point h4
 * Make h2 and h4 also overlap
 */
public class Background_camera {

    private Point h1 = new Point();
    private Point h2 = new Point();
    private Point h3 = new Point();
    private Point h4 = new Point();

    private Point h3_obj = new Point();
    private Point h4_obj = new Point();

    private double background_width;
    private double background_height;

    private final Camera camera = new Camera();

    private double magnification = 1.0;
    private double p_idou_x = 0.0;
    private double p_idou_y = 0.0;
    private double p_rotation_angle = 0.0;
    private double p_rotation_x = 0.0;
    private double p_rotation_y = 0.0;

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
        camera.setCamera(cam0);
    }

    public Point getStandardStatePosition(Point pt) {

        Point pt1 = OritaCalc.point_rotate(new Point(p_rotation_x, p_rotation_y), pt, -getAngle());
        Point pt2 = new Point(pt1.getX() - p_idou_x, pt1.getY() - p_idou_y);

        return new Point(pt2.getX() / magnification, pt2.getY() / magnification);
    }

    public void set_h1(Point ht) {
        h1 = getStandardStatePosition(ht);
    }

    public void set_h2(Point ht) {
        h2 = getStandardStatePosition(ht);
    }

    public void set_h3(Point ht) {
        h3 = ht;
    }

    public void set_h4(Point ht) {
        h4 = ht;
    }

    public void parameter_calculation() {
        magnification = h3.distance(h4) / h1.distance(h2);
        p_idou_x = (1.0 - magnification) * h1.getX() + h3.getX() - h1.getX();
        p_idou_y = (1.0 - magnification) * h1.getY() + h3.getY() - h1.getY();
        p_rotation_angle = OritaCalc.angle(h1, h2, h3, h4);
        p_rotation_x = h3.getX();
        p_rotation_y = h3.getY();

        Logger.info("Background_camera --- parameter_calculation");
        Logger.info("h1 " + h1);
        Logger.info("h2 " + h2);
        Logger.info("h3 " + h3);
        Logger.info("h4 " + h4);
    }

    public void setBackgroundWidth(double d0) {
        background_width = d0;
    }

    public void setBackgroundHeight(double d0) {
        background_height = d0;
    }

    public int getX0() {
        return (int) ((1.0 - magnification) * h1.getX() + h3.getX() - h1.getX());
    }

    public int getY0() {
        return (int) ((1.0 - magnification) * h1.getY() + h3.getY() - h1.getY());
    }

    public int getX1() {
        return (int) (background_width * magnification);
    }

    public int getY1() {
        return (int) (background_height * magnification);
    }

    public double getAngle() {
        return p_rotation_angle;
    }

    public int getRotationX() {
        return (int) p_rotation_x;
    }

    public int getRotationY() {
        return (int) p_rotation_y;
    }

    public void setLocked(boolean i_L) {
    }

    public void h3_obj_and_h4_obj_calculation() {
        h3_obj = camera.TV2object(h3);
        h4_obj = camera.TV2object(h4);
    }

    public void h3_and_h4_calculation() {
        h3 = camera.object2TV(h3_obj);
        h4 = camera.object2TV(h4_obj);
    }

    public Camera getCamera() {
        return camera;
    }

}


  


