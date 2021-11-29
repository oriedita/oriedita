package fold;

import fold.model.FoldFile;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class FoldFileFormatTest {
    @Test
    public void testLoadFoldFile() throws FoldFileFormatException {
        File saveFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("fold/diagonal-cp.fold")).getFile());

        FoldFile foldFile = FoldFactory.foldImport().importFoldFile(saveFile);

        Assertions.assertEquals("Crease Pattern Editor", foldFile.getFile().getCreator());
    }

    @Test
    public void testLoadAndSaveFoldFile() throws IOException, JSONException, FoldFileFormatException {
        File saveFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("fold/diagonal-cp.fold")).getFile());

        FoldFile foldFile = FoldFactory.foldImport().importFoldFile(saveFile);

        File exportFile = File.createTempFile("export", ".fold");

        FoldFactory.foldExport().exportFoldFile(exportFile, foldFile);

        String expected = Files.readString(saveFile.toPath());
        String actual = Files.readString(exportFile.toPath());
        JSONAssert.assertEquals(expected, actual, false);
    }
}
