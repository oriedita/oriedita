package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import origami.crease_pattern.LassoInteractionMode;
import origami.crease_pattern.worker.SelectMode;

@ApplicationScoped
@Handles(MouseMode.SELECT_LASSO_74)
public class MouseHandlerSelectLasso extends BaseMouseHandlerLasso{
    @Inject @Named("mainCreasePattern_Worker")
    CreasePattern_Worker mainCreasePatternWorker;
    @Inject
    CanvasModel canvasModel;

    @Inject
    public MouseHandlerSelectLasso(){}

    @Override
    protected void performAction() {
        SelectMode selectMode = !canvasModel.getToggleLineColor() ? SelectMode.SELECT : SelectMode.UNSELECT;
        int beforeSelectNum = mainCreasePatternWorker.getFoldLineTotalForSelectFolding();
        d.getFoldLineSet().select_lasso(d.getLinePath(), selectMode, LassoInteractionMode.INTERSECT);
        int afterSelectNum = mainCreasePatternWorker.getFoldLineTotalForSelectFolding();
        if(beforeSelectNum != afterSelectNum) d.record();
    }
}
