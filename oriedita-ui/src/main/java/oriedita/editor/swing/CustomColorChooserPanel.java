package oriedita.editor.swing;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class CustomColorChooserPanel extends JPanel {
    private JPanel shapeSpectrumPanel;
    private JPanel hueSpectrumPanel;
    private JRadioButton hueRadioButton;
    private JRadioButton saturationRadioButton;
    private JRadioButton valueRadioButton;
    private JSlider hueSlider;
    private JSlider saturationSlider;
    private JSlider valueSlider;
    private JPanel hsvPanel;
    private JTextField colorCodeTF;
    private JSpinner hueSpinner;
    private JSpinner saturationSpinner;
    private JSpinner valueSpinner;
    private JSlider hueSpectrumSlider;
    private int cursorX;
    private int cursorY;
    private final int shadeSquareSize = 200;
    private int hsvState;
    private final float[] hsv = new float[3];
    private final SpinnerModel hueSPModel, saturationSPModel, valueSPModel;

    public CustomColorChooserPanel(Color ffModelColor) {

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

        hsvState = 0; // set mode
        hueRadioButton.setSelected(true); // set to HUE MODE

        // initialize plus cursor
        // TODO: not working for some reason
        cursorX = (int) (shapeSpectrumPanel.getWidth() * hsv[1]);
        cursorY = (int) (shapeSpectrumPanel.getHeight() * hsv[2]);
        repaint();

        hueSpectrumSlider.setValue((int) (hsv[0] * 360)); // initialize hue spectrum value (HUE MODE)

        // initialize sliders
        hueSlider.setValue((int) (hsv[0] * 360));
        saturationSlider.setValue((int) (hsv[1] * 100));
        valueSlider.setValue((int) (hsv[2] * 100));

        // initialize spinners
        hueSpinner.setValue(hueSlider.getValue());
        saturationSpinner.setValue(saturationSlider.getValue());
        valueSpinner.setValue(valueSlider.getValue());

        // shade spectrum's listeners
        shapeSpectrumPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                // position within the bounds of shapeSpectrumPanel
                cursorX = Math.max(0, Math.min(shapeSpectrumPanel.getWidth() - 1, e.getX()));
                cursorY = Math.max(0, Math.min(shapeSpectrumPanel.getHeight() - 1, e.getY()));
                repaint();
            }
        });
        shapeSpectrumPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // position within the bounds of shapeSpectrumPanel
                cursorX = Math.max(0, Math.min(shapeSpectrumPanel.getWidth() - 1, e.getX()));
                cursorY = Math.max(0, Math.min(shapeSpectrumPanel.getHeight() - 1, e.getY()));
                repaint();
            }
        });

        // hue spectrum's listener
        hueSpectrumSlider.addChangeListener(e -> {
            switch (hsvState) {
                case 0:
                    hsv[0] = (float) hueSpectrumSlider.getValue() / 360;
                    hueSlider.setValue(hueSpectrumSlider.getValue());
                    hueSpinner.setValue(hueSpectrumSlider.getValue());
                    break;
                case 1:
                    hsv[1] = (float) hueSpectrumSlider.getValue() / 360;
                    saturationSlider.setValue((int) (hsv[1] * 100));
                    saturationSpinner.setValue((int) (hsv[1] * 100));
                    break;
                case 2:
                    hsv[2] = (float) hueSpectrumSlider.getValue() / 360;
                    valueSlider.setValue((int) (hsv[2] * 100));
                    valueSpinner.setValue((int) (hsv[2] * 100));
                    break;
                default:
                    break;
            }
            repaint();
        });

        // radio buttons' listeners
        hueRadioButton.addActionListener(e -> {
            hsvState = 0;
            hueSpectrumSlider.setValue((int) (hsv[0] * 360));
            repaint();
        });
        saturationRadioButton.addActionListener(e -> {
            hsvState = 1;
            hueSpectrumSlider.setValue((int) (hsv[1] * 360));
            repaint();
        });
        valueRadioButton.addActionListener(e -> {
            hsvState = 2;
            hueSpectrumSlider.setValue((int) (hsv[2] * 360));
            repaint();
        });

        // sliders' listeners
        hueSlider.addChangeListener(e -> {
//            hsv[0] = (float) hueSlider.getValue() / 360;
            hueSpinner.setValue(hueSlider.getValue());

            /* TODO: increment issue
            *   hueSlider in HUE MODE is smooth with hueSpectrumSlider and vice versa
            *   saturationSlider in SATURATION MODE has the increment issue with hueSpectrumSlider and vice versa
            *   valueSlider doesn't affect the hueSpectrumSlider so ignore it (hueSpectrumSlider does smoothly affect valueSlider)
            *   */

            switch (hsvState) {
                case 0:
                    hueSpectrumSlider.setValue(hueSlider.getValue()); //TODO: This is fine
                    break;
                case 1:
                    break;
                case 2:
                    break;
                default:
                    break;
            }

            repaint();
        });
        saturationSlider.addChangeListener(e -> {
//            hsv[1] = (float) saturationSlider.getValue() / 100;
            saturationSpinner.setValue(saturationSlider.getValue());

            switch (hsvState) {
                case 0:
                    break;
                case 1:
                    hueSpectrumSlider.setValue((int) ((saturationSlider.getValue() / 100F) * 360)); //TODO: This has increment issue
                    break;
                case 2:
                    break;
                default:
                    break;
            }
            repaint();
        });
        valueSlider.addChangeListener(e -> {
//            hsv[2] = (float) valueSlider.getValue() / 100;
            valueSpinner.setValue(valueSlider.getValue());

            switch (hsvState) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    hueSpectrumSlider.setValue((int) (hsv[2] * 360)); //ignore this it's not doing anything
                    break;
                default:
                    break;
            }
            repaint();
        });

        // spinners' listeners
        hueSpinner.addChangeListener(e -> {
//            hsv[0] = (float) ((Integer) hueSpinner.getValue()) / 360F;
            shapeSpectrumPanel.repaint();
            hueSlider.setValue((Integer) hueSpinner.getValue());

            switch (hsvState) {
                case 0:
                    hueSpectrumSlider.setValue((int) (hueSpinner.getValue()));
                    break;
                case 1:
                    break;
                case 2:
                    break;
                default:
                    break;
            }
            repaint();
        });
        saturationSpinner.addChangeListener(e -> {
//            hsv[1] = (float) ((Integer) saturationSpinner.getValue()) / 100F;
            saturationSlider.setValue((Integer) saturationSpinner.getValue());

            switch (hsvState) {
                case 0:
                    break;
                case 1:
                    hueSpectrumSlider.setValue((int) (hsv[1] * 360));
                    break;
                case 2:
                    break;
                default:
                    break;
            }
            repaint();
        });
        valueSpinner.addChangeListener(e -> {
//            hsv[2] = (float) ((Integer) valueSpinner.getValue()) / 100F;
            valueSlider.setValue((Integer) valueSpinner.getValue());

            switch (hsvState) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    hueSpectrumSlider.setValue((int) (hsv[2] * 360));
                    break;
                default:
                    break;
            }
            repaint();
        });

        // add panels
        add(shapeSpectrumPanel);
        add(hueSpectrumPanel);
        add(hsvPanel);
    }

    private void createUIComponents() {
        shapeSpectrumPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                BufferedImage img = new BufferedImage(shadeSquareSize, shadeSquareSize, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = img.createGraphics();

                // for smoother shit
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

                // panel corners
                int x1 = 0, y1 = 0;
                int x2 = getWidth(), y2 = getHeight();

                // linear gradient rainbow variables
                Point2D start = new Point2D.Float(x1, y1);
                Point2D end = new Point2D.Float(x1 + x2, y1);
                Color[] colors = {Color.red, Color.yellow, Color.green, Color.cyan,
                        Color.blue, Color.magenta, Color.red};
                float[] fracs = new float[]{0f, 1 / 6f, 2 / 6f, 3 / 6f, 4 / 6f, 5 / 6f, 1f};

                switch (hsvState) {
                    case 0:
                        // white linear gradient
                        g2d.setPaint(new GradientPaint(x1, y1, Color.WHITE, x2, y1, Color.getHSBColor(hsv[0], 1f, 1f)));
                        g2d.fillRect(x1, y1, x2, y2);

                        // black linear gradient
                        g2d.setPaint(new GradientPaint(x1, y1, new Color(0, 0, 0, 0), x1, y2, Color.BLACK));
                        g2d.fillRect(x1, y1, x2, y2);

                        // plus cursor
                        g2d.setColor(getComplementaryColor(img.getRGB(cursorX, cursorY))); // Set circle color
                        g2d.drawLine(cursorX - 10, cursorY, cursorX + 10, cursorY);
                        g2d.drawLine(cursorX, cursorY - 10, cursorX, cursorY + 10);
                        break;
                    case 1:
                        // linear color gradient
                        g2d.setPaint(new LinearGradientPaint(start, end, fracs, colors));
                        g2d.fillRect(x1, y1, x2, y2);

                        // white fill
                        g2d.setPaint(new Color(1, 1, 1, (1F - hsv[1])));
                        g2d.fillRect(x1, y1, x2, y2);

                        // black gradient
                        g2d.setPaint(new GradientPaint(0, 0, new Color(0, 0, 0, 0), 0, getHeight(), Color.BLACK));
                        g2d.fillRect(x1, y1, x2, y2);
                        break;
                    case 2:
                        // linear color gradient
                        g2d.setPaint(new LinearGradientPaint(start, end, fracs, colors));
                        g2d.fillRect(x1, y1, x2, y2);

                        // white gradient
                        g2d.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 0), 0, getHeight(), Color.WHITE));
                        g2d.fillRect(x1, y1, x2, y2);

                        // black fill
                        g2d.setPaint(new Color(0, 0, 0, (1F - hsv[2])));
                        g2d.fillRect(x1, y1, x2, y2);
                        break;
                    default:
                        break;
                }

                g.drawImage(img, 0, 0, this);
            }
        };
        shapeSpectrumPanel.setPreferredSize(new Dimension(shadeSquareSize, shadeSquareSize));

        hueSpectrumSlider = new JSlider(0, 360);
        hueSpectrumSlider.setUI(new customSliderUI(hueSpectrumSlider));

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
        panel1.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(shapeSpectrumPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 3, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        hueSpectrumPanel = new JPanel();
        hueSpectrumPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(hueSpectrumPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        hueSpectrumSlider.setMaximum(360);
        hueSpectrumSlider.setOrientation(1);
        hueSpectrumPanel.add(hueSpectrumSlider, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        hsvPanel = new JPanel();
        hsvPanel.setLayout(new GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(hsvPanel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        hueRadioButton = new JRadioButton();
        hueRadioButton.setText("Hue");
        hsvPanel.add(hueRadioButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saturationRadioButton = new JRadioButton();
        saturationRadioButton.setText("Saturation");
        hsvPanel.add(saturationRadioButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        valueRadioButton = new JRadioButton();
        valueRadioButton.setText("Value");
        hsvPanel.add(valueRadioButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        hueSlider = new JSlider();
        hueSlider.setMaximum(360);
        hueSlider.setPaintLabels(false);
        hueSlider.setPaintTicks(false);
        hsvPanel.add(hueSlider, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saturationSlider = new JSlider();
        saturationSlider.setPaintLabels(false);
        saturationSlider.setPaintTicks(false);
        hsvPanel.add(saturationSlider, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        valueSlider = new JSlider();
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
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        hsvPanel.add(panel2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(hueRadioButton);
        buttonGroup.add(saturationRadioButton);
        buttonGroup.add(valueRadioButton);
    }

    private Color getComplementaryColor(int rgb) {
        // Extracting individual RGB components
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        // Calculating complementary color by subtracting each component from 255
        int complementaryRed = 255 - red;
        int complementaryGreen = 255 - green;
        int complementaryBlue = 255 - blue;

        // Combining the components to get the final RGB value
        int complementaryColor = (complementaryRed << 16) | (complementaryGreen << 8) | complementaryBlue;

        return new Color(complementaryColor);
    }

    class customSliderUI extends BasicSliderUI {
        private final float[] fracs = {0f, 1 / 6f, 2 / 6f, 3 / 6f, 4 / 6f, 5 / 6f, 1f};
        Color[] colors = {Color.red, Color.magenta, Color.blue, Color.cyan,
                Color.green, Color.yellow, Color.red};

        public customSliderUI(JSlider slider) {
            super(slider);
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
            Point2D end = new Point2D.Float(t.x, t.y + t.height);

            switch (hsvState) {
                case 0:
                    g2d.setPaint(new LinearGradientPaint(start, end, fracs, colors));
                    g2d.fillRect(t.x, t.y - thumbRect.height / 2, t.width, t.height + thumbRect.height);
                    break;
                case 1:
                    g2d.setPaint(new GradientPaint(start, Color.getHSBColor(hsv[0], 1f, 1f), end, Color.WHITE));
                    g2d.fillRect(t.x, t.y - thumbRect.height / 2, t.width, t.height + thumbRect.height);
                    break;
                case 2:
                    g2d.setPaint(new GradientPaint(start, Color.getHSBColor(hsv[0], 1f, 1f), end, Color.BLACK));
                    g2d.fillRect(t.x, t.y - thumbRect.height / 2, t.width, t.height + thumbRect.height);
                    break;
                default:
                    break;
            }
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

        @Override
        public void paintThumb(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle t = thumbRect;
            g2d.setColor(Color.black);
            g2d.fillRect(t.x, t.y, t.width, t.height);
        }
    }
}

