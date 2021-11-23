package oriedita.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;
import oriedita.editor.canvas.MouseMode;

@Singleton
public class MouseHandlerLengthenCreaseSameColor extends MouseHandlerLengthenCrease {
    @Inject
    public MouseHandlerLengthenCreaseSameColor() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.LENGTHEN_CREASE_SAME_COLOR_70;
    }
}
