package origami.folding;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import origami_editor.editor.Save;
import origami_editor.editor.export.Cp;
import origami_editor.editor.folded_figure.FoldedFigure;

import java.io.File;
import java.io.IOException;

@ExtendWith({SnapshotExtension.class})
public class FoldingTest {
    private Expect expect;

    @Test
    public void testSimpleCreasePattern() throws IOException {
        Save save = Cp.importFile(new File("crane.cp"));

    }
}
