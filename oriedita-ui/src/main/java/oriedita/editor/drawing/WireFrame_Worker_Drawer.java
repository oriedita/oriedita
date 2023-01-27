package oriedita.editor.drawing;

import oriedita.editor.Colors;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveProvider;
import oriedita.editor.service.HistoryState;
import oriedita.editor.service.impl.DequeHistoryState;
import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.worker.WireFrame_Worker;
import origami.folding.FoldedFigure;

import java.awt.Color;
import java.awt.Graphics;

public class WireFrame_Worker_Drawer {
    private final WireFrame_Worker wireFrame_worker;
    private final PointSet pointSet;

    private final double d_h_k = 10.0;//Judgment distance whether the neighborhood is closer than a certain distance

    private final Camera camera = new Camera();
    private final Camera cam_front = new Camera();
    private final Camera cam_rear = new Camera();
    private final Camera cam_transparent_front = new Camera();
    private final Camera cam_transparent_rear = new Camera();
    private final HistoryState history = new DequeHistoryState();

    public WireFrame_Worker_Drawer(WireFrame_Worker wireFrame_worker) {
        this.wireFrame_worker = wireFrame_worker;
        this.pointSet = wireFrame_worker.get();
    }

    public Point getStartingFacePoint(int faceId) {
        if (faceId < 1) {
            if (pointSet.inside(new Point(0, 0)) > 0) {
                return pointSet.insidePoint_surface(pointSet.inside(new Point(0, 0)));
            } else {
                return pointSet.insidePoint_surface(1);
            }
        }

        return pointSet.insidePoint_surface(faceId);
    }

    public Point getStartingFacePointTV(int faceId) {
        return camera.object2TV(getStartingFacePoint(faceId));
    }

    public PointSet get() {
        return pointSet;
    }

    public void reset() {
        wireFrame_worker.reset();

        camera.reset();
        cam_front.reset();
        cam_rear.reset();

        cam_transparent_front.reset();
        cam_transparent_rear.reset();
    }

    public void drawing_pointId_with_camera(Graphics g, int i) {    //Draw a dot
        origami.crease_pattern.element.Point tn = new origami.crease_pattern.element.Point();
        tn.set(camera.object2TV(pointSet.getPoint(i)));
        int radius = 7;//radius
        g.setColor(Colors.get(new Color(0, 255, 255, 100)));//light blue
        g.fillOval(gx(tn.getX()) - radius, gy(tn.getY()) - radius, 2 * radius, 2 * radius); //円
    }

    public void drawing_pointId_with_camera_green(Graphics g, int i) {    //Draw a dot
        origami.crease_pattern.element.Point tn = new origami.crease_pattern.element.Point();
        tn.set(camera.object2TV(pointSet.getPoint(i)));
        int radius = 15;//半径
        g.setColor(Colors.get(new Color(0, 255, 0, 100)));//green
        g.fillOval(gx(tn.getX()) - radius, gy(tn.getY()) - radius, 2 * radius, 2 * radius); //円
    }

    public void drawing_pointId_with_camera(Graphics g, int i, FoldedFigure.State ip4) {
        //Draw a dot
        origami.crease_pattern.element.Point point = new origami.crease_pattern.element.Point();
        point.set(camera.object2TV(pointSet.getPoint(i)));
        int radius = 10;//半径
        g.setColor(Colors.get(new Color(0, 255, 0, 50)));//緑色

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
        g.setColor(Colors.get(Color.black));
        for (int i = 1; i <= pointSet.getNumLines(); i++) {
            if (pointSet.getColor(i) == LineColor.BLACK_0) {
                g.setColor(Colors.get(Color.black));
            }
            if (pointSet.getColor(i) == LineColor.RED_1) {
                g.setColor(Colors.get(Color.red));
            }
            if (pointSet.getColor(i) == LineColor.BLUE_2) {
                g.setColor(Colors.get(Color.blue));
            }

            switch (ip4) {
                case FRONT_0:
                    s_tv.set(cam_front.object2TV(pointSet.getLineSegmentFromLineId(i)));
                    g.drawLine(gx(s_tv.determineAX()), gy(s_tv.determineAY()), gx(s_tv.determineBX()), gy(s_tv.determineBY())); //Straight line

                    break;
                case BACK_1:
                    s_tv.set(cam_rear.object2TV(pointSet.getLineSegmentFromLineId(i)));
                    g.drawLine(gx(s_tv.determineAX()), gy(s_tv.determineAY()), gx(s_tv.determineBX()), gy(s_tv.determineBY())); //Straight line

                    break;
                case BOTH_2:
                case TRANSPARENT_3:
                    s_tv.set(cam_front.object2TV(pointSet.getLineSegmentFromLineId(i)));
                    g.drawLine(gx(s_tv.determineAX()), gy(s_tv.determineAY()), gx(s_tv.determineBX()), gy(s_tv.determineBY())); //Straight line

                    s_tv.set(cam_rear.object2TV(pointSet.getLineSegmentFromLineId(i)));
                    g.drawLine(gx(s_tv.determineAX()), gy(s_tv.determineAY()), gx(s_tv.determineBX()), gy(s_tv.determineBY())); //Straight line

                    break;
            }
        }
    }

    public void drawStartingFaceWithCamera(Graphics g, int startingFaceId) {
        //Draw a point inside the surface
        Point point = getStartingFacePointTV(startingFaceId);

        g.setColor(Colors.get(new Color(200, 50, 255, 90)));
        g.fillOval(gx(point.getX()) - 50, gy(point.getY()) - 50, 100, 100); //円

        g.setColor(Colors.get(Color.yellow));
        g.fillOval(gx(point.getX()) - 5, gy(point.getY()) - 5, 10, 10); //円
        g.setColor(Colors.get(Color.black));
        g.drawOval(gx(point.getX()) - 5, gy(point.getY()) - 5, 10, 10); //円
        g.setColor(Colors.get(Color.black));
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

    public void record() {
        history.record(getSave());
    }

    public void undo() {
        setSaveForUndoRedo(history.undo());
    }

    public void redo() {
        setSaveForUndoRedo(history.redo());
    }

    public Save getSave() {
        Save save = SaveProvider.createInstance();
        pointSet.getSave(save);
        return save;
    }

    public void setSaveForUndoRedo(Save memo1) {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<undo,redoでのkiroku復元用
        pointSet.setSave(memo1);
    }

    public Camera getCamera() {
        return camera;
    }
}
