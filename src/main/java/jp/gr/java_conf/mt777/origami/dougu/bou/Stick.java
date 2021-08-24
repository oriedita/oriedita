package jp.gr.java_conf.mt777.origami.dougu.bou;

import jp.gr.java_conf.mt777.origami.orihime.LineType;

/**
 * A line from a point to a point, begin and end refer to points.
 */
public class Stick {
    int begin;
    int end;
    LineType iColor;//If it is 0, there is no mountain valley. If 1, it's a mountain. If it is 2, it is a valley.

    //コンストラクタ
    public Stick() {
        begin = 0;
        end = 0;
        iColor = LineType.BLACK_0;
    }

    //コンストラクタ
    public Stick(int ma, int at, LineType ic) {
        begin = ma;
        end = at;
        iColor = ic;
    }

    //input
    public void set(Stick stick) {
        begin = stick.getBegin();
        end = stick.getEnd();
        iColor = stick.getColor();
    }

    public void set(int i, int j, LineType k) {
        begin = i;
        end = j;
        iColor = k;
    }

    public void setBegin(int i) {
        begin = i;
    }

    public void setEnd(int i) {
        end = i;
    }

    public void setColor(LineType i) {
        iColor = i;
    }

    //出力
    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }

    public LineType getColor() {
        return iColor;
    }

    public void reset() {
        begin = 0;
        end = 0;
        iColor = LineType.BLACK_0;
    }
}
