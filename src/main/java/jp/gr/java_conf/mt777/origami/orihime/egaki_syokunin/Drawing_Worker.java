package jp.gr.java_conf.mt777.origami.orihime.egaki_syokunin;

import jp.gr.java_conf.mt777.graphic2d.circle.Circle;
import jp.gr.java_conf.mt777.graphic2d.grid.Grid;
import jp.gr.java_conf.mt777.graphic2d.linesegment.LineSegment;
import jp.gr.java_conf.mt777.graphic2d.oritacalc.OritaCalc;
import jp.gr.java_conf.mt777.graphic2d.oritacalc.straightline.StraightLine;
import jp.gr.java_conf.mt777.graphic2d.oritaoekaki.OritaDrawing;
import jp.gr.java_conf.mt777.graphic2d.point.Point;
import jp.gr.java_conf.mt777.graphic2d.polygon.Polygon;
import jp.gr.java_conf.mt777.kiroku.memo.Memo;
import jp.gr.java_conf.mt777.origami.dougu.camera.Camera;
import jp.gr.java_conf.mt777.origami.dougu.linestore.LineSegmentSet;
import jp.gr.java_conf.mt777.origami.dougu.orisensyuugou.FoldLineSet;
import jp.gr.java_conf.mt777.origami.orihime.App;
import jp.gr.java_conf.mt777.origami.orihime.LineStyle;
import jp.gr.java_conf.mt777.origami.orihime.LineColor;
import jp.gr.java_conf.mt777.origami.orihime.MouseMode;
import jp.gr.java_conf.mt777.origami.orihime.basicbranch_worker.MoveMode;
import jp.gr.java_conf.mt777.origami.orihime.egaki_syokunin.egaki_syokunin_dougubako.Drawing_Worker_Toolbox;
import jp.gr.java_conf.mt777.origami.orihime.undo_box.Undo_Box;
import jp.gr.java_conf.mt777.seiretu.narabebako.SortingBox_int_double;
import jp.gr.java_conf.mt777.seiretu.narabebako.int_double;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;


// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
public class Drawing_Worker {
    private final LineSegmentSet sen_s = new LineSegmentSet();    //Instantiation of basic branch structure
    public FoldLineSet ori_s = new FoldLineSet();    //Store polygonal lines
    public FoldLineSet ori_v = new FoldLineSet();    //Store Voronoi diagram lines
    public Grid grid = new Grid();
    public int i_drawing_stage;//Stores information about the stage of the procedure for drawing a polygonal line
    public int i_candidate_stage;//Stores information about which candidate for the procedure to draw a polygonal line
    public Polygon operationFrameBox = new Polygon(4);    //Instantiation of selection box (TV coordinates)
    public boolean i_O_F_C = false;//外周部チェック時の外周を表す線分の入力状況。0は入力未完了、1は入力完了（線分が閉多角形になっている。）
    int ir_point = 1;
    LineColor icol;//Line segment color
    LineColor h_icol = LineColor.ORANGE_4;//Auxiliary line color
    int i_hanasi = 0;//マウス位置と入力点の座標を離すなら１、離さないなら０
    boolean i_kou_mitudo_nyuuryoku = false;//1 if you use the input assist function for fine grid display, 0 if you do not use it
    Point pa = new Point(); //マウスボタンが押された位置からa点までのベクトル
    Point pb = new Point(); //マウスボタンが押された位置からb点までのベクトル
    Color circle_custom_color;//Stores custom colors for circles and auxiliary hot lines
    Undo_Box Ubox = new Undo_Box();
    Undo_Box h_Ubox = new Undo_Box();
    Point closest_point = new Point(100000.0, 100000.0); //マウス最寄の点。get_moyori_ten(Ten p)で求める。
    LineSegment closest_lineSegment = new LineSegment(100000.0, 100000.0, 100000.0, 100000.1); //マウス最寄の線分
    LineSegment closest_step_lineSegment = new LineSegment(100000.0, 100000.0, 100000.0, 100000.1); //マウス最寄のstep線分(線分追加のための準備をするための線分)。なお、ここで宣言する必要はないので、どこで宣言すべきか要検討20161113
    Circle closest_circumference = new Circle(100000.0, 100000.0, 10.0, LineColor.PURPLE_8); //Circle with the circumference closest to the mouse
    Circle closest_step_circumference = new Circle(100000.0, 100000.0, 10.0, LineColor.PURPLE_8); //Step circle with the circumference closest to the mouse
    public enum FoldLineAdditionalInputMode {
        POLY_LINE_0,
        AUX_LINE_1,
        BLACK_LINE_2,
        AUX_LIVE_LINE_3,
        BOTH_4
    }
    FoldLineAdditionalInputMode i_foldLine_additional = FoldLineAdditionalInputMode.POLY_LINE_0;//= 0 is polygonal line input = 1 is auxiliary line input mode (when inputting a line segment, these two). When deleting a line segment, the value becomes as follows. = 0 is the deletion of the polygonal line, = 1 is the deletion of the auxiliary picture line, = 2 is the deletion of the black line, = 3 is the deletion of the auxiliary live line, = 4 is the folding line, the auxiliary live line and the auxiliary picture line.
    MoveMode move_mode = MoveMode.NOTHING;    //Operation mode to move the branches. 0 = Do nothing, 1 = Move point a, 2 = Move point b, 3 = Translate branch, 4 = Add new
    int i_branch;              //Active branch number
    FoldLineSet hoj_s = new FoldLineSet();    //Store auxiliary lines
    Drawing_Worker_Toolbox e_s_dougubako = new Drawing_Worker_Toolbox(ori_s);
    Polygon trash = new Polygon(4);    //Trash instantiation
    Polygon medianStrip = new Polygon(4);    //Median strip instantiation
    double medianStrip_xmin = 180.0;
    double medianStrip_xmax = 206.0;
    double medianStrip_ymin = 50.0;
    double medianStrip_ymax = 300.0;
    double kijyun_kakudo = 22.5; //<<<<<<<<<<<<<<<<<<<<<<<基準角度<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    int id_kakudo_kei = 8;//  180/id_kakudo_keiが角度系を表す。たとえば、id_kakudo_kei=3なら180/3＝60度系、id_kakudo_kei=5なら180/5＝36度系
    double d_kakudo_kei;//d_kakudo_kei=180.0/(double)id_kakudo_kei
    double kakudo;
    int foldLineDividingNumber = 1;
    double d_naibun_s;
    double d_naibun_t;
    double d_jiyuu_kaku_1;
    double d_jiyuu_kaku_2;
    double d_jiyuu_kaku_3;
    int numPolygonCorners = 5;
    double d_decision_width = 50.0;//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<入力点が既存の点や線分と近いかどうかを判定する時の値
    int i_circle_drawing_stage;//Stores information about which stage of the circle drawing procedure
    LineSegment[] s_step = new LineSegment[1024];//Used for temporary display when drawing. Do not actually use s_step [0], but use it from s_step [1].
    Circle[] e_step = new Circle[1024];//Used for temporary display when drawing. e_step [0] is not actually used, but is used from e_step [1].
    LineSegment[] s_kouho = new LineSegment[16];//描画時の選択候補表示用に使う。s_kouho[0] は実際は使わず、s_kouho[1]から使う。
    Circle[] e_kouho = new Circle[16];//描画時の選択候補表示用に使う。e_kouho[0] は実際は使わず、e_kouho[1]から使う。
    double measured_length_1 = 0.0;
    double measured_length_2 = 0.0;
    double measured_angle_1 = 0.0;
    double measured_angle_2 = 0.0;
    double measured_angle_3 = 0.0;
    String text_cp_setumei;
    String text_cp_setumei2;
    String s_title; //Used to hold the title that appears at the top of the frame
    Camera camera = new Camera();
    boolean check1 = false;//=0 check1を実施しない、1=実施する　　
    boolean check2 = false;//=0 check2を実施しない、1=実施する　
    boolean check3 = false;//=0 check3を実施しない、1=実施する　
    boolean check4 = false;//=0 check4を実施しない、1=実施する　
    //---------------------------------
    int i_ck4_color_toukado = 100;
    App orihime_app;
    LineColor icol_temp = LineColor.BLACK_0;//Used for temporary memory of color specification
    //i_mouse_modeA==61//長方形内選択（paintの選択に似せた選択機能）の時に使う
    Point operationFrame_p1 = new Point();//TV座標
    Point operationFrame_p2 = new Point();//TV座標
    Point operationFrame_p3 = new Point();//TV座標
    Point operationFrame_p4 = new Point();//TV座標
    OperationFrameMode operationFrameMode = OperationFrameMode.NONE_0;// = 1 Create a new selection box. = 2 Move points. 3 Move the sides. 4 Move the selection box.

    Point p = new Point();
    ArrayList<LineSegment> lineSegment_vonoroi_onePoint = new ArrayList<>(); //Line segment around one point in Voronoi diagram

    // ****************************************************************************************************************************************
// **************　Variable definition so far　****************************************************************************************************
// ****************************************************************************************************************************************
    // ------------------------------------------------------------------------------------------------------------
    int i_mouse_modeA_62_point_overlapping;//Newly added p does not overlap with previously added Point = 0, overlaps = 1
    SortingBox_int_double entyou_kouho_nbox = new SortingBox_int_double();
    int i_dousa_mode = 0;
    int i_dousa_mode_henkou_kanousei = 0;//動作モード変更可能性。0なら不可能、1なら可能。
    Point moyori_point_memo = new Point();
    Point p19_1 = new Point();
    Point p19_2 = new Point();
    Point p19_3 = new Point();
    Point p19_4 = new Point();
    Point p19_a = new Point();
    Point p19_b = new Point();
    Point p19_c = new Point();
    Point p19_d = new Point();
    //--------------------------------------------
    int i_select_mode = 0;//=0は通常のセレクト操作
    //30 30 30 30 30 30 30 30 30 30 30 30 除け_線_変換
    int minrid_30;
    int i_step_for38 = 0;
    //39 39 39 39 39 39 39    i_mouse_modeA==39　;折り畳み可能線入力  qqqqqqqqq
    int i_step_for39 = 0;//i_step_for39=2の場合は、step線が1本だけになっていて、次の操作で入力折線が確定する状態
    int i_takakukei_kansei = 0;//多角形が完成したら1、未完成なら0
    // ------------
    FoldLineAdditionalInputMode i_foldLine_additional_old = FoldLineAdditionalInputMode.POLY_LINE_0;
    int i_ck4_color_toukado_sabun = 10;

    public Drawing_Worker(double r0, App app0) {  //コンストラクタ
        orihime_app = app0;

        //r_ten=r0;
        move_mode = MoveMode.NOTHING;
        i_branch = 0;
        icol = LineColor.BLACK_0;
        trash.set(new Point(10.0, 150.0), 1, new Point(0.0, 0.0));
        trash.set(new Point(10.0, 150.0), 2, new Point(50.0, 0.0));
        trash.set(new Point(10.0, 150.0), 3, new Point(40.0, 50.0));
        trash.set(new Point(10.0, 150.0), 4, new Point(10.0, 50.0));

        medianStrip.set(1, new Point(medianStrip_xmin, medianStrip_ymin));
        medianStrip.set(2, new Point(medianStrip_xmax, medianStrip_ymin));
        medianStrip.set(3, new Point(medianStrip_xmax, medianStrip_ymax));
        medianStrip.set(4, new Point(medianStrip_xmin, medianStrip_ymax));

        //taisyousei=0;

        for (int i = 0; i <= 1024 - 1; i++) {
            s_step[i] = new LineSegment();
        }
        for (int i = 0; i <= 1024 - 1; i++) {
            e_step[i] = new Circle();
        }

        for (int i = 0; i <= 16 - 1; i++) {
            s_kouho[i] = new LineSegment();
        }
        for (int i = 0; i <= 16 - 1; i++) {
            e_kouho[i] = new Circle();
        }

        text_cp_setumei = "1/";
        text_cp_setumei2 = " ";
        s_title = "no title";

        reset();
    }

    //---------------------------------
    public void reset() {
        ir_point = 1;
        ori_s.reset();
        hoj_s.reset();
        move_mode = MoveMode.NOTHING;
        i_branch = 0;

        camera.reset();
        i_drawing_stage = 0;
        i_circle_drawing_stage = 0;

    }

    public void reset_2() {
        //Enter the paper square (start)
        ori_s.addLine(-200.0, -200.0, -200.0, 200.0, LineColor.BLACK_0);
        ori_s.addLine(-200.0, -200.0, 200.0, -200.0, LineColor.BLACK_0);
        ori_s.addLine(200.0, 200.0, -200.0, 200.0, LineColor.BLACK_0);
        ori_s.addLine(200.0, 200.0, 200.0, -200.0, LineColor.BLACK_0);
        //Enter the paper square (end)
    }

    // -------------------------------------------
    public void measurement_display() {
        orihime_app.measured_length_1_display(measured_length_1);
        orihime_app.measured_length_2_display(measured_length_2);

        orihime_app.measured_angle_1_display(measured_angle_1);
        orihime_app.measured_angle_2_display(measured_angle_2);
        orihime_app.measured_angle_3_display(measured_angle_3);
    }

    //------------------------------------
    public void Memo_jyouhou_toridasi(Memo memo1) {

        boolean i_reading;
        String[] st;
        String[] s;

        // 展開図用カメラ設定の読み込み
        i_reading = false;
        for (int i = 1; i <= memo1.getLineCount(); i++) {
            String str = memo1.getLine(i);

            if (str.equals("<camera_of_orisen_nyuuryokuzu>")) {
                i_reading = true;
            } else if (str.equals("</camera_of_orisen_nyuuryokuzu>")) {
                i_reading = false;
            } else {
                if (i_reading) {
                    st = str.split(">", 2);// <-----------------------------------２つに分割するときは2を指定
                    //System.out.println(st[0]+"[___________["+st[1]);
                    if (st[0].equals("<camera_ichi_x")) {
                        s = st[1].split("<", 2);
                        orihime_app.camera_of_orisen_input_diagram.setCameraPositionX(Double.parseDouble(s[0]));
                    }        //  System.out.println(Integer.parseInt(s[0])) ;
                    if (st[0].equals("<camera_ichi_y")) {
                        s = st[1].split("<", 2);
                        orihime_app.camera_of_orisen_input_diagram.setCameraPositionY(Double.parseDouble(s[0]));
                    }
                    if (st[0].equals("<camera_kakudo")) {
                        s = st[1].split("<", 2);
                        orihime_app.camera_of_orisen_input_diagram.setCameraAngle(Double.parseDouble(s[0]));
                    }        //  System.out.println(Integer.parseInt(s[0])) ;
                    if (st[0].equals("<camera_kagami")) {
                        s = st[1].split("<", 2);
                        orihime_app.camera_of_orisen_input_diagram.setCameraMirror(Double.parseDouble(s[0]));
                    }

                    if (st[0].equals("<camera_bairitsu_x")) {
                        s = st[1].split("<", 2);
                        orihime_app.camera_of_orisen_input_diagram.setCameraZoomX(Double.parseDouble(s[0]));
                    }        //  System.out.println(Integer.parseInt(s[0])) ;
                    if (st[0].equals("<camera_bairitsu_y")) {
                        s = st[1].split("<", 2);
                        orihime_app.camera_of_orisen_input_diagram.setCameraZoomY(Double.parseDouble(s[0]));
                    }

                    if (st[0].equals("<hyouji_ichi_x")) {
                        s = st[1].split("<", 2);
                        orihime_app.camera_of_orisen_input_diagram.setDisplayPositionX(Double.parseDouble(s[0]));
                    }
                    if (st[0].equals("<hyouji_ichi_y")) {
                        s = st[1].split("<", 2);
                        orihime_app.camera_of_orisen_input_diagram.setDisplayPositionY(Double.parseDouble(s[0]));
                    }
                }
            }
        }


        // ----------------------------------------- チェックボックス等の設定の読み込み
        i_reading = false;
        for (int i = 1; i <= memo1.getLineCount(); i++) {
            String str = memo1.getLine(i);

            if (str.equals("<settei>")) {
                i_reading = true;
            } else if (str.equals("</settei>")) {
                i_reading = false;
            } else {
                if (i_reading) {
                    st = str.split(">", 2);// <-----------------------------------２つに分割するときは2を指定


                    if (st[0].equals("<ckbox_mouse_settei")) {
                        s = st[1].split("<", 2);

                        boolean selected = Boolean.parseBoolean(s[0].trim());
                        orihime_app.ckbox_mouse_settings.setSelected(selected);
                    }

                    if (st[0].equals("<ckbox_ten_sagasi")) {
                        s = st[1].split("<", 2);

                        boolean selected = Boolean.parseBoolean(s[0].trim());
                        orihime_app.ckbox_point_search.setSelected(selected);
                    }

                    if (st[0].equals("<ckbox_ten_hanasi")) {
                        s = st[1].split("<", 2);

                        boolean selected = Boolean.parseBoolean(s[0].trim());
                        orihime_app.ckbox_ten_hanasi.setSelected(selected);
                    }

                    if (st[0].equals("<ckbox_kou_mitudo_nyuuryoku")) {
                        s = st[1].split("<", 2);

                        boolean selected = Boolean.parseBoolean(s[0].trim());
                        orihime_app.ckbox_kou_mitudo_nyuuryoku.setSelected(selected);
                        set_i_kou_mitudo_nyuuryoku(selected);
                    }

                    if (st[0].equals("<ckbox_bun")) {
                        s = st[1].split("<", 2);

                        boolean selected = Boolean.parseBoolean(s[0].trim());
                        orihime_app.ckbox_bun.setSelected(selected);
                    }

                    if (st[0].equals("<ckbox_cp")) {
                        s = st[1].split("<", 2);

                        boolean selected = Boolean.parseBoolean(s[0].trim());
                        orihime_app.ckbox_cp.setSelected(selected);
                    }

                    if (st[0].equals("<ckbox_a0")) {
                        s = st[1].split("<", 2);

                        boolean selected = Boolean.parseBoolean(s[0].trim());
                        orihime_app.ckbox_a0.setSelected(selected);
                    }

                    if (st[0].equals("<ckbox_a1")) {
                        s = st[1].split("<", 2);

                        boolean selected = Boolean.parseBoolean(s[0].trim());
                        orihime_app.ckbox_a1.setSelected(selected);
                    }

                    if (st[0].equals("<ckbox_mejirusi")) {
                        s = st[1].split("<", 2);

                        boolean selected = Boolean.parseBoolean(s[0].trim());
                        orihime_app.ckbox_mejirusi.setSelected(selected);
                    }

                    if (st[0].equals("<ckbox_cp_ue")) {
                        s = st[1].split("<", 2);

                        boolean selected = Boolean.parseBoolean(s[0].trim());
                        orihime_app.ckbox_cp_ue.setSelected(selected);
                    }

                    if (st[0].equals("<ckbox_oritatami_keika")) {
                        s = st[1].split("<", 2);

                        boolean selected = Boolean.parseBoolean(s[0].trim());
                        orihime_app.ckbox_oritatami_keika.setSelected(selected);
                    }


                    if (st[0].equals("<iTenkaizuSenhaba")) {
                        s = st[1].split("<", 2);
                        orihime_app.iLineWidth = Integer.parseInt(s[0]);
                    }


                    if (st[0].equals("<ir_ten")) {
                        s = st[1].split("<", 2);
                        orihime_app.ir_ten = Integer.parseInt(s[0]);
                        set_ir_ten(orihime_app.ir_ten);
                    }


                    if (st[0].equals("<i_orisen_hyougen")) {
                        s = st[1].split("<", 2);
                        orihime_app.lineStyle = LineStyle.valueOf(s[0].trim());
                    }


                    if (st[0].equals("<i_anti_alias")) {
                        s = st[1].split("<", 2);
                        orihime_app.antiAlias = Boolean.parseBoolean(s[0].trim());
                    }
                }
            }
        }


        // ----------------------------------------- 格子設定の読み込み


        i_reading = false;
        for (int i = 1; i <= memo1.getLineCount(); i++) {
            String str = memo1.getLine(i);

            if (str.equals("<Kousi>")) {
                i_reading = true;
            } else if (str.equals("</Kousi>")) {
                i_reading = false;
            } else {
                if (i_reading) {
                    st = str.split(">", 2);// <-----------------------------------２つに分割するときは2を指定

                    if (st[0].equals("<i_kitei_jyoutai")) {
                        s = st[1].split("<", 2);
                        setBaseState(Grid.State.from(s[0]));
                    }
                    //  System.out.println(Integer.parseInt(s[0])) ;

                    if (st[0].equals("<nyuuryoku_kitei")) {
                        s = st[1].split("<", 2);
                        orihime_app.text1.setText(s[0]);
                        orihime_app.set_grid_bunkatu_suu();
                        //set_kousi_bunkatu_suu(Integer.parseInt(s[0]));

                    }
                    //  System.out.println(Integer.parseInt(s[0])) ;


                    if (st[0].equals("<memori_kankaku")) {
                        s = st[1].split("<", 2);
                        orihime_app.scale_interval = Integer.parseInt(s[0]);
                        orihime_app.text25.setText(s[0]);

                        set_a_to_parallel_scale_interval(orihime_app.scale_interval);
                        set_b_to_parallel_scale_interval(orihime_app.scale_interval);
                    }


                    if (st[0].equals("<a_to_heikouna_memori_iti")) {
                        s = st[1].split("<", 2);
                        grid.set_a_to_parallel_scale_position(Integer.parseInt(s[0]));
                    }
                    if (st[0].equals("<b_to_heikouna_memori_iti")) {
                        s = st[1].split("<", 2);
                        grid.set_b_to_parallel_scale_position(Integer.parseInt(s[0]));
                    }
                    if (st[0].equals("<kousi_senhaba")) {
                        s = st[1].split("<", 2);
                        grid.setGridLineWidth(Integer.parseInt(s[0]));
                    }


                    if (st[0].equals("<d_kousi_x_a")) {
                        s = st[1].split("<", 2);
                        orihime_app.text18.setText(s[0]);
                    }
                    if (st[0].equals("<d_kousi_x_b")) {
                        s = st[1].split("<", 2);
                        orihime_app.text19.setText(s[0]);
                    }
                    if (st[0].equals("<d_kousi_x_c")) {
                        s = st[1].split("<", 2);
                        orihime_app.text20.setText(s[0]);
                    }

                    if (st[0].equals("<d_kousi_y_a")) {
                        s = st[1].split("<", 2);
                        orihime_app.text21.setText(s[0]);
                    }
                    if (st[0].equals("<d_kousi_y_b")) {
                        s = st[1].split("<", 2);
                        orihime_app.text22.setText(s[0]);
                    }
                    if (st[0].equals("<d_kousi_y_c")) {
                        s = st[1].split("<", 2);
                        orihime_app.text23.setText(s[0]);
                    }

                    if (st[0].equals("<d_kousi_kakudo")) {
                        s = st[1].split("<", 2);
                        orihime_app.text24.setText(s[0]);
                    }

                    orihime_app.setGrid();
                }
            }
        }


        // ----------------------------------------- 格子色設定の読み込み
        int i_grid_color_R = 0;
        int i_grid_color_G = 0;
        int i_grid_color_B = 0;
        int i_grid_memori_color_R = 0;
        int i_grid_memori_color_G = 0;
        int i_grid_memori_color_B = 0;

        boolean i_Grid_iro_yomikomi = false;//Kousi_iroの読み込みがあったら1、なければ0
        i_reading = false;
        for (int i = 1; i <= memo1.getLineCount(); i++) {
            String str = memo1.getLine(i);

            if (str.equals("<Kousi_iro>")) {
                i_reading = true;
                i_Grid_iro_yomikomi = true;
            } else if (str.equals("</Kousi_iro>")) {
                i_reading = false;
            } else {
                if (i_reading) {
                    st = str.split(">", 2);// <-----------------------------------２つに分割するときは2を指定


                    if (st[0].equals("<kousi_color_R")) {
                        s = st[1].split("<", 2);
                        i_grid_color_R = (Integer.parseInt(s[0]));
                    }        //  System.out.println(Integer.parseInt(s[0])) ;
                    if (st[0].equals("<kousi_color_G")) {
                        s = st[1].split("<", 2);
                        i_grid_color_G = (Integer.parseInt(s[0]));
                    }
                    if (st[0].equals("<kousi_color_B")) {
                        s = st[1].split("<", 2);
                        i_grid_color_B = (Integer.parseInt(s[0]));
                    }

                    if (st[0].equals("<kousi_memori_color_R")) {
                        s = st[1].split("<", 2);
                        i_grid_memori_color_R = (Integer.parseInt(s[0]));
                    }
                    if (st[0].equals("<kousi_memori_color_G")) {
                        s = st[1].split("<", 2);
                        i_grid_memori_color_G = (Integer.parseInt(s[0]));
                    }
                    if (st[0].equals("<kousi_memori_color_B")) {
                        s = st[1].split("<", 2);
                        i_grid_memori_color_B = (Integer.parseInt(s[0]));
                    }


                }
            }
        }

        if (i_Grid_iro_yomikomi) {//Grid_iroの読み込みがあったら1、なければ0
            grid.setGridColor(new Color(i_grid_color_R, i_grid_color_G, i_grid_color_B)); //gridの色

            System.out.println("i_kousi_memori_color_R= " + i_grid_memori_color_R);
            System.out.println("i_kousi_memori_color_G= " + i_grid_memori_color_G);
            System.out.println("i_kousi_memori_color_B= " + i_grid_memori_color_B);
            orihime_app.kus.setGridScaleColor(new Color(i_grid_memori_color_R, i_grid_memori_color_G, i_grid_memori_color_B)); //grid_memoriの色

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
        i_reading = false;
        for (int i = 1; i <= memo1.getLineCount(); i++) {
            String str = memo1.getLine(i);

            if (str.equals("<oriagarizu>")) {
                i_reading = true;
                i_oriagarizu_yomikomi = true;
            } else if (str.equals("</oriagarizu>")) {
                i_reading = false;
            } else {
                if (i_reading) {
                    st = str.split(">", 2);// <-----------------------------------２つに分割するときは2を指定

                    if (st[0].equals("<oriagarizu_F_color_R")) {
                        s = st[1].split("<", 2);
                        i_oriagarizu_F_color_R = (Integer.parseInt(s[0]));
                    }        //  System.out.println(Integer.parseInt(s[0])) ;
                    if (st[0].equals("<oriagarizu_F_color_G")) {
                        s = st[1].split("<", 2);
                        i_oriagarizu_F_color_G = (Integer.parseInt(s[0]));
                    }
                    if (st[0].equals("<oriagarizu_F_color_B")) {
                        s = st[1].split("<", 2);
                        i_oriagarizu_F_color_B = (Integer.parseInt(s[0]));
                    }

                    if (st[0].equals("<oriagarizu_B_color_R")) {
                        s = st[1].split("<", 2);
                        i_oriagarizu_B_color_R = (Integer.parseInt(s[0]));
                    }        //  System.out.println(Integer.parseInt(s[0])) ;
                    if (st[0].equals("<oriagarizu_B_color_G")) {
                        s = st[1].split("<", 2);
                        i_oriagarizu_B_color_G = (Integer.parseInt(s[0]));
                    }
                    if (st[0].equals("<oriagarizu_B_color_B")) {
                        s = st[1].split("<", 2);
                        i_oriagarizu_B_color_B = (Integer.parseInt(s[0]));
                    }

                    if (st[0].equals("<oriagarizu_L_color_R")) {
                        s = st[1].split("<", 2);
                        i_oriagarizu_L_color_R = (Integer.parseInt(s[0]));
                    }        //  System.out.println(Integer.parseInt(s[0])) ;
                    if (st[0].equals("<oriagarizu_L_color_G")) {
                        s = st[1].split("<", 2);
                        i_oriagarizu_L_color_G = (Integer.parseInt(s[0]));
                    }
                    if (st[0].equals("<oriagarizu_L_color_B")) {
                        s = st[1].split("<", 2);
                        i_oriagarizu_L_color_B = (Integer.parseInt(s[0]));
                    }

                    //  System.out.println(Integer.parseInt(s[0])) ;
                }
            }
        }

        if (i_oriagarizu_yomikomi) {
            orihime_app.OZ.ct_worker.set_F_color(new Color(i_oriagarizu_F_color_R, i_oriagarizu_F_color_G, i_oriagarizu_F_color_B)); //表面の色
            orihime_app.Button_F_color.setBackground(new Color(i_oriagarizu_F_color_R, i_oriagarizu_F_color_G, i_oriagarizu_F_color_B));    //ボタンの色設定

            orihime_app.OZ.ct_worker.set_B_color(new Color(i_oriagarizu_B_color_R, i_oriagarizu_B_color_G, i_oriagarizu_B_color_B));//裏面の色
            orihime_app.Button_B_color.setBackground(new Color(i_oriagarizu_B_color_R, i_oriagarizu_B_color_G, i_oriagarizu_B_color_B));//ボタンの色設定

            orihime_app.OZ.ct_worker.set_L_color(new Color(i_oriagarizu_L_color_R, i_oriagarizu_L_color_G, i_oriagarizu_L_color_B));        //線の色
            orihime_app.Button_L_color.setBackground(new Color(i_oriagarizu_L_color_R, i_oriagarizu_L_color_G, i_oriagarizu_L_color_B));        //ボタンの色設定
        }
    }

    //-----------------------------
    public String setMemo_for_redo_undo(Memo memo1) {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<undo,redoでのkiroku復元用
        return ori_s.setMemo(memo1);
    }

    //-----------------------------
    public void setMemo_for_yomikomi(Memo memo1) {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<データ読み込み用
        Memo_jyouhou_toridasi(memo1);
        ori_s.setMemo(memo1);
        hoj_s.h_setMemo(memo1);
    }

    //-----------------------------
    public void setMemo_for_yomikomi_tuika(Memo memo1) {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<データ読み込み用
        double addx, addy;

        FoldLineSet ori_s_temp = new FoldLineSet();    //追加された折線だけ取り出すために使う
        ori_s_temp.setMemo(memo1);//追加された折線だけ取り出してori_s_tempを作る
        addx = ori_s.get_x_max() + 100.0 - ori_s_temp.get_x_min();
        addy = ori_s.get_y_max() - ori_s_temp.get_y_max();

        ori_s_temp.move(addx, addy);//全体を移動する

        int sousuu_old = ori_s.getTotal();
        ori_s.addMemo(ori_s_temp.getMemo());
        int sousuu_new = ori_s.getTotal();
        ori_s.intersect_divide(1, sousuu_old, sousuu_old + 1, sousuu_new);

        ori_s.unselect_all();
        record();
    }

    //-----------------------------
    public void h_setMemo(Memo memo1) {
        hoj_s.h_setMemo(memo1);
    }

    //-----------------------------
    public void setCamera(Camera cam0) {
        camera.setCamera(cam0);

        calc_d_decision_haba();
    }

    public void set_sen_tokutyuu_color(Color c0) {
        circle_custom_color = c0;
    }

    //-----------------------------
    public void zen_yama_tani_henkan() {
        ori_s.zen_yama_tani_henkan();
    }

    //----------------
    public void branch_trim(double r) {
        ori_s.branch_trim(r);
    }

    //----------------------------------------------
    public LineSegmentSet get() {
        sen_s.setMemo(ori_s.getMemo());
        return sen_s;
    }

    public LineSegmentSet get_for_folding() {
        sen_s.setMemo(ori_s.getMemo_for_folding());
        return sen_s;
    }

    //折畳み推定用にselectされた線分集合の折線数を intとして出力する。//icolが3(cyan＝水色)以上の補助線はカウントしない
    public int get_orisensuu_for_select_oritatami() {
        return ori_s.get_foldLineTotal_for_select_folding();
    }

    public LineSegmentSet get_for_select_oritatami() {//selectした折線で折り畳み推定をする。
        sen_s.setMemo(ori_s.getMemo_for_select_folding());
        return sen_s;
    }

    //--------------------------------------------
    //public void set_r(double r0){r_ten=r0;}
    public void set_ir_ten(int i0) {
        ir_point = i0;
    }

    //--------------------------------------------
    public void set_grid_bunkatu_suu(int i) {
        grid.set_grid_bunkatu_suu(i);
        text_cp_setumei = "1/" + grid.divisionNumber();
        calc_d_decision_haba();
    }

    public void calc_d_decision_haba() {
        d_decision_width = grid.d_width() / 4.0;
        if (camera.getCameraZoomX() * d_decision_width < 10.0) {
            d_decision_width = 10.0 / camera.getCameraZoomX();
        }
    }

    // ----------------------------------------
    public void set_d_grid(double dkxn, double dkyn, double dkk) {
        grid.set_d_grid(dkxn, dkyn, dkk);
    }

    //--------------------------------------------
    public int getTotal() {
        return ori_s.getTotal();
    }

    //-----------------------------
    public void set_kijyun_kakudo(double x) {
        kijyun_kakudo = x;
    } //<<<<<<<<<<<<<<<<<<<<<<<基準角度<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    //------------------------
    public Memo getMemo() {
        return ori_s.getMemo();
    }

    //getMemo(String s_title)はundo,redoのkiroku用
    public Memo getMemo(String s_title) {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<undo,redoのkiroku用
        Memo memo_temp = new Memo();
        memo_temp.set(ori_s.getMemo(s_title));

        Memo_jyouhou_tuika(memo_temp);
        return memo_temp;
    }

    //------------------------
    public Memo h_getMemo() {
        return hoj_s.h_getMemo();
    }

    //------------------------
    public Memo getMemo_for_export() {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<データ書き出し

        Memo memo_temp = new Memo();
        memo_temp.set(ori_s.getMemo());
        memo_temp.addMemo(hoj_s.h_getMemo());
        Memo_jyouhou_tuika(memo_temp);
        return memo_temp;
    }

    //------------------------svgデータ書き出し
    public Memo getMemo_for_svg_export_with_camera(boolean i_bun_hyouji, boolean i_cp_display, boolean i_a0_hyouji, boolean i_a1_hyouji, float fTenkaizuSenhaba, LineStyle lineStyle, float f_h_TenkaizuSenhaba, int p0x_max, int p0y_max, boolean i_mejirusi_hyouji) {//引数はカメラ設定、線幅、画面X幅、画面y高さ
        Memo memo_temp = new Memo();

        LineSegment s_tv = new LineSegment();
        Point a = new Point();
        Point b = new Point();

        // ------------------------------------------------------

        String str_stroke = "";
        String str_strokewidth;
        str_strokewidth = Integer.toString(orihime_app.iLineWidth);
        // ------------------------------------------------------

        //展開図の描画  補助活線以外の折線
        if (i_cp_display) {
            for (int i = 1; i <= ori_s.getTotal(); i++) {
                LineColor color = ori_s.getColor(i);
                if (color.isFoldingLine()) {
                    str_stroke = switch (color) {
                        case BLACK_0 -> "black";
                        case RED_1 -> "red";
                        case BLUE_2 -> "blue";
                        default -> throw new IllegalStateException("Not a folding line: " + color);
                    };

                    if (lineStyle == LineStyle.BLACK_TWO_DOT || lineStyle == LineStyle.BLACK_ONE_DOT) {
                        str_stroke = "black";
                    }

                    String str_stroke_dasharray = switch (lineStyle) {
                        case COLOR -> "";
                        case COLOR_AND_SHAPE, BLACK_ONE_DOT -> switch (color) {
                            case RED_1 -> "stroke-dasharray=\"10 3 3 3\""; //基本指定A　　線の太さや線の末端の形状
                            case BLUE_2 -> "stroke-dasharray=\"8 8\""; //dash_M1,一点鎖線
                            default -> "";
                        };
                        case BLACK_TWO_DOT -> switch (color) {
                            case RED_1 -> "stroke-dasharray=\"10 3 3 3 3 3\""; //基本指定A　　線の太さや線の末端の形状
                            case BLUE_2 -> "stroke-dasharray=\"8 8\""; //dash_M2,二点鎖線
                            default -> "";
                        };
                    };

                    s_tv.set(camera.object2TV(ori_s.get(i)));
                    a.set(s_tv.getA());
                    b.set(s_tv.getB());//a.set(s_tv.getax()+0.000001,s_tv.getay()+0.000001); b.set(s_tv.getbx()+0.000001,s_tv.getby()+0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

                    BigDecimal b_ax = new BigDecimal(String.valueOf(a.getX()));
                    double x1 = b_ax.setScale(2, RoundingMode.HALF_UP).doubleValue();
                    BigDecimal b_ay = new BigDecimal(String.valueOf(a.getY()));
                    double y1 = b_ay.setScale(2, RoundingMode.HALF_UP).doubleValue();
                    BigDecimal b_bx = new BigDecimal(String.valueOf(b.getX()));
                    double x2 = b_bx.setScale(2, RoundingMode.HALF_UP).doubleValue();
                    BigDecimal b_by = new BigDecimal(String.valueOf(b.getY()));
                    double y2 = b_by.setScale(2, RoundingMode.HALF_UP).doubleValue();


                    memo_temp.addLine("<line x1=\"" + x1 + "\"" +
                            " y1=\"" + y1 + "\"" +
                            " x2=\"" + x2 + "\"" +
                            " y2=\"" + y2 + "\"" +
                            " " + str_stroke_dasharray + " " +
                            " stroke=\"" + str_stroke + "\"" +
                            " stroke-width=\"" + str_strokewidth + "\"" + " />");

                    if (ir_point != 0) {
                        if (fTenkaizuSenhaba < 2.0f) {//頂点の黒い正方形を描く
                            int i_haba = ir_point;

                            memo_temp.addLine("<rect style=\"fill:#000000;stroke:#000000;stroke-width:1\"" +
                                    " width=\"" + (2.0 * (double) i_haba + 1.0) + "\"" +
                                    " height=\"" + (2.0 * (double) i_haba + 1.0) + "\"" +
                                    " x=\"" + (x1 - (double) i_haba) + "\"" +
                                    " y=\"" + (y1 - (double) i_haba) + "\"" +
                                    " />");

                            memo_temp.addLine("<rect style=\"fill:#000000;stroke:#000000;stroke-width:1\"" +
                                    " width=\"" + (2.0 * (double) i_haba + 1.0) + "\"" +
                                    " height=\"" + (2.0 * (double) i_haba + 1.0) + "\"" +
                                    " x=\"" + (x2 - (double) i_haba) + "\"" +
                                    " y=\"" + (y2 - (double) i_haba) + "\"" +
                                    " />");
                        }
                    }

                    if (fTenkaizuSenhaba >= 2.0f) {//  太線
                        //g2.setStroke(new BasicStroke(1.0f+fTenkaizuSenhaba%1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定
                        if (ir_point != 0) {
                            //int i_haba=(int)fTenkaizuSenhaba+ir_point;//int i_haba=2;
                            double d_haba = (double) fTenkaizuSenhaba / 2.0 + (double) ir_point;//int i_haba=2;

                            memo_temp.addLine("<circle style=\"fill:#ffffff;stroke:#000000;stroke-width:1\"" +
                                    " r=\"" + d_haba + "\"" +
                                    " cx=\"" + x1 + "\"" +
                                    " cy=\"" + y1 + "\"" +
                                    " />");


                            memo_temp.addLine("<circle style=\"fill:#ffffff;stroke:#000000;stroke-width:1\"" +
                                    " r=\"" + d_haba + "\"" +
                                    " cx=\"" + x2 + "\"" +
                                    " cy=\"" + y2 + "\"" +
                                    " />");
                        }
                    }


                }
            }
        }


        return memo_temp;
    }

    //---------------------------------------------------------------------------------------------------------------------------------
    public void Memo_jyouhou_tuika(Memo memo1) {
        memo1.addLine("<camera_of_orisen_nyuuryokuzu>");
        memo1.addLine("<camera_ichi_x>" + camera.getCameraPositionX() + "</camera_ichi_x>");
        memo1.addLine("<camera_ichi_y>" + camera.getCameraPositionY() + "</camera_ichi_y>");
        memo1.addLine("<camera_kakudo>" + camera.getCameraAngle() + "</camera_kakudo>");
        memo1.addLine("<camera_kagami>" + camera.getCameraMirror() + "</camera_kagami>");
        memo1.addLine("<camera_bairitsu_x>" + camera.getCameraZoomX() + "</camera_bairitsu_x>");
        memo1.addLine("<camera_bairitsu_y>" + camera.getCameraZoomY() + "</camera_bairitsu_y>");
        memo1.addLine("<hyouji_ichi_x>" + camera.getDisplayPositionX() + "</hyouji_ichi_x>");
        memo1.addLine("<hyouji_ichi_y>" + camera.getDisplayPositionY() + "</hyouji_ichi_y>");
        memo1.addLine("</camera_of_orisen_nyuuryokuzu>");


        // ----------------------------------------------------------------------
        memo1.addLine("<settei>");
        memo1.addLine("<ckbox_mouse_settei>" + orihime_app.ckbox_mouse_settings.isSelected() + "</ckbox_mouse_settei>");
        memo1.addLine("<ckbox_ten_sagasi>" + orihime_app.ckbox_point_search.isSelected() + "</ckbox_ten_sagasi>");
        memo1.addLine("<ckbox_ten_hanasi>" + orihime_app.ckbox_ten_hanasi.isSelected() + "</ckbox_ten_hanasi>");
        memo1.addLine("<ckbox_kou_mitudo_nyuuryoku>" + orihime_app.ckbox_kou_mitudo_nyuuryoku.isSelected() + "</ckbox_kou_mitudo_nyuuryoku>");
        memo1.addLine("<ckbox_bun>" + orihime_app.ckbox_bun.isSelected() + "</ckbox_bun>");
        memo1.addLine("<ckbox_cp>" + orihime_app.ckbox_cp.isSelected() + "</ckbox_cp>");
        memo1.addLine("<ckbox_a0>" + orihime_app.ckbox_a0.isSelected() + "</ckbox_a0>");
        memo1.addLine("<ckbox_a1>" + orihime_app.ckbox_a1.isSelected() + "</ckbox_a1>");
        memo1.addLine("<ckbox_mejirusi>" + orihime_app.ckbox_mejirusi.isSelected() + "</ckbox_mejirusi>");
        memo1.addLine("<ckbox_cp_ue>" + orihime_app.ckbox_cp_ue.isSelected() + "</ckbox_cp_ue>");
        memo1.addLine("<ckbox_oritatami_keika>" + orihime_app.ckbox_oritatami_keika.isSelected() + "</ckbox_oritatami_keika>");
        //展開図の線の太さ。
        memo1.addLine("<iTenkaizuSenhaba>" + orihime_app.iLineWidth + "</iTenkaizuSenhaba>");
        //頂点のしるしの幅
        memo1.addLine("<ir_ten>" + orihime_app.ir_ten + "</ir_ten>");
        //折線表現を色で表す
        memo1.addLine("<i_orisen_hyougen>" + orihime_app.lineStyle + "</i_orisen_hyougen>");
//A_A
        memo1.addLine("<i_anti_alias>" + orihime_app.antiAlias + "</i_anti_alias>");
        memo1.addLine("</settei>");

        // ----------------------------------------------------------------------


        memo1.addLine("<Kousi>");

        memo1.addLine("<i_kitei_jyoutai>" + getBaseState() + "</i_kitei_jyoutai>");
        memo1.addLine("<nyuuryoku_kitei>" + orihime_app.nyuuryoku_kitei + "</nyuuryoku_kitei>");

        memo1.addLine("<memori_kankaku>" + orihime_app.scale_interval + "</memori_kankaku>");
        memo1.addLine("<a_to_heikouna_memori_iti>" + grid.get_a_to_parallel_scale_position() + "</a_to_heikouna_memori_iti>");
        memo1.addLine("<b_to_heikouna_memori_iti>" + grid.get_b_to_parallel_scale_position() + "</b_to_heikouna_memori_iti>");
        memo1.addLine("<kousi_senhaba>" + grid.getGridLIneWidth() + "</kousi_senhaba>");

        memo1.addLine("<d_kousi_x_a>" + orihime_app.d_grid_x_a + "</d_kousi_x_a>");
        memo1.addLine("<d_kousi_x_b>" + orihime_app.d_grid_x_b + "</d_kousi_x_b>");
        memo1.addLine("<d_kousi_x_c>" + orihime_app.d_grid_x_c + "</d_kousi_x_c>");
        memo1.addLine("<d_kousi_y_a>" + orihime_app.d_grid_y_a + "</d_kousi_y_a>");
        memo1.addLine("<d_kousi_y_b>" + orihime_app.d_grid_y_b + "</d_kousi_y_b>");
        memo1.addLine("<d_kousi_y_c>" + orihime_app.d_grid_y_c + "</d_kousi_y_c>");
        memo1.addLine("<d_kousi_kakudo>" + orihime_app.d_grid_angle + "</d_kousi_kakudo>");
        //memo1.addGyou("<d_kousi_x_nagasa>"+orihime_ap.d_kousi_x_nagasa+"</d_kousi_x_nagasa>");
        //memo1.addGyou("<d_kousi_y_nagasa>"+orihime_ap.d_kousi_y_nagasa+"</d_kousi_y_nagasa>");


        memo1.addLine("</Kousi>");
        // ----------------------------------------------------------------------

        memo1.addLine("<Kousi_iro>");

        memo1.addLine("<kousi_color_R>" + grid.getGridColor().getRed() + "</kousi_color_R>");
        memo1.addLine("<kousi_color_G>" + grid.getGridColor().getGreen() + "</kousi_color_G>");
        memo1.addLine("<kousi_color_B>" + grid.getGridColor().getBlue() + "</kousi_color_B>");

        memo1.addLine("<kousi_memori_color_R>" + grid.getGridScaleColor().getRed() + "</kousi_memori_color_R>");
        memo1.addLine("<kousi_memori_color_G>" + grid.getGridScaleColor().getGreen() + "</kousi_memori_color_G>");
        memo1.addLine("<kousi_memori_color_B>" + grid.getGridScaleColor().getBlue() + "</kousi_memori_color_B>");

        memo1.addLine("</Kousi_iro>");


        // ----------------------------------------------------------------------

        memo1.addLine("<oriagarizu>");

        memo1.addLine("<oriagarizu_F_color_R>" + orihime_app.OZ.foldedFigure_F_color.getRed() + "</oriagarizu_F_color_R>");
        memo1.addLine("<oriagarizu_F_color_G>" + orihime_app.OZ.foldedFigure_F_color.getGreen() + "</oriagarizu_F_color_G>");
        memo1.addLine("<oriagarizu_F_color_B>" + orihime_app.OZ.foldedFigure_F_color.getBlue() + "</oriagarizu_F_color_B>");

        memo1.addLine("<oriagarizu_B_color_R>" + orihime_app.OZ.foldedFigure_B_color.getRed() + "</oriagarizu_B_color_R>");
        memo1.addLine("<oriagarizu_B_color_G>" + orihime_app.OZ.foldedFigure_B_color.getGreen() + "</oriagarizu_B_color_G>");
        memo1.addLine("<oriagarizu_B_color_B>" + orihime_app.OZ.foldedFigure_B_color.getBlue() + "</oriagarizu_B_color_B>");

        memo1.addLine("<oriagarizu_L_color_R>" + orihime_app.OZ.foldedFigure_L_color.getRed() + "</oriagarizu_L_color_R>");
        memo1.addLine("<oriagarizu_L_color_G>" + orihime_app.OZ.foldedFigure_L_color.getGreen() + "</oriagarizu_L_color_G>");
        memo1.addLine("<oriagarizu_L_color_B>" + orihime_app.OZ.foldedFigure_L_color.getBlue() + "</oriagarizu_L_color_B>");


        memo1.addLine("</oriagarizu>");

    }

    //---------------------------------
    public void setColor(LineColor i) {
        icol = i;
    }

    //---------------------------------
    public int get_ieda() {
        return i_branch;
    }


// ------------------------------------

    //不要な線分を消去する-----------------------------------------------
    public void gomisute() {

        for (int i = 1; i <= ori_s.getTotal(); i++) {
            int idel = 0;


            if (trash.convex_inside(ori_s.get(i))) {
                idel = 1;
            }


            if (idel == 1) {
                ori_s.deleteLine(i);
                i = i - 1;
                i_branch = ori_s.getTotal() + 1;    //<<<<<<<<<<<<<<<<<<
            }
        }
    }

// ------------------------------------

    public void bunkatu_seiri() {
        ori_s.divide_seiri();
    }

    public void kousabunkatu() {
        ori_s.intersect_divide();
    }

    public void point_removal() {
        ori_s.point_removal();
    }

    public void point_removal(double r) {
        ori_s.point_removal(r);
    }

    public void overlapping_line_removal() {
        ori_s.overlapping_line_removal();
    }

    public void overlapping_line_removal(double r) {
        ori_s.overlapping_line_removal(r);
    }

//------------------------------

    public String undo() {
        s_title = setMemo_for_redo_undo(Ubox.undo());

        if (check1) {
            check1(0.001, 0.5);
        }
        if (check2) {
            check2(0.01, 0.5);
        }
        if (check3) {
            check3(0.0001);
        }
        if (check4) {
            check4(0.0001);
        }

        return s_title;
    }

    public String redo() {
        s_title = setMemo_for_redo_undo(Ubox.redo());

        if (check1) {
            check1(0.001, 0.5);
        }
        if (check2) {
            check2(0.01, 0.5);
        }
        if (check3) {
            check3(0.0001);
        }
        if (check4) {
            check4(0.0001);
        }

        return s_title;
    }

    public void setTitle(String s_title0) {
        s_title = s_title0;
    }

    public void record() {
        if (check1) {
            check1(0.001, 0.5);
        }
        if (check2) {
            check2(0.01, 0.5);
        }
        if (check3) {
            check3(0.0001);
        }
        if (check4) {
            check4(0.0001);
        }

        Ubox.record(getMemo(s_title));
    }

    public void h_undo() {
        h_setMemo(h_Ubox.undo());
    }

    public void h_redo() {
        h_setMemo(h_Ubox.redo());
    }

//--------------------------------------------------------------------------------------
//マウス操作----------------------------------------------------------------------------
//--------------------------------------------------------------------------------------

    public void h_kiroku() {
        h_Ubox.record(h_getMemo());
    }

    //------------------------------------------------------------------------------
//基本枝の描画111111111111111111111111111111111111111111111111111111111111111111		//System.out.println("_");
//------------------------------------------------------------------------------
    public void draw_with_camera(Graphics g, boolean i_bun_display, boolean i_cp_display, boolean i_a0_display, boolean i_a1_display, float fTenkaizuSenhaba, LineStyle lineStyle, float f_h_TenkaizuSenhaba, int p0x_max, int p0y_max, boolean i_mejirusi_display) {//引数はカメラ設定、線幅、画面X幅、画面y高さ
        Graphics2D g2 = (Graphics2D) g;

        LineSegment s_tv = new LineSegment();
        Point a = new Point();
        Point b = new Point();

        // ------------------------------------------------------

        //格子線の描画
        grid.draw(g, camera, p0x_max, p0y_max, i_kou_mitudo_nyuuryoku);

        BasicStroke BStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
        g2.setStroke(BStroke);//線の太さや線の末端の形状

        //補助画線（折線と非干渉）の描画
        if (i_a1_display) {
            g2.setStroke(new BasicStroke(f_h_TenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状
            for (int i = 1; i <= hoj_s.getTotal(); i++) {
                g_setColor(g, hoj_s.getColor(i));

                s_tv.set(camera.object2TV(hoj_s.get(i)));
                a.set(s_tv.getAX() + 0.000001, s_tv.getAY() + 0.000001);
                b.set(s_tv.getBX() + 0.000001, s_tv.getBY() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

                g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線

                if (fTenkaizuSenhaba < 2.0f) {//頂点の正方形を描く
                    g.setColor(Color.black);
                    int i_haba = ir_point;
                    g.fillRect((int) a.getX() - i_haba, (int) a.getY() - i_haba, 2 * i_haba + 1, 2 * i_haba + 1); //正方形を描く//g.fillRect(10, 10, 100, 50);長方形を描く
                    g.fillRect((int) b.getX() - i_haba, (int) b.getY() - i_haba, 2 * i_haba + 1, 2 * i_haba + 1); //正方形を描く
                }

                if (fTenkaizuSenhaba >= 2.0f) {//  太線
                    g2.setStroke(new BasicStroke(1.0f + f_h_TenkaizuSenhaba % 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状

                    if (ir_point != 0) {

                        int i_haba = (int) fTenkaizuSenhaba + ir_point;//int i_haba=2;
                        double d_haba = (double) fTenkaizuSenhaba / 2.0 + (double) ir_point;//int i_haba=2;

                        g.setColor(Color.white);
                        g2.fill(new Ellipse2D.Double(a.getX() - d_haba, a.getY() - d_haba, 2.0 * d_haba, 2.0 * d_haba));

                        g.setColor(Color.black);
                        g2.draw(new Ellipse2D.Double(a.getX() - d_haba, a.getY() - d_haba, 2.0 * d_haba, 2.0 * d_haba));

                        g.setColor(Color.white);
                        g2.fill(new Ellipse2D.Double(b.getX() - d_haba, b.getY() - d_haba, 2.0 * d_haba, 2.0 * d_haba));

                        g.setColor(Color.black);
                        g2.draw(new Ellipse2D.Double(b.getX() - d_haba, b.getY() - d_haba, 2.0 * d_haba, 2.0 * d_haba));
                    }

                    g2.setStroke(new BasicStroke(f_h_TenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状

                }
            }
        }

        // ----------------------------------------------------------------

        //check結果の表示

        g2.setStroke(new BasicStroke(15.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定


        //Check1Senbには0番目からsize()-1番目までデータが入っている
        if (check1) {
            for (int i = 0; i < ori_s.check1_size(); i++) {
                LineSegment s_temp = new LineSegment();
                s_temp.set(ori_s.check1_getSenbun(i));
                OritaDrawing.pointingAt1(g, camera.object2TV(s_temp), 7.0, 3.0, 1);
            }
        }

        if (check2) {
            for (int i = 0; i < ori_s.check2_size(); i++) {
                LineSegment s_temp = new LineSegment();
                s_temp.set(ori_s.check2_getSenbun(i));
                OritaDrawing.pointingAt2(g, camera.object2TV(s_temp), 7.0, 3.0, 1);
            }
        }

        g2.setStroke(new BasicStroke(25.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定


        //Check4Senbには0番目からsize()-1番目までデータが入っている
        //System.out.println("ori_s.check4_size() = "+ori_s.check4_size());
        if (check4) {
            for (int i = 0; i < ori_s.check4_size(); i++) {
                LineSegment s_temp = new LineSegment();
                s_temp.set(ori_s.check4_getSenbun(i));
                OritaDrawing.pointingAt4(g, camera.object2TV(s_temp), i_ck4_color_toukado);
            }
        }


        //Check3Senbには0番目からsize()-1番目までデータが入っている
        if (check3) {
            for (int i = 0; i < ori_s.check3_size(); i++) {
                LineSegment s_temp = new LineSegment();
                s_temp.set(ori_s.check3_getSenbun(i));
                //OO.jyuuji(g,camera.object2TV(s_temp.geta()), 7.0 , 3.0 , 1);
                OritaDrawing.pointingAt3(g, camera.object2TV(s_temp), 7.0, 3.0, 1);
            }
        }


        //System.out.println(" E 20170201_4");

        //camera中心を十字で描く
        if (i_mejirusi_display) {
            OritaDrawing.cross(g, camera.object2TV(camera.get_camera_position()), 5.0, 2.0, LineColor.CYAN_3);
        }


        //System.out.println(" E 20170201_5");


        //円を描く　
        //System.out.println(" 円を描く ori_s.cir_size()="+ori_s.cir_size());
        if (i_a0_display) {
            for (int i = 1; i <= ori_s.cir_size(); i++) {

                double d_haba;
                Circle e_temp = new Circle();
                e_temp.set(ori_s.getCircle(i));

                a.set(camera.object2TV(e_temp.getCenter()));//この場合のaは描画座標系での円の中心の位置
                //a.set(a.getx()+0.000001,a.gety()+0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

                g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状
                //g.setColor(Color.cyan);


                if (e_temp.getCustomized() == 0) {
                    g_setColor(g, e_temp.getColor());
                } else if (e_temp.getCustomized() == 1) {
                    g.setColor(e_temp.getCustomizedColor());
                }


                //円周の描画
                d_haba = e_temp.getRadius() * camera.getCameraZoomX();//d_habaは描画時の円の半径。なお、camera.get_camera_bairitsu_x()＝camera.get_camera_bairitsu_y()を前提としている。
                g2.draw(new Ellipse2D.Double(a.getX() - d_haba, a.getY() - d_haba, 2.0 * d_haba, 2.0 * d_haba));
            }
        }


        //円の中心の描画
        if (i_a0_display) {
            for (int i = 1; i <= ori_s.cir_size(); i++) {
                double d_haba;
                Circle e_temp = new Circle();
                e_temp.set(ori_s.getCircle(i));

                a.set(camera.object2TV(e_temp.getCenter()));//この場合のaは描画座標系での円の中心の位置
                //a.set(a.getx()+0.000001,a.gety()+0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

                g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状
                g.setColor(new Color(0, 255, 255, 255));

                //円の中心の描画
                if (fTenkaizuSenhaba < 2.0f) {//中心の黒い正方形を描く
                    g.setColor(Color.black);
                    int i_haba = ir_point;
                    g.fillRect((int) a.getX() - i_haba, (int) a.getY() - i_haba, 2 * i_haba + 1, 2 * i_haba + 1); //正方形を描く//g.fillRect(10, 10, 100, 50);長方形を描く
                }

                if (fTenkaizuSenhaba >= 2.0f) {//  太線指定時の中心を示す黒い小円を描く
                    g2.setStroke(new BasicStroke(1.0f + fTenkaizuSenhaba % 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定
                    if (ir_point != 0) {
                        int i_haba = (int) fTenkaizuSenhaba + ir_point;//int i_haba=2;
                        d_haba = (double) fTenkaizuSenhaba / 2.0 + (double) ir_point;//int i_haba=2;


                        g.setColor(Color.white);
                        g2.fill(new Ellipse2D.Double(a.getX() - d_haba, a.getY() - d_haba, 2.0 * d_haba, 2.0 * d_haba));

                        g.setColor(Color.black);
                        g2.draw(new Ellipse2D.Double(a.getX() - d_haba, a.getY() - d_haba, 2.0 * d_haba, 2.0 * d_haba));
                    }
                }
            }

        }


        //selectの描画
        g2.setStroke(new BasicStroke(fTenkaizuSenhaba * 2.0f + 2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状
        for (int i = 1; i <= ori_s.getTotal(); i++) {
            if (ori_s.get_select(i) == 2) {
                g.setColor(Color.green);

                s_tv.set(camera.object2TV(ori_s.get(i)));

                a.set(s_tv.getAX() + 0.000001, s_tv.getAY() + 0.000001);
                b.set(s_tv.getBX() + 0.000001, s_tv.getBY() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

                g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線

            }
        }


        //展開図の描画 補助活線のみ
        if (i_a0_display) {
            for (int i = 1; i <= ori_s.getTotal(); i++) {
                if (ori_s.getColor(i) == LineColor.CYAN_3) {

                    g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状

                    if (ori_s.getLineCustomized(i) == 0) {
                        g_setColor(g, ori_s.getColor(i));
                    } else if (ori_s.getLineCustomized(i) == 1) {
                        g.setColor(ori_s.getLineCustomizedColor(i));
                    }


                    s_tv.set(camera.object2TV(ori_s.get(i)));
                    a.set(s_tv.getAX() + 0.000001, s_tv.getAY() + 0.000001);
                    b.set(s_tv.getBX() + 0.000001, s_tv.getBY() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

                    g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線

                    if (fTenkaizuSenhaba < 2.0f) {//頂点の黒い正方形を描く
                        g.setColor(Color.black);
                        int i_haba = ir_point;
                        g.fillRect((int) a.getX() - i_haba, (int) a.getY() - i_haba, 2 * i_haba + 1, 2 * i_haba + 1); //正方形を描く//g.fillRect(10, 10, 100, 50);長方形を描く
                        g.fillRect((int) b.getX() - i_haba, (int) b.getY() - i_haba, 2 * i_haba + 1, 2 * i_haba + 1); //正方形を描く
                    }

                    if (fTenkaizuSenhaba >= 2.0f) {//  太線
                        g2.setStroke(new BasicStroke(1.0f + fTenkaizuSenhaba % 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定
                        if (ir_point != 0) {
                            int i_haba = (int) fTenkaizuSenhaba + ir_point;//int i_haba=2;
                            double d_haba = (double) fTenkaizuSenhaba / 2.0 + (double) ir_point;//int i_haba=2;

                            g.setColor(Color.white);
                            g2.fill(new Ellipse2D.Double(a.getX() - d_haba, a.getY() - d_haba, 2.0 * d_haba, 2.0 * d_haba));


                            g.setColor(Color.black);
                            g2.draw(new Ellipse2D.Double(a.getX() - d_haba, a.getY() - d_haba, 2.0 * d_haba, 2.0 * d_haba));

                            g.setColor(Color.white);
                            g2.fill(new Ellipse2D.Double(b.getX() - d_haba, b.getY() - d_haba, 2.0 * d_haba, 2.0 * d_haba));

                            g.setColor(Color.black);
                            g2.draw(new Ellipse2D.Double(b.getX() - d_haba, b.getY() - d_haba, 2.0 * d_haba, 2.0 * d_haba));
                        }
                    }
                }
            }

        }

        //System.out.println(" E 20170201_6");

        //展開図の描画  補助活線以外の折線
        if (i_cp_display) {

            g.setColor(Color.black);

            float[] dash_M1 = {10.0f, 3.0f, 3.0f, 3.0f};//一点鎖線
            float[] dash_M2 = {10.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f};//二点鎖線
            float[] dash_V = {8.0f, 8.0f};//破線

            g.setColor(Color.black);
            for (int i = 1; i <= ori_s.getTotal(); i++) {
                if (ori_s.getColor(i) != LineColor.CYAN_3) {
                    switch (lineStyle) {
                        case COLOR -> {
                            g_setColor(g, ori_s.getColor(i));
                            g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状
                        }
                        case COLOR_AND_SHAPE -> {
                            g_setColor(g, ori_s.getColor(i));
                            if (ori_s.getColor(i) == LineColor.BLACK_0) {
                                g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                            }//基本指定A　　線の太さや線の末端の形状
                            if (ori_s.getColor(i) == LineColor.RED_1) {
                                g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_M1, 0.0f));
                            }//一点鎖線//線の太さや線の末端の形状
                            if (ori_s.getColor(i) == LineColor.BLUE_2) {
                                g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_V, 0.0f));
                            }//破線//線の太さや線の末端の形状
                        }
                        case BLACK_ONE_DOT -> {
                            if (ori_s.getColor(i) == LineColor.BLACK_0) {
                                g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                            }//基本指定A　　線の太さや線の末端の形状
                            if (ori_s.getColor(i) == LineColor.RED_1) {
                                g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_M1, 0.0f));
                            }//一点鎖線//線の太さや線の末端の形状
                            if (ori_s.getColor(i) == LineColor.BLUE_2) {
                                g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_V, 0.0f));
                            }//破線//線の太さや線の末端の形状
                        }
                        case BLACK_TWO_DOT -> {
                            if (ori_s.getColor(i) == LineColor.BLACK_0) {
                                g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                            }//基本指定A　　線の太さや線の末端の形状
                            if (ori_s.getColor(i) == LineColor.RED_1) {
                                g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_M2, 0.0f));
                            }//二点鎖線//線の太さや線の末端の形状
                            if (ori_s.getColor(i) == LineColor.BLUE_2) {
                                g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_V, 0.0f));
                            }//破線//線の太さや線の末端の形状
                        }
                    }


                    s_tv.set(camera.object2TV(ori_s.get(i)));
                    a.set(s_tv.getAX() + 0.000001, s_tv.getAY() + 0.000001);
                    b.set(s_tv.getBX() + 0.000001, s_tv.getBY() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため


                    g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線


                    if (fTenkaizuSenhaba < 2.0f) {//頂点の黒い正方形を描く
                        g.setColor(Color.black);
                        int i_haba = ir_point;
                        g.fillRect((int) a.getX() - i_haba, (int) a.getY() - i_haba, 2 * i_haba + 1, 2 * i_haba + 1); //正方形を描く//g.fillRect(10, 10, 100, 50);長方形を描く
                        g.fillRect((int) b.getX() - i_haba, (int) b.getY() - i_haba, 2 * i_haba + 1, 2 * i_haba + 1); //正方形を描く
                    }


                    if (fTenkaizuSenhaba >= 2.0f) {//  太線
                        g2.setStroke(new BasicStroke(1.0f + fTenkaizuSenhaba % 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定
                        if (ir_point != 0) {
                            int i_haba = (int) fTenkaizuSenhaba + ir_point;//int i_haba=2;
                            double d_haba = (double) fTenkaizuSenhaba / 2.0 + (double) ir_point;//int i_haba=2;


                            g.setColor(Color.white);
                            g2.fill(new Ellipse2D.Double(a.getX() - d_haba, a.getY() - d_haba, 2.0 * d_haba, 2.0 * d_haba));

                            g.setColor(Color.black);
                            g2.draw(new Ellipse2D.Double(a.getX() - d_haba, a.getY() - d_haba, 2.0 * d_haba, 2.0 * d_haba));

                            g.setColor(Color.white);
                            g2.fill(new Ellipse2D.Double(b.getX() - d_haba, b.getY() - d_haba, 2.0 * d_haba, 2.0 * d_haba));

                            g.setColor(Color.black);
                            g2.draw(new Ellipse2D.Double(b.getX() - d_haba, b.getY() - d_haba, 2.0 * d_haba, 2.0 * d_haba));
                        }

                    }
                }
            }
        }

        //i_mouse_modeA==61//長方形内選択（paintの選択に似せた選択機能）の時に使う
        if (orihime_app.i_mouse_modeA == MouseMode.OPERATION_FRAME_CREATE_61) {
            Point p1 = new Point();
            p1.set(camera.TV2object(operationFrame_p1));
            Point p2 = new Point();
            p2.set(camera.TV2object(operationFrame_p2));
            Point p3 = new Point();
            p3.set(camera.TV2object(operationFrame_p3));
            Point p4 = new Point();
            p4.set(camera.TV2object(operationFrame_p4));

            s_step[1].set(p1, p2); //縦線
            s_step[2].set(p2, p3); //横線
            s_step[3].set(p3, p4); //縦線
            s_step[4].set(p4, p1); //横線

            s_step[1].setColor(LineColor.GREEN_6);
            s_step[2].setColor(LineColor.GREEN_6);
            s_step[3].setColor(LineColor.GREEN_6);
            s_step[4].setColor(LineColor.GREEN_6);
        }

        //線分入力時の一時的なs_step線分を描く　

        if ((orihime_app.i_mouse_modeA == MouseMode.OPERATION_FRAME_CREATE_61) && (i_drawing_stage != 4)) {
        } else {
            for (int i = 1; i <= i_drawing_stage; i++) {
                g_setColor(g, s_step[i].getColor());
                g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状

                s_tv.set(camera.object2TV(s_step[i]));
                //a.set(s_tv.geta()); b.set(s_tv.getb());
                a.set(s_tv.getAX() + 0.000001, s_tv.getAY() + 0.000001);
                b.set(s_tv.getBX() + 0.000001, s_tv.getBY() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため


                g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線
                int i_haba_nyuiiryokuji = 3;
                if (i_kou_mitudo_nyuuryoku) {
                    i_haba_nyuiiryokuji = 2;
                }

                if (s_step[i].getActive() == 1) {
                    g.fillOval((int) a.getX() - i_haba_nyuiiryokuji, (int) a.getY() - i_haba_nyuiiryokuji, 2 * i_haba_nyuiiryokuji, 2 * i_haba_nyuiiryokuji); //円
                }
                if (s_step[i].getActive() == 2) {
                    g.fillOval((int) b.getX() - i_haba_nyuiiryokuji, (int) b.getY() - i_haba_nyuiiryokuji, 2 * i_haba_nyuiiryokuji, 2 * i_haba_nyuiiryokuji); //円
                }
                if (s_step[i].getActive() == 3) {
                    g.fillOval((int) a.getX() - i_haba_nyuiiryokuji, (int) a.getY() - i_haba_nyuiiryokuji, 2 * i_haba_nyuiiryokuji, 2 * i_haba_nyuiiryokuji); //円
                    g.fillOval((int) b.getX() - i_haba_nyuiiryokuji, (int) b.getY() - i_haba_nyuiiryokuji, 2 * i_haba_nyuiiryokuji, 2 * i_haba_nyuiiryokuji); //円
                }
            }
        }
        //候補入力時の候補を描く//System.out.println("_");
        g2.setStroke(new BasicStroke(fTenkaizuSenhaba + 0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A


        for (int i = 1; i <= i_candidate_stage; i++) {
            g_setColor(g, s_kouho[i].getColor());

            s_tv.set(camera.object2TV(s_kouho[i]));
            a.set(s_tv.getAX() + 0.000001, s_tv.getAY() + 0.000001);
            b.set(s_tv.getBX() + 0.000001, s_tv.getBY() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

            g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線
            int i_haba = ir_point + 5;

            if (s_kouho[i].getActive() == 1) {
                g.drawLine((int) a.getX() - i_haba, (int) a.getY(), (int) a.getX() + i_haba, (int) a.getY()); //直線
                g.drawLine((int) a.getX(), (int) a.getY() - i_haba, (int) a.getX(), (int) a.getY() + i_haba); //直線
            }
            if (s_kouho[i].getActive() == 2) {
                g.drawLine((int) b.getX() - i_haba, (int) b.getY(), (int) b.getX() + i_haba, (int) b.getY()); //直線
                g.drawLine((int) b.getX(), (int) b.getY() - i_haba, (int) b.getX(), (int) b.getY() + i_haba); //直線
            }
            if (s_kouho[i].getActive() == 3) {
                g.drawLine((int) a.getX() - i_haba, (int) a.getY(), (int) a.getX() + i_haba, (int) a.getY()); //直線
                g.drawLine((int) a.getX(), (int) a.getY() - i_haba, (int) a.getX(), (int) a.getY() + i_haba); //直線

                g.drawLine((int) b.getX() - i_haba, (int) b.getY(), (int) b.getX() + i_haba, (int) b.getY()); //直線
                g.drawLine((int) b.getX(), (int) b.getY() - i_haba, (int) b.getX(), (int) b.getY() + i_haba); //直線
            }
        }

        g.setColor(Color.black);

        //円入力時の一時的な線分を描く　
        for (int i = 1; i <= i_circle_drawing_stage; i++) {
            g_setColor(g, e_step[i].getColor());
            a.set(camera.object2TV(e_step[i].getCenter()));//この場合のs_tvは描画座標系での円の中心の位置
            a.set(a.getX() + 0.000001, a.getY() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

            double d_haba = e_step[i].getRadius() * camera.getCameraZoomX();//d_habaは描画時の円の半径。なお、camera.get_camera_bairitsu_x()＝camera.get_camera_bairitsu_y()を前提としている。

            g2.draw(new Ellipse2D.Double(a.getX() - d_haba, a.getY() - d_haba, 2.0 * d_haba, 2.0 * d_haba));
        }

        g.setColor(Color.black);

        if (i_bun_display) {
            g.drawString(text_cp_setumei, 120, 120);
        }
    }


    //動作モデル00a--------------------------------------------------------------------------------------------------------
    //マウスクリック（マウスの近くの既成点を選択）、マウスドラッグ（選択した点とマウス間の線が表示される）、マウスリリース（マウスの近くの既成点を選択）してから目的の処理をする雛形セット

    // -------------------------------------------------------------------------------------------------------------------------------
    public void g_setColor(Graphics g, LineColor i) {
/*

		Color.black       黒を表します
		Color.blue        青を表します
		Color.cyan        シアンを表します
		Color.darkGray    ダークグレイを表します
		Color.gray        グレイを表します
		Color.green       緑を表します
		Color.lightGray   ライトグレイを表します
		Color.magenta     マゼンタを表します
		Color.orange      オレンジを表します
		Color.pink        ピンクを表します
		Color.red         赤を表します
		Color.white       白を表します
		Color.yellow      黄を表します
*/

        //icol=0 black
        //icol=1 red
        //icol=2 blue
        //icol=3 cyan
        //icol=4 orange
        //icol=5 mazenta
        //icol=6 green
        //icol=7 yellow
        //icol=8 new Color(210,0,255) //紫

        switch (i) {
            case BLACK_0 -> g.setColor(Color.black);
            case RED_1 -> g.setColor(Color.red);
            case BLUE_2 -> g.setColor(Color.blue);
            case CYAN_3 -> g.setColor(new Color(100, 200, 200));
            case ORANGE_4 -> g.setColor(Color.orange);
            case MAGENTA_5 -> g.setColor(Color.magenta);
            case GREEN_6 -> g.setColor(Color.green);
            case YELLOW_7 -> g.setColor(Color.yellow);
            case PURPLE_8 -> g.setColor(new Color(210, 0, 255));
        }
    }

    public void set_i_en_egaki_dankai(int i) {
        i_circle_drawing_stage = i;
    }

    public void set_id_kakudo_kei(int i) {
        id_kakudo_kei = i;
    }

// ------------------------------------
    public void set_i_kou_mitudo_nyuuryoku(boolean i) {
        i_kou_mitudo_nyuuryoku = i;
    }


    //動作モデル00b--------------------------------------------------------------------------------------------------------
    //マウスクリック（近くの既成点かマウス位置を選択）、マウスドラッグ（選択した点とマウス間の線が表示される）、マウスリリース（近くの既成点かマウス位置を選択）してから目的の処理をする雛形セット

    // *************************************************************************************
//--------------------------
    public void add_en(Circle e0) {
        add_en(e0.getX(), e0.getY(), e0.getRadius(), e0.getColor());
    }

    //--------------------------
//--------------------------
    public void add_en(Point t0, double dr, LineColor ic) {
        add_en(t0.getX(), t0.getY(), dr, ic);
    }

    //--------------------------
    public void add_en(double dx, double dy, double dr, LineColor ic) {
        ori_s.addCircle(dx, dy, dr, ic);

        int imin = 1;
        int imax = ori_s.cir_size() - 1;
        int jmin = ori_s.cir_size();
        int jmax = ori_s.cir_size();

        ori_s.circle_circle_intersection(imin, imax, jmin, jmax);
        ori_s.lineSegment_circle_intersection(1, ori_s.getTotal(), jmin, jmax);

    }

    //--------------------------
    public int addsenbun_hojyo(LineSegment s0) {
        hoj_s.addLine(s0);

        return 1;
    }


//--------------------------------------------
//28 28 28 28 28 28 28 28  i_mouse_modeA==28線分内分入力
    //動作概要
    //i_mouse_modeA==1と線分内分以外は同じ

    //--------------------------------------------
    public int addLineSegment(LineSegment s0) {//0=変更なし、1=色の変化のみ、2=線分追加

        ori_s.addLine(s0);//ori_sのsenbunの最後にs0の情報をを加えるだけ
        int sousuu_old = ori_s.getTotal();
        ori_s.lineSegment_circle_intersection(ori_s.getTotal(), ori_s.getTotal(), 1, ori_s.cir_size());

        ori_s.intersect_divide(1, sousuu_old - 1, sousuu_old, sousuu_old);

        return 1;
    }

    //------------------------------------------------------
    public Point get_moyori_ten_orisen_en(Point t0) {//
        //用紙1/1分割時 		折線の端点のみが基準点。格子点が基準点になることはない。
        //用紙1/2から1/512分割時	折線の端点と用紙枠内（-200.0,-200.0 _ 200.0,200.0)）の格子点とが基準点
        Point t1 = new Point(); //折線の端点

        Point t3 = new Point(); //円の中心

        t1.set(ori_s.closestPoint(t0));//ori_s.mottomo_tikai_Ten_sagasiは近い点がないと p_return.set(100000.0,100000.0)と返してくる

        t3.set(ori_s.closestCenter(t0));//ori_s.mottomo_tikai_Ten_sagasiは近い点がないと p_return.set(100000.0,100000.0)と返してくる
        if (t0.distanceSquared(t1) > t0.distanceSquared(t3)) {
            t1.set(t3);
        }


        //if(kus.jyoutai()==0){return t1;}


        //if( t0.kyori2jyou(t1)>  t0.kyori2jyou(kus.moyori_kousi_ten(t0)) ){return kus.moyori_kousi_ten(t0);}
        return t1;
    }

public Point getClosestPoint(Point t0) {
    // When dividing paper 1/1 Only the end point of the folding line is the reference point. The grid point never becomes the reference point.
    // When dividing paper from 1/2 to 1/512 The end point of the polygonal line and the grid point in the paper frame (-200.0, -200.0 _ 200.0, 200.0) are the reference points.
    Point t1 = new Point(); //End point of the polygonal line
    Point t3 = new Point(); //Center of circle

    t1.set(ori_s.closestPoint(t0)); // ori_s.closestPoint returns (100000.0,100000.0) if there is no close point

    t3.set(ori_s.closestCenter(t0)); // ori_s.closestCenter returns (100000.0,100000.0) if there is no close point

    if (t0.distanceSquared(t1) > t0.distanceSquared(t3)) {
        t1.set(t3);
    }

    if (grid.getBaseState() == Grid.State.HIDDEN) {
        return t1;
    }

    if (t0.distanceSquared(t1) > t0.distanceSquared(grid.closestGridPoint(t0))) {
        return grid.closestGridPoint(t0);
    }

    return t1;
}

    //------------------------------
    public LineSegment get_moyori_senbun(Point t0) {
        return ori_s.closestLineSegment(t0);
    }


//1 1 1 1 1 1 01 01 01 01 01 11111111111 i_mouse_modeA==1線分入力 111111111111111111111111111111111
    //動作概要　
    //マウスボタン押されたとき　
    //用紙1/1分割時 		折線の端点のみが基準点。格子点が基準点になることはない。
    //用紙1/2から1/512分割時	折線の端点と用紙枠内（-200.0,-200.0 _ 200.0,200.0)）の格子点とが基準点
    //入力点Pが基準点から格子幅kus.d_haba()の1/4より遠いときは折線集合への入力なし
    //線分が長さがなく1点状のときは折線集合への入力なし

    //------------------------------------------------------
    public LineSegment get_moyori_step_senbun(Point t0, int imin, int imax) {
        int minrid = -100;
        double minr = 100000;//Senbun s1 =new Senbun(100000.0,100000.0,100000.0,100000.1);
        for (int i = imin; i <= imax; i++) {
            double sk = OritaCalc.distance_lineSegment(t0, s_step[i]);
            if (minr > sk) {
                minr = sk;
                minrid = i;
            }//柄の部分に近いかどうか

        }

        return s_step[minrid];
    }

    //------------------------------
    public Circle get_moyori_ensyuu(Point t0) {
        return ori_s.closestCircleMidpoint(t0);
    }

    //------------------------------------------------------
    public Circle get_moyori_step_ensyuu(Point t0, int imin, int imax) {
        int minrid = -100;
        double minr = 100000;
        for (int i = imin; i <= imax; i++) {
            double ek = OritaCalc.distance_circumference(t0, e_step[i]);
            if (minr > ek) {
                minr = ek;
                minrid = i;
            }//円周に近いかどうか
        }
        return e_step[minrid];
    }

    public void set_s_step_iactive(int ia) {
        for (int i = 0; i < 1024; i++) {
            s_step[i].setActive(ia);
        }
    }

    //動作モデル001--------------------------------------------------------------------------------------------------------
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_m_001(Point p0, LineColor i_c) {//マウスで選択できる候補点を表示する。近くに既成の点があるときはその点が候補点となる。近くに既成の点が無いときは候補点無しなので候補点の表示も無し。
        if (i_kou_mitudo_nyuuryoku) {
            s_kouho[1].setActive(3);
            i_candidate_stage = 0;
            p.set(camera.TV2object(p0));
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) < d_decision_width) {
                i_candidate_stage = 1;
                s_kouho[1].set(closest_point, closest_point);
                s_kouho[1].setColor(i_c);
            }
        }
    }

    //動作モデル002--------------------------------------------------------------------------------------------------------
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_m_002(Point p0, LineColor i_c) {//マウスで選択できる候補点を表示する。近くに既成の点があるときはその点、無いときはマウスの位置自身が候補点となる。
        if (i_kou_mitudo_nyuuryoku) {
            s_kouho[1].setActive(3);
            p.set(camera.TV2object(p0));
            i_candidate_stage = 1;
            closest_point.set(getClosestPoint(p));

            if (p.distance(closest_point) < d_decision_width) {
                s_kouho[1].set(closest_point, closest_point);
            } else {
                s_kouho[1].set(p, p);
            }

            s_kouho[1].setColor(i_c);
        }
    }

    //動作モデル003--------------------------------------------------------------------------------------------------------
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_m_003(Point p0, LineColor i_c) {//マウスで選択できる候補点を表示する。常にマウスの位置自身が候補点となる。
        if (i_kou_mitudo_nyuuryoku) {
            //s_kouho[1].setiactive(3);
            p.set(camera.TV2object(p0));
            i_candidate_stage = 1;
            s_kouho[1].set(p, p);

            s_kouho[1].setColor(i_c);
        }
    }

    //マウスを動かしたとき----------------------------------------------
    public void mMoved_m_00a(Point p0, LineColor i_c) {
        mMoved_m_001(p0, i_c);
    }//近い既存点のみ表示

    //マウスクリック----------------------------------------------------
    public void mPressed_m_00a(Point p0, LineColor i_c) {
        i_drawing_stage = 1;
        s_step[1].setActive(2);
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) > d_decision_width) {
            i_drawing_stage = 0;
        }
        s_step[1].set(p, closest_point);
        s_step[1].setColor(i_c);
    }


//62 62 62 62 62 i_mouse_modeA==62 ボロノイ　 Voronoi 111111111111111111111111111111111

    //マウスドラッグ---------------------------------------------------
    public void mDragged_m_00a(Point p0, LineColor i_c) {  //近い既存点のみ表示

        p.set(camera.TV2object(p0));
        s_step[1].setA(p);

        if (i_kou_mitudo_nyuuryoku) {
            i_candidate_stage = 0;
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) < d_decision_width) {
                i_candidate_stage = 1;
                s_kouho[1].set(closest_point, closest_point);
                s_kouho[1].setColor(i_c);
                s_step[1].setA(s_kouho[1].getA());
            }
        }
    }

// ------------------------------------------

    //マウスリリース--------------------------------------------------
    public void mReleased_m_00a(Point p0) {
        if (i_drawing_stage == 1) {
            i_drawing_stage = 0;

            p.set(camera.TV2object(p0));
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) <= d_decision_width) {
                s_step[1].setA(closest_point);
                if (s_step[1].getLength() > 0.00000001) {
                    //やりたい動作はここに書く
                    //addsenbun(s_step[1]);
                    //kiroku();
                }
            }
        }
    }

    //マウスを動かしたとき----------------------------------------------
    public void mMoved_m_00b(Point p0, LineColor i_c) {
        mMoved_m_002(p0, i_c);
    }//近くの既成点かマウス位置表示

    //マウスクリック----------------------------------------------------
    public void mPressed_m_00b(Point p0, LineColor i_c) {
        i_drawing_stage = 1;
        s_step[1].setActive(2);
        p.set(camera.TV2object(p0));
        s_step[1].set(p, p);

        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) < d_decision_width) {
            s_step[1].set(p, closest_point);
        }

        s_step[1].setColor(i_c);
    }

    //マウスドラッグ---------------------------------------------------
    public void mDragged_m_00b(Point p0, LineColor i_c) {  //近くの既成点かマウス位置表示

        p.set(camera.TV2object(p0));
        s_step[1].setA(p);

        if (i_kou_mitudo_nyuuryoku) {
            closest_point.set(getClosestPoint(p));
            i_candidate_stage = 1;
            if (p.distance(closest_point) < d_decision_width) {
                s_kouho[1].set(closest_point, closest_point);
            } else {
                s_kouho[1].set(p, p);
            }
            s_kouho[1].setColor(i_c);
            s_step[1].setA(s_kouho[1].getA());
        }
    }

    //マウスリリース--------------------------------------------------
    public void mReleased_m_00b(Point p0) {
        i_drawing_stage = 0;
        p.set(camera.TV2object(p0));
        s_step[1].setA(p);
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) <= d_decision_width) {
            s_step[1].setA(closest_point);
        }
        if (s_step[1].getLength() > 0.00000001) {
            //やりたい動作はここに書く

        }
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_28(Point p0) {
        mMoved_m_00a(p0, icol);//マウスで選択できる候補点を表示する。近くに既成の点があるときはその点、無いときはマウスの位置自身が候補点となる。

    }

    //マウス操作(i_mouse_modeA==28線分内分入力 でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_28(Point p0) {
        i_drawing_stage = 1;
        s_step[1].setActive(2);
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) < d_decision_width) {
            s_step[1].set(p, closest_point);
            s_step[1].setColor(icol);
            return;
        }
        s_step[1].set(p, p);
        s_step[1].setColor(icol);
    }

    //マウス操作(i_mouse_modeA==28線分入力 でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_28(Point p0) {
        p.set(camera.TV2object(p0));
        s_step[1].setA(p);

        if (i_kou_mitudo_nyuuryoku) {
            closest_point.set(getClosestPoint(p));
            i_candidate_stage = 1;
            if (p.distance(closest_point) < d_decision_width) {
                s_kouho[1].set(closest_point, closest_point);
            } else {
                s_kouho[1].set(p, p);
            }
            s_kouho[1].setColor(icol);
            s_step[1].setA(s_kouho[1].getA());
        }
        return;
    }


//------------------------------

    //マウス操作(i_mouse_modeA==28線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_28(Point p0) {
        i_drawing_stage = 0;
        p.set(camera.TV2object(p0));

        s_step[1].setA(p);
        closest_point.set(getClosestPoint(p));

        if (p.distance(closest_point) <= d_decision_width) {
            s_step[1].setA(closest_point);
        }
        if (s_step[1].getLength() > 0.00000001) {
            if ((d_naibun_s == 0.0) && (d_naibun_t == 0.0)) {
            }
            if ((d_naibun_s == 0.0) && (d_naibun_t != 0.0)) {
                addLineSegment(s_step[1]);
            }
            if ((d_naibun_s != 0.0) && (d_naibun_t == 0.0)) {
                addLineSegment(s_step[1]);
            }
            if ((d_naibun_s != 0.0) && (d_naibun_t != 0.0)) {
                LineSegment s_ad = new LineSegment();
                s_ad.setColor(icol);
                double nx = (d_naibun_t * s_step[1].getBX() + d_naibun_s * s_step[1].getAX()) / (d_naibun_s + d_naibun_t);
                double ny = (d_naibun_t * s_step[1].getBY() + d_naibun_s * s_step[1].getAY()) / (d_naibun_s + d_naibun_t);
                s_ad.set(s_step[1].getAX(), s_step[1].getAY(), nx, ny);
                addLineSegment(s_ad);
                s_ad.set(s_step[1].getBX(), s_step[1].getBY(), nx, ny);
                addLineSegment(s_ad);
            }
            record();
        }
    }


    // -----------------------------------------------

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_01(Point p0) {
        if (i_kou_mitudo_nyuuryoku) {
            s_kouho[1].setActive(3);

            p.set(camera.TV2object(p0));
            i_candidate_stage = 1;
            closest_point.set(getClosestPoint(p));

            if (p.distance(closest_point) < d_decision_width) {
                s_kouho[1].set(closest_point, closest_point);
            } else {
                s_kouho[1].set(p, p);
            }

            if (i_foldLine_additional == FoldLineAdditionalInputMode.POLY_LINE_0) {
                s_kouho[1].setColor(icol);
            }
            if (i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LINE_1) {
                s_kouho[1].setColor(h_icol);
            }

        }
    }

    // --------------------------------------------

    //マウス操作(i_mouse_modeA==1線分入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_01(Point p0) {
        i_drawing_stage = 1;
        s_step[1].setActive(2);
        p.set(camera.TV2object(p0));

        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) < d_decision_width) {
            s_step[1].set(p, closest_point);
            if (i_foldLine_additional == FoldLineAdditionalInputMode.POLY_LINE_0) {
                s_step[1].setColor(icol);
            }
            if (i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LINE_1) {
                s_step[1].setColor(h_icol);
            }
            return;
        }

        s_step[1].set(p, p);
        if (i_foldLine_additional == FoldLineAdditionalInputMode.POLY_LINE_0) {
            s_step[1].setColor(icol);
        }
        if (i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LINE_1) {
            s_step[1].setColor(h_icol);
        }
    }


    //-----------------------------------------------62ここまで　//20181121　iactiveをtppに置き換える


//-------------------------------------------------------------------------------------------------------

//--------------------------------------

    //マウス操作(i_mouse_modeA==1線分入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_01(Point p0) {
        p.set(camera.TV2object(p0));

        if (!i_kou_mitudo_nyuuryoku) {
            s_step[1].setA(p);
        }

        if (i_kou_mitudo_nyuuryoku) {
            closest_point.set(getClosestPoint(p));
            i_candidate_stage = 1;
            if (p.distance(closest_point) < d_decision_width) {
                s_kouho[1].set(closest_point, closest_point);
            } else {
                s_kouho[1].set(p, p);
            }
            if (i_foldLine_additional == FoldLineAdditionalInputMode.POLY_LINE_0) {
                s_kouho[1].setColor(icol);
            }
            if (i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LINE_1) {
                s_kouho[1].setColor(h_icol);
            }
            s_step[1].setA(s_kouho[1].getA());
        }
    }

    public Point get_moyori_ten_sisuu(Point p0) {
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        return new Point(grid.getIndex(closest_point));
    }

    //マウス操作(i_mouse_modeA==1線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_01(Point p0) {
        i_drawing_stage = 0;
        p.set(camera.TV2object(p0));
        s_step[1].setA(p);
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) <= d_decision_width) {
            s_step[1].setA(closest_point);
        }
        if (s_step[1].getLength() > 0.00000001) {
            if (i_foldLine_additional == FoldLineAdditionalInputMode.POLY_LINE_0) {
                addLineSegment(s_step[1]);
                record();
            }
            if (i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LINE_1) {
                addsenbun_hojyo(s_step[1]);
                h_kiroku();
            }
        }
    }

    //11 11 11 11 11 11 11 11 11 11 11
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_11(Point p0) {
        mMoved_m_00a(p0, icol);
    }//近い既存点のみ表示

//------

    //マウス操作(i_mouse_modeA==11線分入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_11(Point p0) {
        mPressed_m_00a(p0, icol);
    }

    //マウス操作(i_mouse_modeA==11線分入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_11(Point p0) {
        mDragged_m_00a(p0, icol);
    }//近い既存点のみ表示

    //マウス操作(i_mouse_modeA==11線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_11(Point p0) {
        if (i_drawing_stage == 1) {
            i_drawing_stage = 0;

            p.set(camera.TV2object(p0));
            closest_point.set(getClosestPoint(p));
            s_step[1].setA(closest_point);
            if (p.distance(closest_point) <= d_decision_width) {
                if (s_step[1].getLength() > 0.00000001) {
                    addLineSegment(s_step[1]);
                    record();
                }
            }
        }
    }

    //Function to operate the mouse (i_mouse_modeA == 62 Voronoi when the mouse is moved)
    public void mMoved_A_62(Point p0) {
        if (i_kou_mitudo_nyuuryoku) {
            s_kouho[1].setActive(3);

            p.set(camera.TV2object(p0));
            i_candidate_stage = 1;
            closest_point.set(getClosestPoint(p));

            if (p.distance(closest_point) < d_decision_width) {
                s_kouho[1].set(closest_point, closest_point);
            } else {
                s_kouho[1].set(p, p);
            }

            if (i_foldLine_additional == FoldLineAdditionalInputMode.POLY_LINE_0) {
                s_kouho[1].setColor(icol);
            }
            if (i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LINE_1) {
                s_kouho[1].setColor(h_icol);
            }

        }
    }


//-------------------------------------------------------------------------------------------------------------------------------

    // ------------------------------------------------------------------------------------------------------------
    int s_step_no_1_top_continue_no_point_no_number() {//s_step [i] returns the number of Point (length 0) from the beginning. Returns 0 if there are no dots
        int r_i = 0;
        int i_add = 1;
        for (int i = 1; i <= i_drawing_stage; i++) {
            if (s_step[i].getLength() > 0.00000001) {
                i_add = 0;
            }
            r_i = r_i + i_add;
        }
        return r_i;
    }

    //マウス操作(i_mouse_modeA==62ボロノイ　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_62(Point p0) {
        p.set(camera.TV2object(p0));

        //Arranged i_drawing_stage to be only the conventional Voronoi mother point (yet, we have not decided whether to add the point p as s_step to the Voronoi mother point)
        i_drawing_stage = s_step_no_1_top_continue_no_point_no_number();//Tenの数

        //Find the point-like line segment s_temp consisting of the closest points of p newly added at both ends (if there is no nearest point, both ends of s_temp are p)
        LineSegment s_temp = new LineSegment();
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) < d_decision_width) {
            s_temp.set(closest_point, closest_point);
            s_temp.setColor(LineColor.MAGENTA_5);
        } else {
            s_temp.set(p, p);
            s_temp.setColor(LineColor.MAGENTA_5);
        }


        //Confirm that the newly added p does not overlap with the previously added Ten
        i_mouse_modeA_62_point_overlapping = 0;

        for (int i = 1; i <= i_drawing_stage; i++) {
            if (OritaCalc.distance(s_step[i].getA(), s_temp.getA()) <= d_decision_width) {
                i_mouse_modeA_62_point_overlapping = i;
            }
        }

        //Confirm that the newly added p does not overlap with the previously added Point.

        if (i_mouse_modeA_62_point_overlapping == 0) {

            //(ここでやっと、点pをs_stepとしてボロノイ母点に加えると決まった)
            i_drawing_stage = i_drawing_stage + 1;
            s_step[i_drawing_stage].set(s_temp);
            s_step[i_drawing_stage].setActive(3);//Circles are drawn at both ends of the line with iactive = 3. For the line of iactive = 1, a circle is drawn only at the a end. The line of iactive = 2 has a circle drawn only at the b end

            //今までのボロノイ図を元に、１個の新しいボロノイ母点を加えたボロノイ図を作る--------------------------------------

            //voronoi_01();//低速、エラーはほとんどないはず
            voronoi_02();//Fast, maybe there are still errors
        } else {//Removed Voronoi mother points with order i_mouse_modeA_62_point_overlapping
            //順番がi_mouse_modeA_62_ten_kasanariのボロノイ母点と順番が最後(=i_egaki_dankai)のボロノイ母点を入れ替える
            //s_step[i]の入れ替え
            LineSegment S_replace = new LineSegment();
            S_replace.set(s_step[i_mouse_modeA_62_point_overlapping]);
            s_step[i_mouse_modeA_62_point_overlapping].set(s_step[i_drawing_stage]);
            s_step[i_drawing_stage].set(S_replace);


            for (int j = 1; j <= ori_v.getTotal(); j++) {
                //Swapping the vonoroiA of the line segment in ori_v
                if (ori_v.getVonoroiA(j) == i_mouse_modeA_62_point_overlapping) {
                    ori_v.setVonoroiA(j, i_drawing_stage);
                } else if (ori_v.getVonoroiA(j) == i_drawing_stage) {
                    ori_v.setVonoroiA(j, i_mouse_modeA_62_point_overlapping);
                }

                //Replacing the vonoroiB of the line segment in ori_v
                if (ori_v.getVonoroiB(j) == i_mouse_modeA_62_point_overlapping) {
                    ori_v.setVonoroiB(j, i_drawing_stage);
                } else if (ori_v.getVonoroiB(j) == i_drawing_stage) {
                    ori_v.setVonoroiB(j, i_mouse_modeA_62_point_overlapping);
                }
            }


            //Deleted the Voronoi mother point of the last order (= i_drawing_stage)

            i_drawing_stage = i_drawing_stage - 1;

            FoldLineSet ori_v_temp = new FoldLineSet();    //修正用のボロノイ図の線を格納する

            //Deselect all ori_v line segments first
            ori_v.unselect_all();

            //i_egaki_dankai+1のボロノイ母点からのボロノイ線分を選択状態にする
            LineSegment s_tem = new LineSegment();
            LineSegment s_tem2 = new LineSegment();
            for (int j = 1; j <= ori_v.getTotal(); j++) {
                s_tem.set(ori_v.get(j));//s_temとしてボロノイ母点からのボロノイ線分か判定
                if (s_tem.getVonoroiA() == i_drawing_stage + 1) {//The two Voronoi vertices of the Voronoi line segment are recorded in vonoroiA and vonoroiB.
                    ori_v.select(j);
                    for (int h = 1; h <= ori_v.getTotal(); h++) {
                        s_tem2.set(ori_v.get(h));
                        if (s_tem.getVonoroiB() == s_tem2.getVonoroiB()) {
                            ori_v.select(h);
                        }
                        if (s_tem.getVonoroiB() == s_tem2.getVonoroiA()) {
                            ori_v.select(h);
                        }
                    }


                    //削除されるi_egaki_dankai+1番目のボロノイ母点と組になる、もう一つのボロノイ母点を取り囲むボロノイ線分のアレイリストを得る。
                    Senb_boro_1p_motome(s_tem.getVonoroiB());

                    for (LineSegment lineSegment : lineSegment_vonoroi_onePoint) {
                        LineSegment add_S = new LineSegment();
                        add_S.set(lineSegment);
                        LineSegment add_S2 = new LineSegment();


                        //Pre-check whether to add add_S to ori_v_temp
                        int i_tuika = 1;//1なら追加する。0なら追加しない。
                        for (int h = 1; h <= ori_v_temp.getTotal(); h++) {
                            add_S2.set(ori_v_temp.get(h));
                            if ((add_S.getVonoroiB() == add_S2.getVonoroiB()) && (add_S.getVonoroiA() == add_S2.getVonoroiA())) {
                                i_tuika = 0;
                            }
                            if ((add_S.getVonoroiB() == add_S2.getVonoroiA()) && (add_S.getVonoroiA() == add_S2.getVonoroiB())) {
                                i_tuika = 0;
                            }
                        }
                        //ori_v_tempにadd_Sを追加するかどうかの事前チェックはここまで

                        if (i_tuika == 1) {
                            ori_v_temp.addLine(lineSegment);
                        }
                    }
                } else if (s_tem.getVonoroiB() == i_drawing_stage + 1) {//The two Voronoi vertices of the Voronoi line segment are recorded in iactive and color.
                    ori_v.select(j);
                    for (int h = 1; h <= ori_v.getTotal(); h++) {
                        s_tem2.set(ori_v.get(h));
                        if (s_tem.getVonoroiA() == s_tem2.getVonoroiB()) {
                            ori_v.select(h);
                        }
                        if (s_tem.getVonoroiA() == s_tem2.getVonoroiA()) {
                            ori_v.select(h);
                        }
                    }

                    //削除されるi_egaki_dankai+1番目のボロノイ母点と組になる、もう一つのボロノイ母点を取り囲むボロノイ線分のアレイリストを得る。
                    Senb_boro_1p_motome(s_tem.getVonoroiA());

                    for (LineSegment lineSegment : lineSegment_vonoroi_onePoint) {
                        LineSegment add_S = new LineSegment();
                        add_S.set(lineSegment);
                        LineSegment add_S2 = new LineSegment();

                        //ori_v_tempにadd_Sを追加するかどうかの事前チェック
                        int i_tuika = 1;//1なら追加する。0なら追加しない。
                        for (int h = 1; h <= ori_v_temp.getTotal(); h++) {
                            add_S2.set(ori_v_temp.get(h));
                            if ((add_S.getVonoroiB() == add_S2.getVonoroiB()) && (add_S.getVonoroiA() == add_S2.getVonoroiA())) {
                                i_tuika = 0;
                            }
                            if ((add_S.getVonoroiB() == add_S2.getVonoroiA()) && (add_S.getVonoroiA() == add_S2.getVonoroiB())) {
                                i_tuika = 0;
                            }
                        }
                        //This is the end of the pre-check whether to add add_S to ori_v_temp

                        if (i_tuika == 1) {
                            ori_v_temp.addLine(lineSegment);
                        }
                    }
                }
            }
            //選択状態のものを削除
            ori_v.del_selected_lineSegment_fast();
            ori_v.del_V_all(); //You may not need this line

            //ori_v_tempのボロノイ線分をボロノイ母点に加える
            //ori_v_temp.hyouji("ori_v_temp---------------------");
            for (int j = 1; j <= ori_v_temp.getTotal(); j++) {
                LineSegment s_t = new LineSegment();
                s_t.set(ori_v_temp.get(j));
                ori_v.addLine(s_t);
            }

            ori_v.del_V_all();

        }


        //ボロノイ図も表示するようにs_stepの後にボロノイ図の線を入れる

        int imax = ori_v.getTotal();
        if (imax > 1020) {
            imax = 1020;
        }

        //System.out.println("ボロノイ図も表示するようにs_stepの後にボロノイ図の線を入れる前");
        //System.out.println("i_egaki_dankai="+i_egaki_dankai+" :  ori_v.getsousuu()= "+ori_v.getsousuu());

        for (int i = 1; i <= imax; i++) {
            i_drawing_stage = i_drawing_stage + 1;
            s_step[i_drawing_stage].set(ori_v.get(i));
            //s_step[i_egaki_dankai].setiactive(3);
            s_step[i_drawing_stage].setActive(0);
            s_step[i_drawing_stage].setColor(LineColor.MAGENTA_5);
        }


    }

    //--------------------------------------------
    public int addLineSegmentVonoroi(LineSegment s0) {//0 = No change, 1 = Color change only, 2 = Line segment added

        ori_v.addLine(s0);//ori_vのsenbunの最後にs0の情報をを加えるだけ
        int sousuu_old = ori_v.getTotal();
        ori_v.lineSegment_circle_intersection(ori_v.getTotal(), ori_v.getTotal(), 1, ori_v.cir_size());

        ori_v.intersect_divide(1, sousuu_old - 1, sousuu_old, sousuu_old);

        return 1;
    }

    // -----------------------------------------------------------------------------
    //マウス操作(i_mouse_modeA==62ボロノイ　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_62(Point p0) {
    }

    // -----------------------------------------------------------------------------
    //マウス操作(i_mouse_modeA==62ボロノイ　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_62(Point p0) {
    }

//--------------------------------------


//71 71 71 71 71 71 71 71 71 71 71 71 71 71    i_mouse_modeA==71　;線分延長モード

    // ------------------------------------------
    public void voronoi_01() {//i=1からi_egaki_dankaiまでのs_step[i]と、i_egaki_dankai-1までのボロノイ図からi_egaki_dankaiのボロノイ図を作成

        //i_egaki_dankai番目のボロノイ頂点とそれ以前のボロノイ頂点間の2等分線をori_vに追加

        for (int i_e_d = 1; i_e_d <= i_drawing_stage - 1; i_e_d++) {
            addLineSegmentVonoroi(OritaCalc.bisection(s_step[i_e_d].getA(), s_step[i_drawing_stage].getA(), 1000.0)); //kiroku();
        }

        //ボロノイに適合するか判定
        //ori_vの線分を最初に全て非選択にする
        ori_v.unselect_all();


        //ボロノイに適合しないものを選択状態にする
        LineSegment s_tem = new LineSegment();
        for (int j = 1; j <= ori_v.getTotal(); j++) {

            //System.out.println("ボロノイ j= "+j);
            s_tem.set(ori_v.get(j));//s_temとしてボロノイに適合するか判定

            //s_tenのa端とボロノイの各頂点との距離の最短値v_min_aを求める
            double v_min_a = 1000000.0;
            for (int i = 1; i <= i_drawing_stage; i++) {
                if (OritaCalc.distance(s_step[i].getA(), s_tem.getA()) < v_min_a) {
                    v_min_a = OritaCalc.distance(s_step[i].getA(), s_tem.getA());
                }
            }
            //System.out.println("v_min_a= "+v_min_a);
            //s_tenのb端とボロノイの各頂点との距離の最短値v_min_bを求める
            double v_min_b = 1000000.0;
            for (int i = 1; i <= i_drawing_stage; i++) {
                if (OritaCalc.distance(s_step[i].getA(), s_tem.getB()) < v_min_b) {
                    v_min_b = OritaCalc.distance(s_step[i].getA(), s_tem.getB());
                }
            }
            //System.out.println("v_min_b= "+v_min_b);


            int a_tomo_b_tomo_closest_voronoi_vertex_no_amount = 0;//aともbとも最も近いボロノイ頂点の数　これが２なら対象線分はボロノイ図として残す
            for (int i = 1; i <= i_drawing_stage; i++) {
                if (Math.abs(OritaCalc.distance(s_step[i].getA(), s_tem.getA()) - v_min_a) < 0.00001) {
                    if (Math.abs(OritaCalc.distance(s_step[i].getA(), s_tem.getB()) - v_min_b) < 0.00001) {
                        a_tomo_b_tomo_closest_voronoi_vertex_no_amount = a_tomo_b_tomo_closest_voronoi_vertex_no_amount + 1;


                    }
                }
            }
            //System.out.println("a_tomo_b_tomo_closest_voronoi_vertex_no_amount= "+a_tomo_b_tomo_closest_voronoi_vertex_no_amount);

            if (a_tomo_b_tomo_closest_voronoi_vertex_no_amount != 2) {

                ori_v.select(j);
            }


        }


        //選択状態のものを削除
        ori_v.del_selected_lineSegment_fast();

        ori_v.del_V_all(); //この行はいらないかも


    }

    public void voronoi_02_01(int tyuusinn_ten_bangou, LineSegment add_lineSegment) {
        //i_egaki_dankai番目のボロノイ頂点は　　s_step[i_egaki_dankai].geta()　　　

        //Organize the line segments to be added
        StraightLine add_straightLine = new StraightLine(add_lineSegment);

        int i_saisyo = lineSegment_vonoroi_onePoint.size() - 1;
        for (int i = i_saisyo; i >= 0; i--) {
            //Organize existing line segments
            LineSegment existing_lineSegment = new LineSegment();
            existing_lineSegment.set(lineSegment_vonoroi_onePoint.get(i));
            StraightLine existing_straightLine = new StraightLine(existing_lineSegment);

            //Fight the line segment to be added with the existing line segment

            OritaCalc.ParallelJudgement parallel = OritaCalc.parallel_judgement(add_straightLine, existing_straightLine, 0.0001);//0 = not parallel, 1 = parallel and 2 straight lines do not match, 2 = parallel and 2 straight lines match

            if (parallel == OritaCalc.ParallelJudgement.NOT_PARALLEL) {//When the line segment to be added and the existing line segment are non-parallel
                Point intersection = new Point();
                intersection.set(OritaCalc.findIntersection(add_straightLine, existing_straightLine));

                if ((add_straightLine.sameSide(s_step[tyuusinn_ten_bangou].getA(), existing_lineSegment.getA()) <= 0) &&
                        (add_straightLine.sameSide(s_step[tyuusinn_ten_bangou].getA(), existing_lineSegment.getB()) <= 0)) {
                    lineSegment_vonoroi_onePoint.remove(i);
                } else if ((add_straightLine.sameSide(s_step[tyuusinn_ten_bangou].getA(), existing_lineSegment.getA()) == 1) &&
                        (add_straightLine.sameSide(s_step[tyuusinn_ten_bangou].getA(), existing_lineSegment.getB()) == -1)) {
                    existing_lineSegment.set(existing_lineSegment.getA(), intersection);
                    if (existing_lineSegment.getLength() < 0.0000001) {
                        lineSegment_vonoroi_onePoint.remove(i);
                    } else {
                        lineSegment_vonoroi_onePoint.set(i, existing_lineSegment);
                    }
                } else if ((add_straightLine.sameSide(s_step[tyuusinn_ten_bangou].getA(), existing_lineSegment.getA()) == -1) &&
                        (add_straightLine.sameSide(s_step[tyuusinn_ten_bangou].getA(), existing_lineSegment.getB()) == 1)) {
                    existing_lineSegment.set(intersection, existing_lineSegment.getB());
                    if (existing_lineSegment.getLength() < 0.0000001) {
                        lineSegment_vonoroi_onePoint.remove(i);
                    } else {
                        lineSegment_vonoroi_onePoint.set(i, existing_lineSegment);
                    }
                }

                //

                if ((existing_straightLine.sameSide(s_step[tyuusinn_ten_bangou].getA(), add_lineSegment.getA()) <= 0) &&
                        (existing_straightLine.sameSide(s_step[tyuusinn_ten_bangou].getA(), add_lineSegment.getB()) <= 0)) {
                    return;
                } else if ((existing_straightLine.sameSide(s_step[tyuusinn_ten_bangou].getA(), add_lineSegment.getA()) == 1) &&
                        (existing_straightLine.sameSide(s_step[tyuusinn_ten_bangou].getA(), add_lineSegment.getB()) == -1)) {
                    add_lineSegment.set(add_lineSegment.getA(), intersection);
                    if (add_lineSegment.getLength() < 0.0000001) {
                        return;
                    }
                } else if ((existing_straightLine.sameSide(s_step[tyuusinn_ten_bangou].getA(), add_lineSegment.getA()) == -1) &&
                        (existing_straightLine.sameSide(s_step[tyuusinn_ten_bangou].getA(), add_lineSegment.getB()) == 1)) {
                    add_lineSegment.set(intersection, add_lineSegment.getB());
                    if (add_lineSegment.getLength() < 0.0000001) {
                        return;
                    }
                }


            } else if (parallel == OritaCalc.ParallelJudgement.PARALLEL_NOT_EQUAL) {//When the line segment to be added and the existing line segment are parallel and the two straight lines do not match
                if (add_straightLine.sameSide(s_step[tyuusinn_ten_bangou].getA(), existing_lineSegment.getA()) == -1) {
                    lineSegment_vonoroi_onePoint.remove(i);
                } else if (existing_straightLine.sameSide(s_step[tyuusinn_ten_bangou].getA(), add_lineSegment.getA()) == -1) {
                    return;
                }


            } else if (parallel == OritaCalc.ParallelJudgement.PARALLEL_EQUAL) {//When the line segment to be added and the existing line segment are parallel and the two straight lines match
                return;
            }
        }

        lineSegment_vonoroi_onePoint.add(add_lineSegment);
    }

    public void Senb_boro_1p_motome(int center_point_count) {//It can be used when s_step contains only Voronoi mother points. Get Senb_boro_1p as a set of Voronoi line segments around center_point_count
        //i_egaki_dankai Obtain an array list of Voronoi line segments surrounding the third Voronoi vertex. // i_egaki_dankai The third Voronoi apex is s_step [i_egaki_dankai] .geta ()
        lineSegment_vonoroi_onePoint.clear();

        for (int i_e_d = 1; i_e_d <= i_drawing_stage; i_e_d++) {
            if (i_e_d != center_point_count) {
                //Find the line segment to add
                LineSegment add_lineSegment = new LineSegment();

                add_lineSegment.set(OritaCalc.bisection(s_step[i_e_d].getA(), s_step[center_point_count].getA(), 1000.0));

                System.out.println("center_point_count= " + center_point_count + " ,i_e_d= " + i_e_d);

                if (i_e_d < center_point_count) {
                    add_lineSegment.setVonoroiA(i_e_d);
                    add_lineSegment.setVonoroiB(center_point_count);//Record the two Voronoi vertices of the Voronoi line segment in iactive and color
                } else {
                    add_lineSegment.setVonoroiA(center_point_count);
                    add_lineSegment.setVonoroiB(i_e_d);//Record the two Voronoi vertices of the Voronoi line segment in iactive and color
                }
                voronoi_02_01(center_point_count, add_lineSegment);
            }
        }
    }

    public void voronoi_02() {//i=1からi_egaki_dankaiまでのs_step[i]と、i_egaki_dankai-1までのボロノイ図からi_egaki_dankaiのボロノイ図を作成

        //i_egaki_dankai番目のボロノイ頂点を取り囲むボロノイ線分のアレイリストを得る。
        Senb_boro_1p_motome(i_drawing_stage);

        //20181109ここでori_v.の既存のボロノイ線分の整理が必要

        //ori_vの線分を最初に全て非選択にする
        ori_v.unselect_all();

        //
        LineSegment s_begin = new LineSegment();
        LineSegment s_end = new LineSegment();

        for (int ia = 0; ia < lineSegment_vonoroi_onePoint.size() - 1; ia++) {
            for (int ib = ia + 1; ib < lineSegment_vonoroi_onePoint.size(); ib++) {

                s_begin.set(lineSegment_vonoroi_onePoint.get(ia));
                s_end.set(lineSegment_vonoroi_onePoint.get(ib));

                StraightLine t_begin = new StraightLine(s_begin);
                StraightLine t_end = new StraightLine(s_end);

                int i_begin = s_begin.getVonoroiA();//In this case, vonoroiA contains the number of the existing Voronoi mother point when the Voronoi line segment is added.
                int i_end = s_end.getVonoroiA();//In this case, vonoroiA contains the number of the existing Voronoi mother point when the Voronoi line segment is added.


                if (i_begin > i_end) {
                    int i_temp = i_begin;
                    i_begin = i_end;
                    i_end = i_temp;
                }

                //The surrounding Voronoi line segment created by adding a new Voronoi matrix is being sought. The polygon of this Voronoi line segment is called a new cell.
                // Before adding a new cell to ori_v, process so that there is no existing line segment of ori_v that is inside the new cell.

                //20181109ここでori_v.の既存のボロノイ線分(iactive()が必ずicolorより小さくなっている)を探す
                for (int j = 1; j <= ori_v.getTotal(); j++) {
                    LineSegment s_kizon = new LineSegment();
                    s_kizon.set(ori_v.get(j));

                    int i_kizon_syou = s_kizon.getVonoroiA();
                    int i_kizon_dai = s_kizon.getVonoroiB();

                    if (i_kizon_syou > i_kizon_dai) {
                        i_kizon_dai = s_kizon.getVonoroiA();
                        i_kizon_syou = s_kizon.getVonoroiB();
                    }

                    if (i_kizon_syou == i_begin) {
                        if (i_kizon_dai == i_end) {

//20181110ここポイント
//
//	-1		0		1
//-1 	何もせず	何もせず	交点まで縮小
// 0	何もせず	有り得ない	削除
// 1	交点まで縮小	削除		削除
//

                            Point kouten = new Point();
                            kouten.set(OritaCalc.findIntersection(s_begin, s_kizon));

                            if ((t_begin.sameSide(s_step[i_drawing_stage].getA(), s_kizon.getA()) >= 0) &&
                                    (t_begin.sameSide(s_step[i_drawing_stage].getA(), s_kizon.getB()) >= 0)) {
                                ori_v.select(j);
                            }

                            if ((t_begin.sameSide(s_step[i_drawing_stage].getA(), s_kizon.getA()) == -1) &&
                                    (t_begin.sameSide(s_step[i_drawing_stage].getA(), s_kizon.getB()) == 1)) {
                                ori_v.set(j, s_kizon.getA(), kouten);
                            }

                            if ((t_begin.sameSide(s_step[i_drawing_stage].getA(), s_kizon.getA()) == 1) &&
                                    (t_begin.sameSide(s_step[i_drawing_stage].getA(), s_kizon.getB()) == -1)) {
                                ori_v.set(j, kouten, s_kizon.getB());
                            }
                        }
                    }
                }
            }
        }

        //for (int i=1; i<=ori_v.getsousuu(); i++ ){System.out.println("    (1)  i= " + i +  ":  ori_v.get(i).getiactive()=  " +  ori_v.get(i).getiactive());}
        //選択状態のものを削除
        ori_v.del_selected_lineSegment_fast();

        ori_v.del_V_all(); //この行はいらないかも


        //Add the line segment of Senb_boro_1p to the end of senbun of ori_v
        for (LineSegment lineSegment : lineSegment_vonoroi_onePoint) {
            LineSegment add_S = new LineSegment();
            add_S.set(lineSegment);
            ori_v.addLine(lineSegment);
        }
    }

    //5 5 5 5 5 55555555555555555    i_mouse_modeA==5　;線分延長モード
    //マウス操作(マウスを動かしたとき)を行う関数    //System.out.println("_");
    public void mMoved_A_05(Point p0) {
        mMoved_A_05or70(p0);
    }//常にマウスの位置のみが候補点

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_05(Point p0) {
        mPressed_A_05or70(p0);
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_05(Point p0) {
        mDragged_A_05or70(p0);
    }





/*
//------折り畳み可能線+格子点系入力


//71 71 71 71 71 71 71    i_mouse_modeA==71　;折り畳み可能線入力  qqqqqqqqq
int i_step_for71=0;//i_step_for71=2の場合は、step線が1本だけになっていて、次の操作で入力折線が確定する状態
//
//課題　step線と既存折線が平行の時エラー方向に線を引くことを改善すること20170407
//
//動作仕様
//（１）点を選択（既存点選択規制）
//（２a）選択点が3以上の奇数折線の頂点の場合
//（３）
//
//
//（２b）２a以外の場合
//



	//マウス操作(マウスを動かしたとき)を行う関数    //System.out.println("_");


	//マウス操作(ボタンを押したとき)時の作業--------------
	public void mPressed_A_71(Ten p0) {
		//Ten p =new Ten();
		p.set(camera.TV2object(p0));

		if(i_egaki_dankai==0){i_step_for71=0;}



		//if(i_egaki_dankai==0){i_step_for71=0;}

		if(i_step_for71==0){
			double hantei_kyori=0.000001;

			//任意の点が与えられたとき、端点もしくは格子点で最も近い点を得る
			moyori_ten.set(get_moyori_ten(p));

			if(p.kyori(moyori_ten)<d_decision_width){
				//i_egaki_dankai=i_egaki_dankai+1;
				//s_step[i_egaki_dankai].set(moyori_ten,moyori_ten);s_step[i_egaki_dankai].setcolor(i_egaki_dankai);

				//moyori_tenを端点とする折線をNarabebakoに入れる
				Narabebako_int_double nbox =new Narabebako_int_double();
				for (int i=1; i<=ori_s.getsousuu(); i++ ){ if((0<=ori_s.getcolor(i))&&(ori_s.getcolor(i)<=2)){
					if(moyori_ten.kyori(ori_s.geta(i))<hantei_kyori){
						nbox.ire_i_tiisaijyun(new int_double( i  , oc.kakudo(ori_s.geta(i),ori_s.getb(i)) ));
					}else if(moyori_ten.kyori(ori_s.getb(i))<hantei_kyori){
						nbox.ire_i_tiisaijyun(new int_double( i  , oc.kakudo(ori_s.getb(i),ori_s.geta(i)) ));
					}
				}}
				//System.out.println("nbox.getsousuu()="+nbox.getsousuu());
				if(nbox.getsousuu()%2==1){//moyori_tenを端点とする折線の数が奇数のときだけif{}内の処理をする
					//System.out.println("20170130_3");

					//int i_kouho_suu=0;
					for (int i=1; i<=nbox.getsousuu(); i++ ){//iは角加減値を求める最初の折線のid
						//折線が奇数の頂点周りの角加減値を2.0で割ると角加減値の最初折線と、折り畳み可能にするための追加の折線との角度になる。
						double kakukagenti=0.0;
						//System.out.println("nbox.getsousuu()="+nbox.getsousuu());
						int tikai_orisen_jyunban;
						int tooi_orisen_jyunban;
						for (int k=1; k<=nbox.getsousuu(); k++ ){//kは角加減値を求める角度の順番
							tikai_orisen_jyunban=i+k-1;if(tikai_orisen_jyunban>nbox.getsousuu()){tikai_orisen_jyunban=tikai_orisen_jyunban-nbox.getsousuu();}
							tooi_orisen_jyunban =i+k  ;if(tooi_orisen_jyunban >nbox.getsousuu()){tooi_orisen_jyunban =tooi_orisen_jyunban -nbox.getsousuu();}

							double add_kakudo=oc.kakudo_osame_0_360(nbox.get_double(tooi_orisen_jyunban)-nbox.get_double(tikai_orisen_jyunban));
							if(k%2==1){kakukagenti=kakukagenti+add_kakudo;
							}else if(k%2==0){kakukagenti=kakukagenti-add_kakudo;
							}
							//System.out.println("i="+i+"   k="+k);
						}

if(nbox.getsousuu()==1){kakukagenti=360.0;}
						//System.out.println("kakukagenti="+kakukagenti);
						//チェック用に角加減値の最初の角度の中にkakukagenti/2.0があるかを確認する
						tikai_orisen_jyunban=i  ;if(tikai_orisen_jyunban>nbox.getsousuu()){tikai_orisen_jyunban=tikai_orisen_jyunban-nbox.getsousuu();}
						tooi_orisen_jyunban =i+1;if(tooi_orisen_jyunban >nbox.getsousuu()){tooi_orisen_jyunban =tooi_orisen_jyunban -nbox.getsousuu();}

						double add_kakudo_1=oc.kakudo_osame_0_360(nbox.get_double(tooi_orisen_jyunban)-nbox.get_double(tikai_orisen_jyunban));
if(nbox.getsousuu()==1){add_kakudo_1=360.0;}

						if((kakukagenti/2.0>0.0+0.000001)&&(kakukagenti/2.0<add_kakudo_1-0.000001)){
							i_egaki_dankai=i_egaki_dankai+1;

							//線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d)
							Senbun s_kiso =new Senbun();
							if(moyori_ten.kyori(ori_s.geta(nbox.get_int(i)))<hantei_kyori){
								s_kiso.set(ori_s.geta(nbox.get_int(i)),ori_s.getb(nbox.get_int(i)));
							}else if(moyori_ten.kyori(ori_s.getb(nbox.get_int(i)))<hantei_kyori){
								s_kiso.set(ori_s.getb(nbox.get_int(i)),ori_s.geta(nbox.get_int(i)));
							}

							double s_kiso_nagasa=s_kiso.getnagasa();

							s_step[i_egaki_dankai].set(oc.Senbun_kaiten(s_kiso,kakukagenti/2.0,kus.d_haba()/s_kiso_nagasa) );
						 	s_step[i_egaki_dankai].setcolor(8);
							s_step[i_egaki_dankai].setiactive(1);

						}

					}
					//if(i_kouho_suu==1){i_step_for71=2;}
					//if(i_kouho_suu>1){i_step_for71=1;}

					if(i_egaki_dankai==1){i_step_for71=2;}
					if(i_egaki_dankai>1){i_step_for71=1;}
				}

				if(i_egaki_dankai==0){//折畳み可能化線がない場合//System.out.println("_");
					i_egaki_dankai=1;
					i_step_for71=1;
					s_step[1].set(moyori_ten,moyori_ten);
				 	s_step[1].setcolor(8);
					s_step[1].setiactive(3);
				}

			}
			return;
		}



		if(i_step_for71==1){
			moyori_senbun.set(get_moyori_step_senbun(p,1,i_egaki_dankai));
			if((i_egaki_dankai>=2)&&(oc.kyori_senbun( p,moyori_senbun)<d_decision_width)){
			//if(oc.kyori_senbun( p,moyori_senbun)<d_decision_width){
				//System.out.println("20170129_5");
				i_step_for71=2;
				i_egaki_dankai=1;
				s_step[1].set(moyori_senbun);
				return;
			}
			//if(oc.kyori_senbun( p,moyori_senbun)>=d_decision_width){
				//System.out.println("");
				moyori_ten.set(get_moyori_ten(p));
				if(p.kyori(moyori_ten)<d_decision_width){
					s_step[1].setb(moyori_ten);
					i_step_for71=2;i_egaki_dankai=1;
					return;
				}
				//System.out.println("20170129_7");
				i_egaki_dankai=0;i_candidate_stage=0;
				return;
			//}
			//return;
		}



		if(i_step_for71==2){//i_step_for71==2であれば、以下でs_step[1]を入力折線を確定する
			moyori_ten.set(get_moyori_ten(p));

			//System.out.println("20170130_1");
			if(moyori_ten.kyori(s_step[1].geta())< 0.00000001 ){
				i_egaki_dankai=0;i_candidate_stage=0;
				return;
			}
			//else if(p.kyori(s_step[1].getb())< kus.d_haba()/10.0 ){
			//else if(p.kyori(s_step[1].getb())< d_decision_width/2.5 ){
			//else if(p.kyori(s_step[1].getb())< d_decision_width ){

			if((p.kyori(s_step[1].getb())< d_decision_width )&&
				(
				p.kyori(s_step[1].getb())<=p.kyori(moyori_ten)
				//moyori_ten.kyori(s_step[1].getb())<0.00000001
				)){
				Senbun add_sen =new Senbun(s_step[1].geta(),s_step[1].getb(),icol);
				addsenbun(add_sen);
				kiroku();
				i_egaki_dankai=0;i_candidate_stage=0;
				return;
			}

		//}


		//if(i_step_for39==2){

			//moyori_ten.set(get_moyori_ten(p));
			if(p.kyori(moyori_ten)<d_decision_width){
				s_step[1].setb(moyori_ten);return;
			}



			moyori_senbun.set(get_moyori_senbun(p));

			Senbun moyori_step_senbun =new Senbun();moyori_step_senbun.set(get_moyori_step_senbun(p,1,i_egaki_dankai));
			if(oc.kyori_senbun( p,moyori_senbun)>=d_decision_width){//最寄の既存折線が遠い場合
				//moyori_senbun.set(get_moyori_step_senbun(p,1,i_egaki_dankai));


				//moyori_ten.set(get_moyori_ten(p));if(p.kyori(moyori_ten)<d_decision_width){s_step[1].setb(moyori_ten);return;}
				//moyori_ten.set(ori_s.mottomo_tikai_Ten(p));if(p.kyori(moyori_ten)<d_decision_width){s_step[1].setb(moyori_ten);return;}



				if(oc.kyori_senbun( p,moyori_step_senbun)<d_decision_width){//最寄のstep_senbunが近い場合

					//moyori_ten.set(get_moyori_ten(p));if(p.kyori(moyori_ten)<d_decision_width){s_step[1].setb(moyori_ten);return;}




					return;
				}
				//最寄のstep_senbunが遠い場合

					//moyori_ten.set(get_moyori_ten(p));if(p.kyori(moyori_ten)<d_decision_width){s_step[1].setb(moyori_ten);return;}
				i_egaki_dankai=0;i_candidate_stage=0;
				return;
			}

			if(oc.kyori_senbun( p,moyori_senbun)<d_decision_width){//最寄の既存折線が近い場合
				//moyori_ten.set(ori_s.mottomo_tikai_Ten(p));if(p.kyori(moyori_ten)<d_decision_width){s_step[1].setb(moyori_ten);return;}
				s_step[2].set(moyori_senbun);
				s_step[2].setcolor(6);
				//System.out.println("20170129_3");
				Ten kousa_ten =new Ten(); kousa_ten.set(oc.kouten_motome(s_step[1],s_step[2]));
				Senbun add_sen =new Senbun(kousa_ten,s_step[1].geta(),icol);
				if(add_sen.getnagasa()>0.00000001){//最寄の既存折線が有効の場合
					addsenbun(add_sen);
					kiroku();
					i_egaki_dankai=0;i_candidate_stage=0;
					return;
				}
				//最寄の既存折線が無効の場合
				moyori_ten.set(get_moyori_ten(p));if(p.kyori(moyori_ten)<d_decision_width){s_step[1].setb(moyori_ten);return;}
				//最寄のstep_senbunが近い場合
				if(oc.kyori_senbun( p,moyori_step_senbun)<d_decision_width){
					return;
				}
				//最寄のstep_senbunが遠い場合
				i_egaki_dankai=0;i_candidate_stage=0;
				return;

			}
			return;
		}





	}

//マウス操作(ドラッグしたとき)を行う関数
	public void mDragged_A_71(Ten p0) {	}

//マウス操作(ボタンを離したとき)を行う関数
	public void mReleased_A_71(Ten p0){

	}












*/


//7777777777777777777    i_mouse_modeA==7;角二等分線モード　

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_05(Point p0) {
        mReleased_A_05or70(p0);
    }

    //70 70 70 70 70 70 70 70 70 70 70 70 70 70    i_mouse_modeA==70　;線分延長モード
    //マウス操作(マウスを動かしたとき)を行う関数    //System.out.println("_");
    public void mMoved_A_70(Point p0) {
        mMoved_A_05or70(p0);
    }//常にマウスの位置のみが候補点

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_70(Point p0) {
        mPressed_A_05or70(p0);
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_70(Point p0) {
        mDragged_A_05or70(p0);
    }

//------


//88888888888888888888888    i_mouse_modeA==8　;内心モード。

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_70(Point p0) {
        mReleased_A_05or70(p0);
    }

    //マウス操作(マウスを動かしたとき)を行う関数    //System.out.println("_");
    public void mMoved_A_05or70(Point p0) {
        mMoved_m_003(p0, icol);
    }//常にマウスの位置のみが候補点

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_05or70(Point p0) {
        p.set(camera.TV2object(p0));
        i_candidate_stage = 0;

        if (i_drawing_stage == 0) {
            entyou_kouho_nbox.reset();
            i_drawing_stage = 1;

            s_step[1].set(p, p);
            s_step[1].setColor(LineColor.MAGENTA_5);//マゼンタ
            return;
        }

        if (i_drawing_stage >= 2) {

            i_drawing_stage = i_drawing_stage + 1;
            s_step[i_drawing_stage].set(p, p);
            s_step[i_drawing_stage].setColor(LineColor.MAGENTA_5);//マゼンタ
            return;
        }

    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_05or70(Point p0) {
        p.set(camera.TV2object(p0));
        if (i_drawing_stage == 1) {
            s_step[i_drawing_stage].setB(p);
        }
        if (i_drawing_stage > 1) {
            s_step[i_drawing_stage].set(p, p);
        }
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_05or70(Point p0) {
        p.set(camera.TV2object(p0));
        closest_lineSegment.set(get_moyori_senbun(p));


        if (i_drawing_stage == 1) {

            s_step[1].setB(p);


            for (int i = 1; i <= ori_s.getTotal(); i++) {
                LineSegment.Intersection i_lineSegment_intersection_decision = OritaCalc.line_intersect_decide(ori_s.get(i), s_step[1], 0.0001, 0.0001);
                int i_jikkou = 0;

                if (i_lineSegment_intersection_decision == LineSegment.Intersection.INTERSECTS_1) {
                    i_jikkou = 1;
                }
                //if(i_lineSegment_intersection_decision== 27 ){ i_jikkou=1;}
                //if(i_lineSegment_intersection_decision== 28 ){ i_jikkou=1;}

                if (i_jikkou == 1) {
                    int_double i_d = new int_double(i, OritaCalc.distance(s_step[1].getA(), OritaCalc.findIntersection(ori_s.get(i), s_step[1])));
                    entyou_kouho_nbox.container_i_smallest_first(i_d);
                }


            }
            if ((entyou_kouho_nbox.getTotal() == 0) && (s_step[1].getLength() <= 0.000001)) {//延長する候補になる折線を選ぶために描いた線分s_step[1]が点状のときの処理
                if (OritaCalc.distance_lineSegment(p, closest_lineSegment) < d_decision_width) {
                    int_double i_d = new int_double(ori_s.closestLineSegmentSearch(p), 1.0);//entyou_kouho_nboxに1本の情報しか入らないのでdoubleの部分はどうでもよいので適当に1.0にした。
                    entyou_kouho_nbox.container_i_smallest_first(i_d);

                    s_step[1].setB(OritaCalc.lineSymmetry_point_find(closest_lineSegment.getA(), closest_lineSegment.getB(), p));

                    s_step[1].set(//s_step[1]を短くして、表示時に目立たない様にする。
                            OritaCalc.point_double(OritaCalc.midPoint(s_step[1].getA(), s_step[1].getB()), s_step[1].getA(), 0.00001 / s_step[1].getLength())
                            ,
                            OritaCalc.point_double(OritaCalc.midPoint(s_step[1].getA(), s_step[1].getB()), s_step[1].getB(), 0.00001 / s_step[1].getLength())
                    );

                }

            }

            System.out.println(" entyou_kouho_nbox.getsousuu() = " + entyou_kouho_nbox.getTotal());


            if (entyou_kouho_nbox.getTotal() == 0) {
                i_drawing_stage = 0;
                return;
            }
            if (entyou_kouho_nbox.getTotal() >= 0) {

                i_drawing_stage = 1 + entyou_kouho_nbox.getTotal();

                for (int i = 2; i <= i_drawing_stage; i++) {
                    s_step[i].set(ori_s.get(entyou_kouho_nbox.getInt(i - 1)));
                    s_step[i].setColor(LineColor.GREEN_6);//グリーン
                }
                return;
            }
            return;
        }


        if (i_drawing_stage >= 3) {
            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) >= d_decision_width) {
                i_drawing_stage = 0;
                return;
            }

            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) < d_decision_width) {


                //最初に選んだ延長候補線分群中に2番目に選んだ線分と等しいものがあるかどうかを判断する。
                int i_senbun_entyou_mode = 0;// i_senbun_entyou_mode=0なら最初に選んだ延長候補線分群中に2番目に選んだ線分と等しいものがない。1ならある。
                for (int i = 1; i <= entyou_kouho_nbox.getTotal(); i++) {
                    if (OritaCalc.line_intersect_decide(ori_s.get(entyou_kouho_nbox.getInt(i)), closest_lineSegment, 0.000001, 0.000001) == LineSegment.Intersection.PARALLEL_EQUAL_31) {//線分が同じならoc.senbun_kousa_hantei==31
                        i_senbun_entyou_mode = 1;
                    }
                }


                LineSegment add_sen = new LineSegment();
                //最初に選んだ延長候補線分群中に2番目に選んだ線分と等しいものがない場合
                if (i_senbun_entyou_mode == 0) {
                    int sousuu_old = ori_s.getTotal();//(1)
                    for (int i = 1; i <= entyou_kouho_nbox.getTotal(); i++) {
                        //最初に選んだ線分と2番目に選んだ線分が平行でない場合
                        if (OritaCalc.parallel_judgement(ori_s.get(entyou_kouho_nbox.getInt(i)), closest_lineSegment, 0.000001) == OritaCalc.ParallelJudgement.NOT_PARALLEL) { //２つの線分が平行かどうかを判定する関数。oc.heikou_hantei(Tyokusen t1,Tyokusen t2)//0=平行でない
                            //s_step[1]とs_step[2]の交点はoc.kouten_motome(Senbun s1,Senbun s2)で求める//２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
                            Point kousa_point = new Point();
                            kousa_point.set(OritaCalc.findIntersection(ori_s.get(entyou_kouho_nbox.getInt(i)), closest_lineSegment));
                            //add_sen =new Senbun(kousa_ten,ori_s.get(entyou_kouho_nbox.get_int(i)).get_tikai_hasi(kousa_ten));
                            add_sen.setA(kousa_point);
                            add_sen.setB(ori_s.get(entyou_kouho_nbox.getInt(i)).getClosestEndpoint(kousa_point));


                            if (add_sen.getLength() > 0.00000001) {
                                if (orihime_app.i_mouse_modeA == MouseMode.LENGTHEN_CREASE_5) {
                                    add_sen.setColor(icol);
                                }
                                if (orihime_app.i_mouse_modeA == MouseMode.CREASE_LENGTHEN_70) {
                                    add_sen.setColor(ori_s.get(entyou_kouho_nbox.getInt(i)).getColor());
                                }

                                //addsenbun(add_sen);
                                ori_s.addLine(add_sen);//ori_sのsenbunの最後にs0の情報をを加えるだけ//(2)
                            }
                        }
                    }
                    ori_s.lineSegment_circle_intersection(sousuu_old, ori_s.getTotal(), 1, ori_s.cir_size());//(3)
                    ori_s.intersect_divide(1, sousuu_old, sousuu_old + 1, ori_s.getTotal());//(4)


                }

                //最初に選んだ延長候補線分群中に2番目に選んだ線分と等しいものがある場合
                if (i_senbun_entyou_mode == 1) {

                    int sousuu_old = ori_s.getTotal();//(1)
                    for (int i = 1; i <= entyou_kouho_nbox.getTotal(); i++) {
                        LineSegment moto_no_sen = new LineSegment();
                        moto_no_sen.set(ori_s.get(entyou_kouho_nbox.getInt(i)));
                        Point p_point = new Point();
                        p_point.set(OritaCalc.findIntersection(moto_no_sen, s_step[1]));

                        if (p_point.distance(moto_no_sen.getA()) < p_point.distance(moto_no_sen.getB())) {
                            moto_no_sen.a_b_swap();
                        }
                        add_sen.set(extendToIntersectionPoint_2(moto_no_sen));


                        if (add_sen.getLength() > 0.00000001) {
                            if (orihime_app.i_mouse_modeA == MouseMode.LENGTHEN_CREASE_5) {
                                add_sen.setColor(icol);
                            }
                            if (orihime_app.i_mouse_modeA == MouseMode.CREASE_LENGTHEN_70) {
                                add_sen.setColor(ori_s.get(entyou_kouho_nbox.getInt(i)).getColor());
                            }

                            ori_s.addLine(add_sen);//ori_sのsenbunの最後にs0の情報をを加えるだけ//(2)
                        }

                    }
                    ori_s.lineSegment_circle_intersection(sousuu_old, ori_s.getTotal(), 1, ori_s.cir_size());//(3)
                    ori_s.intersect_divide(1, sousuu_old, sousuu_old + 1, ori_s.getTotal());//(4)


                }


                record();


                i_drawing_stage = 0;
            }
        }


    }

    //マウス操作(マウスを動かしたとき)を行う関数    //System.out.println("_");
    public void mMoved_A_71(Point p0) {
        if (i_drawing_stage == 0) {
            i_dousa_mode = 0;
            mMoved_A_01(p0);
            return;
        }

        if (i_dousa_mode == 1) {
            mMoved_A_01(p0);
        }
        if (i_dousa_mode == 38) {
            mMoved_A_38(p0);
        }
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_71(Point p0) {
        i_dousa_mode_henkou_kanousei = 0;

        p.set(camera.TV2object(p0));
        double hantei_kyori = 0.000001;

        if (p.distance(moyori_point_memo) <= d_decision_width) {
            i_drawing_stage = 0;
        }


        if (i_drawing_stage == 0) {


            //任意の点が与えられたとき、端点もしくは格子点で最も近い点を得る
            closest_point.set(getClosestPoint(p));
            moyori_point_memo.set(closest_point);

            if (p.distance(closest_point) > d_decision_width) {
                closest_point.set(p);
            }

            //moyori_tenを端点とする折線をNarabebakoに入れる
            SortingBox_int_double nbox = new SortingBox_int_double();
            for (int i = 1; i <= ori_s.getTotal(); i++) {
                if (ori_s.getColor(i).isFoldingLine()) {
                    if (closest_point.distance(ori_s.getA(i)) < hantei_kyori) {
                        nbox.container_i_smallest_first(new int_double(i, OritaCalc.angle(ori_s.getA(i), ori_s.getB(i))));
                    } else if (closest_point.distance(ori_s.getB(i)) < hantei_kyori) {
                        nbox.container_i_smallest_first(new int_double(i, OritaCalc.angle(ori_s.getB(i), ori_s.getA(i))));
                    }
                }
            }
            if (nbox.getTotal() % 2 == 0) {
                i_dousa_mode = 1;
                i_foldLine_additional = FoldLineAdditionalInputMode.POLY_LINE_0;
            }//moyori_tenを端点とする折線の数が偶数のときif{}内の処理をする
            if (nbox.getTotal() % 2 == 1) {
                i_dousa_mode = 38;
                i_dousa_mode_henkou_kanousei = 1;
            }//moyori_tenを端点とする折線の数が奇数のときif{}内の処理をする

        }

        if (i_dousa_mode == 1) {
            mPressed_A_01(p0);
        }
        if (i_dousa_mode == 38) {
            if (mPressed_A_38(p0) == 0) {
                if (i_drawing_stage == 0) {
                    mPressed_A_71(p0);
                }
            }
        }


    }

    //マウス操作(ドラッグしたとき)を行う関数20200
    public void mDragged_A_71(Point p0) {
        if ((i_dousa_mode == 38) && (i_dousa_mode_henkou_kanousei == 1)) {
            //if(i_dousa_mode==38){
            p.set(camera.TV2object(p0));
            moyori_point_memo.set(closest_point);
            if (p.distance(moyori_point_memo) > d_decision_width) {
                i_dousa_mode = 1;
                i_drawing_stage = 1;
                s_step[1].a_b_swap();
                s_step[1].setColor(icol);
                i_dousa_mode_henkou_kanousei = 0;
            }

        }

        if (i_dousa_mode == 1) {
            mDragged_A_01(p0);
        }
        if (i_dousa_mode == 38) {
            mDragged_A_38(p0);
        }
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_71(Point p0) {
        if (i_dousa_mode == 1) {
            mReleased_A_01(p0);
        }
        if (i_dousa_mode == 38) {
            mReleased_A_38(p0);
        }
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_07(Point p0) {
        if ((i_drawing_stage >= 0) && (i_drawing_stage <= 2)) {
            mMoved_A_29(p0);//近い既存点のみ表示
        }

    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_07(Point p0) {


        Point p = new Point();
        p.set(camera.TV2object(p0));

        if ((i_drawing_stage >= 0) && (i_drawing_stage <= 2)) {
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) < d_decision_width) {
                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_point, closest_point);
                s_step[i_drawing_stage].setColor(icol);
                return;
            }
        }

        if (i_drawing_stage == 3) {
            closest_lineSegment.set(get_moyori_senbun(p));
            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) < d_decision_width) {
                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_lineSegment);//s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
                s_step[i_drawing_stage].setColor(LineColor.GREEN_6);
            }
        }

    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_07(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_07(Point p0) {
        if (i_drawing_stage == 4) {
            i_drawing_stage = 0;

            //三角形の内心を求める	public Ten oc.naisin(Ten ta,Ten tb,Ten tc)
            Point naisin = new Point();
            naisin.set(OritaCalc.center(s_step[1].getA(), s_step[2].getA(), s_step[3].getA()));


            LineSegment add_sen2 = new LineSegment(s_step[2].getA(), naisin);


            //add_sen2とs_step[4]の交点はoc.kouten_motome(Senbun s1,Senbun s2)で求める//２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
            Point kousa_point = new Point();
            kousa_point.set(OritaCalc.findIntersection(add_sen2, s_step[4]));

            LineSegment add_sen = new LineSegment(kousa_point, s_step[2].getA(), icol);
            if (add_sen.getLength() > 0.00000001) {
                addLineSegment(add_sen);
                record();
            }


        }


    }

//------

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_08(Point p0) {
        mMoved_A_29(p0);
    }//近い既存点のみ表示

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_08(Point p0) {


        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) < d_decision_width) {
            i_drawing_stage = i_drawing_stage + 1;
            s_step[i_drawing_stage].set(closest_point, closest_point);
            s_step[i_drawing_stage].setColor(icol);
        }


    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_08(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_08(Point p0) {
        if (i_drawing_stage == 3) {
            i_drawing_stage = 0;

            //三角形の内心を求める	public Ten oc.naisin(Ten ta,Ten tb,Ten tc)
            Point naisin = new Point();
            naisin.set(OritaCalc.center(s_step[1].getA(), s_step[2].getA(), s_step[3].getA()));

            LineSegment add_sen1 = new LineSegment(s_step[1].getA(), naisin, icol);
            if (add_sen1.getLength() > 0.00000001) {
                addLineSegment(add_sen1);
            }
            LineSegment add_sen2 = new LineSegment(s_step[2].getA(), naisin, icol);
            if (add_sen2.getLength() > 0.00000001) {
                addLineSegment(add_sen2);
            }
            LineSegment add_sen3 = new LineSegment(s_step[3].getA(), naisin, icol);
            if (add_sen3.getLength() > 0.00000001) {
                addLineSegment(add_sen3);
            }
            record();
        }


    }

//------

    //------
    public double get_L1() {
        return measured_length_1;
    }

    public double get_L2() {
        return measured_length_2;
    }

    public double get_A1() {
        return measured_angle_1;
    }

    public double get_A2() {
        return measured_angle_2;
    }
//------

    public double get_A3() {
        return measured_angle_3;
    }

    //53 53 53 53 53 53 53 53 53    i_mouse_modeA==53　;長さ測定１モード。
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_53(Point p0) {
        mMoved_A_29(p0);
    }//近い既存点のみ表示

    //Work when operating the mouse (when the button is pressed)
    public void mPressed_A_53(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) < d_decision_width) {
            i_drawing_stage = i_drawing_stage + 1;
            s_step[i_drawing_stage].set(closest_point, closest_point);
            s_step[i_drawing_stage].setColor(icol);
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_53(Point p0) {
    }
//------

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_53(Point p0) {
        if (i_drawing_stage == 2) {
            i_drawing_stage = 0;
            measured_length_1 = OritaCalc.distance(s_step[1].getA(), s_step[2].getA()) * (double) grid.divisionNumber() / 400.0;

            orihime_app.measured_length_1_display(measured_length_1);
            //kiroku();
        }


    }

    //------
//54 54 54 54 54 54 54 54 54    i_mouse_modeA==54　;長さ測定2モード。
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_54(Point p0) {
        mMoved_A_29(p0);
    }//近い既存点のみ表示

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_54(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) < d_decision_width) {
            i_drawing_stage = i_drawing_stage + 1;
            s_step[i_drawing_stage].set(closest_point, closest_point);
            s_step[i_drawing_stage].setColor(icol);
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_54(Point p0) {
    }
//------


//999999999999999999    i_mouse_modeA==9　;垂線おろしモード

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_54(Point p0) {
        if (i_drawing_stage == 2) {
            i_drawing_stage = 0;
            measured_length_2 = OritaCalc.distance(s_step[1].getA(), s_step[2].getA()) * (double) grid.divisionNumber() / 400.0;

            orihime_app.measured_length_2_display(measured_length_2);
            //kiroku();
        }


    }

    //------
//55 55 55 55 55 55 55 55 55    i_mouse_modeA==55　;角度測定1モード。
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_55(Point p0) {
        mMoved_A_29(p0);
    }//近い既存点のみ表示

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_55(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) < d_decision_width) {
            i_drawing_stage = i_drawing_stage + 1;
            s_step[i_drawing_stage].set(closest_point, closest_point);
            s_step[i_drawing_stage].setColor(icol);
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_55(Point p0) {
    }
//------
//------
//40 40 40 40 40 40     i_mouse_modeA==40　;平行線入力モード

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_55(Point p0) {
        if (i_drawing_stage == 3) {
            i_drawing_stage = 0;
            measured_angle_1 = OritaCalc.angle(s_step[2].getA(), s_step[3].getA(), s_step[2].getA(), s_step[1].getA());
            if (measured_angle_1 > 180.0) {
                measured_angle_1 = measured_angle_1 - 360.0;
            }

            orihime_app.measured_angle_1_display(measured_angle_1);
            //kiroku();
        }
    }

    //------
//56 56 56 56 56 56 56 56 56    i_mouse_modeA==56　;角度測定2モード。
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_56(Point p0) {
        mMoved_A_29(p0);
    }//近い既存点のみ表示

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_56(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) < d_decision_width) {
            i_drawing_stage = i_drawing_stage + 1;
            s_step[i_drawing_stage].set(closest_point, closest_point);
            s_step[i_drawing_stage].setColor(icol);
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_56(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_56(Point p0) {
        if (i_drawing_stage == 3) {
            i_drawing_stage = 0;
            measured_angle_2 = OritaCalc.angle(s_step[2].getA(), s_step[3].getA(), s_step[2].getA(), s_step[1].getA());
            if (measured_angle_2 > 180.0) {
                measured_angle_2 = measured_angle_2 - 360.0;
            }
            orihime_app.measured_angle_2_display(measured_angle_2);
            //kiroku();
        }
    }


//10 10 10 10 10    i_mouse_modeA==10　;折り返しモード

    //------
//57 57 57 57 57 57 57 57 57    i_mouse_modeA==57　;角度測定3モード。
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_57(Point p0) {
        mMoved_A_29(p0);
    }//近い既存点のみ表示

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_57(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) < d_decision_width) {
            i_drawing_stage = i_drawing_stage + 1;
            s_step[i_drawing_stage].set(closest_point, closest_point);
            s_step[i_drawing_stage].setColor(icol);
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_57(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_57(Point p0) {
        if (i_drawing_stage == 3) {
            i_drawing_stage = 0;
            measured_angle_3 = OritaCalc.angle(s_step[2].getA(), s_step[3].getA(), s_step[2].getA(), s_step[1].getA());
            if (measured_angle_3 > 180.0) {
                measured_angle_3 = measured_angle_3 - 360.0;
            }
            orihime_app.measured_angle_3_display(measured_angle_3);
            //kiroku();
        }
    }


//52 52 52 52 52    i_mouse_modeA==52　;連続折り返しモード ****************************************

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_09(Point p0) {
        if (i_drawing_stage == 0) {
            mMoved_A_29(p0);//近い既存点のみ表示
        }

    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_09(Point p0) {


        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        if (i_drawing_stage == 0) {
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) < d_decision_width) {
                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_point, closest_point);
                s_step[i_drawing_stage].setColor(icol);
                return;
            }
        }

        if (i_drawing_stage == 1) {
            closest_lineSegment.set(get_moyori_senbun(p));
            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) < d_decision_width) {
                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_lineSegment);
                s_step[i_drawing_stage].setColor(LineColor.GREEN_6);
                return;
            }
            i_drawing_stage = 0;
        }

    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_09(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_09(Point p0) {
        if (i_drawing_stage == 2) {
            i_drawing_stage = 0;
            //直線t上の点pの影の位置（点pと最も近い直線t上の位置）を求める。public Ten oc.kage_motome(Tyokusen t,Ten p){
            //oc.Senbun2Tyokusen(Senbun s)//線分を含む直線を得る

            LineSegment add_sen = new LineSegment(s_step[1].getA(), OritaCalc.shadow_request(OritaCalc.lineSegmentToStraightLine(s_step[2]), s_step[1].getA()), icol);
            if (add_sen.getLength() > 0.00000001) {
                addLineSegment(add_sen);
                record();
            }


        }
    }

// ------------------------------------------------------------
    //２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){
    //Ten t_taisyou =new Ten(); t_taisyou.set(oc.sentaisyou_ten_motome(s_step[2].geta(),s_step[3].geta(),s_step[1].geta()));

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_40(Point p0) {
        if (i_drawing_stage == 0) {
            mMoved_A_29(p0);//近い既存点のみ表示
        }

    }

// ------------------------------------------------------------

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_40(Point p0) {


        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        if (i_drawing_stage == 0) {
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) < d_decision_width) {
                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_point, closest_point);
                s_step[i_drawing_stage].setColor(icol);
                return;
            }
        }

        if (i_drawing_stage == 1) {
            closest_lineSegment.set(get_moyori_senbun(p));
            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) < d_decision_width) {
                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_lineSegment);//s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
                s_step[i_drawing_stage].setColor(LineColor.GREEN_6);
                return;
            }
            //i_egaki_dankai=0;
            return;
        }


        if (i_drawing_stage == 2) {
            closest_lineSegment.set(get_moyori_senbun(p));
            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) < d_decision_width) {
                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_lineSegment);//s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
                s_step[i_drawing_stage].setColor(LineColor.GREEN_6);
                return;
            }
        }
    }
// ------------------------------------------------------------

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_40(Point p0) {
    }
// ------------------------------------------------------------


//--------------------------------------------
//27 27 27 27 27 27 27 27  i_mouse_modeA==27線分分割	入力 27 27 27 27 27 27 27 27
    //動作概要　
    //i_mouse_modeA==1と線分分割以外は同じ　
    //

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_40(Point p0) {
        if (i_drawing_stage == 3) {
            i_drawing_stage = 0;
            //s_step[1]を点状から、s_step[2]に平行な線分にする。
            s_step[1].setB(new Point(s_step[1].getAX() + s_step[2].getBX() - s_step[2].getAX(), s_step[1].getAY() + s_step[2].getBY() - s_step[2].getAY()));


            //Ten kousa_ten =new Ten(); kousa_ten.set(oc.kouten_motome(s_step[1],s_step[3]));

            //Senbun add_sen =new Senbun(kousa_ten,s_step[1].geta(),icol);

            if (s_step_tuika_koutenmade(3, s_step[1], s_step[3], icol) > 0) {
                addLineSegment(s_step[4]);
                record();
                i_drawing_stage = 0;
            }
        }
    }

    //------
    //i_egaki_dankaiがi_e_dのときに、線分s_oをTen aはそのままで、Ten b側をs_kの交点までのばした一時折線s_step[i_e_d+1](色はicolo)を追加。成功した場合は1、なんらかの不都合で追加できなかった場合は-500を返す。
    public int s_step_tuika_koutenmade(int i_e_d, LineSegment s_o, LineSegment s_k, LineColor icolo) {

        Point kousa_point = new Point();

        if (OritaCalc.parallel_judgement(s_o, s_k, 0.0000001) == OritaCalc.ParallelJudgement.PARALLEL_NOT_EQUAL) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            return -500;
        }

        if (OritaCalc.parallel_judgement(s_o, s_k, 0.0000001) == OritaCalc.ParallelJudgement.PARALLEL_EQUAL) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            kousa_point.set(s_k.getA());
            if (OritaCalc.distance(s_o.getA(), s_k.getA()) > OritaCalc.distance(s_o.getA(), s_k.getB())) {
                kousa_point.set(s_k.getB());
            }


        }

        if (OritaCalc.parallel_judgement(s_o, s_k, 0.0000001) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            kousa_point.set(OritaCalc.findIntersection(s_o, s_k));
        }


        LineSegment add_sen = new LineSegment(kousa_point, s_o.getA(), icolo);

        if (add_sen.getLength() > 0.00000001) {
            s_step[i_e_d + 1].set(add_sen);
            return 1;
        }
        return -500;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_10(Point p0) {
        mMoved_A_29(p0);
    }//近い既存点のみ表示

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_10(Point p0) {

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) < d_decision_width) {
            i_drawing_stage = i_drawing_stage + 1;
            s_step[i_drawing_stage].set(closest_point, closest_point);
            s_step[i_drawing_stage].setColor(icol);
        }
    }

//--------------------------------------------
//29 29 29 29 29 29 29 29  i_mouse_modeA==29正多角形入力	入力 29 29 29 29 29 29 29 29
    //動作概要　
    //i_mouse_modeA==1と線分分割以外は同じ　
    //

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_10(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_10(Point p0) {
        if (i_drawing_stage == 3) {
            i_drawing_stage = 0;

            //２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){
            Point t_taisyou = new Point();
            t_taisyou.set(OritaCalc.lineSymmetry_point_find(s_step[2].getA(), s_step[3].getA(), s_step[1].getA()));

            LineSegment add_sen = new LineSegment(s_step[2].getA(), t_taisyou);

            add_sen.set(extendToIntersectionPoint(add_sen));
            add_sen.setColor(icol);
            if (add_sen.getLength() > 0.00000001) {
                addLineSegment(add_sen);
                record();
            }
        }
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_52(Point p0) {
        mMoved_A_29(p0);
    }//近い既存点のみ表示

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_52(Point p0) {
        System.out.println("i_egaki_dankai=" + i_drawing_stage);

        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));

        i_drawing_stage = i_drawing_stage + 1;
        if (p.distance(closest_point) < d_decision_width) {
            s_step[i_drawing_stage].set(closest_point, closest_point);
            s_step[i_drawing_stage].setColor(icol);
        } else {
            s_step[i_drawing_stage].set(p, p);
            s_step[i_drawing_stage].setColor(icol);
        }

        System.out.println("i_egaki_dankai=" + i_drawing_stage);
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_52(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_52(Point p0) {
        if (i_drawing_stage == 2) {
            i_drawing_stage = 0;

            LineSegment add_lineSegment = new LineSegment();
            renzoku_orikaesi_new(s_step[1].getA(), s_step[2].getA());
            for (int i = 1; i <= i_drawing_stage; i++) {

                if (s_step[i].getLength() > 0.00000001) {

                    add_lineSegment.set(s_step[i].getA(), s_step[i].getB());//要注意　s_stepは表示上の都合でアクティヴが0以外に設定されているのでadd_senbunにうつしかえてる20170507
                    add_lineSegment.setColor(icol);
                    addLineSegment(add_lineSegment);
                }
            }
            record();

            i_drawing_stage = 0;
        }
    }

    // ------------------------------------------------------------
    public void renzoku_orikaesi_new(Point a, Point b) {//連続折り返しの改良版。
        orihime_app.repaint();

        //ベクトルab(=s0)を点aからb方向に、最初に他の折線(直線に含まれる線分は無視。)と交差するところまで延長する

        //与えられたベクトルabを延長して、それと重ならない折線との、最も近い交点までs_stepとする。
        //補助活線は無視する
        //与えられたベクトルabを延長して、それと重ならない折線との、最も近い交点までs_stepとする


        //「再帰関数における、種の発芽」交点がない場合「種」が成長せずリターン。

        e_s_dougubako.kousaten_made_nobasi_keisan_fukumu_senbun_musi_new(a, b);//一番近い交差点を見つけて各種情報を記録
        if (e_s_dougubako.get_kousaten_made_nobasi_flg_new(a, b) == StraightLine.Intersection.NONE_0) {
            return;
        }

        //「再帰関数における、種の成長」交点が見つかった場合、交点まで伸びる線分をs_step[i_egaki_dankai]に追加
        //if(e_s_dougubako.get_kousaten_made_nobasi_orisen_fukumu_flg(a,b)==3){return;}
        i_drawing_stage = i_drawing_stage + 1;
        if (i_drawing_stage > 100) {
            return;
        }//念のためにs_stepの上限を100に設定した

        s_step[i_drawing_stage].set(e_s_dougubako.get_kousaten_made_nobasi_senbun_new());//要注意　es1でうっかりs_stepにset.(senbun)やるとアクティヴでないので表示が小さくなる20170507
        s_step[i_drawing_stage].setActive(3);

        System.out.println("20201129 saiki repaint ");

        //「再帰関数における、種の生成」求めた最も近い交点から次のベクトル（＝次の再帰関数に渡す「種」）を発生する。最も近い交点が折線とＸ字型に交差している点か頂点かで、種のでき方が異なる。

        //最も近い交点が折線とＸ字型の場合無条件に種を生成し、散布。
        if (e_s_dougubako.get_kousaten_made_nobasi_flg_new(a, b) == StraightLine.Intersection.INTERSECT_X_1) {
            LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment();
            kousaten_made_nobasi_saisyono_lineSegment.set(e_s_dougubako.get_kousaten_made_nobasi_saisyono_senbun_new());

            Point new_a = new Point();
            new_a.set(e_s_dougubako.get_kousaten_made_nobasi_ten_new());//Ten new_aは最も近い交点
            Point new_b = new Point();
            new_b.set(OritaCalc.lineSymmetry_point_find(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

            renzoku_orikaesi_new(new_a, new_b);//種の散布
            return;
        }

        //最も近い交点が頂点（折線端末）の場合、頂点に集まる折線の数で条件分けして、種を生成し散布、
        if ((e_s_dougubako.get_kousaten_made_nobasi_flg_new(a, b) == StraightLine.Intersection.INTERSECT_T_A_21)
                || (e_s_dougubako.get_kousaten_made_nobasi_flg_new(a, b) == StraightLine.Intersection.INTERSECT_T_B_22)) {//System.out.println("20201129 21 or 22");

            StraightLine tyoku1 = new StraightLine(a, b);
            StraightLine.Intersection i_kousa_flg;

            SortingBox_int_double t_m_s_nbox = new SortingBox_int_double();

            t_m_s_nbox.set(ori_s.get_nbox_of_tyouten_b_syuui_orisen(e_s_dougubako.get_kousaten_made_nobasi_senbun_new().getA(), e_s_dougubako.get_kousaten_made_nobasi_senbun_new().getB()));

            //System.out.println("20201129 t_m_s_nbox.getsousuu() = "+ t_m_s_nbox.getsousuu());


            if (t_m_s_nbox.getTotal() == 2) {

                //i_kousa_flg=
                //0=この直線は与えられた線分と交差しない、
                //1=X型で交差する、
                //21=線分のa点でT型で交差する、
                //22=線分のb点でT型で交差する、
                //3=線分は直線に含まれる。
                i_kousa_flg = tyoku1.lineSegment_intersect_reverse_detail(ori_s.get(t_m_s_nbox.getInt(1)));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (i_kousa_flg == StraightLine.Intersection.INCLUDED_3) {
                    return;
                }

                i_kousa_flg = tyoku1.lineSegment_intersect_reverse_detail(ori_s.get(t_m_s_nbox.getInt(2)));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (i_kousa_flg == StraightLine.Intersection.INCLUDED_3) {
                    return;
                }

                StraightLine tyoku2 = new StraightLine(ori_s.get(t_m_s_nbox.getInt(1)));
                i_kousa_flg = tyoku2.lineSegment_intersect_reverse_detail(ori_s.get(t_m_s_nbox.getInt(2)));
                if (i_kousa_flg == StraightLine.Intersection.INCLUDED_3) {
                    LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment();
                    kousaten_made_nobasi_saisyono_lineSegment.set(e_s_dougubako.get_kousaten_made_nobasi_saisyono_senbun_new());

                    Point new_a = new Point();
                    new_a.set(e_s_dougubako.get_kousaten_made_nobasi_ten_new());//Ten new_aは最も近い交点
                    Point new_b = new Point();
                    new_b.set(OritaCalc.lineSymmetry_point_find(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

                    renzoku_orikaesi_new(new_a, new_b);//種の散布
                    return;
                }
                return;
            }


            if (t_m_s_nbox.getTotal() == 3) {

                i_kousa_flg = tyoku1.lineSegment_intersect_reverse_detail(ori_s.get(t_m_s_nbox.getInt(1)));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (i_kousa_flg == StraightLine.Intersection.INCLUDED_3) {
                    StraightLine tyoku2 = new StraightLine(ori_s.get(t_m_s_nbox.getInt(2)));
                    i_kousa_flg = tyoku2.lineSegment_intersect_reverse_detail(ori_s.get(t_m_s_nbox.getInt(3)));
                    if (i_kousa_flg == StraightLine.Intersection.INCLUDED_3) {
                        LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment();
                        kousaten_made_nobasi_saisyono_lineSegment.set(e_s_dougubako.get_kousaten_made_nobasi_saisyono_senbun_new());

                        Point new_a = new Point();
                        new_a.set(e_s_dougubako.get_kousaten_made_nobasi_ten_new());//Ten new_aは最も近い交点
                        Point new_b = new Point();
                        new_b.set(OritaCalc.lineSymmetry_point_find(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

                        renzoku_orikaesi_new(new_a, new_b);//種の散布
                        return;
                    }
                }
                //------------------------------------------------
                i_kousa_flg = tyoku1.lineSegment_intersect_reverse_detail(ori_s.get(t_m_s_nbox.getInt(2)));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (i_kousa_flg == StraightLine.Intersection.INCLUDED_3) {
                    StraightLine tyoku2 = new StraightLine(ori_s.get(t_m_s_nbox.getInt(3)));
                    i_kousa_flg = tyoku2.lineSegment_intersect_reverse_detail(ori_s.get(t_m_s_nbox.getInt(1)));
                    if (i_kousa_flg == StraightLine.Intersection.INCLUDED_3) {
                        LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment();
                        kousaten_made_nobasi_saisyono_lineSegment.set(e_s_dougubako.get_kousaten_made_nobasi_saisyono_senbun_new());

                        Point new_a = new Point();
                        new_a.set(e_s_dougubako.get_kousaten_made_nobasi_ten_new());//Ten new_aは最も近い交点
                        Point new_b = new Point();
                        new_b.set(OritaCalc.lineSymmetry_point_find(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

                        renzoku_orikaesi_new(new_a, new_b);//種の散布
                        return;
                    }
                }
                //------------------------------------------------
                i_kousa_flg = tyoku1.lineSegment_intersect_reverse_detail(ori_s.get(t_m_s_nbox.getInt(3)));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (i_kousa_flg == StraightLine.Intersection.INCLUDED_3) {
                    StraightLine tyoku2 = new StraightLine(ori_s.get(t_m_s_nbox.getInt(1)));
                    i_kousa_flg = tyoku2.lineSegment_intersect_reverse_detail(ori_s.get(t_m_s_nbox.getInt(2)));
                    if (i_kousa_flg == StraightLine.Intersection.INCLUDED_3) {
                        LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment();
                        kousaten_made_nobasi_saisyono_lineSegment.set(e_s_dougubako.get_kousaten_made_nobasi_saisyono_senbun_new());

                        Point new_a = new Point();
                        new_a.set(e_s_dougubako.get_kousaten_made_nobasi_ten_new());//Ten new_aは最も近い交点
                        Point new_b = new Point();
                        new_b.set(OritaCalc.lineSymmetry_point_find(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

                        renzoku_orikaesi_new(new_a, new_b);//種の散布
                    }
                }


            }
        }
    }

    public void renzoku_orikaesi_1_kaime(Point a, Point b) {//連続折り返しの1回目だけここを実施する。連続折り返しの2回目以降はただのrenzoku_orikaesi関数で行う。


        //与えられたベクトルabを延長して、それと重ならない折線との、最も近い交点までs_stepとする
        if (e_s_dougubako.get_kousaten_made_nobasi_flg(a, b) == StraightLine.Intersection.NONE_0) {
            return;
        }

        i_drawing_stage = i_drawing_stage + 1;
        if (i_drawing_stage > 100) {
            return;
        }//念のためにs_stepの上限を100に設定した

        s_step[i_drawing_stage].set(e_s_dougubako.get_kousaten_made_nobasi_senbun(a, b));//要注意　es1でうっかりs_stepにset.(senbun)やるとアクティヴでないので表示が小さくなる20170507
        s_step[i_drawing_stage].setActive(3);

        //求めた交点で、次のベクトルを発生する。

        if (e_s_dougubako.get_kousaten_made_nobasi_flg(a, b) == StraightLine.Intersection.INTERSECT_X_1) {
            LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment();
            kousaten_made_nobasi_saisyono_lineSegment.set(e_s_dougubako.get_kousaten_made_nobasi_saisyono_senbun(a, b));

            Point new_a = new Point();
            new_a.set(e_s_dougubako.get_kousaten_made_nobasi_ten(a, b));
            Point new_b = new Point();
            new_b.set(OritaCalc.lineSymmetry_point_find(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

            renzoku_orikaesi(new_a, new_b);
        }
    }

    public void renzoku_orikaesi(Point a, Point b) {

        //与えられたベクトルabを延長して、それと重ならない折線との、最も近い交点までs_stepとする
        if (e_s_dougubako.get_kousaten_made_nobasi_flg(a, b) == StraightLine.Intersection.NONE_0) {
            return;
        }
        //if(e_s_dougubako.get_kousaten_made_nobasi_orisen_fukumu_flg(a,b)==3){return;}

        i_drawing_stage = i_drawing_stage + 1;
        if (i_drawing_stage > 100) {
            return;
        }//念のためにs_stepの上限を100に設定した

        s_step[i_drawing_stage].set(e_s_dougubako.get_kousaten_made_nobasi_senbun(a, b));//要注意　es1でうっかりs_stepにset.(senbun)やるとアクティヴでないので表示が小さくなる20170507
        s_step[i_drawing_stage].setActive(3);

        //求めた交点で、次のベクトルを発生する。

        if (e_s_dougubako.get_kousaten_made_nobasi_flg(a, b) == StraightLine.Intersection.INTERSECT_X_1) {
            LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment();
            kousaten_made_nobasi_saisyono_lineSegment.set(e_s_dougubako.get_kousaten_made_nobasi_saisyono_senbun(a, b));

            Point new_a = new Point();
            new_a.set(e_s_dougubako.get_kousaten_made_nobasi_ten(a, b));
            Point new_b = new Point();
            new_b.set(OritaCalc.lineSymmetry_point_find(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

            renzoku_orikaesi(new_a, new_b);
        }
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_27(Point p0) {
        mMoved_m_00a(p0, icol);//マウスで選択できる候補点を表示する。近くに既成の点があるときはその点、無いときはマウスの位置自身が候補点となる。
    }


// 19 19 19 19 19 19 19 19 19 select 選択

    //マウス操作(i_mouse_modeA==27線分入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_27(Point p0) {
        i_drawing_stage = 1;
        s_step[1].setActive(2);
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) < d_decision_width) {
            s_step[1].set(p, closest_point);
            s_step[1].setColor(icol);
            return;
        }
        s_step[1].set(p, p);
        s_step[1].setColor(icol);
    }

    //マウス操作(i_mouse_modeA==27線分入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_27(Point p0) {
        p.set(camera.TV2object(p0));
        s_step[1].setA(p);
        if (i_kou_mitudo_nyuuryoku) {
            i_candidate_stage = 0;
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) < d_decision_width) {
                i_candidate_stage = 1;
                s_kouho[1].set(closest_point, closest_point);
                s_kouho[1].setColor(icol);
                s_step[1].setA(s_kouho[1].getA());
            }
        }


    }

    //マウス操作(i_mouse_modeA==27線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_27(Point p0) {
        i_drawing_stage = 0;
        p.set(camera.TV2object(p0));

        s_step[1].setA(p);
        closest_point.set(getClosestPoint(p));

        if (p.distance(closest_point) <= d_decision_width) {
            s_step[1].setA(closest_point);
        }
        if (s_step[1].getLength() > 0.00000001) {
            for (int i = 0; i <= foldLineDividingNumber - 1; i++) {
                double ax = ((double) (foldLineDividingNumber - i) * s_step[1].getAX() + (double) i * s_step[1].getBX()) / ((double) foldLineDividingNumber);
                double ay = ((double) (foldLineDividingNumber - i) * s_step[1].getAY() + (double) i * s_step[1].getBY()) / ((double) foldLineDividingNumber);
                double bx = ((double) (foldLineDividingNumber - i - 1) * s_step[1].getAX() + (double) (i + 1) * s_step[1].getBX()) / ((double) foldLineDividingNumber);
                double by = ((double) (foldLineDividingNumber - i - 1) * s_step[1].getAY() + (double) (i + 1) * s_step[1].getBY()) / ((double) foldLineDividingNumber);
                LineSegment s_ad = new LineSegment(ax, ay, bx, by);
                s_ad.setColor(icol);
                addLineSegment(s_ad);
            }
            record();
        }

    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_29(Point p0) {
        if (i_kou_mitudo_nyuuryoku) {
            s_kouho[1].setActive(3);
            i_candidate_stage = 0;
            p.set(camera.TV2object(p0));
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) < d_decision_width) {
                i_candidate_stage = 1;
                s_kouho[1].set(closest_point, closest_point);
                s_kouho[1].setColor(icol);
            }
        }
    }

    //マウス操作(i_mouse_modeA==29正多角形入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_29(Point p0) {
        s_step[1].setActive(3);

        p.set(camera.TV2object(p0));

        if (i_drawing_stage == 0) {    //第1段階として、点を選択
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) < d_decision_width) {
                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_point, closest_point);
                s_step[i_drawing_stage].setColor(LineColor.MAGENTA_5);
            }
            return;
        }

        if (i_drawing_stage == 1) {    //第2段階として、点を選択
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) >= d_decision_width) {
                i_drawing_stage = 0;
                return;
            }
            if (p.distance(closest_point) < d_decision_width) {

                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_point, closest_point);
                s_step[i_drawing_stage].setColor(LineColor.fromNumber(i_drawing_stage));
                s_step[1].setB(s_step[2].getB());
            }
            if (s_step[1].getLength() < 0.00000001) {
                i_drawing_stage = 0;
            }
        }


    }
//------------------------------------------------------------

    //マウス操作(i_mouse_modeA==29正多角形入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_29(Point p0) {
    }

    //マウス操作(i_mouse_modeA==29正多角形入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_29(Point p0) {
        if (i_drawing_stage == 2) {
            i_drawing_stage = 0;
            LineSegment s_tane = new LineSegment();
            LineSegment s_deki = new LineSegment();


            s_tane.set(s_step[1]);
            s_tane.setColor(icol);
            addLineSegment(s_tane);
            for (int i = 2; i <= numPolygonCorners; i++) {
                s_deki.set(OritaCalc.lineSegment_rotate(s_tane, (double) (numPolygonCorners - 2) * 180.0 / (double) numPolygonCorners));
                s_tane.set(s_deki.getB(), s_deki.getA());
                s_tane.setColor(icol);
                addLineSegment(s_tane);

            }
            ori_s.unselect_all();
            record();
        }
    }

    //37 37 37 37 37 37 37 37 37 37 37;角度規格化
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_37(Point p0) {
        mMoved_A_29(p0);
    }//近い既存点のみ表示

    //マウス操作(i_mouse_modeA==37　でボタンを押したとき)時の作業-------//System.out.println("A");---------------------------------------------
    public void mPressed_A_37(Point p0) {
        s_step[1].setActive(2);
        i_drawing_stage = 1;

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) > d_decision_width) {
            i_drawing_stage = 0;
        }
        s_step[1].set(p, closest_point);
        s_step[1].setColor(icol);

        s_step[2].set(s_step[1]);//ここではs_step[2]は表示されない、計算用の線分
    }


//------------------------------------------------------------

    //マウス操作(i_mouse_modeA==37　でドラッグしたとき)を行う関数--------------//System.out.println("A");--------------------------------------
    public void mDragged_A_37(Point p0) {
        Point syuusei_point = new Point(syuusei_ten_A_37(p0));
        s_step[1].setA(syuusei_point);

        if (i_kou_mitudo_nyuuryoku) {
            i_candidate_stage = 1;
            s_kouho[1].set(kouho_ten_A_37(syuusei_point), kouho_ten_A_37(syuusei_point));
            s_kouho[1].setColor(icol);
            s_step[1].setA(kouho_ten_A_37(syuusei_point));
        }

    }


//------------------------------------------------------------
// 19 19 19 19 19 19 19 19 19 select 選択

    //マウス操作(i_mouse_modeA==37　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_37(Point p0) {
        if (i_drawing_stage == 1) {
            i_drawing_stage = 0;
            Point syuusei_point = new Point(syuusei_ten_A_37(p0));
            s_step[1].setA(kouho_ten_A_37(syuusei_point));
            if (s_step[1].getLength() > 0.00000001) {
                addLineSegment(s_step[1]);
                record();
            }
        }
    }

    // ---
    public Point syuusei_ten_A_37(Point p0) {

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));

        Point syuusei_point = new Point();
        double d_rad = 0.0;
        s_step[2].setA(p);

        if (id_kakudo_kei != 0) {
            d_kakudo_kei = 180.0 / (double) id_kakudo_kei;
            d_rad = (Math.PI / 180) * d_kakudo_kei * (int) Math.round(OritaCalc.angle(s_step[2]) / d_kakudo_kei);
        } else {
            double[] jk = new double[7];
            jk[0] = OritaCalc.angle(s_step[2]);//マウスで入力した線分がX軸となす角度
            jk[1] = d_jiyuu_kaku_1 - 180.0;
            jk[2] = d_jiyuu_kaku_2 - 180.0;
            jk[3] = d_jiyuu_kaku_3 - 180.0;
            jk[4] = 360.0 - d_jiyuu_kaku_1 - 180.0;
            jk[5] = 360.0 - d_jiyuu_kaku_2 - 180.0;
            jk[6] = 360.0 - d_jiyuu_kaku_3 - 180.0;

            double d_kakudo_sa_min = 1000.0;
            for (int i = 1; i <= 6; i++) {
                if (Math.min(OritaCalc.angle_between_0_360(jk[i] - jk[0]), OritaCalc.angle_between_0_360(jk[0] - jk[i])) < d_kakudo_sa_min) {
                    d_kakudo_sa_min = Math.min(OritaCalc.angle_between_0_360(jk[i] - jk[0]), OritaCalc.angle_between_0_360(jk[0] - jk[i]));
                    d_rad = (Math.PI / 180) * jk[i];
                }
            }
        }

        syuusei_point.set(OritaCalc.shadow_request(s_step[2].getB(), new Point(s_step[2].getBX() + Math.cos(d_rad), s_step[2].getBY() + Math.sin(d_rad)), p));
        return syuusei_point;
    }

    // ---
    public Point kouho_ten_A_37(Point syuusei_point) {
        closest_point.set(getClosestPoint(syuusei_point));
        double zure_kakudo = OritaCalc.angle(s_step[2].getB(), syuusei_point, s_step[2].getB(), closest_point);
        int zure_flg = 0;
        if ((0.00001 < zure_kakudo) && (zure_kakudo <= 359.99999)) {
            zure_flg = 1;
        }
        if ((zure_flg == 0) && (syuusei_point.distance(closest_point) <= d_decision_width)) {//最寄点が角度系にのっていて、修正点とも近い場合
            return closest_point;
        }
        return syuusei_point;
    }

    //------------------------------------------------------------
    public void mPressed_A_box_select(Point p0) {
        p19_1.set(p0);

        i_drawing_stage = 0;

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));

        s_step[1].set(p, p);
        s_step[1].setColor(LineColor.MAGENTA_5);
        s_step[2].set(p, p);
        s_step[2].setColor(LineColor.MAGENTA_5);
        s_step[3].set(p, p);
        s_step[3].setColor(LineColor.MAGENTA_5);
        s_step[4].set(p, p);
        s_step[4].setColor(LineColor.MAGENTA_5);

    }


//------------------------------------------------------------

    public void mDragged_A_box_select(Point p0) {
        p19_2.set(p19_1.getX(), p0.getY());
        p19_4.set(p0.getX(), p19_1.getY());

        p19_a.set(camera.TV2object(p19_1));
        p19_b.set(camera.TV2object(p19_2));
        p19_c.set(camera.TV2object(p0));
        p19_d.set(camera.TV2object(p19_4));

        s_step[1].set(p19_a, p19_b);
        s_step[2].set(p19_b, p19_c);
        s_step[3].set(p19_c, p19_d);
        s_step[4].set(p19_d, p19_a);

        i_drawing_stage = 4;//s_step[4]まで描画するために、この行が必要

    }

//20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20

    //マウス操作(i_mouse_modeA==19  select　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_19(Point p0) {
        System.out.println("19  select_");
        System.out.println("i_egaki_dankai=" + i_drawing_stage);

        if (i_drawing_stage == 0) {//i_select_modeを決める
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));

/* 20200930 以下除外　折線をセレクト後格子点を選択するとすぐ作業になる仕様のための部分だが、セレクトが分かりにくくなるので取りやめ
			moyori_ten.set(get_moyori_ten_orisen_en(p));//この最寄点は格子点は対象としない
			if(p.kyori(moyori_ten)<d_decision_width     ){
				i_select_mode=0;
				if(ori_s.tyouten_syuui_sensuu_select(p,0.0001)>0){
					i_select_mode=orihime_ap.i_sel_mou_mode;//=1はmove、=2はmove4p、=3はcopy、=4はcopy4p、=5は鏡映像
				}
			}
*/
        }


        if (i_select_mode == 0) {
            mPressed_A_box_select(p0);
        } else if (i_select_mode == 1) {
            mPressed_A_21(p0);//move
        } else if (i_select_mode == 2) {
            mPressed_A_31(p0);//move 2p2p
        } else if (i_select_mode == 3) {
            mPressed_A_22(p0);//copy
        } else if (i_select_mode == 4) {
            mPressed_A_32(p0);//copy 2p2p
        } else if (i_select_mode == 5) {
            mPressed_A_12(p0);//鏡映
        }
    }

    //マウス操作(i_mouse_modeA==19 select　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_19(Point p0) {
        //mDragged_A_box_select( p0);
        if (i_select_mode == 0) {
            mDragged_A_box_select(p0);
        } else if (i_select_mode == 1) {
            mDragged_A_21(p0);//move
        } else if (i_select_mode == 2) {
            mDragged_A_31(p0);//move 2p2p
        } else if (i_select_mode == 3) {
            mDragged_A_22(p0);//copy
        } else if (i_select_mode == 4) {
            mDragged_A_32(p0);//copy 2p2p
        } else if (i_select_mode == 5) {
            mDragged_A_12(p0);//鏡映
        }


    }

    //マウス操作(i_mouse_modeA==19 select　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_19(Point p0) {
        //mReleased_A_box_select(p0);
        if (i_select_mode == 0) {
            mReleased_A_box_select(p0);
        } else if (i_select_mode == 1) {
            mReleased_A_21(p0);//move
        } else if (i_select_mode == 2) {
            mReleased_A_31(p0);//move 2p2p
        } else if (i_select_mode == 3) {
            mReleased_A_22(p0);//copy
        } else if (i_select_mode == 4) {
            mReleased_A_32(p0);//copy 2p2p
        } else if (i_select_mode == 5) {
            mReleased_A_12(p0);//鏡映
        }


    }

    public void mReleased_A_box_select(Point p0) {
        i_drawing_stage = 0;

        select(p19_1, p0);
        if (p19_1.distance(p0) <= 0.000001) {
            p.set(camera.TV2object(p0));
            if (ori_s.closestLineSegmentDistance(p) < d_decision_width) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                ori_s.select(ori_s.closestLineSegmentSearch(p));
            }
        }

    }

    //マウス操作(i_mouse_modeA==19  select　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_20(Point p0) {
        mPressed_A_box_select(p0);
    }

    //マウス操作(i_mouse_modeA==19 select　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_20(Point p0) {
        mDragged_A_box_select(p0);
    }

    //マウス操作(i_mouse_modeA==20 select　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_20(Point p0) {

        i_drawing_stage = 0;
        unselect(p19_1, p0);

        if (p19_1.distance(p0) <= 0.000001) {
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            if (ori_s.closestLineSegmentDistance(p) < d_decision_width) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                ori_s.unselect(ori_s.closestLineSegmentSearch(p));
            }
        }


    }

    //---------------------
    public int get_i_egaki_dankai() {
        return i_drawing_stage;
    }


//61 61 61 61 61 61 61 61 61 61 61 61 i_mouse_modeA==61//長方形内選択（paintの選択に似せた選択機能）に使う
    //動作概要　
    //マウスボタン押されたとき　
    //用紙1/1分割時 		折線の端点のみが基準点。格子点が基準点になることはない。
    //用紙1/2から1/512分割時	折線の端点と用紙枠内（-200.0,-200.0 _ 200.0,200.0)）の格子点とが基準点
    //入力点Pが基準点から格子幅kus.d_haba()の1/4より遠いときは折線集合への入力なし
    //線分が長さがなく1点状のときは折線集合への入力なし

    public void set_i_egaki_dankai(int i) {
        i_drawing_stage = i;
    }

    //---------------------
    public void select_all() {
        ori_s.select_all();
    }

    public void unselect_all() {
        ori_s.unselect_all();
    }

    public void select(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();


        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();


        p0_a.set(p0a.getX(), p0a.getY());
        p0_b.set(p0a.getX(), p0b.getY());
        p0_c.set(p0b.getX(), p0b.getY());
        p0_d.set(p0b.getX(), p0a.getY());


        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));


        ori_s.select(p_a, p_b, p_c, p_d);
    }


//22222222222222222222222222222222222222222222222222222222222222 展開図移動


    //public void mPressed_A_02(Ten p0) {	}//マウス操作(i_mouse_modeA==2　展開図移動でボタンを押したとき)時の作業
    //public void mDragged_A_02(Ten p0) {	}//マウス操作(i_mouse_modeA==2　展開図移動でドラッグしたとき)を行う関数
    //public void mReleased_A_02(Ten p0){	}//マウス操作(i_mouse_modeA==2　展開図移動でボタンを離したとき)を行う関数

    //--------------------
    public void unselect(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getX(), p0a.getY());
        p0_b.set(p0a.getX(), p0b.getY());
        p0_c.set(p0b.getX(), p0b.getY());
        p0_d.set(p0b.getX(), p0a.getY());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        ori_s.unselect(p_a, p_b, p_c, p_d);
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_61(Point p0) {
        if (i_kou_mitudo_nyuuryoku) {
            s_kouho[1].setActive(3);

            p.set(camera.TV2object(p0));
            i_candidate_stage = 1;
            closest_point.set(getClosestPoint(p));

            if (p.distance(closest_point) < d_decision_width) {
                s_kouho[1].set(closest_point, closest_point);
            } else {
                s_kouho[1].set(p, p);
            }

            //s_kouho[1].setcolor(icol);
            s_kouho[1].setColor(LineColor.GREEN_6);
        }
    }

    //マウス操作(i_mouse_modeA==61　長方形内選択でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_61(Point p0) {
        p.set(camera.TV2object(p0));
        Point p_new = new Point();
        Point p_ob1 = new Point();
        p_ob1.set(camera.TV2object(operationFrame_p1));
        Point p_ob2 = new Point();
        p_ob2.set(camera.TV2object(operationFrame_p2));
        Point p_ob3 = new Point();
        p_ob3.set(camera.TV2object(operationFrame_p3));
        Point p_ob4 = new Point();
        p_ob4.set(camera.TV2object(operationFrame_p4));

        double kyori_min = 100000.0;

        operationFrameMode = OperationFrameMode.NONE_0;
        if (i_drawing_stage == 0) {
            operationFrameMode = OperationFrameMode.CREATE_1;
        }
        if (i_drawing_stage == 4) {
            if (operationFrameBox.inside(p0) == 0) {
                operationFrameMode = OperationFrameMode.CREATE_1;
            }
            if (operationFrameBox.inside(p0) > 0) {
                operationFrameMode = OperationFrameMode.MOVE_BOX_4;
            }


            kyori_min = OritaCalc.min(OritaCalc.distance_lineSegment(p, p_ob1, p_ob2), OritaCalc.distance_lineSegment(p, p_ob2, p_ob3), OritaCalc.distance_lineSegment(p, p_ob3, p_ob4), OritaCalc.distance_lineSegment(p, p_ob4, p_ob1));
            if (kyori_min < d_decision_width) {
                operationFrameMode = OperationFrameMode.MOVE_SIDES_3;
            }


            if (p.distance(p_ob1) < d_decision_width) {
                p_new.set(operationFrame_p1);
                operationFrame_p1.set(operationFrame_p3);
                operationFrame_p3.set(p_new);
                operationFrameMode = OperationFrameMode.MOVE_POINTS_2;
            }
            if (p.distance(p_ob2) < d_decision_width) {
                p_new.set(operationFrame_p2);
                operationFrame_p2.set(operationFrame_p1);
                operationFrame_p1.set(operationFrame_p4);
                operationFrame_p4.set(operationFrame_p3);
                operationFrame_p3.set(p_new);
                operationFrameMode = OperationFrameMode.MOVE_POINTS_2;
            }
            if (p.distance(p_ob3) < d_decision_width) {
                p_new.set(operationFrame_p3);
                operationFrame_p1.set(operationFrame_p1);
                operationFrame_p3.set(p_new);
                operationFrameMode = OperationFrameMode.MOVE_POINTS_2;
            }
            if (p.distance(p_ob4) < d_decision_width) {
                p_new.set(operationFrame_p4);
                operationFrame_p4.set(operationFrame_p1);
                operationFrame_p1.set(operationFrame_p2);
                operationFrame_p2.set(operationFrame_p3);
                operationFrame_p3.set(p_new);
                operationFrameMode = OperationFrameMode.MOVE_POINTS_2;
            }

        }


        if (operationFrameMode == OperationFrameMode.MOVE_SIDES_3) {
            while (OritaCalc.distance_lineSegment(p, p_ob1, p_ob2) != kyori_min) {
                p_new.set(operationFrame_p1);
                operationFrame_p1.set(operationFrame_p2);
                operationFrame_p2.set(operationFrame_p3);
                operationFrame_p3.set(operationFrame_p4);
                operationFrame_p4.set(p_new);
                p_new.set(p_ob1);
                p_ob1.set(p_ob2);
                p_ob2.set(p_ob3);
                p_ob3.set(p_ob4);
                p_ob4.set(p_new);
            }

        }

        if (operationFrameMode == OperationFrameMode.CREATE_1) {
            i_drawing_stage = 4;

            p_new.set(p);

            closest_point.set(getClosestPoint(p));

            if (p.distance(closest_point) < d_decision_width) {
                p_new.set(closest_point);

            }

            operationFrame_p1.set(camera.object2TV(p_new));
            operationFrame_p2.set(camera.object2TV(p_new));
            operationFrame_p3.set(camera.object2TV(p_new));
            operationFrame_p4.set(camera.object2TV(p_new));
        }
    }

//--------------------

    //マウス操作(i_mouse_modeA==61　長方形内選択でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_61(Point p0) {

        p.set(camera.TV2object(p0));
        if (operationFrameMode == OperationFrameMode.MOVE_POINTS_2) {
            operationFrameMode = OperationFrameMode.CREATE_1;
        }

        Point p_new = new Point();

        if (!i_kou_mitudo_nyuuryoku) {
            p_new.set(p);
        }

        if (i_kou_mitudo_nyuuryoku) {
            closest_point.set(getClosestPoint(p));
            i_candidate_stage = 1;
            if (p.distance(closest_point) < d_decision_width) {
                s_kouho[1].set(closest_point, closest_point);
            } else {
                s_kouho[1].set(p, p);
            }
            s_kouho[1].setColor(LineColor.GREEN_6);

            p_new.set(s_kouho[1].getA());
        }


        if (operationFrameMode == OperationFrameMode.MOVE_SIDES_3) {
            if (
                    (operationFrame_p1.getX() - operationFrame_p2.getX()) * (operationFrame_p1.getX() - operationFrame_p2.getX())
                            <
                            (operationFrame_p1.getY() - operationFrame_p2.getY()) * (operationFrame_p1.getY() - operationFrame_p2.getY())
            ) {
                operationFrame_p1.setX(camera.object2TV(p_new).getX());
                operationFrame_p2.setX(camera.object2TV(p_new).getX());
            }

            if (
                    (operationFrame_p1.getX() - operationFrame_p2.getX()) * (operationFrame_p1.getX() - operationFrame_p2.getX())
                            >
                            (operationFrame_p1.getY() - operationFrame_p2.getY()) * (operationFrame_p1.getY() - operationFrame_p2.getY())
            ) {
                operationFrame_p1.setY(camera.object2TV(p_new).getY());
                operationFrame_p2.setY(camera.object2TV(p_new).getY());
            }

        }


        if (operationFrameMode == OperationFrameMode.CREATE_1) {
            operationFrame_p3.set(camera.object2TV(p_new));
            operationFrame_p2.set(operationFrame_p1.getX(), operationFrame_p3.getY());
            operationFrame_p4.set(operationFrame_p3.getX(), operationFrame_p1.getY());
        }
    }
//--------------------

    //マウス操作(i_mouse_modeA==61 長方形内選択　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_61(Point p0) {

        p.set(camera.TV2object(p0));

        Point p_new = new Point();
        p_new.set(p);

        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) <= d_decision_width) {
            p_new.set(closest_point);/*s_step[1].seta(moyori_ten);*/
        }

        if (operationFrameMode == OperationFrameMode.MOVE_SIDES_3) {
            if (
                    (operationFrame_p1.getX() - operationFrame_p2.getX()) * (operationFrame_p1.getX() - operationFrame_p2.getX())
                            <
                            (operationFrame_p1.getY() - operationFrame_p2.getY()) * (operationFrame_p1.getY() - operationFrame_p2.getY())
            ) {
                operationFrame_p1.setX(camera.object2TV(p_new).getX());
                operationFrame_p2.setX(camera.object2TV(p_new).getX());
            }

            if (
                    (operationFrame_p1.getX() - operationFrame_p2.getX()) * (operationFrame_p1.getX() - operationFrame_p2.getX())
                            >
                            (operationFrame_p1.getY() - operationFrame_p2.getY()) * (operationFrame_p1.getY() - operationFrame_p2.getY())
            ) {
                operationFrame_p1.setY(camera.object2TV(p_new).getY());
                operationFrame_p2.setY(camera.object2TV(p_new).getY());
            }

        }

        if (operationFrameMode == OperationFrameMode.CREATE_1) {
            operationFrame_p3.set(camera.object2TV(p_new));
            operationFrame_p2.set(operationFrame_p1.getX(), operationFrame_p3.getY());
            operationFrame_p4.set(operationFrame_p3.getX(), operationFrame_p1.getY());
        }

        operationFrameBox.set(1, operationFrame_p1);
        operationFrameBox.set(2, operationFrame_p2);
        operationFrameBox.set(3, operationFrame_p3);
        operationFrameBox.set(4, operationFrame_p4);

        if (operationFrameBox.calculateArea() * operationFrameBox.calculateArea() < 1.0) {
            i_drawing_stage = 0;
        }
    }
//--------------------

    //3 3 3 3 3 33333333333333333333333333333333333333333333333333333333
    //マウス操作(i_mouse_modeA==3,23 "線分削除" でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_03(Point p0) {
        //System.out.println("(1)zzzzz ori_s.check4_size() = "+ori_s.check4_size());
        if (i_foldLine_additional == FoldLineAdditionalInputMode.POLY_LINE_0) {
            mPressed_A_box_select(p0);
        }//折線の削除
        if (i_foldLine_additional == FoldLineAdditionalInputMode.BLACK_LINE_2) {
            mPressed_A_box_select(p0);
        }//黒の折線
        if (i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LIVE_LINE_3) {
            mPressed_A_box_select(p0);
        }//補助活線

        if (i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LINE_1) {
            mPressed_A_box_select(p0);
        }//補助絵線

        if (i_foldLine_additional == FoldLineAdditionalInputMode.BOTH_4) {
            mPressed_A_box_select(p0);
        }//折線と補助活線と補助絵線
    }

    //マウス操作(i_mouse_modeA==3,23でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_03(Point p0) {
        //System.out.println("(2)zzzzz ori_s.check4_size() = "+ori_s.check4_size());
        if (i_foldLine_additional == FoldLineAdditionalInputMode.POLY_LINE_0) {
            mDragged_A_box_select(p0);
        }
        if (i_foldLine_additional == FoldLineAdditionalInputMode.BLACK_LINE_2) {
            mDragged_A_box_select(p0);
        }
        if (i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LIVE_LINE_3) {
            mDragged_A_box_select(p0);
        }

        if (i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LINE_1) {
            mDragged_A_box_select(p0);
        }

        if (i_foldLine_additional == FoldLineAdditionalInputMode.BOTH_4) {
            mDragged_A_box_select(p0);
        }


    }


//--------------------

    //マウス操作(i_mouse_modeA==3,23 でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_03(Point p0) {//折線と補助活線と円
        //System.out.println("(3_1)zzzzz ori_s.check4_size() = "+ori_s.check4_size());
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        i_drawing_stage = 0;

        //最寄の一つを削除
        if (p19_1.distance(p0) <= 0.000001) {//最寄の一つを削除
            int i_removal_mode = 10;//i_removal_mode is defined and declared here
            if (i_foldLine_additional == FoldLineAdditionalInputMode.POLY_LINE_0) {
                i_removal_mode = 0;
            }
            if (i_foldLine_additional == FoldLineAdditionalInputMode.BLACK_LINE_2) {
                i_removal_mode = 2;
            }
            if (i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LIVE_LINE_3) {
                i_removal_mode = 3;
            }
            if (i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LINE_1) {
                i_removal_mode = 1;
            }
            if (i_foldLine_additional == FoldLineAdditionalInputMode.BOTH_4) {
                i_removal_mode = 10;

                double rs_min = ori_s.closestLineSegmentDistance(p);//点pに最も近い線分(折線と補助活線)の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)

                double re_min = ori_s.closestCircleDistance(p);//点pに最も近い円の番号での、その距離を返す	public double mottomo_tikai_en_kyori(Ten p)

                double hoj_rs_min = hoj_s.closestLineSegmentDistance(p);//点pに最も近い補助絵線の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)

                if ((rs_min <= re_min) && (rs_min <= hoj_rs_min)) {
                    if (ori_s.getColor(ori_s.closestLineSegmentSearchReversedOrder(p)).getNumber() < 3) {
                        i_removal_mode = 0;
                    } else {
                        i_removal_mode = 3;
                    }
                }

                if ((re_min < rs_min) && (re_min <= hoj_rs_min)) {
                    i_removal_mode = 3;
                }
                if ((hoj_rs_min < rs_min) && (hoj_rs_min < re_min)) {
                    i_removal_mode = 1;
                }

            }


            if (i_removal_mode == 0) { //折線の削除

                //Ten p =new Ten(); p.set(camera.TV2object(p0));
                double rs_min;
                rs_min = ori_s.closestLineSegmentDistance(p);//点pに最も近い線分(折線と補助活線)の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                if (rs_min < d_decision_width) {
                    if (ori_s.getColor(ori_s.closestLineSegmentSearchReversedOrder(p)).getNumber() < 3) {
                        ori_s.deleteLineSegment_vertex(ori_s.closestLineSegmentSearchReversedOrder(p));
                        circle_organize();
                        record();
                    }
                }
            }


            if (i_removal_mode == 2) { //黒の折線の削除
                double rs_min = ori_s.closestLineSegmentDistance(p);//点pに最も近い線分(折線と補助活線)の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                if (rs_min < d_decision_width) {
                    if (ori_s.getColor(ori_s.closestLineSegmentSearchReversedOrder(p)) == LineColor.BLACK_0) {
                        ori_s.deleteLineSegment_vertex(ori_s.closestLineSegmentSearchReversedOrder(p));
                        circle_organize();
                        record();
                    }
                }
            }

            if (i_removal_mode == 3) {  //補助活線
                double rs_min = ori_s.closestLineSegmentDistance(p);//点pに最も近い線分(折線と補助活線)の番号での、その距離を返す
                double re_min = ori_s.closestCircleDistance(p);//点pに最も近い円の番号での、その距離を返す	public double mottomo_tikai_en_kyori(Ten p)

                if (rs_min <= re_min) {
                    if (rs_min < d_decision_width) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                        if (ori_s.getColor(ori_s.closestLineSegmentSearchReversedOrder(p)) == LineColor.CYAN_3) {
                            ori_s.deleteLineSegment_vertex(ori_s.closestLineSegmentSearchReversedOrder(p));
                            circle_organize();
                            record();
                        }
                    }
                } else {
                    if (re_min < d_decision_width) {
                        ori_s.delen(ori_s.closest_circle_search_reverse_order(p));
                        circle_organize();
                        record();
                    }
                }
            }

            if (i_removal_mode == 1) { //補助絵線
                double rs_min;
                rs_min = hoj_s.closestLineSegmentDistance(p);//点pに最も近い補助絵線の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)

                if (rs_min < d_decision_width) {
                    hoj_s.deleteLineSegment_vertex(hoj_s.closestLineSegmentSearchReversedOrder(p));
                    //en_seiri();
                    record();
                }
            }
        }


        //四角枠内の削除 //p19_1はselectの最初のTen。この条件は最初のTenと最後の点が遠いので、四角を発生させるということ。
        if (p19_1.distance(p0) > 0.000001) {
            if ((i_foldLine_additional == FoldLineAdditionalInputMode.POLY_LINE_0) || (i_foldLine_additional == FoldLineAdditionalInputMode.BOTH_4)) { //折線の削除	//D_nisuru(p19_1,p0)で折線だけが削除される。
                if (D_nisuru0(p19_1, p0) != 0) {
                    circle_organize();
                    record();
                }
            }


            if (i_foldLine_additional == FoldLineAdditionalInputMode.BLACK_LINE_2) {  //黒の折線のみ削除
                if (D_nisuru2(p19_1, p0) != 0) {
                    circle_organize();
                    record();
                }
            }


            if ((i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LIVE_LINE_3) || (i_foldLine_additional == FoldLineAdditionalInputMode.BOTH_4)) {  //補助活線  //現状では削除しないときもUNDO用に記録されてしまう20161218
                if (D_nisuru3(p19_1, p0) != 0) {
                    circle_organize();
                    record();
                }
            }

            if ((i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LINE_1) || (i_foldLine_additional == FoldLineAdditionalInputMode.BOTH_4)) { //補助絵線	//現状では削除しないときもUNDO用に記録されてしまう20161218
                if (D_nisuru1(p19_1, p0) != 0) {
                    record();
                }
            }

        }

//qqqqqqqqqqqqqqqqqqqqqqqqqqqqq//System.out.println("= ");qqqqq
//check4(0.0001);//D_nisuru0をすると、ori_s.D_nisuru0内でresetが実行されるため、check4のやり直しが必要。
        if (check1) {
            check1(0.001, 0.5);
        }
        if (check2) {
            check2(0.01, 0.5);
        }
        if (check3) {
            check3(0.0001);
        }
        if (check4) {
            check4(0.0001);
        }

    }


//--------------------

    public int D_nisuru(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getX(), p0a.getY());
        p0_b.set(p0a.getX(), p0b.getY());
        p0_c.set(p0b.getX(), p0b.getY());
        p0_d.set(p0b.getX(), p0a.getY());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return ori_s.D_nisuru(p_a, p_b, p_c, p_d);
    }

    public int D_nisuru0(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getX(), p0a.getY());
        p0_b.set(p0a.getX(), p0b.getY());
        p0_c.set(p0b.getX(), p0b.getY());
        p0_d.set(p0b.getX(), p0a.getY());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        //System.out.println("(3_2_1)zzzzz ori_s.check4_size() = "+ori_s.check4_size());
        return ori_s.D_nisuru0(p_a, p_b, p_c, p_d);
    }

    public int D_nisuru2(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getX(), p0a.getY());
        p0_b.set(p0a.getX(), p0b.getY());
        p0_c.set(p0b.getX(), p0b.getY());
        p0_d.set(p0b.getX(), p0a.getY());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return ori_s.D_nisuru2(p_a, p_b, p_c, p_d);
    }

    //--------------------
    public int D_nisuru3(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getX(), p0a.getY());
        p0_b.set(p0a.getX(), p0b.getY());
        p0_c.set(p0b.getX(), p0b.getY());
        p0_d.set(p0b.getX(), p0a.getY());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return ori_s.D_nisuru3(p_a, p_b, p_c, p_d);
    }

//--------------------

    public int chenge_property_in_4kakukei(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getX(), p0a.getY());
        p0_b.set(p0a.getX(), p0b.getY());
        p0_c.set(p0b.getX(), p0b.getY());
        p0_d.set(p0b.getX(), p0a.getY());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return ori_s.chenge_property_in_4kakukei(p_a, p_b, p_c, p_d, circle_custom_color);
    }

    public int D_nisuru1(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getX(), p0a.getY());
        p0_b.set(p0a.getX(), p0b.getY());
        p0_c.set(p0b.getX(), p0b.getY());
        p0_d.set(p0b.getX(), p0a.getY());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return hoj_s.D_nisuru(p_a, p_b, p_c, p_d);
    }

    //59 59 59 59 59 59 59 59 59 59
    //マウス操作(i_mouse_modeA==59 "特注プロパティ指定" でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_59(Point p0) {
        mPressed_A_box_select(p0);   //折線と補助活線と補助絵線
    }

    //マウス操作(i_mouse_modeA==59 "特注プロパティ指定"でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_59(Point p0) {
        mDragged_A_box_select(p0);
    }

    //マウス操作(i_mouse_modeA==59 "特注プロパティ指定" でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_59(Point p0) {//補助活線と円
        i_drawing_stage = 0;
        if (p19_1.distance(p0) > 0.000001) {//現状では削除しないときもUNDO用に記録されてしまう20161218

            //if(D_nisuru3(p19_1,p0)!=0){en_seiri();kiroku();}
            if (chenge_property_in_4kakukei(p19_1, p0) != 0) {
                //kiroku();
            }

        }

        if (p19_1.distance(p0) <= 0.000001) {
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            double rs_min;
            rs_min = ori_s.closestLineSegmentDistance(p);//点pに最も近い補助活線の番号での、その距離を返す
            double re_min;
            re_min = ori_s.closestCircleDistance(p);//点pに最も近い円の番号での、その距離を返す	public double mottomo_tikai_en_kyori(Ten p)


            if (rs_min <= re_min) {
                if (rs_min < d_decision_width) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                    if (ori_s.getColor(ori_s.closestLineSegmentSearchReversedOrder(p)) == LineColor.CYAN_3) {
                        ori_s.setLineCustomized(ori_s.closestLineSegmentSearchReversedOrder(p), 1);
                        ori_s.setLineCustomizedColor(ori_s.closestLineSegmentSearchReversedOrder(p), circle_custom_color);
                        //en_seiri();kiroku();
                    }
                }
            } else {
                if (re_min < d_decision_width) {
                    ori_s.setCircleCustomized(ori_s.closest_circle_search_reverse_order(p), 1);
                    ori_s.setCircleCustomizedColor(ori_s.closest_circle_search_reverse_order(p), circle_custom_color);
                }
            }
        }

//ppppppppppp


    }

    //4 4 4 4 4 444444444444444444444444444444444444444444444444444444444
    public void mPressed_A_04(Point p0) {
    }//マウス操作(i_mouse_modeA==4線_変換　でボタンを押したとき)時の作業

    public void mDragged_A_04(Point p0) {
    }//マウス操作(i_mouse_modeA==4線_変換　でドラッグしたとき)を行う関数
//--------------------

    //マウス操作(i_mouse_modeA==4線_変換　でボタンを離したとき)を行う関数
    public void mReleased_A_04(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));


        if (ori_s.closestLineSegmentDistance(p) < d_decision_width) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
            int minrid;
            minrid = ori_s.closestLineSegmentSearch(p);
            LineColor ic_temp;
            ic_temp = ori_s.getColor(minrid);
            if (ic_temp.isFoldingLine()) {
                ori_s.setColor(minrid, ic_temp.advanceFolding());
                record();
            }
        }

/*
		int minrid;double minr;
		minrid=ori_s.mottomo_tikai_senbun_sagasi(p);
		if(ori_s.senbun_busyo_sagasi(minrid, p, r)!=0){
			int ic_temp;
			ic_temp=ori_s.getcolor(minrid);
			ic_temp=ic_temp+1;if(ic_temp==3){ic_temp=0;}
			ori_s.setcolor(minrid,ic_temp);
		}
*/

    }

    //------
//58 58 58 58 58 58 58 58 58 58
    public void mPressed_A_58(Point p0) {
        mPressed_A_box_select(p0);
    }//マウス操作(i_mouse_modeA==58線_変換　でボタンを押したとき)時の作業

    public void mDragged_A_58(Point p0) {
        mDragged_A_box_select(p0);
    }//マウス操作(i_mouse_modeA==58線_変換　でドラッグしたとき)を行う関数

    //マウス操作(i_mouse_modeA==58線_変換　でボタンを離したとき)を行う関数
    public void mReleased_A_58(Point p0) {//ここの処理の終わりに fix2(0.001,0.5);　をするのは、元から折線だったものと、補助線から変換した折線との組合せで頻発するT字型不接続を修正するため
        i_drawing_stage = 0;

        if (p19_1.distance(p0) > 0.000001) {//
            if (MV_change(p19_1, p0) != 0) {
                fix2(0.001, 0.5);
                record();
            }
        }


        if (p19_1.distance(p0) <= 0.000001) {//
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            if (ori_s.closestLineSegmentDistance(p) < d_decision_width) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                int minrid;
                minrid = ori_s.closestLineSegmentSearch(p);
                LineColor ic_temp;
                ic_temp = ori_s.getColor(minrid);
                if (ic_temp == LineColor.RED_1) {
                    ori_s.setColor(minrid, LineColor.BLUE_2);
                } else if (ic_temp == LineColor.BLUE_2) {
                    ori_s.setColor(minrid, LineColor.RED_1);
                }

                fix2(0.001, 0.5);
                record();
            }

        }
    }
//------


//i_mouse_modeA;マウスの動作に対する反応を規定する。
// -------------1;線分入力モード。
//2;展開図調整(移動)。
//3;"L_del"
//4;"L_chan"

// -------------5;線分延長モード。
// -------------6;2点から等距離線分モード。
// -------------7;角二等分線モード。
// -------------8;内心モード。
// -------------9;垂線おろしモード。
// -------------10;折り返しモード。
// -------------11;線分入力モード。
// -------------12;鏡映モード。

//101:折り上がり図の操作。
//102;F_move
//103;S_face

//10001;test1 入力準備として点を３つ指定する


//66666666666666666666    i_mouse_modeA==6　;2点から等距離線分モード

    //------
    public int MV_change(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getX(), p0a.getY());
        p0_b.set(p0a.getX(), p0b.getY());
        p0_c.set(p0b.getX(), p0b.getY());
        p0_d.set(p0b.getX(), p0a.getY());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return ori_s.MV_change(p_a, p_b, p_c, p_d);
    }

    public void mPressed_A_30(Point p0) {    //マウス操作(i_mouse_modeA==4線_変換　でボタンを押したとき)時の作業
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        minrid_30 = -1;
        if (ori_s.closestLineSegmentDistance(p) < d_decision_width) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
            minrid_30 = ori_s.closestLineSegmentSearch(p);
            LineSegment s01 = new LineSegment();
            s01.set(OritaCalc.lineSegment_double(ori_s.get(minrid_30), 0.01));
            ori_s.setB(minrid_30, s01.getB());
        }
    }

    public void mDragged_A_30(Point p0) {//マウス操作(i_mouse_modeA==4線_変換　でドラッグしたとき)を行う関数
        if (minrid_30 > 0) {

            LineSegment s01 = new LineSegment();
            s01.set(OritaCalc.lineSegment_double(ori_s.get(minrid_30), 100.0));
            ori_s.setB(minrid_30, s01.getB());
            minrid_30 = -1;
        }

    }

//------


//------折り畳み可能線入力


//38 38 38 38 38 38 38    i_mouse_modeA==38　;折り畳み可能線入力  qqqqqqqqq

    //マウス操作(i_mouse_modeA==30 除け_線_変換　でボタンを離したとき)を行う関数（背景に展開図がある場合用）
    public void mReleased_A_30(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));

        if (minrid_30 > 0) {

            LineSegment s01 = new LineSegment();
            s01.set(OritaCalc.lineSegment_double(ori_s.get(minrid_30), 100.0));
            ori_s.setB(minrid_30, s01.getB());

            LineColor ic_temp;
            ic_temp = ori_s.getColor(minrid_30);
            int is_temp;
            is_temp = ori_s.get_select(minrid_30);

            if ((ic_temp == LineColor.BLACK_0) && (is_temp == 0)) {
                ori_s.set_select(minrid_30, 2);
            } else if ((ic_temp == LineColor.BLACK_0) && (is_temp == 2)) {
                ori_s.setColor(minrid_30, LineColor.RED_1);
                ori_s.set_select(minrid_30, 0);
            } else if ((ic_temp == LineColor.RED_1) && (is_temp == 0)) {
                ori_s.setColor(minrid_30, LineColor.BLUE_2);
            } else if ((ic_temp == LineColor.BLUE_2) && (is_temp == 0)) {
                ori_s.setColor(minrid_30, LineColor.BLACK_0);
            }

            record();
        }


    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_06(Point p0) {


        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) < d_decision_width) {
            i_drawing_stage = i_drawing_stage + 1;
            s_step[i_drawing_stage].set(closest_point, closest_point);
            s_step[i_drawing_stage].setColor(LineColor.fromNumber(i_drawing_stage));
        }


    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_06(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_06(Point p0) {
        if (i_drawing_stage == 3) {
            i_drawing_stage = 0;
        }


    }

    //マウス操作(マウスを動かしたとき)を行う関数    //System.out.println("_");
    public void mMoved_A_38(Point p0) {
        if (i_kou_mitudo_nyuuryoku) {
            if (i_drawing_stage == 0) {
                i_step_for38 = 0;
            }

            if (i_step_for38 == 0) {
                mMoved_A_29(p0);
            }

            if (i_step_for38 == 1) {
                s_kouho[1].setActive(3);
                i_candidate_stage = 0;
                //Ten p =new Ten();
                p.set(camera.TV2object(p0));

                closest_lineSegment.set(get_moyori_step_senbun(p, 1, i_drawing_stage));
                if ((i_drawing_stage >= 2) && (OritaCalc.distance_lineSegment(p, closest_lineSegment) < d_decision_width)) {

                    i_candidate_stage = 1;
                    s_kouho[1].set(closest_lineSegment);//s_kouho[1].setcolor(2);
                    return;
                }
            }

            if (i_step_for38 == 2) {
                i_candidate_stage = 0;
                Point p = new Point();
                p.set(camera.TV2object(p0));

                closest_lineSegment.set(get_moyori_senbun(p));
                if (OritaCalc.distance_lineSegment(p, closest_lineSegment) < d_decision_width) {//最寄の既存折線が近い場合
                    i_candidate_stage = 1;
                    s_kouho[1].set(closest_lineSegment);
                    //s_kouho[1].setcolor(2);
                    return;
                }

            }
        }
    }


//------折り畳み可能線+格子点系入力

    //Ten t1 =new Ten();
//マウス操作(ボタンを押したとき)時の作業
    public int mPressed_A_38(Point p0) {//作業がすべて完了し新たな折線を追加でた場合だけ1を返す。それ以外は0を返す。
        i_candidate_stage = 0;
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        if (i_drawing_stage == 0) {
            i_step_for38 = 0;
        }

        if (i_step_for38 == 0) {
            double hantei_kyori = 0.000001;

            Point t1 = new Point();
            t1.set(ori_s.closestPointOfFoldLine(p));//点pに最も近い、「線分の端点」を返すori_s.mottomo_tikai_Tenは近い点がないと p_return.set(100000.0,100000.0)と返してくる

            if (p.distance(t1) < d_decision_width) {
                //i_egaki_dankai=i_egaki_dankai+1;
                //s_step[i_egaki_dankai].set(moyori_ten,moyori_ten);s_step[i_egaki_dankai].setcolor(i_egaki_dankai);

                //t1を端点とする折線をNarabebakoに入れる
                SortingBox_int_double nbox = new SortingBox_int_double();
                for (int i = 1; i <= ori_s.getTotal(); i++) {
                    if (ori_s.getColor(i).isFoldingLine()) {
                        if (t1.distance(ori_s.getA(i)) < hantei_kyori) {
                            nbox.container_i_smallest_first(new int_double(i, OritaCalc.angle(ori_s.getA(i), ori_s.getB(i))));
                        } else if (t1.distance(ori_s.getB(i)) < hantei_kyori) {
                            nbox.container_i_smallest_first(new int_double(i, OritaCalc.angle(ori_s.getB(i), ori_s.getA(i))));
                        }
                    }
                }

                //System.out.println("20170126_4");

                if (nbox.getTotal() % 2 == 1) {//t1を端点とする折線の数が奇数のときだけif{}内の処理をする
                    icol_temp = icol;
                    if (nbox.getTotal() == 1) {
                        icol_temp = ori_s.get(nbox.getInt(1)).getColor();
                    }//20180503この行追加。これは、折線が1本だけの頂点から折り畳み可能線追加機能で、その折線の延長を行った場合に、線の色を延長前の折線と合わせるため

                    //int i_kouho_suu=0;
                    for (int i = 1; i <= nbox.getTotal(); i++) {//iは角加減値を求める最初の折線のid
                        //折線が奇数の頂点周りの角加減値を2.0で割ると角加減値の最初折線と、折り畳み可能にするための追加の折線との角度になる。
                        double kakukagenti = 0.0;
                        //System.out.println("nbox.getsousuu()="+nbox.getsousuu());
                        int tikai_orisen_jyunban;
                        int tooi_orisen_jyunban;
                        for (int k = 1; k <= nbox.getTotal(); k++) {//kは角加減値を求める角度の順番
                            tikai_orisen_jyunban = i + k - 1;
                            if (tikai_orisen_jyunban > nbox.getTotal()) {
                                tikai_orisen_jyunban = tikai_orisen_jyunban - nbox.getTotal();
                            }
                            tooi_orisen_jyunban = i + k;
                            if (tooi_orisen_jyunban > nbox.getTotal()) {
                                tooi_orisen_jyunban = tooi_orisen_jyunban - nbox.getTotal();
                            }

                            double add_kakudo = OritaCalc.angle_between_0_360(nbox.getDouble(tooi_orisen_jyunban) - nbox.getDouble(tikai_orisen_jyunban));
                            if (k % 2 == 1) {
                                kakukagenti = kakukagenti + add_kakudo;
                            } else if (k % 2 == 0) {
                                kakukagenti = kakukagenti - add_kakudo;
                            }
                            //System.out.println("i="+i+"   k="+k);


                        }


                        if (nbox.getTotal() == 1) {
                            kakukagenti = 360.0;
                        }

                        //System.out.println("kakukagenti="+kakukagenti);
                        //チェック用に角加減値の最初の角度の中にkakukagenti/2.0があるかを確認する
                        tikai_orisen_jyunban = i;
                        if (tikai_orisen_jyunban > nbox.getTotal()) {
                            tikai_orisen_jyunban = tikai_orisen_jyunban - nbox.getTotal();
                        }
                        tooi_orisen_jyunban = i + 1;
                        if (tooi_orisen_jyunban > nbox.getTotal()) {
                            tooi_orisen_jyunban = tooi_orisen_jyunban - nbox.getTotal();
                        }

                        double add_kakudo_1 = OritaCalc.angle_between_0_360(nbox.getDouble(tooi_orisen_jyunban) - nbox.getDouble(tikai_orisen_jyunban));
                        if (nbox.getTotal() == 1) {
                            add_kakudo_1 = 360.0;
                        }

                        if ((kakukagenti / 2.0 > 0.0 + 0.000001) && (kakukagenti / 2.0 < add_kakudo_1 - 0.000001)) {
                            //if((kakukagenti/2.0>0.0-0.000001)&&(kakukagenti/2.0<add_kakudo_1+0.000001)){

                            i_drawing_stage = i_drawing_stage + 1;

                            //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d)
                            LineSegment s_kiso = new LineSegment();
                            if (t1.distance(ori_s.getA(nbox.getInt(i))) < hantei_kyori) {
                                s_kiso.set(ori_s.getA(nbox.getInt(i)), ori_s.getB(nbox.getInt(i)));
                            } else if (t1.distance(ori_s.getB(nbox.getInt(i))) < hantei_kyori) {
                                s_kiso.set(ori_s.getB(nbox.getInt(i)), ori_s.getA(nbox.getInt(i)));
                            }

                            double s_kiso_length = s_kiso.getLength();

                            s_step[i_drawing_stage].set(OritaCalc.lineSegment_rotate(s_kiso, kakukagenti / 2.0, grid.d_width() / s_kiso_length));
                            s_step[i_drawing_stage].setColor(LineColor.PURPLE_8);
                            s_step[i_drawing_stage].setActive(0);
                        }
                    }
                    if (i_drawing_stage == 1) {
                        i_step_for38 = 2;
                    }
                    if (i_drawing_stage > 1) {
                        i_step_for38 = 1;
                    }
                }
            }
            return 0;
        }

        if (i_step_for38 == 1) {
            closest_lineSegment.set(get_moyori_step_senbun(p, 1, i_drawing_stage));
            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) < d_decision_width) {
                i_step_for38 = 2;
                i_drawing_stage = 1;
                s_step[1].set(closest_lineSegment);

                //i_egaki_dankai=i_egaki_dankai+1;
                //s_step[i_egaki_dankai].set(moyori_senbun);//s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
                //s_step[i_egaki_dankai].setcolor(8);
                return 0;
            }
            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) >= d_decision_width) {
                i_drawing_stage = 0;
                return 0;
            }
        }

        if (i_step_for38 == 2) {
            closest_lineSegment.set(get_moyori_senbun(p));
            //Senbun moyori_step_senbun =new Senbun(get_moyori_step_senbun(p,1,i_egaki_dankai));
            LineSegment moyori_step_lineSegment = new LineSegment();
            moyori_step_lineSegment.set(get_moyori_step_senbun(p, 1, i_drawing_stage));
            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) >= d_decision_width) {//最寄の既存折線が遠くて選択無効の場合
                //moyori_senbun.set(get_moyori_step_senbun(p,1,i_egaki_dankai));
                if (OritaCalc.distance_lineSegment(p, moyori_step_lineSegment) < d_decision_width) {//最寄のstep_senbunが近い場合
                    //System.out.println("20170129_1");
                    return 0;
                }

                //最寄のstep_senbunが遠い場合
                //System.out.println("20170129_2");

                i_drawing_stage = 0;
                return 0;
            }

            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) < d_decision_width) {//最寄の既存折線が近い場合

                s_step[2].set(closest_lineSegment);
                s_step[2].setColor(LineColor.GREEN_6);

                //System.out.println("20170129_3");
                Point kousa_point = new Point();
                kousa_point.set(OritaCalc.findIntersection(s_step[1], s_step[2]));
                LineSegment add_sen = new LineSegment(kousa_point, s_step[1].getA(), icol_temp);//20180503変更
                if (add_sen.getLength() > 0.00000001) {//最寄の既存折線が有効の場合
                    addLineSegment(add_sen);
                    record();
                    i_drawing_stage = 0;
                    return 1;

                }

                //最寄の既存折線が無効の場合

                //最寄のstep_senbunが近い場合
                if (OritaCalc.distance_lineSegment(p, moyori_step_lineSegment) < d_decision_width) {
                    return 0;
                }

                //最寄のstep_senbunが遠い場合
                i_drawing_stage = 0;
                return 0;

            }


        }


        return 0;


    }
//
//課題　step線と既存折線が平行の時エラー方向に線を引くことを改善すること20170407
//
//動作仕様
//（１）点を選択（既存点選択規制）
//（２a）選択点が3以上の奇数折線の頂点の場合
//（３）
//
//
//（２b）２a以外の場合
//
//Ten t1 =new Ten();

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_38(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_38(Point p0) {

    }

    //マウス操作(マウスを動かしたとき)を行う関数    //System.out.println("_");
    public void mMoved_A_39(Point p0) {
        if (i_drawing_stage == 0) {
            i_step_for39 = 0;
        }
        if (i_kou_mitudo_nyuuryoku) {
            i_candidate_stage = 0;
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));

            if (i_drawing_stage == 0) {
                i_step_for39 = 0;
            }
            System.out.println("i_egaki_dankai= " + i_drawing_stage + "  ;   i_step_for39= " + i_step_for39);

            if (i_step_for39 == 0) {
                mMoved_A_29(p0);
			/*	double hantei_kyori=0.000001;
				//任意の点が与えられたとき、端点もしくは格子点で最も近い点を得る
				moyori_ten.set(get_moyori_ten(p));
				if(p.kyori(moyori_ten)<d_decision_width){
					i_candidate_stage=1;
					s_kouho[1].set(moyori_ten,moyori_ten);
				 	s_kouho[1].setcolor(icol);
				}
				return;
			*/
            }

            if (i_step_for39 == 1) {
                closest_lineSegment.set(get_moyori_step_senbun(p, 1, i_drawing_stage));
                if ((i_drawing_stage >= 2) && (OritaCalc.distance_lineSegment(p, closest_lineSegment) < d_decision_width)) {
                    //System.out.println("20170129_5");
                    i_candidate_stage = 1;
                    s_kouho[1].set(closest_lineSegment);//s_kouho[1].setcolor(2);
                    return;
                }

                closest_point.set(getClosestPoint(p));
                if (p.distance(closest_point) < d_decision_width) {
                    //s_kouho[1].setb(moyori_ten);s_kouho[1].setcolor(2);
                    s_kouho[1].set(closest_point, closest_point);
                    s_kouho[1].setColor(icol);
                    i_candidate_stage = 1;
                    return;
                }
                return;
            }


            if (i_step_for39 == 2) {//i_step_for39==2であれば、以下でs_step[1]を入力折線を確定する
                closest_point.set(getClosestPoint(p));

                if (closest_point.distance(s_step[1].getA()) < 0.00000001) {
                    i_candidate_stage = 1;
                    s_kouho[1].set(closest_point, closest_point);
                    s_kouho[1].setColor(icol);
                    System.out.println("i_step_for39_2_   1");

                    return;


                }


                if ((p.distance(s_step[1].getB()) < d_decision_width) && (p.distance(s_step[1].getB()) <= p.distance(closest_point))) {
                    i_candidate_stage = 1;
                    s_kouho[1].set(s_step[1].getB(), s_step[1].getB());
                    s_kouho[1].setColor(icol);
                    System.out.println("i_step_for39_2_   2");

                    return;
                }


                if (p.distance(closest_point) < d_decision_width) {
                    i_candidate_stage = 1;
                    s_kouho[1].set(closest_point, closest_point);
                    s_kouho[1].setColor(icol);
                    System.out.println("i_step_for39_2_   3");

                    return;
                }

                closest_lineSegment.set(get_moyori_senbun(p));
                LineSegment moyori_step_lineSegment = new LineSegment();
                moyori_step_lineSegment.set(get_moyori_step_senbun(p, 1, i_drawing_stage));
                if (OritaCalc.distance_lineSegment(p, closest_lineSegment) >= d_decision_width) {//最寄の既存折線が遠い場合
                    if (OritaCalc.distance_lineSegment(p, moyori_step_lineSegment) < d_decision_width) {//最寄のstep_senbunが近い場合
                        return;
                    }
                    //最寄のstep_senbunが遠い場合
                    System.out.println("i_step_for39_2_   4");

                    return;
                }

                if (OritaCalc.distance_lineSegment(p, closest_lineSegment) < d_decision_width) {//最寄の既存折線が近い場合
                    i_candidate_stage = 1;
                    s_kouho[1].set(closest_lineSegment);
                    s_kouho[1].setColor(icol);

                    System.out.println("i_step_for39_2_   5");
                    return;
                }
                return;
            }

            return;
        }
    }

    //マウス操作(ボタンを押したとき)時の作業--------------
    public void mPressed_A_39(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));

        if (i_drawing_stage == 0) {
            i_step_for39 = 0;
        }


        //if(i_egaki_dankai==0){i_step_for39=0;}

        if (i_step_for39 == 0) {
            double decision_distance = 0.000001;

            //任意の点が与えられたとき、端点もしくは格子点で最も近い点を得る
            closest_point.set(getClosestPoint(p));

            if (p.distance(closest_point) < d_decision_width) {
                //i_egaki_dankai=i_egaki_dankai+1;
                //s_step[i_egaki_dankai].set(moyori_ten,moyori_ten);s_step[i_egaki_dankai].setcolor(i_egaki_dankai);

                //moyori_tenを端点とする折線をNarabebakoに入れる
                SortingBox_int_double nbox = new SortingBox_int_double();
                for (int i = 1; i <= ori_s.getTotal(); i++) {
                    if (ori_s.getColor(i).isFoldingLine()) {
                        if (closest_point.distance(ori_s.getA(i)) < decision_distance) {
                            nbox.container_i_smallest_first(new int_double(i, OritaCalc.angle(ori_s.getA(i), ori_s.getB(i))));
                        } else if (closest_point.distance(ori_s.getB(i)) < decision_distance) {
                            nbox.container_i_smallest_first(new int_double(i, OritaCalc.angle(ori_s.getB(i), ori_s.getA(i))));
                        }
                    }
                }
                //System.out.println("nbox.getsousuu()="+nbox.getsousuu());
                if (nbox.getTotal() % 2 == 1) {//moyori_tenを端点とする折線の数が奇数のときだけif{}内の処理をする
                    //System.out.println("20170130_3");

                    //int i_kouho_suu=0;
                    for (int i = 1; i <= nbox.getTotal(); i++) {//iは角加減値を求める最初の折線のid
                        //折線が奇数の頂点周りの角加減値を2.0で割ると角加減値の最初折線と、折り畳み可能にするための追加の折線との角度になる。
                        double kakukagenti = 0.0;
                        //System.out.println("nbox.getsousuu()="+nbox.getsousuu());
                        int tikai_orisen_jyunban;
                        int tooi_orisen_jyunban;
                        for (int k = 1; k <= nbox.getTotal(); k++) {//kは角加減値を求める角度の順番
                            tikai_orisen_jyunban = i + k - 1;
                            if (tikai_orisen_jyunban > nbox.getTotal()) {
                                tikai_orisen_jyunban = tikai_orisen_jyunban - nbox.getTotal();
                            }
                            tooi_orisen_jyunban = i + k;
                            if (tooi_orisen_jyunban > nbox.getTotal()) {
                                tooi_orisen_jyunban = tooi_orisen_jyunban - nbox.getTotal();
                            }

                            double add_kakudo = OritaCalc.angle_between_0_360(nbox.getDouble(tooi_orisen_jyunban) - nbox.getDouble(tikai_orisen_jyunban));
                            if (k % 2 == 1) {
                                kakukagenti = kakukagenti + add_kakudo;
                            } else if (k % 2 == 0) {
                                kakukagenti = kakukagenti - add_kakudo;
                            }
                            //System.out.println("i="+i+"   k="+k);
                        }

                        if (nbox.getTotal() == 1) {
                            kakukagenti = 360.0;
                        }
                        //System.out.println("kakukagenti="+kakukagenti);
                        //チェック用に角加減値の最初の角度の中にkakukagenti/2.0があるかを確認する
                        tikai_orisen_jyunban = i;
                        if (tikai_orisen_jyunban > nbox.getTotal()) {
                            tikai_orisen_jyunban = tikai_orisen_jyunban - nbox.getTotal();
                        }
                        tooi_orisen_jyunban = i + 1;
                        if (tooi_orisen_jyunban > nbox.getTotal()) {
                            tooi_orisen_jyunban = tooi_orisen_jyunban - nbox.getTotal();
                        }

                        double add_kakudo_1 = OritaCalc.angle_between_0_360(nbox.getDouble(tooi_orisen_jyunban) - nbox.getDouble(tikai_orisen_jyunban));
                        if (nbox.getTotal() == 1) {
                            add_kakudo_1 = 360.0;
                        }

                        if ((kakukagenti / 2.0 > 0.0 + 0.000001) && (kakukagenti / 2.0 < add_kakudo_1 - 0.000001)) {
                            i_drawing_stage = i_drawing_stage + 1;

                            //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d)
                            LineSegment s_kiso = new LineSegment();
                            if (closest_point.distance(ori_s.getA(nbox.getInt(i))) < decision_distance) {
                                s_kiso.set(ori_s.getA(nbox.getInt(i)), ori_s.getB(nbox.getInt(i)));
                            } else if (closest_point.distance(ori_s.getB(nbox.getInt(i))) < decision_distance) {
                                s_kiso.set(ori_s.getB(nbox.getInt(i)), ori_s.getA(nbox.getInt(i)));
                            }

                            double s_kiso_length = s_kiso.getLength();

                            s_step[i_drawing_stage].set(OritaCalc.lineSegment_rotate(s_kiso, kakukagenti / 2.0, grid.d_width() / s_kiso_length));
                            s_step[i_drawing_stage].setColor(LineColor.PURPLE_8);
                            s_step[i_drawing_stage].setActive(1);

                        }

                    }
                    //if(i_kouho_suu==1){i_step_for39=2;}
                    //if(i_kouho_suu>1){i_step_for39=1;}

                    if (i_drawing_stage == 1) {
                        i_step_for39 = 2;
                    }
                    if (i_drawing_stage > 1) {
                        i_step_for39 = 1;
                    }
                }

                if (i_drawing_stage == 0) {//折畳み可能化線がない場合//System.out.println("_");
                    i_drawing_stage = 1;
                    i_step_for39 = 1;
                    s_step[1].set(closest_point, closest_point);
                    s_step[1].setColor(LineColor.PURPLE_8);
                    s_step[1].setActive(3);
                }

            }
            return;
        }


        if (i_step_for39 == 1) {
            closest_lineSegment.set(get_moyori_step_senbun(p, 1, i_drawing_stage));
            if ((i_drawing_stage >= 2) && (OritaCalc.distance_lineSegment(p, closest_lineSegment) < d_decision_width)) {
                //if(oc.kyori_senbun( p,moyori_senbun)<d_decision_width){
                //System.out.println("20170129_5");
                i_step_for39 = 2;
                i_drawing_stage = 1;
                s_step[1].set(closest_lineSegment);
                return;
            }
            //if(oc.kyori_senbun( p,moyori_senbun)>=d_decision_width){
            //System.out.println("");
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) < d_decision_width) {
                s_step[1].setB(closest_point);
                i_step_for39 = 2;
                i_drawing_stage = 1;
                return;
            }
            //System.out.println("20170129_7");
            i_drawing_stage = 0;
            i_candidate_stage = 0;
            return;
            //}
            //return;
        }


        if (i_step_for39 == 2) {//i_step_for39==2であれば、以下でs_step[1]を入力折線を確定する
            closest_point.set(getClosestPoint(p));

            //System.out.println("20170130_1");
            if (closest_point.distance(s_step[1].getA()) < 0.00000001) {
                i_drawing_stage = 0;
                i_candidate_stage = 0;
                return;
            }
            //else if(p.kyori(s_step[1].getb())< kus.d_haba()/10.0 ){
            //else if(p.kyori(s_step[1].getb())< d_decision_width/2.5 ){
            //else if(p.kyori(s_step[1].getb())< d_decision_width ){

            if ((p.distance(s_step[1].getB()) < d_decision_width) &&
                    (
                            p.distance(s_step[1].getB()) <= p.distance(closest_point)
                            //moyori_ten.kyori(s_step[1].getb())<0.00000001
                    )) {
                LineSegment add_sen = new LineSegment(s_step[1].getA(), s_step[1].getB(), icol);
                addLineSegment(add_sen);
                record();
                i_drawing_stage = 0;
                i_candidate_stage = 0;
                return;
            }

            //}


            //if(i_step_for39==2){

            //moyori_ten.set(get_moyori_ten(p));
            if (p.distance(closest_point) < d_decision_width) {
                s_step[1].setB(closest_point);
                return;
            }


            closest_lineSegment.set(get_moyori_senbun(p));

            LineSegment moyori_step_lineSegment = new LineSegment();
            moyori_step_lineSegment.set(get_moyori_step_senbun(p, 1, i_drawing_stage));
            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) >= d_decision_width) {//最寄の既存折線が遠い場合
                //moyori_senbun.set(get_moyori_step_senbun(p,1,i_egaki_dankai));


                //moyori_ten.set(get_moyori_ten(p));if(p.kyori(moyori_ten)<d_decision_width){s_step[1].setb(moyori_ten);return;}
                //moyori_ten.set(ori_s.mottomo_tikai_Ten(p));if(p.kyori(moyori_ten)<d_decision_width){s_step[1].setb(moyori_ten);return;}


                if (OritaCalc.distance_lineSegment(p, moyori_step_lineSegment) < d_decision_width) {//最寄のstep_senbunが近い場合

                    //moyori_ten.set(get_moyori_ten(p));if(p.kyori(moyori_ten)<d_decision_width){s_step[1].setb(moyori_ten);return;}


                    return;
                }
                //最寄のstep_senbunが遠い場合

                //moyori_ten.set(get_moyori_ten(p));if(p.kyori(moyori_ten)<d_decision_width){s_step[1].setb(moyori_ten);return;}
                i_drawing_stage = 0;
                i_candidate_stage = 0;
                return;
            }

            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) < d_decision_width) {//最寄の既存折線が近い場合
                //moyori_ten.set(ori_s.mottomo_tikai_Ten(p));if(p.kyori(moyori_ten)<d_decision_width){s_step[1].setb(moyori_ten);return;}
                s_step[2].set(closest_lineSegment);
                s_step[2].setColor(LineColor.GREEN_6);
                //System.out.println("20170129_3");
                Point kousa_point = new Point();
                kousa_point.set(OritaCalc.findIntersection(s_step[1], s_step[2]));
                LineSegment add_sen = new LineSegment(kousa_point, s_step[1].getA(), icol);
                if (add_sen.getLength() > 0.00000001) {//最寄の既存折線が有効の場合
                    addLineSegment(add_sen);
                    record();
                    i_drawing_stage = 0;
                    i_candidate_stage = 0;
                    return;
                }
                //最寄の既存折線が無効の場合
                closest_point.set(getClosestPoint(p));
                if (p.distance(closest_point) < d_decision_width) {
                    s_step[1].setB(closest_point);
                    return;
                }
                //最寄のstep_senbunが近い場合
                if (OritaCalc.distance_lineSegment(p, moyori_step_lineSegment) < d_decision_width) {
                    return;
                }
                //最寄のstep_senbunが遠い場合
                i_drawing_stage = 0;
                i_candidate_stage = 0;
                return;

            }
            return;
        }


    }


//33 33 33 33 33 33 33 33 33 33 33魚の骨

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_39(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_39(Point p0) {

    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_33(Point p0) {
        mMoved_A_11(p0);
    }//近い既存点のみ表示

    //マウス操作(i_mouse_modeA==33魚の骨　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_33(Point p0) {
        i_drawing_stage = 1;

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) > d_decision_width) {
            i_drawing_stage = 0;
        }
        s_step[1].set(p, closest_point);
        s_step[1].setColor(icol);
        //k.addsenbun(p,p);
        //ieda=k.getsousuu();
        //k.setcolor(ieda,icol);
    }


//35 35 35 35 35 35 35 35 35 35 35複折り返し   入力した線分に接触している折線を折り返し　に使う

    //マウス操作(i_mouse_modeA==33魚の骨　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_33(Point p0) {
        mDragged_A_11(p0);
    }

    //マウス操作(i_mouse_modeA==33魚の骨　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_33(Point p0) {
        if (i_drawing_stage == 1) {
            i_drawing_stage = 0;

            //s_step[1]を確定する
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            closest_point.set(getClosestPoint(p));
            s_step[1].setA(closest_point);
            //s_step[1]を確定終了


            if (p.distance(closest_point) <= d_decision_width) {  //マウスで指定した点が、最寄点と近かったときに実施
                if (s_step[1].getLength() > 0.00000001) {  //s_step[1]が、線の時（=点状ではない時）に実施
                    double dx = (s_step[1].getAX() - s_step[1].getBX()) * grid.d_width() / s_step[1].getLength();
                    double dy = (s_step[1].getAY() - s_step[1].getBY()) * grid.d_width() / s_step[1].getLength();
                    LineColor icol_temp = icol;
                    //int imax=;

                    Point pxy = new Point();
                    for (int i = 0; i <= (int) Math.floor(s_step[1].getLength() / grid.d_width()); i++) {

                        //System.out.println("_"+i);
                        double px = s_step[1].getBX() + (double) i * dx;
                        double py = s_step[1].getBY() + (double) i * dy;
                        pxy.set(px, py);


                        //if(pxy.kyori(ori_s.mottomo_tikai_Ten(pxy) )>0.001      )         {
                        if (ori_s.closestLineSegmentDistanceExcludingParallel(pxy, s_step[1]) > 0.001) {

                            int i_sen = 0;

                            LineSegment adds = new LineSegment(px, py, px - dy, py + dx);
                            if (kouten_ari_nasi(adds) == 1) {
                                adds.set(extendToIntersectionPoint(adds));
                                adds.setColor(icol_temp);

                                addLineSegment(adds);
                                i_sen = i_sen + 1;
                            }


                            LineSegment adds2 = new LineSegment(px, py, px + dy, py - dx);
                            if (kouten_ari_nasi(adds2) == 1) {
                                adds2.set(extendToIntersectionPoint(adds2));
                                adds2.setColor(icol_temp);

                                addLineSegment(adds2);
                                i_sen = i_sen + 1;
                            }

                            //ori_s.del_V(ori_s.getsousuu()-1,ori_s.getsousuu());
                            //System.out.println("i_sen_"+i_sen);

                            if (i_sen == 2) {
                                ori_s.del_V(pxy, d_decision_width, 0.000001);
                            }

                        }

                        if (icol_temp == LineColor.RED_1) {
                            icol_temp = LineColor.BLUE_2;
                        } else if (icol_temp == LineColor.BLUE_2) {
                            icol_temp = LineColor.RED_1;
                        }


                    }
                    record();

                }  //s_step[1]が、線の時（=点状ではない時）に実施は、ここまで
            }  //マウスで指定した点が、最寄点と近かったときに実施は、ここまで
        }
    }

    //マウス操作(i_mouse_modeA==35　でドラッグしたとき)を行う関数----------------------------------------------------

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_35(Point p0) {
        mMoved_A_11(p0);
    }//近い既存点のみ表示

    //マウス操作(i_mouse_modeA==35　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_35(Point p0) {
        i_drawing_stage = 1;

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) > d_decision_width) {
            i_drawing_stage = 0;
        }
        s_step[1].set(p, closest_point);
        s_step[1].setColor(icol);
        //k.addsenbun(p,p);
        //ieda=k.getsousuu();
        //k.setcolor(ieda,icol);
    }


//------

    public void mDragged_A_35(Point p0) {
        mDragged_A_11(p0);
    }

//------


//------

    //マウス操作(i_mouse_modeA==35　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_35(Point p0) {

        SortingBox_int_double nbox = new SortingBox_int_double();

        if (i_drawing_stage == 1) {
            i_drawing_stage = 0;
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            closest_point.set(getClosestPoint(p));
            s_step[1].setA(closest_point);
            if (p.distance(closest_point) <= d_decision_width) {
                if (s_step[1].getLength() > 0.00000001) {
                    int imax = ori_s.getTotal();
                    for (int i = 1; i <= imax; i++) {
                        LineSegment.Intersection i_lineSegment_intersection_decision = OritaCalc.line_intersect_decide_sweet(ori_s.get(i), s_step[1], 0.01, 0.01);
                        int i_jikkou = 0;
                        //if(i_lineSegment_intersection_decision== 21 ){ i_jikkou=1;}//L字型
                        //if(i_lineSegment_intersection_decision== 22 ){ i_jikkou=1;}//L字型
                        //if(i_lineSegment_intersection_decision== 23 ){ i_jikkou=1;}//L字型
                        //if(i_lineSegment_intersection_decision== 24 ){ i_jikkou=1;}//L字型
                        if (i_lineSegment_intersection_decision == LineSegment.Intersection.INTERSECTS_TSHAPE_S1_VERTICAL_BAR_25) {
                            i_jikkou = 1;
                        }//T字型 s1が縦棒
                        if (i_lineSegment_intersection_decision == LineSegment.Intersection.INTERSECTS_TSHAPE_S1_VERTICAL_BAR_26) {
                            i_jikkou = 1;
                        }//T字型 s1が縦棒

                        if (i_jikkou == 1) {
                            Point t_moto = new Point();
                            t_moto.set(ori_s.getA(i));
                            System.out.println("i_senbun_kousa_hantei_" + i_lineSegment_intersection_decision);
                            if (OritaCalc.distance_lineSegment(t_moto, s_step[1]) < OritaCalc.distance_lineSegment(ori_s.getB(i), s_step[1])) {
                                t_moto.set(ori_s.getB(i));
                            }


                            //２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){
                            Point t_taisyou = new Point();
                            t_taisyou.set(OritaCalc.lineSymmetry_point_find(s_step[1].getA(), s_step[1].getB(), t_moto));

                            LineSegment add_sen = new LineSegment(OritaCalc.findIntersection(ori_s.get(i), s_step[1]), t_taisyou);

                            add_sen.set(extendToIntersectionPoint(add_sen));
                            add_sen.setColor(ori_s.getColor(i));
                            if (add_sen.getLength() > 0.00000001) {
                                addLineSegment(add_sen);
                            }
                        }

                    }


                    record();

                }
            }
        }

    }

//------
//------

    public LineSegment extendToIntersectionPoint(LineSegment s0) {//Extend s0 from point a to b, until it intersects another polygonal line. Returns a new line // Returns the same line if it does not intersect another polygonal line
        LineSegment add_sen = new LineSegment();
        add_sen.set(s0);
        Point kousa_point = new Point(1000000.0, 1000000.0); //この方法だと、エラーの原因になりうる。本当なら全線分のx_max、y_max以上の点を取ればいい。今後修正予定20161120
        double kousa_ten_kyori = kousa_point.distance(add_sen.getA());


        StraightLine tyoku1 = new StraightLine(add_sen.getA(), add_sen.getB());
        StraightLine.Intersection i_kousa_flg;
        for (int i = 1; i <= ori_s.getTotal(); i++) {
            i_kousa_flg = tyoku1.lineSegment_intersect_reverse_detail(ori_s.get(i));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。

            if (i_kousa_flg.isIntersecting()) {
                kousa_point.set(OritaCalc.findIntersection(tyoku1, ori_s.get(i)));
                if (kousa_point.distance(add_sen.getA()) > 0.00001) {

                    if (kousa_point.distance(add_sen.getA()) < kousa_ten_kyori) {

                        double d_kakudo = OritaCalc.angle(add_sen.getA(), add_sen.getB(), add_sen.getA(), kousa_point);
                        if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                            //i_kouten_ari_nasi=1;
                            kousa_ten_kyori = kousa_point.distance(add_sen.getA());
                            add_sen.set(add_sen.getA(), kousa_point);
                        }
                    }
                }
            }
        }
        return add_sen;
    }


//21 21 21 21 21    i_mouse_modeA==21　;移動モード

    public LineSegment extendToIntersectionPoint_2(LineSegment s0) {//Extend s0 from point b in the opposite direction of a to the point where it intersects another polygonal line. Returns a new line // Returns the same line if it does not intersect another polygonal line
        LineSegment add_sen = new LineSegment();
        add_sen.set(s0);
        //Senbun add_sen;add_sen=s0;


        Point kousa_point = new Point(1000000.0, 1000000.0); //この方法だと、エラーの原因になりうる。本当なら全線分のx_max、y_max以上の点を取ればいい。今後修正予定20161120
        double kousa_ten_kyori = kousa_point.distance(add_sen.getA());

        StraightLine tyoku1 = new StraightLine(add_sen.getA(), add_sen.getB());
        StraightLine.Intersection i_intersection_flg;//元の線分を直線としたものと、他の線分の交差状態
        LineSegment.Intersection i_lineSegment_intersection_flg;//元の線分と、他の線分の交差状態

        System.out.println("AAAAA_");
        for (int i = 1; i <= ori_s.getTotal(); i++) {
            i_intersection_flg = tyoku1.lineSegment_intersect_reverse_detail(ori_s.get(i));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。

            //i_lineSegment_intersection_flg=oc.senbun_kousa_hantei_amai( add_sen,ori_s.get(i),0.00001,0.00001);//20180408なぜかこの行の様にadd_senを使うと、i_senbun_kousa_flgがおかしくなる
            i_lineSegment_intersection_flg = OritaCalc.line_intersect_decide_sweet(s0, ori_s.get(i), 0.00001, 0.00001);//20180408なぜかこの行の様にs0のままだと、i_senbun_kousa_flgがおかしくならない。
            if (i_intersection_flg.isIntersecting()) {
                if (!i_lineSegment_intersection_flg.isEndpointIntersection()) {
                    //System.out.println("i_intersection_flg = "+i_intersection_flg  +      " ; i_lineSegment_intersection_flg = "+i_lineSegment_intersection_flg);
                    kousa_point.set(OritaCalc.findIntersection(tyoku1, ori_s.get(i)));
                    if (kousa_point.distance(add_sen.getA()) > 0.00001) {
                        if (kousa_point.distance(add_sen.getA()) < kousa_ten_kyori) {
                            double d_kakudo = OritaCalc.angle(add_sen.getA(), add_sen.getB(), add_sen.getA(), kousa_point);
                            if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                                //i_kouten_ari_nasi=1;
                                kousa_ten_kyori = kousa_point.distance(add_sen.getA());
                                add_sen.set(add_sen.getA(), kousa_point);
                            }
                        }
                    }


                }
            }

            if (i_intersection_flg == StraightLine.Intersection.INCLUDED_3) {
                if (i_lineSegment_intersection_flg != LineSegment.Intersection.PARALLEL_EQUAL_31) {


                    System.out.println("i_intersection_flg = " + i_intersection_flg + " ; i_lineSegment_intersection_flg = " + i_lineSegment_intersection_flg);


                    kousa_point.set(ori_s.get(i).getA());
                    if (kousa_point.distance(add_sen.getA()) > 0.00001) {
                        if (kousa_point.distance(add_sen.getA()) < kousa_ten_kyori) {
                            double d_kakudo = OritaCalc.angle(add_sen.getA(), add_sen.getB(), add_sen.getA(), kousa_point);
                            if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                                //i_kouten_ari_nasi=1;
                                kousa_ten_kyori = kousa_point.distance(add_sen.getA());
                                add_sen.set(add_sen.getA(), kousa_point);
                            }
                        }
                    }

                    kousa_point.set(ori_s.get(i).getB());
                    if (kousa_point.distance(add_sen.getA()) > 0.00001) {
                        if (kousa_point.distance(add_sen.getA()) < kousa_ten_kyori) {
                            double d_kakudo = OritaCalc.angle(add_sen.getA(), add_sen.getB(), add_sen.getA(), kousa_point);
                            if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                                //i_kouten_ari_nasi=1;
                                kousa_ten_kyori = kousa_point.distance(add_sen.getA());
                                add_sen.set(add_sen.getA(), kousa_point);
                            }
                        }
                    }


                }
            }


        }


        add_sen.set(s0.getB(), add_sen.getB());
        return add_sen;
    }

    public int kouten_ari_nasi(LineSegment s0) {//If s0 is extended from the point a to the b direction and intersects with another polygonal line, 0 is returned if it is not 1. The intersecting line segments at the a store have no intersection with this function.
        LineSegment add_line = new LineSegment();
        add_line.set(s0);
        Point intersection_point = new Point(1000000.0, 1000000.0); //この方法だと、エラーの原因になりうる。本当なら全線分のx_max、y_max以上の点を取ればいい。今後修正予定20161120
        double intersection_point_distance = intersection_point.distance(add_line.getA());


        StraightLine tyoku1 = new StraightLine(add_line.getA(), add_line.getB());
        StraightLine.Intersection i_intersection_flg;
        for (int i = 1; i <= ori_s.getTotal(); i++) {
            i_intersection_flg = tyoku1.lineSegment_intersect_reverse_detail(ori_s.get(i));//0 = This straight line does not intersect a given line segment, 1 = X type intersects, 2 = T type intersects, 3 = Line segment is included in the straight line.

            if (i_intersection_flg.isIntersecting()) {
                intersection_point.set(OritaCalc.findIntersection(tyoku1, ori_s.get(i)));
                if (intersection_point.distance(add_line.getA()) > 0.00001) {
                    double d_kakudo = OritaCalc.angle(add_line.getA(), add_line.getB(), add_line.getA(), intersection_point);
                    if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                        return 1;

                    }

                }
            }
        }
        return 0;
    }

    //マウスを動かしたとき
    public void mMoved_A_21(Point p0) {
        mMoved_m_00b(p0, LineColor.MAGENTA_5);
    }//マウスで選択できる候補点を表示する。近くに既成の点があるときはその点、無いときはマウスの位置自身が候補点となる。

    //マウスクリック----------------------------------------------------
    public void mPressed_A_21(Point p0) {
        mPressed_m_00b(p0, LineColor.MAGENTA_5);
    }


//-------------------------

//22 22 22 22 22    i_mouse_modeA==22　;コピペモード

    //マウスドラッグ----------------------------------------------------
    public void mDragged_A_21(Point p0) {
        mDragged_m_00b(p0, LineColor.MAGENTA_5);
    }

    //マウスリリース----------------------------------------------------
    public void mReleased_A_21(Point p0) {

        i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
/*
		i_egaki_dankai=0;
		p.set(camera.TV2object(p0));
		moyori_ten.set(get_moyori_ten(p));

		if(p.kyori(moyori_ten)<=d_decision_width){
			s_step[1].seta(moyori_ten);
			if(s_step[1].getnagasa()>0.00000001){
				//やりたい動作はここに書く

				double addx,addy;
				addx=-s_step[1].getbx()+s_step[1].getax();
				addy=-s_step[1].getby()+s_step[1].getay();

				Orisensyuugou ori_s_temp =new Orisensyuugou();    //セレクトされた折線だけ取り出すために使う
				ori_s_temp.setMemo(ori_s.getMemo_select_sentaku(2));//セレクトされた折線だけ取り出してori_s_tempを作る
				ori_s.del_selected_senbun_hayai();//セレクトされた折線を削除する。
				ori_s_temp.move(addx,addy);//全体を移動する

				int sousuu_old=ori_s.getsousuu();
				ori_s.addMemo(ori_s_temp.getMemo());
				int sousuu_new=ori_s.getsousuu();
				ori_s.kousabunkatu(1,sousuu_old,sousuu_old+1,sousuu_new);

				ori_s.unselect_all();
				kiroku();


			}
		}
*/
        i_drawing_stage = 0;
        p.set(camera.TV2object(p0));
        s_step[1].setA(p);
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) <= d_decision_width) {
            s_step[1].setA(closest_point);
        }
        if (s_step[1].getLength() > 0.00000001) {
            //やりたい動作はここに書く

            double addx, addy;
            addx = -s_step[1].getBX() + s_step[1].getAX();
            addy = -s_step[1].getBY() + s_step[1].getAY();

            FoldLineSet ori_s_temp = new FoldLineSet();    //セレクトされた折線だけ取り出すために使う
            ori_s_temp.setMemo(ori_s.getMemo_select_sentaku(2));//セレクトされた折線だけ取り出してori_s_tempを作る
            ori_s.del_selected_lineSegment_fast();//セレクトされた折線を削除する。
            ori_s_temp.move(addx, addy);//全体を移動する

            int sousuu_old = ori_s.getTotal();
            ori_s.addMemo(ori_s_temp.getMemo());
            int sousuu_new = ori_s.getTotal();
            ori_s.intersect_divide(1, sousuu_old, sousuu_old + 1, sousuu_new);

            ori_s.unselect_all();
            record();

            orihime_app.i_mouse_modeA = MouseMode.CREASE_SELECT_19;//20200930 add セレクトした折線に作業して、その後またセレクトできる状態に戻すための行

        }


    }

    //マウスを動かしたとき
    public void mMoved_A_22(Point p0) {
        mMoved_m_00b(p0, LineColor.MAGENTA_5);
    }//マウスで選択できる候補点を表示する。近くに既成の点があるときはその点、無いときはマウスの位置自身が候補点となる。

    //マウスクリック----------------------------------------------------
    public void mPressed_A_22(Point p0) {
        mPressed_m_00b(p0, LineColor.MAGENTA_5);
    }


//--------------------------------------------
//31 31 31 31 31 31 31 31  i_mouse_modeA==31move2p2p	入力 31 31 31 31 31 31 31 31

//動作概要　
//i_mouse_modeA==1と線分分割以外は同じ　
//

    //マウスドラッグ----------------------------------------------------
    public void mDragged_A_22(Point p0) {
        mDragged_m_00b(p0, LineColor.MAGENTA_5);
    }

    //マウスリリース----------------------------------------------------
    public void mReleased_A_22(Point p0) {

        i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
/*
		i_egaki_dankai=0;
		p.set(camera.TV2object(p0));
		moyori_ten.set(get_moyori_ten(p));

		if(p.kyori(moyori_ten)<=d_decision_width){
			s_step[1].seta(moyori_ten);
			if(s_step[1].getnagasa()>0.00000001){
				//やりたい動作はここに書く

				double addx,addy;
				addx=-s_step[1].getbx()+s_step[1].getax();
				addy=-s_step[1].getby()+s_step[1].getay();

				Orisensyuugou ori_s_temp =new Orisensyuugou();    //セレクトされた折線だけ取り出すために使う
				ori_s_temp.setMemo(ori_s.getMemo_select_sentaku(2));//セレクトされた折線だけ取り出してori_s_tempを作る
				//ori_s.del_selected_senbun_hayai();//セレクトされた折線を削除する。moveと　copyの違いはこの行が有効かどうかの違い
				ori_s_temp.move(addx,addy);//全体を移動する

				int sousuu_old=ori_s.getsousuu();
				ori_s.addMemo(ori_s_temp.getMemo());
				int sousuu_new=ori_s.getsousuu();
				ori_s.kousabunkatu(1,sousuu_old,sousuu_old+1,sousuu_new);

				ori_s.unselect_all();
				kiroku();
			}
		}
*/
        i_drawing_stage = 0;
        p.set(camera.TV2object(p0));
        s_step[1].setA(p);
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) <= d_decision_width) {
            s_step[1].setA(closest_point);
        }
        if (s_step[1].getLength() > 0.00000001) {
            //やりたい動作はここに書く

            double addx, addy;
            addx = -s_step[1].getBX() + s_step[1].getAX();
            addy = -s_step[1].getBY() + s_step[1].getAY();

            FoldLineSet ori_s_temp = new FoldLineSet();    //セレクトされた折線だけ取り出すために使う
            ori_s_temp.setMemo(ori_s.getMemo_select_sentaku(2));//セレクトされた折線だけ取り出してori_s_tempを作る
            //ori_s.del_selected_senbun_hayai();//セレクトされた折線を削除する。moveと　copyの違いはこの行が有効かどうかの違い
            ori_s_temp.move(addx, addy);//全体を移動する

            int sousuu_old = ori_s.getTotal();
            ori_s.addMemo(ori_s_temp.getMemo());
            int sousuu_new = ori_s.getTotal();
            ori_s.intersect_divide(1, sousuu_old, sousuu_old + 1, sousuu_new);

            ori_s.unselect_all();
            record();

            orihime_app.i_mouse_modeA = MouseMode.CREASE_SELECT_19;//20200930 add セレクトした折線に作業して、その後またセレクトできる状態に戻すための行
        }

    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_31(Point p0) {
        mMoved_A_11(p0);
    }//近い既存点のみ表示

    //マウス操作(i_mouse_modeA==31move2p2p　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_31(Point p0) {
        p.set(camera.TV2object(p0));

        if (i_drawing_stage == 0) {    //第1段階として、点を選択
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) < d_decision_width) {
                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_point, closest_point);
                s_step[i_drawing_stage].setColor(LineColor.MAGENTA_5);
            }
            return;
        }

        if (i_drawing_stage == 1) {    //第2段階として、点を選択
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) >= d_decision_width) {
                i_drawing_stage = 0;
                i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                //点の選択が失敗した場合もi_select_mode=0にしないと、セレクトのつもりが動作モードがmove2p2pになったままになる
                return;
            }
            if (p.distance(closest_point) < d_decision_width) {

                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_point, closest_point);
                s_step[i_drawing_stage].setColor(LineColor.fromNumber(i_drawing_stage));

            }
            if (OritaCalc.distance(s_step[1].getA(), s_step[2].getA()) < 0.00000001) {
                i_drawing_stage = 0;
                i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。

            }
            return;
        }


        if (i_drawing_stage == 2) {    //第3段階として、点を選択
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) >= d_decision_width) {
                i_drawing_stage = 0;
                i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。

                return;

            }
            if (p.distance(closest_point) < d_decision_width) {

                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_point, closest_point);
                s_step[i_drawing_stage].setColor(LineColor.fromNumber(i_drawing_stage));

            }
            return;
        }


        if (i_drawing_stage == 3) {    //第4段階として、点を選択
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) >= d_decision_width) {
                i_drawing_stage = 0;
                i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                return;
            }
            if (p.distance(closest_point) < d_decision_width) {

                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_point, closest_point);
                s_step[i_drawing_stage].setColor(LineColor.fromNumber(i_drawing_stage));

            }
            if (OritaCalc.distance(s_step[3].getA(), s_step[4].getA()) < 0.00000001) {
                i_drawing_stage = 0;
                i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。

            }
            return;
        }


    }

//  ********************************************


//--------------------------------------------
//32 32 32 32 32 32 32 32  i_mouse_modeA==32copy2p2p	入力 32 32 32 32 32 32 32 32

//動作概要　
//i_mouse_modeA==1と線分分割以外は同じ　
//

    //マウス操作(i_mouse_modeA==31move2p2p　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_31(Point p0) {
    }

    //マウス操作(i_mouse_modeA==31move2p2p　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_31(Point p0) {
        if (i_drawing_stage == 4) {
            i_drawing_stage = 0;
            i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。

            //double addx,addy;
            //addx=s_step[1].getbx()-s_step[1].getax();
            //addy=s_step[1].getby()-s_step[1].getay();


            FoldLineSet ori_s_temp = new FoldLineSet();    //セレクトされた折線だけ取り出すために使う
            ori_s_temp.setMemo(ori_s.getMemo_select_sentaku(2));//セレクトされた折線だけ取り出してori_s_tempを作る
            ori_s.del_selected_lineSegment_fast();//セレクトされた折線を削除する。
            ori_s_temp.move(s_step[1].getA(), s_step[2].getA(), s_step[3].getA(), s_step[4].getA());//全体を移動する

            int sousuu_old = ori_s.getTotal();
            ori_s.addMemo(ori_s_temp.getMemo());
            int sousuu_new = ori_s.getTotal();
            ori_s.intersect_divide(1, sousuu_old, sousuu_old + 1, sousuu_new);

            ori_s.unselect_all();
            record();
            orihime_app.i_mouse_modeA = MouseMode.CREASE_SELECT_19;//20200930 add セレクトした折線に作業して、その後またセレクトできる状態に戻すための行
        }
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_32(Point p0) {
        mMoved_A_11(p0);
    }//近い既存点のみ表示

    //マウス操作(i_mouse_modeA==32copy2p2p2p2p　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_32(Point p0) {
        p.set(camera.TV2object(p0));

        if (i_drawing_stage == 0) {    //第1段階として、点を選択
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) < d_decision_width) {
                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_point, closest_point);
                s_step[i_drawing_stage].setColor(LineColor.MAGENTA_5);
            }
            return;
        }

        if (i_drawing_stage == 1) {    //第2段階として、点を選択
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) >= d_decision_width) {
                i_drawing_stage = 0;
                i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                return;
            }
            if (p.distance(closest_point) < d_decision_width) {

                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_point, closest_point);
                s_step[i_drawing_stage].setColor(LineColor.fromNumber(i_drawing_stage));

            }
            if (OritaCalc.distance(s_step[1].getA(), s_step[2].getA()) < 0.00000001) {
                i_drawing_stage = 0;
                i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
            }
            return;
        }


        if (i_drawing_stage == 2) {    //第3段階として、点を選択
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) >= d_decision_width) {
                i_drawing_stage = 0;
                i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                return;
            }
            if (p.distance(closest_point) < d_decision_width) {

                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_point, closest_point);
                s_step[i_drawing_stage].setColor(LineColor.fromNumber(i_drawing_stage));

            }
            return;
        }


        if (i_drawing_stage == 3) {    //第4段階として、点を選択
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) >= d_decision_width) {
                i_drawing_stage = 0;
                i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                return;
            }
            if (p.distance(closest_point) < d_decision_width) {

                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_point, closest_point);
                s_step[i_drawing_stage].setColor(LineColor.fromNumber(i_drawing_stage));

            }
            if (OritaCalc.distance(s_step[3].getA(), s_step[4].getA()) < 0.00000001) {
                i_drawing_stage = 0;
                i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
            }
            return;
        }


    }

//  ********************************************

    //マウス操作(i_mouse_modeA==32copy2p2p　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_32(Point p0) {
    }

    //マウス操作(i_mouse_modeA==32copy2p2pp　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_32(Point p0) {
        if (i_drawing_stage == 4) {
            i_drawing_stage = 0;
            i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
            //double addx,addy;
            //addx=s_step[1].getbx()-s_step[1].getax();
            //addy=s_step[1].getby()-s_step[1].getay();


            FoldLineSet ori_s_temp = new FoldLineSet();    //セレクトされた折線だけ取り出すために使う
            ori_s_temp.setMemo(ori_s.getMemo_select_sentaku(2));//セレクトされた折線だけ取り出してori_s_tempを作る
            //ori_s.del_selected_senbun_hayai();//セレクトされた折線を削除する。
            ori_s_temp.move(s_step[1].getA(), s_step[2].getA(), s_step[3].getA(), s_step[4].getA());//全体を移動する

            int sousuu_old = ori_s.getTotal();
            ori_s.addMemo(ori_s_temp.getMemo());
            int sousuu_new = ori_s.getTotal();
            ori_s.intersect_divide(1, sousuu_old, sousuu_old + 1, sousuu_new);

            //ori_s.unselect_all();
            record();
            orihime_app.i_mouse_modeA = MouseMode.CREASE_SELECT_19;//20200930 add セレクトした折線に作業して、その後またセレクトできる状態に戻すための行
        }
    }

    //12 12 12 12 12    i_mouse_modeA==12　;鏡映モード
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_12(Point p0) {
        mMoved_A_11(p0);
    }//近い既存点のみ表示

    //マウス操作(i_mouse_modeA==12鏡映モード　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_12(Point p0) {

        p.set(camera.TV2object(p0));


        if (i_drawing_stage == 0) {    //第1段階として、点を選択


            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) < d_decision_width) {
                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_point, closest_point);
                s_step[i_drawing_stage].setColor(LineColor.MAGENTA_5);

                //s_step[i_egaki_dankai].set(moyori_senbun);        s_step[i_egaki_dankai].setcolor(5);

            }
            return;
        }

        if (i_drawing_stage == 1) {    //第2段階として、点を選択
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) >= d_decision_width) {
                i_drawing_stage = 0;
                i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                return;
            }
            if (p.distance(closest_point) < d_decision_width) {

                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_point, closest_point);
                s_step[i_drawing_stage].setColor(LineColor.fromNumber(i_drawing_stage));
                s_step[1].setB(s_step[2].getB());
            }
            if (s_step[1].getLength() < 0.00000001) {
                i_drawing_stage = 0;
                i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
            }
        }

/*


		moyori_ten.set(get_moyori_ten(p));
		if(p.kyori(moyori_ten)>d_decision_width){i_egaki_dankai=0;}
		s_step[1].set(p,moyori_ten);s_step[1].setcolor(icol);
		//k.addsenbun(p,p);
		//ieda=k.getsousuu();
		//k.setcolor(ieda,icol);
*/
    }

    //Ten p =new Ten(); p.set(camera.TV2object(p0));
    //moyori_ten.set(get_moyori_ten(p));
    //s_step[1].seta(moyori_ten);
    //if(p.kyori(moyori_ten)<=d_decision_width){
    //	if(s_step[1].getnagasa()>0.00000001){

    //addsenbun(adds);
    //ieda=ori_s.getsousuu();
    //ori_s.setcolor(ieda,icol); qqqqqqqqq
    //ori_s.kousabunkatu_symple();
    //ori_s.kousabunkatu();ori_s.kousabunkatu_symple();


    //}
    //kiroku();
    //}

    //マウス操作(i_mouse_modeA==12鏡映モード　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_12(Point p0) {

        //Ten p =new Ten(); p.set(camera.TV2object(p0));
        //s_step[1].seta(p);

        //k.seta(ieda, p);
    }

//34 34 34 34 34 34 34 34 34 34 34入力した線分に重複している折線を順に山谷にする

    //マウス操作(i_mouse_modeA==12鏡映モード　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_12(Point p0) {
        LineSegment adds = new LineSegment();
        //Orisensyuugou ori_s_temp =new Orisensyuugou();
        if (i_drawing_stage == 2) {
            i_drawing_stage = 0;
            i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
            int old_sousuu = ori_s.getTotal();

            for (int i = 1; i <= ori_s.getTotal(); i++) {
                if (ori_s.get_select(i) == 2) {
                    //Senbun adds =new Senbun();
                    adds.set(OritaCalc.sentaisyou_lineSegment_motome(ori_s.get(i), s_step[1]));
                    adds.setColor(ori_s.getColor(i));
                    //addsenbun(adds);

                    //ori_s_temp.addsenbun(adds);


                    //ori_s_temp.addsenbun(adds.geta(),adds.getb());
                    //ori_s_temp.setcolor(ori_s.getsousuu(),ori_s.getcolor(i));

                    ori_s.addLine(adds.getA(), adds.getB());
                    ori_s.setColor(ori_s.getTotal(), ori_s.getColor(i));
                }
            }

            int new_sousuu = ori_s.getTotal();

            ori_s.intersect_divide(1, old_sousuu, old_sousuu + 1, new_sousuu);
/*

			for (int i=1; i<=ori_s_temp.getsousuu(); i++ ){
				adds.set(	ori_s_temp.get(i));
				adds.setcolor(	ori_s_temp.getcolor(i));
				addsenbun(adds);
			}


*/


            ori_s.unselect_all();
            record();
            orihime_app.i_mouse_modeA = MouseMode.CREASE_SELECT_19;//20200930 add セレクトした折線に作業して、その後またセレクトできる状態に戻すための行
        }
    }

    //-------------------------
    public void del_selected_senbun() {
        //ori_s.del_selected_senbun();
        ori_s.del_selected_lineSegment_fast();
        //Memo memo_temp = new Memo();memo_temp.set(ori_s.getMemo_select_jyogai(2));
        //ori_s.reset();
        //ori_s.setMemo(memo_temp);

    }

    public void mMoved_A_34(Point p0) {
        mMoved_A_11(p0);
    }//近い既存点のみ表示
/*
	public void mDragged_A_34(Ten p0) {

		Ten p =new Ten(); p.set(camera.TV2object(p0));
		s_step[1].seta(p);

		//k.seta(ieda, p);
	}
*/

    //マウス操作(i_mouse_modeA==34　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_34(Point p0) {
        i_drawing_stage = 1;

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) > d_decision_width) {
            i_drawing_stage = 0;
        }
        s_step[1].set(p, closest_point);
        s_step[1].setColor(icol);
        //k.addsenbun(p,p);
        //ieda=k.getsousuu();
        //k.setcolor(ieda,icol);
    }


//64 64 64 64 64 64 64 64 64 64 64 64 64入力した線分に重複している折線を削除する

    //マウス操作(i_mouse_modeA==34　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_34(Point p0) {
        mDragged_A_11(p0);
    }

    //マウス操作(i_mouse_modeA==34　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_34(Point p0) {

        SortingBox_int_double nbox = new SortingBox_int_double();

        if (i_drawing_stage == 1) {
            i_drawing_stage = 0;
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            closest_point.set(getClosestPoint(p));
            s_step[1].setA(closest_point);
            if (p.distance(closest_point) <= d_decision_width) {
                if (s_step[1].getLength() > 0.00000001) {
                    for (int i = 1; i <= ori_s.getTotal(); i++) {
						/*
						int i_senbun_kousa_hantei=oc.senbun_kousa_hantei(ori_s.get(i),s_step[1],0.0001,0.0001);
						int i_jikkou=0;
						if(i_senbun_kousa_hantei== 31 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 321 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 322 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 331 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 332 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 341 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 342 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 351 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 352 ){ i_jikkou=1;}

						if(i_senbun_kousa_hantei== 361 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 362 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 363 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 364 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 371 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 372 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 373 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 374 ){ i_jikkou=1;}
					*/
                        //if( i_jikkou==1){
                        if (OritaCalc.lineSegmentoverlapping(ori_s.get(i), s_step[1])) {
                            int_double i_d = new int_double(i, OritaCalc.distance_lineSegment(s_step[1].getB(), ori_s.get(i)));
                            nbox.container_i_smallest_first(i_d);
                        }

                    }

                    //System.out.println("i_d_sousuu"+nbox.getsousuu());

                    LineColor icol_temp = icol;

                    for (int i = 1; i <= nbox.getTotal(); i++) {

                        ori_s.setColor(nbox.getInt(i), icol_temp);


                        if (icol_temp == LineColor.RED_1) {
                            icol_temp = LineColor.BLUE_2;
                        } else if (icol_temp == LineColor.BLUE_2) {
                            icol_temp = LineColor.RED_1;
                        }
                    }


                    record();

                }
            }
        }

    }

    public void mMoved_A_64(Point p0) {
        mMoved_A_11(p0);
    }//近い既存点のみ表示

    //マウス操作(i_mouse_modeA==64　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_64(Point p0) {
        i_drawing_stage = 1;

        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) > d_decision_width) {
            i_drawing_stage = 0;
        }
        s_step[1].set(p, closest_point);
        s_step[1].setColor(LineColor.MAGENTA_5);

    }


//65 65 65 65 65 65 65 65 65 65 65 65 65入力した線分に重複している折線やX交差している折線を削除する

    //マウス操作(i_mouse_modeA==64　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_64(Point p0) {
        mDragged_A_11(p0);
    }

    //マウス操作(i_mouse_modeA==64　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_64(Point p0) {

        SortingBox_int_double nbox = new SortingBox_int_double();

        if (i_drawing_stage == 1) {
            i_drawing_stage = 0;
            p.set(camera.TV2object(p0));
            closest_point.set(getClosestPoint(p));
            s_step[1].setA(closest_point);
            if (p.distance(closest_point) <= d_decision_width) {
                if (s_step[1].getLength() > 0.00000001) {

                    ori_s.D_nisuru_line(s_step[1], "l");//lは小文字のエル

                    record();

                }
            }
        }

    }

    //マウスを動かしたとき
    public void mMoved_A_65(Point p0) {
        mMoved_m_00b(p0, LineColor.MAGENTA_5);
    }//マウスで選択できる候補点を表示する。近くに既成の点があるときはその点、無いときはマウスの位置自身が候補点となる。

    //マウスクリック----------------------------------------------------
    public void mPressed_A_65(Point p0) {
        mPressed_m_00b(p0, LineColor.MAGENTA_5);
    }

//----------------------------------------------------------------------------------------
//多角形を入力(既存頂点への引き寄せあるが既存頂点が遠い場合は引き寄せ無し)し、何らかの作業を行うセット
    //マウス操作(マウスを動かしたとき)を行う関数

    //マウスドラッグ----------------------------------------------------
    public void mDragged_A_65(Point p0) {
        mDragged_m_00b(p0, LineColor.MAGENTA_5);
    }

    //マウス操作(i_mouse_modeA==65　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_65(Point p0) {

        i_drawing_stage = 0;
        p.set(camera.TV2object(p0));

        s_step[1].setA(p);
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) <= d_decision_width) {
            s_step[1].setA(closest_point);
        }
        if (s_step[1].getLength() > 0.00000001) {
            //やりたい動作はここに書く
            ori_s.D_nisuru_line(s_step[1], "lX");//lXは小文字のエルと大文字のエックス
            record();
        }
    }

    public void mMoved_takakukei_and_sagyou(Point p0) {

        //mMoved_m_002(p0,5);

        //マウス操作(マウスを動かしたとき)を行う関数
//	public void mMoved_m_002(Ten p0,int i_c) //マウスで選択できる候補点を表示する。近くに既成の点があるときはその点、無いときはマウスの位置自身が候補点となる。
        if (i_kou_mitudo_nyuuryoku) {
            s_kouho[1].setActive(3);
            p.set(camera.TV2object(p0));
            i_candidate_stage = 1;
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) > p.distance(s_step[1].getA())) {
                closest_point.set(s_step[1].getA());
            }

            if (p.distance(closest_point) < d_decision_width) {
                s_kouho[1].set(closest_point, closest_point);
            } else {
                s_kouho[1].set(p, p);
            }

            s_kouho[1].setColor(LineColor.MAGENTA_5);
            //return;
        }

    }

    //マウス操作(ドラッグしたとき)を行う関数----------------------------------------------------

    //マウス操作(ボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_takakukei_and_sagyou(Point p0) {
//i_egaki_dankai==0なのはこの操作ボタンを押した直後の段階か、多角形が完成して、その後ボタンを押した後
        if (i_takakukei_kansei == 1) {
            i_takakukei_kansei = 0;
            i_drawing_stage = 0;
        }

        i_drawing_stage = i_drawing_stage + 1;
        s_step[i_drawing_stage].setColor(LineColor.MAGENTA_5);
        p.set(camera.TV2object(p0));

        if (i_drawing_stage == 1) {
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) > d_decision_width) {
                closest_point.set(p);
            }
            s_step[i_drawing_stage].set(closest_point, p);

        } else {//ここでi_egaki_dankai=0となることはない。
            s_step[i_drawing_stage].set(s_step[i_drawing_stage - 1].getB(), p);
        }
    }

    public void mDragged_takakukei_and_sagyou(Point p0) {
        //if(i_takakukei_kansei==0)//ここにくるときは必ずi_takakukei_kansei==0なのでif分は無意味

        p.set(camera.TV2object(p0));
        s_step[i_drawing_stage].setB(p);


        if (i_kou_mitudo_nyuuryoku) {
            i_candidate_stage = 1;
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) > p.distance(s_step[1].getA())) {
                closest_point.set(s_step[1].getA());
            }


            if (p.distance(closest_point) < d_decision_width) {
                s_kouho[1].set(closest_point, closest_point);
            } else {
                s_kouho[1].set(p, p);
            }

            //s_kouho[i_egaki_dankai].setcolor(icol);
            s_step[i_drawing_stage].setB(s_kouho[1].getA());
        }


//mDragged_m_00b(Ten p0,int i_c)
    }


//20201024高密度入力がオンならばapのrepaint（画面更新）のたびにTen kus_sisuu=new Ten(es1.get_moyori_ten_sisuu(p_mouse_TV_iti));で最寄り点を求めているので、この描き職人内で別途最寄り点を求めていることは二度手間になっている。

    //マウス操作(ボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_takakukei_and_sagyou(Point p0, int i_mode) {
        //i_candidate_stage=0;
        //if(i_takakukei_kansei==0){//ここにくるときは必ずi_takakukei_kansei==0なのでif分は無意味

        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) > d_decision_width) {
            closest_point.set(p);
        }

        s_step[i_drawing_stage].setB(closest_point);


        if (i_drawing_stage >= 2) {
            if (p.distance(s_step[1].getA()) <= d_decision_width) {
                s_step[i_drawing_stage].setB(s_step[1].getA());
                //i_O_F_C=1;
                i_takakukei_kansei = 1;
            }
        }

        //if(i_O_F_C==1){if(i_egaki_dankai==2){
        //i_egaki_dankai=0;
        //}}
        //}

        //int i_tekisetu=1;//外周部の形状が適切なら1、適切でないなら0
        //if(i_O_F_C==1){
        if (i_takakukei_kansei == 1) {
            Polygon Taka = new Polygon(i_drawing_stage);
            for (int i = 1; i <= i_drawing_stage; i++) {
                Taka.set(i, s_step[i].getA());
            }


            //各動作モードで独自に行う作業は以下に条件分けして記述する
            if (i_mode == 66) {
                ori_s.select_Takakukei(Taka, "select");
            }//66 66 66 66 66 多角形を入力し、それに全体が含まれる折線をselectする
            if (i_mode == 67) {
                ori_s.select_Takakukei(Taka, "unselect");
            }//67 67 67 67 67 多角形を入力し、それに全体が含まれる折線を折線をunselectする
            //各動作モードで独自に行う作業はここまで

            //i_egaki_dankai=0;
        }
    }

    //66 66 66 66 66 多角形を入力し、それに全体が含まれる折線をselectする
    public void mMoved_A_66(Point p0) {
        mMoved_takakukei_and_sagyou(p0);
    }    //マウス操作(マウスを動かしたとき)を行う関数

    public void mPressed_A_66(Point p0) {
        mPressed_takakukei_and_sagyou(p0);
    }    //マウス操作でボタンを押したとき)時の作業----------------------------------------------------

    public void mDragged_A_66(Point p0) {
        mDragged_takakukei_and_sagyou(p0);
    }    //マウス操作(ドラッグしたとき)を行う関数----------------------------------------------------

    public void mReleased_A_66(Point p0) {
        mReleased_takakukei_and_sagyou(p0, 66);
    }    //マウス操作(ボタンを離したとき)を行う関数----------------------------------------------------

    //67 67 67 67 67 多角形を入力し、それに全体が含まれる折線を折線をunselectする
    public void mMoved_A_67(Point p0) {
        mMoved_takakukei_and_sagyou(p0);
    }    //マウス操作(マウスを動かしたとき)を行う関数

    public void mPressed_A_67(Point p0) {
        mPressed_takakukei_and_sagyou(p0);
    }    //マウス操作でボタンを押したとき)時の作業----------------------------------------------------

    public void mDragged_A_67(Point p0) {
        mDragged_takakukei_and_sagyou(p0);
    }    //マウス操作(ドラッグしたとき)を行う関数----------------------------------------------------


//68 68 68 68 68 入力した線分に重複している折線やX交差している折線をselectする

    public void mReleased_A_67(Point p0) {
        mReleased_takakukei_and_sagyou(p0, 67);
    }    //マウス操作(ボタンを離したとき)を行う関数----------------------------------------------------

    //マウスを動かしたとき
    public void mMoved_A_68(Point p0) {
        mMoved_m_00b(p0, LineColor.MAGENTA_5);
    }//マウスで選択できる候補点を表示する。近くに既成の点があるときはその点、無いときはマウスの位置自身が候補点となる。

    //マウスクリック----------------------------------------------------
    public void mPressed_A_68(Point p0) {
        mPressed_m_00b(p0, LineColor.MAGENTA_5);
    }

    //マウスドラッグ----------------------------------------------------
    public void mDragged_A_68(Point p0) {
        mDragged_m_00b(p0, LineColor.MAGENTA_5);
    }


//69 69 69 69 69 入力した線分に重複している折線やX交差している折線をunselectする

    //マウス操作でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_68(Point p0) {

        //i_egaki_dankai=0;
        p.set(camera.TV2object(p0));

        s_step[1].setA(p);
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) <= d_decision_width) {
            s_step[1].setA(closest_point);
        }
        if (s_step[1].getLength() > 0.00000001) {
            //やりたい動作はここに書く
            ori_s.select_lX(s_step[1], "select_lX");//lXは小文字のエルと大文字のエックス

        }

    }

    //マウスを動かしたとき
    public void mMoved_A_69(Point p0) {
        mMoved_m_00b(p0, LineColor.MAGENTA_5);
    }//マウスで選択できる候補点を表示する。近くに既成の点があるときはその点、無いときはマウスの位置自身が候補点となる。

    //マウスクリック----------------------------------------------------
    public void mPressed_A_69(Point p0) {
        mPressed_m_00b(p0, LineColor.MAGENTA_5);
    }

    //マウスドラッグ----------------------------------------------------
    public void mDragged_A_69(Point p0) {
        mDragged_m_00b(p0, LineColor.MAGENTA_5);
    }


//36 36 36 36 36 36 36 36 36 36 36入力した線分にX交差している折線を順に山谷にする

    //マウス操作でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_69(Point p0) {

        //i_egaki_dankai=0;
        p.set(camera.TV2object(p0));

        s_step[1].setA(p);
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) <= d_decision_width) {
            s_step[1].setA(closest_point);
        }
        if (s_step[1].getLength() > 0.00000001) {
            //やりたい動作はここに書く
            ori_s.select_lX(s_step[1], "unselect_lX");//lXは小文字のエルと大文字のエックス

        }

    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_36(Point p0) {
        mMoved_A_28(p0);
    }//近い既存点のみ表示

    //マウス操作(i_mouse_modeA==36　でドラッグしたとき)を行う関数----------------------------------------------------

    //マウス操作(i_mouse_modeA==36　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_36(Point p0) {
        i_drawing_stage = 1;

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) > d_decision_width) {
            closest_point.set(p);
        }
        s_step[1].set(p, closest_point);
        s_step[1].setColor(icol);
        //k.addsenbun(p,p);
        //ieda=k.getsousuu();
        //k.setcolor(ieda,icol);
    }
/*
	public void mDragged_A_36(Ten p0) {

		Ten p =new Ten(); p.set(camera.TV2object(p0));
		s_step[1].seta(p);

		//k.seta(ieda, p);
	}
*/

    public void mDragged_A_36(Point p0) {
        mDragged_A_28(p0);
    }


//63 63 63 外周部の折り畳みチェック


    //マウス操作(マウスを動かしたとき)を行う関数
    //public void mMoved_A_63(Ten p0) {mMoved_A_01(p0);}//近い既存点のみ表示

    //マウス操作(i_mouse_modeA==36　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_36(Point p0) {

        SortingBox_int_double nbox = new SortingBox_int_double();

        if (i_drawing_stage == 1) {
            i_drawing_stage = 0;
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) > d_decision_width) {
                closest_point.set(p);
            }
            s_step[1].setA(closest_point);
            //if(p.kyori(moyori_ten)<=d_decision_width){
            if (s_step[1].getLength() > 0.00000001) {
                for (int i = 1; i <= ori_s.getTotal(); i++) {
                    LineSegment.Intersection i_senbun_kousa_hantei = OritaCalc.line_intersect_decide(ori_s.get(i), s_step[1], 0.0001, 0.0001);
                    int i_jikkou = 0;
                    if (i_senbun_kousa_hantei == LineSegment.Intersection.INTERSECTS_1) {
                        i_jikkou = 1;
                    }
                    if (i_senbun_kousa_hantei == LineSegment.Intersection.INTERSECTS_TSHAPE_S2_VERTICAL_BAR_27) {
                        i_jikkou = 1;
                    }
                    if (i_senbun_kousa_hantei == LineSegment.Intersection.INTERSECTS_TSHAPE_S2_VERTICAL_BAR_28) {
                        i_jikkou = 1;
                    }
                    //if(i_senbun_kousa_hantei== 31 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 321 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 322 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 331 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 332 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 341 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 342 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 351 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 352 ){ i_jikkou=1;}

                    //if(i_senbun_kousa_hantei== 361 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 362 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 363 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 364 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 371 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 372 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 373 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 374 ){ i_jikkou=1;}

                    if (i_jikkou == 1) {
                        int_double i_d = new int_double(i, OritaCalc.distance(s_step[1].getB(), OritaCalc.findIntersection(ori_s.get(i), s_step[1])));
                        nbox.container_i_smallest_first(i_d);
                    }

                }

                System.out.println("i_d_sousuu" + nbox.getTotal());

                LineColor icol_temp = icol;

                for (int i = 1; i <= nbox.getTotal(); i++) {

                    ori_s.setColor(nbox.getInt(i), icol_temp);


                    if (icol_temp == LineColor.RED_1) {
                        icol_temp = LineColor.BLUE_2;
                    } else if (icol_temp == LineColor.BLUE_2) {
                        icol_temp = LineColor.RED_1;
                    }
                }


                record();

            }
            //}
        }

    }


//icol=3 cyan
//icol=4 orange
//icol=5 mazenta
//icol=6 green
//icol=7 yellow

    public void mMoved_A_63(Point p0) {
		/* if(i_kou_mitudo_nyuuryoku==1){s_kouho[1].setiactive(3);

			p.set(camera.TV2object(p0));
			i_candidate_stage=1;
			moyori_ten.set(get_moyori_ten(p));

			if(p.kyori(moyori_ten)<d_decision_width){  s_kouho[1].set(moyori_ten,moyori_ten);}
			else{					s_kouho[1].set(p,p);}

			//s_kouho[1].setcolor(icol);
			if(i_foldLine_additional==0){s_kouho[1].setcolor(icol);}
			if(i_foldLine_additional==1){s_kouho[1].setcolor(h_icol);}

			return;
		}
*/
    }


    //マウス操作(i_mouse_modeA==63　でドラッグしたとき)を行う関数----------------------------------------------------

    //マウス操作(i_mouse_modeA==63　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_63(Point p0) {
        if (i_drawing_stage == 0) {
            i_O_F_C = false;
            i_drawing_stage = i_drawing_stage + 1;

            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            //moyori_ten.set(get_moyori_ten(p));
            //if(p.kyori(moyori_ten)>d_decision_width){moyori_ten.set(p);}
            s_step[i_drawing_stage].set(p, p);
            s_step[i_drawing_stage].setColor(LineColor.YELLOW_7);
            //k.addsenbun(p,p);
            //ieda=k.getsousuu();
            //k.setcolor(ieda,icol);
        } else {
            if (!i_O_F_C) {
                i_drawing_stage = i_drawing_stage + 1;
                p.set(camera.TV2object(p0));
                s_step[i_drawing_stage].set(s_step[i_drawing_stage - 1].getB(), p);
                s_step[i_drawing_stage].setColor(LineColor.YELLOW_7);
                //   s_step[i_egaki_dankai-1].getb();
                //k.addsenbun(p,p);
                //ieda=k.getsousuu();
                //k.setcolor(ieda,icol);
            }
        }

    }

/*
	public void mDragged_A_36(Ten p0) {

		Ten p =new Ten(); p.set(camera.TV2object(p0));
		s_step[1].seta(p);

		//k.seta(ieda, p);
	}
*/

    public void mDragged_A_63(Point p0) {
        if (!i_O_F_C) {
            p.set(camera.TV2object(p0));
            s_step[i_drawing_stage].setB(p);
        }
    }


//--------------------------------------------------------------------------------
//13 13 13 13 13 13    i_mouse_modeA==13　;角度系モード//線分指定、交点まで

    //マウス操作(i_mouse_modeA==63　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_63(Point p0) {


        if (!i_O_F_C) {
            p.set(camera.TV2object(p0));
            s_step[i_drawing_stage].setB(p);


            if (p.distance(s_step[1].getA()) <= d_decision_width) {
                s_step[i_drawing_stage].setB(s_step[1].getA());
                i_O_F_C = true;
                //System.out.println("i_egaki_dankai = " + i_egaki_dankai );
                //System.out.println("i_O_F_C = " +i_O_F_C );
            }


            if (i_O_F_C) {
                if (i_drawing_stage == 2) {
                    i_drawing_stage = 0;
                }
            }


        }

        int i_tekisetu = 1;//外周部の黄色い線と外周部の全折線の交差が適切（全てX型の交差）なら1、1つでも適切でないなら0
        if (i_O_F_C) {
            SortingBox_int_double goukei_nbox = new SortingBox_int_double();
            SortingBox_int_double nbox = new SortingBox_int_double();
            for (int i_s_step = 1; i_s_step <= i_drawing_stage; i_s_step++) {
                nbox.reset();
                for (int i = 1; i <= ori_s.getTotal(); i++) {

                    LineSegment.Intersection i_senbun_kousa_hantei = OritaCalc.line_intersect_decide(ori_s.get(i), s_step[i_s_step], 0.0001, 0.0001);
                    int i_jikkou = 0;

                    if ((i_senbun_kousa_hantei != LineSegment.Intersection.NO_INTERSECTION_0) && (i_senbun_kousa_hantei != LineSegment.Intersection.INTERSECTS_1)) {
                        i_tekisetu = 0;
                    }

                    if (i_senbun_kousa_hantei == LineSegment.Intersection.INTERSECTS_1) {
                        i_jikkou = 1;
                    }
                    //if(i_senbun_kousa_hantei== 27 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 28 ){ i_jikkou=1;}

                    if (ori_s.get(i).getColor().getNumber() >= 3) {
                        i_jikkou = 0;
                    }


                    if (i_jikkou == 1) {
                        int_double i_d = new int_double(i, OritaCalc.distance(s_step[i_s_step].getA(), OritaCalc.findIntersection(ori_s.get(i), s_step[i_s_step])));
                        nbox.container_i_smallest_first(i_d);
                    }
                }


                for (int i = 1; i <= nbox.getTotal(); i++) {
                    int_double i_d = new int_double(nbox.getInt(i), goukei_nbox.getTotal());
                    goukei_nbox.container_i_smallest_first(i_d);
                }


            }
            System.out.println(" --------------------------------");

            //if (i_tekisetu==0){	JOptionPane.showMessageDialog(null, "Message");    }
            if (i_tekisetu == 1) {

                LineColor i_hantai_color = LineColor.MAGENTA_5;//判定結果を表す色番号。5（マゼンタ、赤紫）は折畳不可。3（シアン、水色）は折畳可。

                if (goukei_nbox.getTotal() % 2 != 0) {//外周部として選択した折線の数が奇数
                    i_hantai_color = LineColor.MAGENTA_5;
                } else if (goukei_nbox.getTotal() == 0) {//外周部として選択した折線の数が0
                    i_hantai_color = LineColor.CYAN_3;
                } else {//外周部として選択した折線の数が偶数
                    LineSegment s_idou = new LineSegment();
                    s_idou.set(ori_s.get(goukei_nbox.getInt(1)));

                    for (int i = 2; i <= goukei_nbox.getTotal(); i++) {
                        //System.out.println(" i = "+i+"    Line No = " +goukei_nbox.get_int(i));
                        s_idou.set(OritaCalc.sentaisyou_lineSegment_motome(s_idou, ori_s.get(goukei_nbox.getInt(i))));
                    }
                    i_hantai_color = LineColor.MAGENTA_5;
                    if (OritaCalc.equal(ori_s.get(goukei_nbox.getInt(1)).getA(), s_idou.getA(), 0.0001)) {
                        if (OritaCalc.equal(ori_s.get(goukei_nbox.getInt(1)).getB(), s_idou.getB(), 0.0001)) {
                            i_hantai_color = LineColor.CYAN_3;
                        }
                    }
                }

                for (int i_s_step = 1; i_s_step <= i_drawing_stage; i_s_step++) {
                    s_step[i_s_step].setColor(i_hantai_color);
                }

                System.out.println(" --------------------------------");
            }
        }
//20201010

/*


		if(i_egaki_dankai==1000){
			i_egaki_dankai=0;
			//Ten p =new Ten();
			p.set(camera.TV2object(p0));
			moyori_ten.set(get_moyori_ten(p));
			if(p.kyori(moyori_ten)>d_decision_width){moyori_ten.set(p);}
			s_step[1].seta(moyori_ten);
			//if(p.kyori(moyori_ten)<=d_decision_width){
				if(s_step[1].getnagasa()>0.00000001){
					for (int i=1; i<=ori_s.getsousuu(); i++ ){
						int i_senbun_kousa_hantei=oc.senbun_kousa_hantei(ori_s.get(i),s_step[1],0.0001,0.0001);
						int i_jikkou=0;
						if(i_senbun_kousa_hantei== 1 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 27 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 28 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 31 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 321 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 322 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 331 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 332 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 341 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 342 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 351 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 352 ){ i_jikkou=1;}

						//if(i_senbun_kousa_hantei== 361 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 362 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 363 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 364 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 371 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 372 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 373 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 374 ){ i_jikkou=1;}

						if( i_jikkou==1){
							int_double i_d	=new int_double(i,oc.kyori(s_step[1].getb(),oc.kouten_motome(ori_s.get(i),s_step[1])   ) );
							nbox.ire_i_tiisaijyun(i_d);
						}

					}

					System.out.println("i_d_sousuu"+nbox.getsousuu());

					int icol_temp=icol;

					for (int i=1; i<=nbox.getsousuu(); i++ ){

						ori_s.setcolor(nbox.get_int(i),icol_temp);


						if(icol_temp==1){icol_temp=2;
						}else if(icol_temp==2){icol_temp=1;
						}
					}




					kiroku();

				}
			//}
		}
*/
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_13(Point p0) {

        int honsuu = 0;//1つの端点周りに描く線の本数
        if (id_kakudo_kei != 0) {
            honsuu = id_kakudo_kei * 2 - 1;
        } else if (id_kakudo_kei == 0) {
            honsuu = 6;
        }

        int i_jyunnbi_step_suu = 1;//動作の準備として人間が選択するステップ数

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));

        if (i_drawing_stage == 0) {    //第１段階として、線分を選択
            closest_lineSegment.set(get_moyori_senbun(p));
            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) < d_decision_width) {
                i_drawing_stage = 1;
                s_step[1].set(closest_lineSegment);
                s_step[1].setColor(LineColor.MAGENTA_5);
            }
        }

        if (i_drawing_stage == i_jyunnbi_step_suu) {    //if(i_egaki_dankai==1){        //動作の準備として人間が選択するステップ数が終わった状態で実行
            int i_jyun;//i_jyunは線を描くとき順番に色を変えたいとき使う
            //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d) //    double d_kakudo_kei;double kakudo;

            if (id_kakudo_kei != 0) {
                d_kakudo_kei = 180.0 / (double) id_kakudo_kei;
            } else if (id_kakudo_kei == 0) {
                d_kakudo_kei = 180.0 / 4.0;
            }

            if (id_kakudo_kei != 0) {
                LineSegment s_kiso = new LineSegment(s_step[1].getA(), s_step[1].getB());
                kakudo = 0.0;
                i_jyun = 0;
                for (int i = 1; i <= honsuu; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }
                    i_drawing_stage = i_drawing_stage + 1;
                    kakudo = kakudo + d_kakudo_kei;
                    s_step[i_drawing_stage].set(OritaCalc.lineSegment_rotate(s_kiso, kakudo, 10.0));
                    if (i_jyun == 0) {
                        s_step[i_drawing_stage].setColor(LineColor.GREEN_6);
                    }
                    if (i_jyun == 1) {
                        s_step[i_drawing_stage].setColor(LineColor.ORANGE_4);
                    }
                }

                s_kiso.set(s_step[1].getB(), s_step[1].getA());
                kakudo = 0.0;
                i_jyun = 0;
                for (int i = 1; i <= honsuu; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }
                    i_drawing_stage = i_drawing_stage + 1;
                    kakudo = kakudo + d_kakudo_kei;
                    s_step[i_drawing_stage].set(OritaCalc.lineSegment_rotate(s_kiso, kakudo, 10.0));
                    if (i_jyun == 0) {
                        s_step[i_drawing_stage].setColor(LineColor.GREEN_6);
                    }
                    if (i_jyun == 1) {
                        s_step[i_drawing_stage].setColor(LineColor.ORANGE_4);
                    }
                }
            }
            if (id_kakudo_kei == 0) {
                double[] jk = new double[7];
                jk[0] = 0.0;
                jk[1] = d_jiyuu_kaku_2;
                jk[2] = d_jiyuu_kaku_1;
                jk[3] = d_jiyuu_kaku_3;
                jk[4] = 360.0 - d_jiyuu_kaku_2;
                jk[5] = 360.0 - d_jiyuu_kaku_1;
                jk[6] = 360.0 - d_jiyuu_kaku_3;

                LineSegment s_kiso = new LineSegment(s_step[1].getA(), s_step[1].getB());
                kakudo = 0.0;
                i_jyun = 0;
                for (int i = 1; i <= 6; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }
                    i_drawing_stage = i_drawing_stage + 1;
                    kakudo = jk[i];
                    s_step[i_drawing_stage].set(OritaCalc.lineSegment_rotate(s_kiso, kakudo, 10.0));
                    if (i == 1) {
                        s_step[i_drawing_stage].setColor(LineColor.GREEN_6);
                    }
                    if (i == 2) {
                        s_step[i_drawing_stage].setColor(LineColor.PURPLE_8);
                    }
                    if (i == 3) {
                        s_step[i_drawing_stage].setColor(LineColor.ORANGE_4);
                    }
                    if (i == 4) {
                        s_step[i_drawing_stage].setColor(LineColor.ORANGE_4);
                    }
                    if (i == 5) {
                        s_step[i_drawing_stage].setColor(LineColor.GREEN_6);
                    }
                    if (i == 6) {
                        s_step[i_drawing_stage].setColor(LineColor.PURPLE_8);
                    }
                }

                s_kiso.set(s_step[1].getB(), s_step[1].getA());
                kakudo = 0.0;
                i_jyun = 0;
                for (int i = 1; i <= 6; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }
                    i_drawing_stage = i_drawing_stage + 1;
                    kakudo = jk[i];
                    s_step[i_drawing_stage].set(OritaCalc.lineSegment_rotate(s_kiso, kakudo, 10.0));
                    if (i == 1) {
                        s_step[i_drawing_stage].setColor(LineColor.GREEN_6);
                    }
                    if (i == 2) {
                        s_step[i_drawing_stage].setColor(LineColor.PURPLE_8);
                    }
                    if (i == 3) {
                        s_step[i_drawing_stage].setColor(LineColor.ORANGE_4);
                    }
                    if (i == 4) {
                        s_step[i_drawing_stage].setColor(LineColor.ORANGE_4);
                    }
                    if (i == 5) {
                        s_step[i_drawing_stage].setColor(LineColor.GREEN_6);
                    }
                    if (i == 6) {
                        s_step[i_drawing_stage].setColor(LineColor.PURPLE_8);
                    }
                }
            }


            return;
        }


        if (i_drawing_stage == i_jyunnbi_step_suu + (honsuu) + (honsuu)) {//19     //動作の準備としてソフトが返答するステップ数が終わった状態で実行

            int i_tikai_s_step_suu = 0;

            //s_step[2から10]までとs_step[11から19]まで
            closest_lineSegment.set(get_moyori_step_senbun(p, 2, 1 + (honsuu)));
            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) < d_decision_width) {
                i_drawing_stage = i_drawing_stage + 1;
                i_tikai_s_step_suu = i_tikai_s_step_suu + 1;
                s_step[i_drawing_stage].set(closest_lineSegment);    //s_step[i_egaki_dankai].setcolor(2);//s_step[20]にinput
            }

            //s_step[2から10]までとs_step[11から19]まで
            closest_lineSegment.set(get_moyori_step_senbun(p, 1 + (honsuu) + 1, 1 + (honsuu) + (honsuu)));
            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) < d_decision_width) {
                i_drawing_stage = i_drawing_stage + 1;
                i_tikai_s_step_suu = i_tikai_s_step_suu + 1;
                s_step[i_drawing_stage].set(closest_lineSegment);    //s_step[i_egaki_dankai].setcolor(icol);
            }

            if (i_tikai_s_step_suu == 2) { //この段階でs_stepが[21]までうまってたら、s_step[20]とs_step[21]は共に加える折線なので、ここで処理を終えてしまう。
                //=     1+ (honsuu) +(honsuu) +  2 ){i_egaki_dankai=0; //この段階でs_stepが[21]までうまってたら、s_step[20]とs_step[21]は共に加える折線なので、ここで処理を終えてしまう。
                //例外処理としてs_step[20]とs_step[21]が平行の場合、より近いほうをs_stepが[20]とし、s_stepを[20]としてリターン（この場合はまだ処理は終われない）。
                //２つの線分が平行かどうかを判定する関数。oc.heikou_hantei(Tyokusen t1,Tyokusen t2)//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
                //0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する

                if (OritaCalc.parallel_judgement(s_step[i_drawing_stage - 1], s_step[i_drawing_stage], 0.1) != OritaCalc.ParallelJudgement.NOT_PARALLEL) {//ここは安全を見て閾値を0.1と大目にとっておこのがよさそう

                    //s_step[20]とs_step[21]と点pの距離  //public double kyori_senbun(Ten p0,Senbun s)
                    //if(oc.kyori_senbun(p, s_step[1+     (honsuu) +(honsuu)   +1]) >  oc.kyori_senbun(p, s_step[1+     (honsuu) +(honsuu)   +1+1])          ){
                    //     s_step[1+     (honsuu) +(honsuu)   +1].set(  s_step[1+     (honsuu) +(honsuu)   +1+1]                   )    ;
                    //}

                    //i_egaki_dankai=i_egaki_dankai-1;
                    //i_egaki_dankai=i_egaki_dankai-2;
                    i_drawing_stage = 0;
                    return;
                }


                i_drawing_stage = 0;

                //s_step[20]とs_step[21]の交点はoc.kouten_motome(Senbun s1,Senbun s2)で求める//２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
                Point kousa_point = new Point();
                kousa_point.set(OritaCalc.findIntersection(s_step[1 + (honsuu) + (honsuu) + 1], s_step[1 + (honsuu) + (honsuu) + 1 + 1]));

                LineSegment add_sen = new LineSegment(kousa_point, s_step[1 + (honsuu) + (honsuu) + 1].getA());
                add_sen.setColor(icol);
                if (add_sen.getLength() > 0.00000001) {
                    addLineSegment(add_sen);
                }

                LineSegment add_sen2 = new LineSegment(kousa_point, s_step[1 + (honsuu) + (honsuu) + 1 + 1].getA());
                add_sen2.setColor(icol);
                if (add_sen.getLength() > 0.00000001) {
                    addLineSegment(add_sen2);
                }
                record();
            }

            i_drawing_stage = 0;
            return;
        }
        return;
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_13(Point p0) {
    }

//------


//--------------------------------------------------------------------------------
//17 17 17 17 17 17    i_mouse_modeA==17　;角度系モード

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_13(Point p0) {
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_17(Point p0) {
        if (i_drawing_stage <= 1) {
            mMoved_A_11(p0);//近い既存点のみ表示
        }
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_17(Point p0) {

        int honsuu = 0;//1つの端点周りに描く線の本数
        if (id_kakudo_kei != 0) {
            honsuu = id_kakudo_kei * 2 - 1;
        } else if (id_kakudo_kei == 0) {
            honsuu = 6;
        }

        int i_jyunnbi_step_suu = 2;//動作の準備として人間が選択するステップ数

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));

        if (i_drawing_stage == 0) {    //第1段階として、点を選択
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) < d_decision_width) {
                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_point, closest_point);
                s_step[i_drawing_stage].setColor(LineColor.MAGENTA_5);

                //s_step[i_egaki_dankai].set(moyori_senbun);        s_step[i_egaki_dankai].setcolor(5);

            }
            return;
        }

        if (i_drawing_stage == 1) {    //第2段階として、点を選択
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) >= d_decision_width) {
                i_drawing_stage = 0;
                return;
            }
            if (p.distance(closest_point) < d_decision_width) {

                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_point, closest_point);
                s_step[i_drawing_stage].setColor(LineColor.fromNumber(i_drawing_stage));
                s_step[1].setB(s_step[2].getB());


            }

        }
// ------------------------------------------

        if (i_drawing_stage == i_jyunnbi_step_suu) {    //if(i_egaki_dankai==1){        //動作の準備として人間が選択するステップ数が終わった状態で実行
            int i_jyun;//i_jyunは線を描くとき順番に色を変えたいとき使う
            //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d) //    double d_kakudo_kei;double kakudo;

            if (id_kakudo_kei != 0) {
                d_kakudo_kei = 180.0 / (double) id_kakudo_kei;
            } else if (id_kakudo_kei == 0) {
                d_kakudo_kei = 180.0 / 4.0;
            }

            if (id_kakudo_kei != 0) {

                LineSegment s_kiso = new LineSegment(s_step[1].getA(), s_step[1].getB());
                kakudo = 0.0;
                i_jyun = 0;
                for (int i = 1; i <= honsuu; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }
                    i_drawing_stage = i_drawing_stage + 1;
                    kakudo = kakudo + d_kakudo_kei;
                    s_step[i_drawing_stage].set(OritaCalc.lineSegment_rotate(s_kiso, kakudo, 10.0));
                    if (i_jyun == 0) {
                        s_step[i_drawing_stage].setColor(LineColor.GREEN_6);
                    }
                    if (i_jyun == 1) {
                        s_step[i_drawing_stage].setColor(LineColor.ORANGE_4);
                    }
                }

                s_kiso.set(s_step[1].getB(), s_step[1].getA());
                kakudo = 0.0;
                i_jyun = 0;
                for (int i = 1; i <= honsuu; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }
                    i_drawing_stage = i_drawing_stage + 1;
                    kakudo = kakudo + d_kakudo_kei;
                    s_step[i_drawing_stage].set(OritaCalc.lineSegment_rotate(s_kiso, kakudo, 10.0));
                    if (i_jyun == 0) {
                        s_step[i_drawing_stage].setColor(LineColor.GREEN_6);
                    }
                    if (i_jyun == 1) {
                        s_step[i_drawing_stage].setColor(LineColor.ORANGE_4);
                    }
                }
            }
            if (id_kakudo_kei == 0) {
                double[] jk = new double[7];
                jk[0] = 0.0;
                jk[1] = d_jiyuu_kaku_2;
                jk[2] = d_jiyuu_kaku_1;
                jk[3] = d_jiyuu_kaku_3;
                jk[4] = 360.0 - d_jiyuu_kaku_2;
                jk[5] = 360.0 - d_jiyuu_kaku_1;
                jk[6] = 360.0 - d_jiyuu_kaku_3;

                LineSegment s_kiso = new LineSegment(s_step[1].getA(), s_step[1].getB());
                kakudo = 0.0;
                i_jyun = 0;
                for (int i = 1; i <= 6; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }
                    i_drawing_stage = i_drawing_stage + 1;
                    kakudo = jk[i];
                    s_step[i_drawing_stage].set(OritaCalc.lineSegment_rotate(s_kiso, kakudo, 10.0));
                    if (i == 1) {
                        s_step[i_drawing_stage].setColor(LineColor.GREEN_6);
                    }
                    if (i == 2) {
                        s_step[i_drawing_stage].setColor(LineColor.PURPLE_8);
                    }
                    if (i == 3) {
                        s_step[i_drawing_stage].setColor(LineColor.ORANGE_4);
                    }
                    if (i == 4) {
                        s_step[i_drawing_stage].setColor(LineColor.ORANGE_4);
                    }
                    if (i == 5) {
                        s_step[i_drawing_stage].setColor(LineColor.GREEN_6);
                    }
                    if (i == 6) {
                        s_step[i_drawing_stage].setColor(LineColor.PURPLE_8);
                    }
                }

                s_kiso.set(s_step[1].getB(), s_step[1].getA());
                kakudo = 0.0;
                i_jyun = 0;
                for (int i = 1; i <= 6; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }
                    i_drawing_stage = i_drawing_stage + 1;
                    kakudo = jk[i];
                    s_step[i_drawing_stage].set(OritaCalc.lineSegment_rotate(s_kiso, kakudo, 10.0));
                    if (i == 1) {
                        s_step[i_drawing_stage].setColor(LineColor.GREEN_6);
                    }
                    if (i == 2) {
                        s_step[i_drawing_stage].setColor(LineColor.PURPLE_8);
                    }
                    if (i == 3) {
                        s_step[i_drawing_stage].setColor(LineColor.ORANGE_4);
                    }
                    if (i == 4) {
                        s_step[i_drawing_stage].setColor(LineColor.ORANGE_4);
                    }
                    if (i == 5) {
                        s_step[i_drawing_stage].setColor(LineColor.GREEN_6);
                    }
                    if (i == 6) {
                        s_step[i_drawing_stage].setColor(LineColor.PURPLE_8);
                    }
                }
            }


            return;
        }


        if (i_drawing_stage == i_jyunnbi_step_suu + (honsuu) + (honsuu)) {//19     //動作の準備としてソフトが返答するステップ数が終わった状態で実行

            int i_tikai_s_step_suu = 0;

            //s_step[2から10]までとs_step[11から19]まで
            closest_lineSegment.set(get_moyori_step_senbun(p, 3, 2 + (honsuu)));
            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) < d_decision_width) {
                i_drawing_stage = i_drawing_stage + 1;
                i_tikai_s_step_suu = i_tikai_s_step_suu + 1;
                s_step[i_drawing_stage].set(closest_lineSegment);    //s_step[i_egaki_dankai].setcolor(2);//s_step[20]にinput
            }

            //s_step[2から10]までとs_step[11から19]まで
            closest_lineSegment.set(get_moyori_step_senbun(p, 2 + (honsuu) + 1, 2 + (honsuu) + (honsuu)));
            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) < d_decision_width) {
                i_drawing_stage = i_drawing_stage + 1;
                i_tikai_s_step_suu = i_tikai_s_step_suu + 1;
                s_step[i_drawing_stage].set(closest_lineSegment);    //s_step[i_egaki_dankai].setcolor(icol);
            }

            if (i_tikai_s_step_suu == 2) { //この段階でs_stepが[21]までうまってたら、s_step[20]とs_step[21]は共に加える折線なので、ここで処理を終えてしまう。
                //=     1+ (honsuu) +(honsuu) +  2 ){i_egaki_dankai=0; //この段階でs_stepが[21]までうまってたら、s_step[20]とs_step[21]は共に加える折線なので、ここで処理を終えてしまう。
                //例外処理としてs_step[20]とs_step[21]が平行の場合、より近いほうをs_stepが[20]とし、s_stepを[20]としてリターン（この場合はまだ処理は終われない）。
                //２つの線分が平行かどうかを判定する関数。oc.heikou_hantei(Tyokusen t1,Tyokusen t2)//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
                //0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する

                if (OritaCalc.parallel_judgement(s_step[i_drawing_stage - 1], s_step[i_drawing_stage], 0.1) != OritaCalc.ParallelJudgement.NOT_PARALLEL) {//ここは安全を見て閾値を0.1と大目にとっておこのがよさそう

                    //s_step[20]とs_step[21]と点pの距離  //public double kyori_senbun(Ten p0,Senbun s)
                    //if(oc.kyori_senbun(p, s_step[1+     (honsuu) +(honsuu)   +1]) >  oc.kyori_senbun(p, s_step[1+     (honsuu) +(honsuu)   +1+1])          ){
                    //     s_step[1+     (honsuu) +(honsuu)   +1].set(  s_step[1+     (honsuu) +(honsuu)   +1+1]                   )    ;
                    //}

                    //i_egaki_dankai=i_egaki_dankai-1;
                    //i_egaki_dankai=i_egaki_dankai-2;
                    i_drawing_stage = 0;
                    return;
                }

//System.out.println("aaaaaaaaaaaaaa");
//System.out.println("bbbbbbbbbbbbb");
//System.out.println("cccccccccccccc");


                i_drawing_stage = 0;

                //s_step[20]とs_step[21]の交点はoc.kouten_motome(Senbun s1,Senbun s2)で求める//２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
                Point kousa_point = new Point();
                kousa_point.set(OritaCalc.findIntersection(s_step[2 + (honsuu) + (honsuu) + 1], s_step[2 + (honsuu) + (honsuu) + 1 + 1]));

                LineSegment add_sen = new LineSegment(kousa_point, s_step[2 + (honsuu) + (honsuu) + 1].getA());
                add_sen.setColor(icol);
                if (add_sen.getLength() > 0.00000001) {
                    addLineSegment(add_sen);
                }

                LineSegment add_sen2 = new LineSegment(kousa_point, s_step[2 + (honsuu) + (honsuu) + 2].getA());
                add_sen2.setColor(icol);
                if (add_sen.getLength() > 0.00000001) {
                    addLineSegment(add_sen2);
                }
                record();
            }

            i_drawing_stage = 0;
            return;

        }

        //i_egaki_dankai=               1+ (honsuu) +(honsuu) ;
        return;
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_17(Point p0) {
    }

//------


//VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV

//16 16 16 16 16 16    i_mouse_modeA==16　;角度系モード

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_17(Point p0) {
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_16(Point p0) {
        mMoved_A_17(p0);
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_16(Point p0) {

        int honsuu = 0;//1つの端点周りに描く線の本数
        if (id_kakudo_kei != 0) {
            honsuu = id_kakudo_kei * 2 - 1;
        } else if (id_kakudo_kei == 0) {
            honsuu = 6;
        }


        double kakudo_kei = 36.0;
        double kakudo = 0.0;
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));

        if ((i_drawing_stage == 0) || (i_drawing_stage == 1)) {
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) < d_decision_width) {
                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_point, closest_point);
                s_step[i_drawing_stage].setColor(LineColor.fromNumber(i_drawing_stage));
                if (i_drawing_stage == 0) {
                    return;
                }
            }
        }


        if (i_drawing_stage == 2) {
            //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d)


            if (id_kakudo_kei != 0) {
                d_kakudo_kei = 180.0 / (double) id_kakudo_kei;
            } else if (id_kakudo_kei == 0) {
                d_kakudo_kei = 180.0 / 4.0;
            }

            if (id_kakudo_kei != 0) {


                LineSegment s_kiso = new LineSegment(s_step[2].getA(), s_step[1].getA());
                kakudo = 0.0;

                int i_jyun;
                i_jyun = 0;//i_jyunは線を描くとき順番に色を変えたいとき使う
                for (int i = 1; i <= honsuu; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }

                    i_drawing_stage = i_drawing_stage + 1;
                    kakudo = kakudo + d_kakudo_kei;
                    s_step[i_drawing_stage].set(OritaCalc.lineSegment_rotate(s_kiso, kakudo, 1.0));
                    if (i_jyun == 0) {
                        s_step[i_drawing_stage].setColor(LineColor.GREEN_6);
                    }
                    if (i_jyun == 1) {
                        s_step[i_drawing_stage].setColor(LineColor.ORANGE_4);
                    }
                }

            }

            if (id_kakudo_kei == 0) {
                double[] jk = new double[7];
                jk[0] = 0.0;
                jk[1] = d_jiyuu_kaku_2;
                jk[2] = d_jiyuu_kaku_1;
                jk[3] = d_jiyuu_kaku_3;
                jk[4] = 360.0 - d_jiyuu_kaku_2;
                jk[5] = 360.0 - d_jiyuu_kaku_1;
                jk[6] = 360.0 - d_jiyuu_kaku_3;


                LineSegment s_kiso = new LineSegment(s_step[2].getA(), s_step[1].getA());
                kakudo = 0.0;


                for (int i = 1; i <= 6; i++) {

                    i_drawing_stage = i_drawing_stage + 1;
                    kakudo = jk[i];
                    s_step[i_drawing_stage].set(OritaCalc.lineSegment_rotate(s_kiso, kakudo, 1.0));
                    if (i == 1) {
                        s_step[i_drawing_stage].setColor(LineColor.ORANGE_4);
                    }
                    if (i == 2) {
                        s_step[i_drawing_stage].setColor(LineColor.GREEN_6);
                    }
                    if (i == 3) {
                        s_step[i_drawing_stage].setColor(LineColor.PURPLE_8);
                    }
                    if (i == 4) {
                        s_step[i_drawing_stage].setColor(LineColor.ORANGE_4);
                    }
                    if (i == 5) {
                        s_step[i_drawing_stage].setColor(LineColor.GREEN_6);
                    }
                    if (i == 6) {
                        s_step[i_drawing_stage].setColor(LineColor.PURPLE_8);
                    }
                }


            }


            return;
        }


        if (i_drawing_stage == 2 + (honsuu)) {
            closest_lineSegment.set(get_moyori_step_senbun(p, 3, 2 + (honsuu)));
            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) < d_decision_width) {
                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_lineSegment);//s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
                s_step[i_drawing_stage].setColor(LineColor.BLUE_2);
                return;
            }
            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) >= d_decision_width) {
                i_drawing_stage = 0;
                return;
            }
        }


        if (i_drawing_stage == 2 + (honsuu) + 1) {

            closest_lineSegment.set(get_moyori_senbun(p));
            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) >= d_decision_width) {//最寄折線が遠かった場合
                i_drawing_stage = 0;
                return;
            }

            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) < d_decision_width) {
                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_lineSegment);//s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
                s_step[i_drawing_stage].setColor(LineColor.GREEN_6);
                //return;
            }
        }

//		if(i_egaki_dankai==13){
        if (i_drawing_stage == 2 + (honsuu) + 1 + 1) {
            i_drawing_stage = 0;

            //s_step[12]とs_step[13]の交点はoc.kouten_motome(Senbun s1,Senbun s2)で求める//２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
//			Ten kousa_ten =new Ten(); kousa_ten.set(oc.kouten_motome(s_step[12],s_step[13]));
            Point kousa_point = new Point();
            kousa_point.set(OritaCalc.findIntersection(s_step[2 + (honsuu) + 1], s_step[2 + (honsuu) + 1 + 1]));
            LineSegment add_sen = new LineSegment(kousa_point, s_step[2].getA(), icol);
            if (add_sen.getLength() > 0.00000001) {
                addLineSegment(add_sen);
                record();
            }
            return;
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_16(Point p0) {
    }

//------


//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

//VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV

//18 18 18 18 18 18    i_mouse_modeA==18　;角度系モード

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_16(Point p0) {
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_18(Point p0) {
        mMoved_A_17(p0);
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_18(Point p0) {

        int honsuu = 0;//1つの端点周りに描く線の本数
        if (id_kakudo_kei != 0) {
            honsuu = id_kakudo_kei * 2 - 1;
        } else if (id_kakudo_kei == 0) {
            honsuu = 6;
        }


        double kakudo_kei = 36.0;
        double kakudo = 0.0;
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));

        if ((i_drawing_stage == 0) || (i_drawing_stage == 1)) {
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) < d_decision_width) {
                i_drawing_stage = i_drawing_stage + 1;
                s_step[i_drawing_stage].set(closest_point, closest_point);
                s_step[i_drawing_stage].setColor(LineColor.fromNumber(i_drawing_stage));
                if (i_drawing_stage == 0) {
                    return;
                }
            }
        }


        if (i_drawing_stage == 2) {
            //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d)

            if (id_kakudo_kei != 0) {
                d_kakudo_kei = 180.0 / (double) id_kakudo_kei;
            } else if (id_kakudo_kei == 0) {
                d_kakudo_kei = 180.0 / 4.0;
            }


            if (id_kakudo_kei != 0) {
                LineSegment s_kiso = new LineSegment(s_step[2].getA(), s_step[1].getA());
                kakudo = 0.0;

                int i_jyun;
                i_jyun = 0;//i_jyunは線を描くとき順番に色を変えたいとき使う
                for (int i = 1; i <= honsuu; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }
                    i_drawing_stage = i_drawing_stage + 1;
                    kakudo = kakudo + d_kakudo_kei;
                    s_step[i_drawing_stage].set(OritaCalc.lineSegment_rotate(s_kiso, kakudo, 100.0));
                    if (i_jyun == 0) {
                        s_step[i_drawing_stage].setColor(LineColor.GREEN_6);
                    }
                    if (i_jyun == 1) {
                        s_step[i_drawing_stage].setColor(LineColor.ORANGE_4);
                    }
                }
            }


            if (id_kakudo_kei == 0) {
                double[] jk = new double[7];
                jk[0] = 0.0;
                jk[1] = d_jiyuu_kaku_2;
                jk[2] = d_jiyuu_kaku_1;
                jk[3] = d_jiyuu_kaku_3;
                jk[4] = 360.0 - d_jiyuu_kaku_2;
                jk[5] = 360.0 - d_jiyuu_kaku_1;
                jk[6] = 360.0 - d_jiyuu_kaku_3;

                LineSegment s_kiso = new LineSegment(s_step[2].getA(), s_step[1].getA());
                kakudo = 0.0;


                for (int i = 1; i <= 6; i++) {
                    //i_jyun=i_jyun+1;if(i_jyun==2){i_jyun=0;}

                    i_drawing_stage = i_drawing_stage + 1;
                    kakudo = jk[i];
                    s_step[i_drawing_stage].set(OritaCalc.lineSegment_rotate(s_kiso, kakudo, 100.0));
                    if (i == 1) {
                        s_step[i_drawing_stage].setColor(LineColor.ORANGE_4);
                    }
                    if (i == 2) {
                        s_step[i_drawing_stage].setColor(LineColor.GREEN_6);
                    }
                    if (i == 3) {
                        s_step[i_drawing_stage].setColor(LineColor.PURPLE_8);
                    }
                    if (i == 4) {
                        s_step[i_drawing_stage].setColor(LineColor.ORANGE_4);
                    }
                    if (i == 5) {
                        s_step[i_drawing_stage].setColor(LineColor.GREEN_6);
                    }
                    if (i == 6) {
                        s_step[i_drawing_stage].setColor(LineColor.PURPLE_8);
                    }
                }
            }


            return;
        }


        //if(i_egaki_dankai==11){
        if (i_drawing_stage == 2 + (honsuu)) {
            i_drawing_stage = 0;
            closest_step_lineSegment.set(get_moyori_step_senbun(p, 3, 2 + (honsuu)));
            if (OritaCalc.distance_lineSegment(p, closest_step_lineSegment) >= d_decision_width) {
                return;
            }

            if (OritaCalc.distance_lineSegment(p, closest_step_lineSegment) < d_decision_width) {
                Point mokuhyou_point = new Point();
                mokuhyou_point.set(OritaCalc.shadow_request(closest_step_lineSegment, p));

                closest_lineSegment.set(get_moyori_senbun(p));
                if (OritaCalc.distance_lineSegment(p, closest_lineSegment) < d_decision_width) {//最寄折線が近い場合
                    if (OritaCalc.parallel_judgement(closest_step_lineSegment, closest_lineSegment, 0.000001) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {//最寄折線が最寄step折線と平行の場合は除外
                        Point mokuhyou_point2 = new Point();
                        mokuhyou_point2.set(OritaCalc.findIntersection(closest_step_lineSegment, closest_lineSegment));
                        if (p.distance(mokuhyou_point) * 2.0 > p.distance(mokuhyou_point2)) {
                            mokuhyou_point.set(mokuhyou_point2);
                        }

                    }

                }

                LineSegment add_sen = new LineSegment();
                add_sen.set(mokuhyou_point, s_step[2].getA());
                add_sen.setColor(icol);
                addLineSegment(add_sen);
                record();
            }


			/*
			  oc.kouten_motome(s_step[12],s_step[13]);


				(s_step[1].geta(),oc.kage_motome(oc.Senbun2Tyokusen(s_step[2]),s_step[1].geta()));



			if(add_sen.getnagasa()>0.00000001){addsenbun(add_sen);}

				i_egaki_dankai=i_egaki_dankai+1;
				s_step[i_egaki_dankai].set(moyori_step_senbun);//s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
				s_step[i_egaki_dankai].setcolor(2);
				return;
			}

			}

//		if(i_egaki_dankai==12){
		if(i_egaki_dankai==2+ (honsuu)  +1 ){
			moyori_senbun.set(get_moyori_senbun(p));
			if(oc.kyori_senbun( p,moyori_senbun)<d_decision_width){
				i_egaki_dankai=i_egaki_dankai+1;
				s_step[i_egaki_dankai].set(moyori_senbun);//s_step[i_egaki_dankai].setcolor(i_egaki_dankai);

			//return;
			}
		}

//		if(i_egaki_dankai==13){
		if(i_egaki_dankai==2+ (honsuu)  +1  +1){
			i_egaki_dankai=0;

			//s_step[12]とs_step[13]の交点はoc.kouten_motome(Senbun s1,Senbun s2)で求める//２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
//			Ten kousa_ten =new Ten(); kousa_ten.set(oc.kouten_motome(s_step[12],s_step[13]));
			Ten kousa_ten =new Ten(); kousa_ten.set(oc.kouten_motome(s_step[2+ (honsuu)  +1 ],s_step[2+ (honsuu)  +1  +1]));
			Senbun add_sen =new Senbun(kousa_ten,s_step[2].geta());
			if(add_sen.getnagasa()>0.00000001){addsenbun(add_sen);kiroku();}
			return;
	*/
        }


    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_18(Point p0) {
    }

//------


//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA


//14 14 14 14 14 14 14 14 14    i_mouse_modeA==14　;V追加モード

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_18(Point p0) {
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_14(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        int mts_id;
        mts_id = ori_s.closestLineSegmentSearch(p);//mts_idは点pに最も近い線分の番号	public int ori_s.mottomo_tikai_senbun_sagasi(Ten p)
        LineSegment mts = new LineSegment(ori_s.getA(mts_id), ori_s.getB(mts_id));//mtsは点pに最も近い線分

        if (OritaCalc.distance_lineSegment(p, mts) < d_decision_width) {
            //直線t上の点pの影の位置（点pと最も近い直線t上の位置）を求める。public Ten oc.kage_motome(Tyokusen t,Ten p){}
            //線分を含む直線を得る public Tyokusen oc.Senbun2Tyokusen(Senbun s){}
            Point pk = new Point();
            pk.set(OritaCalc.shadow_request(OritaCalc.lineSegmentToStraightLine(mts), p));//pkは点pの（線分を含む直線上の）影

            //点paが、二点p1,p2を端点とする線分に点p1と点p2で直行する、2つの線分を含む長方形内にある場合は2を返す関数	public int oc.hakononaka(Ten p1,Ten pa,Ten p2){}

            if (OritaCalc.isInside(mts.getA(), pk, mts.getB()) == 2) {
                //線分の分割-----------------------------------------
                ori_s.lineSegment_bunkatu(mts_id, pk);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
                record();
            }

        }
        return;

    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_14(Point p0) {
    }

//------

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_14(Point p0) {
    }

    public void v_del_all() {
        int sousuu_old = ori_s.getTotal();
        ori_s.del_V_all();
        if (sousuu_old != ori_s.getTotal()) {
            record();
        }
    }

    public void v_del_all_cc() {
        int sousuu_old = ori_s.getTotal();
        ori_s.del_V_all_cc();
        if (sousuu_old != ori_s.getTotal()) {
            record();
        }
    }

//15 15 15 15 15 15 15 15 15    i_mouse_modeA==15　;V削除モード

    // ------------------------------------------------------------
    public void all_s_step_to_orisen() {//20181014

        LineSegment add_sen = new LineSegment();
        for (int i = 1; i <= i_drawing_stage; i++) {

            if (s_step[i].getLength() > 0.00000001) {
                add_sen.set(s_step[i]);
                add_sen.setColor(icol);
                addLineSegment(add_sen);
            } else {

                add_en(s_step[i].getAX(), s_step[i].getAY(), 5.0, LineColor.CYAN_3);
            }
        }
        record();

        //i_candidate_stage//int sousuu_old =ori_s.getsousuu();
        //ori_s.del_V_all_cc();
        //if(sousuu_old !=ori_s.getsousuu()){kiroku();}
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_15(Point p0) {


        //Ten p =new Ten();
        p.set(camera.TV2object(p0));

        //点pに最も近い線分の、点pに近い方の端点を、頂点とした場合、何本の線分が出ているか（頂点とr以内に端点がある線分の数）	public int tyouten_syuui_sennsuu(Ten p) {

        ori_s.del_V(p, d_decision_width, 0.000001);
        record();


    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_15(Point p0) {
    }


//------

//41 41 41 41 41 41 41 41    i_mouse_modeA==41　;V削除モード(2つの折線の色が違った場合カラーチェンジして、点削除する。黒赤は赤赤、黒青は青青、青赤は黒にする)

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_15(Point p0) {
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_41(Point p0) {
        p.set(camera.TV2object(p0));

        //点pに最も近い線分の、点pに近い方の端点を、頂点とした場合、何本の線分が出ているか（頂点とr以内に端点がある線分の数）	public int tyouten_syuui_sennsuu(Ten p) {

        ori_s.del_V_cc(p, d_decision_width, 0.000001);

        record();
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_41(Point p0) {
    }


//------

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_41(Point p0) {
    }

    //-------------------------
//23 23 23 23 23
    //マウス操作(i_mouse_modeA==23 "->M" でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_23(Point p0) {
        mPressed_A_box_select(p0);
    }

    //マウス操作(i_mouse_modeA==23でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_23(Point p0) {
        mDragged_A_box_select(p0);
    }

    //マウス操作(i_mouse_modeA==23 でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_23(Point p0) {//ここの処理の終わりに fix2(0.001,0.5);　をするのは、元から折線だったものと、補助線から変換した折線との組合せで頻発するT字型不接続を修正するため
        i_drawing_stage = 0;

        if (p19_1.distance(p0) > 0.000001) {//現状では赤を赤に変えたときもUNDO用に記録されてしまう20161218
            if (M_nisuru(p19_1, p0) != 0) {
                fix2(0.001, 0.5);
                record();
            }
        }
        if (p19_1.distance(p0) <= 0.000001) {//現状では赤を赤に変えたときもUNDO用に記録されてしまう20161218
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            if (ori_s.closestLineSegmentDistance(p) < d_decision_width) {//点pに最も近い線分の番号での、その距離を返す	public double closestLineSegmentDistance(Ten p)
                ori_s.setColor(ori_s.closestLineSegmentSearch(p), LineColor.RED_1);
                fix2(0.001, 0.5);
                record();
            }
        }

    }

    //--------------------
    public int M_nisuru(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getX(), p0a.getY());
        p0_b.set(p0a.getX(), p0b.getY());
        p0_c.set(p0b.getX(), p0b.getY());
        p0_d.set(p0b.getX(), p0a.getY());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return ori_s.M_nisuru(p_a, p_b, p_c, p_d);
    }

    //---------------------
//24 24 24 24 24
    //マウス操作(i_mouse_modeA==24 "->V" でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_24(Point p0) {
        mPressed_A_box_select(p0);
    }

    //マウス操作(i_mouse_modeA==24でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_24(Point p0) {
        mDragged_A_box_select(p0);
    }

    //マウス操作(i_mouse_modeA==24 でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_24(Point p0) {//ここの処理の終わりに fix2(0.001,0.5);　をするのは、元から折線だったものと、補助線から変換した折線との組合せで頻発するT字型不接続を修正するため
        i_drawing_stage = 0;

        if (p19_1.distance(p0) > 0.000001) {
            //V_nisuru(p19_1,p0);
            if (V_nisuru(p19_1, p0) != 0) {
                fix2(0.001, 0.5);
                record();
            }
        }
        if (p19_1.distance(p0) <= 0.000001) {
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            if (ori_s.closestLineSegmentDistance(p) < d_decision_width) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                ori_s.setColor(ori_s.closestLineSegmentSearch(p), LineColor.BLUE_2);
                fix2(0.001, 0.5);
                record();
            }
        }


    }

    //--------------------
    public int V_nisuru(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getX(), p0a.getY());
        p0_b.set(p0a.getX(), p0b.getY());
        p0_c.set(p0b.getX(), p0b.getY());
        p0_d.set(p0b.getX(), p0a.getY());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return ori_s.V_nisuru(p_a, p_b, p_c, p_d);
    }

    //---------------------
//25 25 25 25 25
    //マウス操作(i_mouse_modeA==25 "->E" でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_25(Point p0) {
        mPressed_A_box_select(p0);
    }

    //マウス操作(i_mouse_modeA==25でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_25(Point p0) {
        mDragged_A_box_select(p0);
    }

    //マウス操作(i_mouse_modeA==25 でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_25(Point p0) {//ここの処理の終わりに fix2(0.001,0.5);　をするのは、元から折線だったものと、補助線から変換した折線との組合せで頻発するT字型不接続を修正するため
        i_drawing_stage = 0;


        //E_nisuru(p19_1,p0);

        if (p19_1.distance(p0) > 0.000001) {
            if (E_nisuru(p19_1, p0) != 0) {
                fix2(0.001, 0.5);
                record();
            }
        }

        if (p19_1.distance(p0) <= 0.000001) {
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            if (ori_s.closestLineSegmentDistance(p) < d_decision_width) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                ori_s.setColor(ori_s.closestLineSegmentSearch(p), LineColor.BLACK_0);
                fix2(0.001, 0.5);
                record();
            }
        }


    }

//---------------------

    //--------------------
    public int E_nisuru(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getX(), p0a.getY());
        p0_b.set(p0a.getX(), p0b.getY());
        p0_c.set(p0b.getX(), p0b.getY());
        p0_d.set(p0b.getX(), p0a.getY());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return ori_s.E_nisuru(p_a, p_b, p_c, p_d);
    }

    //60 60 60 60 60
    //マウス操作(i_mouse_modeA==60 "->HK" でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_60(Point p0) {
        mPressed_A_box_select(p0);
    }

    //マウス操作(i_mouse_modeA==60でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_60(Point p0) {
        mDragged_A_box_select(p0);
    }

    //マウス操作(i_mouse_modeA==60 でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_60(Point p0) {
        i_drawing_stage = 0;

        if (p19_1.distance(p0) > 0.000001) {
            if (HK_nisuru(p19_1, p0) != 0) {
                record();
            }//この関数は不完全なのでまだ未公開20171126
        }

        if (p19_1.distance(p0) <= 0.000001) {
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            if (ori_s.closestLineSegmentDistance(p) < d_decision_width) {//点pに最も近い線分の番号での、その距離を返す	public double closestLineSegmentDistance(Ten p)
                if (ori_s.getColor(ori_s.closestLineSegmentSearchReversedOrder(p)).getNumber() < 3) {
                    LineSegment add_sen = new LineSegment();
                    add_sen.set(ori_s.get(ori_s.closestLineSegmentSearchReversedOrder(p)));
                    add_sen.setColor(LineColor.CYAN_3);

                    ori_s.deleteLineSegment_vertex(ori_s.closestLineSegmentSearchReversedOrder(p));
                    addLineSegment(add_sen);

                    circle_organize();
                    record();
                }


                //kiroku();
            }
        }


    }


//camera.object2TV

    //--------------------
    public int HK_nisuru(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getX(), p0a.getY());
        p0_b.set(p0a.getX(), p0b.getY());
        p0_c.set(p0b.getX(), p0b.getY());
        p0_d.set(p0b.getX(), p0a.getY());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return ori_s.HK_nisuru(p_a, p_b, p_c, p_d);
    }


//26 26 26 26    i_mouse_modeA==26　;背景setモード。

    public LineSegment get_s_step(int i) {
        return s_step[i];
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_26(Point p0) {


        //Ten p =new Ten();
        p.set(camera.TV2object(p0));

        if (i_drawing_stage == 3) {
            i_drawing_stage = 4;
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) < d_decision_width) {
                p.set(closest_point);
            }
            s_step[4].set(p, p);
            s_step[4].setColor(LineColor.fromNumber(i_drawing_stage));
        }

        if (i_drawing_stage == 2) {
            i_drawing_stage = 3;
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) < d_decision_width) {
                p.set(closest_point);
            }
            s_step[3].set(p, p);
            s_step[3].setColor(LineColor.fromNumber(i_drawing_stage));
        }

        if (i_drawing_stage == 1) {
            i_drawing_stage = 2;
            s_step[2].set(p, p);
            s_step[2].setColor(LineColor.fromNumber(i_drawing_stage));
        }

        if (i_drawing_stage == 0) {
            i_drawing_stage = 1;
            s_step[1].set(p, p);
            s_step[1].setColor(LineColor.fromNumber(i_drawing_stage));
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_26(Point p0) {
    }

//------


//42 42 42 42 42 42 42 42 42 42 42 42 42 42 42　ここから

    //マウス操作(ボタンを離したとき)を行う関数
    public int mReleased_A_26(Point p0) {
        return i_drawing_stage;
    }

    //マウス操作(i_mouse_modeA==42 円入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_42(Point p0) {
        i_drawing_stage = 1;
        i_circle_drawing_stage = 1;

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) > d_decision_width) {
            i_drawing_stage = 0;
            i_circle_drawing_stage = 0;
        }
        s_step[1].set(p, closest_point);
        s_step[1].setColor(LineColor.CYAN_3);
        e_step[1].set(closest_point.getX(), closest_point.getY(), 0.0);
        e_step[1].setColor(LineColor.CYAN_3);


//System.out.println("20170225  s_step[1].getax()"+s_step[1].getax());

//s_step[1].set(new Ten(200.0,200.0),new Ten(300.0,300.0));
        //k.addsenbun(p,p);
        //ieda=k.getsousuu();
        //k.setcolor(ieda,icol);
    }

    //マウス操作(i_mouse_modeA==42 円入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_42(Point p0) {

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        s_step[1].setA(p);
        e_step[1].setR(OritaCalc.distance(s_step[1].getA(), s_step[1].getB()));

        //k.seta(ieda, p);
    }

//42 42 42 42 42 42 42 42 42 42 42 42 42 42 42  ここまで


//47 47 47 47 47 47 47 47 47 47 47 47 47 47 47　ここから

    //マウス操作(i_mouse_modeA==42 円入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_42(Point p0) {
        if (i_drawing_stage == 1) {
            i_drawing_stage = 0;
            i_circle_drawing_stage = 0;

            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            closest_point.set(getClosestPoint(p));
            s_step[1].setA(closest_point);
            if (p.distance(closest_point) <= d_decision_width) {
                if (s_step[1].getLength() > 0.00000001) {
                    //addsenbun(s_step[1]);
                    add_en(s_step[1].getBX(), s_step[1].getBY(), s_step[1].getLength(), LineColor.CYAN_3);
                    record();
                }
            }

//System.out.println("20170227  ********************1");
//			Memo mtemp =new Memo();
//System.out.println("20170227  ********************2");
//			mtemp.set(getMemo());
//System.out.println("20170227  ********************3");
//			setMemo(mtemp);


        }

    }

    //マウス操作(i_mouse_modeA==47 円入力(フリー　)　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_47(Point p0) {
        i_drawing_stage = 1;
        i_circle_drawing_stage = 1;

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) > d_decision_width) {
            s_step[1].set(p, p);
            s_step[1].setColor(LineColor.CYAN_3);
            e_step[1].set(p.getX(), p.getY(), 0.0);
            e_step[1].setColor(LineColor.CYAN_3);
        } else {
            s_step[1].set(p, closest_point);
            s_step[1].setColor(LineColor.CYAN_3);
            e_step[1].set(closest_point.getX(), closest_point.getY(), 0.0);
            e_step[1].setColor(LineColor.CYAN_3);
        }
    }

    //マウス操作(i_mouse_modeA==47 円入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_47(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        s_step[1].setA(p);
        e_step[1].setR(OritaCalc.distance(s_step[1].getA(), s_step[1].getB()));
    }

//47 47 47 47 47 47 47 47 47 47 47 47 47 47 47  ここまで


//44 44 44 44 44 44 44 44 44 44 44 44 44 44 44　ここから

    //マウス操作(i_mouse_modeA==47 円入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_47(Point p0) {
        if (i_drawing_stage == 1) {
            i_drawing_stage = 0;
            i_circle_drawing_stage = 0;

            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            closest_point.set(getClosestPoint(p));

            if (p.distance(closest_point) <= d_decision_width) {
                s_step[1].setA(closest_point);
            } else {
                s_step[1].setA(p);
            }

            if (s_step[1].getLength() > 0.00000001) {
                add_en(s_step[1].getBX(), s_step[1].getBY(), s_step[1].getLength(), LineColor.CYAN_3);
                record();
            }
        }
    }

    //マウス操作(i_mouse_modeA==44 円 分離入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_44(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));

        if (i_drawing_stage == 0) {
            i_drawing_stage = 0;
            i_circle_drawing_stage = 0;
            if (p.distance(closest_point) > d_decision_width) {
                return;
            }

            i_drawing_stage = 1;
            i_circle_drawing_stage = 0;
            s_step[1].set(closest_point, closest_point);
            s_step[1].setColor(LineColor.CYAN_3);
            return;
        }

        if (i_drawing_stage == 1) {
            i_drawing_stage = 1;
            i_circle_drawing_stage = 0;
            if (p.distance(closest_point) > d_decision_width) {
                return;
            }

            i_drawing_stage = 2;
            i_circle_drawing_stage = 1;
            s_step[2].set(p, closest_point);
            s_step[2].setColor(LineColor.CYAN_3);
            e_step[1].set(s_step[1].getA(), 0.0, LineColor.CYAN_3);
            return;
        }


    }

    //マウス操作(i_mouse_modeA==44 円 分離入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_44(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        if (i_drawing_stage == 2) {
            i_drawing_stage = 2;
            i_circle_drawing_stage = 1;
            s_step[2].setA(p);
            e_step[1].setR(s_step[2].getLength());
        }
    }

//44 44 44 44 44 44 44 44 44 44 44 44 44 44 44  ここまで


//48 48 48 48 48 48 48 48 48 48 48 48 48 48 48　ここから

    //マウス操作(i_mouse_modeA==44 円 分離入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_44(Point p0) {
        if (i_drawing_stage == 2) {
            i_drawing_stage = 0;
            i_circle_drawing_stage = 0;

            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            closest_point.set(getClosestPoint(p));
            s_step[2].setA(closest_point);
            if (p.distance(closest_point) <= d_decision_width) {
                if (s_step[2].getLength() > 0.00000001) {
                    addLineSegment(s_step[2]);
                    add_en(s_step[1].getA(), s_step[2].getLength(), LineColor.CYAN_3);
                    record();
                }
            }

//System.out.println("20170227  ********************1");
//			Memo mtemp =new Memo();
//System.out.println("20170227  ********************2");
//			mtemp.set(getMemo());
//System.out.println("20170227  ********************3");
//			setMemo(mtemp);


        }

    }

    //マウス操作(i_mouse_modeA==48 同心円　線分入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_48(Point p0) {

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_circumference.set(get_moyori_ensyuu(p));
        closest_point.set(getClosestPoint(p));

        if ((i_drawing_stage == 0) && (i_circle_drawing_stage == 0)) {
            if (OritaCalc.distance_circumference(p, closest_circumference) > d_decision_width) {
                return;
            }

            i_drawing_stage = 0;
            i_circle_drawing_stage = 1;
            e_step[1].set(closest_circumference);
            e_step[1].setColor(LineColor.GREEN_6);
            return;
        }


        if ((i_drawing_stage == 0) && (i_circle_drawing_stage == 1)) {
            if (p.distance(closest_point) > d_decision_width) {
                return;
            }

            i_drawing_stage = 1;
            i_circle_drawing_stage = 2;
            s_step[1].set(p, closest_point);
            s_step[1].setColor(LineColor.CYAN_3);
            e_step[2].set(e_step[1]);
            e_step[2].setColor(LineColor.CYAN_3);
            return;
        }
    }

    //マウス操作(i_mouse_modeA==48 同心円　線分入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_48(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        if ((i_drawing_stage == 1) && (i_circle_drawing_stage == 2)) {
            s_step[1].setA(p);
            e_step[2].setR(e_step[1].getRadius() + s_step[1].getLength());
        }
    }

//48 48 48 48 48 48 48 48 48 48 48 48 48 48 48  ここまで

//49 49 49 49 49 49 49 49 49 49 49 49 49 49 49　ここから

    //マウス操作(i_mouse_modeA==48 同心円　線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_48(Point p0) {
        if ((i_drawing_stage == 1) && (i_circle_drawing_stage == 2)) {
            i_drawing_stage = 0;
            i_circle_drawing_stage = 0;

            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            closest_point.set(getClosestPoint(p));
            s_step[1].setA(closest_point);
            if (p.distance(closest_point) <= d_decision_width) {
                if (s_step[1].getLength() > 0.00000001) {
                    addLineSegment(s_step[1]);
                    e_step[2].setR(e_step[1].getRadius() + s_step[1].getLength());
                    add_en(e_step[2]);
                    record();
                }
            }
        }
    }

    //マウス操作(i_mouse_modeA==49 同心円　同心円入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_49(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_circumference.set(get_moyori_ensyuu(p));
        closest_point.set(getClosestPoint(p));

        if ((i_drawing_stage == 0) && (i_circle_drawing_stage == 0)) {
            if (OritaCalc.distance_circumference(p, closest_circumference) > d_decision_width) {
                return;
            }

            i_drawing_stage = 0;
            i_circle_drawing_stage = 1;
            e_step[1].set(closest_circumference);
            e_step[1].setColor(LineColor.GREEN_6);
            return;
        }

        if ((i_drawing_stage == 0) && (i_circle_drawing_stage == 1)) {
            //if(p.kyori(moyori_ten)>d_decision_width){return;}
            if (OritaCalc.distance_circumference(p, closest_circumference) > d_decision_width) {
                return;
            }

            i_drawing_stage = 0;
            i_circle_drawing_stage = 2;
            e_step[2].set(closest_circumference);
            e_step[2].setColor(LineColor.PURPLE_8);
            return;
        }

        if ((i_drawing_stage == 0) && (i_circle_drawing_stage == 2)) {
            //if(p.kyori(moyori_ten)>d_decision_width){return;}
            if (OritaCalc.distance_circumference(p, closest_circumference) > d_decision_width) {
                return;
            }

            i_drawing_stage = 0;
            i_circle_drawing_stage = 3;
            e_step[3].set(closest_circumference);
            e_step[3].setColor(LineColor.PURPLE_8);
            return;
        }
    }

    //マウス操作(i_mouse_modeA==49 同心円　同心円入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_49(Point p0) {

    }

//49 49 49 49 49 49 49 49 49 49 49 49 49 49 49  ここまで

//51 51 51 51 51 51 51 51 51 51 51 51 51 51 51　ここから

    //マウス操作(i_mouse_modeA==49 同心円　同心円入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_49(Point p0) {
        if ((i_drawing_stage == 0) && (i_circle_drawing_stage == 3)) {
            i_drawing_stage = 0;
            i_circle_drawing_stage = 0;
            double add_r = e_step[3].getRadius() - e_step[2].getRadius();
            if (Math.abs(add_r) > 0.00000001) {
                double new_r = add_r + e_step[1].getRadius();

                if (new_r > 0.00000001) {
                    e_step[1].setR(new_r);
                    e_step[1].setColor(LineColor.CYAN_3);
                    add_en(e_step[1]);
                    record();
                }
            }
        }
    }

    //マウス操作(i_mouse_modeA==51 平行線　幅指定入力モード　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_51(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));

        if ((i_drawing_stage == 0) && (i_circle_drawing_stage == 0)) {
            closest_lineSegment.set(get_moyori_senbun(p));
            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) < d_decision_width) {
                i_drawing_stage = 1;
                i_circle_drawing_stage = 0;
                s_step[1].set(closest_lineSegment);
                s_step[1].setColor(LineColor.GREEN_6);
            }
            return;
        }

        if ((i_drawing_stage == 1) && (i_circle_drawing_stage == 0)) {
            if (p.distance(closest_point) > d_decision_width) {
                return;
            }
            i_drawing_stage = 4;
            i_circle_drawing_stage = 0;
            s_step[2].set(p, closest_point);
            s_step[2].setColor(LineColor.CYAN_3);
            s_step[3].set(s_step[1]);
            s_step[3].setColor(LineColor.PURPLE_8);
            s_step[4].set(s_step[1]);
            s_step[4].setColor(LineColor.PURPLE_8);
            return;
        }


        if ((i_drawing_stage == 4) && (i_circle_drawing_stage == 0)) {
            //if(p.kyori(moyori_ten)>d_decision_width){return;}

            i_drawing_stage = 3;
            i_circle_drawing_stage = 0;
            closest_step_lineSegment.set(get_moyori_step_senbun(p, 3, 4));

            //if(oc.kyori_senbun(p,moyori_step_senbun)>d_decision_width){return;}
            s_step[3].set(closest_step_lineSegment);
            return;
        }


    }

    //マウス操作(i_mouse_modeA==51 平行線　幅指定入力モード　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_51(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        if ((i_drawing_stage == 4) && (i_circle_drawing_stage == 0)) {
            s_step[2].setA(p);
            s_step[3].set(OritaCalc.moveParallel(s_step[1], s_step[2].getLength()));
            s_step[3].setColor(LineColor.PURPLE_8);
            s_step[4].set(OritaCalc.moveParallel(s_step[1], -s_step[2].getLength()));
            s_step[4].setColor(LineColor.PURPLE_8);
        }
    }

//51 51 51 51 51 51 51 51 51 51 51 51 51 51 51  ここまで

//45 45 45 45 45 45 45 45 45   i_mouse_modeA==45　;2円の共通接線入力モード。

    //マウス操作(i_mouse_modeA==51 平行線　幅指定入力モード　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_51(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));

        if ((i_drawing_stage == 4) && (i_circle_drawing_stage == 0)) {
            if (p.distance(closest_point) >= d_decision_width) {
                i_drawing_stage = 1;
                i_circle_drawing_stage = 0;
                return;
            }

            s_step[2].setA(closest_point);

            if (s_step[2].getLength() < 0.00000001) {
                i_drawing_stage = 1;
                i_circle_drawing_stage = 0;
                return;
            }
            s_step[3].set(OritaCalc.moveParallel(s_step[1], s_step[2].getLength()));
            s_step[3].setColor(LineColor.PURPLE_8);
            s_step[4].set(OritaCalc.moveParallel(s_step[1], -s_step[2].getLength()));
            s_step[4].setColor(LineColor.PURPLE_8);
        }


        if ((i_drawing_stage == 3) && (i_circle_drawing_stage == 0)) {
            i_drawing_stage = 0;
            i_circle_drawing_stage = 0;

            s_step[3].setColor(icol);
            addLineSegment(s_step[3]);
            record();

            return;
        }


    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_45(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_circumference.set(get_moyori_ensyuu(p));

        if (i_circle_drawing_stage == 0) {
            i_drawing_stage = 0;
            i_circle_drawing_stage = 0;
            if (OritaCalc.distance_circumference(p, closest_circumference) > d_decision_width) {
                return;
            }

            i_drawing_stage = 0;
            i_circle_drawing_stage = 1;
            e_step[1].set(closest_circumference);
            e_step[1].setColor(LineColor.GREEN_6);
            return;
        }

        if (i_circle_drawing_stage == 1) {
            i_drawing_stage = 0;
            i_circle_drawing_stage = 1;
            if (OritaCalc.distance_circumference(p, closest_circumference) > d_decision_width) {
                return;
            }

            i_drawing_stage = 0;
            i_circle_drawing_stage = 2;
            e_step[2].set(closest_circumference);
            e_step[2].setColor(LineColor.GREEN_6);
            return;
        }

        if (i_drawing_stage > 1) {//			i_egaki_dankai=0;i_circle_drawing_stage=1;
            closest_step_lineSegment.set(get_moyori_step_senbun(p, 1, i_drawing_stage));

            if (OritaCalc.distance_lineSegment(p, closest_step_lineSegment) > d_decision_width) {
                return;
            }
            s_step[1].set(closest_step_lineSegment);
            i_drawing_stage = 1;
            i_circle_drawing_stage = 2;

            return;
        }


    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_45(Point p0) {
    }

//45 45 45 45 45 45 45 45 45  ここまで  ------


//50 50 50 50 50 50 50 50 50   i_mouse_modeA==50　;2円に幅同じで接する同心円を加える。

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_45(Point p0) {
        if ((i_drawing_stage == 0) && (i_circle_drawing_stage == 2)) {
            Point c1 = new Point();
            c1.set(e_step[1].getCenter());
            Point c2 = new Point();
            c2.set(e_step[2].getCenter());

            double x1 = e_step[1].getX();
            double y1 = e_step[1].getY();
            double r1 = e_step[1].getRadius();
            double x2 = e_step[2].getX();
            double y2 = e_step[2].getY();
            double r2 = e_step[2].getRadius();
            //0,0,r,        xp,yp,R
            double xp = x2 - x1;
            double yp = y2 - y1;

            if (c1.distance(c2) < 0.000001) {
                i_drawing_stage = 0;
                i_circle_drawing_stage = 0;
                return;
            }//接線0本の場合

            if ((xp * xp + yp * yp) < (r1 - r2) * (r1 - r2)) {
                i_drawing_stage = 0;
                i_circle_drawing_stage = 0;
                return;
            }//接線0本の場合

            if (Math.abs((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2)) < 0.0000001) {//外接線1本の場合
                Point kouten = new Point();
                kouten.set(OritaCalc.naibun(c1, c2, -r1, r2));
                StraightLine ty = new StraightLine(c1, kouten);
                ty.orthogonalize(kouten);
                s_step[1].set(OritaCalc.circle_to_straightLine_no_intersect_wo_connect_LineSegment(new Circle(kouten, (r1 + r2) / 2.0, LineColor.BLACK_0), ty));

                i_drawing_stage = 1;
                i_circle_drawing_stage = 2;
            }

            if (((r1 - r2) * (r1 - r2) < (xp * xp + yp * yp)) && ((xp * xp + yp * yp) < (r1 + r2) * (r1 + r2))) {//外接線2本の場合
                double xq1 = r1 * (xp * (r1 - r2) + yp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double yq1 = r1 * (yp * (r1 - r2) - xp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double xq2 = r1 * (xp * (r1 - r2) - yp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double yq2 = r1 * (yp * (r1 - r2) + xp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線

                double xr1 = xq1 + x1;
                double yr1 = yq1 + y1;
                double xr2 = xq2 + x1;
                double yr2 = yq2 + y1;

                StraightLine t1 = new StraightLine(x1, y1, xr1, yr1);
                t1.orthogonalize(new Point(xr1, yr1));
                StraightLine t2 = new StraightLine(x1, y1, xr2, yr2);
                t2.orthogonalize(new Point(xr2, yr2));

                s_step[1].set(new Point(xr1, yr1), OritaCalc.shadow_request(t1, new Point(x2, y2)));
                s_step[1].setColor(LineColor.PURPLE_8);
                s_step[2].set(new Point(xr2, yr2), OritaCalc.shadow_request(t2, new Point(x2, y2)));
                s_step[2].setColor(LineColor.PURPLE_8);

                i_drawing_stage = 2;
                i_circle_drawing_stage = 2;

            }

            if (Math.abs((xp * xp + yp * yp) - (r1 + r2) * (r1 + r2)) < 0.0000001) {//外接線2本と内接線1本の場合
                double xq1 = r1 * (xp * (r1 - r2) + yp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double yq1 = r1 * (yp * (r1 - r2) - xp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double xq2 = r1 * (xp * (r1 - r2) - yp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double yq2 = r1 * (yp * (r1 - r2) + xp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線

                double xr1 = xq1 + x1;
                double yr1 = yq1 + y1;
                double xr2 = xq2 + x1;
                double yr2 = yq2 + y1;

                StraightLine t1 = new StraightLine(x1, y1, xr1, yr1);
                t1.orthogonalize(new Point(xr1, yr1));
                StraightLine t2 = new StraightLine(x1, y1, xr2, yr2);
                t2.orthogonalize(new Point(xr2, yr2));

                s_step[1].set(new Point(xr1, yr1), OritaCalc.shadow_request(t1, new Point(x2, y2)));
                s_step[1].setColor(LineColor.PURPLE_8);
                s_step[2].set(new Point(xr2, yr2), OritaCalc.shadow_request(t2, new Point(x2, y2)));
                s_step[2].setColor(LineColor.PURPLE_8);

                // -----------------------

                Point kouten = new Point();
                kouten.set(OritaCalc.naibun(c1, c2, r1, r2));
                StraightLine ty = new StraightLine(c1, kouten);
                ty.orthogonalize(kouten);
                s_step[3].set(OritaCalc.circle_to_straightLine_no_intersect_wo_connect_LineSegment(new Circle(kouten, (r1 + r2) / 2.0, LineColor.BLACK_0), ty));
                s_step[3].setColor(LineColor.PURPLE_8);
                // -----------------------

                i_drawing_stage = 3;
                i_circle_drawing_stage = 2;
            }

            if ((r1 + r2) * (r1 + r2) < (xp * xp + yp * yp)) {//外接線2本と内接線2本の場合
                //             ---------------------------------------------------------------
                //                                     -------------------------------------
                //                 -------               -------------   -------   -------       -------------
                double xq1 = r1 * (xp * (r1 - r2) + yp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double yq1 = r1 * (yp * (r1 - r2) - xp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double xq2 = r1 * (xp * (r1 - r2) - yp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double yq2 = r1 * (yp * (r1 - r2) + xp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double xq3 = r1 * (xp * (r1 + r2) + yp * Math.sqrt((xp * xp + yp * yp) - (r1 + r2) * (r1 + r2))) / (xp * xp + yp * yp);//共通内接線
                double yq3 = r1 * (yp * (r1 + r2) - xp * Math.sqrt((xp * xp + yp * yp) - (r1 + r2) * (r1 + r2))) / (xp * xp + yp * yp);//共通内接線
                double xq4 = r1 * (xp * (r1 + r2) - yp * Math.sqrt((xp * xp + yp * yp) - (r1 + r2) * (r1 + r2))) / (xp * xp + yp * yp);//共通内接線
                double yq4 = r1 * (yp * (r1 + r2) + xp * Math.sqrt((xp * xp + yp * yp) - (r1 + r2) * (r1 + r2))) / (xp * xp + yp * yp);//共通内接線


                double xr1 = xq1 + x1;
                double yr1 = yq1 + y1;
                double xr2 = xq2 + x1;
                double yr2 = yq2 + y1;
                double xr3 = xq3 + x1;
                double yr3 = yq3 + y1;
                double xr4 = xq4 + x1;
                double yr4 = yq4 + y1;

                StraightLine t1 = new StraightLine(x1, y1, xr1, yr1);
                t1.orthogonalize(new Point(xr1, yr1));
                StraightLine t2 = new StraightLine(x1, y1, xr2, yr2);
                t2.orthogonalize(new Point(xr2, yr2));
                StraightLine t3 = new StraightLine(x1, y1, xr3, yr3);
                t3.orthogonalize(new Point(xr3, yr3));
                StraightLine t4 = new StraightLine(x1, y1, xr4, yr4);
                t4.orthogonalize(new Point(xr4, yr4));

                s_step[1].set(new Point(xr1, yr1), OritaCalc.shadow_request(t1, new Point(x2, y2)));
                s_step[1].setColor(LineColor.PURPLE_8);
                s_step[2].set(new Point(xr2, yr2), OritaCalc.shadow_request(t2, new Point(x2, y2)));
                s_step[2].setColor(LineColor.PURPLE_8);
                s_step[3].set(new Point(xr3, yr3), OritaCalc.shadow_request(t3, new Point(x2, y2)));
                s_step[3].setColor(LineColor.PURPLE_8);
                s_step[4].set(new Point(xr4, yr4), OritaCalc.shadow_request(t4, new Point(x2, y2)));
                s_step[4].setColor(LineColor.PURPLE_8);

                //e_step[1].setcolor(3);
                //e_step[2].setcolor(3);

                i_drawing_stage = 4;
                i_circle_drawing_stage = 2;

            }
        }

        if (i_drawing_stage == 1) {

            i_drawing_stage = 0;
            i_circle_drawing_stage = 0;

            s_step[1].setColor(icol);
            addLineSegment(s_step[1]);
            record();

            return;
        }


    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_50(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_circumference.set(get_moyori_ensyuu(p));
        closest_point.set(getClosestPoint(p));

        if ((i_drawing_stage == 0) && (i_circle_drawing_stage == 0)) {
            if (OritaCalc.distance_circumference(p, closest_circumference) > d_decision_width) {
                return;
            }

            i_drawing_stage = 0;
            i_circle_drawing_stage = 1;
            e_step[1].set(closest_circumference);
            e_step[1].setColor(LineColor.GREEN_6);
            return;
        }

        if ((i_drawing_stage == 0) && (i_circle_drawing_stage == 1)) {
            //if(p.kyori(moyori_ten)>d_decision_width){return;}
            if (OritaCalc.distance_circumference(p, closest_circumference) > d_decision_width) {
                return;
            }

            i_drawing_stage = 0;
            i_circle_drawing_stage = 2;
            e_step[2].set(closest_circumference);
            e_step[2].setColor(LineColor.GREEN_6);
            return;
        }


    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_50(Point p0) {
    }

//50 50 50 50 50 50 50 50 50  ここまで  ------


//46 46 46 46 46 46 46 46 46   i_mouse_modeA==46　;反転入力モード。

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_50(Point p0) {
        if ((i_drawing_stage == 0) && (i_circle_drawing_stage == 2)) {
            i_drawing_stage = 0;
            i_circle_drawing_stage = 0;
            double add_r = (OritaCalc.distance(e_step[1].getCenter(), e_step[2].getCenter()) - e_step[1].getRadius() - e_step[2].getRadius()) * 0.5;


            if (Math.abs(add_r) > 0.00000001) {
                double new_r1 = add_r + e_step[1].getRadius();
                double new_r2 = add_r + e_step[2].getRadius();

                if ((new_r1 > 0.00000001) && (new_r2 > 0.00000001)) {
                    e_step[1].setR(new_r1);
                    e_step[1].setColor(LineColor.CYAN_3);
                    add_en(e_step[1]);
                    e_step[2].setR(new_r2);
                    e_step[2].setColor(LineColor.CYAN_3);
                    add_en(e_step[2]);
                    record();
                }
            }
        }

    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_46(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));

        closest_circumference.set(get_moyori_ensyuu(p));

        if (i_drawing_stage + i_circle_drawing_stage == 0) {
            closest_lineSegment.set(get_moyori_senbun(p));


            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) < OritaCalc.distance_circumference(p, closest_circumference)) {//線分の方が円周より近い
                i_drawing_stage = 0;
                i_circle_drawing_stage = 0;
                if (OritaCalc.distance_lineSegment(p, closest_lineSegment) > d_decision_width) {
                    return;
                }
                i_drawing_stage = 1;
                i_circle_drawing_stage = 0;
                s_step[1].set(closest_lineSegment);
                s_step[1].setColor(LineColor.GREEN_6);
                return;
            }


            i_drawing_stage = 0;
            i_circle_drawing_stage = 0;
            if (OritaCalc.distance_circumference(p, closest_circumference) > d_decision_width) {
                return;
            }

            i_drawing_stage = 0;
            i_circle_drawing_stage = 1;
            e_step[1].set(closest_circumference);
            e_step[1].setColor(LineColor.GREEN_6);
            return;
        }

        if (i_drawing_stage + i_circle_drawing_stage == 1) {
            if (OritaCalc.distance_circumference(p, closest_circumference) > d_decision_width) {
                return;
            }
            i_circle_drawing_stage = i_circle_drawing_stage + 1;
            e_step[i_circle_drawing_stage].set(closest_circumference);
            e_step[i_circle_drawing_stage].setColor(LineColor.RED_1);
            return;
        }


    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_46(Point p0) {
    }

//46 46 46 46 46 46 46 46 46  ここまで  ------


//43 43 43 43 43 43 43 43 43   i_mouse_modeA==43　;円3点入力モード。

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_46(Point p0) {
        if ((i_drawing_stage == 1) && (i_circle_drawing_stage == 1)) {

            add_hanten(s_step[1], e_step[1]);
            i_drawing_stage = 0;
            i_circle_drawing_stage = 0;

        }

        if ((i_drawing_stage == 0) && (i_circle_drawing_stage == 2)) {
            add_hanten(e_step[1], e_step[2]);
            i_drawing_stage = 0;
            i_circle_drawing_stage = 0;
        }

    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_43(Point p0) {


        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) < d_decision_width) {
            i_drawing_stage = i_drawing_stage + 1;
            s_step[i_drawing_stage].set(closest_point, closest_point);
            s_step[i_drawing_stage].setColor(LineColor.fromNumber(i_drawing_stage));
        }


    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_43(Point p0) {
    }

//43 43 43 43 43 43 43 43 43  ここまで  ------





















/*

//5555555555555555555555    i_mouse_modeA==5　

//マウス操作(ボタンを押したとき)時の作業
	public void mPressed_A_05(Ten p0) {
		//Ten p =new Ten();
		p.set(camera.TV2object(p0));
		moyori_senbun.set(get_moyori_senbun(p));
		if(oc.kyori_senbun( p,moyori_senbun)<d_decision_width){
			i_egaki_dankai=i_egaki_dankai+1;
			s_step[i_egaki_dankai].set(moyori_senbun);//s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
s_step[i_egaki_dankai].setcolor(6);
		}



	}

//マウス操作(ドラッグしたとき)を行う関数
	public void mDragged_A_05(Ten p0) {	}

//マウス操作(ボタンを離したとき)を行う関数
	public void mReleased_A_05(Ten p0){			if(i_egaki_dankai==3){i_egaki_dankai=0;}}

//------

//66666666666666666666    i_mouse_modeA==6　

//マウス操作(ボタンを押したとき)時の作業
	public void mPressed_A_06(Ten p0) {


		//Ten p =new Ten();
		p.set(camera.TV2object(p0));
		moyori_ten.set(get_moyori_ten(p));
		if(p.kyori(moyori_ten)<d_decision_width){
			i_egaki_dankai=i_egaki_dankai+1;
			s_step[i_egaki_dankai].set(moyori_ten,moyori_ten);s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
		}


	}

//マウス操作(ドラッグしたとき)を行う関数
	public void mDragged_A_06(Ten p0) {	}

//マウス操作(ボタンを離したとき)を行う関数
	public void mReleased_A_06(Ten p0){
		if(i_egaki_dankai==3){i_egaki_dankai=0;}



	}

//------



*/


//10001

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_43(Point p0) {
        if (i_drawing_stage == 3) {
            i_drawing_stage = 0;

            LineSegment sen1 = new LineSegment(s_step[1].getA(), s_step[2].getA());
            if (sen1.getLength() < 0.00000001) {
                return;
            }
            LineSegment sen2 = new LineSegment(s_step[2].getA(), s_step[3].getA());
            if (sen2.getLength() < 0.00000001) {
                return;
            }
            LineSegment sen3 = new LineSegment(s_step[3].getA(), s_step[1].getA());
            if (sen3.getLength() < 0.00000001) {
                return;
            }

            if (Math.abs(OritaCalc.angle(sen1, sen2) - 0.0) < 0.000001) {
                return;
            }
            if (Math.abs(OritaCalc.angle(sen1, sen2) - 180.0) < 0.000001) {
                return;
            }
            if (Math.abs(OritaCalc.angle(sen1, sen2) - 360.0) < 0.000001) {
                return;
            }

            if (Math.abs(OritaCalc.angle(sen2, sen3) - 0.0) < 0.000001) {
                return;
            }
            if (Math.abs(OritaCalc.angle(sen2, sen3) - 180.0) < 0.000001) {
                return;
            }
            if (Math.abs(OritaCalc.angle(sen2, sen3) - 360.0) < 0.000001) {
                return;
            }

            if (Math.abs(OritaCalc.angle(sen3, sen1) - 0.0) < 0.000001) {
                return;
            }
            if (Math.abs(OritaCalc.angle(sen3, sen1) - 180.0) < 0.000001) {
                return;
            }
            if (Math.abs(OritaCalc.angle(sen3, sen1) - 360.0) < 0.000001) {
                return;
            }


            StraightLine t1 = new StraightLine(sen1);
            t1.orthogonalize(OritaCalc.naibun(sen1.getA(), sen1.getB(), 1.0, 1.0));
            StraightLine t2 = new StraightLine(sen2);
            t2.orthogonalize(OritaCalc.naibun(sen2.getA(), sen2.getB(), 1.0, 1.0));
            add_en(OritaCalc.findIntersection(t1, t2), OritaCalc.distance(s_step[1].getA(), OritaCalc.findIntersection(t1, t2)), LineColor.CYAN_3);
            record();
        }
    }

    //マウス操作(i_mouse_modeA==10001　でボタンを押したとき)時の作業
    public void mPressed_A_10001(Point p0) {
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) < d_decision_width) {
            i_drawing_stage = i_drawing_stage + 1;
            s_step[i_drawing_stage].set(closest_point, closest_point);
            s_step[i_drawing_stage].setColor(LineColor.fromNumber(i_drawing_stage));
        }
    }

    //マウス操作(i_mouse_modeA==10001　でドラッグしたとき)を行う関数
    public void mDragged_A_10001(Point p0) {
    }

//------
//10002

    //マウス操作(i_mouse_modeA==10001　でボタンを離したとき)を行う関数
    public void mReleased_A_10001(Point p0) {
        if (i_drawing_stage == 3) {
            i_drawing_stage = 0;
        }
    }

    //マウス操作(i_mouse_modeA==10002　でボタンを押したとき)時の作業
    public void mPressed_A_10002(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_lineSegment.set(get_moyori_senbun(p));
        if (OritaCalc.distance_lineSegment(p, closest_lineSegment) < d_decision_width) {
            i_drawing_stage = i_drawing_stage + 1;
            s_step[i_drawing_stage].set(closest_lineSegment);//s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
            s_step[i_drawing_stage].setColor(LineColor.GREEN_6);
        }
    }

    //マウス操作(i_mouse_modeA==10002　でドラッグしたとき)を行う関数
    public void mDragged_A_10002(Point p0) {
    }

//------
//------
//10003

    //マウス操作(i_mouse_modeA==10002　でボタンを離したとき)を行う関数
    public void mReleased_A_10002(Point p0) {
        if (i_drawing_stage == 3) {
            i_drawing_stage = 0;
        }
    }

    //マウス操作(i_mouse_modeA==10003　でボタンを押したとき)時の作業
    public void mPressed_A_10003(Point p0) {
    }

    //マウス操作(i_mouse_modeA==10003　でドラッグしたとき)を行う関数
    public void mDragged_A_10003(Point p0) {
    }

//------

    //マウス操作(i_mouse_modeA==10003　でボタンを離したとき)を行う関数
    public void mReleased_A_10003(Point p0) {
    }

    public void setBaseState(Grid.State i) {
        grid.setBaseState(i);
    }

    public Grid.State getBaseState() {
        return grid.getBaseState();
    }

    public void setFoldLineDividingNumber(int i) {
        foldLineDividingNumber = i;
        if (foldLineDividingNumber < 1) {
            foldLineDividingNumber = 1;
        }
    }

    public void set_d_naibun_st(double ds, double dt) {
        d_naibun_s = ds;
        d_naibun_t = dt;
    }

    public void set_d_jiyuu_kaku(double d_1, double d_2, double d_3) {
        d_jiyuu_kaku_1 = d_1;
        d_jiyuu_kaku_2 = d_2;
        d_jiyuu_kaku_3 = d_3;
    }

    public void setNumPolygonCorners(int i) {
        numPolygonCorners = i;
        if (numPolygonCorners < 3) {
            foldLineDividingNumber = 3;
        }
    }

    public void setFoldLineAdditional(FoldLineAdditionalInputMode i) {
        i_foldLine_additional_old = i_foldLine_additional;
        i_foldLine_additional = i;
    }

    public void modosi_foldLineAdditional() {
        i_foldLine_additional = i_foldLine_additional_old;
    }
// ------------


    public void check1(double r_hitosii, double parallel_decision) {
        ori_s.check1(r_hitosii, parallel_decision);
    }//In ori_s, check and set the funny fold line to the selected state.

    public void fix1(double r_hitosii, double heikou_hantei) {
        while (ori_s.fix1(r_hitosii, heikou_hantei) == 1) {
        }
        //ori_s.addsenbun  delsenbunを実施しているところでcheckを実施
        if (check1) {
            check1(0.001, 0.5);
        }
        if (check2) {
            check2(0.01, 0.5);
        }
        if (check3) {
            check3(0.0001);
        }
        if (check4) {
            check4(0.0001);
        }

    }

    public void set_i_check1(boolean i) {
        check1 = i;
    }

    public void check2(double r_hitosii, double heikou_hantei) {
        ori_s.check2(r_hitosii, heikou_hantei);
    }

    public void fix2(double r_hitosii, double heikou_hantei) {
        while (ori_s.fix2(r_hitosii, heikou_hantei) == 1) {
        }
        //ori_s.addsenbun  delsenbunを実施しているところでcheckを実施
        if (check1) {
            check1(0.001, 0.5);
        }
        if (check2) {
            check2(0.01, 0.5);
        }
        if (check3) {
            check3(0.0001);
        }
        if (check4) {
            check4(0.0001);
        }

    }

    public void setCheck2(boolean i) {
        check2 = i;
    }

    public void check3(double r) {
        ori_s.check3(r);
    }

    public void check4(double r) {
        orihime_app.check4(r);
    }

    public void ap_check4(double r) {
        ori_s.check4(r);
    }


    public void setCheck3(boolean i) {
        check3 = i;
    }

    public void setCheck4(boolean i) {
        check4 = i;
    }


// *******************************************************************************************************

    public void ck4_color_sage() {
        i_ck4_color_toukado = i_ck4_color_toukado - i_ck4_color_toukado_sabun;
        if (i_ck4_color_toukado < 50) {
            i_ck4_color_toukado = i_ck4_color_toukado + i_ck4_color_toukado_sabun;
        }
    }

    public void ck4_color_age() {
        i_ck4_color_toukado = i_ck4_color_toukado + i_ck4_color_toukado_sabun;
        if (i_ck4_color_toukado > 250) {
            i_ck4_color_toukado = i_ck4_color_toukado - i_ck4_color_toukado_sabun;
        }
    }

    public void h_setcolor(LineColor i) {
        h_icol = i;
    }


    //public void  fix3(double r_hitosii,double heikou_hantei){while(ori_s.fix3(r_hitosii,heikou_hantei)==1){;}}

    public void set_Ubox_undo_suu(int i) {
        Ubox.set_i_undo_total(i);
    }

    public void set_h_Ubox_undo_suu(int i) {
        h_Ubox.set_i_undo_total(i);
    }

    public void circle_organize() {//Organize all circles.
        ori_s.circle_organize();
    }

    // ---------------------------
    public void add_hanten(Circle e0, Circle eh) {


        //e0の円周が(x,y)を通るとき
        if (Math.abs(OritaCalc.distance(e0.getCenter(), eh.getCenter()) - e0.getRadius()) < 0.0000001) {
            LineSegment s_add = new LineSegment();
            s_add.set(eh.turnAround_CircleToLineSegment(e0));
            //s_add.setcolor(3);
            addLineSegment(s_add);
            record();
            return;
        }


        //e0の円周の内部に(x,y)がくるとき
        //if(oc.kyori(e0.get_tyuusin(),eh.get_tyuusin())<e0.getr()){
        //	return;
        //}

//System.out.println("20170315  ********************3");
        //e0の円周が(x,y)を通らないとき。e0の円周の外部に(x,y)がくるとき//e0の円周の内部に(x,y)がくるとき
        Circle e_add = new Circle();
        e_add.set(eh.turnAround(e0));
        add_en(e_add);
        record();
    }

    // ---------------------------
    public void add_hanten(LineSegment s0, Circle eh) {
        StraightLine ty = new StraightLine(s0);
        //s0上に(x,y)がくるとき
        if (ty.calculateDistance(eh.getCenter()) < 0.0000001) {
            return;
        }

        //s0が(x,y)を通らないとき。
        Circle e_add = new Circle();
        e_add.set(eh.turnAround_LineSegmentToCircle(s0));
        add_en(e_add);
        record();
    }

    public double get_d_decision_width() {
        return d_decision_width;
    }


    //public double get_kus.d_haba()(){return kus.d_haba();	}

    public void set_a_to_parallel_scale_interval(int i) {
        grid.set_a_to_parallel_scale_interval(i);
    }

    public void set_b_to_parallel_scale_interval(int i) {
        grid.set_b_to_parallel_scale_interval(i);
    }

    public void a_to_heikouna_memori_iti_idou() {
        grid.a_to_parallel_scale_position_change();
    }

    public void b_to_heikouna_memori_iti_idou() {
        grid.b_to_parallel_scale_position_change();
    }

    //--------------------------------------------
    public void test1() {//デバック等のテスト用

        System.out.println("ori_s.getsousuu()  " + ori_s.getTotal());

    }



/*
一値分解する関数itti_bunkai();

public void itti_bunkai(){//（１）2点a,bを指定







}

（１）2点a,bを指定
（２）aを基点とするベクトルabが最初にぶつかる折線との交点cを求める。abと重なる折線は無視
（３）
cが既存の折線の柄の部分だった場合、その線で鏡映し、cをaとし、bを鏡映線の先の点として再帰的に。
cが点だった場合、すでに通過していた点なら、return;


cからベクトルacと一値性を持つベクトルを求める、






*/

    public enum OperationFrameMode {
        NONE_0,
        CREATE_1,
        MOVE_POINTS_2,
        MOVE_SIDES_3,
        MOVE_BOX_4,
    }

    //--------------------------------------------

    //メモ
    //icol=0 black
    //icol=1 red
    //icol=2 blue
    //icol=3 cyan
    //icol=4 orange
    //icol=5 mazenta
    //icol=6 green
    //icol=7 yellow
    //icol=8 new Color(210,0,255) //紫


}
