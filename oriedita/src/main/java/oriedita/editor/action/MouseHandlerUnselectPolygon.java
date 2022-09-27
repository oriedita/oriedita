package oriedita.editor.action;

import oriedita.editor.canvas.MouseMode;

import javax.inject.Inject;
import javax.inject.Singleton;

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
