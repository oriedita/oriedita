package oriedita.editor;

import oriedita.editor.drawing.tools.Camera;
import origami.crease_pattern.FoldingException;
import origami.crease_pattern.LineSegmentSet;
import origami.folding.FoldedFigure;

public interface Foldable {
    void folding_estimated(Camera creasePatternCamera, LineSegmentSet lineSegmentSet) throws InterruptedException, FoldingException;

    void setEstimationOrder(FoldedFigure.EstimationOrder estimationOrder);

    void estimated_initialize();

    String getTextResult();

    void setTextResult(String textResult);

    void createTwoColorCreasePattern(Camera camera_of_foldLine_diagram, LineSegmentSet Ss0) throws InterruptedException; //Two-color crease pattern

}
