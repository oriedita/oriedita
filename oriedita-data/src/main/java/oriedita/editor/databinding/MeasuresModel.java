package oriedita.editor.databinding;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.tools.StringOp;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

@ApplicationScoped
public class MeasuresModel {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private double measuredLength1;
    private double measuredLength2;
    private double measuredAngle1;
    private double measuredAngle2;
    private double measuredAngle3;


    @Inject
    public MeasuresModel() {
        reset();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public void reset() {
        measuredLength1 = 0.0;
        measuredLength2 = 0.0;
        measuredAngle1 = 0.0;
        measuredAngle2 = 0.0;
        measuredAngle3 = 0.0;

        this.pcs.firePropertyChange(null, null, null);
    }

    public double getMeasuredLength1() {
        return measuredLength1;
    }

    public void setMeasuredLength1(double measuredLength1) {
        double oldMeasuredLength1 = this.measuredLength1;
        this.measuredLength1 = measuredLength1;
        this.pcs.firePropertyChange("measuredLength1", oldMeasuredLength1, measuredLength1);
    }

    public double getMeasuredLength2() {
        return measuredLength2;
    }

    public void setMeasuredLength2(double measuredLength2) {
        double oldMeasuredLength2 = this.measuredLength2;
        this.measuredLength2 = measuredLength2;
        this.pcs.firePropertyChange("measuredLength2", oldMeasuredLength2, measuredLength2);
    }

    public double getMeasuredAngle1() {
        return measuredAngle1;
    }

    public void setMeasuredAngle1(double measuredAngle1) {
        double oldMeasuredAngle1 = this.measuredAngle1;
        if (measuredAngle1 > 180.0) {
            this.measuredAngle1 = measuredAngle1 - 360.0;
        } else {
            this.measuredAngle1 = measuredAngle1;
        }
        this.pcs.firePropertyChange("measuredAngle1", oldMeasuredAngle1, this.measuredAngle1);
    }

    public double getMeasuredAngle2() {
        return measuredAngle2;
    }

    public void setMeasuredAngle2(double measuredAngle2) {
        double oldMeasuredAngle2 = this.measuredAngle2;
        if (measuredAngle2 > 280.0) {
            this.measuredAngle2 = measuredAngle2 - 360.0;
        } else {
            this.measuredAngle2 = measuredAngle2;
        }
        this.pcs.firePropertyChange("measuredAngle2", oldMeasuredAngle2, this.measuredAngle2);
    }

    public double getMeasuredAngle3() {
        return measuredAngle3;
    }

    public void setMeasuredAngle3(double measuredAngle3) {
        double oldMeasuredAngle3 = this.measuredAngle3;
        if (measuredAngle3 > 380.0) {
            this.measuredAngle3 = measuredAngle3 - 360.0;
        } else {
            this.measuredAngle3 = measuredAngle3;
        }
        this.pcs.firePropertyChange("measuredAngle3", oldMeasuredAngle3, this.measuredAngle3);
    }



    public double string2double(String str0, double default_if_error) {
        String new_str0 = str0.trim();
        if (new_str0.equals("L1")) {
            str0 = String.valueOf(getMeasuredLength1());
        }
        if (new_str0.equals("L2")) {
            str0 = String.valueOf(getMeasuredLength2());
        }
        if (new_str0.equals("A1")) {
            str0 = String.valueOf(getMeasuredAngle1());
        }
        if (new_str0.equals("A2")) {
            str0 = String.valueOf(getMeasuredAngle2());
        }
        if (new_str0.equals("A3")) {
            str0 = String.valueOf(getMeasuredAngle3());
        }

        return StringOp.String2double(str0, default_if_error);
    }
}
