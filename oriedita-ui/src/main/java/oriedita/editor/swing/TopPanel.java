package oriedita.editor.swing;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.AnimationDurations;
import oriedita.editor.Animations;
import oriedita.editor.Canvas;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.BackgroundModel;
import oriedita.editor.databinding.CameraModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.InternalDivisionRatioModel;
import oriedita.editor.databinding.MeasuresModel;
import oriedita.editor.factory.RegexHighlightFactory;
import oriedita.editor.service.AnimationService;
import oriedita.editor.service.ButtonService;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

@ApplicationScoped
public class TopPanel implements PropertyChangeListener {
    private final MeasuresModel measuresModel;
    private final ButtonService buttonService;
    @SuppressWarnings("unused")
    private final CanvasModel canvasModel;
    private final InternalDivisionRatioModel internalDivisionRatioModel;
    @SuppressWarnings("unused")
    private final CreasePattern_Worker mainCreasePatternWorker;
    private final CameraModel creasePatternCameraModel;
    @SuppressWarnings("unused")
    private final Canvas canvas;
    private final ApplicationModel applicationModel;
    private JButton operationFrameSelectButton;
    private JPanel root;
    private JTextField ratioATextField;
    private JButton moveCreasePatternButton;
    private JButton creasePatternZoomOutButton;
    private JButton creasePatternZoomInButton;
    private JButton rotateAnticlockwiseButton;
    private JButton rotateClockwiseButton;
    private JButton senbun_yoke_henkanButton;
    private JButton lineSegmentInternalDivisionRatioSetButton;
    private JButton drawLineSegmentInternalDivisionRatioButton;
    private JTextField ratioBTextField;
    private JTextField ratioCTextField;
    private JTextField ratioDTextField;
    private JTextField ratioETextField;
    private JTextField ratioFTextField;
    private JTextField scaleFactorTextField;
    private JButton scaleFactorSetButton;
    private JTextField rotationTextField;
    private JButton rotationSetButton;
    private JButton transparentButton;
    private JButton backgroundTrimButton;
    private JButton readBackgroundButton;
    private JCheckBox backgroundToggleButton;
    private JButton backgroundSetPositionButton;
    private JCheckBox backgroundLockButton;
    private JCheckBox mouseSettingsCheckBox;
    private JLabel ratioLabel1;
    private JLabel ratioLabel2;
    private JLabel ratioLabel3;
    private JLabel ratioLabel4;
    private JLabel ratioLabel5;
    private JScrollPane scrollPane1;

    private final AnimationService animationService;

    @Inject
    public TopPanel(MeasuresModel measuresModel,
                    ButtonService buttonService,
                    @Any CanvasModel canvasModel,
                    InternalDivisionRatioModel internalDivisionRatioModel,
                    @Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker,
                    CameraModel creasePatternCameraModel,
                    Canvas canvas,
                    ApplicationModel applicationModel,
                    AnimationService animationService) {
        this.measuresModel = measuresModel;
        this.buttonService = buttonService;
        this.canvasModel = canvasModel;
        this.internalDivisionRatioModel = internalDivisionRatioModel;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.creasePatternCameraModel = creasePatternCameraModel;
        this.canvas = canvas;
        this.applicationModel = applicationModel;
        this.animationService = animationService;
    }

