package oriedita.editor.handler;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import org.tinylog.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.export.FoldImporter;
import oriedita.editor.save.Save;
import origami.Epsilon;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

enum DrawDoveBaseStep {
    SELECT_P1,
    SELECT_P2
}

@ApplicationScoped
@Handles(MouseMode.DRAW_DOVE_BASE)
public class MouseHandlerDrawDoveBase extends StepMouseHandler<DrawDoveBaseStep> {
    private Point p1, p2;
    private Save originalSave;
    private FoldLineSet templateSet = new FoldLineSet();
    private List<LineSegment> previewSegments = new ArrayList<>();
    private List<Circle> startingCircles = new ArrayList<>();

    @Inject
    public MouseHandlerDrawDoveBase() {
        super(DrawDoveBaseStep.SELECT_P1);
        steps.addNode(StepNode.createNode_MD_R(DrawDoveBaseStep.SELECT_P1, this::move_drag_select_p1,
                this::release_select_p1));
        steps.addNode(StepNode.createNode_MD_R(DrawDoveBaseStep.SELECT_P2, this::move_drag_select_p2,
                this::release_select_p2));

        try {
            originalSave = new FoldImporter()
                    .doImport(
                            getClass().getClassLoader().getResourceAsStream("default-molecules/dove_base.fold"));
            templateSet.setSave(originalSave);
            startingCircles = originalSave.getCircles().stream()
                    .filter((circle) -> circle.getR() > Epsilon.UNKNOWN_1EN6).toList();
        } catch (Exception e) {
            Logger.error(DrawDoveBaseStep.class.getSimpleName() + ": " + e);
        }
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, p1, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, p2, d.getLineColor(), camera, d.getGridInputAssist());
        for (LineSegment segment : previewSegments) {
            DrawingUtil.drawLineStep(g2, segment.withColor(d.getLineColor()), camera, settings.getLineWidth(),
                    d.getGridInputAssist());
        }
    }

    @Override
    public void reset() {
        resetStep();
        p1 = null;
        p2 = null;
        previewSegments = new ArrayList<>();
    }

    // Select point 1
    private void move_drag_select_p1(Point p) {
        Point tmpPoint = d.getClosestPoint(p);
        p1 = p;
        if (p.distance(tmpPoint) < d.getSelectionDistance()) {
            p1 = tmpPoint;
        }
    }

    private DrawDoveBaseStep release_select_p1(Point p) {
        return DrawDoveBaseStep.SELECT_P2;
    }

    // Select point 2
    private void move_drag_select_p2(Point p) {
        Point tmpPoint = d.getClosestPoint(p);
        p2 = p;
        if (p.distance(tmpPoint) < d.getSelectionDistance()) {
            p2 = tmpPoint;
        }

        templateSet.setSave(originalSave);
        templateSet.move(startingCircles.get(0).determineCenter(), startingCircles.get(1).determineCenter(), p1, p2);
        previewSegments = templateSet.getLineSegments().stream()
                .filter((segment) -> segment.determineLength() > Epsilon.UNKNOWN_1EN6).toList();
    }

    private DrawDoveBaseStep release_select_p2(Point p) {
        if (p2.distance(p1) < Epsilon.UNKNOWN_1EN6)
            return DrawDoveBaseStep.SELECT_P2;
        for (LineSegment segment : previewSegments) {
            d.addLineSegment(segment.withColor(d.getLineColor()));
        }
        d.record();
        reset();
        return DrawDoveBaseStep.SELECT_P1;
    }
}
