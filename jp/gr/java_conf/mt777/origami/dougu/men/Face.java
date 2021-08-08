package jp.gr.java_conf.mt777.origami.dougu.men;

import java.util.*;

public class Face {
    ArrayList<Integer> TenidL = new ArrayList<>();
    int Tenidsuu;
    int icol;

    //Ten kanrenTen =new Ten();//面に関連する点を格納。面内部の点とかを格納するのに使う。必ずしも使う必要は無いので、使わないときは無視していい。
    //コンストラクタ
    public Face() {
        Tenidsuu = 0;
        icol = 0;
        TenidL.add(0);
    }

    //コンストラクタ
    public Face(Face mn) {
        Tenidsuu = mn.getTenidsuu();
        icol = mn.getcolor();
        for (int i = 0; i <= Tenidsuu; i++) {
            TenidL.add(mn.getPointId(i));
        }
    }

    public void reset() {
        Tenidsuu = 0;
        icol = 0;

        TenidL.clear();

        TenidL.add(0);


    }

    public int getTenidsuu() {
        return Tenidsuu;
    }

    public void setTenidsuu(int i) {
        Tenidsuu = i;
    }

    public void addTenid(int Tid) {
        Tenidsuu = Tenidsuu + 1;
        TenidL.add(Tid);
    }

    public int getPointId(int i) {
		return TenidL.get(i);
    }

    //private	void setTenid(int i,int id){ Tenid[i]=id;}

    private void okikae() { //Tenid[n+1]の値をTenid[n]の値に置き換える。Tenid[1]の値は、最後のTenidにする
        for (int i = 1; i <= Tenidsuu; i++) {
            TenidL.set(i - 1, getPointId(i));
        }
        TenidL.set(Tenidsuu, getPointId(0));
        TenidL.set(0, 0);

    }

    public void setcolor(int i) {
        icol = i;
    }

    public int getcolor() {
        return icol;
    }

    public int getTenidmin() {    //面mptempに含まれる棒のidの最小値を求める。
        //int idmin=10000;
        int idmin = getPointId(1);
        //for(int i=1;i<=Tenidsuu;i++){
        for (int i = 2; i <= Tenidsuu; i++) {
            if (idmin > getPointId(i)) {
                idmin = getPointId(i);
            }
        }
        return idmin;
    }

    public void align() { //Tenid[1]の値がTenid[]のなかで最小になるように整列する。
        int idmin;
        idmin = getTenidmin();
        while (getPointId(1) != idmin) {
            okikae();
        }
    }


}
