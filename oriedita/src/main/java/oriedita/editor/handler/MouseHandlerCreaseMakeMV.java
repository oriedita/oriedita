package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.folding.util.SortingBox;

@ApplicationScoped
@Handles(MouseMode.CREASE_MAKE_MV_34)
public class MouseHandlerCreaseMakeMV extends BaseMouseHandlerInputRestricted {
    private final MouseHandlerDrawCreaseRestricted mouseHandlerDrawCreaseRestricted;

    @Inject
    public MouseHandlerCreaseMakeMV(@Handles(MouseMode.DRAW_CREASE_RESTRICTED_11) MouseHandlerDrawCreaseRestricted mouseHandlerDrawCreaseRestricted) {
        this.mouseHandlerDrawCreaseRestricted = mouseHandlerDrawCreaseRestricted;
    }

    //マウス操作(mouseMode==34　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        Point closest_point = d.getClosestPoint(p);
        if (p.distance(closest_point) > d.getSelectionDistance()) {
            d.getLineStep().clear();
            return;
        }
        d.lineStepAdd(new LineSegment(p, closest_point, d.getLineColor()));
    }

    //マウス操作(mouseMode==34　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        mouseHandlerDrawCreaseRestricted.mouseDragged(p0);
    }


//64 64 64 64 64 64 64 64 64 64 64 64 64入力した線分に重複している折線を削除する

    //マウス操作(mouseMode==34　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        SortingBox<LineSegment> nbox = new SortingBox<>();

        if (d.getLineStep().size() == 1) {
            Point p = d.getCamera().TV2object(p0);
            Point closest_point = d.getClosestPoint(p);
            d.getLineStep().set(0, d.getLineStep().get(0).withA(closest_point));
            if (p.distance(closest_point) <= d.getSelectionDistance()) {
                if (Epsilon.high.gt0(d.getLineStep().get(0).determineLength())) {
                    for (var s : d.getFoldLineSet().getLineSegmentsIterable()) {
                        if (OritaCalc.isLineSegmentOverlapping(s, d.getLineStep().get(0))) {
                            nbox.addByWeight(s, OritaCalc.determineLineSegmentDistance(d.getLineStep().get(0).getB(), s));
                        }
                    }

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
            }
            d.getLineStep().clear();
        }
    }
}
