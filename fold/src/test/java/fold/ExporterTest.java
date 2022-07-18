package fold;

import fold.model.internal.FoldFile;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.File;
import java.nio.file.Files;

public class ExporterTest extends BaseFoldTest {
    @Test
    public void testSaveEmpty() throws Exception {
        FoldFile foldFile = new FoldFile();

        File exportFile = File.createTempFile("exportSaveEmpty", ".fold");

        exporter.exportFile(exportFile, foldFile);

        String contents = Files.readString(exportFile.toPath());

        JSONAssert.assertEquals("{\"file_spec\": 1.1, \"file_creator\": \"oriedita\" }", contents, true);
    }

    @Test
    public void testSaveCustomProperty() throws Exception {
        FoldFile foldFile = new FoldFile();

        foldFile.getCustomPropertyMap().put("fold:test", "testvalue");

        File exportFile = File.createTempFile("exportSaveCustomProperty", ".fold");

        exporter.exportFile(exportFile, foldFile);

        String contents = Files.readString(exportFile.toPath());

        JSONAssert.assertEquals("{\"fold:test\": \"testvalue\", \"file_spec\": 1.1, \"file_creator\": \"oriedita\"}", contents, true);
    }
}
