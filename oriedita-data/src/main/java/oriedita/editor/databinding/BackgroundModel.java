package oriedita.editor.databinding;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

@ApplicationScoped
public class BackgroundModel {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean displayBackground;
    private boolean lockBackground;
    private Image backgroundImage;
    private Polygon backgroundPosition;

    @Inject
    public BackgroundModel() {
        reset();
    }

    public Image getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Image backgroundImage) {
        Image oldBackgroundImage = this.backgroundImage;
        this.backgroundImage = backgroundImage;
        this.pcs.firePropertyChange("backgroundImage", oldBackgroundImage, backgroundImage);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public void reset() {
        backgroundImage = null;
        displayBackground = false;
        lockBackground = false;

        backgroundPosition = new Polygon(new Point(0.0, 0.0),
                new Point(1.0, 1.0),
                new Point(120.0, 120.0),
                new Point(121.0, 121.0));

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

    public Polygon getBackgroundPosition() {
        return backgroundPosition;
    }

    public void setBackgroundPosition(Polygon backgroundPosition) {
        Polygon oldBackgroundPosition = this.backgroundPosition;
        this.backgroundPosition = backgroundPosition;
        this.pcs.firePropertyChange("backgroundPosition", oldBackgroundPosition, backgroundPosition);
    }
}
