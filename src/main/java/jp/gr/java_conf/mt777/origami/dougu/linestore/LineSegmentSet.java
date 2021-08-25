package jp.gr.java_conf.mt777.origami.dougu.linestore;

import jp.gr.java_conf.mt777.graphic2d.linesegment.LineSegment;
import jp.gr.java_conf.mt777.graphic2d.oritacalc.OritaCalc;
import jp.gr.java_conf.mt777.graphic2d.point.Point;
import jp.gr.java_conf.mt777.kiroku.memo.Memo;
import jp.gr.java_conf.mt777.origami.orihime.LineColor;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Collection of line segments.
 */
public class LineSegmentSet {
    int numLineSegments;               //Total number of line segments actually used
    ArrayList<LineSegment> lineSegments = new ArrayList<>(); //Instantiation of line segments

    public LineSegmentSet() {
        reset();
    }

    public void reset() {
        numLineSegments = 0;
        lineSegments.clear();
        lineSegments.add(new LineSegment());
    }

    public void set(LineSegmentSet ss) {
        numLineSegments = ss.getNumLineSegments();
        for (int i = 0; i <= numLineSegments; i++) {
            LineSegment s = getLine(i);
            s.set(ss.get(i));
        }
    }

    private LineSegment getLine(int i) {
        if (numLineSegments + 1 > lineSegments.size()) {
            while (numLineSegments + 1 > lineSegments.size()) {
                lineSegments.add(new LineSegment());
            }
        }//It won't work without this sentence. I don't know exactly why it should be this sentence.
        return lineSegments.get(i);
    }

    private void setLine(int i, LineSegment s) {
        if (numLineSegments + 1 > lineSegments.size()) {
            while (numLineSegments + 1 > lineSegments.size()) {
                lineSegments.add(new LineSegment());
            }
        }//It won't work without this sentence. I don't know exactly why it should be this sentence.
        if (i + 1 <= lineSegments.size()) {
            lineSegments.set(i, s);
        } //For some reason, it doesn't work without this if
    }

    //Get the total number of line segments
    public int getNumLineSegments() {
        return numLineSegments;
    }

    public void setNumLineSegments(int i) {
        numLineSegments = i;
    }

    //Get a line segment
    public LineSegment get(int i) {
        return getLine(i);
    }

    //Get the endpoint of the i-th line segment
    public Point getA(int i) {
        LineSegment s = getLine(i);
        return s.getA();
    }

    public Point getB(int i) {
        LineSegment s = getLine(i);
        return s.getB();
    }

    /**
     * Get the endpoint of the i-th line segment
     */
    public double getAX(int i) {
        LineSegment s = getLine(i);
        return s.getAX();
    }

    public double getBX(int i) {
        LineSegment s;
        s = getLine(i);
        return s.getBX();
    }

    public double getAY(int i) {
        LineSegment s = getLine(i);
        return s.getAY();
    }

    public double getBY(int i) {
        LineSegment s = getLine(i);
        return s.getBY();
    }

    /**
     * Set the position of the end point of the i-th line segment
     */
    public void setA(int i, Point p) {
        LineSegment s = getLine(i);
        s.setA(p);
    }

    public void setB(int i, Point p) {
        LineSegment s = getLine(i);
        s.setB(p);
    }

    /**
     * Enter the value of the i-th line segment
     */
    public void set(int i, Point p, Point q, LineColor ic, int ia) {
        LineSegment s = getLine(i);
        s.set(p, q, ic, ia);
    }

    /**
     * Enter the color of the i-th line segment
     */
    public void setColor(int i, LineColor icol) {
        LineSegment s = getLine(i);
        s.setColor(icol);
    }

    /**
     * Output the color of the i-th line segment
     */
    public LineColor getColor(int i) {
        LineSegment s = getLine(i);
        return s.getColor();
    }

    /**
     * Output the activity of the i-th line segment
     */
    public int getActive(int i) {
        LineSegment s = getLine(i);
        return s.getActive();
    }

