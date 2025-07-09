package oriedita.editor.swing;

import javax.swing.JComboBox;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;

public class DropdownMouseWheelAdapter extends MouseAdapter {
    private final JComboBox<?> comboBox;

    public DropdownMouseWheelAdapter(JComboBox<?> comboBox) {
        this.comboBox = comboBox;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int index = comboBox.getSelectedIndex();
        int itemCount = comboBox.getItemCount();
        if (e.getWheelRotation() > 0) {
            comboBox.setSelectedIndex((index + 1) % itemCount);
        } else if (e.getWheelRotation() < 0) {
            comboBox.setSelectedIndex(index != 0 ? (index - 1) % itemCount : itemCount - 1);
        }

        e.consume();
        comboBox.showPopup();
    }
}
