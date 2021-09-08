package origami_editor.tools.face;

import origami_editor.graphic2d.point.Point;
import origami_editor.tools.pointset.PointSet;

import java.util.ArrayList;

/**
 * Groups a set of points found in a PointSet
 *
 * @see Point
 * @see PointSet
 */
public class Face {
    // 1-indexed list
    ArrayList<Integer> pointIdList = new ArrayList<>();

    int numPoints;
    int icol;

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
}
