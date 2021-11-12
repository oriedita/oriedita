package origami_editor.editor.canvas;

import javax.inject.Singleton;
import origami_editor.editor.MouseMode;

@Singleton
public class MouseHandlerSelectPolygon extends BaseMouseHandlerPolygon {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.SELECT_POLYGON_66;
    }
}
