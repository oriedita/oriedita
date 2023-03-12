package oriedita.editor.swing.component;

import oriedita.editor.action.ActionType;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class DropdownToolButton extends JButton {
    private JPopupMenu dropdownMenu;
    private List<ActionType> actions = new ArrayList<>();
    private ActionType activeAction;

    public DropdownToolButton() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int diffX = getWidth() - e.getX();
                int diffY = getHeight() - e.getY();
                if (diffY + diffX < 15) {
                    dropdownMenu.setLocation(e.getLocationOnScreen());
                    dropdownMenu.setVisible(true);
                    e.consume();
                }
            }
        });
    }

    public JPopupMenu getDropdownMenu() {
        return dropdownMenu;
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
            });
            dropdownMenu.add(item);
        }
        setActiveAction(0);
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
}
