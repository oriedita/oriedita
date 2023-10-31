package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.foldableLinePlusGridInputAction)
public class FoldableLinePlusGridInputButton extends AbstractOrieditaAction{

    @Inject
    CanvasModel canvasModel;

    @Inject
    @Named("mainCreasePattern_Worker")
    CreasePattern_Worker mainCreasePatternWorker;

    @Override
    public void actionPerformed(ActionEvent e) {
        canvasModel.setMouseMode(MouseMode.FOLDABLE_LINE_INPUT_39);
        canvasModel.setMouseModeAfterColorSelection(MouseMode.FOLDABLE_LINE_INPUT_39);

        mainCreasePatternWorker.unselect_all(false);
    }
}
