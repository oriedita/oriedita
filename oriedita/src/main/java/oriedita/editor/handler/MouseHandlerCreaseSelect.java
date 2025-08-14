package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import origami.Epsilon;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.CREASE_SELECT_19)
public class MouseHandlerCreaseSelect extends BaseMouseHandlerBoxSelect {
    private final CreasePattern_Worker d;

    @Inject
    public MouseHandlerCreaseSelect(
            @Named("mainCreasePattern_Worker") CreasePattern_Worker d,) {
        this.d = d;
    }

    @Override
    public void reset() {
        super.reset();
    }
    //マウス操作(mouseMode==19 select　でボタンを離したとき)を行う関数----------------------------------------------------
    @Override
    public void mouseReleased(Point p0) {
        mReleased_A_box_select(p0);
    }

    public void mReleased_A_box_select(Point p0) {
        super.mouseReleased(p0);
        d.getLineStep().clear();

        int beforeSelectNum = d.getFoldLineTotalForSelectFolding();

        d.select(selectionStart, p0);
        if (selectionStart.distance(p0) <= Epsilon.UNKNOWN_1EN6) {
            Point p = d.getCamera().TV2object(p0);
            if (d.getFoldLineSet().closestLineSegmentDistance(p) < d.getSelectionDistance()) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                d.getFoldLineSet().closestLineSegmentSearch(p).setSelected(2);
                d.setIsSelectionEmpty(false);
            }
        }
        int afterSelectNum = d.getFoldLineTotalForSelectFolding();

        if(beforeSelectNum != afterSelectNum) d.record();
    }
}
