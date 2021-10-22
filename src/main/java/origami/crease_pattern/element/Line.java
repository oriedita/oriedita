package origami.crease_pattern.element;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * A line from a point to a point, begin and end refer to points in a PointSet
 */
public class Line implements Serializable {
    int begin;
    int end;
    LineColor color;//If it is 0, there is no mountain valley. If 1, it's a mountain. If it is 2, it is a valley.

    /**
     * Returns the faceId with the smaller faceId of the faces containing the line lineId as the boundary (there are up to two faces). Returns 0 if there is no face containing the line as the boundary
     */
    @JsonIgnore
    int lineInFaceBorder_min;
    /**
     * Returns the faceId with the larger faceId among the faces containing the line lineId as the boundary (there are two faces at the maximum). Returns 0 if there is no face containing the line as the boundary
     */
    @JsonIgnore
    int lineInFaceBorder_max;

    public int getLineInFaceBorder_min() {
        return lineInFaceBorder_min;
    }

    public void setLineInFaceBorder_min(int lineInFaceBorder_min) {
        this.lineInFaceBorder_min = lineInFaceBorder_min;
    }

    public int getLineInFaceBorder_max() {
        return lineInFaceBorder_max;
    }

    public void setLineInFaceBorder_max(int lineInFaceBorder_max) {
        this.lineInFaceBorder_max = lineInFaceBorder_max;
    }

    @JsonIgnore
    double xMax;
    @JsonIgnore
    double xMin;
    @JsonIgnore
    double yMax;
    @JsonIgnore
    double yMin;

    public double getxMax() {
        return xMax;
    }

    public void setxMax(double xMax) {
        this.xMax = xMax;
    }

    public double getxMin() {
        return xMin;
    }

    public void setxMin(double xMin) {
        this.xMin = xMin;
    }

    public double getyMax() {
        return yMax;
    }

    public void setyMax(double yMax) {
        this.yMax = yMax;
    }

    public double getyMin() {
        return yMin;
    }

    public void setyMin(double yMin) {
        this.yMin = yMin;
    }

    //コンストラクタ
    public Line() {
        begin = 0;
        end = 0;
        color = LineColor.BLACK_0;
    }

    //コンストラクタ
    public Line(int ma, int at, LineColor ic) {
        begin = ma;
        end = at;
        color = ic;
        lineInFaceBorder_min = 0;
        lineInFaceBorder_max = 0;
    }

    //input
    public void set(Line line) {
        begin = line.getBegin();
        end = line.getEnd();
        color = line.getColor();

        xMax = line.getxMax();
        xMin = line.getxMin();
        yMax = line.getyMax();
        yMin = line.getyMin();

        lineInFaceBorder_max = line.getLineInFaceBorder_max();
        lineInFaceBorder_min = line.getLineInFaceBorder_min();
    }

    public void set(int i, int j, LineColor k) {
        begin = i;
        end = j;
        color = k;
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
        return color;
    }

    public void setColor(LineColor i) {
        color = i;
    }

    public void reset() {
        begin = 0;
        end = 0;
        color = LineColor.BLACK_0;
    }
}
