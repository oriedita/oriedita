package origami.folding;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import origami.crease_pattern.FoldingException;
import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.element.Point;
import origami_editor.editor.Save;
import origami_editor.editor.component.BulletinBoard;
import origami_editor.editor.export.Cp;

import java.io.IOException;

@ExtendWith({SnapshotExtension.class})
public class FoldingTest {
    private Expect expect;

    @Test
    public void testFoldBirdbase() throws IOException {
        Save save = Cp.importFile(getClass().getClassLoader().getResourceAsStream("birdbase.cp"));

        FoldedFigure foldedFigure = new FoldedFigure(new BulletinBoard());

        LineSegmentSet lineSegmentSet = new LineSegmentSet();

        lineSegmentSet.setSave(save);

        try {
            foldedFigure.estimationOrder = FoldedFigure.EstimationOrder.ORDER_5;
            foldedFigure.folding_estimated(lineSegmentSet, new Point());

            expect.serializer("json").toMatchSnapshot(foldedFigure.cp_worker3.get());
        } catch (InterruptedException | FoldingException e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }
}
