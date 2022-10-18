package oriedita.editor.action2;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.databinding.ApplicationModel;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.lineWidthDecreaseAction)
public class LineWidthDecreaseAction extends AbstractOrieditaAction {
    @Inject
    ApplicationModel applicationModel;

    @Inject
    public LineWidthDecreaseAction() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        applicationModel.decreaseLineWidth();
    }
}
