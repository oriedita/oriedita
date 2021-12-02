package origami_editor.editor.canvas;

import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;
import origami.folding.constraint.CustomConstraint;
import origami_editor.editor.MouseMode;
import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.drawing.FoldedFigure_Drawer;
import origami_editor.editor.service.FoldingService;
import origami_editor.tools.Camera;

import javax.swing.*;
import java.awt.event.MouseEvent;
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
    public void mousePressed(Point p0, MouseEvent e) {
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
        for (int i = 1; i <= foldedFigureSet.getNumFaces(); i++) {
            if (foldedFigureSet.inside(modelCoords, i) == Polygon.Intersection.INSIDE) {
                selectedFaces.add(i);
            }
        }

        List<Integer> white = new ArrayList<>();
        List<Integer> colored = new ArrayList<>();
        for (Integer selFaceIndex : selectedFaces) {
            if (selectedFigure.foldedFigure.cp_worker1.getIFacePosition(selFaceIndex)% 2 == 0) {
                white.add(selFaceIndex);
            }else {
                colored.add(selFaceIndex);
            }
        }

        if (e.getButton() == MouseEvent.BUTTON1) {
            CustomConstraint nearest = null;
            double nearestDist = 100000000;
            for (CustomConstraint customConstraint : selectedFigure.foldedFigure.ct_worker.hierarchyList.getCustomConstraints()) {
                if (nearestDist > customConstraint.getPos().distance(modelCoords)) {
                    nearestDist = customConstraint.getPos().distance(modelCoords);
                    nearest = customConstraint;
                }
            }
            if (nearest != null && nearestDist < drawingWorker.selectionDistance) {
                selectedFigure.foldedFigure.ct_worker.hierarchyList.removeCustomConstraint(nearest);
                selectedFigure.foldedFigure.ct_worker.hierarchyList.addCustomConstraint(nearest.inverted());
            } else {
                CustomConstraint lc = new CustomConstraint(CustomConstraint.FaceOrder.NORMAL, white, colored, modelCoords, CustomConstraint.Type.COLOR_FRONT);
                selectedFigure.foldedFigure.ct_worker.hierarchyList.addCustomConstraint(lc);
                System.out.printf("Pressed in constraint mode %s size of faces: %d (%d/%d)%n", clickedOnFoldedModel, selectedFaces.size(), white.size(), colored.size());
            }
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            CustomConstraint nearest = null;
            double nearestDist = 100000000;
            for (CustomConstraint customConstraint : selectedFigure.foldedFigure.ct_worker.hierarchyList.getCustomConstraints()) {
                if (nearestDist > customConstraint.getPos().distance(modelCoords)) {
                    nearestDist = customConstraint.getPos().distance(modelCoords);
                    nearest = customConstraint;
                }
            }
            if (nearest != null && nearestDist < drawingWorker.selectionDistance) {
                selectedFigure.foldedFigure.ct_worker.hierarchyList.removeCustomConstraint(nearest);
            }
        }
    }

    @Override
    public void mousePressed(Point p0) {

    }

    @Override
    public void mouseDragged(Point p0) {

    }

    @Override
    public void mouseReleased(Point p0) {

    }
}
