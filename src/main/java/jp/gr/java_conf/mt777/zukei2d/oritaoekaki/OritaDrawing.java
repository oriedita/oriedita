package jp.gr.java_conf.mt777.zukei2d.oritaoekaki;

import jp.gr.java_conf.mt777.zukei2d.senbun.*;
import jp.gr.java_conf.mt777.zukei2d.oritacalc.*;
import jp.gr.java_conf.mt777.zukei2d.ten.Point;

import java.awt.*;

public class OritaDrawing { //For drawing
    OritaCalc oc = new OritaCalc();

    //For drawing thick lines
    public void widthLine(Graphics g, Point a, Point b, double width, int iColor) {
        widthLine(g, new LineSegment(a, b), width, iColor);
    }

    public void widthLine(Graphics g, LineSegment s, double r, int iColor) {
        if (iColor == 0) {
            g.setColor(Color.black);
        }
        if (iColor == 1) {
            g.setColor(Color.red);
        }
        if (iColor == 2) {
            g.setColor(Color.blue);
        }
        if (iColor == 3) {
            g.setColor(Color.green);
        }
        if (iColor == 4) {
            g.setColor(Color.orange);
        }
        LineSegment sp = oc.moveParallel(s, r);
        LineSegment sm = oc.moveParallel(s, -r);

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
    public void cross(Graphics g, Point t, double nagasa, double width, int icolor) {
        Point tx0 = new Point();
        Point tx1 = new Point();
        Point ty0 = new Point();
        Point ty1 = new Point();
        tx0.setX(t.getX() - nagasa);
        tx0.setY(t.getY());
        tx1.setX(t.getX() + nagasa);
        tx1.setY(t.getY());
        ty0.setX(t.getX());
        ty0.setY(t.getY() - nagasa);
        ty1.setX(t.getX());
        ty1.setY(t.getY() + nagasa);
        widthLine(g, tx0, tx1, width, icolor);
        widthLine(g, ty0, ty1, width, icolor);
    }

    //Draw a pointing diagram around the specified Point
    public void pointingAt1(Graphics g, LineSegment s_tv, double nagasa, double haba, int icolor) {
        g.setColor(new Color(255, 165, 0, 100));//g.setColor(Color.ORANGE);
        g.drawLine((int) s_tv.getAX(), (int) s_tv.getAY(), (int) s_tv.getBX(), (int) s_tv.getBY()); //直線
    }

    //Draw a pointing diagram around the specified Point
    public void pointingAt2(Graphics g, LineSegment s_tv, double nagasa, double haba, int icolor) {
        g.setColor(new Color(255, 165, 0, 100));//g.setColor(Color.ORANGE);
        g.drawLine((int) s_tv.getAX(), (int) s_tv.getAY(), (int) s_tv.getBX(), (int) s_tv.getBY()); //直線

    }

    //Draw a pointing diagram around the specified Point
    public void pointingAt3(Graphics g, LineSegment s_tv, double nagasa, double haba, int icolor) {
        g.setColor(new Color(255, 200, 0, 50));
        g.drawLine((int) s_tv.getAX(), (int) s_tv.getAY(), (int) s_tv.getBX(), (int) s_tv.getBY()); //直線
    }


    //Draw a pointing diagram around the specified Point
    public void pointingAt4(Graphics g, LineSegment s_tv, int color_transparency) {
        g.setColor(new Color(255, 0, 147, color_transparency));

        g.drawLine((int) s_tv.getAX(), (int) s_tv.getAY(), (int) s_tv.getBX(), (int) s_tv.getBY()); //直線
    }
}
