package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerFishBoneDraw extends BaseMouseHandlerInputRestricted {
    private final MouseHandlerDrawCreaseRestricted mouseHandlerDrawCreaseRestricted;

    public MouseHandlerFishBoneDraw(DrawingWorker d) {
        super(d);
        mouseHandlerDrawCreaseRestricted = new MouseHandlerDrawCreaseRestricted(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.FISH_BONE_DRAW_33;
    }

    //マウス操作(mouseMode==33魚の骨　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        d.lineStep.clear();

        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        Point closest_point = d.getClosestPoint(p);
        if (p.distance(closest_point) > d.selectionDistance) {
            return;
        }
        d.lineStepAdd(new LineSegment(p, closest_point, d.lineColor));
    }

    //マウス操作(mouseMode==33魚の骨　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        mouseHandlerDrawCreaseRestricted.mouseDragged(p0);
    }


//35 35 35 35 35 35 35 35 35 35 35複折り返し   入力した線分に接触している折線を折り返し　に使う

    //マウス操作(mouseMode==33魚の骨　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.lineStep.size() == 1) {
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            Point closest_point = d.getClosestPoint(p);
            d.lineStep.get(0).setA(closest_point);

            if (p.distance(closest_point) <= d.selectionDistance) {  //マウスで指定した点が、最寄点と近かったときに実施
                if (d.lineStep.get(0).determineLength() > 0.00000001) {  //lineStep.get(0)が、線の時（=点状ではない時）に実施
                    double dx = (d.lineStep.get(0).determineAX() - d.lineStep.get(0).determineBX()) * d.grid.getGridWidth() / d.lineStep.get(0).determineLength();
                    double dy = (d.lineStep.get(0).determineAY() - d.lineStep.get(0).determineBY()) * d.grid.getGridWidth() / d.lineStep.get(0).determineLength();
                    LineColor icol_temp = d.lineColor;

                    Point pxy = new Point();
                    for (int i = 0; i <= (int) Math.floor(d.lineStep.get(0).determineLength() / d.grid.getGridWidth()); i++) {
                        double px = d.lineStep.get(0).determineBX() + (double) i * dx;
                        double py = d.lineStep.get(0).determineBY() + (double) i * dy;
                        pxy.set(px, py);


                        if (d.foldLineSet.closestLineSegmentDistanceExcludingParallel(pxy, d.lineStep.get(0)) > 0.001) {

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
                }  //lineStep.get(0)が、線の時（=点状ではない時）に実施は、ここまで
            }  //マウスで指定した点が、最寄点と近かったときに実施は、ここまで
            d.lineStep.clear();
        }
    }
}
