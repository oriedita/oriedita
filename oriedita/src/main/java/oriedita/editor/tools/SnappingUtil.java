package oriedita.editor.tools;

import oriedita.editor.canvas.CreasePattern_Worker;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

public class SnappingUtil {
    public static Point snapToActiveAngleSystem(CreasePattern_Worker d, Point start, Point p, int angleSystemDivider, double[] angles) {
        double d_rad = 0.0;
        LineSegment s = new LineSegment(p, start);
        double d_angle_system;
        if (angleSystemDivider != 0) {
            d_angle_system = 180.0 / (double) angleSystemDivider;
            d_rad = (Math.PI / 180) * d_angle_system * (int) Math.round(OritaCalc.angle(s) / d_angle_system);
        } else {
            double currentAngle = OritaCalc.angle(s);

            double d_kakudo_sa_min = 1000.0;
            for (int i = 0; i < 6; i++) {
                double angle = angles[i] - 180.0;
                if (Math.min(OritaCalc.angle_between_0_360(angle - currentAngle), OritaCalc.angle_between_0_360(currentAngle - angle)) < d_kakudo_sa_min) {
                    d_kakudo_sa_min = Math.min(OritaCalc.angle_between_0_360(angle - currentAngle), OritaCalc.angle_between_0_360(currentAngle - angle));
                    d_rad = (Math.PI / 180) * angle;
                }
            }
        }
        LineSegment s2 = d.getClosestLineSegment(p);
        LineSegment snapLine = new LineSegment(s.getB(), new Point(s.determineBX() + Math.cos(d_rad), s.determineBY() + Math.sin(d_rad)));
        Point pret = OritaCalc.findProjection(snapLine, p);
        if (OritaCalc.determineLineSegmentDistance(p, s2) <= d.getSelectionDistance()) {
            if (OritaCalc.isLineSegmentParallel(s2, snapLine, Epsilon.PARALLEL_FOR_FIX) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {
                pret = OritaCalc.findIntersection(s2, snapLine);
            }
        }
        return pret;
    }

    public static Point snapToClosePointInActiveAngleSystem(CreasePattern_Worker d, Point start, Point p, int angleSystemDivider, double[] angles) {
        Point syuusei_point = snapToActiveAngleSystem(d, start, p, angleSystemDivider, angles);
        Point closestPoint = d.getClosestPoint(syuusei_point);
        double zure_kakudo = OritaCalc.angle(start, syuusei_point, start, closestPoint);
        boolean zure_flg = (Epsilon.UNKNOWN_1EN5 < zure_kakudo) && (zure_kakudo <= 360.0 - Epsilon.UNKNOWN_1EN5);
        if (zure_flg || (syuusei_point.distance(closestPoint) > d.getSelectionDistance())) {
            return syuusei_point;
        } else {//最寄点が角度系にのっていて、修正点とも近い場合
            return closestPoint;
        }
    }
}
