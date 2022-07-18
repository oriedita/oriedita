package fold;

import fold.model.internal.FoldFile;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.util.Objects;
import java.util.ServiceLoader;

public abstract class BaseFoldTest {
    Importer importer;
    Exporter exporter;

    @BeforeEach
    void beforeEach() {
        importer = ServiceLoader.load(Importer.class).iterator().next();
        exporter = ServiceLoader.load(Exporter.class).iterator().next();
    }

    FoldFile loadFile(String name) throws FoldFileFormatException {
        return importer.importFile(new File(Objects.requireNonNull(getClass().getClassLoader().getResource(name)).getFile()));
    }
}
