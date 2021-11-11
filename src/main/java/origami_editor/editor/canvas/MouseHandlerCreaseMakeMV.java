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
public class MouseHandlerCreaseMakeMV extends BaseMouseHandlerInputRestricted {
    private final MouseHandlerDrawCreaseRestricted mouseHandlerDrawCreaseRestricted;

    public MouseHandlerCreaseMakeMV(MouseHandlerDrawCreaseRestricted mouseHandlerDrawCreaseRestricted) {
        this.mouseHandlerDrawCreaseRestricted = mouseHandlerDrawCreaseRestricted;
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_MAKE_MV_34;
    }

    //マウス操作(mouseMode==34　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        Point closest_point = d.getClosestPoint(p);
        if (p.distance(closest_point) > d.selectionDistance) {
            d.lineStep.clear();
            return;
        }
        d.lineStepAdd(new LineSegment(p, closest_point, d.lineColor));
    }

    //マウス操作(mouseMode==34　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        mouseHandlerDrawCreaseRestricted.mouseDragged(p0);
    }


//64 64 64 64 64 64 64 64 64 64 64 64 64入力した線分に重複している折線を削除する

    //マウス操作(mouseMode==34　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        SortingBox<LineSegment> nbox = new SortingBox<>();

        if (d.lineStep.size() == 1) {
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            Point closest_point = d.getClosestPoint(p);
            d.lineStep.get(0).setA(closest_point);
            if (p.distance(closest_point) <= d.selectionDistance) {
                if (Epsilon.high.gt0(d.lineStep.get(0).determineLength())) {
                    for (int i = 1; i <= d.foldLineSet.getTotal(); i++) {
                        LineSegment s = d.foldLineSet.get(i);
                        if (OritaCalc.isLineSegmentOverlapping(s, d.lineStep.get(0))) {
                            WeightedValue<LineSegment> i_d = new WeightedValue<>(s, OritaCalc.determineLineSegmentDistance(d.lineStep.get(0).getB(), s));
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
            d.lineStep.clear();
        }
    }
}
