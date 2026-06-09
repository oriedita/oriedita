package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.handler.step.ObjCoordStepNode;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveProvider;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;


@ApplicationScoped
@Handles(MouseMode.CREASE_COPY_22)
public class MouseHandlerCreaseCopy extends BaseMouseHandlerLineTransform<MouseHandlerCreaseCopy.Step> {

    enum Step {
        CLICK_DRAG_OR_SELECT_POINT,
        SELECT_POINT
    }

    @Inject
    public MouseHandlerCreaseCopy(AngleSystemModel angleSystemModel) {
        super(Step.CLICK_DRAG_OR_SELECT_POINT, angleSystemModel);
        steps.addNode(ObjCoordStepNode.createNode(Step.CLICK_DRAG_OR_SELECT_POINT,
                this::move_click_drag_point,
                (p) -> {
                }, this::drag_click_drag_point, this::release_click_drag_point));
        steps.addNode(ObjCoordStepNode.createNode_MD_R(Step.SELECT_POINT,
                this::click_select_2,
                this::release_select_2));
    }

    @Override
    protected Step release_click_drag_point(Point p) {
        if (p1 == null || p2 == null)
            return Step.CLICK_DRAG_OR_SELECT_POINT;

        if (p.distance(p1) < d.getSelectionDistance())
            return Step.SELECT_POINT;

        doAction();
        d.getFoldLineSet().divideLineSegmentWithNewLines(total_old, total_new);
        d.record();
        d.check4();
        return Step.CLICK_DRAG_OR_SELECT_POINT;
    }

    @Override
    protected Step release_select_2(Point p) {
        if (p2 != null) {

            l1 = new LineSegment(p1, p2, LineColor.GREEN_6);
            if(snapping)
                snapLine();

            delta = new Point(
                    l1.determineBX() - l1.determineAX(),
                    l1.determineBY() - l1.determineAY()
            );
            doAction();
            d.getFoldLineSet().divideLineSegmentWithNewLines(total_old, total_new);
            d.record();
            d.check4();
            return Step.CLICK_DRAG_OR_SELECT_POINT;
        }
        return Step.SELECT_POINT;
    }

    @Override
    protected void doAction() {
        ori_s_temp.setSave(save);//セレクトされた折線だけ取り出してori_s_tempを作る
        ori_s_temp.move(delta.getX(), delta.getY());//全体を移動する
        ori_s_temp.unselect_all();
        total_old = d.getFoldLineSet().getTotal();
        Save save1 = SaveProvider.createInstance();
        ori_s_temp.getSave(save1);
        d.getFoldLineSet().addSave(save1);
        total_new = d.getFoldLineSet().getTotal();
    }
}
