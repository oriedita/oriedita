package oriedita.editor.action2;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.databinding.ApplicationModel;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.antiAliasToggleAction)
public class AntiAliasToggleAction extends AbstractOrieditaAction {
    ApplicationModel applicationModel;

    @Inject
    public AntiAliasToggleAction(ApplicationModel applicationModel) {
        this.applicationModel = applicationModel;
        // This button has text value a_a
        putValue(NAME, "a_a");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        applicationModel.toggleAntiAlias();
    }
}
