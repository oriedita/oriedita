package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;

@ApplicationScoped
@Handles(MouseMode.SELECT_POLYGON_66)
public class MouseHandlerSelectPolygon extends BaseMouseHandlerPolygon {
    @Inject
    public MouseHandlerSelectPolygon() {
    }

}
