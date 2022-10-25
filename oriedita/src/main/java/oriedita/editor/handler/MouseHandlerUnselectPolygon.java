package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;

@ApplicationScoped
@Handles(MouseMode.UNSELECT_POLYGON_67)
public class MouseHandlerUnselectPolygon extends BaseMouseHandlerPolygon {
    @Inject
    public MouseHandlerUnselectPolygon() {
    }

}
