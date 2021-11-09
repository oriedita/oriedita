package origami.crease_pattern.worker;

import origami.crease_pattern.FoldingException;
import origami.crease_pattern.element.LineColor;
import origami.folding.util.AverageCoordinates;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Point;
import origami.data.ListArray;
import origami.data.quadTree.QuadTree;
import origami.data.quadTree.adapter.PointSetFaceAdapter;
import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.PointSet;

import java.util.*;
import java.util.List;

public class WireFrame_Worker {
    //This crease pattern craftsman class has only one PointStore c as a crease pattern.
    //PointSet obtained as a result of folding etc. should be returned to the outside and not held by oneself.
    double r;                   //Criteria for determining the radius of the circles at both ends of the straight line of the basic branch structure and the proximity of the branches to various points
    PointSet pointSet = new PointSet();    //Development view
    //Definition of variables used in VVVVVVVVVVVV oritatami and oekaki VVVVVVVVVVVVVVVVVVVVVVVVVVVV
    int[] iFacePosition;//Indicates how far a surface is from the reference surface. Enter a value such as 1, next to the reference plane, 2, next to the reference plane, and 3 next to it.
    int startingFaceId;
    int[] nextFaceId;//The id of the surface (reference surface side) next to a certain surface
    int[] associatedLineId;//The id of the bar between one side and the next side (reference plane side)
    AverageCoordinates[] tnew;//Stores the position of the point when folded

    public WireFrame_Worker(double r0) {  //コンストラクタ
        r = r0;
    }

    public void reset() {
        r = 3.0;
        pointSet.reset();
    }

    private void configure(int numPoints, int numLines, int numFaces) {
        tnew = new AverageCoordinates[numPoints + 1];
        for (int i = 0; i <= numPoints; i++) {
            tnew[i] = new AverageCoordinates();
        }
        iFacePosition = new int[numFaces + 1];
        nextFaceId = new int[numFaces + 1];         //The id of the surface (reference surface side) next to a certain surface
        associatedLineId = new int[numFaces + 1];         //The id of the bar between one surface and the next surface (reference surface side)
    }

    public int getStartingFaceId() {
        return startingFaceId;
    }

    public int setStartingFaceId(int i) {
        startingFaceId = i;

        if (startingFaceId > pointSet.getNumFaces()) {
            startingFaceId = pointSet.getNumFaces();
        }
        if (startingFaceId < 1) {
            startingFaceId = 1;
        }

        return startingFaceId;
    }

    /**
     * This is the correspondence when the mouse is pressed in the reference plane specification mode 201503
     */
    public int setStartingFaceId(Point p0) {//Returns the datum id that is actually valid
        if (pointSet.inside(p0) > 0) {
            startingFaceId = pointSet.inside(p0);
        }//If c.inside(p) = 0, it is not inside any surface, if it is negative, it is on the boundary line, and if it is a positive number, it is inside. If there are multiple applicable surface numbers, the one with the smaller number is returned.
        return startingFaceId;
    }


    /**
     * Get the total number of lines in the point set
     */
    public int getNumLines() {
        return pointSet.getNumLines();
    }

    /**
     * Obtain the color of the line of the point set (when the point set is treated as a development view, this color represents a mountain valley).
     */
    public LineColor getColor(int i) {
        return pointSet.getColor(i);
    }

    public int getIFacePosition(int i) {
        return iFacePosition[i];
    }

    /**
     * Folding estimation (What you can do here is a wire diagram that does not consider the overlap of surfaces)
     */
    public PointSet folding() throws InterruptedException, FoldingException {//Folding estimate
        // The code that was previously here is identical to getFacePositions
        PointSet pointSet = getFacePositions();
  
        System.out.println("折ったときの点の位置を求める。");
        // Find the position of the point when folded.
        // If the point it is included in the face im
        // Find where to move when the crease pattern is folded by moving the face im.

        System.out.println("折ったときの点の位置を求める（開始）");
        for (int it = 1; it <= this.pointSet.getNumPoints(); it++) {
            tnew[it].reset();
            for (int im = 1; im <= this.pointSet.getNumFaces(); im++) {
                if (this.pointSet.pointInFaceBorder(im, it)) {//c.Ten_moti_hantei returns 1 if the boundary of Face [im] contains Point [it], 0 if it does not.
                    tnew[it].addPoint(fold_movement(it, im));
                    pointSet.setPoint(it, tnew[it].getAveragePoint());
                }
            }
        }
        System.out.println("折ったときの点の位置を求めた（終了）");

        return pointSet;
    }

