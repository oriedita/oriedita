package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerFishBoneDraw extends BaseMouseHandler{
    private final MouseHandlerDrawCreaseRestricted mouseHandlerDrawCreaseRestricted;

    public MouseHandlerFishBoneDraw(DrawingWorker d) {
        super(d);
        mouseHandlerDrawCreaseRestricted = new MouseHandlerDrawCreaseRestricted(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.FISH_BONE_DRAW_33;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        mouseHandlerDrawCreaseRestricted.mouseMoved(p0);
    }//近い既存点のみ表示

    //マウス操作(mouseMode==33魚の骨　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        d.i_drawing_stage = 1;

        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.closest_point.set(d.getClosestPoint(p));
        if (p.distance(d.closest_point) > d.selectionDistance) {
            d.i_drawing_stage = 0;
        }
        d.line_step[1].set(p, d.closest_point);
        d.line_step[1].setColor(d.lineColor);
    }

    //マウス操作(mouseMode==33魚の骨　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        mouseHandlerDrawCreaseRestricted.mouseDragged(p0);
    }


//35 35 35 35 35 35 35 35 35 35 35複折り返し   入力した線分に接触している折線を折り返し　に使う

    //マウス操作(mouseMode==33魚の骨　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.i_drawing_stage == 1) {
            d.i_drawing_stage = 0;

            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            d.closest_point.set(d.getClosestPoint(p));
            d.line_step[1].setA(d.closest_point);

            if (p.distance(d.closest_point) <= d.selectionDistance) {  //マウスで指定した点が、最寄点と近かったときに実施
                if (d.line_step[1].getLength() > 0.00000001) {  //line_step[1]が、線の時（=点状ではない時）に実施
                    double dx = (d.line_step[1].getAX() - d.line_step[1].getBX()) * d.grid.getGridWidth() / d.line_step[1].getLength();
                    double dy = (d.line_step[1].getAY() - d.line_step[1].getBY()) * d.grid.getGridWidth() / d.line_step[1].getLength();
                    LineColor icol_temp = d.lineColor;

                    Point pxy = new Point();
                    for (int i = 0; i <= (int) Math.floor(d.line_step[1].getLength() / d.grid.getGridWidth()); i++) {
                        double px = d.line_step[1].getBX() + (double) i * dx;
                        double py = d.line_step[1].getBY() + (double) i * dy;
                        pxy.set(px, py);


                        if (d.foldLineSet.closestLineSegmentDistanceExcludingParallel(pxy, d.line_step[1]) > 0.001) {

                            int i_sen = 0;

                            LineSegment adds = new LineSegment(px, py, px - dy, py + dx);
                            if (d.kouten_ari_nasi(adds) == 1) {
                                adds.set(d.extendToIntersectionPoint(adds));
                                adds.setColor(icol_temp);

                                d.addLineSegment(adds);
                                i_sen++;
                            }


                            LineSegment adds2 = new LineSegment(px, py, px + dy, py - dx);
                            if (d.kouten_ari_nasi(adds2) == 1) {
                                adds2.set(d.extendToIntersectionPoint(adds2));
                                adds2.setColor(icol_temp);

                                d.addLineSegment(adds2);
                                i_sen++;
                            }

                            if (i_sen == 2) {
                                d.foldLineSet.del_V(pxy, d.selectionDistance, 0.000001);
                            }

                        }

                        if (icol_temp == LineColor.RED_1) {
                            icol_temp = LineColor.BLUE_2;
                        } else if (icol_temp == LineColor.BLUE_2) {
                            icol_temp = LineColor.RED_1;
                        }
                    }
                    d.record();
                }  //line_step[1]が、線の時（=点状ではない時）に実施は、ここまで
            }  //マウスで指定した点が、最寄点と近かったときに実施は、ここまで
        }
    }
}
