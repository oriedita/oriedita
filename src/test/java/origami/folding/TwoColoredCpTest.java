package origami.folding;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import origami.crease_pattern.LineSegmentSet;
import origami_editor.editor.save.Save;
import origami_editor.editor.swing.component.BulletinBoard;
import origami_editor.editor.export.Cp;
import java.io.IOException;

@ExtendWith({SnapshotExtension.class})
public class TwoColoredCpTest {

    private Expect expect;

    @Test
    public void testCreateTwoColoredCp() throws IOException {
        Save save = Cp.importFile(getClass().getClassLoader().getResourceAsStream("birdbase.cp"));

        FoldedFigure foldedFigure = new FoldedFigure(new BulletinBoard());

        LineSegmentSet lineSegmentSet = new LineSegmentSet();

        lineSegmentSet.setSave(save);

        try {
            foldedFigure.createTwoColorCreasePattern(lineSegmentSet, 1);

            expect.serializer("json").toMatchSnapshot(foldedFigure.cp_worker3.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }
}
