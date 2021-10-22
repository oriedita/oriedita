package origami.crease_pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import origami.crease_pattern.element.*;
import origami.data.ListArray;
import origami.data.quadTree.QuadTree;
import origami.data.quadTree.adapter.PointSetFaceAdapter;
import origami.data.symmetricMatrix.SymmetricMatrix;
import origami.folding.element.Face;
import origami_editor.editor.Save;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A collection of points.
 *
 * Every line can be part of a line and/or a face.
 */
public class PointSet implements Serializable {
    Map<Integer, Point_p> points;//Instantiation of points
    List<Line> lines;//Instantiation of lines, 1-indexed

    List<Face> faces; //Face instantiation, 1-indexed

    @JsonIgnore
    List<List<Integer>> point_linking;//point_linking [i] [j] is the number of points connected to t [i]. The number of Tem is stored in t [0].

    /**
     * Contains the value of the line which connects two faces.
     */
    @JsonIgnore
    SymmetricMatrix faceAdjacent;

    public PointSet() {
        reset();
    }

    //---------------------------------------
    public void reset() {
        faceAdjacent = null;
        points = new HashMap<>();
        faces = new ArrayList<>();
        lines = new ArrayList<>();
    }

    //---------------------------------------
    public void configure(int numPoints, int numLines, int numFaces) { //Make sure it passes at the beginning and after a reset.
        points = new HashMap<>(numPoints);
        point_linking = new ArrayList<>(numPoints + 1);

        point_linking.add(new ArrayList<>());
        for (int i = 0; i <= numPoints; i++) {
            List<Integer> list = new ArrayList<>();
            list.add(0);
            point_linking.add(list);
        }

        for (int i = 0; i <= numPoints; i++) {
            setPointLinking(i, 0, 0);
        }

        lines = new ArrayList<>(numLines + 1);
        lines.add(new Line());

        faces = new ArrayList<>(numFaces + 1);
        faces.add(new Face());

        faceAdjacent = SymmetricMatrix.create(numFaces, (int) Math.ceil(Math.log(numLines + 1) / Math.log(2)));
    }

