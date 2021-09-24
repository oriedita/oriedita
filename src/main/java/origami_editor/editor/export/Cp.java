package origami_editor.editor.export;

import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami_editor.editor.Save;
import origami_editor.record.Memo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class Cp {
    public static Memo exportFile(Save save) {
        Memo mem = new Memo();
        for (LineSegment s :
                save.getLineSegments()) {
            int color = s.getColor().getNumber() + 1;

            mem.addLine(String.format("%d %s %s %s %s", color, s.getAX(), s.getAY(), s.getBX(), s.getBY()));
        }

        return mem;
    }

    public static Save importFile(File mem) throws IOException {
        Save save = new Save();

        double d1, d2, d3, d4;

        //オリヒメ　0.Contour, 1.Mountain, 2.Valley　、ORIPA 1.Contour, 2.Mountain, 3.Valley

        try (BufferedReader reader = new BufferedReader(new FileReader(mem))) {
            String fileLine;

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
                s.set(d1, d2, d3, d4, col);

                save.addLineSegment(s.clone());
            }
        }

        return save;
    }
}
