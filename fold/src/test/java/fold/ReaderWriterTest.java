package fold;

import fold.model.FoldFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.File;
import java.nio.file.Files;
import java.util.Objects;

public class ReaderWriterTest extends BaseFoldTest {
    /**
     * Loading a file and writing it to a new file should result in an equal file.
     */
    @Test
    public void testLoadAndSaveFoldFile() throws Exception {
        File saveFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("fold/full.fold")).getFile());

        FoldFile foldFile = reader.read(saveFile);

        File exportFile = File.createTempFile("export", ".fold");

        writer.write(exportFile, foldFile);

        String expected = Files.readString(saveFile.toPath());
        String actual = Files.readString(exportFile.toPath());

        JSONAssert.assertEquals(expected, actual, true);
    }

    @Test
    public void testEquals() throws Exception {
        File saveFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("fold/full.fold")).getFile());

        FoldFile foldFile1 = reader.read(saveFile);
        FoldFile foldFile2 = reader.read(saveFile);

        Assertions.assertEquals(foldFile1, foldFile2);
    }
}
