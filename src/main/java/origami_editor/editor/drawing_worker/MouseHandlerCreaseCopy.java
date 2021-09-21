package origami_editor.editor.drawing_worker;

import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;
import origami_editor.editor.databinding.CanvasModel;

public class MouseHandlerCreaseCopy extends BaseMouseHandler{
    public MouseHandlerCreaseCopy(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_COPY_22;
    }

    //マウスを動かしたとき
    public void mouseMoved(Point p0) {
        d.mMoved_m_00b(p0, LineColor.MAGENTA_5);
    }//マウスで選択できる候補点を表示する。近くに既成の点があるときはその点、無いときはマウスの位置自身が候補点となる。

    //マウスクリック----------------------------------------------------
    public void mousePressed(Point p0) {
        d.mPressed_m_00b(p0, LineColor.MAGENTA_5);
    }

    //マウスドラッグ----------------------------------------------------
    public void mouseDragged(Point p0) {
        d.mDragged_m_00b(p0, LineColor.MAGENTA_5);
    }


//--------------------------------------------
//31 31 31 31 31 31 31 31  mouseMode==31move2p2p	入力 31 31 31 31 31 31 31 31

//動作概要　
//mouseMode==1と線分分割以外は同じ　
//

    //マウスリリース----------------------------------------------------
    public void mouseReleased(Point p0) {
        d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。

        d.i_drawing_stage = 0;
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.line_step[1].setA(p);
        d.closest_point.set(d.getClosestPoint(p));
        if (p.distance(d.closest_point) <= d.selectionDistance) {
            d.line_step[1].setA(d.closest_point);
        }
        if (d.line_step[1].getLength() > 0.00000001) {
            //やりたい動作はここに書く

            double addx, addy;
            addx = -d.line_step[1].getBX() + d.line_step[1].getAX();
            addy = -d.line_step[1].getBY() + d.line_step[1].getAY();

            FoldLineSet ori_s_temp = new FoldLineSet();    //セレクトされた折線だけ取り出すために使う
            ori_s_temp.setSave(d.foldLineSet.getMemoSelectOption(2));//セレクトされた折線だけ取り出してori_s_tempを作る
            ori_s_temp.move(addx, addy);//全体を移動する

            int sousuu_old = d.foldLineSet.getTotal();
            d.foldLineSet.addSave(ori_s_temp.getSave());
            int sousuu_new = d.foldLineSet.getTotal();
            d.foldLineSet.intersect_divide(1, sousuu_old, sousuu_old + 1, sousuu_new);

            d.foldLineSet.unselect_all();
            d.record();

            d.app.canvasModel.setMouseMode(MouseMode.CREASE_SELECT_19);
        }
    }
}
