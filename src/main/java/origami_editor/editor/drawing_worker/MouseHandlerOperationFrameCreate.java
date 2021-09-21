package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;
import origami_editor.editor.MouseMode;

public class MouseHandlerOperationFrameCreate extends BaseMouseHandler{

    public MouseHandlerOperationFrameCreate(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.OPERATION_FRAME_CREATE_61;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        if (d.gridInputAssist) {
            d.line_candidate[1].setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);

            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            d.i_candidate_stage = 1;
            d.closest_point.set(d.getClosestPoint(p));

            if (p.distance(d.closest_point) < d.selectionDistance) {
                d.line_candidate[1].set(d.closest_point, d.closest_point);
            } else {
                d.line_candidate[1].set(p, p);
            }

            d.line_candidate[1].setColor(LineColor.GREEN_6);
        }
    }

    //マウス操作(mouseMode==61　長方形内選択でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        Point p_new = new Point();
        Point p_ob1 = new Point();
        p_ob1.set(d.camera.TV2object(d.operationFrame_p1));
        Point p_ob2 = new Point();
        p_ob2.set(d.camera.TV2object(d.operationFrame_p2));
        Point p_ob3 = new Point();
        p_ob3.set(d.camera.TV2object(d.operationFrame_p3));
        Point p_ob4 = new Point();
        p_ob4.set(d.camera.TV2object(d.operationFrame_p4));

        double distance_min = 100000.0;

        d.operationFrameMode = DrawingWorker.OperationFrameMode.NONE_0;
        if (d.i_drawing_stage == 0) {
            d.operationFrameMode = DrawingWorker.OperationFrameMode.CREATE_1;
        }
        if (d.i_drawing_stage == 4) {
            if (d.operationFrameBox.inside(p0) == Polygon.Intersection.OUTSIDE) {
                d.operationFrameMode = DrawingWorker.OperationFrameMode.CREATE_1;
            } else {
                d.operationFrameMode = DrawingWorker.OperationFrameMode.MOVE_BOX_4;
            }


            distance_min = OritaCalc.min(OritaCalc.distance_lineSegment(p, p_ob1, p_ob2), OritaCalc.distance_lineSegment(p, p_ob2, p_ob3), OritaCalc.distance_lineSegment(p, p_ob3, p_ob4), OritaCalc.distance_lineSegment(p, p_ob4, p_ob1));
            if (distance_min < d.selectionDistance) {
                d.operationFrameMode = DrawingWorker.OperationFrameMode.MOVE_SIDES_3;
            }


            if (p.distance(p_ob1) < d.selectionDistance) {
                p_new.set(d.operationFrame_p1);
                d.operationFrame_p1.set(d.operationFrame_p3);
                d.operationFrame_p3.set(p_new);
                d.operationFrameMode = DrawingWorker.OperationFrameMode.MOVE_POINTS_2;
            }
            if (p.distance(p_ob2) < d.selectionDistance) {
                p_new.set(d.operationFrame_p2);
                d.operationFrame_p2.set(d.operationFrame_p1);
                d.operationFrame_p1.set(d.operationFrame_p4);
                d.operationFrame_p4.set(d.operationFrame_p3);
                d.operationFrame_p3.set(p_new);
                d.operationFrameMode = DrawingWorker.OperationFrameMode.MOVE_POINTS_2;
            }
            if (p.distance(p_ob3) < d.selectionDistance) {
                p_new.set(d.operationFrame_p3);
                d.operationFrame_p1.set(d.operationFrame_p1);
                d.operationFrame_p3.set(p_new);
                d.operationFrameMode = DrawingWorker.OperationFrameMode.MOVE_POINTS_2;
            }
            if (p.distance(p_ob4) < d.selectionDistance) {
                p_new.set(d.operationFrame_p4);
                d.operationFrame_p4.set(d.operationFrame_p1);
                d.operationFrame_p1.set(d.operationFrame_p2);
                d.operationFrame_p2.set(d.operationFrame_p3);
                d.operationFrame_p3.set(p_new);
                d.operationFrameMode = DrawingWorker.OperationFrameMode.MOVE_POINTS_2;
            }

        }

        if (d.operationFrameMode == DrawingWorker.OperationFrameMode.MOVE_SIDES_3) {
            while (OritaCalc.distance_lineSegment(p, p_ob1, p_ob2) != distance_min) {
                p_new.set(d.operationFrame_p1);
                d.operationFrame_p1.set(d.operationFrame_p2);
                d.operationFrame_p2.set(d.operationFrame_p3);
                d.operationFrame_p3.set(d.operationFrame_p4);
                d.operationFrame_p4.set(p_new);
                p_new.set(p_ob1);
                p_ob1.set(p_ob2);
                p_ob2.set(p_ob3);
                p_ob3.set(p_ob4);
                p_ob4.set(p_new);
            }

        }

        if (d.operationFrameMode == DrawingWorker.OperationFrameMode.CREATE_1) {
            d.i_drawing_stage = 4;

            p_new.set(p);

            d.closest_point.set(d.getClosestPoint(p));

            if (p.distance(d.closest_point) < d.selectionDistance) {
                p_new.set(d.closest_point);

            }

            d.operationFrame_p1.set(d.camera.object2TV(p_new));
            d.operationFrame_p2.set(d.camera.object2TV(p_new));
            d.operationFrame_p3.set(d.camera.object2TV(p_new));
            d.operationFrame_p4.set(d.camera.object2TV(p_new));
        }
    }

    //マウス操作(mouseMode==61　長方形内選択でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {

        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        if (d.operationFrameMode == DrawingWorker.OperationFrameMode.MOVE_POINTS_2) {
            d.operationFrameMode = DrawingWorker.OperationFrameMode.CREATE_1;
        }

        Point p_new = new Point();

        if (!d.gridInputAssist) {
            p_new.set(p);
        }

        if (d.gridInputAssist) {
            d.closest_point.set(d.getClosestPoint(p));
            d.i_candidate_stage = 1;
            if (p.distance(d.closest_point) < d.selectionDistance) {
                d.line_candidate[1].set(d.closest_point, d.closest_point);
            } else {
                d.line_candidate[1].set(p, p);
            }
            d.line_candidate[1].setColor(LineColor.GREEN_6);

            p_new.set(d.line_candidate[1].getA());
        }


        if (d.operationFrameMode == DrawingWorker.OperationFrameMode.MOVE_SIDES_3) {
            if (
                    (d.operationFrame_p1.getX() - d.operationFrame_p2.getX()) * (d.operationFrame_p1.getX() - d.operationFrame_p2.getX())
                            <
                            (d.operationFrame_p1.getY() - d.operationFrame_p2.getY()) * (d.operationFrame_p1.getY() - d.operationFrame_p2.getY())
            ) {
                d.operationFrame_p1.setX(d.camera.object2TV(p_new).getX());
                d.operationFrame_p2.setX(d.camera.object2TV(p_new).getX());
            }

            if (
                    (d.operationFrame_p1.getX() - d.operationFrame_p2.getX()) * (d.operationFrame_p1.getX() - d.operationFrame_p2.getX())
                            >
                            (d.operationFrame_p1.getY() - d.operationFrame_p2.getY()) * (d.operationFrame_p1.getY() - d.operationFrame_p2.getY())
            ) {
                d.operationFrame_p1.setY(d.camera.object2TV(p_new).getY());
                d.operationFrame_p2.setY(d.camera.object2TV(p_new).getY());
            }

        }


        if (d.operationFrameMode == DrawingWorker.OperationFrameMode.CREATE_1) {
            d.operationFrame_p3.set(d.camera.object2TV(p_new));
            d.operationFrame_p2.set(d.operationFrame_p1.getX(), d.operationFrame_p3.getY());
            d.operationFrame_p4.set(d.operationFrame_p3.getX(), d.operationFrame_p1.getY());
        }
    }

