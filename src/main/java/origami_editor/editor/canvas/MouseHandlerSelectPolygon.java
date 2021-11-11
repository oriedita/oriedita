package origami_editor.editor.canvas;

import org.springframework.stereotype.Component;
import origami_editor.editor.MouseMode;

@Component
public class MouseHandlerSelectPolygon extends BaseMouseHandlerPolygon {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.SELECT_POLYGON_66;
    }
}
