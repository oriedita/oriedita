package origami_editor.editor;

import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.databinding.FoldedFigureModel;
import origami_editor.editor.databinding.GridModel;
import origami_editor.tools.Camera;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Save implements Serializable {
    private List<LineSegment> lineSegments;
    private List<Circle> circles;
    private String title;
    private List<Point> points;
    private List<LineSegment> auxLineSegments;
    private Camera creasePatternCamera;
    private CanvasModel canvasModel;
    private GridModel gridModel;
    private FoldedFigureModel foldedFigureModel;

    public Save() {
        lineSegments = new ArrayList<>();
        circles = new ArrayList<>();
        points = new ArrayList<>();
        auxLineSegments = new ArrayList<>();
        canvasModel = new CanvasModel();
        gridModel = new GridModel();
        foldedFigureModel = new FoldedFigureModel();
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

    private byte[] convertToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        }
    }

    private Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }
}
