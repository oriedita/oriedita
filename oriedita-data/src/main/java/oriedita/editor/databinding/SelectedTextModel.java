package oriedita.editor.databinding;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.tinylog.Logger;
import oriedita.editor.text.Text;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

@ApplicationScoped
public class SelectedTextModel {

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private Text selectedText;

    private boolean selected;

    private boolean dirty;

    @Inject
    public SelectedTextModel() {
        reset();
    }

    public Text getSelectedText() {
        return selectedText;
    }

    public void setSelectedText(Text selectedText) {
        Text oldText = this.selectedText;
        this.selectedText = selectedText;
        pcs.firePropertyChange("selectedText", oldText, selectedText);
    }

    public void markDirty() {
        setDirty(true);
    }

    public void markClean() {
        setDirty(false);
    }

    @JsonIgnore
    public void setDirty(boolean dirty) {
        boolean oldDirty = this.dirty;
        if (dirty && !oldDirty) {
            Logger.info("text dirty");
        }
        if (!dirty && oldDirty) {
            Logger.info("text clean");
        }
        this.dirty = dirty;
        pcs.firePropertyChange("dirty", !dirty, dirty); // should always trigger a PropertyChangeEvent
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isSelected(Text t) {
        return isSelected() && getSelectedText() == t;
    }

    public void setSelected(boolean selected) {
        boolean oldSelected = this.selected;
        this.selected = selected;
        pcs.firePropertyChange("selected", oldSelected, selected);
    }

    public void reset() {
        setSelected(false);
        setSelectedText(null);
        dirty = false;
        this.pcs.firePropertyChange(null, null, null);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    @JsonIgnore
    public boolean isDirty() {
        return dirty;
    }
}
