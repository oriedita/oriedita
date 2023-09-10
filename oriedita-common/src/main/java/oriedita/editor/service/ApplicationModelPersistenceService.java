package oriedita.editor.service;

import java.io.File;
import java.util.zip.ZipInputStream;

public interface ApplicationModelPersistenceService {
    void init();

    void importApplicationModel(ZipInputStream zis);
}
