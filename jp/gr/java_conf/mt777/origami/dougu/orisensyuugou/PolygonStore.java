package jp.gr.java_conf.mt777.origami.dougu.orisensyuugou;

import java.awt.*;

import jp.gr.java_conf.mt777.kiroku.memo.*;

import jp.gr.java_conf.mt777.zukei2d.en.*;
import jp.gr.java_conf.mt777.zukei2d.senbun.*;
import jp.gr.java_conf.mt777.zukei2d.oritacalc.*;
import jp.gr.java_conf.mt777.zukei2d.oritacalc.tyokusen.*;
import jp.gr.java_conf.mt777.seiretu.narabebako.*;
import jp.gr.java_conf.mt777.zukei2d.takakukei.Polygon;
import jp.gr.java_conf.mt777.zukei2d.ten.Point;

import java.util.*;

// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
public class PolygonStore {
    int total;               //実際に使う線分の総数
    ArrayList<LineSegment> Senb = new ArrayList<>(); //折線とする線分のインスタンス化
    OritaCalc oc = new OritaCalc();          //各種計算用の関数を使うためのクラスのインスタンス化

    ArrayList<LineSegment> Check1Senb = new ArrayList<>(); //check情報を格納する線分のインスタンス化
    ArrayList<LineSegment> Check2Senb = new ArrayList<>(); //check情報を格納する線分のインスタンス化
    ArrayList<LineSegment> Check3Senb = new ArrayList<>(); //check情報を格納する線分のインスタンス化
    ArrayList<LineSegment> Check4Senb = new ArrayList<>(); //check情報を格納する線分のインスタンス化
    ArrayList<Point> check4Point = new ArrayList<>(); //checkすべき点のインスタンス化


    ArrayList<Circle> Cir = new ArrayList<>(); //円のインスタンス化

    public PolygonStore() {
        reset();
    } //コンストラクタ

    public void reset() {
        total = 0;
        Senb.clear();
        Senb.add(new LineSegment());
        Check1Senb.clear();
        Check2Senb.clear();
        Check3Senb.clear();//Check3Senb.add(new Senbun());
        Check4Senb.clear();//Check4Senb.add(new Senbun());
        check4Point.clear();
        Cir.clear();
        Cir.add(new Circle());
    }

    public void hyouji(String s0) {

        System.out.println(s0 + "  sousuu = " + total);
        for (int i = 1; i <= total; i++) {

            LineSegment s;
            s = line(i);
            s.display(" ");

            //System.out.println("  i=" +i +" ; ("+oc.d2s(getax(i))+" , "+oc.d2s(getay(i))+")---("+oc.d2s(getbx(i)+" , "+oc.d2s(getby(i)+")");


        }

    }


    public void set(PolygonStore polygonStore) {
        total = polygonStore.getTotal();
        for (int i = 0; i <= total; i++) {
            LineSegment s;
            s = line(i);
            s.set(polygonStore.get(i));
        }
    }

    private LineSegment line(int i) {
        if (total + 1 > Senb.size()) {
            while (total + 1 > Senb.size()) {
                Senb.add(new LineSegment());
            }
        }//この文がないとうまく行かない。なぜこの文でないといけないかという理由が正確にはわからない。
        return Senb.get(i);
    }

    //
    private void setLine(int i, LineSegment s) {
        if (total + 1 > Senb.size()) {
            while (total + 1 > Senb.size()) {
                Senb.add(new LineSegment());
            }
        }//この文がないとうまく行かない。なぜこの文でないといけないかという理由が正確にはわからない。
        if (i + 1 <= Senb.size()) {
            Senb.set(i, s);
        } //なぜか、このifがないとうまく行かない
    }

    //線分の総数を得る
    public int getTotal() {
        return total;
    }

    public void setTotal(int i) {
        total = i;
    }

    //線分を得る
    public LineSegment get(int i) {
        //Senbun s;s= sen(i);return s;
        return line(i);
    }

    //i番目の線分の端点を得る
    public Point getA(int i) {
        LineSegment s;
        s = line(i);
        return s.getA();
    }

    public Point getB(int i) {
        LineSegment s;
        s = line(i);
        return s.getB();
    }

    //i番目の線分の端点を得る
    public double getax(int i) {
        LineSegment s;
        s = line(i);
        return s.getAx();
    }

    public double getbx(int i) {
        LineSegment s;
        s = line(i);
        return s.getbx();
    }

    public double getay(int i) {
        LineSegment s;
        s = line(i);
        return s.getay();
    }

    public double getby(int i) {
        LineSegment s;
        s = line(i);
        return s.getby();
    }

    //i番目の線分の端点の位置をセットする
    public void seta(int i, Point p) {
        LineSegment s;
        s = line(i);
        s.setA(p);
    }

    public void setb(int i, Point p) {
        LineSegment s;
        s = line(i);
        s.setB(p);
    }


    //i番目の線分をセットする
    //public void set(int i,Senbun s0){Senbun s;s= sen(i);s.set(s0);}

    //i番目の線分の値を入力する
    public void set(int i, Point p, Point q) {
        LineSegment s;
        s = line(i);
        s.setA(p);
        s.setB(q);
    }

    //i番目の線分の値を入力する
    public void set(int i, Point p, Point q, int ic, int ia) {
        LineSegment s;
        s = line(i);
        s.set(p, q, ic, ia);
    }

    //i番目の線分の色を入力する
    public void setcolor(int i, int icol) {
        LineSegment s;
        s = line(i);
        s.setcolor(icol);
    }

    //i番目の線分の色を出力する
    public int getcolor(int i) {
        LineSegment s;
        s = line(i);
        return s.getColor();
    }

    //
    public void set_tpp_sen(int i, int itpp) {
        LineSegment s;
        s = line(i);
        s.set_tpp(itpp);
    }

    public int get_tpp_sen(int i) {
        LineSegment s;
        s = line(i);
        return s.get_tpp();
    }

    public void set_tpp_sen_color(int i, Color c0) {
        LineSegment s;
        s = line(i);
        s.set_tpp_color(c0);
    }

    //public void set_tpp_sen_color(int iR,int iG,int iB){tpp_color=new Color(iR,iG,iB);}
    public Color get_tpp_sen_color(int i) {
        LineSegment s;
        s = line(i);
        return s.get_tpp_color();
    }

    //public Color get_tpp_sen_color(){return new Color(tpp_color.getRed(),tpp_color.getGreen(),tpp_color.getBlue());}
//
//pppppppppp
    public void set_tpp_en(int i, int itpp) {
        Circle e;
        e = cir_getEn(i);
        e.set_tpp(itpp);
    }

    public int get_tpp_en(int i) {
        Circle e;
        e = cir_getEn(i);
        return e.get_tpp();
    }

    public void set_tpp_en_color(int i, Color c0) {
        Circle e;
        e = cir_getEn(i);
        e.set_tpp_color(c0);
    }

    //public void set_tpp_en_color(int iR,int iG,int iB){tpp_color=new Color(iR,iG,iB);}
    public Color get_tpp_en_color(int i) {
        Circle e;
        e = cir_getEn(i);
        return e.get_tpp_color();
    }
    //public Color get_tpp_en_color(){return new Color(tpp_color.getRed(),tpp_color.getGreen(),tpp_color.getBlue());}


//

    //i番目の線分の活性を入力する
    public void setiactive(int i, int iactive) {
        LineSegment s;
        s = line(i);
        s.setiactive(iactive);
    }


    //i番目の線分の活性を出力する
    public int getiactive(int i) {
        LineSegment s;
        s = line(i);
        return s.getiactive();
    }

    //-----------------------------
    //線分集合の全線分の情報を Memoとして出力する。
    public Memo getMemo() {
        return getMemo("_");
    }

