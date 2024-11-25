package oriedita.editor.service;

import com.formdev.flatlaf.FlatLaf;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.AnimationDurations;
import oriedita.editor.Animations;
import oriedita.editor.Canvas;
import oriedita.editor.Colors;
import oriedita.editor.FrameProvider;
import oriedita.editor.action.ActionService;
import oriedita.editor.action.ActionType;
import oriedita.editor.action.LambdaAction;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.FoldLineAdditionalInputMode;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.BackgroundModel;
import oriedita.editor.databinding.CameraModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.databinding.GridModel;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.factory.ActionFactory;
import oriedita.editor.handler.FoldedFigureOperationMode;
import origami.crease_pattern.CustomLineTypes;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.folding.FoldedFigure;

import javax.swing.JColorChooser;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class ActionRegistrationService {
    @Inject
    ActionFactory actionFactory;
    @Inject
    CanvasModel canvasModel;
    @Inject
    ApplicationModel applicationModel;
    @Inject
    GridModel gridModel;
    @Inject
    FrameProvider frameProvider;
    @Inject
    AngleSystemModel angleSystemModel;
    @Inject
    FoldedFigureModel foldedFigureModel;
    @Inject
    BackgroundModel backgroundModel;
    @Inject
    @Named("mainCreasePattern_Worker")
    CreasePattern_Worker mainCreasePatternWorker;
    @Inject
    AnimationService animationService;
    @Inject
    FileSaveService fileSaveService;
    @Inject
    FoldingService foldingService;
    @Inject
    FoldedFiguresList foldedFiguresList;
    @Inject
    CameraModel cameraModel;
    @Inject
    ButtonService buttonService;
    @Inject
    Canvas canvas;

    @Inject ActionService actionService;

    public void registerActionsInitial() {
        // setMouseMode actions
        Map<ActionType, MouseMode> mouseModeActions = new HashMap<>() {{
            put(ActionType.selectAction, MouseMode.CREASE_SELECT_19);
            put(ActionType.unselectAction, MouseMode.CREASE_UNSELECT_20);
            put(ActionType.moveCreasePatternAction, MouseMode.MOVE_CREASE_PATTERN_2);
            put(ActionType.backgroundSetPositionAction, MouseMode.BACKGROUND_CHANGE_POSITION_26);
            put(ActionType.circleDrawAction, MouseMode.CIRCLE_DRAW_42);
            put(ActionType.circleDrawThreePointAction, MouseMode.CIRCLE_DRAW_THREE_POINT_43);
            put(ActionType.circleDrawSeparateAction, MouseMode.CIRCLE_DRAW_SEPARATE_44);
            put(ActionType.circleDrawTangentLineAction, MouseMode.CIRCLE_DRAW_TANGENT_LINE_45);
            put(ActionType.circleDrawInvertedAction, MouseMode.CIRCLE_DRAW_INVERTED_46);
            put(ActionType.circleDrawFreeAction, MouseMode.CIRCLE_DRAW_FREE_47);
            put(ActionType.circleDrawConcentricAction, MouseMode.CIRCLE_DRAW_CONCENTRIC_48);
            put(ActionType.circleDrawConcentricSelectAction, MouseMode.CIRCLE_DRAW_CONCENTRIC_SELECT_49);
            put(ActionType.circleDrawTwoConcentricAction, MouseMode.CIRCLE_DRAW_CONCENTRIC_TWO_CIRCLE_SELECT_50);
            put(ActionType.foldedFigureMoveAction, MouseMode.MOVE_CALCULATED_SHAPE_102);
            put(ActionType.o_F_checkAction, MouseMode.FLAT_FOLDABLE_CHECK_63);
            put(ActionType.del_lAction, MouseMode.CREASE_DELETE_OVERLAPPING_64);
            put(ActionType.del_l_XAction, MouseMode.CREASE_DELETE_INTERSECTING_65);
            put(ActionType.selectLassoAction, MouseMode.SELECT_LASSO_74);
            put(ActionType.unselectLassoAction, MouseMode.UNSELECT_LASSO_75);
            put(ActionType.select_polygonAction, MouseMode.SELECT_POLYGON_66);
            put(ActionType.unselect_polygonAction, MouseMode.UNSELECT_POLYGON_67);
            put(ActionType.select_lXAction, MouseMode.SELECT_LINE_INTERSECTING_68);
            put(ActionType.unselect_lXAction, MouseMode.UNSELECT_LINE_INTERSECTING_69);
            put(ActionType.addColorConstraintAction, MouseMode.ADD_FOLDING_CONSTRAINT);
        }};

        for (Map.Entry<ActionType, MouseMode> entry : mouseModeActions.entrySet()) {
            actionService.registerAction(entry.getKey(), actionFactory.setMouseModeAction(entry.getValue()));
        }

        // setMouseModeWithUnselect actions
        Map<ActionType, MouseMode> mouseModeWithUnselectActions = Map.ofEntries(
                Map.entry(ActionType.senbun_henkanAction, MouseMode.CHANGE_CREASE_TYPE_4),
                Map.entry(ActionType.vertexAddAction, MouseMode.DRAW_POINT_14),
                Map.entry(ActionType.vertexDeleteAction, MouseMode.DELETE_POINT_15),
                Map.entry(ActionType.toMountainAction, MouseMode.CREASE_MAKE_MOUNTAIN_23),
                Map.entry(ActionType.toValleyAction, MouseMode.CREASE_MAKE_VALLEY_24),
                Map.entry(ActionType.toEdgeAction, MouseMode.CREASE_MAKE_EDGE_25),
                Map.entry(ActionType.toAuxAction, MouseMode.CREASE_MAKE_AUX_60),
                Map.entry(ActionType.v_del_ccAction, MouseMode.VERTEX_DELETE_ON_CREASE_41),
                Map.entry(ActionType.senbun_henkan2Action, MouseMode.CREASE_TOGGLE_MV_58),
                Map.entry(ActionType.replace_lineAction, MouseMode.REPLACE_LINE_TYPE_SELECT_72),
                Map.entry(ActionType.del_l_typeAction, MouseMode.DELETE_LINE_TYPE_SELECT_73),
                Map.entry(ActionType.senbun_yoke_henkanAction, MouseMode.CREASE_ADVANCE_TYPE_30),
                Map.entry(ActionType.sen_tokutyuu_color_henkouAction, MouseMode.CIRCLE_CHANGE_COLOR_59),
                Map.entry(ActionType.l1Action, MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_1_53),
                Map.entry(ActionType.l2Action, MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_2_54),
                Map.entry(ActionType.a1Action, MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_1_55),
                Map.entry(ActionType.a2Action, MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_2_56),
                Map.entry(ActionType.a3Action, MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_3_57),
                Map.entry(ActionType.textAction, MouseMode.TEXT)
        );

        for (Map.Entry<ActionType, MouseMode> entry : mouseModeWithUnselectActions.entrySet()) {
            actionService.registerAction(entry.getKey(), actionFactory.setMouseModeWithUnselectAction(entry.getValue()));
        }

        // setMouseModeWithAfterColorAndUnselect actions
        Map<ActionType, MouseMode> mouseModeWithAfterColorAndUnselectActions = new HashMap<>() {{
            put(ActionType.lengthenCreaseAction, MouseMode.LENGTHEN_CREASE_5);
            put(ActionType.angleBisectorAction, MouseMode.SQUARE_BISECTOR_7);
            put(ActionType.rabbitEarAction, MouseMode.INWARD_8);
            put(ActionType.perpendicularDrawAction, MouseMode.PERPENDICULAR_DRAW_9);
            put(ActionType.symmetricDrawAction, MouseMode.SYMMETRIC_DRAW_10);
            put(ActionType.drawCreaseRestrictedAction, MouseMode.DRAW_CREASE_RESTRICTED_11);
            put(ActionType.senbun_b_nyuryokuAction, MouseMode.LINE_SEGMENT_DIVISION_27);
            put(ActionType.fishBoneDrawAction, MouseMode.FISH_BONE_DRAW_33);
            put(ActionType.doubleSymmetricDrawAction, MouseMode.DOUBLE_SYMMETRIC_DRAW_35);
            put(ActionType.makeFlatFoldableAction, MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38);
            put(ActionType.parallelDrawAction, MouseMode.PARALLEL_DRAW_40);
            put(ActionType.setParallelDrawWidthAction, MouseMode.PARALLEL_DRAW_WIDTH_51);
            put(ActionType.continuousSymmetricDrawAction, MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52);
            put(ActionType.lengthenCrease2Action, MouseMode.LENGTHEN_CREASE_SAME_COLOR_70);
            put(ActionType.foldableLineDrawAction, MouseMode.FOLDABLE_LINE_DRAW_71);
            put(ActionType.regularPolygonAction, MouseMode.POLYGON_SET_NO_CORNERS_29);
            put(ActionType.axiom5Action, MouseMode.AXIOM_5);
            put(ActionType.axiom6Action, MouseMode.AXIOM_6);
            put(ActionType.axiom7Action, MouseMode.AXIOM_7);
        }};

        for (Map.Entry<ActionType, MouseMode> entry : mouseModeWithAfterColorAndUnselectActions.entrySet()) {
            actionService.registerAction(entry.getKey(), actionFactory.setMouseModeWithAfterColorAndUnselectAction(entry.getValue()));
        }

        // |---------------------------------------------------------------------------|
        // Categorized by sections in each panel (improvements towards such are appreciated)
        // |---------------------------------------------------------------------------|
        // --- Left Panel ---
        // - line actions
        actionService.registerAction(ActionType.colRedAction, new LambdaAction(() -> canvasModel.setLineColor(LineColor.RED_1)));
        actionService.registerAction(ActionType.colBlueAction, new LambdaAction(() -> canvasModel.setLineColor(LineColor.BLUE_2)));
        actionService.registerAction(ActionType.colBlackAction, new LambdaAction(() -> canvasModel.setLineColor(LineColor.BLACK_0)));
        actionService.registerAction(ActionType.colCyanAction, new LambdaAction(() -> canvasModel.setLineColor(LineColor.CYAN_3)));
        actionService.registerAction(ActionType.antiAliasToggleAction, new LambdaAction(applicationModel::toggleAntiAlias));
        actionService.registerAction(ActionType.lineWidthDecreaseAction, new LambdaAction(applicationModel::decreaseLineWidth));
        actionService.registerAction(ActionType.lineWidthIncreaseAction, new LambdaAction(applicationModel::increaseLineWidth));
        actionService.registerAction(ActionType.pointSizeDecreaseAction, new LambdaAction(applicationModel::decreasePointSize));
        actionService.registerAction(ActionType.pointSizeIncreaseAction, new LambdaAction(applicationModel::increasePointSize));
        actionService.registerAction(ActionType.lineStyleChangeAction, new LambdaAction(applicationModel::advanceLineStyle));

        // - select and transform actions
        actionService.registerAction(ActionType.selectAllAction, new LambdaAction(mainCreasePatternWorker::select_all));
        actionService.registerAction(ActionType.unselectAllAction, new LambdaAction(mainCreasePatternWorker::unselect_all));
        actionService.registerAction(ActionType.moveAction, actionFactory.selectionOperationAction(CanvasModel.SelectionOperationMode.MOVE_1, MouseMode.CREASE_MOVE_21));
        actionService.registerAction(ActionType.move2p2pAction, actionFactory.selectionOperationAction(CanvasModel.SelectionOperationMode.MOVE4P_2, MouseMode.CREASE_MOVE_4P_31));
        actionService.registerAction(ActionType.copyAction, actionFactory.selectionOperationAction(CanvasModel.SelectionOperationMode.COPY_3, MouseMode.CREASE_COPY_22));
        actionService.registerAction(ActionType.copy2p2pAction, actionFactory.selectionOperationAction(CanvasModel.SelectionOperationMode.COPY4P_4, MouseMode.CREASE_COPY_4P_32));
        actionService.registerAction(ActionType.reflectAction, actionFactory.selectionOperationAction(CanvasModel.SelectionOperationMode.MIRROR_5, MouseMode.DRAW_CREASE_SYMMETRIC_12));
        actionService.registerAction(ActionType.deleteSelectedLineSegmentAction, new LambdaAction(() -> {
            mainCreasePatternWorker.del_selected_senbun();
            mainCreasePatternWorker.record();
        }));

        // - line edit actions
        actionService.registerAction(ActionType.v_del_allAction, new LambdaAction(mainCreasePatternWorker::v_del_all));
        actionService.registerAction(ActionType.v_del_all_ccAction, new LambdaAction(mainCreasePatternWorker::v_del_all_cc));
        actionService.registerAction(ActionType.zen_yama_tani_henkanAction, new LambdaAction(() -> {
            mainCreasePatternWorker.allMountainValleyChange();
            mainCreasePatternWorker.unselect_all(false);
        }));
        actionService.registerAction(ActionType.lineSegmentDeleteAction, actionFactory.setMouseModeLineTypeDeleteAction(MouseMode.LINE_SEGMENT_DELETE_3, FoldLineAdditionalInputMode.POLY_LINE_0));
        actionService.registerAction(ActionType.edgeLineSegmentDeleteAction, actionFactory.setMouseModeLineTypeDeleteAction(MouseMode.LINE_SEGMENT_DELETE_3, FoldLineAdditionalInputMode.BLACK_LINE_2));
        actionService.registerAction(ActionType.auxLiveLineSegmentDeleteAction, actionFactory.setMouseModeLineTypeDeleteAction(MouseMode.LINE_SEGMENT_DELETE_3, FoldLineAdditionalInputMode.AUX_LIVE_LINE_3));
        actionService.registerAction(ActionType.in_L_col_changeAction, new LambdaAction(() -> {
            canvasModel.setMouseMode(MouseMode.CREASE_MAKE_MV_34);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.CREASE_MAKE_MV_34);

            if (canvasModel.getLineColor() == LineColor.BLACK_0) {
                canvasModel.setLineColor(LineColor.RED_1);
            }

            mainCreasePatternWorker.unselect_all(false);
        }));
        actionService.registerAction(ActionType.on_L_col_changeAction, new LambdaAction(() -> {
            canvasModel.setMouseMode(MouseMode.CREASES_ALTERNATE_MV_36);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.CREASES_ALTERNATE_MV_36);

            if (canvasModel.getLineColor() == LineColor.BLACK_0) {
                canvasModel.setLineColor(LineColor.BLUE_2);
            }

            mainCreasePatternWorker.unselect_all(false);
        }));
        actionService.registerAction(ActionType.trimBranchesAction, new LambdaAction(() -> {
            mainCreasePatternWorker.point_removal();
            mainCreasePatternWorker.overlapping_line_removal();
            mainCreasePatternWorker.branch_trim();
            mainCreasePatternWorker.organizeCircles();
            mainCreasePatternWorker.record();
            mainCreasePatternWorker.unselect_all(false);
        }));
        actionService.registerAction(ActionType.switchReplaceAction, new LambdaAction(() -> {
            CustomLineTypes temp = applicationModel.getCustomFromLineType();

            applicationModel.setCustomFromLineType(applicationModel.getCustomToLineType());
            applicationModel.setCustomToLineType(temp);
        }));

        // - grid actions
        actionService.registerAction(ActionType.gridSizeIncreaseAction, new LambdaAction(() -> gridModel.setGridSize(gridModel.getGridSize() * 2)));
        actionService.registerAction(ActionType.gridSizeDecreaseAction, new LambdaAction(() -> gridModel.setGridSize(Math.max(gridModel.getGridSize() / 2, 1))));
        actionService.registerAction(ActionType.changeGridStateAction, new LambdaAction(gridModel::advanceBaseState));
        actionService.registerAction(ActionType.gridLineWidthDecreaseAction, new LambdaAction(applicationModel::decreaseGridLineWidth));
        actionService.registerAction(ActionType.gridLineWidthIncreaseAction, new LambdaAction(applicationModel::increaseGridLineWidth));
        actionService.registerAction(ActionType.moveIntervalGridVerticalAction, new LambdaAction(gridModel::changeHorizontalScalePosition));
        actionService.registerAction(ActionType.moveIntervalGridHorizontalAction, new LambdaAction(gridModel::changeVerticalScalePosition));
        actionService.registerAction(ActionType.gridConfigureResetAction, new LambdaAction(gridModel::reset));
        actionService.registerAction(ActionType.gridColorAction, new LambdaAction(() -> {
            //以下にやりたいことを書く
            Color color = JColorChooser.showDialog(frameProvider.get(), "Col", FlatLaf.isLafDark() ? Colors.GRID_LINE_DARK : Colors.GRID_LINE);
            if (color != null) {
                applicationModel.setGridColor(color);
            }
            //以上でやりたいことは書き終わり
        }));
        actionService.registerAction(ActionType.intervalGridColorAction, new LambdaAction(() -> {
            Color color = JColorChooser.showDialog(frameProvider.get(), "Col", FlatLaf.isLafDark() ? Colors.GRID_SCALE_DARK : Colors.GRID_SCALE);
            if (color != null) {
                applicationModel.setGridScaleColor(color);
            }
        }));

        // - other actions
        actionService.registerAction(ActionType.suitei_01Action, actionFactory.suiteiAction(FoldedFigure.EstimationOrder.ORDER_1));
        actionService.registerAction(ActionType.suitei_02Action, actionFactory.suiteiAction(FoldedFigure.EstimationOrder.ORDER_2));
        actionService.registerAction(ActionType.suitei_03Action, actionFactory.suiteiAction(FoldedFigure.EstimationOrder.ORDER_3));
        actionService.registerAction(ActionType.drawTwoColoredCpAction, new LambdaAction(foldingService::createTwoColoredCp));
        actionService.registerAction(ActionType.coloredXRayDecreaseAction, new LambdaAction(foldedFigureModel::decreaseTransparency));
        actionService.registerAction(ActionType.coloredXRayIncreaseAction, new LambdaAction(foldedFigureModel::increaseTransparency));
        actionService.registerAction(ActionType.koteimen_siteiAction, new LambdaAction(() -> {
            FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();

            if (selectedFigure != null && selectedFigure.getFoldedFigure().displayStyle != FoldedFigure.DisplayStyle.NONE_0) {
                canvasModel.setMouseMode(MouseMode.CHANGE_STANDARD_FACE_103);
            }
        }));

        // |---------------------------------------------------------------------------|
        // --- Top Panel ---
        // - transform CP(crease pattern) actions
        actionService.registerAction(ActionType.rotateClockwiseAction, new LambdaAction(cameraModel::decreaseRotation));
        actionService.registerAction(ActionType.rotateAnticlockwiseAction, new LambdaAction(cameraModel::increaseRotation));

        // - background actions
        actionService.registerAction(ActionType.transparentAction, new LambdaAction(canvas::createTransparentBackground));
        actionService.registerAction(ActionType.backgroundLockAction, new LambdaAction(() -> backgroundModel.setLockBackground(!backgroundModel.isLockBackground())));
        actionService.registerAction(ActionType.backgroundToggleAction, new LambdaAction(() -> backgroundModel.setDisplayBackground(!backgroundModel.isDisplayBackground())));

        // |---------------------------------------------------------------------------|
        // --- Right panel ---
        // - validation actions
        actionService.registerAction(ActionType.ck4_colorDecreaseAction, new LambdaAction(mainCreasePatternWorker::lightenCheck4Color));
        actionService.registerAction(ActionType.ck4_colorIncreaseAction, new LambdaAction(mainCreasePatternWorker::darkenCheck4Color));
        actionService.registerAction(ActionType.ckTAction, e -> {
            boolean isEnabled = applicationModel.getCkTEnabled();
            applicationModel.setCkTEnabled(!isEnabled);

            if (isEnabled) {
                mainCreasePatternWorker.check2();//r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度
                mainCreasePatternWorker.setCheck2(true);
            } else {
                mainCreasePatternWorker.setCheck2(false);
            }

            mainCreasePatternWorker.unselect_all();
        });
        actionService.registerAction(ActionType.fxOAction, new LambdaAction(() -> {
            mainCreasePatternWorker.unselect_all();
            mainCreasePatternWorker.fix1();
            mainCreasePatternWorker.check1();
        }));
        actionService.registerAction(ActionType.fxTAction, new LambdaAction(() -> {
            mainCreasePatternWorker.unselect_all();
            mainCreasePatternWorker.fix2();
            mainCreasePatternWorker.check2();
        }));
        actionService.registerAction(ActionType.cAMVAction, e -> {
            applicationModel.setCheck4Enabled(!applicationModel.getCheck4Enabled());
            mainCreasePatternWorker.unselect_all();
            buttonService.Button_shared_operation();
        });
        actionService.registerAction(ActionType.ckOAction, e -> {
            mainCreasePatternWorker.unselect_all();
            boolean isEnabled = applicationModel.getCkOEnabled();
            applicationModel.setCkOEnabled(!isEnabled);

            if (isEnabled) {
                mainCreasePatternWorker.check1();//r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度
                mainCreasePatternWorker.set_i_check1(true);
            } else {
                mainCreasePatternWorker.set_i_check1(false);
            }
        });

        // - angle system actions
        actionService.registerAction(ActionType.angleSystemADecreaseAction, new LambdaAction(angleSystemModel::decreaseAngleSystemA));
        actionService.registerAction(ActionType.angleSystemAAction, new LambdaAction(() -> angleSystemModel.setCurrentAngleSystemDivider(angleSystemModel.getAngleSystemADivider())));
        actionService.registerAction(ActionType.angleSystemAIncreaseAction, new LambdaAction(angleSystemModel::increaseAngleSystemA));
        actionService.registerAction(ActionType.angleSystemBDecreaseAction, new LambdaAction(angleSystemModel::decreaseAngleSystemB));
        actionService.registerAction(ActionType.angleSystemBAction, new LambdaAction(() -> angleSystemModel.setCurrentAngleSystemDivider(angleSystemModel.getAngleSystemBDivider())));
        actionService.registerAction(ActionType.angleSystemBIncreaseAction, new LambdaAction(angleSystemModel::increaseAngleSystemB));
        actionService.registerAction(ActionType.deg1Action, actionFactory.degAction(MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_13, AngleSystemModel.AngleSystemInputType.DEG_1));
        actionService.registerAction(ActionType.deg2Action, actionFactory.degAction(MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_5_37, AngleSystemModel.AngleSystemInputType.DEG_5));
        actionService.registerAction(ActionType.deg3Action, actionFactory.degAction(MouseMode.ANGLE_SYSTEM_16, AngleSystemModel.AngleSystemInputType.DEG_2));
        actionService.registerAction(ActionType.deg4Action, actionFactory.degAction(MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_18, AngleSystemModel.AngleSystemInputType.DEG_4));

        // - other aux actions
        actionService.registerAction(ActionType.colOrangeAction, new LambdaAction(() -> canvasModel.setAuxLiveLineColor(LineColor.ORANGE_4)));
        actionService.registerAction(ActionType.colYellowAction, new LambdaAction(() -> canvasModel.setAuxLiveLineColor(LineColor.YELLOW_7)));
        actionService.registerAction(ActionType.h_senhaba_sageAction, new LambdaAction(applicationModel::decreaseAuxLineWidth));
        actionService.registerAction(ActionType.h_senhaba_ageAction, new LambdaAction(applicationModel::increaseAuxLineWidth));
        actionService.registerAction(ActionType.h_undoAction, new LambdaAction(mainCreasePatternWorker::auxUndo));
        actionService.registerAction(ActionType.h_redoAction, new LambdaAction(mainCreasePatternWorker::auxRedo));
        actionService.registerAction(ActionType.h_senbun_nyuryokuAction, actionFactory.setMouseModeLineTypeDeleteAction(MouseMode.DRAW_CREASE_FREE_1, FoldLineAdditionalInputMode.AUX_LINE_1));
        actionService.registerAction(ActionType.h_senbun_sakujyoAction, actionFactory.setMouseModeLineTypeDeleteAction(MouseMode.LINE_SEGMENT_DELETE_3, FoldLineAdditionalInputMode.AUX_LINE_1));

        // |---------------------------------------------------------------------------|
        // --- Bottom Panel ---
        // foldedFigure actions
        actionService.registerAction(ActionType.foldedFigureToggleAntiAliasAction, new LambdaAction(foldedFigureModel::toggleAntiAlias));
        actionService.registerAction(ActionType.foldedFigureToggleShadowAction, new LambdaAction(foldedFigureModel::toggleDisplayShadows));
        actionService.registerAction(ActionType.foldedFigureSizeIncreaseAction, new LambdaAction(() -> {
            animationService.animate(Animations.ZOOM_FOLDED_MODEL,
                    foldedFigureModel::setScale,
                    foldedFigureModel::getScale,
                    scale -> foldedFigureModel.getScaleForZoomBy(-1, applicationModel.getZoomSpeed(), scale),
                    AnimationDurations.ZOOM);
        }));
        actionService.registerAction(ActionType.foldedFigureSizeDecreaseAction, new LambdaAction(() -> {
            animationService.animate(Animations.ZOOM_FOLDED_MODEL,
                    foldedFigureModel::setScale,
                    foldedFigureModel::getScale,
                    scale -> foldedFigureModel.getScaleForZoomBy(1, applicationModel.getZoomSpeed(), scale),
                    AnimationDurations.ZOOM);
        }));
        actionService.registerAction(ActionType.foldedFigureRotateClockwiseAction, new LambdaAction(() -> {
            double rotation = foldedFigureModel.getState() == FoldedFigure.State.BACK_1 ? foldedFigureModel.getRotation() + 11.25 : foldedFigureModel.getRotation() - 11.25;
            foldedFigureModel.setRotation(OritaCalc.angle_between_m180_180(rotation));
        }));
        actionService.registerAction(ActionType.foldedFigureRotateAntiClockwiseAction, new LambdaAction(() -> {
            double rotation = foldedFigureModel.getState() != FoldedFigure.State.BACK_1 ? foldedFigureModel.getRotation() + 11.25 : foldedFigureModel.getRotation() - 11.25;
            foldedFigureModel.setRotation(OritaCalc.angle_between_m180_180(rotation));
        }));
        actionService.registerAction(ActionType.oriagari_sousaAction, actionFactory.oriagari_sousaAction(FoldedFigureOperationMode.MODE_1));
        actionService.registerAction(ActionType.oriagari_sousa_2Action, actionFactory.oriagari_sousaAction(FoldedFigureOperationMode.MODE_2));

        // |---------------------------------------------------------------------------|
        // --- AppMenuBar ---
        actionService.registerAction(ActionType.IMPORT, new LambdaAction(fileSaveService::importFile));
        actionService.registerAction(ActionType.toggleHelpAction, new LambdaAction(applicationModel::toggleHelpVisible));

        // --- others ---
        actionService.registerAction(ActionType.scaleAction, new LambdaAction(() -> {
            animationService.animate(Animations.ZOOM_FOLDED_MODEL,
                    foldedFigureModel::setScale,
                    foldedFigureModel::getScale,
                    1.0,
                    AnimationDurations.SCALE_SPEED);
        }));
        actionService.registerAction(ActionType.selectAnd3ClickAction, new LambdaAction(() -> {
            canvasModel.setCkbox_add_frame_SelectAnd3click_isSelected(canvasModel.isCkbox_add_frame_SelectAnd3click_isSelected());
        }));
    }

}
