package fold;

import fold.model.FoldFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;

public class ImporterTest extends BaseFoldTest {
    @Test
    public void testLoadFoldFile() throws FoldFileFormatException {
        File saveFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("fold/basic.fold")).getFile());

        FoldFile foldFile = importer.importFile(saveFile);

        Assertions.assertEquals("Crease Pattern Editor", foldFile.getFile().getCreator());
    }

    @Test
    public void testCustomProperty() throws Exception {
        FoldFile foldFile = loadFile("fold/basic.fold");

        Object customProperty = foldFile.getCustomPropertyMap().get("cpedit:page");

        Map<String, Integer> expectedProperty = new LinkedHashMap<>();
        expectedProperty.put("xMin", 0);
        expectedProperty.put("yMin", 0);
        expectedProperty.put("xMax", 1);
        expectedProperty.put("yMax", 1);

        Assertions.assertEquals(expectedProperty, customProperty);

        Assertions.assertEquals("bar", foldFile.getCustomPropertyMap().get("oriedita:foo"));
    }

    @Test
    public void testEmpty() throws Exception {
        FoldFile foldFile = loadFile("fold/empty.fold");

        Assertions.assertEquals("oriedita", foldFile.getFile().getCreator());
        Assertions.assertEquals(1.1, foldFile.getFile().getSpec());
        Assertions.assertNull(foldFile.getFile().getAuthor());
        Assertions.assertNull(foldFile.getFile().getTitle());
        Assertions.assertNull(foldFile.getFile().getDescription());
        Assertions.assertEquals(0, foldFile.getFile().getClasses().size());
        Assertions.assertEquals(0, foldFile.getFile().getFrames().size());

        Assertions.assertNull(foldFile.getFrame().getAuthor());
        Assertions.assertNull(foldFile.getFrame().getTitle());
        Assertions.assertNull(foldFile.getFrame().getDescription());
        Assertions.assertNull(foldFile.getFrame().getClasses());
        Assertions.assertNull(foldFile.getFrame().getAttributes());
        Assertions.assertNull(foldFile.getFrame().getUnit());

        Assertions.assertEquals(0, foldFile.getVertices().getVertices().size());
        Assertions.assertEquals(0, foldFile.getVertices().getCoords().size());
        Assertions.assertEquals(0, foldFile.getVertices().getFaces().size());
    }

    @Test
    public void testMultipleFrame() throws Exception {
        FoldFile foldFile = loadFile("fold/multiple-frame.fold");

        Assertions.assertEquals(2, foldFile.getFile().getFrames().size());

        Assertions.assertEquals("a frame", foldFile.getFile().getFrames().get(0).getFrame().getTitle());
        Assertions.assertEquals("other frame", foldFile.getFile().getFrames().get(1).getFrame().getTitle());
    }

    @Test
    public void testMetadata() throws Exception {
        FoldFile foldFile = loadFile("fold/meta.fold");

        Assertions.assertEquals("test", foldFile.getFile().getAuthor());
        Assertions.assertEquals("The description", foldFile.getFile().getDescription());
    }

    @Test
    public void testMainFrame() throws Exception {
        FoldFile foldFile = loadFile("fold/main_frame.fold");

        Assertions.assertEquals("f_author", foldFile.getFrame().getAuthor());
        Assertions.assertEquals("f_title", foldFile.getFrame().getTitle());
        Assertions.assertEquals("f_description", foldFile.getFrame().getDescription());
        Assertions.assertEquals("unit", foldFile.getFrame().getUnit());
        Assertions.assertEquals(List.of("creasePattern"), foldFile.getFrame().getClasses());
        Assertions.assertEquals(Arrays.asList("2D", "nonSelfIntersecting"), foldFile.getFrame().getAttributes());
    }

    @Test
    public void testInvalid() {
        Assertions.assertThrows(FoldFileFormatException.class, () -> loadFile("fold/invalid.fold"), "Invalid file throws an exception");
    }

    @Test
    public void testInvalidProperty() {
        Assertions.assertThrows(FoldFileFormatException.class, () -> loadFile("fold/invalid-property.fold"));
    }
}
