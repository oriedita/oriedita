package origami_editor.editor.canvas;

import origami_editor.editor.MouseMode;

public class MouseHandlerUnselectPolygon extends BaseMouseHandlerPolygon {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.UNSELECT_POLYGON_67;
    }
}
