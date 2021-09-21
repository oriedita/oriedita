package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerCircleChangeColor extends BaseMouseHandler{
    public MouseHandlerCircleChangeColor(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CIRCLE_CHANGE_COLOR_59;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==59 "特注プロパティ指定" でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        d.mPressed_A_box_select(p0);   //折線と補助活線と補助絵線
    }

    //マウス操作(mouseMode==59 "特注プロパティ指定"でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        d.mDragged_A_box_select(p0);
    }

    //マウス操作(mouseMode==59 "特注プロパティ指定" でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {//補助活線と円
        d.i_drawing_stage = 0;
        if (d.p19_1.distance(p0) > 0.000001) {//現状では削除しないときもUNDO用に記録されてしまう20161218

            if (d.change_property_in_4kakukei(d.p19_1, p0)) {
            }
        }

        if (d.p19_1.distance(p0) <= 0.000001) {
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            double rs_min;
            rs_min = d.foldLineSet.closestLineSegmentDistance(p);//点pに最も近い補助活線の番号での、その距離を返す
            double re_min;
            re_min = d.foldLineSet.closestCircleDistance(p);//点pに最も近い円の番号での、その距離を返す	public double mottomo_tikai_en_kyori(Ten p)

            if (rs_min <= re_min) {
                if (rs_min < d.selectionDistance) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                    if (d.foldLineSet.getColor(d.foldLineSet.closestLineSegmentSearchReversedOrder(p)) == LineColor.CYAN_3) {
                        d.foldLineSet.setLineCustomized(d.foldLineSet.closestLineSegmentSearchReversedOrder(p), 1);
                        d.foldLineSet.setLineCustomizedColor(d.foldLineSet.closestLineSegmentSearchReversedOrder(p), d.customCircleColor);
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
