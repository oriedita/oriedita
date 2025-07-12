package oriedita.editor.swing.tab;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.action.ActionType;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.service.ButtonService;
import oriedita.editor.swing.component.DropdownToolButton;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

@ApplicationScoped
public class DrawingTab {
    private final ButtonService buttonService;
    private final CanvasModel canvasModel;
    private final CreasePattern_Worker mainCreasePatternWorker;

    private JButton drawCreaseFreeBtn;
    private JButton drawCreaseRestrictedBtn;
    private DropdownToolButton angleRestrictedToolsDropdown;
    private JButton lengthenCreaseBtn;
    private JButton rabbitEarBtn;
    private JButton flatfoldVertexBtn;
    private DropdownToolButton perpendicularDropdown;
    private JButton mirroLineBtn;
    private JButton angleBisectorBtn;
    private JButton fishBoneBtn;
    private JButton reflectOverLineBtn;
    private JButton reflectThroughLinesBtn;
    private DropdownToolButton axiomDropdown;
    private JButton voronoiBtn;
    private JButton equallyDividedLineBtn;
    private JButton polygonBtn;
    private DropdownToolButton addSelectionDropdown;
    private DropdownToolButton removeSelectionDropdown;
    private DropdownToolButton setSelectionDropdown;
    private JButton mirrorBtn;
    private JButton copyLineBtn;
    private JButton copy4pBtn;
    private JButton moveLineBtn;
    private JButton move4pBtn;
    private JPanel root;
    private JButton eraseBtn;
    private DropdownToolButton deleteOnLineDropdown;
    private DropdownToolButton removeVerticesDropdown;
    private JButton addVertexBtn;
    private JButton replaceBtn;
    private JButton alternateIntersectedBtn;
    private JButton alternateIncludedBtn;
    private DropdownToolButton mvDropdown;
    private JButton ratioBtn;

    @Inject
    public DrawingTab(ButtonService buttonService,
                      CanvasModel canvasModel,
                      @Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker) {
        this.buttonService = buttonService;
        this.canvasModel = canvasModel;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
    }

    public void init() {
        angleRestrictedToolsDropdown.setActions(
                ActionType.deg2Action, ActionType.deg3Action, ActionType.deg1Action
        );
        perpendicularDropdown.setActions(
                ActionType.perpendicularDrawAction, ActionType.parallelDrawAction
        );
        axiomDropdown.setActions(
                ActionType.axiom5Action, ActionType.axiom7Action
        );
        addSelectionDropdown.setActions(
                ActionType.selectAction, ActionType.select_lXAction, ActionType.select_polygonAction, ActionType.selectLassoAction
        );
        removeSelectionDropdown.setActions(
                ActionType.unselectAction, ActionType.unselect_lXAction, ActionType.unselect_polygonAction, ActionType.unselectLassoAction
        );
        setSelectionDropdown.setActions(
                ActionType.unselectAllAction, ActionType.selectAllAction
        );

        deleteOnLineDropdown.setActions(
                ActionType.del_lAction, ActionType.del_l_XAction, ActionType.trimBranchesAction
        );

        removeVerticesDropdown.setActions(
                ActionType.v_del_allAction, ActionType.v_del_all_ccAction, ActionType.v_del_ccAction
        );

        mvDropdown.setActions(
                ActionType.senbun_henkan2Action,
                ActionType.senbun_henkanAction,
                ActionType.zen_yama_tani_henkanAction
        );

        buttonService.addDefaultListener($$$getRootComponent$$$());

        setData(canvasModel);
        canvasModel.addPropertyChangeListener(event -> setData(canvasModel));

        mainCreasePatternWorker.addPropertyChangeListener(e -> {
            if (e.getPropertyName().equals("isSelectionEmpty")) {
                updateSelectionTransformButtons();
            }
        });
    }

    public void setData(CanvasModel data) {
        updateSelectionTransformButtons();
    }

