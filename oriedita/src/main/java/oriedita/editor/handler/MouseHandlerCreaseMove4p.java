package oriedita.editor.handler;

import java.awt.Graphics2D;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveProvider;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;

enum CreaseMove4pStep {
    SELECT_2_ORIGINAL_POINTS,
    SELECT_2_TARGET_POINTS,
}

@ApplicationScoped
@Handles(MouseMode.CREASE_MOVE_4P_31)
public class MouseHandlerCreaseMove4p extends StepMouseHandler<CreaseMove4pStep> {
    @Inject
    private CanvasModel canvasModel;

    private Point originalPoint1, originalPoint2, targetPoint1, targetPoint2;
    private boolean isFirstSelected;

    @Inject
    public MouseHandlerCreaseMove4p() {
        super(CreaseMove4pStep.SELECT_2_ORIGINAL_POINTS);
        steps.addNode(StepNode.createNode_MD_R(CreaseMove4pStep.SELECT_2_ORIGINAL_POINTS,
                this::move_drag_select_2_original_points, this::release_select_2_original_points));
        steps.addNode(StepNode.createNode_MD_R(CreaseMove4pStep.SELECT_2_TARGET_POINTS,
                this::move_drag_select_2_target_points, this::release_select_2_target_points));
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, originalPoint1, LineColor.MAGENTA_5, camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, originalPoint2, LineColor.BLUE_2, camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, targetPoint1, LineColor.CYAN_3, camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, targetPoint2, LineColor.ORANGE_4, camera, d.getGridInputAssist());
    }

    @Override
    public void reset() {
        resetStep();
        originalPoint1 = null;
        originalPoint2 = null;
        targetPoint1 = null;
        targetPoint2 = null;
        isFirstSelected = false;
    }

    // Select 2 original points
    private void move_drag_select_2_original_points(Point p) {
        Point tmpPoint = d.getClosestPoint(p);
        if (!isFirstSelected) {
            originalPoint1 = p;
            if (p.distance(tmpPoint) < d.getSelectionDistance()) {
                originalPoint1 = tmpPoint;
            }
        } else {
            originalPoint2 = p;
            if (!originalPoint1.equals(tmpPoint) && p.distance(tmpPoint) < d.getSelectionDistance()) {
                originalPoint2 = tmpPoint;
            }
        }
    }

    private CreaseMove4pStep release_select_2_original_points(Point p) {
        if (originalPoint1.distance(d.getClosestPoint(originalPoint1)) > d.getSelectionDistance()) {
            originalPoint1 = null;
            return CreaseMove4pStep.SELECT_2_ORIGINAL_POINTS;
        }
        if (!isFirstSelected) {
            isFirstSelected = true;
            return CreaseMove4pStep.SELECT_2_ORIGINAL_POINTS;
        }
        if (originalPoint2.distance(originalPoint1) < d.getSelectionDistance()
                || originalPoint2.distance(d.getClosestPoint(originalPoint2)) > d.getSelectionDistance()) {
            originalPoint2 = null;
            return CreaseMove4pStep.SELECT_2_ORIGINAL_POINTS;
        }
        isFirstSelected = false;
        return CreaseMove4pStep.SELECT_2_TARGET_POINTS;
    }

    // Select 2 target points
    private void move_drag_select_2_target_points(Point p) {
        Point tmpPoint = d.getClosestPoint(p);
        if (!isFirstSelected) {
            targetPoint1 = p;
            if (p.distance(tmpPoint) < d.getSelectionDistance()) {
                targetPoint1 = tmpPoint;
            }
        } else {
            targetPoint2 = p;
            if (!targetPoint1.equals(tmpPoint) || p.distance(tmpPoint) < d.getSelectionDistance()) {
                targetPoint2 = tmpPoint;
            }
        }
    }

    private CreaseMove4pStep release_select_2_target_points(Point p) {
        if (targetPoint1.distance(d.getClosestPoint(targetPoint1)) > d.getSelectionDistance()) {
            targetPoint1 = null;
            return CreaseMove4pStep.SELECT_2_TARGET_POINTS;
        }
        if (!isFirstSelected) {
            isFirstSelected = true;
            return CreaseMove4pStep.SELECT_2_TARGET_POINTS;
        }
        if (targetPoint2.distance(targetPoint1) < d.getSelectionDistance()
                || targetPoint2.distance(d.getClosestPoint(targetPoint2)) > d.getSelectionDistance()) {
            targetPoint2 = null;
            return CreaseMove4pStep.SELECT_2_TARGET_POINTS;
        }

        canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);// <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。

        FoldLineSet ori_s_temp = new FoldLineSet(); // セレクトされた折線だけ取り出すために使う
        Save save = SaveProvider.createInstance();
        d.getFoldLineSet().getMemoSelectOption(save, 2);
        ori_s_temp.setSave(save);// セレクトされた折線だけ取り出してori_s_tempを作る
        d.getFoldLineSet().delSelectedLineSegmentFast();// セレクトされた折線を削除する。
        ori_s_temp.move(originalPoint1, originalPoint2, targetPoint1, targetPoint2);// 全体を移動する

        int sousuu_old = d.getFoldLineSet().getTotal();
        Save save1 = SaveProvider.createInstance();
        ori_s_temp.getSave(save1);
        d.getFoldLineSet().addSave(save1);
        int sousuu_new = d.getFoldLineSet().getTotal();
        d.getFoldLineSet().divideLineSegmentWithNewLines(sousuu_old, sousuu_new);

        d.unselect_all(false);
        reset();
        return CreaseMove4pStep.SELECT_2_ORIGINAL_POINTS;
    }
}
