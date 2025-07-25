package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;

@ApplicationScoped
@Handles(MouseMode.DRAW_BLINTZ)
public class MouseHandlerDrawBlintz extends MouseHandlerDrawPattern {

    @Inject
    public MouseHandlerDrawBlintz() {
        super("default-molecules/blintz.fold");
    }
}
