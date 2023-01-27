package origami.folding;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.tinylog.Logger;
import oriedita.editor.export.Cp;
import oriedita.editor.save.Save;
import origami.crease_pattern.LineSegmentSet;
import origami.folding.util.TestBulletinBoard;

import java.io.IOException;

@ExtendWith({SnapshotExtension.class})
public class TwoColoredCpTest {

    private Expect expect;

    @Test
    public void testCreateTwoColoredCp() throws IOException {
        Save save = Cp.importFile(getClass().getClassLoader().getResourceAsStream("birdbase.cp"));

        FoldedFigure foldedFigure = new FoldedFigure(new TestBulletinBoard());

        LineSegmentSet lineSegmentSet = new LineSegmentSet();

        lineSegmentSet.setSave(save);

        try {
            foldedFigure.createTwoColorCreasePattern(lineSegmentSet, 1);

            expect.serializer("json").toMatchSnapshot(foldedFigure.wireFrame_worker3.get());
        } catch (InterruptedException e) {
            Logger.error(e, "test got interrupted");
            Assertions.fail();
        }
    }
}
