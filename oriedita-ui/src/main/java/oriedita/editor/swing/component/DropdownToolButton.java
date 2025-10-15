package oriedita.editor.swing.component;

import oriedita.editor.action.ActionType;
import oriedita.editor.action.MouseModeAction;
import oriedita.editor.handler.PopupMenuAdapter;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.Timer;
import javax.swing.event.PopupMenuEvent;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class DropdownToolButton extends JButton {
    private JPopupMenu dropdownMenu;
    private List<ActionType> actions = new ArrayList<>();
    private ActionType activeAction;
    private boolean dropdownOpened = false;

    private Timer pressForDropdownTimer;
    private Timer cycleActivationTimer;
    private long lastCycleMillis = 0;

    public DropdownToolButton() {
        this.addMouseListener(new MouseAdapter() {
            private boolean isInTriangle(MouseEvent e) {
                int diffX = getWidth() - e.getX();
                int diffY = getHeight() - e.getY();
                return  (diffY + diffX < 15);
            }
            @Override
            public void mousePressed(MouseEvent e) {
                pressForDropdownTimer = new Timer(300, ev -> openDropdown());
                pressForDropdownTimer.setRepeats(false);
                pressForDropdownTimer.start();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                pressForDropdownTimer.stop();
                if (e.getButton() == MouseEvent.BUTTON1 && (isInTriangle(e) )) {
                    e.consume();
                    openDropdown();
                }
            }
        });
    }

    private void openDropdown() {
        var locOnScreen = getLocationOnScreen();
        dropdownMenu.setLocation(locOnScreen.x, locOnScreen.y + getHeight());
        dropdownMenu.setVisible(true);
        setEnabled(false);
    }

    public JPopupMenu getDropdownMenu() {
        return dropdownMenu;
    }

    public ActionType getActiveAction() {
        return activeAction;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int triangleSize = 8;
        int border = 2;
        int[] xPoints = new int[] {getWidth()-border, getWidth()-border, getWidth() - triangleSize};
        int[] yPoints = new int[] {getHeight() - triangleSize, getHeight()-border, getHeight()-border};
        g.setColor(getForeground());
        g.fillPolygon(xPoints, yPoints, 3);
    }

    public void setActions(ActionType... actions) {
        this.actions = List.of(actions);
        this.dropdownMenu = new JPopupMenu();
        dropdownMenu.setInvoker(this);
        for (int i = 0; i < actions.length; i++) {
            ActionType action = actions[i];
            JMenuItem item = new JMenuItem();
            item.setActionCommand(action.action());
            int finalI = i;
            item.addActionListener(e -> {
                setActiveAction(finalI);
                dropdownMenu.setVisible(false);
                dropdownOpened = false;
            });
            dropdownMenu.add(item);
        }

        dropdownMenu.addPopupMenuListener(new PopupMenuAdapter() {
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                setEnabled(true);
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                dropdownOpened = false;
            }

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                dropdownOpened = true;
            }
        });
        setActiveAction(0);
    }
    public List<ActionType> getActions() {
        return actions;
    }

    public boolean setActiveAction(int index) {
        if (this.actions.size() > index) {
            ActionType oldActiveAction = activeAction;
            activeAction = actions.get(index);
            this.setActionCommand(activeAction.action());
            super.firePropertyChange("activeAction", oldActiveAction, activeAction);
            return true;
        }
        return false;
    }

    public boolean wasDropdownItemJustSelected() {
        return dropdownOpened && !dropdownMenu.isVisible();
    }

    public void cycleOrActivate(ActionType action) {
        int cycleTimeMs = 400;
        if (isSelected() && msSinceLastCycle()< cycleTimeMs) {
            setActiveAction(
                    (getActions().indexOf(getActiveAction()) + 1)
                            % getActions().size());
        } else {
            setActiveAction(getActions().indexOf(action));
        }
        lastCycleMillis = System.currentTimeMillis();
        if (cycleActivationTimer != null) {
            cycleActivationTimer.stop();
        }
        if (!(getAction() instanceof MouseModeAction)) {
            setSelected(true);
            cycleActivationTimer = new Timer(cycleTimeMs + 10, (e) -> doClick());
            cycleActivationTimer.setRepeats(false);
            cycleActivationTimer.start();
        } else {
            doClick();
        }
    }

    private long msSinceLastCycle() {
        return (System.currentTimeMillis() - lastCycleMillis);
    }
}
