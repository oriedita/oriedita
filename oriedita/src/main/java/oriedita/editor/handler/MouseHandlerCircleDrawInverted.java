package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.StraightLine;

@ApplicationScoped
@Handles(MouseMode.CIRCLE_DRAW_INVERTED_46)
public class MouseHandlerCircleDrawInverted extends BaseMouseHandler {
    @Inject
    public MouseHandlerCircleDrawInverted() {
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);

        Circle closest_circumference = new Circle(100000.0, 100000.0, 10.0, LineColor.PURPLE_8); //Circle with the circumference closest to the mouse
        closest_circumference.set(d.getClosestCircleMidpoint(p));

        if (d.getLineStep().size() + d.getCircleStep().size() == 0) {
            LineSegment closestLineSegment = d.getClosestLineSegment(p).withColor(LineColor.GREEN_6);

            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < OritaCalc.distance_circumference(p, closest_circumference)) {//線分の方が円周より近い
                if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) > d.getSelectionDistance()) {
                    return;
                }

                d.lineStepAdd(closestLineSegment);
                return;
            }

            d.getLineStep().clear();
            if (OritaCalc.distance_circumference(p, closest_circumference) > d.getSelectionDistance()) {
                return;
            }

            d.getCircleStep().add(new Circle(closest_circumference.determineCenter(), closest_circumference.getR(), LineColor.GREEN_6));
            return;
        }

        if (d.getLineStep().size() + d.getCircleStep().size() == 1) {
            if (OritaCalc.distance_circumference(p, closest_circumference) > d.getSelectionDistance()) {
                return;
            }
            d.getCircleStep().add(new Circle(closest_circumference.determineCenter(), closest_circumference.getR(), LineColor.RED_1));
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if ((d.getLineStep().size() == 1) && (d.getCircleStep().size() == 1)) {
            add_hanten(d.getLineStep().get(0), d.getCircleStep().get(0));
            d.getLineStep().clear();
            d.getCircleStep().clear();
        }

        if ((d.getLineStep().size() == 0) && (d.getCircleStep().size() == 2)) {
            add_hanten(d.getCircleStep().get(0), d.getCircleStep().get(1));
            d.getLineStep().clear();
            d.getCircleStep().clear();
        }
    }

    public void add_hanten(Circle e0, Circle eh) {
        //e0の円周が(x,y)を通るとき
        if (Math.abs(OritaCalc.distance(e0.determineCenter(), eh.determineCenter()) - e0.getR()) < Epsilon.UNKNOWN_1EN7) {
            LineSegment s_add = eh.turnAround_CircleToLineSegment(e0);
            //s_add.setcolor(3);
            d.addLineSegment(s_add);
            d.record();
            return;
        }

        //e0の円周が(x,y)を通らないとき。e0の円周の外部に(x,y)がくるとき//e0の円周の内部に(x,y)がくるとき
        Circle e_add = new Circle();
        e_add.set(eh.turnAround(e0));
        d.addCircle(e_add);
        d.record();
    }

    public void add_hanten(LineSegment s0, Circle eh) {
        StraightLine ty = new StraightLine(s0);
        //s0上に(x,y)がくるとき
        if (ty.calculateDistance(eh.determineCenter()) < Epsilon.UNKNOWN_1EN7) {
            return;
        }

        //s0が(x,y)を通らないとき。
        Circle e_add = new Circle();
        e_add.set(eh.turnAround_LineSegmentToCircle(s0));
        d.addCircle(e_add);
        d.record();
    }

}
