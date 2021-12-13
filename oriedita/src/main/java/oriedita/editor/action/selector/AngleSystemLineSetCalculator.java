package oriedita.editor.action.selector;

import oriedita.editor.action.selector.drawing.DrawingSettings;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Calculates all outgoing lines from the point selected by the pointSelector, according to the currently
 * active angle system in d, and rotated so that the line from first to the selected point is at 0 degrees
 */
public class AngleSystemLineSetCalculator extends CalculatedElementSelector<Point, List<LineSegment>> {
    private final Supplier<Point> first;
    private final LineColor[] customAngleColors = new LineColor[] {
            LineColor.ORANGE_4,
            LineColor.GREEN_6,
            LineColor.PURPLE_8
    };

    public AngleSystemLineSetCalculator(Supplier<Point> firstPointSupplier, CreasePatternPointSelector base) {
        super(base, true);
        this.first = firstPointSupplier;
    }

    private List<LineSegment> makePreviewLines(Point pStart, Point pEnd) {
        List<LineSegment> candidates = new ArrayList<>();
        int numPreviewLines;//1つの端点周りに描く線の本数
        if (d.id_angle_system != 0) {
            numPreviewLines = d.id_angle_system * 2 - 1;
        } else {
            numPreviewLines = 6;
        }

        //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d)

        LineSegment startingSegment = new LineSegment(pEnd, pStart);
        startingSegment.setColor(LineColor.GREEN_6);
        candidates.add(startingSegment);

        if (d.id_angle_system != 0) {

            double angle = 0.0;
            double angleStep = 180.0 / d.id_angle_system;
            for (int i = 0; i < numPreviewLines; i++) {
                angle += angleStep;
                LineSegment e = OritaCalc.lineSegment_rotate(startingSegment, angle, 1.0);
                if (i % 2 == 0) {
                    e.setColor(LineColor.ORANGE_4);
                } else {
                    e.setColor(LineColor.GREEN_6);
                }
                e.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
                candidates.add(e);
            }
        } else {
            double[] angles = new double[] {
                    d.d_restricted_angle_1,
                    d.d_restricted_angle_2,
                    d.d_restricted_angle_3,
                    360 - d.d_restricted_angle_1,
                    360 - d.d_restricted_angle_2,
                    360 - d.d_restricted_angle_3
            };

            for (int i = 0; i < 6; i++) {
                LineSegment s = new LineSegment();
                s.set(OritaCalc.lineSegment_rotate(startingSegment, angles[i], 1.0));
                s.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
                candidates.add(s);
                s.setColor(customAngleColors[i%3]);
            }
        }
        return candidates;
    }

    @Override
    protected List<LineSegment> calculate(Point baseSelected) {
        return makePreviewLines(first.get(), baseSelected);
    }

    @Override
    public void draw(List<LineSegment> calculated, Graphics2D g2, Camera camera, DrawingSettings settings) {
        for (LineSegment lineSegment : calculated) {
            DrawingUtil.drawLineStep(g2, lineSegment, camera, settings);
        }
    }
}
