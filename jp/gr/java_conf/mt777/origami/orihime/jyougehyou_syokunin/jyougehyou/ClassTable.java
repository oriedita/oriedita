package jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.jyougehyou;


import jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.jyougehyou.touka_jyouken.*;

import java.util.*;

public class ClassTable {//This class is used to record and utilize the hierarchical relationship of faces when folded.
    int facesTotal;             //Number of faces in the unfolded view before folding

    //  jg[][]は折る前の展開図のすべての面同士の上下関係を1つの表にまとめたものとして扱う
    //　jg[i][j]が1なら面iは面jの上側。0なら下側。
    //  jg[i][j]が-50なら、面iとjは重なが、上下関係は決められていない。
    //jg[i][j]が-100なら、面iとjは重なるところがない。
    int jg[][];
    int jg_h[][];
    int EquivalenceConditionTotal;   //２つの隣接する面の境界線を他の面が突き抜ける状況が生じうる組み合わせ。
    EquivalenceCondition tj = new EquivalenceCondition();

    ArrayList<EquivalenceCondition> tL = new ArrayList<>();

    int uEquivalenceConditionTotal;

    ArrayList<EquivalenceCondition> uL = new ArrayList<>();
    EquivalenceCondition uj = new EquivalenceCondition();//２つの隣接する面a,bの境界線と２つの隣接する面c,dの境界線が、平行かつ一部重なっていて、
    //さらにa,b,c,dがあるSmenで共存する場合の、境界線で突き抜けが生じうる組み合わせ


    public ClassTable() {//コンストラクタ
        reset();
    }

    //
    public void reset() {
        tL.clear();
        tL.add(new EquivalenceCondition());
        uL.clear();
        uL.add(new EquivalenceCondition());
        EquivalenceConditionTotal = 0;
        uEquivalenceConditionTotal = 0;
    }

    //
    public void jg_save() {
        for (int i = 1; i <= facesTotal; i++) {
            for (int j = 1; j <= facesTotal; j++) {
                jg_h[i][j] = jg[i][j];
            }
        }
    }

    //
    public void jg_restore() {
        for (int i = 1; i <= facesTotal; i++) {
            for (int j = 1; j <= facesTotal; j++) {
                jg[i][j] = jg_h[i][j];
            }
        }
    }

    //
    public void set(int i, int j, int jyoutai) {
        jg[i][j] = jyoutai;
        //	System.out.print(i);System.out.print(":上下表:");System.out.println(j);
    }

    public int get(int i, int j) {
        return jg[i][j];
    }

    public void setFacesTotal(int iM) {
        facesTotal = iM;

        int j_g[][] = new int[facesTotal + 1][facesTotal + 1];
        int j_g_h[][] = new int[facesTotal + 1][facesTotal + 1];

        jg = j_g;
        jg_h = j_g_h;


        for (int i = 0; i <= facesTotal; i++) {
            for (int j = 0; j <= facesTotal; j++) {
                jg[i][j] = -100;
                jg_h[i][j] = -100;
            }
        }
    }


    public int getFacesTotal() {
        return facesTotal;
    }


    public int getEquivalenceConditionTotal() {
        return EquivalenceConditionTotal;
    }

    public EquivalenceCondition getEquivalenceCondition(int i) {
        return (EquivalenceCondition) tL.get(i);
    }


    // Add equivalence condition. When there are two adjacent faces im1 and im2 as the boundary of the bar ib, when the folding is estimated
    // The surface im located at the position where it overlaps a part of the bar ib is not sandwiched between the surface im1 and the surface im2 in the vertical direction. From this
    // The equivalent condition of gj [im1] [im] = gj [im2] [im] is satisfied.
    public void addEquivalenceCondition(int ai, int bi, int ci, int di) {
        EquivalenceConditionTotal = EquivalenceConditionTotal + 1;
        tL.add(new EquivalenceCondition(ai, bi, ci, di));
    }

    public int getUEquivalenceConditionTotal() {
        return uEquivalenceConditionTotal;
    }

    public EquivalenceCondition getUEquivalenceCondition(int i) {
        return (EquivalenceCondition) uL.get(i);
    }


    // Add equivalence condition. There are two adjacent faces im1 and im2 as the boundary of the bar ib,
    // Also, there are two adjacent faces im3 and im4 as the boundary of the bar jb, and when ib and jb are parallel and partially overlap, when folding is estimated.
    // The surface of the bar ib and the surface of the surface jb are not aligned with i, j, i, j or j, i, j, i. If this happens,
    // Since there is a mistake in the 3rd place from the beginning, find out what digit this 3rd place is in Smen and advance this digit by 1.
    public void addUEquivalenceCondition(int ai, int bi, int ci, int di) {
        uEquivalenceConditionTotal = uEquivalenceConditionTotal + 1;

        uL.add(new EquivalenceCondition(ai, bi, ci, di));
    }


}
