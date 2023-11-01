package oriedita.editor.service;

import oriedita.editor.action.ActionType;

import java.util.List;

public interface ActionService {
    void registerAction(ActionType actionType, ActionMethod actionMethod);

    List<ActionType> getAllActions();

    interface ActionMethod{
        void run();
    }
}
