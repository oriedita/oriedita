package oriedita.editor.action;

import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MouseHandlerDrawCreaseAngleRestricted5 extends BaseMouseHandlerInputRestricted {
    double d_angle_system;

    @Inject
    public MouseHandlerDrawCreaseAngleRestricted5() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_5_37;
    }

    //マウス操作(mouseMode==37　でボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        Point closestPoint = d.getClosestPoint(p);
        if (p.distance(closestPoint) > d.selectionDistance) {
            return;
        }

        LineSegment s1 = new LineSegment(p, closestPoint, d.lineColor);
        s1.setActive(LineSegment.ActiveState.ACTIVE_B_2);

        d.lineStepAdd(s1);
    }

    //マウス操作(mouseMode==37　でドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
        if (d.lineStep.size() == 1) {
            Point syuusei_point = new Point(syuusei_point_A_37(p0));

            d.lineStep.get(0).setA(syuusei_point);

            if (d.gridInputAssist) {
                d.lineCandidate.clear();
                LineSegment candidate = new LineSegment(kouho_point_A_37(syuusei_point), kouho_point_A_37(syuusei_point), d.lineColor);
                candidate.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);

                d.lineCandidate.add(candidate);
                d.lineStep.get(0).setA(kouho_point_A_37(syuusei_point));
            }
        }
    }

    //マウス操作(mouseMode==37　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.lineStep.size() == 1) {
            Point syuusei_point = new Point(syuusei_point_A_37(p0));
            d.lineStep.get(0).setA(kouho_point_A_37(syuusei_point));
            if (Epsilon.high.gt0(d.lineStep.get(0).determineLength())) {
                d.addLineSegment(d.lineStep.get(0));
                d.record();
            }

            d.lineStep.clear();
        }
    }

    public Point syuusei_point_A_37(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        double d_rad = 0.0;
        d.lineStep.get(0).setA(p);

        if (d.id_angle_system != 0) {
            d_angle_system = 180.0 / (double) d.id_angle_system;
            d_rad = (Math.PI / 180) * d_angle_system * (int) Math.round(OritaCalc.angle(d.lineStep.get(0)) / d_angle_system);
        } else {
            double[] jk = new double[7];
            double currentAngle = OritaCalc.angle(d.lineStep.get(0));
            jk[1] = d.d_restricted_angle_1 - 180.0;
            jk[2] = d.d_restricted_angle_2 - 180.0;
            jk[3] = d.d_restricted_angle_3 - 180.0;
            jk[4] = 360.0 - d.d_restricted_angle_1 - 180.0;
            jk[5] = 360.0 - d.d_restricted_angle_2 - 180.0;
            jk[6] = 360.0 - d.d_restricted_angle_3 - 180.0;

            double d_kakudo_sa_min = 1000.0;
            for (int i = 1; i <= 6; i++) {
                if (Math.min(OritaCalc.angle_between_0_360(jk[i] - currentAngle), OritaCalc.angle_between_0_360(currentAngle - jk[i])) < d_kakudo_sa_min) {
                    d_kakudo_sa_min = Math.min(OritaCalc.angle_between_0_360(jk[i] - currentAngle), OritaCalc.angle_between_0_360(currentAngle - jk[i]));
                    d_rad = (Math.PI / 180) * jk[i];
                }
            }
        }
        LineSegment s = d.getClosestLineSegment(p);
        LineSegment snapLine = new LineSegment(d.lineStep.get(0).getB(), new Point(d.lineStep.get(0).determineBX() + Math.cos(d_rad), d.lineStep.get(0).determineBY() + Math.sin(d_rad)));
        Point pret = OritaCalc.findProjection(snapLine, p);
        if (OritaCalc.determineLineSegmentDistance(p, s) <= d.selectionDistance) {
            if (OritaCalc.isLineSegmentParallel(s, snapLine, Epsilon.PARALLEL) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {
                pret = OritaCalc.findIntersection(s, snapLine);
            }
        }
        return pret;
    }

    // ---
    public Point kouho_point_A_37(Point syuusei_point) {
        Point closestPoint = d.getClosestPoint(syuusei_point);
        double zure_kakudo = OritaCalc.angle(d.lineStep.get(0).getB(), syuusei_point, d.lineStep.get(0).getB(), closestPoint);
        boolean zure_flg = (Epsilon.UNKNOWN_1EN5 < zure_kakudo) && (zure_kakudo <= 360.0 - Epsilon.UNKNOWN_1EN5);
        if (zure_flg || (syuusei_point.distance(closestPoint) > d.selectionDistance)) {
            return syuusei_point;
        } else {//最寄点が角度系にのっていて、修正点とも近い場合
            return closestPoint;
        }
    }
}
