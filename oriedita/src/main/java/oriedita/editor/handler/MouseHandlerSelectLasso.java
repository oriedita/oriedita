package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.tinylog.Logger;
import oriedita.editor.canvas.MouseMode;

@ApplicationScoped
@Handles(MouseMode.SELECT_LASSO_74)
public class MouseHandlerSelectLasso extends BaseMouseHandlerLasso{
    @Inject
    public MouseHandlerSelectLasso(){}

    @Override
    protected void performAction() {
        Logger.debug("selected");
        d.getFoldLineSet().select_lasso(d.getLinePath(), "select");
    }
}
