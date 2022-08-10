package oriedita.editor.save;

import fold.Exporter;
import fold.Importer;
import fold.impl.CustomImporter;
import fold.impl.DefaultExporter;
import fold.impl.DefaultImporter;
import fold.model.FoldFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.File;
import java.nio.file.Files;
import java.util.Objects;

public class FoldTest {
    Importer<OrieditaFoldFile> importer;
    Exporter<OrieditaFoldFile> exporter;

    @BeforeEach
    void beforeEach() {
        importer = new CustomImporter<>(OrieditaFoldFile.class);
        exporter = new DefaultExporter<>();
    }

    /**
     * Loading a file and writing it to a new file should result in an equal file.
     */
    @Test
    public void testLoadAndSaveFoldFile() throws Exception {
        File saveFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("fold/oriedita.fold")).getFile());

        OrieditaFoldFile foldFile = importer.importFile(saveFile);

        File exportFile = File.createTempFile("export", ".fold");

        exporter.exportFile(exportFile, foldFile);

        String expected = Files.readString(saveFile.toPath());
        String actual = Files.readString(exportFile.toPath());

        JSONAssert.assertEquals(expected, actual, true);
    }
}
