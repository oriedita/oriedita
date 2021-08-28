package origami_editor.editor;

import origami_editor.editor.drawing_worker.Drawing_Worker;
import origami_editor.editor.folded_figure.FoldedFigure;
import origami_editor.editor.folded_figure.FoldedFigure_01;
import origami_editor.editor.layout.WrapLayout;
import origami_editor.graphic2d.grid.Grid;
import origami_editor.graphic2d.linesegment.LineSegment;
import origami_editor.graphic2d.oritacalc.OritaCalc;
import origami_editor.graphic2d.point.Point;
import origami_editor.record.memo.Memo;
import origami_editor.record.string_op.StringOp;
import origami_editor.tools.background_camera.Background_camera;
import origami_editor.tools.camera.Camera;
import origami_editor.tools.bulletinboard.BulletinBoard;
import origami_editor.tools.linestore.LineSegmentSet;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

import static origami_editor.editor.ResourceUtil.createImageIcon;

public class App extends JFrame implements ActionListener {

    public FoldedFigure temp_OZ = new FoldedFigure(this);    //Folded figure
    public FoldedFigure OZ;    //Folded figure
    public LineSegmentSet Ss0;//折畳み予測の最初に、ts1.Senbunsyuugou2Tensyuugou(Ss0)として使う。　Ss0は、es1.get_for_oritatami()かes1.get_for_select_oritatami()で得る。
    public BulletinBoard bulletinBoard = new BulletinBoard(this);
    public Camera camera_of_orisen_input_diagram = new Camera();
    public boolean antiAlias = false;//展開図のアンチェイリアスをするかしないか。する=1、しない=0
    public int pointSize = 1;//Specify the shape of the points in the development view
    public LineStyle lineStyle = LineStyle.COLOR;//折線の表現、1＝色、2=色と形状、3=黒で1点鎖線、4=黒で2点鎖線
    public JButton Button_F_color;                    //折り上がり図の表の色の指定に用いる
    public JButton Button_B_color;                    //折り上がり図の裏の色の指定に用いる
    public JButton Button_L_color;                    //折り上がり図の線の色の指定に用いる
    public Color sen_tokutyuu_color = new Color(100, 200, 200);//補助線や円の色を特注的に変える場合の指定色
    public JButton Button_sen_tokutyuu_color;                    //折り上がり図の表の色の指定に用いる
    public JTextField text1;
    public int nyuuryoku_kitei = 0;    //格子の分割数　　　　（入力規定の指定。0なら規定無し、1なら蛇腹入力。
    public JTextField text18;
    public double d_grid_x_a = 1.0;
    public JTextField text19;
    public double d_grid_x_b = 0.0;
    public JTextField text20;
    public double d_grid_x_c = 0.0;
    public JTextField text21;
    public double d_grid_y_a = 1.0;
    public JTextField text22;
    public double d_grid_y_b = 0.0;
    public JTextField text23;
    public double d_grid_y_c = 0.0;


    //アプレット用public void init()または、アプリケーション用public ap() 以外のクラスでも使用されるパネルの部品の宣言はここでしておく。
    //アプレット用public void init()または、アプリケーション用public ap() の中だけで使用されるパネルの部品の宣言ぅラスの中でする。
    //Those that basically change the appearance of the parts are declared here.
    public JTextField text24;
    public double d_grid_angle = 90.0;
    public JTextField text25;
    public int scale_interval = 5;
    public JTextField text29;//double d_oriagarizu_syukusyaku_keisuu=1.0;//折り上がり図の縮尺係数
    public JTextField text30;
    public JCheckBox ckbox_mouse_settings;//マウスの設定。チェックがあると、ホイールマウスとして動作設定
    public JCheckBoxMenuItem ckbox_point_search;//点を探す範囲
    public JCheckBoxMenuItem ckbox_ten_hanasi;//点を離すかどうか
    public JCheckBoxMenuItem ckbox_kou_mitudo_nyuuryoku;//高密度用入力をするかどうか
    public JCheckBoxMenuItem ckbox_bun;//文章
    public JCheckBoxMenuItem ckbox_cp;//折線
    public JCheckBoxMenuItem ckbox_a0;//補助活線cyan
    public JCheckBoxMenuItem ckbox_a1;//補助画線
    public JCheckBox ckbox_check1;//check1
    public JCheckBox ckbox_check2;//check2
    public JCheckBox ckbox_check3;//check3
    public JCheckBox ckbox_check4;//check4
    public JCheckBoxMenuItem ckbox_mark;//Marking lines such as crosses and reference planes
    public JCheckBoxMenuItem ckbox_cp_ue;//展開図を折り上がり予想図の上に描く
    public JCheckBox ckbox_folding_keika;//折り上がり予想の途中経過の書き出し
    public JCheckBox ckbox_cp_kaizen_folding;//cpを折畳み前に自動改善する。
    public JCheckBox ckbox_select_nokosi;//select状態を他の操作をしてもなるべく残す
    public JCheckBox ckbox_toukazu_color;//透過図をカラー化する。
    public int iLineWidth = 1;//The thickness of the line in the development view.
    public int i_h_lineWidth = 3;//Line thickness of non-interference auxiliary line
    public MouseMode i_mouse_modeA = MouseMode.FOLDABLE_LINE_DRAW_71;//Defines the response to mouse movements. If it is 1, the line segment input mode. If it is 2, adjust the development view (move). If it is 101, operate the folded figure.
    public SelectionOperationMode selectionOperationMode;//Specify which operation to perform when selecting and operating the mouse. It is used to select a selected point after selection and automatically switch to the mouse operation that is premised on selection.
    // ------------------------------------------------------------------------
    public Point point_of_referencePlane_old = new Point(); //ten_of_kijyunmen_old.set(OZ.ts1.get_ten_of_kijyunmen_tv());//20180222折り線選択状態で折り畳み推定をする際、以前に指定されていた基準面を引き継ぐために追加
    // *******************************************************************************************************
    public double d_grid_x_length;
    public double d_grid_y_length;
    ////b* アプリケーション用。先頭が／＊／／／で始まる行にはさまれた部分は無視される。
    FileDialog fd;
    double r = 3.0;                   //基本枝構造の直線の両端の円の半径、枝と各種ポイントの近さの判定基準
    public final Drawing_Worker es1 = new Drawing_Worker(r, this);    //Basic branch craftsman. Accepts input from the mouse.
    public Grid kus = es1.grid;
    Memo memo1 = new Memo();
    SubThread sub;
    boolean subThreadRunning = false;//1 if SubThread (folding calculation) is running, 0 if not running
    ArrayList<FoldedFigure> OAZ = new ArrayList<>(); //Instantiation of fold-up diagram
    int i_OAZ = 0;//Specify which number of OAZ Oriagari_Zu is the target of button operation or transformation operation 
    Background_camera h_cam = new Background_camera();
    LineColor icol;//基本枝職人の枝の色を指定する。0は黒、1は赤、2は赤。//icol=0 black	//icol=1 red	//icol=2 blue	//icol=3 cyan	//icol=4 orange	//icol=5 mazenta	//icol=6 green	//icol=7 yellow	//icol=8 new Color(210,0,255) //紫
    LineColor h_icol;//補助線の枝の色を指定する。
    MouseMode iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.DRAW_CREASE_FREE_1;//Number of work to be performed after specifying the color of black, red, blue, and water
    boolean w_image_running = false; // Folding together execution. If a single image export is in progress, it will be true.
    String fname_and_number;//まとめ書き出しに使う。
    //各種変数の定義
    String frame_title_0;//フレームのタイトルの根本部分
    String frame_title;//フレームのタイトルの全体
    Drawing_Worker.FoldLineAdditionalInputMode foldLineAdditionalInputMode = Drawing_Worker.FoldLineAdditionalInputMode.POLY_LINE_0;//=0は折線入力　=1は補助線入力モード
    AngleSystemInputType angle_system_input_id = AngleSystemInputType.DEG_1;//Specifying the input method of the angle system angle_system_input_id = AngleSystemInputType.DEG_1 specifies the line segment, 2 specifies 2 points
    int id_kakudo_kei_a = 12;//角度系の180度を割る数の格納_a
    int id_kakudo_kei_b = 8;//角度系の180度を割る数の格納_b
    JButton Button0b;                    //対称性の指定に用いる
    JButton Button_another_solution;                    //操作の指定に用いる（追加推定一個だけ）
    JButton Button_AS_matome;                    //操作の指定に用いる（追加推定100個）
    JButton Button_bangou_sitei_estimated_display;
    JButton Button_grid_increase;
    JButton Button_grid_decrease;
    JButton ButtonCol_black;                    //折線の色の指定に用いる
    JButton ButtonCol_blue;                    //折線の色の指定に用いる
    JButton ButtonCol_red;                    //折線の色の指定に用いる
    JButton ButtonCol_cyan;                    //折線(補助線)の色の指定に用いる
    JButton Button_Col_orange;                    //補助線1の色の指定に用いる
    JButton Button_Col_yellow;                    //補助線2の色の指定に用いる
    JButton Button_background_Lock_on;//背景のロックオン
    JButton Button_background_kirikae;//背景を表示するかどうかの指定
    JButton Button_kakudo_kei_a;            //角度系で180を割る数を格納_a
    JButton Button_kakudo_kei_b;            //角度系で180を割る数を格納_b
    JButton Button_M_nisuru;                    //元がどんな種類の折線でも、山折りにする
    JButton Button_V_nisuru;                    //元がどんな種類の折線でも、谷折りにする
    JButton Button_E_nisuru;                    //元がどんな種類の折線でも、境界線もしくは山谷未設定線にする
    JButton Button_HK_nisuru;                    //元がどんな種類の折線でも、補助活線にする
    JButton Button_senbun_henkan2;//線分の色を赤から青、青から赤に変換
    JTextField text2;
    int foldLineDividingNumber = 1;//free折線入力で、折線の等分割されている数
    JTextField text3;
    double d_orisen_internalDivisionRatio_a = 1.0;
    JTextField text4;
    double d_orisen_internalDivisionRatio_b = 0.0;
    JTextField text5;
    double d_orisen_internalDivisionRatio_c = 0.0;
    JTextField text6;
    double d_orisen_internalDivisionRatio_d = 0.0;
    JTextField text7;
    double d_orisen_internalDivisionRatio_e = 1.0;
    JTextField text8;
    double d_orisen_internalDivisionRatio_f = 2.0;
    JTextField text9;
    int numPolygonCorners = 5;
    JTextField text10;
    int i_undo_suu = 20;//text31はtext10を参考にしている
    JTextField text11;
    int i_h_undo_suu = 20;
    JTextField text12;
    double d_restricted_angle_a = 40.0;
    JTextField text13;
    double d_restricted_angle_b = 60.0;
    JTextField text14;
    double d_restricted_angle_c = 80.0;
    JTextField text15;
    double d_restricted_angle_d = 30.0;
    JTextField text16;
    double d_restricted_angle_e = 50.0;
    JTextField text17;
    double d_restricted_angle_f = 100.0;
    JTextField text26;
    int foldedCases = 1;//Specify the number of folding estimation to be displayed
    JTextField text27;
    double scaleFactor = 1.0;//Scale factor
    JTextField text28;
    double rotationCorrection = 0.0;//Correction angle of rotation display angle
    JTextField text31;
    int i_undo_suu_om = 5;//text31はtext10を参考にしている
    boolean displayPointSpotlight;
    boolean displayPointOffset;
    boolean displayGridInputAssist;
    boolean displayComments;
    boolean displayCpLines;
    boolean displayAuxLines;
    boolean displayLiveAuxLines;
    boolean displayMarkings;
    boolean displayCreasePatternOnTop;
    boolean displayFoldingProgress;
    JLabel label_length_sokutei_1 = new JLabel("");
    JLabel label_length_sokutei_2 = new JLabel("");
    JLabel label_kakudo_sokutei_1 = new JLabel("");
    // バッファー画面用設定はここまでAAAAAAAAAAAAAAAAAAA
    JLabel label_kakudo_sokutei_2 = new JLabel("");
    JLabel label_kakudo_sokutei_3 = new JLabel("");
    Image img_background;       //Image for background
    String img_background_fname;
    Image img_explanation;       //Image for explanation
    String img_explanation_fname;
    // Buffer screen settings VVVVVVVVVVVVVVVVVVVVVVVVV
    Canvas canvas;
    boolean lockBackground_ori = false;//Lock on background = 1, not = 0
    boolean lockBackground = false;//Lock on background = 1, not = 0
    Point p_mouse_object_position = new Point();//マウスのオブジェクト座標上の位置
    Point p_mouse_TV_position = new Point();//マウスのTV座標上の位置
    // Applet width and height
    Dimension dim;
    boolean displayBackground = false;//If it is 0, the background is not displayed. If it is 1, display it. There is no 2.
    boolean displayExplanation = true;//If it is 0, the explanation is not displayed. If it is 1, display it. There is no 2.
    float fLineWidth = (float) iLineWidth;
    // subThreadMode Subthread operation rules.
    // 0 = Execution of folding estimate 5. It is not a mode to put out different solutions of folding estimation at once.
    // 1 = Execution of folding estimate 5. Another solution for folding estimation is put together.
    // 2 =
    float f_h_lineWidth = (float) i_h_lineWidth;
    //Runnableインターフェイスを実装しているので、myThスレッドの実行内容はrunメソッドに書かれる
    //アプレットでのスレッドの使い方は、”初体験Java”のP231参照
    boolean i_mouseDragged_valid = false;
    boolean i_mouseReleased_valid = false;//0 ignores mouse operation. 1 is valid for mouse operation. When an unexpected mouseDragged or mouseReleased occurs due to on-off of the file box, set it to 0 so that it will not be picked up. These are set to 1 valid when the mouse is clicked.
    SubThread.Mode subThreadMode = SubThread.Mode.FOLDING_ESTIMATE_0;
    Thread myTh;                              //スレッドクラスのインスタンス化
    //画像出力するため20170107_oldと書かれた行をコメントアウトし、20170107_newの行を有効にした。
    //画像出力不要で元にもどすなら、20170107_oldと書かれた行を有効にし、20170107_newの行をコメントアウトにすればよい。（この変更はOrihime.javaの中だけに2箇所ある）
    // オフスクリーン
    BufferedImage offsc_background = null;//20181205add
    boolean flg61 = false;//Used when setting the frame 　20180524
    //= 1 is move, = 2 is move4p, = 3 is copy, = 4 is copy4p, = 5 is mirror image
    String fname_wi;
    JButton Button_move;
    JButton Button_move_2p2p;
    JButton Button_copy_paste;
    JButton Button_copy_paste_2p2p;
    JButton Button_kyouei;
    //ウィンドウ透明化用のパラメータ
    BufferedImage imageT;
    //Vector from the upper left to the limit position where the drawing screen can be seen in the upper left
    int upperLeft_ix = 0;
    int upperLeft_iy = 0;
    //Vector from the lower right corner to the limit position where the drawing screen can be seen in the lower right corner
    int lowerRight_ix = 0;
    int lowerRight_iy = 0;
    JDialog add_frame;
    boolean i_add_frame = false;//1=add_frameが存在する。,0=存在しない。
    boolean ckbox_add_frame_SelectAnd3click_isSelected = false;//1=折線セレクト状態でトリプルクリックするとmoveやcopy等の動作モードに移行する。 20200930
    // **************************************************************************************************************************
// **************************************************************************************************************************
// **************************************************************************************************************************
    boolean i_mouse_right_button_on = false;//1 if the right mouse button is on, 0 if off
    boolean i_mouse_undo_redo_mode = false;//1 for undo and redo mode with mouse
    public enum MouseWheelTarget {
        CREASEPATTERN_0,
        FOLDED_FRONT_1,
        FOLDED_BACK_2,
        TRANSPARENT_FRONT_3,
        TRANSPARENT_BACK_4,
    }
    MouseWheelTarget i_cp_or_oriagari = MouseWheelTarget.CREASEPATTERN_0;//0 if the target of the mouse wheel is a cp development view, 1 if it is a folded view (front), 2 if it is a folded view (back), 3 if it is a transparent view (front), 4 if it is a transparent view (back)
    int i_ClickCount = 0;//Don't you need this variable? 21181208
    double d_ap_check4 = 0.0;
    ////b* アプリケーション用。先頭が／＊／／／で始まる行にはさまれた部分は無視される。
    public App() {
        setTitle("Origami Editor 1.0.0");//Specify the title and execute the constructor
        frame_title_0 = getTitle();
        frame_title = frame_title_0;//Store title in variable
        es1.setTitle(frame_title);

        //--------------------------------------------------------------------------------------------------
        addWindowListener(new WindowAdapter() {//ウィンドウの状態が変化したときの処理
            //終了ボタンを有効化
            public void windowClosing(WindowEvent evt) {
                System.out.println("windowClosing_20200928");
                closing();//Work to be done when pressing X at the right end of the upper side of the window
            }//終了ボタンを有効化 ここまで。

            public void windowOpened(WindowEvent eve) {
                System.out.println("windowOpened_20200928");
            }

            public void windowClosed(WindowEvent eve) {
                System.out.println("windowClosed_20200928");
            }

            public void windowIconified(WindowEvent eve) {
                System.out.println("windowIconified_20200928");
            }

            public void windowDeiconified(WindowEvent eve) {
                System.out.println("windowDeiconified_20200928");
            }

            public void windowActivated(WindowEvent eve) {
                System.out.println("windowActivated_20200928");
            }

            public void windowDeactivated(WindowEvent eve) {
                System.out.println("windowDeactivated_20200928");
            }

            public void windowStateChanged(WindowEvent eve) {
                System.out.println("windowStateChanged_20200928");
            }
        });//Processing when the window state changes Up to here.

        //--------------------------------------------------------------------------------------------------
        addWindowFocusListener(new WindowAdapter() {//オリヒメのメインウィンドウのフォーカスが変化したときの処理
            public void windowGainedFocus(WindowEvent evt) {
                System.out.println("windowGainedFocus_20200929");
            }

            public void windowLostFocus(WindowEvent evt) {
                System.out.println("windowLostFocus_20200929");
            }
        });//オリヒメのメインウィンドウのフォーカスが変化したときの処理 ここまで。
        //--------------------------------------------------------------------------------------------------

//        setVisible(true);                 //アプレットの時は使わない。アプリケーションの時は使う。かな
////b* アプリケーション用。先頭が／＊／／／で始まる行にはさまれた部分は無視される。

        //バッファー画面の設定 ------------------------------------------------------------------
        // 幅と高さをたずねる
        dim = getSize();
        System.out.println(" dim 001 :" + dim.width + " , " + dim.height);//多分削除可能

        //バッファー画面の設定はここまで----------------------------------------------------

        OAZ.clear();
        OAZ_add_new_Oriagari_Zu();
        OZ = OAZ.get(0);//折りあがり図

        //カメラの設定 ------------------------------------------------------------------
        //camera_of_orisen_nyuuryokuzu	;
        camera_of_orisen_input_diagram.setCameraPositionX(0.0);
        camera_of_orisen_input_diagram.setCameraPositionY(0.0);
        camera_of_orisen_input_diagram.setCameraAngle(0.0);
        camera_of_orisen_input_diagram.setCameraMirror(1.0);
        camera_of_orisen_input_diagram.setCameraZoomX(1.0);
        camera_of_orisen_input_diagram.setCameraZoomY(1.0);
        camera_of_orisen_input_diagram.setDisplayPositionX(350.0);
        camera_of_orisen_input_diagram.setDisplayPositionY(350.0);

        OZ.foldedFigure_camera_initialize();


        //camera_haikei	;
        //カメラの設定はここまで----------------------------------------------------
        icol = LineColor.NONE;
        //step=1;
        myTh = null;
        // 初期表示

        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("fishbase.png")));

        // レイアウトの作成レイアウトの作成の部分は”初体験Java”のP179等を参照

        Container contentPane = getContentPane();//JFrame用
        contentPane.setLayout(new BorderLayout());

        canvas = new Canvas(this);
        contentPane.add("Center", canvas);

        // *************************************************
        //上辺（北側）パネルの構築*************************
        // *************************************************
        //上辺（北側）パネルの作成
//pnln10定義済
//pnln11定義済
//pnln12定義済
//pnln13定義済
//pnln14定義済
//pnln15未定義
//pnln20未定義
//pnln25未定義
//pnln30未定義

        JMenuBar menuBar = new JMenuBar();

        setJMenuBar(menuBar);


        JPanel pnln = new JPanel();
//         pnln.setBackground(Color.PINK);//new Color(red,green,blue)
        pnln.setLayout(new FlowLayout(FlowLayout.LEFT));
        //上辺（北側）パネルをレイアウトに貼り付け

        contentPane.add("North", pnln); //Frame用

        //Buttonを作ってパネルにはりつける。
////b* アプリケーション用。先頭が／＊／／／で始まる行にはさまれた部分は無視される。

// ****************************************************************************************************************************

        //------------------------------------------------
        JMenu pnln1 = new JMenu("File");
        pnln1.setMnemonic('F');

        menuBar.add(pnln1);
        //------------------------------------------------


// **********************************************************************************************************************************************************
// **********************************************************************************************************************************************************
// **********************************************************************************************************************************************************
// -------------ボタンの定義の先頭　ファイル読み込み
// **********************************************************************************************************************************************************
// **********************************************************************************************************************************************************
// **********************************************************************************************************************************************************

// ******************************************************************************データ読み込み

        JMenuItem Button_yomi = new JMenuItem("Open");
        Button_yomi.setMnemonic('O');
        Button_yomi.addActionListener(e -> {
            img_explanation_fname = "qqq/yomi.png";
            updateExplanation();

            Button_kyoutuu_sagyou();

            i_mouseDragged_valid = false;
            i_mouseReleased_valid = false;
            Memo memo_temp;

            System.out.println("readFile2Memo() 開始");
            memo_temp = readFile2Memo();
            System.out.println("readFile2Memo() 終了");

            if (memo_temp.getLineCount() > 0) {
                //展開図の初期化　開始
                developmentView_initialization();
                //展開図パラメータの初期化
                es1.reset();                                                //描き職人の初期化

                es1.setBaseState(Grid.State.HIDDEN);

                icol = LineColor.RED_1;
                es1.setColor(icol);                                        //最初の折線の色を指定する。0は黒、1は赤、2は青。
                ButtonCol_reset();
                ButtonCol_red.setForeground(Color.black);
                ButtonCol_red.setBackground(Color.red);    //折線のボタンの色設定
                //展開図の初期化　終了


                //折畳予測図のの初期化　開始
                OZ = temp_OZ;//20171223この行は不要かもしれないが、一瞬でもOZが示すOriagari_Zuがなくなることがないように念のために入れておく
                OAZ.clear();
                OAZ_add_new_Oriagari_Zu();
                set_i_OAZ(0);
                configure_syokika_yosoku();

                Button_F_color.setBackground(OZ.foldedFigure_F_color);    //ボタンの色設定
                Button_B_color.setBackground(OZ.foldedFigure_B_color);    //ボタンの色設定
                Button_L_color.setBackground(OZ.foldedFigure_L_color);    //ボタンの色設定
                //折畳予測図のの初期化　終了

                es1.setCamera(camera_of_orisen_input_diagram);//20170702この１行を入れると、解凍したjarファイルで実行し、最初にデータ読み込んだ直後はホイールでの展開図拡大縮小ができなくなる。jarのままで実行させた場合はもんだいないようだ。原因不明。
                es1.setMemo_for_reading(memo_temp);
                es1.record();


// -----------------20180503追加
                scaleFactor = camera_of_orisen_input_diagram.getCameraZoomX();
                text27.setText(String.valueOf(scaleFactor)); //縮尺係数
                text27.setCaretPosition(0);

                rotationCorrection = camera_of_orisen_input_diagram.getCameraAngle();
                text28.setText(String.valueOf(rotationCorrection));//回転表示角度の補正係数
                text28.setCaretPosition(0);
// -----------------20180503追加ここまで
            }
        });

        Button_yomi.setMargin(new Insets(0, 0, 0, 0));
        pnln1.add(Button_yomi);

        //重要注意　読み込みや書き出しでファイルダイアログのボックスが開くと、それをフレームに重なる位置で操作した場合、ファイルボックスが消えたときに、
        //マウスのドラッグとリリースが発生する。このため、余計な操作がされてしまう可能性がある。なお、このときマウスクリックは発生しない。
        // i_mouseDragged_valid=0;や i_mouseReleased_valid=0;は、この余計な操作を防ぐために指定している。


// ******************************************************************************データ書き出し
        JMenuItem Button_kaki = new JMenuItem("Save");
        Button_kaki.setMnemonic('S');
        Button_kaki.addActionListener(e -> {
            img_explanation_fname = "qqq/kaki.png";
            updateExplanation();
            Button_kyoutuu_sagyou();
            i_mouseDragged_valid = false;
            i_mouseReleased_valid = false;
            writeMemo2File();
            es1.record();
        });
        Button_kaki.setMargin(new Insets(0, 0, 0, 0));
        pnln1.add(Button_kaki);


// ******北************************************************************************

        //Button	Button_senbun_nyuryoku	= new Button(	"L_draw"	);Button_senbun_nyuryoku.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e) {
