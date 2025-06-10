package oriedita.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class AbstractModel implements Serializable {
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

    // firing a normal propertyChange with name null won't notify named listeners, this method will notify every listener
    protected void notifyAllListeners() {
        for (PropertyChangeListener listener : pcs.getPropertyChangeListeners()) {
            listener.propertyChange(new PropertyChangeEvent(this, null, null, null));
        }
    }
}
