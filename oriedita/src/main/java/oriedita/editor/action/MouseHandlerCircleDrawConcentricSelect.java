package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.CIRCLE_DRAW_CONCENTRIC_SELECT_49)
public class MouseHandlerCircleDrawConcentricSelect extends BaseMouseHandler {
    Circle closest_circumference = new Circle(100000.0, 100000.0, 10.0, LineColor.PURPLE_8); //Circle with the circumference closest to the mouse

    @Inject
    public MouseHandlerCircleDrawConcentricSelect() {
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==49 同心円　同心円入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.getCamera().TV2object(p0));
        closest_circumference.set(d.getClosestCircleMidpoint(p));
        // Point closest_point = d.getClosestPoint(p);

        if ((d.getLineStep().size() == 0) && (d.getCircleStep().size() == 0)) {
            if (OritaCalc.distance_circumference(p, closest_circumference) > d.getSelectionDistance()) {
                return;
            }

            d.getCircleStep().add(new Circle(closest_circumference.determineCenter(), closest_circumference.getR(), LineColor.GREEN_6));
        } else if ((d.getLineStep().size() == 0) && (d.getCircleStep().size() == 1)) {
            if (OritaCalc.distance_circumference(p, closest_circumference) > d.getSelectionDistance()) {
                return;
            }

            d.getCircleStep().add(new Circle(closest_circumference.determineCenter(), closest_circumference.getR(), LineColor.PURPLE_8));
        } else if ((d.getLineStep().size() == 0) && (d.getCircleStep().size() == 2)) {
            if (OritaCalc.distance_circumference(p, closest_circumference) > d.getSelectionDistance()) {
                return;
            }

            d.getCircleStep().add(new Circle(closest_circumference.determineCenter(), closest_circumference.getR(), LineColor.PURPLE_8));
        }
    }

    //マウス操作(mouseMode==49 同心円　同心円入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {

    }

    //マウス操作(mouseMode==49 同心円　同心円入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if ((d.getLineStep().size() == 0) && (d.getCircleStep().size() == 3)) {
            Circle circle1 = d.getCircleStep().get(0);
            Circle circle2 = d.getCircleStep().get(1);
            Circle circle3 = d.getCircleStep().get(2);
            d.getCircleStep().clear();
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
