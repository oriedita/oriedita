package oriedita.editor.databinding;

import origami.crease_pattern.OritaCalc;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

@Singleton
public class CameraModel {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private double rotation;
    private double scale;

    @Inject
    public CameraModel() {
        reset();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public void reset() {
        scale = 1.0;
        rotation = 0.0;

        this.pcs.firePropertyChange(null, null, null);
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        double oldRotation = this.rotation;
        this.rotation = OritaCalc.angle_between_m180_180(rotation);
        this.pcs.firePropertyChange("rotation", oldRotation, this.rotation);
    }

    public void increaseRotation() {
        setRotation(rotation + 11.25);
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        double oldScale = this.scale;
        this.scale = Math.max(scale, 0.0);
        this.pcs.firePropertyChange("scale", oldScale, scale);
    }

    public void zoomIn() {
        setScale(scale * Math.sqrt(Math.sqrt(Math.sqrt(2))));
    }

    public void zoomBy(double value) {
        if (value > 0) {
            setScale(scale / Math.pow(1.1, value));
        } else if (value < 0) {
            setScale(scale * Math.pow(1.1, Math.abs(value)));
        }
    }

    public void zoomOut() {
        setScale(scale / Math.sqrt(Math.sqrt(Math.sqrt(2))));
    }

    public void decreaseRotation() {
        setRotation(rotation - 11.25);
    }
}
