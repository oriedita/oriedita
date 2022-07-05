package oriedita.editor.service;

import oriedita.editor.Canvas;
import origami.crease_pattern.element.Point;

public interface FoldedFigureCanvasSelectService {
    Canvas.MouseWheelTarget pointInCreasePatternOrFoldedFigure(Point p);
}
