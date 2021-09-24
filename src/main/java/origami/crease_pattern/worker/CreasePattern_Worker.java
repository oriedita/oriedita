package origami.crease_pattern.worker;

import origami.crease_pattern.element.LineColor;
import origami_editor.editor.Save;
import origami_editor.editor.folded_figure.FoldedFigure;
import origami_editor.editor.undo_box.HistoryState;
import origami_editor.graphic2d.averagecoordinates.AverageCoordinates;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Point;
import origami_editor.tools.Camera;
import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.PointSet;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CreasePattern_Worker {
    public Point point_of_referencePlane_ob = new Point();
    //This crease pattern craftsman class has only one PointStore c as a crease pattern.
    //PointSet obtained as a result of folding etc. should be returned to the outside and not held by oneself.
    double r;                   //Criteria for determining the radius of the circles at both ends of the straight line of the basic branch structure and the proximity of the branches to various points
    PointSet pointSet = new PointSet();    //Development view
    HistoryState Ubox = new HistoryState();
    //Definition of variables used in VVVVVVVVVVVV oritatami and oekaki VVVVVVVVVVVVVVVVVVVVVVVVVVVV
    int[] iFacePosition;//Indicates how far a surface is from the reference surface. Enter a value such as 1, next to the reference plane, 2, next to the reference plane, and 3 next to it.
    int referencePlaneId;
    int[] nextFaceId;//The id of the surface (reference surface side) next to a certain surface
    int[] associatedLineId;//The id of the bar between one side and the next side (reference plane side)
    AverageCoordinates[] tnew;//Stores the position of the point when folded
    Camera camera = new Camera();
    Camera cam_front = new Camera();
    Camera cam_rear = new Camera();
    Camera cam_transparent_front = new Camera();
    Camera cam_transparent_rear = new Camera();
    double d_h_k = 10.0;//Judgment distance whether the neighborhood is closer than a certain distance

    public CreasePattern_Worker(double r0) {  //コンストラクタ
        r = r0;
    }

    public void reset() {
        r = 3.0;
        pointSet.reset();

        camera.reset();
        cam_front.reset();
        cam_rear.reset();

        cam_transparent_front.reset();
        cam_transparent_rear.reset();
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

    public int getReferencePlaneId() {
        return referencePlaneId;
    }

    public Point get_point_of_referencePlane_tv() {
        return camera.object2TV(point_of_referencePlane_ob);
    }

    public int setReferencePlaneId(int i) {
        referencePlaneId = i;

        if (referencePlaneId > pointSet.getNumFaces()) {
            referencePlaneId = pointSet.getNumFaces();
        }
        if (referencePlaneId < 1) {
            referencePlaneId = 1;
        }

        point_of_referencePlane_ob = pointSet.insidePoint_surface(referencePlaneId);

        return referencePlaneId;
    }

    /**
     * This is the correspondence when the mouse is pressed in the reference plane specification mode 201503
     */
    public int setReferencePlaneId(Point p0) {//Returns the datum id that is actually valid
        Point p = new Point();
        p.set(camera.TV2object(p0));
        if (pointSet.inside(p) > 0) {
            referencePlaneId = pointSet.inside(p);
            point_of_referencePlane_ob.set(p);
        }//If c.inside(p) = 0, it is not inside any surface, if it is negative, it is on the boundary line, and if it is a positive number, it is inside. If there are multiple applicable surface numbers, the one with the smaller number is returned.
        return referencePlaneId;
    }

    /**
     * Determine if Point p0 is inside the fold-up diagram
     */
    public int isInside(Point p0) {//Returns the face id where p0 is actually
        Point p = new Point();
        p.set(camera.TV2object(p0));
        return pointSet.inside(p);//If c.inside(p) = 0, it is not inside any surface, if it is negative, it is on the boundary line, and if it is a positive number, it is inside. If there are multiple applicable surface numbers, the one with the smaller number is returned.
    }

    /**
     * Determine if Point p0 is inside the fold-up diagram (table)
     */
    public int isInsideFront(Point p0) {//Returns the face id where p0 is actually
        Point p = new Point();
        p.set(cam_front.TV2object(p0));
        return pointSet.inside(p);//If PointSet c.inside (p) = 0, it is not inside any surface, if it is negative, it is on the boundary line, if it is positive, it is inside. If there are multiple applicable surface numbers, the one with the smaller number is returned.
    }

    /**
     * Determine if Point p0 is inside the folded view (back)
     */
    public int isInsideRear(Point p0) {//Returns the face id where p0 is actually
        Point p = new Point();
        p.set(cam_rear.TV2object(p0));
        return pointSet.inside(p);//If PointSet c.inside (p) = 0, it is not inside any surface, if it is negative, it is on the boundary line, if it is positive, it is inside. If there are multiple applicable surface numbers, the one with the smaller number is returned.
    }

    /**
     * Determine if Point p0 is inside the transparent view (table) that is attached to the folded view
     */
    public int isInsideTransparentFront(Point p0) {//実際にp0がある面idを返す
        Point p = new Point();
        p.set(cam_transparent_front.TV2object(p0));
        return pointSet.inside(p);// PointSet c.naibu(p)=0ならどの面の内部にもない、マイナスなら境界線上、正の数なら内部。該当する面番号が複数ある場合は番号の小さいほうが返される。
    }

    // Determine if Point p0 is inside the transparent view (back) that is attached to the folded view
    public int isInsideTransparentRear(Point p0) {//Returns the face id where p0 is actually
        Point p = new Point();
        p.set(cam_transparent_rear.TV2object(p0));
        return pointSet.inside(p);//If PointSet c.inside (p) = 0, it is not inside any surface, if it is negative, it is on the boundary line, if it is positive, it is inside. If there are multiple applicable surface numbers, the one with the smaller number is returned.
    }

    public void set_r(double r0) {
        r = r0;
    }

    public void setCamera(Camera cam0) {
        camera.setCamera(cam0);
    }

    public void setCam_front(Camera cam0) {
        cam_front.setCamera(cam0);
    }

    public void setCam_rear(Camera cam0) {
        cam_rear.setCamera(cam0);
    }

    public void setCam_transparent_front(Camera cam0) {
        cam_transparent_front.setCamera(cam0);
    }

    public void setCam_transparent_rear(Camera cam0) {
        cam_transparent_rear.setCamera(cam0);
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
    public PointSet folding() {//Folding estimate
        PointSet pointSet = new PointSet();    //Development view
        pointSet.configure(this.pointSet.getNumPoints(), this.pointSet.getNumLines(), this.pointSet.getNumFaces());
        pointSet.set(this.pointSet);

        for (int i = 0; i <= this.pointSet.getNumFaces(); i++) {
            nextFaceId[i] = 0;
            associatedLineId[i] = 0;
            iFacePosition[i] = 0;
        }
        //Grasp the positional relationship between the faces in preparation for folding
        System.out.println("折りたたみの準備として面同士の位置関係を把握する");
        iFacePosition[referencePlaneId] = 1;

        int current_face_position = 1;
        int remaining_facesTotal = this.pointSet.getNumFaces() - 1;

        while (remaining_facesTotal > 0) {
            for (int i = 1; i <= this.pointSet.getNumFaces(); i++) {
                if (iFacePosition[i] == current_face_position) {
                    for (int j = 1; j <= this.pointSet.getNumFaces(); j++) {
                        int mth = this.pointSet.Face_adjacent_determine(i, j);
                        if ((mth > 0) && (iFacePosition[j] == 0)) {
                            iFacePosition[j] = current_face_position + 1;
                            nextFaceId[j] = i;
                            associatedLineId[j] = mth;
                        }
                    }
                }
            }

            current_face_position = current_face_position + 1;

            remaining_facesTotal = 0;
            for (int i = 1; i <= this.pointSet.getNumFaces(); i++) {
                if (iFacePosition[i] == 0) {
                    remaining_facesTotal = remaining_facesTotal + 1;
                }
            }

            System.out.println("remaining_facesTotal = " + remaining_facesTotal);
        }

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
        while (idestination_faceId != referencePlaneId) {
            p.set(lineSymmetry_point_determine(associatedLineId[idestination_faceId], p));
            idestination_faceId = nextFaceId[idestination_faceId];
        }
        return p;
    }

    //Folding estimation (What you can do here is a wire diagram that does not consider the overlap of surfaces)
    public PointSet surface_position_request() {//Folding estimate
        PointSet cn = new PointSet();    //展開図
        cn.configure(pointSet.getNumPoints(), pointSet.getNumLines(), pointSet.getNumFaces());
        cn.set(pointSet);

        for (int i = 0; i <= pointSet.getNumFaces(); i++) {
            nextFaceId[i] = 0;
            associatedLineId[i] = 0;
            iFacePosition[i] = 0;
        }
        //Grasp the positional relationship between the faces in preparation for folding
        System.out.println("折りたたみの準備として面同士の位置関係を把握する");
        iFacePosition[referencePlaneId] = 1;

        int current_FacePosition = 1;
        int remaining_facesTotal = pointSet.getNumFaces() - 1;

        while (remaining_facesTotal > 0) {
            for (int i = 1; i <= pointSet.getNumFaces(); i++) {
                if (iFacePosition[i] == current_FacePosition) {
                    for (int j = 1; j <= pointSet.getNumFaces(); j++) {
                        int mth = pointSet.Face_adjacent_determine(i, j);
                        if ((mth > 0) && (iFacePosition[j] == 0)) {
                            iFacePosition[j] = current_FacePosition + 1;
                            nextFaceId[j] = i;
                            associatedLineId[j] = mth;
                        }
                    }
                }
            }

            current_FacePosition = current_FacePosition + 1;

            remaining_facesTotal = 0;
            for (int i = 1; i <= pointSet.getNumFaces(); i++) {
                if (iFacePosition[i] == 0) {
                    remaining_facesTotal = remaining_facesTotal + 1;
                }
            }

            System.out.println("remaining_facesTotal = " + remaining_facesTotal);
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

    public void lineStore2pointStore(LineSegmentSet lineSegmentSet) {
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

    public void mDragged_selectedPoint_move_with_camera(Point ugokasu_maeno_sentaku_point, Point p0, Point p1, FoldedFigure.State ip4) {   //Move the selected point
        Point pa = new Point();
        if (ip4 == FoldedFigure.State.FRONT_0) {
            pa.set(cam_front.TV2object(p0));
        }
        if (ip4 == FoldedFigure.State.BACK_1) {
            pa.set(cam_rear.TV2object(p0));
        }

        Point pb = new Point();
        if (ip4 == FoldedFigure.State.FRONT_0) {
            pb.set(cam_front.TV2object(p1));
        }
        if (ip4 == FoldedFigure.State.BACK_1) {
            pb.set(cam_rear.TV2object(p1));
        }

        Point p_u = new Point();
        p_u.set(ugokasu_maeno_sentaku_point.getX(), ugokasu_maeno_sentaku_point.getY());
        p_u.move(pa.other_Point_position(pb));

        pointSet.statePointMove(p_u);
    }

    //Numerical conversion function when drawing a figure -----------------------------------------------------------------

    public void mReleased_selectedPoint_move_with_camera(Point ugokasu_maeno_sentaku_point, Point p0, Point p1, FoldedFigure.State ip4) {   // Move the selected point
        Point pa = new Point();
        if (ip4 == FoldedFigure.State.FRONT_0) {
            pa.set(cam_front.TV2object(p0));
        }
        if (ip4 == FoldedFigure.State.BACK_1) {
            pa.set(cam_rear.TV2object(p0));
        }

        Point pb = new Point();
        if (ip4 == FoldedFigure.State.FRONT_0) {
            pb.set(cam_front.TV2object(p1));
        }
        if (ip4 == FoldedFigure.State.BACK_1) {
            pb.set(cam_rear.TV2object(p1));
        }


        Point p_u = new Point();
        p_u.set(ugokasu_maeno_sentaku_point.getX(), ugokasu_maeno_sentaku_point.getY());
        p_u.move(pa.other_Point_position(pb));

        pointSet.statePointMove(p_u);
    }

    public int gx(double d) {
        return (int) d;
    }

    //Drawing of development view -----------------------------------------------------------------

    public int gy(double d) {
        return (int) d;
    }

    public void drawing_pointId_with_camera(Graphics g, int i) {    //Draw a dot
        Point tn = new Point();
        tn.set(camera.object2TV(pointSet.getPoint(i)));
        int radius = 7;//radius
        g.setColor(new Color(0, 255, 255, 100));//light blue
        g.fillOval(gx(tn.getX()) - radius, gy(tn.getY()) - radius, 2 * radius, 2 * radius); //円
    }

    public void drawing_pointId_with_camera_green(Graphics g, int i) {    //Draw a dot
        Point tn = new Point();
        tn.set(camera.object2TV(pointSet.getPoint(i)));
        int radius = 15;//半径
        g.setColor(new Color(0, 255, 0, 100));//green
        g.fillOval(gx(tn.getX()) - radius, gy(tn.getY()) - radius, 2 * radius, 2 * radius); //円
    }

    public void drawing_pointId_with_camera(Graphics g, int i, FoldedFigure.State ip4) {
        //Draw a dot
        Point point = new Point();
        point.set(camera.object2TV(pointSet.getPoint(i)));
        int radius = 10;//半径
        g.setColor(new Color(0, 255, 0, 50));//緑色

        switch (ip4) {
            case FRONT_0:
                point.set(cam_front.object2TV(pointSet.getPoint(i)));
                g.fillOval(gx(point.getX()) - radius, gy(point.getY()) - radius, 2 * radius, 2 * radius); //円

                break;
            case BACK_1:
                point.set(cam_rear.object2TV(pointSet.getPoint(i)));
                g.fillOval(gx(point.getX()) - radius, gy(point.getY()) - radius, 2 * radius, 2 * radius); //円

                break;
            case BOTH_2:
            case TRANSPARENT_3:
                point.set(cam_front.object2TV(pointSet.getPoint(i)));
                g.fillOval(gx(point.getX()) - radius, gy(point.getY()) - radius, 2 * radius, 2 * radius); //円

                point.set(cam_rear.object2TV(pointSet.getPoint(i)));
                g.fillOval(gx(point.getX()) - radius, gy(point.getY()) - radius, 2 * radius, 2 * radius); //円

                break;
        }
    }

    public void drawing_with_camera(Graphics g, FoldedFigure.State ip4) {
        LineSegment s_tv = new LineSegment();
        g.setColor(Color.black);
        for (int i = 1; i <= pointSet.getNumLines(); i++) {
            if (pointSet.getColor(i) == LineColor.BLACK_0) {
                g.setColor(Color.black);
            }
            if (pointSet.getColor(i) == LineColor.RED_1) {
                g.setColor(Color.red);
            }
            if (pointSet.getColor(i) == LineColor.BLUE_2) {
                g.setColor(Color.blue);
            }

            switch (ip4) {
                case FRONT_0:
                    s_tv.set(cam_front.object2TV(pointSet.getLineSegmentFromLineId(i)));
                    g.drawLine(gx(s_tv.getAX()), gy(s_tv.getAY()), gx(s_tv.getBX()), gy(s_tv.getBY())); //Straight line

                    break;
                case BACK_1:
                    s_tv.set(cam_rear.object2TV(pointSet.getLineSegmentFromLineId(i)));
                    g.drawLine(gx(s_tv.getAX()), gy(s_tv.getAY()), gx(s_tv.getBX()), gy(s_tv.getBY())); //Straight line

                    break;
                case BOTH_2:
                case TRANSPARENT_3:
                    s_tv.set(cam_front.object2TV(pointSet.getLineSegmentFromLineId(i)));
                    g.drawLine(gx(s_tv.getAX()), gy(s_tv.getAY()), gx(s_tv.getBX()), gy(s_tv.getBY())); //Straight line

                    s_tv.set(cam_rear.object2TV(pointSet.getLineSegmentFromLineId(i)));
                    g.drawLine(gx(s_tv.getAX()), gy(s_tv.getAY()), gx(s_tv.getBX()), gy(s_tv.getBY())); //Straight line

                    break;
            }
        }
    }

    public void drawing_referencePlane_with_camera(Graphics g) {
        //Draw a point inside the surface
        Point point = new Point();
        point.set(camera.object2TV(point_of_referencePlane_ob));

        g.setColor(new Color(200, 50, 255, 90));
        g.fillOval(gx(point.getX()) - 50, gy(point.getY()) - 50, 100, 100); //円

        g.setColor(Color.yellow);
        g.fillOval(gx(point.getX()) - 5, gy(point.getY()) - 5, 10, 10); //円
        g.setColor(Color.black);
        g.drawOval(gx(point.getX()) - 5, gy(point.getY()) - 5, 10, 10); //円
        g.setColor(Color.black);
    }

    public int getSelectedPointsNum() {
        return pointSet.getSelectedPointsNum();
    }

    public void setPointStateTrue(int i) {
        pointSet.setPointStateTrue(i);
    }

    public void setPointStateFalse(int i) {
        pointSet.setPointStateFalse(i);
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

    /**
     * Returns the number of the closest point that is closer than a certain distance to the given coordinates. If there is no Point within a certain distance, 0 is returned.
     */
    public int closestPointId_with_camera(Point p0) {//For development view
        Point p = new Point();
        p.set(camera.TV2object(p0));

        return pointSet.closestPointId(p, d_h_k / camera.getCameraZoomX());
    }

    /**
     * Returns the number of the closest point that is closer than a certain distance to the given coordinates. If there is no Point within a certain distance, 0 is returned.
     */
    public int closestPointId_with_camera(Point p0, FoldedFigure.State ip4) {//折り上がり図用
        Point p = new Point();
        if (ip4 == FoldedFigure.State.FRONT_0) {
            p.set(cam_front.TV2object(p0));
            return pointSet.closestPointId(p, d_h_k / cam_front.getCameraZoomX());
        }
        if (ip4 == FoldedFigure.State.BACK_1) {
            p.set(cam_rear.TV2object(p0));
            return pointSet.closestPointId(p, d_h_k / cam_rear.getCameraZoomX());
        }
        return 0;
    }

    /**
     * Returns the distance of the closest point that is closer than a certain distance to the given coordinates. If there is no Point within a certain distance, 1000000.0 is returned.
     */
    public double closest_point_distance_with_camera(Point p0) {//p0 is the TV coordinate. It is the distance at ob that is returned
        Point p = new Point();
        p.set(camera.TV2object(p0));
        return pointSet.closest_Point_distance(p, d_h_k / camera.getCameraZoomX());
    }

    /**
     * Returns the distance of the closest point that is closer than a certain distance to the given coordinates. If there is no Point within a certain distance, 1000000.0 is returned.
     */
    public double closest_point_distance_with_camera(Point p0, FoldedFigure.State ip4) {//p0 is the TV coordinate. It is the distance at ob that is returned
        Point p = new Point();
        if (ip4 == FoldedFigure.State.FRONT_0) {
            p.set(cam_front.TV2object(p0));
            return pointSet.closest_Point_distance(p, d_h_k / cam_front.getCameraZoomX());
        }
        if (ip4 == FoldedFigure.State.BACK_1) {
            p.set(cam_rear.TV2object(p0));
            return pointSet.closest_Point_distance(p, d_h_k / cam_rear.getCameraZoomX());
        }
        return 1000000.0;
    }

    public Point getPoint(int i) {
        return pointSet.getPoint(i);
    }

    public void setUndoBoxUndoTotal(int i) {
        Ubox.setUndoTotal(i);
    }

    public void record() {
        Ubox.record(getSave());
    }

    public void undo() {
        setSaveForUndoRedo(Ubox.undo());
    }

    public void redo() {
        setSaveForUndoRedo(Ubox.redo());
    }

    public Save getSave() {
        return pointSet.getSave();
    }

    public void setSaveForUndoRedo(Save memo1) {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<undo,redoでのkiroku復元用
        pointSet.setSave(memo1);
    }
}