    //-----------------------------
    //線分集合の全線分の情報を Memoとして出力する。 //undo,redoの記録用に使う
    public Memo getMemo(String s_title) {
        String str = "";//文字列処理用のクラスのインスタンス化

        Memo memo1 = new Memo();
        memo1.reset();


        memo1.addLine("<タイトル>");
        memo1.addLine("タイトル," + s_title);


        memo1.addLine("<線分集合>");

        for (int i = 1; i <= total; i++) {
            memo1.addLine("番号," + i);
            LineSegment s;
            s = line(i);
            memo1.addLine("色," + s.getColor());

            memo1.addLine("<tpp>" + s.get_tpp() + "</tpp>");                    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            memo1.addLine("<tpp_color_R>" + s.get_tpp_color().getRed() + "</tpp_color_R>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            memo1.addLine("<tpp_color_G>" + s.get_tpp_color().getGreen() + "</tpp_color_G>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            memo1.addLine("<tpp_color_B>" + s.get_tpp_color().getBlue() + "</tpp_color_B>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

            memo1.addLine("座標," + s.getAx() + "," + s.getay() + "," + s.getbx() + "," + s.getby());
        }

        memo1.addLine("<円集合>");
        for (int i = 1; i <= cir_size(); i++) {
            memo1.addLine("番号," + i);
            Circle e_temp = new Circle();
            e_temp.set(cir_getEn(i));
            memo1.addLine("中心と半径と色," + e_temp.getx() + "," + e_temp.gety() + "," + e_temp.getRadius() + "," + e_temp.getcolor());

            memo1.addLine("<tpp>" + e_temp.get_tpp() + "</tpp>");                    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            memo1.addLine("<tpp_color_R>" + e_temp.get_tpp_color().getRed() + "</tpp_color_R>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            memo1.addLine("<tpp_color_G>" + e_temp.get_tpp_color().getGreen() + "</tpp_color_G>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            memo1.addLine("<tpp_color_B>" + e_temp.get_tpp_color().getBlue() + "</tpp_color_B>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


        }


        return memo1;
    }


//-----------------------------


    //線分集合の全線分の情報を Memoとして出力する。//iactiveがijyogaiの折線はメモに書き出さない
    public Memo getMemo_iactive_jyogai(int ijyogai) {
        String str = "";//文字列処理用のクラスのインスタンス化

        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<線分集合>");

        int ibangou = 0;
        for (int i = 1; i <= total; i++) {
            if (getiactive(i) != ijyogai) {
                ibangou = ibangou + 1;
                memo1.addLine("番号," + ibangou);
                LineSegment s;
                s = line(i);
                memo1.addLine("色," + s.getColor());

                memo1.addLine("<tpp>" + s.get_tpp() + "</tpp>");                    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                memo1.addLine("<tpp_color_R>" + s.get_tpp_color().getRed() + "</tpp_color_R>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                memo1.addLine("<tpp_color_G>" + s.get_tpp_color().getGreen() + "</tpp_color_G>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                memo1.addLine("<tpp_color_B>" + s.get_tpp_color().getBlue() + "</tpp_color_B>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


                memo1.addLine("選択," + s.get_i_select());
                memo1.addLine("座標," + s.getAx() + "," + s.getay() + "," + s.getbx() + "," + s.getby());
            }
        }

        memo1.addLine("<円集合>");
        for (int i = 1; i <= cir_size(); i++) {
            memo1.addLine("番号," + i);
            Circle e_temp = new Circle();
            e_temp.set(cir_getEn(i));
            //memo1.addGyou( "色,"+str.valueOf(e_temp.getcolor()));
            memo1.addLine("中心と半径と色," + e_temp.getx() + "," + e_temp.gety() + "," + e_temp.getRadius() + "," + e_temp.getcolor());

            memo1.addLine("<tpp>" + e_temp.get_tpp() + "</tpp>");                    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            memo1.addLine("<tpp_color_R>" + e_temp.get_tpp_color().getRed() + "</tpp_color_R>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            memo1.addLine("<tpp_color_G>" + e_temp.get_tpp_color().getGreen() + "</tpp_color_G>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            memo1.addLine("<tpp_color_B>" + e_temp.get_tpp_color().getBlue() + "</tpp_color_B>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


        }


        return memo1;
    }


    //-----------------------------
    //補助画線分集合の全線分の情報を Memoとして出力する。
    public Memo h_getMemo() {
        String str = "";//文字列処理用のクラスのインスタンス化

        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<補助線分集合>");

        for (int i = 1; i <= total; i++) {
            memo1.addLine("補助番号," + i);
            LineSegment s;
            s = line(i);
            memo1.addLine("補助色," + s.getColor());

            memo1.addLine("<tpp>" + s.get_tpp() + "</tpp>");                    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            memo1.addLine("<tpp_color_R>" + s.get_tpp_color().getRed() + "</tpp_color_R>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            memo1.addLine("<tpp_color_G>" + s.get_tpp_color().getGreen() + "</tpp_color_G>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            memo1.addLine("<tpp_color_B>" + s.get_tpp_color().getBlue() + "</tpp_color_B>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

            memo1.addLine("補助座標," + s.getAx() + "," + s.getay() + "," + s.getbx() + "," + s.getby());
        }
        return memo1;
    }

    //-----------------------------
    //折畳み推定用に線分集合の情報を Memoとして出力する。//icolが3(cyan＝水色)以上の補助線はメモに書き出さない
    public Memo getMemo_for_oritatami() {
        String str = "";//文字列処理用のクラスのインスタンス化

        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<線分集合>");

        int ibangou = 0;
        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = line(i);
            if (s.getColor() < 3) {
                ibangou = ibangou + 1;
                memo1.addLine("番号," + ibangou);

                memo1.addLine("色," + s.getColor());

                memo1.addLine("<tpp>" + s.get_tpp() + "</tpp>");                    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                memo1.addLine("<tpp_color_R>" + s.get_tpp_color().getRed() + "</tpp_color_R>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                memo1.addLine("<tpp_color_G>" + s.get_tpp_color().getGreen() + "</tpp_color_G>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                memo1.addLine("<tpp_color_B>" + s.get_tpp_color().getBlue() + "</tpp_color_B>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


                memo1.addLine("座標," + s.getAx() + "," + s.getay() + "," + s.getbx() + "," + s.getby());
            }
        }
        return memo1;
    }

    //-----------------------------
    //折畳み推定用にselectされた線分集合の情報を Memoとして出力する。//icolが3(cyan＝水色)以上の補助線はメモに書き出さない
    public Memo getMemo_for_select_oritatami() {
        String str = "";//文字列処理用のクラスのインスタンス化
//ggggggggggggggg
        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<線分集合>");

        int ibangou = 0;
        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = line(i);
            if ((s.getColor() < 3) && (s.get_i_select() == 2)) {
                ibangou = ibangou + 1;
                memo1.addLine("番号," + ibangou);

                memo1.addLine("色," + s.getColor());

                memo1.addLine("<tpp>" + s.get_tpp() + "</tpp>");                    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                memo1.addLine("<tpp_color_R>" + s.get_tpp_color().getRed() + "</tpp_color_R>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                memo1.addLine("<tpp_color_G>" + s.get_tpp_color().getGreen() + "</tpp_color_G>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                memo1.addLine("<tpp_color_B>" + s.get_tpp_color().getBlue() + "</tpp_color_B>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


                memo1.addLine("座標," + s.getAx() + "," + s.getay() + "," + s.getbx() + "," + s.getby());
            }
        }
        return memo1;
    }

//-----------------------------

    //折畳み推定用にselectされた線分集合の折線数を intとして出力する。//icolが3(cyan＝水色)以上の補助線はカウントしない
    public int get_orisensuu_for_select_oritatami() {
        String str = "";//文字列処理用のクラスのインスタンス化
//ggggggggggggggg
        //Memo memo1 = new Memo();
        //memo1.reset();
        //memo1.addGyou("<線分集合>");

        int ibangou = 0;
        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = line(i);
            if ((s.getColor() < 3) && (s.get_i_select() == 2)) {
                ibangou = ibangou + 1;
                //memo1.addGyou("番号,"+str.valueOf(ibangou));

                //memo1.addGyou( "色,"+str.valueOf(s.getcolor()));

                //memo1.addGyou( "<tpp>"+str.valueOf(s.get_tpp())+"</tpp>");					//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                //memo1.addGyou("<tpp_color_R>"+str.valueOf(s.get_tpp_color().getRed()	)+"</tpp_color_R>");	//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                //memo1.addGyou("<tpp_color_G>"+str.valueOf(s.get_tpp_color().getGreen()	)+"</tpp_color_G>");	//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                //memo1.addGyou("<tpp_color_B>"+str.valueOf(s.get_tpp_color().getBlue()	)+"</tpp_color_B>");	//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


                //memo1.addGyou( "座標,"  +	str.valueOf(s.getax())+","+ str.valueOf(s.getay())+","+ str.valueOf(s.getbx())+","+ str.valueOf(s.getby()));
            }
        }
        return ibangou;
    }

//-----------------------------


    //特注プロパティの読み込みに使う変数の設定
    String[] st_new;
    String[] s_new;
    int i_tpp = 0;
    int i_tpp_color_R = 0;
    int i_tpp_color_G = 0;
    int i_tpp_color_B = 0;

//-----------------------------


    public String setMemo(Memo memo1) {//戻り値はundo,redoの記録用に使うタイトル

        int yomiflg = 0;//0なら読み込みを行わない。1なら読み込む。
        int ibangou = 0;
        int ic = 0;
        int is = 0;


        String r_title = "";
        r_title = "_";

        double ax, ay, bx, by;
        double dx, dy, dr;

        String str = "";
        //int jtok;

        reset();

        //オリヒメ用ファイル.orhを読む

        //最初に線分の総数を求める
        int isen = 0;
        for (int i = 1; i <= memo1.getLineSize(); i++) {

            StringTokenizer tk = new StringTokenizer(memo1.getLine(i), ",");
            //jtok=    tk.countTokens();

            str = tk.nextToken();
            if (str.equals("<線分集合>")) {
                yomiflg = 1;
            }
            if (str.equals("<円集合>")) {
                yomiflg = 3;
            }
            if ((yomiflg == 1) && (str.equals("番号"))) {
                isen = isen + 1;
            }
        }
        total = isen;
        //最初に線分の総数が求められた
        //

        Circle e_temp = new Circle();

        for (int i = 1; i <= memo1.getLineSize(); i++) {
            String str_i = memo1.getLine(i);

            //旧式の読み込み方法
            StringTokenizer tk = new StringTokenizer(str_i, ",");
            str = tk.nextToken();

            //新式の読み込み方法
            str_i.trim();


            if (str.equals("<タイトル>")) {
                yomiflg = 2;
            }
            if ((yomiflg == 2) && (str.equals("タイトル"))) {
                str = tk.nextToken();
                r_title = str;
            }


            if (str.equals("<線分集合>")) {
                yomiflg = 1;
            }
            if ((yomiflg == 1) && (str.equals("番号"))) {
                str = tk.nextToken();
                ibangou = Integer.parseInt(str);
            }
            if ((yomiflg == 1) && (str.equals("色"))) {
                str = tk.nextToken();
                ic = Integer.parseInt(str);
                LineSegment s;
                s = line(ibangou);
                s.setcolor(ic);
            }

            if (yomiflg == 1) {
                st_new = str_i.split(">", 2);// <-----------------------------------２つに分割するときは2を指定
                if (st_new[0].equals("<tpp")) {
                    s_new = st_new[1].split("<", 2);
                    i_tpp = (Integer.parseInt(s_new[0]));
                    LineSegment s;
                    s = line(ibangou);
                    s.set_tpp(i_tpp);
                }
                //  System.out.println(Integer.parseInt(s[0])) ;

                if (st_new[0].equals("<tpp_color_R")) {
                    s_new = st_new[1].split("<", 2);
                    i_tpp_color_R = (Integer.parseInt(s_new[0]));
                    LineSegment s;
                    s = line(ibangou);
                    s.set_tpp_color(new Color(i_tpp_color_R, i_tpp_color_G, i_tpp_color_B));
                }        //  System.out.println(Integer.parseInt(s[0])) ;

                if (st_new[0].equals("<tpp_color_G")) {
                    s_new = st_new[1].split("<", 2);
                    i_tpp_color_G = (Integer.parseInt(s_new[0]));
                    LineSegment s;
                    s = line(ibangou);
                    s.set_tpp_color(new Color(i_tpp_color_R, i_tpp_color_G, i_tpp_color_B));
                }
                if (st_new[0].equals("<tpp_color_B")) {
                    s_new = st_new[1].split("<", 2);
                    i_tpp_color_B = (Integer.parseInt(s_new[0]));
                    LineSegment s;
                    s = line(ibangou);
                    s.set_tpp_color(new Color(i_tpp_color_R, i_tpp_color_G, i_tpp_color_B));
                }

            }


            if ((yomiflg == 1) && (str.equals("iactive"))) {//20181110追加
                str = tk.nextToken();
                is = Integer.parseInt(str);
                LineSegment s;
                s = line(ibangou);
                s.setiactive(is);
            }


            if ((yomiflg == 1) && (str.equals("選択"))) {
                str = tk.nextToken();
                is = Integer.parseInt(str);
                LineSegment s;
                s = line(ibangou);
                s.set_i_select(is);
            }
            if ((yomiflg == 1) && (str.equals("座標"))) {
                str = tk.nextToken();
                ax = Double.parseDouble(str);
                str = tk.nextToken();
                ay = Double.parseDouble(str);
                str = tk.nextToken();
                bx = Double.parseDouble(str);
                str = tk.nextToken();
                by = Double.parseDouble(str);

                LineSegment s;
                s = line(ibangou);
                s.set(ax, ay, bx, by);
                //	System.out.println(ax );
            }


//pppppppppppppp


            if (str.equals("<円集合>")) {
                yomiflg = 3;
            }

            if ((yomiflg == 3) && (str.equals("番号"))) {
                str = tk.nextToken();
                ibangou = Integer.parseInt(str);

                cir_setEn(ibangou, e_temp);
            }

            if ((yomiflg == 3) && (str.equals("中心と半径と色"))) {

                str = tk.nextToken();
                dx = Double.parseDouble(str);
                str = tk.nextToken();
                dy = Double.parseDouble(str);
                str = tk.nextToken();
                dr = Double.parseDouble(str);

                str = tk.nextToken();
                ic = Integer.parseInt(str);


                cir_getEn(ibangou).set(dx, dy, dr, ic);
            }


            if (yomiflg == 3) {
                st_new = str_i.split(">", 2);// <-----------------------------------２つに分割するときは2を指定
                if (st_new[0].equals("<tpp")) {
                    s_new = st_new[1].split("<", 2);
                    i_tpp = (Integer.parseInt(s_new[0]));
                    cir_getEn(ibangou).set_tpp(i_tpp);
                }

                if (st_new[0].equals("<tpp_color_R")) {
                    s_new = st_new[1].split("<", 2);
                    i_tpp_color_R = (Integer.parseInt(s_new[0]));
                    cir_getEn(ibangou).set_tpp_color(new Color(i_tpp_color_R, i_tpp_color_G, i_tpp_color_B));
                }        //  System.out.println(Integer.parseInt(s[0])) ;

                if (st_new[0].equals("<tpp_color_G")) {
                    s_new = st_new[1].split("<", 2);
                    i_tpp_color_G = (Integer.parseInt(s_new[0]));
                    cir_getEn(ibangou).set_tpp_color(new Color(i_tpp_color_R, i_tpp_color_G, i_tpp_color_B));
                }
                if (st_new[0].equals("<tpp_color_B")) {
                    s_new = st_new[1].split("<", 2);
                    i_tpp_color_B = (Integer.parseInt(s_new[0]));
                    cir_getEn(ibangou).set_tpp_color(new Color(i_tpp_color_R, i_tpp_color_G, i_tpp_color_B));
                }

            }


        }
        return r_title;
    }


//-----------------------------

    public void h_setMemo(Memo memo1) {
        int yomiflg = 0;//0なら読み込みを行わない。1なら読み込む。
        int ibangou = 0;
        int ic = 0;
        int is = 0;

        double ax, ay, bx, by;
        String str = "";

        //オリヒメ用ファイル.orhを読む

        //最初に線分の総数を求める
        int isen = 0;
        for (int i = 1; i <= memo1.getLineSize(); i++) {

            StringTokenizer tk = new StringTokenizer(memo1.getLine(i), ",");
            //jtok=    tk.countTokens();

            str = tk.nextToken();
            if (str.equals("<補助線分集合>")) {
                yomiflg = 1;
            }
            if ((yomiflg == 1) && (str.equals("補助番号"))) {
                isen = isen + 1;
            }
        }
        total = isen;
        //最初に補助線分の総数が求められた
        //

        for (int i = 1; i <= memo1.getLineSize(); i++) {


            StringTokenizer tk = new StringTokenizer(memo1.getLine(i), ",");
            //jtok=    tk.countTokens();
            str = tk.nextToken();
            //  	System.out.println("::::::::::"+ str+"<<<<<" );
            if (str.equals("<補助線分集合>")) {
                yomiflg = 1;
            }
            if ((yomiflg == 1) && (str.equals("補助番号"))) {
                str = tk.nextToken();
                ibangou = Integer.parseInt(str);
            }
            if ((yomiflg == 1) && (str.equals("補助色"))) {
                str = tk.nextToken();
                ic = Integer.parseInt(str);
                LineSegment s;
                s = line(ibangou);
                s.setcolor(ic);
            }
            if ((yomiflg == 1) && (str.equals("補助選択"))) {
                str = tk.nextToken();
                is = Integer.parseInt(str);
                LineSegment s;
                s = line(ibangou);
                s.set_i_select(is);
            }
            if ((yomiflg == 1) && (str.equals("補助座標"))) {
                str = tk.nextToken();
                ax = Double.parseDouble(str);
                str = tk.nextToken();
                ay = Double.parseDouble(str);
                str = tk.nextToken();
                bx = Double.parseDouble(str);
                str = tk.nextToken();
                by = Double.parseDouble(str);

                LineSegment s;
                s = line(ibangou);
                s.set(ax, ay, bx, by);
                //	System.out.println(ax );
            }
        }
    }


    //-----------------------------
    public void addMemo(Memo memo1) {
        int yomiflg = 0;//0なら読み込みを行わない。1なら読み込む。
        int ibangou = 0;
        int ic = 0;

        double ax, ay, bx, by;
        double dx, dy, dr;

        String str = "";
        //int jtok;

        int sousuu_old = total;

        //オリヒメ用ファイル.orhを読む

        //最初に線分の総数を求める
        int isen = 0;
        for (int i = 1; i <= memo1.getLineSize(); i++) {

            StringTokenizer tk = new StringTokenizer(memo1.getLine(i), ",");
            //jtok=    tk.countTokens();

            str = tk.nextToken();
            if (str.equals("<線分集合>")) {
                yomiflg = 1;
            }
            if ((yomiflg == 1) && (str.equals("番号"))) {
                isen = isen + 1;
            }
        }
        total = sousuu_old + isen;


        //最初に線分の総数が求められた
        //

        for (int i = 1; i <= memo1.getLineSize(); i++) {

            String str_i = memo1.getLine(i);

            //旧式の読み込み方法
            StringTokenizer tk = new StringTokenizer(str_i, ",");
            str = tk.nextToken();

            //新式の読み込み方法
            str_i.trim();


            //StringTokenizer tk = new StringTokenizer(memo1.getGyou(i),",");
            //jtok=    tk.countTokens();
            //str=tk.nextToken();
            //  	System.out.println("::::::::::"+ str+"<<<<<" );
            if (str.equals("<線分集合>")) {
                yomiflg = 1;
            }
            if ((yomiflg == 1) && (str.equals("番号"))) {
                str = tk.nextToken();
                ibangou = sousuu_old + Integer.parseInt(str);
            }
            if ((yomiflg == 1) && (str.equals("色"))) {
                str = tk.nextToken();
                ic = Integer.parseInt(str);
                LineSegment s;
                s = line(ibangou);
                s.setcolor(ic);
            }

            if (yomiflg == 1) {
                st_new = str_i.split(">", 2);// <-----------------------------------２つに分割するときは2を指定
                if (st_new[0].equals("<tpp")) {
                    s_new = st_new[1].split("<", 2);
                    i_tpp = (Integer.parseInt(s_new[0]));
                    LineSegment s;
                    s = line(ibangou);
                    s.set_tpp(i_tpp);
                }
                //  System.out.println(Integer.parseInt(s[0])) ;

                if (st_new[0].equals("<tpp_color_R")) {
                    s_new = st_new[1].split("<", 2);
                    i_tpp_color_R = (Integer.parseInt(s_new[0]));
                    LineSegment s;
                    s = line(ibangou);
                    s.set_tpp_color(new Color(i_tpp_color_R, i_tpp_color_G, i_tpp_color_B));
                }        //  System.out.println(Integer.parseInt(s[0])) ;

                if (st_new[0].equals("<tpp_color_G")) {
                    s_new = st_new[1].split("<", 2);
                    i_tpp_color_G = (Integer.parseInt(s_new[0]));
                    LineSegment s;
                    s = line(ibangou);
                    s.set_tpp_color(new Color(i_tpp_color_R, i_tpp_color_G, i_tpp_color_B));
                }
                if (st_new[0].equals("<tpp_color_B")) {
                    s_new = st_new[1].split("<", 2);
                    i_tpp_color_B = (Integer.parseInt(s_new[0]));
                    LineSegment s;
                    s = line(ibangou);
                    s.set_tpp_color(new Color(i_tpp_color_R, i_tpp_color_G, i_tpp_color_B));
                }

            }


            if ((yomiflg == 1) && (str.equals("座標"))) {
                str = tk.nextToken();
                ax = Double.parseDouble(str);
                str = tk.nextToken();
                ay = Double.parseDouble(str);
                str = tk.nextToken();
                bx = Double.parseDouble(str);
                str = tk.nextToken();
                by = Double.parseDouble(str);

				LineSegment s = line(ibangou);
                s.set(ax, ay, bx, by);
                //	System.out.println(ax );
            }


            //----------------------------------------------wwwwwwwwwww


            if (str.equals("<円集合>")) {
                yomiflg = 3;
            }

            if ((yomiflg == 3) && (str.equals("番号"))) {
                str = tk.nextToken();//ibangou=Ii.parseInt(str);
                Cir.add(new Circle(0.0, 0.0, 1.0, 1));
                ibangou = cir_size();
            }

            if ((yomiflg == 3) && (str.equals("中心と半径と色"))) {

                str = tk.nextToken();
                dx = Double.parseDouble(str);
                str = tk.nextToken();
                dy = Double.parseDouble(str);
                str = tk.nextToken();
                dr = Double.parseDouble(str);

                str = tk.nextToken();
                ic = Integer.parseInt(str);

                //En e_temp=new En(dx,dy,dr,ic);

                //Cir.add(e_temp);
                //System.out.println("cir_setEn(ibangou,e_temp)  "+ ibangou+";" +dx+"," +dy+"," +dr+"," +ic);


                cir_getEn(ibangou).set(dx, dy, dr, ic);


            }


            if (yomiflg == 3) {
                st_new = str_i.split(">", 2);// <-----------------------------------２つに分割するときは2を指定
                if (st_new[0].equals("<tpp")) {
                    s_new = st_new[1].split("<", 2);
                    i_tpp = (Integer.parseInt(s_new[0]));
                    cir_getEn(ibangou).set_tpp(i_tpp);
                }
                //  System.out.println(Integer.parseInt(s[0])) ;

                if (st_new[0].equals("<tpp_color_R")) {
                    s_new = st_new[1].split("<", 2);
                    i_tpp_color_R = (Integer.parseInt(s_new[0]));
                    cir_getEn(ibangou).set_tpp_color(new Color(i_tpp_color_R, i_tpp_color_G, i_tpp_color_B));
                }        //  System.out.println(Integer.parseInt(s[0])) ;

                if (st_new[0].equals("<tpp_color_G")) {
                    s_new = st_new[1].split("<", 2);
                    i_tpp_color_G = (Integer.parseInt(s_new[0]));
                    cir_getEn(ibangou).set_tpp_color(new Color(i_tpp_color_R, i_tpp_color_G, i_tpp_color_B));
                }
                if (st_new[0].equals("<tpp_color_B")) {
                    s_new = st_new[1].split("<", 2);
                    i_tpp_color_B = (Integer.parseInt(s_new[0]));
                    cir_getEn(ibangou).set_tpp_color(new Color(i_tpp_color_R, i_tpp_color_G, i_tpp_color_B));
                }

            }


        }
    }

//-----------------------------
    //展開図入力時の線分集合の整理

    public void divide_seiri() {//折り畳み推定などで得られる針金図の整理
        System.out.println("分割整理　１、点削除");
        point_removal();          //念のため、点状の線分を除く
        System.out.println("分割整理　２、重複線分削除");
        overlapping_line_removal();//念のため、全く一致する線分が２つあれば１つを除く
        System.out.println("分割整理　３、交差分割");
        intersect_divide();
        System.out.println("分割整理　４、点削除");
        point_removal();             //折り畳み推定の針金図の整理のため、点状の線分を除く
        System.out.println("分割整理　５、重複線分削除");
        overlapping_line_removal(); //折り畳み推定の針金図の整理のため、全く一致する線分が２つあれば１つを除く
    }

    //全線分の山谷を入れ替える。境界線等の山谷以外の線種は変化なし。
    public void zen_yama_tani_henkan() {
        int ic_temp;

        for (int ic_id = 1; ic_id <= total; ic_id++) {
            ic_temp = getcolor(ic_id);
            if (ic_temp == 1) {
                ic_temp = 2;
            } else if (ic_temp == 2) {
                ic_temp = 1;
            }
            setcolor(ic_id, ic_temp);
        }
    }

    //Smenを発生させるための線分集合の整理

    public void bunkatu_seiri_for_Smen_hassei() {//折り畳み推定などで得られる針金図の整理
        System.out.println("　　Orisensyuugouの中で、Smenを発生させるための線分集合の整理");
        System.out.println("分割整理　１、点削除前	getsousuu() = " + getTotal());
        point_removal();          //念のため、点状の線分を除く
        System.out.println("分割整理　２、重複線分削除前	getsousuu() = " + getTotal());
        overlapping_line_removal();//念のため、全く一致する線分が２つあれば１つを除く
        System.out.println("分割整理　３、交差分割前	getsousuu() = " + getTotal());
        intersect_divide();
        System.out.println("分割整理　４、点削除前	getsousuu() = " + getTotal());
        point_removal();             //折り畳み推定の針金図の整理のため、点状の線分を除く
        System.out.println("分割整理　５、重複線分削除前	getsousuu() = " + getTotal());
        overlapping_line_removal(); //折り畳み推定の針金図の整理のため、全く一致する線分が２つあれば１つを除く
        System.out.println("分割整理　５、重複線分削除後	getsousuu() = " + getTotal());
    }

//--------------------------------------------------------------------------------------------------

    //線分集合の全線分の情報を Memoとして出力する。//selectがijyogaiの折線はメモに書き出さない
    public Memo getMemo_select_jyogai(int ijyogai) {
        String str = "";//文字列処理用のクラスのインスタンス化

        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<線分集合>");

        int ibangou = 0;
        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = line(i);


            if (s.get_i_select() != ijyogai) {
                ibangou = ibangou + 1;
                memo1.addLine("番号," + ibangou);
                //Senbun s;s= sen(i);
                memo1.addLine("色," + s.getColor());

                memo1.addLine("<tpp>" + s.get_tpp() + "</tpp>");                    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                memo1.addLine("<tpp_color_R>" + s.get_tpp_color().getRed() + "</tpp_color_R>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                memo1.addLine("<tpp_color_G>" + s.get_tpp_color().getGreen() + "</tpp_color_G>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                memo1.addLine("<tpp_color_B>" + s.get_tpp_color().getBlue() + "</tpp_color_B>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

                memo1.addLine("iactive," + s.getiactive());//20181110追加
                memo1.addLine("座標," + s.getAx() + "," + s.getay() + "," + s.getbx() + "," + s.getby());

            }

        }


        memo1.addLine("<円集合>");
        for (int i = 1; i <= cir_size(); i++) {
            memo1.addLine("番号," + i);
            Circle e_temp = new Circle();
            e_temp.set(cir_getEn(i));
            //memo1.addGyou( "色,"+str.valueOf(e_temp.getcolor()));
            memo1.addLine("中心と半径と色," + e_temp.getx() + "," + e_temp.gety() + "," + e_temp.getRadius() + "," + e_temp.getcolor());

            memo1.addLine("<tpp>" + e_temp.get_tpp() + "</tpp>");                    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            memo1.addLine("<tpp_color_R>" + e_temp.get_tpp_color().getRed() + "</tpp_color_R>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            memo1.addLine("<tpp_color_G>" + e_temp.get_tpp_color().getGreen() + "</tpp_color_G>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            memo1.addLine("<tpp_color_B>" + e_temp.get_tpp_color().getBlue() + "</tpp_color_B>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


        }


        return memo1;
    }
//-----------------------------

//--------------------------------------------------------------------------------------------------

    //線分集合の全線分の情報を Memoとして出力する。//selectがisentakuの折線をメモに書き出す
    public Memo getMemo_select_sentaku(int isentaku) {
        String str = "";//文字列処理用のクラスのインスタンス化

        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<線分集合>");

        int ibangou = 0;
        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = line(i);


            if (s.get_i_select() == isentaku) {
                ibangou = ibangou + 1;
                memo1.addLine("番号," + ibangou);
                //Senbun s;s= sen(i);
                memo1.addLine("色," + s.getColor());

                memo1.addLine("<tpp>" + s.get_tpp() + "</tpp>");                    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                memo1.addLine("<tpp_color_R>" + s.get_tpp_color().getRed() + "</tpp_color_R>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                memo1.addLine("<tpp_color_G>" + s.get_tpp_color().getGreen() + "</tpp_color_G>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                memo1.addLine("<tpp_color_B>" + s.get_tpp_color().getBlue() + "</tpp_color_B>");    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


                memo1.addLine("座標," + s.getAx() + "," + s.getay() + "," + s.getbx() + "," + s.getby());

            }

        }
        return memo1;
    }

    //-----------------------------
    public void select_all() {
        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = line(i);
            s.set_i_select(2);
        }
    }


    public void unselect_all() {
        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = line(i);
            s.set_i_select(0);
        }
    }


    public void select(int i) {
        LineSegment s;
        s = line(i);
        s.set_i_select(2);
    }

    public void select(Point p1, Point p2, Point p3) {
        Polygon triangle = new Polygon(3);
        triangle.set(1, p1);
        triangle.set(2, p2);
        triangle.set(3, p3);


        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = line(i);
            if (triangle.totu_boundary_inside(s) == 1) {

                s.set_i_select(2);

            }
        }
    }

    public void select(Point p1, Point p2, Point p3, Point p4) {
        //Ten p1 = new Ten();   p1.set(si.geta());
        Polygon sikaku = new Polygon(4);
        sikaku.set(1, p1);
        sikaku.set(2, p2);
        sikaku.set(3, p3);
        sikaku.set(4, p4);

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = line(i);
            if (sikaku.totu_boundary_inside(s) == 1) {

                s.set_i_select(2);

            }
        }
    }


    public void unselect(Point p1, Point p2, Point p3) {
        Polygon sankaku = new Polygon(3);
        sankaku.set(1, p1);
        sankaku.set(2, p2);
        sankaku.set(3, p3);


        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = line(i);
            if (sankaku.totu_boundary_inside(s) == 1) {

                s.set_i_select(0);

            }
        }
    }

    public void unselect(Point p1, Point p2, Point p3, Point p4) {
        //Ten p1 = new Ten();   p1.set(si.geta());
        Polygon sikaku = new Polygon(4);
        sikaku.set(1, p1);
        sikaku.set(2, p2);
        sikaku.set(3, p3);
        sikaku.set(4, p4);

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = line(i);
            if (sikaku.totu_boundary_inside(s) == 1) {

                s.set_i_select(0);

            }
        }
    }

    //--------------------------------
    public int MV_change(Point p1, Point p2, Point p3, Point p4) {
        int i_r = 0;
        //Ten p1 = new Ten();   p1.set(si.geta());
        Polygon sikaku = new Polygon(4);
        sikaku.set(1, p1);
        sikaku.set(2, p2);
        sikaku.set(3, p3);
        sikaku.set(4, p4);

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = line(i);
            if (sikaku.totu_boundary_inside(s) == 1) {
                int ic_temp;
                ic_temp = getcolor(i);/**/
                if (ic_temp == 1) {
                    setcolor(i, 2);
                } else if (ic_temp == 2) {
                    setcolor(i, 1);
                }
                i_r = 1;
            }
        }
        return i_r;
    }
//--------------------------------


    //--------------------------------
    public int M_nisuru(Point p1, Point p2, Point p3, Point p4) {
        int i_r = 0;
        //Ten p1 = new Ten();   p1.set(si.geta());
        Polygon sikaku = new Polygon(4);
        sikaku.set(1, p1);
        sikaku.set(2, p2);
        sikaku.set(3, p3);
        sikaku.set(4, p4);

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = line(i);
            if (sikaku.totu_boundary_inside(s) == 1) {
                s.setcolor(1);
                i_r = 1;
            }
        }
        return i_r;
    }

    //--------------------------------
    public int V_nisuru(Point p1, Point p2, Point p3, Point p4) {
        int i_r = 0;
        //Ten p1 = new Ten();   p1.set(si.geta());
        Polygon sikaku = new Polygon(4);
        sikaku.set(1, p1);
        sikaku.set(2, p2);
        sikaku.set(3, p3);
        sikaku.set(4, p4);

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = line(i);
            if (sikaku.totu_boundary_inside(s) == 1) {
                s.setcolor(2);
                i_r = 1;
            }
        }
        return i_r;
    }

    //--------------------------------
    public int E_nisuru(Point p1, Point p2, Point p3, Point p4) {
        int i_r = 0;
        //Ten p1 = new Ten();   p1.set(si.geta());
        Polygon sikaku = new Polygon(4);
        sikaku.set(1, p1);
        sikaku.set(2, p2);
        sikaku.set(3, p3);
        sikaku.set(4, p4);

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = line(i);
            if (sikaku.totu_boundary_inside(s) == 1) {
                s.setcolor(0);
                i_r = 1;
            }
        }
        return i_r;

    }

    //--------------------------------
    public int HK_nisuru(Point p1, Point p2, Point p3, Point p4) {
        int i_r = 0;
        //Ten p1 = new Ten();   p1.set(si.geta());
        Polygon sikaku = new Polygon(4);
        sikaku.set(1, p1);
        sikaku.set(2, p2);
        sikaku.set(3, p3);
        sikaku.set(4, p4);


        int okikae_suu = 0;
        for (int i = 1; i <= total; i++) {
            if (getcolor(i) < 3) {
                LineSegment s;
                s = line(i);
                if (sikaku.totu_boundary_inside(s) == 1) {
                    okikae_suu = okikae_suu + 1;

                    LineSegment add_sen = new LineSegment();
                    add_sen.set(s);
                    add_sen.setcolor(3);

                    deleteLine(i);
                    addLine(add_sen);
                    i = i - 1;

                    //s.setcolor(3);
                    i_r = 1;
//kousabunkatu();


                }
            }
        }

        int kawatteinai_kazu = total - okikae_suu;
        if (kawatteinai_kazu == 0) {
            intersect_divide();
        }
        if (kawatteinai_kazu >= 1) {
            if (okikae_suu >= 1) {
                intersect_divide(1, total - okikae_suu, total - okikae_suu + 1, total);
            }
        }
//上２行の場合わけが必要な理由は、kousabunkatu()をやってしまうと折線と補助活線との交点で折線が分割されるから。kousabunkatu(1,sousuu-okikae_suu,sousuu-okikae_suu+1,sousuu)だと折線は分割されない。


        return i_r;

    }


//public int Senbun_kasanari_hantei(Senbun s1,Senbun s2){//0は重ならない。1は重なる。20201012追加


//-----------------------wwwwwwwwwwwwwww---------

    //public int Senbun_X_kousa_hantei(Senbun s1,Senbun s2){//0はX交差しない。1は交差する。20201017追加
    //  if (s.substring(4, 7).equals("boa")) {

    public int D_nisuru_line(LineSegment s_step1, String Dousa_mode) {
        //"l"  lXは小文字のエル。Senbun s_step1と重複する部分のある線分を削除するモード。
        //"lX" lXは小文字のエルと大文字のエックス。Senbun s_step1と重複する部分のある線分やX交差する線分を削除するモード。
        int i_r = 0;//たくさんある折線のうち、一本でも削除すれば1、1本も削除しないなら0。
        //Ten p1 = new Ten();   p1.set(si.geta());

        String str = "";//文字列処理用のクラスのインスタンス化
        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<線分集合>");
        int ibangou = 0;

        int i_kono_orisen_wo_sakujyo = 0;//i_この折線を削除　0削除しない、1削除する
        for (int i = 1; i <= total; i++) {

            LineSegment s;
            s = line(i);

            i_kono_orisen_wo_sakujyo = 0;

            if (Dousa_mode.equals("l")) {
                if (oc.LineSegment_overlapping_decide(s, s_step1) == 1) {
                    i_kono_orisen_wo_sakujyo = 1;
                }
            }

            if (Dousa_mode.equals("lX")) {
                if (oc.LineSegment_overlapping_decide(s, s_step1) == 1) {
                    i_kono_orisen_wo_sakujyo = 1;
                }
                if (oc.Senbun_X_kousa_hantei(s, s_step1) == 1) {
                    i_kono_orisen_wo_sakujyo = 1;
                }
            }


            if (i_kono_orisen_wo_sakujyo == 1) {
                i_r = 1;
            }
            if (i_kono_orisen_wo_sakujyo == 0) {
                ibangou = ibangou + 1;
                memo1.addLine("番号," + ibangou);
                //Senbun s;s= sen(i);
                memo1.addLine("色," + s.getColor());
                memo1.addLine("座標," + s.getAx() + "," + s.getay() + "," + s.getbx() + "," + s.getby());
            }
        }

        Point ec = new Point();//円の中心座標を入れる変数
        double er;//円の中心座標を入れる変数

        //Senbun s1=new Senbun(p1,p2);
        //Senbun s2=new Senbun(p2,p3);
        //Senbun s3=new Senbun(p3,p4);
        //Senbun s4=new Senbun(p4,p1);

        memo1.addLine("<円集合>");
        int ii = 0;
        for (int i = 1; i <= cir_size(); i++) {
            int idel = 0;
            Circle e_temp = new Circle();
            e_temp.set(cir_getEn(i));
            ec.set(e_temp.getCenter());
            er = e_temp.getRadius();

            //if(oc.kyori_senbun(ec,s1)<= er){ if((oc.kyori(s1.geta(),ec)>= er)||(oc.kyori(s1.geta(),ec)>= er))  {idel=1;}}
            //if(oc.kyori_senbun(ec,s2)<= er){ if((oc.kyori(s2.geta(),ec)>= er)||(oc.kyori(s2.geta(),ec)>= er))  {idel=1;}}
            //if(oc.kyori_senbun(ec,s3)<= er){ if((oc.kyori(s3.geta(),ec)>= er)||(oc.kyori(s3.geta(),ec)>= er))  {idel=1;}}
            //if(oc.kyori_senbun(ec,s4)<= er){ if((oc.kyori(s4.geta(),ec)>= er)||(oc.kyori(s4.geta(),ec)>= er))  {idel=1;}}

            //if(sikaku.totu_kyoukai_naibu(new Senbun( e_temp.get_tyuusin(), e_temp.get_tyuusin()))==1){idel=1;}

            if (idel == 1) {
                i_r = 1;
            }
            if (idel == 0) {
                ii = ii + 1;
                memo1.addLine("番号," + ii);
                memo1.addLine("中心と半径と色," + e_temp.getx() + "," + e_temp.gety() + "," + e_temp.getRadius() + "," + e_temp.getcolor());
            }
        }


        reset();
        setMemo(memo1);


        return i_r;
    }


//--------------------------------


    //-----------------------wwwwwwwwwwwwwww---------
    public int D_nisuru(Point p1, Point p2, Point p3, Point p4) {
        int i_r = 0;
        //Ten p1 = new Ten();   p1.set(si.geta());
        Polygon sikaku = new Polygon(4);
        sikaku.set(1, p1);
        sikaku.set(2, p2);
        sikaku.set(3, p3);
        sikaku.set(4, p4);

        String str = "";//文字列処理用のクラスのインスタンス化
        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<線分集合>");
        int ibangou = 0;

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = line(i);

            if (sikaku.totu_boundary_inside(s) == 1) {
                i_r = 1;
            }
            if (sikaku.totu_boundary_inside(s) != 1) {
                ibangou = ibangou + 1;
                memo1.addLine("番号," + ibangou);
                //Senbun s;s= sen(i);
                memo1.addLine("色," + s.getColor());
                memo1.addLine("座標," + s.getAx() + "," + s.getay() + "," + s.getbx() + "," + s.getby());
            }
        }

        Point ec = new Point();//円の中心座標を入れる変数
        double er;//円の中心座標を入れる変数

        LineSegment s1 = new LineSegment(p1, p2);
        LineSegment s2 = new LineSegment(p2, p3);
        LineSegment s3 = new LineSegment(p3, p4);
        LineSegment s4 = new LineSegment(p4, p1);

        memo1.addLine("<円集合>");
        int ii = 0;
        for (int i = 1; i <= cir_size(); i++) {
            int idel = 0;
            Circle e_temp = new Circle();
            e_temp.set(cir_getEn(i));
            ec.set(e_temp.getCenter());
            er = e_temp.getRadius();

            if (oc.distance_lineSegment(ec, s1) <= er) {
                if ((oc.distance(s1.getA(), ec) >= er) || (oc.distance(s1.getA(), ec) >= er)) {
                    idel = 1;
                }
            }
            if (oc.distance_lineSegment(ec, s2) <= er) {
                if ((oc.distance(s2.getA(), ec) >= er) || (oc.distance(s2.getA(), ec) >= er)) {
                    idel = 1;
                }
            }
            if (oc.distance_lineSegment(ec, s3) <= er) {
                if ((oc.distance(s3.getA(), ec) >= er) || (oc.distance(s3.getA(), ec) >= er)) {
                    idel = 1;
                }
            }
            if (oc.distance_lineSegment(ec, s4) <= er) {
                if ((oc.distance(s4.getA(), ec) >= er) || (oc.distance(s4.getA(), ec) >= er)) {
                    idel = 1;
                }
            }

            if (sikaku.totu_boundary_inside(new LineSegment(e_temp.getCenter(), e_temp.getCenter())) == 1) {
                idel = 1;
            }

            if (idel == 1) {
                i_r = 1;
            }
            if (idel == 0) {
                ii = ii + 1;
                memo1.addLine("番号," + ii);
                memo1.addLine("中心と半径と色," + e_temp.getx() + "," + e_temp.gety() + "," + e_temp.getRadius() + "," + e_temp.getcolor());
            }
        }


        reset();
        setMemo(memo1);


        return i_r;
    }


    //--------------------------------
    public int D_nisuru0(Point p1, Point p2, Point p3, Point p4) {//折線のみ削除

        //System.out.println("(ori_s_1)zzzzz check4_size() = "+check4_size());
        int i_r = 0;
        //Ten p1 = new Ten();   p1.set(si.geta());
        Polygon sikaku = new Polygon(4);
        sikaku.set(1, p1);
        sikaku.set(2, p2);
        sikaku.set(3, p3);
        sikaku.set(4, p4);

        //System.out.println("(ori_s_2)zzzzz check4_size() = "+check4_size());

        String str = "";//文字列処理用のクラスのインスタンス化
        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<線分集合>");
        int ibangou = 0;

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = line(i);

            if ((sikaku.totu_boundary_inside(s) == 1) && (getcolor(i) < 3)) {
                i_r = 1;
            }//黒赤青線はmemo1に書かれない。つまり削除される。
            else if ((sikaku.totu_boundary_inside(s) != 1) || (getcolor(i) >= 3)) {
                ibangou = ibangou + 1;
                memo1.addLine("番号," + ibangou);
                //Senbun s;s= sen(i);
                memo1.addLine("色," + s.getColor());
                memo1.addLine("座標," + s.getAx() + "," + s.getay() + "," + s.getbx() + "," + s.getby());
            }
        }

        //System.out.println("(ori_s_3)zzzzz check4_size() = "+check4_size());

        //Ten ec=new Ten();//円の中心座標を入れる変数
        //double er;//円の中心座標を入れる変数


        //Senbun s1=new Senbun(p1,p2);
        //Senbun s2=new Senbun(p2,p3);
        //Senbun s3=new Senbun(p3,p4);
        //Senbun s4=new Senbun(p4,p1);


        memo1.addLine("<円集合>");
        int ii = 0;
        for (int i = 1; i <= cir_size(); i++) {
            //int idel=0;
            Circle e_temp = new Circle();
            e_temp.set(cir_getEn(i));//ec.set(e_temp.get_tyuusin());er=e_temp.getr();

            //if(oc.kyori_senbun(ec,s1)<= er){ if((oc.kyori(s1.geta(),ec)>= er)||(oc.kyori(s1.geta(),ec)>= er))  {idel=1;}}
            //if(oc.kyori_senbun(ec,s2)<= er){ if((oc.kyori(s2.geta(),ec)>= er)||(oc.kyori(s2.geta(),ec)>= er))  {idel=1;}}
            //if(oc.kyori_senbun(ec,s3)<= er){ if((oc.kyori(s3.geta(),ec)>= er)||(oc.kyori(s3.geta(),ec)>= er))  {idel=1;}}
            //if(oc.kyori_senbun(ec,s4)<= er){ if((oc.kyori(s4.geta(),ec)>= er)||(oc.kyori(s4.geta(),ec)>= er))  {idel=1;}}

            //if(sikaku.totu_kyoukai_naibu(new Senbun( e_temp.get_tyuusin(), e_temp.get_tyuusin()))==1){idel=1;}

            //if(idel==1){i_r=1;}
            //if(idel==0){
            ii = ii + 1;
            memo1.addLine("番号," + ii);
            memo1.addLine("中心と半径と色," + e_temp.getx() + "," + e_temp.gety() + "," + e_temp.getRadius() + "," + e_temp.getcolor());
            //}
        }


        //System.out.println("(ori_s_4)zzzzz check4_size() = "+check4_size());


        reset();

        //System.out.println("(ori_s_5)zzzzzz check4_size() = "+check4_size());

        setMemo(memo1);

        //System.out.println("(ori_s_6)zzzzz check4_size() = "+check4_size());

        return i_r;
    }


    //--------------------------------
//--------------------------------
    public int D_nisuru2(Point p1, Point p2, Point p3, Point p4) {//折線のみ削除
        int i_r = 0;
        Polygon sikaku = new Polygon(4);
        sikaku.set(1, p1);
        sikaku.set(2, p2);
        sikaku.set(3, p3);
        sikaku.set(4, p4);

        String str = "";//文字列処理用のクラスのインスタンス化
        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<線分集合>");
        int ibangou = 0;

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = line(i);

            if ((sikaku.totu_boundary_inside(s) == 1) && (getcolor(i) == 0)) {
                i_r = 1;
            }//黒線はmemo1に書かれない。つまり削除される。
            else if ((sikaku.totu_boundary_inside(s) != 1) || (getcolor(i) >= 1)) {
                ibangou = ibangou + 1;
                memo1.addLine("番号," + ibangou);
                memo1.addLine("色," + s.getColor());
                memo1.addLine("座標," + s.getAx() + "," + s.getay() + "," + s.getbx() + "," + s.getby());
            }
        }

        memo1.addLine("<円集合>");
        int ii = 0;
        for (int i = 1; i <= cir_size(); i++) {
            Circle e_temp = new Circle();
            e_temp.set(cir_getEn(i));//ec.set(e_temp.get_tyuusin());er=e_temp.getr();
            ii = ii + 1;
            memo1.addLine("番号," + ii);
            memo1.addLine("中心と半径と色," + e_temp.getx() + "," + e_temp.gety() + "," + e_temp.getRadius() + "," + e_temp.getcolor());
        }

        reset();
        setMemo(memo1);
        return i_r;
    }


    //--------------------------------
    public int D_nisuru3(Point p1, Point p2, Point p3, Point p4) {//補助活線のみ削除
        int i_r = 0;
        //Ten p1 = new Ten();   p1.set(si.geta());
        Polygon sikaku = new Polygon(4);
        sikaku.set(1, p1);
        sikaku.set(2, p2);
        sikaku.set(3, p3);
        sikaku.set(4, p4);

        String str = "";//文字列処理用のクラスのインスタンス化
        Memo memo1 = new Memo();
        memo1.reset();
        memo1.addLine("<線分集合>");
        int ibangou = 0;

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = line(i);

            if ((sikaku.totu_boundary_inside(s) == 1) && (getcolor(i) == 3)) {
                i_r = 1;
            } else if ((sikaku.totu_boundary_inside(s) != 1) || (getcolor(i) != 3)) {
                ibangou = ibangou + 1;
                memo1.addLine("番号," + ibangou);
                //Senbun s;s= sen(i);
                memo1.addLine("色," + s.getColor());
                memo1.addLine("座標," + s.getAx() + "," + s.getay() + "," + s.getbx() + "," + s.getby());
            }
        }


        Point ec = new Point();//円の中心座標を入れる変数
        double er;//円の中心座標を入れる変数


        LineSegment s1 = new LineSegment(p1, p2);
        LineSegment s2 = new LineSegment(p2, p3);
        LineSegment s3 = new LineSegment(p3, p4);
        LineSegment s4 = new LineSegment(p4, p1);


        memo1.addLine("<円集合>");
        int ii = 0;
        for (int i = 1; i <= cir_size(); i++) {
            int idel = 0;
            Circle e_temp = new Circle();
            e_temp.set(cir_getEn(i));
            ec.set(e_temp.getCenter());
            er = e_temp.getRadius();

            if (oc.distance_lineSegment(ec, s1) <= er) {
                if ((oc.distance(s1.getA(), ec) >= er) || (oc.distance(s1.getA(), ec) >= er)) {
                    idel = 1;
                }
            }
            if (oc.distance_lineSegment(ec, s2) <= er) {
                if ((oc.distance(s2.getA(), ec) >= er) || (oc.distance(s2.getA(), ec) >= er)) {
                    idel = 1;
                }
            }
            if (oc.distance_lineSegment(ec, s3) <= er) {
                if ((oc.distance(s3.getA(), ec) >= er) || (oc.distance(s3.getA(), ec) >= er)) {
                    idel = 1;
                }
            }
            if (oc.distance_lineSegment(ec, s4) <= er) {
                if ((oc.distance(s4.getA(), ec) >= er) || (oc.distance(s4.getA(), ec) >= er)) {
                    idel = 1;
                }
            }

            if (sikaku.totu_boundary_inside(new LineSegment(e_temp.getCenter(), e_temp.getCenter())) == 1) {
                idel = 1;
            }

            if (idel == 1) {
                i_r = 1;
            }
            if (idel == 0) {
                ii = ii + 1;
                memo1.addLine("番号," + ii);
                memo1.addLine("中心と半径と色," + e_temp.getx() + "," + e_temp.gety() + "," + e_temp.getRadius() + "," + e_temp.getcolor());
            }
        }


        reset();
        setMemo(memo1);


        return i_r;
    }


//--------------------------------