    /**
     * Output the information of all line segments of the line segment set as Memo.
     */
    public Memo getMemo() {
        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<線分集合>"); // <Line segment set>

        for (int i = 1; i <= numLineSegments; i++) {
            memo1.addLine("番号," + i); // number,
            LineSegment s = getLine(i);
            memo1.addLine("色," + s.getColor()); // colour,
            memo1.addLine("座標," + s.getAX() + "," + s.getAY() + "," +
                    s.getBX() + "," + s.getBY()); // Coordinate,
        }

        return memo1;
    }

    public void setMemo(Memo memo1) {
        int reading_flag = 0;//If it is 0, it will not be read. If it is 1, read it.
        int iNumber = 0;
        LineColor ic;

        double ax, ay, bx, by;
        String str;

        //Read the file .orh for Orihime

        //First find the total number of line segments
        int iLine = 0;
        for (int i = 1; i <= memo1.getLineCount(); i++) {
            StringTokenizer tk = new StringTokenizer(memo1.getLine(i), ",");

            str = tk.nextToken();
            if (str.equals("<線分集合>")) { // <Line segment set>
                reading_flag = 1;
            }

            if ((reading_flag == 1) && (str.equals("番号"))) { // number
                iLine = iLine + 1;
            }
        }

        numLineSegments = iLine;
        //First the total number of line segments was calculated

        for (int i = 1; i <= memo1.getLineCount(); i++) {
            StringTokenizer tk = new StringTokenizer(memo1.getLine(i), ",");
            str = tk.nextToken();

            if (str.equals("<線分集合>")) { // Line segment set
                reading_flag = 1;
            }

            if ((reading_flag == 1) && (str.equals("番号"))) { // number
                str = tk.nextToken();
                iNumber = Integer.parseInt(str);

            }
            if ((reading_flag == 1) && (str.equals("色"))) { // colour
                str = tk.nextToken();
                ic = LineColor.from(str);
                LineSegment s = getLine(iNumber);
                s.setColor(ic);
            }
            if ((reading_flag == 1) && (str.equals("座標"))) { // coordinate
                str = tk.nextToken();
                ax = Double.parseDouble(str);
                str = tk.nextToken();
                ay = Double.parseDouble(str);
                str = tk.nextToken();
                bx = Double.parseDouble(str);
                str = tk.nextToken();
                by = Double.parseDouble(str);

                LineSegment s = getLine(iNumber);
                s.set(ax, ay, bx, by);
            }
        }
    }

    /**
     * Arrangement of line segment set when inputting development drawing
     */
    public void split_arrangement() {//Arrangement of wire diagrams obtained by folding estimation, etc.
        System.out.println("分割整理　１、点削除");
        point_removal();          //Just in case, remove the dotted line segment
        System.out.println("分割整理　２、重複線分削除");
        overlapping_line_removal();//Just in case, if there are two line segments that match exactly, remove one.
        System.out.println("分割整理　３、交差分割");
        intersect_divide();
        System.out.println("分割整理　４、点削除");
        point_removal();             //Exclude dotted line segments to organize the wire diagram for folding estimation
        System.out.println("分割整理　５、重複線分削除");
        overlapping_line_removal(); //If there are two line segments that match exactly, remove one to organize the wire diagram for folding estimation.
    }


    /**
     * Replace the mountains and valleys of all lines. There is no change in line types other than mountains and valleys such as boundaries.
     */
    public void changeAllMountainValley() {
        LineColor ic_temp;

        for (int ic_id = 1; ic_id <= numLineSegments; ic_id++) {
            ic_temp = getColor(ic_id);
            if (ic_temp == LineColor.RED_1) {
                ic_temp = LineColor.BLUE_2;
            } else if (ic_temp == LineColor.BLUE_2) {
                ic_temp = LineColor.RED_1;
            }
            setColor(ic_id, ic_temp);
        }
    }

