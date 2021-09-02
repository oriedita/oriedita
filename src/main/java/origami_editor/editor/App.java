package origami_editor.editor;

import origami_editor.editor.component.UndoRedo;
import origami_editor.editor.drawing_worker.DrawingWorker;
import origami_editor.editor.folded_figure.FoldedFigure;
import origami_editor.editor.folded_figure.FoldedFigure_01;
import origami_editor.editor.hierarchylist_worker.HierarchyList_Worker;
import origami_editor.graphic2d.linesegment.LineSegment;
import origami_editor.graphic2d.point.Point;
import origami_editor.record.memo.Memo;
import origami_editor.record.string_op.StringOp;
import origami_editor.tools.background_camera.Background_camera;
import origami_editor.tools.bulletinboard.BulletinBoard;
import origami_editor.tools.camera.Camera;
import origami_editor.tools.linestore.LineSegmentSet;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

import static origami_editor.editor.ResourceUtil.createImageIcon;

public class App extends JFrame implements ActionListener {
    private final NorthPanel northPanel;
    private final EastPanel eastPanel;
    private final SouthPanel southPanel;
    private final WestPanel westPanel;
    private final AppMenuBar appMenuBar;
    public FoldedFigure temp_OZ = new FoldedFigure(this);    //Folded figure
    public FoldedFigure OZ;    //Current Folded figure
    public LineSegmentSet Ss0;//折畳み予測の最初に、ts1.Senbunsyuugou2Tensyuugou(Ss0)として使う。　Ss0は、mainDrawingWorker.get_for_oritatami()かes1.get_for_select_oritatami()で得る。
    public BulletinBoard bulletinBoard = new BulletinBoard(this);
    public Camera camera_of_orisen_input_diagram = new Camera();
    public JButton Button_F_color;                    //折り上がり図の表の色の指定に用いる
    public JButton Button_B_color;                    //折り上がり図の裏の色の指定に用いる
    public JButton Button_L_color;                    //折り上がり図の線の色の指定に用いる
    public Color circleCustomizedColor = new Color(100, 200, 200);//Designated color when customizing the color of auxiliary lines and circles
    public JButton circleCustomizedColorButton;                    //折り上がり図の表の色の指定に用いる

    //アプレット用public void init()または、アプリケーション用public ap() 以外のクラスでも使用されるパネルの部品の宣言はここでしておく。
    //アプレット用public void init()または、アプリケーション用public ap() の中だけで使用されるパネルの部品の宣言ぅラスの中でする。
    //Those that basically change the appearance of the parts are declared here.
    public JTextField foldedFigureSizeTextField;//double d_oriagarizu_syukusyaku_keisuu=1.0;//折り上がり図の縮尺係数
    public JTextField foldedFigureRotateTextField;
    public JCheckBox ckbox_check4;//check4
    public JCheckBox correctCpBeforeFoldingCheckBox;//cpを折畳み前に自動改善する。
    public JCheckBox selectPersistentCheckBox;//select状態を他の操作をしてもなるべく残す
    public JCheckBox ckbox_toukazu_color;//透過図をカラー化する。
    public MouseMode mouseMode = MouseMode.FOLDABLE_LINE_DRAW_71;//Defines the response to mouse movements. If it is 1, the line segment input mode. If it is 2, adjust the development view (move). If it is 101, operate the folded figure.
    public SelectionOperationMode selectionOperationMode;//Specify which operation to perform when selecting and operating the mouse. It is used to select a selected point after selection and automatically switch to the mouse operation that is premised on selection.
    // ------------------------------------------------------------------------
    public Point point_of_referencePlane_old = new Point(); //ten_of_kijyunmen_old.set(OZ.ts1.get_ten_of_kijyunmen_tv());//20180222折り線選択状態で折り畳み推定をする際、以前に指定されていた基準面を引き継ぐために追加
    public SubThread sub;
    // Buffer screen settings VVVVVVVVVVVVVVVVVVVVVVVVV
    public Canvas canvas;
    // *******************************************************************************************************
    ////b* アプリケーション用。先頭が／＊／／／で始まる行にはさまれた部分は無視される。
    FileDialog fd;
    double r = 3.0;                   //基本枝構造の直線の両端の円の半径、枝と各種ポイントの近さの判定基準
    public final DrawingWorker mainDrawingWorker = new DrawingWorker(r, this);    // Basic branch craftsman. Accepts input from the mouse.
    Memo memo1 = new Memo();
    boolean subThreadRunning = false;//1 if SubThread (folding calculation) is running, 0 if not running
    ArrayList<FoldedFigure> foldedFigures = new ArrayList<>(); //Instantiation of fold-up diagram
    int foldedFigureIndex = 0;//Specify which number of foldedFigures Oriagari_Zu is the target of button operation or transformation operation
    Background_camera h_cam = new Background_camera();
    LineColor currentLineColor;//基本枝職人の枝の色を指定する。0は黒、1は赤、2は赤。//currentLineColor=0 black	//currentLineColor=1 red	//currentLineColor=2 blue	//currentLineColor=3 cyan	//currentLineColor=4 orange	//currentLineColor=5 mazenta	//currentLineColor=6 green	//currentLineColor=7 yellow	//currentLineColor=8 new Color(210,0,255) //紫
    LineColor currentAuxLineColor;//補助線の枝の色を指定する。
    MouseMode iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.DRAW_CREASE_FREE_1;//Number of work to be performed after specifying the color of black, red, blue, and water
    boolean w_image_running = false; // Folding together execution. If a single image export is in progress, it will be true.
    String fname_and_number;//まとめ書き出しに使う。
    //各種変数の定義
    String frame_title_0;//フレームのタイトルの根本部分
    String frame_title;//フレームのタイトルの全体
    DrawingWorker.FoldLineAdditionalInputMode foldLineAdditionalInputMode = DrawingWorker.FoldLineAdditionalInputMode.POLY_LINE_0;//=0は折線入力　=1は補助線入力モード
    AngleSystemInputType angle_system_input_id = AngleSystemInputType.DEG_1;//Specifying the input method of the angle system angle_system_input_id = AngleSystemInputType.DEG_1 specifies the line segment, 2 specifies 2 points
    int id_angle_system_a = 12;//角度系の180度を割る数の格納_a
    int id_angle_system_b = 8;//Storage of numbers that divide the angle system by 180 degrees_b
    JButton Button_another_solution;                    //操作の指定に用いる（追加推定一個だけ）
    JButton Button_AS_matome;                    //操作の指定に用いる（追加推定100個）
    JButton Button_bangou_sitei_estimated_display;
    JButton colBlackButton;                    //折線の色の指定に用いる
    JButton colBlueButton;                    //折線の色の指定に用いる
    JButton colRedButton;                    //折線の色の指定に用いる
    JButton colCyanButton;                    //折線(補助線)の色の指定に用いる
    JButton colOrangeButton;                    //補助線1の色の指定に用いる
    JButton colYellowButton;                    //補助線2の色の指定に用いる
    JButton backgroundLockButton;//背景のロックオン
    JButton backgroundToggleButton;//Specify whether to display the background
    JButton toMountainButton;                    //元がどんな種類の折線でも、山折りにする
    JButton toValleyButton;                    //元がどんな種類の折線でも、谷折りにする
    JButton toEdgeButton;                    //元がどんな種類の折線でも、境界線もしくは山谷未設定線にする
    JButton toAuxLiveButton;                    //元がどんな種類の折線でも、補助活線にする
    JButton lineSegmentConvert2Button;//Convert line segment color from red to blue and blue to red
    JTextField lineSegmentDivisionTextField;
    int foldLineDividingNumber = 1;//free折線入力で、折線の等分割されている数
    JTextField ratioATextField;
    double d_orisen_internalDivisionRatio_a = 1.0;
    JTextField ratioBTextField;
    double d_orisen_internalDivisionRatio_b = 0.0;
    JTextField ratioCTextField;
    double d_orisen_internalDivisionRatio_c = 0.0;
    JTextField ratioDTextField;
    double d_orisen_internalDivisionRatio_d = 0.0;
    JTextField ratioETextField;
    double d_orisen_internalDivisionRatio_e = 1.0;
    JTextField ratioFTextField;
    double d_orisen_internalDivisionRatio_f = 2.0;
    JTextField polygonSizeTextField;
    int numPolygonCorners = 5;
    UndoRedo undoRedo;
    int i_undo_suu;//text31はtext10を参考にしている
    JTextField h_undoTotalTextField;
    int i_h_undo_suu;
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
    JTextField scaleFactorTextField;
    double scaleFactor = 1.0;//Scale factor
    JTextField rotationTextField;
    double rotationCorrection = 0.0;//Correction angle of rotation display angle
    UndoRedo foldedFigureUndoRedo;
    int i_undo_suu_om;//text31はtext10を参考にしている
    JLabel length1Label;
    JLabel measuredLength2Label;
    JLabel measuredAngle1Label;
    // バッファー画面用設定はここまでAAAAAAAAAAAAAAAAAAA
    JLabel measuredAngle2Label;
    JLabel measuredAngle3Label;
    Image img_background;       //Image for background
    boolean lockBackground_ori = false;//Lock on background = 1, not = 0
    boolean lockBackground = false;//Lock on background = 1, not = 0
    Point p_mouse_object_position = new Point();//マウスのオブジェクト座標上の位置
    Point p_mouse_TV_position = new Point();//マウスのTV座標上の位置

