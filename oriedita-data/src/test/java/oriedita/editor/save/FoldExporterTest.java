package oriedita.editor.save;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import fold.io.CustomFoldReader;
import fold.io.CustomFoldWriter;
import fold.model.FoldFile;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import oriedita.editor.databinding.GridModel;
import oriedita.editor.exception.FileReadingException;
import oriedita.editor.export.FoldExporter;
import oriedita.editor.export.FoldImporter;
import oriedita.editor.text.Text;
import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.element.Circle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ExtendWith({SnapshotExtension.class})
public class FoldExporterTest {
    Expect expect;

    /**
     * Loading a .fold file missing all but the absolute essential fields should not fail
     * <p>
     *     this file contains only file_spec, vertices_coords, edges_vertices and edges_assigment
     * </p>
     */
    @Test
    public void testLoadMinimalFoldFile() throws Exception {
        File saveFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("fold/oriedita_minimal.fold")).getFile());
        var importer = new FoldImporter();
        var save = importer.doImport(saveFile);
        Assertions.assertEquals(11, save.getLineSegments().size());
    }

    /**
     * Loading a file and writing it to a new file should result in an equal file.
     * <p>
     * This test makes sure that anything that is read is also written back to the file.
     */
    @Test
    public void testLoadAndSaveFoldFile() throws Exception {
        File saveFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("fold/oriedita.fold")).getFile());


        OrieditaFoldFile foldFile;
        try (FileInputStream inputStream = new FileInputStream(saveFile)) {
            CustomFoldReader<OrieditaFoldFile> orieditaFoldFileCustomFoldReader = new CustomFoldReader<>(OrieditaFoldFile.class, inputStream);
            foldFile = orieditaFoldFileCustomFoldReader.read();
        }

        File exportFile = File.createTempFile("export", ".fold");

        try (FileOutputStream fileOutputStream = new FileOutputStream(exportFile)) {
            CustomFoldWriter<FoldFile> foldFileCustomFoldWriter = new CustomFoldWriter<>(fileOutputStream);
            foldFileCustomFoldWriter.write(foldFile);
        }

        String expected = Files.readString(saveFile.toPath());
        String actual = Files.readString(exportFile.toPath());

        JSONAssert.assertEquals(expected, actual, true);
    }

    @Test
    public void testExportAndImport() throws IOException, JSONException, FileReadingException {
        File saveFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("fold/oriedita.fold")).getFile());

        FoldImporter importer = new FoldImporter();
        FoldExporter exporter = new FoldExporter();
        Save foldSave = importer.importFile(saveFile);


        File exportFile = File.createTempFile("export", ".fold");

        exporter.doExport(foldSave, exportFile);

        String expected = Files.readString(saveFile.toPath());
        String actual = Files.readString(exportFile.toPath());

        JSONAssert.assertEquals(expected, actual, true);
    }

    @Test
    public void testSaveCircles() throws Exception {
        Save save = SaveProvider.createInstance();

        List<Circle> circles = new ArrayList<>();

        Circle circle = new Circle();
        circle.setX(1);
        circle.setY(2);
        circle.setR(10);
        circles.add(circle);

        save.setCircles(circles);

        save.setGridModel(new GridModel());

        FoldExporter f = new FoldExporter();

        LineSegmentSet lineSegmentSet = new LineSegmentSet();
        lineSegmentSet.reset(1);
        OrieditaFoldFile orieditaFoldFile = f.toFoldSave(save, lineSegmentSet);

        Assertions.assertEquals(1, orieditaFoldFile.getCircles().size());

        // We have another object here.
        Assertions.assertNotEquals(circle, orieditaFoldFile.getCircles().get(0));

        Assertions.assertEquals(circle.getX(), orieditaFoldFile.getCircles().get(0).getX());
        Assertions.assertEquals(circle.getY(), orieditaFoldFile.getCircles().get(0).getY());
        Assertions.assertEquals(circle.getR(), orieditaFoldFile.getCircles().get(0).getR());
    }

    @Test
    public void testSaveCirclesFile() throws Exception {
        Save save = SaveProvider.createInstance();

        List<Circle> circles = new ArrayList<>();

        Circle circle = new Circle();
        circle.setX(1);
        circle.setY(2);
        circle.setR(10);
        circles.add(circle);

        save.setCircles(circles);

        FoldExporter f = new FoldExporter();

        File tempFile = File.createTempFile("fold", "fold");
        f.doExport(save, tempFile);

        expect.serializer("json").toMatchSnapshot(Files.readString(tempFile.toPath()));
    }

    @Test
    public void testSaveTexts() throws Exception {
        Save save = SaveProvider.createInstance();

        List<Text> texts = new ArrayList<>();

        Text text = new Text(1, 2, "TestText");
        texts.add(text);

        save.setTexts(texts);

        FoldExporter f = new FoldExporter();

        LineSegmentSet lineSegmentSet = new LineSegmentSet();
        lineSegmentSet.reset(1);
        OrieditaFoldFile orieditaFoldFile = f.toFoldSave(save, lineSegmentSet);

        Assertions.assertEquals(1, orieditaFoldFile.getTexts().size());

        // We have another object here.
        Assertions.assertNotEquals(text, orieditaFoldFile.getTexts().get(0));

        Assertions.assertEquals(text.getX(), orieditaFoldFile.getTexts().get(0).getX());
        Assertions.assertEquals(text.getY(), orieditaFoldFile.getTexts().get(0).getY());
        Assertions.assertEquals(text.getText(), orieditaFoldFile.getTexts().get(0).getText());
    }

    @Test
    public void testSaveTextsFile() throws Exception {
        Save save = SaveProvider.createInstance();

        List<Text> texts = new ArrayList<>();

        Text text = new Text(1, 2, "TestText");
        texts.add(text);

        save.setTexts(texts);

        FoldExporter f = new FoldExporter();

        File tempFile = File.createTempFile("fold", "fold");
        f.doExport(save, tempFile);

        expect.serializer("json").toMatchSnapshot(Files.readString(tempFile.toPath()));
    }
}
