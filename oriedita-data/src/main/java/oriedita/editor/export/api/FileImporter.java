package oriedita.editor.export.api;

import oriedita.editor.save.Save;

import java.io.File;
import java.io.IOException;

public interface FileImporter {
    boolean supports(File filename);

    Save doImport(File file) throws IOException;
}
