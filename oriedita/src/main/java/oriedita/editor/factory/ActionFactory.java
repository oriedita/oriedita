package oriedita.editor.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.action.ActionType;
import oriedita.editor.action.DegAction;
import oriedita.editor.action.Oriagari_sousaAction;
import oriedita.editor.action.SelectionOperationAction;
import oriedita.editor.action.SetMouseModeAction;
import oriedita.editor.action.SetMouseModeLineTypeDeleteAction;
import oriedita.editor.action.SetMouseModeWithAfterColorAndUnselectAction;
import oriedita.editor.action.SetMouseModeWithUnselectAction;
import oriedita.editor.action.SuiteiAction;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.FoldLineAdditionalInputMode;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.handler.FoldedFigureOperationMode;
import oriedita.editor.service.FoldingService;
import origami.folding.FoldedFigure;

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

    @Named("SetMouseModeAction")
    public SetMouseModeAction setMouseModeAction(ActionType actionType, MouseMode mouseMode){
        return new SetMouseModeAction(canvasModel, actionType, mouseMode);
    }

    @Named("SetMouseModeWithUnselectAction")
    public SetMouseModeWithUnselectAction setMouseModeWithUnselectAction(ActionType actionType, MouseMode mouseMode){
        return new SetMouseModeWithUnselectAction(canvasModel, mainCreasePattern_Worker, actionType, mouseMode);
    }

    @Named("SetMouseModeWithAfterColorAndUnselectAction")
    public SetMouseModeWithAfterColorAndUnselectAction setMouseModeWithAfterColorAndUnselectAction(ActionType actionType, MouseMode mouseMode){
        return new SetMouseModeWithAfterColorAndUnselectAction(canvasModel, mainCreasePattern_Worker, actionType, mouseMode);
    }

    @Named("SetMouseModeLineTypeDeleteAction")
    public SetMouseModeLineTypeDeleteAction setMouseModeLineTypeDeleteAction(ActionType actionType, MouseMode mouseMode, FoldLineAdditionalInputMode foldLineAdditionalInputMode){
        return new SetMouseModeLineTypeDeleteAction(canvasModel, mainCreasePattern_Worker, actionType, mouseMode, foldLineAdditionalInputMode);
    }

    @Named("SelectionOperationAction")
    public SelectionOperationAction selectionOperationAction(ActionType actionType, CanvasModel.SelectionOperationMode selectionOperationMode, MouseMode mouseMode){
        return new SelectionOperationAction(canvasModel, actionType, selectionOperationMode, mouseMode);
    }

    @Named("SuiteiAction")
    public SuiteiAction suiteiAction(ActionType actionType, FoldedFigure.EstimationOrder estimationOrder){
        return new SuiteiAction(foldingService, mainCreasePattern_Worker, actionType, estimationOrder);
    }

    @Named("Oriagari_sousaAction")
    public Oriagari_sousaAction oriagari_sousaAction(ActionType actionType, FoldedFigureOperationMode foldedFigureOperationMode){
        return new Oriagari_sousaAction(canvasModel, actionType, foldedFigureOperationMode);
    }

    @Named("DegAction")
    public DegAction degAction(ActionType actionType, MouseMode mouseMode, AngleSystemModel.AngleSystemInputType angleSystemInputType){
        return new DegAction(canvasModel, angleSystemModel, actionType, mouseMode, angleSystemInputType);
    }
}
