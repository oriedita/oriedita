package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.*;
import java.util.List;
import java.util.*;

@ApplicationScoped
@Handles(MouseMode.ANGLE_SYSTEM_16)
public class MouseHandlerAngleSystem extends BaseMouseHandlerInputRestricted {
    private final AngleSystemModel angleSystemModel;
    Point pStart, pEnd;
    List<LineSegment> candidates = new ArrayList<>();
    LineSegment direction;
    LineSegment previewLine;
    Step currentStep = Step.SELECT_FIRST_POINT;
    LineColor[] customAngleColors = new LineColor[]{
            LineColor.ORANGE_4,
            LineColor.GREEN_6,
            LineColor.PURPLE_8
    };

    enum Step {
        SELECT_FIRST_POINT,
        SELECT_SECOND_POINT,
        SELECT_DIRECTION,
        SELECT_LENGTH
    }

    @Inject
    public MouseHandlerAngleSystem(AngleSystemModel angleSystemModel) {
        this.angleSystemModel = angleSystemModel;
    }

    @Override
    public EnumSet<Feature> getSubscribedFeatures() {
        return EnumSet.of(Feature.BUTTON_1);
    }

    public void mouseMoved(Point p0) {
        super.mouseMoved(p0);
        Point p = d.getCamera().TV2object(p0);
        switch (currentStep) {
            case SELECT_FIRST_POINT:
                pStart = d.getClosestPoint(p);
                break;
            case SELECT_SECOND_POINT:
                pEnd = determinePEnd(p);
                candidates = makePreviewLines(pStart, pEnd);
                break;
            case SELECT_DIRECTION:
                direction = determineSelectedCandidate(p);
                break;
            case SELECT_LENGTH:
                previewLine = determineLineSegmentForPreview(p);
                break;
        }
    }

    private Point determinePEnd(Point p) {
        Point previewPEnd = d.getClosestPoint(p);
        if (previewPEnd.distance(p) >= d.getSelectionDistance()) {
            previewPEnd = p;
        }
        return previewPEnd;
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.getCamera().TV2object(p0));

        // Apply values from mouseMoved
        switch (currentStep) {
            case SELECT_FIRST_POINT:
                pStart = d.getClosestPoint(p);
                currentStep = Step.SELECT_SECOND_POINT;
                return;
            case SELECT_SECOND_POINT:
                pEnd = determinePEnd(p);
                candidates = makePreviewLines(pStart, pEnd);
                if (pEnd.distance(p) >= d.getSelectionDistance()) {
                    return;
                }
                currentStep = Step.SELECT_DIRECTION;
                return;
            case SELECT_DIRECTION:
                direction = determineSelectedCandidate(p);
                currentStep = Step.SELECT_LENGTH;
                if (direction == null) {
                    reset();
                } else {
                    candidates.clear();
                }
                return;
            case SELECT_LENGTH:
                LineSegment add_sen = determineLineSegmentToAdd(p);

                if (add_sen != null && Epsilon.high.gt0(add_sen.determineLength())) {
                    d.addLineSegment(add_sen);
                    d.record();
                }
                reset();
        }
    }

    private LineSegment determineLineSegmentForPreview(Point p) {
        LineSegment ls = determineLineSegmentToAdd(p);
        if (ls == null) {
            Point newEndPoint = OritaCalc.findProjection(direction, p);
            return new LineSegment(newEndPoint, pEnd, d.getLineColor());
        }
        return ls;
    }

    private LineSegment determineLineSegmentToAdd(Point p) {
        LineSegment closestLineSegment = d.getClosestLineSegment(p);
        if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance()) {
            LineSegment s = new LineSegment();
            s.set(closestLineSegment);
            s.setColor(LineColor.GREEN_6);
            Point startingPoint = new Point();
            startingPoint.set(OritaCalc.findIntersection(s, direction));
            return new LineSegment(startingPoint, pEnd, d.getLineColor());
        }
        return null;
    }

    private LineSegment determineSelectedCandidate(Point p) {
        Optional<LineSegment> closestLineSegmentO = candidates.stream()
                .min(Comparator.comparingDouble(cand -> OritaCalc.determineLineSegmentDistance(p, cand)));
        if (closestLineSegmentO.isPresent()) {
            LineSegment closestLineSegment = closestLineSegmentO.get();
            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance()) {
                LineSegment s = new LineSegment();
                s.set(closestLineSegment);
                s.setColor(LineColor.BLUE_2);
                return s;
            }
        }
        return null;
    }

    private List<LineSegment> makePreviewLines(Point pStart, Point pEnd) {
        List<LineSegment> candidates = new ArrayList<>();
        int numPreviewLines;//1つの端点周りに描く線の本数
        if (angleSystemModel.getCurrentAngleSystemDivider() != 0) {
            numPreviewLines = angleSystemModel.getCurrentAngleSystemDivider() * 2 - 1;
        } else {
            numPreviewLines = 6;
        }

        //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d)

        LineSegment startingSegment = new LineSegment(pEnd, pStart);
        startingSegment.setColor(LineColor.GREEN_6);
        candidates.add(startingSegment);

        if (angleSystemModel.getCurrentAngleSystemDivider() != 0) {

            double angle = 0.0;
            double angleStep = 180.0 / angleSystemModel.getCurrentAngleSystemDivider();
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
            double[] angles = angleSystemModel.getAngles();

            for (int i = 0; i < 6; i++) {
                LineSegment s = new LineSegment();
                s.set(OritaCalc.lineSegment_rotate(startingSegment, angles[i], 1.0));
                s.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
                candidates.add(s);
                s.setColor(customAngleColors[i % 3]);
            }
        }
        return candidates;
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        for (LineSegment candidate : candidates) {
            DrawingUtil.drawLineStep(g2, candidate, camera, settings.getLineWidth(), d.getGridInputAssist());
        }
        if (direction != null && currentStep == Step.SELECT_DIRECTION) {
            DrawingUtil.drawLineStep(g2, direction, camera, settings.getLineWidth(), d.getGridInputAssist());
        }
        if (pStart != null) {
            DrawingUtil.drawStepVertex(g2, pStart, LineColor.RED_1, camera, d.getGridInputAssist());
        }
        if (pEnd != null) {
            DrawingUtil.drawStepVertex(g2, pEnd, LineColor.BLUE_2, camera, d.getGridInputAssist());
        }
        if (previewLine != null) {
            DrawingUtil.drawCpLine(g2, previewLine, camera, settings.getLineStyle(), settings.getLineWidth(), d.getPointSize(), settings.getWidth(), settings.getHeight());
        }
    }

    @Override
    public void reset() {
        candidates.clear();
        currentStep = Step.SELECT_FIRST_POINT;
        pStart = null;
        pEnd = null;
        direction = null;
        previewLine = null;
    }
}
