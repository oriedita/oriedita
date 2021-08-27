package jp.gr.java_conf.mt777.origami.dougu.orisensyuugou;

import jp.gr.java_conf.mt777.graphic2d.circle.Circle;
import jp.gr.java_conf.mt777.graphic2d.linesegment.LineSegment;
import jp.gr.java_conf.mt777.graphic2d.oritacalc.OritaCalc;
import jp.gr.java_conf.mt777.graphic2d.oritacalc.straightline.StraightLine;
import jp.gr.java_conf.mt777.graphic2d.point.Point;
import jp.gr.java_conf.mt777.graphic2d.polygon.Polygon;
import jp.gr.java_conf.mt777.kiroku.memo.Memo;
import jp.gr.java_conf.mt777.origami.orihime.LineColor;
import jp.gr.java_conf.mt777.seiretu.narabebako.SortingBox_int_double;
import jp.gr.java_conf.mt777.seiretu.narabebako.int_double;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class FoldLineSet {
    int total;               //Total number of line segments actually used
    List<LineSegment> lineSegments = new ArrayList<>(); //折線とする線分のインスタンス化

    List<LineSegment> Check1LineSegment = new ArrayList<>(); //Instantiation of line segments to store check information
    List<LineSegment> Check2LineSegment = new ArrayList<>(); //Instantiation of line segments to store check information
    List<LineSegment> Check3LineSegment = new ArrayList<>(); //Instantiation of line segments to store check information
    List<LineSegment> Check4LineSegment = new ArrayList<>(); //Instantiation of line segments to store check information
    List<Point> check4Point = new ArrayList<>(); //Instantiation of points to check


    List<Circle> circles = new ArrayList<>(); //円のインスタンス化
    //Setting variables used to load custom properties
    String[] st_new;
    String[] s_new;
    int i_customized = 0;
    int i_customized_color_R = 0;
    int i_customized_color_G = 0;
    int i_customized_color_B = 0;
    // Specify the point Q, delete the line segments AQ and QC, and add the line segment AC (however, only two line segments have Q as the end point) // When implemented, 1 when nothing is done Returns 0.
    // The procedure is (1) The point p is determined by clicking the mouse.
    // (2) Point p The end point q included in the nearest development drawing is determined.
    // (3) If the distance between the end point of the fold line in the development drawing that is closer to q and q is r or less, the fold line is assumed to be connected to the point q.
    int[] i_s = new int[2];//この変数はdel_Vとtyouten_syuui_sensuuとで共通に使う。tyouten_syuui_sensuuで、頂点回りの折線数が2のときにその2折線の番号を入れる変数。なお、折線数が3以上のときは意味を成さない。//qを端点とする2本の線分の番号
    double fushimi_decision_angle_goukei = 360.0;

    public FoldLineSet() {
        reset();
    } //コンストラクタ

    public void reset() {
        total = 0;
        lineSegments.clear();
        lineSegments.add(new LineSegment());
        Check1LineSegment.clear();
        Check2LineSegment.clear();
        Check3LineSegment.clear();
        Check4LineSegment.clear();
        check4Point.clear();
        circles.clear();
        circles.add(new Circle());
    }

    public void display(String s0) {
        System.out.println(s0 + "  sousuu = " + total);
        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = getLineSegment(i);
            s.display(" ");
        }
    }

    public void set(FoldLineSet foldLineSet) {
        total = foldLineSet.getTotal();
        for (int i = 0; i <= total; i++) {
            LineSegment s = getLineSegment(i);
            s.set(foldLineSet.get(i));
        }
    }

    private LineSegment getLineSegment(int i) {
        if (total + 1 > lineSegments.size()) {
            while (total + 1 > lineSegments.size()) {
                lineSegments.add(new LineSegment());
            }
        }//It won't work without this sentence. I don't know exactly why it should be this sentence.
        return lineSegments.get(i);
    }

    //Get the total number of line segments
    public int getTotal() {
        return total;
    }

    //Get a line segment
    public LineSegment get(int i) {
        return getLineSegment(i);
    }

    //Get the endpoint of the i-th line segment
    public Point getA(int i) {
        LineSegment s;
        s = getLineSegment(i);
        return s.getA();
    }

    public Point getB(int i) {
        LineSegment s;
        s = getLineSegment(i);
        return s.getB();
    }

    //Get the endpoint of the i-th line segment
    public double getAX(int i) {
        LineSegment s;
        s = getLineSegment(i);
        return s.getAX();
    }

    public double getBX(int i) {
        LineSegment s;
        s = getLineSegment(i);
        return s.getBX();
    }

    public double getAY(int i) {
        LineSegment s;
        s = getLineSegment(i);
        return s.getAY();
    }

    public double getBY(int i) {
        LineSegment s;
        s = getLineSegment(i);
        return s.getBY();
    }

    //Set the position of the end point of the i-th line segment
    public void setA(int i, Point p) {
        LineSegment s;
        s = getLineSegment(i);
        s.setA(p);
    }

    public void setB(int i, Point p) {
        LineSegment s;
        s = getLineSegment(i);
        s.setB(p);
    }

    //Enter the value of the i-th line segment
    public void set(int i, Point p, Point q) {
        LineSegment s = getLineSegment(i);
        s.setA(p);
        s.setB(q);
    }

    //Enter the value of the i-th line segment
    public void set(int i, Point p, Point q, LineColor ic, LineSegment.ActiveState ia) {
        LineSegment s = getLineSegment(i);
        s.set(p, q, ic, ia);
    }

    //Enter the color of the i-th line segment
    public void setColor(int i, LineColor icol) {
        LineSegment s = getLineSegment(i);
        s.setColor(icol);
    }

    //Output the color of the i-th line segment
    public LineColor getColor(int i) {
        LineSegment s;
        s = getLineSegment(i);
        return s.getColor();
    }

    public void setLineCustomized(int i, int customized) {
        LineSegment s;
        s = getLineSegment(i);
        s.setCustomized(customized);
    }

    public int getLineCustomized(int i) {
        return getLineSegment(i).getCustomized();
    }

    public void setLineCustomizedColor(int i, Color c0) {
        LineSegment s;
        s = getLineSegment(i);
        s.setCustomizedColor(c0);
    }

    public Color getLineCustomizedColor(int i) {
        LineSegment s;
        s = getLineSegment(i);
        return s.getCustomizedColor();
    }

    public void setCircleCustomized(int i, int customized) {
        Circle e;
        e = getCircle(i);
        e.setCustomized(customized);
    }

    public void setCircleCustomizedColor(int i, Color c0) {
        Circle e;
        e = getCircle(i);
        e.setCustomizedColor(c0);
    }

    //Enter the activity of the i-th line segment
    public void setActive(int i, LineSegment.ActiveState iactive) {
        LineSegment s;
        s = getLineSegment(i);
        s.setActive(iactive);
    }

    //Output the activity of the i-th line segment
    public LineSegment.ActiveState getActive(int i) {
        LineSegment s;
        s = getLineSegment(i);
        return s.getActive();
    }

    public int getVonoroiA(int i) {
        return getLineSegment(i).getVonoroiA();
    }

    public int getVonoroiB(int i) {
        return getLineSegment(i).getVonoroiB();
    }

    public void setVonoroiA(int i, int a) {
        getLineSegment(i).setVonoroiA(a);
    }

    public void setVonoroiB(int i, int b) {
        getLineSegment(i).setVonoroiB(b);
    }

    //Output the information of all line segments of the line segment set as Memo.
    public Memo getMemo() {
        return getMemo("_");
    }

    //Output the information of all line segments of the line segment set as Memo. // Used for recording undo and redo
    public Memo getMemo(String s_title) {
        Memo memo1 = new Memo();
        memo1.reset();

        memo1.addLine("<タイトル>");
        memo1.addLine("タイトル," + s_title);

        memo1.addLine("<線分集合>");

        for (int i = 1; i <= total; i++) {
            memo1.addLine("番号," + i);
            LineSegment s = getLineSegment(i);
            memo1.addLine("色," + s.getColor());

            memo1.addLine("<tpp>" + s.getCustomized() + "</tpp>");
            memo1.addLine("<tpp_color_R>" + s.getCustomizedColor().getRed() + "</tpp_color_R>");
            memo1.addLine("<tpp_color_G>" + s.getCustomizedColor().getGreen() + "</tpp_color_G>");
            memo1.addLine("<tpp_color_B>" + s.getCustomizedColor().getBlue() + "</tpp_color_B>");

            memo1.addLine("座標," + s.getAX() + "," + s.getAY() + "," + s.getBX() + "," + s.getBY());
        }

        memo1.addLine("<円集合>");
        for (int i = 1; i <= numCircles(); i++) {
            memo1.addLine("番号," + i);
            Circle e_temp = new Circle();
            e_temp.set(getCircle(i));
            memo1.addLine("中心と半径と色," + e_temp.getX() + "," + e_temp.getY() + "," + e_temp.getRadius() + "," + e_temp.getColor());

            memo1.addLine("<tpp>" + e_temp.getCustomized() + "</tpp>");
            memo1.addLine("<tpp_color_R>" + e_temp.getCustomizedColor().getRed() + "</tpp_color_R>");
            memo1.addLine("<tpp_color_G>" + e_temp.getCustomizedColor().getGreen() + "</tpp_color_G>");
            memo1.addLine("<tpp_color_B>" + e_temp.getCustomizedColor().getBlue() + "</tpp_color_B>");
        }

        return memo1;
    }

    //Output the information of all line segments of the line segment set as Memo. // Iactive does not write out the fold line of excluding in the memo
    public Memo getMemo_active_excluding(LineSegment.ActiveState excluding) {
        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<線分集合>");

        int number = 0;
        for (int i = 1; i <= total; i++) {
            if (getActive(i) != excluding) {
                number++;
                memo1.addLine("番号," + number);
                LineSegment s = getLineSegment(i);
                memo1.addLine("色," + s.getColor());

                memo1.addLine("<tpp>" + s.getCustomized() + "</tpp>");
                memo1.addLine("<tpp_color_R>" + s.getCustomizedColor().getRed() + "</tpp_color_R>");
                memo1.addLine("<tpp_color_G>" + s.getCustomizedColor().getGreen() + "</tpp_color_G>");
                memo1.addLine("<tpp_color_B>" + s.getCustomizedColor().getBlue() + "</tpp_color_B>");


                memo1.addLine("選択," + s.getSelected());
                memo1.addLine("座標," + s.getAX() + "," + s.getAY() + "," + s.getBX() + "," + s.getBY());
            }
        }

        memo1.addLine("<円集合>");
        for (int i = 1; i <= numCircles(); i++) {
            memo1.addLine("番号," + i);
            Circle e_temp = new Circle();
            e_temp.set(getCircle(i));
            memo1.addLine("中心と半径と色," + e_temp.getX() + "," + e_temp.getY() + "," + e_temp.getRadius() + "," + e_temp.getColor());

            memo1.addLine("<tpp>" + e_temp.getCustomized() + "</tpp>");
            memo1.addLine("<tpp_color_R>" + e_temp.getCustomizedColor().getRed() + "</tpp_color_R>");
            memo1.addLine("<tpp_color_G>" + e_temp.getCustomizedColor().getGreen() + "</tpp_color_G>");
            memo1.addLine("<tpp_color_B>" + e_temp.getCustomizedColor().getBlue() + "</tpp_color_B>");
        }

        return memo1;
    }

    //Outputs the information of all line segments of the auxiliary line segment set as Memo.
    public Memo h_getMemo() {
        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<補助線分集合>");

        for (int i = 1; i <= total; i++) {
            memo1.addLine("補助番号," + i);
            LineSegment s = getLineSegment(i);
            memo1.addLine("補助色," + s.getColor());

            memo1.addLine("<tpp>" + s.getCustomized() + "</tpp>");
            memo1.addLine("<tpp_color_R>" + s.getCustomizedColor().getRed() + "</tpp_color_R>");
            memo1.addLine("<tpp_color_G>" + s.getCustomizedColor().getGreen() + "</tpp_color_G>");
            memo1.addLine("<tpp_color_B>" + s.getCustomizedColor().getBlue() + "</tpp_color_B>");

            memo1.addLine("補助座標," + s.getAX() + "," + s.getAY() + "," + s.getBX() + "," + s.getBY());
        }
        return memo1;
    }

    //Output the line segment set information as Memo for folding estimation. // Do not write out auxiliary lines with icol of 3 (cyan = light blue) or more in the memo
    public Memo getMemo_for_folding() {
        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<線分集合>");

        int ibangou = 0;
        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = getLineSegment(i);
            if (s.getColor().isFoldingLine()) {
                ibangou = ibangou + 1;
                memo1.addLine("番号," + ibangou);

                memo1.addLine("色," + s.getColor());

                memo1.addLine("<tpp>" + s.getCustomized() + "</tpp>");
                memo1.addLine("<tpp_color_R>" + s.getCustomizedColor().getRed() + "</tpp_color_R>");
                memo1.addLine("<tpp_color_G>" + s.getCustomizedColor().getGreen() + "</tpp_color_G>");
                memo1.addLine("<tpp_color_B>" + s.getCustomizedColor().getBlue() + "</tpp_color_B>");

                memo1.addLine("座標," + s.getAX() + "," + s.getAY() + "," + s.getBX() + "," + s.getBY());
            }
        }
        return memo1;
    }

    //Output the line segment set information as Memo for folding estimation. // Do not write out auxiliary lines with icol of 3 (cyan = light blue) or more in the memo
    public Memo getMemo_for_select_folding() {
        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<線分集合>");

        int number = 0;
        for (int i = 1; i <= total; i++) {
            LineSegment s = getLineSegment(i);
            if ((s.getColor().isFoldingLine()) && (s.getSelected() == 2)) {
                number++;
                memo1.addLine("番号," + number);

                memo1.addLine("色," + s.getColor());

                memo1.addLine("<tpp>" + s.getCustomized() + "</tpp>");
                memo1.addLine("<tpp_color_R>" + s.getCustomizedColor().getRed() + "</tpp_color_R>");
                memo1.addLine("<tpp_color_G>" + s.getCustomizedColor().getGreen() + "</tpp_color_G>");
                memo1.addLine("<tpp_color_B>" + s.getCustomizedColor().getBlue() + "</tpp_color_B>");


                memo1.addLine("座標," + s.getAX() + "," + s.getAY() + "," + s.getBX() + "," + s.getBY());
            }
        }
        return memo1;
    }

    //The number of broken lines of the line segment set selected for folding estimation is output as an int.
    // Do not count auxiliary lines with icol of 3 (cyan = light blue) or more
    public int getFoldLineTotalForSelectFolding() {
        int number = 0;
        for (int i = 1; i <= total; i++) {
            LineSegment s = getLineSegment(i);
            if (s.getColor().isFoldingLine() && s.getSelected() == 2) {
                number++;
            }
        }
        return number;
    }

    public String setMemo(Memo memo1) {//The return value is the title used for recording undo and redo.

        int reading_flag = 0;//If it is 0, it will not be read. If it is 1, read it.
        int number = 0;
        LineColor ic;
        LineSegment.ActiveState is;


        String r_title;
        r_title = "_";

        double ax, ay, bx, by;
        double dx, dy, dr;

        String str;

        reset();

        //Read the file .orh for Orihime

        //First find the total number of line segments
        int numLines = 0;
        for (int i = 1; i <= memo1.getLineCount(); i++) {
            StringTokenizer tk = new StringTokenizer(memo1.getLine(i), ",");

            str = tk.nextToken();
            if (str.equals("<線分集合>")) {
                reading_flag = 1;
            }
            if (str.equals("<円集合>")) {
                reading_flag = 3;
            }
            if ((reading_flag == 1) && (str.equals("番号"))) {
                numLines++;
            }
        }
        total = numLines;
        //First the total number of line segments was calculated

        Circle e_temp = new Circle();

        for (int i = 1; i <= memo1.getLineCount(); i++) {
            String str_i = memo1.getLine(i);

            //Old-fashioned reading method
            StringTokenizer tk = new StringTokenizer(str_i, ",");
            str = tk.nextToken();

            if (str.equals("<タイトル>")) {
                reading_flag = 2;
            }
            if ((reading_flag == 2) && (str.equals("タイトル"))) {
                str = tk.nextToken();
                r_title = str;
            }

            if (str.equals("<線分集合>")) {
                reading_flag = 1;
            }
            if ((reading_flag == 1) && (str.equals("番号"))) {
                str = tk.nextToken();
                number = Integer.parseInt(str);
            }
            if ((reading_flag == 1) && (str.equals("色"))) {
                str = tk.nextToken();
                ic = LineColor.from(str);
                LineSegment s = getLineSegment(number);
                s.setColor(ic);
            }

            if (reading_flag == 1) {
                st_new = str_i.split(">", 2);// <-----------------------------------２つに分割するときは2を指定
                if (st_new[0].equals("<tpp")) {
                    s_new = st_new[1].split("<", 2);
                    i_customized = (Integer.parseInt(s_new[0]));
                    LineSegment s = getLineSegment(number);
                    s.setCustomized(i_customized);
                }

                if (st_new[0].equals("<tpp_color_R")) {
                    s_new = st_new[1].split("<", 2);
                    i_customized_color_R = (Integer.parseInt(s_new[0]));
                    LineSegment s = getLineSegment(number);
                    s.setCustomizedColor(new Color(i_customized_color_R, i_customized_color_G, i_customized_color_B));
                }

                if (st_new[0].equals("<tpp_color_G")) {
                    s_new = st_new[1].split("<", 2);
                    i_customized_color_G = (Integer.parseInt(s_new[0]));
                    LineSegment s = getLineSegment(number);
                    s.setCustomizedColor(new Color(i_customized_color_R, i_customized_color_G, i_customized_color_B));
                }
                if (st_new[0].equals("<tpp_color_B")) {
                    s_new = st_new[1].split("<", 2);
                    i_customized_color_B = (Integer.parseInt(s_new[0]));
                    LineSegment s = getLineSegment(number);
                    s.setCustomizedColor(new Color(i_customized_color_R, i_customized_color_G, i_customized_color_B));
                }
            }

            if ((reading_flag == 1) && (str.equals("iactive"))) {//20181110追加
                str = tk.nextToken();
                is = LineSegment.ActiveState.valueOf(str);
                LineSegment s = getLineSegment(number);
                s.setActive(is);
            }

            if ((reading_flag == 1) && (str.equals("iva"))) {
                str = tk.nextToken();
                int iva = Integer.parseInt(str);
                LineSegment s = getLineSegment(number);
                s.setVonoroiA(iva);
            }

            if ((reading_flag == 1) && (str.equals("ivb"))) {
                str = tk.nextToken();
                int ivb = Integer.parseInt(str);
                LineSegment s = getLineSegment(number);
                s.setVonoroiB(ivb);
            }

            if ((reading_flag == 1) && (str.equals("選択"))) {
                str = tk.nextToken();
                int isel = Integer.parseInt(str);
                LineSegment s = getLineSegment(number);
                s.setSelected(isel);
            }
            if ((reading_flag == 1) && (str.equals("座標"))) {
                str = tk.nextToken();
                ax = Double.parseDouble(str);
                str = tk.nextToken();
                ay = Double.parseDouble(str);
                str = tk.nextToken();
                bx = Double.parseDouble(str);
                str = tk.nextToken();
                by = Double.parseDouble(str);

                LineSegment s = getLineSegment(number);
                s.set(ax, ay, bx, by);
            }

            if (str.equals("<円集合>")) {
                reading_flag = 3;
            }

            if ((reading_flag == 3) && (str.equals("番号"))) {
                str = tk.nextToken();
                number = Integer.parseInt(str);

                cir_setCircle(number, e_temp);
            }

            if ((reading_flag == 3) && (str.equals("中心と半径と色"))) {
                str = tk.nextToken();
                dx = Double.parseDouble(str);
                str = tk.nextToken();
                dy = Double.parseDouble(str);
                str = tk.nextToken();
                dr = Double.parseDouble(str);

                str = tk.nextToken();
                ic = LineColor.from(str);


                getCircle(number).set(dx, dy, dr, ic);
            }

            if (reading_flag == 3) {
                st_new = str_i.split(">", 2);// <-----------------------------------２つに分割するときは2を指定
                if (st_new[0].equals("<tpp")) {
                    s_new = st_new[1].split("<", 2);
                    i_customized = (Integer.parseInt(s_new[0]));
                    getCircle(number).setCustomized(i_customized);
                }

                if (st_new[0].equals("<tpp_color_R")) {
                    s_new = st_new[1].split("<", 2);
                    i_customized_color_R = (Integer.parseInt(s_new[0]));
                    getCircle(number).setCustomizedColor(new Color(i_customized_color_R, i_customized_color_G, i_customized_color_B));
                }

                if (st_new[0].equals("<tpp_color_G")) {
                    s_new = st_new[1].split("<", 2);
                    i_customized_color_G = (Integer.parseInt(s_new[0]));
                    getCircle(number).setCustomizedColor(new Color(i_customized_color_R, i_customized_color_G, i_customized_color_B));
                }
                if (st_new[0].equals("<tpp_color_B")) {
                    s_new = st_new[1].split("<", 2);
                    i_customized_color_B = (Integer.parseInt(s_new[0]));
                    getCircle(number).setCustomizedColor(new Color(i_customized_color_R, i_customized_color_G, i_customized_color_B));
                }
            }
        }
        return r_title;
    }

    //Arrangement of line segment set when inputting development drawing
    public void h_setMemo(Memo memo1) {
        int reading_flg = 0;//If it is 0, it will not be read. If it is 1, read it.
        int number = 0;
        LineColor ic;
        int is;

        double ax, ay, bx, by;
        String str;

        //Read the file .orh for Orihime

        //First find the total number of line segments
        int numLines = 0;
        for (int i = 1; i <= memo1.getLineCount(); i++) {

            StringTokenizer tk = new StringTokenizer(memo1.getLine(i), ",");

            str = tk.nextToken();
            if (str.equals("<補助線分集合>")) {
                reading_flg = 1;
            }
            if ((reading_flg == 1) && (str.equals("補助番号"))) {
                numLines++;
            }
        }
        total = numLines;
        //First the total number of auxiliary line segments was calculated

        for (int i = 1; i <= memo1.getLineCount(); i++) {
            StringTokenizer tk = new StringTokenizer(memo1.getLine(i), ",");
            str = tk.nextToken();
            if (str.equals("<補助線分集合>")) {
                reading_flg = 1;
            }
            if ((reading_flg == 1) && (str.equals("補助番号"))) {
                str = tk.nextToken();
                number = Integer.parseInt(str);
            }
            if ((reading_flg == 1) && (str.equals("補助色"))) {
                str = tk.nextToken();
                ic = LineColor.from(str);
                LineSegment s = getLineSegment(number);
                s.setColor(ic);
            }
            if ((reading_flg == 1) && (str.equals("補助選択"))) {
                str = tk.nextToken();
                is = Integer.parseInt(str);
                LineSegment s = getLineSegment(number);
                s.setSelected(is);
            }
            if ((reading_flg == 1) && (str.equals("補助座標"))) {
                str = tk.nextToken();
                ax = Double.parseDouble(str);
                str = tk.nextToken();
                ay = Double.parseDouble(str);
                str = tk.nextToken();
                bx = Double.parseDouble(str);
                str = tk.nextToken();
                by = Double.parseDouble(str);

                LineSegment s = getLineSegment(number);
                s.set(ax, ay, bx, by);
            }
        }
    }

    public void addMemo(Memo memo1) {
        int reading_flag = 0;//If it is 0, it will not be read. If it is 1, read it.
        int number = 0;
        LineColor ic;

        double ax, ay, bx, by;
        double dx, dy, dr;

        String str;

        int total_old = total;

        //Read the file .orh for Orihime

        //First find the total number of line segments
        int numLines = 0;
        for (int i = 1; i <= memo1.getLineCount(); i++) {

            StringTokenizer tk = new StringTokenizer(memo1.getLine(i), ",");

            str = tk.nextToken();
            if (str.equals("<線分集合>")) {
                reading_flag = 1;
            }
            if ((reading_flag == 1) && (str.equals("番号"))) {
                numLines++;
            }
        }
        total = total_old + numLines;


        //First the total number of line segments was calculated
        for (int i = 1; i <= memo1.getLineCount(); i++) {

            String str_i = memo1.getLine(i);

            //Old-fashioned reading method
            StringTokenizer tk = new StringTokenizer(str_i, ",");
            str = tk.nextToken();

            if (str.equals("<線分集合>")) {
                reading_flag = 1;
            }
            if ((reading_flag == 1) && (str.equals("番号"))) {
                str = tk.nextToken();
                number = total_old + Integer.parseInt(str);
            }
            if ((reading_flag == 1) && (str.equals("色"))) {
                str = tk.nextToken();
                ic = LineColor.from(str);
                LineSegment s = getLineSegment(number);
                s.setColor(ic);
            }

            if (reading_flag == 1) {
                st_new = str_i.split(">", 2);// <-----------------------------------２つに分割するときは2を指定
                if (st_new[0].equals("<tpp")) {
                    s_new = st_new[1].split("<", 2);
                    i_customized = (Integer.parseInt(s_new[0]));
                    LineSegment s = getLineSegment(number);
                    s.setCustomized(i_customized);
                }

                if (st_new[0].equals("<tpp_color_R")) {
                    s_new = st_new[1].split("<", 2);
                    i_customized_color_R = (Integer.parseInt(s_new[0]));
                    LineSegment s = getLineSegment(number);
                    s.setCustomizedColor(new Color(i_customized_color_R, i_customized_color_G, i_customized_color_B));
                }

                if (st_new[0].equals("<tpp_color_G")) {
                    s_new = st_new[1].split("<", 2);
                    i_customized_color_G = (Integer.parseInt(s_new[0]));
                    LineSegment s = getLineSegment(number);
                    s.setCustomizedColor(new Color(i_customized_color_R, i_customized_color_G, i_customized_color_B));
                }
                if (st_new[0].equals("<tpp_color_B")) {
                    s_new = st_new[1].split("<", 2);
                    i_customized_color_B = (Integer.parseInt(s_new[0]));
                    LineSegment s = getLineSegment(number);
                    s.setCustomizedColor(new Color(i_customized_color_R, i_customized_color_G, i_customized_color_B));
                }
            }

            if ((reading_flag == 1) && (str.equals("座標"))) {
                str = tk.nextToken();
                ax = Double.parseDouble(str);
                str = tk.nextToken();
                ay = Double.parseDouble(str);
                str = tk.nextToken();
                bx = Double.parseDouble(str);
                str = tk.nextToken();
                by = Double.parseDouble(str);

                LineSegment s = getLineSegment(number);
                s.set(ax, ay, bx, by);
            }

            if (str.equals("<円集合>")) {
                reading_flag = 3;
            }

            if ((reading_flag == 3) && (str.equals("番号"))) {
                str = tk.nextToken();
                circles.add(new Circle(0.0, 0.0, 1.0, LineColor.RED_1));
                number = numCircles();
            }

            if ((reading_flag == 3) && (str.equals("中心と半径と色"))) {
                str = tk.nextToken();
                dx = Double.parseDouble(str);
                str = tk.nextToken();
                dy = Double.parseDouble(str);
                str = tk.nextToken();
                dr = Double.parseDouble(str);

                str = tk.nextToken();
                ic = LineColor.from(str);

                getCircle(number).set(dx, dy, dr, ic);
            }

            if (reading_flag == 3) {
                st_new = str_i.split(">", 2);// <-----------------------------------Specify 2 when splitting into two
                if (st_new[0].equals("<tpp")) {
                    s_new = st_new[1].split("<", 2);
                    i_customized = (Integer.parseInt(s_new[0]));
                    getCircle(number).setCustomized(i_customized);
                }

                if (st_new[0].equals("<tpp_color_R")) {
                    s_new = st_new[1].split("<", 2);
                    i_customized_color_R = (Integer.parseInt(s_new[0]));
                    getCircle(number).setCustomizedColor(new Color(i_customized_color_R, i_customized_color_G, i_customized_color_B));
                }        //  System.out.println(Integer.parseInt(s[0])) ;

                if (st_new[0].equals("<tpp_color_G")) {
                    s_new = st_new[1].split("<", 2);
                    i_customized_color_G = (Integer.parseInt(s_new[0]));
                    getCircle(number).setCustomizedColor(new Color(i_customized_color_R, i_customized_color_G, i_customized_color_B));
                }
                if (st_new[0].equals("<tpp_color_B")) {
                    s_new = st_new[1].split("<", 2);
                    i_customized_color_B = (Integer.parseInt(s_new[0]));
                    getCircle(number).setCustomizedColor(new Color(i_customized_color_R, i_customized_color_G, i_customized_color_B));
                }
            }
        }
    }

    //Replace the mountains and valleys of all lines. There is no change in line types other than mountains and valleys such as boundaries.
    public void allMountainValleyChange() {
        for (int ic_id = 1; ic_id <= total; ic_id++) {
            setColor(ic_id, getColor(ic_id).changeMV());
        }
    }

    //Output the information of all line segments of the line segment set as Memo.
    // Do not write down the fold line of select except
    public Memo getMemoExceptSelected(int except) {
        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<線分集合>");

        int number = 0;
        for (int i = 1; i <= total; i++) {
            LineSegment s = getLineSegment(i);

            if (s.getSelected() != except) {
                number++;
                memo1.addLine("番号," + number);
                memo1.addLine("色," + s.getColor());

                memo1.addLine("<tpp>" + s.getCustomized() + "</tpp>");
                memo1.addLine("<tpp_color_R>" + s.getCustomizedColor().getRed() + "</tpp_color_R>");
                memo1.addLine("<tpp_color_G>" + s.getCustomizedColor().getGreen() + "</tpp_color_G>");
                memo1.addLine("<tpp_color_B>" + s.getCustomizedColor().getBlue() + "</tpp_color_B>");

                memo1.addLine("iactive," + s.getActive());//20181110追加
                memo1.addLine("iva," + s.getVonoroiA());
                memo1.addLine("ivb," + s.getVonoroiB());
                memo1.addLine("座標," + s.getAX() + "," + s.getAY() + "," + s.getBX() + "," + s.getBY());
            }
        }


        memo1.addLine("<円集合>");
        for (int i = 1; i <= numCircles(); i++) {
            memo1.addLine("番号," + i);
            Circle e_temp = new Circle();
            e_temp.set(getCircle(i));
            memo1.addLine("中心と半径と色," + e_temp.getX() + "," + e_temp.getY() + "," + e_temp.getRadius() + "," + e_temp.getColor());

            memo1.addLine("<tpp>" + e_temp.getCustomized() + "</tpp>");
            memo1.addLine("<tpp_color_R>" + e_temp.getCustomizedColor().getRed() + "</tpp_color_R>");
            memo1.addLine("<tpp_color_G>" + e_temp.getCustomizedColor().getGreen() + "</tpp_color_G>");
            memo1.addLine("<tpp_color_B>" + e_temp.getCustomizedColor().getBlue() + "</tpp_color_B>");
        }

        return memo1;
    }

    //Output the information of all line segments of the line segment set as Memo.
    // select writes out option polygonal line in a memo
    public Memo getMemoSelectOption(int option) {
        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<線分集合>");

        int number = 0;
        for (int i = 1; i <= total; i++) {
            LineSegment s = getLineSegment(i);

            if (s.getSelected() == option) {
                number++;
                memo1.addLine("番号," + number);
                memo1.addLine("色," + s.getColor());

                memo1.addLine("<tpp>" + s.getCustomized() + "</tpp>");
                memo1.addLine("<tpp_color_R>" + s.getCustomizedColor().getRed() + "</tpp_color_R>");
                memo1.addLine("<tpp_color_G>" + s.getCustomizedColor().getGreen() + "</tpp_color_G>");
                memo1.addLine("<tpp_color_B>" + s.getCustomizedColor().getBlue() + "</tpp_color_B>");

                memo1.addLine("座標," + s.getAX() + "," + s.getAY() + "," + s.getBX() + "," + s.getBY());
            }
        }
        return memo1;
    }

    public void select_all() {
        for (int i = 1; i <= total; i++) {
            LineSegment s = getLineSegment(i);
            s.setSelected(2);
        }
    }

    public void unselect_all() {
        for (int i = 1; i <= total; i++) {
            LineSegment s = getLineSegment(i);
            s.setSelected(0);
        }
    }

    public void select(int i) {
        LineSegment s = getLineSegment(i);
        s.setSelected(2);
    }

    public void select(Point p1, Point p2, Point p3) {
        Polygon triangle = new Polygon(3);
        triangle.set(1, p1);
        triangle.set(2, p2);
        triangle.set(3, p3);

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = getLineSegment(i);
            if (triangle.totu_boundary_inside(s)) {
                s.setSelected(2);
            }
        }
    }

    public void select(Point p1, Point p2, Point p3, Point p4) {
        Polygon sikaku = new Polygon(4);
        sikaku.set(1, p1);
        sikaku.set(2, p2);
        sikaku.set(3, p3);
        sikaku.set(4, p4);

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = getLineSegment(i);
            if (sikaku.totu_boundary_inside(s)) {

                s.setSelected(2);

            }
        }
    }

    public void unselect(Point p1, Point p2, Point p3) {
        Polygon sankaku = new Polygon(3);
        sankaku.set(1, p1);
        sankaku.set(2, p2);
        sankaku.set(3, p3);


        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = getLineSegment(i);
            if (sankaku.totu_boundary_inside(s)) {

                s.setSelected(0);

            }
        }
    }
