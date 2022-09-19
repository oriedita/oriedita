package oriedita.editor.databinding;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.tools.ResourceUtil;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.Serializable;

@ApplicationScoped
public class FileModel implements Serializable {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private String savedFileName;
    private boolean saved;

    @Inject
    public FileModel() {
        reset();
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        boolean oldSaved = this.saved;
        this.saved = saved;
        this.pcs.firePropertyChange("saved", oldSaved, saved);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
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
        saved = true;
        savedFileName = null;

        this.pcs.firePropertyChange(null, null, null);
    }

    private static final String frame_title_0 = "Oriedita " + ResourceUtil.getVersionFromManifest();

    public String determineFrameTitle() {
        String frame_title;
        if (getSavedFileName() != null) {
            File file = new File(getSavedFileName());

            frame_title = frame_title_0 + "        " + file.getName();
        } else {
            frame_title = frame_title_0 + "        " + "Unsaved";
        }

        if (!isSaved()) {
            frame_title += "*";
        }

        return frame_title;
    }
}
