package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.FoldLineAdditionalInputMode;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.voronoiAction)
public class VoronoiAction extends AbstractOrieditaAction {

    @Inject
    CanvasModel canvasModel;

    @Inject @Named("mainCreasePattern_Worker")
    CreasePattern_Worker mainCreasePatternWorker;

    @Inject
    public VoronoiAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.POLY_LINE_0);
        canvasModel.setMouseMode(MouseMode.VORONOI_CREATE_62);
        canvasModel.setMouseModeAfterColorSelection(MouseMode.VORONOI_CREATE_62);

        mainCreasePatternWorker.unselect_all();
    }
}
