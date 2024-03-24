package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.folding.util.SortingBox;

@ApplicationScoped
@Handles(MouseMode.FLAT_FOLDABLE_CHECK_63)
public class MouseHandlerFlatFoldableCheck extends BaseMouseHandler {
    boolean i_O_F_C = false;

    @Inject
    public MouseHandlerFlatFoldableCheck() {
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
    }


//lineColor=3 cyan
//lineColor=4 orange
//lineColor=5 mazenta
//lineColor=6 green
//lineColor=7 yellow

    //マウス操作(mouseMode==63　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        if (d.getLineStep().size() == 0) {
            i_O_F_C = false;

            Point p = d.getCamera().TV2object(p0);
            d.lineStepAdd(new LineSegment(p, p, LineColor.YELLOW_7));
        } else {
            if (!i_O_F_C) {
                Point p = d.getCamera().TV2object(p0);
                d.lineStepAdd(new LineSegment(d.getLineStep().get(d.getLineStep().size() - 1).getB(), p, LineColor.YELLOW_7));
            }
        }

    }


    //マウス操作(mouseMode==63　でドラッグしたとき)を行う関数----------------------------------------------------

    public void mouseDragged(Point p0) {
        if (!i_O_F_C) {
            Point p = d.getCamera().TV2object(p0);
            d.getLineStep().set(d.getLineStep().size() - 1, d.getLineStep().get(d.getLineStep().size() - 1).withB(p));
        }
    }

    //マウス操作(mouseMode==63　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {


        if (!i_O_F_C) {
            Point p = d.getCamera().TV2object(p0);
            d.getLineStep().set(d.getLineStep().size() - 1, d.getLineStep().get(d.getLineStep().size() - 1).withB(p));


            if (p.distance(d.getLineStep().get(0).getA()) <= d.getSelectionDistance()) {
                d.getLineStep().set(d.getLineStep().size() - 1,
                        d.getLineStep().get(d.getLineStep().size() - 1).withB(d.getLineStep().get(0).getA()));
                i_O_F_C = true;
            }


            if (i_O_F_C) {
                if (d.getLineStep().size() == 2) {
                    d.getLineStep().clear();
                }
            }
        }

        int i_tekisetu = 1;//外周部の黄色い線と外周部の全折線の交差が適切（全てX型の交差）なら1、1つでも適切でないなら0
        if (i_O_F_C) {
            SortingBox<LineSegment> goukei_nbox = new SortingBox<>();
            SortingBox<LineSegment> nbox = new SortingBox<>();
            for (LineSegment s2 : d.getLineStep()) {
                nbox.reset();
                for (var s : d.getFoldLineSet().getLineSegmentsIterator()) {
                    LineSegment.Intersection i_senbun_kousa_hantei = OritaCalc.determineLineSegmentIntersection(s, s2, Epsilon.UNKNOWN_1EN4);
                    int i_jikkou = 0;

                    if ((i_senbun_kousa_hantei != LineSegment.Intersection.NO_INTERSECTION_0) && (i_senbun_kousa_hantei != LineSegment.Intersection.INTERSECTS_1)) {
                        i_tekisetu = 0;
                    }

                    if (i_senbun_kousa_hantei == LineSegment.Intersection.INTERSECTS_1) {
                        i_jikkou = 1;
                    }

                    if (s.getColor().getNumber() >= 3) {
                        i_jikkou = 0;
                    }


                    if (i_jikkou == 1) {
                        nbox.addByWeight(s, OritaCalc.distance(s2.getA(), OritaCalc.findIntersection(s, s2)));
                    }
                }


                for (int i = 1; i <= nbox.getTotal(); i++) {
                    goukei_nbox.addByWeight(nbox.getValue(i), goukei_nbox.getTotal());
                }
            }

            if (i_tekisetu == 1) {

                LineColor i_hantai_color;//判定結果を表す色番号。5（マゼンタ、赤紫）は折畳不可。3（シアン、水色）は折畳可。

                if (goukei_nbox.getTotal() % 2 != 0) {//外周部として選択した折線の数が奇数
                    i_hantai_color = LineColor.MAGENTA_5;
                } else if (goukei_nbox.getTotal() == 0) {//外周部として選択した折線の数が0
                    i_hantai_color = LineColor.CYAN_3;
                } else {//外周部として選択した折線の数が偶数
                    LineSegment s_idou = new LineSegment(goukei_nbox.getValue(1));

                    for (int i = 2; i <= goukei_nbox.getTotal(); i++) {
                        s_idou = OritaCalc.findLineSymmetryLineSegment(s_idou, goukei_nbox.getValue(i));
                    }
                    i_hantai_color = LineColor.MAGENTA_5;
                    if (OritaCalc.equal(goukei_nbox.getValue(1).getA(), s_idou.getA(), Epsilon.UNKNOWN_1EN4)) {
                        if (OritaCalc.equal(goukei_nbox.getValue(1).getB(), s_idou.getB(), Epsilon.UNKNOWN_1EN4)) {
                            i_hantai_color = LineColor.CYAN_3;
                        }
                    }
                }


                for (LineSegment s : d.getLineStep()) {
                    s.setColor(i_hantai_color);
                }
            }
        }
    }
}
