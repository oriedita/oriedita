package oriedita.editor.action;

import oriedita.editor.action.selector.CreasePatternPointSelector;
import oriedita.editor.canvas.MouseMode;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MouseHandlerCircleDrawFree extends MouseHandlerCircleDraw {
    @Inject
    public MouseHandlerCircleDrawFree() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CIRCLE_DRAW_FREE_47;
    }

    @Override
    protected CreasePatternPointSelector.SelectionMode getSelectionMode() {
        return CreasePatternPointSelector.SelectionMode.CLOSEST_POINT_OR_FREE;
    }
}
