package origami.folding;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import origami.crease_pattern.FoldingException;
import origami.crease_pattern.LineSegmentSet;
import origami_editor.editor.save.Save;
import origami_editor.editor.component.BulletinBoard;
import origami_editor.editor.export.Cp;

import java.io.IOException;

public class FoldingSolutionsTest {
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
            e.printStackTrace();
            Assertions.fail();
        }
    }
}
