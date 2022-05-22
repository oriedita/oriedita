package oriedita.editor.export;

import fold.FoldFactory;
import fold.FoldFileFormatException;
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

import java.io.File;

public class Fold {
    public static Save toSave(FoldFile foldFile) {
        Save save = Save.createInstance();

        double[][] verticeCoords = foldFile.getVertices().getCoords();
        FoldEdgeAssignment[] edgeAssignments = foldFile.getEdges().getAssignment();

        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for (int i = 0; i < foldFile.getEdges().getVertices().length; i++) {
            int[] edgeVertices = foldFile.getEdges().getVertices()[i];

            LineSegment ls = new LineSegment();
            double ax = verticeCoords[edgeVertices[0]][0];
            double ay = verticeCoords[edgeVertices[0]][1];
            ls.setA(new Point(ax, ay));
            double bx = verticeCoords[edgeVertices[1]][0];
            double by = verticeCoords[edgeVertices[1]][1];
            ls.setB(new Point(bx, by));
            ls.setColor(getColor(edgeAssignments[i]));

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
            case NONE:
            default:
                return FoldEdgeAssignment.U;
            case BLACK_0:
                return FoldEdgeAssignment.B;
            case RED_1:
                return FoldEdgeAssignment.M;
            case BLUE_2:
                return FoldEdgeAssignment.V;
            case CYAN_3:
            case ORANGE_4:
            case MAGENTA_5:
            case GREEN_6:
            case YELLOW_7:
            case PURPLE_8:
            case OTHER_9:
                return FoldEdgeAssignment.F;
        }
    }

    public static Save importFile(File file) throws FoldFileFormatException {
        return toSave(FoldFactory.foldImport().importFoldFile(file));
    }


    public static void exportFile(LineSegmentSet lineSegmentSet, File file) throws InterruptedException, FoldFileFormatException {
        FoldFactory.foldExport().exportFoldFile(file, toFoldSave(lineSegmentSet));
    }

    public static FoldFile toFoldSave(LineSegmentSet save) throws InterruptedException {
        WireFrame_Worker wireFrame_worker = new WireFrame_Worker(3.0);
        wireFrame_worker.setLineSegmentSet(save);

        PointSet pointSet = wireFrame_worker.get();

        FoldFile foldFile = new FoldFile();

        FoldEdgeAssignment[] edgeAssignments = new FoldEdgeAssignment[pointSet.getNumLines()];
        int[][] edgeVertices = new int[pointSet.getNumLines()][];
        double[][] verticesCoords = new double[pointSet.getNumPoints()][];
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

        foldFile.getEdges().setAssignment(edgeAssignments);
        foldFile.getEdges().setVertices(edgeVertices);
        foldFile.getEdges().setFoldAngle(edgesFoldAngles);
        foldFile.getFaces().setVertices(faceVertices);
        foldFile.getVertices().setCoords(verticesCoords);

        return foldFile;
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
        return new double[]{p.getX(), p.getY()};
    }
}
