package oriedita.editor.action.drawing;

import oriedita.editor.action.selector.drawing.Drawer;
import oriedita.editor.action.selector.drawing.DrawingSettings;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.crease_pattern.element.Circle;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Singleton
public class CircleDrawer implements Drawer<Circle> {

    @Inject
    public CircleDrawer() {
    }

    @Override
    public void draw(Circle element, Graphics2D g, Camera camera, DrawingSettings settings) {
        DrawingUtil.drawCircleStep(g, element, camera);
    }
}
