package oriedita.editor.drawing.tools;

import oriedita.editor.Colors;
import oriedita.editor.action.selector.drawing.DrawingSettings;
import oriedita.editor.canvas.LineStyle;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Static utility class for drawing
 */
public class DrawingUtil {
    //For drawing thick lines
    public static void widthLine(Graphics g, Point a, Point b, double width, LineColor iColor) {
        widthLine(g, new LineSegment(a, b), width, iColor);
    }

    public static void widthLine(Graphics g, LineSegment s, double r, LineColor iColor) {
        switch (iColor) {
            case BLACK_0:
                g.setColor(Colors.get(Color.black));
                break;
            case RED_1:
                g.setColor(Colors.get(Color.red));
                break;
            case BLUE_2:
                g.setColor(Colors.get(Color.blue));
                break;
            case CYAN_3:
                g.setColor(Colors.get(Color.green));
                break;
            case ORANGE_4:
                g.setColor(Colors.get(Color.orange));
                break;
            default:
                break;
        }
        LineSegment sp = OritaCalc.moveParallel(s, r);
        LineSegment sm = OritaCalc.moveParallel(s, -r);

        int[] x = new int[5];
        int[] y = new int[5];

        x[0] = (int) sp.determineAX();
        y[0] = (int) sp.determineAY();
        x[1] = (int) sp.determineBX();
        y[1] = (int) sp.determineBY();
        x[2] = (int) sm.determineBX();
        y[2] = (int) sm.determineBY();
        x[3] = (int) sm.determineAX();
        y[3] = (int) sm.determineAY();

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

    public static void drawVertex(Graphics2D g, Point a, int pointSize) {
        g.setColor(Colors.get(Color.black));
        g.fillRect((int)(a.getX()-pointSize), (int)(a.getY()-pointSize), (int)(pointSize*2+0.5), (int)(pointSize*2+0.5));
    }

    //Draw a pointing diagram around the specified Point
    public static void pointingAt1(Graphics g, LineSegment s_tv) {
        g.setColor(Colors.get(new Color(255, 165, 0, 100)));//g.setColor(Colors.get(Color.ORANGE));
        g.drawLine((int) s_tv.determineAX(), (int) s_tv.determineAY(), (int) s_tv.determineBX(), (int) s_tv.determineBY()); //直線
    }

    //Draw a pointing diagram around the specified Point
    public static void pointingAt2(Graphics g, LineSegment s_tv) {
        g.setColor(Colors.get(new Color(255, 165, 0, 100)));//g.setColor(Colors.get(Color.ORANGE));
        g.drawLine((int) s_tv.determineAX(), (int) s_tv.determineAY(), (int) s_tv.determineBX(), (int) s_tv.determineBY()); //直線

    }

    //Draw a pointing diagram around the specified Point
    public static void pointingAt3(Graphics g, LineSegment s_tv) {
        g.setColor(Colors.get(new Color(255, 200, 0, 50)));
        g.drawLine((int) s_tv.determineAX(), (int) s_tv.determineAY(), (int) s_tv.determineBX(), (int) s_tv.determineBY()); //直線
    }

    //Draw a pointing diagram around the specified Point
    public static void pointingAt4(Graphics g, LineSegment s_tv, int color_transparency) {
        g.setColor(Colors.get(new Color(255, 0, 147, color_transparency)));

        g.drawLine((int) s_tv.determineAX(), (int) s_tv.determineAY(), (int) s_tv.determineBX(), (int) s_tv.determineBY()); //直線
    }

    public static void setColor(Graphics g, LineColor i) {
        switch (i) {
            case BLACK_0:
                g.setColor(Colors.get(Color.black));
                break;
            case RED_1:
                g.setColor(Colors.get(Color.red));
                break;
            case BLUE_2:
                g.setColor(Colors.get(Color.blue));
                break;
            case CYAN_3:
                g.setColor(Colors.get(new Color(100, 200, 200)));
                break;
            case ORANGE_4:
                g.setColor(Colors.get(Color.orange));
                break;
            case MAGENTA_5:
                g.setColor(Colors.get(Color.magenta));
                break;
            case GREEN_6:
                g.setColor(Colors.get(Color.green));
                break;
            case YELLOW_7:
                g.setColor(Colors.get(Color.yellow));
                break;
            case PURPLE_8:
                g.setColor(Colors.get(new Color(210, 0, 255)));
                break;
            default:
                break;
        }
    }

    public static void drawSelectLine(Graphics g, LineSegment s, Camera camera) {
        g.setColor(Colors.get(Color.green));

        LineSegment s_tv = new LineSegment();
        s_tv.set(camera.object2TV(s));

        Point a = new Point();
        Point b = new Point();
        a.set(s_tv.determineAX() + Epsilon.UNKNOWN_1EN6, s_tv.determineAY() + Epsilon.UNKNOWN_1EN6);
        b.set(s_tv.determineBX() + Epsilon.UNKNOWN_1EN6, s_tv.determineBY() + Epsilon.UNKNOWN_1EN6);//なぜEpsilon.UNKNOWN_0000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

        g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線
    }

    public static void drawAuxLiveLine(Graphics g, LineSegment as, Camera camera, float lineWidth, int pointSize, float f_h_WireframeLineWidth) {
        setColor(g, as.getColor());

        Graphics2D g2 = (Graphics2D) g;

        LineSegment s_tv = new LineSegment();
        s_tv.set(camera.object2TV(as));
        Point a = new Point();
        Point b = new Point();
        a.set(s_tv.determineAX() + Epsilon.UNKNOWN_1EN6, s_tv.determineAY() + Epsilon.UNKNOWN_1EN6);
        b.set(s_tv.determineBX() + Epsilon.UNKNOWN_1EN6, s_tv.determineBY() + Epsilon.UNKNOWN_1EN6);//なぜEpsilon.UNKNOWN_0000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

        g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線

        if (lineWidth < 2.0f) {//Draw a square at the vertex
            g.setColor(Colors.get(Color.black));
            g.fillRect((int) a.getX() - pointSize, (int) a.getY() - pointSize, 2 * pointSize + 1, 2 * pointSize + 1); //正方形を描く//g.fillRect(10, 10, 100, 50);長方形を描く
            g.fillRect((int) b.getX() - pointSize, (int) b.getY() - pointSize, 2 * pointSize + 1, 2 * pointSize + 1); //正方形を描く
        }

        if (lineWidth >= 2.0f) {//  Thick line
            g2.setStroke(new BasicStroke(1.0f + f_h_WireframeLineWidth % 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状

            if (pointSize != 0) {
                double d_width = (double) lineWidth / 2.0 + (double) pointSize;

                g.setColor(Colors.get(Color.white));
                g2.fill(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                g.setColor(Colors.get(Color.black));
                g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                g.setColor(Colors.get(Color.white));
                g2.fill(new Ellipse2D.Double(b.getX() - d_width, b.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                g.setColor(Colors.get(Color.black));
                g2.draw(new Ellipse2D.Double(b.getX() - d_width, b.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
            }

            g2.setStroke(new BasicStroke(f_h_WireframeLineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状

        }
    }

    public static void drawCircle(Graphics g, Circle circle, Camera camera, float lineWidth, int pointSize) {
        Point a= new Point();
        a.set(camera.object2TV(circle.determineCenter()));//この場合のaは描画座標系での円の中心の位置

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状

        if (circle.getCustomized() == 0) {
            setColor(g, circle.getColor());
        } else if (circle.getCustomized() == 1) {
            g.setColor(circle.getCustomizedColor());
        }

        //円周の描画
        double d_width = circle.getR() * camera.getCameraZoomX();//d_habaは描画時の円の半径。なお、camera.get_camera_bairitsu_x()＝camera.get_camera_bairitsu_y()を前提としている。
        g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

        a.set(camera.object2TV(circle.determineCenter()));//この場合のaは描画座標系での円の中心の位置

        g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状
        g.setColor(Colors.get(new Color(0, 255, 255, 255)));

        //円の中心の描画
        if (lineWidth < 2.0f) {//中心の黒い正方形を描く
            g.setColor(Colors.get(Color.black));
            g.fillRect((int) a.getX() - pointSize, (int) a.getY() - pointSize, 2 * pointSize + 1, 2 * pointSize + 1); //正方形を描く//g.fillRect(10, 10, 100, 50);長方形を描く
        }

        if (lineWidth >= 2.0f) {//  太線指定時の中心を示す黒い小円を描く
            g2.setStroke(new BasicStroke(1.0f + lineWidth % 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定
            if (pointSize != 0) {
                d_width = (double) lineWidth / 2.0 + (double) pointSize;


                g.setColor(Colors.get(Color.white));
                g2.fill(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                g.setColor(Colors.get(Color.black));
                g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
            }
        }
    }

    public static void drawAuxLine(Graphics g, LineSegment s, Camera camera, float lineWidth, int pointSize) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状

        if (s.getCustomized() == 0) {
            setColor(g, s.getColor());
        } else if (s.getCustomized() == 1) {
            g.setColor(s.getCustomizedColor());
        }

        LineSegment s_tv = new LineSegment();
        s_tv.set(camera.object2TV(s));
        Point a= new Point();
        Point b = new Point();
        a.set(s_tv.determineAX() + Epsilon.UNKNOWN_1EN6, s_tv.determineAY() + Epsilon.UNKNOWN_1EN6);
        b.set(s_tv.determineBX() + Epsilon.UNKNOWN_1EN6, s_tv.determineBY() + Epsilon.UNKNOWN_1EN6);//なぜEpsilon.UNKNOWN_0000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

        g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線

        if (lineWidth < 2.0f) {//頂点の黒い正方形を描く
            drawVertex(g2, a, pointSize);
            drawVertex(g2, b, pointSize);
        }

        if (lineWidth >= 2.0f) {//  太線
            g2.setStroke(new BasicStroke(1.0f + lineWidth % 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定
            if (pointSize != 0) {
                double d_width = (double) lineWidth / 2.0 + (double) pointSize;

                g.setColor(Colors.get(Color.white));
                g2.fill(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));


                g.setColor(Colors.get(Color.black));
                g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                g.setColor(Colors.get(Color.white));
                g2.fill(new Ellipse2D.Double(b.getX() - d_width, b.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                g.setColor(Colors.get(Color.black));
                g2.draw(new Ellipse2D.Double(b.getX() - d_width, b.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
            }
        }
    }

    public static void drawLineStep(Graphics g, LineSegment s, Camera camera, DrawingSettings settings) {
        drawLineStep(g, s, camera, settings.getLineWidth(), settings.displayGridInputAssist());
    }

    public static void drawLineStep(Graphics g, LineSegment s, Camera camera, float lineWidth, boolean gridInputAssist) {
        Graphics2D g2 = (Graphics2D) g;
        setColor(g, s.getColor());
        g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状

        LineSegment s_tv = new LineSegment();
        s_tv.set(camera.object2TV(s));
        Point a = new Point();
        Point b = new Point();
        a.set(s_tv.determineAX() + Epsilon.UNKNOWN_1EN6, s_tv.determineAY() + Epsilon.UNKNOWN_1EN6);
        b.set(s_tv.determineBX() + Epsilon.UNKNOWN_1EN6, s_tv.determineBY() + Epsilon.UNKNOWN_1EN6);//The reason for adding Epsilon.UNKNOWN_0000001 is to prevent the original fold line from being affected by the new fold line when drawing on the display.


        g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線
        int i_width_nyuiiryokuji = 3;
        if (gridInputAssist) {
            i_width_nyuiiryokuji = 2;
        }

        switch (s.getActive()) {
            case ACTIVE_A_1:
                g.fillOval((int) a.getX() - i_width_nyuiiryokuji, (int) a.getY() - i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji); //円
                break;
            case ACTIVE_B_2:
                g.fillOval((int) b.getX() - i_width_nyuiiryokuji, (int) b.getY() - i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji); //円
                break;
            case ACTIVE_BOTH_3:
                g.fillOval((int) a.getX() - i_width_nyuiiryokuji, (int) a.getY() - i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji); //円
                g.fillOval((int) b.getX() - i_width_nyuiiryokuji, (int) b.getY() - i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji); //円
                break;
            default:
                break;
        }
    }

    public static void drawStepVertex(Graphics2D g, Point p, LineColor color, Camera camera, DrawingSettings s) {
        drawStepVertex(g, p, color, camera, s.displayGridInputAssist());
    }

    public static void drawStepVertex(Graphics2D g, Point p, LineColor color, Camera camera, boolean gridInputAssist) {
        setColor(g, color);
        Point a = camera.object2TV(p);
        int i_width_nyuiiryokuji = 3;
        if (gridInputAssist) {
            i_width_nyuiiryokuji = 2;
        }

        g.fillOval((int) a.getX() - i_width_nyuiiryokuji, (int) a.getY() - i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji); //円

    }

    public static void drawLineCandidate(Graphics g, LineSegment s, Camera camera, int pointSize) {
        setColor(g, s.getColor());

        LineSegment s_tv = new LineSegment();
        s_tv.set(camera.object2TV(s));
        Point a = new Point();
        Point b = new Point();
        a.set(s_tv.determineAX() + Epsilon.UNKNOWN_1EN6, s_tv.determineAY() + Epsilon.UNKNOWN_1EN6);
        b.set(s_tv.determineBX() + Epsilon.UNKNOWN_1EN6, s_tv.determineBY() + Epsilon.UNKNOWN_1EN6);//なぜEpsilon.UNKNOWN_0000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

        g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線
        int i_width = pointSize + 5;

        switch (s.getActive()) {
            case ACTIVE_A_1:
                g.drawLine((int) a.getX() - i_width, (int) a.getY(), (int) a.getX() + i_width, (int) a.getY()); //直線
                g.drawLine((int) a.getX(), (int) a.getY() - i_width, (int) a.getX(), (int) a.getY() + i_width); //直線
                break;
            case ACTIVE_B_2:
                g.drawLine((int) b.getX() - i_width, (int) b.getY(), (int) b.getX() + i_width, (int) b.getY()); //直線
                g.drawLine((int) b.getX(), (int) b.getY() - i_width, (int) b.getX(), (int) b.getY() + i_width); //直線
                break;
            case ACTIVE_BOTH_3:
                g.drawLine((int) a.getX() - i_width, (int) a.getY(), (int) a.getX() + i_width, (int) a.getY()); //直線
                g.drawLine((int) a.getX(), (int) a.getY() - i_width, (int) a.getX(), (int) a.getY() + i_width); //直線

                g.drawLine((int) b.getX() - i_width, (int) b.getY(), (int) b.getX() + i_width, (int) b.getY()); //直線
                g.drawLine((int) b.getX(), (int) b.getY() - i_width, (int) b.getX(), (int) b.getY() + i_width); //直線
                break;
            default:
                break;
        }
    }

    public static void drawCircleStep(Graphics g, Circle c, Camera camera) {
        Graphics2D g2 = (Graphics2D) g;
        setColor(g, c.getColor());
        Point a = new Point();

        a.set(camera.object2TV(c.determineCenter()));//この場合のs_tvは描画座標系での円の中心の位置
        a.set(a.getX() + Epsilon.UNKNOWN_1EN6, a.getY() + Epsilon.UNKNOWN_1EN6);//なぜEpsilon.UNKNOWN_0000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

        double d_width = c.getR() * camera.getCameraZoomX();//d_habaは描画時の円の半径。なお、camera.get_camera_bairitsu_x()＝camera.get_camera_bairitsu_y()を前提としている。

        g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
    }

    private static final float[] dash_M1 = {10.0f, 3.0f, 3.0f, 3.0f};//一点鎖線
    private static final float[] dash_M2 = {10.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f};//二点鎖線
    private static final float[] dash_V = {8.0f, 8.0f};//破線

    private static final Point defaultMove = new Point(Epsilon.UNKNOWN_1EN6, Epsilon.UNKNOWN_1EN6);

    public static void drawCpLine(Graphics g, LineSegment s, Camera camera, LineStyle lineStyle, float lineWidth, int pointSize, int clipX, int clipY) {

        Point a = camera.object2TV(s.getA());
        a.move(defaultMove);
        Point b = camera.object2TV(s.getB());
        b.move(defaultMove);

        int aflag = cohenSutherlandRegion(clipX, clipY, a);
        if (aflag != CENTER) {
            int bflag = cohenSutherlandRegion(clipX, clipY, b);
            if ((aflag & bflag) != CENTER) {
                return;
            }
        }

        Graphics2D g2 = (Graphics2D) g;
        switch (lineStyle) {
            case COLOR:
                setColor(g, s.getColor());
                g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状
                break;
            case COLOR_AND_SHAPE:
                setColor(g, s.getColor());
                if (s.getColor() == LineColor.BLACK_0) {
                    g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                }//基本指定A　　線の太さや線の末端の形状
                if (s.getColor() == LineColor.RED_1) {
                    g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_M1, 0.0f));
                }//一点鎖線//線の太さや線の末端の形状
                if (s.getColor() == LineColor.BLUE_2) {
                    g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_V, 0.0f));
                }//破線//線の太さや線の末端の形状
                break;
            case BLACK_ONE_DOT:
                if (s.getColor() == LineColor.BLACK_0) {
                    g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                }//基本指定A　　線の太さや線の末端の形状
                if (s.getColor() == LineColor.RED_1) {
                    g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_M1, 0.0f));
                }//一点鎖線//線の太さや線の末端の形状
                if (s.getColor() == LineColor.BLUE_2) {
                    g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_V, 0.0f));
                }//破線//線の太さや線の末端の形状
                break;
            case BLACK_TWO_DOT:
                if (s.getColor() == LineColor.BLACK_0) {
                    g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                }//基本指定A　　線の太さや線の末端の形状
                if (s.getColor() == LineColor.RED_1) {
                    g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_M2, 0.0f));
                }//二点鎖線//線の太さや線の末端の形状
                if (s.getColor() == LineColor.BLUE_2) {
                    g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_V, 0.0f));
                }//破線//線の太さや線の末端の形状
                break;
        }


        g2.drawLine((int)a.getX(), (int)a.getY(), (int)b.getX(), (int)b.getY());

        if (Epsilon.high.eq0(lineWidth)) {

        } else if (lineWidth < 2.0f) {//頂点の黒い正方形を描く
            drawVertex(g2, a, pointSize);
            if (a.distance(b) > 1) {
                drawVertex(g2, b, pointSize);
            }
        } else if (lineWidth >= 2.0f) {//  太線
            g2.setStroke(new BasicStroke(1.0f + lineWidth % 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定
            if (pointSize != 0) {
                double d_width = (double) lineWidth / 2.0 + (double) pointSize;

                g.setColor(Colors.get(Color.white));
                g2.fill(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                g.setColor(Colors.get(Color.black));
                g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                g.setColor(Colors.get(Color.white));
                g2.fill(new Ellipse2D.Double(b.getX() - d_width, b.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                g.setColor(Colors.get(Color.black));
                g2.draw(new Ellipse2D.Double(b.getX() - d_width, b.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
            }
        }
    }


    public static final int CENTER = 0;
    public static final int WEST  = 0b0001;
    public static final int EAST  = 0b0010;
    public static final int NORTH = 0b0100;
    public static final int SOUTH = 0b1000;

    public static int cohenSutherlandRegion(int clipX, int clipY, Point point) {
        return cohenSutherlandRegion(0,0,clipX, clipY, point);
    }

    /**
     * returns the region according to the Cohen-Sutherland Line Clipping Algorithm using a viewport rectangle
     * going from (clipLowX, clipLowY) to (clipHighX, clipHighY). If the point is inside the viewport, the region
     * will be CENTER (= 0), otherwise, the direction of the point in relation to the viewport can be retrieved
     * using bitwise-and with the Constants WEST, EAST, NORTH, SOUTH.
     *
     * If the bitwise-and of the Endpoints of a Line is not CENTER, the whole line is outside the viewport.
     *
     * for more info, see https://en.wikipedia.org/wiki/Cohen%E2%80%93Sutherland_algorithm
     */
    public static int cohenSutherlandRegion(int clipLowX, int clipLowY, int clipHighX, int clipHighY, Point point) {
        int region = CENTER;
        if (point.getX() < clipLowX) {
            region |= WEST;
        } else if (point.getX() > clipHighX) {
            region |= EAST;
        }
        if (point.getY() < clipLowY) {
            region |= SOUTH;
        } else if (point.getY() > clipHighY) {
            region |= NORTH;
        }
        return region;
    }
}
