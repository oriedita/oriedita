package oriedita.editor.drawing.tools;

import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.io.Serializable;

public class Camera implements Serializable { // Mediation between actual coordinates and display coordinates

    double camera_position_x, camera_position_y;
    double camera_angle;
    double camera_mirror; //-Either 1.0 or 1.0 Change the sign only on the x-axis // Mirror
    double camera_zoom_x;
    double camera_zoom_y;
    double display_position_x, display_position_y;

    Camera parent;

    double do2rad = 3.14159265 / 180.0;
    double camera_rad;
    double sin_rad;
    double cos_rad;

    public Camera() {
        camera_position_x = 0.0;
        camera_position_y = 0.0;
        camera_angle = 0.0;
        camera_rad = camera_angle * do2rad;
        sin_rad = Math.sin(camera_rad);
        cos_rad = Math.cos(camera_rad);
        camera_mirror = 1.0;       //鏡
        camera_zoom_x = 1.0;
        camera_zoom_y = 1.0;

        display_position_x = 350.0;
        display_position_y = 350.0;
    }

    public void reset() {
        camera_position_x = 0.0;
        camera_position_y = 0.0;
        camera_angle = 0.0;
        camera_rad = camera_angle * do2rad;
        sin_rad = Math.sin(camera_rad);
        cos_rad = Math.cos(camera_rad);
        camera_mirror = 1.0;       //鏡
        camera_zoom_x = 1.0;
        camera_zoom_y = 1.0;
        parent = null;

        display_position_x = 350.0;
        display_position_y = 350.0;
    }

    public Camera getParent() {
        return parent;
    }

    public void setParent(Camera parent) {
        this.parent = parent;
    }

    public void multiplyCameraZoomX(double d) {
        camera_zoom_x = d * camera_zoom_x;
    }

    public void multiplyCameraZoomY(double d) {
        camera_zoom_y = d * camera_zoom_y;
    }

    public void setCamera(Camera c0) {
        double d_camera_position_x = c0.getCameraPositionX();
        double d_camera_position_y = c0.getCameraPositionY();
        double d_camera_angle = c0.getCameraAngle();
        double d_camera_mirror = c0.getCameraMirror();
        double d_camera_zoom_x = c0.getCameraZoomX();
        double d_camera_zoom_y = c0.getCameraZoomY();

        double d_display_position_x = c0.getDisplayPositionX();
        double d_display_position_y = c0.getDisplayPositionY();

        setCameraPositionX(d_camera_position_x);
        setCameraPositionY(d_camera_position_y);
        setCameraAngle(d_camera_angle);
        setCameraMirror(d_camera_mirror);
        setCameraZoomX(d_camera_zoom_x);
        setCameraZoomY(d_camera_zoom_y);
        setParent(c0.getParent());

        setDisplayPositionX(d_display_position_x);
        setDisplayPositionY(d_display_position_y);
    }

    public double getCameraPositionX() {
        return camera_position_x;
    }

    public void setCameraPositionX(double d) {
        camera_position_x = d;
    }

    public double getCameraPositionY() {
        return camera_position_y;
    }

    public void setCameraPositionY(double d) {
        camera_position_y = d;
    }

    public double getCameraAngle() {
        return camera_angle;
    }

    public void setCameraAngle(double d) {
        camera_angle = d;
        camera_rad = camera_angle * do2rad;
        sin_rad = Math.sin(camera_rad);
        cos_rad = Math.cos(camera_rad);
    }

    public double getCameraMirror() {
        return camera_mirror;
    }       //鏡

    public void setCameraMirror(double d) {
        camera_mirror = d;
    }       //鏡

    public boolean determineIsCameraMirrored() {
        return camera_mirror == -1.0;
    }

    public double getCameraZoomX() {
        return camera_zoom_x;
    }

    public void setCameraZoomX(double d) {
        camera_zoom_x = d;
    }

    public double getCameraZoomY() {
        return camera_zoom_y;
    }

