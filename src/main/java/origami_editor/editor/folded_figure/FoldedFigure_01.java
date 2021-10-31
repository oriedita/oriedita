package origami_editor.editor.folded_figure;

import origami.folding.FoldedFigure;
import origami.folding.util.IBulletinBoard;
import origami_editor.editor.databinding.FoldedFigureModel;

public class FoldedFigure_01 extends FoldedFigure {//FoldedFigure-based folding prediction algorithm

    public FoldedFigure_01(IBulletinBoard bb) {
        super(bb);
    }

    public void setData(FoldedFigureModel foldedFigureModel) {
        ip4 = foldedFigureModel.getState();
        findAnotherOverlapValid = foldedFigureModel.isFindAnotherOverlapValid();
    }

    public void getData(FoldedFigureModel foldedFigureModel) {
        foldedFigureModel.setState(ip4);
        foldedFigureModel.setFindAnotherOverlapValid(findAnotherOverlapValid);
    }
}
