package oriedita.editor.action2;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tinylog.Logger;
import oriedita.editor.canvas.CreasePattern_Worker;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.all_s_step_to_orisenAction)
public class All_s_step_to_orisenAction extends AbstractOrieditaAction {
    @Inject
    @Named("mainCreasePattern_Worker")
    CreasePattern_Worker mainCreasePatternWorker;

    @Inject
    public All_s_step_to_orisenAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Logger.info("i_egaki_dankai = " + mainCreasePatternWorker.getDrawingStage());
        Logger.info("i_kouho_dankai = " + mainCreasePatternWorker.getCandidateSize());

        mainCreasePatternWorker.all_s_step_to_orisen();
    }
}
