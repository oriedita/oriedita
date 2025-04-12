package oriedita.editor.service;

import oriedita.editor.AbstractModel;
import oriedita.common.converter.Converter;

import javax.swing.JTextField;

public interface BindingService {
    <T> void addBinding(AbstractModel model, JTextField component, String property, Converter<T, String> converter);
}
