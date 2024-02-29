package oriedita.editor.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import oriedita.editor.action.OrieditaAction;
import oriedita.editor.action.ActionService;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ActionServiceImpl implements ActionService {
    private final List<OrieditaAction> registeredActions;
    private final Instance<OrieditaAction> actions;

    @Inject
    public ActionServiceImpl(@Any Instance<OrieditaAction> actions){
        this.actions = actions;
        registeredActions = new ArrayList<>();
        extractActionInstances(actions);
    }

    @Override
    public synchronized void registerAction(OrieditaAction orieditaAction) { registeredActions.add(orieditaAction); }

    @Override
    public List<OrieditaAction> getAllRegisteredActions() { return registeredActions; }

    private void extractActionInstances(Instance<OrieditaAction> actions){ actions.stream().forEach(this::registerAction); }
}
