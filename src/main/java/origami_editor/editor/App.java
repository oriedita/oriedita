package origami_editor.editor;

import origami_editor.editor.component.UndoRedo;
import origami_editor.editor.drawing_worker.Drawing_Worker;
import origami_editor.editor.folded_figure.FoldedFigure;
import origami_editor.editor.folded_figure.FoldedFigure_01;
import origami_editor.editor.hierarchylist_worker.HierarchyList_Worker;
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
    public JTextField foldedFigureSizeTextField;//double d_oriagarizu_syukusyaku_keisuu=1.0;//折り上がり図の縮尺係数
    public JTextField foldedFigureRotateTextField;
    public JCheckBox ckbox_mouse_settings;//マウスの設定。チェックがあると、ホイールマウスとして動作設定
    public JCheckBoxMenuItem ckbox_point_search;//点を探す範囲
    public JCheckBoxMenuItem ckbox_ten_hanasi;//点を離すかどうか
    public JCheckBoxMenuItem ckbox_kou_mitudo_nyuuryoku;//高密度用入力をするかどうか
    public JCheckBoxMenuItem ckbox_bun;//文章
    public JCheckBoxMenuItem ckbox_cp;//折線
    public JCheckBoxMenuItem ckbox_a0;//補助活線cyan
    public JCheckBoxMenuItem ckbox_a1;//補助画線
    public JCheckBox ckbox_check4;//check4
    public JCheckBoxMenuItem ckbox_mark;//Marking lines such as crosses and reference planes
    public JCheckBoxMenuItem ckbox_cp_ue;//展開図を折り上がり予想図の上に描く
    public JCheckBox ckbox_folding_keika;//Writing out the progress of the folding forecast
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
    public SubThread sub;
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
    int id_angle_system_a = 12;//角度系の180度を割る数の格納_a
    int id_angle_system_b = 8;//Storage of numbers that divide the angle system by 180 degrees_b
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
    JButton Button_to_mountain;                    //元がどんな種類の折線でも、山折りにする
    JButton Buton_to_valley;                    //元がどんな種類の折線でも、谷折りにする
    JButton Button_to_edge;                    //元がどんな種類の折線でも、境界線もしくは山谷未設定線にする
    JButton Button_to_aux_live;                    //元がどんな種類の折線でも、補助活線にする
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
    JTextField polygonSizeTextField;
    int numPolygonCorners = 5;
    UndoRedo undoRedo;
    int i_undo_suu = 20;//text31はtext10を参考にしている
    JTextField h_undoTotalTextField;
    int i_h_undo_suu = 20;
    JTextField angleATextField;
    double d_restricted_angle_a = 40.0;
    JTextField angleBTextField;
    double d_restricted_angle_b = 60.0;
    JTextField angleCTextField;
    double d_restricted_angle_c = 80.0;
    JTextField andleDTextField;
    double d_restricted_angle_d = 30.0;
    JTextField angleETextField;
    double d_restricted_angle_e = 50.0;
    JTextField angleFTextField;
    double d_restricted_angle_f = 100.0;
    JTextField text26;
    int foldedCases = 1;//Specify the number of folding estimation to be displayed
    JTextField text27;
    double scaleFactor = 1.0;//Scale factor
    JTextField text28;
    double rotationCorrection = 0.0;//Correction angle of rotation display angle
    UndoRedo foldedFigureUndoRedo;
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
    JLabel label_length_sokutei_1;
    JLabel label_length_sokutei_2;
    JLabel label_kakudo_sokutei_1;
    // バッファー画面用設定はここまでAAAAAAAAAAAAAAAAAAA
    JLabel label_kakudo_sokutei_2;
    JLabel label_kakudo_sokutei_3;
    Image img_background;       //Image for background
    String img_background_fname;
    Image img_explanation;       //Image for explanation
    // Buffer screen settings VVVVVVVVVVVVVVVVVVVVVVVVV
    public Canvas canvas;
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
    boolean showAddFrame = false;//1=add_frameが存在する。,0=存在しない。
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
            setHelp("qqq/yomi.png");

            Button_shared_operation();

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
            setHelp("qqq/kaki.png");
            Button_shared_operation();
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


