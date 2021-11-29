package origami_editor.editor.canvas;

import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;
import origami.folding.constraint.LayerOrderConstraint;
import origami_editor.editor.MouseMode;
import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.drawing.FoldedFigure_Drawer;
import origami_editor.editor.service.FoldingService;
import origami_editor.tools.Camera;

import javax.swing.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class MouseHandlerAddFoldingConstraints implements MouseModeHandler {
    private final FoldingService foldingService;
    private final CanvasModel canvasModel;
    private final DefaultComboBoxModel<FoldedFigure_Drawer> foldedFiguresList;
    private final CreasePattern_Worker drawingWorker;
    private FoldedFigure_Drawer selectedFigure;
    private List<Integer> selectedFaces;

    @Override
    public EnumSet<Feature> getSubscribedFeatures() {
        return EnumSet.of(Feature.BUTTON_1, Feature.BUTTON_3);
    }

    public MouseHandlerAddFoldingConstraints(FoldingService foldingService, CanvasModel canvasModel,
                                             DefaultComboBoxModel<FoldedFigure_Drawer> foldedFiguresList, CreasePattern_Worker drawingWorker) {
        this.foldingService = foldingService;
        this.canvasModel = canvasModel;
        this.foldedFiguresList = foldedFiguresList;
        this.drawingWorker = drawingWorker;
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.ADD_FOLDING_CONSTRAINT;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    @Override
    public void mousePressed(Point p0) {
        selectedFigure = (FoldedFigure_Drawer) foldedFiguresList.getSelectedItem();
        if (selectedFigure == null) {
            return;
        }
        Camera modelCamera = selectedFigure.foldedFigureFrontCamera;
        Point modelCoords = modelCamera.TV2object(p0);
        PointSet foldedFigureSet = selectedFigure.foldedFigure.cp_worker2.get();
        Point cpCoords = drawingWorker.camera.TV2object(p0);
        boolean clickedOnFoldedModel = foldedFigureSet.inside(modelCoords) != 0;
        selectedFaces = new ArrayList<>();
        System.out.printf("size of pointSet: %d%n", foldedFigureSet.getNumFaces());
        int last = 0;
        for (int i = 1; i <= foldedFigureSet.getNumFaces(); i++) {
            if (foldedFigureSet.inside(modelCoords, i) == Polygon.Intersection.INSIDE) {
                selectedFaces.add(i);
                last = i;
            }
        }
        LayerOrderConstraint lc = new LayerOrderConstraint(last, LayerOrderConstraint.Type.TOP, selectedFaces, modelCoords);
        selectedFigure.foldedFigure.ct_worker.hierarchyList.addCustomConstraint(lc);
        System.out.printf("Pressed in constraint mode %s size of faces: %d%n", clickedOnFoldedModel, selectedFaces.size());
    }

    @Override
    public void mouseDragged(Point p0) {

    }

    @Override
    public void mouseReleased(Point p0) {

    }
}
