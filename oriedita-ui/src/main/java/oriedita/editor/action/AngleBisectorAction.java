package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.angleBisectorAction)
public class AngleBisectorAction extends AbstractOrieditaAction {
    @Inject
    CanvasModel canvasModel;

    @Inject @Named("mainCreasePattern_Worker")
    CreasePattern_Worker mainCreasePatternWorker;

    @Inject
    public AngleBisectorAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        canvasModel.setMouseMode(MouseMode.SQUARE_BISECTOR_7);
        canvasModel.setMouseModeAfterColorSelection(MouseMode.SQUARE_BISECTOR_7);

        mainCreasePatternWorker.unselect_all();
    }
}
