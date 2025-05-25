package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

enum AngleSystemStep {
    CLICK_DRAG_POINT,
    SELECT_DIRECTION,
    SELECT_LENGTH
}

@ApplicationScoped
@Handles(MouseMode.ANGLE_SYSTEM_16)
public class MouseHandlerAngleSystem extends StepMouseHandler<AngleSystemStep> {
    private final AngleSystemModel angleSystemModel;
    LineColor[] customAngleColors = new LineColor[]{
            LineColor.ORANGE_4,
            LineColor.GREEN_6,
            LineColor.PURPLE_8
    };

    private Point anchorPoint, releasePoint;
    private LineSegment selectedSegment, destinationSegment;
    List<LineSegment> candidates = new ArrayList<>();

    @Inject
    private CanvasModel canvasModel;

    @Inject
    public MouseHandlerAngleSystem(AngleSystemModel angleSystemModel) {
        super(AngleSystemStep.CLICK_DRAG_POINT);
        steps.addNode(StepNode.createNode(AngleSystemStep.CLICK_DRAG_POINT, this::move_click_drag_point, (p) -> {}, this::drag_click_drag_point, this::release_click_drag_point));
        steps.addNode(StepNode.createNode_MD_R(AngleSystemStep.SELECT_DIRECTION, this::move_drag_select_direction, this::release_drag_select_direction));
        steps.addNode(StepNode.createNode_MD_R(AngleSystemStep.SELECT_LENGTH, this::move_drag_select_length, this::release_select_length));
        this.angleSystemModel = angleSystemModel;
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, anchorPoint, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, releasePoint, d.getLineColor(), camera, d.getGridInputAssist());
        for (LineSegment candidate : candidates) {
            DrawingUtil.drawLineStep(g2, candidate, camera, settings.getLineWidth(), d.getGridInputAssist());
        }
        DrawingUtil.drawLineStep(g2, selectedSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, destinationSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
    }

    @Override
    public void reset() {
        anchorPoint = null;
        releasePoint = null;
        selectedSegment = null;
        destinationSegment = null;
        candidates.clear();
        move_click_drag_point(canvasModel.getMouseObjPosition());
        steps.setCurrentStep(AngleSystemStep.CLICK_DRAG_POINT);
    }

    // Click drag point
    private void move_click_drag_point(Point p) {
        if(p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            anchorPoint = d.getClosestPoint(p);
        } else anchorPoint = null;
    }
    private void drag_click_drag_point(Point p) {
        if(anchorPoint == null) return;

        releasePoint = p;
        if(p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            releasePoint = d.getClosestPoint(p);
        }

        candidates = makePreviewLines(anchorPoint, releasePoint);
    }
    private AngleSystemStep release_click_drag_point(Point p) {
        if(p == null || anchorPoint == null || releasePoint == null || anchorPoint.equals(releasePoint)) {
            reset();
            return AngleSystemStep.CLICK_DRAG_POINT;
        }
        move_drag_select_direction(p);
        return AngleSystemStep.SELECT_DIRECTION;
    }

    // Select direction
    private void move_drag_select_direction(Point p) {
        selectedSegment = determineSelectedCandidate(p);
    }
    private AngleSystemStep release_drag_select_direction(Point p) {
        if (selectedSegment == null) return AngleSystemStep.SELECT_DIRECTION;
        candidates.clear();
        move_drag_select_length(p);
        return AngleSystemStep.SELECT_LENGTH;
    }

    // Select length
    private void move_drag_select_length(Point p) {
        selectedSegment = new LineSegment(releasePoint, OritaCalc.findProjection(selectedSegment, p), d.getLineColor());

        if (OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p)) < d.getSelectionDistance()) {
            destinationSegment = d.getClosestLineSegment(p).withColor(LineColor.ORANGE_4);
        } else destinationSegment = null;
    }
    private AngleSystemStep release_select_length (Point p) {
        if(destinationSegment == null) return AngleSystemStep.SELECT_LENGTH;
        LineSegment add_sen = determineLineSegmentToAdd(p);

        if (add_sen != null && Epsilon.high.gt0(add_sen.determineLength())) {
            d.addLineSegment(add_sen);
            d.record();
        }
        reset();
        return AngleSystemStep.CLICK_DRAG_POINT;
    }

    private LineSegment determineLineSegmentToAdd(Point p) {
        LineSegment closestLineSegment = d.getClosestLineSegment(p);
        if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance()) {
            LineSegment s = closestLineSegment.withColor(LineColor.GREEN_6);
            Point startingPoint = OritaCalc.findIntersection(s, selectedSegment);
            return new LineSegment(startingPoint, releasePoint, d.getLineColor());
        }
        return null;
    }

    private LineSegment determineSelectedCandidate(Point p) {
        Optional<LineSegment> closestLineSegmentO = candidates.stream()
                .min(Comparator.comparingDouble(cand -> OritaCalc.determineLineSegmentDistance(p, cand)));
        if (closestLineSegmentO.isPresent()) {
            LineSegment closestLineSegment = closestLineSegmentO.get();
            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance()) {
                return closestLineSegment.withColor(d.getLineColor());
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

        LineSegment startingSegment = new LineSegment(pEnd, pStart, LineColor.GREEN_6);
        candidates.add(startingSegment);

        if (angleSystemModel.getCurrentAngleSystemDivider() != 0) {

            double angle = 0.0;
            double angleStep = 180.0 / angleSystemModel.getCurrentAngleSystemDivider();
            for (int i = 0; i < numPreviewLines; i++) {
                angle += angleStep;
                LineSegment e = OritaCalc.lineSegment_rotate(startingSegment, angle, 1.0);
                if (i % 2 == 0) {
                    e = e.withColor(LineColor.ORANGE_4);
                } else {
                    e = e.withColor(LineColor.GREEN_6);
                }
                e.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
                candidates.add(e);
            }
        } else {
            double[] angles = angleSystemModel.getAngles();

            for (int i = 0; i < 6; i++) {
                LineSegment s = OritaCalc.lineSegment_rotate(startingSegment, angles[i], 1.0);
                s.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
                candidates.add(s);
                d.getFoldLineSet().setColor(s, customAngleColors[i % 3]);
            }
        }
        return candidates;
    }
}
