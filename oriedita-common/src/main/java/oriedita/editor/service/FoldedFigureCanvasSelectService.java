package oriedita.editor.service;

import oriedita.editor.canvas.MouseWheelTarget;
import origami.crease_pattern.element.Point;

public interface FoldedFigureCanvasSelectService {
    MouseWheelTarget pointInCreasePatternOrFoldedFigure(Point p);
}
