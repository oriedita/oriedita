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

        x[0] = (int) sp.getAx();
        y[0] = (int) sp.getay();
        x[1] = (int) sp.getbx();
        y[1] = (int) sp.getby();
        x[2] = (int) sm.getbx();
        y[2] = (int) sm.getby();
        x[3] = (int) sm.getAx();
        y[3] = (int) sm.getay();

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

    //指定されたTenを中心に指差し図を描く
    public void yubisasi1(Graphics g, LineSegment s_tv, double nagasa, double haba, int icolor) {
        Graphics2D g2 = (Graphics2D) g;
        //	g2.setStroke(new BasicStroke(15.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定

        g.setColor(new Color(255, 165, 0, 100));//g.setColor(Color.ORANGE);
        g.drawLine((int) s_tv.getAx(), (int) s_tv.getay(), (int) s_tv.getbx(), (int) s_tv.getby()); //直線
    }

    //指定されたTenを中心に指差し図を描く
    public void yubisasi2(Graphics g, LineSegment s_tv, double nagasa, double haba, int icolor) {
        Graphics2D g2 = (Graphics2D) g;
        //	g2.setStroke(new BasicStroke(15.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定

        g.setColor(new Color(255, 165, 0, 100));//g.setColor(Color.ORANGE);
        g.drawLine((int) s_tv.getAx(), (int) s_tv.getay(), (int) s_tv.getbx(), (int) s_tv.getby()); //直線

    }

    //指定されたTenを中心に指差し図を描く
    public void yubisasi3(Graphics g, LineSegment s_tv, double nagasa, double haba, int icolor) {
        Graphics2D g2 = (Graphics2D) g;
        //	g2.setStroke(new BasicStroke(15.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定

        g.setColor(new Color(255, 200, 0, 50));//g.setColor(Color.yellow);
        g.drawLine((int) s_tv.getAx(), (int) s_tv.getay(), (int) s_tv.getbx(), (int) s_tv.getby()); //直線
    }


    //指定されたTenを中心に指差し図を描く
    public void yubisasi4(Graphics g, LineSegment s_tv, int icolor_toukado) {

//System.out.println("OO.yubisasi4 "+s_tv.getax()+", "+s_tv.getay()+" - "+s_tv.getbx()+", "+s_tv.getby());

        Graphics2D g2 = (Graphics2D) g;
        //	g2.setStroke(new BasicStroke(15.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定

        g.setColor(new Color(255, 0, 147, icolor_toukado));
        //g.setColor(Color.PINK);

        g.drawLine((int) s_tv.getAx(), (int) s_tv.getay(), (int) s_tv.getbx(), (int) s_tv.getby()); //直線
    }


}
