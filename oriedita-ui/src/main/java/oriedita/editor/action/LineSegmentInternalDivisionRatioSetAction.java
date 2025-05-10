package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.InternalDivisionRatioModel;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.lineSegmentInternalDivisionRatioSetAction)
public class LineSegmentInternalDivisionRatioSetAction extends AbstractOrieditaAction{
    @Inject
    CanvasModel canvasModel;

    @Inject
    public LineSegmentInternalDivisionRatioSetAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_RATIO_SET_28);
        canvasModel.setMouseModeAfterColorSelection(MouseMode.LINE_SEGMENT_RATIO_SET_28);
    }
}
