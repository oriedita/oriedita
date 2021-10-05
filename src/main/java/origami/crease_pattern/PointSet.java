package origami.crease_pattern;

import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Point_p;
import origami.crease_pattern.element.Polygon;
import origami_editor.editor.Save;
import origami.folding.element.Face;
import origami.crease_pattern.element.Line;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A collection of points.
 *
 * Every line can be part of a line and/or a face.
 */
public class PointSet implements Serializable {
    int numFaces_temp;

    int numPoints;               //Total number of points actually used
    int numLines;               //Total number of lines actually used
    int numFaces;               //Total number of faces actually used

    Point_p[] points;//Instantiation of points
    Line[] lines;//Instantiation of lines
    int[] lineInFaceBorder_min;
    int[] lineInFaceBorder_max;

    Face[] faces; //Face instantiation

    double[] line_x_max;
    double[] line_x_min;
    double[] line_y_max;
    double[] line_y_min;

    double[] face_x_max;
    double[] face_x_min;
    double[] face_y_max;
    double[] face_y_min;

    List<List<Integer>> point_linking;//point_linking [i] [j] is the number of points connected to t [i]. The number of Tem is stored in t [0].

    int[][] face_adjacent;//face_adjacent [i] [j] is the Line number at the boundary between m [i] and m [j]. Stores 0 when m [i] and m [j] are not adjacent.

    public PointSet() {
        reset();
    }

    //---------------------------------------
    public void reset() {
        numPoints = 0;
        numLines = 0;
        numFaces = 0;
    }

    //---------------------------------------
    public void configure(int numPoints, int numLines, int numFaces) { //Make sure it passes at the beginning and after a reset.
        numFaces_temp = numFaces;

        points = new Point_p[numPoints + 1];
        point_linking = new ArrayList<>(numPoints + 1);

        point_linking.add(new ArrayList<>());
        for (int i = 0; i <= numPoints; i++) {
            List<Integer> list = new ArrayList<>();
            list.add(0);
            point_linking.add(list);
        }

        for (int i = 0; i <= numPoints; i++) {
            points[i] = new Point_p();
            setPointLinking(i, 0, 0);
        }

        lines = new Line[numLines + 1];
        lineInFaceBorder_min = new int[numLines + 1];
        lineInFaceBorder_max = new int[numLines + 1];
        for (int i = 0; i <= numLines; i++) {
            lines[i] = new Line();
            lineInFaceBorder_min[i] = 0;
            lineInFaceBorder_max[i] = 0;
        }

        faces = new Face[numFaces + 1];

        face_adjacent = new int[numFaces + 1][numFaces + 1];

        for (int i = 0; i <= numFaces; i++) {
            faces[i] = new Face();
            for (int j = 0; j <= numFaces; j++) {
                face_adjacent[i][j] = 0;
            }
        }

        line_x_max = new double[numLines + 1];
        line_x_min = new double[numLines + 1];
        line_y_max = new double[numLines + 1];
        line_y_min = new double[numLines + 1];

        face_x_max = new double[numFaces + 1];
        face_x_min = new double[numFaces + 1];
        face_y_max = new double[numFaces + 1];
        face_y_min = new double[numFaces + 1];
    }

    //---------------
    private int getPointLinking(int i, int j) {
        return point_linking.get(i).get(j);
    }

    private void setPointLinking(int i, int j, int tid) {
        if (j + 1 > point_linking.get(i).size()) {
            while (j + 1 > point_linking.get(i).size()) {
                point_linking.get(i).add(0);
            }
        }//It won't work without this sentence. I don't know exactly why it should be this sentence.
        point_linking.get(i).set(j, tid);
    }

    //------------------------------
    private double getAverage_x() {
        double x = 0.0;
        for (int i = 1; i <= numPoints; i++) {
            x = x + points[i].getX();
        }
        return x / ((double) numPoints);
    }

    public void set(PointSet ts) {
        numPoints = ts.getNumPoints();
        numLines = ts.getNumLines();
        numFaces = ts.getNumFaces();
        for (int i = 0; i <= numPoints; i++) {
            points[i].set(ts.getPoint(i));                                                         //  <<<-------
            for (int j = 1; j <= ts.getPointLinking(i, 0); j++) {
                setPointLinking(i, j, ts.getPointLinking(i, j));
            }
        }
        for (int i = 0; i <= numLines; i++) {
            lines[i].set(ts.getLine(i));
            lineInFaceBorder_min[i] = ts.get_lineInFaceBorder_min(i);
            lineInFaceBorder_max[i] = ts.get_lineInFaceBorder_max(i);
        }
        for (int i = 0; i <= numFaces; i++) {
            faces[i] = new Face(ts.getFace(i));
            for (int j = 0; j <= numFaces; j++) {
                face_adjacent[i][j] = ts.getFaceAdjecent(i, j);
            }
        }
    }

