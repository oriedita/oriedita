package fold;

import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import oriedita.editor.exception.FileReadingException;
import oriedita.editor.export.Fold;
import oriedita.editor.export.fold.FoldSave;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Objects;

public class FoldFileFormatTest {
    @Test
    public void testLoadFoldFile() throws URISyntaxException, FileReadingException, IOException {
        File saveFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("fold/diagonal-cp.fold")).toURI());

        FoldSave foldSave = Fold.importFoldFile(saveFile);

        Assertions.assertEquals("Crease Pattern Editor", foldSave.getFileCreator());
    }

    @Test
    public void testLoadAndSaveFoldFile() throws URISyntaxException, FileReadingException, IOException, JSONException {
        File saveFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("fold/diagonal-cp.fold")).toURI());

        FoldSave foldSave = Fold.importFoldFile(saveFile);

        File exportFile = File.createTempFile("export", ".fold");

        Fold.exportFoldFile(exportFile, foldSave);

        String expected = Files.readString(saveFile.toPath());
        String actual = Files.readString(exportFile.toPath());
        JSONAssert.assertEquals(expected, actual, false);
    }
}
