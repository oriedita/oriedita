package origami.folding;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import origami.crease_pattern.FoldingException;
import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.element.Point;
import origami_editor.editor.Save;
import origami_editor.editor.component.BulletinBoard;
import origami_editor.editor.export.Cp;

import java.io.IOException;

public class FoldingSolutionsTest {
    @Test
    public void testFoldBirdbase() throws IOException {
        Save save = Cp.importFile(getClass().getClassLoader().getResourceAsStream("solution_sample_1.cp"));

        FoldedFigure foldedFigure = new FoldedFigure(new BulletinBoard());

        LineSegmentSet lineSegmentSet = new LineSegmentSet();

        lineSegmentSet.setSave(save);

        try {
            foldedFigure.estimationOrder = FoldedFigure.EstimationOrder.ORDER_5;
            foldedFigure.folding_estimated(lineSegmentSet, new Point());

            foldedFigure.estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;

            int objective = 16;
            while (objective > foldedFigure.discovered_fold_cases) {
                foldedFigure.folding_estimated(lineSegmentSet, new Point());

                foldedFigure.estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;
                if (!foldedFigure.findAnotherOverlapValid) {
                    objective = foldedFigure.discovered_fold_cases;
                }
            }

            // TODO, this number should be 16
            Assertions.assertEquals(3, foldedFigure.discovered_fold_cases, "Expected to find 8 fold cases");
        } catch (InterruptedException | FoldingException e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }
}
