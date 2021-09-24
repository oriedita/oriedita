package origami_editor.editor.databinding;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class FileModel implements Serializable {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private String defaultDirectory;
    private String savedFileName;

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public String getDefaultDirectory() {
        return defaultDirectory;
    }

    public void setDefaultDirectory(String defaultDirectory) {
        String oldDefaultDirectory = this.defaultDirectory;
        this.defaultDirectory = defaultDirectory;
        this.pcs.firePropertyChange("defaultDirectory", oldDefaultDirectory, defaultDirectory);
    }

    public String getSavedFileName() {
        return savedFileName;
    }

    public void setSavedFileName(String savedFileName) {
        String oldSavedFileName = this.savedFileName;
        this.savedFileName = savedFileName;
        this.pcs.firePropertyChange("savedFileName", oldSavedFileName, savedFileName);
    }

    public void reset() {
        this.savedFileName = null;
        this.defaultDirectory = null;

        this.pcs.firePropertyChange(null, null, null);
    }
}