//--------------------------------

    public void unselect(Point p1, Point p2, Point p3, Point p4) {
        //Ten p1 = new Ten();   p1.set(si.geta());
        Polygon sikaku = new Polygon(4);
        sikaku.set(1, p1);
        sikaku.set(2, p2);
        sikaku.set(3, p3);
        sikaku.set(4, p4);

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = getLineSegment(i);
            if (sikaku.totu_boundary_inside(s)) {
                s.setSelected(0);
            }
        }
    }

    //--------------------------------
    public int MV_change(Point p1, Point p2, Point p3, Point p4) {
        int i_r = 0;
        //Ten p1 = new Ten();   p1.set(si.geta());
        Polygon sikaku = new Polygon(4);
        sikaku.set(1, p1);
        sikaku.set(2, p2);
        sikaku.set(3, p3);
        sikaku.set(4, p4);

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = getLineSegment(i);
            if (sikaku.totu_boundary_inside(s)) {
                LineColor ic_temp;
                ic_temp = getColor(i);/**/
                if (ic_temp == LineColor.RED_1) {
                    setColor(i, LineColor.BLUE_2);
                } else if (ic_temp == LineColor.BLUE_2) {
                    setColor(i, LineColor.RED_1);
                }
                i_r = 1;
            }
        }
        return i_r;
    }

    //--------------------------------
    public int M_nisuru(Point p1, Point p2, Point p3, Point p4) {
        int i_r = 0;
        //Ten p1 = new Ten();   p1.set(si.geta());
        Polygon sikaku = new Polygon(4);
        sikaku.set(1, p1);
        sikaku.set(2, p2);
        sikaku.set(3, p3);
        sikaku.set(4, p4);

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = getLineSegment(i);
            if (sikaku.totu_boundary_inside(s)) {
                s.setColor(LineColor.RED_1);
                i_r = 1;
            }
        }
        return i_r;
    }

    //--------------------------------
    public int V_nisuru(Point p1, Point p2, Point p3, Point p4) {
        int i_r = 0;
        //Ten p1 = new Ten();   p1.set(si.geta());
        Polygon sikaku = new Polygon(4);
        sikaku.set(1, p1);
        sikaku.set(2, p2);
        sikaku.set(3, p3);
        sikaku.set(4, p4);

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = getLineSegment(i);
            if (sikaku.totu_boundary_inside(s)) {
                s.setColor(LineColor.BLUE_2);
                i_r = 1;
            }
        }
        return i_r;
    }


    public int E_nisuru(Point p1, Point p2, Point p3, Point p4) {
        int i_r = 0;
        //Ten p1 = new Ten();   p1.set(si.geta());
        Polygon sikaku = new Polygon(4);
        sikaku.set(1, p1);
        sikaku.set(2, p2);
        sikaku.set(3, p3);
        sikaku.set(4, p4);

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = getLineSegment(i);
            if (sikaku.totu_boundary_inside(s)) {
                s.setColor(LineColor.BLACK_0);
                i_r = 1;
            }
        }
        return i_r;

    }


