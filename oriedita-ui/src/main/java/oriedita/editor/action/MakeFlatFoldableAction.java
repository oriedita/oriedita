package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.makeFlatFoldableAction)
public class MakeFlatFoldableAction extends AbstractOrieditaAction {

    @Inject
    CanvasModel canvasModel;

    @Inject @Named("mainCreasePattern_Worker")
    CreasePattern_Worker mainCreasePatternWorker;

    @Inject
    public MakeFlatFoldableAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        canvasModel.setMouseMode(MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38);
        canvasModel.setMouseModeAfterColorSelection(MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38);

        mainCreasePatternWorker.unselect_all(false);
    }
}
