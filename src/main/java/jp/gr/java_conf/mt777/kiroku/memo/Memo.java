package jp.gr.java_conf.mt777.kiroku.memo;

import java.util.*;

public class Memo {//Notepad for data storage
    ArrayList<String> lines = new ArrayList<>();
    //---------------------------------

    //コンストラクタ
    public Memo() {
        lines.add("");
    }

    //
    public void reset() {
        lines.clear();
        lines.add("");
    }

    //
    public int getLineSize() {
        return lines.size() - 1;
    }

    //
    public String getLine(int i) {
        return lines.get(i);
    }

    //
    public void addLine(String s) {
        lines.add(s);
    }


    public void set(Memo m0) {
        Memo m1 = new Memo();
        for (int i = 1; i <= m0.getLineSize(); i++) {
            m1.addLine(m0.getLine(i));
        }//m0はまずm1に複製され、m1がsetされる。こうするのは、自分自身をsetする場合riset()によって中身が消えてしまうことを防ぐため

        reset();
        for (int i = 1; i <= m1.getLineSize(); i++) {
            addLine(m1.getLine(i));
        }
    }

    public void set_old(Memo m0) {
        reset();
        for (int i = 1; i <= m0.getLineSize(); i++) {
            addLine(m0.getLine(i));
        }
    }


    public void addMemo(Memo m0) {
        for (int i = 1; i <= m0.getLineSize(); i++) {
            addLine(m0.getLine(i));
        }
    }


    public void systemout() {
        System.out.println("memo naiyou kokokara  VVVVVVVVVVVVVVVVVVVVVVVVVVVVVV");
        for (int i = 1; i <= getLineSize(); i++) {
            System.out.println(getLine(i));
        }
        System.out.println("memo naiyou kokomade  AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    }

}