    //--------------------------------
    public int chenge_property_in_4kakukei(Point p1, Point p2, Point p3, Point p4, Color sen_tokutyuu_color) {//4角形の中にある円や補助活線の色などのプロパティを変える
        int i_r = 0;
        //Ten p1 = new Ten();   p1.set(si.geta());
        Polygon sikaku = new Polygon(4);
        sikaku.set(1, p1);
        sikaku.set(2, p2);
        sikaku.set(3, p3);
        sikaku.set(4, p4);

        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = line(i);

            if ((sikaku.totu_boundary_inside(s) == 1) && (getcolor(i) == 3)) {
                i_r = 1;
                set_tpp_sen(i, 1);
                set_tpp_sen_color(i, sen_tokutyuu_color);
            }
        }

//wwwwwwww

        Point ec = new Point();//円の中心座標を入れる変数
        double er;//円の中心座標を入れる変数


        LineSegment s1 = new LineSegment(p1, p2);
        LineSegment s2 = new LineSegment(p2, p3);
        LineSegment s3 = new LineSegment(p3, p4);
        LineSegment s4 = new LineSegment(p4, p1);


        //("<円集合>");
        for (int i = 1; i <= cir_size(); i++) {
            int i_change = 0;
            Circle e_temp = new Circle();
            e_temp.set(cir_getEn(i));
            ec.set(e_temp.getCenter());
            er = e_temp.getRadius();

            if (oc.distance_lineSegment(ec, s1) <= er) {
                if ((oc.distance(s1.getA(), ec) >= er) || (oc.distance(s1.getA(), ec) >= er)) {
                    i_change = 1;
                }
            }
            if (oc.distance_lineSegment(ec, s2) <= er) {
                if ((oc.distance(s2.getA(), ec) >= er) || (oc.distance(s2.getA(), ec) >= er)) {
                    i_change = 1;
                }
            }
            if (oc.distance_lineSegment(ec, s3) <= er) {
                if ((oc.distance(s3.getA(), ec) >= er) || (oc.distance(s3.getA(), ec) >= er)) {
                    i_change = 1;
                }
            }
            if (oc.distance_lineSegment(ec, s4) <= er) {
                if ((oc.distance(s4.getA(), ec) >= er) || (oc.distance(s4.getA(), ec) >= er)) {
                    i_change = 1;
                }
            }

            if (sikaku.totu_boundary_inside(new LineSegment(e_temp.getCenter(), e_temp.getCenter())) == 1) {
                i_change = 1;
            }

            if (i_change == 1) {
                i_r = 1;
                set_tpp_en(i, 1);
                set_tpp_en_color(i, sen_tokutyuu_color);

            }
        }

        return i_r;
    }


//--------------------------------


    public void unselect(int i) {
        LineSegment s;
        s = line(i);
        s.set_i_select(0);
    }

    public int get_select(int i) {
        LineSegment s;
        s = line(i);
        return s.get_i_select();
    }

    public void set_select(int i, int isel) {
        LineSegment s;
        s = line(i);
        s.set_i_select(isel);
    }


    //----------------------------------------
    public void del_selected_senbun_hayai() {
        Memo memo_temp = new Memo();
        memo_temp.set(getMemo_select_jyogai(2));
        reset();
        setMemo(memo_temp);
    }

    //----------------------------------------
    public void del_selected_senbun() {
        int i_Flag = 1;
        while (i_Flag == 1) {
            //System.out.println("sousuu=" +sousuu);
            i_Flag = del_selected_senbun_symple_roop();
        }
    }

    //----------------------------------------
    public int del_selected_senbun_symple_roop() {//
        for (int i = 1; i <= total; i++) {
            if (get_select(i) == 2) {
                delsenbun_vertex(i);
                return 1;
            }
        }
        return 0;
    }