    public void set(int i, Point tn) {
        points[i].set(tn);
    }                                               //  <<<-------

    private int getFaceAdjecent(int i, int j) {
        return face_adjacent[i][j];
    }

    private int get_lineInFaceBorder_min(int i) {
        return lineInFaceBorder_min[i];
    }

    private int get_lineInFaceBorder_max(int i) {
        return lineInFaceBorder_max[i];
    }

    //Determine if the point is inside a face. 0 is not inside, 1 is on the border, 2 is inside
    public Polygon.Intersection simple_inside(Point p, int n) {    // 0 = external, 1 = boundary, 2 = internal
        if (p.getX() + 0.5 < face_x_min[n]) {
            return Polygon.Intersection.OUTSIDE;
        }
        if (p.getX() - 0.5 > face_x_max[n]) {
            return Polygon.Intersection.OUTSIDE;
        }
        if (p.getY() + 0.5 < face_y_min[n]) {
            return Polygon.Intersection.OUTSIDE;
        }
        if (p.getY() - 0.5 > face_y_max[n]) {
            return Polygon.Intersection.OUTSIDE;
        }
        return inside(p, faces[n]);
    }

    //Determine if the point is inside a face.
    public Polygon.Intersection inside(Point p, int n) {      //0=外部、　1=境界、　2=内部
        return inside(p, faces[n]);
    }

    //Determine if the point is inside a face. 0 is not inside, 1 is on the border, 2 is inside
    private Polygon.Intersection inside(Point point, Face face) {      //0=外部、　1=境界、　2=内部
        Polygon polygon;
        polygon = makePolygon(face);
        return polygon.inside(point);
    }

    //Determine which surface the point is inside. If it is 0, it is not inside any surface, if it is negative, it is on the boundary line, and if it is a positive number, it is inside. If there are multiple applicable surface numbers, the one with the smaller number is returned.
    public int inside(Point p) {
        for (int i = 1; i <= getNumFaces(); i++) {
            if (inside(p, i) == Polygon.Intersection.INSIDE) {
                return i;
            }
            if (inside(p, i) == Polygon.Intersection.BORDER) {
                return -i;
            }
        }
        return 0;
    }

    //Make Face polygonal
    private Polygon makePolygon(Face face) {
        Polygon polygon = new Polygon(face.getNumPoints());
        for (int i = 0; i <= face.getNumPoints(); i++) {
            polygon.set(i, points[face.getPointId(i)]);
        }
        return polygon;
    }

    // Even a part of the line segment s0 is inside the surface of the convex polygon (the boundary line is not regarded as the inside)
    // Returns 1 if it exists, 0 otherwise. If the surface is a concave polygon, the result will be strange, so do not use it.
    public boolean simple_convex_inside(int ib, int im) {
        //バグがあるようだったが，多分取り除けた
        if (line_x_max[ib] + 0.5 < face_x_min[im]) {
            return false;
        }
        if (line_x_min[ib] - 0.5 > face_x_max[im]) {
            return false;
        }
        if (line_y_max[ib] + 0.5 < face_y_min[im]) {
            return false;
        }
        if (line_y_min[ib] - 0.5 > face_y_max[im]) {
            return false;
        }

        return convex_inside(new LineSegment(points[lines[ib].getBegin()], points[lines[ib].getEnd()]), faces[im]);
    }

    private boolean convex_inside(LineSegment s0, Face mn) {
        Polygon tk;//=new Takakukei();
        tk = makePolygon(mn);
        return tk.convex_inside(s0);
    }

    public boolean convex_inside(double d, int ib, int im) {
        LineSegment sn = new LineSegment(points[lines[ib].getBegin()], points[lines[ib].getEnd()]);
        return convex_inside(OritaCalc.moveParallel(sn, d), faces[im]);
    }

    //Make a line a line segment
    private LineSegment lineToLineSegment(Line line) {
        return new LineSegment(points[line.getBegin()], points[line.getEnd()]);
    }

