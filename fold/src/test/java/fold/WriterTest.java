package fold;

import fold.io.FoldWriter;
import fold.model.FoldFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;

public class WriterTest extends BaseFoldTest {
    /**
     * Test saving of a default FoldFile without any changes.
     */
    @Test
    public void testSaveEmpty() throws Exception {
        FoldFile foldFile = new FoldFile();

        File exportFile = File.createTempFile("exportSaveEmpty", ".fold");

        try (FileOutputStream outputStream = new FileOutputStream(exportFile)) {
            FoldWriter foldWriter = new FoldWriter(outputStream);
            foldWriter.write(foldFile);
        }

        String contents = Files.readString(exportFile.toPath());

        JSONAssert.assertEquals("{\"file_spec\": 1.1, \"file_creator\": \"oriedita\" }", contents, true);
    }

    @Test
    public void testSave() throws Exception {
        FoldFile foldFile = new FoldFile();
        foldFile.setAuthor("TestSuite");

        File exportFile = File.createTempFile("testSave", ".fold");

        try (FileOutputStream outputStream = new FileOutputStream(exportFile)) {
            FoldWriter foldWriter = new FoldWriter(outputStream);
            foldWriter.write(foldFile);
        }

        String contents = Files.readString(exportFile.toPath());

        JSONAssert.assertEquals("{\"file_spec\": 1.1, \"file_creator\": \"oriedita\", \"file_author\": \"TestSuite\" }", contents, true);
    }

    @Test
    public void testSaveCustomProperty() throws Exception {
        FoldFile foldFile = new FoldFile();

        foldFile.setCustomProperty("fold", "test", "testvalue");

        File exportFile = File.createTempFile("exportSaveCustomProperty", ".fold");

        try (FileOutputStream outputStream = new FileOutputStream(exportFile)) {
            FoldWriter foldWriter = new FoldWriter(outputStream);
            foldWriter.write(foldFile);
        }

        String contents = Files.readString(exportFile.toPath());

        JSONAssert.assertEquals("{\"fold:test\": \"testvalue\", \"file_spec\": 1.1, \"file_creator\": \"oriedita\"}", contents, true);
    }

    @Test
    public void testSaveInvalidCustomProperty() throws Exception {
        FoldFile foldFile = new FoldFile();
        foldFile.getCustomPropertyMap().put("my_custom_property", "yes");

        File exportFile = File.createTempFile("testSaveInvalidProperty", ".fold");

        Assertions.assertThrows(FoldFileFormatException.class, () -> {
            try (FileOutputStream outputStream = new FileOutputStream(exportFile)) {
                FoldWriter foldWriter = new FoldWriter(outputStream);
                foldWriter.write(foldFile);
            }
        });
    }
}
