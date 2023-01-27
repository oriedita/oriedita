package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.moveCreasePatternAction)
public class MoveCreasePatternAction extends AbstractOrieditaAction{
    @Inject
    CanvasModel canvasModel;

    @Inject
    public MoveCreasePatternAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        canvasModel.setMouseMode(MouseMode.MOVE_CREASE_PATTERN_2);
    }
}
