package origami_editor.editor.component;

import origami.folding.util.IBulletinBoard;
import origami_editor.editor.Colors;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;

@Singleton
public class BulletinBoard implements IBulletinBoard {

    int ix0 = 10;//X coordinate of the upper left corner of the bulletin board
    int iy0 = 135;//Y coordinate at the upper left corner of the bulletin board
    int i_interval = 20;//Line spacing on the bulletin board

    String s01 = "";
    String s02 = "";
    String s03 = "";
    String s04 = "";
    String s05 = "";
    String s06 = "";
    String s07 = "";
    String s08 = "";
    String s09 = "";
    String s10 = "";

    java.util.List<ChangeListener> listener;

    @Inject
    public BulletinBoard() {
        listener = new ArrayList<>();
    }

    public void addChangeListener(ChangeListener changeListener) {
        listener.add(changeListener);
    }

    public void removeChangeListener(ChangeListener changeListener) {
        listener.remove(changeListener);
    }

    //input
    public void set_s01(String s0) {
        s01 = s0;
    }

    public void set_s02(String s0) {
        s02 = s0;
    }

    public void set_s03(String s0) {
        s03 = s0;
    }

    public void set_s04(String s0) {
        s04 = s0;
    }

    public void set_s05(String s0) {
        s05 = s0;
    }

    public void set_s06(String s0) {
        s06 = s0;
    }

    public void set_s07(String s0) {
        s07 = s0;
    }

    public void set_s08(String s0) {
        s08 = s0;
    }

    public void set_s09(String s0) {
        s09 = s0;
    }

    public void set_s10(String s0) {
        s10 = s0;
    }

    public void write(String s0) {
        s01 = s02;
        s02 = s03;
        s03 = s04;
        s04 = s05;
        s05 = s06;
        s06 = s07;
        s07 = s08;
        s08 = s09;
        s09 = s10;
        s10 = s0;
        repaint();
    }

    public void rewrite(int i, String s0) {
        if (i == 1) {
            s01 = s0;
        }
        if (i == 2) {
            s02 = s0;
        }
        if (i == 3) {
            s03 = s0;
        }
        if (i == 4) {
            s04 = s0;
        }
        if (i == 5) {
            s05 = s0;
        }
        if (i == 6) {
            s06 = s0;
        }
        if (i == 7) {
            s07 = s0;
        }
        if (i == 8) {
            s08 = s0;
        }
        if (i == 9) {
            s09 = s0;
        }
        if (i == 10) {
            s10 = s0;
        }
        repaint();
    }

    public void clear() {
        s01 = "";
        s02 = "";
        s03 = "";
        s04 = "";
        s05 = "";
        s06 = "";
        s07 = "";
        s08 = "";
        s09 = "";
        s10 = "";

        repaint();
    }

    public void repaint() {
        for (ChangeListener changeListener : listener) {
            changeListener.stateChanged(new ChangeEvent(this));
        }
    }

    public void draw(Graphics g) {
        g.setColor(Colors.get(Color.blue));
        g.drawString(s01, ix0, iy0 + 1 * i_interval);
        g.drawString(s02, ix0, iy0 + 2 * i_interval);
        g.drawString(s03, ix0, iy0 + 3 * i_interval);
        g.drawString(s04, ix0, iy0 + 4 * i_interval);
        g.drawString(s05, ix0, iy0 + 5 * i_interval);
        g.drawString(s06, ix0, iy0 + 6 * i_interval);
        g.drawString(s07, ix0, iy0 + 7 * i_interval);
        g.drawString(s08, ix0, iy0 + 8 * i_interval);
        g.drawString(s09, ix0, iy0 + 9 * i_interval);
        g.drawString(s10, ix0, iy0 + 10 * i_interval);
    }
}
