package jp.gr.java_conf.mt777.origami.orihime;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class OpenFrame extends Frame implements ActionListener {
    //変数の宣言
    App orihime_app;


    public JCheckBox ckbox_add_frame_SelectAnd3click;//20200930


    //スレッド作成時に実行される処理

    //	public OpenFrame(String name) {
    public OpenFrame(String name, App app0) {
        super(name);//本来、子コンストラクタは先頭で親コンストラクタを呼び出さなければならない。superは親インスタンスを示す。

        orihime_app = app0;

        //setSize(100, orihime_ap.getSize().height- 108);
        //setSize(300, 550);
        setSize(300, 250);
        //setSize(200, 300);

        System.out.println("Y：" + orihime_app.getSize().height);//.height

        //setLocation(orihime_ap.getLocation().getX(), orihime_ap.getLocation().getY());

        setLocation(
                (int) (orihime_app.getLocation().getX()) + orihime_app.getSize().width - getSize().width - 131
                ,
                (int) (orihime_app.getLocation().getY()) + orihime_app.getSize().height - getSize().height - 44
        );
        //setVisible(true);//ここでsetVisible(true);にすると最初はここまでの設定で可視化されてしまう。まだ初期設定があるなら、それが終わってからsetVisible(true);するべき。

        //   Container contentPane = getContentPane(); //JFrame用
        //   contentPane.setBackground(Color.ORANGE);//JFrame用


        setResizable(false);
        //ユーザーがこのフレームのサイズを変更できるかどうかを設定します。

        //setUndecorated(true);
        //このフレームの装飾の有効化と無効化を切り替えます。
/*
		MenuBar mb = new MenuBar();
		mb.add(new Menu("Sakura"));
		mb.add(new Menu("Tomoyo" , true));
		setMenuBar(mb);
*/


        addWindowListener(new WindowAdapter() {//終了ボタンを有効化
            public void windowClosing(WindowEvent evt) {
                //owari();//ウィンドウ上辺右端のXを押したときに行う作業

                //System.exit(0);//これだとプログラム全体が終了してしまう
                orihime_app.i_add_frame = 0;
                dispose();
            }
/*
  public void windowIconifing(WindowEvent evt){
    System.out.println("windowIconifiedwwwwwww");
  }
*/

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


        //addMouseListener(this);
        //addMouseMotionListener(this);
        //addMouseWheelListener(this);

        //setLayout(new GridLayout(2, 3));

        // setLayout(new BorderLayout());

        //追加フレームのレイアウト等条件設定
        setLayout(new GridLayout(1, 1));
        //setLayout(new BorderLayout());//Frame用
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


        //setVisible(true);

//------------------------------------------------

//チェックボックス
/*20200930
	ckbox_add_frame_to_front = new JCheckBox("toFront");
	ckbox_add_frame_to_front.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
					orihime_ap.img_kaisetu_fname=
"qqq/af/ckbox_add_frame_to_front.png";
					orihime_ap.readImageFromFile3();

					if(ckbox_add_frame_SelectAnd3click_isSelected()==1){
					orihime_ap.ckbox_add_frame_SelectAnd3click_isSelected=1;
					}else{orihime_ap.ckbox_add_frame_SelectAnd3click_isSelected=0;}
					orihime_ap.repaint();
	}});
	ckbox_add_frame_to_front.setIcon(	 new ImageIcon(getClass().getClassLoader().getResource(
"ppp/af/ckbox_add_frame_to_front_off.png")));
	ckbox_add_frame_to_front.setSelectedIcon(new ImageIcon(getClass().getClassLoader().getResource(
"ppp/af/ckbox_add_frame_to_front_on.png")));

	ckbox_add_frame_to_front.setMargin(new Insets(0,0,0,0));
			pnl_01.add(
	ckbox_add_frame_to_front);
20200930 */

//チェックボックス
//20200930ここから
        ckbox_add_frame_SelectAnd3click = new JCheckBox("sel<=>mcm");
        ckbox_add_frame_SelectAnd3click.addActionListener(e -> {
            orihime_app.img_kaisetu_fname =
                    "qqq/af/ckbox_add_frame_SelectAnd3click.png";
            orihime_app.readImageFromFile3();

            if (ckbox_add_frame_SelectAnd3click_isSelected() == 1) {
                orihime_app.ckbox_add_frame_SelectAnd3click_isSelected = 1;
            } else {
                orihime_app.ckbox_add_frame_SelectAnd3click_isSelected = 0;
            }
            orihime_app.repaint();
        });
        ckbox_add_frame_SelectAnd3click.setIcon(new ImageIcon(getClass().getClassLoader().getResource(
                "ppp/af/ckbox_add_frame_SelectAnd3click_off.png")));
        ckbox_add_frame_SelectAnd3click.setSelectedIcon(new ImageIcon(getClass().getClassLoader().getResource(
                "ppp/af/ckbox_add_frame_SelectAnd3click_on.png")));
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
            orihime_app.img_kaisetu_fname = "qqq/af/O_F_check.png";
            orihime_app.readImageFromFile3();

            orihime_app.i_mouse_modeA = 63;
            System.out.println("i_mouse_modeA = " + orihime_app.i_mouse_modeA);
            orihime_app.Button_kyoutuu_sagyou();
            orihime_app.repaint();


        });
        pnl_01.add(Button_O_F_check);

        //Button_O_F_check.setBorder(new LineBorder(Color.green, 4, true));
        //Button_O_F_check.setBackground(Color.green);
        //Button_O_F_check.setBackground(Color.white);
        Button_O_F_check.setMargin(new Insets(0, 0, 0, 0));


//-----------------------------------------------

        Panel pnl_02 = new Panel();
        pnl_02.setBackground(Color.PINK);
        pnl_02.setLayout(new GridLayout(1, 3));
        pnl_00.add(pnl_02);
//------------------------------------------------


/*
//71 71 71 -------------71;複数線分延長モード。
		JButton		Button_senbun_entyou_3	= new JButton(	""	);
				Button_senbun_entyou_3.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e) {
	orihime_ap.img_kaisetu_fname="qqq/af/senbun_entyou_3.png";orihime_ap.readImageFromFile3();

						orihime_ap.i_mouse_modeA=   71    ;
						orihime_ap.iro_sitei_ato_ni_jissisuru_sagyou_bangou=71;//何のためにやるかわすれたけどやっとく20201107 //20201206多分何もやっていない。将来的に削除予定
						System.out.println("i_mouse_modeA = "+orihime_ap.i_mouse_modeA);

						orihime_ap.es1.unselect_all();
						orihime_ap.Button_kyoutuu_sagyou();
						orihime_ap.repaint();

		}});
		pnl_02.add(	Button_senbun_entyou_3);

		Button_senbun_entyou_3.setMargin(new Insets(0,0,0,0));
		Button_senbun_entyou_3.setIcon(new ImageIcon(getClass().getClassLoader().getResource(
		  "ppp/af/senbun_entyou_3.png")));

// -------------71モード。ここまで
*/
// -------------39;折り畳み可能線+格子点系入力
        JButton Button_oritatami_kanousen_and_kousitenkei = new JButton("");
        Button_oritatami_kanousen_and_kousitenkei.addActionListener(e -> {
            orihime_app.img_kaisetu_fname = "qqq/oritatami_kanousen_and_kousitenkei.png";
            orihime_app.readImageFromFile3();

            orihime_app.i_mouse_modeA = 39;
            orihime_app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = 39;
            System.out.println("i_mouse_modeA = " + orihime_app.i_mouse_modeA);

            orihime_app.es1.unselect_all();
            orihime_app.Button_kyoutuu_sagyou();
            orihime_app.repaint();
        });
        pnl_02.add(Button_oritatami_kanousen_and_kousitenkei);

        Button_oritatami_kanousen_and_kousitenkei.setMargin(new Insets(0, 0, 0, 0));
        Button_oritatami_kanousen_and_kousitenkei.setIcon(new ImageIcon(getClass().getClassLoader().getResource(
                "ppp/oritatami_kanousen_and_kousitenkei.png")));


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
            orihime_app.img_kaisetu_fname = "qqq/af/select_polygon.png";
            orihime_app.readImageFromFile3();

            orihime_app.i_mouse_modeA = 66;
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
            orihime_app.img_kaisetu_fname = "qqq/af/unselect_polygon.png";
            orihime_app.readImageFromFile3();

            orihime_app.i_mouse_modeA = 67;
            System.out.println("i_mouse_modeA = " + orihime_app.i_mouse_modeA);
            orihime_app.Button_kyoutuu_sagyou();
            orihime_app.repaint();


        });
        pnl_03.add(Button_unselect_polygon);

        //Button_unselect_polygon.setBorder(new LineBorder(Color.green, 4, true));
        Button_unselect_polygon.setBackground(Color.green);
        //Button_unselect_polygon.setBackground(Color.white);
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
            orihime_app.img_kaisetu_fname = "qqq/af/select_lX.png";
            orihime_app.readImageFromFile3();

            orihime_app.i_mouse_modeA = 68;
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
            orihime_app.img_kaisetu_fname = "qqq/af/unselect_lX.png";
            orihime_app.readImageFromFile3();

            orihime_app.i_mouse_modeA = 69;
            System.out.println("i_mouse_modeA = " + orihime_app.i_mouse_modeA);
            orihime_app.Button_kyoutuu_sagyou();
            orihime_app.repaint();


        });
        pnl_04.add(Button_unselect_lX);

        //Button_unselect_lX.setBorder(new LineBorder(Color.green, 4, true));
        Button_unselect_lX.setBackground(Color.green);
        //Button_unselect_lX.setBackground(Color.white);
        Button_unselect_lX.setMargin(new Insets(0, 0, 0, 0));


