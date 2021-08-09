package jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.jyougehyou.touka_jyouken;

public class EquivalenceCondition {//このクラスは上下表でjg[a][b]=jg[c][d]という関係を記録、活用するのに使う。
    int a, b, c, d;

    //コンストラクタ
    public EquivalenceCondition() {
        a = 0;
        b = 0;
        c = 0;
        d = 0;
    }

    //コンストラクタ
    public EquivalenceCondition(int ia, int ib, int ic, int id) {
        a = ia;
        b = ib;
        c = ic;
        d = id;
    }

    //
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

    //折り畳み推定の際の等価条件の登録は　addTouka_jyouken(im,Mid_min,im,Mid_max);  による

}