    //Returns 1 if two lines are parallel and partially or wholly overlap, otherwise 0. If one point overlaps, 0 is returned.
    public boolean parallel_overlap(int ib1, int ib2) {
        LineSegment.Intersection skh;
        skh = OritaCalc.determineLineSegmentIntersection(lineToLineSegment(lines[ib1]), lineToLineSegment(lines[ib2]));

        return skh.isSegmentOverlapping();
    }


    //面の内部の点を求める
    public Point insidePoint_surface(int n) {
        return insidePoint_surface(faces[n]);
    }

    //面の内部の点を求める
    private Point insidePoint_surface(Face mn) {
        Polygon tk;
        tk = makePolygon(mn);
        return tk.insidePoint_find();
    }

    private double calculateArea(Face mn) {
        Polygon tk;
        tk = makePolygon(mn);
        return tk.calculateArea();
    }

    public int getNumPoints() {
        return numPoints;
    }   //Get the total number of points

    public int getNumLines() {
        return numLines;
    }   //棒の総数を得る

    public int getNumFaces() {
        return numFaces;
    }   //面の総数を得る

    public int getPointId(int i, int j) {
        return faces[i].getPointId(j);
    }  // void setTensuu(int i){Tensuu=i;}

    public double getPointX(int i) {
        return points[i].getX();
    }

    public double getPointY(int i) {
        return points[i].getY();
    }

    public Point getPoint(int i) {
        return points[i];
    }   //点を得る       <<<------------tは、スーパークラスのTenのサブクラスTen_Pクラスのオブジェクト。スーパークラスの変数にサブクラスのオブジェクトを代入可能なので、このまま使う。

    private Line getLine(int i) {
        return lines[i];
    }   //棒を得る

    public Point getBeginPointFromLineId(int i) {
        return points[getBegin(i)];
    }    //棒のidから前点を得る              <<<------------　　同上

    public Point getEndPointFromLineId(int i) {
        return points[getEnd(i)];
    }    //棒のidから後点を得る              <<<------------　　同上


    public LineSegment getLineSegmentFromLineId(int i) {
        return lineToLineSegment(getLine(i));
    }    //棒のidからSenbunを得る

    public Face getFace(int i) {
        return faces[i];
    }   //面を得る

    public int getBegin(int i) {
        return lines[i].getBegin();
    } //棒のidから前点のidを得る

    public int getEnd(int i) {
        return lines[i].getEnd();
    } //棒のidから後点のidを得る

    public double getBeginX(int i) {
        return points[lines[i].getBegin()].getX();
    }

    public double getBeginY(int i) {
        return points[lines[i].getBegin()].getY();
    }

    public double getEndX(int i) {
        return points[lines[i].getEnd()].getX();
    }

    public double getEndY(int i) {
        return points[lines[i].getEnd()].getY();
    }

    public int getPointsCount(int i) {
        return faces[i].getNumPoints();
    }

    public void setPoint(int i, Point tn) {
        points[i].set(tn);
    }                                                        //   <<<------------

    public void addPoint(double x, double y) {
        numPoints = numPoints + 1;
        points[numPoints].set(x, y);
    }   //点を加える

    public void addLine(int i, int j, LineColor icol) {
        numLines = numLines + 1;
        lines[numLines].set(i, j, icol);
    }   //棒を加える

    public LineColor getColor(int i) {
        return lines[i].getColor();
    }

    private void t_renketu_sakusei() {
        for (int k = 1; k <= numLines; k++) {
            setPointLinking(lines[k].getBegin(), 0, getPointLinking(lines[k].getBegin(), 0) + 1);
            setPointLinking(lines[k].getBegin(), getPointLinking(lines[k].getBegin(), 0), lines[k].getEnd());
            setPointLinking(lines[k].getEnd(), 0, getPointLinking(lines[k].getEnd(), 0) + 1);
            setPointLinking(lines[k].getEnd(), getPointLinking(lines[k].getEnd(), 0), lines[k].getBegin());
        }
    }

    //Find the number of the point when going from point i to point j and then going from point j to the right side of point i.
    private int getRPoint(int i, int j) {
        int n = 0;
        double angle = 876.0;   //Keep angle in a large number

        boolean iflg = false;
        for (int k = 1; k <= getPointLinking(i, 0); k++) {
            if (getPointLinking(i, k) == j) {
                iflg = true;
                break;
            }
        }

        if (!iflg) {
            return 0;
        }//点iと点jが連結していない時は0を返す

        for (int ik = 1; ik <= getPointLinking(j, 0); ik++) {
            int k;
            k = getPointLinking(j, ik);
            if (k != i) {
                if (OritaCalc.angle(points[j], points[i], points[j], points[k]) <= angle) {
                    n = k;
                    angle = OritaCalc.angle(points[j], points[i], points[j], points[k]);
                }
            }
        }
        return n; //点jに連結している点が点iしかない時は0を返す
    }
    //--------------------------------

