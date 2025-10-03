package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.handler.step.StepMouseHandler;
import oriedita.editor.handler.step.ObjCoordStepNode;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Point_p;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

enum DrawAngleConvergingLinesStep {
    SELECT_2P_OR_SEGMENT,
    SELECT_2P,
    SELECT_CONVERGING_POINT
}

@ApplicationScoped
@Handles(MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_13)
public class MouseHandlerDrawCreaseAngleRestricted extends StepMouseHandler<DrawAngleConvergingLinesStep> {
    @Inject
    private AngleSystemModel angleSystemModel;

    private Point point1, point2, convergePoint;
    private LineSegment segment, resultSegment1, resultSegment2;
    private List<LineSegment> indicators;
    private List<Point> intersections;
    int honsuu;

    @Inject
    public MouseHandlerDrawCreaseAngleRestricted(AngleSystemModel angleSystemModel) {
        super(DrawAngleConvergingLinesStep.SELECT_2P_OR_SEGMENT);
        steps.addNode(ObjCoordStepNode.createNode_MD_R(DrawAngleConvergingLinesStep.SELECT_2P_OR_SEGMENT,
                this::move_drag_select_2p_or_segment, this::release_select_2p_or_segment));
        steps.addNode(ObjCoordStepNode.createNode_MD_R(DrawAngleConvergingLinesStep.SELECT_2P, this::move_drag_select_2p,
                this::release_select_2p));
        steps.addNode(ObjCoordStepNode.createNode_MD_R(DrawAngleConvergingLinesStep.SELECT_CONVERGING_POINT,
                this::move_drag_select_converging_point, this::release_select_converging_point));
        angleSystemModel.addPropertyChangeListener(e -> {
            indicators = new ArrayList<>();
            intersections = new ArrayList<>();
            setHonsuu();
            setupIndicators();
        });
    }

