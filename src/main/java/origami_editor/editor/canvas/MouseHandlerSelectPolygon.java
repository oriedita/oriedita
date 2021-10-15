package origami_editor.editor.canvas;

import origami_editor.editor.MouseMode;

public class MouseHandlerSelectPolygon extends BaseMouseHandlerPolygon {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.SELECT_POLYGON_66;
    }
}