// ------61;長方形内選択モード。ここまで




// ******************************************************************************
////b* アプリケーション用。先頭が／＊／／／で始まる行にはさまれた部分は無視される。
// ******************************************************************************


// ******************************************************************************

// ******北************************************************************************表示するものの選択

//ここからチェックボックスの連続

        JMenu menu_view = new JMenu("View");
        menu_view.setMnemonic('V');

        menuBar.add(menu_view);




// -------------------------------------------------------------------
//点探し
        ckbox_point_search = new JCheckBoxMenuItem("Show point range");
        ckbox_point_search.addActionListener(e -> {
            setHelp("qqq/ckbox_ten_sagasi.png");
            canvas.repaint();
        });
        menu_view.add(ckbox_point_search);

// -------------------------------------------------------------------
//点離し
        ckbox_ten_hanasi = new JCheckBoxMenuItem("Offset cursor");
        ckbox_ten_hanasi.addActionListener(e -> {
            setHelp("qqq/ckbox_ten_hanasi.png");

            canvas.repaint();
        });
        menu_view.add(
                ckbox_ten_hanasi);
// -------------------------------------------------------------------
//高密度入力
        ckbox_kou_mitudo_nyuuryoku = new JCheckBoxMenuItem("Grid input assist");
        ckbox_kou_mitudo_nyuuryoku.addActionListener(e -> {
            setHelp("qqq/ckbox_kou_mitudo_nyuuryoku.png");

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
            setHelp("qqq/ckbox_bun.png");
            canvas.repaint();
        });
        menu_view.add(ckbox_bun);
// -------------------------------------------------------------------
//折線表示
        ckbox_cp = new JCheckBoxMenuItem("Display cp lines");
        ckbox_cp.addActionListener(e -> {
            setHelp("qqq/ckbox_cp.png");
            canvas.repaint();
        });
        menu_view.add(ckbox_cp);
// -------------------------------------------------------------------
//補助活線表示
        ckbox_a0 = new JCheckBoxMenuItem("Display aux lines");
        ckbox_a0.addActionListener(e -> {
            setHelp("qqq/ckbox_a0.png");
            canvas.repaint();
        });
        menu_view.add(ckbox_a0);
// -------------------------------------------------------------------
//補助画線表示
        ckbox_a1 = new JCheckBoxMenuItem("Display live aux lines");
        ckbox_a1.addActionListener(e -> {
            setHelp("qqq/ckbox_a1.png");
            canvas.repaint();
        });
        menu_view.add(ckbox_a1);
// -------------------------------------------------------------------
//十字や基準面などの目印画線
        ckbox_mark = new JCheckBoxMenuItem("Display standard face marks");
        ckbox_mark.addActionListener(e -> {
            setHelp("qqq/ckbox_mejirusi.png");

            canvas.repaint();
        });

        ckbox_mark.setMargin(new Insets(0, 0, 0, 0));
        menu_view.add(
                ckbox_mark);

// -------------------------------------------------------------------
//折りあがり図を補助線の手前側にするかどうか
        ckbox_cp_ue = new JCheckBoxMenuItem("Crease pattern on top");
        ckbox_cp_ue.addActionListener(e -> {
            setHelp("qqq/ckbox_cp_ue.png");

            canvas.repaint();
        });

        ckbox_cp_ue.setMargin(new Insets(0, 0, 0, 0));
        menu_view.add(ckbox_cp_ue);

