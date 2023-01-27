package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.databinding.BackgroundModel;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.backgroundToggleAction)
public class BackgroundToggleAction extends AbstractOrieditaAction{
    @Inject
    BackgroundModel backgroundModel;

    @Inject
    public BackgroundToggleAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        backgroundModel.setDisplayBackground(!backgroundModel.isDisplayBackground());
    }
}
