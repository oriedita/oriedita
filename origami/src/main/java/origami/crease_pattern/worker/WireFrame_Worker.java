package origami.crease_pattern.worker;

import org.tinylog.Logger;
import origami.crease_pattern.FoldingException;
import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;
import origami.data.ListArray;
import origami.data.quadTree.QuadTree;
import origami.data.quadTree.adapter.InitialAdapter;
import origami.data.quadTree.adapter.PointSetFaceAdapter;
import origami.data.quadTree.adapter.PointSetPointAdapter;
import origami.data.quadTree.collector.PointCollector;
import origami.folding.util.AverageCoordinates;

import java.util.SortedSet;
import java.util.TreeSet;

public class WireFrame_Worker {
    //This crease pattern craftsman class has only one PointStore c as a crease pattern.
    //PointSet obtained as a result of folding etc. should be returned to the outside and not held by oneself.
    double r;                   //Criteria for determining the radius of the circles at both ends of the straight line of the basic branch structure and the proximity of the branches to various points
    PointSet pointSet = new PointSet();    //Development view
    //Definition of variables used in VVVVVVVVVVVV oritatami and oekaki VVVVVVVVVVVVVVVVVVVVVVVVVVVV
    int[] facePosition;//Indicates how far a surface is from the reference surface. Enter a value such as 1, next to the reference plane, 2, next to the reference plane, and 3 next to it.
    int startingFaceId = -1;
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
        facePosition = new int[numFaces + 1];
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
            startingFaceId = pointSet.inside(new Point(0, 0));
            if (startingFaceId < 1) {
                startingFaceId = 1;
            }
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
        return facePosition[i];
    }

    /**
     * Folding estimation (What you can do here is a wire diagram that does not consider the overlap of surfaces)
     */
    public PointSet folding() throws InterruptedException, FoldingException {//Folding estimate
        // The code that was previously here is identical to getFacePositions
        PointSet pointSet = getFacePositions();

        Logger.info("折ったときの点の位置を求める。");
        // Find the position of the point when folded.
        // If the point it is included in the face im
        // Find where to move when the crease pattern is folded by moving the face im.

        QuadTree qt = new QuadTree(new PointSetFaceAdapter(pointSet));
        Logger.info("折ったときの点の位置を求める（開始）");
        for (int it = 1; it <= this.pointSet.getNumPoints(); it++) {
            tnew[it].reset();
            for (int im : qt.collect(new PointCollector(pointSet.getPoint(it)))) {
                if (pointSet.pointInFaceBorder(im, it)) {//c.Ten_moti_hantei returns 1 if the boundary of Face [im] contains Point [it], 0 if it does not.
                    tnew[it].addPoint(fold_movement(it, im));
                    pointSet.setPoint(it, tnew[it].getAveragePoint());
                }
            }
        }
        Logger.info("折ったときの点の位置を求めた（終了）");

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
            facePosition[i] = 0;
        }
        //Grasp the positional relationship between the faces in preparation for folding
        Logger.info("折りたたみの準備として面同士の位置関係を把握する");
        facePosition[startingFaceId] = 1;

        int depth = 1;
        int remaining_facesTotal = pointSet.getNumFaces() - 1;

        // Tsai: I'm not sure if ordering matters, so I just play safe here.
        SortedSet<Integer> currentRound = new TreeSet<>();
        currentRound.add(startingFaceId);

        while (remaining_facesTotal > 0) {
            SortedSet<Integer> nextRound = new TreeSet<>();
            for (int i : currentRound) {
                for (int j : qt.getPotentialCollision(i, 0)) {
                    if (facePosition[j] != 0) continue;
                    int mth = pointSet.findAdjacentLine(i, j, map);
                    if (mth > 0) {
                        nextRound.add(j);
                        facePosition[j] = depth + 1;
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

    public void setLineSegmentSetWithoutFaceOccurence(LineSegmentSet lineSegmentSet) throws InterruptedException {
        reset();
        definePointSet(lineSegmentSet);
        defineLines(lineSegmentSet);
    }

    public void setLineSegmentSet(LineSegmentSet lineSegmentSet) throws InterruptedException {
        reset();

        //First, define the points in PointSet.
        definePointSet(lineSegmentSet);

        //Next, define the lines in PointSet.
        defineLines(lineSegmentSet);

        //Then generate a surface within PointSet.
        pointSet.FaceOccurrence();
    }

    private void definePointSet(LineSegmentSet lineSegmentSet) throws InterruptedException {
        Logger.info("Line set->Point set: Define points in point set");
        boolean found;
        Point ti;

        InitialAdapter adapter = new InitialAdapter(lineSegmentSet, lineSegmentSet.getNumLineSegments()*2);
        QuadTree qt = new QuadTree(adapter);

        for (int i = 0; i < lineSegmentSet.getNumLineSegments(); i++) {
            found = false;
            ti = lineSegmentSet.getA(i);
            for (int j : qt.collect(new PointCollector(ti))) {
                if (OritaCalc.equal(ti, adapter.get(j))) found = true;
            }
            if (!found) {
                adapter.add(ti);
                qt.grow(1);
            }

            found = false;
            ti = lineSegmentSet.getB(i);
            for (int j : qt.collect(new PointCollector(ti))) {
                if (OritaCalc.equal(ti, adapter.get(j))) found = true;
            }
            if (!found) {
                adapter.add(ti);
                qt.grow(1);
            }

            if (Thread.interrupted()) throw new InterruptedException();
        }

        int numPoints = adapter.getCount();
        Logger.info("点の全数　addPointNum＝　");
        Logger.info(numPoints);

        int numLines = lineSegmentSet.getNumLineSegments();

        // Euler's formula says F - E + V = 1 (for bounded faces)
        int supposedNumFaces = numLines - numPoints + 1;
        /**
         * However the numbers could be off due to rounding errors (see comments in
         * PointSet), so we add a bit more just in case that happens. The "max" thing
         * here is partly for compatibility with the old tests, but also for ensuring
         * that the extra room is enough.
         */
        int estimatedNumFaces = supposedNumFaces + Math.max(supposedNumFaces / 100, 99);

        configure(numPoints, numLines, estimatedNumFaces);
        pointSet.configure(numPoints, numLines, estimatedNumFaces);

        for (int i = 0; i < numPoints; i++) {
            pointSet.addPoint(adapter.get(i));
        }
    }

    private void defineLines(LineSegmentSet lineSegmentSet) throws InterruptedException {
        Logger.info("Line set->Point set: Defining a line in the point set");

        QuadTree qt = new QuadTree(new PointSetPointAdapter(pointSet));
        for (int n = 0; n < lineSegmentSet.getNumLineSegments(); n++) {
            int start = 0, end = 0;
            for (int i : qt.collect(new PointCollector(lineSegmentSet.getA(n)))) {
                if (OritaCalc.equal(lineSegmentSet.getA(n), pointSet.getPoint(i))) {
                    start = i;
                    break;
                }
            }
            for (int i : qt.collect(new PointCollector(lineSegmentSet.getB(n)))) {
                if (OritaCalc.equal(lineSegmentSet.getB(n), pointSet.getPoint(i))) {
                    end = i;
                    break;
                }
            }
            pointSet.addLine(start, end, lineSegmentSet.getColor(n));

            if (Thread.interrupted()) throw new InterruptedException();
        }

        Logger.info("棒の全数　＝ {}", pointSet.getNumLines());
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
