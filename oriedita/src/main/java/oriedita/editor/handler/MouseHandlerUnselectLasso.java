package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.tinylog.Logger;
import oriedita.editor.canvas.MouseMode;

@ApplicationScoped
@Handles(MouseMode.UNSELECT_LASSO_75)
public class MouseHandlerUnselectLasso extends BaseMouseHandlerLasso{
    @Inject
    public MouseHandlerUnselectLasso(){}

    @Override
    protected void performAction() {
        Logger.debug("unselected");
        d.getFoldLineSet().select_lasso(d.getLinePath(), "unselect");
    }
}
