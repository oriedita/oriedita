package origami_editor.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.StraightLine;
import origami_editor.editor.canvas.MouseMode;
import origami_editor.editor.canvas.CreasePattern_Worker;
import origami.crease_pattern.util.CreasePattern_Worker_Toolbox;
import origami.folding.util.SortingBox;
import origami_editor.editor.databinding.CanvasModel;

@Singleton
public class MouseHandlerContinuousSymmetricDraw extends BaseMouseHandlerInputRestricted {
    private final CreasePattern_Worker d;
    private final CanvasModel canvasModel;
    CreasePattern_Worker_Toolbox e_s_dougubako;

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52;
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        System.out.println("i_egaki_dankai=" + d.lineStep.size());

        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        Point closest_point = d.getClosestPoint(p);

        if (p.distance(closest_point) < d.selectionDistance) {
            d.lineStepAdd(new LineSegment(closest_point, closest_point, d.lineColor));
        } else {
            d.lineStepAdd(new LineSegment(p, p, d.lineColor));
        }

        System.out.println("i_egaki_dankai=" + d.lineStep.size());
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.lineStep.size() == 2) {
            continuous_folding_new(d.lineStep.get(0).getA(), d.lineStep.get(1).getA());

            d.record();

            d.lineStep.clear();
        }
    }

    @Inject
    public MouseHandlerContinuousSymmetricDraw(CreasePattern_Worker d, CanvasModel canvasModel) {
        this.d = d;
        this.canvasModel = canvasModel;
        this.e_s_dougubako = new CreasePattern_Worker_Toolbox(d.foldLineSet);
    }

    public void continuous_folding_new(Point a, Point b) {//An improved version of continuous folding.
        canvasModel.markDirty();

        //ベクトルab(=s0)を点aからb方向に、最初に他の折線(直線に含まれる線分は無視。)と交差するところまで延長する

        //与えられたベクトルabを延長して、それと重ならない折線との、最も近い交点までs_stepとする。
        //補助活線は無視する
        //与えられたベクトルabを延長して、それと重ならない折線との、最も近い交点までs_stepとする


        //「再帰関数における、種の発芽」交点がない場合「種」が成長せずリターン。

        e_s_dougubako.lengthenUntilIntersectionCalculateDisregardIncludedLineSegment_new(a, b);//一番近い交差点を見つけて各種情報を記録
        if (e_s_dougubako.getLengthenUntilIntersectionFlg_new(a, b) == StraightLine.Intersection.NONE_0) {
            return;
        }

        LineSegment s = new LineSegment();
        s.set(e_s_dougubako.getLengthenUntilIntersectionLineSegment_new());
        d.lineStepAdd(s);
        s.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);

        System.out.println("20201129 saiki repaint ");

        //「再帰関数における、種の生成」求めた最も近い交点から次のベクトル（＝次の再帰関数に渡す「種」）を発生する。最も近い交点が折線とＸ字型に交差している点か頂点かで、種のでき方が異なる。

        //最も近い交点が折線とＸ字型の場合無条件に種を生成し、散布。
        if (e_s_dougubako.getLengthenUntilIntersectionFlg_new(a, b) == StraightLine.Intersection.INTERSECT_X_1) {
            LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment();
            kousaten_made_nobasi_saisyono_lineSegment.set(e_s_dougubako.getLengthenUntilIntersectionFirstLineSegment_new());

            Point new_a = new Point();
            new_a.set(e_s_dougubako.getLengthenUntilIntersectionPoint_new());//Ten new_aは最も近い交点
            Point new_b = new Point();
            new_b.set(OritaCalc.findLineSymmetryPoint(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

            continuous_folding_new(new_a, new_b);//種の散布
            return;
        }

        //最も近い交点が頂点（折線端末）の場合、頂点に集まる折線の数で条件分けして、種を生成し散布、
        if ((e_s_dougubako.getLengthenUntilIntersectionFlg_new(a, b) == StraightLine.Intersection.INTERSECT_T_A_21)
                || (e_s_dougubako.getLengthenUntilIntersectionFlg_new(a, b) == StraightLine.Intersection.INTERSECT_T_B_22)) {//System.out.println("20201129 21 or 22");

            StraightLine tyoku1 = new StraightLine(a, b);
            StraightLine.Intersection intersection;

            SortingBox<LineSegment> t_m_s_nbox = new SortingBox<>();

            t_m_s_nbox.set(d.foldLineSet.get_SortingBox_of_vertex_b_surrounding_foldLine(e_s_dougubako.getLengthenUntilIntersectionLineSegment_new().getA(), e_s_dougubako.getLengthenUntilIntersectionLineSegment_new().getB()));

            if (t_m_s_nbox.getTotal() == 2) {
                intersection = tyoku1.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(1));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (intersection == StraightLine.Intersection.INCLUDED_3) {
                    return;
                }

                intersection = tyoku1.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(2));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (intersection == StraightLine.Intersection.INCLUDED_3) {
                    return;
                }

                StraightLine tyoku2 = new StraightLine(t_m_s_nbox.getValue(1));
                intersection = tyoku2.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(2));
                if (intersection == StraightLine.Intersection.INCLUDED_3) {
                    LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment();
                    kousaten_made_nobasi_saisyono_lineSegment.set(e_s_dougubako.getLengthenUntilIntersectionFirstLineSegment_new());

                    Point new_a = new Point();
                    new_a.set(e_s_dougubako.getLengthenUntilIntersectionPoint_new());//Ten new_aは最も近い交点
                    Point new_b = new Point();
                    new_b.set(OritaCalc.findLineSymmetryPoint(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

                    continuous_folding_new(new_a, new_b);//種の散布
                    return;
                }
                return;
            }


            if (t_m_s_nbox.getTotal() == 3) {
                intersection = tyoku1.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(1));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (intersection == StraightLine.Intersection.INCLUDED_3) {
                    StraightLine tyoku2 = new StraightLine(t_m_s_nbox.getValue(2));
                    intersection = tyoku2.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(3));
                    if (intersection == StraightLine.Intersection.INCLUDED_3) {
                        LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment();
                        kousaten_made_nobasi_saisyono_lineSegment.set(e_s_dougubako.getLengthenUntilIntersectionFirstLineSegment_new());

                        Point new_a = new Point();
                        new_a.set(e_s_dougubako.getLengthenUntilIntersectionPoint_new());//Ten new_aは最も近い交点
                        Point new_b = new Point();
                        new_b.set(OritaCalc.findLineSymmetryPoint(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

                        continuous_folding_new(new_a, new_b);//種の散布
                        return;
                    }
                }
                //------------------------------------------------
                intersection = tyoku1.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(2));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (intersection == StraightLine.Intersection.INCLUDED_3) {
                    StraightLine tyoku2 = new StraightLine(t_m_s_nbox.getValue(3));
                    intersection = tyoku2.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(1));
                    if (intersection == StraightLine.Intersection.INCLUDED_3) {
                        LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment();
                        kousaten_made_nobasi_saisyono_lineSegment.set(e_s_dougubako.getLengthenUntilIntersectionFirstLineSegment_new());

                        Point new_a = new Point();
                        new_a.set(e_s_dougubako.getLengthenUntilIntersectionPoint_new());//Ten new_aは最も近い交点
                        Point new_b = new Point();
                        new_b.set(OritaCalc.findLineSymmetryPoint(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

                        continuous_folding_new(new_a, new_b);//種の散布
                        return;
                    }
                }
                //------------------------------------------------
                intersection = tyoku1.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(3));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (intersection == StraightLine.Intersection.INCLUDED_3) {
                    StraightLine tyoku2 = new StraightLine(t_m_s_nbox.getValue(1));
                    intersection = tyoku2.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(2));
                    if (intersection == StraightLine.Intersection.INCLUDED_3) {
                        LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment();
                        kousaten_made_nobasi_saisyono_lineSegment.set(e_s_dougubako.getLengthenUntilIntersectionFirstLineSegment_new());

                        Point new_a = new Point();
                        new_a.set(e_s_dougubako.getLengthenUntilIntersectionPoint_new());//Ten new_aは最も近い交点
                        Point new_b = new Point();
                        new_b.set(OritaCalc.findLineSymmetryPoint(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

                        continuous_folding_new(new_a, new_b);//種の散布
                    }
                }
            }
        }
    }
}
