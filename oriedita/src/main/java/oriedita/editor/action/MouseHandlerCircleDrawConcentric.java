package oriedita.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import oriedita.editor.canvas.MouseMode;

@Singleton
public class MouseHandlerCircleDrawConcentric extends BaseMouseHandler {
    @Inject
    public MouseHandlerCircleDrawConcentric() {
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
        p.set(d.getCamera().TV2object(p0));
        Circle closest_circumference = new Circle(); //Circle with the circumference closest to the mouse
        closest_circumference.set(d.getClosestCircleMidpoint(p));
        Point closestPoint = d.getClosestPoint(p);

        if ((d.getLineStep().size() == 0) && (d.getCircleStep().size() == 0)) {
            if (OritaCalc.distance_circumference(p, closest_circumference) > d.getSelectionDistance()) {
                return;
            }

            d.getLineStep().clear();
            d.getCircleStep().clear();
            d.getCircleStep().add(new Circle(closest_circumference.determineCenter(), closest_circumference.getR(), LineColor.GREEN_6));
            return;
        }

        if ((d.getLineStep().size() == 0) && (d.getCircleStep().size() == 1)) {
            if (p.distance(closestPoint) > d.getSelectionDistance()) {
                return;
            }

            d.lineStepAdd(new LineSegment(p, closestPoint, LineColor.CYAN_3));
            d.getCircleStep().add(new Circle(d.getCircleStep().get(0).determineCenter(), d.getCircleStep().get(0).getR(), LineColor.GREEN_6));
        }
    }

    //マウス操作(mouseMode==48 同心円　線分入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        Point p = new Point();
        p.set(d.getCamera().TV2object(p0));
        if ((d.getLineStep().size() == 1) && (d.getCircleStep().size() == 2)) {
            d.getLineStep().get(0).setA(p);
            d.getCircleStep().get(1).setR(d.getCircleStep().get(0).getR() + d.getLineStep().get(0).determineLength());
        }
    }

    //マウス操作(mouseMode==48 同心円　線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if ((d.getLineStep().size() == 1) && (d.getCircleStep().size() == 2)) {
            Circle circle1 = d.getCircleStep().get(0);
            Circle circle2 = d.getCircleStep().get(1);

            d.getCircleStep().clear();

            Point p = new Point();
            p.set(d.getCamera().TV2object(p0));
            Point closestPoint = d.getClosestPoint(p);
            d.getLineStep().get(0).setA(closestPoint);
            if (p.distance(closestPoint) <= d.getSelectionDistance()) {
                if (Epsilon.high.gt0(d.getLineStep().get(0).determineLength())) {
                    d.addLineSegment(d.getLineStep().get(0));
                    circle2.setR(circle1.getR() + d.getLineStep().get(0).determineLength());
                    d.addCircle(circle2);
                    d.record();
                }
            }

            d.getLineStep().clear();
        }
    }
}
