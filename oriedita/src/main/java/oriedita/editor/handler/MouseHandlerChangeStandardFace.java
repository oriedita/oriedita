package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tinylog.Logger;
import oriedita.editor.Colors;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.handler.step.StepFactory;
import oriedita.editor.handler.step.StepGraph;
import oriedita.editor.handler.step.StepMouseHandler;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;
import origami.folding.FoldedFigure;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Handles(MouseMode.CHANGE_STANDARD_FACE_103)
public class MouseHandlerChangeStandardFace extends StepMouseHandler<MouseHandlerChangeStandardFace.Step> {
    private final FoldedFiguresList foldedFiguresList;
    private Polygon polygon;

    public enum Step {
        SELECT_STARTING_FACE
    }

    @Inject
    public MouseHandlerChangeStandardFace(FoldedFiguresList foldedFiguresList, @Named("mainCreasePattern_Worker") CreasePattern_Worker d) {
        this.foldedFiguresList = foldedFiguresList;
        this.d = d;
    }

    @Override
    protected StepGraph<Step> initStepGraph(StepFactory stepFactory) {
        var g = new StepGraph<>(Step.SELECT_STARTING_FACE);
        g.addNode(stepFactory.createNode_MD_R(
                Step.SELECT_STARTING_FACE,
                p -> {
                    FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();
                    if (selectedFigure == null) {
                        return;
                    }
                    var pointSet = selectedFigure.getFoldedFigure().wireFrameWorker_flatCp.get();
                    int newStartingFaceId = pointSet.inside(p);
                    if (newStartingFaceId < 1) return;
                    var face = pointSet.getFace(newStartingFaceId);
                    var numPoints = face.getNumPoints();
                    List<Point> points = new ArrayList<>();
                    for (int i = 1; i <= numPoints; i++) {
                        points.add(pointSet.getPoint(face.getPointId(i)));
                    }
                    polygon = new Polygon(points);
                },
                p -> {
                    FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();
                    if (selectedFigure == null) {
                        return Step.SELECT_STARTING_FACE;
                    }
                    changeStartingFace(selectedFigure, p);
                    return Step.SELECT_STARTING_FACE;
                }
        ));
        return g;
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        if (polygon == null) {return;}
        g2.setColor(Colors.get(Colors.FIGURE_HIGHLIGHT));
        Path2D.Double path = new Path2D.Double();

        Point t1 = camera.object2TV(polygon.get(0));
        path.moveTo(t1.getX(), t1.getY());

        for (int i = 1; i < polygon.degree(); i++) {
            t1 = camera.object2TV(polygon.get(i));
            path.lineTo(t1.getX(), t1.getY());
        }

        path.closePath();

        g2.fill(path);
    }

    @Override
    public void reset() {
        super.reset();
        polygon = null;
    }

    private static void changeStartingFace(FoldedFigure_Drawer selectedFigure, Point p) {
        int oldStartingFaceId = selectedFigure.getStartingFaceId();

        int newStartingFaceId = selectedFigure.getFoldedFigure().wireFrameWorker_flatCp.get().inside(p);

        if (newStartingFaceId < 1) return;

        selectedFigure.setStartingFaceId(newStartingFaceId);

        Logger.info("kijyunmen_id = " + newStartingFaceId);
        if (selectedFigure.getFoldedFigure().foldedFigure_worker.face_rating != null) {//20180227追加
            int index = selectedFigure.getFoldedFigure().foldedFigure_worker.nbox.getSequence(newStartingFaceId);
            Logger.info(
                    "OZ.js.nbox.get_jyunjyo = " + index + " , rating = " + selectedFigure.getFoldedFigure().foldedFigure_worker.nbox.getWeight(index)
            );

        }
        if ((newStartingFaceId != oldStartingFaceId) && (selectedFigure.getFoldedFigure().estimationStep != FoldedFigure.EstimationStep.STEP_0)) {
            selectedFigure.getFoldedFigure().estimationStep = FoldedFigure.EstimationStep.STEP_1;
        }
    }
}