    private void updateSelectionTransformButtons() {
        Border defaultBorder = (Border) UIManager.get("Button.border");
        String warningMessage = "";
        var transformBtns = (new JButton[]{
                mirrorBtn,
                copyLineBtn,
                copy4pBtn,
                moveLineBtn,
                move4pBtn
        });
        for (JButton button : transformBtns) {
            button.setBorder(defaultBorder);
            if (mainCreasePatternWorker.getIsSelectionEmpty()) {
                button.setEnabled(false);
                if (button.isSelected()) {
                    var highlight = new LineBorder(Color.yellow);
                    button.setBorder(highlight);
                    warningMessage = "Selection Transformation Tools depend on crease(s) being selected in advance";
                }
            } else {
                button.setEnabled(true);
            }
        }
        canvasModel.setWarningMessage(warningMessage);
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
        root = new JPanel();
        root.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        root.setMinimumSize(new Dimension(100, 597));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(10, 1, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Draw");
        panel2.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        panel1.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        drawCreaseFreeBtn = new JButton();
        drawCreaseFreeBtn.setActionCommand("drawCreaseFreeAction");
        drawCreaseFreeBtn.setText("drawCreaseFree");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(drawCreaseFreeBtn, gbc);
        drawCreaseRestrictedBtn = new JButton();
        drawCreaseRestrictedBtn.setActionCommand("drawCreaseRestrictedAction");
        drawCreaseRestrictedBtn.setText("drawCreaseRestricted");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(drawCreaseRestrictedBtn, gbc);
        angleRestrictedToolsDropdown = new DropdownToolButton();
        angleRestrictedToolsDropdown.setText("angleRestricted");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(angleRestrictedToolsDropdown, gbc);
        lengthenCreaseBtn = new JButton();
        lengthenCreaseBtn.setActionCommand("lengthenCreaseAction");
        lengthenCreaseBtn.setText("lengthenCrease");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(lengthenCreaseBtn, gbc);
        rabbitEarBtn = new JButton();
        rabbitEarBtn.setActionCommand("rabbitEarAction");
        rabbitEarBtn.setSelected(false);
        rabbitEarBtn.setText("rabbitEar");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(rabbitEarBtn, gbc);
        flatfoldVertexBtn = new JButton();
        flatfoldVertexBtn.setActionCommand("makeFlatFoldableAction");
        flatfoldVertexBtn.setText("flatfoldVertex");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(flatfoldVertexBtn, gbc);
        perpendicularDropdown = new DropdownToolButton();
        perpendicularDropdown.setText("perpendicular");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(perpendicularDropdown, gbc);
        mirroLineBtn = new JButton();
        mirroLineBtn.setActionCommand("symmetricDrawAction");
        mirroLineBtn.setText("mirrorLine");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(mirroLineBtn, gbc);
        angleBisectorBtn = new JButton();
        angleBisectorBtn.setActionCommand("angleBisectorAction");
        angleBisectorBtn.setText("angleBisector");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(angleBisectorBtn, gbc);
        fishBoneBtn = new JButton();
        fishBoneBtn.setActionCommand("fishBoneDrawAction");
        fishBoneBtn.setText("gridFill");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(fishBoneBtn, gbc);
        reflectOverLineBtn = new JButton();
        reflectOverLineBtn.setActionCommand("doubleSymmetricDrawAction");
        reflectOverLineBtn.setText("reflectOverLine");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(reflectOverLineBtn, gbc);
        reflectThroughLinesBtn = new JButton();
        reflectThroughLinesBtn.setActionCommand("continuousSymmetricDrawAction");
        reflectThroughLinesBtn.setText("reflectThroughLines");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(reflectThroughLinesBtn, gbc);
        axiomDropdown = new DropdownToolButton();
        axiomDropdown.setText("axioms");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(axiomDropdown, gbc);
        ratioBtn = new JButton();
        ratioBtn.setActionCommand("drawLineSegmentInternalDivisionRatioAction");
        ratioBtn.setText("ratio");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(ratioBtn, gbc);
        equallyDividedLineBtn = new JButton();
        equallyDividedLineBtn.setActionCommand("senbun_b_nyuryokuAction");
        equallyDividedLineBtn.setText("equallyDividedLine");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(equallyDividedLineBtn, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(panel4, gbc);
        polygonBtn = new JButton();
        polygonBtn.setActionCommand("regularPolygonAction");
        polygonBtn.setText("polygon");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(polygonBtn, gbc);
        voronoiBtn = new JButton();
        voronoiBtn.setActionCommand("voronoiAction");
        voronoiBtn.setText("voronoi");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(voronoiBtn, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Select");
        panel1.add(label2, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 5), new Dimension(-1, 5), null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel1.add(spacer3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 7), new Dimension(-1, 7), null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        panel1.add(panel5, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        addSelectionDropdown = new DropdownToolButton();
        addSelectionDropdown.setText("addSelection");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(addSelectionDropdown, gbc);
        removeSelectionDropdown = new DropdownToolButton();
        removeSelectionDropdown.setText("removeSelection");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(removeSelectionDropdown, gbc);
        setSelectionDropdown = new DropdownToolButton();
        setSelectionDropdown.setText("setSelection");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(setSelectionDropdown, gbc);
        mirrorBtn = new JButton();
        mirrorBtn.setActionCommand("reflectAction");
        mirrorBtn.setText("mirror");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(mirrorBtn, gbc);
        copyLineBtn = new JButton();
        copyLineBtn.setActionCommand("copyAction");
        copyLineBtn.setText("copyLine");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(copyLineBtn, gbc);
        copy4pBtn = new JButton();
        copy4pBtn.setActionCommand("copy2p2pAction");
        copy4pBtn.setText("copy4p");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(copy4pBtn, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(panel6, gbc);
        panel6.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        moveLineBtn = new JButton();
        moveLineBtn.setActionCommand("moveAction");
        moveLineBtn.setText("moveLine");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(moveLineBtn, gbc);
        move4pBtn = new JButton();
        move4pBtn.setActionCommand("move2p2pAction");
        move4pBtn.setText("move4p");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(move4pBtn, gbc);
        final Spacer spacer4 = new Spacer();
        panel1.add(spacer4, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 5), new Dimension(-1, 5), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Edit");
        panel1.add(label3, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridBagLayout());
        panel1.add(panel7, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(panel8, gbc);
        deleteOnLineDropdown = new DropdownToolButton();
        deleteOnLineDropdown.setText("deleteOnLine");
        deleteOnLineDropdown.setToolTipText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel8.add(deleteOnLineDropdown, gbc);
        removeVerticesDropdown = new DropdownToolButton();
        removeVerticesDropdown.setText("removeVertices");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel8.add(removeVerticesDropdown, gbc);
        addVertexBtn = new JButton();
        addVertexBtn.setActionCommand("vertexAddAction");
        addVertexBtn.setText("addVertex");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel8.add(addVertexBtn, gbc);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        panel8.add(panel9, gbc);
        panel9.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        replaceBtn = new JButton();
        replaceBtn.setActionCommand("replace_lineAction");
        replaceBtn.setText("replace");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(replaceBtn, gbc);
        eraseBtn = new JButton();
        eraseBtn.setActionCommand("del_l_typeAction");
        eraseBtn.setText("erase");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(eraseBtn, gbc);
        alternateIntersectedBtn = new JButton();
        alternateIntersectedBtn.setActionCommand("on_L_col_changeAction");
        alternateIntersectedBtn.setText("alternateIntersected");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel8.add(alternateIntersectedBtn, gbc);
        alternateIncludedBtn = new JButton();
        alternateIncludedBtn.setActionCommand("in_L_col_changeAction");
        alternateIncludedBtn.setText("alternateIncluded");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel8.add(alternateIncludedBtn, gbc);
        mvDropdown = new DropdownToolButton();
        mvDropdown.setText("invertMv");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel8.add(mvDropdown, gbc);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(panel10, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

}
