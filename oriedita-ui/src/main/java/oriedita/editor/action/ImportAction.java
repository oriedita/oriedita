package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.service.FileSaveService;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.IMPORT)
public class ImportAction extends AbstractOrieditaAction {
    @Inject
    FileSaveService fileSaveService;

    @Inject
    public ImportAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        fileSaveService.importFile();
    }
}
