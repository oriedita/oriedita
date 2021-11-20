package origami_editor.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;

import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;
import origami_editor.editor.MouseMode;
import origami_editor.editor.databinding.BackgroundModel;
import origami_editor.editor.service.ButtonService;


@Singleton
public class MouseHandlerBackgroundChangePosition extends BaseMouseHandler {
    private final ButtonService buttonService;
    private final BackgroundModel backgroundModel;

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.BACKGROUND_CHANGE_POSITION_26;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        if (d.lineStep.size() == 3) {
            Point closestPoint = d.getClosestPoint(p);

            if (p.distance(closestPoint) < d.selectionDistance) {
                p.set(closestPoint);
            }
            d.lineStepAdd(new LineSegment(p, p, LineColor.ORANGE_4));
        } else if (d.lineStep.size() == 2) {
            Point closestPoint = d.getClosestPoint(p);

            if (p.distance(closestPoint) < d.selectionDistance) {
                p.set(closestPoint);
            }
            d.lineStepAdd(new LineSegment(p, p, LineColor.CYAN_3));
        } else if (d.lineStep.size() == 1) {
            d.lineStepAdd(new LineSegment(p, p, LineColor.BLUE_2));
        } else if (d.lineStep.size() == 0) {
            d.lineStepAdd(new LineSegment(p, p, LineColor.RED_1));
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    @Inject
    public MouseHandlerBackgroundChangePosition(ButtonService buttonService, BackgroundModel backgroundModel) {
        this.buttonService = buttonService;
        this.backgroundModel = backgroundModel;
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.lineStep.size() == 4) {
            LineSegment s_1 = new LineSegment();
            s_1.set(d.lineStep.get(0));
            LineSegment s_2 = new LineSegment();
            s_2.set(d.lineStep.get(1));
            LineSegment s_3 = new LineSegment();
            s_3.set(d.lineStep.get(2));
            LineSegment s_4 = new LineSegment();
            s_4.set(d.lineStep.get(3));
            buttonService.Button_shared_operation();

            backgroundModel.setLockBackground(false);

            Polygon polygon = new Polygon(d.camera.object2TV(s_1.getA()),
                    d.camera.object2TV(s_2.getA()),
                    d.camera.object2TV(s_3.getA()),
                    d.camera.object2TV(s_4.getA()));

            backgroundModel.setBackgroundPosition(polygon);
        }
    }
}
