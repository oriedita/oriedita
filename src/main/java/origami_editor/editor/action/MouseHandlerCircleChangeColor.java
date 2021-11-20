package origami_editor.editor.action;

import origami.Epsilon;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MouseHandlerCircleChangeColor extends BaseMouseHandlerBoxSelect {
    @Inject
    public MouseHandlerCircleChangeColor() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CIRCLE_CHANGE_COLOR_59;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==59 "特注プロパティ指定" でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {//補助活線と円
        d.lineStep.clear();
        if (selectionStart.distance(p0) > Epsilon.UNKNOWN_1EN6) {//現状では削除しないときもUNDO用に記録されてしまう20161218

            if (d.change_property_in_4kakukei(selectionStart, p0)) {
            }
        }

        if (selectionStart.distance(p0) <= Epsilon.UNKNOWN_1EN6) {
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            double rs_min;
            rs_min = d.foldLineSet.closestLineSegmentDistance(p);//点pに最も近い補助活線の番号での、その距離を返す
            double re_min;
            re_min = d.foldLineSet.closestCircleDistance(p);//点pに最も近い円の番号での、その距離を返す	public double mottomo_tikai_en_kyori(Ten p)

            if (rs_min <= re_min) {
                if (rs_min < d.selectionDistance) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                    LineSegment closestLineSegment = d.foldLineSet.closestLineSegmentSearchReversedOrder(p);
                    if (closestLineSegment.getColor() == LineColor.CYAN_3) {
                        closestLineSegment.setCustomized(1);
                        closestLineSegment.setCustomizedColor(d.customCircleColor);
                        //en_seiri();kiroku();
                    }
                }
            } else {
                if (re_min < d.selectionDistance) {
                    d.foldLineSet.setCircleCustomized(d.foldLineSet.closest_circle_search_reverse_order(p), 1);
                    d.foldLineSet.setCircleCustomizedColor(d.foldLineSet.closest_circle_search_reverse_order(p), d.customCircleColor);
                }
            }
        }
    }
}
