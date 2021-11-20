package origami_editor.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;
import origami_editor.editor.canvas.CreasePattern_Worker;
import origami_editor.editor.databinding.MeasuresModel;

@Singleton
public class MouseHandlerDisplayLengthBetweenPoints1 extends BaseMouseHandlerInputRestricted {
    private final MeasuresModel measuresModel;

    @Inject
    public MouseHandlerDisplayLengthBetweenPoints1(CreasePattern_Worker d, MeasuresModel measuresModel) {
        this.d = d;
        this.measuresModel = measuresModel;
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_1_53;
    }

    //Work when operating the mouse (when the button is pressed)
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
        if (d.lineStep.size() == 2) {
            measuresModel.setMeasuredLength1(OritaCalc.distance(d.lineStep.get(0).getA(), d.lineStep.get(1).getA()) * (double) d.grid.getGridSize() / 400.0);
            d.lineStep.clear();
        }
    }
}
