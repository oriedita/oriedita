package origami_editor.record.memo;

import java.util.ArrayList;

public class Memo {//Notepad for data storage
    ArrayList<String> lines = new ArrayList<>();

    public Memo() {
        lines.add("");
    }

    public void reset() {
        lines.clear();
        lines.add("");
    }

    public int getLineCount() {
        return lines.size() - 1;
    }

    public String getLine(int i) {
        return lines.get(i);
    }

    public void addLine(String s) {
        lines.add(s);
    }

    public void set(Memo m0) {
        Memo m1 = new Memo();
        for (int i = 1; i <= m0.getLineCount(); i++) {
            m1.addLine(m0.getLine(i));
        }// m0 is first duplicated to m1 and m1 is set. This is to prevent the contents from being erased by riset () when setting itself.

        reset();
        for (int i = 1; i <= m1.getLineCount(); i++) {
            addLine(m1.getLine(i));
        }
    }

    public void addMemo(Memo m0) {
        for (int i = 1; i <= m0.getLineCount(); i++) {
            addLine(m0.getLine(i));
        }
    }
}



