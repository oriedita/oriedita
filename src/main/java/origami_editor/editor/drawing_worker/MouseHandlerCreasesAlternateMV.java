package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;
import origami_editor.sortingbox.SortingBox;
import origami_editor.sortingbox.WeightedValue;

public class MouseHandlerCreasesAlternateMV extends BaseMouseHandler{
    private final MouseHandlerLineSegmentRatioSet mouseHandlerLineSegmentRatioSet;

    public MouseHandlerCreasesAlternateMV(DrawingWorker d) {
        super(d);
        this.mouseHandlerLineSegmentRatioSet = new MouseHandlerLineSegmentRatioSet(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASES_ALTERNATE_MV_36;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        this.mouseHandlerLineSegmentRatioSet.mouseMoved(p0);
    }//近い既存点のみ表示

    //マウス操作(mouseMode==36　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        d.i_drawing_stage = 1;

        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.closest_point.set(d.getClosestPoint(p));
        if (p.distance(d.closest_point) > d.selectionDistance) {
            d.closest_point.set(p);
        }
        d.line_step[1].set(p, d.closest_point);
        d.line_step[1].setColor(d.lineColor);
    }

    //マウス操作(mouseMode==36　でドラッグしたとき)を行う関数----------------------------------------------------

    public void mouseDragged(Point p0) {
        mouseHandlerLineSegmentRatioSet.mouseDragged(p0);
    }

    //マウス操作(mouseMode==36　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        SortingBox<LineSegment> nbox = new SortingBox<>();

        if (d.i_drawing_stage == 1) {
            d.i_drawing_stage = 0;
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            d.closest_point.set(d.getClosestPoint(p));
            if (p.distance(d.closest_point) > d.selectionDistance) {
                d.closest_point.set(p);
            }
            d.line_step[1].setA(d.closest_point);
            if (d.line_step[1].getLength() > 0.00000001) {
                for (int i = 1; i <= d.foldLineSet.getTotal(); i++) {
                    LineSegment s = d.foldLineSet.get(i);
                    LineSegment.Intersection i_senbun_kousa_hantei = OritaCalc.line_intersect_decide(s, d.line_step[1], 0.0001, 0.0001);
                    int i_jikkou = 0;
                    if (i_senbun_kousa_hantei == LineSegment.Intersection.INTERSECTS_1) {
                        i_jikkou = 1;
                    }
                    if (i_senbun_kousa_hantei == LineSegment.Intersection.INTERSECTS_TSHAPE_S2_VERTICAL_BAR_27) {
                        i_jikkou = 1;
                    }
                    if (i_senbun_kousa_hantei == LineSegment.Intersection.INTERSECTS_TSHAPE_S2_VERTICAL_BAR_28) {
                        i_jikkou = 1;
                    }

                    if (i_jikkou == 1) {
                        WeightedValue<LineSegment> i_d = new WeightedValue<>(s, OritaCalc.distance(d.line_step[1].getB(), OritaCalc.findIntersection(s, d.line_step[1])));
                        nbox.container_i_smallest_first(i_d);
                    }

                }

                System.out.println("i_d_sousuu" + nbox.getTotal());

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
