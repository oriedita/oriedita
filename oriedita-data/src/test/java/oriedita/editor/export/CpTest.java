package oriedita.editor.export;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import oriedita.editor.save.Save;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

class CpTest {

    @Test
    void testLoadAndSaveCpFile() throws IOException {
        URL birdbase = getClass().getClassLoader().getResource("square.cp"); // very simple file so rounding doesnt make the test fail
        assert birdbase != null;
        Save save = CpExporter.importFile(birdbase.openStream());

        File saveFile = File.createTempFile("export", ".cp");
        CpExporter.exportFile(save, saveFile);

        String expected = Files.readString(saveFile.toPath()).replace("\r","");

        String actual = Files.readString(new File(birdbase.getFile()).toPath()).replace("\r","");

        Assertions.assertEquals(expected, actual);
    }


}