    public void setCameraZoomY(double d) {
        camera_zoom_y = d;
    }

    public double getDisplayPositionX() {
        return display_position_x;
    }

    public void setDisplayPositionX(double d) {
        display_position_x = d;
    }

    public double getDisplayPositionY() {
        return display_position_y;
    }

    public void setDisplayPositionY(double d) {
        display_position_y = d;
    }

    public void setDisplayPosition(Point p) {
        setDisplayPositionX(p.getX());
        setDisplayPositionY(p.getY());
    }

    public Point getCameraPosition() {
        return new Point(camera_position_x, camera_position_y);
    }

    public void setCameraPosition(Point p) {
        setCameraPositionX(p.getX());
        setCameraPositionY(p.getY());
    }

    public Point object2TV(Point t_ob) {
        double x1 = t_ob.getX() - camera_position_x;
        double y1 = t_ob.getY() - camera_position_y;
        double x2 = cos_rad * x1 + sin_rad * y1;
        double y2 = -sin_rad * x1 + cos_rad * y1;

        x2 = x2 * camera_mirror;       //鏡
        x2 = x2 * camera_zoom_x;
        y2 = y2 * camera_zoom_y;
        Point t_tv = new Point(x2 + display_position_x, y2 + display_position_y);
        if (parent != null) {
            t_tv = parent.object2TV(t_tv);
        }
        return t_tv;
    }

    public LineSegment object2TV(LineSegment s_ob) {
        return s_ob
                .withA(object2TV(s_ob.getA()))
                .withB(object2TV(s_ob.getB()));
    }

    public GeneralPath object2TV(GeneralPath path_ob) {
        AffineTransform transform = new AffineTransform();
        transform.translate(display_position_x, display_position_y);
        transform.rotate(-camera_rad);
        transform.scale(getCameraZoomX(), getCameraZoomY());
        transform.translate(-camera_position_x, -camera_position_y);
        return (GeneralPath) path_ob.createTransformedShape(transform);
    }

    public Circle object2TV(Circle s_ob) {
        Point p_ob = s_ob.determineCenter();
        Point p_tv = object2TV(p_ob);
        return new Circle(p_tv, s_ob.getR() * camera_zoom_x, s_ob.getColor());
    }

    public Point TV2object(Point t_tv) {
        if (parent != null) {
            t_tv = parent.TV2object(t_tv);
        }
        double x1, y1;
        double x2, y2;
        x1 = t_tv.getX();
        y1 = t_tv.getY();
        x1 = x1 - display_position_x;
        y1 = y1 - display_position_y;
        x1 = x1 / camera_zoom_x;
        y1 = y1 / camera_zoom_y;

        x1 = x1 * camera_mirror;       //鏡

        x2 = cos_rad * x1 - sin_rad * y1;
        y2 = sin_rad * x1 + cos_rad * y1;

        return new Point(x2 + camera_position_x, y2 + camera_position_y);
    }

    public LineSegment TV2object(LineSegment s_tv) {
        return s_tv
                .withA(TV2object(s_tv.getA()))
                .withB(TV2object(s_tv.getB()));
    }

    public void displayPositionMove(Point tuika) {
        if (parent != null) {
            Point origin = parent.TV2object(new Point(0,0));
            Point delta_tr = parent.TV2object(tuika);
            tuika = origin.delta(delta_tr);
        }
        display_position_x = display_position_x + tuika.getX();
        display_position_y = display_position_y + tuika.getY();
    }

    //Make sure that the display on the TV does not change, and adjust the camera position to the position of the subject corresponding to the coordinates Ten P on the TV.
    public void camera_position_specify_from_TV(Point p) {
        if (parent != null) {
            setCameraPosition(TV2object(p));
            setDisplayPosition(parent.TV2object(p));
        } else {
            setCameraPosition(TV2object(p));
            setDisplayPosition(p);
        }
    }

    public Point getDisplayPosition() {
        return new Point(display_position_x, display_position_y);
    }
}
