package oriedita.editor.action.selector.drawing;

import oriedita.editor.drawing.tools.Camera;

import java.awt.*;

/**
 * Can draw Elements of the type T to a Graphics2D object
 * @param <T> Type of the element that can be drawn
 */
@FunctionalInterface
public interface Drawer<T>  {
    /**
     * Draws an element to the graphics object
     */
    void draw(T element, Graphics2D g,  Camera camera, DrawingSettings settings);
}
