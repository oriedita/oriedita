package origami_editor.editor.canvas;

import org.springframework.stereotype.Component;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;
import origami.folding.util.SortingBox;
import origami.folding.util.WeightedValue;

@Component
public class MouseHandlerCreasesAlternateMV extends BaseMouseHandlerInputRestricted {
    private final MouseHandlerLineSegmentRatioSet mouseHandlerLineSegmentRatioSet;

    public MouseHandlerCreasesAlternateMV(MouseHandlerLineSegmentRatioSet mouseHandlerLineSegmentRatioSet) {
        this.mouseHandlerLineSegmentRatioSet = mouseHandlerLineSegmentRatioSet;
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASES_ALTERNATE_MV_36;
    }

    //マウス操作(mouseMode==36　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        Point closestPoint = d.getClosestPoint(p);
        if (p.distance(closestPoint) > d.selectionDistance) {
            closestPoint.set(p);
        }
        d.lineStepAdd(new LineSegment(p, closestPoint, d.lineColor));
    }

    //マウス操作(mouseMode==36　でドラッグしたとき)を行う関数----------------------------------------------------

    public void mouseDragged(Point p0) {
        mouseHandlerLineSegmentRatioSet.mouseDragged(p0);
    }

    //マウス操作(mouseMode==36　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        SortingBox<LineSegment> nbox = new SortingBox<>();

        if (d.lineStep.size() == 1) {
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) > d.selectionDistance) {
                closestPoint.set(p);
            }
            d.lineStep.get(0).setA(closestPoint);
            if (Epsilon.high.gt0(d.lineStep.get(0).determineLength())) {
                for (int i = 1; i <= d.foldLineSet.getTotal(); i++) {
                    LineSegment s = d.foldLineSet.get(i);
                    LineSegment.Intersection i_senbun_kousa_hantei = OritaCalc.determineLineSegmentIntersection(s, d.lineStep.get(0), Epsilon.UNKNOWN_1EN4, Epsilon.UNKNOWN_1EN4);
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
                        WeightedValue<LineSegment> i_d = new WeightedValue<>(s, OritaCalc.distance(d.lineStep.get(0).getB(), OritaCalc.findIntersection(s, d.lineStep.get(0))));
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

            d.lineStep.clear();
        }
    }
}
