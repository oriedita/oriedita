package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;
import origami_editor.sortingbox.SortingBox;
import origami_editor.sortingbox.WeightedValue;

public class MouseHandlerCreaseMakeMV extends BaseMouseHandler{
    private final MouseHandlerDrawCreaseRestricted mouseHandlerDrawCreaseRestricted;

    public MouseHandlerCreaseMakeMV(DrawingWorker d) {
        super(d);
        mouseHandlerDrawCreaseRestricted = new MouseHandlerDrawCreaseRestricted(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_MAKE_MV_34;
    }

    public void mouseMoved(Point p0) {
        mouseHandlerDrawCreaseRestricted.mouseMoved(p0);
    }//近い既存点のみ表示

    //マウス操作(mouseMode==34　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        d.i_drawing_stage = 1;

        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.closest_point.set(d.getClosestPoint(p));
        if (p.distance(d.closest_point) > d.selectionDistance) {
            d.i_drawing_stage = 0;
        }
        d.line_step[1].set(p, d.closest_point);
        d.line_step[1].setColor(d.lineColor);
    }

    //マウス操作(mouseMode==34　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        mouseHandlerDrawCreaseRestricted.mouseDragged(p0);
    }


//64 64 64 64 64 64 64 64 64 64 64 64 64入力した線分に重複している折線を削除する

    //マウス操作(mouseMode==34　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {

        SortingBox<LineSegment> nbox = new SortingBox<>();

        if (d.i_drawing_stage == 1) {
            d.i_drawing_stage = 0;
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            d.closest_point.set(d.getClosestPoint(p));
            d.line_step[1].setA(d.closest_point);
            if (p.distance(d.closest_point) <= d.selectionDistance) {
                if (d.line_step[1].getLength() > 0.00000001) {
                    for (int i = 1; i <= d.foldLineSet.getTotal(); i++) {
                        LineSegment s = d.foldLineSet.get(i);
                        if (OritaCalc.lineSegmentoverlapping(s, d.line_step[1])) {
                            WeightedValue<LineSegment> i_d = new WeightedValue<>(s, OritaCalc.distance_lineSegment(d.line_step[1].getB(), s));
                            nbox.container_i_smallest_first(i_d);
                        }

                    }

                    LineColor icol_temp = d.lineColor;

                    for (int i = 1; i <= nbox.getTotal(); i++) {
                        nbox.getValue(i).setColor(icol_temp);

                        if (icol_temp == LineColor.RED_1) {
                            icol_temp = LineColor.BLUE_2;
                        } else if (icol_temp == LineColor.BLUE_2) {
                            icol_temp = LineColor.RED_1;
                        }
                    }
                    d.record();
                }
            }
        }
    }
}
