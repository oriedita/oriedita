package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.tinylog.Logger;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.canvas.OperationFrame;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Handles(MouseMode.OPERATION_FRAME_CREATE_61)
public class MouseHandlerOperationFrameCreate extends BaseMouseHandler {
    CreasePattern_Worker.OperationFrameMode operationFrameMode;
    Point lastMousePos;

    @Inject
    public MouseHandlerOperationFrameCreate() {
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        if (d.getGridInputAssist()) {
            d.getLineCandidate().clear();

            Point p = d.getCamera().TV2object(p0);
            Point closest_point = d.getClosestPoint(p);

            if (p.distance(closest_point) < d.getSelectionDistance()) {
                d.getLineCandidate().add(new LineSegment(closest_point, closest_point, LineColor.GREEN_6));
            } else {
                d.getLineCandidate().add(new LineSegment(p, p, LineColor.GREEN_6));
            }

            d.getLineCandidate().get(0).setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
        }
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        OperationFrame frame = d.getOperationFrame();
        Camera cam = d.getCamera();
        List<LineSegment> ls = new ArrayList<>();
        ls.add(new LineSegment(frame.getP1(), frame.getP2()));
        ls.add(new LineSegment(frame.getP2(), frame.getP3()));
        ls.add(new LineSegment(frame.getP3(), frame.getP4()));
        ls.add(new LineSegment(frame.getP4(), frame.getP1()));
        for (LineSegment l : ls) {
            l = l.withColor(LineColor.GREEN_6);
            l.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
            DrawingUtil.drawLineStep(g2,
                    cam.TV2object(l),
                    camera, settings.getLineWidth(), d.getGridInputAssist());
        }
    }

    //マウス操作(mouseMode==61　長方形内選択でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Camera cam = d.getCamera();
        Point p = cam.TV2object(p0);
        OperationFrame frame = d.getOperationFrame();
        Point p_ob1 = cam.TV2object(frame.getP1());
        Point p_ob2 = cam.TV2object(frame.getP2());
        Point p_ob3 = cam.TV2object(frame.getP3());
        Point p_ob4 = cam.TV2object(frame.getP4());
        lastMousePos = p;
        Logger.info(p_ob1);

        double distance_min = 100000.0;
        operationFrameMode = CreasePattern_Worker.OperationFrameMode.NONE_0;
        if (!frame.isActive()) {
            operationFrameMode = CreasePattern_Worker.OperationFrameMode.CREATE_1;
        }
        Point p_new;
        if (frame.isActive()) {
            distance_min = OritaCalc.min(
                    OritaCalc.determineLineSegmentDistance(p, p_ob1, p_ob2),
                    OritaCalc.determineLineSegmentDistance(p, p_ob2, p_ob3),
                    OritaCalc.determineLineSegmentDistance(p, p_ob3, p_ob4),
                    OritaCalc.determineLineSegmentDistance(p, p_ob4, p_ob1));
            if (distance_min < d.getSelectionDistance()) {
                operationFrameMode = CreasePattern_Worker.OperationFrameMode.MOVE_SIDES_3;
            } else if (frame.getPolygon().inside(p0) == Polygon.Intersection.OUTSIDE) {
                operationFrameMode = CreasePattern_Worker.OperationFrameMode.CREATE_1;
            } else {
                operationFrameMode = CreasePattern_Worker.OperationFrameMode.MOVE_BOX_4;
            }


            if (p.distance(p_ob1) < d.getSelectionDistance()) {
                p_new = frame.getP1();
                frame.setFramePoint(0, frame.getP3());
                frame.setFramePoint(2, p_new);
                operationFrameMode = CreasePattern_Worker.OperationFrameMode.MOVE_POINTS_2;
            }
            if (p.distance(p_ob2) < d.getSelectionDistance()) {
                p_new = frame.getP2();
                frame.setFramePoint(1, frame.getP1());
                frame.setFramePoint(0, frame.getP4());
                frame.setFramePoint(3, frame.getP3());
                frame.setFramePoint(2, p_new);
                operationFrameMode = CreasePattern_Worker.OperationFrameMode.MOVE_POINTS_2;
            }
            if (p.distance(p_ob3) < d.getSelectionDistance()) {
                p_new = frame.getP3();
                frame.setFramePoint(0, frame.getP1());
                frame.setFramePoint(2, p_new);
                operationFrameMode = CreasePattern_Worker.OperationFrameMode.MOVE_POINTS_2;
            }
            if (p.distance(p_ob4) < d.getSelectionDistance()) {
                p_new = frame.getP4();
                frame.setFramePoint(3, frame.getP1());
                frame.setFramePoint(0, frame.getP2());
                frame.setFramePoint(1, frame.getP3());
                frame.setFramePoint(2, p_new);
                operationFrameMode = CreasePattern_Worker.OperationFrameMode.MOVE_POINTS_2;
            }

        }
        if (operationFrameMode == CreasePattern_Worker.OperationFrameMode.MOVE_SIDES_3) {
            while (OritaCalc.determineLineSegmentDistance(p, p_ob1, p_ob2) != distance_min) {
                p_new = frame.getP1();
                frame.setFramePoint(0, frame.getP2());
                frame.setFramePoint(1, frame.getP3());
                frame.setFramePoint(2, frame.getP4());
                frame.setFramePoint(3, p_new);
                p_new = p_ob1;
                p_ob1 = p_ob2;
                p_ob2 = p_ob3;
                p_ob3 = p_ob4;
                p_ob4 = p_new;
            }
        }

