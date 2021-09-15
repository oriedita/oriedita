package origami_editor.graphic2d.oritaoekaki;

import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Point;

import java.awt.*;

/**
 * Static utility class for drawing
 */
public class OritaDrawing {
    //For drawing thick lines
    public static void widthLine(Graphics g, Point a, Point b, double width, LineColor iColor) {
        widthLine(g, new LineSegment(a, b), width, iColor);
    }

    public static void widthLine(Graphics g, LineSegment s, double r, LineColor iColor) {
        switch (iColor) {
            case BLACK_0:
                g.setColor(Color.black);
                break;
            case RED_1:
                g.setColor(Color.red);
                break;
            case BLUE_2:
                g.setColor(Color.blue);
                break;
            case CYAN_3:
                g.setColor(Color.green);
                break;
            case ORANGE_4:
                g.setColor(Color.orange);
                break;
        }
        LineSegment sp = OritaCalc.moveParallel(s, r);
        LineSegment sm = OritaCalc.moveParallel(s, -r);

        int[] x = new int[5];
        int[] y = new int[5];

        x[0] = (int) sp.getAX();
        y[0] = (int) sp.getAY();
        x[1] = (int) sp.getBX();
        y[1] = (int) sp.getBY();
        x[2] = (int) sm.getBX();
        y[2] = (int) sm.getBY();
        x[3] = (int) sm.getAX();
        y[3] = (int) sm.getAY();

        g.fillPolygon(x, y, 4);
    }

    //Draw a cross around the designated Point
    public static void cross(Graphics g, Point t, double length, double width, LineColor icolor) {
        Point tx0 = new Point();
        Point tx1 = new Point();
        Point ty0 = new Point();
        Point ty1 = new Point();
        tx0.setX(t.getX() - length);
        tx0.setY(t.getY());
        tx1.setX(t.getX() + length);
        tx1.setY(t.getY());
        ty0.setX(t.getX());
        ty0.setY(t.getY() - length);
        ty1.setX(t.getX());
        ty1.setY(t.getY() + length);
        widthLine(g, tx0, tx1, width, icolor);
        widthLine(g, ty0, ty1, width, icolor);
    }

    //Draw a pointing diagram around the specified Point
    public static void pointingAt1(Graphics g, LineSegment s_tv, double length, double width, int icolor) {
        g.setColor(new Color(255, 165, 0, 100));//g.setColor(Color.ORANGE);
        g.drawLine((int) s_tv.getAX(), (int) s_tv.getAY(), (int) s_tv.getBX(), (int) s_tv.getBY()); //直線
    }

    //Draw a pointing diagram around the specified Point
    public static void pointingAt2(Graphics g, LineSegment s_tv, double length, double width, int icolor) {
        g.setColor(new Color(255, 165, 0, 100));//g.setColor(Color.ORANGE);
        g.drawLine((int) s_tv.getAX(), (int) s_tv.getAY(), (int) s_tv.getBX(), (int) s_tv.getBY()); //直線

    }

    //Draw a pointing diagram around the specified Point
    public static void pointingAt3(Graphics g, LineSegment s_tv, double length, double width, int icolor) {
        g.setColor(new Color(255, 200, 0, 50));
        g.drawLine((int) s_tv.getAX(), (int) s_tv.getAY(), (int) s_tv.getBX(), (int) s_tv.getBY()); //直線
    }


    //Draw a pointing diagram around the specified Point
    public static void pointingAt4(Graphics g, LineSegment s_tv, int color_transparency) {
        g.setColor(new Color(255, 0, 147, color_transparency));

        g.drawLine((int) s_tv.getAX(), (int) s_tv.getAY(), (int) s_tv.getBX(), (int) s_tv.getBY()); //直線
    }
}
