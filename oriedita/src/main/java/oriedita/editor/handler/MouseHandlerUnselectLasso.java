package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import origami.crease_pattern.LassoInteractionMode;
import origami.crease_pattern.worker.SelectMode;

@ApplicationScoped
@Handles(MouseMode.UNSELECT_LASSO_75)
public class MouseHandlerUnselectLasso extends BaseMouseHandlerLasso{
    @Inject @Named("mainCreasePattern_Worker")
    CreasePattern_Worker mainCreasePatternWorker;

    @Inject
    public MouseHandlerUnselectLasso(){}

    @Override
    protected void performAction() {
        int beforeSelectNum = mainCreasePatternWorker.getFoldLineTotalForSelectFolding();
        d.getFoldLineSet().select_lasso(d.getLinePath(), SelectMode.UNSELECT, LassoInteractionMode.INTERSECT_CONTAIN);
        d.refreshIsSelectionEmpty();
        int afterSelectNum = mainCreasePatternWorker.getFoldLineTotalForSelectFolding();
        if(beforeSelectNum != afterSelectNum) d.record();
    }
}