    public void init() {
        buttonService.addDefaultListener($$$getRootComponent$$$());

        buttonService.registerButton(creasePatternZoomInButton, "creasePatternZoomInAction");
        buttonService.registerButton(creasePatternZoomOutButton, "creasePatternZoomOutAction");
        buttonService.registerButton(rotateAnticlockwiseButton, "rotateAnticlockwiseAction");
        buttonService.registerButton(rotateClockwiseButton, "rotateClockwiseAction");
        buttonService.registerButton(senbun_yoke_henkanButton, "senbun_yoke_henkanAction");
        buttonService.registerButton(lineSegmentInternalDivisionRatioSetButton, "lineSegmentInternalDivisionRatioSetAction");
        buttonService.registerButton(scaleFactorSetButton, "scaleFactorSetAction");
        buttonService.registerButton(rotationSetButton, "rotationSetAction");
        buttonService.registerButton(transparentButton, "transparentAction");
        buttonService.registerButton(backgroundTrimButton, "backgroundTrimAction");
        buttonService.registerButton(readBackgroundButton, "readBackgroundAction");
        buttonService.registerButton(backgroundToggleButton, "backgroundToggleAction");
        buttonService.registerButton(backgroundSetPositionButton, "backgroundSetPositionAction");
        buttonService.registerButton(backgroundLockButton, "backgroundLockAction");
        buttonService.registerButton(mouseSettingsCheckBox, "mouseSettingsAction");

        buttonService.setIcon(ratioLabel1, "labelPlus");
        buttonService.setIcon(ratioLabel2, "labelSqrt");
        buttonService.setIcon(ratioLabel3, "labelColon");
        buttonService.setIcon(ratioLabel4, "labelPlus");
        buttonService.setIcon(ratioLabel5, "labelSqrt");

        internalDivisionRatioModel.bind(ratioATextField, "displayInternalDivisionRatioA");
        internalDivisionRatioModel.bind(ratioBTextField, "displayInternalDivisionRatioB");
        internalDivisionRatioModel.bind(ratioCTextField, "displayInternalDivisionRatioC");
        internalDivisionRatioModel.bind(ratioDTextField, "displayInternalDivisionRatioD");
        internalDivisionRatioModel.bind(ratioETextField, "displayInternalDivisionRatioE");
        internalDivisionRatioModel.bind(ratioFTextField, "displayInternalDivisionRatioF");

        mouseSettingsCheckBox.addActionListener(e -> applicationModel.setMouseWheelMovesCreasePattern(mouseSettingsCheckBox.isSelected()));

        ratioATextField.getDocument().addDocumentListener(RegexHighlightFactory.doubleRegexAdapter(ratioATextField));
        ratioATextField.addKeyListener(new InputEnterKeyAdapter(ratioATextField));
        ratioATextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                internalDivisionRatioModel.setInternalDivisionRatioA(measuresModel.string2double(internalDivisionRatioModel.getDisplayInternalDivisionRatioA(), internalDivisionRatioModel.getInternalDivisionRatioA()));
            }
        });
        ratioBTextField.getDocument().addDocumentListener(RegexHighlightFactory.doubleRegexAdapter(ratioBTextField));
        ratioBTextField.addKeyListener(new InputEnterKeyAdapter(ratioBTextField));
        ratioBTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                internalDivisionRatioModel.setInternalDivisionRatioB(measuresModel.string2double(internalDivisionRatioModel.getDisplayInternalDivisionRatioB(), internalDivisionRatioModel.getInternalDivisionRatioB()));
            }
        });
        ratioCTextField.getDocument().addDocumentListener(RegexHighlightFactory.doubleRegexAdapter(ratioCTextField));
        ratioCTextField.addKeyListener(new InputEnterKeyAdapter(ratioCTextField));
        ratioCTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                internalDivisionRatioModel.setInternalDivisionRatioC(measuresModel.string2double(internalDivisionRatioModel.getDisplayInternalDivisionRatioC(), internalDivisionRatioModel.getInternalDivisionRatioC()));
            }
        });
        ratioDTextField.getDocument().addDocumentListener(RegexHighlightFactory.doubleRegexAdapter(ratioDTextField));
        ratioDTextField.addKeyListener(new InputEnterKeyAdapter(ratioDTextField));
        ratioDTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                internalDivisionRatioModel.setInternalDivisionRatioD(measuresModel.string2double(internalDivisionRatioModel.getDisplayInternalDivisionRatioD(), internalDivisionRatioModel.getInternalDivisionRatioD()));
            }
        });
        ratioETextField.getDocument().addDocumentListener(RegexHighlightFactory.doubleRegexAdapter(ratioETextField));
        ratioETextField.addKeyListener(new InputEnterKeyAdapter(ratioETextField));
        ratioETextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                internalDivisionRatioModel.setInternalDivisionRatioE(measuresModel.string2double(internalDivisionRatioModel.getDisplayInternalDivisionRatioE(), internalDivisionRatioModel.getInternalDivisionRatioE()));
            }
        });
        ratioFTextField.getDocument().addDocumentListener(RegexHighlightFactory.doubleRegexAdapter(ratioFTextField));
        ratioFTextField.addKeyListener(new InputEnterKeyAdapter(ratioFTextField));
        ratioFTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                internalDivisionRatioModel.setInternalDivisionRatioF(measuresModel.string2double(internalDivisionRatioModel.getDisplayInternalDivisionRatioF(), internalDivisionRatioModel.getInternalDivisionRatioF()));
            }
        });

        scaleFactorSetButton.addActionListener(e -> {
            double d_syukusyaku_keisuu_old = creasePatternCameraModel.getScale();
            double x = measuresModel.string2double(scaleFactorTextField.getText(), d_syukusyaku_keisuu_old);
            animationService.animate(Animations.ZOOM_CP,
                    creasePatternCameraModel::setScale, creasePatternCameraModel::getScale, s -> x,
                    AnimationDurations.ZOOM);
        });
        scaleFactorTextField.addActionListener(e -> scaleFactorSetButton.doClick());
        scaleFactorTextField.getDocument().addDocumentListener(RegexHighlightFactory.doubleRegexAdapter(scaleFactorTextField));
        scaleFactorTextField.addKeyListener(new InputEnterKeyAdapter(scaleFactorTextField));
        scaleFactorTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                double d_syukusyaku_keisuu_old = creasePatternCameraModel.getScale();
                double x = measuresModel.string2double(scaleFactorTextField.getText(), d_syukusyaku_keisuu_old);
                animationService.animate(Animations.ZOOM_CP,
                        creasePatternCameraModel::setScale, creasePatternCameraModel::getScale, s -> x,
                        AnimationDurations.ZOOM);
            }
        });
        rotationSetButton.addActionListener(e -> creasePatternCameraModel.setRotation(measuresModel.string2double(rotationTextField.getText(), creasePatternCameraModel.getRotation())));
        rotationTextField.addActionListener(e -> rotationSetButton.doClick());
        rotationTextField.getDocument().addDocumentListener(RegexHighlightFactory.doubleRegexAdapter(rotationTextField));
        rotationTextField.addKeyListener(new InputEnterKeyAdapter(rotationTextField));
        rotationTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                creasePatternCameraModel.setRotation(measuresModel.string2double(rotationTextField.getText(), creasePatternCameraModel.getRotation()));
            }
        });
    }

    public void setData(ApplicationModel applicationModel) {
        mouseSettingsCheckBox.setSelected(applicationModel.getMouseWheelMovesCreasePattern());
    }

    public void setData(PropertyChangeEvent e, CanvasModel canvasModel) {
        if (e.getPropertyName() == null || e.getPropertyName().equals("mouseMode")) {
            MouseMode m = canvasModel.getMouseMode();

            backgroundSetPositionButton.setSelected(m == MouseMode.BACKGROUND_CHANGE_POSITION_26);
            senbun_yoke_henkanButton.setSelected(m == MouseMode.CREASE_ADVANCE_TYPE_30);
            moveCreasePatternButton.setSelected(m == MouseMode.MOVE_CREASE_PATTERN_2);
            drawLineSegmentInternalDivisionRatioButton.setSelected(m == MouseMode.LINE_SEGMENT_RATIO_SET_28);
            operationFrameSelectButton.setSelected(m == MouseMode.OPERATION_FRAME_CREATE_61);
        }
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
        createUIComponents();
        root = new JPanel();
        root.setLayout(new GridLayoutManager(1, 2, new Insets(1, 1, 1, 1), 1, 1));
        scrollPane1.setVerticalScrollBarPolicy(21);
        root.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 10, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane1.setViewportView(panel1);
        operationFrameSelectButton = new JButton();
        operationFrameSelectButton.setActionCommand("operationFrameSelectAction");
        operationFrameSelectButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tyouhoukei_select.png")));
        panel1.add(operationFrameSelectButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        mouseSettingsCheckBox = new JCheckBox();
        mouseSettingsCheckBox.setIcon(new ImageIcon(getClass().getResource("/ppp/ckbox_mouse_settei_off.png")));
        mouseSettingsCheckBox.setSelected(false);
        mouseSettingsCheckBox.setSelectedIcon(new ImageIcon(getClass().getResource("/ppp/ckbox_mouse_settei_on.png")));
        panel1.add(mouseSettingsCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), 1, 1));
        panel1.add(panel2, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), 1, 1));
        panel2.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ratioATextField = new JTextField();
        ratioATextField.setColumns(2);
        ratioATextField.setMinimumSize(new Dimension(60, 30));
        ratioATextField.setText("1.0");
        panel3.add(ratioATextField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        ratioLabel1 = new JLabel();
        ratioLabel1.setIcon(new ImageIcon(getClass().getResource("/ppp/plus.png")));
        ratioLabel1.setText("");
        panel3.add(ratioLabel1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ratioBTextField = new JTextField();
        ratioBTextField.setColumns(2);
        ratioBTextField.setMinimumSize(new Dimension(60, 30));
        ratioBTextField.setText("0.0");
        panel3.add(ratioBTextField, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        ratioLabel2 = new JLabel();
        ratioLabel2.setIcon(new ImageIcon(getClass().getResource("/ppp/root.png")));
        panel3.add(ratioLabel2, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ratioCTextField = new JTextField();
        ratioCTextField.setColumns(2);
        ratioCTextField.setMinimumSize(new Dimension(60, 30));
        ratioCTextField.setText("0.0");
        panel3.add(ratioCTextField, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        ratioLabel3 = new JLabel();
        ratioLabel3.setIcon(new ImageIcon(getClass().getResource("/ppp/tenten.png")));
        panel2.add(ratioLabel3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), 1, 1));
        panel2.add(panel4, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ratioDTextField = new JTextField();
        ratioDTextField.setColumns(2);
        ratioDTextField.setMinimumSize(new Dimension(60, 30));
        ratioDTextField.setText("0.0");
        panel4.add(ratioDTextField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        ratioLabel4 = new JLabel();
        ratioLabel4.setIcon(new ImageIcon(getClass().getResource("/ppp/plus.png")));
        panel4.add(ratioLabel4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ratioETextField = new JTextField();
        ratioETextField.setColumns(2);
        ratioETextField.setMinimumSize(new Dimension(60, 30));
        ratioETextField.setText("1.0");
        panel4.add(ratioETextField, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        ratioLabel5 = new JLabel();
        ratioLabel5.setIcon(new ImageIcon(getClass().getResource("/ppp/root.png")));
        panel4.add(ratioLabel5, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ratioFTextField = new JTextField();
        ratioFTextField.setColumns(2);
        ratioFTextField.setMinimumSize(new Dimension(60, 30));
        ratioFTextField.setText("2.0");
        panel4.add(ratioFTextField, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        lineSegmentInternalDivisionRatioSetButton = new JButton();
        lineSegmentInternalDivisionRatioSetButton.setActionCommand("lineSegmentInternalDivisionRatioSetAction");
        lineSegmentInternalDivisionRatioSetButton.setText("Set");
        panel2.add(lineSegmentInternalDivisionRatioSetButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        drawLineSegmentInternalDivisionRatioButton = new JButton();
        drawLineSegmentInternalDivisionRatioButton.setActionCommand("drawLineSegmentInternalDivisionRatioAction");
        drawLineSegmentInternalDivisionRatioButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_n_nyuryoku.png")));
        panel2.add(drawLineSegmentInternalDivisionRatioButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        moveCreasePatternButton = new JButton();
        moveCreasePatternButton.setActionCommand("moveCreasePatternAction");
        moveCreasePatternButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_idiu.png")));
        panel1.add(moveCreasePatternButton, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), 1, 1));
        panel1.add(panel5, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 1, false));
        creasePatternZoomOutButton = new JButton();
        creasePatternZoomOutButton.setActionCommand("creasePatternZoomOutAction");
        creasePatternZoomOutButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_syukusyou.png")));
        panel5.add(creasePatternZoomOutButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scaleFactorTextField = new JTextField();
        scaleFactorTextField.setColumns(2);
        scaleFactorTextField.setText("1.0");
        panel5.add(scaleFactorTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        scaleFactorSetButton = new JButton();
        scaleFactorSetButton.setActionCommand("scaleFactorSetAction");
        scaleFactorSetButton.setText("S");
        panel5.add(scaleFactorSetButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        creasePatternZoomInButton = new JButton();
        creasePatternZoomInButton.setActionCommand("creasePatternZoomInAction");
        creasePatternZoomInButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_kakudai.png")));
        panel5.add(creasePatternZoomInButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), 1, 1));
        panel1.add(panel6, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 1, false));
        rotateAnticlockwiseButton = new JButton();
        rotateAnticlockwiseButton.setActionCommand("rotateAnticlockwiseAction");
        rotateAnticlockwiseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_p_kaiten.png")));
        panel6.add(rotateAnticlockwiseButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        rotationTextField = new JTextField();
        rotationTextField.setColumns(2);
        rotationTextField.setEnabled(true);
        panel6.add(rotationTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        rotationSetButton = new JButton();
        rotationSetButton.setActionCommand("rotationSetAction");
        rotationSetButton.setText("S");
        panel6.add(rotationSetButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        rotateClockwiseButton = new JButton();
        rotateClockwiseButton.setActionCommand("rotateClockwiseAction");
        rotateClockwiseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_m_kaiten.png")));
        panel6.add(rotateClockwiseButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 7, new Insets(0, 0, 0, 0), 1, 1));
        panel1.add(panel7, new GridConstraints(0, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        readBackgroundButton = new JButton();
        readBackgroundButton.setActionCommand("readBackgroundAction");
        readBackgroundButton.setText("Select");
        panel7.add(readBackgroundButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        backgroundToggleButton = new JCheckBox();
        backgroundToggleButton.setActionCommand("backgroundToggleAction");
        backgroundToggleButton.setEnabled(false);
        backgroundToggleButton.setText("Show");
        panel7.add(backgroundToggleButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        backgroundLockButton = new JCheckBox();
        backgroundLockButton.setActionCommand("backgroundLockAction");
        backgroundLockButton.setEnabled(false);
        backgroundLockButton.setText("Lock");
        panel7.add(backgroundLockButton, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(46, 21), null, 0, false));
        senbun_yoke_henkanButton = new JButton();
        senbun_yoke_henkanButton.setActionCommand("senbun_yoke_henkanAction");
        senbun_yoke_henkanButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_yoke_henkan.png")));
        panel7.add(senbun_yoke_henkanButton, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        backgroundTrimButton = new JButton();
        backgroundTrimButton.setActionCommand("backgroundTrimAction");
        backgroundTrimButton.setEnabled(false);
        backgroundTrimButton.setText("Trim");
        panel7.add(backgroundTrimButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        transparentButton = new JButton();
        transparentButton.setActionCommand("transparentAction");
        transparentButton.setText("Transparent");
        panel7.add(transparentButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        backgroundSetPositionButton = new JButton();
        backgroundSetPositionButton.setActionCommand("backgroundSetPositionAction");
        backgroundSetPositionButton.setText("S");
        panel7.add(backgroundSetPositionButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(5, -1), null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(5, -1), null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel1.add(spacer3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(5, -1), null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        root.add(spacer4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

    public void setData(BackgroundModel backgroundModel) {
        backgroundToggleButton.setEnabled(backgroundModel.getBackgroundImage() != null);
        backgroundLockButton.setEnabled(backgroundModel.getBackgroundImage() != null);

        backgroundTrimButton.setEnabled(backgroundModel.getBackgroundImage() != null);

        backgroundToggleButton.setSelected(backgroundModel.isDisplayBackground());
        backgroundLockButton.setSelected(backgroundModel.isLockBackground());
    }

    public void setData(CameraModel creasePatternCameraModel) {
        rotationTextField.setText(String.valueOf(creasePatternCameraModel.getRotation()));
        rotationTextField.setCaretPosition(0);
        scaleFactorTextField.setText(String.valueOf(creasePatternCameraModel.getScale()));
        scaleFactorTextField.setCaretPosition(0);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof BackgroundModel) {
            setData((BackgroundModel) evt.getSource());
        }
        if (evt.getSource() instanceof ApplicationModel) {
            setData((ApplicationModel) evt.getSource());
        }
        if (evt.getSource() instanceof CameraModel) {
            setData((CameraModel) evt.getSource());
        }
        if (evt.getSource() instanceof CanvasModel) {
            setData(evt, (CanvasModel) evt.getSource());
        }
    }

    private void createUIComponents() {
        scrollPane1 = new JScrollPane();
        scrollPane1.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane1.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
    }
}
