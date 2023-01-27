package oriedita.editor;

import javax.swing.JTextField;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

public class AbstractModel {
    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

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
     */
    public void bind(JTextField component, String property) {
        try {
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor(property, getClass());
            propertyDescriptor.getWriteMethod().invoke(this, component.getText());

            AtomicReference<String> value = new AtomicReference<>(component.getText());

            component.addCaretListener(e -> {
                try {
                    if (!value.get().equals(component.getText())) {
                        value.set(component.getText());
                        propertyDescriptor.getWriteMethod().invoke(this, component.getText());
                    }
                } catch (IllegalAccessException | InvocationTargetException ex) {
                    ex.printStackTrace();
                }
            });
            addPropertyChangeListener(property, e -> {
                if (!e.getNewValue().equals(component.getText())) {
                    component.setText((String) e.getNewValue());
                }
            });

        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
