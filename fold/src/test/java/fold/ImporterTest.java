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

        Assertions.assertEquals("Crease Pattern Editor", foldFile.getCreator());
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

        Assertions.assertEquals("oriedita", foldFile.getCreator());
        Assertions.assertEquals(1.1, foldFile.getSpec());
        Assertions.assertNull(foldFile.getAuthor());
        Assertions.assertNull(foldFile.getTitle());
        Assertions.assertNull(foldFile.getDescription());
        Assertions.assertEquals(0, foldFile.getClasses().size());
        Assertions.assertEquals(0, foldFile.getFrames().size());

        Assertions.assertNull(foldFile.getFrameAuthor());
        Assertions.assertNull(foldFile.getFrameTitle());
        Assertions.assertNull(foldFile.getFrameDescription());
        Assertions.assertNull(foldFile.getFrameClasses());
        Assertions.assertNull(foldFile.getAttributes());
        Assertions.assertNull(foldFile.getUnit());

        Assertions.assertEquals(0, foldFile.getVertices().size());
    }

    @Test
    public void testMultipleFrame() throws Exception {
        FoldFile foldFile = loadFile("fold/multiple-frame.fold");

        Assertions.assertEquals(2, foldFile.getFrames().size());

        Assertions.assertEquals("a frame", foldFile.getFrames().get(0).getFrameTitle());
        Assertions.assertEquals("other frame", foldFile.getFrames().get(1).getFrameTitle());
    }

    @Test
    public void testMetadata() throws Exception {
        FoldFile foldFile = loadFile("fold/meta.fold");

        Assertions.assertEquals("test", foldFile.getAuthor());
        Assertions.assertEquals("The description", foldFile.getDescription());
    }

    @Test
    public void testMainFrame() throws Exception {
        FoldFile foldFile = loadFile("fold/main_frame.fold");

        Assertions.assertEquals("f_author", foldFile.getFrameAuthor());
        Assertions.assertEquals("f_title", foldFile.getFrameTitle());
        Assertions.assertEquals("f_description", foldFile.getFrameDescription());
        Assertions.assertEquals("unit", foldFile.getUnit());
        Assertions.assertEquals(List.of("creasePattern"), foldFile.getFrameClasses());
        Assertions.assertEquals(Arrays.asList("2D", "nonSelfIntersecting"), foldFile.getAttributes());
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
