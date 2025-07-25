package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;

@ApplicationScoped
@Handles(MouseMode.DRAW_FROG_BASE)
public class MouseHandlerDrawFrogBase extends MouseHandlerDrawPattern {

    @Inject
    public MouseHandlerDrawFrogBase() {
        super("default-molecules/frog_base.fold");
    }
}
