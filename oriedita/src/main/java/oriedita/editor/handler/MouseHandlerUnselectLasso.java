package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import origami.crease_pattern.SelectLassoMode;

@ApplicationScoped
@Handles(MouseMode.UNSELECT_LASSO_75)
public class MouseHandlerUnselectLasso extends BaseMouseHandlerLasso{
    @Inject @Named("mainCreasePattern_Worker")
    CreasePattern_Worker mainCreasePatternWorker;
    @Inject
    CanvasModel canvasModel;

    @Inject
    public MouseHandlerUnselectLasso(){}

    @Override
    protected void performAction() {
        SelectLassoMode mode = !canvasModel.getToggleLineColor() ? SelectLassoMode.INTERSECT : SelectLassoMode.CONTAIN;
        int beforeSelectNum = mainCreasePatternWorker.getFoldLineTotalForSelectFolding();
        d.getFoldLineSet().select_lasso(d.getLinePath(), "unselect", mode);
        int afterSelectNum = mainCreasePatternWorker.getFoldLineTotalForSelectFolding();
        if(beforeSelectNum != afterSelectNum) d.record();
    }
}
