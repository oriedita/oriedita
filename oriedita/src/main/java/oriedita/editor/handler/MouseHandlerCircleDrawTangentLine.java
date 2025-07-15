package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.StraightLine;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

enum CircleDrawTangentLineStep {
    SELECT_1ST_POINT_OR_CIRCLE,
    SELECT_2ND_POINT_OR_CIRCLE,
    SELECT_CIRCLE,
    SELECT_INDICATOR
}

@ApplicationScoped
@Handles(MouseMode.CIRCLE_DRAW_TANGENT_LINE_45)
public class MouseHandlerCircleDrawTangentLine extends StepMouseHandler<CircleDrawTangentLineStep> {

    private Circle circle1, circle2;
    private Point point;
    private List<LineSegment> indicators;
    private LineSegment resultSegment;

    @Inject
    public MouseHandlerCircleDrawTangentLine() {
        super(CircleDrawTangentLineStep.SELECT_1ST_POINT_OR_CIRCLE);
        steps.addNode(StepNode.createNode_MD_R(CircleDrawTangentLineStep.SELECT_1ST_POINT_OR_CIRCLE,
                this::move_drag_select_first_point_or_circle,
                this::release_select_first_point_or_circle));
        steps.addNode(StepNode.createNode_MD_R(CircleDrawTangentLineStep.SELECT_CIRCLE,
                this::move_drag_select_circle,
                this::release_select_circle));
        steps.addNode(StepNode.createNode_MD_R(CircleDrawTangentLineStep.SELECT_2ND_POINT_OR_CIRCLE,
                this::move_drag_select_second_point_or_circle,
                this::release_select_second_point_or_circle));
        steps.addNode(StepNode.createNode_MD_R(CircleDrawTangentLineStep.SELECT_INDICATOR,
                this::move_drag_select_indicator, this::release_drag_select_indicator));
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, point, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawCircleStep(g2, circle1, camera);
        DrawingUtil.drawCircleStep(g2, circle2, camera);
        for (LineSegment indicator : indicators) {
            DrawingUtil.drawLineStep(g2, indicator, camera, settings.getLineWidth(),
                    d.getGridInputAssist());
        }
        DrawingUtil.drawLineStep(g2, resultSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
    }

    @Override
    public void reset() {
        resetStep();
        circle1 = null;
        circle2 = null;
        point = null;
        indicators = new ArrayList<>();
        resultSegment = null;
    }

    // Select first point or circle
    private void move_drag_select_first_point_or_circle(Point p) {
        Circle tmpCircle = d.getClosestCircleMidpoint(p);
        if (OritaCalc.distance_circumference(p, tmpCircle) < d.getSelectionDistance()) {
            circle1 = new Circle(tmpCircle);
            circle1.setColor(LineColor.GREEN_6);
        } else
            circle1 = null;

        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            circle1 = null;
            point = d.getClosestPoint(p);
        } else
            point = null;
    }

    private CircleDrawTangentLineStep release_select_first_point_or_circle(Point p) {
        if (point == null && circle1 == null)
            return CircleDrawTangentLineStep.SELECT_1ST_POINT_OR_CIRCLE;
        if (point != null && circle1 == null) {
            return CircleDrawTangentLineStep.SELECT_CIRCLE;
        }
        return CircleDrawTangentLineStep.SELECT_2ND_POINT_OR_CIRCLE;
    }

    // If selected point first, select a circle
    private void move_drag_select_circle(Point p) {
        Circle tmpCircle = d.getClosestCircleMidpoint(p);
        if (OritaCalc.distance_circumference(p, tmpCircle) < d.getSelectionDistance()
                && point.distance(tmpCircle.determineCenter()) > tmpCircle.getR()) {
            circle2 = new Circle(tmpCircle);
            circle2.setColor(LineColor.GREEN_6);
        } else
            circle2 = null;
    }

    private CircleDrawTangentLineStep release_select_circle(Point p) {
        if (circle2 == null)
            return CircleDrawTangentLineStep.SELECT_CIRCLE;
        setupIndicator_1P_1C(point, circle2);
        return CircleDrawTangentLineStep.SELECT_INDICATOR;
    }

    // If selected circle first, select either point or another circle
    private void move_drag_select_second_point_or_circle(Point p) {
        Circle tmpCircle = d.getClosestCircleMidpoint(p);
        if (OritaCalc.distance_circumference(p, tmpCircle) < d.getSelectionDistance()) {
            circle2 = new Circle(tmpCircle);
            circle2.setColor(LineColor.GREEN_6);
        } else
            circle2 = null;

        Point tmpPoint = d.getClosestPoint(p);
        if (p.distance(tmpPoint) < d.getSelectionDistance()
                && tmpPoint.distance(circle1.determineCenter()) > circle1.getR()) {
            circle2 = null;
            point = d.getClosestPoint(p);
        } else
            point = null;
    }

    private CircleDrawTangentLineStep release_select_second_point_or_circle(Point p) {
        if (point == null && circle2 == null)
            return CircleDrawTangentLineStep.SELECT_2ND_POINT_OR_CIRCLE;
        if (point != null) {
            setupIndicator_1P_1C(point, circle1);
        } else {
            setupIndicator_2C(circle1, circle2);
        }
        return CircleDrawTangentLineStep.SELECT_INDICATOR;
    }

