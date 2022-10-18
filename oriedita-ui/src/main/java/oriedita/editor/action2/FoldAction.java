package oriedita.editor.action2;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tinylog.Logger;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.service.FoldingService;
import origami.crease_pattern.worker.foldlineset.Check4;
import origami.folding.FoldedFigure;

import javax.swing.*;
import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.foldAction)
public class FoldAction extends AbstractOrieditaAction {
    @Inject
    FoldingService foldingService;
    @Inject
    ApplicationModel applicationModel;
    @Inject @Named("mainCreasePattern_Worker")
    CreasePattern_Worker mainCreasePatternWorker;

    @Inject
    public FoldAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Logger.info("20180220 get_i_fold_type() = " + foldingService.getFoldType());

        if (!applicationModel.getFoldWarning()) {
            try {
                Check4.apply(mainCreasePatternWorker.getFoldLineSet());
            } catch (InterruptedException bruh) {
                Logger.info("Warning window broke");
            }
            if (!mainCreasePatternWorker.getFoldLineSet().getViolations().isEmpty()) {
                JCheckBox checkbox = new JCheckBox("Don't show this again");
                Object[] params = {"Detected errors in flat foldability. Continue to fold?", checkbox};
                int warningResult = JOptionPane.showConfirmDialog(null, params, "Warning", JOptionPane.YES_NO_OPTION);
                if (warningResult == JOptionPane.YES_OPTION || checkbox.isSelected()) {
                    foldCreasePattern(mainCreasePatternWorker, foldingService, applicationModel);
                }
                applicationModel.setFoldWarning(checkbox.isSelected());
            } else {
                foldCreasePattern(mainCreasePatternWorker, foldingService, applicationModel);
            }
        } else {
            foldCreasePattern(mainCreasePatternWorker, foldingService, applicationModel);
        }
    }

    private void foldCreasePattern(CreasePattern_Worker mainCreasePatternWorker, FoldingService foldingService, ApplicationModel applicationModel) {
        foldingService.fold(FoldedFigure.EstimationOrder.ORDER_5);//引数の意味は(i_fold_type , i_suitei_meirei);

        if (!applicationModel.getSelectPersistent()) {
            mainCreasePatternWorker.unselect_all();
        }
    }
}
