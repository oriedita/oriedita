package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.service.TaskExecutorService;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.haltAction)
public class HaltAction extends AbstractOrieditaAction {
    @Inject
    @Named("camvExecutor")
    TaskExecutorService camvTaskExecutor;
    @Inject
    @Named("foldingExecutor")
    TaskExecutorService foldingTaskExecutor;

    @Inject
    public HaltAction() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        camvTaskExecutor.stopTask();
        foldingTaskExecutor.stopTask();
    }
}
