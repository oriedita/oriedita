package origami_editor.editor.drawing_worker;

import origami_editor.editor.MouseMode;

public class MouseHandlerUnselectPolygon extends BaseMouseHandlerPolygon {
    public MouseHandlerUnselectPolygon(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.UNSELECT_POLYGON_67;
    }
}
