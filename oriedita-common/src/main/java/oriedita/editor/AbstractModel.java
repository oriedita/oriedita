package oriedita.editor;

import oriedita.common.converter.Converter;
import oriedita.editor.service.BindingService;

import javax.swing.JTextField;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class AbstractModel {
    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private  final BindingService bindingService;

    public AbstractModel(BindingService bindingService) {
        this.bindingService = bindingService;
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Bind a textfield component to a field in this model.
     * @param property name of the property to bind to (without get or set)
     */
    public <T> void bind(JTextField component, String property, Converter<T, String> converter) {
        bindingService.addBinding(this, component, property, converter);
    }
}