    public Set<Map.Entry<Integer, Point_p>> iterPoints() {
        return points.entrySet();
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

    public void set(PointSet ts) {
        points.clear();
        for (int i = 0; i <= ts.getNumPoints(); i++) {
            Point_p p = new Point_p();
            p.set(ts.getPoint(i));
            points.put(i, p);
            for (int j = 1; j <= ts.getPointLinking(i, 0); j++) {
                setPointLinking(i, j, ts.getPointLinking(i, j));
            }
        }
        lines.clear();
        for (int i = 0; i <= ts.getNumLines(); i++) {
            Line line = new Line();
            line.set(ts.getLine(i));
            lines.add(line);
        }
        faces.clear();
        faces.add(new Face());
        for (int i = 1; i <= ts.getNumFaces(); i++) {
            faces.add(new Face(ts.getFace(i)));
            for (int j = 1; j <= ts.getNumFaces(); j++) {
                faceAdjacent.set(i, j, ts.getFaceAdjecent(i, j));
            }
        }
    }

    //Determine if the point is inside a face. 0 is not inside, 1 is on the border, 2 is inside
    public Polygon.Intersection simple_inside(Point p, Face face) {    // 0 = external, 1 = boundary, 2 = internal
        if (p.getX() + 0.5 < face.getxMin()) {
            return Polygon.Intersection.OUTSIDE;
        }
        if (p.getX() - 0.5 > face.getxMax()) {
            return Polygon.Intersection.OUTSIDE;
        }
        if (p.getY() + 0.5 < face.getyMin()) {
            return Polygon.Intersection.OUTSIDE;
        }
        if (p.getY() - 0.5 > face.getyMax()) {
            return Polygon.Intersection.OUTSIDE;
        }
        return inside(p, face);
    }

    //Determine if the point is inside a face. 0 is not inside, 1 is on the border, 2 is inside
    public Polygon.Intersection inside(Point point, Face face) {      //0=外部、　1=境界、　2=内部
        Polygon polygon;
        polygon = makePolygon(face);
        return polygon.inside(point);
    }

    //Determine which surface the point is inside. If it is 0, it is not inside any surface, if it is negative, it is on the boundary line, and if it is a positive number, it is inside. If there are multiple applicable surface numbers, the one with the smaller number is returned.
    public int inside(Point p) {
        for (int i = 1; i <= getNumFaces(); i++) {
            Face face = getFace(i);
            if (inside(p, face) == Polygon.Intersection.INSIDE) {
                return i;
            }
            if (inside(p, face) == Polygon.Intersection.BORDER) {
                return -i;
            }
        }
        return 0;
    }

    //Make Face polygonal
    private Polygon makePolygon(Face face) {
        Polygon polygon = new Polygon(face.getNumPoints());
        for (int i = 0; i <= face.getNumPoints(); i++) {
            polygon.set(i, getPoint(face.getPointId(i)));
        }
        return polygon;
    }

    // Even a part of the line segment s0 is inside the surface of the convex polygon (the boundary line is not regarded as the inside)
    // Returns 1 if it exists, 0 otherwise. If the surface is a concave polygon, the result will be strange, so do not use it.
    public boolean simple_convex_inside(Line line, Face face) {
        //バグがあるようだったが，多分取り除けた
        if (line.getxMax() + 0.5 < face.getxMin()) {
            return false;
        }
        if (line.getxMin() - 0.5 > face.getxMax()) {
            return false;
        }
        if (line.getyMax() + 0.5 < face.getyMin()) {
            return false;
        }
        if (line.getyMin() - 0.5 > face.getyMax()) {
            return false;
        }

        return convex_inside(new LineSegment(getPoint(line.getBegin()), getPoint(line.getEnd())), face);
    }

    private boolean convex_inside(LineSegment s0, Face mn) {
        Polygon tk = makePolygon(mn);
        return tk.convex_inside(s0);
    }

    public boolean convex_inside(double d, Line line, Face face) {
        LineSegment sn = new LineSegment(getPoint(line.getBegin()), getPoint(line.getEnd()));
        return convex_inside(OritaCalc.moveParallel(sn, d), face);
    }

    //Make a line a line segment
    private LineSegment lineToLineSegment(Line line) {
        return new LineSegment(getPoint(line.getBegin()), getPoint(line.getEnd()));
    }

    //Returns 1 if two lines are parallel and partially or wholly overlap, otherwise 0. If one point overlaps, 0 is returned.
    public boolean parallel_overlap(Line line1, Line line2) {
        LineSegment.Intersection skh;
        skh = OritaCalc.determineLineSegmentIntersection(lineToLineSegment(line1), lineToLineSegment(line2));

        return skh.isSegmentOverlapping();
    }

    //面の内部の点を求める
    public Point insidePoint_surface(Face mn) {
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
        return points.size() - 1;
    }   //Get the total number of points

    public Iterable<Line> iterLines() {
        return lines.stream().skip(1).collect(Collectors.toList());
    }

    public Iterable<Face> iterFaces() {
        return faces.stream().skip(1).collect(Collectors.toList());
    }

    public int getNumLines() {
        return lines.size() - 1;
    }   //棒の総数を得る

    public int getNumFaces() {
        return faces.size() - 1;
    }   //面の総数を得る

    public Point_p getPoint(int i) {
        return points.computeIfAbsent(i, j -> new Point_p());
    }   //点を得る       <<<------------tは、スーパークラスのTenのサブクラスTen_Pクラスのオブジェクト。スーパークラスの変数にサブクラスのオブジェクトを代入可能なので、このまま使う。

    public Line getLine(int i) {
        return lines.get(i);
    }   //棒を得る

    public LineSegment getLineSegmentFromLineId(int i) {
        return lineToLineSegment(getLine(i));
    }    //棒のidからSenbunを得る

    public Face getFace(int i) {
        return faces.get(i);
    }   //面を得る

    public void setPoint(int i, Point tn) {
        points.computeIfAbsent(i, j -> new Point_p()).set(tn);
    }                                                        //   <<<------------

    public void addPoint(int index, double x, double y) {
        Point_p p = new Point_p();
        p.set(x, y);
        points.put(index, p);
    }   //点を加える

    public void addLine(int i, int j, LineColor icol) {
        Line newLine = new Line();
        newLine.set(i, j, icol);

        lines.add(newLine);
    }   //棒を加える

    private void searchPointLinking() {
        for (Line line : iterLines()) {
            setPointLinking(line.getBegin(), 0, getPointLinking(line.getBegin(), 0) + 1);
            setPointLinking(line.getBegin(), getPointLinking(line.getBegin(), 0), line.getEnd());
            setPointLinking(line.getEnd(), 0, getPointLinking(line.getEnd(), 0) + 1);
            setPointLinking(line.getEnd(), getPointLinking(line.getEnd(), 0), line.getBegin());
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
                Point_p pointJ = getPoint(j);
                Point_p pointI = getPoint(i);
                Point_p pointK = getPoint(k);
                if (OritaCalc.angle(pointJ, pointI, pointJ, pointK) <= angle) {
                    n = k;
                    angle = OritaCalc.angle(pointJ, pointI, pointJ, pointK);
                }
            }
        }
        return n; //点jに連結している点が点iしかない時は0を返す
    }

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
    //--------------------------------

