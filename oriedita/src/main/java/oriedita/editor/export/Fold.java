package oriedita.editor.export;

import fold.Exporter;
import fold.FoldFileFormatException;
import fold.Importer;
import fold.model.FoldEdgeAssignment;
import fold.model.FoldFile;
import oriedita.editor.save.Save;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.worker.WireFrame_Worker;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Singleton
public class Fold {
    private final Importer anImporter;
    private final Exporter exporter;

    @Inject
    public Fold(Importer anImporter, Exporter exporter) {
        this.anImporter = anImporter;
        this.exporter = exporter;
    }

    public Save toSave(FoldFile foldFile) {
        Save save = Save.createInstance();

        List<List<Double>> verticeCoords = foldFile.getVertices().getCoords();
        List<FoldEdgeAssignment> edgeAssignments = foldFile.getEdges().getAssignment();

        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for (int i = 0; i < foldFile.getEdges().getVertices().size(); i++) {
            List<Integer> edgeVertices = foldFile.getEdges().getVertices().get(i);

            LineSegment ls = new LineSegment();
            double ax = verticeCoords.get(edgeVertices.get(0)).get(0);
            double ay = verticeCoords.get(edgeVertices.get(0)).get(1);
            ls.setA(new Point(ax, ay));
            double bx = verticeCoords.get(edgeVertices.get(1)).get(0);
            double by = verticeCoords.get(edgeVertices.get(1)).get(1);
            ls.setB(new Point(bx, by));
            ls.setColor(getColor(edgeAssignments.get(i)));

            minX = Math.min(Math.min(minX, ax), bx);
            minY = Math.min(Math.min(minY, ay), by);
            maxX = Math.max(Math.max(maxX, ax), bx);
            maxY = Math.max(Math.max(maxY, ay), by);

            save.addLineSegment(ls);
        }

        FoldLineSet ori_s_temp = new FoldLineSet();    //セレクトされた折線だけ取り出すために使う
        ori_s_temp.setSave(save);//セレクトされた折線だけ取り出してori_s_tempを作る
        ori_s_temp.move(
                new Point(minX, minY),
                new Point(minX, maxY),
                new Point(-200, -200),
                new Point(-200, 200)
        );

        Save save1 = Save.createInstance();
        ori_s_temp.getSave(save1);

        return save1;
    }

    private LineColor getColor(FoldEdgeAssignment edgeAssignment) {
        switch (edgeAssignment) {
            case BORDER:
                return LineColor.BLACK_0;
            case MOUNTAIN_FOLD:
                return LineColor.RED_1;
            case VALLEY_FOLD:
                return LineColor.BLUE_2;
            case FLAT_FOLD:
                return LineColor.CYAN_3;
            case UNASSIGNED:
            default:
                return LineColor.BLACK_0;
        }
    }

    private FoldEdgeAssignment getAssignment(LineColor lineColor) {
        switch (lineColor) {
            case ANGLE:
            case NONE:
            default:
                return FoldEdgeAssignment.UNASSIGNED;
            case BLACK_0:
                return FoldEdgeAssignment.BORDER;
            case RED_1:
                return FoldEdgeAssignment.MOUNTAIN_FOLD;
            case BLUE_2:
                return FoldEdgeAssignment.VALLEY_FOLD;
            case CYAN_3:
            case ORANGE_4:
            case MAGENTA_5:
            case GREEN_6:
            case YELLOW_7:
            case PURPLE_8:
            case OTHER_9:
                return FoldEdgeAssignment.FLAT_FOLD;
        }
    }

    public Save importFile(File file) throws FoldFileFormatException {
        return toSave(anImporter.importFile(file));
    }


    public void exportFile(LineSegmentSet lineSegmentSet, File file) throws InterruptedException, FoldFileFormatException {
        exporter.exportFile(file, toFoldSave(lineSegmentSet));
    }

    public FoldFile toFoldSave(LineSegmentSet save) throws InterruptedException {
        WireFrame_Worker wireFrame_worker = new WireFrame_Worker(3.0);
        wireFrame_worker.setLineSegmentSet(save);

        PointSet pointSet = wireFrame_worker.get();

        FoldFile foldFile = new FoldFile();

        List<FoldEdgeAssignment> edgeAssignments = new ArrayList<>(pointSet.getNumLines());
        List<List<Integer>> edgeVertices = new ArrayList<>(pointSet.getNumLines());
        List<List<Double>> verticesCoords = new ArrayList<>(pointSet.getNumPoints());
        List<List<Integer>> faceVertices = new ArrayList<>(pointSet.getNumFaces());

        List<Double> edgesFoldAngles = new ArrayList<>(pointSet.getNumLines());

        for (int i = 1; i <= pointSet.getNumLines(); i++) {
            edgeAssignments.add(getAssignment(pointSet.getColor(i)));
            edgesFoldAngles.add(getFoldAngle(pointSet.getColor(i)));
            edgeVertices.add(Arrays.asList(pointSet.getBegin(i) - 1, pointSet.getEnd(i) - 1));
        }

        for (int i = 1; i <= pointSet.getNumPoints(); i++) {
            verticesCoords.add(toFoldPoint(pointSet.getPoint(i)));
        }

        for (int i = 1; i <= pointSet.getNumFaces(); i++) {
            int numPoints = pointSet.getFace(i).getNumPoints();
            List<Integer> faceVertex = new ArrayList<>(numPoints);

            for (int j = 1; j <= numPoints; j++) {
                faceVertex.add(pointSet.getFace(i).getPointId(j) -1);
            }

            faceVertices.add(faceVertex);
        }

        foldFile.getEdges().setAssignment(edgeAssignments);
        foldFile.getEdges().setVertices(edgeVertices);
        foldFile.getEdges().setFoldAngle(edgesFoldAngles);
        foldFile.getFaces().setVertices(faceVertices);
        foldFile.getVertices().setCoords(verticesCoords);

        return foldFile;
    }

    private double getFoldAngle(LineColor color) {
        switch (color) {
            case BLUE_2:
                return 180;
            case RED_1:
                return -180;
            default:
                return 0;
        }
    }

    private List<Double> toFoldPoint(Point p) {
        return Arrays.asList(p.getX(), p.getY());
    }
}
