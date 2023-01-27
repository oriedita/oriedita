package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.databinding.ApplicationModel;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.pointSizeDecreaseAction)
public class PointSizeDecreaseAction extends AbstractOrieditaAction {
    @Inject
    ApplicationModel applicationModel;

    @Inject
    public PointSizeDecreaseAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        applicationModel.decreasePointSize();
    }
}
