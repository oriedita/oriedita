package origami_editor.editor.canvas;

import org.springframework.stereotype.Component;
import origami_editor.editor.MouseMode;

@Component
public class MouseHandlerLengthenCreaseSameColor extends MouseHandlerLengthenCrease {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.LENGTHEN_CREASE_SAME_COLOR_70;
    }
}
