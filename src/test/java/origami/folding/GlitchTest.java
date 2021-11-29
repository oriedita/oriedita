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

public class GlitchTest {
    private static final Logger logger = LogManager.getLogger(GlitchTest.class);
    @Test
    public void testFoldGlitch() throws IOException {
        Save save = Cp.importFile(getClass().getClassLoader().getResourceAsStream("glitch.cp"));

        FoldedFigure foldedFigure = new FoldedFigure(new BulletinBoard());

        LineSegmentSet lineSegmentSet = new LineSegmentSet();

        lineSegmentSet.setSave(save);

        try {
            foldedFigure.estimationOrder = FoldedFigure.EstimationOrder.ORDER_3;
            foldedFigure.folding_estimated(lineSegmentSet, 1);

            Assertions.assertEquals(119, foldedFigure.ct_worker.SubFaceTotal, "Expected 119 SubFaces");
        } catch (InterruptedException | FoldingException e) {
            logger.fatal("test got interrupted", e);
            Assertions.fail();
        }
    }
}