    // Select indicator
    private void move_drag_select_indicator(Point p) {
        double minDist = 100000.0;
        for (LineSegment indicator : indicators) {
            double dist = OritaCalc.determineLineSegmentDistance(p, indicator);
            if (dist < minDist) {
                minDist = dist;
                if (dist < d.getSelectionDistance()) {
                    resultSegment = new LineSegment(indicator, LineColor.ORANGE_4);
                } else
                    resultSegment = null;
            }
        }
    }

    private CircleDrawTangentLineStep release_drag_select_indicator(Point p) {
        if (resultSegment == null)
            return CircleDrawTangentLineStep.SELECT_INDICATOR;

        d.addLineSegment(new LineSegment(resultSegment, d.getLineColor()));
        d.record();
        reset();
        return CircleDrawTangentLineStep.SELECT_1ST_POINT_OR_CIRCLE;
    }

    private void setupIndicator_1P_1C(Point p, Circle c) {
        if (Math.abs(c.getR() - OritaCalc.distance(c.determineCenter(), p)) < Epsilon.UNKNOWN_1EN7) {
            LineSegment projectionLine = new LineSegment(c.determineCenter(), p);
            indicators.add(OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(p,
                    OritaCalc.findProjection(OritaCalc.moveParallel(projectionLine, 1), p),
                    LineColor.PURPLE_8)));
            indicators.add(OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(p,
                    OritaCalc.findProjection(OritaCalc.moveParallel(projectionLine, -1), p),
                    LineColor.PURPLE_8)));
            return;
        }
        LineSegment diameter = new LineSegment(p, c.determineCenter());
        Circle constructCir = new Circle(diameter, LineColor.GREEN_6);
        LineSegment connectSegment = OritaCalc
                .circle_to_circle_no_intersection_wo_musubu_lineSegment(constructCir, c);
        indicators.add(new LineSegment(p, connectSegment.getA(), LineColor.PURPLE_8));
        indicators.add(new LineSegment(p, connectSegment.getB(), LineColor.PURPLE_8));
    }

    private void setupIndicator_2C(Circle circle1, Circle circle2) {
        Point c1 = circle1.determineCenter();
        Point c2 = circle2.determineCenter();

        double x1 = circle1.getX();
        double y1 = circle1.getY();
        double r1 = circle1.getR();
        double x2 = circle2.getX();
        double y2 = circle2.getY();
        double r2 = circle2.getR();
        // 0,0,r, xp,yp,R
        double xp = x2 - x1;
        double yp = y2 - y1;

        if (c1.distance(c2) < Epsilon.UNKNOWN_1EN6)
            return;
        if ((xp * xp + yp * yp) < (r1 - r2) * (r1 - r2))
            return;

        if (Math.abs((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2)) < Epsilon.UNKNOWN_1EN7) {// 外接線1本の場合
            Point kouten = OritaCalc.internalDivisionRatio(c1, c2, -r1, r2);
            StraightLine ty = new StraightLine(c1, kouten).orthogonalize(kouten);

            d.lineStepAdd(OritaCalc.circle_to_straightLine_no_intersect_wo_connect_LineSegment(
                    new Circle(kouten, (r1 + r2) / 2.0, LineColor.BLACK_0), ty));
        } else if (((r1 - r2) * (r1 - r2) < (xp * xp + yp * yp))
                && ((xp * xp + yp * yp) < (r1 + r2) * (r1 + r2))) {// 外接線2本の場合
            double xq1 = r1 * (xp * (r1 - r2) + yp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2)))
                    / (xp * xp + yp * yp);// 共通外接線
            double yq1 = r1 * (yp * (r1 - r2) - xp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2)))
                    / (xp * xp + yp * yp);// 共通外接線
            double xq2 = r1 * (xp * (r1 - r2) - yp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2)))
                    / (xp * xp + yp * yp);// 共通外接線
            double yq2 = r1 * (yp * (r1 - r2) + xp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2)))
                    / (xp * xp + yp * yp);// 共通外接線

            double xr1 = xq1 + x1;
            double yr1 = yq1 + y1;
            double xr2 = xq2 + x1;
            double yr2 = yq2 + y1;

            StraightLine t1 = new StraightLine(x1, y1, xr1, yr1).orthogonalize(new Point(xr1, yr1));
            StraightLine t2 = new StraightLine(x1, y1, xr2, yr2).orthogonalize(new Point(xr2, yr2));

            indicators.add(new LineSegment(new Point(xr1, yr1),
                    OritaCalc.findProjection(t1, new Point(x2, y2)),
                    LineColor.PURPLE_8));
            indicators.add(new LineSegment(new Point(xr2, yr2),
                    OritaCalc.findProjection(t2, new Point(x2, y2)),
                    LineColor.PURPLE_8));
        } else if (Math.abs((xp * xp + yp * yp) - (r1 + r2) * (r1 + r2)) < Epsilon.UNKNOWN_1EN7) {// 外接線2本と内接線1本の場合
            double xq1 = r1 * (xp * (r1 - r2) + yp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2)))
                    / (xp * xp + yp * yp);// 共通外接線
            double yq1 = r1 * (yp * (r1 - r2) - xp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2)))
                    / (xp * xp + yp * yp);// 共通外接線
            double xq2 = r1 * (xp * (r1 - r2) - yp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2)))
                    / (xp * xp + yp * yp);// 共通外接線
            double yq2 = r1 * (yp * (r1 - r2) + xp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2)))
                    / (xp * xp + yp * yp);// 共通外接線

            double xr1 = xq1 + x1;
            double yr1 = yq1 + y1;
            double xr2 = xq2 + x1;
            double yr2 = yq2 + y1;

            StraightLine t1 = new StraightLine(x1, y1, xr1, yr1).orthogonalize(new Point(xr1, yr1));
            StraightLine t2 = new StraightLine(x1, y1, xr2, yr2).orthogonalize(new Point(xr2, yr2));

            indicators.add(new LineSegment(new Point(xr1, yr1),
                    OritaCalc.findProjection(t1, new Point(x2, y2)),
                    LineColor.PURPLE_8));
            indicators.add(new LineSegment(new Point(xr2, yr2),
                    OritaCalc.findProjection(t2, new Point(x2, y2)),
                    LineColor.PURPLE_8));

            Point kouten = OritaCalc.internalDivisionRatio(c1, c2, r1, r2);
            StraightLine ty = new StraightLine(c1, kouten).orthogonalize(kouten);
            LineSegment s = OritaCalc.circle_to_straightLine_no_intersect_wo_connect_LineSegment(
                    new Circle(kouten, (r1 + r2) / 2.0, LineColor.BLACK_0), ty)
                    .withColor(LineColor.PURPLE_8);
            indicators.add(s);
        } else if ((r1 + r2) * (r1 + r2) < (xp * xp + yp * yp)) {// 外接線2本と内接線2本の場合
            // ---------------------------------------------------------------
            // -------------------------------------
            // ------- ------------- ------- ------- -------------
            double xq1 = r1 * (xp * (r1 - r2) + yp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2)))
                    / (xp * xp + yp * yp);// 共通外接線
            double yq1 = r1 * (yp * (r1 - r2) - xp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2)))
                    / (xp * xp + yp * yp);// 共通外接線
            double xq2 = r1 * (xp * (r1 - r2) - yp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2)))
                    / (xp * xp + yp * yp);// 共通外接線
            double yq2 = r1 * (yp * (r1 - r2) + xp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2)))
                    / (xp * xp + yp * yp);// 共通外接線
            double xq3 = r1 * (xp * (r1 + r2) + yp * Math.sqrt((xp * xp + yp * yp) - (r1 + r2) * (r1 + r2)))
                    / (xp * xp + yp * yp);// 共通内接線
            double yq3 = r1 * (yp * (r1 + r2) - xp * Math.sqrt((xp * xp + yp * yp) - (r1 + r2) * (r1 + r2)))
                    / (xp * xp + yp * yp);// 共通内接線
            double xq4 = r1 * (xp * (r1 + r2) - yp * Math.sqrt((xp * xp + yp * yp) - (r1 + r2) * (r1 + r2)))
                    / (xp * xp + yp * yp);// 共通内接線
            double yq4 = r1 * (yp * (r1 + r2) + xp * Math.sqrt((xp * xp + yp * yp) - (r1 + r2) * (r1 + r2)))
                    / (xp * xp + yp * yp);// 共通内接線

            double xr1 = xq1 + x1;
            double yr1 = yq1 + y1;
            double xr2 = xq2 + x1;
            double yr2 = yq2 + y1;
            double xr3 = xq3 + x1;
            double yr3 = yq3 + y1;
            double xr4 = xq4 + x1;
            double yr4 = yq4 + y1;

            StraightLine t1 = new StraightLine(x1, y1, xr1, yr1).orthogonalize(new Point(xr1, yr1));
            StraightLine t2 = new StraightLine(x1, y1, xr2, yr2).orthogonalize(new Point(xr2, yr2));
            StraightLine t3 = new StraightLine(x1, y1, xr3, yr3).orthogonalize(new Point(xr3, yr3));
            StraightLine t4 = new StraightLine(x1, y1, xr4, yr4).orthogonalize(new Point(xr4, yr4));

            indicators.add(new LineSegment(new Point(xr1, yr1),
                    OritaCalc.findProjection(t1, new Point(x2, y2)),
                    LineColor.PURPLE_8));
            indicators.add(new LineSegment(new Point(xr2, yr2),
                    OritaCalc.findProjection(t2, new Point(x2, y2)),
                    LineColor.PURPLE_8));
            indicators.add(new LineSegment(new Point(xr3, yr3),
                    OritaCalc.findProjection(t3, new Point(x2, y2)),
                    LineColor.PURPLE_8));
            indicators.add(new LineSegment(new Point(xr4, yr4),
                    OritaCalc.findProjection(t4, new Point(x2, y2)),
                    LineColor.PURPLE_8));
        }
    }
}
