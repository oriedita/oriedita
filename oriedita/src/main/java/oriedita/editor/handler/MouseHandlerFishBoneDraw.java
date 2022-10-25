package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.StraightLine;

@ApplicationScoped
@Handles(MouseMode.FISH_BONE_DRAW_33)
public class MouseHandlerFishBoneDraw extends BaseMouseHandlerInputRestricted {
    private final MouseHandlerDrawCreaseRestricted mouseHandlerDrawCreaseRestricted;

    @Inject
    public MouseHandlerFishBoneDraw(@Handles(MouseMode.DRAW_CREASE_RESTRICTED_11) MouseHandlerDrawCreaseRestricted mouseHandlerDrawCreaseRestricted) {
        this.mouseHandlerDrawCreaseRestricted = mouseHandlerDrawCreaseRestricted;
    }

    //マウス操作(mouseMode==33魚の骨　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        d.getLineStep().clear();

        Point p = new Point();
        p.set(d.getCamera().TV2object(p0));
        Point closest_point = d.getClosestPoint(p);
        if (p.distance(closest_point) > d.getSelectionDistance()) {
            return;
        }
        d.lineStepAdd(new LineSegment(p, closest_point, d.getLineColor()));
    }

    //マウス操作(mouseMode==33魚の骨　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        mouseHandlerDrawCreaseRestricted.mouseDragged(p0);
    }


//35 35 35 35 35 35 35 35 35 35 35複折り返し   入力した線分に接触している折線を折り返し　に使う

    //マウス操作(mouseMode==33魚の骨　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.getLineStep().size() == 1) {
            Point p = new Point();
            p.set(d.getCamera().TV2object(p0));
            Point closest_point = d.getClosestPoint(p);
            d.getLineStep().get(0).setA(closest_point);

            if (p.distance(closest_point) <= d.getSelectionDistance()) {  //マウスで指定した点が、最寄点と近かったときに実施
                if (Epsilon.high.gt0(d.getLineStep().get(0).determineLength())) {  //lineStep.get(0)が、線の時（=点状ではない時）に実施
                    double dx = (d.getLineStep().get(0).determineAX() - d.getLineStep().get(0).determineBX()) * d.getGrid().getGridWidth() / d.getLineStep().get(0).determineLength();
                    double dy = (d.getLineStep().get(0).determineAY() - d.getLineStep().get(0).determineBY()) * d.getGrid().getGridWidth() / d.getLineStep().get(0).determineLength();
                    LineColor icol_temp = d.getLineColor();

                    Point pxy = new Point();
                    for (int i = 0; i <= (int) Math.floor(d.getLineStep().get(0).determineLength() / d.getGrid().getGridWidth()); i++) {
                        double px = d.getLineStep().get(0).determineBX() + (double) i * dx;
                        double py = d.getLineStep().get(0).determineBY() + (double) i * dy;
                        pxy.set(px, py);


                        if (d.getFoldLineSet().closestLineSegmentDistanceExcludingParallel(pxy, d.getLineStep().get(0)) > Epsilon.UNKNOWN_0001) {

                            int i_sen = 0;

                            LineSegment adds = new LineSegment(px, py, px - dy, py + dx);
                            if (kouten_ari_nasi(adds) == 1) {
                                adds.set(d.extendToIntersectionPoint(adds));
                                adds.setColor(icol_temp);

                                d.addLineSegment(adds);
                                i_sen++;
                            }


                            LineSegment adds2 = new LineSegment(px, py, px + dy, py - dx);
                            if (kouten_ari_nasi(adds2) == 1) {
                                adds2.set(d.extendToIntersectionPoint(adds2));
                                adds2.setColor(icol_temp);

                                d.addLineSegment(adds2);
                                i_sen++;
                            }

                            if (i_sen == 2) {
                                d.getFoldLineSet().del_V(pxy, d.getSelectionDistance(), Epsilon.UNKNOWN_1EN6);
                            }

                        }

                        if (icol_temp == LineColor.RED_1) {
                            icol_temp = LineColor.BLUE_2;
                        } else if (icol_temp == LineColor.BLUE_2) {
                            icol_temp = LineColor.RED_1;
                        }
                    }
                    d.record();
                }  //lineStep.get(0)が、線の時（=点状ではない時）に実施は、ここまで
            }  //マウスで指定した点が、最寄点と近かったときに実施は、ここまで
            d.getLineStep().clear();
        }
    }

    public int kouten_ari_nasi(LineSegment s0) {//If s0 is extended from the point a to the b direction and intersects with another polygonal line, 0 is returned if it is not 1. The intersecting line segments at the a store have no intersection with this function.
        LineSegment add_line = new LineSegment();
        add_line.set(s0);
        Point intersection_point = new Point(1000000.0, 1000000.0); //この方法だと、エラーの原因になりうる。本当なら全線分のx_max、y_max以上の点を取ればいい。今後修正予定20161120

        StraightLine tyoku1 = new StraightLine(add_line.getA(), add_line.getB());
        StraightLine.Intersection i_intersection_flg;
        for (int i = 1; i <= d.getFoldLineSet().getTotal(); i++) {
            i_intersection_flg = tyoku1.lineSegment_intersect_reverse_detail(d.getFoldLineSet().get(i));//0 = This straight line does not intersect a given line segment, 1 = X type intersects, 2 = T type intersects, 3 = Line segment is included in the straight line.

            if (i_intersection_flg.isIntersecting()) {
                intersection_point.set(OritaCalc.findIntersection(tyoku1, d.getFoldLineSet().get(i)));
                if (intersection_point.distance(add_line.getA()) > Epsilon.UNKNOWN_1EN5) {
                    double d_kakudo = OritaCalc.angle(add_line.getA(), add_line.getB(), add_line.getA(), intersection_point);
                    if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                        return 1;

                    }

                }
            }
        }
        return 0;
    }

}
