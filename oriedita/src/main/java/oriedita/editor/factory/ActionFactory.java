package oriedita.editor.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.action.AbstractOrieditaAction;
import oriedita.editor.action.DegAction;
import oriedita.editor.action.Oriagari_sousaAction;
import oriedita.editor.action.OrieditaAction;
import oriedita.editor.action.SelectionOperationAction;
import oriedita.editor.action.SetMouseModeLineTypeDeleteAction;
import oriedita.editor.action.SetMouseModeWithAfterColorAndUnselectAction;
import oriedita.editor.action.SuiteiAction;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.FoldLineAdditionalInputMode;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.handler.FoldedFigureOperationMode;
import oriedita.editor.service.ButtonService;
import oriedita.editor.service.FoldingService;
import origami.folding.FoldedFigure;

import java.awt.event.ActionEvent;

@ApplicationScoped
public class ActionFactory {
    @Inject
    CanvasModel canvasModel;
    @Inject
    @Named("mainCreasePattern_Worker")
    CreasePattern_Worker mainCreasePattern_Worker;
    @Inject
    AngleSystemModel angleSystemModel;
    @Inject
    FoldingService foldingService;
    @Inject
    FoldedFiguresList foldedFiguresList;
    @Inject
    ButtonService buttonService;

    public OrieditaAction setMouseModeAction(MouseMode mouseMode){
        return new AbstractOrieditaAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvasModel.setMouseMode(mouseMode);
            }
        };
    }

    public OrieditaAction setMouseModeWithUnselectAction(MouseMode mouseMode){
        return new AbstractOrieditaAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvasModel.setMouseMode(mouseMode);
                mainCreasePattern_Worker.unselect_all();
            }
        };
    }

    public SetMouseModeWithAfterColorAndUnselectAction setMouseModeWithAfterColorAndUnselectAction(MouseMode mouseMode){
        return new SetMouseModeWithAfterColorAndUnselectAction(canvasModel, mainCreasePattern_Worker, buttonService, mouseMode);
    }

    public SetMouseModeLineTypeDeleteAction setMouseModeLineTypeDeleteAction(MouseMode mouseMode, FoldLineAdditionalInputMode foldLineAdditionalInputMode){
        return new SetMouseModeLineTypeDeleteAction(canvasModel, mainCreasePattern_Worker, mouseMode, foldLineAdditionalInputMode);
    }

    public SelectionOperationAction selectionOperationAction(CanvasModel.SelectionOperationMode selectionOperationMode, MouseMode mouseMode){
        return new SelectionOperationAction(canvasModel, selectionOperationMode, mouseMode);
    }

    public SuiteiAction suiteiAction(FoldedFigure.EstimationOrder estimationOrder){
        return new SuiteiAction(foldingService, mainCreasePattern_Worker, estimationOrder);
    }

    public Oriagari_sousaAction oriagari_sousaAction(FoldedFigureOperationMode foldedFigureOperationMode){
        return new Oriagari_sousaAction(canvasModel, foldedFiguresList, foldedFigureOperationMode);
    }

    public DegAction degAction(MouseMode mouseMode, AngleSystemModel.AngleSystemInputType angleSystemInputType){
        return new DegAction(canvasModel, angleSystemModel, buttonService, mouseMode, angleSystemInputType);
    }
}
