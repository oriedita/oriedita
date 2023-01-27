package oriedita.editor.folded_figure;

import oriedita.editor.databinding.FoldedFigureModel;
import origami.folding.FoldedFigure;
import origami.folding.util.IBulletinBoard;

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
