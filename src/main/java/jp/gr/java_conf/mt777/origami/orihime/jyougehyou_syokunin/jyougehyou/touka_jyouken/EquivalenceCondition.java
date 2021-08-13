package jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.jyougehyou.touka_jyouken;

//This class is used to record and utilize the relationship hierarchyList[a][b] = hierarchyList[c][d] in the hierarchy list.
public class EquivalenceCondition {
    int a, b, c, d;

    public EquivalenceCondition() {
        a = 0;
        b = 0;
        c = 0;
        d = 0;
    }

    public EquivalenceCondition(int ia, int ib, int ic, int id) {
        a = ia;
        b = ib;
        c = ic;
        d = id;
    }

    public void reset() {
        a = 0;
        b = 0;
        c = 0;
        d = 0;
    }

    public void set(int ai, int bi, int ci, int di) {
        a = ai;
        b = bi;
        c = ci;
        d = di;
    }

    public void set(EquivalenceCondition tj) {
        a = tj.getA();
        b = tj.getB();
        c = tj.getC();
        d = tj.getD();
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public int getC() {
        return c;
    }

    public int getD() {
        return d;
    }

    //Registration of the equivalence condition at the time of folding estimation is by addEquivalenceCondition(im, Mid_min, im, Mid_max) ;.

}
