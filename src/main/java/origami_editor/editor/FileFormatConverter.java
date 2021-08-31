package origami_editor.editor;

import origami_editor.graphic2d.point.Point;
import origami_editor.record.memo.Memo;
import origami_editor.tools.line.Line;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class FileFormatConverter {

    static Memo obj2orihime(Memo mem) {
        System.out.println("objファイルをオリヒメ用にする");
        Memo MemR = new Memo();
        int jtok;

        ArrayList<Point> tL = new ArrayList<>();

        tL.add(new Point());

        Point tn;
        int Tenmax = 0;

        ArrayList<Line> lineList = new ArrayList<>();
        lineList.add(new Line());

        Line line;
        int Boumax = 0;

        ArrayList<Integer> itempL = new ArrayList<>();
        itempL.add(0);

        int ia;
        int ib;
        LineColor ic;
        int id;
        double d1, d2, d3, d4;

        double xmax = -10000.0;
        double xmin = 10000.0;
        double ymax = -10000.0;
        double ymin = 10000.0;

        String str = "";

        for (int ig = 1; ig <= mem.getLineCount(); ig++) {
            if (mem.getLine(ig).length() != 0) {
                StringTokenizer tk = new StringTokenizer(mem.getLine(ig), " ");
                jtok = tk.countTokens();

                str = tk.nextToken();

                if (str.equals("v")) {
                    d1 = Double.parseDouble(tk.nextToken());
                    d2 = Double.parseDouble(tk.nextToken());
                    d3 = Double.parseDouble(tk.nextToken());

                    if (d1 > xmax) {
                        xmax = d1;
                    }
                    if (d1 < xmin) {
                        xmin = d1;
                    }
                    if (d2 > ymax) {
                        ymax = d2;
                    }
                    if (d2 < ymin) {
                        ymin = d2;
                    }

                    Tenmax = Tenmax + 1;
                    tL.add(new Point(d1, d2));
                }
                if (str.equals("f")) {
                    itempL.clear();
                    itempL.add(0);
                    for (int i = 2; i <= jtok; i++) {
                        int ite = Integer.parseInt(tk.nextToken());
                        itempL.add(ite);
                    }
                    itempL.set(0, itempL.get(jtok - 1));
                    for (int i = 1; i <= jtok - 1; i++) {
                        int iflg = 0;
                        Integer I_itempL = itempL.get(i);
                        Integer Im1_itempL = itempL.get(i - 1);
                        for (int j = 1; j <= Boumax; j++) {
                            line = lineList.get(j);
                            if ((line.getBegin() == Im1_itempL) && (line.getEnd() == I_itempL)) {
                                iflg = iflg + 1;
                            }
                            if ((line.getBegin() == I_itempL) && (line.getEnd() == Im1_itempL)) {
                                iflg = iflg + 1;
                            }
                        }
                        if (iflg == 0) {
                            Boumax = Boumax + 1;
                            lineList.add(new Line(Im1_itempL, I_itempL, LineColor.BLACK_0));
                        }
                    }
                }

                if (str.equals("#e")) {
                    ia = Integer.parseInt(tk.nextToken());
                    ib = Integer.parseInt(tk.nextToken());
                    ic = LineColor.from(tk.nextToken());
                    id = Integer.parseInt(tk.nextToken());
                    for (int i = 1; i <= Boumax; i++) {
                        line = lineList.get(i);
                        if ((line.getBegin() == ia) && (line.getEnd() == ib)) {
                            line.setColor(ic);
                        }
                        if ((line.getBegin() == ib) && (line.getEnd() == ia)) {
                            line.setColor(ic);
                        }
                    }
                }
            }
        }

        MemR.reset();
        MemR.addLine("<線分集合>");
        for (int i = 1; i <= Boumax; i++) {
            MemR.addLine("番号," + i);
            line = lineList.get(i);

            LineColor icol;
            icol = LineColor.fromNumber(line.getColor().getNumber() - 1);
            line.setColor(icol);
            if (line.getColor() == LineColor.RED_1) {
                icol = LineColor.BLUE_2;
            }
            if (line.getColor() == LineColor.BLUE_2) {
                icol = LineColor.RED_1;
            }

            if (icol != LineColor.BLACK_0) {
                line.setColor(icol);
            }

            MemR.addLine("色," + icol);

            tn = tL.get(line.getBegin());
            d1 = tn.getX();
            d2 = tn.getY();

            tn = tL.get(line.getEnd());
            d3 = tn.getX();
            d4 = tn.getY();

            MemR.addLine("座標," + d1 + "," + d2 + "," + d3 + "," + d4);
        }
        return MemR;
    }

    static Memo cp2orihime(Memo mem) {
        System.out.println("cpファイルをオリヒメ用にする");
        System.out.println("cpファイルをオリヒメ用にする");
        Memo MemR = new Memo();

        int ibangou = 0;
        double d1, d2, d3, d4;
        String str;
        int icol;

        //オリヒメ　0.Contour, 1.Mountain, 2.Valley　、ORIPA 1.Contour, 2.Mountain, 3.Valley

        MemR.reset();
        MemR.addLine("<線分集合>");

        for (int ig = 1; ig <= mem.getLineCount(); ig++) {
            if (mem.getLine(ig).length() != 0) {
                StringTokenizer tk = new StringTokenizer(mem.getLine(ig), " ");
                str = tk.nextToken();
                if (str.equals("1")) {
                    icol = 0;
                    ibangou = ibangou + 1;
                    d1 = Double.parseDouble(tk.nextToken());
                    d2 = Double.parseDouble(tk.nextToken());
                    d3 = Double.parseDouble(tk.nextToken());
                    d4 = Double.parseDouble(tk.nextToken());
                    MemR.addLine("番号," + ibangou);
                    MemR.addLine("色," + icol);

                    //d1=d1+350.0;d2=d2+350.0;d3=d3+350.0;d4=d4+350.0;//ORIPAからオリヒメへ移すときの座標調整

                    MemR.addLine("座標," + d1 + "," + d2 + "," + d3 + "," + d4);
                }
                if (str.equals("2")) {
                    icol = 1;
                    ibangou = ibangou + 1;
                    d1 = Double.parseDouble(tk.nextToken());
                    d2 = Double.parseDouble(tk.nextToken());
                    d3 = Double.parseDouble(tk.nextToken());
                    d4 = Double.parseDouble(tk.nextToken());
                    MemR.addLine("番号," + ibangou);
                    MemR.addLine("色," + icol);

                    //d1=d1+350.0;d2=d2+350.0;d3=d3+350.0;d4=d4+350.0;//ORIPAからオリヒメへ移すときの座標調整

                    MemR.addLine("座標," + d1 + "," + d2 + "," + d3 + "," + d4);
                }
                if (str.equals("3")) {
                    icol = 2;
                    ibangou = ibangou + 1;
                    d1 = Double.parseDouble(tk.nextToken());
                    d2 = Double.parseDouble(tk.nextToken());
                    d3 = Double.parseDouble(tk.nextToken());
                    d4 = Double.parseDouble(tk.nextToken());
                    MemR.addLine("番号," + ibangou);
                    MemR.addLine("色," + icol);

                    //d1=d1+350.0;d2=d2+350.0;d3=d3+350.0;d4=d4+350.0;//ORIPAからオリヒメへ移すときの座標調整

                    MemR.addLine("座標," + d1 + "," + d2 + "," + d3 + "," + d4);
                }
            }
        }

        return MemR;
    }

    static Memo orihime2cp(Memo mem) {
        System.out.println("オリヒメ用ファイルをcp用にする");
        Memo MemR = new Memo();
        int ibangou = 0;
        double d1, d2, d3, d4;
        String str;
        int icol = 0;

        //オリヒメ　0.Contour, 1.Mountain, 2.Valley　、ORIPA 1.Contour, 2.Mountain, 3.Valley

        MemR.reset();

        for (int ig = 1; ig <= mem.getLineCount(); ig++) {
            if (mem.getLine(ig).length() != 0) {
                StringTokenizer tk = new StringTokenizer(mem.getLine(ig), ",");
                str = tk.nextToken();
                if (str.equals("番号")) {
                    ibangou = ibangou + 1;
                }
                if (str.equals("色")) {
                    icol = Integer.parseInt(tk.nextToken()) + 1;
                }
                if (str.equals("座標")) {
                    d1 = Double.parseDouble(tk.nextToken());
                    d2 = Double.parseDouble(tk.nextToken());
                    d3 = Double.parseDouble(tk.nextToken());
                    d4 = Double.parseDouble(tk.nextToken());

                    MemR.addLine(icol + " " + d1 + " " + d2 + " " + d3 + " " + d4);
                }
            }
        }
        return MemR;
    }

    static Memo orihime2svg(Memo mem) {//これはes1.getMemo_for_kakidasi()を入力して展開図の生データのsvgを出力
        System.out.println("オリヒメ用ファイルをsvg用にする");
        Memo MemR = new Memo();
        int ibangou = 0;
        double d1, d2, d3, d4;
        String str = "";
        String str_stroke = "";
        String str_strokewidth = "";
        str_strokewidth = "1";
        LineColor icol = LineColor.BLACK_0;

        //オリヒメ　0.Contour, 1.Mountain, 2.Valley　、ORIPA 1.Contour, 2.Mountain, 3.Valley

        MemR.reset();

        MemR.addLine("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">");

        for (int ig = 1; ig <= mem.getLineCount(); ig++) {
            //     System.out.print("cpファイルの行順番..."); System.out.println(ig);
            if (mem.getLine(ig).length() != 0) {
                StringTokenizer tk = new StringTokenizer(mem.getLine(ig), ",");
                str = tk.nextToken();
                //    System.out.print("..."+str+"..."); System.out.println(ig);
                if (str.equals("番号")) {
                    ibangou = ibangou + 1;
                }
                if (str.equals("色")) {
                    icol = LineColor.from(tk.nextToken());
                }
                str_stroke = "black";
                if (icol == LineColor.BLACK_0) {
                    str_stroke = "black";
                } else if (icol == LineColor.RED_1) {
                    str_stroke = "red";
                } else if (icol == LineColor.BLUE_2) {
                    str_stroke = "blue";
                }


                if (str.equals("座標")) {
                    d1 = Double.parseDouble(tk.nextToken());
                    d2 = Double.parseDouble(tk.nextToken());
                    d3 = Double.parseDouble(tk.nextToken());
                    d4 = Double.parseDouble(tk.nextToken());

                    MemR.addLine("<line x1=\"" + d1 + "\"" +
                            " y1=\"" + d2 + "\"" +
                            " x2=\"" + d3 + "\"" +
                            " y2=\"" + d4 + "\"" +
                            " stroke=\"" + str_stroke + "\"" +
                            " stroke-width=\"" + str_strokewidth + "\"" + " />"
                    );
                }
            }
        }

        MemR.addLine("</svg>");
        return MemR;
    }

    static Memo orihime2svg(Memo mem_tenkaizu, Memo mem_oriagarizu) {
        System.out.println("svg画像出力");
        Memo MemR = new Memo();

        MemR.reset();

        MemR.addLine("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">");

        MemR.addMemo(mem_tenkaizu);
        MemR.addMemo(mem_oriagarizu);

        MemR.addLine("</svg>");
        return MemR;
    }
}
