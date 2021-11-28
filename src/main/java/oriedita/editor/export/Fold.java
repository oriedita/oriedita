package oriedita.editor.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import oriedita.editor.exception.FileReadingException;
import oriedita.editor.export.fold.FoldEdgeAssignment;
import oriedita.editor.export.fold.FoldObjectMapper;
import oriedita.editor.export.fold.FoldSave;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveV1;
import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.worker.WireFrame_Worker;

import java.io.File;
import java.io.IOException;

public class Fold {
    public static Save toSave(FoldSave foldSave) {
        Save save = new SaveV1();

        double[][] verticeCoords = foldSave.getVerticesCoords();
        FoldEdgeAssignment[] edgeAssignments = foldSave.getEdgesAssignment();

        for (int i = 0; i < foldSave.getEdgesVertices().length; i++) {
            int[] edgeVertices = foldSave.getEdgesVertices()[i];

            LineSegment ls = new LineSegment();
            ls.setA(new Point(verticeCoords[edgeVertices[0]][0] * 400 - 200, verticeCoords[edgeVertices[0]][1] * 400 - 200));
            ls.setB(new Point(verticeCoords[edgeVertices[1]][0] * 400 - 200, verticeCoords[edgeVertices[1]][1] * 400 - 200));
            ls.setColor(getColor(edgeAssignments[i]));

            save.addLineSegment(ls);
        }

        return save;
    }

    private static LineColor getColor(FoldEdgeAssignment edgeAssignment) {
        switch (edgeAssignment) {
            case B:
                return LineColor.BLACK_0;
            case M:
                return LineColor.RED_1;
            case V:
                return LineColor.BLUE_2;
            case F:
                return LineColor.CYAN_3;
            case U:
            default:
                return LineColor.BLACK_0;
        }
    }

    private static FoldEdgeAssignment getAssignment(LineColor lineColor) {
        switch (lineColor) {
            case ANGLE:
            default:
                return FoldEdgeAssignment.U;
            case NONE:
                return FoldEdgeAssignment.U;
            case BLACK_0:
                return FoldEdgeAssignment.B;
            case RED_1:
                return FoldEdgeAssignment.M;
            case BLUE_2:
                return FoldEdgeAssignment.V;
            case CYAN_3:
                return FoldEdgeAssignment.F;
            case ORANGE_4:
                return FoldEdgeAssignment.F;
            case MAGENTA_5:
                return FoldEdgeAssignment.F;
            case GREEN_6:
                return FoldEdgeAssignment.F;
            case YELLOW_7:
                return FoldEdgeAssignment.F;
            case PURPLE_8:
                return FoldEdgeAssignment.F;
            case OTHER_9:
                return FoldEdgeAssignment.F;
        }
    }

    public static Save importFile(File file) throws FileReadingException {
        return toSave(importFoldFile(file));
    }

    public static FoldSave importFoldFile(File file) throws FileReadingException {
        try {
            ObjectMapper mapper = new FoldObjectMapper();
            return mapper.readValue(file, FoldSave.class);
        } catch (IOException e) {
            throw new FileReadingException(e);
        }
    }

    public static void exportFoldFile(File file, FoldSave save) {
        try {
            ObjectMapper mapper = new FoldObjectMapper();

            mapper.writeValue(file, save);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportFile(LineSegmentSet lineSegmentSet, File file) throws InterruptedException {
        exportFoldFile(file, toFoldSave(lineSegmentSet));
    }

    public static FoldSave toFoldSave(LineSegmentSet save) throws InterruptedException {
        WireFrame_Worker wireFrame_worker = new WireFrame_Worker(3.0);
        wireFrame_worker.setLineSegmentSet(save);

        PointSet pointSet = wireFrame_worker.get();

        FoldSave foldSave = new FoldSave();

        FoldEdgeAssignment[] edgeAssignments = new FoldEdgeAssignment[pointSet.getNumLines()];
        int[][] edgeVertices = new int[pointSet.getNumLines()][];
        double[][] verticesCoords = new double[pointSet.getNumPoints()][];
        int[][] faceEdges = new int[pointSet.getNumFaces()][];
        int[][] faceVertices = new int[pointSet.getNumFaces()][];

        double[] edgesFoldAngles = new double[pointSet.getNumLines()];

        for (int i = 1; i <= pointSet.getNumLines(); i++) {
            edgeAssignments[i - 1] = getAssignment(pointSet.getColor(i));
            edgesFoldAngles[i - 1] = getFoldAngle(pointSet.getColor(i));
            edgeVertices[i - 1] = new int[]{pointSet.getBegin(i) - 1, pointSet.getEnd(i) - 1};
        }

        for (int i = 1; i <= pointSet.getNumPoints(); i++) {
            verticesCoords[i - 1] = toFoldPoint(pointSet.getPoint(i));
        }

        for (int i = 1; i <= pointSet.getNumFaces(); i++) {
            int numPoints = pointSet.getFace(i).getNumPoints();
            int[] faceVertex = new int[numPoints];

            for (int j = 1; j <= numPoints; j++) {
                faceVertex[j - 1] = pointSet.getFace(i).getPointId(j) - 1;
            }

            faceVertices[i - 1] = faceVertex;
        }

        foldSave.setEdgesAssignment(edgeAssignments);
        foldSave.setVerticesCoords(verticesCoords);
        foldSave.setEdgesVertices(edgeVertices);
        foldSave.setFacesVertices(faceVertices);
        foldSave.setEdgesFoldAngle(edgesFoldAngles);

        return foldSave;
    }

    private static double getFoldAngle(LineColor color) {
        switch (color) {
            case BLUE_2:
                return 180;
            case RED_1:
                return -180;
            default:
                return 0;
        }
    }

    private static double[] toFoldPoint(Point p) {
        return new double[]{(p.getX() + 200) / 400, (p.getY() + 200) / 400};
    }
}
