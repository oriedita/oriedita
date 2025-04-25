package oriedita.editor.swing.component.combobox;

import oriedita.editor.Colors;
import oriedita.editor.swing.component.ColorIcon;
import oriedita.editor.swing.component.GridColorIcon;
import origami.crease_pattern.CustomLineTypes;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;

public class CustomLineTypeComboBoxRenderer implements ListCellRenderer<CustomLineTypes> {
    ListCellRenderer<CustomLineTypes> baseRenderer;

    public CustomLineTypeComboBoxRenderer() {
        baseRenderer = new CustomTextComboBoxRenderer<>(l -> switch (l) {
            case ANY -> "Any";
            case EGDE -> "E";
            case MANDV -> "M & V";
            case MOUNTAIN -> "M";
            case VALLEY -> "V";
            case AUX -> "A";
        });
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends CustomLineTypes> list, CustomLineTypes value, int index, boolean isSelected, boolean cellHasFocus) {
        var c = baseRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (c instanceof JLabel label) {
            var icon = switch (value) {
                case ANY -> new GridColorIcon(
                        Colors.get(Color.BLACK), Colors.get(Color.RED), Colors.get(Color.CYAN), Colors.get(Color.BLUE)
                );
                case EGDE -> new ColorIcon(Colors.get(Color.BLACK), 14, false);
                case MANDV -> new GridColorIcon(
                        Colors.get(Color.RED), Colors.get(Color.BLUE)
                );
                case MOUNTAIN -> new ColorIcon(Colors.get(Color.RED), 14, false);
                case VALLEY -> new ColorIcon(Colors.get(Color.BLUE), 14, false);
                case AUX -> new ColorIcon(Colors.get(Color.CYAN), 14, false);
            };
            label.setIcon(icon);
        }
        return c;
    }
}
