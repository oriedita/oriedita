package origami_editor.editor.drawing_worker;

import origami_editor.editor.MouseMode;

public class MouseHandlerUnselectPolygon extends BaseMouseHandlerPolygon {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.UNSELECT_POLYGON_67;
    }
}
