package origami_editor.editor.canvas;

import javax.inject.Inject;
import javax.inject.Singleton;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

@Singleton
public class MouseHandlerCircleDrawConcentricTwoCircleSelect extends BaseMouseHandler {
    @Inject
    public MouseHandlerCircleDrawConcentricTwoCircleSelect() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CIRCLE_DRAW_CONCENTRIC_TWO_CIRCLE_SELECT_50;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        Circle closest_circumference = new Circle(); //Circle with the circumference closest to the mouse
        closest_circumference.set(d.getClosestCircleMidpoint(p));
        Point closest_point = d.getClosestPoint(p);

        if ((d.lineStep.size() == 0) && (d.circleStep.size() == 0)) {
            if (OritaCalc.distance_circumference(p, closest_circumference) > d.selectionDistance) {
                return;
            }

            d.lineStep.clear();
            d.circleStep.add(new Circle(closest_circumference.determineCenter(), closest_circumference.getR(), LineColor.GREEN_6));
        } else if ((d.lineStep.size() == 0) && (d.circleStep.size() == 1)) {
            if (OritaCalc.distance_circumference(p, closest_circumference) > d.selectionDistance) {
                return;
            }

            d.lineStep.clear();
            d.circleStep.add(new Circle(closest_circumference.determineCenter(), closest_circumference.getR(), LineColor.GREEN_6));
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if ((d.lineStep.size() == 0) && (d.circleStep.size() == 2)) {
            Circle circle1 = d.circleStep.get(0);
            Circle circle2 = d.circleStep.get(1);
            d.circleStep.clear();
            double add_r = (OritaCalc.distance(circle1.determineCenter(), circle2.determineCenter()) - circle1.getR() - circle2.getR()) / 2;

            if (!Epsilon.high.eq0(add_r)) {
                double new_r1 = add_r + circle1.getR();
                double new_r2 = add_r + circle2.getR();

                if (Epsilon.high.gt0(new_r1) && Epsilon.high.gt0(new_r2)) {
                    circle1.setR(new_r1);
                    circle1.setColor(LineColor.CYAN_3);
                    d.addCircle(circle1);
                    circle2.setR(new_r2);
                    circle2.setColor(LineColor.CYAN_3);
                    d.addCircle(circle2);
                    d.record();
                }
            }
        }

    }
}
