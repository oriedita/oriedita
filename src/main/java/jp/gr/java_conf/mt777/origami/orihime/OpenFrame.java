package jp.gr.java_conf.mt777.origami.orihime;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static jp.gr.java_conf.mt777.origami.orihime.ResourceUtil.createImageIcon;

class OpenFrame extends Frame implements ActionListener {
    public JCheckBox ckbox_add_frame_SelectAnd3click;//20200930
    //変数の宣言
    App orihime_app;

    //スレッド作成時に実行される処理

    //	public OpenFrame(String name) {
    public OpenFrame(String name, App app0) {
        super(name);//本来、子コンストラクタは先頭で親コンストラクタを呼び出さなければならない。superは親インスタンスを示す。

        orihime_app = app0;

        setSize(300, 250);

        System.out.println("Y：" + orihime_app.getSize().height);//.height

        setLocation(
                (int) (orihime_app.getLocation().getX()) + orihime_app.getSize().width - getSize().width - 131
                ,
                (int) (orihime_app.getLocation().getY()) + orihime_app.getSize().height - getSize().height - 44
        );

        setResizable(false);
        //ユーザーがこのフレームのサイズを変更できるかどうかを設定します。

        //このフレームの装飾の有効化と無効化を切り替えます。

        addWindowListener(new WindowAdapter() {//終了ボタンを有効化
            public void windowClosing(WindowEvent evt) {
                orihime_app.i_add_frame = 0;
                dispose();
            }

            public void windowOpened(WindowEvent evt) {
                System.out.println("windowOpendwwwwwwwwwww");
            }

            public void windowClosed(WindowEvent evt) {
                System.out.println("windowClosedwwwwwww");
            }

            public void windowIconified(WindowEvent e) {
                System.out.println("windowIconifiedwwwwwww");
            }

            public void windowDeiconified(WindowEvent e) {
                System.out.println("windowDeiconifiedwwwwwww");
            }

            public void windowActivated(WindowEvent e) {
                System.out.println("windowActivatedwwwwwwwww");
            }

            public void windowDeactivated(WindowEvent e) {
                System.out.println("windowDeactivatedwwwwwwwwwwww");
            }

        });//終了ボタンを有効化 ここまで。

        //追加フレームのレイアウト等条件設定
        setLayout(new GridLayout(1, 1));
        setBackground(Color.PINK);

        //左辺（西側）パネルの作成


        Panel pnl_00 = new Panel();
        pnl_00.setBackground(Color.PINK);
        //pnl_00.setLayout(new GridLayout(30, 1));
        pnl_00.setLayout(new GridLayout(10, 1));
        add(pnl_00);

        Panel pnl_01 = new Panel();
        pnl_01.setBackground(Color.PINK);
        pnl_01.setLayout(new GridLayout(1, 3));
        pnl_00.add(pnl_01);


//------------------------------------------------

//チェックボックス
//20200930ここから
        ckbox_add_frame_SelectAnd3click = new JCheckBox("sel<=>mcm");
        ckbox_add_frame_SelectAnd3click.addActionListener(e -> {
            orihime_app.img_explanation_fname =
                    "qqq/af/ckbox_add_frame_SelectAnd3click.png";
            orihime_app.readImageFromFile3();

            if (ckbox_add_frame_SelectAnd3click_isSelected() == 1) {
                orihime_app.ckbox_add_frame_SelectAnd3click_isSelected = 1;
            } else {
                orihime_app.ckbox_add_frame_SelectAnd3click_isSelected = 0;
            }
            orihime_app.repaint();
        });
        ckbox_add_frame_SelectAnd3click.setIcon(createImageIcon("ppp/af/ckbox_add_frame_SelectAnd3click_off.png"));
        ckbox_add_frame_SelectAnd3click.setSelectedIcon(createImageIcon("ppp/af/ckbox_add_frame_SelectAnd3click_on.png"));
        ckbox_add_frame_SelectAnd3click.setBackground(Color.PINK);
        ckbox_add_frame_SelectAnd3click.setBorderPainted(false);
        ckbox_add_frame_SelectAnd3click.setMargin(new Insets(0, 0, 0, 0));
        pnl_01.add(
                ckbox_add_frame_SelectAnd3click);
//20200930 ここまで

//----------------------------------------------------------------------------------------------


        JButton Button_O_F_check =
                new JButton("O_F_check");
        Button_O_F_check.addActionListener(e -> {
            orihime_app.img_explanation_fname = "qqq/af/O_F_check.png";
            orihime_app.readImageFromFile3();

            orihime_app.i_mouse_modeA = MouseMode.FLAT_FOLDABLE_CHECK_63;
            System.out.println("i_mouse_modeA = " + orihime_app.i_mouse_modeA);
            orihime_app.Button_kyoutuu_sagyou();
            orihime_app.repaint();


        });
        pnl_01.add(Button_O_F_check);

        Button_O_F_check.setMargin(new Insets(0, 0, 0, 0));


//-----------------------------------------------

        Panel pnl_02 = new Panel();
        pnl_02.setBackground(Color.PINK);
        pnl_02.setLayout(new GridLayout(1, 3));
        pnl_00.add(pnl_02);
//------------------------------------------------
// -------------39;Foldable line + grid point system input
        JButton Button_oritatami_kanousen_and_kousitenkei = new JButton("");
        Button_oritatami_kanousen_and_kousitenkei.addActionListener(e -> {
            orihime_app.img_explanation_fname = "qqq/oritatami_kanousen_and_kousitenkei.png";
            orihime_app.readImageFromFile3();

            orihime_app.i_mouse_modeA = MouseMode.FOLDABLE_LINE_INPUT_39;
            orihime_app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = 39;
            System.out.println("i_mouse_modeA = " + orihime_app.i_mouse_modeA);

            orihime_app.es1.unselect_all();
            orihime_app.Button_kyoutuu_sagyou();
            orihime_app.repaint();
        });
        pnl_02.add(Button_oritatami_kanousen_and_kousitenkei);

        Button_oritatami_kanousen_and_kousitenkei.setMargin(new Insets(0, 0, 0, 0));
        Button_oritatami_kanousen_and_kousitenkei.setIcon(createImageIcon("ppp/oritatami_kanousen_and_kousitenkei.png"));


// -------------39;折り畳み可能線+格子点系入力。ここまで
//------------------------------------------------

        Panel pnl_03 = new Panel();
        pnl_03.setBackground(Color.PINK);
        pnl_03.setLayout(new GridLayout(1, 3));
        pnl_00.add(pnl_03);
//----------------------------------------------------------------------------------------------

//------------------------------------------------

        JButton Button_select_polygon =
                new JButton("select_polygon");
        Button_select_polygon.addActionListener(e -> {
            orihime_app.img_explanation_fname = "qqq/af/select_polygon.png";
            orihime_app.readImageFromFile3();

            orihime_app.i_mouse_modeA = MouseMode.SELECT_POLYGON_66;
            System.out.println("i_mouse_modeA = " + orihime_app.i_mouse_modeA);
            orihime_app.Button_kyoutuu_sagyou();
            orihime_app.repaint();


        });
        pnl_03.add(Button_select_polygon);

        //Button_select_polygon.setBorder(new LineBorder(Color.green, 4, true));
        Button_select_polygon.setBackground(Color.green);
        //Button_select_polygon.setBackground(Color.white);
        Button_select_polygon.setMargin(new Insets(0, 0, 0, 0));


//------------------------------------------------
//------------------------------------------------

        JButton Button_unselect_polygon =
                new JButton("unselect_polygon");
        Button_unselect_polygon.addActionListener(e -> {
            orihime_app.img_explanation_fname = "qqq/af/unselect_polygon.png";
            orihime_app.readImageFromFile3();

            orihime_app.i_mouse_modeA = MouseMode.UNSELECT_POLYGON_67;
            System.out.println("i_mouse_modeA = " + orihime_app.i_mouse_modeA);
            orihime_app.Button_kyoutuu_sagyou();
            orihime_app.repaint();


        });
        pnl_03.add(Button_unselect_polygon);

        Button_unselect_polygon.setBackground(Color.green);
        Button_unselect_polygon.setMargin(new Insets(0, 0, 0, 0));

//------------------------------------------------
        Panel pnl_04 = new Panel();
        pnl_04.setBackground(Color.PINK);
        pnl_04.setLayout(new GridLayout(1, 3));
        pnl_00.add(pnl_04);


//------------------------------------------------


        JButton Button_select_lX =
                new JButton("select_lX");
        Button_select_lX.addActionListener(e -> {
            orihime_app.img_explanation_fname = "qqq/af/select_lX.png";
            orihime_app.readImageFromFile3();

            orihime_app.i_mouse_modeA = MouseMode.SELECT_LINE_INTERSECTING_68;
            System.out.println("i_mouse_modeA = " + orihime_app.i_mouse_modeA);
            orihime_app.Button_kyoutuu_sagyou();
            orihime_app.repaint();


        });
        pnl_04.add(Button_select_lX);

        //Button_select_lX.setBorder(new LineBorder(Color.green, 4, true));
        Button_select_lX.setBackground(Color.green);
        //Button_select_lX.setBackground(Color.white);
        Button_select_lX.setMargin(new Insets(0, 0, 0, 0));


//------------------------------------------------
        JButton Button_unselect_lX =
                new JButton("unselect_lX");
        Button_unselect_lX.addActionListener(e -> {
            orihime_app.img_explanation_fname = "qqq/af/unselect_lX.png";
            orihime_app.readImageFromFile3();

            orihime_app.i_mouse_modeA = MouseMode.UNSELECT_LINE_INTERSECTING_69;
            System.out.println("i_mouse_modeA = " + orihime_app.i_mouse_modeA);
            orihime_app.Button_kyoutuu_sagyou();
            orihime_app.repaint();


        });
        pnl_04.add(Button_unselect_lX);

        Button_unselect_lX.setBackground(Color.green);
        Button_unselect_lX.setMargin(new Insets(0, 0, 0, 0));


//----------------------------------------------------------------------------------------------
        Panel pnl_05 = new Panel();
        pnl_05.setBackground(Color.PINK);
        pnl_05.setLayout(new GridLayout(1, 3));
        pnl_00.add(pnl_05);

//----------------------------------------------------------------------------------------------
        JButton Button_Del_l = new JButton("Del_l");
        Button_Del_l.addActionListener(e -> {
            orihime_app.img_explanation_fname = "qqq/af/Del_l.png";
            orihime_app.readImageFromFile3();

            orihime_app.i_mouse_modeA = MouseMode.CREASE_DELETE_OVERLAPPING_64;
            System.out.println("i_mouse_modeA = " + orihime_app.i_mouse_modeA);
            orihime_app.Button_kyoutuu_sagyou();
            orihime_app.repaint();


        });
        pnl_05.add(Button_Del_l);

        Button_Del_l.setMargin(new Insets(0, 0, 0, 0));

//------------------------------------------------


        JButton Button_Del_l_X = new JButton("Del_l_X");
        Button_Del_l_X.addActionListener(e -> {
            orihime_app.img_explanation_fname = "qqq/af/Del_l_X.png";
            orihime_app.readImageFromFile3();

            orihime_app.i_mouse_modeA = MouseMode.CREASE_DELETE_INTERSECTING_65;
            System.out.println("i_mouse_modeA = " + orihime_app.i_mouse_modeA);
            orihime_app.Button_kyoutuu_sagyou();
            orihime_app.repaint();


        });
        pnl_05.add(Button_Del_l_X);

        Button_Del_l_X.setMargin(new Insets(0, 0, 0, 0));

//------------------------------------------------

//追加フレーム表示の最初にやること

//20200930 ckbox_add_frame_to_front.setSelected(true);//表示するかどうかの選択

        //表示するかどうかの選択
        ckbox_add_frame_SelectAnd3click.setSelected(orihime_app.ckbox_add_frame_SelectAnd3click_isSelected == 1);//表示するかどうかの選択


        setVisible(true);
    }
//----------------------------------------------------------------------

    public int ckbox_add_frame_SelectAnd3click_isSelected() {
        if (ckbox_add_frame_SelectAnd3click.isSelected()) {
            return 1;
        }
        return 0;
    }


    public void actionPerformed(ActionEvent e) {
        System.out.println("20190522_");
    }
}
