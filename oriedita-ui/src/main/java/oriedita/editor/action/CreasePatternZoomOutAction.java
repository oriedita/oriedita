package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.Canvas;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.CameraModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import origami.crease_pattern.element.Point;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.creasePatternZoomOutAction)
public class CreasePatternZoomOutAction extends AbstractOrieditaAction{
    @Inject
    FoldedFiguresList foldedFiguresList;
    @Inject
    Canvas canvas;
    @Inject
    FoldedFigureModel foldedFigureModel;
    @Inject
    CameraModel creasePatternCameraModel;

    @Inject
    ApplicationModel applicationModel;

    @Inject
    public CreasePatternZoomOutAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        creasePatternCameraModel.zoomOut(applicationModel.getZoomSpeed());

        double magnification = 1.0 / Math.sqrt(Math.sqrt(Math.sqrt(2.0)));//  sqrt(sqrt(2))=1.1892

        FoldedFigure_Drawer OZi;
        for (int i_oz = 0; i_oz < foldedFiguresList.getSize(); i_oz++) {
            OZi = foldedFiguresList.getElementAt(i_oz);

            Point t_o2tv = canvas.getCreasePatternCamera().object2TV(canvas.getCreasePatternCamera().getCameraPosition());

            OZi.scale(magnification, t_o2tv);
        }

        foldedFigureModel.zoomOut(applicationModel.getZoomSpeed());
    }
}
