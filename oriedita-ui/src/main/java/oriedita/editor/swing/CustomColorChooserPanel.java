package oriedita.editor.swing;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import oriedita.editor.databinding.FoldedFigureModel;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

public class CustomColorChooserPanel extends JPanel {
    private JSlider hueSlider;
    private JSlider saturationSlider;
    private JSlider valueSlider;
    private JPanel hsvPanel;
    private JTextField colorCodeTF;
    private JSpinner hueSpinner;
    private JSpinner saturationSpinner;
    private JSpinner valueSpinner;
    private JPanel colorPreviewPanel;
    private final FoldedFigureModel foldedFigureModel;
    private final float[] hsv = new float[3];
    private final SpinnerModel hueSPModel, saturationSPModel, valueSPModel;

    public CustomColorChooserPanel(FoldedFigureModel foldedFigureModel, Color ffModelColor) {
        this.foldedFigureModel = foldedFigureModel;
        // get array of hsv values
        int red = ffModelColor.getRed();
        int green = ffModelColor.getGreen();
        int blue = ffModelColor.getBlue();
        Color.RGBtoHSB(red, green, blue, hsv);

        // Spinner models for JSpinners
        hueSPModel = new SpinnerNumberModel((int) (hsv[0] * 360), 0, 360, 1);
        saturationSPModel = new SpinnerNumberModel((int) (hsv[1] * 100), 0, 100, 1);
        valueSPModel = new SpinnerNumberModel((int) (hsv[2] * 100), 0, 100, 1);

        $$$setupUI$$$();

        // initialize sliders
        hueSlider.setValue((int) (hsv[0] * 360));
        saturationSlider.setValue((int) (hsv[1] * 100));
        valueSlider.setValue((int) (hsv[2] * 100));

        // initialize spinners
        hueSpinner.setValue(hueSlider.getValue());
        saturationSpinner.setValue(saturationSlider.getValue());
        valueSpinner.setValue(valueSlider.getValue());

        // initialize color code & preview panel
        colorCodeTF.setText(Integer.toHexString(ffModelColor.getRGB() & 0x00FFFFFF));
        colorPreviewPanel.setBackground(ffModelColor);

        // sliders' listeners
        hueSlider.addChangeListener(e -> {
            hsv[0] = (float) hueSlider.getValue() / 360;
            hueSpinner.setValue(hueSlider.getValue());
            colorCodeTF.setText(Integer.toHexString(Color.getHSBColor(hsv[0], hsv[1], hsv[2]).getRGB() & 0x00FFFFFF));
            colorPreviewPanel.setBackground(Color.getHSBColor(hsv[0], hsv[1], hsv[2]));
            repaint();
        });
        saturationSlider.addChangeListener(e -> {
            hsv[1] = (float) saturationSlider.getValue() / 100;
            saturationSpinner.setValue(saturationSlider.getValue());
            colorCodeTF.setText(Integer.toHexString(Color.getHSBColor(hsv[0], hsv[1], hsv[2]).getRGB() & 0x00FFFFFF));
            colorPreviewPanel.setBackground(Color.getHSBColor(hsv[0], hsv[1], hsv[2]));
            repaint();
        });
        valueSlider.addChangeListener(e -> {
            hsv[2] = (float) valueSlider.getValue() / 100;
            valueSpinner.setValue(valueSlider.getValue());
            colorCodeTF.setText(Integer.toHexString(Color.getHSBColor(hsv[0], hsv[1], hsv[2]).getRGB() & 0x00FFFFFF));
            colorPreviewPanel.setBackground(Color.getHSBColor(hsv[0], hsv[1], hsv[2]));
            repaint();
        });

        // spinners' listeners
        hueSpinner.addChangeListener(e -> {
            hsv[0] = (float) ((Integer) hueSpinner.getValue()) / 360F;
            hueSlider.setValue((Integer) hueSpinner.getValue());
            colorCodeTF.setText(Integer.toHexString(Color.getHSBColor(hsv[0], hsv[1], hsv[2]).getRGB() & 0x00FFFFFF));
            colorPreviewPanel.setBackground(Color.getHSBColor(hsv[0], hsv[1], hsv[2]));
            repaint();
        });
        saturationSpinner.addChangeListener(e -> {
            hsv[1] = (float) ((Integer) saturationSpinner.getValue()) / 100F;
            saturationSlider.setValue((Integer) saturationSpinner.getValue());
            colorCodeTF.setText(Integer.toHexString(Color.getHSBColor(hsv[0], hsv[1], hsv[2]).getRGB() & 0x00FFFFFF));
            colorPreviewPanel.setBackground(Color.getHSBColor(hsv[0], hsv[1], hsv[2]));
            repaint();
        });
        valueSpinner.addChangeListener(e -> {
            hsv[2] = (float) ((Integer) valueSpinner.getValue()) / 100F;
            valueSlider.setValue((Integer) valueSpinner.getValue());
            colorCodeTF.setText(Integer.toHexString(Color.getHSBColor(hsv[0], hsv[1], hsv[2]).getRGB() & 0x00FFFFFF));
            colorPreviewPanel.setBackground(Color.getHSBColor(hsv[0], hsv[1], hsv[2]));
            repaint();
        });

        // color code listener
        colorCodeTF.addActionListener(e -> {
            try {
                Color temp = new Color(Integer.valueOf(colorCodeTF.getText().substring(0, 2), 16),
                                Integer.valueOf(colorCodeTF.getText().substring(2, 4), 16),
                                Integer.valueOf(colorCodeTF.getText().substring(4, 6), 16));
                Color.RGBtoHSB(temp.getRed(), temp.getGreen(), temp.getBlue(), hsv);

                hueSlider.setValue((int) (hsv[0] * 360));
                saturationSlider.setValue((int) (hsv[1] * 100));
                valueSlider.setValue((int) (hsv[2] * 100));
            } catch (NumberFormatException ex) {
                throw new RuntimeException(ex);
            }
        });
        add(hsvPanel);
    }

