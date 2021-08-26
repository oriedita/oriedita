package jp.gr.java_conf.mt777.origami.dougu.men;

import java.util.*;

public class Face {
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

    public void addPointId(int Tid) {
        numPoints = numPoints + 1;
        pointIdList.add(Tid);
    }

    public int getPointId(int i) {
		return pointIdList.get(i);
    }

    private void okikae() { //Tenid[n+1]の値をTenid[n]の値に置き換える。Tenid[1]の値は、最後のTenidにする
        for (int i = 1; i <= numPoints; i++) {
            pointIdList.set(i - 1, getPointId(i));
        }
        pointIdList.set(numPoints, getPointId(0));
        pointIdList.set(0, 0);

    }

    public void setColor(int i) {
        icol = i;
    }

    public int getColor() {
        return icol;
    }

    public int getMinimumPointId() {    //面mptempに含まれる棒のidの最小値を求める。
        int idmin = getPointId(1);
        for (int i = 2; i <= numPoints; i++) {
            if (idmin > getPointId(i)) {
                idmin = getPointId(i);
            }
        }
        return idmin;
    }

    public void align() { //Tenid[1]の値がTenid[]のなかで最小になるように整列する。
        int idmin;
        idmin = getMinimumPointId();
        while (getPointId(1) != idmin) {
            okikae();
        }
    }
}