    @Override
    public EnumSet<MouseHandlerSettingGroup> getSettings() {
        return EnumSet.of(MouseHandlerSettingGroup.ANGLE_SYSTEM);
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, point1, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, point2, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, segment, camera, settings.getLineWidth(), d.getGridInputAssist());
        for (LineSegment indicator : indicators) {
            DrawingUtil.drawLineStep(g2, indicator, camera, settings.getLineWidth(), d.getGridInputAssist());
        }
        for (Point intersection : intersections) {
            DrawingUtil.drawStepVertex(g2, intersection, d.getLineColor(), camera,
                    d.getGridInputAssist());
        }
        if (resultSegment1 != null)
            DrawingUtil.drawLineStep(g2, resultSegment1.withColor(d.getLineColor()), camera, 3.0f,
                    d.getGridInputAssist());
        if (resultSegment2 != null)
            DrawingUtil.drawLineStep(g2, resultSegment2.withColor(d.getLineColor()), camera, 3.0f,
                    d.getGridInputAssist());
    }

    @Override
    public void reset() {
        resetStep();
        point1 = null;
        point2 = null;
        convergePoint = null;
        segment = null;
        resultSegment1 = null;
        resultSegment2 = null;
        indicators = new ArrayList<>();
        intersections = new ArrayList<>();
        setHonsuu();

    }

    // Select 2 points or segment
    private void move_drag_select_2p_or_segment(Point p) {
        LineSegment tmpSegment = d.getClosestLineSegment(p);
        if (OritaCalc.determineLineSegmentDistance(p, tmpSegment) < d.getSelectionDistance()) {
            segment = new LineSegment(tmpSegment, LineColor.PURPLE_8);
        } else
            segment = null;

        Point tmpPoint = d.getClosestPoint(p);
        if (p.distance(tmpPoint) < d.getSelectionDistance()) {
            segment = null;
            point1 = tmpPoint;
        } else
            point1 = null;
    }

    private DrawAngleConvergingLinesStep release_select_2p_or_segment(Point p) {
        if (point1 == null && segment == null)
            return DrawAngleConvergingLinesStep.SELECT_2P_OR_SEGMENT;
        if (point1 != null)
            return DrawAngleConvergingLinesStep.SELECT_2P;

        setupIndicators();
        if (indicators.isEmpty())
            return DrawAngleConvergingLinesStep.SELECT_2P_OR_SEGMENT;
        return DrawAngleConvergingLinesStep.SELECT_CONVERGING_POINT;
    }

    // Select another point
    private void move_drag_select_2p(Point p) {
        Point tmpPoint = d.getClosestPoint(p);
        if (p.distance(tmpPoint) < d.getSelectionDistance()) {
            segment = null;
            point2 = new Point(tmpPoint);
        } else
            point2 = null;
    }

    private DrawAngleConvergingLinesStep release_select_2p(Point p) {
        if (point2 == null)
            return DrawAngleConvergingLinesStep.SELECT_2P;

        segment = new LineSegment(point1, point2, LineColor.PURPLE_8);
        setupIndicators();
        if (indicators.isEmpty())
            return DrawAngleConvergingLinesStep.SELECT_2P;
        return DrawAngleConvergingLinesStep.SELECT_CONVERGING_POINT;
    }

    // Select converging point of any 2 indicators
    private void move_drag_select_converging_point(Point p) {
        Point tmpPoint = getClosestIntersection(intersections, p);
        if (tmpPoint != null) {
            convergePoint = new Point_p(tmpPoint);
        } else
            convergePoint = null;

        if (convergePoint != null) {
            resultSegment1 = new LineSegment(segment.getA(), convergePoint, d.getLineColor());
            resultSegment2 = new LineSegment(segment.getB(), convergePoint, d.getLineColor());
        } else {
            resultSegment1 = null;
            resultSegment2 = null;
        }
    }

    private DrawAngleConvergingLinesStep release_select_converging_point(Point p) {
        if (convergePoint == null)
            return DrawAngleConvergingLinesStep.SELECT_CONVERGING_POINT;

        d.addLineSegment(resultSegment1);
        d.addLineSegment(resultSegment2);
        d.record();
        reset();
        return DrawAngleConvergingLinesStep.SELECT_2P_OR_SEGMENT;
    }

    private void setupIndicators() {
        if (segment == null)
            return;
        int divider = angleSystemModel.getCurrentAngleSystemDivider();
        double denominator = divider != 0 ? (double) divider : 4.0;
        double d_angle_system = 180.0 / denominator;

        if (divider != 0) {
            drawIndicators(d_angle_system, segment);
            drawIndicators(d_angle_system, segment.withSwappedCoordinates());
        } else {
            double[] jk = angleSystemModel.getAngles();
            drawIndicators(jk, segment);
            drawIndicators(jk, segment.withSwappedCoordinates());
        }

        setupIntersections();
    }

    private void drawIndicators(double d_angle_system, LineSegment s_kiso) {
        double angle = 0.0;
        for (int i = 0; i < honsuu; i++) {
            LineColor i_jyun_color = i % 2 == 0 ? LineColor.ORANGE_4 : LineColor.GREEN_6;

            angle += d_angle_system;
            LineSegment s = OritaCalc.lineSegment_rotate(s_kiso, angle, 10.0).withColor(i_jyun_color);
            indicators.add(s);
        }
    }

    private void drawIndicators(double[] jk, LineSegment s_kiso) {
        for (int i = 1; i <= 6; i++) {
            LineSegment s = OritaCalc.lineSegment_rotate(s_kiso, jk[i], 10.0);
            if (i == 1 || i == 5)
                s = s.withColor(LineColor.GREEN_6);
            if (i == 2 || i == 6)
                s = s.withColor(LineColor.PURPLE_8);
            if (i == 3 || i == 4)
                s = s.withColor(LineColor.ORANGE_4);

            indicators.add(s);
        }
    }

    private void setupIntersections() {
        for (int i = 0; i < indicators.size(); i++) {
            for (int j = i + 1; j < indicators.size(); j++) {
                LineSegment s1 = indicators.get(i);
                LineSegment s2 = indicators.get(j);

                LineSegment.Intersection intersection = OritaCalc.determineLineSegmentIntersection(s1, s2);
                if (!intersection.isIntersection() || intersection.isOverlapping())
                    continue;

                Point intPoint = OritaCalc.findIntersection(s1, s2);
                if (intPoint.equals(segment.getA()) || intPoint.equals(segment.getB()))
                    continue;
                if (intersections.stream().anyMatch(point -> point.equals(intPoint)))
                    continue;

                intersections.add(intPoint);
            }
        }
    }

    private void setHonsuu() {
        if (angleSystemModel.getCurrentAngleSystemDivider() != 0) {
            honsuu = angleSystemModel.getCurrentAngleSystemDivider() * 2 - 1;
        } else
            honsuu = 6;
    }

    private Point getClosestIntersection(List<Point> list, Point p) {
        double minDist = 100000.0;
        Point closestPoint = null;

        for (Point intersection : list) {
            double dist = p.distance(intersection);
            if (dist > minDist)
                continue;

            minDist = dist;
            if (p.distance(intersection) > d.getSelectionDistance())
                continue;

            closestPoint = new Point(intersection);
        }

        return closestPoint;
    }
}
