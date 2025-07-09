package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.FoldLineAdditionalInputMode;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.drawCreaseFreeAction)
public class DrawCreaseFreeAction extends AbstractOrieditaAction implements MouseModeAction {
    @Inject
    CanvasModel canvasModel;
    @Inject @Named("mainCreasePattern_Worker")
    CreasePattern_Worker mainCreasePatternWorker;

    @Inject
    public DrawCreaseFreeAction() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.POLY_LINE_0);
        canvasModel.setMouseMode(MouseMode.DRAW_CREASE_FREE_1);
        canvasModel.setMouseModeAfterColorSelection(MouseMode.DRAW_CREASE_FREE_1);

        mainCreasePatternWorker.unselect_all(false);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.DRAW_CREASE_FREE_1;
    }
}
