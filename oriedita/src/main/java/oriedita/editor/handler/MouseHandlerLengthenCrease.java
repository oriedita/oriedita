package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.tinylog.Logger;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.folding.util.SortingBox;

@ApplicationScoped
@Handles(MouseMode.LENGTHEN_CREASE_5)
public class MouseHandlerLengthenCrease extends BaseMouseHandler {
    SortingBox<LineSegment> entyou_kouho_nbox = new SortingBox<>();

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

        if (d.getLineStep().size() == 0) {
            entyou_kouho_nbox.reset();

            d.lineStepAdd(new LineSegment(p, p, LineColor.MAGENTA_5));
        } else if (d.getLineStep().size() >= 2) {
            d.lineStepAdd(new LineSegment(p, p, LineColor.MAGENTA_5));
        }

    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        if (d.getLineStep().size() == 1) {
            d.getLineStep().get(0).setB(p);
        }
        if (d.getLineStep().size() > 1) {
            d.getLineStep().get(d.getLineStep().size() - 1).set(p, p);
        }
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        LineSegment closestLineSegment = new LineSegment(d.getClosestLineSegment(p));

        if (d.getLineStep().size() == 1) {
            d.getLineStep().get(0).setB(p);

            for (int i = 1; i <= d.getFoldLineSet().getTotal(); i++) {
                LineSegment s = d.getFoldLineSet().get(i);
                LineSegment.Intersection i_lineSegment_intersection_decision = OritaCalc.determineLineSegmentIntersection(s, d.getLineStep().get(0), Epsilon.UNKNOWN_1EN4);
                boolean i_jikkou = i_lineSegment_intersection_decision == LineSegment.Intersection.INTERSECTS_1;

                if (i_jikkou) {
                    entyou_kouho_nbox.addByWeight(s, OritaCalc.distance(d.getLineStep().get(0).getA(), OritaCalc.findIntersection(s, d.getLineStep().get(0))));
                }
            }

            if ((entyou_kouho_nbox.getTotal() == 0) && (d.getLineStep().get(0).determineLength() <= Epsilon.UNKNOWN_1EN6)) {//延長する候補になる折線を選ぶために描いた線分s_step[1]が点状のときの処理
                if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance()) {
                    //entyou_kouho_nboxに1本の情報しか入らないのでdoubleの部分はどうでもよいので適当に1.0にした。
                    entyou_kouho_nbox.addByWeight(d.getFoldLineSet().closestLineSegmentSearch(p), 1.0);

                    d.getLineStep().get(0).setB(OritaCalc.findLineSymmetryPoint(closestLineSegment.getA(), closestLineSegment.getB(), p));

                    d.getLineStep().get(0).set(//lineStep.get(0)を短くして、表示時に目立たない様にする。
                            OritaCalc.point_double(OritaCalc.midPoint(d.getLineStep().get(0).getA(), d.getLineStep().get(0).getB()), d.getLineStep().get(0).getA(), Epsilon.UNKNOWN_1EN5 / d.getLineStep().get(0).determineLength())
                            ,
                            OritaCalc.point_double(OritaCalc.midPoint(d.getLineStep().get(0).getA(), d.getLineStep().get(0).getB()), d.getLineStep().get(0).getB(), Epsilon.UNKNOWN_1EN5 / d.getLineStep().get(0).determineLength())
                    );

                }

            }

            Logger.info(" entyou_kouho_nbox.getsousuu() = " + entyou_kouho_nbox.getTotal());


