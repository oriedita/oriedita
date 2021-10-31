package origami_editor.editor.export;

import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.worker.FoldedFigure_Worker;
import origami.crease_pattern.worker.WireFrame_Worker;
import origami.folding.element.Face;
import origami.folding.element.SubFace;
import origami_editor.editor.LineStyle;
import origami_editor.editor.folded_figure.FoldedFigure;
import origami_editor.sortingbox.SortingBox;
import origami_editor.tools.Camera;
import origami_editor.tools.StringOp;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class Svg {
    public static void exportFile(FoldLineSet foldLineSet, Camera camera, boolean i_cp_display, float fCreasePatternLineWidth, int lineWidth, LineStyle lineStyle, int pointSize, List<FoldedFigure> foldedFigures, File file) {
        try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw); PrintWriter pw = new PrintWriter(bw)) {
            System.out.println("svg画像出力");

            pw.println("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">");

            exportSvgWithCamera(pw, foldLineSet, camera, i_cp_display, fCreasePatternLineWidth, lineWidth, lineStyle, pointSize);

            for (int i_oz = 1; i_oz <= foldedFigures.size() - 1; i_oz++) {
                exportSvgFoldedFigure(pw, foldedFigures.get(i_oz));
            }

            pw.println("</svg>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getMemo_wirediagram_for_svg_export(PrintWriter pw, FoldedFigure_Worker ctworker, Camera camera, FoldedFigure foldedFigure, WireFrame_Worker orite, PointSet otta_Men_zu, boolean i_fill) {
        boolean flipped = camera.determineIsCameraMirrored();

        Point t_ob = new Point();
        Point t_tv = new Point();

        String str_stroke;
        str_stroke = "black";
        String str_strokewidth;
        str_strokewidth = "1";
        String str_fill;
        str_fill = "";

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

            StringBuilder text;//文字列処理用のクラスのインスタンス化

            text = new StringBuilder("M ");
            t_ob.setX(otta_Men_zu.getPointX(otta_Men_zu.getPointId(im, 1)));
            t_ob.setY(otta_Men_zu.getPointY(otta_Men_zu.getPointId(im, 1)));
            t_tv.set(camera.object2TV(t_ob));
            BigDecimal b_t_tv_x = new BigDecimal(String.valueOf(t_tv.getX()));
            BigDecimal b_t_tv_y = new BigDecimal(String.valueOf(t_tv.getY()));

            text.append(b_t_tv_x.setScale(2, RoundingMode.HALF_UP).doubleValue()).append(" ").append(b_t_tv_y.setScale(2, RoundingMode.HALF_UP).doubleValue()).append(" ");


            for (int i = 2; i <= otta_Men_zu.getPointsCount(im); i++) {
                text.append("L ");
                t_ob.setX(otta_Men_zu.getPointX(otta_Men_zu.getPointId(im, i)));
                t_ob.setY(otta_Men_zu.getPointY(otta_Men_zu.getPointId(im, i)));
                t_tv.set(camera.object2TV(t_ob));
                BigDecimal b_t_tv_x_i = new BigDecimal(String.valueOf(t_tv.getX()));
                BigDecimal b_t_tv_y_i = new BigDecimal(String.valueOf(t_tv.getY()));

                text.append(b_t_tv_x_i.setScale(2, RoundingMode.HALF_UP).doubleValue()).append(" ").append(b_t_tv_y_i.setScale(2, RoundingMode.HALF_UP).doubleValue()).append(" ");
            }

            text.append("Z");

            if (!i_fill) {
                str_fill = "none";

            } else {

                if (orite.getIFacePosition(im) % 2 == 1) {
                    str_fill = StringOp.toHtmlColor(foldedFigure.foldedFigureModel.getFrontColor());
                }
                if (orite.getIFacePosition(im) % 2 == 0) {
                    str_fill = StringOp.toHtmlColor(foldedFigure.foldedFigureModel.getBackColor());
                }

                if (flipped) {
                    if (orite.getIFacePosition(im) % 2 == 1) {
                        str_fill = StringOp.toHtmlColor(foldedFigure.foldedFigureModel.getBackColor());
                    }
                    if (orite.getIFacePosition(im) % 2 == 0) {
                        str_fill = StringOp.toHtmlColor(foldedFigure.foldedFigureModel.getFrontColor());
                    }
                }
            }

            pw.println("<path d=\"" + text + "\"" +
                    " style=\"" + "stroke:" + str_stroke + "\"" +
                    " stroke-width=\"" + str_strokewidth + "\"" +
                    " fill=\"" + str_fill + "\"" + " />"
            );
        }
    }

    private static boolean isBottomMostDrawnFace(SubFace[] s0, int SubFaceTotal, int fis, List<Integer> doneFaceId) {
        for (int im2 = 1; im2 <= SubFaceTotal; im2++) {
            if (s0[im2].getFaceIdCount() <= 0) continue;

            SubFace otherSubFace = s0[im2];

            // Other face contains this face id
            if (otherSubFace.FaceIdIndex(fis) > 0) {
                // Find the lowest face in this subface.
                int index = otherSubFace.getFaceIdCount();
                int otherFis;
                do {
                    otherFis = otherSubFace.fromTop_count_FaceId(index--);
                } while (doneFaceId.contains(otherFis) && index > 0);

                // There exists a face which is lower than this one.
                if (otherFis > 0 && fis != otherFis) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean isFaceVisible(SubFace[] s0, int SubFaceTotal, int fis) {
        for (int im2 = 1; im2 <= SubFaceTotal; im2++) {
            if (s0[im2].getFaceIdCount() <= 0) continue;

            SubFace otherSubFace = s0[im2];

            // Other face contains this face id
            if (otherSubFace.FaceIdIndex(fis) > 0) {
                int otherFace = otherSubFace.fromTop_count_FaceId(1);
                if (otherFace == fis) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void getMemo_for_svg_with_camera(PrintWriter pw, Camera camera, FoldedFigure foldedFigure, WireFrame_Worker orite, PointSet subFace_figure) {//折り上がり図(hyouji_flg==5)
        boolean front_back = camera.determineIsCameraMirrored();

        Point t0 = new Point();
        Point t1 = new Point();

        pw.println("<style type=\"text/css\">.f{stroke:" + StringOp.toHtmlColor(foldedFigure.foldedFigureModel.getLineColor()) + ";stroke-linejoin:bevel;}</style>");

        StringBuilder str_zahyou;
        String str_stroke = "black";

        int SubFaceTotal = subFace_figure.getNumFaces();
        SubFace[] s0 = foldedFigure.ct_worker.s0;

        //面を描く-----------------------------------------------------------------------------------------------------
        String[] x = new String[100];
        String[] y = new String[100];

        //SubFaceの.set_Menid2uekara_kazoeta_itiは現在の上下表をもとに、上から数えてi番めの面のid番号を全ての順番につき格納する。
        for (int im = 1; im <= SubFaceTotal; im++) { //SubFaceから上からの指定した番目の面のidを求める。
            s0[im].set_FaceId2fromTop_counted_position(foldedFigure.ct_worker.hierarchyList);//s0[]はSubFace_zuから得られるSubFaceそのもの、jgは上下表Jyougehyouのこと
        }
        //ここまでで、上下表の情報がSubFaceの各面に入った

        PointSet faceFigure = foldedFigure.cp_worker2.get();

        List<Integer> doneFaceId = new ArrayList<>();

        int fis = 0;
        while (doneFaceId.size() < faceFigure.getNumFaces()) {
            fis++;
            if (fis == faceFigure.getNumFaces() + 1) {
                fis = 1;
            }
            if (doneFaceId.contains(fis)) continue;

            // Find if this face is the lowest
            if (!isBottomMostDrawnFace(s0, SubFaceTotal, fis, doneFaceId)) continue;

            // Do not export invisible faces
            if (!isFaceVisible(s0, SubFaceTotal, fis)) {
                doneFaceId.add(fis);
                continue;
            }

            doneFaceId.add(fis);

            Face face = faceFigure.getFace(fis);

            int iFacePosition = orite.getIFacePosition(fis);
            if (iFacePosition % 2 == 1) {
                str_stroke = StringOp.toHtmlColor(foldedFigure.foldedFigureModel.getFrontColor());
            }//g.setColor(F_color)
            if (iFacePosition % 2 == 0) {
                str_stroke = StringOp.toHtmlColor(foldedFigure.foldedFigureModel.getBackColor());
            }//g.setColor(B_color)

            if (front_back) {
                if (iFacePosition % 2 == 0) {
                    str_stroke = StringOp.toHtmlColor(foldedFigure.foldedFigureModel.getFrontColor());
                }//g.setColor(F_color)
                if (iFacePosition % 2 == 1) {
                    str_stroke = StringOp.toHtmlColor(foldedFigure.foldedFigureModel.getBackColor());
                }//g.setColor(B_color)
            }

            //折り上がり図を描くときのSubFaceの色を決めるのはここまで

            //折り上がり図を描くときのim番目のSubFaceの多角形の頂点の座標（PC表示上）を求める
            for (int i = 1; i <= face.getNumPoints() - 1; i++) {
                t0.setX(faceFigure.getPointX(faceFigure.getPointId(fis, i)));
                t0.setY(faceFigure.getPointY(faceFigure.getPointId(fis, i)));
                t1.set(camera.object2TV(t0));
                x[i] = String.format("%.4f", t1.getX());
                y[i] = String.format("%.4f", t1.getY());
            }

            t0.setX(faceFigure.getPointX(faceFigure.getPointId(fis, face.getNumPoints())));
            t0.setY(faceFigure.getPointY(faceFigure.getPointId(fis, face.getNumPoints())));
            t1.set(camera.object2TV(t0));
            x[0] = String.format("%.4f", t1.getX());
            y[0] = String.format("%.4f", t1.getY());
            //折り上がり図を描くときのim番目のSubFaceの多角形の頂点の座標（PC表示上）を求めるのはここまで

            str_zahyou = new StringBuilder("M" + x[0] + " " + y[0]);
            for (int i = 1; i <= faceFigure.getPointsCount(fis) - 1; i++) {
                str_zahyou.append("L").append(x[i]).append(" ").append(y[i]).append(" ");
            }

            pw.println("<path class=\"f\" d=\"" + str_zahyou.append("Z") + "\"" +
                    " style=\"fill:" + str_stroke + "\"" +
                    " stroke-width=\"1\" />"
            );
        }
    }


    public static void exportSvgFoldedFigure(PrintWriter pw, FoldedFigure foldedFigure) {
        //Wire diagram svg
        if (foldedFigure.displayStyle == FoldedFigure.DisplayStyle.WIRE_2) {
            getMemo_wirediagram_for_svg_export(pw, foldedFigure.ct_worker, foldedFigure.foldedFigureFrontCamera, foldedFigure, foldedFigure.cp_worker1, foldedFigure.cp_worker2.get(), false);//If the fourth integer is 0, only the frame of the face is painted, and if it is 1, the face is painted.
        }

        //Folded figure (table) svg
        if (((foldedFigure.ip4 == FoldedFigure.State.FRONT_0) || (foldedFigure.ip4 == FoldedFigure.State.BOTH_2)) || (foldedFigure.ip4 == FoldedFigure.State.TRANSPARENT_3)) {
            //透過図のsvg
            if (foldedFigure.displayStyle == FoldedFigure.DisplayStyle.TRANSPARENT_3) {        // displayStyle;折り上がり図の表示様式の指定。１なら実際に折り紙を折った場合と同じ。２なら透過図。3なら針金図。
                getMemo_wirediagram_for_svg_export(pw, foldedFigure.ct_worker, foldedFigure.foldedFigureFrontCamera, foldedFigure, foldedFigure.cp_worker1, foldedFigure.cp_worker2.get(), true);
            }

            //折り上がり図のsvg*************
            if (foldedFigure.displayStyle == FoldedFigure.DisplayStyle.PAPER_5) {
                getMemo_for_svg_with_camera(pw, foldedFigure.foldedFigureFrontCamera, foldedFigure, foldedFigure.cp_worker1, foldedFigure.cp_worker3.get());// displayStyle;折り上がり図の表示様式の指定。5なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
            }
        }

        //折りあがり図（裏）のsvg
        if (((foldedFigure.ip4 == FoldedFigure.State.BACK_1) || (foldedFigure.ip4 == FoldedFigure.State.BOTH_2)) || (foldedFigure.ip4 == FoldedFigure.State.TRANSPARENT_3)) {
            //透過図のsvg
            if (foldedFigure.displayStyle == FoldedFigure.DisplayStyle.TRANSPARENT_3) {        // displayStyle;折り上がり図の表示様式の指定。１なら実際に折り紙を折った場合と同じ。２なら透過図。3なら針金図。
                getMemo_wirediagram_for_svg_export(pw, foldedFigure.ct_worker, foldedFigure.foldedFigureRearCamera, foldedFigure, foldedFigure.cp_worker1, foldedFigure.cp_worker2.get(), true);
            }

            //折り上がり図のsvg*************
            if (foldedFigure.displayStyle == FoldedFigure.DisplayStyle.PAPER_5) {
                getMemo_for_svg_with_camera(pw, foldedFigure.foldedFigureRearCamera, foldedFigure, foldedFigure.cp_worker1, foldedFigure.cp_worker3.get());// displayStyle;折り上がり図の表示様式の指定。5なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
            }
        }
    }

    public static void exportSvgWithCamera(PrintWriter pw, FoldLineSet foldLineSet, Camera camera, boolean i_cp_display, float fCreasePatternLineWidth, int lineWidth, LineStyle lineStyle, int pointSize) {//引数はカメラ設定、線幅、画面X幅、画面y高さ
        LineSegment s_tv = new LineSegment();
        Point a = new Point();
        Point b = new Point();

        String str_stroke;
        String str_strokewidth = Integer.toString(lineWidth);

        //Drawing of crease pattern Polygonal lines other than auxiliary live lines
        if (i_cp_display) {
            for (int i = 1; i <= foldLineSet.getTotal(); i++) {
                LineSegment s = foldLineSet.get(i);
                LineColor color = s.getColor();
                switch (color) {
                    case BLACK_0:
                        str_stroke = "black";
                        break;
                    case RED_1:
                        str_stroke = "red";
                        break;
                    case BLUE_2:
                        str_stroke = "blue";
                        break;
                    case CYAN_3:
                        str_stroke = "#64c8c8";
                        break;
                    default:
                        continue;
                }

                if (lineStyle == LineStyle.BLACK_TWO_DOT || lineStyle == LineStyle.BLACK_ONE_DOT) {
                    str_stroke = "black";
                }

                String str_stroke_dasharray;
                switch (lineStyle) {
                    case COLOR:
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

                s_tv.set(camera.object2TV(s));
                a.set(s_tv.getA());
                b.set(s_tv.getB());

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

                if (pointSize != 0) {
                    if (fCreasePatternLineWidth < 2.0f) {
                        //Draw a black square at the vertex

                        pw.println("<rect style=\"fill:#000000;stroke:none\"" +
                                " width=\"" + 2.0 * (double) pointSize + "\"" +
                                " height=\"" + 2.0 * (double) pointSize + "\"" +
                                " x=\"" + (x1 - (double) pointSize) + "\"" +
                                " y=\"" + (y1 - (double) pointSize) + "\"" +
                                " />");

                        pw.println("<rect style=\"fill:#000000;stroke:none\"" +
                                " width=\"" + 2.0 * (double) pointSize + "\"" +
                                " height=\"" + 2.0 * (double) pointSize + "\"" +
                                " x=\"" + (x2 - (double) pointSize) + "\"" +
                                " y=\"" + (y2 - (double) pointSize) + "\"" +
                                " />");
                    } else {
                        //  Thick line
                        double d_width = (double) fCreasePatternLineWidth / 2.0 + (double) pointSize;

                        pw.println("<circle style=\"fill:#ffffff;stroke:#000000;stroke-width:1\"" +
                                " r=\"" + d_width + "\"" +
                                " cx=\"" + x1 + "\"" +
                                " cy=\"" + y1 + "\"" +
                                " />");

                        pw.println("<circle style=\"fill:#ffffff;stroke:#000000;stroke-width:1\"" +
                                " r=\"" + d_width + "\"" +
                                " cx=\"" + x2 + "\"" +
                                " cy=\"" + y2 + "\"" +
                                " />");
                    }
                }
            }
        }
    }
}
