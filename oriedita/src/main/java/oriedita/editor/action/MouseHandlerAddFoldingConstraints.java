package oriedita.editor.action;

import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.drawing.tools.Camera;
import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;
import origami.folding.FoldedFigure;
import origami.folding.HierarchyList;
import origami.folding.constraint.CustomConstraint;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Singleton
public class MouseHandlerAddFoldingConstraints implements MouseModeHandler {
    private final FoldedFiguresList foldedFiguresList;
    private final CreasePattern_Worker drawingWorker;

    @Override
    public EnumSet<Feature> getSubscribedFeatures() {
        return EnumSet.of(Feature.BUTTON_1, Feature.BUTTON_3);
    }

    @Inject
    public MouseHandlerAddFoldingConstraints(FoldedFiguresList foldedFiguresList, CreasePattern_Worker drawingWorker) {
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
        FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();
        if (selectedFigure == null) {
            return;
        }
        PointSet foldedFigureSet = selectedFigure.getFoldedFigure().wireFrame_worker2.get();
        Camera modelCameraFront = selectedFigure.getFoldedFigureFrontCamera();
        Camera modelCameraBack = selectedFigure.getFoldedFigureRearCamera();
        FoldedFigure.State displayState = selectedFigure.getFoldedFigure().ip4;
        Point modelCoords;
        boolean backside;
        switch (displayState) {
            case FRONT_0:
                modelCoords = modelCameraFront.TV2object(p0);
                backside = false;
                break;
            case BACK_1:
                modelCoords = modelCameraBack.TV2object(p0);
                backside = true;
                break;
            default:
                modelCoords = modelCameraBack.TV2object(p0);
                backside = true;
                if (foldedFigureSet.inside(modelCoords) == 0) {
                    modelCoords = modelCameraFront.TV2object(p0);
                    backside = false;
                }
        }
        if (foldedFigureSet.inside(modelCoords) == 0) {
            return;
        }

        if (e.getButton() == MouseEvent.BUTTON1) {
            CustomConstraint nearest = nearConstraintInSelectionRadius(modelCoords, backside,
                    selectedFigure.getFoldedFigure().foldedFigure_worker.hierarchyList.getCustomConstraints());
            if (nearest != null && !e.isControlDown()) {
                invertColor(nearest, selectedFigure.getFoldedFigure().foldedFigure_worker.hierarchyList);
            } else {
                addConstraint(modelCoords, backside, selectedFigure.getFoldedFigure());
            }
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            removeNearestConstraint(modelCoords, backside, selectedFigure.getFoldedFigure().foldedFigure_worker.hierarchyList);
        }
    }

    private void removeNearestConstraint(Point modelCoords, boolean backside, HierarchyList hierarchyList) {
        CustomConstraint nearest = nearConstraintInSelectionRadius(modelCoords, backside, hierarchyList.getCustomConstraints());
        if (nearest != null) {
            hierarchyList.removeCustomConstraint(nearest);
        }
    }

    private void addConstraint(Point modelCoords, boolean backside, FoldedFigure foldedFigure) {
        List<Integer> selectedFaces = findSelectedFaces(modelCoords, foldedFigure.wireFrame_worker2.get());

        List<Integer> white = new ArrayList<>();
        List<Integer> colored = new ArrayList<>();
        for (Integer selFaceIndex : selectedFaces) {
            if (foldedFigure.wireFrame_worker1.getIFacePosition(selFaceIndex)% 2 == 0) {
                white.add(selFaceIndex);
            }else {
                colored.add(selFaceIndex);
            }
        }
        if (backside) {
            List<Integer> tmp = white;
            white = colored;
            colored = tmp;
        }
        CustomConstraint.FaceOrder fo = backside ? CustomConstraint.FaceOrder.FLIPPED : CustomConstraint.FaceOrder.NORMAL;
        CustomConstraint lc = new CustomConstraint(fo, white, colored, modelCoords, CustomConstraint.Type.COLOR_BACK);
        foldedFigure.foldedFigure_worker.hierarchyList.addCustomConstraint(lc);
    }

    private void invertColor(CustomConstraint nearest, HierarchyList hierarchyList) {
        hierarchyList.removeCustomConstraint(nearest);
        hierarchyList.addCustomConstraint(nearest.inverted());
    }

    private CustomConstraint nearConstraintInSelectionRadius(Point modelCoords, boolean backside, Iterable<CustomConstraint> constraints) {
        CustomConstraint nearest = null;
        double nearestDist = drawingWorker.getSelectionDistance();
        for (CustomConstraint customConstraint : constraints) {
            if (backside) {
                if (customConstraint.getFaceOrder() != CustomConstraint.FaceOrder.FLIPPED) {
                    continue;
                }
            } else {
                if (customConstraint.getFaceOrder() != CustomConstraint.FaceOrder.NORMAL) {
                    continue;
                }
            }
            if (nearestDist > customConstraint.getPos().distance(modelCoords)) {
                nearestDist = customConstraint.getPos().distance(modelCoords);
                nearest = customConstraint;
            }
        }
        return nearest;
    }

    private List<Integer> findSelectedFaces(Point modelCoords, PointSet foldedFigureSet) {
        List<Integer> selectedFaces = new ArrayList<>();
        for (int i = 1; i <= foldedFigureSet.getNumFaces(); i++) {
            if (foldedFigureSet.inside(modelCoords, i) == Polygon.Intersection.INSIDE) {
                selectedFaces.add(i);
            }
        }
        return selectedFaces;
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