    //Arrangement of line segment sets to generate SubFace
    public void bunkatu_seiri_for_SubFace_generation() {//Arrangement of wire diagrams obtained by folding estimation, etc.
        System.out.println("　　Senbunsyuugouの中で、Smenを発生させるための線分集合の整理");
        System.out.println("分割整理　１、点削除前	getsousuu() = " + getNumLineSegments());
        point_removal();          //Just in case, remove the dotted line segment
        System.out.println("分割整理　２、重複線分削除前	getsousuu() = " + getNumLineSegments());
        overlapping_line_removal();//念のため、全く一致する線分が２つあれば１つを除く
        System.out.println("分割整理　３、交差分割前	getsousuu() = " + getNumLineSegments());
        intersect_divide();
        System.out.println("分割整理　４、点削除前	getsousuu() = " + getNumLineSegments());
        point_removal();             //折り畳み推定の針金図の整理のため、点状の線分を除く
        System.out.println("分割整理　５、重複線分削除前	getsousuu() = " + getNumLineSegments());
        overlapping_line_removal(); //折り畳み推定の針金図の整理のため、全く一致する線分が２つあれば１つを除く
        System.out.println("分割整理　５、重複線分削除後	getsousuu() = " + getNumLineSegments());
    }


    //Remove dotted line segments
    public void point_removal() {
        for (int i = 1; i <= numLineSegments; i++) {
            LineSegment s;
            s = getLine(i);
            if (OritaCalc.equal(s.getA(), s.getB())) {
                deleteLineSegment(i);
                i = i - 1;
            }
        }
    }

    public void point_removal(double r) {
        for (int i = 1; i <= numLineSegments; i++) {
            LineSegment s = getLine(i);
            if (OritaCalc.equal(s.getA(), s.getB(), r)) {
                deleteLineSegment(i);
                i = i - 1;
            }
        }
    }

    /**
     * When there are two completely overlapping line segments, the one with the latest number is deleted.
     */
    public void overlapping_line_removal(double r) {
        boolean[] removal_flg = new boolean[numLineSegments + 1];
        LineSegment[] snew = new LineSegment[numLineSegments + 1];
        for (int i = 1; i <= numLineSegments; i++) {
            removal_flg[i] = false;
            snew[i] = new LineSegment();
        }

        for (int i = 1; i <= numLineSegments - 1; i++) {
            LineSegment si = getLine(i);
            for (int j = i + 1; j <= numLineSegments; j++) {
                LineSegment sj = getLine(j);
                if (r <= -9999.9) {
                    if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.PARALLEL_EQUAL_31) {
                        removal_flg[j] = true;
                    }
                } else {
                    if (OritaCalc.line_intersect_decide(si, sj, r, r) == LineSegment.Intersection.PARALLEL_EQUAL_31) {
                        removal_flg[j] = true;
                    }
                }
            }
        }

        int smax = 0;
        for (int i = 1; i <= numLineSegments; i++) {
            if (!removal_flg[i]) {
                LineSegment si = getLine(i);
                smax = smax + 1;
                snew[smax].set(si);
            }
        }

