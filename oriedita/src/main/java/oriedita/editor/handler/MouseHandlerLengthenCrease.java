package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.tinylog.Logger;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.folding.util.SortingBox;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@ApplicationScoped
@Handles(MouseMode.LENGTHEN_CREASE_5)
public class MouseHandlerLengthenCrease extends BaseMouseHandler {
    SortingBox<LineSegment> linesToExtendSortingBox = new SortingBox<>();
    List<LineSegment> linesToExtend = new ArrayList<>();
    LineSegment selectionLine;
    Point extensionPoint;
    Step currentStep = Step.START;
    private enum Step {
        START,
        DRAW_SELECTION_LINE,
        DRAW_EXTENSION_POINT
    }

    @Override
    public void reset() {
        super.reset();
        linesToExtend.clear();
        selectionLine = null;
        extensionPoint = null;
        currentStep = Step.START;
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        LineSegment extensionLine = extensionPoint == null?
                null : new LineSegment(extensionPoint, extensionPoint, LineColor.MAGENTA_5, LineSegment.ActiveState.ACTIVE_B_2);
        Stream.concat(
                Stream.of(selectionLine, extensionLine),
                linesToExtend.stream())
                .filter(Objects::nonNull)
                .forEach(l -> DrawingUtil.drawLineStep(g2, l, camera, settings.getLineWidth(), d.getGridInputAssist()));
    }

    @Inject
    public MouseHandlerLengthenCrease() {
    }

