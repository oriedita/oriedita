package origami_editor.editor.canvas;

import org.springframework.stereotype.Component;
import origami.Epsilon;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

@Component
public class MouseHandlerLineSegmentDelete extends BaseMouseHandlerBoxSelect {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.LINE_SEGMENT_DELETE_3;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==3,23 でボタンを離したとき)を行う関数----------------------------------------------------
    @Override
    public void mouseReleased(Point p0) {//折線と補助活線と円
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.lineStep.clear();

        //最寄の一つを削除
        if (selectionStart.distance(p0) <= Epsilon.UNKNOWN_1EN6) {//最寄の一つを削除
            int i_removal_mode;//i_removal_mode is defined and declared here
            switch (d.i_foldLine_additional) {
                case POLY_LINE_0:
                    i_removal_mode = 0;
                    break;
                case BLACK_LINE_2:
                    i_removal_mode = 2;
                    break;
                case AUX_LIVE_LINE_3:
                    i_removal_mode = 3;
                    break;
                case AUX_LINE_1:
                    i_removal_mode = 1;
                    break;
                case BOTH_4:
                    i_removal_mode = 10;
                    double rs_min = d.foldLineSet.closestLineSegmentDistance(p);//点pに最も近い線分(折線と補助活線)の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                    double re_min = d.foldLineSet.closestCircleDistance(p);//点pに最も近い円の番号での、その距離を返す	public double mottomo_tikai_en_kyori(Ten p)
                    double hoj_rs_min = d.auxLines.closestLineSegmentDistance(p);//点pに最も近い補助絵線の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                    if ((rs_min <= re_min) && (rs_min <= hoj_rs_min)) {
                        LineSegment closestLineSegment = d.foldLineSet.closestLineSegmentSearchReversedOrder(p);
                        if (closestLineSegment.getColor().getNumber() < 3) {
                            i_removal_mode = 0;
                        } else {
                            i_removal_mode = 3;
                        }
                    }
                    if ((re_min < rs_min) && (re_min <= hoj_rs_min)) {
                        i_removal_mode = 3;
                    }
                    if ((hoj_rs_min < rs_min) && (hoj_rs_min < re_min)) {
                        i_removal_mode = 1;
                    }
                    break;
                default:
                    throw new IllegalArgumentException();
            }

            if (i_removal_mode == 0) { //折線の削除

                //Ten p =new Ten(); p.set(camera.TV2object(p0));
                double rs_min;
                rs_min = d.foldLineSet.closestLineSegmentDistance(p);//点pに最も近い線分(折線と補助活線)の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                if (rs_min < d.selectionDistance) {
                    LineSegment closestLineSegment = d.foldLineSet.closestLineSegmentSearchReversedOrder(p);
                    if (closestLineSegment.getColor().getNumber() < 3) {
                        d.foldLineSet.deleteLineSegment_vertex(closestLineSegment);
                        d.organizeCircles();
                        d.record();
                    }
                }
            }

            if (i_removal_mode == 2) { //黒の折線の削除
                double rs_min = d.foldLineSet.closestLineSegmentDistance(p);//点pに最も近い線分(折線と補助活線)の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                if (rs_min < d.selectionDistance) {
                    LineSegment closestLineSegment = d.foldLineSet.closestLineSegmentSearchReversedOrder(p);
                    if (closestLineSegment.getColor() == LineColor.BLACK_0) {
                        d.foldLineSet.deleteLineSegment_vertex(closestLineSegment);
                        d.organizeCircles();
                        d.record();
                    }
                }
            }

            if (i_removal_mode == 3) {  //補助活線
                double rs_min = d.foldLineSet.closestLineSegmentDistance(p);//点pに最も近い線分(折線と補助活線)の番号での、その距離を返す
                double re_min = d.foldLineSet.closestCircleDistance(p);//点pに最も近い円の番号での、その距離を返す	public double mottomo_tikai_en_kyori(Ten p)

                if (rs_min <= re_min) {
                    if (rs_min < d.selectionDistance) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                        LineSegment closestLineSegment = d.foldLineSet.closestLineSegmentSearchReversedOrder(p);
                        if (closestLineSegment.getColor() == LineColor.CYAN_3) {
                            d.foldLineSet.deleteLineSegment_vertex(closestLineSegment);
                            d.organizeCircles();
                            d.record();
                        }
                    }
                } else {
                    if (re_min < d.selectionDistance) {
                        d.foldLineSet.deleteCircle(d.foldLineSet.closest_circle_search_reverse_order(p));
                        d.organizeCircles();
                        d.record();
                    }
                }
            }

            if (i_removal_mode == 1) { //補助絵線
                double rs_min;
                rs_min = d.auxLines.closestLineSegmentDistance(p);//点pに最も近い補助絵線の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)

                if (rs_min < d.selectionDistance) {
                    LineSegment closestLineSegment = d.foldLineSet.closestLineSegmentSearchReversedOrder(p);

                    d.auxLines.deleteLineSegment_vertex(closestLineSegment);
                    d.record();
                }
            }
        }


        //四角枠内の削除 //p19_1はselectの最初のTen。この条件は最初のTenと最後の点が遠いので、四角を発生させるということ。
        if (selectionStart.distance(p0) > Epsilon.UNKNOWN_1EN6) {
            if ((d.i_foldLine_additional == FoldLineAdditionalInputMode.POLY_LINE_0) || (d.i_foldLine_additional == FoldLineAdditionalInputMode.BOTH_4)) { //折線の削除	//D_nisuru(selectionStart,p0)で折線だけが削除される。
                if (d.deleteInside_foldingLine(selectionStart, p0)) {
                    d.organizeCircles();
                    d.record();
                }
            }


            if (d.i_foldLine_additional == FoldLineAdditionalInputMode.BLACK_LINE_2) {  //Delete only the black polygonal line
                if (d.deleteInside_edge(selectionStart, p0)) {
                    d.organizeCircles();
                    d.record();
                }
            }


            if ((d.i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LIVE_LINE_3) || (d.i_foldLine_additional == FoldLineAdditionalInputMode.BOTH_4)) {  //Auxiliary live line // Currently it is recorded for undo even if it is not deleted 20161218
                if (d.deleteInside_aux(selectionStart, p0)) {
                    d.organizeCircles();
                    d.record();
                }
            }

            if ((d.i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LINE_1) || (d.i_foldLine_additional == FoldLineAdditionalInputMode.BOTH_4)) { //補助絵線	//現状では削除しないときもUNDO用に記録されてしまう20161218
                if (d.deleteInside(selectionStart, p0)) {
                    d.record();
                }
            }

        }

//qqqqqqqqqqqqqqqqqqqqqqqqqqqqq//System.out.println("= ");qqqqq
//check4(Epsilon.UNKNOWN_00001);//D_nisuru0をすると、foldLineSet.D_nisuru0内でresetが実行されるため、check4のやり直しが必要。
        if (d.check1) {
            d.check1();
        }
        if (d.check2) {
            d.check2();
        }
        if (d.check3) {
            d.check3();
        }
        if (d.check4) {
            d.check4();
        }

    }
}
