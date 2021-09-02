package origami_editor.editor;

import origami_editor.record.string_op.StringOp;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GridConfigureDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton gridSizeDecreaseButton;
    private JTextField gridSizeTextField;
    private JButton gridSizeSetButton;
    private JButton gridSizeIncreaseButton;
    private JButton gridColorButton;
    private JButton gridLineWidthDecreaseButton;
    private JButton gridLineWidthIncreaseButton;
    private JButton i_kitei_jyoutaiButton;
    private JButton memori_tate_idouButton;
    private JTextField intervalGridSizeTextField;
    private JButton memori_kankaku_syutokuButton;
    private JButton memori_yoko_idouButton;
    private JButton intervalGridColorButton;
    private JTextField gridXATextField;
    private JTextField gridXBTextField;
    private JTextField gridXCTextField;
    private JTextField gridYATextField;
    private JTextField gridYBTextField;
    private JTextField gridYCTextField;
    private JTextField gridAngleTextField;
    private JButton setGridParametersButton;

    public GridConfigureDialog(App app) {
        super(app, "Configure Grid");
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        gridSizeDecreaseButton.addActionListener(e -> {
            app.setHelp("qqq/kitei2.png");

            app.gridSize = app.gridSize / 2;
            if (app.gridSize < 1) {
                app.gridSize = 1;
            }

            if (app.gridSize < -0) {
                app.gridSize = -1;
            }

            //ボタンの色変え
            if (app.gridSize >= 1) {
                gridSizeIncreaseButton.setForeground(Color.black);
                gridSizeIncreaseButton.setBackground(Color.white);
            }
            if (app.gridSize == 0) {
                gridSizeIncreaseButton.setForeground(Color.black);
                gridSizeIncreaseButton.setBackground(new Color(0, 200, 200));
            }
            //ボタンの色変え(ここまで)
            //ボタンの色変え
            if (app.gridSize >= 1) {
                gridSizeDecreaseButton.setForeground(Color.black);
                gridSizeDecreaseButton.setBackground(Color.white);
            }
            if (app.gridSize == 0) {
                gridSizeDecreaseButton.setForeground(Color.black);
                gridSizeDecreaseButton.setBackground(new Color(0, 200, 200));
            }
            //ボタンの色変え(ここまで)

            gridSizeTextField.setText(String.valueOf(app.gridSize));
            app.es1.setGridSize(app.gridSize);
            app.repaintCanvas();
        });
        gridSizeSetButton.addActionListener(e -> {
            app.setHelp("qqq/syutoku.png");
            app.setGridSize();
        });
        gridSizeIncreaseButton.addActionListener(e -> {
            app.setHelp("qqq/kitei.png");

            app.gridSize = app.gridSize * 2;

            //ボタンの色変え
            if (app.gridSize >= 1) {
                gridSizeIncreaseButton.setForeground(Color.black);
                gridSizeIncreaseButton.setBackground(Color.white);
            }
            if (app.gridSize == 0) {
                gridSizeIncreaseButton.setForeground(Color.black);
                gridSizeIncreaseButton.setBackground(new Color(0, 200, 200));
            }
            //ボタンの色変え(ここまで)
            //ボタンの色変え
            if (app.gridSize >= 1) {
                gridSizeDecreaseButton.setForeground(Color.black);
                gridSizeDecreaseButton.setBackground(Color.white);
            }
            if (app.gridSize == 0) {
                gridSizeDecreaseButton.setForeground(Color.black);
                gridSizeDecreaseButton.setBackground(new Color(0, 200, 200));
            }
            //ボタンの色変え(ここまで)
            gridSizeTextField.setText(String.valueOf(app.gridSize));
            app.es1.setGridSize(app.gridSize);
            app.repaintCanvas();
        });
        gridColorButton.addActionListener(e -> {
            app.setHelp("qqq/kousi_color.png");
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;

            //以下にやりたいことを書く
            Color color = JColorChooser.showDialog(null, "Col", new Color(230, 230, 230));
            if (color != null) {
                app.kus.setGridColor(color);
            }
            //以上でやりたいことは書き終わり

            app.repaintCanvas();
        });
        gridLineWidthDecreaseButton.addActionListener(e -> {
            app.kus.decreaseGridLineWidth();
            app.setHelp("qqq/kousi_senhaba_sage.png");
            app.repaintCanvas();
        });
        gridLineWidthIncreaseButton.addActionListener(e -> {
            app.kus.increaseGridLineWidth();
            app.setHelp("qqq/kousi_senhaba_age.png");
            app.repaintCanvas();
        });
        i_kitei_jyoutaiButton.addActionListener(e -> {
            app.setHelp("qqq/i_kitei_jyoutai.png");

            app.es1.setBaseState(app.es1.getBaseState().advance());
            app.repaintCanvas();
        });
        memori_tate_idouButton.addActionListener(e -> {
            app.setHelp("qqq/memori_tate_idou.png");
            app.es1.a_to_parallel_scale_position_change();

            app.repaintCanvas();
        });
        memori_kankaku_syutokuButton.addActionListener(e -> {
            app.setHelp("qqq/memori_kankaku_syutoku.png");
            int scale_interval_old = app.scale_interval;
            app.scale_interval = StringOp.String2int(intervalGridSizeTextField.getText(), scale_interval_old);
            if (app.scale_interval < 0) {
                app.scale_interval = 1;
            }
            intervalGridSizeTextField.setText(String.valueOf(app.scale_interval));
            app.es1.set_a_to_parallel_scale_interval(app.scale_interval);
            app.es1.set_b_to_parallel_scale_interval(app.scale_interval);
        });
        memori_yoko_idouButton.addActionListener(e -> {
            app.setHelp("qqq/memori_yoko_idou.png");

            app.es1.b_to_parallel_scale_position_change();
        });
        intervalGridColorButton.addActionListener(e -> {
            app.setHelp("qqq/kousi_memori_color.png");
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;


            //以下にやりたいことを書く
            Color color = JColorChooser.showDialog(null, "Col", new Color(180, 200, 180));
            if (color != null) {
                app.kus.setGridScaleColor(color);
            }
            //以上でやりたいことは書き終わり

            app.repaintCanvas();
        });
        setGridParametersButton.addActionListener(e -> {
            app.setHelp("qqq/kousi_syutoku.png");
            app.setGrid();
            app.repaintCanvas();
        });
    }

    public JTextField getGridSizeTextField() {
        return gridSizeTextField;
    }

    public JTextField getIntervalGridSizeTextField() {
        return intervalGridSizeTextField;
    }

    public JTextField getGridXATextField() {
        return gridXATextField;
    }

    public JTextField getGridXBTextField() {
        return gridXBTextField;
    }

    public JTextField getGridXCTextField() {
        return gridXCTextField;
    }

    public JTextField getGridYATextField() {
        return gridYATextField;
    }

    public JTextField getGridYBTextField() {
        return gridYBTextField;
    }

    public JTextField getGridYCTextField() {
        return gridYCTextField;
    }

    public JTextField getGridAngleTextField() {
        return gridAngleTextField;
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(0, 0));
        contentPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        contentPane.add(panel1, BorderLayout.CENTER);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel2, gbc);
        buttonOK = new JButton();
        buttonOK.setText("OK");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(buttonOK, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel2.add(spacer2, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel3, gbc);
        panel3.setBorder(BorderFactory.createTitledBorder(null, "Interval Grid Position", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        memori_tate_idouButton = new JButton();
        memori_tate_idouButton.setIcon(new ImageIcon(getClass().getResource("/ppp/memori_tate_idou.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(memori_tate_idouButton, gbc);
        intervalGridSizeTextField = new JTextField();
        intervalGridSizeTextField.setColumns(2);
        intervalGridSizeTextField.setText("8");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(intervalGridSizeTextField, gbc);
        memori_kankaku_syutokuButton = new JButton();
        memori_kankaku_syutokuButton.setText("S");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(memori_kankaku_syutokuButton, gbc);
        memori_yoko_idouButton = new JButton();
        memori_yoko_idouButton.setIcon(new ImageIcon(getClass().getResource("/ppp/memori_yoko_idou.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(memori_yoko_idouButton, gbc);
        intervalGridColorButton = new JButton();
        intervalGridColorButton.setText("Color");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(intervalGridColorButton, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel4, gbc);
        panel4.setBorder(BorderFactory.createTitledBorder(null, "Line Width", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        gridLineWidthDecreaseButton = new JButton();
        gridLineWidthDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kousi_senhaba_sage.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(gridLineWidthDecreaseButton, gbc);
        gridLineWidthIncreaseButton = new JButton();
        gridLineWidthIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kousi_senhaba_age.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(gridLineWidthIncreaseButton, gbc);
        i_kitei_jyoutaiButton = new JButton();
        i_kitei_jyoutaiButton.setIcon(new ImageIcon(getClass().getResource("/ppp/i_kitei_jyoutai.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(i_kitei_jyoutaiButton, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel5, gbc);
        panel5.setBorder(BorderFactory.createTitledBorder(null, "Grid Size", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        gridSizeDecreaseButton = new JButton();
        gridSizeDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kitei2.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(gridSizeDecreaseButton, gbc);
        gridSizeTextField = new JTextField();
        gridSizeTextField.setColumns(2);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(gridSizeTextField, gbc);
        gridSizeSetButton = new JButton();
        gridSizeSetButton.setText("S");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(gridSizeSetButton, gbc);
        gridSizeIncreaseButton = new JButton();
        gridSizeIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kitei.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(gridSizeIncreaseButton, gbc);
        gridColorButton = new JButton();
        gridColorButton.setText("Color");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(gridColorButton, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel6, gbc);
        panel6.setBorder(BorderFactory.createTitledBorder(null, "Properties", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        gridXATextField = new JTextField();
        gridXATextField.setText("0.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel6.add(gridXATextField, gbc);
        final JLabel label1 = new JLabel();
        label1.setIcon(new ImageIcon(getClass().getResource("/ppp/plus_min.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel6.add(label1, gbc);
        gridXBTextField = new JTextField();
        gridXBTextField.setText("1.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel6.add(gridXBTextField, gbc);
        final JLabel label2 = new JLabel();
        label2.setIcon(new ImageIcon(getClass().getResource("/ppp/root_min.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        panel6.add(label2, gbc);
        gridXCTextField = new JTextField();
        gridXCTextField.setText("1.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        panel6.add(gridXCTextField, gbc);
        gridYATextField = new JTextField();
        gridYATextField.setText("0.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel6.add(gridYATextField, gbc);
        gridYBTextField = new JTextField();
        gridYBTextField.setText("1.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        panel6.add(gridYBTextField, gbc);
        gridYCTextField = new JTextField();
        gridYCTextField.setText("1.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        panel6.add(gridYCTextField, gbc);
        final JLabel label3 = new JLabel();
        label3.setIcon(new ImageIcon(getClass().getResource("/ppp/root_min.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        panel6.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setIcon(new ImageIcon(getClass().getResource("/ppp/plus_min.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel6.add(label4, gbc);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel7, gbc);
        panel7.setBorder(BorderFactory.createTitledBorder(null, "Grid Angle", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        gridAngleTextField = new JTextField();
        gridAngleTextField.setHorizontalAlignment(11);
        gridAngleTextField.setText("90.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(gridAngleTextField, gbc);
        setGridParametersButton = new JButton();
        setGridParametersButton.setText("Set");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(setGridParametersButton, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