        if (operationFrameMode == CreasePattern_Worker.OperationFrameMode.CREATE_1) {
            frame.setActive(true);

            p_new = p;

            Point closest_point = d.getClosestPoint(p);

            if (p.distance(closest_point) < d.getSelectionDistance()) {
                p_new = closest_point;
            }

            frame.setFramePoint(0, cam.object2TV(p_new));
            frame.setFramePoint(1, cam.object2TV(p_new));
            frame.setFramePoint(2, cam.object2TV(p_new));
            frame.setFramePoint(3, cam.object2TV(p_new));
        }
    }

    //マウス操作(mouseMode==61　長方形内選択でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {

        Point p = d.getCamera().TV2object(p0);
        OperationFrame frame = d.getOperationFrame();
        if (operationFrameMode == CreasePattern_Worker.OperationFrameMode.MOVE_POINTS_2) {
            operationFrameMode = CreasePattern_Worker.OperationFrameMode.CREATE_1;
        }

        Point p_new = new Point();

        if (!d.getGridInputAssist()) {
            p_new = p;
        }

        if (d.getGridInputAssist()) {
            d.getLineCandidate().clear();

            Point closest_point = d.getClosestPoint(p);
            if (p.distance(closest_point) < d.getSelectionDistance()) {
                d.getLineCandidate().add(new LineSegment(closest_point, closest_point, LineColor.GREEN_6));
            } else {
                d.getLineCandidate().add(new LineSegment(p, p, LineColor.GREEN_6));
            }
            d.getLineCandidate().get(0).setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);

            p_new = d.getLineCandidate().get(0).getA();
        }


        updateFrame(frame, p_new);
        lastMousePos = p_new;
    }

//--------------------

    //マウス操作(mouseMode==61 長方形内選択　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {

        Point p = d.getCamera().TV2object(p0);
        OperationFrame frame = d.getOperationFrame();
        Point p_new = p;

        Point closest_point = d.getClosestPoint(p);
        if (p.distance(closest_point) <= d.getSelectionDistance()) {
            p_new = closest_point;/*line_step[1].seta(moyori_ten);*/
        }

        updateFrame(frame, p_new);

        if (frame.getPolygon().calculateArea() < 1.0) {
            frame.setActive(false);
        }
    }

    @Override
    public void reset() {
        d.getOperationFrame().setActive(false);
    }

    private void updateFrame(OperationFrame frame, Point p_new) {
        Camera cam = d.getCamera();
        if (operationFrameMode == CreasePattern_Worker.OperationFrameMode.MOVE_SIDES_3) {
            if (Math.abs(frame.getP1().getX() - frame.getP2().getX())
                    <
                    Math.abs(frame.getP1().getY() - frame.getP2().getY())
            ) {
                frame.setFramePointX(0, cam.object2TV(p_new).getX());
                frame.setFramePointX(1, cam.object2TV(p_new).getX());
            }

            if (Math.abs(frame.getP1().getX() - frame.getP2().getX())
                    >
                    Math.abs(frame.getP1().getY() - frame.getP2().getY())
            ) {
                frame.setFramePointY(0, cam.object2TV(p_new).getY());
                frame.setFramePointY(1, cam.object2TV(p_new).getY());
            }
        }

        if (operationFrameMode == CreasePattern_Worker.OperationFrameMode.MOVE_BOX_4) {
            for (int i = 0; i < 4; i++) {
                frame.setFramePoint(i,
                        frame.getFramePoint(i).move(
                                cam.object2TV(lastMousePos).delta(cam.object2TV(p_new))));
            }
        }

        if (operationFrameMode == CreasePattern_Worker.OperationFrameMode.CREATE_1) {
            frame.setFramePoint(2, d.getCamera().object2TV(p_new));
            frame.setFramePoint(1, new Point(frame.getP1().getX(), frame.getP3().getY()));
            frame.setFramePoint(3, new Point(frame.getP3().getX(), frame.getP1().getY()));
        }
    }
}
