package origami_editor.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

@Singleton
public class MouseHandlerCircleDrawConcentricSelect extends BaseMouseHandler {
    Circle closest_circumference = new Circle(100000.0, 100000.0, 10.0, LineColor.PURPLE_8); //Circle with the circumference closest to the mouse

    @Inject
    public MouseHandlerCircleDrawConcentricSelect() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CIRCLE_DRAW_CONCENTRIC_SELECT_49;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==49 同心円　同心円入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        closest_circumference.set(d.getClosestCircleMidpoint(p));
        Point closest_point = d.getClosestPoint(p);

        if ((d.lineStep.size() == 0) && (d.circleStep.size() == 0)) {
            if (OritaCalc.distance_circumference(p, closest_circumference) > d.selectionDistance) {
                return;
            }

            d.circleStep.add(new Circle(closest_circumference.determineCenter(), closest_circumference.getR(), LineColor.GREEN_6));
        } else if ((d.lineStep.size() == 0) && (d.circleStep.size() == 1)) {
            if (OritaCalc.distance_circumference(p, closest_circumference) > d.selectionDistance) {
                return;
            }

            d.circleStep.add(new Circle(closest_circumference.determineCenter(), closest_circumference.getR(), LineColor.PURPLE_8));
        } else if ((d.lineStep.size() == 0) && (d.circleStep.size() == 2)) {
            if (OritaCalc.distance_circumference(p, closest_circumference) > d.selectionDistance) {
                return;
            }

            d.circleStep.add(new Circle(closest_circumference.determineCenter(), closest_circumference.getR(), LineColor.PURPLE_8));
        }
    }

    //マウス操作(mouseMode==49 同心円　同心円入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {

    }

    //マウス操作(mouseMode==49 同心円　同心円入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if ((d.lineStep.size() == 0) && (d.circleStep.size() == 3)) {
            Circle circle1 = d.circleStep.get(0);
            Circle circle2 = d.circleStep.get(1);
            Circle circle3 = d.circleStep.get(2);
            d.circleStep.clear();
            double add_r = circle3.getR() - circle2.getR();
            if (!Epsilon.high.eq0(add_r)) {
                double new_r = add_r + circle1.getR();

                if (Epsilon.high.gt0(new_r)) {
                    circle1.setR(new_r);
                    circle1.setColor(LineColor.CYAN_3);
                    d.addCircle(circle1);
                    d.record();
                }
            }
        }
    }
}
