package fold;

import fold.io.FoldReader;
import fold.io.FoldWriter;
import fold.model.FoldFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Objects;

public class ReaderWriterTest extends BaseFoldTest {
    /**
     * Loading a file and writing it to a new file should result in an equal file.
     */
    @Test
    public void testLoadAndSaveFoldFile() throws Exception {
        String saveFile = Objects.requireNonNull(getClass().getClassLoader().getResource("fold/full.fold")).getFile();

        FoldFile foldFile;
        try (FileInputStream in = new FileInputStream(saveFile)) {
            FoldReader foldReader = new FoldReader(in);
            foldFile = foldReader.read();
        }

        File exportFile = File.createTempFile("export", ".fold");

        FoldWriter foldWriter;
        try (FileOutputStream out = new FileOutputStream(exportFile)) {
            foldWriter = new FoldWriter(out);
            foldWriter.write(foldFile);
        }


        String expected;
        try (FileInputStream in = new FileInputStream(saveFile)) {
            expected = new String(new BufferedInputStream(in).readAllBytes());
        }
        String actual;
        try (FileInputStream in = new FileInputStream(saveFile)) {
            actual = new String(new BufferedInputStream(in).readAllBytes());
        }

        JSONAssert.assertEquals(expected, actual, true);
    }

    @Test
    public void testEquals() throws Exception {
        File saveFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("fold/full.fold")).getFile());

        FoldFile foldFile1;
        try (FileInputStream inputStream = new FileInputStream(saveFile)) {
            FoldReader foldReader = new FoldReader(inputStream);
            foldFile1 = foldReader.read();
        }

        FoldFile foldFile2;
        try (FileInputStream inputStream = new FileInputStream(saveFile)) {
            FoldReader foldReader = new FoldReader(inputStream);
            foldFile2 = foldReader.read();
        }

        Assertions.assertEquals(foldFile1, foldFile2);
    }
}
