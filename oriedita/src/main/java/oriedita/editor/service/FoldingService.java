package oriedita.editor.service;

import oriedita.editor.drawing.FoldedFigure_Drawer;
import origami.crease_pattern.FoldingException;
import origami.crease_pattern.LineSegmentSet;
import origami.folding.FoldedFigure;

public interface FoldingService {
    void folding_estimated(FoldedFigure_Drawer selectedFigure) throws InterruptedException, FoldingException;

    void fold(FoldedFigure.EstimationOrder estimationOrder);

    FoldType getFoldType();

    FoldedFigure_Drawer initFoldedFigure();

    void createTwoColoredCp();

    void foldAnother(FoldedFigure_Drawer selectedItem);

    LineSegmentSet getLineSegmentsForFolding();

    enum FoldType {
        FOR_ALL_CONNECTED_LINES_1,
        FOR_SELECTED_LINES_2
    }
}
