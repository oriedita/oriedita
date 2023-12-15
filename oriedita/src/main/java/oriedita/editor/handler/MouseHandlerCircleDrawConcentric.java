package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.MouseHandlerDrawingHelper;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;

@ApplicationScoped
@Handles(MouseMode.CIRCLE_DRAW_CONCENTRIC_48)
public class MouseHandlerCircleDrawConcentric extends BaseMouseHandler {
    private LineSegment radiusDifference;
    private Circle originalCircle;
    private Circle newCircle;

    @Inject
    public MouseHandlerCircleDrawConcentric() {
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    @Override
    public void reset() {
        super.reset();
        originalCircle = null;
        radiusDifference = null;
        newCircle = null;
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        MouseHandlerDrawingHelper helper = new MouseHandlerDrawingHelper(g2, settings, camera, d.getGridInputAssist());
        helper.drawLineStep(radiusDifference);
        helper.drawCircle(originalCircle);
        helper.drawCircle(newCircle);
    }

    //マウス操作(mouseMode==48 同心円　線分入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        Circle closest_circumference = new Circle(d.getClosestCircleMidpoint(p)); //Circle with the circumference closest to the mouse
        Point closestPoint = d.getClosestPoint(p);

        if ((originalCircle == null) && (radiusDifference == null)) {
            if (OritaCalc.distance_circumference(p, closest_circumference) > d.getSelectionDistance()) {
                return;
            }
            originalCircle = new Circle(
                    closest_circumference.determineCenter(),
                    closest_circumference.getR(),
                    LineColor.GREEN_6);
            return;
        }

        if ((originalCircle != null) && (radiusDifference == null)) {
            if (p.distance(closestPoint) > d.getSelectionDistance()) {
                return;
            }

            radiusDifference = new LineSegment(p, closestPoint, LineColor.CYAN_3);
            newCircle = new Circle(
                    originalCircle.determineCenter(),
                    originalCircle.getR(),
                    LineColor.GREEN_6);
        }
    }

    //マウス操作(mouseMode==48 同心円　線分入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        if (radiusDifference != null && newCircle != null) {
            radiusDifference = radiusDifference.withA(p);
            newCircle.setR(originalCircle.getR() + radiusDifference.determineLength());
        }
    }

    //マウス操作(mouseMode==48 同心円　線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if ((radiusDifference != null) && (newCircle != null)) {
            Circle circle1 = originalCircle;
            Circle circle2 = newCircle;

            Point p = d.getCamera().TV2object(p0);
            Point closestPoint = d.getClosestPoint(p);
            radiusDifference = radiusDifference.withA(closestPoint);
            if (p.distance(closestPoint) <= d.getSelectionDistance()) {
                if (Epsilon.high.gt0(radiusDifference.determineLength())) {
                    d.addLineSegment(radiusDifference);
                    circle2.setR(circle1.getR() + radiusDifference.determineLength());
                    d.addCircle(circle2);
                    d.record();
                }
            }
            reset();
        }
    }
}
