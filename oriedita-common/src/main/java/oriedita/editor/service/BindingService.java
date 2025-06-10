package oriedita.editor.service;

import oriedita.editor.AbstractModel;
import oriedita.common.converter.Converter;

import javax.swing.JComboBox;
import javax.swing.JTextField;

public interface BindingService {
    <T> void addBinding(AbstractModel model, String property, JTextField component, Converter<T, String> converter);
    default void addBinding(AbstractModel model, String property, JTextField component) {
        addBinding(model, property, component, null);
    }
    <T> void addBinding(AbstractModel model, String modelProperty, JComboBox<T> component);
}
