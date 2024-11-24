package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tinylog.Logger;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import origami.Epsilon;
import origami.crease_pattern.element.Point;

import java.awt.event.MouseEvent;

@ApplicationScoped
@Handles(MouseMode.CREASE_SELECT_19)
public class MouseHandlerCreaseSelect extends BaseMouseHandlerBoxSelect {
    private final MouseHandlerCreaseMove4p mouseHandlerCreaseMove4p;
    private final MouseHandlerCreaseCopy4p mouseHandlerCreaseCopy4p;
    private final MouseHandlerCreaseCopy mouseHandlerCreaseCopy;
    private final MouseHandlerCreaseMove mouseHandlerCreaseMove;
    private final MouseHandlerDrawCreaseSymmetric mouseHandlerDrawCreaseSymmetric;
    private final CanvasModel canvasModel;
    private final CreasePattern_Worker d;
    private boolean tripleClick;

    @Inject
    public MouseHandlerCreaseSelect(
            @Named("mainCreasePattern_Worker") CreasePattern_Worker d,
            CanvasModel canvasModel,
            @Handles(MouseMode.CREASE_MOVE_4P_31) MouseHandlerCreaseMove4p mouseHandlerCreaseMove4p,
            @Handles(MouseMode.CREASE_COPY_4P_32) MouseHandlerCreaseCopy4p mouseHandlerCreaseCopy4p,
            @Handles(MouseMode.CREASE_MOVE_21) MouseHandlerCreaseMove mouseHandlerCreaseMove,
            @Handles(MouseMode.CREASE_COPY_22) MouseHandlerCreaseCopy mouseHandlerCreaseCopy,
            @Handles(MouseMode.DRAW_CREASE_SYMMETRIC_12) MouseHandlerDrawCreaseSymmetric mouseHandlerDrawCreaseSymmetric) {
        this.d = d;
        this.canvasModel = canvasModel;
        this.mouseHandlerCreaseMove4p = mouseHandlerCreaseMove4p;
        this.mouseHandlerCreaseCopy4p = mouseHandlerCreaseCopy4p;
        this.mouseHandlerCreaseMove = mouseHandlerCreaseMove;
        this.mouseHandlerCreaseCopy = mouseHandlerCreaseCopy;
        this.mouseHandlerDrawCreaseSymmetric = mouseHandlerDrawCreaseSymmetric;
        tripleClick = false;
    }

    @Override
    public void reset() {
        super.reset();
        tripleClick = false;
    }

    @Override
    public void mousePressed(Point p0, MouseEvent e, int pressedButton) {
        if (e.getClickCount() == 3 && canvasModel.isCkbox_add_frame_SelectAnd3click_isSelected()) {
            Logger.info("3_Click");//("トリプルクリック"
            tripleClick = true;
            switch (canvasModel.getSelectionOperationMode()) {
                case MOVE_1 -> canvasModel.setMouseMode(MouseMode.CREASE_MOVE_21);
                case MOVE4P_2 -> canvasModel.setMouseMode(MouseMode.CREASE_MOVE_4P_31);
                case COPY_3 -> canvasModel.setMouseMode(MouseMode.CREASE_COPY_22);
                case COPY4P_4 -> canvasModel.setMouseMode(MouseMode.CREASE_COPY_4P_32);
                case MIRROR_5 -> canvasModel.setMouseMode(MouseMode.DRAW_CREASE_SYMMETRIC_12);
                default -> {}
            }
        } else {
            tripleClick = false;
            mousePressed(p0);
        }
    }

    //マウス操作(mouseMode==19  select　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Logger.info("19  select_");
        Logger.info("i_egaki_dankai=" + d.getLineStep().size());

        if (!tripleClick) {
            super.mousePressed(p0);
            return;
        }
        switch (d.getI_select_mode()) {
            case NORMAL_0 -> super.mousePressed(p0);
            case MOVE_1 -> mouseHandlerCreaseMove.mousePressed(p0);//move
            case MOVE4P_2 -> mouseHandlerCreaseMove4p.mousePressed(p0);//move 2p2p
            case COPY_3 -> mouseHandlerCreaseCopy.mousePressed(p0);//copy
            case COPY4P_4 -> mouseHandlerCreaseCopy4p.mousePressed(p0);//copy 2p2p
            case MIRROR_5 -> mouseHandlerDrawCreaseSymmetric.mousePressed(p0);//鏡映
        }
    }

//20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20

    //マウス操作(mouseMode==19 select　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        if (!tripleClick) {
            super.mouseDragged(p0);
            return;
        }

        switch (d.getI_select_mode()) {
            case NORMAL_0 -> super.mouseDragged(p0);
            case MOVE_1 -> mouseHandlerCreaseMove.mouseDragged(p0);//move
            case MOVE4P_2 -> mouseHandlerCreaseMove4p.mouseDragged(p0);//move 2p2p
            case COPY_3 -> mouseHandlerCreaseCopy.mouseDragged(p0);//copy
            case COPY4P_4 -> mouseHandlerCreaseCopy4p.mouseDragged(p0);//copy 2p2p
            case MIRROR_5 -> mouseHandlerDrawCreaseSymmetric.mouseDragged(p0);//鏡映
        }
    }

    //マウス操作(mouseMode==19 select　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {

        if (!tripleClick) {
            mReleased_A_box_select(p0);
            return;
        }
        switch (d.getI_select_mode()) {
            case NORMAL_0 -> mReleased_A_box_select(p0);
            case MOVE_1 -> mouseHandlerCreaseMove.mouseReleased(p0);//move
            case MOVE4P_2 -> mouseHandlerCreaseMove4p.mouseReleased(p0);//move 2p2p
            case COPY_3 -> mouseHandlerCreaseCopy.mouseReleased(p0);//copy
            case COPY4P_4 -> mouseHandlerCreaseCopy4p.mouseReleased(p0);//copy 2p2p
            case MIRROR_5 -> mouseHandlerDrawCreaseSymmetric.mouseReleased(p0);//鏡映
        }
        tripleClick = false;
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
