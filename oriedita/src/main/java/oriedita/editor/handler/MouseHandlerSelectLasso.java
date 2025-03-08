package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;

@ApplicationScoped
@Handles(MouseMode.SELECT_LASSO_74)
public class MouseHandlerSelectLasso extends BaseMouseHandlerLasso{
    @Inject @Named("mainCreasePattern_Worker")
    CreasePattern_Worker mainCreasePatternWorker;

    @Inject
    public MouseHandlerSelectLasso(){}

    @Override
    protected void performAction() {
        int beforeSelectNum = mainCreasePatternWorker.getFoldLineTotalForSelectFolding();
        d.getFoldLineSet().select_lasso(d.getLinePath(), "select");
        int afterSelectNum = mainCreasePatternWorker.getFoldLineTotalForSelectFolding();
        if(beforeSelectNum != afterSelectNum) d.record();
    }
}
