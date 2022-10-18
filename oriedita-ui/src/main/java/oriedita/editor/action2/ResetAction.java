package oriedita.editor.action2;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CameraModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFiguresList;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.resetAction)
public class ResetAction extends AbstractOrieditaAction{
    @Inject
    FoldedFiguresList foldedFiguresList;
    @Inject
    CanvasModel canvasModel;
    @Inject @Named("mainCreasePattern_Worker")
    CreasePattern_Worker mainCreasePatternWorker;
    @Inject
    CameraModel creasePatternCameraModel;

    @Inject
    public ResetAction() {

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        mainCreasePatternWorker.clearCreasePattern();
        creasePatternCameraModel.reset();
        foldedFiguresList.removeAllElements();

        canvasModel.setMouseMode(MouseMode.FOLDABLE_LINE_DRAW_71);

        mainCreasePatternWorker.record();
        mainCreasePatternWorker.auxRecord();
    }
}
