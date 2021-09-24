package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerCircleDrawConcentric extends BaseMouseHandler {

    public MouseHandlerCircleDrawConcentric(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CIRCLE_DRAW_CONCENTRIC_48;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==48 同心円　線分入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        Circle closest_circumference = new Circle(); //Circle with the circumference closest to the mouse
        closest_circumference.set(d.getClosestCircleMidpoint(p));
        Point closestPoint = d.getClosestPoint(p);

        if ((d.lineStep.size() == 0) && (d.circleStep.size() == 0)) {
            if (OritaCalc.distance_circumference(p, closest_circumference) > d.selectionDistance) {
                return;
            }

            d.lineStep.clear();
            d.circleStep.clear();
            d.circleStep.add(new Circle(closest_circumference.getCenter(), closest_circumference.getRadius(), LineColor.GREEN_6));
            return;
        }

        if ((d.lineStep.size() == 0) && (d.circleStep.size() == 1)) {
            if (p.distance(closestPoint) > d.selectionDistance) {
                return;
            }

            d.lineStepAdd(new LineSegment(p, closestPoint, LineColor.CYAN_3));
            d.circleStep.add(new Circle(d.circleStep.get(0).getCenter(), d.circleStep.get(0).getRadius(), LineColor.GREEN_6));
        }
    }

    //マウス操作(mouseMode==48 同心円　線分入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        if ((d.lineStep.size() == 1) && (d.circleStep.size() == 2)) {
            d.lineStep.get(0).setA(p);
            d.circleStep.get(1).setR(d.circleStep.get(0).getRadius() + d.lineStep.get(0).getLength());
        }
    }

    //マウス操作(mouseMode==48 同心円　線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if ((d.lineStep.size() == 1) && (d.circleStep.size() == 2)) {
            Circle circle1 = d.circleStep.get(0);
            Circle circle2 = d.circleStep.get(1);

            d.circleStep.clear();

            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            Point closestPoint = d.getClosestPoint(p);
            d.lineStep.get(0).setA(closestPoint);
            if (p.distance(closestPoint) <= d.selectionDistance) {
                if (d.lineStep.get(0).getLength() > 0.00000001) {
                    d.addLineSegment(d.lineStep.get(0));
                    circle2.setR(circle1.getRadius() + d.lineStep.get(0).getLength());
                    d.addCircle(circle2);
                    d.record();
                }
            }

            d.lineStep.clear();
        }
    }
}
