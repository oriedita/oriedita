package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.handler.step.StepFactory;
import oriedita.editor.handler.step.StepGraph;
import oriedita.editor.handler.step.StepMouseHandler;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveProvider;
import origami.Epsilon;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.CREASE_MOVE_21)
public class MouseHandlerCreaseMove extends StepMouseHandler<MouseHandlerCreaseMove.Step> {
    public enum Step {
        SELECT_LINE
    }
    @Inject
    public MouseHandlerCreaseMove(@Named("mainCreasePattern_Worker") CreasePattern_Worker d) {
        super();
        this.d = d;
    }

    @Override
    protected StepGraph<Step> initStepGraph(StepFactory stepFactory) {
        var graph = new StepGraph<>(Step.SELECT_LINE);
        graph.addNode(stepFactory.createCachedLineTransformStepNode(Step.SELECT_LINE, LineColor.PURPLE_8,
            delta -> {
                if (Epsilon.high.gt0(delta.distance(new Point(0, 0)))) {
                    //やりたい動作はここに書く

                    FoldLineSet ori_s_temp = new FoldLineSet();    //セレクトされた折線だけ取り出すために使う
                    Save save = SaveProvider.createInstance();
                    d.getFoldLineSet().getMemoSelectOption(save, 2);
                    ori_s_temp.setSave(save);//セレクトされた折線だけ取り出してori_s_tempを作る
                    d.getFoldLineSet().delSelectedLineSegmentFast();//セレクトされた折線を削除する。
                    ori_s_temp.move(delta.getX(), delta.getY());//全体を移動する
                    int total_old = d.getFoldLineSet().getTotal();
                    Save save1 = SaveProvider.createInstance();
                    ori_s_temp.getSave(save1);
                    d.getFoldLineSet().addSave(save1);
                    int total_new = d.getFoldLineSet().getTotal();
                    d.getFoldLineSet().divideLineSegmentWithNewLines(total_old, total_new);

                    d.unselect_all(false);
                    d.record();
                }
                return Step.SELECT_LINE;
        }));
        return graph;
    }
}
