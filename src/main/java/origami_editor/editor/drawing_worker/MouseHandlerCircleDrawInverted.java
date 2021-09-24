package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerCircleDrawInverted extends BaseMouseHandler {

    public MouseHandlerCircleDrawInverted(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CIRCLE_DRAW_INVERTED_46;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        Circle closest_circumference = new Circle(100000.0, 100000.0, 10.0, LineColor.PURPLE_8); //Circle with the circumference closest to the mouse
        closest_circumference.set(d.getClosestCircleMidpoint(p));

        if (d.lineStep.size() + d.circleStep.size() == 0) {
            LineSegment closestLineSegment = new LineSegment();
            closestLineSegment.set(d.getClosestLineSegment(p));

            if (OritaCalc.distance_lineSegment(p, closestLineSegment) < OritaCalc.distance_circumference(p, closest_circumference)) {//線分の方が円周より近い
                if (OritaCalc.distance_lineSegment(p, closestLineSegment) > d.selectionDistance) {
                    return;
                }

                closestLineSegment.setColor(LineColor.GREEN_6);
                d.lineStepAdd(closestLineSegment);
                return;
            }

            d.lineStep.clear();
            if (OritaCalc.distance_circumference(p, closest_circumference) > d.selectionDistance) {
                return;
            }

            d.circleStep.add(new Circle(closest_circumference.getCenter(), closest_circumference.getRadius(), LineColor.GREEN_6));
            return;
        }

        if (d.lineStep.size() + d.circleStep.size() == 1) {
            if (OritaCalc.distance_circumference(p, closest_circumference) > d.selectionDistance) {
                return;
            }
            d.circleStep.add(new Circle(closest_circumference.getCenter(), closest_circumference.getRadius(), LineColor.RED_1));
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if ((d.lineStep.size() == 1) && (d.circleStep.size() == 1)) {
            d.add_hanten(d.lineStep.get(0), d.circleStep.get(0));
            d.lineStep.clear();
            d.circleStep.clear();
        }

        if ((d.lineStep.size() == 0) && (d.circleStep.size() == 2)) {
            d.add_hanten(d.circleStep.get(0), d.circleStep.get(1));
            d.lineStep.clear();
            d.circleStep.clear();
        }
    }
}
