package origami.folding;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import origami.crease_pattern.FoldingException;
import origami.crease_pattern.LineSegmentSet;
import oriedita.editor.save.Save;
import oriedita.editor.swing.component.BulletinBoard;
import oriedita.editor.export.Cp;

import java.io.IOException;

public class FoldingSolutionsTest {
    private static final Logger logger = LogManager.getLogger(FoldingSolutionsTest.class);
    @Test
    public void testFoldSolutionNumber() throws IOException {
        Save save = Cp.importFile(getClass().getClassLoader().getResourceAsStream("solution_sample_1.cp"));

        FoldedFigure foldedFigure = new FoldedFigure(new BulletinBoard());

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
            logger.fatal("test got interrupted", e);
            Assertions.fail();
        }
    }
}
