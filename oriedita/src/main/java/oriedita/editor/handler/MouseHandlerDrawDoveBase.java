package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;

@ApplicationScoped
@Handles(MouseMode.DRAW_DOVE_BASE)
public class MouseHandlerDrawDoveBase extends MouseHandlerDrawPattern {

    @Inject
    public MouseHandlerDrawDoveBase() {
        super("default-molecules/dove_base.fold");
    }
}