    //-------------------------------------
    public void FaceOccurrence() throws InterruptedException {
        boolean addNewFace;
        Face tempFace;
        faces.clear();
        faces.add(new Face());
        searchPointLinking();

        ListArray<Integer> map = new ListArray<>(getNumPoints() + 1);

        for (Line line : iterLines()) {
            tempFace = Face_request(line.getBegin(), line.getEnd());
            addNewFace = true;   //　0なら面を追加する。1なら　面を追加しない。
            for (int j : map.get(line.getBegin())) {
                Face face = getFace(j);
                if (face.equals(tempFace)) {
                    addNewFace = false;
                    break;
                }
            }
            if (addNewFace && tempFace.getNumPoints() != 0 && calculateArea(tempFace) > 0.0) {
                addFace(tempFace, map);
            }

            tempFace = Face_request(line.getEnd(), line.getBegin());
            addNewFace = true;   //　0なら面を追加する。1なら　面を追加しない。
            for (int j : map.get(line.getBegin())) {
                Face face = getFace(j);
                if (face.equals(tempFace)) {
                    addNewFace = false;
                    break;
                }
            }
            if (addNewFace && tempFace.getNumPoints() != 0 && calculateArea(tempFace) > 0.0) {
                addFace(tempFace, map);
            }

            // No need for InterruptedException here since this algorithm is now way too
            // fast even for Ryujin.
        }

        System.out.print("全面数　＝　");
        System.out.println(getNumFaces());
        Face_adjacent_create();
        findLineInFaceBorder();
    }

    private void findLineInFaceBorder() throws InterruptedException {
        int[] head = new int[getNumPoints() + 1];

        // 1-based
        List<Integer> list = new ArrayList<>();
        List<Integer> next = new ArrayList<>();
        list.add(null);
        next.add(null);

        // Index all points
        for (int i = 1; i <= getNumFaces(); i++) {
            int count = getFace(i).getNumPoints();
            for (int j = 1; j <= count; j++) {
                int id = getFace(i).getPointId(j);
                next.add(head[id]);
                head[id] = list.size();
                list.add(i);
            }
        }

        // Registration of both sides of line
        for (Line line : iterLines()) {
            int min = getNumFaces() + 1, max = 0;
            int cursor = head[line.getBegin()];
            while (cursor != 0) {
                int id = list.get(cursor);
                Face face = getFace(id);
                if (lineInFaceBorder(face, line)) {
                    if (min > id) min = id;
                    if (max < id) max = id;
                }
                cursor = next.get(cursor);
            }
            if (max > 0) {
                line.setLineInFaceBorder_min(min);
                line.setLineInFaceBorder_max(max);
            }
            if (Thread.interrupted()) throw new InterruptedException();
        }
    }

