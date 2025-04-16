package oriedita.editor.databinding;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.tinylog.Logger;
import oriedita.editor.AbstractModel;
import oriedita.editor.service.BindingService;
import origami.crease_pattern.OritaCalc;

@ApplicationScoped
public class CameraModel extends AbstractModel {
    private double rotation;
    private double scale;

    @Inject
    public CameraModel(BindingService bindingService) {
        super(bindingService);
        reset();
    }

    public void reset() {
        scale = 1.0;
        rotation = 0.0;

        this.notifyAllListeners();
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        double oldRotation = this.rotation;
        this.rotation = OritaCalc.angle_between_m180_180(rotation);
        this.pcs.firePropertyChange("rotation", oldRotation, this.rotation);
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        double oldScale = this.scale;
        this.scale = Math.max(scale, 0.00001);
        this.pcs.firePropertyChange("scale", oldScale, scale);
    }

    public double getScaleForZoomBy(double value, double zoomSpeed, double initialScale) {
        double zoomBase = 1 + zoomSpeed/10;
        if (value > 0) {
            return (initialScale / Math.pow(zoomBase, value));
        } else if (value < 0) {
            return (initialScale * Math.pow(zoomBase, Math.abs(value)));
        }
        return initialScale;
    }
}
