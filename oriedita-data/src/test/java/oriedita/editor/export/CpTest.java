package oriedita.editor.export;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import oriedita.editor.save.Save;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class CpTest {

    @Test
    void testLoadAndSaveCpFile() throws IOException {
        URL birdbase = getClass().getClassLoader().getResource("square.cp"); // very simple file so rounding doesnt make the test fail
        assert birdbase != null;
        Save save = Cp.importFile(birdbase.openStream());

        File saveFile = File.createTempFile("export", ".cp");
        Cp.exportFile(save, saveFile);

        String expected = Files.readString(saveFile.toPath());

        String actual = Files.readString(new File(birdbase.getFile()).toPath());

        Assertions.assertEquals(expected, actual);
    }


}