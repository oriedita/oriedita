package oriedita.editor.databinding;

import com.fasterxml.jackson.annotation.JsonIgnore;
import oriedita.editor.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

@Singleton
public class SelectedTextModel {

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private Text selectedText;

    private boolean selected;

    private boolean textDirty;

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
        if (!Objects.equals(selectedText, oldText)) {
            textDirty = true;
        }
        pcs.firePropertyChange("selectedText", oldText, selectedText);
    }

    public void markDirty() {
        pcs.firePropertyChange("dirty", false, true);
    }

    public void markTextClean() {
        textDirty = false;
    }

    @JsonIgnore
    public boolean isTextDirty() {
        return textDirty;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        boolean oldSelected = this.selected;
        this.selected = selected;
        pcs.firePropertyChange("selected", oldSelected, selected);
    }

    public void reset() {
        setSelected(false);
        setSelectedText(null);
        this.pcs.firePropertyChange(null, null, null);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

}