    private Face Face_request(int i, int j) {//Find the surface by following the bar on the right side for the first time from the i-th point and the j-th point.
        Face tempFace = new Face();
        tempFace.addPointId(i);
        tempFace.addPointId(j);
        int nextT;

        nextT = getRPoint(tempFace.getPointId(1), tempFace.getPointId(2));
        do {
            if (nextT == 0) {
                tempFace.reset();
                return tempFace;
            }//エラー時の対応
            tempFace.addPointId(nextT);
            nextT = getRPoint(tempFace.getPointId(tempFace.getNumPoints() - 1), tempFace.getPointId(tempFace.getNumPoints()));
        } while (!tempFace.containsPointId(nextT));
        tempFace.align();
        return tempFace;
    }

    //-------------------------------------
    public void FaceOccurrence() {
        int flag1;
        Face tempFace;
        numFaces = 0;
        t_renketu_sakusei();

        for (int i = 1; i <= numLines; i++) {
            tempFace = Face_request(lines[i].getBegin(), lines[i].getEnd());
            flag1 = 0;   //　0なら面を追加する。1なら　面を追加しない。
            for (int j = 1; j <= numFaces; j++) {
                if (equals(tempFace, faces[j])) {
                    flag1 = 1;
                    break;
                }
            }

            if (((flag1 == 0) && (tempFace.getNumPoints() != 0)) &&
                    (calculateArea(tempFace) > 0.0)) {
                addFace(tempFace);
            }
            //

            tempFace = Face_request(lines[i].getEnd(), lines[i].getBegin());
            flag1 = 0;   //　0なら面を追加する。1なら　面を追加しない。
            for (int j = 1; j <= numFaces; j++) {
                if (equals(tempFace, faces[j])) {
                    flag1 = 1;
                    break;
                }
            }

            if (((flag1 == 0) && (tempFace.getNumPoints() != 0)) && (calculateArea(tempFace) > 0.0)) {
                addFace(tempFace);
            }
        }

        System.out.print("全面数　＝　");
        System.out.println(numFaces);
        Face_adjacent_create();

        //Registration of both sides of line
        for (int ib = 1; ib <= numLines; ib++) {
            lineInFaceBorder_min[ib] = lineinFaceBorder_min_search(ib);
            lineInFaceBorder_max[ib] = lineInFaceBorder_max_search(ib);
        }
    }

    //BouやMenの座標の最大値、最小値を求める。kantan_totu_naibu関数にのみ用いる。kantan_totu_naibu関数を使うなら折り畳み推定毎にやる必要あり。
    public void LineFaceMaxMinCoordinate() {
        //Find the maximum and minimum coordinates of Line (this may be better done immediately after Line is added than done here)
        for (int ib = 1; ib <= numLines; ib++) {

            line_x_max[ib] = points[lines[ib].getBegin()].getX();
            line_x_min[ib] = points[lines[ib].getBegin()].getX();
            line_y_max[ib] = points[lines[ib].getBegin()].getY();
            line_y_min[ib] = points[lines[ib].getBegin()].getY();

            if (line_x_max[ib] < points[lines[ib].getEnd()].getX()) {
                line_x_max[ib] = points[lines[ib].getEnd()].getX();
            }
            if (line_x_min[ib] > points[lines[ib].getEnd()].getX()) {
                line_x_min[ib] = points[lines[ib].getEnd()].getX();
            }
            if (line_y_max[ib] < points[lines[ib].getEnd()].getY()) {
                line_y_max[ib] = points[lines[ib].getEnd()].getY();
            }
            if (line_y_min[ib] > points[lines[ib].getEnd()].getY()) {
                line_y_min[ib] = points[lines[ib].getEnd()].getY();
            }
            faceMaxMinCoordinate();
        }
    }

