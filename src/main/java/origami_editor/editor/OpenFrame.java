package origami_editor.editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class OpenFrame extends JDialog implements ActionListener {
    public JCheckBox ckbox_add_frame_SelectAnd3click;//20200930
    App app;

    //Process executed when thread is created
    public OpenFrame(String name, App app0) {
        super(app0, name);//Originally, the child constructor must call the parent constructor at the beginning. super indicates the parent instance.

        setAlwaysOnTop(true);
        app = app0;

        setSize(300, 250);

        System.out.println("Y：" + app.getSize().height);//.height

        setLocation(
                (int) (app.getLocation().getX()) + app.getSize().width - getSize().width - 131
                ,
                (int) (app.getLocation().getY()) + app.getSize().height - getSize().height - 44
        );

        setResizable(false);
        //Sets whether the user can resize this frame.

        //Toggle between enabling and disabling decorations for this frame.

        addWindowListener(new WindowAdapter() {//Toggle between enabling and disabling decorations for this frame.
            public void windowClosing(WindowEvent evt) {
                app.i_add_frame = false;
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

        });//Enable the end button So far.

        //Setting conditions such as layout of additional frames
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridLayout(1, 1));

        //Creating the left side (west side) panel


        JPanel pnl_00 = new JPanel();
        pnl_00.setLayout(new GridLayout(10, 1));
        contentPane.add(pnl_00);

        JPanel pnl_01 = new JPanel();
        pnl_01.setLayout(new GridLayout(1, 3));
        pnl_00.add(pnl_01);


//Checkbox
//20200930 From here
        ckbox_add_frame_SelectAnd3click = new JCheckBox("sel<=>mcm");
        ckbox_add_frame_SelectAnd3click.addActionListener(e -> {
            app.img_explanation_fname = "qqq/af/ckbox_add_frame_SelectAnd3click.png";
            app.updateExplanation();

            app.ckbox_add_frame_SelectAnd3click_isSelected = ckbox_add_frame_SelectAnd3click_isSelected();
            app.repaint();
        });
        ckbox_add_frame_SelectAnd3click.setIcon(ResourceUtil.createImageIcon("ppp/af/ckbox_add_frame_SelectAnd3click_off.png"));
        ckbox_add_frame_SelectAnd3click.setSelectedIcon(ResourceUtil.createImageIcon("ppp/af/ckbox_add_frame_SelectAnd3click_on.png"));
        ckbox_add_frame_SelectAnd3click.setBorderPainted(false);
        ckbox_add_frame_SelectAnd3click.setMargin(new Insets(0, 0, 0, 0));
        pnl_01.add(
                ckbox_add_frame_SelectAnd3click);
//20200930 to this point

//----------------------------------------------------------------------------------------------
        JButton Button_O_F_check = new JButton("O_F_check");
        Button_O_F_check.addActionListener(e -> {
            app.img_explanation_fname = "qqq/af/O_F_check.png";
            app.updateExplanation();

            app.i_mouse_modeA = MouseMode.FLAT_FOLDABLE_CHECK_63;
            System.out.println("i_mouse_modeA = " + app.i_mouse_modeA);
            app.Button_kyoutuu_sagyou();
            app.repaint();
        });
        pnl_01.add(Button_O_F_check);

        Button_O_F_check.setMargin(new Insets(0, 0, 0, 0));

//-----------------------------------------------

        JPanel pnl_02 = new JPanel();
        pnl_02.setLayout(new GridLayout(1, 3));
        pnl_00.add(pnl_02);
//------------------------------------------------
// -------------39;Foldable line + grid point system input
        JButton Button_folding_kanousen_and_kousitenkei = new JButton("");
        Button_folding_kanousen_and_kousitenkei.addActionListener(e -> {
            app.img_explanation_fname = "qqq/oritatami_kanousen_and_kousitenkei.png";
            app.updateExplanation();

            app.i_mouse_modeA = MouseMode.FOLDABLE_LINE_INPUT_39;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.FOLDABLE_LINE_INPUT_39;
            System.out.println("i_mouse_modeA = " + app.i_mouse_modeA);

            app.es1.unselect_all();
            app.Button_kyoutuu_sagyou();
            app.repaint();
        });
        pnl_02.add(Button_folding_kanousen_and_kousitenkei);

        Button_folding_kanousen_and_kousitenkei.setMargin(new Insets(0, 0, 0, 0));
        Button_folding_kanousen_and_kousitenkei.setIcon(ResourceUtil.createImageIcon("ppp/oritatami_kanousen_and_kousitenkei.png"));


// -------------39;Foldable line + grid point system input. to this point
//------------------------------------------------

        JPanel pnl_03 = new JPanel();
        pnl_03.setLayout(new GridLayout(1, 3));
        pnl_00.add(pnl_03);
//----------------------------------------------------------------------------------------------
//------------------------------------------------

        JButton Button_select_polygon = new JButton("select_polygon");
        Button_select_polygon.addActionListener(e -> {
            app.img_explanation_fname = "qqq/af/select_polygon.png";
            app.updateExplanation();

            app.i_mouse_modeA = MouseMode.SELECT_POLYGON_66;
            System.out.println("i_mouse_modeA = " + app.i_mouse_modeA);
            app.Button_kyoutuu_sagyou();
            app.repaint();
        });
        pnl_03.add(Button_select_polygon);

        Button_select_polygon.setBackground(Color.green);
        Button_select_polygon.setMargin(new Insets(0, 0, 0, 0));

//------------------------------------------------
//------------------------------------------------

        JButton Button_unselect_polygon = new JButton("unselect_polygon");
        Button_unselect_polygon.addActionListener(e -> {
            app.img_explanation_fname = "qqq/af/unselect_polygon.png";
            app.updateExplanation();

            app.i_mouse_modeA = MouseMode.UNSELECT_POLYGON_67;
            System.out.println("i_mouse_modeA = " + app.i_mouse_modeA);
            app.Button_kyoutuu_sagyou();
            app.repaint();


        });
        pnl_03.add(Button_unselect_polygon);

        Button_unselect_polygon.setBackground(Color.green);
        Button_unselect_polygon.setMargin(new Insets(0, 0, 0, 0));

//------------------------------------------------
        JPanel pnl_04 = new JPanel();
//        pnl_04.setBackground(Color.PINK);
        pnl_04.setLayout(new GridLayout(1, 3));
        pnl_00.add(pnl_04);

//------------------------------------------------

        JButton Button_select_lX = new JButton("select_lX");
        Button_select_lX.addActionListener(e -> {
            app.img_explanation_fname = "qqq/af/select_lX.png";
            app.updateExplanation();

            app.i_mouse_modeA = MouseMode.SELECT_LINE_INTERSECTING_68;
            System.out.println("i_mouse_modeA = " + app.i_mouse_modeA);
            app.Button_kyoutuu_sagyou();
            app.repaint();
        });
        pnl_04.add(Button_select_lX);

        Button_select_lX.setBackground(Color.green);
        Button_select_lX.setMargin(new Insets(0, 0, 0, 0));


//------------------------------------------------
        JButton Button_unselect_lX = new JButton("unselect_lX");
        Button_unselect_lX.addActionListener(e -> {
            app.img_explanation_fname = "qqq/af/unselect_lX.png";
            app.updateExplanation();

            app.i_mouse_modeA = MouseMode.UNSELECT_LINE_INTERSECTING_69;
            System.out.println("i_mouse_modeA = " + app.i_mouse_modeA);
            app.Button_kyoutuu_sagyou();
            app.repaint();
        });
        pnl_04.add(Button_unselect_lX);

        Button_unselect_lX.setBackground(Color.green);
        Button_unselect_lX.setMargin(new Insets(0, 0, 0, 0));

//----------------------------------------------------------------------------------------------
        JPanel pnl_05 = new JPanel();
//        pnl_05.setBackground(Color.PINK);
        pnl_05.setLayout(new GridLayout(1, 3));
        pnl_00.add(pnl_05);

//----------------------------------------------------------------------------------------------
        JButton Button_Del_l = new JButton("Del_l");
        Button_Del_l.addActionListener(e -> {
            app.img_explanation_fname = "qqq/af/Del_l.png";
            app.updateExplanation();

            app.i_mouse_modeA = MouseMode.CREASE_DELETE_OVERLAPPING_64;
            System.out.println("i_mouse_modeA = " + app.i_mouse_modeA);
            app.Button_kyoutuu_sagyou();
            app.repaint();
        });
        pnl_05.add(Button_Del_l);

        Button_Del_l.setMargin(new Insets(0, 0, 0, 0));

//------------------------------------------------

        JButton Button_Del_l_X = new JButton("Del_l_X");
        Button_Del_l_X.addActionListener(e -> {
            app.img_explanation_fname = "qqq/af/Del_l_X.png";
            app.updateExplanation();

            app.i_mouse_modeA = MouseMode.CREASE_DELETE_INTERSECTING_65;
            System.out.println("i_mouse_modeA = " + app.i_mouse_modeA);
            app.Button_kyoutuu_sagyou();
            app.repaint();


        });
        pnl_05.add(Button_Del_l_X);

        Button_Del_l_X.setMargin(new Insets(0, 0, 0, 0));

        //------------------------------------------------

        //What to do at the beginning of the additional frame display

        //Select whether to display
        ckbox_add_frame_SelectAnd3click.setSelected(app.ckbox_add_frame_SelectAnd3click_isSelected);//Select whether to display

        setVisible(true);
    }

    public boolean ckbox_add_frame_SelectAnd3click_isSelected() {
        return ckbox_add_frame_SelectAnd3click.isSelected();
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("20190522_");
    }
}
