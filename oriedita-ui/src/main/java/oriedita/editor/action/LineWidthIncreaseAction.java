package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.databinding.ApplicationModel;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.lineWidthIncreaseAction)
public class LineWidthIncreaseAction extends AbstractOrieditaAction {
    @Inject
    ApplicationModel applicationModel;

    @Inject
    public LineWidthIncreaseAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        applicationModel.increaseLineWidth();
    }
}