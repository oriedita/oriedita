package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.databinding.CanvasModel;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.selectAnd3ClickAction)
public class SelectAnd3ClickAction extends AbstractOrieditaAction {
    @Inject
    CanvasModel canvasModel;

    @Override
    public void actionPerformed(ActionEvent e) {
        canvasModel.setCkbox_add_frame_SelectAnd3click_isSelected(canvasModel.isCkbox_add_frame_SelectAnd3click_isSelected());
    }
}
