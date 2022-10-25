package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.databinding.BackgroundModel;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.backgroundLockAction)
public class BackgroundLockAction extends AbstractOrieditaAction {
    @Inject
    BackgroundModel backgroundModel;

    @Inject
    public BackgroundLockAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        backgroundModel.setLockBackground(!backgroundModel.isLockBackground());
    }
}
