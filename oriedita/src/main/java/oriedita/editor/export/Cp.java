package oriedita.editor.export;

import org.tinylog.Logger;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveV1;

import java.io.*;
import java.util.StringTokenizer;

public class Cp {

    public static void exportFile(Save save, File file) {
        try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw); PrintWriter pw = new PrintWriter(bw)) {
            for (LineSegment s : save.getLineSegments()) {
                int color = s.getColor().getNumber() + 1;

                pw.println(String.format("%d %s %s %s %s", color, s.determineAX(), s.determineAY(), s.determineBX(), s.determineBY()));
            }
        } catch (IOException e) {
            Logger.error(e, "Error exporting cp file");
        }
    }

    public static Save importFile(BufferedReader reader) throws IOException {
        Save save = new SaveV1();

        double d1, d2, d3, d4;

        String fileLine;
        //オリヒメ　0.Contour, 1.Mountain, 2.Valley　、ORIPA 1.Contour, 2.Mountain, 3.Valley

        while ((fileLine = reader.readLine()) != null) {
            StringTokenizer tk = new StringTokenizer(fileLine, " ");
            String str = tk.nextToken();
            LineColor col;
            switch (str) {
                case "1":
                    col = LineColor.BLACK_0;
                    break;
                case "2":
                    col = LineColor.RED_1;
                    break;
                case "3":
                    col = LineColor.BLUE_2;
                    break;
                case "4":
                    col = LineColor.CYAN_3;
                    break;
                default:
                    continue;
            }
            d1 = Double.parseDouble(tk.nextToken());
            d2 = Double.parseDouble(tk.nextToken());
            d3 = Double.parseDouble(tk.nextToken());
            d4 = Double.parseDouble(tk.nextToken());

            LineSegment s = new LineSegment();
            s.set(d1, d2, d3, d4, col);

            save.addLineSegment(s.clone());
        }

        return save;
    }

    public static Save importFile(InputStream stream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            return importFile(reader);
        }
    }

    public static Save importFile(File mem) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(mem))) {
            return importFile(reader);
        }
    }
}
