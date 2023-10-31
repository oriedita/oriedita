package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.databinding.CanvasModel;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.deg2Action)
public class Deg2Action extends AbstractOrieditaAction{
    @Inject
    AngleSystemModel angleSystemModel;

    @Inject
    CanvasModel canvasModel;

    @Override
    public void actionPerformed(ActionEvent e) {
        angleSystemModel.setAngleSystemInputType(AngleSystemModel.AngleSystemInputType.DEG_2);
        canvasModel.setMouseMode(MouseMode.ANGLE_SYSTEM_16);
    }
}