    private void faceMaxMinCoordinate() {
        //Find the maximum and minimum of Face's coordinates
        for (int faceId = 1; faceId <= numFaces; faceId++) {
            face_x_max[faceId] = points[faces[faceId].getPointId(1)].getX();
            face_x_min[faceId] = points[faces[faceId].getPointId(1)].getX();
            face_y_max[faceId] = points[faces[faceId].getPointId(1)].getY();
            face_y_min[faceId] = points[faces[faceId].getPointId(1)].getY();
            for (int i = 2; i <= faces[faceId].getNumPoints(); i++) {
                if (face_x_max[faceId] < points[faces[faceId].getPointId(i)].getX()) {
                    face_x_max[faceId] = points[faces[faceId].getPointId(i)].getX();
                }
                if (face_x_min[faceId] > points[faces[faceId].getPointId(i)].getX()) {
                    face_x_min[faceId] = points[faces[faceId].getPointId(i)].getX();
                }
                if (face_y_max[faceId] < points[faces[faceId].getPointId(i)].getY()) {
                    face_y_max[faceId] = points[faces[faceId].getPointId(i)].getY();
                }
                if (face_y_min[faceId] > points[faces[faceId].getPointId(i)].getY()) {
                    face_y_min[faceId] = points[faces[faceId].getPointId(i)].getY();
                }
            }
        }
    }

    //--------------
    //Returns the faceId with the smaller faceId of the faces containing the bar lineId as the boundary (there are up to two faces). Returns 0 if there is no face containing the bar as the boundary
    private int lineinFaceBorder_min_search(int lineId) {
        for (int faceId = 1; faceId <= numFaces; faceId++) {
            if (lineInFaceBorder(faceId, lineId)) {
                return faceId;
            }
        }
        return 0;
    }

    //Returns the faceId with the larger faceId among the faces containing the line lineId as the boundary (there are two faces at the maximum). Returns 0 if there is no face containing the bar as the boundary
    private int lineInFaceBorder_max_search(int lineId) {
        for (int faceId = numFaces; faceId >= 1; faceId--) {
            if (lineInFaceBorder(faceId, lineId)) {
                return faceId;
            }
        }
        return 0;
    }

    //---------------

    //Boundary of lines Boundary surface (two sides in yellow) Here, faceId of the proliferating branch of faceId was made.
    public int lineInFaceBorder_min_lookup(int lineId) {
        return lineInFaceBorder_min[lineId];
    }

    //Returns the faceId with the larger faceId of the faces containing the bar lineId as the boundary (there are up to two faces). Returns 0 if there is no face containing the line as the boundary
    public int lineInFaceBorder_max_lookup(int lineId) {
        return lineInFaceBorder_max[lineId];
    }

    //---------------
    private boolean equals(Face m, Face n) { //Returns 1 if they are the same, 0 if they are different
        if (m.getNumPoints() != n.getNumPoints()) {
            return false;
        }

        for (int i = 1; i <= m.getNumPoints(); i++) {
            if (m.getPointId(i) != n.getPointId(i)) {
                return false;
            }
        }

        return true;

    }

    //Returns 1 if the boundary of Face [faceId] contains Point [pointId], 0 if pointId does not.
    public boolean pointInFaceBorder(int faceId, int pointId) {
        for (int i = 1; i <= faces[faceId].getNumPoints(); i++) {
            if (pointId == faces[faceId].getPointId(i)) {
                return true;
            }
        }
        return false;
    }

    //Returns true if line [lineId] is included in the boundary of face [faceId], false if it is not included
    private boolean lineInFaceBorder(int faceId, int lineId) {
        for (int i = 1; i <= faces[faceId].getNumPoints() - 1; i++) {
            if ((lines[lineId].getBegin() == faces[faceId].getPointId(i)) && (lines[lineId].getEnd() == faces[faceId].getPointId(i + 1))) {
                return true;
            }
            if ((lines[lineId].getEnd() == faces[faceId].getPointId(i)) && (lines[lineId].getBegin() == faces[faceId].getPointId(i + 1))) {
                return true;
            }
        }
        if ((lines[lineId].getBegin() == faces[faceId].getPointId(faces[faceId].getNumPoints())) && (lines[lineId].getEnd() == faces[faceId].getPointId(1))) {
            return true;
        }
        return (lines[lineId].getEnd() == faces[faceId].getPointId(faces[faceId].getNumPoints())) && (lines[lineId].getBegin() == faces[faceId].getPointId(1));
    }