    //BouやMenの座標の最大値、最小値を求める。kantan_totu_naibu関数にのみ用いる。kantan_totu_naibu関数を使うなら折り畳み推定毎にやる必要あり。
    public void LineFaceMaxMinCoordinate() {
        //Find the maximum and minimum coordinates of Line (this may be better done immediately after Line is added than done here)
        for (Line line : iterLines()) {
            Point_p pointBegin = getPoint(line.getBegin());
            double line_x_max = pointBegin.getX();
            double line_x_min = pointBegin.getX();
            double line_y_max = pointBegin.getY();
            double line_y_min = pointBegin.getY();

            Point_p pointEnd = getPoint(line.getEnd());
            if (line_x_max < pointEnd.getX()) {
                line_x_max = pointEnd.getX();
            }
            if (line_x_min > pointEnd.getX()) {
                line_x_min = pointEnd.getX();
            }
            if (line_y_max < pointEnd.getY()) {
                line_y_max = pointEnd.getY();
            }
            if (line_y_min > pointEnd.getY()) {
                line_y_min = pointEnd.getY();
            }

            line.setxMax(line_x_max);
            line.setxMin(line_x_min);
            line.setyMax(line_y_max);
            line.setyMin(line_y_min);

            faceMaxMinCoordinate();
        }
    }

    private void faceMaxMinCoordinate() {
        //Find the maximum and minimum of Face's coordinates
        for (Face face : iterFaces()) {
            Point_p point1 = getPoint(face.getPointId(1));
            double face_x_max = point1.getX();
            double face_x_min = point1.getX();
            double face_y_max = point1.getY();
            double face_y_min = point1.getY();
            for (int i = 2; i <= face.getNumPoints(); i++) {
                Point_p pointI = getPoint(face.getPointId(i));
                if (face_x_max < pointI.getX()) {
                    face_x_max = pointI.getX();
                }
                if (face_x_min > pointI.getX()) {
                    face_x_min = pointI.getX();
                }
                if (face_y_max < pointI.getY()) {
                    face_y_max = pointI.getY();
                }
                if (face_y_min > pointI.getY()) {
                    face_y_min = pointI.getY();
                }
            }

            face.setxMax(face_x_max);
            face.setxMin(face_x_min);
            face.setyMax(face_y_max);
            face.setyMin(face_y_min);
        }
    }

    //Returns 1 if the boundary of Face [faceId] contains Point [pointId], 0 if pointId does not.
    public boolean pointInFaceBorder(Face face, int pointId) {
        for (int i = 1; i <= face.getNumPoints(); i++) {
            if (pointId == face.getPointId(i)) {
                return true;
            }
        }
        return false;
    }

    //Returns true if line [lineId] is included in the boundary of face [faceId], false if it is not included
    private boolean lineInFaceBorder(Face face, Line line) {
        for (int i = 1; i <= face.getNumPoints() - 1; i++) {
            if ((line.getBegin() == face.getPointId(i)) && (line.getEnd() == face.getPointId(i + 1))) {
                return true;
            }
            if ((line.getEnd() == face.getPointId(i)) && (line.getBegin() == face.getPointId(i + 1))) {
                return true;
            }
        }
        if ((line.getBegin() == face.getPointId(face.getNumPoints())) && (line.getEnd() == face.getPointId(1))) {
            return true;
        }
        return (line.getEnd() == face.getPointId(face.getNumPoints())) && (line.getBegin() == face.getPointId(1));
    }

