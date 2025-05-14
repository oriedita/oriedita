package oriedita.editor;

import oriedita.common.converter.Converter;
import oriedita.editor.service.BindingService;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class AbstractModel implements Serializable {
    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final BindingService bindingService;

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

    // firing a normal propertyChange with name null won't notify named listeners, this method will notify every listener
    protected void notifyAllListeners() {
        for (PropertyChangeListener listener : pcs.getPropertyChangeListeners()) {
            listener.propertyChange(new PropertyChangeEvent(this, null, null, null));
        }
    }

    /**
     * Bind a textfield component to a field in this model.
     * @param property name of the property to bind to (without get or set)
     */
    public <T> void bind(JTextField component, String property, Converter<T, String> converter) {
        bindingService.addBinding(this, component, property, converter);
    }

    /**
     * Bind a textfield component to a field in this model.
     * @param property name of the property to bind to (without get or set)
     */
    public void bind(JTextField component, String property) {
        bindingService.addBinding(this, component, property, (Converter<?, String>) null);
    }

    public void bind(JComboBox<?> component, String property) {
        bindingService.addBinding(this, component, property);
    }
}