//----------------------------------------------------------------------------------------------
        Panel pnl_05 = new Panel();
        // pnl_05.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e) { }});
        pnl_05.setBackground(Color.PINK);
        pnl_05.setLayout(new GridLayout(1, 3));
        pnl_00.add(pnl_05);

//----------------------------------------------------------------------------------------------
        JButton Button_Del_l =
                new JButton("Del_l");
        Button_Del_l.addActionListener(e -> {
            orihime_app.img_kaisetu_fname = "qqq/af/Del_l.png";
            orihime_app.readImageFromFile3();

            orihime_app.i_mouse_modeA = 64;
            System.out.println("i_mouse_modeA = " + orihime_app.i_mouse_modeA);
            orihime_app.Button_kyoutuu_sagyou();
            orihime_app.repaint();


        });
        pnl_05.add(Button_Del_l);

        //Button_O_F_check.setBorder(new LineBorder(Color.green, 4, true));
        //Button_O_F_check.setBackground(Color.green);
        //Button_O_F_check.setBackground(Color.white);
        Button_Del_l.setMargin(new Insets(0, 0, 0, 0));

//------------------------------------------------


        JButton Button_Del_l_X =
                new JButton("Del_l_X");
        Button_Del_l_X.addActionListener(e -> {
            orihime_app.img_kaisetu_fname = "qqq/af/Del_l_X.png";
            orihime_app.readImageFromFile3();

            orihime_app.i_mouse_modeA = 65;
            System.out.println("i_mouse_modeA = " + orihime_app.i_mouse_modeA);
            orihime_app.Button_kyoutuu_sagyou();
            orihime_app.repaint();


        });
        pnl_05.add(Button_Del_l_X);

        //Button_O_F_check.setBorder(new LineBorder(Color.green, 4, true));
        //Button_O_F_check.setBackground(Color.green);
        //Button_O_F_check.setBackground(Color.white);
        Button_Del_l_X.setMargin(new Insets(0, 0, 0, 0));

