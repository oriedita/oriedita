package jp.gr.java_conf.mt777.origami.dougu.bou;

public class Stick {
    int begin;
    int end;
    int icol;//If it is 0, there is no mountain valley. If 1, it's a mountain. If it is 2, it is a valley. 

    //コンストラクタ
    public Stick() {
        begin = 0;
        end = 0;
        icol = 0;
    }

    //コンストラクタ
    public Stick(int ma, int at, int ic) {
        begin = ma;
        end = at;
        icol = ic;
    }

    //入力
    public void set(Stick bu) {
        begin = bu.getBegin();
        end = bu.getEnd();
        icol = bu.getColor();
    }

    public void set(int i, int j, int k) {
        begin = i;
        end = j;
        icol = k;
    }

    public void setBegin(int i) {
        begin = i;
    }

    public void setEnd(int i) {
        end = i;
    }

    public void setColor(int i) {
        icol = i;
    }

    //出力
    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }

    public int getColor() {
        return icol;
    }

    public void reset() {
        begin = 0;
        end = 0;
        icol = 0;
    }
}
