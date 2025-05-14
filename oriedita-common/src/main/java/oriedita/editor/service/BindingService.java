package oriedita.editor.service;

import org.tinylog.Logger;
import oriedita.editor.AbstractModel;
import oriedita.common.converter.Converter;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.io.Serializable;

public interface BindingService {
    <T> void addBinding(AbstractModel model, JTextField component, String property, Converter<T, String> converter);
    <T> void addBinding(AbstractModel model, JComboBox<T> component, String modelProperty);

    /**
     * @return a dummy bindingService that does nothing when bind is called.
     */
    static BindingService dummy() {
        return new DummyBindingService();
    }
    class DummyBindingService implements BindingService, Serializable{
        @Override
        public <T> void addBinding(AbstractModel model, JTextField component, String property, Converter<T, String> converter) {
            Logger.warn("called dummy addBinding");
        }

        @Override
        public <T> void addBinding(AbstractModel model, JComboBox<T> component, String modelProperty) {
            Logger.warn("called dummy addBinding");
        }
    }
}
