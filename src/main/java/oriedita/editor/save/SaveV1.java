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
public class SaveV1 implements Save{
    private List<LineSegment> lineSegments;
    private List<Circle> circles;
    private String title;
    private List<Point> points;
    private List<LineSegment> auxLineSegments;
    private Camera creasePatternCamera;
    private CanvasModel canvasModel;
    private GridModel gridModel;
    private FoldedFigureModel foldedFigureModel;
    private ApplicationModel applicationModel;

    public ApplicationModel getApplicationModel() {
        return applicationModel;
    }

    public void setApplicationModel(ApplicationModel applicationModel) {
        this.applicationModel = applicationModel;
    }

    public SaveV1() {
        lineSegments = new ArrayList<>();
        circles = new ArrayList<>();
        points = new ArrayList<>();
        auxLineSegments = new ArrayList<>();
    }

    public void addPoint(Point p) {
        points.add(p);
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public Camera getCreasePatternCamera() {
        return creasePatternCamera;
    }

    public void setCreasePatternCamera(Camera creasePatternCamera) {
        this.creasePatternCamera = creasePatternCamera;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addLineSegment(LineSegment lineSegment) {
        this.lineSegments.add(lineSegment);
    }

    public void addCircle(Circle circle) {
        this.circles.add(circle);
    }

    public List<LineSegment> getLineSegments() {
        return lineSegments;
    }

    public void setLineSegments(List<LineSegment> lineSegments) {
        this.lineSegments = lineSegments;
    }

    public List<Circle> getCircles() {
        return circles;
    }

    public void setCircles(List<Circle> circles) {
        this.circles = circles;
    }

    public void set(Save save) {
        for (LineSegment s : save.getLineSegments()) {
            addLineSegment(s);
        }
        for (Circle c : save.getCircles()) {
            addCircle(c);
        }
        for (LineSegment s : save.getAuxLineSegments()) {
            addAuxLineSegment(s);
        }

        setTitle(save.getTitle());
    }

    public CanvasModel getCanvasModel() {
        return canvasModel;
    }

    public void setCanvasModel(CanvasModel canvasModel) {
        this.canvasModel = canvasModel;
    }

    public GridModel getGridModel() {
        return gridModel;
    }

    public void setGridModel(GridModel gridModel) {
        this.gridModel = gridModel;
    }

    public FoldedFigureModel getFoldedFigureModel() {
        return foldedFigureModel;
    }

    public void setFoldedFigureModel(FoldedFigureModel foldedFigureModel) {
        this.foldedFigureModel = foldedFigureModel;
    }

    public List<LineSegment> getAuxLineSegments() {
        return auxLineSegments;
    }

    public void setAuxLineSegments(List<LineSegment> auxLineSegments) {
        this.auxLineSegments = auxLineSegments;
    }

    public void addAuxLineSegment(LineSegment lineSegment) {
        this.auxLineSegments.add(lineSegment);
    }

    public void add(Save save) {
        for (LineSegment s : save.getLineSegments()) {
            addLineSegment(s);
        }
        for (Circle c : save.getCircles()) {
            addCircle(c);
        }
        for (LineSegment s : save.getAuxLineSegments()) {
            addAuxLineSegment(s);
        }
    }

    /**
     * Returns if this save contains lines which are not savable to a .cp file without losing information.
     */
    public boolean canSaveAsCp() {
        return circles.isEmpty() && auxLineSegments.isEmpty();
    }
}
