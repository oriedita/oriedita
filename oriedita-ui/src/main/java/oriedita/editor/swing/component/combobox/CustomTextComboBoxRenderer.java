package oriedita.editor.swing.component.combobox;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Component;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CustomTextComboBoxRenderer<T> implements ListCellRenderer<T> {


    private final BiFunction<T, Integer, String> textProvider;
    private final DefaultListCellRenderer defaultRenderer;

    public CustomTextComboBoxRenderer(BiFunction<T, Integer, String> textProvider) {
        this.textProvider = textProvider;
        this.defaultRenderer = new DefaultListCellRenderer();
    }

    public CustomTextComboBoxRenderer(Function<T, String> textProvider) {
        this((t, index) -> textProvider.apply(t));
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {
        var c = defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (c instanceof JLabel label) {
            label.setText(textProvider.apply(value, index));
        }
        return c;
    }
}
