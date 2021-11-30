package oriedita.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;
import oriedita.editor.canvas.MouseMode;

@Singleton
public class MouseHandlerSelectPolygon extends BaseMouseHandlerPolygon {
    @Inject
    public MouseHandlerSelectPolygon() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.SELECT_POLYGON_66;
    }
}
