package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.databinding.ApplicationModel;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.pointSizeIncreaseAction)
public class PointSizeIncreaseAction extends AbstractOrieditaAction{
    @Inject
    ApplicationModel applicationModel;

    @Inject
    public PointSizeIncreaseAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        applicationModel.increasePointSize();
    }
}
