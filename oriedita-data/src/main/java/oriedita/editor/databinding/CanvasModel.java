package oriedita.editor.databinding;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.AbstractModel;
import oriedita.editor.canvas.FoldLineAdditionalInputMode;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.canvas.MouseWheelTarget;
import oriedita.editor.handler.FoldedFigureOperationMode;
import oriedita.editor.service.BindingService;
import origami.crease_pattern.CustomLineTypes;
import origami.crease_pattern.element.LineColor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
public class CanvasModel extends AbstractModel implements Serializable {
    private LineColor lineColor;
    private LineColor auxLiveLineColor;
    private MouseMode mouseMode;
    private MouseMode mouseModeAfterColorSelection;
    private FoldLineAdditionalInputMode foldLineAdditionalInputMode;
    private FoldLineAdditionalInputMode foldLineAdditionalInputMode_old;
    private String warningMessage;
    private boolean toggleLineColor;
    private CustomLineTypes customFromLineType;
    private CustomLineTypes customToLineType;
    private CustomLineTypes delLineType;
    /**
     * Specify which operation to perform when selecting and operating the mouse. It is used to select a selected point after selection and automatically switch to the mouse operation that is premised on selection.
     */
    private SelectionOperationMode selectionOperationMode;
    private FoldedFigureOperationMode foldedFigureOperationMode;
    private MouseWheelTarget mouseInCpOrFoldedFigure;
    private final AtomicBoolean w_image_running = new AtomicBoolean(false); // Folding together execution. If a single image export is in progress, it will be true.
    private boolean ckbox_add_frame_SelectAnd3click_isSelected;

    @Inject
    public CanvasModel(BindingService bindingService) {
        super(bindingService);
        reset();
    }

    public CanvasModel() {
        this(BindingService.dummy());
    }

    public AtomicBoolean getW_image_running() {
        return w_image_running;
    }

    public void markDirty() {
        this.pcs.firePropertyChange("dirty", false, true);
    }

    public boolean getToggleLineColor() {
        return toggleLineColor;
    }

    public void setToggleLineColor(boolean toggleLineColor) {
        boolean oldToggleLineColor = this.toggleLineColor;
        this.toggleLineColor = toggleLineColor;
        this.pcs.firePropertyChange("toggleLineColor", oldToggleLineColor, toggleLineColor);
    }

    public FoldedFigureOperationMode getFoldedFigureOperationMode() {
        return foldedFigureOperationMode;
    }

    public void setFoldedFigureOperationMode(FoldedFigureOperationMode foldedFigureOperationMode) {
        FoldedFigureOperationMode oldI_foldedFigure_operation_mode = this.foldedFigureOperationMode;
        this.foldedFigureOperationMode = foldedFigureOperationMode;
        this.pcs.firePropertyChange("foldedFigureOperationMode", oldI_foldedFigure_operation_mode, foldedFigureOperationMode);
    }

    public boolean isCkbox_add_frame_SelectAnd3click_isSelected() {
        return ckbox_add_frame_SelectAnd3click_isSelected;
    }

    public void setCkbox_add_frame_SelectAnd3click_isSelected(boolean ckbox_add_frame_SelectAnd3click_isSelected) {
        boolean oldCkbox_add_frame_SelectAnd3click_isSelected = this.ckbox_add_frame_SelectAnd3click_isSelected;
        this.ckbox_add_frame_SelectAnd3click_isSelected = ckbox_add_frame_SelectAnd3click_isSelected;
        this.pcs.firePropertyChange("ckbox_add_frame_SelectAnd3click_isSelected", oldCkbox_add_frame_SelectAnd3click_isSelected, ckbox_add_frame_SelectAnd3click_isSelected);
    }

    public SelectionOperationMode getSelectionOperationMode() {
        return selectionOperationMode;
    }

    public void setSelectionOperationMode(SelectionOperationMode selectionOperationMode) {
        SelectionOperationMode oldSelectionOperationMode = this.selectionOperationMode;
        this.selectionOperationMode = selectionOperationMode;
        this.pcs.firePropertyChange("selectionOperationMode", oldSelectionOperationMode, selectionOperationMode);
    }

    public void restoreFoldLineAdditionalInputMode() {
        setFoldLineAdditionalInputMode(foldLineAdditionalInputMode_old);
    }

    public FoldLineAdditionalInputMode getFoldLineAdditionalInputMode() {
        return foldLineAdditionalInputMode;
    }