            if (entyou_kouho_nbox.getTotal() == 0) {
                d.getLineStep().clear();
                return;
            }
            if (entyou_kouho_nbox.getTotal() >= 0) {
                for (int i = 2; i <= entyou_kouho_nbox.getTotal() + 1; i++) {
                    LineSegment s = new LineSegment(entyou_kouho_nbox.getValue(i - 1));
                    s.setColor(LineColor.GREEN_6);

                    d.lineStepAdd(s);
                }
                return;
            }
            return;
        }


        if (d.getLineStep().size() >= 3) {
            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) >= d.getSelectionDistance()) {
                d.getLineStep().clear();
                return;
            }

            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance()) {


                //最初に選んだ延長候補線分群中に2番目に選んだ線分と等しいものがあるかどうかを判断する。
                boolean i_senbun_entyou_mode = false;// i_senbun_entyou_mode=0なら最初に選んだ延長候補線分群中に2番目に選んだ線分と等しいものがない。1ならある。
                for (int i = 1; i <= entyou_kouho_nbox.getTotal(); i++) {
                    if (OritaCalc.determineLineSegmentIntersection(entyou_kouho_nbox.getValue(i), closestLineSegment, Epsilon.UNKNOWN_1EN6) == LineSegment.Intersection.PARALLEL_EQUAL_31) {//線分が同じならoc.senbun_kousa_hantei==31
                        i_senbun_entyou_mode = true;
                    }
                }


                LineSegment addLineSegment = new LineSegment();
                //最初に選んだ延長候補線分群中に2番目に選んだ線分と等しいものがない場合
                if (!i_senbun_entyou_mode) {
                    int sousuu_old = d.getFoldLineSet().getTotal();//(1)
                    for (int i = 1; i <= entyou_kouho_nbox.getTotal(); i++) {
                        //最初に選んだ線分と2番目に選んだ線分が平行でない場合
                        if (OritaCalc.isLineSegmentParallel(entyou_kouho_nbox.getValue(i), closestLineSegment, Epsilon.UNKNOWN_1EN6) == OritaCalc.ParallelJudgement.NOT_PARALLEL) { //２つの線分が平行かどうかを判定する関数。oc.heikou_hantei(Tyokusen t1,Tyokusen t2)//0=平行でない
                            //line_step[1]とs_step[2]の交点はoc.kouten_motome(Senbun s1,Senbun s2)で求める//２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
                            Point kousa_point = OritaCalc.findIntersection(entyou_kouho_nbox.getValue(i), closestLineSegment);
                            //addLineSegment =new Senbun(kousa_ten,foldLineSet.get(entyou_kouho_nbox.get_int(i)).get_tikai_hasi(kousa_ten));
                            addLineSegment.setA(kousa_point);
                            addLineSegment.setB(entyou_kouho_nbox.getValue(i).determineClosestEndpoint(kousa_point));


                            if (Epsilon.high.gt0(addLineSegment.determineLength())) {
                                if (getMouseMode() == MouseMode.LENGTHEN_CREASE_5) {
                                    addLineSegment.setColor(d.getLineColor());
                                }
                                if (getMouseMode() == MouseMode.LENGTHEN_CREASE_SAME_COLOR_70) {
                                    addLineSegment.setColor(entyou_kouho_nbox.getValue(i).getColor());
                                }

                                //addsenbun(addLineSegment);
                                d.getFoldLineSet().addLine(addLineSegment);//ori_sのsenbunの最後にs0の情報をを加えるだけ//(2)
                            }
                        }
                    }
                    d.getFoldLineSet().applyLineSegmentCircleIntersection(sousuu_old, d.getFoldLineSet().getTotal(), 0, d.getFoldLineSet().numCircles() - 1);//(3)
                    d.getFoldLineSet().divideLineSegmentWithNewLines(sousuu_old, d.getFoldLineSet().getTotal());//(4)


                } else {
                    //最初に選んだ延長候補線分群中に2番目に選んだ線分と等しいものがある場合

                    int sousuu_old = d.getFoldLineSet().getTotal();//(1)
                    for (int i = 1; i <= entyou_kouho_nbox.getTotal(); i++) {
                        LineSegment moto_no_sen = new LineSegment(entyou_kouho_nbox.getValue(i));
                        Point p_point = OritaCalc.findIntersection(moto_no_sen, d.getLineStep().get(0));

                        if (p_point.distance(moto_no_sen.getA()) < p_point.distance(moto_no_sen.getB())) {
                            moto_no_sen.a_b_swap();
                        }
                        addLineSegment = OritaCalc.extendToIntersectionPoint_2(d.getFoldLineSet(), moto_no_sen);


                        if (Epsilon.high.gt0(addLineSegment.determineLength())) {
                            if (getMouseMode() == MouseMode.LENGTHEN_CREASE_5) {
                                addLineSegment.setColor(d.getLineColor());
                            }
                            if (getMouseMode() == MouseMode.LENGTHEN_CREASE_SAME_COLOR_70) {
                                addLineSegment.setColor(entyou_kouho_nbox.getValue(i).getColor());
                            }

                            d.getFoldLineSet().addLine(addLineSegment);//ori_sのsenbunの最後にs0の情報をを加えるだけ//(2)
                        }

                    }
                    d.getFoldLineSet().applyLineSegmentCircleIntersection(sousuu_old, d.getFoldLineSet().getTotal(), 0, d.getFoldLineSet().numCircles() - 1);//(3)
                    d.getFoldLineSet().divideLineSegmentWithNewLines(sousuu_old, d.getFoldLineSet().getTotal());//(4)
                }

                d.record();

                d.getLineStep().clear();
            }
        }
    }
}
