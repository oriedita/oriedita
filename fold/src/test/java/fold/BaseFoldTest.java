package fold;

import fold.impl.DefaultExporter;
import fold.impl.DefaultImporter;
import fold.model.FoldFile;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.util.Objects;

public abstract class BaseFoldTest {
    Importer<FoldFile> importer;
    Exporter<FoldFile> exporter;

    @BeforeEach
    void beforeEach() {
        importer = new DefaultImporter();
        exporter = new DefaultExporter<>();
    }

    FoldFile loadFile(String name) throws FoldFileFormatException {
        return importer.importFile(new File(Objects.requireNonNull(getClass().getClassLoader().getResource(name)).getFile()));
    }
}
