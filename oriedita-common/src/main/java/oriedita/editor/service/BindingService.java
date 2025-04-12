package oriedita.editor.service;

import oriedita.editor.AbstractModel;
import oriedita.common.converter.Converter;

import javax.swing.JComboBox;
import javax.swing.JTextField;

public interface BindingService {
    <T> void addBinding(AbstractModel model, JTextField component, String property, Converter<T, String> converter);
    <T> void addBinding(AbstractModel model, JComboBox<T> component, String modelProperty);

    /**
     * @return a dummy bindingService that does nothing when bind is called.
     */
    static BindingService dummy() {
        return new BindingService() {
            @Override
            public <T> void addBinding(AbstractModel model, JTextField component, String property, Converter<T, String> converter) {
            }

            @Override
            public <T> void addBinding(AbstractModel model, JComboBox<T> component, String modelProperty) {
                
            }

        };
    }
}