//------------------------------------------------

/* 20200930


		Panel   pnl_06 = new Panel();
			pnl_06.setBackground(Color.PINK);
			pnl_06.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_06);

		Panel   pnl_07 = new Panel();
			pnl_07.setBackground(Color.PINK);
			pnl_07.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_07);





		Panel   pnl_08 = new Panel();
			pnl_08.setBackground(Color.PINK);
			pnl_08.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_08);
		Panel   pnl_09 = new Panel();
			pnl_09.setBackground(Color.PINK);
			pnl_09.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_09);
		Panel   pnl_10 = new Panel();
			pnl_10.setBackground(Color.PINK);
			pnl_10.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_10);
		Panel   pnl_11 = new Panel();
			pnl_11.setBackground(Color.PINK);
			pnl_11.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_11);
		Panel   pnl_12 = new Panel();
			pnl_12.setBackground(Color.PINK);
			pnl_12.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_12);
		Panel   pnl_13 = new Panel();
			pnl_13.setBackground(Color.PINK);
			pnl_13.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_13);
		Panel   pnl_14 = new Panel();
			pnl_14.setBackground(Color.PINK);
			pnl_14.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_14);
		Panel   pnl_15 = new Panel();
			pnl_15.setBackground(Color.PINK);
			pnl_15.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_15);
		Panel   pnl_16 = new Panel();
			pnl_16.setBackground(Color.PINK);
			pnl_16.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_16);
		Panel   pnl_17 = new Panel();
			pnl_17.setBackground(Color.PINK);
			pnl_17.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_17);
		Panel   pnl_18 = new Panel();
			pnl_18.setBackground(Color.PINK);
			pnl_18.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_18);
		Panel   pnl_19 = new Panel();
			pnl_19.setBackground(Color.PINK);
			pnl_19.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_19);
		Panel   pnl_20 = new Panel();
			pnl_20.setBackground(Color.PINK);
			pnl_20.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_20);
		Panel   pnl_21 = new Panel();
			pnl_21.setBackground(Color.PINK);
			pnl_21.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_21);
		Panel   pnl_22 = new Panel();
			pnl_22.setBackground(Color.PINK);
			pnl_22.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_22);
		Panel   pnl_23 = new Panel();
			pnl_23.setBackground(Color.PINK);
			pnl_23.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_23);
		Panel   pnl_24 = new Panel();
			pnl_24.setBackground(Color.PINK);
			pnl_24.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_24);
		Panel   pnl_25 = new Panel();
			pnl_25.setBackground(Color.PINK);
			pnl_25.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_25);
		Panel   pnl_26 = new Panel();
			pnl_26.setBackground(Color.PINK);
			pnl_26.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_26);
		Panel   pnl_27 = new Panel();
			pnl_27.setBackground(Color.PINK);
			pnl_27.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_27);
		Panel   pnl_28 = new Panel();
			pnl_28.setBackground(Color.PINK);
			pnl_28.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_28);
		Panel   pnl_29 = new Panel();
			pnl_29.setBackground(Color.PINK);
			pnl_29.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_29);
		Panel   pnl_30 = new Panel();
			pnl_30.setBackground(Color.PINK);
			pnl_30.setLayout(new GridLayout(1, 3));
	     pnl_00.add(pnl_30);

20200930 */

//追加フレーム表示の最初にやること

//20200930 ckbox_add_frame_to_front.setSelected(true);//表示するかどうかの選択


        if (orihime_app.ckbox_add_frame_SelectAnd3click_isSelected != 1) {
            ckbox_add_frame_SelectAnd3click.setSelected(false);//表示するかどうかの選択
        } else if (orihime_app.ckbox_add_frame_SelectAnd3click_isSelected == 1) {
            ckbox_add_frame_SelectAnd3click.setSelected(true);//表示するかどうかの選択
        }


        setVisible(true);
    }
//----------------------------------------------------------------------
    //public void a() {
    //	new OpenFrame("Additional Frame");
    //}


    public int ckbox_add_frame_SelectAnd3click_isSelected() {
        if (ckbox_add_frame_SelectAnd3click.isSelected()) {
            return 1;
        }
        return 0;
    }


    public void actionPerformed(ActionEvent e) {
        //new OpenFrame("Additional Frame");
        System.out.println("20190522_");
    }
/*
	public static void main(String[] args) {
		JFrame frame = new OpenFrame("First Frame");
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
*/
}
