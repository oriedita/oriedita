package origami_editor.tools.line;

import origami_editor.editor.LineColor;

/**
 * A line from a point to a point, begin and end refer to points in a PointSet
 */
public class Line {
    int begin;
    int end;
    LineColor iColor;//If it is 0, there is no mountain valley. If 1, it's a mountain. If it is 2, it is a valley.

    //コンストラクタ
    public Line() {
        begin = 0;
        end = 0;
        iColor = LineColor.BLACK_0;
    }

    //コンストラクタ
    public Line(int ma, int at, LineColor ic) {
        begin = ma;
        end = at;
        iColor = ic;
    }

    //input
    public void set(Line line) {
        begin = line.getBegin();
        end = line.getEnd();
        iColor = line.getColor();
    }

    public void set(int i, int j, LineColor k) {
        begin = i;
        end = j;
        iColor = k;
    }

    //出力
    public int getBegin() {
        return begin;
    }

    public void setBegin(int i) {
        begin = i;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int i) {
        end = i;
    }

    public LineColor getColor() {
        return iColor;
    }

    public void setColor(LineColor i) {
        iColor = i;
    }

    public void reset() {
        begin = 0;
        end = 0;
        iColor = LineColor.BLACK_0;
    }
}