    private void createUIComponents() {
        hueSlider = new JSlider(0, 360);
        saturationSlider = new JSlider(0, 100);
        valueSlider = new JSlider(0, 100);

        hueSlider.setUI(new customSliderUI(hueSlider, 0));
        saturationSlider.setUI(new customSliderUI(saturationSlider, 1));
        valueSlider.setUI(new customSliderUI(valueSlider, 2));

        hueSpinner = new JSpinner(hueSPModel);
        saturationSpinner = new JSpinner(saturationSPModel);
        valueSpinner = new JSpinner(valueSPModel);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        hsvPanel = new JPanel();
        hsvPanel.setLayout(new GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(hsvPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        hueSlider.setMaximum(360);
        hueSlider.setPaintLabels(false);
        hueSlider.setPaintTicks(false);
        hsvPanel.add(hueSlider, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saturationSlider.setPaintLabels(false);
        saturationSlider.setPaintTicks(false);
        hsvPanel.add(saturationSlider, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        valueSlider.setPaintLabels(false);
        valueSlider.setPaintTicks(false);
        hsvPanel.add(valueSlider, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        hsvPanel.add(hueSpinner, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        hsvPanel.add(saturationSpinner, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        hsvPanel.add(valueSpinner, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        colorCodeTF = new JTextField();
        hsvPanel.add(colorCodeTF, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Color Code");
        hsvPanel.add(label1, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        colorPreviewPanel = new JPanel();
        colorPreviewPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        hsvPanel.add(colorPreviewPanel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Hue");
        hsvPanel.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Saturation");
        hsvPanel.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Value");
        hsvPanel.add(label4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    class customSliderUI extends BasicSliderUI {
        private final int sliderState;
        private final float[] fracs = {0f, 1 / 6f, 2 / 6f, 3 / 6f, 4 / 6f, 5 / 6f, 1f};
        Color[] colors = {Color.red, Color.yellow, Color.green, Color.cyan,
                Color.blue, Color.magenta, Color.red};

        public customSliderUI(JSlider slider, int sliderState) {
            super(slider);
            this.sliderState = sliderState;
        }

        @Override
        public void paintFocus(Graphics g) {
        }

        @Override
        protected void calculateThumbSize() {
            super.calculateThumbSize();
        }

        @Override
        public void paintTrack(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            Rectangle t = trackRect;

            Point2D start = new Point2D.Float(t.x, t.y);
            Point2D end = new Point2D.Float(t.x + t.width, t.y);

            switch (sliderState) {
                case 0:
                    g2d.setPaint(new LinearGradientPaint(start, end, fracs, colors));
                    break;
                case 1:
                    g2d.setPaint(new GradientPaint(start, Color.WHITE, end, Color.getHSBColor(hsv[0], 1f, 1f)));
                    break;
                case 2:
                    g2d.setPaint(new GradientPaint(start, Color.BLACK, end, Color.getHSBColor(hsv[0], 1f, 1f)));
                    break;
                default:
                    break;
            }
            g2d.fillRect(t.x, t.y, t.width, t.height);
        }

        @Override
        protected void scrollDueToClickInTrack(int direction) {
            int value = slider.getValue();

            if (slider.getOrientation() == JSlider.HORIZONTAL) {
                value = this.valueForXPosition(slider.getMousePosition().x);
            } else if (slider.getOrientation() == JSlider.VERTICAL) {
                value = this.valueForYPosition(slider.getMousePosition().y);
            }
            slider.setValue(value);
        }
    }
}