    private Point fold_movement(int it, int im) { //A function that finds the position of the destination when the point it is folded as a member of the surface im
        Point p = new Point();
        p.set(pointSet.getPoint(it));
        int idestination_faceId = im;//The id number of the first face. From now on, we will follow the planes adjacent to the reference plane.
        while (idestination_faceId != startingFaceId) {
            p.set(lineSymmetry_point_determine(associatedLineId[idestination_faceId], p));
            idestination_faceId = nextFaceId[idestination_faceId];
        }
        return p;
    }

    //Folding estimation (What you can do here is a wire diagram that does not consider the overlap of surfaces)
    public PointSet getFacePositions() throws InterruptedException {//Folding estimate
        PointSet cn = new PointSet();    //展開図
        cn.configure(pointSet.getNumPoints(), pointSet.getNumLines(), pointSet.getNumFaces());
        cn.set(pointSet);

        ListArray map = pointSet.getPointToLineMap();
        QuadTree qt = new QuadTree(new PointSetFaceAdapter(pointSet));

        for (int i = 0; i <= pointSet.getNumFaces(); i++) {
            nextFaceId[i] = 0;
            associatedLineId[i] = 0;
            iFacePosition[i] = 0;
        }
        //Grasp the positional relationship between the faces in preparation for folding
        System.out.println("折りたたみの準備として面同士の位置関係を把握する");
        iFacePosition[startingFaceId] = 1;

        int depth = 1;
        int remaining_facesTotal = pointSet.getNumFaces() - 1;

        // Tsai: I'm not sure if ordering matters, so I just play safe here.
        SortedSet<Integer> currentRound = new TreeSet<>();
        currentRound.add(startingFaceId);

        while (remaining_facesTotal > 0) {
            SortedSet<Integer> nextRound = new TreeSet<>();
            for (int i : currentRound) {
                for (int j : qt.getPotentialCollision(i - 1, -1)) {
                    if (iFacePosition[++j] != 0) continue;
                    int mth = pointSet.findAdjacentLine(i, j, map);
                    if (mth > 0) {
                        nextRound.add(j);
                        iFacePosition[j] = depth + 1;
                        nextFaceId[j] = i;
                        associatedLineId[j] = mth;
                        remaining_facesTotal--;
                    }
                }
            }
            currentRound = nextRound;
            depth++;

            if (Thread.interrupted()) throw new InterruptedException();
        }

        return cn;
    }

    private Point lineSymmetry_point_determine(int lineId, Point point) {//Given the id of the bar and any point, returns the point that is axisymmetric of the given point with respect to the corresponding bar.
        return OritaCalc.findLineSymmetryPoint(pointSet.getBeginPointFromLineId(lineId), pointSet.getEndPointFromLineId(lineId), point);
    }

    public int getPointsTotal() {
        return pointSet.getNumPoints();
    }

    public void set(PointSet ts) {
        configure(ts.getNumPoints(), ts.getNumLines(), ts.getNumFaces());
        pointSet.configure(ts.getNumPoints(), ts.getNumLines(), ts.getNumFaces());
        pointSet.set(ts);
    }

    public PointSet get() {
        return pointSet;
    }

    public LineSegmentSet getLineStore() {
        //Instantiation of basic branch structure
        return new LineSegmentSet(pointSet);
    }

