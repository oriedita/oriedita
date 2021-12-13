package oriedita.editor.action;

import oriedita.editor.action.selector.drawing.DrawingSettings;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import origami.crease_pattern.element.Point;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public interface MouseModeHandler {
    enum Feature {
        BUTTON_1,
        BUTTON_3;

        public static EnumSet<Feature> getFeatures(MouseEvent e, int mouseButton) {
            List<Feature> features = new ArrayList<>();
            if (mouseButton == MouseEvent.BUTTON1) {
                features.add(BUTTON_1);
            }
            if (e.getButton() == MouseEvent.BUTTON1) {
                features.add(BUTTON_1);
            }
            if (mouseButton == MouseEvent.BUTTON3) {
                features.add(BUTTON_3);
            }
            if (e.getButton() == MouseEvent.BUTTON3) {
                features.add(BUTTON_3);
            }
            if (features.isEmpty()) {
                return EnumSet.noneOf(Feature.class);
            }
            return EnumSet.copyOf(features);
        }
    }
    default boolean accepts(MouseEvent e, int mouseButton) {
        EnumSet<Feature> features = Feature.getFeatures(e, mouseButton);
        for (Feature feature : features) {
            if (getSubscribedFeatures().contains(feature)) return true;
        }
        return false;
    }

    MouseMode getMouseMode();

    EnumSet<Feature> getSubscribedFeatures();

    default void mouseMoved(Point p0, MouseEvent e) {
        mouseMoved(p0);
    }

    void mouseMoved(Point p0);

    default void mousePressed(Point p0, MouseEvent e) {
        mousePressed(p0);
    }

    void mousePressed(Point p0);

    default void mouseDragged(Point p0, MouseEvent e) {
        mouseDragged(p0);
    }

    void mouseDragged(Point p0);

    default void mouseReleased(Point p0, MouseEvent e) {
        mouseReleased(p0);
    }

    void mouseReleased(Point p0);

    default void reset() {}

    default void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {}
}