    private void Face_adjacent_create() throws InterruptedException {
        System.out.println("面となり作成　開始");
        QuadTree qt = new QuadTree(new PointSetFaceAdapter(this));
        for (int im = 1; im <= getNumFaces() - 1; im++) {
            for (int in = im + 1; in <= getNumFaces(); in++) {
                faceAdjacent.set(im, in, 0);
            }
            for (int in : qt.getPotentialCollision(im - 1)) { // qt is 0-based
                in++; // qt is 0-based
                int ima, imb, ina, inb;
                boolean found = false;
                Face faceM = getFace(im);
                for (int iim = 1; iim <= faceM.getNumPoints() && !found; iim++) {
                    ima = faceM.getPointId(iim);
                    if (iim == faceM.getNumPoints()) {
                        imb = faceM.getPointId(1);
                    } else {
                        imb = faceM.getPointId(iim + 1);
                    }

                    Face faceN = getFace(in);
                    for (int iin = 1; iin <= faceN.getNumPoints(); iin++) {
                        ina = faceN.getPointId(iin);

                        if (iin == faceN.getNumPoints()) {
                            inb = faceN.getPointId(1);
                        } else {
                            inb = faceN.getPointId(iin + 1);
                        }

                        if (((ima == ina) && (imb == inb)) || ((ima == inb) && (imb == ina))) {
                            int ib = line_search(ima, imb);
                            faceAdjacent.set(im, in, ib);
                            found = true;
                            break;
                        }
                    }
                }
            }
            if (Thread.interrupted()) throw new InterruptedException();
        }
        System.out.println("面となり作成　終了");
    }

    //Returns the line number containing points t1 and t2
    private int line_search(int t1, int t2) {
        for (int i = 1; i <= getNumLines(); i++) {
            Line line = getLine(i);
            if ((line.getBegin() == t1) && (line.getEnd() == t2)) {
                return i;
            }
            if ((line.getBegin() == t2) && (line.getEnd() == t1)) {
                return i;
            }
        }
        return 0;
    }

    // If Face [im] and Face [ib] are adjacent, return the id number of the bar at the boundary. Returns 0 if not adjacent
    public int getFaceAdjecent(int im, int in) {
        return faceAdjacent.get(im, in);
    }

    public void clearFaceAdjacent() {
        faceAdjacent = null;
        System.gc();
    }

    private void addFace(Face tempFace, ListArray<Integer> map) {
        Face face = new Face(tempFace);

        for (int i = 1; i <= tempFace.getNumPoints(); i++) {
            map.add(tempFace.getPointId(i), getNumFaces() + 1);
        }

        faces.add(face);
    }

    /**
     * Returns the number of the closest point that is closer than a certain distance to the given coordinates. If there is no Ten within a certain distance, 0 is returned.
     */
    public int closestPointId(Point p, double r) {
        int ireturn = 0;
        double rmin = 1000000.0;
        double rtemp;
        for (Map.Entry<Integer, Point_p> entry : points.entrySet()) {
            Point_p p2 = entry.getValue();
            rtemp = OritaCalc.distance(p, p2);
            if (rtemp < r) {
                if (rtemp < rmin) {
                    rmin = rtemp;
                    ireturn = entry.getKey();
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
        for (Point_p p2 : points.values()) {
            rtemp = OritaCalc.distance(p, p2);
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
        for (Point_p entry : points.values()) {
            if (entry.getPointState()) {
                r_int++;
            }
        }
        return r_int;
    }

    public void setPointStateTrue(int i) {
        getPoint(i).setPointStateTrue();
    }

    public void setPointStateFalse(int i) {
        getPoint(i).setPointStateFalse();
    }

    public void setAllPointStateFalse() {
        for (Point_p p : points.values()) {
            p.setPointStateFalse();
        }
    }

    public void changePointState(int i) {
        Point_p point = getPoint(i);
        if (point.getPointState()) {
            point.setPointStateFalse();
        } else {
            point.setPointStateTrue();
        }
    }

    public boolean getPointState(int i) {
        return getPoint(i).getPointState();
    }

    public void statePointMove(Point p) {
        for (Point_p p2 : points.values()) {
            if (p2.getPointState()) {
                p2.set(p);
            }
        }
    }

    public Save getSave() {
        Save save = new Save();

        for (Map.Entry<Integer, Point_p> entry : points.entrySet()) {
            save.addPoint(entry.getKey(), new Point(entry.getValue()));
        }

        return save;
    }

    public void setSave(Save save) {
        for (Map.Entry<Integer, Point> entry : save.getPoints().entrySet()) {
            getPoint(entry.getKey()).set(entry.getValue());
        }
    }
}
