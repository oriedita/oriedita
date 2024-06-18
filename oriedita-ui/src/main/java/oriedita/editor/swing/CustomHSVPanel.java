package oriedita.editor.swing;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import oriedita.editor.FrameProvider;
import oriedita.editor.factory.RegexHighlightFactory;

import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
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
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

public class CustomHSVPanel extends AbstractColorChooserPanel {
    private JSlider hueSlider;
    private JSlider saturationSlider;
    private JSlider valueSlider;
    private JTextField colorCodeTF;
    private JSpinner hueSpinner;
    private JSpinner saturationSpinner;
    private JSpinner valueSpinner;
    private final float[] hsv = new float[3];

    public static Color showCustomColorDialog(FrameProvider frameProvider, String title, Color initialColor){
        JColorChooser colorChooser = new JColorChooser();
        colorChooser.addChooserPanel(new CustomHSVPanel());

        final boolean[] isOK = new boolean[1];

        if(initialColor != null){
            colorChooser.setColor(initialColor);
        }
        ActionListener okListener = e -> isOK[0] = true;

        ActionListener cancelListener = e -> isOK[0] = false;

        JDialog dialog = JColorChooser.createDialog(frameProvider.get(), title, true, colorChooser, okListener, cancelListener);
        dialog.setVisible(true);

        return isOK[0] ? colorChooser.getColor() : initialColor;
    }

    @Override
    public void updateChooser() {
        updateHSV();
        getColorSelectionModel().setSelectedColor(Color.getHSBColor(hsv[0], hsv[1], hsv[2]));
    }