// -------------------------------------------------------------------
//折り畳み経過の表示
        ckbox_folding_keika = new JCheckBox("");
        ckbox_folding_keika.addActionListener(e -> {
            setHelp("qqq/ckbox_oritatami_keika.png");

            canvas.repaint();
        });
        ckbox_folding_keika.setIcon(createImageIcon(
                "ppp/ckbox_oritatami_keika_off.png"));
        ckbox_folding_keika.setSelectedIcon(createImageIcon(
                "ppp/ckbox_oritatami_keika_on.png"));

        ckbox_folding_keika.setMargin(new Insets(0, 0, 0, 0));

        NorthPanel northPanel = new NorthPanel(this);
        contentPane.add("North", northPanel); //Frame用

        ckbox_mouse_settings = northPanel.getMouseSettingsCheckBox();

        text3 = northPanel.getRatioATextField();
        text4 = northPanel.getRatioBTextField();
        text5 = northPanel.getRatioCTextField();
        text6 = northPanel.getRatioDTextField();
        text7 = northPanel.getRatioETextField();
        text8 = northPanel.getRatioFTextField();

        text27 = northPanel.getTenkaizu_syukusyouTextField();

        text28 = northPanel.getTenkaizu_kaitenTextField();
        Button_background_kirikae = northPanel.getBackgroundToggleButton();
        Button_background_Lock_on = northPanel.getBackgroundLockButton();

// ******************************************************************************
////b* アプリケーション用。先頭が／＊／／／で始まる行にはさまれた部分は無視される。
// ******************************************************************************

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        menuBar.add(helpMenu);

// *******北*********************************************************************** 解説
        JMenuItem toggleHelpMenuItem = new JMenuItem("Toggle help");
        toggleHelpMenuItem.addActionListener(e -> {
            displayExplanation = !displayExplanation;

            i_mouseDragged_valid = false;
            i_mouseReleased_valid = false;

            canvas.repaint();
        });
        toggleHelpMenuItem.setMargin(new Insets(0, 0, 0, 0));
        helpMenu.add(toggleHelpMenuItem);

        toggleHelpMenuItem.setMargin(new Insets(0, 0, 0, 0));

        WestPanel westPanel = new WestPanel(this);

        contentPane.add("West", westPanel);

        undoRedo = westPanel.getUndoRedo();

        ButtonCol_red = westPanel.getColRedButton();
        ButtonCol_blue = westPanel.getColBlueButton();
        ButtonCol_black = westPanel.getColBlackButton();
        ButtonCol_cyan = westPanel.getColCyanButton();

        text2 = westPanel.getLineSegmentDivisionTextField();

        Button_move = westPanel.getMoveButton();

        Button_move_2p2p = westPanel.getMove2p2pButton();

        Button_copy_paste = westPanel.getCopyButton();

        Button_copy_paste_2p2p = westPanel.getCopy2p2pButton();

        Button_kyouei = westPanel.getKyoueiButton();

        Button_to_mountain = westPanel.getM_nisuruButton();

        Buton_to_valley = westPanel.getV_nisuruButton();

        Button_to_edge = westPanel.getE_nisuruButton();

        Button_to_aux_live = westPanel.getHK_nisuruButton();

        Button_senbun_henkan2 = westPanel.getSenbun_henkan2Button();

        Button_grid_decrease = westPanel.getGridSizeDecreaseButton();

        text1 = westPanel.getGridSizeTextField();

        Button_grid_increase = westPanel.getGridSizeIncreaseButton();

        text25 = westPanel.getIntervalGridSizeTextField();

        text21 = westPanel.getGridYATextField();
        text22 = westPanel.getGridYBTextField();
        text23 = westPanel.getGridYCTextField();
        text18 = westPanel.getGridXATextField();
        text19 = westPanel.getGridXBTextField();
        text20 = westPanel.getGridXCTextField();

        text24 = westPanel.getGridAngleTextField();

        EastPanel eastPanel = new EastPanel(this);
        contentPane.add("East", eastPanel);

        ckbox_check4 = eastPanel.getcAMVCheckBox();
        angleATextField = eastPanel.getAngleATextField();
        angleBTextField = eastPanel.getAngleBTextField();
        angleCTextField = eastPanel.getAngleCTextField();
        andleDTextField = eastPanel.getAngleDTextField();
        angleETextField = eastPanel.getAngleETextField();
        angleFTextField = eastPanel.getAngleFTextField();
        polygonSizeTextField = eastPanel.getPolygonSizeTextField();
        Button_sen_tokutyuu_color = eastPanel.getC_colButton();
        h_undoTotalTextField = eastPanel.getH_undoTotalTextField();
        Button_Col_orange = eastPanel.getColOrangeButton();
        Button_Col_yellow = eastPanel.getColYellowButton();
        label_length_sokutei_1 = eastPanel.getL1Label();
        label_length_sokutei_2 = eastPanel.getL2Label();
        label_kakudo_sokutei_1 = eastPanel.getA1Label();
        label_kakudo_sokutei_2 = eastPanel.getA2Label();
        label_kakudo_sokutei_3 = eastPanel.getA3Label();

        ckbox_cp_kaizen_folding = westPanel.getCkbox_cp_kaizen_folding();

        ckbox_select_nokosi = westPanel.getCkbox_select_nokosi();

        ckbox_toukazu_color = westPanel.getCkbox_toukazu_color();

        // Paste the bottom (south side) panel into the layout
        SouthPanel southPanel = new SouthPanel(this);

        contentPane.add("South", southPanel); //Frame用

        Button_AS_matome = southPanel.getAs100Button();
        text26 = southPanel.getGoToFoldedFigureTextField();
        Button_bangou_sitei_estimated_display = southPanel.getGoToFoldedFigureButton();
        Button_another_solution = southPanel.getAnotherSolutionButton();
        foldedFigureUndoRedo = southPanel.getUndoRedo();
        foldedFigureSizeTextField = southPanel.getFoldedFigureResizeTextField();
        foldedFigureRotateTextField = southPanel.getFoldedFigureRotateTextField();
        Button_F_color = southPanel.getFCButton();
        Button_B_color = southPanel.getBCButton();
        Button_L_color = southPanel.getLCButton();

