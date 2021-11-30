package oriedita.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.StraightLine;
import oriedita.editor.canvas.MouseMode;
import origami.folding.util.SortingBox;

@Singleton
public class MouseHandlerLengthenCrease extends BaseMouseHandler {
    private static final Logger logger = LogManager.getLogger(MouseHandlerLengthenCrease.class);
    SortingBox<LineSegment> entyou_kouho_nbox = new SortingBox<>();

    @Inject
    public MouseHandlerLengthenCrease() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.LENGTHEN_CREASE_5;
    }

    //5 5 5 5 5 55555555555555555    mouseMode==5　;線分延長モード
    //マウス操作(マウスを動かしたとき)を行う関数    //logger.info("_");
    public void mouseMoved(Point p0) {
        //マウスで選択できる候補点を表示する。常にマウスの位置自身が候補点となる。
        if (d.gridInputAssist) {
            Point p = new Point();
            p.set(d.camera.TV2object(p0));

            d.lineCandidate.clear();
            d.lineCandidate.add(new LineSegment(p, p, d.lineColor));
        }
    }//常にマウスの位置のみが候補点

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        d.lineCandidate.clear();

        if (d.lineStep.size() == 0) {
            entyou_kouho_nbox.reset();

            d.lineStepAdd(new LineSegment(p, p, LineColor.MAGENTA_5));
        } else if (d.lineStep.size() >= 2) {
            d.lineStepAdd(new LineSegment(p, p, LineColor.MAGENTA_5));
        }

    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        if (d.lineStep.size() == 1) {
            d.lineStep.get(0).setB(p);
        }
        if (d.lineStep.size() > 1) {
            d.lineStep.get(d.lineStep.size() - 1).set(p, p);
        }
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        LineSegment closestLineSegment = new LineSegment();
        closestLineSegment.set(d.getClosestLineSegment(p));

        if (d.lineStep.size() == 1) {
            d.lineStep.get(0).setB(p);

            for (int i = 1; i <= d.foldLineSet.getTotal(); i++) {
                LineSegment s = d.foldLineSet.get(i);
                LineSegment.Intersection i_lineSegment_intersection_decision = OritaCalc.determineLineSegmentIntersection(s, d.lineStep.get(0), Epsilon.UNKNOWN_1EN4);
                boolean i_jikkou = i_lineSegment_intersection_decision == LineSegment.Intersection.INTERSECTS_1;

                if (i_jikkou) {
                    entyou_kouho_nbox.addByWeight(s, OritaCalc.distance(d.lineStep.get(0).getA(), OritaCalc.findIntersection(s, d.lineStep.get(0))));
                }
            }

            if ((entyou_kouho_nbox.getTotal() == 0) && (d.lineStep.get(0).determineLength() <= Epsilon.UNKNOWN_1EN6)) {//延長する候補になる折線を選ぶために描いた線分s_step[1]が点状のときの処理
                if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.selectionDistance) {
                    //entyou_kouho_nboxに1本の情報しか入らないのでdoubleの部分はどうでもよいので適当に1.0にした。
                    entyou_kouho_nbox.addByWeight(d.foldLineSet.closestLineSegmentSearch(p), 1.0);

                    d.lineStep.get(0).setB(OritaCalc.findLineSymmetryPoint(closestLineSegment.getA(), closestLineSegment.getB(), p));

                    d.lineStep.get(0).set(//lineStep.get(0)を短くして、表示時に目立たない様にする。
                            OritaCalc.point_double(OritaCalc.midPoint(d.lineStep.get(0).getA(), d.lineStep.get(0).getB()), d.lineStep.get(0).getA(), Epsilon.UNKNOWN_1EN5 / d.lineStep.get(0).determineLength())
                            ,
                            OritaCalc.point_double(OritaCalc.midPoint(d.lineStep.get(0).getA(), d.lineStep.get(0).getB()), d.lineStep.get(0).getB(), Epsilon.UNKNOWN_1EN5 / d.lineStep.get(0).determineLength())
                    );

                }

            }

            logger.info(" entyou_kouho_nbox.getsousuu() = " + entyou_kouho_nbox.getTotal());


            if (entyou_kouho_nbox.getTotal() == 0) {
                d.lineStep.clear();
                return;
            }
            if (entyou_kouho_nbox.getTotal() >= 0) {
                for (int i = 2; i <= entyou_kouho_nbox.getTotal() + 1; i++) {
                    LineSegment s = new LineSegment();
                    s.set(entyou_kouho_nbox.getValue(i - 1));
                    s.setColor(LineColor.GREEN_6);

                    d.lineStepAdd(s);
                }
                return;
            }
            return;
        }


        if (d.lineStep.size() >= 3) {
            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) >= d.selectionDistance) {
                d.lineStep.clear();
                return;
            }

            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.selectionDistance) {


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
                    int sousuu_old = d.foldLineSet.getTotal();//(1)
                    for (int i = 1; i <= entyou_kouho_nbox.getTotal(); i++) {
                        //最初に選んだ線分と2番目に選んだ線分が平行でない場合
                        if (OritaCalc.isLineSegmentParallel(entyou_kouho_nbox.getValue(i), closestLineSegment, Epsilon.UNKNOWN_1EN6) == OritaCalc.ParallelJudgement.NOT_PARALLEL) { //２つの線分が平行かどうかを判定する関数。oc.heikou_hantei(Tyokusen t1,Tyokusen t2)//0=平行でない
                            //line_step[1]とs_step[2]の交点はoc.kouten_motome(Senbun s1,Senbun s2)で求める//２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
                            Point kousa_point = new Point();
                            kousa_point.set(OritaCalc.findIntersection(entyou_kouho_nbox.getValue(i), closestLineSegment));
                            //addLineSegment =new Senbun(kousa_ten,foldLineSet.get(entyou_kouho_nbox.get_int(i)).get_tikai_hasi(kousa_ten));
                            addLineSegment.setA(kousa_point);
                            addLineSegment.setB(entyou_kouho_nbox.getValue(i).determineClosestEndpoint(kousa_point));


                            if (Epsilon.high.gt0(addLineSegment.determineLength())) {
                                if (getMouseMode() == MouseMode.LENGTHEN_CREASE_5) {
                                    addLineSegment.setColor(d.lineColor);
                                }
                                if (getMouseMode() == MouseMode.LENGTHEN_CREASE_SAME_COLOR_70) {
                                    addLineSegment.setColor(entyou_kouho_nbox.getValue(i).getColor());
                                }

                                //addsenbun(addLineSegment);
                                d.foldLineSet.addLine(addLineSegment);//ori_sのsenbunの最後にs0の情報をを加えるだけ//(2)
                            }
                        }
                    }
                    d.foldLineSet.applyLineSegmentCircleIntersection(sousuu_old, d.foldLineSet.getTotal(), 0, d.foldLineSet.numCircles() - 1);//(3)
                    d.foldLineSet.divideLineSegmentWithNewLines(sousuu_old, d.foldLineSet.getTotal());//(4)


                } else {
                    //最初に選んだ延長候補線分群中に2番目に選んだ線分と等しいものがある場合

                    int sousuu_old = d.foldLineSet.getTotal();//(1)
                    for (int i = 1; i <= entyou_kouho_nbox.getTotal(); i++) {
                        LineSegment moto_no_sen = new LineSegment();
                        moto_no_sen.set(entyou_kouho_nbox.getValue(i));
                        Point p_point = new Point();
                        p_point.set(OritaCalc.findIntersection(moto_no_sen, d.lineStep.get(0)));

                        if (p_point.distance(moto_no_sen.getA()) < p_point.distance(moto_no_sen.getB())) {
                            moto_no_sen.a_b_swap();
                        }
                        addLineSegment.set(extendToIntersectionPoint_2(moto_no_sen));


                        if (Epsilon.high.gt0(addLineSegment.determineLength())) {
                            if (getMouseMode() == MouseMode.LENGTHEN_CREASE_5) {
                                addLineSegment.setColor(d.lineColor);
                            }
                            if (getMouseMode() == MouseMode.LENGTHEN_CREASE_SAME_COLOR_70) {
                                addLineSegment.setColor(entyou_kouho_nbox.getValue(i).getColor());
                            }

                            d.foldLineSet.addLine(addLineSegment);//ori_sのsenbunの最後にs0の情報をを加えるだけ//(2)
                        }

                    }
                    d.foldLineSet.applyLineSegmentCircleIntersection(sousuu_old, d.foldLineSet.getTotal(), 0, d.foldLineSet.numCircles() - 1);//(3)
                    d.foldLineSet.divideLineSegmentWithNewLines(sousuu_old, d.foldLineSet.getTotal());//(4)
                }

                d.record();

                d.lineStep.clear();
            }
        }


    }

    public LineSegment extendToIntersectionPoint_2(LineSegment s0) {//Extend s0 from point b in the opposite direction of a to the point where it intersects another polygonal line. Returns a new line // Returns the same line if it does not intersect another polygonal line
        LineSegment add_sen = new LineSegment();
        add_sen.set(s0);

        Point kousa_point = new Point(1000000.0, 1000000.0); //この方法だと、エラーの原因になりうる。本当なら全線分のx_max、y_max以上の点を取ればいい。今後修正予定20161120
        double kousa_point_distance = kousa_point.distance(add_sen.getA());

        StraightLine tyoku1 = new StraightLine(add_sen.getA(), add_sen.getB());
        StraightLine.Intersection i_intersection_flg;//元の線分を直線としたものと、他の線分の交差状態
        LineSegment.Intersection i_lineSegment_intersection_flg;//元の線分と、他の線分の交差状態

        logger.info("AAAAA_");
        for (int i = 1; i <= d.foldLineSet.getTotal(); i++) {
            i_intersection_flg = tyoku1.lineSegment_intersect_reverse_detail(d.foldLineSet.get(i));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。

            //i_lineSegment_intersection_flg=oc.senbun_kousa_hantei_amai( add_sen,foldLineSet.get(i),Epsilon.UNKNOWN_000001,Epsilon.UNKNOWN_000001);//20180408なぜかこの行の様にadd_senを使うと、i_senbun_kousa_flgがおかしくなる
            i_lineSegment_intersection_flg = OritaCalc.determineLineSegmentIntersectionSweet(s0, d.foldLineSet.get(i), Epsilon.UNKNOWN_1EN5, Epsilon.UNKNOWN_1EN5);//20180408なぜかこの行の様にs0のままだと、i_senbun_kousa_flgがおかしくならない。
            if (i_intersection_flg.isIntersecting()) {
                if (!i_lineSegment_intersection_flg.isEndpointIntersection()) {
                    //logger.info("i_intersection_flg = "+i_intersection_flg  +      " ; i_lineSegment_intersection_flg = "+i_lineSegment_intersection_flg);
                    kousa_point.set(OritaCalc.findIntersection(tyoku1, d.foldLineSet.get(i)));
                    if (kousa_point.distance(add_sen.getA()) > Epsilon.UNKNOWN_1EN5) {
                        if (kousa_point.distance(add_sen.getA()) < kousa_point_distance) {
                            double d_kakudo = OritaCalc.angle(add_sen.getA(), add_sen.getB(), add_sen.getA(), kousa_point);
                            if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                                //i_kouten_ari_nasi=1;
                                kousa_point_distance = kousa_point.distance(add_sen.getA());
                                add_sen.set(add_sen.getA(), kousa_point);
                            }
                        }
                    }
                }
            }

            if (i_intersection_flg == StraightLine.Intersection.INCLUDED_3) {
                if (i_lineSegment_intersection_flg != LineSegment.Intersection.PARALLEL_EQUAL_31) {

                    logger.info("i_intersection_flg = " + i_intersection_flg + " ; i_lineSegment_intersection_flg = " + i_lineSegment_intersection_flg);


                    kousa_point.set(d.foldLineSet.get(i).getA());
                    if (kousa_point.distance(add_sen.getA()) > Epsilon.UNKNOWN_1EN5) {
                        if (kousa_point.distance(add_sen.getA()) < kousa_point_distance) {
                            double d_kakudo = OritaCalc.angle(add_sen.getA(), add_sen.getB(), add_sen.getA(), kousa_point);
                            if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                                kousa_point_distance = kousa_point.distance(add_sen.getA());
                                add_sen.set(add_sen.getA(), kousa_point);
                            }
                        }
                    }

                    kousa_point.set(d.foldLineSet.get(i).getB());
                    if (kousa_point.distance(add_sen.getA()) > Epsilon.UNKNOWN_1EN5) {
                        if (kousa_point.distance(add_sen.getA()) < kousa_point_distance) {
                            double d_kakudo = OritaCalc.angle(add_sen.getA(), add_sen.getB(), add_sen.getA(), kousa_point);
                            if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                                kousa_point_distance = kousa_point.distance(add_sen.getA());
                                add_sen.set(add_sen.getA(), kousa_point);
                            }
                        }
                    }
                }
            }
        }

        add_sen.set(s0.getB(), add_sen.getB());
        return add_sen;
    }
}
