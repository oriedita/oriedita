package oriedita.editor.export;

import fold.io.CustomFoldReader;
import fold.model.Edge;
import fold.model.FoldEdgeAssignment;
import fold.model.FoldFrame;
import jakarta.enterprise.context.ApplicationScoped;
import oriedita.editor.databinding.GridModel;
import oriedita.editor.exception.FileReadingException;
import oriedita.editor.export.api.FileImporter;
import oriedita.editor.save.OrieditaFoldFile;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveProvider;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

@ApplicationScoped
public class FoldImporter implements FileImporter {
    @Override
    public boolean supports(File filename) {
        return filename.getName().endsWith(".fold");
    }

    public Save toSave(OrieditaFoldFile foldFile) {
        Save save = SaveProvider.createInstance();

        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        FoldFrame rootFrame = foldFile.getRootFrame();

        for (int i = 0; i < rootFrame.getEdges().size(); i++) {
            Edge edge = rootFrame.getEdges().get(i);

            double ax = edge.getStart().getX();
            double ay = edge.getStart().getY();
            double bx = edge.getEnd().getX();
            double by = edge.getEnd().getY();
            LineSegment ls = new LineSegment(
                    new Point(ax, ay),
                    new Point(bx, by),
                    getColor(edge.getAssignment()));

            minX = Math.min(Math.min(minX, ax), bx);
            minY = Math.min(Math.min(minY, ay), by);
            maxX = Math.max(Math.max(maxX, ax), bx);
            maxY = Math.max(Math.max(maxY, ay), by);

            save.addLineSegment(ls);
        }

        save.setCircles(new ArrayList<>(foldFile.getCircles()));

        FoldLineSet ori_s_temp = new FoldLineSet();    //セレクトされた折線だけ取り出すために使う
        ori_s_temp.setSave(save);//セレクトされた折線だけ取り出してori_s_tempを作る
        ori_s_temp.move(
                new Point(minX, minY),
                new Point(minX, maxY),
                new Point(-200, -200),
                new Point(-200, 200)
        );

        Save save1 = SaveProvider.createInstance();
        ori_s_temp.getSave(save1);

        save1.setTexts(new ArrayList<>(foldFile.getTexts()));
        var gridModel = new GridModel();
        gridModel.setGridSize(foldFile.getGridSize());
        gridModel.setBaseState(foldFile.getGridStyle());
        save1.setGridModel(gridModel);

        return save1;
    }

    public static LineColor getColor(FoldEdgeAssignment edgeAssignment) {
        return switch (edgeAssignment) {
            case MOUNTAIN_FOLD -> LineColor.RED_1;
            case VALLEY_FOLD -> LineColor.BLUE_2;
            case FLAT_FOLD -> LineColor.CYAN_3;
            default -> LineColor.BLACK_0;
        };
    }

    public Save importFile(File file) throws FileReadingException, IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            CustomFoldReader<OrieditaFoldFile> orieditaFoldFileCustomFoldReader = new CustomFoldReader<>(OrieditaFoldFile.class, fileInputStream);
            return toSave(orieditaFoldFileCustomFoldReader.read());
        }
    }

    @Override
    public Save doImport(File file) throws IOException {
        try {
            return importFile(file);
        } catch (FileReadingException e) {
            throw new RuntimeException(e);
        }
    }
}
