package origami_editor.editor;

import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.databinding.ApplicationModel;
import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.databinding.FoldedFigureModel;
import origami_editor.editor.databinding.GridModel;
import origami_editor.tools.Camera;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Save implements Serializable {
    private String version;
    private List<LineSegment> lineSegments;
    private List<Circle> circles;
    private String title;
    private Map<Integer, Point> points;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Save() {
        lineSegments = new ArrayList<>();
        circles = new ArrayList<>();
        points = new HashMap<>();
        auxLineSegments = new ArrayList<>();
        canvasModel = new CanvasModel();
        gridModel = new GridModel();
        foldedFigureModel = new FoldedFigureModel();
    }

    public void addPoint(int index, Point p) {
        points.put(index, p);
    }

    public Map<Integer, Point> getPoints() {
        return points;
    }

    public void setPoints(Map<Integer, Point> points) {
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
     * @return
     */
    public boolean canSaveAsCp() {
        return circles.isEmpty() && auxLineSegments.isEmpty();
    }
}