    public void setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode foldLineAdditionalInputMode) {
        FoldLineAdditionalInputMode oldFoldLineAdditionalInputMode = this.foldLineAdditionalInputMode;
        this.foldLineAdditionalInputMode_old = this.foldLineAdditionalInputMode;
        this.foldLineAdditionalInputMode = foldLineAdditionalInputMode;
        this.pcs.firePropertyChange("foldLineAdditionalInputMode", oldFoldLineAdditionalInputMode, foldLineAdditionalInputMode);
    }

    public MouseMode getMouseModeAfterColorSelection() {
        return mouseModeAfterColorSelection;
    }

    public void setMouseModeAfterColorSelection(MouseMode mouseModeAfterColorSelection) {
        MouseMode oldMouseModeAfterColorSelection = this.mouseModeAfterColorSelection;
        this.mouseModeAfterColorSelection = mouseModeAfterColorSelection;
        this.pcs.firePropertyChange("mouseModeAfterColorSelection", oldMouseModeAfterColorSelection, mouseModeAfterColorSelection);
    }

    public MouseMode getMouseMode() {
        return mouseMode;
    }

    public void setMouseMode(MouseMode mouseMode) {
        MouseMode oldMouseMode = this.mouseMode;
        this.mouseMode = mouseMode;

        // let propertyChange fire despite having the same value, otherwise
        // re-triggering the same MouseHandler won't reset its fields
        PropertyChangeEvent event = new PropertyChangeEvent( pcs, "mouseMode", oldMouseMode, mouseMode );
        for( PropertyChangeListener listener : pcs.getPropertyChangeListeners() ) {
            listener.propertyChange( event );
        }
    }

    public LineColor calculateLineColor() {
        return toggleLineColor ? lineColor.changeMV() : lineColor;
    }

    public LineColor calculateAuxColor(){
        return toggleLineColor ? auxLiveLineColor.changeAuxColor() : auxLiveLineColor;
    }

    public LineColor getLineColor() {
        return lineColor;
    }

    public void setLineColor(LineColor lineColor) {
        LineColor oldLineColor = this.lineColor;
        this.lineColor = lineColor;
        this.pcs.firePropertyChange("lineColor", oldLineColor, lineColor);
    }

    public LineColor getAuxLiveLineColor() {
        return auxLiveLineColor;
    }

    public void setAuxLiveLineColor(LineColor auxLiveLineColor) {
        LineColor oldAuxLiveLineColor = this.auxLiveLineColor;
        this.auxLiveLineColor = auxLiveLineColor;
        this.pcs.firePropertyChange("auxLiveLineColor", oldAuxLiveLineColor, auxLiveLineColor);
    }

    public void reset() {
        lineColor = LineColor.RED_1;
        auxLiveLineColor = LineColor.ORANGE_4;

        mouseMode = MouseMode.FOLDABLE_LINE_DRAW_71;
        mouseModeAfterColorSelection = MouseMode.FOLDABLE_LINE_DRAW_71;

        foldLineAdditionalInputMode = FoldLineAdditionalInputMode.POLY_LINE_0;
        foldLineAdditionalInputMode_old = FoldLineAdditionalInputMode.POLY_LINE_0;

        selectionOperationMode = SelectionOperationMode.NORMAL_0;

        ckbox_add_frame_SelectAnd3click_isSelected = false;

        toggleLineColor = false;

        mouseInCpOrFoldedFigure = MouseWheelTarget.CREASE_PATTERN_0;

        customFromLineType = CustomLineTypes.ANY;
        customToLineType = CustomLineTypes.EGDE;

        delLineType = CustomLineTypes.ANY;

        this.notifyAllListeners();
    }


    public void set(CanvasModel canvasModel) {
        toggleLineColor = canvasModel.getToggleLineColor();
        lineColor = canvasModel.getLineColor();
        auxLiveLineColor = canvasModel.getAuxLiveLineColor();
        mouseMode = canvasModel.getMouseMode();
        mouseModeAfterColorSelection = canvasModel.getMouseModeAfterColorSelection();
        foldLineAdditionalInputMode = canvasModel.getFoldLineAdditionalInputMode();

        selectionOperationMode = canvasModel.getSelectionOperationMode();
        customFromLineType = canvasModel.getCustomFromLineType();
        customToLineType = canvasModel.getCustomToLineType();
        delLineType = canvasModel.getDelLineType();
        this.notifyAllListeners();
    }

    public MouseWheelTarget getMouseInCpOrFoldedFigure() {
        return mouseInCpOrFoldedFigure;
    }

    public void setMouseInCpOrFoldedFigure(MouseWheelTarget mouseInCpOrFoldedFigure) {
        MouseWheelTarget oldMouseInCpOrFoldedFigure = this.mouseInCpOrFoldedFigure;
        this.mouseInCpOrFoldedFigure = mouseInCpOrFoldedFigure;
        this.pcs.firePropertyChange("mouseInCpOrFoldedFigure", oldMouseInCpOrFoldedFigure, mouseInCpOrFoldedFigure);
    }

    public String getWarningMessage() {
        return warningMessage;
    }

    public void setWarningMessage(String warningMessage) {
        String oldWarningMessage = this.warningMessage;
        this.warningMessage = warningMessage;
        this.pcs.firePropertyChange("warningMessage", oldWarningMessage, warningMessage);
    }

    public void setCustomFromLineType(CustomLineTypes customFromLineType){
        CustomLineTypes oldCustomFromLineType = this.customFromLineType;
        this.customFromLineType = customFromLineType;
        this.pcs.firePropertyChange("customFromLineType", oldCustomFromLineType, customFromLineType);
    }

    public CustomLineTypes getCustomFromLineType(){
        return customFromLineType;
    }

    public void setCustomToLineType(CustomLineTypes customToLineType){
        CustomLineTypes oldCustomToLineType = this.customToLineType;
        this.customToLineType = customToLineType;
        this.pcs.firePropertyChange("customToLineType", oldCustomToLineType, customToLineType);
    }

    public CustomLineTypes getDelLineType() { return delLineType; }

    public void setDelLineType(CustomLineTypes delLineType) {
        CustomLineTypes oldDelLineType = this.delLineType;
        this.delLineType = delLineType;
        this.pcs.firePropertyChange("delLineType", oldDelLineType, delLineType);
    }

    public CustomLineTypes getCustomToLineType(){
        return customToLineType;
    }

    public enum SelectionOperationMode {
        NORMAL_0,
        MOVE_1,
        MOVE4P_2,
        COPY_3,
        COPY4P_4,
        MIRROR_5,
    }
}
