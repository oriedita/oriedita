package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.BackgroundModel;
import oriedita.editor.service.ResetService;
import origami.crease_pattern.element.Box;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;


@ApplicationScoped
@Handles(MouseMode.BACKGROUND_CHANGE_POSITION_26)
public class MouseHandlerBackgroundChangePosition extends BaseMouseHandler {
    private final ResetService resetService;
    private final BackgroundModel backgroundModel;

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);

        if (d.getLineStep().size() == 3) {
            Point closestPoint = d.getClosestPoint(p);

            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                p = closestPoint;
            }
            d.lineStepAdd(new LineSegment(p, p, LineColor.ORANGE_4));
        } else if (d.getLineStep().size() == 2) {
            Point closestPoint = d.getClosestPoint(p);

            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                p = closestPoint;
            }
            d.lineStepAdd(new LineSegment(p, p, LineColor.CYAN_3));
        } else if (d.getLineStep().size() == 1) {
            d.lineStepAdd(new LineSegment(p, p, LineColor.BLUE_2));
        } else if (d.getLineStep().size() == 0) {
            d.lineStepAdd(new LineSegment(p, p, LineColor.RED_1));
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    @Inject
    public MouseHandlerBackgroundChangePosition(ResetService resetService, BackgroundModel backgroundModel) {
        this.resetService = resetService;
        this.backgroundModel = backgroundModel;
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.getLineStep().size() == 4) {
            LineSegment s_1 = d.getLineStep().get(0);
            LineSegment s_2 = d.getLineStep().get(1);
            LineSegment s_3 = d.getLineStep().get(2);
            LineSegment s_4 = d.getLineStep().get(3);
            resetService.Button_shared_operation();

            backgroundModel.setLockBackground(false);

            Polygon polygon = new Box(d.getCamera().object2TV(s_1.getA()),
                    d.getCamera().object2TV(s_2.getA()),
                    d.getCamera().object2TV(s_3.getA()),
                    d.getCamera().object2TV(s_4.getA()));

            backgroundModel.setBackgroundPosition(polygon);
        }
    }
}
