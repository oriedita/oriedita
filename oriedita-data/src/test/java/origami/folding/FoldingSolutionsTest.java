package origami.folding;

import fold.io.CreasePatternReader;
import fold.model.Edge;
import fold.model.FoldFile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tinylog.Logger;
import oriedita.editor.export.CpImporter;
import oriedita.editor.export.FoldImporter;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveProvider;
import origami.crease_pattern.FoldingException;
import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.folding.util.TestBulletinBoard;

import java.io.IOException;
import java.io.InputStream;

public class FoldingSolutionsTest {
    @Inject CpImporter importer;
    @Test
    public void testFoldSolutionNumber() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("solution_sample_1.cp");
        Save save1 = SaveProvider.createInstance();

        CreasePatternReader creasePatternReader = new CreasePatternReader(is);

        FoldFile foldFile = creasePatternReader.read();

        for (Edge edge : foldFile.getRootFrame().getEdges()) {
            save1.addLineSegment(new LineSegment(new Point(edge.getStart().getX(), edge.getStart().getY()), new Point(edge.getEnd().getX(), edge.getEnd().getY()), FoldImporter.getColor(edge.getAssignment())));
        }

        Save save = save1;

        FoldedFigure foldedFigure = new FoldedFigure(new TestBulletinBoard());

        LineSegmentSet lineSegmentSet = new LineSegmentSet();

        lineSegmentSet.setSave(save);

        try {
            foldedFigure.estimationOrder = FoldedFigure.EstimationOrder.ORDER_5;
            foldedFigure.folding_estimated(lineSegmentSet, 1);

            foldedFigure.estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;

            int objective = 17;
            while (objective > foldedFigure.discovered_fold_cases) {
                foldedFigure.folding_estimated(lineSegmentSet, 1);

                foldedFigure.estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;
                if (!foldedFigure.findAnotherOverlapValid) {
                    objective = foldedFigure.discovered_fold_cases;
                }
            }

            Assertions.assertEquals(16, foldedFigure.discovered_fold_cases, "Expected to find 16 solutions");
        } catch (InterruptedException | FoldingException e) {
            Logger.error(e, "test got interrupted");
            Assertions.fail();
        }
    }
}
