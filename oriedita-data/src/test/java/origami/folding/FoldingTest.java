package origami.folding;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.tinylog.Logger;
import oriedita.editor.export.CpImporter;
import oriedita.editor.save.Save;
import origami.crease_pattern.FoldingException;
import origami.crease_pattern.LineSegmentSet;
import origami.folding.util.TestBulletinBoard;

import java.io.IOException;

@ExtendWith({SnapshotExtension.class})
public class FoldingTest {
    private Expect expect;

    @Test
    public void testFoldBirdbase() throws IOException {
        Save save = new CpImporter().doImport(getClass().getClassLoader().getResourceAsStream("birdbase.cp"));

        FoldedFigure foldedFigure = new FoldedFigure(new TestBulletinBoard());

        LineSegmentSet lineSegmentSet = new LineSegmentSet();

        lineSegmentSet.setSave(save);

        try {
            foldedFigure.estimationOrder = FoldedFigure.EstimationOrder.ORDER_5;
            foldedFigure.folding_estimated(lineSegmentSet, 1);

            expect.serializer("json").toMatchSnapshot(foldedFigure.wireFrameWorker_foldedSubdivided.get());
        } catch (InterruptedException | FoldingException e) {
            Logger.error(e, "test got interrupted");
            Assertions.fail();
        }
    }
}