        numLineSegments = smax;
        for (int i = 1; i <= numLineSegments; i++) {
            LineSegment si = getLine(i);
            si.set(snew[i]);
        }
    }

    public void overlapping_line_removal() {
        overlapping_line_removal(-10000.0);
    }

    public boolean overlapping_line_removal(int i, int j) {    //Returns 1 if duplicates are removed, 0 otherwise
        if (i == j) {
            return false;
        }
        LineSegment si;
        si = getLine(i);
        LineSegment sj;
        sj = getLine(j);
        if (OritaCalc.line_intersect_decide(si, sj) == LineSegment.Intersection.PARALLEL_EQUAL_31) {  //31 indicates that si and sj overlap exactly the same
            deleteLineSegment(j);
            return true;
        }
        return false;
    }

    /**
     * Divide the two line segments at the intersection of the two intersecting line segments. If there were two line segments that completely overlapped, both would remain without any processing.
     */
    public void intersect_divide() {
        int i_divide = 1;//1 if there is a split, 0 if not

        ArrayList<Integer> k_flg = new ArrayList<>();//A flag that indicates that there is an effect of crossing.

        for (int i = 0; i <= numLineSegments + 1; i++) {
            k_flg.add(1);
        }

        while (i_divide != 0) {
            i_divide = 0;
            for (int i = 1; i <= numLineSegments; i++) {
                Integer I_k_flag = k_flg.get(i);
                if (I_k_flag == 1) {
                    k_flg.set(i, 0);
                    for (int j = 1; j <= numLineSegments; j++) {
                        if (i != j) {
                            Integer J_k_flag = k_flg.get(j);
                            if (J_k_flag == 1) {
                                int old_sousuu = numLineSegments;
                                boolean itemp = intersect_divide(i, j);
                                if (old_sousuu < numLineSegments) {
                                    for (int is = old_sousuu + 1; is <= numLineSegments; is++) {
                                        k_flg.add(1);
                                    }
                                }
                                if (itemp) {
                                    i_divide = i_divide + 1;
                                    k_flg.set(i, 1);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Divide the two line segments at the intersection of the two intersecting line segments. After splitting 1. Returns 0 if not done. From Orihime 2.002, the color of the line after splitting is also controlled (if there is an overlap, it will be unified and the color will be the one with the later number).
     */
    public boolean intersect_divide(int i, int j) {
        if (i == j) {
            return false;
        }

        LineSegment si = getLine(i);
        LineSegment sj = getLine(j);

        Point p1 = new Point(si.getA());
        Point p2 = new Point(si.getB());
        Point p3 = new Point(sj.getA());
        Point p4 = new Point(sj.getB());
        Point pk = new Point();

        double ixmax = Math.max(si.getAX(), si.getBX());
        double ixmin = Math.min(si.getAX(), si.getBX());
        double iymax = Math.max(si.getAY(), si.getBY());
        double iymin = Math.min(si.getAY(), si.getBY());

        double jxmax = Math.max(sj.getAX(), sj.getBX());
        double jxmin = Math.min(sj.getAX(), sj.getBX());
        double jymax = Math.max(sj.getAY(), sj.getBY());
        double jymin = Math.min(sj.getAY(), sj.getBY());

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

        LineSegment.Intersection intersect_decide = OritaCalc.line_intersect_decide(si, sj);
        switch (intersect_decide) {
            case INTERSECTS_1:
                pk.set(OritaCalc.findIntersection(si, sj));    //<<<<<<<<<<<<<<<<<<<<<<<

                si.setA(p1);
                si.setB(pk);
                sj.setA(p3);
                sj.setB(pk);
                addLine(p2, pk, si.getColor());
                addLine(p4, pk, sj.getColor());
                return true;
            case INTERSECTS_TSHAPE_S1_VERTICAL_BAR_25:
            case INTERSECTS_TSHAPE_S1_VERTICAL_BAR_26:
                pk.set(OritaCalc.findIntersection(si, sj));    //<<<<<<<<<<<<<<<<<<<<<<<

                sj.setA(p3);
                sj.setB(pk);
                addLine(p4, pk, sj.getColor());
                return true;

            case INTERSECTS_TSHAPE_S2_VERTICAL_BAR_27:
            case INTERSECTS_TSHAPE_S2_VERTICAL_BAR_28:
                pk.set(OritaCalc.findIntersection(si, sj));    //<<<<<<<<<<<<<<<<<<<<<<<

                si.setA(p1);
                si.setB(pk);
                addLine(p2, pk, si.getColor());
                return true;

            case PARALLEL_EQUAL_31: //If the two line segments are exactly the same, do nothing.
                return false;
            case PARALLEL_START_OF_S1_CONTAINS_START_OF_S2_321: {//The endpoints of two line segments (p1 and p3) overlap at one point. si contains sj
                si.setA(p2);
                si.setB(p4);

                LineColor overlapping_col;
                overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                sj.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_START_OF_S2_CONTAINS_START_OF_S1_322: {//The endpoints of two line segments (p1 and p3) overlap at one point. sj contains si
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
            case PARALLEL_START_OF_S1_CONTAINS_END_OF_S2_331: {//The endpoints of two line segments (p1 and p4) overlap at one point. si contains sj
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
            case PARALLEL_END_OF_S2_CONTAINS_START_OF_S1_332: {//The endpoints of two line segments (p1 and p4) overlap at one point. sj contains si
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
            case PARALLEL_END_OF_S1_CONTAINS_START_OF_S2_341: {//The endpoints of two line segments (p2 and p3) overlap at one point. si contains sj
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
            case PARALLEL_START_OF_S2_CONTAINS_END_OF_S1_342: {//The endpoints of two line segments (p2 and p3) overlap at one point. sj contains si
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
            case PARALLEL_END_OF_S1_CONTAINS_END_OF_S2_351: {//The endpoints of two line segments (p2 and p4) overlap at one point. si contains sj
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
            case PARALLEL_END_OF_S2_CONTAINS_END_OF_S1_352: {//The endpoints of two line segments (p2 and p4) overlap at one point. sj contains si
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
            case PARALLEL_S1_INCLUDES_S2_361: {//In order of p1-p3-p4-p2
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
            case PARALLEL_S1_INCLUDES_S2_362: {//In order of p1-p4-p3-p2
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
            case PARALLEL_S2_INCLUDES_S1_363: {//In order of p3-p1-p2-p4
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
            case PARALLEL_S2_INCLUDES_S1_364: {//In order of p3-p2-p1-p4
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
            case PARALLEL_S1_END_OVERLAPS_S2_START_371: {//In order of p1-p3-p2-p4
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
            case PARALLEL_S1_END_OVERLAPS_S2_END_372: {//In order of p1-p4-p2-p3
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
            case PARALLEL_S1_START_OVERLAPS_S2_END_373: {//In order of p3-p1-p4-p2
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
            case PARALLEL_S1_START_OVERLAPS_S2_START_374: {//In order of p4-p1-p3-p2
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
            default:
                return false;
        }
    }


    /**
     * Add line segment
     */
    public void addLine(Point pi, Point pj, LineColor i_c) {
        numLineSegments++;

        LineSegment s = getLine(numLineSegments);
        s.set(pi, pj, i_c);
    }

    /**
     * Add line segment
     */
    public void addLine(double ax, double ay, double bx, double by, LineColor ic) {
        numLineSegments++;

        LineSegment s = getLine(numLineSegments);
        s.set(ax, ay, bx, by, ic);
    }

    /**
     * Add line segment
     */
    public void addLine(Point pi, Point pj) {
        numLineSegments++;

        LineSegment s = getLine(numLineSegments);

        s.setA(pi);
        s.setB(pj);
    }

    /**
     * Delete line segment
     */
    public void deleteLineSegment(int j) {   //Delete the jth line segment
        for (int i = j; i <= numLineSegments - 1; i++) {
            LineSegment si = getLine(i);
            LineSegment si1 = getLine(i + 1);
            si.set(si1);
        }
        numLineSegments--;
    }

    /**
     * Get the length of the i-th line segment
     */
    public double getLength(int i) {
        LineSegment s = getLine(i);
        return s.getLength();
    }

    /**
     * Remove the branching line segments without forming a closed polygon.
     */
    public void branch_trim(double r) {
        boolean iflga;
        boolean iflgb;
        for (int i = 1; i <= numLineSegments; i++) {
            iflga = false;
            iflgb = false;
            LineSegment si;
            si = getLine(i);
            for (int j = 1; j <= numLineSegments; j++) {
                if (i != j) {
                    LineSegment sj;
                    sj = getLine(j);
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

            if (!(iflga && iflgb)) {
                deleteLineSegment(i);
                i = 1;
            }
        }
    }

    /**
     * Delete only one separated line segment
     */
    public void singleLineSegment_delete(double r) {
        boolean iflg;
        for (int i = 1; i <= numLineSegments; i++) {
            iflg = false;
            LineSegment si;
            si = getLine(i);
            for (int j = 1; j <= numLineSegments; j++) {
                if (i != j) {
                    LineSegment sj;
                    sj = getLine(j);
                    if (OritaCalc.distance(si.getA(), sj.getA()) < r) {
                        iflg = true;
                    }
                    if (OritaCalc.distance(si.getB(), sj.getB()) < r) {
                        iflg = true;
                    }
                    if (OritaCalc.distance(si.getA(), sj.getB()) < r) {
                        iflg = true;
                    }
                    if (OritaCalc.distance(si.getB(), sj.getA()) < r) {
                        iflg = true;
                    }
                }
            }

            if (!iflg) {
                deleteLineSegment(i);
                i = 1;
            }
        }
    }


    /**
     * A function that searches for a line segment close to point p (within r) and returns that number (however, the jth line segment is out of scope). Returns 0 if there is no close line segment ---------------------------------
     * If there is no line segment to exclude, set j to 0 or a negative integer.
     * 070317 When the additional function j is -10, the unactivated branch (getiactive (i) is 0) is targeted.
     */
    public int lineSegment_search(Point p, double r, int j) {
        if (j == -10) {
            for (int i = 1; i <= numLineSegments; i++) {
                if (((lineSegment_position_search(i, p, r) == 1) && (i != j)) && (getActive(i) == 0)) {
                    return i;
                }
            }
            for (int i = 1; i <= numLineSegments; i++) {
                if (((lineSegment_position_search(i, p, r) == 2) && (i != j)) && (getActive(i) == 0)) {
                    return i;
                }
            }
            for (int i = 1; i <= numLineSegments; i++) {
                if (((lineSegment_position_search(i, p, r) == 3) && (i != j)) && (getActive(i) == 0)) {
                    return i;
                }
            }
            return 0;
        }

        for (int i = 1; i <= numLineSegments; i++) {
            if ((lineSegment_position_search(i, p, r) == 1) && (i != j)) {
                return i;
            }
        }
        for (int i = 1; i <= numLineSegments; i++) {
            if ((lineSegment_position_search(i, p, r) == 2) && (i != j)) {
                return i;
            }
        }
        for (int i = 1; i <= numLineSegments; i++) {
            if ((lineSegment_position_search(i, p, r) == 3) && (i != j)) {
                return i;
            }
        }
        return 0;
    }


    /**
     * A function that determines where the point p is close to the specified line segment (within r)
     * 0 = not close, 1 = close to point a, 2 = close to point b, 3 = close to handle
     */
    public int lineSegment_position_search(int i, Point p, double r) {
        if (r > OritaCalc.distance(p, getA(i))) {
            return 1;
        }//Whether it is close to point a
        if (r > OritaCalc.distance(p, getB(i))) {
            return 2;
        }//Whether it is close to point b
        if (r > OritaCalc.distance_lineSegment(p, get(i))) {
            return 3;
        }//Whether it is close to the handle
        return 0;
    }


    /**
     * Returns the number of the line segment closest to the point p
     */
    public int closest_lineSegment_Search(Point p) {
        int minrid = 0;
        double minr = 100000;
        for (int i = 1; i <= numLineSegments; i++) {
            double sk = OritaCalc.distance_lineSegment(p, get(i));
            if (minr > sk) {
                minr = sk;
                minrid = i;
            }//Whether it is close to the handle

        }
        return minrid;
    }

    /**
     * Returns the endpoint of the line segment closest to point p
     */
    public Point closest_point_search(Point p) {
        Point p_return = new Point();
        p_return.set(100000.0, 100000.0);
        Point p_temp = new Point();
        for (int i = 1; i <= numLineSegments; i++) {
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

    /**
     * Activation of line segments near point p
     */
    public void activate(Point p, double r) {
        for (int i = 1; i <= numLineSegments; i++) {
            LineSegment si = getLine(i);
            si.activate(p, r);
        }
    }

    /**
     * Inactivation of all lines
     */
    public void deactivate() {
        for (int i = 1; i <= numLineSegments; i++) {
            LineSegment si = getLine(i);
            si.deactivate();
        }
    }


    /**
     * Make the activated line segment the coordinates of point p
     */
    public void set(Point p) {
        for (int i = 1; i <= numLineSegments; i++) {
            LineSegment si = getLine(i);
            si.set(p);
        }

    }

    /**
     * If there is a line segment i0 in the line segment set and a line segment other than i0 that completely overlaps, the number is returned. If not, it returns -10.
     */
    public int overlapping_lineSegment_search(int i0) {
        for (int i = 1; i <= numLineSegments; i++) {
            if (i != i0) {
                if (OritaCalc.line_intersect_decide(get(i), get(i0)) == LineSegment.Intersection.PARALLEL_EQUAL_31) {
                    return i;
                }
            }
        }
        return -10;
    }
}
