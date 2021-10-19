package origami_editor.editor.export;

import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami_editor.editor.Save;

import java.io.*;
import java.util.StringTokenizer;

public class Cp {
    private static int SCALE_FACTOR = 10;

    public static void exportFile(Save save, File file) {
        try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw); PrintWriter pw = new PrintWriter(bw)) {
            for (LineSegment s : save.getLineSegments()) {
                int color = s.getColor().getNumber() + 1;

                pw.println(String.format("%d %s %s %s %s", color, s.determineAX() / SCALE_FACTOR, s.determineAY() / SCALE_FACTOR, s.determineBX() / SCALE_FACTOR, s.determineBY() / SCALE_FACTOR));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Save importFile(BufferedReader reader) throws IOException {
        Save save = new Save();

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
                default:
                    continue;
            }
            d1 = Double.parseDouble(tk.nextToken());
            d2 = Double.parseDouble(tk.nextToken());
            d3 = Double.parseDouble(tk.nextToken());
            d4 = Double.parseDouble(tk.nextToken());

            LineSegment s = new LineSegment();
            s.set(d1 * SCALE_FACTOR, d2 * SCALE_FACTOR, d3 * SCALE_FACTOR, d4 * SCALE_FACTOR, col);

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
