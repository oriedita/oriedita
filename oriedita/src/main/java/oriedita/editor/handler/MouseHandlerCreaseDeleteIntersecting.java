package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.handler.step.StepFactory;
import oriedita.editor.handler.step.StepGraph;
import oriedita.editor.handler.step.StepMouseHandler;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;

import java.util.EnumSet;

@ApplicationScoped
@Handles(MouseMode.CREASE_DELETE_INTERSECTING_65)
public class MouseHandlerCreaseDeleteIntersecting extends StepMouseHandler<MouseHandlerCreaseDeleteIntersecting.Step> {
    private final CanvasModel canvasModel;

    public enum Step {
        SELECT_LINE
    }

    @Inject
    public MouseHandlerCreaseDeleteIntersecting(CanvasModel canvasModel) {
        super();
        this.canvasModel = canvasModel;
    }

    @Override
    public EnumSet<MouseHandlerSettingGroup> getSettings() {
        return EnumSet.of(MouseHandlerSettingGroup.ERASER_COLOR,
                super.getSettings().toArray(new MouseHandlerSettingGroup[0]));
    }

    @Override
    protected StepGraph<Step> initStepGraph(StepFactory stepFactory) {
        var graph = new StepGraph<>(Step.SELECT_LINE);
        graph.addNode(stepFactory.createSelectIntersectingLinesNode(Step.SELECT_LINE, LineColor.PURPLE_8,
            lines -> {
                if (!lines.isEmpty()) {
                    //やりたい動作はここに書く
                    for (LineSegment l : lines) {
                        d.getFoldLineSet().deleteLine(l);//lXは小文字のエルと大文字のエックス
                    }
                    d.record();
                }
                return Step.SELECT_LINE;
            }, (p) -> {}, (ls) -> {},
                ls -> canvasModel.getDelLineType().matches(ls.getColor())));
        return graph;
    }

    //----------------------------------------------------------------------------------------
//多角形を入力(既存頂点への引き寄せあるが既存頂点が遠い場合は引き寄せ無し)し、何らかの作業を行うセット
    //マウス操作(マウスを動かしたとき)を行う関数

}
