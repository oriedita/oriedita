package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;

@ApplicationScoped
@Handles(MouseMode.DRAW_BIRD_BASE)
public class MouseHandlerDrawBirdBase extends MouseHandlerDrawPattern {
    @Inject
    public MouseHandlerDrawBirdBase() {
        super("default-molecules/bird_base.fold");
    }
}
