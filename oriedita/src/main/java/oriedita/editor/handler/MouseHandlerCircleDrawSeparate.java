package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.CIRCLE_DRAW_SEPARATE_44)
public class MouseHandlerCircleDrawSeparate extends BaseMouseHandler {
    @Inject
    public MouseHandlerCircleDrawSeparate() {
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==44 円 分離入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        Point closest_point = d.getClosestPoint(p);

        if (d.getLineStep().size() == 0) {
            d.getCircleStep().clear();
            if (p.distance(closest_point) > d.getSelectionDistance()) {
                return;
            }

            d.lineStepAdd(new LineSegment(closest_point, closest_point, LineColor.CYAN_3));
        } else if (d.getLineStep().size() == 1) {
            if (p.distance(closest_point) > d.getSelectionDistance()) {
                return;
            }

            d.lineStepAdd(new LineSegment(p, closest_point, LineColor.CYAN_3));

            d.getCircleStep().clear();
            d.getCircleStep().add(new Circle(d.getLineStep().get(0).getA(), 0.0, LineColor.CYAN_3));
        }
    }

    //マウス操作(mouseMode==44 円 分離入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        if (d.getLineStep().size() == 2) {
            d.getLineStep().set(1, d.getLineStep().get(1).withA(p));
            d.getCircleStep().get(0).setR(d.getLineStep().get(0).determineLength());
        }
    }

    //マウス操作(mouseMode==44 円 分離入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.getLineStep().size() == 2) {
            d.getCircleStep().clear();

            Point p = d.getCamera().TV2object(p0);
            Point closest_point = d.getClosestPoint(p);
            d.getLineStep().set(1, d.getLineStep().get(1).withA(closest_point));
            if (p.distance(closest_point) <= d.getSelectionDistance()) {
                if (Epsilon.high.gt0(d.getLineStep().get(1).determineLength())) {
                    d.addLineSegment(d.getLineStep().get(1));
                    d.addCircle(d.getLineStep().get(0).getA(), d.getLineStep().get(1).determineLength(), LineColor.CYAN_3);
                    d.record();
                }
            }

            d.getLineStep().clear();
        }
    }
}
