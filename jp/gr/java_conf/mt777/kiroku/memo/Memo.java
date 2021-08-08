package jp.gr.java_conf.mt777.kiroku.memo;

import java.util.*;

public class Memo {//データ格納用のメモ帳
    ArrayList<String> Gyou = new ArrayList<>();
    //---------------------------------

    //コンストラクタ
    public Memo() {
        Gyou.add("");
    }

    //
    public void reset() {
        Gyou.clear();
        Gyou.add("");
    }

    //
    public int getGyousuu() {
        return Gyou.size() - 1;
    }

    //
    public String getGyou(int i) {
        return Gyou.get(i);
    }

    //
    public void addGyou(String s) {
        Gyou.add(s);
    }


    public void set(Memo m0) {
        Memo m1 = new Memo();
        for (int i = 1; i <= m0.getGyousuu(); i++) {
            m1.addGyou(m0.getGyou(i));
        }//m0はまずm1に複製され、m1がsetされる。こうするのは、自分自身をsetする場合riset()によって中身が消えてしまうことを防ぐため

        reset();
        for (int i = 1; i <= m1.getGyousuu(); i++) {
            addGyou(m1.getGyou(i));
        }
    }

    public void set_old(Memo m0) {
        reset();
        for (int i = 1; i <= m0.getGyousuu(); i++) {
            addGyou(m0.getGyou(i));
        }
    }


    public void addMemo(Memo m0) {
        for (int i = 1; i <= m0.getGyousuu(); i++) {
            addGyou(m0.getGyou(i));
        }
    }


    public void systemout() {
        System.out.println("memo naiyou kokokara  VVVVVVVVVVVVVVVVVVVVVVVVVVVVVV");
        for (int i = 1; i <= getGyousuu(); i++) {
            System.out.println(getGyou(i));
        }
        System.out.println("memo naiyou kokomade  AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    }

}



