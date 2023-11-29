package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.FoldLineAdditionalInputMode;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.folding.util.SortingBox;

@ApplicationScoped
@Handles(MouseMode.FOLDABLE_LINE_DRAW_71)
public class MouseHandlerFoldableLineDraw extends BaseMouseHandler {
    private final MouseHandlerDrawCreaseFree mouseHandlerDrawCreaseFree;
    private final MouseHandlerVertexMakeAngularlyFlatFoldable mouseHandlerVertexMakeAngularlyFlatFoldable;
    MouseMode operationMode = null;
    boolean operationModeChangeable = false;
    Point moyori_point_memo = new Point();
    Point closest_point;

    @Inject
    public MouseHandlerFoldableLineDraw(
            @Handles(MouseMode.DRAW_CREASE_FREE_1) MouseHandlerDrawCreaseFree mouseHandlerDrawCreaseFree,
            @Handles(MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38) MouseHandlerVertexMakeAngularlyFlatFoldable mouseHandlerVertexMakeAngularlyFlatFoldable
    ) {
        this.mouseHandlerDrawCreaseFree = mouseHandlerDrawCreaseFree;
        this.mouseHandlerVertexMakeAngularlyFlatFoldable = mouseHandlerVertexMakeAngularlyFlatFoldable;
    }

    public void mouseMoved(Point p0) {
        if (d.getLineStep().size() == 0) {
            operationMode = null;
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
        Point p = d.getCamera().TV2object(p0);
        double decision_distance = Epsilon.UNKNOWN_1EN6;

        if (p.distance(moyori_point_memo) <= d.getSelectionDistance()) {
            d.getLineStep().clear();
        }

        if (d.getLineStep().size() == 0) {
            //任意の点が与えられたとき、端点もしくは格子点で最も近い点を得る
            closest_point = d.getClosestPoint(p);
            moyori_point_memo = closest_point;

            if (p.distance(closest_point) > d.getSelectionDistance()) {
                closest_point = p;
            }

            //moyori_tenを端点とする折線をNarabebakoに入れる
            SortingBox<LineSegment> nbox = new SortingBox<>();
            for (int i = 1; i <= d.getFoldLineSet().getTotal(); i++) {
                LineSegment s = d.getFoldLineSet().get(i);
                if (s.getColor().isFoldingLine()) {
                    if (closest_point.distance(s.getA()) < decision_distance) {
                        nbox.addByWeight(s, OritaCalc.angle(s.getA(), s.getB()));
                    } else if (closest_point.distance(s.getB()) < decision_distance) {
                        nbox.addByWeight(s, OritaCalc.angle(s.getB(), s.getA()));
                    }
                }
            }
            if (nbox.getTotal() % 2 == 0) {
                operationMode = MouseMode.DRAW_CREASE_FREE_1;
                d.setFoldLineAdditional(FoldLineAdditionalInputMode.POLY_LINE_0);
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
            if (!mouseHandlerVertexMakeAngularlyFlatFoldable.isWorkDone()) {
                if (d.getLineStep().size() == 0) {
                    mousePressed(p0);
                }
            }
        }


    }

    //マウス操作(ドラッグしたとき)を行う関数20200
    public void mouseDragged(Point p0) {
        if ((operationMode == MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38) && operationModeChangeable) {
            Point p = d.getCamera().TV2object(p0);
            moyori_point_memo = closest_point;
            if (p.distance(moyori_point_memo) > d.getSelectionDistance()) {
                operationMode = MouseMode.DRAW_CREASE_FREE_1;
                d.getLineStep().set(0, d.getLineStep().get(0).withSwappedCoordinates());
                d.getLineStep().get(0).setColor(d.getLineColor());
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
