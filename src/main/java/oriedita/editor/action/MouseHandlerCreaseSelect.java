package oriedita.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;
import origami.Epsilon;
import origami.crease_pattern.element.Point;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.canvas.CreasePattern_Worker;

@Singleton
public class MouseHandlerCreaseSelect extends BaseMouseHandlerBoxSelect {
    private final MouseHandlerCreaseMove4p mouseHandlerCreaseMove4p;
    private final MouseHandlerCreaseCopy4p mouseHandlerCreaseCopy4p;
    private final MouseHandlerCreaseCopy mouseHandlerCreaseCopy;
    private final MouseHandlerCreaseMove mouseHandlerCreaseMove;
    private final MouseHandlerDrawCreaseSymmetric mouseHandlerDrawCreaseSymmetric;
    private final CreasePattern_Worker d;

    @Inject
    public MouseHandlerCreaseSelect(
            CreasePattern_Worker d,
            MouseHandlerCreaseMove4p mouseHandlerCreaseMove4p,
            MouseHandlerCreaseCopy4p mouseHandlerCreaseCopy4p,
            MouseHandlerCreaseMove mouseHandlerCreaseMove,
            MouseHandlerCreaseCopy mouseHandlerCreaseCopy,
            MouseHandlerDrawCreaseSymmetric mouseHandlerDrawCreaseSymmetric) {
        this.d = d;
        this.mouseHandlerCreaseMove4p = mouseHandlerCreaseMove4p;
        this.mouseHandlerCreaseCopy4p = mouseHandlerCreaseCopy4p;
        this.mouseHandlerCreaseMove = mouseHandlerCreaseMove;
        this.mouseHandlerCreaseCopy = mouseHandlerCreaseCopy;
        this.mouseHandlerDrawCreaseSymmetric = mouseHandlerDrawCreaseSymmetric;
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_SELECT_19;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==19  select　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        System.out.println("19  select_");
        System.out.println("i_egaki_dankai=" + d.lineStep.size());

        Point p = new Point();

        if (d.lineStep.size() == 0) {//i_select_modeを決める
            p.set(d.camera.TV2object(p0));
        }

        switch (d.i_select_mode) {
            case NORMAL_0:
                super.mousePressed(p0);
                break;
            case MOVE_1:
                mouseHandlerCreaseMove.mousePressed(p0);//move
                break;
            case MOVE4P_2:
                mouseHandlerCreaseMove4p.mousePressed(p0);//move 2p2p
                break;
            case COPY_3:
                mouseHandlerCreaseCopy.mousePressed(p0);//copy
                break;
            case COPY4P_4:
                mouseHandlerCreaseCopy4p.mousePressed(p0);//copy 2p2p
                break;
            case MIRROR_5:
                mouseHandlerDrawCreaseSymmetric.mousePressed(p0);//鏡映
                break;
        }
    }

//20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20

    //マウス操作(mouseMode==19 select　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        //mDragged_A_box_select( p0);
        switch (d.i_select_mode) {
            case NORMAL_0:
                super.mouseDragged(p0);
                break;
            case MOVE_1:
                mouseHandlerCreaseMove.mouseDragged(p0);//move
                break;
            case MOVE4P_2:
                mouseHandlerCreaseMove4p.mouseDragged(p0);//move 2p2p
                break;
            case COPY_3:
                mouseHandlerCreaseCopy.mouseDragged(p0);//copy
                break;
            case COPY4P_4:
                mouseHandlerCreaseCopy4p.mouseDragged(p0);//copy 2p2p
                break;
            case MIRROR_5:
                mouseHandlerDrawCreaseSymmetric.mouseDragged(p0);//鏡映
                break;
        }
    }

    //マウス操作(mouseMode==19 select　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        switch (d.i_select_mode) {
            case NORMAL_0:
                mReleased_A_box_select(p0);
                break;
            case MOVE_1:
                mouseHandlerCreaseMove.mouseReleased(p0);//move
                break;
            case MOVE4P_2:
                mouseHandlerCreaseMove4p.mouseReleased(p0);//move 2p2p
                break;
            case COPY_3:
                mouseHandlerCreaseCopy.mouseReleased(p0);//copy
                break;
            case COPY4P_4:
                mouseHandlerCreaseCopy4p.mouseReleased(p0);//copy 2p2p
                break;
            case MIRROR_5:
                mouseHandlerDrawCreaseSymmetric.mouseReleased(p0);//鏡映
                break;
        }
    }

    public void mReleased_A_box_select(Point p0) {
        d.lineStep.clear();

        d.select(selectionStart, p0);
        if (selectionStart.distance(p0) <= Epsilon.UNKNOWN_1EN6) {
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            if (d.foldLineSet.closestLineSegmentDistance(p) < d.selectionDistance) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                d.foldLineSet.closestLineSegmentSearch(p).setSelected(2);
            }
        }
    }
}