    HelpDialog explanation;

    boolean displayBackground = false;//If it is 0, the background is not displayed. If it is 1, display it. There is no 2.
    // subThreadMode Subthread operation rules.
    // 0 = Execution of folding estimate 5. It is not a mode to put out different solutions of folding estimation at once.
    // 1 = Execution of folding estimate 5. Another solution for folding estimation is put together.
    // 2 =
    //Runnableインターフェイスを実装しているので、myThスレッドの実行内容はrunメソッドに書かれる
    //アプレットでのスレッドの使い方は、”初体験Java”のP231参照
    boolean mouseDraggedValid = false;
    boolean mouseReleasedValid = false;//0 ignores mouse operation. 1 is valid for mouse operation. When an unexpected mouseDragged or mouseReleased occurs due to on-off of the file box, set it to 0 so that it will not be picked up. These are set to 1 valid when the mouse is clicked.
    SubThread.Mode subThreadMode = SubThread.Mode.FOLDING_ESTIMATE_0;
    Thread myTh;                              //スレッドクラスのインスタンス化
    //画像出力するため20170107_oldと書かれた行をコメントアウトし、20170107_newの行を有効にした。
    //画像出力不要で元にもどすなら、20170107_oldと書かれた行を有効にし、20170107_newの行をコメントアウトにすればよい。（この変更はOrihime.javaの中だけに2箇所ある）
    // オフスクリーン
    BufferedImage offsc_background = null;//20181205add
    boolean flg61 = false;//Used when setting the frame 　20180524
    //= 1 is move, = 2 is move4p, = 3 is copy, = 4 is copy4p, = 5 is mirror image
    String fname_wi;
    JButton moveButton;
    JButton move2p2pButton;
    JButton copyButton;
    JButton copy2p2pButton;
    JButton reflectButton;
    //ウィンドウ透明化用のパラメータ
    BufferedImage imageT;
    //Vector from the upper left to the limit position where the drawing screen can be seen in the upper left
    int upperLeftX = 0;
    int upperLeftY = 0;
    //Vector from the lower right corner to the limit position where the drawing screen can be seen in the lower right corner
    int lowerRightX = 0;
    int lowerRightY = 0;
    JDialog additionalFrame;
    boolean showAddFrame = false;//1=add_frameが存在する。,0=存在しない。
    boolean ckbox_add_frame_SelectAnd3click_isSelected = false;//1=折線セレクト状態でトリプルクリックするとmoveやcopy等の動作モードに移行する。 20200930
    // **************************************************************************************************************************
    // **************************************************************************************************************************
    // **************************************************************************************************************************
    boolean i_mouse_right_button_on = false;//1 if the right mouse button is on, 0 if off
    boolean i_mouse_undo_redo_mode = false;//1 for undo and redo mode with mouse
    MouseWheelTarget i_cp_or_oriagari = MouseWheelTarget.CREASEPATTERN_0;//0 if the target of the mouse wheel is a cp development view, 1 if it is a folded view (front), 2 if it is a folded view (back), 3 if it is a transparent view (front), 4 if it is a transparent view (back)
    double d_ap_check4 = 0.0;

