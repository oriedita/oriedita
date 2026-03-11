package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.handler.step.StepFactory;
import oriedita.editor.handler.step.StepGraph;
import oriedita.editor.handler.step.StepMouseHandler;
import origami.Epsilon;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Handles(MouseMode.CREASE_DELETE_INTERSECTING_65)
public class MouseHandlerCreaseDeleteIntersecting extends StepMouseHandler<MouseHandlerCreaseDeleteIntersecting.Step> {
    public enum Step {
        SELECT_LINE
    }

    private List<LineSegment> highlightedLines = new ArrayList<>();

    @Inject
    public MouseHandlerCreaseDeleteIntersecting() {
        super();
    }

    @Override
    protected StepGraph<Step> initStepGraph(StepFactory stepFactory) {
        var graph = new StepGraph<>(Step.SELECT_LINE);
        graph.addNode(stepFactory.createDrawLineNode(Step.SELECT_LINE, LineColor.PURPLE_8,
                l -> {
                    if (Epsilon.high.gt0(l.determineLength())) {
                        //やりたい動作はここに書く
                        d.getFoldLineSet().deleteInsideLine(l, FoldLineSet.IntersectionMode.CONTAIN_OR_INTERSECT);//lXは小文字のエルと大文字のエックス
                        d.record();
                        highlightedLines.clear();
                    }
            return Step.SELECT_LINE;
                }, (p) -> {}, (ls) -> {
                if (Epsilon.high.gt0(ls.determineLength())) {
                    highlightedLines = d.getFoldLineSet().getInsideLine(ls, FoldLineSet.IntersectionMode.CONTAIN_OR_INTERSECT);
                }
                }));
        return graph;
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        for (LineSegment segment : highlightedLines) {
            DrawingUtil.drawLineStep(g2, segment, camera, settings.getLineWidth() + 1);
        }
    }

    //----------------------------------------------------------------------------------------
//多角形を入力(既存頂点への引き寄せあるが既存頂点が遠い場合は引き寄せ無し)し、何らかの作業を行うセット
    //マウス操作(マウスを動かしたとき)を行う関数

}
