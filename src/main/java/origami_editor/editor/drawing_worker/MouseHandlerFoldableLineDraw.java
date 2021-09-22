package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;
import origami_editor.sortingbox.SortingBox;
import origami_editor.sortingbox.WeightedValue;

public class MouseHandlerFoldableLineDraw extends BaseMouseHandler{
    private final MouseHandlerDrawCreaseFree mouseHandlerDrawCreaseFree;
    private final MouseHandlerVertexMakeAngularlyFlatFoldable mouseHandlerVertexMakeAngularlyFlatFoldable;
    MouseMode operationMode = MouseMode.UNUSED_0;
    boolean operationModeChangeable = false;
    Point moyori_point_memo = new Point();

    public MouseHandlerFoldableLineDraw(DrawingWorker d) {
        super(d);
        this.mouseHandlerDrawCreaseFree = new MouseHandlerDrawCreaseFree(d);
        this.mouseHandlerVertexMakeAngularlyFlatFoldable = new MouseHandlerVertexMakeAngularlyFlatFoldable(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.FOLDABLE_LINE_DRAW_71;
    }

    public void mouseMoved(Point p0) {
        if (d.i_drawing_stage == 0) {
            operationMode = MouseMode.UNUSED_0;
            mouseHandlerDrawCreaseFree.mouseMoved(p0);
            return;
        }

        if (operationMode == MouseMode.DRAW_CREASE_FREE_1) {
            mouseHandlerDrawCreaseFree.mouseMoved(p0);
        }
        if (operationMode == MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38) {
            mouseHandlerVertexMakeAngularlyFlatFoldable.mouseMoved(p0);
        }
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        operationModeChangeable = false;
Point p = new Point();
        p.set(d.camera.TV2object(p0));
        double hantei_kyori = 0.000001;

        if (p.distance(moyori_point_memo) <= d.selectionDistance) {
            d.i_drawing_stage = 0;
        }

        if (d.i_drawing_stage == 0) {
            //任意の点が与えられたとき、端点もしくは格子点で最も近い点を得る
            d.closest_point.set(d.getClosestPoint(p));
            moyori_point_memo.set(d.closest_point);

            if (p.distance(d.closest_point) > d.selectionDistance) {
                d.closest_point.set(p);
            }

            //moyori_tenを端点とする折線をNarabebakoに入れる
            SortingBox<LineSegment> nbox = new SortingBox<>();
            for (int i = 1; i <= d.foldLineSet.getTotal(); i++) {
                LineSegment s = d.foldLineSet.get(i);
                if (s.getColor().isFoldingLine()) {
                    if (d.closest_point.distance(s.getA()) < hantei_kyori) {
                        nbox.container_i_smallest_first(new WeightedValue<>(s, OritaCalc.angle(s.getA(), s.getB())));
                    } else if (d.closest_point.distance(s.getB()) < hantei_kyori) {
                        nbox.container_i_smallest_first(new WeightedValue<>(s, OritaCalc.angle(s.getB(), s.getA())));
                    }
                }
            }
            if (nbox.getTotal() % 2 == 0) {
                operationMode = MouseMode.DRAW_CREASE_FREE_1;
                d.i_foldLine_additional = FoldLineAdditionalInputMode.POLY_LINE_0;
            }//When the number of polygonal lines with moyori_ten as the end point is an even number, the processing inside if {} is performed.
            if (nbox.getTotal() % 2 == 1) {
                operationMode = MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38;
                operationModeChangeable = true;
            }//moyori_tenを端点とする折線の数が奇数のときif{}内の処理をする
        }

        if (operationMode == MouseMode.DRAW_CREASE_FREE_1) {
            mouseHandlerDrawCreaseFree.mousePressed(p0);
        }
        if (operationMode == MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38) {
            mouseHandlerVertexMakeAngularlyFlatFoldable.mousePressed(p0);
            if (!mouseHandlerVertexMakeAngularlyFlatFoldable.workDone) {
                if (d.i_drawing_stage == 0) {
                    mousePressed(p0);
                }
            }
        }


    }

    //マウス操作(ドラッグしたとき)を行う関数20200
    public void mouseDragged(Point p0) {
        if ((operationMode == MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38) && operationModeChangeable) {
            d.p.set(d.camera.TV2object(p0));
            moyori_point_memo.set(d.closest_point);
            if (d.p.distance(moyori_point_memo) > d.selectionDistance) {
                operationMode = MouseMode.DRAW_CREASE_FREE_1;
                d.i_drawing_stage = 1;
                d.line_step[1].a_b_swap();
                d.line_step[1].setColor(d.lineColor);
                operationModeChangeable = false;
            }

        }

        if (operationMode == MouseMode.DRAW_CREASE_FREE_1) {
            mouseHandlerDrawCreaseFree.mouseDragged(p0);
        }
        if (operationMode == MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38) {
            mouseHandlerVertexMakeAngularlyFlatFoldable.mouseDragged(p0);
        }
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (operationMode == MouseMode.DRAW_CREASE_FREE_1) {
            mouseHandlerDrawCreaseFree.mouseReleased(p0);
        }
        if (operationMode == MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38) {
            mouseHandlerVertexMakeAngularlyFlatFoldable.mouseReleased(p0);
        }
    }
}
