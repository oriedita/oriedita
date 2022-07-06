package oriedita.editor;

import oriedita.editor.drawing.tools.Camera;
import origami.crease_pattern.FoldingException;
import origami.crease_pattern.LineSegmentSet;
import origami.folding.FoldedFigure;

public interface Foldable {
    void setEstimationOrder(FoldedFigure.EstimationOrder estimationOrder);
    void folding_estimated(Camera creasePatternCamera, LineSegmentSet lineSegmentSet) throws InterruptedException, FoldingException;
    void estimated_initialize();
    String getTextResult();
    void setTextResult(String testResutl);
    void createTwoColorCreasePattern(Camera creasePatternCamera, LineSegmentSet lineSegmentsForFolding) throws InterruptedException;
}
