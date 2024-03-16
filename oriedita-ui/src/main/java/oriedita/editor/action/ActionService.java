package oriedita.editor.action;

import java.util.List;
import java.util.Map;

public interface ActionService {
    /**
     * Perform logic registration
     * @param orieditaAction action attached with custom logic
     */
    void registerAction(ActionType actionType, OrieditaAction orieditaAction);

    /**
     * Return registered actions
     * @return Map of OrieditaActions
     */
    Map<ActionType, OrieditaAction> getAllRegisteredActions();
}
