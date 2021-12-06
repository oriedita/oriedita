package oriedita.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;
import oriedita.editor.canvas.MouseMode;

@Singleton
public class MouseHandlerUnselectPolygon extends BaseMouseHandlerPolygon {
    @Inject
    public MouseHandlerUnselectPolygon() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.UNSELECT_POLYGON_67;
    }
}
