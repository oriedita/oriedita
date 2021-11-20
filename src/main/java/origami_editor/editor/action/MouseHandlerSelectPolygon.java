package origami_editor.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;
import origami_editor.editor.MouseMode;

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
