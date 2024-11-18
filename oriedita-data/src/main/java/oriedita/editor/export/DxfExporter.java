package oriedita.editor.export;

import jakarta.enterprise.context.ApplicationScoped;
import oriedita.editor.save.Save;
import oriedita.editor.export.api.FileExporter;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


@ApplicationScoped
public class DxfExporter implements FileExporter {

    @Override
    public void doExport(Save save, File file) throws IOException {
        double scale = 3.0;
        double center = 4.0;
        double x1, y1, x2, y2;
        LineColor lineColor;

        try(FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw); PrintWriter pw = new PrintWriter(bw)){
            pw.println("  0");
            pw.println("SECTION");
            pw.println("  2");
            pw.println("HEADER");
            pw.println("  9");
            pw.println("$ACADVER");
            pw.println("  1");
            pw.println("AC1009");
            pw.println("  0");
            pw.println("ENDSEC");
            pw.println("  0");
            pw.println("SECTION");
            pw.println("  2");
            pw.println("ENTITIES");


            for (LineSegment lineSegment : save.getLineSegments()) {
                    pw.println("  0");
                    pw.println("LINE");
                    pw.println("  8");

                    lineColor = lineSegment.getColor();
                    String layerName = "noname";
                    int colorNumber = 0;

                    switch (lineColor) {
                        case BLACK_0:
                            layerName = "CutLine";
                            colorNumber = 250; // gray
                            break;
                        case RED_1:
                            layerName = "MountainLine";
                            colorNumber = 1; // red
                            break;
                        case BLUE_2:
                            layerName = "ValleyLine";
                            colorNumber = 5; // blue
                            break;
                        case CYAN_3:
                            layerName = "AuxiliaryLine";
                            colorNumber = 4; // cyan
                        default:
                    }
                    x1 = lineSegment.determineAX() + 200;
                    y1 = lineSegment.determineAY() - 200;
                    x2 = lineSegment.determineBX() + 200;
                    y2 = lineSegment.determineBY() - 200;

                    pw.println(layerName);
                    pw.println("  6");
                    pw.println("CONTINUOUS");
                    pw.println("  62");
                    pw.println(colorNumber);

                    pw.println("  10");
                    pw.println(scale(x1, scale, center));
                    pw.println("  20");
                    pw.println(scale(y1, -scale, center));

                    pw.println("  11");
                    pw.println(scale(x2, scale, center));
                    pw.println("  21");
                    pw.println(scale(y2, -scale, center));

            }

            pw.println("  0");
            pw.println("ENDSEC");
            pw.println("  0");
            pw.println("EOF");
        }
    }

    private static double scale(double d, double scale, double center) {
        return d * scale + center;
    }

    @Override
    public String getName() {
        return "DXF";
    }

    @Override
    public String getExtension() {
        return ".dxf";
    }
}
