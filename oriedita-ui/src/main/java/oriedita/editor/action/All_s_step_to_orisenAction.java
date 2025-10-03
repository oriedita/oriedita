package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tinylog.Logger;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.handler.Handles;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.all_s_step_to_orisenAction)
public class All_s_step_to_orisenAction extends AbstractOrieditaAction {
    @Inject
    @Named("mainCreasePattern_Worker")
    CreasePattern_Worker mainCreasePatternWorker;

    @Inject
    @Handles(MouseMode.VORONOI_CREATE_62)
    VoronoiHandler voronoiHandler;

    @Inject
    CanvasModel canvasModel;

    @Inject
    public All_s_step_to_orisenAction() {
    }

    @Override
    public boolean resetLineStep() {
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Logger.info("lineStep_Size = " + mainCreasePatternWorker.getLineStep().size());
        if (canvasModel.getMouseMode() == MouseMode.VORONOI_CREATE_62) {
            voronoiHandler.apply();
            mainCreasePatternWorker.record();
        }
    }

    public interface VoronoiHandler {
        void apply();
    }
}
