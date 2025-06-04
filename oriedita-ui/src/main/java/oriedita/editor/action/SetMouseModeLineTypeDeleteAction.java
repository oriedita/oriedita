package oriedita.editor.action;

import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.FoldLineAdditionalInputMode;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;

import java.awt.event.ActionEvent;

public class SetMouseModeLineTypeDeleteAction extends AbstractOrieditaAction implements MouseModeAction {
    private final CanvasModel canvasModel;
    private final CreasePattern_Worker mainCreasePatternWorker;
    private final MouseMode mouseMode;
    private final FoldLineAdditionalInputMode foldLineAdditionalInputMode;

    public SetMouseModeLineTypeDeleteAction(CanvasModel canvasModel,
                                            CreasePattern_Worker mainCreasePatternWorker,
                                            MouseMode mouseMode,
                                            FoldLineAdditionalInputMode foldLineAdditionalInputMode){
        this.canvasModel = canvasModel;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.mouseMode = mouseMode;
        this.foldLineAdditionalInputMode = foldLineAdditionalInputMode;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        canvasModel.setMouseMode(mouseMode);
        canvasModel.setFoldLineAdditionalInputMode(foldLineAdditionalInputMode);
        mainCreasePatternWorker.unselect_all();
    }

    @Override
    public MouseMode getMouseMode() {
        return mouseMode;
    }
}
