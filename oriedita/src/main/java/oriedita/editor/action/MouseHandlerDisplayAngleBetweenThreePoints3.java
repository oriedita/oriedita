package oriedita.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.databinding.MeasuresModel;

@Singleton
public class MouseHandlerDisplayAngleBetweenThreePoints3 extends BaseMouseHandlerInputRestricted {
    private final CreasePattern_Worker d;
    private final MeasuresModel measuresModel;

    @Inject
    public MouseHandlerDisplayAngleBetweenThreePoints3(CreasePattern_Worker d, MeasuresModel measuresModel) {
        this.d = d;
        this.measuresModel = measuresModel;
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_3_57;
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        Point closest_point = d.getClosestPoint(p);
        if (p.distance(closest_point) < d.selectionDistance) {
            d.lineStepAdd(new LineSegment(closest_point, closest_point, d.lineColor));
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.lineStep.size() == 3) {
            measuresModel.setMeasuredAngle3(OritaCalc.angle(d.lineStep.get(1).getA(), d.lineStep.get(2).getA(), d.lineStep.get(1).getA(), d.lineStep.get(0).getA()));
            d.lineStep.clear();
        }
    }
}
