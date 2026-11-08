package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.handler.step.ObjCoordStepNode;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveProvider;


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

        enum_step_first = Step.CLICK_DRAG_OR_SELECT_POINT;
        enum_step_second = Step.SELECT_POINT;

        steps.addNode(ObjCoordStepNode.createNode(enum_step_first,
                this::first_click_hover,
                (p) -> {
                }, this::first_click_drag, this::first_click_release));
        steps.addNode(ObjCoordStepNode.createNode_MD_R(enum_step_second,
                this::further_click_hover,
                this::further_click_release));
    }

    @Override
    protected void doAction() {
        fls_selected.setSave(save);//セレクトされた折線だけ取り出してori_s_tempを作る
        fls_selected.move(delta.getX(), delta.getY());//全体を移動する
        fls_selected.unselect_all();
        int total_old = d.getFoldLineSet().getTotal();
        Save save1 = SaveProvider.createInstance();
        fls_selected.getSave(save1);
        d.getFoldLineSet().addSave(save1);
        int total_new = d.getFoldLineSet().getTotal();
        d.getFoldLineSet().divideLineSegmentWithNewLines(total_old, total_new);
    }
}
