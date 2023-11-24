package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.MeasuresModel;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_2_56)
public class MouseHandlerDisplayAngleBetweenThreePoints2 extends BaseMouseHandlerInputRestricted {
    private final CreasePattern_Worker d;
    private final MeasuresModel measuresModel;

    @Inject
    public MouseHandlerDisplayAngleBetweenThreePoints2(@Named("mainCreasePattern_Worker") CreasePattern_Worker d, MeasuresModel measuresModel) {
        this.d = d;
        this.measuresModel = measuresModel;
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        Point closest_point = d.getClosestPoint(p);
        if (p.distance(closest_point) < d.getSelectionDistance()) {
            d.lineStepAdd(new LineSegment(closest_point, closest_point, d.getLineColor()));
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.getLineStep().size() == 3) {
            measuresModel.setMeasuredAngle2(OritaCalc.angle(d.getLineStep().get(1).getA(), d.getLineStep().get(2).getA(), d.getLineStep().get(1).getA(), d.getLineStep().get(0).getA()));
            d.getLineStep().clear();
        }
    }
}
