package oriedita.editor.service;

import java.util.zip.ZipInputStream;

public interface ApplicationModelPersistenceService {
    void init();

    void importApplicationModel(ZipInputStream zis);
}
