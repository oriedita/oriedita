package origami_editor.editor.save;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import origami.Epsilon;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.canvas.CreasePattern_Worker;
import origami_editor.editor.databinding.*;
import origami_editor.editor.drawing.tools.Camera;
import origami_editor.editor.exception.FileReadingException;
import origami_editor.editor.service.FileSaveService;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

/**
 * Test if the current version of the save is compatible with the previous version.
 */
public class SaveTest {
    @Test
    public void testSave0011() throws URISyntaxException {
        Camera creasePatternCamera = new Camera();
        FileModel fileModel = new FileModel();
        ApplicationModel applicationModel = new ApplicationModel();
        CanvasModel canvasModel = new CanvasModel();
        GridModel gridModel = new GridModel();
        FoldedFigureModel foldedFigureModel = new FoldedFigureModel();
        CreasePattern_Worker mainCreasePatternWorker = new CreasePattern_Worker(creasePatternCamera,
                canvasModel,
                applicationModel,
                gridModel,
                foldedFigureModel,
                fileModel,
                null,
                null);
        FileSaveService fileSaveService = new FileSaveService(null,
                creasePatternCamera,
                mainCreasePatternWorker,
                fileModel,
                applicationModel,
                canvasModel,
                () -> {

                },
                null);
        File saveFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("save0011.ori")).toURI());

        try {
            fileSaveService.openFile(saveFile);

            List<Circle> list = (List<Circle>) mainCreasePatternWorker.foldLineSet.getCircles();

            Assertions.assertEquals(1, list.size(), "Excpected one circle");

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

            Assertions.assertEquals(8, mainCreasePatternWorker.foldLineSet.getTotal());

            LineSegment mountainLineSegment = mainCreasePatternWorker.foldLineSet.get(5);
            Assertions.assertEquals(LineColor.RED_1, mountainLineSegment.getColor());

            LineSegment valleyLineSegment = mainCreasePatternWorker.foldLineSet.get(6);
            Assertions.assertEquals(LineColor.BLUE_2, valleyLineSegment.getColor());

            LineSegment edgeLineSegment = mainCreasePatternWorker.foldLineSet.get(7);
            Assertions.assertEquals(LineColor.BLACK_0, edgeLineSegment.getColor());

            LineSegment auxLineSegment = mainCreasePatternWorker.foldLineSet.get(8);
            Assertions.assertEquals(LineColor.CYAN_3, auxLineSegment.getColor());
        } catch (FileReadingException e) {
            Assertions.fail(e);
        }
    }
}