    //5 5 5 5 5 55555555555555555    mouseMode==5　;線分延長モード
    //マウス操作(マウスを動かしたとき)を行う関数    //Logger.info("_");
    public void mouseMoved(Point p0) {
        //マウスで選択できる候補点を表示する。常にマウスの位置自身が候補点となる。
        if (d.getGridInputAssist()) {
            Point p = d.getCamera().TV2object(p0);

            d.getLineCandidate().clear();
            d.getLineCandidate().add(new LineSegment(p, p, d.getLineColor()));
        }
    }//常にマウスの位置のみが候補点

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);

        d.getLineCandidate().clear();

        if (currentStep == Step.START) {
            linesToExtendSortingBox.reset();
            selectionLine = new LineSegment(p, p, LineColor.MAGENTA_5, LineSegment.ActiveState.ACTIVE_BOTH_3);
            currentStep = Step.DRAW_SELECTION_LINE;
        } else if (currentStep == Step.DRAW_EXTENSION_POINT) {
            extensionPoint = p;
        }

    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        if (currentStep == Step.DRAW_SELECTION_LINE) {
            selectionLine = selectionLine.withB(p);
        } else if (currentStep == Step.DRAW_EXTENSION_POINT) {
            extensionPoint = p;
        }
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        LineSegment closestLineSegment = d.getClosestLineSegment(p);
        FoldLineSet lineSet = d.getFoldLineSet();
        if (currentStep == Step.DRAW_SELECTION_LINE) {
            selectionLine = selectionLine.withB(p);

            for (int i = 1; i <= lineSet.getTotal(); i++) {
                LineSegment s = lineSet.get(i);
                LineSegment.Intersection i_lineSegment_intersection_decision =
                        OritaCalc.determineLineSegmentIntersection(s, selectionLine, Epsilon.UNKNOWN_1EN4);
                boolean lineIntersectsSelectionLine =
                        i_lineSegment_intersection_decision == LineSegment.Intersection.INTERSECTS_1;

                if (lineIntersectsSelectionLine) {
                    linesToExtendSortingBox.addByWeight(s,
                            OritaCalc.distance(selectionLine.getA(), OritaCalc.findIntersection(s, selectionLine)));
                }
            }

            if ((linesToExtendSortingBox.getTotal() == 0) && (selectionLine.determineLength() <= Epsilon.UNKNOWN_1EN6)) {//延長する候補になる折線を選ぶために描いた線分s_step[1]が点状のときの処理
                if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance()) {
                    //entyou_kouho_nboxに1本の情報しか入らないのでdoubleの部分はどうでもよいので適当に1.0にした。
                    LineSegment closestLine = lineSet.closestLineSegmentSearch(p);
                    linesToExtendSortingBox.addByWeight(closestLine, 1.0);
                    Point newp = OritaCalc.findProjection(closestLine, selectionLine.getB());
                    if (OritaCalc.determineLineSegmentDistance(newp, closestLine) > Epsilon.UNKNOWN_1EN6) {
                        newp = closestLine.determineClosestEndpoint(newp);
                    }
                    selectionLine = selectionLine.withCoordinates(newp, newp);
                }
            }

            Logger.info(" entyou_kouho_nbox.getsousuu() = " + linesToExtendSortingBox.getTotal());

            if (linesToExtendSortingBox.getTotal() == 0) {
                reset();
                return;
            }
            for (int i = 1; i <= linesToExtendSortingBox.getTotal(); i++) {
                LineSegment s = new LineSegment(linesToExtendSortingBox.getValue(i));
                s.setColor(LineColor.GREEN_6);
                s.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);

                linesToExtend.add(s);
            }
            currentStep = Step.DRAW_EXTENSION_POINT;
            return;
        }


        if (currentStep == Step.DRAW_EXTENSION_POINT) {
            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) >= d.getSelectionDistance()) {
                reset();
                return;
            }
            //最初に選んだ延長候補線分群中に2番目に選んだ線分と等しいものがあるかどうかを判断する。
            boolean i_senbun_entyou_mode = false;// i_senbun_entyou_mode=0なら最初に選んだ延長候補線分群中に2番目に選んだ線分と等しいものがない。1ならある。
            for (int i = 1; i <= linesToExtendSortingBox.getTotal(); i++) {
                if (OritaCalc.determineLineSegmentIntersection(linesToExtendSortingBox.getValue(i), closestLineSegment, Epsilon.UNKNOWN_1EN6) == LineSegment.Intersection.PARALLEL_EQUAL_31) {//線分が同じならoc.senbun_kousa_hantei==31
                    i_senbun_entyou_mode = true;
                }
            }


            LineSegment addLineSegment;
            //最初に選んだ延長候補線分群中に2番目に選んだ線分と等しいものがない場合
            if (!i_senbun_entyou_mode) {
                int sousuu_old = lineSet.getTotal();//(1)
                for (int i = 1; i <= linesToExtendSortingBox.getTotal(); i++) {
                    //最初に選んだ線分と2番目に選んだ線分が平行でない場合
                    LineSegment s = linesToExtendSortingBox.getValue(i);
                    if (OritaCalc.isLineSegmentParallel(s, closestLineSegment, Epsilon.UNKNOWN_1EN6) == OritaCalc.ParallelJudgement.NOT_PARALLEL) { //２つの線分が平行かどうかを判定する関数。oc.heikou_hantei(Tyokusen t1,Tyokusen t2)//0=平行でない
                        //line_step[1]とs_step[2]の交点はoc.kouten_motome(Senbun s1,Senbun s2)で求める//２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
                        Point kousa_point = OritaCalc.findIntersection(s, closestLineSegment);
                        addLineSegment = new LineSegment(
                                kousa_point, s.determineClosestEndpoint(kousa_point));

                        addExtendedLineSegment(lineSet, addLineSegment, s);
                    }
                }
                lineSet.applyLineSegmentCircleIntersection(sousuu_old, lineSet.getTotal(), 0, lineSet.numCircles() - 1);//(3)


            } else {
                //最初に選んだ延長候補線分群中に2番目に選んだ線分と等しいものがある場合

                int sousuu_old = lineSet.getTotal();//(1)
                for (int i = 1; i <= linesToExtendSortingBox.getTotal(); i++) {
                    LineSegment lineToExtend = new LineSegment(linesToExtendSortingBox.getValue(i));
                    Point p_point = OritaCalc.findIntersection(lineToExtend, selectionLine);

                    if (p_point.distance(lineToExtend.getA()) < p_point.distance(lineToExtend.getB())) {
                        lineToExtend.a_b_swap();
                    }
                    addLineSegment = OritaCalc.extendToIntersectionPoint_2(lineSet, lineToExtend);
                    addExtendedLineSegment(lineSet, addLineSegment, lineToExtend);
                }
                lineSet.applyLineSegmentCircleIntersection(sousuu_old, lineSet.getTotal(), 0, lineSet.numCircles() - 1);//(3)
            }

            d.record();

            reset();

        }
    }

    private void addExtendedLineSegment(FoldLineSet lineSet, LineSegment addLineSegment, LineSegment original) {
        if (Epsilon.high.gt0(addLineSegment.determineLength())) {
            if (getMouseMode() == MouseMode.LENGTHEN_CREASE_5) {
                addLineSegment.setColor(d.getLineColor());
            }
            if (getMouseMode() == MouseMode.LENGTHEN_CREASE_SAME_COLOR_70) {
                addLineSegment.setColor(original.getColor());
            }

            lineSet.addLine(addLineSegment);//ori_sのsenbunの最後にs0の情報をを加えるだけ//(2)
            lineSet.divideLineSegmentWithNewLines(lineSet.getTotal()-1, lineSet.getTotal());//(4)
        }
    }
}
