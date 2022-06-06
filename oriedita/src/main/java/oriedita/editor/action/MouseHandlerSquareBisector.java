package oriedita.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import oriedita.editor.canvas.MouseMode;

@Singleton
public class MouseHandlerSquareBisector extends BaseMouseHandlerInputRestricted {
    @Inject
    public MouseHandlerSquareBisector() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.SQUARE_BISECTOR_7;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        if (d.lineStep.size() <= 2) {
            //Only close existing points are displayed
            super.mouseMoved(p0);
        }
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = new Point();
        LineSegment line = new LineSegment();
        p.set(d.camera.TV2object(p0));

        // If condition is for 2 lines bisect
        if((d.lineStep.isEmpty() || d.lineStep.get(0).determineLength() > 0)){
            // Click 2 lines to form bisect and then a destination line
            if(d.lineStep.size() == 0 && d.getClosestPoint(p).distance(p) > d.selectionDistance) {
                line.set(d.getClosestLineSegment(p));
                if (OritaCalc.determineLineSegmentDistance(p, line) < d.selectionDistance) {
                    line.setColor(LineColor.GREEN_6);
                    d.lineStepAdd(line);
                }
                return;
            }
            if(d.lineStep.size() >= 1) {
                line.set(d.getClosestLineSegment(p));
                if (OritaCalc.determineLineSegmentDistance(p, line) < d.selectionDistance) {
                    line.setColor(LineColor.GREEN_6);
                    d.lineStepAdd(line);
                }
                return;
            }
        }
        // Else if condition is for 3 points bisect
        if (d.lineStep.isEmpty() || d.lineStep.get(0).determineLength() <= 0.0){
            if (d.lineStep.size() <= 2) {
                Point closestPoint = d.getClosestPoint(p);
                if (p.distance(closestPoint) < d.selectionDistance) {
                    d.lineStepAdd(new LineSegment(closestPoint, closestPoint, d.lineColor));
                }
            } else if (d.lineStep.size() == 3) {
                LineSegment closestLineSegment = new LineSegment();
                closestLineSegment.set(d.getClosestLineSegment(p));
                if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.selectionDistance) {
                    closestLineSegment.setColor(LineColor.GREEN_6);
                    d.lineStepAdd(closestLineSegment);
                }
            }
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        // Calculation for 3 points
        if (d.lineStep.size() == 4 && d.lineStep.get(0).determineLength() < Epsilon.UNKNOWN_1EN4) {
            //三角形の内心を求める    public Ten oc.naisin(Ten ta,Ten tb,Ten tc)
            Point naisin = new Point();
            naisin.set(OritaCalc.center(d.lineStep.get(0).getA(), d.lineStep.get(1).getA(), d.lineStep.get(2).getA()));

            LineSegment add_sen2 = new LineSegment(d.lineStep.get(1).getA(), naisin);

            //add_sen2とs_step[4]の交点はoc.kouten_motome(Senbun s1,Senbun s2)で求める//２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
            Point cross_point = new Point();
            cross_point.set(OritaCalc.findIntersection(add_sen2, d.lineStep.get(3)));

            LineSegment add_sen = new LineSegment(cross_point, d.lineStep.get(1).getA(), d.lineColor);
            if (Epsilon.high.gt0(add_sen.determineLength())) {
                d.addLineSegment(add_sen);
                d.record();
            }

            d.lineStep.clear();
        }
        // Calculation for 2 lines
        else if (d.lineStep.size() == 3 && d.lineStep.get(0).determineLength() > 0){
            // Find intersection of 2 lines
            Point intersection = new Point();
            intersection.set(OritaCalc.findIntersection(d.lineStep.get(0), d.lineStep.get(1)));

            // Find another point by taking the center point of 3 points
            /* 2 points that are not the intersection have to be far away from said intersection
             * to prevent them from being the intersection themselves, which can cause problems when
             * finding the triangle center.
             */
            Point center = new Point();
            center.set(OritaCalc.center(intersection, d.lineStep.get(0).determineFurthestEndpoint(intersection), d.lineStep.get(1).determineFurthestEndpoint(intersection)));

            // Make a temporary line to connect intersection and center
            LineSegment tempBisect = new LineSegment(intersection, center);

            // Find intersection of temp line to the destination line
            Point cross_point = new Point();
            cross_point.set(OritaCalc.findIntersection(tempBisect, d.lineStep.get(2)));

            // Draw the bisector
            LineSegment destinationLine = new LineSegment(cross_point, intersection, d.lineColor);
            if (Epsilon.high.gt0(destinationLine.determineLength())) {
                d.addLineSegment(destinationLine);
                d.record();
            }

            d.lineStep.clear();
        }
    }
}