//--------------------

    //マウス操作(mouseMode==61 長方形内選択　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {

        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        Point p_new = new Point();
        p_new.set(p);

        d.closest_point.set(d.getClosestPoint(p));
        if (p.distance(d.closest_point) <= d.selectionDistance) {
            p_new.set(d.closest_point);/*line_step[1].seta(moyori_ten);*/
        }

        if (d.operationFrameMode == DrawingWorker.OperationFrameMode.MOVE_SIDES_3) {
            if (
                    (d.operationFrame_p1.getX() - d.operationFrame_p2.getX()) * (d.operationFrame_p1.getX() - d.operationFrame_p2.getX())
                            <
                            (d.operationFrame_p1.getY() - d.operationFrame_p2.getY()) * (d.operationFrame_p1.getY() - d.operationFrame_p2.getY())
            ) {
                d.operationFrame_p1.setX(d.camera.object2TV(p_new).getX());
                d.operationFrame_p2.setX(d.camera.object2TV(p_new).getX());
            }

            if (
                    (d.operationFrame_p1.getX() - d.operationFrame_p2.getX()) * (d.operationFrame_p1.getX() - d.operationFrame_p2.getX())
                            >
                            (d.operationFrame_p1.getY() - d.operationFrame_p2.getY()) * (d.operationFrame_p1.getY() - d.operationFrame_p2.getY())
            ) {
                d.operationFrame_p1.setY(d.camera.object2TV(p_new).getY());
                d.operationFrame_p2.setY(d.camera.object2TV(p_new).getY());
            }

        }

        if (d.operationFrameMode == DrawingWorker.OperationFrameMode.CREATE_1) {
            d.operationFrame_p3.set(d.camera.object2TV(p_new));
            d.operationFrame_p2.set(d.operationFrame_p1.getX(), d.operationFrame_p3.getY());
            d.operationFrame_p4.set(d.operationFrame_p3.getX(), d.operationFrame_p1.getY());
        }

        d.operationFrameBox.set(1, d.operationFrame_p1);
        d.operationFrameBox.set(2, d.operationFrame_p2);
        d.operationFrameBox.set(3, d.operationFrame_p3);
        d.operationFrameBox.set(4, d.operationFrame_p4);

        if (d.operationFrameBox.calculateArea() * d.operationFrameBox.calculateArea() < 1.0) {
            d.i_drawing_stage = 0;
        }
    }
}
