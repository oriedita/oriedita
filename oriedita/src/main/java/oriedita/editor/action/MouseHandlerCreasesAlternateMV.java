package oriedita.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.tinylog.Logger;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import oriedita.editor.canvas.MouseMode;
import origami.folding.util.SortingBox;

@Singleton
public class MouseHandlerCreasesAlternateMV extends BaseMouseHandlerInputRestricted {
    private final MouseHandlerLineSegmentRatioSet mouseHandlerLineSegmentRatioSet;

    @Inject
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
        p.set(d.getCamera().TV2object(p0));
        Point closestPoint = d.getClosestPoint(p);
        if (p.distance(closestPoint) > d.getSelectionDistance()) {
            closestPoint.set(p);
        }
        d.lineStepAdd(new LineSegment(p, closestPoint, d.getLineColor()));
    }

    //マウス操作(mouseMode==36　でドラッグしたとき)を行う関数----------------------------------------------------

    public void mouseDragged(Point p0) {
        mouseHandlerLineSegmentRatioSet.mouseDragged(p0);
    }

    //マウス操作(mouseMode==36　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        SortingBox<LineSegment> nbox = new SortingBox<>();

        if (d.getLineStep().size() == 1) {
            Point p = new Point();
            p.set(d.getCamera().TV2object(p0));
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) > d.getSelectionDistance()) {
                closestPoint.set(p);
            }
            d.getLineStep().get(0).setA(closestPoint);
            if (Epsilon.high.gt0(d.getLineStep().get(0).determineLength())) {
                for (int i = 1; i <= d.getFoldLineSet().getTotal(); i++) {
                    LineSegment s = d.getFoldLineSet().get(i);
                    LineSegment.Intersection i_senbun_kousa_hantei = OritaCalc.determineLineSegmentIntersection(s, d.getLineStep().get(0), Epsilon.UNKNOWN_1EN4);
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
                        nbox.addByWeight(s, OritaCalc.distance(d.getLineStep().get(0).getB(), OritaCalc.findIntersection(s, d.getLineStep().get(0))));
                    }
                }

                Logger.info("i_d_sousuu" + nbox.getTotal());

                LineColor icol_temp = d.getLineColor();

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

            d.getLineStep().clear();
        }
    }
}
