package oriedita.editor.service;

import java.io.File;

public interface ApplicationModelPersistenceService {
    void init();

    void importApplicationModel(File configFile);
}
