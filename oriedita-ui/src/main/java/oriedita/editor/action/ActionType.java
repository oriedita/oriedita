package oriedita.editor.action;

import java.util.HashMap;
import java.util.Map;

public enum ActionType {
    lineWidthDecreaseAction("lineWidthDecreaseAction"),
    IMPORT("importAction"),
    IMPORT_ADD("inputDataAction"),
    drawCreaseFreeAction("drawCreaseFreeAction"),
    lineWidthIncreaseAction("lineWidthIncreaseAction"),
    pointSizeDecreaseAction("pointSizeDecreaseAction"),
    pointSizeIncreaseAction("pointSizeIncreaseAction"),
    antiAliasToggleAction("antiAliasToggleAction"),
    lineStyleChangeAction("lineStyleChangeAction"),
    colRedAction("colRedAction"),
    colBlueAction("colBlueAction"),
    colBlackAction("colBlackAction"),
    colCyanAction("colCyanAction"),
    toMountainAction("toMountainAction"),
    toValleyAction("toValleyAction"),
    toEdgeAction("toEdgeAction"),
    toAuxAction("toAuxAction"),
    zen_yama_tani_henkanAction("zen_yama_tani_henkanAction"),
    senbun_henkan2Action("senbun_henkan2Action"),
    senbun_henkanAction("senbun_henkanAction"),
    in_L_col_changeAction("in_L_col_changeAction"),
    on_L_col_changeButton("on_L_col_changeButton"),
    v_addAction("v_addAction"),
    v_delAction("v_delAction"),
    v_del_ccAction("v_del_ccAction"),
    v_del_allAction("v_del_allAction"),
    v_del_all_ccAction("v_del_all_ccAction"),
    drawTwoColoredCpAction("drawTwoColoredCpAction"),
    suitei_01Action("suitei_01Action"),
    koteimen_siteiAction("koteimen_siteiAction"),
    suitei_02Action("suitei_02Action"),
    suitei_03Action("suitei_03Action"),
    coloredXRayDecreaseAction("coloredXRayDecreaseAction"),
    coloredXRayIncreaseAction("coloredXRayIncreaseAction"),
    perpendicularDrawAction("perpendicularDrawAction"),
    lineSegmentDivisionSetAction("lineSegmentDivisionSetAction"),
    senbun_b_nyuryokuAction("senbun_b_nyuryokuAction"),
    all_s_step_to_orisenAction("all_s_step_to_orisenAction"),
    voronoiAction("voronoiAction"),
    parallelDrawAction("parallelDrawAction"),
    fishBoneDrawAction("fishBoneDrawAction"),
    setParallelDrawWidthAction("setParallelDrawWidthAction"),
    doubleSymmetricDrawAction("doubleSymmetricDrawAction"),
    makeFlatFoldableAction("makeFlatFoldableAction"),
    continuousSymmetricDrawAction("continuousSymmetricDrawAction"),
    symmetricDrawAction("symmetricDrawAction"),
    angleBisectorAction("angleBisectorAction"),
    lengthenCrease2Action("lengthenCrease2Action"),
    lengthenCreaseAction("lengthenCreaseAction"),
    drawCreaseRestrictedAction("drawCreaseRestrictedAction"),
    rabbitEarAction("rabbitEarAction"),
    foldableLineAction("foldableLineAction"),
    selectAction("selectAction"),
    selectAllAction("selectAllAction"),
    moveAction("moveAction"),
    move2p2pAction("move2p2pAction"),
    reflectAction("reflectAction"),
    unselectAction("unselectAction"),
    unselectAllAction("unselectAllAction"),
    copyAction("copyAction"),
    copy2p2pAction("copy2p2pAction"),
    d_s_LAction("d_s_LAction"),
    gridSizeDecreaseAction("gridSizeDecreaseAction"),
    gridSizeSetAction("gridSizeSetAction"),
    gridSizeIncreaseAction("gridSizeIncreaseAction"),
    gridColorAction("gridColorAction"),
    gridLineWidthDecreaseAction("gridLineWidthDecreaseAction"),
    gridLineWidthIncreaseAction("gridLineWidthIncreaseAction"),
    changeGridStateAction("changeGridStateAction"),
    moveIntervalGridVerticalAction("moveIntervalGridVerticalAction"),
    setIntervalGridSizeAction("setIntervalGridSizeAction"),
    moveIntervalGridHorizontalAction("moveIntervalGridHorizontalAction"),
    intervalGridColorAction("intervalGridColorAction"),
    lineSegmentDeleteAction("lineSegmentDeleteAction"),
    edgeLineSegmentDeleteAction("edgeLineSegmentDeleteAction"),
    auxLiveLineSegmentDeleteAction("auxLiveLineSegmentDeleteAction"),
    trimBranchesAction("trimBranchesAction"),
    setGridParametersAction("setGridParametersAction"),
    resetGridAction("resetGridAction"),
    operationFrameSelectAction("operationFrameSelectAction"),
    moveCreasePatternAction("moveCreasePatternAction"),
    creasePatternZoomOutAction("creasePatternZoomOutAction"),
    creasePatternZoomInAction("creasePatternZoomInAction"),
    rotateAnticlockwiseAction("rotateAnticlockwiseAction"),
    rotateClockwiseAction("rotateClockwiseAction"),
    senbun_yoke_henkanAction("senbun_yoke_henkanAction"),
    lineSegmentInternalDivisionRatioSetAction("lineSegmentInternalDivisionRatioSetAction"),
    drawLineSegmentInternalDivisionRatioAction("drawLineSegmentInternalDivisionRatioAction"),
    scaleFactorSetAction("scaleFactorSetAction"),
    rotationSetAction("rotationSetAction"),
    transparentAction("transparentAction"),
    backgroundTrimAction("backgroundTrimAction"),
    readBackgroundAction("readBackgroundAction"),
    backgroundToggleAction("backgroundToggleAction"),
    backgroundSetPositionAction("backgroundSetPositionAction"),
    backgroundLockAction("backgroundLockAction"),
    mouseSettingsAction("mouseSettingsAction"),
    foldAction("foldAction"),
    trashAction("foldedFigureTrashAction"),
    anotherSolutionAction("anotherSolutionAction"),
    haltAction("haltAction"),
    resetAction("resetAction"),
    flipAction("foldedFigureFlipAction"),
    scaleAction("scaleAction"),
    duplicate("duplicateAction"),
    frontColorAction("frontColorAction"),
    As100Action("As100Action"),
    lineColorAction("lineColorAction"),
    backColorAction("backColorAction"),
    oriagari_sousaAction("oriagari_sousaAction");


    static final Map<String, ActionType> actionMap;

    static {
        actionMap = new HashMap<>();
        for (ActionType type : values()) {
            actionMap.put(type.action(), type);
        }
    }

    private final String action;

    ActionType(String action) {
        this.action = action;
    }

    public static ActionType fromAction(String action) {
        if (actionMap.containsKey(action)) {
            return actionMap.get(action);
        }

        return null;
    }

    public String action() {
        return action;
    }
}
