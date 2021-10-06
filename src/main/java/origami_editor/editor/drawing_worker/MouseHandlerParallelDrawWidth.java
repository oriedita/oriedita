package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerParallelDrawWidth extends BaseMouseHandler {
    public MouseHandlerParallelDrawWidth(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.PARALLEL_DRAW_WIDTH_51;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==51 平行線　幅指定入力モード　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        Point closest_point = d.getClosestPoint(p);

        if ((d.lineStep.size() == 0) && (d.circleStep.size() == 0)) {
            LineSegment closestLineSegment = new LineSegment();
            closestLineSegment.set(d.getClosestLineSegment(p));
            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.selectionDistance) {
                closestLineSegment.setColor(LineColor.GREEN_6);
                d.lineStepAdd(closestLineSegment);
            }
            return;
        }

        if ((d.lineStep.size() == 1) && (d.circleStep.size() == 0)) {
            if (p.distance(closest_point) > d.selectionDistance) {
                return;
            }

            d.lineStepAdd(new LineSegment(p, closest_point, LineColor.CYAN_3));
            LineSegment segment2 = new LineSegment();
            segment2.set(d.lineStep.get(0));
            segment2.setColor(LineColor.PURPLE_8);
            d.lineStepAdd(segment2);
            LineSegment segment3 = new LineSegment();
            segment3.set(d.lineStep.get(0));
            segment3.setColor(LineColor.PURPLE_8);
            d.lineStepAdd(segment3);

            return;
        }


        if ((d.lineStep.size() == 4) && (d.circleStep.size() == 0)) {
            LineSegment closest_step_lineSegment = d.get_moyori_step_lineSegment(p, 3, 4);
            d.lineStep.remove(3);

            d.lineStep.get(2).set(closest_step_lineSegment);
        }
    }

    //マウス操作(mouseMode==51 平行線　幅指定入力モード　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        if ((d.lineStep.size() == 4) && (d.circleStep.size() == 0)) {
            d.lineStep.get(1).setA(p);
            d.lineStep.get(2).set(OritaCalc.moveParallel(d.lineStep.get(0), d.lineStep.get(1).determineLength()));
            d.lineStep.get(2).setColor(LineColor.PURPLE_8);
            d.lineStep.get(3).set(OritaCalc.moveParallel(d.lineStep.get(0), -d.lineStep.get(1).determineLength()));
            d.lineStep.get(3).setColor(LineColor.PURPLE_8);
        }
    }

    //マウス操作(mouseMode==51 平行線　幅指定入力モード　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        Point closest_point = d.getClosestPoint(p);

        if ((d.lineStep.size() == 4) && (d.circleStep.size() == 0)) {
            if (p.distance(closest_point) >= d.selectionDistance) {
                d.lineStep.remove(3);
                d.lineStep.remove(2);
                d.lineStep.remove(1);
                return;
            }

            d.lineStep.get(1).setA(closest_point);

            if (d.lineStep.get(1).determineLength() < 0.00000001) {
                d.lineStep.remove(3);
                d.lineStep.remove(2);
                d.lineStep.remove(1);
                return;
            }
            d.lineStep.get(2).set(OritaCalc.moveParallel(d.lineStep.get(0), d.lineStep.get(1).determineLength()));
            d.lineStep.get(2).setColor(LineColor.PURPLE_8);
            d.lineStep.get(3).set(OritaCalc.moveParallel(d.lineStep.get(0), -d.lineStep.get(1).determineLength()));
            d.lineStep.get(3).setColor(LineColor.PURPLE_8);
        }


        if ((d.lineStep.size() == 3) && (d.circleStep.size() == 0)) {
            d.lineStep.get(2).setColor(d.lineColor);
            d.addLineSegment(d.lineStep.get(2));

            d.lineStep.clear();
            d.record();
        }
    }
}
