package origami_editor.editor.databinding;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class BackgroundModel {
    private boolean displayBackground;
    private boolean lockBackground;

    public Image getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Image backgroundImage) {
        Image oldBackgroundImage = this.backgroundImage;
        this.backgroundImage = backgroundImage;
        this.pcs.firePropertyChange("backgroundImage", oldBackgroundImage, backgroundImage);
    }

    private Image backgroundImage;

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public BackgroundModel() {
        reset();
    }

    public void reset() {
        displayBackground = true;
        lockBackground = false;

        this.pcs.firePropertyChange(null, null, null);
    }

    public boolean isDisplayBackground() {
        return displayBackground;
    }

    public void setDisplayBackground(boolean displayBackground) {
        boolean oldDisplayBackground = this.displayBackground;
        this.displayBackground = displayBackground;
        this.pcs.firePropertyChange("displayBackground", oldDisplayBackground, displayBackground);
    }

    public boolean isLockBackground() {
        return lockBackground;
    }

    public void setLockBackground(boolean lockBackground) {
        boolean oldLockBackground = this.lockBackground;
        this.lockBackground = lockBackground;
        this.pcs.firePropertyChange("lockBackground", oldLockBackground, lockBackground);
    }
}
