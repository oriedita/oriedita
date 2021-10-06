package origami_editor.editor.export;

import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami_editor.editor.LineStyle;
import origami_editor.editor.Save;
import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.databinding.FoldedFigureModel;
import origami_editor.editor.databinding.GridModel;
import origami_editor.graphic2d.grid.Grid;
import origami_editor.tools.Camera;
import origami_editor.tools.StringOp;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Orh {
    /**
     * Read an Orihime file
     */
    public static Save importFile(File file) throws IOException {
        Save save = new Save();
        Pattern p = Pattern.compile("<(.+)>(.+)</(.+)>");

        boolean reading;

        // Loading the camera settings for the development view
        reading = false;

        List<String> fileLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String str;

            while ((str = reader.readLine()) != null ) {
                fileLines.add(str);
            }
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
                        canvasModel.setMouseWheelMovesCreasePattern(selected);
                        break;
                    }
                    case "showPointRangeAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        canvasModel.setDisplayPointSpotlight(selected);
                        break;
                    }
                    case "pointOffsetAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        canvasModel.setDisplayPointOffset(selected);
                        break;
                    }
                    case "gridInputAssistAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        canvasModel.setDisplayGridInputAssist(selected);
                        break;
                    }
                    case "displayCommentsAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        canvasModel.setDisplayComments(selected);
                        break;
                    }
                    case "displayCpLinesAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        canvasModel.setDisplayCpLines(selected);
                        break;
                    }
                    case "displayAuxLinesAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        canvasModel.setDisplayAuxLines(selected);
                        break;
                    }
                    case "displayLiveAuxLinesAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        canvasModel.setDisplayLiveAuxLines(selected);
                        break;
                    }
                    case "displayStandardFaceMarksAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        canvasModel.setDisplayMarkings(selected);
                        break;
                    }
                    case "cpOnTopAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        canvasModel.setDisplayCreasePatternOnTop(selected);
                        break;
                    }
                    case "ckbox_oritatami_keika": {
                        boolean selected = Boolean.parseBoolean(value);
                        canvasModel.setDisplayFoldingProgress(selected);
                        break;
                    }
                    case "iTenkaizuSenhaba":
                        canvasModel.setLineWidth(Integer.parseInt(value));
                        break;
                    case "ir_ten":
                        canvasModel.setPointSize(Integer.parseInt(value));
                        break;
                    case "i_orisen_hyougen":
                        canvasModel.setLineStyle(LineStyle.from(value));
                        break;
                    case "i_anti_alias":
                        canvasModel.setAntiAlias(Boolean.parseBoolean(value));
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
                    case "changeGridStateAction":
                        gridModel.setBaseState(Grid.State.from(m.group(2)));
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
                        gridModel.setGridLineWidth(Integer.parseInt(m.group(2)));
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
                        gridYB = StringOp.String2double(m.group(2), gridModel.getGridYC());
                        break;
                    case "d_kousi_kakudo":
                        gridYC = StringOp.String2double(m.group(2), gridModel.getGridAngle());
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
            gridModel.setGridColor(new Color(i_grid_color_R, i_grid_color_G, i_grid_color_B));

            System.out.println("i_kousi_memori_color_R= " + i_grid_memori_color_R);
            System.out.println("i_kousi_memori_color_G= " + i_grid_memori_color_G);
            System.out.println("i_kousi_memori_color_B= " + i_grid_memori_color_B);
            gridModel.setGridScaleColor(new Color(i_grid_memori_color_R, i_grid_memori_color_G, i_grid_memori_color_B));
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
        }

        java.util.List<LineSegment> lineSegments = save.getLineSegments();
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
                number = Integer.parseInt(str);
            }
            if ((reading_flag == 1) && (str.equals("色"))) {
                str = tk.nextToken();
                ic = LineColor.from(str);
                LineSegment s0 = save.getLineSegments().get(number - 1);
                s0.setColor(ic);
            }

            if (reading_flag == 1) {
                String[] st_new = str_i.split(">", 2);// <-----------------------------------２つに分割するときは2を指定
                if (st_new[0].equals("<tpp")) {
                    String[] s_new = st_new[1].split("<", 2);
                    int i_customized = (Integer.parseInt(s_new[0]));
                    LineSegment s0 = lineSegments.get(number);
                    s0.setCustomized(i_customized);
                }

                if (st_new[0].equals("<tpp_color_R")) {
                    String[] s_new = st_new[1].split("<", 2);
                    i_customized_color_R = (Integer.parseInt(s_new[0]));
                    LineSegment s0 = lineSegments.get(number);
                    s0.setCustomizedColor(new Color(i_customized_color_R, i_customized_color_G, i_customized_color_B));
                }

                if (st_new[0].equals("<tpp_color_G")) {
                    String[] s_new = st_new[1].split("<", 2);
                    i_customized_color_G = (Integer.parseInt(s_new[0]));
                    LineSegment s0 = lineSegments.get(number);
                    s0.setCustomizedColor(new Color(i_customized_color_R, i_customized_color_G, i_customized_color_B));
                }
                if (st_new[0].equals("<tpp_color_B")) {
                    String[] s_new = st_new[1].split("<", 2);
                    i_customized_color_B = (Integer.parseInt(s_new[0]));
                    LineSegment s0 = lineSegments.get(number);
                    s0.setCustomizedColor(new Color(i_customized_color_R, i_customized_color_G, i_customized_color_B));
                }
            }

            if ((reading_flag == 1) && (str.equals("iactive"))) {//20181110追加
                str = tk.nextToken();
                is = LineSegment.ActiveState.valueOf(str);
                LineSegment s0 = lineSegments.get(number);
                s0.setActive(is);
            }

            if ((reading_flag == 1) && (str.equals("iva"))) {
                str = tk.nextToken();
                int iva = Integer.parseInt(str);
                LineSegment s0 = lineSegments.get(number);
                s0.setVoronoiA(iva);
            }

            if ((reading_flag == 1) && (str.equals("ivb"))) {
                str = tk.nextToken();
                int ivb = Integer.parseInt(str);
                LineSegment s0 = lineSegments.get(number);
                s0.setVoronoiB(ivb);
            }

            if ((reading_flag == 1) && (str.equals("選択"))) {
                str = tk.nextToken();
                int isel = Integer.parseInt(str);
                LineSegment s0 = lineSegments.get(number);
                s0.setSelected(isel);
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

                LineSegment s0 = lineSegments.get(number);
                s0.set(ax, ay, bx, by);
            }

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

    public static void exportFile(Save save, File file) {
        try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw); PrintWriter pw = new PrintWriter(bw)) {
            pw.println("<タイトル>");
            pw.println("タイトル," + save.getTitle());

            pw.println("<線分集合>");

            int index = 1;
            for (LineSegment s : save.getLineSegments()) {
                pw.println("番号," + index++);
                pw.println("色," + s.getColor());

                pw.println("<tpp>" + s.getCustomized() + "</tpp>");
                pw.println("<tpp_color_R>" + s.getCustomizedColor().getRed() + "</tpp_color_R>");
                pw.println("<tpp_color_G>" + s.getCustomizedColor().getGreen() + "</tpp_color_G>");
                pw.println("<tpp_color_B>" + s.getCustomizedColor().getBlue() + "</tpp_color_B>");

                pw.println("座標," + s.determineAX() + "," + s.determineAY() + "," + s.determineBX() + "," + s.determineBY());
            }

            pw.println("<円集合>");
            index = 1;
            for (Circle circle : save.getCircles()) {
                pw.println("番号," + index++);
                Circle e_temp = new Circle();
                e_temp.set(circle);
                pw.println("中心と半径と色," + e_temp.getX() + "," + e_temp.getY() + "," + e_temp.getRadius() + "," + e_temp.getColor());

                pw.println("<tpp>" + e_temp.getCustomized() + "</tpp>");
                pw.println("<tpp_color_R>" + e_temp.getCustomizedColor().getRed() + "</tpp_color_R>");
                pw.println("<tpp_color_G>" + e_temp.getCustomizedColor().getGreen() + "</tpp_color_G>");
                pw.println("<tpp_color_B>" + e_temp.getCustomizedColor().getBlue() + "</tpp_color_B>");
            }

            pw.println("<補助線分集合>");

            index = 1;
            for (LineSegment s : save.getAuxLineSegments()) {
                pw.println("補助番号," + index++);
                pw.println("補助色," + s.getColor());

                pw.println("<tpp>" + s.getCustomized() + "</tpp>");
                pw.println("<tpp_color_R>" + s.getCustomizedColor().getRed() + "</tpp_color_R>");
                pw.println("<tpp_color_G>" + s.getCustomizedColor().getGreen() + "</tpp_color_G>");
                pw.println("<tpp_color_B>" + s.getCustomizedColor().getBlue() + "</tpp_color_B>");

                pw.println("補助座標," + s.determineAX() + "," + s.determineAY() + "," + s.determineBX() + "," + s.determineBY());
            }

            Camera camera = save.getCreasePatternCamera() != null ? save.getCreasePatternCamera() : new Camera();
            pw.println("<camera_of_orisen_nyuuryokuzu>");
            pw.println("<camera_ichi_x>" + camera.getCameraPositionX() + "</camera_ichi_x>");
            pw.println("<camera_ichi_y>" + camera.getCameraPositionY() + "</camera_ichi_y>");
            pw.println("<camera_kakudo>" + camera.getCameraAngle() + "</camera_kakudo>");
            pw.println("<camera_kagami>" + camera.getCameraMirror() + "</camera_kagami>");
            pw.println("<camera_bairitsu_x>" + camera.getCameraZoomX() + "</camera_bairitsu_x>");
            pw.println("<camera_bairitsu_y>" + camera.getCameraZoomY() + "</camera_bairitsu_y>");
            pw.println("<hyouji_ichi_x>" + camera.getDisplayPositionX() + "</hyouji_ichi_x>");
            pw.println("<hyouji_ichi_y>" + camera.getDisplayPositionY() + "</hyouji_ichi_y>");
            pw.println("</camera_of_orisen_nyuuryokuzu>");

            pw.println("<settei>");
            CanvasModel canvasModel = save.getCanvasModel();
            pw.println("<ckbox_mouse_settei>" + canvasModel.getMouseWheelMovesCreasePattern() + "</ckbox_mouse_settei>");
            pw.println("<ckbox_ten_sagasi>" + canvasModel.getDisplayPointSpotlight() + "</ckbox_ten_sagasi>");
            pw.println("<ckbox_ten_hanasi>" + canvasModel.getDisplayPointOffset() + "</ckbox_ten_hanasi>");
            pw.println("<ckbox_kou_mitudo_nyuuryoku>" + canvasModel.getDisplayGridInputAssist() + "</ckbox_kou_mitudo_nyuuryoku>");
            pw.println("<ckbox_bun>" + canvasModel.getDisplayComments() + "</ckbox_bun>");
            pw.println("<ckbox_cp>" + canvasModel.getDisplayCpLines() + "</ckbox_cp>");
            pw.println("<ckbox_a0>" + canvasModel.getDisplayAuxLines() + "</ckbox_a0>");
            pw.println("<ckbox_a1>" + canvasModel.getDisplayLiveAuxLines() + "</ckbox_a1>");
            pw.println("<ckbox_mejirusi>" + canvasModel.getDisplayMarkings() + "</ckbox_mejirusi>");
            pw.println("<ckbox_cp_ue>" + canvasModel.getDisplayCreasePatternOnTop() + "</ckbox_cp_ue>");
            pw.println("<ckbox_oritatami_keika>" + canvasModel.getDisplayFoldingProgress() + "</ckbox_oritatami_keika>");
            //The thickness of the line in the development view.
            pw.println("<iTenkaizuSenhaba>" + canvasModel.getLineWidth() + "</iTenkaizuSenhaba>");
            //Width of vertex sign
            pw.println("<ir_ten>" + canvasModel.getPointSize() + "</ir_ten>");
            //Express the polygonal line expression with color
            pw.println("<i_orisen_hyougen>" + canvasModel.getLineStyle() + "</i_orisen_hyougen>");
            pw.println("<i_anti_alias>" + canvasModel.getAntiAlias() + "</i_anti_alias>");
            pw.println("</settei>");

            GridModel gridModel = save.getGridModel();
            pw.println("<Kousi>");
            pw.println("<i_kitei_jyoutai>" + gridModel.getBaseState() + "</i_kitei_jyoutai>");
            pw.println("<nyuuryoku_kitei>" + gridModel.getGridSize() + "</nyuuryoku_kitei>");

            pw.println("<memori_kankaku>" + gridModel.getIntervalGridSize() + "</memori_kankaku>");
            pw.println("<a_to_heikouna_memori_iti>" + gridModel.getHorizontalScalePosition() + "</a_to_heikouna_memori_iti>");
            pw.println("<b_to_heikouna_memori_iti>" + gridModel.getVerticalScalePosition() + "</b_to_heikouna_memori_iti>");
            pw.println("<kousi_senhaba>" + gridModel.getGridLineWidth() + "</kousi_senhaba>");

            pw.println("<d_kousi_x_a>" + gridModel.getGridXA() + "</d_kousi_x_a>");
            pw.println("<d_kousi_x_b>" + gridModel.getGridXB() + "</d_kousi_x_b>");
            pw.println("<d_kousi_x_c>" + gridModel.getGridXC() + "</d_kousi_x_c>");
            pw.println("<d_kousi_y_a>" + gridModel.getGridYA() + "</d_kousi_y_a>");
            pw.println("<d_kousi_y_b>" + gridModel.getGridYB() + "</d_kousi_y_b>");
            pw.println("<d_kousi_y_c>" + gridModel.getGridYC() + "</d_kousi_y_c>");
            pw.println("<d_kousi_kakudo>" + gridModel.getGridAngle() + "</d_kousi_kakudo>");
            pw.println("</Kousi>");

            pw.println("<Kousi_iro>");
            pw.println("<kousi_color_R>" + gridModel.getGridColor().getRed() + "</kousi_color_R>");
            pw.println("<kousi_color_G>" + gridModel.getGridColor().getGreen() + "</kousi_color_G>");
            pw.println("<kousi_color_B>" + gridModel.getGridColor().getBlue() + "</kousi_color_B>");

            pw.println("<kousi_memori_color_R>" + gridModel.getGridScaleColor().getRed() + "</kousi_memori_color_R>");
            pw.println("<kousi_memori_color_G>" + gridModel.getGridScaleColor().getGreen() + "</kousi_memori_color_G>");
            pw.println("<kousi_memori_color_B>" + gridModel.getGridScaleColor().getBlue() + "</kousi_memori_color_B>");
            pw.println("</Kousi_iro>");

            pw.println("<oriagarizu>");

            FoldedFigureModel foldedFigureModel = save.getFoldedFigureModel();
            pw.println("<oriagarizu_F_color_R>" + foldedFigureModel.getFrontColor().getRed() + "</oriagarizu_F_color_R>");
            pw.println("<oriagarizu_F_color_G>" + foldedFigureModel.getFrontColor().getGreen() + "</oriagarizu_F_color_G>");
            pw.println("<oriagarizu_F_color_B>" + foldedFigureModel.getFrontColor().getBlue() + "</oriagarizu_F_color_B>");

            pw.println("<oriagarizu_B_color_R>" + foldedFigureModel.getBackColor().getRed() + "</oriagarizu_B_color_R>");
            pw.println("<oriagarizu_B_color_G>" + foldedFigureModel.getBackColor().getGreen() + "</oriagarizu_B_color_G>");
            pw.println("<oriagarizu_B_color_B>" + foldedFigureModel.getBackColor().getBlue() + "</oriagarizu_B_color_B>");

            pw.println("<oriagarizu_L_color_R>" + foldedFigureModel.getLineColor().getRed() + "</oriagarizu_L_color_R>");
            pw.println("<oriagarizu_L_color_G>" + foldedFigureModel.getLineColor().getGreen() + "</oriagarizu_L_color_G>");
            pw.println("<oriagarizu_L_color_B>" + foldedFigureModel.getLineColor().getBlue() + "</oriagarizu_L_color_B>");

            pw.println("</oriagarizu>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
