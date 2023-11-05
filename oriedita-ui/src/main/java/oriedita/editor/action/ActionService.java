package oriedita.editor.action;

import java.util.List;

public interface ActionService {
    /**
     * Perform logic registration
     * @param orieditaAction action attached with custom logic
     */
    void registerAction(OrieditaAction orieditaAction);

    /**
     * Return registered actions
     * @return List of OrieditaActions
     */
    List<OrieditaAction> getAllRegisteredActions();
}
