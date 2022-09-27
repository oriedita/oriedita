package oriedita.editor.action;

import oriedita.editor.canvas.MouseMode;

import javax.inject.Inject;
import javax.inject.Singleton;

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
