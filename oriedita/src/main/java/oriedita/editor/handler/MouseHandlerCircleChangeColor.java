package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.CIRCLE_CHANGE_COLOR_59)
public class MouseHandlerCircleChangeColor extends BaseMouseHandlerBoxSelect {
    @Inject
    public MouseHandlerCircleChangeColor() {
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==59 "特注プロパティ指定" でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {//補助活線と円
        super.mouseReleased(p0);
        d.getLineStep().clear();
        if (selectionStart.distance(p0) > Epsilon.UNKNOWN_1EN6) {//現状では削除しないときもUNDO用に記録されてしまう20161218

            if (d.change_property_in_4kakukei(selectionStart, p0)) {
            }
        }

        if (selectionStart.distance(p0) <= Epsilon.UNKNOWN_1EN6) {
            Point p = new Point();
            p.set(d.getCamera().TV2object(p0));
            double rs_min;
            rs_min = d.getFoldLineSet().closestLineSegmentDistance(p);//点pに最も近い補助活線の番号での、その距離を返す
            double re_min;
            re_min = d.getFoldLineSet().closestCircleDistance(p);//点pに最も近い円の番号での、その距離を返す	public double mottomo_tikai_en_kyori(Ten p)

            if (rs_min <= re_min) {
                if (rs_min < d.getSelectionDistance()) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                    LineSegment closestLineSegment = d.getFoldLineSet().closestLineSegmentSearchReversedOrder(p);
                    if (closestLineSegment.getColor() == LineColor.CYAN_3) {
                        closestLineSegment.setCustomized(1);
                        closestLineSegment.setCustomizedColor(d.getCustomCircleColor());
                        //en_seiri();kiroku();
                    }
                }
            } else {
                if (re_min < d.getSelectionDistance()) {
                    d.getFoldLineSet().setCircleCustomized(d.getFoldLineSet().closest_circle_search_reverse_order(p), 1);
                    d.getFoldLineSet().setCircleCustomizedColor(d.getFoldLineSet().closest_circle_search_reverse_order(p), d.getCustomCircleColor());
                }
            }
        }
    }
}
