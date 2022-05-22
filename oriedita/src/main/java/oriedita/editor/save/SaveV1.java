package oriedita.editor.save;

import com.fasterxml.jackson.annotation.JsonTypeName;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.GridModel;
import oriedita.editor.drawing.tools.Camera;

import java.util.ArrayList;
import java.util.List;

@JsonTypeName("v1")
public class SaveV1 extends BaseSave{
}