//--------------------------------

    //--------------------------------
    public int HK_nisuru(Point p1, Point p2, Point p3, Point p4) {
        int i_r = 0;
        //Ten p1 = new Ten();   p1.set(si.geta());
        Polygon sikaku = new Polygon(4);
        sikaku.set(1, p1);
        sikaku.set(2, p2);
        sikaku.set(3, p3);
        sikaku.set(4, p4);


        int okikae_suu = 0;
        for (int i = 1; i <= total; i++) {
            if (getColor(i).isFoldingLine()) {
                LineSegment s;
                s = getLineSegment(i);
                if (sikaku.totu_boundary_inside(s)) {
                    okikae_suu = okikae_suu + 1;

                    LineSegment add_sen = new LineSegment();
                    add_sen.set(s);
                    add_sen.setColor(LineColor.CYAN_3);

                    deleteLine(i);
                    addLine(add_sen);
                    i = i - 1;

                    i_r = 1;
                }
            }
        }

        int kawatteinai_kazu = total - okikae_suu;
        if (kawatteinai_kazu == 0) {
            intersect_divide();
        }
        if (kawatteinai_kazu >= 1) {
            if (okikae_suu >= 1) {
                intersect_divide(1, total - okikae_suu, total - okikae_suu + 1, total);
            }
        }
//上２行の場合わけが必要な理由は、kousabunkatu()をやってしまうと折線と補助活線との交点で折線が分割されるから。kousabunkatu(1,sousuu-okikae_suu,sousuu-okikae_suu+1,sousuu)だと折線は分割されない。


        return i_r;

    }

    public int D_nisuru_line(LineSegment s_step1, String Dousa_mode) {
        //"l"  lXは小文字のエル。Senbun s_step1と重複する部分のある線分を削除するモード。
        //"lX" lXは小文字のエルと大文字のエックス。Senbun s_step1と重複する部分のある線分やX交差する線分を削除するモード。
        int i_r = 0;//たくさんある折線のうち、一本でも削除すれば1、1本も削除しないなら0。

        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<線分集合>");
        int ibangou = 0;

        boolean i_kono_orisen_wo_sakujyo;//i_この折線を削除　0削除しない、1削除する
        for (int i = 1; i <= total; i++) {

            LineSegment s;
            s = getLineSegment(i);

            i_kono_orisen_wo_sakujyo = false;

            if (Dousa_mode.equals("l")) {
                if (OritaCalc.lineSegmentoverlapping(s, s_step1)) {
                    i_kono_orisen_wo_sakujyo = true;
                }
            }

            if (Dousa_mode.equals("lX")) {
                if (OritaCalc.lineSegmentoverlapping(s, s_step1)) {
                    i_kono_orisen_wo_sakujyo = true;
                }
                if (OritaCalc.lineSegment_X_kousa_decide(s, s_step1)) {
                    i_kono_orisen_wo_sakujyo = true;
                }
            }


            if (i_kono_orisen_wo_sakujyo) {
                i_r = 1;
            }
            if (!i_kono_orisen_wo_sakujyo) {
                ibangou = ibangou + 1;
                memo1.addLine("番号," + ibangou);
                memo1.addLine("色," + s.getColor());
                memo1.addLine("座標," + s.getAX() + "," + s.getAY() + "," + s.getBX() + "," + s.getBY());
            }
        }

        Point ec = new Point();//円の中心座標を入れる変数

        memo1.addLine("<円集合>");
        int ii = 0;
        for (int i = 1; i <= numCircles(); i++) {
            boolean idel = false;
            Circle e_temp = new Circle();
            e_temp.set(getCircle(i));
            ec.set(e_temp.getCenter());

            if (idel) {
                i_r = 1;
            }
            if (!idel) {
                ii = ii + 1;
                memo1.addLine("番号," + ii);
                memo1.addLine("中心と半径と色," + e_temp.getX() + "," + e_temp.getY() + "," + e_temp.getRadius() + "," + e_temp.getColor());
            }
        }

        reset();
        setMemo(memo1);

        return i_r;
    }

    //-----------------------wwwwwwwwwwwwwww---------
    public int D_nisuru(Point p1, Point p2, Point p3, Point p4) {
        int i_r = 0;
        Polygon sikaku = new Polygon(4);
        sikaku.set(1, p1);
        sikaku.set(2, p2);
        sikaku.set(3, p3);
        sikaku.set(4, p4);

        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<線分集合>");
        int number = 0;

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = getLineSegment(i);

            if (sikaku.totu_boundary_inside(s)) {
                i_r = 1;
            }
            if (!sikaku.totu_boundary_inside(s)) {
                number = number + 1;
                memo1.addLine("番号," + number);
                memo1.addLine("色," + s.getColor());
                memo1.addLine("座標," + s.getAX() + "," + s.getAY() + "," + s.getBX() + "," + s.getBY());
            }
        }

        Point ec = new Point();//円の中心座標を入れる変数
        double er;//円の中心座標を入れる変数

        LineSegment s1 = new LineSegment(p1, p2);
        LineSegment s2 = new LineSegment(p2, p3);
        LineSegment s3 = new LineSegment(p3, p4);
        LineSegment s4 = new LineSegment(p4, p1);

        memo1.addLine("<円集合>");
        int ii = 0;
        for (int i = 1; i <= numCircles(); i++) {
            boolean idel = false;
            Circle e_temp = new Circle();
            e_temp.set(getCircle(i));
            ec.set(e_temp.getCenter());
            er = e_temp.getRadius();

            if (OritaCalc.distance_lineSegment(ec, s1) <= er) {
                if ((OritaCalc.distance(s1.getA(), ec) >= er) || (OritaCalc.distance(s1.getA(), ec) >= er)) {
                    idel = true;
                }
            }
            if (OritaCalc.distance_lineSegment(ec, s2) <= er) {
                if ((OritaCalc.distance(s2.getA(), ec) >= er) || (OritaCalc.distance(s2.getA(), ec) >= er)) {
                    idel = true;
                }
            }
            if (OritaCalc.distance_lineSegment(ec, s3) <= er) {
                if ((OritaCalc.distance(s3.getA(), ec) >= er) || (OritaCalc.distance(s3.getA(), ec) >= er)) {
                    idel = true;
                }
            }
            if (OritaCalc.distance_lineSegment(ec, s4) <= er) {
                if ((OritaCalc.distance(s4.getA(), ec) >= er) || (OritaCalc.distance(s4.getA(), ec) >= er)) {
                    idel = true;
                }
            }

            if (sikaku.totu_boundary_inside(new LineSegment(e_temp.getCenter(), e_temp.getCenter()))) {
                idel = true;
            }

            if (idel) {
                i_r = 1;
            }
            if (!idel) {
                ii = ii + 1;
                memo1.addLine("番号," + ii);
                memo1.addLine("中心と半径と色," + e_temp.getX() + "," + e_temp.getY() + "," + e_temp.getRadius() + "," + e_temp.getColor());
            }
        }

        reset();
        setMemo(memo1);

        return i_r;
    }

    //--------------------------------
    public int D_nisuru0(Point p1, Point p2, Point p3, Point p4) {//Delete only the polygonal line
        int i_r = 0;
        Polygon sikaku = new Polygon(4);
        sikaku.set(1, p1);
        sikaku.set(2, p2);
        sikaku.set(3, p3);
        sikaku.set(4, p4);

        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<線分集合>");
        int ibangou = 0;

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = getLineSegment(i);

            if ((sikaku.totu_boundary_inside(s)) && getColor(i).isFoldingLine()) {
                i_r = 1;
            }//黒赤青線はmemo1に書かれない。つまり削除される。
            else if ((!sikaku.totu_boundary_inside(s)) || !getColor(i).isFoldingLine()) {
                ibangou = ibangou + 1;
                memo1.addLine("番号," + ibangou);
                memo1.addLine("色," + s.getColor());
                memo1.addLine("座標," + s.getAX() + "," + s.getAY() + "," + s.getBX() + "," + s.getBY());
            }
        }

        memo1.addLine("<円集合>");
        int ii = 0;
        for (int i = 1; i <= numCircles(); i++) {
            Circle e_temp = new Circle();
            e_temp.set(getCircle(i));//ec.set(e_temp.get_tyuusin());er=e_temp.getr();

            ii = ii + 1;
            memo1.addLine("番号," + ii);
            memo1.addLine("中心と半径と色," + e_temp.getX() + "," + e_temp.getY() + "," + e_temp.getRadius() + "," + e_temp.getColor());
        }

        reset();

        setMemo(memo1);

        return i_r;
    }

    public int D_nisuru2(Point p1, Point p2, Point p3, Point p4) {//折線のみ削除
        int i_r = 0;
        Polygon sikaku = new Polygon(4);
        sikaku.set(1, p1);
        sikaku.set(2, p2);
        sikaku.set(3, p3);
        sikaku.set(4, p4);

        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<線分集合>");
        int ibangou = 0;

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = getLineSegment(i);

            if ((sikaku.totu_boundary_inside(s)) && (getColor(i) == LineColor.BLACK_0)) {
                i_r = 1;
            }//黒線はmemo1に書かれない。つまり削除される。
            else if ((!sikaku.totu_boundary_inside(s)) || (getColor(i) != LineColor.BLACK_0)) {
                ibangou = ibangou + 1;
                memo1.addLine("番号," + ibangou);
                memo1.addLine("色," + s.getColor());
                memo1.addLine("座標," + s.getAX() + "," + s.getAY() + "," + s.getBX() + "," + s.getBY());
            }
        }

        memo1.addLine("<円集合>");
        int ii = 0;
        for (int i = 1; i <= numCircles(); i++) {
            Circle e_temp = new Circle();
            e_temp.set(getCircle(i));//ec.set(e_temp.get_tyuusin());er=e_temp.getr();
            ii = ii + 1;
            memo1.addLine("番号," + ii);
            memo1.addLine("中心と半径と色," + e_temp.getX() + "," + e_temp.getY() + "," + e_temp.getRadius() + "," + e_temp.getColor());
        }

        reset();
        setMemo(memo1);
        return i_r;
    }

    public int D_nisuru3(Point p1, Point p2, Point p3, Point p4) {//Delete only auxiliary live line
        int i_r = 0;
        Polygon sikaku = new Polygon(4);
        sikaku.set(1, p1);
        sikaku.set(2, p2);
        sikaku.set(3, p3);
        sikaku.set(4, p4);

        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<線分集合>");
        int ibangou = 0;

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = getLineSegment(i);

            if ((sikaku.totu_boundary_inside(s)) && (getColor(i) == LineColor.CYAN_3)) {
                i_r = 1;
            } else if ((!sikaku.totu_boundary_inside(s)) || (getColor(i) != LineColor.CYAN_3)) {
                ibangou = ibangou + 1;
                memo1.addLine("番号," + ibangou);
                memo1.addLine("色," + s.getColor());
                memo1.addLine("座標," + s.getAX() + "," + s.getAY() + "," + s.getBX() + "," + s.getBY());
            }
        }

        Point ec = new Point();//円の中心座標を入れる変数
        double er;//円の中心座標を入れる変数

        LineSegment s1 = new LineSegment(p1, p2);
        LineSegment s2 = new LineSegment(p2, p3);
        LineSegment s3 = new LineSegment(p3, p4);
        LineSegment s4 = new LineSegment(p4, p1);

        memo1.addLine("<円集合>");
        int ii = 0;
        for (int i = 1; i <= numCircles(); i++) {
            boolean idel = false;
            Circle e_temp = new Circle();
            e_temp.set(getCircle(i));
            ec.set(e_temp.getCenter());
            er = e_temp.getRadius();

            if (OritaCalc.distance_lineSegment(ec, s1) <= er) {
                if ((OritaCalc.distance(s1.getA(), ec) >= er) || (OritaCalc.distance(s1.getA(), ec) >= er)) {
                    idel = true;
                }
            }
            if (OritaCalc.distance_lineSegment(ec, s2) <= er) {
                if ((OritaCalc.distance(s2.getA(), ec) >= er) || (OritaCalc.distance(s2.getA(), ec) >= er)) {
                    idel = true;
                }
            }
            if (OritaCalc.distance_lineSegment(ec, s3) <= er) {
                if ((OritaCalc.distance(s3.getA(), ec) >= er) || (OritaCalc.distance(s3.getA(), ec) >= er)) {
                    idel = true;
                }
            }
            if (OritaCalc.distance_lineSegment(ec, s4) <= er) {
                if ((OritaCalc.distance(s4.getA(), ec) >= er) || (OritaCalc.distance(s4.getA(), ec) >= er)) {
                    idel = true;
                }
            }

            if (sikaku.totu_boundary_inside(new LineSegment(e_temp.getCenter(), e_temp.getCenter()))) {
                idel = true;
            }

            if (idel) {
                i_r = 1;
            }
            if (!idel) {
                ii = ii + 1;
                memo1.addLine("番号," + ii);
                memo1.addLine("中心と半径と色," + e_temp.getX() + "," + e_temp.getY() + "," + e_temp.getRadius() + "," + e_temp.getColor());
            }
        }

        reset();
        setMemo(memo1);

        return i_r;
    }

    //--------------------------------
    public int chenge_property_in_4kakukei(Point p1, Point p2, Point p3, Point p4, Color sen_tokutyuu_color) {//Change properties such as the color of circles and auxiliary live lines inside a quadrangle
        int i_r = 0;
        Polygon sikaku = new Polygon(4);
        sikaku.set(1, p1);
        sikaku.set(2, p2);
        sikaku.set(3, p3);
        sikaku.set(4, p4);

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = getLineSegment(i);

            if (sikaku.totu_boundary_inside(s) && (getColor(i) == LineColor.CYAN_3)) {
                i_r = 1;
                setLineCustomized(i, 1);
                setLineCustomizedColor(i, sen_tokutyuu_color);
            }
        }

        Point ec = new Point();//円の中心座標を入れる変数
        double er;//円の中心座標を入れる変数

        LineSegment s1 = new LineSegment(p1, p2);
        LineSegment s2 = new LineSegment(p2, p3);
        LineSegment s3 = new LineSegment(p3, p4);
        LineSegment s4 = new LineSegment(p4, p1);

        //("<円集合>");
        for (int i = 1; i <= numCircles(); i++) {
            int i_change = 0;
            Circle e_temp = new Circle();
            e_temp.set(getCircle(i));
            ec.set(e_temp.getCenter());
            er = e_temp.getRadius();

            if (OritaCalc.distance_lineSegment(ec, s1) <= er) {
                if ((OritaCalc.distance(s1.getA(), ec) >= er) || (OritaCalc.distance(s1.getA(), ec) >= er)) {
                    i_change = 1;
                }
            }
            if (OritaCalc.distance_lineSegment(ec, s2) <= er) {
                if ((OritaCalc.distance(s2.getA(), ec) >= er) || (OritaCalc.distance(s2.getA(), ec) >= er)) {
                    i_change = 1;
                }
            }
            if (OritaCalc.distance_lineSegment(ec, s3) <= er) {
                if ((OritaCalc.distance(s3.getA(), ec) >= er) || (OritaCalc.distance(s3.getA(), ec) >= er)) {
                    i_change = 1;
                }
            }
            if (OritaCalc.distance_lineSegment(ec, s4) <= er) {
                if ((OritaCalc.distance(s4.getA(), ec) >= er) || (OritaCalc.distance(s4.getA(), ec) >= er)) {
                    i_change = 1;
                }
            }

            if (sikaku.totu_boundary_inside(new LineSegment(e_temp.getCenter(), e_temp.getCenter()))) {
                i_change = 1;
            }

            if (i_change == 1) {
                i_r = 1;
                setCircleCustomized(i, 1);
                setCircleCustomizedColor(i, sen_tokutyuu_color);
            }
        }

        return i_r;
    }

    public void unselect(int i) {
        LineSegment s;
        s = getLineSegment(i);
        s.setSelected(0);
    }

    public int get_select(int i) {
        LineSegment s;
        s = getLineSegment(i);
        return s.getSelected();
    }

    public void set_select(int i, int isel) {
        LineSegment s;
        s = getLineSegment(i);
        s.setSelected(isel);
    }

    public void delSelectedLineSegmentFast() {
        Memo memo_temp = new Memo();
        memo_temp.set(getMemoExceptSelected(2));
        reset();
        setMemo(memo_temp);
    }

    public void deleteSelectedLineSegment() {
        int i_Flag = 1;
        while (i_Flag == 1) {
            i_Flag = del_selected_lineSegment_symple_roop();
        }
    }

    public int del_selected_lineSegment_symple_roop() {
        for (int i = 1; i <= total; i++) {
            if (get_select(i) == 2) {
                deleteLineSegment_vertex(i);
                return 1;
            }
        }
        return 0;
    }

    //Remove dotted line segments
    public void point_removal() {
        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = getLineSegment(i);
            if (OritaCalc.equal(s.getA(), s.getB())) {
                deleteLine(i);
                i = i - 1;
            }
        }
    }

    public void point_removal(double r) {
        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = getLineSegment(i);
            if (OritaCalc.equal(s.getA(), s.getB(), r)) {
                deleteLine(i);
                i = i - 1;
            }
        }
    }

    // When there are two completely overlapping line segments, the one with the later number is deleted.
    public void overlapping_line_removal(double r) {
        int[] removal_flg = new int[total + 1];
        LineSegment[] snew = new LineSegment[total + 1];
        for (int i = 1; i <= total; i++) {
            removal_flg[i] = 0;
            snew[i] = new LineSegment();
        }

        for (int i = 1; i <= total - 1; i++) {
            LineSegment si;
            si = getLineSegment(i);
            for (int j = i + 1; j <= total; j++) {
                LineSegment sj;
                sj = getLineSegment(j);
                if (r <= -9999.9) {
                    if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.PARALLEL_EQUAL_31) {
                        removal_flg[j] = 1;
                    }
                } else {
                    if (OritaCalc.line_intersect_decide(si, sj, r, r) == LineSegment.Intersection.PARALLEL_EQUAL_31) {
                        removal_flg[j] = 1;
                    }
                }
            }
        }

        int smax = 0;
        for (int i = 1; i <= total; i++) {
            if (removal_flg[i] == 0) {
                LineSegment si;
                si = getLineSegment(i);
                smax = smax + 1;
                snew[smax].set(si);
            }
        }

        total = smax;
        for (int i = 1; i <= total; i++) {
            LineSegment si;
            si = getLineSegment(i);
            si.set(snew[i]);
        }
    }

    public void overlapping_line_removal() {
        overlapping_line_removal(-10000.0);
    }

    //------------------zzzzzzzzz-------------------------------------------------------------------
    //Divide the two line segments at the intersection of the two intersecting line segments. If there were two line segments that completely overlapped, both would remain without any processing.
    public void intersect_divide(int i1, int i2, int i3, int i4) {//Crossing division when i3 to i4 fold lines are added to the original i1 to i2 fold lines
        for (int i = 1; i <= total; i++) {
            setActive(i, LineSegment.ActiveState.INACTIVE_0);
        }//削除すべき線は iactive=100とする
        //System.out.println("1234567890   kousabunkatu");
        ArrayList<Integer> k_flg = new ArrayList<>();//交差分割の影響があることを示すフラッグ。

        for (int i = 0; i <= total + 100; i++) {
            k_flg.add(0);
        }//0は交差分割の対象外、１は元からあった折線、2は加える折線として交差分割される。3は削除すべきと判定された折線
        for (int i = i1; i <= i2; i++) {
            k_flg.set(i, 1);
        }//0は交差分割の対象外、１は元からあった折線、2は加える折線として交差分割される
        for (int i = i3; i <= i4; i++) {
            k_flg.set(i, 2);
        }//0は交差分割の対象外、１は元からあった折線、2は加える折線として交差分割される
        for (int i = 1; i <= total; i++) {
            Integer I_k_flag = k_flg.get(i);
            if (I_k_flag == 2) {//k_flg.set(i,new Integer(0));
                for (int j = 1; j <= total; j++) {
                    if (i != j) {
                        Integer J_k_flag = k_flg.get(j);
                        if (J_k_flag == 1) {
                            LineSegment.Intersection itemp = intersect_divide_fast(i, j);//i is the one to add (2), j is the original one (1)
                            if (itemp == LineSegment.Intersection.INTERSECTS_1) {
                                k_flg.add(2);//For some reason this is added as 0 instead of 2. 20161130
                                k_flg.add(1);
                                k_flg.set(total - 1, 2);//
                                k_flg.set(total, 1);
                            }
                            if (itemp == LineSegment.Intersection.INTERSECTS_AUX_2) {
                                k_flg.add(2);//For some reason this is added as 0 instead of 2. 20161130
                                k_flg.set(total, 2);
                            }
                            if (itemp == LineSegment.Intersection.INTERSECTS_AUX_3) {
                                k_flg.add(1);//For some reason this is added as 0 instead of 2. 20161130
                                k_flg.set(total, 1);
                            }


                            if (itemp == LineSegment.Intersection.INTERSECT_T_A_121) {
                                k_flg.add(1);
                                k_flg.set(total, 1);

                            }
                            if (itemp == LineSegment.Intersection.INTERSECT_T_B_122) {
                                k_flg.add(1);
                                k_flg.set(total, 1);
                            }
                            if (itemp == LineSegment.Intersection.INTERSECT_T_A_211) {
                                k_flg.add(2);
                                k_flg.set(total, 2);

                            }
                            if (itemp == LineSegment.Intersection.INTERSECT_T_B_221) {
                                k_flg.add(2);
                                k_flg.set(total, 2);
                            }

                            if (itemp == LineSegment.Intersection.PARALLEL_S1_INCLUDES_S2_361) {
                                k_flg.set(j, 0);//何もしなくてもいいかも//ori_s_temp.senbun_bunkatu(s1.getb());//p1とp3が一致、siにsjが含まれる。加える折線をkousa_tenで分割すること
                                k_flg.add(2);
                                k_flg.set(total, 2);
                            }

                            if (itemp == LineSegment.Intersection.PARALLEL_S1_INCLUDES_S2_362) {
                                k_flg.set(j, 0);//何もしなくてもいいかも//ori_s_temp.senbun_bunkatu(s1.getb());//p1とp3が一致、siにsjが含まれる。加える折線をkousa_tenで分割すること
                                k_flg.add(2);
                                k_flg.set(total, 2);
                            }
                            if (itemp == LineSegment.Intersection.PARALLEL_S2_INCLUDES_S1_363) {
                                //k_flg.set(j,new Integer(0));//何もしなくてもいいかも//ori_s_temp.senbun_bunkatu(s1.getb());//p1とp3が一致、siにsjが含まれる。加える折線をkousa_tenで分割すること
                                k_flg.add(1);
                                k_flg.set(total, 1);
                            }
                            if (itemp == LineSegment.Intersection.PARALLEL_S2_INCLUDES_S1_364) {
                                //k_flg.set(j,new Integer(0));//何もしなくてもいいかも//ori_s_temp.senbun_bunkatu(s1.getb());//p1とp3が一致、siにsjが含まれる。加える折線をkousa_tenで分割すること
                                k_flg.add(1);
                                k_flg.set(total, 1);
                            }
                            if (itemp == LineSegment.Intersection.PARALLEL_S1_END_OVERLAPS_S2_START_371) {
                                //k_flg.set(j,new Integer(0));//何もしなくてもいいかも//ori_s_temp.senbun_bunkatu(s1.getb());//p1とp3が一致、siにsjが含まれる。加える折線をkousa_tenで分割すること
                                k_flg.add(0);
                                k_flg.set(total, 0);
                            }
                            if (itemp == LineSegment.Intersection.PARALLEL_S1_END_OVERLAPS_S2_END_372) {
                                //k_flg.set(j,new Integer(0));//何もしなくてもいいかも//ori_s_temp.senbun_bunkatu(s1.getb());//p1とp3が一致、siにsjが含まれる。加える折線をkousa_tenで分割すること
                                k_flg.add(0);
                                k_flg.set(total, 0);
                            }
                            if (itemp == LineSegment.Intersection.PARALLEL_S1_START_OVERLAPS_S2_END_373) {
                                //k_flg.set(j,new Integer(0));//何もしなくてもいいかも//ori_s_temp.senbun_bunkatu(s1.getb());//p1とp3が一致、siにsjが含まれる。加える折線をkousa_tenで分割すること
                                k_flg.add(0);
                                k_flg.set(total, 0);
                            }
                            if (itemp == LineSegment.Intersection.PARALLEL_S1_START_OVERLAPS_S2_START_374) {
                                //k_flg.set(j,new Integer(0));//何もしなくてもいいかも//ori_s_temp.senbun_bunkatu(s1.getb());//p1とp3が一致、siにsjが含まれる。加える折線をkousa_tenで分割すること
                                k_flg.add(0);
                                k_flg.set(total, 0);
                            }
                        }
                    }
                }
            }
        }

        Memo memo_temp = new Memo();
        memo_temp.set(getMemo_active_excluding(LineSegment.ActiveState.MARK_FOR_DELETION_100));
        reset();
        setMemo(memo_temp);
    }

    public LineSegment.Intersection intersect_divide_fast(int i, int j) {//i is the one to add (2), j is the original one (1) // = 0 does not intersect
        LineSegment si = getLineSegment(i);
        LineSegment sj = getLineSegment(j);

        if (si.getMaxX() < sj.getMinX()) {
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }
        if (sj.getMaxX() < si.getMinX()) {
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }
        if (si.getMaxY() < sj.getMinY()) {
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }
        if (sj.getMaxY() < si.getMinY()) {
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }
        Point intersect_point = new Point();
        StraightLine.Intersection intersect_flg0, intersect_flg1;

        // Here, as the idea of "how two line segments A and B intersect", (1) make the line segment A a straight line, and make the line segment B a line segment as it is (2) a line segment. Think of whether the two endpoints of B are both on one side of the straight line, or separately on both sides of the straight line.
        // After this confirmation is completed, next, make the line segment B a straight line, make the line segment A a line segment as it is, and confirm in the same way. Considering how the two line segments A and B intersect.

        StraightLine straightLine0 = new StraightLine(si.getA(), si.getB());
        intersect_flg0 = straightLine0.lineSegment_intersect_reverse_detail(sj);//senbun_kousa_hantei(Senbun s0){//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
        if (intersect_flg0 == StraightLine.Intersection.NONE_0) {
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }

        StraightLine straightLine1 = new StraightLine(sj.getA(), sj.getB());
        intersect_flg1 = straightLine1.lineSegment_intersect_reverse_detail(si);
        if (intersect_flg1 == StraightLine.Intersection.NONE_0) {
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }

        // --------------------------------------
        //	X intersection
        // --------------------------------------
        if ((intersect_flg0 == StraightLine.Intersection.INTERSECT_X_1) && (intersect_flg1 == StraightLine.Intersection.INTERSECT_X_1)) {//(intersect_flg0==1)&&(intersect_flg1==1) 加える折線と既存の折線はX型で交わる
            intersect_point.set(OritaCalc.findIntersection(straightLine0, straightLine1));

            if (((si.getColor() != LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3))
                    || ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3))) {

                addLine(intersect_point, si.getB(), si);
                si.setB(intersect_point);  //Divide the i-th line segment (end points a and b) at point p. Change the i-th line segment ab to ap and add the line segment pb.

                addLine(intersect_point, sj.getB(), sj);

                sj.setB(intersect_point);  //Divide the i-th line segment (end points a and b) at point p. Change the i-th line segment ab to ap and add the line segment pb.

                return LineSegment.Intersection.INTERSECTS_1;
            }

            if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {//The one to add i is the light blue line (auxiliary live line), and the one from the original j is the polygonal line.
                addLine(intersect_point, si.getB(), si);

                si.setB(intersect_point);  //Divide the i-th line segment (end points a and b) at point p. Change the i-th line segment ab to ap and add the line segment pb.

                return LineSegment.Intersection.INTERSECTS_AUX_2;
            }

            if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {//加えるほうiが折線、元からあるほうjが水色線（補助活線）
                addLine(intersect_point, sj.getB(), sj);

                sj.setB(intersect_point);  //j番目の線分(端点aとb)を点pで分割する。j番目の線分abをapに変え、線分pbを加える。

                return LineSegment.Intersection.INTERSECTS_AUX_3;
            }
        }


        // --------------------------------------
        //	T交差(加える折線のa点で交わる)
        // --------------------------------------
        if ((intersect_flg0 == StraightLine.Intersection.INTERSECT_X_1) && (intersect_flg1 == StraightLine.Intersection.INTERSECT_T_A_21)) {//加える折線と既存の折線はT型(加える折線が縦、既存の折線が横)で交わる(縦のa点で交わる)

            Point pk = new Point();
            pk.set(OritaCalc.findProjection(OritaCalc.lineSegmentToStraightLine(sj), si.getA()));//pkは点pの（線分を含む直線上の）影
            intersect_point.set(pk);//交差点は折線i上のs0の端点の影 20161129
            //foldLines.senbun_bunkatu(i , kousa_ten);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
            //以上で操作終了			kousa_ten.set(oc.kouten_motome(straightLine0,straightLine1));

            if (((si.getColor() != LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3))
                    || ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3))) {
                addLine(intersect_point, sj.getB(), sj);

                sj.setB(intersect_point);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
                return LineSegment.Intersection.INTERSECT_T_A_121;
            }

            if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {//加えるほうiが水色線（補助活線）、元からあるほうjが折線
                return LineSegment.Intersection.NO_INTERSECTION_0;//T交差はしてるが、縦線が補助活線なので何もしないので、0でreturnする。
            }

            if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {//加えるほうiが折線、元からあるほうjが水色線（補助活線）
                addLine(intersect_point, sj.getB(), sj);

                sj.setB(intersect_point);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
                return LineSegment.Intersection.INTERSECT_T_A_121;
            }
        }

        // --------------------------------------
        //	T交差(加える折線のb点で交わる)
        // --------------------------------------
        if ((intersect_flg0 == StraightLine.Intersection.INTERSECT_X_1) && (intersect_flg1 == StraightLine.Intersection.INTERSECT_T_B_22)) {//加える折線と既存の折線はT型(加える折線が縦、既存の折線が横)で交わる(縦のb点で交わる)
            Point pk = new Point();
            pk.set(OritaCalc.findProjection(OritaCalc.lineSegmentToStraightLine(sj), si.getB()));//pkは点pの（線分を含む直線上の）影
            intersect_point.set(pk);//交差点は折線i上のs0の端点の影 20161129
            //foldLines.senbun_bunkatu(i , kousa_ten);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
            //以上で操作終了			kousa_ten.set(oc.kouten_motome(straightLine0,straightLine1));

            if (((si.getColor() != LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3))
                    || ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3))) {
                addLine(intersect_point, sj.getB(), sj);

                sj.setB(intersect_point);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
                return LineSegment.Intersection.INTERSECT_T_B_122;
            }

            if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {//加えるほうiが水色線（補助活線）、元からあるほうjが折線
                return LineSegment.Intersection.NO_INTERSECTION_0;//T交差はしてるが、縦線が補助活線なので何もしないので、0でreturnする。
            }

            if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {//加えるほうiが折線、元からあるほうjが水色線（補助活線）
                addLine(intersect_point, sj.getB(), sj);

                sj.setB(intersect_point);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
                return LineSegment.Intersection.INTERSECT_T_B_122;
            }
        }

        // --------------------------------------
        //	T intersection (intersect at point a of the original polygonal line)
        // --------------------------------------
        if ((intersect_flg0 == StraightLine.Intersection.INTERSECT_T_A_21) && (intersect_flg1 == StraightLine.Intersection.INTERSECT_X_1)) {//The added fold line and the existing fold line intersect at a T shape (the added fold line is horizontal and the existing fold line is vertical) (intersect at the vertical a point).
            Point pk = new Point();
            pk.set(OritaCalc.findProjection(OritaCalc.lineSegmentToStraightLine(si), sj.getA()));//pk is the projection (on a straight line including the line segment) of point p
            intersect_point.set(pk);//交差点は折線i上のs0の端点の影 20161129
            //foldLines.senbun_bunkatu(i , kousa_ten);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
            //This is the end of the operation 			kousa_ten.set(oc.kouten_motome(straightLine0,straightLine1));

            if (((si.getColor() != LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3))
                    || ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3))) {
                addLine(intersect_point, si.getB(), si);

                si.setB(intersect_point);
                return LineSegment.Intersection.INTERSECT_T_A_211;
            }

            if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {//The one to add i is the light blue line (auxiliary live line), and the one from the original j is the polygonal line.
                addLine(intersect_point, si.getB(), si);

                si.setB(intersect_point);
                return LineSegment.Intersection.INTERSECT_T_A_211;
            }

            if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {//The one to add i is the polygonal line, and the one from the original j is the light blue line (auxiliary live line)
                return LineSegment.Intersection.NO_INTERSECTION_0;//There is a T intersection, but since the vertical line is an auxiliary live line, nothing is done, so return with 0.
            }
        }

        // --------------------------------------
        //	T intersection (intersect at point b of the original polygonal line)
        // --------------------------------------
        if ((intersect_flg0 == StraightLine.Intersection.INTERSECT_T_B_22) && (intersect_flg1 == StraightLine.Intersection.INTERSECT_X_1)) {//The added fold line and the existing fold line intersect at a T shape (the added fold line is horizontal and the existing fold line is vertical) (intersect at the vertical a point).
            Point pk = new Point();
            pk.set(OritaCalc.findProjection(OritaCalc.lineSegmentToStraightLine(si), sj.getB()));//pkは点pの（線分を含む直線上の）影
            intersect_point.set(pk);//The intersection is the shadow of the end point of s0 on the polygonal line i 20161129
            //foldLines.senbun_bunkatu(i , kousa_ten);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
            //以上で操作終了			kousa_ten.set(oc.kouten_motome(straightLine0,straightLine1));

            if (((si.getColor() != LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3))
                    || ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3))) {
                addLine(intersect_point, si.getB(), si);

                si.setB(intersect_point);
                return LineSegment.Intersection.INTERSECT_T_B_221;
            }

            if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {//The one to add i is the light blue line (auxiliary live line), and the one from the original j is the polygonal line.
                addLine(intersect_point, si.getB(), si);

                si.setB(intersect_point);
                return LineSegment.Intersection.INTERSECT_T_B_221;
            }

            if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {//加えるほうiが折線、元からあるほうjが水色線（補助活線）
                return LineSegment.Intersection.NO_INTERSECTION_0;//T交差はしてるが、縦線が補助活線なので何もしないので、0でreturnする。
            }
        }


        // --------------------------------------
        //	加える折線と既存の折線は平行
        // --------------------------------------
        if (intersect_flg0 == StraightLine.Intersection.INCLUDED_3) {//加える折線と既存の折線は同一直線上にある
            Point p1 = new Point();
            p1.set(si.getA());
            Point p2 = new Point();
            p2.set(si.getB());
            Point p3 = new Point();
            p3.set(sj.getA());
            Point p4 = new Point();
            p4.set(sj.getB());

            //setiactive(j,100)とされた折線は、kousabunkatu(int i1,int i2,int i3,int i4)の操作が戻った後で削除される。

            LineSegment.Intersection i_intersection_decision = OritaCalc.line_intersect_decide(si, sj, 0.000001, 0.000001);//iは加える方(2)、jは元からある方(1)


            if (i_intersection_decision == LineSegment.Intersection.PARALLEL_EQUAL_31) {//The two line segments are exactly the same
                if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                setActive(j, LineSegment.ActiveState.MARK_FOR_DELETION_100);
                return LineSegment.Intersection.PARALLEL_EQUAL_31;

            } else if (i_intersection_decision == LineSegment.Intersection.PARALLEL_START_OF_S1_CONTAINS_START_OF_S2_321) {//(p1=p3)_p4_p2、siにsjが含まれる。
                if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）
                sj.setColor(si.getColor());
                si.setA(sj.getB());
                return LineSegment.Intersection.PARALLEL_START_OF_S1_CONTAINS_START_OF_S2_321;
            } else if (i_intersection_decision == LineSegment.Intersection.PARALLEL_START_OF_S2_CONTAINS_START_OF_S1_322) {//(p1=p3)_p2_p4、siがsjに含まれる。
                if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                sj.setA(si.getB());
                return LineSegment.Intersection.PARALLEL_START_OF_S2_CONTAINS_START_OF_S1_322;
            } else if (i_intersection_decision == LineSegment.Intersection.PARALLEL_START_OF_S1_CONTAINS_END_OF_S2_331) {//(p1=p4)_p3_p2、siにsjが含まれる。
                if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                sj.setColor(si.getColor());
                si.setA(sj.getA());
                return LineSegment.Intersection.PARALLEL_START_OF_S1_CONTAINS_END_OF_S2_331;
            } else if (i_intersection_decision == LineSegment.Intersection.PARALLEL_END_OF_S2_CONTAINS_START_OF_S1_332) {//(p1=p4)_p2_p3、siがsjに含まれる。
                if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                sj.setB(si.getB());
                return LineSegment.Intersection.PARALLEL_END_OF_S2_CONTAINS_START_OF_S1_332;


            } else if (i_intersection_decision == LineSegment.Intersection.PARALLEL_END_OF_S1_CONTAINS_START_OF_S2_341) {//(p2=p3)_p4_p1、siにsjが含まれる。
                if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                }//The one to add i is the polygonal line, and the one from the original j is the light blue line (auxiliary live line) 

                sj.setColor(si.getColor());
                si.setB(sj.getB());
                return LineSegment.Intersection.PARALLEL_END_OF_S1_CONTAINS_START_OF_S2_341;

            } else if (i_intersection_decision == LineSegment.Intersection.PARALLEL_START_OF_S2_CONTAINS_END_OF_S1_342) {//(p2=p3)_p1_p4、siがsjに含まれる。
                if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                sj.setA(si.getA());
                return LineSegment.Intersection.PARALLEL_START_OF_S2_CONTAINS_END_OF_S1_342;


            } else if (i_intersection_decision == LineSegment.Intersection.PARALLEL_END_OF_S1_CONTAINS_END_OF_S2_351) {//(p2=p4)_p3_p1、siにsjが含まれる。
                if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                sj.setColor(si.getColor());
                si.setB(sj.getA());
                return LineSegment.Intersection.PARALLEL_END_OF_S1_CONTAINS_END_OF_S2_351;


            } else if (i_intersection_decision == LineSegment.Intersection.PARALLEL_END_OF_S2_CONTAINS_END_OF_S1_352) {//(p2=p4)_p1_p3、siがsjに含まれる。
                if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                sj.setB(si.getA());
                return LineSegment.Intersection.PARALLEL_END_OF_S2_CONTAINS_END_OF_S1_352;


            } else if (i_intersection_decision == LineSegment.Intersection.PARALLEL_S1_INCLUDES_S2_361) {//線分(p1,p2)に線分(p3,p4)が含まれる ori_s_temp.senbun_bunkatu(s1.geta()); ori_s_temp.senbun_bunkatu(s1.getb());   foldLines.setiactive(i,100);//imax=imax-1;
                if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                sj.setColor(si.getColor());
                addLine(sj.getB(), si.getB(), si);

                si.setB(sj.getA());
                return LineSegment.Intersection.PARALLEL_S1_INCLUDES_S2_361;
            } else if (i_intersection_decision == LineSegment.Intersection.PARALLEL_S1_INCLUDES_S2_362) {//線分(p1,p2)に線分(p4,p3)が含まれる; ori_s_temp.senbun_bunkatu(s1.getb());   foldLines.setiactive(i,100);//imax=imax-1;
                if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                sj.setColor(si.getColor());
                addLine(sj.getA(), si.getB(), si);

                si.setB(sj.getB());
                return LineSegment.Intersection.PARALLEL_S1_INCLUDES_S2_362;
            } else if (i_intersection_decision == LineSegment.Intersection.PARALLEL_S2_INCLUDES_S1_363) {//線分(p3,p4)に線分(p1,p2)が含まれる foldLines.addsenbun(s0.getb(),s1.getb(),s1.getcolor());foldLines.setb(i,s0.geta());
                if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                addLine(si.getB(), sj.getB(), sj);

                sj.setB(si.getA());
                return LineSegment.Intersection.PARALLEL_S2_INCLUDES_S1_363;
            } else if (i_intersection_decision == LineSegment.Intersection.PARALLEL_S2_INCLUDES_S1_364) {//線分(p3,p4)に線分(p2,p1)が含まれるori_s.addsenbun(s0.geta(),s1.getb(),s1.getcolor());foldLines.setb(i,s0.getb());
                if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                //addsenbun(si.geta(),sj.getb(),sj.getcolor());
                addLine(si.getA(), sj.getB(), sj);

                sj.setB(si.getB());
                return LineSegment.Intersection.PARALLEL_S2_INCLUDES_S1_364;


            } else if (i_intersection_decision == LineSegment.Intersection.PARALLEL_S1_END_OVERLAPS_S2_START_371) {//線分(p1,p2)のP2側と線分(p3,p4)のP3側が部分的に重なる//ori_s_temp.senbun_bunkatu(s1.geta());foldLines.seta(i,s0.getb());
                if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                //addsenbun(p3,p2,si.getcolor());
                addLine(p3, p2, si);

                si.setB(p3);
                sj.setA(p2);
                return LineSegment.Intersection.PARALLEL_S1_END_OVERLAPS_S2_START_371;

            } else if (i_intersection_decision == LineSegment.Intersection.PARALLEL_S1_END_OVERLAPS_S2_END_372) {//線分(p1,p2)のP2側と線分(p4,p3)のP4側が部分的に重なる//ori_s_temp.senbun_bunkatu(s1.getb());foldLines.setb(i,s0.getb());
                if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                addLine(p4, p2, si);

                si.setB(p4);
                sj.setB(p2);
                return LineSegment.Intersection.PARALLEL_S1_END_OVERLAPS_S2_END_372;

            } else if (i_intersection_decision == LineSegment.Intersection.PARALLEL_S1_START_OVERLAPS_S2_END_373) {//線分(p3,p4)のP4側と線分(p1,p2)のP1側が部分的に重なる//ori_s_temp.senbun_bunkatu(s1.getb());foldLines.setb(i,s0.geta());
                if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                addLine(p1, p4, si);

                si.setA(p4);
                sj.setB(p1);
                return LineSegment.Intersection.PARALLEL_S1_START_OVERLAPS_S2_END_373;

            } else if (i_intersection_decision == LineSegment.Intersection.PARALLEL_S1_START_OVERLAPS_S2_START_374) {//線分(p4,p3)のP3側と線分(p1,p2)のP1側が部分的に重なる//ori_s_temp.senbun_bunkatu(s1.geta());foldLines.seta(i,s0.geta());
                if ((si.getColor() == LineColor.CYAN_3) && (sj.getColor() != LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != LineColor.CYAN_3) && (sj.getColor() == LineColor.CYAN_3)) {
                    return LineSegment.Intersection.NO_INTERSECTION_0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                addLine(p1, p3, si);

                si.setA(p3);
                sj.setA(p1);
                return LineSegment.Intersection.PARALLEL_S1_START_OVERLAPS_S2_START_374;
            }
        }
        return LineSegment.Intersection.NO_INTERSECTION_0;
    }

    //---------------------
    //交差している２つの線分の交点で２つの線分を分割する。　まったく重なる線分が２つあった場合は、なんの処理もなされないまま２つとも残る。
    public void intersect_divide() {
        int ibunkatu = 1;//分割があれば1、なければ0
        ArrayList<Integer> k_flg = new ArrayList<>();//交差分割の影響があることを示すフラッグ。

        for (int i = 0; i <= total + 1; i++) {
            k_flg.add(1);
        }

        while (ibunkatu != 0) {
            ibunkatu = 0;
            for (int i = 1; i <= total; i++) {
                Integer I_k_flag = k_flg.get(i);
                if (I_k_flag == 1) {
                    k_flg.set(i, 0);
                    for (int j = 1; j <= total; j++) {
                        if (i != j) {
                            Integer J_k_flag = k_flg.get(j);
                            if (J_k_flag == 1) {
                                int old_sousuu = total;
                                boolean itemp = intersect_divide(i, j);
                                if (old_sousuu < total) {
                                    for (int is = old_sousuu + 1; is <= total; is++) {
                                        k_flg.add(1);
                                    }
                                }
                                if (itemp) {
                                    ibunkatu = ibunkatu + 1;
                                    k_flg.set(i, 1);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    //円の追加-------------------------------

    //交差している２つの線分の交点で２つの線分を分割する。分割を行ったら1。行わなかったら0を返す。オリヒメ2.002から分割後の線の色も制御するようにした(重複部がある場合は一本化し、番号の遅いほうの色になる)。
    public boolean intersect_divide(int i, int j) {
        if (i == j) {
            return false;
        }

        LineSegment si;
        si = getLineSegment(i);
        LineSegment sj;
        sj = getLineSegment(j);

        if (si.getMaxX() < sj.getMinX()) {
            return false;
        }//これはSenbunにi_max_xがちゃんと定義されているときでないとうまくいかない
        if (sj.getMaxX() < si.getMinX()) {
            return false;
        }//これはSenbunにi_min_xがちゃんと定義されているときでないとうまくいかない
        if (si.getMaxY() < sj.getMinY()) {
            return false;
        }//これはSenbunにi_max_yがちゃんと定義されているときでないとうまくいかない
        if (sj.getMaxY() < si.getMinY()) {
            return false;
        }//これはSenbunにi_min_yがちゃんと定義されているときでないとうまくいかない

        Point p1 = new Point();
        p1.set(si.getA());
        Point p2 = new Point();
        p2.set(si.getB());
        Point p3 = new Point();
        p3.set(sj.getA());
        Point p4 = new Point();
        p4.set(sj.getB());
        Point pk = new Point();

        double ixmax;
        double ixmin;
        double iymax;
        double iymin;

        ixmax = si.getAX();
        ixmin = si.getAX();
        iymax = si.getAY();
        iymin = si.getAY();

        if (ixmax < si.getBX()) {
            ixmax = si.getBX();
        }
        if (ixmin > si.getBX()) {
            ixmin = si.getBX();
        }
        if (iymax < si.getBY()) {
            iymax = si.getBY();
        }
        if (iymin > si.getBY()) {
            iymin = si.getBY();
        }

        double jxmax;
        double jxmin;
        double jymax;
        double jymin;

        jxmax = sj.getAX();
        jxmin = sj.getAX();
        jymax = sj.getAY();
        jymin = sj.getAY();

        if (jxmax < sj.getBX()) {
            jxmax = sj.getBX();
        }
        if (jxmin > sj.getBX()) {
            jxmin = sj.getBX();
        }
        if (jymax < sj.getBY()) {
            jymax = sj.getBY();
        }
        if (jymin > sj.getBY()) {
            jymin = sj.getBY();
        }

        if (ixmax + 0.5 < jxmin) {
            return false;
        }
        if (jxmax + 0.5 < ixmin) {
            return false;
        }
        if (iymax + 0.5 < jymin) {
            return false;
        }
        if (jymax + 0.5 < iymin) {
            return false;
        }

        if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.INTERSECTS_1) {
            pk.set(OritaCalc.findIntersection(si, sj));
            si.setA(p1);
            si.setB(pk);
            sj.setA(p3);
            sj.setB(pk);
            addLine(p2, pk, si.getColor());
            addLine(p4, pk, sj.getColor());
            return true;
        }

        if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.INTERSECTS_TSHAPE_S1_VERTICAL_BAR_25) {
            pk.set(OritaCalc.findIntersection(si, sj));
            sj.setA(p3);
            sj.setB(pk);
            addLine(p4, pk, sj.getColor());
            return true;
        }

        if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.INTERSECTS_TSHAPE_S1_VERTICAL_BAR_26) {
            pk.set(OritaCalc.findIntersection(si, sj));
            sj.setA(p3);
            sj.setB(pk);
            addLine(p4, pk, sj.getColor());
            return true;
        }

        if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.INTERSECTS_TSHAPE_S2_VERTICAL_BAR_27) {
            pk.set(OritaCalc.findIntersection(si, sj));
            si.setA(p1);
            si.setB(pk);
            addLine(p2, pk, si.getColor());
            return true;
        }

        if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.INTERSECTS_TSHAPE_S2_VERTICAL_BAR_28) {
            pk.set(OritaCalc.findIntersection(si, sj));
            si.setA(p1);
            si.setB(pk);
            addLine(p2, pk, si.getColor());
            return true;
        }

        if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.NO_INTERSECTION_0) {//このifないと本来この後で処理されるべき条件がここで処理されてしまうことある
            if (OritaCalc.distance_lineSegment(si.getA(), sj) < 0.01) {
                if (OritaCalc.lineSegment_endpoint_search(si.getA(), sj, 0.01) == 3) { //20161107 わずかに届かない場合
                    pk.set(OritaCalc.findIntersection(si, sj));
                    sj.setA(p3);
                    sj.setB(pk);
                    addLine(p4, pk, sj.getColor());
                    return true;
                }
            }

            if (OritaCalc.distance_lineSegment(si.getB(), sj) < 0.01) {
                if (OritaCalc.lineSegment_endpoint_search(si.getB(), sj, 0.01) == 3) { //20161107 わずかに届かない場合
                    pk.set(OritaCalc.findIntersection(si, sj));
                    sj.setA(p3);
                    sj.setB(pk);
                    addLine(p4, pk, sj.getColor());
                    return true;
                }
            }

            if (OritaCalc.distance_lineSegment(sj.getA(), si) < 0.01) {
                if (OritaCalc.lineSegment_endpoint_search(sj.getA(), si, 0.01) == 3) { //20161107 わずかに届かない場合
                    pk.set(OritaCalc.findIntersection(si, sj));
                    si.setA(p1);
                    si.setB(pk);
                    addLine(p2, pk, si.getColor());
                    return true;
                }
            }

            if (OritaCalc.distance_lineSegment(sj.getB(), si) < 0.01) {
                if (OritaCalc.lineSegment_endpoint_search(sj.getB(), si, 0.01) == 3) { //20161107 わずかに届かない場合
                    pk.set(OritaCalc.findIntersection(si, sj));    //<<<<<<<<<<<<<<<<<<<<<<<
                    si.setA(p1);
                    si.setB(pk);
                    addLine(p2, pk, si.getColor());
                    return true;
                }
            }

        }

        if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.PARALLEL_EQUAL_31) {//2つの線分がまったく同じ場合は、何もしない。
            return false;
        }

        if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.PARALLEL_START_OF_S1_CONTAINS_START_OF_S2_321) {//2つの線分の端点どうし(p1とp3)が1点で重なる。siにsjが含まれる
            si.setA(p2);
            si.setB(p4);

            LineColor overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            sj.setColor(overlapping_col);

            return true;
        }

        if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.PARALLEL_START_OF_S2_CONTAINS_START_OF_S1_322) {//2つの線分の端点どうし(p1とp3)が1点で重なる。sjにsiが含まれる
            sj.setA(p2);
            sj.setB(p4);
            LineColor overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            si.setColor(overlapping_col);

            return true;
        }

        if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.PARALLEL_START_OF_S1_CONTAINS_END_OF_S2_331) {//2つの線分の端点どうし(p1とp4)が1点で重なる。siにsjが含まれる
            si.setA(p2);
            si.setB(p3);

            LineColor overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            sj.setColor(overlapping_col);

            return true;
        }

        if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.PARALLEL_END_OF_S2_CONTAINS_START_OF_S1_332) {//2つの線分の端点どうし(p1とp4)が1点で重なる。sjにsiが含まれる
            sj.setA(p2);
            sj.setB(p3);
            LineColor overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            si.setColor(overlapping_col);
            return true;
        }

        if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.PARALLEL_END_OF_S1_CONTAINS_START_OF_S2_341) {//2つの線分の端点どうし(p2とp3)が1点で重なる。siにsjが含まれる
            si.setA(p1);
            si.setB(p4);
            LineColor overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            sj.setColor(overlapping_col);

            return true;
        }

        if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.PARALLEL_START_OF_S2_CONTAINS_END_OF_S1_342) {//2つの線分の端点どうし(p2とp3)が1点で重なる。sjにsiが含まれる
            sj.setA(p1);
            sj.setB(p4);
            LineColor overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            si.setColor(overlapping_col);

            return true;
        }

        if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.PARALLEL_END_OF_S1_CONTAINS_END_OF_S2_351) {//2つの線分の端点どうし(p2とp4)が1点で重なる。siにsjが含まれる
            si.setA(p1);
            si.setB(p3);

            LineColor overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            sj.setColor(overlapping_col);

            return true;
        }

        if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.PARALLEL_END_OF_S2_CONTAINS_END_OF_S1_352) {//2つの線分の端点どうし(p2とp4)が1点で重なる。sjにsiが含まれる
            sj.setA(p1);
            sj.setB(p3);
            LineColor overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            si.setColor(overlapping_col);

            return true;
        }


        if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.PARALLEL_S1_INCLUDES_S2_361) {//p1-p3-p4-p2の順
            si.setA(p1);
            si.setB(p3);

            addLine(p2, p4, si.getColor());
            LineColor overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            sj.setColor(overlapping_col);

            return true;
        }

        if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.PARALLEL_S1_INCLUDES_S2_362) {//p1-p4-p3-p2の順
            si.setA(p1);
            si.setB(p4);

            addLine(p2, p3, si.getColor());

            LineColor overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            sj.setColor(overlapping_col);

            return true;
        }

        if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.PARALLEL_S2_INCLUDES_S1_363) {//p3-p1-p2-p4の順
            sj.setA(p1);
            sj.setB(p3);

            addLine(p2, p4, sj.getColor());

            LineColor overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            si.setColor(overlapping_col);

            return true;
        }

        if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.PARALLEL_S2_INCLUDES_S1_364) {//p3-p2-p1-p4の順
            sj.setA(p1);
            sj.setB(p4);

            addLine(p2, p3, sj.getColor());

            LineColor overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            si.setColor(overlapping_col);

            return true;
        }

        if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.PARALLEL_S1_END_OVERLAPS_S2_START_371) {//p1-p3-p2-p4の順
            si.setA(p1);
            si.setB(p3);

            sj.setA(p2);
            sj.setB(p4);

            LineColor overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            addLine(p2, p3, overlapping_col);
            return true;
        }

        if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.PARALLEL_S1_END_OVERLAPS_S2_END_372) {//p1-p4-p2-p3の順
            si.setA(p1);
            si.setB(p4);

            sj.setA(p3);
            sj.setB(p2);

            LineColor overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            addLine(p2, p4, overlapping_col);
            return true;
        }

        if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.PARALLEL_S1_START_OVERLAPS_S2_END_373) {//p3-p1-p4-p2の順
            sj.setA(p1);
            sj.setB(p3);
            si.setA(p2);
            si.setB(p4);
            LineColor overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            addLine(p1, p4, overlapping_col);
            return true;
        }

        if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.PARALLEL_S1_START_OVERLAPS_S2_START_374) {//p4-p1-p3-p2の順
            sj.setA(p1);
            sj.setB(p4);
            si.setA(p3);
            si.setB(p2);
            LineColor overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            addLine(p1, p3, overlapping_col);
            return true;
        }

        return false;
    }

    public void addCircle(double dx, double dy, double dr, LineColor ic) {
        circles.add(new Circle(dx, dy, dr, ic));
    }

    public void addCircle(Point t, double dr) {
        addCircle(t.getX(), t.getY(), dr, LineColor.BLACK_0);
    }

    //Generates a circle with a radius of 0 at the intersection of circles -------------------------------
    public void circle_circle_intersection(int imin, int imax, int jmin, int jmax) {
        for (int i = imin; i <= imax; i++) {
            Circle ei = new Circle();
            ei.set(getCircle(i));
            if (ei.getRadius() > 0.0000001) {//Circles with a radius of 0 are not applicable
                for (int j = jmin; j <= jmax; j++) {

                    Circle ej = new Circle();
                    ej.set(getCircle(j));
                    if (ej.getRadius() > 0.0000001) {//Circles with a radius of 0 are not applicable
                        if (OritaCalc.distance(ei.getCenter(), ej.getCenter()) < 0.000001) {//Two circles are concentric and do not intersect
                        } else if (Math.abs(OritaCalc.distance(ei.getCenter(), ej.getCenter()) - ei.getRadius() - ej.getRadius()) < 0.0001) {//Two circles intersect at one point
                            addCircle(OritaCalc.internalDivisionRatio(ei.getCenter(), ej.getCenter(), ei.getRadius(), ej.getRadius()), 0.0);
                        } else if (OritaCalc.distance(ei.getCenter(), ej.getCenter()) > ei.getRadius() + ej.getRadius()) {//Two circles do not intersect

                        } else if (Math.abs(OritaCalc.distance(ei.getCenter(), ej.getCenter()) - Math.abs(ei.getRadius() - ej.getRadius())) < 0.0001) {//Two circles intersect at one point
                            addCircle(OritaCalc.internalDivisionRatio(ei.getCenter(), ej.getCenter(), -ei.getRadius(), ej.getRadius()), 0.0);
                        } else if (OritaCalc.distance(ei.getCenter(), ej.getCenter()) < Math.abs(ei.getRadius() - ej.getRadius())) {//Two circles do not intersect

                        } else {//Two circles intersect at two points
                            LineSegment lineSegment = new LineSegment();
                            lineSegment.set(OritaCalc.circle_to_circle_no_intersection_wo_musubu_lineSegment(ei, ej));

                            addCircle(lineSegment.getA(), 0.0);
                            addCircle(lineSegment.getB(), 0.0);
                        }
                    }
                }
            }
        }
    }

    //A circle with a radius of 0 is generated at the intersection of the circle and the polygonal line.
    public void lineSegment_circle_intersection(int imin, int imax, int jmin, int jmax) {
        for (int i = imin; i <= imax; i++) {
            LineSegment si = getLineSegment(i);

            StraightLine ti = new StraightLine();
            ti.set(OritaCalc.lineSegmentToStraightLine(si));
            for (int j = jmin; j <= jmax; j++) {

                Circle ej = new Circle();
                ej.set(getCircle(j));
                if (ej.getRadius() > 0.0000001) {//Circles with a radius of 0 are not applicable
                    double tc_kyori = ti.calculateDistance(ej.getCenter()); //Distance between the center of a straight line and a circle


                    if (Math.abs(tc_kyori - ej.getRadius()) < 0.000001) {//Circle and straight line intersect at one point
                        if (
                                Math.abs(
                                        OritaCalc.distance_lineSegment(ej.getCenter(), si) - ej.getRadius()
                                ) < 0.000001
                        ) {
                            addCircle(OritaCalc.findProjection(ti, ej.getCenter()), 0.0);
                        }
                    } else if (tc_kyori > ej.getRadius()) {//Circles and straight lines do not intersect
                    } else {//Circle and straight line intersect at two points
                        LineSegment k_senb = new LineSegment();
                        k_senb.set(OritaCalc.circle_to_straightLine_no_intersect_wo_connect_LineSegment(ej, ti));

                        if (OritaCalc.distance_lineSegment(k_senb.getA(), si) < 0.00001) {
                            addCircle(k_senb.getA(), 0.0);
                        }
                        if (OritaCalc.distance_lineSegment(k_senb.getB(), si) < 0.00001) {
                            addCircle(k_senb.getB(), 0.0);
                        }
                    }
                }
            }
        }
    }

    //Delete circle-----------------------------------------
    public void deleteCircle(int j) {   //Delete the jth circle
        circles.remove(j);
    }

    //Arrangement of circles -----------------------------------------
    public boolean circle_organize(int i0) {//Organize the jth circle. Returns 1 if deleted by pruning, 0 if not deleted.
        int ies3 = circle_state(i0, 3);
        int ies4 = circle_state(i0, 4);
        int ies5 = circle_state(i0, 5);

        if (ies3 == 100000) {
            return false;
        }
        if (ies3 == 2) {
            return false;
        }
        if ((ies3 == 1) && (ies4 >= 1)) {
            return false;
        }
        if ((ies3 == 1) && (ies5 >= 1)) {
            return false;
        }

        circles.remove(i0);
        return true;

    }

    //円の整理-----------------------------------------
    public void circle_organize() {//全ての円を対象に整理をする。
        for (int i = numCircles(); i >= 1; i--) {
            circle_organize(i);
        }
    }

    //Circle status display -----------------------------------------
    public int circle_state(int i0, int i_mode) {   //Indicates the state of the i-th circle.
        // = 100000 The radius of the i-th circle is not 0
        // =      0 The radius of the i-th circle is 0. It is far from the circumference of other circles. It is far from the center of other circles. Twice
        // =      1 1st digit number. The number that the i-th circle has a radius of 0 and overlaps the center of another circle with a radius of 0. When it overlaps with two or more, it is displayed as 2.
        // =     10 Second digit number. The number of i-th circles with a radius of 0 and other non-zero radii that overlap the center of the circle. When it overlaps with two or more, it is displayed as 2.
        // =   100 3rd digit number. The number of circles with an i-th radius of 0 that overlap the circumference of other non-zero radii. When it overlaps with two or more, it is displayed as 2.
        // =  1000 4th digit number. The number of i-th circles with a radius of 0 that overlaps with other polygonal lines. When it overlaps with two or more, it is displayed as 2.
        // = 10000 5th digit number. The number of i-th circles with a radius of 0 that overlaps with other auxiliary hot lines. When it overlaps with two or more, it is displayed as 2.
        Circle e_temp = new Circle();
        e_temp.set(getCircle(i0));
        double er_0 = e_temp.getRadius();
        Point ec_0 = new Point();
        ec_0.set(e_temp.getCenter());

        double er_1;
        Point ec_1 = new Point();

        int ir1 = 0;
        int ir2 = 0;
        int ir3 = 0;
        int ir4 = 0;
        int ir5 = 0;

        if (er_0 < 0.0000001) {
            for (int i = 1; i <= numCircles(); i++) {
                if (i != i0) {
                    e_temp.set(getCircle(i));
                    er_1 = e_temp.getRadius();
                    ec_1.set(e_temp.getCenter());
                    if (er_1 < 0.0000001) {//The radius of the other circle is 0
                        if (ec_0.distance(ec_1) < 0.0000001) {
                            ir1 = ir1 + 1;
                        }
                    } else {//The radius of the other circle is not 0
                        if (ec_0.distance(ec_1) < 0.0000001) {
                            ir2 = ir2 + 1;
                        } else if (Math.abs(ec_0.distance(ec_1) - er_1) < 0.0000001) {
                            ir3 = ir3 + 1;
                        }
                    }
                }
            }

            for (int i = 1; i <= total; i++) {
                LineSegment si;
                si = getLineSegment(i);
                if (OritaCalc.distance_lineSegment(ec_0, si) < 0.000001) {

                    if (si.getColor().getNumber() <= 2) {
                        ir4 = ir4 + 1;
                    } else if (si.getColor() == LineColor.CYAN_3) {
                        ir5 = ir5 + 1;
                    }
                }
            }

            if (ir1 > 2) {
                ir1 = 2;
            }
            if (ir2 > 2) {
                ir2 = 2;
            }
            if (ir3 > 2) {
                ir3 = 2;
            }
            if (ir4 > 2) {
                ir4 = 2;
            }
            if (ir5 > 2) {
                ir5 = 2;
            }

            if (i_mode == 0) {
                return ir1 + ir2 * 10 + ir3 * 100 + ir4 * 1000 + ir5 * 10000;
            }
            if (i_mode == 1) {
                return ir1;
            }
            if (i_mode == 2) {
                return ir2;
            }
            if (i_mode == 3) {
                return ir3;
            }
            if (i_mode == 4) {
                return ir4;
            }
            if (i_mode == 5) {
                return ir5;
            }
        }

        return 100000;
    }

    //線分の追加-------------------------------
    public void addLine(Point pi, Point pj, LineColor i_c) {
        total++;

        LineSegment s;
        s = getLineSegment(total);
        s.set(pi, pj, i_c);
    }

    //線分の追加-------------------------------wwwwwwwwww
    public void addLine(Point pi, Point pj, LineSegment s0) {//Ten piからTen pjまでの線分を追加。この追加する線分のその他のパラメータはs0と同じ
        total++;

        LineSegment s;
        s = getLineSegment(total);
        s.set(s0);
        s.set(pi, pj);
    }

    //線分の追加-------------------------------
    public void addLine(Point pi, Point pj, LineColor i_c, LineSegment.ActiveState i_a, int v_a, int v_b) {
        total++;

        LineSegment s;
        s = getLineSegment(total);
        s.set(pi, pj, i_c, i_a, v_a, v_b);
    }

    //Add line segment -------------------------------
    public void addLine(double ax, double ay, double bx, double by, LineColor ic) {
        total++;

        LineSegment s;
        s = getLineSegment(total);
        s.set(ax, ay, bx, by, ic);
    }

    //線分の追加-------------------------------
    public void addLine(Point pi, Point pj) {
        total++;

        LineSegment s;
        s = getLineSegment(total);

        s.setA(pi);
        s.setB(pj);
    }

    //線分の追加-------------------------------
    public void addLine(LineSegment s0) {
        addLine(s0.getA(), s0.getB(), s0.getColor(), s0.getActive(), s0.getVonoroiA(), s0.getVonoroiB());//20181110追加
    }

    //線分の削除-----------------------------------------
    public void deleteLine(int j) {   //j番目の線分を削除する  このsi= sen(i)は大丈夫なのだろうか????????si= sen(i)　20161106
        for (int i = j; i <= total - 1; i++) {
            LineSegment si = getLineSegment(i);
            LineSegment si1 = getLineSegment(i + 1);
            si.set(si1);
        }
        total--;
    }

    //線分の分割-----------------qqqqq------------------------
    public void lineSegment_bunkatu(int i, Point p) {   //Divide the i-th line segment (end points a and b) at point p. Change the i-th line segment ab to ap and add the line segment pb.

        LineSegment s1 = new LineSegment(p, getB(i));//Create the i-th line segment ab before changing it to ap
        LineColor i_c = getColor(i);

        setB(i, p);//Change the i-th line segment ab to ap

        addLine(s1);
        setColor(getTotal(), i_c);
    }

    //Remove the branching line segments without forming a closed polygon.
    public void branch_trim(double r) {
        boolean iflga;
        boolean iflgb;
        for (int i = 1; i <= total; i++) {
            iflga = false;
            iflgb = false;
            LineSegment si;
            si = getLineSegment(i);
            for (int j = 1; j <= total; j++) {
                if (i != j) {
                    LineSegment sj;
                    sj = getLineSegment(j);
                    if (OritaCalc.distance(si.getA(), sj.getA()) < r) {
                        iflga = true;
                    }
                    if (OritaCalc.distance(si.getA(), sj.getB()) < r) {
                        iflga = true;
                    }
                    if (OritaCalc.distance(si.getB(), sj.getA()) < r) {
                        iflgb = true;
                    }
                    if (OritaCalc.distance(si.getB(), sj.getB()) < r) {
                        iflgb = true;
                    }
                }
            }

            if (!iflga || !iflgb) {
                deleteLineSegment_vertex(i);
                i = 1;
            }
        }
    }

    public void deleteLineSegment_vertex(int i) {//When erasing the i-th fold line, if the end point of the fold line can also be erased, erase it.
        Point pa = new Point();
        pa.set(getA(i));
        Point pb = new Point();
        pb.set(getB(i));
        deleteLine(i);

        del_V(pa, 0.000001, 0.000001);
        del_V(pb, 0.000001, 0.000001);
    }

    //Find and return the number of the circle closest to the point p in reverse order (the higher the number means priority)
    public int closest_circle_search_reverse_order(Point p) {
        int minrid = 0;
        double minr = 100000;
        double rtemp;
        for (int i = 1; i <= numCircles(); i++) {
            Circle e_temp = new Circle();
            e_temp.set(getCircle(i));

            rtemp = p.distance(e_temp.getCenter());
            if (minr >= rtemp) {
                minr = rtemp;
                minrid = i;
            }

            rtemp = Math.abs(p.distance(e_temp.getCenter()) - e_temp.getRadius());
            if (minr >= rtemp) {
                minr = rtemp;
                minrid = i;
            }
        }

        return minrid;
    }

    // Returns the distance at the number of the circle closest to the point p
    public double closestCircleDistance(Point p) {
        double minr = 100000;
        double rtemp;
        for (int i = 1; i <= numCircles(); i++) {
            Circle e_temp = new Circle();
            e_temp.set(getCircle(i));


            rtemp = p.distance(e_temp.getCenter());
            if (minr > rtemp) {
                minr = rtemp;
            }

            rtemp = Math.abs(p.distance(e_temp.getCenter()) - e_temp.getRadius());
            if (minr > rtemp) {
                minr = rtemp;
            }
        }

        return minr;
    }

    //Returns the number of the line segment closest to the point p
    public int closestLineSegmentSearch(Point p) {
        int minrid = 0;
        double minr = 100000;
        for (int i = 1; i <= total; i++) {
            double sk = OritaCalc.distance_lineSegment(p, get(i));
            if (minr > sk) {
                minr = sk;
                minrid = i;
            }//Whether it is close to the handle

        }
        return minrid;
    }

    //Find and return the number of the line segment closest to the point p from the opposite (meaning from the larger number to the smaller number)
    public int closestLineSegmentSearchReversedOrder(Point p) {
        int minrid = 0;
        double minr = 100000;
        for (int i = total; i >= 1; i--) {
            double sk = OritaCalc.distance_lineSegment(p, get(i));
            if (minr > sk) {
                minr = sk;
                minrid = i;
            }//Whether it is close to the handle

        }
        return minrid;
    }

    //Returns the distance at the number of the line segment closest to the point p
    public double closestLineSegmentDistance(Point p) {
        double minr = 100000.0;
        for (int i = 1; i <= total; i++) {
            double sk = OritaCalc.distance_lineSegment(p, get(i));
            if (minr > sk) {
                minr = sk;
            }//Whether it is close to the handle

        }
        return minr;
    }

    //Returns the distance at the number of the line segment closest to the point p. However, the polygonal line parallel to the line segment s0 is not included in the survey. That is, even if parallel polygonal lines overlap, they are not considered to be close to each other.
    public double closestLineSegmentDistanceExcludingParallel(Point p, LineSegment s0) {
        double minr = 100000.0;
        for (int i = 1; i <= total; i++) {
            if (OritaCalc.parallel_judgement(get(i), s0, 0.0001) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {

                double sk = OritaCalc.distance_lineSegment(p, get(i));
                if (minr > sk) {
                    minr = sk;
                }
            }
        }
        return minr;
    }

    public Circle closestCircleMidpoint(Point p) {
        int minrid = 0;
        double minr = 100000.0;
        Circle e1 = new Circle(100000.0, 100000.0, 1.0, LineColor.BLACK_0);
        for (int i = 1; i <= numCircles(); i++) {
            double ek = OritaCalc.distance_circumference(p, getCircle(i));
            if (minr > ek) {
                minr = ek;
                minrid = i;
            }//Whether it is close to the circumference
        }

        if (minrid == 0) {
            return e1;
        }

        return getCircle(minrid);
    }

    public LineSegment closestLineSegment(Point p) {
        int minrid = 0;
        double minr = 100000.0;
        LineSegment s1 = new LineSegment(100000.0, 100000.0, 100000.0, 100000.1);
        for (int i = 1; i <= total; i++) {
            double sk = OritaCalc.distance_lineSegment(p, get(i));
            if (minr > sk) {
                minr = sk;
                minrid = i;
            }//Whether it is close to the handle
        }

        if (minrid == 0) {
            return s1;
        }

        return get(minrid);
    }

    //Returns the "end point of the line segment" closest to the point p
    public Point closestPoint(Point p) {
        Point p_return = new Point();
        p_return.set(100000.0, 100000.0);
        Point p_temp = new Point();
        for (int i = 1; i <= total; i++) {
            p_temp.set(getA(i));
            if (p.distanceSquared(p_temp) < p.distanceSquared(p_return)) {
                p_return.set(p_temp.getX(), p_temp.getY());
            }
            p_temp.set(getB(i));
            if (p.distanceSquared(p_temp) < p.distanceSquared(p_return)) {
                p_return.set(p_temp.getX(), p_temp.getY());
            }

        }
        return p_return;
    }

    //Returns the "center point of the circle" closest to the point p
    public Point closestCenter(Point p) {
        Point p_return = new Point();
        p_return.set(100000.0, 100000.0);
        Point p_temp = new Point();
        for (int i = 1; i <= numCircles(); i++) {
            Circle e_temp = new Circle();
            e_temp.set(getCircle(i));
            p_temp.set(e_temp.getCenter());
            if (p.distanceSquared(p_temp) < p.distanceSquared(p_return)) {
                p_return.set(p_temp.getX(), p_temp.getY());
            }
        }
        return p_return;
    }

    //Returns the "end point of the line segment" closest to the point p. However, auxiliary live lines are not applicable
    public Point closestPointOfFoldLine(Point p) {
        Point p_return = new Point();
        p_return.set(100000.0, 100000.0);
        Point p_temp = new Point();
        for (int i = 1; i <= total; i++) {
            if (getColor(i).isFoldingLine()) {
                p_temp.set(getA(i));
                if (p.distanceSquared(p_temp) < p.distanceSquared(p_return)) {
                    p_return.set(p_temp.getX(), p_temp.getY());
                }
                p_temp.set(getB(i));
                if (p.distanceSquared(p_temp) < p.distanceSquared(p_return)) {
                    p_return.set(p_temp.getX(), p_temp.getY());
                }

            }
        }
        return p_return;
    }

    public void del_V(int i, int j) {//Erasing when two fold lines are the same color and there are no end points for other fold lines
        LineSegment.Intersection i_lineSegment_intersection_decision = OritaCalc.line_intersect_decide(get(i), get(j), 0.00001, 0.00001);

        LineSegment addLine = new LineSegment();
        int i_ten = 0;
        if (i_lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_START_OF_S1_INTERSECTS_START_OF_S2_323) {
            addLine.set(getB(i), getB(j));
            i_ten = vertex_surrounding_lineCount(getA(i), 0.00001);
        }
        if (i_lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_START_OF_S1_INTERSECTS_END_OF_S2_333) {
            addLine.set(getB(i), getA(j));
            i_ten = vertex_surrounding_lineCount(getA(i), 0.00001);
        }
        if (i_lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_END_OF_S1_INTERSECTS_START_OF_S2_343) {
            addLine.set(getA(i), getB(j));
            i_ten = vertex_surrounding_lineCount(getB(i), 0.00001);
        }
        if (i_lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_END_OF_S1_INTERSECTS_END_OF_S2_353) {
            addLine.set(getA(i), getA(j));
            i_ten = vertex_surrounding_lineCount(getB(i), 0.00001);
        }

        if (i_ten == 2) {
            LineColor i_c = LineColor.BLACK_0;
            if ((getColor(i) == LineColor.BLACK_0) && (getColor(j) == LineColor.BLACK_0)) {
                i_c = LineColor.BLACK_0;
            }
            if ((getColor(i) == LineColor.BLACK_0) && (getColor(j) == LineColor.RED_1)) {
                i_c = LineColor.RED_1;
            }
            if ((getColor(i) == LineColor.BLACK_0) && (getColor(j) == LineColor.BLUE_2)) {
                i_c = LineColor.BLUE_2;
            }
            if ((getColor(i) == LineColor.BLACK_0) && (getColor(j) == LineColor.CYAN_3)) {
                return;
            }

            if ((getColor(i) == LineColor.RED_1) && (getColor(j) == LineColor.BLACK_0)) {
                i_c = LineColor.RED_1;
            }
            if ((getColor(i) == LineColor.RED_1) && (getColor(j) == LineColor.RED_1)) {
                i_c = LineColor.RED_1;
            }
            if ((getColor(i) == LineColor.RED_1) && (getColor(j) == LineColor.BLUE_2)) {
                i_c = LineColor.BLACK_0;
            }
            if ((getColor(i) == LineColor.RED_1) && (getColor(j) == LineColor.CYAN_3)) {
                return;
            }

            if ((getColor(i) == LineColor.BLUE_2) && (getColor(j) == LineColor.BLACK_0)) {
                i_c = LineColor.BLUE_2;
            }
            if ((getColor(i) == LineColor.BLUE_2) && (getColor(j) == LineColor.RED_1)) {
                i_c = LineColor.BLACK_0;
            }
            if ((getColor(i) == LineColor.BLUE_2) && (getColor(j) == LineColor.BLUE_2)) {
                i_c = LineColor.BLUE_2;
            }
            if ((getColor(i) == LineColor.BLUE_2) && (getColor(j) == LineColor.CYAN_3)) {
                return;
            }

            if ((getColor(i) == LineColor.CYAN_3) && (getColor(j) == LineColor.BLACK_0)) {
                return;
            }
            if ((getColor(i) == LineColor.CYAN_3) && (getColor(j) == LineColor.RED_1)) {
                return;
            }
            if ((getColor(i) == LineColor.CYAN_3) && (getColor(j) == LineColor.BLUE_2)) {
                return;
            }
            if ((getColor(i) == LineColor.CYAN_3) && (getColor(j) == LineColor.CYAN_3)) {
                return;
            }

            deleteLine(j);
            deleteLine(i);
            addLine(addLine);
            setColor(getTotal(), i_c);
        }//p2,p1,p4 ixb_ixa,iya_iyb

    }

    public void del_V_all() {
        int total_old = total + 1;
        while (total < total_old) {
            total_old = total;
            for (int i = 1; i <= total - 1; i++) {
                for (int j = i + 1; j <= total; j++) {
                    if (getColor(i) == getColor(j)) {//If the two are the same color, carry out
                        if (getColor(i) != LineColor.CYAN_3) {//Auxiliary live line is not applicable
                            del_V(i, j);
                        }
                    }
                }
            }
        }
    }

    public void del_V_all_cc() {
        int sousuu_old = total + 1;
        while (total < sousuu_old) {
            sousuu_old = total;
            for (int i = 1; i <= total - 1; i++) {
                for (int j = i + 1; j <= total; j++) {
                    del_V(i, j);
                }
            }
        }
    }

    public int del_V(Point p, double hikiyose_hankei, double r) {
        Point q = new Point();
        q.set(closestPoint(p));//q is the end point closer to the point p
        if (q.distanceSquared(p) > hikiyose_hankei * hikiyose_hankei) {
            return 0;
        }

        if (vertex_syuui_numLines_for_del_V(q, r) == 2) {
            int ix, iy;
            ix = i_s[0];
            iy = i_s[1];
            int i_decision;
            i_decision = 0;//If i_hantei is 1, the two line segments do not overlap and are connected in a straight line.
            LineSegment.Intersection i_lineSegment_intersection_decision;
            i_lineSegment_intersection_decision = OritaCalc.line_intersect_decide(get(ix), get(iy), 0.000001, 0.000001);

            if (i_lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_START_OF_S1_INTERSECTS_START_OF_S2_323) {
                i_decision = 1;
            }
            if (i_lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_START_OF_S1_INTERSECTS_END_OF_S2_333) {
                i_decision = 1;
            }
            if (i_lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_END_OF_S1_INTERSECTS_START_OF_S2_343) {
                i_decision = 1;
            }
            if (i_lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_END_OF_S1_INTERSECTS_END_OF_S2_353) {
                i_decision = 1;
            }

            System.out.println("i_lineSegment_intersection_decision=" + i_lineSegment_intersection_decision + "---tyouten_syuui_sensuu_for_del_V(q,r)_" + vertex_syuui_numLines_for_del_V(q, r));
            if (i_decision == 0) {
                return 0;
            }


            if (getColor(ix) != getColor(iy)) {
                return 0;
            }//If the two are not the same color, do not carry out

            LineColor i_c;
            i_c = getColor(ix);

            LineSegment s_ixb_iyb = new LineSegment(getB(ix), getB(iy));
            LineSegment s_ixb_iya = new LineSegment(getB(ix), getA(iy));
            LineSegment s_ixa_iyb = new LineSegment(getA(ix), getB(iy));
            LineSegment s_ixa_iya = new LineSegment(getA(ix), getA(iy));


            if (i_lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_START_OF_S1_INTERSECTS_START_OF_S2_323) {
                deleteLine(iy);
                deleteLine(ix);
                addLine(s_ixb_iyb);
                setColor(getTotal(), i_c);
            }//p2,p1,p4 ixb_ixa,iya_iyb
            if (i_lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_START_OF_S1_INTERSECTS_END_OF_S2_333) {
                deleteLine(iy);
                deleteLine(ix);
                addLine(s_ixb_iya);
                setColor(getTotal(), i_c);
            }//p2,p1,p3 ixb_ixa,iyb_iya
            if (i_lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_END_OF_S1_INTERSECTS_START_OF_S2_343) {
                deleteLine(iy);
                deleteLine(ix);
                addLine(s_ixa_iyb);
                setColor(getTotal(), i_c);
            }//p1,p2,p4 ixa_ixb,iya_iyb
            if (i_lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_END_OF_S1_INTERSECTS_END_OF_S2_353) {
                deleteLine(iy);
                deleteLine(ix);
                addLine(s_ixa_iya);
                setColor(getTotal(), i_c);
            }//p1,p2,p3 ixa_ixb,iyb_iya
        }

        return 0;
    }


    public int del_V_cc(Point p, double hikiyose_hankei, double r) {//2つの折線の色が違った場合カラーチェンジして、点削除する。黒赤は赤赤、黒青は青青、青赤は黒にする
        Point q = new Point();
        q.set(closestPoint(p));//qは点pに近い方の端点
        if (q.distanceSquared(p) > hikiyose_hankei * hikiyose_hankei) {
            return 0;
        }

        if (vertex_syuui_numLines_for_del_V(q, r) == 2) {
            int ix, iy;
            ix = i_s[0];
            iy = i_s[1];
            int i_decision;
            i_decision = 0;//i_hanteiは１なら2線分は重ならず、直線状に繋がっている
            LineSegment.Intersection lineSegment_intersection_decision;
            lineSegment_intersection_decision = OritaCalc.line_intersect_decide(get(ix), get(iy), 0.000001, 0.000001);

            if (lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_START_OF_S1_INTERSECTS_START_OF_S2_323) {
                i_decision = 1;
            }
            if (lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_START_OF_S1_INTERSECTS_END_OF_S2_333) {
                i_decision = 1;
            }
            if (lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_END_OF_S1_INTERSECTS_START_OF_S2_343) {
                i_decision = 1;
            }
            if (lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_END_OF_S1_INTERSECTS_END_OF_S2_353) {
                i_decision = 1;
            }
            if (i_decision == 0) {
                return 0;
            }

            if ((getColor(ix) == LineColor.BLACK_0) && (getColor(iy) == LineColor.BLACK_0)) {
                setColor(ix, LineColor.BLACK_0);
                setColor(iy, LineColor.BLACK_0);
            }
            if ((getColor(ix) == LineColor.BLACK_0) && (getColor(iy) == LineColor.RED_1)) {
                setColor(ix, LineColor.RED_1);
                setColor(iy, LineColor.RED_1);
            }
            if ((getColor(ix) == LineColor.BLACK_0) && (getColor(iy) == LineColor.BLUE_2)) {
                setColor(ix, LineColor.BLUE_2);
                setColor(iy, LineColor.BLUE_2);
            }
            if ((getColor(ix) == LineColor.BLACK_0) && (getColor(iy) == LineColor.CYAN_3)) {
                return 0;
            }

            if ((getColor(ix) == LineColor.RED_1) && (getColor(iy) == LineColor.BLACK_0)) {
                setColor(ix, LineColor.RED_1);
                setColor(iy, LineColor.RED_1);
            }
            if ((getColor(ix) == LineColor.RED_1) && (getColor(iy) == LineColor.RED_1)) {
                setColor(ix, LineColor.RED_1);
                setColor(iy, LineColor.RED_1);
            }
            if ((getColor(ix) == LineColor.RED_1) && (getColor(iy) == LineColor.BLUE_2)) {
                setColor(ix, LineColor.BLACK_0);
                setColor(iy, LineColor.BLACK_0);
            }
            if ((getColor(ix) == LineColor.RED_1) && (getColor(iy) == LineColor.CYAN_3)) {
                return 0;
            }

            if ((getColor(ix) == LineColor.BLUE_2) && (getColor(iy) == LineColor.BLACK_0)) {
                setColor(ix, LineColor.BLUE_2);
                setColor(iy, LineColor.BLUE_2);
            }
            if ((getColor(ix) == LineColor.BLUE_2) && (getColor(iy) == LineColor.RED_1)) {
                setColor(ix, LineColor.BLACK_0);
                setColor(iy, LineColor.BLACK_0);
            }
            if ((getColor(ix) == LineColor.BLUE_2) && (getColor(iy) == LineColor.BLUE_2)) {
                setColor(ix, LineColor.BLUE_2);
                setColor(iy, LineColor.BLUE_2);
            }
            if ((getColor(ix) == LineColor.BLUE_2) && (getColor(iy) == LineColor.CYAN_3)) {
                return 0;
            }

            if ((getColor(ix) == LineColor.CYAN_3) && (getColor(iy) == LineColor.BLACK_0)) {
                return 0;
            }
            if ((getColor(ix) == LineColor.CYAN_3) && (getColor(iy) == LineColor.RED_1)) {
                return 0;
            }
            if ((getColor(ix) == LineColor.CYAN_3) && (getColor(iy) == LineColor.BLUE_2)) {
                return 0;
            }
            if ((getColor(ix) == LineColor.CYAN_3) && (getColor(iy) == LineColor.CYAN_3)) {
                setColor(ix, LineColor.CYAN_3);
                setColor(iy, LineColor.CYAN_3);
            }


            LineColor i_c;
            i_c = getColor(ix);

            LineSegment s_ixb_iyb = new LineSegment(getB(ix), getB(iy));
            LineSegment s_ixb_iya = new LineSegment(getB(ix), getA(iy));
            LineSegment s_ixa_iyb = new LineSegment(getA(ix), getB(iy));
            LineSegment s_ixa_iya = new LineSegment(getA(ix), getA(iy));


            if (lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_START_OF_S1_INTERSECTS_START_OF_S2_323) {
                deleteLine(iy);
                deleteLine(ix);
                addLine(s_ixb_iyb);
                setColor(getTotal(), i_c);
            }//p2,p1,p4 ixb_ixa,iya_iyb
            if (lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_START_OF_S1_INTERSECTS_END_OF_S2_333) {
                deleteLine(iy);
                deleteLine(ix);
                addLine(s_ixb_iya);
                setColor(getTotal(), i_c);
            }//p2,p1,p3 ixb_ixa,iyb_iya
            if (lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_END_OF_S1_INTERSECTS_START_OF_S2_343) {
                deleteLine(iy);
                deleteLine(ix);
                addLine(s_ixa_iyb);
                setColor(getTotal(), i_c);
            }//p1,p2,p4 ixa_ixb,iya_iyb
            if (lineSegment_intersection_decision == LineSegment.Intersection.PARALLEL_END_OF_S1_INTERSECTS_END_OF_S2_353) {
                deleteLine(iy);
                deleteLine(ix);
                addLine(s_ixa_iya);
                setColor(getTotal(), i_c);
            }//p1,p2,p3 ixa_ixb,iyb_iya
        }

        return 0;
    }

    //If the end point of the line segment closest to the point p and the end point closer to the point p is the apex, how many line segments are out (the number of line segments with an end point within the apex and r)
    // for del_V Function of
    public int vertex_syuui_numLines_for_del_V(Point p, double r) {//del_V用の関数
        Point q = new Point();
        q.set(closestPoint(p));//qは点pに近い方の端点
        Point p_temp = new Point();


        int i_return;
        i_return = 0;
        int i_temp;
        i_temp = 1;//ここのi_tempはi_temp=1-i_tempの形でつかうので、0,1,0,1,0,1,,,という風に変化していく
        for (int i = 1; i <= total; i++) {
            p_temp.set(getA(i));
            if (q.distanceSquared(getB(i)) < q.distanceSquared(getA(i))) {
                p_temp.set(getB(i));
            }
            if (q.distanceSquared(p_temp) < r * r) {
                i_temp = 1 - i_temp;
                i_s[i_temp] = i;
                i_return = i_return + 1;
            }

        }

        return i_return;
    }

    //If the end point of the line segment closest to the point p and the end point closer to the point p is the vertex, how many line segments are present (the number of line segments having an end point within the vertex and r).
    public int vertex_surrounding_lineCount(Point p, double r) {
        Point q = new Point();
        q.set(closestPoint(p));//qは点pに近い方の端点
        Point p_temp = new Point();

        int i_return;
        i_return = 0;

        for (int i = 1; i <= total; i++) {
            p_temp.set(getA(i));
            if (q.distanceSquared(getB(i)) < q.distanceSquared(getA(i))) {
                p_temp.set(getB(i));
            }

            if (q.distanceSquared(p_temp) < r * r) {
                i_return++;
            }
        }

        return i_return;
    }

    //If the end point of the line segment closest to the point p and the end point closer to the point p is the vertex, how many red line segments appear (the number of line segments having an end point within the vertex and r).
    public int vertex_surrounding_lineCount_red(Point p, double r) {
        Point q = new Point();
        q.set(closestPoint(p));//q is the end point closer to the point p
        Point p_temp = new Point();

        int i_return = 0;

        for (int i = 1; i <= total; i++) {
            p_temp.set(getA(i));
            if (q.distanceSquared(getB(i)) < q.distanceSquared(getA(i))) {
                p_temp.set(getB(i));
            }
            if (q.distanceSquared(p_temp) < r * r) {
                if (getColor(i) == LineColor.RED_1) {
                    i_return++;
                }
            }

        }

        return i_return;
    }

    //--------------------------------------------
    //If the end point of the line segment closest to the point p and the end point closer to the point p is the vertex, how many blue line segments appear (the number of line segments having an end point within the vertex and r).
    public int vertex_surrounding_lineCount_blue(Point p, double r) {
        Point q = new Point();
        q.set(closestPoint(p));//qは点pに近い方の端点
        Point p_temp = new Point();

        int i_return;
        i_return = 0;

        for (int i = 1; i <= total; i++) {
            p_temp.set(getA(i));
            if (q.distanceSquared(getB(i)) < q.distanceSquared(getA(i))) {
                p_temp.set(getB(i));
            }
            if (q.distanceSquared(p_temp) < r * r) {
                if (getColor(i) == LineColor.BLUE_2) {
                    i_return++;
                }
            }

        }

        return i_return;
    }

    //--------------------------------------------
    //If the end point of the line segment closest to the point p and the end point closer to the point p is the vertex, how many black line segments appear (the number of line segments having an end point within the vertex and r).
    public int vertex_surrounding_lineCount_black(Point p, double r) {
        Point q = new Point();
        q.set(closestPoint(p));//qは点pに近い方の端点
        Point p_temp = new Point();

        int i_return = 0;

        for (int i = 1; i <= total; i++) {
            p_temp.set(getA(i));
            if (q.distanceSquared(getB(i)) < q.distanceSquared(getA(i))) {
                p_temp.set(getB(i));
            }
            if (q.distanceSquared(p_temp) < r * r) {
                if (getColor(i) == LineColor.BLACK_0) {
                    i_return++;
                }
            }
        }

        return i_return;
    }

    //--------------------------------------------
    //If the end point of the line segment closest to the point p and the end point closer to the point p is the vertex, how many auxiliary live lines are present (the number of line segments having an end point within the vertex and r).
    public int vertex_surrounding_lineCount_auxiliary_live_line(Point p, double r) {
        Point q = new Point();
        q.set(closestPoint(p));//qは点pに近い方の端点
        Point p_temp = new Point();


        int i_return = 0;

        for (int i = 1; i <= total; i++) {
            p_temp.set(getA(i));
            if (q.distanceSquared(getB(i)) < q.distanceSquared(getA(i))) {
                p_temp.set(getB(i));
            }
            if (q.distanceSquared(p_temp) < r * r) {
                if (!getColor(i).isFoldingLine()) {
                    i_return = i_return + 1;
                }
            }
        }

        return i_return;
    }


    //線分の活性化されたものを点pの座標にする
    public void set(Point p) {
        for (int i = 1; i <= total; i++) {
            LineSegment si;
            si = getLineSegment(i);
            si.set(p);
        }
    }

    //Divide the polygonal line i by the projection of the point p. However, if the projection of point p is considered to be the same as the end point of any polygonal line, nothing is done.
    public int lineSegment_bunkatu(Point p, int i) {//何もしない=0,分割した=1

        int mts_id;
        mts_id = i;
        LineSegment mts = new LineSegment(getA(mts_id), getB(mts_id));//mtsは点pに最も近い線分

        //直線t上の点pの影の位置（点pと最も近い直線t上の位置）を求める。public Ten oc.kage_motome(Tyokusen t,Ten p){}
        //線分を含む直線を得る public Tyokusen oc.Senbun2Tyokusen(Senbun s){}
        Point pk = new Point();
        pk.set(OritaCalc.findProjection(OritaCalc.lineSegmentToStraightLine(mts), p));//pkは点pの（線分を含む直線上の）影
        //線分の分割-----------------------------------------
        lineSegment_bunkatu(mts_id, pk);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
        return 1;
    }

    public void move(double dx, double dy) {//折線集合全体の位置を移動する。
        Point temp_a = new Point();
        Point temp_b = new Point();
        for (int i = 1; i <= getTotal(); i++) {
            temp_a.set(getA(i));
            temp_b.set(getB(i));
            temp_a.setX(temp_a.getX() + dx);
            temp_a.setY(temp_a.getY() + dy);
            temp_b.setX(temp_b.getX() + dx);
            temp_b.setY(temp_b.getY() + dy);
            setA(i, temp_a);
            setB(i, temp_b);
        }

        for (int i = 1; i <= numCircles(); i++) {
            Circle e_temp = new Circle();
            e_temp.set(getCircle(i));

            e_temp.setX(e_temp.getX() + dx);
            e_temp.setY(e_temp.getY() + dy);
            cir_setCircle(i, e_temp);
        }
    }

    public void move(Point ta, Point tb, Point tc, Point td) {//Move the position of the entire set of polygonal lines.
        double d = OritaCalc.angle(ta, tb, tc, td);
        double r = tc.distance(td) / ta.distance(tb);

        double dx = tc.getX() - ta.getX();
        double dy = tc.getY() - ta.getY();

        Point temp_a = new Point();
        Point temp_b = new Point();
        for (int i = 1; i <= getTotal(); i++) {
            temp_a.set(OritaCalc.point_rotate(ta, getA(i), d, r));
            temp_b.set(OritaCalc.point_rotate(ta, getB(i), d, r));
            temp_a.setX(temp_a.getX() + dx);
            temp_a.setY(temp_a.getY() + dy);
            temp_b.setX(temp_b.getX() + dx);
            temp_b.setY(temp_b.getY() + dy);
            setA(i, temp_a);
            setB(i, temp_b);
        }
    }

    public void check1(double r_hitosii, double parallel_decision) {
        Check1LineSegment.clear();
        unselect_all();
        for (int i = 1; i <= total - 1; i++) {
            if (getColor(i) != LineColor.CYAN_3) {

                LineSegment si = getLineSegment(i);

                for (int j = i + 1; j <= total; j++) {
                    if (getColor(j) != LineColor.CYAN_3) {
                        LineSegment sj;
                        sj = getLineSegment(j);//r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度

                        LineSegment si1 = new LineSegment();
                        si1.set(si);
                        LineSegment sj1 = new LineSegment();
                        sj1.set(sj);

                        if (OritaCalc.line_intersect_decide(si, sj, r_hitosii, parallel_decision) == LineSegment.Intersection.PARALLEL_EQUAL_31) {
                            Check1LineSegment.add(si1);
                            Check1LineSegment.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                        if (OritaCalc.line_intersect_decide(si, sj, r_hitosii, parallel_decision) == LineSegment.Intersection.PARALLEL_START_OF_S1_CONTAINS_START_OF_S2_321) {
                            Check1LineSegment.add(si1);
                            Check1LineSegment.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                        if (OritaCalc.line_intersect_decide(si, sj, r_hitosii, parallel_decision) == LineSegment.Intersection.PARALLEL_START_OF_S2_CONTAINS_START_OF_S1_322) {
                            Check1LineSegment.add(si1);
                            Check1LineSegment.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                        if (OritaCalc.line_intersect_decide(si, sj, r_hitosii, parallel_decision) == LineSegment.Intersection.PARALLEL_START_OF_S1_CONTAINS_END_OF_S2_331) {
                            Check1LineSegment.add(si1);
                            Check1LineSegment.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                        if (OritaCalc.line_intersect_decide(si, sj, r_hitosii, parallel_decision) == LineSegment.Intersection.PARALLEL_END_OF_S2_CONTAINS_START_OF_S1_332) {
                            Check1LineSegment.add(si1);
                            Check1LineSegment.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                        if (OritaCalc.line_intersect_decide(si, sj, r_hitosii, parallel_decision) == LineSegment.Intersection.PARALLEL_END_OF_S1_CONTAINS_START_OF_S2_341) {
                            Check1LineSegment.add(si1);
                            Check1LineSegment.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                        if (OritaCalc.line_intersect_decide(si, sj, r_hitosii, parallel_decision) == LineSegment.Intersection.PARALLEL_START_OF_S2_CONTAINS_END_OF_S1_342) {
                            Check1LineSegment.add(si1);
                            Check1LineSegment.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                        if (OritaCalc.line_intersect_decide(si, sj, r_hitosii, parallel_decision) == LineSegment.Intersection.PARALLEL_END_OF_S1_CONTAINS_END_OF_S2_351) {
                            Check1LineSegment.add(si1);
                            Check1LineSegment.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                        if (OritaCalc.line_intersect_decide(si, sj, r_hitosii, parallel_decision) == LineSegment.Intersection.PARALLEL_END_OF_S2_CONTAINS_END_OF_S1_352) {
                            Check1LineSegment.add(si1);
                            Check1LineSegment.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                        if (OritaCalc.line_intersect_decide(si, sj, r_hitosii, parallel_decision).isContainedInside()) {
                            Check1LineSegment.add(si1);
                            Check1LineSegment.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }

                    }
                }
            }
        }
    }

    public boolean fix1(double r_hitosii, double parallel_decision) {//Returns 0 if nothing is done, 1 if something is modified.
        unselect_all();
        for (int i = 1; i <= total - 1; i++) {
            if (getColor(i) != LineColor.CYAN_3) {

                LineSegment si;
                si = getLineSegment(i);
                for (int j = i + 1; j <= total; j++) {
                    if (getColor(j) != LineColor.CYAN_3) {
                        LineSegment sj;
                        sj = getLineSegment(j);//r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度
                        //T字型交差
                        if (OritaCalc.line_intersect_decide(si, sj, r_hitosii, parallel_decision) == LineSegment.Intersection.PARALLEL_EQUAL_31) {
                            setColor(i, getColor(j));
                            deleteLine(j);
                            return true;
                        }
                        if (OritaCalc.line_intersect_decide(si, sj, r_hitosii, parallel_decision) == LineSegment.Intersection.PARALLEL_START_OF_S1_CONTAINS_START_OF_S2_321) {
                            set_select(i, 2);
                            set_select(j, 2);
                        }
                        if (OritaCalc.line_intersect_decide(si, sj, r_hitosii, parallel_decision) == LineSegment.Intersection.PARALLEL_START_OF_S2_CONTAINS_START_OF_S1_322) {
                            set_select(i, 2);
                            set_select(j, 2);
                        }
                        if (OritaCalc.line_intersect_decide(si, sj, r_hitosii, parallel_decision) == LineSegment.Intersection.PARALLEL_START_OF_S1_CONTAINS_END_OF_S2_331) {
                            set_select(i, 2);
                            set_select(j, 2);
                        }
                        if (OritaCalc.line_intersect_decide(si, sj, r_hitosii, parallel_decision) == LineSegment.Intersection.PARALLEL_END_OF_S2_CONTAINS_START_OF_S1_332) {
                            set_select(i, 2);
                            set_select(j, 2);
                        }
                        if (OritaCalc.line_intersect_decide(si, sj, r_hitosii, parallel_decision) == LineSegment.Intersection.PARALLEL_END_OF_S1_CONTAINS_START_OF_S2_341) {
                            set_select(i, 2);
                            set_select(j, 2);
                        }
                        if (OritaCalc.line_intersect_decide(si, sj, r_hitosii, parallel_decision) == LineSegment.Intersection.PARALLEL_START_OF_S2_CONTAINS_END_OF_S1_342) {
                            set_select(i, 2);
                            set_select(j, 2);
                        }
                        if (OritaCalc.line_intersect_decide(si, sj, r_hitosii, parallel_decision) == LineSegment.Intersection.PARALLEL_END_OF_S1_CONTAINS_END_OF_S2_351) {
                            set_select(i, 2);
                            set_select(j, 2);
                        }
                        if (OritaCalc.line_intersect_decide(si, sj, r_hitosii, parallel_decision) == LineSegment.Intersection.PARALLEL_END_OF_S2_CONTAINS_END_OF_S1_352) {
                            set_select(i, 2);
                            set_select(j, 2);
                        }
                        if (OritaCalc.line_intersect_decide(si, sj, r_hitosii, parallel_decision).isContainedInside()) {
                            set_select(i, 2);
                            set_select(j, 2);
                        }
                    }
                }
            }
        }
        return false;
    }

    public void check2(double r_hitosii, double parallel_decision) {
        Check2LineSegment.clear();

        unselect_all();
        for (int i = 1; i <= total - 1; i++) {
            if (getColor(i) != LineColor.CYAN_3) {

                LineSegment si;
                si = getLineSegment(i);
                for (int j = i + 1; j <= total; j++) {
                    if (getColor(j) != LineColor.CYAN_3) {
                        LineSegment sj;
                        sj = getLineSegment(j);//r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度

                        LineSegment si1 = new LineSegment();
                        si1.set(si);
                        LineSegment sj1 = new LineSegment();
                        sj1.set(sj);

                        //T-shaped intersection
                        if (OritaCalc.line_intersect_decide_sweet(si, sj, r_hitosii, parallel_decision) == LineSegment.Intersection.INTERSECTS_TSHAPE_S1_VERTICAL_BAR_25) {
                            Check2LineSegment.add(si1);
                            Check2LineSegment.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                        if (OritaCalc.line_intersect_decide_sweet(si, sj, r_hitosii, parallel_decision) == LineSegment.Intersection.INTERSECTS_TSHAPE_S1_VERTICAL_BAR_26) {
                            Check2LineSegment.add(si1);
                            Check2LineSegment.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                        if (OritaCalc.line_intersect_decide_sweet(si, sj, r_hitosii, parallel_decision) == LineSegment.Intersection.INTERSECTS_TSHAPE_S2_VERTICAL_BAR_27) {
                            Check2LineSegment.add(si1);
                            Check2LineSegment.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                        if (OritaCalc.line_intersect_decide_sweet(si, sj, r_hitosii, parallel_decision) == LineSegment.Intersection.INTERSECTS_TSHAPE_S2_VERTICAL_BAR_28) {
                            Check2LineSegment.add(si1);
                            Check2LineSegment.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                    }
                }
            }
        }
    }

    public int fix2(double r_hitosii, double heikou_hantei) {//何もしなかったら0、何か修正したら1を返す。
        unselect_all();
        for (int i = 1; i <= total - 1; i++) {
            if (getColor(i) != LineColor.CYAN_3) {

                LineSegment si;
                si = getLineSegment(i);
                for (int j = i + 1; j <= total; j++) {
                    if (getColor(j) != LineColor.CYAN_3) {
                        LineSegment sj;
                        sj = getLineSegment(j);//r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度
                        //T字型交差
                        //折線iをその点pの影で分割する。ただし、点pの影がどれか折線の端点と同じとみなされる場合は何もしない。
                        //	public void senbun_bunkatu(Ten p,int i){
                        if (OritaCalc.line_intersect_decide_sweet(si, sj, r_hitosii, heikou_hantei) == LineSegment.Intersection.INTERSECTS_TSHAPE_S1_VERTICAL_BAR_25) {
                            if (lineSegment_bunkatu(getA(i), j) == 1) {
                                return 1;
                            }
                        }
                        if (OritaCalc.line_intersect_decide_sweet(si, sj, r_hitosii, heikou_hantei) == LineSegment.Intersection.INTERSECTS_TSHAPE_S1_VERTICAL_BAR_26) {
                            if (lineSegment_bunkatu(getB(i), j) == 1) {
                                return 1;
                            }
                        }
                        if (OritaCalc.line_intersect_decide_sweet(si, sj, r_hitosii, heikou_hantei) == LineSegment.Intersection.INTERSECTS_TSHAPE_S2_VERTICAL_BAR_27) {
                            if (lineSegment_bunkatu(getA(j), i) == 1) {
                                return 1;
                            }
                        }
                        if (OritaCalc.line_intersect_decide_sweet(si, sj, r_hitosii, heikou_hantei) == LineSegment.Intersection.INTERSECTS_TSHAPE_S2_VERTICAL_BAR_28) {
                            if (lineSegment_bunkatu(getB(j), i) == 1) {
                                return 1;
                            }
                        }
                    }
                }

            }
        }
        return 0;
    }

    // ***********************************ppppppppppppqqqqqq
//Cirには1番目からcir_size()番目までデータが入っている
    public int numCircles() {
        return circles.size() - 1;
    }

    public Circle getCircle(int i) {
        return circles.get(i);
    }

    public void cir_setCircle(int i, Circle e0) {
        //iの指定があったとき、EnはCirのi-1番目に格納される　
        //i>cir_size()のときは、Cirのi-1番目の円はまだ定義されていないので、とりあえずi-1番目まで円を存在させる必要がある

        if (i > numCircles()) {
            while (i > numCircles()) {
                circles.add(new Circle());
            }
        }

        Circle etemp = new Circle();
        etemp.set(e0);

        circles.set(i, etemp);
    }

    public int check1_size() {
        return Check1LineSegment.size();
    }

    public int check2_size() {
        return Check2LineSegment.size();
    }

    public int check3_size() {
        return Check3LineSegment.size();
    }//Check3Senbには0番目からsize()-1番目までデータが入っている

    public int check4_size() {
        return Check4LineSegment.size();
    }//Check4Senbには0番目からsize()-1番目までデータが入っている

    public int check4_point_size() {
        return check4Point.size();
    }//Check4Tenには0番目からsize()-1番目までデータが入っている

    public LineSegment check1_getLineSegment(int i) {
        return Check1LineSegment.get(i);
    }

    public LineSegment check2_getLineSegment(int i) {
        return Check2LineSegment.get(i);
    }

    public LineSegment check3_getLineSegment(int i) {
        return Check3LineSegment.get(i);
    }

    public LineSegment check4_getLineSegment(int i) {
        return Check4LineSegment.get(i);
    }

    public Point check4_getPoint(int i) {
        return check4Point.get(i);
    }

    public void check3(double r) {//Check the number of lines around the vertex
        Check3LineSegment.clear();
        unselect_all();
        for (int i = 1; i <= total; i++) {
            if (getColor(i) != LineColor.CYAN_3) {
                LineSegment si;
                si = getLineSegment(i);
                Point p = new Point();
                int tss;    //頂点の周りの折線の数。　tss%2==0 偶数、==1 奇数
                int tss_red;    //Number of mountain fold lines around the vertex 。
                int tss_blue;    //頂点の周りの谷折線の数。
                int tss_black;    //頂点の周りの境界線の数。
                int tss_hojyo_kassen;    //頂点の周りの補助活線の数。

                //-----------------
                p.set(si.getA());
                tss = vertex_surrounding_lineCount(p, r);
                tss_red = vertex_surrounding_lineCount_red(p, r);
                tss_blue = vertex_surrounding_lineCount_blue(p, r);
                tss_black = vertex_surrounding_lineCount_black(p, r);
                tss_hojyo_kassen = vertex_surrounding_lineCount_auxiliary_live_line(p, r);

                if ((tss_black != 0) && (tss_black != 2)) {//黒線がないか2本以外の場合はおかしい。
                    Check3LineSegment.add(new LineSegment(p, p));//set_select(i,2);
                }

                if (tss_black == 0) {//黒線がない場合
                    if (tss - tss_hojyo_kassen == tss_red + tss_blue) {//（前提として境界は黒で、山谷未設定折線はないこと。）頂点周囲に赤か青しかない。つまり、用紙内部の点

                        if (Math.abs(tss_red - tss_blue) != 2) {//用紙内部の点で前川定理を満たさないのはダメ
                            Check3LineSegment.add(new LineSegment(p, p));//set_select(i,2);
                        }
                    }
                    if (!extended_fushimi_decide_inside(p)) {
                        Check3LineSegment.add(new LineSegment(p, p));//set_select(i,2);
                    }
                }

                if (tss_black == 2) {//黒線が2本の場合
                    if (!extended_fushimi_decide_sides(p)) {
                        Check3LineSegment.add(new LineSegment(p, p));//set_select(i,2);
                    }
                }

                //-----------------
                p.set(si.getB());
                tss = vertex_surrounding_lineCount(p, r);
                tss_red = vertex_surrounding_lineCount_red(p, r);
                tss_blue = vertex_surrounding_lineCount_blue(p, r);
                tss_black = vertex_surrounding_lineCount_black(p, r);
                tss_hojyo_kassen = vertex_surrounding_lineCount_auxiliary_live_line(p, r);

                //-----------------
                if ((tss_black != 0) && (tss_black != 2)) {//黒線がないか2本以外の場合はおかしい。
                    Check3LineSegment.add(new LineSegment(p, p));//set_select(i,2);
                }

                if (tss_black == 0) {//黒線がない場合
                    if (tss - tss_hojyo_kassen == tss_red + tss_blue) {//（前提として境界は黒で、山谷未設定折線はないこと。）頂点周囲に赤か青しかない。つまり、用紙内部の点
                        if (Math.abs(tss_red - tss_blue) != 2) {//用紙内部の点で前川定理を満たさないのはダメ
                            Check3LineSegment.add(new LineSegment(p, p));//set_select(i,2);
                        }
                    }
                    if (!extended_fushimi_decide_inside(p)) {
                        Check3LineSegment.add(new LineSegment(p, p));//set_select(i,2);
                    }
                }

                if (tss_black == 2) {//黒線が2本の場合
                    if (!extended_fushimi_decide_sides(p)) {
                        Check3LineSegment.add(new LineSegment(p, p));//set_select(i,2);
                    }
                }
            }
        }
    }

    public int Check4Point_overlapping_check(Point p0) {
        for (int i = 0; i < check4_point_size(); i++) {
            Point p = new Point();
            p.set(check4_getPoint(i));
            if ((-0.00000001 < p0.getX() - p.getX()) && (p0.getX() - p.getX() < 0.00000001)) {
                if ((-0.00000001 < p0.getY() - p.getY()) && (p0.getY() - p.getY() < 0.00000001)) {
                    return 1;
                }
            }
        }
        return 0;
    }

    public void check4(double r) {//Check the number of lines around the apex
        Check4LineSegment.clear();
        check4Point.clear();

        unselect_all();

        //Counting places to check
        for (int i = 1; i <= total; i++) {
            if (getColor(i) != LineColor.CYAN_3) {

                LineSegment si;
                si = getLineSegment(i);

                Point pa = new Point();
                pa.set(si.getA());
                if (Check4Point_overlapping_check(pa) == 0) {
                    check4Point.add(pa);
                }

                Point pb = new Point();
                pb.set(si.getB());
                if (Check4Point_overlapping_check(pb) == 0) {
                    check4Point.add(pb);
                }
            }
        }

        System.out.println("check4_T_size() = " + check4_point_size());

        //Selection of whether the place to be checked can be folded flat
        for (int i = 0; i < check4_point_size(); i++) {
            Point p = new Point();
            p.set(check4_getPoint(i));

            if (!i_flat_ok(p, r)) {
                Check4LineSegment.add(new LineSegment(p, p));
            }
        }
    }

    public boolean i_flat_ok(Point p, double r) {//Foldable flat = 1
        double hantei_kyori = 0.00001;
        //If the end point of the line segment closest to the point p and the end point closer to the point p is the apex, how many line segments are present (the number of line segments having an end point within the apex and r).
        int i_customized = 0;    //i_customized% 2 == 0 even, == 1 odd
        int i_tss_red = 0;
        int i_tss_blue = 0;
        int i_tss_black = 0;
        int i_tss_cyan = 0;

        SortingBox_int_double nbox = new SortingBox_int_double();

        for (int i = 1; i <= getTotal(); i++) {
            if ((p.distanceSquared(getA(i)) < r * r) || (p.distanceSquared(getB(i)) < r * r)) {
                i_customized = i_customized + 1;
                if (getColor(i) == LineColor.RED_1) {
                    i_tss_red++;
                } else if (getColor(i) == LineColor.BLUE_2) {
                    i_tss_blue++;
                } else if (getColor(i) == LineColor.BLACK_0) {
                    i_tss_black++;
                } else if (getColor(i).getNumber() >= 3) {
                    i_tss_cyan++;
                }
            }

            //Put a polygonal line with p as the end point in Narabebako
            if (getColor(i).isFoldingLine()) { //Auxiliary live lines are excluded at this stage
                if (p.distance(getA(i)) < hantei_kyori) {
                    nbox.container_i_smallest_first(new int_double(i, OritaCalc.angle(getA(i), getB(i))));
                } else if (p.distance(getB(i)) < hantei_kyori) {
                    nbox.container_i_smallest_first(new int_double(i, OritaCalc.angle(getB(i), getA(i))));
                }
            }
        }

        // Judgment start-------------------------------------------
        if ((i_tss_black != 0) && (i_tss_black != 2)) {//It is strange if there are no black lines or if there are other than two lines.
            return false;
        }

        if (i_tss_black == 0) {//If there is no black line
            if (Math.abs(i_tss_red - i_tss_blue) != 2) {//Do not satisfy Maekawa's theorem in terms of the inside of the paper
                return false;
            }

            return extended_fushimi_decide_inside(p, nbox);
        }

        //When there are two black lines
        return extended_fushimi_decide_sides(p, nbox);
    }

    //Point p に最も近い用紙辺部の端点が拡張伏見定理を満たすか判定
    public boolean extended_fushimi_decide_sides(Point p) {//return　0=満たさない、　1=満たす。　
        double hantei_kyori = 0.00001;

        Point t1 = new Point();
        t1.set(closestPointOfFoldLine(p));//点pに最も近い、「線分の端点」を返すori_s.closestPointは近い点がないと p_return.set(100000.0,100000.0)と返してくる

        //t1を端点とする折線をNarabebakoに入れる
        SortingBox_int_double nbox = new SortingBox_int_double();
        for (int i = 1; i <= getTotal(); i++) {
            if (getColor(i).isFoldingLine()) { //この段階で補助活線は除く
                if (t1.distance(getA(i)) < hantei_kyori) {
                    nbox.container_i_smallest_first(new int_double(i, OritaCalc.angle(getA(i), getB(i))));
                } else if (t1.distance(getB(i)) < hantei_kyori) {
                    nbox.container_i_smallest_first(new int_double(i, OritaCalc.angle(getB(i), getA(i))));
                }
            }
        }

        return extended_fushimi_decide_sides(p, nbox);
    }

    // ---------------------------------
    public boolean extended_fushimi_decide_sides(Point p, SortingBox_int_double nbox) {//return　0=満たさない、　1=満たす。　
        if (nbox.getTotal() == 2) {//t1を端点とする折線の数が2のとき
            if (getColor(nbox.getInt(1)) != LineColor.BLACK_0) {//1本目が黒でないならダメ
                return false;
            }
            if (getColor(nbox.getInt(2)) != LineColor.BLACK_0) {//2本目が黒でないならダメ
                return false;
            }

            //2本の線種が黒黒
            return true;
        }


        //以下はt1を端点とする折線の数が3以上の偶数のとき

        //fushimi_decision_angle_goukei=360.0;


        //辺の折線が,ならべばこnbox,の一番目と最後の順番になるようにする。

        int saisyo_ni_suru = -10;
        for (int i = 1; i <= nbox.getTotal() - 1; i++) {
            if ((getColor(nbox.getInt(i)) == LineColor.BLACK_0) &&
                    (getColor(nbox.getInt(i + 1)) == LineColor.BLACK_0)) {
                saisyo_ni_suru = i + 1;
            }
        }

        if ((getColor(nbox.getInt(nbox.getTotal())) == LineColor.BLACK_0) &&
                (getColor(nbox.getInt(1)) == LineColor.BLACK_0)) {
            saisyo_ni_suru = 1;
        }

        if (saisyo_ni_suru < 0) {
            return false;
        }

        for (int i = 1; i <= saisyo_ni_suru - 1; i++) {
            nbox.shift();
        }

        //ならべばこnbox,の一番目の折線がx軸となす角度が0になるようにする。
        SortingBox_int_double nbox1 = new SortingBox_int_double();

        double sasihiku_kakudo = nbox.getDouble(1);

        for (int i = 1; i <= nbox.getTotal(); i++) {
            int_double i_d_0 = new int_double();
            i_d_0.set(nbox.get_i_d(i));

            i_d_0.setDouble(OritaCalc.angle_between_0_360(i_d_0.getDouble() - sasihiku_kakudo));
            nbox1.add(i_d_0);
        }

        nbox.set(nbox1);

        while (nbox.getTotal() > 2) {//点から出る折線の数が2になるまで実行する
            nbox1.set(extended_fushimi_determine_sides_theorem(nbox));
            if (nbox1.getTotal() == nbox.getTotal()) {
                return false;
            }
            nbox.set(nbox1);
        }

        return true;
    }

    //Obtain SortingBox with a polygonal line starting at b. They are arranged in ascending order of angle with the line segment ba.
    public SortingBox_int_double get_SortingBox_of_vertex_b_surrounding_foldLine(Point a, Point b) {
        SortingBox_int_double r_nbox = new SortingBox_int_double();
        double hantei_kyori = 0.00001;

        //Put a polygonal line with b as the end point in Narabebako

        for (int i = 1; i <= getTotal(); i++) {
            if (getColor(i).isFoldingLine()) { //Auxiliary live lines are excluded at this stage
                if (b.distance(getA(i)) < hantei_kyori) {
                    r_nbox.container_i_smallest_first(new int_double(i, OritaCalc.angle(b, a, getA(i), getB(i))));
                } else if (b.distance(getB(i)) < hantei_kyori) {
                    r_nbox.container_i_smallest_first(new int_double(i, OritaCalc.angle(b, a, getB(i), getA(i))));
                }
            }
        }

        return r_nbox;
    }

    //Operation to make three angles adjacent to each other at the points of the side into one angle or to cut the corner of the side like the extended Fushimi theorem
    public SortingBox_int_double extended_fushimi_determine_sides_theorem(SortingBox_int_double nbox0) {
        SortingBox_int_double nbox1 = new SortingBox_int_double();

        double angle_min = 10000.0;
        double temp_angle;

        //Find the minimum angle angle_min
        for (int k = 1; k <= nbox0.getTotal() - 1; k++) {//kは角度の順番
            temp_angle = nbox0.getDouble(k + 1) - nbox0.getDouble(k);
            if (temp_angle < angle_min) {
                angle_min = temp_angle;
            }
        }

        temp_angle = nbox0.getDouble(2) - nbox0.getDouble(1);
        if (Math.abs(temp_angle - angle_min) < 0.00001) {// 折線を1つ減らせる条件に適合したので、新たにnbox1を作ってリターンする。
            for (int i = 2; i <= nbox0.getTotal(); i++) {
                int_double i_d_0 = new int_double();
                i_d_0.set(nbox0.get_i_d(i));
                nbox1.add(i_d_0);
            }
            return nbox1;
        }

        temp_angle = nbox0.getDouble(nbox0.getTotal()) - nbox0.getDouble(nbox0.getTotal() - 1);
        if (Math.abs(temp_angle - angle_min) < 0.00001) {// 折線を1つ減らせる条件に適合したので、新たにnbox1を作ってリターンする。
            for (int i = 1; i <= nbox0.getTotal() - 1; i++) {
                int_double i_d_0 = new int_double();
                i_d_0.set(nbox0.get_i_d(i));
                nbox1.add(i_d_0);
            }
            return nbox1;
        }

        for (int k = 2; k <= nbox0.getTotal() - 2; k++) {//kは角度の順番
            temp_angle = nbox0.getDouble(k + 1) - nbox0.getDouble(k);
            if (Math.abs(temp_angle - angle_min) < 0.00001) {
                if (getColor(nbox0.getInt(k)) != getColor(nbox0.getInt(k + 1))) {//この場合に隣接する３角度を1つの角度にする
                    // 折線を2つ減らせる条件に適合したので、新たにnbox1を作ってリターンする。

                    for (int i = 1; i <= k - 1; i++) {
                        int_double i_d_0 = new int_double();
                        i_d_0.set(nbox0.get_i_d(i));
                        nbox1.add(i_d_0);
                    }

                    for (int i = k + 2; i <= nbox0.getTotal(); i++) {
                        int_double i_d_0 = new int_double();
                        i_d_0.set(nbox0.get_i_d(i));
                        i_d_0.setDouble(
                                i_d_0.getDouble() - 2.0 * angle_min
                        );
                        nbox1.add(i_d_0);
                    }

                    return nbox1;
                }
            }
        }

        // 折線を減らせる条件に適合した角がなかった場合nbox0とおなじnbox1を作ってリターンする。
        for (int i = 1; i <= nbox0.getTotal(); i++) {
            nbox1.add(nbox0.get_i_d(i));
        }
        return nbox1;
    }

    //Determine if the endpoint inside the paper closest to Point p satisfies the extended Fushimi theorem
    public boolean extended_fushimi_decide_inside(Point p) {//return　0=満たさない、　1=満たす。　
        double hantei_kyori = 0.00001;

        Point t1 = new Point();
        t1.set(closestPointOfFoldLine(p));//点pに最も近い、「線分の端点」を返すori_s.mottomo_tikai_Tenは近い点がないと p_return.set(100000.0,100000.0)と返してくる

        //t1を端点とする折線をNarabebakoに入れる
        SortingBox_int_double nbox = new SortingBox_int_double();
        for (int i = 1; i <= getTotal(); i++) {
            if (getColor(i).isFoldingLine()) { //この段階で補助活線は除く
                if (t1.distance(getA(i)) < hantei_kyori) {
                    nbox.container_i_smallest_first(new int_double(i, OritaCalc.angle(getA(i), getB(i))));
                } else if (t1.distance(getB(i)) < hantei_kyori) {
                    nbox.container_i_smallest_first(new int_double(i, OritaCalc.angle(getB(i), getA(i))));
                }
            }
        }

        return extended_fushimi_decide_inside(p, nbox);
    }

    public boolean extended_fushimi_decide_inside(Point p, SortingBox_int_double nbox) {//return　0=満たさない、　1=満たす。　
        double hantei_kyori = 0.00001;

        if (nbox.getTotal() % 2 == 1) {//t1を端点とする折線の数が奇数のとき
            return false;
        }

        if (nbox.getTotal() == 2) {//t1を端点とする折線の数が2のとき
            if (getColor(nbox.getInt(1)) != getColor(nbox.getInt(2))) {//2本の線種が違うなら角度関係なしにダメ
                return false;
            }

            //The following is when the two line types are blue-blue or red-red
            LineSegment.Intersection i_senbun_kousa_hantei = OritaCalc.line_intersect_decide(get(nbox.getInt(1)), get(nbox.getInt(2)), 0.00001, 0.00001);

            return switch (i_senbun_kousa_hantei) {
                case PARALLEL_START_OF_S1_INTERSECTS_START_OF_S2_323,
                        PARALLEL_START_OF_S1_INTERSECTS_END_OF_S2_333,
                        PARALLEL_END_OF_S1_INTERSECTS_END_OF_S2_353,
                        PARALLEL_END_OF_S1_INTERSECTS_START_OF_S2_343 -> true;
                default -> false;
            };
        }

        //以下はt1を端点とする折線の数が4以上の偶数のとき

        fushimi_decision_angle_goukei = 360.0;

        SortingBox_int_double nbox1 = new SortingBox_int_double();

        while (nbox.getTotal() > 2) {//点から出る折線の数が2になるまで実行する
            nbox1.set(extended_fushimi_decision_inside_theorem(nbox));
            if (nbox1.getTotal() == nbox.getTotal()) {
                return false;
            }
            nbox.set(nbox1);
        }

        double temp_kakudo = OritaCalc.angle_between_0_kmax(
                OritaCalc.angle_between_0_kmax(nbox.getDouble(1), fushimi_decision_angle_goukei)
                        -
                        OritaCalc.angle_between_0_kmax(nbox.getDouble(2), fushimi_decision_angle_goukei)
                , fushimi_decision_angle_goukei
        );

        return Math.abs(fushimi_decision_angle_goukei - temp_kakudo * 2.0) < hantei_kyori;//この0だけ、角度がおかしいという意味
    }

    public SortingBox_int_double extended_fushimi_decision_inside_theorem(SortingBox_int_double nbox0) {//拡張伏見定理で隣接する３角度を1つの角度にする操作
        SortingBox_int_double nboxtemp = new SortingBox_int_double();
        SortingBox_int_double nbox1 = new SortingBox_int_double();
        int tikai_orisen_jyunban;
        int tooi_orisen_jyunban;

        double kakudo_min = 10000.0;

        //角度の最小値kakudo_minを求める
        for (int k = 1; k <= nbox0.getTotal(); k++) {//kは角度の順番
            tikai_orisen_jyunban = k;
            if (tikai_orisen_jyunban > nbox0.getTotal()) {
                tikai_orisen_jyunban = tikai_orisen_jyunban - nbox0.getTotal();
            }
            tooi_orisen_jyunban = k + 1;
            if (tooi_orisen_jyunban > nbox0.getTotal()) {
                tooi_orisen_jyunban = tooi_orisen_jyunban - nbox0.getTotal();
            }

            double temp_kakudo = OritaCalc.angle_between_0_kmax(
                    OritaCalc.angle_between_0_kmax(nbox0.getDouble(tooi_orisen_jyunban), fushimi_decision_angle_goukei)
                            -
                            OritaCalc.angle_between_0_kmax(nbox0.getDouble(tikai_orisen_jyunban), fushimi_decision_angle_goukei)

                    , fushimi_decision_angle_goukei
            );

            if (temp_kakudo < kakudo_min) {
                kakudo_min = temp_kakudo;
            }
        }

        for (int k = 1; k <= nbox0.getTotal(); k++) {//kは角度の順番
            double temp_kakudo = OritaCalc.angle_between_0_kmax(nbox0.getDouble(2) - nbox0.getDouble(1), fushimi_decision_angle_goukei);

            if (Math.abs(temp_kakudo - kakudo_min) < 0.00001) {
                if (getColor(nbox0.getInt(1)) != getColor(nbox0.getInt(2))) {//この場合に隣接する３角度を1つの角度にする
                    // 折線を2つ減らせる条件に適合したので、新たにnbox1を作ってリターンする。

                    double kijyun_kakudo = nbox0.getDouble(3);

                    for (int i = 1; i <= nbox0.getTotal(); i++) {
                        int_double i_d_0 = new int_double();
                        i_d_0.set(nbox0.get_i_d(i));

                        i_d_0.setDouble(
                                OritaCalc.angle_between_0_kmax(i_d_0.getDouble() - kijyun_kakudo, fushimi_decision_angle_goukei)
                        );

                        nboxtemp.add(i_d_0);
                    }

                    for (int i = 3; i <= nboxtemp.getTotal(); i++) {
                        int_double i_d_0 = new int_double();
                        i_d_0.set(nboxtemp.get_i_d(i));

                        nbox1.add(i_d_0);
                    }

                    fushimi_decision_angle_goukei = fushimi_decision_angle_goukei - 2.0 * kakudo_min;
                    return nbox1;
                }
            }
            nbox0.shift();

        }

        // 折線を2つ減らせる条件に適合した角がなかった場合nbox0とおなじnbox1を作ってリターンする。
        for (int i = 1; i <= nbox0.getTotal(); i++) {
            nbox1.add(nbox0.get_i_d(i));
        }
        return nbox1;
    }

    public double get_x_max() {//sousuu=0のときは0.0を返す
        if (total == 0) {
            return 0.0;
        }
        double dm = getAX(1);
        for (int i = 1; i <= total; i++) {
            if (dm < getAX(i)) {
                dm = getAX(i);
            }
            if (dm < getBX(i)) {
                dm = getBX(i);
            }
        }
        return dm;
    }

    public double get_x_min() {//sousuu=0のときは0.0を返す
        if (total == 0) {
            return 0.0;
        }
        double dm = getAX(1);
        for (int i = 1; i <= total; i++) {
            if (dm > getAX(i)) {
                dm = getAX(i);
            }
            if (dm > getBX(i)) {
                dm = getBX(i);
            }
        }
        return dm;
    }

    public double get_y_max() {//sousuu=0のときは0.0を返す
        if (total == 0) {
            return 0.0;
        }
        double dm = getAY(1);
        for (int i = 1; i <= total; i++) {
            if (dm < getAY(i)) {
                dm = getAY(i);
            }
            if (dm < getBY(i)) {
                dm = getBY(i);
            }
        }
        return dm;
    }

    public double get_y_min() {//sousuu=0のときは0.0を返す
        if (total == 0) {
            return 0.0;
        }
        double dm = getAY(1);
        for (int i = 1; i <= total; i++) {
            if (dm > getAY(i)) {
                dm = getAY(i);
            }
            if (dm > getBY(i)) {
                dm = getBY(i);
            }
        }
        return dm;
    }

    public void select_Takakukei(Polygon polygon, String Dousa_mode) {
        //select_lX,unselect_lX
        //"lX" lXは小文字のエルと大文字のエックス。Senbun s_step1と重複する部分のある線分やX交差する線分を対象にするモード。

        boolean i_kono_foldLine_wo_kaeru;//i_この折線を変える

        for (int i = 1; i <= total; i++) {
            i_kono_foldLine_wo_kaeru = false;
            LineSegment s = getLineSegment(i);

            Polygon.Intersection intersection = polygon.inside_outside_check(s);

            if (intersection == Polygon.Intersection.BORDER) {
                i_kono_foldLine_wo_kaeru = true;
            }
            if (intersection == Polygon.Intersection.BORDER_INSIDE) {
                i_kono_foldLine_wo_kaeru = true;
            }
            if (intersection == Polygon.Intersection.INSIDE) {
                i_kono_foldLine_wo_kaeru = true;
            }

            if (i_kono_foldLine_wo_kaeru) {
                if (Dousa_mode.equals("select")) {
                    s.setSelected(2);
                }
                if (Dousa_mode.equals("unselect")) {
                    s.setSelected(0);
                }
            }
        }

    }

    public void select_lX(LineSegment s_step1, String Dousa_mode) {
        //select_lX,unselect_lX
        //"lX" lXは小文字のエルと大文字のエックス。Senbun s_step1と重複する部分のある線分やX交差する線分を対象にするモード。

        boolean i_kono_foldLine_wo_kaeru;//i_この折線を変える

        for (int i = 1; i <= total; i++) {
            i_kono_foldLine_wo_kaeru = false;
            LineSegment s;
            s = getLineSegment(i);

            if (OritaCalc.lineSegmentoverlapping(s, s_step1)) {
                i_kono_foldLine_wo_kaeru = true;
            }
            if (OritaCalc.lineSegment_X_kousa_decide(s, s_step1)) {
                i_kono_foldLine_wo_kaeru = true;
            }

            if (i_kono_foldLine_wo_kaeru) {
                if (Dousa_mode.equals("select_lX")) {
                    s.setSelected(2);
                }
                if (Dousa_mode.equals("unselect_lX")) {
                    s.setSelected(0);
                }
            }
        }
    }
}