    @Override
    protected void buildChooser() {
        updateHSV();

        JPanel hsvPanel = new JPanel();
        hsvPanel.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));

        // initialize sliders
        hueSlider = new JSlider(0, 360, Math.round(hsv[0] * 360));
        saturationSlider = new JSlider(0, 100, Math.round(hsv[1] * 100));
        valueSlider = new JSlider(0, 100, Math.round(hsv[2] * 100));

        hueSlider.setUI(new customSliderUI(hueSlider, 0));
        saturationSlider.setUI(new customSliderUI(saturationSlider, 1));
        valueSlider.setUI(new customSliderUI(valueSlider, 2));

        // Spinner models for JSpinners
        hueSpinner = new JSpinner(new SpinnerNumberModel(Math.round(hsv[0] * 360), 0, 360, 1));
        saturationSpinner = new JSpinner(new SpinnerNumberModel(Math.round(hsv[1] * 100), 0, 100, 1));
        valueSpinner = new JSpinner(new SpinnerNumberModel(Math.round(hsv[2] * 100), 0, 100, 1));

        // initialize spinners
        hueSpinner.setValue(hueSlider.getValue());
        saturationSpinner.setValue(saturationSlider.getValue());
        valueSpinner.setValue(valueSlider.getValue());

        // initialize color code
        colorCodeTF = new JTextField(getHexString());

        getColorSelectionModel().setSelectedColor(Color.getHSBColor(hsv[0], hsv[1], hsv[2]));

        // sliders' listeners
        hueSlider.addChangeListener(e -> {
            hsv[0] = hueSlider.getValue() / 360F;
            hueSpinner.setValue(hueSlider.getValue());
            colorCodeTF.setText(getHexString());
            getColorSelectionModel().setSelectedColor(Color.getHSBColor(hsv[0], hsv[1], hsv[2]));
            repaint();
        });
        saturationSlider.addChangeListener(e -> {
            hsv[1] = saturationSlider.getValue() / 100F;
            saturationSpinner.setValue(saturationSlider.getValue());
            colorCodeTF.setText(getHexString());
            getColorSelectionModel().setSelectedColor(Color.getHSBColor(hsv[0], hsv[1], hsv[2]));
            repaint();
        });
        valueSlider.addChangeListener(e -> {
            hsv[2] = valueSlider.getValue() / 100F;
            valueSpinner.setValue(valueSlider.getValue());
            colorCodeTF.setText(getHexString());
            getColorSelectionModel().setSelectedColor(Color.getHSBColor(hsv[0], hsv[1], hsv[2]));
            repaint();
        });

        // spinners' listeners
        hueSpinner.addChangeListener(e -> {
            hsv[0] = ((Integer) hueSpinner.getValue()) / 360F;
            hueSlider.setValue((Integer) hueSpinner.getValue());
            colorCodeTF.setText(getHexString());
            getColorSelectionModel().setSelectedColor(Color.getHSBColor(hsv[0], hsv[1], hsv[2]));
            repaint();
        });
        saturationSpinner.addChangeListener(e -> {
            hsv[1] = ((Integer) saturationSpinner.getValue()) / 100F;
            saturationSlider.setValue((Integer) saturationSpinner.getValue());
            colorCodeTF.setText(getHexString());
            getColorSelectionModel().setSelectedColor(Color.getHSBColor(hsv[0], hsv[1], hsv[2]));
            repaint();
        });
        valueSpinner.addChangeListener(e -> {
            hsv[2] = ((Integer) valueSpinner.getValue()) / 100F;
            valueSlider.setValue((Integer) valueSpinner.getValue());
            colorCodeTF.setText(getHexString());
            getColorSelectionModel().setSelectedColor(Color.getHSBColor(hsv[0], hsv[1], hsv[2]));
            repaint();
        });

        // color code listener
        colorCodeTF.addActionListener(e -> {
            try {
                colorCodeTF.setText(colorCodeTF.getText().toUpperCase());
                String colorHex = colorCodeTF.getText().toLowerCase();
                Color temp = new Color(Integer.valueOf(colorHex.substring(0, 2), 16),
                                Integer.valueOf(colorHex.substring(2, 4), 16),
                                Integer.valueOf(colorHex.substring(4, 6), 16));
                Color.RGBtoHSB(temp.getRed(), temp.getGreen(), temp.getBlue(), hsv);

                hueSlider.setValue(Math.round(hsv[0] * 360));
                saturationSlider.setValue(Math.round(hsv[1] * 100));
                valueSlider.setValue(Math.round(hsv[2] * 100));

                getColorSelectionModel().setSelectedColor(Color.getHSBColor(hsv[0], hsv[1], hsv[2]));
            } catch (NumberFormatException ex) {
                colorCodeTF.setText(getHexString());
            }
        });
        colorCodeTF.getDocument().addDocumentListener(RegexHighlightFactory.hexColorRegexAdapter(colorCodeTF));

        hsvPanel.add(hueSlider, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));
        hsvPanel.add(hueSpinner, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));
        hsvPanel.add(saturationSlider, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));
        hsvPanel.add(saturationSpinner, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));
        hsvPanel.add(valueSlider, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));
        hsvPanel.add(valueSpinner, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));
        hsvPanel.add(new JLabel("Color Code"), new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, 1, 1, null, null, null, 0, false));
        hsvPanel.add(colorCodeTF, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));

        add(hsvPanel);
    }

    @Override
    public String getDisplayName() { return "HSVCustom"; }

    @Override
    public Icon getSmallDisplayIcon() { return null; }

    @Override
    public Icon getLargeDisplayIcon() { return null; }

    private void updateHSV(){
        Color color = getColorFromModel();
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        Color.RGBtoHSB(red, green, blue, hsv);
    }

    private String getHexString(){
        return String.format("%06X", (Color.getHSBColor(hsv[0], hsv[1], hsv[2]).getRGB() & 0x00FFFFFF)).toUpperCase();
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
        public void paintTrack(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            Rectangle t = trackRect;

            Point2D start = new Point2D.Float(t.x, t.y);
            Point2D end = new Point2D.Float(t.x + t.width, t.y);

            Color hueColor = Color.getHSBColor(hsv[0], 1f, 1f);

            switch (sliderState) {
                case 0:
                    g2d.setPaint(new LinearGradientPaint(start, end, fracs, colors));
                    break;
                case 1:
                    g2d.setPaint(new GradientPaint(start, Color.WHITE, end, hueColor));
                    break;
                case 2:
                    g2d.setPaint(new GradientPaint(start, Color.BLACK, end, hueColor));
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

