package oriedita.editor.swing;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

public class CustomColorChooserPanel extends AbstractColorChooserPanel {
    private JSlider hueSlider;
    private JSlider saturationSlider;
    private JSlider valueSlider;
    private JTextField colorCodeTF;
    private JSpinner hueSpinner;
    private JSpinner saturationSpinner;
    private JSpinner valueSpinner;
    private final float[] hsv = new float[3];
    private Color color, currentColor;

    @Override
    public void updateChooser() {
        updateHSV();

        hueSlider.setValue((int) (hsv[0] * 360));
        saturationSlider.setValue((int) (hsv[1] * 100));
        valueSlider.setValue((int) (hsv[2] * 100));

        hueSpinner.setValue(hueSlider.getValue());
        saturationSpinner.setValue(saturationSlider.getValue());
        valueSpinner.setValue(valueSlider.getValue());

        colorCodeTF.setText(Integer.toHexString(color.getRGB() & 0x00FFFFFF));
    }

    @Override
    protected void buildChooser() {
        updateHSV();

        JPanel hsvPanel = new JPanel();
        hsvPanel.setLayout(new GridLayoutManager(4, 2, new Insets(10, 10, 0, 10), -1, -1));

        hueSlider = new JSlider(0, 360);
        saturationSlider = new JSlider(0, 100);
        valueSlider = new JSlider(0, 100);

        hueSlider.setUI(new customSliderUI(hueSlider, 0));
        saturationSlider.setUI(new customSliderUI(saturationSlider, 1));
        valueSlider.setUI(new customSliderUI(valueSlider, 2));

        colorCodeTF = new JTextField(Integer.toHexString(Color.getHSBColor(hsv[0], hsv[1], hsv[2]).getRGB() & 0x00FFFFFF));

        getColorSelectionModel().setSelectedColor(Color.getHSBColor(hsv[0], hsv[1], hsv[2]));

        // Spinner models for JSpinners
        SpinnerModel hueSPModel = new SpinnerNumberModel((int) (hsv[0] * 360), 0, 360, 1);
        SpinnerModel saturationSPModel = new SpinnerNumberModel((int) (hsv[1] * 100), 0, 100, 1);
        SpinnerModel valueSPModel = new SpinnerNumberModel((int) (hsv[2] * 100), 0, 100, 1);

        hueSpinner = new JSpinner(hueSPModel);
        saturationSpinner = new JSpinner(saturationSPModel);
        valueSpinner = new JSpinner(valueSPModel);

        // initialize sliders
        hueSlider.setValue((int) (hsv[0] * 360));
        saturationSlider.setValue((int) (hsv[1] * 100));
        valueSlider.setValue((int) (hsv[2] * 100));

        // initialize spinners
        hueSpinner.setValue(hueSlider.getValue());
        saturationSpinner.setValue(saturationSlider.getValue());
        valueSpinner.setValue(valueSlider.getValue());

        // initialize color code & preview panel
        colorCodeTF.setText(Integer.toHexString(color.getRGB() & 0x00FFFFFF));

        // sliders' listeners
        hueSlider.addChangeListener(e -> {
            hsv[0] = (float) hueSlider.getValue() / 360;
            hueSpinner.setValue((int) (hsv[0] * 360));

            currentColor = Color.getHSBColor(hsv[0], hsv[1], hsv[2]);
            colorCodeTF.setText(Integer.toHexString(currentColor.getRGB() & 0x00FFFFFF));
            getColorSelectionModel().setSelectedColor(currentColor);
            repaint();
        });
        saturationSlider.addChangeListener(e -> {
            hsv[1] = (float) saturationSlider.getValue() / 100;
            saturationSpinner.setValue((int) (hsv[1] * 100));

            currentColor = Color.getHSBColor(hsv[0], hsv[1], hsv[2]);
            colorCodeTF.setText(Integer.toHexString(currentColor.getRGB() & 0x00FFFFFF));
            getColorSelectionModel().setSelectedColor(currentColor);
            repaint();
        });
        valueSlider.addChangeListener(e -> {
            hsv[2] = (float) valueSlider.getValue() / 100;
            valueSpinner.setValue((int) (hsv[2] * 100));

            currentColor = Color.getHSBColor(hsv[0], hsv[1], hsv[2]);
            colorCodeTF.setText(Integer.toHexString(currentColor.getRGB() & 0x00FFFFFF));
            getColorSelectionModel().setSelectedColor(currentColor);
            repaint();
        });

        // spinners' listeners
        hueSpinner.addChangeListener(e -> {
            hsv[0] = (float) ((Integer) hueSpinner.getValue()) / 360;
            hueSlider.setValue((int) (hsv[0] * 360));

            currentColor = Color.getHSBColor(hsv[0], hsv[1], hsv[2]);
            colorCodeTF.setText(Integer.toHexString(currentColor.getRGB() & 0x00FFFFFF));
            getColorSelectionModel().setSelectedColor(currentColor);
            repaint();
        });
        saturationSpinner.addChangeListener(e -> {
            hsv[1] = (float) ((Integer) saturationSpinner.getValue()) / 100;
            saturationSlider.setValue((int) (hsv[1] * 100));

            currentColor = Color.getHSBColor(hsv[0], hsv[1], hsv[2]);
            colorCodeTF.setText(Integer.toHexString(currentColor.getRGB() & 0x00FFFFFF));
            getColorSelectionModel().setSelectedColor(currentColor);
            repaint();
        });
        valueSpinner.addChangeListener(e -> {
            hsv[2] = (float) ((Integer) valueSpinner.getValue()) / 100;
            valueSlider.setValue((int) (hsv[2] * 100));

            currentColor = Color.getHSBColor(hsv[0], hsv[1], hsv[2]);
            colorCodeTF.setText(Integer.toHexString(currentColor.getRGB() & 0x00FFFFFF));
            getColorSelectionModel().setSelectedColor(currentColor);
            repaint();
        });

        // color code listener
        colorCodeTF.addActionListener(e -> {
            try {
                String colorHex = colorCodeTF.getText();
                Color temp = new Color(Integer.valueOf(colorHex.substring(0, 2), 16),
                                Integer.valueOf(colorHex.substring(2, 4), 16),
                                Integer.valueOf(colorHex.substring(4, 6), 16));
                Color.RGBtoHSB(temp.getRed(), temp.getGreen(), temp.getBlue(), hsv);

                hueSlider.setValue((int) (hsv[0] * 360));
                saturationSlider.setValue((int) (hsv[1] * 100));
                valueSlider.setValue((int) (hsv[2] * 100));

                currentColor = Color.getHSBColor(hsv[0], hsv[1], hsv[2]);
                getColorSelectionModel().setSelectedColor(currentColor);
            } catch (NumberFormatException ex) {
                throw new RuntimeException(ex);
            }
        });

        hsvPanel.add(hueSlider, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));
        hsvPanel.add(hueSpinner, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));
        hsvPanel.add(saturationSlider, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));
        hsvPanel.add(saturationSpinner, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));
        hsvPanel.add(valueSlider, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));
        hsvPanel.add(valueSpinner, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));
        hsvPanel.add(colorCodeTF, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));

        add(hsvPanel);
    }

    @Override
    public String getDisplayName() {
        return "Custom";
    }

    @Override
    public Icon getSmallDisplayIcon() {
        return null;
    }

    @Override
    public Icon getLargeDisplayIcon() {
        return null;
    }

    private void updateHSV(){
        color = getColorFromModel();
        currentColor = getColorFromModel();
        // get array of hsv values
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        Color.RGBtoHSB(red, green, blue, hsv);
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
        public void paintFocus(Graphics g) {}

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