    public void setLineSegmentSet(LineSegmentSet lineSegmentSet) throws InterruptedException {
        Point ti;
        reset();

        //First, define a point in PointSet.
        System.out.println("線分集合->点集合：点集合内で点の定義");
        boolean flag1;
        double x, y;

        double[] addPointX = new double[lineSegmentSet.getNumLineSegments() + 1 + 1]; // If you do not add +1 you will get an error when the number of faces is 1.
        double[] addPointY = new double[lineSegmentSet.getNumLineSegments() + 1 + 1]; // If you do not add +1 you will get an error when the number of faces is 1.
        int addPointNum = 0;

        for (int i = 0; i < lineSegmentSet.getNumLineSegments(); i++) {
            flag1 = false;
            ti = lineSegmentSet.getA(i);
            x = ti.getX();
            y = ti.getY();

            for (int j = 1; j <= addPointNum; j++) {
                if (OritaCalc.equal(ti, new Point(addPointX[j], addPointY[j]))) {
                    flag1 = true;
                }
            }

            if (!flag1) {
                addPointNum = addPointNum + 1;
                addPointX[addPointNum] = x;
                addPointY[addPointNum] = y;
            }
            flag1 = false;
            ti = lineSegmentSet.getB(i);
            x = ti.getX();
            y = ti.getY();

            for (int j = 1; j <= addPointNum; j++) {
                if (OritaCalc.equal(ti, new Point(addPointX[j], addPointY[j]))) {
                    flag1 = true;
                }
            }

            if (!flag1) {
                addPointNum = addPointNum + 1;
                addPointX[addPointNum] = x;
                addPointY[addPointNum] = y;
            }
        }

        System.out.print("点の全数　addPointNum＝　");
        System.out.println(addPointNum);

        configure(addPointNum, lineSegmentSet.getNumLineSegments(), lineSegmentSet.getNumLineSegments() - addPointNum + 100);//<< It may be better to have more room here to ensure redundancy. Consideration required 20150315
        pointSet.configure(addPointNum, lineSegmentSet.getNumLineSegments(), lineSegmentSet.getNumLineSegments() - addPointNum + 100);//<< It may be better to have more room here to ensure redundancy. Consideration required 20150315

        for (int i = 1; i <= addPointNum; i++) {
            pointSet.addPoint(addPointX[i], addPointY[i]);
        }

        //Next, define a bar in PointSet.
        System.out.println("線分集合->点集合：点集合内で棒の定義");

        List<Integer> ika2ic = new ArrayList<>();
        List<Integer> ikb2ic = new ArrayList<>();
        for (int n = 0; n < lineSegmentSet.getNumLineSegments(); n++) {
            for (int i = 1; i <= pointSet.getNumPoints(); i++) {
                if (OritaCalc.equal(lineSegmentSet.getA(n), pointSet.getPoint(i))) {
                    ika2ic.add(i);
                    break;
                }
            }
            for (int i = 1; i <= pointSet.getNumPoints(); i++) {
                if (OritaCalc.equal(lineSegmentSet.getB(n), pointSet.getPoint(i))) {
                    ikb2ic.add(i);
                    break;
                }
            }
        }

        for (int n = 0; n < lineSegmentSet.getNumLineSegments(); n++) {
            pointSet.addLine(ika2ic.get(n), ikb2ic.get(n), lineSegmentSet.getColor(n));
        }

        System.out.print("棒の全数　＝　");
        System.out.println(pointSet.getNumLines());
        //
        System.out.println("線分集合->点集合：点集合内で面を発生　開始");
        //Then generate a surface within PointSet.
        pointSet.FaceOccurrence();

        System.out.println("線分集合->点集合：点集合内で面を発生　終了");
    }

    /**
     * Returns the faceId with the smaller faceId of the faces containing the line lineId as the boundary (there are up to two faces). Returns 0 if there is no face containing the line as the boundary
     */
    public int lineInFaceBorder_min_request(int lineId) {
        return pointSet.lineInFaceBorder_min_lookup(lineId);
    }

    /**
     * Returns the faceId with the larger faceId among the faces containing the line lineId as the boundary (there are two faces at the maximum). Returns 0 if there is no face containing the line as the boundary
     */
    public int lineInFaceBorder_max_request(int lineId) {
        return pointSet.lineInFaceBorder_max_lookup(lineId);
    }



    public int getSelectedPointsNum() {
        return pointSet.getSelectedPointsNum();
    }

    public void setPointStateTrue(int i) {
        pointSet.setPointStateTrue(i);
    }

    public void setAllPointStateFalse() {
        pointSet.setAllPointStateFalse();
    }

    public void changePointState(int i) {
        pointSet.changePointState(i);
    }

    /**
     * Get whether the i-th point is selected as 0 or 1.
     */
    public boolean getPointState(int i) {
        return pointSet.getPointState(i);
    }

    public Point getPoint(int i) {
        return pointSet.getPoint(i);
    }
}
