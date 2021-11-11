package origami_editor.editor.canvas;

import org.springframework.stereotype.Component;
import origami_editor.editor.MouseMode;

@Component
public class MouseHandlerUnselectPolygon extends BaseMouseHandlerPolygon {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.UNSELECT_POLYGON_67;
    }
}
