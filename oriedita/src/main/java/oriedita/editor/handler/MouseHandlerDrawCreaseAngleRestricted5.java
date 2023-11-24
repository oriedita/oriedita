package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.tools.SnappingUtil;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_5_37)
public class MouseHandlerDrawCreaseAngleRestricted5 extends BaseMouseHandlerInputRestricted {
    private final AngleSystemModel angleSystemModel;
    Point start;

    @Inject
    public MouseHandlerDrawCreaseAngleRestricted5(AngleSystemModel angleSystemModel) {
        this.angleSystemModel = angleSystemModel;
    }

    //マウス操作(mouseMode==37　でボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        start = d.getClosestPoint(p);
        if (p.distance(start) > d.getSelectionDistance()) {
            return;
        }

        LineSegment s1 = new LineSegment(p, start, d.getLineColor());
        s1.setActive(LineSegment.ActiveState.ACTIVE_B_2);

        d.lineStepAdd(s1);
    }

    //マウス操作(mouseMode==37　でドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
        if (d.getLineStep().size() == 1) {
            Point syuusei_point = new Point(syuusei_point_A_37(p0));

            d.getLineStep().get(0).setA(syuusei_point);
            d.getLineStep().get(0).setColor(d.getLineColor());

            if (d.getGridInputAssist()) {
                d.getLineCandidate().clear();
                LineSegment candidate = new LineSegment(kouho_point_A_37(syuusei_point), kouho_point_A_37(syuusei_point), d.getLineColor());
                candidate.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);

                d.getLineCandidate().add(candidate);
                d.getLineStep().get(0).setA(kouho_point_A_37(syuusei_point));
            }
        }
    }

    //マウス操作(mouseMode==37　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.getLineStep().size() == 1) {
            Point syuusei_point = new Point(syuusei_point_A_37(p0));
            d.getLineStep().get(0).setA(kouho_point_A_37(syuusei_point));
            if (Epsilon.high.gt0(d.getLineStep().get(0).determineLength())) {
                d.addLineSegment(d.getLineStep().get(0));
                d.record();
            }

            d.getLineStep().clear();
        }
    }

    public Point syuusei_point_A_37(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        return SnappingUtil.snapToActiveAngleSystem(d, start, p, angleSystemModel.getCurrentAngleSystemDivider(), angleSystemModel.getAngles());
    }

    // ---
    public Point kouho_point_A_37(Point syuusei_point) {
        Point closestPoint = d.getClosestPoint(syuusei_point);
        double zure_kakudo = OritaCalc.angle(d.getLineStep().get(0).getB(), syuusei_point, d.getLineStep().get(0).getB(), closestPoint);
        boolean zure_flg = (Epsilon.UNKNOWN_1EN5 < zure_kakudo) && (zure_kakudo <= 360.0 - Epsilon.UNKNOWN_1EN5);
        if (zure_flg || (syuusei_point.distance(closestPoint) > d.getSelectionDistance())) {
            return syuusei_point;
        } else {//最寄点が角度系にのっていて、修正点とも近い場合
            return closestPoint;
        }
    }
}
