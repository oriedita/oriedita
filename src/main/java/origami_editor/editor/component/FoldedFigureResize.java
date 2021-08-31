package origami_editor.editor.component;

import origami_editor.editor.App;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FoldedFigureResize extends JPanel {
    private JButton foldedFigureSizeDecreaseButton;
    private JPanel panel1;
    private JTextField foldedFigureSizeTextField;
    private JButton foldedFigureSizeSetButton;
    private JButton foldedFigureSizeIncreaseButton;

    public FoldedFigureResize(App app) {
        add($$$getRootComponent$$$());

        foldedFigureSizeSetButton.addActionListener(e -> {
            double d_foldedFigure_scale_factor_old = app.OZ.d_foldedFigure_scale_factor;
            app.OZ.d_foldedFigure_scale_factor = app.String2double(foldedFigureSizeTextField.getText(), d_foldedFigure_scale_factor_old);
            if (app.OZ.d_foldedFigure_scale_factor <= 0.0) {
                app.OZ.d_foldedFigure_scale_factor = d_foldedFigure_scale_factor_old;
            }
            foldedFigureSizeTextField.setText(String.valueOf(app.OZ.d_foldedFigure_scale_factor));
            if (app.OZ.d_foldedFigure_scale_factor != d_foldedFigure_scale_factor_old) {
                app.OZ.camera_of_foldedFigure.setCameraZoomX(app.OZ.d_foldedFigure_scale_factor);
                app.OZ.camera_of_foldedFigure.setCameraZoomY(app.OZ.d_foldedFigure_scale_factor);

                app.OZ.camera_of_foldedFigure_front.setCameraZoomX(app.OZ.d_foldedFigure_scale_factor);
                app.OZ.camera_of_foldedFigure_front.setCameraZoomY(app.OZ.d_foldedFigure_scale_factor);

                app.OZ.camera_of_foldedFigure_rear.setCameraZoomX(app.OZ.d_foldedFigure_scale_factor);
                app.OZ.camera_of_foldedFigure_rear.setCameraZoomY(app.OZ.d_foldedFigure_scale_factor);

                app.OZ.camera_of_transparent_front.setCameraZoomX(app.OZ.d_foldedFigure_scale_factor);
                app.OZ.camera_of_transparent_front.setCameraZoomY(app.OZ.d_foldedFigure_scale_factor);

                app.OZ.camera_of_transparent_rear.setCameraZoomX(app.OZ.d_foldedFigure_scale_factor);
                app.OZ.camera_of_transparent_rear.setCameraZoomY(app.OZ.d_foldedFigure_scale_factor);
            }
            foldedFigureSizeTextField.setText(String.valueOf(app.OZ.d_foldedFigure_scale_factor));
            foldedFigureSizeTextField.setCaretPosition(0);
            app.repaintCanvas();

            app.setHelp("qqq/oriagarizu_syukusyaku_keisuu_set.png");
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        foldedFigureSizeDecreaseButton.addActionListener(e -> {
            app.setHelp("qqq/oriagari_syukusyou.png");

            app.OZ.d_foldedFigure_scale_factor = app.OZ.d_foldedFigure_scale_factor / Math.sqrt(Math.sqrt(Math.sqrt(2.0)));
            app.OZ.camera_of_foldedFigure.setCameraZoomX(app.OZ.d_foldedFigure_scale_factor);
            app.OZ.camera_of_foldedFigure.setCameraZoomY(app.OZ.d_foldedFigure_scale_factor);

            app.OZ.camera_of_foldedFigure_front.setCameraZoomX(app.OZ.d_foldedFigure_scale_factor);
            app.OZ.camera_of_foldedFigure_front.setCameraZoomY(app.OZ.d_foldedFigure_scale_factor);

            app.OZ.camera_of_foldedFigure_rear.setCameraZoomX(app.OZ.d_foldedFigure_scale_factor);
            app.OZ.camera_of_foldedFigure_rear.setCameraZoomY(app.OZ.d_foldedFigure_scale_factor);

            app.OZ.camera_of_transparent_front.setCameraZoomX(app.OZ.d_foldedFigure_scale_factor);
            app.OZ.camera_of_transparent_front.setCameraZoomY(app.OZ.d_foldedFigure_scale_factor);

            app.OZ.camera_of_transparent_rear.setCameraZoomX(app.OZ.d_foldedFigure_scale_factor);
            app.OZ.camera_of_transparent_rear.setCameraZoomY(app.OZ.d_foldedFigure_scale_factor);

            foldedFigureSizeTextField.setText(String.valueOf(app.OZ.d_foldedFigure_scale_factor));
            foldedFigureSizeTextField.setCaretPosition(0);

            app.Button_shared_operation();
            app.repaintCanvas();
        });
        foldedFigureSizeIncreaseButton.addActionListener(e -> {
            app.setHelp("qqq/oriagari_kakudai.png");

            app.OZ.d_foldedFigure_scale_factor = app.OZ.d_foldedFigure_scale_factor * Math.sqrt(Math.sqrt(Math.sqrt(2.0)));
            app.OZ.camera_of_foldedFigure.setCameraZoomX(app.OZ.d_foldedFigure_scale_factor);
            app.OZ.camera_of_foldedFigure.setCameraZoomY(app.OZ.d_foldedFigure_scale_factor);

            app.OZ.camera_of_foldedFigure_front.setCameraZoomX(app.OZ.d_foldedFigure_scale_factor);
            app.OZ.camera_of_foldedFigure_front.setCameraZoomY(app.OZ.d_foldedFigure_scale_factor);

            app.OZ.camera_of_foldedFigure_rear.setCameraZoomX(app.OZ.d_foldedFigure_scale_factor);
            app.OZ.camera_of_foldedFigure_rear.setCameraZoomY(app.OZ.d_foldedFigure_scale_factor);

            app.OZ.camera_of_transparent_front.setCameraZoomX(app.OZ.d_foldedFigure_scale_factor);
            app.OZ.camera_of_transparent_front.setCameraZoomY(app.OZ.d_foldedFigure_scale_factor);

            app.OZ.camera_of_transparent_rear.setCameraZoomX(app.OZ.d_foldedFigure_scale_factor);
            app.OZ.camera_of_transparent_rear.setCameraZoomY(app.OZ.d_foldedFigure_scale_factor);

            foldedFigureSizeTextField.setText(String.valueOf(app.OZ.d_foldedFigure_scale_factor));
            foldedFigureSizeTextField.setCaretPosition(0);

            app.Button_shared_operation();
            app.repaintCanvas();
        });
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
        panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        panel1.setBackground(new Color(-1));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        foldedFigureSizeDecreaseButton = new JButton();
        foldedFigureSizeDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oriagari_syukusyou.png")));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(foldedFigureSizeDecreaseButton, gbc);
        foldedFigureSizeTextField = new JTextField();
        foldedFigureSizeTextField.setColumns(2);
        foldedFigureSizeTextField.setHorizontalAlignment(4);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(foldedFigureSizeTextField, gbc);
        foldedFigureSizeSetButton = new JButton();
        foldedFigureSizeSetButton.setText("S");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(foldedFigureSizeSetButton, gbc);
        foldedFigureSizeIncreaseButton = new JButton();
        foldedFigureSizeIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oriagari_kakudai.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(foldedFigureSizeIncreaseButton, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

    public JTextField getSizeTextField() {
        return foldedFigureSizeTextField;
    }
}