    //------------------------------------------------------
    private void Face_adjacent_create() {
        System.out.println("面となり作成　開始");
        for (int im = 1; im <= numFaces - 1; im++) {
            for (int in = im + 1; in <= numFaces; in++) {
                face_adjacent[im][in] = 0;
                face_adjacent[in][im] = 0;
                int ima, imb, ina, inb;
                for (int iim = 1; iim <= faces[im].getNumPoints(); iim++) {
                    ima = faces[im].getPointId(iim);
                    if (iim == faces[im].getNumPoints()) {
                        imb = faces[im].getPointId(1);
                    } else {
                        imb = faces[im].getPointId(iim + 1);
                    }

                    for (int iin = 1; iin <= faces[in].getNumPoints(); iin++) {
                        ina = faces[in].getPointId(iin);

                        if (iin == faces[in].getNumPoints()) {
                            inb = faces[in].getPointId(1);
                        } else {
                            inb = faces[in].getPointId(iin + 1);
                        }

                        if (((ima == ina) && (imb == inb)) || ((ima == inb) && (imb == ina))) {
                            int ib;
                            ib = line_search(ima, imb);
                            face_adjacent[im][in] = ib;
                            face_adjacent[in][im] = ib;
                        }
                    }
                }

            }
        }
        System.out.println("面となり作成　終了");
    }

    //Returns the line number containing points t1 and t2
    private int line_search(int t1, int t2) {
        for (int i = 1; i <= numLines; i++) {
            if ((lines[i].getBegin() == t1) && (lines[i].getEnd() == t2)) {
                return i;
            }
            if ((lines[i].getBegin() == t2) && (lines[i].getEnd() == t1)) {
                return i;
            }
        }
        return 0;
    }

    // If Face [im] and Face [ib] are adjacent, return the id number of the bar at the boundary. Returns 0 if not adjacent
    public int Face_adjacent_determine(int im, int in) {
        return face_adjacent[im][in];
    }

    private void addFace(Face tempFace) {
        numFaces = numFaces + 1;

        faces[numFaces].reset();
        for (int i = 1; i <= tempFace.getNumPoints(); i++) {
            faces[numFaces].addPointId(tempFace.getPointId(i));
        }
        faces[numFaces].setColor(tempFace.getColor());
    }

    /**
     * Returns the number of the closest point that is closer than a certain distance to the given coordinates. If there is no Ten within a certain distance, 0 is returned.
     */
    public int closestPointId(Point p, double r) {
        int ireturn = 0;
        double rmin = 1000000.0;
        double rtemp;
        for (int i = 1; i <= numPoints; i++) {
            rtemp = OritaCalc.distance(p, points[i]);
            if (rtemp < r) {
                if (rtemp < rmin) {
                    rmin = rtemp;
                    ireturn = i;
                }
            }
        }
        return ireturn;
    }


    /**
     * Returns the distance of the closest point that is closer than a certain distance to the given coordinates. If there is no Ten within a certain distance, 1000000.0 is returned.
     */
    public double closest_Point_distance(Point p, double r) {
        double rmin = 1000000.0;
        double rtemp;
        for (int i = 1; i <= numPoints; i++) {
            rtemp = OritaCalc.distance(p, points[i]);
            if (rtemp < r) {
                if (rtemp < rmin) {
                    rmin = rtemp;
                }
            }
        }
        return rmin;
    }

    public int getSelectedPointsNum() {
        int r_int = 0;
        for (int i = 1; i <= numPoints; i++) {

            if (points[i].getPointState()) {
                r_int = r_int + 1;
            }

        }
        return r_int;
    }

    public void setPointStateTrue(int i) {
        points[i].setPointStateTrue();
    }

    public void setPointStateFalse(int i) {
        points[i].setPointStateFalse();
    }

    public void setAllPointStateFalse() {
        for (int i = 1; i <= numPoints; i++) {
            points[i].setPointStateFalse();
        }
    }

    public void changePointState(int i) {
        Point_p point = points[i];
        if (point.getPointState()) {
            point.setPointStateFalse();
        } else {
            point.setPointStateTrue();
        }
    }

    public boolean getPointState(int i) {
        return points[i].getPointState();
    }

    public void statePointMove(Point p) {
        for (int i = 1; i <= numPoints; i++) {

            if (points[i].getPointState()) {
                set(i, p);
            }
        }
    }

    public Save getSave() {
        Save save = new Save();

        for (int i = 1; i <= numPoints; i++) {
            Point p = new Point();
            p.set(points[i]);
            save.addPoint(p);
        }

        return save;
    }

    public void setSave(Save save) {
        for (int i = 0; i < save.getPoints().size(); i++) {
            points[i+1].set(save.getPoints().get(i));
        }
    }
}
