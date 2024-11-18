package oriedita.editor.export;

import fold.io.CustomFoldWriter;
import fold.model.Edge;
import fold.model.Face;
import fold.model.FoldEdgeAssignment;
import fold.model.FoldFile;
import fold.model.FoldFrame;
import fold.model.Vertex;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.export.api.FileExporter;
import oriedita.editor.save.OrieditaFoldFile;
import oriedita.editor.save.Save;
import oriedita.editor.tools.ResourceUtil;
import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.worker.WireFrame_Worker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FoldExporter implements FileExporter {
    @Inject
    public FoldExporter() {
    }

    private FoldEdgeAssignment getAssignment(LineColor lineColor) {
        return switch (lineColor) {
            default -> FoldEdgeAssignment.UNASSIGNED;
            case BLACK_0 -> FoldEdgeAssignment.BORDER;
            case RED_1 -> FoldEdgeAssignment.MOUNTAIN_FOLD;
            case BLUE_2 -> FoldEdgeAssignment.VALLEY_FOLD;
            case CYAN_3, ORANGE_4, MAGENTA_5, GREEN_6, YELLOW_7, PURPLE_8, OTHER_9 -> FoldEdgeAssignment.FLAT_FOLD;
        };
    }

    private void exportFile(Save save, LineSegmentSet lineSegmentSet, File file) throws InterruptedException, IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            CustomFoldWriter<FoldFile> foldFileCustomFoldWriter = new CustomFoldWriter<>(fileOutputStream);
            foldFileCustomFoldWriter.write(toFoldSave(save, lineSegmentSet));
        }
    }

    public OrieditaFoldFile toFoldSave(Save save) throws InterruptedException {
        LineSegmentSet s = new LineSegmentSet();
        s.setSave(save);
        return toFoldSave(save, s);
    }

    public OrieditaFoldFile toFoldSave(Save save, LineSegmentSet lineSegmentSet) throws InterruptedException {
        WireFrame_Worker wireFrame_worker = new WireFrame_Worker(3.0);
        wireFrame_worker.setLineSegmentSetWithoutFaceOccurence(lineSegmentSet);
        PointSet pointSet = wireFrame_worker.get();
        boolean includeFaces = pointSet.calculateFaces();

        OrieditaFoldFile foldFile = new OrieditaFoldFile();
        foldFile.setCreator("oriedita");
        FoldFrame rootFrame = foldFile.getRootFrame();

        for (int i = 1; i <= pointSet.getNumPoints(); i++) {
            Vertex vertex = new Vertex();
            vertex.setX(pointSet.getPoint(i).getX());
            vertex.setY(pointSet.getPoint(i).getY());
            rootFrame.getVertices().add(vertex);
        }

        for (int i = 1; i <= pointSet.getNumLines(); i++) {
            Edge edge = new Edge();
            edge.setAssignment(getAssignment(pointSet.getColor(i)));
            edge.setFoldAngle(getFoldAngle(pointSet.getColor(i)));
            Vertex startVertex = rootFrame.getVertices().get(pointSet.getBegin(i) - 1);
            Vertex endVertex = rootFrame.getVertices().get(pointSet.getEnd(i) - 1);

            edge.setStart(startVertex);
            edge.setEnd(endVertex);

            rootFrame.getEdges().add(edge);
        }

        if (includeFaces) {
            for (int i = 1; i <= pointSet.getNumFaces(); i++) {
                var pface = pointSet.getFace(i);
                var face = new Face();

                var faceVertices = new ArrayList<Vertex>();
                var faceEdges = new ArrayList<Edge>();
                var vertexFirst = rootFrame.getVertices().get(pface.getPointId(1) - 1);
                var vertexLast = rootFrame.getVertices().get(pface.getPointId(pface.getNumPoints()) - 1);
                faceVertices.add(vertexFirst);
                faceEdges.add(findEdge(vertexFirst, vertexLast, rootFrame.getEdges()));
                for (var j = 2; j <= pface.getNumPoints(); j++) {
                    var currentVertex = rootFrame.getVertices().get(pface.getPointId(j) - 1);
                    var previousVertex = rootFrame.getVertices().get(pface.getPointId(j - 1) - 1);
                    faceVertices.add(currentVertex);
                    faceEdges.add(findEdge(currentVertex, previousVertex, rootFrame.getEdges()));
                }
                face.setVertices(faceVertices);
                face.setEdges(faceEdges);

                rootFrame.getFaces().add(face);
            }
        }

        foldFile.setCircles(save.getCircles());
        foldFile.setTexts(save.getTexts());
        foldFile.setVersion(ResourceUtil.getVersionFromManifest());

        return foldFile;
    }

    private Edge findEdge(Vertex v1, Vertex v2, List<Edge> edges) {
        var foundEdge = edges.stream().filter(e -> (e.getStart() == v1 && e.getEnd() == v2) || e.getStart() == v2 && e.getEnd() == v1).findFirst();

        if (foundEdge.isPresent()) {
            return foundEdge.get();
        }

        throw new IllegalStateException("Edge in face not found");
    }

    private double getFoldAngle(LineColor color) {
        return switch (color) {
            case BLUE_2 -> 180;
            case RED_1 -> -180;
            default -> 0;
        };
    }

    @Override
    public boolean supports(File filename) {
        return filename.getName().endsWith(".fold");
    }

    @Override
    public void doExport(Save save, File file) throws IOException {
        try {
            LineSegmentSet s = new LineSegmentSet();
            s.setSave(save);
            if (s.getNumLineSegments() == 0) {
                s.addLine(new Point(0, 0), new Point(0, 0), LineColor.BLACK_0);
            }
            exportFile(save, s, file);
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    @Override
    public String getName() {
        return "FOLD";
    }

    @Override
    public String getExtension() {
        return ".fold";
    }
}