    public final GridConfiguration gridConfiguration = new GridConfiguration();
    public final CanvasConfiguration canvasConfiguration = new CanvasConfiguration();

    ////b* アプリケーション用。先頭が／＊／／／で始まる行にはさまれた部分は無視される。
    public App() {
        setTitle("Origami Editor 1.0.0");//Specify the title and execute the constructor
        frame_title_0 = getTitle();
        frame_title = frame_title_0;//Store title in variable
        mainDrawingWorker.setTitle(frame_title);

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

        //バッファー画面の設定はここまで----------------------------------------------------

        foldedFigures.clear();
        addNewFoldedFigure();
        OZ = foldedFigures.get(0);//折りあがり図

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
        currentLineColor = LineColor.NONE;
        //step=1;
        myTh = null;
        // 初期表示

        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("fishbase.png")));

        // レイアウトの作成レイアウトの作成の部分は”初体験Java”のP179等を参照

        Editor editor = new Editor(this);

        setContentPane(editor.$$$getRootComponent$$$());

        northPanel = editor.getNorthPanel1();
        eastPanel = editor.getEastPanel1();
        southPanel = editor.getSouthPanel1();
        westPanel = editor.getWestPanel1();

        canvas = editor.getCanvas1();

        appMenuBar = new AppMenuBar(this);

        setJMenuBar(appMenuBar);

        JCheckBox ckbox_folding_keika = new JCheckBox("");
        ckbox_folding_keika.addActionListener(e -> {
            setHelp("ckbox_oritatami_keika");
            canvasConfiguration.setDisplayFoldingProgress(ckbox_folding_keika.isSelected());

            updateCanvas();
        });
        ckbox_folding_keika.setIcon(createImageIcon("ppp/ckbox_oritatami_keika_off.png"));
        ckbox_folding_keika.setSelectedIcon(createImageIcon("ppp/ckbox_oritatami_keika_on.png"));

        ckbox_folding_keika.setMargin(new Insets(0, 0, 0, 0));
        /*
         * Extract fields from northPanel
         */
        ratioATextField = northPanel.getRatioATextField();
        ratioBTextField = northPanel.getRatioBTextField();
        ratioCTextField = northPanel.getRatioCTextField();
        ratioDTextField = northPanel.getRatioDTextField();
        ratioETextField = northPanel.getRatioETextField();
        ratioFTextField = northPanel.getRatioFTextField();

        scaleFactorTextField = northPanel.getScaleFactorTextField();

        rotationTextField = northPanel.getRotationTextField();
        backgroundToggleButton = northPanel.getBackgroundToggleButton();
        backgroundLockButton = northPanel.getBackgroundLockButton();
        /*
         * Extract fields from westPanel
         */
        undoRedo = westPanel.getUndoRedo();

        westPanel.getGridConfigurationData(gridConfiguration);

        colRedButton = westPanel.getColRedButton();
        colBlueButton = westPanel.getColBlueButton();
        colBlackButton = westPanel.getColBlackButton();
        colCyanButton = westPanel.getColCyanButton();

        lineSegmentDivisionTextField = westPanel.getLineSegmentDivisionTextField();

        moveButton = westPanel.getMoveButton();

        move2p2pButton = westPanel.getMove2p2pButton();

        copyButton = westPanel.getCopyButton();

        copy2p2pButton = westPanel.getCopy2p2pButton();

        reflectButton = westPanel.getReflectButton();

        toMountainButton = westPanel.getM_nisuruButton();

        toValleyButton = westPanel.getV_nisuruButton();

        toEdgeButton = westPanel.getE_nisuruButton();
        toAuxLiveButton = westPanel.getHK_nisuruButton();
        lineSegmentConvert2Button = westPanel.getSenbun_henkan2Button();

        /*
         * Extract fields from eastPanel
         */
        ckbox_check4 = eastPanel.getcAMVCheckBox();
        angleATextField = eastPanel.getAngleATextField();
        angleBTextField = eastPanel.getAngleBTextField();
        angleCTextField = eastPanel.getAngleCTextField();
        andleDTextField = eastPanel.getAngleDTextField();
        angleETextField = eastPanel.getAngleETextField();
        angleFTextField = eastPanel.getAngleFTextField();
        polygonSizeTextField = eastPanel.getPolygonSizeTextField();
        circleCustomizedColorButton = eastPanel.getC_colButton();
        h_undoTotalTextField = eastPanel.getAuxUndoTotalTextField();
        colOrangeButton = eastPanel.getColOrangeButton();
        colYellowButton = eastPanel.getColYellowButton();
        length1Label = eastPanel.getMeasuredLength1Label();
        measuredLength2Label = eastPanel.getMeasuredLength2Label();
        measuredAngle1Label = eastPanel.getMeasuredAngle1Label();
        measuredAngle2Label = eastPanel.getMeasuredAngle2Label();
        measuredAngle3Label = eastPanel.getMeasuredAngle3Label();

        correctCpBeforeFoldingCheckBox = westPanel.getCorrectCpBeforeFoldingCheckBox();

        selectPersistentCheckBox = westPanel.getSelectPersistentCheckBox();

        ckbox_toukazu_color = westPanel.getColoredXRayButton();

        /*
         * Extract fields from southPanel
         */

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
        int scale_interval = 5;
        gridConfiguration.setIntervalGridSize(scale_interval);

        updateGrid();

        selectionOperationMode = SelectionOperationMode.MOVE_1;
        Button_sel_mou_wakukae();//セレクトされた折線がある状態で、セレクトされている折線の頂点をクリックした場合の動作モードの初期設定

        //Initialization of folding prediction map started
        configure_initialize_prediction();
        //折畳予測図のの初期化　終了

        Button_shared_operation();

        updateCanvas();

        circleCustomizedColorButton.setBackground(circleCustomizedColor);//特注色の指定色表示

        // 測定長さと角度の表示

        mainDrawingWorker.measurement_display();
        mainDrawingWorker.setCamera(camera_of_orisen_input_diagram);

        mainDrawingWorker.record();
        mainDrawingWorker.auxRecord();

        OZ.ct_worker.set_F_color(OZ.foldedFigure_F_color); //折り上がり図の表面の色
        Button_F_color.setBackground(OZ.foldedFigure_F_color);    //ボタンの色設定

        OZ.ct_worker.set_B_color(OZ.foldedFigure_B_color);//折り上がり図の裏面の色
        Button_B_color.setBackground(OZ.foldedFigure_B_color);//ボタンの色設定

        OZ.ct_worker.set_L_color(OZ.foldedFigure_L_color);        //折り上がり図の線の色
        Button_L_color.setBackground(OZ.foldedFigure_L_color);        //ボタンの色設定

        //            frame.setSize(1200, 700);
        pack();
        setLocationRelativeTo(null);//If you want to put the application window in the center of the screen, use the setLocationRelativeTo () method. If you pass null, it will always be in the center.
        setVisible(true);

        explanation = new HelpDialog(this);
        explanation.setVisible(true);
    }//------------------------------------------ボタンの定義等、ここまでがコンストラクタとして起動直後に最初に実行される内容

    public void repaintCanvas() {
        canvas.repaint();
    }

    public FoldType getFoldType() {

        FoldType foldType;//= 0 Do nothing, = 1 Folding estimation for all fold lines in the normal development view, = 2 for fold estimation for selected fold lines, = 3 for changing the folding state
        int foldLineTotalForSelectFolding = mainDrawingWorker.getFoldLineTotalForSelectFolding();
        System.out.println("foldedFigures.size() = " + foldedFigures.size() + "    : foldedFigureIndex = " + foldedFigureIndex + "    : mainDrawingWorker.get_orisensuu_for_select_oritatami() = " + foldLineTotalForSelectFolding);
        if (foldedFigures.size() == 1) {                        //折り上がり系図無し
            if (foldedFigureIndex == 0) {                            //展開図指定
                if (foldLineTotalForSelectFolding == 0) {        //折り線選択無し
                    foldType = FoldType.FOR_ALL_LINES_1;//全展開図で折畳み
                } else {        //折り線選択有り
                    foldType = FoldType.FOR_SELECTED_LINES_2;//選択された展開図で折畳み
                }
            } else {                        //折り上がり系図指定
                foldType = FoldType.NOTHING_0;//有り得ない
            }
        } else {                        //折り上がり系図有り
            if (foldedFigureIndex == 0) {                            //展開図指定
                if (foldLineTotalForSelectFolding == 0) {        //折り線選択無し
                    foldType = FoldType.NOTHING_0;//何もしない
                } else {        //折り線選択有り
                    foldType = FoldType.FOR_SELECTED_LINES_2;//選択された展開図で折畳み
                }
            } else {                        //折り上がり系図指定
                if (foldLineTotalForSelectFolding == 0) {        //折り線選択無し
                    foldType = FoldType.CHANGING_FOLDED_3;//指定された折り上がり系図で折畳み
                } else {        //折り線選択有り
                    foldType = FoldType.FOR_SELECTED_LINES_2;//選択された展開図で折畳み
                }
            }
        }

        return foldType;
    }

    //ここまでが変数等の定義

    public void oritatame(FoldType foldType, FoldedFigure.EstimationOrder estimationOrder) {//引数の意味は(foldType , estimationOrder)
        //i_fold_typeはget_i_fold_type()関数で取得する。
        //foldType=0なにもしない、=1通常の展開図の全折線を対象とした折り畳み推定、=2はselectされた折線を対象とした折り畳み推定、=3は折畳み状態を変更
        if (foldType == FoldType.NOTHING_0) {
            System.out.println(" oritatame 20180108");
        } else if ((foldType == FoldType.FOR_ALL_LINES_1) || (foldType == FoldType.FOR_SELECTED_LINES_2)) {
            if (foldType == FoldType.FOR_ALL_LINES_1) {
                mainDrawingWorker.select_all();
            }
            //
            if (correctCpBeforeFoldingCheckBox.isSelected()) {// Automatically correct strange parts (branch-shaped fold lines, etc.) in the development drawing
                DrawingWorker drawingWorker2 = new DrawingWorker(r, this);    // Basic branch craftsman. Accepts input from the mouse.
                drawingWorker2.setMemo_for_reading(mainDrawingWorker.foldLineSet.getMemoForSelectFolding());
                drawingWorker2.point_removal();
                drawingWorker2.overlapping_line_removal();
                drawingWorker2.branch_trim(0.000001);
                drawingWorker2.organizeCircles();
                Ss0 = drawingWorker2.getForFolding();
            } else {
                Ss0 = mainDrawingWorker.getForSelectFolding();
            }

            point_of_referencePlane_old.set(OZ.cp_worker1.get_point_of_referencePlane_tv());//20180222折り線選択状態で折り畳み推定をする際、以前に指定されていた基準面を引き継ぐために追加
            //これより前のOZは古いOZ
            folding_prepare();//OAZのアレイリストに、新しく折り上がり図をひとつ追加し、それを操作対象に指定し、foldedFigures(0)共通パラメータを引き継がせる。
            //これより後のOZは新しいOZに変わる

            OZ.estimationOrder = estimationOrder;

            if (!subThreadRunning) {
                subThreadRunning = true;
                subThreadMode = SubThread.Mode.FOLDING_ESTIMATE_0;//1=折畳み推定の別解をまとめて出す。0=折畳み推定の別解をまとめて出すモードではない。この変数はサブスレッドの動作変更につかうだけ。20170611にVer3.008から追加
                mks();//新しいスレッドを作る
                sub.start();
            }

        } else if (foldType == FoldType.CHANGING_FOLDED_3) {
            OZ.estimationOrder = estimationOrder;

            if (!subThreadRunning) {
                subThreadRunning = true;
                subThreadMode = SubThread.Mode.FOLDING_ESTIMATE_0;//1=折畳み推定の別解をまとめて出す。0=折畳み推定の別解をまとめて出すモードではない。この変数はサブスレッドの動作変更につかうだけ。20170611にVer3.008から追加
                mks();//新しいスレッドを作る
                sub.start();
            }
        }
    }

    void folding_prepare() {//Add one new folding diagram to the foldedFigures array list, specify it as the operation target, and inherit the foldedFigures (0) common parameters.
        System.out.println(" oritatami_jyunbi 20180107");

        addNewFoldedFigure(); //OAZのアレイリストに、新しく折り上がり図をひとつ追加する。

        set_i_OAZ(foldedFigures.size() - 1);//foldedFigureIndex=i;OZ = (Oriagari_Zu)foldedFigures.get(foldedFigureIndex); OZ(各操作の対象となる折上がり図）に、アレイリストに最新に追加された折上がり図を割り当てる)

        FoldedFigure orz = foldedFigures.get(0);//foldedFigures(0)(共通パラメータを保持する折上がり図）をorzに割り付ける

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

    // ------------------------------------------------------------------------------
    public void addNewFoldedFigure() {
        foldedFigures.add(new FoldedFigure_01(this));
    }

    public void displayMeasuredLength1(double d0) {
        length1Label.setText(String.valueOf(d0));
    }

    public void displayMeasuredLength2(double d0) {
        measuredLength2Label.setText(String.valueOf(d0));
    }

    public void displayMeasuredAngle1(double d0) {
        measuredAngle1Label.setText(String.valueOf(d0));
    }

    public void displayMeasuredAngle2(double d0) {
        measuredAngle2Label.setText(String.valueOf(d0));
    }

    public void displayMeasuredAngle3(double d0) {
        measuredAngle3Label.setText(String.valueOf(d0));
    }

    public void updateGrid() {
        mainDrawingWorker.setGridConfigurationData(gridConfiguration);
        westPanel.setGridConfigurationData(gridConfiguration);
    }

    public void twoColorNoSelectedPolygonalLineWarning() {
        JLabel label = new JLabel(
                "<html>２色塗りわけ展開図を描くためには、あらかじめ対象範囲を選択してください（selectボタンを使う）。<br>" +
                        "To get 2-Colored crease pattern, select the target range in advance (use the select button).<html>");
        JOptionPane.showMessageDialog(this, label);
    }

    public void foldingNoSelectedPolygonalLineWarning() {
        JLabel label = new JLabel(
                "<html>新たに折り上がり図を描くためには、あらかじめ対象範囲を選択してください（selectボタンを使う）。<br>" +
                        "To calculate new folded shape, select the target clease lines range in advance (use the select button).<html>");
        JOptionPane.showMessageDialog(this, label);
    }

    public void halt() {
        int option = JOptionPane.showConfirmDialog(this, createImageIcon("ppp/keisan_tyuusi_DLog.png"));

        if (option == JOptionPane.YES_OPTION) {
            mouseDraggedValid = false;
            mouseReleasedValid = false;
            writeMemo2File();
            mainDrawingWorker.record();
        } else if (option == JOptionPane.NO_OPTION) {
        } else if (option == JOptionPane.CANCEL_OPTION) {
            return;
        }

        sub.stop();
        subThreadRunning = false;

        configure_initialize_prediction();
    }

    public void closing() {
        int option = JOptionPane.showConfirmDialog(this, createImageIcon("ppp/owari.png"));

        if (option == JOptionPane.YES_OPTION) {
            mouseDraggedValid = false;
            mouseReleasedValid = false;
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
        mainDrawingWorker.reset();
        mainDrawingWorker.reset_2();    //描き職人の初期化


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

        mainDrawingWorker.setCamera(camera_of_orisen_input_diagram);
        OZ.cp_worker1.setCamera(camera_of_orisen_input_diagram);

        //折線入力か補助線入力か
        foldLineAdditionalInputMode = DrawingWorker.FoldLineAdditionalInputMode.POLY_LINE_0;
//北辺

        canvasConfiguration.reset();
        mainDrawingWorker.setData(canvasConfiguration);

        correctCpBeforeFoldingCheckBox.setSelected(false);//cpを折畳み前に自動改善する
        selectPersistentCheckBox.setSelected(false);//select状態を折畳み操作をしてもなるべく残す
        ckbox_toukazu_color.setSelected(false);//透過図をカラー化する。

        //内分された折線の指定
        d_orisen_internalDivisionRatio_a = 1.0;
        ratioATextField.setText(String.valueOf(d_orisen_internalDivisionRatio_a));
        d_orisen_internalDivisionRatio_b = 0.0;
        ratioBTextField.setText(String.valueOf(d_orisen_internalDivisionRatio_b));
        d_orisen_internalDivisionRatio_c = 0.0;
        ratioCTextField.setText(String.valueOf(d_orisen_internalDivisionRatio_c));
        d_orisen_internalDivisionRatio_d = 0.0;
        ratioDTextField.setText(String.valueOf(d_orisen_internalDivisionRatio_d));
        d_orisen_internalDivisionRatio_e = 1.0;
        ratioETextField.setText(String.valueOf(d_orisen_internalDivisionRatio_e));
        d_orisen_internalDivisionRatio_f = 2.0;
        ratioFTextField.setText(String.valueOf(d_orisen_internalDivisionRatio_f));

        //
        scaleFactor = 1.0;
        scaleFactorTextField.setText(String.valueOf(scaleFactor)); //縮尺係数
        rotationCorrection = 0.0;
        rotationTextField.setText(String.valueOf(rotationCorrection));//回転表示角度の補正係数

        OZ.d_foldedFigure_scale_factor = 1.0;
        foldedFigureSizeTextField.setText(String.valueOf(OZ.d_foldedFigure_scale_factor));//折り上がり図の縮尺係数
        OZ.d_foldedFigure_rotation_correction = 0.0;
        foldedFigureRotateTextField.setText(String.valueOf(OZ.d_foldedFigure_rotation_correction));//折り上がり図の回転表示角度の補正角度

        //背景表示
        displayBackground = true;
        backgroundToggleButton.setBackground(Color.ORANGE);

        //背景ロックオン
        lockBackground = false;
        lockBackground_ori = false;
        backgroundLockButton.setBackground(Color.gray);
//西辺

        //展開図の線の太さ。

        //基本枝構造の直線の両端の円の半径、（以前は枝と各種ポイントの近さの判定基準）
        //double r=3.0;
        //mainDrawingWorker.set_r(r);

        //折線表現を色で表す

        //ペンの色の指定
        currentLineColor = LineColor.RED_1;
        mainDrawingWorker.setColor(currentLineColor);    //最初の折線の色を指定する。0は黒、1は赤、2は青。
        buttonColorReset();
        colRedButton.setForeground(Color.black);
        colRedButton.setBackground(Color.red);    //折線のボタンの色設定


        //折線分割数
        foldLineDividingNumber = 2;
        lineSegmentDivisionTextField.setText(String.valueOf(foldLineDividingNumber));
        mainDrawingWorker.setFoldLineDividingNumber(foldLineDividingNumber);//フリー折線入力時の分割数


        //格子分割数の指定

        //格子の適用範囲の指定

        gridConfiguration.reset();

        updateGrid();
//--------------------------------------------
//東辺
        //角度系入力を22.5度系にする。
        mainDrawingWorker.set_id_angle_system(8);

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
        currentAuxLineColor = LineColor.ORANGE_4;
        mainDrawingWorker.setAuxLineColor(currentAuxLineColor);                                        //最初の補助線の色を指定する。4はオレンジ、7は黄。
        auxColorButtonReset();
        colOrangeButton.setForeground(Color.black);
        colOrangeButton.setBackground(Color.ORANGE);    //補助線のボタンの色設定

        ckbox_check4.setSelected(false);//checkするかどうかの選択
        mainDrawingWorker.setCheck4(false);
    }

    // *******************************************************************************************************
    public void showAdditionalFrame() {
        //Frame additionalFrame
        if (showAddFrame) {
            System.out.println("111 showAddFrame=" + showAddFrame);
            additionalFrame.dispose();
            additionalFrame = new OpenFrame("additionalFrame", this);
        } else {
            System.out.println("000 showAddFrame=" + showAddFrame);
            additionalFrame = new OpenFrame("additionalFrame", this);
        }
        showAddFrame = true;
        additionalFrame.toFront();
    }


// *******************************************************************************************************

    //ボタンを押されたときの処理----------------
    public void actionPerformed(ActionEvent e) {

    }

// ------------------------------------------------------

    public void setInternalDivisionRatio() {
        d_orisen_internalDivisionRatio_a = String2double(ratioATextField.getText(), d_orisen_internalDivisionRatio_a);
        d_orisen_internalDivisionRatio_b = String2double(ratioBTextField.getText(), d_orisen_internalDivisionRatio_b);
        d_orisen_internalDivisionRatio_c = String2double(ratioCTextField.getText(), d_orisen_internalDivisionRatio_c);
        if (d_orisen_internalDivisionRatio_c < 0.0) {
            d_orisen_internalDivisionRatio_c = 0.0;
        }
        d_orisen_internalDivisionRatio_d = String2double(ratioDTextField.getText(), d_orisen_internalDivisionRatio_d);
        d_orisen_internalDivisionRatio_e = String2double(ratioETextField.getText(), d_orisen_internalDivisionRatio_e);
        d_orisen_internalDivisionRatio_f = String2double(ratioFTextField.getText(), d_orisen_internalDivisionRatio_f);
        if (d_orisen_internalDivisionRatio_f < 0.0) {
            d_orisen_internalDivisionRatio_f = 0.0;
        }

        double d_internalDivisionRatio_s = d_orisen_internalDivisionRatio_a + d_orisen_internalDivisionRatio_b * Math.sqrt(d_orisen_internalDivisionRatio_c);
        if (d_internalDivisionRatio_s < 0.0) {
            d_orisen_internalDivisionRatio_b = 0.0;
        }
        double d_internalDivisionRatio_t = d_orisen_internalDivisionRatio_d + d_orisen_internalDivisionRatio_e * Math.sqrt(d_orisen_internalDivisionRatio_f);
        if (d_internalDivisionRatio_t < 0.0) {
            d_orisen_internalDivisionRatio_e = 0.0;
        }

        ratioATextField.setText(String.valueOf(d_orisen_internalDivisionRatio_a));
        ratioBTextField.setText(String.valueOf(d_orisen_internalDivisionRatio_b));
        ratioCTextField.setText(String.valueOf(d_orisen_internalDivisionRatio_c));
        ratioDTextField.setText(String.valueOf(d_orisen_internalDivisionRatio_d));
        ratioETextField.setText(String.valueOf(d_orisen_internalDivisionRatio_e));
        ratioFTextField.setText(String.valueOf(d_orisen_internalDivisionRatio_f));

        mainDrawingWorker.set_d_internalDivisionRatio_st(d_internalDivisionRatio_s, d_internalDivisionRatio_t);
    }


// *******************************************************************************************************

    public void set_restricted_angle_abc() {
        d_restricted_angle_a = String2double(angleATextField.getText(), d_restricted_angle_a);
        d_restricted_angle_b = String2double(angleBTextField.getText(), d_restricted_angle_b);
        d_restricted_angle_c = String2double(angleCTextField.getText(), d_restricted_angle_c);

        angleATextField.setText(String.valueOf(d_restricted_angle_a));
        angleBTextField.setText(String.valueOf(d_restricted_angle_b));
        angleCTextField.setText(String.valueOf(d_restricted_angle_c));

        mainDrawingWorker.set_d_restricted_angle(d_restricted_angle_a, d_restricted_angle_b, d_restricted_angle_c);
    }

    public void setRestrictedAngleDEF() { //このdefは「定義」と言う意味ではなく、dとeとfを扱うという意味
        d_restricted_angle_d = String2double(andleDTextField.getText(), d_restricted_angle_d);
        d_restricted_angle_e = String2double(angleETextField.getText(), d_restricted_angle_e);
        d_restricted_angle_f = String2double(angleFTextField.getText(), d_restricted_angle_f);

        andleDTextField.setText(String.valueOf(d_restricted_angle_d));
        angleETextField.setText(String.valueOf(d_restricted_angle_e));
        angleFTextField.setText(String.valueOf(d_restricted_angle_f));

        mainDrawingWorker.set_d_restricted_angle(d_restricted_angle_d, d_restricted_angle_e, d_restricted_angle_f);
    }

    //--------------------------------------------------------
    void Button_sel_mou_wakukae() {
        moveButton.setBorder(new LineBorder(new Color(150, 150, 150), 1, false));
        move2p2pButton.setBorder(new LineBorder(new Color(150, 150, 150), 1, false));
        copyButton.setBorder(new LineBorder(new Color(150, 150, 150), 1, false));
        copy2p2pButton.setBorder(new LineBorder(new Color(150, 150, 150), 1, false));
        reflectButton.setBorder(new LineBorder(new Color(150, 150, 150), 1, false));

        switch (selectionOperationMode) {
            case MOVE_1:
                moveButton.setBorder(new LineBorder(Color.green, 3, false));
                break;
            case MOVE4P_2:
                move2p2pButton.setBorder(new LineBorder(Color.green, 3, false));
                break;
            case COPY_3:
                copyButton.setBorder(new LineBorder(Color.green, 3, false));
                break;
            case COPY4P_4:
                copy2p2pButton.setBorder(new LineBorder(Color.green, 3, false));
                break;
            case MIRROR_5:
                reflectButton.setBorder(new LineBorder(Color.green, 3, false));
                break;
        }
    }

    //--------------------------------------------------------
    void buttonReset() {
        toMountainButton.setForeground(Color.black);
        toValleyButton.setForeground(Color.black);
        toEdgeButton.setForeground(Color.black);
        toAuxLiveButton.setForeground(Color.black); //HKとは補助活線のこと
        lineSegmentConvert2Button.setForeground(Color.black);

        toMountainButton.setBackground(Color.white);
        toValleyButton.setBackground(Color.white);
        toEdgeButton.setBackground(Color.white);
        toAuxLiveButton.setBackground(Color.white);
        lineSegmentConvert2Button.setBackground(Color.white);
    }

    //--------------------------------------------------------
    void buttonColorReset() {
        colBlackButton.setForeground(Color.black);
        colBlueButton.setForeground(Color.black);
        colRedButton.setForeground(Color.black);
        colCyanButton.setForeground(Color.black);

        colBlackButton.setBackground(new Color(150, 150, 150));
        colBlueButton.setBackground(new Color(150, 150, 150));
        colRedButton.setBackground(new Color(150, 150, 150));
        colCyanButton.setBackground(new Color(150, 150, 150));
    }

    //--------------------------------------------------------
    void auxColorButtonReset() {
        colOrangeButton.setBackground(new Color(150, 150, 150));
        colYellowButton.setBackground(new Color(150, 150, 150));
    }

    public void Button_shared_operation() {
        mainDrawingWorker.setDrawingStage(0);
        mainDrawingWorker.set_i_circle_drawing_stage(0);
        mainDrawingWorker.set_s_step_iactive(LineSegment.ActiveState.ACTIVE_BOTH_3);//要注意　es1でうっかりs_stepにset.(senbun)やるとアクティヴでないので表示が小さくなる20170507
        mainDrawingWorker.voronoiLineSet.reset();
    }

    // *******************************************************************************************zzzzzzzzzzzz
    public void pointInCpOrFoldedFigure(Point p) {//A function that determines which of the development and folding views the Ten obtained with the mouse points to.
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
        for (int i = 1; i <= foldedFigures.size() - 1; i++) {
            OZi = foldedFigures.get(i);


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
// ---------------------------------------

    // *******************************************************************************************cccccccccc
    void set_i_OAZ(int i) {//OZが切り替わるときの処理
        System.out.println("foldedFigureIndex = " + foldedFigureIndex);
        foldedFigureIndex = i;
        OZ = foldedFigures.get(foldedFigureIndex);
        //透過図はカラー化しない。
        ckbox_toukazu_color.setSelected(OZ.transparencyColor);//透過図はカラー化。
    }

    public Point e2p(MouseEvent e) {
        double d_width = 0.0;
        if (canvasConfiguration.getDisplayPointOffset()) {
            d_width = camera_of_orisen_input_diagram.getCameraZoomX() * mainDrawingWorker.getSelectionDistance();
        }
        return new Point(e.getX() - (int) d_width, e.getY() - (int) d_width);
    }


    //=============================================================================
    //マウスのホイールが回転した時に呼ばれるメソッド
    //=============================================================================

    public void mouse_object_position(Point p) {//この関数はmouseMoved等と違ってマウスイベントが起きても自動では認識されない
        p_mouse_TV_position.set(p.getX(), p.getY());

        p_mouse_object_position.set(camera_of_orisen_input_diagram.TV2object(p_mouse_TV_position));
    }


    //----------------------------------------------------------------------
    //マウス操作(移動やボタン操作)を行う関数------------------------------
    //----------------------------------------------------------------------

    // ------------------------------------------------------
    public void background_set(Point t1, Point t2, Point t3, Point t4) {
        h_cam.set_h1(t1);
        h_cam.set_h2(t2);
        h_cam.set_h3(t3);
        h_cam.set_h4(t4);

        h_cam.parameter_calculation();
    }


//mouseMode;マウスの動作に対する反応を規定する。
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

    // ------------------------------------------------------
    public void drawBackground(Graphics2D g2h, Image imgh) {//引数はカメラ設定、線幅、画面X幅、画面y高さ
        //背景画を、画像の左上はしを、ウィンドウの(0,0)に合わせて回転や拡大なしで表示した場合を基準状態とする。
        //背景画上の点h1を中心としてa倍拡大する。次に、h1を展開図上の点h3と重なるように背景画を平行移動する。
        //この状態の展開図を、h3を中心にb度回転したよう見えるように座標を回転させて貼り付けて、その後、座標の回転を元に戻すという関数。
        //引数は、Graphics2D g2h,Image imgh,Ten h1,Ten h2,Ten h3,Ten h4
        //h2,とh4も重なるようにする
        //

        //最初に

        if (lockBackground) {
            h_cam.setCamera(camera_of_orisen_input_diagram);
            h_cam.h3_and_h4_calculation();
            h_cam.parameter_calculation();
        }

        AffineTransform at = new AffineTransform();
        at.rotate(h_cam.getAngle() * Math.PI / 180.0, h_cam.get_cx(), h_cam.get_cy());
        g2h.setTransform(at);


        g2h.drawImage(imgh, h_cam.get_x0(), h_cam.get_y0(), h_cam.get_x1(), h_cam.get_y1(), this);

        at.rotate(-h_cam.getAngle() * Math.PI / 180.0, h_cam.get_cx(), h_cam.get_cy());
        g2h.setTransform(at);

    }


    // --------------------------------------------------

    void configure_initialize_prediction() {
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

        mouseDraggedValid = false;
        mouseReleasedValid = false;//0は、マウス操作を無視。1はマウス操作有効。ファイルボックスのon-offなどで、予期せぬmouseDraggedやmouseReleasedが発生したとき、それを拾わないように0に設定する。これらは、マウスがクリックされたときに、1有効指定にする。

        OZ.estimated_initialize();
        bulletinBoard.clear();
    }

    ////b* アプリケーション用。先頭が／＊／／／で始まる行にはさまれた部分は無視される。
    void readImageFromFile() {
        FileDialog fd = new FileDialog(this, "Select Image File.", FileDialog.LOAD);
        fd.setVisible(true);
        String img_background_fname = fd.getDirectory() + fd.getFile();
        boolean iDisplayBackground_old;
        iDisplayBackground_old = displayBackground;
        try {
            if (fd.getFile() != null) {
                Toolkit tk = Toolkit.getDefaultToolkit();
                img_background = tk.getImage(img_background_fname);

                if (img_background != null) {
                    displayBackground = true;
                    backgroundToggleButton.setBackground(Color.ORANGE);
                    lockBackground = false;
                    lockBackground_ori = false;
                    backgroundLockButton.setBackground(Color.gray);
                }
            }

        } catch (Exception e) {
            displayBackground = iDisplayBackground_old;
            if (!displayBackground) {
                backgroundToggleButton.setBackground(Color.gray);
            }
        }
    }

    void writeImage() {
        // String String fname_wi
        fname_wi = selectFileName("file name for Img save");
        flg61 = false;
        if ((mouseMode == MouseMode.OPERATION_FRAME_CREATE_61) && (mainDrawingWorker.getDrawingStage() == 4)) {
            flg61 = true;
            mainDrawingWorker.setDrawingStage(0);
        }

        if (fname_wi != null) {
            canvas.flg_wi = true;
            repaintCanvas();//Necessary to not export the green border
        }
    }

    //---------------------------------------------------------
    String selectFileName(String coment0) {
        fd = new FileDialog(this, coment0, FileDialog.SAVE);
        fd.setVisible(true);
        String fileName = null;
        if (fd.getFile() != null) {
            fileName = fd.getDirectory() + fd.getFile();
        }
        return fileName;
    }

    public void setHelp(String resource) {
        explanation.setExplanation(resource);
    }

    //-------------------
    Memo readFile2Memo() {
        String fname;
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
        mainDrawingWorker.setTitle(frame_title);

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
            mainDrawingWorker.setTitle(frame_title);
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

    //---------------------------------------------------------

    void writeMemo2File() {
        Memo memo1;
        memo1 = mainDrawingWorker.getMemo_for_export();
        String fname = selectFileName("書き出しファイルの名前");

        if (fname != null) {
            if (fname.endsWith("cp")) {
                memoAndName2File(FileFormatConverter.orihime2cp(memo1), fname);

                frame_title = frame_title_0 + "        " + fd.getFile();
                setTitle(frame_title);
                mainDrawingWorker.setTitle(frame_title);

            } else if (fname.endsWith("orh")) {
                memoAndName2File(memo1, fname);

                frame_title = frame_title_0 + "        " + fd.getFile();
                setTitle(frame_title);
                mainDrawingWorker.setTitle(frame_title);

            } else {
                fname = fname + ".orh";
                memoAndName2File(memo1, fname);

                frame_title = frame_title_0 + "        " + fd.getFile() + ".orh";
                setTitle(frame_title);
                mainDrawingWorker.setTitle(frame_title);
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

    void folding_settings_two_color() {//２色塗りわけ展開図
        OZ.folding_settings_two_color(camera_of_orisen_input_diagram, Ss0);
    }

    void mks() {
        sub = new SubThread(this);
    }

////b* アプリケーション用。先頭が／＊／／／で始まる行にはさまれた部分は無視される

    public double String2double(String str0, double default_if_error) {
        String new_str0 = str0.trim();
        if (new_str0.equals("L1")) {
            str0 = String.valueOf(mainDrawingWorker.get_L1());
        }
        if (new_str0.equals("L2")) {
            str0 = String.valueOf(mainDrawingWorker.get_L2());
        }
        if (new_str0.equals("A1")) {
            str0 = String.valueOf(mainDrawingWorker.get_A1());
        }
        if (new_str0.equals("A2")) {
            str0 = String.valueOf(mainDrawingWorker.get_A2());
        }
        if (new_str0.equals("A3")) {
            str0 = String.valueOf(mainDrawingWorker.get_A3());
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

    public void updateCanvas() {
        mainDrawingWorker.setData(canvasConfiguration);
        canvas.setData(canvasConfiguration);
        appMenuBar.setData(canvasConfiguration);
        northPanel.setData(canvasConfiguration);
    }

    public enum MouseWheelTarget {
        CREASEPATTERN_0,
        FOLDED_FRONT_1,
        FOLDED_BACK_2,
        TRANSPARENT_FRONT_3,
        TRANSPARENT_BACK_4,
    }

    public enum FoldType {
        NOTHING_0,
        FOR_ALL_LINES_1,
        FOR_SELECTED_LINES_2,
        CHANGING_FOLDED_3,
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