// -----61;長方形内選択モード。
        JButton Button_tyouhoukei_select = new JButton("");
        Button_tyouhoukei_select.addActionListener(e -> {
            img_explanation_fname = "qqq/tyouhoukei_select.png";
            updateExplanation();
            foldLineAdditionalInputMode = Drawing_Worker.FoldLineAdditionalInputMode.POLY_LINE_0;//=0は折線入力　=1は補助線入力モード
            es1.setFoldLineAdditional(foldLineAdditionalInputMode);//このボタンと機能は補助絵線共通に使っているのでi_orisen_hojyosenの指定がいる
            i_mouse_modeA = MouseMode.OPERATION_FRAME_CREATE_61;
            iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.DRAW_CREASE_FREE_1;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnln.add(Button_tyouhoukei_select);


        Button_tyouhoukei_select.setMargin(new Insets(0, 0, 0, 0));
        Button_tyouhoukei_select.setIcon(createImageIcon("ppp/tyouhoukei_select.png"));

// ------61;長方形内選択モード。ここまで


// *****************************************************************************画像書き出し
        JButton Button_writeImage = new JButton("Im_s");
        Button_writeImage.addActionListener(e -> {
            img_explanation_fname = "qqq/writeImage.png";
            updateExplanation();
            if (i_mouse_modeA != MouseMode.OPERATION_FRAME_CREATE_61) {
                Button_kyoutuu_sagyou();
                es1.setDrawingStage(0);
            }//枠設定時(==61)には、その枠を消さないためにes1.set_i_egaki_dankaiを０にしないでおく　20180524
            i_mouseDragged_valid = false;
            i_mouseReleased_valid = false;

            writeImage();
            canvas.repaint();
        });
        Button_writeImage.setMargin(new Insets(0, 0, 0, 0));
        pnln.add(Button_writeImage);

// ******************************************************************************
////b* アプリケーション用。先頭が／＊／／／で始まる行にはさまれた部分は無視される。
// ******************************************************************************


// ******************************************************************************

// ******北************************************************************************表示するものの選択

//ここからチェックボックスの連続

        JMenu menu_view = new JMenu("View");
        menu_view.setMnemonic('V');

        menuBar.add(menu_view);
        //------------------------------------------------
        JPanel pnln13 = new JPanel();
        pnln13.setLayout(new GridLayout(1, 7));

        pnln.add(pnln13);
        //------------------------------------------------

//マウス設定
        ckbox_mouse_settings = new JCheckBox("");
        ckbox_mouse_settings.addActionListener(e -> {
            img_explanation_fname =
                    "qqq/ckbox_mouse_settei.png";
            updateExplanation();
            canvas.repaint();
        });
        ckbox_mouse_settings.setIcon(createImageIcon("ppp/ckbox_mouse_settei_off.png"));
        ckbox_mouse_settings.setSelectedIcon(createImageIcon("ppp/ckbox_mouse_settei_on.png"));

        ckbox_mouse_settings.setMargin(new Insets(0, 0, 0, 0));
        pnln13.add(ckbox_mouse_settings);


// -------------------------------------------------------------------
//点探し
        ckbox_point_search = new JCheckBoxMenuItem("Show point range");
        ckbox_point_search.addActionListener(e -> {
            img_explanation_fname = "qqq/ckbox_ten_sagasi.png";
            updateExplanation();
            canvas.repaint();
        });
        menu_view.add(ckbox_point_search);

// -------------------------------------------------------------------
//点離し
        ckbox_ten_hanasi = new JCheckBoxMenuItem("Offset cursor");
        ckbox_ten_hanasi.addActionListener(e -> {
            img_explanation_fname =
                    "qqq/ckbox_ten_hanasi.png";
            updateExplanation();

            canvas.repaint();
        });
        menu_view.add(
                ckbox_ten_hanasi);
// -------------------------------------------------------------------
//高密度入力
        ckbox_kou_mitudo_nyuuryoku = new JCheckBoxMenuItem("Grid input assist");
        ckbox_kou_mitudo_nyuuryoku.addActionListener(e -> {
            img_explanation_fname =
                    "qqq/ckbox_kou_mitudo_nyuuryoku.png";
            updateExplanation();

            if (ckbox_kou_mitudo_nyuuryoku.isSelected()) {
                System.out.println(" kou_mitudo_nyuuryoku on");
                es1.set_i_kou_mitudo_nyuuryoku(true);
            } else {
                System.out.println(" kou_mitudo_nyuuryoku off");
                es1.set_i_kou_mitudo_nyuuryoku(false);
            }
            canvas.repaint();
        });
        menu_view.add(ckbox_kou_mitudo_nyuuryoku);
// -------------------------------------------------------------------

//文表示
        ckbox_bun = new JCheckBoxMenuItem("Display comments");
        ckbox_bun.addActionListener(e -> {
            img_explanation_fname = "qqq/ckbox_bun.png";
            updateExplanation();
            canvas.repaint();
        });
        menu_view.add(ckbox_bun);
// -------------------------------------------------------------------
//折線表示
        ckbox_cp = new JCheckBoxMenuItem("Display cp lines");
        ckbox_cp.addActionListener(e -> {
            img_explanation_fname = "qqq/ckbox_cp.png";
            updateExplanation();
            canvas.repaint();
        });
        menu_view.add(ckbox_cp);
// -------------------------------------------------------------------
//補助活線表示
        ckbox_a0 = new JCheckBoxMenuItem("Display aux lines");
        ckbox_a0.addActionListener(e -> {
            img_explanation_fname = "qqq/ckbox_a0.png";
            updateExplanation();
            canvas.repaint();
        });
        menu_view.add(ckbox_a0);
// -------------------------------------------------------------------
//補助画線表示
        ckbox_a1 = new JCheckBoxMenuItem("Display live aux lines");
        ckbox_a1.addActionListener(e -> {
            img_explanation_fname = "qqq/ckbox_a1.png";
            updateExplanation();
            canvas.repaint();
        });
        menu_view.add(ckbox_a1);
// -------------------------------------------------------------------
//十字や基準面などの目印画線
        ckbox_mark = new JCheckBoxMenuItem("Display standard face marks");
        ckbox_mark.addActionListener(e -> {
            img_explanation_fname =
                    "qqq/ckbox_mejirusi.png";
            updateExplanation();

            canvas.repaint();
        });
//        ckbox_mark.setIcon(createImageIcon(
//                "ppp/ckbox_mejirusi_off.png"));
//        ckbox_mark.setSelectedIcon(createImageIcon(
//                "ppp/ckbox_mejirusi_on.png"));

        ckbox_mark.setMargin(new Insets(0, 0, 0, 0));
        menu_view.add(
                ckbox_mark);

// -------------------------------------------------------------------
//折りあがり図を補助線の手前側にするかどうか
        ckbox_cp_ue = new JCheckBoxMenuItem("Crease pattern on top");
        ckbox_cp_ue.addActionListener(e -> {
            img_explanation_fname =
                    "qqq/ckbox_cp_ue.png";
            updateExplanation();

            canvas.repaint();
        });
//        ckbox_cp_ue.setIcon(createImageIcon(
//                "ppp/ckbox_cp_ue_off.png"));
//        ckbox_cp_ue.setSelectedIcon(createImageIcon(
//                "ppp/ckbox_cp_ue_on.png"));

        ckbox_cp_ue.setMargin(new Insets(0, 0, 0, 0));
        menu_view.add(ckbox_cp_ue);

// -------------------------------------------------------------------
//折り畳み経過の表示
        ckbox_folding_keika = new JCheckBox("");
        ckbox_folding_keika.addActionListener(e -> {
            img_explanation_fname =
                    "qqq/ckbox_oritatami_keika.png";
            updateExplanation();

            canvas.repaint();
        });
        ckbox_folding_keika.setIcon(createImageIcon(
                "ppp/ckbox_oritatami_keika_off.png"));
        ckbox_folding_keika.setSelectedIcon(createImageIcon(
                "ppp/ckbox_oritatami_keika_on.png"));

        ckbox_folding_keika.setMargin(new Insets(0, 0, 0, 0));

// ******北************************************************************************


        //------------------------------------------------
        JPanel pnln10 = new JPanel();
        pnln10.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
        pnln10.setBorder(new LineBorder(Color.black, 1));

        pnln.add(pnln10);
        //------------------------------------------------
// ******北************************************************************************線分入力モード。比率入力
// -----1;線分入力モード。比率入力

        //------------------------------------------------
        JPanel pnln11 = new JPanel();
        pnln11.setBackground(Color.white);
        pnln11.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
        pnln11.setBorder(new LineBorder(Color.black, 1));

        pnln10.add(pnln11);
        //------------------------------------------------

        text3 = new JTextField("", 2);
        text3.setHorizontalAlignment(JTextField.RIGHT);
        pnln11.add(text3);

        JLabel Lb01;
        Lb01 = new JLabel();
        Lb01.setSize(new Dimension(6,20));
        Lb01.setIcon(createImageIcon("ppp/plus.png"));
        pnln11.add(Lb01);

        text4 = new JTextField("", 2);

        text4.setHorizontalAlignment(JTextField.RIGHT);
        pnln11.add(text4);

        JLabel Lb02;
        Lb02 = new JLabel();
        Lb01.setSize(new Dimension(9,20));
        Lb02.setIcon(createImageIcon("ppp/root.png"));
        pnln11.add(Lb02);

        text5 = new JTextField("", 2);
        text5.setHorizontalAlignment(JTextField.RIGHT);
        pnln11.add(text5);

        JLabel Lb03;
        Lb03 = new JLabel();
        Lb01.setSize(new Dimension(5,23));
        Lb03.setIcon(createImageIcon("ppp/tenten.png"));
        pnln10.add(Lb03);

        //------------------------------------------------
        JPanel pnln12 = new JPanel();
        pnln12.setBackground(Color.white);
        pnln12.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
        pnln12.setBorder(new LineBorder(Color.black, 1));

        pnln10.add(pnln12);
        //------------------------------------------------

        text6 = new JTextField("", 2);
        text6.setHorizontalAlignment(JTextField.RIGHT);
        pnln12.add(text6);

        JLabel Lb04;
        Lb04 = new JLabel();
        Lb04.setSize(new Dimension(6, 20));
        Lb04.setIcon(createImageIcon("ppp/plus.png"));
        pnln12.add(Lb04);

        text7 = new JTextField("", 2);
        text7.setHorizontalAlignment(JTextField.RIGHT);
        pnln12.add(text7);

        JLabel Lb05;
        Lb05 = new JLabel();
        Lb05.setSize(new Dimension(9, 20));
        Lb05.setIcon(createImageIcon("ppp/root.png"));
        pnln12.add(Lb05);

        text8 = new JTextField("", 2);
        text8.setHorizontalAlignment(JTextField.RIGHT);
        pnln12.add(text8);

// -------------------------------------------------------------------------------線分入力モード。比率set

        //Button	Button_senbun_internalDivisionRatio_set
// -----1;Line segment ratio set
        JButton Button_senbun_internalDivisionRatio_set = new JButton("Set");
        Button_senbun_internalDivisionRatio_set.addActionListener(e -> {
            setInternalDivisionRatio();

            img_explanation_fname = "qqq/senbun_naibun_set.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.LINE_SEGMENT_RATIO_SET_28;
            iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.LINE_SEGMENT_RATIO_SET_28;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        Button_senbun_internalDivisionRatio_set.setBounds(197, 2, 25, 23);
        pnln10.add(Button_senbun_internalDivisionRatio_set);

        Button_senbun_internalDivisionRatio_set.setMargin(new Insets(0, 0, 0, 0));

// ------1;線分比率set。ここまで


// -------------------------------------------------------------------------------線分入力モード。比率分割

        //Button	Button_senbun_nyuryoku	= new Button(	"L_draw"	);Button_senbun_nyuryoku.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e) {
// -----28;線分入力モード。
        JButton Button_senbun_n_nyuryoku = new JButton("");
        Button_senbun_n_nyuryoku.addActionListener(e -> {
            setInternalDivisionRatio();

            img_explanation_fname = "qqq/senbun_n_nyuryoku.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.LINE_SEGMENT_RATIO_SET_28;
            iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.LINE_SEGMENT_RATIO_SET_28;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        Button_senbun_n_nyuryoku.setBounds(223, 2, 23, 23);
        pnln10.add(Button_senbun_n_nyuryoku);

        Button_senbun_n_nyuryoku.setMargin(new Insets(0, 0, 0, 0));
        Button_senbun_n_nyuryoku.setIcon(createImageIcon(
                "ppp/senbun_n_nyuryoku.png"));

// ------28;線分入力モード。ここまで


//-----------------------------------------------------------------------------------
        //------------------------------------------------
        JPanel pnln7 = new JPanel();
//         pnln7.setBackground(Color.PINK);
        pnln7.setLayout(new GridLayout(1, 1));

        //pnln.add(pnln7);
        //------------------------------------------------

// ****北**************************************************************************
        JButton Button_tenkaizu_idiu = new JButton("");
        Button_tenkaizu_idiu.addActionListener(e -> {
            //JButton	Button_tenkaizu_idiu	= new JButton(	"CP_move"	);Button_tenkaizu_idiu.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e) {
            img_explanation_fname = "qqq/tenkaizu_idiu.png";
            updateExplanation();

            i_mouse_modeA = MouseMode.MOVE_CREASE_PATTERN_2;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            canvas.repaint();
        });
        pnln.add(Button_tenkaizu_idiu);

        Button_tenkaizu_idiu.setIcon(createImageIcon(
                "ppp/tenkaizu_idiu.png"));

// *****北*************************************************************************


// ******北************************************************************************


        //------------------------------------------------
        JPanel pnln8 = new JPanel();
        pnln8.setBackground(Color.white);
        pnln8.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
        pnln8.setBorder(new LineBorder(Color.black, 1));
        pnln.add(pnln8);
        //------------------------------------------------


// *****北*************************************************************************sssssssssssssss
        JButton Button_tenkaizu_syukusyou = new JButton("");
        Button_tenkaizu_syukusyou.addActionListener(e -> {
            img_explanation_fname = "qqq/tenkaizu_syukusyou.png";
            updateExplanation();

            double d_bairitu = 1.0 / Math.sqrt(Math.sqrt(Math.sqrt(2.0)));//  sqrt(sqrt(2))=1.1892
            scaleFactor = scaleFactor / Math.sqrt(Math.sqrt(Math.sqrt(2.0)));//  sqrt(sqrt(2))=1.1892
            camera_of_orisen_input_diagram.multiplyCameraZoomX(d_bairitu);
            camera_of_orisen_input_diagram.multiplyCameraZoomY(d_bairitu);

//20180122追加
            FoldedFigure OZi;
            for (int i_oz = 1; i_oz <= OAZ.size() - 1; i_oz++) {
                OZi = OAZ.get(i_oz);

                Point t_o2tv = camera_of_orisen_input_diagram.object2TV(camera_of_orisen_input_diagram.getCameraPosition());

                OZi.d_foldedFigure_scale_factor = OZi.d_foldedFigure_scale_factor * d_bairitu;

                OZi.camera_of_foldedFigure.camera_ichi_sitei_from_TV(t_o2tv);
                OZi.camera_of_foldedFigure.multiplyCameraZoomX(d_bairitu);
                OZi.camera_of_foldedFigure.multiplyCameraZoomY(d_bairitu);

                OZi.camera_of_foldedFigure_front.camera_ichi_sitei_from_TV(t_o2tv);
                OZi.camera_of_foldedFigure_front.multiplyCameraZoomX(d_bairitu);
                OZi.camera_of_foldedFigure_front.multiplyCameraZoomY(d_bairitu);

                OZi.camera_of_foldedFigure_rear.camera_ichi_sitei_from_TV(t_o2tv);
                OZi.camera_of_foldedFigure_rear.multiplyCameraZoomX(d_bairitu);
                OZi.camera_of_foldedFigure_rear.multiplyCameraZoomY(d_bairitu);

                OZi.camera_of_transparent_front.camera_ichi_sitei_from_TV(t_o2tv);
                OZi.camera_of_transparent_front.multiplyCameraZoomX(d_bairitu);
                OZi.camera_of_transparent_front.multiplyCameraZoomY(d_bairitu);

                OZi.camera_of_transparent_rear.camera_ichi_sitei_from_TV(t_o2tv);
                OZi.camera_of_transparent_rear.multiplyCameraZoomX(d_bairitu);
                OZi.camera_of_transparent_rear.multiplyCameraZoomY(d_bairitu);
                text29.setText(String.valueOf(OZi.d_foldedFigure_scale_factor));
                text29.setCaretPosition(0);
            }
//20180122追加　ここまで


            text27.setText(String.valueOf(scaleFactor));
            text27.setCaretPosition(0);
            canvas.repaint();
        });
        pnln8.add(Button_tenkaizu_syukusyou);
        Button_tenkaizu_syukusyou.setIcon(createImageIcon("ppp/tenkaizu_syukusyou.png"));


// ****北**************************************************************************
        text27 = new JTextField("", 2);
        text27.setHorizontalAlignment(JTextField.RIGHT);

        pnln8.add(text27);

// ****北**************************************************************************
// -----縮尺係数set
        JButton Button_syukusyaku_keisuu_set = new JButton("S");
        Button_syukusyaku_keisuu_set.addActionListener(e -> {
            double d_syukusyaku_keisuu_old = scaleFactor;
            scaleFactor = String2double(text27.getText(), d_syukusyaku_keisuu_old);
            if (scaleFactor <= 0.0) {
                scaleFactor = d_syukusyaku_keisuu_old;
            }
            text27.setText(String.valueOf(scaleFactor));
            if (scaleFactor != d_syukusyaku_keisuu_old) {
                camera_of_orisen_input_diagram.setCameraZoomX(scaleFactor);
                camera_of_orisen_input_diagram.setCameraZoomY(scaleFactor);

//20180225追加

                double d_bairitu = scaleFactor / d_syukusyaku_keisuu_old;


                FoldedFigure OZi;
                for (int i_oz = 1; i_oz <= OAZ.size() - 1; i_oz++) {
                    OZi = OAZ.get(i_oz);

                    Point t_o2tv = camera_of_orisen_input_diagram.object2TV(camera_of_orisen_input_diagram.getCameraPosition());

                    OZi.d_foldedFigure_scale_factor = OZi.d_foldedFigure_scale_factor * d_bairitu;


                    OZi.camera_of_foldedFigure.camera_ichi_sitei_from_TV(t_o2tv);
                    OZi.camera_of_foldedFigure.multiplyCameraZoomX(d_bairitu);
                    OZi.camera_of_foldedFigure.multiplyCameraZoomY(d_bairitu);

                    OZi.camera_of_foldedFigure_front.camera_ichi_sitei_from_TV(t_o2tv);
                    OZi.camera_of_foldedFigure_front.multiplyCameraZoomX(d_bairitu);
                    OZi.camera_of_foldedFigure_front.multiplyCameraZoomY(d_bairitu);

                    OZi.camera_of_foldedFigure_rear.camera_ichi_sitei_from_TV(t_o2tv);
                    OZi.camera_of_foldedFigure_rear.multiplyCameraZoomX(d_bairitu);
                    OZi.camera_of_foldedFigure_rear.multiplyCameraZoomY(d_bairitu);

                    OZi.camera_of_transparent_front.camera_ichi_sitei_from_TV(t_o2tv);
                    OZi.camera_of_transparent_front.multiplyCameraZoomX(d_bairitu);
                    OZi.camera_of_transparent_front.multiplyCameraZoomY(d_bairitu);

                    OZi.camera_of_transparent_rear.camera_ichi_sitei_from_TV(t_o2tv);
                    OZi.camera_of_transparent_rear.multiplyCameraZoomX(d_bairitu);
                    OZi.camera_of_transparent_rear.multiplyCameraZoomY(d_bairitu);

                    text29.setText(String.valueOf(OZ.d_foldedFigure_scale_factor));
                    text29.setCaretPosition(0);
                }
//20180225追加　ここまで


            }
            text27.setText(String.valueOf(scaleFactor));
            text27.setCaretPosition(0);
            canvas.repaint();

            img_explanation_fname = "qqq/syukusyaku_keisuu_set.png";
            updateExplanation();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnln8.add(Button_syukusyaku_keisuu_set);

// ------縮尺係数set。ここまで


// ****北**************************************************************************
        JButton Button_tenkaizu_kakudai = new JButton("");
        Button_tenkaizu_kakudai.addActionListener(e -> {
            img_explanation_fname = "qqq/tenkaizu_kakudai.png";
            updateExplanation();

            double d_bairitu = Math.sqrt(Math.sqrt(Math.sqrt(2.0)));//  sqrt(sqrt(2))=1.1892
            scaleFactor = scaleFactor * Math.sqrt(Math.sqrt(Math.sqrt(2.0)));//  sqrt(sqrt(2))=1.1892
            camera_of_orisen_input_diagram.multiplyCameraZoomX(d_bairitu);
            camera_of_orisen_input_diagram.multiplyCameraZoomY(d_bairitu);


//20180122追加
            FoldedFigure OZi;
            for (int i_oz = 1; i_oz <= OAZ.size() - 1; i_oz++) {
                OZi = OAZ.get(i_oz);

                Point t_o2tv = camera_of_orisen_input_diagram.object2TV(camera_of_orisen_input_diagram.getCameraPosition());

                OZi.d_foldedFigure_scale_factor = OZi.d_foldedFigure_scale_factor * d_bairitu;


                OZi.camera_of_foldedFigure.camera_ichi_sitei_from_TV(t_o2tv);
                OZi.camera_of_foldedFigure.multiplyCameraZoomX(d_bairitu);
                OZi.camera_of_foldedFigure.multiplyCameraZoomY(d_bairitu);

                OZi.camera_of_foldedFigure_front.camera_ichi_sitei_from_TV(t_o2tv);
                OZi.camera_of_foldedFigure_front.multiplyCameraZoomX(d_bairitu);
                OZi.camera_of_foldedFigure_front.multiplyCameraZoomY(d_bairitu);

                OZi.camera_of_foldedFigure_rear.camera_ichi_sitei_from_TV(t_o2tv);
                OZi.camera_of_foldedFigure_rear.multiplyCameraZoomX(d_bairitu);
                OZi.camera_of_foldedFigure_rear.multiplyCameraZoomY(d_bairitu);

                OZi.camera_of_transparent_front.camera_ichi_sitei_from_TV(t_o2tv);
                OZi.camera_of_transparent_front.multiplyCameraZoomX(d_bairitu);
                OZi.camera_of_transparent_front.multiplyCameraZoomY(d_bairitu);

                OZi.camera_of_transparent_rear.camera_ichi_sitei_from_TV(t_o2tv);
                OZi.camera_of_transparent_rear.multiplyCameraZoomX(d_bairitu);
                OZi.camera_of_transparent_rear.multiplyCameraZoomY(d_bairitu);

                text29.setText(String.valueOf(OZ.d_foldedFigure_scale_factor));
                text29.setCaretPosition(0);
            }
//20180122追加　ここまで


            text27.setText(String.valueOf(scaleFactor));
            text27.setCaretPosition(0);
            canvas.repaint();
        });
        pnln8.add(Button_tenkaizu_kakudai);
        Button_tenkaizu_kakudai.setIcon(createImageIcon("ppp/tenkaizu_kakudai.png"));


// ******北************************************************************************

        //------------------------------------------------
        JPanel pnln14 = new JPanel();
        pnln14.setBackground(Color.white);
        pnln14.setLayout(new FlowLayout(FlowLayout.LEFT, 1,1));
        pnln14.setBorder(new LineBorder(Color.black, 1));
        pnln.add(pnln14);
        //------------------------------------------------


// *****北*************展開の回転************************************************************
        JButton Button_tenkaizu_p_kaiten = new JButton("");
        Button_tenkaizu_p_kaiten.addActionListener(e -> {
            img_explanation_fname = "qqq/tenkaizu_p_kaiten.png";
            updateExplanation();

            rotationCorrection = OritaCalc.angle_between_m180_180(rotationCorrection + 11.25);
            camera_of_orisen_input_diagram.setCameraAngle(rotationCorrection);
            text28.setText(String.valueOf(rotationCorrection));
            text28.setCaretPosition(0);

            //Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnln14.add(Button_tenkaizu_p_kaiten);
        Button_tenkaizu_p_kaiten.setIcon(createImageIcon("ppp/tenkaizu_p_kaiten.png"));

// ****北**************************************************************************
//回転角度補正
        text28 = new JTextField("", 2);
        text28.setHorizontalAlignment(JTextField.RIGHT);
        pnln14.add(text28);

// ****北**************************************************************************
// -----回転角度補正set
        JButton Button_kaiten_hosei_set = new JButton("S");
        Button_kaiten_hosei_set.addActionListener(e -> {
            double d_kaiten_hosei_old = rotationCorrection;
            rotationCorrection = OritaCalc.angle_between_m180_180(String2double(text28.getText(), d_kaiten_hosei_old));

            text28.setText(String.valueOf(rotationCorrection));

            if (rotationCorrection != d_kaiten_hosei_old) {
                camera_of_orisen_input_diagram.setCameraAngle(rotationCorrection);
            }

            text28.setText(String.valueOf(rotationCorrection));
            text28.setCaretPosition(0);
            canvas.repaint();


            img_explanation_fname = "qqq/kaiten_hosei_set.png";
            updateExplanation();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnln14.add(Button_kaiten_hosei_set);

// ------回転角度補正set。ここまで


// *****北*************************************************************************
        JButton Button_tenkaizu_m_kaiten = new JButton("");
        Button_tenkaizu_m_kaiten.addActionListener(e -> {
            img_explanation_fname = "qqq/tenkaizu_m_kaiten.png";
            updateExplanation();
            rotationCorrection = OritaCalc.angle_between_m180_180(rotationCorrection - 11.25);
            camera_of_orisen_input_diagram.setCameraAngle(rotationCorrection);
            text28.setText(String.valueOf(rotationCorrection));
            text28.setCaretPosition(0);
            canvas.repaint();
        });
        pnln14.add(Button_tenkaizu_m_kaiten);


        Button_tenkaizu_m_kaiten.setIcon(createImageIcon("ppp/tenkaizu_m_kaiten.png"));

//背景のPC画面を背景画として読み込む
        JButton Button_toumei = new JButton("T");
        Button_toumei.addActionListener(e -> {

            img_explanation_fname = "qqq/toumei.png";
            updateExplanation();
            Robot robot;

            try {
                robot = new Robot();
            } catch (AWTException ex) {
                ex.printStackTrace();
                return;
            }

            // 範囲を指定してキャプチャ

            Rectangle bounds = getBounds();
            Rectangle canvasBounds = canvas.getBounds();
            Insets insets = getInsets();
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println("bounds.x=" + bounds.x + "   :bounds.y=" + bounds.y + "    :bounds.width=" + bounds.width + "   :bounds.height=" + bounds.height);
            System.out.println("insets.top=" + insets.top + "   :insets.left=" + insets.left + "    :insets.right=" + insets.right + "   :insets.bottom=" + insets.bottom);


            //左上端から、左上で描画用画面の見える限界位置へのベクトル
            //int upperLeft_ix=115;
            //int upperLeft_iy=60;

            //右下端から、右下で描画用画面の見える限界位置へのベクトル
            //int lowerRight_ix=115;
            //int lowerRight_iy=40;

            //int i_dx=115;int i_dy=0;

            bounds = new Rectangle(bounds.x + canvasBounds.x,
                    bounds.y + canvasBounds.y,
                    canvasBounds.width - upperLeft_ix - lowerRight_ix,
                    canvasBounds.height - upperLeft_iy - lowerRight_iy);

            setVisible(false);
            try {
                Thread.sleep(200);
            } catch (InterruptedException ie) {
            }//A line to make you wait only 100. Without this line, there is a risk of capturing Orihime itself when executed in a jar file. The ie of InterruptedException ie was initially e. 20181125
            imageT = robot.createScreenCapture(bounds);
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
            }//A line to make you wait only 100. Without this line, there is a risk of capturing Orihime itself when executed in a jar file. The ie of InterruptedException ie was initially e. 20181125
            setVisible(true);

            img_background = imageT;
            OritaCalc.display("新背景カメラインスタンス化");
            h_cam = new Background_camera();//20181202

            double dvx = upperLeft_ix;
            double dvy = upperLeft_iy;

            background_set(new Point(120.0, 120.0),
                    new Point(120.0 + 10.0, 120.0),
                    new Point(dvx, dvy),
                    new Point(dvx + 10.0, dvy));


//背景表示の各条件を設定
            displayBackground = true;
            Button_background_kirikae.setBackground(Color.ORANGE);

            if (lockBackground) {//20181202  このifが無いとlock on のときに背景がうまく表示できない
                h_cam.set_i_Lock_on(lockBackground);
                h_cam.setCamera(camera_of_orisen_input_diagram);
                h_cam.h3_obj_and_h4_obj_calculation();
            }

            canvas.repaint();
        });
        Button_toumei.setMargin(new Insets(0, 0, 0, 0));
        //Button_writeImage.setBackground(Color.ORANGE);
        pnln.add(Button_toumei);

// *******北***********************************************************************

//drawImage
//  img - 描画される指定イメージ。img が null の場合には何も行わない
//  x - x 座標
//  y - y 座標
//  width - 矩形の幅
//  height - 矩形の高さ
//  observer - イメージがさらに変換されることが通知されるオブジェクト

// *****北*************************************************************************
        JButton Button_background_trim = new JButton("Tr");
        Button_background_trim.addActionListener(e -> {
            img_explanation_fname = "qqq/haikei_trim.png";
            updateExplanation();


            offsc_background = new BufferedImage(2000, 1100, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2_background = offsc_background.createGraphics();
            //背景表示
            if ((img_background != null) && displayBackground) {
                int iw = img_background.getWidth(null);//イメージの幅を取得
                int ih = img_background.getHeight(null);//イメージの高さを取得

                h_cam.setBackgroundWidth(iw);
                h_cam.setBackgroundHeight(ih);

                drawBackground(g2_background, img_background);
            }


//枠設定時の背景を枠内のみ残してトリム 20181204
            if ((i_mouse_modeA == MouseMode.OPERATION_FRAME_CREATE_61) && (es1.getDrawingStage() == 4)) {//枠線が表示されている状態
                int xmin = (int) es1.operationFrameBox.getXMin();
                int xmax = (int) es1.operationFrameBox.getXMax();
                int ymin = (int) es1.operationFrameBox.getYMin();
                int ymax = (int) es1.operationFrameBox.getYMax();

                img_background = offsc_background.getSubimage(xmin, ymin, xmax - xmin, ymax - ymin);

                h_cam = new Background_camera();

                background_set(new Point(120.0, 120.0),
                        new Point(120.0 + 10.0, 120.0),
                        new Point(xmin, ymin),
                        new Point((double) xmin + 10.0, ymin));

                if (lockBackground) {//20181202  このifが無いとlock on のときに背景がうまく表示できない
                    h_cam.set_i_Lock_on(lockBackground);
                    h_cam.setCamera(camera_of_orisen_input_diagram);
                    h_cam.h3_obj_and_h4_obj_calculation();
                }


            }
        });
        Button_background_trim.setMargin(new Insets(0, 0, 0, 0));
        pnln.add(Button_background_trim);

////b* アプリケーション用。先頭が／＊／／／で始まる行にはさまれた部分は無視される。

        //------------------------------------------------
        JPanel pnln9 = new JPanel();
        pnln9.setLayout(new GridLayout(1, 5));

        pnln.add(pnln9);

        JButton Button_background = new JButton("BG");
        Button_background.addActionListener(e -> {
            img_explanation_fname = "qqq/haikei.png";
            updateExplanation();

            i_mouseDragged_valid = false;
            i_mouseReleased_valid = false;

            readImageFromFile();

            displayBackground = true;
            Button_background_kirikae.setBackground(Color.ORANGE);


            h_cam = new Background_camera();//20181202
            if (lockBackground) {//20181202  このifが無いとlock on のときに背景がうまく表示できない
                h_cam.set_i_Lock_on(lockBackground);
                h_cam.setCamera(camera_of_orisen_input_diagram);
                h_cam.h3_obj_and_h4_obj_calculation();
            }

            canvas.repaint();
        });
        Button_background.setMargin(new Insets(0, 0, 0, 0));
        Button_background.setBackground(Color.ORANGE);
        pnln9.add(Button_background);

        Button_background_kirikae = new JButton("off");
        Button_background_kirikae.addActionListener(e -> {
            img_explanation_fname = "qqq/haikei_kirikae.png";
            updateExplanation();

            displayBackground = !displayBackground;

            if (!displayBackground) {
                Button_background_kirikae.setBackground(Color.gray);
            } else {
                Button_background_kirikae.setBackground(Color.ORANGE);
            }

            canvas.repaint();
        });
        Button_background_kirikae.setMargin(new Insets(0, 0, 0, 0));
        Button_background_kirikae.setBackground(Color.ORANGE);
        pnln9.add(Button_background_kirikae);


// ******************************************************************************
        JButton Button_set_BG = new JButton("S");
        Button_set_BG.addActionListener(e -> {
            img_explanation_fname = "qqq/set_BG.png";
            updateExplanation();

            i_mouse_modeA = MouseMode.BACKGROUND_CHANGE_POSITION_26;
            Button_kyoutuu_sagyou();
            canvas.repaint();
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

        });
        Button_set_BG.setMargin(new Insets(0, 0, 0, 0));
        Button_set_BG.setBackground(Color.ORANGE);
        pnln9.add(Button_set_BG);

// ******北************************************************************************
        Button_background_Lock_on = new JButton("L");
        Button_background_Lock_on.addActionListener(e -> {
            img_explanation_fname = "qqq/haikei_Lock_on.png";
            updateExplanation();

            lockBackground_ori = !lockBackground_ori;
            lockBackground = lockBackground_ori;

            if (lockBackground) {
                Button_background_Lock_on.setBackground(Color.ORANGE);

                h_cam.set_i_Lock_on(lockBackground);
                h_cam.setCamera(camera_of_orisen_input_diagram);
                h_cam.h3_obj_and_h4_obj_calculation();
            } else {
                Button_background_Lock_on.setBackground(Color.gray);

                h_cam.set_i_Lock_on(lockBackground);
            }

            canvas.repaint();
        });
        Button_background_Lock_on.setMargin(new Insets(0, 0, 0, 0));

        pnln9.add(Button_background_Lock_on);


// ******北************************************************************************　線分除けて線種変換

        JButton Button_senbun_yoke_henkan = new JButton("");
        Button_senbun_yoke_henkan.addActionListener(e -> {

            img_explanation_fname = "qqq/senbun_yoke_henkan.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.CREASE_ADVANCE_TYPE_30;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnln9.add(Button_senbun_yoke_henkan);

        Button_senbun_yoke_henkan.setMargin(new Insets(0, 0, 0, 0));
        Button_senbun_yoke_henkan.setIcon(createImageIcon("ppp/senbun_yoke_henkan.png"));


// ******************************************************************************
////b* アプリケーション用。先頭が／＊／／／で始まる行にはさまれた部分は無視される。
// ******************************************************************************

        JMenu menu_help = new JMenu("Help");
        menu_help.setMnemonic('H');
        menuBar.add(menu_help);

// *******北*********************************************************************** 解説
        JMenuItem Button_kaisetu = new JMenuItem("Toggle help");
        Button_kaisetu.addActionListener(e -> {
            displayExplanation = !displayExplanation;

            i_mouseDragged_valid = false;
            i_mouseReleased_valid = false;

            canvas.repaint();
        });
        Button_kaisetu.setMargin(new Insets(0, 0, 0, 0));
        menu_help.add(Button_kaisetu);

        Button_kaisetu.setMargin(new Insets(0, 0, 0, 0));

// ******************************************************************************


        // *************************************************
        //左辺（西側）パネルの構築*************************
        // *************************************************
        //左辺（西側）パネルの作成


        JPanel pnlw = new JPanel();
        pnlw.setLayout(new GridLayout(32, 1));


        //パネルpnlwをレイアウト左辺（西側）に貼り付け
        contentPane.add("West", pnlw); //Frame用

        //------------------------------------------------
        JPanel pnlw26 = new JPanel();
        pnlw26.setLayout(new GridLayout(1, 3));

        pnlw.add(pnlw26);
        //------------------------------------------------

// *****西******************* Un Do ******************************************************

        JButton Button_undo = new JButton("");
        Button_undo.addActionListener(e -> {
            img_explanation_fname = "qqq/undo.png";
            updateExplanation();
            //es1.setMemo(Ubox.getMemo());
            setTitle(es1.undo());
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw26.add(Button_undo);
        Button_undo.setMargin(new Insets(0, 0, 0, 0));
        Button_undo.setIcon(createImageIcon(
                "ppp/undo.png"));

// *****西*************************************************************************

        text10 = new JTextField("", 1);
        text10.setHorizontalAlignment(JTextField.RIGHT);

        pnlw26.add(text10);
// *****西*************************************************************************
        JButton Button_undo_syutoku = new JButton("S");
        Button_undo_syutoku.addActionListener(e -> {
            img_explanation_fname = "qqq/undo_syutoku.png";
            updateExplanation();
            int i_undo_suu_old = i_undo_suu;
            i_undo_suu = StringOp.String2int(text10.getText(), i_undo_suu_old);
            if (i_undo_suu < 0) {
                i_undo_suu = 0;
            }
            text10.setText(String.valueOf(i_undo_suu));
            es1.set_Ubox_undo_suu(i_undo_suu);
        });
        pnlw26.add(Button_undo_syutoku);

        Button_undo_syutoku.setMargin(new Insets(0, 0, 0, 0));


// *****西*********************** Re Do **************************************************

        JButton Button_redo = new JButton("");
        Button_redo.addActionListener(e -> {
            img_explanation_fname = "qqq/redo.png";
            updateExplanation();

            setTitle(es1.redo());
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw26.add(Button_redo);
        Button_redo.setMargin(new Insets(0, 0, 0, 0));
        Button_redo.setIcon(createImageIcon(
                "ppp/redo.png"));


        JPanel pnlw22 = new JPanel();
        pnlw22.setLayout(new GridLayout(1, 3));

        pnlw.add(pnlw22);

        JPanel pnlw23 = new JPanel();
        pnlw23.setLayout(new GridLayout(1, 2));

        pnlw22.add(pnlw23);
        //------------------------------------------------

// ****西*********************　線幅　下げ　*****************************************************
        JButton Button_lineWidth_decrease = new JButton("");
        Button_lineWidth_decrease.addActionListener(e -> {
            iLineWidth = iLineWidth - 2;
            if (iLineWidth < 1) {
                iLineWidth = 1;
            }
            img_explanation_fname = "qqq/senhaba_sage.png";
            updateExplanation();
            canvas.repaint();
        });
        pnlw23.add(Button_lineWidth_decrease);

        Button_lineWidth_decrease.setMargin(new Insets(0, 0, 0, 0));
        Button_lineWidth_decrease.setIcon(createImageIcon(
                "ppp/senhaba_sage.png"));

// ****西********************　線幅　上げ　******************************************************

        JButton Button_lineWidth_increase = new JButton("");
        Button_lineWidth_increase.addActionListener(e -> {
            iLineWidth = iLineWidth + 2;
            img_explanation_fname = "qqq/senhaba_age.png";
            updateExplanation();
            canvas.repaint();
        });
        pnlw23.add(Button_lineWidth_increase);

        Button_lineWidth_increase.setMargin(new Insets(0, 0, 0, 0));
        Button_lineWidth_increase.setIcon(createImageIcon(
                "ppp/senhaba_age.png"));


        JPanel pnlw24 = new JPanel();
        pnlw24.setLayout(new GridLayout(1, 2));

        pnlw22.add(pnlw24);


// ****西********************************　点幅　下げ　******************************************点幅

        JButton Button_point_width_reduce = new JButton("");
        Button_point_width_reduce.addActionListener(e -> {
            img_explanation_fname = "qqq/tenhaba_sage.png";
            updateExplanation();

            pointSize = pointSize - 1;
            if (pointSize < 0) {
                pointSize = 0;
            }
            es1.setPointSize(pointSize);

            canvas.repaint();
        });
        pnlw24.add(Button_point_width_reduce);

        Button_point_width_reduce.setMargin(new Insets(0, 0, 0, 0));
        Button_point_width_reduce.setIcon(createImageIcon(
                "ppp/tenhaba_sage.png"));

// ****西*******************************　点幅　上げ　*******************************************
        JButton Button_point_width_increase = new JButton("");
        Button_point_width_increase.addActionListener(e -> {
            img_explanation_fname = "qqq/tenhaba_age.png";
            updateExplanation();

            pointSize = pointSize + 1;
            //if(pointSize<0){pointSize=0;}
            es1.setPointSize(pointSize);

            //Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw24.add(Button_point_width_increase);

        Button_point_width_increase.setMargin(new Insets(0, 0, 0, 0));
        Button_point_width_increase.setIcon(createImageIcon(
                "ppp/tenhaba_age.png"));

// ******西*************展開図の線をアンチエイリアス表示にする***********************************************************
        JButton Button_anti_alias = new JButton("a_a");
        Button_anti_alias.addActionListener(e -> {
            antiAlias = !antiAlias;

            img_explanation_fname = "qqq/anti_alias.png";
            updateExplanation();

            canvas.repaint();
        });
        pnlw22.add(Button_anti_alias);

        Button_anti_alias.setMargin(new Insets(0, 0, 0, 0));
        //Button_anti_alias.setIcon(createImageIcon(
        //  "ppp/anti_alias.png")));


        //------------------------------------------------
        JPanel pnlw27 = new JPanel();
//         pnlw27.setBackground(Color.PINK);
        pnlw27.setLayout(new GridLayout(1, 4));

        pnlw.add(pnlw27);
        //------------------------------------------------

        //------------------------------------------------
        JPanel pnlw30 = new JPanel();
//         pnlw30.setBackground(Color.PINK);
        pnlw30.setLayout(new GridLayout(1, 4));

        pnlw27.add(pnlw30);
        //------------------------------------------------
// ********************************************************折線の表現方法

        JButton Button_orisen_hyougen = new JButton("");
        Button_orisen_hyougen.addActionListener(e -> {

            Button_kyoutuu_sagyou();
            lineStyle = lineStyle.advance();

            img_explanation_fname = "qqq/orisen_hyougen.png";
            updateExplanation();

            canvas.repaint();
        });
        pnlw27.add(Button_orisen_hyougen);

        Button_orisen_hyougen.setMargin(new Insets(0, 0, 0, 0));
        Button_orisen_hyougen.setIcon(createImageIcon(
                "ppp/orisen_hyougen.png"));


        //------------------------------------------------
        JPanel pnlw31 = new JPanel();
//         pnlw31.setBackground(Color.PINK);
        pnlw31.setLayout(new GridLayout(1, 4));

        pnlw27.add(pnlw31);
        //------------------------------------------------
// ******西************************************************************************
        //------------------------------------------------
        JPanel pnlw25 = new JPanel();
//         pnlw25.setBackground(Color.PINK);
        pnlw25.setLayout(new GridLayout(1, 4));

        pnlw.add(pnlw25);
        //------------------------------------------------


        //-------------------------------------------------------------
        ButtonCol_red = new JButton("M");
        ButtonCol_red.addActionListener(e -> {
            img_explanation_fname = "qqq/ButtonCol_red.png";
            updateExplanation();
            ButtonCol_reset();
            ButtonCol_red.setForeground(Color.black);
            ButtonCol_red.setBackground(Color.red);
            icol = LineColor.RED_1;
            es1.setColor(icol);

            canvas.repaint();
        });
        pnlw25.add(ButtonCol_red);
        ButtonCol_red.setBackground(new Color(150, 150, 150));
        ButtonCol_red.setMargin(new Insets(0, 0, 0, 0));

// ******西************************************************************************

        //-------------------------------------------------------------
        ButtonCol_blue = new JButton("V");
        ButtonCol_blue.addActionListener(e -> {


            img_explanation_fname = "qqq/ButtonCol_blue.png";
            updateExplanation();
            ButtonCol_reset();
            ButtonCol_blue.setForeground(Color.black);
            ButtonCol_blue.setBackground(Color.blue);
            icol = LineColor.BLUE_2;
            es1.setColor(icol);

            canvas.repaint();
        });
        pnlw25.add(ButtonCol_blue);
        ButtonCol_blue.setBackground(new Color(150, 150, 150));
        ButtonCol_blue.setMargin(new Insets(0, 0, 0, 0));
// ******西************************************************************************
        //-------------------------------------------------------------

        ButtonCol_black = new JButton("E");
        ButtonCol_black.addActionListener(e -> {
            img_explanation_fname = "qqq/ButtonCol_black.png";
            updateExplanation();

            ButtonCol_reset();
            ButtonCol_black.setForeground(Color.white);
            ButtonCol_black.setBackground(Color.black);
            icol = LineColor.BLACK_0;
            es1.setColor(icol);
            //  Button_kyoutuu_sagyou();

//iro_sitei_ato_ni_jissisuru_sagyou();

            canvas.repaint();
        });
        pnlw25.add(ButtonCol_black);
        ButtonCol_black.setBackground(new Color(150, 150, 150));
        ButtonCol_black.setMargin(new Insets(0, 0, 0, 0));
// ******西************************************************************************
        //-------------------------------------------------------------

        ButtonCol_cyan = new JButton("A");
        ButtonCol_cyan.addActionListener(e -> {
            img_explanation_fname = "qqq/ButtonCol_cyan.png";
            updateExplanation();

            ButtonCol_reset();
            ButtonCol_cyan.setForeground(Color.black);
            ButtonCol_cyan.setBackground(Color.cyan);
            icol = LineColor.CYAN_3;
            es1.setColor(icol);
            //  Button_kyoutuu_sagyou();

//iro_sitei_ato_ni_jissisuru_sagyou();

            canvas.repaint();
        });
        pnlw25.add(ButtonCol_cyan);
        ButtonCol_cyan.setBackground(new Color(150, 150, 150));
        ButtonCol_cyan.setMargin(new Insets(0, 0, 0, 0));


//icol=3 cyan
//icol=4 orange
//icol=5 mazenta
//icol=6 green
//icol=7 yellow

// ******西************************************************************************
        //------------------------------------------------
        JPanel pnlw1 = new JPanel();
//         pnlw1.setBackground(Color.PINK);
        pnlw1.setLayout(new GridLayout(1, 3));
        pnlw.add(pnlw1);//パネルpnlw1をpnlwに貼り付け


        //Button	Button_senbun_nyuryoku	= new Button(	"L_draw"	);Button_senbun_nyuryoku.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e) {
// -----1;線分入力モード。
        JButton Button_senbun_nyuryoku = new JButton("");
        Button_senbun_nyuryoku.addActionListener(e -> {
            img_explanation_fname = "qqq/senbun_nyuryoku.png";
            updateExplanation();
            foldLineAdditionalInputMode = Drawing_Worker.FoldLineAdditionalInputMode.POLY_LINE_0;//=0は折線入力　=1は補助線入力モード
            es1.setFoldLineAdditional(foldLineAdditionalInputMode);//このボタンと機能は補助絵線共通に使っているのでi_orisen_hojyosenの指定がいる
            i_mouse_modeA = MouseMode.DRAW_CREASE_FREE_1;
            iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.DRAW_CREASE_FREE_1;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw1.add(Button_senbun_nyuryoku);


        Button_senbun_nyuryoku.setMargin(new Insets(0, 0, 0, 0));
        Button_senbun_nyuryoku.setIcon(createImageIcon(
                "ppp/senbun_nyuryoku.png"));

// ------1;線分入力モード。ここまで


// -------------11;線分入力モード。
        JButton Button_senbun_nyuryoku11 = new JButton("");
        Button_senbun_nyuryoku11.addActionListener(e -> {
            //Button	Button_senbun_nyuryoku11	= new Button(	"L_draw11"	);Button_senbun_nyuryoku11.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e) {
            img_explanation_fname = "qqq/senbun_nyuryoku11.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.DRAW_CREASE_RESTRICTED_11;
            iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.DRAW_CREASE_RESTRICTED_11;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });

        pnlw1.add(Button_senbun_nyuryoku11);

        Button_senbun_nyuryoku11.setMargin(new Insets(0, 0, 0, 0));
        Button_senbun_nyuryoku11.setIcon(createImageIcon(
                "ppp/senbun_nyuryoku11.png"));


// -------------11;線分入力モード。ここまで

// -----62 62 62 ボロノイ図。Voronoi 20181020

        JButton Button_Voronoi = new JButton("");
        Button_Voronoi.addActionListener(e -> {
            img_explanation_fname = "qqq/Voronoi.png";
            updateExplanation();
            foldLineAdditionalInputMode = Drawing_Worker.FoldLineAdditionalInputMode.POLY_LINE_0;//=0は折線入力　=1は補助線入力モード
            es1.setFoldLineAdditional(foldLineAdditionalInputMode);//このボタンと機能は補助絵線共通に使っているのでi_orisen_hojyosenの指定がいる
            i_mouse_modeA = MouseMode.VONOROI_CREATE_62;
            iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.VONOROI_CREATE_62;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw1.add(Button_Voronoi);


        Button_Voronoi.setMargin(new Insets(0, 0, 0, 0));
        Button_Voronoi.setIcon(createImageIcon("ppp/Voronoi.png"));

// ------1;線分入力モード。ここまで
// *******西***********************************************************************
// -------------38;折り畳み可能線入力
        JButton Button_folding_kanousen = new JButton("");
        Button_folding_kanousen.addActionListener(e -> {
            img_explanation_fname = "qqq/oritatami_kanousen.png";
            updateExplanation();

            i_mouse_modeA = MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38;
            iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw1.add(Button_folding_kanousen);

        Button_folding_kanousen.setMargin(new Insets(0, 0, 0, 0));
        Button_folding_kanousen.setIcon(createImageIcon(
                "ppp/oritatami_kanousen.png"));


// -------------38;折り畳み可能線入力。ここまで

// *******西***********************************************************************

        //------------------------------------------------
        JPanel pnlw2 = new JPanel();
//         pnlw2.setBackground(Color.PINK);
        pnlw2.setLayout(new GridLayout(1, 4));
        pnlw.add(pnlw2);//パネルpnlw2をpnlwに貼り付け

// ******************************************************************************
// 5 5 5 5 5 -------------5;線分延長モード。
        JButton Button_senbun_entyou = new JButton("");//Button_senbun_entyou	= new JButton(	"L_en"	);
        Button_senbun_entyou.addActionListener(e -> {
            img_explanation_fname = "qqq/senbun_entyou.png";
            updateExplanation();

            i_mouse_modeA = MouseMode.LENGTHEN_CREASE_5;
            iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.LENGTHEN_CREASE_5;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();


        });
        pnlw2.add(Button_senbun_entyou);

        Button_senbun_entyou.setMargin(new Insets(0, 0, 0, 0));
        Button_senbun_entyou.setIcon(createImageIcon(
                "ppp/senbun_entyou.png"));

// -------------5;線分延長モード。ここまで
// ******************************************************************************

// ******************************************************************************
// 70 70 70 -------------70;線分延長モード。
        JButton Button_senbun_entyou_2 = new JButton("");//Button_senbun_entyou	= new JButton(	"L_en"	);
        Button_senbun_entyou_2.addActionListener(e -> {
            img_explanation_fname = "qqq/senbun_entyou_2.png";
            updateExplanation();

            i_mouse_modeA = MouseMode.CREASE_LENGTHEN_70;
            iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.LENGTHEN_CREASE_5;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();


        });
        pnlw2.add(Button_senbun_entyou_2);

        Button_senbun_entyou_2.setMargin(new Insets(0, 0, 0, 0));
        Button_senbun_entyou_2.setIcon(createImageIcon("ppp/senbun_entyou_2.png"));

// -------------70;線分延長モード。ここまで
// ******************************************************************************


// -------------7;Square bisector mode。
        JButton Button_kaku_toubun = new JButton("");//Button_kaku_toubun	= new JButton(	"kaku_toubun"	);
        Button_kaku_toubun.addActionListener(e -> {
            img_explanation_fname = "qqq/kaku_toubun.png";
            updateExplanation();

            i_mouse_modeA = MouseMode.SQUARE_BISECTOR_7;
            iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.SQUARE_BISECTOR_7;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw2.add(Button_kaku_toubun);

        Button_kaku_toubun.setMargin(new Insets(0, 0, 0, 0));
        Button_kaku_toubun.setIcon(createImageIcon(
                "ppp/kaku_toubun.png"));

// -------------7;角二等分線モード。ここまで

// ******************************************************************************
// -------------8;Inward mode 。
        JButton Button_naishin = new JButton("");//Button_naishin	= new JButton(	"naishin"	);
        Button_naishin.addActionListener(e -> {
            img_explanation_fname = "qqq/naishin.png";
            updateExplanation();

            i_mouse_modeA = MouseMode.INWARD_8;
            iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.INWARD_8;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw2.add(Button_naishin);

        Button_naishin.setMargin(new Insets(0, 0, 0, 0));
        Button_naishin.setIcon(createImageIcon(
                "ppp/naishin.png"));

// -------------8;内心モード。ここまで


// *******西***********************************************************************


        //------------------------------------------------
        JPanel pnlw3 = new JPanel();
//         pnlw3.setBackground(Color.PINK);
        pnlw3.setLayout(new GridLayout(1, 3));
        pnlw.add(pnlw3);


// *******西***********************************************************************
// -------------9;Perpendicular line down mode.
        JButton Button_suisen = new JButton("");//Button_suisen	= new JButton(	"suisen"	);
        Button_suisen.addActionListener(e -> {
            img_explanation_fname = "qqq/suisen.png";
            updateExplanation();

            i_mouse_modeA = MouseMode.PERPENDICULAR_DRAW_9;
            iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.PERPENDICULAR_DRAW_9;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw3.add(Button_suisen);

        Button_suisen.setMargin(new Insets(0, 0, 0, 0));
        Button_suisen.setIcon(createImageIcon(
                "ppp/suisen.png"));


// -------------9;垂線おろしモード。ここまで


// *******西***********************************************************************
// -------------10;Wrap mode.
        JButton Button_orikaesi = new JButton("");//Button_orikaesi	= new JButton(	"orikaesi"	);
        Button_orikaesi.addActionListener(e -> {
            img_explanation_fname = "qqq/orikaesi.png";
            updateExplanation();

            i_mouse_modeA = MouseMode.SYMMETRIC_DRAW_10;
            iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.SYMMETRIC_DRAW_10;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw3.add(Button_orikaesi);

        Button_orikaesi.setMargin(new Insets(0, 0, 0, 0));
        Button_orikaesi.setIcon(createImageIcon(
                "ppp/orikaesi.png"));


// -------------10;折り返しモード。ここまで


// *******西***********************************************************************
// -------------52;Continuous wrap mode.
        JButton Button_renzoku_orikaesi = new JButton("");
        Button_renzoku_orikaesi.addActionListener(e -> {
            img_explanation_fname = "qqq/renzoku_orikaesi.png";
            updateExplanation();

            i_mouse_modeA = MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52;
            iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw3.add(Button_renzoku_orikaesi);

        Button_renzoku_orikaesi.setMargin(new Insets(0, 0, 0, 0));
        Button_renzoku_orikaesi.setIcon(createImageIcon(
                "ppp/renzoku_orikaesi.png"));


// -------------52;連続折り返しモード。ここまで


// *******西***********************************************************************
        //------------------------------------------------
        JPanel pnlw4 = new JPanel();
//         pnlw4.setBackground(Color.PINK);
        pnlw4.setLayout(new GridLayout(1, 3));
        pnlw.add(pnlw4);


// *******西***********************************************************************
// -------------40;Parallel line input mode.
        JButton Button_heikousen = new JButton("");//Button_suisen	= new JButton(	"suisen"	);
        Button_heikousen.addActionListener(e -> {
            img_explanation_fname = "qqq/heikousen.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.PARALLEL_DRAW_40;
            iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.PARALLEL_DRAW_40;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw4.add(Button_heikousen);
        Button_heikousen.setMargin(new Insets(0, 0, 0, 0));
        Button_heikousen.setIcon(createImageIcon(
                "ppp/heikousen.png"));
// -------------40;平行線入力モード。ここまで

// -------------51;Parallel line width specification input mode.
        JButton Button_heikousen_width_sitei = new JButton("");
        Button_heikousen_width_sitei.addActionListener(e -> {
            img_explanation_fname = "qqq/heikousen_haba_sitei.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.PARALLEL_DRAW_WIDTH_51;
            iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.PARALLEL_DRAW_WIDTH_51;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw4.add(Button_heikousen_width_sitei);
        Button_heikousen_width_sitei.setMargin(new Insets(0, 0, 0, 0));
        Button_heikousen_width_sitei.setIcon(createImageIcon(
                "ppp/heikousen_haba_sitei.png"));
// -------------51;平行線　幅指定入力モード。ここまで


// *******西***********************************************************************
// -------------71;Foldable line + grid point system input
        JButton Button_oritatami_kanousen_and_kousitenkei_simple = new JButton("");
        Button_oritatami_kanousen_and_kousitenkei_simple.addActionListener(e -> {
            img_explanation_fname = "qqq/oritatami_kanousen_and_kousitenkei_simple.png";
            updateExplanation();

            i_mouse_modeA = MouseMode.FOLDABLE_LINE_DRAW_71;
            iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.FOLDABLE_LINE_DRAW_71;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw4.add(Button_oritatami_kanousen_and_kousitenkei_simple);

        Button_oritatami_kanousen_and_kousitenkei_simple.setMargin(new Insets(0, 0, 0, 0));
        Button_oritatami_kanousen_and_kousitenkei_simple.setIcon(createImageIcon(
                "ppp/oritatami_kanousen_and_kousitenkei_simple.png"));


// -------------39;折り畳み可能線+格子点系入力。ここまで

        //------------------------------------------------
        JPanel pnlw29 = new JPanel();
//         pnlw29.setBackground(Color.PINK);
        pnlw29.setLayout(new GridLayout(1, 2));
        pnlw.add(pnlw29);
        //------------------------------------------------


// ******西**********全kouho線分(線分入力時の一時的な線分)を実折線に変換モード**************************************************************
        JButton Button_all_s_step_to_orisen = new JButton("");//
        Button_all_s_step_to_orisen.addActionListener(e -> {
            System.out.println("i_egaki_dankai = " + es1.i_drawing_stage);
            System.out.println("i_kouho_dankai = " + es1.i_candidate_stage);

            img_explanation_fname = "qqq/all_s_step_to_orisen.png";
            updateExplanation();
            es1.all_s_step_to_orisen();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw29.add(Button_all_s_step_to_orisen);
        Button_all_s_step_to_orisen.setMargin(new Insets(0, 0, 0, 0));
        Button_all_s_step_to_orisen.setIcon(createImageIcon(
                "ppp/all_s_step_to_orisen.png"));

        //Button_v_del_all.setBackground(Color.green);

// ********西**********************************************************************
// -------------33;Fish bone mode.

        JButton Button_sakananohone = new JButton("");
        Button_sakananohone.addActionListener(e -> {
            img_explanation_fname = "qqq/sakananohone.png";
            updateExplanation();

            i_mouse_modeA = MouseMode.FISH_BONE_DRAW_33;
            iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.FISH_BONE_DRAW_33;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw29.add(Button_sakananohone);

        Button_sakananohone.setMargin(new Insets(0, 0, 0, 0));
        Button_sakananohone.setIcon(createImageIcon("ppp/sakananohone.png"));


// -------------10;魚の骨モード。ここまで


// *******西***********************************************************************
// -------------35;Double wrap mode.
        JButton Button_fuku_orikaesi = new JButton("");//Button_orikaesi	= new JButton(	"orikaesi"	);
        Button_fuku_orikaesi.addActionListener(e -> {
            img_explanation_fname = "qqq/fuku_orikaesi.png";
            updateExplanation();

            i_mouse_modeA = MouseMode.DOUBLE_SYMMETRIC_DRAW_35;
            iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.DOUBLE_SYMMETRIC_DRAW_35;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw29.add(Button_fuku_orikaesi);

        Button_fuku_orikaesi.setMargin(new Insets(0, 0, 0, 0));
        Button_fuku_orikaesi.setIcon(createImageIcon("ppp/fuku_orikaesi.png"));


// -------------35;複折り返しモード。ここまで


// ********西**********************************************************************


        //------------------------------------------------
        JPanel pnlw15 = new JPanel();
        pnlw15.setLayout(new GridLayout(1, 3));

        pnlw.add(pnlw15);
        //------------------------------------------------

// ******西************************************************************************
// -----1;線分入力モード。分割数入力
        text2 = new JTextField("", 2);
        text2.setHorizontalAlignment(JTextField.RIGHT);
        pnlw15.add(text2);


// -------------------------------------------------------------------------------

        //Button	Button_lineSegment_division_set
// -----1;Line segment division number set
        JButton Button_lineSegment_division_set = new JButton("Set");
        Button_lineSegment_division_set.addActionListener(e -> {

            int i_orisen_bunkatu_suu_old = foldLineDividingNumber;
            foldLineDividingNumber = StringOp.String2int(text2.getText(), i_orisen_bunkatu_suu_old);
            if (foldLineDividingNumber < 1) {
                foldLineDividingNumber = 1;
            }
            text2.setText(String.valueOf(foldLineDividingNumber));
            es1.setFoldLineDividingNumber(foldLineDividingNumber);

            img_explanation_fname = "qqq/senbun_bunkatu_set.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.LINE_SEGMENT_DIVISION_27;
            iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.LINE_SEGMENT_DIVISION_27;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw15.add(Button_lineSegment_division_set);

        Button_lineSegment_division_set.setMargin(new Insets(0, 0, 0, 0));

// ------1;線分分割数set。ここまで


// -------------------------------------------------------------------------------


// -----27;Line segment division input mode.
        JButton Button_senbun_b_nyuryoku = new JButton("");
        Button_senbun_b_nyuryoku.addActionListener(e -> {

            int i_orisen_bunkatu_suu_old = foldLineDividingNumber;
            foldLineDividingNumber = StringOp.String2int(text2.getText(), i_orisen_bunkatu_suu_old);
            if (foldLineDividingNumber < 1) {
                foldLineDividingNumber = 1;
            }
            text2.setText(String.valueOf(foldLineDividingNumber));
            es1.setFoldLineDividingNumber(foldLineDividingNumber);

            img_explanation_fname = "qqq/senbun_b_nyuryoku.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.LINE_SEGMENT_DIVISION_27;
            iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.LINE_SEGMENT_DIVISION_27;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw15.add(Button_senbun_b_nyuryoku);

        Button_senbun_b_nyuryoku.setMargin(new Insets(0, 0, 0, 0));
        Button_senbun_b_nyuryoku.setIcon(createImageIcon(
                "ppp/senbun_b_nyuryoku.png"));

// ------27;線分入力モード。ここまで


        //------------------------------------------------
        JPanel pnlw6 = new JPanel();
        pnlw6.setLayout(new GridLayout(1, 3));
        pnlw.add(pnlw6);


// ******西************************************************************************
//------------------------------------------------
        JButton Button_select =
                new JButton("sel");
        Button_select.addActionListener(e -> {
            img_explanation_fname = "qqq/Select.png";
            updateExplanation();

            i_mouse_modeA = MouseMode.CREASE_SELECT_19;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw6.add(Button_select);

        Button_select.setBackground(Color.green);
        Button_select.setMargin(new Insets(0, 0, 0, 0));

//------------------------------------------------

// ******西************************************************************************
        JButton Button_select_all = new JButton("s_al");//
        Button_select_all.addActionListener(e -> {

            img_explanation_fname = "qqq/select_all.png";
            updateExplanation();
            es1.select_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw6.add(Button_select_all);
        Button_select_all.setBackground(Color.green);
        Button_select_all.setMargin(new Insets(0, 0, 0, 0));

// ****西**************************************************************************

        //------------------------------------------------
        JPanel pnlw7 = new JPanel();
        pnlw7.setLayout(new GridLayout(1, 2));
        pnlw.add(pnlw7);


//------------------------------------------------
        JButton Button_unselect =
                new JButton("unsel");
        Button_unselect.addActionListener(e -> {
            img_explanation_fname = "qqq/unselect.png";
            updateExplanation();

            i_mouse_modeA = MouseMode.CREASE_UNSELECT_20;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw7.add(Button_unselect);
        Button_unselect.setBackground(Color.green);
        Button_unselect.setMargin(new Insets(0, 0, 0, 0));


//------------------------------------------------

        JButton Button_unselect_all = new JButton("uns_al");//
        Button_unselect_all.addActionListener(e -> {

            img_explanation_fname = "qqq/unselect_all.png";
            updateExplanation();
            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw7.add(Button_unselect_all);
        Button_unselect_all.setBackground(Color.green);
        Button_unselect_all.setMargin(new Insets(0, 0, 0, 0));
// ******************************************************************************

// *****西*************************************************************************


        //------------------------------------------------
        JPanel pnlw16 = new JPanel();
//         pnlw16.setBackground(Color.PINK);
        pnlw16.setLayout(new GridLayout(1, 2));
        pnlw.add(pnlw16);


// -------------21;移動モード。
        Button_move = new JButton("move");
        Button_move.addActionListener(e -> {

            img_explanation_fname = "qqq/move.png";
            updateExplanation();
            selectionOperationMode = SelectionOperationMode.MOVE_1;
            Button_sel_mou_wakukae();

            i_mouse_modeA = MouseMode.CREASE_MOVE_21;
            Button_kyoutuu_sagyou();
            canvas.repaint();
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);
        });
        pnlw16.add(Button_move);
        Button_move.setBackground(new Color(170, 220, 170));
        Button_move.setMargin(new Insets(0, 0, 0, 0));
// -------------21;移動モード。ここまで


// -------------31;移動2p2pモード。
        Button_move_2p2p = new JButton("mv_4p");
        Button_move_2p2p.addActionListener(e -> {

            img_explanation_fname = "qqq/move_2p2p.png";
            updateExplanation();
            selectionOperationMode = SelectionOperationMode.MOVE4P_2;
            Button_sel_mou_wakukae();


            i_mouse_modeA = MouseMode.CREASE_MOVE_4P_31;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw16.add(Button_move_2p2p);
        Button_move_2p2p.setBackground(new Color(170, 220, 170));
        Button_move_2p2p.setMargin(new Insets(0, 0, 0, 0));
// -------------31;移動2p2pモード。ここまで


// *********西*********************************************************************


        //------------------------------------------------
        JPanel pnlw17 = new JPanel();
        pnlw17.setLayout(new GridLayout(1, 2));
        pnlw.add(pnlw17);


// -------------22;コピーモード。
        Button_copy_paste = new JButton("copy");
        Button_copy_paste.addActionListener(e -> {

            img_explanation_fname = "qqq/copy_paste.png";
            updateExplanation();
            selectionOperationMode = SelectionOperationMode.COPY_3;
            Button_sel_mou_wakukae();


            i_mouse_modeA = MouseMode.CREASE_COPY_22;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw17.add(Button_copy_paste);
        Button_copy_paste.setBackground(new Color(170, 220, 170));
        Button_copy_paste.setMargin(new Insets(0, 0, 0, 0));
        //Button_copy_paste.setIcon(createImageIcon(
        //  "ppp/copy_paste.png")));
// -------------22;コピーモード。ここまで


// -------------32;コピー2p2pモード。
        Button_copy_paste_2p2p = new JButton("cp_4p");
        Button_copy_paste_2p2p.addActionListener(e -> {

            img_explanation_fname = "qqq/copy_paste_2p2p.png";
            updateExplanation();
            selectionOperationMode = SelectionOperationMode.COPY4P_4;
            Button_sel_mou_wakukae();


            i_mouse_modeA = MouseMode.CREASE_COPY_4P_32;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw17.add(Button_copy_paste_2p2p);
        Button_copy_paste_2p2p.setBackground(new Color(170, 220, 170));
        Button_copy_paste_2p2p.setMargin(new Insets(0, 0, 0, 0));
        //Button_copy_paste_2p2p.setIcon(createImageIcon(
        //  "ppp/copy_paste_2p2p.png")));
// -------------32;コピー2p2pモード。ここまで


// ********西**********************************************************************

        //------------------------------------------------
        JPanel pnlw35 = new JPanel();
//         pnlw35.setBackground(Color.PINK);
        pnlw35.setLayout(new GridLayout(1, 2));
        pnlw.add(pnlw35);


// -------------12;鏡映モード。
        Button_kyouei = new JButton("");//new JButton(	"kyouei"	);
        Button_kyouei.addActionListener(e -> {

            img_explanation_fname = "qqq/kyouei.png";
            updateExplanation();
            selectionOperationMode = SelectionOperationMode.MIRROR_5;
            Button_sel_mou_wakukae();

            i_mouse_modeA = MouseMode.DRAW_CREASE_SYMMETRIC_12;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw35.add(Button_kyouei);
        Button_kyouei.setBackground(new Color(170, 220, 170));
        Button_kyouei.setMargin(new Insets(0, 0, 0, 0));
        Button_kyouei.setIcon(createImageIcon(
                "ppp/kyouei.png"));


// -------------12;鏡映モード。ここまで


// *******西***********************************************************************
// -------------;selectした折線の削除
        JButton Button_del_selected_senbun = new JButton("d_s_L");//new JButton(	"del_sel_L"	);
        Button_del_selected_senbun.addActionListener(e -> {

            img_explanation_fname = "qqq/del_selected_senbun.png";
            updateExplanation();
            es1.del_selected_senbun();
            es1.record();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw35.add(Button_del_selected_senbun);
        Button_del_selected_senbun.setMargin(new Insets(0, 0, 0, 0));
        //Button_del_selected_senbun.setIcon(createImageIcon(
        // "ppp/del_selected_senbun.png")));


// -------------selectした折線の削除。ここまで


        //JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        //separator.setPreferredSize(new Dimension(50 , 1));
//pnlw.add(separator);


        //------------------------------------------------
        JPanel pnlw5 = new JPanel();
//         pnlw5.setBackground(Color.PINK);
        pnlw5.setLayout(new GridLayout(1, 2));
        pnlw.add(pnlw5);


// ******西************************************************************************ 消しゴム

// -------------;線分削除モード。
        JButton Button_senbun_sakujyo = new JButton("");
        Button_senbun_sakujyo.addActionListener(e -> {

            img_explanation_fname = "qqq/senbun_sakujyo.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.LINE_SEGMENT_DELETE_3;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);


            foldLineAdditionalInputMode = Drawing_Worker.FoldLineAdditionalInputMode.POLY_LINE_0;//=0は折線入力　=1は補助線入力モード
            es1.setFoldLineAdditional(foldLineAdditionalInputMode);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw5.add(Button_senbun_sakujyo);

        Button_senbun_sakujyo.setMargin(new Insets(0, 0, 0, 0));
        Button_senbun_sakujyo.setIcon(createImageIcon(
                "ppp/senbun_sakujyo.png"));


// ******西************************************************************************ 黒線のみの消しゴム

// -------------;黒線のみの線分削除モード。
        JButton Button_kuro_lineSegment_removal = new JButton("");
        Button_kuro_lineSegment_removal.addActionListener(e -> {

            img_explanation_fname = "qqq/kuro_senbun_sakujyo.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.LINE_SEGMENT_DELETE_3;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);


            foldLineAdditionalInputMode = Drawing_Worker.FoldLineAdditionalInputMode.BLACK_LINE_2;//= 2 is the black polygonal line deletion mode
            es1.setFoldLineAdditional(foldLineAdditionalInputMode);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw5.add(Button_kuro_lineSegment_removal);

        Button_kuro_lineSegment_removal.setMargin(new Insets(0, 0, 0, 0));
        Button_kuro_lineSegment_removal.setIcon(createImageIcon(
                "ppp/kuro_senbun_sakujyo.png"));


// ******西************************************************************************ 消しゴム(補助活線のみ)

// -------------;線分削除モード。消しゴム(補助活線のみ)
        JButton Button_senbun3_sakujyo = new JButton("");
        Button_senbun3_sakujyo.addActionListener(e -> {

            img_explanation_fname = "qqq/senbun3_sakujyo.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.LINE_SEGMENT_DELETE_3;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);


            foldLineAdditionalInputMode = Drawing_Worker.FoldLineAdditionalInputMode.AUX_LIVE_LINE_3;//= 0 is polygonal line input = 1 is auxiliary line input mode = 3 is for auxiliary live line only
            es1.setFoldLineAdditional(foldLineAdditionalInputMode);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw5.add(Button_senbun3_sakujyo);

        Button_senbun3_sakujyo.setMargin(new Insets(0, 0, 0, 0));
        Button_senbun3_sakujyo.setIcon(createImageIcon(
                "ppp/senbun3_sakujyo.png"));


// *********西*********************************************************************
// -------------;
        JButton Button_eda_kesi = new JButton("");//JButton	Button_eda_kesi		= new JButton(	"Trim"	);
        Button_eda_kesi.addActionListener(e -> {

            img_explanation_fname = "qqq/eda_kesi.png";
            updateExplanation();
            es1.point_removal();
            es1.overlapping_line_removal();
            es1.branch_trim(0.000001);
            es1.circle_organize();
            es1.record();
            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw5.add(Button_eda_kesi);


        Button_eda_kesi.setMargin(new Insets(0, 0, 0, 0));
        Button_eda_kesi.setIcon(createImageIcon(
                "ppp/eda_kesi.png"));

// ******西************************************************************************


// *****西*************************************************************************


        //------------------------------------------------
        JPanel pnlw8 = new JPanel();
//         pnlw8.setBackground(Color.PINK);
        pnlw8.setLayout(new GridLayout(1, 4));
        //------------------------------------------------
        pnlw.add(pnlw8);

// *******西***********************************************************************
        Button_M_nisuru = new JButton(" ");
        Button_M_nisuru.addActionListener(e -> {
            img_explanation_fname = "qqq/M_nisuru.png";
            updateExplanation();
            Button_reset();
            Button_M_nisuru.setForeground(Color.black);
            Button_M_nisuru.setBackground(Color.red);
            //icol=1;es1.setcolor(icol);
            i_mouse_modeA = MouseMode.CREASE_MAKE_MOUNTAIN_23;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();


        });
        pnlw8.add(Button_M_nisuru);
        Button_M_nisuru.setBackground(Color.white);
        Button_M_nisuru.setMargin(new Insets(0, 0, 0, 0));

        Button_M_nisuru.setIcon(createImageIcon(
                "ppp/M_nisuru.png"));

//Button_M_nisuru.setHorizontalTextPosition(JButton.RIGHT);


// ******************************************************************************
        Button_V_nisuru = new JButton(" ");
        Button_V_nisuru.addActionListener(e -> {
            img_explanation_fname = "qqq/V_nisuru.png";
            updateExplanation();
            Button_reset();
            Button_V_nisuru.setForeground(Color.black);
            Button_V_nisuru.setBackground(Color.blue);
            //icol=1;es1.setcolor(icol);
            i_mouse_modeA = MouseMode.CREASE_MAKE_VALLEY_24;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw8.add(Button_V_nisuru);
        Button_V_nisuru.setBackground(Color.white);
        Button_V_nisuru.setMargin(new Insets(0, 0, 0, 0));

        Button_V_nisuru.setIcon(createImageIcon(
                "ppp/V_nisuru.png"));
// ******************************************************************************
        Button_E_nisuru = new JButton(" ");
        Button_E_nisuru.addActionListener(e -> {
            img_explanation_fname = "qqq/E_nisuru.png";
            updateExplanation();
            Button_reset();
            Button_E_nisuru.setForeground(Color.white);
            Button_E_nisuru.setBackground(Color.black);
            //icol=1;es1.setcolor(icol);
            i_mouse_modeA = MouseMode.CREASE_MAKE_EDGE_25;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw8.add(Button_E_nisuru);
        Button_E_nisuru.setBackground(Color.white);
        Button_E_nisuru.setMargin(new Insets(0, 0, 0, 0));

        Button_E_nisuru.setIcon(createImageIcon("ppp/E_nisuru.png"));


// ******************************************************************************

        Button_HK_nisuru = new JButton(" ");//HKとは補助活線のこと
        Button_HK_nisuru.addActionListener(e -> {
            img_explanation_fname = "qqq/HK_nisuru.png";
            updateExplanation();
            Button_reset();
            Button_HK_nisuru.setForeground(Color.white);
            Button_HK_nisuru.setBackground(new Color(100, 200, 200));
            //icol=1;es1.setcolor(icol);
            i_mouse_modeA = MouseMode.CREASE_MAKE_AUX_60;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw8.add(Button_HK_nisuru);
        Button_HK_nisuru.setBackground(Color.white);
        Button_HK_nisuru.setMargin(new Insets(0, 0, 0, 0));

        Button_HK_nisuru.setIcon(createImageIcon("ppp/HK_nisuru.png"));


// ******************************************************************************

        //------------------------------------------------
        JPanel pnlw28 = new JPanel();
//         pnlw28.setBackground(Color.PINK);
        pnlw28.setLayout(new GridLayout(1, 2));

        //------------------------------------------------
        pnlw.add(pnlw28);


// *****西*************************************************************************
        JButton Button_zen_yama_tani_henkan = new JButton("AC");
        Button_zen_yama_tani_henkan.addActionListener(e -> {
            img_explanation_fname = "qqq/zen_yama_tani_henkan.png";
            updateExplanation();
            es1.allMountainValleyChange();
            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw28.add(Button_zen_yama_tani_henkan);
        Button_zen_yama_tani_henkan.setMargin(new Insets(0, 0, 0, 0));
// ******西************************************************************************線分の色を赤から青、青から赤に変換

        Button_senbun_henkan2 = new JButton("");//new JButton(	"L_chan"	);
        Button_senbun_henkan2.addActionListener(e -> {

            img_explanation_fname = "qqq/senbun_henkan2.png";
            updateExplanation();
            Button_reset();
            Button_senbun_henkan2.setBackground(new Color(138, 43, 226));

            i_mouse_modeA = MouseMode.CREASE_TOGGLE_MV_58;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw28.add(Button_senbun_henkan2);

        Button_senbun_henkan2.setBackground(Color.white);
        Button_senbun_henkan2.setMargin(new Insets(0, 0, 0, 0));
        Button_senbun_henkan2.setIcon(createImageIcon(
                "ppp/senbun_henkan2.png"));

// ******西************************************************************************線分の色を黒、赤、、青、黒の順に変換

        JButton Button_senbun_henkan = new JButton("");//new JButton(	"L_chan"	);
        Button_senbun_henkan.addActionListener(e -> {

            img_explanation_fname = "qqq/senbun_henkan.png";
            updateExplanation();
            Button_reset();

            i_mouse_modeA = MouseMode.CHANGE_CREASE_TYPE_4;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw28.add(Button_senbun_henkan);

        Button_senbun_henkan.setMargin(new Insets(0, 0, 0, 0));
        Button_senbun_henkan.setIcon(createImageIcon("ppp/senbun_henkan.png"));


// ******西************************************************************************

        //------------------------------------------------
        JPanel pnlw21 = new JPanel();
        pnlw21.setLayout(new GridLayout(1, 3));
        //------------------------------------------------
        pnlw.add(pnlw21);

// ****************************************************************************** //線内色変換

        JButton Button_in_L_col_change = new JButton("");//new JButton(	"in_L_col_change"	);
        Button_in_L_col_change.addActionListener(e -> {
            img_explanation_fname = "qqq/in_L_col_change.png";
            updateExplanation();

            i_mouse_modeA = MouseMode.CREASE_MAKE_MV_34;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);
            iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.CREASE_MAKE_MV_34;


            if (icol == LineColor.BLACK_0) {
                icol = LineColor.RED_1;
                es1.setColor(icol);                                        //最初の折線の色を指定する。0は黒、1は赤、2は青。
                ButtonCol_reset();
                ButtonCol_red.setForeground(Color.black);
                ButtonCol_red.setBackground(Color.red);    //折線のボタンの色設定
            }


            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw21.add(Button_in_L_col_change);

        Button_in_L_col_change.setMargin(new Insets(0, 0, 0, 0));
        Button_in_L_col_change.setIcon(createImageIcon(
                "ppp/in_L_col_change.png"));


// ****************************************************************************** //線X交差色変換

        JButton Button_on_L_col_change = new JButton("");//new JButton(	"on_L_col_change"	);
        Button_on_L_col_change.addActionListener(e -> {
            img_explanation_fname = "qqq/on_L_col_change.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.CREASES_ALTERNATE_MV_36;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);
            iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.CREASES_ALTERNATE_MV_36;


            if (icol == LineColor.BLACK_0) {
                icol = LineColor.BLUE_2;
                es1.setColor(icol);                                        //最初の折線の色を指定する。0は黒、1は赤、2は青。
                ButtonCol_reset();
                ButtonCol_blue.setForeground(Color.black);
                ButtonCol_blue.setBackground(Color.blue);    //折線のボタンの色設定
            }


            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw21.add(Button_on_L_col_change);

        Button_on_L_col_change.setMargin(new Insets(0, 0, 0, 0));
        Button_on_L_col_change.setIcon(createImageIcon("ppp/on_L_col_change.png"));


// *******西***********************************************************************

        //------------------------------------------------
        JPanel pnlw10 = new JPanel();
        pnlw10.setLayout(new GridLayout(1, 4));

        pnlw.add(pnlw10);
        //------------------------------------------------
// -------------14;Point addition mode.
        JButton Button_v_add = new JButton("");// new JButton(	"V_add"	);
        Button_v_add.addActionListener(e -> {

            img_explanation_fname = "qqq/v_add.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.DRAW_POINT_14;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw10.add(Button_v_add);

        Button_v_add.setMargin(new Insets(0, 0, 0, 0));
        Button_v_add.setIcon(createImageIcon("ppp/v_add.png"));


// -------------14;点追加モード。ここまで

// ******************************************************************************
// -------------15;点削除モード。
        JButton Button_v_del = new JButton("");//new JButton(	"V_del"	);
        Button_v_del.addActionListener(e -> {
            img_explanation_fname = "qqq/v_del.png";
            updateExplanation();

            i_mouse_modeA = MouseMode.DELETE_POINT_15;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw10.add(Button_v_del);

        Button_v_del.setMargin(new Insets(0, 0, 0, 0));
        Button_v_del.setIcon(createImageIcon(
                "ppp/v_del.png"));


// -------------15;点削除モード。ここまで


// -------------15;点削除モード（カラーチェンジ）。
        JButton Button_v_del_cc = new JButton("");//new JButton(	"V_del"	);
        Button_v_del_cc.addActionListener(e -> {
            img_explanation_fname = "qqq/v_del_cc.png";
            updateExplanation();

            i_mouse_modeA = MouseMode.VERTEX_DELETE_ON_CREASE_41;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw10.add(Button_v_del_cc);

        Button_v_del_cc.setMargin(new Insets(0, 0, 0, 0));
        Button_v_del_cc.setIcon(createImageIcon(
                "ppp/v_del_cc.png"));


// -------------15;点削除モード。ここまで

// ****西**************************************************************************


        //------------------------------------------------
        JPanel pnlw13 = new JPanel();
//         pnlw13.setBackground(Color.PINK);
        pnlw13.setLayout(new GridLayout(1, 3));

        //------------------------------------------------
        pnlw.add(pnlw13);


// ******************************************************************************
// ******西**********全点削除モード。*****(両側の折線の色が同じものに実行)。*********************************************************
        JButton Button_v_del_all = new JButton("");//
        Button_v_del_all.addActionListener(e -> {

            img_explanation_fname = "qqq/v_del_all.png";
            updateExplanation();
            //i_mouse_modeA=19;
            es1.v_del_all();
            System.out.println("es1.v_del_all()");
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw13.add(Button_v_del_all);
        Button_v_del_all.setMargin(new Insets(0, 0, 0, 0));
        Button_v_del_all.setIcon(createImageIcon(
                "ppp/v_del_all.png"));

        //Button_v_del_all.setBackground(Color.green);


// ******西**********全点削除モード(両側の折線の色が違っても実行)。**************************************************************
        JButton Button_v_del_all_cc = new JButton("");//
        Button_v_del_all_cc.addActionListener(e -> {

            img_explanation_fname = "qqq/v_del_all_cc.png";
            updateExplanation();
            es1.v_del_all_cc();
            System.out.println("es1.v_del_all_cc()");
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw13.add(Button_v_del_all_cc);
        Button_v_del_all_cc.setMargin(new Insets(0, 0, 0, 0));
        Button_v_del_all_cc.setIcon(createImageIcon(
                "ppp/v_del_all_cc.png"));

// ****西**************************************************************************


        //------------------------------------------------
        JPanel pnlw32 = new JPanel();
//         pnlw32.setBackground(Color.PINK);
        pnlw32.setLayout(new GridLayout(1, 3));

        //------------------------------------------------
        pnlw.add(pnlw32);


// *********西*********************************************************************

        //------------------------------------------------
        JPanel pnlw9 = new JPanel();
        pnlw9.setBounds(2, 2, 93, 20);
//         pnlw9.setBackground(Color.PINK);
        pnlw9.setLayout(null);
        //pnlw9.setLayout(new GridLayout(1,5));
        //------------------------------------------------
        pnlw.add(pnlw9);


// *******西***********************************************************************
//------------------------------------------------
        //格子表示2
        Button_grid_decrease = new JButton("");//new JButton(	"Grid2"	);
        Button_grid_decrease.addActionListener(e -> {
            img_explanation_fname = "qqq/kitei2.png";
            updateExplanation();

            nyuuryoku_kitei = nyuuryoku_kitei / 2;
            if (nyuuryoku_kitei < 1) {
                nyuuryoku_kitei = 1;
            }

            if (nyuuryoku_kitei < -0) {
                nyuuryoku_kitei = -1;
            }

            //ボタンの色変え
            if (nyuuryoku_kitei >= 1) {
                Button_grid_increase.setForeground(Color.black);
                Button_grid_increase.setBackground(Color.white);
            }
            if (nyuuryoku_kitei == 0) {
                Button_grid_increase.setForeground(Color.black);
                Button_grid_increase.setBackground(new Color(0, 200, 200));
            }
            //ボタンの色変え(ここまで)
            //ボタンの色変え
            if (nyuuryoku_kitei >= 1) {
                Button_grid_decrease.setForeground(Color.black);
                Button_grid_decrease.setBackground(Color.white);
            }
            if (nyuuryoku_kitei == 0) {
                Button_grid_decrease.setForeground(Color.black);
                Button_grid_decrease.setBackground(new Color(0, 200, 200));
            }
            //ボタンの色変え(ここまで)

            text1.setText(String.valueOf(nyuuryoku_kitei));
            es1.setGridDivisionNumber(nyuuryoku_kitei);
            canvas.repaint();
        });
        pnlw9.add(Button_grid_decrease);
        Button_grid_decrease.setBounds(0, 1, 20, 19);

        Button_grid_decrease.setMargin(new Insets(0, 0, 0, 0));
        Button_grid_decrease.setIcon(createImageIcon("ppp/kitei2.png"));


// *****西*************************************************************************

        text1 = new JTextField("", 2);
        text1.setHorizontalAlignment(JTextField.RIGHT);
        text1.setBounds(20, 1, 35, 19);
        pnlw9.add(text1);

// *****西*************************************************************************
        //JButton button_syutoku = new JButton("取得");
        JButton Button_syutoku = new JButton("S");
        Button_syutoku.addActionListener(e -> {


            img_explanation_fname = "qqq/syutoku.png";
            updateExplanation();
            set_grid_bunkatu_suu();


        });
        pnlw9.add(Button_syutoku);
        Button_syutoku.setBounds(55, 1, 15, 19);
        Button_syutoku.setMargin(new Insets(0, 0, 0, 0));


//-------西-----------------------------------------
        //格子表示
        Button_grid_increase = new JButton("");// new JButton(	"Grid"	);

        Button_grid_increase.addActionListener(e -> {
            img_explanation_fname = "qqq/kitei.png";
            updateExplanation();

            nyuuryoku_kitei = nyuuryoku_kitei * 2;

            //ボタンの色変え
            if (nyuuryoku_kitei >= 1) {
                Button_grid_increase.setForeground(Color.black);
                Button_grid_increase.setBackground(Color.white);
            }
            if (nyuuryoku_kitei == 0) {
                Button_grid_increase.setForeground(Color.black);
                Button_grid_increase.setBackground(new Color(0, 200, 200));
            }
            //ボタンの色変え(ここまで)
            //ボタンの色変え
            if (nyuuryoku_kitei >= 1) {
                Button_grid_decrease.setForeground(Color.black);
                Button_grid_decrease.setBackground(Color.white);
            }
            if (nyuuryoku_kitei == 0) {
                Button_grid_decrease.setForeground(Color.black);
                Button_grid_decrease.setBackground(new Color(0, 200, 200));
            }
            //ボタンの色変え(ここまで)
            text1.setText(String.valueOf(nyuuryoku_kitei));
            es1.setGridDivisionNumber(nyuuryoku_kitei);
            canvas.repaint();
        });
        pnlw9.add(Button_grid_increase);

        Button_grid_increase.setBounds(70, 1, 20, 19);
        Button_grid_increase.setMargin(new Insets(0, 0, 0, 0));
        Button_grid_increase.setIcon(createImageIcon(
                "ppp/kitei.png"));

//------------------------------------//System.out.println("__");----

// -------------格子線の色の選択
        JButton Button_grid_color = new JButton("C");
        Button_grid_color.addActionListener(e -> {
            img_explanation_fname = "qqq/kousi_color.png";
            updateExplanation();
            //Button_kyoutuu_sagyou();
            i_mouseDragged_valid = false;
            i_mouseReleased_valid = false;

            //以下にやりたいことを書く
            Color color = JColorChooser.showDialog(null, "Col", new Color(230, 230, 230));
            if (color != null) {
                kus.setGridColor(color);
            }
            //以上でやりたいことは書き終わり

            canvas.repaint();
        });
        Button_grid_color.setBounds(94, 1, 15, 19);
        Button_grid_color.setMargin(new Insets(0, 0, 0, 0));
        pnlw9.add(Button_grid_color);

        //重要注意　読み込みや書き出しでファイルダイアログのボックスが開くと、それをフレームに重なる位置で操作した場合、ファイルボックスが消えたときに、
        //マウスのドラッグとリリースが発生する。このため、余計な操作がされてしまう可能性がある。なお、このときマウスクリックは発生しない。
        // i_mouseDragged_valid=0;や i_mouseReleased_valid=0;は、この余計な操作を防ぐために指定している。

// ********西**********************************************************************
        //------------------------------------------------
        JPanel pnlw34 = new JPanel();
        pnlw34.setBounds(2, 2, 93, 20);
//         pnlw34.setBackground(Color.PINK);
        pnlw34.setLayout(null);
        //------------------------------------------------
        pnlw.add(pnlw34);

// ****西**************************************************************************
        JButton Button_grid_lineWidth_decrease = new JButton("");
        Button_grid_lineWidth_decrease.addActionListener(e -> {
            kus.decreaseGridLineWidth();
            img_explanation_fname = "qqq/kousi_senhaba_sage.png";
            updateExplanation();
            //Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw34.add(Button_grid_lineWidth_decrease);
        Button_grid_lineWidth_decrease.setBounds(0, 1, 20, 19);
        Button_grid_lineWidth_decrease.setMargin(new Insets(0, 0, 0, 0));
        Button_grid_lineWidth_decrease.setIcon(createImageIcon(
                "ppp/kousi_senhaba_sage.png"));

// ****西**************************************************************************

        JButton Button_grid_lineWidthIncrease = new JButton("");
        Button_grid_lineWidthIncrease.addActionListener(e -> {
            kus.increaseGridLineWidth();
            img_explanation_fname = "qqq/kousi_senhaba_age.png";
            updateExplanation();
            //Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw34.add(Button_grid_lineWidthIncrease);
        Button_grid_lineWidthIncrease.setBounds(20, 1, 20, 19);
        Button_grid_lineWidthIncrease.setMargin(new Insets(0, 0, 0, 0));
        Button_grid_lineWidthIncrease.setIcon(createImageIcon(
                "ppp/kousi_senhaba_age.png"));

// ---------------------------------------------------


        //基底（格子）の状況   =0は全域で無効だが、格子幅だけは既存端点への引き寄せ半径の設定に使うので有効、状況=1は用紙内のみ有効、状況=2は全領域で有効
        JButton Button_i_kitei_jyoutai = new JButton("");
        Button_i_kitei_jyoutai.addActionListener(e -> {


            img_explanation_fname = "qqq/i_kitei_jyoutai.png";
            updateExplanation();

            es1.setBaseState(es1.getBaseState().advance());
            //Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw34.add(Button_i_kitei_jyoutai);
        Button_i_kitei_jyoutai.setBounds(40, 1, 69, 19);
        Button_i_kitei_jyoutai.setMargin(new Insets(0, 0, 0, 0));
        Button_i_kitei_jyoutai.setIcon(createImageIcon(
                "ppp/i_kitei_jyoutai.png"));

//------------------------------------------


// ****西**************************************************************************

        //------------------------------------------------
        //Panel   pnlw33 = new JPanel();
//         //	pnlw33.setBackground(Color.PINK);
        //	pnlw33.setLayout(new GridLayout(1,3));
        //pnlw.add(pnlw33);
        //------------------------------------------------
        //------------------------------------------------
        JPanel pnlw33 = new JPanel();
        pnlw33.setBounds(2, 2, 93, 20);
//         pnlw33.setBackground(Color.PINK);
        pnlw33.setLayout(null);
        //------------------------------------------------
        pnlw.add(pnlw33);

// *****西*************************************************************************

        JButton Button_memori_tate_idou = new JButton("");
        Button_memori_tate_idou.addActionListener(e -> {
            img_explanation_fname = "qqq/memori_tate_idou.png";
            updateExplanation();
            es1.a_to_parallel_scale_position_change();

            //Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw33.add(Button_memori_tate_idou);
        Button_memori_tate_idou.setBounds(0, 1, 20, 19);
        Button_memori_tate_idou.setMargin(new Insets(0, 0, 0, 0));
        Button_memori_tate_idou.setIcon(createImageIcon(
                "ppp/memori_tate_idou.png"));

// *****西*************************************************************************


        text25 = new JTextField("", 1);
        text25.setHorizontalAlignment(JTextField.RIGHT);
        text25.setBounds(20, 1, 35, 19);
        pnlw33.add(text25);

// *****西*************************************************************************
        JButton Button_scale_interval_syutoku = new JButton("S");
        Button_scale_interval_syutoku.addActionListener(e -> {
            img_explanation_fname = "qqq/memori_kankaku_syutoku.png";
            updateExplanation();
            int scale_interval_old = scale_interval;
            scale_interval = StringOp.String2int(text25.getText(), scale_interval_old);
            if (scale_interval < 0) {
                scale_interval = 1;
            }
            text25.setText(String.valueOf(scale_interval));
            //es1.set_Ubox_undo_suu(i_undo_suu);
            es1.set_a_to_parallel_scale_interval(scale_interval);
            es1.set_b_to_parallel_scale_interval(scale_interval);

        });
        pnlw33.add(Button_scale_interval_syutoku);
        Button_scale_interval_syutoku.setBounds(55, 1, 15, 19);
        Button_scale_interval_syutoku.setMargin(new Insets(0, 0, 0, 0));

// *****西*************************************************************************

        JButton Button_memori_yoko_idou = new JButton("");
        Button_memori_yoko_idou.addActionListener(e -> {
            img_explanation_fname = "qqq/memori_yoko_idou.png";
            updateExplanation();

            es1.b_to_parallel_scale_position_change();
            //Button_kyoutuu_sagyou();repaint();
        });
        pnlw33.add(Button_memori_yoko_idou);
        Button_memori_yoko_idou.setBounds(70, 1, 20, 19);

        Button_memori_yoko_idou.setMargin(new Insets(0, 0, 0, 0));
        Button_memori_yoko_idou.setIcon(createImageIcon(
                "ppp/memori_yoko_idou.png"));


// -------------格子目盛り線の色の選択
        JButton Button_grid_scale_color = new JButton("C");
        Button_grid_scale_color.addActionListener(e -> {
            img_explanation_fname = "qqq/kousi_memori_color.png";
            updateExplanation();
            //Button_kyoutuu_sagyou();
            i_mouseDragged_valid = false;
            i_mouseReleased_valid = false;


            //以下にやりたいことを書く
            Color color = JColorChooser.showDialog(null, "Col", new Color(180, 200, 180));
            if (color != null) {
                kus.setGridScaleColor(color);
            }
            //以上でやりたいことは書き終わり

            canvas.repaint();
        });
        Button_grid_scale_color.setBounds(94, 1, 15, 19);
        Button_grid_scale_color.setMargin(new Insets(0, 0, 0, 0));
        pnlw33.add(Button_grid_scale_color);


        //重要注意　読み込みや書き出しでファイルダイアログのボックスが開くと、それをフレームに重なる位置で操作した場合、ファイルボックスが消えたときに、
        //マウスのドラッグとリリースが発生する。このため、余計な操作がされてしまう可能性がある。なお、このときマウスクリックは発生しない。
        // i_mouseDragged_valid=0;や i_mouseReleased_valid=0;は、この余計な操作を防ぐために指定している。


// ********************************************************


        //------------------------------------------------
        JPanel pnlw19 = new JPanel();
        //pnlw19.setBounds(103, 2, 93, 20);
        pnlw19.setBounds(2, 2, 93, 20);
//         pnlw19.setBackground(Color.PINK);
        pnlw19.setLayout(null);
        //pnlw19.setBorder(new LineBorder(Color.black, 1));

        pnlw.add(pnlw19);
        //------------------------------------------------

        text21 = new JTextField("", 2);


        text21.setBounds(2, 2, 30, 17);
        text21.setHorizontalAlignment(JTextField.RIGHT);
        pnlw19.add(text21);

        JLabel Lb08;
        Lb08 = new JLabel();
        Lb08.setBounds(32, 2, 8, 17);
        Lb08.setIcon(createImageIcon("ppp/plus_min.png"));
        pnlw19.add(Lb08);

        text22 = new JTextField("", 2);
        text22.setBounds(40, 2, 30, 17);
        text22.setHorizontalAlignment(JTextField.RIGHT);
        pnlw19.add(text22);

        JLabel Lb09;
        Lb09 = new JLabel();
        Lb09.setBounds(70, 2, 9, 17);
        Lb09.setIcon(createImageIcon("ppp/root_min.png"));
        pnlw19.add(Lb09);

        text23 = new JTextField("", 2);
        text23.setBounds(79, 2, 30, 17);
        text23.setHorizontalAlignment(JTextField.RIGHT);
        pnlw19.add(text23);

        //------------------------------------------------
        JPanel pnlw18 = new JPanel();

        pnlw18.setBounds(2, 2, 70, 20);
//         pnlw18.setBackground(Color.PINK);
        pnlw18.setLayout(null);
        //pnlw18.setBorder(new LineBorder(Color.black, 1));

        pnlw.add(pnlw18);
        //------------------------------------------------

        text18 = new JTextField("", 2);
        text18.setBounds(2, 2, 30, 17);
        text18.setHorizontalAlignment(JTextField.RIGHT);
        pnlw18.add(text18);

        JLabel Lb06;
        Lb06 = new JLabel();
        Lb06.setBounds(32, 2, 8, 17);
        Lb06.setIcon(createImageIcon("ppp/plus_min.png"));
        pnlw18.add(Lb06);

        text19 = new JTextField("", 2);
        text19.setBounds(40, 2, 30, 17);
        text19.setHorizontalAlignment(JTextField.RIGHT);
        pnlw18.add(text19);

        JLabel Lb07;
        Lb07 = new JLabel();
        Lb07.setBounds(70, 2, 9, 17);
        Lb07.setIcon(createImageIcon("ppp/root_min.png"));
        pnlw18.add(Lb07);

        text20 = new JTextField("", 2);
        text20.setBounds(79, 2, 30, 17);
        text20.setHorizontalAlignment(JTextField.RIGHT);
        pnlw18.add(text20);


// *****西*************************************************************************
        //------------------------------------------------
        JPanel pnlw14 = new JPanel();
//         pnlw14.setBackground(Color.PINK);
        pnlw14.setLayout(new GridLayout(1, 4));
        //pnlw9.setLayout(new FlowLayout(FlowLayout.LEFT));
        //pnlw9.setLayout(new FlowLayout(FlowLayout.LEFT));
        //------------------------------------------------

        pnlw.add(pnlw14);
// *****西*************************************************************************

        text24 = new JTextField("", 2);
        text24.setHorizontalAlignment(JTextField.RIGHT);
        pnlw14.add(text24);

// *****西*************************************************************************
        JButton Button_grid_syutoku = new JButton("Set");
        Button_grid_syutoku.addActionListener(e -> {
            img_explanation_fname = "qqq/kousi_syutoku.png";
            updateExplanation();
            setGrid();
            //Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnlw14.add(Button_grid_syutoku);

        Button_grid_syutoku.setMargin(new Insets(0, 0, 0, 0));
        //Button_grid_syutoku.setIcon(createImageIcon(
        //  "ppp/kousi_syutoku.png")));


// *****西*************************************************************************xxxxxxxxxxxxxx


//------------------------------------------------qqqqqq
// ***東***************************************************************************

        // *************************************************
        //右辺（東側）パネルの構築*************************
        // *************************************************
        //右辺（東側）パネルの作成
        JPanel pnle = new JPanel();
//         pnle.setBackground(Color.PINK);
        pnle.setLayout(new GridLayout(28, 1));

        //右辺（東側）パネルをレイアウトに貼り付け
        contentPane.add("East", pnle); //Frame用
        //contentPane.add(pnle, BorderLayout.EAST);//JFrame用
//------------------------------------------------


        //------------------------------------------------
        JPanel pnle20 = new JPanel();
//         pnle20.setBackground(Color.PINK);
        pnle20.setLayout(new GridLayout(1, 2));
        pnle.add(pnle20);
        //------------------------------------------------
// ***東***チェック系************************************************************************


        ckbox_check1 = new JCheckBox("ckO");
        ckbox_check1.addActionListener(e -> {
            img_explanation_fname = "qqq/check1.png";
            updateExplanation();
            es1.unselect_all();

            if (ckbox_check1.isSelected()) {
                es1.check1(0.001, 0.5);//r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度
                es1.set_i_check1(true);
            } else {
                es1.set_i_check1(false);
            }
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        ckbox_check1.setIcon(createImageIcon("ppp/ckbox_check1_off.png"));
        ckbox_check1.setSelectedIcon(createImageIcon("ppp/ckbox_check1_on.png"));
        ckbox_check1.setBorderPainted(true);
        ckbox_check1.setMargin(new Insets(0, 0, 0, 0));
        pnle20.add(ckbox_check1);


//------------------------------------------


        JButton Button_fix1 = new JButton("fxO");
        Button_fix1.addActionListener(e -> {


            img_explanation_fname = "qqq/fix1.png";
            updateExplanation();
            es1.unselect_all();
            es1.fix1(0.001, 0.5);
            es1.check1(0.001, 0.5);
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle20.add(Button_fix1);

        Button_fix1.setMargin(new Insets(0, 0, 0, 0));
        //Button_check.setIcon(createImageIcon(
        //"ppp/check.png")));


//------------------------------------------
        //------------------------------------------------
        JPanel pnle21 = new JPanel();
//         pnle21.setBackground(Color.PINK);
        pnle21.setLayout(new GridLayout(1, 2));
        pnle.add(pnle21);
        //------------------------------------------------


// ***東***チェック系************************************************************************

        ckbox_check2 = new JCheckBox("ckT");
        ckbox_check2.addActionListener(e -> {
            img_explanation_fname = "qqq/check2.png";
            updateExplanation();
            es1.unselect_all();

            if (ckbox_check2.isSelected()) {
                es1.check2(0.01, 0.5);//r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度
                es1.setCheck2(true);
            } else {
                es1.setCheck2(false);
            }
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        ckbox_check2.setIcon(createImageIcon("ppp/ckbox_check2_off.png"));
        ckbox_check2.setSelectedIcon(createImageIcon("ppp/ckbox_check2_on.png"));
        ckbox_check2.setBorderPainted(true);
        ckbox_check2.setMargin(new Insets(0, 0, 0, 0));
        pnle21.add(ckbox_check2);


//------------------------------------------


        JButton Button_fix2 = new JButton("fxT");
        Button_fix2.addActionListener(e -> {


            img_explanation_fname = "qqq/fix2.png";
            updateExplanation();
            es1.unselect_all();
            es1.fix2(0.001, 0.5);
            es1.check2(0.001, 0.5);
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle21.add(Button_fix2);

        Button_fix2.setMargin(new Insets(0, 0, 0, 0));
        //Button_check.setIcon(createImageIcon(
        //"ppp/check.png")));

//------------------------------------------


// ***東****チェック系***********************************************************************
        //------------------------------------------------
        JPanel pnle22 = new JPanel();
//         pnle22.setBackground(Color.PINK);
        pnle22.setLayout(new GridLayout(1, 2));
        //pnle.add(pnle22);
        //------------------------------------------------

//---チェック系---------------------------------------20170717 まだckbox_check3は実装していない。

        ckbox_check3 = new JCheckBox("check A");
        ckbox_check3.addActionListener(e -> {
            img_explanation_fname = "qqq/check3.png";
            updateExplanation();
            es1.unselect_all();

            if (ckbox_check3.isSelected()) {
                es1.check3(0.0001);//r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度
                es1.setCheck3(true);
            } else {
                es1.setCheck3(false);
            }
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        ckbox_check3.setIcon(createImageIcon("ppp/ckbox_check3_off.png"));
        ckbox_check3.setSelectedIcon(createImageIcon("ppp/ckbox_check3_on.png"));
        ckbox_check3.setBorderPainted(true);
        ckbox_check3.setMargin(new Insets(0, 0, 0, 0));
        pnle22.add(ckbox_check3);

//------------------------------------------


//---------------頂点のチェック---------------------------
        //------------------------------------------------
        JPanel pnle23 = new JPanel();
//         pnle23.setBackground(Color.PINK);
        pnle23.setLayout(new GridLayout(1, 2));
        pnle.add(pnle23);
        //------------------------------------------------


        ckbox_check4 = new JCheckBox("cAMV");
        ckbox_check4.addActionListener(e -> {
            img_explanation_fname = "qqq/check4.png";
            updateExplanation();
            es1.unselect_all();

            if (ckbox_check4.isSelected()) {
                es1.check4(0.0001);//r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度
                es1.setCheck4(true);
            } else {
                es1.setCheck4(false);
            }
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        ckbox_check4.setIcon(createImageIcon("ppp/ckbox_check4_off.png"));
        ckbox_check4.setSelectedIcon(createImageIcon("ppp/ckbox_check4_on.png"));
        ckbox_check4.setBorderPainted(true);
        ckbox_check4.setMargin(new Insets(0, 0, 0, 0));
        pnle23.add(ckbox_check4);

// ****東**************************************************************************
        //------------------------------------------------
        JPanel pnle29 = new JPanel();
//         pnle29.setBackground(Color.PINK);
        pnle29.setLayout(new GridLayout(1, 2));
        pnle23.add(pnle29);
        //------------------------------------------------


// ****東***頂点チェック結果表示円の色の濃さ調整　下げ***********************************************************************

        JButton Button_ck4_color_sage = new JButton("");
        Button_ck4_color_sage.addActionListener(e -> {
            es1.ck4_color_sage();
            img_explanation_fname = "qqq/ck4_color_sage.png";
            updateExplanation();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle29.add(Button_ck4_color_sage);

        Button_ck4_color_sage.setMargin(new Insets(0, 0, 0, 0));
        Button_ck4_color_sage.setIcon(createImageIcon(
                "ppp/ck4_color_sage.png"));

// ****東***頂点チェック結果表示円の色の濃さ調整　上げ***********************************************************************

        JButton Button_ck4_color_age = new JButton("");
        Button_ck4_color_age.addActionListener(e -> {
            es1.ck4_color_age();
            img_explanation_fname = "qqq/ck4_color_age.png";
            updateExplanation();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle29.add(Button_ck4_color_age);

        Button_ck4_color_age.setMargin(new Insets(0, 0, 0, 0));
        Button_ck4_color_age.setIcon(createImageIcon(
                "ppp/ck4_color_age.png"));


//------------------------------------------


        //------------------------------------------------
        JPanel pnle4 = new JPanel();
//         pnle4.setBackground(Color.PINK);
        pnle4.setLayout(new GridLayout(1, 3));

        pnle.add(pnle4);
        //------------------------------------------------


        //------------------------------------------------
        JPanel pnle6 = new JPanel();
//         pnle6.setBackground(Color.PINK);
        //pnle6.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnle6.setLayout(null);
        //------------------------------------------------
        pnle.add(pnle6);

// 東***　角度系入力　***************************************************************************

        JButton Button_kakudo_kei_a_tiisaku = new JButton("");
        Button_kakudo_kei_a_tiisaku.addActionListener(e -> {
            img_explanation_fname = "qqq/kakudo_kei_a_tiisaku.png";
            updateExplanation();

            switch (angle_system_input_id) {
                case DEG_1:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_13;
                    break;
                case DEG_2:
                    i_mouse_modeA = MouseMode.ANGLE_SYSTEM_16;
                    break;
                case DEG_3:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_2_17;
                    break;
                case DEG_4:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_18;
                    break;
                case DEG_5:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_37;
                    break;
            }
            ;

            id_kakudo_kei_a = id_kakudo_kei_a + 1;//if(id_kakudo_kei_a<2){id_kakudo_kei_a=2;}
            Button_kakudo_kei_a.setText("180/" + id_kakudo_kei_a + "=" + (double) (Math.round((180.0 / ((double) id_kakudo_kei_a)) * 1000)) / 1000.0);

            es1.set_id_angle_system(id_kakudo_kei_a);
            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle6.add(Button_kakudo_kei_a_tiisaku);
        Button_kakudo_kei_a_tiisaku.setMargin(new Insets(0, 0, 0, 0));
        Button_kakudo_kei_a_tiisaku.setIcon(createImageIcon(
                "ppp/tiisaku.png"));
        Button_kakudo_kei_a_tiisaku.setBounds(2, 2, 10, 20);


        //Button_id_kakudo_kei_18.setHorizontalAlignment(JButton.LEFT);
// ***　角度系入力　***************************************************************************


        //-------------------------------------------------------------
        Button_kakudo_kei_a = new JButton("180/" + id_kakudo_kei_a + "=" + (double) (Math.round((180.0 / ((double) id_kakudo_kei_a)) * 1000)) / 1000.0);
        Button_kakudo_kei_a.addActionListener(e -> {
            img_explanation_fname = "qqq/kakudo_kei_a.png";
            updateExplanation();

            switch (angle_system_input_id) {
                case DEG_1:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_13;
                    break;
                case DEG_2:
                    i_mouse_modeA = MouseMode.ANGLE_SYSTEM_16;
                    break;
                case DEG_3:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_2_17;
                    break;
                case DEG_4:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_18;
                    break;
                case DEG_5:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_37;
                    break;
            }

            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            Button_kakudo_kei_a.setText("180/" + id_kakudo_kei_a + "=" + (double) (Math.round((180.0 / ((double) id_kakudo_kei_a)) * 1000)) / 1000.0);

            es1.set_id_angle_system(id_kakudo_kei_a);
            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle6.add(Button_kakudo_kei_a);
//ButtonCol_red.setBackground(new Color(150,150,150));
        Button_kakudo_kei_a.setMargin(new Insets(0, 0, 0, 0));
//ButtonCol_red.setImage("board.png");

        Button_kakudo_kei_a.setBounds(10, 2, 90, 20);


// *東**　角度系入力　***************************************************************************


        JButton Button_kakudo_kei_a_ookiku = new JButton("");
        Button_kakudo_kei_a_ookiku.addActionListener(e -> {
            img_explanation_fname = "qqq/kakudo_kei_a_ookiku.png";
            updateExplanation();

            switch (angle_system_input_id) {
                case DEG_1:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_13;
                    break;
                case DEG_2:
                    i_mouse_modeA = MouseMode.ANGLE_SYSTEM_16;
                    break;
                case DEG_3:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_2_17;
                    break;
                case DEG_4:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_18;
                    break;
                case DEG_5:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_37;
                    break;
            }

            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            id_kakudo_kei_a = id_kakudo_kei_a - 1;
            if (id_kakudo_kei_a < 2) {
                id_kakudo_kei_a = 2;
            }
            Button_kakudo_kei_a.setText("180/" + id_kakudo_kei_a + "=" + (double) (Math.round((180.0 / ((double) id_kakudo_kei_a)) * 1000)) / 1000.0);

            es1.set_id_angle_system(id_kakudo_kei_a);
            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle6.add(Button_kakudo_kei_a_ookiku);
        Button_kakudo_kei_a_ookiku.setMargin(new Insets(0, 0, 0, 0));
        Button_kakudo_kei_a_ookiku.setIcon(createImageIcon(
                "ppp/ookiku.png"));

        Button_kakudo_kei_a_ookiku.setBounds(100, 2, 10, 20);

//東******************************************************************************


        //------------------------------------------------
        JPanel pnle7 = new JPanel();
//         pnle7.setBackground(Color.PINK);
        //pnle6.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnle7.setLayout(null);
        //------------------------------------------------
        pnle.add(pnle7);

//東******　角度系入力　************************************************************************

        JButton Button_kakudo_kei_b_tiisaku = new JButton("");
        Button_kakudo_kei_b_tiisaku.addActionListener(e -> {
            img_explanation_fname = "qqq/kakudo_kei_b_tiisaku.png";
            updateExplanation();

            switch (angle_system_input_id) {
                case DEG_1:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_13;
                    break;
                case DEG_2:
                    i_mouse_modeA = MouseMode.ANGLE_SYSTEM_16;
                    break;
                case DEG_3:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_2_17;
                    break;
                case DEG_4:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_18;
                    break;
                case DEG_5:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_37;
                    break;
            }

            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            id_kakudo_kei_b = id_kakudo_kei_b + 1;//if(id_kakudo_kei_b<2){id_kakudo_kei_b=2;}
            Button_kakudo_kei_b.setText("180/" + id_kakudo_kei_b + "=" + (double) (Math.round((180.0 / ((double) id_kakudo_kei_b)) * 1000)) / 1000.0);

            es1.set_id_angle_system(id_kakudo_kei_b);
            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle7.add(Button_kakudo_kei_b_tiisaku);
        Button_kakudo_kei_b_tiisaku.setMargin(new Insets(0, 0, 0, 0));
        Button_kakudo_kei_b_tiisaku.setIcon(createImageIcon(
                "ppp/tiisaku.png"));
        Button_kakudo_kei_b_tiisaku.setBounds(2, 2, 10, 20);


        //Button_id_kakudo_kei_18.setHorizontalAlignment(JButton.LEFT);
// 東******************************************************************************


        //-------------------------------------------------------------
        Button_kakudo_kei_b = new JButton("180/" + id_kakudo_kei_b + "=" + (double) (Math.round((180.0 / ((double) id_kakudo_kei_b)) * 1000)) / 1000.0);
        Button_kakudo_kei_b.addActionListener(e -> {
            img_explanation_fname = "qqq/kakudo_kei_b.png";
            updateExplanation();

            switch (angle_system_input_id) {
                case DEG_1:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_13;
                    break;
                case DEG_2:
                    i_mouse_modeA = MouseMode.ANGLE_SYSTEM_16;
                    break;
                case DEG_3:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_2_17;
                    break;
                case DEG_4:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_18;
                    break;
                case DEG_5:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_37;
                    break;
            }

            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            Button_kakudo_kei_b.setText("180/" + id_kakudo_kei_b + "=" + (double) (Math.round((180.0 / ((double) id_kakudo_kei_b)) * 1000)) / 1000.0);

            es1.set_id_angle_system(id_kakudo_kei_b);
            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle7.add(Button_kakudo_kei_b);
//ButtonCol_red.setBackground(new Color(150,150,150));
        Button_kakudo_kei_b.setMargin(new Insets(0, 0, 0, 0));
//ButtonCol_red.setImage("board.png");

        Button_kakudo_kei_b.setBounds(10, 2, 90, 20);


// 東******************************************************************************


        JButton Button_kakudo_kei_b_increase = new JButton("");
        Button_kakudo_kei_b_increase.addActionListener(e -> {
            img_explanation_fname = "qqq/kakudo_kei_b_ookiku.png";
            updateExplanation();

            switch (angle_system_input_id) {
                case DEG_1:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_13;
                    break;
                case DEG_2:
                    i_mouse_modeA = MouseMode.ANGLE_SYSTEM_16;
                    break;
                case DEG_3:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_2_17;
                    break;
                case DEG_4:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_18;
                    break;
                case DEG_5:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_37;
                    break;
            }

            id_kakudo_kei_b = id_kakudo_kei_b - 1;
            if (id_kakudo_kei_b < 2) {
                id_kakudo_kei_b = 2;
            }
            Button_kakudo_kei_b.setText("180/" + id_kakudo_kei_b + "=" + (double) (Math.round((180.0 / ((double) id_kakudo_kei_b)) * 1000)) / 1000.0);

            es1.set_id_angle_system(id_kakudo_kei_b);
            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle7.add(Button_kakudo_kei_b_increase);
        Button_kakudo_kei_b_increase.setMargin(new Insets(0, 0, 0, 0));
        Button_kakudo_kei_b_increase.setIcon(createImageIcon("ppp/ookiku.png"));

        Button_kakudo_kei_b_increase.setBounds(100, 2, 10, 20);

//東******************************************************************************


// ******************************************************************************
        JButton Button_id_kakudo_kei_12 = new JButton("180/12= 15");
        Button_id_kakudo_kei_12.addActionListener(e -> {
            img_explanation_fname = "qqq/id_kakudo_kei_12.png";
            updateExplanation();

            switch (angle_system_input_id) {
                case DEG_1:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_13;
                    break;
                case DEG_2:
                    i_mouse_modeA = MouseMode.ANGLE_SYSTEM_16;
                    break;
                case DEG_3:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_2_17;
                    break;
                case DEG_4:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_18;
                    break;
                case DEG_5:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_37;
                    break;
            }

            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.set_id_angle_system(12);
            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        //      pnle.add(Button_id_kakudo_kei_12);


// 東******************************************************************************


// *******東***********************************************************************

        JButton Button_id_kakudo_kei_08 = new JButton("180/8= 22.5");
        Button_id_kakudo_kei_08.addActionListener(e -> {
            img_explanation_fname = "qqq/id_kakudo_kei_08.png";
            updateExplanation();

            switch (angle_system_input_id) {
                case DEG_1:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_13;
                    break;
                case DEG_2:
                    i_mouse_modeA = MouseMode.ANGLE_SYSTEM_16;
                    break;
                case DEG_3:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_2_17;
                    break;
                case DEG_4:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_18;
                    break;
                case DEG_5:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_37;
                    break;
            }

            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.set_id_angle_system(8);
            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        // pnle.add(Button_id_kakudo_kei_08);


// *******東***********************************************************************

        JPanel pnle18 = new JPanel();
        //pnlw33.setPreferredSize(new Dimension(93, 23));
        pnle18.setBounds(2, 2, 102, 21);
//         pnle18.setBackground(Color.pink);
        //pnln12.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnle18.setLayout(null);
        //pnle18.setBorder(new LineBorder(Color.black, 1));

        pnle.add(pnle18);
        //------------------------------------------------

        text12 = new JTextField("", 2);
        text12.setBounds(2, 2, 33, 19);
        text12.setHorizontalAlignment(JTextField.RIGHT);
        pnle18.add(text12);

        text13 = new JTextField("", 2);
        text13.setBounds(35, 2, 33, 19);
        text13.setHorizontalAlignment(JTextField.RIGHT);
        pnle18.add(text13);

        text14 = new JTextField("", 2);
        text14.setBounds(68, 2, 33, 19);
        text14.setHorizontalAlignment(JTextField.RIGHT);
        pnle18.add(text14);


// -----自由角set
        JButton Button_restricted_angle_set_a = new JButton("");
        Button_restricted_angle_set_a.addActionListener(e -> {

            set_restricted_angle_abc();
            img_explanation_fname = "qqq/jiyuu_kaku_set_a.png";
            updateExplanation();

            switch (angle_system_input_id) {
                case DEG_1:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_13;
                    break;
                case DEG_2:
                    i_mouse_modeA = MouseMode.ANGLE_SYSTEM_16;
                    break;
                case DEG_3:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_2_17;
                    break;
                case DEG_4:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_18;
                    break;
                case DEG_5:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_37;
                    break;
            }

            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.set_id_angle_system(0);
            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        Button_restricted_angle_set_a.setBounds(101, 2, 10, 19);
        pnle18.add(Button_restricted_angle_set_a);

        Button_restricted_angle_set_a.setMargin(new Insets(0, 0, 0, 0));
        Button_restricted_angle_set_a.setIcon(createImageIcon("ppp/jiyuu_kaku_set_a.png"));

// -----自由角set。ここまで


// *******東***********************************************************************

        JPanel pnle19 = new JPanel();
        pnle19.setBounds(2, 2, 102, 21);
//         pnle19.setBackground(Color.pink);
        pnle19.setLayout(null);
        //pnle19.setBorder(new LineBorder(Color.black, 1));

        pnle.add(pnle19);
        //------------------------------------------------

        text15 = new JTextField("", 2);
        text15.setBounds(2, 2, 33, 19);
        text15.setHorizontalAlignment(JTextField.RIGHT);
        pnle19.add(text15);

        text16 = new JTextField("", 2);
        text16.setBounds(35, 2, 33, 19);
        text16.setHorizontalAlignment(JTextField.RIGHT);
        pnle19.add(text16);

        text17 = new JTextField("", 2);
        text17.setBounds(68, 2, 33, 19);
        text17.setHorizontalAlignment(JTextField.RIGHT);
        pnle19.add(text17);


// -----自由角set
        JButton Button_restricted_angle_set_b = new JButton("");
        Button_restricted_angle_set_b.addActionListener(e -> {
            set_restricted_angle_def();
            img_explanation_fname = "qqq/jiyuu_kaku_set_b.png";
            updateExplanation();

            switch (angle_system_input_id) {
                case DEG_1:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_13;
                    break;
                case DEG_2:
                    i_mouse_modeA = MouseMode.ANGLE_SYSTEM_16;
                    break;
                case DEG_3:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_2_17;
                    break;
                case DEG_4:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_18;
                    break;
                case DEG_5:
                    i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_37;
                    break;
            }

            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.set_id_angle_system(0);
            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        Button_restricted_angle_set_b.setBounds(101, 2, 10, 19);
        pnle19.add(Button_restricted_angle_set_b);

        Button_restricted_angle_set_b.setMargin(new Insets(0, 0, 0, 0));
        Button_restricted_angle_set_b.setIcon(createImageIcon(
                "ppp/jiyuu_kaku_set_b.png"));

// -----自由角set。ここまで


// *******東***********************************************************************


        //Button_id_kakudo_kei_17.setHorizontalAlignment(JButton.LEFT);


        //------------------------------------------------
        JPanel pnle1 = new JPanel();
        pnle1.setLayout(new GridLayout(1, 3));

        //------------------------------------------------

        //------------------------------------------------
        JPanel pnle2 = new JPanel();
        pnle2.setLayout(new GridLayout(1, 3));

        //------------------------------------------------
        pnle.add(pnle2);


// -------------13;角度系モード。
        JButton Button_deg = new JButton("");//new JButton(	"kakudokei"	);
        Button_deg.addActionListener(e -> {
            img_explanation_fname = "qqq/deg.png";
            updateExplanation();

            angle_system_input_id = AngleSystemInputType.DEG_1;
            i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_13;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);


            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle2.add(Button_deg);

        Button_deg.setMargin(new Insets(0, 0, 0, 0));
        Button_deg.setIcon(createImageIcon(
                "ppp/deg.png"));
// -------------13;角度系モード。ここまで


// -----東--------17;角度系モード。//2点指定
        JButton Button_deg3 = new JButton("");//new JButton(	"kakudokei_3"	);
        Button_deg3.addActionListener(e -> {
            img_explanation_fname = "qqq/deg3.png";
            updateExplanation();

            angle_system_input_id = AngleSystemInputType.DEG_3;
            i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_2_17;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);


            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle2.add(Button_deg3);

        Button_deg3.setMargin(new Insets(0, 0, 0, 0));
        Button_deg3.setIcon(createImageIcon(
                "ppp/deg3.png"));
// ------東-------17;角度系モード。ここまで

// -------------37;角度規格化線分入力モード。
        JButton Button_senbun_nyuryoku37 = new JButton("");
        Button_senbun_nyuryoku37.addActionListener(e -> {
            //Button	Button_senbun_nyuryoku37	= new Button(	"L_draw11"	);Button_senbun_nyuryoku11.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e) {
            img_explanation_fname = "qqq/senbun_nyuryoku37.png";
            updateExplanation();
            angle_system_input_id = AngleSystemInputType.DEG_5;
            i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_37;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();

//System.out.println("AAAAA_1a");
        });

        pnle2.add(Button_senbun_nyuryoku37);

        Button_senbun_nyuryoku37.setMargin(new Insets(0, 0, 0, 0));
        Button_senbun_nyuryoku37.setIcon(createImageIcon(
                "ppp/senbun_nyuryoku37.png"));


// -------------37;角度規格化線分入力モード。ここまで

        //------------------------------------------------
        JPanel pnle3 = new JPanel();
        pnle3.setLayout(new GridLayout(1, 2));

        //------------------------------------------------
        pnle.add(pnle3);
// ----東---------16;Angle system mode.
        JButton Button_deg2 = new JButton("");
        Button_deg2.addActionListener(e -> {

            img_explanation_fname = "qqq/deg2.png";
            updateExplanation();
            angle_system_input_id = AngleSystemInputType.DEG_2;
            i_mouse_modeA = MouseMode.ANGLE_SYSTEM_16;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle3.add(Button_deg2);

        Button_deg2.setMargin(new Insets(0, 0, 0, 0));
        Button_deg2.setIcon(createImageIcon("ppp/deg2.png"));
// -------------16;角度系モード。ここまで

// ----東---------18;角度系モード。2点指定、自由末端
        JButton Button_deg4 = new JButton("");
        Button_deg4.addActionListener(e -> {

            img_explanation_fname = "qqq/deg4.png";
            updateExplanation();
            angle_system_input_id = AngleSystemInputType.DEG_4;
            i_mouse_modeA = MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_18;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle3.add(Button_deg4);

        Button_deg4.setMargin(new Insets(0, 0, 0, 0));
        Button_deg4.setIcon(createImageIcon("ppp/deg4.png"));
// -------------18;角度系モード。ここまで


//東********東**********************************************************************


        //------------------------------------------------
        JPanel pnle5 = new JPanel();
        pnle5.setLayout(new GridLayout(1, 3));

        pnle.add(pnle5);
        //------------------------------------------------

// ******東************************************************************************
// -----1;sei 正多角形入力モード。角数入力
        text9 = new JTextField("", 2);
        text9.setHorizontalAlignment(JTextField.RIGHT);
        pnle5.add(text9);


// -------------------------------------------------------------------------------

        //Button	Button_lineSegment_division_set
// -----1;	角数set
        JButton Button_kakusuu_set = new JButton("Set");
        Button_kakusuu_set.addActionListener(e -> {

            int numPolygonCornersOld = numPolygonCorners;
            numPolygonCorners = StringOp.String2int(text9.getText(), numPolygonCornersOld);
            if (numPolygonCorners < 3) {
                numPolygonCorners = 3;
            }
            if (numPolygonCorners > 100) {
                numPolygonCorners = 100;
            }
            text9.setText(String.valueOf(numPolygonCorners));
            es1.setNumPolygonCorners(numPolygonCorners);

            img_explanation_fname = "qqq/kakusuu_set.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.POLYGON_SET_NO_CORNERS_29;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle5.add(Button_kakusuu_set);

        Button_kakusuu_set.setMargin(new Insets(0, 0, 0, 0));
        //Button_lineSegment_division_set.setIcon(createImageIcon(
        //"ppp/senbun_bunkatu_set.png")));

// ------1;角数set。ここまで


// ------------------------qqqqqqqq-------------------------------------------------------

// -----29;正多角形入力モード。
        JButton Button_sei_takakukei = new JButton("");
        Button_sei_takakukei.addActionListener(e -> {

            int i_sei_takakukei_old = numPolygonCorners;
            numPolygonCorners = StringOp.String2int(text9.getText(), i_sei_takakukei_old);
            if (numPolygonCorners < 3) {
                numPolygonCorners = 3;
            }
            text9.setText(String.valueOf(numPolygonCorners));
            es1.setNumPolygonCorners(numPolygonCorners);

            img_explanation_fname = "qqq/sei_takakukei.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.POLYGON_SET_NO_CORNERS_29;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);
            iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.POLYGON_SET_NO_CORNERS_29;
            Button_kyoutuu_sagyou();
            canvas.repaint();
            es1.unselect_all();
        });
        pnle5.add(Button_sei_takakukei);

        Button_sei_takakukei.setMargin(new Insets(0, 0, 0, 0));
        Button_sei_takakukei.setIcon(createImageIcon("ppp/sei_takakukei.png"));

// ------29;正多角形入力モード。ここまで

// ********東******************************

        //------------------------------------------------
        JPanel pnle31 = new JPanel();
//         pnle31.setBackground(Color.PINK);
        pnle31.setLayout(new GridLayout(1, 2));
        pnle.add(pnle31);
        //------------------------------------------------


// *************** 円　折線とは相互作用する補助線として働く *************************************

// ********東******************************
        //------------------------------------------------
        JPanel pnle9 = new JPanel();
//         pnle9.setBackground(Color.PINK);
        pnle9.setLayout(new GridLayout(1, 2));
        //------------------------------------------------
        pnle.add(pnle9);

// -------------47;円入力モード。(フリー)
        JButton Button_en_nyuryoku_free = new JButton("");
        Button_en_nyuryoku_free.addActionListener(e -> {
            img_explanation_fname = "qqq/en_nyuryoku_free.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.CIRCLE_DRAW_FREE_47;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });

        pnle9.add(Button_en_nyuryoku_free);

        Button_en_nyuryoku_free.setMargin(new Insets(0, 0, 0, 0));
        Button_en_nyuryoku_free.setIcon(createImageIcon(
                "ppp/en_nyuryoku_free.png"));


// -------------47;円入力モード。ここまで


// -------------42;円入力モード。
        JButton Button_en_nyuryoku = new JButton("");
        Button_en_nyuryoku.addActionListener(e -> {
            img_explanation_fname = "qqq/en_nyuryoku.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.CIRCLE_DRAW_42;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });

        pnle9.add(Button_en_nyuryoku);

        Button_en_nyuryoku.setMargin(new Insets(0, 0, 0, 0));
        Button_en_nyuryoku.setIcon(createImageIcon(
                "ppp/en_nyuryoku.png"));


// -------------42;円入力モード。ここまで

// -------------44;Circle separate input mode. (Specify the center and radius apart)
        JButton Button_en_bunri_nyuryoku = new JButton("");
        Button_en_bunri_nyuryoku.addActionListener(e -> {
            img_explanation_fname = "qqq/en_bunri_nyuryoku.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.CIRCLE_DRAW_SEPARATE_44;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });

        pnle9.add(Button_en_bunri_nyuryoku);

        Button_en_bunri_nyuryoku.setMargin(new Insets(0, 0, 0, 0));
        Button_en_bunri_nyuryoku.setIcon(createImageIcon(
                "ppp/en_bunri_nyuryoku.png"));


// -------------44;円　分離入力モード。ここまで

        //------------------------------------------------
        JPanel pnle16 = new JPanel();
//         pnle16.setBackground(Color.PINK);
        pnle16.setLayout(new GridLayout(1, 3));

        pnle.add(pnle16);
        //------------------------------------------------


// -------------48;円　同心円追加モード。(元円の円周と同心円の円周との幅は線分で指定する)
        JButton Button_dousin_en_tuika_s = new JButton("");
        Button_dousin_en_tuika_s.addActionListener(e -> {
            img_explanation_fname = "qqq/dousin_en_tuika_s.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.CIRCLE_DRAW_CONCENTRIC_48;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });

        pnle16.add(Button_dousin_en_tuika_s);

        Button_dousin_en_tuika_s.setMargin(new Insets(0, 0, 0, 0));
        Button_dousin_en_tuika_s.setIcon(createImageIcon(
                "ppp/dousin_en_tuika_s.png"));

// -------------48;円　同心円追加モード。ここまで

// -------------49;円　同心円追加モード。(元円の円周と同心円の円周との幅は他の同心円の組で指定する)
        JButton Button_dousin_en_tuika_d = new JButton("");
        Button_dousin_en_tuika_d.addActionListener(e -> {
            img_explanation_fname = "qqq/dousin_en_tuika_d.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.CIRCLE_DRAW_CONCENTRIC_SELECT_49;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });

        pnle16.add(Button_dousin_en_tuika_d);

        Button_dousin_en_tuika_d.setMargin(new Insets(0, 0, 0, 0));
        Button_dousin_en_tuika_d.setIcon(createImageIcon(
                "ppp/dousin_en_tuika_d.png"));

// -------------49;円　同心円追加モード。ここまで

// ******************************************************************************


        //------------------------------------------------
        JPanel pnle17 = new JPanel();
//         pnle17.setBackground(Color.PINK);
        pnle17.setLayout(new GridLayout(1, 3));

        pnle.add(pnle17);
        //------------------------------------------------


// -------------50;Specify 2 circles and add concentric circles to each circle. Make sure that the widths of the band regions formed in each concentric pair are equal so that the added concentric circles touch each other.
        JButton Button_en_en_dousin_en = new JButton("");
        Button_en_en_dousin_en.addActionListener(e -> {
            img_explanation_fname = "qqq/en_en_dousin_en.png";
            updateExplanation();

            i_mouse_modeA = MouseMode.CIRCLE_DRAW_CONCENTRIC_TWO_CIRCLE_SELECT_50;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle17.add(Button_en_en_dousin_en);

        Button_en_en_dousin_en.setMargin(new Insets(0, 0, 0, 0));
        Button_en_en_dousin_en.setIcon(createImageIcon(
                "ppp/en_en_dousin_en.png"));

// -------------50;2円の共通接線入力モード。ここまで


// -------------45;2円の共通接線入力モード。
        JButton Button_en_en_sessen = new JButton("");
        Button_en_en_sessen.addActionListener(e -> {
            img_explanation_fname = "qqq/en_en_sessen.png";
            updateExplanation();

            i_mouse_modeA = MouseMode.CIRCLE_DRAW_TANGENT_LINE_45;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle17.add(Button_en_en_sessen);

        Button_en_en_sessen.setMargin(new Insets(0, 0, 0, 0));
        Button_en_en_sessen.setIcon(createImageIcon(
                "ppp/en_en_sessen.png"));

// -------------45;2円の共通接線入力モード。ここまで


// ********東******************************
        //------------------------------------------------
        JPanel pnle10 = new JPanel();
//         pnle10.setBackground(Color.PINK);
        pnle10.setLayout(new GridLayout(1, 3));
        //------------------------------------------------
        pnle.add(pnle10);


// ******************************************************************************
// -------------43;3点円入力モード。
        JButton Button_en_3ten_nyuryoku = new JButton("");
        Button_en_3ten_nyuryoku.addActionListener(e -> {
            img_explanation_fname = "qqq/en_3ten_nyuryoku.png";
            updateExplanation();

            i_mouse_modeA = MouseMode.CIRCLE_DRAW_THREE_POINT_43;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle10.add(Button_en_3ten_nyuryoku);

        Button_en_3ten_nyuryoku.setMargin(new Insets(0, 0, 0, 0));
        Button_en_3ten_nyuryoku.setIcon(createImageIcon(
                "ppp/en_3ten_nyuryoku.png"));

// -------------43;3点円入力モード。ここまで


// ******************************************************************************
// -------------46;Inverted input mode.
        JButton Button_hanten = new JButton("");
        Button_hanten.addActionListener(e -> {
            img_explanation_fname = "qqq/hanten.png";
            updateExplanation();

            i_mouse_modeA = MouseMode.INVERTED_INPUT_46;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle10.add(Button_hanten);

        Button_hanten.setMargin(new Insets(0, 0, 0, 0));
        Button_hanten.setIcon(createImageIcon(
                "ppp/hanten.png"));

// -------------46;反転入力モード。ここまで


        //------------------------------------------------
        JPanel pnle32 = new JPanel();
//         pnle32.setBackground(Color.PINK);
        pnle32.setLayout(new GridLayout(1, 2));
        pnle.add(pnle32);
        //------------------------------------------------


        //------------------------------------------------
        JPanel pnle8 = new JPanel();
        pnle8.setBackground(Color.white);
        pnle8.setLayout(new GridLayout(1, 2));
        //------------------------------------------------
        pnle.add(pnle8);

// ---------補助線や円の色を特注で変えるさいの指定色の指定
        Button_sen_tokutyuu_color = new JButton("C_col ");
        Button_sen_tokutyuu_color.addActionListener(e -> {
            img_explanation_fname = "qqq/sen_tokutyuu_color.png";
            updateExplanation();
            Button_kyoutuu_sagyou();
            i_mouseDragged_valid = false;
            i_mouseReleased_valid = false;
            //以下にやりたいことを書く

            Color color = JColorChooser.showDialog(null, "color", new Color(100, 200, 200));
            if (color != null) {
                sen_tokutyuu_color = color;
            }


            //以上でやりたいことは書き終わり
            Button_sen_tokutyuu_color.setBackground(sen_tokutyuu_color);    //ボタンの色設定

            i_mouse_modeA = MouseMode.CIRCLE_CHANGE_COLOR_59;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);
            es1.set_sen_tokutyuu_color(sen_tokutyuu_color);

            canvas.repaint();
        });
        Button_sen_tokutyuu_color.setMargin(new Insets(0, 0, 0, 0));
        pnle8.add(Button_sen_tokutyuu_color);

        //重要注意　読み込みや書き出しでファイルダイアログのボックスが開くと、それをフレームに重なる位置で操作した場合、ファイルボックスが消えたときに、
        //マウスのドラッグとリリースが発生する。このため、余計な操作がされてしまう可能性がある。なお、このときマウスクリックは発生しない。
        // i_mouseDragged_valid=0;や i_mouseReleased_valid=0;は、この余計な操作を防ぐために指定している。
// --------------------------------------

// -------------;線分色特注変更(補助活線のみ)
        JButton Button_sen_tokutyuu_color_henkou = new JButton("");
        Button_sen_tokutyuu_color_henkou.addActionListener(e -> {

            img_explanation_fname = "qqq/sen_tokutyuu_color_henkou.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.CIRCLE_CHANGE_COLOR_59;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);
            es1.set_sen_tokutyuu_color(sen_tokutyuu_color);
            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle8.add(Button_sen_tokutyuu_color_henkou);

        Button_sen_tokutyuu_color_henkou.setMargin(new Insets(0, 0, 0, 0));
        Button_sen_tokutyuu_color_henkou.setIcon(createImageIcon(
                "ppp/sen_tokutyuu_color_henkou.png"));

// ********東******************************
        //------------------------------------------------
        JPanel pnle15 = new JPanel();
        pnle15.setLayout(new GridLayout(1, 2));
        //------------------------------------------------


// *************** 補助線　折線とは相互作用せず、ただの絵として振舞う *************************************


// ****東**************************************************************************

        //------------------------------------------------
        JPanel pnle12 = new JPanel();
        pnle12.setLayout(new GridLayout(1, 3));

        pnle.add(pnle12);
        //------------------------------------------------

// *****東*************************************************************************

        JButton Button_h_undo = new JButton("");
        Button_h_undo.addActionListener(e -> {
            img_explanation_fname = "qqq/undo.png";
            updateExplanation();

            es1.h_undo();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle12.add(Button_h_undo);
        Button_h_undo.setMargin(new Insets(0, 0, 0, 0));
        Button_h_undo.setIcon(createImageIcon("ppp/h_undo.png"));


// *****東*************************************************************************


        text11 = new JTextField("", 1);
        text11.setHorizontalAlignment(JTextField.RIGHT);

        pnle12.add(text11);
// *****東*************************************************************************

        JButton Button_h_undo_syutoku = new JButton("S");
        Button_h_undo_syutoku.addActionListener(e -> {


            img_explanation_fname = "qqq/h_undo_syutoku.png";
            updateExplanation();
            int i_h_undo_suu_old = i_undo_suu;
            i_h_undo_suu = StringOp.String2int(text11.getText(), i_h_undo_suu_old);
            if (i_h_undo_suu < 0) {
                i_h_undo_suu = 0;
            }
            text11.setText(String.valueOf(i_h_undo_suu));
            es1.set_h_Ubox_undo_suu(i_h_undo_suu);


        });
        pnle12.add(Button_h_undo_syutoku);

        Button_h_undo_syutoku.setMargin(new Insets(0, 0, 0, 0));


// *****東*************************************************************************


        JButton Button_h_redo = new JButton("");
        Button_h_redo.addActionListener(e -> {


            img_explanation_fname = "qqq/h_redo.png";
            updateExplanation();

            es1.h_redo();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle12.add(Button_h_redo);
        Button_h_redo.setMargin(new Insets(0, 0, 0, 0));
        Button_h_redo.setIcon(createImageIcon("ppp/h_redo.png"));


// ********************************************************

// ********東******************************
        //------------------------------------------------
        JPanel pnle11 = new JPanel();
        pnle11.setLayout(new GridLayout(1, 2));
        //------------------------------------------------
        pnle.add(pnle11);


// ********東******************************
        //------------------------------------------------
        JPanel pnle14 = new JPanel();
        pnle14.setLayout(new GridLayout(1, 2));
        //------------------------------------------------
        pnle11.add(pnle14);

// ****東**************************************************************************補助線の幅小さく
        JButton Button_h_lineWidthDecrease = new JButton("");
        Button_h_lineWidthDecrease.addActionListener(e -> {
            i_h_lineWidth = i_h_lineWidth - 2;
            if (i_h_lineWidth < 3) {
                i_h_lineWidth = 3;
            }

            img_explanation_fname = "qqq/h_senhaba_sage.png";
            updateExplanation();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle14.add(Button_h_lineWidthDecrease);

        Button_h_lineWidthDecrease.setMargin(new Insets(0, 0, 0, 0));
        Button_h_lineWidthDecrease.setIcon(createImageIcon(
                "ppp/h_senhaba_sage.png"));

// ****東**************************************************************************補助線の幅大きく

        JButton Button_h_lineWidthIncrease = new JButton("");
        Button_h_lineWidthIncrease.addActionListener(e -> {
            i_h_lineWidth = i_h_lineWidth + 2;
            img_explanation_fname = "qqq/h_senhaba_age.png";
            updateExplanation();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle14.add(Button_h_lineWidthIncrease);

        Button_h_lineWidthIncrease.setMargin(new Insets(0, 0, 0, 0));
        Button_h_lineWidthIncrease.setIcon(createImageIcon(
                "ppp/h_senhaba_age.png"));
//icol=3 cyan
//icol=4 orange
//icol=5 mazenta
//icol=6 green
//icol=7 yellow
// ******東************************************************************************
        Button_Col_orange = new JButton("a1");
        Button_Col_orange.addActionListener(e -> {
            img_explanation_fname = "qqq/Button_Col_orange.png";
            updateExplanation();
            Button_h_Col_reset();
            Button_Col_orange.setBackground(Color.ORANGE);
            h_icol = LineColor.ORANGE_4;
            es1.h_setcolor(h_icol);
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle11.add(Button_Col_orange);
        Button_Col_orange.setBackground(new Color(150, 150, 150));
        Button_Col_orange.setMargin(new Insets(0, 0, 0, 0));
// ******東************************************************************************
        Button_Col_yellow = new JButton("a2");
        Button_Col_yellow.addActionListener(e -> {
            img_explanation_fname = "qqq/Button_Col_yellow.png";
            updateExplanation();
            Button_h_Col_reset();
            Button_Col_yellow.setBackground(Color.yellow);
            h_icol = LineColor.YELLOW_7;
            es1.h_setcolor(h_icol);
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle11.add(Button_Col_yellow);
        Button_Col_yellow.setBackground(new Color(150, 150, 150));
        Button_Col_yellow.setMargin(new Insets(0, 0, 0, 0));
// ******東************************************************************************

        //------------------------------------------------
        JPanel pnle13 = new JPanel();
//         pnle13.setBackground(Color.PINK);
        pnle13.setLayout(new GridLayout(1, 2));
        //------------------------------------------------
        pnle.add(pnle13);

// -------------h_1;補助線入力モード。
        JButton Button_h_senbun_nyuryoku = new JButton("");
        Button_h_senbun_nyuryoku.addActionListener(e -> {
            img_explanation_fname = "qqq/h_senbun_nyuryoku.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.DRAW_CREASE_FREE_1;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
            foldLineAdditionalInputMode = Drawing_Worker.FoldLineAdditionalInputMode.AUX_LINE_1;//=0は折線入力　=1は補助線入力モード
            es1.setFoldLineAdditional(foldLineAdditionalInputMode);
        });

        pnle13.add(Button_h_senbun_nyuryoku);

        Button_h_senbun_nyuryoku.setMargin(new Insets(0, 0, 0, 0));
        Button_h_senbun_nyuryoku.setIcon(createImageIcon("ppp/h_senbun_nyuryoku.png"));


// -------------h_1;補助線入力モード。ここまで

// ******東************************************************************************ 消しゴム
// -------------;h_線分削除モード。

        JButton Button_h_senbun_sakujyo = new JButton("");
        Button_h_senbun_sakujyo.addActionListener(e -> {
            img_explanation_fname = "qqq/h_senbun_sakujyo.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.LINE_SEGMENT_DELETE_3;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);


            foldLineAdditionalInputMode = Drawing_Worker.FoldLineAdditionalInputMode.AUX_LINE_1;//= 0 is polygonal line input = 1 is auxiliary line input mode
            es1.setFoldLineAdditional(foldLineAdditionalInputMode);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnle13.add(Button_h_senbun_sakujyo);

        Button_h_senbun_sakujyo.setMargin(new Insets(0, 0, 0, 0));
        Button_h_senbun_sakujyo.setIcon(createImageIcon(
                "ppp/h_senbun_sakujyo.png"));

// ******東************************************************************************
        //------------------------------------------------
        JPanel pnle30 = new JPanel();
        pnle30.setLayout(new GridLayout(1, 2));
        pnle.add(pnle30);
        //------------------------------------------------

// ******東************************************************************************
// ---------------------------------------------------------------------------------------------------------------
        EtchedBorder border = new EtchedBorder(EtchedBorder.RAISED, Color.white, Color.black);

        //------------------------------------------------
        JPanel pnle24 = new JPanel();
        pnle24.setBounds(2, 2, 93, 20);
        pnle24.setLayout(null);
        //------------------------------------------------
        pnle.add(pnle24);

// -------------長さ1測定モード。
        JButton Button_length_sokutei_1 = new JButton("L1=");
        Button_length_sokutei_1.addActionListener(e -> {
            img_explanation_fname = "qqq/nagasa_sokutei_1.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_1_53;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        Button_length_sokutei_1.setBounds(2, 2, 30, 20);
        pnle24.add(Button_length_sokutei_1);
        Button_length_sokutei_1.setBorder(border);
        Button_length_sokutei_1.setMargin(new Insets(0, 0, 0, 0));
// -------------長さ1測定モード。ここまで
        label_length_sokutei_1.setOpaque(true);
        label_length_sokutei_1.setBackground(Color.white);
        label_length_sokutei_1.setText("");
        label_length_sokutei_1.setBorder(border);
        label_length_sokutei_1.setBounds(30, 2, 80, 20);
        pnle24.add(label_length_sokutei_1);
// -------------

        //------------------------------------------------
        JPanel pnle25 = new JPanel();
        pnle25.setBounds(2, 2, 93, 20);
        pnle25.setLayout(null);
        //------------------------------------------------
        pnle.add(pnle25);
// -------------長さ2測定モード。
        JButton Button_length_sokutei_2 = new JButton("L2=");
        Button_length_sokutei_2.addActionListener(e -> {
            img_explanation_fname = "qqq/nagasa_sokutei_2.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_2_54;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        Button_length_sokutei_2.setBounds(2, 2, 30, 20);
        pnle25.add(Button_length_sokutei_2);
        Button_length_sokutei_2.setBorder(border);
        Button_length_sokutei_2.setMargin(new Insets(0, 0, 0, 0));
// -------------長さ2測定モード。ここまで
        label_length_sokutei_2.setOpaque(true);
        label_length_sokutei_2.setBackground(Color.white);
        label_length_sokutei_2.setText("");
        label_length_sokutei_2.setBorder(border);
        label_length_sokutei_2.setBounds(30, 2, 80, 20);
        pnle25.add(label_length_sokutei_2);
// -------------


        //------------------------------------------------
        JPanel pnle26 = new JPanel();
        pnle26.setBounds(2, 2, 93, 20);
        pnle26.setLayout(null);
        //------------------------------------------------
        pnle.add(pnle26);
// -------------角度1測定モード。
        JButton Button_kakudo_sokutei_1 = new JButton("A1=");
        Button_kakudo_sokutei_1.addActionListener(e -> {
            img_explanation_fname = "qqq/kakudo_sokutei_1.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_1_55;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        Button_kakudo_sokutei_1.setBounds(2, 2, 30, 20);
        pnle26.add(Button_kakudo_sokutei_1);
        Button_kakudo_sokutei_1.setBorder(border);
        Button_kakudo_sokutei_1.setMargin(new Insets(0, 0, 0, 0));
// -------------角度1測定モード。ここまで
        label_kakudo_sokutei_1.setOpaque(true);
        label_kakudo_sokutei_1.setBackground(Color.white);
        label_kakudo_sokutei_1.setText("");
        label_kakudo_sokutei_1.setBorder(border);
        label_kakudo_sokutei_1.setBounds(30, 2, 80, 20);
        pnle26.add(label_kakudo_sokutei_1);


        //------------------------------------------------
        JPanel pnle27 = new JPanel();
        pnle27.setBounds(2, 2, 93, 20);
//         pnle27.setBackground(Color.PINK);
        pnle27.setLayout(null);
        //------------------------------------------------
        pnle.add(pnle27);
// -------------角度2測定モード。
        JButton Button_kakudo_sokutei_2 = new JButton("A2=");
        Button_kakudo_sokutei_2.addActionListener(e -> {
            img_explanation_fname = "qqq/kakudo_sokutei_2.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_2_56;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        Button_kakudo_sokutei_2.setBounds(2, 2, 30, 20);
        pnle27.add(Button_kakudo_sokutei_2);
        Button_kakudo_sokutei_2.setBorder(border);
        Button_kakudo_sokutei_2.setMargin(new Insets(0, 0, 0, 0));
// -------------角度2測定モード。ここまで
        label_kakudo_sokutei_2.setOpaque(true);
        label_kakudo_sokutei_2.setBackground(Color.white);
        label_kakudo_sokutei_2.setText("");
        label_kakudo_sokutei_2.setBorder(border);
        label_kakudo_sokutei_2.setBounds(30, 2, 80, 20);
        pnle27.add(label_kakudo_sokutei_2);


        //------------------------------------------------
        JPanel pnle28 = new JPanel();
        pnle28.setBounds(2, 2, 93, 20);
        pnle28.setLayout(null);
        //------------------------------------------------
        pnle.add(pnle28);
// -------------角度3測定モード。
        JButton Button_kakudo_sokutei_3 = new JButton("A3=");
        Button_kakudo_sokutei_3.addActionListener(e -> {
            img_explanation_fname = "qqq/kakudo_sokutei_3.png";
            updateExplanation();
            i_mouse_modeA = MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_3_57;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.unselect_all();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        Button_kakudo_sokutei_3.setBounds(2, 2, 30, 20);
        pnle28.add(Button_kakudo_sokutei_3);
        Button_kakudo_sokutei_3.setBorder(border);
        Button_kakudo_sokutei_3.setMargin(new Insets(0, 0, 0, 0));
// -------------角度3測定モード。ここまで
        label_kakudo_sokutei_3.setOpaque(true);
        label_kakudo_sokutei_3.setBackground(Color.white);
        label_kakudo_sokutei_3.setText("");
        label_kakudo_sokutei_3.setBorder(border);
        label_kakudo_sokutei_3.setBounds(30, 2, 80, 20);
        pnle28.add(label_kakudo_sokutei_3);


// ******************************************************************************************************************************************************************************

        //------------------------------------------------
        JPanel pnle33 = new JPanel();
        pnle33.setLayout(new GridLayout(1, 2));
        pnle.add(pnle33);
        //------------------------------------------------


// -------------追加フレーム表示
        JButton Button_tuika_kinou = new JButton("ad_fnc");//追加機能を英語で訳すと additional function
        Button_tuika_kinou.addActionListener(e -> {
            img_explanation_fname = "qqq/tuika_kinou.png";
            updateExplanation();

            Frame_tuika();
            add_frame.toFront();
        });
        pnle33.add(Button_tuika_kinou);

        Button_tuika_kinou.setMargin(new Insets(0, 0, 0, 0));


// -------------追加フレーム表示。ここまで


// ***東***************************************************************************************************************************************************************************


        // *************************************************
        //下辺（南側）パネルの構築*************************
        // *************************************************
        //下辺（南側）パネルの作成
        JPanel pnls = new JPanel();
        pnls.setLayout(new WrapLayout(WrapLayout.LEFT));
        //下辺（南側）パネルをレイアウトに貼り付け
        contentPane.add("South", pnls); //Frame用


        //------------------------------------------------
        JPanel pnlw11 = new JPanel();
        pnlw11.setLayout(new GridLayout(1, 3));

        //------------------------------------------------
        pnlw.add(pnlw11);


        //------------------------------------------------
        JPanel pnlw12 = new JPanel();
        pnlw12.setPreferredSize(new Dimension(76, 30));
        pnlw12.setLayout(null);
        pnlw.add(pnlw12);
        //------------------------------------------------

// ***西***************************************************************************データ読み込み追加

        JButton Button_yomi_tuika = new JButton("Op");
        Button_yomi_tuika.addActionListener(e -> {

            img_explanation_fname = "qqq/yomi_tuika.png";
            updateExplanation();

            Button_kyoutuu_sagyou();

            i_mouseDragged_valid = false;
            i_mouseReleased_valid = false;
            Memo memo_temp;

            System.out.println("readFile2Memo() 開始");
            memo_temp = readFile2Memo();
            System.out.println("readFile2Memo() 終了");

            if (memo_temp.getLineCount() > 0) {
                es1.setMemo_for_reading_tuika(memo_temp);
                es1.record();
                canvas.repaint();
            }
        });
        Button_yomi_tuika.setBounds(0, 0, 30, 21);
        Button_yomi_tuika.setMargin(new Insets(0, 0, 0, 0));
        pnlw12.add(Button_yomi_tuika);


// -------------------------------------------------------------------
//cpを折畳み前に自動改善する
        ckbox_cp_kaizen_folding = new JCheckBox("");
        ckbox_cp_kaizen_folding.addActionListener(e -> {
            img_explanation_fname =
                    "qqq/ckbox_cp_kaizen_oritatami.png";
            updateExplanation();

            canvas.repaint();
        });
        ckbox_cp_kaizen_folding.setIcon(createImageIcon(
                "ppp/ckbox_cp_kaizen_oritatami_off.png"));
        ckbox_cp_kaizen_folding.setSelectedIcon(createImageIcon(
                "ppp/ckbox_cp_kaizen_oritatami_on.png"));
        ckbox_cp_kaizen_folding.setMargin(new Insets(0, 0, 0, 0));
        pnlw12.add(ckbox_cp_kaizen_folding);
        ckbox_cp_kaizen_folding.setBounds(31, 0, 20, 21);
// -------------------------------------------------------------------
//select状態を他の操作をしてもなるべく残す
        ckbox_select_nokosi = new JCheckBox("");
        ckbox_select_nokosi.addActionListener(e -> {
            img_explanation_fname =
                    "qqq/ckbox_select_nokosi.png";
            updateExplanation();

            canvas.repaint();
        });
        ckbox_select_nokosi.setIcon(createImageIcon(
                "ppp/ckbox_select_nokosi_off.png"));
        ckbox_select_nokosi.setSelectedIcon(createImageIcon(
                "ppp/ckbox_select_nokosi_on.png"));
        pnlw12.add(ckbox_select_nokosi);
        ckbox_select_nokosi.setBounds(51, 0, 30, 21);


// ***西****************************************************************** ２色展開図************************************************
        JButton Button_2syoku_tenkaizu = new JButton("");//new JButton(	"Del_F"	);
        Button_2syoku_tenkaizu.addActionListener(e -> {
            img_explanation_fname = "qqq/2syoku_tenkaizu.png";
            updateExplanation();

            Ss0 = es1.getForSelectFolding();

            if (es1.getFoldLineTotalForSelectFolding() == 0) {        //折り線選択無し
                keikoku_sentaku_sareta_orisen_ga_nai();//警告　選択された折線がない


            } else if (es1.getFoldLineTotalForSelectFolding() > 0) {
                folding_prepare();//ここでOZがOAZ(0)からOAZ(i)に切り替わる
                OZ.estimationOrder = FoldedFigure.EstimationOrder.ORDER_5;

                if (!subThreadRunning) {
                    subThreadRunning = true;
                    subThreadMode = SubThread.Mode.TWO_COLORED_4;
                    mks();//新しいスレッドを作る
                    sub.start();
                }
            }

            es1.unselect_all();
            Button_kyoutuu_sagyou();

        });
        pnlw12.add(Button_2syoku_tenkaizu);
        Button_2syoku_tenkaizu.setBounds(81, 0, 30, 21);
        Button_2syoku_tenkaizu.setMargin(new Insets(0, 0, 0, 0));
        Button_2syoku_tenkaizu.setIcon(createImageIcon("ppp/2syoku_tenkaizu.png"));


        JPanel pnlw20 = new JPanel();
        pnlw20.setLayout(new GridLayout(1, 2));
        pnlw.add(pnlw20);


        JButton Button_suitei_01 = new JButton("CP_rcg");
        Button_suitei_01.addActionListener(e -> {
            img_explanation_fname = "qqq/suitei_01.png";
            updateExplanation();

            oritatame(getFoldType(), FoldedFigure.EstimationOrder.ORDER_1);//引数の意味は(i_fold_type , i_suitei_meirei);
            if (ckbox_select_nokosi.isSelected()) {
            } else {
                es1.unselect_all();
            }

            Button_kyoutuu_sagyou();
        });
        pnlw20.add(Button_suitei_01);
        Button_suitei_01.setMargin(new Insets(0, 0, 0, 0));


        // *********南****************************************************************
        JButton Button_koteimen_sitei = new JButton("S_face");
        Button_koteimen_sitei.addActionListener(e -> {

            img_explanation_fname = "qqq/koteimen_sitei.png";
            updateExplanation();
            if (OZ.displayStyle != FoldedFigure.DisplayStyle.NONE_0) {
                i_mouse_modeA = MouseMode.CHANGE_STANDARD_FACE_103;
                System.out.println("i_mouse_modeA = " + i_mouse_modeA);
            }
            Button_kyoutuu_sagyou();
        });
        pnlw20.add(Button_koteimen_sitei);

        Button_koteimen_sitei.setMargin(new Insets(0, 0, 0, 0));

        // **********南***************************************************************


        //------------------------------------------------
        JPanel pnlw36 = new JPanel();
        pnlw36.setPreferredSize(new Dimension(76, 21));
        pnlw36.setLayout(null);
        pnlw.add(pnlw36);
        //------------------------------------------------

// *******南******************************************************************

        JButton Button_suitei_02 = new JButton("");
        Button_suitei_02.addActionListener(e -> {
            img_explanation_fname = "qqq/suitei_02.png";
            updateExplanation();

            oritatame(getFoldType(), FoldedFigure.EstimationOrder.ORDER_2);//引数の意味は(i_fold_type , i_suitei_meirei);
            if (!ckbox_select_nokosi.isSelected()) {
                es1.unselect_all();
            }

            Button_kyoutuu_sagyou();
        });
        pnlw36.add(Button_suitei_02);

        Button_suitei_02.setBounds(0, 0, 20, 21);//20180210,4番目の21が23以上だとアイコン表示がかえって部分的にしか表示されない

        Button_suitei_02.setMargin(new Insets(0, 0, 0, 0));
        Button_suitei_02.setIcon(createImageIcon(
                "ppp/suitei_02.png"));

// *******南******************************************************************


        //------------------------------------------------
        JPanel pnls4 = new JPanel();
        pnls4.setPreferredSize(new Dimension(76, 21));//pnls4.setPreferredSize(new Dimension(76, 30)
        pnls4.setBackground(Color.white);
        pnls4.setLayout(null);
        pnlw36.add(pnls4);
        pnls4.setBounds(30, 0, 76, 21);//20180210,4番目の21が23以上だとアイコン表示がかえって部分的にしか表示されない

        //------------------------------------------------

// *******南******************************************************************

        JButton Button_suitei_03 = new JButton("");//透過図表示new JButton(	"Transparent_gr"	);
        Button_suitei_03.addActionListener(e -> {
            img_explanation_fname = "qqq/suitei_03.png";
            updateExplanation();

            oritatame(getFoldType(), FoldedFigure.EstimationOrder.ORDER_3);//引数の意味は(i_fold_type , i_suitei_meirei);

            if (ckbox_select_nokosi.isSelected()) {
            } else {
                es1.unselect_all();
            }
            Button_kyoutuu_sagyou();
        });
        pnls4.add(Button_suitei_03);
        Button_suitei_03.setBounds(1, 0, 20, 21);//Button_suitei_03.setBounds(1, 1, 20, 28);


        Button_suitei_03.setMargin(new Insets(0, 0, 0, 0));
        Button_suitei_03.setIcon(createImageIcon("ppp/suitei_03.png"));


// ******南*******************************************************************ccccccccccccccc
//透過図をカラー化する。

        ckbox_toukazu_color = new JCheckBox("");
        ckbox_toukazu_color.addActionListener(e -> {
            img_explanation_fname =
                    "qqq/ckbox_toukazu_color.png";
            updateExplanation();
            if (ckbox_toukazu_color.isSelected()) {
                OZ.transparencyColor = true;
                System.out.println("ckbox_toukazu_color.isSelected()");
            }//カラーの透過図
            else {
                OZ.transparencyColor = false;
                System.out.println("ckbox_toukazu_color.is not Selected()");
            }
            canvas.repaint();
        });
        ckbox_toukazu_color.setBounds(21, 0, 18, 21);

        ckbox_toukazu_color.setIcon(createImageIcon(
                "ppp/ckbox_toukazu_color_off.png"));
        ckbox_toukazu_color.setSelectedIcon(createImageIcon(
                "ppp/ckbox_toukazu_color_on.png"));
        ckbox_toukazu_color.setMargin(new Insets(0, 0, 0, 0));
        pnls4.add(ckbox_toukazu_color);


// *******透過図の色の濃さ調整　下げ***********************************************************************

        JButton Button_toukazu_color_sage = new JButton("");
        Button_toukazu_color_sage.addActionListener(e -> {
            OZ.decreaseTransparency();
            img_explanation_fname = "qqq/toukazu_color_sage.png";
            updateExplanation();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnls4.add(Button_toukazu_color_sage);
        Button_toukazu_color_sage.setBounds(39, 0, 18, 21);
        Button_toukazu_color_sage.setMargin(new Insets(0, 0, 0, 0));
        Button_toukazu_color_sage.setIcon(createImageIcon(
                "ppp/ck4_color_sage.png"));


// *******透過図の色の濃さ調整　上げ***********************************************************************

        JButton Button_toukazu_color_age = new JButton("");
        Button_toukazu_color_age.addActionListener(e -> {
            OZ.increaseTransparency();
            img_explanation_fname = "qqq/toukazu_color_age.png";
            updateExplanation();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnls4.add(Button_toukazu_color_age);
        Button_toukazu_color_age.setBounds(57, 0, 18, 21);
        Button_toukazu_color_age.setMargin(new Insets(0, 0, 0, 0));
        Button_toukazu_color_age.setIcon(createImageIcon(
                "ppp/ck4_color_age.png"));


// ********南*****************************************************************
        JButton Button_fold = new JButton("Fold");
        Button_fold.addActionListener(e -> {
            img_explanation_fname = "qqq/suitei_04.png";
            updateExplanation();

            System.out.println("20180220 get_i_fold_type() = " + getFoldType());
            oritatame(getFoldType(), FoldedFigure.EstimationOrder.ORDER_5);//引数の意味は(i_fold_type , i_suitei_meirei);

            if (ckbox_select_nokosi.isSelected()) {
            } else {
                es1.unselect_all();
            }

            Button_kyoutuu_sagyou();

        });
        pnls.add(Button_fold);

        Button_fold.setIcon(createImageIcon("ppp/suitei_04.png"));

// *******南******************************************************************


// *****南********************************************************************
        //-------------------------------------
        Button_another_solution = new JButton("a_s");
        Button_another_solution.addActionListener(e -> {
            img_explanation_fname = "qqq/Button3.png";
            updateExplanation();

            OZ.estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;

            subThreadMode = SubThread.Mode.FOLDING_ESTIMATE_0;//1 = Put together another solution for folding estimation. 0 = It is not a mode to put out different solutions of folding estimation at once. This variable is used to change the behavior of subthreads.
            if (!subThreadRunning) {
                subThreadRunning = true;
                mks();//新しいスレッドを作る
                sub.start();
            }
        });
        pnls.add(Button_another_solution);


// *******南***************************************************** //System.out.println("裏表");*************
        //------------------------------------------------------------------------------------------------------
        Button0b = new JButton("");//new JButton(	"Back"		);
        Button0b.addActionListener(e -> {

            img_explanation_fname = "qqq/Button0b.png";
            updateExplanation();
            OZ.ip4 = OZ.ip4.advance();
            if ((i_mouse_modeA == MouseMode.MODIFY_CALCULATED_SHAPE_101) && (OZ.ip4 == FoldedFigure.State.BOTH_2)) {
                OZ.ip4 = FoldedFigure.State.FRONT_0;
            }//Fold-up forecast map Added to avoid the mode that can not be moved when moving
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnls.add(Button0b);

        Button0b.setIcon(createImageIcon("ppp/Button0b.png"));


// *****南********************************************************************
        //-------------------------------------
        Button_AS_matome = new JButton("AS100");
        Button_AS_matome.addActionListener(e -> {

            subThreadMode = SubThread.Mode.FOLDING_ESTIMATE_SAVE_100_1;
            img_explanation_fname = "qqq/AS_matome.png";
            updateExplanation();
            if (OZ.findAnotherOverlapValid) {
                //OZ.i_suitei_jissi_umu=0;//i_suitei_jissi_umuは、折り畳み推定の計算を実施したかどうかを表す。int i_suitei_jissi_umu=0なら実施しない。1なら実施した。
                OZ.estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;

                if (!subThreadRunning) {
                    subThreadRunning = true;
                    mks();//新しいスレッドを作る
                    sub.start();

                }
            }
        });
        pnls.add(Button_AS_matome);


// **********南***************************************************************


        //------------------------------------------------
        JPanel pnls1 = new JPanel();
        pnls1.setLayout(new GridLayout(1, 2));
        pnls.add(pnls1);

// -----
        text26 = new JTextField("", 2);
        text26.setHorizontalAlignment(JTextField.RIGHT);
        pnls1.add(text26);
// -------------------------------------------------------------------------------
// -----;	//折り畳み推定の指定番目を表示する
        Button_bangou_sitei_estimated_display = new JButton("Go");
        Button_bangou_sitei_estimated_display.addActionListener(e -> {

            int foldedCases_old = foldedCases;
            foldedCases = StringOp.String2int(text26.getText(), foldedCases_old);
            if (foldedCases < 1) {
                foldedCases = 1;
            }

            text26.setText(String.valueOf(foldedCases));

            OZ.estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;

            if (foldedCases < OZ.discovered_fold_cases) {
                configure_syokika_yosoku();//折り上がり予想の廃棄
                OZ.estimationOrder = FoldedFigure.EstimationOrder.ORDER_51;    //i_suitei_meirei=51はoritatami_suiteiの最初の推定図用カメラの設定は素通りするための設定。推定図用カメラの設定を素通りしたら、i_suitei_meirei=5に変更される。
                //1例目の折り上がり予想はi_suitei_meirei=5を指定、2例目以降の折り上がり予想はi_suitei_meirei=6で実施される
            }

            subThreadMode = SubThread.Mode.FOLDING_ESTIMATE_SPECIFIC_2;
            if (!subThreadRunning) {
                subThreadRunning = true;
                mks();//新しいスレッドを作る
                sub.start();
            }

            img_explanation_fname = "qqq/bangou_sitei_suitei_hyouji.png";
            updateExplanation();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnls1.add(Button_bangou_sitei_estimated_display);

// ------ここまで


//折り上がり図	のredoとundo

        JButton Button_undo_om = new JButton("");//_omは折り上がり図モディファイ（変形）の意味
        Button_undo_om.addActionListener(e -> {
            img_explanation_fname = "qqq/undo.png";
            updateExplanation();

            OZ.undo();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnls.add(Button_undo_om);
        Button_undo_om.setMargin(new Insets(0, 0, 0, 0));
        Button_undo_om.setIcon(createImageIcon(
                "ppp/undo.png"));

// *****南*************************************************************************


        text31 = new JTextField("", 1);
        text31.setHorizontalAlignment(JTextField.RIGHT);

        pnls.add(text31);
// *****南*************************************************************************
        JButton Button_undo_syutoku_om = new JButton("S");
        Button_undo_syutoku_om.addActionListener(e -> {


            img_explanation_fname = "qqq/undo_syutoku.png";
            updateExplanation();
            int i_undo_suu_om_old = i_undo_suu_om;
            i_undo_suu_om = StringOp.String2int(text31.getText(), i_undo_suu_om_old);
            if (i_undo_suu < 0) {
                i_undo_suu_om = 0;
            }
            text31.setText(String.valueOf(i_undo_suu_om));
            OZ.cp_worker2.setUndoBoxUndoTotal(i_undo_suu_om);                  //  <<<------------


        });
        pnls.add(Button_undo_syutoku_om);

        Button_undo_syutoku_om.setMargin(new Insets(0, 0, 0, 0));


// *****南*************************************************************************

        JButton Button_redo_om = new JButton("");
        Button_redo_om.addActionListener(e -> {


            img_explanation_fname = "qqq/redo.png";
            updateExplanation();

            OZ.redo();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnls.add(Button_redo_om);
        Button_redo_om.setMargin(new Insets(0, 0, 0, 0));
        Button_redo_om.setIcon(createImageIcon("ppp/redo.png"));


// ********************************************************


// ******南*******************************************************************
        JButton Button_oriagari_sousa = new JButton("");//折り上がり図操作　針金図(	"F_Modify"		)
        Button_oriagari_sousa.addActionListener(e -> {
            img_explanation_fname = "qqq/oriagari_sousa.png";
            updateExplanation();
            OZ.i_foldedFigure_operation_mode = 1;
            OZ.setAllPointState0();
            OZ.record();
            i_mouse_modeA = MouseMode.MODIFY_CALCULATED_SHAPE_101;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            Button_kyoutuu_sagyou();
        });
        pnls.add(Button_oriagari_sousa);
        Button_oriagari_sousa.setIcon(createImageIcon("ppp/oriagari_sousa.png"));

// ******南*******************************************************************

        JButton Button_oriagari_sousa_2 = new JButton("");//new JButton(	"F_Modify"		)
        Button_oriagari_sousa_2.addActionListener(e -> {
            img_explanation_fname = "qqq/oriagari_sousa_2.png";
            updateExplanation();
            OZ.i_foldedFigure_operation_mode = 2;
            OZ.setAllPointState0();
            OZ.record();
            i_mouse_modeA = MouseMode.MODIFY_CALCULATED_SHAPE_101;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);


            Button_kyoutuu_sagyou();
            //repaint();
        });
        pnls.add(Button_oriagari_sousa_2);


        Button_oriagari_sousa_2.setMargin(new Insets(0, 0, 0, 0));
        Button_oriagari_sousa_2.setIcon(createImageIcon("ppp/oriagari_sousa_2.png"));

// *******南******************************************************************
        JButton Button_oriagari_idiu = new JButton("");// new JButton(	"F_move"	);
        Button_oriagari_idiu.addActionListener(e -> {
            img_explanation_fname = "qqq/oriagari_idiu.png";
            updateExplanation();

            i_mouse_modeA = MouseMode.MOVE_CALCULATED_SHAPE_102;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);
            Button_kyoutuu_sagyou();
            //repaint();
        });
        pnls.add(Button_oriagari_idiu);


        Button_oriagari_idiu.setMargin(new Insets(0, 0, 0, 0));
        Button_oriagari_idiu.setIcon(createImageIcon("ppp/oriagari_idiu.png"));

// *******南******************************************************************

        //------------------------------------------------
        JPanel pnls2 = new JPanel();
        pnls2.setBackground(Color.white);
        pnls2.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
        pnls2.setBorder(new LineBorder(Color.black, 1));
        pnls.add(pnls2);
        //------------------------------------------------


// ********南*****************************************************************
        JButton Button_oriagari_syukusyou = new JButton("");// new JButton(	"F_z_out"	);
        Button_oriagari_syukusyou.addActionListener(e -> {
            img_explanation_fname = "qqq/oriagari_syukusyou.png";
            updateExplanation();


            OZ.d_foldedFigure_scale_factor = OZ.d_foldedFigure_scale_factor / Math.sqrt(Math.sqrt(Math.sqrt(2.0)));
            OZ.camera_of_foldedFigure.setCameraZoomX(OZ.d_foldedFigure_scale_factor);
            OZ.camera_of_foldedFigure.setCameraZoomY(OZ.d_foldedFigure_scale_factor);

            OZ.camera_of_foldedFigure_front.setCameraZoomX(OZ.d_foldedFigure_scale_factor);
            OZ.camera_of_foldedFigure_front.setCameraZoomY(OZ.d_foldedFigure_scale_factor);

            OZ.camera_of_foldedFigure_rear.setCameraZoomX(OZ.d_foldedFigure_scale_factor);
            OZ.camera_of_foldedFigure_rear.setCameraZoomY(OZ.d_foldedFigure_scale_factor);

            OZ.camera_of_transparent_front.setCameraZoomX(OZ.d_foldedFigure_scale_factor);
            OZ.camera_of_transparent_front.setCameraZoomY(OZ.d_foldedFigure_scale_factor);

            OZ.camera_of_transparent_rear.setCameraZoomX(OZ.d_foldedFigure_scale_factor);
            OZ.camera_of_transparent_rear.setCameraZoomY(OZ.d_foldedFigure_scale_factor);

            text29.setText(String.valueOf(OZ.d_foldedFigure_scale_factor));
            text29.setCaretPosition(0);

            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnls2.add(Button_oriagari_syukusyou);
        Button_oriagari_syukusyou.setMargin(new Insets(0, 0, 0, 0));
        Button_oriagari_syukusyou.setIcon(createImageIcon("ppp/oriagari_syukusyou.png"));


// *******南******************************************************************

        text29 = new JTextField("", 2);
        text29.setHorizontalAlignment(JTextField.RIGHT);

        pnls2.add(text29);

// ****南**************************************************************************

// -----縮尺係数set
        JButton Button_oriagarizu_syukusyaku_keisuu_set = new JButton("S");
        Button_oriagarizu_syukusyaku_keisuu_set.addActionListener(e -> {
            //set_syukusyaku_keisuu();
            //public void set_syukusyaku_keisuu(){
            double d_oriagarizu_syukusyaku_keisuu_old = OZ.d_foldedFigure_scale_factor;
            OZ.d_foldedFigure_scale_factor = String2double(text29.getText(), d_oriagarizu_syukusyaku_keisuu_old);
            if (OZ.d_foldedFigure_scale_factor <= 0.0) {
                OZ.d_foldedFigure_scale_factor = d_oriagarizu_syukusyaku_keisuu_old;
            }
            text29.setText(String.valueOf(OZ.d_foldedFigure_scale_factor));
            if (OZ.d_foldedFigure_scale_factor != d_oriagarizu_syukusyaku_keisuu_old) {
                OZ.camera_of_foldedFigure.setCameraZoomX(OZ.d_foldedFigure_scale_factor);
                OZ.camera_of_foldedFigure.setCameraZoomY(OZ.d_foldedFigure_scale_factor);

                OZ.camera_of_foldedFigure_front.setCameraZoomX(OZ.d_foldedFigure_scale_factor);
                OZ.camera_of_foldedFigure_front.setCameraZoomY(OZ.d_foldedFigure_scale_factor);

                OZ.camera_of_foldedFigure_rear.setCameraZoomX(OZ.d_foldedFigure_scale_factor);
                OZ.camera_of_foldedFigure_rear.setCameraZoomY(OZ.d_foldedFigure_scale_factor);

                OZ.camera_of_transparent_front.setCameraZoomX(OZ.d_foldedFigure_scale_factor);
                OZ.camera_of_transparent_front.setCameraZoomY(OZ.d_foldedFigure_scale_factor);

                OZ.camera_of_transparent_rear.setCameraZoomX(OZ.d_foldedFigure_scale_factor);
                OZ.camera_of_transparent_rear.setCameraZoomY(OZ.d_foldedFigure_scale_factor);
            }
            text29.setText(String.valueOf(OZ.d_foldedFigure_scale_factor));
            text29.setCaretPosition(0);
            canvas.repaint();

            //}
            img_explanation_fname = "qqq/oriagarizu_syukusyaku_keisuu_set.png";
            updateExplanation();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnls2.add(Button_oriagarizu_syukusyaku_keisuu_set);

        Button_oriagarizu_syukusyaku_keisuu_set.setMargin(new Insets(0, 0, 0, 0));


// ------縮尺係数set。ここまで


// ****南**************************************************************************
        JButton Button_oriagari_kakudai = new JButton("");
        Button_oriagari_kakudai.addActionListener(e -> {

            img_explanation_fname = "qqq/oriagari_kakudai.png";
            updateExplanation();

            OZ.d_foldedFigure_scale_factor = OZ.d_foldedFigure_scale_factor * Math.sqrt(Math.sqrt(Math.sqrt(2.0)));
            OZ.camera_of_foldedFigure.setCameraZoomX(OZ.d_foldedFigure_scale_factor);
            OZ.camera_of_foldedFigure.setCameraZoomY(OZ.d_foldedFigure_scale_factor);

            OZ.camera_of_foldedFigure_front.setCameraZoomX(OZ.d_foldedFigure_scale_factor);
            OZ.camera_of_foldedFigure_front.setCameraZoomY(OZ.d_foldedFigure_scale_factor);

            OZ.camera_of_foldedFigure_rear.setCameraZoomX(OZ.d_foldedFigure_scale_factor);
            OZ.camera_of_foldedFigure_rear.setCameraZoomY(OZ.d_foldedFigure_scale_factor);

            OZ.camera_of_transparent_front.setCameraZoomX(OZ.d_foldedFigure_scale_factor);
            OZ.camera_of_transparent_front.setCameraZoomY(OZ.d_foldedFigure_scale_factor);

            OZ.camera_of_transparent_rear.setCameraZoomX(OZ.d_foldedFigure_scale_factor);
            OZ.camera_of_transparent_rear.setCameraZoomY(OZ.d_foldedFigure_scale_factor);

            text29.setText(String.valueOf(OZ.d_foldedFigure_scale_factor));
            text29.setCaretPosition(0);

            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnls2.add(Button_oriagari_kakudai);
        Button_oriagari_kakudai.setMargin(new Insets(0, 0, 0, 0));
        Button_oriagari_kakudai.setIcon(createImageIcon("ppp/oriagari_kakudai.png"));


// *****南********************************************************************
        //------------------------------------------------
        JPanel pnls3 = new JPanel();
        pnls3.setBackground(Color.white);
        pnls3.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
        pnls3.setBorder(new LineBorder(Color.black, 1));
        pnls.add(pnls3);
        //------------------------------------------------

// *****南********************************************************************
        JButton Button_oriagari_p_kaiten = new JButton("");//new JButton(	"F+rot"	);
        Button_oriagari_p_kaiten.addActionListener(e -> {
            img_explanation_fname = "qqq/oriagari_p_kaiten.png";
            updateExplanation();

            OZ.d_foldedFigure_rotation_correction = OritaCalc.angle_between_m180_180(OZ.d_foldedFigure_rotation_correction + 11.25);
            OZ.camera_of_foldedFigure.setCameraAngle(OZ.d_foldedFigure_rotation_correction);
            OZ.camera_of_foldedFigure_front.setCameraAngle(OZ.d_foldedFigure_rotation_correction);
            OZ.camera_of_foldedFigure_rear.setCameraAngle(OZ.d_foldedFigure_rotation_correction);
            OZ.camera_of_transparent_front.setCameraAngle(OZ.d_foldedFigure_rotation_correction);
            OZ.camera_of_transparent_rear.setCameraAngle(OZ.d_foldedFigure_rotation_correction);

            text30.setText(String.valueOf(OZ.d_foldedFigure_rotation_correction));
            text30.setCaretPosition(0);

            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnls3.add(Button_oriagari_p_kaiten);
        Button_oriagari_p_kaiten.setMargin(new Insets(0, 0, 0, 0));
        Button_oriagari_p_kaiten.setIcon(createImageIcon("ppp/oriagari_p_kaiten.png"));


// ****南**************************************************************************
//回転角度補正
        text30 = new JTextField("", 2);
        text30.setHorizontalAlignment(JTextField.RIGHT);
        pnls3.add(text30);

// ****南**************************************************************************

// -----回転角度補正set
        JButton Button_oriagarizu_kaiten_hosei_set = new JButton("S");
        Button_oriagarizu_kaiten_hosei_set.addActionListener(e -> {
            double d_oriagarizu_kaiten_hosei_old = OZ.d_foldedFigure_rotation_correction;
            OZ.d_foldedFigure_rotation_correction = OritaCalc.angle_between_m180_180(String2double(text30.getText(), d_oriagarizu_kaiten_hosei_old));

            text30.setText(String.valueOf(OZ.d_foldedFigure_rotation_correction));

            if (OZ.d_foldedFigure_rotation_correction != d_oriagarizu_kaiten_hosei_old) {

                OZ.camera_of_foldedFigure.setCameraAngle(OZ.d_foldedFigure_rotation_correction);
                OZ.camera_of_foldedFigure_front.setCameraAngle(OZ.d_foldedFigure_rotation_correction);
                OZ.camera_of_foldedFigure_rear.setCameraAngle(OZ.d_foldedFigure_rotation_correction);
                OZ.camera_of_transparent_front.setCameraAngle(OZ.d_foldedFigure_rotation_correction);
                OZ.camera_of_transparent_rear.setCameraAngle(OZ.d_foldedFigure_rotation_correction);
            }
            text30.setText(String.valueOf(OZ.d_foldedFigure_rotation_correction));
            text30.setCaretPosition(0);
            canvas.repaint();


            img_explanation_fname = "qqq/oriagarizu_kaiten_hosei_set.png";
            updateExplanation();
            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnls3.add(Button_oriagarizu_kaiten_hosei_set);

        Button_oriagarizu_kaiten_hosei_set.setMargin(new Insets(0, 0, 0, 0));


// ------回転角度補正set。ここまで


// ******南*******************************************************************
        JButton Button_oriagari_m_kaiten = new JButton("");//new JButton(	"F-rot"	);
        Button_oriagari_m_kaiten.addActionListener(e -> {

            img_explanation_fname = "qqq/oriagari_m_kaiten.png";
            updateExplanation();
            OZ.d_foldedFigure_rotation_correction = OritaCalc.angle_between_m180_180(OZ.d_foldedFigure_rotation_correction - 11.25);
            OZ.camera_of_foldedFigure.setCameraAngle(OZ.d_foldedFigure_rotation_correction);
            OZ.camera_of_foldedFigure_front.setCameraAngle(OZ.d_foldedFigure_rotation_correction);
            OZ.camera_of_foldedFigure_rear.setCameraAngle(OZ.d_foldedFigure_rotation_correction);
            OZ.camera_of_transparent_front.setCameraAngle(OZ.d_foldedFigure_rotation_correction);
            OZ.camera_of_transparent_rear.setCameraAngle(OZ.d_foldedFigure_rotation_correction);

            text30.setText(String.valueOf(OZ.d_foldedFigure_rotation_correction));
            text30.setCaretPosition(0);


            Button_kyoutuu_sagyou();
            canvas.repaint();
        });
        pnls3.add(Button_oriagari_m_kaiten);
        Button_oriagari_m_kaiten.setMargin(new Insets(0, 0, 0, 0));
        Button_oriagari_m_kaiten.setIcon(createImageIcon("ppp/oriagari_m_kaiten.png"));

// *******南******************************************************************


// ******************************************************** //折りあがり図のanti_aliasアンチェイリアスの変更

        JButton Button_a_a = new JButton("a_a");
        Button_a_a.addActionListener(e -> {
            Button_kyoutuu_sagyou();
            img_explanation_fname = "qqq/a_a.png";
            updateExplanation();

            OZ.ct_worker.toggleAntiAlias();
            canvas.repaint();
        });
        pnls.add(Button_a_a);

        Button_a_a.setMargin(new Insets(0, 0, 0, 0));
// ******************************************************** //折りあがり図の影付け

        JButton Button_shadows = new JButton("S");
        Button_shadows.addActionListener(e -> {
            Button_kyoutuu_sagyou();
            img_explanation_fname = "qqq/kage.png";
            updateExplanation();
            OZ.ct_worker.toggleDisplayShadows();
            canvas.repaint();
        });
        pnls.add(Button_shadows);

        Button_shadows.setMargin(new Insets(0, 0, 0, 0));
// *********南****************************************************************
// -------------折り上がり予測図表面の色の選択

        Button_F_color = new JButton(" ");
        Button_F_color.addActionListener(e -> {
            img_explanation_fname = "qqq/F_color.png";
            updateExplanation();
            Button_kyoutuu_sagyou();
            i_mouseDragged_valid = false;
            i_mouseReleased_valid = false;

            //以下にやりたいことを書く

            OZ.foldedFigure_F_color = JColorChooser.showDialog(null, "F_col", Color.white);
            if (OZ.foldedFigure_F_color != null) {
                OZ.ct_worker.set_F_color(OZ.foldedFigure_F_color);
            }

            //以上でやりたいことは書き終わり

            Button_F_color.setBackground(OZ.foldedFigure_F_color);    //ボタンの色設定

            canvas.repaint();
        });
        Button_F_color.setMargin(new Insets(0, 0, 0, 0));
        Button_F_color.setIcon(createImageIcon("ppp/F_color.png"));
        pnls.add(Button_F_color);


        //重要注意　読み込みや書き出しでファイルダイアログのボックスが開くと、それをフレームに重なる位置で操作した場合、ファイルボックスが消えたときに、
        //マウスのドラッグとリリースが発生する。このため、余計な操作がされてしまう可能性がある。なお、このときマウスクリックは発生しない。
        // i_mouseDragged_valid=0;や i_mouseReleased_valid=0;は、この余計な操作を防ぐために指定している。


// -------------折り上がり予測図裏面の色の選択

        Button_B_color = new JButton(" ");
        Button_B_color.addActionListener(e -> {
            img_explanation_fname = "qqq/B_color.png";
            updateExplanation();
            Button_kyoutuu_sagyou();
            i_mouseDragged_valid = false;
            i_mouseReleased_valid = false;

            //以下にやりたいことを書く
            OZ.foldedFigure_B_color = JColorChooser.showDialog(null, "B_col", Color.white);

            if (OZ.foldedFigure_B_color != null) {
                OZ.ct_worker.set_B_color(OZ.foldedFigure_B_color);
            }
            //以上でやりたいことは書き終わり

            Button_B_color.setBackground(OZ.foldedFigure_B_color);    //ボタンの色設定
            canvas.repaint();
        });
        Button_B_color.setMargin(new Insets(0, 0, 0, 0));
        Button_B_color.setIcon(createImageIcon("ppp/B_color.png"));
        pnls.add(Button_B_color);


        //重要注意　読み込みや書き出しでファイルダイアログのボックスが開くと、それをフレームに重なる位置で操作した場合、ファイルボックスが消えたときに、
        //マウスのドラッグとリリースが発生する。このため、余計な操作がされてしまう可能性がある。なお、このときマウスクリックは発生しない。
        // i_mouseDragged_valid=0;や i_mouseReleased_valid=0;は、この余計な操作を防ぐために指定している。


// -------------折り上がり予測図　線の色の選択

        Button_L_color = new JButton(" ");
        Button_L_color.addActionListener(e -> {
            img_explanation_fname = "qqq/L_color.png";
            updateExplanation();
            Button_kyoutuu_sagyou();
            i_mouseDragged_valid = false;
            i_mouseReleased_valid = false;

            //以下にやりたいことを書く

            OZ.foldedFigure_L_color = JColorChooser.showDialog(null, "L_col", Color.white);
            if (OZ.foldedFigure_L_color != null) {
                OZ.ct_worker.set_L_color(OZ.foldedFigure_L_color);
            }


            //以上でやりたいことは書き終わり

            Button_L_color.setBackground(OZ.foldedFigure_L_color);    //ボタンの色設定
            canvas.repaint();
        });
        Button_L_color.setMargin(new Insets(0, 0, 0, 0));
        Button_L_color.setIcon(createImageIcon("ppp/L_color.png"));
        pnls.add(Button_L_color);


        //重要注意　読み込みや書き出しでファイルダイアログのボックスが開くと、それをフレームに重なる位置で操作した場合、ファイルボックスが消えたときに、
        //マウスのドラッグとリリースが発生する。このため、余計な操作がされてしまう可能性がある。なお、このときマウスクリックは発生しない。
        // i_mouseDragged_valid=0;や i_mouseReleased_valid=0;は、この余計な操作を防ぐために指定している。


// *******南******************************************************************
        JButton Button_keisan_tyuusi = new JButton("");//折り上がり予想の計算の中止
        Button_keisan_tyuusi.addActionListener(e -> {

            img_explanation_fname = "qqq/keisan_tyuusi.png";
            updateExplanation();

            if (subThreadRunning) {
                keisan_tyuusi();
            }

            Button_kyoutuu_sagyou();
        });
        pnls.add(Button_keisan_tyuusi);

        Button_keisan_tyuusi.setMargin(new Insets(0, 0, 0, 0));
        Button_keisan_tyuusi.setIcon(createImageIcon("ppp/keisan_tyuusi.png"));


// *******南****************************************************************** 折り上がり予想の廃棄 ************************************************
        JButton Button_settei_syokika = new JButton("");//new JButton(	"Del_F"	);
        Button_settei_syokika.addActionListener(e -> {

            img_explanation_fname = "qqq/settei_syokika.png";
            updateExplanation();


            if (i_OAZ == 0) {
                return;
            }
            OZ = temp_OZ;//20171223この行は不要かもしれないが、一瞬でもOZが示すOriagari_Zuがなくなることがないように念のために入れておく
            if (i_OAZ == OAZ.size() - 1) {
                OAZ.remove(i_OAZ);
                set_i_OAZ(OAZ.size() - 1);
            }
            if (i_OAZ < OAZ.size() - 1) {
                OAZ.remove(i_OAZ);
                set_i_OAZ(i_OAZ);
            }

            Button_kyoutuu_sagyou();
            canvas.repaint();

        });
        pnls.add(Button_settei_syokika);

        Button_settei_syokika.setMargin(new Insets(0, 0, 0, 0));
        Button_settei_syokika.setIcon(createImageIcon("ppp/settei_syokika.png"));

// *******南*************bbbbbbbbbb*****************************************************全操作廃棄 (ﾉToT)ﾉ ┫:･'.::･  ****************************************************

        JButton Button_zen_syokika = new JButton("");//new JButton(	"Del_all"	);
        Button_zen_syokika.addActionListener(e -> {

            img_explanation_fname = "qqq/zen_syokika.png";
            updateExplanation();

            //展開図の初期化　開始
            //settei_syokika_cp();//展開図パラメータの初期化
            developmentView_initialization();
            //展開図の初期化　終了
            //
            //折畳予測図のの初期化　開始
            OZ = temp_OZ;//20171223この行は不要かもしれないが、一瞬でもOZが示すOriagari_Zuがなくなることがないように念のために入れておく
            OAZ.clear();
            OAZ_add_new_Oriagari_Zu();
            set_i_OAZ(0);
            configure_syokika_yosoku();
            //折畳予測図のの初期化　終了

            Button_kyoutuu_sagyou();
            canvas.repaint();
            i_mouse_modeA = MouseMode.FOLDABLE_LINE_DRAW_71;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

            es1.record();
            es1.h_record();
        });
        pnls.add(Button_zen_syokika);

        Button_zen_syokika.setMargin(new Insets(0, 0, 0, 0));
        Button_zen_syokika.setIcon(createImageIcon("ppp/zen_syokika.png"));

// *******南*********ボタンの定義はここまで*******************************************************************************************************************************

        //展開図の初期化　開始
        //settei_syokika_cp();//展開図パラメータの初期化
        developmentView_initialization();
        //展開図の初期化　終了

        i_undo_suu = 20;
        text10.setText(String.valueOf(i_undo_suu));
        i_undo_suu_om = 5;
        text31.setText(String.valueOf(i_undo_suu_om));
        i_h_undo_suu = 20;
        text11.setText(String.valueOf(i_h_undo_suu));
        scale_interval = 5;
        text25.setText(String.valueOf(scale_interval));
        es1.set_a_to_parallel_scale_interval(scale_interval);
        es1.set_b_to_parallel_scale_interval(scale_interval);

        selectionOperationMode = SelectionOperationMode.MOVE_1;
        Button_sel_mou_wakukae();//セレクトされた折線がある状態で、セレクトされている折線の頂点をクリックした場合の動作モードの初期設定

        //折畳予測図のの初期化　開始
        configure_syokika_yosoku();
        //折畳予測図のの初期化　終了

        Button_kyoutuu_sagyou();

        canvas.repaint();

        img_explanation_fname = "qqq/a__hajimeni.png";
        updateExplanation();

        Button_sen_tokutyuu_color.setBackground(sen_tokutyuu_color);//特注色の指定色表示

        // 測定長さと角度の表示

        es1.measurement_display();
        es1.setCamera(camera_of_orisen_input_diagram);

        //Ubox.test1();
        es1.record();
        es1.h_record();

        OZ.ct_worker.set_F_color(OZ.foldedFigure_F_color); //折り上がり図の表面の色
        Button_F_color.setBackground(OZ.foldedFigure_F_color);    //ボタンの色設定

        OZ.ct_worker.set_B_color(OZ.foldedFigure_B_color);//折り上がり図の裏面の色
        Button_B_color.setBackground(OZ.foldedFigure_B_color);//ボタンの色設定

        OZ.ct_worker.set_L_color(OZ.foldedFigure_L_color);        //折り上がり図の線の色
        Button_L_color.setBackground(OZ.foldedFigure_L_color);        //ボタンの色設定
    }//------------------------------------------ボタンの定義等、ここまでがコンストラクタとして起動直後に最初に実行される内容

    //ここまでが変数等の定義

    public enum FoldType {
        NOTHING_0,
        FOR_ALL_LINES_1,
        FOR_SELECTED_LINES_2,
        CHANGING_FOLDED_3,
    }

    private FoldType getFoldType() {

        FoldType i_fold_type;//= 0 Do nothing, = 1 Folding estimation for all fold lines in the normal development view, = 2 for fold estimation for selected fold lines, = 3 for changing the folding state
        int foldLineTotalForSelectFolding = es1.getFoldLineTotalForSelectFolding();
        System.out.println("OAZ.size() = " + OAZ.size() + "    : i_OAZ = " + i_OAZ + "    : es1.get_orisensuu_for_select_oritatami() = " + foldLineTotalForSelectFolding);
        if (OAZ.size() == 1) {                        //折り上がり系図無し
            if (i_OAZ == 0) {                            //展開図指定
                if (foldLineTotalForSelectFolding == 0) {        //折り線選択無し
                    i_fold_type = FoldType.FOR_ALL_LINES_1;//全展開図で折畳み
                } else {        //折り線選択有り
                    i_fold_type = FoldType.FOR_SELECTED_LINES_2;//選択された展開図で折畳み
                }
            } else {                        //折り上がり系図指定
                i_fold_type = FoldType.NOTHING_0;//有り得ない
            }
        } else {                        //折り上がり系図有り
            if (i_OAZ == 0) {                            //展開図指定
                if (foldLineTotalForSelectFolding == 0) {        //折り線選択無し
                    i_fold_type = FoldType.NOTHING_0;//何もしない
                } else {        //折り線選択有り
                    i_fold_type = FoldType.FOR_SELECTED_LINES_2;//選択された展開図で折畳み
                }
            } else {                        //折り上がり系図指定
                if (foldLineTotalForSelectFolding == 0) {        //折り線選択無し
                    i_fold_type = FoldType.CHANGING_FOLDED_3;//指定された折り上がり系図で折畳み
                } else {        //折り線選択有り
                    i_fold_type = FoldType.FOR_SELECTED_LINES_2;//選択された展開図で折畳み
                }
            }
        }

        return i_fold_type;
    }

    private void oritatame(FoldType i_fold_type, FoldedFigure.EstimationOrder i_suitei_meirei) {//引数の意味は(i_fold_type , i_suitei_meirei)
        //i_fold_typeはget_i_fold_type()関数で取得する。
        //i_fold_type=0なにもしない、=1通常の展開図の全折線を対象とした折り畳み推定、=2はselectされた折線を対象とした折り畳み推定、=3は折畳み状態を変更
        if (i_fold_type == FoldType.NOTHING_0) {
            System.out.println(" oritatame 20180108");
        } else if ((i_fold_type == FoldType.FOR_ALL_LINES_1) || (i_fold_type == FoldType.FOR_SELECTED_LINES_2)) {
            if (i_fold_type == FoldType.FOR_ALL_LINES_1) {
                es1.select_all();
            }
            //
            if (ckbox_cp_kaizen_folding.isSelected()) {//展開図のおかしい所（枝状の折り線等）を自動修正する
                Drawing_Worker es2 = new Drawing_Worker(r, this);    //基本枝職人。マウスからの入力を受け付ける。
                es2.setMemo_for_reading(es1.foldLines.getMemo_for_select_folding());
                es2.point_removal();
                es2.overlapping_line_removal();
                es2.branch_trim(0.000001);
                es2.circle_organize();
                Ss0 = es2.get_for_folding();
            } else {
                Ss0 = es1.getForSelectFolding();
            }

            point_of_referencePlane_old.set(OZ.cp_worker1.get_point_of_referencePlane_tv());//20180222折り線選択状態で折り畳み推定をする際、以前に指定されていた基準面を引き継ぐために追加
            //これより前のOZは古いOZ
            folding_prepare();//OAZのアレイリストに、新しく折り上がり図をひとつ追加し、それを操作対象に指定し、OAZ(0)共通パラメータを引き継がせる。
            //これより後のOZは新しいOZに変わる

            OZ.estimationOrder = i_suitei_meirei;

            if (!subThreadRunning) {
                subThreadRunning = true;
                subThreadMode = SubThread.Mode.FOLDING_ESTIMATE_0;//1=折畳み推定の別解をまとめて出す。0=折畳み推定の別解をまとめて出すモードではない。この変数はサブスレッドの動作変更につかうだけ。20170611にVer3.008から追加
                mks();//新しいスレッドを作る
                sub.start();
            }

        } else if (i_fold_type == FoldType.CHANGING_FOLDED_3) {
            OZ.estimationOrder = i_suitei_meirei;

            if (!subThreadRunning) {
                subThreadRunning = true;
                subThreadMode = SubThread.Mode.FOLDING_ESTIMATE_0;//1=折畳み推定の別解をまとめて出す。0=折畳み推定の別解をまとめて出すモードではない。この変数はサブスレッドの動作変更につかうだけ。20170611にVer3.008から追加
                mks();//新しいスレッドを作る
                sub.start();
            }
        }
    }

    void folding_prepare() {//Add one new folding diagram to the OAZ array list, specify it as the operation target, and inherit the OAZ (0) common parameters.
        System.out.println(" oritatami_jyunbi 20180107");

        OAZ_add_new_Oriagari_Zu(); //OAZのアレイリストに、新しく折り上がり図をひとつ追加する。

        set_i_OAZ(OAZ.size() - 1);//i_OAZ=i;OZ = (Oriagari_Zu)OAZ.get(i_OAZ); OZ(各操作の対象となる折上がり図）に、アレイリストに最新に追加された折上がり図を割り当てる)

        FoldedFigure orz = OAZ.get(0);//OAZ(0)(共通パラメータを保持する折上がり図）をorzに割り付ける

        //以下ではOAZ(0)の共通パラメータを、現在操作対象となっているOZに渡す
        OZ.foldedFigure_F_color = orz.ct_worker.get_F_color();//20171223折り上がり図の表面の色の変更はOZ.oriagarizu_F_colorとOZ.js.set_F_colorの両方やる必要あり
        OZ.ct_worker.set_F_color(OZ.foldedFigure_F_color); //折り上がり図の表面の色
        Button_F_color.setBackground(OZ.foldedFigure_F_color);    //ボタンの色設定

        OZ.foldedFigure_B_color = orz.ct_worker.get_B_color();//20171223折り上がり図の表面の色の変更はOZ.oriagarizu_F_colorとOZ.js.set_F_colorの両方やる必要あり
        OZ.ct_worker.set_B_color(OZ.foldedFigure_B_color); //折り上がり図の表面の色
        Button_B_color.setBackground(OZ.foldedFigure_B_color);    //ボタンの色設定

        OZ.foldedFigure_L_color = orz.ct_worker.get_L_color();//20171223折り上がり図の表面の色の変更はOZ.oriagarizu_F_colorとOZ.js.set_F_colorの両方やる必要あり
        OZ.ct_worker.set_L_color(OZ.foldedFigure_L_color); //折り上がり図の表面の色
        Button_L_color.setBackground(OZ.foldedFigure_L_color);    //ボタンの色設定
        //以上でOAZ(0)の共通パラメータを、OZに渡す作業は終了

    }

// ----------------------------------

    // ------------------------------------------------------------------------------
    public void OAZ_add_new_Oriagari_Zu() {
        OAZ.add(new FoldedFigure_01(this));
    }

    public void measured_length_1_display(double d0) {
        label_length_sokutei_1.setText(String.valueOf(d0));
    }
    // ----------------------------------------------------------

    public void measured_length_2_display(double d0) {
        label_length_sokutei_2.setText(String.valueOf(d0));
    }

    // ----------------------------------------------------------

    public void measured_angle_1_display(double d0) {
        label_kakudo_sokutei_1.setText(String.valueOf(d0));
    }
    // ----------------------------------------------------------

    public void measured_angle_2_display(double d0) {
        label_kakudo_sokutei_2.setText(String.valueOf(d0));
    }

    // ----------------------------------------------------------

    public void measured_angle_3_display(double d0) {
        label_kakudo_sokutei_3.setText(String.valueOf(d0));
    }


// *******************************************************************************************************

    //----------------------------------------------------------

    public void iro_sitei_ato_ni_jissisuru_sagyou() {
        if (iro_sitei_ato_ni_jissisuru_sagyou_bangou == MouseMode.DRAW_CREASE_FREE_1) {
            foldLineAdditionalInputMode = Drawing_Worker.FoldLineAdditionalInputMode.POLY_LINE_0;//=0は折線入力　=1は補助線入力モード
            es1.setFoldLineAdditional(foldLineAdditionalInputMode);//このボタンと機能は補助絵線共通に使っているのでi_orisen_hojyosenの指定がいる
            i_mouse_modeA = MouseMode.DRAW_CREASE_FREE_1;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

        } else if (iro_sitei_ato_ni_jissisuru_sagyou_bangou == MouseMode.DRAW_CREASE_RESTRICTED_11) {
            i_mouse_modeA = MouseMode.DRAW_CREASE_RESTRICTED_11;
            System.out.println("i_mouse_modeA = " + i_mouse_modeA);

        }
    }

    public void set_grid_bunkatu_suu() {
        int nyuuryoku_kitei_old = nyuuryoku_kitei;
        nyuuryoku_kitei = StringOp.String2int(text1.getText(), nyuuryoku_kitei_old);
        if (nyuuryoku_kitei < 1) {
            nyuuryoku_kitei = 1;
        }
        text1.setText(String.valueOf(nyuuryoku_kitei));
        es1.setGridDivisionNumber(nyuuryoku_kitei);
    }

    public void keikoku_sentaku_sareta_orisen_ga_nai() {
        JLabel label = new JLabel(
                "<html>２色塗りわけ展開図を描くためには、あらかじめ対象範囲を選択してください（selectボタンを使う）。<br>" +
                        "To get 2-Colored crease pattern, select the target range in advance (use the select button).<html>");
        JOptionPane.showMessageDialog(this, label);
    }

    public void keikoku_sentaku_sareta_orisen_ga_nai_2() {
        JLabel label = new JLabel(
                "<html>新たに折り上がり図を描くためには、あらかじめ対象範囲を選択してください（selectボタンを使う）。<br>" +
                        "To calculate new folded shape, select the target clease lines range in advance (use the select button).<html>");
        JOptionPane.showMessageDialog(this, label);
    }

    public void keisan_tyuusi() {
        int option = JOptionPane.showConfirmDialog(this, createImageIcon("ppp/keisan_tyuusi_DLog.png"));

        if (option == JOptionPane.YES_OPTION) {
            i_mouseDragged_valid = false;
            i_mouseReleased_valid = false;
            writeMemo2File();
            es1.record();
        } else if (option == JOptionPane.NO_OPTION) {
        } else if (option == JOptionPane.CANCEL_OPTION) {
            return;
        }

        sub.stop();
        subThreadRunning = false;

        configure_syokika_yosoku();
    }

    public void closing() {
        int option = JOptionPane.showConfirmDialog(this, createImageIcon("ppp/owari.png"));

        if (option == JOptionPane.YES_OPTION) {
            i_mouseDragged_valid = false;
            i_mouseReleased_valid = false;
            writeMemo2File();
            if (subThreadRunning) {
                sub.stop();
            }
            System.exit(0);
        } else if (option == JOptionPane.NO_OPTION) {
            if (subThreadRunning) {
                sub.stop();
            }
            System.exit(0);
        } else if (option == JOptionPane.CANCEL_OPTION) {
            return;
        }
    }

    // --------展開図の初期化-----------------------------
    void developmentView_initialization() {

//全体
        //描き職人の初期化
        es1.reset();
        es1.reset_2();    //描き職人の初期化


        //camera_of_orisen_nyuuryokuzu	の設定;
        camera_of_orisen_input_diagram.setCameraPositionX(0.0);
        camera_of_orisen_input_diagram.setCameraPositionY(0.0);
        camera_of_orisen_input_diagram.setCameraAngle(0.0);
        camera_of_orisen_input_diagram.setCameraMirror(1.0);
        camera_of_orisen_input_diagram.setCameraZoomX(1.0);
        camera_of_orisen_input_diagram.setCameraZoomY(1.0);
        camera_of_orisen_input_diagram.setDisplayPositionX(350.0);
        camera_of_orisen_input_diagram.setDisplayPositionY(350.0);

        //camera_haikei	;

        es1.setCamera(camera_of_orisen_input_diagram);
        OZ.cp_worker1.setCamera(camera_of_orisen_input_diagram);

        //折線入力か補助線入力か
        foldLineAdditionalInputMode = Drawing_Worker.FoldLineAdditionalInputMode.POLY_LINE_0;
//北辺

        ckbox_mouse_settings.setSelected(true);//表示するかどうかの選択
        ckbox_point_search.setSelected(false);//表示するかどうかの選択
        ckbox_ten_hanasi.setSelected(false);//es1.set_i_hanasi(0);          //表示するかどうかの選択
        ckbox_kou_mitudo_nyuuryoku.setSelected(false);
        es1.set_i_kou_mitudo_nyuuryoku(false);          //高密度入力するかどうかの選択
        ckbox_bun.setSelected(true);//文を表示するかどうかの選択
        ckbox_cp.setSelected(true);//折線を表示するかどうかの選択
        ckbox_a0.setSelected(true);//補助活線を表示するかどうかの選択
        ckbox_a1.setSelected(true);//補助画線を表示するかどうかの選択
        ckbox_mark.setSelected(true);//十字や基準面などの目印画線
        ckbox_cp_ue.setSelected(false);//展開図を折り上がり予想図の上に描く
        ckbox_folding_keika.setSelected(false);//折り上がり予想の途中経過の書き出し
        ckbox_cp_kaizen_folding.setSelected(false);//cpを折畳み前に自動改善する
        ckbox_select_nokosi.setSelected(false);//select状態を折畳み操作をしてもなるべく残す
        ckbox_toukazu_color.setSelected(false);//透過図をカラー化する。


        //内分された折線の指定
        d_orisen_internalDivisionRatio_a = 1.0;
        text3.setText(String.valueOf(d_orisen_internalDivisionRatio_a));
        d_orisen_internalDivisionRatio_b = 0.0;
        text4.setText(String.valueOf(d_orisen_internalDivisionRatio_b));
        d_orisen_internalDivisionRatio_c = 0.0;
        text5.setText(String.valueOf(d_orisen_internalDivisionRatio_c));
        d_orisen_internalDivisionRatio_d = 0.0;
        text6.setText(String.valueOf(d_orisen_internalDivisionRatio_d));
        d_orisen_internalDivisionRatio_e = 1.0;
        text7.setText(String.valueOf(d_orisen_internalDivisionRatio_e));
        d_orisen_internalDivisionRatio_f = 2.0;
        text8.setText(String.valueOf(d_orisen_internalDivisionRatio_f));

        //
        scaleFactor = 1.0;
        text27.setText(String.valueOf(scaleFactor)); //縮尺係数
        rotationCorrection = 0.0;
        text28.setText(String.valueOf(rotationCorrection));//回転表示角度の補正係数

        OZ.d_foldedFigure_scale_factor = 1.0;
        text29.setText(String.valueOf(OZ.d_foldedFigure_scale_factor));//折り上がり図の縮尺係数
        OZ.d_foldedFigure_rotation_correction = 0.0;
        text30.setText(String.valueOf(OZ.d_foldedFigure_rotation_correction));//折り上がり図の回転表示角度の補正角度


        //背景表示
        displayBackground = true;
        Button_background_kirikae.setBackground(Color.ORANGE);

        //背景ロックオン
        lockBackground = false;
        lockBackground_ori = false;
        Button_background_Lock_on.setBackground(Color.gray);

//西辺


        //展開図の線の太さ。
        iLineWidth = 1;

        //頂点のしるしの幅
        pointSize = 1;


        //基本枝構造の直線の両端の円の半径、（以前は枝と各種ポイントの近さの判定基準）
        //double r=3.0;
        //es1.set_r(r);

        //折線表現を色で表す
        lineStyle = LineStyle.COLOR;

        //ペンの色の指定
        icol = LineColor.RED_1;
        es1.setColor(icol);    //最初の折線の色を指定する。0は黒、1は赤、2は青。
        ButtonCol_reset();
        ButtonCol_red.setForeground(Color.black);
        ButtonCol_red.setBackground(Color.red);    //折線のボタンの色設定


        //折線分割数
        foldLineDividingNumber = 2;
        text2.setText(String.valueOf(foldLineDividingNumber));
        es1.setFoldLineDividingNumber(foldLineDividingNumber);//フリー折線入力時の分割数


        //格子分割数の指定
        text1.setText("8");
        set_grid_bunkatu_suu();

        //格子の適用範囲の指定
        es1.setBaseState(Grid.State.WITHIN_PAPER);//格子の状態を用紙内適用にする。

        //任意格子
        d_grid_x_a = 0.0;
        text18.setText(String.valueOf(d_grid_x_a));
        d_grid_x_b = 1.0;
        text19.setText(String.valueOf(d_grid_x_b));
        d_grid_x_c = 1.0;
        text20.setText(String.valueOf(d_grid_x_c));

        d_grid_y_a = 0.0;
        text21.setText(String.valueOf(d_grid_y_a));
        d_grid_y_b = 1.0;
        text22.setText(String.valueOf(d_grid_y_b));
        d_grid_y_c = 1.0;
        text23.setText(String.valueOf(d_grid_y_c));

        d_grid_angle = 90.0;
        text24.setText(String.valueOf(d_grid_angle));

        setGrid();
//--------------------------------------------
//東辺
        //角度系入力を22.5度系にする。
        es1.set_id_angle_system(8);

        //自由角度
        d_restricted_angle_a = 40.0;
        text12.setText(String.valueOf(d_restricted_angle_a));
        d_restricted_angle_b = 60.0;
        text13.setText(String.valueOf(d_restricted_angle_b));
        d_restricted_angle_c = 80.0;
        text14.setText(String.valueOf(d_restricted_angle_c));

        d_restricted_angle_a = 30.0;
        text15.setText(String.valueOf(d_restricted_angle_d));
        d_restricted_angle_b = 50.0;
        text16.setText(String.valueOf(d_restricted_angle_e));
        d_restricted_angle_c = 100.0;
        text17.setText(String.valueOf(d_restricted_angle_f));

        //多角形の角数
        numPolygonCorners = 5;
        text9.setText(String.valueOf(numPolygonCorners));

        //補助画線の色
        h_icol = LineColor.ORANGE_4;
        es1.h_setcolor(h_icol);                                        //最初の補助線の色を指定する。4はオレンジ、7は黄。
        Button_h_Col_reset();
        Button_Col_orange.setForeground(Color.black);
        Button_Col_orange.setBackground(Color.ORANGE);    //補助線のボタンの色設定

        ckbox_check3.setSelected(false);//checkするかどうかの選択
        es1.setCheck3(false);
        ckbox_check4.setSelected(false);//checkするかどうかの選択
        es1.setCheck4(false);
    }


// *******************************************************************************************************

    public void setGrid() {
        double d_grid_x_a_old = d_grid_x_a;
        double d_grid_x_b_old = d_grid_x_b;
        double d_grid_x_c_old = d_grid_x_c;
        double d_grid_y_a_old = d_grid_y_a;
        double d_grid_y_b_old = d_grid_y_b;
        double d_grid_y_c_old = d_grid_y_c;

        double d_grid_angle_old = d_grid_angle;

        d_grid_x_a = String2double(text18.getText(), d_grid_x_a_old);
        d_grid_x_b = String2double(text19.getText(), d_grid_x_b_old);
        d_grid_x_c = String2double(text20.getText(), d_grid_x_c_old);
        if (d_grid_x_c < 0.0) {
            d_grid_x_c = 0.0;
        }
        d_grid_y_a = String2double(text21.getText(), d_grid_y_a_old);
        d_grid_y_b = String2double(text22.getText(), d_grid_y_b_old);
        d_grid_y_c = String2double(text23.getText(), d_grid_y_c_old);
        if (d_grid_y_c < 0.0) {
            d_grid_y_c = 0.0;
        }

        d_grid_angle = String2double(text24.getText(), d_grid_angle_old);
        if (Math.abs(OritaCalc.angle_between_0_360(d_grid_angle)) < 0.1) {
            d_grid_angle = 90.0;
        }
        if (Math.abs(OritaCalc.angle_between_0_360(d_grid_angle - 180.0)) < 0.1) {
            d_grid_angle = 90.0;
        }
        if (Math.abs(OritaCalc.angle_between_0_360(d_grid_angle - 360.0)) < 0.1) {
            d_grid_angle = 90.0;
        }


        d_grid_x_length = d_grid_x_a + d_grid_x_b * Math.sqrt(d_grid_x_c);
        if (d_grid_x_length < 0.0) {
            d_grid_x_a = 1.0;
            d_grid_x_b = 0.0;
            d_grid_x_c = 0.0;
        }
        d_grid_y_length = d_grid_y_a + d_grid_y_b * Math.sqrt(d_grid_y_c);
        if (d_grid_y_length < 0.0) {
            d_grid_y_a = 1.0;
            d_grid_y_b = 0.0;
            d_grid_y_c = 0.0;
        }
        if (Math.abs(d_grid_x_length) < 0.0001) {
            d_grid_x_a = 1.0;
            d_grid_x_b = 0.0;
            d_grid_x_c = 0.0;
            d_grid_x_length = d_grid_x_a + d_grid_x_b * Math.sqrt(d_grid_x_c);
        }
        if (Math.abs(d_grid_y_length) < 0.0001) {
            d_grid_y_a = 1.0;
            d_grid_y_b = 0.0;
            d_grid_y_c = 0.0;
            d_grid_y_length = d_grid_y_a + d_grid_y_b * Math.sqrt(d_grid_y_c);
        }

        text18.setText(String.valueOf(d_grid_x_a));
        text19.setText(String.valueOf(d_grid_x_b));
        text20.setText(String.valueOf(d_grid_x_c));
        text21.setText(String.valueOf(d_grid_y_a));
        text22.setText(String.valueOf(d_grid_y_b));
        text23.setText(String.valueOf(d_grid_y_c));

        text24.setText(String.valueOf(d_grid_angle));

        es1.setGrid(d_grid_x_length, d_grid_y_length, d_grid_angle);
    }

// ------------------------------------------------------

    // *******************************************************************************************************
    public void Frame_tuika() {
        //Frame add_frame
        if (i_add_frame) {
            System.out.println("111 i_add_frame=" + i_add_frame);
            add_frame.dispose();
            add_frame = new OpenFrame("add_frame", this);
        } else {
            System.out.println("000 i_add_frame=" + i_add_frame);
            add_frame = new OpenFrame("add_frame", this);
        }
        i_add_frame = true;
        add_frame.toFront();
    }


// *******************************************************************************************************

    //ボタンを押されたときの処理----------------
    public void actionPerformed(ActionEvent e) {

    }

    public void setInternalDivisionRatio() {
        double d_orisen_internalDivisionRatio_a_old = d_orisen_internalDivisionRatio_a;
        double d_orisen_internalDivisionRatio_b_old = d_orisen_internalDivisionRatio_b;
        double d_orisen_internalDivisionRatio_c_old = d_orisen_internalDivisionRatio_c;
        double d_orisen_internalDivisionRatio_d_old = d_orisen_internalDivisionRatio_d;
        double d_orisen_internalDivisionRatio_e_old = d_orisen_internalDivisionRatio_e;
        double d_orisen_internalDivisionRatio_f_old = d_orisen_internalDivisionRatio_f;

        d_orisen_internalDivisionRatio_a = String2double(text3.getText(), d_orisen_internalDivisionRatio_a_old);
        d_orisen_internalDivisionRatio_b = String2double(text4.getText(), d_orisen_internalDivisionRatio_b_old);
        d_orisen_internalDivisionRatio_c = String2double(text5.getText(), d_orisen_internalDivisionRatio_c_old);
        if (d_orisen_internalDivisionRatio_c < 0.0) {
            d_orisen_internalDivisionRatio_c = 0.0;
        }
        d_orisen_internalDivisionRatio_d = String2double(text6.getText(), d_orisen_internalDivisionRatio_d_old);
        d_orisen_internalDivisionRatio_e = String2double(text7.getText(), d_orisen_internalDivisionRatio_e_old);
        d_orisen_internalDivisionRatio_f = String2double(text8.getText(), d_orisen_internalDivisionRatio_f_old);
        if (d_orisen_internalDivisionRatio_f < 0.0) {
            d_orisen_internalDivisionRatio_f = 0.0;
        }

        double d_internalDivisionRatio_s;
        d_internalDivisionRatio_s = d_orisen_internalDivisionRatio_a + d_orisen_internalDivisionRatio_b * Math.sqrt(d_orisen_internalDivisionRatio_c);
        if (d_internalDivisionRatio_s < 0.0) {
            d_orisen_internalDivisionRatio_b = 0.0;
        }
        double d_internalDivisionRatio_t;
        d_internalDivisionRatio_t = d_orisen_internalDivisionRatio_d + d_orisen_internalDivisionRatio_e * Math.sqrt(d_orisen_internalDivisionRatio_f);
        if (d_internalDivisionRatio_t < 0.0) {
            d_orisen_internalDivisionRatio_e = 0.0;
        }

        text3.setText(String.valueOf(d_orisen_internalDivisionRatio_a));
        text4.setText(String.valueOf(d_orisen_internalDivisionRatio_b));
        text5.setText(String.valueOf(d_orisen_internalDivisionRatio_c));
        text6.setText(String.valueOf(d_orisen_internalDivisionRatio_d));
        text7.setText(String.valueOf(d_orisen_internalDivisionRatio_e));
        text8.setText(String.valueOf(d_orisen_internalDivisionRatio_f));

        es1.set_d_internalDivisionRatio_st(d_internalDivisionRatio_s, d_internalDivisionRatio_t);
    }

    public void set_restricted_angle_abc() {
        double d_restricted_angle_a_old = d_restricted_angle_a;
        double d_restricted_angle_b_old = d_restricted_angle_b;
        double d_restricted_angle_c_old = d_restricted_angle_c;

        d_restricted_angle_a = String2double(text12.getText(), d_restricted_angle_a_old);
        d_restricted_angle_b = String2double(text13.getText(), d_restricted_angle_b_old);
        d_restricted_angle_c = String2double(text14.getText(), d_restricted_angle_c_old);

        text12.setText(String.valueOf(d_restricted_angle_a));
        text13.setText(String.valueOf(d_restricted_angle_b));
        text14.setText(String.valueOf(d_restricted_angle_c));

        es1.set_d_restricted_angle(d_restricted_angle_a, d_restricted_angle_b, d_restricted_angle_c);
    }

    public void set_restricted_angle_def() { //このdefは「定義」と言う意味ではなく、dとeとfを扱うという意味
        double d_restricted_angle_d_old = d_restricted_angle_d;
        double d_restricted_angle_e_old = d_restricted_angle_e;
        double d_restricted_angle_f_old = d_restricted_angle_f;

        d_restricted_angle_d = String2double(text15.getText(), d_restricted_angle_d_old);
        d_restricted_angle_e = String2double(text16.getText(), d_restricted_angle_e_old);
        d_restricted_angle_f = String2double(text17.getText(), d_restricted_angle_f_old);

        text15.setText(String.valueOf(d_restricted_angle_d));
        text16.setText(String.valueOf(d_restricted_angle_e));
        text17.setText(String.valueOf(d_restricted_angle_f));

        es1.set_d_restricted_angle(d_restricted_angle_d, d_restricted_angle_e, d_restricted_angle_f);
    }

    //--------------------------------------------------------
    void Button_sel_mou_wakukae() {

        Button_move.setBorder(new LineBorder(new Color(150, 150, 150), 1, false));
        Button_move_2p2p.setBorder(new LineBorder(new Color(150, 150, 150), 1, false));
        Button_copy_paste.setBorder(new LineBorder(new Color(150, 150, 150), 1, false));
        Button_copy_paste_2p2p.setBorder(new LineBorder(new Color(150, 150, 150), 1, false));
        Button_kyouei.setBorder(new LineBorder(new Color(150, 150, 150), 1, false));


        switch (selectionOperationMode) {
            case MOVE_1:
                Button_move.setBorder(new LineBorder(Color.green, 3, false));
                break;
            case MOVE4P_2:
                Button_move_2p2p.setBorder(new LineBorder(Color.green, 3, false));
                break;
            case COPY_3:
                Button_copy_paste.setBorder(new LineBorder(Color.green, 3, false));
                break;
            case COPY4P_4:
                Button_copy_paste_2p2p.setBorder(new LineBorder(Color.green, 3, false));
                break;
            case MIRROR_5:
                Button_kyouei.setBorder(new LineBorder(Color.green, 3, false));
                break;
        }
    }

    //--------------------------------------------------------
    void Button_reset() {
        Button_M_nisuru.setForeground(Color.black);
        Button_V_nisuru.setForeground(Color.black);
        Button_E_nisuru.setForeground(Color.black);
        Button_HK_nisuru.setForeground(Color.black); //HKとは補助活線のこと
        Button_senbun_henkan2.setForeground(Color.black);

        Button_M_nisuru.setBackground(Color.white);
        Button_V_nisuru.setBackground(Color.white);
        Button_E_nisuru.setBackground(Color.white);
        Button_HK_nisuru.setBackground(Color.white);
        Button_senbun_henkan2.setBackground(Color.white);
    }

    //--------------------------------------------------------
    void ButtonCol_reset() {
        ButtonCol_black.setForeground(Color.black);
        ButtonCol_blue.setForeground(Color.black);
        ButtonCol_red.setForeground(Color.black);
        ButtonCol_cyan.setForeground(Color.black);

        ButtonCol_black.setBackground(new Color(150, 150, 150));
        ButtonCol_blue.setBackground(new Color(150, 150, 150));
        ButtonCol_red.setBackground(new Color(150, 150, 150));
        ButtonCol_cyan.setBackground(new Color(150, 150, 150));
    }

    //--------------------------------------------------------
    void Button_h_Col_reset() {
        Button_Col_orange.setBackground(new Color(150, 150, 150));
        Button_Col_yellow.setBackground(new Color(150, 150, 150));
    }
// ---------------------------------------
    
    void Button_kyoutuu_sagyou() {
        es1.setDrawingStage(0);
        es1.set_i_circle_drawing_stage(0);
        es1.set_s_step_iactive(LineSegment.ActiveState.ACTIVE_BOTH_3);//要注意　es1でうっかりs_stepにset.(senbun)やるとアクティヴでないので表示が小さくなる20170507
        es1.vonoroiLines.reset();
    }

    // *******************************************************************************************zzzzzzzzzzzz
    public void i_cp_or_oriagari_decide(Point p) {//A function that determines which of the development and folding views the Ten obtained with the mouse points to.
        //20171216
        //hyouji_flg==2,ip4==0  omote
        //hyouji_flg==2,ip4==1	ura
        //hyouji_flg==2,ip4==2	omote & ura
        //hyouji_flg==2,ip4==3	omote & ura

        //hyouji_flg==3,ip4==0  omote
        //hyouji_flg==3,ip4==1	ura
        //hyouji_flg==3,ip4==2	omote & ura
        //hyouji_flg==3,ip4==3	omote & ura

        //hyouji_flg==5,ip4==0  omote
        //hyouji_flg==5,ip4==1	ura
        //hyouji_flg==5,ip4==2	omote & ura
        //hyouji_flg==5,ip4==3	omote & ura & omote2 & ura2

        //OZ_hyouji_mode=0;  nun
        //OZ_hyouji_mode=1;  omote
        //OZ_hyouji_mode=2;  ura
        //OZ_hyouji_mode=3;  omote & ura
        //OZ_hyouji_mode=4;  omote & ura & omote2 & ura2

        int temp_i_OAZ = 0;
        MouseWheelTarget temp_i_cp_or_oriagari = MouseWheelTarget.CREASEPATTERN_0;
        FoldedFigure OZi;
        for (int i = 1; i <= OAZ.size() - 1; i++) {
            OZi = OAZ.get(i);


            int OZ_display_mode = 0;//No fold-up diagram display
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.WIRE_2) && (OZi.ip4 == FoldedFigure.State.FRONT_0)) {
                OZ_display_mode = 1;
            }//	omote
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.WIRE_2) && (OZi.ip4 == FoldedFigure.State.BACK_1)) {
                OZ_display_mode = 2;
            }//	ura
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.WIRE_2) && (OZi.ip4 == FoldedFigure.State.BOTH_2)) {
                OZ_display_mode = 3;
            }//	omote & ura
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.WIRE_2) && (OZi.ip4 == FoldedFigure.State.TRANSPARENT_3)) {
                OZ_display_mode = 3;
            }//	omote & ura

            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.TRANSPARENT_3) && (OZi.ip4 == FoldedFigure.State.FRONT_0)) {
                OZ_display_mode = 1;
            }//	omote
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.TRANSPARENT_3) && (OZi.ip4 == FoldedFigure.State.BACK_1)) {
                OZ_display_mode = 2;
            }//	ura
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.TRANSPARENT_3) && (OZi.ip4 == FoldedFigure.State.BOTH_2)) {
                OZ_display_mode = 3;
            }//	omote & ura
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.TRANSPARENT_3) && (OZi.ip4 == FoldedFigure.State.TRANSPARENT_3)) {
                OZ_display_mode = 3;
            }//	omote & ura

            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.PAPER_5) && (OZi.ip4 == FoldedFigure.State.FRONT_0)) {
                OZ_display_mode = 1;
            }//	omote
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.PAPER_5) && (OZi.ip4 == FoldedFigure.State.BACK_1)) {
                OZ_display_mode = 2;
            }//	ura
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.PAPER_5) && (OZi.ip4 == FoldedFigure.State.BOTH_2)) {
                OZ_display_mode = 3;
            }//	omote & ura
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.PAPER_5) && (OZi.ip4 == FoldedFigure.State.TRANSPARENT_3)) {
                OZ_display_mode = 4;
            }//	omote & ura & omote2 & ura2

            //temp_i_cp_or_oriagari=0;

            if (OZi.cp_worker2.isInsideFront(p) > 0) {
                if (((OZ_display_mode == 1) || (OZ_display_mode == 3)) || (OZ_display_mode == 4)) {
                    temp_i_cp_or_oriagari = MouseWheelTarget.FOLDED_FRONT_1;
                    temp_i_OAZ = i;
                }
            }

            if (OZi.cp_worker2.isInsideRear(p) > 0) {
                if (((OZ_display_mode == 2) || (OZ_display_mode == 3)) || (OZ_display_mode == 4)) {
                    temp_i_cp_or_oriagari = MouseWheelTarget.FOLDED_BACK_2;
                    temp_i_OAZ = i;
                }
            }

            if (OZi.cp_worker2.isInsideTransparentFront(p) > 0) {
                if (OZ_display_mode == 4) {
                    temp_i_cp_or_oriagari = MouseWheelTarget.TRANSPARENT_FRONT_3;
                    temp_i_OAZ = i;
                }
            }

            if (OZi.cp_worker2.isInsideTransparentRear(p) > 0) {
                if (OZ_display_mode == 4) {
                    temp_i_cp_or_oriagari = MouseWheelTarget.TRANSPARENT_BACK_4;
                    temp_i_OAZ = i;
                }
            }
        }
        i_cp_or_oriagari = temp_i_cp_or_oriagari;

        set_i_OAZ(temp_i_OAZ);
    }


    //=============================================================================
    //マウスのホイールが回転した時に呼ばれるメソッド
    //=============================================================================

    // *******************************************************************************************cccccccccc
    void set_i_OAZ(int i) {//OZが切り替わるときの処理
        System.out.println("i_OAZ = " + i_OAZ);
        i_OAZ = i;
        OZ = OAZ.get(i_OAZ);
        //透過図はカラー化しない。
        ckbox_toukazu_color.setSelected(OZ.transparencyColor);//透過図はカラー化。
    }


    //----------------------------------------------------------------------
    //マウス操作(移動やボタン操作)を行う関数------------------------------
    //----------------------------------------------------------------------

    public Point e2p(MouseEvent e) {

        double d_width = 0.0;
        if (ckbox_ten_hanasi.isSelected()) {
            d_width = camera_of_orisen_input_diagram.getCameraZoomX() * es1.get_d_decision_width();
        }
        return new Point(e.getX() - (int) d_width, e.getY() - (int) d_width);
    }


//i_mouse_modeA;マウスの動作に対する反応を規定する。
// -------------1;線分入力モード。
//2;展開図調整(移動)。
//3;線分削除モード
//4;線分_chan"

// -------------5;線分延長モード。
// -------------6;2点から等距離線分モード。
// -------------7;角二等分線モード。
// -------------8;内心モード。
// -------------9;垂線おろしモード。
// -------------10;折り返しモード。
// -------------11;線分入力モード。
// -------------12;鏡映モード。
// -------------13;15度入力モード。


//101:折り上がり図の操作。
//102;F_move
//103;S_face

//10001;test1 入力準備として点を３つ指定する

    public void mouse_object_position(Point p) {//この関数はmouseMoved等と違ってマウスイベントが起きても自動では認識されない
        p_mouse_TV_position.set(p.getX(), p.getY());

        p_mouse_object_position.set(camera_of_orisen_input_diagram.TV2object(p_mouse_TV_position));
    }


    // --------------------------------------------------

    // ------------------------------------------------------
    public void background_set(Point t1, Point t2, Point t3, Point t4) {
        h_cam.set_h1(t1);
        h_cam.set_h2(t2);
        h_cam.set_h3(t3);
        h_cam.set_h4(t4);

        h_cam.parameter_calculation();
    }

    // ------------------------------------------------------
    public void drawBackground(Graphics2D g2h, Image imgh) {//引数はカメラ設定、線幅、画面X幅、画面y高さ
        //背景画を、画像の左上はしを、ウィンドウの(0,0)に合わせて回転や拡大なしで表示した場合を基準状態とする。
        //背景画上の点h1を中心としてa倍拡大する。次に、h1を展開図上の点h3と重なるように背景画を平行移動する。
        //この状態の展開図を、h3を中心にb度回転したよう見えるように座標を回転させて貼り付けて、その後、座標の回転を元に戻すという関数。
        //引数は、Graphics2D g2h,Image imgh,Ten h1,Ten h2,Ten h3,Ten h4
        //h2,とh4も重なるようにする
        //

        //最初に

        //if(lockBackground>=10){lockBackground=lockBackground-10;}
        if (lockBackground) {
            h_cam.setCamera(camera_of_orisen_input_diagram);
            h_cam.h3_and_h4_calculation();
            h_cam.parameter_calculation();
        }

        AffineTransform at = new AffineTransform();
        at.rotate(h_cam.getAngle() * Math.PI / 180.0, h_cam.get_cx(), h_cam.get_cy());
        g2h.setTransform(at);


        g2h.drawImage(imgh, h_cam.get_x0(), h_cam.get_y0(), h_cam.get_x1(), h_cam.get_y1(), this);

        //g2h.drawImage(imgh,kaisi_x,kaisi_y,this);//hx0,hy0,は描画開始位置

        at.rotate(-h_cam.getAngle() * Math.PI / 180.0, h_cam.get_cx(), h_cam.get_cy());
        g2h.setTransform(at);

    }

    void configure_syokika_yosoku() {
        OZ.text_result = "";
        OZ.displayStyle = FoldedFigure.DisplayStyle.NONE_0;//折り上がり図の表示様式の指定。１なら実際に折り紙を折った場合と同じ。２なら透過図
        OZ.display_flg_backup = FoldedFigure.DisplayStyle.NONE_0;//表示様式hyouji_flgの一時的バックアップ用

        //表示用の値を格納する変数
        OZ.ip1_anotherOverlapValid = -1;//上下表職人の初期設定時に、折った後の表裏が同じ面が
        //隣接するという誤差があれが0を、無ければ1000を格納する変数。
        //ここでの初期値は(0か1000)以外の数ならなんでもいい。
        OZ.ip2_possibleOverlap = -1;//上下表職人が折り畳み可能な重なり方を探した際に、
        //可能な重なり方がなければ0を、可能な重なり方があれば1000を格納する変数。
        //ここでの初期値は(0か1000)以外の数ならなんでもいい。
        OZ.ip3 = 1;//ts1が折り畳みを行う際の基準面を指定するのに使う。

        //ip4=0;//これは、ts1の最初に裏返しをするかどうかを指定する。0ならしない。1なら裏返す。//20170615 実行しないようにした（折りあがり図の表示状況を変えないようにするため）

        OZ.ip5 = -1;    //上下表職人が一旦折り畳み可能な紙の重なりを示したあとで、
        //さらに別の紙の重なりをさがす時の最初のjs.susumu(SubFaceTotal)の結果。
        //0なら新たにsusumu余地がなかった。0以外なら変化したSubFaceのidの最も小さい番号
        OZ.ip6 = -1;    //上下表職人が一旦折り畳み可能な紙の重なりを示したあとで、
        //さらに別の紙の重なりをさがす時の js.kanou_kasanari_sagasi()の結果。
        //0なら可能な重なりかたとなる状態は存在しない。
        //1000なら別の重なり方が見つかった。

        OZ.findAnotherOverlapValid = false;     //これは「別の重なりを探す」ことが有効の場合は１、無効の場合は０をとる。
        OZ.discovered_fold_cases = 0;    //折り重なり方で、何通り発見したかを格納する。

        i_mouseDragged_valid = false;
        i_mouseReleased_valid = false;//0は、マウス操作を無視。1はマウス操作有効。ファイルボックスのon-offなどで、予期せぬmouseDraggedやmouseReleasedが発生したとき、それを拾わないように0に設定する。これらは、マウスがクリックされたときに、1有効指定にする。

        OZ.estimated_initialize();
        bulletinBoard.clear();
    }

    ////b* アプリケーション用。先頭が／＊／／／で始まる行にはさまれた部分は無視される。
    void readImageFromFile() {
        FileDialog fd = new FileDialog(this, "Select Image File.", FileDialog.LOAD);
        fd.setVisible(true);
        img_background_fname = fd.getDirectory() + fd.getFile();
        boolean iDisplayBackground_old;
        iDisplayBackground_old = displayBackground;
        try {
            if (fd.getFile() != null) {
                Toolkit tk = Toolkit.getDefaultToolkit();
                img_background = tk.getImage(img_background_fname);

                if (img_background != null) {
                    displayBackground = true;
                    Button_background_kirikae.setBackground(Color.ORANGE);
                    lockBackground = false;
                    lockBackground_ori = false;
                    Button_background_Lock_on.setBackground(Color.gray);
                }
            }

        } catch (Exception e) {
            displayBackground = iDisplayBackground_old;
            if (!displayBackground) {
                Button_background_kirikae.setBackground(Color.gray);
            }
        }
    }

    void writeImage() {
        // String String fname_wi
        fname_wi = selectFileName("file name for Img save");
        flg61 = false;
        if ((i_mouse_modeA == MouseMode.OPERATION_FRAME_CREATE_61) && (es1.getDrawingStage() == 4)) {
            flg61 = true;
            es1.setDrawingStage(0);
        }

        if (fname_wi != null) {
            canvas.flg_wi = true;
            canvas.repaint();//Necessary to not export the green border
        }
    }

    //---------------------------------------------------------
    String selectFileName(String coment0) {
        FileDialog fd = new FileDialog(this, coment0, FileDialog.SAVE);
        fd.setVisible(true);
        String fname = null;
        if (fd.getFile() != null) {
            fname = fd.getDirectory() + fd.getFile();
        }
        return fname;
    }

    //---------------------------------------------------------

    // -----------------------------------mmmmmmmmmmmmmm-------
    void writeImageFile(String fname) {//i=1　png, 2=jpg
        if (fname != null) {
            int i = 1;

            if (fname.endsWith("svg")) {
                Memo memo1;
                memo1 = es1.getMemo_for_svg_export_with_camera(displayComments, displayCpLines, displayAuxLines, displayLiveAuxLines, fLineWidth, lineStyle, f_h_lineWidth, dim.width, dim.height, displayMarkings);//渡す情報はカメラ設定、線幅、画面X幅、画面y高さ,展開図動かし中心の十字の目印の表示

                Memo memo2 = new Memo();

                //各折り上がりのmemo
                FoldedFigure OZi;
                for (int i_oz = 1; i_oz <= OAZ.size() - 1; i_oz++) {
                    OZi = OAZ.get(i_oz);

                    memo2.addMemo(OZi.getMemo_for_svg_export());
                }

                memoAndName2File(FileFormatConverter.orihime2svg(memo1, memo2), fname);
                return;
            } else if (fname.endsWith("png")) {
                i = 1;
            } else if (fname.endsWith("jpg")) {
                i = 2;
            } else {
                fname = fname + ".png";
                i = 1;
            }

            dim = canvas.getSize();

            //	ファイル保存

            try {
                if (flg61) { //枠設定時の枠内のみ書き出し 20180524
                    int xmin = (int) es1.operationFrameBox.getXMin();
                    int xmax = (int) es1.operationFrameBox.getXMax();
                    int ymin = (int) es1.operationFrameBox.getYMin();
                    int ymax = (int) es1.operationFrameBox.getYMax();

                    if (i == 1) {
                        ImageIO.write(canvas.offscreen.getSubimage(xmin, ymin, xmax - xmin + 1, ymax - ymin + 1), "png", new File(fname));
                    }
                    if (i == 2) {
                        ImageIO.write(canvas.offscreen.getSubimage(xmin, ymin, xmax - xmin + 1, ymax - ymin + 1), "jpg", new File(fname));
                    }

                } else {//枠無しの場合の全体書き出し
                    System.out.println("2018-529_");
                    if (i == 1) {
                        ImageIO.write(canvas.offscreen.getSubimage(upperLeft_ix, upperLeft_iy, dim.width - lowerRight_ix - upperLeft_ix, dim.height - lowerRight_iy - upperLeft_iy), "png", new File(fname));
                    }
                    if (i == 2) {
                        ImageIO.write(canvas.offscreen.getSubimage(upperLeft_ix, upperLeft_iy, dim.width - lowerRight_ix - upperLeft_ix, dim.height - lowerRight_iy - upperLeft_iy), "jpg", new File(fname));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("終わりました");

        }

    }

    void updateExplanation() {
        URL url = getClass().getClassLoader().getResource(img_explanation_fname);

        try {
            Toolkit tk = Toolkit.getDefaultToolkit();
            img_explanation = tk.getImage(url);
        } catch (Exception e) {
            System.out.println(e);
        }
        canvas.repaint();
    }

    //-------------------
    Memo readFile2Memo() {
        String fname = "";
        Memo memo_temp = new Memo();

        int file_ok = 0;//1 if the extension of the read file name is appropriate (orh, obj, cp), 0 otherwise

        FileDialog fd = new FileDialog(this, "Open file", FileDialog.LOAD);
        fd.setFile("*.orh;*.obj;*.cp");
        fd.setFilenameFilter((dir, name) -> name.endsWith(".orh") || name.endsWith(".obj") || name.endsWith(".cp"));
        fd.setVisible(true);

        if (fd.getFile() == null) {
            return memo_temp;
        }

        fname = fd.getDirectory() + fd.getFile();

        if (fname.endsWith(".orh")) {
            file_ok = 1;
        }
        if (fname.endsWith(".obj")) {
            file_ok = 1;
        }
        if (fname.endsWith(".cp")) {
            file_ok = 1;
        }

        if (file_ok == 0) {
            return memo_temp;
        }

        frame_title = frame_title_0 + "        " + fd.getFile();
        setTitle(frame_title);
        es1.setTitle(frame_title);

        try {
            if (fd.getFile() != null) {  //キャンセルではない場合。
                BufferedReader br = new BufferedReader(new FileReader(fname));

                String rdata;

                memo1.reset();
                while ((rdata = br.readLine()) != null) {
                    memo_temp.addLine(rdata);
                }
                br.close();
            }
        } catch (Exception e) {
            System.out.println(e);
            frame_title = frame_title_0 + "        " + "X";
            setTitle(frame_title);
            es1.setTitle(frame_title);
        }

        if (fname.endsWith("obj")) {
            System.out.println("objファイル読みこみ");
            return FileFormatConverter.obj2orihime(memo_temp);
        }
        if (fname.endsWith("cp")) {
            System.out.println("cpファイル読みこみ");
            return FileFormatConverter.cp2orihime(memo_temp);
        }
        return memo_temp;
    }

    void writeMemo2File() {
        Memo memo1;
        memo1 = es1.getMemo_for_export();
        String fname = selectFileName("書き出しファイルの名前");

        if (fname != null) {
            if (fname.endsWith("cp")) {
                memoAndName2File(FileFormatConverter.orihime2cp(memo1), fname);

                frame_title = frame_title_0 + "        " + fd.getFile();
                setTitle(frame_title);
                es1.setTitle(frame_title);

            } else if (fname.endsWith("orh")) {
                memoAndName2File(memo1, fname);

                frame_title = frame_title_0 + "        " + fd.getFile();
                setTitle(frame_title);
                es1.setTitle(frame_title);

            } else {
                fname = fname + ".orh";
                memoAndName2File(memo1, fname);

                frame_title = frame_title_0 + "        " + fd.getFile() + ".orh";
                setTitle(frame_title);
                es1.setTitle(frame_title);
            }
        }
    }

    void memoAndName2File(Memo memo1, String fname) {
        System.out.println("ファイル書きこみ");
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fname)));
            for (int i = 1; i <= memo1.getLineCount(); i++) {
                pw.println(memo1.getLine(i));
            }
            pw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void folding_estimated() {
        OZ.folding_estimated(camera_of_orisen_input_diagram, Ss0);
    }

////b* アプリケーション用。先頭が／＊／／／で始まる行にはさまれた部分は無視される

    void folding_settings_two_color() {//２色塗りわけ展開図
        OZ.folding_settings_two_color(camera_of_orisen_input_diagram, Ss0);
    }

    void mks() {
        sub = new SubThread(this);
    }

    public double String2double(String str0, double default_if_error) {
        String new_str0 = str0.trim();
        if (new_str0.equals("L1")) {
            str0 = String.valueOf(es1.get_L1());
        }
        if (new_str0.equals("L2")) {
            str0 = String.valueOf(es1.get_L2());
        }
        if (new_str0.equals("A1")) {
            str0 = String.valueOf(es1.get_A1());
        }
        if (new_str0.equals("A2")) {
            str0 = String.valueOf(es1.get_A2());
        }
        if (new_str0.equals("A3")) {
            str0 = String.valueOf(es1.get_A3());
        }

        return StringOp.String2double(str0, default_if_error);
    }

    public void check4(double r) {
        d_ap_check4 = r;
        if (!subThreadRunning) {
            subThreadMode = SubThread.Mode.CHECK_CAMV_3;//3=頂点周りの折畳み可能性判定、1=折畳み推定の別解をまとめて出す。0=折畳み推定の別解をまとめて出すモードではない。この変数はサブスレッドの動作変更につかうだけ。20170611にVer3.008から追加

            subThreadRunning = true;
            mks();//Create a new thread
            sub.start();
        } else {
            if (subThreadMode == SubThread.Mode.CHECK_CAMV_3) {
                sub.stop();
                mks();//Create a new thread
                sub.start();
            }
        }
    }

    public enum AngleSystemInputType {
        DEG_1,
        DEG_2,
        DEG_3,
        DEG_4,
        DEG_5,
    }

    public enum SelectionOperationMode {
        NORMAL_0,
        MOVE_1,
        MOVE4P_2,
        COPY_3,
        COPY4P_4,
        MIRROR_5,
    }
}
