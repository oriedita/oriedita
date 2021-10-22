package origami.folding.element;

import com.fasterxml.jackson.annotation.JsonIgnore;
import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.Point;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Groups a set of points found in a PointSet, two faces with the same containing points are considered equal.
 *
 * @see Point
 * @see PointSet
 */
public class Face implements Serializable {
    // 1-indexed list
    ArrayList<Integer> pointIdList = new ArrayList<>();

    int numPoints;
    int icol;

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

    public Face() {
        numPoints = 0;
        icol = 0;
        pointIdList.add(0);
    }

    public Face(Face mn) {
        numPoints = mn.getNumPoints();
        icol = mn.getColor();
        for (int i = 0; i <= numPoints; i++) {
            pointIdList.add(mn.getPointId(i));
        }
    }

    public void reset() {
        numPoints = 0;
        icol = 0;

        pointIdList.clear();
        pointIdList.add(0);
    }

    public int getNumPoints() {
        return numPoints;
    }

    public void setNumPoints(int i) {
        numPoints = i;
    }

    public void addPointId(int pointId) {
        numPoints = numPoints + 1;
        pointIdList.add(pointId);
    }

    public int getPointId(int i) {
        return pointIdList.get(i);
    }

    public boolean containsPointId(int i) {
        return pointIdList.contains(i);
    }

    private void replace() { //Replace the value of pointIdList[n + 1] with the value of pointId[n]. The value of PointId[1] should be the last PointId
        for (int i = 1; i <= numPoints; i++) {
            pointIdList.set(i - 1, getPointId(i));
        }
        pointIdList.set(numPoints, getPointId(0));
        pointIdList.set(0, 0);

    }

    public int getColor() {
        return icol;
    }

    public void setColor(int i) {
        icol = i;
    }

    public int getMinimumPointId() {    //Find the minimum id of the line contained in the surface mptemp.
        int idmin = getPointId(1);
        for (int i = 2; i <= numPoints; i++) {
            if (idmin > getPointId(i)) {
                idmin = getPointId(i);
            }
        }
        return idmin;
    }

    public void align() { //Align so that the value of PointId [1] is the smallest in PointId [].
        int idmin = getMinimumPointId();
        while (getPointId(1) != idmin) {
            replace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Face face = (Face) o;
        return numPoints == face.numPoints && Objects.equals(pointIdList, face.pointIdList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointIdList, numPoints);
    }
}
