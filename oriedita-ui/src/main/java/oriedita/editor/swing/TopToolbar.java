package oriedita.editor.swing;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tinylog.Logger;
import oriedita.common.converter.DoubleConverter;
import oriedita.editor.Colors;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.CameraModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.GridModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.handler.MouseHandlerSettingGroup;
import oriedita.editor.handler.MouseModeHandler;
import oriedita.editor.handler.UiFor;
import oriedita.editor.service.ButtonService;
import oriedita.editor.swing.component.DraggableTextField;
import origami.crease_pattern.element.Point;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class TopToolbar {
    private JPanel root;
    private JButton cycleGridButton;
    private JButton decreaseGridSizeButton;
    private JButton increaseGridSizeButton;
    private JButton resetButton;
    private JButton foldButton;
    private JButton stopFoldingButton;
    private JButton trashButton;
    private JButton moveButton;
    private JButton zoomInButton;
    private JButton zoomOutButton;
    private DraggableTextField zoomTextField;
    private JButton rotateCounterClockwiseButton;
    private JButton rotateClockwiseButton;
    private DraggableTextField rotationTextField;
    private JCheckBox foldabilityCheckbox;
    private JScrollPane scrollPane;
    private DraggableTextField gridSizeTextField;
    private JPanel settingsRoot;

    private final ButtonService buttonService;
    private final GridModel gridModel;
    private final CanvasModel canvasModel;
    private final CameraModel creasePatternCameraModel;
    private final ApplicationModel applicationModel;
    private final Camera cpCam;

    @Inject
    public TopToolbar(ButtonService buttonService,
                      GridModel gridModel,
                      CanvasModel canvasModel,
                      CameraModel creasePatternCameraModel,
                      ApplicationModel applicationModel,
                      CameraModel cameraModel,
                      @Named("creasePatternCamera") Camera cpCam
    ) {
        this.buttonService = buttonService;
        this.gridModel = gridModel;
        this.canvasModel = canvasModel;
        this.creasePatternCameraModel = creasePatternCameraModel;
        this.applicationModel = applicationModel;
        this.cpCam = cpCam;
        $$$setupUI$$$();
    }

    public void init() {
        buttonService.addDefaultListener($$$getRootComponent$$$());

        applicationModel.addPropertyChangeListener(e -> setData(applicationModel));

        scrollPane.getHorizontalScrollBar().addAdjustmentListener(e -> {
            if (scrollPane.getHorizontalScrollBar().isVisible()) {
                scrollPane.setMinimumSize(new Dimension(-1, 40));
            } else {
                scrollPane.setMinimumSize(new Dimension(-1, 30));
            }
            root.revalidate();
            root.validate();
            root.repaint();
        });
        root.addComponentListener(new ComponentAdapter() {
            int lastHeight = root.getHeight();

            @Override
            public void componentResized(ComponentEvent e) {
                int heightChange = lastHeight - root.getHeight();
                if (heightChange != 0) {
                    cpCam.displayPositionMove(new Point(0, heightChange));
                    canvasModel.markDirty();
                    lastHeight = root.getHeight();
                }
            }
        });

        gridModel.bind(gridSizeTextField, "gridSize");

        creasePatternCameraModel.bind(zoomTextField, "scale", new DoubleConverter("0.0####"));
        creasePatternCameraModel.bind(rotationTextField, "rotation", new DoubleConverter("0.0####"));

        gridSizeTextField.addTickListener(d -> gridModel.setGridSize(gridModel.getGridSize() + d));
        zoomTextField.addRawListener((d, fine) -> {
            creasePatternCameraModel.setScale(creasePatternCameraModel.getScaleForZoom(-d, fine ? .05 : .5));
        });
        rotationTextField.addRawListener(
                (d, fine) -> creasePatternCameraModel.setRotation(
                        creasePatternCameraModel.getRotation() + d * (fine ? .05 : 1)));
        root.revalidate();
        root.validate();
        root.repaint();

    }

    private void setData(ApplicationModel applicationModel) {
        foldabilityCheckbox.setSelected(applicationModel.getCheck4Enabled());
    }

    public void createUIComponents() {
        gridSizeTextField = new DraggableTextField(3);
        rotationTextField = new DraggableTextField();
        zoomTextField = new DraggableTextField();
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
        root.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane = new JScrollPane();
        root.add(scrollPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 8, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane.setViewportView(panel1);
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        cycleGridButton = new JButton();
        cycleGridButton.setActionCommand("changeGridStateAction");
        cycleGridButton.setText("cycleGrid");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 10;
        panel2.add(cycleGridButton, gbc);
        decreaseGridSizeButton = new JButton();
        decreaseGridSizeButton.setActionCommand("gridSizeDecreaseAction");
        decreaseGridSizeButton.setText("decreaseGridSize");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 10;
        panel2.add(decreaseGridSizeButton, gbc);
        increaseGridSizeButton = new JButton();
        increaseGridSizeButton.setActionCommand("gridSizeIncreaseAction");
        increaseGridSizeButton.setText("increaseGridSize");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 10;
        panel2.add(increaseGridSizeButton, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(gridSizeTextField, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        panel1.add(panel3, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        resetButton = new JButton();
        resetButton.setActionCommand("resetAction");
        resetButton.setText("reset");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 20;
        panel3.add(resetButton, gbc);
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        panel1.add(panel4, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        foldButton = new JButton();
        foldButton.setActionCommand("foldAction");
        foldButton.setText("fold");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 10;
        panel4.add(foldButton, gbc);
        stopFoldingButton = new JButton();
        stopFoldingButton.setActionCommand("haltAction");
        stopFoldingButton.setText("stopFolding");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 10;
        panel4.add(stopFoldingButton, gbc);
        trashButton = new JButton();
        trashButton.setActionCommand("foldedFigureTrashAction");
        trashButton.setText("trash");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 10;
        panel4.add(trashButton, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        panel1.add(panel5, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel5.add(panel6, gbc);
        moveButton = new JButton();
        moveButton.setActionCommand("moveCreasePatternAction");
        moveButton.setText("move");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 10;
        panel6.add(moveButton, gbc);
        zoomOutButton = new JButton();
        zoomOutButton.setActionCommand("creasePatternZoomOutAction");
        zoomOutButton.setText("zoomOut");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 10;
        panel6.add(zoomOutButton, gbc);
        zoomInButton = new JButton();
        zoomInButton.setActionCommand("creasePatternZoomInAction");
        zoomInButton.setText("zoomIn");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 10;
        panel6.add(zoomInButton, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(zoomTextField, gbc);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridBagLayout());
        panel1.add(panel7, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        rotateCounterClockwiseButton = new JButton();
        rotateCounterClockwiseButton.setActionCommand("rotateAnticlockwiseAction");
        rotateCounterClockwiseButton.setText("rotateCounterClockwise");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 10;
        panel7.add(rotateCounterClockwiseButton, gbc);
        rotateClockwiseButton = new JButton();
        rotateClockwiseButton.setActionCommand("rotateClockwiseAction");
        rotateClockwiseButton.setText("rotateClockwise");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 10;
        panel7.add(rotateClockwiseButton, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(rotationTextField, gbc);
        foldabilityCheckbox = new JCheckBox();
        foldabilityCheckbox.setActionCommand("cAMVAction");
        foldabilityCheckbox.setText("Check Foldability");
        panel1.add(foldabilityCheckbox, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

}
