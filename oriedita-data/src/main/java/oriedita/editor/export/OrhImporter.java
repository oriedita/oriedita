package oriedita.editor.export;

import jakarta.enterprise.context.ApplicationScoped;
import org.tinylog.Logger;
import oriedita.editor.canvas.LineStyle;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.GridModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.export.api.FileImporter;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveProvider;
import oriedita.editor.tools.StringOp;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class OrhImporter implements FileImporter {
    @Override
    public boolean supports(File filename) {
        return filename.getName().endsWith(".orh");
    }

    /**
     * Read an Orihime file
     */
    @Override
    public Save doImport(File file) throws IOException {
        Save save = SaveProvider.createInstance();
        Pattern p = Pattern.compile("<(.+)>(.+)</(.+)>");

        boolean reading;

        // Loading the camera settings for the development view
        reading = false;

        List<String> fileLines = loadFile(file);

        if (fileLines == null) {
            throw new IOException("Encoding not detected");
        }

        for (String str : fileLines) {
            if (str.equals("<camera_of_orisen_nyuuryokuzu>")) {
                reading = true;
            } else if (str.equals("</camera_of_orisen_nyuuryokuzu>")) {
                reading = false;
            } else {
                if (!reading) {
                    continue;
                }

                Matcher m = p.matcher(str);

                if (!m.matches()) {
                    continue;
                }

                Camera creasePatternCamera = new Camera();
                save.setCreasePatternCamera(creasePatternCamera);
                switch (m.group(1)) {
                    case "camera_ichi_x":

                        creasePatternCamera.setCameraPositionX(Double.parseDouble(m.group(2)));
                        break;
                    case "camera_ichi_y":
                        creasePatternCamera.setCameraPositionY(Double.parseDouble(m.group(2)));
                        break;
                    case "camera_kakudo":
                        creasePatternCamera.setCameraAngle(Double.parseDouble(m.group(2)));
                        break;
                    case "camera_kagami":
                        creasePatternCamera.setCameraMirror(Double.parseDouble(m.group(2)));
                        break;
                    case "camera_bairitsu_x":
                        creasePatternCamera.setCameraZoomX(Double.parseDouble(m.group(2)));
                        break;
                    case "camera_bairitsu_y":
                        creasePatternCamera.setCameraZoomY(Double.parseDouble(m.group(2)));
                        break;
                    case "hyouji_ichi_x":
                        creasePatternCamera.setDisplayPositionX(Double.parseDouble(m.group(2)));
                        break;
                    case "hyouji_ichi_y":
                        creasePatternCamera.setDisplayPositionY(Double.parseDouble(m.group(2)));
                        break;
                }
            }
        }

        CanvasModel canvasModel = new CanvasModel();
        save.setCanvasModel(canvasModel);

        ApplicationModel applicationModel = new ApplicationModel();
        save.setApplicationModel(applicationModel);

        // ----------------------------------------- チェックボックス等の設定の読み込み
        reading = false;
        for (String str : fileLines) {
            if (str.equals("<settei>")) {
                reading = true;
            } else if (str.equals("</settei>")) {
                reading = false;
            } else {
                if (!reading) {
                    continue;
                }
                Matcher m = p.matcher(str);
                if (!m.matches()) {
                    continue;
                }

                String value = m.group(2).trim();
                switch (m.group(1)) {
                    case "mouseSettingsAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        applicationModel.setMouseWheelMovesCreasePattern(selected);
                        break;
                    }
                    case "showPointRangeAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        applicationModel.setDisplayPointSpotlight(selected);
                        break;
                    }
                    case "pointOffsetAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        applicationModel.setDisplayPointOffset(selected);
                        break;
                    }
                    case "gridInputAssistAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        applicationModel.setDisplayGridInputAssist(selected);
                        break;
                    }
                    case "displayCommentsAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        applicationModel.setDisplayComments(selected);
                        break;
                    }
                    case "displayCpLinesAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        applicationModel.setDisplayCpLines(selected);
                        break;
                    }
                    case "displayAuxLinesAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        applicationModel.setDisplayAuxLines(selected);
                        break;
                    }
                    case "displayLiveAuxLinesAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        applicationModel.setDisplayLiveAuxLines(selected);
                        break;
                    }
                    case "displayStandardFaceMarksAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        applicationModel.setDisplayMarkings(selected);
                        break;
                    }
                    case "cpOnTopAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        applicationModel.setDisplayCreasePatternOnTop(selected);
                        break;
                    }
                    case "ckbox_oritatami_keika": {
                        boolean selected = Boolean.parseBoolean(value);
                        applicationModel.setDisplayFoldingProgress(selected);
                        break;
                    }
                    case "iTenkaizuSenhaba":
                        applicationModel.setLineWidth(Integer.parseInt(value));
                        break;
                    case "ir_ten":
                        applicationModel.setPointSize(Integer.parseInt(value));
                        break;
                    case "i_orisen_hyougen":
                        applicationModel.setLineStyle(LineStyle.from(value));
                        break;
                    case "i_anti_alias":
                        applicationModel.setAntiAlias(Boolean.parseBoolean(value));
                        break;
                }
            }
        }

        // ----------------------------------------- 格子設定の読み込み

        reading = false;
        GridModel gridModel = new GridModel();
        save.setGridModel(gridModel);
        double gridXA = 0.0;
        double gridXB = 1.0;
        double gridXC = 1.0;
        double gridYA = 0.0;
        double gridYB = 1.0;
        double gridYC = 1.0;
        for (String str : fileLines) {
            if (str.equals("<Kousi>")) {
                reading = true;
            } else if (str.equals("</Kousi>")) {
                reading = false;
            } else {
                if (!reading) {
                    continue;
                }

                Matcher m = p.matcher(str);
                if (!m.matches()) {
                    continue;
                }

                switch (m.group(1)) {
                    case "i_kitei_jyoutai":
                        gridModel.setBaseState(GridModel.State.from(m.group(2)));
                        break;
                    case "nyuuryoku_kitei":
                        gridModel.setGridSize(StringOp.String2int(m.group(2), gridModel.getGridSize()));
                        break;
                    case "memori_kankaku":
                        int scale_interval = Integer.parseInt(m.group(2));

                        gridModel.setIntervalGridSize(scale_interval);
                        break;
                    case "a_to_heikouna_memori_iti":
                        gridModel.setHorizontalScalePosition(Integer.parseInt(m.group(2)));
                        break;
                    case "b_to_heikouna_memori_iti":
                        gridModel.setVerticalScalePosition(Integer.parseInt(m.group(2)));
                        break;
                    case "kousi_senhaba":
                        applicationModel.setGridLineWidth(Integer.parseInt(m.group(2)));
                        break;
                    case "d_kousi_x_a":
                        gridXA = StringOp.String2double(m.group(2), gridModel.getGridXA());
                        break;
                    case "d_kousi_x_b":
                        gridXB = StringOp.String2double(m.group(2), gridModel.getGridXB());
                        break;
                    case "d_kousi_x_c":
                        gridXC = StringOp.String2double(m.group(2), gridModel.getGridXC());
                        break;
                    case "d_kousi_y_a":
                        gridYA = StringOp.String2double(m.group(2), gridModel.getGridYA());
                        break;
                    case "d_kousi_y_b":
                        gridYB = StringOp.String2double(m.group(2), gridModel.getGridYB());
                        break;
                    case "d_kousi_y_c":
                        gridYC = StringOp.String2double(m.group(2), gridModel.getGridYC());
                        break;
                    case "d_kousi_kakudo":
                        gridModel.setGridAngle(StringOp.String2double(m.group(2), gridModel.getGridAngle()));
                        break;
                }

            }
        }

        gridModel.applyGridX(gridXA, gridXB, gridXC);
        gridModel.applyGridY(gridYA, gridYB, gridYC);

        // ----------------------------------------- 格子色設定の読み込み
        int i_grid_color_R = 0;
        int i_grid_color_G = 0;
        int i_grid_color_B = 0;
        int i_grid_memori_color_R = 0;
        int i_grid_memori_color_G = 0;
        int i_grid_memori_color_B = 0;

        boolean i_Grid_iro_yomikomi = false;//Kousi_iroの読み込みがあったら1、なければ0
        reading = false;
        for (String str : fileLines) {
            if (str.equals("<Kousi_iro>")) {
                reading = true;
                i_Grid_iro_yomikomi = true;
            } else if (str.equals("</Kousi_iro>")) {
                reading = false;
            } else {
                if (!reading) {
                    continue;
                }
                Matcher m = p.matcher(str);

                if (!m.matches()) {
                    continue;
                }

                switch (m.group(1)) {
                    case "kousi_color_R":
                        i_grid_color_R = (Integer.parseInt(m.group(2)));
                        break;
                    case "kousi_color_G":
                        i_grid_color_G = (Integer.parseInt(m.group(2)));
                        break;
                    case "kousi_color_B":
                        i_grid_color_B = (Integer.parseInt(m.group(2)));
                        break;
                    case "kousi_memori_color_R":
                        i_grid_memori_color_R = (Integer.parseInt(m.group(2)));
                        break;
                    case "kousi_memori_color_G":
                        i_grid_memori_color_G = (Integer.parseInt(m.group(2)));
                        break;
                    case "kousi_memori_color_B":
                        i_grid_memori_color_B = (Integer.parseInt(m.group(2)));
                        break;
                }
            }
        }

        if (i_Grid_iro_yomikomi) {//Grid_iroの読み込みがあったら1、なければ0
            applicationModel.setGridColor(new Color(i_grid_color_R, i_grid_color_G, i_grid_color_B));

            Logger.info("i_kousi_memori_color_R= " + i_grid_memori_color_R);
            Logger.info("i_kousi_memori_color_G= " + i_grid_memori_color_G);
            Logger.info("i_kousi_memori_color_B= " + i_grid_memori_color_B);
            applicationModel.setGridScaleColor(new Color(i_grid_memori_color_R, i_grid_memori_color_G, i_grid_memori_color_B));
        }

        // 折り上がり図設定の読み込み -------------------------------------------------------------------------

        int i_oriagarizu_F_color_R = 0;
        int i_oriagarizu_F_color_G = 0;
        int i_oriagarizu_F_color_B = 0;

        int i_oriagarizu_B_color_R = 0;
        int i_oriagarizu_B_color_G = 0;
        int i_oriagarizu_B_color_B = 0;

        int i_oriagarizu_L_color_R = 0;
        int i_oriagarizu_L_color_G = 0;
        int i_oriagarizu_L_color_B = 0;


        boolean i_oriagarizu_yomikomi = false;//oriagarizuの読み込みがあったら1、なければ0
        reading = false;
        for (String str : fileLines) {
            if (str.equals("<oriagarizu>")) {
                reading = true;
                i_oriagarizu_yomikomi = true;
            } else if (str.equals("</oriagarizu>")) {
                reading = false;
            } else {
                if (!reading) {
                    continue;
                }
                Matcher m = p.matcher(str);
                if (!m.matches()) {
                    continue;
                }

                switch (m.group(1)) {
                    case "oriagarizu_F_color_R":
                        i_oriagarizu_F_color_R = (Integer.parseInt(m.group(2)));
                        break;
                    case "oriagarizu_F_color_G":
                        i_oriagarizu_F_color_G = (Integer.parseInt(m.group(2)));
                        break;
                    case "oriagarizu_F_color_B":
                        i_oriagarizu_F_color_B = (Integer.parseInt(m.group(2)));
                        break;
                    case "oriagarizu_B_color_R":
                        i_oriagarizu_B_color_R = (Integer.parseInt(m.group(2)));
                        break;
                    case "oriagarizu_B_color_G":
                        i_oriagarizu_B_color_G = (Integer.parseInt(m.group(2)));
                        break;
                    case "oriagarizu_B_color_B":
                        i_oriagarizu_B_color_B = (Integer.parseInt(m.group(2)));
                        break;
                    case "oriagarizu_L_color_R":
                        i_oriagarizu_L_color_R = (Integer.parseInt(m.group(2)));
                        break;
                    case "oriagarizu_L_color_G":
                        i_oriagarizu_L_color_G = (Integer.parseInt(m.group(2)));
                        break;
                    case "oriagarizu_L_color_B":
                        i_oriagarizu_L_color_B = (Integer.parseInt(m.group(2)));
                        break;
                }
            }
        }

        FoldedFigureModel foldedFigureModel = new FoldedFigureModel();
        save.setFoldedFigureModel(foldedFigureModel);
        if (i_oriagarizu_yomikomi) {
            foldedFigureModel.setFrontColor(new Color(i_oriagarizu_F_color_R, i_oriagarizu_F_color_G, i_oriagarizu_F_color_B));
            foldedFigureModel.setBackColor(new Color(i_oriagarizu_B_color_R, i_oriagarizu_B_color_G, i_oriagarizu_B_color_B));
            foldedFigureModel.setLineColor(new Color(i_oriagarizu_L_color_R, i_oriagarizu_L_color_G, i_oriagarizu_L_color_B));
        }

        int reading_flag = 0;//If it is 0, it will not be read. If it is 1, read it.
        int number = 0;
        LineColor ic;
        LineSegment.ActiveState is;


        String r_title = "_";

        double ax, ay, bx, by;
        double dx, dy, dr;

        String str;

        //Read the file .orh for Orihime

        //First find the total number of line segments
        int numLines = 0;
        for (String line : fileLines) {
            StringTokenizer tk = new StringTokenizer(line, ",");

            str = tk.nextToken();
            if (str.equals("<線分集合>")) {
                reading_flag = 1;
            }
            if (str.equals("<円集合>")) {
                reading_flag = 3;
            }
            if ((reading_flag == 1) && (str.equals("番号"))) {
                numLines++;
            }
        }

        while (save.getLineSegments().size() <= numLines) {
            save.addLineSegment(new LineSegment());
            save.addCircle(new Circle());
        }
        //First the total number of line segments was calculated

        Circle e_temp = new Circle();

        int i_customized_color_R = 0;
        int i_customized_color_G = 0;
        int i_customized_color_B = 0;

        List<Circle> circles = save.getCircles();
        for (String str_i : fileLines) {
            //Old-fashioned reading method
            StringTokenizer tk = new StringTokenizer(str_i, ",");
            str = tk.nextToken();

            if (str.equals("<タイトル>")) {
                reading_flag = 2;
            }
            if ((reading_flag == 2) && (str.equals("タイトル"))) {
                str = tk.nextToken();
                r_title = str;
            }

            if (str.equals("<線分集合>")) {
                reading_flag = 1;
            }
            if ((reading_flag == 1) && (str.equals("番号"))) {
                str = tk.nextToken();
                number = Integer.parseInt(str) - 1;
            }
            LineSegment s = save.getLineSegments().get(number);
            if ((reading_flag == 1) && (str.equals("色"))) {
                str = tk.nextToken();
                ic = LineColor.from(str);
                s = s.withColor(ic);
            }

            if (reading_flag == 1) {
                String[] st_new = str_i.split(">", 2);// <-----------------------------------２つに分割するときは2を指定
                if (st_new[0].equals("<tpp")) {
                    String[] s_new = st_new[1].split("<", 2);
                    int i_customized = (Integer.parseInt(s_new[0]));
                    s.setCustomized(i_customized);
                }

                if (st_new[0].equals("<tpp_color_R")) {
                    String[] s_new = st_new[1].split("<", 2);
                    i_customized_color_R = (Integer.parseInt(s_new[0]));
                    s.setCustomizedColor(new Color(i_customized_color_R, i_customized_color_G, i_customized_color_B));
                }

                if (st_new[0].equals("<tpp_color_G")) {
                    String[] s_new = st_new[1].split("<", 2);
                    i_customized_color_G = (Integer.parseInt(s_new[0]));
                    s.setCustomizedColor(new Color(i_customized_color_R, i_customized_color_G, i_customized_color_B));
                }
                if (st_new[0].equals("<tpp_color_B")) {
                    String[] s_new = st_new[1].split("<", 2);
                    i_customized_color_B = (Integer.parseInt(s_new[0]));
                    s.setCustomizedColor(new Color(i_customized_color_R, i_customized_color_G, i_customized_color_B));
                }
            }

            if ((reading_flag == 1) && (str.equals("iactive"))) {//20181110追加
                str = tk.nextToken();
                is = LineSegment.ActiveState.valueOf(str);
                s.setActive(is);
            }

            if ((reading_flag == 1) && (str.equals("選択"))) {
                str = tk.nextToken();
                int isel = Integer.parseInt(str);
                s.setSelected(isel);
            }
            if ((reading_flag == 1) && (str.equals("座標"))) {
                str = tk.nextToken();
                ax = Double.parseDouble(str);
                str = tk.nextToken();
                ay = Double.parseDouble(str);
                str = tk.nextToken();
                bx = Double.parseDouble(str);
                str = tk.nextToken();
                by = Double.parseDouble(str);

                s = s.withCoordinates(ax, ay, bx, by);
            }
            // TODO: test performance, implement and use LineSegmentBuilder if too slow
            save.getLineSegments().set(number, s);
            if (str.equals("<円集合>")) {
                reading_flag = 3;
            }

            if ((reading_flag == 3) && (str.equals("番号"))) {
                str = tk.nextToken();
                number = Integer.parseInt(str) - 1;

                save.getCircles().get(number).set(e_temp);
            }

            if ((reading_flag == 3) && (str.equals("中心と半径と色"))) {
                str = tk.nextToken();
                dx = Double.parseDouble(str);
                str = tk.nextToken();
                dy = Double.parseDouble(str);
                str = tk.nextToken();
                dr = Double.parseDouble(str);

                str = tk.nextToken();
                ic = LineColor.from(str);


                circles.get(number).set(dx, dy, dr, ic);
            }

            if (reading_flag == 3) {
                String[] st_new = str_i.split(">", 2);// <-----------------------------------２つに分割するときは2を指定
                if (st_new[0].equals("<tpp")) {
                    String[] s_new = st_new[1].split("<", 2);
                    int i_customized = (Integer.parseInt(s_new[0]));
                    circles.get(number).setCustomized(i_customized);
                }

                if (st_new[0].equals("<tpp_color_R")) {
                    String[] s_new = st_new[1].split("<", 2);
                    i_customized_color_R = (Integer.parseInt(s_new[0]));
                    circles.get(number).setCustomizedColor(new Color(i_customized_color_R, i_customized_color_G, i_customized_color_B));
                }

                if (st_new[0].equals("<tpp_color_G")) {
                    String[] s_new = st_new[1].split("<", 2);
                    i_customized_color_G = (Integer.parseInt(s_new[0]));
                    circles.get(number).setCustomizedColor(new Color(i_customized_color_R, i_customized_color_G, i_customized_color_B));
                }
                if (st_new[0].equals("<tpp_color_B")) {
                    String[] s_new = st_new[1].split("<", 2);
                    i_customized_color_B = (Integer.parseInt(s_new[0]));
                    circles.get(number).setCustomizedColor(new Color(i_customized_color_R, i_customized_color_G, i_customized_color_B));
                }
            }
        }

        save.setTitle(r_title);

        return save;
    }

    private static List<String> loadFile(File file) throws IOException {
        // Possible charsets
        List<Charset> possibleCharsets = List.of(
                StandardCharsets.UTF_8,
                Charset.forName("EUC-JP"),
                Charset.forName("EUC-CN"),
                Charset.forName("Shift_JIS"),
                Charset.forName("GBK")
        );

        for (Charset charset : possibleCharsets) {
            try {
                return Files.readAllLines(file.toPath(), charset);
            } catch (CharacterCodingException exception) {
                Logger.info("File is not " + charset.displayName());
                // ignored
            }
        }

        return null;
    }
}