// *******南*********ボタンの定義はここまで*******************************************************************************************************************************

        //展開図の初期化　開始
        //settei_syokika_cp();//展開図パラメータの初期化
        developmentView_initialization();
        //展開図の初期化　終了

        i_undo_suu = 20;
        undoRedo.setText(String.valueOf(i_undo_suu));
        i_undo_suu_om = 5;
        foldedFigureUndoRedo.setText(String.valueOf(i_undo_suu_om));
        i_h_undo_suu = 20;
        h_undoTotalTextField.setText(String.valueOf(i_h_undo_suu));
        scale_interval = 5;
        text25.setText(String.valueOf(scale_interval));
        es1.set_a_to_parallel_scale_interval(scale_interval);
        es1.set_b_to_parallel_scale_interval(scale_interval);

        selectionOperationMode = SelectionOperationMode.MOVE_1;
        Button_sel_mou_wakukae();//セレクトされた折線がある状態で、セレクトされている折線の頂点をクリックした場合の動作モードの初期設定

        //折畳予測図のの初期化　開始
        configure_syokika_yosoku();
        //折畳予測図のの初期化　終了

        Button_shared_operation();

        canvas.repaint();

        setHelp("qqq/a__hajimeni.png");

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

    public FoldType getFoldType() {

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

    public void oritatame(FoldType i_fold_type, FoldedFigure.EstimationOrder i_suitei_meirei) {//引数の意味は(i_fold_type , i_suitei_meirei)
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
        foldedFigureSizeTextField.setText(String.valueOf(OZ.d_foldedFigure_scale_factor));//折り上がり図の縮尺係数
        OZ.d_foldedFigure_rotation_correction = 0.0;
        foldedFigureRotateTextField.setText(String.valueOf(OZ.d_foldedFigure_rotation_correction));//折り上がり図の回転表示角度の補正角度


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
        angleATextField.setText(String.valueOf(d_restricted_angle_a));
        d_restricted_angle_b = 60.0;
        angleBTextField.setText(String.valueOf(d_restricted_angle_b));
        d_restricted_angle_c = 80.0;
        angleCTextField.setText(String.valueOf(d_restricted_angle_c));

        d_restricted_angle_a = 30.0;
        andleDTextField.setText(String.valueOf(d_restricted_angle_d));
        d_restricted_angle_b = 50.0;
        angleETextField.setText(String.valueOf(d_restricted_angle_e));
        d_restricted_angle_c = 100.0;
        angleFTextField.setText(String.valueOf(d_restricted_angle_f));

        //多角形の角数
        numPolygonCorners = 5;
        polygonSizeTextField.setText(String.valueOf(numPolygonCorners));

        //補助画線の色
        h_icol = LineColor.ORANGE_4;
        es1.h_setcolor(h_icol);                                        //最初の補助線の色を指定する。4はオレンジ、7は黄。
        Button_h_Col_reset();
        Button_Col_orange.setForeground(Color.black);
        Button_Col_orange.setBackground(Color.ORANGE);    //補助線のボタンの色設定

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
        if (showAddFrame) {
            System.out.println("111 showAddFrame=" + showAddFrame);
            add_frame.dispose();
            add_frame = new OpenFrame("add_frame", this);
        } else {
            System.out.println("000 showAddFrame=" + showAddFrame);
            add_frame = new OpenFrame("add_frame", this);
        }
        showAddFrame = true;
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

        d_restricted_angle_a = String2double(angleATextField.getText(), d_restricted_angle_a_old);
        d_restricted_angle_b = String2double(angleBTextField.getText(), d_restricted_angle_b_old);
        d_restricted_angle_c = String2double(angleCTextField.getText(), d_restricted_angle_c_old);

        angleATextField.setText(String.valueOf(d_restricted_angle_a));
        angleBTextField.setText(String.valueOf(d_restricted_angle_b));
        angleCTextField.setText(String.valueOf(d_restricted_angle_c));

        es1.set_d_restricted_angle(d_restricted_angle_a, d_restricted_angle_b, d_restricted_angle_c);
    }

    public void set_restricted_angle_def() { //このdefは「定義」と言う意味ではなく、dとeとfを扱うという意味
        double d_restricted_angle_d_old = d_restricted_angle_d;
        double d_restricted_angle_e_old = d_restricted_angle_e;
        double d_restricted_angle_f_old = d_restricted_angle_f;

        d_restricted_angle_d = String2double(andleDTextField.getText(), d_restricted_angle_d_old);
        d_restricted_angle_e = String2double(angleETextField.getText(), d_restricted_angle_e_old);
        d_restricted_angle_f = String2double(angleFTextField.getText(), d_restricted_angle_f_old);

        andleDTextField.setText(String.valueOf(d_restricted_angle_d));
        angleETextField.setText(String.valueOf(d_restricted_angle_e));
        angleFTextField.setText(String.valueOf(d_restricted_angle_f));

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
        Button_to_mountain.setForeground(Color.black);
        Buton_to_valley.setForeground(Color.black);
        Button_to_edge.setForeground(Color.black);
        Button_to_aux_live.setForeground(Color.black); //HKとは補助活線のこと
        Button_senbun_henkan2.setForeground(Color.black);

        Button_to_mountain.setBackground(Color.white);
        Buton_to_valley.setBackground(Color.white);
        Button_to_edge.setBackground(Color.white);
        Button_to_aux_live.setBackground(Color.white);
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
    
    public void Button_shared_operation() {
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
        OZ.ip1_anotherOverlapValid = HierarchyList_Worker.HierarchyListStatus.UNKNOWN_N1;//上下表職人の初期設定時に、折った後の表裏が同じ面が
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
    
    public void setHelp(String resource) {
        URL url = getClass().getClassLoader().getResource(resource);

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
