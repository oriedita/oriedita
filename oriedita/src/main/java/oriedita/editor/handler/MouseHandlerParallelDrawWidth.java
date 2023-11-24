package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.PARALLEL_DRAW_WIDTH_51)
public class MouseHandlerParallelDrawWidth extends BaseMouseHandler {
    @Inject
    public MouseHandlerParallelDrawWidth() {
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==51 平行線　幅指定入力モード　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);

        Point closest_point = d.getClosestPoint(p);

        if ((d.getLineStep().size() == 0) && (d.getCircleStep().size() == 0)) {
            LineSegment closestLineSegment = new LineSegment();
            closestLineSegment.set(d.getClosestLineSegment(p));
            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance()) {
                closestLineSegment.setColor(LineColor.GREEN_6);
                d.lineStepAdd(closestLineSegment);
            }
            return;
        }

        if ((d.getLineStep().size() == 1) && (d.getCircleStep().size() == 0)) {
            if (p.distance(closest_point) > d.getSelectionDistance()) {
                return;
            }

            d.lineStepAdd(new LineSegment(p, closest_point, LineColor.CYAN_3));
            LineSegment segment2 = new LineSegment();
            segment2.set(d.getLineStep().get(0));
            segment2.setColor(LineColor.PURPLE_8);
            d.lineStepAdd(segment2);
            LineSegment segment3 = new LineSegment();
            segment3.set(d.getLineStep().get(0));
            segment3.setColor(LineColor.PURPLE_8);
            d.lineStepAdd(segment3);

            return;
        }


        if ((d.getLineStep().size() == 4) && (d.getCircleStep().size() == 0)) {
            LineSegment closest_step_lineSegment = d.get_moyori_step_lineSegment(p, 3, 4);
            d.getLineStep().remove(3);

            d.getLineStep().get(2).set(closest_step_lineSegment);
        }
    }

    //マウス操作(mouseMode==51 平行線　幅指定入力モード　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        if ((d.getLineStep().size() == 4) && (d.getCircleStep().size() == 0)) {
            d.getLineStep().get(1).setA(p);
            d.getLineStep().get(2).set(OritaCalc.moveParallel(d.getLineStep().get(0), d.getLineStep().get(1).determineLength()));
            d.getLineStep().get(2).setColor(LineColor.PURPLE_8);
            d.getLineStep().get(3).set(OritaCalc.moveParallel(d.getLineStep().get(0), -d.getLineStep().get(1).determineLength()));
            d.getLineStep().get(3).setColor(LineColor.PURPLE_8);
        }
    }

    //マウス操作(mouseMode==51 平行線　幅指定入力モード　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        Point closest_point = d.getClosestPoint(p);

        if ((d.getLineStep().size() == 4) && (d.getCircleStep().size() == 0)) {
            if (p.distance(closest_point) >= d.getSelectionDistance()) {
                d.getLineStep().remove(3);
                d.getLineStep().remove(2);
                d.getLineStep().remove(1);
                return;
            }

            d.getLineStep().get(1).setA(closest_point);

            if (Epsilon.high.le0(d.getLineStep().get(1).determineLength())) {
                d.getLineStep().remove(3);
                d.getLineStep().remove(2);
                d.getLineStep().remove(1);
                return;
            }
            d.getLineStep().get(2).set(OritaCalc.moveParallel(d.getLineStep().get(0), d.getLineStep().get(1).determineLength()));
            d.getLineStep().get(2).setColor(LineColor.PURPLE_8);
            d.getLineStep().get(3).set(OritaCalc.moveParallel(d.getLineStep().get(0), -d.getLineStep().get(1).determineLength()));
            d.getLineStep().get(3).setColor(LineColor.PURPLE_8);
        }


        if ((d.getLineStep().size() == 3) && (d.getCircleStep().size() == 0)) {
            d.getLineStep().get(2).setColor(d.getLineColor());
            d.addLineSegment(d.getLineStep().get(2));

            d.getLineStep().clear();
            d.record();
        }
    }
}
