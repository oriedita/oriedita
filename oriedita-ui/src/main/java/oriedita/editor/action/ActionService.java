package oriedita.editor.action;

import java.awt.event.ActionEvent;
import java.util.Map;

public interface ActionService {
    /**
     * Perform logic registration
     * @param orieditaAction action attached with custom logic
     */
    void registerAction(ActionType actionType, OrieditaAction orieditaAction);

    @FunctionalInterface
    interface ActionLambda {
        void actionPerformed(ActionEvent e);
    }

    default void registerAction(ActionType actionType, ActionLambda action) {
        registerAction(actionType, new AbstractOrieditaAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.actionPerformed(e);
            }
        });
    }

    /**
     * Return registered actions
     * @return Map of OrieditaActions
     */
    Map<ActionType, OrieditaAction> getAllRegisteredActions();
}
