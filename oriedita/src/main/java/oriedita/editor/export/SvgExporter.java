package oriedita.editor.export;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.LineStyle;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.save.Save;
import oriedita.editor.text.Text;
import oriedita.editor.tools.StringOp;
import oriedita.editor.export.api.FileExporter;
import origami.Epsilon;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.worker.FoldedFigure_Worker;
import origami.crease_pattern.worker.WireFrame_Worker;
import origami.folding.FoldedFigure;
import origami.folding.element.SubFace;
import origami.folding.util.SortingBox;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class SvgExporter implements FileExporter {
    private final CreasePattern_Worker mainCreasePatternWorker;
    private final FoldedFiguresList foldedFiguresList;

    @Inject
    public SvgExporter(
            @Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker,
            FoldedFiguresList foldedFiguresList
    ) {
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.foldedFiguresList = foldedFiguresList;
    }

    public static void exportFile(FoldLineSet foldLineSet, List<Text> texts, boolean showText, Camera camera, boolean i_cp_display, float fCreasePatternLineWidth, int lineWidth, LineStyle lineStyle, int pointSize, FoldedFiguresList foldedFigures, File file) throws IOException {
        try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw); PrintWriter pw = new PrintWriter(bw)) {
            Locale.setDefault(Locale.ENGLISH);
            pw.println("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">");

            if (i_cp_display) {
                pw.println("<g id=\"crease-pattern\">");
                exportSvgWithCamera(pw, foldLineSet, camera, fCreasePatternLineWidth, lineWidth, lineStyle, pointSize);
                pw.println("</g>");
            }

            if (showText) {
                pw.println("<g id=\"text\">");
                exportSvgTextWithCamera(pw, texts, camera);
                pw.println("</g>");
            }

            for (int i_oz = 0; i_oz < foldedFigures.getSize(); i_oz++) {
                pw.println("<g id=\"folded-figure-" + i_oz + "\">");
                exportSvgFoldedFigure(pw, foldedFigures.getElementAt(i_oz));
                pw.println("</g>");
            }

            pw.println("</svg>");
        }
    }

    private static void exportSvgTextWithCamera(PrintWriter pw, List<Text> texts, Camera camera) {
        for (Text text : texts) {
            Point p = camera.object2TV(text.getPos());
            DecimalFormat format = new DecimalFormat("#.#");
            String x = format.format(p.getX());
            String y = format.format(p.getY());
            double yLine = p.getY();
            pw.printf("<text style=\"font-family:sans-serif,Arial,Segoe UI;font-size:12px;\" x=\"%s\" y=\"%s\" fill=\"black\">", x, y);
            for (String s : text.getText().split("\n")) {
                String yLineString = format.format(yLine);
                pw.printf("<tspan x=\"%s\" y=\"%s\" fill=\"black\">%s</tspan>", x, yLineString, s);
                yLine += 16;
            }
            pw.println("</text>");
        }
    }

    public static void getMemo_wirediagram_for_svg_export(PrintWriter pw, Camera camera, FoldedFigure_Drawer foldedFigure, boolean i_fill) {
        FoldedFigure_Worker ctworker = foldedFigure.getFoldedFigure().foldedFigure_worker;
        WireFrame_Worker orite = foldedFigure.getFoldedFigure().wireFrame_worker1;
        PointSet otta_Men_zu = foldedFigure.getFoldedFigure().wireFrame_worker2.get();

        boolean flipped = camera.determineIsCameraMirrored();

        String str_stroke;
        str_stroke = "black";
        String str_strokewidth;
        str_strokewidth = "1";
        String str_fill;

        SortingBox<Integer> nbox = ctworker.rating2();

        //面を描く準備

        //BigDecimalのコンストラクタの引数は浮動小数点数型と文字列型どちらもok。引数が浮動小数点数型は誤差が発生。正確な値を扱うためには、引数は文字列型で指定。

        for (int i_nbox = 1; i_nbox <= otta_Men_zu.getNumFaces(); i_nbox++) {
            int im;
            if (camera.getCameraMirror() == -1.0) {//カメラの鏡設定が-1(x軸の符号を反転)なら、折り上がり図は裏表示
                im = nbox.backwardsGetValue(i_nbox);
            } else {
                im = nbox.getValue(i_nbox);
            }

            pw.print("<path d=\"");

            pw.print("M ");
            Point t_ob = new Point(
                    otta_Men_zu.getPointX(otta_Men_zu.getPointId(im, 1)),
                    otta_Men_zu.getPointY(otta_Men_zu.getPointId(im, 1)));
            Point t_tv = camera.object2TV(t_ob);
            String b_t_tv_x = String.format("%.2f", t_tv.getX());
            String b_t_tv_y = String.format("%.2f", t_tv.getY());

            pw.print(b_t_tv_x);
            pw.print(" ");
            pw.print(b_t_tv_y);
            pw.print(" ");

            for (int i = 2; i <= otta_Men_zu.getPointsCount(im); i++) {
                pw.print("L ");
                t_ob = new Point(
                        otta_Men_zu.getPointX(otta_Men_zu.getPointId(im, i)),
                        otta_Men_zu.getPointY(otta_Men_zu.getPointId(im, i)));
                t_tv = camera.object2TV(t_ob);
                String b_t_tv_x_i = String.format("%.2f", t_tv.getX());
                String b_t_tv_y_i = String.format("%.2f", t_tv.getY());

                pw.print(b_t_tv_x_i);
                pw.print(" ");
                pw.print(b_t_tv_y_i);
                pw.print(" ");
            }

            pw.print("Z\" ");

            if (!i_fill) {
                str_fill = "none";
            } else {
                if (flipped) {
                    if (orite.getIFacePosition(im) % 2 == 1) {
                        str_fill = StringOp.toHtmlColor(foldedFigure.getFoldedFigureModel().getBackColor());
                    } else {
                        str_fill = StringOp.toHtmlColor(foldedFigure.getFoldedFigureModel().getFrontColor());
                    }
                } else {
                    if (orite.getIFacePosition(im) % 2 == 1) {
                        str_fill = StringOp.toHtmlColor(foldedFigure.getFoldedFigureModel().getFrontColor());
                    } else {
                        str_fill = StringOp.toHtmlColor(foldedFigure.getFoldedFigureModel().getBackColor());
                    }
                }
            }

            pw.println("style=\"" + "stroke:" + str_stroke + "\"" +
                    " stroke-width=\"" + str_strokewidth + "\"" +
                    " fill=\"" + str_fill + "\"" + " />"
            );
        }
    }


    public static void getMemo_for_svg_with_camera(PrintWriter pw, Camera camera, FoldedFigure_Drawer foldedFigure) {//折り上がり図(hyouji_flg==5)
        WireFrame_Worker orite = foldedFigure.getFoldedFigure().wireFrame_worker1;
        PointSet subFace_figure = foldedFigure.getFoldedFigure().wireFrame_worker3.get();
        boolean front_back = camera.determineIsCameraMirrored();

        String str_stroke = "black";
        String str_strokewidth = "1";

        int SubFaceTotal = subFace_figure.getNumFaces();
        SubFace[] s0 = foldedFigure.getFoldedFigure().foldedFigure_worker.s0;

        //面を描く-----------------------------------------------------------------------------------------------------
        String[] x = new String[100];
        String[] y = new String[100];

        //SubFaceの.set_Menid2uekara_kazoeta_itiは現在の上下表をもとに、上から数えてi番めの面のid番号を全ての順番につき格納する。
        for (int im = 1; im <= SubFaceTotal; im++) { //SubFaceから上からの指定した番目の面のidを求める。
            s0[im].set_FaceId2fromTop_counted_position(foldedFigure.getFoldedFigure().foldedFigure_worker.hierarchyList);//s0[]はSubFace_zuから得られるSubFaceそのもの、jgは上下表Jyougehyouのこと
        }
        //ここまでで、上下表の情報がSubFaceの各面に入った

        //面を描く
        int face_order;
        for (int im = 1; im <= SubFaceTotal; im++) {//imは各SubFaceの番号
            if (s0[im].getFaceIdCount() > 0) {//MenidsuuはSubFace(折り畳み推定してえられた針金図を細分割した面)で重なっているMen(折りたたむ前の展開図の面)の数。これが0なら、ドーナツ状の穴の面なので描画対象外

                //Determine the color of the imth SubFace when drawing a fold-up diagram
                face_order = 1;
                if (front_back) {
                    face_order = s0[im].getFaceIdCount();
                }


                if (orite.getIFacePosition(s0[im].fromTop_count_FaceId(face_order)) % 2 == 1) {
                    str_stroke = StringOp.toHtmlColor(foldedFigure.getFoldedFigureModel().getFrontColor());
                }//g.setColor(F_color)
                if (orite.getIFacePosition(s0[im].fromTop_count_FaceId(face_order)) % 2 == 0) {
                    str_stroke = StringOp.toHtmlColor(foldedFigure.getFoldedFigureModel().getBackColor());
                }//g.setColor(B_color)

                if (front_back) {
                    if (orite.getIFacePosition(s0[im].fromTop_count_FaceId(face_order)) % 2 == 0) {
                        str_stroke = StringOp.toHtmlColor(foldedFigure.getFoldedFigureModel().getFrontColor());
                    }//g.setColor(F_color)
                    if (orite.getIFacePosition(s0[im].fromTop_count_FaceId(face_order)) % 2 == 1) {
                        str_stroke = StringOp.toHtmlColor(foldedFigure.getFoldedFigureModel().getBackColor());
                    }//g.setColor(B_color)
                }

                //折り上がり図を描くときのSubFaceの色を決めるのはここまで

                //折り上がり図を描くときのim番目のSubFaceの多角形の頂点の座標（PC表示上）を求める
                Point t0;
                Point t1;
                for (int i = 1; i <= subFace_figure.getPointsCount(im) - 1; i++) {
                    t0 = new Point(
                            subFace_figure.getPointX(subFace_figure.getPointId(im, i)),
                            subFace_figure.getPointY(subFace_figure.getPointId(im, i)));
                    t1 = camera.object2TV(t0);
                    x[i] = String.format("%.2f", t1.getX());
                    y[i] = String.format("%.2f", t1.getY());
                }

                t0 = new Point(
                        subFace_figure.getPointX(subFace_figure.getPointId(im, subFace_figure.getPointsCount(im))),
                        subFace_figure.getPointY(subFace_figure.getPointId(im, subFace_figure.getPointsCount(im))));
                t1 = camera.object2TV(t0);
                x[0] = String.format("%.2f", t1.getX());
                y[0] = String.format("%.2f", t1.getY());
                //折り上がり図を描くときのim番目のSubFaceの多角形の頂点の座標（PC表示上）を求めるのはここまで

                StringBuilder str_zahyou = new StringBuilder(x[0] + "," + y[0]);
                for (int i = 1; i <= subFace_figure.getPointsCount(im) - 1; i++) {
                    str_zahyou.append(" ").append(x[i]).append(",").append(y[i]);
                }

                pw.println("<polygon points=\"" + str_zahyou + "\"" +
                        " style=\"" + "stroke:" + str_stroke + ";fill:" + str_stroke + "\"" +
                        " stroke-width=\"" + str_strokewidth + "\"" + " />"
                );
            }
        }
        //面を描く　ここまで-----------------------------------------------------------------------------------------


        //棒を描く-----------------------------------------------------------------------------------------

        str_stroke = StringOp.toHtmlColor(foldedFigure.getFoldedFigureModel().getLineColor());

        for (int ib = 1; ib <= subFace_figure.getNumLines(); ib++) {
            int faceId_min, faceId_max; //棒の両側のSubFaceの番号の小さいほうがMid_min,　大きいほうがMid_max
            int faceOrderMin, faceOrderMax;//PC画面に表示したときSubFace(faceId_min) で見える面の番号がMen_jyunban_min、SubFace(faceId_max) で見える面の番号がMen_jyunban_max
            boolean drawing_flg;

            drawing_flg = false;
            faceId_min = subFace_figure.lineInFaceBorder_min_lookup(ib);//棒ibを境界として含む面(最大で2面ある)のうちでMenidの小さいほうのMenidを返す。棒を境界として含む面が無い場合は0を返す
            faceId_max = subFace_figure.lineInFaceBorder_max_lookup(ib);

            if (s0[faceId_min].getFaceIdCount() == 0) {
                drawing_flg = true;
            }//menをもたない、ドーナツの穴状のSubFaceは境界の棒を描く
            else if (s0[faceId_max].getFaceIdCount() == 0) {
                drawing_flg = true;
            } else if (faceId_min == faceId_max) {
                drawing_flg = true;
            }//一本の棒の片面だけにSubFace有り
            else {
                faceOrderMin = 1;
                if (front_back) {
                    faceOrderMin = s0[faceId_min].getFaceIdCount();
                }
                faceOrderMax = 1;
                if (front_back) {
                    faceOrderMax = s0[faceId_max].getFaceIdCount();
                }
                if (s0[faceId_min].fromTop_count_FaceId(faceOrderMin) != s0[faceId_max].fromTop_count_FaceId(faceOrderMax)) {
                    drawing_flg = true;
                }//この棒で隣接するSubFaceの1番上の面は異なるので、この棒は描く。
            }

            if (drawing_flg) {//棒を描く。
                LineSegment s_ob = new LineSegment(subFace_figure.getBeginX(ib), subFace_figure.getBeginY(ib), subFace_figure.getEndX(ib), subFace_figure.getEndY(ib));
                LineSegment s_tv = camera.object2TV(s_ob);

                Point a = s_tv.getA();
                Point b = s_tv.getB();

                String b_ax = String.format("%.2f", a.getX());
                String b_ay = String.format("%.2f", a.getY());
                String b_bx = String.format("%.2f", b.getX());
                String b_by = String.format("%.2f", b.getY());

                pw.println("<line x1=\"" + b_ax + "\"" +
                        " y1=\"" + b_ay + "\"" +
                        " x2=\"" + b_bx + "\"" +
                        " y2=\"" + b_by + "\"" +
                        " style=\"" + "stroke:" + str_stroke + "\"" +
                        " stroke-width=\"" + str_strokewidth + "\"" + " />"
                );
            }
        }
    }


    public static void exportSvgFoldedFigure(PrintWriter pw, FoldedFigure_Drawer foldedFigure) {
        //Wire diagram svg
        if (foldedFigure.getFoldedFigure().displayStyle == FoldedFigure.DisplayStyle.WIRE_2) {
            getMemo_wirediagram_for_svg_export(pw, foldedFigure.getFoldedFigureFrontCamera(), foldedFigure, false);//If the fourth integer is 0, only the frame of the face is painted, and if it is 1, the face is painted.
        }

        //Folded figure (table) svg
        if (((foldedFigure.getFoldedFigure().ip4 == FoldedFigure.State.FRONT_0) || (foldedFigure.getFoldedFigure().ip4 == FoldedFigure.State.BOTH_2)) || (foldedFigure.getFoldedFigure().ip4 == FoldedFigure.State.TRANSPARENT_3)) {
            //透過図のsvg
            if (foldedFigure.getFoldedFigure().displayStyle == FoldedFigure.DisplayStyle.TRANSPARENT_3) {        // displayStyle;折り上がり図の表示様式の指定。１なら実際に折り紙を折った場合と同じ。２なら透過図。3なら針金図。
                getMemo_wirediagram_for_svg_export(pw, foldedFigure.getFoldedFigureFrontCamera(), foldedFigure, true);
            }

            //折り上がり図のsvg*************
            if (foldedFigure.getFoldedFigure().displayStyle == FoldedFigure.DisplayStyle.PAPER_5) {
                getMemo_for_svg_with_camera(pw, foldedFigure.getFoldedFigureFrontCamera(), foldedFigure);// displayStyle;折り上がり図の表示様式の指定。5なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
            }
        }

        //折りあがり図（裏）のsvg
        if (((foldedFigure.getFoldedFigure().ip4 == FoldedFigure.State.BACK_1) || (foldedFigure.getFoldedFigure().ip4 == FoldedFigure.State.BOTH_2)) || (foldedFigure.getFoldedFigure().ip4 == FoldedFigure.State.TRANSPARENT_3)) {
            //透過図のsvg
            if (foldedFigure.getFoldedFigure().displayStyle == FoldedFigure.DisplayStyle.TRANSPARENT_3) {        // displayStyle;折り上がり図の表示様式の指定。１なら実際に折り紙を折った場合と同じ。２なら透過図。3なら針金図。
                getMemo_wirediagram_for_svg_export(pw, foldedFigure.getFoldedFigureRearCamera(), foldedFigure, true);
            }

            //折り上がり図のsvg*************
            if (foldedFigure.getFoldedFigure().displayStyle == FoldedFigure.DisplayStyle.PAPER_5) {
                getMemo_for_svg_with_camera(pw, foldedFigure.getFoldedFigureRearCamera(), foldedFigure);// displayStyle;折り上がり図の表示様式の指定。5なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
            }
        }
    }

    public static void exportSvgWithCamera(PrintWriter pw, FoldLineSet foldLineSet, Camera camera, float fCreasePatternLineWidth, int lineWidth, LineStyle lineStyle, int pointSize) {//引数はカメラ設定、線幅、画面X幅、画面y高さ

        //Drawing of crease pattern Polygonal lines other than auxiliary live lines
        String str_stroke;
        String str_strokewidth = Integer.toString(lineWidth);
        for (var s : foldLineSet.getLineSegmentsIterator()) {
            LineColor color = s.getColor();
            str_stroke = getStrokeColor(color);
            if (str_stroke == null) continue;

            if (lineStyle == LineStyle.BLACK_TWO_DOT || lineStyle == LineStyle.BLACK_ONE_DOT) {
                str_stroke = "black";
            }

            if(lineStyle == LineStyle.BLACK_WHITE){
                switch(color){
                    case BLACK_0:
                    case RED_1:
                        str_stroke = "black";
                        break;
                    case BLUE_2:
                        str_stroke = "#A2A2A2";
                        break;
                    default:
                        break;
                }
            }

            String str_stroke_dasharray;
            switch (lineStyle) {
                case COLOR:
                case BLACK_WHITE:
                    str_stroke_dasharray = "";
                    break;
                case COLOR_AND_SHAPE:
                case BLACK_ONE_DOT:
                    //基本指定A　　線の太さや線の末端の形状
                    //dash_M1,一点鎖線
                    switch (color) {
                        case RED_1:
                            str_stroke_dasharray = "stroke-dasharray=\"10 3 3 3\"";
                            break;
                        case BLUE_2:
                            str_stroke_dasharray = "stroke-dasharray=\"8 8\"";
                            break;
                        default:
                            str_stroke_dasharray = "";
                            break;
                    }
                    break;
                case BLACK_TWO_DOT:
                    //基本指定A　　線の太さや線の末端の形状
                    //dash_M2,二点鎖線
                    switch (color) {
                        case RED_1:
                            str_stroke_dasharray = "stroke-dasharray=\"10 3 3 3 3 3\"";
                            break;
                        case BLUE_2:
                            str_stroke_dasharray = "stroke-dasharray=\"8 8\"";
                            break;
                        default:
                            str_stroke_dasharray = "";
                            break;
                    }
                    break;
                default:
                    throw new IllegalArgumentException();
            }

            LineSegment s_tv = camera.object2TV(s);
            Point a = s_tv.getA();
            Point b = s_tv.getB();

            BigDecimal b_ax = new BigDecimal(String.valueOf(a.getX()));
            double x1 = b_ax.setScale(2, RoundingMode.HALF_UP).doubleValue();
            BigDecimal b_ay = new BigDecimal(String.valueOf(a.getY()));
            double y1 = b_ay.setScale(2, RoundingMode.HALF_UP).doubleValue();
            BigDecimal b_bx = new BigDecimal(String.valueOf(b.getX()));
            double x2 = b_bx.setScale(2, RoundingMode.HALF_UP).doubleValue();
            BigDecimal b_by = new BigDecimal(String.valueOf(b.getY()));
            double y2 = b_by.setScale(2, RoundingMode.HALF_UP).doubleValue();

            pw.println("<line x1=\"" + x1 + "\"" +
                    " y1=\"" + y1 + "\"" +
                    " x2=\"" + x2 + "\"" +
                    " y2=\"" + y2 + "\"" +
                    " " + str_stroke_dasharray + " " +
                    " stroke=\"" + str_stroke + "\"" +
                    " stroke-width=\"" + str_strokewidth + "\"" + " />");


            drawVertex(pw, fCreasePatternLineWidth, pointSize, x1, y1);
            drawVertex(pw, fCreasePatternLineWidth, pointSize, x2, y2);
        }

        for (Circle c : foldLineSet.getCircles()) {
            LineColor color = c.getColor();
            str_stroke = getStrokeColor(color);
            if (c.getCustomized() == 1) {
                str_stroke = StringOp.toHtmlColor(c.getCustomizedColor());
            }
            if (str_stroke == null) continue;
            Circle c_tv = camera.object2TV(c);
            double x1 = c_tv.getX();
            double y1 = c_tv.getY();

            if (Epsilon.high.eq0(c.getR())) {
                // Draw a vertex
                drawVertex(pw, fCreasePatternLineWidth, pointSize, x1, y1);
            } else {
                // Draw a circle
                drawVertex(pw, fCreasePatternLineWidth, pointSize, x1, y1);
                pw.println("<circle style=\"fill:none;stroke:" + str_stroke + ";stroke-width:1\"" +
                        " r=\"" + c_tv.getR() + "\"" +
                        " cx=\"" + x1 + "\"" +
                        " cy=\"" + y1 + "\"" +
                        " />");
            }
        }
    }

    private static String getStrokeColor(LineColor color) {
        switch (color) {
            case BLACK_0:
                return "black";
            case RED_1:
                return "red";
            case BLUE_2:
                return "blue";
            case CYAN_3:
                return "#64c8c8";
            case YELLOW_7:
                return "yellow";
            case ORANGE_4:
                return "orange";
            default:
                return null;
        }
    }

    private static void drawVertex(PrintWriter pw, float fCreasePatternLineWidth, int pointSize, double x1, double y1) {
        if (pointSize != 0) {
            if (fCreasePatternLineWidth < 2.0f) {
                //Draw a black square at the vertex

                pw.println("<rect style=\"fill:#000000;stroke:none\"" +
                        " width=\"" + 2.0 * (double) pointSize + "\"" +
                        " height=\"" + 2.0 * (double) pointSize + "\"" +
                        " x=\"" + (x1 - (double) pointSize) + "\"" +
                        " y=\"" + (y1 - (double) pointSize) + "\"" +
                        " />");
            } else {
                //  Thick line
                double d_width = (double) fCreasePatternLineWidth / 2.0 + (double) pointSize;

                pw.println("<circle style=\"fill:#ffffff;stroke:#000000;stroke-width:1\"" +
                        " r=\"" + d_width + "\"" +
                        " cx=\"" + x1 + "\"" +
                        " cy=\"" + y1 + "\"" +
                        " />");
            }
        }
    }

    @Override
    public boolean supports(File file) {
        return false;
    }

    @Override
    public void doExport(Save save, File file) throws IOException {
        var applicationModel = save.getApplicationModel();
        boolean displayCpLines = applicationModel.getDisplayCpLines();
        float lineWidth = applicationModel.determineCalculatedLineWidth();
        int intLineWidth = applicationModel.getLineWidth();
        LineStyle lineStyle = applicationModel.getLineStyle();
        int pointSize = applicationModel.getPointSize();
        boolean showText = applicationModel.getDisplayComments();

        exportFile(mainCreasePatternWorker.getFoldLineSet(), save.getTexts(), showText, save.getCreasePatternCamera(), displayCpLines, lineWidth, intLineWidth, lineStyle, pointSize, foldedFiguresList, file);
    }

    @Override
    public String getName() {
        return "Scalable Vector Graphics";
    }

    @Override
    public String getExtension() {
        return ".svg";
    }
}