//--------------------------------------------------------

    //点状の線分を削除
    public void point_removal() {
        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = line(i);
            if (oc.equal(s.getA(), s.getB())) {
                deleteLine(i);
                i = i - 1;
            }
        }
    }

    public void point_removal(double r) {
        for (int i = 1; i <= total; i++) {
            LineSegment s;
            s = line(i);
            if (oc.equal(s.getA(), s.getB(), r)) {
                deleteLine(i);
                i = i - 1;
            }
        }
    }

    // 全く重なる線分が2本存在するときに番号の遅いほうを削除する。
    public void overlapping_line_removal(double r) {
        int[] removal_flg = new int[total + 1];
        LineSegment[] snew = new LineSegment[total + 1];
        for (int i = 1; i <= total; i++) {
            removal_flg[i] = 0;
            snew[i] = new LineSegment();
        }

        for (int i = 1; i <= total - 1; i++) {
            LineSegment si;
            si = line(i);
            for (int j = i + 1; j <= total; j++) {
                LineSegment sj;
                sj = line(j);
                if (r <= -9999.9) {
                    if (oc.line_intersect_decide(si, sj) == 31) {
                        removal_flg[j] = 1;
                    }
                } else {
                    if (oc.line_intersect_decide(si, sj, r, r) == 31) {
                        removal_flg[j] = 1;
                    }
                }
            }
        }

        int smax = 0;
        for (int i = 1; i <= total; i++) {
            if (removal_flg[i] == 0) {
                LineSegment si;
                si = line(i);
                smax = smax + 1;
                snew[smax].set(si);
            }
        }

        total = smax;
        for (int i = 1; i <= total; i++) {
            LineSegment si;
            si = line(i);
            si.set(snew[i]);
        }
    }

    //
    public void overlapping_line_removal() {
        overlapping_line_removal(-10000.0);
    }

    //
    public int overlapping_line_removal(int i, int j) {    //重複の削除をしたら1、しなければ0を返す
        if (i == j) {
            return 0;
        }
        LineSegment si;
        si = line(i);
        LineSegment sj;
        sj = line(j);
        if (oc.line_intersect_decide(si, sj) == 31) {  //31はsiとsjが全く同じに重なることを示す
            deleteLine(j);
            return 1;
        }
        return 0;
    }


    //Divide the two line segments at the intersection of the two intersecting line segments. If there were two line segments that completely overlapped, both would remain without any processing.
    public void intersect_divide_symple() {//System.out.println("1234567890   k_symple");
        int i_Flag = 1;
        while (i_Flag == 1) {
            System.out.println("sousuu=" + total);
            i_Flag = intersect_divide_symple_roop();
        }
    }


    public int intersect_divide_symple_roop() {//1回交差分割があったら、直ちに1をリターンする。交差分割がまったくないなら0をリターンする。、
//int jj=0;
        for (int i = 1; i <= total - 1; i++) {

            //System.out.println("(i,j) = " +i+","+jj);
            for (int j = i + 1; j <= total; j++) {
//jj=j;
                if (intersect_divide(i, j) == 1) {
                    //System.out.println("(i,j) = " +i+","+j);
                    return 1;
                }


            }
        }
        return 0;
    }


    //------------------zzzzzzzzz-------------------------------------------------------------------
    //Divide the two line segments at the intersection of the two intersecting line segments. If there were two line segments that completely overlapped, both would remain without any processing.
    public void intersect_divide(int i1, int i2, int i3, int i4) {//Crossing division when i3 to i4 fold lines are added to the original i1 to i2 fold lines
        int ibunkatu = 1;//分割があれば1、なければ0

        for (int i = 1; i <= total; i++) {
            setiactive(i, 0);
        }//削除すべき線は iactive=100とする
        //System.out.println("1234567890   kousabunkatu");
        ArrayList<Integer> k_flg = new ArrayList<>();//交差分割の影響があることを示すフラッグ。

        for (int i = 0; i <= total + 100; i++) {
            k_flg.add(0);
        }//0は交差分割の対象外、１は元からあった折線、2は加える折線として交差分割される。3は削除すべきと判定された折線
        for (int i = i1; i <= i2; i++) {
            k_flg.set(i, 1);
        }//0は交差分割の対象外、１は元からあった折線、2は加える折線として交差分割される
        for (int i = i3; i <= i4; i++) {
            k_flg.set(i, 2);
        }//0は交差分割の対象外、１は元からあった折線、2は加える折線として交差分割される
        //int old_sousuu =sousuu;
        //while(ibunkatu!=0){ibunkatu=0;
        for (int i = 1; i <= total; i++) {
            Integer I_k_flag = k_flg.get(i);
            //System.out.println("sousuu="+sousuu +",i="+i+", I_k_flag="+I_k_flag ) ;
            if (I_k_flag == 2) {//k_flg.set(i,new Integer(0));
                for (int j = 1; j <= total; j++) {
                    if (i != j) {
                        Integer J_k_flag = k_flg.get(j);
                        if (J_k_flag == 1) {
                            int itemp = 0;
                            //int old_sousuu =sousuu;
                            itemp = kousabunkatu_hayai(i, j);//iは加える方(2)、jは元からある方(1)
                            //System.out.println("itemp="+itemp);

                            //	if(old_sousuu<sousuu){
                            //		for (int is=old_sousuu+1;is<=sousuu;is++){
                            //			k_flg.add(new Integer(1));
                            //		}
                            //	}
                            //
                            if (itemp == 1) {
                                //ibunkatu=ibunkatu+1;
                                k_flg.add(2);//なぜかこれだと2でなくて0として追加される。20161130
                                k_flg.add(1);
                                //System.out.println("sousuu="+sousuu +",i="+i+", I_k_flag="+I_k_flag ) ;
                                k_flg.set(total - 1, 2);//
                                k_flg.set(total, 1);
                            }
                            if (itemp == 2) {
                                //ibunkatu=ibunkatu+1;
                                k_flg.add(2);//なぜかこれだと2でなくて0として追加される。20161130
                                k_flg.set(total, 2);
                            }
                            if (itemp == 3) {
                                //ibunkatu=ibunkatu+1;
                                k_flg.add(1);//なぜかこれだと2でなくて0として追加される。20161130
                                k_flg.set(total, 1);
                            }


                            if (itemp == 121) {
                                //ibunkatu=ibunkatu+1;
                                //k_flg.add(new Integer(2));
                                k_flg.add(1);
                                k_flg.set(total, 1);

                            }
                            if (itemp == 122) {
                                //ibunkatu=ibunkatu+1;
                                //k_flg.add(new Integer(2));
                                k_flg.add(1);
                                k_flg.set(total, 1);
                            }
                            if (itemp == 211) {
                                //ibunkatu=ibunkatu+1;
                                k_flg.add(2);
                                k_flg.set(total, 2);

                            }
                            if (itemp == 221) {
                                //ibunkatu=ibunkatu+1;
                                k_flg.add(2);
                                k_flg.set(total, 2);
                            }
							/*
							if (itemp==321){
								k_flg.set(j,new Integer(0));//何もしなくてもいいかも//ori_s_temp.senbun_bunkatu(s1.getb());//p1とp3が一致、siにsjが含まれる。加える折線をkousa_tenで分割すること

                                                		//k_flg.add(new Integer(2));
								//k_flg.set(sousuu,new Integer(2));

								}
							if (itemp==322){//何もしない
							}
							*/
                            if (itemp == 361) {
                                k_flg.set(j, 0);//何もしなくてもいいかも//ori_s_temp.senbun_bunkatu(s1.getb());//p1とp3が一致、siにsjが含まれる。加える折線をkousa_tenで分割すること
                                k_flg.add(2);
                                k_flg.set(total, 2);
                            }

                            if (itemp == 362) {
                                k_flg.set(j, 0);//何もしなくてもいいかも//ori_s_temp.senbun_bunkatu(s1.getb());//p1とp3が一致、siにsjが含まれる。加える折線をkousa_tenで分割すること
                                k_flg.add(2);
                                k_flg.set(total, 2);
                            }
                            if (itemp == 363) {
                                //k_flg.set(j,new Integer(0));//何もしなくてもいいかも//ori_s_temp.senbun_bunkatu(s1.getb());//p1とp3が一致、siにsjが含まれる。加える折線をkousa_tenで分割すること
                                k_flg.add(1);
                                k_flg.set(total, 1);
                            }
                            if (itemp == 364) {
                                //k_flg.set(j,new Integer(0));//何もしなくてもいいかも//ori_s_temp.senbun_bunkatu(s1.getb());//p1とp3が一致、siにsjが含まれる。加える折線をkousa_tenで分割すること
                                k_flg.add(1);
                                k_flg.set(total, 1);
                            }
                            if (itemp == 371) {
                                //k_flg.set(j,new Integer(0));//何もしなくてもいいかも//ori_s_temp.senbun_bunkatu(s1.getb());//p1とp3が一致、siにsjが含まれる。加える折線をkousa_tenで分割すること
                                k_flg.add(0);
                                k_flg.set(total, 0);
                            }
                            if (itemp == 372) {
                                //k_flg.set(j,new Integer(0));//何もしなくてもいいかも//ori_s_temp.senbun_bunkatu(s1.getb());//p1とp3が一致、siにsjが含まれる。加える折線をkousa_tenで分割すること
                                k_flg.add(0);
                                k_flg.set(total, 0);
                            }
                            if (itemp == 373) {
                                //k_flg.set(j,new Integer(0));//何もしなくてもいいかも//ori_s_temp.senbun_bunkatu(s1.getb());//p1とp3が一致、siにsjが含まれる。加える折線をkousa_tenで分割すること
                                k_flg.add(0);
                                k_flg.set(total, 0);
                            }
                            if (itemp == 374) {
                                //k_flg.set(j,new Integer(0));//何もしなくてもいいかも//ori_s_temp.senbun_bunkatu(s1.getb());//p1とp3が一致、siにsjが含まれる。加える折線をkousa_tenで分割すること
                                k_flg.add(0);
                                k_flg.set(total, 0);
                            }
							/*if (itemp==3){
								Senbun si;si= sen(i);Senbun sj;sj= sen(j);
								int i_kousa_hantei=oc.senbun_kousa_hantei(si,sj,0.000001,0.000001);//iは加える方(2)、jは元からある方(1)
								if(      i_kousa_hantei==321){k_flg.set(j,new Integer(3));//ori_s_temp.senbun_bunkatu(s1.getb());//p1とp3が一致、siにsjが含まれる。加える折線をkousa_tenで分割すること

								}else if(i_kousa_hantei==322){//ori_s.senbun_bunkatu(i , s0.getb());  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
								}else if(i_kousa_hantei==331){//ori_s_temp.senbun_bunkatu(s1.geta());//加える折線をkousa_tenで分割すること
								}else if(i_kousa_hantei==332){//ori_s.senbun_bunkatu(i , s0.getb());  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
								}else if(i_kousa_hantei==341){//ori_s_temp.senbun_bunkatu(s1.getb());//加える折線をkousa_tenで分割すること
								}else if(i_kousa_hantei==342){//ori_s.senbun_bunkatu(i , s0.geta());  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
								}else if(i_kousa_hantei==351){//ori_s_temp.senbun_bunkatu(s1.geta());//加える折線をkousa_tenで分割すること
								}else if(i_kousa_hantei==352){//ori_s.senbun_bunkatu(i , s0.geta());  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。

								}else if(i_kousa_hantei==361){//ori_s_temp.senbun_bunkatu(s1.geta()); ori_s_temp.senbun_bunkatu(s1.getb());   ori_s.setiactive(i,100);//imax=imax-1;
								}else if(i_kousa_hantei==362){//ori_s_temp.senbun_bunkatu(s1.geta()); ori_s_temp.senbun_bunkatu(s1.getb());   ori_s.setiactive(i,100);//imax=imax-1;
								}else if(i_kousa_hantei==363){//ori_s.addsenbun(s0.getb(),s1.getb(),s1.getcolor());ori_s.setb(i,s0.geta());
								}else if(i_kousa_hantei==364){//ori_s.addsenbun(s0.geta(),s1.getb(),s1.getcolor());ori_s.setb(i,s0.getb());

								}else if(i_kousa_hantei==371){//ori_s_temp.senbun_bunkatu(s1.geta());ori_s.seta(i,s0.getb());
								}else if(i_kousa_hantei==372){//ori_s_temp.senbun_bunkatu(s1.getb());ori_s.setb(i,s0.getb());
								}else if(i_kousa_hantei==373){//ori_s_temp.senbun_bunkatu(s1.getb());ori_s.setb(i,s0.geta());
								}else if(i_kousa_hantei==374){//ori_s_temp.senbun_bunkatu(s1.geta());ori_s.seta(i,s0.geta());


								}

			 				}*/
                        }
                    }
                }
            }
        }

        Memo memo_temp = new Memo();
        memo_temp.set(getMemo_iactive_jyogai(100));
        reset();
        setMemo(memo_temp);
    }

    //---------------------
    public int kousabunkatu_hayai(int i, int j) {//iは加える方(2)、jは元からある方(1)//=0 交差せず
        LineSegment si;
        si = line(i);
        LineSegment sj;
        sj = line(j);

        if (si.get_i_max_x() < sj.get_i_min_x()) {
            return 0;
        }
        if (sj.get_i_max_x() < si.get_i_min_x()) {
            return 0;
        }
        if (si.get_i_max_y() < sj.get_i_min_y()) {
            return 0;
        }
        if (sj.get_i_max_y() < si.get_i_min_y()) {
            return 0;
        }
        //		System.out.println("kousabunkatu_hayai 01");
        Point intersect_point = new Point();
        int intersect_flg0, intersect_flg1;

//ここでの「2本の線分A,Bがどのように交差するか」の考え方として（１）線分Aを直線にして、線分Bはそのまま線分とする（２）線分Bの2つの端点が共にその直線の片側にあるか、別々に直線の両側にあるかという風に考える。
//この確認がおわったら、次に線分Bを直線にして、線分Aはそのまま線分とし、同様に確認する。　以上を総合して2本の線分A,Bがどのように交差するかを考える。


        StraightLine tyoku0 = new StraightLine(si.getA(), si.getB());
        intersect_flg0 = tyoku0.lineSegment_intersect_hantei_kuwasii(sj);//senbun_kousa_hantei(Senbun s0){//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
        if (intersect_flg0 == 0) {
            return 0;
        }

        StraightLine tyoku1 = new StraightLine(sj.getA(), sj.getB());
        intersect_flg1 = tyoku1.lineSegment_intersect_hantei_kuwasii(si);
        if (intersect_flg0 == 0) {
            return 0;
        }

        // --------------------------------------
        //	X交差
        // --------------------------------------
        if ((intersect_flg0 == 1) && (intersect_flg1 == 1)) {//(intersect_flg0==1)&&(intersect_flg1==1) 加える折線と既存の折線はX型で交わる
            intersect_point.set(oc.findIntersection(tyoku0, tyoku1));
            //kousa_ten.set(oc.kouten_motome(tyoku0,tyoku1));
            //kousa_ten.set(oc.kouten_motome(tyoku0,tyoku1));

            if (((si.getColor() != 3) && (sj.getColor() != 3))
                    || ((si.getColor() == 3) && (sj.getColor() == 3))) {

                //addsenbun(kousa_ten,si.getb(),si.getcolor());
                addLine(intersect_point, si.getB(), si);
                si.setB(intersect_point);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。

                //addsenbun(kousa_ten,sj.getb(),sj.getcolor());
                addLine(intersect_point, sj.getB(), sj);

                sj.setB(intersect_point);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。

                return 1;
            }

            if ((si.getColor() == 3) && (sj.getColor() != 3)) {//加えるほうiが水色線（補助活線）、元からあるほうjが折線

                //addsenbun(kousa_ten,si.getb(),si.getcolor());
                addLine(intersect_point, si.getB(), si);

                si.setB(intersect_point);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。

                //addsenbun(kousa_ten,sj.getb(),sj.getcolor());
                //sj.setb(kousa_ten);  //j番目の線分(端点aとb)を点pで分割する。j番目の線分abをapに変え、線分pbを加える。

                return 2;
            }

            if ((si.getColor() != 3) && (sj.getColor() == 3)) {//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                //addsenbun(kousa_ten,si.getb(),si.getcolor());
                //si.setb(kousa_ten);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。

                //addsenbun(kousa_ten,sj.getb(),sj.getcolor());
                addLine(intersect_point, sj.getB(), sj);

                sj.setB(intersect_point);  //j番目の線分(端点aとb)を点pで分割する。j番目の線分abをapに変え、線分pbを加える。

                return 3;
            }
        }


        // --------------------------------------
        //	T交差(加える折線のa点で交わる)
        // --------------------------------------
        if ((intersect_flg0 == 1) && (intersect_flg1 == 21)) {//加える折線と既存の折線はT型(加える折線が縦、既存の折線が横)で交わる(縦のa点で交わる)

            Point pk = new Point();
            pk.set(oc.shadow_request(oc.lineSegmentToStraightLine(sj), si.getA()));//pkは点pの（線分を含む直線上の）影
            intersect_point.set(pk);//交差点は折線i上のs0の端点の影 20161129
            //ori_s.senbun_bunkatu(i , kousa_ten);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
            //以上で操作終了			kousa_ten.set(oc.kouten_motome(tyoku0,tyoku1));

            if (((si.getColor() != 3) && (sj.getColor() != 3))
                    || ((si.getColor() == 3) && (sj.getColor() == 3))) {
                //addsenbun(kousa_ten,sj.getb(),sj.getcolor());
                addLine(intersect_point, sj.getB(), sj);

                sj.setB(intersect_point);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
                return 121;
            }

            if ((si.getColor() == 3) && (sj.getColor() != 3)) {//加えるほうiが水色線（補助活線）、元からあるほうjが折線
                return 0;//T交差はしてるが、縦線が補助活線なので何もしないので、0でreturnする。
            }

            if ((si.getColor() != 3) && (sj.getColor() == 3)) {//加えるほうiが折線、元からあるほうjが水色線（補助活線）
                //addsenbun(kousa_ten,sj.getb(),sj.getcolor());
                addLine(intersect_point, sj.getB(), sj);

                sj.setB(intersect_point);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
                return 121;
            }
        }

        // --------------------------------------
        //	T交差(加える折線のb点で交わる)
        // --------------------------------------
        if ((intersect_flg0 == 1) && (intersect_flg1 == 22)) {//加える折線と既存の折線はT型(加える折線が縦、既存の折線が横)で交わる(縦のb点で交わる)
            Point pk = new Point();
            pk.set(oc.shadow_request(oc.lineSegmentToStraightLine(sj), si.getB()));//pkは点pの（線分を含む直線上の）影
            intersect_point.set(pk);//交差点は折線i上のs0の端点の影 20161129
            //ori_s.senbun_bunkatu(i , kousa_ten);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
            //以上で操作終了			kousa_ten.set(oc.kouten_motome(tyoku0,tyoku1));

            if (((si.getColor() != 3) && (sj.getColor() != 3))
                    || ((si.getColor() == 3) && (sj.getColor() == 3))) {
                //addsenbun(kousa_ten,sj.getb(),sj.getcolor());
                addLine(intersect_point, sj.getB(), sj);

                sj.setB(intersect_point);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
                return 122;
            }

            if ((si.getColor() == 3) && (sj.getColor() != 3)) {//加えるほうiが水色線（補助活線）、元からあるほうjが折線
                return 0;//T交差はしてるが、縦線が補助活線なので何もしないので、0でreturnする。
            }

            if ((si.getColor() != 3) && (sj.getColor() == 3)) {//加えるほうiが折線、元からあるほうjが水色線（補助活線）
                //addsenbun(kousa_ten,sj.getb(),sj.getcolor());
                addLine(intersect_point, sj.getB(), sj);

                sj.setB(intersect_point);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
                return 122;
            }
        }

        // --------------------------------------
        //	T交差(元からあった折線のa点で交わる)
        // --------------------------------------
        if ((intersect_flg0 == 21) && (intersect_flg1 == 1)) {//加える折線と既存の折線はT型(加える折線が横、既存の折線が縦)で交わる(縦のa点で交わる)
            Point pk = new Point();
            pk.set(oc.shadow_request(oc.lineSegmentToStraightLine(si), sj.getA()));//pkは点pの（線分を含む直線上の）影
            intersect_point.set(pk);//交差点は折線i上のs0の端点の影 20161129
            //ori_s.senbun_bunkatu(i , kousa_ten);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
            //以上で操作終了			kousa_ten.set(oc.kouten_motome(tyoku0,tyoku1));

            if (((si.getColor() != 3) && (sj.getColor() != 3))
                    || ((si.getColor() == 3) && (sj.getColor() == 3))) {
                //addsenbun(kousa_ten,si.getb(),si.getcolor());
                addLine(intersect_point, si.getB(), si);

                si.setB(intersect_point);
                return 211;
            }

            if ((si.getColor() == 3) && (sj.getColor() != 3)) {//加えるほうiが水色線（補助活線）、元からあるほうjが折線
                //addsenbun(kousa_ten,si.getb(),si.getcolor());
                addLine(intersect_point, si.getB(), si);

                si.setB(intersect_point);
                return 211;
            }

            if ((si.getColor() != 3) && (sj.getColor() == 3)) {//加えるほうiが折線、元からあるほうjが水色線（補助活線）
                return 0;//T交差はしてるが、縦線が補助活線なので何もしないので、0でreturnする。
            }
        }

        // --------------------------------------
        //	T交差(元からあった折線の折線のb点で交わる)
        // --------------------------------------
        if ((intersect_flg0 == 22) && (intersect_flg1 == 1)) {//加える折線と既存の折線はT型(加える折線が横、既存の折線が縦)で交わる(縦のa点で交わる)
            Point pk = new Point();
            pk.set(oc.shadow_request(oc.lineSegmentToStraightLine(si), sj.getB()));//pkは点pの（線分を含む直線上の）影
            intersect_point.set(pk);//交差点は折線i上のs0の端点の影 20161129
            //ori_s.senbun_bunkatu(i , kousa_ten);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
            //以上で操作終了			kousa_ten.set(oc.kouten_motome(tyoku0,tyoku1));

            if (((si.getColor() != 3) && (sj.getColor() != 3))
                    || ((si.getColor() == 3) && (sj.getColor() == 3))) {
                //addsenbun(kousa_ten,si.getb(),si.getcolor());
                addLine(intersect_point, si.getB(), si);

                si.setB(intersect_point);
                return 221;
            }

            if ((si.getColor() == 3) && (sj.getColor() != 3)) {//加えるほうiが水色線（補助活線）、元からあるほうjが折線
                //addsenbun(kousa_ten,si.getb(),si.getcolor());
                addLine(intersect_point, si.getB(), si);

                si.setB(intersect_point);
                return 221;
            }

            if ((si.getColor() != 3) && (sj.getColor() == 3)) {//加えるほうiが折線、元からあるほうjが水色線（補助活線）
                return 0;//T交差はしてるが、縦線が補助活線なので何もしないので、0でreturnする。
            }

        }


/*
			if(((si.getcolor()!=3)&&(sj.getcolor()!=3))
			|| ((si.getcolor()==3)&&(sj.getcolor()==3)) )	{


			}

			if((si.getcolor()==3)&&(sj.getcolor()!=3)){//加えるほうiが水色線（補助活線）、元からあるほうjが折線

			}

			if((si.getcolor()!=3)&&(sj.getcolor()==3)){//加えるほうiが折線、元からあるほうjが水色線（補助活線）

			}
*/


        // --------------------------------------
        //	加える折線と既存の折線は平行
        // --------------------------------------
        if (intersect_flg0 == 3) {//加える折線と既存の折線は同一直線上にある
            Point p1 = new Point();
            p1.set(si.getA());
            Point p2 = new Point();
            p2.set(si.getB());
            Point p3 = new Point();
            p3.set(sj.getA());
            Point p4 = new Point();
            p4.set(sj.getB());

            //setiactive(j,100)とされた折線は、kousabunkatu(int i1,int i2,int i3,int i4)の操作が戻った後で削除される。

            int i_kousa_hantei = oc.line_intersect_decide(si, sj, 0.000001, 0.000001);//iは加える方(2)、jは元からある方(1)


            if (i_kousa_hantei == 31) {// ２つの線分が全く同じ
                if ((si.getColor() == 3) && (sj.getColor() != 3)) {
                    return 0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != 3) && (sj.getColor() == 3)) {
                    return 0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                setiactive(j, 100);
                //si.seta(sj.getb());
                return 31;

            } else if (i_kousa_hantei == 321) {//(p1=p3)_p4_p2、siにsjが含まれる。
                if ((si.getColor() == 3) && (sj.getColor() != 3)) {
                    return 0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != 3) && (sj.getColor() == 3)) {
                    return 0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）
                sj.setcolor(si.getColor());
                si.setA(sj.getB());
                return 321;
            } else if (i_kousa_hantei == 322) {//(p1=p3)_p2_p4、siがsjに含まれる。
                if ((si.getColor() == 3) && (sj.getColor() != 3)) {
                    return 0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != 3) && (sj.getColor() == 3)) {
                    return 0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                sj.setA(si.getB());
                return 322;
            } else if (i_kousa_hantei == 331) {//(p1=p4)_p3_p2、siにsjが含まれる。
                if ((si.getColor() == 3) && (sj.getColor() != 3)) {
                    return 0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != 3) && (sj.getColor() == 3)) {
                    return 0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                sj.setcolor(si.getColor());
                si.setA(sj.getA());
                return 331;
            } else if (i_kousa_hantei == 332) {//(p1=p4)_p2_p3、siがsjに含まれる。
                if ((si.getColor() == 3) && (sj.getColor() != 3)) {
                    return 0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != 3) && (sj.getColor() == 3)) {
                    return 0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                sj.setB(si.getB());
                return 332;


            } else if (i_kousa_hantei == 341) {//(p2=p3)_p4_p1、siにsjが含まれる。
                if ((si.getColor() == 3) && (sj.getColor() != 3)) {
                    return 0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != 3) && (sj.getColor() == 3)) {
                    return 0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                sj.setcolor(si.getColor());
                si.setB(sj.getB());
                return 341;

            } else if (i_kousa_hantei == 342) {//(p2=p3)_p1_p4、siがsjに含まれる。
                if ((si.getColor() == 3) && (sj.getColor() != 3)) {
                    return 0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != 3) && (sj.getColor() == 3)) {
                    return 0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                sj.setA(si.getA());
                return 342;


            } else if (i_kousa_hantei == 351) {//(p2=p4)_p3_p1、siにsjが含まれる。
                if ((si.getColor() == 3) && (sj.getColor() != 3)) {
                    return 0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != 3) && (sj.getColor() == 3)) {
                    return 0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                sj.setcolor(si.getColor());
                si.setB(sj.getA());
                return 351;


            } else if (i_kousa_hantei == 352) {//(p2=p4)_p1_p3、siがsjに含まれる。
                if ((si.getColor() == 3) && (sj.getColor() != 3)) {
                    return 0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != 3) && (sj.getColor() == 3)) {
                    return 0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                sj.setB(si.getA());
                return 352;


            } else if (i_kousa_hantei == 361) {//線分(p1,p2)に線分(p3,p4)が含まれる ori_s_temp.senbun_bunkatu(s1.geta()); ori_s_temp.senbun_bunkatu(s1.getb());   ori_s.setiactive(i,100);//imax=imax-1;
                if ((si.getColor() == 3) && (sj.getColor() != 3)) {
                    return 0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != 3) && (sj.getColor() == 3)) {
                    return 0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                sj.setcolor(si.getColor());
                //addsenbun(sj.getb(),si.getb(),si.getcolor());
                addLine(sj.getB(), si.getB(), si);

                si.setB(sj.getA());
                return 361;
            } else if (i_kousa_hantei == 362) {//線分(p1,p2)に線分(p4,p3)が含まれる; ori_s_temp.senbun_bunkatu(s1.getb());   ori_s.setiactive(i,100);//imax=imax-1;
                if ((si.getColor() == 3) && (sj.getColor() != 3)) {
                    return 0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != 3) && (sj.getColor() == 3)) {
                    return 0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                sj.setcolor(si.getColor());
                //addsenbun(sj.geta(),si.getb(),si.getcolor());
                addLine(sj.getA(), si.getB(), si);

                si.setB(sj.getB());
                return 362;
            } else if (i_kousa_hantei == 363) {//線分(p3,p4)に線分(p1,p2)が含まれる ori_s.addsenbun(s0.getb(),s1.getb(),s1.getcolor());ori_s.setb(i,s0.geta());
                if ((si.getColor() == 3) && (sj.getColor() != 3)) {
                    return 0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != 3) && (sj.getColor() == 3)) {
                    return 0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                //addsenbun(si.getb(),sj.getb(),sj.getcolor());
                addLine(si.getB(), sj.getB(), sj);

                sj.setB(si.getA());
                return 363;
            } else if (i_kousa_hantei == 364) {//線分(p3,p4)に線分(p2,p1)が含まれるori_s.addsenbun(s0.geta(),s1.getb(),s1.getcolor());ori_s.setb(i,s0.getb());
                if ((si.getColor() == 3) && (sj.getColor() != 3)) {
                    return 0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != 3) && (sj.getColor() == 3)) {
                    return 0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                //addsenbun(si.geta(),sj.getb(),sj.getcolor());
                addLine(si.getA(), sj.getB(), sj);

                sj.setB(si.getB());
                return 364;


            } else if (i_kousa_hantei == 371) {//線分(p1,p2)のP2側と線分(p3,p4)のP3側が部分的に重なる//ori_s_temp.senbun_bunkatu(s1.geta());ori_s.seta(i,s0.getb());
                if ((si.getColor() == 3) && (sj.getColor() != 3)) {
                    return 0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != 3) && (sj.getColor() == 3)) {
                    return 0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                //addsenbun(p3,p2,si.getcolor());
                addLine(p3, p2, si);

                si.setB(p3);
                sj.setA(p2);
                return 371;

            } else if (i_kousa_hantei == 372) {//線分(p1,p2)のP2側と線分(p4,p3)のP4側が部分的に重なる//ori_s_temp.senbun_bunkatu(s1.getb());ori_s.setb(i,s0.getb());
                if ((si.getColor() == 3) && (sj.getColor() != 3)) {
                    return 0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != 3) && (sj.getColor() == 3)) {
                    return 0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                //addsenbun(p4,p2,si.getcolor());
                addLine(p4, p2, si);

                si.setB(p4);
                sj.setB(p2);
                return 372;

            } else if (i_kousa_hantei == 373) {//線分(p3,p4)のP4側と線分(p1,p2)のP1側が部分的に重なる//ori_s_temp.senbun_bunkatu(s1.getb());ori_s.setb(i,s0.geta());
                if ((si.getColor() == 3) && (sj.getColor() != 3)) {
                    return 0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != 3) && (sj.getColor() == 3)) {
                    return 0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                //addsenbun(p1,p4,si.getcolor());
                addLine(p1, p4, si);

                si.setA(p4);
                sj.setB(p1);
                return 373;

            } else if (i_kousa_hantei == 374) {//線分(p4,p3)のP3側と線分(p1,p2)のP1側が部分的に重なる//ori_s_temp.senbun_bunkatu(s1.geta());ori_s.seta(i,s0.geta());
                if ((si.getColor() == 3) && (sj.getColor() != 3)) {
                    return 0;
                } //加えるほうiが水色線（補助活線）、元からあるほうjが折線
                if ((si.getColor() != 3) && (sj.getColor() == 3)) {
                    return 0;
                }//加えるほうiが折線、元からあるほうjが水色線（補助活線）

                //addsenbun(p1,p3,si.getcolor());
                addLine(p1, p3, si);

                si.setA(p3);
                sj.setA(p1);
                return 374;

            }


        }
        return 0;
    }

    //---------------------
    //交差している２つの線分の交点で２つの線分を分割する。　まったく重なる線分が２つあった場合は、なんの処理もなされないまま２つとも残る。
    public void intersect_divide() {
        int ibunkatu = 1;//分割があれば1、なければ0
//System.out.println("1234567890   kousabunkatu");
        ArrayList<Integer> k_flg = new ArrayList<>();//交差分割の影響があることを示すフラッグ。

        for (int i = 0; i <= total + 1; i++) {
            k_flg.add(1);
        }

        while (ibunkatu != 0) {
            ibunkatu = 0;
            for (int i = 1; i <= total; i++) {
                Integer I_k_flag = k_flg.get(i);
                if (I_k_flag == 1) {
                    k_flg.set(i, 0);
                    for (int j = 1; j <= total; j++) {
                        if (i != j) {
                            Integer J_k_flag = k_flg.get(j);
                            if (J_k_flag == 1) {
                                int itemp;
                                int old_sousuu = total;
                                itemp = intersect_divide(i, j);
                                if (old_sousuu < total) {
                                    for (int is = old_sousuu + 1; is <= total; is++) {
                                        k_flg.add(1);
                                    }
                                }
                                if (itemp == 1) {
                                    ibunkatu = ibunkatu + 1;
                                    k_flg.set(i, 1);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

//---------------------

    //交差している２つの線分の交点で２つの線分を分割する。分割を行ったら1。行わなかったら0を返す。オリヒメ2.002から分割後の線の色も制御するようにした(重複部がある場合は一本化し、番号の遅いほうの色になる)。
    public int intersect_divide(int i, int j) {
        if (i == j) {
            return 0;
        }

        LineSegment si;
        si = line(i);
        LineSegment sj;
        sj = line(j);

        if (si.get_i_max_x() < sj.get_i_min_x()) {
            return 0;
        }//これはSenbunにi_max_xがちゃんと定義されているときでないとうまくいかない
        if (sj.get_i_max_x() < si.get_i_min_x()) {
            return 0;
        }//これはSenbunにi_min_xがちゃんと定義されているときでないとうまくいかない
        if (si.get_i_max_y() < sj.get_i_min_y()) {
            return 0;
        }//これはSenbunにi_max_yがちゃんと定義されているときでないとうまくいかない
        if (sj.get_i_max_y() < si.get_i_min_y()) {
            return 0;
        }//これはSenbunにi_min_yがちゃんと定義されているときでないとうまくいかない

        //           System.out.println("kousabunkatu("+i +","+j+")    (" 	+   si.getax() +","+   si.getay()  +")-("+  si.getbx()  +","+  si.getby()  +")---("
        //								+   sj.getax() +","+   sj.getay()  +")-("+  sj.getbx()  +","+  sj.getby()  +")    "    );
        Point p1 = new Point();
        p1.set(si.getA());
        Point p2 = new Point();
        p2.set(si.getB());
        Point p3 = new Point();
        p3.set(sj.getA());
        Point p4 = new Point();
        p4.set(sj.getB());
        Point pk = new Point();

        double ixmax;
        double ixmin;
        double iymax;
        double iymin;

        ixmax = si.getAx();
        ixmin = si.getAx();
        iymax = si.getay();
        iymin = si.getay();

        if (ixmax < si.getbx()) {
            ixmax = si.getbx();
        }
        if (ixmin > si.getbx()) {
            ixmin = si.getbx();
        }
        if (iymax < si.getby()) {
            iymax = si.getby();
        }
        if (iymin > si.getby()) {
            iymin = si.getby();
        }

        double jxmax;
        double jxmin;
        double jymax;
        double jymin;

        jxmax = sj.getAx();
        jxmin = sj.getAx();
        jymax = sj.getay();
        jymin = sj.getay();

        if (jxmax < sj.getbx()) {
            jxmax = sj.getbx();
        }
        if (jxmin > sj.getbx()) {
            jxmin = sj.getbx();
        }
        if (jymax < sj.getby()) {
            jymax = sj.getby();
        }
        if (jymin > sj.getby()) {
            jymin = sj.getby();
        }

        if (ixmax + 0.5 < jxmin) {
            return 0;
        }
        if (jxmax + 0.5 < ixmin) {
            return 0;
        }
        if (iymax + 0.5 < jymin) {
            return 0;
        }
        if (jymax + 0.5 < iymin) {
            return 0;
        }

        //  System.out.println("oc.senbun_kousa_hantei(si,sj)="+ oc.senbun_kousa_hantei(si,sj));


        if (oc.line_intersect_decide(si, sj) == 1) {
            pk.set(oc.findIntersection(si, sj));    //<<<<<<<<<<<<<<<<<<<<<<<
            si.setA(p1);
            si.setB(pk);
            sj.setA(p3);
            sj.setB(pk);
            addLine(p2, pk, si.getColor());
            addLine(p4, pk, sj.getColor());
            return 1;
        }

        //oc.senbun_kousa_hantei(si,sj)が21から24まではくの字型の交差で、なにもしない。

//		if(oc.senbun_kousa_hantei_amai(si,sj)==25){
        if (oc.line_intersect_decide(si, sj) == 25) {
            pk.set(oc.findIntersection(si, sj));    //<<<<<<<<<<<<<<<<<<<<<<<
            sj.setA(p3);
            sj.setB(pk);
            addLine(p4, pk, sj.getColor());
            return 1;
        }

//		if(oc.senbun_kousa_hantei_amai(si,sj)==26){
        if (oc.line_intersect_decide(si, sj) == 26) {
            pk.set(oc.findIntersection(si, sj));    //<<<<<<<<<<<<<<<<<<<<<<<
            sj.setA(p3);
            sj.setB(pk);
            addLine(p4, pk, sj.getColor());
            return 1;
        }

//		if(oc.senbun_kousa_hantei_amai(si,sj)==27){
        if (oc.line_intersect_decide(si, sj) == 27) {
            pk.set(oc.findIntersection(si, sj));    //<<<<<<<<<<<<<<<<<<<<<<<
            si.setA(p1);
            si.setB(pk);
            addLine(p2, pk, si.getColor());
            return 1;
        }

//		if(oc.senbun_kousa_hantei_amai(si,sj)==28){
        if (oc.line_intersect_decide(si, sj) == 28) {
            pk.set(oc.findIntersection(si, sj));    //<<<<<<<<<<<<<<<<<<<<<<<
            si.setA(p1);
            si.setB(pk);
            addLine(p2, pk, si.getColor());
            return 1;
        }

//-----------------

        if (oc.line_intersect_decide(si, sj) == 0) {//このifないと本来この後で処理されるべき条件がここで処理されてしまうことある

//System.out.println("      888888888888888888888888888");

            if (oc.distance_lineSegment(si.getA(), sj) < 0.01) {
                if (oc.lineSegment_busyo_search(si.getA(), sj, 0.01) == 3) { //20161107 わずかに届かない場合
                    pk.set(oc.findIntersection(si, sj));    //<<<<<<<<<<<<<<<<<<<<<<<
                    sj.setA(p3);
                    sj.setB(pk);
                    addLine(p4, pk, sj.getColor());
                    return 1;
                }
            }

            if (oc.distance_lineSegment(si.getB(), sj) < 0.01) {
                if (oc.lineSegment_busyo_search(si.getB(), sj, 0.01) == 3) { //20161107 わずかに届かない場合
                    pk.set(oc.findIntersection(si, sj));    //<<<<<<<<<<<<<<<<<<<<<<<
                    sj.setA(p3);
                    sj.setB(pk);
                    addLine(p4, pk, sj.getColor());
                    return 1;
                }
            }

            if (oc.distance_lineSegment(sj.getA(), si) < 0.01) {
                if (oc.lineSegment_busyo_search(sj.getA(), si, 0.01) == 3) { //20161107 わずかに届かない場合
                    pk.set(oc.findIntersection(si, sj));    //<<<<<<<<<<<<<<<<<<<<<<<
                    si.setA(p1);
                    si.setB(pk);
                    addLine(p2, pk, si.getColor());
                    return 1;
                }
            }

            if (oc.distance_lineSegment(sj.getB(), si) < 0.01) {
                if (oc.lineSegment_busyo_search(sj.getB(), si, 0.01) == 3) { //20161107 わずかに届かない場合
                    pk.set(oc.findIntersection(si, sj));    //<<<<<<<<<<<<<<<<<<<<<<<
                    si.setA(p1);
                    si.setB(pk);
                    addLine(p2, pk, si.getColor());
                    return 1;
                }
            }

        }


//-----------------

        //
        if (oc.line_intersect_decide(si, sj) == 31) {//2つの線分がまったく同じ場合は、何もしない。
            return 0;
        }


        if (oc.line_intersect_decide(si, sj) == 321) {//2つの線分の端点どうし(p1とp3)が1点で重なる。siにsjが含まれる
//System.out.println("                              321");
            si.setA(p2);
            si.setB(p4);

            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            sj.setcolor(overlapping_col);

            return 1;


        }

        if (oc.line_intersect_decide(si, sj) == 322) {//2つの線分の端点どうし(p1とp3)が1点で重なる。sjにsiが含まれる
//System.out.println("                              322");
            sj.setA(p2);
            sj.setB(p4);
            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            si.setcolor(overlapping_col);

            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 331) {//2つの線分の端点どうし(p1とp4)が1点で重なる。siにsjが含まれる
//System.out.println("                              331");
            si.setA(p2);
            si.setB(p3);

            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            sj.setcolor(overlapping_col);

            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 332) {//2つの線分の端点どうし(p1とp4)が1点で重なる。sjにsiが含まれる
//System.out.println("                              332");
            sj.setA(p2);
            sj.setB(p3);
            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            si.setcolor(overlapping_col);
            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 341) {//2つの線分の端点どうし(p2とp3)が1点で重なる。siにsjが含まれる
//System.out.println("                              341");
            si.setA(p1);
            si.setB(p4);
            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            sj.setcolor(overlapping_col);

            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 342) {//2つの線分の端点どうし(p2とp3)が1点で重なる。sjにsiが含まれる
//System.out.println("                              342");
            sj.setA(p1);
            sj.setB(p4);
            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            si.setcolor(overlapping_col);


            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 351) {//2つの線分の端点どうし(p2とp4)が1点で重なる。siにsjが含まれる
//System.out.println("                              351");

            si.setA(p1);
            si.setB(p3);

            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            sj.setcolor(overlapping_col);

            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 352) {//2つの線分の端点どうし(p2とp4)が1点で重なる。sjにsiが含まれる
//System.out.println("                              352");
            sj.setA(p1);
            sj.setB(p3);
            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            si.setcolor(overlapping_col);

            return 1;
        }


        if (oc.line_intersect_decide(si, sj) == 361) {//p1-p3-p4-p2の順
            si.setA(p1);
            si.setB(p3);

            addLine(p2, p4, si.getColor());
            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            sj.setcolor(overlapping_col);

            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 362) {//p1-p4-p3-p2の順
            si.setA(p1);
            si.setB(p4);

            addLine(p2, p3, si.getColor());

            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            sj.setcolor(overlapping_col);

            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 363) {//p3-p1-p2-p4の順
            sj.setA(p1);
            sj.setB(p3);

            addLine(p2, p4, sj.getColor());

            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            si.setcolor(overlapping_col);

            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 364) {//p3-p2-p1-p4の順
            sj.setA(p1);
            sj.setB(p4);

            addLine(p2, p3, sj.getColor());

            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            si.setcolor(overlapping_col);

            return 1;
        }

        //
        if (oc.line_intersect_decide(si, sj) == 371) {//p1-p3-p2-p4の順
            //System.out.println("371");
            si.setA(p1);
            si.setB(p3);

            sj.setA(p2);
            sj.setB(p4);

            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            addLine(p2, p3, overlapping_col);
            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 372) {//p1-p4-p2-p3の順
            //System.out.println("372");
            si.setA(p1);
            si.setB(p4);

            sj.setA(p3);
            sj.setB(p2);

            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            addLine(p2, p4, overlapping_col);
            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 373) {//p3-p1-p4-p2の順
            //System.out.println("373");
            sj.setA(p1);
            sj.setB(p3);
            si.setA(p2);
            si.setB(p4);
            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            addLine(p1, p4, overlapping_col);
            return 1;
        }

        if (oc.line_intersect_decide(si, sj) == 374) {//p4-p1-p3-p2の順
            //System.out.println("374");
            sj.setA(p1);
            sj.setB(p4);
            si.setA(p3);
            si.setB(p2);
            int overlapping_col;
            overlapping_col = si.getColor();
            if (i < j) {
                overlapping_col = sj.getColor();
            }
            addLine(p1, p3, overlapping_col);
            return 1;
        }

        return 0;
    }


    //円の追加-------------------------------


    public void addCircle(double dx, double dy, double dr, int ic) {
        Cir.add(new Circle(dx, dy, dr, ic));

        //Senbun s;s= sen(sousuu);
        //s.set(pi,pj,i_c);
    }

    public void addCircle(Point t, double dr) {
        addCircle(t.getX(), t.getY(), dr, 0);
    }


    //Generates a circle with a radius of 0 at the intersection of circles -------------------------------
    public void circle_circle_intersection(int imin, int imax, int jmin, int jmax) {
        for (int i = imin; i <= imax; i++) {
            Circle ei = new Circle();
            ei.set(cir_getEn(i));
            if (ei.getRadius() > 0.0000001) {//半径0の円は対象外
                for (int j = jmin; j <= jmax; j++) {

                    Circle ej = new Circle();
                    ej.set(cir_getEn(j));
                    if (ej.getRadius() > 0.0000001) {//半径0の円は対象外
                        if (oc.distance(ei.getCenter(), ej.getCenter()) < 0.000001) {//2つの円は同心円で交差しない
						} else if (Math.abs(oc.distance(ei.getCenter(), ej.getCenter()) - ei.getRadius() - ej.getRadius()) < 0.0001) {//2つの円は1点で交差
                            addCircle(oc.naibun(ei.getCenter(), ej.getCenter(), ei.getRadius(), ej.getRadius()), 0.0);
                        } else if (oc.distance(ei.getCenter(), ej.getCenter()) > ei.getRadius() + ej.getRadius()) {//2つの円は交差しない

						} else if (Math.abs(oc.distance(ei.getCenter(), ej.getCenter()) - Math.abs(ei.getRadius() - ej.getRadius())) < 0.0001) {//2つの円は1点で交差
                            addCircle(oc.naibun(ei.getCenter(), ej.getCenter(), -ei.getRadius(), ej.getRadius()), 0.0);
                        } else if (oc.distance(ei.getCenter(), ej.getCenter()) < Math.abs(ei.getRadius() - ej.getRadius())) {//2つの円は交差しない

						} else {//2つの円は2点で交差
                            LineSegment lineSegment = new LineSegment();
                            lineSegment.set(oc.circle_to_circle_no_intersection_wo_musubu_lineSegment(ei, ej));

                            addCircle(lineSegment.getA(), 0.0);
                            addCircle(lineSegment.getB(), 0.0);
                        }
                    }
                }
            }
        }
    }


    //円と折線の交点に半径0の円を発生-------------------------------
    public void Senbun_en_kouten(int imin, int imax, int jmin, int jmax) {
        for (int i = imin; i <= imax; i++) {
            LineSegment si;
            si = line(i);

            StraightLine ti = new StraightLine();
            ti.set(oc.lineSegmentToStraightLine(si));
            //if(ei.getr()>0.0000001){//半径0の円は対象外
            for (int j = jmin; j <= jmax; j++) {

                Circle ej = new Circle();
                ej.set(cir_getEn(j));
                if (ej.getRadius() > 0.0000001) {//半径0の円は対象外
                    double tc_kyori = ti.calculateDistance(ej.getCenter()); //直線と円の中心の距離


                    if (Math.abs(tc_kyori - ej.getRadius()) < 0.000001) {//円と直線は1点で交差
                        if (
                                Math.abs(
                                        oc.distance_lineSegment(ej.getCenter(), si) - ej.getRadius()
                                ) < 0.000001
                        ) {
                            addCircle(oc.shadow_request(ti, ej.getCenter()), 0.0);
                        }
                    } else if (tc_kyori > ej.getRadius()) {//円と直線は交差しない
					} else {//円と直線は2点で交差
                        LineSegment k_senb = new LineSegment();
                        k_senb.set(oc.circle_to_straightLine_no_kouten_wo_musubu_LineSegment(ej, ti));

                        if (oc.distance_lineSegment(k_senb.getA(), si) < 0.00001) {
                            addCircle(k_senb.getA(), 0.0);
                        }
                        if (oc.distance_lineSegment(k_senb.getB(), si) < 0.00001) {
                            addCircle(k_senb.getB(), 0.0);
                        }
                    }

                }
            }
            //}
        }
    }


    //円の削除-----------------------------------------
    public void delen(int j) {   //j番目の円を削除する
        Cir.remove(j);
    }

    //円の整理-----------------------------------------
    public int circle_organize(int i0) {//j番目の円を整理する。整理で削除したら1 、削除しないなら0を返す。
        int ies3 = en_jyoutai(i0, 3);
        int ies4 = en_jyoutai(i0, 4);
        int ies5 = en_jyoutai(i0, 5);

        if (ies3 == 100000) {
            return 0;
        }
        if (ies3 == 2) {
            return 0;
        }
        if ((ies3 == 1) && (ies4 >= 1)) {
            return 0;
        }
        if ((ies3 == 1) && (ies5 >= 1)) {
            return 0;
        }

        Cir.remove(i0);
        return 1;

    }

    //円の整理-----------------------------------------
    public void circle_organize() {//全ての円を対象に整理をする。
        for (int i = cir_size(); i >= 1; i--) {
            circle_organize(i);
        }
    }


    //円の状態表示-----------------------------------------
    public int en_jyoutai(int i0, int i_mode) {   //i番目の円の状態を示す。
        //=100000　i番目の円の半径が0でない
        //=     0　i番目の円の半径が0。他の円の円周と離れている。他の円の中心と離れている。　
        //=     1　1桁目の数字。i番目の円の半径が0で、他の半径が0の円の中心と重なっている数。2個以上と重なっているときは2と表示される。
        //=    10　2桁目の数字。i番目の円の半径が0で、他の半径が0でない円の中心と重なっている数。2個以上と重なっているときは2と表示される。
        //=   100　3桁目の数字。i番目の円の半径が0で、他の半径が0でない円の円周と重なっている数。2個以上と重なっているときは2と表示される。
        //=  1000　4桁目の数字。i番目の円の半径が0で、他の折線と重なっている数。2個以上と重なっているときは2と表示される。
        //= 10000　5桁目の数字。i番目の円の半径が0で、他の補助活線と重なっている数。2個以上と重なっているときは2と表示される。
        Circle e_temp = new Circle();
        e_temp.set(cir_getEn(i0));
        double er_0 = e_temp.getRadius();
        Point ec_0 = new Point();
        ec_0.set(e_temp.getCenter());

        double er_1;
        Point ec_1 = new Point();

        int ir1 = 0;
        int ir2 = 0;
        int ir3 = 0;
        int ir4 = 0;
        int ir5 = 0;


        if (er_0 < 0.0000001) {
            for (int i = 1; i <= cir_size(); i++) {
                if (i != i0) {
                    e_temp.set(cir_getEn(i));
                    er_1 = e_temp.getRadius();
                    ec_1.set(e_temp.getCenter());
                    if (er_1 < 0.0000001) {//他の円の半径が0
                        if (ec_0.distance(ec_1) < 0.0000001) {
                            ir1 = ir1 + 1;
                        }
                    } else {//他の円の半径が0でない
                        if (ec_0.distance(ec_1) < 0.0000001) {
                            ir2 = ir2 + 1;
                        } else if (Math.abs(ec_0.distance(ec_1) - er_1) < 0.0000001) {
                            ir3 = ir3 + 1;
                        }
                    }
                }
            }

            for (int i = 1; i <= total; i++) {
                LineSegment si;
                si = line(i);
                if (oc.distance_lineSegment(ec_0, si) < 0.000001) {

                    if (si.getColor() <= 2) {
                        ir4 = ir4 + 1;
                    } else if (si.getColor() == 3) {
                        ir5 = ir5 + 1;
                    }

                }
            }


            if (ir1 > 2) {
                ir1 = 2;
            }
            if (ir2 > 2) {
                ir2 = 2;
            }
            if (ir3 > 2) {
                ir3 = 2;
            }
            if (ir4 > 2) {
                ir4 = 2;
            }
            if (ir5 > 2) {
                ir5 = 2;
            }


            if (i_mode == 0) {
                return ir1 + ir2 * 10 + ir3 * 100 + ir4 * 1000 + ir5 * 10000;
            }
            if (i_mode == 1) {
                return ir1;
            }
            if (i_mode == 2) {
                return ir2;
            }
            if (i_mode == 3) {
                return ir3;
            }
            if (i_mode == 4) {
                return ir4;
            }
            if (i_mode == 5) {
                return ir5;
            }

        }

        return 100000;
    }


    //線分の追加-------------------------------
    public void addLine(Point pi, Point pj, int i_c) {
        total++;

        LineSegment s;
        s = line(total);
        s.set(pi, pj, i_c);
    }

    //線分の追加-------------------------------wwwwwwwwww
    public void addLine(Point pi, Point pj, LineSegment s0) {//Ten piからTen pjまでの線分を追加。この追加する線分のその他のパラメータはs0と同じ
        total++;

        LineSegment s;
        s = line(total);
        //s.set(pi,pj,s0.getcolor());
        s.set(s0);
        s.set(pi, pj);

    }

    //線分の追加-------------------------------
    public void addLine(Point pi, Point pj, int i_c, int i_a) {
        total++;

        LineSegment s;
        s = line(total);
        s.set(pi, pj, i_c, i_a);
    }

    //線分の追加-------------------------------
    public void addLine(double ax, double ay, double bx, double by, int ic) {
        total++;

        LineSegment s;
        s = line(total);
        s.set(ax, ay, bx, by, ic);
    }

    //線分の追加-------------------------------
    public void addLine(Point pi, Point pj) {
        total++;

        LineSegment s;
        s = line(total);

        s.setA(pi);
        s.setB(pj);
    }

    //線分の追加-------------------------------
    public void addLine(LineSegment s0) {
        //addsenbun(s0.geta(),s0.getb(),s0.getcolor());//20181110コメントアウト
        addLine(s0.getA(), s0.getB(), s0.getColor(), s0.getiactive());//20181110追加
    }

    //線分の削除-----------------------------------------
    public void deleteLine(int j) {   //j番目の線分を削除する  このsi= sen(i)は大丈夫なのだろうか????????si= sen(i)　20161106
        for (int i = j; i <= total - 1; i++) {
            LineSegment si;
            si = line(i);
            LineSegment si1;
            si1 = line(i + 1);
            si.set(si1);

        }
        total--;
    }


    //線分の分割-----------------qqqqq------------------------
    public void senbun_bunkatu(int i, Point p) {   //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。

        LineSegment s1 = new LineSegment(p, getB(i));//i番目の線分abをapに変える前に作っておく
        int i_c;
        i_c = getcolor(i);

        setb(i, p);//i番目の線分abをapに変える

        addLine(s1);
        setcolor(getTotal(), i_c);
    }

/*
	public void senbun_binkatu(int i,Ten p){   //pとi番目の線分の端点aとの線分を加え、pとi番目の線分の端点bとの線分を加え、i番目の線分を削除する
		int i_c;i_c=getcolor(i);
		Senbun s1 =new Senbun(geta(i),p);
		Senbun s2 =new Senbun(getb(i),p);
		delsenbun(i);
		addsenbun(s1);setcolor(getsousuu(),i_c);
		addsenbun(s2);setcolor(getsousuu(),i_c);
	}
*/

    //i番目の線分の長さを得る---------------------------
    public double getnagasa(int i) {
        LineSegment s;
        s = line(i);
        return s.getLength();
    }

    //Remove the branching line segments without forming a closed polygon.
    public void branch_trim(double r) {
        int iflga = 0;
        int iflgb = 0;
        for (int i = 1; i <= total; i++) {
            iflga = 0;
            iflgb = 0;
            LineSegment si;
            si = line(i);
            for (int j = 1; j <= total; j++) {
                if (i != j) {
                    LineSegment sj;
                    sj = line(j);
                    if (oc.distance(si.getA(), sj.getA()) < r) {
                        iflga = 1;
                    }
                    if (oc.distance(si.getA(), sj.getB()) < r) {
                        iflga = 1;
                    }
                    if (oc.distance(si.getB(), sj.getA()) < r) {
                        iflgb = 1;
                    }
                    if (oc.distance(si.getB(), sj.getB()) < r) {
                        iflgb = 1;
                    }
                }
            }

            if ((iflga == 0) || (iflgb == 0)) {
                delsenbun_vertex(i);
                i = 1;
            }
        }
    }

    //-----------------------------------------------
    public void delsenbun_vertex(int i) {//i番目の折線を消すとき、その折線の端点も消せる場合は消す
        Point pa = new Point();
        pa.set(getA(i));
        Point pb = new Point();
        pb.set(getB(i));
        deleteLine(i);

        del_V(pa, 0.000001, 0.000001);
        del_V(pb, 0.000001, 0.000001);
    }

//----------------------------------------------------


    //一本だけの離れてある線分を削除する。
    public void tanSenbun_sakujyo(double r) {
        int iflg = 0;
        for (int i = 1; i <= total; i++) {
            iflg = 0;
            LineSegment si;
            si = line(i);
            for (int j = 1; j <= total; j++) {
                if (i != j) {
                    LineSegment sj;
                    sj = line(j);
                    if (oc.distance(si.getA(), sj.getA()) < r) {
                        iflg = 1;
                    }
                    if (oc.distance(si.getB(), sj.getB()) < r) {
                        iflg = 1;
                    }
                    if (oc.distance(si.getA(), sj.getB()) < r) {
                        iflg = 1;
                    }
                    if (oc.distance(si.getB(), sj.getA()) < r) {
                        iflg = 1;
                    }
                }
            }

            if (iflg == 0) {
                deleteLine(i);
                i = 1;
            }
        }
    }


    //点pに近い(r以内)線分をさがし、その番号を返す関数(ただし、j番目の線分は対象外)。近い線分がなければ、0を返す---------------------------------
    //もし対象外にする線分が無い場合は、jを0とか負の整数とかにする。
    //070317　追加機能　j　が　-10　の時は　活性化していない枝（getiactive(i)が0）を対象にする。

    public int lineSegment_search(Point p, double r, int j) {
        if (j == -10) {
            for (int i = 1; i <= total; i++) {
                if (((lineSegment_position_search(i, p, r) == 1) && (i != j)) && (getiactive(i) == 0)) {
                    return i;
                }
            }
            for (int i = 1; i <= total; i++) {
                if (((lineSegment_position_search(i, p, r) == 2) && (i != j)) && (getiactive(i) == 0)) {
                    return i;
                }
            }
            for (int i = 1; i <= total; i++) {
                if (((lineSegment_position_search(i, p, r) == 3) && (i != j)) && (getiactive(i) == 0)) {
                    return i;
                }
            }
            return 0;
        }

        for (int i = 1; i <= total; i++) {
            if ((lineSegment_position_search(i, p, r) == 1) && (i != j)) {
                return i;
            }
        }
        for (int i = 1; i <= total; i++) {
            if ((lineSegment_position_search(i, p, r) == 2) && (i != j)) {
                return i;
            }
        }
        for (int i = 1; i <= total; i++) {
            if ((lineSegment_position_search(i, p, r) == 3) && (i != j)) {
                return i;
            }
        }
        return 0;
    }

    // A function that determines where the point p is close to the specified line segment (within r) ------------------------ ---------
    // 0 = not close, 1 = close to point a, 2 = close to point b, 3 = close to handle
    public int lineSegment_position_search(int i, Point p, double r) {
        if (r > oc.distance(p, getA(i))) {
            return 1;
        }//a点に近いかどうか
        if (r > oc.distance(p, getB(i))) {
            return 2;
        }//b点に近いかどうか
        if (r > oc.distance_lineSegment(p, get(i))) {
            return 3;
        }//柄の部分に近いかどうか
        return 0;
    }


    //点pに最も近い円（円周と中心の両方を考慮する）の番号を返す
    public int mottomo_tikai_en_search(Point p) {


        int minrid = 0;
        double minr = 100000;
        double rtemp;
        Point p_temp = new Point();
        for (int i = 1; i <= cir_size(); i++) {
            Circle e_temp = new Circle();
            e_temp.set(cir_getEn(i));


            rtemp = p.distance(e_temp.getCenter());
            if (minr > rtemp) {
                minr = rtemp;
                minrid = i;
            }

            rtemp = Math.abs(p.distance(e_temp.getCenter()) - e_temp.getRadius());
            if (minr > rtemp) {
                minr = rtemp;
                minrid = i;
            }

        }

        return minrid;

    }


    //点pに最も近い円の番号を逆順で（番号の大きいほうが優先という意味）探して返す
    public int mottomo_tikai_circle_search_gyakujyun(Point p) {
        int minrid = 0;
        double minr = 100000;
        double rtemp;
        Point p_temp = new Point();
        for (int i = 1; i <= cir_size(); i++) {
            Circle e_temp = new Circle();
            e_temp.set(cir_getEn(i));


            rtemp = p.distance(e_temp.getCenter());
            if (minr >= rtemp) {
                minr = rtemp;
                minrid = i;
            }

            rtemp = Math.abs(p.distance(e_temp.getCenter()) - e_temp.getRadius());
            if (minr >= rtemp) {
                minr = rtemp;
                minrid = i;
            }

        }

        return minrid;

    }


    //点pに最も近い円の番号での、その距離を返す
    public double mottomo_tikai_en_kyori(Point p) {
        int minrid = 0;
        double minr = 100000;
        double rtemp;
        Point p_temp = new Point();
        for (int i = 1; i <= cir_size(); i++) {
            Circle e_temp = new Circle();
            e_temp.set(cir_getEn(i));


            rtemp = p.distance(e_temp.getCenter());
            if (minr > rtemp) {
                minr = rtemp;
                minrid = i;
            }

            rtemp = Math.abs(p.distance(e_temp.getCenter()) - e_temp.getRadius());
            if (minr > rtemp) {
                minr = rtemp;
                minrid = i;
            }

        }

        return minr;
    }


    //点pに最も近い線分の番号を返す
    public int mottomo_tikai_lineSegment_search(Point p) {
        int minrid = 0;
        double minr = 100000;
        for (int i = 1; i <= total; i++) {
            double sk = oc.distance_lineSegment(p, get(i));
            if (minr > sk) {
                minr = sk;
                minrid = i;
            }//柄の部分に近いかどうか

        }
        return minrid;
    }


    //点pに最も近い線分の番号を逆から（番号の大きいほうから小さいほうへという意味）探して返す
    public int mottomo_tikai_lineSegment_search_gyakujyun(Point p) {
        int minrid = 0;
        double minr = 100000;
        for (int i = total; i >= 1; i--) {
            double sk = oc.distance_lineSegment(p, get(i));
            if (minr > sk) {
                minr = sk;
                minrid = i;
            }//柄の部分に近いかどうか

        }
        return minrid;
    }


    //点pに最も近い線分の番号での、その距離を返す
    public double mottomo_tikai_senbun_kyori(Point p) {
        int minrid = 0;
        double minr = 100000.0;
        for (int i = 1; i <= total; i++) {
            double sk = oc.distance_lineSegment(p, get(i));
            if (minr > sk) {
                minr = sk;
                minrid = i;
            }//柄の部分に近いかどうか

        }
        return minr;
    }


    //点pに最も近い線分の番号での、その距離を返す。ただし線分s0と平行な折線が調査の対象外。つまり、平行な折線が重なっていても近い距離にあるとはみなされない。
    public double mottomo_tikai_senbun_kyori_heikou_jyogai(Point p, LineSegment s0) {
        double minr = 100000.0;
        for (int i = 1; i <= total; i++) {
            if (oc.parallel_judgement(get(i), s0, 0.0001) == 0) {

                double sk = oc.distance_lineSegment(p, get(i));
                if (minr > sk) {
                    minr = sk;
                }
            }
        }
        return minr;
    }


    public Circle mottomo_tikai_ensyuu(Point p) {
        int minrid = 0;
        double minr = 100000.0;
        Circle e1 = new Circle(100000.0, 100000.0, 1.0, 0);
        for (int i = 1; i <= cir_size(); i++) {
            double ek = oc.distance_circumference(p, cir_getEn(i));
            if (minr > ek) {
                minr = ek;
                minrid = i;
            }//円周の部分に近いかどうか
        }

        if (minrid == 0) {
            return e1;
        }

        return cir_getEn(minrid);
    }


    public LineSegment mottomo_tikai_Senbun(Point p) {
        int minrid = 0;
        double minr = 100000.0;
        LineSegment s1 = new LineSegment(100000.0, 100000.0, 100000.0, 100000.1);
        for (int i = 1; i <= total; i++) {
            double sk = oc.distance_lineSegment(p, get(i));
            if (minr > sk) {
                minr = sk;
                minrid = i;
            }//柄の部分に近いかどうか

        }

        if (minrid == 0) {
            return s1;
        }

        return get(minrid);
    }


    //点pに最も近い、「線分の端点」を返す
    //public Ten mottomo_tikai_Ten_sagasi(Ten p) {
    public Point mottomo_tikai_Ten(Point p) {
        Point p_return = new Point();
        p_return.set(100000.0, 100000.0);
        Point p_temp = new Point();
        for (int i = 1; i <= total; i++) {
            //p_temp.set(geta(i));if(p.kyori(p_temp)<p.kyori(p_return) ) {p_return.set(p_temp.etx(),p_temp.gety()); }
            //p_temp.set(getb(i));if(p.kyori(p_temp)<p.kyori(p_return) ) {p_return.set(p_temp.getx(),p_temp.gety()); }
            p_temp.set(getA(i));
            if (p.distanceSquared(p_temp) < p.distanceSquared(p_return)) {
                p_return.set(p_temp.getX(), p_temp.getY());
            }
            p_temp.set(getB(i));
            if (p.distanceSquared(p_temp) < p.distanceSquared(p_return)) {
                p_return.set(p_temp.getX(), p_temp.getY());
            }

        }
        return p_return;
    }

    //点pに最も近い、「円の中心点」を返す
    //public Ten mottomo_tikai_Tyuusin(Ten p) {   //qqqqqqqqqqqq
    public Point mottomo_tikai_Tyuusin(Point p) {
        Point p_return = new Point();
        p_return.set(100000.0, 100000.0);
        Point p_temp = new Point();
        for (int i = 1; i <= cir_size(); i++) {
            Circle e_temp = new Circle();
            e_temp.set(cir_getEn(i));
            p_temp.set(e_temp.getCenter());
            if (p.distanceSquared(p_temp) < p.distanceSquared(p_return)) {
                p_return.set(p_temp.getX(), p_temp.getY());
            }
        }
        return p_return;
    }

    //点pに最も近い、「線分の端点」を返す。ただし、補助活線は対象外
    public Point mottomo_tikai_Ten_with_icol_0_1_2(Point p) {
        Point p_return = new Point();
        p_return.set(100000.0, 100000.0);
        Point p_temp = new Point();
        for (int i = 1; i <= total; i++) {
            if ((0 <= getcolor(i)) && (getcolor(i) <= 2)) {
                p_temp.set(getA(i));
                if (p.distanceSquared(p_temp) < p.distanceSquared(p_return)) {
                    p_return.set(p_temp.getX(), p_temp.getY());
                }
                p_temp.set(getB(i));
                if (p.distanceSquared(p_temp) < p.distanceSquared(p_return)) {
                    p_return.set(p_temp.getX(), p_temp.getY());
                }

            }
        }
        return p_return;
    }

    // ---------------------------
    public void del_V(int i, int j) {//2本の折線が同じ色で、他の折線の端点がない場合に点消しをする
        //if(getcolor(i)!=getcolor(j)){return;}//2本が同じ色でないなら実施せず

        int i_senbun_kousa_hantei;
        i_senbun_kousa_hantei = oc.line_intersect_decide(get(i), get(j), 0.00001, 0.00001);

        LineSegment addsen = new LineSegment();
        int i_ten = 0;
        if (i_senbun_kousa_hantei == 323) {
            addsen.set(getB(i), getB(j));
            i_ten = tyouten_syuui_sensuu(getA(i), 0.00001);
        }
        if (i_senbun_kousa_hantei == 333) {
            addsen.set(getB(i), getA(j));
            i_ten = tyouten_syuui_sensuu(getA(i), 0.00001);
        }
        if (i_senbun_kousa_hantei == 343) {
            addsen.set(getA(i), getB(j));
            i_ten = tyouten_syuui_sensuu(getB(i), 0.00001);
        }
        if (i_senbun_kousa_hantei == 353) {
            addsen.set(getA(i), getA(j));
            i_ten = tyouten_syuui_sensuu(getB(i), 0.00001);
        }

        //System.out.println("i_senbun_kousa_hantei="+ i_senbun_kousa_hantei+"---i_ten_"+i_ten);
        if (i_ten == 2) {

            int i_c = 0;
            if ((getcolor(i) == 0) && (getcolor(j) == 0)) {
                i_c = 0;
            }
            if ((getcolor(i) == 0) && (getcolor(j) == 1)) {
                i_c = 1;
            }
            if ((getcolor(i) == 0) && (getcolor(j) == 2)) {
                i_c = 2;
            }
            if ((getcolor(i) == 0) && (getcolor(j) == 3)) {
                return;
            }

            if ((getcolor(i) == 1) && (getcolor(j) == 0)) {
                i_c = 1;
            }
            if ((getcolor(i) == 1) && (getcolor(j) == 1)) {
                i_c = 1;
            }
            if ((getcolor(i) == 1) && (getcolor(j) == 2)) {
                i_c = 0;
            }
            if ((getcolor(i) == 1) && (getcolor(j) == 3)) {
                return;
            }

            if ((getcolor(i) == 2) && (getcolor(j) == 0)) {
                i_c = 2;
            }
            if ((getcolor(i) == 2) && (getcolor(j) == 1)) {
                i_c = 0;
            }
            if ((getcolor(i) == 2) && (getcolor(j) == 2)) {
                i_c = 2;
            }
            if ((getcolor(i) == 2) && (getcolor(j) == 3)) {
                return;
            }

            if ((getcolor(i) == 3) && (getcolor(j) == 0)) {
                return;
            }
            if ((getcolor(i) == 3) && (getcolor(j) == 1)) {
                return;
            }
            if ((getcolor(i) == 3) && (getcolor(j) == 2)) {
                return;
            }
            //if((getcolor(i)==3) && (getcolor(j)==3) ){i_c=3;}
            if ((getcolor(i) == 3) && (getcolor(j) == 3)) {
                return;
            }

            deleteLine(j);
            deleteLine(i);
            addLine(addsen);
            setcolor(getTotal(), i_c);
        }//p2,p1,p4 ixb_ixa,iya_iyb

    }

// ---------------------------


    public void del_V_all() {

        int sousuu_old = total + 1;
        while (total < sousuu_old) {
            sousuu_old = total;
            for (int i = 1; i <= total - 1; i++) {
                for (int j = i + 1; j <= total; j++) {
                    if (getcolor(i) == getcolor(j)) {//2本が同じ色なら実施
                        if (getcolor(i) != 3) {//補助活線は対象外
                            del_V(i, j);
                        }
                    }
                }
            }

        }

    }

    public void del_V_all_cc() {
        int sousuu_old = total + 1;
        while (total < sousuu_old) {
            sousuu_old = total;
            for (int i = 1; i <= total - 1; i++) {
                for (int j = i + 1; j <= total; j++) {
                    del_V(i, j);
                }
            }

        }
    }


    //点Qを指定し、線分AQとQCを削除し線分ACを追加（ただしQを端点とするのは2本の線分のみ）//実施したときは1、何もしなかったときは0を返す。
//手順は（１）マウスクリックで点pが決まる。
//（２）点p最も近い展開図に含まれる端点qが決まる。
//（３）展開図中の折線でその端点のうち、qに近いほうと、qとの距離がr以下の場合、その折線は点qと連結しているとして
//
//
//
//
//
//
//
//
    int[] i_s = new int[2];//この変数はdel_Vとtyouten_syuui_sensuuとで共通に使う。tyouten_syuui_sensuuで、頂点回りの折線数が2のときにその2折線の番号を入れる変数。なお、折線数が3以上のときは意味を成さない。//qを端点とする2本の線分の番号

    public int del_V(Point p, double hikiyose_hankei, double r) {
        int i_return;
        i_return = 0;

        Point q = new Point();
        q.set(mottomo_tikai_Ten(p));//qは点pに近い方の端点
        if (q.distanceSquared(p) > hikiyose_hankei * hikiyose_hankei) {
            return 0;
        }
        Point p_temp = new Point();

        if (tyouten_syuui_sensuu_for_del_V(q, r) == 2) {
            //int i_s[] = new int[2];//qを端点とする2本の線分の番号
/*
			i_s[0]=0;i_s[1]=0;
			int i_temp;i_temp=0;
			for(int i=1;i<=sousuu;i++){
				p_temp.set(geta(i));if(q.kyori(getb(i))<q.kyori(geta(i)) ) {p_temp.set(getb(i)); }
				if(q.kyori(p_temp)<r) {
					if(i_temp==2){return 0;}//ここの場合i_temp==2とは、すでに2本の折線が見つかっているところに3本目も見つかったということになっている。
					i_s[i_temp]=i; i_temp=i_temp+1;
				}
			}
			if(i_temp!=2){return 0;}//ここの場合i_temp==2とは、折線が2本だけ見つかったということで、望ましい結果
*/
            int ix, iy;
            ix = i_s[0];
            iy = i_s[1];
            int i_hantei;
            i_hantei = 0;//i_hanteiは１なら2線分は重ならず、直線状に繋がっている
            int i_senbun_kousa_hantei;
            i_senbun_kousa_hantei = oc.line_intersect_decide(get(ix), get(iy), 0.000001, 0.000001);

            if (i_senbun_kousa_hantei == 323) {
                i_hantei = 1;
            }
            if (i_senbun_kousa_hantei == 333) {
                i_hantei = 1;
            }
            if (i_senbun_kousa_hantei == 343) {
                i_hantei = 1;
            }
            if (i_senbun_kousa_hantei == 353) {
                i_hantei = 1;
            }


            System.out.println("i_senbun_kousa_hantei=" + i_senbun_kousa_hantei + "---tyouten_syuui_sensuu_for_del_V(q,r)_" + tyouten_syuui_sensuu_for_del_V(q, r));
            if (i_hantei == 0) {
                return 0;
            }


            if (getcolor(ix) != getcolor(iy)) {
                return 0;
            }//2本が同じ色でないなら実施せず

            int i_c;
            i_c = getcolor(ix);

            LineSegment s_ixb_iyb = new LineSegment(getB(ix), getB(iy));
            LineSegment s_ixb_iya = new LineSegment(getB(ix), getA(iy));
            LineSegment s_ixa_iyb = new LineSegment(getA(ix), getB(iy));
            LineSegment s_ixa_iya = new LineSegment(getA(ix), getA(iy));


            if (i_senbun_kousa_hantei == 323) {
                deleteLine(iy);
                deleteLine(ix);
                addLine(s_ixb_iyb);
                setcolor(getTotal(), i_c);
            }//p2,p1,p4 ixb_ixa,iya_iyb
            if (i_senbun_kousa_hantei == 333) {
                deleteLine(iy);
                deleteLine(ix);
                addLine(s_ixb_iya);
                setcolor(getTotal(), i_c);
            }//p2,p1,p3 ixb_ixa,iyb_iya
            if (i_senbun_kousa_hantei == 343) {
                deleteLine(iy);
                deleteLine(ix);
                addLine(s_ixa_iyb);
                setcolor(getTotal(), i_c);
            }//p1,p2,p4 ixa_ixb,iya_iyb
            if (i_senbun_kousa_hantei == 353) {
                deleteLine(iy);
                deleteLine(ix);
                addLine(s_ixa_iya);
                setcolor(getTotal(), i_c);
            }//p1,p2,p3 ixa_ixb,iyb_iya
            //
            //	addsenbun(s1);setcolor(getsousuu(),i_c);
            //	addsenbun(s2);setcolor(getsousuu(),i_c);


        }


        return 0;
    }


// -------------------------------------------------------------------------------------------------------------

    public int del_V_cc(Point p, double hikiyose_hankei, double r) {//2つの折線の色が違った場合カラーチェンジして、点削除する。黒赤は赤赤、黒青は青青、青赤は黒にする
        int i_return;
        i_return = 0;

        Point q = new Point();
        q.set(mottomo_tikai_Ten(p));//qは点pに近い方の端点
        if (q.distanceSquared(p) > hikiyose_hankei * hikiyose_hankei) {
            return 0;
        }
        Point p_temp = new Point();

        if (tyouten_syuui_sensuu_for_del_V(q, r) == 2) {


            int ix, iy;
            ix = i_s[0];
            iy = i_s[1];
            int i_hantei;
            i_hantei = 0;//i_hanteiは１なら2線分は重ならず、直線状に繋がっている
            int i_senbun_kousa_hantei;
            i_senbun_kousa_hantei = oc.line_intersect_decide(get(ix), get(iy), 0.000001, 0.000001);

            if (i_senbun_kousa_hantei == 323) {
                i_hantei = 1;
            }
            if (i_senbun_kousa_hantei == 333) {
                i_hantei = 1;
            }
            if (i_senbun_kousa_hantei == 343) {
                i_hantei = 1;
            }
            if (i_senbun_kousa_hantei == 353) {
                i_hantei = 1;
            }
            if (i_hantei == 0) {
                return 0;
            }

            //if(getcolor(ix)!=getcolor(iy)){return 0;}//2本が同じ色でないなら実施せず

            if ((getcolor(ix) == 0) && (getcolor(iy) == 0)) {
                setcolor(ix, 0);
                setcolor(iy, 0);
            }
            if ((getcolor(ix) == 0) && (getcolor(iy) == 1)) {
                setcolor(ix, 1);
                setcolor(iy, 1);
            }
            if ((getcolor(ix) == 0) && (getcolor(iy) == 2)) {
                setcolor(ix, 2);
                setcolor(iy, 2);
            }
            if ((getcolor(ix) == 0) && (getcolor(iy) == 3)) {
                return 0;
            }

            if ((getcolor(ix) == 1) && (getcolor(iy) == 0)) {
                setcolor(ix, 1);
                setcolor(iy, 1);
            }
            if ((getcolor(ix) == 1) && (getcolor(iy) == 1)) {
                setcolor(ix, 1);
                setcolor(iy, 1);
            }
            if ((getcolor(ix) == 1) && (getcolor(iy) == 2)) {
                setcolor(ix, 0);
                setcolor(iy, 0);
            }
            if ((getcolor(ix) == 1) && (getcolor(iy) == 3)) {
                return 0;
            }

            if ((getcolor(ix) == 2) && (getcolor(iy) == 0)) {
                setcolor(ix, 2);
                setcolor(iy, 2);
            }
            if ((getcolor(ix) == 2) && (getcolor(iy) == 1)) {
                setcolor(ix, 0);
                setcolor(iy, 0);
            }
            if ((getcolor(ix) == 2) && (getcolor(iy) == 2)) {
                setcolor(ix, 2);
                setcolor(iy, 2);
            }
            if ((getcolor(ix) == 2) && (getcolor(iy) == 3)) {
                return 0;
            }

            if ((getcolor(ix) == 3) && (getcolor(iy) == 0)) {
                return 0;
            }
            if ((getcolor(ix) == 3) && (getcolor(iy) == 1)) {
                return 0;
            }
            if ((getcolor(ix) == 3) && (getcolor(iy) == 2)) {
                return 0;
            }
            if ((getcolor(ix) == 3) && (getcolor(iy) == 3)) {
                setcolor(ix, 3);
                setcolor(iy, 3);
            }


            int i_c;
            i_c = getcolor(ix);

            LineSegment s_ixb_iyb = new LineSegment(getB(ix), getB(iy));
            LineSegment s_ixb_iya = new LineSegment(getB(ix), getA(iy));
            LineSegment s_ixa_iyb = new LineSegment(getA(ix), getB(iy));
            LineSegment s_ixa_iya = new LineSegment(getA(ix), getA(iy));


            if (i_senbun_kousa_hantei == 323) {
                deleteLine(iy);
                deleteLine(ix);
                addLine(s_ixb_iyb);
                setcolor(getTotal(), i_c);
            }//p2,p1,p4 ixb_ixa,iya_iyb
            if (i_senbun_kousa_hantei == 333) {
                deleteLine(iy);
                deleteLine(ix);
                addLine(s_ixb_iya);
                setcolor(getTotal(), i_c);
            }//p2,p1,p3 ixb_ixa,iyb_iya
            if (i_senbun_kousa_hantei == 343) {
                deleteLine(iy);
                deleteLine(ix);
                addLine(s_ixa_iyb);
                setcolor(getTotal(), i_c);
            }//p1,p2,p4 ixa_ixb,iya_iyb
            if (i_senbun_kousa_hantei == 353) {
                deleteLine(iy);
                deleteLine(ix);
                addLine(s_ixa_iya);
                setcolor(getTotal(), i_c);
            }//p1,p2,p3 ixa_ixb,iyb_iya

        }


        return 0;
    }


    //点pに最も近い線分の、点pに近い方の端点を、頂点とした場合、何本の線分が出ているか（頂点とr以内に端点がある線分の数）//del_V用の関数
    public int tyouten_syuui_sensuu_for_del_V(Point p, double r) {//del_V用の関数

        Point q = new Point();
        q.set(mottomo_tikai_Ten(p));//qは点pに近い方の端点
        Point p_temp = new Point();


        int i_return;
        i_return = 0;
        int i_temp;
        i_temp = 1;//ここのi_tempはi_temp=1-i_tempの形でつかうので、0,1,0,1,0,1,,,という風に変化していく
        for (int i = 1; i <= total; i++) {
            p_temp.set(getA(i));
            if (q.distanceSquared(getB(i)) < q.distanceSquared(getA(i))) {
                p_temp.set(getB(i));
            }
            if (q.distanceSquared(p_temp) < r * r) {
                i_temp = 1 - i_temp;
                i_s[i_temp] = i;
                i_return = i_return + 1;
            }

        }

        return i_return;
    }
//--------------------------------------------


//--------------------------------------------


    //点pに最も近い線分の、点pに近い方の端点を、頂点とした場合、何本の線分が出ているか（頂点とr以内に端点がある線分の数）
    public int tyouten_syuui_sensuu(Point p, double r) {

        Point q = new Point();
        q.set(mottomo_tikai_Ten(p));//qは点pに近い方の端点
        Point p_temp = new Point();


        int i_return;
        i_return = 0;

        for (int i = 1; i <= total; i++) {
            p_temp.set(getA(i));
            if (q.distanceSquared(getB(i)) < q.distanceSquared(getA(i))) {
                p_temp.set(getB(i));
            }


            if (q.distanceSquared(p_temp) < r * r) {
                i_return = i_return + 1;
            }


        }

        return i_return;
    }
//--------------------------------------------


    //点pに最も近い線分の、点pに近い方の端点を、頂点とした場合、何本の赤い線分が出ているか（頂点とr以内に端点がある線分の数）
    public int tyouten_syuui_sensuu_red(Point p, double r) {

        Point q = new Point();
        q.set(mottomo_tikai_Ten(p));//qは点pに近い方の端点
        Point p_temp = new Point();


        int i_return;
        i_return = 0;

        for (int i = 1; i <= total; i++) {
            p_temp.set(getA(i));
            if (q.distanceSquared(getB(i)) < q.distanceSquared(getA(i))) {
                p_temp.set(getB(i));
            }
            if (q.distanceSquared(p_temp) < r * r) {
                if (getcolor(i) == 1) {
                    i_return = i_return + 1;
                }
            }

        }

        return i_return;
    }

    //--------------------------------------------
    //点pに最も近い線分の、点pに近い方の端点を、頂点とした場合、何本の青い線分が出ているか（頂点とr以内に端点がある線分の数）
    public int tyouten_syuui_sensuu_blue(Point p, double r) {

        Point q = new Point();
        q.set(mottomo_tikai_Ten(p));//qは点pに近い方の端点
        Point p_temp = new Point();


        int i_return;
        i_return = 0;

        for (int i = 1; i <= total; i++) {
            p_temp.set(getA(i));
            if (q.distanceSquared(getB(i)) < q.distanceSquared(getA(i))) {
                p_temp.set(getB(i));
            }
            if (q.distanceSquared(p_temp) < r * r) {
                if (getcolor(i) == 2) {
                    i_return = i_return + 1;
                }
            }

        }

        return i_return;
    }

    //--------------------------------------------
    //点pに最も近い線分の、点pに近い方の端点を、頂点とした場合、何本の黒い線分が出ているか（頂点とr以内に端点がある線分の数）
    public int tyouten_syuui_sensuu_black(Point p, double r) {

        Point q = new Point();
        q.set(mottomo_tikai_Ten(p));//qは点pに近い方の端点
        Point p_temp = new Point();


        int i_return;
        i_return = 0;

        for (int i = 1; i <= total; i++) {
            p_temp.set(getA(i));
            if (q.distanceSquared(getB(i)) < q.distanceSquared(getA(i))) {
                p_temp.set(getB(i));
            }
            if (q.distanceSquared(p_temp) < r * r) {
                if (getcolor(i) == 0) {
                    i_return = i_return + 1;
                }
            }

        }

        return i_return;
    }

    //--------------------------------------------
    //点pに最も近い線分の、点pに近い方の端点を、頂点とした場合、何本の補助活線が出ているか（頂点とr以内に端点がある線分の数）
    public int tyouten_syuui_sensuu_hojyo_kassen(Point p, double r) {

        Point q = new Point();
        q.set(mottomo_tikai_Ten(p));//qは点pに近い方の端点
        Point p_temp = new Point();


        int i_return;
        i_return = 0;

        for (int i = 1; i <= total; i++) {
            p_temp.set(getA(i));
            if (q.distanceSquared(getB(i)) < q.distanceSquared(getA(i))) {
                p_temp.set(getB(i));
            }
            if (q.distanceSquared(p_temp) < r * r) {
                if (getcolor(i) >= 3) {
                    i_return = i_return + 1;
                }
            }

        }

        return i_return;
    }


    //--------------------------------------------
    //点pに最も近い線分の、点pに近い方の端点を、頂点とした場合、何本の選択された線分が出ているか。　20180918追加（頂点とr以内に端点がある線分の数）
    public int tyouten_syuui_sensuu_select(Point p, double r) {//rの値は0.0001位で頂点周りの折り畳み判定とかはうまく動いている

        Point q = new Point();
        q.set(mottomo_tikai_Ten(p));//qは点pに近い方の端点
        Point p_temp = new Point();


        int i_return;
        i_return = 0;

        for (int i = 1; i <= total; i++) {
            p_temp.set(getA(i));
            if (q.distanceSquared(getB(i)) < q.distanceSquared(getA(i))) {
                p_temp.set(getB(i));
            }
            if (q.distanceSquared(p_temp) < r * r) {
                if (get_select(i) == 2) {
                    i_return = i_return + 1;
                }
            }

        }

        return i_return;
    }

//--------------------------------------------


    //点pの近くの線分の活性化
    public void kasseika(Point p, double r) {
        for (int i = 1; i <= total; i++) {
            LineSegment si;
            si = line(i);
            si.activate(p, r);
        }
    }

    //全線分の非活性化
    public void hikasseika() {
        for (int i = 1; i <= total; i++) {
            LineSegment si;
            si = line(i);
            si.deactivate();
        }
    }


    //線分の活性化されたものを点pの座標にする
    public void set(Point p) {
        for (int i = 1; i <= total; i++) {
            LineSegment si;
            si = line(i);
            si.set(p);
        }

    }

    //線分集合の中の線分i0と、i0以外で、全く重なる線分があれば、その番号を返す。なければ-10を返す。
    public int overlapping_lineSegment_search(int i0) {
        //int minrid=0;double minr=100000;
        for (int i = 1; i <= total; i++) {
            if (i != i0) {
                if (oc.line_intersect_decide(get(i), get(i0)) == 31) {
                    return i;
                }
            }
        }
        return -10;


    }


    //線分s0と全く重なる線分が線分集合の中の線分にあれば、その番号を返す。なければ-10を返す。
    public int overlapping_lineSegment_search(LineSegment s0) {

        for (int i = 1; i <= total; i++) {
            if (oc.line_intersect_decide(get(i), s0) == 31) {
                return i;
            }
        }
        return -10;


    }

    //線分s0と部分的に重なる線分が線分集合の中の線分にあれば、最初に見つかった線分の番号を返す。なければ-10を返す。
    public int bubun_overlapping_lineSegment_search(LineSegment s0) {

        for (int i = 1; i <= total; i++) {
            if (oc.line_intersect_decide(get(i), s0) == 321) {
                return i;
            }
            if (oc.line_intersect_decide(get(i), s0) == 322) {
                return i;
            }
            if (oc.line_intersect_decide(get(i), s0) == 331) {
                return i;
            }
            if (oc.line_intersect_decide(get(i), s0) == 332) {
                return i;
            }
            if (oc.line_intersect_decide(get(i), s0) == 341) {
                return i;
            }
            if (oc.line_intersect_decide(get(i), s0) == 342) {
                return i;
            }
            if (oc.line_intersect_decide(get(i), s0) == 351) {
                return i;
            }
            if (oc.line_intersect_decide(get(i), s0) == 352) {
                return i;
            }

            if (oc.line_intersect_decide(get(i), s0) == 361) {
                return i;
            }
            if (oc.line_intersect_decide(get(i), s0) == 362) {
                return i;
            }
            if (oc.line_intersect_decide(get(i), s0) == 363) {
                return i;
            }
            if (oc.line_intersect_decide(get(i), s0) == 364) {
                return i;
            }
            if (oc.line_intersect_decide(get(i), s0) == 371) {
                return i;
            }
            if (oc.line_intersect_decide(get(i), s0) == 372) {
                return i;
            }
            if (oc.line_intersect_decide(get(i), s0) == 373) {
                return i;
            }
            if (oc.line_intersect_decide(get(i), s0) == 374) {
                return i;
            }


        }
        return -10;


    }


    //点pにもっとも近い折線をその点pの影で分割する。ただし、点pの影がどれか折線の端点と同じとみなされる場合は何もしない。
    public void senbun_bunkatu(Point p) {//点pが折線の端点と一致していないことが前提


        int mts_id;
        mts_id = mottomo_tikai_lineSegment_search(p);//mts_idは点pに最も近い線分の番号	public int ori_s.mottomo_tikai_senbun_sagasi(Ten p)
        LineSegment mts = new LineSegment(getA(mts_id), getB(mts_id));//mtsは点pに最も近い線分

        //直線t上の点pの影の位置（点pと最も近い直線t上の位置）を求める。public Ten oc.kage_motome(Tyokusen t,Ten p){}
        //線分を含む直線を得る public Tyokusen oc.Senbun2Tyokusen(Senbun s){}
        Point pk = new Point();
        pk.set(oc.shadow_request(oc.lineSegmentToStraightLine(mts), p));//pkは点pの（線分を含む直線上の）影
        if (pk.distance(mottomo_tikai_Ten(pk)) < 0.000001) {
            return;
        }//この行は、点pが折線の端点と一致していないことが前提
        //線分の分割-----------------------------------------
        senbun_bunkatu(mts_id, pk);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
    }


    //折線iをその点pの影で分割する。ただし、点pの影がどれか折線の端点と同じとみなされる場合は何もしない。
    public int senbun_bunkatu(Point p, int i) {//何もしない=0,分割した=1

        int mts_id;
        mts_id = i;
        LineSegment mts = new LineSegment(getA(mts_id), getB(mts_id));//mtsは点pに最も近い線分

        //直線t上の点pの影の位置（点pと最も近い直線t上の位置）を求める。public Ten oc.kage_motome(Tyokusen t,Ten p){}
        //線分を含む直線を得る public Tyokusen oc.Senbun2Tyokusen(Senbun s){}
        Point pk = new Point();
        pk.set(oc.shadow_request(oc.lineSegmentToStraightLine(mts), p));//pkは点pの（線分を含む直線上の）影
        //if(pk.kyori(mottomo_tikai_Ten(pk))<0.000001) {return 0;}
        //線分の分割-----------------------------------------
        senbun_bunkatu(mts_id, pk);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
        return 1;

    }


    public void add_sonomama(PolygonStore o_temp) {//別の折線集合の折線を追加する。単に追加するだけで、他の処理は一切しない。
        for (int i = 1; i <= o_temp.getTotal(); i++) {
            addLine(o_temp.getA(i), o_temp.getB(i), o_temp.getcolor(i));

        }
    }

    // --------------ccccccccc
    public void move(double dx, double dy) {//折線集合全体の位置を移動する。
        Point temp_a = new Point();
        Point temp_b = new Point();
        for (int i = 1; i <= getTotal(); i++) {
            temp_a.set(getA(i));
            temp_b.set(getB(i));
            temp_a.setX(temp_a.getX() + dx);
            temp_a.setY(temp_a.getY() + dy);
            temp_b.setX(temp_b.getX() + dx);
            temp_b.setY(temp_b.getY() + dy);
            seta(i, temp_a);
            setb(i, temp_b);
        }

        for (int i = 1; i <= cir_size(); i++) {

            Circle e_temp = new Circle();
            e_temp.set(cir_getEn(i));

            e_temp.setx(e_temp.getx() + dx);
            e_temp.sety(e_temp.gety() + dy);
            cir_setEn(i, e_temp);
        }


    }


    public void move(Point ta, Point tb, Point tc, Point td) {//折線集合全体の位置を移動する。
        double d;
        d = oc.angle(ta, tb, tc, td);
        double r;
        r = tc.distance(td) / ta.distance(tb);

        double dx;
        dx = tc.getX() - ta.getX();
        double dy;
        dy = tc.getY() - ta.getY();

        //oc.ten_kaiten(Ten a,Ten b,double d,double r)//点aを中心に点bをd度回転しabの距離がr倍の点を返す関数（元の点は変えずに新しい点を返す）

        Point temp_a = new Point();
        Point temp_b = new Point();
        for (int i = 1; i <= getTotal(); i++) {
            temp_a.set(oc.point_rotate(ta, getA(i), d, r));
            temp_b.set(oc.point_rotate(ta, getB(i), d, r));
            temp_a.setX(temp_a.getX() + dx);
            temp_a.setY(temp_a.getY() + dy);
            temp_b.setX(temp_b.getX() + dx);
            temp_b.setY(temp_b.getY() + dy);
            seta(i, temp_a);
            setb(i, temp_b);
        }
    }


    public void check1(double r_hitosii, double heikou_hantei) {
        Check1Senb.clear();
        unselect_all();
        for (int i = 1; i <= total - 1; i++) {
            if (getcolor(i) != 3) {

                LineSegment si;
                si = line(i);

                for (int j = i + 1; j <= total; j++) {
                    if (getcolor(j) != 3) {
                        LineSegment sj;
                        sj = line(j);//r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度

                        LineSegment si1 = new LineSegment();
                        si1.set(si);
                        LineSegment sj1 = new LineSegment();
                        sj1.set(sj);

                        if (oc.line_intersect_decide(si, sj, r_hitosii, heikou_hantei) == 31) {
                            Check1Senb.add(si1);
                            Check1Senb.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                        if (oc.line_intersect_decide(si, sj, r_hitosii, heikou_hantei) == 321) {
                            Check1Senb.add(si1);
                            Check1Senb.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                        if (oc.line_intersect_decide(si, sj, r_hitosii, heikou_hantei) == 322) {
                            Check1Senb.add(si1);
                            Check1Senb.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                        if (oc.line_intersect_decide(si, sj, r_hitosii, heikou_hantei) == 331) {
                            Check1Senb.add(si1);
                            Check1Senb.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                        if (oc.line_intersect_decide(si, sj, r_hitosii, heikou_hantei) == 332) {
                            Check1Senb.add(si1);
                            Check1Senb.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                        if (oc.line_intersect_decide(si, sj, r_hitosii, heikou_hantei) == 341) {
                            Check1Senb.add(si1);
                            Check1Senb.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                        if (oc.line_intersect_decide(si, sj, r_hitosii, heikou_hantei) == 342) {
                            Check1Senb.add(si1);
                            Check1Senb.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                        if (oc.line_intersect_decide(si, sj, r_hitosii, heikou_hantei) == 351) {
                            Check1Senb.add(si1);
                            Check1Senb.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                        if (oc.line_intersect_decide(si, sj, r_hitosii, heikou_hantei) == 352) {
                            Check1Senb.add(si1);
                            Check1Senb.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                        if (oc.line_intersect_decide(si, sj, r_hitosii, heikou_hantei) >= 360) {
                            Check1Senb.add(si1);
                            Check1Senb.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }

                    }
                }
            }
        }
    }


    public int fix1(double r_hitosii, double heikou_hantei) {//何もしなかったら0、何か修正したら1を返す。
        unselect_all();
        for (int i = 1; i <= total - 1; i++) {
            if (getcolor(i) != 3) {

                LineSegment si;
                si = line(i);
                for (int j = i + 1; j <= total; j++) {
                    if (getcolor(j) != 3) {
                        LineSegment sj;
                        sj = line(j);//r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度
                        //T字型交差
                        if (oc.line_intersect_decide(si, sj, r_hitosii, heikou_hantei) == 31) {
                            setcolor(i, getcolor(j));
                            deleteLine(j);
                            return 1;
                        }
                        if (oc.line_intersect_decide(si, sj, r_hitosii, heikou_hantei) == 321) {
                            set_select(i, 2);
                            set_select(j, 2);
                        }
                        if (oc.line_intersect_decide(si, sj, r_hitosii, heikou_hantei) == 322) {
                            set_select(i, 2);
                            set_select(j, 2);
                        }
                        if (oc.line_intersect_decide(si, sj, r_hitosii, heikou_hantei) == 331) {
                            set_select(i, 2);
                            set_select(j, 2);
                        }
                        if (oc.line_intersect_decide(si, sj, r_hitosii, heikou_hantei) == 332) {
                            set_select(i, 2);
                            set_select(j, 2);
                        }
                        if (oc.line_intersect_decide(si, sj, r_hitosii, heikou_hantei) == 341) {
                            set_select(i, 2);
                            set_select(j, 2);
                        }
                        if (oc.line_intersect_decide(si, sj, r_hitosii, heikou_hantei) == 342) {
                            set_select(i, 2);
                            set_select(j, 2);
                        }
                        if (oc.line_intersect_decide(si, sj, r_hitosii, heikou_hantei) == 351) {
                            set_select(i, 2);
                            set_select(j, 2);
                        }
                        if (oc.line_intersect_decide(si, sj, r_hitosii, heikou_hantei) == 352) {
                            set_select(i, 2);
                            set_select(j, 2);
                        }
                        if (oc.line_intersect_decide(si, sj, r_hitosii, heikou_hantei) >= 360) {
                            set_select(i, 2);
                            set_select(j, 2);
                        }
                    }
                }

            }
        }
        return 0;
    }

// ***********************************

    public void check2(double r_hitosii, double heikou_hantei) {
        Check2Senb.clear();

        unselect_all();
        for (int i = 1; i <= total - 1; i++) {
            if (getcolor(i) != 3) {

                LineSegment si;
                si = line(i);
                for (int j = i + 1; j <= total; j++) {
                    if (getcolor(j) != 3) {
                        LineSegment sj;
                        sj = line(j);//r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度

                        LineSegment si1 = new LineSegment();
                        si1.set(si);
                        LineSegment sj1 = new LineSegment();
                        sj1.set(sj);

                        //T字型交差
                        if (oc.line_intersect_decide_sweet(si, sj, r_hitosii, heikou_hantei) == 25) {
                            Check2Senb.add(si1);
                            Check2Senb.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                        if (oc.line_intersect_decide_sweet(si, sj, r_hitosii, heikou_hantei) == 26) {
                            Check2Senb.add(si1);
                            Check2Senb.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                        if (oc.line_intersect_decide_sweet(si, sj, r_hitosii, heikou_hantei) == 27) {
                            Check2Senb.add(si1);
                            Check2Senb.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                        if (oc.line_intersect_decide_sweet(si, sj, r_hitosii, heikou_hantei) == 28) {
                            Check2Senb.add(si1);
                            Check2Senb.add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }
                    }
                }
            }
        }
    }


    public int fix2(double r_hitosii, double heikou_hantei) {//何もしなかったら0、何か修正したら1を返す。
        unselect_all();
        for (int i = 1; i <= total - 1; i++) {
            if (getcolor(i) != 3) {

                LineSegment si;
                si = line(i);
                for (int j = i + 1; j <= total; j++) {
                    if (getcolor(j) != 3) {
                        LineSegment sj;
                        sj = line(j);//r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度
                        //T字型交差
                        //折線iをその点pの影で分割する。ただし、点pの影がどれか折線の端点と同じとみなされる場合は何もしない。
                        //	public void senbun_bunkatu(Ten p,int i){
                        if (oc.line_intersect_decide_sweet(si, sj, r_hitosii, heikou_hantei) == 25) {
                            if (senbun_bunkatu(getA(i), j) == 1) {
                                return 1;
                            }
                        }
                        if (oc.line_intersect_decide_sweet(si, sj, r_hitosii, heikou_hantei) == 26) {
                            if (senbun_bunkatu(getB(i), j) == 1) {
                                return 1;
                            }
                        }
                        if (oc.line_intersect_decide_sweet(si, sj, r_hitosii, heikou_hantei) == 27) {
                            if (senbun_bunkatu(getA(j), i) == 1) {
                                return 1;
                            }
                        }
                        if (oc.line_intersect_decide_sweet(si, sj, r_hitosii, heikou_hantei) == 28) {
                            if (senbun_bunkatu(getB(j), i) == 1) {
                                return 1;
                            }
                        }
                    }
                }

            }
        }
        return 0;
    }


    // ***********************************ppppppppppppqqqqqq
//Cirには1番目からcir_size()番目までデータが入っている
    public int cir_size() {
        return Cir.size() - 1;
    }

    public Circle cir_getEn(int i) {

        //if(sousuu+1> Senb.size()){while(sousuu+1> Senb.size()){Senb.add(new Senbun());}}//この文がないとうまく行かない。なぜこの文でないといけないかという理由が正確にはわからない。
        //return (Senbun)Senb.get(i);
        return Cir.get(i);

    }

    public void cir_setEn(int i, Circle e0) {
        //iの指定があったとき、EnはCirのi-1番目に格納される　
        //i>cir_size()のときは、Cirのi-1番目の円はまだ定義されていないので、とりあえずi-1番目まで円を存在させる必要がある


//for(int j=1;j<=i-1;j++) {
//	System.out.println("(2)cir_setEn(i,e_temp)  "+ j+";" +cir_getEn(j).getx()+"," +cir_getEn(j).gety()+"," +cir_getEn(j).getr());
//}
//	System.out.println("(2)cir_setEn(i,e_temp)  "+ i+";" +e0.getx()+"," +e0.gety()+"," +e0.getr());

        if (i > cir_size()) {
            while (i > cir_size()) {
                Cir.add(new Circle());
            }
        }

        Circle etemp = new Circle();
        etemp.set(e0);
//System.out.println("etemp "+ etemp.getcolor()  );

        //Cir.set(i,e0) ; //e0を直接Cirのsetすると、CirのEnはiが違っても全部e0で同じになるので、途中にetempを新しくつくって、e0の値をコピーしてCirにsetさせる。
        Cir.set(i, etemp);
//for(int j=1;j<=i;j++) {
//	System.out.println("(3)cir_setEn(i,e_temp)  "+ j+";" +cir_getEn(j).getx()+"," +cir_getEn(j).gety()+"," +cir_getEn(j).getr());
//}
    }


    public int check1_size() {
        return Check1Senb.size();
    }

    public int check2_size() {
        return Check2Senb.size();
    }

    public int check3_size() {
        return Check3Senb.size();
    }//Check3Senbには0番目からsize()-1番目までデータが入っている

    public int check4_size() {
        return Check4Senb.size();
    }//Check4Senbには0番目からsize()-1番目までデータが入っている

    public int check4_T_size() {
        return check4Point.size();
    }//Check4Tenには0番目からsize()-1番目までデータが入っている

    public LineSegment check1_getSenbun(int i) {
        return Check1Senb.get(i);
    }

    public LineSegment check2_getSenbun(int i) {
        return Check2Senb.get(i);
    }

    public LineSegment check3_getSenbun(int i) {
        return Check3Senb.get(i);
    }

    public LineSegment check4_getSenbun(int i) {
        return Check4Senb.get(i);
    }

    public Point check4_getTen(int i) {
        return check4Point.get(i);
    }

    public void check3(double r) {//頂点周囲の線数チェック
        Check3Senb.clear();
        unselect_all();
        for (int i = 1; i <= total; i++) {
            if (getcolor(i) != 3) {
                LineSegment si;
                si = line(i);
                Point p = new Point();
                int tss;    //頂点の周りの折線の数。　tss%2==0 偶数、==1 奇数
                int tss_red;    //頂点の周りの山折線の数。
                int tss_blue;    //頂点の周りの谷折線の数。
                int tss_black;    //頂点の周りの境界線の数。
                int tss_hojyo_kassen;    //頂点の周りの補助活線の数。

                //-----------------
                p.set(si.getA());
                tss = tyouten_syuui_sensuu(p, r);
                tss_red = tyouten_syuui_sensuu_red(p, r);
                tss_blue = tyouten_syuui_sensuu_blue(p, r);
                tss_black = tyouten_syuui_sensuu_black(p, r);
                tss_hojyo_kassen = tyouten_syuui_sensuu_hojyo_kassen(p, r);
                //System.out.println("senbun("+i+") a : tss="+tss+", tss_red="+tss_red+", tss_blue="+tss_blue+", tss_black="+tss_black+", tss_hojyo_kassen="+tss_hojyo_kassen);
                //-----------------

                if ((tss_black != 0) && (tss_black != 2)) {//黒線がないか2本以外の場合はおかしい。
                    //System.out.println("20170216_1");
                    Check3Senb.add(new LineSegment(p, p));//set_select(i,2);

                }


                if (tss_black == 0) {//黒線がない場合
                    if (tss - tss_hojyo_kassen == tss_red + tss_blue) {//（前提として境界は黒で、山谷未設定折線はないこと。）頂点周囲に赤か青しかない。つまり、用紙内部の点

                        if (Math.abs(tss_red - tss_blue) != 2) {//用紙内部の点で前川定理を満たさないのはダメ
                            //System.out.println("20170216_2");
                            Check3Senb.add(new LineSegment(p, p));//set_select(i,2);
                        }
                    }
                    if (kakutyou_fushimi_hantei_naibu(p) == 0) {
                        //System.out.println("20170216_3");
                        Check3Senb.add(new LineSegment(p, p));//set_select(i,2);
                    }


                }

                if (tss_black == 2) {//黒線が2本の場合
                    if (kakutyou_fushimi_hantei_henbu(p) == 0) {
                        //System.out.println("20170216_3");
                        Check3Senb.add(new LineSegment(p, p));//set_select(i,2);
                    }
                }


                //-----------------


                //-----------------
                p.set(si.getB());
                tss = tyouten_syuui_sensuu(p, r);
                tss_red = tyouten_syuui_sensuu_red(p, r);
                tss_blue = tyouten_syuui_sensuu_blue(p, r);
                tss_black = tyouten_syuui_sensuu_black(p, r);
                tss_hojyo_kassen = tyouten_syuui_sensuu_hojyo_kassen(p, r);
                //System.out.println("senbun("+i+")  b : tss="+tss+", tss_red="+tss_red+", tss_blue="+tss_blue+", tss_black="+tss_black+", tss_hojyo_kassen="+tss_hojyo_kassen);

                //-----------------
                if ((tss_black != 0) && (tss_black != 2)) {//黒線がないか2本以外の場合はおかしい。
                    //System.out.println("20170216_4");
                    Check3Senb.add(new LineSegment(p, p));//set_select(i,2);
                }

                if (tss_black == 0) {//黒線がない場合
                    if (tss - tss_hojyo_kassen == tss_red + tss_blue) {//（前提として境界は黒で、山谷未設定折線はないこと。）頂点周囲に赤か青しかない。つまり、用紙内部の点
                        if (Math.abs(tss_red - tss_blue) != 2) {//用紙内部の点で前川定理を満たさないのはダメ
                            //System.out.println("20170216_5");
                            Check3Senb.add(new LineSegment(p, p));//set_select(i,2);
                        }
                    }
                    if (kakutyou_fushimi_hantei_naibu(p) == 0) {
                        //System.out.println("20170216_6");
                        Check3Senb.add(new LineSegment(p, p));//set_select(i,2);
                    }


                }


                if (tss_black == 2) {//黒線が2本の場合
                    if (kakutyou_fushimi_hantei_henbu(p) == 0) {
                        //System.out.println("20170216_3");
                        Check3Senb.add(new LineSegment(p, p));//set_select(i,2);
                    }
                }


                //-----------------
            }
        }


    }


    // -----------------------------------------------------
    public int Check4Point_overlapping_check(Point p0) {
        for (int i = 0; i < check4_T_size(); i++) {
            Point p = new Point();
            p.set(check4_getTen(i));
            if ((-0.00000001 < p0.getX() - p.getX()) && (p0.getX() - p.getX() < 0.00000001)) {
                if ((-0.00000001 < p0.getY() - p.getY()) && (p0.getY() - p.getY() < 0.00000001)) {
                    return 1;
                }
            }
        }
        return 0;
    }

    // -----------------------------------------------------
    public void check4(double r) {//頂点周囲の線数チェック
        Check4Senb.clear();
        check4Point.clear();

        unselect_all();

        //チェックすべき場所の数え上げ
        for (int i = 1; i <= total; i++) {
            if (getcolor(i) != 3) {

                LineSegment si;
                si = line(i);

                //System.out.println("sen("+i+")=si  : ax="+si.geta().getx()+", ay="+si.geta().gety()+", bx="+si.getb().getx()+", by="+si.getb().gety());

                Point pa = new Point();
                pa.set(si.getA());
                if (Check4Point_overlapping_check(pa) == 0) {
                    check4Point.add(pa);
                }

                Point pb = new Point();
                pb.set(si.getB());
                if (Check4Point_overlapping_check(pb) == 0) {
                    check4Point.add(pb);
                }

                //20170828
                //なぜか
                //Ten p=new Ten();
                //p.set(si.geta());
                //Check4Ten_jyuufuku_check(p)
                //p.set(si.getb());
                //Check4Ten_jyuufuku_check(p)
                //とすると前の2行もp.set(si.getb());の場合の結果になってしまう。
            }
        }

        System.out.println("check4_T_size() = " + check4_T_size());

        //チェックすべき場所が平らに折りたたみ可能かの選別
        for (int i = 0; i < check4_T_size(); i++) {
            //System.out.println("i = "+i+"/"+check4_T_size());
            Point p = new Point();
            p.set(check4_getTen(i));

            //tyouten_syuui_sensuu_all(p,r);
            //-----------------
            if (i_taira_ok(p, r) == 0) {
                Check4Senb.add(new LineSegment(p, p));
            }
        }
    }

    // -------------------------------------------------------------------
    public int i_taira_ok(Point p, double r) {//平らに折りたたみ可能=1
        double hantei_kyori = 0.00001;
        //点pに最も近い線分の、点pに近い方の端点を、頂点とした場合、何本の線分が出ているか（頂点とr以内に端点がある線分の数）
        int i_tss = 0;    //i_tss%2==0 偶数、==1 奇数
        int i_tss_red = 0;
        int i_tss_blue = 0;
        int i_tss_black = 0;
        int i_tss_cyan = 0;

        Narabebako_int_double nbox = new Narabebako_int_double();

        for (int i = 1; i <= getTotal(); i++) {
            if ((p.distanceSquared(getA(i)) < r * r) || (p.distanceSquared(getB(i)) < r * r)) {
                i_tss = i_tss + 1;
                if (getcolor(i) == 1) {
                    i_tss_red = i_tss_red + 1;
                } else if (getcolor(i) == 2) {
                    i_tss_blue = i_tss_blue + 1;
                } else if (getcolor(i) == 0) {
                    i_tss_black = i_tss_black + 1;
                } else if (getcolor(i) >= 3) {
                    i_tss_cyan = i_tss_cyan + 1;
                }
            }
            //System.out.println("q.kyori2jyou(p_temp)="+q.kyori2jyou(p_temp)+", r*r="+r*r);


            //	}


            //	for (int i=1; i<=getsousuu(); i++ ){

            //pを端点とする折線をNarabebakoに入れる
            if ((0 <= getcolor(i)) && (getcolor(i) <= 2)) { //この段階で補助活線は除く
                if (p.distance(getA(i)) < hantei_kyori) {
                    nbox.ire_i_tiisaijyun(new int_double(i, oc.angle(getA(i), getB(i))));
                } else if (p.distance(getB(i)) < hantei_kyori) {
                    nbox.ire_i_tiisaijyun(new int_double(i, oc.angle(getB(i), getA(i))));
                }
            }
        }


        // 判定開始-------------------------------------------

        if ((i_tss_black != 0) && (i_tss_black != 2)) {//黒線がないか2本以外の場合はおかしい。
            return 0;
        }

        if (i_tss_black == 0) {//黒線がない場合
            if (Math.abs(i_tss_red - i_tss_blue) != 2) {//用紙内部の点で前川定理を満たさないのはダメ
                return 0;
            }
// --------------------------


/*

		//pを端点とする折線をNarabebakoに入れる
		Narabebako_int_double nbox =new Narabebako_int_double();
		for (int i=1; i<=getsousuu(); i++ ){ if((0<=getcolor(i))&&(getcolor(i)<=2)){ //この段階で補助活線は除く
			if(      p.kyori(geta(i))<hantei_kyori){
				nbox.ire_i_tiisaijyun(new int_double( i  , oc.kakudo(geta(i),getb(i)) ));
			}else if(p.kyori(getb(i))<hantei_kyori){
				nbox.ire_i_tiisaijyun(new int_double( i  , oc.kakudo(getb(i),geta(i)) ));
			}
		}}

*/

// --------------------------
            return kakutyou_fushimi_hantei_naibu(p, nbox);


            //return kakutyou_fushimi_hantei_naibu(p);
        }

        if (i_tss_black == 2) {//黒線が2本の場合


// ------------------

            //double hantei_kyori=0.00001;

/*
		//pを端点とする折線をNarabebakoに入れる
		Narabebako_int_double nbox =new Narabebako_int_double();
		for (int i=1; i<=getsousuu(); i++ ){ if((0<=getcolor(i))&&(getcolor(i)<=2)){ //この段階で補助活線は除く
			if(      p.kyori(geta(i))<hantei_kyori){
				nbox.ire_i_tiisaijyun(new int_double( i  , oc.kakudo(geta(i),getb(i)) ));
			}else if(p.kyori(getb(i))<hantei_kyori){
				nbox.ire_i_tiisaijyun(new int_double( i  , oc.kakudo(getb(i),geta(i)) ));
			}
		}}
*/
// ------------------
            return kakutyou_fushimi_hantei_henbu(p, nbox);


            //return kakutyou_fushimi_hantei_henbu(p);
        }
        return 1;
    }


    // -----------------------------------------------------
    public void check4_old(double r) {
        //_oldをとればオリジナルのcheck4(double r)関数として作動する
        //頂点周囲の線数チェック
        Check4Senb.clear();
        unselect_all();
        for (int i = 1; i <= total; i++) {
            if (getcolor(i) != 3) {
                LineSegment si;
                si = line(i);
                Point p = new Point();
                int tss;    //tss%2==0 偶数、==1 奇数
                int tss_red;
                int tss_blue;
                int tss_black;
                int tss_cyan;

                //-----------------
                p.set(si.getA());
                tss = tyouten_syuui_sensuu(p, r);
                tss_red = tyouten_syuui_sensuu_red(p, r);
                tss_blue = tyouten_syuui_sensuu_blue(p, r);
                tss_black = tyouten_syuui_sensuu_black(p, r);
                tss_cyan = tyouten_syuui_sensuu_hojyo_kassen(p, r);
                //System.out.println("senbun("+i+") a : tss="+tss+", tss_red="+tss_red+", tss_blue="+tss_blue+", tss_black="+tss_black+", tss_cyan="+tss_cyan);
                //-----------------

                if ((tss_black != 0) && (tss_black != 2)) {//黒線がないか2本以外の場合はおかしい。
                    //System.out.println("20170216_1");
                    Check4Senb.add(new LineSegment(p, p));//set_select(i,2);

                }


                if (tss_black == 0) {//黒線がない場合
                    if (tss - tss_cyan == tss_red + tss_blue) {//（前提として境界は黒で、山谷未設定折線はないこと。）頂点周囲に赤か青しかない。つまり、用紙内部の点

                        if (Math.abs(tss_red - tss_blue) != 2) {//用紙内部の点で前川定理を満たさないのはダメ
                            //System.out.println("20170216_2");
                            Check4Senb.add(new LineSegment(p, p));//set_select(i,2);
                        }
                    }
                    if (kakutyou_fushimi_hantei_naibu(p) == 0) {
                        //System.out.println("20170216_3");
                        Check4Senb.add(new LineSegment(p, p));//set_select(i,2);
                    }


                }

                if (tss_black == 2) {//黒線が2本の場合
                    if (kakutyou_fushimi_hantei_henbu(p) == 0) {
                        //System.out.println("20170216_3");
                        Check4Senb.add(new LineSegment(p, p));//set_select(i,2);
                    }
                }


                //-----------------


                //-----------------
                p.set(si.getB());
                tss = tyouten_syuui_sensuu(p, r);
                tss_red = tyouten_syuui_sensuu_red(p, r);
                tss_blue = tyouten_syuui_sensuu_blue(p, r);
                tss_black = tyouten_syuui_sensuu_black(p, r);
                tss_cyan = tyouten_syuui_sensuu_hojyo_kassen(p, r);
                //System.out.println("senbun("+i+")  b : tss="+tss+", tss_red="+tss_red+", tss_blue="+tss_blue+", tss_black="+tss_black+", tss_cyan="+tss_cyan);

                //-----------------
                if ((tss_black != 0) && (tss_black != 2)) {//黒線がないか2本以外の場合はおかしい。
                    //System.out.println("20170216_4");
                    Check4Senb.add(new LineSegment(p, p));//set_select(i,2);
                }

                if (tss_black == 0) {//黒線がない場合
                    if (tss - tss_cyan == tss_red + tss_blue) {//（前提として境界は黒で、山谷未設定折線はないこと。）頂点周囲に赤か青しかない。つまり、用紙内部の点
                        if (Math.abs(tss_red - tss_blue) != 2) {//用紙内部の点で前川定理を満たさないのはダメ
                            //System.out.println("20170216_5");
                            Check4Senb.add(new LineSegment(p, p));//set_select(i,2);
                        }
                    }
                    if (kakutyou_fushimi_hantei_naibu(p) == 0) {
                        //System.out.println("20170216_6");
                        Check4Senb.add(new LineSegment(p, p));//set_select(i,2);
                    }


                }


                if (tss_black == 2) {//黒線が2本の場合
                    if (kakutyou_fushimi_hantei_henbu(p) == 0) {
                        //System.out.println("20170216_3");
                        Check4Senb.add(new LineSegment(p, p));//set_select(i,2);
                    }
                }


                //-----------------
            }
        }


    }


// **************************************

    //Ten p に最も近い用紙辺部の端点が拡張伏見定理を満たすか判定
    public int kakutyou_fushimi_hantei_henbu(Point p) {//return　0=満たさない、　1=満たす。　
        double hantei_kyori = 0.00001;

        Point t1 = new Point();
        t1.set(mottomo_tikai_Ten_with_icol_0_1_2(p));//点pに最も近い、「線分の端点」を返すori_s.mottomo_tikai_Tenは近い点がないと p_return.set(100000.0,100000.0)と返してくる

        //t1を端点とする折線をNarabebakoに入れる
        Narabebako_int_double nbox = new Narabebako_int_double();
        for (int i = 1; i <= getTotal(); i++) {
            if ((0 <= getcolor(i)) && (getcolor(i) <= 2)) { //この段階で補助活線は除く
                if (t1.distance(getA(i)) < hantei_kyori) {
                    nbox.ire_i_tiisaijyun(new int_double(i, oc.angle(getA(i), getB(i))));
                } else if (t1.distance(getB(i)) < hantei_kyori) {
                    nbox.ire_i_tiisaijyun(new int_double(i, oc.angle(getB(i), getA(i))));
                }
            }
        }

        return kakutyou_fushimi_hantei_henbu(p, nbox);
    }

    // ---------------------------------
    public int kakutyou_fushimi_hantei_henbu(Point p, Narabebako_int_double nbox) {//return　0=満たさない、　1=満たす。　
        double hantei_kyori = 0.00001;

        int tikai_orisen_jyunban;
        int tooi_orisen_jyunban;
        if (nbox.getsousuu() == 2) {//t1を端点とする折線の数が2のとき
            if (getcolor(nbox.get_int(1)) != 0) {//1本目が黒でないならダメ
                return 0;
            }
            if (getcolor(nbox.get_int(2)) != 0) {//2本目が黒でないならダメ
                return 0;
            }

            //2本の線種が黒黒
            return 1;
        }


        //以下はt1を端点とする折線の数が3以上の偶数のとき

        //fushimi_hantei_kakudo_goukei=360.0;


        //辺の折線が,ならべばこnbox,の一番目と最後の順番になるようにする。

        int saisyo_ni_suru = -10;
        for (int i = 1; i <= nbox.getsousuu() - 1; i++) {
            if ((getcolor(nbox.get_int(i)) == 0) &&
                    (getcolor(nbox.get_int(i + 1)) == 0)) {
                saisyo_ni_suru = i + 1;
            }
        }

        if ((getcolor(nbox.get_int(nbox.getsousuu())) == 0) &&
                (getcolor(nbox.get_int(1)) == 0)) {
            saisyo_ni_suru = 1;
        }

        if (saisyo_ni_suru < 0) {
            return 0;
        }//

        for (int i = 1; i <= saisyo_ni_suru - 1; i++) {
            nbox.jyunkai2wo1nisuru();
        }


        //System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
        //nbox.hyouji();

        //ならべばこnbox,の一番目の折線がx軸となす角度が0になるようにする。
        Narabebako_int_double nbox1 = new Narabebako_int_double();

        double sasihiku_kakudo = nbox.get_double(1);
//System.out.println("sasihiku_kakudo="+sasihiku_kakudo);

        for (int i = 1; i <= nbox.getsousuu(); i++) {
            int_double i_d_0 = new int_double();
            i_d_0.set(nbox.get_i_d(i));
//System.out.println("i_d_0.get_d()="+i_d_0.get_d());


            i_d_0.set_d(
                    oc.angle_between_0_360(i_d_0.get_d() - sasihiku_kakudo)
            );
            nbox1.add(i_d_0);
        }

        nbox.set(nbox1);

        //nbox.hyouji();


        while (nbox.getsousuu() > 2) {//点から出る折線の数が2になるまで実行する
            nbox1.set(kakutyou_fushimi_hantei_henbu_tejyun(nbox));
            //System.out.println("nbox1.getsousuu()="+nbox1.getsousuu()+",nbox.getsousuu()="+nbox.getsousuu());
            if (nbox1.getsousuu() == nbox.getsousuu()) {
                //System.out.println("20170216_14");
                return 0;
            }
            nbox.set(nbox1);
        }

        return 1;
    }


// -------------------------------------------------------
// **************************************

    //bを端点とする折線が入ったNarabebakoを得る。線分baとのなす角が小さい順に並んでいる。
    public Narabebako_int_double get_nbox_of_tyouten_b_syuui_orisen(Point a, Point b) {
        Narabebako_int_double r_nbox = new Narabebako_int_double();
        double hantei_kyori = 0.00001;

        //bを端点とする折線をNarabebakoに入れる

        for (int i = 1; i <= getTotal(); i++) {
            if ((0 <= getcolor(i)) && (getcolor(i) <= 2)) { //この段階で補助活線は除く
                if (b.distance(getA(i)) < hantei_kyori) {
                    r_nbox.ire_i_tiisaijyun(new int_double(i, oc.angle(b, a, getA(i), getB(i))));
                } else if (b.distance(getB(i)) < hantei_kyori) {
                    r_nbox.ire_i_tiisaijyun(new int_double(i, oc.angle(b, a, getB(i), getA(i))));
                }
            }
        }

        return r_nbox;
    }


    //ベクトルabとcdのなす角度//oc.kakudo(Ten a,Ten b,Ten c,Ten d){return kakudo_osame_0_360(kakudo(c,d)-kakudo(a,b));}


// ---------------------------------


    public Narabebako_int_double kakutyou_fushimi_hantei_henbu_tejyun(Narabebako_int_double nbox0) {//拡張伏見定理のようにして辺部の点で隣接する３角度を1つの角度にするか辺部の角を削る操作
        Narabebako_int_double nboxtemp = new Narabebako_int_double();
        Narabebako_int_double nbox1 = new Narabebako_int_double();
        int tikai_orisen_jyunban = 1;
        int tooi_orisen_jyunban = 2;

        double kakudo_min = 10000.0;
        double temp_kakudo;


        //角度の最小値kakudo_minを求める
        for (int k = 1; k <= nbox0.getsousuu() - 1; k++) {//kは角度の順番
            temp_kakudo = nbox0.get_double(k + 1) - nbox0.get_double(k);
            if (temp_kakudo < kakudo_min) {
                kakudo_min = temp_kakudo;
            }
        }


        //k=1
        temp_kakudo = nbox0.get_double(2) - nbox0.get_double(1);
        if (Math.abs(temp_kakudo - kakudo_min) < 0.00001) {// 折線を1つ減らせる条件に適合したので、新たにnbox1を作ってリターンする。
            for (int i = 2; i <= nbox0.getsousuu(); i++) {
                int_double i_d_0 = new int_double();
                i_d_0.set(nbox0.get_i_d(i));
                nbox1.add(i_d_0);
            }
            return nbox1;
        }

        //k=nbox0.getsousuu()-1
        temp_kakudo = nbox0.get_double(nbox0.getsousuu()) - nbox0.get_double(nbox0.getsousuu() - 1);
        if (Math.abs(temp_kakudo - kakudo_min) < 0.00001) {// 折線を1つ減らせる条件に適合したので、新たにnbox1を作ってリターンする。
            for (int i = 1; i <= nbox0.getsousuu() - 1; i++) {
                int_double i_d_0 = new int_double();
                i_d_0.set(nbox0.get_i_d(i));
                nbox1.add(i_d_0);
            }
            return nbox1;
        }

        for (int k = 2; k <= nbox0.getsousuu() - 2; k++) {//kは角度の順番
            temp_kakudo = nbox0.get_double(k + 1) - nbox0.get_double(k);
            if (Math.abs(temp_kakudo - kakudo_min) < 0.00001) {
                if (getcolor(nbox0.get_int(k)) != getcolor(nbox0.get_int(k + 1))) {//この場合に隣接する３角度を1つの角度にする
                    // 折線を2つ減らせる条件に適合したので、新たにnbox1を作ってリターンする。


                    double kijyun_kakudo = nbox0.get_double(3);
                    //System.out.println("折線を2つ減らせる条件に適合したので、新たにnbox1を作ってリターンする。 kijyun_kakudo="+kijyun_kakudo);


                    for (int i = 1; i <= k - 1; i++) {
                        int_double i_d_0 = new int_double();
                        i_d_0.set(nbox0.get_i_d(i));
                        nbox1.add(i_d_0);
                    }

                    for (int i = k + 2; i <= nbox0.getsousuu(); i++) {
                        int_double i_d_0 = new int_double();
                        i_d_0.set(nbox0.get_i_d(i));
                        i_d_0.set_d(
                                i_d_0.get_d() - 2.0 * kakudo_min
                        );
                        nbox1.add(i_d_0);
                    }

                    return nbox1;
                }
            }
        }

        // 折線を減らせる条件に適合した角がなかった場合nbox0とおなじnbox1を作ってリターンする。
        for (int i = 1; i <= nbox0.getsousuu(); i++) {
            nbox1.add(nbox0.get_i_d(i));
        }
        return nbox1;
    }


// ***************************************************************

    double fushimi_hantei_kakudo_goukei = 360.0;
// ***************************************************************


    //Ten p に最も近い用紙内部の端点が拡張伏見定理を満たすか判定
    public int kakutyou_fushimi_hantei_naibu(Point p) {//return　0=満たさない、　1=満たす。　
        double hantei_kyori = 0.00001;

        Point t1 = new Point();
        t1.set(mottomo_tikai_Ten_with_icol_0_1_2(p));//点pに最も近い、「線分の端点」を返すori_s.mottomo_tikai_Tenは近い点がないと p_return.set(100000.0,100000.0)と返してくる

        //t1を端点とする折線をNarabebakoに入れる
        Narabebako_int_double nbox = new Narabebako_int_double();
        for (int i = 1; i <= getTotal(); i++) {
            if ((0 <= getcolor(i)) && (getcolor(i) <= 2)) { //この段階で補助活線は除く
                if (t1.distance(getA(i)) < hantei_kyori) {
                    nbox.ire_i_tiisaijyun(new int_double(i, oc.angle(getA(i), getB(i))));
                } else if (t1.distance(getB(i)) < hantei_kyori) {
                    nbox.ire_i_tiisaijyun(new int_double(i, oc.angle(getB(i), getA(i))));
                }
            }
        }

        return kakutyou_fushimi_hantei_naibu(p, nbox);

    }

    public int kakutyou_fushimi_hantei_naibu(Point p, Narabebako_int_double nbox) {//return　0=満たさない、　1=満たす。　
        double hantei_kyori = 0.00001;

        if (nbox.getsousuu() % 2 == 1) {//t1を端点とする折線の数が奇数のとき
            return 0;
        }


        int tikai_orisen_jyunban;
        int tooi_orisen_jyunban;
        if (nbox.getsousuu() == 2) {//t1を端点とする折線の数が2のとき
            if (getcolor(nbox.get_int(1)) != getcolor(nbox.get_int(2))) {//2本の線種が違うなら角度関係なしにダメ
                //System.out.println("20170216_12");
                return 0;
            }

            //以下は2本の線種が青青、または赤赤の場合
            int i_senbun_kousa_hantei;
            i_senbun_kousa_hantei = oc.line_intersect_decide(get(nbox.get_int(1)), get(nbox.get_int(2)), 0.00001, 0.00001);
            if (i_senbun_kousa_hantei == 323) {
                return 1;
            }
            if (i_senbun_kousa_hantei == 333) {
                return 1;
            }
            if (i_senbun_kousa_hantei == 343) {
                return 1;
            }
            if (i_senbun_kousa_hantei == 353) {
                return 1;
            }

            //System.out.println("20170216_13");
            return 0;
        }

        //以下はt1を端点とする折線の数が4以上の偶数のとき

        fushimi_hantei_kakudo_goukei = 360.0;

        Narabebako_int_double nbox1 = new Narabebako_int_double();

        while (nbox.getsousuu() > 2) {//点から出る折線の数が2になるまで実行する
            nbox1.set(kakutyou_fushimi_hantei_naibu_tejyun(nbox));
            //System.out.println("nbox1.getsousuu()="+nbox1.getsousuu()+",nbox.getsousuu()="+nbox.getsousuu());
            if (nbox1.getsousuu() == nbox.getsousuu()) {
                //System.out.println("20170216_14");
                return 0;
            }
            nbox.set(nbox1);
        }

        double temp_kakudo = oc.angle_betwen_0_kmax(
                oc.angle_betwen_0_kmax(nbox.get_double(1), fushimi_hantei_kakudo_goukei)
                        -
                        oc.angle_betwen_0_kmax(nbox.get_double(2), fushimi_hantei_kakudo_goukei)
                , fushimi_hantei_kakudo_goukei
        );

        if (Math.abs(fushimi_hantei_kakudo_goukei - temp_kakudo * 2.0) < hantei_kyori) {
            return 1;
        }

        return 0;//この0だけ、角度がおかしいという意味
    }


// ------------------------------------------------------------------------------------------------------------------------------------

    public Narabebako_int_double kakutyou_fushimi_hantei_naibu_tejyun(Narabebako_int_double nbox0) {//拡張伏見定理で隣接する３角度を1つの角度にする操作
        Narabebako_int_double nboxtemp = new Narabebako_int_double();
        Narabebako_int_double nbox1 = new Narabebako_int_double();
        int tikai_orisen_jyunban = 1;
        int tooi_orisen_jyunban = 2;

        double kakudo_min = 10000.0;


        //System.out.println("伏見判定　頂点周りの角度合計 = "+fushimi_hantei_kakudo_goukei);
        //角度の最小値kakudo_minを求める
        for (int k = 1; k <= nbox0.getsousuu(); k++) {//kは角度の順番
            tikai_orisen_jyunban = k;
            if (tikai_orisen_jyunban > nbox0.getsousuu()) {
                tikai_orisen_jyunban = tikai_orisen_jyunban - nbox0.getsousuu();
            }
            tooi_orisen_jyunban = k + 1;
            if (tooi_orisen_jyunban > nbox0.getsousuu()) {
                tooi_orisen_jyunban = tooi_orisen_jyunban - nbox0.getsousuu();
            }

            double temp_kakudo = oc.angle_betwen_0_kmax(
                    oc.angle_betwen_0_kmax(nbox0.get_double(tooi_orisen_jyunban), fushimi_hantei_kakudo_goukei)
                            -
                            oc.angle_betwen_0_kmax(nbox0.get_double(tikai_orisen_jyunban), fushimi_hantei_kakudo_goukei)

                    , fushimi_hantei_kakudo_goukei
            );


            //System.out.println("角度の網羅　"+k+"番目:  "+
            //nbox0.get_double(tooi_orisen_jyunban)+" - "+
            //nbox0.get_double(tikai_orisen_jyunban)+" = "+
            //temp_kakudo);


            if (temp_kakudo < kakudo_min) {
                kakudo_min = temp_kakudo;
            }
        }

        //System.out.println("kakudo_min="+kakudo_min);

        for (int k = 1; k <= nbox0.getsousuu(); k++) {//kは角度の順番
            double temp_kakudo = oc.angle_betwen_0_kmax(nbox0.get_double(2) - nbox0.get_double(1), fushimi_hantei_kakudo_goukei);

            //System.out.println("k="+k+",temp_kakudo="+temp_kakudo);

            if (Math.abs(temp_kakudo - kakudo_min) < 0.00001) {
                //if(Math.abs(temp_kakudo-kakudo_min)<0.001){


                //System.out.println("   getcolor(nbox0.get_int(1))="+getcolor(nbox0.get_int(1))+",getcolor(nbox0.get_int(2))="+getcolor(nbox0.get_int(2)));
                if (getcolor(nbox0.get_int(1)) != getcolor(nbox0.get_int(2))) {//この場合に隣接する３角度を1つの角度にする
                    // 折線を2つ減らせる条件に適合したので、新たにnbox1を作ってリターンする。


                    double kijyun_kakudo = nbox0.get_double(3);
                    //System.out.println("折線を2つ減らせる条件に適合したので、新たにnbox1を作ってリターンする。 kijyun_kakudo="+kijyun_kakudo);


                    for (int i = 1; i <= nbox0.getsousuu(); i++) {
                        int_double i_d_0 = new int_double();
                        i_d_0.set(nbox0.get_i_d(i));
                        //i_d_0.set_d(i_d_0.get_d ()- kijyun_kakudo );

                        i_d_0.set_d(
                                oc.angle_betwen_0_kmax(i_d_0.get_d() - kijyun_kakudo, fushimi_hantei_kakudo_goukei)
                        );

                        //nbox1.add(nbox0.get_i_d(i));
                        nboxtemp.add(i_d_0);

                    }


                    for (int i = 3; i <= nboxtemp.getsousuu(); i++) {
                        int_double i_d_0 = new int_double();
                        i_d_0.set(nboxtemp.get_i_d(i));
                        //i_d_0.set_d(i_d_0.get_d ()- kijyun_kakudo );

                        //nbox1.add(nbox0.get_i_d(i));
                        nbox1.add(i_d_0);

                    }

                    fushimi_hantei_kakudo_goukei = fushimi_hantei_kakudo_goukei - 2.0 * kakudo_min;
                    return nbox1;
                }
            }
            nbox0.jyunkai2wo1nisuru();

        }

        // 折線を2つ減らせる条件に適合した角がなかった場合nbox0とおなじnbox1を作ってリターンする。
        for (int i = 1; i <= nbox0.getsousuu(); i++) {
            nbox1.add(nbox0.get_i_d(i));
        }
        return nbox1;
    }


    // ---------------------------
    public int X_koisa_ari_nasi(LineSegment s0) {//s0とX字で交差する折線があれば1、なければ0を返す
        for (int i = 1; i <= total; i++) {
            if (oc.line_intersect_decide(get(i), s0, 0.00001, 0.00001) == 1) {
                return 1;
            }
        }
        return 0;
    }


    // ---------------------------
    public double get_x_max() {//sousuu=0のときは0.0を返す
        if (total == 0) {
            return 0.0;
        }
        double dm = getax(1);
        for (int i = 1; i <= total; i++) {
            if (dm < getax(i)) {
                dm = getax(i);
            }
            if (dm < getbx(i)) {
                dm = getbx(i);
            }
        }
        return dm;
    }

    // ---------------------------
    public double get_x_min() {//sousuu=0のときは0.0を返す
        if (total == 0) {
            return 0.0;
        }
        double dm = getax(1);
        for (int i = 1; i <= total; i++) {
            if (dm > getax(i)) {
                dm = getax(i);
            }
            if (dm > getbx(i)) {
                dm = getbx(i);
            }
        }
        return dm;
    }

    // ---------------------------
// ---------------------------
    public double get_y_max() {//sousuu=0のときは0.0を返す
        if (total == 0) {
            return 0.0;
        }
        double dm = getay(1);
        for (int i = 1; i <= total; i++) {
            if (dm < getay(i)) {
                dm = getay(i);
            }
            if (dm < getby(i)) {
                dm = getby(i);
            }
        }
        return dm;
    }

    // ---------------------------
    public double get_y_min() {//sousuu=0のときは0.0を返す
        if (total == 0) {
            return 0.0;
        }
        double dm = getay(1);
        for (int i = 1; i <= total; i++) {
            if (dm > getay(i)) {
                dm = getay(i);
            }
            if (dm > getby(i)) {
                dm = getby(i);
            }
        }
        return dm;
    }
// ---------------------------


    // ---------------------------
    public int TL_koisa_ari_nasi(LineSegment s0) {//s0とT字またはL字で交差する折線があれば1、なければ0を返す
        for (int i = 1; i <= total; i++) {
            int i_senbun_kousa_hantei = oc.line_intersect_decide(get(i), s0, 0.00001, 0.00001);
            if (i_senbun_kousa_hantei == 21) {
                return 1;
            }
            if (i_senbun_kousa_hantei == 22) {
                return 1;
            }
            if (i_senbun_kousa_hantei == 23) {
                return 1;
            }
            if (i_senbun_kousa_hantei == 24) {
                return 1;
            }
            if (i_senbun_kousa_hantei == 25) {
                return 1;
            }
            if (i_senbun_kousa_hantei == 26) {
                return 1;
            }
            if (i_senbun_kousa_hantei == 27) {
                return 1;
            }
            if (i_senbun_kousa_hantei == 28) {
                return 1;
            }
        }
        return 0;
    }

    //-------------------------------------------------------------
    public void select_Takakukei(Polygon Taka, String Dousa_mode) {
        //select_lX,unselect_lX
        //"lX" lXは小文字のエルと大文字のエックス。Senbun s_step1と重複する部分のある線分やX交差する線分を対象にするモード。

        int i_kono_orisen_wo_kaeru = 0;//i_この折線を変える

        for (int i = 1; i <= total; i++) {
            i_kono_orisen_wo_kaeru = 0;
            LineSegment s;
            s = line(i);
            //線分s0の全部が凸多角形の外部（境界線は内部とみなさない）に存在するとき0、
            //線分s0が凸多角形の外部と境界線の両方に渡って存在するとき1、
            //線分s0が凸多角形の内部と境界線と外部に渡って存在するとき2、
            //線分s0の全部が凸多角形の境界線に存在するとき3、
            //線分s0が凸多角形の内部と境界線の両方に渡って存在するとき4、
            //線分s0の全部が凸多角形の内部（境界線は内部とみなさない）に存在するとき5、

            if (Taka.naibu_gaibu_hantei(s) == 3) {
                i_kono_orisen_wo_kaeru = 1;
            }
            if (Taka.naibu_gaibu_hantei(s) == 4) {
                i_kono_orisen_wo_kaeru = 1;
            }
            if (Taka.naibu_gaibu_hantei(s) == 5) {
                i_kono_orisen_wo_kaeru = 1;
            }


/*
			if(oc.Senbun_kasanari_hantei(s,s_step1)==1){i_kono_orisen_wo_kaeru=1;}
			if(oc.Senbun_X_kousa_hantei(s,s_step1)==1){i_kono_orisen_wo_kaeru=1;}
*/
            if (i_kono_orisen_wo_kaeru == 1) {
                if (Dousa_mode.equals("select")) {
                    s.set_i_select(2);
                }
                if (Dousa_mode.equals("unselect")) {
                    s.set_i_select(0);
                }
            }
        }

    }

    //-------------------------------------------------------------
    public void select_lX(LineSegment s_step1, String Dousa_mode) {
        //select_lX,unselect_lX
        //"lX" lXは小文字のエルと大文字のエックス。Senbun s_step1と重複する部分のある線分やX交差する線分を対象にするモード。

        int i_kono_orisen_wo_kaeru = 0;//i_この折線を変える

        for (int i = 1; i <= total; i++) {
            i_kono_orisen_wo_kaeru = 0;
            LineSegment s;
            s = line(i);

            if (oc.LineSegment_overlapping_decide(s, s_step1) == 1) {
                i_kono_orisen_wo_kaeru = 1;
            }
            if (oc.Senbun_X_kousa_hantei(s, s_step1) == 1) {
                i_kono_orisen_wo_kaeru = 1;
            }

            if (i_kono_orisen_wo_kaeru == 1) {
                if (Dousa_mode.equals("select_lX")) {
                    s.set_i_select(2);
                }
                if (Dousa_mode.equals("unselect_lX")) {
                    s.set_i_select(0);
                }
            }
        }

    }

//-------------------------------------------------------------

}
