package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;

@ApplicationScoped
@Handles(MouseMode.DRAW_FISH_BASE)
public class MouseHandlerDrawFishBase extends MouseHandlerDrawPattern {

    @Inject
    public MouseHandlerDrawFishBase() {
        super("default-molecules/fish_base.fold");
    }
}
