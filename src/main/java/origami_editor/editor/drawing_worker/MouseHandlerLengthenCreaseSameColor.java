package origami_editor.editor.drawing_worker;

import origami_editor.editor.MouseMode;

public class MouseHandlerLengthenCreaseSameColor extends MouseHandlerLengthenCrease {
    public MouseHandlerLengthenCreaseSameColor(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.LENGTHEN_CREASE_SAME_COLOR_70;
    }
}
