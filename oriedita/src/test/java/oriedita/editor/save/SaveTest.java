package oriedita.editor.save;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.TextWorker;
import oriedita.editor.canvas.impl.CreasePattern_Worker_Impl;
import oriedita.editor.databinding.*;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.exception.FileReadingException;
import oriedita.editor.service.FileSaveService;
import oriedita.editor.service.ResetService;
import oriedita.editor.service.impl.FileSaveServiceImpl;
import oriedita.editor.service.impl.DequeHistoryState;
import oriedita.editor.service.impl.SingleTaskExecutorServiceImpl;
import oriedita.editor.text.Text;
import origami.Epsilon;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

/**
 * Test if the current version of the save is compatible with the previous version.
 */
public class SaveTest {

    private FileSaveService fileSaveService;
    private CreasePattern_Worker mainCreasePatternWorker;

    private FileModel fileModel;

    @BeforeEach
    public void setupBeforeEach() {
        Camera creasePatternCamera = new Camera();
        fileModel = new FileModel();
        ApplicationModel applicationModel = new ApplicationModel();
        CanvasModel canvasModel = new CanvasModel();
        GridModel gridModel = new GridModel();
        FoldedFigureModel foldedFigureModel = new FoldedFigureModel();
        SelectedTextModel textModel = new SelectedTextModel();
        TextWorker textWorker = new TextWorker();
        mainCreasePatternWorker = new CreasePattern_Worker_Impl(creasePatternCamera, new DequeHistoryState(), new DequeHistoryState(), new FoldLineSet(), new FoldLineSet(), new SingleTaskExecutorServiceImpl(), canvasModel, applicationModel, gridModel, foldedFigureModel, fileModel, null, textWorker, textModel);
        ResetService resetService = () -> {};
        fileSaveService = new FileSaveServiceImpl(null, creasePatternCamera, mainCreasePatternWorker, null, fileModel, applicationModel, canvasModel, new FoldedFiguresList(), resetService, null);
    }

    @ParameterizedTest
    @CsvSource({"save/save-0.0.11.ori,false", "save/save-0.0.12.ori,false", "save/save-1.0.0-ALPHA.13.ori,false",
            "save/save-1.0.0-v1.1.ori,true"})
    public void testSave(String filename, boolean hasText) throws URISyntaxException {
        File saveFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(filename)).toURI());

        try {
            fileSaveService.openFile(saveFile);

            FoldLineSet foldLineSet = mainCreasePatternWorker.getFoldLineSet();
            List<Circle> list = (List<Circle>) foldLineSet.getCircles();

            Assertions.assertEquals(1, list.size(), "Expected one circle");

            Circle circle = list.get(0);
            Assertions.assertEquals(50.0, circle.getR());
            Assertions.assertEquals(100.0, circle.getX(), Epsilon.UNKNOWN_1EN7);
            Assertions.assertEquals(-100.0, circle.getY());

            FoldLineSet auxFoldLineSet = mainCreasePatternWorker.getAuxFoldLineSet();
            Assertions.assertEquals(2, auxFoldLineSet.getTotal());

            LineSegment lineSegment1 = auxFoldLineSet.get(1);
            Assertions.assertEquals(LineColor.ORANGE_4, lineSegment1.getColor());
            Assertions.assertEquals(new Point(150.0, 50.0), lineSegment1.getA());
            Assertions.assertEquals(new Point(-150.0, 50.0), lineSegment1.getB());

            LineSegment lineSegment2 = auxFoldLineSet.get(2);
            Assertions.assertEquals(LineColor.YELLOW_7, lineSegment2.getColor());
            Assertions.assertEquals(new Point(150.0, 100.0), lineSegment2.getA());
            Assertions.assertEquals(new Point(-150.0, 100.0), lineSegment2.getB());

            Assertions.assertEquals(8, foldLineSet.getTotal());

            LineSegment mountainLineSegment = foldLineSet.get(5);
            Assertions.assertEquals(LineColor.RED_1, mountainLineSegment.getColor());

            LineSegment valleyLineSegment = foldLineSet.get(6);
            Assertions.assertEquals(LineColor.BLUE_2, valleyLineSegment.getColor());

            LineSegment edgeLineSegment = foldLineSet.get(7);
            Assertions.assertEquals(LineColor.BLACK_0, edgeLineSegment.getColor());

            LineSegment auxLineSegment = foldLineSet.get(8);
            Assertions.assertEquals(LineColor.CYAN_3, auxLineSegment.getColor());

            if (hasText) {
                Assertions.assertEquals(1, mainCreasePatternWorker.getTextWorker().getTexts().size());
                Text t = mainCreasePatternWorker.getTextWorker().getTexts().get(0);
                Assertions.assertEquals("Test", t.getText());
            }
        } catch (FileReadingException e) {
            Assertions.fail(e);
        }
    }

    @ParameterizedTest
    @CsvSource({"save/save-0.0.11.ori,false", "save/save-0.0.12.ori,false", "save/save-1.0.0-ALPHA.13.ori,false",
            "save/save-1.0.0-v1.1.ori,true"})
    void testSaveAndReload(String fileName, boolean hasText) throws URISyntaxException {
        testSave(fileName, hasText);
        File saveFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("save/tmp_save.ori")).toURI());
        fileModel.setSavedFileName(saveFile.getPath());
        fileSaveService.saveFile();
        testSave("save/tmp_save.ori", hasText);
    }

    @Test
    void testUnknownVersionDetection() throws URISyntaxException {
        File saveFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("save/corrupted.ori")).toURI());
        try {
            Save save = fileSaveService.readImportFile(saveFile, false);
            Assertions.assertTrue(saveFile.exists());
            Assertions.assertNull(save);

        } catch (FileReadingException e) {
            Assertions.fail(e);
        }
    }
}
